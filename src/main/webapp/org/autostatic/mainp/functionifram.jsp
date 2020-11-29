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
	String checktemp=(String)request.getParameter("checktemp");
	String mode = request.getParameter("mode")==null?"":(String)request.getParameter("mode");
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT> 
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>  
   <SCRIPT LANGUAGE=javascript src="/js/selectfunction.js"></SCRIPT>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu" onclick="saveCalculation();"></div>
   <SCRIPT LANGUAGE=javascript> 
             var m_sXMLFile="/org/autostatic/mainp/get_function_tree.jsp?checktemp=<%=checktemp%>&mode=<%=mode%>";	 //
                          
             var root=new xtreeItem("root",KQ_FORMULA_FUNCTIONS,"","",KQ_FORMULA_FUNCTIONS,"/images/ac.gif",m_sXMLFile);
             root.setup(document.getElementById("treemenu"));
   </SCRIPT> 
<BODY>
</HTML>


