package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableSetBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class RenameTaxTableTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String description =(String)this.getFormHM().get("description");
			String taxid = (String)this.getFormHM().get("taxid");
			TaxTableSetBo bo = new TaxTableSetBo(this.getFrameconn());
			bo.renameTaxTable(taxid,SafeCode.decode(description));
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
