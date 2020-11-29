package com.hjsj.hrms.transaction.performance.objectiveManage.designateTask;

import com.hjsj.hrms.businessobject.performance.objectiveManage.DesignateTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteSinglePeopleTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String p0400=(String)this.getFormHM().get("p0400");
			String group_id=(String)this.getFormHM().get("group_id");
			String taskid=(String)this.getFormHM().get("taskid");
			DesignateTaskBo bo = new DesignateTaskBo(this.getFrameconn(),this.userView);
			bo.deleteSinglePeople(taskid, p0400, group_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
