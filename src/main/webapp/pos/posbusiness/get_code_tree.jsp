<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeActionXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.actionform.train.resource.course.CourseForm"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=request.getParameter("codesetid");
	String codeitemid=request.getParameter("codeitemid");
	String privflag=request.getParameter("privflag");
	//String action="/pos/posbusiness/searchposbusinesslist.do";
	String action=request.getParameter("action");
	action=action==null||action.length()==0?"/pos/posbusiness/searchposbusinesslist.do":action;
	String target=request.getParameter("target");
	target=target==null||target.length()==0?"mil_body":target;
	String backdate = request.getParameter("backdate");
	backdate=backdate==null||backdate.length()==0?"":backdate;
	String checked= request.getParameter("checked");
	checked=checked==null||checked.length()==0?"":checked;
	String validateflag=(String)request.getParameter("validateflag");
	validateflag=validateflag==null||validateflag.length()==0?"":validateflag;
	CreateCodeActionXml codexml=new CreateCodeActionXml(codesetid,codeitemid,action,target,privflag,"/pos/posbusiness/get_code_tree.jsp",userView,backdate,checked,validateflag);
	try
	{
	  String xmlc=codexml.outCodeTree();
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>