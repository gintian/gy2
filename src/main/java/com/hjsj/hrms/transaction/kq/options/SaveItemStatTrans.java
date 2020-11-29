package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SaveItemStatTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String flag = (String) this.getFormHM().get("flag");
            if (!"1".equals(flag))
                return;
            
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String items = (String) hm.get("akq_item");
            String s_expr = (String) this.getFormHM().get("s_expr");
            String mann = (String) this.getFormHM().get("manner");

            StringBuffer st = new StringBuffer();
            st.append("update kq_item set s_expr='");
            st.append((s_expr + mann).toString());
            st.append("' where item_id='");
            st.append(items.toString());
            st.append("'");
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            dao.update(st.toString());

        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }

    }

}
