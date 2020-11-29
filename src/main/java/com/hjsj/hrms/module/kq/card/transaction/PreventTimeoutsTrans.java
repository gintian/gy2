package com.hjsj.hrms.module.kq.card.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

public class PreventTimeoutsTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search("select 1 from usra01 where 1=2");
            
            if("true".equalsIgnoreCase((String) this.userView.getHm().get("finish"))) {
                this.getFormHM().put("finish", "true");
                this.userView.getHm().remove("finish");
            } else {
                this.getFormHM().put("finish", "false");
            }
            
            String errorMsg = (String) this.userView.getHm().get("errorMsg");
            if(StringUtils.isEmpty(errorMsg))
                errorMsg = "true";
            
            this.getFormHM().put("errorMsg", errorMsg);
            this.userView.getHm().remove("errorMsg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
