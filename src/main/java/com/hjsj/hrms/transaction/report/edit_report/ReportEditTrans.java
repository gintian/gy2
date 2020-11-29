package com.hjsj.hrms.transaction.report.edit_report;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ReportEditTrans extends IBusiness {

	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		if(tabid==null|| "".equals(tabid)){
			tabid = "";
		}
		this.getFormHM().put("tabid", tabid);
	}

}
