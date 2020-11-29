package com.hjsj.hrms.module.template.historydata.transaction;

import com.hjsj.hrms.module.template.historydata.businessobject.HistoryDataBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateSet;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.*;

/**
 * 
* @Title: GetHistoryDataTrans
* @Description:历史数据
* @author: hej
* @date 2019年10月29日 下午3:36:42
* @version
 */
public class GetHistoryDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String tabid = (String)this.formHM.get("tabid");
		String queryType = (String)this.formHM.get("queryType");
		String timeslot = (String)this.formHM.get("timeslot");
		String querytype = (String) this.getFormHM().get("type");
		String transType = (String)this.formHM.get("transType");
		String module_id = (String)this.formHM.get("module_id");
		try {
			if(StringUtils.isNotBlank(querytype)){//页面模糊查询
	            StringBuffer condsql = new StringBuffer("");
	            TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("templatehistorydata");
	            if("1".equals(querytype)){//查询栏查询
	            	 List values = (ArrayList) this.getFormHM().get("inputValues");
	       			 for(int i=0;i<values.size();i++){
	   					String value = SafeCode.decode(values.get(i).toString());
	   					if (i == 0) {
	   						condsql.append(" and (");
	   					}else {
	   						condsql.append(" or ");
	   					}
						condsql.append("( name like '%"+value+"%' or ");
	   					condsql.append(" only_value like '%"+value+"%') ");
	   					if(i == values.size()-1){
	   						condsql.append(" ) ");
	   					}
	   				}
	       			tableCache.setQuerySql(condsql.toString());
	            }else if("2".equals(querytype)) {//方案查询
	            	MorphDynaBean md=(MorphDynaBean)this.getFormHM().get("customParams");
	    			ArrayList fieldsMap=(ArrayList)md.get("fieldsMap");
	    			HashMap map=new HashMap();
	    			for(int i=0;i<fieldsMap.size();i++){
	    				MorphDynaBean fieldMd=(MorphDynaBean)fieldsMap.get(i);
	    				String itemid=fieldMd.get("itemid")+"";
	    				String itemdesc=fieldMd.get("itemdesc")+"";
	    				String itemtype=fieldMd.get("itemtype")+"";
	    				String codesetid=fieldMd.get("codesetid")+"";
	    				String useflag=fieldMd.get("useflag")+"";
	    				FieldItem item = new FieldItem();
	    				item.setCodesetid(codesetid);
	    				item.setUseflag(useflag);
	    				item.setItemtype(itemtype);
	    				item.setItemid(itemid);
	    				item.setItemdesc(itemdesc);
	    				map.put(itemid,item);
	    			}
	    			 String exp = (String) this.getFormHM().get("exp");
	    			 exp = SafeCode.decode(exp);
	    	         String cond = (String) this.getFormHM().get("cond");
	    	         cond = SafeCode.decode(cond);
	    	         FactorList factor_bo=new FactorList(PubFunc.keyWord_reback(exp),PubFunc.keyWord_reback(cond.toUpperCase()),userView.getUserId(),map);
					String factorsql = factor_bo.getSingleTableSqlExpression("myGridData");
		   	        if(StringUtils.isNotBlank(factorsql)) {
		   	        	 factorsql = factorsql.replace("myGridData.", "");
		   	        	 condsql.append(" and ");
		   	        	 condsql.append(factorsql);
		   	        }
		   	        tableCache.setQuerySql(condsql.toString());
	            }
			}else {
				if("0".equals(transType)) {
					this.getHistoryData(tabid,queryType,timeslot,module_id);
				}
				else if("1".equals(transType)) {
					HistoryDataBo historyDataBo = new HistoryDataBo(this.frameconn,this.userView);
					ArrayList selectRecords=(ArrayList) this.getFormHM().get("selectRecords");
					String flag = (String)this.formHM.get("flag");
					TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get("templatehistorydata");
					if("0".equals(flag)) {
						selectRecords = historyDataBo.getAllRecordList(tableCache);
					}
					this.exportExcel(selectRecords,tableCache,flag);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 导出excel
	 * @param selectRecords
	 * @param flag 
	 * @param flag 
	 * @param historyDataBo 
	 * @throws GeneralException 
	 */
	private void exportExcel(ArrayList selectRecords, TableDataConfigCache tableCache, String flag) throws GeneralException {
		try {
			TemplateUtilBo utilBo = new TemplateUtilBo(frameconn, userView);
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn());
		    ArrayList<ColumnsInfo> columnList = tableCache.getTableColumns();
		    ArrayList headList = new ArrayList();
		    ArrayList dataList = new ArrayList();
		    //数据列
		    //导出Excel
		    String fileName = this.userView.getUserName()+"_"+ResourceFactory.getProperty("template.processArchiving.archivehistorydata")+".xls";
		    HashSet tabidSet = new HashSet();
		    HashMap filenameMap = new HashMap();
		    HashMap tablenameMap = new HashMap();
			ArrayList sheetNameList = new ArrayList();
			if(selectRecords.size()>0) {
			    for(int i=0;i<selectRecords.size();i++) {
			    	HashMap rec = new HashMap();
					if("0".equals(flag)) {
						rec = (HashMap) selectRecords.get(i);
					}else {
						MorphDynaBean mdb =(MorphDynaBean)selectRecords.get(i);
						rec = PubFunc.DynaBean2Map(mdb);
					}
					String tabid = (String) rec.get("tabid");
					String tablename = (String) rec.get("tablename");
					String record_id = (String) rec.get("record_id");
					String archive_id = (String) rec.get("archive_id");
					String archive_year = (String) rec.get("year"); 
					ArrayList cellList = utilBo.getArchiveCell(Integer.parseInt(tabid),archive_id,-1);
					TemplateDataBo dataBo = new TemplateDataBo(this.frameconn,this.userView,Integer.parseInt(tabid),archive_id);
					if(!tabidSet.contains(tabid)) {
						HashMap headmap = new HashMap();
						ArrayList headlist = new ArrayList();
						for(int k=0;k<columnList.size();k++) {
					    	ColumnsInfo columnsInfo = columnList.get(k);
					    	LazyDynaBean abean = new LazyDynaBean();
					    	if("print".equals(columnsInfo.getColumnId())||"year".equals(columnsInfo.getColumnId())||"task_id".equals(columnsInfo.getColumnId())||
					    			"tabid".equals(columnsInfo.getColumnId())||"record_id".equals(columnsInfo.getColumnId())||"archive_id".equals(columnsInfo.getColumnId())
					    			||"ins_id".equals(columnsInfo.getColumnId())) {
					    		continue;
					    	}
					    	abean.set("itemid", columnsInfo.getColumnId());
					        abean.set("content", columnsInfo.getColumnDesc());
					        abean.set("colType", columnsInfo.getColumnType());
					        abean.set("dateFormat", "yyyy-MM-dd HH:mm:ss");
					        abean.set("columnWidth",4000);
					        if("D".equalsIgnoreCase(columnsInfo.getColumnType())) {
					        	abean.set("columnWidth",6000);
					        }
					        abean.set("codesetid", columnsInfo.getCodesetId());
					        headlist.add(abean);
						}
						for(int k=0;k<cellList.size();k++) {
							LazyDynaBean abean = new LazyDynaBean();
							TemplateSet setbo = (TemplateSet) cellList.get(k);
							if(setbo.getFlag()==null|| "".equalsIgnoreCase(setbo.getFlag()))
								setbo.setFlag("H");
			        		if("H".equals(setbo.getFlag())) {
			        			continue;
			        		}
			        		String fldname  = setbo.getTableFieldName();
							if(StringUtils.isNotBlank(fldname))
								fldname =fldname.toLowerCase();
							if(setbo.isABKItem()) {
								if(setbo.isSubflag()) {
									continue;
								}
								abean.set("itemid", fldname);
						        abean.set("content", setbo.getField_hz());
						        abean.set("colType", setbo.getField_type());
						        abean.set("columnWidth",4000);
						        if("D".equalsIgnoreCase(setbo.getField_type())) {
						        	abean.set("columnWidth",6000);
						        }
						        abean.set("dateFormat", "yyyy-MM-dd HH:mm:ss");
						        abean.set("codesetid", setbo.getCodeid());
						        headlist.add(abean);
							}
						}
						if(sheetNameList.contains(tablename)){
							int num = 0;
							if(filenameMap.containsKey(tablename)){
								num = Integer.parseInt((String)filenameMap.get(tablename));
								filenameMap.put(tablename,num+1);
							}else{
								num = 1;
								filenameMap.put(tablename,1);
							}
							tablename = tablename+num;
						}
	    				sheetNameList.add(tablename);
	    				tablenameMap.put(tabid,tablename);
						headmap.put(tablename, headlist);
						headList.add(headmap);
						tabidSet.add(tabid);
						//拼装数据list
						HashMap datamap = new HashMap();
						ArrayList datalist = new ArrayList();
						LazyDynaBean abean = new LazyDynaBean();
						LazyDynaBean dataBean = new LazyDynaBean();
						dataBean.set("content", (String)rec.get("name"));
						abean.set("name", dataBean);
						dataBean = new LazyDynaBean();
						dataBean.set("content", (String)rec.get("tablename"));
						abean.set("tablename", dataBean);
						dataBean = new LazyDynaBean();
						dataBean.set("content", (String)rec.get("nbase"));
						abean.set("nbase", dataBean);
						dataBean = new LazyDynaBean();
						dataBean.set("content", (String)rec.get("only_value"));
						abean.set("only_value", dataBean);
						dataBean = new LazyDynaBean();
						dataBean.set("content", (String)rec.get("end_date"));
						abean.set("end_date", dataBean);
						dataBean = new LazyDynaBean();
						dataBean.set("content", (String)rec.get("start_date"));
						abean.set("start_date", dataBean);
						dataBean = new LazyDynaBean();
						String b0110 = (String)rec.get("b0110");
						if(StringUtils.isNotEmpty(b0110)&&b0110.indexOf("`")!=-1) {
							b0110 = b0110.split("`")[1];
						}
						dataBean.set("content", b0110);
						abean.set("b0110", dataBean);
						dataBean = new LazyDynaBean();
						String e0122 = (String)rec.get("e0122");
						if(StringUtils.isNotEmpty(e0122)&&e0122.indexOf("`")!=-1) {
							e0122 = e0122.split("`")[1];
						}
						dataBean.set("content", e0122);
						abean.set("e0122", dataBean);
						/*dataBean = new LazyDynaBean();
						dataBean.set("content", (String)rec.get("ins_id"));
						abean.set("ins_id", dataBean);*/
						HashMap datamap_ = dataBo.analysisJson2Map(record_id, archive_year);
						for(int k=0;k<cellList.size();k++) {
							TemplateSet setbo = (TemplateSet) cellList.get(k);
			        		if("H".equals(setbo.getFlag())) {
			        			continue;
			        		}
			        		String fldname  = setbo.getTableFieldName();
							if(StringUtils.isNotBlank(fldname))
								fldname =fldname.toLowerCase();
							if(setbo.getFlag()==null|| "".equalsIgnoreCase(setbo.getFlag()))
								setbo.setFlag("H");
							if(setbo.isABKItem()) {
								if(setbo.isSubflag()) {
									continue;
								}
								String codesetid = setbo.getCodeid();
								String itemtype = setbo.getField_type();
								LazyDynaBean dataBean_ = new LazyDynaBean();
								String content = String.valueOf(datamap_.get(fldname)==null?"":datamap_.get(fldname));
								if("A".equalsIgnoreCase(itemtype)&&StringUtils.isNotBlank(codesetid)&&!"0".equals(codesetid)) {
									content=AdminCode.getCodeName(codesetid,content);
								}
								dataBean_.set("content", content);
								abean.set(fldname, dataBean_);
							}
						}
						datalist.add(abean);
						datamap.put(tablename, datalist);
						dataList.add(datamap);
					}else {
						for(int j=0;j<dataList.size();j++) {
							HashMap datamap = (HashMap) dataList.get(j);
							tablename = (String) tablenameMap.get(tabid);
							if(datamap.containsKey(tablename)) {
								ArrayList datalist = (ArrayList) datamap.get(tablename);
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean dataBean = new LazyDynaBean();
								dataBean.set("content", (String)rec.get("name"));
								abean.set("name", dataBean);
								dataBean = new LazyDynaBean();
								dataBean.set("content", (String)rec.get("tablename"));
								abean.set("tablename", dataBean);
								dataBean = new LazyDynaBean();
								dataBean.set("content", (String)rec.get("nbase"));
								abean.set("nbase", dataBean);
								dataBean = new LazyDynaBean();
								dataBean.set("content", (String)rec.get("only_value"));
								abean.set("only_value", dataBean);
								dataBean = new LazyDynaBean();
								dataBean.set("content", (String)rec.get("end_date"));
								abean.set("end_date", dataBean);
								dataBean = new LazyDynaBean();
								dataBean.set("content", (String)rec.get("start_date"));
								abean.set("start_date", dataBean);
								dataBean = new LazyDynaBean();
								String b0110 = (String)rec.get("b0110");
								if(StringUtils.isNotEmpty(b0110)&&b0110.indexOf("`")!=-1) {
									b0110 = b0110.split("`")[1];
								}
								dataBean.set("content", b0110);
								abean.set("b0110", dataBean);
								dataBean = new LazyDynaBean();
								String e0122 = (String)rec.get("e0122");
								if(StringUtils.isNotEmpty(e0122)&&e0122.indexOf("`")!=-1) {
									e0122 = e0122.split("`")[1];
								}
								dataBean.set("content", e0122);
								abean.set("e0122", dataBean);
								/*dataBean = new LazyDynaBean();
								dataBean.set("content", (String)rec.get("ins_id"));
								abean.set("ins_id", dataBean);*/
								HashMap datamap_ = dataBo.analysisJson2Map(record_id, archive_year);
								for(int k=0;k<cellList.size();k++) {
									TemplateSet setbo = (TemplateSet) cellList.get(k);
					        		if("H".equals(setbo.getFlag())) {
					        			continue;
					        		}
					        		String fldname  = setbo.getTableFieldName();
									if(StringUtils.isNotBlank(fldname))
										fldname =fldname.toLowerCase();
									if(setbo.getFlag()==null|| "".equalsIgnoreCase(setbo.getFlag()))
										setbo.setFlag("H");
									if(setbo.isABKItem()) {
										if(setbo.isSubflag()) {
											continue;
										}
										String codesetid = setbo.getCodeid();
										String itemtype = setbo.getField_type();
										LazyDynaBean dataBean_ = new LazyDynaBean();
										String content = String.valueOf(datamap_.get(fldname)==null?"":datamap_.get(fldname));
										if("A".equalsIgnoreCase(itemtype)&&StringUtils.isNotBlank(codesetid)&&!"0".equals(codesetid)) {
											content=AdminCode.getCodeName(codesetid,content);
										}
										dataBean_.set("content", content);
										abean.set(fldname, dataBean_);
									}
								}
								datalist.add(abean);
							}
						}
					}
				}
			    excelUtil.exportExcelWithMultiSheets(fileName,null, headList, dataList, null, 0);
			}else {
				for(int k=0;k<columnList.size();k++) {
			    	ColumnsInfo columnsInfo = columnList.get(k);
			    	LazyDynaBean abean = new LazyDynaBean();
			    	if("print".equals(columnsInfo.getColumnId())||"year".equals(columnsInfo.getColumnId())||"task_id".equals(columnsInfo.getColumnId())||
			    			"tabid".equals(columnsInfo.getColumnId())||"record_id".equals(columnsInfo.getColumnId())||"archive_id".equals(columnsInfo.getColumnId())
			    			||"ins_id".equals(columnsInfo.getColumnId())) {
			    		continue;
			    	}
			    	abean.set("itemid", columnsInfo.getColumnId());
			        abean.set("content", columnsInfo.getColumnDesc());
			        abean.set("colType", columnsInfo.getColumnType());
			        abean.set("dateFormat", "yyyy-MM-dd HH:mm:ss");
			        abean.set("columnWidth",4000);
			        if("D".equalsIgnoreCase(columnsInfo.getColumnType())) {
			        	abean.set("columnWidth",6000);
			        }
			        abean.set("codesetid", columnsInfo.getCodesetId());
			        headList.add(abean);
				}
				excelUtil.exportExcel(fileName,"sheet1",null, headList, dataList, null, 0);
			}
	        this.formHM.put("fileName", PubFunc.encrypt(fileName));
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 查询历史数据
	 * @param tabid
	 * @param queryType
	 * @param timeslot
	 * @param module_id 
	 * @param intoflag 
	 * @throws GeneralException 
	 */
	private void getHistoryData(String tabid, String queryType, String timeslot, String module_id) throws GeneralException {
		RowSet rset = null;
		try {
			HistoryDataBo historyDataBo = new HistoryDataBo(this.frameconn,this.userView);
			ContentDAO dao = new ContentDAO(this.frameconn);
			Calendar calendar = Calendar.getInstance();
			String searchsql = "";
			//查询归档的年份
			String sql = "select "+Sql_switcher.year("start_date")+" year from t_instance_archive group by "+Sql_switcher.year("start_date");
			rset = dao.search(sql);
			while(rset.next()) {
				int year = rset.getInt("year");
				//xus 20/5/9 vfs_dm Sql_switcher.searchDbServer()==Constant.ORACEL 用于判断达梦和oracle 与 其他数据库的区别		
				searchsql+=" select DISTINCT a.record_id,a.archive_id,a.name,a.ins_id,a.tabid,a.b0110,a.e0122,c.dbname nbase,a.only_value,b.start_date,b.end_date,"+year+" year,a.task_id,"
						+(Sql_switcher.searchDbServer()==Constant.ORACEL?Sql_switcher.numberToChar("a.tabid")+"||'：'||d.name": Sql_switcher.numberToChar("a.tabid")+"+'：'+d.name")+" tablename from "
						+ "t_data_"+year+" a,t_instance_archive b,dbname c,t_cells_archive d where a.ins_id=b.ins_id and c.pre=a.nbase and a.tabid=d.tabid and d.id=a.archive_id ";
				if(!this.userView.isSuper_admin()) {
					searchsql+=this.getPriWhereSql(historyDataBo,module_id);
					StringBuffer dbnamelist = this.userView.getDbpriv();
					if(dbnamelist.length()>0) {
						String dbname = "";
						String[] dbnamearr = dbnamelist.toString().split(",");
						for(int i=0;i<dbnamearr.length;i++) {
							if(StringUtils.isNotBlank(dbnamearr[i]))
								dbname+="'"+dbnamearr[i]+"',";
						}
						if(dbname.length()>0) {
							dbname = dbname.substring(0,dbname.length()-1);
						}
						if(StringUtils.isNotBlank(dbname))
							searchsql+=" and c.pre in ("+dbname+")";
						else {
							searchsql+=" and c.pre in ('-1')";
						}
					}
				}else {
					String tabids = historyDataBo.getTabids(module_id);
					searchsql+=" and b.tabid in ("+tabids+")";
				}
				searchsql+=" union all";
			}
			if(searchsql.length()>0) {
				searchsql = searchsql.substring(0,searchsql.length()-9);
			}else {
				Calendar date = Calendar.getInstance();
		        String year = String.valueOf(date.get(Calendar.YEAR));
				searchsql+=" select '' record_id,'' archive_id,'' name,'' ins_id,'' tabid,'' b0110,'' e0122,''nbase,'' only_value,b.start_date,b.end_date,"+year+" year,'' task_id,'' tablename from "
						+ "t_instance_archive b where 1=1 ";
				/*if(!this.userView.isSuper_admin()) {
					searchsql+=this.getPriWhereSql(historyDataBo,module_id);
				}else {
					String tabids = historyDataBo.getTabids(module_id);
					searchsql+=" and b.tabid in ("+tabids+")";
				}*/
			}
			if("1".equals(queryType)) {
				String conSql = "";
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("templatehistorydata");
				if("toyear".equals(timeslot)) {
					conSql+=" and ";
					conSql+=Sql_switcher.year("start_date");
					conSql+="=";
					conSql+=Sql_switcher.toYear();
				}
				else if("toquarter".equals(timeslot)) {
					conSql+=" and ";
					switch(calendar.get(Calendar.MONTH)){
			        	case 0:
			        	case 1:
			        	case 2:
			        		conSql+=Sql_switcher.month("start_date");
			        		conSql+=" in (1,2,3)";
			        		break;
			        	case 3:
			        	case 4:
			        	case 5:
			        		conSql+=Sql_switcher.month("start_date");
			        		conSql+=" in (4,5,6)";
			        		break;
		        		case 6:
			        	case 7:
			        	case 8:
			        		conSql+=Sql_switcher.month("start_date");
			        		conSql+=" in (7,8,9)";
			        		break;
			        	case 9:
			        	case 10:
			        	case 11:
			        		conSql+=Sql_switcher.month("start_date");
			        		conSql+=" in (10,11,12)";
			        		break;
		        		
					}
					conSql+=" and ";
					conSql+=Sql_switcher.year("start_date");
					conSql+="=";
					conSql+=Sql_switcher.toYear();
				}
				else if("tomonth".equals(timeslot)) {
					conSql+=" and ";
					conSql+=Sql_switcher.month("start_date");
					conSql+="=";
					conSql+=Sql_switcher.toMonth();
					conSql+=" and ";
					conSql+=Sql_switcher.year("start_date");
					conSql+="=";
					conSql+=Sql_switcher.toYear();
				}
				else if("timeframe".equals(timeslot)) {//时间段
					String start = (String)this.formHM.get("start");
					String end = (String)this.formHM.get("end");
					if(StringUtils.isNotBlank(start)){
						start= start.replace(".", "-").replace("-", "");
						conSql+=" and ";
						conSql+=Sql_switcher.year("start_date")+"*10000 + ";
						conSql+=Sql_switcher.month("start_date")+"*100 +";
						conSql+=Sql_switcher.day("start_date")+"";
						conSql+=">=";
						conSql+=start;
					}
					
					if(StringUtils.isNotBlank(end)){
						end= end.replace(".", "-").replace("-", "");
						conSql+=" and ";
						conSql+=Sql_switcher.year("start_date")+"*10000 + ";
						conSql+=Sql_switcher.month("start_date")+"*100 +";
						conSql+=Sql_switcher.day("start_date")+"";
						conSql+="<=";
						conSql+=end;
					}
				}
				if(!"-1".equals(tabid)) {
					conSql+=" and tabid="+tabid;
				}
				if(searchsql.length()>0) {
					tableCache.setTableSql("select * from ("+searchsql+") aa where 1=1 "+conSql.toString());
				}
				//tableCache.setQuerySql(conSql.toString());
				return;
			}
			
			ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
			columnList = historyDataBo.getHisColumn();
			TableConfigBuilder builder = new TableConfigBuilder("templatehistorydata", columnList, "templatehistorydata1", userView, this.getFrameconn());
			builder.setDataSql(searchsql);
			builder.setOrderBy("order by start_date desc");
			builder.setSelectable(true);
			builder.setSortable(false);
			builder.setPageSize(20);
			builder.setColumnFilter(true);
			builder.setTableTools(historyDataBo.getButtonList(module_id));
			String config = builder.createExtTableConfig();
			this.formHM.put("tableConfig", config.toString());
			ArrayList fieldsArray = new ArrayList();
			ArrayList fieldsMap = new ArrayList();
			for(int i=0;i<columnList.size();i++) {
				ColumnsInfo column = columnList.get(i);
				LazyDynaBean item = new LazyDynaBean();
				HashMap map = new HashMap();
				if(column.getLoadtype()!=3){
					if("print".equals(column.getColumnId())) {
						continue;
					}
		            item.set("codesetid", column.getCodesetId());
		            item.set("useflag", "1");
		            item.set("itemtype", column.getColumnType());
		            item.set("itemid", column.getColumnId().toUpperCase());
		            item.set("itemdesc", column.getColumnDesc());
		            item.set("format", column.getDisFormat());
		            map.put("type", column.getColumnType());
		            map.put("itemid", column.getColumnId().toUpperCase());
		            map.put("itemdesc", column.getColumnDesc());
		            map.put("codesetid", column.getCodesetId());
		            map.put("codesetValid", false);
		            //通用查询 日期改为按年月日查询
		            if("D".equals(column.getColumnType())) {
		                map.put("format", "Y-m-d");
		            }
		            fieldsMap.add(item);
		            fieldsArray.add(map);
				}
			}
			this.formHM.put("fieldsMap", fieldsMap);
			this.formHM.put("fieldsArray", fieldsArray);
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}
	private String getPriWhereSql(HistoryDataBo historyDataBo, String module_id) {
		StringBuffer sql = new StringBuffer();
		//查询登录用户权限范围内模板
		String tabids = historyDataBo.getTemplates(module_id);
		if(tabids.length()==0){
			sql.append(" and 1=2");
		}
		else
			sql.append(" and b.tabid in ("+tabids+")");
		String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板  
        if((operOrg!=null)&&(!"UN`".equalsIgnoreCase(operOrg)))
        {
            String strB0110Where="";	                                  
            if(operOrg.length() >3)
            {
                String[] temp = operOrg.split("`");
                for (int j = 0; j < temp.length; j++) { 
                     if (temp[j]!=null&&temp[j].length()>0) {
                         strB0110Where =strB0110Where+ 
                             " or a.b0110 like '" + temp[j].substring(2)+ "%'"+
                             " or a.e0122 like '" + temp[j].substring(2)+ "%'";             
                     }
                }	         
            }
            //strB0110Where=strB0110Where +" or "+Sql_switcher.sqlNull("a.b0110", "##")+"='##'";
            if(strB0110Where.length()>0){
                strB0110Where=strB0110Where.substring(3);
                strB0110Where = "("+strB0110Where+")";
                sql.append(" and ");
                sql.append(strB0110Where);             
            }                      	
        }
        return sql.toString();
	}
}
