package com.hjsj.hrms.transaction.performance.nworkdiary.myworkdiary.monthwork;

import com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary.WorkDiaryBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ExportWorkDiaryExcelTrans extends IBusiness{
	public void execute() throws GeneralException {
		try{
			String state  =(String)this.getFormHM().get("state");
			String syear  =(String)this.getFormHM().get("syear");
			String smonth  =(String)this.getFormHM().get("smonth");
			String sday  =(String)this.getFormHM().get("sday");
			String eyear  =(String)this.getFormHM().get("eyear");
			String emonth  =(String)this.getFormHM().get("emonth");
			String eday  =(String)this.getFormHM().get("eday");
			WorkDiaryBo bo = new WorkDiaryBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			String outName = bo.creatExcel(state,syear,smonth,sday,eyear,emonth,eday);
			this.getFormHM().put("outName", PubFunc.encrypt(outName));
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}





