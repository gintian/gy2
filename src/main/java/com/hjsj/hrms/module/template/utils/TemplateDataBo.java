package com.hjsj.hrms.module.template.utils;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplatePendingTaskBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateItem;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.cyberneko.html.parsers.DOMParser;
import org.xml.sax.InputSource;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Title:TemplateDataBo.java</p>
 * <p>Description>: 模板数据类，列表与卡片界面共用。主要功能如下：</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-11-20 上午11:14:35</p>
 * <p>@version: 7.0</p>
 */
public class TemplateDataBo {
	private Connection conn = null;
	private UserView userView = null;
	private TemplateParam paramBo = null;
	private int tabId;
	private ContentDAO dao;
	private TemplateUtilBo utilBo= null;
	private DbWizard dbw=null;
	private RecordVo table_vo=null;
	//vfs上传附件文件名
	private String fileName="";
	private String hmuster_sql="";//当前模板处理人员的sql
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * 初始化构造函数 tabid
	 */
	public TemplateDataBo (Connection conn,UserView userview,int tabid){
		this.tabId=tabid;
		init(conn,userview);
		this.paramBo = new TemplateParam(conn, userview, tabid);

	}
	/**
	 * 初始化构造函数 传递TemplateParam类，不用新创建了
	 */
	public TemplateDataBo (Connection conn,UserView userview,TemplateParam param){
		this.paramBo = param;
		this.tabId=param.getTabId();
		init(conn,userview);
	}


	/**
	 * 初始化本来，创建一些公共类
	 */
	private void init(Connection conn,UserView userview){
		this.conn = conn;
		this.userView = userview;
		dao = new ContentDAO(conn);
		utilBo= new TemplateUtilBo(conn,this.userView);
		dbw=new DbWizard(this.conn);
	}


	/**
	 * @Title: getFieldPrivMap
	 * @Description:获取模板指标的读写权限。
	 * @param @param allCellList 返回有单元格celllist
	 * @param @param taskId 任务ids
	 * @param @return
	 * @return HashMap
	 * @throws
	 */
	public HashMap getFieldPrivMap(ArrayList allCellList, String taskId) {
		HashMap filedPrivMap = new HashMap();
		/** (变化后指标)数据录入不判断子集和指标权限, 0判断(默认值),1不判断 */
		String insertDataCtrl = this.paramBo.getUnrestrictedMenuPriv_Input();
		HashMap nodePrivMap = new HashMap();
		if (this.paramBo.getSp_mode()==0&&!"3".equals(this.paramBo.getReturnFlag())&&this.paramBo.isBsp_flag()) {// 获取节点的读写权限 我的申请浏览也不需要判断节点权限
			nodePrivMap = utilBo.getFieldPrivByNode(taskId,this.tabId);
		}
		for (int i = 0; i < allCellList.size(); i++) {
			TemplateSet setBo = (TemplateSet) allCellList.get(i);
			String flag = setBo.getFlag();
			if (flag == null || "".equals(flag) || "H".equals(flag)) {
				continue;
			}
			String uniqueId = setBo.getUniqueId();
			String tableFieldName = setBo.getTableFieldName();
			String field_name = setBo.getField_name();
			if ("C".equalsIgnoreCase(flag) ) {
				filedPrivMap.put(uniqueId, "2");
				filedPrivMap.put(tableFieldName, "2");
				continue;
			}
			if("F".equalsIgnoreCase(flag)) {//附件需判断其归档至哪里，多媒体子集和子集需要判断权限
				String flag_miea = "2";
				if(paramBo.getInfor_type()==1) {
					String archive_attach_to = paramBo.getArchive_attach_to();
					if("".equals(archive_attach_to))
						archive_attach_to = "A00";
					if("A01".equalsIgnoreCase(archive_attach_to)) {
						flag_miea = "2";
					}else if("A00".equalsIgnoreCase(archive_attach_to)) {//归档到多媒体
						flag_miea = userView.analyseTablePriv("A00");
					}else{//归档到子集
						flag_miea = this.userView.analyseTablePriv(archive_attach_to);
					}
				}
				if("0".equals(this.paramBo.getNeedJudgPre()))
					flag_miea = "2";
				String fillInfo = (String) this.userView.getHm().get("fillInfo");
				if("1".equals(fillInfo))
					flag_miea = "2";
				filedPrivMap.put(uniqueId, flag_miea);
				filedPrivMap.put(tableFieldName, flag_miea);
				continue;
			}
			if("S".equalsIgnoreCase(flag)){//获取签章节点上设置的读写权限
				String  state="2";
				if (nodePrivMap.get("s_"+setBo.getPageId()+"_"+setBo.getGridno()) != null) {// 如果是无权限,跳出
					state = (String) nodePrivMap.get("s_"+setBo.getPageId()+"_"+setBo.getGridno());
				}
				filedPrivMap.put(uniqueId, state);
				filedPrivMap.put("S_"+setBo.getPageId()+"_"+setBo.getGridno(), state);
				continue;
			}
			if("V".equalsIgnoreCase(flag)){
				if("1".equals(setBo.getReadOnly())){
					filedPrivMap.put(uniqueId, "1");
					filedPrivMap.put(tableFieldName, "1");
				}else{
					filedPrivMap.put(uniqueId, "2");
					filedPrivMap.put(tableFieldName, "2");
				}
				continue;
			}
			String state = "0";
			if (!setBo.isSubflag()) {// 这里用来判断非子集的字段
				state = this.userView.analyseFieldPriv(setBo.getField_name());
				boolean specialItem = setBo.isSpecialItem();
				if (specialItem)
					state = "2";
			} else {// 子集数据
				state = this.userView.analyseTablePriv(setBo.getSetname());
			}
			if ("1".equals(insertDataCtrl)&&setBo.getChgstate()==2)
				state="2";

			// 处理是没有构库的指标
			if (setBo.isABKItem()) {
				//if (!"0".equals(taskId)) {// 如果不是发起人的话,那么就要判断节点的读写权限
				String tableFieldName_lin = "";
				if(setBo.isSubflag()){//子集
					if(tableFieldName.startsWith("t_")){//判断子集权限
						tableFieldName_lin = tableFieldName.substring(2,tableFieldName.length());
						if(tableFieldName_lin.endsWith("_1")){
							tableFieldName_lin = setBo.getSetname().toLowerCase()+"_1";
						}else if(tableFieldName_lin.endsWith("_2")){
							tableFieldName_lin = setBo.getSetname().toLowerCase()+"_2";
						}
					}else
						tableFieldName_lin = tableFieldName;
				}else
					tableFieldName_lin = tableFieldName;
				if (nodePrivMap.get(tableFieldName_lin) != null) {// 如果是无权限,跳出
					state = (String) nodePrivMap.get(tableFieldName_lin);
				}
				//}
				if(!setBo.isExistsThisField()){
					state = "0";
				}
			}
			if (TemplateUtilBo.isJobtitleVoteModule(this.userView) ){
				state="1";
			}
			if ("C".equals(flag)){//计算项
				state="1";
			}
			filedPrivMap.put(uniqueId, state);
			filedPrivMap.put(tableFieldName, state);
		}
		return filedPrivMap;
	}
	/**
	 * @Title: getTaskList
	 * @Description: 将任务号字符串转化为List格式 兼容批量审批模式。
	 * @param task_ids
	 * @return
	 * @return ArrayList
	 */
	private ArrayList getTaskList(String task_ids) {
		ArrayList taskList = new ArrayList();
		String[] lists=StringUtils.split(task_ids,",");
		for(int i=0;i<lists.length;i++)
		{
			taskList.add(lists[i]);
		}
		return taskList;
	}
	/**
	 * @Description: 查找业务类型 0,1,2,3,4,10  对人员调入，人员调出等业务对一些特殊的规则
	 * @author gaohy
	 * @date 2016-1-18 上午11:20:41
	 * @version V7x
	 */
	private int findOperationType(String operationcode)
	{
			/*
			StringBuffer strsql=new StringBuffer();
			strsql.append("select operationtype from operation where operationcode='");
			strsql.append(operationcode);
			strsql.append("'");
			ContentDAO dao=new ContentDAO(this.conn);
			int flag=-1;
			try
			{
				RowSet rset=dao.search(strsql.toString());
				if(rset.next())
					flag=rset.getInt("operationtype");
					//flag=0;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}*/
		int flag=TemplateStaticDataBo.getOperationType(operationcode, conn);
		return flag;
	}

