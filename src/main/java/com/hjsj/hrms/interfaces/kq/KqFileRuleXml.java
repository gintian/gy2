package com.hjsj.hrms.interfaces.kq;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;

public class KqFileRuleXml {
	private String params;
    
	 private String target; 
	   
	 public KqFileRuleXml(String target,String params) 
	 {
	       this.params=params;
	       this.target=target;
	  }
	 public String outPutFileRuleXml()throws GeneralException
	 {
		 StringBuffer xmls = new StringBuffer();
		 StringBuffer sql=new StringBuffer();
	     ResultSet rset = null;
	     Connection conn=AdminDb.getConnection();
	     Element root = new Element("TreeNode");
	     root.setAttribute("id","00");
	     root.setAttribute("text","root");
	     root.setAttribute("title","kq_data_rule");
	     Document myDocument = new Document(root);
	     String actionname="/kq/machine/kq_rule_data.do";
	     String theaction=null;  
		 sql.append("select rule_id,rule_name ");
		 sql.append("from kq_data_rule ");
		 sql.append(" order by rule_id");
		 try
		 {
		     ContentDAO dao = new ContentDAO(conn);             
	         rset = dao.search(sql.toString());
	         while(rset.next())
	         {
	        	 Element child = new Element("TreeNode");
		         child.setAttribute("id", rset.getString("rule_id"));
		         child.setAttribute("text", rset.getString("rule_name"));
		         child.setAttribute("title", rset.getString("rule_name"));
		         theaction=actionname+"?b_query=link&encryptParam="+PubFunc.encrypt("rule_id="+rset.getString("rule_id"));
		         child.setAttribute("href", theaction);
		         child.setAttribute("target", this.target);
		         child.setAttribute("icon","/images/icon_wsx.gif");
		         root.addContent(child);
	         }
	         XMLOutputter outputter = new XMLOutputter();
	         Format format=Format.getPrettyFormat();
	         format.setEncoding("UTF-8");
	         outputter.setFormat(format);
	         xmls.append(outputter.outputString(myDocument));
		 }catch(Exception e)
		 {
			 e.printStackTrace();
		 }finally {
		     PubFunc.closeResource(rset);
		     PubFunc.closeResource(conn);
		 }
		 
		 return xmls.toString();
	 }
}
