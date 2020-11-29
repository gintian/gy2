package com.hjsj.hrms.transaction.report.retport_status;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SetReportStatusInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String lookInfo =(String)hm.get("b_lookInfo");
		String  unitcode=(String)hm.get("unitcode");
		String  reportSet=(String)hm.get("reportSet");
		String  status=(String)hm.get("status");
		
		this.getUserView().getHm().put("statusInfo",unitcode+"/"+reportSet+"/"+status);
		if(lookInfo!=null&& "look".equals(lookInfo))
			this.getUserView().getHm().put("lookInfo",lookInfo);
		
	}

}
