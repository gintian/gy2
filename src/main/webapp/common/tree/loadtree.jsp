
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.LoadTreeNodeXml"%>
<%@ page import="com.hrms.struts.exception.*" %>


<%
    	response.setContentType("text/xml;charset=UTF-8");
	String params=(String)request.getParameter("params");
	//System.out.println("------>params="+params);	
	String parentid=(String)request.getParameter("parentid");
	//System.out.println("------>parentid="+parentid);
	String fieldname=request.getParameter("fieldname");
	//System.out.println("fieldname" + fieldname);
	String type=request.getParameter("type");
	//System.out.println("type" + type);
	//managepriv,String target,String action,String params,boolean isSuperuser,String userbase,String parentid,String src
	LoadTreeNodeXml orgxml=new LoadTreeNodeXml(params,true,"managepriv","1",fieldname,type,"action","treefrm","/common/tree/loadtree.jsp",parentid,"usr");
	try
	{
		String xmlc=orgxml.outTreeNode();  //create xtree.js treeview.
		//out.println(xmlc);
	    response.getWriter().write(xmlc);
	    response.getWriter().close();	
	}
	catch(Exception ee)
	{
      	    ee.printStackTrace();
	}

%>




