package com.hjsj.hrms.transaction.performance.objectiveManage.designateTask;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class EditDesignateTaskTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String taskid = (String)map.get("task_id");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select to_p0407 from per_designate_task where task_id="+taskid);
			String p0407="";
			while(this.frowset.next())
			{
				p0407=this.frowset.getString("to_p0407");
			}
			this.getFormHM().put("p0407", p0407);
			this.getFormHM().put("taskid", taskid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
