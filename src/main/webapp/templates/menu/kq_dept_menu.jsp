<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient" %>
<%@ page import="com.hrms.hjsj.sys.VersionControl" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
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
	VersionControl ver_ctrl=new VersionControl();		      	              
	EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
	ver_ctrl.setVer(lock.getVersion());
	String[] funcs =StringUtils.split("0C3105,0C3100,0C3120,0C3121,0C3123,0C3113",","); //人员变动比对，生成日考勤明细表，统计，計算，个人业务处理，浏览日明细
	boolean bfunc=false;
	try
	{
	   for(int r=0;r<funcs.length;r++)
	   {
	      bfunc=ver_ctrl.searchFunctionId(funcs[r],userView.hasTheFunction(funcs[r]));
	      if(bfunc)
	       break;
	   } 
	}catch(Exception e)
	{
	  e.printStackTrace();
	}
	
	String url="/kq/register/daily_register.do?b_search=link&action=daily_registerdata.do&target=mil_body&flag=noself&privtype=kq";
	if(!bfunc&&ver_ctrl.searchFunctionId("0C3110",userView.hasTheFunction("0C3110")))//浏览权限中有浏览月汇总
	  url="/kq/register/select_collect.do?b_search=link&action=select_collectdata.do&target=mil_body&flag=noself&privtype=kq";
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -250;
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
	<hrms:priv func_id="0C34">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>申请登记</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	       <hrms:priv func_id="0C341">   
	        <tr>
	         <td  align="center" class="loginFont" ><hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q11&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q11&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">加班申请</font></hrms:link>
	           </td>
	        </tr>
	        </hrms:priv>
	         <hrms:priv func_id="0C342"> 
	        <tr>
	           <td  align="center" class="loginFont" ><hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q15&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q15&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">请假申请</font></hrms:link>
	           </td>
	        </tr>
	        </hrms:priv>
	         <hrms:priv func_id="0C343"> 
	        <tr>
	           <td  align="center" class="loginFont" ><hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q13&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q13&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">公出申请</font></hrms:link>
	            </td>
	         </tr>
	       </hrms:priv> 
	       <hrms:priv func_id="0C344"> 
	        <tr>
	           <td  align="center" class="loginFont" ><hrms:link href="/kq/app_check_in/exchange_class/exchange.do?b_search=link&action=exchangedata.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/app_check_in/exchange_class/exchange.do?b_search=link&action=exchangedata.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">调班申请</font></hrms:link>
	            </td>
	         </tr>
	       </hrms:priv> 
	       <hrms:priv func_id="0C345"> 
	        <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/app_check_in/redeploy_rest/redeploy.do?b_search=link&action=redeploydata.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();" function_id="xxx"><img src="/images/apply.gif" border=0></hrms:link></td>
	         </tr>
	        <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/app_check_in/redeploy_rest/redeploy.do?b_search=link&action=redeploydata.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();" ><font id="a001" class="menu_a">调休申请</font></hrms:link>
	              </td>
	        </tr> 
	       </hrms:priv> 
	        <hrms:priv func_id="0C346"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/machine/netsignin/empNetSingnin.do?b_query=link&action=empNetSingnin_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" function_id="" onclick="turn();"><img src="/images/ss.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/machine/netsignin/empNetSingnin.do?b_query=link&action=empNetSingnin_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">网上签到</font></hrms:link>
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
	<hrms:priv func_id="0C31,0C32,0C33,0C36,0C39">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="2">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>日常考勤</span></td>
	  </tr>  
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu2> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="0C31"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="<%=url %>" target="il_body" onclick="turn();"><img src="/images/employ_data.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="<%=url %>" target="il_body" onclick="turn();"><font id="a001" class="menu_a">员工明细数据</font></hrms:link>
	              </td>
	            </tr>  
	            </hrms:priv> 
	            <hrms:priv func_id="0C32"> 
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/register/audit_register.do?b_search=link&action=audit_registerdata.do&target=mil_body&viewPost=kq&flag=noself&privtype=kq" target="il_body" onclick="turn();"><img src="/images/tool.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/register/audit_register.do?b_search=link&action=audit_registerdata.do&target=mil_body&viewPost=kq&flag=noself&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">考勤待审数据</font></hrms:link>
	            </td>
	           </tr>
	           </hrms:priv> 
	           <hrms:priv func_id="0C33"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/register/history/dailybrowse.do?b_search=link&action=dailybrowsedata.do&target=mil_body&viewPost=kq&a_inforkind=1&privtype=kq" target="il_body" onclick="turn();"><img src="/images/employ_data.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/register/history/dailybrowse.do?b_search=link&action=dailybrowsedata.do&target=mil_body&viewPost=kq&a_inforkind=1&landing=myself&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">员工历史数据</font></hrms:link>
	              </td>
	            </tr> 
	            </hrms:priv>      
	            <hrms:priv func_id="0C36"> 
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/kqself/plan/annual_plan_institute.do?b_query=link&table=q29" target="il_body" ><img src="/images/ss.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/kqself/plan/annual_plan_institute.do?b_query=link&table=q29" target="il_body" ><font id="a016" class="menu_a" >休假计划制定</font></a></td>
	            </tr>  
	            </hrms:priv>
	            <hrms:priv func_id="0C39">
	              <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/register/history/statfx.do?b_search=link&action=statfxdata.do&target=mil_body&viewPost=kq&a_inforkind=1" target="il_body" onclick="turn();"><img src="/images/lstatic.gif" border=0></a></td>
	              </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/register/history/statfx.do?b_search=link&action=statfxdata.do&target=mil_body&viewPost=kq&a_inforkind=1" target="il_body" onclick="turn();"><font id="a001" class="menu_a">统计分析</font></a></td>
	            </tr>
	             </hrms:priv>        
	          </table>
	     </div> 
	   </td>
	  </tr>
	</table>
	</hrms:priv> 
	<hrms:priv func_id="0C35">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="4">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>排班管理</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu4> 
	       <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	
	         <hrms:priv func_id="0C35"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/team/array/search_array.do?b_query=link&action=search_array_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" function_id="" onclick="turn();"><img src="/images/tool.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/team/array/search_array.do?b_query=link&action=search_array_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">排班处理</font></hrms:link>
	              </td>
	            </tr>   
	        </hrms:priv> 
	        <hrms:priv func_id="0C353">   
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/team/history/search_array.do?b_query=link&action=search_array_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" function_id="" onclick="turn();"><img src="/images/employ_data.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/team/history/search_array.do?b_query=link&action=search_array_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" function_id="" onclick="turn();"><font id="a001" class="menu_a">历史数据</font></hrms:link>
	              </td>
	            </tr>  
	            </hrms:priv>                                                                        
	       </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv> 
	 <hrms:priv func_id="0C37">   
	<table cellpadding=0 cellspacing=0 width="159" height="400" class=menu_table index="6">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);"><span><span id=arrow6><img src="/images/darrow.gif" border=0></span>刷卡数据</span></td>
	  </tr>
	  <tr>
	    <td valign="top">
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu6> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="0C37"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/machine/search_card.do?b_query=link&action=search_card_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" function_id="" onclick="turn();"><img src="/images/zb.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/machine/search_card.do?b_query=link&action=search_card_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">刷卡数据</font></hrms:link>
	              </td>
	            </tr>   
	            </hrms:priv>   
	            <hrms:priv func_id="0C38"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/machine/analyse/data_analyse.do?b_query=link&action=data_analyse_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" function_id="" onclick="turn();"><img src="/images/mc.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/machine/analyse/data_analyse.do?b_query=link&action=data_analyse_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">数据处理</font></hrms:link>
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

</tr>
</table> 
<script language="javascript">
	showFirst();
</script>   

                                                                              