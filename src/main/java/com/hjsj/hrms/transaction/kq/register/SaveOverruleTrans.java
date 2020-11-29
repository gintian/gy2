package com.hjsj.hrms.transaction.kq.register;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveOverruleTrans extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");		
		this.getFormHM().put("overrulelist",selectedinfolist);
		this.getFormHM().put("overrule","");
		this.getFormHM().put("overrule_status","1");
	}
}
