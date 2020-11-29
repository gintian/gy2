package com.hjsj.hrms.transaction.performance.nworkplan.week;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ExportWeekWorkPlanTrans extends IBusiness{
	public void execute() throws GeneralException {
		try{
			String p0100  =(String)this.getFormHM().get("p0100");
			String summarizeTime  =(String)this.getFormHM().get("summarizeTime");
			summarizeTime = summarizeTime.replaceAll("－－", "--");
			String summarizeFields=(String)this.getFormHM().get("summarizeFields"); 
			String planFields=(String)this.getFormHM().get("planFields");
			NworkPlanBo  bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			ArrayList zongjieFieldsList = bo.getJihuaOrZongjieFieldsList("","",null,"",summarizeFields);
			ArrayList jihuaFieldsList = bo.getJihuaOrZongjieFieldsList("","",null,"",planFields);
			String outName = bo.creatWeekExcel(p0100,zongjieFieldsList,jihuaFieldsList,summarizeTime);
			this.getFormHM().put("outName", PubFunc.encrypt(outName));
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}   
	}
}
