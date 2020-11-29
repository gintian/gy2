package com.hjsj.hrms.interfaces.kq;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.GroupsArray;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class KqGroupByXml {
    private String   params;
    private String   action;
    private String   target;
    private String   codeitem;
    private UserView userView;

    public KqGroupByXml(String params, String action, String target, String codeitem, UserView userView) {
        this.params = params;
        this.target = target;
        this.action = PubFunc.keyWord_reback(action);
        if (codeitem == null || codeitem.length() <= 0)
            codeitem = "UN";
        this.codeitem = codeitem;
        this.userView = userView;
    }

    public String outTree() throws GeneralException {

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
        try {
            ContentDAO dao = new ContentDAO(conn);

            if ("gp".equals(params))// 班组
            {
                strsql = new StringBuffer();
                GroupsArray groupsArray = new GroupsArray(conn, this.userView);
                String groupOrgWhr = groupsArray.getGroupOrgWhr(this.codeitem);
                strsql.append("select group_id,name,org_id from kq_shift_group");
                if (groupOrgWhr != null && groupOrgWhr.length() > 0) {
                    strsql.append(" WHERE ");
                    strsql.append(groupOrgWhr);
                }
                strsql.append(" order by ");
                strsql.append(Sql_switcher.substr("org_id", "2", Sql_switcher.length("org_id") + "-2"));
                strsql.append(",group_id");

                rset = dao.search(strsql.toString());
                while (rset.next()) {

                    if ((this.userView.isHaveResource(IResourceConstant.KQ_CLASS_GROUP, rset.getString("group_id")))) {
                        Element child = new Element("TreeNode");
                        child.setAttribute("id", rset.getString("group_id"));
                        child.setAttribute("text", rset.getString("name"));
                        child.setAttribute("title", rset.getString("name"));
                        theaction = this.action + "?b_search=link&encryptParam="+PubFunc.encrypt("a_code=GP" + rset.getString("group_id"));
                        child.setAttribute("href", theaction);
                        child.setAttribute("target", this.target);
                        child.setAttribute("xml", "/kq/team/array/group_list.jsp?params="+ PubFunc.encrypt("GP" + rset.getString("group_id")) + "&straction=" + this.action);
                        child.setAttribute("icon", "/images/admin.gif");
                        root.addContent(child);
                    }

                }
            } else if (params.length() > 2) {
                //暂停考勤人员条件
                String kqTypeWhr = new KqUtilsClass(conn, this.userView).getKqTypeWhere(KqConstant.KqType.STOP, true);
                
                ArrayList dlist = getDbase(conn);
                if (dlist != null && dlist.size() > 0) {
                    for (int i = 0; i < dlist.size(); i++) {
                        CommonData vo = (CommonData) dlist.get(i);
                        String dbper = vo.getDataValue();
                        KqParameter para = new KqParameter(this.userView, "", conn);
                        HashMap hashmap = para.getKqParamterMap();
                        String g_no = (String) hashmap.get("g_no");
                        strsql = new StringBuffer();
                        String group_id = params.substring(2);
                        strsql.append("select A.a0100,nbase ,A.a0101," + g_no + " from kq_group_emp KQ," + dbper + "A01 A");
                        strsql.append(" where group_id='" + group_id + "'");
                        strsql.append(" and nbase='" + dbper + "' and KQ.a0100=A.a0100");
                        strsql.append(kqTypeWhr);
                        
                        String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbper);
                        if (!this.userView.isSuper_admin()) {
                            String privCode = RegisterInitInfoData.getKqPrivCode(userView);
                            String privCodeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
                            if (!"".equals(privCodeValue)) {
                                if (privCode != null && "UN".equals(privCode))
                                    strsql.append(" and A.b0110 like '" + privCodeValue + "%'");
                                else if (privCode != null && "UM".equals(privCode))
                                    strsql.append(" and A.e0122 like '" + privCodeValue + "%'");
                                else if (privCode != null && "@K".equals(privCode))
                                    strsql.append(" and A.e01a1 like '" + privCodeValue + "%'");
                            }
                            strsql.append(" and   KQ.a0100 in(select distinct a0100 " + whereA0100In + ") ");
                        }

                        rset = dao.search(strsql.toString());
                        while (rset.next()) {
                            String nbase = rset.getString("nbase");
                            String a0100 = rset.getString("a0100");
                            String gno = rset.getString(g_no);
                            String a0101 = rset.getString("a0101");
                            if (gno != null && !"".equalsIgnoreCase(gno))
                                a0101 += "(" + gno + ")";
                            Element child = new Element("TreeNode");
                            child.setAttribute("id", nbase + a0100);
                            if (a0101 == null)
                                a0101 = "";
                            child.setAttribute("text", a0101);
                            child.setAttribute("title", a0101);
                            child.setAttribute("target", this.target);
                            theaction = this.action + "?b_search=link&encryptParam="+PubFunc.encrypt("a_code=EP" + rset.getString("a0100") + "&nbase=" + nbase);
                            child.setAttribute("href", theaction);
                            child.setAttribute("icon", "/images/man.gif");
                            root.addContent(child);
                        }
                    }
                } else {
                    strsql = new StringBuffer();
                    String group_id = params.substring(2);
                    strsql.append("select a0100,nbase ,a0101 from kq_group_emp");
                    strsql.append(" where group_id='" + group_id + "'");
                    if (!this.userView.isSuper_admin()) {
                        String privCodeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
                        if (!"".equals(privCodeValue)) {
                            strsql.append(" and e0122 like '" + privCodeValue + "%'");
                        }
                    }
                    rset = dao.search(strsql.toString());
                    while (rset.next()) {
                        String nbase = rset.getString("nbase");
                        String a0100 = rset.getString("a0100");
                        String a0101 = rset.getString("a0101");
                        Element child = new Element("TreeNode");
                        child.setAttribute("id", nbase + a0100);
                        if (a0101 == null)
                            a0101 = "";
                        child.setAttribute("text", a0101);
                        child.setAttribute("title", a0101);
                        child.setAttribute("target", this.target);
                        theaction = this.action + "?b_search=link&encryptParam="+PubFunc.encrypt("a_code=EP" + rset.getString("a0100") + "&nbase=" + nbase);
                        child.setAttribute("href", theaction);
                        child.setAttribute("icon", "/images/man.gif");
                        root.addContent(child);
                    }
                }

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
        return xmls.toString();
    }

    private ArrayList getDbase(Connection conn) throws GeneralException {

        StringBuffer stb = new StringBuffer();
        ContentDAO dao = new ContentDAO(conn);
        HashMap hash = new HashMap();
        ArrayList dbaselist = RegisterInitInfoData.getDase3(hash, this.userView, conn);
        ArrayList slist = new ArrayList();
        // String[] base=dlist.split(",");
        RowSet rs = null;
        try {
            stb.append("select * from dbname");
            rs = dao.search(stb.toString());
            while (rs.next()) {
                String dbpre = rs.getString("pre");
                for (int i = 0; i < dbaselist.size(); i++) {
                    String userbase = dbaselist.get(i).toString();
                    if (dbpre != null && dbpre.equalsIgnoreCase(userbase)) {
                        CommonData vo = new CommonData(rs.getString("pre"), rs.getString("dbname"));
                        slist.add(vo);
                    }
                }
            }
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return slist;

    }
}
