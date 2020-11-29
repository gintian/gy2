package com.hjsj.hrms.module.workplan.worklog.transaction;

import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/**
 * 展示多层级部门
 * @Title:        ShowUplevelCodeTrans.java
 * @Description:  多层级部门显示
 * @Company:      hjsj     
 * @Create time:  2017-3-21 下午04:17:25
 * @author        chenxg
 * @version       1.0
 */
public class ShowUplevelCodeTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String e0122 = (String) this.getFormHM().get("e0122");
        String codeName = "";
        try {
            if(StringUtils.isEmpty(e0122))
                this.getFormHM().put("codeDesc", codeName);
            else {
                String upLevel = (String) this.getFormHM().get("uplevel");
                upLevel = StringUtils.isEmpty(upLevel) ? "0" : upLevel;
                
                String codeId = e0122.split("`")[0];
                codeName = e0122.split("`")[1];
                if ("0".equalsIgnoreCase(upLevel)) {
                    this.getFormHM().put("codeDesc", AdminCode.getCodeName("UM", codeId));
                } else {
                    CodeItem item = AdminCode.getCode("UM", codeId, Integer.parseInt(upLevel));
                    this.getFormHM().put("codeDesc", item.getCodename());
                }
            }                
        } catch (Exception e) {
            e.printStackTrace();
            this.getFormHM().put("codeDesc", codeName);
        }

    }

}
