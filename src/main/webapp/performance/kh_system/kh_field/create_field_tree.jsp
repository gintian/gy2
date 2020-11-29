<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.transaction.performance.kh_system.kh_field.SelectFieldTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String pointsetid = request.getParameter("pointsetid");
	String subsys_id=request.getParameter("subsys_id");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	SelectFieldTree stst = new SelectFieldTree(userView,pointsetid,subsys_id);
	try
	{
	  String xmlc=stst.GetTreeXMLString();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>