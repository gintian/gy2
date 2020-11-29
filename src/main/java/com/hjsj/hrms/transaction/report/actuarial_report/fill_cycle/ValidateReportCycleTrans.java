package com.hjsj.hrms.transaction.report.actuarial_report.fill_cycle;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ValidateReportCycleTrans extends IBusiness {

	
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String reportcycleid=(String)hm.get("reportcycleid");
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id,name,bos_date,theyear,kmethod,status from tt_cycle where status='04'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String flag ="false";
		try 
		{
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next()) 
			{
				flag ="true";
			}

		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);				
		}
		this.getFormHM().put("flag",flag);
		this.getFormHM().put("reportcycleid",reportcycleid);
	}
	
}
