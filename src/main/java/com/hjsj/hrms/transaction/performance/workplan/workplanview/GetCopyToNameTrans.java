package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetCopyToNameTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String personstr=(String)this.getFormHM().get("personstr");
			WorkPlanViewBo bo = new WorkPlanViewBo(this.userView,this.getFrameconn());
			String name=bo.changeA0100ToName(personstr);
			this.getFormHM().put("name",name);
			if(personstr.length()>0)
				personstr=personstr.substring(0,personstr.length()-1);
			this.getFormHM().put("a0100",personstr);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
