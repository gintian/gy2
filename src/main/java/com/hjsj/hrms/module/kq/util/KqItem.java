package com.hjsj.hrms.module.kq.util;

import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 考勤规则类
 * @Title:        KqItem.java
 * @Description:  考勤规则类
 * @Company:      hjsj     
 * @Create time:  2017-11-1 上午10:08:22
 * @author        chenxg
 * @version       1.0
 */
public class KqItem {
    private Connection conn;
    
    public KqItem(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * 考勤规则的一个hashmap集
     * @return
     * @throws GeneralException
     */
    public HashMap<String, HashMap<String, String>> getKqItem() throws GeneralException {
        RowSet rs = null;
        String kq_item_sql = "select item_id,item_name,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap<String, HashMap<String, String>> hashM = new HashMap<String, HashMap<String, String>>();
        String fielditemid = "";
        try {
            rs = dao.search(kq_item_sql);
            while (rs.next()) {
                HashMap<String, String> hashm_one = new HashMap<String, String>();
                if (rs.getString("fielditemid") == null || rs.getString("fielditemid").length() <= 0)
                    continue;
                
                ArrayList<FieldItem> fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
                for (int i = 0; i < fielditemlist.size(); i++) {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                    fielditemid = rs.getString("fielditemid");
                    if (fielditemid.equalsIgnoreCase(fielditem.getItemid())) {
                        hashm_one.put("fielditemid", rs.getString("fielditemid"));
                        String has_rest = rs.getString("has_rest") != null
                                && rs.getString("has_rest").length() > 0 ? rs.getString("has_rest") : "0";
                        String has_feast = rs.getString("has_feast") != null
                                && rs.getString("has_feast").length() > 0 ? rs.getString("has_feast") : "0";
                        hashm_one.put("has_rest", has_rest);
                        hashm_one.put("has_feast", has_feast);
                        hashm_one.put("item_unit", PubFunc.DotstrNull(rs.getString("item_unit")));
                        hashm_one.put("sdata_src", PubFunc.DotstrNull(rs.getString("sdata_src")));
                        hashm_one.put("item_name", PubFunc.DotstrNull(rs.getString("item_name")));
                        hashM.put(rs.getString("item_id"), hashm_one);
                        continue;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return hashM;
    }
    
    /**
     * 根据考勤规则id获取考勤规则信息
     * @param kqItemId 考勤规则id
     * @return HashMap
     * @throws GeneralException
     */
    public HashMap<String, String> getKqItem(String kqItemId) throws GeneralException {
        HashMap<String, String> kqItemHM = new HashMap<String, String>();
        if (StringUtils.isBlank(kqItemId)) {
            return kqItemHM;
        }
        
        KqVer kqVer = new KqVer();
        
        StringBuffer sql = new StringBuffer();
        sql.append("select item_name,has_rest,has_feast,item_unit,fielditemid,sdata_src");
        sql.append(" from kq_item");
        sql.append(" where item_id=?");
        
        ArrayList<String> sqlParams = new ArrayList<String>();
        sqlParams.add(kqItemId);
        
        ContentDAO dao = new ContentDAO(this.conn);
        
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString(), sqlParams);
            if (rs.next()) {
                String srcTab = kqVer.getVersion() == KqConstant.Version.STANDARD ? "Q03" : "Q35";
                
                String fielditemid = StringUtils.isBlank(rs.getString("fielditemid")) ? "" : rs.getString("fielditemid");
                
                if (!"".equals(fielditemid)) {
                    FieldItem fielditem = DataDictionary.getFieldItem(fielditemid, srcTab);
                    if (fielditem == null || "0".equals(fielditem.getUseflag())) {
                        fielditemid = "";
                    }
                }
                    
                String has_rest = rs.getString("has_rest") != null
                        && rs.getString("has_rest").length() > 0 ? rs.getString("has_rest") : "0";
                        
                String has_feast = rs.getString("has_feast") != null
                        && rs.getString("has_feast").length() > 0 ? rs.getString("has_feast") : "0";
                        
                kqItemHM.put("fielditemid", fielditemid);
                kqItemHM.put("has_rest", has_rest);
                kqItemHM.put("has_feast", has_feast);
                kqItemHM.put("item_unit", PubFunc.DotstrNull(rs.getString("item_unit")));
                kqItemHM.put("sdata_src", PubFunc.DotstrNull(rs.getString("sdata_src")));
                kqItemHM.put("item_name", PubFunc.DotstrNull(rs.getString("item_name")));
                kqItemHM.put("item_id", kqItemId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return kqItemHM;
    }

    /**
     * 通过班次名称查找考勤规则
     * 
     * @param name
     * @return
     * @throws GeneralException
     */
    public HashMap<String, String> getKqItemByNameFromDB(String name) throws GeneralException {
        RowSet rs = null;
        String kq_item_sql = "select item_id,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";
        kq_item_sql = kq_item_sql + " where item_name='" + name + "'";
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap<String, String> hashm_one = new HashMap<String, String>();
        try {
            rs = dao.search(kq_item_sql);
            if (rs.next()) {
                hashm_one.put("fielditemid", rs.getString("fielditemid"));
                hashm_one.put("has_rest", PubFunc.DotstrNull(rs.getString("has_rest")));
                hashm_one.put("has_feast", PubFunc.DotstrNull(rs.getString("has_feast")));
                hashm_one.put("item_unit", PubFunc.DotstrNull(rs.getString("item_unit")));
                hashm_one.put("sdata_src", PubFunc.DotstrNull(rs.getString("sdata_src")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return hashm_one;
    }
}
