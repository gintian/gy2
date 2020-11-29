package com.hjsj.hrms.transaction.kq.options.imports;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Apr 28, 2010:9:30:08 AM
 * </p>
 * 
 * @author wangyao
 * @version 1.0
 * 
 */
public class ImportAnalysisTrans extends IBusiness {

    public void execute() throws GeneralException {
        String items = (String) this.getFormHM().get("subset");
        String temidtype = (String) this.getFormHM().get("temidtype");
        ArrayList listitemid = getitemidList(temidtype, items); // 指标
        this.getFormHM().put("itemidlist", listitemid);
        ArrayList listbegindate = getitemidList("D", items); // 开始时间
        this.getFormHM().put("listdate", listbegindate);
    }

    /**
     * 指标类型
     * 
     * @param type
     * @param fieldsetid
     * @return
     */
    public ArrayList getitemidList(String type, String fieldsetid) {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        sql.append("select itemid,itemdesc from fielditem");
        sql.append(" where UPPER(fieldsetid)='" + fieldsetid.toUpperCase());
        sql.append("' and itemtype='" + type + "' and useflag='1'");
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                CommonData dataobj = new CommonData(rs.getString("itemid"), rs.getString("itemdesc"));
                list.add(dataobj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return list;
    }
}
