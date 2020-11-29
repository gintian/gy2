<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.hire.CreateOrganizationFilterXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    	response.setContentType("text/xml;charset=UTF-8");
	String params=(String)request.getParameter("params");
	String flag=request.getParameter("flag");
	String edition=request.getParameter("edition");  //招聘2版 扩展的属性
	String straction="/hire/zp_options/pos_filter_login.do";
	if(flag!=null&&flag.equals("org"))
	   straction="/hire/zp_options/pos_filter.do";
	CreateOrganizationFilterXml orgxml=new CreateOrganizationFilterXml(params,straction,"mil_body",flag);
	if(edition!=null&&edition.equals("2"))	  //招聘2版 扩展的属性
		orgxml.setEdition("2");
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




