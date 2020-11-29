<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
 <%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
   EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
   String netHref=employPortalForm.getNetHref();
    String aurl = (String)request.getServerName();
	    String port=request.getServerPort()+"";
	    String prl=request.getScheme();
	    String url_p=prl+"://"+aurl+":"+port;
	    String chl_id=(String)request.getParameter("chl_id");
%>
<html>
<head>
<LINK href="/css/employNetStyle.css" type=text/css rel=stylesheet>
<LINK href="/css/main.css" type=text/css rel=stylesheet>
<LINK href="/css/nav.css" type=text/css rel=stylesheet>
<script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
<style  id="iframeCss">
div{
cursor:hand;font-size:12px;
}
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}

.f12white {
	font-size: 12px;
	line-height: 140%;
	color: #ffffff;
	text-decoration: none;
	font-family: "Microsoft Sans Serif";
	font-weight:bold;
}
a.f12white:link {
	font-size: 12px;
	color: #ffffff;
	text-decoration: none;
}
a.f12white:visited {
	font-size: 12px;
	color: #ffffff;
	text-decoration: none;
}
a.f12white:hover {
	font-size: 12px;
	color: #ffffff;
	text-decoration: underline;
}
/*菜单背景颜色*/
.MenuRow {
	border: 0px;
	BORDER-BOTTOM: 0pt solid; 
	BORDER-LEFT: 0pt solid; 
	BORDER-RIGHT:0pt solid; 
	BORDER-TOP: 0pt solid;
	font-size: 20px;
	/*color:#FFFFFF;*/
	border-collapse:collapse; 
	height:22;
	background-color:#ebeff3;
	text-align:center;
	/*background-color:#FFFFFF*/
}
.MenuRow_1 {
	border: 0px;
	border-bottom:1px solid #fff; 
	BORDER-LEFT: 0pt solid; 
	BORDER-RIGHT:0pt solid; 
	BORDER-TOP: 0pt solid;
	font-size: 20px;
	border-collapse:collapse; 
	height:22;
	background-color:#ebeff3;
	text-align:center;
	/*background-color:#FFFFFF*/
}
/*第一层菜单背景颜色*/
.firstMenuRow{
/* color:#FFFFFF;*/
 background-image:url(../images/search_middle.jpg);
 background-repeat:repeat-x;
 size:13pt;
 margin-top:300px;
  cursor:hand;
 /*background-color:#006E6D*/
}
/*菜单字体*/
.MenuRowFont{
   color:#666;
   font-size:12px;
}
/*平铺菜单左侧圆角型图片*/
.MenuLeftHead
{
    background-image: url(../../images/search_left.jpg);
	background-repeat:no-repeat;
	/*background-color: #A2D9DC;
	background-color: #FFFFFF;*/
	background-position:center
}
/*平铺菜单右侧圆角型图片*/
.MenuRightHead
{
    background-image: url(../../images/search_right.jpg);
	background-repeat:no-repeat;
	/*background-color: #A2D9DC;
	background-color: #FFFFFF;*/
	background-position:center
}
</style>
<script language="javascript">
var width=window.screen.width-300;
var height=window.screen.height-760;

   function SETTDCOLOR(obj,tdcolor)
   {
      obj.style.backgroundColor =tdcolor;
   }
</script>
</head>
<body>
<table  width="94%" border="0" cellspacing="0" cellpadding="0" style='ALIGN:center'>
   <tr>
     <td width="166" height="120" >
     <%if(netHref!=null&&netHref.length()>0){ %>
     <a href="<%=netHref%>" target="_blank"><img src="/images/header_logo.jpg" border="0"/></a>
     <%}else{ %>
     <img src='/images/header_logo.jpg' border='0'/>
    <%} %>
     </td>
     <script language='javascript' >
		var awidth=(window.screen.width*0.85*0.94)-166-800;	
		document.write("<td width='"+awidth+"' height='120' class='hj_zhaopinleft'>");
	</script>
    &nbsp;</td>
    <td width="800" class='hj_zhaopin'>
           <hrms:cms_channel chl_no="1"></hrms:cms_channel>
   		<br>
	</td>
 </tr>
 </table>

</body>
</html>
