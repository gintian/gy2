package com.hjsj.hrms.servlet.kq;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.CreateKqCardXML;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EmpSendCardServlet extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{ 
          String params=req.getParameter("params");
          String action=req.getParameter("action");
          String target=req.getParameter("target");
          String id=(String)req.getParameter("id");
          String flag=(String)req.getParameter("flag");
  		  String dbtype=(String)req.getParameter("dbtype");
  		  String kq_type=(String)req.getParameter("kq_type");
  		  String kq_card=(String)req.getParameter("kq_card");
  		  
  		  // 考勤组织机构树是否显示岗位
  		  String viewPost = req.getParameter("viewPost");
  		  boolean isPost = true;
          if ("kq".equalsIgnoreCase(viewPost)) {
          	KqParameter para = new KqParameter();
          	if ("1".equalsIgnoreCase(para.getKq_orgView_post())) {
          		isPost = false;
          	} else {
          		isPost = true;
          	}
          } else {
          	isPost = true;
          }
  		  
  		  CreateKqCardXML orgxml=null;
  		  if ("kq".equalsIgnoreCase(viewPost)) 
  			orgxml = new CreateKqCardXML(params,action,target,flag,dbtype,isPost,viewPost);
  		  else
  			orgxml = new CreateKqCardXML(params,action,target,flag,dbtype);
  		  try
  		  {
  		    UserView userview=(UserView) req.getSession().getAttribute(WebConstant.userView);			
  		    String xmlc=orgxml.outOrgEmpTree(userview,id,kq_type,kq_card); 
  		    resp.setContentType("text/xml;charset=UTF-8");
  		    resp.getWriter().println(xmlc);   
  		 }
  		catch(Exception ee)
  		{
  	      ee.printStackTrace();
  		}
    }	 


}
