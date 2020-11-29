<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%

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
   var  divHeight = window.screen.availHeight - window.screenTop -195;
</SCRIPT>
</head>
<body class=menuBodySet style="margin:0 0 0 0">    

<table cellpadding=0 cellspacing=0 width="169" class=menu_table>
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>工资变动</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display=block;"  id=menu1> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">

            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/query_interface.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/employee.gif" border=0></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/workbench/query/query_interface.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">工资变动</font></hrms:link>
              </td>
            </tr>
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>

     
<table cellpadding=0 cellspacing=0 width="169" class=menu_table>
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>工资发放</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">

            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/employee.gif" border=0></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">工资计算</font></hrms:link>
              </td>
            </tr>
                  
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/resource_plan/search_resource_list.do?b_query=link" target="il_body"><img src="/images/query_set.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/resource_plan/search_resource_list.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >工资审批</font></a></td>
            </tr> 
            
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>

<table cellpadding=0 cellspacing=0 width="169" class=menu_table>
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>工资分析</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">

            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/employee.gif" border=0></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">工资分析</font></hrms:link>
              </td>
            </tr>
                       
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>

<table cellpadding=0 cellspacing=0 width="169" class=menu_table>
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">

            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/employee.gif" border=0></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/hire/staffreq/staffreqquery.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">参数设置</font></hrms:link>
              </td>
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
  parent.frames[1].name = "il_body"; 
</script>  



                                                                                                                                                       