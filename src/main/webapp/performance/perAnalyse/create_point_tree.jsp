<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.AnalyseSelPointTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.performance.PerAnalyseForm,com.hjsj.hrms.utils.PubFunc"%>
<%
        response.setContentType("text/xml;charset=UTF-8");
	PerAnalyseForm perAnalyseForm=(PerAnalyseForm)session.getAttribute("perAnalyseForm");
    String busitype = request.getParameter("busitype"); // 业务分类字段 =0(绩效考核); =1(能力素质)
	String planids = request.getParameter("planids");
	String flag = request.getParameter("flag");
	String pointsetid=request.getParameter("pointsetid");
    String objSelected=PubFunc.decrypt(perAnalyseForm.getObjSelected());
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);  
	AnalyseSelPointTree perPointByTree = new AnalyseSelPointTree(planids,pointsetid,flag,userView,busitype,objSelected);
	try
	{
	  String xmlc=perPointByTree.GetTreeXMLString();  //create xtree.js treeview.
	  //out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close(); 
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>