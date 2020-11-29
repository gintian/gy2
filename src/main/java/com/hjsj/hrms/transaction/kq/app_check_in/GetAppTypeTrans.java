package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetAppTypeTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String isshow = "0";
            String appType = (String) this.getFormHM().get("app_type");

            if (appType != null && !"".equals(appType.trim())) {
                if (KqParam.getInstance().isHoliday(this.frameconn, userView, appType))
                    isshow = "1";
            }
            this.getFormHM().put("isshow", isshow);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
