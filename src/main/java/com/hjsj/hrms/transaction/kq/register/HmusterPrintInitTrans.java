package com.hjsj.hrms.transaction.kq.register;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class HmusterPrintInitTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String relatTableid=(String)this.getFormHM().get("relatTableid");
    	String condition=(String)this.getFormHM().get("condition");
    	String returnUrl=(String)this.getFormHM().get("returnURL");
    	this.getFormHM().put("condition",condition);
    	this.getFormHM().put("relatTableid",relatTableid);
    	this.getFormHM().put("returnURL",returnUrl);
    }

}
