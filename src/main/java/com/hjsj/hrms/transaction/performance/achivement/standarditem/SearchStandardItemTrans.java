package com.hjsj.hrms.transaction.performance.achivement.standarditem;

import com.hjsj.hrms.businessobject.performance.achivement.StandardItemBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchStandardItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String point_id="emp_001";
			StandardItemBo bo = new StandardItemBo(this.getFrameconn());
			String html = bo.getStandardItemHTML(point_id);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
