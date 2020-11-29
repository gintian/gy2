package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.WorkDiaryBo;
import com.hjsj.hrms.utils.PageNumber;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class GlobalQueryWorkDiaryTrans extends IBusiness{
	public void execute() throws GeneralException  {
		try{
		    
			
			String queryTitle = SafeCode.decode((String)this.getFormHM().get("queryTitle"));
			String queryStart_time = (String)this.getFormHM().get("queryStart_time");
			String queryEnd_time = (String)this.getFormHM().get("queryEnd_time");
			String queryStartHour = (String)this.getFormHM().get("queryStartHour");
			String queryEndHour = (String)this.getFormHM().get("queryEndHour");
			String queryStartMinute = (String)this.getFormHM().get("queryStartMinute");
			String queryEndMinute = (String)this.getFormHM().get("queryEndMinute");
			String frompage = (String)this.getFormHM().get("frompage");
			String fromyear = (String)this.getFormHM().get("fromyear");
			String frommonth = (String)this.getFormHM().get("frommonth");
			String fromday = (String)this.getFormHM().get("fromday");
			String a = (String)this.getFormHM().get("a");
			String queryContent = SafeCode.decode((String)this.getFormHM().get("queryContent"));
			PageNumber handlePage=(PageNumber)this.userView.getHm().get("handlePage");
			if(a==null|| "".equals(a.trim())){
				handlePage = null;
			}
			WorkDiaryBo bo = new WorkDiaryBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			bo.a = a;
			String tableHtml = bo.getQueryResults(handlePage,fromyear,frommonth,fromday,frompage,queryTitle,queryStart_time,queryEnd_time,queryStartHour,queryEndHour,queryContent,queryStartMinute,queryEndMinute);
			this.getFormHM().put("tableHtml", SafeCode.encode(tableHtml));
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}
