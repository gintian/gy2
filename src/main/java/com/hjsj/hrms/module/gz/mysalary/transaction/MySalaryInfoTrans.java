package com.hjsj.hrms.module.gz.mysalary.transaction;

import com.hjsj.hrms.module.gz.mysalary.businessobject.MySalaryService;
import com.hjsj.hrms.module.gz.mysalary.businessobject.impl.MySalaryServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

public class MySalaryInfoTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String type = (String) this.getFormHM().get("type");
		String schemeId = (String) this.getFormHM().get("id");
		MySalaryService mySalaryService = new MySalaryServiceImpl(this.frameconn);
		
		HashMap<String, Object> salaryDataMap = new HashMap<String, Object>();
		String return_code = "";
		String return_msg = "";
		Map salaryInfo = null;
		try {
			if("month".equals(type)) {
				String startDate = (String) this.getFormHM().get("startDate");
				String endDate = (String) this.getFormHM().get("endDate");
				salaryInfo = mySalaryService.getMySalaryInfo(this.userView, schemeId, startDate, endDate);
			}else if("year".equals(type)) {
				String year = (String) this.getFormHM().get("year");
				salaryInfo = mySalaryService.getMySalaryYearInfo(this.userView, schemeId, year);
			}else if("history".equals(type)) {
				salaryInfo = mySalaryService.getMySalaryHistoryInfo(this.userView, schemeId);
			}
			return_code = "success";
		} catch (GeneralException e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = e.getErrorDescription();
		}
		salaryDataMap.put("return_code", return_code);
		salaryDataMap.put("return_msg", return_msg);
		salaryDataMap.put("return_data", salaryInfo);
		
		this.getFormHM().put("returnStr", salaryDataMap);
	}
	
}
