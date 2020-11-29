package com.hjsj.hrms.module.questionnaire.analysis.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.Pageable;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

/**
 * <p>Title: AnalysisBo </p>
 * <p>Description: 获取问卷数据</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-9-8 下午3:23:47</p>
 * @author jingq
 * @version 1.0
 */
public class AnalysisBo {
	
	private Connection conn = null;

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public AnalysisBo() {
		
	}

	public AnalysisBo(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * 获取原始数据分析需要的数据
	 * @Title: getDataAnalysis   
	 * @Description:    
	 * @param qnid 问卷id
	 * @param planid 计划id
	 * @param subObject 被调查对象
	 * @param userView
	 * @return 
	 * @return String
	 */
	public String getDataAnalysis(String qnid, String planid, String subObject, UserView userView){
		String returnstr = "";
		Connection connection = null;
		boolean flag = false;
		ResultSet rs = null;
		try {
			if(this.conn==null){
				connection = AdminDb.getConnection();
				flag = true;
			} else {
				connection = conn;
			}
			ContentDAO dao = new ContentDAO(connection);
			//问卷题目
			HashMap<String, HashMap<String, String>> itemmap = getQuestion(qnid, dao);
			//题目选项
			HashMap<String, ArrayList<HashMap<String, String>>> optionmap = getOptions(qnid, dao);
			//矩阵题横向选项
			HashMap<String, ArrayList<HashMap<String, String>>> matrixmap = getMatrixOption(qnid, dao);
			//量表题选项
			HashMap<String, HashMap<String, String>> scalemap = getScaleLevel(dao, qnid);
			
			//题目顺序
			ArrayList<String> orderlist = new ArrayList<String>();
			rs = dao.search("select itemid from qn_question_item item join qn_question_type type " +
					"on item.typeId = type.typeId where qnid = '"+qnid+"' " +
					"and  typeKind <> 9 and typeKind <> 10 and typeKind <> 11 order by norder");
			while(rs.next()){
				orderlist.add(rs.getString("itemid"));
			}
			//表格头
			ArrayList<ColumnsInfo> columnlist = new ArrayList<ColumnsInfo>();
			ColumnsInfo cl = new ColumnsInfo();
			cl.setColumnDesc(ResourceFactory.getProperty("label.serialnumber"));
			cl.setColumnId("index");
			cl.setTextAlign("center");
			cl.setSortable(false);//add by xiegh
			cl.setColumnType("N");
			//cl.setLocked(false);
			cl.setColumnWidth(50);
			columnlist.add(cl);
			
			for (int i = 0; i < orderlist.size(); i++) {
				String itemid = orderlist.get(i);
				String itemname = itemmap.get(itemid).get("name");
				String typekind = itemmap.get(itemid).get("typekind");
				
				if("9".equals(typekind)||"10".equals(typekind)||"11".equals(typekind))
					continue;
				//题目选项
				ArrayList<HashMap<String, String>> optionlist = optionmap.get(itemid);
				
				if("3".equals(typekind)||"12".equals(typekind)||"13".equals(typekind)){//填空题、打分题、量表题
					ColumnsInfo column = new ColumnsInfo();
					column.setColumnDesc(itemname);
					column.setTextAlign("center");
					column.setLocked(false);//changxy
					column.setSortable(false);//add by xiegh 
					String key = "Q"+itemid+"_1";
					column.setColumnId(key.toLowerCase());
					columnlist.add(column);
				}else if("7".equals(typekind)||"8".equals(typekind)){//矩阵单（多）选
					ColumnsInfo ci = new ColumnsInfo();
					ci.setColumnId("Q"+itemid+"_typekind");
					ci.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					columnlist.add(ci);
					
					ci = new ColumnsInfo();
					ci.setColumnDesc(itemname);
					ArrayList<HashMap<String, String>> matrixlist = matrixmap.get(itemid);
					for (int j = 0; j < optionlist.size(); j++) {
						String optid = optionlist.get(j).get("optid");
						ColumnsInfo c = new ColumnsInfo();
						c.setColumnDesc(optionlist.get(j).get("optname"));
						for (int k = 0; k < matrixlist.size(); k++) {
							ColumnsInfo column = new ColumnsInfo();
							column.setColumnDesc(matrixlist.get(k).get("optname"));
							column.setColumnType("N");
							column.setSortable(false);//add by xiegh
							column.setTextAlign("center");
							column.setRendererFunc("analysisCheckRenderFn");
							column.setLocked(false);//changxy
							String matrixid = matrixlist.get(k).get("optid");
							String key = "Q"+itemid+"_"+optid+"_"+matrixid;
							column.setColumnId(key.toLowerCase());
							c.addChildColumn(column);
						}
						ci.addChildColumn(c);
					}
					columnlist.add(ci);
				}else {
					ColumnsInfo ci = new ColumnsInfo();
					ci.setColumnId("Q"+itemid+"_typekind");
					ci.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					columnlist.add(ci);
					
					ci = new ColumnsInfo();
					ci.setColumnDesc(itemname);
					ArrayList<HashMap<String, String>> optlist = optionmap.get(itemid);
					if(optlist==null)
						continue;
					for (int j = 0; j < optlist.size(); j++) {
						HashMap<String, String> optmap = optlist.get(j);
						ColumnsInfo column = new ColumnsInfo();
						column.setColumnDesc(optmap.get("optname"));
						column.setSortable(false);//add by xiegh
						column.setTextAlign("center");
						column.setLocked(false);//changxy
						String optid = optlist.get(j).get("optid");
						if("14".equals(typekind)||"15".equals(typekind)){//矩阵打分题、矩阵量表题
							if("14".equals(typekind))
								column.setColumnType("N");
							String key = "Q"+itemid+"_"+optid;
							column.setColumnId(key.toLowerCase());
						} else {
							String key = "Q"+itemid+"_"+optid;
							column.setColumnId(key.toLowerCase());
							column.setSortable(false);
							if(!"4".equals(typekind)){
								column.setColumnType("N");
								column.setRendererFunc("analysisCheckRenderFn");
							}
						}
						ci.addChildColumn(column);
					}
					columnlist.add(ci);
				}
			}
			
			//表格数据
			ArrayList<LazyDynaBean> datalist = new ArrayList<LazyDynaBean>();
			//非矩阵数据
			HashMap datamap = getTableData(qnid, planid, subObject, dao, itemmap, connection, optionmap);
			//矩阵数据
			HashMap mtdatamap = getTableMatrixData(dao, qnid, planid, subObject, connection);
			
			HashSet recordKey = new HashSet();
			
			recordKey.addAll(datamap.keySet());
			Set mtRecordKey = mtdatamap.keySet();
			
			Iterator ite= mtRecordKey.iterator();
			while(ite.hasNext()) {
				String key = (String)ite.next();
				if(!recordKey.contains(key))
					recordKey.add(key);
			}
			
			Iterator keyIte =  recordKey.iterator();
			int indexValue = 1;
			while(keyIte.hasNext()) {
				String keyValue = (String)keyIte.next();
				HashMap record = (HashMap)datamap.get(keyValue);
				HashMap mtAnswers = (HashMap)mtdatamap.get(keyValue);
				mtAnswers = mtAnswers==null?new HashMap():mtAnswers;
				LazyDynaBean rowData = new LazyDynaBean();
				rowData.set("index", indexValue);
				indexValue++;
				for (int i = 0; i < orderlist.size(); i++) {
					String itemid = orderlist.get(i);
					String typekind = itemmap.get(itemid).get("typekind");
					if("9".equals(typekind)||"10".equals(typekind)||"11".equals(typekind))
						continue;
					
					rowData.set(("Q"+itemid+"_typekind").toLowerCase(), typekind);
					
					//题目选项
					ArrayList<HashMap<String, String>> optionlist = optionmap.get(itemid);
					if("1".equals(typekind)||"5".equals(typekind)) {//单选 和 图片单选题
						String field = "Q"+itemid+"_1";
						String value = (String)record.get(field);
						value = value==null?"":value;
						if(value.length()<1)
							continue;

						//有个别选择题，设置了题目，但是没有给选项，导致空指针异常
						if(optionlist == null){
							continue;
						}
						for (int j = 0; j < optionlist.size(); j++) {
							String optid = optionlist.get(j).get("optid");
							String realField = "Q"+itemid+"_"+optid;
							
							if(optid.equals(value))
								rowData.set(realField.toLowerCase(), "1");
							else
								rowData.set(realField.toLowerCase(), "");
						}
						
					}else if("2".equals(typekind) || "6".equals(typekind)) { //多选 和 图片多选
						//有个别选择题，设置了题目，但是没有给选项，导致空指针异常
						if(optionlist == null){
							continue;
						}
						for (int j = 0; j < optionlist.size(); j++) {
							String optid = optionlist.get(j).get("optid");
							String field = "Q"+itemid+"_"+optid;
							
							String value = (String)record.get(field);
							value = value==null?"":value;
							
							if("1".equals(value))
								rowData.set(field.toLowerCase(), "1");
							else
								rowData.set(field.toLowerCase(), "");
						}
						
					}else if("3".equals(typekind)) {//填空题
						
						String field = "Q"+itemid+"_1";
						String value = (String)record.get(field);
						value = value==null?"":value;
						
						String code = getItemCodeSet(qnid, itemid, dao);
						
						if(code!=null&&!"".equals(code))
							value = AdminCode.getCodeName(code, value);
						
						rowData.set(field.toLowerCase(), value);
					}else if("4".equals(typekind)){ //多项填空题
						
						for (int j = 0; j < optionlist.size(); j++) {
							String optid = optionlist.get(j).get("optid");
							String field = "Q"+itemid+"_"+optid;
							
							String value = (String)record.get(field);
							value = value==null?"":value;
							
							String code = getItemCodeSet(qnid, itemid, dao);
							if(code!=null&&!"".equals(code))
								value = AdminCode.getCodeName(code, value);
							
							rowData.set(field.toLowerCase(), value);
						}
					}else if("7".equals(typekind)||"8".equals(typekind)) {//矩阵单多选
						ArrayList<HashMap<String, String>> matrixlist = matrixmap.get(itemid);
						for (int j = 0; j < optionlist.size(); j++) {
							String optid = optionlist.get(j).get("optid");
							for (int k = 0; k < matrixlist.size(); k++) {
								String matrixid = matrixlist.get(k).get("optid");
								String field = "Q"+itemid+"_"+optid+"_"+matrixid;
								HashMap mtRecord = (HashMap)mtAnswers.get(itemid+"_"+optid);
								if(mtRecord==null)
									continue;
								rowData.set(field.toLowerCase(), mtRecord.get("C"+(k+1)));
							}
						}
					}else if("12".equals(typekind)) { //打分题
						String field = "Q"+itemid+"_1";
						String value = (String)record.get(field);
						value = value==null?"":value;
						rowData.set(field.toLowerCase(), value);
						
					}else if("13".equals(typekind)){//量表题显示 题目选项（分数）
						HashMap<String, String> scale = scalemap.get(itemid);
						String field = "Q"+itemid+"_1";
						String value = (String)record.get(field);
						value = value==null?"":value;
						if(!"".equals(value))
							value = scale.get(value)+"("+value+")";
						
						rowData.set(field.toLowerCase(), value);
					}else if("14".equals(typekind)) {
						
						for (int j = 0; j < optionlist.size(); j++) {
							String optid = optionlist.get(j).get("optid");
							String field = "Q"+itemid+"_"+optid;
							HashMap mtRecord = (HashMap)mtAnswers.get(itemid+"_"+optid);
							if(mtRecord==null)
								continue;
							String score  = (String)mtRecord.get("score");
							rowData.set(field.toLowerCase(), score);
						}
					}else if("15".equals(typekind)){//矩阵打分、量表
						
						for (int j = 0; j < optionlist.size(); j++) {
							String optid = optionlist.get(j).get("optid");
							String field = "Q"+itemid+"_"+optid;
							HashMap mtRecord = (HashMap)mtAnswers.get(itemid+"_"+optid);
							if(mtRecord==null)
								continue;
							String score  = (String)mtRecord.get("score");
							score = score==null?"":score;
							if(score.indexOf(".")!=-1){
								score = score.substring(0, score.indexOf("."));
							}
							String value = scalemap.get(itemid).get(score);
							rowData.set(field.toLowerCase(), value);
						}
					}
				}
				
				datalist.add(rowData);
			}
			
			/*
			
			for (int i = 0; i < orderlist.size(); i++) {
				String itemid = orderlist.get(i);
				String itemname = itemmap.get(itemid).get("name");
				String typekind = itemmap.get(itemid).get("typekind");
				if("9".equals(typekind)||"10".equals(typekind)||"11".equals(typekind))
					continue;
				//题目选项
				ArrayList<HashMap<String, String>> optionlist = optionmap.get(itemid);
				if("3".equals(typekind)||"12".equals(typekind)||"13".equals(typekind)){//填空题、打分题、量表题
					ColumnsInfo column = new ColumnsInfo();
					column.setColumnDesc(itemname);
					column.setTextAlign("center");
					column.setLocked(false);//changxy
					column.setSortable(false);//add by xiegh 
					String key = "Q"+itemid+"_1";
					column.setColumnId(key.toLowerCase());
					columnlist.add(column);
					ArrayList<String> datas = datamap.get(key);
					for (int j = 0;datas!=null && j < datas.size(); j++) {
						LazyDynaBean data = new LazyDynaBean();
						if(datalist.size()>j)
							data = datalist.get(j);
						data.set("index", j+1);
						if("13".equals(typekind)){//量表题显示 题目选项（分数）
							HashMap<String, String> scale = scalemap.get(itemid);
							String str = datas.get(j);
							if(!"".equals(str))
								data.set(key.toLowerCase(), scale.get(str)+"("+str+")");
						} else if("12".equals(typekind)){//打分题直接显示数据
							data.set(key.toLowerCase(), datas.get(j));
						} else {//填空题将code转为文字
							String code = getItemCodeSet(qnid, itemid, dao);
							if(code!=null&&!"".equals(code))
								data.set(key.toLowerCase(), AdminCode.getCodeName(code, datas.get(j)));
							else
								data.set(key.toLowerCase(), datas.get(j));
						}
						if(datalist.size()>j)
							datalist.set(j, data);
						else
							datalist.add(data);
					}
				} else if("7".equals(typekind)||"8".equals(typekind)){//矩阵单（多）选
					ColumnsInfo ci = new ColumnsInfo();
					ci.setColumnId("Q"+itemid+"_typekind");
					ci.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					columnlist.add(ci);
					
					ci = new ColumnsInfo();
					ci.setColumnDesc(itemname);
					ArrayList<HashMap<String, String>> matrixlist = matrixmap.get(itemid);
					for (int j = 0; j < optionlist.size(); j++) {
						String optid = optionlist.get(j).get("optid");
						ColumnsInfo c = new ColumnsInfo();
						c.setColumnDesc(optionlist.get(j).get("optname"));
						for (int k = 0; k < matrixlist.size(); k++) {
							ColumnsInfo column = new ColumnsInfo();
							column.setColumnDesc(matrixlist.get(k).get("optname"));
							column.setColumnType("N");
							column.setSortable(false);//add by xiegh
							column.setTextAlign("center");
							column.setRendererFunc("analysisCheckRenderFn");
							column.setLocked(false);//changxy
							String matrixid = matrixlist.get(k).get("optid");
							String key = "Q"+itemid+"_"+optid+"_"+matrixid;
							column.setColumnId(key.toLowerCase());
							c.addChildColumn(column);
							if(mtdatamap.get(itemid)!=null&&mtdatamap.get(itemid).get(optid)!=null){
								ArrayList<HashMap<String, String>> datas = mtdatamap.get(itemid).get(optid);
								for (int l = 0; l < datas.size(); l++) {
									LazyDynaBean data = new LazyDynaBean();
									if(datalist.size()>l)
										data = datalist.get(l);
									data.set("index", l+1);
									data.set(("Q"+itemid+"_typekind").toLowerCase(), typekind);
									data.set(key.toLowerCase(), datas.get(l).get("C"+(k+1)));
									if(datalist.size()>l)
										datalist.set(l, data);
									else
										datalist.add(data);
								}
							}
						}
						ci.addChildColumn(c);
					}
					columnlist.add(ci);
				} else {
					ColumnsInfo ci = new ColumnsInfo();
					ci.setColumnId("Q"+itemid+"_typekind");
					ci.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					columnlist.add(ci);
					
					ci = new ColumnsInfo();
					ci.setColumnDesc(itemname);
					ArrayList<HashMap<String, String>> optlist = optionmap.get(itemid);
					if(optlist==null)
						continue;
					for (int j = 0; j < optlist.size(); j++) {
						HashMap<String, String> optmap = optlist.get(j);
						ColumnsInfo column = new ColumnsInfo();
						column.setColumnDesc(optmap.get("optname"));
						column.setSortable(false);//add by xiegh
						column.setTextAlign("center");
						column.setLocked(false);//changxy
						String optid = optlist.get(j).get("optid");
						if("14".equals(typekind)||"15".equals(typekind)){//矩阵打分题、矩阵量表题
							if("14".equals(typekind))
								column.setColumnType("N");
							String key = "Q"+itemid+"_"+optid;
							column.setColumnId(key.toLowerCase());
							if(mtdatamap.get(itemid)!=null&&mtdatamap.get(itemid).get(optid)!=null){
								ArrayList<HashMap<String, String>> datas = mtdatamap.get(itemid).get(optid);
								for (int k = 0; k < datas.size(); k++) {
									LazyDynaBean data = new LazyDynaBean();
									if(datalist.size()>k)
										data = datalist.get(k);
									data.set("index", k+1);
									String value = datas.get(k).get("score");
									if("14".equals(typekind)){//矩阵打分题
										data.set(key.toLowerCase(), (int) Float.parseFloat(value==null?"0":value));
									} else {//矩阵量表题
										if(value.indexOf(".")!=-1){
											value = value.substring(0, value.indexOf("."));
										}
										String ss = scalemap.get(itemid).get(value);
										if(ss!=null&&!"".equals(value))
											data.set(key.toLowerCase(), ss+"("+value+")");
									}
									if(datalist.size()>k)
										datalist.set(k, data);
									else
										datalist.add(data);
								}
							}
						} else {
							String key = "Q"+itemid+"_"+optid;
							column.setColumnId(key.toLowerCase());
							column.setSortable(false);//add by xiegh
							if(!"4".equals(typekind)){
								column.setColumnType("N");
								column.setRendererFunc("analysisCheckRenderFn");
							}
							ArrayList<String> datas = null;
							if("1".equals(typekind)||"5".equals(typekind))//(图片)单选题
								datas = datamap.get("Q"+itemid+"_1");
							else
								datas = datamap.get(key);
							for (int l = 0; datas!=null && l < datas.size(); l++) {
								LazyDynaBean data = new LazyDynaBean();
								if(datalist.size()>l)
									data = datalist.get(l);
								data.set("index", l+1);
								data.set(("Q"+itemid+"_typekind").toLowerCase(), typekind);
								if("4".equals(typekind)){
									String code = getItemCodeSet(qnid, itemid, dao);
									if(code!=null&&!"".equals(code))
										data.set(key.toLowerCase(), AdminCode.getCodeName(code, datas.get(l)));
									else
										data.set(key.toLowerCase(), datas.get(l));
								} else {
									if("1".equals(typekind)||"5".equals(typekind)){
										if(optid.equals(datas.get(l)))
											data.set(key.toLowerCase(), "1");
										else
											data.set(key.toLowerCase(), "");
									} else {
										if("1".equals(datas.get(l)))
											data.set(key.toLowerCase(), "1");
										else
											data.set(key.toLowerCase(), "");
									}
								}
									
								if(datalist.size()>l)
									datalist.set(l, data);
								else
									datalist.add(data);
							}
						}
						ci.addChildColumn(column);
					}
					columnlist.add(ci);
				}
			}
			*/
			
			TableConfigBuilder table = new TableConfigBuilder("questionnaire_dataanalysis_00001", 
					columnlist, "analysis", userView, connection);
			table.setAutoRender(false);
			table.setDataList(datalist);
			//table.setLockable(false);
			Pageable pageable = new Pageable();
			pageable.setDataList(datalist);
			table.setPageable(pageable);
			table.setPageSize(20);
			table.setScheme(false);
        	table.setSetScheme(false);
        	table.setSchemeItemKey(null);
        	table.setShowPublicPlan(false);
			table.setAnalyse(false);
			//table.setSearchConfig("QN_111111111", "aababab", false);
			ArrayList<ButtonInfo> tools = new ArrayList<ButtonInfo>();
			ButtonInfo button = new ButtonInfo();
			button.setText(ResourceFactory.getProperty("button.export"));
			button.setFunctype("export");
			tools.add(button);
			table.setTableTools(tools);
		
			
			String configStr = table.createExtTableConfig();
			returnstr = configStr;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			if(flag)
				PubFunc.closeResource(connection);
		}
		return returnstr;
	}
	
	/**
	 * 获取图表分析数据
	 * @Title: getChartAnalysisData   
	 * @Description:    
	 * @param qnid
	 * @return 
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getChartAnalysisData(String qnid, String planid, String subObject){
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		Connection connection = null;
		RowSet rs = null;
		boolean flag = false;
		try {
			if(this.conn==null){
				connection = AdminDb.getConnection();
				flag = true;
			} else {
				connection = conn;
			}
			ContentDAO dao = new ContentDAO(connection);
			//问卷题目
			HashMap<String, HashMap<String, String>> itemmap = getQuestion(qnid, dao);
			//问卷选项
			HashMap<String, ArrayList<HashMap<String, String>>> optionmap = getOptions(qnid, dao);
			//矩阵选项
			HashMap<String, ArrayList<HashMap<String, String>>> matrixmap = getMatrixOption(qnid, dao);
			//(矩阵)量表题选项
			HashMap<String, HashMap<String, String>> scalemap = getScaleLevel(dao, qnid);
			//（矩阵）打分最大分数
			HashMap<String, Integer> scoremap = getMaxScope(qnid, dao);
			
			//保存题目顺序的list，为了前台显示的图表分析顺序和题目顺序相同
			ArrayList<String> orderlist = new ArrayList<String>();
			rs = dao.search("select itemid from qn_question_item item join qn_question_type type " +
					"on item.typeId = type.typeId where qnid = '"+qnid+"' " +
					"and  typeKind <> 9 and typeKind <> 10 and typeKind <> 11 order by norder");
			while(rs.next()){
				orderlist.add(rs.getString("itemid"));
			}
			//非矩阵题数据
			HashMap<String, ArrayList<String>> datamap = getChartData(qnid, planid, subObject, dao, itemmap, connection, optionmap);
			//处理数据
			HashMap<String, HashMap<String, Object>> dmap = new HashMap<String, HashMap<String, Object>>();
			for (String key : datamap.keySet()) {
				String itemid = key.substring(1, key.indexOf("_"));//题目号
				String typekind = itemmap.get(itemid).get("typekind");//题目类型
				ArrayList<String> data = datamap.get(key);
				int countsize = 0;//记录当前题目答题总数
				//多选题、图片多选题
				if("2".equals(typekind)||"6".equals(typekind)){
					HashMap<String, Object> map = dmap.get(itemid);
					if(map==null)
						map = new HashMap<String, Object>();
					String optionid = key.substring(key.indexOf("_")+1);//选项号
					int count = 0;//记录当前选项被选中次数
					for (int i = 0; i < data.size(); i++) {
						if("".equals(data.get(i))||"0".equals(data.get(i))){//如果当前选项没有选，需要判断此题另外的选项是否有选中，来区分此题是否已答。
							ArrayList<HashMap<String, String>> options = optionmap.get(itemid);//获取当前题目的所有选项
							boolean index = false;
							for (int j = 0; j < options.size(); j++) {
								String optid = options.get(j).get("optid");
								if(!optionid.equals(optid)){//循环当前题的选项，如果某个选项被选中，则认为当前题目已被答。
									if("1".equals(datamap.get("Q"+itemid+"_"+optid).get(i))){
										index = true;
										break;
									}
								}
							}
							if(index)
								countsize++;
						} else {
							countsize++;
							count++;
						}
					}
					map.put(optionid, count);//保存选项被选中次数。
					int size = -1;
					if(map.containsKey("countsize"))
						size = Integer.parseInt(map.get("countsize")+"");
					if(countsize>size)//添加countsize属性，用于保存此题被答次数。
						map.put("countsize", countsize);
					dmap.put(itemid, map);
				} else if("3".equals(typekind)||"4".equals(typekind)){//（多项）填空题
					HashMap<String, Object> map = dmap.get(itemid);
					if(map==null)
						map = new HashMap<String, Object>();
					String optionid = key.substring(key.indexOf("_")+1);//选项号
					for (int i = 0; i < data.size(); i++) {
						if(data.get(i)==null||"".equals(data.get(i))){//如果当前选项没有选，需要判断此题另外的选项是否有选中，来区分此题是否已答。
							ArrayList<HashMap<String, String>> options = optionmap.get(itemid);//获取当前题目的所有选项
							if(options==null)//如果没有options，说明是填空题，直接跳过。
								continue;
							boolean index = false;
							for (int j = 0; j < options.size(); j++) {
								String optid = options.get(j).get("optid");
								if(!optionid.equals(optid)){//循环当前题的选项，如果某个选项有值，则认为当前题目已被答。
									if(!"".equals(datamap.get("Q"+itemid+"_"+optid).get(i))){
										index = true;
										break;
									}
								}
							}
							if(index)
								countsize++;
						} else {
							countsize++;
						}
					}
					map.put(optionid, data);
					int size = -1;
					if(map.containsKey("countsize"))
						size = Integer.parseInt(map.get("countsize")+"");
					if(countsize>size)//添加countsize属性，用于保存此题被答次数。
						map.put("countsize", countsize);
					dmap.put(itemid, map);
				} else if("1".equals(typekind)||"5".equals(typekind)){//（图片）单选题
					HashMap<String, Object> map = new HashMap<String, Object>();
					for (int i = 0; i < optionmap.get(itemid).size(); i++) {
						HashMap<String, String> opt = optionmap.get(itemid).get(i);
						map.put(opt.get("optid"), 0);
					}
					for (int i = 0; i < data.size(); i++) {
						String str = data.get(i);
						if("".equals(str))
							continue;
						countsize++;
						if(map.containsKey(str))
							map.put(str, Integer.parseInt(map.get(str)+"")+1);
						else
							map.put(str, 1);
					}
					map.put("countsize", countsize);
					dmap.put(itemid, map);
				} else if("12".equals(typekind)){//打分题
					int min = 0;
					int max = 0;
					int count = 0;
					for (int i = 0; i < data.size(); i++) {
						String str = data.get(i);
						if(str==null||"".equals(str))
							continue;
						countsize++;
						int s = Integer.parseInt(str);
						if(i==0)
							min = s;
						if(s<min)
							min = s;
						if(s>max)
							max = s;
						count = count+s;
					}
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("min", min);
					map.put("max", max);
					if(countsize>0)
						map.put("avg", new BigDecimal(count).divide(new BigDecimal(countsize),2,BigDecimal.ROUND_HALF_UP));
					else
						map.put("avg", 0);
					map.put("countsize", countsize);
					dmap.put(itemid, map);
				} else if("13".equals(typekind)){//量表题
					HashMap<String, String> scale = scalemap.get(itemid);
					HashMap<String, Object> map = new HashMap<String, Object>();
					LinkedHashMap<String, Integer> m = new LinkedHashMap<String, Integer>();
					for (String str : scale.keySet()) {
						//非空验证 量表题没有"showscore"对应属性值 24902 wangb 2017-4-24 
						if(scale.get(str)!=null){ 
							m.put(scale.get(str)+"("+str+")", 0);
						}
					}
					for (int i = 0; i < data.size(); i++) {
						String str = data.get(i)==null?"":data.get(i);
						if("".equals(str))
							continue;
						String s = scale.get(str)+"("+str+")";
						if(m.containsKey(s))
							m.put(s, m.get(s)+1);
						else
							m.put(s, 1);
						countsize++;
					}
					map.put("data", m);
					map.put("countsize", countsize);
					dmap.put(itemid, map);
				}
			}
			
			//矩阵题目答案
			HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> mtdmap = getChartMatrixData(dao, qnid, planid, subObject, connection);
			
			HashMap<String, HashMap<String, HashMap<String, Object>>> mtdatamap = 
					new HashMap<String, HashMap<String,HashMap<String,Object>>>();
			//处理矩阵数据
			for (String itemid : itemmap.keySet()) {
				String type = itemmap.get(itemid).get("typekind");
				if(!"7".equals(type)&&!"8".equals(type)&&!"14".equals(type)&&!"15".equals(type))
					continue;
				HashMap<String, ArrayList<HashMap<String, String>>> itemap = mtdmap.get(itemid);
				HashMap<String, HashMap<String, Object>> map1 = new HashMap<String, HashMap<String, Object>>();
				ArrayList<HashMap<String, String>> optionlist = optionmap.get(itemid);
				for (HashMap<String, String> map : optionlist) {
					HashMap<String, Object> map2 = new HashMap<String, Object>();
					if(itemap!=null&&itemap.get(map.get("optid"))!=null){
						ArrayList<HashMap<String, String>> datalist = itemap.get(map.get("optid"));
						for (HashMap<String, String> optmap : datalist) {
							for (String key : optmap.keySet()) {
								if("score".equals(key)){
									ArrayList<String> list1 = new ArrayList<String>();
									if(map2.containsKey("score"))
										list1 = (ArrayList<String>) map2.get("score");
									list1.add(optmap.get(key));
									map2.put("score", list1);
								} else if("1".equals(optmap.get(key))){
									if(map2.containsKey(key))
										map2.put(key, Integer.parseInt(map2.get(key)+"")+1);
									else
										map2.put(key, 1);
								}
							}
						}
						map2.put("size", datalist.size());
					} else {
						map2.put("score", new ArrayList<String>());
						map2.put("size", 0);
					}
					map1.put(map.get("optid"), map2);
				}
				mtdatamap.put(itemid, map1);
			}
			
			for (int i = 0; i < orderlist.size(); i++) {
				list.add(new HashMap<String, Object>());
			}
			
			//将矩阵题答案转为页面显示数据
			for (String itemid : mtdatamap.keySet()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				String typekind = itemmap.get(itemid).get("typekind");
				map.put("name", itemmap.get(itemid).get("name"));
				map.put("typekind", typekind);
				//统计图数据
				ArrayList<HashMap<String, Object>> chartdata = new ArrayList<HashMap<String,Object>>();
				//统计图需要的字段
				ArrayList<HashMap<String, String>> chartfield = new ArrayList<HashMap<String, String>>();
				//统计图数据所有字段
				ArrayList<String> allfield = new ArrayList<String>();
				allfield.add("matrixname");
				//表格需要的字段
				ArrayList<HashMap<String, String>> tablefield = new ArrayList<HashMap<String, String>>();
				//表格数据
				ArrayList<HashMap<String, Object>> tabledata = new ArrayList<HashMap<String,Object>>();
				//矩阵题横向选项
				ArrayList<HashMap<String, String>> matrixlist = matrixmap.get(itemid);
				//矩阵题纵向选项
				ArrayList<HashMap<String, String>> mtlist = optionmap.get(itemid);
				//本题数据
				HashMap<String, HashMap<String, Object>> item = mtdatamap.get(itemid);
				
				HashMap<String, String> m = new HashMap<String, String>();
				int countsize = 0;
				int maximum = 10;
				if("7".equals(typekind)||"8".equals(typekind)){//矩阵单（多）选题
					//临时map，将数据存入此map后，再排序，使chart按照顺序显示统计图
					HashMap<String, HashMap<String, Object>> tempmap = new HashMap<String, HashMap<String, Object>>();
					
					m = new HashMap<String, String>();
					m.put("text", "&nbsp;");
					m.put("value", "dataname");
					tablefield.add(m);
					
					for (int j = 0; j < mtlist.size(); j++) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						HashMap<String, String> mtoptmap = mtlist.get(j);
						String optid = mtoptmap.get("optid");
						data.put("dataname", mtoptmap.get("optname"));
						HashMap<String, Object> matrixdatamap = item.get(optid);
						
						m = new HashMap<String, String>();
						m.put("text", mtoptmap.get("optname"));
						m.put("value", "Q"+itemid+"_"+optid);
						chartfield.add(m);
						allfield.add("Q"+itemid+"_"+optid);
						for (int k = 0; k < matrixlist.size(); k++) {
							String id = matrixlist.get(k).get("optid");
							HashMap<String, Object> chartmap = new HashMap<String, Object>();
							if(tempmap.containsKey(id))
								chartmap = tempmap.get(id);
							
							if(j==0){
								m = new HashMap<String, String>();
								m.put("text", matrixlist.get(k).get("optname"));
								m.put("value", "Q"+itemid+"_"+(k+1));
								tablefield.add(m);
								countsize = (Integer) matrixdatamap.get("size");
								chartmap.put("matrixname", matrixlist.get(k).get("optname"));
							}
							
							if(matrixdatamap.containsKey("C"+(k+1))){
								int num = (Integer) matrixdatamap.get("C"+(k+1));
								if(num>maximum)
									maximum = num;
								data.put("Q"+itemid+"_"+(k+1), num);
								chartmap.put("Q"+itemid+"_"+optid, num);
							} else {
								data.put("Q"+itemid+"_"+(k+1), 0);
								chartmap.put("Q"+itemid+"_"+optid, 0);
							}
							tempmap.put(id, chartmap);
						}
						tabledata.add(data);
					}
					
					for (int j = 0; j < matrixlist.size(); j++) {
						chartdata.add(tempmap.get(matrixlist.get(j).get("optid")));
					}
					
					map.put("tabledata", tabledata);
					if(maximum%10>0)
						maximum = (maximum/10+1)*10;
					map.put("maximum", maximum);
				} else if("14".equals(typekind)){//矩阵打分题
					ArrayList<HashMap<String, String>> optlist = optionmap.get(itemid);
					
					m = new HashMap<String, String>();
					m.put("text", "&nbsp;");
					m.put("value", "matrixname");
					tablefield.add(m);
					for (int i = 0; i < optlist.size(); i++) {
						HashMap<String, String> optmap = optlist.get(i);
						String optid = optmap.get("optid");
						HashMap<String, Object> matrix = item.get(optid);
						ArrayList<String> score = (ArrayList<String>) matrix.get("score");
						BigDecimal max = new BigDecimal(0);
						BigDecimal count = new BigDecimal(0);
						BigDecimal min = new BigDecimal(0);
						for (int k = 0; k < score.size(); k++) {
							BigDecimal index = new BigDecimal(score.get(k));
							if(k==0){
								max = index;
								count = index;
								min = index;
							} else {
								if(max.compareTo(index)==-1)
									max = index;
								if(min.compareTo(index)==1)
									min = index;
								count = count.add(index);
							}
						}
						countsize = Integer.parseInt(matrix.get("size")+"");
						HashMap<String, Object> dm = new HashMap<String, Object>();
						dm.put("min", min.setScale(2,BigDecimal.ROUND_HALF_UP));
						if(countsize>0)
							dm.put("avg", 
									count.divide(new BigDecimal(countsize), 2, BigDecimal.ROUND_HALF_UP));
						else
							dm.put("avg", 0);
						dm.put("max", max.setScale(2,BigDecimal.ROUND_HALF_UP));
						dm.put("matrixname", optmap.get("optname"));
						chartdata.add(dm);
					}
					m = new HashMap<String, String>();
					m.put("text", ResourceFactory.getProperty("kq.formula.max"));
					m.put("value", "max");
					chartfield.add(m);
					tablefield.add(m);
					m = new HashMap<String, String>();
					m.put("text", ResourceFactory.getProperty("kq.formula.min"));
					m.put("value", "min");
					chartfield.add(m);
					tablefield.add(m);
					m = new HashMap<String, String>();
					m.put("text", ResourceFactory.getProperty("kq.formula.average"));
					m.put("value", "avg");
					chartfield.add(m);
					tablefield.add(m);
					allfield.add("max");
					allfield.add("min");
					allfield.add("avg");
					allfield.add("matrixname");
					
					map.put("tabledata", chartdata);
					map.put("maximum", scoremap.get(itemid));
				} else {//矩阵量表题
					HashMap<String, String> scale = scalemap.get(itemid);
					ArrayList<HashMap<String, String>> optlist = optionmap.get(itemid);
					m = new HashMap<String, String>();
					m.put("text", "&nbsp;");
					m.put("value", "dataname");
					tablefield.add(m);
					
					HashMap<String, HashMap<String, Object>> tabmap = new HashMap<String, HashMap<String, Object>>();
					int i = 0;
					String showscore=scale.get("showscore");//changxy  20160921 取出showscore 判断是否显示分值
					scale.remove("showscore");//移除不需要的参数
					for (String key : scale.keySet()) {
						i++;
						String matrixname="";
						if("true".equalsIgnoreCase(showscore))//设置为true时不显示分值则选项描述后不加内容
							matrixname = scale.get(key);
						else
							matrixname = scale.get(key)+"("+key+")";
						String tableid = "Q"+itemid+"_"+i;
						m = new HashMap<String, String>();
						m.put("text", matrixname);
						m.put("value", tableid);
						tablefield.add(m);
						BigDecimal b = new BigDecimal(key).setScale(2, BigDecimal.ROUND_HALF_UP);
						HashMap<String, Object> chartmap = new HashMap<String, Object>();
						chartmap.put("matrixname", matrixname);
						for (HashMap<String, String> optmap : optlist) {
							String optid = optmap.get("optid");
							String optname = optmap.get("optname");
							if(i==1){
								m = new HashMap<String, String>();
								m.put("text", optname);
								m.put("value", "Q"+itemid+"_"+optid);
								chartfield.add(m);
								allfield.add("Q"+itemid+"_"+optid);
							}
							ArrayList<String> scorelist = (ArrayList<String>) item.get(optid).get("score");
							int num = 0;
							for (String score : scorelist) {
								BigDecimal d = new BigDecimal(score).setScale(2, BigDecimal.ROUND_HALF_UP);
								if(b.compareTo(d)==0)
									num++;
							}
							if(scorelist.size()>countsize)
								countsize = scorelist.size();
							chartmap.put("Q"+itemid+"_"+optid, num);
							HashMap<String, Object> dm = new HashMap<String, Object>();
							if(tabmap.containsKey(optid))
								dm = tabmap.get(optid);
							else
								dm.put("dataname", optname);
							dm.put(tableid, num);
							tabmap.put(optid, dm);
						}
						chartdata.add(chartmap);
					}
					for (HashMap<String, String> hm : optlist) {
						tabledata.add(tabmap.get(hm.get("optid")));
					}
					allfield.add("matrixname");
					
					map.put("tabledata", tabledata);
					if(maximum%10>0)
						maximum = (maximum/10+1)*10;
					map.put("maximum", maximum);
				}
				map.put("itemid", itemid);
				map.put("countsize", countsize);
				map.put("allfield", allfield);
				map.put("chartfield", chartfield);
				map.put("chartdata", chartdata);
				map.put("tablefield", tablefield);
				int index = orderlist.indexOf(itemid);
				list.set(index,map);
			}
			//将非矩阵题数据转为页面需要的数据
			for (String itemid : dmap.keySet()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				HashMap<String, String> item = itemmap.get(itemid);
				map.put("name", item.get("name"));
				String typekind = item.get("typekind");
				map.put("typekind", typekind);
				ArrayList<HashMap<String, Object>> datalist = new ArrayList<HashMap<String,Object>>();
				HashMap<String, Object> imap = dmap.get(itemid);
				int maximum = 10;
				for (String optid : imap.keySet()) {
					HashMap<String, Object> dm = new HashMap<String, Object>();
					if(!"countsize".equals(optid)){
						if("12".equals(typekind)){//打分题
							if("min".equals(optid))
								dm.put("dataname", ResourceFactory.getProperty("lable.examine.lowestscore"));
							else if("max".equals(optid))
								dm.put("dataname", ResourceFactory.getProperty("lable.examine.hightestScore"));
							else
								dm.put("dataname", ResourceFactory.getProperty("lable.examine.avgScore"));
							dm.put("datavalue", imap.get(optid));
							datalist.add(dm);
							if(scoremap.get(itemid)>maximum)
								maximum = scoremap.get(itemid);
						} else if("3".equals(typekind)){//填空题
							ArrayList<String> li = new ArrayList<String>();
							li.add("Q"+itemid+"_1");
							map.put("column", li);
						} else if("4".equals(typekind)){//多项填空题
							if(datalist.size()<=0){
								ArrayList<HashMap<String, String>> optlist = optionmap.get(itemid);
								ArrayList<HashMap<String, String>> col = new ArrayList<HashMap<String, String>>();
								for (int j = 0; j < optlist.size(); j++) {
									HashMap<String, String> m = new HashMap<String, String>();
									m.put("text", optlist.get(j).get("optname"));
									m.put("value", "Q"+itemid+"_"+optlist.get(j).get("optid"));
									col.add(m);
								}
								map.put("column", col);
							}
						} else if("13".equals(typekind)){//量表题
							if("data".equals(optid)){
								HashMap<String, Integer> hm = (HashMap<String, Integer>) imap.get(optid);
								for (String key : hm.keySet()) {
									int num = hm.get(key);
									if(num>maximum)
										maximum = num;
									HashMap<String, Object> m = new HashMap<String, Object>();
									m.put("dataname", key);
									m.put("datavalue", num);
									datalist.add(m);
								}
							}
						} else {//（图片）单（多）选题
							ArrayList<HashMap<String, String>> optlist = optionmap.get(itemid);
							if(datalist.size()<=0){
								for (int i = 0; i < optlist.size(); i++) {
									datalist.add(new HashMap<String, Object>());
								}
							}
							for (int j = 0; j < optlist.size(); j++) {
								HashMap<String, String> optmap = optlist.get(j);
								if(optid.equals(optmap.get("optid"))){
									int num = (Integer) imap.get(optid);
									if(num>maximum)
										maximum = num;
									if("5".equals(typekind)||"6".equals(typekind)){//图片单（多）选题
										//xus 20/4/22 【59496】VFS+UTF-8：问卷调查/列表界面展现，点击"操作"列的"分析"（含有图片单选或多选题目的问卷），选择"图表分析"，题目选项中的图片为“×”，不对，详见附件
//										dm.put("imgurl", "<img width=\"30px\" height=\"30px\" " +
//												"src=\"/servlet/DisplayOleContent?bencrypt=true&filePath="+SafeCode.encode(PubFunc.encrypt(optmap.get("imgurl")))+"\"></img>");
										dm.put("imgurl", "<img width=\"30px\" height=\"30px\" " +
												"src=\"/servlet/vfsservlet?fileid="+optmap.get("imgurl")+"\"></img>");
									}
									dm.put("dataname", optmap.get("optname"));
									dm.put("datavalue", num);
									datalist.set(j, dm);
									break;
								}
							}
						}
					} else {
						map.put("countsize", imap.get(optid));
					}
				}
				map.put("itemid", itemid);
				map.put("data", datalist);
				if(maximum%10>0)
					maximum = (maximum/10+1)*10;
				map.put("maximum", maximum);
				int index = orderlist.indexOf(itemid);
				list.set(index,map);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			if(flag)
				PubFunc.closeResource(connection);
		}
		return JSON.toString(list);
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
			sql.append("select itemid,name,typekind,score,options from qn_question_item item join qn_question_type type ");
			sql.append("on item.typeId = type.typeId where qnId = '"+qnid+"' and typekind <> '9' ");
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
	
	
	private HashMap getTableMatrixData(ContentDAO dao, String qnid, String planid, String subObject, Connection conn) {
		HashMap dataMap = new HashMap();
		
		ResultSet rs = null;
		try {
			DbWizard w = new DbWizard(conn);
			
			if(!w.isExistTable("qn_matrix_"+qnid+"_data",false))
				return dataMap;
			
			StringBuffer sql = new StringBuffer();
			sql.append("select mainObject,itemid,optid,");
			for (int i = 1; i <= 10; i++) {
				sql.append("C"+i+",");
			}
			sql.append(" score from qn_matrix_"+qnid+"_data where planid = '"+planid+"' ");
			if(subObject!=null&&!"".equals(subObject)){
				sql.append("and subObject = '"+subObject+"' ");
			}
			sql.append(" and status in( '2','0' ) ");
			sql.append(" order by mainObject,itemid ");
			
			rs = dao.search(sql.toString());
			
			while(rs.next()) {
				
				String mainObject = rs.getString("mainObject");
				HashMap valueMap;
				if(dataMap.containsKey(mainObject)) {
					valueMap = (HashMap)dataMap.get(mainObject);
				}else {
					valueMap = new HashMap();
					dataMap.put(mainObject, valueMap);
				}
				
				HashMap record = new HashMap();
				
				record.put("score", rs.getString("score"));
				for (int i = 1; i <= 10; i++) {
					String value = rs.getString("C"+i);
					value = value==null?"":value;
					record.put("C"+i,value);
				}
				String key = rs.getString("itemid")+"_"+rs.getString("optid");
				valueMap.put(key,record);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			
		}
		
		return dataMap;
	}
	
	
	
	/**
	 * 获取矩阵表中的数据
	 * @Title: getChartMatrixData   
	 * @Description:    
	 * @param dao
	 * @param qnid 问卷id
	 * @param planid 计划id
	 * @param subObject 调查对象
	 * @param conn
	 * @return 
	 * @return HashMap<String,HashMap<String,ArrayList<HashMap<String,String>>>>
	 */
	private HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> 
	getChartMatrixData(ContentDAO dao, String qnid, String planid, String subObject, Connection conn){
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
			sql.append("score from qn_matrix_"+qnid+"_data where planid = '"+planid+"' ");
			if(subObject!=null&&!"".equals(subObject)){
				sql.append("and subObject = '"+subObject+"' ");
			}
			sql.append("and status in( '2','0' ) ");
			sql.append("order by itemid,mainObject");
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
	
	/**
	 * 获取（矩阵）量表题选项
	 * @Title: getScaleLevel   
	 * @Description:    
	 * @param dao
	 * @param qnid
	 * @return 
	 * @return HashMap<String,HashMap<String,String>>
	 */
	private HashMap<String, HashMap<String, String>> getScaleLevel(ContentDAO dao, String qnid){
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String,String>>();
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("select itemid,options from qn_question_item item join qn_question_type type ");
			sb.append("on item.typeId = type.typeId where (typeKind = '13' or typeKind = '15') ");
			sb.append("and qnId = '"+qnid+"' ");
			rs = dao.search(sb.toString());
			while(rs.next()){
				String xml = rs.getString("options");
				LinkedHashMap<String, String> opt = parseXml(xml);
				map.put(rs.getString("itemid"), opt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return map;
	}
	
	/**
	 * 解析量表题参数
	 * @Title: parseXml   
	 * @Description:    
	 * @param xml
	 * @return 
	 * @return HashMap<String,String> key为score，value为text
	 */
	private LinkedHashMap<String, String> parseXml(String xml){
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		try {
			Document doc = PubFunc.generateDom(xml);
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> list = root.getChildren();
			for (int i = 0; i < list.size(); i++) {
				Element ele = list.get(i);
				String name = ele.getName();
				if("levels".equals(name)){
					@SuppressWarnings("unchecked")
					List<Element> arr = ele.getChildren();
					for (int j = 0; j < arr.size(); j++) {
						Element e = arr.get(j);
						map.put(e.getAttributeValue("score"), e.getAttributeValue("text"));
					}
					break;
				}
			}
			String showscore=root.getChildText("showscore");//矩阵量标题设置是否显示值 changxy 20160921
			map.put("showscore", showscore);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 获取非矩阵题数据
	 * @Title: getData   
	 * @Description:    
	 * @param qnid 问卷id
	 * @param planid 计划id
	 * @param subObject 被调查对象
	 * @param dao
	 * @param itemmap 题目
	 * @param conn
	 * @param optionmap 题目选项
	 * @return 
	 * @return HashMap<String,ArrayList<String>>
	 */
	private HashMap getTableData(String qnid, String planid, String subObject, ContentDAO dao, 
			HashMap<String, HashMap<String, String>> itemmap, Connection conn,
			HashMap<String, ArrayList<HashMap<String, String>>> optionmap){
		HashMap datamap = new HashMap<String,Object>();
		ResultSet rs = null;
		try {
			DbWizard w = new DbWizard(conn);
			if(!w.isExistTable("qn_"+qnid+"_data",false))
				return datamap;
			StringBuffer sql = new StringBuffer();
			ArrayList fieldList = new ArrayList();
			String fieldName;
			sql.append("select ");
			for (String itemid : itemmap.keySet()) {
				HashMap<String, String> map = itemmap.get(itemid);
				String kind = map.get("typekind");
				
				if("1".equals(kind)||"3".equals(kind)||"5".equals(kind)
						||"12".equals(kind)||"13".equals(kind)){
					fieldName = "Q"+map.get("itemid")+"_1";
					sql.append(fieldName).append(",");
					fieldList.add(fieldName);
				} else if("2".equals(kind)||"4".equals(kind)||"6".equals(kind)){
					ArrayList<HashMap<String, String>> arr = optionmap.get(map.get("itemid"));
					if(arr==null)
						continue;
					for (int j = 0; j < arr.size(); j++) {
						fieldName = "Q"+map.get("itemid")+"_"+arr.get(j).get("optid");
						sql.append(fieldName).append(",");
						fieldList.add(fieldName);
					}
				}
			}
			
			//如果没有值，说明没有矩阵题，跳出
			if(fieldList.size()<1)
				return datamap;
			
			//sql.delete(sql.toString().length()-1, sql.toString().length());
			sql.append(" mainObject ");
			sql.append(" from qn_"+qnid+"_data where planid='").append(planid).append("' ");
			if(subObject!=null&&!"".equals(subObject)){
				sql.append(" and subObject = '"+subObject+"'");
			}
			sql.append(" and status in( '2','0' ) ");
			sql.append(" order by dataid");
			rs = dao.search(sql.toString());
			while(rs.next()){
				HashMap record = new HashMap();
				String mainObject = rs.getString("mainObject");
				for (int i=0;i<fieldList.size();i++) {
					fieldName = (String)fieldList.get(i);
					String value = rs.getString(fieldName);
					value = value==null?"":value;
					record.put(fieldName, value);
				}
				datamap.put(mainObject, record);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return datamap;
	}
	
	
	
	/**
	 * 获取非矩阵题数据
	 * @Title: getData   
	 * @Description:    
	 * @param qnid 问卷id
	 * @param planid 计划id
	 * @param subObject 被调查对象
	 * @param dao
	 * @param itemmap 题目
	 * @param conn
	 * @param optionmap 题目选项
	 * @return 
	 * @return HashMap<String,ArrayList<String>>
	 */
	private HashMap<String, ArrayList<String>> getChartData(String qnid, String planid, String subObject, ContentDAO dao, 
			HashMap<String, HashMap<String, String>> itemmap, Connection conn,
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
				if("1".equals(kind)||"3".equals(kind)||"5".equals(kind)
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
			
			//如果没有值，说明没有矩阵题，跳出
			if(datamap.size()<1)
				return datamap;
			
			sql.delete(sql.toString().length()-1, sql.toString().length());
			sql.append(" from qn_"+qnid+"_data");
			if(subObject!=null&&!"".equals(subObject)){
				sql.append(" where subObject = '"+subObject+"'");
				sql.append(" and status in( '2','0' ) ");
			}else{
				sql.append(" where status in( '2','0' ) ");
			}
			
			sql.append(" order by dataid");
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
	 * 查询（矩阵）打分最大分数
	 * @Title: getMaxScope   
	 * @Description:    
	 * @param qnid
	 * @param conn
	 * @return 
	 * @return HashMap<String,Integer>
	 */
	private HashMap<String, Integer> getMaxScope(String qnid, ContentDAO dao){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		ResultSet rs = null;
		try {
			String sql = "select itemid,options from qn_question_item item left join " +
					"qn_question_type type on item.typeId = type.typeId where (type.typeKind = 12" +
					" or type.typeKind = 14) and qnid = "+qnid;
			rs = dao.search(sql);
			while(rs.next()){
				String itemid = rs.getString("itemid");
				String xml = rs.getString("options");
				int max = 0;
				Document doc = PubFunc.generateDom(xml);
				Element root = doc.getRootElement();
				@SuppressWarnings("unchecked")
				List<Element> list = root.getChildren();
				for (Element ele : list) {
					if("maxscore".equals(ele.getName()))
						max = Integer.parseInt(ele.getValue());
				}
				map.put(itemid, max);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	
	public String getItemCodeSet(String qnid, String itemid, ContentDAO dao){
		String codeset = "";
		ResultSet rs = null;
		try {
			rs = dao.search("select options from qn_question_item where qnid = '"+qnid+"' and itemid = '"+itemid+"'");
			String options = "";
			while(rs.next()){
				options = rs.getString("options");
			}
			Document doc = PubFunc.generateDom(options);
			Element root = doc.getRootElement();
			Element e = root.getChild("inputtype");
			String type = e.getValue();
			if("4".equals(type)){
				Element ele = root.getChild("codeset");
				codeset = ele.getValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return codeset;
	}
	
}
