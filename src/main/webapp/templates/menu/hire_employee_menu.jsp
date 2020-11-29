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

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -180;
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
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table>
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 ><span>招聘自助</span></td>
	  </tr>
	  <tr> 
	    <td>
	   <div class=sec_menu style="width:159;height:800;filter:alpha(Opacity=100);display=block;"  id=menu1> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	        <hrms:priv func_id="0A03" module_id="4">
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_employQuery1=query" target="il_body" onclick="turn();"><img src="../../images/bjbb.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                 <hrms:link href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_employQuery1=query" target="il_body" onclick="turn();"><font id="a001" class="menu_a">需求报批</font></hrms:link>
	              </td>
	            </tr>
	          </hrms:priv> 
	           <hrms:priv func_id="0A07" module_id="4">
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_employQuery2=query" target="il_body" onclick="turn();"><img src="../../images/apply.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	             <td  align="center" class="loginFont" >
	             <hrms:link href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_employQuery2=query" target="il_body" onclick="turn();"><font id="a006" class="menu_a" >需求审核</font></hrms:link></td>
	            </tr>
	            </hrms:priv>
	         <hrms:priv func_id="0A02" module_id="4">                      
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_employQuery3=query" target="il_body" onclick="turn();"><img src="../../images/cx.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                 <hrms:link href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_employQuery3=query" target="il_body" onclick="turn();"><font id="a001" class="menu_a">审核查询</font></hrms:link>
	              </td>
	            </tr>
	          </hrms:priv> 
	            <hrms:priv func_id="0A08" module_id="4">	            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/demandPlan/hireOrder.do?br_orgtree=query" target="il_body" onclick="turn();"><img src="../../images/bx.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	             <td  align="center" class="loginFont" ><a href="/hire/demandPlan/hireOrder.do?br_orgtree=query" target="il_body" onclick="turn();"><font id="a006" class="menu_a" >招聘订单</font></a></td>
	            </tr>
	            </hrms:priv>	 
	          <hrms:priv func_id="0A05" module_id="4">         
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/employActualize/employResume.do?b_query=link&z0301=-1&personType=0&operate=init&employType=1" target="il_body"><img src="/images/employ_data.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/employActualize/employResume.do?b_query=link&z0301=-1&personType=0&operate=init&employType=1" target="il_body"><font id="a003" class="menu_a" >应聘简历</font></a></td>
	            </tr> 
	          </hrms:priv>
	          <hrms:priv func_id="0A04">                        	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/employActualize/employResume.do?b_query=link&z0301=-1&personType=1&operate=init&employType=1" target="il_body" function_id="xxx"><img src="/images/cx.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/employActualize/employResume.do?b_query=link&z0301=-1&personType=1&operate=init&employType=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">人才库</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	         <hrms:priv func_id="0A01" module_id="4">                      
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/interviewEvaluating/interviewArrange.do?br_employQuery=link" target="il_body" onclick="turn();"><img src="../../images/browser.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                 <hrms:link href="/hire/interviewEvaluating/interviewArrange.do?br_employQuery=link" target="il_body" onclick="turn();"><font id="a001" class="menu_a">面试安排</font></hrms:link>
	              </td>
	            </tr>
	          </hrms:priv>           
	           
	          <hrms:priv func_id="0A06" module_id="4">         
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_query=link" target="il_body"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/innerEmployNetPortal/initInnerEmployPos.do?b_query=link" target="il_body"><font id="a003" class="menu_a" >内部招聘</font></a></td>
	            </tr> 
	            </hrms:priv> 
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
</td>

</tr>
</table> 
<script language="javascript">
  parent.frames[1].name= "il_body"; 
</script>  



                                                                                                                                                       