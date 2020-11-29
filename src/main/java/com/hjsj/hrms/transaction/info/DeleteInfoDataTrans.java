/*
 * Created on 2005-7-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteInfoDataTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            ArrayList selfinfolist = (ArrayList) this.getFormHM().get("selectedinfolist");
            String userbase = (String) this.getFormHM().get("userbase");
            if (selfinfolist == null || selfinfolist.size() == 0)
                return;

            InfoUtils infoutils = new InfoUtils();
            String a0101s = infoutils.deletePersonInfo(this.frameconn, userbase, "DEL", selfinfolist, this.userView);
            this.getFormHM().put("@eventlog", a0101s);
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
    }

}
