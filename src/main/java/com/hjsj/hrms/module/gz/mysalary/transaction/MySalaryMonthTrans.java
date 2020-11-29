package com.hjsj.hrms.module.gz.mysalary.transaction;

import com.hjsj.hrms.module.gz.mysalary.businessobject.MySalaryService;
import com.hjsj.hrms.module.gz.mysalary.businessobject.impl.MySalaryServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取某年每月主页面数据 
 */
public class MySalaryMonthTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		
		String year = (String) this.getFormHM().get("year");
		String schemeId = (String) this.getFormHM().get("id");
		MySalaryService mySalaryService = new MySalaryServiceImpl(this.frameconn);
		
		HashMap<String, Object> salaryDataMap = new HashMap<String, Object>();
		String return_code = "";
		String return_msg = "";
		Map monthData = null;
		
		try {
			return_code = "success";
			monthData = mySalaryService.getMySalaryMonthData(this.userView, schemeId, year);
		} catch (GeneralException e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = e.getErrorDescription();
		}
		salaryDataMap.put("return_code", return_code);
		salaryDataMap.put("return_msg", return_msg);
		salaryDataMap.put("return_data", monthData);
		
		this.getFormHM().put("returnStr", salaryDataMap);
		
	}
	
}
