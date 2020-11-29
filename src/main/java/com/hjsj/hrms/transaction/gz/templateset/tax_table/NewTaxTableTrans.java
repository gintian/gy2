package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableSetBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class NewTaxTableTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String description ="";
			if(this.getFormHM().get("description") != null)
				description = (String)this.getFormHM().get("description");
		   // IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			//String taxid = idg.getId("gz_tax_rate.taxid");
			TaxTableSetBo bo = new TaxTableSetBo(this.getFrameconn());
			int taxid=bo.getTaxId("gz_tax_rate","taxid");
			StringBuffer sql = new StringBuffer();
			sql.append(" insert into gz_tax_rate (taxid,description) values(");
			sql.append(taxid+",?)");
			ArrayList list = new ArrayList();
			list.add(SafeCode.decode(description));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.insert(sql.toString(),list);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
