<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hrms.struts.exception.*" %>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.interfaces.sys.CreateCodeXml"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>


<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String privflag=(String)request.getParameter("privflag");
	String flag = (String)request.getParameter("flag");
	//添加权限
	if(StringUtils.isEmpty(codeitemid) 
	        || ("All".equalsIgnoreCase(codeitemid) && !userView.isSuper_admin()
	         && ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) 
	         || "@K".equalsIgnoreCase(codesetid)))) {
	    codeitemid = userView.getUnitIdByBusi("4");
	    if(StringUtils.isNotEmpty(codeitemid))
	    	codeitemid = PubFunc.getTopOrgDept(codeitemid);
	}
	    
	CreateCodeXml codexml=new CreateCodeXml(codesetid,codeitemid,privflag,userView);
	try
	{
	  String xmlc=codexml.outCodeTree();  //create xtree.js treeview.
	  response.getWriter().write(xmlc);
	  response.getWriter().close();	  
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>