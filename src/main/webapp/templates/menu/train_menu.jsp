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
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
           //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
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
  <td width="162" id="menucol"> 
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="1">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>培训业务</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:600;filter:alpha(Opacity=100);display:none;"   id=menu1> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="0904">              
	           <tr>
	              <td  align="center" class="loginFont" ><a href="/train/plan/trainPlanList0.do?b_query0=init&model=1" target="il_body" ><img src="/images/jh.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/plan/trainPlanList0.do?b_query0=init&model=1" target="il_body" ><font class="menu_a" >计划制定</font></a></td>
	            </tr> 
	          </hrms:priv> 
	           <hrms:priv func_id="0905">                     
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/plan/planTree.do?b_search=link" target="il_body" onclick="turn();"><img src="/images/jh.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/plan/planTree.do?b_search=link&stateFlag=" target="il_body" onclick="turn();"><font class="menu_a" >计划审核</font></a></td>
	            </tr> 
	          </hrms:priv>            
	           
	           
			<!-- 
	           <hrms:priv func_id="0901">           
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/educate/infopick/infopicksearch.do?b_query=link" target="il_body" ><img src="/images/request.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/educate/infopick/infopicksearch.do?b_query=link" target="il_body" ><font class="menu_a" >需求采集</font></a></td>
	            </tr> 
	          </hrms:priv>  
	           <hrms:priv func_id="0902">                                
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/educate/edulesson/searchedu.do?b_query=link" target="il_body" ><img src="/images/query_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/educate/edulesson/searchedu.do?b_query=link" target="il_body" ><font class="menu_a" >培训班</font></a></td>
	            </tr>
	          </hrms:priv> 
	           <hrms:priv func_id="0903">                         
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/educate/edulesson/reg.do?b_query=link" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/educate/edulesson/reg.do?b_query=link" target="il_body" ><font class="menu_a" >报名申请</font></a></td>
	            </tr> 
	          </hrms:priv>   
	           -->
	          
	          <hrms:priv func_id="0906">    
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/job/browseTrainClassList.do?b_query=link&operate=init" target="il_body" ><img src="/images/query_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/job/browseTrainClassList.do?b_query=link&operate=init" target="il_body" ><font class="menu_a" >浏览培训班</font></a></td>
	            </tr>
	           </hrms:priv>
	           <hrms:priv func_id="0907"> 
	             <tr>
	              <td  align="center" class="loginFont" ><a href="/train/job/browseTrainClassList.do?b_myClass=link" target="il_body" ><img src="/images/query_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/job/browseTrainClassList.do?b_myClass=link" target="il_body" ><font class="menu_a" >我的培训班</font></a></td>
	            </tr>
	           </hrms:priv>
	           <hrms:priv func_id="0908">
	          	<tr>
	              <td  align="center" class="loginFont" ><a href="/train/signUp/browseSignUpAuditingList.do?b_query=link&operate=init" target="il_body" ><img src="/images/query_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/signUp/browseSignUpAuditingList.do?b_query=link&operate=init" target="il_body" ><font class="menu_a" >报名审核</font></a></td>
	            </tr>
	           </hrms:priv>
	                    
	          </table>
	        
	     </div>
	   </td>
	  </tr>
	</table>
</td>

</tr>
</table> 

<script language="javascript">
	showFirst();

</script>  

                                                                              