<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.transaction.general.inform.emp.view.CreateMultimediaTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
    response.setContentType("text/xml;charset=UTF-8");
    String isvisible=(String)request.getParameter("isvisible");
	String a0100=(String)request.getParameter("a0100");
	String dbname=(String)request.getParameter("dbname");
	String multimediaflag=(String)request.getParameter("multimediaflag");
	String action="/general/inform/multimedia/opermultimedia.do";
	String tree = "/general/multimedia/multimedia_tree.jsp";
	String kind = (String)request.getParameter("kind");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	CreateMultimediaTree codexml=new CreateMultimediaTree(a0100,dbname,multimediaflag,action,kind,tree,userView,isvisible);
	try{ 
	  	String xmlc = "";
	  	   xmlc = codexml.getMultimediaTree();  //create xtree.js treeview.
	   response.getWriter().write(xmlc);
	    response.getWriter().close();
	}catch(GeneralException ee){
      	ee.printStackTrace();
	}
%>