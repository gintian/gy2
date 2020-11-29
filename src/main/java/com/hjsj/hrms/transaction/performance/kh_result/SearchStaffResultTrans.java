package com.hjsj.hrms.transaction.performance.kh_result;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchStaffResultTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
