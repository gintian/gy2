<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

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
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -160;
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
	<hrms:priv func_id="0D1">      
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="1">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>述职述廉</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:block;"   id=menu1> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="0D12"> 
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/performance/selfGrade.do?b_init=init&model=1" target="il_body" function_id="xxx"><img src="/images/browser.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/selfservice/performance/selfGrade.do?b_init=init&model=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">提交述职报告</font></hrms:link>
	              </td>
	            </tr>         
	          </hrms:priv>  
	            
	         <hrms:priv func_id="0D11"> 	    
	           <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/reportwork/searchReportWork.do?b_search=search&opt=1" target="il_body" function_id="xxx"><img src="/images/browser.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/reportwork/searchReportWork.do?b_search=search&opt=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">查阅述职报告</font></hrms:link>
	              </td>
	            </tr>   
	         </hrms:priv>                
				 
			     
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv>  
	<hrms:priv func_id="0D2">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="2">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>民主评议</span></td>
	  </tr>
	  <tr>
	    <td valign="top">
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu2>
		<table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="0D21">
	            <tr>
	              	<td  align="center" class="loginFont" >
		              <hrms:link href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=1&optObject=1" target="il_body" function_id="xxx"><img src="/images/flow_upload.gif" border=0 ></hrms:link>
	             	</td>
	            </tr>
	            <tr>
	            　<td  align="center" class="loginFont" >
			     <hrms:link href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=1&optObject=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">领导班子测评</font></hrms:link> 
	            　</td>
	            </tr>
	       </hrms:priv>
	       <hrms:priv func_id="0D22">      
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=1&optObject=2" target="il_body" function_id="xxx"><img src="/images/flow_upload.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	            　<td  align="center" class="loginFont" >
	          
			     <hrms:link href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=1&optObject=2" target="il_body" function_id="xxx"><font id="a001" class="menu_a">班子成员测评</font></hrms:link> 
	            　</td>
	            </tr>
	        </hrms:priv> 
	         
	          <hrms:priv func_id="0D23">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/performance/selfGrade.do?b_query=link&bint=int&model=1" target="il_body" function_id="xxx"><img src="/images/table_upload.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	             <td  align="center" class="loginFont" >
	             <hrms:link href="/selfservice/performance/selfGrade.do?b_query=link&bint=int&model=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">自我评价</font></hrms:link>
	              </td>
	            </tr>
	          </hrms:priv> 
	          <hrms:priv func_id="0D24">              
	           <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/markStatus/markStatusList.do?b_search=link&model=1" target="il_body" function_id="xxx" ><img src="/images/per_result.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/markStatus/markStatusList.do?b_search=link&model=1" target="il_body" function_id="xxx" ><font id="a001" class="menu_a">打分状态</font></hrms:link>
	              </td>
	            </tr> 
	          </hrms:priv> 
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv>
	<hrms:priv func_id="0D4"> 
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="3">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>后备推荐</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu3> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	 	       <hrms:priv func_id="0D41">  	
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/commend/insupportcommend/initInSupportCommend.do?b_init=init" target="il_body" function_id="xxx"><img src="/images/edit_info.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/commend/insupportcommend/initInSupportCommend.do?b_init=init" target="il_body" function_id="xxx"><font id="a001" class="menu_a">候选人提名</font></hrms:link>
	              </td>
	            </tr>  
	           </hrms:priv>
	           <hrms:priv func_id="0D42">             
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/commend/choosecandidate/initChooseCandidateList.do?b_init=init" target="il_body" function_id="xxx"><img src="/images/organization.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/commend/choosecandidate/initChooseCandidateList.do?b_init=init" target="il_body" function_id="xxx"><font id="a001" class="menu_a">民主推荐</font></hrms:link>
	              </td>
	            </tr>
			   </hrms:priv> 
			   <hrms:priv func_id="0D4">             
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/options/checkBodyObjectList.do?b_query=link&bodyType=0&noself=1" target="il_body" function_id="xxx"><img src="/images/hmuster.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/options/checkBodyObjectList.do?b_query=link&bodyType=0&noself=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">投票人类别</font></hrms:link>
	              </td>
	            </tr>
			   </hrms:priv>
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv>
	
	
	<hrms:priv func_id="0D3"> 
	<table cellpadding=0 cellspacing=0 width=159  height="500" class="menu_table" index="4">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>结果分析</span></td>
	  </tr>
	  <tr>
	    <td valign="top">
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu4> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
	     	   <hrms:priv func_id="0D31">            
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_result/kh_plan_list.do?b_init=link&opt=1&model=0&distinctionFlag=1" target="il_body" function_id="xxx"><img src="/images/per_result.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/kh_result/kh_plan_list.do?b_init=link&opt=1&model=0&distinctionFlag=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">本人测评结果</font></hrms:link>
	              </td>
	            </tr>
	           </hrms:priv> 
	     	   <hrms:priv func_id="0D32">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_result/kh_result_orgtree.do?b_init=init&distinctionFlag=1&model=1" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/per_result.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/kh_result/kh_result_orgtree.do?b_init=init&distinctionFlag=1&model=1" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">成员测评结果</font></hrms:link>
	              </td>
	            </tr> 
	           </hrms:priv> 
	     	   <hrms:priv func_id="0D33">            
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/commend/insupportcommend/everyYearAnalyseVote.do?b_init=init&type=1" target="il_body" function_id="xxx"><img src="/images/edit_info.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/commend/insupportcommend/everyYearAnalyseVote.do?b_init=init&type=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">后备推荐结果分析</font></hrms:link>
	              </td>
	            </tr>             
	           </hrms:priv>  
	           <hrms:priv func_id="0D34">                 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/commend/insupportcommend/voteStatusAnalyse.do?b_init=init&opt=1" target="il_body" function_id="xxx"><img src="/images/edit_info.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/commend/insupportcommend/voteStatusAnalyse.do?b_init=init&opt=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a">投票状况分析</font></hrms:link>
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

                                                                              