	/**
	 * @Title: getManageSqlWhere
	 * @Description: 获取权限where条件 任务监控时 按权限范围查看数据
	 * @param @param dataTabName
	 * @param @return
	 * @return String
	 */
	private String getManageSqlWhere(String dataTabName) {
		String strB0110Where="";
		String operOrg = this.userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板

		if (TemplateUtilBo.isJobtitleVoteModule(this.userView)){//职称评审投票系统不是使用ehr的用户
			operOrg="UN`";
		}

		String un_1="";
		String um_1="";
		String um_2="";
		String un_2="";
		String um_3="";
		String un_3="";
		if (this.paramBo.getOperationType()==0)
		{
			if(dbw.isExistField(dataTabName, "e0122_2", false))
				um_2="e0122_2";
			if(dbw.isExistField(dataTabName, "b0110_2", false))
				un_2="b0110_2";
		}
		else
		{
			if(dbw.isExistField(dataTabName, "e0122_1", false))
				um_1="e0122_1";
			if(dbw.isExistField(dataTabName, "b0110_1", false))
				un_1="b0110_1";
			if(dbw.isExistField(dataTabName, "e0122_2", false))
				um_3="e0122_2";
			if(dbw.isExistField(dataTabName, "b0110_2", false))
				un_3="b0110_2";
		}
		if(operOrg==null||!"UN`".equalsIgnoreCase(operOrg))
		{
			if((operOrg!=null)&&(!"UN`".equalsIgnoreCase(operOrg)))
			{
				if(operOrg.length() >3)
				{
					String[] temp = operOrg.split("`");
					for (int j = 0; j < temp.length; j++) {
						if (temp[j]!=null&&temp[j].length()>0) {
							String _pre=temp[j].substring(0,2);
							if (this.paramBo.getOperationType()==0)
							{
								if("UN".equalsIgnoreCase(_pre))
								{
									if(un_2.length()>0)
										strB0110Where =strB0110Where+ " or  T."+un_2+" like '" + temp[j].substring(2)+ "%'";
									else if(um_2.length()>0)
										strB0110Where =strB0110Where+ " or  T."+um_2+" like '" + temp[j].substring(2)+ "%'";
								}
								else if("UM".equalsIgnoreCase(_pre)&&um_2.length()>0)
								{
									strB0110Where =strB0110Where+ " or  T."+um_2+" like '" + temp[j].substring(2)+ "%'";
								}
							}
							else
							{
								if("UN".equalsIgnoreCase(_pre))
								{
									if(un_1.length()>0){//bug 37948 审批人没有单据中变化前部门权限，审批过后通过任务监控打开空白
										if(un_3.length()>0){
											strB0110Where =strB0110Where+ " or  (T."+un_1+" like '" + temp[j].substring(2)+ "%'  or (T."+un_3+" like '" + temp[j].substring(2)+ "%' and T.state=1) )";
										}else{
											strB0110Where =strB0110Where+ " or  T."+un_1+" like '" + temp[j].substring(2)+ "%'";
										}
									}
									else if(um_1.length()>0)
										if(um_3.length()>0){//bug 37948 审批人没有单据中变化前部门权限，审批过后通过任务监控打开空白
											strB0110Where =strB0110Where+ " or  (T."+um_1+" like '" + temp[j].substring(2)+ "%'  or (T."+um_3+" like '" + temp[j].substring(2)+ "%' and T.state=1))";
										}else{
											strB0110Where =strB0110Where+ " or  T."+um_1+" like '" + temp[j].substring(2)+ "%'";
										}
								}
								else if("UM".equalsIgnoreCase(_pre)&&um_1.length()>0)
								{
									if(um_3.length()>0){//bug 37948 审批人没有单据中变化前部门权限，审批过后通过任务监控打开空白
										strB0110Where =strB0110Where+ " or  (T."+um_1+" like '" + temp[j].substring(2)+ "%'  or ( T."+um_3+" like '" + temp[j].substring(2)+ "%' and T.state=1 ) )";
									}else{
										strB0110Where =strB0110Where+ " or  T."+um_1+" like '" + temp[j].substring(2)+ "%'";
									}
								}

							}
						}
					}
					//如果单位信息、部门信息不填写的话，可能存在自己申请的单据看不到情况，
					//目前不改bug19063，以后也不做兼容。
				}
				else if(operOrg==null)
				{
					strB0110Where=strB0110Where +" or 1=2 ";
				}
				if(StringUtils.isNotEmpty(strB0110Where)){
					strB0110Where=strB0110Where.substring(3);
					strB0110Where = "("+strB0110Where+")";
				}
			}
		}
		return strB0110Where;
	}

