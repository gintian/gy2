
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeActionXmlCanBack"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@page import="com.hjsj.hrms.actionform.dtgh.party.person.PersonForm"%>


<%
    	response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String privflag=(String)request.getParameter("privflag");
	String backdate = ((PersonForm)request.getSession().getAttribute("personForm")).getBackdate();
	String action="/dtgh/party/person/searchbusinesslist.do";
	CreateCodeActionXmlCanBack codexml=new CreateCodeActionXmlCanBack(codesetid,codeitemid,action,"nil_body",privflag,"/dtgh/party/person/get_code_tree.jsp",backdate);
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