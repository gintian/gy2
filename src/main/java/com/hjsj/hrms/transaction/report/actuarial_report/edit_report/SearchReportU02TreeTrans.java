package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchReportU02TreeTrans extends IBusiness {


	public void execute() throws GeneralException {
		String unitcode=(String)this.getFormHM().get("unitcode");
		String id=(String)this.getFormHM().get("id");
		String flag=(String)this.getFormHM().get("flag");
		String Report_id=(String)this.getFormHM().get("report_id");
		this.getFormHM().put("unitcode", unitcode);
		this.getFormHM().put("id", id);
		this.getFormHM().put("flag", flag);
		this.getFormHM().put("report_id", Report_id);
	}

}
