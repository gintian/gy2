package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Node;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.module.jobtitle.configfile.transaction.DomXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 3, 2010 11:39:04 AM</p> 
 *@author dengc
 *@version 5.0
 */
public class SearchTemplateListTrans extends IBusiness { 

	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			
			HashMap hmMap=(HashMap)this.getFormHM().get("requestPamaHM");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(hmMap.get("b_init")!=null)//如果是点击菜单进入或者是从卡片切换进来
			{
				this.getFormHM().put("tabid", (String)hmMap.get("tabid"));
				hmMap.remove("b_init");
				
				String tabid=(String)hmMap.get("tabid");
				this.getFormHM().put("hiddenItem","");
				this.getFormHM().put("fieldSetSortStr","");
				this.getFormHM().put("lockedItemStr","");
				this.getFormHM().put("orderStr","");
				this.getFormHM().put("filterStr","");
				this.getFormHM().put("codeid", "");
				this.getFormHM().put("isSelectAll", "0");
				this.getFormHM().put("isCompare","0");
				this.getFormHM().put("sortitem","");//人员排序
				
				String  isInitData="1"; //是否需要初始化数据
				if(hmMap.get("isInitData")!=null)
				{
					isInitData=(String)hmMap.get("isInitData");
					hmMap.remove("isInitData");
				}
				this.getFormHM().put("isInitData",isInitData);
				
				if(hmMap.get("operationname")!=null)
				{
					this.getFormHM().put("operationname", (String)hmMap.get("operationname"));
					this.getFormHM().put("staticid", (String)hmMap.get("staticid"));
					hmMap.remove("operationname");hmMap.remove("staticid");
				}
				else
				{
					this.getFormHM().put("operationname","");
					this.getFormHM().put("staticid","");
				}
				
				if(hmMap.get("isEmployee")!=null)
				{
					this.getFormHM().put("isEmployee",(String)hmMap.get("isEmployee"));
					hmMap.remove("isEmployee");
				}
				else
					this.getFormHM().put("isEmployee", "0");
				
				String tasklist_str=(String)hmMap.get("tasklist_str");
				String ins_id_str=(String)hmMap.get("ins_id_str");
				String returnflag=(String)hmMap.get("returnflag");
				String task_id=hmMap.get("task_id")!=null?(String)hmMap.get("task_id"):"0";
				//在待办任务中传递过来的task_id是加密的，所以需要进行解密操作，liuzy 20150923
				if(hmMap.get("wait")!=null&&"1".equals(hmMap.get("wait"))){
				   task_id=PubFunc.decrypt(task_id);
				   hmMap.remove("wait");
				}
				String batch_task=(String)hmMap.get("batch_task");
				this.getFormHM().put("batch_task", batch_task);
				
				/**安全平台改造,若是有卡片模式切换过来,那么templateMap肯定不为空，存放着后台的taskid**/
				HashMap templateMap =(HashMap) this.userView.getHm().get("templateMap");
				/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
				if(!task_id.equals("0")&&templateMap!=null&&!templateMap.containsKey(task_id)){
					throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
				}
				*/
				String ins_id=hmMap.get("ins_id")!=null?(String)hmMap.get("ins_id"):"0";
				//列表，卡片切换不用检索条件
				String index_template = (String)hmMap.get("index_template");
				this.getFormHM().put("index_template",index_template);
				hmMap.remove("index_template");
				this.getFormHM().put("task_id", task_id);
				this.getFormHM().put("ins_id", ins_id);
				this.getFormHM().put("sp_mode","");
				this.getFormHM().put("sp_ctrl","");
				String sp_flag="1";
				if(hmMap.get("sp_flag")!=null)
					this.getFormHM().put("sp_flag",sp_flag);
				
				if(tasklist_str==null)
					tasklist_str="";
				if(tasklist_str.length()!=0){
					this.getFormHM().put("sp_batch","1");
				}else{
					this.getFormHM().put("sp_batch","0");
				}
				if(tasklist_str.length()==0&&task_id!=null&&task_id.length()>0&&!"0".equals(task_id))
					tasklist_str=task_id;
				
				this.getFormHM().put("tasklist_str",tasklist_str);
				if(ins_id_str==null)
					ins_id_str="";
				if(ins_id_str.length()==0&&ins_id!=null&&ins_id.length()>0&&!"0".equals(ins_id))
					ins_id_str=ins_id;
				
			
				TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
				TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
				
				
				String fromModel=(String)hmMap.get("fromModel");
				if("1".equals(isInitData)&&(fromModel==null||!"mb".equalsIgnoreCase(fromModel)))
				{
					/**创建或修临时表*/
					/**发起流程时才需要创建临时表,审批环节不用创建临时表*/
					if("0".equalsIgnoreCase(ins_id))
					{
						//查找模板中是否有多个相同变化后子集
						tablebo.checkIsHaveMutilSub();
						tablebo.createTempTemplateTable(this.userView.getUserName());
						String tablename=this.userView.getUserName()+"templet_"+tabid;  
						RowSet rowSet=dao.search("select count(*) from "+tablename);
						int n=0;
						if(rowSet.next())
							n=rowSet.getInt(1);
						if(n==0)
						{
							bo.autoAddRecord(tablebo,tablename);
						}
						
						/**档案中与模板中的数据进行数据同步*/
						tablebo.syncDataFromArchive();
						
						//解决封板包的问题，后期因注释掉
						if(tablebo.getOperationtype()==0||tablebo.getOperationtype()==5)
							updateSeqNum(tablebo.getInfor_type(),tablebo.getTabid());
					}
					else
					{	
						tablebo.changeSpTableStrut();
						if(tablebo.getOperationtype()!=0&&tablebo.getOperationtype()!=1&&tablebo.getOperationtype()!=2)
						{
					//		tablebo.setImpDataFromArchive_sub(false);
							
							
							ArrayList inslist=new ArrayList();
							inslist.add(ins_id);
							tablebo.setInslist(inslist);
							tablebo.setIns_id(Integer.parseInt(ins_id));
							/**新增任务列表,20080418*/
							
							ArrayList tasklist=new ArrayList();
							if(tasklist_str.length()>0)
							{
								String[] temp=tasklist_str.split(",");
								for(int i=0;i<temp.length;i++)
								{
									if(temp[i]==null||temp[i].length()==0)
										continue;
									/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
									if(templateMap!=null&&!templateMap.containsKey(temp[i])){
										throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
									}
									*/
									tasklist.add(temp[i]);
									
								}
							}
							 tablebo.setTasklist(tasklist);
					       // tablebo.getTasklist().add(task_id); 
							/*//因客户的需求，当流程发起以后，无需将档案中与模板中的数据进行数据同步，故屏蔽掉 liuzy 20150907
							 tablebo.syncDataFromArchive(Integer.parseInt(ins_id),"templet_"+tabid);*/
						}else{//如果不是调入、调出、内部调动
							if(tasklist_str.length()>0)
							{
								String[] temp=tasklist_str.split(",");
								for(int i=0;i<temp.length;i++)
								{
									if(temp[i]==null||temp[i].length()==0)
										continue;
									/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
									if(templateMap!=null&&!templateMap.containsKey(temp[i])){
										throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
									}
									*/
									
								}
							}
						}
					}
					
					this.getFormHM().put("isSyncStruct","1");
				}
				
				if((templateMap==null|| "0".equals(task_id))&&tasklist_str!=null&&!(tasklist_str.trim().length()>0)){
					//如果存放taskid的map为空，那么表明是发起，那么发起时要将taskid放进map中去,如果tasklist_str不为空绝对不会是发起 因此不用将tasklist_str中包含的task_id放进去
					templateMap=new HashMap();
					templateMap.put(task_id, PubFunc.encrypt(task_id));
				}
				this.userView.getHm().put("templateMap",templateMap);
				HttpSession session=(HttpSession)this.getFormHM().get("session");
				HashMap hm=new HashMap();
				ArrayList fieldlist=tablebo.getAllFieldItem();
				hm.put("fieldlist",addOtherField(1,fieldlist,tablebo.getInfor_type()));
				hm.put("templet_"+tabid,"templet");
				/**返回对象类型
				 * =0,返回对象为RecordVo
				 * =1,返回对象为LazyDynaBean对象类型
				 * */
				hm.put("objecttype","0");//RecordVo
				session.setAttribute("templet_"+tabid,hm);
//				session.setAttribute("SYS_FILTER_FACTOR",tablebo.getFactor()); 2014-02-22  dengcan  当模板定义了检索条件，条件选人时会出错，选不到人 
				session.setAttribute("SYS_FILTER_FACTOR","");
				if("1".equals(tablebo.getFilter_by_factor()))
					session.setAttribute("SUPPORT_VARIABLE_SQL","select  *  from   midvariable where nflag=0 and templetid= "+tabid);
				if("1".equals(tablebo.getFilter_by_factor()))
					session.setAttribute("SUPPORT_VARIABLE_SQL","select  *  from   midvariable where nflag=0 and templetid= "+tabid);
				session.setAttribute("MODEL_STRING","RSYD");
				this.getFormHM().put("sys_filter_factor",SafeCode.encode(tablebo.getFactor().replaceAll("\"","@")));
				this.getFormHM().put("filter_by_factor", tablebo.getFilter_by_factor());
				this.getFormHM().put("ins_id_str",ins_id_str);
				this.getFormHM().put("returnflag",returnflag);
				if(hmMap.get("warn_id")==null)
					this.getFormHM().put("warn_id","");
				else
					this.getFormHM().put("warn_id", (String)hmMap.get("warn_id"));
				hmMap.remove("task_id");hmMap.remove("ins_id");hmMap.remove("ins_id_str");hmMap.remove("tasklist_str");
				hmMap.remove("warn_id");
				
				if(hmMap.get("fromModel")==null||!"mb".equalsIgnoreCase((String)hmMap.get("fromModel")))
				{
					this.getFormHM().put("num", "0");
					hmMap.remove("fromModel");
				}
				else if("mb".equalsIgnoreCase((String)hmMap.get("fromModel")))
					hmMap.remove("fromModel");
				this.getFormHM().put("no_sp_yj", tablebo.getNo_sp_yj());
				HashMap cell_param_map = tablebo.getModeCell4();
                String limit_manage_priv="";
                String un  = isPriv_ctrl(cell_param_map, "b0110_2");
                if(un!=null&&un.trim().length()>0)
                    limit_manage_priv+="UN";
                 un  = isPriv_ctrl(cell_param_map, "e0122_2");
                if(un!=null&&un.trim().length()>0)
                    limit_manage_priv+=",UM";
                un  = isPriv_ctrl(cell_param_map, "e01a1_2");
                if(un!=null&&un.trim().length()>0)
                    limit_manage_priv+=",@K";
                un  = isPriv_ctrl(cell_param_map, "parentid_2");
                if(un!=null&&un.trim().length()>0)
                    limit_manage_priv+=",UM";
                this.getFormHM().put("limit_manage_priv", limit_manage_priv);
				
			}//如果是点击菜单进入或者是从卡片切换进来
			else//如果是手工刷新(该交易类会执行两次。第二次就执行这个方法)
			{
				
				String isSyncStruct=(String)this.getFormHM().get("isSyncStruct");
				String tabid=(String)this.getFormHM().get("tabid");//手工刷新用到，不能remove
				String codeid=(String)hmMap.get("a_code");//手工刷新用到，不能remove
				
				
				String  isInitData="1"; //是否需要初始化数据
				if("0".equals(this.getFormHM().get("isInitData")))
				{
					isInitData="0";
				}
				
				TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
				
				ArrayList templateSetList=bo.getAllCell();
				
				String    hiddenItem="";                     //隐藏指标串  以,分割
				String    fieldSetSortStr=(String)this.getFormHM().get("fieldSetSortStr");                //指标排列顺序 以,分割
				String    lockedItemStr=(String)this.getFormHM().get("lockedItemStr");                  //锁定指标串   以,分割
				String    orderStr=(String)this.getFormHM().get("orderStr");                       //人员排序sql串   order by XXXXX
				/**安全平台处理，filterStr传过来时是加密了的，所以现在进行解密**/
				String    filterStr=PubFunc.decrypt((String)this.getFormHM().get("filterStr"));                     //查询过滤条件串  and XXXXXX
				String    isCompare=(String)this.getFormHM().get("isCompare");
				String    tasklist_str=(String)this.getFormHM().get("tasklist_str");
				String    task_id=(String)this.getFormHM().get("task_id");
                if ("0".equals(task_id) && (tasklist_str !=null && tasklist_str.length()>0)){
                    String[] arrTmp= tasklist_str.split(",");
                    if (arrTmp.length>0){
                        if (arrTmp[0].length()>0)
                            task_id=arrTmp[0];
                    }
                    
                }		
				/**安全平台改造判断taskid是否是由后台传来的防止串改信息**/
				HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
				/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
				if(!task_id.equals("0")&&templateMap!=null&&!templateMap.containsKey(task_id)){
					throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
				}
				*/
				String    ins_id=(String)this.getFormHM().get("ins_id");
				HashMap  prechangemap = new HashMap();
				bo.setTaskid(task_id);
//				prechangemap=(HashMap)this.getFormHM().get("prechangemap");
				if(tasklist_str==null)
					tasklist_str="";
				
				if(((String)this.getFormHM().get("hiddenItem")).length()==0)  //设默认隐藏指标
				{
					hiddenItem=bo.getDefaultHiddenitemStr(templateSetList);
				}
				else
					hiddenItem=(String)this.getFormHM().get("hiddenItem");
				
				ArrayList tasklist=new ArrayList();
				if(tasklist_str.length()>0)
				{
					String[] temp=tasklist_str.split(",");
					for(int i=0;i<temp.length;i++)
					{
						if(temp[i]==null||temp[i].length()==0)
							continue;
						/**安全平台改造判断taskid是否是由后台传来的防止串改信息**/
						/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
						if(templateMap!=null&&!templateMap.containsKey(temp[i])){
							throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
						}
						*/
						tasklist.add(temp[i]);
						
					}
				}
				
				if(hmMap.get("selectAll")==null||!"1".equals((String)hmMap.get("selectAll")))
				{
					
					StringBuffer sql=new StringBuffer();
					sql.append("update templet_"+tabid+" set submitflag=0  where 1=1 ");
					if(tasklist!=null&&tasklist.size()>0)
					{
						StringBuffer strins=new StringBuffer();
						for(int i=0;i<tasklist.size();i++)//按任务号查询需要审批的对象20080418
						{
											if(i!=0)
											  strins.append(",");
											strins.append((String)tasklist.get(i));
						}
						sql.append(" and ( task_id in(");
						sql.append(strins.toString());
						sql.append(")");				
						//角色属性是否为汇报关系 “直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”，
						sql.append(" or exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id ");
						sql.append("  and task_id in ("+strins.toString()+") and state=0 ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ) ");
										
					}
					String strsql=sql.toString();
					if(tasklist==null||tasklist.size()==0){
						strsql=strsql.replaceAll("templet_"+tabid,this.userView.getUserName()+"templet_"+tabid);
					dao.update(strsql);
					}
				}
				hmMap.remove("selectAll");
				TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
				if(ins_id!=null&&!"0".equals(ins_id)&& "1".equals((String)hmMap.get("sp_flag"))&&!"61".equals(hmMap.get("businessModel"))&&!"71".equals(hmMap.get("businessModel"))){ //报备和加签的不进入
					//判断当前任务是否当前人审批
					String self=tablebo.getDef_flow_self();   //=1:自定义审批流程
					  if ("1".equals(self)){
							for(int k=0;k<tasklist.size();k++){
								if (tablebo.isDef_flow_self(Integer.parseInt(tasklist.get(k).toString()))){
			                      self="2";
			                      break;
			                  }
							}
						}
					String errorinfo= getInformation(tasklist,self);
					if(errorinfo.trim().length()>0)
						throw new GeneralException(ResourceFactory.getProperty(errorinfo));
				}
				
				
				/**创建或修临时表*/
				/**发起流程时才需要创建临时表,审批环节不用创建临时表*/
				if("1".equals(isInitData)&&(isSyncStruct==null||!"1".equals(isSyncStruct)))
				{
			 		if("0".equalsIgnoreCase(ins_id))
					{
			 			//查找模板中是否有多个相同变化后子集
						tablebo.checkIsHaveMutilSub();
						tablebo.createTempTemplateTable(this.userView.getUserName());
						String tablename=this.userView.getUserName()+"templet_"+tabid;  
						RowSet rowSet=dao.search("select count(*) from "+tablename);
						int n=0;
						if(rowSet.next())
							n=rowSet.getInt(1);
						if(n==0)
						{
							bo.autoAddRecord(tablebo,tablename);
						}
						
						//档案中与模板中的数据进行数据同步 
						tablebo.syncDataFromArchive();
					}
					else
					{	
						tablebo.changeSpTableStrut();
						if(tablebo.getOperationtype()!=0&&tablebo.getOperationtype()!=1&&tablebo.getOperationtype()!=2)
						{
					//		tablebo.setImpDataFromArchive_sub(false);
							ArrayList inslist=new ArrayList();
							inslist.add(ins_id);
							tablebo.setInslist(inslist);
							tablebo.setIns_id(Integer.parseInt(ins_id));
							//新增任务列表,20080418
					     //   tablebo.getTasklist().add(task_id); 
							tablebo.setTasklist(tasklist);
							/*//因客户的需求，当流程发起以后，无需将档案中与模板中的数据进行数据同步，故屏蔽掉 liuzy 20150907
							tablebo.syncDataFromArchive(Integer.parseInt(ins_id),"templet_"+tabid);*/
						}
					}
				}
				if(templateMap==null){//如果存放taskid的map为空，那么表明是发起，那么发起时要将taskid放进map中去,如果tasklist_str不为空绝对不会是发起因此不用将tasklist_str中包含的task_id放进去
					templateMap=new HashMap();
					templateMap.put(task_id, PubFunc.encrypt(task_id));
				}
				ArrayList tableHeadSetList=bo.getTableHeadSetList(templateSetList,fieldSetSortStr,hiddenItem, lockedItemStr,isCompare,tasklist);
				ArrayList tableDataList=bo.getTableData(tableHeadSetList,orderStr,filterStr,isCompare,tasklist,codeid,prechangemap);
				this.getFormHM().put("hasRecordFromMessage", bo.getHasRecordFromMessage());
				
				if(tasklist!=null&&tasklist.size()>0)
				{
					this.getFormHM().put("table_name", "templet_"+tabid);
					this.getFormHM().put("isAppealTable", "1");
				}
				else
				{
					this.getFormHM().put("table_name", this.userView.getUserName()+"templet_"+tabid);
					this.getFormHM().put("isAppealTable", "0");
					this.getFormHM().put("ins_id_str","");
					this.getFormHM().put("ins_id","0");
					this.getFormHM().put("task_id","0");
					
				}
				
				String sp_batch=(String)this.getFormHM().get("sp_batch");
				String batch_task=(String)hmMap.get("batch_task");
				this.getFormHM().put("batch_task", batch_task);
				if(bo.getBo().isBsp_flag()&&bo.getBo().getSp_mode()==0)  //自动流转
				{
		/*			WorkflowBo wbo=new WorkflowBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView); 
	  				String nextNodeStr=wbo.getNextNodeStr(0,0,"0");
	  				this.getFormHM().put("nextNodeStr",nextNodeStr);
					*/
					if("1".equals(sp_batch))
						this.getFormHM().put("applyobj",getApplyObjectNameByxgq(task_id,ins_id,tabid,true));
					else
						this.getFormHM().put("applyobj",getApplyObjectNameByxgq(task_id,ins_id,tabid,false)); //getApplyObjectName(task_id,ins_id,bo.getBo()));
				
				}
				else
				{
					this.getFormHM().put("applyobj",ResourceFactory.getProperty("button.appeal"));
				//	this.getFormHM().put("nextNodeStr","");
				}
				
				 
				
				this.getFormHM().put("nextNodeStr","");
				if(bo.getBo().isBsp_flag())
					this.getFormHM().put("sp_ctrl","1"); //需要审批,又简单模式和通用模式两种
				else
					this.getFormHM().put("sp_ctrl","0");//不需要审批
				this.getFormHM().put("def_flow_self",bo.getBo().getDef_flow_self());//自定义审批流程
				this.getFormHM().put("isDisSubMeetingButton", isDisSubMeetingButton());
				
		/*		if(bo.getBo().getSp_mode()==1&&bo.getBo().isBsp_flag())
				{
					this.getFormHM().put("isFinishTask","0");
					
				}
				if(bo.getBo().getSp_mode()==0&&bo.getBo().isBsp_flag())
				{
					this.getFormHM().put("isFinishTask",isFinishTask(task_id,ins_id));
					
				}*/
				// 审批模式
				if(!"0".equals(ins_id)&&tablebo.isBsp_flag())
				{
					this.getFormHM().put("isFinishTask",isFinishTask(task_id,ins_id));
					
				}
				else
				{
					this.getFormHM().put("isFinishTask","0");
				}
				
				setReadFlag(task_id);
				
				
				this.getFormHM().put("sp_mode",String.valueOf(bo.getBo().getSp_mode()));
				String isSendMessage="0";
				if(bo.getBo().isBemail()&&bo.getBo().isBsms())
					isSendMessage="3";
				else if(bo.getBo().isBemail())
					isSendMessage="1";
				else if(bo.getBo().isBsms())
					isSendMessage="2";
				if(!this.userView.hasTheFunction("2701515")&&!this.userView.hasTheFunction("0C34815")&&!this.userView.hasTheFunction("32015")&&!this.userView.hasTheFunction("325010115")&&!this.userView.hasTheFunction("324010115")&&!this.userView.hasTheFunction("010701")&&!this.userView.hasTheFunction("32115")&&!this.userView.hasTheFunction("3800715"))
					isSendMessage="0";
				
				
				if(checkFlagHmuster(tabid)){
					this.getFormHM().put("checkhmuster","1");
				}else{
					this.getFormHM().put("checkhmuster","0");
				}
				this.getFormHM().put("filter_by_factor", tablebo.getFilter_by_factor());
				this.getFormHM().put("outformlist",tablebo.getMusterOrTemplate());
				/**安全平台改造.平台前端不应当存放sql**/
				//this.getFormHM().put("hmuster_sql", bo.getHmuster_sql());
				this.userView.getHm().put("template_sql", bo.getHmuster_sql());
				this.getFormHM().put("infor_type",String.valueOf(tablebo.getInfor_type()));
				
				if(bo.getBo().isBsp_flag()&&bo.getBo().getSp_mode()==0)  //自动流转
				{
					if("1".equals(sp_batch))
					{
						StringBuffer _sqlstr=new StringBuffer("select distinct ins_id,task_id from t_wf_task where 1=1 ");
						if(tasklist!=null&&tasklist.size()>0)
						{
							StringBuffer strins=new StringBuffer();
							for(int i=0;i<tasklist.size();i++)//按任务号查询需要审批的对象20080418
							{
												if(i!=0)
												  strins.append(",");
												strins.append((String)tasklist.get(i));
												if("0".equals(task_id)){
													task_id=(String)tasklist.get(i);
												}
							}
							_sqlstr.append(" and task_id in(");
							_sqlstr.append(strins.toString());
							_sqlstr.append(")");
						}
						this.frowset=dao.search(_sqlstr.toString());
						int n=0;
						while(this.frowset.next())
						{
							String _startflag=tablebo.isStartNode(this.frowset.getString("ins_id"),this.frowset.getString("task_id"),tabid,bo.getBo().getSp_mode());
							if("0".equals(_startflag))
							{
								n++; 
								break;
							}
						}
						if(n>0)
							this.getFormHM().put("startflag","0");
						else
							this.getFormHM().put("startflag","1");
					}
					else
						this.getFormHM().put("startflag",tablebo.isStartNode(ins_id,task_id,tabid,bo.getBo().getSp_mode()));
				}
				else
					this.getFormHM().put("startflag",tablebo.isStartNode(ins_id,task_id,tabid,bo.getBo().getSp_mode()));
				this.getFormHM().put("_static",String.valueOf(bo.getBo().get_static()));
				
				if(tasklist!=null&&tasklist.size()>0&&tasklist.size()==1)
				{
					this.getFormHM().put("isFinishedRecord",bo.getBo().isFinishedRecord((String)tasklist.get(0)));
				}
				else
					this.getFormHM().put("isFinishedRecord","0");
				 HashMap cell_param_map = tablebo.getModeCell4();
		         String limit_manage_priv="";
		         String un  = isPriv_ctrl(cell_param_map, "b0110_2");
		         if(un!=null&&un.trim().length()>0)
		             limit_manage_priv+="UN";
		          un  = isPriv_ctrl(cell_param_map, "e0122_2");
		         if(un!=null&&un.trim().length()>0)
		             limit_manage_priv+=",UM";
		         un  = isPriv_ctrl(cell_param_map, "e01a1_2");
		         if(un!=null&&un.trim().length()>0)
		             limit_manage_priv+=",@K";
		         un  = isPriv_ctrl(cell_param_map, "parentid_2");
	             if(un!=null&&un.trim().length()>0)
	                 limit_manage_priv+=",UM";
		         this.getFormHM().put("limit_manage_priv", limit_manage_priv);
				 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				 String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
				 FieldItem item = DataDictionary.getFieldItem(onlyname);
				 String generalmessage ="可以输入\"姓名\"";
					if (item != null) {
						generalmessage+=",\""+item.getItemdesc()+"\"";
					}
					String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
					item  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
					if (!(pinyin_field == null|| "".equals(pinyin_field) || "#".equals(pinyin_field)||item==null|| "0".equals(item.getUseflag())))
						generalmessage+=",\""+item.getItemdesc()+"\"";
					generalmessage+="进行查询";
					generalmessage = SafeCode.encode(generalmessage);
					//节点设置权限批量处理解决不了
					String taskid_cop = task_id;
					if(tasklist!=null&&tasklist.size()>0&&task_id!=null&& "0".equals(task_id)){
						if(tasklist.get(0).toString().length()>0)
							taskid_cop = ""+tasklist.get(0);
					}
					if(ins_id!=null&&!"0".equals(ins_id)&&taskid_cop!=null&&!"0".equals(taskid_cop)){
						HashMap FieldPriv  = tablebo.getFieldPriv(taskid_cop, this.getFrameconn());
						String fields= this.userView.getFieldpriv().toString();
						fields = fields.toUpperCase();
						if(FieldPriv!=null){
								Iterator iterator=FieldPriv.entrySet().iterator();
								String key="";
								String value="";
								while(iterator.hasNext())
								{
									Entry entry=(Entry)iterator.next();
									key=(String)entry.getKey();
									key = key.toUpperCase();
									value=(String)entry.getValue();
									if(key.indexOf("_")!=-1){
										key = key.substring(0,key.indexOf("_"));
										if(fields.indexOf(key)!=-1){
										if("0".equals(value))
										{
											if(fields.startsWith(key)){
												fields = fields.replace(key+"0", "");
												fields = fields.replace(key+"1", "");
												fields = fields.replace(key+"2", "");
											}else{
												fields = fields.replace(","+key+"0", "");
												fields = fields.replace(","+key+"1", "");
												fields = fields.replace(","+key+"2", "");
											}
										}else if("1".equals(value)){
											if(fields.startsWith(key)){
												fields = fields.replace(key+"0", key+"1");
												fields = fields.replace(key+"1", key+"1");
												fields = fields.replace(key+"2", key+"1");
											}else{
												fields = fields.replace(","+key+"0", ","+key+"1");
												fields = fields.replace(","+key+"1", ","+key+"1");
												fields = fields.replace(","+key+"2", ","+key+"1");
											}
										}else if("2".equals(value)){
											if(fields.startsWith(key)){
												fields = fields.replace(key+"0", key+"2");
												fields = fields.replace(key+"1", key+"2");
												fields = fields.replace(key+"2", key+"2");
											}else{
												fields = fields.replace(","+key+"0", ","+key+"2");
												fields = fields.replace(","+key+"1", ","+key+"2");
												fields = fields.replace(","+key+"2", ","+key+"2");
											}
										}
										}else{
											fields=fields+","+key+value;
										}
									}else{
										if(fields.indexOf(key)!=-1){
										if("0".equals(value))
										{
											if(fields.startsWith(key)){
												fields = fields.replace(key+"0", "");
												fields = fields.replace(key+"1", "");
												fields = fields.replace(key+"2", "");
											}else{
												fields = fields.replace(","+key+"0", "");
												fields = fields.replace(","+key+"1", "");
												fields = fields.replace(","+key+"2", "");
											}
										}else if("1".equals(value)){
											if(fields.startsWith(key)){
												fields = fields.replace(key+"0", key+"1");
												fields = fields.replace(key+"1", key+"1");
												fields = fields.replace(key+"2", key+"1");
											}else{
												fields = fields.replace(","+key+"0", ","+key+"1");
												fields = fields.replace(","+key+"1", ","+key+"1");
												fields = fields.replace(","+key+"2", ","+key+"1");
											}
										}else if("2".equals(value)){
											if(fields.startsWith(key)){
												fields = fields.replace(key+"0", key+"2");
												fields = fields.replace(key+"1", key+"2");
												fields = fields.replace(key+"2", key+"2");
											}else{
												fields = fields.replace(","+key+"0", ","+key+"2");
												fields = fields.replace(","+key+"1", ","+key+"2");
												fields = fields.replace(","+key+"2", ","+key+"2");
											}
										}
										}else{
											fields=fields+","+key+value;
										}
									
									}
								}
								this.getFormHM().put("nodeprive", fields);
			        	}else{
			        		this.getFormHM().put("nodeprive", "-1");
			        	}
					}else{
						this.getFormHM().put("nodeprive", "-1");
					}
					
					
				if(task_id!=null&&!"0".equals(task_id))
				{
					WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());
					this.getFormHM().put("isEndTask_flow",String.valueOf(ins.isEndNode(Integer.parseInt(task_id),tablebo)));
				}
				else
						this.getFormHM().put("isEndTask_flow","false");
				this.getFormHM().put("allow_def_flow_self",String.valueOf(tablebo.isDef_flow_self(Integer.parseInt(task_id))));	
					