	/**
	 * @Title: isHasSeqnum
	 * @Description:是否启用了新的 Seqnum
	 * @param @param taskList
	 * @param @return
	 * @return boolean
	 */
	private boolean isHasSeqnum(ArrayList taskList) {
		boolean b=false;
		try
		{
			RowSet rset=null;
			String seqnum="";
			if(taskList.size()>0){
				rset=dao.search("select seqnum from t_wf_task_objlink where task_id="+(String)taskList.get(0));
				if(rset.next())
					seqnum=rset.getString(1)!=null?rset.getString(1):"";
			}
			if (seqnum.length()>0)
				b=true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return b;
	}

	/**
	 * @Title: getSql
	 * @Description: 获取数据sql   卡片与列表调用
	 * @param moduleId
	 * @param returnFlag
	 * @param approveFlag
	 * @param dataTabName
	 * @param task_ids
	 * @param objectId
	 * @param isDelete 是否是被撤销的 终止的
	 * @param @return
	 * @param @throws GeneralException
	 * @return String
	 * @throws
	 */
	public String  getSql(String moduleId,String returnFlag,String approveFlag,String dataTabName,
						  String task_ids,String objectId,String filterStr, String isDelete) throws GeneralException {
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
				_withNoLock=" WITH(NOLOCK) ";
			String muster_sql="";//高级花名册用
			boolean bInProcess= (!"0".equals(task_ids));//审批中
			StringBuffer sql=new StringBuffer();
			muster_sql="select "+dataTabName+".* from "+dataTabName;
			if (bInProcess) {//待办，我的申请，我的已办 任务监控
				ArrayList taskList =getTaskList(task_ids);
				StringBuffer strTaskIds=new StringBuffer();
				StringBuffer strInsIds = getIns_ids(task_ids);
				for(int i=0;i<taskList.size();i++)
				{
					if(i!=0)
						strTaskIds.append(",");
					strTaskIds.append((String)taskList.get(i));
					//strInsIds.append(utilBo.getInsId((String)taskList.get(i)));
				}
				muster_sql =muster_sql+" where ins_id in ("+strInsIds.toString()+")";
				sql.append("select T.*,O.task_id as realtask_id,O.submitflag submitflag2,T.seqnum seqnum2 ");
				if(this.paramBo.getInfor_type()==1){
					sql.append(",T.basepre");
					sql.append(Sql_switcher.concat());
					sql.append("'`'");
					sql.append(Sql_switcher.concat());
					sql.append("T.a0100 objectid ");


					sql.append(",T.basepre");
					sql.append(Sql_switcher.concat());
					sql.append("'`'");
					sql.append(Sql_switcher.concat());
					sql.append("T.a0100 objectid_noencrypt ");
				}else if(this.paramBo.getInfor_type()==2){
					sql.append(",T.b0110 objectid ");
					sql.append(",T.b0110 objectid_noencrypt ");
				}else{
					sql.append(",T.e01a1 objectid ");
					sql.append(",T.e01a1 objectid_noencrypt ");
				}
				sql.append("from " + dataTabName+" T "+_withNoLock+"");
				sql.append(",t_wf_task_objlink O "+_withNoLock+" where T.seqnum=O.seqnum  and T.ins_id=O.ins_id");
				String searchSeq=" select seqnum,ins_id from t_wf_task_objlink "+_withNoLock+"";
				if("1".equals(returnFlag)||"2".equals(returnFlag)||"11".equals(returnFlag)||"12".equals(returnFlag)||"13".equals(returnFlag) ){//待办 ，我的已办\首页待办\首页待办列表\来自第三方系统或邮件
					sql.append(" and ( "+Sql_switcher.isnull("O.special_node","0")+"=0 or ("+Sql_switcher.isnull("O.special_node","0")+"=1 and (lower(O.username)='"+this.userView.getUserName().toLowerCase()+"' ");
					if(this.userView.getA0100()!=null&& this.userView.getA0100().trim().length()>0)//liuyz bug 32108 业务用户没有关联自助用户this.userView.getDbname().toLowerCase()+this.userView.getA0100()结果为空串，会查出不属于这个人的数据。
					{
						sql.append(" or lower(O.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"'");
					}
					sql.append(" )   )  ) ");
					searchSeq+=" where ("+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' ";
					if(this.userView.getA0100()!=null&& this.userView.getA0100().trim().length()>0)
					{
						searchSeq+="or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ";
					}
					searchSeq+=" )   ) ) ";
				}
				sql.append("  and O.task_id in ("+strTaskIds.toString()+") and O.tab_id="+this.tabId);
				if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
					sql.append(" and ( "+Sql_switcher.isnull("O.state","0")+"=3 ) ");
				}else {
					sql.append(" and ( "+Sql_switcher.isnull("O.state","0")+"<>3 ) ");
				}

				if(searchSeq.indexOf("where")!=-1)
				{
					searchSeq+=" and task_id in ("+strTaskIds+" )";
					if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
						searchSeq+=" and ( "+Sql_switcher.isnull("state","0")+"=3 ) ";
					}else {
						searchSeq+=" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ";
					}
					searchSeq+=" and ins_id="+dataTabName+".ins_id and seqnum="+dataTabName+".seqnum and submitflag=1 ";
				}
				else
				{
					searchSeq+=" where task_id in ("+strTaskIds+" )";
					if(("4".equals(returnFlag)||"3".equals(returnFlag))&&"true".equalsIgnoreCase(isDelete)) {
						searchSeq+=" and ( "+Sql_switcher.isnull("state","0")+"=3 ) ";
					}else {
						searchSeq+=" and ( "+Sql_switcher.isnull("state","0")+"<>3 ) ";
					}
					searchSeq+=" and ins_id="+dataTabName+".ins_id and seqnum="+dataTabName+".seqnum and submitflag=1 ";
				}
				muster_sql +=" and exists  ( "+searchSeq+" ) ";

				// 人事模板-任务监控、即查看模式：只能看到权限范围内的人员。我的申请,我的已办不需要卡范围
				if ((this.paramBo.getInfor_type()==1)&& ("0".equals(approveFlag))&& !"3".equals(returnFlag) && !"2".equals(returnFlag)) {
					//指定查看的人员 -职称评审
					if ( objectId!=null && objectId.length()>0){
						String nbase =objectId.substring(0,3);
						String a0100 =objectId.substring(3);
						sql.append(" and upper(T.basepre)='"+nbase.toUpperCase()+"'");
						sql.append(" and T.a0100='"+a0100+"'");
						muster_sql+=" and upper(basepre)='"+nbase.toUpperCase()+"' and a0100='"+a0100+"' ";
					}
					if(!"0".equals(this.paramBo.getNeedJudgPre()))//职称评审无需判断权限 (上会材料-评审材料) dengcan 2017-58
					{
						String strB0110Where= getManageSqlWhere(dataTabName);
						String tmp= strB0110Where.replace("T.", "");
						if(tmp.trim().length()>0)
							muster_sql =muster_sql+" and "+tmp;
						if (strB0110Where.length()>0){
							if(sql.indexOf("where")!=-1)
								sql.append(" and "+strB0110Where);
							else
								sql.append(" where "+strB0110Where);
						}
					}
				}
			}
			else {
				sql.append("select T.*,T.submitflag submitflag2,T.seqnum seqnum2 ");
				if(this.paramBo.getInfor_type()==1){
					sql.append(",T.basepre");
					sql.append(Sql_switcher.concat());
					sql.append("'`'");
					sql.append(Sql_switcher.concat());
					sql.append("T.a0100 objectid ");

					sql.append(",T.basepre");
					sql.append(Sql_switcher.concat());
					sql.append("'`'");
					sql.append(Sql_switcher.concat());
					sql.append("T.a0100 objectid_noencrypt ");
				}else if(this.paramBo.getInfor_type()==2){
					sql.append(",T.b0110 objectid ");
					sql.append(",T.b0110 objectid_noencrypt ");
				}else{
					sql.append(",T.e01a1 objectid ");
					sql.append(",T.e01a1 objectid_noencrypt ");
				}
				sql.append("from ");
				sql.append(dataTabName+" T "+_withNoLock+"");
				if  ("9".equals(moduleId)){//自助业务申请
					sql.append(" where T.a0100='");
					sql.append(this.userView.getA0100());
					sql.append("' and T.basepre='");
					sql.append(this.userView.getDbname());
					sql.append("'");
				}
				else {
					sql.append(" where 1=1 ");
				}
				muster_sql=muster_sql+" where 1=1 and submitflag=1";
			}

			if(filterStr!=null && !"".equals(filterStr)){
				//sql.append(filterStr);
				muster_sql+=filterStr.replace("T.", "");;
			}

			//排序
	            /*if((this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
	            		&&(this.paramBo.getOperationType()==8||
	            				this.paramBo.getOperationType()==9))
				{
					String key="b0110";
					if(this.paramBo.getInfor_type()==3)
						key="e01a1";
					sql.append("  order by "+Sql_switcher.isnull("to_id","100000000")+",case when "+key+"=to_id then 100000000 else a0000 end asc ");
				}
				else
					sql.append(" order by a0000");	*/

			String strsql=sql.toString();
			this.userView.getHm().put("template_sql", muster_sql);
			return sql.toString();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 通过task_id得到ins_id
	 * @param taskId
	 * @return
	 */
	private StringBuffer getIns_ids(String task_ids) {
		StringBuffer ins_ids = new StringBuffer("");
		RowSet rowSet=null;
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
				_withNoLock=" WITH(NOLOCK) ";
			String sql = "select ins_id from t_wf_task "+_withNoLock+" where task_id in ("+task_ids+")";
			rowSet = dao.search(sql);
			int i=0;
			while(rowSet.next()) {
				int ins_id = rowSet.getInt("ins_id");
				if(i==0)
					ins_ids.append(ins_id);
				else
					ins_ids.append(","+ins_id);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(rowSet);
		}
		return ins_ids;
	}
	/**
	 * @author lis
	 * @Description: 得到模板列表sql
	 * @date May 31, 2016
	 * @param moduleId
	 * @param returnFlag
	 * @param approveFlag
	 * @param dataTabName
	 * @param task_ids
	 * @param objectId
	 * @param filterStr
	 * @param tableHeadSetList
	 * @return sql
	 * @throws GeneralException
	 */
	public String  getTemplateListSql(String moduleId,String returnFlag,String approveFlag,String dataTabName,
									  String task_ids,String objectId,String filterStr, ArrayList tableHeadSetList) throws GeneralException {
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
				_withNoLock=" WITH(NOLOCK) ";
			String muster_sql="";//高级花名册用
			StringBuffer columnStr = new StringBuffer();
			for (int i = 0; i < tableHeadSetList.size(); i++) {
				LazyDynaBean headBean = (LazyDynaBean) tableHeadSetList.get(i);
				String columnId = headBean.get("item_id").toString();
				String subflag = headBean.get("subflag").toString();
				String state = headBean.get("state").toString();
				String chgstate = headBean.get("chgstate").toString();
				String flag = headBean.get("flag").toString();
				String fieldType = headBean.get("item_type").toString();

				if ("1".equals(subflag)) {//子集
					String sub_domain = headBean.get("sub_domain").toString();
					columnStr.append(",");
					columnStr.append(state);
					columnStr.append(" sub_");
					columnStr.append(columnId);
					columnStr.append(",");
					columnStr.append(chgstate);
					columnStr.append(" chg_");
					columnStr.append(columnId);
				}
				if("F".equals(flag)){//附件
					columnStr.append(",");
					columnStr.append(state);
					columnStr.append(" att_");
					columnStr.append(columnId);
				}
				if("M".equals(fieldType)){//html编辑器
					String inputType =headBean.get("inputType").toString();
					if("1".equals(inputType)){
						columnStr.append(",");
						columnStr.append(state);
						columnStr.append(" htm_");
						columnStr.append(columnId);
					}
				}
			}
			muster_sql="select "+dataTabName+".* from "+dataTabName;
			boolean bInProcess= (!"0".equals(task_ids));//审批中
			StringBuffer sql=new StringBuffer();
			if (bInProcess) {//待办，我的申请，我的已办 任务监控
				ArrayList taskList =getTaskList(task_ids);
				StringBuffer strTaskIds=new StringBuffer();
				StringBuffer strInsIds=new StringBuffer();
				for(int i=0;i<taskList.size();i++)
				{
					if(i!=0)
					{
						strTaskIds.append(",");
						strInsIds.append(",");
					}
					strTaskIds.append((String)taskList.get(i));
					strInsIds.append(utilBo.getInsId((String)taskList.get(i)));
				}
				muster_sql +=" where ins_id in ("+strInsIds+")";
				sql.append("select T.*,O.task_id as realtask_id,O.submitflag submitflag2,T.seqnum seqnum2 ");
				sql.append(columnStr);
				if(this.paramBo.getInfor_type()==1){
					sql.append(",T.basepre");
					sql.append(Sql_switcher.concat());
					sql.append("'`'");
					sql.append(Sql_switcher.concat());
					sql.append("T.a0100 objectid ");
				}else if(this.paramBo.getInfor_type()==2){
					sql.append(",T.b0110 objectid ");
				}else{
					sql.append(",T.e01a1 objectid ");
				}
				sql.append("from " + dataTabName+" T "+_withNoLock+"");
				sql.append(",t_wf_task_objlink O "+_withNoLock+" where T.seqnum=O.seqnum  and T.ins_id=O.ins_id");
				String searchSeq=" select seqnum,ins_id from t_wf_task_objlink "+_withNoLock+"";
				if("1".equals(returnFlag)||"2".equals(returnFlag) ||"11".equals(returnFlag)||"12".equals(returnFlag)||"13".equals(returnFlag)){//待办 ，我的已办\首页待办\首页待办列表\来自第三方系统或邮件
					sql.append(" and ( "+Sql_switcher.isnull("O.special_node","0")+"=0 or ("+Sql_switcher.isnull("O.special_node","0")+"=1 and (lower(O.username)='"+this.userView.getUserName().toLowerCase()+"' ");
					if(this.userView.getA0100()!=null&& this.userView.getA0100().trim().length()>0)//liuyz bug 32108 业务用户没有关联自助用户this.userView.getDbname().toLowerCase()+this.userView.getA0100()结果为空串，会查出不属于这个人的数据。
						sql.append( " or lower(O.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ");
					sql.append(" )   )  ) ");
					searchSeq+=" where ("+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' ";
					if(this.userView.getA0100()!=null&& this.userView.getA0100().trim().length()>0)//liuyz bug 32108 业务用户没有关联自助用户this.userView.getDbname().toLowerCase()+this.userView.getA0100()结果为空串，会查出不属于这个人的数据。
						searchSeq+=" or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ";
					searchSeq+=" )   ) )";
				}
				sql.append("  and O.task_id in ("+strTaskIds.toString()+") and O.tab_id="+this.tabId
						+" and ( "+Sql_switcher.isnull("O.state","0")+"<>3 ) ");//state is null or  state=0
				if(searchSeq.indexOf("where")!=-1)
				{
					searchSeq+=" and task_id in ("+strTaskIds+" ) and ( "+Sql_switcher.isnull("state","0")+"<>3 ) and ins_id="+dataTabName+".ins_id and seqnum="+dataTabName+".seqnum and submitflag=1 ";
				}
				else
				{
					searchSeq+=" where task_id in ("+strTaskIds+" ) and ( "+Sql_switcher.isnull("state","0")+"<>3 ) and ins_id="+dataTabName+".ins_id and seqnum="+dataTabName+".seqnum and submitflag=1 ";
				}
				muster_sql +=" and exists  ( "+searchSeq+" ) ";
				// 人事模板-任务监控、即查看模式：只能看到权限范围内的人员。我的申请,我的已办不需要卡范围
				if ((this.paramBo.getInfor_type()==1)&& ("0".equals(approveFlag))&& !"3".equals(returnFlag) && !"2".equals(returnFlag)) {
					//指定查看的人员 -职称评审
					if ( objectId!=null && objectId.length()>0){
						String nbase =objectId.substring(0,3);
						String a0100 =objectId.substring(3);
						sql.append(" and upper(T.basepre)='"+nbase.toUpperCase()+"'");
						sql.append(" and T.a0100='"+a0100+"'");
						muster_sql+=" and upper(basepre)='"+nbase.toUpperCase()+"' and a0100='"+a0100+"' ";
					}

					String strB0110Where= getManageSqlWhere(dataTabName);
					if (strB0110Where.length()>0){
						if(sql.indexOf("where")!=-1)
							sql.append(" and "+strB0110Where);
						else
							sql.append(" where "+strB0110Where);
						muster_sql+="  and "+strB0110Where.replace("T.", "");
					}
				}
			}
			else {
				sql.append("select T.*,T.submitflag submitflag2,T.seqnum seqnum2 ");
				sql.append(columnStr);
				if(this.paramBo.getInfor_type()==1){
					sql.append(",T.basepre");
					sql.append(Sql_switcher.concat());
					sql.append("'`'");
					sql.append(Sql_switcher.concat());
					sql.append("T.a0100 objectid ");
				}else if(this.paramBo.getInfor_type()==2){
					sql.append(",T.b0110 objectid ");
				}else{
					sql.append(",T.e01a1 objectid ");
				}
				sql.append("from ");
				sql.append(dataTabName+" T "+_withNoLock+"");
				if  ("9".equals(moduleId)){//自助业务申请
					sql.append(" where T.a0100='");
					sql.append(this.userView.getA0100());
					sql.append("' and T.basepre='");
					sql.append(this.userView.getDbname());
					sql.append("'");
				}
				else {
					sql.append(" where 1=1 ");
				}
				muster_sql+=" where 1=1 and submitflag=1";
			}

			if(filterStr!=null && !"".equals(filterStr)){
				sql.append(filterStr);
				muster_sql+=filterStr.replace("T.", "");
			}
			//排序
	            /*if((this.paramBo.getInfor_type()==2||this.paramBo.getInfor_type()==3)
	            		&&(this.paramBo.getOperationType()==8||
	            				this.paramBo.getOperationType()==9))
				{
					String key="b0110";
					if(this.paramBo.getInfor_type()==3)
						key="e01a1";
					sql.append("  order by "+Sql_switcher.isnull("to_id","100000000")+",case when "+key+"=to_id then 100000000 else a0000 end asc ");
				}
				else
					sql.append(" order by a0000");	*/

			//String strsql=sql.toString();
			this.userView.getHm().put("template_sql", muster_sql);
			return sql.toString();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * @Title: getDataList
	 * @Description: 获取卡片左侧人员列表
	 * @param @param moduleId 调用模块号
	 * @param @param approveFlag 审批标记
	 * @param @param returnFlag 调用功能号
	 * @param @param task_ids 任务号
	 * @param @param objectId 只显示某人记录。
	 * @param @return
	 * @return ArrayList
	 * @throws
	 */
		/*public ArrayList getDataList(String moduleId,String returnFlag,String approveFlag,String task_ids,String objectId,String filterStr)
		{
	        DbWizard dbWizard = new DbWizard(this.conn);
	        ArrayList dataList=new ArrayList();
	        try {
	        	boolean bInProcess= (!"0".equals(task_ids));//审批中
	        	String dataTabName=utilBo.getTableName(moduleId,this.tabId,task_ids);
				ArrayList taskList =getTaskList(task_ids);
				StringBuffer strTaskIds=new StringBuffer();
			//	boolean bHasSeqNum= isHasSeqnum(taskList);
	        	String sql =this.getSql(moduleId,returnFlag,approveFlag,dataTabName,task_ids, objectId,filterStr);
	            RowSet rset=dao.search(sql);
	            if (this.paramBo.getInfor_type() == 1) {//人事模板
	                while(rset.next()){
	                    HashMap datamap = new HashMap();
	                    String a0100 =rset.getString("a0100");
	                    String basepre =rset.getString("basepre");
	                    String state=rset.getString("state"); //是否来自通知单
	                    String name=null;
	                    if(this.paramBo.getOperationType() == 0){//人员调入型
	                        if (dbWizard.isExistField(dataTabName, "a0101_2", false)) {
	                            name =rset.getString("a0101_2");
	                        }
	                    }else{
	                        name=rset.getString("a0101_1");
	                    }
	                    if(name==null)
	                    	name="";
	                    LazyDynaBean lazyvo = new LazyDynaBean();
	                    lazyvo.set("name", name);
	                    lazyvo.set("objectid", basepre+"`"+a0100);
	                    lazyvo.set("basepre", basepre);
	                    lazyvo.set("a0100", a0100);
	                    lazyvo.set("state", state+"");
	                    if (bInProcess){
		                    lazyvo.set("ins_id",rset.getString("ins_id"));
		                    lazyvo.set("realtask_id", rset.getString("realtask_id"));
	                   // 	if (bHasSeqNum)
	                    	{
	                    	}
	                    	else {
	                    		lazyvo.set("realtask_id", "0");
	                    	}
	                    }
	                    else {
	                    	lazyvo.set("ins_id", "0");
	                    	lazyvo.set("realtask_id", "0");
	                    }


	                    String submitflag="0";
	                    submitflag=rset.getString("submitflag2");
	                    if (bInProcess){//审批中
	                    }else{
	                    	submitflag=rset.getString("submitflag");
	                    }
	                    lazyvo.set("submitflag", submitflag);
	                    dataList.add(lazyvo);
	                }
	            }else{
	                int group_no=0;
	                String _to_id="";
	                String b0110="";
	                while(rset.next()){
	                    boolean isvalue=false;
	                    if(this.paramBo.getInfor_type() == 2){
	                        b0110=rset.getString("b0110");
	                    }else{
	                        b0110=rset.getString("e01a1");
	                    }
	                    String name="";
	                    if(this.paramBo.getOperationType() == 5){
	                       name=rset.getString("codeitemdesc_2");
	                    }else{
	                        name=rset.getString("codeitemdesc_1");
	                    }

	                    if (this.paramBo.getOperationType() == 8 || this.paramBo.getOperationType() == 9) {// 如果是合并划转
	                        if(rset.getString("to_id")==null){//如果获得的to_id==null,什么都不用处理

	                        }else{
	                            if(_to_id.length()==0){//这说明是第一个有to_id的数据那么就是第一组
	                                _to_id=rset.getString("to_id");
	                                group_no=1;
	                            }
	                            if(!_to_id.equalsIgnoreCase(rset.getString("to_id"))){//如果_to_id不等于取出记录的to_id那么说明组号是另外一个分组了
	                                group_no++;
	                                _to_id=rset.getString("to_id");
	                            }

	                        }
	                    }
	                    LazyDynaBean lazyvo = new LazyDynaBean();
	                    if(name==null)
	                    	name="";
	                    lazyvo.set("name", name);
	                    lazyvo.set("objectid", b0110);
	                    String state=rset.getString("state"); //是否来自通知单
	                    lazyvo.set("state", state+"");
	                    if (bInProcess){
		                    lazyvo.set("ins_id",rset.getString("ins_id"));
	                    	lazyvo.set("realtask_id", rset.getString("realtask_id"));

	                    }
	                    else {
	                    	lazyvo.set("ins_id", "0");
	                    	lazyvo.set("realtask_id", "0");
	                    }
	                    dataList.add(lazyvo);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return dataList;
	    } */

	/**
	 * 返回模板中所有的指标项(变量、子集区域)列表
	 * @return 列表中存放的是FieldItem对象
	 */
	public ArrayList getAllFieldItem()throws GeneralException
	{
		ArrayList fieldItemList = new ArrayList();
		try {
			ArrayList templateItemList = getAllTemplateItem(true);
			for (int i = 0; i < templateItemList.size(); i++) {
				TemplateItem templateItem = (TemplateItem) templateItemList.get(i);
				String field_name = templateItem.getFieldName();

				// 将TemplateItem转换为FiledItem
				FieldItem fldItem = (FieldItem) templateItem.getFieldItem().cloneItem();
				fldItem.setItemid(field_name);
				if (templateItem.isbSubSetItem()) {
					// fldItem.setFormula(temItem.get) ;
				}
				fldItem.setItemtype(templateItem.getFieldType());
				fldItem.setFormula(templateItem.getCellBo().getFormula());
				if (templateItem.isbSubSetItem()){
					fldItem.setVarible(2);
				}
				fieldItemList.add(fldItem);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldItemList;
	}


	/**
	 * @Title: getAllTemplateItem
	 * @Description: 返回模板中所有的指标项(变量、子集区域)列表
	 * @param @param isAddSysItem 是否添加系统指标 b0110_1 e0122_1 e01a1_1 a0101_1
	 * @param @throws GeneralException
	 * @return ArrayList 列表中存放的是TemplateItem对象
	 * @throws
	 */
	public ArrayList getAllTemplateItem(boolean isAddSysItem)throws GeneralException
	{
		try
		{
			ArrayList allFieldList=new ArrayList();
			ArrayList fieldList=utilBo.getAllTemplateItem(this.tabId,this.paramBo.getOutPriPageList());
			String str="";
			for(int i=0;i<fieldList.size();i++){

				TemplateItem temItem=(TemplateItem)fieldList.get(i);
				String field_name=temItem.getFieldName();
				allFieldList.add(temItem);
				str=str+","+field_name.toLowerCase();
			}
			//如果系统指标未引入到单元格，则增加系统指标
			if(this.paramBo.getInfor_type()==1&&this.paramBo.getOperationType()!=0){
				if (str.indexOf("b0110_1")==-1)
					allFieldList.add(utilBo.getTempItem(ResourceFactory.getProperty("b0110.label"),
							"B0110","A","UN","A01","1","0","0","0"));
				if (str.indexOf("e0122_1")==-1)
					allFieldList.add(utilBo.getTempItem(ResourceFactory.getProperty("e0122.label"),
							"E0122","A","UM","A01","1","0","0","0"));
				if (str.indexOf("e01a1_1")==-1)
					allFieldList.add(utilBo.getTempItem(ResourceFactory.getProperty("e01a1.label"),
							"E01A1","A","@K","A01","1","0","0","0"));
				if (str.indexOf("a0101_1")==-1)
					allFieldList.add(utilBo.getTempItem("姓名","A0101","A","0","A01","1","0","0","0"));
			}
			return allFieldList;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}


	/**
	 * @Description: 获得当前模板处理人员的sql
	 * @author gaohy
	 * @date 2016-1-18 上午11:28:07
	 * @version V7x
	 */
	public void getTempSql(ArrayList headSetList,String orderStr,String filterStr,ArrayList tasklist,String _codeid)
	{
		ArrayList list=new ArrayList();
		HashMap endMap = new HashMap();
		HashMap preMap = new HashMap();
		HashMap submitMap=new HashMap();
		String strsql="";
		try
		{
			String _withNoLock="";
			if(Sql_switcher.searchDbServer()==Constant.MSSQL) //针对SQLSERVER
				_withNoLock=" WITH(NOLOCK) ";
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=null;
			StringBuffer sql=new StringBuffer();
			sql.append("select * from templet_"+this.tabId+" "+_withNoLock+" where 1=1 ");
			if(tasklist!=null&&tasklist.size()>0)
			{
				StringBuffer strins=new StringBuffer();
				for(int i=0;i<tasklist.size();i++)//按任务号查询需要审批的对象20080418
				{
					if(i!=0)
						strins.append(",");
					strins.append((String)tasklist.get(i));
				}
				sql.append(" and   exists (select null from t_wf_task_objlink "+_withNoLock+" where templet_"+this.tabId+".seqnum=t_wf_task_objlink.seqnum and templet_"+this.tabId+".ins_id=t_wf_task_objlink.ins_id ");
				sql.append("  and task_id in ("+strins.toString()+") and tab_id="+this.tabId+" and ( state=0 or  state is null ) and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) )  ");

				String _sql=" select submitflag,seqnum,task_id from t_wf_task_objlink "+_withNoLock+" where  task_id in ("+strins.toString()+") and tab_id="+this.tabId+" and ( state=0 or  state is null ) and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ";
				rowSet=dao.search(_sql);
				while(rowSet.next())
				{
					String submitflag="0";
					if(rowSet.getString("submitflag")!=null&& "1".equals(rowSet.getString("submitflag")))
						submitflag="1";
					submitMap.put(rowSet.getString("seqnum"),rowSet.getString("task_id")+"`"+submitflag);
				}

			}
			String tempname="";
			if(this.paramBo.getInfor_type()==2){
				tempname="b0110";
				if(this.paramBo.getOperationType()==5)
				{
					DbWizard dbwizard=new DbWizard(this.conn);
					if(tasklist==null||tasklist.size()==0){
						if(dbwizard.isExistField(this.userView.getUserName()+"templet_"+this.tabId, "parentid_2",false)){
							tempname="parentid_2";
						}
					}else{
						if(dbwizard.isExistField("templet_"+this.tabId, "parentid_2",false)){
							tempname="parentid_2";
						}
					}
				}
			}
			if(this.paramBo.getInfor_type()==3){
				tempname="e01a1";
				if(this.paramBo.getOperationType()==5)
				{
					DbWizard dbwizard=new DbWizard(this.conn);
					if(tasklist==null||tasklist.size()==0){
						if(dbwizard.isExistField(this.userView.getUserName()+"templet_"+this.tabId, "parentid_2",false)){
							tempname="parentid_2";
						}
					}else{
						if(dbwizard.isExistField("templet_"+this.tabId, "parentid_2",false)){
							tempname="parentid_2";
						}
					}
				}
			}
			if(_codeid!=null&&_codeid.trim().length()>2)
			{
				String value=_codeid.substring(2);
				if(this.paramBo.getOperationType()!=0){
					if(this.paramBo.getInfor_type()==1){
						if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
						{
							sql.append(" and b0110_1 like '"+value+"%'");
						}
						else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
						{
							sql.append(" and e0122_1 like '"+value+"%'");
						}
						else if("@K".equalsIgnoreCase(_codeid.substring(0,2)))
						{
							sql.append(" and e01a1_1 like '"+value+"%'");
						}
					}else if(this.paramBo.getInfor_type()==2){
						if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
						{
							sql.append(" and "+tempname+" like '"+value+"%'");
						}
						else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
						{
							sql.append(" and "+tempname+" like '"+value+"%'");
						}
					}else if(this.paramBo.getInfor_type()==3){
						if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
						{
							sql.append(" and "+tempname+" like '"+value+"%'");
						}
						else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
						{
							sql.append(" and "+tempname+" like '"+value+"%'");
						}

					}
				}else{
					for(int i=0;i<headSetList.size();i++){
						LazyDynaBean abean=(LazyDynaBean)headSetList.get(i);
						if(this.paramBo.getInfor_type()==1){
							if("UN".equalsIgnoreCase(_codeid.substring(0,2))&& "b0110".equalsIgnoreCase(abean.get("field_name").toString()))
							{
								sql.append(" and b0110_2 like '"+value+"%'");
								break;
							}
							else if("UM".equalsIgnoreCase(_codeid.substring(0,2))&& "e0122".equalsIgnoreCase(abean.get("field_name").toString()))
							{
								sql.append(" and e0122_2 like '"+value+"%'");
								break;
							}
							else if("@K".equalsIgnoreCase(_codeid.substring(0,2))&& "e01a1".equalsIgnoreCase(abean.get("field_name").toString()))
							{
								sql.append(" and e01a1_2 like '"+value+"%'");
								break;
							}
						}else if(this.paramBo.getInfor_type()==2){
							if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
							{
								sql.append(" and "+tempname+" like '"+value+"%'");
								break;
							}
							else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
							{
								sql.append(" and "+tempname+" like '"+value+"%'");
								break;
							}
						}else if(this.paramBo.getInfor_type()==3){

							if("UN".equalsIgnoreCase(_codeid.substring(0,2)))
							{
								sql.append(" and "+tempname+" like '"+value+"%'");
								break;
							}
							else if("UM".equalsIgnoreCase(_codeid.substring(0,2)))
							{
								sql.append(" and "+tempname+" like '"+value+"%'");
								break;
							}

						}
					}

				}
			}
			strsql=sql.toString();
			if(tasklist==null||tasklist.size()==0)
				strsql=strsql.replaceAll("templet_"+tabId,this.userView.getUserName()+"templet_"+tabId);
			filterStr = PubFunc.keyWord_reback(filterStr);
			if(filterStr.length()>0)
				filterStr=" and "+filterStr;
			strsql +=filterStr+" ";
//				sql.append(filterStr);
			if(orderStr.length()==0){
				if((this.paramBo.getInfor_type()==3&&this.paramBo.getOperationType()==8)||((this.paramBo.getInfor_type()==2)
						&&(this.paramBo.getOperationType()==8||this.paramBo.getOperationType()==9)))
				{
					String key="b0110";
					if(this.paramBo.getInfor_type()==3)
						key="e01a1";
					strsql+="  order by "+Sql_switcher.isnull("to_id","'bb0000000'")+",case when "+key+"=to_id then 100000000 else a0000 end asc ";
				}
				else
					strsql+=" order by a0000";
			}
			else
				strsql +=orderStr;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		this.hmuster_sql=strsql;
	}
	/**
	 * @Title: getListDataList
	 * @Description:   获取人事异动列表的展示数据 同 getDataList
	 * @param @param moduleId
	 * @param @param returnFlag
	 * @param @param approveFlag
	 * @param @param task_ids
	 * @param @param objectId
	 * @param @param filterStr
	 * @param @return
	 * @return ArrayList
	 * @throws
	 */
		/*public ArrayList getListDataList(String moduleId,String returnFlag,String approveFlag,
				String task_ids,String objectId,String filterStr)
		{
	        DbWizard dbWizard = new DbWizard(this.conn);
	        ArrayList dataList=new ArrayList();
	        RowSet rset=null;
	        try {
	        	boolean bInProcess= (!"0".equals(task_ids));//审批中
	        	String dataTabName=utilBo.getTableName(moduleId,this.tabId,task_ids);
				ArrayList taskList =getTaskList(task_ids);
				StringBuffer strTaskIds=new StringBuffer();
		//		boolean bHasSeqNum= isHasSeqnum(taskList); 暂不考虑4年前的结构了
	        	String sql =this.getSql(moduleId, returnFlag,approveFlag, dataTabName,task_ids, objectId,filterStr);
	        	if(filterStr!=null && !"".equals(filterStr)){
	        		sql=" select * from ( "+sql+" ) templet where 1=1 "+filterStr;
	        	}
	           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	           SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
	           rset=dao.search(sql);
	            if (this.paramBo.getInfor_type() == 1) {
	                while(rset.next()){
	                    LazyDynaBean lazyvo = new LazyDynaBean();
	                    ArrayList fieldList=getAllTemplateItem();
	                    for(int i=0;i<fieldList.size();i++)
	        			{
	                        TemplateItem temItem=(TemplateItem)fieldList.get(i);
	                        String field_name=temItem.getFieldName();
	                        if("photo".equals(field_name) || "attachment".equals(field_name) || "signature".equals(field_name)){
	                        	continue;
	                        }
	                        //人事异动列表展现时无需获取子集的数据，在点击子集按钮时才会去数据库中查询，因此在这里进行特殊处理，传递空值。
	                        if(field_name.startsWith("t_")){
	                        	lazyvo.set(field_name, "");
	                        	continue;
	                        }
	                        String fieldData="";
	                        if(temItem.getFieldType().equalsIgnoreCase("D"))
	                        {
	                        	if(rset.getTime(field_name)!=null)
	                        	{
		                        	if(temItem.getFieldItem().getDisplayid()==25) //disformat=25: 1990.01.01 10:30
		                        	{
		                        		fieldData=dateFormat.format(rset.getTimestamp(field_name));
		                        	}
		                        	else
		                        		fieldData=dateFormat2.format(rset.getDate(field_name));
	                        	}
	                        }
	                        else
	                        	fieldData=rset.getString(field_name)==null?"":rset.getString(field_name);
	                        lazyvo.set(field_name, fieldData);
	        			}
	                    if (bInProcess){
		                    lazyvo.set("ins_id",rset.getString("ins_id"));
	               //     	if (bHasSeqNum)
	                    	{
	                    		lazyvo.set("realtask_id", rset.getString("realtask_id"));
	                    	}
	                    	else {
	                    		lazyvo.set("realtask_id", "0");
	                    	}
	                    }
	                    else {
	                    	lazyvo.set("ins_id", "0");
	                    	lazyvo.set("realtask_id", "0");
	                    }
	                    String submitflag="0";
	                    if (bInProcess){//审批中
	                    	submitflag=rset.getString("submitflag2");
	                    }else{
	                    	submitflag=rset.getString("submitflag");
	                    }
	                    String state=rset.getString("state"); //是否来自通知单
	                    lazyvo.set("state", state+"");
	                    lazyvo.set("submitflag", submitflag);
	                    lazyvo.set("a0100", rset.getString("a0100"));
	                    lazyvo.set("basepre", rset.getString("basepre"));
	                    lazyvo.set("seqnum", rset.getString("seqnum"));
	                    dataList.add(lazyvo);
	                }
	            }else{
	                int group_no=0;
	                String _to_id="";
	                String b0110="";
	                while(rset.next()){
	                    boolean isvalue=false;
	                    if(this.paramBo.getInfor_type() == 2){
	                        b0110=rset.getString("b0110");
	                    }else{
	                        b0110=rset.getString("e01a1");
	                    }
	                    String name="";
	                    if(this.paramBo.getOperationType() == 5){
	                       name=rset.getString("codeitemdesc_2");
	                    }else{
	                        name=rset.getString("codeitemdesc_1");
	                    }

	                    if (this.paramBo.getOperationType() == 8 || this.paramBo.getOperationType() == 9) {// 如果是合并划转
	                        if(rset.getString("to_id")==null){//如果获得的to_id==null,什么都不用处理

	                        }else{
	                            if(_to_id.length()==0){//这说明是第一个有to_id的数据那么就是第一组
	                                _to_id=rset.getString("to_id");
	                                group_no=1;
	                            }
	                            if(!_to_id.equalsIgnoreCase(rset.getString("to_id"))){//如果_to_id不等于取出记录的to_id那么说明组号是另外一个分组了
	                                group_no++;
	                                _to_id=rset.getString("to_id");
	                            }

	                        }
	                    }
	                    LazyDynaBean lazyvo = new LazyDynaBean();
	                    lazyvo.set("name", name);
	                    lazyvo.set("objectid", b0110);
	                    if (bInProcess){
		                    lazyvo.set("ins_id",rset.getString("ins_id"));
		                    lazyvo.set("realtask_id", rset.getString("realtask_id"));

	                    }
	                    else {
	                    	lazyvo.set("ins_id", "0");
	                    	lazyvo.set("realtask_id", "0");
	                    }
	                    String state=rset.getString("state"); //是否来自通知单
	                    lazyvo.set("state", state+"");
	                    dataList.add(lazyvo);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        finally
	        {
	        	PubFunc.closeDbObj(rset);
	        }
	        return dataList;
	    } */

	/**
	 * 取得输出花名册及以WORD、EXCEL模板
	 * @return 返回 LazyDynaBean对象列表,对象
	 * 包括三个属性
	 * id 花名册号
	 * name 名称
	 * flag 标志位 =0花名册 =1输出模板 =2登记表
	 */
	public ArrayList getMusterOrTemplate()
	{
		ArrayList list=new ArrayList();
		/**取花名册列表*/
		ContentDAO dao=null;
		RowSet rset=null;
		try
		{
			StringBuffer strsql=new StringBuffer();
			String strtabid=null;
			String card_str= paramBo.getCard_str();
			String muster_str= paramBo.getMuster_str();
			/**登记表*/
			dao=new ContentDAO(this.conn);
			if(!((card_str==null|| "".equals(card_str))) && paramBo.getOperationType()!=0)
			{
				strtabid=card_str.replaceAll("`",",");
				strsql.append("select tabid,name from rname where tabid in (");
				strsql.append(strtabid);
				strsql.append(")");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("tabid",String.valueOf(this.tabId));
					bean.set("id",rset.getString("tabid"));
					bean.set("name",rset.getString("name"));
					bean.set("flag","2");
					list.add(bean);
				}
			}
			/**高级花名册*/
			if(!(muster_str==null|| "".equals(muster_str)))
			{
				strtabid=muster_str.replaceAll("`",",");
				strsql.append("select tabid,cname from muster_name where tabid in (");
				strsql.append(strtabid);
				strsql.append(")");
				rset=dao.search(strsql.toString());
				while(rset.next())
				{
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("tabid",String.valueOf(this.tabId));
					bean.set("id",rset.getString("tabid"));
					bean.set("name",rset.getString("cname"));
					bean.set("flag","0");
					list.add(bean);
				}
			}
			/**模板单据WORD/EXCEL模板*/
			strsql.setLength(0);
			//liuyz 查询单人多人模版
			strsql.append("select tp_id,name,content,filetype from t_wf_template where tabid=");
			strsql.append(this.tabId);
			rset=dao.search(strsql.toString());
			DOMParser parser = new DOMParser();
			while(rset.next())
			{
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("tabid",String.valueOf(this.tabId));
				bean.set("id",rset.getString("tp_id"));
				bean.set("name",rset.getString("name"));
				bean.set("filetype",rset.getString("filetype"));//liuyz 单人模版还是多人模版
				bean.set("flag","1");

				InputStream in = null;
				try
				{
					bean.set("isHtml","true");//liuyz 导出单人、多人模版支持word直接上传 true表示是转换为html格式上传的。
					in = rset.getBinaryStream("content");
					InputSource inputsource=new InputSource(in);
					parser.parse(inputsource);
					org.w3c.dom.Document doc=parser.getDocument();
					org.w3c.dom.Node node=doc.getDocumentElement().getFirstChild();
					if(node.getNamespaceURI()==null)//直接上传word此值为null
					{
						bean.set("isHtml","false");
					}
					else if(node.getNamespaceURI().length()==0)
						continue;
				}
				catch(Exception ee)
				{
					//ee.printStackTrace();
					continue;
				} finally {
					PubFunc.closeIoResource(in);
				}

				list.add(bean);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取表格内已有的人员 加密的nbase+a0100
	 * @param sql
	 * @return
	 */
	public ArrayList getNbaseA0100List(String sql){
		ArrayList list = new ArrayList();

		RowSet rset=null;
		try
		{
			rset=dao.search(sql);
			while(rset.next()){
				String nbase = rset.getString("basepre");
				String a0100 = rset.getString("a0100");

				list.add(PubFunc.encrypt(nbase+a0100));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return list;
	}
	/**
	 * 获取登录人所有代办任务的taskid
	 * @param module_id
	 * @param tabid
	 * @param bs_flag
	 * @param task_count
	 * @param combine_num
	 * @param combine_nodeid
	 * @return
	 * @throws Exception
	 */
	public ArrayList getDbTaskForUser(String tabid, String bs_flag, int task_count, int combine_num, String task_state, String combine_nodeid) throws Exception{
		ArrayList taskIdArr = new ArrayList();
		ArrayList taskIdArr_ = new ArrayList();
		ArrayList taskIdArr_en = new ArrayList();
		String taskId = "";
		String taskId_en = "";
		try{
			LazyDynaBean paramBean=new LazyDynaBean();
			paramBean.set("start_date", "");
			paramBean.set("end_date", "");
			paramBean.set("days", "");
			paramBean.set("query_type", "");
			paramBean.set("tabid", tabid);
			paramBean.set("module_id", "100");
			paramBean.set("bs_flag", bs_flag);
			TemplatePendingTaskBo templatePendingTaskBo=new TemplatePendingTaskBo(this.conn,this.userView);
			ArrayList dataList=templatePendingTaskBo.getDBList(paramBean,this.userView);
			LazyDynaBean abean=null;
			for(Iterator t=dataList.iterator();t.hasNext();)
			{
				abean=(LazyDynaBean)t.next();
				String states = (String)abean.get("states");
				String nodeid = (String)abean.get("node_id");
				if(!states.equals(task_state))
					continue;
				String task_id = (String)abean.get("taskid_noEncrypt");
				if(nodeid.equals(combine_nodeid)) {
					taskIdArr.add(task_id);
					taskIdArr_en.add(PubFunc.encrypt(task_id));
				}
			}
			if(task_count<combine_num){
				for(int i=0;i<task_count&&i<taskIdArr.size();i++){
					taskId+=taskIdArr.get(i)+",";
					taskId_en +=taskIdArr_en.get(i).toString()+",";
				}
			}else if(task_count>=combine_num){
				int a = (int)Math.ceil(task_count/(combine_num*1.0));
				for(int j=(a-1)*combine_num;j<task_count&&j<taskIdArr.size();j++){
					taskId+=taskIdArr.get(j)+",";
					taskId_en +=taskIdArr_en.get(j).toString()+",";
				}
			}

			if(taskId.lastIndexOf(",")!=-1){
				taskId = taskId.substring(0,taskId.length()-1);
				taskId_en = taskId_en.substring(0,taskId_en.length()-1);
			}
			taskIdArr_.add(taskId);
			taskIdArr_.add(taskId_en);
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return taskIdArr_;
	}
	/**
	 * @Title: isHaveReadFieldPriv
	 * @Description: 判断此模板页是否显示，
	 * （1）有插入指标，且插入指标全部为无权限。
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public boolean isHaveReadFieldPriv(String pageId ,String taskid) {
		boolean bHavePriv = true;
		try {
			ArrayList cellList =  utilBo.getPageCell(this.tabId,Integer.parseInt(pageId));
			HashMap privMap = this.getFieldPrivMap(cellList, taskid);
			for (int i = 0; i < cellList.size(); i++) {
				TemplateSet setBo = (TemplateSet) cellList.get(i);
				String flag = setBo.getFlag();
				if ("".equals(flag) || "H".equalsIgnoreCase(flag)) {
					continue;
				}
				if (setBo.isABKItem()) {
					bHavePriv = false;
					if (privMap.get(setBo.getUniqueId()) != null) {
						String rwPriv = (String) privMap.get(setBo.getUniqueId());
						if ("1".equals(rwPriv) || "2".equals(rwPriv)) {
							bHavePriv = true;
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bHavePriv;
	}

	/**
	 * 通过底层函数进行文件保存
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	public void updateDAO(RecordVo vo, String fileid,String a0100,String basepre,String ins_id,
						  String tablename) throws GeneralException {
		InputStream in=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			if(StringUtils.isNotBlank(fileid)){
				String tempdir=System.getProperty("java.io.tmpdir");
				String pathFileName = tempdir + File.separator + fileid;
				vo.setString("a0100",a0100);
				vo.setString("basepre",basepre);
				if (!"0".equals(ins_id)){
					vo.setString("ins_id",ins_id);
				}
				if(StringUtils.isNotEmpty(this.fileName)) {
					String fname=this.fileName;
					in=VfsService.getFile(fileid);
					int indexInt=fname.lastIndexOf(".");
					String ext=fname.substring(indexInt,fname.length());
					vo.setString("ext",ext);
				}else {
					File file=new File(pathFileName);//要转换的文件
					in=new FileInputStream(file);
				}
				//vfs改造 图片存储改为fileid
				switch (Sql_switcher.searchDbServer()) {
					case Constant.ORACEL:
						Blob blob = getOracleBlob(in, tablename, a0100, basepre);
						vo.setObject("photo", blob);
						break;
					default:
						vo.setObject("photo", in);
						break;
				}
				vo.setString("fileid", fileid);
				dao.updateValueObject(vo);
			}else{//bug 49240 微信删除头像保存，头像又显示出来了,库中数据没有清空。
				switch (Sql_switcher.searchDbServer()) {
					case 2 :{
						StringBuffer sql = new StringBuffer();
						sql.append("update  ");
						sql.append(tablename);
						sql.append(" set photo=EMPTY_BLOB(),ext='',fileid='' where a0100=\'");
						sql.append(a0100);
						sql.append("' and basepre='");
						sql.append(basepre);
						sql.append("'");
						if (!"0".equals(ins_id)) {
							sql.append(" and ins_id='").append(ins_id).append("' ");
						}
						dao.update(sql.toString());
						break;
					}
					default :
						StringBuffer sql = new StringBuffer();
						sql.append("update  ");
						sql.append(tablename);
						sql.append(" set photo=null,ext='',fileid='' where a0100=\'");
						sql.append(a0100);
						sql.append("' and basepre='");
						sql.append(basepre);
						sql.append("'");
						if (!"0".equals(ins_id)) {
							sql.append(" and ins_id='").append(ins_id).append("' ");
						}
						dao.update(sql.toString());
				}
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		}finally {
			PubFunc.closeIoResource(in);
		}
	}

	/**
	 * @param vo
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(String pathFileName,String tablename,String a0100,String basepre) throws FileNotFoundException, IOException {
		File file=new File(pathFileName);//要转换的文件
		FileInputStream inputStream=new FileInputStream(file);
		Blob blob=getOracleBlob(inputStream, tablename, a0100, basepre);
		return blob;
	}

	private Blob getOracleBlob(InputStream inputStream,String tablename,String a0100,String basepre) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select photo from ");
		strSearch.append(tablename);
		strSearch.append(" where a0100='");
		strSearch.append(a0100);
		strSearch.append("' and basepre='");
		strSearch.append(basepre);
		strSearch.append("' ");
		strSearch.append("  FOR UPDATE");
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set photo=EMPTY_BLOB() where a0100='");
		strInsert.append(a0100);
		strInsert.append("' and basepre='");
		strInsert.append(basepre);
		strInsert.append("'");
		OracleBlobUtils blobutils=new OracleBlobUtils(this.conn);
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),inputStream); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}


	/*------类属性----------------*/
	public TemplateParam getParamBo() {
		return paramBo;
	}
	public void setParamBo(TemplateParam paramBo) {
		this.paramBo = paramBo;
	}
	public String getHmuster_sql() {
		return hmuster_sql;
	}
	public TemplateUtilBo getUtilBo() {
		return utilBo;
	}
	public void setUtilBo(TemplateUtilBo utilBo) {
		this.utilBo = utilBo;
	}
}

