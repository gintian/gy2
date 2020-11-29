package com.hjsj.hrms.businessobject.kq;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreateKqPersonXML {

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
    /** 人员还是对组织 */
    private String  flag;
    /**
     * 加载应用库标识 =0权限范围内的库 =1权限范围内的登录库
     * */
    private String  dbtype;
    private String  frist;
    private boolean isPost;

    public String getFrist() {
        return frist;
    }

    public void setFrist(String frist) {
        this.frist = frist;
    }

    /**
     * 构造函数
     * 
     * @param params
     * @param action
     * @param target
     * @param flag
     */
    public CreateKqPersonXML(String params, String action, String target, String flag, String dbtype) {
        this.params = PubFunc.keyWord_reback(params);
        this.target = target;
        this.action = action;
        this.flag = flag;
        this.dbtype = dbtype;
    }

    /**
     * 构造函数
     * 
     * @param params
     * @param action
     * @param target
     * @param flag
     */
    public CreateKqPersonXML(String params, String action, String target, String flag, String dbtype, boolean isPost) {
        this.params = PubFunc.keyWord_reback(params);
        this.target = target;
        this.action = action;
        this.flag = flag;
        this.dbtype = dbtype;
        this.isPost = isPost;
    }

    public CreateKqPersonXML(String params, String action, String target, String flag) {
        this.params = PubFunc.keyWord_reback(params);
        this.target = target;
        this.action = action;
        this.flag = flag;
        this.dbtype = "0";
    }

    /**
     * 得到连接的人员节点
     * 
     * @param userview
     * @param parentid
     * @param root
     * @param conn
     */
    private void getEmploys_Href(UserView userview, String parentid, Element root, Connection conn, String kq_type) {
        
        String strsql = getKqEmpSql(userview, parentid, conn, kq_type, false);
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
                    // theaction=this.action+"?b_search=link&a_code=EP"+rset.getString("a0100")+"&nbase="+nbase;
                    theaction = "javascript:org_Emp('" + nbase + "','EP" + rset.getString("a0100") + "')";
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
            if (rset != null)
                try {
                    rset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
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
            if (parentid.toUpperCase().indexOf("GP") == -1) {
                strsql.append("select codesetid,codeitemid,codeitemdesc,childid from organization where ");
                strsql.append(params);
                strsql.append(" and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date ");
                if (isPost) {
                    strsql.append(" and codesetid<>'@K'");
                }
                strsql.append(" ORDER BY a0000,codeitemid ");
                ContentDAO dao = new ContentDAO(conn);
                rset = dao.search(strsql.toString());
                /** 加载组织机构树 */
                while (rset.next()) {
                    Element child = new Element("TreeNode");
                    child.setAttribute("id", rset.getString("codesetid") + rset.getString("codeitemid"));
                    child.setAttribute("text", rset.getString("codeitemdesc"));
                    child.setAttribute("title", rset.getString("codeitemdesc"));
                    if (!(this.action == null || "".equals(this.action))) {
                        if (this.action.indexOf('?') == 0)
                            theaction = this.action + "?a_code=" + rset.getString("codesetid") + rset.getString("codeitemid");
                        else
                            theaction = this.action + "&a_code=" + rset.getString("codesetid") + rset.getString("codeitemid");
                    }
                    
                    boolean haveChild = haveChildNode(conn, 
                            rset.getString("codesetid"), 
                            rset.getString("codeitemid"), 
                            userview, 
                            rset.getString("codesetid")+rset.getString("codeitemid"), 
                            kq_type);
                    
                    if (theaction == null || "".equals(theaction) || !haveChild)
                        child.setAttribute("href", "javascript:void(0)");
                    else
                        child.setAttribute("href", theaction);
                    child.setAttribute("target", this.target);
                    
                    if (haveChild) {
                        String url = "/kq/person/tree?";
                        String url_Encryption = "";
                        url_Encryption="target=" + this.target + "&flag=" + this.flag + "&dbtype=" + this.dbtype + "&frist=2";
                        url_Encryption = url_Encryption + "&kq_type=" + kq_type + "&params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid") + "'";
                        url_Encryption = url_Encryption + "&id=" + rset.getString("codesetid") + rset.getString("codeitemid");
                        url = url+"encryptParam="+PubFunc.encrypt(url_Encryption);
                        child.setAttribute("xml", url);
                    }
                    
                    if ("UN".equals(rset.getString("codesetid")))
                        child.setAttribute("icon", "/images/unit.gif");
                    
                    if ("UM".equals(rset.getString("codesetid")))
                        child.setAttribute("icon", "/images/dept.gif");
                    
                    if ("@K".equals(rset.getString("codesetid")))
                        child.setAttribute("icon", "/images/pos_l.gif");
                    root.addContent(child);
                }
                /** 加载当前机构下的人员 */

                if (this.frist == null || !"1".equals(frist))
                    if ("1".equals(flag))
                        getEmploys_Href(userview, parentid, root, conn, kq_type);
            }
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            xmls.append(outputter.outputString(myDocument));
        } catch (Exception ee) {
            ee.printStackTrace();
            GeneralExceptionHandler.Handle(ee);
        } finally {
            PubFunc.closeResource(rset);
            PubFunc.closeResource(conn);
        }
        // System.out.println(xmls.toString());
        return xmls.toString();
    }

    private String getPrivSql(UserView userview, String parentid, Connection conn, String s_where, boolean forCount) {
        StringBuffer strSql = new StringBuffer();

        try {
            /** 权限因子 */
            String codeid = parentid.substring(0, 2);
            String codevalue = parentid.substring(2);
            StringBuffer expr = new StringBuffer();

            //2014.11.5 xxd当单位下直接挂有岗位的时候还要对岗位进行判断
            if ("UN".equalsIgnoreCase(codeid)) {
                expr.append("1*2*3|");
                expr.append("E01A1=`E0122=`B0110=");
            } else if ("UM".equalsIgnoreCase(codeid)) {
                if (isPost) {
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
            // ArrayList dblist=getFilterDbList(userview,conn);//
            // userview.getPrivDbList();
            HashMap formHM = new HashMap();
            ArrayList dblist = RegisterInitInfoData.getB0110Dase(formHM, userview, conn, parentid);
            if (dblist.size() == 0)
                return "";
            ArrayList fieldlist = new ArrayList();
            String strWhere = null;
            String strSelect = null;

            for (int i = 0; i < dblist.size(); i++) {
                if (userview.getKqManageValue() != null && !"".equals(userview.getKqManageValue()))
                    strWhere = userview.getKqPrivSQLExpression(expr.toString(), (String) dblist.get(i), fieldlist);
                else
                    strWhere = userview.getPrivSQLExpression(expr.toString(), (String) dblist.get(i), false, true, fieldlist);
                strSelect = getSelectString((String) dblist.get(i), forCount);
                strSql.append(strSelect);
                strSql.append(strWhere);

                if (s_where != null && s_where.length() > 0) {
                    strSql.append(" " + s_where);
                }
                strSql.append(" UNION ");
            }
            strSql.setLength(strSql.length() - 7);
            strSql.append(" order by dbase desc,a0000");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strSql.toString();
    }

    /**
     * 取得人员库列表
     * 
     * @param userview
     * @param conn
     * @return
     */
    private ArrayList getFilterDbList(UserView userview, Connection conn) {
        ArrayList list = new ArrayList();
        try {
            ArrayList dblist = userview.getPrivDbList();
            if ("0".equals(this.dbtype))
                return dblist;
            DbNameBo dbbo = new DbNameBo(conn);
            ArrayList logdblist = dbbo.getAllLoginDbNameList();
            StringBuffer strlog = new StringBuffer();
            for (int i = 0; i < logdblist.size(); i++) {
                RecordVo vo = (RecordVo) logdblist.get(i);
                strlog.append(vo.getString("pre"));
                strlog.append(",");
            }
            String str_db = strlog.toString().toUpperCase();
            for (int j = 0; j < dblist.size(); j++) {
                String dbpre = (String) dblist.get(j);
                if (str_db.indexOf(dbpre.toUpperCase()) == -1)
                    continue;
                list.add(dbpre);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private String getSelectString(String dbpre, boolean forCount) {
        StringBuffer strsql = new StringBuffer();
        strsql.append("select");
        if (!forCount) {
            strsql.append(" distinct a0000,");
            strsql.append(dbpre);
            strsql.append("a01.a0100 ,'");
            strsql.append(dbpre);
            strsql.append("' as dbase,");
            strsql.append(dbpre);
            strsql.append("a01.b0110 b0110,e0122,");
            strsql.append(dbpre);
            strsql.append("a01.e01a1 e01a1,a0101 ");
        } else {
            strsql.append(" 1 ");
        }
        
        strsql.append(" ");
        return strsql.toString();
    }

    /**
     * 检查组织机构节点是否还有下级组织或人员
     */
    private boolean haveChildNode(Connection conn, String codeSetId, String codeItemId, UserView userview, String parentid, String kq_type) {
		    boolean haveChild = false;
		    
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String backdate =sdf.format(new Date());
            
		    StringBuilder strsql = new StringBuilder();
		    //检查是否有下级组织机构
		    strsql.append("select 1 from organization");
            strsql.append(" WHERE "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
            if (isPost) {
                strsql.append(" and codesetid<>'@K'");
            }
            strsql.append(" AND codeItemId<>'" + codeItemId + "'");
            strsql.append(" AND codeItemId LIKE '" + codeItemId + "%'");
            
            ResultSet rset = null;
            
            try {
                ContentDAO dao = new ContentDAO(conn);
                rset = dao.search(strsql.toString());
                haveChild = rset.next();
                
                if (haveChild)
                    return haveChild;
                
                if(!"1".equals(flag))
                    return haveChild;
                
                //检查是否有人员
                String sql = getKqEmpSql(userview, parentid, conn, kq_type, true);
                if ("".equals(strsql))
                    return haveChild; 
                
                rset = null;
                rset = dao.search(sql);
                haveChild = rset.next();
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                PubFunc.closeResource(rset);
            }
		    
		    return haveChild;
		}
    
    private String getKqTypeWhr(String kq_type) {
        String kqTypeWhr = "";
        if (kq_type != null && kq_type.length() > 0) {
            kqTypeWhr = " and " + Sql_switcher.isnull(kq_type, "'04'") + "<>'04'";
            if (Sql_switcher.searchDbServer() == Constant.MSSQL)
                kqTypeWhr = kqTypeWhr + " AND " + kq_type + "<>''";
        }
        
        return kqTypeWhr;
    }
    
    private String getKqEmpSql(UserView userview, String parentid, Connection conn, String kq_type, boolean forCount) {        
        String kqTypeWhr = getKqTypeWhr(kq_type);
        String strsql = getPrivSql(userview, parentid, conn, kqTypeWhr, false);
        
        return strsql;
    }
}
