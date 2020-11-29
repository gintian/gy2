package com.hjsj.hrms.module.template.templatenavigation.transaction;

import com.hjsj.hrms.businessobject.general.template.tasklist.TaskListRoleActorInfoBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchRolePeopleTrans.java</p>
 * <p>Description>:查看具有某种角色的人员</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-5-20 上午11:53:17</p>
 * <p>@author:hej</p>
 * <p>@version: 1.0</p>
 */
public class SearchRoleActorTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try
        {
            String tabId=(String)this.getFormHM().get("tabid");
            String taskid=(String)this.getFormHM().get("taskid");
            String flag=(String)this.getFormHM().get("flag");
            String ins_id=(String)this.getFormHM().get("ins_id");
            taskid= PubFunc.decrypt(taskid);
            TaskListRoleActorInfoBo roleActorInfoBo= new TaskListRoleActorInfoBo(this.frameconn,this.userView);
            ContentDAO dao=new ContentDAO(this.frameconn);
            if("1".equals(flag)){//已办任务
            	String sql= "select task_id from t_wf_task where ins_id='"+ins_id+"' and task_state<>'5' ORDER BY task_id desc";
            	this.frowset=dao.search(sql);
            	while(this.frowset.next()){
            		taskid = this.frowset.getString("task_id");
            		break;
            	}
            }
            HashMap map =roleActorInfoBo.getRoleActorHtml(tabId,taskid);
            this.getFormHM().put("approvePeople", SafeCode.encode((String)map.get("approvePeople")));
            this.getFormHM().put("approveContent", SafeCode.encode((String)map.get("approveContent")));
       
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }

    }
    
  
}