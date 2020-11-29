package com.hjsj.hrms.transaction.performance.objectiveManage.designateTask;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveDesignateTaskTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String p0407=(String)this.getFormHM().get("p0407");
			String taskid=(String)this.getFormHM().get("taskid");
			p0407=PubFunc.getStr(p0407);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select p0400,group_id from per_designate_task where task_id="+taskid);
			int p0400=0;
			int group_id=0;
			while(this.frowset.next())
			{
				p0400=this.frowset.getInt("p0400");
				group_id=this.frowset.getInt("group_id");
			}
			//更新下达任务表
			dao.update("update per_designate_task set to_p0407='"+p0407+"' where p0400="+p0400+" and group_id="+group_id);
			//dao.update("update per_designate_task set to_p0407='"+p0407+"' where task_id="+taskid);
			//更新目标卡表
			dao.update("update p04 set p0407='"+p0407+"' where p0400 in (select to_p0400 from per_designate_task where p0400="+p0400+" and group_id="+group_id+" and task_type=1)");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
