<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.performance.MainTypeTree"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%  
    response.setContentType("text/xml;charset=UTF-8");
	MainTypeTree tree= new MainTypeTree();
	try
	{
	  String xmlc=tree.outPutGenRelationXmlStr();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close(); 
	}
	catch(Exception e)
	{
       e.printStackTrace();
	}
	
%>
