package com.hjsj.hrms.transaction.report.actuarial_report.fill_cycle;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddReportCycleTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
		RecordVo vo = new RecordVo("tt_cycle");
		Calendar c =  Calendar.getInstance();
		String year =String.valueOf(c.get(Calendar.YEAR));
		vo.setDate("bos_date", new Date());
		vo.setString("theyear", year);
			this.getFormHM().put("reportcyclevo2", vo);
	      	
	}
	
	

}
