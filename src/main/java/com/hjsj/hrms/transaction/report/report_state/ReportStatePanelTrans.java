package com.hjsj.hrms.transaction.report.report_state;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ReportStatePanelTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String code = (String) hm.get("ucode");
		this.getFormHM().put("ucode",code);
		//System.out.println("ucode===" + code);
		hm.remove("ucode");
		String dxt = (String)hm.get("returnvalue");
		if(dxt!=null&&!"dxt".equals(dxt))
			hm.remove("returnvalue");
		if(dxt==null)
			dxt="";
		this.getFormHM().put("returnflag", dxt);
	}

}
