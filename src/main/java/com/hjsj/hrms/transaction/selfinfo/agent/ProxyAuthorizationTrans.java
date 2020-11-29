package com.hjsj.hrms.transaction.selfinfo.agent;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ProxyAuthorizationTrans extends IBusiness {
	public void execute() throws GeneralException {
		String role_id=this.userView.getDbname()+this.userView.getA0100();
		String user_flag=this.userView.getStatus()+"";
		this.getFormHM().put("role_id", role_id);
		this.getFormHM().put("user_flag", user_flag);
		HashMap rMap = (HashMap)this.getFormHM().get("requestPamaHM");
		String id=(String)rMap.get("id");
		rMap.remove("id");
		this.getFormHM().put("id", id);
		
	}

}
