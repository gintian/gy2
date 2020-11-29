package com.hjsj.hrms.module.gz.mysalary.transaction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjsj.hrms.module.gz.mysalary.businessobject.impl.MySalaryServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.StringReader;
import java.util.List;

/**
 * 我的薪酬配置主界面
 * @author Administrator
 *
 */
public class SearchSelfSalaryTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		MySalaryServiceImpl salaryService = new MySalaryServiceImpl(this.getFrameconn());
		String jsonStr = (String) this.getFormHM().get("jsonStr");
		JsonParser parse =new JsonParser();  //创建json解析器
		JsonObject json=(JsonObject) parse.parse(new StringReader(jsonStr));
		String type = json.get("type").getAsString();
		if("main".equals(type)) {//主界面
			List returnData = null;
			try {
				returnData = salaryService.listMySalaryScheme(getUserView());
			} catch (GeneralException e) {
				e.printStackTrace();
				this.getFormHM().put("return_code", "fail");
				this.getFormHM().put("return_msg", e.getErrorDescription());
			}
			this.getFormHM().put("return_code", "success");
			this.getFormHM().put("return_data", returnData);
		}
	}

}
