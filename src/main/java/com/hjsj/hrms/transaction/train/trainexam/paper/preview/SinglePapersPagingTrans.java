package com.hjsj.hrms.transaction.train.trainexam.paper.preview;

import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SinglePapersPagingTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		Map map = (HashMap) this.getFormHM().get("requestPamaHM");
		String state = (String)map.get("state");//上一页  -1   下一页1   
		state=state==null||state.length()<1?"0":state;
		map.remove("state");
		String currentstr = (String)map.get("current");//当前页
		int current = currentstr!=null&&currentstr.length()>0?Integer.parseInt(currentstr)+Integer.parseInt(state):1;
		
		String flag = (String) this.getFormHM().get("flag");
		String r5300 = (String) this.getFormHM().get("r5300");//试卷id
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String exam_type = (String) this.getFormHM().get("exam_type");
		// paper_id
		String paper_id = (String) map.get("paper_id");
		paper_id = PubFunc.decrypt(SafeCode.decode(paper_id));
		// 课程id
		String r5000 = (String) map.get("r5000");
		r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
		
		ArrayList typeList = (ArrayList)this.getFormHM().get("typeList");//类型顺序 用于类型前编号的分析
		
		// 查询的列
		String columns = "";
		String strsql = "";
		String strwhere = "";
		String order = "";
		if ("5".equals(flag)) {// 自测考试
			//试题编号,试题内容,试题选项,主观题答案,客观题答案,试题解析,考试时间,考试得分,题型编号,用户得分,用户主观题答案,用户客观题答案
			columns="r5200,r5205,r5207,r5208,r5209,r5210,r5211,r5213,type_id,score,s_answer,o_answer";
			strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5211,r5213,t.type_id,"+Sql_switcher.isnull("a.score", "0.0")+" score,"+Sql_switcher.isnull("s_answer", "''")+" s_answer,"+Sql_switcher.isnull("o_answer", "''")+" o_answer";
			strwhere=" from (select * from tr_selfexam_test where paper_id="+paper_id+") t left join r52 r on r.r5200=t.r5200";
			strwhere+=" left join ( select * from tr_exam_answer where exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"' and exam_no="+paper_id+") a on a.r5200=r.r5200 ";
			strwhere+=" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="+r5300;
			strwhere+=" where t.paper_id="+paper_id;
			order = " order by q.norder ,t.norder";
		} else if("2".equals(flag)){// 我的考试
		
			//试题编号,试题内容,试题选项,主观题答案,客观题答案,试题解析,考试时间，考试得分,题型编号,用户得分,用户主观题答案,用户客观题答案
			columns="r5200,r5205,r5207,r5208,r5209,r5210,r5211,r5213,type_id,score,s_answer,o_answer";
			strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5211,r5213,t.type_id,"+Sql_switcher.isnull("a.score", "0.0")+" score,"+Sql_switcher.isnull("s_answer", "''")+" s_answer,"+Sql_switcher.isnull("o_answer", "''")+" o_answer";
			strwhere=" from tr_exam_paper t left join r52 r on r.r5200=t.r5200";
			strwhere+=" left join (select * from tr_exam_answer where exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"' and exam_no="+paper_id+") a on a.r5200=r.r5200 and exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"'";
			strwhere+=" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="+r5300;
			strwhere+=" where t.r5300="+r5300;
			if (Integer.parseInt(userView.getA0100()) % 2 == 1) {
				order = " order by q.norder,t.norder";
			} else {
				order = " order by q.norder,t.norder desc";
			}
			
		} else {
		
			//试题编号,试题内容,试题选项,主观题答案,客观题答案,试题解析,考试时间,考试得分,题型编号,用户得分,用户主观题答案,用户客观题答案
			columns="r5200,r5205,r5207,r5208,r5209,r5210,r5211,r5213,type_id,score, s_answer, o_answer";
			strsql="select r.r5200,r5205,r5207,r5208,r5209,r5210,r5211,r5213,t.type_id,a.score,"+Sql_switcher.isnull("s_answer", "''")+" s_answer, "+Sql_switcher.isnull("o_answer", "''")+" o_answer";
			strwhere=" from tr_exam_paper t left join r52 r on r.r5200=t.r5200";
			strwhere+=" left join tr_exam_answer a on a.r5200=r.r5200 and exam_type="+exam_type+" and nbase='"+userView.getDbname()+"' and a0100='"+userView.getA0100()+"'";
			strwhere+=" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="+r5300;
			strwhere+=" where t.r5300="+r5300;
			order = " order by q.norder ,t.norder";
		}
		
		HashMap questionMap = new HashMap();//试题信息 
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(strsql+strwhere+order, 1, current);
			if(this.frowset.next()){
				questionMap.put("r5200", String.valueOf(this.frowset.getInt("r5200")));
				questionMap.put("r5207", Sql_switcher.readMemo(this.frowset, "r5207"));
				questionMap.put("r5208", Sql_switcher.readMemo(this.frowset, "r5208"));
				questionMap.put("r5209", Sql_switcher.readMemo(this.frowset, "r5209"));
				questionMap.put("r5210", Sql_switcher.readMemo(this.frowset, "r5210"));
				questionMap.put("r5211", String.valueOf(this.frowset.getInt("r5211")));
				questionMap.put("r5213", String.valueOf(this.frowset.getFloat("r5213")));
				questionMap.put("score", String.valueOf(this.frowset.getFloat("score")));
				questionMap.put("s_answer", Sql_switcher.readMemo(this.frowset, "s_answer"));
				questionMap.put("o_answer", Sql_switcher.readMemo(this.frowset, "o_answer"));
				int type_id = this.frowset.getInt("type_id");
				questionMap.put("type_id", String.valueOf(type_id));
				
				//操作该类型属于改试卷的第几大题型，操作某类型下的试题编号
				for (int j = 0; j < typeList.size(); j++) {
					int id = Integer.parseInt(typeList.get(j).toString());
					if(id==type_id){
						questionMap.put("typeTitle", QuestionesBo.getTitle(String.valueOf(type_id), SafeCode.encode(PubFunc.encrypt(String.valueOf(r5300))), String.valueOf(j+1)));
						
						String typeid = (String)this.getFormHM().get("typeid");
						int tmp = typeid==null||typeid.length()<1?0:Integer.parseInt(typeid);
						if(current==1||(tmp!=type_id&& "1".equals(state)))
							map.put("tmp"+type_id, "1");
						if(map.get("tmp"+type_id)==null|| "".equals(map.get("tmp"+type_id))){
							map.put("tmp"+type_id, "1");
						}else{
							if(tmp==type_id&&current!=1)
								map.put("tmp"+type_id, String.valueOf(Integer.parseInt(map.get("tmp"+type_id).toString())+Integer.parseInt(state)));
						}
					}
				}
				questionMap.put("r5205", "第"+map.get("tmp"+type_id)+"题("+String.valueOf(this.frowset.getFloat("r5213"))+"分)&nbsp;&nbsp;"+Sql_switcher.readMemo(this.frowset, "r5205"));
				this.getFormHM().put("typeid", String.valueOf(type_id));
			}
			
			
			// 获得开始时间，结束时间，剩余时间
			if ("2".equals(flag)) {
				
				String sql2 = "select r5506,r5507 from r55 where r5400=" + paper_id + " and a0100='"+this.userView.getA0100()+"' and nbase='"+this.userView.getDbname()+"'";
				this.frowset = dao.search(sql2);
				Date start = null;
				Date end = null;
				if (this.frowset.next()) {
					start = this.frowset.getDate("r5506");
					if(Sql_switcher.searchDbServer() == Constant.ORACEL&&start!= null){
						String[] start1 = DateUtils.format(start, "yyyy-MM-dd HH:mm:ss").split(" ");
						String[] start2 = DateUtils.format(this.frowset.getTime("r5506"), "yyyy-MM-dd HH:mm:ss").split(" ");
						String date = start1[0]+" "+start2[1];
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try{
							start = sdf.parse(date);
						}catch (ParseException e) {
							e.printStackTrace();
						}
						
					}
					end = this.frowset.getDate("r5507");
				}
				String sqls = "select r5405,r5406,R5413,r5415,r5407,r5410 from r54 where r5400=" + paper_id;
				this.frowset = dao.search(sqls);
				if(this.frowset.next()){
					String r5415 = this.frowset.getString("r5415");
					r5415 = r5415 == null ? "2" : r5415;
					
					this.getFormHM().put("isSingle", this.frowset.getString("r5410"));
					
					int over = this.frowset.getInt("r5407") * 60;
					this.getFormHM().put("over", "" + over);
					
					if ("2".equals(r5415)) {
						this.getFormHM().put("startTime",DateUtils.format(this.frowset.getDate("r5405"), "yyyy-MM-dd HH:mm:ss"));
						this.getFormHM().put("endTime",DateUtils.format(this.frowset.getDate("r5406"), "yyyy-MM-dd HH:mm:ss"));
						long tim = this.frowset.getDate("r5406").getTime();
						long nowt = new Date().getTime();
						long over2 = (tim - nowt)/1000;
						this.getFormHM().put("over", "" + over2);
					} else {
						Date d = new Date();
						if (start != null) {
							d = start;
							over = over - Integer.parseInt((new Date().getTime() - d.getTime())/1000 + "");
						}
						this.getFormHM().put("startTime",DateUtils.format(d , "yyyy-MM-dd HH:mm:ss"));
						String endt= (String)this.getFormHM().get("endTime");
						Date endTime = DateUtils.getDate(endt,"yyyy-MM-dd HH:mm:ss");
						if (endTime.getTime() > this.frowset.getDate("r5406").getTime()) {
							this.getFormHM().put("over", "" + (this.frowset.getDate("r5406").getTime() - new Date().getTime())/1000);
							this.getFormHM().put("endTime",DateUtils.format(this.frowset.getDate("r5406"), "yyyy-MM-dd HH:mm:ss"));
						} else {
							this.getFormHM().put("over", "" + (endTime.getTime() - new Date().getTime())/1000);

						}
					}
					

					
					
					String r5413 = this.frowset.getString("r5413");
					r5413 = r5413 == null ? "-1" : r5413;
					
					
					
					this.getFormHM().put("r5413", r5413);
					this.getFormHM().put("r5415", r5415);
				}
				
				
				// 更新考试状态
				String paperState = (String) map.get("paperState");
				
				// 更新结束时间
				RecordVo vo = new RecordVo("r55");
				vo.setString("nbase", this.userView.getDbname());
				vo.setString("a0100", this.userView.getA0100());
				vo.setInt("r5400", Integer.parseInt(paper_id));
				
				vo = dao.findByPrimaryKey(vo);
				Date date = vo.getDate("r5506");
				if (date == null) {
					vo.setDate("r5506", new Date());
				}
				if (paperState != null && paperState.length() > 0) {
					vo.setInt("r5513", Integer.parseInt(paperState));
				}
				dao.updateValueObject(vo);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//计算剩余时间=交卷时间-当前时间  (秒)
		//Date now = new Date();
		String endTimestr = (String)this.getFormHM().get("endTime");
		Date endTime = DateUtils.getDate(endTimestr,"yyyy-MM-dd HH:mm:ss");
		int l=(int) ((endTime.getTime()-new Date().getTime())/1000);
		
		if ("2".equals(flag)) {
		    TrainExamPlanBo bo = new TrainExamPlanBo(this.frameconn, this.userView);
		    ArrayList<String> msg = new ArrayList<String>();
		    msg.add(paper_id);
		    msg.add(this.userView.getDbname());
		    msg.add(this.userView.getA0100());
		    ArrayList<ArrayList<String>> students = new ArrayList<ArrayList<String>>();
		    students.add(msg);
		    try {
                bo.updatePendingTask(students, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
			this.getFormHM().put("remaintime", this.getFormHM().get("over")); 
		} else {
			this.getFormHM().put("remaintime", String.valueOf(l));
		}
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(r5300)));
		this.getFormHM().put("current", String.valueOf(current));
		this.getFormHM().put("questionMap", questionMap);
		this.getFormHM().put("paper_id", SafeCode.encode(PubFunc.encrypt(paper_id)));
		this.getFormHM().put("r5000", SafeCode.encode(PubFunc.encrypt(r5000)));
		this.getFormHM().put("exam_type", exam_type);
		
	}
}
