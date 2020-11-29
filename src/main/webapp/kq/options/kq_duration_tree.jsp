<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.kq.DurationDirectoryByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    	response.setContentType("text/xml;charset=UTF-8");
        String params=request.getParameter("params");
	DurationDirectoryByXml kqxml=new DurationDirectoryByXml("mil_body",params);
	try
	{
	  String xmlc=kqxml.outPutDirectoryXml();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}
%>