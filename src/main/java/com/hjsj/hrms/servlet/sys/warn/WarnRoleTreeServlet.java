package com.hjsj.hrms.servlet.sys.warn;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
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
import java.sql.Statement;

public class WarnRoleTreeServlet extends HttpServlet {

	
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String type=(String)req.getParameter("type");
		//DomainTool tool = new DomainTool();
		//Map tempMap = tool.getRoleMap();
		
		//Iterator it = tempMap.keySet().iterator();
		Element root = new Element("TreeNode");
        root.setAttribute("id","$$00");
        root.setAttribute("text","root");
        root.setAttribute("title","root");
        Document myDocument = new Document(root);
        Connection conn = null;
		Statement stmt = null;
		ResultSet rs=null;
		String strOrgSql = "select role_id,role_name from t_sys_role order by norder";
		try{
			conn=AdminDb.getConnection();//getBussTrans().getFrameconn();
			ContentDAO db=new ContentDAO(conn);
			rs =db.search(strOrgSql);	
			String strId = "";
			String strName = "";
			while(rs.next()){
				strId = rs.getString("role_id");
				strName = rs.getString("role_name");
				Element child = new Element("TreeNode");
		        child.setAttribute("id", "RL"+strId);
		        child.setAttribute("text",strName);
		        child.setAttribute("title", strName);
		        child.setAttribute("href", "javascript:void(0)");					
				child.setAttribute("icon","/images/overview_obj.gif");	
				root.addContent(child);	
			}
		}catch (Exception sqle){
			sqle.printStackTrace();
		}		
		finally{
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					;
				}
				stmt = null;
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					;
				}
				conn = null;
			}		
		}
		XMLOutputter outputter = new XMLOutputter();
		Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		StringBuffer treexml = new StringBuffer();	
		treexml.append(outputter.outputString(myDocument));		
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(treexml.toString()); 
	}
}
