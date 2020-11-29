package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveSalarySetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String itemid=(String)this.getFormHM().get("itemid");
			String salaryid=(String)this.getFormHM().get("salaryid");
			GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),0);
			bo.setSalarySet(itemid, salaryid);
			bo.saveParameters();
			this.getFormHM().put("salaryid", salaryid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
