package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;

import com.hjsj.hrms.businessobject.report.actuarial_report.ReportExcelBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExportExcelTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String cycle_id=(String)this.getFormHM().get("cycle_id");
			String unitcode=(String)this.getFormHM().get("unitcode");
			String report_id=(String)this.getFormHM().get("report_id"); //U03,U04,U05
			String flag = (String)this.getFormHM().get("flag");
			
			ReportExcelBo bo=new ReportExcelBo(this.getFrameconn());
			String fileName=bo.executeReportExcel(report_id,unitcode,cycle_id,this.userView,flag);
//			fileName=fileName.replaceAll(".xls","#");;
			fileName = PubFunc.encrypt(fileName);  //add by wangchaoqun on 2014-9-16
			this.getFormHM().put("fileName",fileName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
