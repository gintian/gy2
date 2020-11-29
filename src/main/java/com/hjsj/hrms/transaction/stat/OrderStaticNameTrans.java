package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.interfaces.sys.IResourceConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 统计条件调整
 * <p>Title:OrderStaticItemTrans.java</p>
 * <p>Description>:OrderStaticItemTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 28, 2010 9:21:41 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class OrderStaticNameTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    
            String infokind = (String) hm.get("infor_Flag");
            infokind = infokind == null || infokind.length() <= 0 ? "1" : infokind;
    
            //zxj 20150625 常用统计编号，如不为空或-1，则是对统计项进行排序
            String statid = (String) hm.get("statid");
    
            ArrayList list = null;
            ContentDAO dao = new ContentDAO(this.getFrameconn());
    
            if (statid == null || "".equals(statid) || "-1".equals(statid))
                list = getSnameList(dao, infokind);
            else
                list = getLegendList(dao, statid);
    
            this.getFormHM().put("infor_Flag", infokind);
            this.getFormHM().put("orderlist", list);
            this.getFormHM().put("statid", statid);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private ArrayList getSnameList(ContentDAO dao, String infokind) {
        String sql = "select id,name from sname where infokind='" + infokind + "' order by snorder";
        ArrayList list = new ArrayList();
        try {
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                if ((this.userView.isHaveResource(IResourceConstant.STATICS, this.frowset.getString("id")))
                        || "su".equalsIgnoreCase(this.userView.getUserName())) {
                    CommonData da = new CommonData();
                    da.setDataName(this.frowset.getString("name"));
                    da.setDataValue(this.frowset.getString("id"));
                    list.add(da);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private ArrayList getLegendList(ContentDAO dao, String statid) {
        String sql = "select norder, legend from slegend where id='" + statid + "' order by norder";
        ArrayList list = new ArrayList();
        try {
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                CommonData da = new CommonData();
                da.setDataName(this.frowset.getString("legend"));
                da.setDataValue(this.frowset.getString("nOrder"));
                list.add(da);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
