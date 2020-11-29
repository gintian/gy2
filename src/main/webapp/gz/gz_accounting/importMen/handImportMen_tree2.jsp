<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.train.OrgPersonByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>


<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String flag = request.getParameter("flag");
	String id = request.getParameter("id");
	String nbase=request.getParameter("nbase");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	OrgPersonByXml orgPersonByXml = new OrgPersonByXml(flag,id,nbase,"0",userView);
	try
	{
	  String xmlc=orgPersonByXml.outPutXml();  //create xtree.js treeview.
	  if(xmlc!=null&&xmlc.length()>0){
     	  //out.println(xmlc);
     	  response.getWriter().write(xmlc);
	  response.getWriter().close();
     	  }
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>