package com.hjsj.hrms.transaction.report.report_pigeonhole;

import com.hjsj.hrms.businessobject.report.report_pigeonhole.ReportPigeonholeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:报表批量归档</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 4, 2007:5:38:34 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class ReportBatchPigeonholeTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String operate=(String)this.getFormHM().get("operate");					 // 1:表类  2：单表
			String selectedIDs=(String)this.getFormHM().get("selectedIDs");
			String selectUnitType=(String)this.getFormHM().get("selectUnitType");    //1:全部  2：部分
			String unitIDs=(String)this.getFormHM().get("unitIDs");
			String reportType=(String)this.getFormHM().get("reportType");
			String year=(String)this.getFormHM().get("year");
			String count=(String)this.getFormHM().get("count");
			String week="";
			    if(Integer.parseInt(reportType)==6)
			    	week=(String)this.getFormHM().get("week");
			ReportPigeonholeBo bo=new ReportPigeonholeBo(this.getFrameconn());
			String info=bo.reportPigeonholeTrans(this.getUserView(),operate,selectedIDs,selectUnitType,unitIDs,year,count,reportType,week);
			this.getFormHM().put("info",info);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
