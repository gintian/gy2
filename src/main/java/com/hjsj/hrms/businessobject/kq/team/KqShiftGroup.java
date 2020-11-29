package com.hjsj.hrms.businessobject.kq.team;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 考勤班组信息
 * <p>
 * Title:KqShiftGroup.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Oct 26, 2006 2:20:30 PM
 * </p>
 * 
 * @author sunxin
 * @version 1.0
 * 
 */
public class KqShiftGroup {
    private Connection conn     = null;
    private UserView   userView = null;

    public KqShiftGroup() {
    }

    public KqShiftGroup(Connection conn) {
        this.conn = conn;
    }

    public KqShiftGroup(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    /**
     * 得到部门或单位下的班组sql语句
     * 
     * @param a_code
     * @return
     */
    public HashMap getKqGroupSQL(String orgId) {
        HashMap hash = new HashMap();
        
        hash.put("sqlstr", "select " + KqClassArrayConstant.kq_shift_group_Id + "," + KqClassArrayConstant.kq_shift_group_name + "," + Sql_switcher.substr("org_id", "3", Sql_switcher.length("org_id")) + "org_id ");
        hash.put("column", KqClassArrayConstant.kq_shift_group_Id + "," + KqClassArrayConstant.kq_shift_group_name + ",org_id");
        
        StringBuffer wherebuf = new StringBuffer();
         
        wherebuf.append("from " + KqClassArrayConstant.kq_shift_group_table);
        wherebuf.append(" where 1=1");
        
        GroupsArray groupsArray = new GroupsArray(this.conn, this.userView);
        String groupOrgWhr = groupsArray.getGroupOrgWhr(orgId);
        if (groupOrgWhr != null && groupOrgWhr.length() > 0) {
            wherebuf.append(" and " + groupOrgWhr);
        }
        
        if (!this.userView.isSuper_admin()) {
            String priv = this.userView.getResourceString(IResourceConstant.KQ_CLASS_GROUP);
            if (priv == null || priv.length() <= 0) {
                wherebuf.append(" and 1=2");
            } else {
                String[] ss = priv.split(",");
                wherebuf.append(" and " + KqClassArrayConstant.kq_shift_group_Id + " in ('','UN',");
                for (int i = 0; i < ss.length; i++) {
                    wherebuf.append("'" + ss[i] + "',");
                }
                wherebuf.setLength(wherebuf.length() - 1);
                wherebuf.append(")");
            }
        }

        hash.put("where", wherebuf.toString());

        return hash;
    }

    /**
     * 
     * @param sql
     * @return
     */
    public ArrayList getGroupVoList(String sql) {
        ArrayList list = new ArrayList();
        return list;
    }

    /**
     * 得到单位编号
     * 
     * @param a_code
     * @param nbase
     * @param userView
     * @param formHM
     * @return
     * @throws GeneralException
     */
    public String getOrgId(String a_code, String nbase, UserView userView, HashMap formHM) throws GeneralException {
        String b0110 = "";
        RowSet rs = null;
        try {
            if (a_code == null || a_code.length() <= 0) {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.conn);
                b0110 = managePrivCode.getPrivOrgId();
            } else {
                if (a_code.indexOf("UN") != -1) {
                    if (!"UN".equals(a_code)) {
                        b0110 = a_code.substring(2);
                    }

                } else if (a_code.indexOf("UM") != -1) {
                    b0110 = a_code.substring(2);

                } else if (a_code.indexOf("@K") != -1) {
                    String code = a_code.substring(2);
                    String orgSql = "SELECT parentid,codeitemid from organization where codeitemid='" + code + "'";
                    ContentDAO dao = new ContentDAO(this.conn);
                    rs = dao.search(orgSql);
                    if (rs.next()) {
                        b0110 = rs.getString("parentid");
                    }

                } else if (a_code.indexOf("EP") != -1) {
                    String code = a_code.substring(2);
                    String sql = "select b0110,e0122 from " + nbase + "A01 where a0100='" + code + "'";
                    ContentDAO dao = new ContentDAO(this.conn);
                    rs = dao.search(sql);
                    if (rs.next()) {
                        b0110 = rs.getString("e0122");
                        if (b0110 == null || b0110.length() <= 0) {
                            b0110 = rs.getString("b0110");
                        }
                    }
                } else if (a_code.indexOf("GP") != -1) {
                    /*
                     * String code=a_code.substring(2); String
                     * sql="select b0110,e0122 from kq_group_emp where group_id='"
                     * +code+"'"; ContentDAO dao=new ContentDAO(this.conn);
                     * RowSet rs=dao.search(sql); if(rs.next()) {
                     * b0110=rs.getString("e0122");
                     * if(b0110==null||b0110.length()<=0) {
                     * b0110=rs.getString("b0110"); } }
                     */
                    b0110 = userView.getUserOrgId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return b0110;
    }

    public String getQueryString(ArrayList dblist, UserView userView, String code, String kind, ArrayList emplist) throws GeneralException {
        StringBuffer strsql = new StringBuffer();

        try {
            if (dblist.size() <= 0 || dblist == null) {
                throw new GeneralException(ResourceFactory.getProperty("kq.param.nosave.userbase"));
            }

            String kqTypeFld = getKqTypeField();
            for (int i = 0; i < dblist.size(); i++) {
                String nbase = (String) dblist.get(i);
                String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
                strsql.append("select '");
                strsql.append(nbase);
                strsql.append("' as nbase,");
                strsql.append("b0110,e0122,e01a1,a0100,a0101");
                strsql.append(" from ");
                strsql.append(nbase);
                strsql.append("a01");
                strsql.append(" where ");
                if ("1".equals(kind)) {
                    strsql.append("e0122 like '" + code + "%'");
                } else if ("0".equals(kind)) {
                    strsql.append("e01a1 like '" + code + "%'");
                } else if ("2".equals(kind)) {
                    strsql.append("b0110 like '" + code + "%'");
                } else if ("a01".equals(kind)) {
                    strsql.append("a0100 = '" + code + "'");
                }
                
                strsql.append(getKqTypeWhr(kqTypeFld));
                
                if (emplist != null) {
                    StringBuffer a0100s = new StringBuffer();
                    for (int r = 0; r < emplist.size(); r++) {
                        ArrayList one_list = (ArrayList) emplist.get(r);
                        String userbase = one_list.get(0).toString();
                        if (userbase == null || userbase.length() <= 0) {
                            userbase = "";
                        }
                        if (nbase.equalsIgnoreCase(userbase)) {
                            a0100s.append("'" + one_list.get(1).toString() + "',");
                        }
                    }
                    if (a0100s != null && a0100s.length() > 0) {
                        a0100s.setLength(a0100s.length() - 1);
                        strsql.append(" and a0100 not in (" + a0100s + ")");
                    }
                }
                
                strsql.append(" and a0100 in (select a0100 " + whereIN + ")");
                strsql.append(" UNION ");
            }
            strsql.setLength(strsql.length() - 7);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }

        return strsql.toString();
    }
    
    private String getKqTypeField() {
        KqParameter para = new KqParameter(this.userView, this.conn);
        return para.getKq_type();
    }
    
    private String getKqTypeWhr(String kqTypeFld) {
        String whr = "";
        if(kqTypeFld == null || kqTypeFld.length() <= 0) {
            return whr;
        }
        
        whr = " AND " + Sql_switcher.sqlNull(kqTypeFld, "04") + "<>'04'"; 
        if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
            whr = whr + " AND " + kqTypeFld + "<>''";
        }
            
        return whr;    
    }
}
