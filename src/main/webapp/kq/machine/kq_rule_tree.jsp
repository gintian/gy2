<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.kq.KqFileRuleXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    	response.setContentType("text/xml;charset=UTF-8");
        String params=request.getParameter("params");
	KqFileRuleXml kqxml=new KqFileRuleXml("mil_body","");
	try
	{
	  String xmlc=kqxml.outPutFileRuleXml();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}
%>