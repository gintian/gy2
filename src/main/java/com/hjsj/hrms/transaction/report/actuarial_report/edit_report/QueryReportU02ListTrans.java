package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class QueryReportU02ListTrans  extends IBusiness {


	public void execute() throws GeneralException {
		String unitcode=(String)this.getFormHM().get("unitcode");
		String id=(String)this.getFormHM().get("id");		
		String Report_id=(String)this.getFormHM().get("report_id");	
		String kmethod =(String)this.getFormHM().get("kmethod");
		EditReport editReport=new EditReport();
    	ArrayList fieldlist=editReport.getU02FieldList(this.getFrameconn(),Report_id,true);
		ArrayList list = editReport.getU02QueryList(fieldlist);
    	
		this.getFormHM().put("editlistU02", list);
		this.getFormHM().put("report_id", Report_id);
		this.getFormHM().put("unitcode", unitcode);
		this.getFormHM().put("id", id);
//		this.getFormHM().put("olditemdesc", olditemdesc);
	}

}
