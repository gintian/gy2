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
   if(window.screenTop==128)//fullscreen status
	   var  divHeight = window.screen.availHeight - window.screenTop -(6*28+21);  
   else
	   var  divHeight = window.screen.availHeight - window.screenTop -(6*28+21+20); //add other height

   this.status="";
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

	<hrms:priv func_id="3101">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>招聘需求</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display=block;"  id=menu1> 
	     <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="31011">
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_query1=query" target="il_body" onclick="turn();"><img src="../../images/bjbb.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	             <td  align="center" class="loginFont" ><a href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_query1=query" target="il_body" onclick="turn();"><font id="a006" class="menu_a" >需求报批</font></a></td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="31012">
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_query2=query" target="il_body" onclick="turn();"><img src="../../images/apply.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	             <td  align="center" class="loginFont" ><a href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_query2=query" target="il_body" onclick="turn();"><font id="a006" class="menu_a" >需求审核</font></a></td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="31013">            
	             <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/demandPlan/engagePlan.do?b_query=query" target="il_body" ><img src="../../images/bx.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	             <td  align="center" class="loginFont" ><a href="/hire/demandPlan/engagePlan.do?b_query=query" target="il_body" ><font id="a006" class="menu_a" >招聘计划</font></a></td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="31014">
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_query3=query" target="il_body" onclick="turn();"><img src="../../images/browser.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	             <td  align="center" class="loginFont" ><a href="/hire/demandPlan/positionDemand/positionDemandTree.do?br_query3=query" target="il_body" onclick="turn();"><font id="a006" class="menu_a" >审核查询</font></a></td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="31015">	            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/demandPlan/hireOrder.do?br_orgtree=query" target="il_body" onclick="turn();"><img src="../../images/bx.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	             <td  align="center" class="loginFont" ><a href="/hire/demandPlan/hireOrder.do?br_orgtree=query" target="il_body" onclick="turn();"><font id="a006" class="menu_a" >招聘订单</font></a></td>
	            </tr>
	            </hrms:priv>	            
	          </table>
		</form>
	      </div>
	    </td>
	  </tr>
	</table>
	</hrms:priv>
	
	
	<hrms:priv func_id="3102">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>招聘实施</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="31022">          	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/employActualize/employPosition.do?b_query=link&operate=init" target="il_body" function_id="xxx"><img src="/images/aaa.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/employActualize/employPosition.do?b_query=link&operate=init" target="il_body" function_id="xxx"><font id="a001" class="menu_a">招聘职位</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>  
	            <hrms:priv func_id="31023">                     	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/employActualize/employResume.do?b_query=link&z0301=-1&personType=0&operate=init" target="il_body" function_id="xxx"><img src="/images/bx.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/employActualize/employResume.do?b_query=link&z0301=-1&personType=0&operate=init" target="il_body" function_id="xxx"><font id="a001" class="menu_a">应聘简历</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>  
	          
	            <tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/employActualize/employResume.do?b_query=link&z0301=-1&personType=4&operate=init" target="il_body" function_id="xxx"><img src="/images/hmc.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/employActualize/employResume.do?b_query=link&z0301=-1&personType=4&operate=init" target="il_body" function_id="xxx"><font id="a001" class="menu_a">我的收藏夹</font></hrms:link>
	              </td>
	            </tr>
	            
	            
	            <hrms:priv func_id="31024">                        	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/employActualize/employResume.do?b_query=link&z0301=-1&personType=1&operate=init" target="il_body" function_id="xxx"><img src="/images/cx.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/employActualize/employResume.do?b_query=link&z0301=-1&personType=1&operate=init" target="il_body" function_id="xxx"><font id="a001" class="menu_a">人才库</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>      
	            
	            
	                  <%if(SystemConfig.getPropertyValue("OfficialExamField")!=null&&!SystemConfig.getPropertyValue("OfficialExamField").equals("")){ %>                  	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/employActualize/appointpassmark.do?b_init=link&opt=0" target="il_body" function_id="xxx"><img src="/images/px.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/employActualize/appointpassmark.do?b_init=link&opt=0" target="il_body" function_id="xxx"><font id="a001" class="menu_a">分数线指定</font></hrms:link>
	              </td>
	            </tr>
	            <%} %>    
	          	<!-- 
	          	<hrms:priv func_id="31021">
	            <tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/employActualize/personnelFilter/personnelFilterTree.do?br_query=link" target="i_body" function_id="xxx"><img src="/images/aaa.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/employActualize/personnelFilter/personnelFilterTree.do?br_query=link" target="i_body" function_id="xxx"><font id="a001" class="menu_a">人员甑选</font></hrms:link>
	              </td>
	            </tr>
				</hrms:priv>
				 -->
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	
	
	<hrms:priv func_id="3103">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>面试测评</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	          	<hrms:priv func_id="31031">
	            <tr>
	              <td  align="center" class="loginFont" >
	                 <hrms:link href="/hire/interviewEvaluating/interviewArrange.do?br_query=link" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/browser.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/interviewEvaluating/interviewArrange.do?br_query=link" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">面试安排</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="31032">
	             <tr>
	              <td  align="center" class="loginFont" >
	                 <hrms:link href="/hire/interviewEvaluating/interviewAnnounce.do?br_query=link" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/browser.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/interviewEvaluating/interviewAnnounce.do?br_query=link" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">面试通知</font></hrms:link>
	              </td>
	            </tr>    
	            </hrms:priv>
	            <hrms:priv func_id="31033">
	             <tr>
	              <td  align="center" class="loginFont" >
	                 <hrms:link href="/hire/interviewEvaluating/interviewExamine.do?br_query=link" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/browser.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/interviewEvaluating/interviewExamine.do?br_query=link" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">面试考核</font></hrms:link>
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
	<hrms:priv func_id="3104">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="4">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>录用总结</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="31041">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/hire/employSummarise/personnelEmploy.do?br_query=link" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/djx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/employSummarise/personnelEmploy.do?br_query=link&operate=init" target="il_body" onclick="turn();"><font id="a001" class="menu_a">员工录用</font></hrms:link>
	              </td>
	            </tr>
	          	</hrms:priv>
	          	<hrms:priv func_id="31042">
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/employSummarise/hireSummarise.do?b_query=link&operate=init" target="il_body"><img src="/images/edit_info.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/employSummarise/hireSummarise.do?b_query=link&operate=init" target="il_body"><font id="a003" class="menu_a" >招聘总结</font></a></td>
	            </tr>  
	            </hrms:priv>         
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	
	<hrms:priv func_id="3106">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>统计分析</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="31061">          	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/zp_options/stat/showstatestat.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/aaa.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/zp_options/stat/showstatestat.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">按职位统计</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>  
	            <hrms:priv func_id="31062">                     	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/zp_options/stat/statestat/showstateresult.do?b_query=link&init=1" target="il_body" function_id="xxx"><img src="/images/bx.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/zp_options/stat/statestat/showstateresult.do?b_query=link&init=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">按简历状态统计</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>  
	            
	            <hrms:priv func_id="31063">                        	
	          	<tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/hire/zp_options/stat/itemstat/showstatresult.do?b_query=link&init=1&pos=menu" target="il_body" function_id="xxx"><img src="/images/cx.gif" border=0></hrms:link>
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/hire/zp_options/stat/itemstat/showstatresult.do?b_query=link&init=1&pos=menu" target="il_body" function_id="xxx"><font id="a001" class="menu_a">按类别统计</font></hrms:link>
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
	
	<hrms:priv func_id="3105">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="6">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle7 onclick="menuChange(menu7,divHeight,menuTitle7,arrow7);"><span><span id=arrow7><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu7> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="31051">
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/zp_options/basesetfield.do?br_query=link" target="il_body" function_id="xxx"><img src="/images/cardset.gif" border=0></a></td>
	            </tr>
	            <tr>
	               <td  align="center" class="loginFont" ><a href="/hire/zp_options/basesetfield.do?br_query=link" target="il_body" ><font id="a005" class="menu_a" >应聘人才库</font></a></td>
	            </tr>
	           </hrms:priv>
				<hrms:priv func_id="31053">
			    <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/parameterSet/configureParameter.do?b_init=link" target="il_body" ><img src="/images/jgbm.gif" border=0 ></a></td>
	            </tr>
	             <tr>
	                <td  align="center" class="loginFont" ><a href="/hire/parameterSet/configureParameter.do?b_init=link" target="il_body" ><font id="a006" class="menu_a" >配置参数</font></a></td>
	            </tr>
	            </hrms:priv>
		    	<hrms:priv func_id="31054">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/sys/options/template/searchTemplate.do?b_query=link&templateType=32&opt=1" target="il_body" ><img src="/images/mbsz.gif" border=0 ></a></td>
	            </tr>
	             <tr>
	                <td  align="center" class="loginFont" ><a href="/sys/options/template/searchTemplate.do?b_query=link&templateType=32&opt=1" target="il_body" ><font id="a006" class="menu_a" >通知模板</font></a></td>
	            </tr>
	            </hrms:priv>            
		    	<hrms:priv func_id="31055">                
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/sys/cms/channelTree.do?b_search=link" target="il_body" ><img src="/images/mb.gif" border=0 ></a></td>
	            </tr>
	             <tr>
	                <td  align="center" class="loginFont" ><a href="/sys/cms/channelTree.do?b_search=link" target="il_body" ><font id="a006" class="menu_a" >外网内容管理</font></a></td>
	            </tr>
	            </hrms:priv>  
		    	<hrms:priv func_id="31056">                         
	             <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/zp_options/cond/zpCondTemplate.do?b_query=query" target="il_body" ><img src="/images/browser.gif" border=0 ></a></td>
	            </tr>
	             <tr>
	                <td  align="center" class="loginFont" ><a href="/hire/zp_options/cond/zpCondTemplate.do?b_query=query" target="il_body" ><font id="a006" class="menu_a" >简历筛选模板</font></a></td>
	            </tr>
	            </hrms:priv>   
	            <hrms:priv func_id="31057">              
	             <tr>
	              <td  align="center" class="loginFont" ><a href="/hire/parameterSet/configureParameter/init_table_data.do?b_init=query" target="il_body" ><img src="/images/browser.gif" border=0 ></a></td>
	            </tr>
	             <tr>
	                <td  align="center" class="loginFont" ><a href="/hire/parameterSet/configureParameter/init_table_data.do?b_init=query" target="il_body" ><font id="a006" class="menu_a" >数据初始化</font></a></td>
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
	
	<%  
	
	if(userView!=null&&userView.getHm().get("flik")!=null)
	{
		
		String flik =(String)userView.getHm().get("flik");	
			
		if(flik!=null&&!flik.equals("1")) 
		{
		%>
			var obj=eval("menuTitle<%=flik%>");	
			obj.fireEvent('onclick');
		
		<%
		}
		userView.getHm().remove("flik");
	}
	%>
	
	
</script>  



                                                                                                                                                       