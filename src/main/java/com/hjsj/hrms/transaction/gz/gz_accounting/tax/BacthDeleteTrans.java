package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class BacthDeleteTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String whereSql=(String)this.getFormHM().get("whereSql");
			String fromtable=(String)this.getFormHM().get("fromtable");
			if(fromtable==null)
				fromtable="gz_tax_mx";
			whereSql=PubFunc.decrypt(SafeCode.decode(whereSql));
			 
			whereSql=whereSql.toString().substring(whereSql.indexOf("where")+5, whereSql.indexOf("order by"));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String sql="delete from "+fromtable+" where "+whereSql;
			dao.delete(sql, new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
