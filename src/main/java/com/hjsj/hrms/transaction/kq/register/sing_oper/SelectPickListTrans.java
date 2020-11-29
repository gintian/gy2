package com.hjsj.hrms.transaction.kq.register.sing_oper;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SelectPickListTrans extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
		ArrayList opinlist=(ArrayList)this.getFormHM().get("opinlist");	
		if(opinlist==null||opinlist.size()<=0)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.work.error"),"",""));
		}else
		{
			this.getFormHM().put("opinlist",opinlist);
		}    	
	}

}
