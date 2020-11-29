package com.hjsj.hrms.servlet.kq;

import com.hjsj.hrms.businessobject.kq.CreateKqPersonXML;
import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoadKqPersonTreeServlet extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{ 
          String params=req.getParameter("params");
          String action=req.getParameter("action");
          String target=req.getParameter("target");
          String id=(String)req.getParameter("id");
          String flag=(String)req.getParameter("flag");
  		  String dbtype=(String)req.getParameter("dbtype");
  		  String kq_type=(String)req.getParameter("kq_type");  	
  		  String frist=(String)req.getParameter("frist");
  		  
  		  CreateKqPersonXML orgxml=null;
  		  KqParameter para = new KqParameter();
    	  if ("1".equalsIgnoreCase(para.getKq_orgView_post())) {
    		  orgxml=new CreateKqPersonXML(params,action,target,flag,dbtype,true);
    	  }else{
    		  orgxml=new CreateKqPersonXML(params,action,target,flag,dbtype,false);
    	  }
  		  try
  		  {
  		    UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);			
  		    orgxml.setFrist(frist);
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
