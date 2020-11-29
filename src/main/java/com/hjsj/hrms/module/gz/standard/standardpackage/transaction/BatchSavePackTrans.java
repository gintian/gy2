package com.hjsj.hrms.module.gz.standard.standardpackage.transaction;

import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.impl.StandardPackageServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title BatchSavePackTrans
 * @Description 批量保存历史沿革数据修改
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/12/3
 * @Version 1.0.0
 */
public class BatchSavePackTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        Map return_data = new HashMap();
        try {
            IStandardPackageService standardPackage = new StandardPackageServiceImpl(this.frameconn, this.userView);
            ArrayList updateInfor = (ArrayList) this.getFormHM().get("updateInfor");
            standardPackage.batchSaveStandPackInfor(updateInfor);
            //获取历史沿革列表功能权限map
            Map funcPrivMap = standardPackage.getFuncPrivMap();
            return_data.put("funcPrivMap", funcPrivMap);
        } catch (GeneralException e) {
            e.printStackTrace();
            return_code = "fail";
            return_msg = e.getErrorDescription();
        }  finally {
            this.getFormHM().put("return_code", return_code);
            this.getFormHM().put("return_msg", return_msg);
            this.getFormHM().put("return_data", return_data);
        }
    }
}
