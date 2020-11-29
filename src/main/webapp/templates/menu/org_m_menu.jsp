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
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -120;
   function turn()
   {
   /*
    var menucolobj=document.getElementById("menucol"); 
	var menusplit=document.getElementById("split");    
	if(parent.myBody.cols != '8,*')
	{
		parent.myBody.cols = '8,*';
		menucolobj.style.display="none";
		menusplit.src="/images/right_arrow.gif";
		menusplit.alt='open';			
	}
	else
	{
		parent.myBody.cols = '170,*';
		menucolobj.style.display="";
		menusplit.src="/images/left_arrow.gif";
		menusplit.alt='close';			
	}
	*/
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
   var  divHeight = window.screen.availHeight - window.screenTop -205;
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">    
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol">  
	<hrms:priv func_id="2301">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table  index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>查询浏览</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display=block;"  id=menu1> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	          <hrms:priv func_id="23011">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/inform/org/searchorgbrowse.do?b_query=link" target="il_body" function_id="xxx" onclick="turn()"><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/inform/org/searchorgbrowse.do?b_query=link" target="il_body" function_id="xxx" onclick="turn()"><font id="a001" class="menu_a">信息浏览</font></hrms:link>
	              </td>
	            </tr>  
	          </hrms:priv>         
	          <hrms:priv func_id="23010">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/query_interface.do?b_query=link&a_inforkind=2&home=10" target="il_body" function_id="xxx"><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/query_interface.do?b_query=link&a_inforkind=2&home=10" target="il_body" function_id="xxx"><font id="a001" class="menu_a">快速查询</font></hrms:link>
	              </td>
	            </tr>
	
	            <hrms:priv func_id="2301002">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/hquery_interface.do?a_query=1&b_query=link&a_inforkind=2&home=10" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/hquery_interface.do?a_query=1&b_query=link&a_inforkind=2&home=10" target="il_body" ><font id="a001" class="menu_a">简单查询</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="2301003">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/hquery_interface.do?a_query=2&b_query=link&a_inforkind=2" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/hquery_interface.do?a_query=2&b_query=link&a_inforkind=2" target="il_body" ><font id="a001" class="menu_a">通用查询</font></hrms:link>
	              </td>
	            </tr>   
	             </hrms:priv>         
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/query_interface.do?b_gquery=link&type=2&home=3" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/query_interface.do?b_gquery=link&type=2&home=3" target="il_body" ><font id="a001" class="menu_a">常用查询</font></hrms:link>
	              </td>
	            </tr>            
	            
	         </hrms:priv> 
	           
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	
	</table>
	</hrms:priv>
	<hrms:priv func_id="2302">     
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>统计分析</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="23020">
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/select_field.do?b_query=link&a_inforkind=2"  target="il_body" function_id="xxx"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/static/select_field.do?b_query=link&a_inforkind=2" target="il_body" function_id="xxx"><font id="a001" class="menu_a">简单统计</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>
	         <hrms:priv func_id="23021">          
	            <tr>
	              <td  align="center" class="loginFont" >
	              	<a href="/general/static/select_static_fields.do?b_query=link&a_inforkind=2" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/select_static_fields.do?b_query=link&a_inforkind=2" target="il_body"><font id="a003" class="menu_a" >通用统计</font></a></td>
	            </tr> 
	        </hrms:priv>
	        <hrms:priv func_id="23022">      
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/two_dim_static.do?b_query=link&a_inforkind=2" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/two_dim_static.do?b_query=link&a_inforkind=2" target="il_body"><font id="a003" class="menu_a" >二维统计</font></a></td>
	            </tr> 
	         </hrms:priv>
	         <hrms:priv func_id="23023">          
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=2&home=0" target="il_body" onclick="turn()"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=2&home=0" target="il_body" onclick="turn()"><font id="a003" class="menu_a" >常用统计</font></a></td>
	            </tr> 
	         </hrms:priv>
	         <hrms:priv func_id="23024">      
	           <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/static/singlestatic/single_static.do?b_query=link&a_inforkind=2" target="il_body" function_id="xxx"><img src="/images/lstatic.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/static/singlestatic/single_static.do?b_query=link&a_inforkind=2" target="il_body" function_id="xxx"><font id="a001" class="menu_a">单项统计</font></hrms:link>
	              </td>
	            </tr>    
	           </hrms:priv>                                  
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="2303"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>花 名 册</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="2303101">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/muster/select_muster_fields.do?b_query=link&a_inforkind=2" target="il_body" function_id="xxx"><img src="/images/hmuster.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/muster/select_muster_fields.do?b_query=link&a_inforkind=2" target="il_body" function_id="xxx"><font id="a001" class="menu_a">新建花名册</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>  
	         <hrms:priv func_id="23031">       
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchroster.do?b_search=link&a_inforkind=2&result=0" target="il_body" onclick="turn();"><img src="/images/hmuster.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchroster.do?b_search=link&a_inforkind=2&result=0" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >常用花名册</font></a></td>
	            </tr> 
	         </hrms:priv>  
	         <hrms:priv func_id="23032">  
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=21&a_inforkind=2&result=0" target="il_body" onclick="turn();"><img src="/images/ll.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=21&a_inforkind=2&result=0" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >高级花名册</font></a></td>
	            </tr> 
	          </hrms:priv>             
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="2304"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="4">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>登 记 表</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	          <hrms:priv func_id="2304">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=2" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/card.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=2" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a" >登 记 表</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>  
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="2305"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>组织机构</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="23050"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/org/orginfo/searchorgtree.do?b_query=link&code=${userView.managePrivCodeValue}" target="il_body" function_id="xxx"><img src="/images/organization.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/org/orginfo/searchorgtree.do?b_query=link&code=${userView.managePrivCodeValue}" target="il_body" function_id="xxx"><font id="a001" class="menu_a">机构编码</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>  
	         <hrms:priv func_id="23051">         
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchorgmap.do?b_search=link" target="il_body" onclick="turn()"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchorgmap.do?b_search=link" target="il_body" onclick="turn()"><font id="a003" class="menu_a" >机构图</font></a></td>
	            </tr> 
	         </hrms:priv>  
	         <hrms:priv func_id="23052">             
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchhistoryorgmap.do?b_search=link" target="il_body" onclick="turn()"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchhistoryorgmap.do?b_search=link" target="il_body" onclick="turn()"><font id="a003" class="menu_a" >历史机构</font></a></td>
	            </tr> 
	          </hrms:priv>
	          <!--  <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/org_pigeonhole.do" target="il_body"><img src="/images/query_set.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/org_pigeonhole.do" target="il_body"><font id="a003" class="menu_a" >机构归档</font></a></td>
	            </tr> -->
	                        
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="2306"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="6">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);"><span><span id=arrow6><img src="/images/darrow.gif" border=0></span>信息维护</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu6> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="23060">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/orginfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=org&kind=2&target=mil_body" target="il_body" function_id="xxx" onclick="turn()"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/orginfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=org&kind=2&target=mil_body" target="il_body" function_id="xxx"><font id="a001" class="menu_a" onclick="turn()">信息维护</font></hrms:link>
	              </td>
	              
	            </tr>
	         </hrms:priv> 
	         <hrms:priv func_id="23061">	                  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/org/autostatic/confset/datasynchro.do?b_init=link" target="il_body" function_id="xxx"><img src="/images/organization.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/org/autostatic/confset/datasynchro.do?b_init=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">数据联动</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv> 	
	         <hrms:priv func_id="23064"> 	         
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/org/orgpre/get_org_tree.do?b_query=link&infor=2&unit_type=3" target="il_body" onclick="turn();"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/org/orgpre/get_org_tree.do?b_query=link&infor=2&unit_type=3" target="il_body"><font id="a001" class="menu_a" onclick="turn();">编制管理</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>		                      
	         <hrms:priv func_id="23062"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/posparameter/ps_parameter.do?b_search_unit=link" target="il_body" function_id="xxx"><img src="/images/login_type.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/posparameter/ps_parameter.do?b_search_unit=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">机构编制参数设置</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>
	         <!-- 
            	<tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/org/orgdata/org_tree.do?b_init=link" target="il_body" function_id="xxx"><img src="/images/login_type.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/org/orgdata/org_tree.do?b_init=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">机构维护</font></hrms:link>
	              </td>
	            </tr>
 			  -->	            
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
</td>

<!-- 
<td width="8" align="right" valign="top" class="menu_split">
<IMG alt='close' id="split"
      src="/images/left_arrow.gif" width=7 border=0 height="28" onclick="turn();">
</td>
  -->	 
</tr>
</table> 
<script language="javascript">
  showFirst();
</script>  



                                                                                                                                                       