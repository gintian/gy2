package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class HasThisGradeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String ids = (String)this.getFormHM().get("ids");
			KhFieldBo  bo = new KhFieldBo(this.getFrameconn());
			String msg = "";
			msg=bo.hasThisGrade(ids,"33");
			this.getFormHM().put("msg",msg);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
