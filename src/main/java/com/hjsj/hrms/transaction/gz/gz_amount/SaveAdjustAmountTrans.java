package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveAdjustAmountTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			ArrayList fieldList = (ArrayList)this.getFormHM().get("fieldList");
			String optType=(String)this.getFormHM().get("optType");
			String setid=(String)this.getFormHM().get("isHasAdjustSet");
			GrossManagBo gmb = new GrossManagBo(this.getFrameconn());
			gmb.saveRecord(setid, optType, fieldList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
