package com.hjsj.hrms.transaction.train.exchange;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ExchangeInitialTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String model = (String)hm.get("model");
		
		/*
		 * 清空一些查询条件
		 */
		
		
		if("1".equals(model)){
			this.getFormHM().put("searchstr", "");
			this.getFormHM().put("r5713", "");
		}else if("2".equals(model)){
			this.getFormHM().put("a0101", "");
			this.getFormHM().put("searchstr", "");
			this.getFormHM().put("startdate", "");
			this.getFormHM().put("enddate", "");
		}
		Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";
		this.getFormHM().put("uplevel", uplevel);
	}
	
}
