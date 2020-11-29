package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class IsHaveFieldSetidTrans extends IBusiness{

	
	public void execute() throws GeneralException {
		try
		{
			String fieldsetid = (String)this.getFormHM().get("fieldsetid");
			String setdesc = (String)this.getFormHM().get("setdesc");
			setdesc = setdesc!=null?setdesc:"";
			setdesc = com.hrms.frame.codec.SafeCode.decode(setdesc);
			String msg = "no";
			BusiSelStr bss = new BusiSelStr();
			boolean flag = bss.isHaveFieldsetid(setdesc,fieldsetid, this.getFrameconn());
			if(flag)
			{
				msg = "yes";
			}
			this.getFormHM().put("msg",msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
