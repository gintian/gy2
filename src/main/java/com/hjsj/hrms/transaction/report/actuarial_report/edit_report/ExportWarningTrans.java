package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ReportExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 
 * create time:2010-06-21 11:00:00
 * </p>
 * 
 * @author xieguiquan
 * @version 1.0
 * 
 */
public class ExportWarningTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
    try
	{
		String cycle_id=(String)this.getFormHM().get("id");
		String unitcode=(String)this.getFormHM().get("unitcode");
		String report_id=(String)this.getFormHM().get("report_id"); //U01,U03,U05
	
		ReportExcelBo bo=new ReportExcelBo(this.getFrameconn());
		String fileName=bo.exportWarningExcel(report_id,unitcode,cycle_id,this.userView);
//		fileName=fileName.replaceAll(".xls","#");;
		fileName = PubFunc.encrypt(fileName);
		this.getFormHM().put("fileName",fileName);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}

	
	
}
