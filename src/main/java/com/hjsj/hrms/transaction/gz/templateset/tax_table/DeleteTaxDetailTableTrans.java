package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteTaxDetailTableTrans extends IBusiness  {

	public void execute() throws GeneralException {
		try{
			String taxid=(String)this.getFormHM().get("taxid");
			String taxitem=(String)this.getFormHM().get("taxitem");
			deleteRecords(taxid,taxitem);
			this.getFormHM().put("taxid",taxid);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public void deleteRecords(String taxid,String taxitem){
		try{
			String sql = "delete from gz_taxrate_item where taxitem in("+taxitem+") and taxid="+taxid;
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    dao.delete(sql,new ArrayList());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
