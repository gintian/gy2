package com.hjsj.hrms.taglib.kq;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 针对考勤分组人员
 * <p>Title:OrgEmpTreeTag.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 14, 2006 10:25:20 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class OrgEmpTreeTag extends BodyTagSupport {
    /**选中节点相应，弹出的网页*/
    private String  action;
    /**弹出网页目标帧*/
    private String  target;
    /**不加载组织机构下的人员信息*/
    private String  flag       = "0";
    /**选择方式
     * =0,正常方式
     * =1,checkbox
     * =2,radio
     * */
    private String  selecttype = "0";
    /**是否显示根节点*/
    private boolean showroot   = true;
    /**已选择的值*/
    private String  checkvalue = "";
    /**登录用户库标识
     * =0 权限范围内的库
     * =1 权限范围内的登录用户库
     */
    private String  dbtype     = "0";
    /**是否要加权限*/
    private String  priv       = "1";
    private String  orgcode    = "";
    private String  kqtype;
    private String  emp_flag   = "";

    public String getKqtype() {
        return kqtype;
    }

    public void setKqtype(String kqtype) {
        this.kqtype = kqtype;
    }

    public String getDbtype() {
        return dbtype;
    }

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }

    private String outTreePanel() {
        Connection conn = null;
        TreeItemView treeItem = new TreeItemView();
        try {
            conn = AdminDb.getConnection();
            String codeid = orgcode.substring(0, 2);
            String codevalue = orgcode.substring(2);
            treeItem.setName("root");
            treeItem.setIcon("/images/unit.gif");
            treeItem.setTarget(this.target);
            String rootdesc = "";
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(conn);
            rootdesc = sysoth.getValue(Sys_Oth_Parameter.ORG_ROOT_DESC);
            if (rootdesc == null || rootdesc.length() <= 0) {
                rootdesc = ResourceFactory.getProperty("tree.orgroot.orgdesc");
            }
            treeItem.setTitle(rootdesc);
            treeItem.setRootdesc(rootdesc.replaceAll("&", "&amp;"));
            treeItem.setText(rootdesc.replaceAll("&", "&amp;"));
            String paramStr = "target=" + this.target + "&kqtype=" + this.kqtype 
                    + "&emp_flag=" + this.emp_flag + "&flag=" + this.flag 
                    + "&dbtype=" + this.dbtype + "&priv=" + this.priv;
            if ("0".equals(this.priv))//不加权限过滤
            {
            	paramStr = paramStr + "&params=codeitemid%3Dparentid&id=UN";
            } else {
                if (!("UN".equals(orgcode))) {
                	paramStr = paramStr + "&params=codeitemid%3D'" + codevalue + "'&id=" + codeid + codevalue;
                } else {
                	paramStr = paramStr + "&params=codeitemid%3Dparentid&id=UN";
                }
            }
            String url = "/servlet/kq/array_load_emptree?encryptParam=" + PubFunc.encrypt(paramStr); 
            treeItem.setLoadChieldAction(url);
            treeItem.setAction("javascript:void(0)");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return treeItem.toJS();
    }

    public int doEndTag() throws JspException {
        StringBuffer strhtml = new StringBuffer();
        try {
            /*
            strhtml.append("<link href=\"/css/xtree.css\" rel=\"stylesheet\" type=\"text/css\" >");
            strhtml.append("\n");
            strhtml.append("<script LANGUAGE=\"javascript\" src=\"/js/xtree.js\"></script>");
            strhtml.append("\n");	
            */
            strhtml.append("<div id=\"treemenu\" class=\"fixedDiv\">");
            strhtml.append("<SCRIPT LANGUAGE=\"javascript\">");
            strhtml.append("\n");
            strhtml.append("Global.defaultInput=");
            strhtml.append(this.selecttype);
            strhtml.append(";\n");
            if (!this.showroot) {
                strhtml.append("\n");
                strhtml.append("Global.showroot=false;\n");
            }
            strhtml.append("\n");
            if (!(this.checkvalue == null || "".equals(this.checkvalue))) {
                strhtml.append("Global.checkvalue=\"");
                strhtml.append(this.checkvalue);
                strhtml.append("\";\n");
            }
            strhtml.append(outTreePanel());
            strhtml.append("</SCRIPT>");
            strhtml.append("</div>");
            pageContext.getOut().println(strhtml.toString());
            return SKIP_BODY;
        } catch (Exception ex) {
            ex.printStackTrace();
            return SKIP_BODY;
        }
    }

    public int doStartTag() throws JspException {
        return super.doStartTag();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getSelecttype() {
        return selecttype;
    }

    public void setSelecttype(String selecttype) {
        this.selecttype = selecttype;
    }

    public String getCheckvalue() {
        return checkvalue;
    }

    public void setCheckvalue(String checkvalue) {
        this.checkvalue = checkvalue;
    }

    public boolean isShowroot() {
        return showroot;
    }

    public void setShowroot(boolean showroot) {
        this.showroot = showroot;
    }

    public String getPriv() {
        return priv;
    }

    public void setPriv(String priv) {
        this.priv = priv;
    }

    public String getOrgcode() {
        return orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

}
