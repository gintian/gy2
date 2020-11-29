
package com.hjsj.hrms.module.template.templatetoolbar.apply.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.NodeType;
import com.hjsj.hrms.businessobject.general.template.workflow.SendMessageBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.module.template.templatetoolbar.apply.businessobject.TemplateApplyBo;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateInterceptorAdapter;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.service.business.TemplateServiceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:TemplateApplyTrans.java</p>
 * <p>Description>:报批、审批</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-4-18 上午09:42:41</p>
 * <p>@author:wangrd</p>
 * <p>@version: 7.0</p>
 */
public class TemplateApplyTrans extends IBusiness {
    String unDealedTaskIds="";//这次审批未选中的单据。
    @Override
    public void execute() throws GeneralException {
        HashMap hm = this.getFormHM();
        WF_Actor wf_actor = null;
        TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
        String sysType = frontProperty.getSysType();
        String moduleId = frontProperty.getModuleId();
        String returnFlag = frontProperty.getReturnFlag();
        String tabId = frontProperty.getTabId();
        String taskId = frontProperty.getTaskId();
        String infor_type = frontProperty.getInforType();
        String flag = (String) hm.get("flag");
        String def_flow_self = (String)hm.get("def_flow_self");
        String selfapply = "0";
        if (frontProperty.isSelfApply()){
            selfapply="1";
        }
        boolean bSelfApply = frontProperty.isSelfApply();        
        boolean bBatchApprove = frontProperty.isBatchApprove();
        
        TemplateBo templateBo = new TemplateBo(this.getFrameconn(), this.userView, Integer.parseInt(tabId));
        TemplateParam paramBo = templateBo.getParamBo();
        /** 审批模式=0自动流转，=1手工指派 */
        int sp_mode =paramBo.getSp_mode();
        // 3:邮件、消息 // //  // 2：消息// // 1：邮件
        String isSendMessage="0";
        if(paramBo.isBemail()&&paramBo.isBsms())
            isSendMessage="3";
        else if(paramBo.isBemail())
            isSendMessage="1";
        else if(paramBo.isBsms())
            isSendMessage="2";
        if(!this.userView.hasTheFunction("2701515")&&!this.userView.hasTheFunction("0C34815")&&!this.userView.hasTheFunction("400040115")
                &&!this.userView.hasTheFunction("32015")&&!this.userView.hasTheFunction("325010115")&&!this.userView.hasTheFunction("324010115")&&!this.userView.hasTheFunction("010701")&&!this.userView.hasTheFunction("32115")&&!this.userView.hasTheFunction("3800715"))
            isSendMessage="0";
        
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        TemplateTableBo tablebo = new TemplateTableBo(this.getFrameconn(), 
                Integer.parseInt(tabId), this.userView);
        tablebo.setBEmploy(bSelfApply);
        tablebo.setValidateM_L(true);//已校验过
        tablebo.setPcOrMobile("0");
        
        try {
            String reportObjectId = (String) hm.get("reportObjectId");// 抄送对象 格式： // ,1:Usr00000049
            if (reportObjectId == null) reportObjectId = "";
            
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
                            if (reportObjectId.length()>0)
                                reportObjectId=reportObjectId+","+strUser;
                            else     
                                reportObjectId=strUser; 
                        }
                    }
                }
            }
            
            boolean emailSelf = paramBo.isEmail_staff();// 是否通知本人 
            String specialOperate = (String) hm.get("specialOperate"); // 业务模板中人员需要报送给各自领导进行审批处理
            String specialRoleUserStr = (String) hm.get("specialRoleUserStr"); // 特殊角色指定的用户
            if (specialRoleUserStr == null)  specialRoleUserStr = "";
            String pri = (String)hm.get("pri");
            String actorType = (String)hm.get("actorType");
            String actorId = (String)hm.get("actorId");
            if (actorId!=null && actorId.length()>0/* && !"2".equals(actorType)*/)//角色已经加密
            {
            	if("3".equals(actorType)) //组织机构
            	{
            		String codesetid=actorId.substring(0,2);
            		String temp_str=actorId.substring(2);
            		actorId=codesetid+ PubFunc.decrypt(temp_str);
            	}
            	else
            		actorId = PubFunc.decrypt(actorId);
            
            }
            
            String actorName = (String)hm.get("actorName");
           // if (true) return;
            String sp_yj="01";            
            String content= (String)hm.get("content");
            if (content!=null){
                content=SafeCode.decode(content.replace("\r\n", "<p>").replace(" ", "&nbsp;"));
            }
            else {
                content="";
            }
            if (!"0".equals(taskId) && "".equals(content)){//非首次报批，审批意见为空则默认同意
            	
                content = ResourceFactory.getProperty("label.agree");
                if ("2".equals(flag))// 如果是驳回  
                	content=ResourceFactory.getProperty("label.nagree"); //不同意
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
            wf_actor = new WF_Actor(actorId, actorType);
            wf_actor.setContent(content);
            wf_actor.setEmergency(pri);
            wf_actor.setSp_yj(sp_yj);
            wf_actor.setActorname(actorName);
            if (sp_mode==0) {
                wf_actor.setBexchange(false);
            }
            
            if (specialRoleUserStr.length() > 0)// 特殊角色
                wf_actor.setSpecialRoleUserList(specialRoleUserStr);
            /** 员工自助申请 */
            ArrayList fieldlist = templateBo.getAllFieldItem();//不对 todo
            
            TemplateApplyBo  applyBo = new TemplateApplyBo(this.frameconn,this.userView,paramBo,frontProperty);
            String srcTab =templateBo.getTableName(moduleId,Integer.parseInt(tabId), taskId);     
            String url_s = this.userView.getServerurl();
            if ("0".equals(taskId)) {// 报批
                ArrayList whlList = new ArrayList();
                if (!frontProperty.isSelfApply()) { // 非自助申请，判断是否拆单
                  /*  if (SystemConfig.getPropertyValue("clientName") != null && SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("gdzy")) {
                        SynOaService sos = new SynOaService();
                        String tab_ids = sos.getTabids();
                        if (tab_ids.indexOf("," + tabId + ",") != -1) {
                            whlList = sos.getSplitInstanceWhl(tabId, this.userView, this.getFrameconn());
                        }
                    } else */
                	
                    {
                        whlList = tablebo.getSplitInstanceWhl();
                        if (whlList.size() == 1 && ((String) whlList.get(0)).trim().length() == 0)// 不拆单
                        {
                                ;
                        } else{
                        	if(!(wf_actor.getSpecialRoleUserList()!=null&&wf_actor.getSpecialRoleUserList().size()>0))
                        		wf_actor.setSpecialRoleUserList(new ArrayList());
                        }
                    }
                }
                else {
                    whlList.add(""); 
                }
                ArrayList personlist =applyBo.getPersonlist(paramBo.getInfor_type(),srcTab);
                for (int i = 0; i < whlList.size(); i++) {
                    RecordVo ins_vo = new RecordVo("t_wf_instance");
                    WF_Instance ins = new WF_Instance(tablebo, this.getFrameconn());
                    ins.setbSelfApply(bSelfApply);
                    ins.setUrl_s(url_s);
                    ins.setSpecialOperate(specialOperate);
                    ins.setModuleId(moduleId);
                    String whl = (String) whlList.get(i);
                    if ("1".equalsIgnoreCase(selfapply))
                        ins.setObjs_sql(ins.getObjsSql(0, 0, 1, tabId, this.userView, ""));
                    else
                        ins.setObjs_sql(ins.getObjsSql(0, 0, 2, tabId, this.userView, whl));
                    // 邮件抄送
                    if (isSendMessage != null && !"0".equals(isSendMessage)) {//是否抄送本人
                        ins.setIsSendMessage(isSendMessage);
                        ins.setEmail_staff_value(emailSelf?"1":"0"); // 通知本人
                    } 
                    ins.setUser_h_s(reportObjectId); // 抄送人员
                    // 将数据插入到t_wf_instance、t_wf_task_objlink和t_wf_task中
                    if (ins.createInstance(ins_vo, wf_actor, whl)){
                        //将数据提交到审批表
                        applyBo.saveSubmitTemplateData(tablebo,ins_vo,wf_actor,whl,selfapply);     
                        int ins_id = ins_vo.getInt("ins_id");
                        //通过whl得到对应的人员
                        ArrayList personlist_ = applyBo.getPersonlist(paramBo.getInfor_type(),ins_id,tabId,whl);
                        TempletChgLogBo chglogBo=new TempletChgLogBo(frameconn, userView,paramBo);
                        chglogBo.updateChangeInfoAddIns_id(personlist_, taskId, tabId, String.valueOf(ins_id),paramBo.getInfor_type());//提交时把变动日志更新到年度表中
                        // 把附件增加到流程中 应该放在saveSubmitTemplateData里面放在一起 todo 
                        applyBo.transAttachmentFile(srcTab,String.valueOf(ins_id),personlist_);  
                        //发送信息给OA
                        /*  dengcan 20161122
                        if (ins.getTask_vo()!=null){
                            applyBo.sendOA(String.valueOf(ins_id), String.valueOf(ins.getTask_vo().getInt("task_id")), selfapply);
                        }*/
 
                        //单据发起时将内容发送至其它OA系统
                        if (ins.getTask_vo()!=null&&!ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){
                        	TemplateServiceBo templateServiceBo=new TemplateServiceBo(this.frameconn);
                        	String isSuccess=templateServiceBo.sendTemplateDataToOths(this.userView,tabId,String.valueOf(ins.getTask_vo().getInt("task_id")));
                        	if("false".equals(isSuccess))//如果没有同步成功需还原数据
                        	{
                        		applyBo.restoreTemplateData(this.userView,ins,ins_id,tabId,bSelfApply);
                        		throw GeneralExceptionHandler.Handle(new Exception("同步至第三方系统报错，请重新执行报批操作!"));
                        	}
                        	else if(!"false".equals(isSuccess)&&!"true".equals(isSuccess)){//如果是url
                        		this.formHM.put("othersUrl",isSuccess);
                        	}
                        }
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
                this.getFormHM().put("@eventlog","执行报批操作");
            } else {// 审批
            	//审批之前判断是不是同一个单据的  如果是true 需要复制流程
            	applyBo.setApplyFlag(flag);
            	String taskId_copy=taskId;
            	taskId = applyBo.validateIsFromOne(taskId);
                ArrayList tasklist = getTaskList(flag,templateBo,tabId,taskId,taskId_copy);
                WF_Instance ins = new WF_Instance(tablebo, this.getFrameconn());// 因为驳回任务（rejectTask()是在WF_Instance这个类里写的。所以要初始化ins这个对象）
                ins.setSpecialOperate(specialOperate);
                // 邮件抄送
                if (isSendMessage != null && !"0".equals(isSendMessage)) {
                    ins.setEmail_staff_value(emailSelf?"1":"0");
                    ins.setUser_h_s(reportObjectId);
                    ins.setIsSendMessage(isSendMessage);
                } else
                    ins.setUser_h_s(reportObjectId);

                HashMap otherParaMap = new HashMap();
                ins.setUrl_s(url_s);
                /** 支持多任务审批 */

                for (int i = 0; i < tasklist.size(); i++)// 循环每一个任务
                {
                    boolean isEnd = false;
                    RecordVo ins_vo = new RecordVo("t_wf_instance");
                    String ins_id = ((RecordVo) tasklist.get(i)).getString("ins_id");
                    String taskid = ((RecordVo) tasklist.get(i)).getString("task_id");
                    // 检查当前任务的活动状态是否是结束状态
                    this.frowset = dao.search("select count(*) from t_wf_task where ( task_state='5' or task_state='4' ) and task_id=" + taskid);
                    if (this.frowset.next()) {
                        if (this.frowset.getInt(1) > 0)
                            throw GeneralExceptionHandler.Handle(new Exception("当前单据已被处理,操作失败"));
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
                            this.getFormHM().put("@eventlog","执行驳回操作");
                            ins.updateApproveOpinion(ins_vo, wf_actor, this.userView, Integer.parseInt(taskid));
                            TemplateInterceptorAdapter.afterHandle(Integer.parseInt(taskid),0,tablebo.getTabid(),null,"reject",this.userView);
                        } else{// 如果不是驳回
                            
                        	this.getFormHM().put("@eventlog","执行报批操作");
                        	 /**填充起始节点标识，此申请是否为当前用户*/ 
        			        String isStartNode=templateBo.isStartNode(taskid);
        			        if ("1".equals(isStartNode) && !"0".equals(taskid)) //发起人再报批，也需同步到第三方系统
        			        {
        			        	TemplateServiceBo templateServiceBo=new TemplateServiceBo(this.frameconn);
                            	String isSuccess=templateServiceBo.sendTemplateDataToOths(this.userView,tabId,taskid);
                            	if("false".equals(isSuccess))//如果没有同步成功需还原数据
                            	{
                            		throw GeneralExceptionHandler.Handle(new Exception("同步至第三方系统报错，请重新执行报批操作!"));
                            	}
                            	else if(!"false".equals(isSuccess)&&!"true".equals(isSuccess)){//如果是url
                            		this.formHM.put("othersUrl",isSuccess);
                            	}
        			        }
                        	
                        	
                        	ins.createNextTask(ins_vo, wf_actor, Integer.parseInt(taskid), this.userView);// 在这个函数里面执行了expDataIntoArchive()
                            ins_vo =dao.findByPrimaryKey(ins_vo);//重新获取vo lis 21060825
                            
                            
                            
                            // 提交时，将个人附件归档 
                            if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){//下一个流程结点是结束结点
                            	TemplateInterceptorAdapter.afterHandle(0,ins_vo.getInt("ins_id"),tablebo.getTabid(),paramBo,"submit",this.userView);
                            	this.getFormHM().put("@eventlog","提交到档案库");
                            	applyBo.submitAttachmentFile(ins_vo.getString("ins_id"), tablebo, "1", tabId);
                                applyBo.SendEmailToBeginUser(tablebo, ins_vo, dao, ins, tabId);
								TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
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
									TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
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
                        if("2".equals(def_flow_self)&&isTheEndSp&&"1".equals(flag))
                        	flag="3";
                        if ("1".equals(flag)){ // 重新分配
                            if (ins.reAssignTask(ins_vo, wf_actor, Integer.parseInt(taskid), this.userView)) {
                            	this.getFormHM().put("@eventlog","执行重新分派操作");
                                ;
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
                            this.getFormHM().put("@eventlog","执行驳回操作");
                        } else {// 批准，最后数据提交到档案库中去
                        	isEnd = true;
                            if (tablebo.getOperationtype() == 0) {
                            	
                            }
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
                                    if (!("01").equals(wf_actor.getSp_yj()))
                                        operState = "07";
                                     
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
                                TempletChgLogBo chgLogBo=new TempletChgLogBo(this.frameconn,this.userView,paramBo);
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

                                /*
                                 * 判断是否 调用中建接口
                                 * sso_templetOwner=1:(2;lia;1~4;lsj;0)&51:(54:Usr00000004;1)
                                 */
                                SendMessageBo bo = new SendMessageBo(this.getFrameconn(), this.userView);
                                bo.sendMessageToOa(tabId);
                                this.getFormHM().put("@eventlog","提交到档案库");

                            }
                        }
                        
                        //bug32669 填写审批意见移到创建流程之后 liuyz
                        if(!isEnd)
                        	ins.updateApproveOpinion(ins_vo, wf_actor, this.userView, Integer.parseInt(taskid));
                    }
                  
                }

            }

            if ("0".equals(taskId)) {
                // 判断临时表里是否有记录，没有记录更新临时表的结构（解决模板变动时保留的多余字段sqlserver可能造成8060的问题）
                if (Sql_switcher.searchDbServer() == 1) {// oracle 不判断8086问题 2015-07-25
                    this.frowset = dao.search(" select * from " + srcTab + "");
                    if (!this.frowset.next()) {
                        dao.update(" drop table " + srcTab + "");
                        // 创建表结构
                        if ("1".equalsIgnoreCase(selfapply)) {
                            templateBo.createTempTemplateTable("");
                        } else {
                            templateBo.createTempTemplateTable(this.userView.getUserName());
                        }
                    }
                }
            }
            else{
                //未处理的单据。
                this.getFormHM().put("unDealedTaskIds", unDealedTaskIds);
            }
            
            this.getFormHM().put("info", "");
            

        } catch (Exception ex) {
            ex.printStackTrace();
            String message = ex.toString();
            if (message.indexOf("最大") != -1 && message.indexOf("8060") != -1 && Sql_switcher.searchDbServer() == 1) {
                PubFunc.resolve8060(this.getFrameconn(), "templet_" + tabId);
                throw GeneralExceptionHandler.Handle(new Exception("请重新执行报批操作!"));
            } else
                throw GeneralExceptionHandler.Handle(ex);
        }
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
            if (i != 0)
                strsql.append(",");
            strsql.append(lists[i]);
        }
        strsql.append(")");
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            RowSet rset = dao.search(strsql.toString());
            while (rset.next()) {
                RecordVo taskvo = new RecordVo("t_wf_task");
                taskvo.setInt("task_id", rset.getInt("task_id"));
                taskvo.setInt("ins_id", rset.getInt("ins_id"));
                tmpTasklist.add(taskvo);
            }
           /***
            * 55110  首页待办人事异动审批选择部分数据提交后 会跳转到主页
            * 拆单情况下选中的task_id  applyBo.validateIsFromOne 会返回处理后的taskid
            * 判断 task_Id_old 与处理后的taskid是否有不同，不同则是未处理的，记录到unDealedTaskIds；
            * 不拆单情况下 task_id与task_Id_old相同 则需要遍历单据，检查单据中是否有未选中的数据，过滤未选中的单据
            */
            String[] taskId_oldArry=task_Id_old.split(",");
            for (int i = 0; i < taskId_oldArry.length; i++) {
            	if(!task_id.contains(taskId_oldArry[i])) {
            		unDealedTaskIds += ","+PubFunc.encrypt(taskId_oldArry[i]);
            	}
			}
          
            // 过滤单据：去除未选中的单据
            for (int i = 0; i < tmpTasklist.size(); i++) {
                RecordVo taskvo = (RecordVo) tmpTasklist.get(i);
                String taskid = taskvo.getString("task_id");
                if (isSelectedTaskId(dao, tabId, taskid)) {
                    String ins_id_reject = taskvo.getString("ins_id");
                    // 判断当前任务节点是不是初始节点
                    // =1为初始节点，=0不是初始节点，对于批量审批而已，有一个不是初始节点的单据，那么startflag=1
                     if("2".equals(flag)) //把单据是发起节点且要驳回的单据记录下来。
                     {
                        String startflag = templateBo.isStartNode(taskid);
                        if ("1".equals(startflag)){
                            /* 应该放到前面preparetrans检测。todo
                            if("".equals(beginRejectTaskIds)){
                                beginRejectTaskIds=taskid;
                            }else{
                                beginRejectTaskIds=beginRejectTaskIds+","+taskid;
                            }
                            beginRejectFlag=true;
                            */
                            unDealedTaskIds=unDealedTaskIds+","+PubFunc.encrypt(taskid);
                            continue;
                        }
                     }
                     tasklist.add(taskvo);
                } else {// 记录下来，后续页面刷新时会展现未处理的单据。
                     unDealedTaskIds=unDealedTaskIds+","+PubFunc.encrypt(taskid); ;
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
        try {
            String sqlstr = "select count(*) from templet_" + tabId + " where  seqnum in  (select seqnum  from t_wf_task_objlink where  task_id=" + task_id + "   and submitflag=1  and (state is null or  state=0 ) and (" + Sql_switcher.isnull("special_node", "0") + "=0  or ( " + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
            this.frowset = dao.search(sqlstr);
            if (this.frowset.next()) {
                if (this.frowset.getInt(1) == 0)
                    b = false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return b;
    }

  

}
