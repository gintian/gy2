package com.hjsj.hrms.servlet.duty;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadSdutyTreeServlet extends HttpServlet{

	private Category cat = Category.getInstance(this.getClass());
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		StringBuffer xmls = new StringBuffer();
		String codesetid=req.getParameter("codesetid");
		String codeitemid = req.getParameter("codeitemid");
		String param = req.getParameter("param");
		String target = req.getParameter("target");
		req.removeAttribute("target");
		Connection conn=null;
		ResultSet rs = null;
		try {
			 conn=AdminDb.getConnection();
			 ContentDAO dao = new ContentDAO(conn);
			 Date now = new Date();
             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
             String date = sdf.format(now);
			StringBuffer sql=new StringBuffer("select  c.codeitemid,c.codeitemdesc,c.childid,");
			sql.append(" (select COUNT(*) from codeitem where codesetid='"+codesetid+"' and parentid=c.codeitemid and parentid<>codeitemid and "+Sql_switcher.dateValue(date)+" between start_date and end_date) childNum");
			sql.append(" from codeitem c ");
			if(param!=null && "root".equals(param)){
				sql.append(" where c.codesetid='"+codesetid+"' and c.codeitemid=c.parentid and ");
			}else{
				sql.append(" where c.codesetid='"+codesetid+"' and c.parentid='"+codeitemid+"' and  c.codeitemid<>c.parentid and ");
			}
			
			sql.append( Sql_switcher.dateValue(date)+" between start_date and end_date");
		    
			rs = dao.search(sql.toString());
			
			Element root = new Element("TreeNode");
	        root.setAttribute("id","$$00");
	        root.setAttribute("text","root");
	        root.setAttribute("title","codeitem");
	        Document doc = new Document(root);
	        
			while(rs.next()){
				 String codeid = rs.getString("codeitemid");
				 String codedesc = rs.getString("codeitemdesc");
				 String childid = rs.getString("childid");
				 int childNum = rs.getInt("childNum");
				 Element child = new Element("TreeNode");
				 child.setAttribute("id",codesetid+codeid);
				 child.setAttribute("text",codedesc);
					 
					 child.setAttribute("icon","/images/pos_l.gif");
					 
					 if(childNum >0){
						 child.setAttribute("xml","/servlet/sduty/getSdutyTree?param=child&target="+target+"&codesetid="+codesetid+"&codeitemid="+codeid);
						 child.setAttribute("title","0");
					 }else
						 child.setAttribute("title","1");
				 
				 root.addContent(child);
			}
			
			XMLOutputter outputter = new XMLOutputter();
	          Format format=Format.getPrettyFormat();
	          format.setEncoding("UTF-8");
	          outputter.setFormat(format);
	          xmls.append(outputter.outputString(doc));
			
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
		    PubFunc.closeResource(rs);
			PubFunc.closeResource(conn);
		}
		
		cat.debug("catalog xml" + xmls.toString());
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().write(xmls.toString());
		resp.getWriter().close();
	}
    
}
