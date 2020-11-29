package com.hjsj.hrms.module.gz.standard.standardpackage.transaction;

import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.impl.StandardPackageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @Title InitStandPackTrans
 * @Description 单个历史沿革初始化
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/12/3
 * @Version 1.0.0
 */
public class InitStandPackTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        HashMap return_data = new HashMap();
        
        try {
            IStandardPackageService standardPackage = new StandardPackageServiceImpl(this.frameconn, this.userView);
            String pkg_id = PubFunc.decrypt((String)this.getFormHM().get("pkg_id"));
            String init_type = (String) this.getFormHM().get("init_type");
            return_data = (HashMap)standardPackage.getStandPackageInfor(pkg_id, init_type);
        } catch (GeneralException e) {
            e.printStackTrace();
            return_code = "fail";
            return_msg = e.getErrorDescription();
        } finally {
            this.formHM.put("return_code", return_code);
            this.formHM.put("return_msg", return_msg);
            this.formHM.put("return_data", return_data);
        }
    }
}
