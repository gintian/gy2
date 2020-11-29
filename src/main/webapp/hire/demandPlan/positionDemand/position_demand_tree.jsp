<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.hire.OrganizationByXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
    response.setContentType("text/xml;charset=UTF-8");
    response.setHeader("Cache-Control", "no-cathe");
    String init=request.getParameter("init");    
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag = request.getParameter("flag");
	String codeid = request.getParameter("codeid");
	String model=request.getParameter("model");
	
	OrganizationByXml organizationByXml = new OrganizationByXml(flag,codeid,model,init);
	try
	{
	  String xmlc=organizationByXml.outPutOrgXml();  //create xtree.js treeview.
	  //out.println(xmlc);
		
	  response.getWriter().write(xmlc.trim());
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>