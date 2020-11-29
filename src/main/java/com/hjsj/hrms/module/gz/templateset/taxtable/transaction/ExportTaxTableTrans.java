package com.hjsj.hrms.module.gz.templateset.taxtable.transaction;

import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.ITaxTableService;
import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.impl.TaxTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @Description: 导出税率表方案交易类
 * @Author manjg
 * @Date 2019/12/3 14:39
 * @Version V1.0
 **/
public class ExportTaxTableTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
    	HashMap returnData = new HashMap();
    	String returnCode = "success";
    	String returnMsg = "";
		try {
			String ids = (String)this.getFormHM().get("ids");
			String[] encrypttaxid = ids.split(",");
			String taxid = "";
			for (int i = 0;i<encrypttaxid.length;i++) {
				taxid = taxid + PubFunc.decrypt(encrypttaxid[i]);
				if(i<encrypttaxid.length-1) {
					taxid = taxid + ",";
				}
			}
			ITaxTableService taxTableService = new TaxTableServiceImpl(this.frameconn,this.userView);
			//返回导出的excel名称
			String file_name = taxTableService.exportTaxTable(taxid);
			returnData.put("file_name", file_name);
		} catch (GeneralException e) {
			e.printStackTrace();
    		returnCode = "fail";
			returnMsg = e.getErrorDescription();
		}
		this.formHM.put("return_code", returnCode);
		this.formHM.put("return_data", returnData);
		this.formHM.put("return_Msg", returnMsg);
    }
}