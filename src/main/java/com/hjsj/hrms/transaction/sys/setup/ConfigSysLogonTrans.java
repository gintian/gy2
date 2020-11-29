package com.hjsj.hrms.transaction.sys.setup;

import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ConfigSysLogonTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String username=SystemConfig.getProperty("db_user");
		String password=SystemConfig.getProperty("db_user_pwd");
		String iusername=(String) hm.get("username");
		String ipassword=(String) hm.get("password");
		if(username.equalsIgnoreCase(iusername)&&password.equalsIgnoreCase(ipassword)){
			hm.put("inflag","1");
		}
		else{
			hm.put("inflag","0");
//			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.user.password"),"",""));

		}
	}

}
