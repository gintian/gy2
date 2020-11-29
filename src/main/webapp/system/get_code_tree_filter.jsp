
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>


<%
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String privflag=(String)request.getParameter("privflag");
	CreateCodeXml codexml=new CreateCodeXml(codesetid,codeitemid,privflag);
	try
	{
	  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	  String xmlc=codexml.outCodeTreesx(userView);  //create xtree.js treeview.
	 // out.println(xmlc);
	    response.getWriter().write(xmlc);
	    response.getWriter().close();	  
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>