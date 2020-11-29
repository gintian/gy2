package com.hjsj.hrms.module.system.filepathsetting.transaction;

import com.hjsj.hrms.module.system.filepathsetting.businessobject.FilePathSettingService;
import com.hjsj.hrms.module.system.filepathsetting.businessobject.impl.FilePathSettingServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

import java.sql.SQLException;

/**
 * 查询文件存储位置
 * @author zhangh
 */
public class SearchFilePathSetting extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        FilePathSettingService settingService = new FilePathSettingServiceImpl(this.getFrameconn());
        //查询文件存储位置
        String params = null;
        try {
            params = settingService.getFilePathSetting();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JSONObject returnStr = new JSONObject();
        returnStr.put("return_data", params);
        returnStr.put("return_msg", "");
        returnStr.put("return_code", "success");
        this.getFormHM().put("returnStr", returnStr);
    }
}
