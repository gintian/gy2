<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.performance.HandSelTree"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%@ page import="java.sql.*"%>
<%
    response.setContentType("text/xml;charset=UTF-8");
	String type = request.getParameter("type");
	String code = request.getParameter("code");
	String planId = request.getParameter("planID");
	Connection conn=null;
	if(code==null)
		code="";
	 
	try 
	{
	  conn = (Connection) AdminDb.getConnection();
	  HandSelTree tree = new HandSelTree(planId,code,type,conn);
	  String xmlc=tree.outPutXmlStr();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close(); 
	}
	catch(Exception e)
	{
      	    e.printStackTrace();
	}finally
	{
	  if(conn!=null)
	   	conn.close();	
	}
	
%>