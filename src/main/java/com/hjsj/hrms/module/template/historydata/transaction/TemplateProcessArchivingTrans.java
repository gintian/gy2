package com.hjsj.hrms.module.template.historydata.transaction;

import com.hjsj.hrms.module.template.historydata.businessobject.HistoryDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
* @Title: TemplateProcessArchivingTrans
* @Description:流程归档
* @author: hej
* @date 2019年10月22日 下午1:31:06
* @version
 */
public class TemplateProcessArchivingTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String transType=(String) this.getFormHM().get("transType");
		String querytype = (String) this.getFormHM().get("type");
		try{
			if(StringUtils.isNotBlank(querytype)){//页面模糊查询
	            StringBuffer condsql = new StringBuffer("");
	            TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("processarchiving");
	            if("1".equals(querytype)){//查询栏查询
	            	 List values = (ArrayList) this.getFormHM().get("inputValues");
	       			 for(int i=0;i<values.size();i++){
	   					String value = SafeCode.decode(values.get(i).toString());
	   					if (i == 0) {
	   						condsql.append(" and (");
	   					}else {
	   						condsql.append(" or ");
	   					}
						condsql.append("( tabid like '%"+value+"%' or ");
	   					condsql.append(" name like '%"+value+"%') ");
	   					if(i == values.size()-1){
	   						condsql.append(" ) ");
	   					}
	   				}
	       			tableCache.setQuerySql(condsql.toString());
	            }
			}else {
				HistoryDataBo historyDataBo = new HistoryDataBo(this.frameconn,this.userView);
				String _static="static";
				if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
					_static="static_o";
				}
				if("0".equals(transType)) {
					//查询流程归档列表数据
					String sql = "select a.tabid,a.operationname,a.name,"+Sql_switcher.dateToChar("b.archive_time", "YYYY.MM.dd")+
							" archive_time from template_table a left join (select tabid,max(archive_time) as archive_time from t_cells_archive GROUP BY tabid) b on a.tabid=b.tabid ";
					if(!this.userView.isSuper_admin()) {
						//查询登录用户权限范围内模板
						String tabids = historyDataBo.getTemplates("-1");
						if(tabids.length()==0) {
							sql+=" where 1=2";
						}else
							sql+=" where a.tabid in ("+tabids+")";
					}else {
						sql+=" where 1=1 ";
					}
					sql+=" and a."+_static+"!=10 and a."+_static+"!=11";//暂时屏蔽单位岗位模板归档
					String ordersql = " order by archive_time desc,tabid asc";
					if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
						ordersql = " order by archive_time desc nulls last,tabid asc";
					}
					ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
					column = historyDataBo.getColumn();
					TableConfigBuilder builder = new TableConfigBuilder("processarchiving", column, "processarchiving1", userView, this.getFrameconn());
					builder.setDataSql(sql);
					builder.setOrderBy(ordersql);
					builder.setSelectable(true);
					builder.setSortable(false);
					builder.setPageSize(100);
					String config = builder.createExtTableConfig();
					this.formHM.put("tableConfig", config.toString());
				}else if("1".equals(transType)) {
					String processdate = (String) this.getFormHM().get("processdate");
					String tabids = (String) this.getFormHM().get("tabids");
					if("all".equals(processdate)) {
						//得到当前日期
						Date date = new Date();  
				        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
				        processdate = sdf.format(date);  
					}
					//查询归档日期以前的所有模板流程数据(已结束或已终止)
					this.searchTemplateEndProcess(processdate,historyDataBo,tabids);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 查询归档日期以前的所有模板流程数据(已结束或已终止)
	 * @param processdate
	 * @param historyDataBo 
	 * @param tabids 
	 * @throws GeneralException 
	 */
	private void searchTemplateEndProcess(String processdate, HistoryDataBo historyDataBo, String tabids) throws GeneralException {
		RowSet rset = null;
		JSONObject returnJson = new JSONObject();
		try {
			DbWizard dbwizard=new DbWizard(this.frameconn);
			ContentDAO dao = new ContentDAO(this.frameconn);
			//采用vfs 附件不存路径下
			/*ConstantXml constantXml = new ConstantXml(this.frameconn,"FILEPATH_PARAM");
	        String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");
	        if(StringUtils.isNotBlank(rootDir)){
	        	   rootDir=rootDir.replace("\\",File.separator);          
	        	   if (!rootDir.endsWith(File.separator)) 
	        		   rootDir =rootDir+File.separator;   
	        }
	        //判断多媒体文件路径是否存在
	        File rootFile = new File(rootDir);
     	    if(!rootFile.isDirectory()) {//文件路径不存在
			    JSONObject returnData = new JSONObject();
				returnJson.put("return_data", returnData);
				returnJson.put("return_code", "failed");
	            returnJson.put("return_msg", ResourceFactory.getProperty("template.processArchiving.filepatherrormessage"));
	            this.getFormHM().put("returnStr", returnJson.toString());
				return;
     	    }
	        historyDataBo.setRootDir(rootDir);*/
			StringBuffer strsql=new StringBuffer();
			StringBuffer countsql=new StringBuffer();
			StringBuffer topsql=new StringBuffer();
			countsql.append("select count(a.ins_id) num from (");
			String withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) { 
				withNoLock=" WITH(NOLOCK) ";
				topsql.append("select top 50000 a.ins_start_date from (");
			}
			String format="yyyy-MM-dd";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
				topsql.append("select a.ins_start_date from (");
			}
			strsql.append("select U.ins_id,case when T.task_topic like '%"+ResourceFactory.getProperty("label.sum")+"0%' then U.name  else T.task_topic end name,U.tabid,U.actorname fullname, U.b0110  unitname,a0101, task_state finished ,"
				+Sql_switcher.dateToChar("U.start_date",format)+" as ins_start_date,"+Sql_switcher.dateToChar("T.end_date",format)+" as ins_end_date,T.actor_type,T.actorname,T.task_id ");
			strsql.append("from t_wf_task T "+withNoLock+",t_wf_instance U "+withNoLock+",template_table tt "+withNoLock);
			strsql.append(" where T.ins_id=U.ins_id  and  U.tabid=tt.tabid ");
			strsql.append(" and ( T.task_type='9' and  (T.task_state='5' or T.task_state='4') )");
			strsql.append(" and tt.tabid in (");
			strsql.append(tabids);
			strsql.append(")");
			//1：审批任务  
			strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  ");
			//按归档时间时间查询
			strsql.append(PubFunc.getDateSql("<=","U.start_date",processdate));
			countsql.append(strsql);
			topsql.append(strsql);
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) { 
				topsql.append(") a order by a.ins_start_date");
			}else if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				topsql.append(" order by ins_start_date) a rownum<=50000 ");
			}
			strsql.append(" order by U.tabid,ins_start_date,U.ins_id");
			countsql.append(") a");
			//校验所选归档数据个数，限制在5万内，超出给出提示。
			rset = dao.search(countsql.toString());
			int num = 0;
			if(rset.next()) {
				num = rset.getInt("num");
				if(num>50000) {
					//查询出可归档的数据时间
					rset = dao.search(topsql.toString());
					if(rset.last()) {
						String processDate = rset.getString("ins_start_date");
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						long dif = df.parse(processDate).getTime() - 86400 * 1000;
						Date date = new Date();
						date.setTime(dif);
						processDate = df.format(date);
						JSONObject returnData = new JSONObject();
						returnJson.put("return_data", returnData);
						returnJson.put("return_code", "failed");
			            returnJson.put("return_msg", ResourceFactory.getProperty("template.processArchiving.warnmessage")+processDate+"!");
			            this.getFormHM().put("returnStr", returnJson.toString());
						return;
					}
				}
			}
			
			boolean isHaveData = false;
			Set tabset = new HashSet();
			HashMap tabidMap = new HashMap();
			int i=0;
			int j=1;
			int tab_id = 0;
			ArrayList insList = new ArrayList();
			ArrayList insArchList = new ArrayList();
			int archivecellid = 0;
			int maxfileNumber = 1;
			int fileSize = 0;
			//一页500条
			int page = num / 500 + 1; 
			for(int m=0;m<page;m++) {
				rset = dao.search(strsql.toString(), 500, m+1);
			    while(rset.next()) {
					isHaveData = true;
					ArrayList list = new ArrayList();
					ArrayList list_arch = new ArrayList();
					int ins_id = rset.getInt("ins_id");
					int task_id = rset.getInt("task_id");
					int tabid =rset.getInt("tabid");
					//创建数据表按年份t_data_年份
		            ArrayList paramlist = new ArrayList();
		            String sql = "select "+Sql_switcher.year("start_date")+" year from t_wf_task where ins_id=?";
		            paramlist.add(ins_id);
		            this.frowset = dao.search(sql,paramlist);
					int year = 0;
					if(this.frowset.next()) {
						year = this.frowset.getInt("year");
						String tablename = "t_data_"+year; 
						Table table=new Table(tablename);
						if(!dbwizard.isExistTable(tablename,false)){
							historyDataBo.addFieldItem(table);	
							dbwizard.createTable(table);
						}
					}
					if(!tabset.contains(tabid)) {
						/*
						 * 调用vfs无需考虑文件夹下存储多少文件
						//查询与模板相关的最大的file_number
						sql = "select max(file_number) maxnum from t_cells_archive where tabid=?";
						ArrayList paramList = new ArrayList();
						paramList.add(tabid);
						this.frowset = dao.search(sql, paramList);
						if(this.frowset.next()) {
							maxfileNumber = this.frowset.getInt("maxnum");
							if(maxfileNumber==0) {
								maxfileNumber =1;
							}
						}
						//查到最大filenumber文件下有多少个文件
						String file_patch = "TEMPLATE_ARCHIVE"+File.separator+"Y_"+year+File.separator+"T_"+tabid+"_"+maxfileNumber;
						File file = new File(rootDir+file_patch);
						if(file.exists()) {
							String[] files = file.list();
							fileSize = files.length;
						}else
							fileSize = 0;
						i=fileSize;
						//模板样式归档表【t_cells_archive】插入数据
*/						archivecellid = historyDataBo.insertIntoArchiveCells(tabid,processdate);
						tabset.add(tabid);
					}
					/*if(!tabidMap.containsKey(tabid)) {
						i=fileSize;
						j=1;
						tabidMap.put(tabid, i);
						tab_id = tabid;
					}
					if(tab_id==tabid) {
						if(i==1000*j){//限制1000之内
							j++;
						}
						tabidMap.put(tabid, i);
					}
					i++;
					*/
					list.add(ins_id);
					insList.add(list);
					list_arch.add(ins_id);
					list_arch.add(ins_id);
					insArchList.add(list_arch);
					//各单据记录JSON格式数据文件存储
					historyDataBo.saveDataTocells(tabid,year,ins_id,task_id,(maxfileNumber+j-1),archivecellid);
					//更新t_cells_archive表中file_number字段
					RecordVo vo = new RecordVo("t_cells_archive");
		        	vo.setInt("id", archivecellid);
		        	vo.setInt("file_number", (maxfileNumber+j-1));
		        	dao.updateValueObject(vo);
				}
			}
			if(!isHaveData) {
				JSONObject returnData = new JSONObject();
				returnJson.put("return_data", returnData);
				returnJson.put("return_code", "failed");
	            returnJson.put("return_msg", ResourceFactory.getProperty("template.processArchiving.nodata"));
	            this.getFormHM().put("returnStr", returnJson.toString());
				return;
			}else {
				//填充t_instance_archive表的sql
				String processdate_ = processdate;
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					processdate_ = "'"+processdate.replace("-", ".")+"'";
				StringBuffer insertinsData = new StringBuffer("insert into t_instance_archive(ins_id,name,start_date,end_date,finished,tabid,template_type,bfile,actor_type,"
						+ "actorid,actorname,b0110,archive_time,archive_user,archive_fullname) select twi.ins_id,name,twi.start_date,twt.end_date,finished,"
						+ "tabid,template_type,bfile,twi.actor_type,twi.actorid,twi.actorname,b0110,"+Sql_switcher.charToDate(processdate_)+",'"
						+ this.userView.getUserName()+"','"+this.userView.getUserFullName()+"' from t_wf_instance twi,t_wf_task twt where twi.ins_id=twt.ins_id "
						+ "and twi.ins_id =? and twt.task_id =(select max(task_id) from t_wf_task where ins_id=?)");
				//填充t_instance_archive
				dao.batchUpdate(insertinsData.toString(),insArchList);
				//复制t_wf_task 以及t_wf_task_objlink
				historyDataBo.copyData2Archive(processdate,insList);
				//删除t_wf_instance表相关数据
				String delSql = "delete from t_wf_instance where ins_id=?";
				String delSql1 = "delete from t_wf_task where ins_id=?";
				String delSql2 = "delete from t_wf_task_objlink where ins_id=?";
				for(Object obj:tabset) {
					String delSql3 = "delete from templet_"+obj+" where ins_id=?";
					dao.batchUpdate(delSql3,insList);
				}
				String delSql4 = "delete from t_wf_file where ins_id=?";
				dao.batchUpdate(delSql,insList);
				dao.batchUpdate(delSql1,insList);
				dao.batchUpdate(delSql2,insList);
				dao.batchUpdate(delSql4,insList);
				JSONObject returnData = new JSONObject();
				returnJson.put("return_data", returnData);
				returnJson.put("return_code", "success");
	            returnJson.put("return_msg", "success");
	            this.getFormHM().put("returnStr", returnJson.toString());
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}

}
