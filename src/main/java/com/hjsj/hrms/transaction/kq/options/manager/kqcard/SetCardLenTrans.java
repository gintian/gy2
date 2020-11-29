package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SetCardLenTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            KqCardLength kqCardLength = new KqCardLength(this.getFrameconn());
            int int_id_len = kqCardLength.getCardLend();
            this.getFormHM().put("id_len", int_id_len + "");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
