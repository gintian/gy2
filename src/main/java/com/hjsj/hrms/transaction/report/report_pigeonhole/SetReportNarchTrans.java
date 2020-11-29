package com.hjsj.hrms.transaction.report.report_pigeonhole;

import com.hjsj.hrms.businessobject.report.report_pigeonhole.ReportPigeonholeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SetReportNarchTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		try
		{
			
	
			String operate=(String)this.getFormHM().get("operate");					 // 1:表类  2：单表
			String selectedIDs=(String)this.getFormHM().get("selectedIDs");
			String narch=(String)this.getFormHM().get("narch");
			ReportPigeonholeBo bo=new ReportPigeonholeBo(this.getFrameconn());
			String info="1";
			if(!bo.setReportNarch(selectedIDs,operate,narch))
				info="0";
			this.getFormHM().put("info",info);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.getFormHM().put("info","0");
		}
	}

}
