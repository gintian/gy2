package com.hjsj.hrms.transaction.kq.register.empchange;

import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class LeaveEmpChangeTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
        this.getFormHM().put("changestatus", "LeaveN");
        String TabName = (String) this.getFormHM().get("TabName");
        
        new DbWizard(this.getFrameconn()).dropTable(TabName);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
