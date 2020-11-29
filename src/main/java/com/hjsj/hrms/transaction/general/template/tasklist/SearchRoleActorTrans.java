package com.hjsj.hrms.transaction.general.template.tasklist;

import com.hjsj.hrms.businessobject.general.template.tasklist.TaskListRoleActorInfoBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchRolePeopleTrans.java</p>
 * <p>Description>:查看具有某种角色的人员</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-1-13 下午03:38:17</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class SearchRoleActorTrans extends IBusiness {

    /* (non-Javadoc)
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        try
        {
            String tabId=(String)this.getFormHM().get("tabId");
            String taskid=(String)this.getFormHM().get("taskId");
            taskid= PubFunc.decrypt(taskid);
            TaskListRoleActorInfoBo roleActorInfoBo= new TaskListRoleActorInfoBo(this.frameconn,this.userView);
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
