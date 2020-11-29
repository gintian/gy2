
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeXmlOrg"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>

<%
	UserView userView = (UserView) session
					.getAttribute("userView");
	boolean isAll = (userView.getManagePrivCode()!=null&&userView.getManagePrivCode().length()>0);
    if(userView.isSuper_admin())
    	isAll=true;
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String privflag=(String)request.getParameter("privflag");
	String isfirstnode=(String)request.getParameter("isfirstnode");
	CreateCodeXmlOrg codexml=new CreateCodeXmlOrg(codesetid,codeitemid,privflag,isfirstnode,isAll);
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