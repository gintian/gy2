package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SavePermisViewTrans.java
 * </p>
 * <p>
 * Description:保存审核意见
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-09-03 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SavePermisViewTrans extends IBusiness {
    public void execute() throws GeneralException {

        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String stuids = (String) hm.get("stuids");
        String[] stuArray = stuids.split("@");
        String permisView = (String) this.getFormHM().get("permisView");
        String type = (String) this.getFormHM().get("type");
        String classid = (String) this.getFormHM().get("classid");
        this.getFormHM().remove("classid");

        int n = 0;
        String id = "";
        ArrayList list = new ArrayList();

        for (int i = 0; i < stuArray.length; i++) {
            if (n > 0)
                id += ",";
            id += "'" + PubFunc.decrypt(SafeCode.decode(stuArray[i])) + "'";
            n++;

            if (n == 1000) {
                list.add(id);
                id = "";
                n = 0;
            }
        }

        if (id != null && id.length() > 0) {
            list.add(id);
        }

        String flag = "07"; // 驳回
        if ("pz".equals(type))// 批准
            flag = "03";
        
        ArrayList sqlList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            StringBuffer sql = new StringBuffer();
            sql.append("update r40 set r4013='");
            sql.append(flag);
            sql.append("',r4015='");
            sql.append(permisView);
            sql.append("' where r4001 in (");
            sql.append(list.get(i));
            sql.append(")");
            sql.append(" and r4005='" + classid + "'");
            sqlList.add(sql.toString());
        }
        
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            dao.batchUpdate(sqlList);

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }
}
