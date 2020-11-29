<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.sys.CteateProjectXml"%>
<%@ page import="com.hrms.struts.exception.*" %> 

<%
    response.setContentType("text/xml;charset=UTF-8");
	CteateProjectXml codexml=new CteateProjectXml();
	try{
	  	String xmlc = codexml.levelTree();  //create xtree.js treeview.
	  	//out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}catch(GeneralException ee){
      	ee.printStackTrace();
	}

%>