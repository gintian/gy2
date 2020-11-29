package com.hjsj.hrms.module.gz.templateset.taxtable.transaction;

import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.ITaxTableService;
import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.impl.TaxTableServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: 保存税率表方案交易类
 * @Author manjg
 * @Date 2019/12/3 14:38
 * @Version V1.0
 **/
public class SaveTaxTableTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
    	List<DynaBean> params = (ArrayList<DynaBean>)this.getFormHM().get("params");
    	HashMap resultMap = new HashMap();
    	try {
    		ITaxTableService tts = new TaxTableServiceImpl(this.frameconn,this.userView);
    		tts.saveTaxTable(params);
    		resultMap.put("return_code", "success");
			this.formHM.put("returnStr", resultMap);
		} catch (GeneralException e) {
			// TODO: handle exception
			e.printStackTrace();
			resultMap.put("return_code", "fail");
			resultMap.put("return_msg", e.getErrorDescription());
			this.formHM.put("returnStr", resultMap);
		}
    }
}