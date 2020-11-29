<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.businessobject.train.OrgTreeBo"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.exception.*" %>

<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    response.setContentType("text/xml;charset=UTF-8");
    String itemid = (String)request.getParameter("itemid");
    String nbase = (String)request.getParameter("nbase");
    String preflag = (String)request.getParameter("preflag");
    String itemkey = (String)request.getParameter("itemkey");

	OrgTreeBo org=new OrgTreeBo();
	try{
	  	String xmlc = org.outViewAreaTree(itemid,userView,nbase,preflag,itemkey);
	  	//out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}catch(GeneralException ee){
      	ee.printStackTrace();
	}
%>