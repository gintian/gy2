package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SetHmusterTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String  nFlag=(String)hm.get("nFlag");
		this.getFormHM().put("modelFlag",nFlag);
		String relatTableid=(String)hm.get("relatTableid");
		String condition=(String)this.getFormHM().get("condition");
		condition=condition.replaceAll("%20"," ");
		String returnURL=(String)this.getFormHM().get("returnURL");
		String tabID=(String)hm.get("tabID");
		
		this.getFormHM().put("tabID",tabID);
		this.getFormHM().put("relatTableid",relatTableid);
		this.getFormHM().put("condition",condition);
		this.getFormHM().put("returnURL",returnURL);
		this.getFormHM().put("inforkind",nFlag);
		
		

	}

}
