package com.hjsj.hrms.transaction.performance.objectiveManage.designateTask;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class DelDesignateTaskTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String opt = (String)this.getFormHM().get("opt");
			String taskid=(String)this.getFormHM().get("taskid");
			if("del".equals(opt))//删除下达任务
			{
				String hasXDR=(String)this.getFormHM().get("hasr");//该任务是否已经下达给别人，
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
