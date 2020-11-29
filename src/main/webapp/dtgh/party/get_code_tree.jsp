
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeActionXmlCanBack"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@page import="com.hjsj.hrms.actionform.dtgh.party.PartyBusinessForm"%>


<%
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String privflag=(String)request.getParameter("privflag");
	PartyBusinessForm partyBusinessForm =(PartyBusinessForm)request.getSession().getAttribute("partyBusinessForm");
	String backdate = partyBusinessForm.getBackdate();
	String param = partyBusinessForm.getParam();
	String action="/dtgh/party/searchpartybusinesslist.do";
	CreateCodeActionXmlCanBack codexml=new CreateCodeActionXmlCanBack(codesetid,codeitemid,action,"mil_body",privflag,"/dtgh/party/get_code_tree.jsp",backdate,param);
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