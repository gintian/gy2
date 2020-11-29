package com.hjsj.hrms.transaction.kq.options.struts;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 28, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class DeleteTemplateTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String t_name = (String) hm.get("tname");
            if (t_name != null && "q11".equalsIgnoreCase(t_name))
                t_name = "Kq_template_Q11";
            else if (t_name != null && "q13".equalsIgnoreCase(t_name))
                t_name = "Kq_template_Q13";
            else if (t_name != null && "q15".equalsIgnoreCase(t_name))
                t_name = "Kq_template_Q15";
            else
                return;

            StringBuffer sql = new StringBuffer();
            sql.append("select description,content from kq_parameter where b0110='UN' and name='" + t_name + "'");
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            try {
                this.frowset = dao.search(sql.toString());
                String desc = "";
                String path = "";
                if (this.frowset.next()) {
                    desc = this.frowset.getString("description");
                    path = this.frowset.getString("content");
                    File file = new File(SafeCode.decode(path + "\\" + desc));
                    if (file.exists())
                        file.delete();
                }
                
                String delete = "delete from kq_parameter where b0110='UN' and name='" + t_name + "'";
                dao.delete(delete, new ArrayList());
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.getFormHM().put("tab_name", "tab8");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
