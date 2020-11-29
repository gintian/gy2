<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.interfaces.performance.PerObjectTree"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String planId = request.getParameter("planId");
	String codeid = request.getParameter("codeid");
	String codesetid=request.getParameter("codesetid");
	String model=request.getParameter("model");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	PerObjectTree perObjectTree = new PerObjectTree(planId,codeid,codesetid,model,userView);
	try
	{
	  String xmlc=perObjectTree.outPut_Xml();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>