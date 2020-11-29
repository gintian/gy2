
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.CreateOrganizationXml"%>
<%@ page import="com.hrms.struts.exception.*" %>

<%

    	response.setContentType("text/xml;charset=UTF-8");
	String params=(String)request.getParameter("params");
	String flag=request.getParameter("flag");
	String straction="/system/security/assign_login.do";
	
	if(flag!=null&&flag.equals("org"))
	   straction="/system/security/assign_org_login.do";
	   
	if(flag!=null&&flag.equals("perObject"))
	   straction="/selfservice/performance/performanceImplement.do";    
 
	CreateOrganizationXml orgxml=new CreateOrganizationXml(params,straction,"mil_body",flag);
	try
	{
		
	  String xmlc=orgxml.outOrganizationTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();	 
	}
	catch(Exception ee)
	{
      	    ee.printStackTrace();
	}

%>




