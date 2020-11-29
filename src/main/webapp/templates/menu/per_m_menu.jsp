<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
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
<hrms:priv func_id="32601"> 
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol"> 
		<table cellpadding=0 cellspacing=0 width="159"  class=menu_table index="1">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>考核体系</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		            <hrms:priv func_id="3260101">  
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&subsys_id=33" target="il_body"  onclick="turn();"><img src="/images/card.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&subsys_id=33" target="il_body" ><font id="a001" class="menu_a" onclick="turn();">考核指标</font></hrms:link>
		              </td>
		            </tr>
		             </hrms:priv>
		            <hrms:priv func_id="3260102">  
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link&subsys_id=33&isVisible=1&method=0&templateId=-1" target="il_body"  onclick="turn();"><img src="/images/card.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link&subsys_id=33&isVisible=1&method=0&templateId=-1" target="il_body" ><font id="a001" class="menu_a" onclick="turn();">考核模板</font></hrms:link>
		              </td>
		            </tr>
		             </hrms:priv>		             
		          </table>
		   </div>
		 </td>
		  </tr>
		</table>




</hrms:priv>	
<hrms:priv func_id="32602">
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>考核计划</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		           
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_plan/khplanorgtree.do?b_query=link&flow_flag=0&gz_module=0" target="il_body" onclick="turn();"><img src="/images/bjbb.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/kh_plan/khplanorgtree.do?b_query=link&flow_flag=0&gz_module=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a">考核计划</font></hrms:link>
		              </td>
		            </tr>

		    </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>
<hrms:priv func_id="32603">		
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>考核实施</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
				<hrms:priv func_id="3260301">
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_plan/performPlanList.do?b_query=link&busitype=0&jxmodul=1" target="il_body" onclick="turn();"  ><img src="/images/hmc.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/kh_plan/performPlanList.do?b_query=link&busitype=0&jxmodul=1" target="il_body" onclick="turn();"  ><font id="a001" class="menu_a">考核实施</font></hrms:link>
		              </td>
		            </tr>
				</hrms:priv>
				<hrms:priv func_id="3260302">
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_plan/performPlanList.do?b_query=link&busitype=0&jxmodul=3" target="il_body" onclick="turn();"  ><img src="/images/hmc.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/kh_plan/performPlanList.do?b_query=link&busitype=0&jxmodul=3" target="il_body" onclick="turn();"  ><font id="a001" class="menu_a">数据采集</font></hrms:link>
		              </td>
		            </tr>
				</hrms:priv>
		          </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>
<hrms:priv func_id="32604">			
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="4">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>绩效评估</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		          
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_plan/performPlanList.do?b_query=link&busitype=0&jxmodul=2" target="il_body" onclick="turn();" ><img src="/images/lstatic.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/kh_plan/performPlanList.do?b_query=link&busitype=0&jxmodul=2" target="il_body" onclick="turn();" ><font id="a001" class="menu_a">绩效评估</font></hrms:link>
		              </td>
		            </tr>
		       
		          </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>
<hrms:priv func_id="32605">			
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="5">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>绩效分析</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">

		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/perAnalyse.do?br_query=link&busitype=0" target="il_body" onclick="turn();"><img src="/images/lstatic.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/perAnalyse.do?br_query=link&busitype=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a">绩效分析</font></hrms:link>
		              </td>
		            </tr>

		          </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>		
<hrms:priv func_id="32606">		
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="6">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);"><span><span id=arrow6><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
		  </tr>
		  <tr>
		    <td >
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu6> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		           <hrms:priv func_id="3260601">  
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/options/checkBodyObjectList.do?b_query=link&bodyType=0" target="il_body" ><img src="/images/hmuster.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/options/checkBodyObjectList.do?b_query=link&bodyType=0" target="il_body" ><font id="a001" class="menu_a">主体类别</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>
		          <hrms:priv func_id="3260602"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/options/checkBodyObjectList.do?b_query=link&bodyType=1" target="il_body" ><img src="/images/jhhs.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		<hrms:link href="/performance/options/checkBodyObjectList.do?b_query=link&bodyType=1" target="il_body" ><font id="a001" class="menu_a">对象类别</font></hrms:link>
		              </td>
		            </tr>
		           </hrms:priv> 	
		           <hrms:priv func_id="3260603"> 
		          <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/options/perKnowList.do?b_query=link" target="il_body" ><img src="/images/px.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/options/perKnowList.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">了解程度</font></hrms:link>
		              </td>
		            </tr>
		            </hrms:priv>
		            <hrms:priv func_id="3260604"> 
		          <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/options/perDegreeList.do?b_query=link&busitype=0" target="il_body" ><img src="/images/px.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/options/perDegreeList.do?b_query=link&busitype=0" target="il_body" ><font id="a001" class="menu_a">等级分类</font></hrms:link>
		              </td>
		            </tr>
		            </hrms:priv>
		            <hrms:priv func_id="3260605"> 
		          <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/options/perParamList.do?b_query=link" target="il_body" ><img src="/images/px.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/options/perParamList.do?b_query=link&from_eval=0" target="il_body" ><font id="a001" class="menu_a">评语模板</font></hrms:link>
		              </td>
		            </tr>
		            </hrms:priv>
		           <hrms:priv func_id="3260606"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/per_initData.do?b_query=link" target="il_body" ><img src="/images/px.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/per_initData.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">初始化</font></hrms:link>
		              </td>
		            </tr>
		             </hrms:priv>
		              <hrms:priv func_id="3260607">   
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/options/kh_relation.do?br_int=link" target="il_body" ><img src="/images/px.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/options/kh_relation.do?br_int=link" target="il_body" ><font id="a001" class="menu_a">考核关系</font></hrms:link>
		              </td>
		            </tr>
		              </hrms:priv> 
		            <hrms:priv func_id="3260608"> 
		          <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/performance/options/configParameter.do?b_query=link" target="il_body" ><img src="/images/px.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/performance/options/configParameter.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">配置参数</font></hrms:link>
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



                                                                                                                                                       