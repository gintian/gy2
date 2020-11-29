package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteBankItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String itemids=(String)this.getFormHM().get("itemids");
			String bank_id=(String)this.getFormHM().get("bank_id");
		    String[] arr=itemids.split(",");
		    StringBuffer idsBuf= new StringBuffer();
		    for(int i=0;i<arr.length;i++)
		    {
		    	idsBuf.append(",'");
		    	idsBuf.append(arr[i]);
		    	idsBuf.append("'");
		    }
			StringBuffer sql = new StringBuffer();
			
			sql.append("delete from gz_bank_item ");
			sql.append("where field_name in(");
			sql.append(idsBuf.toString().substring(1));
			sql.append(")");
			sql.append(" and bank_id=");
			sql.append(bank_id);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.delete(sql.toString(), new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
