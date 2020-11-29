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
   
   function validate(vermodule,cs_module)
   {
   		sysForm.target="il_body";
		sysForm.action="/templates/menu/busi_m_menu.do?b_query2=link&module="+vermodule+"&cs_module="+cs_module;
		sysForm.submit();    
   }
   
   /*
   function winexec(app_flag,ver_flag)
   {
    	  var cs_str=$F('cs_app');
    	  var com='C:/hrp2000/hrms.exe '+cs_str+' '+app_flag+' '+'0';
  
          try
          {
    		var wsh = new ActiveXObject('WScript.Shell');
    		if (wsh)
    		{
      			wsh.Run(com);
    		}
    	 }
    	 catch(ex)
    	 {
    	 	alert("您的计算机未下载“后台业务”模块，\n请进入“系统管理”，点击资源下载中的“后台业务下载”即可!\n如果机器未安装JDK运行环境，则在下载后台业务模块以前，须先下载JDK！");
    	 }
   }   */
 </SCRIPT>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT language="JavaScript1.2"  src="/ajax/common.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -235;
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">  
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="150" id="menucol">  
	<html:form action="/templates/menu/employ_m_menu">
	<input type="hidden" id="cs_app" value="${sysForm.cs_app_str}" name="cs">
	
	<hrms:priv func_id="0G1">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>单位考核</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="0G11">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=4" target="il_body" ><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=4" target="il_body" onclick=""><font id="a001" class="menu_a">考核打分</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	            <hrms:priv func_id="0G12">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_result/org_kh_plan.do?b_init=init&distinctionFlag=0&model=3&modelType=UN" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/kh_result/org_kh_plan.do?b_init=init&distinctionFlag=0&model=3&modelType=UN" target="il_body" ><font id="a001" class="menu_a" >考核结果</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>  	
	             <hrms:priv func_id="0G13">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/showkhresult/show_kh.do?b_query=link&modelType=UN&opertor=1" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/showkhresult/show_kh.do?b_query=link&&modelType=UN&opertor=1" target="il_body" ><font id="a001" class="menu_a" >分数查询</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>   	                          
	          </table>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>  
	 
	<hrms:priv func_id="0G2">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>部门考核</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="0G21">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=2" target="il_body" ><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=2" target="il_body" ><font id="a001" class="menu_a" >考核打分</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>       
	           <hrms:priv func_id="0G22">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_result/org_kh_plan.do?b_init=init&distinctionFlag=0&model=3&modelType=UM" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/kh_result/org_kh_plan.do?b_init=init&distinctionFlag=0&model=3&modelType=UM" target="il_body" ><font id="a001" class="menu_a">考核结果</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="0G23">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/showkhresult/show_kh.do?b_query=link&modelType=UM&opertor=1" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/showkhresult/show_kh.do?b_query=link&modelType=UM&opertor=1" target="il_body" ><font id="a001" class="menu_a">分数查询</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	          </table>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>
	 <hrms:priv func_id="0G3">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>岗位考核</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="0G31">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=3" target="il_body" ><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/selfservice/performance/singleGrade.do?b_query=link&fromModel=menu&model=3" target="il_body" ><font id="a001" class="menu_a">考核打分</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	            <hrms:priv func_id="0G32">        
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/kh_result/kh_plan_list.do?b_init=link&model=0&distinctionFlag=0&opt=1" target="il_body"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/kh_result/kh_plan_list.do?b_init=link&model=0&distinctionFlag=0&opt=1" target="il_body"><font id="a003" class="menu_a" >本人考核结果</font></a></td>
	            </tr> 
	             </hrms:priv>
	            <hrms:priv func_id="0G33">  
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/kh_result/kh_result_orgtree.do?b_init=init&distinctionFlag=0&model=1" target="il_body"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/kh_result/kh_result_orgtree.do?b_init=init&distinctionFlag=0&model=1" target="il_body"><font id="a003" class="menu_a" >员工考核结果</font></a></td>
	            </tr> 
	             </hrms:priv>
	            <hrms:priv func_id="0G34">       
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/showkhresult/show_kh.do?b_query=link&modelType=@K&opertor=1" target="il_body" ><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/showkhresult/show_kh.do?b_query=link&modelType=@K&opertor=1" target="il_body"><font id="a003" class="menu_a" >分数查询</font></a></td>
	            </tr> 
	             </hrms:priv>                  
	          </table>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>
	 </html:form>
 </td>

</tr>
</table> 
<script language="javascript">
	showFirst();
</script>  



                                                                                                                                                       