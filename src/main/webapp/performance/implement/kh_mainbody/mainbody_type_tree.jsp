<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.performance.MainTypeTree"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%  
    response.setContentType("text/xml;charset=UTF-8");
	String planID = request.getParameter("planID");
	if(planID==null)
		planID="";
	MainTypeTree tree= new MainTypeTree(planID);
	try
	{
	  String xmlc=tree.outPutXmlStr("0");  //create xtree.js treeview.
	  response.getWriter().write(xmlc);
	  response.getWriter().close(); 
	}
	catch(Exception e)
	{
      	    e.printStackTrace();
	}
	
%>
