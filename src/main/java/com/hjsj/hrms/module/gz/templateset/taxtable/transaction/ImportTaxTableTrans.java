package com.hjsj.hrms.module.gz.templateset.taxtable.transaction;

import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.ITaxTableService;
import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.impl.TaxTableServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 导入税率表
 * @Author manjg
 * @Date 2019/12/3 14:40
 * @Version V1.0
 **/
public class ImportTaxTableTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
    	String fileId = (String)this.getFormHM().get("fileid");
    	ArrayList taxList = (ArrayList)this.getFormHM().get("taxList");
    	HashMap returnData = new HashMap();
    	String returnCode = "success";
    	String returnMsg = "";
    	try {
    		ITaxTableService taxTableService = new TaxTableServiceImpl(this.frameconn,this.userView);
    		if(taxList==null) {
				//返回内容为 id：税率表名
    			List<Map> list = taxTableService.importTaxTable(fileId);
    			returnData.put("taxList", list);
    		}else {
    			taxTableService.importTaxTable(taxList);
    		}
    	}catch (GeneralException e) {
    		e.printStackTrace();
    		returnCode = "fail";
			returnMsg = e.getErrorDescription();
		}
		this.formHM.put("return_code", returnCode);
		this.formHM.put("return_data", returnData);
		this.formHM.put("return_Msg", returnMsg);
    }
    
    
    
}