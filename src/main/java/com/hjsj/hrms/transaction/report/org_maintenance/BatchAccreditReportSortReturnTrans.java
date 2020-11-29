package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class BatchAccreditReportSortReturnTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String unitCodes = (String)hm.get("uc");
		String [] ucs = unitCodes.split(",");
		String unitCode = (String)ucs[0];
		//设置确定后的跳转参数，当前填报单位编码
		this.getFormHM().put("unitCodeFalg",unitCode);
		
	}

}
