package com.hjsj.hrms.transaction.performance.objectiveManage.designateTask;

import com.hjsj.hrms.businessobject.performance.objectiveManage.DesignateTaskBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SavePeopleTrans extends IBusiness {
	public void execute() throws GeneralException {
		try
		{
			DesignateTaskBo bo = new DesignateTaskBo(this.getFrameconn(),this.userView);
			String p0400=(String)this.getFormHM().get("p0400");
			String a0100s=(String)this.getFormHM().get("hiddenStr");
			String p0407=SafeCode.decode((String)this.getFormHM().get("p0407"));
			String type=(String)this.getFormHM().get("type");
			String taskid=(String)this.getFormHM().get("taskid");
			String group_id=(String)this.getFormHM().get("group_id");
			String task_type=(String)this.getFormHM().get("task_type");
			bo.addDesinateTask2(p0400, a0100s, p0407, type, taskid, group_id, task_type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
}
