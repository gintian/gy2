package com.hjsj.hrms.module.system.filepathsetting.transaction;

import com.hjsj.hrms.module.system.filepathsetting.businessobject.FileMoveService;
import com.hjsj.hrms.module.system.filepathsetting.businessobject.impl.FileMoveServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;

/**
 * 文件迁移
 */
public class FileMoveTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        FileMoveService service = new FileMoveServiceImpl(this.getUserView());
        String type = (String)this.getFormHM().get("type");
        //查询迁移进度信息
        if("queryMove".equalsIgnoreCase(type)){
            JSONObject returnStr = new JSONObject();
            returnStr.put("return_data", service.queryMoveProgress());
            returnStr.put("return_msg", "");
            returnStr.put("return_code", "success");
            this.getFormHM().put("returnStr", returnStr);
        //查询还原进度信息
        }else if("queryRecovery".equalsIgnoreCase(type)){
            JSONObject returnStr = new JSONObject();
            returnStr.put("return_data", service.queryRecoveryProgress());
            returnStr.put("return_msg", "");
            returnStr.put("return_code", "success");
            this.getFormHM().put("returnStr", returnStr);

        } else if("move".equalsIgnoreCase(type)){
            //文件迁移
            service.fileMove();
        }else if("recovery".equalsIgnoreCase(type)){
            try {
                //文件还原
                service.fileRecovery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
