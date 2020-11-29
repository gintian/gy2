package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class MakeupNetSignInTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String singin_flag = "0";
            String card_causation = KqParam.getInstance().getCardCausation();
            if (card_causation == null || card_causation.length() <= 0) {
                card_causation = "";
            }
            this.getFormHM().put("card_causation", card_causation);////补刷卡原因代码项
            NetSignIn netSignIn = new NetSignIn();
            String makeup_date = netSignIn.getWork_date();
            this.getFormHM().put("singin_flag", singin_flag);
            this.getFormHM().put("oper_cause", "");
            this.getFormHM().put("makeup_date", makeup_date);
            this.getFormHM().put("inout_flag", "0");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
