<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
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
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse"); 
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
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
<body class=menuBodySet style="margin:0 0 0 0">         
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol">  
	<table cellpadding=0 cellspacing=0 width=159 class=menu_table>
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 ><span>竞聘上岗</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:expression(document.body.offsetHeight);filter:alpha(Opacity=100);overflow:auto;"  id=menu1> 
	      <table cellpadding=2 cellspacing=3 align=center width=159  class="DetailTable" style="position:relative;top:10px;">
	      	<hrms:priv func_id="0F1">   
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/hire/jp_contest/search_jp_pos.do?b_query=link" target="il_body"  ><img src="/images/tx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/jp_contest/search_jp_pos.do?b_query=link" target="il_body"  ><font id="a001" class="menu_a">拟竞聘岗位</font></hrms:link>
	              </td>
	            </tr>
			</hrms:priv> 
    
    		<hrms:priv func_id="0F2">
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/jp_contest/apply/apply_jp_pos.do?b_search=link" target="il_body" ><img src="/images/jh.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/jp_contest/apply/apply_jp_pos.do?b_search=link" target="il_body" ><font id="a003" class="menu_a" >申请竞聘岗位</font></a></td>
	            </tr>    
           </hrms:priv>     
    		<hrms:priv func_id="0F3">                
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/jp_contest/personinfo/showinfodata.do?b_search=link&flag=infoself" target="il_body" ><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/jp_contest/personinfo/showinfodata.do?b_search=link&flag=infoself" target="il_body" ><font class="menu_a">竞聘名单</font></a></td>
	            </tr>
           </hrms:priv>            
    		<hrms:priv func_id="0F4">    
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/hire/jp_contest/param/engageparam.do?b_query=link" target="il_body" ><img src="/images/jgbm.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/hire/jp_contest/param/engageparam.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">参数设置</font></hrms:link>
	              </td>
	            </tr>
	        </hrms:priv>  
	       
	          </table>
	   </div>
	 </td>
	  </tr>
	</table>
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

                                                                              