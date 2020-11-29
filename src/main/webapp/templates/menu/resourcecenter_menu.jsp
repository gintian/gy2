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
   var  divHeight = window.screen.availHeight - window.screenTop - 180;
</SCRIPT>
</head>
<body class=menuBodySet style="margin:0 0 0 0" >         

  
<table cellpadding=0 cellspacing=0 width=169  class=menu_table>
  <tr style="cursor:hand;">
    <td  class=menu_title align="center" id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>资源中心</span></td>
  </tr>
  <tr>
    <td>
   <div class="sec_menu" style="width:169;height:405;filter:alpha(Opacity=100);overflow:hidden;"  id=menu1> 
   <form name="list" action="/hrms/servlet/com.eTechSoft.hrms.user.translate.TranslateController" method=post target="rightFrame">
      <input type=hidden name="actionType" >
     <table cellpadding=2 cellspacing=3 align=center width=174  class="DetailTable" style="position:relative;top:10px;">
            <tr>
              <td  align="center"><a href="/serlet/com.eTechSoft.hrms.security.SecurityController?actionType=listGroup" target="operationFrame"><font id="a001" class="menu_a" onClick="changMenuStyle('a001');"><img src="/images/organization.gif" border=0></font></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/addressbook/editaddressbook.do?b_search=link" target="operationFrame"><font id="a001" class="menu_a" >组织机构图</font></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/pos.gif" border=0 ></td>
            </tr>
             <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/addressbook/editaddressbook.do?b_search=link" target="operationFrame" ><font id="a002" class="menu_a" >岗位职责说明书</font></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/addr_note.gif" border=0></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/addressbook/editaddressbook.do?b_search=link" target="operationFrame"><font id="a003" class="menu_a" >通讯录</font></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/cardset.gif" border=0 ></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/cardingcard/cardingcardshow.do" target="operationFrame" function_id="xxx"><font id="a004" class="menu_a" >名片夹</font></a></td>
            </tr>
                    
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>

<table cellpadding=0 cellspacing=0 width=169  class="menu_table" >
  <tr style="cursor:hand;">
    <td width="1383" align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>规章制度</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu2> 
      <form name="entry2" method="post"  action="/hrms/servlet/com.eTechSoft.hrms.insurance.InsuranceController"> 
        <input type=hidden name="actionType" value="listInsurance">
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/investigate.gif" border=0 ></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/selfservice/lawresource/lawresource.do?b_query=link" target="operationFrame" ><font id="a005" class="menu_a">规章制度</font></a></td>
            </tr>
          </table>
     </form>
        
     </div>
   </td>
  </tr>
</table>
<table cellpadding=0 cellspacing=0 width=169  class="menu_table" >
  <tr style="cursor:hand;">
    <td width="1383" align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>下载中心</span></td>
  </tr>
  <tr>
    <td>
     <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"   id=menu3> 
      <form name="entry2" method="post"  action="/hrms/servlet/com.eTechSoft.hrms.insurance.InsuranceController"> 
        <input type=hidden name="actionType" value="listInsurance">
          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/table_download.gif" border=0 ></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/downfile/downfilelist.do?b_query=link&fileflag=1" target="operationFrame" function_id="xxx"><font id="a009" class="menu_a" >表格下载</font></hrms:link></td>
            </tr>                      
            <tr>
              <td  align="center" class="loginFont" ><img src="/images/table_download.gif" border=0 ></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/downfile/downfilelist.do?b_query=link&fileflag=2" target="operationFrame" function_id="xxx"><font id="a009" class="menu_a" onClick="changMenuStyle('a009');">办事流程下载</font></hrms:link></td>
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

                                                                              