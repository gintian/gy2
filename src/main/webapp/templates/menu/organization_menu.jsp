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
    String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
           //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
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
  <td width="162" id="menucol" > 
	<table cellpadding=0 cellspacing=0 width="100%" height="700" class=menu_table>
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 ><span>机构信息</span></td>
	  </tr>
	  <tr>
	    <td valign="top">
	   <div class=sec_menu style="width:159;height:expression(document.body.offsetHeight);filter:alpha(Opacity=100);overflow:hidden;"  id=menu1> 
	   <form name="employ" action="" method=post target="il_menu">
	      <table cellpadding=2 cellspacing=3 align=center width=159  class="DetailTable" style="position:relative;top:10px;">
		<hrms:priv func_id="050101">      	 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/inform/org/searchorgbrowse.do?b_query=link&droit=0&busiPriv=1" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/organization.gif" border="0"></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/general/inform/org/searchorgbrowse.do?b_query=link&droit=0&busiPriv=1" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">信息浏览</font></hrms:link>
	              </td>
	            </tr>
	        </hrms:priv> 
	           <hrms:priv func_id="050104">  
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/orginfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=org&kind=2&target=mil_body&busiPriv=1" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/account.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/workbench/orginfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=org&kind=2&target=mil_body&busiPriv=1" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">信息维护</font></hrms:link>
	              </td>
	            </tr>
	           </hrms:priv>         
	         <hrms:priv func_id="050102">         
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchorgmap.do?b_search=link" target="il_body" onclick="turn();"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchorgmap.do?b_search=link" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >机构图</font></a></td>
	            </tr> 
	         </hrms:priv>  
	         <hrms:priv func_id="050103">             
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchhistoryorgmap.do?b_search=link" target="il_body" onclick="turn();"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchhistoryorgmap.do?b_search=link" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >历史机构</font></a></td>
	            </tr> 
	          </hrms:priv>          
	          </table>
		</form>
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

                                                                              