package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.edit_report.EditReport;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 评估引入
 * @author Owner
 *
 */
public class IntroduceEvaluateDataTrans  extends IBusiness {


	public void execute() throws GeneralException 
	{
		String id=(String)this.getFormHM().get("id");		
		String flag=(String)this.getFormHM().get("flag");
		String report_id=(String)this.getFormHM().get("report_id");
		String unitcode=(String)this.getFormHM().get("unitcode");
		EditReport.introduceData(unitcode,id,report_id,this.getFrameconn(),this.userView);
	}
	
}
