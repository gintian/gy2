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
   var  divHeight = window.screen.availHeight - window.screenTop -180;
</SCRIPT>
</head>
<body class=menuBodySet style="margin:0 0 0 0">         
<table cellpadding=0 cellspacing=0 width="169" class=menu_table>
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="../../images/darrow.gif" border=0></span>招聘计划</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:800;filter:alpha(Opacity=100);display=block;"  id=menu1> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
            <hrms:priv func_id="120101">
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/hire/zp_plan/search_zp_plan.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/employee.gif" border=0></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
              <hrms:link href="/hire/zp_plan/search_zp_plan.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">招聘计划</font></hrms:link>
              </td>
            </tr>
          </hrms:priv> 
     	  <hrms:priv func_id="120201">                      
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_job/search_zp_joblist.do?b_query=link" target="il_body"><img src="/images/query_set.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_job/search_zp_joblist.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >招聘活动</font></a></td>
            </tr> 
          </hrms:priv> 
          <hrms:priv func_id="120301">                      
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_release_pos/search_release_poslist.do?b_query=link" target="il_body"><img src="/images/query_set.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/hire/zp_release_pos/search_release_poslist.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >招聘岗位发布</font></a></td>
            </tr> 
          </hrms:priv> 
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>

<script language="javascript">
  parent.frames[1].name= "il_body"; 
</script>  



                                                                                                                                                       