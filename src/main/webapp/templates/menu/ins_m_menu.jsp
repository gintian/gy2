<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation"%>
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
    <hrms:priv func_id="32501">  
		<table cellpadding=0 cellspacing=0 width="159"  class=menu_table index="1">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>保险核定</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		            <hrms:priv func_id="32501">  
		            <tr>
		             <td  align="center" class="loginFont" >
		               <%if(isopen_2!=null&&isopen_2.equals("true")) {%>
		                <hrms:link href="/general/template/search_bs_sort.do?b_search=link&type=8&res_flag=17" target="il_body" onclick="turn();"><img src="/images/card.gif" border=0></hrms:link>
		               <%}else {%>
		                <hrms:link href="/general/template/search_bs_tree.do?b_query=link&type=8&res_flag=17" target="il_body"  onclick="turn();"><img src="/images/card.gif" border=0></hrms:link></td>
		               <%} %> 
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <%if(isopen_2!=null&&isopen_2.equals("true")) {%>
		                  <hrms:link href="/general/template/search_bs_sort.do?b_search=link&type=8&res_flag=17" target="il_body" onclick="turn();"><font id="a001" class="menu_a">保险核定</font></hrms:link>
		                <%}else {%>
		                  <hrms:link href="/general/template/search_bs_tree.do?b_query=link&type=8&res_flag=17" target="il_body" ><font id="a001" class="menu_a" onclick="turn();">保险核定</font></hrms:link>
		                   <%} %> 
		              </td>
		            </tr>
		             </hrms:priv>
		          </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>	
<hrms:priv func_id="32502,32503,32516,32508">
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>缴费核算</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		           <hrms:priv func_id="32502"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/gz_set_list.do?b_query=link&flow_flag=0&gz_module=1" target="il_body" onclick="turn();"><img src="/images/bjbb.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/gz_set_list.do?b_query=link&flow_flag=0&gz_module=1" target="il_body" onclick="turn();"><font id="a001" class="menu_a">缴费核算</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv> 
		          <hrms:priv func_id="32503"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/gz_sp_setlist.do?b_query=link&flow_flag=1&gz_module=1" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/gz_sp_setlist.do?b_query=link&flow_flag=1&gz_module=1" target="il_body" onclick="turn();"><font id="a001" class="menu_a">缴费审批</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>
		          
		          
		           <hrms:priv func_id="32516"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/gz_sp_setcollectlist.do?b_querycollect=link&flow_flag=1&gz_module=1" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/gz_sp_setcollectlist.do?b_querycollect=link&flow_flag=1&gz_module=1" target="il_body" onclick="turn();"><font id="a001" class="menu_a">缴费汇总审批</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>
		          
		          
                 <hrms:priv func_id="32508">
		          <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/sp_flow/init_sp_flow.do?b_init=link&gz_module=1&init=first&binit=first&cinit=first" target="il_body" onclick="turn();"><img src="/images/bjbb.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
	                <hrms:link href="/gz/gz_accounting/sp_flow/init_sp_flow.do?b_init=link&gz_module=1&init=first&binit=first&cinit=first" target="il_body" onclick="turn();"><font id="a001" class="menu_a">审批流程</font></hrms:link>
		              </td>
		            </tr>  
		            </hrms:priv>  
		          </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>
<hrms:priv func_id="3250401,3250402">			
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="4">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>保险分析</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		          <hrms:priv func_id="3250401"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_analyse/gzAnalyseList.do?b_query=link&gz_module=1" target="il_body" ><img src="/images/lstatic.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_analyse/gzAnalyseList.do?b_query=link&gz_module=1" target="il_body" ><font id="a001" class="menu_a">保险分析</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>
		          <hrms:priv func_id="3250402"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_analyse/historydata/salary_set_list.do?b_query=query&gz_module=1" target="il_body" ><img src="/images/lstatic.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_analyse/historydata/salary_set_list.do?b_query=query&gz_module=1" target="il_body" ><font id="a001" class="menu_a">保险历史数据</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>
		          </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>		
<hrms:priv func_id="32505">		
		
		<table cellpadding=0 cellspacing=0 width="159" height="500" class=menu_table index="5">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
		  </tr>
		  <tr>
		    <td valign="top">
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		          <hrms:priv func_id="32506">  		     	     
		           <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/org/gzdatamaint/gzdatamaint.do?b_addsubclass=link&tagname=1&infor=2&gzflag=3" target="il_body" ><img src="/images/hmuster.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/org/gzdatamaint/gzdatamaint.do?b_addsubclass=link&tagname=1&infor=2&gzflag=3" target="il_body" ><font id="a001" class="menu_a">相关子集设置</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>	
		          <hrms:priv func_id="32507">  		
		           <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/org/gzdatamaint/gz_org_tree.do?b_query=link&infor=2&gzflag=3" target="il_body" onclick="turn();"><img src="/images/hmuster.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/org/gzdatamaint/gz_org_tree.do?b_query=link&infor=2&gzflag=3" target="il_body" onclick="turn();"><font id="a001" class="menu_a">基本数据维护</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>	
		          <hrms:priv func_id="32505">  
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/templateset/gz_templatelist.do?b_query=link0&gz_module=1" target="il_body" ><img src="/images/hmuster.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/templateset/gz_templatelist.do?b_query=link0&gz_module=1" target="il_body" ><font id="a001" class="menu_a">保险类别</font></hrms:link>
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



                                                                                                                                                       