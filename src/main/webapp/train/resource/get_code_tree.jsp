
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeActionXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>


<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String privflag=(String)request.getParameter("privflag");
	String action="/train/resource/trainRescList.do?b_query=link&type=5";
	CreateCodeActionXml codexml=new CreateCodeActionXml(codesetid,codeitemid,action,"mil_body",privflag,"/train/resource/get_code_tree.jsp",userView);
	try
	{
	  String xmlc=codexml.outCodeItemTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>