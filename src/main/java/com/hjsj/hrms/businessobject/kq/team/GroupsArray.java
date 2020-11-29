package com.hjsj.hrms.businessobject.kq.team;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * <p>Title: GroupsArray </p>
 * <p>Description: 班组辅助类</p>
 * <p>Company: hjsj</p>
 * <p>create 2013-8-19 下午05:40:43</p>
 * @modify zhaoxj
 * @version 2.0
 *
 */
public class GroupsArray implements KqClassArrayConstant {
    private Connection conn;
    private UserView   userView;

    public GroupsArray(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    public GroupsArray() {

    }

    public String getWhere(String a_code) {
        StringBuffer where = new StringBuffer();
        String org_id = "";
        where.append(" 1=1 ");
        if (a_code == null || a_code.length() <= 2) {
            if (!this.userView.isSuper_admin()) {
                org_id = RegisterInitInfoData.getKqPrivCodeValue(userView);
                if (org_id == null || org_id.length() <= 0) {
                    org_id = this.userView.getUserDeptId();
                }
                if (org_id == null || org_id.length() <= 0) {
                    org_id = this.userView.getUserOrgId();
                }
                where.append(" and org_id like '" + org_id + "%'");
            }
        } else {
            String codesetid = a_code.substring(0, 2);
            String codesetvalue = a_code.substring(2);
            if ("GP".equalsIgnoreCase(codesetid)) {
                where.append(" and group_id='" + codesetvalue + "'");
            } else {
                where.append(" and org_id like '" + codesetvalue + "%'");
            }
        }
        return where.toString();
    }

    public ArrayList groupEmpFieldlist() {
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        KqParameter para = new KqParameter(this.userView, "", this.conn);
        String g_no = para.getG_no();
        String cardno = para.getCardno();
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        ArrayList list = new ArrayList();
        FieldItem fielditem = new FieldItem();
        fielditem = new FieldItem();
        fielditem.setItemdesc("人员编号");
        fielditem.setItemid("a0100");
        fielditem.setItemtype("A");
        fielditem.setCodesetid("0");
        fielditem.setVisible(false);
        list.add(fielditem);
        fielditem = new FieldItem();
        fielditem.setItemdesc("人员库");
        fielditem.setItemid("nbase");
        fielditem.setItemtype("A");
        fielditem.setVisible(true);
        fielditem.setCodesetid("@@");
        list.add(fielditem);
        fielditem = new FieldItem();
        fielditem.setItemdesc("班组编号");
        fielditem.setItemid("group_id");
        fielditem.setItemtype("A");
        fielditem.setVisible(false);
        fielditem.setCodesetid("0");
        list.add(fielditem);
        fielditem = new FieldItem();
        fielditem.setItemdesc("单位名称");
        fielditem.setItemid("b0110");
        fielditem.setItemtype("A");
        fielditem.setCodesetid("UN");
        fielditem.setVisible(true);
        list.add(fielditem);
        fielditem = new FieldItem();
        fielditem.setItemdesc("部门");
        fielditem.setItemid("e0122");
        fielditem.setItemtype("A");
        fielditem.setCodesetid("UM");
        fielditem.setVisible(true);
        list.add(fielditem);
        fielditem = new FieldItem();
        fielditem.setItemdesc("姓名");
        fielditem.setItemid("a0101");
        fielditem.setItemtype("A");
        fielditem.setCodesetid("0");
        fielditem.setVisible(true);
        list.add(fielditem);
        fielditem = new FieldItem();
        fielditem.setItemid(g_no.toLowerCase());
        fielditem.setItemtype("A");
        fielditem.setCodesetid("0");
        fielditem.setVisible(true);
        fielditem.setItemdesc("工号");
        list.add(fielditem);
        fielditem = new FieldItem();
        fielditem.setItemid(cardno.toLowerCase());
        fielditem.setItemtype("A");
        fielditem.setCodesetid("0");
        fielditem.setVisible(true);
        fielditem.setItemdesc("考勤卡号");
        list.add(fielditem);
        return list;
    }

    public String groupEmpColumns() {
        String columns = "a0100,nbase,group_id,a0101,b0110,e0122";
        return columns;
    }

    /**
     * 通过班组id得到组织编号
     * @param group_id
     * @param conn
     * @return
     */
    public String getCodeFromGroupId(String group_id, Connection conn) {
        StringBuffer sql = new StringBuffer();
        sql.append("select " + kq_shift_org_id + " from ");
        sql.append(" " + kq_shift_group_table + " where ");
        sql.append(kq_shift_group_Id + "='" + group_id + "'");
        RowSet rs = null;
        String code = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                code = rs.getString(kq_shift_org_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return code;
    }

    public String getA_codeFromCodeItemId(String codeitemid, Connection conn) {
        StringBuffer sql = new StringBuffer();
        sql.append("select codesetid,codeitemid,codeitemdesc,childid from organization");
        sql.append(" where codeitemid='" + codeitemid + "'");
        RowSet rs = null;
        String codesetid = "";
        String a_code = "";
        try {
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                codesetid = rs.getString("codesetid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        a_code = codesetid + codeitemid;
        return a_code;
    }

    /**
     * 所属机构号
     * @return
     */
    public String orgCodeID() {
        String codeitem = "";
        if (this.userView.isSuper_admin()) {
            return "UN";
        } else {
            if (RegisterInitInfoData.getKqPrivCode(userView) != null && RegisterInitInfoData.getKqPrivCode(userView).length() > 0) {
                if (!"@K".equalsIgnoreCase(RegisterInitInfoData.getKqPrivCode(userView))) {
                    codeitem = RegisterInitInfoData.getKqPrivCode(userView) + RegisterInitInfoData.getKqPrivCodeValue(userView);
                }
            }
        }
        if (codeitem.length() <= 0) {
            codeitem = this.userView.getUserDeptId();
            if (codeitem == null || codeitem.length() <= 0) {
                codeitem = "UM" + codeitem;
            }
            if (codeitem == null || codeitem.length() <= 0) {
                codeitem = this.userView.getUserOrgId();
                codeitem = "UN" + codeitem;
            } else {
                codeitem = "UN";
            }

        }
        return codeitem;
    }

    /**
     * 得到班组表的组织机构条件
     * @orgId 组织机构id，格式"UN01"等带前缀的数据
     */
    public String getGroupOrgWhr(String orgId) {
        if (orgId == null || orgId.length() < 2 || "root".equals(orgId)) {
            orgId = orgCodeID();
        }

        orgId = orgId.substring(2, orgId.length());

        String groupOrgFld = Sql_switcher.isnull("org_id", "'UN'");
        StringBuffer whr = new StringBuffer();
        whr.append(" (" + groupOrgFld + " LIKE 'UN" + orgId + "%'");
        whr.append(" OR " + groupOrgFld + " LIKE 'UM" + orgId + "%'");
        whr.append(" OR " + groupOrgFld + "='UN') ");

        return whr.toString();
    }
}
