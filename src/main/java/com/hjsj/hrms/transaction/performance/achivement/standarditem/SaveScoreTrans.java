package com.hjsj.hrms.transaction.performance.achivement.standarditem;

import com.hjsj.hrms.businessobject.performance.achivement.StandardItemBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveScoreTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String value=(String)this.getFormHM().get("value");
			StandardItemBo bo = new StandardItemBo(this.getFrameconn());
			bo.saveSocre(value);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
