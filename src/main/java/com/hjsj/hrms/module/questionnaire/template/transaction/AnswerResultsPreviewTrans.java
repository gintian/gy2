package com.hjsj.hrms.module.questionnaire.template.transaction;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerResultsPreviewTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException{
		PubFunc pubfunc = new PubFunc();
		String mainObject = (String)this.getFormHM().get("mainObject");
		String subObject = (String)this.getFormHM().get("subObject");
		if(!"".equals(mainObject)&&mainObject!=null){
			mainObject = PubFunc.decrypt(mainObject);
		}
		if(!"".equals(subObject)&&subObject!=null){
			subObject = PubFunc.decrypt(subObject);
		}
		Object qnid =  this.getFormHM().get("qnid");
		Object[] objs = new Object[1];
		objs[0] = "qnid="+qnid;
		String jsonobject = getTemplate(objs,mainObject,subObject);
		this.getFormHM().put("jsonobject", jsonobject);
	}
	private String getTemplate(Object[] objs,String mainObject,String subObject){
		HashMap<String, Object> map = new HashMap<String, Object>();
		Connection connection = null;
		RowSet rs = null;
		boolean flag = false;
		try {
			connection = AdminDb.getConnection();
			int qnid = -1;
			String qnname = "";
			for (int i = 0; i < objs.length; i++) {
				String obj = (String) objs[i];
				if(obj==null)
					continue;
				if(obj.contains("qnid=")){
					qnid = Integer.parseInt(PubFunc.decrypt(obj.substring(obj.indexOf("=")+1)));
				} else if(obj.contains("qnname=")){
					qnname = obj.substring(obj.indexOf("=")+1);
				}
			}
			//获取问卷名称，填空说明，正常结束、提前结束时提示信息及配置参数。
			map = getTemplateInfo(qnid,qnname,connection);
			//获取问卷题目
			ArrayList<HashMap<String, Object>> questionList = getTemplateQuestion(qnid,mainObject,subObject,connection);
			map.put("questionList", questionList);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(connection);
		}
		return JSON.toString(map);
	}
	/**
	 * 获取问卷信息及配置参数
	 * @Title: getTemplateInfo   
	 * @Description:    
	 * @param qnid
	 * @return 
	 * @return String
	 */
	private HashMap<String, Object> getTemplateInfo(int qnid,String qnname,Connection conn){
		HashMap<String, Object> map = new HashMap<String, Object>();
		RowSet rs = null;
		try{
			String sql = "select qnName,qnLongName,instruction,finishMsg,advanceEndMsg,"
						+ "tp_options from qn_template where qnId = '"+qnid+"'";
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			while(rs.next()){
				if(qnname.length()>0){
					map.put("qnname", qnname);
					map.put("qnlongname", qnname);
				} else {
					map.put("qnname", rs.getString("qnName"));
					map.put("qnlongname", rs.getString("qnLongName"));
				}
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
	 * 获取问卷题目
	 * @Title: getTemplateQuestion   
	 * @Description:    
	 * @param qnid
	 * @param conn
	 * @return 
	 * @return String
	 */
	private ArrayList<HashMap<String, Object>> getTemplateQuestion(int qnid,String mainObject,String subObject,Connection conn){
		ContentDAO dao = new ContentDAO(conn);
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		//问卷题目
		HashMap<String, HashMap<String, String>> itemmap = getQuestion(String.valueOf(qnid), dao);
		//题目选项
		HashMap<String, ArrayList<HashMap<String, String>>> optionmap = getOptions(String.valueOf(qnid), dao);
		//矩阵选项
		HashMap<String, ArrayList<HashMap<String, String>>> matrixmap = getMatrixOption(String.valueOf(qnid), dao);
		//获取某个人的非矩阵答题数据
		HashMap<String, ArrayList<String>> datamap = getData(String.valueOf(qnid), mainObject,subObject,dao, itemmap, conn, optionmap);
		//获取某个人的矩阵数据
		HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> mtdatamap = getMatrixData(dao, String.valueOf(qnid),  mainObject,subObject, conn);
		RowSet rs = null;
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
				//拼接答案
				if(typekind==3||typekind==1||typekind==5||typekind==9){//填空题、单选
					String key = "Q"+itemid+"_1";
					ArrayList<String> datas = datamap.get(key);
					for (int j = 0; j < datas.size(); j++) {
						String answer = datas.get(0);
						map.put("answer", answer);
					}
				}
				if(typekind==12||typekind==13){//打分、量表
					String key = "Q"+itemid+"_1";
					String key1 = "Q"+itemid+"_desc";
					List listopt = new ArrayList();
					Map optmaps = new HashMap();
					ArrayList<String> datas = datamap.get(key);
					String answer = "";
					if(datas.size()>0){
						answer = datas.get(0);
					}
					optmaps.put("score", answer);
					ArrayList<String> datas1 = datamap.get(key1);
					if(datas1!=null){
						String answerdesc = datas1.get(0);
						optmaps.put("desc", answerdesc);
					}
					listopt.add(optmaps);
					map.put("answer", listopt);
				}
				if(typekind==2||typekind==6){//多选题、图片多选题
					ArrayList<HashMap<String, String>> optlist = optionmap.get(String.valueOf(itemid));//题目的选项
					List listopt = new ArrayList();
					for(int i=0;i<optlist.size();i++){
						HashMap<String, String> optmap = optlist.get(i);
						String optid = optmap.get("optid");
						String key1 = "Q"+itemid+"_"+optid;
						ArrayList<String> datas1 = datamap.get(key1);
						for (int k = 0; k < datas1.size(); k++) {
							String answer = datas1.get(0);
							if("1".equals(answer)){
								listopt.add(optid);
							}
							else{
							}
						}
					}
					map.put("answer", listopt);
				}
				if(typekind==4){//多项填空题
					ArrayList<HashMap<String, String>> optlist = optionmap.get(String.valueOf(itemid));//题目的选项
					List listopt = new ArrayList();
					for(int i=0;i<optlist.size();i++){
						HashMap<String, String> optmap = optlist.get(i);
						String optid = optmap.get("optid");
						String key1 = "Q"+itemid+"_"+optid;
						ArrayList<String> datas1 = datamap.get(key1);
						for (int k = 0; k < datas1.size(); k++) {
							Map optmaps = new HashMap();
							String answer = datas1.get(0);
							optmaps.put("optid", optid);
							optmaps.put("optvalue", answer);
							listopt.add(optmaps);
						}
					}
					map.put("answer", listopt);
				}
				//拼接矩阵题答案
				if(typekind==7||typekind==8){
					//矩阵题横向选项
					ArrayList<HashMap<String, String>> matrixlist = matrixmap.get(String.valueOf(itemid));
					//矩阵题纵向选项
					ArrayList<HashMap<String, String>> mtlist = optionmap.get(String.valueOf(itemid));
					HashMap<String, ArrayList<HashMap<String, String>>> itemap = mtdatamap.get(String.valueOf(itemid));//每道题对应的答案
					List listopt = new ArrayList();
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
							String key = "C"+levelid;
							for (int k = 0; k < datalist.size(); k++) {
								Map optmaps = new HashMap();
								HashMap<String, String> optdatamap = datalist.get(k);
								if(optdatamap.get(key)!=null&&!"".equals(optdatamap.get(key))){
									if(Integer.parseInt(optdatamap.get(key))==1){//选中的
										optmaps.put("optid", optid);
										optmaps.put("optvalue", levelid);
										listopt.add(optmaps);
									}
								}
							}
						}
					}
					map.put("answer", listopt);
				}	
				if(typekind==14||typekind==15){//矩阵打分题/量表题
					//获取矩阵打分题选项
					ArrayList<HashMap<String, String>> mtlist = optionmap.get(String.valueOf(itemid));
					HashMap<String, ArrayList<HashMap<String, String>>> itemap = mtdatamap.get(String.valueOf(itemid));
					List listopt = new ArrayList();
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
								optmaps.put("optid", optid);
								optmaps.put("score", answer);
							}
							if(optdatamap.get("C_desc")!=null&&!"".equals(optdatamap.get("C_desc"))){
								answerdesc = optdatamap.get("C_desc");
								optmaps.put("desc", answerdesc);
							}
							listopt.add(optmaps);
						}
					}
					map.put("answer", listopt);
				}
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
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
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
	private HashMap<String, ArrayList<String>> getData(String qnid,String mainObject,String subObject,ContentDAO dao,
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
			sql.append(" from qn_"+qnid+"_data where mainObject='"+mainObject+"' and subObject='"+subObject+"'");
			
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
	 * 获取图片保存路径
	 * @Title: getPicturePath   
	 * @Description:    
	 * @param qnid
	 * @return 
	 * @return String
	 */
	private String getPicturePath(String qnid, Connection conn){
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
		return path;
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
				map.put("imgurl", rs.getString("imgUrl")==null?"":rs.getString("imgUrl"));
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
	 * 获取矩阵表中的数据
	 * @Title: getMatrixData   
	 * @Description:    
	 * @param dao
	 * @param sql
	 * @return 
	 * @return HashMap<String,HashMap<String,HashMap<String,Object>>>
	 */
	private HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> getMatrixData(ContentDAO dao,String qnid,String mainObject,String subObject,Connection conn){
		HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> map = 
				new HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>>();
		ResultSet rs = null;
		try {
			DbWizard w = new DbWizard(conn);
			int planid = 0;
			String plansql = "select planid from qn_plan where qnid='"+qnid+"'";
			this.frowset = dao.search(plansql);
			while(this.frowset.next()){
				planid = this.frowset.getInt("planid");
			}
			if(!w.isExistTable("qn_matrix_"+qnid+"_data",false))
				return map;
			
			StringBuffer sql = new StringBuffer();
			sql.append("select itemid,optid,");
			for (int i = 1; i <= 10; i++) {
				sql.append("C"+i+",");
			}
			sql.append("score from qn_matrix_"+qnid+"_data where planid = '"+planid+"' and mainObject='"+mainObject+"' and subObject='"+subObject+"' order by itemid,mainObject");
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
}          
