package com.hjsj.hrms.module.selfservice.workflow.transction;

import com.hjsj.hrms.businessobject.general.template.workflow.TemplateSelfServicePlatformBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;


public class GetCodeTreeTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        Map returnData = new HashMap();
        TemplateSelfServicePlatformBo templateSelfServicePlatformBo = new TemplateSelfServicePlatformBo(this.frameconn, this.userView);
        Map map_result = templateSelfServicePlatformBo.getCodeTree(this.formHM);
        String return_code = (String) map_result.get("return_code");
        String return_msg = (String) map_result.get("return_msg");
        map_result.remove("return_code");
        map_result.remove("return_msg");
        returnData.put("code", map_result);
        this.getFormHM().put("return_code", return_code);
        this.getFormHM().put("return_msg", return_msg);
        this.getFormHM().put("return_data", returnData);
    }

}
