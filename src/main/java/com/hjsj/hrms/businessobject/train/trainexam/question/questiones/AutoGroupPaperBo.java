package com.hjsj.hrms.businessobject.train.trainexam.question.questiones;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * 自动组卷生成试卷
 */

public class AutoGroupPaperBo {
	
	private Connection conn;
	private UserView userView;
	private String r5300;
	private int examscore;//满分值
	private int examtime;
	
	private ArrayList list;//试卷信息 (题型)
	private ArrayList idsList ;
	private ArrayList typeIdList;
	//private int maxId;//暂时不考虑试题出现频率了
	//private int minId;
	private Random random = new Random();
	
	public AutoGroupPaperBo(Connection conn,UserView userView,String r5300,int examscore,int examtime){
		this.conn = conn;
		this.userView = userView;
		this.r5300 = r5300;
		this.examscore = examscore;
		this.examtime = examtime;
		idsList = new ArrayList();
		typeIdList = new ArrayList();
		init();
	}

	private void init() {
		list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		//题型编号，题型名称，题型分类，分数，考试时间，试题数量，知识点
		String sql = "select q.type_id,type_name,ques_type,score,answer_time,max_num,know_ids from tr_exam_question_type q left join tr_question_type t on q.type_id=t.type_id where R5300="+this.r5300+" order by t.norder desc";//先取大题部分
		RowSet rs = null;
		try {
			LazyDynaBean bean = null;
			rs = dao.search(sql);
			while(rs.next()){
				bean = new LazyDynaBean();
				bean.set("type_id", String.valueOf(rs.getInt("type_id")));
				bean.set("type_name", rs.getString("type_name"));
				bean.set("ques_type", String.valueOf(rs.getInt("ques_type")));
				bean.set("score", String.valueOf(rs.getFloat("score")));
				bean.set("answer_time", String.valueOf(rs.getInt("answer_time")));
				bean.set("max_num", String.valueOf(rs.getInt("max_num")));
				String know_id = rs.getString("know_ids");
				know_id = know_id==null||know_id.length()<2?"":know_id;
				bean.set("know_ids", know_id);
				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * 生成试卷
	 * @return 生成状态  ok=成功
	 */
	public String getGroupPaperIds(){
		String state="ok";
		RowSet rs = null;
		String sql="";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			LazyDynaBean bean = null;
			for (int m = 0; m < list.size(); m++) {
				bean = (LazyDynaBean)list.get(m);
				int max_num = Integer.parseInt(bean.get("max_num").toString());//试题数量
				float score = Float.parseFloat(bean.get("score").toString());//试题分数
				int type_id = Integer.parseInt(bean.get("type_id").toString());//题型
				String know_ids = (String) bean.get("know_ids");
				if(max_num<=0||score<=0) {
                    continue;
                }
				
				ArrayList r5200list = new ArrayList();//获取该类型下所有试题
				ArrayList r5213list = new ArrayList();//获取该类型下所有试题分数
				ArrayList knowlist = new ArrayList();//获取该类型下所有试题知识点
				ArrayList r5203list = new ArrayList();//获取该类型下所有试题难度
				sql = "select r.r5200,r5203,r5213,know_id,r5203 from r52 r left join tr_test_knowledge t on t.r5200=r.r5200 where type_id="+type_id;
				sql = sql + " and (1=1";
				if(know_ids!=null&&know_ids.length()>0){
					sql+=" and (";
					String []knowid = know_ids.split(",");
					for (int i = 0; i < knowid.length; i++) {
						if(i>0) {
                            sql+=" or ";
                        }
						sql+="know_id like '"+knowid[i]+"%'";
					}
					sql += ")";
				}
				sql+=" and r5213<="+score;
				// 权限过滤
				if (!this.userView.isSuper_admin()) {
					TrainCourseBo tb = new TrainCourseBo(this.userView);
					String unit = tb.getUnitIdByBusi();//this.userView.getUnitIdByBusi("6");
					if(unit.indexOf("UN`")==-1){
						String []units = unit.split("`");
						sql+=" and (";
						if (units.length > 0 && unit.length() > 0) {
							for (int i = 0; i < units.length; i++) {
								String b0110s = units[i].substring(2);
								sql+="b0110=" + Sql_switcher.substr("'"+b0110s+"'", "1", Sql_switcher.length("b0110"));
								sql+=" or b0110 like '";
								sql+=b0110s;
								sql+="%'";
								sql+=" or ";
							}
						}
						sql+=Sql_switcher.isnull("b0110", "'-1'");
						sql+="='-1'";
						if (Sql_switcher.searchDbServer() == 1) {
							sql+=" or b0110=''";
						}
						sql+=" or r5216='1'))";
					}else{
						sql += ")";
					}
				}else{
					sql += ")";
				}
				rs = dao.search(sql);
				while(rs.next()){
					knowlist.add(rs.getString("know_id"));
					r5200list.add(String.valueOf(rs.getInt("r5200")));
					r5213list.add(String.valueOf(rs.getFloat("r5213")));
					r5203list.add(String.valueOf(rs.getInt("r5203")));
				}

				//判断知识点是否存在比例(为设置比例 则随机)
				int zhishidianbili=0;
				sql="select * from tr_knowledge_diff where r5300="+r5300+" and type_id="+type_id;
				rs = dao.search(sql);
				while(rs.next()){
					for(int t=1;t<9;t++){
						zhishidianbili += rs.getInt("diff"+t);
					}
				}

				if(zhishidianbili<1){//该类型无知识点  随机取
					state = typeGroup1(max_num,score,type_id,bean.get("type_name").toString(),r5200list,r5213list);
				}else{
					state = typeGroup2(max_num,score,type_id,bean.get("type_name").toString(),know_ids,r5200list,r5213list,r5203list,knowlist);
				}
				
				if(!"ok".equals(state)) {
                    break;
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return state;
	}
	
	/**
	 * 根据类型来组卷  无知识点比例限制
	 * @param type_name 类型名称
	 * @param score 该类型许取题的分数
	 * @param max_num 该类型许取题的个数
	 * @param know_ids 该类型须取的知识点
	 * 以下顺序对应
	 * @param r5213list 试题对应分数集合
	 * @param r5200list 试题对应id集合
	 */
	private String typeGroup1(int max_num, float score,int type_id, String type_name, ArrayList r5200list, ArrayList r5213list){
		String state="ok";
		float tmpScore=0;
		//试题数量
		int o=0;
		
		
		float tmpS=0f;
		for (int i = 0; i < r5200list.size()&&o<max_num; i++) {
			tmpScore = score/(max_num-o);//单题的大致分数
			
			tmpScore = (float) (Math.round(tmpScore*10)/10.0);
			
			tmpS+=getDecimals(tmpScore);
			if(tmpS>0&&tmpS<0.5) {
                tmpScore=(int)tmpScore;
            } else if(tmpS==0.5){
				tmpS=0;
			}else if(tmpS>0.5){
				if(tmpS==1){
					tmpScore=(float) (1+(int)tmpScore);
					tmpS--;
				}else{
					tmpScore=(float) (0.5+(int)tmpScore);
					tmpS-=0.5;
				}
			}
			
			score=score-tmpScore;
			
			ArrayList tmpequal = new ArrayList();//相等的值
			ArrayList tmpless = new ArrayList();//小于的值
			//取出大致分数下的试题
			for(int j=0;j<r5213list.size();j++){
				float tmp = Float.parseFloat(r5213list.get(j).toString());
				if(tmpScore==tmp) {
                    tmpequal.add(String.valueOf(j));//保存试题在list中的位置
                }
				if(tmpScore>tmp) {
                    tmpless.add(String.valueOf(j));//保存试题在list中的位置
                }
			}
			
			if(tmpequal.size()<1&&tmpless.size()>0){//没有相等分数的试题
				if(tmpless.size()<=max_num){
					state="该试题库"+type_name+"的题量不足，无法获取满足规则的试题量!";
					break;
				}
				int n = random.nextInt(tmpless.size());
				int l=0;
				while(idsList.contains(r5200list.get(Integer.parseInt((String)tmpless.get(n))))){
					n = random.nextInt(tmpless.size());
					if(l>tmpless.size()){//如果取题次数大于试题个数
						state="该试题库"+type_name+"的题量不足，无法获取满足规则的试题量!";
						break;
					}
					l++;
				}
				if(!idsList.contains(r5200list.get(Integer.parseInt((String)tmpless.get(n))))){
					idsList.add(r5200list.get(Integer.parseInt((String)tmpless.get(n))));
					typeIdList.add(String.valueOf(type_id));
					tmpScore=(float) (tmpScore-Float.parseFloat(r5213list.get(Integer.parseInt((String)tmpless.get(n))).toString()));
					o++;
				}
			}else if(tmpequal.size()>0&&tmpequal.size()<=max_num){//相等分数的题型小于要取的数量
				int n = random.nextInt(tmpequal.size());
				int l=0;
				while(l<tmpequal.size()&&idsList.contains(r5200list.get(Integer.parseInt((String)tmpequal.get(n))))){
					n = random.nextInt(tmpequal.size());
					l++;
				}
				if(!idsList.contains(r5200list.get(Integer.parseInt((String)tmpequal.get(n))))){
					idsList.add(r5200list.get(Integer.parseInt((String)tmpequal.get(n))));
					typeIdList.add(String.valueOf(type_id));
					tmpScore=tmpScore-tmpScore;
					o++;
				}else if(tmpless.size()>0){
					n = random.nextInt(tmpless.size());
					l=0;
					while(idsList.contains(r5200list.get(Integer.parseInt((String)tmpless.get(n))))){
						n = random.nextInt(tmpless.size());
						if(l>tmpless.size()){
							state="该试题库"+type_name+"的题量不足，无法获取满足规则的试题量!";
							break;
						}
						l++;
					}
					if(idsList.contains(r5200list.get(Integer.parseInt((String)tmpless.get(n))))){
						idsList.add(r5200list.get(Integer.parseInt((String)tmpless.get(n))));
						typeIdList.add(String.valueOf(type_id));
						tmpScore=(float) (tmpScore-Float.parseFloat(r5213list.get(Integer.parseInt((String)tmpless.get(n))).toString()));
						o++;
					}
				}
			}else if(tmpequal.size()>max_num){//从相等题型中取
				int n = random.nextInt(tmpequal.size());
				while(idsList.contains(r5200list.get(Integer.parseInt((String)tmpequal.get(n))))){
					n = random.nextInt(tmpequal.size());
				}
				idsList.add(r5200list.get(Integer.parseInt((String)tmpequal.get(n))));
				typeIdList.add(String.valueOf(type_id));
				tmpScore=tmpScore-tmpScore;
				o++;
			}
			score=score+tmpScore;
			if(!"ok".equals(state)) {
                break;
            }
		}
		//examscore=examscore+score;
		if("ok".equals(state)&&(score<-1||score>1)){
			state="该试题库"+type_name+"的题量不足，无法获取满足规则的试题量!";
		}
		//System.out.println(state);
		return state;
	}
	
	/**
	 * 根据类型来组卷  知识点比例限制
	 * @param type_name 类型名称
	 * @param score 该类型许取题的分数
	 * @param max_num 该类型许取题的个数
	 * @param know_ids 该类型须取的知识点
	 * 以下顺序对应
	 * @param r5213list 试题对应分数集合
	 * @param r5200list 试题对应id集合
	 * @param r5203list 试题对应难度集合
	 * @param knowlist 
	 */
	private String typeGroup2(int max_num, float score,int type_id, String type_name, String know_ids, ArrayList r5200list, ArrayList r5213list, ArrayList r5203list, ArrayList knowlist){
		String state="ok";
		float tmpscore=score;
		float tmpScore=0;
//		Random random = new Random();
		KnowLedgeDiffBo bo = new KnowLedgeDiffBo(this.conn,r5300);
		String knows[] = know_ids.split(",");
		float remainderS = 0f;//余数分数
		float remainderN = 0f;//余数题数
		for (int r = 0; r < knows.length; r++) {
			for (int d = -2; d < 6; d++) {
				String tmpLedgeDiff = bo.getValue(type_id+"_"+knows[r]+"_"+d);
				if(tmpLedgeDiff==null||tmpLedgeDiff.length()<1) {
                    continue;
                }
				float scoreT = (float) (tmpscore*(Float.parseFloat(tmpLedgeDiff)/100.0));
				int max_numT = (int) (max_num*(Integer.parseInt(tmpLedgeDiff)/100.0));
				remainderS+=getDecimals(scoreT);
				if(remainderS==0.5){
					remainderS=0;
				}else if(remainderS>0.5){
					scoreT=(float) (0.5+(int)scoreT);
					remainderS=(float) (remainderS-0.5);
				}
				remainderN+=getDecimals((float)(max_num*(Integer.parseInt(tmpLedgeDiff)/100.0)));
				if(remainderN==1){
					max_numT++;
					remainderN=0;
				}else if(remainderN>1){
					max_numT++;
					remainderN=remainderN-1;
				}
				//试题数量
				int o=0;
				float tmpS = 0f;
				for (int i = 0; i < r5200list.size()&&o<max_numT; i++) {
					tmpScore = scoreT/(max_numT-o);//单题的大致分数
					tmpScore = (float) (Math.round(tmpScore*10)/10.0);
					
					tmpS+=getDecimals(tmpScore);
					if(tmpS>0&&tmpS<0.5) {
                        tmpScore=(int)tmpScore;
                    } else if(tmpS==0.5){
						tmpS=0;
					}else if(tmpS>0.5){
						if(tmpS>=1){
							tmpScore=(float) (1+(int)tmpScore);
							tmpS--;
						}else{
							tmpScore=(float) (0.5+(int)tmpScore);
							tmpS-=0.5;
						}
					}
					
					score=score-tmpScore;
					scoreT-=tmpScore;
					ArrayList tmpequal = new ArrayList();//相等的值
					ArrayList tmpless = new ArrayList();//小于的值
					//取出大致分数下的试题
					for(int j=0;j<r5213list.size();j++){
						if(d!=Integer.parseInt(r5203list.get(j).toString()))//过滤掉不属于本难度系数的试题
                        {
                            continue;
                        }
						if(!knows[r].equals(knowlist.get(j)))//过滤掉不属于本知识点的试题
                        {
                            continue;
                        }
						float tmp = Float.parseFloat(r5213list.get(j).toString());
						if(tmpScore==tmp) {
                            tmpequal.add(String.valueOf(j));//保存试题在list中的位置
                        }
						if(tmpScore>tmp) {
                            tmpless.add(String.valueOf(j));//保存试题在list中的位置
                        }
					}
					
					if(tmpequal.size()<1&&tmpless.size()>0){//没有相等分数的试题
						if(tmpless.size()<=max_num){
							state = QuestionesBo.getKnowledgeviewvaluee(knows[r])+"下难度系数为"+QuestionesBo.getDifficultyValue(d)+"的"+type_name+"题量不足,无法获取满足规则的试题量！";
							//state="该试题库"+type_name+"的题量不足，无法获取"+tmpLedgeDiff+"%("+knows[r]+"/"+d+")的题数"+max_num+",分数"+score+"的试题量";
							break;
						}
						int n = random.nextInt(tmpless.size());
						int l=0;
						while(idsList.contains(r5200list.get(Integer.parseInt((String)tmpless.get(n))))){
							n = random.nextInt(tmpless.size());
							if(l>tmpless.size()){//如果取题次数大于试题个数
								state = QuestionesBo.getKnowledgeviewvaluee(knows[r])+"下难度系数为"+QuestionesBo.getDifficultyValue(d)+"的"+type_name+"题量不足,无法获取满足规则的试题量！";
								//state="该试题库"+type_name+"的题量不足，无法获取"+tmpLedgeDiff+"%("+knows[r]+"/"+d+")的题数"+max_num+",分数"+score+"的试题量";
								break;
							}
							l++;
						}
						if(!idsList.contains(r5200list.get(Integer.parseInt((String)tmpless.get(n))))){
							idsList.add(r5200list.get(Integer.parseInt((String)tmpless.get(n))));
							typeIdList.add(String.valueOf(type_id));
							tmpScore=(float) (tmpScore-Float.parseFloat(r5213list.get(Integer.parseInt((String)tmpless.get(n))).toString()));
							o++;
						}
					}else if(tmpequal.size()>0&&tmpequal.size()<=max_num){//相等分数的题型小于要取的数量
						int n = random.nextInt(tmpequal.size());
						int l=0;
						while(l<tmpequal.size()&&idsList.contains(r5200list.get(Integer.parseInt((String)tmpequal.get(n))))){
							n = random.nextInt(tmpequal.size());
							l++;
						}
						if(!idsList.contains(r5200list.get(Integer.parseInt((String)tmpequal.get(n))))){
							idsList.add(r5200list.get(Integer.parseInt((String)tmpequal.get(n))));
							typeIdList.add(String.valueOf(type_id));
							tmpScore=tmpScore-tmpScore;
							o++;
						}else if(tmpless.size()>0){
							n = random.nextInt(tmpless.size());
							l=0;
							while(idsList.contains(r5200list.get(Integer.parseInt((String)tmpless.get(n))))){
								n = random.nextInt(tmpless.size());
								if(l>tmpless.size()){
									state = QuestionesBo.getKnowledgeviewvaluee(knows[r])+"下难度系数为"+QuestionesBo.getDifficultyValue(d)+"的"+type_name+"题量不足,无法获取满足规则的试题量！";
									//state="该试题库"+type_name+"的题量不足，无法获取"+tmpLedgeDiff+"%("+knows[r]+"/"+d+")的题数"+max_num+",分数"+score+"的试题量";
									break;
								}
								l++;
							}
							if(idsList.contains(r5200list.get(Integer.parseInt((String)tmpless.get(n))))){
								idsList.add(r5200list.get(Integer.parseInt((String)tmpless.get(n))));
								typeIdList.add(String.valueOf(type_id));
								tmpScore=(float) (tmpScore-Float.parseFloat(r5213list.get(Integer.parseInt((String)tmpless.get(n))).toString()));
								o++;
							}
						}
					}else if(tmpequal.size()>max_num){//从相等题型中取
						int n = random.nextInt(tmpequal.size());
						while(idsList.contains(r5200list.get(Integer.parseInt((String)tmpequal.get(n))))){
							n = random.nextInt(tmpequal.size());
						}
						idsList.add(r5200list.get(Integer.parseInt((String)tmpequal.get(n))));
						typeIdList.add(String.valueOf(type_id));
						tmpScore=tmpScore-tmpScore;
						o++;
					}
					score=score+tmpScore;
					scoreT+=tmpScore;
				}
				
				if(!"ok".equals(state)) {
                    break;
                }
			}

		}
		
		//examscore=examscore+score;
		//System.out.println(score);
		if("ok".equals(state)&&(score<-1||score>1)){
			state="该试题库"+type_name+"的题量不足，无法获取满足规则的试题量！";
		}
		return state;
	}
	
	private float getDecimals(float num){
		float tmp = 0f;
		if(num>0){
			tmp = num-(int)num;
		}
		return tmp;
	}
	
	/**
	 * 自动组卷的ids
	 * @return
	 */
	public ArrayList getIdsList(){
		return this.idsList;
	}
	/**
	 * 自动组卷的type_ids
	 * @return
	 */
	public ArrayList getTypeIdsList(){
		return this.typeIdList;
	}
	
	public void closeConn(){
		if(conn!=null){
			try {
				if(!conn.isClosed()) {
                    conn.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}