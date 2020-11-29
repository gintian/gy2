package com.hjsj.hrms.module.gz.standard.standardpackage.transaction;

import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.impl.StandardPackageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @Title DeletePackageTrans
 * @Description 删除历史沿革
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/12/3
 * @Version 1.0.0
 */
public class DeletePackageTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        HashMap return_data = new HashMap();
        try {
            IStandardPackageService standardPackage = new StandardPackageServiceImpl(this.frameconn, this.userView);
            String pkg_id = PubFunc.decrypt((String)this.getFormHM().get("pkg_id"));
            String flag = standardPackage.deletePackageInfor(pkg_id);
            return_data.put("flag", flag);
        } catch (GeneralException e) {
            e.printStackTrace();
            return_code = "fail";
            return_msg = e.getErrorDescription();
        } finally {
            this.getFormHM().put("return_code", return_code);
            this.getFormHM().put("return_msg", return_msg);
            this.getFormHM().put("return_data", return_data);
        }       
    }
}
