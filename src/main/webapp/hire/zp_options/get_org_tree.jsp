<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.hire.CreateOrganizationXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    	response.setContentType("text/xml;charset=UTF-8");
	String params=(String)request.getParameter("params");
	String flag=request.getParameter("flag");
	String straction="/hire/zp_options/poslistlogin.do";
	if(flag!=null&&flag.equals("org"))
	   straction="/hire/zp_options/poslist.do";
	CreateOrganizationXml orgxml=new CreateOrganizationXml(params,straction,"mil_body",flag);
	try
	{
	  String xmlc=orgxml.outOrganizationTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>




