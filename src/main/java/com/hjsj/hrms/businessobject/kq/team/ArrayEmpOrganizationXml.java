package com.hjsj.hrms.businessobject.kq.team;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ArrayEmpOrganizationXml {

    /**
     * 参数串
     */
    private String  params;
    /**
     * 执行jsp文件
     */
    private String  action;
    /**
     * 目标窗口
     */
    private String  target;
    /**人员还是对组织*/
    private String  flag;
    /**加载应用库标识
     * =0权限范围内的库
     * =1权限范围内的登录库
     * */
    private String  dbtype;
    /**权限过滤标识
     * =0， 不进行权限过滤
     * =1  进行权限过滤
     * */
    private String  priv   = "1";
    private String  emp_flag;
    private String  kqtype;
    private boolean isPost = true; //考勤组织机构树是否显示岗位
    private boolean bfirst;

    public boolean isBfirst() {
        return bfirst;
    }

    public void setBfirst(boolean bfirst) {
        this.bfirst = bfirst;
    }

    /**
     * 构造函数
     * @param params
     * @param action
     * @param target
     * @param flag
     */
    public ArrayEmpOrganizationXml(String params, String action, String target, String flag, String dbtype) {
        this.params = PubFunc.keyWord_reback(params);
        this.target = target;
        this.action = action;
        this.flag = flag;
        this.dbtype = dbtype;
    }

    public ArrayEmpOrganizationXml(String params, String action, String target, String flag, String dbtype, String priv,
            String emp_flag, String kqtype) {
        this( PubFunc.keyWord_reback(params), action, target, flag, dbtype);
        this.priv = priv;
        this.emp_flag = emp_flag;
        this.kqtype = kqtype;
    }

    public ArrayEmpOrganizationXml(String params, String action, String target, String flag, String dbtype, String priv,
            String emp_flag, String kqtype, boolean isPost) {
        this( PubFunc.keyWord_reback(params), action, target, flag, dbtype);
        this.priv = priv;
        this.emp_flag = emp_flag;
        this.kqtype = kqtype;
        this.isPost = isPost;
    }

    public ArrayEmpOrganizationXml(String params, String action, String target, String flag) {
        this.params =  PubFunc.keyWord_reback(params);
        this.target = target;
        this.action = action;
        this.flag = flag;
        this.dbtype = "0";
    }

    private String getSelectString(String dbpre) {
        StringBuffer strsql = new StringBuffer();
        strsql.append("select distinct a0000,");
        strsql.append(dbpre);
        strsql.append("a01.a0100 ,'");
        strsql.append(dbpre);
        strsql.append("' as dbase,");
        strsql.append(dbpre);
        strsql.append("a01.b0110 b0110,e0122,");
        strsql.append(dbpre);
        strsql.append("a01.e01a1 e01a1,a0101 ");
        strsql.append(" ");
        return strsql.toString();
    }

    private void getEmploys(UserView userview, String parentid, Element root, Connection conn) {
        String strsql = getPrivSql(userview, parentid, conn);
        if ("".equals(strsql)) {
            return;
        }

        ContentDAO dao = new ContentDAO(conn);
        RowSet rset = null;
        try {
            rset = dao.search(strsql);
            while (rset.next()) {
                String nbase = rset.getString("dbase");
                String a0100 = rset.getString("a0100");
                String a0101 = rset.getString("a0101");
                Element child = new Element("TreeNode");
                child.setAttribute("id", nbase + a0100);
                if (a0101 == null) {
                    a0101 = "";
                }
                child.setAttribute("text", a0101);
                child.setAttribute("title", a0101);
                child.setAttribute("icon", "/images/man.gif");
                root.addContent(child);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getPrivSql(UserView userview, String parentid, Connection conn) {
        StringBuffer strSql = new StringBuffer();

        try {
            /**权限因子*/
            String codeid = parentid.substring(0, 2);
            String codevalue = parentid.substring(2);
            StringBuffer expr = new StringBuffer();
          //2014.11.5 xxd当单位下直接挂有岗位的时候还要对岗位进行判断
            if ("UN".equalsIgnoreCase(codeid)) {
                expr.append("1*2*3|");
                expr.append("E01A1=`E0122=`B0110=");
            } else if ("UM".equalsIgnoreCase(codeid)) {
                if (!isPost) {
                    expr.append("1|");
                    expr.append("E0122=");
                } else {
                    expr.append("1*2|");
                    expr.append("E01A1=`E0122=");
                }
            } else {
                expr.append("1|");
                expr.append("E01A1=");
            }
            expr.append(codevalue + "`");

            HashMap formHM = new HashMap();
            ArrayList dblist = RegisterInitInfoData.getB0110Dase(formHM, userview, conn, parentid);
            if (dblist.size() == 0) {
                return "";
            }

            String kqTypeWhr = new KqUtilsClass(conn, userview).getKqTypeWhere(KqConstant.KqType.STOP, true);
            
            ArrayList fieldlist = new ArrayList();
            String strWhere = null;
            String strSelect = null;
            String kq_group_emp_table = "kq_group_emp";
            
            /**加权限过滤*/
            for (int i = 0; i < dblist.size(); i++) {
                String nbase = (String) dblist.get(i);

                if ("1".equals(priv)) {
                    if (userview.getKqManageValue() != null && !"".equals(userview.getKqManageValue())) {
                        strWhere = userview.getKqPrivSQLExpression(expr.toString(), nbase, fieldlist);
                    } else {
                        strWhere = userview.getPrivSQLExpression(expr.toString(), nbase, false, true, fieldlist);
                    }
                } else {
                    FactorList factor_bo = new FactorList(expr.toString(), nbase, false, false, true, 1, userview.getUserId());
                    strWhere = factor_bo.getSqlExpression();
                }
                
                strSelect = getSelectString(nbase);
                strSql.append(strSelect);
                strSql.append(strWhere);
                strSql.append(kqTypeWhr);
                strSql.append(" and NOT EXISTS(SELECT 1 FROM " + kq_group_emp_table);
                strSql.append(" where " + nbase + "A01.a0100=kq_group_emp.a0100");
                strSql.append(" and UPPER(kq_group_emp.nbase)='" + nbase.toUpperCase() + "')");
                strSql.append(" UNION ");
            }

            strSql.setLength(strSql.length() - 7);
            strSql.append(" order by dbase desc,a0000");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return strSql.toString();
    }

    public String outOrgEmployTree(UserView userview, String parentid) throws GeneralException {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        Connection conn = AdminDb.getConnection();
        Element root = new Element("TreeNode");
        root.setAttribute("id", "00");
        root.setAttribute("text", "root");
        root.setAttribute("title", "organization");
        Document myDocument = new Document(root);
        String theaction = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String backdate = sdf.format(new Date());
        try {
            strsql.append("select codesetid,codeitemid,codeitemdesc,childid from organization where ");
            strsql.append(params);
            strsql.append(" and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date ");
            if (!isPost) {
                strsql.append(" and codesetid<>'@K'");
            }
            strsql.append(" ORDER BY a0000,codeitemid ");
            
            ContentDAO dao = new ContentDAO(conn);
            rset = dao.search(strsql.toString());
            /**加载组织机构树*/
            while (rset.next()) {
                Element child = new Element("TreeNode");
                child.setAttribute("id", rset.getString("codesetid") + rset.getString("codeitemid"));
                child.setAttribute("text", rset.getString("codeitemdesc"));
                child.setAttribute("title", rset.getString("codeitemdesc"));
                if (!(this.action == null || "".equals(this.action))) {
                    if (this.action.indexOf('?') == 0) {
                        theaction = this.action + "?a_code=" + URLEncoder.encode(rset.getString("codesetid") + rset.getString("codeitemid"));
                    } else {
                        theaction = this.action + "&a_code=" + URLEncoder.encode(rset.getString("codesetid") + rset.getString("codeitemid"));
                    }
                }
                if (theaction == null || "".equals(theaction)) {
                    child.setAttribute("href", "javascript:void(0)");
                } else {
                    child.setAttribute("href", theaction);
                }
                child.setAttribute("target", this.target);
                String url = "/servlet/kq/array_load_emptree?emp_flag=1&target=" + this.target + "&flag=" + this.flag
                        + "&dbtype=" + this.dbtype + "&priv=" + this.priv;
                url = url + "&first=1&kqtype=" + this.kqtype + "&params="+URLEncoder.encode("parentId<>codeitemid and parentid%3D'"
                        + rset.getString("codeitemid") + "'");
                url = url + "&id=" + rset.getString("codesetid") + rset.getString("codeitemid");
                child.setAttribute("xml", url);
                if ("UN".equals(rset.getString("codesetid"))) {
                    child.setAttribute("icon", "/images/unit.gif");
                }
                if ("UM".equals(rset.getString("codesetid"))) {
                    child.setAttribute("icon", "/images/dept.gif");
                }
                if ("@K".equals(rset.getString("codesetid"))) {
                    child.setAttribute("icon", "/images/pos_l.gif");
                }
                root.addContent(child);
            }
            /**加载当前机构下的人员*/
            if ("1".equals(flag) && this.isBfirst()) {
                getEmploys(userview, parentid, root, conn);
            }
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            xmls.append(outputter.outputString(myDocument));
            //System.out.println("SQL=" +xmls.toString());
        } catch (Exception ee) {
            ee.printStackTrace();
            GeneralExceptionHandler.Handle(ee);
        } finally {
            PubFunc.closeResource(rset);
            PubFunc.closeResource(conn);
        }
        return xmls.toString();
    }

    public String getPriv() {
        return priv;
    }

    public void setPriv(String priv) {
        this.priv = priv;
    }
}
