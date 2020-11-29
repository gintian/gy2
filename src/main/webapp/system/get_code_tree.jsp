
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>

<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String privflag=(String)request.getParameter("privflag");
	String hirechannel=(String)request.getParameter("hirechannel");
	String isValidCtr =(String)request.getParameter("isValidCtr");
	CreateCodeXml codexml=new CreateCodeXml(codesetid,codeitemid,privflag,userView);
	if(hirechannel!=null && hirechannel.trim().length()>0){
		codexml.setHirechannel(hirechannel);
	}
	if(isValidCtr!=null && isValidCtr.trim().length()>0){
        codexml.setIsValidCtr(isValidCtr);
    }
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