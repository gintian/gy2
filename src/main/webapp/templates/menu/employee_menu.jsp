<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="java.sql.*"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter"%>
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
	Connection connection=null;
	String browse_photo="";
	try
	{
	   connection = (Connection) AdminDb.getConnection();
	   Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(connection);
	   browse_photo=sysbo.getValue(Sys_Oth_Parameter.BROWSE_PHOTO);	
	   browse_photo=browse_photo!=null&&browse_photo.length()>0?browse_photo:"0";//0默认为表格信息，1照片显示	
	}catch(Exception e)
	{
	}finally
	{
	  if(connection!=null)
	   connection.close();	
	}
	String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -80;
   
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
	<hrms:priv func_id="030101,0312,030701,030301,030401,0308,030201,030501,0306,0313">	  
	<table cellpadding=0 cellspacing=0 width=159 class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);"><span><span id=arrow1><img src="/images/darrow.gif" border=0></span>员工信息</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
	   <form name="employ" action="" method=post target="il_menu">
	      <table cellpadding="2" cellspacing="3" align="center" width="100%"  class="DetailTable" style="position:relative;top:10px;">
		<hrms:priv func_id="030101">      	 
	            <tr>
	              <td  align="center" class="loginFont" >
	              <%if(browse_photo!=null&&browse_photo.equals("1")){%>
	                <hrms:link href="/workbench/browse/showphoto.do?b_search=link&action=showphotodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=0" target="il_body"  onclick="turn();"><img src="/images/cx.gif" border=0></hrms:link>
	              <%}else{%>
	               <hrms:link href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=0" target="il_body"  onclick="turn();"><img src="/images/cx.gif" border=0></hrms:link>
	              <%} %>	               
	              </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <%if(browse_photo!=null&&browse_photo.equals("1")){%>
	                <hrms:link href="/workbench/browse/showphoto.do?b_search=link&action=showphotodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=0" target="il_body"  onclick="turn();"><font id="a001" class="menu_a">信息浏览</font></hrms:link>
	              <%}else{%>
	               <hrms:link  href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=&flag=noself&isUserEmploy=0" target="il_body"  onclick="turn();"><font id="a001" class="menu_a">信息浏览</font></hrms:link>
	              <%} %>
	              </td>
	            </tr>
	        </hrms:priv> 
		<hrms:priv func_id="0312">     	        
	       		<tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/muster/emp_muster.do?b_query=link" target="il_body"><img src="/images/browser.gif" border="0"></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/general/muster/emp_muster.do?b_query=link" target="il_body" ><font id="a001" class="menu_a">员工名册</font></hrms:link>
	              </td>
	            </tr>	
	    </hrms:priv>   	                    
		<hrms:priv func_id="030701">          
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/synthesisbrowse.do?b_dbname=link" target="il_body" onclick="turn()"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/inform/synthesisbrowse.do?b_dbname=link" target="il_body" onclick="turn()"><font id="a003" class="menu_a" >综合信息</font></a></td>
	            </tr>    
	        </hrms:priv>                  
		<hrms:priv func_id="030301">                  
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/workbench/query/query_interface.do?b_query=link&a_inforkind=1&home=0" target="il_body"><img src="/images/cx.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/workbench/query/query_interface.do?b_query=link&a_inforkind=1&home=0" target="il_body"><font class="menu_a">信息查询</font></a></td>
	            </tr>
	        </hrms:priv>          
		<hrms:priv func_id="030401">  
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/info/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&flag=noself" target="il_body" onclick="turn();"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/workbench/info/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&flag=noself&isBrowse=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a">信息维护</font></hrms:link>
	              </td>
	            </tr>
	        </hrms:priv>   
	            <hrms:priv func_id="0308">                     
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/approve/personinfo/sum.do?b_query=link" target="il_body" ><img src="/images/investigate.gif" border=0 ></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/approve/personinfo/sum.do?b_query=link" target="il_body" ><font id="a013" class="menu_a" >信息审核</font></a></td>
	            </tr> 
	          </hrms:priv>   
		<hrms:priv func_id="030201">           
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/ykcard/showinfo.do?b_search=link&action=showinfodata.do&target=mil_body&flag=noself" target="il_body" onclick="turn();"><img src="/images/jhhs.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/ykcard/showinfo.do?b_search=link&action=showinfodata.do&target=mil_body&flag=noself" target="il_body" onclick="turn();"><font id="a002" class="menu_a" >员工薪酬</font></hrms:link>
	              </td>
	            </tr>
	        </hrms:priv>   
	        <hrms:priv func_id="030501">       
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/media/showinfo.do?b_search=link&isUserEmploy=1&action=showinfodata.do&target=mil_body" target="il_body" onclick="turn();"><img src="/images/mbsz.gif" border=0></hrms:link></td>
	            </tr>
	             <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/workbench/media/showinfo.do?b_search=link&isUserEmploy=1&action=showinfodata.do&target=mil_body" target="il_body" onclick="turn();"><font id="a002" class="menu_a" >多媒体</font></hrms:link>
	              </td>
	            </tr>
	       </hrms:priv>   
	       
	        <hrms:priv func_id="0306">     
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/workdiary/workdiary.do" target="il_body" onclick="turn();"><img src="/images/browser.gif" border="0"></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/performance/workdiary/workdiary.do" target="il_body" onclick="turn();"><font id="a001" class="menu_a">员工日志</font></hrms:link>
	              </td>
	            </tr>
	       </hrms:priv> 
	       <hrms:priv func_id="0313">       
				<tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/impev/importantev.do?b_tree=link" target="il_body" onclick="turn();"><img src="/images/browser.gif" border="0"></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/general/impev/importantev.do?b_tree=link" target="il_body" onclick="turn();"><font id="a001" class="menu_a">重要信息报告</font></hrms:link>
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
	<hrms:priv func_id="26031,0309,26032,0311">	
	<table cellpadding=0 cellspacing=0 width=159 class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);"><span><span id=arrow2><img src="/images/darrow.gif" border=0></span>员工名册</span></td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	   <form name="employ" action="" method=post target="il_menu">
	      <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="26031,0309">       
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchroster.do?b_search=link&a_inforkind=1&result=0" target="il_body" onclick="turn();"><img src="/images/hmuster.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/general/muster/hmuster/searchroster.do?b_search=link&a_inforkind=1&result=0" target="il_body" onclick="turn();"><font id="a003" class="menu_a" >常用花名册</font></a></td>
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
	
	       
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>	
	 </hrms:priv>	
	 <hrms:priv func_id="0314">	
		<table cellpadding=0 cellspacing=0 width=159 class=menu_table index="3">
		  <tr style="cursor:hand;">
		    <td  class=menu_title align="center" id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);"><span><span id=arrow3><img src="/images/darrow.gif" border=0></span>数据上报</span></td>
		  </tr>
		  <tr>
		    <td>
		   <div class=sec_menu style="width:159;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
		   <form name="employ" action="" method=post target="il_menu">
		      <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
		           
		            <tr>
		              <td  align="center" class="loginFont" ><a href="/gz/gz_data/gz_set_list.do?b_query=link" target="il_body" onclick="turn();"><img src="/images/bjbb.gif" border=0></a></td>
		            </tr>
		            <tr>
		              <td  align="center" class="loginFont" ><a href="/gz/gz_data/gz_set_list.do?b_query=link" target="il_body" onclick="turn();"><font id="a003" class="menu_a" ></font>数据上报</a></td>
		            </tr> 
		     
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


                                                                              