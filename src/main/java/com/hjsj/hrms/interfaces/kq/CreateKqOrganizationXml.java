package com.hjsj.hrms.interfaces.kq;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreateKqOrganizationXml {

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
    private boolean bfirst;

    // 组织机构中是否显示岗位
    private boolean isPost = true;

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
    public CreateKqOrganizationXml(String params, String action, String target, String flag, String dbtype) {
        this.params = PubFunc.keyWord_reback(params);
        this.target = target;
        this.action = action;
        this.flag = flag;
        this.dbtype = dbtype;
    }

    public CreateKqOrganizationXml(String params, String action, String target, String flag) {
        this.params = PubFunc.keyWord_reback(params);
        this.target = target;
        this.action = action;
        this.flag = flag;
        this.dbtype = "0";
    }

    public CreateKqOrganizationXml(String params, String action, String target, String flag, String dbtype, boolean isPost) {
        this.params = PubFunc.keyWord_reback(params);
        this.target = target;
        this.action = action;
        this.flag = flag;
        this.dbtype = dbtype;
        this.isPost = isPost;

    }

    /**
     * 得到连接的人员节点
     * @param userview
     * @param parentid
     * @param root
     * @param conn
     */
    private void getEmploys_Href(UserView userview, String parentid, Element root, Connection conn, String kq_type) {
        String strsql = getPrivSql(userview, parentid, conn, true);
        if ("".equals(strsql))
            return;

        ContentDAO dao = new ContentDAO(conn);
        RowSet rset = null;
        String theaction = null;
        try {
            rset = dao.search(strsql);
            while (rset.next()) {
                String nbase = rset.getString("dbase");
                String a0100 = rset.getString("a0100");
                String a0101 = rset.getString("a0101");
                Element child = new Element("TreeNode");
                child.setAttribute("id", nbase + a0100);
                if (a0101 == null)
                    a0101 = "";
                child.setAttribute("text", a0101);
                child.setAttribute("title", a0101);
                child.setAttribute("target", this.target);
                if (!(this.action == null || "".equals(this.action))) {
                    theaction = this.action + "?b_search=link&encryptParam="+PubFunc.encrypt("a_code=EP" + rset.getString("a0100") + "&nbase=" + nbase);
                }
                if (theaction == null || "".equals(theaction))
                    child.setAttribute("href", "javascript:void(0)");
                else
                    child.setAttribute("href", theaction);
                child.setAttribute("icon", "/images/man.gif");
                root.addContent(child);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rset);
        }
    }

    /**
     * 班组的信息
     * @param userview
     * @param parentid
     * @param root
     * @param conn
     * @return
     */
    public ArrayList getGroup_Href(UserView userview, String parentid, Element root, Connection conn) {
        String codeitemid = parentid.substring(2);

        StringBuffer sql = new StringBuffer();
        sql.append("select group_id,name,org_id from kq_shift_group");
        sql.append(" where org_id='" + codeitemid + "'");

        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(conn);
        RowSet rset = null;
        String theaction = null;
        try {
            rset = dao.search(sql.toString());
            while (rset.next()) {
                Element child = new Element("TreeNode");
                child.setAttribute("id", "GP" + rset.getString("group_id"));
                child.setAttribute("text", rset.getString("name"));
                child.setAttribute("title", rset.getString("name"));
                if (!(this.action == null || "".equals(this.action))) {
                    theaction = this.action + "?b_search=link&encryptParam="+PubFunc.encrypt("a_code=GP" + rset.getString("group_id"));
                }

                if (theaction == null || "".equals(theaction))
                    child.setAttribute("href", "javascript:void(0)");
                else
                    child.setAttribute("href", theaction);

                child.setAttribute("target", this.target);

                String url = "/common/orgemp/loadtree?first=1&target=" + this.target + "&flag=" + this.flag + "&dbtype="
                        + this.dbtype + "&action=" + this.action;
                url = url + "&params=parentId<>codeitemid and parentid%3D'GP" + rset.getString("group_id") + "'";
                url = url + "&id=GP" + rset.getString("group_id");

                child.setAttribute("xml", url);
                child.setAttribute("icon", "/images/admin.gif");
                root.addContent(child);
                list.add(rset.getString("group_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rset);
        }
        return list;
    }

    public String outOrgEmpTree(UserView userview, String parentid, String kq_type) throws GeneralException {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        Connection conn = AdminDb.getConnection();
        ResultSet rset = null;

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

            ContentDAO dao=new ContentDAO(conn);
            rset = dao.search(strsql.toString());
            /**加载组织机构树*/
            while (rset.next()) {
                Element child = new Element("TreeNode");
                child.setAttribute("id", rset.getString("codesetid") + rset.getString("codeitemid"));
                child.setAttribute("text", rset.getString("codeitemdesc"));
                child.setAttribute("title", rset.getString("codeitemdesc"));
                if (!(this.action == null || "".equals(this.action))) {
                    theaction = this.action + "?b_search=link&encryptParam="+PubFunc.encrypt("a_code=" + rset.getString("codesetid")
                            + rset.getString("codeitemid"));
                }

                if (theaction == null || "".equals(theaction))
                    child.setAttribute("href", "javascript:void(0)");
                else
                    child.setAttribute("href", theaction);
                child.setAttribute("target", this.target);

                String url = "first=1&target=" + this.target + "&flag=" + this.flag + "&dbtype="
                        + this.dbtype + "&action=" + this.action;
                url = url + "&kq_type=" + kq_type + "&params=parentId<>codeitemid and parentid%3D'"
                        + rset.getString("codeitemid") + "'";
                url = url + "&id=" + rset.getString("codesetid") + rset.getString("codeitemid");
                url = "/common/orgemp/loadtree?encryptParam=" +PubFunc.encrypt(url);
                
                
                String childId = rset.getString("childid");
                
                //zxj 20141018 
                //当子节点为空无法判断是否有下级节点或人时，或者有下级组织机构节点时，或者没有下级组织机构但有人员时
                //以上三种情况需要显示节点前的展开标记（+）
                if (childId == null 
                        || !childId.equalsIgnoreCase(rset.getString("codeitemid")) 
                        || hasEmployee(conn, userview, rset.getString("codesetid"), rset.getString("codeitemid")))
                    child.setAttribute("xml", url);

                if ("UN".equals(rset.getString("codesetid")))
                    child.setAttribute("icon", "/images/unit.gif");
                else if ("UM".equals(rset.getString("codesetid")))
                    child.setAttribute("icon", "/images/dept.gif");
                else if ("@K".equals(rset.getString("codesetid")))
                    child.setAttribute("icon", "/images/pos_l.gif");

                root.addContent(child);
            }
            /**加载当前机构下的人员*/
            if ("1".equals(flag) && this.isBfirst())
                getEmploys_Href(userview, parentid, root, conn, kq_type);

            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            xmls.append(outputter.outputString(myDocument));
        } catch (Exception ee) {
            ee.printStackTrace();
            throw GeneralExceptionHandler.Handle(ee);
        } finally {
            KqUtilsClass.closeDBResource(rset);
            KqUtilsClass.closeDBResource(conn);
        }
        return xmls.toString();
    }

    private String getPrivSql(UserView userview, String parentid, Connection conn, boolean needOrderBy) {
        StringBuffer strSql = new StringBuffer();

        try {
            ArrayList dblist = getFilterDbList(userview, parentid, conn);
            if (dblist.size() == 0)
                return "";

            /**权限因子*/
            String codeid = parentid.substring(0, 2);
            String codevalue = parentid.substring(2);
            StringBuffer expr = new StringBuffer();
          //2014.11.11 xxd当单位下直接挂有岗位的时候还要对岗位进行判断
            if ("UN".equalsIgnoreCase(codeid)) {
                //2015.08.2 zxj 不显示岗位的情况下，单位下直接挂岗位只需判断没有部门的情况
                if (!isPost) {
                    expr.append("1*2|");
                    expr.append("E0122=`B0110=");
                } else {
                    expr.append("1*2*3|");
                    expr.append("E01A1=`E0122=`B0110=");
                }
            } else if ("UM".equalsIgnoreCase(codeid)) {
                if (!isPost) {
                    expr.append("1|");
                    expr.append("E0122=");
                } else {
                    expr.append("1*2|");
                    expr.append("E01A1=`E0122=");
                }
            } else if ("GP".equalsIgnoreCase(codeid)) {
                expr.append("1*2|");
                expr.append("E0122=`B0110=");
            } else {
                expr.append("1|");
                expr.append("E01A1=");
            }
            expr.append(codevalue + "`");

            ArrayList fieldlist = new ArrayList();
            String strWhere = null;
            String strSelect = null;
            String kqTypeWhr = new KqUtilsClass(conn, userview).getKqTypeWhere(KqConstant.KqType.STOP, true);

            for (int i = 0; i < dblist.size(); i++) {
                strSelect = getSelectString((String) dblist.get(i));
                strSql.append(strSelect);
                
                if (userview.getKqManageValue() != null && !"".equals(userview.getKqManageValue()))
                    strWhere = userview.getKqPrivSQLExpression(expr.toString(), (String) dblist.get(i), fieldlist);
                else
                    strWhere = userview.getPrivSQLExpression(expr.toString(), (String) dblist.get(i), false, true, fieldlist);
                strSql.append(strWhere);
                
                strSql.append(kqTypeWhr);

                /* zxj 不再支持中途修改考勤方式的情形
                ArrayList datelist = RegisterDate.getKqDayList(conn);
                if (datelist != null && datelist.size() == 2) {
                    String start = (String) datelist.get(0);
                    String end = (String) datelist.get(1);
                    strSql.append(" or a0100 in (select DISTINCT a0100 from q03 ");
                    strSql.append(" where nbase='" + dblist.get(i) + "' and  q03z0>='" + start + "' and q03z0<='" + end + "'");
                    strSql.append(")");
                }
                strSql.append(")");
                */
                
                strSql.append(" UNION ");
            }
            strSql.setLength(strSql.length() - 7);
            
            if (needOrderBy)
                strSql.append(" order by dbase desc,a0000");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strSql.toString();
    }

    /**
     * 取得人员库列表
     * @param userview
     * @param conn
     * @return
     */
    private ArrayList getFilterDbList(UserView userview, String parentid, Connection conn) {
        HashMap formHM = new HashMap();
        return RegisterInitInfoData.getB0110Dase(formHM, userview, conn, parentid);
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

    /**
     * 判断组织机构下是否有考勤人员
     * @Title: hasEmployee   
     * @Description:  判断组织机构下是否有考勤人员  
     * @param conn
     * @param userView
     * @param codeSetId
     * @param codeItemId
     * @return
     */
    private boolean hasEmployee(Connection conn, UserView userView, String codeSetId, String codeItemId) {
        boolean hasRec = false;
        
        String strsql = getPrivSql(userView, codeSetId+codeItemId, conn, false);
        if ("".equals(strsql))
            return true;

        ContentDAO dao = new ContentDAO(conn);
        RowSet rset = null;
        try {
            rset = dao.search("select count(*) as reccount from (" + strsql + ") A");
            if (rset.next()) {
                int reccount = rset.getInt("reccount");
                hasRec = 0 < reccount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rset);
        }
        
        return hasRec;
    }
}
