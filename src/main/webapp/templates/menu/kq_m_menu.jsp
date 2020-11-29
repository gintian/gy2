<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient" %>
<%@ page import="com.hrms.hjsj.sys.VersionControl" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
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
	VersionControl ver_ctrl=new VersionControl();		      	              
	EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
    ver_ctrl.setVer(lock.getVersion());
	String[] funcs ="2702011,2702010,2702020,2702021,2702024,2702032".split(","); //人员变动比对，生成日考勤明细表，统计，計算，个人业务处理，浏览日明细
	boolean bfunc=false;
	for(int r=0;r<funcs.length;r++)
	{
	    bfunc=ver_ctrl.searchFunctionId(funcs[r],userView.hasTheFunction(funcs[r]));
	    if(bfunc)
	       break;
	} 
	String url="/kq/register/daily_register.do?b_search=link&action=daily_registerdata.do&target=mil_body&flag=noself&privtype=kq";
	if(!bfunc&&ver_ctrl.searchFunctionId("2702030",userView.hasTheFunction("2702030")))
	   url="/kq/register/select_collect.do?b_search=link&action=select_collectdata.do&target=mil_body&flag=noself&privtype=kq";
	        	    
%>
<SCRIPT LANGUAGE="JavaScript">
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
   var  divHeight = window.screen.availHeight - window.screenTop -80;   
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">  
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol"> 
	<hrms:priv func_id="2701">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>申请登记</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display=block;"  id=menu1> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	       <hrms:priv func_id="27010">   
	        <tr>
	         <td  align="center" class="loginFont" ><hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q11&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q11&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">加班申请</font></hrms:link>
	           </td>
	        </tr>
	        </hrms:priv>
	         <hrms:priv func_id="27011"> 
	        <tr>
	           <td  align="center" class="loginFont" ><hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&viewPost=kq&table=Q15&privtype=kq" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q15&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">请假申请</font></hrms:link>
	           </td>
	        </tr>
	        </hrms:priv>
	         <hrms:priv func_id="27012"> 
	        <tr>
	           <td  align="center" class="loginFont" ><hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q13&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/app_check_in/all_app.do?b_query=link&action=all_app_data.do&target=mil_body&table=Q13&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">公出申请</font></hrms:link>
	            </td>
	         </tr>
	       </hrms:priv>  
	       <hrms:priv func_id="27013"> 
	        <tr>
	           <td  align="center" class="loginFont" ><hrms:link href="/kq/app_check_in/exchange_class/exchange.do?b_search=link&action=exchangedata.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/app_check_in/exchange_class/exchange.do?b_search=link&action=exchangedata.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">调班申请</font></hrms:link>
	            </td>
	         </tr>
	       </hrms:priv> 
	       <hrms:priv func_id="27014"> 
	        <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/app_check_in/redeploy_rest/redeploy.do?b_search=link&action=redeploydata.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();" function_id="xxx"><img src="/images/apply.gif" border=0></hrms:link></td>
	         </tr>
	        <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/app_check_in/redeploy_rest/redeploy.do?b_search=link&action=redeploydata.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();" ><font id="a001" class="menu_a">调休申请</font></hrms:link>
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
	<hrms:priv func_id="2702">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>考勤数据</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	          <hrms:priv func_id="27020">   
	            <tr>

	              <td  align="center" class="loginFont" ><hrms:link href="<%=url %>" target="il_body" onclick="turn();"><img src="/images/employ_data.gif" border=0></hrms:link></td>

	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >

	                <hrms:link href="<%=url %>" target="il_body" onclick="turn();"><font id="a001" class="menu_a">员工明细数据</font></hrms:link>

	              </td>
	            </tr>  
	            </hrms:priv>
	           <hrms:priv func_id="27021">          
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/register/browse_orgregister.do?b_search=link&action=search_orgregisterdata.do&target=mil_body&viewPost=kq&flag=noself&privtype=kq" target="il_body" onclick="turn();"><img src="/images/organization.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/register/browse_orgregister.do?b_search=link&action=search_orgregisterdata.do&target=mil_body&viewPost=kq&flag=noself&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">部门考勤浏览</font></a></td>
	            </tr> 
	            </hrms:priv>
	
	           <hrms:priv func_id="2702025,2702027"> 
	           <!--<tr>
	             <td  align="center" class="loginFont" ><hrms:link href="/kq/register/browse_register.do?b_search=link&action=browse_registerdata.do&target=mil_body&flag=noself" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></hrms:link></td>
	           </tr>
	           <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/register/browse_register.do?b_search=link&action=browse_registerdata.do&target=mil_body&flag=noself" target="il_body" onclick="turn();"><font id="a001" class="menu_a">考勤待批数据</font></hrms:link>
	           </td>
	         </tr>-->
	          </hrms:priv>
	           <hrms:priv func_id="2702025">   
	           <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/register/search_register.do?b_search=link&action=search_registerdata.do&target=mil_body&viewPost=kq&flag=noself&privtype=kq" target="il_body" onclick="turn();"><img src="/images/cardset.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/register/search_register.do?b_search=link&action=search_registerdata.do&target=mil_body&viewPost=kq&flag=noself&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">考勤月末处理</font></hrms:link>
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
	<hrms:priv func_id="2705">  
	 <table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>历史查询</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	          <hrms:priv func_id="27050"> 
	          <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/register/history/dailybrowse.do?b_search=link&action=dailybrowsedata.do&target=mil_body&viewPost=kq&a_inforkind=1&privtype=kq" target="il_body" onclick="turn();"><img src="/images/employ_data.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/register/history/dailybrowse.do?b_search=link&action=dailybrowsedata.do&target=mil_body&viewPost=kq&a_inforkind=1&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">员工历史数据</font></hrms:link>
	              </td>
	            </tr> 
	            </hrms:priv>
	            <hrms:priv func_id="27051">                    
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/register/history/dailyorgbrowse.do?b_search=link&action=dailyorgbrowsedata.do&target=mil_body&viewPost=kq&a_inforkind=1&privtype=kq" target="il_body" onclick="turn();"><img src="/images/hmc.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/register/history/dailyorgbrowse.do?b_search=link&action=dailyorgbrowsedata.do&target=mil_body&viewPost=kq&a_inforkind=1&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">部门历史数据</font></a></td>
	            </tr> 
	              </hrms:priv>
	              
	             <hrms:priv func_id="27053">
	              <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/register/history/tab_check.do?b_tab=link&action=history_check.do&target=mil_body&viewPost=kq&start_date=&end_date=&select_time_type=2&wo=3&select_flag=1&dotflag=1" target="il_body" onclick="turn();"><img src="/images/apply.gif" border=0></a></td>
	              </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/register/history/tab_check.do?b_tab=link&action=history_check.do&target=mil_body&viewPost=kq&start_date=&end_date=&select_time_type=2&wo=3&select_flag=1&dotflag=1" target="il_body" onclick="turn();"><font id="a001" class="menu_a">业务历史数据</font></a></td>
	            </tr>
	             </hrms:priv>
	             <hrms:priv func_id="27054">
	              <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/machine/historical/search_card.do?b_query=link&action=search_card_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><img src="/images/zb.gif" border=0></a></td>
	              </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/machine/historical/search_card.do?b_query=link&action=search_card_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">刷卡历史数据</font></a></td>
	            </tr>
	             </hrms:priv>	                 
	              <hrms:priv func_id="27052">
	              <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/register/history/statfx.do?b_search=link&action=statfxdata.do&target=mil_body&viewPost=kq&a_inforkind=1&privtype=kq" target="il_body" onclick="turn();"><img src="/images/lstatic.gif" border=0></a></td>
	              </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/register/history/statfx.do?b_search=link&action=statfxdata.do&target=mil_body&viewPost=kq&a_inforkind=1&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">统计分析</font></a></td>
	            </tr>
	             </hrms:priv> 
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="2704">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="4">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>假期管理</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/feast_manage/manager.do?b_search=link&action=hols_manager.do&target=mil_body&viewPost=kq&flag=noself&kind=2&privtype=kq" target="il_body" onclick="turn();"><img src="/images/tx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/feast_manage/manager.do?b_search=link&action=hols_manager.do&target=mil_body&viewPost=kq&flag=noself&kind=2&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">假期管理</font></hrms:link>
	              </td>
	            </tr>  
	            <hrms:priv func_id="27044"> 
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/kqself/plan/annual_plan_institute.do?b_query=link&table=q29&privtype=kq" target="il_body" ><img src="/images/ss.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/kqself/plan/annual_plan_institute.do?b_query=link&table=q29&privtype=kq" target="il_body" ><font id="a016" class="menu_a" >休假计划制定</font></a></td>
	            </tr>  
	            </hrms:priv>          
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv> 
	 <hrms:priv func_id="2708">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>基础信息</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="2708"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/options/manager/usermanager.do?b_search=link&action=usermanagerdata.do&target=mil_body&viewPost=kq&flag=noself&menu=1&privtype=kq" target="il_body" function_id="xxx"><img src="/images/zd.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/options/manager/usermanager.do?b_search=init&action=usermanagerdata.do&target=mil_body&viewPost=kq&flag=noself&menu=1&privtype=kq" target="il_body" ><font id="a001" class="menu_a">人员基本信息</font></hrms:link>
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
	 <hrms:priv func_id="2706">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="6">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);"><span><span id=arrow6><img src="/images/darrow.gif" border=0></span>刷卡数据</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu6> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="27060"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/machine/search_card.do?b_query=link&action=search_card_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" function_id="" onclick="turn();"><img src="/images/zb.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/machine/search_card.do?b_query=link&action=search_card_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">刷卡数据</font></hrms:link>
	              </td>
	            </tr>   
	            </hrms:priv> 
	            <hrms:priv func_id="27062"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/machine/analyse/data_analyse.do?b_query=link&action=data_analyse_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" function_id="" onclick="turn();"><img src="/images/mc.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/machine/analyse/data_analyse.do?b_query=link&action=data_analyse_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">数据处理</font></hrms:link>
	              </td>
	            </tr>   
	            </hrms:priv> 
	            <hrms:priv func_id="27061"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/machine/kq_rule.do?b_query=link&action=kq_rule_data.do&target=mil_body&privtype=kq" target="il_body" function_id=""><img src="/images/rzfs.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/machine/kq_rule.do?b_query=link&action=kq_rule_data.do&target=mil_body&privtype=kq" target="il_body"><font id="a001" class="menu_a">文件规则</font></hrms:link>
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
	<hrms:priv func_id="2707">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="7">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle7 onclick="menuChange(menu7,divHeight,menuTitle7,arrow7);"><span><span id=arrow7><img src="/images/darrow.gif" border=0></span>排班管理</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu7> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	        <hrms:priv func_id="27070"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/team/array/search_array.do?b_query=link&action=search_array_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" function_id="" onclick="turn();"><img src="/images/tool.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/team/array/search_array.do?b_query=link&action=search_array_data.do&target=mil_body&viewPost=kq&privtype=kq&state=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a">排班处理</font></hrms:link>
	              </td>
	            </tr>   
	            </hrms:priv> 
	            <hrms:priv func_id="27073">   
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/team/history/search_array.do?b_query=link&action=search_array_data.do&target=mil_body&viewPost=kq&privtype=kq" target="il_body" function_id="" onclick="turn();"><img src="/images/employ_data.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/team/history/search_array.do?b_query=link&action=search_array_data.do&target=mil_body&viewPost=kq&privtype=kq&state=0" target="il_body" function_id="" onclick="turn();"><font id="a001" class="menu_a">历史数据</font></hrms:link>
	              </td>
	            </tr>  
	            </hrms:priv>    
	           <!-- <hrms:priv func_id="27071">      
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/team/array_set/search_array.do?b_query=link&action=search_array_data.do&target=mil_body" target="il_body" function_id="xxx"><img src="/images/zg.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/team/array_set/search_array.do?b_query=link&action=search_array_data.do&target=mil_body" target="il_body" ><font id="a001" class="menu_a">班组设置</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>-->
	          </table>
		</form>  
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv> 
	<hrms:priv func_id="2703">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="8">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle8 onclick="menuChange(menu8,divHeight,menuTitle8,arrow8);"><span><span id=arrow8><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu8> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	       <hrms:priv func_id="27030"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/options/struts/select_parameter.do?b_query=link" target="il_body" function_id="27030"><img src="/images/tool.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/options/struts/select_parameter.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">结构参数</font></hrms:link>
	              </td>
	            </tr>   
	      </hrms:priv>
	      <hrms:priv func_id="27031">            
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/options/duration_detail.do" target="il_body" function_id="xxx"><img src="/images/zg.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/options/duration_detail.do" target="il_body" ><font id="a001" class="menu_a">考勤期间</font></hrms:link>
	              </td>
	            </tr>
	     </hrms:priv>
	     <hrms:priv func_id="27032">              
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/options/search_feast.do?b_query=link" target="il_body"><img src="/images/zb.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/options/search_feast.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >节假日维护</font></a></td>
	           </tr> 
	      </hrms:priv>
	      <hrms:priv func_id="27034">   
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/options/kq_rest.do?b_query=link" target="il_body"><img src="/images/rz.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/options/kq_rest.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >公休日维护</font></a></td>
	            </tr> 
	      </hrms:priv>
	      <hrms:priv func_id="27036">                
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/options/search_rest.do?b_query=link&mege=4" target="il_body"><img src="/images/aaa.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/options/search_rest.do?b_query=link&mege=4" target="il_body"><font id="a003" class="menu_a" >公休日倒休</font></a></td>
	            </tr> 
	     </hrms:priv>  
	      <hrms:priv func_id="27033"> 
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/options/kq_item_detail.do" target="il_body"><img src="/images/yw.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/options/kq_item_detail.do" target="il_body"><font id="a003" class="menu_a" >考勤规则</font></a></td>
	            </tr>
	      </hrms:priv>
	        <hrms:priv func_id="27038"> 
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/options/class/kq_class.do?b_init=link" target="il_body"><img src="/images/zb.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/options/class/kq_class.do?b_init=link" target="il_body"><font id="a003" class="menu_a" >基本班次</font></a></td>
	            </tr>
	      </hrms:priv>
	     <hrms:priv func_id="27039">            
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/options/machine/machine_location.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/zg.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/options/machine/machine_location.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">考勤机管理</font></hrms:link>
	              </td>
	            </tr>
	     </hrms:priv>
	     <hrms:priv func_id="27039a">            
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/options/adjustcode/adjustcode.do?b_search=link" target="il_body" function_id="xxx"><img src="/images/zb.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/options/adjustcode/adjustcode.do?b_search=link" target="il_body" ><font id="a001" class="menu_a">调整指标</font></hrms:link>
	              </td>
	            </tr>
	     </hrms:priv>
	     <hrms:priv func_id="27035">                                          
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/options/initcode/kq_setdata.do?br_query=link" target="il_body" function_id="xxx"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/options/initcode/kq_setdata.do?br_query=link" target="il_body" ><font id="a001" class="menu_a">数据初始化</font></hrms:link>
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



                                                                                                                                                       