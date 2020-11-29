package com.hjsj.hrms.transaction.performance.objectiveManage.designateTask;

import com.hjsj.hrms.businessobject.performance.objectiveManage.DesignateTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 90100170025
 * <p>Title:DeleteDesignateTaskTrans.java</p>
 * <p>Description>:DeleteDesignateTaskTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Aug 19, 2011  6:25:23 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class DeleteDesignateTaskTrans extends IBusiness {
	public void execute() throws GeneralException {
		try
		{
			String type=(String)this.getFormHM().get("type");
			String taskid=(String)this.getFormHM().get("taskid");
			String p0400=(String)this.getFormHM().get("p0400");
			String group_id=(String)this.getFormHM().get("group_id");
			DesignateTaskBo bo = new DesignateTaskBo(this.getFrameconn(),this.userView);
			bo.deleteTask(type, taskid, p0400, group_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
