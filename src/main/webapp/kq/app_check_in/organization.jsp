<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData"%>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String flag=request.getParameter("flag");

	String codeid=RegisterInitInfoData.getKqPrivCode(userView);;
	String codevalue=RegisterInitInfoData.getKqPrivCodeValue(userView);
    
	String a_code=codeid+codevalue;
	if(userView.isSuper_admin())
	   a_code="UN";
	String action="/kq/app_check_in/manuselect.do?b_query=link&a_code="+a_code;
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <hrms:themes /> <!-- 7.0css -->
   <br>
</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
   <div id="treemenu" ></div>
<BODY>
</HTML>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript>
<%
 if(!a_code.equals("UN"))
 {
%>
	var m_sXMLFile	= "/kq/app_check_in/get_org_tree.jsp?flag=<%=flag%>&params=codeitemid%3D'<%=codevalue%>'";//
<%
 }
 else
 {
%>
	var m_sXMLFile	= "/kq/app_check_in/get_org_tree.jsp?flag=<%=flag%>&params=codeitemid%3Dparentid";	 //
<%}%>
var root=new xtreeItem("UN","组织机构",'<%=action%>',"mil_body","组织机构","/images/unit.gif",m_sXMLFile);
root.setup(document.getElementById("treemenu"));
</SCRIPT>