<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java"%>

<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeXmlOrg"%>
<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeActionXml"%>
<%@ page import="com.hrms.struts.exception.*"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>


<%
	response.setContentType("text/xml;charset=UTF-8");
	String codesetid = (String) request.getParameter("codesetid");
	String codeitemid = (String) request.getParameter("codeitemid");
	String privflag = (String) request.getParameter("privflag");
	String isfirstnode = (String) request.getParameter("isfirstnode");
	UserView userView = (UserView)session.getAttribute("userView");
	CreateCodeActionXml codexml = new CreateCodeActionXml(codesetid,
			codeitemid, "", "", privflag,
			"/system/get_code_treeinputinfo1.jsp",userView);
	try {
		String xmlc = codexml.outCodeItemTree1();
		//create xtree.js treeview.
		//out.println(xmlc);
	    response.getWriter().write(xmlc);
	    response.getWriter().close();			
	} catch (GeneralException ee) {
		ee.printStackTrace();
	}
%>