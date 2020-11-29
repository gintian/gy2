package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ReportWorkPlanTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String p0100=(String)this.getFormHM().get("p0100");
			String opt=(String)this.getFormHM().get("opt");
			
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	

}
