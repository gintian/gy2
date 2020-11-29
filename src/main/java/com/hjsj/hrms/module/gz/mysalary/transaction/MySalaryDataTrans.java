package com.hjsj.hrms.module.gz.mysalary.transaction;

import com.hjsj.hrms.module.gz.mysalary.businessobject.MySalaryService;
import com.hjsj.hrms.module.gz.mysalary.businessobject.impl.MySalaryServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
/**
 * 获取我的薪酬主页面数据
 */
public class MySalaryDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		
		String schemeId = (String) this.getFormHM().get("id"); 
		Map salaryData = new HashMap();
		MySalaryService mySalaryService = new MySalaryServiceImpl(this.frameconn);
		HashMap<String, Object> salaryDataMap = new HashMap<String, Object>();
		String return_code = "";
		String return_msg = "";
		try {
			return_code = "success";
			salaryData = mySalaryService.getMySalaryData(this.userView,schemeId);
		} catch (GeneralException e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = e.getErrorDescription();
			if(StringUtils.isEmpty(schemeId)) { //初始化时获取第一个薪酬方案有异常,防止页面空白
				try {
					HashMap salaryScheme = ((MySalaryServiceImpl) mySalaryService).getMySalaryScheme(this.userView);
					salaryData.put("schemes", salaryScheme == null ? "[]" : salaryScheme.get("schemes"));
				}catch (GeneralException ex) {
					return_msg = ex.getErrorDescription();
				}
			}
		}
		salaryDataMap.put("return_code", return_code);
		salaryDataMap.put("return_msg", return_msg);
		salaryDataMap.put("return_data", salaryData);
		
		this.getFormHM().put("returnStr", salaryDataMap);
		
	}

}
