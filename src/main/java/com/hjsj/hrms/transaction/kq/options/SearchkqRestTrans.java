/*
 * Created on 2006-12-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchkqRestTrans extends IBusiness {

    public void execute() throws GeneralException {

        try {
            ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
            String b0110 = managePrivCode.getUNB0110();

            ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, this.getFrameconn());
            String rest_date = restList.get(0).toString();
            rest_date = KQRestOper.getRestStrTurn(rest_date);
            
            String[] tt = rest_date.split(",");
            this.getFormHM().put("rest_weeks", tt);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
