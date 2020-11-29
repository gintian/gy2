package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewExamine;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:DelInterviewEvaluTrans.java
 * </p>
 * <p>
 * Description:删除面试评价
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-05-14 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class DelInterviewEvaluTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String a0100 = (String) hm.get("a0100");
            this.getFormHM().put("a0100", a0100);
            a0100 = com.hjsj.hrms.utils.PubFunc.decrypt(a0100);

            String i9999s = (String) hm.get("i9999s");
            String[] i9999Array = i9999s.split("@");
            
            String dbName = (String) this.getFormHM().get("dbName");
            dbName = com.hjsj.hrms.utils.PubFunc.decrypt(dbName);
            
            String subset = (String) this.getFormHM().get("examineNeedRecordSet");
            subset = com.hjsj.hrms.utils.PubFunc.decrypt(subset);

            StringBuffer sql = new StringBuffer();
            sql.append("delete from " + dbName + subset);
            sql.append(" where a0100=? and i9999 in (");
            for (int i = 0; i < i9999Array.length; i++) {
                sql.append(i9999Array[i]);
                if (i < i9999Array.length-1)
                    sql.append(",");
            }
            sql.append(")");
            
            ArrayList params = new ArrayList();
            params.add(a0100);

            ContentDAO dao = new ContentDAO(this.getFrameconn());
            dao.delete(sql.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
