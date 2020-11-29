package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class PrintFeastInfoTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String condition=(String)this.getFormHM().get("condition");
    	this.getFormHM().put("condition",condition);
    	this.getFormHM().put("relatTableid","17");
    	this.getFormHM().put("returnURL","/kq/feast_manage/managerdata.do?b_search=link");
    }

	

}
