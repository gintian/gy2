package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class RefreshSelectTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			String flag= (String)this.getFormHM().get("flag");
			BusiSelStr busiselstr=new BusiSelStr();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.getFormHM().put("relating",SafeCode.encode(busiselstr.getRelatingCode(dao,"")));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
   
}
