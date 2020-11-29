package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class TransactGroupTrans extends IBusiness implements KqClassArrayConstant {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String save_flag = (String) hm.get("save_flag");
        this.getFormHM().put("save_flag", save_flag);
        if (save_flag == null || save_flag.length() <= 0)
            return;

        if ("update".equalsIgnoreCase(save_flag)) {
            String group_id = (String) hm.get("group_id");
            String sql = "select name,org_id from " + kq_shift_group_table + " where " + kq_shift_group_Id + "='" + group_id + "'";
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            String name = "";
            String org_id = "";
            String org_name = "";
            try {
                this.frowset = dao.search(sql);
                if (this.frowset.next()) {
                    name = this.frowset.getString("name");
                    org_id = this.frowset.getString("org_id");
                    if (org_id != null && org_id.length()>2) {
                        org_name = AdminCode.getCodeName(org_id.substring(0,2), org_id.substring(2));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.getFormHM().put("name", name);
            this.getFormHM().put("org_id", org_id);
            this.getFormHM().put("org_name", org_name);
        }
    }

}
