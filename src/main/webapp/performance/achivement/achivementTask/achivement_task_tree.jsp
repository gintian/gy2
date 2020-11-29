<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.AchivementTaskTree"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
        response.setContentType("text/xml;charset=UTF-8");
  
	String opt = request.getParameter("opt");
	String codeid = request.getParameter("codeid");
	String num = request.getParameter("num1");
	
	
	AchivementTaskTree achivementTaskTree = new AchivementTaskTree(opt,codeid,num);
	try
	{
	  String xmlc=achivementTaskTree.outPut_Xml();
//	  out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>