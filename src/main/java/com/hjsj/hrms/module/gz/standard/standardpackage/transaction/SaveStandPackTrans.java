package com.hjsj.hrms.module.gz.standard.standardpackage.transaction;

import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.impl.StandardPackageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title SaveStandPackTrans
 * @Description 保存历史沿革数据
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/12/3
 * @Version 1.0.0
 */
public class SaveStandPackTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        String return_msg = "";
        HashMap return_data = new HashMap();
        try {
            Map funcPrivMap = new HashMap();
            HashMap<String,String> standPackageInfor = new HashMap<String,String>();
            IStandardPackageService standardPackageService = new StandardPackageServiceImpl(this.frameconn, this.userView);
            String pkg_id = PubFunc.decrypt((String)this.getFormHM().get("pkg_id"));
            String name = (String) this.getFormHM().get("name");
            name = PubFunc.hireKeyWord_filter(name);
            String start_date = (String) this.getFormHM().get("start_date");
            String status = (String) this.getFormHM().get("status");
            String owner_org = (String) this.getFormHM().get("owner_org");
            List ref_standIds =(ArrayList)this.getFormHM().get("ref_standIds");
            standPackageInfor.put("pkg_id",pkg_id);
            standPackageInfor.put("name",name);
            standPackageInfor.put("start_date",start_date);
            standPackageInfor.put("status",status);
            standPackageInfor.put("b0110",owner_org);
            standardPackageService.saveStandPackageInfor(standPackageInfor,ref_standIds);
            funcPrivMap = standardPackageService.getFuncPrivMap();
            return_data.put("funcPrivMap",funcPrivMap);
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
