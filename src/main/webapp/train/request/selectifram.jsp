<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
	String nbase = (String)request.getParameter("nbase");
	String preflag = (String)request.getParameter("preflag");
	String itemkey = (String)request.getParameter("itemkey");
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>  
   <SCRIPT LANGUAGE=javascript src="./selectper.js"></SCRIPT>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0" style="border: none;">
   <div id="treemenu" ondblclick="selectPerson();"></div>
   <SCRIPT LANGUAGE=javascript> 
             var m_sXMLFile="/train/request/select_tree.jsp?itemid=root&nbase=<%=nbase%>&preflag=<%=preflag%>&itemkey=<%=itemkey%>";             
             var root=new xtreeItem("root","组织机构","","","组织机构","/images/unit.gif",m_sXMLFile);
             root.setup(document.getElementById("treemenu"));
   </SCRIPT> 
<BODY>
</HTML>


