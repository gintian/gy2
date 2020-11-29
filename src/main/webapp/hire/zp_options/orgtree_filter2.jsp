<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String edition="2";
	String flag=request.getParameter("flag");
	String codeid=userView.getManagePrivCode();
	String codevalue=userView.getManagePrivCodeValue();
	String a_code=codeid+codevalue;
	if(userView.isSuper_admin())
	   a_code="UN";
	String action="/hire/zp_options/pos_filter_login.do?b_query=link&a_code="+a_code;
	if(flag.equals("org"))
	{
	  action="/hire/zp_options/pos_filter.do?b_query=link&a_code="+a_code+"&edition=2";

   }
%>

<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
<hrms:themes></hrms:themes>
   <div id="treemenu"></div>
<BODY>
</HTML>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript>
<%
 if(!a_code.equals("UN"))
 {
%>
	var m_sXMLFile	= "/hire/zp_options/get_org_tree_filter.jsp?flag=<%=flag%>&edition=2&params=codeitemid%3D'<%=codevalue%>'";	 //
<%
 }
 else
 {
%>
	var m_sXMLFile	= "/hire/zp_options/get_org_tree_filter.jsp?flag=<%=flag%>&edition=2&params=codeitemid%3Dparentid";	 //
<%}
 
%>
var root=new xtreeItem("UN",ORGANIZATION,'<%=action%>',"mil_body",ORGANIZATION,"/images/unit.gif",m_sXMLFile);
root.setup(document.getElementById("treemenu"));


</SCRIPT>