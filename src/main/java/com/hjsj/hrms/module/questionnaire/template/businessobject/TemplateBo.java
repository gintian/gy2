package com.hjsj.hrms.module.questionnaire.template.businessobject;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title: TemplateBo </p>
 * <p>Description: 问卷设计</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-8-26 上午9:48:57</p>
 * @author jingq
 * @version 1.0
 */

public class TemplateBo {
	
	private Connection conn = null;
	
	public TemplateBo(){
		
	}

	public TemplateBo(Connection conn) {
		this.conn = conn;
	}
	
	static HashMap questionCacheMap = new HashMap();
	
	/***
	 * 
	 * @Title: removeCachMap   
	 * @Description:查看问卷模板保存修改的问卷后 清空修改的问卷questionCacheMap中对应的qnid    
	 * @param @param qnid
	 * @return void    
	 * @throws
	 * @author changxy
	 * 20160820 
	 */
	public void removeCachMap(String qnid){
		if(questionCacheMap.containsKey(qnid+"")){
			questionCacheMap.remove(qnid+"");
		}
	}
	public String getTemplate(Object[] objs,boolean realData){
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap questionMap =null;
		Connection connection = null;
		RowSet rs = null;
		boolean flag = false;
		try {
			int qnid = -1;
			String qnname = "";
			for (int i = 0; i < objs.length; i++) {
				String obj = (String) objs[i];
				if(obj==null)
					continue;
				if(obj.contains("qnid=")){
					qnid = Integer.parseInt(obj.substring(obj.indexOf("=")+1));
				} else if(obj.contains("qnname=")){
					qnname = obj.substring(obj.indexOf("=")+1);
				}
			}
			if(questionCacheMap.containsKey(qnid+"") && !realData){
				map = (HashMap) questionCacheMap.get(qnid+"");
				return JSON.toString(map);
			}
			
			if(this.conn==null){
				connection = AdminDb.getConnection();
				flag = true;
			} else {
				connection = conn;
			}
			//获取问卷名称，填空说明，正常结束、提前结束时提示信息及配置参数。
			map = getTemplateInfo(qnid,connection);
			//获取问卷题目
			ArrayList<HashMap<String, Object>> questionList = getTemplateQuestion(qnid, connection);
			map.put("questionList", questionList);
			questionCacheMap.put(qnid+"", map);

			questionMap = (HashMap) map.clone();
			if(!StringUtils.isEmpty(qnname)) {
				questionMap.put("qnname", qnname);
				questionMap.put("qnlongname", qnname);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			if(flag)
				PubFunc.closeResource(connection);
		}
		return JSON.toString(questionMap);
	}
	
	/**
	 * 获取问卷内容
	 * @Title: getTemplate   
	 * @Description:    
	 * @param objs qnid=00001,qnname=XXX
	 * @return 
	 * @return String
	 */
	public String getTemplate(Object[] objs){
		return this.getTemplate(objs,false);
	}
	
	/**
	 * 获取问卷信息及配置参数
	 * @Title: getTemplateInfo   
	 * @Description:    
	 * @param qnid
	 * @return 
	 * @return String
	 */
	private HashMap<String, Object> getTemplateInfo(int qnid,Connection conn){
		HashMap<String, Object> map = new HashMap<String, Object>();
		RowSet rs = null;
		try{
			String sql = "select qnName,qnLongName,instruction,finishMsg,advanceEndMsg,"
						+ "tp_options from qn_template where qnId = '"+qnid+"'";
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				map.put("qnname", rs.getString("qnName"));
				map.put("qnlongname", rs.getString("qnLongName"));
				map.put("instruction", rs.getString("instruction"));
				map.put("finishmsg", rs.getString("finishMsg"));
				map.put("advanceendmsg", rs.getString("advanceEndMsg"));
				String xml = rs.getString("tp_options");
				if(xml!=null&&xml.length()>0){
					HashMap<String, Object> qnset = parseXml(xml);
					map.put("qnset", qnset);
				} else {
					map.put("qnset", null);
				}
			}
			map.put("qnid", qnid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
	}
	
	/**
	 * 解析xml，
	 * @Title: parseXml   
	 * @Description:    
	 * @param xml
	 * @return 
	 * @return String
	 */
	private HashMap<String, Object> parseXml(String xml){
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			Document doc = PubFunc.generateDom(xml);
			Element ele = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> list = ele.getChildren();
			for (int i = 0; i < list.size(); i++) {
				Element e = list.get(i);
				String name = e.getName();
				if(!"levels".equals(name)){
					map.put(name, e.getText());
				} else {//量表题和矩阵量表题参数
					@SuppressWarnings("unchecked")
					List<Element> arr = e.getChildren();
					ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String,String>>();
					for (int j = 0; j < arr.size(); j++) {
						HashMap<String, String> mp = new HashMap<String, String>();
						Element el = arr.get(j);
						@SuppressWarnings("unchecked")
						List<Attribute> l = el.getAttributes();
						for (int k = 0; k < l.size(); k++) {
							Attribute att = l.get(k);
							mp.put(att.getName(), att.getValue());
						}
						al.add(mp);
					}
					map.put(name, al);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 获取问卷题目
	 * @Title: getTemplateQuestion   
	 * @Description:    
	 * @param qnid
	 * @param conn
	 * @return 
	 * @return String
	 */
	
	public ArrayList<HashMap<String, Object>> getTemplateQuestion(int qnid,Connection conn){
		return getTemplateQuestion(qnid,conn,"","","");
	}
	public ArrayList<HashMap<String, Object>> getTemplateQuestion(int qnid,Connection conn,String subobject,String mainobject,String planid){
		ContentDAO dao = new ContentDAO(conn);
		AnswerResultBo ab = new AnswerResultBo(conn);
		RowSet rs = null;
		RowSet rs1 = null;
		RowSet rs2 = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		//问卷题目
		HashMap<String, HashMap<String, String>> itemmap = getQuestion(String.valueOf(qnid), dao);
		//题目选项
		HashMap<String, ArrayList<HashMap<String, String>>> optionmap = getOptions(String.valueOf(qnid), dao);
		//矩阵选项
		HashMap<String, ArrayList<HashMap<String, String>>> matrixmap = getMatrixOption(String.valueOf(qnid), dao);
		
		HashMap<String, ArrayList<String>> datamap = null;
		HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> mtdatamap = null;
		boolean loadAnswer = false;
		if((subobject!=null && subobject.length()>0) || (subobject!=null && subobject.length()>0)){
			//获取某个人的非矩阵答题数据
			datamap = getData(String.valueOf(qnid),"", mainobject,subobject,dao, itemmap, conn, optionmap);
			//获取某个人的矩阵数据
			mtdatamap = getMatrixData(dao, String.valueOf(qnid),PubFunc.decrypt(planid),  mainobject,subobject, conn);
			loadAnswer = true;
		}
		
		
		try {
			String sql = "select itemId,name,longName,item.typeId typeid,typeKind,norder,options from qn_question_item item "
					+ " join qn_question_type type on item.typeId = type.typeId where qnId = '"+qnid+"' order by norder";
			rs = dao.search(sql);
			while(rs.next()){
				HashMap<String, Object> map = new HashMap<String, Object>();
				int typekind = rs.getInt("typeKind");
				map.put("typekind", typekind);
				int itemid = rs.getInt("itemId");
				map.put("questionid", itemid);
				map.put("name", rs.getString("name"));
				map.put("longname", rs.getString("longName"));
				map.put("typeid", rs.getInt("typeid"));
				map.put("orders", rs.getInt("norder"));
				
				if(typekind==9||typekind==10||typekind==11){//描述说明题、分割线、分页符
					list.add(map);
					continue;
				}
				if(typekind==5||typekind==6)//如果是图片题，多保存一个imageSavePath属性
					map.put("imageSavePath", getPicturePath(qnid+"", conn));
				String xml = rs.getString("options");
				HashMap<String, Object> set = parseXml(xml);
				map.put("set", set);
				//填空题、打分题、量表题没有题目选项
				if(typekind!=3||typekind!=12||typekind!=13){
					ArrayList<HashMap<String, Object>> optionList = getQuestionOptions(qnid, itemid, typekind, conn);
					map.put("optionList", optionList);
					//矩阵单（多）选题有levelist
					if(typekind==8||typekind==7){
						ArrayList<HashMap<String, Object>> levellist = getMatrixOptions(qnid, itemid, typekind, conn);
						map.put("levelList", levellist);
					}
				}
				if(!loadAnswer){
					list.add(map);
					continue;
				}
					
				//拼接答案
				if(typekind==1||typekind==5){//填空题、单选（去除描述说明回显答案）
					String key = "Q"+itemid+"_1";
					Map answerMap = new HashMap();
					ArrayList<String> datas = datamap.get(key);
					if(datas!=null && !datas.isEmpty())
						answerMap.put(datas.get(0), true);
					map.put("answer", answerMap);
				}
				if(typekind==3){
					String key = "Q"+itemid+"_1";
					String codeset = (String)set.get("codeset");
					ArrayList<String> datas = datamap.get(key);
					for (int j = 0;datas!=null && j < datas.size(); j++) {
						String answer = datas.get(0);
						String codeitemdesc = "";
						if(codeset!=null&&!"".equals(codeset)){
							if("@K".equals(codeset)|| "UN".equals(codeset)|| "UM".equals(codeset)){
								String codesql = "select codeitemdesc from organization where codesetid='"+codeset+"' and codeitemid='"+answer+"'";
								rs1 = dao.search(codesql);
								while(rs1.next()){
									codeitemdesc = rs1.getString("codeitemdesc");
								}
							}else{
								String codesql = "select codeitemdesc from codeitem where codesetid='"+codeset+"' and codeitemid='"+answer+"'";
								rs1 = dao.search(codesql);
								while(rs1.next()){
									codeitemdesc = rs1.getString("codeitemdesc");
								}
							}
							answer = answer+'`'+codeitemdesc;
						}
						map.put("answer", answer);
					}
				}
				if(typekind==12||typekind==13){//打分、量表
					String key = "Q"+itemid+"_1";
					String key1 = "Q"+itemid+"_desc";
					//List listopt = new ArrayList();
					Map answerMap = new HashMap();
					ArrayList<String> datas = datamap.get(key);
					String answer = "";
					if(datas!=null&&datas.size()>0){//xiegh
						answer = datas.get(0);
					}
					answerMap.put("score", answer);
					ArrayList<String> datas1 = datamap.get(key1);
					if(datas1!=null&&datas1.size()>0){//xiegh
						String answerdesc = datas1.get(0);
						answerMap.put("desc", answerdesc);
					}
					//listopt.add(optmaps);
					map.put("answer", answerMap);
				}
				if(typekind==2||typekind==6){//多选题、图片多选题
					ArrayList<HashMap<String, String>> optlist = optionmap.get(String.valueOf(itemid));//题目的选项
					//List listopt = new ArrayList();
					if(null==optlist||optlist.size()==0)//xiegh
						continue;
					HashMap answerMap = new HashMap();
					for(int i=0;i<optlist.size();i++){
						HashMap<String, String> optmap = optlist.get(i);
						String optid = optmap.get("optid");
						String key1 = "Q"+itemid+"_"+optid;
						ArrayList<String> datas1 = datamap.get(key1);
						for (int k = 0; datas1!=null && k < datas1.size(); k++) {
							String answer = datas1.get(0);
							if("1".equals(answer)){
								//listopt.add(optid);
								answerMap.put(optid, true);
							}
							else{
							}
						}
					}
					map.put("answer", answerMap);
				}
				if(typekind==4){//多项填空题
					ArrayList<HashMap<String, String>> optlist = optionmap.get(String.valueOf(itemid));//题目的选项
					//List listopt = new ArrayList();
					HashMap answerMap = new HashMap();
					for(int i=0;i<optlist.size();i++){
						HashMap<String, String> optmap = optlist.get(i);
						String optid = optmap.get("optid");
						String key1 = "Q"+itemid+"_"+optid;
						ArrayList<String> datas1 = datamap.get(key1);
						for (int k = 0; datas1!=null && k < datas1.size(); k++) {
							String answer = datas1.get(0);
							String codeset = (String)set.get("codeset");
							String codeitemdesc = "";
							if(codeset!=null&&!"".equals(codeset)){
								if("@K".equals(codeset)|| "UN".equals(codeset)|| "UM".equals(codeset)){
									String codesql = "select codeitemdesc from organization where codesetid='"+codeset+"' and codeitemid='"+answer+"'";
									rs1 = dao.search(codesql);
									while(rs1.next()){
										codeitemdesc = rs1.getString("codeitemdesc");
									}
								}else{
									String codesql = "select codeitemdesc from codeitem where codesetid='"+codeset+"' and codeitemid='"+answer+"'";
									rs1 = dao.search(codesql);
									while(rs1.next()){
										codeitemdesc = rs1.getString("codeitemdesc");
									}
								}
								answer = answer+'`'+codeitemdesc;
							}						
							//optmaps.put("optid", optid);
							//optmaps.put("optvalue", answer);
							//listopt.add(optmaps);
							answerMap.put(optid, answer);
						}
					}
					map.put("answer", answerMap);
				}
				//拼接矩阵题答案
				if(typekind==7||typekind==8){
					//矩阵题横向选项
					ArrayList<HashMap<String, String>> matrixlist = matrixmap.get(String.valueOf(itemid));
					//矩阵题纵向选项
					ArrayList<HashMap<String, String>> mtlist = optionmap.get(String.valueOf(itemid));
					HashMap<String, ArrayList<HashMap<String, String>>> itemap = mtdatamap.get(String.valueOf(itemid));//每道题对应的答案
					//List listopt = new ArrayList();
					HashMap answerMap = new HashMap();
					for(int i=0;i<mtlist.size();i++){
						if(itemap==null)
							continue;
						HashMap<String, String> optmap = mtlist.get(i);
						String optid = optmap.get("optid");
						ArrayList<HashMap<String, String>> datalist = itemap.get(optid);//每道题的每个选项对应的答案
						if(datalist==null)
							continue;
						for(int j=0;j<matrixlist.size();j++){
							HashMap<String, String> levelmap = matrixlist.get(j);
							String levelid = levelmap.get("optid");
							String key = "C"+(Integer.parseInt(levelid)+1);
							for (int k = 0; k < datalist.size(); k++) {
								Map optmaps = new HashMap();
								HashMap<String, String> optdatamap = datalist.get(k);
								if(optdatamap.get(key)!=null&&!"".equals(optdatamap.get(key))){
									if(Integer.parseInt(optdatamap.get(key))==1){//选中的
										//optmaps.put("optid", optid);
										//optmaps.put("optvalue", levelid);
										//listopt.add(optmaps);
										answerMap.put(optid+"_"+levelid, true);
									}
								}
							}
						}
					}
					map.put("answer", answerMap);
				}	
				if(typekind==14||typekind==15){//矩阵打分题/量表题
					//获取矩阵打分题选项
					ArrayList<HashMap<String, String>> mtlist = optionmap.get(String.valueOf(itemid));
					HashMap<String, ArrayList<HashMap<String, String>>> itemap = mtdatamap.get(String.valueOf(itemid));
					//List listopt = new ArrayList();
					HashMap answerMap = new HashMap();
					for(int i=0;i<mtlist.size();i++){
						if(itemap==null)
							continue;
						HashMap<String, String> optmap = mtlist.get(i);
						String optid = optmap.get("optid");
						ArrayList<HashMap<String, String>> datalist = itemap.get(optid);//每道题的每个选项对应的答案
						if(datalist==null)
							continue;
						for (int k = 0; k < datalist.size(); k++) {
							Map optmaps = new HashMap();
							HashMap<String, String> optdatamap = datalist.get(k);
							String answer = "";
							String answerdesc = "";
							if(optdatamap.get("score")!=null&&!"".equals(optdatamap.get("score"))){
								answer = optdatamap.get("score");
								//optmaps.put("optid", optid);
								//optmaps.put("score", answer);
								answerMap.put(optid, answer);
							}
							if(optdatamap.get("C_desc")!=null&&!"".equals(optdatamap.get("C_desc"))){
								answerdesc = optdatamap.get("C_desc");
								//optmaps.put("desc", answerdesc);
								answerMap.put("desc", answerdesc);
							}
							//listopt.add(optmaps);
						}
					}
					map.put("answer", answerMap);
				}
				
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
		return list;
	}
		
	/**
	 * 根据问卷id获取问卷题目
	 * @Title: getQuestion   
	 * @Description:    
	 * @param qnid
	 * @param dao
	 * @return 
	 * @return HashMap<String,HashMap<String,String>>
	 */
	private HashMap<String, HashMap<String, String>> getQuestion(String qnid,ContentDAO dao){
		HashMap<String, HashMap<String, String>> itemmap = new HashMap<String, HashMap<String, String>>();
		StringBuffer sql = new StringBuffer();
		ResultSet rs = null;
		try {
			sql.append("select itemid,name,typekind,score from qn_question_item item join qn_question_type type ");
			sql.append("on item.typeId = type.typeId where qnId = '"+qnid+"' ");
			sql.append("and typekind <> '10' and typekind <> '11' order by norder");
			rs = dao.search(sql.toString());
			while(rs.next()){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("itemid", rs.getString("itemid"));
				map.put("name", rs.getString("name"));
				map.put("typekind", rs.getString("typekind"));
				map.put("score", rs.getString("score")==null?"":rs.getString("score"));
				itemmap.put(rs.getString("itemid"), map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return itemmap;
	}
	
	/**
	 * 根据问卷id获取题目选项
	 * @Title: getOptions   
	 * @Description:    
	 * @param qnid
	 * @param dao
	 * @return 
	 * @return HashMap<String,ArrayList<HashMap<String,String>>>
	 */
	private HashMap<String,ArrayList<HashMap<String,String>>> getOptions(String qnid,ContentDAO dao){
		HashMap<String,ArrayList<HashMap<String,String>>> optionmap = 
				new HashMap<String, ArrayList<HashMap<String, String>>>();
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("select item.itemid,optid,optname,imgurl from qn_question_item item ");
			sql.append("join qn_question_item_opts options ");
			sql.append("on item.itemId = options.itemId and item.qnId = options.qnId ");
			sql.append("where item.qnId = '"+qnid+"' order by item.norder,options.norder");
			rs = dao.search(sql.toString());
			while(rs.next()){
				ArrayList<HashMap<String,String>> arr = null;
				String itemid = rs.getString("itemid");
				if(optionmap.containsKey(itemid))
					arr = optionmap.get(itemid);
				else
					arr = new ArrayList<HashMap<String,String>>();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("optid", rs.getString("optid"));
				map.put("optname", rs.getString("optname"));
				map.put("imgurl", rs.getString("imgurl")==null?"":rs.getString("imgurl"));
				arr.add(map);
				optionmap.put(itemid, arr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return optionmap;
	}
	
	
	
	/**
	 * 根据问卷id获取矩阵题选项
	 * @Title: getMatrixOption   
	 * @Description:    
	 * @param qnid
	 * @param dao
	 * @return 
	 * @return HashMap
	 */
	private HashMap<String, ArrayList<HashMap<String, String>>> getMatrixOption(String qnid,ContentDAO dao){
		HashMap<String, ArrayList<HashMap<String, String>>> map = 
				new HashMap<String, ArrayList<HashMap<String, String>>>();
		ResultSet rs = null;
		try {
			String sql = "select itemid,optid,optname from qn_question_item_matrix_opts where "
					+"qnid = '"+qnid+"' order by itemid,norder";
			rs = dao.search(sql);
			while(rs.next()){
				ArrayList<HashMap<String, String>> arr = null;
				String itemid = rs.getString("itemid");
				if(map.containsKey(itemid))
					arr = map.get(itemid);
				else
					arr = new ArrayList<HashMap<String,String>>();
				HashMap<String, String> mp = new HashMap<String, String>();
				mp.put("optid", rs.getString("optid"));
				mp.put("optname", rs.getString("optname"));
				arr.add(mp);
				map.put(itemid, arr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
	}
	
	
	/**
	 * 获取非矩阵题数据
	 * @Title: getData   
	 * @Description:    
	 * @param qnid 试卷id
	 * @param planid 计划id
	 * @param dao
	 * @param itemmap 试卷题目map
	 * @param optionmap 试卷题目选项map
	 * @return 
	 * @return HashMap<String,ArrayList<String>>
	 */
	private HashMap<String, ArrayList<String>> getData(String qnid,String planid,String mainObject,String subObject,ContentDAO dao,
			HashMap<String, HashMap<String, String>> itemmap,Connection conn,
			HashMap<String, ArrayList<HashMap<String, String>>> optionmap){
		HashMap<String, ArrayList<String>> datamap = new HashMap<String, ArrayList<String>>();
		ResultSet rs = null;
		try {
			DbWizard w = new DbWizard(conn);
			if(!w.isExistTable("qn_"+qnid+"_data",false))
				return datamap;
			StringBuffer sql = new StringBuffer();
			sql.append("select ");
			for (String itemid : itemmap.keySet()) {
				HashMap<String, String> map = itemmap.get(itemid);
				String kind = map.get("typekind");
				if("1".equals(kind)||"3".equals(kind)||"5".equals(kind)||"9".equals(kind)
						||"12".equals(kind)||"13".equals(kind)){
					sql.append("Q"+map.get("itemid")+"_1,");
					datamap.put("Q"+map.get("itemid")+"_1",new ArrayList<String>());
					if("12".equals(kind)||"13".equals(kind)){
						sql.append("Q"+map.get("itemid")+"_desc,");
						datamap.put("Q"+map.get("itemid")+"_desc",new ArrayList<String>());
					}
				} else if("2".equals(kind)||"4".equals(kind)||"6".equals(kind)){
					ArrayList<HashMap<String, String>> arr = optionmap.get(map.get("itemid"));
					if(arr==null)
						continue;
					for (int j = 0; j < arr.size(); j++) {
						sql.append("Q"+map.get("itemid")+"_"+arr.get(j).get("optid")+",");
						datamap.put("Q"+map.get("itemid")+"_"+arr.get(j).get("optid"),new ArrayList<String>());
					}
				}
			}
			sql.delete(sql.toString().length()-1, sql.toString().length());
			sql.append(" from qn_"+qnid+"_data where mainObject='"+mainObject+"'");
			if(subObject!=null&&!"".equals(subObject)){
				sql.append(" and subObject='"+subObject+"'");
			}
			rs = dao.search(sql.toString());
			while(rs.next()){
				for (String column : datamap.keySet()) {
					ArrayList<String> arr = datamap.get(column);
					arr.add(rs.getString(column)==null?"":rs.getString(column));
					datamap.put(column, arr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return datamap;
	}
	
	/**
	 * 获取矩阵表中的数据
	 * @Title: getMatrixData   
	 * @Description:    
	 * @param dao
	 * @param sql
	 * @return 
	 * @return HashMap<String,HashMap<String,HashMap<String,Object>>>
	 */
	private HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> getMatrixData(ContentDAO dao,String qnid,String planid,String mainObject,String subObject,Connection conn){
		HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> map = 
				new HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>>();
		ResultSet rs = null;
		try {
			DbWizard w = new DbWizard(conn);
			if(!w.isExistTable("qn_matrix_"+qnid+"_data",false))
				return map;
			
			StringBuffer sql = new StringBuffer();
			sql.append("select itemid,optid,");
			for (int i = 1; i <= 10; i++) {
				sql.append("C"+i+",");
			}
			sql.append("C_desc,score from qn_matrix_"+qnid+"_data where planid = '"+planid+"' and mainObject='"+mainObject+"'");
			if(subObject!=null&&!"".equals(subObject)){
				sql.append(" and subObject='"+subObject+"'");
			}
			sql.append(" order by itemid,mainObject");
			rs = dao.search(sql.toString());
			while(rs.next()){
				String itemid = rs.getString("itemid");
				String optid = rs.getString("optid");
				HashMap<String, ArrayList<HashMap<String, String>>> itemmap = 
						new HashMap<String, ArrayList<HashMap<String, String>>>();
				if(map.containsKey(itemid))
					itemmap = map.get(itemid);
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				if(itemmap.containsKey(optid))
					list = itemmap.get(optid);
				HashMap<String, String> optmap = new HashMap<String, String>();
				for (int i = 1; i <= 10; i++) {
					optmap.put("C"+i, rs.getString("C"+i)==null?"":rs.getString("C"+i));
				}
				optmap.put("score", rs.getString("score"));
				optmap.put("C_desc", rs.getString("C_desc"));
				list.add(optmap);
				itemmap.put(optid, list);
				map.put(itemid, itemmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 获取问卷题型
	 * @Title: getQuestion   
	 * @Description:    
	 * @param objs objs为空即可
	 * @return 
	 * @return String
	 */
	public String getQuestionType(Object[] objs){
		Connection connection = null;
		RowSet rs = null;
		boolean flag = false;
		HashMap<String, ArrayList<HashMap<String, String>>> map = new HashMap<String, ArrayList<HashMap<String, String>>>();
		ArrayList<HashMap<String, String>> commonlist = new ArrayList<HashMap<String,String>>();
		ArrayList<HashMap<String, String>> scorelist = new ArrayList<HashMap<String,String>>();
		try{
			if(this.conn==null){
				connection = AdminDb.getConnection();
				flag = true;
			} else {
				connection = conn;
			}
			//xus 20/4/14 vfs-dm 创建/进入问卷设计界面，常用题型和评分题中的试题类型文字显示顺序混乱，与771不一致，比如：单选题应该在第一条
			String sql = "select typeName,typeKind,typeClass from qn_question_type order by typeClass,typeKind";
			ContentDAO dao = new ContentDAO(connection);
			rs = dao.search(sql);
			while(rs.next()){
				HashMap<String, String> obj = new HashMap<String, String>();
				String typeKind = rs.getString("typeKind");
				obj.put("typeName", rs.getString("typeName"));
				obj.put("typeKind", typeKind);
				String typeClass = rs.getString("typeClass");
				if("1".equals(typeClass)){
					commonlist.add(obj);
				} else if("2".equals(typeClass)){
					scorelist.add(obj);
				}
			}
			map.put("common", commonlist);//常用题型
			map.put("score", scorelist);//评分题
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			if(flag)
				PubFunc.closeResource(connection);
		}
		return JSON.toString(map);
	}
	
	/**
	 * 获取题目选项
	 * @Title: getQuestionOptions   
	 * @Description:    
	 * @param qnid 问卷id
	 * @param itemid 题目id
	 * @param typekind 题目类型
	 * @param conn
	 * @return 
	 * @return String
	 */
	private ArrayList<HashMap<String, Object>> getQuestionOptions(int qnid,int itemid,int typekind,Connection conn){
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		RowSet rs = null;
		try {
			String sql = "select optId,optName,optLongName,imgUrl,norder from qn_question_item_opts "
					+ " where qnId = '"+qnid+"' and itemId = '"+itemid+"' order by norder";
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("optid", rs.getInt("optId"));
				map.put("optname", rs.getString("optname"));
				map.put("optlongname", rs.getString("optLongName"));
				String imgUrl = rs.getString("imgUrl")==null?"":rs.getString("imgUrl");
				//xus 20/3/2 vfs改造
//				imgUrl = PubFunc.encrypt(imgUrl);
				map.put("imgurl",imgUrl);
				map.put("orders", rs.getInt("norder"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return list;
	}
	
	/**
	 * 获取矩阵类题目的选项
	 * @Title: getMatrixOptions   
	 * @Description:    
	 * @param qnid 问卷id
	 * @param itemid 题目id
	 * @param typekind 题目类型
	 * @param conn
	 * @return 
	 * @return String
	 */
	private ArrayList<HashMap<String, Object>> getMatrixOptions(int qnid,int itemid,int typekind,Connection conn){
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		RowSet rs = null;
		try {
			String sql = "select optId,optName,optLongName,norder from qn_question_item_matrix_opts "
					+ " where qnId = '"+qnid+"' and itemId = '"+itemid+"' order by norder";
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("optid", rs.getInt("optId"));
				map.put("optname", rs.getString("optname"));
				map.put("optlongname", rs.getString("optLongName"));
				map.put("orders", rs.getInt("norder"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return list;
	}
	
	
	/**
	 * 
	 * @param actionType 1:insert 2:update
	 * @param quesObj 试卷对象
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public int insertOrUpdateQuestionnaire(int actionType,JSONObject quesObj,Connection conn) throws Exception{
		ContentDAO dao = new ContentDAO(conn);
		RecordVo templateVo = new RecordVo("qn_template");
		
		int qnid;
		if(actionType==1){
			IDGenerator idg = new IDGenerator(2,conn);
			String idStr = idg.getId("qn_template.qnid");
			qnid = Integer.parseInt(idStr);
		}else{
			qnid = Integer.parseInt(quesObj.getString("qnid"));
			//如果是修改问卷，需要先把qn_question_item、question_item_options、qn_question_item_matrix_options表中原有的数据删除
			String sql = "delete from qn_question_item where qnid = "+qnid;
			dao.update(sql);
			sql = "delete from qn_question_item_opts where qnid = "+qnid;
			dao.update(sql);
			sql = "delete from qn_question_item_matrix_opts where qnid = "+qnid;
			dao.update(sql);
		}
		
		templateVo.setInt("qnid", qnid);
		templateVo.setString("qnname", quesObj.getString("qnname"));
		templateVo.setString("qnlongname", quesObj.getString("qnlongname"));
		templateVo.setString("instruction", quesObj.getString("instruction"));
		templateVo.setString("finishmsg", quesObj.getString("finishmsg"));
		templateVo.setString("advanceendmsg", quesObj.getString("advanceendmsg"));
		if(actionType==1)
			dao.addValueObject(templateVo);
		else
			dao.updateValueObject(templateVo);
		
		HashMap<String, String> typemap = getType(conn);
		String ques_item_sql = "insert into qn_question_item(qnid,itemid,name,longname,typeid,norder,options) values(?,?,?,?,?,?,?) ";
		List itemValues = new ArrayList();
		
		String ques_opt_sql = "insert into qn_question_item_opts(qnid,itemid,optid,optname,optlongname,imgurl,norder) values(?,?,?,?,?,?,?) ";
		List optValues = new ArrayList();
		
		String ques_mtxopt_sql = "insert into qn_question_item_matrix_opts(qnid,itemid,optid,optname,optlongname,norder) values(?,?,?,?,?,?) ";
		List mtxOptValues = new ArrayList();
		
		JSONArray arr = quesObj.getJSONArray("questionList");
		
		for(int i=0;i<arr.size();i++){
			JSONObject ques = arr.getJSONObject(i);
			List value = new ArrayList();
			
			value.add(qnid);
			value.add(i);
			value.add(ques.get("name"));
			value.add(ques.get("longname"));
			String typekind = ques.getString("typekind");
			value.add(Integer.parseInt(typemap.get(typekind)));
			value.add(i+1);
			String set = ques.containsKey("set")?ques.getString("set"):null;
			value.add(createXml(set));
			
			itemValues.add(value);
			
			if(!ques.containsKey("optionList"))
				continue;
			
			JSONArray options = ques.getJSONArray("optionList");
			if(options.size()==0)
				continue;
			
			for (int j = 0; j < options.size(); j++) {
				JSONObject option = options.getJSONObject(j);
				value = new ArrayList();
				value.add(qnid);
				value.add(i);
				value.add(j);
				value.add(option.getString("optname"));
				value.add(option.getString("optlongname"));
				String url = option.containsKey("imgurl")?option.getString("imgurl"):"";
				//xus 20/3/2 vfs改造
//				url = PubFunc.decrypt(url);
				value.add(url);
				value.add(Integer.parseInt(option.getString("orders")));
				
				optValues.add(value);	 
			}
			
			if(!ques.containsKey("levelList"))
				continue;
			
			JSONArray levels = ques.getJSONArray("levelList");
			if(levels.size()==0)
				continue;
			for (int j = 0; j < levels.size(); j++) {
				JSONObject level = levels.getJSONObject(j);
					
				value = new ArrayList();
				value.add(qnid);
				value.add(i);
				value.add(j);
				value.add(level.getString("optname"));
				value.add(level.getString("optlongname"));
				value.add(Integer.parseInt(level.getString("orders")));	
				    
				mtxOptValues.add(value);	
			}
			
		}
		
		if(itemValues.size()>0)
			dao.batchInsert(ques_item_sql, itemValues);
		if(optValues.size()>0)
			dao.batchInsert(ques_opt_sql, optValues);
		if(mtxOptValues.size()>0)
			dao.batchInsert(ques_mtxopt_sql, mtxOptValues);
		return qnid;
	}
	
	/**
	 * 保存问卷
	 * @Title: SaveTemplate   
	 * @Description:    
	 * @param objs [obj,username]
	 * @param action  0:保存问卷 / 1:发布问卷 / 2:自动保存，不提示保存成功 / 3:存为模板
	 * @return void
	 * @throws GeneralException 
	 */
	public void SaveTemplate(String action,JSONObject quesObj,UserView userView) throws Exception{
		boolean flag = false;
		Connection connection = null;
		String qnid;
		String planid;
		try {
 			if(this.conn==null){
				connection = AdminDb.getConnection();
				flag = true;
			} else {
				connection = conn;
			}
			ContentDAO dao = new ContentDAO(connection);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sdf.format(new Date());
			
			/**如果是另存模板，将原问卷copy一份，并在问卷模板表插入数据**/
			if("3".equals(action)){
				String b0110 = "";
				if(quesObj.containsKey("qnb0110")){
					b0110 = quesObj.getString("qnb0110");
					b0110 = "`".equals(b0110)?"":b0110.split("`")[0];
				}
				String qnname = "";
				if(quesObj.containsKey("qnname"))
					   qnname = quesObj.getString("qnname");
				templateVerification(qnname,b0110,"");
				//新增问卷
				int temQnId = insertOrUpdateQuestionnaire(1,quesObj,connection);
				//问卷模板表添加
				RecordVo libvo = new RecordVo("qn_template_library");
				libvo.setInt("qnid",temQnId);
				libvo.setString("createuser",userView.getUserName());
				libvo.setDate("createtime", time);
				
				libvo.setString("b0110",b0110);
				libvo.setString("qntype",quesObj.getString("qntype"));
				libvo.setInt("isshare", Integer.parseInt(quesObj.getString("qnshare")));
				dao.addValueObject(libvo);
				//qnid = quesObj.getString("qnid");
				//quesObj.put("qnid", temQnId);
				return;
			}
			
			/**除了另存，其他实际都是保存操作：**/
			
			/**如果有qnid，并且有planid说明是更新操作**/
			if(quesObj.containsKey("qnid") && quesObj.containsKey("planid")){
				questionCacheMap.remove(quesObj.get("qnid")+"");
				qnid = insertOrUpdateQuestionnaire(2,quesObj,connection)+"";
				//同步问卷计划
				RecordVo planvo = new RecordVo("qn_plan");
				planvo.setInt("planid", Integer.parseInt(quesObj.getString("planid")));
				planvo.setString("planname", quesObj.getString("qnname"));
				dao.updateValueObject(planvo);
				return;
			}
			
			/**下面就是新增操作了 **/
			
			
			/*检查计划明称是否重复*/
			String sql = "select 1 from qn_plan where createuser=? and planname=?";
			ArrayList value = new ArrayList();
			value.add(userView.getUserFullName());
			value.add(quesObj.getString("qnname"));
			value = (ArrayList)ExecuteSQL.executePreMyQuery(sql, value, connection);
			if(value.size()>0)
				throw new Exception("此问卷名称已被您使用过，请修改问卷名称！");
			
			qnid = insertOrUpdateQuestionnaire(1,quesObj,connection)+"";
			//新增问卷计划
			RecordVo planvo = new RecordVo("qn_plan");
			IDGenerator idg = new IDGenerator(2,connection);
			planid = idg.getId("qn_plan.planid");
			planid = Integer.parseInt(planid)+"";
			planvo.setInt("planid",Integer.parseInt(planid));
			planvo.setString("planname", quesObj.getString("qnname"));
			planvo.setString("createuser",userView.getUserFullName());
			planvo.setDate("createtime", time);
			planvo.setInt("status", 0);
			planvo.setString("b0110",userView.getUserOrgId());
			planvo.setInt("qnid", Integer.parseInt(qnid));
			dao.addValueObject(planvo);
			//回写qnid和planid
			quesObj.put("qnid", qnid);
			quesObj.put("planid", planid);
			
		} catch (Exception e) {
			throw e;
		} finally {
			if(flag)
				PubFunc.closeResource(connection);
		}
		
		return;
	}
	
	/**
	 * @author xiegh
	 * @param qnname
	 * @param b0110
	 * @throws Exception
	 * @description 公共方法：对模板保存操作做校验
	 * @date 2017-3-15
	 */
	public void templateVerification(String qnname,String b0110,String qnId) throws Exception {
		Connection conn = null;
		try{
			if(conn==null){
				conn = AdminDb.getConnection();
				ArrayList value = new ArrayList();
				value.add(qnname);
				//【60951】V77问卷调查：问卷设计界面，存为模板，输入的模板名称已经被使用，但是所属机构不一致，也可以保存成功，不提示让修改模板名称，不对
				//value.add(b0110);
				String sql = 
					"select a.* from qn_template a,qn_template_library  b where a.qnid = b.qnid  and  qnname = ? ";
				value = (ArrayList) ExecuteSQL
						.executePreMyQuery(sql, value, conn);
				if(value.size()>0) {
					if(StringUtils.isEmpty(qnId)) {//新增时无qnid 只校验是否存在多个，
						//应测试要求，把您这个字去掉
						throw new Exception("此模板名称已被使用过，请修改模板名称！");
					}else {//修改模板 校验查询的qnid是否与当前qnid相同  bug号：36343	
						if(value.size()==1) {
							LazyDynaBean bean=(LazyDynaBean)value.get(0);
							if(qnId.equals(bean.get("qnid"))) {
								return;
							}else {
								throw new Exception("此模板名称已被您使用过，请修改模板名称！");
							}
						}
					}
					
				}
			}
		}catch(Exception e){
			throw e;
		}finally {
			PubFunc.closeResource(conn);
		}
	}


	
	/**
	 * 根据数据生成xml
	 * @Title: createXml   
	 * @Description:    
	 * @param str JSON格式
	 * @return 
	 * @return String
	 */
	private String createXml(String str){
		if(str==null || str.trim().length()<1)
			 return "";
		JSONObject obj = JSONObject.fromObject(str);
		JSONArray array = obj.names();
		Element root = new Element("root");
		for (int i = 0; i < array.size(); i++) {
			String name = array.getString(i);
			Element ele = new Element(name);
			if("levels".equals(name)){
				JSONArray list = obj.getJSONArray(name);
				for (int j = 0; j < list.size(); j++) {
					JSONObject jo = list.getJSONObject(j);
					Element e = new Element("level"+j);
					for (Object s : jo.keySet()) {
						e.setAttribute(s.toString(), jo.getString(s.toString()));
					}
					ele.addContent(e);
				}
			} else {
				ele.setText(obj.getString(name));
			}
			root.addContent(ele);
		}
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		Document doc = new Document(root);
		XMLOutputter output = new XMLOutputter(format);
		String xml = output.outputString(doc);
		return xml;
	}
	
	/**
	 * 获取试题类型
	 * @Title: getType   
	 * @Description:    
	 * @param conn
	 * @return 
	 * @return HashMap<String,String> key为typekind，value为typeid
	 */
	private HashMap<String, String> getType(Connection conn){
		HashMap<String, String> map = new HashMap<String, String>();
		String sql = "select typekind,typeid from qn_question_type";
		ContentDAO dao = new ContentDAO(conn);
		ResultSet rs = null;
		try {
			rs = dao.search(sql);
			while(rs.next()){
				map.put(rs.getString("typekind"), rs.getString("typeid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
	}
	
	/**
	 * 发布问卷
	 * @Title: publishTemplate   
	 * @Description:    
	 * @param planid 问卷计划号
	 * @return void
	 */
	public void publishTemplate(String planid,String qnid){
		Connection connection = null;
		boolean flag = false;
		try {
			if(this.conn==null){
				connection = AdminDb.getConnection();
				flag = true;
			} else {
				connection = conn;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(new Date());
			ContentDAO dao = new ContentDAO(connection);
			RecordVo vo = new RecordVo("qn_plan");
			vo.setInt("planid", Integer.parseInt(planid));
			vo.setInt("status", 1);
			vo.setDate("pubtime", date);
		    vo.setInt("recoverycount", 0);
			dao.updateValueObject(vo);
			Object[] objs = new Object[5];
			objs[0] = "qnid="+qnid;
			String question = getTemplate(objs);
			createTemplateDataTable(question);
			
			//int qnidN = Integer.parseInt(qnid);
			//if(this.questionCacheMap.containsKey(qnidN))
			//	this.questionCacheMap.remove(qnidN);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(flag)
				PubFunc.closeResource(connection);
		}
	}
	
	/**
	 * 创建问卷答案表
	 * @Title: createTemplateDataTable   
	 * @Description:    
	 * @param question
	 * @param cleanflag
	 * @return void
	 */
	public void createTemplateDataTable(String question){
		boolean flag = false;
		Connection connection = null;
		try {
			if(this.conn==null){
				connection = AdminDb.getConnection();
				flag = true;
			} else {
				connection = conn;
			}
			boolean matrix = false;
			boolean common = false;
			
			DbWizard dbw = new DbWizard(connection);
			JSONObject obj = JSONObject.fromObject(question);
			String tablename = "qn_"+obj.getString("qnid")+"_data";
			String matrixname = "qn_matrix_"+obj.getString("qnid")+"_data";//矩阵表名称
			
			Table table = new Table(tablename);
			
			Table matrixtable = null;
			
			Field field = new Field("dataId");
			field.setDatatype(DataType.STRING);
			field.setKeyable(true);
			field.setNullable(false);
			field.setLength(40);
			table.addField(field);
			field = new Field("mainObject");
			field.setDatatype(DataType.STRING);
			field.setKeyable(false);
			field.setNullable(true);
			field.setLength(50);
			table.addField(field);
			field = new Field("subObject");
			field.setDatatype(DataType.STRING);
			field.setKeyable(false);
			field.setNullable(true);
			field.setLength(50);
			table.addField(field);
			field = new Field("cip");
			field.setDatatype(DataType.STRING);
			field.setKeyable(false);
			field.setNullable(true);
			field.setLength(50);
			table.addField(field);
			field = new Field("planId");
			field.setDatatype(DataType.STRING);
			field.setKeyable(false);
			field.setNullable(true);
			field.setLength(50);
			table.addField(field);
			field = new Field("status");
			field.setDatatype(DataType.INT);
			field.setKeyable(false);
			field.setNullable(true);
			table.addField(field);
			
			matrixtable = new Table(matrixname);
			
			//Field matrixfield = new Field("dataId");
			//matrixfield.setDatatype(DataType.STRING);
			//matrixfield.setKeyable(true);
			//matrixfield.setNullable(false);
			//matrixfield.setLength(40);
			//matrixtable.addField(matrixfield);
			
			Field matrixfield = new Field("mainObject");
			matrixfield.setDatatype(DataType.STRING);
			matrixfield.setNullable(false);
			matrixfield.setLength(50);
			matrixfield.setKeyable(false);
			matrixtable.addField(matrixfield);
			
			
			matrixfield = new Field("subObject");
			matrixfield.setDatatype(DataType.STRING);
			matrixfield.setKeyable(false);
			matrixfield.setNullable(true);
			matrixfield.setLength(50);
			matrixtable.addField(matrixfield);
			
			matrixfield = new Field("cip");
			matrixfield.setDatatype(DataType.STRING);
			matrixfield.setKeyable(false);
			matrixfield.setNullable(true);
			matrixfield.setLength(50);
			matrixtable.addField(matrixfield);
			
			matrixfield = new Field("planId");
			matrixfield.setDatatype(DataType.INT);
			matrixfield.setNullable(false);
			matrixfield.setKeyable(false);
			matrixtable.addField(matrixfield);
			
			matrixfield = new Field("itemId");
			matrixfield.setDatatype(DataType.INT);
			matrixfield.setNullable(false);
			matrixfield.setKeyable(false);
			matrixtable.addField(matrixfield);
			
			matrixfield = new Field("optId");
			matrixfield.setDatatype(DataType.STRING);
			matrixfield.setNullable(false);
			matrixfield.setLength(50);
			matrixfield.setKeyable(false);
			matrixtable.addField(matrixfield);
			
			matrixfield = new Field("score");
			matrixfield.setDatatype(DataType.FLOAT);
			matrixfield.setKeyable(false);
			matrixfield.setNullable(true);
			matrixfield.setLength(10);
			matrixfield.setDecimalDigits(2);
			matrixtable.addField(matrixfield);
				
			matrixfield = new Field("C_desc");
			matrixfield.setDatatype(DataType.CLOB);
			matrixfield.setKeyable(false);
			matrixfield.setNullable(true);
			matrixtable.addField(matrixfield);
			
			matrixfield = new Field("status");
			matrixfield.setDatatype(DataType.INT);
			matrixfield.setKeyable(false);
			matrixfield.setNullable(true);
			matrixtable.addField(matrixfield);
			
			JSONArray questionlist = (JSONArray) obj.get("questionList");
			for (int i = 0; i < questionlist.size(); i++) {
				JSONObject quesobj = questionlist.getJSONObject(i);
				String typekind = quesobj.getString("typekind");
				String questionid = quesobj.getString("questionid");
				if("1".equals(typekind)||"3".equals(typekind)||"5".equals(typekind)
					||"9".equals(typekind)||"12".equals(typekind)||"13".equals(typekind)){
					field = new Field("Q"+questionid+"_1");
					field.setDatatype(DataType.CLOB);
					field.setKeyable(false);
					field.setNullable(true);
					table.addField(field);
					if("12".equals(typekind)||"13".equals(typekind)){
						field = new Field("Q"+questionid+"_desc");
						field.setDatatype(DataType.CLOB);
						field.setKeyable(false);
						field.setNullable(true);
						table.addField(field);
					}
					common = true;
				} else if("2".equals(typekind)||"4".equals(typekind)||"6".equals(typekind)){
					JSONArray optionlist = quesobj.getJSONArray("optionList");
					for (int j = 0; j < optionlist.size(); j++) {
						JSONObject optobj = optionlist.getJSONObject(j);
						field = new Field("Q"+questionid+"_"+optobj.getString("optid"));
						field.setDatatype(DataType.CLOB);
						field.setKeyable(false);
						field.setNullable(true);
						table.addField(field);
					}
					common = true;
				} else if("7".equals(typekind)||"8".equals(typekind)||"14".equals(typekind)||"15".equals(typekind)){
					if(!matrixtable.containsKey("c1")){
						for (int j = 1; j <= 10; j++) {
							matrixfield = new Field("C"+j);
							matrixfield.setDatatype(DataType.INT);
							matrixfield.setKeyable(false);
							matrixfield.setNullable(true);
							matrixtable.addField(matrixfield);
						}
					}
					matrix = true;
				}
			}

			DBMetaModel dbmodel = new DBMetaModel(connection);
			if(dbw.isExistTable(tablename,false))
				dbw.dropTable(tablename);
			if(common) {
				dbw.createTable(table);
				dbmodel.reloadTableModel(tablename);
			}
			if(dbw.isExistTable(matrixname, false));
				dbw.dropTable(matrixname);
			if(matrix) {
				dbw.createTable(matrixtable);
				dbmodel.reloadTableModel(matrixname);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(flag)
				PubFunc.closeResource(connection);
		}
	}
	
	/**
	 * 获取图片保存路径
	 * @Title: getPicturePath   
	 * @Description:    
	 * @param qnid
	 * @return 
	 * @return String
	 */
	public String getPicturePath(String qnid, Connection conn){
		String path = "";
		try {
			ConstantXml constantXml = new ConstantXml(conn,"FILEPATH_PARAM");
	        String RootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
	        path = RootDir+File.separator+qnid;
	        File file = new File(path);
	        if(!file.exists())
	        	file.mkdirs();
	        path = path+File.separator;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return PubFunc.encrypt(path);
	}
	
	/**
	 * @Title: createPlan   
	 * @Description:创建计划（计划创建完成即为发布状态）
	 * @param qnid 计划id
	 * @param qnname 计划名称
	 * @param username 创建人名字
	 * @param b0110 创建人b0110
	 * @return 
	 * @return String
	 */
	public String createPlan(String qnid, String qnname, String username, String b0110){
		boolean flag = false;
		Connection connection = null;
		String planid = "";
		try {
			if(this.conn==null){
				connection = AdminDb.getConnection();
				flag = true;
			} else {
				connection = conn;
			}
			RecordVo vo = new RecordVo("qn_plan");
			IDGenerator idg = new IDGenerator(2,connection);
			planid = idg.getId("qn_plan.planid");
			vo.setString("planid", planid);
			vo.setString("planname", qnname);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sdf.format(new Date());
			vo.setString("createtime", time);
			vo.setString("qnid", qnid);
			vo.setString("status", "1");
			vo.setString("createuser", username);
			vo.setString("pubtime", time);
			vo.setString("b0110", b0110);
			vo.setString("recoverycount", "0");
			ContentDAO dao = new ContentDAO(connection);
			dao.addValueObject(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(flag){
				PubFunc.closeResource(connection);
			}
		}
		return planid;
	}
	
	/**
	 * 查询到期的问卷，并将问卷状态置为已结束
	 * @Title: searchExpirePlan   
	 * @Description:     
	 * @return void
	 */
	public void searchExpirePlan(){
		boolean flag = false;
		Connection connection = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		try {
			if(this.conn==null){
				connection = AdminDb.getConnection();
				flag = true;
			} else {
				connection = conn;
			}
			ArrayList<String> list = new ArrayList<String>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			sb.append("select planid,");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sb.append("to_char(pubtime,'yyyy-mm-dd hh24:mi:ss') pubtime,");
			else
				sb.append("pubtime,");
			sb.append("tp_options from qn_plan qplan join qn_template temp ");
			sb.append("on qplan.qnid = temp.qnid where status = 1 and tp_options is not null");
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)
				sb.append(" and DATALENGTH(tp_options) > 0");
			
			ContentDAO dao = new ContentDAO(connection);
			rs = dao.search(sb.toString());
			while(rs.next()){
				HashMap<String, Object> map = parseXml(rs.getString("tp_options"));
				if(map.containsKey("enddateselected")&&map.containsKey("enddatevalue")){
					if(!"1".equals(map.get("enddateselected")))
						continue;
					String num = (String) map.get("enddatevalue");
					String pubtime = rs.getString("pubtime");
					long time = PubFunc.getHourSpan(sdf.parse(pubtime), new Date());
					BigDecimal bd = new BigDecimal(time);
					BigDecimal length = new BigDecimal(Integer.parseInt(num)*24);
					if(bd.compareTo(length)>-1){
						list.add(rs.getString("planid"));
					}
				}
			}
			sb = new StringBuffer();
			sb.append("update qn_plan set status = 3 where 1 = 0 ");
			for (String str : list) {
				sb.append(" or planid = "+str);
			}
			dao.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			if(flag){
				PubFunc.closeResource(connection);
			}
		}
	}
	
	/**
	 * 更新计划问卷收集份数，自动关闭问卷
	 * @param autocloseselected
	 * @param planId
	 * @param autoclosevalue
	 * @param matrixResultdatasub 
	 * @param matrixResultdata 
	 */
	public boolean updateRecoveryCount(int autoclose,int planId,int count,
			String mainObject,UserView userView,String qnid,Connection conn){
		RowSet rs = null;
		try {
			DbWizard dw = new DbWizard(conn);
			ContentDAO dao = new ContentDAO(conn);
			String sequence_name = "qn_"+qnid+"_data";
			String sequence_namematr =  "qn_matrix_"+qnid+"_data";
			StringBuffer checksql = new  StringBuffer();
			int recoveryCount = 0;
			
			boolean isExist = false;
			//如果有userview，查询重复的答题记录，如果有，不更新收集份数了
			if(userView!=null){
				
				if(dw.isExistTable(sequence_name, false)){
					checksql.append("select 1 from "+sequence_name+" where mainObject='"+mainObject+"' and status='2'");
					rs=dao.search(checksql.toString());
					if(rs.next())
						isExist = true;
				}
				
				if(!isExist && dw.isExistTable(sequence_namematr, false)){
					checksql.setLength(0);
					checksql.append("select 1 from "+sequence_namematr+" where mainObject='"+mainObject+"' and status='2'");
					rs=dao.search(checksql.toString());
					if(rs.next())//如果有的话
						isExist = true;
				}
					
				if(isExist)	
					return true;
			}
			//如果没有设置 收集份数自动关闭,份数直接+1
			if(autoclose!=1){
				String upsqls = "update qn_plan set recoveryCount=recoveryCount+1 where planId='"+planId+"'";
				dao.update(upsqls);
				return true;
			}
			
			//如果最大收集份数小于1，当做不设置
			if(count<1){
				String upsqls = "update qn_plan set recoveryCount=recoveryCount+1 where planId='"+planId+"'";
				dao.update(upsqls);
				return true;
			}
			
			//走到这说明需要 判断收集份数自动关闭问卷
			synchronized (TemplateBo.class) {
				String sql = "select recoveryCount from qn_plan where planId='"+planId+"'";
				rs = dao.search(sql);
				rs.next();
				recoveryCount = rs.getInt("recoveryCount");
				
				if(recoveryCount+1>count)
					return false;
				if(recoveryCount+1 == count){
					sql = "update qn_plan set status='3' where planId = '"+planId+"'";
					dao.update(sql);
					//return false;
				}
				String upsqls = "update qn_plan set recoveryCount=recoveryCount+1 where planId='"+planId+"'";
				dao.update(upsqls);
			}
				
		} catch (Exception e) {
				e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 更新热点调查里 问卷名称    wangb 20190523 bug 48105
	 * @param planid 问卷id号
	 */
	public void updatePendingTaskPlanName(Connection conn,String planid){
		ContentDAO dao = new ContentDAO(conn);
		RecordVo vo = new RecordVo("qn_plan");
		vo.setInt("planid", Integer.parseInt(planid));
		try {
			vo = dao.findByPrimaryKey(vo);
			String url = "/module/system/questionnaire/template/AnswerQn.jsp?suerveyid="+PubFunc.encryption(planid);
			String title = "";
			String sql = "update t_hr_pendingtask set pending_title=? where pending_url=? and Pending_type='80' and (pending_status='0' or pending_status='3')";
			ArrayList list = new ArrayList();
			list.add(vo.getString("planname"));
			list.add(url);
			dao.update(sql, list);
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取用户对应权限的单位编号和单位名称
	 * @return
	 */
	public String getFunc() {
		String func = "";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String sql = "";
		try {
			sql = "select codeitemid,codeitemdesc from organization where codesetid='UN' and codeitemid=parentid order by codeitemid asc";
			rs = dao.search(sql);
			//默认只会显示一条
			if(rs.next()){
				func = rs.getString("codeitemid")+"`"+rs.getString("codeitemdesc");
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return func;
	}
}
