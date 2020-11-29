package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveTemplateFieldSortTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String bank_id=(String)this.getFormHM().get("bank_id");
			String sortids=(String)this.getFormHM().get("sortids");
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			bo.sortTemplateField((sortids==null|| "".equals(sortids))?"":sortids.substring(1), bank_id);
			this.getFormHM().put("ids",sortids);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
