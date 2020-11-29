package com.hjsj.hrms.module.gz.templateset.taxtable.transaction;

import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.ITaxTableService;
import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.impl.TaxTableServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @Description: 删除税率方案记录
 * @Author manjg
 * @Date 2019/12/3 14:39
 * @Version V1.0
 **/
public class DeleteTaxTableTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
    	String ids = this.getFormHM().get("ids").toString();
    	HashMap returnData = new HashMap();
    	HashMap resultMap = new HashMap();
    	try {
    		ITaxTableService taxTableSer = new TaxTableServiceImpl(this.frameconn, this.userView);
    		taxTableSer.deleteTaxTable(ids);
    		resultMap.put("return_code", "success");
			this.formHM.put("returnStr", resultMap);
		} catch (GeneralException e) {
			e.printStackTrace();
			resultMap.put("return_code", "fail");
			resultMap.put("return_msg", e.getErrorDescription().split(",")[0]);
			returnData.put("file_name", e.getErrorDescription().split(",")[1]);
			resultMap.put("return_data", returnData);
			this.formHM.put("returnStr", resultMap);
		}
    }
}