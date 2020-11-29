<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.transaction.pos.posreport.PosReportRelationsTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%
    response.setContentType("text/xml;charset=UTF-8");
	String position=(String)request.getParameter("position");
	String yfiles = (String)request.getParameter("yfiles");
	String sep = (String)request.getParameter("sep")!=null ? PubFunc.keyWord_reback((String)request.getParameter("sep")) : "/";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);	
	PosReportRelationsTree codexml=new PosReportRelationsTree(position,userView);
	codexml.setYfiles(yfiles);
	codexml.setSep(sep);
	try{
	  	String xmlc = "";
	  	if(userView != null&&!userView.isSuper_admin())
	  	{
	  		//org.CodeItemId like
	  	   /*
	  	   *cmq changed at 20121003 for
	  	   */
	  	   //codexml.setCode(userView.getManagePrivCodeValue());
	  		codexml.setCode(userView.getUnitPosWhereByPriv("org.CodeItemId"));
	  		//System.out.println(userView.getUnitPosWhereByPriv("org.CodeItemId"));
	  	}
	  	else
	  	   codexml.setCode("");
	  	   xmlc = codexml.getReportRelationsTree();  //create xtree.js treeview.
	  	//out.println(xmlc);
	  	response.getWriter().write(xmlc);
	  response.getWriter().close();
	}catch(GeneralException ee){
      	ee.printStackTrace();
	}
%>