package com.hjsj.hrms.transaction.mobileapp.kq.util;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;

public class KqParamForApp {
   
    public KqParamForApp() {

    }

    /*
     * 获取假期管理假类
     */
    public String getHolidayTypes(Connection conn, UserView userView) {
        String name = "Holiday_type";
        return getContentWithGroup(conn, userView, name, "06,");
    }
    
    public String getHolidayTypes(Connection conn, String b0110) {
        String name = "Holiday_type";
        if (!b0110.startsWith("UN"))
            b0110 = "UN" + b0110;
        return getContentWithGroup(conn, b0110, name, "06,");
    }
    
    public String getOverTimeForLeaveTime(Connection conn) {
        ContentDAO dao = new ContentDAO(conn);
        return getContent(dao, "OVERTIME_FOR_LEAVETIME", "UN", "");
    }

    private String getUserManagePrivOrgIdWithPre(Connection conn, UserView userView) {
        String orgId;
        if (userView.isSuper_admin()) {
            orgId = "UN";
        } else {
            ManagePrivCode managePrivCode = new ManagePrivCode(userView, conn);
            orgId = "UN" + managePrivCode.getPrivOrgId();
        }

        return orgId;
    }

    /**
     * @Title: getContentWithGroup   
     * @Description: 取操作用户管理范围考勤参数content部分，支持集团化   
     * @param @param conn
     * @param @param userView
     * @param @param name
     * @param @param defaultValue
     * @param @return 
     * @return String    
     * @throws
     */
    private String getContentWithGroup(Connection conn, UserView userView, String name, String defaultValue) {
        String orgId = getUserManagePrivOrgIdWithPre(conn, userView);
        return getContentWithGroup(conn, orgId, name, defaultValue); 
    }
    
    /**
     * @Title: getContentWithGroup   
     * @Description: 取某单位的参数content部分，支持集团化   
     * @param @param conn
     * @param @param b0110
     * @param @param name
     * @param @param defaultValue
     * @param @return 
     * @return String    
     * @throws
     */
    private String getContentWithGroup(Connection conn, String b0110, String name, String defaultValue) {
        ContentDAO dao = new ContentDAO(conn);
        return getContent(dao, name, b0110, defaultValue);
    }
    
    /**
     * @Title: getContent   
     * @Description: 取考勤参数content部分，如本单位没有设置，将查找上级单位，直至UN  
     * @param @param dao
     * @param @param name
     * @param @param b0110
     * @param @param defaultValue
     * @param @return 
     * @return String    
     * @throws
     */
    public String getContent(ContentDAO dao, String name, String b0110, String defaultValue) {
        return getParamInfo(dao, name, b0110, defaultValue, "content");
    }
    
    private String getParamInfo(ContentDAO dao, String name, String b0110, String defaultValue, String paramField) {
        String value = defaultValue;
        
        RowSet rs = null;
        try {
            rs = queryParam(dao, name, b0110);
            while (!foundParam(rs, b0110, paramField)) {
                closeRowSet(rs);
                
                b0110 = getParentB0110(dao, b0110);
                rs = queryParam(dao, name, b0110);
            }
            
            if (rs.first()) {
                value = rs.getString(paramField);
                
                if (value == null || "".equals(value.trim()) || "#".equals(value.trim()))
                    value = defaultValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeRowSet(rs);
        }
        
        return value;
    }

    private RowSet queryParam(ContentDAO dao, String paramName, String b0110) {
        StringBuffer sql = new StringBuffer();
        sql.append("select content,status from kq_parameter");
        sql.append(" where upper(name)='" + paramName.toUpperCase() + "'");
        sql.append(" and upper(b0110)='" + b0110.toUpperCase() + "'");

        try {
            return dao.search(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getParentB0110(ContentDAO dao, String b0110) {
        RowSet rs = null;
        String parentId = "UN";
        try {
            if (b0110.startsWith("UN"))
                b0110 = b0110.substring(2);

            String orgSql = "SELECT parentid from organization where codeitemid='" + b0110 + "'";
            rs = dao.search(orgSql);
            if (rs.next()) {
                if (!b0110.equalsIgnoreCase(rs.getString("parentid")))
                    parentId = "UN" + rs.getString("parentid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeRowSet(rs);
        }

        return parentId;
    }

    /**
     * @Title: foundParam   
     * @Description: 判断是否取到了参数（已上溯到根节点或当前节点有参数）  
     * @param @param rs
     * @param @param b0110
     * @param @return 
     * @return boolean    
     * @throws
     */
    private boolean foundParam(RowSet rs, String b0110, String paramField) {
        boolean found = false;
        try {
            found = "UN".equals(b0110.toUpperCase()) //到根节点
                    || (rs.next() && (null != rs.getString(paramField) && !"".equals(rs.getString(paramField).trim())));//没取到参数
        } catch (Exception e) {

        }

        return found;
    }
    
    public static void closeRowSet(RowSet rs) {
        if (null == rs) return;
        
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
