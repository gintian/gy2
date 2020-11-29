<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>

<script language="javascript" src="/js/constant.js"></script>
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
   var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=1;
   function turn()
   {
	<% 
	  if(isturn==null||!isturn.equals("false"))
	     out.println("parent.menupnl.toggleCollapse(false);");
	%>
   }   
   
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
   }  
   function initDate(){
   		if(confirm("确定删除所有培训计划和培训班吗?")){
 			var hashvo=new ParameterSet();
   			hashvo.setValue("checkinfor","1");
			var request=new Request({method:'post',asynchronous:false,functionId:'2020020224'},hashvo);
			alert("数据初始化成功!");
		}
 	}
 	 function moveColor(obj){
 	 	obj.style.color="#E39E19";
 	 }
 	  function levelColor(obj){
 	 	obj.style.color="#1B4A98";
 	 }
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
  <td width="162" id="menucol">  
	<html:form action="/templates/menu/train_m_menu">
	<input type="hidden" id="cs_app" value="${sysForm.cs_app_str}" name="cs">
	
	<hrms:priv func_id="3230">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>培训体系</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="32300">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/resource/trainPro.do?br_query=link&returnvalue=" target="il_body" onclick="turn()"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/resource/trainPro.do?br_query=link&returnvalue=" target="il_body" onclick="turn()"><font id="a001" class="menu_a">培训类别</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="32301">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/resource/trainRescList.do?b_query=link&type=1&returnvalue=" target="il_body"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/resource/trainRescList.do?b_query=link&type=1&returnvalue=" target="il_body"><font id="a001" class="menu_a">培训机构</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	            <hrms:priv func_id="32302">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/resource/trainRescList.do?b_query=link&type=2&returnvalue=" target="il_body" ><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/resource/trainRescList.do?b_query=link&type=2&returnvalue=" target="il_body"><font id="a001" class="menu_a">培训教师</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	            <hrms:priv func_id="32303">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/resource/trainRescList.do?b_query=link&type=3&returnvalue=" target="il_body" ><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/resource/trainRescList.do?b_query=link&type=3&returnvalue=" target="il_body"><font id="a001" class="menu_a">培训场所</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	            <hrms:priv func_id="32304">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/resource/trainRescList.do?b_query=link&type=4&returnvalue=" target="il_body"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/resource/trainRescList.do?b_query=link&type=4&returnvalue=" target="il_body"><font id="a001" class="menu_a">培训设施</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	            <hrms:priv func_id="32305">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/resource/trainRescList.do?b_tree=link&returnvalue=" target="il_body" onclick="turn();"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/resource/trainRescList.do?b_tree=link&returnvalue=" target="il_body" onclick="turn();"><font id="a001" class="menu_a">培训资料</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>  
	            <hrms:priv func_id="32306">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/resource/course.do?b_tree=link&returnvalue=" target="il_body"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/resource/course.do?b_tree=link&returnvalue=" target="il_body"><font id="a001" class="menu_a">培训课程</font></hrms:link>
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
	<hrms:priv func_id="3231">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>培训需求</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="32310">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/traincourse/org_tree.do?b_query=link&amp;model=1&returnvalue=" target="il_body" onclick="turn()"><img src="/images/card.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/traincourse/org_tree.do?b_query=link&amp;model=1&returnvalue=" target="il_body" ><font id="a001" class="menu_a" onclick="turn()">需求采集</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>       
	            <hrms:priv func_id="32311">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/traincourse/org_tree.do?b_query=link&amp;model=2&returnvalue=" target="il_body"  onclick="turn();"><img src="/images/investigate.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/traincourse/org_tree.do?b_query=link&amp;model=2&returnvalue=" target="il_body"  onclick="turn();"><font id="a001" class="menu_a">需求审批</font></hrms:link>
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
	 <hrms:priv func_id="3232">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>培训计划</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="32320">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/b_plan/planTrain.do?b_org=link&amp;model=1&returnvalue=" target="il_body"  onclick="turn();"><img src="/images/card.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/b_plan/planTrain.do?b_org=link&amp;model=1&returnvalue=" target="il_body"  onclick="turn();"><font id="a001" class="menu_a">计划制订</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	            <hrms:priv func_id="32321">        
	            <tr>
	              <td  align="center" class="loginFont"><a href="/train/b_plan/planTrain.do?b_org=link&amp;model=2&returnvalue=" target="il_body"  onclick="turn();"><img src="/images/investigate.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont"><a href="/train/b_plan/planTrain.do?b_org=link&amp;model=2&returnvalue=" target="il_body"  onclick="turn();"><font id="a003" class="menu_a">计划审批</font></a></td>
	            </tr> 
	             </hrms:priv>               
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>  
	 <hrms:priv func_id="3233,32330,32331,3235">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>培 训 班</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="32330">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/request/trainsData.do?b_org=link&model=1&returnvalue=" target="il_body"  onclick="turn();"><img src="/images/hmuster.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/request/trainsData.do?b_org=link&model=1&returnvalue=" target="il_body"  onclick="turn();"><font id="a001" class="menu_a">培训班</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	            <hrms:priv func_id="32331">       
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org_tree.do?b_query=link&inforflag=2&returnvalue=" target="il_body" onclick="turn();"><img src="/images/hmuster.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org_tree.do?b_query=link&inforflag=2&returnvalue=" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >外部培训</font></a></td>
	            </tr> 
	             </hrms:priv>
	            <hrms:priv func_id="3235">  
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/trainCosts/trainCosts.do?b_org=link&model=1&returnvalue=" target="il_body" onclick="turn();"><img src="/images/ll.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/trainCosts/trainCosts.do?b_org=link&model=1&returnvalue=" target="il_body" onclick="turn();"><font id="a003" class="menu_a">培训费用</font></a></td>
	            </tr> 
	             </hrms:priv>                                   
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>  
	<hrms:priv func_id="32332">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="8">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle8 onclick="menuChange(menu8,divHeight,menuTitle8,arrow8);"><span><span id=arrow8><img src="/images/darrow.gif" border=0></span>培训考勤</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu8> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="3233201">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/attendance/orgAtteTree.do?br_query=link&type=1" target="il_body"  onclick="turn();"><img src="/images/tool.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/attendance/orgAtteTree.do?br_query=link&type=1" target="il_body"  onclick="turn();"><font id="a001" class="menu_a">培训排课</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	            <hrms:priv func_id="3233202">       
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/attendance/orgAtteTree.do?br_query=link&type=2" target="il_body" onclick="turn();"><img src="/images/zb.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/attendance/orgAtteTree.do?br_query=link&type=2" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >培训签到</font></a></td>
	            </tr> 
	             </hrms:priv>
	            <hrms:priv func_id="3233203">  
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/attendance/orgAtteTree.do?b_sign=link&type=3" target="il_body" onclick="turn();"><img src="/images/mc.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/train/attendance/orgAtteTree.do?b_sign=link&type=3" target="il_body" onclick="turn();"><font id="a003" class="menu_a">出勤汇总</font></a></td>
	            </tr> 
	             </hrms:priv>                                   
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>  
	 <hrms:priv func_id="3236">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="6">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);"><span><span id=arrow6><img src="/images/darrow.gif" border=0></span>培训分析</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu6> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="32360">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/report/orgTree.do?b_query=link&amp;type=1&returnvalue=" target="il_body"  onclick="turn();"><img src="/images/gzfh.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/report/orgTree.do?b_query=link&amp;type=1&returnvalue=" target="il_body" onclick="turn();"><font id="a001" class="menu_a" >单位部门报表</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	             <hrms:priv func_id="32361">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/report/orgTree.do?b_query=link&amp;type=2&returnvalue=" target="il_body"  onclick="turn();"><img src="/images/gzfh.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/report/orgTree.do?b_query=link&amp;type=2&returnvalue=" target="il_body" onclick="turn();"><font id="a001" class="menu_a" >培训类别报表</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	             <hrms:priv func_id="32362">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/report/orgTree.do?b_query=link&amp;type=3&returnvalue=" target="il_body"  onclick="turn();"><img src="/images/gzfh.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/report/orgTree.do?b_query=link&amp;type=3&returnvalue=" target="il_body" onclick="turn();"><font id="a001" class="menu_a">学员培训报表</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	             <hrms:priv func_id="323630">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/postAnalyse/notaccordpost.do?br_init=link&query=1" target="il_body"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	             </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                 <hrms:link href="/train/postAnalyse/notaccordpost.do?br_init=link&query=1" target="il_body"><font id="a001" class="menu_a" >不符合本岗位培训要求</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	             <hrms:priv func_id="323631">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/postAnalyse/notaccordpost.do?br_init=link&query=2&flag=1" target="il_body"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	             </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                 <hrms:link href="/train/postAnalyse/notaccordpost.do?br_init=link&query=2&flag=1" target="il_body"><font id="a001" class="menu_a" >按岗位要求匹配</font></hrms:link>
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
	<hrms:priv func_id="3237">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="7">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle7 onclick="menuChange(menu7,divHeight,menuTitle7,arrow7);"><span><span id=arrow7><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu7> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <!--
	            <hrms:priv func_id="32370">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="###" target="il_body" onclick="turn()"><img src="/images/edit_info.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="###" target="il_body" ><font id="a001" class="menu_a" onclick="turn()">指标维护</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	              -->
	            <hrms:priv func_id="32371">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/traincourse/inDate.do?b_query=link&returnvalue=" target="il_body"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	             </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                 <hrms:link href="/train/traincourse/inDate.do?b_query=link&returnvalue=" target="il_body"><font id="a001" class="menu_a" >数据初始化</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>  
	            
	            <hrms:priv func_id="323720">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&amp;subsys_id=20&returnvalue=" target="il_body" onclick="turn()"><img src="/images/ll.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&amp;subsys_id=20&returnvalue=" target="il_body" onclick="turn()"><font id="a001" class="menu_a" >评估指标</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>  	             
	            <hrms:priv func_id="323721">                     
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link&amp;subsys_id=20&amp;isVisible=1&amp;method=0&returnvalue=" target="il_body"  onclick="turn();"><img src="/images/ll.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/kh_system/kh_template/kh_template_tree.do?b_query=link&amp;subsys_id=20&amp;isVisible=1&amp;method=0&returnvalue=" target="il_body"  onclick="turn();"><font id="a013" class="menu_a" >评估模板</font></a></td>
	            </tr> 
	          </hrms:priv>                          
	            <hrms:priv func_id="32373">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/train/setparam/project.do?b_query=link&returnvalue=" target="il_body"><img src="/images/rzfs.gif" border=0></hrms:link></td>
	            </tr>
	             <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/train/setparam/project.do?b_query=link&returnvalue=" target="il_body"><font id="a002" class="menu_a" >其它参数</font></hrms:link>
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
	 </html:form>
 </td>

</tr>
</table> 
<script language="javascript">
	showFirst();
</script>  



                                                                                                                                                       