package com.hjsj.hrms.businessobject.general.template.workflow;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.general.template.selfplatform.TemplateSelfPlateFormCardBo;
import com.hjsj.hrms.businessobject.general.template.selfplatform.*;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.template.templatecard.businessobject.AttachmentBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.TemplateCardBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.templatenavigation.businessobject.TemplateNavigationBo;
import com.hjsj.hrms.module.template.templatesubset.businessobject.TemplateSubsetBo;
import com.hjsj.hrms.module.template.templatetoolbar.apply.businessobject.TemplateApplyBo;
import com.hjsj.hrms.module.template.templatetoolbar.apply.businessobject.TemplateApplyPrepareBo;
import com.hjsj.hrms.module.template.templatetoolbar.businessobject.TemplateToolBarBo;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject.DownAttachUtils;
import com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject.OutWordBo;
import com.hjsj.hrms.module.template.utils.*;
import com.hjsj.hrms.module.template.utils.javabean.*;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.io.File;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能：自助平台模板接口
 * @author GH
 *
 */
public class TemplateSelfServicePlatformBo {
	private Connection conn = null;
    private UserView userView = null;
    private String tableName="";//流程表表名
    private boolean syncTabInfo=false;
    private  int recorsionCount;
	// 日志文件
	private Logger log = LoggerFactory.getLogger(TemplateSelfServicePlatformBo.class);
	public TemplateSelfServicePlatformBo(Connection _conn, UserView _userView)
    {
    	this.conn=_conn;
    	this.userView=_userView;
    }
	//--导入子集--------------------------------------------------------------------------------------------
	/**
	 * 导出子集Excel模板
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String outExcel(HashMap paramMap)throws Exception {
		String fieldSet=(String)paramMap.get("fieldSet");//子集
		ArrayList<MorphDynaBean> columnList=(ArrayList<MorphDynaBean>)paramMap.get("column");
		TemplateSelfSubSetExcelOutBo templateSelfSubSetExcelOutBo=new TemplateSelfSubSetExcelOutBo(conn, userView);
		String fileName=templateSelfSubSetExcelOutBo.createExcel(fieldSet,columnList);
		return PubFunc.encrypt(fileName);
	}
	/**
	 * 功能：导入子集数据。
	 * @param paramMap
	 * @throws GeneralException
	 */
	public Map importExcelInfo(HashMap paramMap) throws GeneralException {
		Map data=new HashMap();
		try {
			TemplateSelfSubSetExcelOutBo templateSelfSubSetExcelOutBo=new TemplateSelfSubSetExcelOutBo(conn, userView);
			data=templateSelfSubSetExcelOutBo.importExcelInfo(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException(e.getMessage());
		}
		return data;
	}

	//-选中记录---------------------------------------------------------------------------------------------
	/**
	 * 选中记录
	 * @param paramMap
	 * @throws GeneralException
	 */
	public void filterFlag(HashMap paramMap) throws GeneralException {
		String taskId = (String) paramMap.get("taskId");
		String setname=this.tableName;
		String filterSql = (String) paramMap.get("filterStr");
		String search_sql = (String) paramMap.get("search_sql");
		String tableSql=(String) paramMap.get("tableSql");
		TemplateUtilBo utilBo = new TemplateUtilBo(this.conn,this.userView);
		ArrayList list = new ArrayList();
		RowSet rst = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(StringUtils.isEmpty(tableSql)){
				String moduleId=(String)paramMap.get("moduleId");
		    	String tabId=(String)paramMap.get("tabId");
		    	 /** 区分报审、报备、加签
		         * 1：报审 2：加签  3 报备
		        */
		        String approveFlag=(String)paramMap.get("approveFlag");
		        if(StringUtils.isEmpty(approveFlag)){
		        	approveFlag="1";
		        }
		        String searchName=(String)paramMap.get("searchName");
				tableSql=getSearchSql(tabId, taskId, moduleId, searchName, approveFlag);
			}
			if("0".equals(taskId))
			{
				//查看能不能查出数据
				if(StringUtils.isNotEmpty(filterSql)) {
					tableSql+=filterSql;
				}
				rst = dao.search(tableSql.toString());
				if(rst.next()){
					StringBuffer updateSql = new StringBuffer("");
					updateSql.append("update " + setname + " set submitflag=? where 1=1 ");

					//增加查询控件的查询条件
					if(StringUtils.isNotEmpty(search_sql)){
						updateSql.append(search_sql);
					}
					//过滤条件
					if(StringUtils.isNotEmpty(filterSql)){
						updateSql.append(filterSql);
					}
					//先全部设置为不选中
					dao.update("update " + setname + " set submitflag=0 where 1=1 ");
					list.add("1");
					dao.update(updateSql.toString(),list);
				}
			}
			else
			{
				if(StringUtils.isNotBlank(taskId)){
					if(StringUtils.isNotEmpty(filterSql)) {
						tableSql+=filterSql;
					}
					rst = dao.search(tableSql.toString());
					if(rst.next()){
						ArrayList listnot = new ArrayList();
						StringBuffer updateSql = new StringBuffer("");
						StringBuffer updateSql1 = new StringBuffer("");
						StringBuffer updateSql2 = new StringBuffer("");

						updateSql1.append("update t_wf_task_objlink set submitflag=? ");
						updateSql1.append(" where seqnum in (select seqnum from "+setname+" where 1=1 ");
						updateSql1.append(" and ins_id=? ");

						updateSql2.append("   ) ");
						updateSql2.append("  and  task_id=?");

						//然后加上过滤条件设置选中
						updateSql.append(updateSql1.toString());
						//增加查询控件的查询条件
						if(StringUtils.isNotEmpty(search_sql)){
							updateSql.append(search_sql);
						}
						//过滤条件
						if(StringUtils.isNotEmpty(filterSql)){
							updateSql.append(filterSql);
						}
						updateSql.append(updateSql2.toString());
						//批量的情况 task_id 有逗号
						ArrayList listAll = utilBo.getTaskIdtoInsId(taskId);
						for(int i=0;i<listAll.size();i++){
							HashMap map = (HashMap) listAll.get(i);
							String insId = (String)map.get("ins_id");
							String taskIdo = (String)map.get("task_id");
							ArrayList listTemp = new ArrayList();
							listTemp.add("1");
							listTemp.add(insId);
							listTemp.add(taskIdo);
							list.add(listTemp);

							ArrayList listnotTemp = new ArrayList();
							listnotTemp.add("0");
							listnotTemp.add(insId);
							listnotTemp.add(taskIdo);
							listnot.add(listnotTemp);
						}

						//先全部设置为不选中
						dao.batchUpdate(updateSql1.toString()+updateSql2.toString(),listnot);
						dao.batchUpdate(updateSql.toString(),list);
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rst);
		}
	}

	private String getSearchSql(String tabId, String taskId, String module_id,
			String searchName, String approveFlag) throws GeneralException {
		TemplateDataBo dataBo = new TemplateDataBo(this.conn,
		        this.userView, Integer.parseInt(tabId));
		String dataTabName = dataBo.getUtilBo().getTableName(module_id,
		        Integer.valueOf(tabId), taskId);
		String sql = dataBo.getSql(module_id, "1", approveFlag,
		        dataTabName, taskId, "", "", "" );
		String nameColumn = "";
		if (dataBo.getParamBo().getInfor_type() == 2
		        || dataBo.getParamBo().getInfor_type() == 3) {//单位名称
		    if (dataBo.getParamBo().getOperationType() == 5) {
		        nameColumn = "codeitemdesc_2";
		    } else {
		        nameColumn = "codeitemdesc_1";
		    }
		}
		if (dataBo.getParamBo().getInfor_type() == 1) {
		    DbWizard dbWizard = new DbWizard(this.conn);
		    if (dataBo.getParamBo().getOperationType() == 0) {//人员调入型
		        if (dbWizard.isExistField(dataTabName, "a0101_2", false)) {
		            nameColumn = "a0101_2";
		        }
		    } else {
		        nameColumn = "a0101_1";
		    }
		}
		if(StringUtils.isNotEmpty(searchName)){
		    sql+= " and "+nameColumn+" like '%"+searchName+"%'";
		}
		return sql;
	}
	//-撤销------------------------------------------------------------------------------------------------
	public HashMap delTemplate(HashMap paramMap) throws GeneralException {
		HashMap returnData = new HashMap();
		RowSet rowset=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String tab_id=(String)paramMap.get("tab_id");
			String task_id=(String)paramMap.get("task_id");
			String infor_type=(String)paramMap.get("infor_type");  //=1时代表人员 =2时代表单位 =3时代表职位

			String isDelMsg=(String)paramMap.get("isDelMsg"); //是否删除消息表中的记录

			StringBuffer task_ids=new StringBuffer("");
			if(!"0".equalsIgnoreCase(task_id)){//进入了审批流
				String dataStr= (String) paramMap.get("dataStr");  //存储taskid 的集合，用“,”分割
				if(dataStr.indexOf(",")!=-1){//多个人
					String[] dataArr=dataStr.split(",");
					for(int i=0;i<dataArr.length;i++){
						if(dataArr[i].trim().length()>0)
						{
							String taskId = dataArr[i];
							if(!"0".equals(taskId)) {
								taskId = PubFunc.decrypt(taskId);
							}
							task_ids.append(","+taskId);
						}
					}
				}else{//单个人
					String taskId = dataStr;
					if(!"0".equals(taskId)) {
						taskId = PubFunc.decrypt(taskId);
					}
					task_ids.append(","+taskId);
				}
			}

			String updateMsg="delete from tmessage where   object_type=?  and noticetempid=? ";
			ArrayList updateMsgList=new ArrayList();
			String select_str="";
			if(isDelMsg==null|| "0".equals(isDelMsg)) //是否同步删除通知单里的记录
			{
				updateMsg="update tmessage set  state=0 where  object_type=?  and noticetempid=?  ";
			}

			if("1".equals(infor_type))
			{
				updateMsg+=" and a0100=?  and lower(db_type)=?";
				select_str=" a0100,basepre ";

			}
			else if("2".equals(infor_type))
			{
				updateMsg+=" and b0110=? ";
				select_str=" b0110 ";

			}
			else if("3".equals(infor_type))
			{
				updateMsg+=" and e01a1=? ";
				select_str=" e01a1 ";

			}

			// 根据tab_id和ins_id的取值得到人事异动列表对应的表名
			String setname= this.userView.getUserName()+"templet_"+tab_id;
			StringBuffer sql=new StringBuffer();
			if(task_id.length()>0 && !"0".equalsIgnoreCase(task_id)){//进入了审批流
				setname="templet_"+tab_id;
				sql=new StringBuffer("select "+select_str+" from "+setname+" where state=1 ");
				sql.append(" and  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
				sql.append(" and t_wf_task_objlink.submitflag=1 and t_wf_task_objlink.task_id in ("+task_ids.substring(1)+")  and t_wf_task_objlink.state<>3   )");

			}
			else//未进入审批流
			{
				sql=new StringBuffer("select "+select_str+" from "+setname+" where state=1 ");
				sql.append(" and  submitflag=1 ");
			}
			rowset=dao.search(sql.toString());
			while(rowset.next())
			{
				ArrayList valueList=new ArrayList();
				valueList.add(new Integer(infor_type));
				valueList.add(new Integer(tab_id));
				if("1".equals(infor_type))
				{
					valueList.add(rowset.getString("a0100"));
					valueList.add(rowset.getString("basepre").toLowerCase());
				}
				else if("2".equals(infor_type)) {
					valueList.add(rowset.getString("b0110"));
				} else if("3".equals(infor_type)) {
					valueList.add(rowset.getString("e01a1"));
				}
				updateMsgList.add(valueList);
			}
			if(updateMsgList.size()>0)
			{
				dao.batchUpdate(updateMsg.toString(), updateMsgList); //撤销记录时对通知表的操作
			}

			TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tab_id),this.userView);
			///开始撤销数据
			TemplateParam paramBo=new TemplateParam(this.conn,this.userView,Integer.parseInt(tab_id));
			TempletChgLogBo chgLogBo=new TempletChgLogBo(this.conn,this.userView,paramBo);

			if("0".equals(task_id)){//未进入审批流
				tablebo.updateWorkCodeState(setname,"submitflag=1");
				//删除签章
				StringBuffer signsql=new StringBuffer("select * from "+this.userView.getUserName()+"templet_"+tab_id);
				signsql.append(" where  submitflag=1");
				rowset=dao.search(signsql.toString());
				ArrayList personList=new ArrayList();
				while(rowset.next()){
					if("1".equalsIgnoreCase(infor_type)){
						String nbase=rowset.getString("BasePre");
						String a0100=rowset.getString("a0100");
						ArrayList list=new ArrayList();
						list.add(nbase);
						list.add(a0100);
						personList.add(list);
					}else if("2".equalsIgnoreCase(infor_type)){
						String nbase=rowset.getString("b0110");
						ArrayList list=new ArrayList();
						list.add(nbase);
						personList.add(list);
					}else if("3".equalsIgnoreCase(infor_type)){
						String nbase=rowset.getString("e01a1");
						ArrayList list=new ArrayList();
						list.add(nbase);
						personList.add(list);
					}
//					String signature = rowset.getString("signature");
//					if(signature!=null&&!"".equalsIgnoreCase(signature))//liuyz bug28641
//					{
//						delSignatureXml(dao,signature,paramBo);
//					}
					//删除附件 liuyz bug 26890
					if("1".equals(infor_type)&&(rowset.getString("basepre")!=null&&rowset.getString("a0100")!=null))
					{
						StringBuffer attarSql=new StringBuffer("delete from t_wf_file where Lower(basepre)=Lower(?) and objectid=? and tabid=? and create_user=? and ins_id=?");
						ArrayList childList=new ArrayList();
						childList.add(rowset.getString("basepre"));
						childList.add(rowset.getString("a0100"));
						childList.add(tab_id);
						childList.add(this.userView.getUserName());
						childList.add("0");
						dao.delete(attarSql.toString(), childList);
					}
					else if("2".equals(infor_type)&&(rowset.getString("b0110")!=null)){
						StringBuffer attarSql=new StringBuffer("delete from t_wf_file where  objectid=? and tabid=? and create_user=? and ins_id=?");
						ArrayList childList=new ArrayList();
						childList.add(rowset.getString("b0110"));
						childList.add(tab_id);
						childList.add(this.userView.getUserName());
						childList.add("0");
						dao.delete(attarSql.toString(), childList);
					}else if("3".equals(infor_type)&&(rowset.getString("e01a1")!=null)){
						StringBuffer attarSql=new StringBuffer("delete from t_wf_file where  objectid=? and tabid=? and create_user=? and ins_id=?");
						ArrayList childList=new ArrayList();
						childList.add(rowset.getString("e01a1"));
						childList.add(tab_id);
						childList.add(this.userView.getUserName());
						childList.add("0");
						dao.delete(attarSql.toString(), childList);
					}
				}
				dao.update("delete from "+setname+"  where  submitflag=1 ");
				chgLogBo.deleteChangeInfoNoInProcess(personList, "0", tab_id, "0", Integer.parseInt(infor_type));//删除变动日志信息。
			}
			else
			{
				WF_Instance ins=new  WF_Instance(Integer.parseInt(tab_id),this.conn,this.userView);
				TemplateParam tableParamBo = new TemplateParam(this.conn, this.userView,Integer.parseInt(tab_id));
				int sp_mode = tableParamBo.getSp_mode();
				StringBuffer strsql=new StringBuffer("select * from templet_"+tab_id);
				strsql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
				strsql.append(" and  task_id=xxx  and (state is null  or state=0)  and tab_id="+tab_id+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ");
				String[] temps=task_ids.toString().split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						ins.insertKqApplyTable(strsql.toString().replaceAll("xxx",temps[i]),tab_id,"0","10","templet_"+tab_id); //往考勤申请单中写入报批记录
						chgLogBo.deleteChangeInfoInProcess(strsql.toString().replaceAll("xxx",temps[i]),tab_id,Integer.parseInt(infor_type));//删除变动日志信息。
					}
				}
				{
					StringBuffer t_sql=new StringBuffer("select * from templet_"+tab_id+" where  ");
					t_sql.append("   exists (select null from t_wf_task_objlink where templet_"+tab_id+".seqnum=t_wf_task_objlink.seqnum and templet_"+tab_id+".ins_id=t_wf_task_objlink.ins_id ");
					t_sql.append(" and t_wf_task_objlink.task_id in ("+task_ids.substring(1)+")  and (t_wf_task_objlink.state is null  or t_wf_task_objlink.state=0)  and t_wf_task_objlink.tab_id="+tab_id+"  ");
					t_sql.append(" and t_wf_task_objlink.submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )   )");
					ArrayList recordList=dao.searchDynaList(t_sql.toString());
					TemplateInterceptorAdapter.deleteRecords(recordList,new Integer(tab_id).intValue(),paramBo,this.userView);
				}
				dao.update("update t_wf_task_objlink set state=3   where task_id in ("+task_ids.substring(1)+")  and (state is null  or state=0)  and tab_id="+tab_id+"  and  submitflag=1  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )");
				/**结束正在处理的任务*/
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						int taskid=Integer.parseInt(temps[i]);
						if(ins.isStartNode(taskid+"") && isAllSelectedTaskId(dao, tab_id, taskid)){
							ins.processEnd(Integer.valueOf(taskid), Integer.valueOf(tab_id), userView,0);
						}else{
							String topic=tablebo.getRecordBusiTopic(taskid,0);
							if(topic.indexOf(",共0")!=-1)
							{
								int ins_id = this.isAllPriTask(dao, tab_id, taskid ,sp_mode);
								if(ins_id!=-1){
									//结束流程
									RecordVo ins_vo=new RecordVo("t_wf_instance");
									ins_vo.setInt("ins_id",ins_id);
									ins_vo=dao.findByPrimaryKey(ins_vo);
									if(ins_vo!=null){
										ins_vo.setDate("end_date",new Date());
										ins_vo.setString("finished","6");
										dao.updateValueObject(ins_vo);
									}
								}
								RecordVo task_vo=new RecordVo("t_wf_task");
								task_vo.setInt("task_id",taskid);
								task_vo=dao.findByPrimaryKey(task_vo);
								if(task_vo!=null)
								{
									topic=tablebo.getRecordBusiTopicByState(taskid,3);
									task_vo.setDate("end_date",new Date()); //DateStyle.getSystemTime());
									task_vo.setString("task_state",String.valueOf(NodeType.TASK_TERMINATE));
									task_vo.setString("task_topic", topic);

									String fullsender=this.userView.getUserFullName();
									if(fullsender==null|| "".equalsIgnoreCase(fullsender)) {
										fullsender=this.userView.getUserName();
									}
									String sender=null;
									if(this.userView.getStatus()!=0) {
										sender=this.userView.getDbname()+this.userView.getA0100();
									} else {
										sender=this.userView.getUserId();
									}
									String appuser=task_vo.getString("appuser")+this.userView.getUserName()+",";
									task_vo.setString("appuser", appuser);
									task_vo.setString("a0100",sender);
									task_vo.setString("a0101",fullsender);
									task_vo.setString("content","撤销记录");
									if(ins_id!=-1){
										task_vo.setString("state", "06");
										task_vo.setString("task_type", String.valueOf(NodeType.END_NODE));
										task_vo.setString("actorname", "");
										task_vo.setString("sp_yj", "02");
									}
									dao.updateValueObject(task_vo);
								}
								/** 删除其它系统的待办任务 */
								PendingTask imip=new PendingTask();
								String pendingType="业务模板";
								String pendingCode="HRMS-"+PubFunc.encrypt(taskid+"");
								imip.updatePending("T",pendingCode,100,pendingType,userView);
							}
							else {
								dao.update("update t_wf_task set task_topic='"+topic+"' where task_id="+taskid);
							}
						}
					}
				}
			}
			returnData.put("return_code", "success");
		}
		catch(Exception e)
		{
			returnData.put("return_msg", e.getMessage());
			returnData.put("return_code", "error");
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rowset);
		}
		return returnData;
	}
	/**
	 * 确认流程走过的节点是否都是当前节点之前一条线上的
	 * @param dao
	 * @param tabid
	 * @param task_id
	 * @param sp_mode
	 * @return
	 * @throws GeneralException
	 */
	private int isAllPriTask(ContentDAO dao, String tabid, int task_id, int sp_mode) throws GeneralException {
		int ins_id = -1;
		recorsionCount = 0;
		RowSet rs=null;
		try{
	            String sqlstr = "select * from t_wf_task where ins_id=(select ins_id from t_wf_task where task_id="+task_id+")";
	            rs=dao.search(sqlstr);
	            int insid = 0;
	            int current_node_id = 0;
	            //走过的节点集合
	            HashSet usedNode = new HashSet();
	            while(rs.next())
	            {
	                if(task_id==rs.getInt("task_id")){//当前任务对应的节点
	                	current_node_id = rs.getInt("node_id");
	                	insid = rs.getInt("ins_id");
	                }else {
						usedNode.add(rs.getInt("node_id"));
					}
	            }
	            if(sp_mode==1) {
	            	ins_id = insid==0?-1:insid;
	            }else {
	            	int current_node_id_ = current_node_id;
		            //其一条线上的节点集合
		            HashSet usedNode_ = new HashSet();
	            	//找出前一个节点 并与走过的节点对比 并把它放入一个set中
		            //递归 超过400次自动结束
	            	this.getpriNode(dao,usedNode_,current_node_id_,usedNode);
	            	if(recorsionCount<=400){
	            		if(usedNode_.size()<usedNode.size()){//证明有不是他一条线的
	    	            }else{
	    	            	//将流程结束
	    	            	ins_id = insid;
	    	            }
	            	}
	            }
	        }
	        catch(Exception ex){
	            ex.printStackTrace();
	            throw GeneralExceptionHandler.Handle(ex);
	        }finally{
	        	PubFunc.closeDbObj(rs);
	        }
		 return ins_id;
	}
	/**
	 * 递归调用查找上一个节点
	 * @param dao
	 * @param usedNode_
	 * @param current_node_id_
	 * @param usedNode
	 * @throws SQLException
	 */
	private void getpriNode(ContentDAO dao, HashSet usedNode_, int current_node_id_, HashSet usedNode) throws SQLException{
		RowSet rowSet = null;
		int pre_node = 0;
		if(recorsionCount>400){//超过400次自动退出递归
			return;
		}
		recorsionCount++;
		try {
			String sql = "select pre_nodeid from t_wf_transition where next_nodeid="+current_node_id_;
			rowSet = dao.search(sql);
			while(rowSet.next()){
				pre_node = rowSet.getInt("pre_nodeid");//前一个节点
				if(usedNode_.contains(pre_node)) {
					return;
				}
				if(usedNode.contains(pre_node)){
					current_node_id_ = pre_node;
					usedNode_.add(pre_node);
					getpriNode(dao,usedNode_,current_node_id_,usedNode);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
	}

	/**
	 * @Title: isSelectedTaskId
	 * @Description: 判断单据里面的记录是否被选中，如果没有选中的，则后续不处理
	 * @param @param dao
	 * @param @param tabid
	 * @param @param task_id
	 * @param @return
	 * @param @throws GeneralException
	 * @return boolean
	 * @author:wangrd
	 * @throws
	*/
	private boolean isAllSelectedTaskId(ContentDAO dao,String tabid,int task_id)throws GeneralException
    {
	    boolean b=false;
	    RowSet rs=null;
        try
        {
            String sqlstr = "select count(*) from templet_"+tabid
                +" where  exists (select null from t_wf_task_objlink where templet_"
                +tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id "
                +"  and task_id="+task_id+"   and submitflag=0  and (state is null or  state=0 ) and ("
                +Sql_switcher.isnull("special_node","0")+"=0  or ( "
                +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
            rs=dao.search(sqlstr);
            if(rs.next())
            {
                if(rs.getInt(1)==0) {
					b=true;
				}
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }finally{
        	PubFunc.closeDbObj(rs);
        }
        return b;
    }
	//----导出pdf word-------------------------------------------------------------------------------------------------
	public HashMap outPdf(HashMap paramMap) throws GeneralException{
		HashMap returnData = new HashMap();
		TemplateFrontProperty frontProperty =new TemplateFrontProperty(paramMap);
		String moduleId = frontProperty.getModuleId();
		String returnFlag = frontProperty.getReturnFlag();
		String tabId = frontProperty.getTabId();
		String task_id = frontProperty.getTaskId();
		String infor_type=frontProperty.getInforType();
		String flag=(String)paramMap.get("flag");
		String outtype=(String)paramMap.get("outtype");//导出的类型 1 word 0 pdf
		String downtype=(String)paramMap.get("downtype");
		String noShowPageNo = frontProperty.getOtherParam("noshow_pageno");//设定的不显示的页签
		String taskid_validate = PubFunc.decrypt(frontProperty.getOtherParam("taskid_validate"));
		String isDelete = frontProperty.getOtherParam("isDelete");
		TemplateUtilBo utilBo= new TemplateUtilBo(this.conn,this.userView);
		String tableName=utilBo.getTableName(frontProperty.getModuleId(),
				Integer.parseInt(frontProperty.getTabId()), frontProperty.getTaskId());
		TemplateParam paramBo=new TemplateParam(this.conn,this.userView,Integer.parseInt(tabId));
		int signatureType = paramBo.getTemplateModuleParam().getSignatureType();
		if(flag==null|| "".equals(flag)) {
			flag="0";
		}
		ArrayList inslist=null;
		ArrayList tasklist=null;
		if(frontProperty.isBatchApprove())
		{
			tasklist=getTaskList(task_id);
		}
		else
		{
			tasklist=new ArrayList();
			tasklist.add(task_id);
		}
		String pageno=(String)paramMap.get("pageno");
		String out_pages=(String)paramMap.get("out_pages");
		if(StringUtils.isNotEmpty(out_pages)) {
			pageno=PubFunc.decrypt(out_pages);
		}
		RowSet rset=null;
		try
		{
			ArrayList objlist=new ArrayList();
			/**打印全部人员*/
			if("2".equals(flag))
			{
				StringBuffer buf=new StringBuffer();
				if(!"0".equals(task_id))
				{
					if("1".equals(infor_type)){
						buf.append("select basepre,a0100,ins_id from ");
					}else if("2".equals(infor_type)){
						buf.append("select b0110,ins_id from ");
					}else if("3".equals(infor_type)){
						buf.append("select e01a1,ins_id from ");
					}else{
						buf.append("select basepre,a0100,ins_id from ");
					}
					buf.append(tableName);
					buf.append(" where 1=1 ");

					buf.append(" and exists (select null from t_wf_task_objlink where "+tableName+".seqnum=t_wf_task_objlink.seqnum  and "+tableName+".ins_id=t_wf_task_objlink.ins_id  ");
					if("1".equals(returnFlag)||"2".equals(returnFlag) ||"11".equals(returnFlag)||"12".equals(returnFlag)||"13".equals(returnFlag)){//待办 ，我的已办\首页待办\首页待办列表\来自第三方系统或邮件
						buf.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
					}

					if(frontProperty.isBatchApprove())
					{
						buf.append(" and   task_id in (");
						for(int i=0;i<tasklist.size();i++)
						{
							if(i!=0) {
								buf.append(",");
							}
							buf.append(tasklist.get(i));
						}
						buf.append(")");
					}
					else
					{
						if(!"0".equals(task_id))
						{
							buf.append(" and  task_id=");
							buf.append(task_id);
						}
					}
					if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
						buf.append(" and ( "+Sql_switcher.isnull("state","0")+"=3 ) ) ");
					}else {
						buf.append(" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ");
					}
				}
				else
				{
					if("1".equals(infor_type)){
						buf.append("select basepre,a0100  from ");
					}else if("2".equals(infor_type)){
						buf.append("select b0110 from ");
					}else if("3".equals(infor_type)){
						buf.append("select e01a1 from ");
					}else{
						buf.append("select basepre,a0100  from ");
					}

					buf.append(tableName);
					buf.append(" where 1=1 ");
				}

//				if(filterStr.trim().length()>0){
//					buf.append(" and "+filterStr);
//				}
				buf.append(this.getOrderBy(tabId));
				ContentDAO dao=new ContentDAO(this.conn);
				rset=dao.search(buf.toString());
				/**求每个对应的实例*/
				inslist=new ArrayList();
				while(rset.next())
				{
					if(!"0".equals(task_id)) {
						inslist.add(rset.getString("ins_id"));
					} else {
						inslist.add("0");
					}
					if("1".equals(infor_type)){
						objlist.add(rset.getString("basepre")+rset.getString("a0100"));
					}else if("2".equals(infor_type)){
						objlist.add(rset.getString("b0110"));
					}else if("3".equals(infor_type)){
						objlist.add(rset.getString("e01a1"));
					}else{
						objlist.add(rset.getString("basepre")+rset.getString("a0100"));
					}

				}
			}
			else if ("3".equals(flag))//选中人员
			{
				StringBuffer buf=new StringBuffer();
				if(!"0".equals(task_id))
				{
					if("1".equals(infor_type)){
						buf.append("select basepre,a0100,ins_id from ");
					}else if("2".equals(infor_type)){
						buf.append("select b0110,ins_id from ");
					}else if("3".equals(infor_type)){
						buf.append("select e01a1,ins_id from ");
					}else{
						buf.append("select basepre,a0100,ins_id from ");
					}
					buf.append(tableName);


					buf.append(" where 1=1  ");
					buf.append(" and exists (select null from t_wf_task_objlink where "+tableName+".seqnum=t_wf_task_objlink.seqnum and "+tableName+".ins_id=t_wf_task_objlink.ins_id  ");
					if("1".equals(moduleId)||"2".equals(moduleId)||"3".equals(moduleId)){//待办，我的申请，我的已办
						buf.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");
					}
					if(frontProperty.isBatchApprove())
					{
						//buf.append(" where submitflag=1 and  ins_id in (");
						buf.append(" and submitflag=1 and task_id in (");
						for(int i=0;i<tasklist.size();i++)
						{
							if(i!=0) {
								buf.append(",");
							}
							buf.append(tasklist.get(i));
						}
						buf.append(")");
					}
					else
					{
						//buf.append(" where submitflag=1 and ins_id=");
						if(!"0".equals(task_id))
						{
							buf.append(" and submitflag=1 and task_id=");
							buf.append(task_id);
							//buf.append(")");
						}
					}
					if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
						buf.append(" and ( "+Sql_switcher.isnull("state","0")+"=3 ) ) ");
					}else {
						buf.append(" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ) ");
					}
				}

				else
				{
					if("1".equals(infor_type)){
						buf.append("select basepre,a0100  from ");
					}else if("2".equals(infor_type)){
						buf.append("select b0110 from ");
					}else if("3".equals(infor_type)){
						buf.append("select e01a1 from ");
					}else{
						buf.append("select basepre,a0100  from ");
					}
					buf.append(tableName);
					buf.append(" where submitflag=1");
				}
				buf.append(this.getOrderBy(tabId));
				ContentDAO dao=new ContentDAO(this.conn);
				rset=dao.search(buf.toString());
				/**求每个对应的实例*/
				inslist=new ArrayList();
				while(rset.next())
				{
					if(!"0".equals(task_id)) {
						inslist.add(rset.getString("ins_id"));
					} else {
						inslist.add("0");
					}
					if("1".equals(infor_type)){
						objlist.add(rset.getString("basepre")+rset.getString("a0100"));
					}else if("2".equals(infor_type)){
						objlist.add(rset.getString("b0110"));
					}else if("3".equals(infor_type)){
						objlist.add(rset.getString("e01a1"));
					}else{
						objlist.add(rset.getString("basepre")+rset.getString("a0100"));
					}
				}
			}
			else//当前人员
			{
				String cur_task_id= (String)paramMap.get("cur_task_id");
				cur_task_id = "0".equals(cur_task_id)?cur_task_id:PubFunc.decryption(cur_task_id);
				inslist=new ArrayList();
				String ins_id =utilBo.getInsId(cur_task_id);
				inslist.add(ins_id);
				String basepre="";
				String object_id =(String)paramMap.get("object_id");
//				object_id = PubFunc.decrypt(object_id);
				String a0100="";
				if("2".equals(infor_type)){
					a0100=object_id;
					objlist.add(a0100);
				}else if("3".equals(infor_type)){
					a0100=object_id;
					objlist.add(a0100);
				}else{
					int i = object_id.indexOf("`");
					if (i>0){
						basepre=object_id.substring(0,i);
						a0100=object_id.substring(i+1);
					}

					objlist.add(basepre+a0100);
				}
				if(objlist.size()==1&&(objlist.get(0)==null||((String)objlist.get(0)).trim().length()==0)) {
					objlist=new ArrayList();
				}
			}

			if(objlist.size()==0){
				String midString = "";
				if("1".equals(outtype))//word
				{
					midString = "WORD";
				} else//pdf
				{
					midString = "PDF";
				}
				if("1".equals(infor_type)){
					throw new GeneralException("请选择需要生成"+midString+"的人员!");
				}else if("2".equals(infor_type)){
					throw new GeneralException("请选择需要生成"+midString+"的机构!");
				}else if("3".equals(infor_type)){
					throw new GeneralException("请选择需要生成"+midString+"的岗位!");
				}
			}

			OutWordBo owbo = new OutWordBo(this.conn,this.userView,Integer.parseInt(tabId),tasklist.get(0).toString());
			if (frontProperty.isSelfApply()){
				owbo.setSelfApply(true);
			}
			String filename=null;
			owbo.setSigntype(signatureType);
			owbo.setNoshow_pageno(noShowPageNo);
			owbo.getParamBo().setReturnFlag(returnFlag);
			owbo.setDowntype(downtype);
			owbo.setOuttype(outtype);
			owbo.setShow_pageno(pageno);
			owbo.setModule_id(moduleId);
			if(taskid_validate!=null&&!"".equals(taskid_validate)&&task_id.equals(taskid_validate.split("_")[0])) {
				owbo.getParamBo().setNeedJudgPre("0");
			}

			filename=owbo.outword(objlist,1,inslist);
//			filename=PubFunc.encrypt(System.getProperty("java.io.tmpdir")+File.separator+filename);
			filename=PubFunc.encrypt(filename);
			returnData.put("return_code", "success");
			returnData.put("path", filename);
		}
		catch(Exception ex)
		{
			returnData.put("return_msg", ex.getMessage());
			returnData.put("return_code", "error");
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			PubFunc.closeDbObj(rset);
		}

        return returnData;

	}

	private String getOrderBy(String tabId) {
		String orderBy = " order by a0000";
		String subModuleId = "templet_"+tabId;
		TableDataConfigCache tableCacheList = (TableDataConfigCache) userView.getHm().get(subModuleId);
		if(tableCacheList!=null)
		{
			String sortSql = tableCacheList.getSortSql();
			HashMap customParamHM = tableCacheList.getCustomParamHM()==null?new HashMap():tableCacheList.getCustomParamHM();
			String property = customParamHM.get("property")==null?"":(String)customParamHM.get("property");
			String direction = customParamHM.get("direction")==null?"":(String)customParamHM.get("direction");
			if(StringUtils.isNotBlank(property)&&StringUtils.isNotBlank(direction)) {
				orderBy = " order by "+property+ " "+direction;
			}
			else if(sortSql!=null&&sortSql.trim().length()>0)
			{
				orderBy = sortSql;
			}
		}
		return orderBy;
	}

	private ArrayList getTaskList(String batch_task)throws GeneralException
	{
		String[] lists=StringUtils.split(batch_task,",");
		ArrayList list=new ArrayList();
		HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
		for(int i=0;i<lists.length;i++){
			String temptaskid=lists[i];
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
				if(templateMap!=null&&!templateMap.containsKey(temptaskid)){
					throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
				}
			 */
			list.add(lists[i]);
		}
		return list;

	}

	//-----删除附件--------------------------------------------------------------------------------------------
		public HashMap deleteAttachment(HashMap paramMap) throws Exception{
			String file_ids =(String)paramMap.get("fileIds");//删除返回的是用”,“分割的字符串
			String module_id =(String)paramMap.get("moduleId");
			HashMap result=new HashMap();
			RowSet rs=null;
			try {
				if(file_ids==null||file_ids.trim().length()==0){
					throw new Exception("上传文件失败");
				}
				StringBuffer fileIds = new StringBuffer();
				for(String file_id:file_ids.split(",")){
					/**基于安全平台改造,将加密的文件Id解密回来**/
					if(file_id!=null&&file_id.trim().length()>0){
//					file_id = PubFunc.decrypt(SafeCode.decode(file_id));
						fileIds.append(",'");
						fileIds.append(file_id);
						fileIds.append("'");
					}
				}
				ContentDAO dao = new ContentDAO(this.conn);
				String username=this.userView.getUserName();
				if("9".equalsIgnoreCase(module_id)&&this.userView.getStatus()==0&&StringUtils.isNotBlank(this.userView.getDbname())&&StringUtils.isNotBlank(this.userView.getA0100())){
					DbNameBo db = new DbNameBo(this.conn);
					String loginNameField = db.getLogonUserNameField();
					String usernameSele="";
					if(StringUtils.isNotBlank(loginNameField)) {
						loginNameField = loginNameField.toLowerCase();
						String sql="select "+loginNameField+" as username from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"' ";
						rs=dao.search(sql);
						while(rs.next()){
							usernameSele=rs.getString("username");
						}
						if(StringUtils.isNotBlank(usernameSele)){
							username=usernameSele;
						}
					}
				}
				StringBuffer sb = new StringBuffer();
				sb.append("select filepath from t_wf_file where create_user='");
				sb.append(username);
				sb.append("' and filepath in('-1',");
				sb.append(fileIds.toString().substring(1));
				sb.append(")");
				rs = dao.search(sb.toString());
				while(rs.next()){
					String filePath = rs.getString("filepath");
					if(StringUtils.isNotBlank(filePath)){
						//区分文件来源 人事异动上传的文件真删除 否则假删除
						VfsFileEntity enty = VfsService.getFileEntity(filePath);
						if(enty.getModuleid().equals(VfsModulesEnum.RS.toString())) {
							//文件从人事异动表单提交归档的附件 filetag 标记为YG的认为是已归档的附件 不删除
							if(StringUtils.isEmpty(enty.getFiletag())||!enty.getFiletag().equals(VfsModulesEnum.YG.toString())) {
								VfsService.deleteFile(this.userView.getUserName(), filePath);
							}
						}
					}
				}
				sb.setLength(0);
				sb.append("update  t_wf_file set state=1  where create_user='");//将真删除改为假删除，打上state=1标识删除
				sb.append(username);
				sb.append("' and filepath in('-1',");
				sb.append(fileIds.toString().substring(1));
				sb.append(")");
				dao.update(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException(e.getMessage());
			}finally{
				PubFunc.closeDbObj(rs);
			}
			return result;
		}
	//-----保存附件---------------------------------------------------------------------------------------------
	public HashMap saveAttachment(HashMap paramMap) throws Exception{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet=null;
		HashMap result=new HashMap();
		try {
			String tabId = (String) paramMap.get("tabId");
			String ins_id = (String) paramMap.get("ins_id");
			String infor_type = (String) paramMap.get("infor_type");
			String taskId = (String) paramMap.get("taskId");
			//VFS fileid
			String fileId = (String) paramMap.get("fileid");
			//文件名 可通过fileid获取到附件信息
			String filename = (String) paramMap.get("filename");
			String object_id = (String)paramMap.get("object_id")==null?"":(String)paramMap.get("object_id");
			String moduleId = (String)paramMap.get("moduleId")==null?"":(String)paramMap.get("moduleId");
			//附件类型 =0公共附件 =1 个人附件
			String attachmenttype = (String) paramMap.get("attachmenttype");
			//文件类型
			String filetype=(String) paramMap.get("filetype");

			String create_user= userView.getUserName();
			if(StringUtils.isNotBlank(moduleId)){//如果是通过自助申请，且是业务用户关联自助用户，就把上传附件人的帐号填入自助的帐号，否则用微信打开看不到。
				if("9".equalsIgnoreCase(moduleId)&&this.userView.getStatus()==0&&StringUtils.isNotBlank(this.userView.getA0100())&&StringUtils.isNotBlank(this.userView.getDbname())){
					String username="";
					DbNameBo db = new DbNameBo(this.conn);
					String loginNameField = db.getLogonUserNameField();
					if(StringUtils.isNotBlank(loginNameField)) {
						loginNameField = loginNameField.toLowerCase();
						String sql="select "+loginNameField+" as username from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"' ";
						rowSet=dao.search(sql);
						while(rowSet.next()){
							username=rowSet.getString("username");
						}
						if(StringUtils.isNotBlank(username)){
							create_user=username;
						}
					}
				}
			}
			VfsFileEntity vfsFileEntity =null;
			try {
	        	vfsFileEntity = VfsService.getFileEntity(fileId);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if(vfsFileEntity==null){
				throw new GeneralException("上传文件失败");
			}
			//获取文件名，如sutemplate_4 〔24〕.xls
			filename=vfsFileEntity.getName();

			AttachmentBo attachmentBo = new AttachmentBo(userView, this.conn,tabId);
			String[] object_id_Arr = object_id.split("`");
			String basepre = null;
			String objectid = null; //a0100|b0110|e01a1
			if("1".equals(infor_type)){
				basepre = object_id_Arr[0];
				objectid = object_id_Arr[1]; //a0100|b0110|e01a1
			}else{
				objectid = object_id;
			}
			RecordVo vo = new RecordVo("t_wf_file");
			vo.setString("ins_id", ins_id);
			vo.setString("tabid", tabId);
			//附件类型 =0公共附件 =1 个人附件
			vo.setString("attachmenttype", attachmenttype);
			vo.setString("objectid", objectid);
			if ("1".equals(infor_type)) {
				vo.setString("basepre", basepre);
			}
			else {
				vo.setString("basepre", null);
			}
			vo.setString("filepath",fileId);//保存相对路径
			String fileid = "0";
			List<Map<String,String>> medialist = this.getMediasortList("1",taskId,false,true);
			for(int i=0;i<medialist.size();i++) {
				Map<String,String> map = medialist.get(i);
				String value = map.get("value");
				if(value.equals(filetype)) {
					fileid = map.get("id");
					break;
				}
			}
			vo.setString("filetype", fileid);
			String id = attachmentBo.getMaxEitId(this.conn);
			if (id == null){
				throw new GeneralException("获取附件表的序号失败，请联系管理员！");
			}
			vo.setString("name", filename.substring(0,filename.lastIndexOf(".")));
			vo.setString("file_id", id);
			vo.setString("ext", filename.substring(filename.lastIndexOf("."),filename.length()));
			String cur_date = PubFunc.getStringDate("yyyy-MM-dd HH.mm.ss");
			Date cur_d = DateUtils.getDate(cur_date, "yyyy-MM-dd HH.mm.ss");
			vo.setString("create_user", create_user);
			vo.setString("fullname", userView.getUserFullName());
			vo.setDate("create_time", cur_d);
			vo.setString("i9999", "-1");
			vo.setInt("state", 0);
			dao.addValueObject(vo);
			result.put("return_msg", "");
			result.put("return_code", "success");
		} finally{
			PubFunc.closeDbObj(rowSet);
		}
		return result;
	}

	//----------刷新代码类功能------------------------------------------------------------------------------
	/**
	 *
	 * @param paramMap
	 * { "tabId":"1"             //模板ID
		 ,"taskId":"20"           // 待办任务号（发起任务|通知单 0）
		 ,"ins_id":"102"          //实例ID（发起任务|通知单 0）
		 ,"moduleId":"9"    //模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审 12、证照管理
		 ,"basepre":"Usr"     //模板数据ID，人员：库前缀
		  ,"objectId":"Usr`00000001" //模板数据ID，人员：库前缀+A0100
		                                   单位|岗位：B0100|E01A1
		 ,”isSysData”:false   //true 为整体刷新，false 为只刷新子集数据
		                      //整体刷新功能，需要调用下获取表单数据方法，不再考虑回传数据。
		 ,"infor_type":"1"//1 人事模板  2单位模板 3岗位模板
         ,”columnName”:”t_A19_2”  //要刷新的按过去数据
		}
	 * @return:返回内容： key值为return_code ：success 表示返回成功 ，可获取form：表结构，data值内容. 否则返回报错信息
	 * {return_msg=, return_code=success, refSubData={t_azh_1`t_azh_1=[{CZH02=04, record_key_id=su1589450181476528, CZH05=1986.07.01, CZH06=吉林工业大学, CZH03=0408, hisEdit=1, state=, canEdit=true, serial_id=0, CZH05_D=18, I9999=1, verify_status=, isHaveChange=true, postil_username=su, postil_msg=}, {CZH02=03, record_key_id=su15894501814761782, CZH05=1989.06.26, CZH06=吉林工业大学, CZH03=0308, hisEdit=1, state=, canEdit=true, serial_id=1, CZH05_D=18, I9999=2, verify_status=, isHaveChange=true, postil_username=su, postil_msg=}]}}
	 */
	public HashMap syncSubsetInfo(HashMap paramMap) throws Exception{
		HashMap returnData = new HashMap();
		try{
			String tab_id = (String)paramMap.get("tabId");
			tab_id=tab_id!=null&&tab_id.trim().length()>0?tab_id:"";
			String infor_type = (String)paramMap.get("infor_type");
			infor_type = infor_type!=null&&infor_type.trim().length()>0?infor_type:"";
			String id = (String)paramMap.get("objectId");
			String module_id = (String)paramMap.get("moduleId");
			String task_id = (String)paramMap.get("taskId");
			//true 为整体刷新，false 为只刷新子集数据
			Boolean isSysData =(Boolean)paramMap.get("isSysData");
			if(isSysData!=null&&isSysData)
			{
				TemplateBo templateBo=new TemplateBo(this.conn,this.userView,Integer.parseInt(tab_id));
				templateBo.setModuleId(module_id);
				if (task_id.contains(",")){
					String[] strArr= task_id.split(",");
					task_id="";
					for (int i=0;i<strArr.length;i++){
						String tmp = strArr[i];
						if ("".equals(tmp)){
							continue;
						}
						String _value= tmp;
						if ("".equals(task_id)){
							task_id=_value;
						}
						else {
							task_id=task_id+","+_value;
						}
					}
				}

			    templateBo.setTaskId(task_id);
				templateBo.syncDataFromArchive();
			}
			else
			{
				String columnName = (String)paramMap.get("columnName");
				if(StringUtils.isBlank(id)){
					throw new  Exception("当前人员信息不能为空");
				}
				ArrayList resultlist = new ArrayList();
		        TemplateBo templateBo=new TemplateBo(this.conn,this.userView,Integer.parseInt(tab_id));
		        templateBo.setModuleId(module_id);
		        templateBo.setTaskId(task_id);
				if("1".equals(infor_type)){//人事
					ArrayList a0100s = new ArrayList();//人员编号
					String a0100 = id.split("`")[1];
					String basepre = id.split("`")[0];
					a0100s.add(a0100);
					if(a0100s.size()>0){
						resultlist=templateBo.refDataFromArchive(a0100s, basepre,columnName);//按人员库前缀刷新数据
					}

				}else {//单位、岗位
					ArrayList a0100s = new ArrayList();//单位、岗位编号
					a0100s.add(id);
					if("2".equals(infor_type)){
						resultlist=templateBo.refDataFromArchive(a0100s, "B",columnName);//单位
					}else if("3".equals(infor_type)){
						resultlist=templateBo.refDataFromArchive(a0100s, "K",columnName);//岗位
					}
				}
				getRecords(resultlist);
				JsonObject subsetJson = new JsonObject();
			    Gson gson = new Gson();
		        String res = gson.toJson(resultlist);
			    JsonArray jsonArray = new JsonParser().parse(res).getAsJsonArray();
			    subsetJson.add(columnName+"`"+columnName, jsonArray);
			    returnData.put("refSubData", gson.fromJson(subsetJson, Map.class));
			}
		} catch (Exception e) {
			throw new GeneralException(e.getMessage());
		}
		return returnData;
	}
	/**
	 * 得到返回前台的数据
	 * @param subsetlist[{CZH05_D=18, CZH02=04`学士, record_key_id=su159178703626266, I9999=1, CZH05=2002.07.01, CZH06=东北师范大学, CZH03=04`学士, hisEdit=1, isHaveChange=false, canEdit=true}, {CZH05_D=18, CZH02=03`硕士, record_key_id=su15917870362626790, I9999=2, CZH05=2007.01.15, CZH06=首都师范大学, CZH03=0305`文学硕士学位, hisEdit=1, isHaveChange=false, canEdit=true}]
	 * @return
	 * @throws Exception
	 */
    public ArrayList getRecords(ArrayList subsetlist) throws Exception {
        Gson gson = new Gson();
        for(int j=0;j<subsetlist.size();j++){
        	//获取数据
        	HashMap subsetMap=(HashMap) subsetlist.get(j);
        	for (String key:(Set<String>)subsetMap.keySet()) {
        		if("attach".equalsIgnoreCase(key)){
        			ArrayList attachList = new ArrayList();
        			String value=(String)subsetMap.get(key);
        			if(value.length()>0) {
                        String values [] = value.split(",");
                      //filename+"|"+path+"|"+srcfilename+"|"+fileSizeString+"|"+id+"|"+m+"|"+"type:"+filetype ;
                        for(int m=0;m<values.length;m++) {
                            String valuearr []= values[m].split("\\|");
                            if(valuearr.length<7) {
                                continue;
                            }
                            HashMap valueMap = new HashMap();
                            valueMap.put("fileId", PubFunc.encrypt(valuearr[0]));
                            valueMap.put("path", valuearr[1]);
                            valueMap.put("name", valuearr[2]);
                            valueMap.put("size", valuearr[3]);
                            valueMap.put("fromhistory", valuearr[4]);
                            valueMap.put("index", valuearr[5]);
                            if(StringUtils.isEmpty(valuearr[6])) {
                                valueMap.put("fileType", "");
                            }else {
                                valueMap.put("fileType", valuearr[6].split(":",-1)[1]);
                            }
                          //添加文件绝对路径 供前端下载调用
                            valueMap.put("absolutepath",PubFunc.encrypt(valuearr[1]));
                            attachList.add(valueMap);
                        }
                    }
                    String _value = gson.toJson(attachList);
                    subsetMap.put("attach", _value);
                }
        		FieldItem item=DataDictionary.getFieldItem(key);
        		if(item!=null&&StringUtils.isNotBlank(item.getCodesetid())&&!"0".equals(item.getCodesetid())){
        			String val=(String)subsetMap.get(key);
        			subsetMap.put(key, StringUtils.isBlank(val)?"":val.split("`")[0]);
        		}
        	}
        	subsetMap.put("serial_id", j+"");
        	subsetMap.put("state", "");
        	subsetMap.put("verify_status", "");
        	subsetMap.put("postil_msg", "");
        	subsetMap.put("postil_username", this.userView.getUserName());
        }
        return subsetlist;
    }

	//----------获取代码类------------------------------------------------------------------------------------
	/**
	 * HashMap paramBean=new HashMap();
			//模板号
			paramBean.put("codesetid", codesetid);
			//要展开的节点id（codeitemid）
			paramBean.put("node", nodeid);
			//
			paramBean.put("parentid", parentid);
			//add by xiegh on date20180109 是否隐藏提示信息
			paramBean.put("isHideTip", isHideTip);
			//该字段可以为空，vorg 支持虚拟机构查询
			paramBean.put("vorg", vorg);
			//该字段可以为空，是否添加多选框
			paramBean.put("multiple", multiple);
			//该字段可以为空，是否显示多层级等部门
			paramBean.put("showLevelDept", showLevelDept);
			//该字段可以为空，要展开的节点id是否选中
			paramBean.put("checkroot", checkroot);
			//该字段可以为空，是否需要加密
			paramBean.put("isencrypt", isencrypt);
			//该字段可以为空，是否展开，不设置为null=false
			paramBean.put("expandTop", "false".equals(expandTop));
			//该字段可以为空，选中的codeid  格式如D75YkeJaIHkPAATTP3HJDPAATTP`QFuwy1WrBD0PAATTP3HJDPAATTP 其中codeid是加密的
			paramBean.put("checkedcodeids", checkedcodeids);
			//该字段可以为空，是否设置“进末级代码项可选”
			paramBean.put("onlySelectCodeset", onlySelectCodeset);
			//该字段可以为空，只针对部门   层级显示
			paramBean.put("isShowLayer", isShowLayer+"");
			//该字段可以为空，过滤类型  如果codesetid 为机构（UN、UM、@K）
			//         0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
			//         默认值为1
			//  如果是普通代码类
			//         0：不过滤，其他任意值（包括""）代表需要过滤（有效或在有效日期），默认过滤
			paramBean.put("ctrltype", ctrltype);
			//模块号
			paramBean.put("nmodule", nmodule);
			//多个代码类  可以为null
			paramBean.put("codesetList", new ArrayList());
			//返回内容： key值为return_code ：success 表示返回成功 ，可获取相关代码，否则返回报错信息
			HashMap returnData=templateSelfServicePlatformBo.getCodeTree(paramBean);
	 * @param paramMap
	 * @return
	 */
	public HashMap getCodeTree(HashMap paramMap){
		HashMap returnData = new HashMap();
		String codesetid = (String)paramMap.get("codesetid");
		String nodeid = "ALL"; // 要展开的节点id（codeitemid）
		String parentid = (String)paramMap.get("parentid");
		parentid = parentid==null?"":parentid;
		Boolean isHideTip = (Boolean)paramMap.get("isHideTip");//add by xiegh on date20180109 是否隐藏提示信息
		// 33945 linbz 20180113 该参数缺少默认值 为null在做处理时报错
		//true:隐藏提示 false不隐藏
		isHideTip = null==isHideTip?false:isHideTip;
		String currentid = (String)paramMap.get("currentid");
		currentid = currentid==null?"":currentid;
		boolean vorg = false;
		GetCodeTreeBo getCodeTreeBo=new GetCodeTreeBo(conn, userView);
		if(paramMap.containsKey("vorg")) {
			vorg = (Boolean)paramMap.get("vorg");
		}
		getCodeTreeBo.setLoadVorg(vorg);

		boolean multiple = false;
		if(paramMap.containsKey("multiple")) {
			multiple = (Boolean)paramMap.get("multiple");
		}

		//xus 18/3/14 微信端部门是否多层级显示
		boolean showLevelDept = false;
		if(paramMap.containsKey("showLevelDept")) {
			showLevelDept = (Boolean)paramMap.get("showLevelDept");
		}

		boolean checkroot = false;
		if(paramMap.containsKey("checkroot")) {
			checkroot = (Boolean)paramMap.get("checkroot");
		}
		boolean isencrypt = false;
		if(paramMap.containsKey("isencrypt")) {
			isencrypt = (Boolean)paramMap.get("isencrypt");//是否需要加密
		}

		boolean expandTops = false;
		if(paramMap.containsKey("expandTop")) {
			expandTops = (Boolean)paramMap.get("expandTop");
		}
		String expandTop = Boolean.toString(expandTops);
		expandTop = "true".equals(expandTop)?"true":"false"; //是否展开，不设置为null=false

		String checkedcodeids = "";
		if(paramMap.containsKey("checkedcodeids"))//选中的codeid  格式如D75YkeJaIHkPAATTP3HJDPAATTP`QFuwy1WrBD0PAATTP3HJDPAATTP 其中codeid是加密的
		{
			checkedcodeids = (String)paramMap.get("checkedcodeids");
		}
		if(StringUtils.isNotBlank(checkedcodeids)){//解密
			String idsArray [] = checkedcodeids.split("`");
			String checkedids = "`";
			for (int i=0;i<idsArray.length;i++) {
				String org = PubFunc.decrypt(SafeCode.decode(idsArray[i]));
				checkedids+=org+"`";
			}
			checkedcodeids = checkedids;
		}
//		boolean onlySelectCodeset = getCodeTreeBo.getSelectFlag(codesetid);//add by xiegh on date 20180319 是否设置“进末级代码项可选”
		boolean onlySelectCodeset = true;
		if(("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)) && paramMap.containsKey("onlySelectCodeset")) {
			onlySelectCodeset = (Boolean)paramMap.get("onlySelectCodeset");
		}

		int isShowLayer = 0;
		if(paramMap.containsKey("isShowLayer")) {
			isShowLayer = (String)paramMap.get("isShowLayer")==null?0:Integer.parseInt((String)paramMap.get("isShowLayer"));
		}

		ArrayList treeItems = new ArrayList();
		try{
			nodeid = nodeid.replaceAll("root", "ALL");

			//如果加密，解密
			if(isencrypt==true&&!"ALL".equals(nodeid)){
				nodeid = PubFunc.decrypt(PubFunc.hireKeyWord_filter_reback(nodeid));
			}

			/**
			 * ctrltype
			 * 过滤类型  如果codesetid 为机构（UN、UM、@K）
			 *         0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
			 *         默认值为1
			 *  如果是普通代码类
			 *         0：不过滤，其他任意值（包括""）代表需要过滤（有效或在有效日期），默认过滤
			 */
			String ctrltype = (String)paramMap.get("ctrltype");
			ctrltype = ctrltype==null || ctrltype.length()<1?"1":ctrltype;

			String nmodule = (String)paramMap.get("nmodule");// 模块号

			//正常加载代码
			boolean doChecked = false;
			if(checkroot) {
				doChecked = true;
			}

			JSONArray codesetList = (JSONArray)paramMap.get("codesetList");
			if(codesetList==null||codesetList.isEmpty()) {
				if(StringUtils.isNotBlank(codesetid)){
					codesetList=new JSONArray();
					codesetList.add(codesetid);
				}else{
					returnData.put("return_msg", "代码类不能为空");
					returnData.put("return_code", "error");
					return returnData;
				}
			}

			if(!codesetList.isEmpty()) {
				for(int i = 0; i < codesetList.size(); i++) {
					String codesetid_ = (String) codesetList.get(i);
					if("UN".equalsIgnoreCase(codesetid_) || "UM".equalsIgnoreCase(codesetid_) || "@K".equalsIgnoreCase(codesetid_)) {
						treeItems = getCodeTreeBo.getCodeListParams(expandTop,codesetid_, nodeid,parentid,
								ctrltype, nmodule, multiple,doChecked,onlySelectCodeset,isShowLayer,isHideTip,showLevelDept,checkedcodeids);
					}else {
						treeItems=getCodeTreeBo.fastGetCodeItems(codesetid_);
					}
					returnData.put(codesetid_, new Gson().toJson(treeItems));
				}
			}

			returnData.put("return_code", "success");
		}catch(Exception e){
			e.printStackTrace();
			returnData.put("return_code", "error");
    		returnData.put("return_msg", e.getMessage());
		}
		return returnData;

	}

	//----------获取左侧列表-----------------------------------------------------------------------------------
	/**
	 * 功能：获取左侧列表  支持分页查询。
	 * @param tabId
	 * @param insId
	 * @param taskId
	 * @param module_id
	 * @param pageSize ：页数
	 * @param pageNum：页码
	 * @param searchName 根据姓名模糊查询
	 * @return
	 * @throws GeneralException
	 */
	public Map getApplyTableData(String tabId, String insId, String taskId,String module_id,int pageSize,int pageNum,String searchName,String approveFlag,String returnFlag) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        Map tableData = new HashMap();
        List tableList = new ArrayList();
        try {
        	 /** 区分报审、报备、加签
             * 1：报审 2：加签  3 报备
            */
            if(StringUtils.isEmpty(approveFlag)){
            	approveFlag="1";
            }
            TemplateDataBo dataBo = new TemplateDataBo(this.conn,
                    this.userView, Integer.parseInt(tabId));
            String dataTabName = dataBo.getUtilBo().getTableName(module_id,
                    Integer.valueOf(tabId), taskId);
            //获取排序语句
            StringBuffer orderBy = new StringBuffer();
            if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3)
                    && (dataBo.getParamBo().getOperationType() == 8 || dataBo
                    .getParamBo().getOperationType() == 9)) {
                String key = "b0110";
                if (dataBo.getParamBo().getInfor_type() == 3){
                    key = "e01a1";
                }
                orderBy.append("  order by "
                        + Sql_switcher.isnull("to_id", "100000000")
                        + ",case when " + key
                        + "=to_id then 100000000 else a0000 end asc ");
            } else{
                orderBy.append(" order by a0000");
            }
            String sql = dataBo.getSql(module_id, returnFlag, approveFlag,
                    dataTabName, taskId, "", "", "" );
            String nameColumn = "";
            if (dataBo.getParamBo().getInfor_type() == 2
                    || dataBo.getParamBo().getInfor_type() == 3) {//单位名称
                if (dataBo.getParamBo().getOperationType() == 5) {
                    nameColumn = "codeitemdesc_2";
                } else {
                    nameColumn = "codeitemdesc_1";
                }
            }
            boolean isPersonTemplate = false;
            if (dataBo.getParamBo().getInfor_type() == 1) {
                isPersonTemplate =true;
                DbWizard dbWizard = new DbWizard(this.conn);

                if (dataBo.getParamBo().getOperationType() == 0) {//人员调入型
                    if (dbWizard.isExistField(dataTabName, "a0101_2", false)) {
                        nameColumn = "a0101_2";
                    }
                } else {
                    nameColumn = "a0101_1";
                }
            }
            if(StringUtils.isNotEmpty(searchName)){
                sql+= " and "+nameColumn+" like '%"+searchName+"%'";
            }
            String fullSql = sql + " "+orderBy;
            rs = dao.search(fullSql,pageSize,pageNum);
            while (rs.next()){
                Map rowMap = new HashMap();
                if(!StringUtils.equalsIgnoreCase("0",taskId)){
                    String task_id = rs.getString("realtask_id");
                    rowMap.put("task_id",task_id);
                }
                String submitflag = rs.getString("submitflag2");
                String objectid = rs.getString("objectid");
//                if(isPersonTemplate){
//                    objectid = objectid.replaceAll("`","");
//                }
                //如果是单位模板 则是b0100 如果是岗位模板 则是e01a1
//                String objectid_noencrypt = rs.getString("objectid_noencrypt");
                String name = rs.getString(nameColumn);
                rowMap.put("submitflag",submitflag);
                rowMap.put("objectid",objectid);
                rowMap.put("objectid_encrypt",PubFunc.encrypt(objectid));
                rowMap.put("name",name);
                tableList.add(rowMap);
            }
            //获取总记录数t
            sql = "select count(*) from ("+sql+") t";
            rs =dao.search(sql);
            int totalCount = 0;
            if(rs.next()){
                totalCount = rs.getInt(1);
            }
            tableData.put("totalCount",totalCount);
            tableData.put("tableList",tableList);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return tableData;
    }
	//----获取模板数据操作---------------------------------------------------------------------------------

	/**
	 *
	 * @param paramMap
	 * { "tabId":"1"             //模板ID
		 ,"isEdit":"1"            // 1：数据可编辑  0：只读（浏览已批的任务）
		 ,"taskId":"20"           // 待办任务号（发起任务|通知单 为空值）
		 ,"ins_id":"102"          //实例ID（发起任务|通知单 为空值）
		 ,"moduleId":"9"    //模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审 12、证照管理
		 ,"fromMessage":"1"     //1：来自通知单待办  0：不是
		 ,"objectId":"Usr`00000001" //模板数据ID，人员：库前缀+A0100
		                                   单位|岗位：B0100|E01A1
		 ,”approveFlag”:”mobile”   //1：报审 2：加签  3 报备
		 ,"needForm":"0"//需要表单结构信息，即map中会有structJson 。 为空或者0时，传值结构信息。
         ,”pageId”:”1”  //设置当前pageid
		}
	 * @return:返回内容： key值为return_code ：success 表示返回成功 ，可获取form：表结构，data值内容. 否则返回报错信息
	 * {form={"module":"1","pages":[{.....}]
data=[{"a0101_1`a0101_1":"王鹤","a0107_1`a0107_1":"1","a0121_1`a0121_1":"01","ext`ext":".jpg","photo`photo":"blank","a0111_1`a0111_1":"1980-04-05","a2205_1`a2205_1":"01","a1020_2`a1020_2":"","c100c_1`c100c_1":"2007-12-17","a0704_1`a0704_1":"团委书记","a0141_1`a0141_1":"2002-07-01","c018g_1`c018g_1":"2002-07-01","c018p_1`c018p_1":"08","c2704_1`c2704_1":"0202","c0602_1`c0602_1":"","a62af_2`a62af_2":"","a62ae_2`a62ae_2":"19","a1926_2`a1926_2":"0","yk244`yk244":"2007-12-17","yk245`yk245":"2020-06-09","aacaa_2`aacaa_2":"","aacab_2`aacab_2":"","aacac_2`aacac_2":"","t_azh_1`t_azh_1":[{"serial_id":"0","record_key_id":"su15909812635774895","i9999":"1","ishavechange":true,"state":null,"readonly":false,"verify_status":"0","CZH05":"2002.07.01","CZH02":"学士||04","CZH03":"学士||04","CZH06":"东北师范大学"},{"serial_id":"1","record_key_id":"su15909812635772400","i9999":"2","ishavechange":true,"state":null,"readonly":false,"verify_status":"0","CZH05":"2007.01.15","CZH02":"硕士||03","CZH03":"文学硕士学位||0305","CZH06":"首都师范大学"}],"t_a04_1`t_a04_1":[{"serial_id":"0","record_key_id":"su15909812637523185","i9999":"1","ishavechange":true,"state":null,"readonly":false,"verify_status":"0","A0430":"2002.07.01","C0403":"大学本科毕业生||04","A0435":"东北师范大学","A0444":"美术教育","A0425":"4"}],"basepre`basepre":"Usr","a0100`a0100":"00001563","signature`signature":"","tabId":"11","taskId":"0","insId":"0","moduleId":"1","pageId":"2"}], return_code=success}
	 */
    public  HashMap  getTemplateInfo (HashMap paramMap)
    {
    	String errorMsg="";
    	String ins_id=(String)paramMap.get("ins_id");   //实例ID
    	ins_id=StringUtils.isEmpty(ins_id)?"0":ins_id;
    	paramMap.put("ins_id", ins_id);

    	String taskId=(String)paramMap.get("taskId");  // 待办任务号（发起任务|通知单 为空值）
    	taskId=StringUtils.isEmpty(taskId)?"0":taskId;
    	paramMap.put("taskId", taskId);
		String return_flag = (String) paramMap.get("return_flag");
    	String moduleId=(String)paramMap.get("moduleId");
    	String tabId=(String)paramMap.get("tabId");
    	 /** 区分报审、报备、加签
         * 1：报审 2：加签  3 报备
        */
        String approveFlag=(String)paramMap.get("approveFlag");
        if(StringUtils.isEmpty(approveFlag)){
        	approveFlag="1";
        }
		HashMap returnData = new HashMap();
		RowSet rowset=null;
    	try
    	{
    		TemplateFrontProperty frontProperty=new TemplateFrontProperty(new HashMap());
    		frontProperty.setModuleId(moduleId);
    		frontProperty.setTabId(tabId);
    		frontProperty.setTaskId(taskId);
    		frontProperty.setApproveFlag(approveFlag);

    		TemplateParam tableParamBo=new TemplateParam(conn, this.userView,Integer.parseInt(tabId));
    		tableParamBo.setReturnFlag(return_flag);
    		//------初始化表数据开始--------------------------------------------------
    		if(!syncTabInfo){
    			autoSyscDataInfo(paramMap);
    		}
    		if(StringUtils.isEmpty(this.tableName)){
        		//获取表名
        		TemplateUtilBo utilBo = new TemplateUtilBo(this.conn,this.userView);
        		this.tableName=utilBo.getTableName(moduleId,Integer.parseInt(tabId), taskId);
        	}
    		//------初始化表数据结束--------------------------------------------------
    		String structJson = "";
    		String needForm=(String)paramMap.get("needForm");
    		TemplateSelfPlateFormCardBo cardBo = new TemplateSelfPlateFormCardBo(this.conn, this.userView, tableParamBo);

			String selfapply = "0";
			if (frontProperty.isSelfApply()) {
				selfapply = "1";
			}
			cardBo.setSelfApply(selfapply);
			cardBo.setApproveFlag(approveFlag);
			cardBo.setTask_id(taskId);
			cardBo.setCurTaskId(taskId.indexOf(",")!=-1?StringUtils.split(taskId, ",")[0]:taskId);
			//得到每页是否有读写权限
			ArrayList pageList = getPageList(Integer.parseInt(tabId), false, "",taskId);
			String curpageId = (String) paramMap.get("pageId");
			cardBo.setCurPageId(curpageId);
			if(pageList.size()>0&&StringUtils.isEmpty(curpageId)) {
				for(int i=0;i<pageList.size();i++) {
					TemplatePage pagebo = (TemplatePage) pageList.get(i);
					boolean isShow = pagebo.isShow();
					if(isShow) {
						curpageId = pagebo.getPageId()+"";
						break;
					}
				}
			}
			paramMap.put("pageId", curpageId);
			//获取审批意见指标
	        TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tabId),this.userView);
	        String optionField=tablebo.getOpinion_field();
	        paramMap.put("optionField", StringUtils.isEmpty(optionField)?"":optionField+"_2");
            if(needForm==null||"0".equals(needForm)) {
            	//获取前台使用指标类型指标模型。存储指标的各种值：类型、权限等
            	paramMap.put("fieldList", cardBo.getFieldList());
            	/**读取json字符串*/
        		DownAttachUtils attachUtils=new DownAttachUtils(userView, conn, tabId);
        		structJson=attachUtils.getHtmlJsoninfo();
        		if(StringUtils.isEmpty(structJson)){
        			/**
        			 * 功能优化：如果没有定义htmljson模板，从当前模板中生成标准htmljson字符串。
        			 */
        			TemplateHtmlJsonBo templateHtmlJsonBo=new TemplateHtmlJsonBo(Integer.valueOf(tabId), this.conn, this.userView);
        	        JSONObject htmlJson=templateHtmlJsonBo.getLayoutBySet();
        	        structJson=htmlJson.toString();
        			if(StringUtils.isEmpty(structJson)){
        				errorMsg="业务表单HTML布局文件未定义!";
        				throw new Exception(errorMsg);
        			}
        		}
                paramMap.put("structJson", structJson);
                paramMap.put("pageList", pageList);
                //将模板设计相关属性以及节点设置权限必填等添加到json数据上
                structJson = updateStructJson(paramMap);
            }
            String dataJson = "";

            //---获取当前模板数据信息----------------------------------------------------
            String returnFlag=(String)paramMap.get("returnFlag");
            String objectId=(String)paramMap.get("objectId");
            //过滤sql
            String filterStr=(String)paramMap.get("filterStr");
            //是否是被撤销的 终止的
            String isDelete=(String)paramMap.get("isDelete");


            if(StringUtils.isEmpty(objectId)){
            	//是否显示左侧人员列表 是否自助  当前模板类型
            	TemplateDataBo dataBo = new TemplateDataBo(this.conn,
            			this.userView, Integer.parseInt(tabId));
            	StringBuffer orderBy = new StringBuffer();
            	if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo
            			.getParamBo().getInfor_type() == 3)
            			&& (dataBo.getParamBo().getOperationType() == 8 || dataBo
            			.getParamBo().getOperationType() == 9)) {
            		String key = "b0110";
            		if (dataBo.getParamBo().getInfor_type() == 3) {
						key = "e01a1";
					}
            		orderBy.append("  order by "
            				+ Sql_switcher.isnull("to_id", "100000000")
            				+ ",case when " + key
            				+ "=to_id then 100000000 else a0000 end asc ");
            	} else {
					orderBy.append(" order by a0000");
				}
            	String sql = dataBo.getSql(moduleId, returnFlag, approveFlag,
            			tableName, taskId, StringUtils.isEmpty(objectId)?"":objectId.replace("`", ""), filterStr, isDelete);
            	//将sql中T.*替换成具体得字段
            	String replaceSql = "";
            	if(dataBo.getParamBo().getInfor_type() == 2) {
					replaceSql+="T.b0110,";
				} else if(dataBo.getParamBo().getInfor_type() == 3) {
					replaceSql+="T.e01a1,";
				} else if(dataBo.getParamBo().getInfor_type() == 1) {
					replaceSql+="T.basepre,T.a0100,";
				}
            	replaceSql+="T.a0000";
            	if(replaceSql.length()>0) {
            		sql = sql.replace("T.*", replaceSql);
            	}
            	sql+=orderBy;
            	//29235 linbz 增加选人控件不显示的人员参数
            	ContentDAO dao=new ContentDAO(this.conn);
            	rowset=dao.search(sql);
            	ArrayList objectslist = new ArrayList();
            	//29235 linbz 增加选人控件不显示的人员参数
            	if(dataBo.getParamBo().getInfor_type() == 1){
            		while(rowset.next()){
            			String nbase = rowset.getString("basepre");
            			String a0100 = rowset.getString("a0100");
            			if(StringUtils.isEmpty(objectId)){
            				objectId=nbase+"`"+a0100;
            			}
            			objectslist.add(PubFunc.encrypt(nbase+a0100));
            		}
            	}
            	returnData.put("objectslist", objectslist);
            }
			cardBo.setObjectId(objectId);
//------------------------获取当前page页的数据---------------------------------------------------------------------
            paramMap.put("fieldValueList", cardBo.getFieldValueList(ins_id,moduleId));
            //将单元格指标对应数据添加到json数据上
            dataJson = updateDataJson(paramMap);
            returnData.put("return_code", "success");
            returnData.put("form", structJson);
            returnData.put("data", dataJson);
    	}catch(Exception e){
    		e.printStackTrace();
    		returnData.put("return_code", "error");
    		returnData.put("return_msg", e.getMessage());
    	}finally{
    		PubFunc.closeDbObj(rowset);
    	}
    	return returnData;
    }
    /**
     * 功能：人事异动模板初始化，同步表数据
     * @param taskId
     * @param tabId
     * @param fillInfo
     * @param approveFlag 1：报审 2：加签  3 报备
     * @throws Exception
     * @throws GeneralException
     */
	public void autoSyscDataInfo(HashMap paramMap) throws Exception, GeneralException {
    	String taskId=(String)paramMap.get("taskId");  // 待办任务号（发起任务|通知单 为空值）
    	taskId=StringUtils.isEmpty(taskId)?"0":taskId;
    	paramMap.put("taskId", taskId);

    	String moduleId=(String)paramMap.get("moduleId");
    	String tabId=(String)paramMap.get("tabId");
    	String fillInfo = (String) this.userView.getHm().get("fillInfo");
    	 /** 区分报审、报备、加签
         * 1：报审 2：加签  3 报备
        */
        String approveFlag=(String)paramMap.get("approveFlag");
        if(StringUtils.isEmpty(approveFlag)){
        	approveFlag="1";
        }
		HashMap returnData = new HashMap();
		TemplateFrontProperty frontProperty=new TemplateFrontProperty(new HashMap());
		frontProperty.setModuleId(moduleId);
		frontProperty.setTabId(tabId);
		frontProperty.setTaskId(taskId);
		frontProperty.setApproveFlag(approveFlag);
		//获取表名
		TemplateUtilBo utilBo = new TemplateUtilBo(this.conn,this.userView);
		this.tableName=utilBo.getTableName(moduleId,Integer.parseInt(tabId), taskId);

		TemplateBo templateBo=new TemplateBo(this.conn,this.userView,Integer.parseInt(tabId));
		templateBo.setModuleId(moduleId);
		templateBo.setTaskId(taskId);

		TemplateParam tableParamBo=templateBo.getParamBo();
		String errorMsg="";
		if(tableParamBo.getTable_vo().getValues().size()==0)//liuyz bug32523  如果根据模版id不存在提示用户
		{
			errorMsg= "此模版不存在！";
			throw new Exception(errorMsg);
		}

		if("0".equals(taskId)){//起草状态判断是否有模板的资源权限
		}else{
			boolean ExistsTaskId = this.checkExistsTaskId(taskId);
			if(!ExistsTaskId){
				errorMsg="该单据已被撤回！";
				throw new Exception(errorMsg);
			}
		}

		//校验此单据是否处理过及被其他人锁定
		if ("1".equals(approveFlag)&& !"0".equals(taskId)/*&& !"1".equals(templateBo.isStartNode(taskId))*/){ // && !frontProperty.isBatchApprove()){
			String def_flow_self =tableParamBo.getDef_flow_self();
			if ("1".equals(def_flow_self)){
				String[] tasklist=StringUtils.split(taskId,",");
				for(int i=0;i<tasklist.length;i++){
					if (tableParamBo.isDef_flow_self(Integer.parseInt(tasklist[i]))){
						def_flow_self="2";
						break;
					}
				}
			}
			String errorInfo= templateBo.checkDealTaskInformation(taskId, def_flow_self);
			if (errorInfo.length()>0){//已处理过，置为不能审批
				throw new Exception(errorInfo);
			}
		}

		if("0".equalsIgnoreCase(taskId))
		{
		    if (frontProperty.isSelfApply()){//是否业务申请
		        if  ("".equals(this.userView.getA0100())){
		        	errorMsg="没有关联自助用户!";
					throw new Exception(errorMsg);
		        }
		        //创建临时表
		        templateBo.createTempTemplateTable("");
		        if("1".equals(fillInfo)){
		        	//删除当前日期之前的记录
		        	this.deleteFillInfo(tableName);
		        	//自动生成一条记录。
			        templateBo.autoAddRecord(tableName);
		        }else{
		        	// 从档案库中导入当前人的数据
			        ArrayList a0100list=new ArrayList();
			        a0100list.add(this.userView.getA0100());
		            templateBo.impDataFromArchive(a0100list,this.userView.getDbname());
		        }
		    }
		    else {
		        templateBo.createTempTemplateTable(this.userView.getUserName());
		        // 自动生成一条记录。
		        templateBo.autoAddRecord(tableName);
		        // 同步档案库数据
		        if("1".equals(tableParamBo.getAutosync_beforechg_item())) {
					templateBo.syncDataFromArchive();
				}
		    }
		}
		else
		{
			utilBo.getAllTemplateItem(Integer.valueOf(tabId));//在流程中校验指标是否存在或数据类型改变 lis 20160802
			// 同步审批表结构
		    templateBo.changeSpTableStrut();

		    if ("1".equals(tableParamBo.getAutosync_beforechg_item()) &&(!"0".equals(approveFlag)&&!"3".equals(approveFlag))){//需要同步档案库数据时才同步 //approveFlag=3代表是报备任务。
		        templateBo.syncDataFromArchive();
		    }
		    this.setReadFlag(taskId);//设置是否阅读
		}
		//同步成功
		syncTabInfo=true;
	}
	/**
     * 设置已读标识
     * @param taskid
     */
    private void setReadFlag(String taskid)
 	{
		if(taskid==null|| "".equals(taskid)|| "0".equals(taskid)) {
			return;
		}
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			int j=1;
			StringBuffer updateSql = new StringBuffer("update t_wf_task set bread=1 where bread<>1 and task_id in (-1");
			StringBuffer middleSql = new StringBuffer("");
			String[] taskidStr = taskid.split(",");
			for(int i=0;i<taskidStr.length;i++){
				String taskId = taskidStr[i];
				middleSql.append(",");
				middleSql.append(taskId);
				if(i==500*j){//每500条执行一次
					j++;
					middleSql.append(")");
					dao.update(updateSql.toString()+middleSql.toString());
					middleSql.setLength(0);
				}
			}
			if(middleSql.length()>0) {
				dao.update(updateSql.toString()+middleSql.toString()+" )");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
     * 修改模板对应结构json属性
     * @param paramMap  {structJson:'',tab_id:'',task_id:'',ins_id:'',procId:''}
     * @return
     */
    public String updateStructJson(HashMap paramMap) throws Exception {
        String tabId = (String) paramMap.get("tabId");
        String structJson = (String) paramMap.get("structJson");
        String taskId = (String) paramMap.get("taskId");
        String isEdit = (String) paramMap.get("isEdit");
        String optionField = (String) paramMap.get("optionField");//获取审批意见指标
      //得到每页是否有读写权限
        ArrayList pageList = (ArrayList) paramMap.get("pageList");
        if(pageList==null){
        	pageList = getPageList(Integer.parseInt(tabId), false, "",taskId);
        }
        boolean readOnly=false;//控制子集或者指标是否只读
        if(paramMap.containsKey("readOnly")){
            readOnly=(Boolean) paramMap.get("readOnly");
        }
        if("0".equals(isEdit)) {
            readOnly = true;
        }
        //更新json格式
        if(StringUtils.isBlank(structJson)) {
            structJson = "{}";
            return structJson;
        }
        //得到模板相关设置包括权限必填等
        ArrayList fieldStructList = (ArrayList) paramMap.get("fieldList");;
        Map<String,Object> approvedFileItem=null;
        boolean isreject=false;
        boolean editFormFlag=true;//表单编辑权限 默认为false
        //驳回到发起人 以及流程发起节点，不再考虑 表单编辑权限
        if("0".equals(taskId)||isreject) {
            editFormFlag=true;
        }
        JsonObject jsonObject = new JsonParser().parse(structJson).getAsJsonObject();
        JsonArray pageArray = jsonObject.getAsJsonArray("pages");
        boolean isAdd = false;
        for(int i=0;i<pageArray.size();i++) {
            JsonObject pageJson=pageArray.get(i).getAsJsonObject();
            JsonArray layout = pageJson.getAsJsonArray("layout");
            String pageId = pageJson.get("page_id").getAsString();
            for(int j=0;j<pageList.size();j++) {
                TemplatePage pagebo = (TemplatePage) pageList.get(j);
                if(Integer.parseInt(pageId)==pagebo.getPageId()) {//更新页签属性
                    pageJson.addProperty("hidden", !pagebo.isShow());
                    pageJson.addProperty("readonly",editFormFlag?pagebo.isShow():true);
                    //如果没有审批意见指标，审批意见栏不展示
                    if(pagebo.isShow()&&!isAdd) {
                        JsonObject collapseJson = new JsonObject();
                        JsonArray row_collapse=new JsonArray();
                        createCollapseEditor(row_collapse, "审批意见");
                        collapseJson.addProperty("horizontal_id", "y1");
                        collapseJson.addProperty("columns_num", 1+"");
                        collapseJson.addProperty("columns_width", "100%");
                        collapseJson.add("content", row_collapse);
                        layout.add(collapseJson);

                        JsonObject optionJson = new JsonObject();
                        JsonArray row_option=new JsonArray();
                        createOptionEditor(row_option, "opinionContent");
                        optionJson.addProperty("horizontal_id", "y2");
                        optionJson.addProperty("columns_num", 1+"");
                        optionJson.addProperty("columns_width", "100%");
                        optionJson.add("content", row_option);
                        layout.add(optionJson);
                        isAdd = true;
                    }
                    break;
                }
            }
            for(int j=0;j<layout.size();j++) {
                JsonObject layoutJson=layout.get(j).getAsJsonObject();
                JsonArray content = layoutJson.getAsJsonArray("content");
                for(int k=0;k<content.size();k++) {
                    JsonObject contentJson = content.get(k).getAsJsonObject();
                    String type = contentJson.get("type").getAsString();//类型
                    if("collapse".equals(type)) {
                        String title = contentJson.get("title").getAsString();
                        if("0".equals(taskId)&&"审批意见".equals(title)) {
                            contentJson.addProperty("hidden", true);
                        }
                        continue;
                    }
                    //当前对象为 分割线 或者描述信息时，是没有element_id 会报空指针
                    String elementId = contentJson.get("element_id")!=null?(contentJson.get("element_id").getAsString()):"";
                    if("0".equals(taskId)) {//起始节点
                        if("opinionContent".equalsIgnoreCase(elementId)) {//审批意见
                            contentJson.addProperty("hidden", true);
                            continue;
                        }
                    }
                    if(contentJson.get("relation_id")==null||contentJson.get("relation_id").isJsonNull()) {
                        continue;
                    }
                    String relationId = contentJson.get("relation_id").getAsString();
                    for(int m = 0;m<fieldStructList.size();m++) {
                        LazyDynaBean bean = (LazyDynaBean) fieldStructList.get(m);
                        String tableFieldName = (String) bean.get("tableFieldName");
                        String rwPriv = (String) bean.get("rwPriv");
                        String chgState = (String) bean.get("chgState");
                        String review = (String) bean.get("rwPriv");//指标 0 1 2 无 读 写
                        String flag = (String) bean.get("flag");
                        //String codeSetId = (String) bean.get("codeSetId");
                        //如果前台传递参数只读为true时 指标和子集结构 readonly和disable 都应该为true
                        if(readOnly){
                            chgState="1";
                            if("2".equals(review)) {
                                review="1";
                            }
                        }else {
                            if("V".equalsIgnoreCase(flag)||"F".equalsIgnoreCase(flag)) {
                                chgState = rwPriv;
                            }
                        }

                        if(elementId.equalsIgnoreCase(tableFieldName)&&relationId.equalsIgnoreCase(tableFieldName)) {//更新单元格指标属性
                            contentJson.addProperty("required", bean.getMap().containsKey("yneed")?(boolean)bean.get("yneed"):false);
                            contentJson.addProperty("readonly", editFormFlag?("1".equalsIgnoreCase(chgState)||("2".equalsIgnoreCase(chgState)&&!"2".equalsIgnoreCase(rwPriv))):true);
                            contentJson.addProperty("hidden", "0".equalsIgnoreCase(rwPriv));
                            contentJson.addProperty("disabled", "1".equalsIgnoreCase(chgState)||("2".equalsIgnoreCase(chgState)&&!"2".equalsIgnoreCase(rwPriv)));
                            if("table".equalsIgnoreCase(type)) {
                                SubSetDomain subDomain = (SubSetDomain) bean.get("subSetDomain");
                                if(subDomain==null){
                                	continue;
                                }
                                String allow_del_his = subDomain.getAllow_del_his();
                                contentJson.addProperty("allow_del_his","1".equals(allow_del_his));
                                JsonArray columns = contentJson.getAsJsonArray("columns");
                                String subXml = (String) bean.get("subXml");
                                SAXBuilder saxbuilder = new SAXBuilder();
                                StringReader reader = new StringReader(subXml);
                                Document doc = saxbuilder.build(reader);
                                Element eleRoot = null;
                                String xpath = "/fields";
                                XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
                                eleRoot = (Element) findPath.selectSingleNode(doc);
                                List fields = eleRoot.getChildren();
                                for(int n=0;n<fields.size();n++) {
                                    Element field = (Element) fields.get(n);
                                    //子集xml中  存储操作人的子集指标权限  rwPriv
                                    String fieldPriv = field.getAttributeValue("rwPriv");
                                    String his_readonly = field.getAttributeValue("his_readonly");
                                    String need = field.getAttributeValue("need");
                                    String fldTitle = field.getAttributeValue("fldTitle");
                                    String format = field.getAttributeValue("format");
                                    String default_value = field.getAttributeValue("defaultValue");
                                    //需要跟节点指标权限结合使用
                                    if("2".equals(rwPriv)&&"2".equals(fieldPriv)){
                                        fieldPriv="2";
                                    }
                                    //如果 子集权限是读 那么操作人即使有写权限 那也只是读
                                    if("1".equals(fieldPriv)){
                                        fieldPriv="1";
                                    }
                                    //如果子集权限是无 权限也是无
                                    //子集没有设置权限 走操作人的指标权限
                                    //如果节点子集权限是写 并且人员子集指标权限是写的时候才有写的权限
                                    if(StringUtils.isEmpty(fieldPriv)){
                                        fieldPriv=rwPriv;
                                    }
                                    if(readOnly){
                                        fieldPriv="1";
                                    }
                                    String fldName = field.getAttributeValue("fldName");
                                    for(int p=0;p<columns.size();p++) {
                                        JsonObject columnJson = columns.get(p).getAsJsonObject();
                                        String columnId = columnJson.get("column_id").getAsString();
                                        if(columnId.equalsIgnoreCase(fldName)) {
                                            columnJson.addProperty("readonly", editFormFlag?"1".equals(fieldPriv):true);
                                            columnJson.addProperty("his_readonly", "true".equals(his_readonly));
                                            columnJson.addProperty("required","true".equals(need));
                                            columnJson.addProperty("title",fldTitle);
                                            columnJson.addProperty("format", format);
                                            columnJson.addProperty("default_value", default_value);
                                        }
                                    }
                                }
                                contentJson.addProperty("isverify", review);
                                //控制子集拖拽
                                if("0".equals(taskId)||isreject){
                                    contentJson.addProperty("rowdrop",true);
                                }

                                if(editFormFlag) {
                                    JsonObject btn=new JsonObject();
                                    btn.addProperty("button_id", "b6");
                                    btn.addProperty("button_desc", "刷新");
                                    btn.addProperty("function_id", "ZC00006306");
                                    btn.addProperty("button_type", "refresh");
                                    if("0".equals(taskId)||isreject){//起草或者驳回申报人的
                                        if("1".equalsIgnoreCase(chgState)||("2".equalsIgnoreCase(chgState)&&!"2".equalsIgnoreCase(rwPriv))) {
                                            JsonArray buttons = new JsonArray();
                                            buttons.add(btn);
                                            contentJson.add("buttons", buttons);
                                        }else {
                                            JsonArray buttons = contentJson.getAsJsonArray("buttons");
                                            buttons.add(btn);
                                            contentJson.add("buttons", buttons);
                                        }
                                    }else {//审核中的
                                        if("1".equalsIgnoreCase(chgState)){//变化前的
                                            JsonArray buttons = new JsonArray();
                                            buttons.add(btn);
                                            contentJson.add("buttons", buttons);
                                        }else if ("2".equalsIgnoreCase(chgState)&&!"2".equalsIgnoreCase(rwPriv)) {//变化后的无写权限的
                                            contentJson.add("buttons", new JsonArray());
                                        }
                                    }
                                }
                                else {
                                    contentJson.add("buttons", new JsonArray());
                                }
                                //驳回记录 子集添加只读控制
                                if(approvedFileItem!=null&&approvedFileItem.containsKey(pageId+relationId.toLowerCase())){
                                    contentJson.addProperty("readonly",true);
                                    contentJson.add("buttons", new JsonArray());
                                }
                            }else {//非子集
                                //postil_flag 0,1,2 无，看，写
                                String postil_flag = "1";
                                contentJson.addProperty("postil_flag", postil_flag);
                            }
                            break;
                        }
                    }
                }
            }
        }
        //-----------------------------------------------------------------------------------------------
        //获取前台展示按钮信息
        /*加载标题栏、工具栏*/
		TemplateToolBarBo toolBarBo= new TemplateToolBarBo(this.conn,this.userView);
		TemplateParam paramBo=new TemplateParam(this.conn,this.userView,Integer.parseInt(tabId));
		HashMap paramBean=new HashMap();
		String return_flag = (String) paramMap.get("return_flag");
    	String moduleId=(String)paramMap.get("moduleId");
    	 /** 区分报审、报备、加签
         * 1：报审 2：加签  3 报备
        */
        String approveFlag=(String)paramMap.get("approveFlag");
        if(StringUtils.isEmpty(approveFlag)){
        	approveFlag="1";
        }
    	//模板号
    	paramBean.put("tab_id", tabId);
    	//任务号
    	paramBean.put("task_id", taskId!=null?PubFunc.encrypt(taskId):"");
    	//module_id 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审 12、证照管理
    	paramBean.put("module_id", moduleId);
    	paramBean.put("return_flag", StringUtils.isEmpty(return_flag)?"6":return_flag);
    	paramBean.put("approve_flag", approveFlag);
    	//1： 1：bs平台  2：移动平台
    	paramBean.put("sys_type", "1");
		ArrayList buttonList = toolBarBo.getAllToolButtonList(paramBo,paramBean);
		boolean b_save = false;//保存
        boolean b_expW = false;//导出word
        boolean b_expP = false;//导出pdf
        boolean b_refresh=false;//刷新
        boolean b_reject=false;//驳回
        boolean b_recall=false;//撤回
        boolean b_apply=false;//报批
        boolean b_cancel=false;//撤销
        //报批 保存 撤回 退回
		/**解析buttonList 获取当前页面展示button*/
		for(int k=0;k<buttonList.size();k++){
			Object obj=buttonList.get(k);
			//判断是否是按钮类型
			if(obj instanceof ButtonInfo){
				ButtonInfo btn=(ButtonInfo) obj;
				String btnName=btn.getText();
				String btnHandler=btn.getHandler();
				if(StringUtils.isNotBlank(btnName)&&btnName.equalsIgnoreCase(ResourceFactory.getProperty("button.reject"))){
					b_reject=true;
				}
				if(StringUtils.isNotBlank(btnName)&& "刷新".equalsIgnoreCase(btnName)){
					b_refresh=true;
				}
				if(StringUtils.isNotBlank(btnName)&& "导出".equalsIgnoreCase(btnName)){
					b_expW=true;
				}
				if(StringUtils.isNotBlank(btnName)&& "预览".equalsIgnoreCase(btnName)){
					b_expP=true;
				}
				if(StringUtils.isNotBlank(btnName)&&("撤回".equalsIgnoreCase(btnName))){
					b_recall=true;
				}
				if(StringUtils.isNotBlank(btnName)&&btnName.equalsIgnoreCase(ResourceFactory.getProperty("button.save"))){
					b_save=true;
				}
				if(StringUtils.isNotBlank(btnHandler)&&("templateTool_me.apply()".equalsIgnoreCase(btnHandler)|| "templateTool_me.assign(1)".equalsIgnoreCase(btnHandler)|| "templateTool_me.assign(4)".equalsIgnoreCase(btnHandler)|| "templateTool_me.submit()".equalsIgnoreCase(btnHandler)|| "templateTool_me.assign(3)".equalsIgnoreCase(btnHandler))){
					b_apply=true;
				}
				if(StringUtils.isNotBlank(btnName)&&btnName.equalsIgnoreCase(ResourceFactory.getProperty("button.abolish"))){
					b_cancel=true;
				}
			}else{//默认为String字符串
				String btn2=(String) obj;
				//<jsfn>{xtype:'button',text:'gz_new.gz_accounting.FunctionNavigation',id:'navigationId',menu:{items:[{text:'导入数据',handler:function(){templateTool_me.downTempData();},id:'m_downLoad'},{text:'导入HTML表单',handler:function(){templateTool_me.downExcelTemp();},id:'z_downExcelTemp'},{text:'批量处理',menu:{items:[{text:'menu.gz.batchmany.update',handler:function(){templateTool_me.batchUpdateFields();}},{text:'menu.gz.single.update',handler:function(){templateTool_me.singleUpdateFields();}}]}},{text:'打印',handler:function(){print();},id:'printButton'},{text:'导出PDF',menu:{items:[{text:'当前人员生成PDF',handler:function(){outPdf(1,1);},id:'curOutPdf'},{text:'全部人员生成PDF',menu:{items:[{text:'一人一文档',handler:function(){outPdf(2,0);}},{text:'多人一文档',handler:function(){outPdf(2,1);}}]}},{text:'部分人员生成PDF',menu:{items:[{text:'一人一文档',handler:function(){outPdf(3,0);}},{text:'多人一文档',handler:function(){outPdf(3,1);}}]}}]}},{text:'导出WORD',menu:{items:[{text:'menu.gz.currword',handler:function(){outword(1,1);},id:'curOutword'},{text:'menu.gz.allword',menu:{items:[{text:'一人一文档',handler:function(){outword(2,0);}},{text:'多人一文档',handler:function(){outword(2,1);}}]}},{text:'menu.gz.selword',menu:{items:[{text:'一人一文档',handler:function(){outword(3,0);}},{text:'多人一文档',handler:function(){outword(3,1);}}]}}]}},{text:'设置',menu:{items:[{text:'临时变量',handler:function(){templateTool_me.setTempVar();}},{text:'计算公式',handler:function(){templateTool_me.setFormula();}},{text:'审核公式',handler:function(){templateTool_me.checkFormula();}},{text:'设置业务日期',menu:{items:[{xtype:'datepicker',handler:function(picker, date){templateTool_me.setAppDate(picker,date);},todayTip:'',value:new Date('2020','5','22')}]}}]}}]}}</jsfn>
				if(StringUtils.isNotBlank(btn2)&&btn2.startsWith("<jsfn>")&&btn2.endsWith("</jsfn>")){
					if(btn2.indexOf("'导出WORD'")!=-1){
						b_expW=true;
					}
					if(btn2.indexOf("'导出PDF'")!=-1){
						b_expP=true;
					}
				}
			}
		}
		jsonObject.addProperty("b_cancel", b_cancel);
		jsonObject.addProperty("b_apply", b_apply);
		jsonObject.addProperty("b_recall", b_recall);
		jsonObject.addProperty("b_reject", b_reject);
        jsonObject.addProperty("b_save", b_save);
        jsonObject.addProperty("b_expP", b_expP);
        jsonObject.addProperty("b_expW", b_expW);
        jsonObject.addProperty("b_refresh", b_refresh);
        Gson gson = new Gson();
        jsonObject.addProperty("mediasortList", gson.toJson(getMediasortList("1",taskId,isreject,true)));
        return jsonObject.toString();
    }

    /***
     * 获取多媒体分类
     * @param inforkind
    * @param taskId
    * @param isreject
    * @param needPrive
     * @return
     * @throws Exception
     */
    public List<Map<String,String>> getMediasortList(String inforkind, String taskId, boolean isreject, boolean needPrive) throws Exception{
        List<Map<String,String>> mediaList=new ArrayList<Map<String,String>>();
        String sql="select sortname,flag,id from mediasort where dbflag='"+inforkind+"' order by id";
        ContentDAO dao=new ContentDAO(this.conn);
        ArrayList<LazyDynaBean> list=dao.searchDynaList(sql);
        Map<String,String> map=null;
        Map<String,String> firstmap = new HashMap<String, String>();
        int i = 0;
        for(LazyDynaBean bean:list){
            map=new HashMap<>();
            String flag = (String)bean.get("flag");
            if(i==0) {
                firstmap.put("label",(String)bean.get("sortname"));
                firstmap.put("value",flag);
                firstmap.put("id",(String)bean.get("id"));
            }
            i++;
            if(needPrive&&("0".equals(taskId)||isreject)) {//申报人或者驳回申报人
                if (!this.userView.isSuper_admin()){//判断多媒体权限
                    if (!this.userView.hasTheMediaSet(flag)) {
						continue;
					}
                }
            }
            map.put("label",(String)bean.get("sortname"));
            map.put("value",flag);
            map.put("id",(String)bean.get("id"));
            mediaList.add(map);
        }
        if(mediaList.size()==0) {
            mediaList.add(firstmap);
        }
        return mediaList;
    }
     public void createCollapseEditor(JsonArray td,String hz) {
         JsonObject text=new JsonObject();
         text.addProperty("type", "collapse");
         text.addProperty("name",getUniqueOne());
         text.addProperty("title", hz);
         text.addProperty("accordion", false);
         td.add(text);
     }

     public void createOptionEditor(JsonArray td, String element_id) {
         JsonObject text=new JsonObject();
         text.addProperty("type", "opinion");
         text.addProperty("element_id", element_id);
         td.add(text);
     }

     public String getUniqueOne(){
         UUID uuid=UUID.randomUUID();
         String uuidStr=uuid.toString();
         return uuidStr;
     }
     /**
      * 修改模板指标对应数据结构json属性
      *
      * @param paramMap {dataJson:'',userName:'',tab_id:'',task_id:'',seqnum:'',ins_id:'',moduleId:'',procId:'',batchId:''}
      * @return
      */
     public String updateDataJson(HashMap paramMap) throws Exception {
         String tabId = (String) paramMap.get("tabId");
         String dataJson = (String) paramMap.get("dataJson");
         String taskId = (String) paramMap.get("taskId");
         String insId = (String) paramMap.get("ins_id");
         String moduleId = (String) paramMap.get("moduleId");
         String pageId = (String) paramMap.get("pageId");
         String optionField = (String) paramMap.get("optionField");//获取审批意见指标
         String optionFieldVal="";
         // 获得临时表数据，拼接到json数据上
         if(StringUtils.isBlank(dataJson)) {
             dataJson = "{}";
         }
         AttachmentBo attachmentBo=new AttachmentBo(userView, conn, tabId);
         attachmentBo.initParam(true);

         ArrayList fieldValueMap =(ArrayList) paramMap.get("fieldValueList");
         //记录子集审批通过的节点
         Map<String,Object> subSetApproveMap = new HashMap<String,Object>();
         JsonArray dataJsonArray = new JsonArray();
         JsonObject dataJsonObject = new JsonObject();
         Map codeMap=new HashMap();
         for(int i=0;i<fieldValueMap.size();i++) {
        	 LazyDynaBean fieldBean = (LazyDynaBean) fieldValueMap.get(i);
        	 String elementId = (String) fieldBean.get("fldName");
        	 String relationId = (String) fieldBean.get("fldName");
        	 if(relationId.startsWith("t_")) {//子集
        		 String disValue = (String) fieldBean.get("disValue");
        		 JsonObject jsonObject = new JsonParser().parse(disValue).getAsJsonObject();
        		 JsonArray jsonArray = jsonObject.getAsJsonArray("records");
        		 String columns = jsonObject.get("columns").getAsString();
        		 String columnArr [] = columns.split("`");
        		 JsonArray subsetArray = new JsonArray();
        		 for(int j=0;j<jsonArray.size();j++) {
        			 JsonObject json = jsonArray.get(j).getAsJsonObject();
        			 String contentValue = (json.get("contentValue")==null||json.get("contentValue").isJsonNull())?null:json.get("contentValue").getAsString();
        			 String i9999 = (json.get("I9999")==null||json.get("I9999").isJsonNull())?null:json.get("I9999").getAsString();
        			 String record_key_id = (json.get("record_key_id")==null||json.get("record_key_id").isJsonNull())?null:json.get("record_key_id").getAsString();
        			 String isHaveChange = (json.get("isHaveChange")==null||json.get("isHaveChange").isJsonNull())?null:json.get("isHaveChange").getAsString();
        			 String state = (json.get("state")==null||json.get("state").isJsonNull())?null:json.get("state").getAsString();
        			 //songyl 注释掉，该json字符串中 没有 timestmp 报错。
        			 //String timestamp = json.getString("timestamp");
        			 String verify_status = (json.get("verify_status")==null||json.get("verify_status").isJsonNull())?"0":json.get("verify_status").getAsString();
        			 String postil_msg = (json.get("postil_msg")==null||json.get("postil_msg").isJsonNull())?"":json.get("postil_msg").getAsString();
        			 String postil_username = (json.get("postil_username")==null||json.get("postil_username").isJsonNull())?"":json.get("postil_username").getAsString();
        			 JsonObject subsetJson = new JsonObject();
        			 subsetJson.addProperty("serial_id", j+"");
        			 subsetJson.addProperty("record_key_id", record_key_id);
        			 subsetJson.addProperty("i9999", i9999);
        			 subsetJson.addProperty("ishavechange", Boolean.parseBoolean(isHaveChange));
        			 subsetJson.addProperty("state", state);
        			 subsetJson.addProperty("readonly", false);
        			 subsetJson.addProperty("verify_status", verify_status);
        			 String contentArr [] = contentValue.split("`",-1);
        			 //遍历子集内容 查找是否存在相同代码项
        			 for(int k=0;k<contentArr.length;k++) {

        				 if(!codeMap.containsKey(columnArr[k])){
        					 FieldItem item=DataDictionary.getFieldItem(columnArr[k]);
        					 if(item!=null&&!"0".equals(item.getCodesetid())){
        						 codeMap.put(columnArr[k], true);
        					 }else{
        						 codeMap.put(columnArr[k], false);
        					 }
        				 }
        				 //附件解析特殊处理
        				 if ("attach".equalsIgnoreCase(columnArr[k])) {
        					 subsetJson.addProperty(columnArr[k], parseSubsetAttach(contentArr[k],attachmentBo));
        				 } else if((boolean) codeMap.get(columnArr[k])){//代码项指标
        					 subsetJson.addProperty(columnArr[k], contentArr[k].indexOf("||")==-1?contentArr[k]:((contentArr[k].split("\\|\\|")[1])));
        				 }else{
        					 subsetJson.addProperty(columnArr[k], contentArr[k]);
        				 }
        			 }
        			 if(subSetApproveMap!=null&&subSetApproveMap.containsKey(relationId)){
        				 Map<String,String> record_map = (Map<String,String>)subSetApproveMap.get(relationId);
        				 //审批通过的子集记录不可编辑 只记录通过的子集记录
        				 if(record_map.containsKey(record_key_id)){
        					 subsetJson.addProperty("readonly",true);
        				 }
        			 }
        			 subsetArray.add(subsetJson);
        		 }
        		 dataJsonObject.add(elementId+"`"+relationId, subsetArray);
        	 }else {
        		 String fldValue = (String) fieldBean.get("keyValue");
        		 //如果有审批意见 指标， 赋予其值
        		 if(StringUtils.isNotEmpty(optionField)&&StringUtils.isNotEmpty(fldValue)&&optionField.equalsIgnoreCase(elementId)){
        			 optionFieldVal=fldValue;
        		 }
        		 dataJsonObject.addProperty(elementId+"`"+relationId, fldValue);
        		 if(relationId.startsWith("attachment_")) {//附件
        			 String filetype = (String) fieldBean.get("filetype");//批注
        			 dataJsonObject.addProperty(elementId+"`"+relationId+"`mediasort", filetype);
        		 }
        	 }
        	 if(i==fieldValueMap.size()-1) {
        		 dataJsonObject.addProperty("tabId", tabId);
        		 dataJsonObject.addProperty("taskId", taskId);
        		 dataJsonObject.addProperty("insId", insId);
        		 dataJsonObject.addProperty("moduleId", moduleId);
        		 dataJsonObject.addProperty("pageId", pageId);
        		//将审批意见添加上
                 Gson gson = new Gson();
                 if(!"0".equals(insId)) {
					 //获取审批意见----人事异动的任务监控中的审批过程
					 TemplateProcessBo templateProcess = new TemplateProcessBo(conn, userView);
					 ArrayList commentsList = templateProcess.viewProcess(paramMap);
					 String res = gson.toJson(commentsList);
					 JsonArray commentsArray = new JsonParser().parse(res).getAsJsonArray();
					 dataJsonObject.add("opinionContent", commentsArray);
                 }
				 dataJsonArray.add(dataJsonObject);
				 dataJsonObject = new JsonObject();
        	 }
         }
         if(fieldValueMap.size()==0) {
             dataJsonObject.addProperty("tabId", tabId);
             dataJsonObject.addProperty("taskId", taskId);
             dataJsonObject.addProperty("insId", insId);
             dataJsonObject.addProperty("moduleId", moduleId);
             dataJsonArray.add(dataJsonObject);
         }
         return dataJsonArray.toString();
     }
     /**
     * 解析子集附件
     * @param data
     *  {
     *   fileId:"V8OseaQ7tZtUHJH9k40AnwPAATTP3HJDPAATTPPAATTP3HJDPAATTP",
     *   name: 'dssds.xls',
     *   size：xxx
     *   fromhistory:id,
     *   index:
     *   fileType:'F',
     * }
     * @throws Exception
     */
    private String parseSubsetAttach(String data,AttachmentBo bo) throws Exception {
        String rootDir=bo.getRootDir();
        Gson gson = new Gson();
        String parseText = "";
        List<Map<String, String>> list = new ArrayList<>();
        String[] arry=data.split(",",-1);
        Map<String, String> map = null;
        for (String obj : arry) {
            if (StringUtils.isEmpty(obj) || StringUtils.equalsIgnoreCase(" ",obj)) {
                continue;
            }
            //filename+"|"+path+"|"+srcfilename+"|"+fileSizeString+"|"+id+"|"+m+"|"+"type:"+filetype ;
            String[] record =obj.split("\\|", -1);
            map = new HashMap<>();
            //暂用普通文件路径
            map.put("fileId",record[1]);
            map.put("filepath",record[1]);
            map.put("name",record[2]);
            map.put("size",record[3]);
            map.put("fromhistory",record[4]);
            map.put("index", record[5]);
            if(StringUtils.isEmpty(record[6])) {
                map.put("fileType", "");
            }else {
                map.put("fileType", record[6].split(":",-1)[1]);
            }
            //添加文件绝对路径 供前端下载调用
            String absdir=rootDir+PubFunc.decrypt(record[1]);
            map.put("absolutepath",PubFunc.encrypt(absdir));
            list.add(map);
        }
        parseText = gson.toJson(list);

        return parseText;
    }

    /**
	 * @Title: getPageList
	 * @Description:  获取模板显示的页签
	 * @param @param isMobile 是否显示异动标签
	 * @param noShowPageNo  不显示那些页签
	 * @param @return
	 * @param @throws Exception
	 * @return ArrayList
	 */
	private ArrayList getPageList(int tabId, boolean isMobile, String noShowPageNo,String taskId) throws Exception {
		ArrayList outlist = new ArrayList();
		try {
			TemplateUtilBo utilBo = new TemplateUtilBo(this.conn,
					this.userView);
			TemplateCardBo cardBo = new TemplateCardBo(this.conn,this.userView,tabId);
						   cardBo.setTask_id(taskId);
			ArrayList list = utilBo.getAllTemplatePage(tabId);
			for (int i = 0; i < list.size(); i++) {
				TemplatePage pagebo = (TemplatePage) list.get(i);
				if(!"".equals(noShowPageNo)){//如果有设置的不显示页签 优先走这个
					String pageid =  String.valueOf(pagebo.getPageId());
					String pagearr [] = noShowPageNo.split(",");
					boolean noprint = false;
					for(String pid:pagearr){
						if(pid.equalsIgnoreCase(pageid)){
							noprint = true;
							break;
						}
					}
					if(noprint) {
						continue;
					}
				}else if (!pagebo.isShow()) {
					continue;
				}

				if (isMobile != pagebo.isMobile()) {
					continue;
				}

				if(!pagebo.isPrint())//设置此页不打印 不显示此页
				{
					continue;
				}

				//判断此页的指标无读写权限。无读写权限指标的不显示

				if (!cardBo.isHaveReadFieldPriv(pagebo.getPageId() + "")) {//
					continue;
				}
				outlist.add(pagebo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return outlist;
	}

	/**
	 * 删除之前记录
	 * @param tableName
	 */
	private void deleteFillInfo(String tableName) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			ArrayList values = new ArrayList();
			String fieldname = "create_time";
			String sql="delete from "+tableName+" where ("+Sql_switcher.diffDays(Sql_switcher.sqlNow() ,fieldname)+")>0.5";
			dao.delete(sql, values);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 判断taskid的任务是否存在
	 * @param taskId
	 */
	private boolean checkExistsTaskId(String taskId) {
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		boolean ExistsTaskId = false;
		try{
			String[] tasklist=StringUtils.split(taskId,",");
			if(tasklist.length==1){
				rowSet = dao.search("select 1 from t_wf_instance t,t_wf_task t1 where t.ins_id=t1.ins_id and t1.task_id='"+tasklist[0]+"'");
				if(rowSet.next()) {
					ExistsTaskId = true;
				}
			}else {
				ExistsTaskId = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
		return ExistsTaskId;
	}

	//-------模板保存操作---------------------------------------------------------------------------------
	/**
	 * 功能：保存模板操作
	 * @param valueMap：
	 *       moduleId： 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审 12、证照管理
	 *       tabId：模板号
	 *       taskId：任务号
	 *       savedata：获取变化的数据 获取变化的数据信息。
	 *       isCompute: 为true 且该模板是自动计算，会执行计算公式
	 * @return
	 */
	public String saveTempInfo(HashMap valueMap){
		String message="success";
        String moduleId = (String)valueMap.get("moduleId");
        String tabId = (String)valueMap.get("tabId");
        String taskId = (String)valueMap.get("taskId");
        ArrayList list=(ArrayList)valueMap.get("savedata"); //获取变化的数据 获取变化的数据信息。
        String noHint=(String)valueMap.get("noHint"); //是否不显示提示信息 默认为抛出异常
        String isCompute=(String)valueMap.get("isCompute"); //为true 且该模板是自动计算，会执行计算公式
		isCompute ="true";
        if ("".equals(taskId)){
            return message;
        }
        TemplateParam paramBo=new TemplateParam(this.conn,this.userView,Integer.parseInt(tabId));
        TemplateUtilBo utilBo= new TemplateUtilBo(this.conn,this.userView);
        TemplateBo templateBo=new TemplateBo(this.conn,this.userView,paramBo);
        templateBo.setModuleId(moduleId);
        templateBo.setTaskId(taskId);

        if((list==null||list.size()==0))
        {
        	String autoCompute = this.batchCompute(taskId,templateBo,paramBo,utilBo,isCompute,noHint);
            return message;
        }
        TemplateDataBo tableDataBo=new TemplateDataBo(this.conn,this.userView,paramBo);
        String tableName=utilBo.getTableName(moduleId,Integer.parseInt(tabId), taskId);

        /**数据集字段列表*/
        ArrayList cellList= utilBo.getAllCell(Integer.parseInt(tabId));

        // 查找变化前的历史记录单元格,保存时把这部分单元格的内容过滤掉，不作处理
        HashMap filedPrivMap =tableDataBo.getFieldPrivMap(cellList, taskId);
        ArrayList fieldList=filterTemplateSetList(cellList,filedPrivMap);
        HashMap fieldMap = new HashMap();
        for (int i=0;i<fieldList.size();i++){
            TemplateSet setBo =(TemplateSet)fieldList.get(i);
            fieldMap.put(setBo.getTableFieldName(), "1");
        }
        String blacklist_per="";//黑名单人员库
        String blacklist_field="";//黑名单人员指标
        ContentDAO dao=new ContentDAO(this.conn);
        ArrayList<LazyDynaBean> subDataList = new ArrayList<LazyDynaBean>();
        try
        {
            if(paramBo.getOperationType()==0){//人员调入模板
                Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
                blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");//黑名单人员库
                blacklist_field=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");//黑名单人员指标
            }

            for(int i=0;i<list.size();i++){
//                MorphDynaBean bean=(MorphDynaBean)list.get(i);
//                HashMap map = PubFunc.DynaBean2Map(bean);
            	HashMap map=(HashMap) list.get(i);
                //判断是否是黑名单里的人物
                if(blacklist_per!=null&&blacklist_field!=null&&blacklist_per.trim().length()>0&&blacklist_field.trim().length()>0)
                {
                    if(fieldMap.get(blacklist_field+"_2")!=null)
                    {
                        String value=(String)map.get(blacklist_field+"_2");
                        if(value!=null&&value.trim().length()>0)
                        {
                            if(templateBo.validateIsBlackList(blacklist_per,blacklist_field,value))
                            {
                                return map.get("a0101_1")+"在黑名单库有记录，不允许保存!";
                            }
                        }
                    }
                }

                ArrayList updFieldList=new ArrayList();//要修改的字段
                ArrayList updDataList=new ArrayList(); //要修改字段对应的数据
                ArrayList updAutoLogSetBoList=new ArrayList();
                String updAutoLotObjectid="";
                String ins_id="0";

                for(int j=0;j<fieldList.size();j++)
                {
                	boolean bUpdA0101_1=false;//是否需要同步更改变化前姓名 人员调入、新增机构模板需要 wangrd 20160908
                    TemplateSet setBo =(TemplateSet)fieldList.get(j);
                    if ("C".equals(setBo.getFlag())||"P".equals(setBo.getFlag())){
                        continue;
                    }
                    String fieldName= setBo.getTableFieldName();
                    if(StringUtils.contains(fieldName,"attachment")){
						continue;
					}
                    String key = fieldName+"`"+fieldName;
                    if (updFieldList.contains(fieldName)){//排除多个单元格指定同一指标的情况。
                        continue;
                    }

                    int updDataSize =updDataList.size();
                    //liuyz 28807 首先先判断用户是否修改了这个字段，数值型表格控件删除指后传过来的值为null，手工在这里修改一下让用户清空值能保存上,否则用户无法保存清空值。
                    if(map.containsKey(key)&&map.get(key)==null&&"N".equals(setBo.getField_type()))
                    {
                    	map.put(key,"");
                    }
                    if(map.get(key)!=null){//record有此指标的值
                    	String data = "";
                        if("signature".equals(key)){//签章
                        	data=map.get(key)+"";
                        	if(!"".equals(data)){
                        		int signatureType = paramBo.getTemplateModuleParam().getSignatureType();
                        		data = analysisSignatureXml(dao,data,signatureType);
                        	}
                        }else{
                        	data=map.get(key)+"";
                        }
                        if (data== null) {
							data="";
						}

                        if (setBo.isABKItem() && !setBo.isSubflag()){//普通指标
                        	TemplateItem templateItem = utilBo.convertTemplateSetToTemplateItem(setBo);//lis 20160705
                            FieldItem fldItem = templateItem.getFieldItem();
                            if (fldItem==null) {
								continue;
							}
                            if(setBo.isBcode()){//代码型
                                if (data!=null&&data.length()>0){
                                    String []  arrData= data.split("`");
                                    if (arrData.length>0){
                                        data=arrData[0];
                                    }
                                    else {//兼容data="`"的情况
                                        data=data.replace("`", "");
                                    }

                                }
                                updDataList.add(data);
                            }else if("D".equals(fldItem.getItemtype())){//
                                String disformat=setBo.getDisformat()+"";   //disformat=25: 1990.01.01 10:30
                                if(StringUtils.isNotBlank(data)){
                                	int dateformat = getFormat(setBo.getDisformat());//4,7,10,16
                                	Timestamp datetime = null;
                                	Calendar now = Calendar.getInstance();
                                	int month = now.get(Calendar.MONTH) + 1;
                                	int day = now.get(Calendar.DAY_OF_MONTH);
                                	if(dateformat==4){//年
                                		if(data.indexOf("-")<0) {
											datetime = DateUtils.getTimestamp(data+"."+month+"."+day,"yyyy.MM.dd");
										} else {
											datetime = DateUtils.getTimestamp(data+"-"+month+"-"+day,"yyyy-MM-dd");
										}
                                		updDataList.add(datetime);
                                	}
                                	else if(dateformat==7){//年月
                                		if(data.indexOf("-")<0) {
											datetime = DateUtils.getTimestamp(data+"."+day,"yyyy.MM.dd");
										} else {
											datetime = DateUtils.getTimestamp(data+"-"+day,"yyyy-MM-dd");
										}
                                		updDataList.add(datetime);
                                	}
                                	else if(dateformat==10){//年月日
                                		if(data.indexOf("-")<0) {
											datetime = DateUtils.getTimestamp(data,"yyyy.MM.dd");
										} else {
											datetime = DateUtils.getTimestamp(data,"yyyy-MM-dd");
										}
                                		updDataList.add(datetime);
                                	}
                                	else if(dateformat==16){//年月日时分
                                		if(data.indexOf("-")<0) {
											datetime = DateUtils.getTimestamp(data+":00","yyyy.MM.dd HH:mm:ss");
										} else {
											datetime = DateUtils.getTimestamp(data+":00","yyyy-MM-dd HH:mm:ss");
										}
                                		updDataList.add(datetime);
                                	}
                                }else{
                                    updDataList.add(null);
                                }
                            }else if("N".equals(fldItem.getItemtype())){
                                if(data.indexOf(".")!=-1){
                                    if(data.split("\\.")[0].length()>fldItem.getItemlength()){
                                        String valueLengthError=fldItem.getItemdesc()
                                        +ResourceFactory.getProperty("templa.value.lengthError")
                                        +fldItem.getItemlength()+","+ResourceFactory.getProperty("templa.value.fix");
                                        return valueLengthError.toString();
                                    }
                                }else{
                                    if(data.length()>fldItem.getItemlength()){
                                        String valueLengthError=fldItem.getItemdesc()
                                        +ResourceFactory.getProperty("templa.value.lengthError")
                                                 +fldItem.getItemlength()+","+ResourceFactory.getProperty("templa.value.fix");
                                        return valueLengthError.toString();
                                    }
                                }
                                if(fldItem.getDecimalwidth()==0){
                                	//liuyz bug26865 指标设置的长度和模版设置的小数长度不同，导致用户可以输入带小数的值，这里fldItem取的是指标小数位数，所以转换时会出现异常。给出提示。
                                	try{
                                		updDataList.add(data.length()==0?null:Integer.parseInt(data));
                                	}
                                	catch (Exception e) {
                                		return "指标项："+fldItem.getItemdesc()+"不允许有小数位";
									}
                                }else{
                                    String value = PubFunc.DoFormatDecimal(data==null||data.length()==0?"":data, fldItem.getDecimalwidth());
                                    updDataList.add(value.length()==0?null:PubFunc.parseDouble(value));
                                }
                            }   else  if("M".equals(fldItem.getItemtype())){
                            	String opinion_field = paramBo.getOpinion_field();
                            	//liuyz 大文本html编辑器不需要检测是否超过字数限制。但是fldItem中的inputType不对，需要重新获取。
                            	FieldItem fielditem = DataDictionary.getFieldItem(fldItem.getItemid());
                    			if(fielditem!=null) {
									fldItem.setInputtype(fielditem.getInputtype());
								}
                            	if(StringUtils.isNotBlank(opinion_field) && !opinion_field.equalsIgnoreCase(fldItem.getItemid())&&fldItem.getItemlength()!=10&&fldItem.getItemlength()!=0&&fldItem.getInputtype()!=1){
                            		  if(data.length()>fldItem.getItemlength()){
	                            			  StringBuffer valueLengthError = new StringBuffer();
	                                          valueLengthError.append(ResourceFactory.getProperty("template_new.filed"));
	                                          valueLengthError.append("[");
	  	                                      valueLengthError.append(fldItem.getItemdesc());
	  	                                      valueLengthError.append("]");
	                                          valueLengthError.append(ResourceFactory.getProperty("template_new.allowMaxInputLength"));
	                                          valueLengthError.append(fldItem.getItemlength());
	                                          valueLengthError.append(ResourceFactory.getProperty("template_new.char"));
	                                          return valueLengthError.toString();
	                                    }
                            	}
                            	updDataList.add(data);
                         }
                          else {
                                if(TemplateFuncBo.getStrLength(data)>fldItem.getItemlength()){
	                                    StringBuffer valueLengthError = new StringBuffer();
	                                    valueLengthError.append(ResourceFactory.getProperty("template_new.filed"));
	                                    valueLengthError.append("[");
	                                    valueLengthError.append(fldItem.getItemdesc());
	                                    valueLengthError.append("]");
	                                    valueLengthError.append(ResourceFactory.getProperty("template_new.allowMaxInputLength"));
	                                    valueLengthError.append(fldItem.getItemlength());
	                                    valueLengthError.append(ResourceFactory.getProperty("template_new.char"));
	                                    return valueLengthError.toString();
                                }
                                updDataList.add(data);
                                if("a0101_2".equalsIgnoreCase(fieldName)){//变化后姓名
                                	if (paramBo.getOperationType() ==0){//人员调入模板
                                		updDataList.add(data);
                                		bUpdA0101_1=true;
                                	}

                                }else if("codeitemdesc_2".equalsIgnoreCase(fieldName)){//变化后机构名称
                                	if (paramBo.getOperationType() ==5){//新增机构、岗位
                                		updDataList.add(data);
                                		bUpdA0101_1=true;
                                	}
                                }
                            }
                        }
                        else if (setBo.isSubflag()) {//子集
                            //卡片模式保存子集，如果有上传附件则保存到指定目录
                            TemplateSubsetBo subBo=new TemplateSubsetBo(this.conn,this.userView,tabId,setBo.getTableFieldName());
                            ArrayList subList = (ArrayList) map.get(key);
                            ArrayList subList_ = new ArrayList();
                            for(int z = 0;z<subList.size();z++){
                            	subList_.add(PubFunc.DynaBean2Map((MorphDynaBean)subList.get(z)));
							}
                            data = subBo.data2Xml(subList_);
                            updDataList.add(data);
                        }
                        else if("S".equalsIgnoreCase(setBo.getFlag())){//签章
                        	updDataList.add(data);
                        }
                        else {//临时变量

                        	 if(setBo.isBcode()){//代码型
                                 if (data!=null&&data.length()>0){
                                     String []  arrData= data.split("`");
                                     if (arrData.length>0){
                                         data=arrData[0];
                                     }
                                     else {//兼容data="`"的情况
                                         data=data.replace("`", "");
                                     }

                                 }
                                 updDataList.add(data);
                             }else if("D".equals(setBo.getField_type())){//
                                 if(StringUtils.isNotBlank(data)){
                                       java.sql.Date date = null;
                                       String dateStr = data;
                                       if(dateStr.indexOf("-")<0) {
										   date = DateUtils.getSqlDate(data,"yyyy.MM.dd");
									   } else {
										   date = DateUtils.getSqlDate(data,"yyyy-MM-dd");
									   }
                                        updDataList.add(date);
                                 }else{
                                     updDataList.add(null);
                                 }
                             }else if("N".equals(setBo.getField_type())){
                            	 int flddec=setBo.getVarVo().getInt("flddec");
                            	 int fldlen=setBo.getVarVo().getInt("fldlen");
                            	 String chz=setBo.getVarVo().getString("chz");
                            	 if(data.indexOf(".")!=-1){
                                     if(data.split("\\.")[0].length()>fldlen){
                                         String valueLengthError=chz
                                         +ResourceFactory.getProperty("templa.value.lengthError")
                                         +fldlen+","+ResourceFactory.getProperty("templa.value.fix");
                                         return valueLengthError.toString();
                                     }
                                 }else{
                                     if(data.length()>fldlen){
                                         String valueLengthError=chz
                                         +ResourceFactory.getProperty("templa.value.lengthError")
                                                  +fldlen+","+ResourceFactory.getProperty("templa.value.fix");
                                          return valueLengthError.toString();
                                     }
                                 }
                                 if(flddec==0){
                                     updDataList.add(data.length()==0?null:Integer.parseInt(data));
                                 }else{
                                     String value = PubFunc.DoFormatDecimal(data==null||data.length()==0?"":data,flddec);
                                     updDataList.add(value.length()==0?null:PubFunc.parseDouble(value));
                                 }
                             }else if("A".equals(setBo.getField_type())&&"0".equals(setBo.getCodeid())) {//增加临时变量字符型长度校验
                            	 int fldlen=setBo.getVarVo().getInt("fldlen");
                            	 String chz=setBo.getVarVo().getString("chz");
                            	 if(TemplateFuncBo.getStrLength(data)>fldlen){
                            		 StringBuffer valueLengthError = new StringBuffer();
                            		 valueLengthError.append(ResourceFactory.getProperty("label.gz.variable"));
	                                 valueLengthError.append("[");
	                                 valueLengthError.append(chz);
	                                 valueLengthError.append("]");
	                                 valueLengthError.append(ResourceFactory.getProperty("template_new.allowMaxInputLength"));
	                                 valueLengthError.append(fldlen);
	                                 valueLengthError.append(ResourceFactory.getProperty("template_new.char"));
                                     return valueLengthError.toString();
                                 }
                                 updDataList.add(data);
                             }
                             else {
								 updDataList.add(data);
							 }
                        }

                        if(updDataList.size()>updDataSize){//datalist放入数据了 fieldlist也得相应增加
                            updFieldList.add(fieldName);
                            updAutoLogSetBoList.add(setBo);
                            if (bUpdA0101_1){
                            	if("a0101_2".equalsIgnoreCase(fieldName)){//变化后姓名
                            		updFieldList.add("a0101_1");
                            		updAutoLogSetBoList.add(setBo);
                                }else if("codeitemdesc_2".equalsIgnoreCase(fieldName)){//变化后机构名称
                                	updFieldList.add("codeitemdesc_1");
                                	updAutoLogSetBoList.add(setBo);
                                }
                            }
                        }
                    }
                }

                String updateSql="update "+tableName+" set ";
                String fieldName = null;
                StringBuffer updateFields = new StringBuffer();
                for(int j=0;j<updFieldList.size();j++){
                    fieldName = (String)updFieldList.get(j);
                    updateFields.append("," + fieldName+"=?");
                }
                if(updateFields.length() > 1) {
					updateSql += updateFields.substring(1);
				}
                if (paramBo.getInfor_type()== 1){
                	String basepre = "";
                	String a0100 = "";
                	basepre=(String)map.get("basepre`basepre");
                	a0100=(String)map.get("a0100`a0100");
                	updAutoLotObjectid=basepre+"`"+a0100;
                    updateSql+=" where A0100='"+a0100+"' and BasePre='"+basepre+"'";
                }
                else if (paramBo.getInfor_type()== 2){
                	String b0110 = "";
                	b0110=(String)map.get("b0110`b0110");
                	updAutoLotObjectid=b0110;
                    updateSql+=" where b0110='"+b0110+"'";
                }
                else {
                	String e01a1 = "";
                	e01a1=(String)map.get("e01a1`e01a1");
                	updAutoLotObjectid=e01a1;
                    updateSql+=" where e01a1='"+e01a1+"'";
                }
                if (!"0".equals(taskId)){
                    ins_id=(String)valueMap.get("insId");
                    updateSql+=" and ins_id="+ins_id+"";
                }
                if (updFieldList.size()>0){
                	Boolean isAotuLog = paramBo.getIsAotuLog();
        			Boolean isRejectAotuLog = paramBo.getIsRejectAotuLog();
        			if(isRejectAotuLog==true&&!"0".equalsIgnoreCase(ins_id)){
        				Boolean haveReject= utilBo.isHaveRejectTaskByInsId(ins_id);
        				if(haveReject){
        					isAotuLog=true;
        				}
        			}
                	if(isAotuLog&&!("0".equalsIgnoreCase(ins_id)&&(paramBo.getOperationType()==0||paramBo.getOperationType()==5))){
                		TempletChgLogBo chgLogBo=new TempletChgLogBo(this.conn,this.userView,paramBo);
                		chgLogBo.createTemplateChgLogTable("templet_chg_log");
                		String realTask_id=(String) map.get("realtask_id_e");
                		if(StringUtils.isBlank(realTask_id)){
                			realTask_id="0";
                		}
                		if(taskId.indexOf(",")==-1&&StringUtils.isNotBlank(taskId)){
                			realTask_id=taskId;
                		}
                		chgLogBo.insertOrUpdateAllLogger(updFieldList,updAutoLogSetBoList,updDataList,ins_id,realTask_id,updAutoLotObjectid,tableName,paramBo.getInfor_type());
                	}
                    dao.update(updateSql, updDataList);
                }
            }
            String autoCompute = this.batchCompute(taskId,templateBo,paramBo,utilBo,isCompute,noHint);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            //解决保存提示8060问题
            message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{
				PubFunc.resolve8060(this.conn,tableName);
				message="请重新操作!";
			}
        }
        return message;
	}

	public String batchCompute(String taskId,TemplateBo templateBo,TemplateParam paramBo,TemplateUtilBo utilBo,String isCompute,String noHint) {
		String autoCompute="false";
		try {
			Boolean bCalc=false;
			if("0".equals(taskId)|| "1".equals(templateBo.isStartNode(taskId))){
				if(paramBo.getAutoCaculate().length()==0){
					if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
						bCalc=true;
					}
				}
				else if("1".equals(paramBo.getAutoCaculate())){
					bCalc=true;
				}
			}else {
				if(paramBo.getSpAutoCaculate().length()==0){
					if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
						bCalc=true;
					}
				}
				else if("1".equals(paramBo.getSpAutoCaculate())){
					bCalc=true;
				}
			}
			if(bCalc &&"true".equalsIgnoreCase(isCompute)){//不再根据是否有变化后指标修改判断是否需要计算。按照保存计算，切人、切页不计算
				ArrayList formulalist=templateBo.readFormula();
				formulalist.addAll(templateBo.readSubsetFormula());
				if(formulalist.size()>0)
				{
					String[] taskids = taskId.split(",");
					String ins_ids="";
					for(int i=0;i<taskids.length;i++){
						String ins_id =utilBo.getInsId(taskids[i]);
						ins_ids=ins_ids+","+ins_id;
					}
					if("true".equals(noHint)) {
						templateBo.setThrow(false);
					}
					templateBo.setInsid(ins_ids.substring(1));
					templateBo.batchCompute(ins_ids.substring(1));
					autoCompute="true";
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return autoCompute;
	}

	private String analysisSignatureXml(ContentDAO dao,String signature,int signatureType) {
		RowSet rowSet=null;
		Document doc = null;
		try {
			doc = PubFunc.generateDom(signature);
			List<Element> elelist = doc.getRootElement().getChildren();
			for(int j = 0; j < elelist.size(); j++){
				Element ele = elelist.get(j);
				String documentid = ele.getAttributeValue("DocuemntID");
				if(signatureType==1){//BJCA
					if("BJCA".equals(documentid)){
						List<Element> list = ele.getChildren();
						for (int i = 0; i < list.size(); i++) {
							Element e = list.get(i);
							if("item".equals(e.getName())){
								String SignatureID = e.getAttributeValue("SignatureID");
								String delflag = e.getAttributeValue("delflag");
								if(delflag!=null&&"true".equals(delflag)){
									String sql = "delete from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+documentid+"'";
									dao.delete(sql, new ArrayList());
									File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+SignatureID+".gif");
									if (!tempFile.exists()) {
										continue;
									}
									tempFile.getAbsoluteFile().delete();
									ele.removeContent(e);
								}
								rowSet = dao.search("select * from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+documentid+"'");
								while(rowSet.next()){
									String username = rowSet.getString("username");
									username = username==null?"":username;
									e.setAttribute("UserName", username);
								}
							}
						}
					}
				}else if(signatureType==0||signatureType==3){//金格科技
					if(!"BJCA".equals(documentid)){
						List<Element> ele2list = ele.getChildren("item");
						if(ele2list!=null&&ele2list.size()>0){
							for(int k=0;k<ele2list.size();k++){
								Element ele2=(Element)ele2list.get(k);
								String SignatureID = ele2.getAttributeValue("SignatureID");
								rowSet = dao.search("select * from HTMLSignature where signatureid='"+SignatureID+"' and documentid='"+documentid+"'");
								if(rowSet.next()){
									String username = rowSet.getString("username");
									username = username==null?"":username;
									ele2.setAttribute("UserName", username);
								}else{
									String delflag = ele2.getAttributeValue("delflag");
									if("true".equals(delflag)) {

									}else {
										ele.removeContent(ele2);
									}
								}
							}
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		XMLOutputter outputter=new XMLOutputter();
		Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		return outputter.outputString(doc);
	}



	/**
	 * @Title: filterTemplateSetList
	 * @Description: 过滤掉无权限的指标
	 * @param @param templateSetList
	 * @param @return
	 * @return ArrayList
	 * @throws
	 */
	private ArrayList filterTemplateSetList(ArrayList cellList,HashMap filedPrivMap)
	{
		ArrayList fieldList = new ArrayList();
		for (int i = cellList.size() - 1; i >= 0; i--) {
			TemplateSet setBo = (TemplateSet) cellList.get(i);
			String fieldname = setBo.getTableFieldName();
			if ("signature".equals(fieldname)) {
				fieldList.add(setBo);
				continue;
			}
			if (setBo.isSubflag()) {//子集变化前也可以保存 wangrd 20160829 不区分权限了。
				fieldList.add(setBo);
				continue;
			}
			if ("".equals(fieldname)) {
				continue;
			}
			if (setBo.getChgstate() == 1 && (!"V".equals(setBo.getFlag()))) {// 变化前 临时变量
				continue;
			}
			if (filedPrivMap.get(setBo.getUniqueId()) != null) {
				String rwPriv = (String) filedPrivMap.get(setBo.getUniqueId());
				if (!"2".equals(rwPriv)) {
					continue;
				}
			}
			fieldList.add(setBo);
		}
		return fieldList;
	}

	private int getFormat(int templateSetFormat){
		int format = 0;
		try {
			switch (templateSetFormat) {
			case 6:
				format = 10;
				break;
			case 7:
				format = 10;
				break;
			case 8:
				format = 7;
				break;
			case 9:
				format = 7;
				break;
			case 10:
				format = 7;
				break;
			case 11:
				format = 7;
				break;
			case 12:
				format = 10;
				break;
			case 13:
				format = 7;
				break;
			case 14:
				format = 10;
				break;
			case 15:
				format = 7;
				break;
			case 16:
				format = 10;
				break;
			case 17:
				format = 7;
				break;
			case 19:
				format = 4;
				break;
			case 22:
				format = 7;
				break;
			case 23:
				format = 10;
				break;
			case 24:
				format = 10;
				break;
			case 25:
				format = 16;
				break;
			default:
				format = 10;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return format;
	}

    //--流程报批操作---------------------------------------------------------------------------------------------------------
    /**
     *
     * @param valueMap
     * {  “tabid”:”1”     //模板id
		  ."ins_id":"101"  //实例ID
		  ,"taskid":"20"   //任务ID
		   ,"moduleId":"9"     //模板号。自助服务 为9
		   ,"isWeiXin":"true"  //表示
		  ,"content":""  //审批意见
		  ,"opt":"1"  //报批类型  1：确定、继续报批 ；2 驳回； 3批准   4：同意
		  ,"actorid":""  // 手工报批时指定的审批人，暂不支持手工报批,自动审批模板此参数传空即可
		 }
		moduleId：  模块ID
	 * 1、人事异动
	 * 2、薪资管理
	 * 3、劳动合同
	 * 4、保险管理
	 * 5、出国管理
	 * 6、资格评审
	 * 7、机构管理
	 * 8、岗位管理
	 * 9、业务申请（自助）
	 * 10、考勤管理
	 * 11、职称评审
	 * 12、证照管理
     * @return success :成功 ，其它信息为保存不成功返回的错误信息
     */
    public String dealTask (HashMap valueMap) throws GeneralException
    {
    	String info="success";
    	try
    	{
   			String tabid=(String)valueMap.get("tabid");
   	    	String ins_id=(String)valueMap.get("ins_id");
   	    	String moduleId=(String)valueMap.get("moduleId");
   	    	if(StringUtils.isEmpty(ins_id))
   	    	{
   	    		ins_id="0";
   	    	}
   	    	String taskid=(String)valueMap.get("taskid");
   	    	if(StringUtils.isEmpty(taskid))
   	    	{
   	    		taskid="0";
   	    	}
   	    	String opt=(String)valueMap.get("opt");
   	    	String isValidate=(String)valueMap.get("isValidate");
   	    	if(!"true".equals(isValidate)){
				info = validateInfo(ins_id,taskid,opt,tabid,moduleId); //报批、批准前执行  计算、审核、 编制控制校验
				if(StringUtils.isNotBlank(info)){
					throw new GeneralException(info);
				}
   	    	}
			templateApply(valueMap);
    	}
    	catch(Exception ex)
   		{
			 String errorMsg=ex.toString();
	         int index_i=errorMsg.lastIndexOf("description:");
	         info=errorMsg.substring(index_i+12);
   		}
    	return info;
    }

    /**
     * 报批操作
     * @param valueMap
     * @throws GeneralException
     */
    public String templateApply(HashMap valueMap)throws GeneralException
    {
    	String message="success";
    	String moduleId=(String)valueMap.get("moduleId");
    	String taskId=(String)valueMap.get("taskid");
    	String tabId=(String)valueMap.get("tabid");
    	String flag = (String) valueMap.get("opt");//报批类型  1：确定、继续报批 ；2 驳回； 3批准   4：同意
    	String content= (String)valueMap.get("content");//审批内容

    	String specialOperate = (String) valueMap.get("specialOperate"); // 业务模板中人员需要报送给各自领导进行审批处理
    	String specialRoleUserStr = (String) valueMap.get("specialRoleUserStr"); // 特殊角色指定的用户
    	String pri = (String)valueMap.get("pri");//优先级
    	String actorType = (String)valueMap.get("actorType");//报送对象类型
    	String actorId = (String)valueMap.get("actorId"); //报送对象
//        String def_flow_self = (String)valueMap.get("def_flow_self");//是否自定义审批流程
        String reportObjectId = (String) valueMap.get("reportObjectId");// 抄送对象 格式： // ,1:Usr00000049
        String actorName = (String)valueMap.get("actorName");//报送对象名称
    	String sp_yj="01";

        TemplateFrontProperty frontProperty=new TemplateFrontProperty(new HashMap());
		frontProperty.setModuleId(moduleId);
		frontProperty.setTabId(tabId);
		frontProperty.setTaskId(taskId);

        TemplateBo templateBo = new TemplateBo(this.conn, this.userView, Integer.parseInt(tabId));
        TemplateParam paramBo = templateBo.getParamBo();
        /** 审批模式=0自动流转，=1手工指派 */
        int sp_mode =paramBo.getSp_mode();
        TemplateTableBo tablebo = new TemplateTableBo(this.conn,
        		Integer.parseInt(tabId), this.userView);
        tablebo.setBEmploy(frontProperty.isSelfApply());
        tablebo.setValidateM_L(true);//已校验过
        tablebo.setPcOrMobile("0");
        try {
        	if (reportObjectId == null) {
				reportObjectId = "";
			}
        	if (reportObjectId.length()>0){//抄送人员id加密了 ，需解密
        		String[] users=reportObjectId.split(",");
        		reportObjectId="";
        		for(int i=0;i<users.length;i++){
        			String strUser= users[i];
        			if(strUser!=null&&strUser.trim().length()>0){
        				String[] temps=strUser.split(":");
        				if (temps.length==2){
        					String userId= temps[1];//人员及用户、角色都需要解密
        					userId= PubFunc.decrypt(userId);
        					strUser=temps[0]+":"+userId;
        					if (reportObjectId.length()>0) {
								reportObjectId=reportObjectId+","+strUser;
							} else {
								reportObjectId=strUser;
							}
        				}
        			}
        		}
        	}

        	if (specialRoleUserStr == null) {
				specialRoleUserStr = "";
			}
        	if (actorId!=null && actorId.length()>0/* && !"2".equals(actorType)*/)//角色已经加密
        	{
        		if("3".equals(actorType)) //组织机构
        		{
        			String codesetid=actorId.substring(0,2);
        			String temp_str=actorId.substring(2);
        			actorId=codesetid+ PubFunc.decrypt(temp_str);
        		}
        		else {
					actorId = PubFunc.decrypt(actorId);
				}
        	}
        	if (content!=null){
        		content=SafeCode.decode(content.replace("\r\n", "<p>").replace(" ", "&nbsp;"));
        	}
        	else {
        		content="";
        	}
        	if (!"0".equals(taskId) && "".equals(content)){//非首次报批，审批意见为空则默认同意
        		content = ResourceFactory.getProperty("label.agree");
        		if ("2".equals(flag))// 如果是驳回
				{
					content=ResourceFactory.getProperty("label.nagree"); //不同意
				}
        	}
        	if (sp_mode==0){
        		if (this.userView.getStatus() == 0) {
        			actorId = this.userView.getUserName();
        			actorType = "4";
        		} else {
        			actorId = this.userView.getDbname() + this.userView.getA0100(); // this.userView.getUserName();
        			actorType = "1";
        		}
        		actorName= this.userView.getUserFullName();
        	}
        	WF_Actor wf_actor = new WF_Actor(actorId, actorType);
        	wf_actor.setContent(content);
        	wf_actor.setEmergency(pri);
        	wf_actor.setSp_yj(sp_yj);
        	wf_actor.setActorname(actorName);
        	if (sp_mode==0) {
        		wf_actor.setBexchange(false);
        	}
        	if (specialRoleUserStr.length() > 0)// 特殊角色
			{
				wf_actor.setSpecialRoleUserList(specialRoleUserStr);
			}

        	TemplateApplyBo  applyBo = new TemplateApplyBo(this.conn,this.userView,paramBo,frontProperty);
        	String srcTab =templateBo.getTableName(moduleId,Integer.parseInt(tabId), taskId);
        	if ("0".equals(taskId)) {
        		//报批操作
        		firstAppeal(wf_actor, frontProperty, paramBo, tablebo, reportObjectId, specialOperate, applyBo, srcTab);
        	} else {
        		//是否是自定义审批过程 0未勾选 1 勾选未定义(起草判断) 2 勾选定义了
        		String def_flow_self =paramBo.getDef_flow_self();
        		if ("1".equals(def_flow_self)){
        			String[] tasklist=StringUtils.split(taskId,",");
        			for(int i=0;i<tasklist.length;i++){
        				if (paramBo.isDef_flow_self(Integer.parseInt(tasklist[i]))){
                            def_flow_self="2";
                            break;
                        }
        			}
        		}
        		message=autoAppeal(wf_actor, flag, def_flow_self, templateBo, paramBo, tablebo, reportObjectId, specialOperate, tabId, taskId, applyBo);
        	}
        } catch (Exception ex) {
        	ex.printStackTrace();
        	message = ex.toString();
        	if (message.indexOf("最大") != -1 && message.indexOf("8060") != -1 && Sql_switcher.searchDbServer() == 1) {
        		PubFunc.resolve8060(this.conn, "templet_" + tabId);
        		message="请重新执行报批操作!";
        	}
        	throw new GeneralException(message);
        }
        return message;
    }
    public String autoAppeal(WF_Actor wf_actor,
    		String flag, String def_flow_self, TemplateBo templateBo,
    		TemplateParam paramBo, TemplateTableBo tablebo, String reportObjectId,  String specialOperate,String tabId,String taskId, TemplateApplyBo applyBo) {
    	String message="success";
    	RowSet rs=null;
    	try {
			String url_s = this.userView.getServerurl();
			String pri=wf_actor.getEmergency();
			String actorName=wf_actor.getActorname();
			String content=wf_actor.getContent();
			// 3:邮件、消息 // //  // 2：消息// // 1：邮件
			String isSendMessage="0";
			if(paramBo.isBemail()&&paramBo.isBsms()) {
				isSendMessage="3";
			} else if(paramBo.isBemail()) {
				isSendMessage="1";
			} else if(paramBo.isBsms()) {
				isSendMessage="2";
			}
			if(!this.userView.hasTheFunction("2701515")&&!this.userView.hasTheFunction("0C34815")&&!this.userView.hasTheFunction("400040115")
					&&!this.userView.hasTheFunction("32015")&&!this.userView.hasTheFunction("325010115")&&!this.userView.hasTheFunction("324010115")&&!this.userView.hasTheFunction("010701")&&!this.userView.hasTheFunction("32115")&&!this.userView.hasTheFunction("3800715")) {
				isSendMessage="0";
			}

			String sp_yj=wf_actor.getSp_yj();
			boolean emailSelf = paramBo.isEmail_staff();// 是否通知本人
			// 审批
			//审批之前判断是不是同一个单据的  如果是true 需要复制流程
			ContentDAO dao = new ContentDAO(this.conn);
			applyBo.setApplyFlag(flag);
			String taskId_copy=taskId;
			taskId = applyBo.validateIsFromOne(taskId);
			ArrayList tasklist = getTaskList(flag,templateBo,tabId,taskId,taskId_copy);
			WF_Instance ins = new WF_Instance(tablebo, this.conn);// 因为驳回任务（rejectTask()是在WF_Instance这个类里写的。所以要初始化ins这个对象）
			ins.setSpecialOperate(specialOperate);
			// 邮件抄送
			if (isSendMessage != null && !"0".equals(isSendMessage)) {
				ins.setEmail_staff_value(emailSelf?"1":"0");
				ins.setUser_h_s(reportObjectId);
				ins.setIsSendMessage(isSendMessage);
			} else {
				ins.setUser_h_s(reportObjectId);
			}
			HashMap otherParaMap = new HashMap();
			ins.setUrl_s(url_s);
			/** 审批模式=0自动流转，=1手工指派 */
			int sp_mode =paramBo.getSp_mode();
			/** 支持多任务审批 */
			for (int i = 0; i < tasklist.size(); i++)// 循环每一个任务
			{
				boolean isEnd = false;
				RecordVo ins_vo = new RecordVo("t_wf_instance");
				String ins_id = ((RecordVo) tasklist.get(i)).getString("ins_id");
				String taskid = ((RecordVo) tasklist.get(i)).getString("task_id");
				// 检查当前任务的活动状态是否是结束状态
				rs = dao.search("select count(*) from t_wf_task where ( task_state='5' or task_state='4' ) and task_id=" + taskid);
				if (rs.next()) {
					if (rs.getInt(1) > 0) {
						throw GeneralExceptionHandler.Handle(new Exception("当前单据已被处理,操作失败"));
					}
				}
				// 待办信息
				String pendingCode = "HRMS-" + PubFunc.encrypt(taskid);
				otherParaMap.put("pre_pendingID", pendingCode);
				ins.setOtherParaMap(otherParaMap);
				ins.setObjs_sql(ins.getObjsSql(Integer.parseInt(ins_id), Integer.parseInt(taskid), 3, tabId, this.userView, ""));// 作用是
				ins_vo.setInt("ins_id", Integer.parseInt(ins_id));
				tablebo.setIns_id(Integer.parseInt(ins_id));
				/** 驳回意见 */
				ins.setIns_id(Integer.parseInt(ins_id));
				if (sp_mode==0) {//自动审批
					if ("2".equals(flag))// 如果是驳回
					{
						wf_actor.setSp_yj("02");
						wf_actor.setBexchange(true);
						String reject_type = paramBo.getReject_type();// =1 or null：逐级驳回 // =2：驳回到发起人
						if ("2".equalsIgnoreCase(reject_type)) {// 驳回到发起人
							TemplateStaticDataBo.removeEleFromBeginCountMap(ins_id);//清除掉缓存中的相关记录。
							ins.rejectTaskToSponsor(ins_vo, wf_actor, Integer.parseInt(taskid), this.userView);// 此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
						} else {// 逐级驳回
							ins.rejectTask(ins_vo, wf_actor, Integer.parseInt(taskid), this.userView);// 此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
						}
						ins.updateApproveOpinion(ins_vo, wf_actor, this.userView, Integer.parseInt(taskid));
						TemplateInterceptorAdapter.afterHandle(Integer.parseInt(taskid),0,tablebo.getTabid(),null,"reject",this.userView);
					} else{// 如果不是驳回
						/**填充起始节点标识，此申请是否为当前用户*/
						ins.createNextTask(ins_vo, wf_actor, Integer.parseInt(taskid), this.userView);// 在这个函数里面执行了expDataIntoArchive()
						ins_vo =dao.findByPrimaryKey(ins_vo);//重新获取vo lis 21060825
						// 提交时，将个人附件归档
						if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){//下一个流程结点是结束结点
							TemplateInterceptorAdapter.afterHandle(0,ins_vo.getInt("ins_id"),tablebo.getTabid(),paramBo,"submit",this.userView);
							applyBo.submitAttachmentFile(ins_vo.getString("ins_id"), tablebo, "1", tabId);
							applyBo.SendEmailToBeginUser(tablebo, ins_vo, dao, ins, tabId);
							TempletChgLogBo chgLogBo=new TempletChgLogBo(this.conn,this.userView,paramBo);
							chgLogBo.insertChangeInfoToYearTable(ins_vo.getString("ins_id"));//提交时把变动日志更新到年度表中
							TemplateStaticDataBo.removeEleFromBeginCountMap(ins_id);//任务结束清除缓存中保存的考试节点数目的数据。
						}
						else //相邻节点如果是自己审批则自动执行批准操作  20180822 dengcan
						{
							ins.updateApproveOpinion(ins_vo, wf_actor, this.userView, Integer.parseInt(taskid));
							TemplateInterceptorAdapter.afterHandle(Integer.parseInt(taskid),0,tablebo.getTabid(),null,"appeal",this.userView);
							ins_vo= applyBo.autoApplyTask(ins,ins_vo,content,pri,sp_yj,actorName,otherParaMap,tablebo); //自动执行批准操作

							// 提交时，将个人附件归档
							if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){//下一个流程结点是结束结点
								TemplateInterceptorAdapter.afterHandle(0,ins_vo.getInt("ins_id"),tablebo.getTabid(),paramBo,"submit",this.userView);
								applyBo.submitAttachmentFile(ins_vo.getString("ins_id"), tablebo, "1", tabId);
								applyBo.SendEmailToBeginUser(tablebo, ins_vo, dao, ins, tabId);
								TempletChgLogBo chgLogBo=new TempletChgLogBo(this.conn,this.userView,paramBo);
								chgLogBo.insertChangeInfoToYearTable(ins_vo.getString("ins_id"));//提交时把变动日志更新到年度表中
								TemplateStaticDataBo.removeEleFromBeginCountMap(ins_id);//任务结束清除缓存中保存的考试节点数目的数据。
							}
						}
					}
				} else {//手工审批
					//手工审批 是否自定义审批流程
					boolean isTheEndSp = false;//是否是自定义审批流程的最后一级审批人
					if("2".equals(def_flow_self)&&!"0".equals(taskid)){
						isTheEndSp = ins.isEndNode(Integer.parseInt(taskid),tablebo);
					}
					if("2".equals(def_flow_self)&&isTheEndSp&&"1".equals(flag)) {
						flag="3";
					}
					if ("1".equals(flag)){ // 重新分配
						if (ins.reAssignTask(ins_vo, wf_actor, Integer.parseInt(taskid), this.userView)) {
						}
					} else if ("2".equals(flag)){// 驳回重审,把任务指给上次发送过的人
						wf_actor.setSp_yj("02");
						String reject_type = paramBo.getReject_type();// =1 or null：逐级驳回 // =2：驳回到发起人
						if ("2".equalsIgnoreCase(reject_type)) {// 驳回到发起人
							TemplateStaticDataBo.removeEleFromBeginCountMap(ins_id);
							ins.rejectTaskToSponsor(ins_vo, wf_actor, Integer.parseInt(taskid), this.userView);// 此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
						} else {// 逐级驳回
							ins.rejectTask(ins_vo, wf_actor, Integer.parseInt(taskid), this.userView);// 此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
						}
						TemplateInterceptorAdapter.afterHandle(Integer.parseInt(taskid),0,tablebo.getTabid(),null,"reject",this.userView);
					} else {// 批准，最后数据提交到档案库中去
						isEnd = true;
						/** 员工自助申请 */
						ArrayList fieldlist = templateBo.getAllFieldItem();//不对 todo
						//将校验主集指标是否有写权限提到前面来了
						HashMap subhm=tablebo.readUpdatesSetField(fieldlist);
						if(tablebo.getOperationtype()==0&&subhm.get("A01")==null)//=1:人员调出
						{
							throw new GeneralException(ResourceFactory.getProperty("error.input.a01read"));
						}
						if (ins.finishTask(ins_vo, wf_actor, Integer.parseInt(taskid), this.userView, "5")) {
							if (ins.getTask_vo().getInt("task_id") != 0) // //往考勤申请单中写入记录
							{
								StringBuffer strsql = new StringBuffer("");
								strsql.append("select * from templet_" + tabId);
								strsql.append(" where  seqnum in  (select  seqnum  from t_wf_task_objlink where   ");
								strsql.append("   task_id=" + ins.getTask_vo().getInt("task_id") + " and tab_id=" + tabId + " and state=1 )   ");

								String operState = "03";
								if (!("01").equals(wf_actor.getSp_yj())) {
									operState = "07";
								}

								ins.insertKqApplyTable(strsql.toString(), tabId, "0", operState, "templet_" + tabId); // 往考勤申请单中写入报批记录
								//20190731
								TemplateInterceptorAdapter.preHandle("templet_"+tabId,Integer.parseInt(tabId),ins.getTask_vo().getInt("task_id") , paramBo, "submit", this.userView,"");
							}
							boolean bhave = ins.isHaveObjTheTask(Integer.parseInt(taskid));
							tablebo.setSp_yj(wf_actor.getSp_yj());
							if (tablebo.getInfor_type() == 1) {// 如果是人员
								ins.resetDbpre("templet_" + tabId, tablebo, taskid);
							}
							ins.updateApproveOpinion(ins_vo, wf_actor, this.userView, Integer.parseInt(taskid));
							tablebo.expDataIntoArchive(ins.getTask_vo().getInt("task_id"));
							//20190731
							TemplateInterceptorAdapter.afterHandle(ins.getTask_vo().getInt("task_id"),0,tablebo.getTabid(),null,"submit",this.userView);
							/////////////////提交时，将个人附件归档///////////////
							applyBo.submitAttachmentFile(ins_vo.getString("ins_id"), tablebo, "1", tabId);
							TempletChgLogBo chgLogBo=new TempletChgLogBo(this.conn,this.userView,paramBo);
							chgLogBo.insertChangeInfoToYearTable(ins_vo.getString("ins_id"));
							try {
								StringBuffer buf = new StringBuffer();
								/** 如果当前流程实例中存在正在运行中的任务，重新把实例置为运行状态 */
								if (ins.isHaveRuningTask(ins_vo.getInt("ins_id")) || bhave) {
									buf.append("update t_wf_instance set end_date=null,finished='2' where ins_id=");
									buf.append(ins_vo.getInt("ins_id"));
									dao.update(buf.toString());
									// xcs modify @ 2014-4-1
									buf.setLength(0);
									buf.append("update t_wf_task set flag=1");
									buf.append(" where task_id=");
									buf.append(ins.getTask_vo().getInt("task_id")/* taskid */);
									dao.update(buf.toString());
								} else {
									buf.setLength(0);
									buf.append("update t_wf_task set flag=1");
									buf.append(" where task_id=");
									buf.append(ins.getTask_vo().getInt("task_id")/* taskid */);
									dao.update(buf.toString());
									applyBo.SendEmailToBeginUser(tablebo, ins_vo, dao, ins, tabId);
								}
								TemplateStaticDataBo.removeEleFromBeginCountMap(ins_id);//任务结束清除缓存中保存的考试节点数目的数据。
							} catch (Exception ex) {
								ex.printStackTrace();
								throw GeneralExceptionHandler.Handle(ex);
							}
							SendMessageBo bo = new SendMessageBo(this.conn, this.userView);
							bo.sendMessageToOa(tabId);
						}
					}
					//bug32669 填写审批意见移到创建流程之后 liuyz
					if(!isEnd) {
						ins.updateApproveOpinion(ins_vo, wf_actor, this.userView, Integer.parseInt(taskid));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
        	message = ex.toString();
        	if (message.indexOf("最大") != -1 && message.indexOf("8060") != -1 && Sql_switcher.searchDbServer() == 1) {
        		PubFunc.resolve8060(this.conn, "templet_" + tabId);
        		message="请重新执行报批操作!";
        	}
		}finally{
			PubFunc.closeDbObj(rs);
		}
    	return message;
    }

    /**
     * 功能：报批操作。
     * @param wf_actor
     * @param frontProperty
     * @param paramBo
     * @param tablebo
     * @param reportObjectId
     * @param specialOperate
     * @param applyBo
     * @param srcTab
     * @throws GeneralException
     */
    public void firstAppeal(WF_Actor wf_actor,  TemplateFrontProperty frontProperty,
    		TemplateParam paramBo, TemplateTableBo tablebo,String reportObjectId, String specialOperate,
    		TemplateApplyBo applyBo,String srcTab)
    				throws GeneralException {
    	String url_s = this.userView.getServerurl();
    	String pri=wf_actor.getEmergency();
    	String actorName=wf_actor.getActorname();
    	String content=wf_actor.getContent();
    	// 3:邮件、消息 // //  // 2：消息// // 1：邮件
    	String isSendMessage="0";
    	if(paramBo.isBemail()&&paramBo.isBsms()) {
			isSendMessage="3";
		} else if(paramBo.isBemail()) {
			isSendMessage="1";
		} else if(paramBo.isBsms()) {
			isSendMessage="2";
		}
    	if(!this.userView.hasTheFunction("2701515")&&!this.userView.hasTheFunction("0C34815")&&!this.userView.hasTheFunction("400040115")
    			&&!this.userView.hasTheFunction("32015")&&!this.userView.hasTheFunction("325010115")&&!this.userView.hasTheFunction("324010115")&&!this.userView.hasTheFunction("010701")&&!this.userView.hasTheFunction("32115")&&!this.userView.hasTheFunction("3800715")) {
			isSendMessage="0";
		}

    	String sp_yj=wf_actor.getSp_yj();
    	boolean emailSelf = paramBo.isEmail_staff();// 是否通知本人
    	String moduleId=frontProperty.getModuleId();
    	String taskId=frontProperty.getTaskId();
    	String tabId=frontProperty.getTabId();
    	boolean bSelfApply = frontProperty.isSelfApply();
    	String selfapply = "0";
    	if (frontProperty.isSelfApply()){
    		selfapply="1";
    	}
    	// 报批
    	ArrayList whlList = new ArrayList();
    	if (!frontProperty.isSelfApply()) { // 非自助申请，判断是否拆单
    		{
    			whlList = tablebo.getSplitInstanceWhl();
    			if (whlList.size() == 1 && ((String) whlList.get(0)).trim().length() == 0)// 不拆单
    			{
    				;
    			} else{
    				if(!(wf_actor.getSpecialRoleUserList()!=null&&wf_actor.getSpecialRoleUserList().size()>0)) {
						wf_actor.setSpecialRoleUserList(new ArrayList());
					}
    			}
    		}
    	}
    	else {
    		whlList.add("");
    	}
    	ArrayList personlist =applyBo.getPersonlist(paramBo.getInfor_type(),srcTab);
    	for (int i = 0; i < whlList.size(); i++) {
    		RecordVo ins_vo = new RecordVo("t_wf_instance");
    		WF_Instance ins = new WF_Instance(tablebo, this.conn);
    		ins.setbSelfApply(bSelfApply);
    		ins.setUrl_s(url_s);
    		ins.setSpecialOperate(specialOperate);
    		ins.setModuleId(moduleId);
    		String whl = (String) whlList.get(i);
    		if ("1".equalsIgnoreCase(selfapply)) {
				ins.setObjs_sql(ins.getObjsSql(0, 0, 1, tabId, this.userView, ""));
			} else {
				ins.setObjs_sql(ins.getObjsSql(0, 0, 2, tabId, this.userView, whl));
			}
    		// 邮件抄送
    		if (isSendMessage != null && !"0".equals(isSendMessage)) {//是否抄送本人
    			ins.setIsSendMessage(isSendMessage);
    			ins.setEmail_staff_value(emailSelf?"1":"0"); // 通知本人
    		}
    		ins.setUser_h_s(reportObjectId); // 抄送人员
    		if (ins.createInstance(ins_vo, wf_actor, whl)){
    			//将数据提交到审批表
    			applyBo.saveSubmitTemplateData(tablebo,ins_vo,wf_actor,whl,selfapply);
    			int ins_id = ins_vo.getInt("ins_id");
    			//通过whl得到对应的人员
    			ArrayList personlist_ = applyBo.getPersonlist(paramBo.getInfor_type(),ins_id,tabId,whl);
    			TempletChgLogBo chglogBo=new TempletChgLogBo(conn, userView,paramBo);
    			chglogBo.updateChangeInfoAddIns_id(personlist_, taskId, tabId, String.valueOf(ins_id),paramBo.getInfor_type());//提交时把变动日志更新到年度表中
    			// 把附件增加到流程中 应该放在saveSubmitTemplateData里面放在一起 todo
    			applyBo.transAttachmentFile(srcTab,String.valueOf(ins_id),personlist_);
    			//发送信息给OA
    			// 个人附件归档
    			if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){//下一个流程结点是结束结点
    				applyBo.submitAttachmentFile(String.valueOf(ins_id), tablebo, "1", tabId);
    				TemplateInterceptorAdapter.afterHandle(0,ins_id,tablebo.getTabid(),paramBo,"submit",this.userView);
    			}
    			else //相邻节点如果是自己审批则自动执行批准操作  20180822 dengcan
    			{
    				ins_vo=applyBo.autoApplyTask(ins,ins_vo,content,pri,sp_yj,actorName,new HashMap(),tablebo); //自动执行批准操作
    				// 提交时，将个人附件归档
    				if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){//下一个流程结点是结束结点
    					applyBo.submitAttachmentFile(String.valueOf(ins_id), tablebo, "1", tabId);
    					TemplateInterceptorAdapter.afterHandle(0,ins_id,tablebo.getTabid(),paramBo,"submit",this.userView);
    				}
    				else
    				{
    					TemplateInterceptorAdapter.afterHandle(0,ins_id,tablebo.getTabid(),paramBo,"apply",this.userView);
    				}
    			}
    		}
    	}
    	//删除附件
    	applyBo.deleteAttachmentFile(srcTab,personlist);
    }

        /**
         * @Title: getTaskList
         * @Description: 过滤没有选中的单据
         * @param @param flag
         * @param @param templateBo
         * @param @param tabId
         * @param @param task_id
         * @param @return
         * @param @throws GeneralException
         * @return ArrayList
         * @throws
        */
        private ArrayList getTaskList(String flag ,TemplateBo templateBo, String tabId, String task_id,String task_Id_old) throws GeneralException {
            ArrayList tasklist = new ArrayList();
            ArrayList tmpTasklist = new ArrayList();
            String[] lists = StringUtils.split(task_id, ",");
            StringBuffer strsql = new StringBuffer();
            strsql.append("select * from t_wf_task where task_id in (");
            HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
            for (int i = 0; i < lists.length; i++) {
                if (i != 0) {
					strsql.append(",");
				}
                strsql.append(lists[i]);
            }
            strsql.append(")");
            try {
                ContentDAO dao = new ContentDAO(this.conn);
                RowSet rset = dao.search(strsql.toString());
                while (rset.next()) {
                    RecordVo taskvo = new RecordVo("t_wf_task");
                    taskvo.setInt("task_id", rset.getInt("task_id"));
                    taskvo.setInt("ins_id", rset.getInt("ins_id"));
                    tmpTasklist.add(taskvo);
                }
                // 过滤单据：去除未选中的单据
                for (int i = 0; i < tmpTasklist.size(); i++) {
                    RecordVo taskvo = (RecordVo) tmpTasklist.get(i);
                    String taskid = taskvo.getString("task_id");
                    if (isSelectedTaskId(dao, tabId, taskid)) {
                        // 判断当前任务节点是不是初始节点
                        // =1为初始节点，=0不是初始节点，对于批量审批而已，有一个不是初始节点的单据，那么startflag=1
                         if("2".equals(flag)) //把单据是发起节点且要驳回的单据记录下来。
                         {
                            String startflag = templateBo.isStartNode(taskid);
                            if ("1".equals(startflag)){
                                continue;
                            }
                         }
                         tasklist.add(taskvo);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw GeneralExceptionHandler.Handle(ex);
            }
            return tasklist;
        }
        private boolean isSelectedTaskId(ContentDAO dao, String tabId, String task_id) throws GeneralException {
            boolean b = true;
            RowSet rs2=null;
            try {
                String sqlstr = "select count(*) from templet_" + tabId + " where  seqnum in  (select seqnum  from t_wf_task_objlink where  task_id=" + task_id + "   and submitflag=1  and (state is null or  state=0 ) and (" + Sql_switcher.isnull("special_node", "0") + "=0  or ( " + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
                rs2 = dao.search(sqlstr);
                if (rs2.next()) {
                    if (rs2.getInt(1) == 0) {
						b = false;
					}
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                throw GeneralExceptionHandler.Handle(ex);
            }
            return b;
        }

    /**自助平台校验必填项  报错信息不再抛出到后台
     * @param tablebo
     * @param taskList
     * @param frontProperty
     * @throws GeneralException
     */
    public void  validateMustFillItem(TemplateTableBo tablebo,ArrayList taskList, TemplateFrontProperty frontProperty) throws GeneralException
    {
    	TemplateUtilBo utilBo= new TemplateUtilBo(conn,this.userView);
    	String tabName=utilBo.getTableName(frontProperty.getModuleId(),
    			Integer.parseInt(frontProperty.getTabId()), frontProperty.getTaskId());
    	boolean bSelfApply = frontProperty.isSelfApply();
    	TemplateSelfApplyBo templateSelfApplyBo=new TemplateSelfApplyBo(conn, Integer.valueOf(frontProperty.getTabId()), userView);
    	if (bSelfApply){
    		templateSelfApplyBo.setBEmploy(true);
    	}
    	ArrayList fieldlist = templateSelfApplyBo.getAllFieldItem("0");//liuyz 30408 手机模版和非手机模版子集有相同子集造成子集必填判断不正确。
    	if(taskList.size()>0)
    	{
    		if(taskList.size()==1&& "0".equals(((RecordVo)taskList.get(0)).getString("task_id"))) {
				templateSelfApplyBo.checkMustFillItem(tabName, fieldlist,0);
			} else
    		{
    			ArrayList taskidsList=splitTaskByNode(taskList);//获得不同审批节点下的任务号
    			for (int i = 0; i < taskidsList.size(); i++) {

    				String task_ids= (String)taskidsList.get(i);
    				templateSelfApplyBo.checkMustFillItem_batch(tabName, fieldlist,task_ids);
    			}
    		}
    	}
    }
    /**
     * 获得不同审批节点下的任务号
     * @param taskList
     * @return
     */
    private ArrayList splitTaskByNode(ArrayList taskList)throws GeneralException
    {
    	ArrayList taskids=new ArrayList();
    	try {
    		ContentDAO dao=new ContentDAO(this.conn);
	    	StringBuffer task_ids=new StringBuffer("");
	    	for (int i = 0; i < taskList.size(); i++) {
	            String task_id=((RecordVo)taskList.get(i)).getString("task_id");
	            task_ids.append(","+task_id);
	        }
	    	RowSet rowSet=dao.search("select node_id,task_id from t_wf_task where task_id in ("+task_ids.substring(1)+") order by node_id");
	    	String oldNode="";
	    	String oldTaskId="";
	    	while(rowSet.next())
	    	{
	    		String node_id=rowSet.getString("node_id");
	    		String task_id=rowSet.getString("task_id");
	    		if(oldNode.length()==0) {
					oldNode=node_id;
				}

	    		if(!oldNode.equals(node_id))
	    		{
	    			taskids.add(oldTaskId.substring(1));
	    			oldTaskId="";
	    		}
	    		oldTaskId+=","+task_id;
	    	}
	    	taskids.add(oldTaskId.substring(1));

    	 } catch (Exception ex) {
             ex.printStackTrace();
         }
    	 return taskids;
    }

    /**校验审核公式
     * @param templateBo
     * @param taskList
     * @param frontProperty
     * @throws GeneralException
     */
    public void  checkLogicExpress(TemplateBo templateBo,ArrayList taskList, TemplateFrontProperty frontProperty) throws GeneralException
    {
    	TemplateUtilBo utilBo= new TemplateUtilBo(conn,this.userView);
    	boolean bSelfApply = frontProperty.isSelfApply();
    	if(taskList.size()>0)
    	{
    		if(taskList.size()==1&&("0".equals(((RecordVo)taskList.get(0)).getString("task_id"))||bSelfApply))
    		{
    			templateBo.checkLogicExpress(0);
    		}
    		else
    		{
    			String taskids="";
    			for (int i = 0; i < taskList.size(); i++) {
    				String task_id=((RecordVo)taskList.get(i)).getString("task_id");
    				String noCheckTemplateIds = SystemConfig.getPropertyValue("noCheckTemplateIds"); //system.properties -->  noCheckTemplateIds=12,88
    				if (noCheckTemplateIds != null && Integer.parseInt(task_id) > 0 && ("," + noCheckTemplateIds + ",").indexOf("," + templateBo.getParamBo().getTabId() + ",") != -1) {

    				}
    				else {
						taskids+=","+task_id;
					}
    			}
    			if(taskids.length()>0) {
					templateBo.checkLogicExpress_batch(taskids.substring(1));
				}
    		}
    	}
    }
    /**
	 * 合并 和 划转业务 需判断同一组中的记录是否都被选中，同时去掉没有指定（划转|合并）目标记录的选中标记
	 * @param sql
     * @param paramBo
	 * @throws GeneralException
     * @throws SQLException
	 */
	public void checkSelectedRule(String sql,String srctab,String task_id, TemplateParam paramBo)throws GeneralException, SQLException
	{
		ContentDAO dao=new ContentDAO(this.conn);
		HashSet set=new HashSet();
		RowSet rowSet=dao.search(sql);
		if(paramBo.getInfor_type()==2||(paramBo.getInfor_type()==3&&paramBo.getOperationType()==8))
		{
			while(rowSet.next())
			{
				if(rowSet.getString("to_id")!=null) {
					set.add(rowSet.getString("to_id"));
				}
			}
			String to_ids="";
			for(Iterator t=set.iterator();t.hasNext();) {
				to_ids+=",'"+(String)t.next()+"'";
			}
			if(to_ids.length()==0)
			{
				String desc="合并";
				String target = "机构";
				if(paramBo.getInfor_type()==3) {
					target = "岗位";
				}
				if(paramBo.getOperationType()==9) {
					desc="划转";
				}
				//throw GeneralExceptionHandler.Handle(new Exception("没有选中符合业务要求"+desc+"的记录!"));
				throw GeneralExceptionHandler.Handle(new Exception("请选择需要"+desc+"的目标"+target+"！"));
			}
			if(task_id==null||task_id.trim().length()==0|| "0".equals(task_id.trim())) {
				rowSet=dao.search("select count(*) from "+srctab+" where to_id in ("+to_ids.substring(1)+") and ( submitflag is null or submitflag=0 )");
			} else{
				StringBuffer strsql = new StringBuffer();
				strsql.append("select count(*) from "+srctab+" where  to_id in ("+to_ids.substring(1)+") ");
				strsql.append(" and  exists (select null from t_wf_task_objlink where "+srctab+".seqnum=t_wf_task_objlink.seqnum and "+srctab+".ins_id=t_wf_task_objlink.ins_id ");
				strsql.append("  and task_id="+task_id+"   and ( submitflag is null or submitflag=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ");
				rowSet=dao.search(strsql.toString());

			}
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0)
				{
					if(paramBo.getOperationType()==8) {
						throw GeneralExceptionHandler.Handle(new Exception("合并业务同组内的记录需都被选中!"));
					}
					if(paramBo.getOperationType()==9) {
						throw GeneralExceptionHandler.Handle(new Exception("划转业务同组内的记录需都被选中!"));
					}
				}
			}
			//抛出同一组号只有一条数据的记录
			for(Iterator t=set.iterator();t.hasNext();){
				String to_id="'"+(String)t.next()+"'";
				if(task_id==null||task_id.trim().length()==0|| "0".equals(task_id.trim())) {
					rowSet=dao.search("select count(*) from "+srctab+" where to_id in ("+to_id+") ");
				} else{
					StringBuffer strsql = new StringBuffer();
					strsql.append("select count(*) from "+srctab+" where  to_id in ("+to_id+") ");
					strsql.append(" and  exists (select null from t_wf_task_objlink where "+srctab+".seqnum=t_wf_task_objlink.seqnum and "+srctab+".ins_id=t_wf_task_objlink.ins_id ");
					strsql.append("  and task_id="+task_id+"  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )  ) ");
					rowSet=dao.search(strsql.toString());
				}

				if(rowSet.next())
				{
					if(rowSet.getInt(1)<2)
					{
						if(paramBo.getOperationType()==8) {
							throw GeneralExceptionHandler.Handle(new Exception("合并业务同组内的记录仅一条数据不允许操作!"));
						}
						if(paramBo.getOperationType()==9) {
							throw GeneralExceptionHandler.Handle(new Exception("划转业务同组内的记录仅一条数据不允许操作!"));
						}
					}
				}
			}
			//去掉没有指定（划转|合并）目标记录的选中标记
			dao.update("update "+srctab+" set submitflag=0 where to_id is null or to_id=''");
			if(rowSet!=null) {
				rowSet.close();
			}
		}
		else if(paramBo.getInfor_type()==3&&paramBo.getOperationType()==9)
		{
			int num=0;
			while(rowSet.next())
			{
				num++;
				if(rowSet.getString("parentid_2")!=null) {
					set.add(rowSet.getString("parentid_2"));
				}
			}
			String to_ids="";
			for(Iterator t=set.iterator();t.hasNext();) {
				to_ids+=",'"+(String)t.next()+"'";
			}
			if(num>0&&to_ids.length()==0)
			{
				String	desc="划转";
				throw GeneralExceptionHandler.Handle(new Exception("没有选中符合业务要求"+desc+"的记录!"));

			}

			//去掉没有指定（划转|合并）目标记录的选中标记
			dao.update("update "+srctab+" set submitflag=0 where parentid_2 is null or parentid_2=''");
			if(rowSet!=null) {
				rowSet.close();
			}
		}
	}

	/**
	 * 校验新建组织单元模板系统指标是否建立，及内容是否有填写
	 * @param sql
	 * @throws GeneralException
	 */
	public void checkNewOrgFillItem(TemplateTableBo tablebo, String sql,
			TemplateParam paramBo)throws GeneralException
	{
		RowSet rowSet=null;
		RowSet rowSet2=null;
		RowSet rowSet3=null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			 rowSet=dao.search(sql);
			 rowSet2=null;
			 ResultSetMetaData   rsmd = rowSet.getMetaData();
			 HashMap columnMap=new HashMap();
			 String codename=",";
				int columnCount  =  rsmd.getColumnCount();//得到列数
					String[] temp3=new String[columnCount];
					for(int i=0;i<temp3.length;i++)
					{
							codename+=rsmd.getColumnName(i+1).trim().toLowerCase()+",";
							columnMap.put(rsmd.getColumnName(i+1).trim().toLowerCase(), "1");
					}

			tablebo.validateSysItem(columnMap);
			StringBuffer strInfo=new StringBuffer();//存储为空的信息
			strInfo.append("下列信息不能为空,请填写完整!\n\r");
			StringBuffer strInfo2=new StringBuffer();//存储有问题的信息
			strInfo2.append("下列信息填写有问题!\n\r");
			StringBuffer errorInfo=new StringBuffer("");
			StringBuffer errorInfo2=new StringBuffer("");
			boolean flag=false;
			HashMap map = tablebo.getBKmap();
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next())
			{
				errorInfo2.setLength(0);
				String codeitemdesc="";
				if(paramBo.getOperationType()==5) {
					codeitemdesc=rowSet.getString("codeitemdesc_2");
				} else {
					codeitemdesc=rowSet.getString("codeitemdesc_1");
				}
				errorInfo.setLength(0);

				if(paramBo.getInfor_type()==2&&paramBo.getOperationType()==5)//如果是单位
				{
					if(rowSet.getString("codesetid_2")==null||rowSet.getString("codesetid_2").trim().length()==0) {
						errorInfo.append("拟[组织单元类型]不能为空,");
					}
				}
				if(paramBo.getInfor_type()==2&&paramBo.getOperationType()==5)//xgq
				{
					if(rowSet.getString("codeitemdesc_2")==null||rowSet.getString("codeitemdesc_2").trim().length()==0|| "--".equals(rowSet.getString("codeitemdesc_2").trim())) {
						errorInfo.append("拟[组织单元名称]不能为空,");
					}
				}
				if(paramBo.getInfor_type()==3&&paramBo.getOperationType()==5)//如果是岗位
				{
					if(rowSet.getString("codeitemdesc_2")==null||rowSet.getString("codeitemdesc_2").trim().length()==0|| "--".equals(rowSet.getString("codeitemdesc_2").trim())) {
						errorInfo.append("拟[职务名称]不能为空,");
					}
				}
				if((paramBo.getOperationType()==5)&&(rowSet.getString("parentid_2")==null||rowSet.getString("parentid_2").trim().length()==0)){
					if(paramBo.getInfor_type()==2&&rowSet.getString("codesetid_2")!=null&& "UN".equals(rowSet.getString("codesetid_2"))){//只有(新建)单位才能在组织机构下建

					}else {
						errorInfo.append("拟[上级组织单元]不能为空,");
					}
				}
				if(paramBo.getOperationType()!=6&&paramBo.getOperationType()!=10)
				{
					if(rowSet.getDate("start_date_2")==null||rowSet.getDate("start_date_2").toString().trim().length()==0) {
						errorInfo.append("拟[生效日期]不能为空,");
					}
				}

				else if(paramBo.getOperationType()==6&&codename.indexOf(",codeitemdesc_2,")!=-1)
				{
					if(rowSet.getString("codeitemdesc_2")==null||rowSet.getString("codeitemdesc_2").trim().length()==0|| "--".equals(rowSet.getString("codeitemdesc_2").trim()))
					{
						if(paramBo.getInfor_type()==2) {
							errorInfo.append("拟[组织单元名称]不能为空,");
						} else if(paramBo.getInfor_type()==3) {
							errorInfo.append("拟[岗位名称]不能为空,");
						}


					}
				}

				if(errorInfo.length()>0)
				{
					flag=true;
					strInfo.append(codeitemdesc+"  "+errorInfo.toString());
				}else{//单位不可挂在部门下
					if(paramBo.getInfor_type()==2&&(paramBo.getOperationType()==5))//xgq
					{
						if(rowSet.getString("codesetid_2")!=null&& "UN".equals(rowSet.getString("codesetid_2"))&&rowSet.getString("parentid_2")!=null&&rowSet.getString("parentid_2").trim().length()>0){
							if(map!=null&&map.get(rowSet.getString("parentid_2"))!=null&& "UM".equals(map.get(rowSet.getString("parentid_2")))){
								flag=true;
								errorInfo2.append("的上级不能为部门,");
								strInfo2.append(codeitemdesc+"  "+errorInfo2.toString());
							}
						}
					}

					//您选择了有效日期起为当日的机构，不允许撤销||合并||划转
					//生效日期不能小于开始日期
					if(paramBo.getInfor_type()!=1){
						String codeitemid ="";
						if(paramBo.getInfor_type()==2) {
							codeitemid = rowSet.getString("B0110");
						}
						if(paramBo.getInfor_type()==3) {
							codeitemid = rowSet.getString("E01A1");
						}

						if(paramBo.getOperationType()!=6&&paramBo.getOperationType()!=10)
						{
							String start_date_2 = rowSet.getDate("start_date_2").toString();
							if(start_date_2.length()>=10){
							start_date_2=start_date_2.substring(0,10);
							}else{
								continue;
							}
							if(start_date_2.length()==10){
								StringBuffer ext_sql = new StringBuffer();
								Calendar d=Calendar.getInstance();
								String to_day=df.format(d.getTime());

								ext_sql.append(PubFunc.getDateSql("<=","start_date",start_date_2));
								rowSet2=dao.search("select * from organization where codeitemid='"+codeitemid+"' ");
								boolean flagdate =false;
								String start_date="";
								if(rowSet2.next()){
									flagdate=true;
									if(rowSet2.getDate("start_date")!=null) {
										start_date=df.format(rowSet2.getDate("start_date"));
									}
								}
								if(flagdate){
									rowSet3=dao.search("select * from organization where codeitemid='"+codeitemid+"' "+ext_sql+"  ");
									if(rowSet3.next()){
									}else{
										flag=true;
										errorInfo2.append("的生效日期需不小于创建日期:"+start_date+",");
										strInfo2.append(codeitemdesc+"  "+errorInfo2.toString());
									}
								}

								if(paramBo.getOperationType()==7||paramBo.getOperationType()==8||paramBo.getOperationType()==9)
								{
									if(start_date.equalsIgnoreCase(to_day))
									{
										flag=true;
										errorInfo2.setLength(0);
										errorInfo2.append("不能 撤销|合并|划转 当天创建的机构,");
										if(strInfo2.length()>0){
										  strInfo2.append(errorInfo2.toString());
										}else{
										  strInfo2.append(codeitemdesc+"  "+errorInfo2.toString());
										}
									}
								}

							}
						}
						if(paramBo.getOperationType()==6&&codename.indexOf(",codeitemdesc_2,")==-1){
							flag=true;
							strInfo2.setLength(0);
							if(paramBo.getInfor_type()==2) {
								strInfo2.append("该模板不存在变化后组织单元名称!");
							} else if(paramBo.getInfor_type()==3) {
								strInfo2.append("该模板不存在变化后职位名称!");
							}

						}
					}
					}

			}//while结束
			if(flag){
				if(strInfo.length()>20){

				}else{
					strInfo=strInfo2;
				}
				strInfo.setLength(strInfo.length()-1);
				strInfo.append("!");//最后一个符号改为叹号  郭峰
				throw GeneralExceptionHandler.Handle(new Exception(strInfo.toString()));
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(new Exception(ex));
		}
		finally{
			try {
				if(rowSet!=null) {
					rowSet.close();
				}
				if(rowSet2!=null) {
					rowSet2.close();
				}
				if(rowSet3!=null) {
					rowSet3.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    /**
     * 功能：返回报错信息
     * @param ins_id
     * @param taskId
     * @param opt
     * @param tabId
     * @param moduleId
     * @return
     */
    private String validateInfo(String ins_id, String taskId,
			String opt, String tabId, String moduleId) throws GeneralException, SQLException {
        String info ="";
        boolean bReject="2".equals(opt);//报批类型  1：确定、继续报批 ；2 驳回； 3批准   4：同意
        RowSet rs=null;
        try {
        	TemplateBo templateBo = new TemplateBo(this.conn, this.userView, Integer.parseInt(tabId));
        	TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tabId),this.userView);
        	templateBo.setModuleId(moduleId);
        	templateBo.setTaskId(taskId);
        	TemplateParam paramBo = templateBo.getParamBo();
			// 报批时判断调动人员库 是否设置了目标库
			if (paramBo.getOperationType() == 4
			        && (paramBo.getDest_base() == null || "".equals(paramBo.getDest_base()))) {
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.desddbase"));
			}

			//是否是自定义审批过程 0未勾选 1 勾选未定义(起草判断) 2 勾选定义了
    		String def_flow_self =paramBo.getDef_flow_self();
    		if ("1".equals(def_flow_self)){
    			String[] tasklist=StringUtils.split(taskId,",");
    			for(int i=0;i<tasklist.length;i++){
    				if (paramBo.isDef_flow_self(Integer.parseInt(tasklist[i]))){
                        def_flow_self="2";
                        break;
                    }
    			}
    			if(!"2".equals(def_flow_self)){
    				throw new GeneralException("当前用户需要自定义审批流程!");
    			}
    		}
			// 单据正在处理，不允许重复申请
			if ("0".equals(taskId)){
			    info = templateBo.validateExistData();
			    if (info.length() > 0) {
					throw new GeneralException(info);
				}
			}
			TemplateFrontProperty frontProperty=new TemplateFrontProperty(new HashMap());
			frontProperty.setModuleId(moduleId);
			frontProperty.setTabId(tabId);
			frontProperty.setTaskId(taskId);
			TemplateApplyPrepareBo prepareBo=new TemplateApplyPrepareBo(this.conn,this.userView,paramBo,frontProperty);
			//"1"校验是否选中 ； "2" 校验业务规则  "3"校验编制
			//校验是否选中
			String validateFlag="1";
			//校验目标库是否有当前唯一指标值对应记录 暂定只在起草时校验
			if(info.length()<1 &&"0".equals(taskId)&&paramBo.getInfor_type()==1&&(paramBo.getOperationType()==0||
					paramBo.getOperationType()==1||paramBo.getOperationType()==2||paramBo.getOperationType()==4)) {//0,1,2,4
				info = prepareBo.validateExistOnlyData();
			}
			//校验流程是否关联审批关系
			if(info.length()<1 &&"0".equals(taskId)&&paramBo.getSp_mode()==0&&paramBo.isBsp_flag()) {
				info = prepareBo.validateApplyRelation();
			}
			//校验业务规则
			if (info.length()<1 && (!bReject)){
			    validateFlag ="2";
			    /* 判断操作类型是否是0的0代表不加业务判断规则非0要加业务判断规则 */
			    if (paramBo.getOperationType() != 0 && paramBo.getOperationType() != 5) {
			        info =prepareBo.judgeBusinessRule();
			    }
			}
			ArrayList taskList =prepareBo.getTaskList(frontProperty.getTaskId());
			// 校验必填项及审核公式
			if (info.length()<1 && (!bReject)){
			    validateMustFillItem(tablebo,taskList,frontProperty);
			    checkLogicExpress(templateBo, taskList,frontProperty);
			}
			//单位校验
			if((paramBo.getInfor_type()==2||paramBo.getInfor_type()==3) && (!bReject)) {//如果是单位部门或岗位
			    String srcTab =templateBo.getTableName(moduleId,Integer.parseInt(tabId), taskId);
			    StringBuffer strsql=new StringBuffer("");
			    if("0".equals(taskId)){
			    	strsql.append("select * from ");
			    	strsql.append(srcTab);
			    	strsql.append(" where submitflag=1");
			    	ContentDAO dao = new ContentDAO(this.conn);
			        rs=dao.search(strsql.toString());
			        HashMap tableColumnMap=new HashMap();
			        ResultSetMetaData mt=rs.getMetaData();
			        for(int i=1;i<=mt.getColumnCount();i++)
			        {
			            String columnName=mt.getColumnName(i);
			            tableColumnMap.put(columnName.toLowerCase(),"1");
			        }
			        tablebo.validateSysItem(tableColumnMap);
			        if(paramBo.getOperationType()==5)
			        {
			            checkNewOrgFillItem(tablebo,strsql.toString(),paramBo);
			        }
			        if(paramBo.getOperationType()==8||paramBo.getOperationType()==9)
			        {
			            checkSelectedRule(strsql.toString(),srcTab,"",paramBo);
			        }
			        if(paramBo.getOperationType()==7) {//撤销机构验证机构下是否还有人员
			        	String havePerson = tablebo.checkIsHavePerson(strsql.toString(),"");
			        	if(havePerson.length()>1&&info.length()<1) {
			        		validateFlag ="4";//校验机构撤销
			        		info=havePerson+"组织下还有人员,是否要执行此项操作?";
			        		//弹出confirm框，不强制
		                	info = "confirm:"+info;
			        	}
			        }
			    }else{
			    	strsql.append("select * from ");
					strsql.append(srcTab);
					strsql.append(" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum and "+srcTab+".ins_id=t_wf_task_objlink.ins_id ");
					strsql.append("  and task_id="+taskId+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");
					if(paramBo.getOperationType()==8||paramBo.getOperationType()==9)
					{
						checkSelectedRule(strsql.toString(),srcTab,taskId,paramBo);
					}
			    }

			}
			//校验编制 必须放在最后 warn方式下点确定后，此交易类就不再执行了。
			if (info.length()<1 && "1".equals(paramBo.getHeadCount_control())&& (!bReject)){
			    validateFlag ="3";
                HashMap map =prepareBo.validateHeadCount();
                String headControlType =(String)map.get("flag");//warn 或error
                info =(String)map.get("msgs");
                if("warn".equalsIgnoreCase(headControlType)){
                	//弹出confirm框，不强制
                	info = "confirm:"+info;
                }
			}
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return info;
	}
    /**
     * 获得人事异动的已办任务
     * @param LazyDynaBean paramBean
     *   start_date //开始时间
     *   end_date 结束时间
     *   days 最近几天
     *   query_type：//按日期or按时间段 1 or other  最近几天 传值为1 today 今天、week 本周、year 本年、quarter 本季  all 全部
     *   tabid 模板号
     *   module_id 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审 12、证照管理
     *   bs_flag 任务类型 1：审批任务 2：加签任务 3：报备任务  4：空任务 10：首页待办任务,包含审批任务和报备任务
     *   topic_info 按照主题发送人 筛选功能
     * @return ArrayList
     */
    public ArrayList getYpTask (LazyDynaBean paramBean)
    {
	    	ArrayList dataList=new ArrayList();
	    	RowSet rowSet=null;
	    	RowSet rowSet2=null;
	    	/* 模块ID
			 * 1、人事异动
			 * 2、薪资管理
			 * 3、劳动合同
			 * 4、保险管理
			 * 5、出国管理
			 * 6、资格评审
			 * 7、机构管理
			 * 8、岗位管理
			 * 9、业务申请（自助）
			 * 10、考勤管理
			 * 11、职称评审
			*/
			String _withNoLock="";
	    	try
			{
	    		String module_id=(String) paramBean.get("module_id");
	    		String tabid = (String) paramBean.get("tabid");//模板号
	    		String bs_flag = (String) paramBean.get("bs_flag");//任务类型
	    		String days=(String) paramBean.get("days");//最近几天
	    		String start_date=(String) paramBean.get("start_date");//开始时间
	    		String end_date=(String) paramBean.get("end_date");//结束时间
	    		String query_type = (String) paramBean.get("query_type");//按日期or按时间段 1 or other
	    		String topic_info = (String) paramBean.get("topic_info");//按照主题发送人 筛选功能
	    		TemplateNavigationBo bo = new TemplateNavigationBo(conn,this.userView);
	    		if(query_type==null || query_type.trim().length()==0) {
					query_type="1";
				}
	    		if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
				{
					_withNoLock=" WITH(NOLOCK) ";
				}

	    		StringBuffer strsql = new StringBuffer();
	    		StringBuffer strsql2 = new StringBuffer();
	    		ArrayList valueList=new ArrayList();
	    		ContentDAO dao=new ContentDAO(conn);
	    		String format_str="yyyy-MM-dd HH:mm";
	    		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
					format_str="yyyy-MM-dd hh24:mi";
				}
	    		String static_="static";
	    		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
	    			static_="static_o";
	    		}
	    		strsql.append("select U.ins_id,T.task_topic,U.tabid,U.actorname fullname,(select o.codeitemid from organization o "+_withNoLock+" where o.codeitemid=U.b0110) unitname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" end_date,");
	    		strsql.append("T.actorname,T.actor_type,T.task_id,T.flag,U.tabid,tt."+static_+",U.finished insfinished,T.content   from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table tt "+_withNoLock+"");
	    		strsql.append(" where  T.ins_id=U.ins_id  and U.tabid=tt.tabid and ((task_type='2' and  (task_state='5'  or task_state='6' )) or(task_type='9' and task_state='4' and U.finished='6' and T.task_topic like '%被撤销)') ) ");
	    		strsql2.append(strsql);
	    		if(tabid!=null&&!"-1".equals(tabid)&&tabid.length()>0)
	    		{
	    			strsql.append(" and tt.tabid=?  " );
	    			valueList.add(new Integer(tabid));
	    		}
	    		if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整
	    			strsql.append(" and tt."+static_+"=? " );
	    			if("7".equals(module_id)){
	    				valueList.add(new Integer(10));
	    				strsql2.append(" and tt."+static_+"=10 " );
	    			}
	    			else if("8".equals(module_id)){
	    				valueList.add(new Integer(11));
	    				strsql2.append(" and tt."+static_+"=11 " );
	    			}
	    		}
	    		else
	    		{
	    			if(userView.getStatus() != 4 || !"9".equals(module_id)){
	    				strsql.append(" and tt."+static_+"!=10 and tt."+static_+"!=11 ");
	    				strsql2.append(" and tt."+static_+"!=10 and tt."+static_+"!=11 ");
	    			}
	    		}
	    		String kq_tabids="";
	    		String zz_tabids = bo.getBusinessTabid("12");
	    		if(module_id==null||!"9".equals(module_id)) //业务申请的待办无需过滤考勤模板
	    		{
	    			TemplateTableParamBo tp=new TemplateTableParamBo(this.conn);
	    			kq_tabids=tp.getAllDefineKqTabs(0);
	    		}
	    		if(module_id!=null&& "10".equals(module_id)){ //考勤业务办理
	    			if(kq_tabids.length()==0){
	    				strsql.append(" and 1=2 ");
	    				strsql2.append(" and 1=2 ");
	    			}
	    			else{
	    				strsql.append(" and U.tabid in ("+kq_tabids.substring(1)+")  and tt.tabid in ("+kq_tabids.substring(1)+")  ");
	    				strsql2.append(" and U.tabid in ("+kq_tabids.substring(1)+")  and tt.tabid in ("+kq_tabids.substring(1)+")  ");
	    			}
	    		}
	    		else if(module_id!=null&&"12".equals(module_id)) {//证照管理
	    			if(zz_tabids.length()==0){
	    				strsql.append(" and 1=2 ");
	    				strsql2.append(" and 1=2 ");
	    			}
	    			else{
	    				strsql.append(" and U.tabid in ("+zz_tabids+")  and tt.tabid in ("+zz_tabids+")  ");
	    				strsql2.append(" and U.tabid in ("+zz_tabids+")  and tt.tabid in ("+zz_tabids+")  ");
	    			}
	    		}
	    		else if (kq_tabids.length()>0||zz_tabids.length()>0)
	    		{
	    			if(kq_tabids.length()>0) {
	    				strsql.append(" and tt.tabid not in ("+kq_tabids.substring(1)+")" );
	    				strsql2.append(" and tt.tabid not in ("+kq_tabids.substring(1)+")" );
	    			}
	    			if(zz_tabids.length()>0) {
	    				strsql.append(" and tt.tabid not in ("+zz_tabids+")" );
	    				strsql2.append(" and tt.tabid not in ("+zz_tabids+")" );
	    			}
	    		}
	    		//1：审批任务 2：加签任务 3：报备任务  4：空任务
	    		strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"=? ");
	    		strsql2.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"=" + bs_flag);
	    		valueList.add(bs_flag);
	    		strsql.append("  and ( "+getInsFilterWhere2("T."));
	    		strsql2.append(" and ( "+getInsFilterWhere2("T."));
	    		strsql.append("   and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"),"' '")+"<>'重新分派'  )  ");
	    		strsql2.append("   and "+Sql_switcher.isnull(Sql_switcher.sqlToChar("T.content"),"' '")+"<>'重新分派' ) ");
	    		strsql.append(")  and 1=1 ");
	    		strsql2.append(")  and 1=1 ");
	    		if("1".equals(query_type))//最近多少天
	    		{
	    			if(bo.validateNum(days)){
	    				String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
	    				strsql.append(" and U.start_date>=");
	    				strsql.append(strexpr);
	    				strsql2.append(" and U.start_date>=");
	    				strsql2.append(strexpr);
	    			}
	    		}else if("today".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
	    			strsql.append(PubFunc.getDateSql(">=","T.start_date",new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
	    			strsql2.append(PubFunc.getDateSql(">=","T.start_date",new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
				}else if("week".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
					strsql.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getWeekStart()));
					strsql2.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getWeekStart()));
				}else if("year".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
					strsql.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getYearStart()));
					strsql2.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getYearStart()));
				}else if("quarter".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
					strsql.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getCurrentQuarterStartTime()));
					strsql2.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getCurrentQuarterStartTime()));
				}else{
	    			if("2".equals(query_type)){
	    				strsql.append(" and ( 1=1 ");
	    				strsql2.append(" and ( 1=1 ");
	    				if(bo.validateDate(start_date)){
	    					strsql.append(PubFunc.getDateSql(">=","U.start_date",start_date));
	    					strsql2.append(PubFunc.getDateSql(">=","U.start_date",start_date));
	    				}
	    				if(bo.validateDate(end_date)){
	    					strsql.append(PubFunc.getDateSql("<=","U.start_date",end_date));
	    					strsql2.append(PubFunc.getDateSql("<=","U.start_date",end_date));
	    				}
	    				strsql.append(" )");
	    				strsql2.append(" )");
	    			}
	    		}
	    		//获得各实例下当前审批人sql
	    		int index=strsql.toString().indexOf("from");
	    		String subsql=strsql.toString().substring(index+4, strsql.toString().length());  //为了得到第二个from的位置，先将第一个from的位置之前的部分去掉
	    		index=subsql.indexOf("from");  //得到第二个from的位置，即可根据index截取要查询的sql liuzy 20151112
	    		String sql="";
	    		if(index!=-1) {
					sql=" select ins_id,actorname,actor_type from t_wf_task "+_withNoLock+" where ins_id in (select  U.ins_id "+subsql.substring(index)+") and task_type='2' and  "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='3' order by ins_id";
				}

	    		strsql.append(" order by T.end_date DESC");
	    		if(valueList.size()>0) {
					rowSet=dao.search(strsql.toString(),valueList);
				} else {
					rowSet=dao.search(strsql.toString());
				}
	    		//获得各实例下当前审批人
	    		HashMap ins_CurrentSpInfo=new HashMap(); //
	    		if(sql.length()>0)
	    		{
	    			if(valueList.size()>0) {
						rowSet2=dao.search(sql,valueList);
					} else {
						rowSet2=dao.search(sql);
					}
	    			int ins_id=0;
	    			String actorname="";
	    			String actor_type = "";
	    			while(rowSet2.next())
	    			{
	    				if(ins_id==0) {
							ins_id=rowSet2.getInt("ins_id");
						}
	    				if(ins_id==rowSet2.getInt("ins_id")){
	    					actorname+=","+rowSet2.getString("actorname");
	    					actor_type +=","+ rowSet2.getString("actor_type");
	    				}else
	    				{
	    					if(actorname.toString().length()>1&&actor_type.length()>1)//liuyz 排除actor_type和actorname为空值的情况
	    					{
	    						ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1)+"`"+actor_type.substring(1));
	    						ins_id=rowSet2.getInt("ins_id");
	    						actorname=","+rowSet2.getString("actorname");
	    						actor_type =","+ rowSet2.getString("actor_type");
	    					}
	    				}
	    			}
	    			if(ins_id!=0&&actorname.length()>1)//liuyz 排除actor_type和actorname为空值的情况
					{
						ins_CurrentSpInfo.put(String.valueOf(ins_id),actorname.toString().substring(1)+"`"+actor_type.substring(1));
					}
	    		}

	    		LazyDynaBean abean=null;
	    		HashMap operationTypeMap=new HashMap();
	    		HashMap tableNameMap=new HashMap();

	    		while(rowSet.next())
	    		{
	    			abean=new LazyDynaBean();
	    			String _tabid = rowSet.getString("tabid");
	    			String task_id=rowSet.getString("task_id");
	    			String ins_id=rowSet.getString("ins_id");
	    			String _flag=rowSet.getString("flag")!=null?rowSet.getString("flag"):"";
	    			String task_topic=rowSet.getString("task_topic");
	    			String _static=rowSet.getString(static_);
	    			TemplateParam param = new TemplateParam(conn,this.userView,Integer.valueOf(_tabid));
	    			if("".equals(_flag))
	    			{
	    				String operationType="";
	    				if(operationTypeMap.get(_tabid)==null)
	    				{
	    					operationType=findOperationType(_tabid);
	    					operationTypeMap.put(_tabid, operationType);
	    				}
	    				else {
							operationType=(String)operationTypeMap.get(_tabid);
						}
	    				String tabName="";
	    				if(tableNameMap.get(_tabid)==null)
	    				{
	    					tabName=findTabName(_tabid);
	    					tableNameMap.put(_tabid,tabName);
	    				}
	    				else
	    				{
	    					tabName=(String)tableNameMap.get(_tabid);
	    				}
	    				String topic="";
	    				if(userView.getStatus()!=4) {
							topic=getTopic(task_id,"templet_"+_tabid,Integer.parseInt(operationType),_tabid,module_id,param);
						} else
	    				{
	    					if(_static!=null&&("10".equals(_static)|| "11".equals(_static)))
	    					{
	    						topic=getTopic(task_id,"templet_"+_tabid,Integer.parseInt(operationType),_tabid,module_id,param);
	    					}
	    					else {
								topic=getTopic(task_id,"templet_"+_tabid,Integer.parseInt(operationType),_tabid,module_id,param);
							}
	    				}
	    				if(topic.indexOf(",共0")!=-1) //撤销任务主题
	    				{
	    					topic=getRecordBusiTopicByState(Integer.parseInt(task_id),3,"templet_"+_tabid,dao,Integer.parseInt(operationType), module_id,param);
	    				}
	    				task_topic = tabName+topic;

	    			}
	    			if(ins_CurrentSpInfo.get(ins_id)!=null){
	    				abean.set("sp_info",((String)ins_CurrentSpInfo.get(ins_id)).split("`")[0]);
	    				abean.set("actor_type", ((String)ins_CurrentSpInfo.get(ins_id)).split("`")[1]);
	    			}else{
	    				abean.set("sp_info","");
	    				abean.set("actor_type","");
	    			}
	    			String content=Sql_switcher.readMemo(rowSet,"content");// 自动流转的任务标题后加【自动审批】标识

	    			if(content.indexOf("【自动审批】")!=-1) {
						task_topic=task_topic+" 【自动审批】";
					}
	    			// 按照主题发送人 筛选功能
	    			if(StringUtils.isNotEmpty(topic_info)&&task_topic.indexOf(topic_info)==-1&&(rowSet.getString("fullname")).indexOf(topic_info)==-1){
	    				continue;
	    			}
	    			abean.set("task_topic",task_topic);
	    			abean.set("ins_id",rowSet.getString("ins_id"));
	    			abean.set("tabid",rowSet.getString("tabid"));
	    			abean.set("fullname",rowSet.getString("fullname"));
	    			String unitname=rowSet.getString("unitname")==null?"":rowSet.getString("unitname");
	    			abean.set("unitname",unitname);
	    			abean.set("start_date", rowSet.getString("start_date"));
	    			abean.set("end_date", rowSet.getString("end_date"));
	    			//耗时（小时）列 取值
	    			abean.set("used_time", calculatetimeGapHour("yyyy-MM-dd HH:mm", rowSet.getString("start_date"), rowSet.getString("end_date")));
	    			/**安全改造，将参数加密**/
	    			abean.set("task_id",PubFunc.encrypt(rowSet.getString("task_id")));
	    			String insfinished = rowSet.getString("insfinished");
	    			if("5".equals(insfinished)){
	    				abean.set("flag", "结束");
	    			}else if("6".equals(insfinished)){//lis 20160520
	    				abean.set("flag", "终止");
	    			}else{
	    				abean.set("flag", "等待");
	    			}
	    			dataList.add(abean);
	    		}
			}
	    	catch (Exception e) {
	            e.printStackTrace();
		    }
		    finally{
		            PubFunc.closeDbObj(rowSet);
		            PubFunc.closeDbObj(rowSet2);
		    }
	    	return dataList;
    }

    /**
     *
     */
    /**
     * 获得人事异动的代办已办报备任务
     * @param LazyDynaBean paramBean
     *   start_date //开始时间
     *   end_date 结束时间
     *   days 最近几天
     *   query_type 按日期or按时间段 1 or other
     *   tabid 模板号
     *   module_id 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审 12、证照管理
     *   bs_flag 任务类型 1：审批任务 2：加签任务 3：报备任务  4：空任务 10：首页待办任务,包含审批任务和报备任务
     * @param userView
     * @return ArrayList
     */
    public ArrayList getReportTask(LazyDynaBean paramBean){
    	/**
    	 * 获取代办任务的报备任务列表
    	 */
    	paramBean.set("isorder","false");
		ArrayList dataList=getDBList(paramBean);
		/**获取已办任务的报备任务列表*/
		dataList.addAll(getYpTask(paramBean));
		Collections.sort(dataList,new Comparator () {
            @Override
            public int compare(Object o1, Object o2) {
                if(o1 instanceof LazyDynaBean && o2 instanceof LazyDynaBean){
                	SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            		Date d=new Date();
            		String current_str=simpleformat.format(d);
                	LazyDynaBean e1 = (LazyDynaBean) o1;
                	LazyDynaBean e2 = (LazyDynaBean) o2;
                	String e1_start_date=e1.get("start_date")!=null?(String)e1.get("start_date"):current_str;
                	String e2_start_date=e2.get("start_date")!=null?(String)e2.get("start_date"):current_str;
                	Date e1_date =d;
                	Date e2_date =d;
                	try
                	{
                		e1_date=simpleformat.parse(e1_start_date);
                		e2_date=simpleformat.parse(e2_start_date);
                	}
                	catch(Exception e)
                	{

                	}
                	int value=1;
                	if(e1_date.getTime()>e2_date.getTime()) {
						value=-1;
					} else if(e1_date.getTime()==e2_date.getTime())//liuyz bug31898
					{
						value=0;
					}
                    return value;
                }
                throw new ClassCastException("排序出错");
            }
        });
    	return dataList;
    }
	private String getInsFilterWhere2(String othername)
	{
		String _withNoLock="";
		if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
		{
			_withNoLock=" WITH(NOLOCK) ";
		}
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
		if(userid==null||userid.length()==0) {
			userid="-1";
		}
		strwhere.append(" upper("+othername+"actorid) in ('");
		strwhere.append(userid.toUpperCase());
		strwhere.append("','");
		strwhere.append(this.userView.getUserName().toUpperCase());
		strwhere.append("') ");
		strwhere.append(" or  ( (");
		if(this.userView.getRolelist().size()>0)
		{
			strwhere.append(" ( "+othername+"actor_type=2 and  upper("+othername+"actorid) in ( ");
			String str="";
			for(int i=0;i<this.userView.getRolelist().size();i++)
			{
				str+=",'"+(String)this.userView.getRolelist().get(i)+"'";
			}
			strwhere.append(str.substring(1));
			strwhere.append(" )) or ");
		}
		strwhere.append(othername+"actor_type=5 ) ");
		strwhere.append(" and exists (select null from t_wf_task_objlink "+_withNoLock+" where ins_id=U.ins_id and task_id="+othername+"task_id and node_id="+othername+"node_id and tab_id=U.tabid and (state=1 or state=2) and upper(username) in ('"+this.userView.getUserName().toUpperCase()+"','"+userid.toUpperCase()+"') ) " );
		String a0100=this.userView.getDbname()+this.userView.getA0100();
		if(a0100==null||a0100.length()==0) {
			a0100=this.userView.getUserName();
		}

		/**组织元*/
		strwhere.append(" or ( "+othername+"actor_type='3'   and "+othername+"a0100='"+a0100+"')");   //2016-05-20   dengcan 追加了 actor_type='5' ,解决本人审批看不到问题，这块很乱，得花时间重新梳理
		return " ( "+strwhere.toString()+" ) ";
	}
	public String findTabName(String tabid)
	{
		String tabName="";
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			RowSet rowSet=dao.search("select name from template_table where tabid="+tabid);
			if(rowSet.next()) {
				tabName=rowSet.getString("name");
			}
			if(rowSet!=null) {
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return tabName;
	}

	public String findOperationType(String tabid)
	{
		String operationType="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select operationtype from operation where operationcode=(select operationcode from template_table where tabid="+tabid+")");
			if(rowSet.next()) {
				operationType=rowSet.getString("operationtype");
			}
			if(rowSet!=null) {
				rowSet.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return operationType;
	}
	/**
	 * 求实际的业务数,本次模板做了多少人的业务
	 * @param param
	 * @return
	 */
	public String getRecordBusiTopicByState(int task_id,int state,String tabname,ContentDAO dao,int operationtype,String type, TemplateParam param)
	{
		String _withNoLock="";
		if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
		{
			_withNoLock=" WITH(NOLOCK) ";
		}
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		try
		{
			StringBuffer strsql=new StringBuffer();
			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
					a0101="a0101_2";
			}
			if(param.getInfor_type()==2 || param.getInfor_type()==3)//如果是单位管理机构调整 或 岗位管理机构调整
			{
				a0101="codeitemdesc_1";
				if(operationtype==5) {
					a0101="codeitemdesc_2";
				}
			}
			String strWhere=" where ";
			String strWhere2="";
			if(state==0) {
				strWhere2=" and (state is null or  state=0) ";
			} else {
				strWhere2=" and state="+state+" ";
			}
			strWhere+=" exists (select null from t_wf_task_objlink "+_withNoLock+" where "+tabname+".seqnum=t_wf_task_objlink.seqnum and task_id="+task_id+" "+strWhere2+"  )";
			strsql.append("select  ");
			strsql.append(a0101);
			strsql.append(" from ");
			strsql.append(tabname+_withNoLock);
			strsql.append(strWhere);
			RowSet rset=dao.search(strsql.toString());
			int i=0;
			while(rset.next())
			{
				if(i>4) {
					break;
				}
				if(i!=0) {
					stopic.append(",");
				}
				stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
				i++;
			}
			strsql.setLength(0);

			strsql.append("select count(*) as nmax from ");
			strsql.append(tabname+_withNoLock);
			strsql.append(strWhere.toString());

			rset=dao.search(strsql.toString());
			if(rset.next()) {
				nmax=rset.getInt("nmax");
			}
			stopic.append(",");
			stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
			stopic.append(nmax);
			if(type!=null&&("7".equals(type)|| "8".equals(type)))//如果是单位管理机构调整 或 岗位管理机构调整
			{
				stopic.append("条记录 ");
			} else {
				stopic.append("人");
			}
			if(state==3) {
				stopic.append(" 被撤销");
			}
			stopic.append(")");
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();
	}
	public String getTopic(String task_id,String tabname,int operationtype,String tab_id,String type, TemplateParam param)
	{
		int nmax=0;
		StringBuffer stopic=new StringBuffer();
		stopic.append("(");
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
			{
				_withNoLock=" WITH(NOLOCK) ";
			}
			ContentDAO dao=new ContentDAO(conn);
			String a0101="a0101_1";
			RecordVo vo=new RecordVo(tabname);
			if(operationtype==0&&vo.hasAttribute("a0101_2"))//调入
			{
				a0101="a0101_2";
			}
			if(param.getInfor_type()==2 || param.getInfor_type()==3){//如果是单位管理机构调整 或 岗位管理机构调整
				a0101="codeitemdesc_1";
				if(operationtype==5) {
					a0101="codeitemdesc_2";
				}
			}
			String sql="";
			String seqnum="1";
			RowSet rset=null;
			rset=dao.search("select seqnum from "+tabname+""+_withNoLock+" where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )");
			if(rset.next()) {
				seqnum=rset.getString(1)!=null?rset.getString(1):"";
			}
				if(seqnum.length()>0)
				{
					sql=" select "+a0101+" from "+tabname+""+_withNoLock+",t_wf_task_objlink two "+_withNoLock+" where "+tabname+".seqnum=two.seqnum and "+tabname+".ins_id=two.ins_id "
							+" and two.task_id="+task_id+" and two.tab_id="+tab_id +" and ( "+Sql_switcher.isnull("two.state","0")+"<>3 )  and ("+Sql_switcher.isnull("two.special_node","0")+"=0  or ( "+Sql_switcher.isnull("two.special_node","0")+"=1 and (lower(two.username)='"+this.userView.getUserName().toLowerCase()+"' or lower(two.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
				}
				else
				{
					sql=" select "+a0101+" from "+tabname+" "+_withNoLock+"  where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )";
				}
				rset=dao.search(sql);
				int i=0;
				while(rset.next())
				{
					if(i>4) {
						break;
					}
					if(i!=0) {
						stopic.append(",");
					}
					stopic.append(rset.getString(a0101)==null?"":rset.getString(a0101));
					i++;
				}
				if(i>4)
				{
					if(seqnum.length()>0) {
						sql="select count(*)  from t_wf_task_objlink "+_withNoLock+"  where task_id="+task_id+" and tab_id="+tab_id +"  and ( "+Sql_switcher.isnull("state","0")+"<>3 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )";
					} else {
						sql=" select count(*) from "+tabname+" "+_withNoLock+" where ins_id=(select ins_id from t_wf_task "+_withNoLock+" where task_id="+task_id+" )";
					}
					rset=dao.search(sql);
					if(rset.next()) {
						nmax=rset.getInt(1);
					}
				}
				else {
					nmax=i;
				}
				stopic.append(",");
				stopic.append(ResourceFactory.getProperty("hmuster.label.total"));
				stopic.append(nmax);
				if(param.getInfor_type()==2 || param.getInfor_type()==3)//如果是单位管理机构调整 或 岗位管理机构调整
				{
					stopic.append("条记录)");
				} else {
					stopic.append("人)");
				}
				if(rset!=null) {
					rset.close();
				}
		}
		catch(Exception ex)
		{
			return stopic.toString();
		}
		return stopic.toString();

	}

	/**  */
	/**
	 * 功能：比较两个时间相差小时
	 * @param pattern:时间格式：yyyy-MM-dd HH:mm
	 * @param time1 开始时间
	 * @param time2结束时间
	 * @return
	 */
	public String calculatetimeGapHour(String pattern,String time1, String time2) {
		String hourStr = "";
		if(StringUtils.isEmpty(pattern)||StringUtils.isEmpty(time1)||StringUtils.isEmpty(time2)){
			return hourStr;
		}
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			Date date1 = simpleDateFormat.parse(time1);
			Date date2 = simpleDateFormat.parse(time2);
			double millisecond = date2.getTime() - date1.getTime();
			double hours = millisecond / (60 * 60 * 1000);
			DecimalFormat df = new DecimalFormat("0.00");
			hourStr = df.format(hours);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return hourStr;
	}
	//--------------------------------------------------------------------------------------
	/**
     * 获得人事异动的我的申请   以及任务监控处调用
     * @param LazyDynaBean paramBean
     *   start_date //开始时间
     *   end_date 结束时间
     *   days 最近几天
     *   query_type：//按日期or按时间段 1 or other  最近几天 传值为1 today 今天、week 本周、year 本年、quarter 本季  all 全部
     *   tabid 模板号
     *   module_id 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审 12、证照管理
     *   bs_flag 任务类型 1：审批任务 2：加签任务 3：报备任务  4：空任务 10：首页待办任务,包含审批任务和报备任务
     *   sp_flag：1:我的申请  2:任务监控
     *   query_method 1 运行  3：终止  2：运行
     *   topic_info 按照主题发送人 筛选功能
     * @return ArrayList
     */
	public ArrayList getCtrlTask(LazyDynaBean paramBean){
		log.info("paramBean:"+paramBean.toString());
		ArrayList dataList=new ArrayList();
		RowSet rowSet=null;
		try
		{
			/* 模块ID
			 * 1、人事异动
			 * 2、薪资管理
			 * 3、劳动合同
			 * 4、保险管理
			 * 5、出国管理
			 * 6、资格评审
			 * 7、机构管理
			 * 8、岗位管理
			 * 9、业务申请（自助）
			 * 10、考勤管理
			 * 11、职称评审
			 */
			String module_id=(String)paramBean.get("module_id");
			String tabid = (String) paramBean.get("tabid");//模板号
			String days=(String)paramBean.get("days");//最近几天
			String start_date=(String)paramBean.get("start_date");//开始时间
			String end_date=(String)paramBean.get("end_date");//结束时间
			String query_type = (String) paramBean.get("query_type");//按日期or按时间段 1 or other
			String sp_flag=(String)paramBean.get("sp_flag");// 1:我的申请  2:任务监控
			String fromflag=(String)paramBean.get("fromflag"); //deskTop 从首页进入我的申请
			String topic_info = (String) paramBean.get("topic_info");//按照主题发送人 筛选功能
			String query_method=(String) paramBean.get("query_method");
			if(query_method==null || query_method.trim().length()==0) {
				query_method="1";
			}
			if("0".equals(query_method)) {
				query_method="";
			}
			if(query_type==null || query_type.trim().length()==0) {
				query_type="1";
			}
			StringBuffer strsql=new StringBuffer();
			TemplateNavigationBo bo = new TemplateNavigationBo(this.conn,this.userView);
			bo.setModule_id(module_id);
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
			{
				_withNoLock=" WITH(NOLOCK) ";
			}
			ContentDAO dao = new ContentDAO(this.conn);
			String format_str="yyyy-MM-dd HH:mm:ss";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
				format_str="yyyy-MM-dd hh24:mi:ss";
			}
			String _static="static";
			if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
				_static="static_o";
			}
			//结束状态  兼容旧程序  2014-04-01 dengcan   case when T.task_topic like '%共0%' then U.name  else T.task_topic end name
			strsql.append("select U.ins_id,case when T.task_topic like '%共0%' then U.name  else T.task_topic end name,U.tabid,U.actorname fullname, U.b0110  unitname,a0101, task_state finished ,"+Sql_switcher.dateToChar("U.start_date",format_str)+" as ins_start_date,"+Sql_switcher.dateToChar("T.end_date",format_str)+" as ins_end_date,T.actor_type,T.actorname,T.task_id ");
			if("1".equals(sp_flag)&&("1".equals(query_method)||"".equals(query_method))) {
				strsql.append(",U.actor_type actortype,case when (select count(1) from t_wf_task t1  where  t1.task_type='2' and T1.ins_id=u.ins_id and t1.bread=1)>0 then 0  else 1 end  recallflag ");
			}
			strsql.append("from t_wf_task T "+_withNoLock+",t_wf_instance U "+_withNoLock+",template_table tt "+_withNoLock+"");
			strsql.append(" where T.ins_id=U.ins_id ");
			if(!("2".equals(query_method) || "3".equals(query_method))) //结束状态 兼容旧程序  2014-04-01 dengcan
			{
				strsql.append(" and  task_topic not like '%共0人%' and  task_topic not like '%共0条%' ");
			}
			if(topic_info.length()>0)
			{
				strsql.append(" and (task_topic like '%"+topic_info+"%' or U.actorname like '%"+topic_info+"%')");
			}
			if(module_id!=null&&("7".equals(module_id)|| "8".equals(module_id))){//如果是单位管理机构调整 或 岗位管理机构调整

				if("7".equals(module_id)) {
					strsql.append(" and   U.tabid=tt.tabid and tt."+_static+"=10   ");
				} else if("8".equals(module_id)) {
					strsql.append(" and   U.tabid=tt.tabid and tt."+_static+"=11   ");
				}
			}
			else
			{
				strsql.append("  and  U.tabid=tt.tabid and tt."+_static+"!=10 and tt."+_static+"!=11  ");
			}
			String kq_tabids="";
			String zz_tabids = bo.getBusinessTabid("12");
			if(module_id==null||!"9".equals(module_id)) //业务申请的待办无需过滤考勤模板
			{
		        kq_tabids=bo.getKqTabIds(module_id);//liuyz 考勤支持业务模版
			}

			if(module_id!=null&& "10".equals(module_id)){ //考勤业务办理
				if(kq_tabids.length()==0) {
					strsql.append(" and 1=2 ");
				} else {
					strsql.append("   and tt.tabid in ("+kq_tabids.substring(1)+")  ");
				}
			}
			else if(module_id!=null&&"12".equals(module_id)) {//证照管理
				if(zz_tabids.length()==0){
					strsql.append(" and 1=2 ");
				}
				else{
					strsql.append("   and tt.tabid in ("+zz_tabids+")  ");
				}
			}
			else if (kq_tabids.length()>0||zz_tabids.length()>0)
			{
				if(!"deskTop".equalsIgnoreCase(fromflag)) {//从首页进入查看我的申请 不按模块过滤模板
					if(kq_tabids.length()>0) {
						strsql.append(" and tt.tabid not in ("+kq_tabids.substring(1)+")" );
					}
					if(zz_tabids.length()>0) {
						strsql.append(" and tt.tabid not in ("+zz_tabids+")" );
					}
				}
			}
			if("1".equals(query_method))
			{
				strsql.append(" and task_type='2' and finished='2' and ( task_state='3'  or task_state='6' )");//=3等待状态 =6暂停
			}
			else if("2".equals(query_method))
			{
				strsql.append(" and ( T.task_type='9' and  T.task_state='5' )");//Finished T.task_type='2' and T.flag=1//task_type='2' and task_state='5' and state<>'07'
			}else if("3".equals(query_method)) //&&!sp_flag.equals("1"))//终止
			{
				strsql.append(" and ( T.task_type='9' and  T.task_state='4' )");//Finished T.task_type='2' and T.flag=1//task_type='2' and task_state='5' and state<>'07'
			}else if(StringUtils.isEmpty(query_method)) {
				strsql.append(" and ( task_type='2' and finished='2' and ( task_state='3'  or task_state='6' ) "
						+ " or (T.task_type='9' and  T.task_state='5') "
						+ " or ( T.task_type='9' and  T.task_state='4' ) )");
			}

			if("1".equals(sp_flag)) //我的申请
			{
				strsql.append(" and (");
				strsql.append(getInsFilterWhere(module_id));
				strsql.append(")");
			}
			else //2 任务监控
			{
				if(!this.userView.isSuper_admin())
				{
					String tmp = getTemplates();
					if(tmp.length()==0)
					{
						strsql.append(" and 1=2");
					}
					else
					{
						strsql.append(" and tt.tabid in (");
						strsql.append(tmp);
						strsql.append(")");
					}
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
	                                     " or  U.b0110 like '" + temp[j].substring(2)+ "%'";
	                             }
	                        }
	                    }
	                    strB0110Where=strB0110Where +" or "+Sql_switcher.sqlNull("U.b0110", "##")+"='##'";
	                    if(strB0110Where.length()>0){
                            strB0110Where=strB0110Where.substring(3);
                            strB0110Where = "("+strB0110Where+")";
                            strsql.append(" and ");
                            strsql.append(strB0110Where);
                        }
	                }
	            }
			}
			StringBuffer strsql2=new StringBuffer();
			strsql2.append(strsql.toString());
			if(tabid!=null&&!"-1".equals(tabid)&&tabid.length()>0)
			{
				if(bo.validateNum(tabid)) {
					strsql.append(" and tt.tabid="+tabid );
				}
			}

			//1：审批任务
			strsql.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  ");
			strsql2.append(" and "+Sql_switcher.isnull("T.bs_flag","'1'")+"='1'  ");
			//增加时间查询
			if("1".equals(sp_flag))
			{
				if("1".equals(query_type))//最近多少天
				{
					if(bo.validateNum(days)){
						String strexpr=Sql_switcher.addDays(Sql_switcher.sqlNow(),"-"+days);//"GetDate()"
						strsql.append(" and U.start_date>=");
						strsql.append(strexpr);
						strsql2.append(" and U.start_date>="+strexpr);
					}
				}else if("today".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
	    			strsql2.append(PubFunc.getDateSql(">=","T.start_date",new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
				}else if("week".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
					strsql2.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getWeekStart()));
				}else if("year".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
					strsql2.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getYearStart()));
				}else if("quarter".equalsIgnoreCase(query_type))//today 今天、week 本周、year 本年、quarter 本季
				{
					strsql2.append(PubFunc.getDateSql(">=","T.start_date",PubFunc.getCurrentQuarterStartTime()));
				}else if("2".equals(query_type)){
						StringBuffer tempSql=new StringBuffer("");
						tempSql.append(" and ( 1=1 ");
						if(bo.validateDate(start_date))
						{
							tempSql.append(PubFunc.getDateSql(">=","U.start_date",start_date));
						}
						if(bo.validateDate(end_date))
						{
							tempSql.append(PubFunc.getDateSql("<=","U.start_date",end_date));
						}
						tempSql.append("  ) ");
						strsql.append(tempSql.toString());
						strsql2.append(tempSql.toString());
					}
			}
			String order_sql="";
			if("1".equals(query_method)||"".equals(query_method))  //运行中
			{
				order_sql=" order by ins_start_date DESC";
			}
			if("2".equals(query_method) || "3".equals(query_method))//结束
			{
				order_sql=" order by ins_end_date DESC";
			}
			strsql2.append(order_sql);
			LazyDynaBean abean=null;
			log.info("查询我的申请sql:{}",strsql2.toString());
			rowSet=dao.search(strsql2.toString());
			while(rowSet.next())
    		{
    			abean=new LazyDynaBean();
    			abean.set("ins_id",rowSet.getString("ins_id"));
    			abean.set("tabid",rowSet.getString("tabid"));
    			/**安全改造，将参数加密**/
    			abean.set("task_id",PubFunc.encrypt(rowSet.getString("task_id")));
    			abean.set("actor_type",rowSet.getString("actor_type"));
    			String task_topic=rowSet.getString("name");
    			abean.set("task_topic",task_topic);
    			abean.set("fullname",rowSet.getString("fullname")==null?"":rowSet.getString("fullname"));
    			abean.set("actorname",rowSet.getString("actorname")==null?"":rowSet.getString("actorname"));
    			String unitname=rowSet.getString("unitname")==null?"":rowSet.getString("unitname");
    			abean.set("unitname",unitname);
    			abean.set("start_date", rowSet.getString("ins_start_date")==null?"":rowSet.getString("ins_start_date"));
    			abean.set("end_date", rowSet.getString("ins_end_date")==null?"":rowSet.getString("ins_end_date"));
    			String insfinished = rowSet.getString("finished");
    			abean.set("flag", AdminCode.getCodeName("38", insfinished));
    			dataList.add(abean);
    		}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
		return dataList;
	}



	/**
	 * 求权限范围下的模板串
	 * @return
	 */
	private String getTemplates() {
		StringBuffer mb=new StringBuffer();
		String rsbd=this.userView.getResourceString(IResourceConstant.RSBD);
		mb.append(rsbd);
		mb.append(",");

		String orgbd=this.userView.getResourceString(IResourceConstant.ORG_BD);
		mb.append(orgbd);
		mb.append(",");
		String posbd=this.userView.getResourceString(IResourceConstant.POS_BD);
		mb.append(posbd);
		mb.append(",");

		String gzbd=this.userView.getResourceString(IResourceConstant.GZBD);
		mb.append(gzbd);
		mb.append(",");
		String bybd=this.userView.getResourceString(IResourceConstant.INS_BD);
		mb.append(bybd);
		mb.append(",");
		String pso=this.userView.getResourceString(IResourceConstant.PSORGANS);
		mb.append(pso);
		mb.append(",");
		String fg=this.userView.getResourceString(IResourceConstant.PSORGANS_FG);
		mb.append(fg);
		mb.append(",");
		String gx=this.userView.getResourceString(IResourceConstant.PSORGANS_GX);
		mb.append(gx);
		mb.append(",");
		String jcg=this.userView.getResourceString(IResourceConstant.PSORGANS_JCG);
		mb.append(jcg);
		mb.append(",");
		String[] bdarr=StringUtils.split(mb.toString(),",");
		if(bdarr==null || bdarr.length==0) {
			return "";
		}

		String tmp=StringUtils.join(bdarr, ',');
		tmp = tmp.replace("r", "");
		tmp = tmp.replace("R", "");
		tmp = tmp.replace(" ", "");
		tmp = tmp.replace(",,", ",");
		return tmp;
	}

	private String getInsFilterWhere(String module_id)
	{
		StringBuffer strwhere=new StringBuffer();
		/**用户号*/
		String dbpre=this.userView.getDbname(); //库前缀
		String userid=dbpre+this.userView.getA0100();//人员编号
		/**如果为空，设置一个不可能出现的用户，为了少写几行代码*/
		if(userid==null||userid.length()==0) {
			userid="-1";
		}

		/**人员列表*/
		strwhere.append( " ( upper(U.actorid) in ('");
		strwhere.append(userid.toUpperCase());
		if(("9".equals(module_id)&&this.userView.getStatus()==4)||!"9".equals(module_id)) {
			strwhere.append("','");
			strwhere.append(this.userView.getUserName().toUpperCase());
		}
		strwhere.append("'))");
    	return strwhere.toString();
	}
