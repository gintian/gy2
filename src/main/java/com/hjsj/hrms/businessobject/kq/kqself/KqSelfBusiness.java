package com.hjsj.hrms.businessobject.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;

public class KqSelfBusiness {
    private Connection conn;

    private KqSelfBusiness() {
    }

    public KqSelfBusiness(UserView userView, Connection conn) {
        this.conn = conn;
    }

    public boolean getIsTemplate(String template_type) {
        String name = "";
        if (template_type != null && "q11".equalsIgnoreCase(template_type)) {
            name = "Kq_template_Q11";
        } else if (template_type != null && "q13".equalsIgnoreCase(template_type)) {
            name = "Kq_template_Q13";
        } else if (template_type != null && "q15".equalsIgnoreCase(template_type)) {
            name = "Kq_template_Q15";
        } else {
            return false;
        }

        boolean isCorrect = false;
        RowSet rs = null;
        try {
            String sql = "select description from kq_parameter where b0110='UN' and name='" + name + "'";
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            isCorrect = rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
    }
}
