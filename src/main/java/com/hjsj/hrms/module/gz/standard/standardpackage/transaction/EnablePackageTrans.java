package com.hjsj.hrms.module.gz.standard.standardpackage.transaction;

import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.impl.StandardPackageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @Title EnablePackageTrans
 * @Description 启用历史沿革
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/12/3
 * @Version 1.0.0
 */
public class EnablePackageTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        try {
            IStandardPackageService standardPackage = new StandardPackageServiceImpl(this.frameconn, this.userView);
            String pkg_id = (String) this.getFormHM().get("pkg_id");
            pkg_id = PubFunc.decrypt(pkg_id);
            standardPackage.enablePackage(pkg_id,"");
            this.getFormHM().put("return_code", "success");
        } catch (GeneralException e) {
            e.printStackTrace();
            this.getFormHM().put("return_code", "fail");
            this.getFormHM().put("return_msg",e.getErrorDescription());
        }        
    }
}
