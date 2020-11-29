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
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<head>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop - 160;
</SCRIPT>
</head>
<body class=menuBodySet style="margin:0 0 0 0" >         

  
<table cellpadding=0 cellspacing=0 width=175  class=menu_table>
  <tr style="cursor:hand;">
      
    <td  class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><FONT face="webdings">4</FONT></span>资源中心</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:175;height:405;filter:alpha(Opacity=100);overflow:hidden;"  id=menu1> 
   <form name="list" action="/hrms/servlet/com.eTechSoft.hrms.user.translate.TranslateController" method=post target="rightFrame">
      <input type=hidden name="actionType" >
     <table cellpadding=2 cellspacing=3 align=center width=174  class="DetailTable" style="position:relative;top:10px;">
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/organization.gif" border=0></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hrms/servlet/com.eTechSoft.hrms.security.SecurityController?actionType=listGroup" target="operationFrame"><font id="a001" class="menu_a" onClick="changMenuStyle('a001');">组织机构图</font></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/icons/note3.gif" border=0 ></td>
            </tr>
             <tr>
              <td  align="center" class="loginFont" ><a href="/hrms/servlet/com.eTechSoft.hrms.security.SecurityController?actionType=listRole" target="operationFrame" ><font id="a002" class="menu_a" onClick="changMenuStyle('a002');">岗位职责说明书</font></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/depart.gif" border=0></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hrms/servlet/com.eTechSoft.hrms.security.SecurityController?actionType=entryPassword" target="operationFrame"><font id="a003" class="menu_a" onClick="changMenuStyle('a003');">通讯录</font></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/setting.gif" border=0 ></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/cardingcard/cardingcardshow.do" target="il_body" ><font id="a004" class="menu_a" onClick="changMenuStyle('a004');">名片夹</font></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/setting.gif" border=0 ></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hrms/servlet/com.eTechSoft.hrms.security.SecurityController?actionType=systemConfigEntry" target="operationFrame" ><font id="a009" class="menu_a" onClick="changMenuStyle('a009');">表格下载</font></a></td>
            </tr>                      
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/setting.gif" border=0 ></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hrms/servlet/com.eTechSoft.hrms.security.SecurityController?actionType=systemConfigEntry" target="operationFrame" ><font id="a009" class="menu_a" onClick="changMenuStyle('a009');">办事流程下载</font></a></td>
            </tr>                      
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>

<table cellpadding=0 cellspacing=0 width=175  class="menu_table" >
  <tr style="cursor:hand;">
    <td width="1383"  class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><FONT face="webdings">4</FONT></span>规章制度</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:175;height:0;filter:alpha(Opacity=100);display:none;"   id=menu2> 
      <form name="entry2" method="post"  action="/hrms/servlet/com.eTechSoft.hrms.insurance.InsuranceController"> 
        <input type=hidden name="actionType" value="listInsurance">
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/setting.gif" border=0 ></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hrms/servlet/com.eTechSoft.hrms.security.SecurityController?actionType=systemConfigEntry" target="operationFrame" ><font id="a005" class="menu_a" onClick="changMenuStyle('a005');">规章制度</font></a></td>
            </tr>
          </table>
     </form>
        
     </div>
   </td>
  </tr>
</table>


<script language="javascript">
  var whichOpen=menuTitle1;
  var whichContinue="";
  document.all.menu1.style.height =divHeight;
  document.all.menu1.style.display="block";
  parent.frames[1].name = "operationFrame"; 
</script>  

                                                                              