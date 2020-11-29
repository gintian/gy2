package com.hjsj.hrms.transaction.workplan.summary;

import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;
import java.util.HashMap;

public class TeamWorkWeekTrans extends IBusiness {

	public void execute() throws GeneralException {

		try{
			
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String type = (String) hm.get("type");
			
	        String summaryCycle = (String) this.getFormHM().get("cycle");
	        String summaryYear = (String) this.getFormHM().get("year");
	        String summaryMonth = (String) this.getFormHM().get("month");
	        String summaryWeek = (String) this.getFormHM().get("week");
	        
			// 获取当前年月
	        Date now = new Date();
	        summaryCycle = com.hjsj.hrms.businessobject.workplan.WorkPlanConstant.SummaryCycle.WEEK;
	        summaryYear = String.valueOf(DateUtils.getYear(now));
	        summaryMonth = String.valueOf(DateUtils.getMonth(now));
	        summaryWeek = "1";
	        
			WorkPlanSummaryBo wp = new WorkPlanSummaryBo();
			int weeknum = wp.getWeekNum(Integer.parseInt(summaryYear), Integer.parseInt(summaryMonth));
			
			this.getFormHM().put("weeknum", weeknum+"");
			this.getFormHM().put("type", type);
			this.getFormHM().put("cycle", summaryCycle);
			this.getFormHM().put("year", summaryYear);
			this.getFormHM().put("month", summaryMonth);
			this.getFormHM().put("week", summaryWeek);
			
		}catch (Exception e) {
			throw  GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}
