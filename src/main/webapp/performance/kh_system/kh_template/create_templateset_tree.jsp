<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.SelectTemplateSetTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String templatesetid = request.getParameter("templatesetid");
	String subsys_id=request.getParameter("subsys_id");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	SelectTemplateSetTree stst = new SelectTemplateSetTree(userView,templatesetid,subsys_id);
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