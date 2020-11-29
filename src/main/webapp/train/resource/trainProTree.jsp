<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.train.*"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
    response.setContentType("text/xml;charset=UTF-8");
	String code = request.getParameter("code");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(code==null)
		code="";
	TrainProTree tree = new TrainProTree(code,userView);
	try
	{
	  String xmlc=tree.outPutXmlStr();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;  
	}
	catch(Exception e)
	{
      e.printStackTrace();
	}
	
%>
