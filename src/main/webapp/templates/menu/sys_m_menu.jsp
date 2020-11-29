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
	int version = userView.getVersion();
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight- window.screenTop -200;
   function turn()
   {
	 <% 
	  if(isturn==null||!isturn.equals("false"))
	     out.println("parent.menupnl.toggleCollapse(false);");
	%>
   }  
    
   function install_ocx()
   {
    	
   }  
</SCRIPT>

<body class="menuBodySet" style="margin:0 0 0 0" >  
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol">        
	<hrms:priv func_id="3002"> 
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="1">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>内容维护</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu1> 
	          <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="070301,30020">                     
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/propose/upfilelist.do?b_query=link&fileflag=2" target="il_body" function_id="xxx"><img src="/images/flow_upload.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	            <td  align="center" class="loginFont" >
	             <hrms:link href="/selfservice/propose/upfilelist.do?b_query=link&fileflag=2" target="il_body" function_id="xxx"><font id="a001" class="menu_a" >流程上传</font></hrms:link>
	            </td>
	            </tr> 
	          </hrms:priv>             
		  <hrms:priv func_id="070401,30021">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/propose/upfilelist.do?b_query=link&fileflag=1" target="il_body" function_id="xxx"><img src="/images/table_upload.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	               <hrms:link href="/selfservice/propose/upfilelist.do?b_query=link&fileflag=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a" >表格上传</font></hrms:link>
	              </td>
	            </tr>
	          </hrms:priv>   
		 
		  <hrms:priv func_id="070501,30022">                    
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/selfservice/infomanager/board/searchboard.do?b_query=link&opt=1&announce=1" target="il_body" function_id="xxx"><img src="/images/public_info.gif" border=0 ></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/selfservice/infomanager/board/searchboard.do?b_query=link&opt=1&announce=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a" >公告栏维护</font></hrms:link>
	              </td>
	            </tr> 
	          </hrms:priv>   
		      <hrms:priv func_id="070601,30023">                     
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/infomanager/askinv/searchtopic.do?b_query=link" target="il_body" ><img src="/images/investigate.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/infomanager/askinv/searchtopic.do?b_query=link" target="il_body" ><font id="a013" class="menu_a" >问卷调查表定义</font></a></td>
	            </tr> 
	          </hrms:priv> 
	          <hrms:priv func_id="070902,30024">                                 
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/param/friend.do?b_query=link" target="il_body" ><img src="/images/card_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/param/friend.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >友情链接</font></a></td>
	            </tr>
	          </hrms:priv>            
	
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv> 
	
	<hrms:priv func_id="3001">  
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="2">
	  <tr style="cursor:hand;">
	    <td width="1383" align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>应用设置</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu2> 
	          <table cellpadding=2 cellspacing=3 align=center width="100%"   class="DetailTable" style="position:relative;top:10px;">
		  <hrms:priv func_id="070101,300150">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/query_template.do?b_query=link" target="il_body" ><img src="/images/query_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/query_template.do?b_query=link" target="il_body" ><font id="a005" class="menu_a" >查询设置</font></a></td>
	            </tr>
	          </hrms:priv>
	
		  <hrms:priv func_id="070201,300152">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/cardconstantset.do?b_cardset=set" target="il_body" ><img src="/images/salary_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/cardconstantset.do?b_cardset=set" target="il_body" ><font id="a006" class="menu_a" >薪酬表设置</font></a></td>
	            </tr> 
	          </hrms:priv>          
	          <hrms:priv func_id="30015">           
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/sys_param_panel.do" target="il_body" ><img src="/images/rzfs.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/sys_param_panel.do" target="il_body" ><font id="a006" class="menu_a" >参数设置</font></a></td>
	            </tr>  
	          </hrms:priv>                      
	       
			  <hrms:priv func_id="30017">              
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/deci/definition/search_definition.do?b_query=link&a_inforkind=1" target="il_body" ><img src="/images/gzfh.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/deci/definition/search_definition.do?b_query=link&a_inforkind=1" target="il_body" ><font id="a001" class="menu_a">单指标分析</font></hrms:link>
	              </td>
	            </tr> 
	          </hrms:priv>  
	          <hrms:priv func_id="30019">                           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/deci/definition/statCutline/searchStatCutline.do?b_query=link" target="il_body" ><img src="/images/rz.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/deci/definition/statCutline/searchStatCutline.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">多指标分析</font></hrms:link>
	              </td>
	            </tr> 
	          </hrms:priv> 
	          <hrms:priv func_id="3001A,080805">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/message/sys_manager.do?b_query=link" target="il_body" ><img src="/images/ld.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/message/sys_manager.do?b_query=link" target="il_body" ><font id="a004" class="menu_a">系统公告维护</font></a></td>
	            </tr>
	          </hrms:priv>  
	          <hrms:priv func_id="3001B,080807">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/export/searchclass_info.do?b_query=link" target="il_body" ><img src="/images/rzfs.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/export/searchclass_info.do?b_query=link" target="il_body" ><font id="a004" class="menu_a">后台作业</font></a></td>
	            </tr>
	          </hrms:priv> 
	          
	          
	           <hrms:priv func_id="3001C">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/email_template/addEmailTemplate.do?b_tree=link" target="il_body" ><img src="/images/rzfs.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/email_template/addEmailTemplate.do?b_tree=link" target="il_body" ><font id="a004" class="menu_a">邮件模板</font></a></td>
	            </tr>
	          </hrms:priv>  
	          
	          	          <!--   	          
	          <hrms:priv func_id="30016">     
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/sys_maintence.do" target="il_body" ><img src="/images/card.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/sys_maintence.do" target="il_body" ><font id="a006" class="menu_a" >系统维护</font></a></td>
	            </tr>  
	          </hrms:priv> 
	          --> 
	          <hrms:priv func_id="080809,3001E">  	          
	           <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/addressbook/addressbookset.do?b_search=link" target="il_body" ><img src="/images/addrnote_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/addressbook/addressbookset.do?b_search=link" target="il_body" ><font id="a007" class="menu_a" >通讯录设置</font></a></td>
	            </tr> 
	          </hrms:priv>  
	          <hrms:priv func_id="080808,3001D">           
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/sys/export/hr_org_tree.do?b_query=link" target="il_body" ><img src="/images/px.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/sys/export/hr_org_tree.do?b_query=link" target="il_body" ><font id="a004" class="menu_a">数据视图</font></a></td>
	            </tr>
	          </hrms:priv>   	          
	          <hrms:priv func_id="3001F">                           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/system/options/interfaces/setInterfaces.do?b_query=link" target="il_body" ><img src="/images/rz.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/system/options/interfaces/setInterfaces.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">平台数据接口设置</font></hrms:link>
	              </td>
	            </tr> 
	          </hrms:priv>
	          <hrms:priv func_id="3001G">   

	          		<tr>
	              		<td  align="center" class="loginFont" ><hrms:link href="/system/options/customreport.do?b_search=link&toFirst=yes" target="il_body" ><img src="/images/card.gif" border="0"></hrms:link></td>
	            	</tr>
	            	<tr>
	              		<td  align="center" class="loginFont" >
	                		<hrms:link href="/system/options/customreport.do?b_search=link&toFirst=yes" target="il_body" ><font id="a001" class="menu_a">自定制报表</font></hrms:link>
	              		</td>
	            	</tr> 
	          </hrms:priv>         
	          <!--                                              
	            
	            <tr>
	              <td  align="center" class="loginFont" ><img src="/images/card_set.gif" border=0 ></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/cardingcard/cardingcardset.do?b_cardset=set" target="il_body" ><font id="a008" class="menu_a" >名片夹设置</font></a></td>
	            </tr> 
	           
	 --> 
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv>  
	<!-- 
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>规章制度</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu3> 
	          <table cellpadding=2 cellspacing=3 align=center width=154  class="DetailTable" style="position:relative;top:10px;">
		  <hrms:priv func_id="070701">           
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/lawbase/law_maintenance0.do?basetype=1" target="il_body" ><img src="/images/public_info.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/lawbase/law_maintenance0.do?basetype=1" target="il_body" ><font id="a014" class="menu_a" >规章制度</font></a></td>
	            </tr>
	          </hrms:priv>            
	          </table>
	     </div>
	   </td>
	  </tr>
	</table>
	-->
	
	
	<hrms:priv func_id="3003">
	<table cellpadding="0" cellspacing=0 width="159" class="menu_table" index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>权限管理</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	
	     <table cellpadding=2 cellspacing=3 align=center width="100%"   class="DetailTable" style="position:relative;top:10px;">
	    
	     	  <hrms:priv func_id="080101,30030">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/system/security/rolesearch.do?b_query=link" target="il_body" function_id="xxx"><img src="/images/role.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/system/security/rolesearch.do?b_query=link" target="il_body" function_id="xxx"><font id="a001" class="menu_a">角色管理</font></hrms:link>
	              </td>
	            </tr>
	          </hrms:priv> 
	     	  <hrms:priv func_id="30031">                      
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/logonuser/search_user_tree.do?b_query=link" target="il_body" onclick="turn();"><img src="/images/organization.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/logonuser/search_user_tree.do?b_query=link" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >用户管理</font></a></td>
	            </tr> 
	          </hrms:priv> 
	     	  <hrms:priv func_id="080301,30032">                          
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/system/security/assign_login0.do" target="il_body" ><img src="/images/account.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/system/security/assign_login0.do" target="il_body" ><font id="a002" class="menu_a" >帐号分配</font></hrms:link>
	              </td>
	            </tr>
	          </hrms:priv>
	     	  <hrms:priv func_id="080701,30033">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/assign_login1.do" target="il_body" ><img src="/images/sp.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/assign_login1.do" target="il_body" ><font id="a004" class="menu_a">角色快速分配</font></a></td>
	            </tr>
	          </hrms:priv>            
	          </table>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	
	
	<hrms:priv func_id="3004">
	<table cellpadding=0 cellspacing=0 width=159 class=menu_table index="4">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>安全策略</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"   class="DetailTable" style="position:relative;top:10px;">
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/online_user.do?b_query=link" target="il_body" ><img src="/images/employee.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/online_user.do?b_query=link" target="il_body" ><font id="a016" class="menu_a" >在线用户</font></a></td>
	            </tr>              
	          <hrms:priv func_id="080801,30043">          
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/query_log.do?b_query=link" target="il_body" ><img src="/images/bb.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/query_log.do?b_query=link" target="il_body" ><font id="a016" class="menu_a" >操作日志</font></a></td>
	            </tr>  
	          </hrms:priv>       
	     	  <hrms:priv func_id="080401,30040">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/ip_addr_manager.do?b_query=link" target="il_body" ><img src="/images/ld.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/ip_addr_manager.do?b_query=link" target="il_body" ><font id="a004" class="menu_a">IP地址管理</font></a></td>
	            </tr>
	          </hrms:priv>  
	          
	
	     	  <hrms:priv func_id="080601,30041">               
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/login_base_options.do?b_query=link" target="il_body" ><img src="/images/bx.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/login_base_options.do?b_query=link" target="il_body" ><font id="a016" class="menu_a" >认证应用库</font></a></td>
	            </tr>
	          </hrms:priv>
	     	  <hrms:priv func_id="080701,30042">          
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/login_username_options.do?b_query=link" target="il_body" ><img src="/images/admin_pwd.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/security/login_username_options.do?b_query=link" target="il_body" ><font id="a016" class="menu_a" >认证用户名</font></a></td>
	            </tr>  
	          </hrms:priv>
	     	  <hrms:priv func_id="30044,080804">            
	          <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/ad_authenticateset.do?b_search=link" target="il_body" ><img src="/images/admin_pwd.gif" border=0 ></a></td>
	          </tr>
	          <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/ad_authenticateset.do?b_search=link" target="il_body" ><font id="a006" class="menu_a" >LDAP目录认证配置</font></a></td>
	          </tr>  
	          </hrms:priv>  
	       
	          </table>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	
	<hrms:priv func_id="3006">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="6">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);"><span><span id=arrow6><img src="/images/darrow.gif" border=0></span>通讯平台</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu6> 
	          <table cellpadding=2 cellspacing=3 align=center width="100%"   class="DetailTable" style="position:relative;top:10px;">
			  <hrms:priv func_id="30064"> 			         
	            <tr>
	              <!-- <td  align="center" class="loginFont" ><A href="/system/sms/mail_list.do?b_query=link"  target="il_body"><img src="/images/addrnote_set.gif" border=0 ></a></td> -->
	              <td  align="center" class="loginFont" ><A href="/system/sms/sms_list_options.do?br_query=link"  target="il_body"><img src="/images/addrnote_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <!-- <td  align="center" class="loginFont" ><A href="/system/sms/mail_list.do?b_query=link"  target="il_body"><font id="a014" class="menu_a" >短信信箱</font></a></td> -->
	              <td  align="center" class="loginFont" ><A href="/system/sms/sms_list_options.do?br_query=link"  target="il_body"><font id="a014" class="menu_a" >短信信箱</font></a></td>
	            </tr>  
				</hrms:priv>  
			  <hrms:priv func_id="30065"> 				           
	            <tr>
	              <td  align="center" class="loginFont" ><A href="/system/sms/send_sms.do"  target="il_body"><img src="/images/addr_note.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><A href="/system/sms/send_sms.do"  target="il_body"><font id="a014" class="menu_a" >发送短信</font></a></td>
	            </tr> 
				</hrms:priv>                           
	           
	          <hrms:priv func_id="070301,30061">            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/param/phoneparam.do?b_phone=link" target="il_body" ><img src="/images/cardset.gif" border=0 ></a></td>
	            </tr>
	               <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/param/phoneparam.do?b_phone=link" target="il_body" ><font id="a006" class="menu_a" >电话邮箱设置</font></a></td>
	            </tr> 
	          </hrms:priv>           
	          <hrms:priv func_id="30062">             
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/stmp_options.do?b_query=link" target="il_body" ><img src="/images/addrnote_set.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/system/options/stmp_options.do?b_query=link" target="il_body" ><font id="a006" class="menu_a" >邮件服务器</font></a></td>
	            </tr> 
	          </hrms:priv>           
			  <hrms:priv func_id="30063">          
	            <tr>
	              <td  align="center" class="loginFont" ><A href="/system/sms/sms_options.do?br_query=link"  target="il_body"><img src="/images/tool.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><A href="/system/sms/sms_options.do?br_query=link"  target="il_body"><font id="a014" class="menu_a" >短信接口参数</font></a></td>
	            </tr>  
				</hrms:priv>    
	         </table>
	     </div>
	   </td>
	  </tr>
	</table>
	</hrms:priv>  
	
	
	<hrms:priv func_id="3005">
	<table cellpadding=0 cellspacing=0 width=159  class="menu_table" index="7">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle7 onclick="menuChange(menu7,divHeight,menuTitle7,arrow7);"><span><span id=arrow7><img src="/images/darrow.gif" border=0></span>资源下载</span></td>
	  </tr>
	  <tr>
	    <td>
	     <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"   id=menu7> 
	          <table cellpadding=2 cellspacing=3 align=center width="100%"   class="DetailTable" style="position:relative;top:10px;">
      
	           <hrms:priv func_id="30050">                                                             
	            <tr>
	              <td  align="center" class="loginFont" ><A href="/thirdparty/jre-1_5_0_06-windows-i586-p.exe"  target="i_body"><img src="/images/table_download.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><A href="/thirdparty/jre-1_5_0_06-windows-i586-p.exe"  target="i_body"><font id="a014" class="menu_a" >jdk下载</font></a></td>
	            </tr>  
	          </hrms:priv> 
	           <hrms:priv func_id="30051">             
	            <tr>
	              <td  align="center" class="loginFont" ><A href="/thirdparty/SVGView.exe"  target="i_body"><img src="/images/table_download.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><A href="/thirdparty/SVGView.exe"  target="i_body"><font id="a014" class="menu_a" >SVG View</font></a></td>
	            </tr>  
	          </hrms:priv> 
	           <hrms:priv func_id="30053">             
	            <tr>
	              <td  align="center" class="loginFont" ><A href="/thirdparty/AdbeRdr70_chs_full.exe"  target="i_body"><img src="/images/table_download.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><A href="/thirdparty/AdbeRdr70_chs_full.exe"  target="i_body"><font id="a014" class="menu_a" >PDF阅读器</font></a></td>
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

                                                                              