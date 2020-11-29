package com.hjsj.hrms.interfaces.kq;

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
import java.sql.SQLException;

public class DurationDirectoryByXml {
	
	 private String params;
	    
	 private String target; 
	   
	 public DurationDirectoryByXml(String target,String params) {
	       this.params=params;
	       this.target=target;
	  }
	    
	    /**
	     * 输出考勤期间目录结构树
	     * @return
	     */
	  public String outPutDirectoryXml()throws GeneralException
	   {
	        StringBuffer xmls = new StringBuffer();
	        StringBuffer strsql = new StringBuffer();        
	        ResultSet rset = null;
	        Connection conn=AdminDb.getConnection();
	        Element root = new Element("TreeNode");
	        root.setAttribute("id","00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","kqduration");
	        Document myDocument = new Document(root);
	        String actionname="/kq/options/duration_details.do";
	        String theaction=null;        
	        try
	        {
	          strsql.append("select kq_year from kq_duration ");
	          strsql.append(" group by kq_year");
	          
	          ContentDAO dao=new ContentDAO(conn);
	          rset = dao.search(strsql.toString());
	          while (rset.next())
	          {
	            Element child = new Element("TreeNode");
	            child.setAttribute("id", rset.getString("kq_year"));
	            child.setAttribute("text", rset.getString("kq_year"));
	            child.setAttribute("title", rset.getString("kq_year"));
	            theaction=actionname+"?b_query=link&encryptParam="+PubFunc.encrypt("kq_year="+rset.getString("kq_year"));
	            child.setAttribute("href", theaction);
	            child.setAttribute("target", this.target);
	            child.setAttribute("icon","/images/close.png");
	            root.addContent(child);
	          }

	          XMLOutputter outputter = new XMLOutputter();
	          Format format=Format.getPrettyFormat();
	          format.setEncoding("UTF-8");
	          outputter.setFormat(format);
	          xmls.append(outputter.outputString(myDocument));
	        }
	        catch (SQLException ee)
	        {
	          ee.printStackTrace();
	          GeneralExceptionHandler.Handle(ee);
	        }
	        finally
	        {
	            PubFunc.closeResource(rset);
	            PubFunc.closeResource(conn);
	        }
	        return xmls.toString();
	    }

}
