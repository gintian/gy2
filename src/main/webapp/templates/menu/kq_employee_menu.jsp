<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
    String kind="";
    String code="";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >"); 
      String codeid=RegisterInitInfoData.getKqPrivCode(userView);;
	  String codevalue=RegisterInitInfoData.getKqPrivCodeValue(userView);
      if("UN".equals(codeid))
	  {
	  	kind="2";
	  }else if("UM".equals(codeid))
	  {
	    kind="1";
	  }else if("@K".equals(codeid))
	  {
	    kind="0";
	  }
       code=codevalue;
   }   
   String isturn=SystemConfig.getPropertyValue("Menutogglecollapse"); 
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<head>
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -190;
   function turn()
   {
    <% 
	  if(isturn==null||!isturn.equals("false"))
	     out.println("parent.menupnl.toggleCollapse(false);");
	%>
   }      
</SCRIPT>
</head>
<body class=menuBodySet style="margin:0 0 0 0">  
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol">        
	<hrms:priv func_id="0B0">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="1">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>我的考勤</span></td>
	  </tr>  
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu1> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="0B04">     
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/kqself/card/carddata.do?b_query=link" target="il_body" ><img src="/images/query_set.gif" border=0></hrms:link></td>
	             </tr>
	              <tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/kq/kqself/card/carddata.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">我的刷卡数据</font></hrms:link>
	              </td>
	              </tr>
	              </hrms:priv> 
	            <hrms:priv func_id="0B01"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/kqself/details/month_details.do?b_query=link&dtable=Q03" target="il_body" function_id="xxx"><img src="/images/role.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/kq/kqself/details/month_details.do?b_query=link&dtable=Q03" target="il_body" function_id="xxx"><font id="a001" class="menu_a">我的明细信息</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	            <hrms:priv func_id="0B02"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/kqself/details/kq_details.do?b_query=link&table=Q03" target="il_body" function_id="xxx"><img src="/images/role.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/kq/kqself/details/kq_details.do?b_query=link&table=Q03" target="il_body" function_id="xxx"><font id="a001" class="menu_a">我的月汇总信息</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	             <hrms:priv func_id="0B03"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/kqself/class/kq_class.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/role.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/kq/kqself/class/kq_class.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">我的排班</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 	            
	          </table>
	     </div> 
	   </td>
	  </tr>
	</table>
	</hrms:priv> 
	<hrms:priv func_id="0B2">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="3">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>我的申请</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu3> 
	       <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	
	    <hrms:priv func_id="0B21"> 
	        <tr>
	         <td  align="center" class="loginFont" ><hrms:link href="/kq/kqself/search_kqself.do?b_query=link&table=Q11" target="il_body" ><img src="/images/employee.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/kqself/search_kqself.do?b_query=link&table=Q11" target="il_body" ><font id="a001" class="menu_a">加班申请</font></hrms:link>
	           </td>
	        </tr>
	     </hrms:priv> 
	     <hrms:priv func_id="0B22">     
	        <tr>
	           <td  align="center" class="loginFont" ><hrms:link href="/kq/kqself/search_kqself.do?b_query=link&table=Q15" target="il_body" ><img src="/images/query_set.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/kqself/search_kqself.do?b_query=link&table=Q15" target="il_body" ><font id="a001" class="menu_a">请假申请</font></hrms:link>
	           </td>
	        </tr>
	      </hrms:priv> 
	      <hrms:priv func_id="0B23">    
	        <tr>
	           <td  align="center" class="loginFont" ><hrms:link href="/kq/kqself/search_kqself.do?b_query=link&table=Q13" target="il_body" ><img src="/images/employee.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/kqself/search_kqself.do?b_query=link&table=Q13" target="il_body" ><font id="a001" class="menu_a">公出申请</font></hrms:link>
	            </td>
	         </tr>
	      </hrms:priv> 
	      <hrms:priv func_id="0B24"> 
	        <tr>
	           <td  align="center" class="loginFont" ><hrms:link href="/kq/kqself/exchange_class/exchangedata.do?b_search=link&action=exchangedata.do&target=mil_body" target="il_body" ><img src="/images/organization.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/kqself/exchange_class/exchangedata.do?b_search=link&action=exchangedata.do&target=mil_body" target="il_body"><font id="a001" class="menu_a">调班申请</font></hrms:link>
	            </td>
	         </tr>
	       </hrms:priv> 
	       <hrms:priv func_id="0B25"> 
	        <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/kq/kqself/redeploy_rest/redeploydata.do?b_search=link&action=redeploydata.do&target=mil_body" target="il_body" function_id="xxx"><img src="/images/tool.gif" border=0></hrms:link></td>
	         </tr>
	        <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/kq/kqself/redeploy_rest/redeploydata.do?b_search=link&action=redeploydata.do&target=mil_body" target="il_body"  ><font id="a001" class="menu_a">调休申请</font></hrms:link>
	              </td>
	        </tr> 
	       </hrms:priv> 
	       <hrms:priv func_id="0B26"> 
	            <tr>
	             <td  align="center" class="loginFont" >
	              	<a href="/kq/kqself/apply/my_annual_apply.do?b_query=link" target="il_body" ><img src="/images/organization.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              	<a href="/kq/kqself/apply/my_annual_apply.do?b_query=link" target="il_body" ><font id="a016" class="menu_a" >计划申请</font></a></td>
	           </tr> 
	      </hrms:priv>                                                                        
	       </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv> 
	<hrms:priv func_id="0B1">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="2">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>我的假期</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu2> 
	          <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           
	           <hrms:priv func_id="0B10"> 
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/kqself/annual/my_rest.do" target="il_body" ><img src="/images/employee.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/kqself/annual/my_rest.do" target="il_body" ><font id="a016" class="menu_a" >我的假期</font></a></td>
	            </tr> 
	            </hrms:priv> 
	             
	            <hrms:priv func_id="0B11"> 
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/kqself/plan/my_plan_info.do?b_query=link&table=q31" target="il_body" ><img src="/images/organization.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/kq/kqself/plan/my_plan_info.do?b_query=link&table=q31" target="il_body" ><font id="a016" class="menu_a" >休假计划</font></a></td>
	            </tr>
	            </hrms:priv>                                                      
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv> 
	<hrms:priv func_id="0B3">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="4">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>考 勤 表</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu4> 
	       <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	    <hrms:priv func_id="0B31"> 
	        <tr>
	         <td  align="center" class="loginFont" ><hrms:link href="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&viewPost=kq&code=${code}&kind=${kind}&report_id=1&self_flag=self&privtype=kq" target="il_body" onclick="turn();"><img src="/images/employee.gif" border=0></hrms:link></td>
	        </tr>
	        <tr>
	           <td  align="center" class="loginFont" >
	             <hrms:link href="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&viewPost=kq&report_id=1&code=${code}&kind=${kind}&self_flag=self&privtype=kq" target="il_body" onclick="turn();"><font id="a001" class="menu_a">考 勤 表</font></hrms:link>
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

                                                                              