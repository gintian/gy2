package com.hjsj.hrms.transaction.selfinfo.agent;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class AgentPurviewTrans extends IBusiness {
	public void execute() throws GeneralException {
//		String role_id=this.userView.getDbname()+this.userView.getA0100();
//		String user_flag=this.userView.getStatus()+"";
//		this.getFormHM().put("role_id", role_id);
//		this.getFormHM().put("user_flag", user_flag);
//		String id=(String)this.getFormHM().get("id");
		HashMap rMap = (HashMap)this.getFormHM().get("requestPamaHM");
		String operate = (String)rMap.get("operate");
		rMap.remove("operate");
		this.getFormHM().put("operate", operate);
//		this.getFormHM().put("id", id);
	}

}
