<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String codetiem="";
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  	
	   if(userView.isSuper_admin())
	   {
		codetiem= "UN";
	   }else
	   {
		 if(userView.getManagePrivCodeValue()!=null&&userView.getManagePrivCodeValue().length()>0)
		 {
		  codetiem="UN"+userView.getManagePrivCodeValue();
		 }else
		 {
		   codetiem= "UN"+userView.getUserOrgId();
		 }
	   }
    } 
	String id = (String)request.getParameter("id");
    if(id!=null&&id.length()>0)
      codetiem=id;
%>

<HTML>
<HEAD>
  <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
   <SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>  
   <SCRIPT LANGUAGE=javascript>
    	
   </SCRIPT>  
</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
 <tr>  
    <td valign="top">
	<div id="treemenu"></div>
    </td>
  </tr>
</table>
<BODY>
</HTML>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript>
  var codetiem="<%=codetiem%>";
  function setCode(code)
  {
    codetiem=code;
    alert(codetiem);    
  }
  var m_sXMLFile= "/kq/team/history/group_list.jsp?params=gp&codetiem="+codetiem+"";	
  var root=new xtreeItem("root","所有班组","/kq/team/history/search_array_data.do?b_search=link&group_id=&a_code=GP","mil_body","所有班组","/images/table.gif",m_sXMLFile);
  root.setup(document.getElementById("treemenu"));
</SCRIPT>