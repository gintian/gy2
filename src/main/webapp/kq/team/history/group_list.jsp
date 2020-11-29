<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.kq.KqGroupByXml"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%
    	response.setContentType("text/xml;charset=UTF-8");
	    String params=(String)request.getParameter("params");
	    String straction=(String)request.getParameter("straction");
	    if(straction==null||straction.length()<0)
	       straction="/kq/team/history/search_array_data.do";
            String codetiem=(String)request.getParameter("codetiem");
	    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	
 
	KqGroupByXml  orgxml=new KqGroupByXml (params,straction,"mil_body",codetiem,userView);
	try
	{
	  String xmlc=orgxml.outTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}
	catch(Exception ee)
	{
      	    ee.printStackTrace();
	}
%>