package com.hjsj.hrms.module.gz.standard.standardpackage.transaction;

import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.IStandardPackageService;
import com.hjsj.hrms.module.gz.standard.standardpackage.businessobject.impl.StandardPackageServiceImpl;
import com.hjsj.hrms.module.gz.standard.utils.DownLoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title ImportPackageTrans
 * @Description 导入历史沿革
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/12/3
 * @Version 1.0.0
 */
public class ImportPackageTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        ArrayList<Object> msgList = new ArrayList<Object>();//存放错误信息
        String return_code = "success";
        String return_msg = "";
        String logFileName =this.userView.getUserName()+"_"+ResourceFactory.getProperty("standard.standardPackage.importLogName");
        HashMap returnStrMap = new HashMap();
        Map return_data = new HashMap();
        Map importMap = null;
        try {
            IStandardPackageService iStandardPackageService = new StandardPackageServiceImpl(this.frameconn, this.userView);
            String flag = (String) this.formHM.get("flag");
            HashMap fileHM = PubFunc.DynaBean2Map((MorphDynaBean) (this.formHM.get("file")));
            String fileid = (String) fileHM.get("fileid");
            //显示出导入的标准表
            if ("0".equals(flag)) {
                return_data = iStandardPackageService.importPackageStandStruct(fileid);
                if (return_data.size() == 2) {
                    return_code = "fail";
                    returnStrMap.put("return_msg", return_data.get("errorLogName"));
                }
            } else {
                //  1覆盖  2追加
                String stand_id = (String) this.formHM.get("stand_ids");
                importMap = iStandardPackageService.importPackageStandStruct(flag, stand_id, fileid);
                if (importMap.size() != 0) {
                    return_code = "fail";
                    returnStrMap.put("return_msg", importMap.get("errorLogName"));
                }
            }
        } catch (Exception e) {
            return_code = "fail";
            msgList.add(ResourceFactory.getProperty("standard.standardPackage.threeImportError"));
            msgList.add(return_msg);
            DownLoadXml.exportErrorLog(msgList,userView);
            logFileName = PubFunc.encrypt(logFileName);
            returnStrMap.put("return_msg", logFileName);
        } finally {
            returnStrMap.put("return_code", return_code);
            returnStrMap.put("return_data", return_data);
            this.formHM.put("returnStr", returnStrMap);
        }
    }
}
