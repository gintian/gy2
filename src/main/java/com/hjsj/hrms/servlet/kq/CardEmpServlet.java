package com.hjsj.hrms.servlet.kq;

import com.hjsj.hrms.interfaces.kq.CreateKqCardOrganizationXml;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CardEmpServlet extends HttpServlet{
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{ 
          String params=req.getParameter("params");
          String action=req.getParameter("action");
          String target=req.getParameter("target");
          String id=(String)req.getParameter("id");
          String flag=(String)req.getParameter("flag");
  		  String dbtype=(String)req.getParameter("dbtype");
  		  String kq_type=(String)req.getParameter("kq_type");  	
  		  String first=(String)req.getParameter("first");	
          
  		  CreateKqCardOrganizationXml orgxml=new CreateKqCardOrganizationXml(params,action,target,flag,dbtype);
  		  if(first==null|| "".equalsIgnoreCase(first))
			orgxml.setBfirst(false);
		  else
			orgxml.setBfirst(true);
  		
  		  try
  		  {
  		    UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);			
  		    String xmlc=orgxml.outOrgEmpTree(userview,id,kq_type); 
  		    resp.setContentType("text/xml;charset=UTF-8");
  		    resp.getWriter().println(xmlc);   
  		 }
  		catch(Exception ee)
  		{
  	      ee.printStackTrace();
  		}
    }	
}
