<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
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
 </SCRIPT>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -235;
</SCRIPT>

<body class="menuBodySet" style="margin:0 0 0 0">  
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol"> 
	<hrms:priv func_id="3220">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>信息浏览</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="32204">       
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/deci/leader/leaderframe.do?b_query=link" onclick="turn()" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/deci/leader/leaderframe.do?b_query=link" onclick="turn()" target="il_body"><font id="a003" class="menu_a" >领导班子</font></a></td>
	            </tr> 
	             </hrms:priv>	     
	           <hrms:priv func_id="32200">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr&flag=noself" target="il_body" onclick="turn()"><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr&flag=noself" target="il_body" ><font id="a001" class="menu_a" onclick="turn()">人员信息</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>  
	           <hrms:priv func_id="32201">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/inform/org/searchorgbrowse.do?b_query=link" target="il_body" function_id="xxx" onclick="turn()"><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/inform/org/searchorgbrowse.do?b_query=link" target="il_body" function_id="xxx" onclick="turn()"><font id="a001" class="menu_a">机构信息</font></hrms:link>
	              </td>
	            </tr>  
	          </hrms:priv>  
	         <hrms:priv func_id="32202">         
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchorgmap.do?b_search=link" target="il_body"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchorgmap.do?b_search=link" target="il_body"><font id="a003" class="menu_a" >机 构 图</font></a></td>
	            </tr> 
	         </hrms:priv>    
	         <hrms:priv func_id="32203">         
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/synthesisbrowse.do?b_dbname=link" target="il_body" onclick="turn()"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/synthesisbrowse.do?b_dbname=link" target="il_body" onclick="turn()"><font id="a003" class="menu_a" >综合信息</font></a></td>
	            </tr> 
	         </hrms:priv>  

	      </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>  
	 
	<hrms:priv func_id="3221">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>统计分析</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="32210">       
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=1&home=0&statid=" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=1&home=0&statid=" target="il_body"><font id="a003" class="menu_a" >人员统计</font></a></td>
	            </tr> 
	             </hrms:priv>  
	         	<hrms:priv func_id="32211">          
	            	<tr>
	              		<td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=2&home=0&statid=" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	            	</tr>
	            	<tr>
	              		<td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=2&home=0&statid=" target="il_body"><font id="a003" class="menu_a" >单位统计</font></a></td>
	            	</tr> 
	         	</hrms:priv>
	         	<hrms:priv func_id="32212">                  
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=3&home=0&statid=" target="il_body"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/static/commonstatic/statshow.do?b_ini=link&infokind=3&home=0&statid=" target="il_body"><font id="a003" class="menu_a" >职位统计</font></a></td>
	            </tr> 
	            </hrms:priv>   
	          <hrms:priv func_id="32213">         
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/deci/statics/loademploymakeupanalyse.do?b_search=link" target="il_body" onclick="turn()"><img src="/images/lstatic.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/deci/statics/loademploymakeupanalyse.do?b_search=link" target="il_body" onclick="turn()"><font id="a003" class="menu_a" >人员结构分析</font></a></td>
	            </tr> 
	         </hrms:priv>                   	                
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>
	 <hrms:priv func_id="3222">   
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>指标分析</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
			  <hrms:priv func_id="32220">            
	          <tr>
	              <td  align="center" class="loginFont" ><a href="/general/deci/browser/single/single_field_analyse.do?br_query=link" target="il_body" onclick="turn()"><img src="/images/lstatic.gif" border=0 ></a></td>
	          </tr>
	          <tr>
	              <td  align="center" class="loginFont" ><a href="/general/deci/browser/single/single_field_analyse.do?br_query=link" target="il_body" onclick="turn()"><font id="a006" class="menu_a" >单指标分析</font></a></td>
	          </tr> 
	          </hrms:priv>  
	          
	          <hrms:priv func_id="32221">            
	          <tr>
	              <td  align="center" class="loginFont" ><a href="/general/deci/browser/much/much_field_analyse.do?br_query=link" target="il_body" onclick="turn()"><img src="/images/lstatic.gif" border=0 ></a></td>
	          </tr>
	          <tr>
	              <td  align="center" class="loginFont" ><a href="/general/deci/browser/much/much_field_analyse.do?br_query=link" target="il_body" onclick="turn()"><font id="a006" class="menu_a" >多指标分析</font></a></td>
	          </tr> 
	          </hrms:priv> 
	                                            
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	 </hrms:priv>
	
	
	<hrms:priv func_id="3223">  
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>报表分析</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
									<hrms:priv func_id="32230">
										<tr>
											<td align="center" class="loginFont">
												<hrms:link href="/report/report_analyse/reportunittree.do" target="il_body" onclick="turn()">
													<img src="/images/mc.gif" border=0>
												</hrms:link>
											</td>
										</tr>
										<tr>
											<td align="center" class="loginFont">
												<hrms:link href="/report/report_analyse/reportunittree.do" target="il_body" onclick="turn()">
													<font id="a001" class="menu_a">报表分析</font>
												</hrms:link>
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
</td>

</tr>
</table> 
<script language="javascript">
	showFirst();
</script>  



                                                                                                                                                       