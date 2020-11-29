<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeActionXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.dao.utility.DateUtils"%>
<%@ page import="java.util.Date"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String privflag=(String)request.getParameter("privflag");
	//String action="/pos/posbusiness/searchposbusinesslist.do";
	String action=(String)request.getParameter("action");
	action=action==null||action.length()==0?"/pos/posbusiness/searchposbusinesslist.do":action;
	String target=(String)request.getParameter("target");
	target=target==null||target.length()==0?"mil_body":target;
	
	CreateCodeActionXml codexml;
	if(codesetid.equals("68")){
	    String backdate = DateUtils.format(new Date(), "yyyy-MM-dd");
	    codexml = new CreateCodeActionXml(codesetid,codeitemid,action,target,privflag,
	            "/pos/posbusiness/train_get_code_tree.jsp",userView,
	            backdate,null,"1");
	}else{
	    codexml = new CreateCodeActionXml(codesetid,codeitemid,action,target,privflag,"/pos/posbusiness/train_get_code_tree.jsp",userView);
	}
	try
	{
	  String xmlc=codexml.outTrainCodeItemTree();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>