package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 统计或批量删除子集记录交易类
 * @Title:        CountOrDeleteSubsetDataTrans.java
 * @Description:  统计将要删除的子集记录或批量删除符合条件的子集记录
 * @Company:      hjsj     
 * @Create time:  2018年5月29日 上午10:54:17
 * @author        chenxg
 * @version       1.0
 */
public class CountOrDeleteSubsetDataTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        RowSet rs = null;
        try {
            String type = (String) this.getFormHM().get("type");
            String setname = (String) this.getFormHM().get("setname");
            String dbname = (String) this.getFormHM().get("dbname");
            String dataRangeType = (String) this.getFormHM().get("dataRangeType");
            String subsetDataType = (String) this.getFormHM().get("subsetDataType");
            String selectId = (String) this.getFormHM().get("selectId");
            String whereValue = (String) this.getFormHM().get("whereStr");
            InfoUtils info = new InfoUtils();
            String whereStr = info.getSubsetDataWhere(this.userView, dataRangeType, subsetDataType,
                    dbname, setname, selectId, whereValue, this.frameconn);
            
            String tablename = dbname + setname;
            if("count".equalsIgnoreCase(type)) {
                //统计将要删除的子集记录
                int count = 0;
                StringBuffer sqlStr = new StringBuffer();
                sqlStr.append("select count(1) countid from ");
                sqlStr.append(tablename);
                sqlStr.append(" where 1=1");
                sqlStr.append(whereStr);
                
                ContentDAO dao = new ContentDAO(this.frameconn);
                rs = dao.search(sqlStr.toString());
                if(rs.next())
                    count = rs.getInt(1);
                
                this.getFormHM().put("count", count + "");
                
            } else if("delete".equalsIgnoreCase(type)) {
                //批量删除子集记录
                StringBuffer sqlStr = new StringBuffer();
                sqlStr.append("delete from ");
                sqlStr.append(tablename);
                sqlStr.append(" where 1=1");
                sqlStr.append(whereStr);
                
                ContentDAO dao = new ContentDAO(this.frameconn);
                dao.delete(sqlStr.toString(), new ArrayList<String>());
            }
            
            this.getFormHM().put("success", "true");
        } catch (Exception e) {
            e.printStackTrace();
            this.getFormHM().put("success", e.getMessage());
        } finally {
            PubFunc.closeResource(rs);
        }

    }
}
