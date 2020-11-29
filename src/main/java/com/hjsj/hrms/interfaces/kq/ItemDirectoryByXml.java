/*
 * Created on 2005-12-31
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.interfaces.kq;

import com.hjsj.hrms.businessobject.kq.options.KqItem;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;

public class ItemDirectoryByXml {

    private String params;

    private String action;

    private String target;

    public ItemDirectoryByXml(String params, String action, String target) {
        this.params = PubFunc.keyWord_reback(params);
        this.target = target;
        this.action = action;
    }

    public String outTree() throws GeneralException {
        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();
        ResultSet rset = null;
        String theaction = null;

        Connection conn = AdminDb.getConnection();
        try {
            KqItem kqItem = new KqItem(conn);
            
            Element root = new Element("TreeNode");
            root.setAttribute("id", "00");
            root.setAttribute("text", "root");
            root.setAttribute("title", "organization");
            
            strsql.append("select codesetid,codeitemid,codeitemdesc from codeitem");
            strsql.append(" where codesetid='27' and ");
            strsql.append(params);
            strsql.append(" ORDER BY a0000,codeitemid");
            
            ContentDAO dao = new ContentDAO(conn);
            rset = dao.search(strsql.toString());
            while (rset.next()) {
                Element child = new Element("TreeNode");
                child.setAttribute("id", rset.getString("codeitemid"));
                child.setAttribute("text", rset.getString("codeitemdesc"));
                child.setAttribute("title", rset.getString("codeitemdesc"));
                // returnFlag为编辑考勤项目保存时返回上一个页面的标志
                theaction = this.action + "?b_query=link&encryptParam="+PubFunc.encrypt("codeitemid=" + rset.getString("codeitemid") + "&returnFlag=" + rset.getString("codeitemid"));
                child.setAttribute("href", theaction);
                child.setAttribute("target", this.target);
                //有下级节点时，树节点前显示“+”
                if (kqItem.haveChildKqItem(rset.getString("codeitemid")))
                  child.setAttribute("xml", "/kq/options/item_list.jsp?params=parentId<>codeitemid and parentid%3D'" + rset.getString("codeitemid") + "'");
                child.setAttribute("icon", "/images/table.gif");
                root.addContent(child);
            }

            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);

            Document myDocument = new Document(root);
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
}