				this.getFormHM().put("generalmessage", generalmessage);
				this.getFormHM().put("isSendMessage", isSendMessage);
				this.getFormHM().put("dbpres",bo.getDbStr());
				if(this.getFormHM().get("staticid")!=null&& "60".equals((String)this.getFormHM().get("staticid")))
					this.getFormHM().put("dbpres",bo.getDbStr("23"));
				this.getFormHM().put("no_priv_ctrl",bo.getBo().getNo_priv_ctrl());
				this.getFormHM().put("sys_filter_factor",SafeCode.encode(bo.getBo().getFactor().replaceAll("\"","@")));
				this.getFormHM().put("taskState",getTaskState(task_id));
				this.getFormHM().put("codeid",codeid);
				this.getFormHM().put("operationtype",String.valueOf(bo.getOperationtype()));
				this.getFormHM().put("isName", bo.validateIsName(tableHeadSetList));
				this.getFormHM().put("lockBean", bo.getLastLockTarget(tableHeadSetList, fieldSetSortStr, lockedItemStr,isCompare));
				this.getFormHM().put("tableHeadSetList", tableHeadSetList);
				this.getFormHM().put("templatelist", tableDataList);
				this.getFormHM().put("templateSetList", templateSetList);
				this.getFormHM().put("prechangemap", prechangemap);
				this.getFormHM().put("hiddenItem", hiddenItem);
				this.getFormHM().put("queryfieldlist", bo.selectField(templateSetList, hiddenItem));
				this.getFormHM().put("combinefieldlist", bo.selectField2(templateSetList, hiddenItem));
				this.getFormHM().put("isShowCondition", "none");
				/**人事异动改造，不能将sql传向前端，所以将sql放在useview中**/
				//this.getFormHM().put("needcondition", bo.getCondition(tableHeadSetList, tasklist, codeid));
				this.userView.getHm().put("template_sql_1", bo.getCondition(tableHeadSetList, tasklist, codeid));
				this.getFormHM().put("codesetidlist", bo.getCodesetid());
				if("1".equals(tablebo.getId_gen_manual())){
					tablebo.existFilloutSequence();
					if("1".equals(tablebo.getExistid_gen_manual())){
				    this.getFormHM().put("sequence", "1");
					}else{
					this.getFormHM().put("sequence", "0");
					}
				}else{
					this.getFormHM().put("sequence", "0");
				}
//				this.getFormHM().put("filterStr","");
				this.getFormHM().put("no_sp_yj",tablebo.getNo_sp_yj());
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	private boolean checkFlagHmuster(String relatTableid){
		boolean checkflag = false;
		String temp=this.userView.getResourceString(5);
		if(temp.trim().length()==0) 
			temp="-1";
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT tabid FROM muster_name where ");
		strsql.append("nmodule='5'");
		strsql.append(" and nPrint="+relatTableid);
//		if(!this.userView.isAdmin()&&!this.userView.getGroupId().equals("1")){
//			strsql.append(" and tabid in (");   
//			strsql.append(temp); 
//			strsql.append(") ");
//		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search(strsql.toString());
			String tabid ="-1";
			while(this.frowset.next()){
				tabid = this.frowset.getString("tabid");
				checkflag=true;
				break;
//				if(!this.userView.isSuper_admin()){
//					if(temp.indexOf(tabid)!=-1){
//						checkflag=true;
//						break;
//					}
//				}else{
//					checkflag=true;
//					break;
//				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return checkflag;
	}
	
	public String getTaskState(String task_id)
	{
		String task_state="-1";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select task_state from t_wf_task where task_id="+task_id+" and flag=1");
			if(rowSet.next())
				task_state=rowSet.getString(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return task_state;
	}
	
	
	/**
	 * 判断其他审批人是否已处理完。
	 * @param task_id
	 * @param ins_id
	 * @return
	 */
	public String isFinishTask(String task_id,String ins_id)
	{
		String flag="0";
		try
		{
			/*//报给特殊角色只能选择其中一个人
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql="select count(*) from t_wf_task_datalink where ins_id="+ins_id+" and task_id<>"+task_id+" and state=0";
			       sql+=" and seqnum=(select seqnum from  t_wf_task_datalink where ins_id="+ins_id+" and task_id="+task_id+")";
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
			{
				if(rowSet.getInt(1)>0)
					flag="1";
			}
			*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return flag;
	}
	
	
	/**
	 * 分析此实例，是否为当前用户发起的申请
	 * @param ins_id
	 */
	private String isStartNode(String ins_id,String task_id,String tabid,int sp_mode)
	{
		String startflag="0"; 
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer buf=new StringBuffer();
			buf.append("select actorid from t_wf_instance where ins_id=?");
			ArrayList list=new ArrayList();
			list.add(ins_id);
			RowSet rset=dao.search(buf.toString(),list);
			if(rset.next())
			{
				/**
				 * 申请人编码
				 * 自助平台用户:应用库前缀+人员编码
				 * 业务平台用户：operuser中的账号
				 */
				String applyobj=rset.getString("actorid");//   ins_vo.getString("actorid");
				String a0100=this.userView.getDbname()+this.userView.getA0100();
				String usrname=this.userView.getUserId();
				if(applyobj!=null&&(applyobj.equalsIgnoreCase(a0100)||applyobj.equalsIgnoreCase(usrname)))
					startflag="1";
				
				if("1".equals(startflag)&&task_id!=null&&!"0".equals(task_id)&&task_id.trim().length()>0&&sp_mode==0)
				{
					rset=dao.search("select nodetype from t_wf_node where tabid="+tabid+" and node_id=(select node_id from t_wf_task where task_id="+task_id+")");
					if(rset.next())
					{
						 
						if(!"1".equals(rset.getString("nodetype"))&&!"9".equals(rset.getString("nodetype")))
							startflag="0";
					}
				}
				
			}
		}
		catch(Exception ex)
		{
			;
		}
		return startflag;
	}
	
	
	
	/**
	 * 得到下一环节审批对象的名称，为了在审批或提交按钮上显示
	 * 审批对象的名称
	 * @param task_id
	 * @param ins_id
	 * @return
	 * @throws GeneralException
	 */
	private String getApplyObjectName(String task_id,String ins_id,TemplateTableBo tablebo)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		ArrayList actorlist=new ArrayList();
		ArrayList nextlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());		
		WF_Node wf_node=null;
		boolean b_end=false;
		try
		{
			if(ins_id==null|| "0".equals(ins_id)||(this.getFormHM().get("businessModel")!=null&& "2".equals((String)this.getFormHM().get("businessModel"))))
			{
				wf_node=tablebo.getWF_StartNode();
				ArrayList list=wf_node.getNextHumanNodeList();
				if(list.size()>0)
				{
					wf_node=(WF_Node)list.get(0);
					actorlist=wf_node.getActorList();
				}
			}
			else
			{
				RecordVo task_vo=new RecordVo("t_wf_task");
				task_vo.setInt("task_id",Integer.parseInt(task_id));
				task_vo=dao.findByPrimaryKey(task_vo);
				if(task_vo==null)
					throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));
				int node_id=task_vo.getInt("node_id");
				wf_node=new WF_Node(node_id,this.getFrameconn(),tablebo);
				/**分析下一个节点是否为END*/
				nextlist=wf_node.getNextNodeList(null);
				if(nextlist.size()==1)
				{
					WF_Node wf_endnode=(WF_Node)nextlist.get(0);
					if(wf_endnode.getNodetype()==NodeType.END_NODE)
					{
						b_end=true;
					}
				}
				
				boolean isKhRelationNode_current=wf_node.isKhRelationNode(node_id); // 判断当前节点是否是考核关系节点，是则模板中对象肯定只有1个
				nextlist=wf_node.getNextHumanNodeList();//取下一个人工节点
				for(int i=0;i<nextlist.size();i++)
				{
					wf_node=(WF_Node)nextlist.get(i);				
					actorlist.addAll(wf_node.getActorList());
				}
				
				//如果自动审批流程中 下一节点也为考核关系角色，但当前对象考核关系表中没有数据，则自动跳级。
				if(!b_end&&actorlist.size()>0&&isKhRelationNode_current)
				{
					WF_Actor _actor=null;
					while(true)
					{
						_actor=(WF_Actor)actorlist.get(0);
						int role_property=_actor.decideIsKhRelation(_actor.getActorid(),_actor.getActortype(),this.getFrameconn());
						if(role_property!=0)
						{
							//根据 ins_id  ,task_id 取得 正在处理的人员
							LazyDynaBean abean=tablebo.getPerson(ins_id,task_id,"templet_"+tablebo.getTabid());
							if(abean==null)
								 abean=tablebo.getPerson2(ins_id,task_id,"templet_"+tablebo.getTabid());
							if(abean!=null)
							{
								ArrayList approverList=tablebo.get_Object_Approvers((String)abean.get("a0100"),(String)abean.get("basepre"),role_property);
								if(approverList.size()>0)
									break;
								else
								{
									int _nodeid=wf_node.getNode_id();
									wf_node=new WF_Node(_nodeid,this.getFrameconn(),tablebo);
									nextlist=wf_node.getNextNodeList(null);
									if(nextlist.size()==1)
									{
										WF_Node wf_endnode=(WF_Node)nextlist.get(0);
										if(wf_endnode.getNodetype()==NodeType.END_NODE)
										{
											b_end=true;
											break;
										}
										else
										{
											actorlist=new ArrayList();
											for(int i=0;i<nextlist.size();i++)
											{
												wf_node=(WF_Node)nextlist.get(i);				
												actorlist.addAll(wf_node.getActorList());
											}
											
										}
									}
								}
							}
							else
								break;
						}
						else
							break;
					}
				}
				
				//actorlist=wf_node.getActorList();
			}	
			/**参与者对象列表*/
			if(!b_end)
			{
				for(int i=0;i<actorlist.size();i++)
				{
					WF_Actor wf_actor=(WF_Actor)actorlist.get(i);
					if(i>0)
						buf.append(",");
					else
					{
						buf.append("报送[");
					}
					
					if(SystemConfig.getPropertyValue("clientName")!=null&& "bjpt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
					{
						String node_id=String.valueOf(wf_actor.getNode_id());
						RowSet rowSet=dao.search("select nodename from t_wf_node where node_id="+node_id+" and tabid="+tablebo.getTabid());
						if(rowSet.next())
							buf.append(rowSet.getString(1));
						rowSet.close();
					}
					else
						buf.append(wf_actor.getActorname());
					
					int role_property=wf_actor.decideIsKhRelation(wf_actor.getActorid(),wf_actor.getActortype(),this.getFrameconn());
					if(role_property!=0)
					{
						this.getFormHM().put("isApplySpecialRole", "1");
					}
					else
						this.getFormHM().put("isApplySpecialRole","0");
					
				}//for i loop end.
			}
			if(buf.length()==0)
			{
				if(b_end)
					buf.append(ResourceFactory.getProperty("button.submit"));					
				else
					buf.append(ResourceFactory.getProperty("button.appeal"));
				this.getFormHM().put("isApplySpecialRole","0");
			}
			else
			{
				buf.append("]处理");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return buf.toString();
	}
	
	/**
	 * 得到下一环节审批对象的名称，为了在审批或提交按钮上显示
	 * 审批对象的名称
	 * @param task_id
	 * @param ins_id
	 * @return
	 * @throws GeneralException
	 */
	private String getApplyObjectNameByxgq(String task_id,String ins_id,String tabid,boolean isBatch)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
		ArrayList actorlist=new ArrayList();
		ArrayList nextlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());		
		WF_Node wf_node=null;
		boolean b_end=false;
		try
		{
			
			int node_id=-1;
			if(!"0".equals(task_id))
			{
				try {
					this.frowset=dao.search("select node_id from t_wf_task where task_id="+task_id);
				
				if(this.frowset.next())
					node_id=this.frowset.getInt(1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(node_id==-1|| "0".equals(ins_id))
			{
				 wf_node=new  WF_Node(this.getFrameconn());
				RecordVo vo=wf_node.getBeginNode(String.valueOf(tabid));
				node_id=vo.getInt("node_id");
			}
			 wf_node=new  WF_Node(node_id,this.getFrameconn());  
			 
			 String sp_flag=wf_node.getSpFlag(wf_node.getExt_param());
			if(sp_flag.length()>0)
			{
					buf.append(sp_flag);
			}
			else
			{
				if(isBatch&&!"0".equals(task_id))
					return "报送&确认";
			 
				ArrayList nextNodeList=wf_node.getNextNodeList(null); //获得下一节点
				if(nextNodeList==null||nextNodeList.size()==0){
					buf.append(ResourceFactory.getProperty("button.appeal"));
					return buf.toString();
				}
				WF_Node nextnode=(WF_Node)nextNodeList.get(0);
				if(nextnode.getNodetype()==9){
					buf.append(ResourceFactory.getProperty("button.submit"));
                }else if  (nextnode.getNodetype()==7){//或汇聚 判断下一节点是否是最后节点，如果是最后节点也显示提交
                    nextNodeList=nextnode.getNextNodeList(null); 
                    if(nextNodeList==null||nextNodeList.size()==0){
                        buf.append(ResourceFactory.getProperty("button.appeal"));
                        return buf.toString();
                    }
                    nextnode=(WF_Node)nextNodeList.get(0);
                    if(nextnode.getNodetype()==9){
                        buf.append(ResourceFactory.getProperty("button.submit"));
                    }
                    else 
                    {
                        buf.append(ResourceFactory.getProperty("button.appeal"));
                    } 
                    
                }
                else 
                {
					buf.append(ResourceFactory.getProperty("button.appeal"));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return buf.toString();
	}
	
	private void setReadFlag(String taskid)
	{
		if(taskid==null|| "".equals(taskid)|| "0".equals(taskid))
			return;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo taskvo=new RecordVo("t_wf_task");
			taskvo.setInt("task_id",Integer.parseInt(taskid));
			taskvo.setInt("bread",1);
			dao.updateValueObject(taskvo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	

	/**
	 * 加上主键以及其它控制字段
	 * @param flag
	 * @param fieldlist
	 */
	private ArrayList  addOtherField(int flag,ArrayList fieldlist,int infor_type)
	{
		ArrayList list=new ArrayList();
        Field item = null;
        String field_name=null;
		for(int i=0;i<fieldlist.size();i++)
		{
            Object obj = fieldlist.get(i);
            if(obj instanceof FieldItem)
            {
                FieldItem fielditem = (FieldItem)obj;
                item = fielditem.cloneField();
            } else
            {
                item = (Field)obj;
            }	
			if(item.isChangeAfter())
				field_name=item.getName()+"_2";
			else if(item.isChangeBefore())
				field_name=item.getName()+"_1";
			else 
				field_name=item.getName();            
           	item.setName(field_name);
            list.add(item);
		}
		Field temp=null;
		if(infor_type==1)
		{
			temp=new Field("a0100",ResourceFactory.getProperty("a0100.label"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setKeyable(true);
			temp.setSortable(false);	
			temp.setLength(10);
			//temp.setSequenceable(true);
			//temp.setSequencename("rsbd.a0100");
			list.add(temp);
	
			temp=new Field("A0101_1",ResourceFactory.getProperty("label.title.name"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setKeyable(false);
			temp.setSortable(false);	
			temp.setReadonly(true);	
			//temp.setNChgstate(1);
			temp.setLength(30);
			list.add(temp);
			
			temp=new Field("basepre",ResourceFactory.getProperty("label.dbase"));
			temp.setDatatype(DataType.STRING);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);	
			temp.setKeyable(true);
			temp.setLength(3);
			list.add(temp);
		
		}
		else if(infor_type==2)
		{
			temp=new Field("B0110",ResourceFactory.getProperty("column.sys.org"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(30);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setCodesetid("UN");
			temp.setKeyable(true);
			list.add(temp);
			 
			temp=new Field("codeitemdesc_1",ResourceFactory.getProperty("general.template.orgname"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(50);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setKeyable(false);
			list.add(temp);
		}
		else if(infor_type==3)
		{
			temp=new Field("E01A1",ResourceFactory.getProperty("column.sys.pos"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(30);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setCodesetid("@K");
			temp.setKeyable(true);
			list.add(temp);
			
			temp=new Field("codeitemdesc_1",ResourceFactory.getProperty("e01a1.label"));
			temp.setDatatype(DataType.STRING);
			temp.setLength(50);
			temp.setVisible(true);
			temp.setNullable(true);
			temp.setKeyable(false);
			list.add(temp);
		}
		
		
		/**提交选择标识*/
		temp=new Field("submitflag","submitflag");
		temp.setDatatype(DataType.INT);
		temp.setVisible(false);
		temp.setNullable(false);
		temp.setSortable(false);
		temp.setValue("0");
		list.add(temp);
		
		
		
		
		
		if(flag==0)
		{
			/**状态标志=0,=1来源消息(其它模板发过来的通知)*/
			temp=new Field("state","state");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
			temp.setValue("0");
			list.add(temp);
		}
		else //审批表结构
		{
			temp=new Field("state","state");
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
			temp.setKeyable(true);//key field

			list.add(temp);
			temp=new Field("ins_id","ins_id");
			temp.setLength(12);
			temp.setDatatype(DataType.INT);
			temp.setVisible(false);
			temp.setNullable(false);
			temp.setSortable(false);
			temp.setKeyable(true);//key field
			list.add(temp);
		}
		return list;
	}
	
	/**
	 * 对人员调入模板业务，需要升级，前台人员列表姓名才不为空
	 * @throws GeneralException
	 */
	private void updateSeqNum(int infor_type,int tabid)throws GeneralException
	{
		String strDesT=null;
		 
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			 /*
			
			strDesT=this.userView.getUserName()+"templet_"+tabid; 
			String sql="select * from "+strDesT ;
			RowSet rowSet=dao.search(sql);
			while(rowSet.next()){
				
				String	seqnum = rowSet.getString("seqnum");
				if(seqnum==null||seqnum.trim().length()==0){
					seqnum=CreateSequence.getUUID();
					if(infor_type==1) 
					{
						dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+rowSet.getString("basepre").toLowerCase()+"'");
					}
					else if(infor_type==2)
					{
						dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where b0110='"+rowSet.getString("b0110")+"'");
					}
					else if(infor_type==3)
					{
						dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where E01A1='"+rowSet.getString("E01A1")+"'");
					}
				}
			}
			rowSet.close();*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
	   public String  isPriv_ctrl(HashMap cell_param_map,String field){
	        String sub_domain="";
	        Document doc = null;
	        Element element=null;
	        StringBuffer sb = new StringBuffer();
	        LazyDynaBean bean = (LazyDynaBean)cell_param_map.get(field);
	        if(bean!=null&&bean.get("sub_domain")!=null)
	            sub_domain=(String)bean.get("sub_domain");
	        sub_domain = SafeCode.decode(sub_domain);
	        if(sub_domain!=null&&sub_domain.trim().length()>5)
	        {
	            try {
	                doc=PubFunc.generateDom(sub_domain);;
	                String xpath="/sub_para/para";
	                XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
	                List childlist=findPath.selectNodes(doc);   
	                if(childlist!=null&&childlist.size()>0)
	                {
	                    element=(Element)childlist.get(0);
	                    String priv =(String)element.getAttributeValue("limit_manage_priv");
	                    if("1".equals(priv)){
	                        if(!this.userView.isSuper_admin()){
	                             
	                            if(this.userView.getManagePrivCodeValue()!=null&&this.userView.getManagePrivCodeValue().length()>=2)
	                                sb.append(" and  codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'");
	                            else{
	                                sb.append(" and 1=2 ");
	                            }
	                        }
	                    }
	                }
	            } catch (JDOMException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
	        }
	
		/*
		 * 是否显示上会 按钮
		 * */
		public String isDisSubMeetingButton(){
			String str = "0";
			String tabid=(String)this.getFormHM().get("tabid");
			try{
				DomXml	domXml = new DomXml();
				String templateId= ","+domXml.getJobtitleTemplateByType(this.frameconn, "5")+",";
				if (templateId.contains(tabid)){
					str="1";
					return str;
				}
				templateId= ","+domXml.getJobtitleTemplateByType(this.frameconn, "6")+",";
				if (templateId.contains(tabid)){
					str="1";
					return str;
				}
		
			}catch(Exception e){
				e.printStackTrace();
			}
			return str;
		}
		
		/**
		 * 判断是否是当前人审批
		 * @param task_ids
		 * @param self
		 * @return
		 */
		public String getInformation(ArrayList task_ids, String self){
			String errorinfo="";
			ContentDAO dao=new ContentDAO(this.frameconn);
			try {
				PendingTask imip=new PendingTask();
				String taskids=""; 
				for(int i=0;i<task_ids.size();i++)
				{
					taskids+=","+(String)task_ids.get(i);
				}
				String sql ="";
				if("2".equals(self)){   // 当自定义审批流程时，应查询t_wf_node_manual表中的数据，liuzy 20150813
					sql="select t.ins_id,t.task_id,t.task_topic,t.node_id,t.actorid,t.actor_type,t.actorname,t.start_date,m.tabid from t_wf_task t,t_wf_node_manual m where t.ins_id=m.ins_id and t.node_id = m.id and t.task_id in ("+taskids.substring(1)+" ) ";
				}else{
					sql ="select ins_id,task_id,task_topic,t_wf_task.node_id,actorid,actor_type,actorname,start_date,t_wf_node.tabid from t_wf_task,t_wf_node where t_wf_task.node_id=t_wf_node.node_id and task_state='3'  and t_wf_task.task_id in ("+taskids.substring(1)+")";
				}
				RowSet  frowset=dao.search(sql);
				RowSet  frowset2=null;
				String actor_type ="";
				String node_id="";
				String tabid ="";
				String task_id="";
				int j=0;
				//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
				ArrayList usernameList=PubFunc.SearchOperUserOrSelfUserName(userView);
				while(frowset.next()){
					j++;
					actor_type = frowset.getString("actor_type");
					node_id = frowset.getString("node_id");
					tabid= frowset.getString("tabid");
					task_id=frowset.getString("task_id");
					String actor_id =frowset.getString("actorid");
					
					{
						LazyDynaBean abean=null;
						HashMap dataMap=new HashMap();
						if("5".equals(actor_type))//本人
						{
							
							String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
							sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0 and tt.a0100='"+userView.getA0100()+"' and lower(tt.basepre)='"+userView.getDbname().toLowerCase()+"'";
							frowset2=dao.search(sql0);
							
							while(frowset2.next())
							{
								
								if(dataMap.get(node_id+task_id)==null)
								{
									dao.update("update t_wf_task_objlink set special_node=1 where task_id="+task_id+" and node_id="+node_id);
									dataMap.put(node_id+task_id,"1");
								}
								
								String username=frowset2.getString("username");
								if(username==null||username.trim().length()==0)
									dao.update("update t_wf_task_objlink set username='"+userView.getDbname().toUpperCase()+userView.getA0100()+"' where seqnum='"+frowset2.getString("seqnum")+"' and task_id="+frowset2.getString("task_id"));
								
							}
							 
						}
						else if("2".equals(actor_type)){//角色

							String scope_field="";
							String containUnderOrg="0";
							frowset2=dao.search("select * from t_wf_node where tabid="+tabid+" and node_id="+node_id);
							String ext_param="";
							Document doc=null;
							Element element=null;
							if(frowset2.next())
								ext_param=Sql_switcher.readMemo(frowset2,"ext_param"); 
							if(ext_param!=null&&ext_param.trim().length()>0)
							{
								doc=PubFunc.generateDom(ext_param);; 
								String xpath="/params/scope_field";
								XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
								List childlist=findPath.selectNodes(doc);
								if(childlist.size()==0){
									xpath="/param/scope_field";
									 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
									 childlist=findPath.selectNodes(doc);
								}
								if(childlist!=null&&childlist.size()>0)
								{
									for(int i=0;i<childlist.size();i++)
									{
										element=(Element)childlist.get(i);
										if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
										{
											scope_field=element.getText().trim();
											if(element.getAttribute("flag")!=null&& "1".equals(element.getAttributeValue("flag").trim()))
												containUnderOrg="1";
										}
									}
								}
							}
							if(scope_field.length()>0)
							{
								String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tabid+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
								sql0+=" and twt.task_id="+task_id+" and "+Sql_switcher.isnull("twt.state","0")+"=0  and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"' ";
								//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
								if(usernameList.size()>0){
									for(int i=0;i<usernameList.size();i++){
										sql0+=" or username='"+usernameList.get(i)+"' ";
									}
								}		
								sql0+= " ) ";
								{
									String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
									//如果角色特征为单位领导或部门领导，则根据直接根据角色特征过滤一下 不走业务范围 1：部门领导 6：单位领导 
									if (actor_id!=null && actor_id.length()>0){
										String role_property="";//角色特征
										frowset2= dao.search("select role_property from t_sys_role where role_id= '"+actor_id+"'");
										if (frowset2.next()){    	
											role_property= frowset2.getString("role_property");
										}
										
										String filterField="";
										if ("1".equals(role_property)){//部门领导
											String e0122=this.userView.getUserDeptId();
											if (e0122!=null &&e0122.length()>0){
												operOrg="UN"+e0122;
											}
											else {
												operOrg="";
											}
										}
										else if ("6".equals(role_property)){//单位领导
											String b0110=this.userView.getUserOrgId();
											if (b0110!=null &&b0110.length()>0){
												operOrg="UN"+b0110;
											}
											else {
												operOrg="";
											}
										}
										
									}
									
									
									
									String codesetid="";
									boolean noSql=true;
									if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
									{ 
										if(scope_field.toUpperCase().indexOf("E0122")!=-1)
										{
											codesetid="UM";
											String value=getSubmitTaskInfo(task_id,"UM");
											if(value.length()>0)
											{ 
												scope_field="'"+value+"'"; 
											}
											else
											{
												noSql=false;
												 
											}
										}
										else if(scope_field.toUpperCase().indexOf("B0110")!=-1)
										{
											codesetid="UN";  
											String value=getSubmitTaskInfo(task_id,"UN");
											if(value.length()>0)
											{
												scope_field="'"+value+"'"; 
											}
											else
											{
												noSql=false;
												 
											}
										}
									}
									else
									{
										String[] temps=scope_field.split("_");
										String itemid=temps[0].toLowerCase(); 
										
										FieldItem _item=DataDictionary.getFieldItem(itemid);
										codesetid=_item.getCodesetid();
									}
									if("UN`".equalsIgnoreCase(operOrg))
									{
										
									}
									else if(noSql)
									{
										if(operOrg!=null && operOrg.length() > 3)
										{
											StringBuffer tempSql = new StringBuffer(""); 
											String[] temp = operOrg.split("`");
											for (int i = 0; i < temp.length; i++) {
												if("1".equals(containUnderOrg))
												{
													tempSql.append(" or "+scope_field+" like '" + temp[i].substring(2)+ "%'");				
												}
												else
												{
													if ("UN".equalsIgnoreCase(codesetid)&& "UN".equalsIgnoreCase(temp[i].substring(0, 2)))
														tempSql.append(" or "+scope_field+"='" + temp[i].substring(2)+ "'");
													else if ("UM".equalsIgnoreCase(codesetid)&& "UM".equalsIgnoreCase(temp[i].substring(0, 2)))
														tempSql.append(" or "+scope_field+" like '" + temp[i].substring(2)+ "%'");				
												}
											}
											
											if(tempSql.length()==0)
											{
												if("UN".equalsIgnoreCase(codesetid))
												{
													if("1".equals(containUnderOrg))
														tempSql.append(" or "+scope_field+" like '"+userView.getUserDeptId()+"%'");
													else
														tempSql.append(" or "+scope_field+"='"+userView.getUserOrgId()+"'");
												}
												else if ("UM".equalsIgnoreCase(codesetid))
												{
													tempSql.append(" or "+scope_field+" like '"+userView.getUserDeptId()+"%'");
												}
											}
											
											if(tempSql.toString().trim().length()==0)
												tempSql.append(" or 1=2 ");
											
											sql0+=" and ( " + tempSql.substring(3) + " ) ";
										}
										else
										{
											if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) // 2014-04-01 dengcan
											{
												if("UN".equalsIgnoreCase(codesetid))
												{
													if("1".equals(containUnderOrg))
														sql0+=" and"+scope_field+" like '"+userView.getUserDeptId()+"%'";
													else
														sql0+=" and "+scope_field+"='"+userView.getUserOrgId()+"'";
												}
												else if ("UM".equalsIgnoreCase(codesetid))
												{
													sql0+=" and"+scope_field+" like '"+userView.getUserDeptId()+"%'";
												}
											}
											else
												sql0+=" and 1=2 ";
										}
									}
									else
									{
										sql0+=" and 1=2 ";
									}
								}
							
								frowset2=dao.search(sql0);
								ArrayList updList=new ArrayList();
								while(frowset2.next())
								{
									String username=frowset2.getString("username");
									if(dataMap.get(node_id+task_id)==null)
									{
										dao.update("update t_wf_task_objlink set special_node=1 where task_id="+task_id+" and node_id="+node_id);
										dataMap.put(node_id+task_id,"1");
									}
									
									
									if(username==null||username.trim().length()==0)
									{
										ArrayList tempList=new ArrayList();
										tempList.add(userView.getUserName());
										tempList.add(frowset2.getString("seqnum"));
										tempList.add(new Integer(frowset2.getString("task_id")));
										updList.add(tempList);
									//	dao.update("update t_wf_task_objlink set username='"+userView.getUserName()+"' where seqnum='"+frowset2.getString("seqnum")+"' and task_id="+frowset2.getString("task_id"));
									}
									
									
								}
								
								if(updList.size()>0)
								{ 
									dao.batchUpdate("update t_wf_task_objlink set username=? where seqnum=? and task_id=?",updList );
								}
								
								 
								sql0="select * from  t_wf_task_objlink  where task_id="+task_id+"   and (username='"+userView.getUserName()+"'   ";
								//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
								if(usernameList.size()>0){
									for(int i=0;i<usernameList.size();i++){
										sql0+=" or username='"+usernameList.get(i)+"' ";
									}
								}	
								sql0+= ")";	
								frowset2=dao.search(sql0);
								if(frowset2.next())
								{
									
								}else 
								{
									String encrypt_task_id=PubFunc.encrypt(task_id);
									imip.updatePending("T","HRMS-"+encrypt_task_id,100,"业务模板",this.userView);
									errorinfo="该单据已被他人锁定处理了！";
								}
							}
							else
							{
								////普通角色也抢单 2013-7-19 dengc
								String	sql0="select * from  t_wf_task_objlink  where task_id="+task_id+"   and ( "+Sql_switcher.isnull("username","' '")+"=' ' or username='"+userView.getUserName()+"' ";
								//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
								if(usernameList.size()>0){
									for(int i=0;i<usernameList.size();i++){
										sql0+=" or username='"+usernameList.get(i)+"' ";
									}
								}	
								sql0+= " )   ";	
								frowset2=dao.search(sql0);
								if(frowset2.next())
								{
									String username=frowset2.getString("username");
									if(username==null||username.trim().length()==0)
										dao.update("update t_wf_task_objlink set username='"+userView.getUserName()+"'  where task_id="+task_id+" and node_id="+node_id);
									 
								}else{
										String encrypt_task_id=PubFunc.encrypt(task_id);
										imip.updatePending("T","HRMS-"+encrypt_task_id,100,"业务模板",this.userView);
										errorinfo="该单据已被他人锁定处理了！";
								}
							}
						
						
						}
					}
					
					
					
				}
				if(frowset2!=null)
					frowset2.close();
				if(frowset!=null)
					frowset.close();
				
				if(j==0){
					//调用第三方接口将单子置为已办
					for(int i=0;i<task_ids.size();i++)
					{
						String taskid =(String)task_ids.get(i);
						String encrypt_task_id=PubFunc.encrypt(taskid);
						imip.updatePending("T","HRMS-"+encrypt_task_id,1,"业务模板",this.userView);
					}
					//查询人是不是都被撤销。如果没有则提示。
					String sqlstr="select count(1) num from t_wf_task_objlink where task_id in ("+taskids.substring(1)+") and state='3'";
					RowSet rowSet = dao.search(sqlstr);
					int coutNum=0;
					if(rowSet.next())
					{
						coutNum = rowSet.getInt("num");
					}
					PubFunc.closeDbObj(rowSet);
					if(coutNum==0)
						return "该单据已处理！";
					else 
						return "";
				}
				
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return errorinfo;
		
			
		}
		
		
		/**
		 * 获得报批人所在的单位  或 部门
		 * @param task_id
		 * @param orgFlag UN:单位  UM：部门
		 * @return
		 */
		private String getSubmitTaskInfo(String task_id,String orgFlag)
		{
			String info="";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String fielditem="e0122";
			if("UN".equalsIgnoreCase(orgFlag))
				fielditem="b0110";
			RowSet rset=null;
			try
			{
				String a0100="";
				//bug34093 业务帐号关联自助用户，或者自助帐号，查询对应的自助或者业务帐号，查询角色代办时一个抢了业务自助帐号都能查看。
				int ins_id=0;
				int node_id=0;
				String state="";
			//	rset=dao.search("select a0100_1 from t_wf_task where task_id="+task_id);
				rset=dao.search("select ins_id,state,node_id from t_wf_task where task_id="+task_id);
				if(rset.next())
				{
					node_id=rset.getInt("node_id");
					ins_id=rset.getInt("ins_id");
					state=rset.getString("state");
				}
				if("07".equals(state))  //驳回
				{ 
					rset=dao.search("select a0100_1 from t_wf_task where node_id="+node_id+" and ins_id="+ins_id+" and state='08' and "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='5' order by task_id desc");
				}
				else
					rset=dao.search("select a0100_1 from t_wf_task where task_id="+task_id); 
				if(rset.next())
					a0100=rset.getString(1);
				if(a0100!=null&&a0100.trim().length()>0)
				{
					if(a0100.length()>3)
					{
						String dbpre=a0100.substring(0,3);
						boolean flag=false;
						ArrayList dblist=DataDictionary.getDbpreList();
						for(int i=0;i<dblist.size();i++)
						{
							if(((String)dblist.get(i)).equalsIgnoreCase(dbpre))
								flag=true;
						}
						if(flag)
						{
							rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
							if(rset.next())
							{
								info=rset.getString(1);
							}
						} 
					}
					
					if(info.length()==0)
					{
						rset=dao.search("select a0100,nbase from operuser where username='"+a0100+"'");
						if(rset.next())
						{
							String _a0100=rset.getString("a0100");
							String _nbase=rset.getString("nbase");
							if(_a0100!=null&&_a0100.length()>0&&_nbase!=null&&_nbase.length()>0)
							{
								a0100 = _nbase+_a0100;
								rset=dao.search("select "+fielditem+" from "+_nbase+"a01 where a0100='"+_a0100+"' ");
								if(rset.next())
								{
									info=rset.getString(1);
								}
							}
							
						}
						
					}
					if(info.length()==0&&"UM".equalsIgnoreCase(orgFlag)) {
						String dbpre=a0100.substring(0,3);
						fielditem="b0110";
						rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
						if(rset.next())
						{
							info=rset.getString(1);
						}
					}
					
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if(rset!=null)
						rset.close();
				}
				catch(Exception e)
				{
					
				}
			}
			return info;
		}
		
		
}
