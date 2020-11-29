package com.hjsj.hrms.transaction.performance.achivement.standarditem;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteNoPointItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.delete("delete from per_standard_item where UPPER(point_id)='XXXXPPPP'",new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
