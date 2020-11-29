package com.hjsj.hrms.module.gz.standard.standardpackage.transaction;

import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.impl.StandardPackageServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title InitStandPackageListTrans
 * @Description 历史沿革列表初始化
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/12/3
 * @Version 1.0.0
 */

public class InitStandPackageListTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
    	try {
    		IStandardPackageService standardPackage = new StandardPackageServiceImpl(this.frameconn, this.userView);
    		String tableConfig = standardPackage.getStandardPackageTableConfig();
    		//获取历史沿革列表功能权限map
    		Map funcPrivMap = standardPackage.getFuncPrivMap();

    		Map return_data = new HashMap();
            return_data.put("tableConfig", tableConfig);
            return_data.put("funcPrivMap", funcPrivMap);

            this.formHM.put("return_code", "success");
            this.formHM.put("return_data", return_data);
        }catch (GeneralException e) {
        	e.printStackTrace();
            this.formHM.put("return_code", "fail");
            this.formHM.put("return_msg", e.getErrorDescription());
        }
    }
}
