package com.hjsj.hrms.module.gz.mysalary.transaction;

import com.hjsj.hrms.module.gz.mysalary.businessobject.impl.MySalaryServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 检查视图表是否重名
 * @author Administrator
 *
 */
public class SearchSalaryFeildSetTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String salary_table = (String) this.getFormHM().get("salary_table");
		String salary_table_name = (String) this.getFormHM().get("salary_table_name");
		MySalaryServiceImpl salaryService = new MySalaryServiceImpl(this.getFrameconn());
		String return_code = "";
		try {
			return_code = salaryService.checkSalaryViewTable(salary_table_name,salary_table);
		} catch (GeneralException e) {
			e.printStackTrace();
			return_code ="fail";
			this.getFormHM().put("return_msg", e.getErrorDescription());
		}
		this.getFormHM().put("return_code", return_code);
	}

}
