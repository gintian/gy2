package com.hjsj.hrms.transaction.train.resource.mylessons;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class MyLessonsTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
		String home = (String)hm.get("home");
		hm.remove("home");
		
		if(null == home)
		{
		    home = "0";
		}
		this.getFormHM().put("home", home);
	}
	
}
