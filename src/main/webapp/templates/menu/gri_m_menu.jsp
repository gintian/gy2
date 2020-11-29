<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
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
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
%>
<head>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -80;
function turn()
{
  <%if(isturn==null||!isturn.equals("false")){%>
	if(parent.myBody.cols != '0,*')
	{
		parent.myBody.cols = '0,*';
	}
	else
	{
		parent.myBody.cols = '170,*';
	}
  <%}%>
}   
 </SCRIPT>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -195;
</SCRIPT>
</head>
<body class=menuBodySet style="margin:0 0 0 0">    

<table cellpadding=0 cellspacing=0 width="169" class=menu_table index="1">
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>干部任免</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
            <hrms:priv func_id="26040">  
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/general/card/searchcard.do?b_query=link&inforkind=1" target="il_body"  onclick="turn();"><img src="/images/card.gif" border=0></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/general/card/searchcard.do?b_query=link&inforkind=1" target="il_body" ><font id="a001" class="menu_a" onclick="turn();">任免表</font></hrms:link>
              </td>
            </tr>
             </hrms:priv>
          </table>
	</form>
   </div>
 </td>
  </tr>
</table>

<table cellpadding=0 cellspacing=0 width="169" class=menu_table index="2">
  <tr style="cursor:hand;">
    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>花 名 册</span></td>
  </tr>
  <tr>
    <td>
   <div class=sec_menu style="width:169;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
   <form name="static" action="" method="post">
     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
            <tr>
              <td  align="center" class="loginFont" ><hrms:link href="/general/muster/select_muster_fields.do?b_query=link&a_inforkind=1" target="il_body" ><img src="/images/hmuster.gif" border=0></hrms:link></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" >
                <hrms:link href="/general/muster/select_muster_fields.do?b_query=link&a_inforkind=1" target="il_body" ><font id="a001" class="menu_a">新建花名册</font></hrms:link>
              </td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/general/muster/muster_list.do?b_query=link&a_inforkind=1" target="il_body"><img src="/images/hmuster.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/general/muster/muster_list.do?b_query=link&a_inforkind=1" target="il_body"><font id="a003" class="menu_a" >常用花名册</font></a></td>
            </tr> 

            <tr>
              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/select_muster_name.do?b_query=link&a_inforkind=1" target="il_body"><img src="/images/ll.gif" border=0></a></td>
            </tr>
            <tr>
              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/select_muster_name.do?b_query=link&a_inforkind=1" target="il_body"><font id="a003" class="menu_a" >高级花名册</font></a></td>
            </tr> 

          </table>
	</form>
   </div>
 </td>
  </tr>
</table>

<script language="javascript">
	showFirst();
</script>  



                                                                                                                                                       