package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExitTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.getFormHM().put("posID","");
			this.getFormHM().put("a0100","");
			this.getFormHM().put("userName","");
			this.getFormHM().put("loginName","");
			this.getFormHM().put("password","");
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		

	}

}
