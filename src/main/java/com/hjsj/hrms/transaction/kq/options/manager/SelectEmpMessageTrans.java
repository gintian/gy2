package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.options.UserManager;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SelectEmpMessageTrans  extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	UserManager userManager=new UserManager();
    	ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");		
    	this.getFormHM().put("opinlist",selectedinfolist);
    }

}
