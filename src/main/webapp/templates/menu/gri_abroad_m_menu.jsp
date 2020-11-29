<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%

    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
%>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -80;
   function turn()
   {
     <% 
	  if(isturn==null||!isturn.equals("false"))
	     out.println("parent.menupnl.toggleCollapse(false);");
	%>
   }   
 </SCRIPT>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -195;
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">  
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol">   
	<html:form action="/templates/menu/gri_abroad_m_menu">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>出国出境</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/template/operation/show_explain.do?b_query=link" target="il_body" ><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/general/template/operation/show_explain.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">因公出国(境)</font></a>
	              </td>
	            </tr>   		           
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/template/operation/show_privy_explain.do?b_query=link" target="il_body" ><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/general/template/operation/show_privy_explain.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">因私出国(境)</font></a>
	              </td>
	            </tr>   		           
	     </table>
	   </div>
	 </td>
	  </tr>
	</table>
	<hrms:priv func_id="3212" module_id="">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>汇总统计</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="32120">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/template/goabroad/collect/searchstat.do?b_query=link&action=searchstatdata.do&target=mil_body" target="il_body" ><img src="/images/hmuster.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/template/goabroad/collect/searchstat.do?b_query=link&action=searchstatdata.do&target=mil_body" target="il_body" ><font id="a001" class="menu_a">汇总统计</font></hrms:link>
	              </td>
	            </tr>
	           
	          </hrms:priv>              
	      </table>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="3213" module_id="">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="4">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
	          <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="32130">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/param/sys_param.do?b_query=link" target="il_body" ><img src="/images/card.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/param/sys_param.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >参数设置</font></a></td>
	            </tr>
	            </hrms:priv>   
	          </table>
	
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	</html:form>
</td>

</tr>
</table>
<script language="javascript">
	showFirst();
</script>  



                                                                                                                                                       