//---------------------------------------------------------------------------------------------------------
	/**
     * 获得人事异动的待审批任务
     * @param LazyDynaBean paramBean
     *   start_date //开始时间
     *   end_date 结束时间
     *   days 最近几天
     *   query_type 按日期or按时间段 1 or other
     *   tabid 模板号
     *   module_id 模块ID  * 1、人事异动* 2、薪资管理* 3、劳动合同* 4、保险管理* 5、出国管理* 6、资格评审* 7、机构管理* 8、岗位管理* 9、业务申请（自助） * 10、考勤管理* 11、职称评审 12、证照管理
     *   bs_flag 任务类型 1：审批任务 2：加签任务 3：报备任务  4：空任务 10：首页待办任务,包含审批任务和报备任务
     * @param userView
     * @return ArrayList
     */
    public ArrayList getDBList(LazyDynaBean paramBean)
    {
    	ArrayList list=new ArrayList();
    	try
    	{
    		TemplatePendingTaskBo templatePendingTaskBo=new TemplatePendingTaskBo(conn, userView);
    		TemplateNavigationBo bo = new TemplateNavigationBo(this.conn,userView);
    		HashSet tabidSet=new HashSet();
    		String module_id=(String)paramBean.get("module_id");
    		String kq_tabids="";
    		templatePendingTaskBo.isHaveIndex();
			if(module_id==null||!"9".equals(module_id)) //业务申请待办无需过滤考勤模板
			{
			//	kq_tabids=bo.getKqTabIds(module_id);//liuyz 考勤支持业务模版
				TemplateTableParamBo tp=new TemplateTableParamBo(this.conn);
		        kq_tabids=tp.getAllDefineKqTabs(0);
			}
			paramBean.set("kq_tabids", kq_tabids);
    		LazyDynaBean sqlRelationParam=templatePendingTaskBo.getSqlRelationParam(paramBean,userView);//获得查询待办的SQL
    		Object obj=paramBean.get("isorder");
    		ArrayList dataList=new ArrayList();
    		if(!"3".equals(((String)paramBean.get("bs_flag")))){ //1：审批任务 2：加签任务 3：报备任务  4：空任务
				dataList.addAll(templatePendingTaskBo.getTmessageList(paramBean,bo,tabidSet,userView));
			}

    		dataList.addAll(templatePendingTaskBo.getRecordListByUser(sqlRelationParam ,tabidSet,userView)); //获得报批给人员|用户|机构 审批节点的待办任务
    		if(!this.userView.isBThreeUser()){
    			dataList.addAll(templatePendingTaskBo.getRecordlistByRole(((StringBuffer)sqlRelationParam.get("from_where_sql_role")).toString(),(ArrayList)sqlRelationParam.get("valueList"),tabidSet,userView)); //获得报批给角色审批节点的待办任务
			}
    		dataList.addAll(templatePendingTaskBo.getRecordListBySelf(sqlRelationParam ,paramBean,tabidSet,userView));

    		if(!(obj!=null&&"false".equals((obj.toString())))){
    			Collections.sort(dataList,new Comparator () {
                    @Override
                    public int compare(Object o1, Object o2) {
                        if(o1 instanceof LazyDynaBean && o2 instanceof LazyDynaBean){
                        	SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    		Date d=new Date();
                    		String current_str=simpleformat.format(d);
                        	LazyDynaBean e1 = (LazyDynaBean) o1;
                        	LazyDynaBean e2 = (LazyDynaBean) o2;
                        	String e1_start_date=e1.get("start_date")!=null?(String)e1.get("start_date"):current_str;
                        	String e2_start_date=e2.get("start_date")!=null?(String)e2.get("start_date"):current_str;
                        	Date e1_date =d;
                        	Date e2_date =d;
                        	try
                        	{
                        		e1_date=simpleformat.parse(e1_start_date);
                        		e2_date=simpleformat.parse(e2_start_date);
                        	}
                        	catch(Exception e)
                        	{

                        	}
                        	int value=1;
                        	if(e1_date.getTime()>e2_date.getTime()) {
								value=-1;
							} else if(e1_date.getTime()==e2_date.getTime())//liuyz bug31898
							{
								value=0;
							}
                            return value;
                        }
                        throw new ClassCastException("排序出错");
                    }
                });
    		}
    		list.addAll(dataList);
    		paramBean.set("tabidSet", tabidSet);

    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}


    	return list;
    }
	public boolean isSyncTabInfo() {
		return syncTabInfo;
	}
	public void setSyncTabInfo(boolean syncTabInfo) {
		this.syncTabInfo = syncTabInfo;
	}

}
