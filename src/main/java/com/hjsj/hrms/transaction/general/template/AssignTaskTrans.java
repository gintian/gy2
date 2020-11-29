/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateUtilBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.SendMessageBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Title:任务指派</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 30, 200611:29:28 AM
 * @author chenmengqing
 * @version 4.0
 */
public class AssignTaskTrans extends IBusiness {

	private ArrayList getTaskList(String batch_task)throws GeneralException
	{
		ArrayList tasklist=new ArrayList();
		String[] lists=StringUtils.split(batch_task,",");
		StringBuffer strsql=new StringBuffer();
		strsql.append("select * from t_wf_task where task_id in (");
		HashMap templateMap =(HashMap) this.userView.getHm().get("templateMap");
		for(int i=0;i<lists.length;i++)
		{
			if(i!=0)
				strsql.append(",");
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 215-04-10
			if(templateMap!=null&&!templateMap.containsKey(lists[i])){
				throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			}
			*/
			strsql.append(lists[i]);
		}
		strsql.append(")");
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
			{
				RecordVo taskvo=new RecordVo("t_wf_task");
				taskvo.setInt("task_id",rset.getInt("task_id"));
				taskvo.setInt("ins_id",rset.getInt("ins_id"));
				tasklist.add(taskvo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return tasklist;
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
	private boolean isSelectedTaskId(ContentDAO dao,String tabid,String task_id)throws GeneralException
    {
	    boolean b=true;
        try
        {
            String sqlstr ="select count(*) from templet_"+tabid 
                +" where  seqnum in   (select  seqnum  from t_wf_task_objlink where  "
                +"   task_id="+task_id+"   and submitflag=1  and (state is null or  state=0 ) and ("
                +Sql_switcher.isnull("special_node","0")+"=0  or ( "
                +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
            this.frowset=dao.search(sqlstr);
            if(this.frowset.next())
            {
                if(this.frowset.getInt(1)==0)
                   b=false; 
            }
                    
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return b;
    }
	
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		ArrayList tasklist=new ArrayList();//所有要处理的任务
		String sp_mode=(String)hm.get("sp_mode");
		String tabid=(String)hm.get("tabid");
		String taskid=(String)(String)hm.get("taskid");
		String sp_batch=(String)hm.get("sp_batch");
        if(sp_batch==null|| "".equals(sp_batch))
            sp_batch="0";//单个任务审批
		/**安全平台改造，判断taskid是否存在后台**/
		HashMap templateMap = (HashMap) this.userView.getHm().get("templateMap");
		/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
		if(templateMap!=null && !templateMap.containsKey(taskid) && !"1".equals(sp_batch)){//如果是批量审批,就不用在这里面做处理了，直接在批量审批方式中判断就可以了
			throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
		}
		*/
		String ins_id=(String)hm.get("ins_id");
		String msgs ="";
		//hm.remove("overflag");
		/**批量审批*/
		
		
		ContentDAO dao=new ContentDAO(this.frameconn);
		String unDealedTaskIds="";//这次未处理单据号 批量审批时才有用
		String beginRejectTaskIds="";  //在批量审批时，将发起人驳回的情况的taskid存储起来
		boolean beginRejectFlag=false;  //判断有没有发起人驳回的情况 liuzy 20151130
		if("1".equals(sp_batch))
		{
			String batch_task=(String)hm.get("batch_task");
			ArrayList tmpTasklist=getTaskList(batch_task);

			LazyDynaBean actorBean=(LazyDynaBean)hm.get("actor");//对话框中的数据。包括：{content=, sp_yj=02, pri=1, flag=2}
			String flag=(String)actorBean.get("flag");//=1报批，=2驳回,=3确认 
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			
			//过滤单据：去除未选中的单据			
			for(int i=0;i<tmpTasklist.size();i++)
            {
			    RecordVo taskvo= (RecordVo)tmpTasklist.get(i);
                taskid=taskvo.getString("task_id");
                
                if (isSelectedTaskId(dao, tabid, taskid)){
                	String ins_id_reject=taskvo.getString("ins_id");
                    //判断当前任务节点是不是初始节点 =1为初始节点，=0不是初始节点，对于批量审批而已，有一个不是初始节点的单据，那么startflag=1
                    String startflag=tablebo.isStartNode(ins_id_reject,taskid,tabid,tablebo.getSp_mode());
                    if("1".equals(startflag) && "2".equals(flag)) //把单据是发起节点且要驳回的单据记录下来。 liuzy 20151130
                    {
                    	 if("".equals(beginRejectTaskIds)){
                    		 beginRejectTaskIds=taskid;
                    	 }else{
                    	     beginRejectTaskIds=beginRejectTaskIds+","+taskid;
                    	 }
                     	 unDealedTaskIds=unDealedTaskIds+","+taskid;
                     	 beginRejectFlag=true;
                    }else{
                         tasklist.add(taskvo); 
                    }
                }
                else {//记录下来，后续页面刷新时会展现未处理的单据。
                    unDealedTaskIds=unDealedTaskIds+","+taskid ;
                }
            }    
		}
		else//单任务审批
		{
			RecordVo taskvo=new RecordVo("t_wf_task");
			taskvo.setInt("task_id",Integer.parseInt(taskid));
			taskvo.setInt("ins_id",Integer.parseInt(ins_id));	
			tasklist.add(taskvo);
		}
		
		if(sp_mode==null|| "".equals(sp_mode))
			sp_mode="0";		
		try
		{
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			if(!"1".equals(sp_batch)){//验证是否存在串号 wangrd 2015-07-10
				if (!tablebo.taskIsMatchedInstance(Integer.parseInt(ins_id), Integer.parseInt(taskid))){
					Category.getInstance(this.getClass()).error("存在串号：ins_id"+ins_id+" taskid："+taskid);
					throw new GeneralException("检测到浏览器异常，请关闭所有浏览器重新登录审批！");
				}
			}
			tablebo.getTasklist().add(taskid);
			ArrayList fieldlist=tablebo.getAllFieldItem();
			String srcTab="templet_"+Integer.parseInt(tabid);
			
			LazyDynaBean actor=(LazyDynaBean)hm.get("actor");//对话框中的数据。包括：{content=, sp_yj=02, pri=1, flag=2}
			String flag=(String)actor.get("flag");//=1报批，=2驳回,=3确认
			if ("1".equals(flag)||"3".equals(flag)){//如果没填写审批意见 则自动写入同意 wangrd 2015-01-26
			     if ("01".equals(((String)actor.get("sp_yj"))) && "".equals(((String)actor.get("content")))) {
			         actor.set("content", ResourceFactory.getProperty("label.agree"));//"默认同意" 
			     }else if ("02".equals(((String)actor.get("sp_yj"))) && "".equals(((String)actor.get("content")))) { //当选择不同意，且没有填写审批意见，则自动写入不同意，liuzy 2015-07-01
			         actor.set("content", ResourceFactory.getProperty("label.nagree"));//"默认不同意" 
			     }
			}
			ArrayList addlist = new ArrayList();
			ArrayList movelist = new ArrayList();
			for(int i=0;i<tasklist.size();i++)
			{
				ins_id=((RecordVo)tasklist.get(i)).getString("ins_id");
				taskid=((RecordVo)tasklist.get(i)).getString("task_id");
				
				
				//自动流程在判断变迁条件时，已在GetNextNodeStrTrans.java中判断 
				if(!(tablebo.isBsp_flag()&&tablebo.getSp_mode()==0))
				{
					Boolean bCalc=false;
					if("0".equals(taskid)||"".equals(taskid) || "1".equals(tablebo.isStartNode(taskid))){
		    			if(tablebo.getAutoCaculate().length()==0){
		    				if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
		    					bCalc=true;
		    				}
		    			}
		    			else if("1".equals(tablebo.getAutoCaculate())){
		    				bCalc=true;
		    			}	
		    		}else {
		    			if(tablebo.getSpAutoCaculate().length()==0){
		    				if("true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute"))){
		    					bCalc=true;
		    				}
		    			}
		    			else if("1".equals(tablebo.getSpAutoCaculate())){
		    				bCalc=true;
		    			}
		    		}
					
					if(bCalc){
						tablebo.batchCompute(ins_id);
					}
					if(!"2".equals(flag)){
						 tablebo.checkMustFillItem(srcTab,fieldlist,Integer.parseInt(taskid));
						 String noCheckTemplateIds=SystemConfig.getPropertyValue("noCheckTemplateIds");  //system.properties -->  noCheckTemplateIds=12,88
						//20141210  dengcan  汉口银行行长在批准单据时嫌速度太慢，与刘红梅商量针对行长的单据在审批时无需审核，提高程序执行效率
						if(noCheckTemplateIds!=null&&Integer.parseInt(taskid)>0&&(","+noCheckTemplateIds+",").indexOf(","+tablebo.getTabid()+",")!=-1)  
						{
								
							
						}
						else
							tablebo.checkLogicExpress(srcTab, Integer.parseInt(taskid), fieldlist);
						tablebo.setValidateM_L(true);
						  
					}
				}
				else if(!"2".equals(flag))
					 tablebo.setValidateM_L(true);
				
				/**  如果为合并 和 划转业务 需判断同一组中的记录是否都被选中，同时去掉没有指定（划转|合并）目标记录的选中标记 */
				if(tablebo.getInfor_type()==2||tablebo.getInfor_type()==3)
				{
					StringBuffer strsql=new StringBuffer(""); 
					strsql.append("select * from ");
					strsql.append(srcTab); 
//					strsql.append(" where task_id=");
//					strsql.append(taskid);
//					strsql.append(" and submitflag=1 ");
					strsql.append(" where  seqnum  in   (select  seqnum   from t_wf_task_objlink where  ");
					strsql.append("    task_id="+taskid+"  and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");	
					if(tablebo.getOperationtype()==8||tablebo.getOperationtype()==9)
					{
						tablebo.checkSelectedRule(strsql.toString(),srcTab,taskid);
					}				
				}
				else//如果是人员
				{
					/**编制控制*/
					if(flag!=null && ("1".equals(flag) || "3".equals(flag))&&tablebo.isHeadCountControl(Integer.parseInt(taskid))&&false){//报批和提交时验证超编
						StringBuffer sbsql = new StringBuffer("");//查询的sql语句
						sbsql.append("select * from ");
						sbsql.append(srcTab);
						sbsql.append(" where  seqnum in   (select seqnum   from t_wf_task_objlink where  ");
						sbsql.append("   task_id="+taskid+"  and submitflag=1  and (state is null or  state=0 ) and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");
						///源库与目标库
						String srcDb = "";//源库
						String destinationDb = "";//目标库
						srcDb = tablebo.getDestinationDb(sbsql.toString());
						destinationDb = tablebo.getDestBase(); //移库模板的目标库
						if("".equals(destinationDb)){//说明不是移库模板
							destinationDb = srcDb;
						}
						///对那些库进行编制控制
						PosparameXML pos = new PosparameXML(this.getFrameconn());//得到constant表中UNIT_WORKOUT对应的参数
						String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");//对哪个库进行编制控制
						dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
						if(dbs.length()>0){
							if(dbs.charAt(0)!=','){
								dbs=","+dbs;
							}
							if(dbs.charAt(dbs.length()-1)!=','){
								dbs=dbs+",";
							}
						}
						int currentOperation = tablebo.getOperationtype();//模板类型
						String addFlag = tablebo.getAddFlag(currentOperation,dbs,srcDb,destinationDb);//1:新增。0：修改 2：不控制
						
						ScanFormationBo scanFormationBo=new ScanFormationBo(this.getFrameconn(),this.userView);
							
						if(("1".equals(addFlag) || "0".equals(addFlag)) && scanFormationBo.doScan()){//如果需要编制控制
							
							///得到所有变化后的指标（包含子集中的指标）
							StringBuffer allFields = new StringBuffer("");//所有的指标（如果是子集，也要把所包含的指标提取出来）
							ArrayList allFieldsList = new ArrayList();//list(0)为变化后的指标。list(1)为map.键为变化后的子集，键值为该子集中涉及到的指标
							allFieldsList = tablebo.getAllFields(tabid);
							if(allFieldsList.size()>0){
								ArrayList afterFields = (ArrayList)allFieldsList.get(0);//变化后的指标（仅仅是指标）
								ArrayList temp_list = new ArrayList();//所有的指标（包括子集中的）
								temp_list.addAll(afterFields);
								HashMap temp_map = (HashMap)allFieldsList.get(1);//所有子集的map(包括兼职子集)。键全为小写
								
								Set key = temp_map.keySet();
								for (Iterator it = key.iterator(); it.hasNext();) {
									String s = (String) it.next();
									ArrayList innerlist = (ArrayList)temp_map.get(s);
									temp_list.addAll(innerlist);
								}
								
								//将所有变化后的指标（包括子集中的指标）变为字符串的形式
								for(int m=0;m<temp_list.size();m++){
									String str = (String)temp_list.get(m);
									allFields.append(str+",");
								}
								if(allFields.length()>0 && allFields.charAt(allFields.length()-1)==','){
									allFields.setLength(allFields.length()-1);
								}
								String itemids = "all";
								if(currentOperation!=1 && currentOperation!=2){//不是移库型
									itemids = allFields.toString();
								}
								if(scanFormationBo.needDoScan(destinationDb,itemids)){//当前模板的指标是否能引起编制检查
									ArrayList beanlist = new ArrayList();
									//开始传list。list中存放着bean.
									String partType = "0";//兼职指标以何种方式维护。=0：以罗列指标的形式   =1：在兼职子集中
									partType = tablebo.getPartType(temp_map);
									if("0".equals(partType)){
										beanlist = tablebo.getBeanList_1(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map,tabid);
									}else{
										beanlist = tablebo.getBeanList_2(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map);
									}
									scanFormationBo.execDate2TmpTable(beanlist);
									String mess=scanFormationBo.isOverstaffs();
									if(!"ok".equals(mess)){
										if("warn".equals(scanFormationBo.getMode())){//提示，继续。
											msgs += mess;
										}else{
											throw GeneralExceptionHandler.Handle(new Exception(mess));
										}
									}
								} //当前模板的指标能引起编制检查 结束
							} //allFieldsList.size()>0 结束
						} //需要编制控制 结束 
					}
				} //人员 结束
				
			} //tasklit end loop
			
			//开始把审批流数据写入数据库
			
			if("1".equals(sp_mode))//手工指派
				generalApply(hm,tasklist,addlist,movelist);
			else//自动流转
			{
				boolean isEnd = false;
				String rejectObj=(String)hm.get("rejectObj");
				String specialOperate=(String)hm.get("specialOperate"); //业务模板中人员需要报送给各自领导进行审批处理
				String specialRoleUserStr=(String)hm.get("specialRoleUserStr"); //特殊角色指定的用户   nodeid:xxxx,nodeid:yyyy
				if(specialRoleUserStr==null)
					specialRoleUserStr="";
				WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());//因为驳回任务（rejectTask()是在WF_Instance这个类里写的。所以要初始化ins这个对象）
				ins.setSpecialOperate(specialOperate);
				//抄送邮件
				String isSendMessage=(String)hm.get("isSendMessage");
				String user_h_s=(String)hm.get("user_h_s");
				if(user_h_s==null)
					user_h_s="";
				String email_staff_value=(String)hm.get("email_staff_value");

//				邮件抄送
				if(isSendMessage!=null&&!"0".equals(isSendMessage))
				{
					ins.setEmail_staff_value(email_staff_value);
					ins.setUser_h_s(user_h_s);
					ins.setIsSendMessage(isSendMessage);
				}
				else
					ins.setUser_h_s(user_h_s);
				
				HashMap otherParaMap=new HashMap();
				/* wangrd 屏蔽 在下面循环任务加 2015-06-24
//				普天代办
		//		if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
				{
					String pre_pendingID="";
					if(hm.get("pre_pendingID")!=null)
						pre_pendingID=(String)hm.get("pre_pendingID");
					otherParaMap.put("pre_pendingID",pre_pendingID);
				}
				ins.setOtherParaMap(otherParaMap);
				*/
				String url_s=(String)this.getFormHM().get("url_s");
				ins.setUrl_s(url_s);
				/**支持多任务审批*/
				
				
				for(int i=0;i<tasklist.size();i++)//循环每一个任务
				{
					RecordVo ins_vo=new RecordVo("t_wf_instance");
					ins_id=((RecordVo)tasklist.get(i)).getString("ins_id");
					taskid=((RecordVo)tasklist.get(i)).getString("task_id");
					
					//检查当前任务的活动状态是否是结束状态  
					this.frowset=dao.search("select count(*) from t_wf_task where ( task_state='5' or task_state='4' ) and task_id="+taskid);
					if(this.frowset.next())
					{
						if(this.frowset.getInt(1)>0)
							throw GeneralExceptionHandler.Handle(new Exception("当前单据已被处理,操作失败"));
					}
					//待办信息 wangrd 2015-06-24
					String pendingCode="HRMS-"+PubFunc.encrypt(taskid); 
					otherParaMap.put("pre_pendingID",pendingCode);				
					ins.setOtherParaMap(otherParaMap);
					
					//ins.setObjs_sql()的作用是
					ins.setObjs_sql(ins.getObjsSql(Integer.parseInt(ins_id),Integer.parseInt(taskid),3,tabid,this.userView,""));//作用是
					ins_vo.setInt("ins_id",Integer.parseInt(ins_id));
					String actorid="";
					String actor_type="";
					tablebo.setIns_id(Integer.parseInt(ins_id));
					 
					
					if(this.userView.getStatus()==0)//业务用户
					{
						actorid=this.userView.getUserName();
						actor_type="4";
					}
					else
					{
						actorid=this.userView.getDbname()+this.userView.getA0100();
						actor_type="1";
					}
					WF_Actor wf_actor=new WF_Actor(actorid,actor_type);//流程参与者的相关信息。对话框中的数据全部会存进去
					wf_actor.setBexchange(false);//不让替换成选择的报送对象
					wf_actor.setActorname(this.userView.getUserFullName());			
					wf_actor.setContent(SafeCode.decode((String)actor.get("content")));//.replace("\r\n", "<p>").replace(" ", "&nbsp;")); //批复内容
					wf_actor.setEmergency((String)actor.get("pri")); //优先级
					wf_actor.setSp_yj((String)actor.get("sp_yj"));//审批意见
					if(tasklist.size()==1 &&specialRoleUserStr.length()>0) //批量审批时 后面审批流程重新取特殊角色，不然都会默认使用第一条单据的。wangrd 20151203
						wf_actor.setSpecialRoleUserList(specialRoleUserStr); 
					/**驳回意见*/
					ins.setIns_id(Integer.parseInt(ins_id));
				 
					/*String opinionstr = " select * from templet_"+tabid+" ";
					opinionstr+=" where seqnum in  (select seqnum  from t_wf_task_objlink where   ";
					opinionstr+="    task_id="+taskid+"   and submitflag=1  and (state is null or  state=0 )  and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) )) ";
				 
					String opinion_field = tablebo.getOpinion_field();//审批意见指标
					for(int j=0;j<fieldlist.size();j++)
					{
						FieldItem fielditem=(FieldItem)fieldlist.get(j);
						String field_name=fielditem.getItemid();
						if(fielditem.isChangeAfter()&&opinion_field!=null&&opinion_field.length()>0&&opinion_field.equalsIgnoreCase(field_name))
						{
							String approveopinion ="";
							
							if(flag.equals("2")){
								approveopinion = tablebo.getApproveOpinion(ins_vo,taskid, wf_actor,"02");
							}else{
								approveopinion = tablebo.getApproveOpinion(ins_vo,taskid, wf_actor,"");
							}
						 
							tablebo.updateApproveOpinion( "templet_"+tabid,ins_id,opinion_field+"_2",approveopinion,opinionstr);
							break;
						}
							
					}*/
					if("2".equals(flag))//如果是驳回
					{
						wf_actor.setSp_yj("02");
						wf_actor.setBexchange(true);
						String reject_type = "1";//=1 or null：逐级驳回  =2：驳回到发起人
						RecordVo template_tableVo =TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.getFrameconn());
						TemplateTableBo ttb=new TemplateTableBo(this.getFrameconn(),template_tableVo,this.getUserView());
						reject_type = ttb.getReject_type();
						if("2".equalsIgnoreCase(reject_type)){//驳回到发起人
							ins.rejectTaskToSponsor(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView);//此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
						}else{//逐级驳回
							ins.rejectTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView);//此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
						}
					}
					else//如果不是驳回
					{
						ins.createNextTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView);//在这个函数里面执行了expDataIntoArchive()
		/////////////////提交时，将个人附件归档///////////////
						ins_vo=dao.findByPrimaryKey(ins_vo);
						if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){//下一个流程结点是结束结点
							isEnd = true;
							if("1".equals(flag) && tablebo.hasFunction()){//不是驳回，并且有归档的权限
								StringBuffer sb = new StringBuffer("");
								sb.append("select * from t_wf_file where ins_id="+ins_id+" and tabid="+tabid+" and attachmenttype=1");//个人附件
								if(!this.userView.isAdmin() && !"1".equals(this.userView.getGroupId()) && !tablebo.getIsIgnorePriv()){
									sb.append(" and filetype in (select id from mediasort where flag in "+tablebo.getMediaPriv()+")");
								}
								this.frowset = dao.search(sb.toString());
								SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
								String time = sdf.format(new Date());//得到当前日期
							    switch(Sql_switcher.searchDbServer())
							    {
									case Constant.ORACEL:
								    {
								    	time="to_date('"+time+"','yyyy-mm-dd')";
								    	break;
								    }
									case Constant.MSSQL:
								    {
								    	time="'"+time+"'";
								    	break;
								    }
								}
							    HashMap map = tablebo.getDestination_a0100();
								while(this.frowset.next()){
									sb.setLength(0);
									int flagid = this.frowset.getInt("filetype");//和mediasort表的id相关联
									int file_id = this.frowset.getInt("file_id");
									if(tablebo.getInfor_type()==1){
										String basepre = "";//要归档到的人员库
										String a0100 = "";//最终的人员编号
										if(tablebo.getOperationtype()==0||tablebo.getOperationtype()==1 || tablebo.getOperationtype()==2){
											basepre = tablebo.getDestBase();
											String sourcea0100 = a0100 = this.frowset.getString("objectid");
											a0100 = (String)tablebo.getDestination_a0100().get(sourcea0100);
										}else{
											basepre = this.frowset.getString("basepre");
											a0100 = this.frowset.getString("objectid");
										}
										if ((basepre!=null)&& (a0100!=null)&& (!"".equals(basepre))&&(!"".equals(a0100))){
										    sb.append("insert into "+basepre+"A00 (id,a0100,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
	                                        sb.append(" select null,'"+a0100+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from "+basepre+"a00 where a0100='"+a0100+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time
	                                                +" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);//liuyz 附件通过表单归档状态应是批准
	                                        dao.update(sb.toString());
										}
									}else if(tablebo.getInfor_type()==2){
										String b0110 = this.frowset.getString("objectid");
										//lis add 20160719
										if(map!=null){
										    String objectid = (String) map.get(b0110);
										    if(objectid!=null&&!objectid.equals(b0110)){
										        b0110 = objectid;
										    }
										}
										//lis end 20160719
										if (b0110!=null && (!"".equals(b0110))){
										    sb.append("insert into B00 (b0110,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
	                                        sb.append(" select '"+b0110+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where b0110='"+b0110+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id); //liuyz 附件通过表单归档状态应是批准 
	                                        dao.update(sb.toString());
										}
									
									}else if(tablebo.getInfor_type()==3){
										String e01a1 = this.frowset.getString("objectid");
										//lis add 20160719
										if(map!=null){
				                            String objectid = (String) map.get(e01a1);
				                            if(objectid!=null&&!objectid.equals(e01a1)){
				                                e01a1 = objectid;
				                            }
				                        }
										//lis end 20160719
										if (e01a1!=null && (!"".equals(e01a1))){
	                                        sb.append("insert into K00 (e01a1,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
	                                        sb.append(" select '"+e01a1+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where e01a1='"+e01a1+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);  //liuyz 附件通过表单归档状态应是批准
	                                        dao.update(sb.toString());
										}

									}
									
								} //while 遍历结束	
							} //flag=1  结束
							////////////////个人附件归档结束//////////////////
			                SendEmailToBeginUser(tablebo, ins_vo, dao, ins, tabid);
						}
						else {
		                    if (tablebo.getInfor_type()==1){//调用sap接口
		                        tablebo.sendDataToSAP(tabid,ins_vo.getString("ins_id"));
		                    }
						}
					}
					if(!isEnd){
						ins.updateApproveOpinion(ins_vo, wf_actor, this.userView, Integer.parseInt(taskid));
					}
				}
			} //自动流转 结束
			String returnflag=(String)this.getFormHM().get("returnflag");
			this.getFormHM().clear();
			this.getFormHM().put("returnflag", returnflag);
			if(!"yes".equals(msgs)&&msgs.trim().length()>0)
			{
					Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
					String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
					if(mode!=null&& "warn".equalsIgnoreCase(mode)){
						this.getFormHM().put("msgs", msgs);
					}
			}
			this.getFormHM().put("msgs","");
			
            if("1".equals(sp_batch)&& unDealedTaskIds.length()>0)
            {//批量审批 如果有未处理完的单据，则需要刷新当前页面。wangrd 2015-01-16
               unDealedTaskIds=PubFunc.encrypt(unDealedTaskIds);
               this.getFormHM().put("unDealedTaskIds",SafeCode.encode(unDealedTaskIds));
               this.getFormHM().put("beginRejectFlag",beginRejectFlag);
               if(beginRejectFlag){
            	    ArrayList taskidsList=new ArrayList();
            	    if(beginRejectTaskIds.indexOf(",")!=-1){
            	    	String[]taskids=beginRejectTaskIds.split(",");
            	    	for(int i=0;i<taskids.length;i++){
            	    		taskidsList.add(taskids[i]);
            	    	}
            	    }else{
            	    	taskidsList.add(beginRejectTaskIds);
            	    }
            	    String content="";
            	    for(int i=0;i<taskidsList.size();i++){
            	    	int rej_taskid=Integer.parseInt(taskidsList.get(i)+"");
            	    	RecordVo vo=new RecordVo("t_wf_task");
    					vo.setInt("task_id", rej_taskid);
    					vo=dao.findByPrimaryKey(vo);
    					String task_topic=vo.getString("task_topic");
    					int m=task_topic.indexOf("(");
                		int n=task_topic.indexOf(",共");
                		String name=task_topic.substring(m+1, n);
                		if("".equals(content)){
                			content=name;
                		}else{
                		    content+=","+name;
                		}
            	    }
            	    content=subText(content)+"的单据由你发起，不能驳回！";
            	    this.getFormHM().put("contentTip",content);
               }
            }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	private String subText(String text)
    {
        if (text == null || text.length() <= 0)
            return "";
        if (text.length() < 18)
            return text;
        text = text.substring(0, 18) + "...等人";
        return text;
    }
	
	/**
	 * 支持填写意见及同一节点任意流转
	 * @param hm
	 * @param tasklist
	 * @throws GeneralException
	 */
	private void generalApply(HashMap hm,ArrayList tasklist,ArrayList addlist,ArrayList movelist) throws GeneralException {
		
		try
		{
	    boolean isEnd = false;
		LazyDynaBean actor=(LazyDynaBean)hm.get("actor");
		String rejectObj=(String)hm.get("rejectObj");
		//String tabid=(String)actor.get("tabid");
		String tabid=(String)hm.get("tabid");
		String actorid=(String)actor.get("name");
		String actorname=(String)actor.get("fullname");			
		String ins_id="";//(String)hm.get("ins_id");
		String taskid="";//(String)(String)hm.get("taskid");		
		String actor_type=(String)actor.get("objecttype");
		String specialOperate=(String)hm.get("specialOperate"); //业务模板中人员需要报送给各自领导进行审批处理
		
		/**批量审批*/
		String sp_batch=(String)hm.get("sp_batch");
		if(sp_batch==null|| "".equals(sp_batch))
			sp_batch="0";//单个任务审批
		
		
		/**
		 * =1报批，
		 * =2驳回
		 * =3确认
		 * 根据上述三种状态，进行对应操作
		 */
		String flag=(String)actor.get("flag");
		TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
		WF_Instance ins=new WF_Instance(tablebo,this.getFrameconn());
		String url_s=(String)this.getFormHM().get("url_s");
		ins.setUrl_s(url_s);
		ins.setSpecialOperate(specialOperate);
		
		//抄送邮件
		String isSendMessage=(String)hm.get("isSendMessage");
		String user_h_s=(String)hm.get("user_h_s");//抄送的人  格式：   ,1:Usr00000049
		String email_staff_value=(String)hm.get("email_staff_value");//是否通知本人
//		邮件抄送
		
		if(isSendMessage!=null&&!"0".equals(isSendMessage))
		{
			ins.setEmail_staff_value(email_staff_value);
			ins.setUser_h_s(user_h_s);
			ins.setIsSendMessage(isSendMessage);
		}
		
		HashMap otherParaMap=new HashMap();
		/*屏蔽 下面已加上了。
		{
			String pre_pendingID="";
			if(hm.get("pre_pendingID")!=null)
				pre_pendingID=(String)hm.get("pre_pendingID");
			otherParaMap.put("pre_pendingID",pre_pendingID);
		}
		*/
		ins.setOtherParaMap(otherParaMap);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		for(int i=0;i<tasklist.size();i++)
		{
			ins_id=((RecordVo)tasklist.get(i)).getString("ins_id");
			taskid=((RecordVo)tasklist.get(i)).getString("task_id");
			//wangrd 2015-08-29
			String pendingCode="HRMS-"+PubFunc.encrypt(taskid); 					
			otherParaMap.put("pre_pendingID",pendingCode);				
			ins.setOtherParaMap(otherParaMap);
			this.frowset=dao.search("select count(*) from t_wf_task where ( task_state='5' or task_state='4' ) and task_id="+taskid);
			if(this.frowset.next())
			{
				if(this.frowset.getInt(1)>0)
					throw GeneralExceptionHandler.Handle(new Exception("当前单据已被处理,操作失败"));
			}
			
			if("1".equals(sp_batch))
			{
				String sqlstr ="select count(*) from templet_"+tabid ;
				sqlstr+=" where  seqnum in   (select  seqnum  from t_wf_task_objlink where  ";
				sqlstr+="   task_id="+taskid+"   and submitflag=1  and (state is null or  state=0 ) and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
				this.frowset=dao.search(sqlstr);
				if(this.frowset.next())
				{
					if(this.frowset.getInt(1)==0)
						continue;
				}
				
			}
			
			ins.setObjs_sql(ins.getObjsSql(Integer.parseInt(ins_id),Integer.parseInt(taskid),3,tabid,this.userView,""));
			RecordVo ins_vo=new RecordVo("t_wf_instance");
			ins_vo.setInt("ins_id",Integer.parseInt(ins_id));
			/**审批对象*/
			WF_Actor wf_actor=new WF_Actor(actorid,actor_type);
			wf_actor.setContent(SafeCode.decode((String)actor.get("content")));//.replace("\r\n", "<p>").replace(" ", "&nbsp;")); //批复内容
			wf_actor.setEmergency((String)actor.get("pri")); //优先级
			wf_actor.setSp_yj((String)actor.get("sp_yj"));//审批意见   01:同意
			wf_actor.setActorname(actorname);
			ins.setIns_id(Integer.parseInt(ins_id));
			tablebo.setIns_id(Integer.parseInt(ins_id));
			tablebo.getTasklist().add(taskid);
			ArrayList fieldlist=tablebo.getAllFieldItem();
			
			if("1".equals(flag))  //重新分配
			{
				if(ins.reAssignTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView))
				{
				    if (tablebo.getInfor_type()==1){//调用sap接口
                        tablebo.sendDataToSAP(tabid,ins_vo.getString("ins_id"));
                    };
				}
			}
			else if("2".equals(flag))//驳回重审,把任务指给上次发送过的人
			{
				wf_actor.setSp_yj("02");
				String reject_type = "1";//=1 or null：逐级驳回  =2：驳回到发起人
				RecordVo template_tableVo = TemplateUtilBo.readTemplate(Integer.parseInt(tabid),this.getFrameconn());
				TemplateTableBo ttb=new TemplateTableBo(this.getFrameconn(),template_tableVo,this.getUserView());
				reject_type = ttb.getReject_type();
				if("2".equalsIgnoreCase(reject_type)){//驳回到发起人
					ins.rejectTaskToSponsor(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView);//此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
				}else{//逐级驳回
					ins.rejectTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView);//此方法先结束掉当前任务，再创建一个新任务(更新t_wf_task和t_wf_task_objlink这两张表)
				}
			}
			else  //最后数据提交到档案库中去,批准
			{
				
				if(tablebo.getOperationtype()==0)
				{
					
					HashMap subhm=tablebo.readUpdatesSetField(fieldlist);
					if(subhm.get("A01")==null)
					{
						throw new GeneralException(ResourceFactory.getProperty("error.input.a01read"));
					}
				}
				
				
				/**必填项校验*/
				tablebo.setValidateM_L(true);
				isEnd=true;
				if(ins.finishTask(ins_vo,wf_actor,Integer.parseInt(taskid),this.userView,"5"))
				{
					if(ins.getTask_vo().getInt("task_id")!=0) ////往考勤申请单中写入记录
					{
						StringBuffer strsql=new StringBuffer("");
						strsql.append("select * from templet_"+tabid); 
						strsql.append(" where  seqnum  in  (select  seqnum   from t_wf_task_objlink where  ");
						strsql.append("   task_id="+ins.getTask_vo().getInt("task_id")+" and tab_id="+tabid+" and state=1 ) ");
						
						String operState="03";
						if(!"01".equals((String)actor.get("sp_yj")))
							operState="07"; 
						ins.insertKqApplyTable(strsql.toString(),tabid,"0",operState,"templet_"+tabid); //往考勤申请单中写入报批记录
						
					}
					boolean bhave=ins.isHaveObjTheTask(Integer.parseInt(taskid));
					tablebo.setSp_yj((String)actor.get("sp_yj"));
					if(tablebo.getInfor_type()==1){//如果是人员
		                ins.resetDbpre("templet_"+tabid,tablebo,taskid);
		            }
					ins.updateApproveOpinion(ins_vo, wf_actor, userView, Integer.parseInt(taskid));
					tablebo.expDataIntoArchive(ins.getTask_vo().getInt("task_id"));
					
	/////////////////提交时，将个人附件归档///////////////
					//if(flag.equals("1") && tablebo.hasFunction()){xcs 2014-05-20 flag=1代表继续报批而不是提交入库 
					if("3".equals(flag) && tablebo.hasFunction()){
						StringBuffer sb = new StringBuffer("");
						sb.append("select * from t_wf_file where ins_id="+ins_id+" and tabid="+tabid+" and attachmenttype=1");//个人附件
						if(!this.userView.isAdmin() && !"1".equals(this.userView.getGroupId()) && !tablebo.getIsIgnorePriv()){
							sb.append(" and filetype in (select id from mediasort where flag in "+tablebo.getMediaPriv()+")");
						}
						this.frowset = dao.search(sb.toString());
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
						String time = sdf.format(new Date());//得到当前日期
					    switch(Sql_switcher.searchDbServer())
					    {
							case Constant.ORACEL:
						    {
						    	time="to_date('"+time+"','yyyy-mm-dd')";
						    	break;
						    }
							case Constant.MSSQL:
						    {
						    	time="'"+time+"'";
						    	break;
						    }
						}
					    HashMap map = tablebo.getDestination_a0100();
						while(this.frowset.next()){
							sb.setLength(0);
							int flagid = this.frowset.getInt("filetype");//和mediasort表的id相关联
							int file_id = this.frowset.getInt("file_id");
							if(tablebo.getInfor_type()==1){
								String basepre = "";//要归档到的人员库
								String a0100 = "";//最终的人员编号
								if(tablebo.getOperationtype()==0 ||tablebo.getOperationtype()==1 || tablebo.getOperationtype()==2){
									basepre = tablebo.getDestBase();
									String sourcea0100 = a0100 = this.frowset.getString("objectid");
									a0100 = (String)tablebo.getDestination_a0100().get(sourcea0100);
								}else{
									basepre = this.frowset.getString("basepre");
									a0100 = this.frowset.getString("objectid");
								}
								sb.append("insert into "+basepre+"A00 (id,a0100,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
								sb.append(" select null,'"+a0100+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from "+basepre+"a00 where a0100='"+a0100+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
							}else if(tablebo.getInfor_type()==2){
								String b0110 = this.frowset.getString("objectid");
								//lis add 20160719
								if(map!=null){
								    String objectid = (String) map.get(b0110);
								    if(objectid!=null&&!objectid.equals(b0110)){
								        b0110 = objectid;
								    }
								}
								//lis end 20160719
								sb.append("insert into B00 (b0110,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
								sb.append(" select '"+b0110+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where b0110='"+b0110+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
							}else if(tablebo.getInfor_type()==3){
								String e01a1 = this.frowset.getString("objectid");
								//lis add 20160719
								if(map!=null){
		                            String objectid = (String) map.get(e01a1);
		                            if(objectid!=null&&!objectid.equals(e01a1)){
		                                e01a1 = objectid;
		                            }
		                        }
								//lis end 20160719
								sb.append("insert into K00 (e01a1,i9999,title,ole,flag,ext,createusername,modusername,state,createtime,modtime)");
								sb.append(" select '"+e01a1+"',(select "+Sql_switcher.isnull("max(i9999)","0")+"+1 from B00 where e01a1='"+e01a1+"') i9999,name,content,(select flag from mediasort where id="+flagid+") flag,"+"'.'"+Sql_switcher.concat()+"ext,create_user,create_user,3,(select "+time+" from t_wf_file where file_id="+file_id+") createtime,(select "+time+" from t_wf_file where file_id="+file_id+") modtime from t_wf_file where file_id="+file_id);
							}
							dao.update(sb.toString());
						} //while 遍历结束	
					} //flag=1 结束
					////////////////个人附件归档结束//////////////////
					
					try
					{
						StringBuffer buf=new StringBuffer();
						/**如果当前流程实例中存在正在运行中的任务，重新把实例置为运行状态*/
						if(ins.isHaveRuningTask(ins_vo.getInt("ins_id"))||bhave)
						{
							/**流程实例启动*/
							
							buf.append("update t_wf_instance set end_date=null,finished='2' where ins_id=");
							buf.append(ins_vo.getInt("ins_id"));
							dao.update(buf.toString());
							//xcs modify @ 2014-4-1
							buf.setLength(0);
	                        buf.append("update t_wf_task set flag=1");
	                        buf.append(" where task_id=");
	                        buf.append(ins.getTask_vo().getInt("task_id")/*taskid*/);
	                        dao.update(buf.toString());
						}else{
						    buf.setLength(0);
	                        buf.append("update t_wf_task set flag=1");
	                        buf.append(" where task_id=");
	                        buf.append(ins.getTask_vo().getInt("task_id")/*taskid*/);
	                        dao.update(buf.toString());
	                        SendEmailToBeginUser(tablebo, ins_vo, dao, ins, tabid);
						}
//						buf.setLength(0);
//						buf.append("update t_wf_task set flag=1");
//						buf.append(" where task_id=");
//						buf.append(ins.getTask_vo().getInt("task_id")/*taskid*/);
//						dao.update(buf.toString());
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						throw GeneralExceptionHandler.Handle(ex);
					}
					
					
					/*判断是否 调用中建接口
					 * sso_templetOwner=1:(2;lia;1~4;lsj;0)&51:(54:Usr00000004;1)
					 */
					SendMessageBo bo=new SendMessageBo(this.getFrameconn(),this.userView);
					bo.sendMessageToOa(tabid) ;
					
					
					//更新联动兼职数据
					DbNameBo db=new DbNameBo(this.getFrameconn());
					for( int j=0;j<addlist.size();j++){
						String pos_value = (String)addlist.get(j);
						if(pos_value!=null&&pos_value.trim().length()>0){
						db.dateLinkage(pos_value, 1, "+");
						}
					}
					for( int j=0;j<movelist.size();j++){
						String pos_value = (String)movelist.get(j);
						if(pos_value!=null&&pos_value.trim().length()>0){
						db.dateLinkage(pos_value, 1, "-");
						}
					}
				}
			}
			if(!isEnd){
				ins.updateApproveOpinion(ins_vo, wf_actor, userView, Integer.parseInt(taskid));
			}
			
		} //for i loop end.
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		
	}
	public void SendEmailToBeginUser(TemplateTableBo tablebo,RecordVo ins_vo,ContentDAO dao,WF_Instance ins,String tabid) throws GeneralException{
	    try{
	    	if (!"true".equalsIgnoreCase(tablebo.getNotice_initiator())){//发送邮件并且在单据提交入库时要通知到流程发起人
	    		return;
	    	}
	    	if (!(tablebo.isBemail() || tablebo.isBsms())){
	    		return;
	    	}
            
            ins_vo=dao.findByPrimaryKey(ins_vo);
            if(ins_vo==null){
                throw new GeneralException(ResourceFactory.getProperty("error.wf.notins_id"));
            }

            int actorType=ins_vo.getInt("actor_type");
            String tempactorid=ins_vo.getString("actorid");
            String Titlename="";
            String context="";
            String a0100="";
            String nbase="";
            String sql="";
            if(actorType==4){//说明是业务用户
                sql="select * from operuser where userName='"+tempactorid+"'";
                this.frowset=dao.search(sql);
                if(this.frowset.next()){
                    a0100=this.frowset.getString("a0100");
                    nbase=this.frowset.getString("nbase");
                }
            }else{//自助用户
                if(tempactorid.length()>3){
                    nbase=tempactorid.substring(0,3);
                    a0100=tempactorid.substring(3);
                }
            }
            
            ArrayList attachList = null;
            Titlename=tablebo.getTable_vo().getString("name");//得到模版的名称 也就是邮件头
            context=Titlename;
            String template_initiator="";
            SendMessageBo sendBo=null;
            template_initiator=(String)tablebo.getTemplate_initiator();
            sendBo=new SendMessageBo(this.getFrameconn(),tablebo.getUserview());
            if(template_initiator!=null&&template_initiator.trim().length()>0) //获得审批模板
            {
                if(ins.getTask_vo().getInt("task_id")!=0)
                    sendBo.setTask_id(String.valueOf(ins.getTask_vo().getInt("task_id")));
                sendBo.setIns_id(String.valueOf(ins_vo.getInt("ins_id")));
                sendBo.setSp_flag("1");
                LazyDynaBean mailInfo=sendBo.getTemplateMailInfo(template_initiator);
                context=(String)mailInfo.get("content");
                
                //zxj 20141023 邮件模板附件
                attachList = (ArrayList)mailInfo.get("attach"); 
            }
            
            String _context = context.replaceAll("\r\n","<br>");
            LazyDynaBean _bean = null;
            //这地方好像颠倒了 先改过来 wangrd 2015-05-27 
            if(template_initiator==null||template_initiator.trim().length()==0){
            	_bean = sendBo.getTileAndContent("3", Titlename, _context, tempactorid, tablebo.getUserview(), tabid, ins.getObjs_sql(), null,tablebo.getInfor_type());//单据结束通知发起人,opt传3,让信息中的提示得到的是已被批准
            }else{
            	_bean = sendBo.getEmailBean("2",null,Titlename,_context,tempactorid,tablebo.getUserview(),tabid,ins.getObjs_sql());
            }
            String objectId = nbase+a0100;
            _bean.set("objectId", objectId);
            if(attachList!=null&&attachList.size()>0){
                _bean.set("attachList", attachList);
            }
            
            if (tablebo.isBemail() ){ //发送邮件
            	AsyncEmailBo newEmailBo = null;
            	try{
            		newEmailBo = new AsyncEmailBo(this.frameconn, this.userView);
            	}
            	catch(Exception e){
            		throw new GeneralException(ResourceFactory.getProperty("邮箱服务器配置错误，通知发起人失败"));
            	} 
            	newEmailBo.send(_bean);
            	//发送微信信息
            	new TemplateUtilBo(this.frameconn,this.userView).sendWeixinMessageFromEmail(_bean);
            }
            
            if (tablebo.isBsms() ){ //发送短信
				SmsBo smsbo=new SmsBo(this.frameconn);
				smsbo.sendMessage(this.userView,objectId,_context);
            }
	    }catch(Exception e){
	        e.printStackTrace();
	        throw GeneralExceptionHandler.Handle(e);
	    }
    
	}
	
 
	
	   /**
     * 从系统邮件服务器设置中得到发送邮件的地址
     * @return
     */
    public String getFromAddr() throws GeneralException 
    {
        String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null)
            return "";
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param))
            return "";
        try
        {
            Document doc = PubFunc.generateDom(param);;
            Element root = doc.getRootElement();
            Element stmp=root.getChild("stmp"); 
            str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
    }
}
