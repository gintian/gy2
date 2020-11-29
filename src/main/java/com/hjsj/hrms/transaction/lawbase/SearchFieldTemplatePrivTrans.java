package com.hjsj.hrms.transaction.lawbase;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchFieldTemplatePrivTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
    		String flag=(String)this.getFormHM().get("flag");
    		String roleid=(String)this.getFormHM().get("roleid");
     		String res_flag=(String)this.getFormHM().get("res_flag");
     		this.getFormHM().put("flag",flag);
     		this.getFormHM().put("roleid",roleid);
     		this.getFormHM().put("res_flag", res_flag);
     		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
