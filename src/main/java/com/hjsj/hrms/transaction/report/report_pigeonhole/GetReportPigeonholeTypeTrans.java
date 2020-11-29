package com.hjsj.hrms.transaction.report.report_pigeonhole;

import com.hjsj.hrms.businessobject.report.report_pigeonhole.ReportPigeonholeBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;


public class GetReportPigeonholeTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String operate=(String)this.getFormHM().get("operate");					 // 1:表类  2：单表
		String selectedIDs=(String)this.getFormHM().get("selectedIDs");
		ReportPigeonholeBo bo=new ReportPigeonholeBo(this.getFrameconn());
		ArrayList list=bo.getReportPigonholeType(selectedIDs,operate);
		String narch="";
		String info="";
		if(list.size()>1)
		{
			info=ResourceFactory.getProperty("report_collect.info6")+"！";
		}
		else if(list.size()==1)
		{
			narch=(String)list.get(0);
		}
		this.getFormHM().put("info",info);
		this.getFormHM().put("narch",narch);
	
	}
}
