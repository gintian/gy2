<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
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
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight- window.screenTop -80;
   function turn()
   {
    <% 
	  if(isturn==null||!isturn.equals("false"))
	     out.println("parent.menupnl.toggleCollapse(false);");
	%>
   }     
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">         
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol"> 
	<hrms:priv func_id="340">  
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table"  index="1">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>发文管理</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu1> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
			   <hrms:priv func_id="3401" module_id="">             
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/lawbase/lawtext/law_maintenance0.do?b_init=link&basetype=5" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>     
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/selfservice/lawbase/lawtext/law_maintenance0.do?b_init=link&basetype=5" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">文档浏览</font></hrms:link>
	              </td>            
	            </tr>
	    	   </hrms:priv> 
			   <hrms:priv func_id="3402" module_id="">      	                            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/lawbase/law_maintenance0.do?b_init=link&basetype=5" target="il_body" onclick="turn();"><img src="/images/lpublic_info.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/lawbase/law_maintenance0.do?b_init=link&basetype=5" target="il_body" onclick="turn();"><font id="a014" class="menu_a" >文档维护</font></a></td>
	            </tr>
	    	   </hrms:priv> 
	          </table>
	     </div>
	    </td>
	  </tr>
	</table>
	</hrms:priv> 
	<hrms:priv func_id="280">  	
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table"  index="2">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>制度政策</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu2> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
			   <hrms:priv func_id="2801" module_id="">             
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/lawbase/lawtext/law_maintenance0.do?b_init=link&basetype=1" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>     
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/selfservice/lawbase/lawtext/law_maintenance0.do?b_init=link&basetype=1" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">制度浏览</font></hrms:link>
	              </td>            
	            </tr>
	    	   </hrms:priv> 
			   <hrms:priv func_id="2802" module_id="">      	                            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/lawbase/law_maintenance0.do?b_init=link&basetype=1" target="il_body" onclick="turn();"><img src="/images/lpublic_info.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/lawbase/law_maintenance0.do?b_init=link&basetype=1" target="il_body" onclick="turn();"><font id="a014" class="menu_a" >制度维护</font></a></td>
	            </tr>
	    	   </hrms:priv> 
	          </table>
	     </div>
	    </td>
	  </tr>
	</table>
	</hrms:priv> 	
</td>

</tr>
</table>
<script language="javascript">
	showFirst();
</script>  

                                                                              