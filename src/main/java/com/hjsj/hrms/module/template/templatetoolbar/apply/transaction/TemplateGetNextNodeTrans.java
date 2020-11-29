package com.hjsj.hrms.module.template.templatetoolbar.apply.transaction;

import com.hjsj.hrms.businessobject.general.template.workflow.WorkflowBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Title:TemplateGetNextNodeTrans.java</p>
 * <p>Description>:判断是否是特殊角色且角色成员大于一个，只能选择一个成员</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-3-25 下午03:50:51</p>
 * <p>@author:wangrd</p>
 * <p>@version: 7.0</p>
 */
public class TemplateGetNextNodeTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
            String sysType = frontProperty.getSysType();
            String moduleId = frontProperty.getModuleId();
            String returnFlag = frontProperty.getReturnFlag();
            String tabId = frontProperty.getTabId();
            String taskId = frontProperty.getTaskId();
            boolean bBatchApprove = frontProperty.isBatchApprove();
            boolean bSelfApply = frontProperty.isSelfApply();
            String applyFlag = TemplateFuncBo.getValueFromMap(this.getFormHM(),"applyFlag");
            String selectSpecial="false";
            String specialRoleUserStr="";
            
            HashMap specialRoleMap = new HashMap();
            if (!"2".equals(applyFlag)){//非驳回
                TemplateParam paramBo = new TemplateParam(this.getFrameconn(), this.userView, Integer.parseInt(tabId));
                int sp_mode = paramBo.getSp_mode();
                boolean bspecial=false;
                String roleId="";
                int roleProperty = 0;
                String selfapply = "0";
                if (frontProperty.isSelfApply()) {
                    selfapply = "1";
                }
              
                //获取特殊角色成员
                if (sp_mode==0){ // 自动流转
                    WorkflowBo wfBo = new WorkflowBo(this.getFrameconn(), Integer.parseInt(tabId), this.userView);
                    String ins_id = "0";
                    String task_id = "0";
                    String[] temps = taskId.split(",");
                    
                    for (int i = 0; i < temps.length; i++) {
                        if (temps[i] != null && temps[i].trim().length() > 0)
                        {
                        	ArrayList valueList=new ArrayList();
                        	valueList.add(new Integer(temps[i]));
                        	valueList.add(new Integer(tabId));
                        	this.frowset=dao.search("select ins_id  from t_wf_task_objlink where task_id=? and submitflag=1 and tab_id=? and state<>3",valueList); //20160630 邓灿 只考虑选择的记录
                        	if(this.frowset.next())
                        	{
                        		task_id = temps[i];
                        		ins_id=this.frowset.getString("ins_id");
                        		break;
                        	}
                        }
                    } 
                    wfBo.setTask_ids(taskId);
                    String nextNodeStr = wfBo.getNextNodeStr(Integer.parseInt(task_id), Integer.parseInt(ins_id), selfapply);
                    if (nextNodeStr.length()>0){//是特殊角色
                        if (nextNodeStr.startsWith("$$")){//一个，直接取
                            specialRoleUserStr=nextNodeStr.substring(2);
                        }
                        else {//普通角色或多个
                            String [] arr0 = nextNodeStr.split("`");
                            for (int i=0;i<arr0.length;i++){
                                String str = arr0[i];
                                String [] arr1 = str.split(":");
                                if ("1".equals(arr1[1])){//特殊角色
                                    specialRoleUserStr=specialRoleUserStr+"`"+arr1[0];
                                }
                            }
                            if (specialRoleUserStr.length()>1){
                                //specialRoleUserStr=specialRoleUserStr.substring(1);
                                specialRoleMap = wfBo.getSpecialRoleMap(specialRoleUserStr, Integer.parseInt(ins_id), Integer.parseInt(task_id), selfapply);
                            }
                        }  
                    }                
                } else if (sp_mode==1){ // 手工流转
                    String actortype = (String) this.getFormHM().get("actorType"); // 对象类型
                    if ("2".equals(actortype)){// 如果是角色
                        roleId = (String) this.getFormHM().get("actorId");
                        roleId = PubFunc.decrypt(roleId);
                        this.frowset = dao.search("select * from t_sys_role where role_id='" + roleId + "'");
                        if (this.frowset.next()) {
                            roleProperty = this.frowset.getInt("role_property");
                            if (roleProperty == 9 || roleProperty == 10 || roleProperty == 11 || roleProperty == 12 || roleProperty == 13) {
                                if (paramBo.getRelation_id() == null || paramBo.getRelation_id().length() == 0)
                                    throw new GeneralException("该业务流程没有定义审批关系!");

                                if ("gwgx".equalsIgnoreCase(paramBo.getRelation_id())){ // 标准岗位关系
                                    if (roleProperty == 13) {
                                        throw new GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info7") + "!");
                                    }
                                } 
                                bspecial=true;
                            }
                        }
                    }
                    if (bspecial){
                        WorkflowBo wfBo = new WorkflowBo(this.getFrameconn(), Integer.parseInt(tabId), this.userView);
                        specialRoleMap = wfBo.getSpecialRoleMap(roleId, roleProperty + "");
                    }
                }
                
                //判断特殊角色成员是否大于1个。如果大于 前台需要弹出界面选择1个角色成员                
                Set keySet=specialRoleMap.keySet();
                for(Iterator t=keySet.iterator();t.hasNext();)
                {
                    String key=(String)t.next();
                    ArrayList list=(ArrayList)specialRoleMap.get(key);
                    if (list.size()>1){//多个需要前台提供界面，选择审批节点。
                        selectSpecial="true";
                        break;
                    }
                    else {
                        ;
                    }
                }   
            }
           
            this.getFormHM().put("selectSpecial",selectSpecial);
            this.getFormHM().put("specialRoleUserStr",specialRoleUserStr);
            if ("true".equals(selectSpecial)){
                Set keySet=specialRoleMap.keySet();
                ArrayList specialUserList = new ArrayList();
                ArrayList specialNodeList = new ArrayList();
                for(Iterator t=keySet.iterator();t.hasNext();){                
                    String key=(String)t.next();
                    String[] temps=key.split("`");
                    String node_name= temps[0];                    
                    ArrayList list=(ArrayList)specialRoleMap.get(key);
                    String node_type="";
                    String node_id="";
                    if (list.size()>1){
                        for (int i=0;i<list.size();i++){
                            LazyDynaBean abean=(LazyDynaBean)list.get(i);
                            node_type=(String)abean.get("actor_type"); //1 自助用户  4：业务用户
                            node_id=(String)abean.get("node_id");
                            String displayName="";
                            String userId="";
                            String a0101=(String)abean.get("a0101");
                            userId=(String)abean.get("mainbodyid");
                            if ("1".equals(node_type)){
                                String b0110=(String)abean.get("b0110");
                                String e0122=(String)abean.get("e0122");
                                String e01a1=(String)abean.get("e01a1");
                                displayName=b0110+"/"+e0122+"/"+e01a1+"/"+a0101;   
                                
                            }
                            else {
                                displayName=(String)abean.get("groupname")+"/"+userId;
                                if (!userId.equals(a0101) && a0101!=null && a0101.length()>0){
                                    displayName=displayName+"("+a0101+")";
                                }
                            }
                            LazyDynaBean userBean= new LazyDynaBean(); 
                            userBean.set("node_id", node_id);
                            userBean.set("node_type", node_type);
                            userBean.set("displayName", displayName);
                            userBean.set("userId", userId);
                            userBean.set("a0101", a0101);
                            specialUserList.add(userBean);
                        }
                        LazyDynaBean nodeBean= new LazyDynaBean();
                        nodeBean.set("node_id", node_id);
                        nodeBean.set("node_type", node_type);
                        nodeBean.set("node_name", node_name);
                        specialNodeList.add(nodeBean);
                    }
                    
                }
                this.getFormHM().put("specialUserList",specialUserList);
                this.getFormHM().put("specialNodeList",specialNodeList);
                
            }

        } catch (Exception e) {
        	e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    /**
     * 自动校验
     * 
     * @param task_id
     * @param ins_id
     * @param sp_batch
     * @param batch_task
     * @param selfapply
     * @param tablebo
     * @throws GeneralException
     */

}
