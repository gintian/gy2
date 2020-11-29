package com.hjsj.hrms.module.recruitment.util.transaction;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

/**
 * 防止导入数据时，后台运行时间过长导致页面链接失效
 * 
 * @Title: PreventInvalidTrans.java
 * @Description: 防止页面链接失效
 * @Company: hjsj
 * @Create time: 2016-7-7 下午12:03:28
 * @author chenxg
 * @version 1.0
 */
public class PreventInvalidTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        String zpflag = (String) this.getFormHM().get("zpflag");
        String deleteInfor = (String) this.getFormHM().get("deleteInfor");
        if (StringUtils.isEmpty(zpflag)) {
            if ("1".equalsIgnoreCase(deleteInfor))
                this.userView.getHm().put("thirdPartyShowInfor", "");

            String showinfor = (String) this.userView.getHm().get("thirdPartyShowInfor");
            this.getFormHM().put("showInfor", showinfor);
        } else if("examScore".equalsIgnoreCase(zpflag)) {
            if ("1".equalsIgnoreCase(deleteInfor))
                this.userView.getHm().put("error_message", "");

            String showinfor = (String) this.userView.getHm().get("error_message");
            String batchId = (String) this.userView.getHm().get("batchId");
            this.getFormHM().put("error_message", showinfor);
            this.getFormHM().put("batchId", batchId);
        }
    }

}
