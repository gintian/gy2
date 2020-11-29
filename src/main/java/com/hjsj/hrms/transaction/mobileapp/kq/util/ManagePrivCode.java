package com.hjsj.hrms.transaction.mobileapp.kq.util;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;

public class ManagePrivCode {
    private UserView   userView;
    private Connection conn;

    public ManagePrivCode() {

    }

    public ManagePrivCode(UserView userView, Connection conn) {
        this.userView = userView;
        this.conn = conn;
    }

    /**
     * 加UN的单位编号
     * @return
     * @throws GeneralException
     */
    public String getUNB0110() {
        String b0110 = "";
        b0110 = "UN";
        try {
            if (userView.isSuper_admin())
                return b0110;
            
            if (userView.getUserOrgId() != null && userView.getUserOrgId().length() > 0) {
                b0110 = b0110 + userView.getUserOrgId();
            } else {
                b0110 = b0110 + getPrivCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b0110;
    }

    /**
     * 单位编号
     * @return
     * @throws GeneralException
     */
    public String getPrivOrgId() {
        String b0110 = "";
        try {
            if (userView.getUserOrgId() != null && userView.getUserOrgId().length() > 0) {
                //	  	  		b0110=userView.getUserOrgId();    //这里更改为得到用户当前最大权限
                b0110 = getKqPrivCodeValue(this.userView);
            } else {
                b0110 = getPrivCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b0110;
    }

    public String getPrivCode() throws GeneralException {
        String code = "";
        if ("UN".equalsIgnoreCase(getKqPrivCode(this.userView))) {
            code = getKqPrivCodeValue(this.userView);
        } else {
            code = getDbB0100();
        }
        return code;
    }

    /**
     * 得到单位
     * @return
     * @throws GeneralException
     */
    public String getDbB0100() throws GeneralException {
        String code = getKqPrivCodeValue(this.userView);
        String b0110 = code;
        String codesetid = "";
        String kind = getKqPrivCode(this.userView);
        if ("UM".equalsIgnoreCase(kind) || "@K".equalsIgnoreCase(kind)) {
            codesetid = code;
            do {
                String codeset[] = getB0100(b0110);
                if (codeset != null && codeset.length >= 0) {
                    codesetid = codeset[0];
                    b0110 = codeset[1];
                }
            } while (!"UN".equals(codesetid));

        }
        return b0110;
    }

    private String[] getB0100(String codeitemid) throws GeneralException {
        String codeset[] = new String[2];
        String parentid = "";
        String codesetid = "";
        RowSet rs = null;
        try {
            String orgSql = "SELECT parentid,codeitemid,codesetid from organization where codeitemid='" + codeitemid + "'";
            ContentDAO dao = new ContentDAO(this.conn);

            rs = dao.search(orgSql);
            if (rs.next()) {
                codesetid = rs.getString("codesetid");
                parentid = rs.getString("parentid");
                if (codesetid != null && "UN".equalsIgnoreCase(codesetid)) {
                    codeset[0] = "UN";
                    codeset[1] = parentid;
                } else {
                    orgSql = "SELECT parentid,codesetid from organization where codeitemid='" + parentid + "'";
                    rs = dao.search(orgSql);
                    if (rs.next()) {
                        codeset[0] = rs.getString("codesetid");
                        codeset[1] = parentid;
                    }
                }
            } else {
                codeset[0] = "UN";
                codeset[1] = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return codeset;
    }

    public String getB0110FromA0100(String a0100, String nbase) throws GeneralException {
        if (a0100 == null || a0100.length() <= 0)
            return "";
        
        if (nbase == null || nbase.length() <= 0)
            return "";
        
        String b0110 = "";
        StringBuffer sql = new StringBuffer();
        sql.append("select B0110 from " + nbase + "A01");
        sql.append(" where A0100='" + a0100 + "'");
        
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                b0110 = rs.getString("B0110");
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(rs);
        }
        return b0110;
    }
    /**
     * 得到考勤管理范围编码
     * @param userView
     * @return
     */
    public static String getKqPrivCode(UserView userView)
    {
        String privCode=userView.getKqManageValue();
        if(privCode!=null&&privCode.length()>0)
            privCode=privCode.substring(0,2);
        else
            privCode=userView.getManagePrivCode();
        return privCode;
    }
    /**
     * 得到考勤范围编码值
     * @param userView
     * @return
     */
    public static String getKqPrivCodeValue(UserView userView)
    {
        if(userView.isSuper_admin())
            return "";
        String privCode=userView.getKqManageValue();
        if(privCode!=null&&privCode.length()>0)
            privCode=privCode.substring(2);
        else
            privCode=userView.getManagePrivCodeValue();
        return privCode;
    }
}
