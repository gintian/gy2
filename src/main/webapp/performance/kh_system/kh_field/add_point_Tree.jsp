<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.hire.CreatepointunitByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
    response.setContentType("text/xml;charset=UTF-8");
    response.setHeader("Cache-Control", "no-cathe");
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag = request.getParameter("flag");
	String codeid = request.getParameter("a_code");
	String init=request.getParameter("init");
	CreatepointunitByXml cpubx=new CreatepointunitByXml(flag,codeid,init);
	try
	{
	  String xmlc=cpubx.outPutTreeByXml();  //create xtree.js treeview.
	  //out.println(xmlc);
		
	  response.getWriter().write(xmlc.trim());
	  response.getWriter().close();
	}
	catch(Exception e)
	{
      	    e.printStackTrace();
	}
%>