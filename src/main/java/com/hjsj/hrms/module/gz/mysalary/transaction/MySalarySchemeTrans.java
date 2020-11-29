package com.hjsj.hrms.module.gz.mysalary.transaction;

import com.hjsj.hrms.module.gz.mysalary.businessobject.impl.MySalaryServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的薪酬主界面操作
 * @author Administrator
 *
 */
public class MySalarySchemeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		MySalaryServiceImpl salaryService = new MySalaryServiceImpl(this.getFrameconn());
		String jsonStr = (String) this.getFormHM().get("jsonStr");
		Map returnData = null;
		String returnCode = "success";
		String type = (String) this.getFormHM().get("type");
		if("delete".equals(type)) {
			String ids = (String) this.getFormHM().get("ids");
			try {
				returnCode = salaryService.deleteMySalaryScheme(ids);
			} catch (GeneralException e) {
				e.printStackTrace();
				returnCode = "fail";
				this.getFormHM().put("return_msg", e.getErrorDescription());
			}
		} else if (StringUtils.equals("search", type)) {
			String id = (String) this.getFormHM().get("id");
			try {
				returnData = salaryService.getMySalaryScheme(id, this.getUserView());
				if (StringUtils.isNotBlank(id)) {
					String salary_table = (String) ((HashMap) returnData.get("salary_fields")).get("salary_table");
					Map salaryViewField = salaryService.getMySalaryViewField(salary_table);
					returnData.put("salaryViewField",salaryViewField);
				}
			} catch (GeneralException e) {
				returnCode = "fail";
				this.getFormHM().put("return_msg", e.getErrorDescription());
			}

		}else if(StringUtils.equals("searchSalaryViewField",type)){//查询视图表字段
			String salaryTableName = (String) this.getFormHM().get("tableName");
			Map salaryViewField = salaryService.getMySalaryViewField(salaryTableName);
			returnData = salaryViewField;
		}else if(StringUtils.equals("save",type)){
			String data = (String) this.getFormHM().get("data");
			JSONObject dataObject = JSONObject.fromObject(data);
			Map map = dataObject;
			//HashMap dataMap = PubFunc.DynaBean2Map(data);
			try {
				returnData = salaryService.saveMySalaryScheme(map,this.getUserView());

			} catch (GeneralException e) {
				returnCode = "fail";
				this.getFormHM().put("return_msg", e.getErrorDescription());
			}
		}else if (StringUtils.equals("saveNorder",type)){
			ArrayList sortArray = (ArrayList) this.getFormHM().get("sortArray");
			for (Object list : sortArray){
				String sortItem = (String) list;
				salaryService.saveNorder(sortItem);
			}
			List salaryScheme = salaryService.listMySalaryScheme(this.getUserView());
			returnData = new HashMap();
			returnData.put("salaryScheme",salaryScheme);
			returnCode = "success";

		}
		this.getFormHM().put("return_data", returnData);
		this.getFormHM().put("return_code", returnCode);
	}

}
