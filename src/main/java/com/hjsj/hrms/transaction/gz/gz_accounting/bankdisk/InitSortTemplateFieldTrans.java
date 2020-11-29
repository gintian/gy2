package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitSortTemplateFieldTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String bank_id=(String)map.get("bank_id");
			String salaryid=(String)map.get("salaryid");
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn()); 
			HashMap salarySetMap=bo.getSalarySetFields(salaryid);
			ArrayList sortFieldList=bo.getSelectedItemList(bank_id,salarySetMap);
			this.getFormHM().put("sortFieldList",sortFieldList);
			this.getFormHM().put("bank_id",bank_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
