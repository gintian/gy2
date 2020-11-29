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
    String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
           //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	boolean version = false;
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	}	
%>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -120;
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
   var  divHeight = window.screen.availHeight - window.screenTop -205;
</SCRIPT>

<body class=menuBodySet style="margin:0 0 0 0">    
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="162" id="menucol">  
	<hrms:priv func_id="2301,23051,23052,23060,230600,23011">
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table  index="1">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>单位管理</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display=block;"  id=menu1> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	          <hrms:priv func_id="23011">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/inform/org/searchorgbrowse.do?b_query=link&returnvalue=&busiPriv=1" target="il_body" function_id="xxx" onclick="turn()"><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/inform/org/searchorgbrowse.do?b_query=link&returnvalue=&busiPriv=1" target="il_body" function_id="xxx" onclick="turn()"><font id="a001" class="menu_a">信息浏览</font></hrms:link>
	              </td>
	            </tr>  
	          </hrms:priv>   

             <%if(version){ %>	         
	         <hrms:priv func_id="230600">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/orginfo/searchorginfo.do?b_search=link&backdate=&returnvalue1=&action=searchorginfodata.do&treetype=vorg&kind=2&target=nil_body&loadtype=1&busiPriv=1" target="il_body" function_id="xxx" onclick="turn()"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/orginfo/searchorginfo.do?b_search=link&backdate=&returnvalue1=&action=searchorginfodata.do&treetype=vorg&kind=2&target=nil_body&loadtype=1&busiPriv=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a" onclick="turn()">信息维护</font></hrms:link>
	              </td>
	              
	            </tr>
	         </hrms:priv>   
			<%} else {%>   	
	         <hrms:priv func_id="23060">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/orginfo/editorginfo.do?b_search=link&returnvalue1=&action=editorginfodata.do&edittype=new&treetype=org&kind=2&target=mil_body&busiPriv=1" target="il_body" function_id="xxx" onclick="turn()"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/orginfo/editorginfo.do?b_search=link&returnvalue1=&action=editorginfodata.do&edittype=new&treetype=org&kind=2&target=mil_body&busiPriv=1" target="il_body" function_id="xxx"><font id="a001" class="menu_a" onclick="turn()">信息维护</font></hrms:link>
	              </td>
	              
	            </tr>
	         </hrms:priv>  
			 <%} %>   	         			                   
	         <hrms:priv func_id="23051">         
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchorgmap.do?b_search=link&returnvalue=" target="il_body"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchorgmap.do?b_search=link&returnvalue=" target="il_body"><font id="a003" class="menu_a" >机构图</font></a></td>
	            </tr> 
	         </hrms:priv>  
	         <hrms:priv func_id="23052">             
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchhistoryorgmap.do?b_search=link" target="il_body"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/org/map/searchhistoryorgmap.do?b_search=link" target="il_body"><font id="a003" class="menu_a" >历史机构</font></a></td>
	            </tr> 
	          </hrms:priv>	               
	           
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	
	</table>
	</hrms:priv>
	<hrms:priv func_id="25011,25060,25040,23059,2311">     
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);"><span><span id=arrow5><img src="/images/darrow.gif" border=0></span>岗位管理</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="25011"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/inform/pos/searchorgbrowse.do?b_query=link&returnvalue=" target="il_body" function_id="xxx" onclick="turn()"><img src="/images/browser.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/inform/pos/searchorgbrowse.do?b_query=link&returnvalue=" target="il_body" function_id="xxx"><font id="a001" class="menu_a" onclick="turn()">信息浏览</font></hrms:link>
	              </td>
	            </tr>   
	          </hrms:priv>  

	            <%if(version){ %>	
	         <hrms:priv func_id="231101"> 	            
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/dutyinfo/searchdutyinfo.do?b_search=link&returnvalue1=&backdate=&action=searchdutyinfodata.do&treetype=vorg&kind=2&target=nil_body&loadtype=1" target="il_body" function_id="xxx" onclick="turn()"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/dutyinfo/searchdutyinfo.do?b_search=link&returnvalue1=&backdate=&action=searchdutyinfodata.do&treetype=vorg&kind=2&target=nil_body&loadtype=1" target="il_body" function_id="xxx" onclick="turn()"><font id="a001" class="menu_a">岗位设置</font></hrms:link>
	              </td>
	            </tr>   
	         </hrms:priv>		            
	          <%} else {%>  
	         <hrms:priv func_id="25060"> 	           	
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/dutyinfo/editorginfo.do?b_search=link&returnvalue1=&action=editorginfodata.do&edittype=new&treetype=duty&kind=0&target=mil_body" target="il_body" function_id="xxx"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/dutyinfo/editorginfo.do?b_search=link&returnvalue1=&action=editorginfodata.do&edittype=new&treetype=duty&kind=0&target=mil_body" target="il_body" function_id="xxx"><font id="a001" class="menu_a">信息维护</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>	            
	           <%} %>    

	         <hrms:priv func_id="2504"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=4&returnvalue=" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/card.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=4&returnvalue=" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a" >岗位说明书</font></hrms:link>
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
	<hrms:priv func_id="23064,23061"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>编制管理</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="23061">	                  
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/org/autostatic/confset/datasynchro.do?b_init=link&returnvalue=" target="il_body" function_id="xxx"><img src="/images/jgbm.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/org/autostatic/confset/datasynchro.do?b_init=link&returnvalue=" target="il_body" function_id="xxx"><font id="a001" class="menu_a">数据联动</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv> 		     
	         <hrms:priv func_id="23064"> 	         
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/org/orgpre/get_org_tree.do?b_query=link&infor=2&unit_type=3&returnvalue=" target="il_body" onclick="turn();"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/org/orgpre/get_org_tree.do?b_query=link&infor=2&unit_type=3&returnvalue=" target="il_body"><font id="a001" class="menu_a" onclick="turn();">编制管理</font></hrms:link>
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
	<hrms:priv func_id="23050,25050,23056,23057,23058"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>基础设置</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="23050"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/org/orginfo/searchorgtree.do?b_query=link&backdate=&code=&returnvalue=" target="il_body" function_id="xxx"><img src="/images/organization.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/org/orginfo/searchorgtree.do?b_query=link&backdate=&code=&returnvalue=" target="il_body" function_id="xxx"><font id="a001" class="menu_a">机构编码</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>  
	         <hrms:priv func_id="23057"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&param=PS_CODE&returnvalue=" target="il_body" function_id="xxx"><img src="/images/rz.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&param=PS_CODE&returnvalue=" target="il_body" function_id="xxx"><font id="a001" class="menu_a">职务体系设置</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>	
	         <hrms:priv func_id="23056"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&param=PS_LEVEL_CODE&returnvalue=" target="il_body" function_id="xxx"><img src="/images/yw.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&param=PS_LEVEL_CODE&returnvalue=" target="il_body" function_id="xxx"><font id="a001" class="menu_a">职务级别设置</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>   
	         <hrms:priv func_id="25050"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&param=PS_C_CODE&returnvalue=" target="il_body" function_id="xxx"><img src="/images/sp.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&param=PS_C_CODE&returnvalue=" target="il_body" function_id="xxx"><font id="a001" class="menu_a">岗位体系设置</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>
	         <hrms:priv func_id="23058"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&param=PS_C_LEVEL_CODE&returnvalue=" target="il_body" function_id="xxx"><img src="/images/yw.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/posbusiness/searchposbusinesstree.do?b_query=link&first=4&param=PS_C_LEVEL_CODE&returnvalue=" target="il_body" function_id="xxx"><font id="a001" class="menu_a">岗位级别设置</font></hrms:link>
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

	<hrms:priv func_id="23062,25062"> 
	<table cellpadding=0 cellspacing=0 width="159" class=menu_table index="4">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);"><span><span id=arrow4><img src="/images/darrow.gif" border=0></span>参数设置</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	         <hrms:priv func_id="23062"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/posparameter/ps_parameter.do?b_search_unit=link&returnvalue=" target="il_body" function_id="xxx"><img src="/images/login_type.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/posparameter/ps_parameter.do?b_search_unit=link&returnvalue=" target="il_body" function_id="xxx"><font id="a001" class="menu_a">编制参数设置</font></hrms:link>
	              </td>
	            </tr>
	         </hrms:priv>
	          <hrms:priv func_id="25062"> 
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/pos/posparameter/ps_parameter.do?b_search=link&flag=pos" target="il_body" function_id="xxx"><img src="/images/login_type.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/pos/posparameter/ps_parameter.do?b_search=link&flag=pos" target="il_body" function_id="xxx"><font id="a001" class="menu_a">岗位参数设置</font></hrms:link>
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



                                                                                                                                                       