<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.DataCollectHandImportMenXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>


<%
    response.setContentType("text/xml;charset=UTF-8");
  
	String flag = request.getParameter("flag");
	String id = request.getParameter("id");
	String nbase=request.getParameter("nbase");
	String fieldsetid=request.getParameter("fieldsetid");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	DataCollectHandImportMenXml Xml = new DataCollectHandImportMenXml(flag,id,nbase,userView,fieldsetid);
	try
	{
	  String xmlc=Xml.outPutXml(); 
	  if(xmlc!=null&&xmlc.length()>0){
     	 response.getWriter().write(xmlc);
	  response.getWriter().close();
	  }
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>