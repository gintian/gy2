package com.hjsj.hrms.transaction.train.resource.course.ability;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @Title: TrainAbilityDelTrans.java
 * @Description: 撤销培训课程关联的素质指标
 * @Company: hjsj
 * @Create time: 2014-5-22 下午05:11:19
 * @author: chenxg
 * @version: 6.x
 */
public class TrainAbilityDelTrans extends IBusiness {

    public void execute() throws GeneralException {
        String r5000 = (String) this.getFormHM().get("r5000");
        r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
        String pointids = (String) this.getFormHM().get("ids");
        pointids = PubFunc.keyWord_reback(pointids);
        pointids = pointids.substring(0, pointids.length() - 1);
        String flag = "no";
        String msg = "";
        ContentDAO dao = new ContentDAO(this.frameconn);
        String[] pointid = pointids.split(",");
        ArrayList list = new ArrayList();
        int n = 0;
        String id = "";
        for (int i = 0; i < pointid.length; i++) {
            if (n > 0)
                id += ",";
            id += pointid[i];
            n++;
            if (n == 1000) {
                list.add(id);
                id = "";
                n = 0;
            }

        }

        if (id.length() > 0) {
            list.add(id);
        }

        ArrayList sqlList = new ArrayList();
        try {
            for (int i = 0; i < list.size(); i++) {
                String pid = (String) list.get(i);
                String sql = "delete from per_point_course where r5000='" + r5000 + "' and point_id in (" + pid + ")";
                sqlList.add(sql);

                this.frowset = dao.search("select point_id,pointname from per_point where point_id in (" + pid + ")");
                while (this.frowset.next()) {
                    // 判断用户权限
                    if (!userView.isSuper_admin() && !"1".equals(userView.getGroupId())) {
                        if (!userView.isRWHaveResource(IResourceConstant.KH_FIELD, this.frowset.getString("point_id"))
                                && !userView.isRWHaveResource(IResourceConstant.KH_FIELD, this.frowset.getString("point_id") + "R")) {
                            msg += "[" + this.frowset.getString("pointname") + "]，";
                        }
                    }
                }

                if (msg != null && msg.length() > 0) {
                    msg = ResourceFactory.getProperty("train.course.ability.outview1") + "\n\n" + msg.substring(0, msg.length() - 1);
                    msg += "\n\n" + ResourceFactory.getProperty("train.course.ability.outview2");
                    throw GeneralExceptionHandler.Handle(new Exception(msg));
                }
            }
            int[] i = dao.batchUpdate(sqlList);
            if (i.length > 0)
                flag = "yes";
            this.getFormHM().put("flag", flag);
        } catch (SQLException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
