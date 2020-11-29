<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
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
	
	<hrms:priv func_id="2600,26064">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>信息录入</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="26000">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/info/addinfo/add.do?b_add=add&a0100=A0100&i9999=I9999&actiontype=new&setname=A01&tolastpageflag=yes&flag=notself" target="il_body" onclick="turn()"><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/info/addinfo/add.do?b_add=add&a0100=A0100&i9999=I9999&actiontype=new&setname=A01&tolastpageflag=yes&flag=notself" target="il_body" onclick="turn()"><font id="a001" class="menu_a">新增人员</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv> 
	            <hrms:priv func_id="26064">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/inform/org_tree.do?b_query=link" target="il_body" onclick="turn()"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/inform/org_tree.do?b_query=link" target="il_body" onclick="turn()"><font id="a001" class="menu_a" >信息录入</font></hrms:link>
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
	 
	<hrms:priv func_id="2601">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>查询浏览</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id="26011">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=1" target="il_body" ><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=1" target="il_body" ><font id="a001" class="menu_a" >信息浏览</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>       
	           <hrms:priv func_id="26010">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/query_interface.do?b_query=link&a_inforkind=1&home=10" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/query_interface.do?b_query=link&a_inforkind=1&home=10" target="il_body" ><font id="a001" class="menu_a">快速查询</font></hrms:link>
	              </td>
	            </tr>
	            <hrms:priv func_id="2601002">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/hquery_interface.do?a_query=1&b_query=link&a_inforkind=1&home=1" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/hquery_interface.do?a_query=1&b_query=link&a_inforkind=1&home=1" target="il_body" ><font id="a001" class="menu_a">简单查询</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="2601003">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/hquery_interface.do?a_query=2&b_query=link&a_inforkind=1" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/hquery_interface.do?a_query=2&b_query=link&a_inforkind=1" target="il_body" ><font id="a001" class="menu_a">通用查询</font></hrms:link>
	              </td>
	            </tr>     
	            </hrms:priv>
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/query_interface.do?b_gquery=link&type=1&home=3" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/query_interface.do?b_gquery=link&type=1&home=3" target="il_body" ><font id="a001" class="menu_a">常用查询</font></hrms:link>
	              </td>
	            </tr>    
	            <hrms:priv func_id="2601004">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/query/complex_interface.do?b_gquery=link" target="il_body" ><img src="/images/cx.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/query/complex_interface.do?b_gquery=link" target="il_body" ><font id="a001" class="menu_a">复杂查询</font></hrms:link>
	              </td>
	            </tr>    
	            </hrms:priv>          
	         </hrms:priv>
	 
	     
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>
	 <hrms:priv func_id="2602">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>统计分析</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="26020">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/static/select_field.do?b_query=link&a_inforkind=1" target="il_body" ><img src="/images/lstatic.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/static/select_field.do?b_query=link&a_inforkind=1" target="il_body" ><font id="a001" class="menu_a">简单统计</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	            <hrms:priv func_id="26021">        
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/select_static_fields.do?b_query=link&a_inforkind=1" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/select_static_fields.do?b_query=link&a_inforkind=1" target="il_body"><font id="a003" class="menu_a" >通用统计</font></a></td>
	            </tr> 
	             </hrms:priv>
	            <hrms:priv func_id="26022">  
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/two_dim_static.do?b_query=link&a_inforkind=1" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/two_dim_static.do?b_query=link&a_inforkind=1" target="il_body"><font id="a003" class="menu_a" >二维统计</font></a></td>
	            </tr> 
	             </hrms:priv>
	            <hrms:priv func_id="26023">       
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=1&home=0" target="il_body" onclick="turn()"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=1&home=0" target="il_body" onclick="turn()"><font id="a003" class="menu_a" >常用统计</font></a></td>
	            </tr> 
	             </hrms:priv>
	            <hrms:priv func_id="26024">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/static/singlestatic/single_static.do?b_query=link&a_inforkind=1" target="il_body" ><img src="/images/lstatic.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/static/singlestatic/single_static.do?b_query=link&a_inforkind=1" target="il_body" ><font id="a001" class="menu_a">单项统计</font></hrms:link>
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
	 
	
	<hrms:priv func_id="2603">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>花 名 册</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="2603101">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/muster/select_muster_fields.do?b_query=link&a_inforkind=1" target="il_body" ><img src="/images/hmuster.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/muster/select_muster_fields.do?b_query=link&a_inforkind=1" target="il_body" ><font id="a001" class="menu_a">新建花名册</font></hrms:link>
	              </td>
	            </tr>
	              </hrms:priv>
	            <hrms:priv func_id="26031,0309">       
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchroster.do?b_search=link&a_inforkind=1&result=0" target="il_body" onclick="turn();"><img src="/images/hmuster.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchroster.do?b_search=link&a_inforkind=1&result=0" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >花名册</font></a></td>
	            </tr> 
	             </hrms:priv>
	            <hrms:priv func_id="26032,0311">  
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=3&a_inforkind=1&result=0" target="il_body" onclick="turn();"><img src="/images/ll.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=3&a_inforkind=1&result=0" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >高级花名册</font></a></td>
	            </tr> 
	             </hrms:priv>
			<!--
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/select_code_tree.do?b_query=link&codesetid=AB&codeitemid=&privflag=" target="il_body"><img src="/images/query_set.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/select_code_tree.do?b_query=link&codesetid=AB&codeitemid=&privflag=" target="il_body"><font id="a003" class="menu_a" >测试选择代码树</font></a></td>
	            </tr>
	            
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/temp/test_editor.do" target="il_body"><img src="/images/query_set.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/temp/test_editor.do" target="il_body"><font id="a003" class="menu_a" >测试编辑框</font></a></td>
	            </tr>   
	            
	           
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/frame.do" target="il_body"><img src="/images/query_set.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/frame.do" target="il_body"><font id="a003" class="menu_a" >信息维护例子</font></a></td>
	              <td  align="center" class="loginFont" ><a href="javascript:window.open('/general/inform/frame.do','_blank','toolbar=no,location=0,directories=0,status=no,menubar=no,scrollbars=no,resizable=yes');"><font id="a003" class="menu_a" >信息维护例子</font></a></td>
	            </tr>
	                 -->                                       
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>
	 <hrms:priv func_id="2604">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="6">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);"><span><span id=arrow6><img src="/images/darrow.gif" border=0></span>登 记 表</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu6> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=1&result=0" target="il_body"  onclick="turn();"><img src="/images/card.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=1&result=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a" >登 记 表</font></hrms:link>
	              </td>
	            </tr>
	             
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	
	 </hrms:priv>
	<hrms:priv func_id="2606">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="7">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle7 onclick="menuChange(menu7,divHeight,menuTitle7,arrow7);"><span><span id=arrow7><img src="/images/darrow.gif" border=0></span>信息维护</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu7> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="26060">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/info/showinfo.do?b_searchsort=link&action=showinfodata.do&isUserEmploy=1&target=nil_body&flag=noself" target="il_body" onclick="turn()"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/info/showinfo.do?b_searchsort=link&action=showinfodata.do&isUserEmploy=1&target=nil_body&flag=noself" target="il_body" ><font id="a001" class="menu_a" onclick="turn()">记录方式</font></hrms:link>
	              </td>
	            </tr>
	             </hrms:priv>
	            <hrms:priv func_id="26062">  
	            <tr>
	              <td  align="center" class="loginFont" ><A href="javascript:validate('11','10');"><img src="/images/edit_info.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <A href="javascript:validate('11','10');" ><font id="a001" class="menu_a">表格方式</font></a>
	              </td>
	            </tr>
	             </hrms:priv>  
            
	            <hrms:priv func_id="26063">                     
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/approve/personinfo/sum.do?b_query=link" target="il_body" ><img src="/images/investigate.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/approve/personinfo/sum.do?b_query=link" target="il_body" ><font id="a013" class="menu_a" >信息审核</font></a></td>
	            </tr> 
	          </hrms:priv>                          
	            <hrms:priv func_id="26061">  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/media/showinfo.do?b_search=link&isUserEmploy=1&action=showinfodata.do&target=mil_body" target="il_body" onclick="turn();"><img src="/images/mb.gif" border=0></hrms:link></td>
	            </tr>
	             <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/media/showinfo.do?b_search=link&isUserEmploy=1&action=showinfodata.do&target=mil_body" target="il_body" onclick="turn();"><font id="a002" class="menu_a" >多媒体</font></hrms:link>
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



                                                                                                                                                       