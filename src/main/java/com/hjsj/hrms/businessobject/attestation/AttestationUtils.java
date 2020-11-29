package com.hjsj.hrms.businessobject.attestation;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

public class AttestationUtils {

    public LazyDynaBean getUserNamePassField() {
        LazyDynaBean bean = new LazyDynaBean();
        Connection con = null;
        try {
            con = AdminDb.getConnection();
            DbNameBo dbNameBo = new DbNameBo(con);
            bean.set("name", dbNameBo.getLogonUserNameField());
            bean.set("pass", dbNameBo.getLogonPassWordField());
        } catch (Exception e) {

        } finally {
            PubFunc.closeDbObj(con);
        }

        return bean;
    }

    public String getetoken(String nbase, String a0100, Connection conn) {

        LazyDynaBean fieldbean = getUserNamePassField();
        String username_field = (String) fieldbean.get("name");
        String password_field = (String) fieldbean.get("pass");
        StringBuffer sql = new StringBuffer("");
        sql.append("select a0101," + username_field + " username," + password_field + " password,a0101 from " + nbase + "A01");
        sql.append(" where a0100='" + a0100 + "'");
        ContentDAO dao = new ContentDAO(conn);
        String etoken = "";
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            String username = "";
            String password = "";
            if (rs.next()) {
                username = rs.getString("username");
                password = rs.getString("password");
            }

            if (password == null) {
                password = "";
            }
            etoken = PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username + "," + password));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return etoken;
    }

    public String getUsername(String value, String field) {
        String userName = "";
        LazyDynaBean bean = getUserNamePassField();
        String username_field = (String) bean.get("name");

        Connection conn = null;
        RowSet rs = null;
        RowSet rs2 = null;
        StringBuffer sql = new StringBuffer();

        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            String sqls = "select str_value from constant where constant='SS_LOGIN'";
            rs = dao.search(sqls);
            String nbases = "";
            if (rs.next()) {
                nbases = rs.getString("str_value");
            }

            if (nbases != null && nbases.length() > 0) {
                String[] nbase = nbases.split(",");
                for (int i = 0; i < nbase.length; i++) {
                    sql.delete(0, sql.length());
                    sql.append("select " + username_field + " username222 ");
                    sql.append(" from " + nbase[i] + "A01 where " + field + "='" + value + "'");

                    rs2 = dao.search(sql.toString());

                    if (rs2.next()) {
                        userName = rs2.getString("username222");
                        userName = userName == null ? "" : userName;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(rs2);
            PubFunc.closeDbObj(conn);
        }

        return userName;
    }
}
