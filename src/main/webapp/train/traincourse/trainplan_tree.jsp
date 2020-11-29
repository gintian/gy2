<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.train.TrainPlanTree"%> 
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>	
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    response.setContentType("text/xml;charset=UTF-8");
    String classId=request.getParameter("classId").toString();
	TrainPlanTree codexml=new TrainPlanTree(classId);
	codexml.setUserView(userView);
	String xmlc = codexml.outPutXmlStr();
	//out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
%>
