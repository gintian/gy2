package com.hjsj.hrms.module.gz.standard.standardpackage.transaction;

import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.impl.StandardPackageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title ExportPackageTrans
 * @Description 导出历史沿革
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/12/3
 * @Version 1.0.0
 */
public class ExportPackageTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String fileName = "";
        String return_code = "success";
        String return_msg = "";
        HashMap return_data=new HashMap();
        HashMap returnStrMap = new HashMap();
        try {
            //选中的要导出的标准表id
            String standardIDs = (String) this.getFormHM().get("stand_ids");
            //历史沿革id
            String pkg_id = (String) this.getFormHM().get("pkg_id");
            pkg_id = PubFunc.decrypt(pkg_id);
            String outFileName = (String) this.getFormHM().get("outfilename");
            IStandardPackageService iStandardPackageService = new StandardPackageServiceImpl(this.frameconn, this.userView);
            Map outNameMap = iStandardPackageService.exportPackageStandStruct(pkg_id, standardIDs,outFileName);
            fileName = PubFunc.encrypt(outNameMap.get("outName").toString());
            return_data.put("fileName", fileName);
        } catch (GeneralException e) {
            e.printStackTrace();
            return_code = "fail";
            return_msg = e.getErrorDescription();
        } finally {
            returnStrMap.put("return_code", return_code);
            returnStrMap.put("return_msg", return_msg);
            returnStrMap.put("return_data", return_data);
            this.formHM.put("returnStr", returnStrMap);
        }
    }
}
