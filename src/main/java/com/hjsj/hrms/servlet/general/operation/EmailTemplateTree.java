package com.hjsj.hrms.servlet.general.operation;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EmailTemplateTree extends HttpServlet {
	protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException
	{
		doPost(req,resp);
	}
	protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException
	{
		String templatesetid=(String)req.getParameter("templatesetid");
		StringBuffer xmlTree = new StringBuffer();
		xmlTree=this.getXml(templatesetid);
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(xmlTree.toString());  
	}
    private StringBuffer getXml(String templatesetid)
    {
    	StringBuffer buf = new StringBuffer();
    	try
    	{
    		Element root = new Element("TreeNode");
			root.setAttribute("id","$$00");
			root.setAttribute("text","root");
			root.setAttribute("title","root");
			Document myDocument = new Document(root);
			Element child = new Element("TreeNode");
			child.setAttribute("id","1");
			child.setAttribute("text","人员模板");
			child.setAttribute("title","人员模板");
			child.setAttribute("target","mil_body");
			child.setAttribute("href","/general/email_template/addEmailTemplate.do?b_init=init&opt=edit&templateId=first&type=1&nmodule=1");
			child.setAttribute("icon","/images/img_l.gif");
			root.addContent(child);
			
			Element child1 = new Element("TreeNode");
			child1.setAttribute("id","2");
			child1.setAttribute("text","薪资发放模板");
			child1.setAttribute("title","薪资发放模板");
			child1.setAttribute("target","mil_body");
			child1.setAttribute("href","/general/email_template/addEmailTemplate.do?b_init=init&opt=edit&templateId=first&type=1&nmodule=2");
			child1.setAttribute("icon","/images/img_l.gif");
			root.addContent(child1);
			
			Element child2 = new Element("TreeNode");
			child2.setAttribute("id","5");
			child2.setAttribute("text","薪资审批模板");
			child2.setAttribute("title","薪资审批模板");
			child2.setAttribute("target","mil_body");
			child2.setAttribute("href","/general/email_template/addEmailTemplate.do?b_init=init&opt=edit&templateId=first&type=1&nmodule=5");
			child2.setAttribute("icon","/images/img_l.gif");
			root.addContent(child2);
			
			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(myDocument));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return buf;
    }
}
