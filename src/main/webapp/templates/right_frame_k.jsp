<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>HRPWEB3</title>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript">
function pf_ChangeFocus() 
{
   key = window.event.keyCode;
   if ( key==0xD && event.srcElement.tagName!='TEXTAREA') /*0xD*/
   {
   	window.event.keyCode=9;
   }
   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
   if ( key==116)
   {
   	window.event.keyCode=0;	
	window.event.returnValue=false;
   }   
   if ((window.event.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
   {    
        window.event.keyCode=0;	
	window.event.returnValue=false;
   } 
}

//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
/*
function document.oncontextmenu() 
{ 
  	return false; 
} 
*/
</script>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">

<STYLE type=text/css>
.point{position:absolute;background-color:#dddddd;font-size:12px;cursor:hand;}
</STYLE>
<SCRIPT language=javascript>
var ua  = window.navigator.userAgent;
var opera = /opera [56789]|opera\/[56789]/i.test(ua);
var ie  = !opera && /msie [56789]/i.test(ua);
var moz  = !opera && /mozilla\/[56789]/i.test(ua);

function changeWin(o){
 if(!ie) return false; 
 if(parent.forum.cols != "2,*") {
  parent.forum.cols = "2,*";
  o.innerHTML = "<font face='Webdings' color='#000000' class='point'>4</font>";
 } else {
  parent.forum.cols = "200,*";
  o.innerHTML = "<font face='Webdings' color='#000000' class='point'>3</font>";
 }
}
</SCRIPT>

</HEAD>

<BODY leftMargin=0 topMargin=0 marginwidth="0" marginheight="0">
<TABLE height="100%" cellSpacing=0 cellPadding=0 width="102%" border=0>
<TR>
  
  <TD bgColor=#aaaaaa><IMG height=1 src="" width=1></TD>
  <TD bgColor=#dddddd>
    <TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%" border=0>
    <TR>
      <TD onclick=changeWin() height=1><IMG height=1 src="" width=10></TD></TR>
      <TR>
      <TD id="menuSwitch" onclick="changeWin(this)" height="100%"  ><FONT face="Webdings" color="#000000" class="point">3</FONT></TD>
    </TR>
    </TABLE>
  </TD>

  <TD width="100%">
    <IFRAME style="VISIBILITY: inherit; WIDTH: 100%; HEIGHT: 100%" marginWidth=0 frameSpacing=0 marginHeight=0 src=<%=request.getParameter("url")%> frameborder="no" border="0" noresize></IFRAME>
  </TD>
  
  
</TR>
</TABLE>
</BODY>
</HTML>