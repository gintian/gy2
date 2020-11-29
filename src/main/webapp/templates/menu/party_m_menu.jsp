<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
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
	<html:form action="/templates/menu/party_m_menu">

	<hrms:priv func_id="3501">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>党务管理</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="35011">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/dtgh/party/searchpartybusinesstree.do?b_query=link&param=Y&backdate=" target="il_body" onclick="turn()"><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/dtgh/party/searchpartybusinesstree.do?b_query=link&param=Y&backdate=" target="il_body" onclick="turn()"><font id="a001" class="menu_a">党组织机构</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	            <hrms:priv func_id="35012">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/dtgh/party/person/searchbusinesstree.do?b_query=link&param=Y&backdate=&politics=" target="il_body" onclick="turn()"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/dtgh/party/person/searchbusinesstree.do?b_query=link&param=Y&backdate=&politics=" target="il_body" onclick="turn()"><font id="a001" class="menu_a" >党组织内人员</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>  	 	                          
	          </table>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>  
	 
	<hrms:priv func_id="3502">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>团务管理</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="35021">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/dtgh/party/searchpartybusinesstree.do?b_query=link&param=V&backdate=" target="il_body" onclick="turn()"><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/dtgh/party/searchpartybusinesstree.do?b_query=link&param=V&backdate=" target="il_body" onclick="turn()"><font id="a001" class="menu_a">团组织机构</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	            <hrms:priv func_id="35022">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/dtgh/party/person/searchbusinesstree.do?b_query=link&param=V&backdate=&politics=" target="il_body" onclick="turn()"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/dtgh/party/person/searchbusinesstree.do?b_query=link&param=V&backdate=&politics=" target="il_body" onclick="turn()"><font id="a001" class="menu_a" >团组织内人员</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>   
	     
         </table>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>
	 <!-- 
	 <hrms:priv func_id="3503">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>工会组织</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="35031">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/dtgh/party/searchpartybusinesstree.do?b_query=link&param=W&backdate=" target="il_body" onclick="turn()"><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/dtgh/party/searchpartybusinesstree.do?b_query=link&param=W&backdate=" target="il_body" onclick="turn()"><font id="a001" class="menu_a">工会组织机构</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	            <hrms:priv func_id="35032">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/inform/org_tree.do?b_query=link" target="il_body" onclick="turn()"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="" target="il_body" onclick="turn()"><font id="a001" class="menu_a" >工会会员</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>             
          </table>
	   </div>
	 </td>
	  </tr>
	</table>
	</hrms:priv>
	  -->
	
	<hrms:priv func_id="3504">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	         <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="35041">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/dtgh/party/person/party_parameter.do?b_query=link" target="il_body" ><img src="/images/hmuster.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/dtgh/party/person/party_parameter.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">参数设置</font></hrms:link>
	              </td>
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



                                                                                                                                                       