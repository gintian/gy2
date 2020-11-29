package com.hjsj.hrms.module.recruitment.util;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/****
 * 反馈信息结果处理类
 * <p>
 * Title: FeedBackBo
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2016-1-28 下午02:50:37
 * </p>
 * 
 * @author xiexd
 * @version 1.0
 */
public class FeedBackBo {

    Connection conn;
    UserView userview;

    public FeedBackBo(Connection conn) {
        this.conn = conn;
    }

    /**
     * 查询反馈信息
     * 
     * @param map
     *            人员信息（主键为 a0100（人员编号）、nbase（应聘人员库）、zp_pos_id（申请的职位编号））
     * @return 反馈信息
     */
    public String queryFeedBack(HashMap map) {

        if (map == null || map.isEmpty())
            return "";

        String feedBack = "";
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT DESCRIPTION ");
            sql.append(" FROM ZP_POS_TACHE");
            sql.append(" WHERE A0100=? ");
            sql.append(" AND NBASE=?");
            sql.append(" AND ZP_POS_ID=?");

            ArrayList<String> valueList = new ArrayList<String>();
            String a0100 = (String) map.get("a0100");
            if (StringUtils.isEmpty(a0100))
                return "";

            valueList.add(a0100);
            valueList.add((String) map.get("nbase"));
            valueList.add((String) map.get("zp_pos_id"));
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString(), valueList);
            if (rs.next())
                feedBack = rs.getString("DESCRIPTION");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return feedBack;
    }

    /**
     * 更新保存反馈信息
     * 
     * @param valueList
     *            需要更新的数据的参数（形式为：ArrayList<ArrayList<String>>，
     *            其中参数的顺序为：DESCRIPTION
     *            （反馈信息）、A0100（人员编号）、NBASE（应聘人员库）、ZP_POS_ID（申请的职位编号））
     */
    public void updateFeedBack(ArrayList<ArrayList<String>> valueList) {
        try {
            updateFeedBackInfo(valueList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除反馈信息，即：将反馈信息保存为空
     * 
     * @param valueList
     *            需要更新的数据的参数（形式为：ArrayList<ArrayList<String>>，
     *            其中参数的顺序为：DESCRIPTION
     *            （反馈信息）、A0100（人员编号）、NBASE（应聘人员库）、ZP_POS_ID（申请的职位编号））
     */
    public void deleteFeedBack(ArrayList<ArrayList<String>> valueList) {
        try {
            updateFeedBackInfo(valueList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新保存反馈信息
     * 
     * @param valueList
     *            需要更新的数据的参数（形式为：ArrayList<ArrayList<String>>，
     *            其中参数的顺序为：DESCRIPTION
     *            （反馈信息）、A0100（人员编号）、NBASE（应聘人员库）、ZP_POS_ID（申请的职位编号））
     */
    private void updateFeedBackInfo(ArrayList<ArrayList<String>> valueList) {
        if (valueList == null || valueList.size() < 1)
            return;

        try {
            StringBuffer sql = new StringBuffer();
            sql.append("UPDATE ZP_POS_TACHE  ");
            sql.append(" SET DESCRIPTION=?");
            sql.append(" WHERE A0100=? ");
            sql.append(" AND NBASE=?");
            sql.append(" AND ZP_POS_ID=?");

            ContentDAO dao = new ContentDAO(conn);
            dao.batchUpdate(sql.toString(), valueList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
