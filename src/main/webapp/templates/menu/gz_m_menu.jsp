<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@page import="com.hrms.hjsj.sys.VersionControl"%>
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
	VersionControl ver = new VersionControl();
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
   var counter=0;
function analyseChartinit()
{
   if(counter==0)
   {
      parent.mil_body.location="/gz/gz_analyse/gzAnalyseChart.do?br_query=link";
      counter=1;
      window.setTimeout('initCounter()',2000);   
   }
   
   
}
function initCounter()
{
   counter=0;
}
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0"> 
 
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol"> 
<hrms:priv func_id="32401">      
		<table cellpadding=0 cellspacing=0 width="159"  class=menu_table index="1">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>薪资变动</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		            <hrms:priv func_id="32401">  
		            <tr>
		              <td  align="center" class="loginFont" >
		              <%if(isopen_2!=null&&isopen_2.equals("true")) {%>
		                 <hrms:link href="/general/template/search_bs_sort.do?b_search=link&type=2&res_flag=8" target="il_body" onclick="turn();"><img src="/images/card.gif" border=0></hrms:link>
		              <%}else {%>
		                  <hrms:link href="/general/template/search_bs_tree.do?b_query=link&type=2&res_flag=8" target="il_body"  onclick="turn();"><img src="/images/card.gif" border=0></hrms:link>
		              <%} %> 
		              </td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		               <%if(isopen_2!=null&&isopen_2.equals("true")) {%>
		                 <hrms:link href="/general/template/search_bs_sort.do?b_search=link&type=2&res_flag=8" target="il_body" onclick="turn();"><font id="a001" class="menu_a">薪资变动</font></hrms:link>
		               <%}else {%>
		                <hrms:link href="/general/template/search_bs_tree.do?b_query=link&type=2&res_flag=8" target="il_body" ><font id="a001" class="menu_a" onclick="turn();">薪资变动</font></hrms:link>
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
<hrms:priv func_id="32402,32403,32404,32416,32415,324021401,324021501">
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>薪资发放</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		           <hrms:priv func_id="32402"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/gz_set_list.do?b_query=link&flow_flag=0&gz_module=0" target="il_body" onclick="turn();"><img src="/images/bjbb.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/gz_set_list.do?b_query=link&flow_flag=0&gz_module=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a">薪资发放</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv> 
		          <hrms:priv func_id="32403"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/gz_sp_setlist.do?b_query=link&flow_flag=1&gz_module=0" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/gz_sp_setlist.do?b_query=link&flow_flag=1&gz_module=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a">薪资审批</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>
		          
		          
		           <hrms:priv func_id="32416"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/gz_sp_setcollectlist.do?b_querycollect=link&flow_flag=1&gz_module=0&returnvalue=menu" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/gz_sp_setcollectlist.do?b_querycollect=link&flow_flag=1&gz_module=0&returnvalue=menu" target="il_body" onclick="turn();"><font id="a001" class="menu_a">薪资汇总审批</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>
		          
		          
		          
		          <hrms:priv func_id="324021501"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/premium/premium_allocate/monthPremiumList.do?b_query=link" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/premium/premium_allocate/monthPremiumList.do?b_query=link" target="il_body" onclick="turn();"><font id="a001" class="menu_a">月奖金管理</font></hrms:link>
		              </td>
		            </tr>	
		          </hrms:priv> 
		          
		  <%
          	if(ver.searchFunctionId("3240214")){ 
          %>
		          <hrms:priv func_id="324021401"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/bonus/inform.do?br_orgtree=link&flow_flag=1&gz_module=0" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/bonus/inform.do?br_orgtree=link&flow_flag=1&gz_module=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a">单项奖金发放</font></hrms:link>
		              </td>
		            </tr>	
		          </hrms:priv> 
		   	  <%
       }
          %>       
		          <hrms:priv func_id="32404"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/tax/gz_tax_org_tree.do?b_query=link&is_back=not" target="il_body" onclick="turn();"><img src="/images/jh.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_accounting/tax/gz_tax_org_tree.do?b_query=link&is_back=not" target="il_body" onclick="turn();"><font id="a001" class="menu_a">所得税管理</font></hrms:link>
		              </td>
		            </tr>                    
		          </hrms:priv>    
		          <hrms:priv func_id="32415">
		          <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_accounting/sp_flow/init_sp_flow.do?b_init=link&gz_module=0&init=first&binit=first&cinit=first" target="il_body" onclick="turn();"><img src="/images/bjbb.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
	                <hrms:link href="/gz/gz_accounting/sp_flow/init_sp_flow.do?b_init=link&gz_module=0&init=first&binit=first&cinit=first" target="il_body" onclick="turn();"><font id="a001" class="menu_a">审批流程</font></hrms:link>
		              </td>
		            </tr>  
		            </hrms:priv>  
		          </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>
<hrms:priv func_id="32405">		
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>薪资总额</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		            <hrms:priv func_id="32405"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_amount/gz_gross_tree.do?b_query=link" target="il_body" ><img src="/images/hmc.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_amount/gz_gross_tree.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">薪资总额</font></hrms:link>
		              </td>
		            </tr>
		            </hrms:priv>

		          </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>
<hrms:priv func_id="32407">			
		<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="4">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>薪资分析</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		          <hrms:priv func_id="324071"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_analyse/gzAnalyseList.do?b_query=link&gz_module=0" target="il_body" ><img src="/images/lstatic.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_analyse/gzAnalyseList.do?b_query=link&gz_module=0" target="il_body" ><font id="a001" class="menu_a">薪资分析表</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>
		          <hrms:priv func_id="324072"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="javascript:analyseChartinit();"><img src="/images/lstatic.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="javascript:analyseChartinit();"><font id="a001" class="menu_a">薪资分析图</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>	
		           <hrms:priv func_id="324073"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_analyse/gz_fare/fare_analyse_orgtree.do?b_query=query&opt=init&type=0" target="il_body" ><img src="/images/lstatic.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_analyse/gz_fare/fare_analyse_orgtree.do?b_query=query&opt=init&type=0" target="il_body" ><font id="a001" class="menu_a">发放进展表</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>		
		          
		           <hrms:priv func_id="324074"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_analyse/historydata/salary_set_list.do?b_query=query&gz_module=0" target="il_body" ><img src="/images/lstatic.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_analyse/historydata/salary_set_list.do?b_query=query&gz_module=0" target="il_body" ><font id="a001" class="menu_a">薪资历史数据</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>	          
		          </table>
		   </div>
		 </td>
		  </tr>
		</table>
</hrms:priv>		
<hrms:priv func_id="32408,32409,32410,32411,32412,32413,32415">		
		
		<table cellpadding=0 cellspacing=0 width="159" height="500" class=menu_table index="5">
		  <tr style="cursor:hand;">
		    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
		  </tr>
		  <tr>
		    <td valign="top">
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
		     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		          <hrms:priv func_id="32412"> 		     
		           <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/org/gzdatamaint/gzdatamaint.do?b_addsubclass=link&tagname=1&infor=2&gzflag=2" target="il_body" ><img src="/images/hmuster.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/org/gzdatamaint/gzdatamaint.do?b_addsubclass=link&tagname=1&infor=2&gzflag=2" target="il_body" ><font id="a001" class="menu_a">相关子集设置</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv> 
		          <hrms:priv func_id="32413"> 
		           <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/org/gzdatamaint/gz_org_tree.do?b_query=link&infor=2&gzflag=2" target="il_body" onclick="turn();"><img src="/images/hmuster.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/org/gzdatamaint/gz_org_tree.do?b_query=link&infor=2&gzflag=2" target="il_body" onclick="turn();"><font id="a001" class="menu_a">基本数据维护</font></hrms:link>
		              </td>
		            </tr>
				  </hrms:priv> 		            
		          <hrms:priv func_id="32408">  
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/templateset/gz_templatelist.do?b_query=link0&gz_module=0" target="il_body" ><img src="/images/hmuster.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/templateset/gz_templatelist.do?b_query=link0&gz_module=0" target="il_body" ><font id="a001" class="menu_a">薪资类别</font></hrms:link>
		              </td>
		            </tr>
		          </hrms:priv>
		          <hrms:priv func_id="32409"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/templateset/tax_table/initTaxTable.do?b_init=init" target="il_body" ><img src="/images/jhhs.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		<hrms:link href="/gz/templateset/tax_table/initTaxTable.do?b_init=init" target="il_body" ><font id="a001" class="menu_a">税率表</font></hrms:link>
		              </td>
		            </tr>
		           </hrms:priv> 
		           <hrms:priv func_id="32410"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/templateset/standard/standardPackage.do?b_query=init" target="il_body" ><img src="/images/gzfh.gif" border=0></hrms:link></td>
		            </tr>
		            
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/templateset/standard/standardPackage.do?b_query=init" target="il_body" ><font id="a001" class="menu_a">薪资标准</font></hrms:link>
		              </td>
		            </tr>  
		           </hrms:priv>   
		            <hrms:priv func_id="32411"> 
		          <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/gz_amount/init_parameter_config.do?b_query=link&opt=init" target="il_body" ><img src="/images/px.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/gz_amount/init_parameter_config.do?b_query=link&opt=init" target="il_body" ><font id="a001" class="menu_a">薪资总额参数</font></hrms:link>
		              </td>
		            </tr>
		            </hrms:priv>	
		            	  <%
          	if(ver.searchFunctionId("3240214")){ 
          %>
		          <hrms:priv func_id="324021402"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/bonus/param.do?br_menu=link" target="il_body" ><img src="/images/px.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/bonus/param.do?br_menu=link" target="il_body" ><font id="a001" class="menu_a">单项奖金参数设置</font></hrms:link>
		              </td>
		            </tr> 
		           </hrms:priv>	 
		           	            	  <%
          }
          %>
		           <hrms:priv func_id="324021502"> 
		            <tr>
		              <td  align="center" class="loginFont" ><hrms:link href="/gz/premium/param.do?b_query=link" target="il_body" ><img src="/images/px.gif" border=0></hrms:link></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" >
		                <hrms:link href="/gz/premium/param.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">月奖金参数设置</font></hrms:link>
		              </td>
		            </tr> 
		           </hrms:priv>
		           <tr>
		              <td  align="center" class="loginFont" ></td>
		           </tr>                          
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



                                                                                                                                                       