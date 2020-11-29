<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.performance.singleGrade.DirectUpperPosBo"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
           //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}  
	
	
	DirectUpperPosBo bo=new DirectUpperPosBo();
	String flag=bo.getGradeFashion("0");
	userView.getHm().put("gradeFashion",flag);
	String batchGradeUrl="";
	if(flag.equals("1"))
		batchGradeUrl="/selfservice/performance/batchGrade.do?b_query=link&linkType=1&model=0";
	else
		batchGradeUrl="/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&linkType=1&planContext=all";
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<script type="text/javascript" src="../../ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="../../ext/ext-all.js"></script>
<script type="text/javascript" src="../../ext/rpc_command.js"></script> 	
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -140;
   function turn()
   {
   	 <% 
	  if(isturn==null||!isturn.equals("false"))
	     out.println("parent.menupnl.toggleCollapse(false);");
	%>
   }    
   function authorize(response)
   {
		var value=response.responseText;
		var map=Ext.util.JSON.decode(value);
		if(map.succeed==false)
		{
			alert(map.message);
		}
   }

   function handle()
   {
        var map = new HashMap();
        map.put("module",29);
        map.put("auth_lock","true");
        Rpc({functionId:'1010010206',success:authorize},map); 
   }   
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">   
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol">  
	<hrms:priv func_id="0605">      
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="1">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>考评实施</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:block;"   id=menu1> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	          <hrms:priv func_id="060501">         
				<tr>
				  <td  align="center" class="loginFont" ><a href="/selfservice/performance/performanceImplement0.do" target="il_body" ><img src="/images/admin_pwd.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/performance/performanceImplement0.do" target="il_body" ><font id="a004" class="menu_a">考核表分发</font></a></td>
	            </tr>
	          </hrms:priv>  
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv>  
	
	
	<hrms:priv func_id="0608" module_id="29">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="5">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle5 onclick="handle();menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>任务业绩</span></td>
	  </tr>
	  
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu5> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">    
				<hrms:priv func_id="060801"> 
				<tr>
				  <td  align="center" class="loginFont" ><a href="/performance/achivement/achivementTask.do?br_init=int" target="il_body" ><img src="/images/hmuster.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/achivement/achivementTask.do?br_init=int" target="il_body" ><font id="a004" class="menu_a">业绩任务书</font></a></td>
	            </tr>
	            </hrms:priv>
	  			<hrms:priv func_id="060802"> 
		  			<tr>
					  <td  align="center" class="loginFont" ><a href="/performance/achivement/dataCollection/khplanMenu.do?b_query=link" target="il_body" ><img src="/images/jh.gif" border=0 ></a></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" ><a href="/performance/achivement/dataCollection/khplanMenu.do?b_query=link" target="il_body" ><font id="a004" class="menu_a">业绩数据录入</font></a></td>
		            </tr>
		        </hrms:priv>
	          </table>
	     </div>
	   </td>
	  </tr>
	 
	</table>
	</hrms:priv>
	
	
	
	<hrms:priv func_id="0610"  module_id="29" >
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="7">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle7 onclick="handle();menuChange(menu7,divHeight,menuTitle7,arrow7);"><span><span id=arrow7><img src="/images/darrow.gif" border=0></span>日志管理</span></td>
	  </tr>
	  
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu7> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">    
				<hrms:priv func_id="061001"> 
				<tr>
				  <td  align="center" class="loginFont" ><a href="/performance/workdiary/index.jsp" target="il_body" ><img src="/images/jh.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/workdiary/index.jsp" target="il_body" ><font id="a004" class="menu_a">我的日志</font></a></td>
	            </tr>
	            </hrms:priv>
	  		
	  			<hrms:priv func_id="061002"> 
		  			<tr>
					  <td  align="center" class="loginFont" ><a href="/performance/workdiary/workdiary.do" target="il_body" ><img src="/images/browser.gif" border=0 ></a></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" ><a href="/performance/workdiary/workdiary.do" target="il_body" ><font id="a004" class="menu_a">员工日志</font></a></td>
		            </tr>
		        </hrms:priv>
	         
	         
	           <hrms:priv func_id="061003"> 
		  			<tr>
					  <td  align="center" class="loginFont" ><a href="/general/impev/importantev.do?b_query=link" target="il_body" ><img src="/images/jh.gif" border=0 ></a></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" ><a href="/general/impev/importantev.do?b_query=link" target="il_body" ><font id="a004" class="menu_a">我的重要信息报告</font></a></td>
		            </tr>
		        </hrms:priv>
		        
		        <hrms:priv func_id="061004"> 
		  			<tr>
					  <td  align="center" class="loginFont" ><a href="/general/impev/importantev.do?b_tree=link" target="il_body" ><img src="/images/browser.gif" border=0 ></a></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" ><a href="/general/impev/importantev.do?b_tree=link" target="il_body" ><font id="a004" class="menu_a">员工重要信息报告</font></a></td>
		            </tr>
		        </hrms:priv>
	         
	         
	          </table>

	     </div>
	   </td>
	  </tr>
	 
	</table>
	</hrms:priv>
	
	
	
	
	
	
	
	<hrms:priv func_id="0607" module_id="29">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="4">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle4 onclick="handle();menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>目标考核</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu4> 
	          <table cellpadding=2 cellspacing=3 align=center width=100%  class="DetailTable" style="position:relative;top:10px;">    
				<hrms:priv func_id="060701">   
				<tr>
				  <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&opt=1&returnflag=menu" target="il_body" ><img src="/images/cardset.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&opt=1&returnflag=menu" target="il_body" ><font id="a004" class="menu_a">团队绩效</font></a></td>
	            </tr>
	  			</hrms:priv>
	  			<hrms:priv func_id="060702">   
	            <tr>
				  <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/myObjective/my_objective_list.do?b_init=init&opt=1&returnflag=menu" target="il_body" ><img src="/images/cardset.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/myObjective/my_objective_list.do?b_init=init&opt=1&returnflag=menu" target="il_body" ><font id="a004" class="menu_a">我的目标</font></a></td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="060703">   
	  
	  			<tr>
				  <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/setUnderlingObjective/underling_objective_tree.do?b_query=link&entranceType=0&returnflag=menu" target="il_body" ><img src="/images/cardset.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/setUnderlingObjective/underling_objective_tree.do?b_query=link&entranceType=0&returnflag=menu&convertPageEntry=1" target="il_body" ><font id="a004" class="menu_a">员工目标</font></a></td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="060704">   
	            <tr>
				  <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&opt=1&entranceType=0&isSort=0&returnflag=menu" target="il_body" ><img src="/images/edit_info.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&opt=1&entranceType=0&isSort=0&returnflag=menu" target="il_body" ><font id="a004" class="menu_a">目标评分</font></a></td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="060705">   
	            <tr>
				  <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/manageKeyMatter/orgTree.do?b_query=link&action=keyMatterList.do&treetype=duty&kind=0&target=mil_body" target="il_body" ><img src="/images/mc.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/manageKeyMatter/orgTree.do?b_query=link&action=keyMatterList.do&treetype=duty&kind=0&target=mil_body" target="il_body" ><font id="a004" class="menu_a">关键事件</font></a></td>	             
	            </tr>
	  			</hrms:priv>
	  			
	  			<hrms:priv func_id="060708">   
	            <tr>
				  <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/objectiveDecision/dec_performance_list.do?b_query=link&opt=1&action=keyMatterList.do&treetype=duty&kind=0&target=mil_body" target="il_body" ><img src="/images/mc.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/objectiveDecision/dec_performance_list.do?b_query=link&opt=1&action=keyMatterList.do&treetype=duty&kind=0&target=mil_body" target="il_body" ><font id="a004" class="menu_a">目标卡制订</font></a></td>	             
	            </tr>
	  			</hrms:priv>
	  			 <hrms:priv func_id="060707"> 
	  				<tr>
				  <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/setUnderlingObjective/underling_objective_tree.do?b_view=link&entranceType=0&returnflag=menu" target="il_body" ><img src="/images/mc.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/setUnderlingObjective/underling_objective_tree.do?b_view=link&entranceType=0&returnflag=menu" target="il_body" ><font id="a004" class="menu_a">目标执行情况</font></a></td>	             
	            </tr>
	  			</hrms:priv>
	  			<hrms:priv func_id="060706"> 
	  				<tr>
				  <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_tree=tree&change=0&convertPageEntry=1" target="il_body" ><img src="/images/mc.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_tree=tree&change=0&convertPageEntry=1" target="il_body" ><font id="a004" class="menu_a">目标卡状态</font></a></td>	             
	            </tr>
	  			</hrms:priv>
	  			
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv>  
	
	
	
	
	<hrms:priv func_id="0606"> 
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="2">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>考评打分</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu2> 
	          <table cellpadding=2 cellspacing=3 align=center width=100%  class="DetailTable" style="position:relative;top:10px;">	 
	            <hrms:priv func_id="060602">   
	            <tr>
				  <td  align="center" class="loginFont" ><a href="/selfservice/performance/selfGrade.do?b_query=link&bint=int&model=0" target="il_body" ><img src="/images/flow_upload.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/performance/selfGrade.do?b_query=link&bint=int&model=0" target="il_body" ><font id="a004" class="menu_a">自我评价</font></a></td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="060603">   
	            <tr>
				  <td  align="center" class="loginFont" ><a href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=0" target="il_body" ><img src="/images/flow_upload.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=0" target="il_body" ><font id="a004" class="menu_a">单人考评</font></a></td>	             
	            </tr>
	  			</hrms:priv>
	  			
	  			 <hrms:priv func_id="060601"> 
	  				<tr>
				  <td  align="center" class="loginFont" ><a href="<%=batchGradeUrl%>" target="il_body" ><img src="/images/flow_upload.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="<%=batchGradeUrl%>" target="il_body" ><font id="a004" class="menu_a">多人考评</font></a></td>	             
	            </tr>
	  			</hrms:priv>
	  			
	  			 <hrms:priv func_id="060604"> 
	  				<tr>
				  <td  align="center" class="loginFont" ><a href="/performance/markStatus/markStatusList.do?b_search=link&model=0" target="il_body" ><img src="/images/flow_upload.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/markStatus/markStatusList.do?b_search=link&busitype=-1&model=0" target="il_body" ><font id="a004" class="menu_a">打分状态</font></a></td>	             
	            </tr>
	  			</hrms:priv>
	         
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="0609" >      
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="6">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);"><span><span id=arrow6><img src="/images/darrow.gif" border=0></span>考核沟通</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu6> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	          <hrms:priv func_id="060901">         
				<tr>
				  <td  align="center" class="loginFont" ><a href="/performance/interview/search_interview_list.do?b_init=init&opt=1&plan_id=-1&type=0" target="il_body" ><img src="/images/addr_note.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/interview/search_interview_list.do?b_init=init&opt=1&plan_id=-1&type=0" target="il_body" ><font id="a004" class="menu_a">面谈记录</font></a></td>
	            </tr>
	          </hrms:priv> 
	          
	          
	           <hrms:priv func_id="060902">         
				<tr>
				  <td  align="center" class="loginFont" ><a href="/performance/interview/search_interview_list.do?br_ywsq=query" target="il_body" ><img src="/images/jh.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/interview/search_interview_list.do?br_ywsq=query" target="il_body" ><font id="a004" class="menu_a">考核申诉</font></a></td>
	            </tr>
	          </hrms:priv> 
	           
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv>  
	<hrms:priv func_id="0603"> 
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="3">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>考评反馈</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu3> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	     	   <hrms:priv func_id="060301">            
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_result/kh_plan_list.do?b_init=link&model=0&distinctionFlag=0&opt=1" target="il_body" function_id="xxx"><img src="/images/per_result.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/kh_result/kh_plan_list.do?b_init=link&model=0&distinctionFlag=0&opt=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">本人考核结果</font></hrms:link>
	              </td>
	            </tr>     	        
	           </hrms:priv> 
	     	   <hrms:priv func_id="060401">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_result/kh_result_orgtree.do?b_init=init&distinctionFlag=0&model=1" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/per_result.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/kh_result/kh_result_orgtree.do?b_init=init&distinctionFlag=0&model=1" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">员工考核结果</font></hrms:link>
	              </td>
	            </tr> 
	           </hrms:priv>  
	            <hrms:priv func_id="060303">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_result/org_kh_plan.do?b_init=init&distinctionFlag=0&model=3&modelType=UU" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/per_result.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/kh_result/org_kh_plan.do?b_init=init&distinctionFlag=0&model=3&modelType=UU" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">团队考核结果</font></hrms:link>
	              </td>
	            </tr> 
	         </hrms:priv>
	          <hrms:priv func_id="060302">  
	           <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/showkhresult/show_kh.do?b_query=link&modelType=ALL&opertor=1" target="il_body" function_id="xxx" ><img src="/images/per_result.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/showkhresult/show_kh.do?b_query=link&modelType=ALL&opertor=1" target="il_body" function_id="xxx" ><font id="a001" class="menu_a">考评分数查询</font></hrms:link>
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
	function fullwin()
	{
		window.open("/selfservice/performance/batchGrade.do?b_query=link","","fullscreen=yes")
		window.open('/templates/welcome/welcome.html','il_body');	
		
	//	window.open("/selfservice/performance/batchGrade.do?b_query=link","","fullscreen=yes,scrollbars=yes,status=no")
	//	window.open("/selfservice/performance/batchGrade.do?b_query=link",'','fullscreen,scrollbars');
	}
	
</script>  

                                                                              