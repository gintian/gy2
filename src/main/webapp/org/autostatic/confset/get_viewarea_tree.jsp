<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.sys.CteateProjectXml"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.exception.*" %>

<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView); 
    response.setContentType("text/xml;charset=UTF-8");
	String codeitemid=request.getParameter("codeitemid");
	codeitemid = codeitemid!=null&&codeitemid.trim().length()>0?codeitemid:"";

	CteateProjectXml codexml=new CteateProjectXml();
	try{
	  	String xmlc = codexml.outViewAreaTree(codeitemid,userView);  
	  	//out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}catch(GeneralException ee){
      	ee.printStackTrace();
	}
%>