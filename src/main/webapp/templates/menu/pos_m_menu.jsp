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
   var  divHeight = window.screen.availHeight - window.screenTop -215;
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">  
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol">    
	<hrms:priv func_id="2501">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>查询浏览</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display=block;"  id=menu1> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="25011"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/inform/pos/searchorgbrowse.do?b_query=link" target="il_body" function_id="xxx" onclick="turn()"><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/inform/pos/searchorgbrowse.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a" onclick="turn()">信息浏览</font></hrms:link>
	              </td>
	            </tr>   
	          </hrms:priv>     
	         <hrms:priv func_id="25010">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/query_interface.do?b_query=link&a_inforkind=3&home=1" target="il_body" function_id="xxx"><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/query_interface.do?b_query=link&a_inforkind=3&home=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">快速查询</font></hrms:link>
	              </td>
	            </tr>
	            <hrms:priv func_id="2501002">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/hquery_interface.do?a_query=1&b_query=link&a_inforkind=3&home=1" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/hquery_interface.do?a_query=1&b_query=link&a_inforkind=3&home=1" target="il_body" ><font id="a001" class="menu_a">简单查询</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="2501003">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/hquery_interface.do?a_query=2&b_query=link&a_inforkind=3" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/hquery_interface.do?a_query=2&b_query=link&a_inforkind=3" target="il_body" ><font id="a001" class="menu_a">通用查询</font></hrms:link>
	              </td>
	            </tr>   
	            </hrms:priv>         
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/query_interface.do?b_gquery=link&type=3&home=3" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/query_interface.do?b_gquery=link&type=3&home=3" target="il_body" ><font id="a001" class="menu_a">常用查询</font></hrms:link>
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
	<hrms:priv func_id="2502">    
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>统计分析</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="25020"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/static/select_field.do?b_query=link&a_inforkind=3" target="il_body" function_id="xxx"><img src="/images/lstatic.gif" border=0></hrms:link></td>
	
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/static/select_field.do?b_query=link&a_inforkind=3" target="il_body" function_id="xxx"><font id="a001" class="menu_a">简单统计</font></hrms:link>
	
	              </td>
	            </tr>
	         </hrms:priv>
	         <hrms:priv func_id="25021">         
	            <tr>
	
	              <td  align="center" class="loginFont" ><a href="/general/static/select_static_fields.do?b_query=link&a_inforkind=3" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/select_static_fields.do?b_query=link&a_inforkind=3" target="il_body"><font id="a003" class="menu_a" >通用统计</font></a></td>
	
	            </tr> 
	         </hrms:priv>
	         <hrms:priv func_id="25022">             
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/two_dim_static.do?b_query=link&a_inforkind=3" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/two_dim_static.do?b_query=link&a_inforkind=3" target="il_body"><font id="a003" class="menu_a" >二维统计</font></a></td>
	            </tr> 
	         </hrms:priv>
	         <hrms:priv func_id="25023">                  
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=3&home=0" target="il_body" onclick="turn()"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=3&home=0" target="il_body" onclick="turn()"><font id="a003" class="menu_a" >常用统计</font></a></td>
	            </tr> 
	         </hrms:priv>
	         <hrms:priv func_id="25024">             
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/singlestatic/single_static.do?b_query=link&a_inforkind=3" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/singlestatic/single_static.do?b_query=link&a_inforkind=3" target="il_body"><font id="a003" class="menu_a" >单项统计</font></a></td>
	            </tr>   
	         </hrms:priv>                                 
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="2503"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>花 名 册</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="2503101"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/muster/select_muster_fields.do?b_query=link&a_inforkind=3" target="il_body" function_id="xxx"><img src="/images/hmuster.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/muster/select_muster_fields.do?b_query=link&a_inforkind=3" target="il_body" function_id="xxx"><font id="a001" class="menu_a">新建花名册</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>
	         <hrms:priv func_id="25031">                   
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchroster.do?b_search=link&a_inforkind=3&result=0" target="il_body" onclick="turn();"><img src="/images/hmuster.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchroster.do?b_search=link&a_inforkind=3&result=0" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >常用花名册</font></a></td>
	            </tr> 
	         </hrms:priv>
	         <hrms:priv func_id="25032">             
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=41&a_inforkind=3&result=0" target="il_body" onclick="turn();"><img src="/images/ll.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=41&a_inforkind=3&result=0" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >高级花名册</font></a></td>
	            </tr> 
	         </hrms:priv>                 
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="2504"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="4">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>说 明 书</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="2504"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=4" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/card.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=4" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a" >职位说明书</font></hrms:link>
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
	<hrms:priv func_id="2505"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>职位体系</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="25050"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4" target="il_body" function_id="xxx"><img src="/images/organization.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4" target="il_body" function_id="xxx"><font id="a001" class="menu_a">岗位系列编码</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>
	         <hrms:priv func_id="25051">                   
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/org/orginfo/searchorgtree.do?b_query=link&code=${userView.managePrivCodeValue}" target="il_body"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/org/orginfo/searchorgtree.do?b_query=link&code=${userView.managePrivCodeValue}" target="il_body"><font id="a003" class="menu_a" >职位编码</font></a></td>
	            </tr> 
	         </hrms:priv>
	         <hrms:priv func_id="25052">             
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchhistoryorgmap.do?b_search=link" target="il_body"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchhistoryorgmap.do?b_search=link" target="il_body"><font id="a003" class="menu_a" >历史机构</font></a></td>
	            </tr> 
	          </hrms:priv>                  
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="2506"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="6">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);"><span><span id=arrow6><img src="/images/darrow.gif" border=0></span>信息维护</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu6> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="25060"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/dutyinfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=duty&kind=0&target=mil_body" target="il_body" function_id="xxx"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/dutyinfo/editorginfo.do?b_search=link&action=editorginfodata.do&edittype=new&treetype=duty&kind=0&target=mil_body" target="il_body" function_id="xxx"><font id="a001" class="menu_a">信息维护</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>
	         <!-- 
	         <hrms:priv func_id="25061"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/posparameter/ps_codeset.do?b_search=link" target="il_body" function_id="xxx"><img src="/images/organization.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/posparameter/ps_codeset.do?b_search=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">职务代码设置</font></hrms:link>
	              </td>
	            </tr>
	          </hrms:priv>
	           -->
	          <hrms:priv func_id="25062"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/posparameter/ps_parameter.do?b_search=link&flag=pos" target="il_body" function_id="xxx"><img src="/images/login_type.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/posparameter/ps_parameter.do?b_search=link&flag=pos" target="il_body" function_id="xxx"><font id="a001" class="menu_a">参数设置</font></hrms:link>
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



                                                                                                                                                       