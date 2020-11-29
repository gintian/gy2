<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.performance.UnityGradeAchivementTree"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
    response.setContentType("text/xml;charset=UTF-8");
    String init=request.getParameter("init");    
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String flag = request.getParameter("flag");
	String codeid = request.getParameter("codeid");
	String object_type=request.getParameter("object_type");
	String target_id=request.getParameter("target_id");
	
	UnityGradeAchivementTree unityGradeTree = new UnityGradeAchivementTree(flag,codeid,object_type,init,target_id,userView);
	try
	{
	  String xmlc=unityGradeTree.outPutOrgXml();  //create xtree.js treeview.
//	  out.println(xmlc);
	  response.getWriter().write(xmlc);
	  response.getWriter().close();
	}
	catch(GeneralException e)
	{
      	    e.printStackTrace();
	}

%>