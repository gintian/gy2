package com.hjsj.hrms.module.gz.templateset.taxtable.transaction;

import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.ITaxTableService;
import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.impl.TaxTableServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 初始化税率表数据交易类
 * @Author manjg
 * @Date 2019/12/3 14:37
 * @Version V1.0
 **/
public class InitTaxTableTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
    	 String tableConfig = "";
    	 Boolean editable = false;
         Map<String, Object> returnStr = new HashMap<String, Object>();
         Map<String, Object> returnData = new HashMap<String, Object>();
         try {
             ITaxTableService tts = new TaxTableServiceImpl(this.frameconn,this.userView);
             tableConfig = tts.getTaxTableConfig();
             editable = tts.isHaveOperationPriv(ITaxTableService.EDIT_FUNC_ID);
             returnStr.put("return_code","success");
             returnData.put("getTableConfig", tableConfig);
             returnData.put("editable", editable);
             returnStr.put("return_data",returnData);
             this.formHM.put("returnStr", returnStr);
         } catch (GeneralException e) {
             returnStr.put("return_code","fail");
             returnStr.put("return_msg",e.getErrorDescription());
             this.formHM.put("returnStr", returnStr);
             e.printStackTrace();
         }
     }
}