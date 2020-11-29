<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	SubsysOperation subsysOperation=new SubsysOperation();
	HashMap map = subsysOperation.getMap();
	String isopen_2=(String)map.get("34");//34是薪资管理
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
<hrms:priv func_id="3270">      
		<table cellpadding=0 cellspacing=0 width="159"  class=menu_table index="1">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>薪资发放</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		            <hrms:priv func_id="32702">  
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/gz_set_list.do?b_query=link&flow_flag=0&gz_module=0" target="il_body" onclick="turn();"><img src="/images/bjbb.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/gz_set_list.do?b_query=link&flow_flag=0&gz_module=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a">薪资发放</font></hrms:link>
		              </td>
		            </tr>
		             </hrms:priv>
		              <hrms:priv func_id="32703"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/gz_sp_setlist.do?b_query=link&flow_flag=1&gz_module=0" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/gz_sp_setlist.do?b_query=link&flow_flag=1&gz_module=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a">薪资审批</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>
		          <hrms:priv func_id="32704"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/tax/gz_tax_org_tree.do?b_query=link&is_back=not" target="il_body" onclick="turn();"><img src="/images/jh.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/tax/gz_tax_org_tree.do?b_query=link&is_back=not" target="il_body" onclick="turn();"><font id="a001" class="menu_a">所得税管理</font></hrms:link>
		              </td>
		            </tr>                  
		          </hrms:priv>      
		             
		             
		             
		          </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>	
<hrms:priv func_id="3271">
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>缴费核算</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		           <hrms:priv func_id="32712"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/gz_set_list.do?b_query=link&flow_flag=0&gz_module=1" target="il_body" onclick="turn();"><img src="/images/bjbb.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/gz_set_list.do?b_query=link&flow_flag=0&gz_module=1" target="il_body" onclick="turn();"><font id="a001" class="menu_a">缴费核算</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv> 
		          <hrms:priv func_id="32713"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/gz_sp_setlist.do?b_query=link&flow_flag=1&gz_module=1" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/gz_sp_setlist.do?b_query=link&flow_flag=1&gz_module=1" target="il_body" onclick="turn();"><font id="a001" class="menu_a">缴费审批</font></hrms:link>
		              </td>
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



                                                                                                                                                       