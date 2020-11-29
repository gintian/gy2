package com.hjsj.hrms.transaction.hire.innerEmployNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

public class SaveAttachTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            FormFile form_file = (FormFile) this.getFormHM().get("attachFile");
            
            String a0100 = PubFunc.decrypt((String) this.getFormHM().get("zpkA0100"));
            RecordVo vo2 = ConstantParamter.getConstantVo("ZP_DBNAME");
            String dbname = vo2.getString("str_value");
            String i9999 = PubFunc.decrypt((String) this.getFormHM().get("i9999"));
            String type = (String) this.getFormHM().get("type");
            /**上传附件是这个参数绕过了过滤器,用过滤器处理一下**/
            type = PubFunc.hireKeyWord_filter(type);
            if("0".equals(type)){
                if (!FileTypeUtil.isFileTypeEqual(form_file)) {
                    throw new GeneralException(ResourceFactory.getProperty("error.fileuploaderror"));
                }
            }
            String fileName = (String) this.getFormHM().get("fileName");
            fileName = PubFunc.hireKeyWord_filter(fileName);
            String userName = this.userView.getUserName();

            EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(this.getFrameconn(), "1");
            RecordVo vo = new RecordVo(dbname + "a00");
            employNetPortalBo.insertDAO(vo, form_file, a0100, dbname, userName, "N", i9999, type, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

}
