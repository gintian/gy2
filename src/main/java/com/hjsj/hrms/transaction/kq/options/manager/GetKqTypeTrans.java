package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.options.UserManager;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
public class GetKqTypeTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	UserManager userManager=new UserManager();
    	/*ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");		
		if(selectedinfolist==null||selectedinfolist.size()==0)
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.noselect.manager"),"",""));*/
		ArrayList list=userManager.getKqTypeList(this.getFrameconn());
        this.getFormHM().put("codelist",list);    	
    }

}
