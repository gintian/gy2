package com.hjsj.hrms.module.system.filepathsetting.transaction;

import com.hjsj.hrms.module.system.filepathsetting.businessobject.FilePathSettingService;
import com.hjsj.hrms.module.system.filepathsetting.businessobject.impl.FilePathSettingServiceImpl;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

/**
 * 保存文件存储位置
 * @author zhangh
 */
public class SaveFilePathSetting extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        //文件存储位置设置
        String  params = (String)this.getFormHM().get("params");
        FilePathSettingService settingService = new FilePathSettingServiceImpl(this.getFrameconn());
        boolean flag = false;
        String errorMsg = "";
        try {
            flag = settingService.saveFilePathSetting(params);
        } catch (Exception e) {
            flag = false;
            if(e.getCause()!=null){
                if(e.getCause().getMessage().contains("login")){
                    errorMsg = ResourceFactory.getProperty("FilePathSetting.user.error");

                }else if(e.getCause().getMessage().contains("timed")){
                    errorMsg = ResourceFactory.getProperty("FilePathSetting.timeout.error");
                } else {
                    errorMsg = e.getMessage();
                }
            }else{
                if(e.toString().contains("ClassNotFoundException")){
                    errorMsg = ResourceFactory.getProperty("FilePathSetting.class.error");
                }else if(e.toString().contains("not find")){
                    errorMsg = ResourceFactory.getProperty("FilePathSetting.type.error");
                }else{
                    errorMsg = e.getMessage();
                }
            }
            e.printStackTrace();
        }
        JSONObject returnStr = new JSONObject();
        //是否保存成功
        returnStr.put("return_code", flag==true?"success":"fail");
        returnStr.put("return_msg", errorMsg);
        this.getFormHM().put("returnStr", returnStr);
    }
}
