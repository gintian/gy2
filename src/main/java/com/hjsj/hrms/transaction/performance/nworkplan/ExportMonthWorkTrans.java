package com.hjsj.hrms.transaction.performance.nworkplan;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExportMonthWorkTrans extends IBusiness{
	public void execute() throws GeneralException {
		try{
			String p0100  =(String)this.getFormHM().get("p0100");
			String currentYear  =(String)this.getFormHM().get("currentYear");
			String currentMonth  =(String)this.getFormHM().get("currentMonth");
			String nextYear  =(String)this.getFormHM().get("nextYear");
			String nextMonth  =(String)this.getFormHM().get("nextMonth");
			NworkPlanBo  bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			String outName = bo.creatMonthExcel(p0100,currentYear,currentMonth,nextYear,nextMonth);
			this.getFormHM().put("outName", SafeCode.decode(outName));
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}
