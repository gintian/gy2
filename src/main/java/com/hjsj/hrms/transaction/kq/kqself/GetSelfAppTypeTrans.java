package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.utils.FuncVersion;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetSelfAppTypeTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String sels = (String) this.getFormHM().get("sels");
            String isshow = "0";

            FuncVersion fv = new FuncVersion(this.userView);
            
            //专业版才有调休假功能
            if (fv.haveKqLeaveTypeUsedOverTimeFunc()) {
                DbWizard dbWizard = new DbWizard(this.getFrameconn());
                if (!dbWizard.isExistTable("Q33", false)) {
                    isshow = "0";
                } else {
                    //没设置调休假参数的，不出现调休加班功能
                    if (sels != null && !"".equals(sels.trim())) {
                        String content = KqParam.getInstance().getLEAVETIME_TYPE_USED_OVERTIME();
                        if (("," + content + ",").contains("," + sels + ","))
                            isshow = "1";
                    }
                }
            }
            this.getFormHM().put("isshow", isshow);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

}
