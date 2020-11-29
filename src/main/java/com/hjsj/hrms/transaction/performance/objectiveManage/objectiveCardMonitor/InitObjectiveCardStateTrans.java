package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCardMonitor;

import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class InitObjectiveCardStateTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String plan_id=(String)this.getFormHM().get("plan_id");
			String objects=(String)this.getFormHM().get("ids");
			String opt=(String)this.getFormHM().get("opt"); //1:初始化 2:状态初始化
			if(opt==null)
				opt="1";
			SetUnderlingObjectiveBo bo = new SetUnderlingObjectiveBo(this.getFrameconn());
			bo.initObjectiveCardState(objects, plan_id, this.getUserView(),opt);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
