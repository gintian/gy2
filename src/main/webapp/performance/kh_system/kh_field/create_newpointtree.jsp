<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.SelectPointTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.interfaces.performance.SelectTyPointTree"%>
<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String unitcode = request.getParameter("unitcode");
	String flag = request.getParameter("flag");
	String pointsetid=request.getParameter("pointsetid");
	String subsys_id=request.getParameter("subsys_id");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	SelectTyPointTree perPointByTree = new SelectTyPointTree(flag,pointsetid,userView,subsys_id,unitcode);
	try
	{
	  String xmlc=perPointByTree.getXmlTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>