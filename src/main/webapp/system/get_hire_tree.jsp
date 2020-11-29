
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.CreateHireXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String privflag=(String)request.getParameter("privflag");
	CreateHireXml codexml=new CreateHireXml(codesetid,codeitemid,privflag);
	try
	{
	  String xmlc=codexml.outCodeTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();	  
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>