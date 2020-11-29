package com.hjsj.hrms.transaction.kq.month_kq;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;

public class ItemDirectoryByXml {

    private String params;

    private String target;

    public ItemDirectoryByXml(String params, String action, String target) {
        this.params = params;
        this.target = target;
    }

    public String outTree() throws GeneralException {

        StringBuffer xmls = new StringBuffer();
        StringBuffer strsql = new StringBuffer();

        RowSet rset = null;
        Connection conn = null; 
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            
            Element root = new Element("TreeNode");
            root.setAttribute("id", "00");
            root.setAttribute("text", "root");
            root.setAttribute("title", "organization");
            Document myDocument = new Document(root);

            strsql.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='27' and ");
            strsql.append(params);
            
            rset = dao.search(strsql.toString());
            while (rset.next()) {
                Element child = new Element("TreeNode");
                child.setAttribute("id", rset.getString("codeitemid"));
                child.setAttribute("text", rset.getString("codeitemdesc"));
                child.setAttribute("title", rset.getString("codeitemdesc"));
                child.setAttribute("href", "javascript:alert('" + rset.getString("codeitemid") + "')");
                child.setAttribute("target", this.target);
                child.setAttribute("xml", "/kq/options/item_list.jsp?params=parentId<>codeitemid and parentid%3D'"
                        + rset.getString("codeitemid") + "'");
                child.setAttribute("icon", "/images/table.gif");
                root.addContent(child);
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
            KqUtilsClass.closeDBResource(rset);
            KqUtilsClass.closeDBResource(conn);
        }
        return xmls.toString();
    }
}
