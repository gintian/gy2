package com.hjsj.hrms.module.selfservice.workflow.transction;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplateSelfServicePlatformBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * 导出word和pdf交易类
 */
public class exportTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_msg = "";
        String return_code = "success";
        try {
            TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.frameconn, this.userView);
            Map result = templateSelfServicePlatformBo.outPdf(this.getFormHM());
            return_code = (String) result.get("return_code");
            if (StringUtils.equalsIgnoreCase(return_code, "error")) {
                return_msg = (String) result.get("return_msg");
            } else {
                this.getFormHM().put("fileId", result.get("path"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.getFormHM().put("return_code", return_code);
        this.getFormHM().put("return_msg", return_msg);
    }
}
