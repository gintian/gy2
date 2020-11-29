<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.sys.CreateVorgXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	codesetid=codesetid!=null?codesetid:"";
	String codeitemid=(String)request.getParameter("codeitemid");
	codeitemid=codeitemid!=null?codeitemid:"";
	String type=(String)request.getParameter("type");
	type=type!=null?type:"";
	CreateVorgXml codexml=new CreateVorgXml();
	try
	{
	  String xmlc=codexml.outCodeTree(codesetid,codeitemid,type);  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>
