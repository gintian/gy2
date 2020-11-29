package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.interfaces.sys.CreateTreeXml;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * <p>Title:</p>
 * <p>Description:加载树</p> 
 * <p>Company:hjsj</p> 
 * @author dc
 * @version 4.0
 */
public class LoadTreeServlet extends HttpServlet {
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String action=(String)req.getSession().getAttribute("SYS_LOAD_ORG_ACTION");
		String target=(String)req.getParameter("target");
		String id=(String)req.getParameter("id");
		String codeSetID=(String)req.getParameter("codeSetID");  
		String first=(String)req.getParameter("first");
		CreateTreeXml orgxml=new CreateTreeXml(action,target,codeSetID);
		if(first==null|| "".equalsIgnoreCase(first))
			orgxml.setBfirst(false);
		else
		    orgxml.setBfirst(true);
		try
		{
		  UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);			
		  String xmlc=xmlc=orgxml.outOrgEmployTree(userview,id); 
		  resp.setContentType("text/xml;charset=UTF-8");
		  resp.getWriter().println(xmlc);   
		}
		catch(Exception ee)
		{
	      ee.printStackTrace();
		}
		
	}

	
	
}
