package com.hjsj.hrms.servlet.orgtree;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.interfaces.kq.CreateKqOrganizationXml;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrgEmptreeServlet  extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req,resp);
	}

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
  		  
  		 // 组织机构中是否显示岗位
  		  KqParameter param = new KqParameter();
  		  boolean isPost = true;
	  	  if ("1".equals(param.getKq_orgView_post())) {
	    		isPost = false;
	      } else {
	    		isPost = true;
	      }
  		  CreateKqOrganizationXml orgxml = new CreateKqOrganizationXml(params,action,target,flag,dbtype,isPost);
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
