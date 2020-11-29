
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>

<%@ page import="com.hjsj.hrms.interfaces.sys.CreateCodeActionXml"%>
<%@ page import="com.hrms.struts.exception.*" %>
<%@page import="com.hrms.struts.valueobject.UserView"%>

<%
    response.setContentType("text/xml;charset=UTF-8");
	String codesetid=(String)request.getParameter("codesetid");
	String codeitemid=(String)request.getParameter("codeitemid");
	String r5100 = (String) request.getParameter("r5100");
	String r5000 = (String) request.getParameter("r5000");
	String classes = (String) request.getParameter("classes");
	String privflag=(String)request.getParameter("privflag");
	String action="/train/resource/course.do?b_query=link";
	if ("69".equals(codesetid)) {
		action="/train/trainexam/question/questiones/questiones.do?b_query=link";
	} else  if ("40000".equals(codesetid) || "40001".equals(codesetid)) {
		
		
		action="/train/resouce/lessons.do?b_show=link&r5100=" + r5100 + "&classes=" + classes +"&r5000=" + r5000;
	}
	UserView userView = (UserView)session.getAttribute("userView");
	CreateCodeActionXml codexml=new CreateCodeActionXml(codesetid,codeitemid,action,"mil_body",privflag,"/train/resource/course/get_code_tree.jsp",userView);
	try
	{
		if ("40000".equals(codesetid)|| "40001".equals(codesetid)) {
			codexml.setR5100(r5100);
			codexml.setClasses(classes);
			codexml.setR5000(r5000);
		}
	  String xmlc="";  //create xtree.js treeview.
	  //out.println(xmlc);
	  
	  if ("40000".equals(codesetid) || "40001".equals(codesetid)) {// scorm课件目录
	  	xmlc=codexml.outCodeItemTree2();
	  } else {
	  	xmlc=codexml.outCodeItemTree();
	  }
	  response.getWriter().write(xmlc);
	  response.getWriter().close();;
	}
	catch(GeneralException ee)
	{
      	    ee.printStackTrace();
	}

%>