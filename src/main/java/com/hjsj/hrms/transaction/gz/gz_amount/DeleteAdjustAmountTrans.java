package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteAdjustAmountTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String setid=(String)this.getFormHM().get("setid");
			String ids=(String)this.getFormHM().get("ids");
			GrossManagBo gmb = new GrossManagBo(this.getFrameconn());
			gmb.deleteRecord(ids, setid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
