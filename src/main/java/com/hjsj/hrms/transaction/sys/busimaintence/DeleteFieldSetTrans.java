package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteFieldSetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String fieldsetid = (String)this.getFormHM().get("fieldsetid");
			String msg = "no";
			BusiSelStr bss = new BusiSelStr();
			boolean flag = bss.isDeleteFieldSet(fieldsetid, this.getFrameconn());
			if(flag)
				msg="yes";//delete
			this.getFormHM().put("msg",msg);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
