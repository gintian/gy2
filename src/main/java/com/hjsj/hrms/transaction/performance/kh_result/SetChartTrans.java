package com.hjsj.hrms.transaction.performance.kh_result;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SetChartTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String isChart = "0";
			String drawId = (String)this.getFormHM().get("drawId");
			if("1".equals(drawId)){
				isChart = "1";
			}
			this.getFormHM().put("isChart", isChart);
			this.getFormHM().put("drawId", drawId);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
