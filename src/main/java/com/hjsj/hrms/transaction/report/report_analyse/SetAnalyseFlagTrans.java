package com.hjsj.hrms.transaction.report.report_analyse;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SetAnalyseFlagTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap map = (HashMap)(this.getFormHM().get("requestPamaHM"));
		String showFlag=(String)map.get("b_setShowflag");
		this.getFormHM().put("showFlag", showFlag);
		this.getFormHM().put("chartFlag","no");
	}

}
