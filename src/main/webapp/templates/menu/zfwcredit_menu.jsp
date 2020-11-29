<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

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
	 
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<style type="text/css">
<!--
.tabtext {
	font-family: "宋体";
	font-size: 14px;
	font-style: normal;
	font-weight: 900;
	text-decoration: none;
}
.menu_a
{
   font-family: "宋体";
	font-size: 14px;
	font-style: normal;	
	font-weight: 550;
	text-decoration: none;
}
-->
</style>
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -80;
   
   function turn()
   {
	   parent.menupnl.toggleCollapse(false);
   }   
</SCRIPT>
<body class=menuBodySet style="margin:0 0 0 0">         
<table cellpadding=0 cellspacing=0 >
<tr>
  <td width="188" id="menucol">  
	<table cellpadding=0 cellspacing=0 width=188 class=menu_table index="1">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle1 onclick="menuChange(menu1,divHeight,menuTitle1,arrow1);">
	    <table cellpadding=0 cellspacing=0 width="100%">
	     <tr>
	     <td width="40">
	     </td>
	     <td valign="bottom">
	        <table cellpadding=0 cellspacing=0 width="100%">
	         <tr>
	           <td width="10" valign="bottom">
	             <span id=arrow1><img src="/images/darrow.gif" border=0></span>
	           </td>
	           <td align="left" valign="bottom">
	              &nbsp;<img src="/images/jbxx.png" border=0>
	           </td>
	         </tr>         
	        </table>
	     </td>
	     </tr>
	     </table>   
	    </td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:188;height:0;filter:alpha(Opacity=100);display:none;"  id=menu1> 
	   <form name="employ" action="" method=post target="il_menu">
	      <table cellpadding="2" cellspacing="3" align="center" width="100%"  class="DetailTable" style="position:relative;top:10px;">
		       <hrms:priv func_id="01">
		        <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/browse/showselfinfo.do?b_search=link&a0100=A0100&userbase=Usr&flag=infoself" target="il_body"><img src="/images/browser.gif" border="0"></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/workbench/browse/showselfinfo.do?b_search=link&a0100=A0100&userbase=Usr&flag=infoself" target="il_body" ><font id="a001" class="menu_a">我的信息</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="030101">
	            <tr>
	            <td  align="center" class="loginFont" >
	             <hrms:link href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr&flag=noself&isUserEmploy=0" target="il_body"  onclick="turn();"><img src="/images/cx.gif" border=0></hrms:link>
	            </td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/workbench/browse/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr&flag=noself&isUserEmploy=0" target="il_body"  onclick="turn();"><font id="a001" class="menu_a">队伍信息浏览</font></hrms:link>
	             </td>
	            </tr>
	            </hrms:priv>
	            <hrms:priv func_id="030401">
	             <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/workbench/info/showinfo.do?b_search=link&action=showinfodata.do&target=mil_body&flag=noself" target="il_body" onclick="turn();"><img src="/images/edit_info.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	              <hrms:link href="/workbench/info/showinfo.do?b_search=link&action=showinfodata.do&target=mil_body&flag=noself" target="il_body" onclick="turn();"><font id="a001" class="menu_a">队伍信息维护</font></hrms:link>
	              </td>
	            </tr>
	            </hrms:priv>
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>
	<hrms:priv func_id="2504"> 
	<table cellpadding=0 cellspacing=0 width=188 class=menu_table index="2">
	  <tr style="cursor:hand;">
	    <td  class=menu_title align="center" id=menuTitle2 onclick="menuChange(menu2,divHeight,menuTitle2,arrow2);">
	    <table cellpadding=0 cellspacing=0 width="100%">
	     <tr>
	     <td width="40">
	     </td>	     
	     <td>
	       <td valign="bottom">
	        <table cellpadding=0 cellspacing=0 width="100%">
	         <tr>
	           <td width="10" valign="bottom">
	             <span id=arrow2><img src="/images/darrow.gif" border=0></span>
	           </td>
	           <td align="left" valign="bottom">
	              &nbsp;<img src="/images/zwsms.png" border=0>
	           </td>
	         </tr>         
	        </table>
	     </td>
	       <!-- <span class="tabtext"><span id=arrow2><img src="/images/darrow.gif" border=0></span>职位说明书</span> -->
	     </tr>
	     </table>
	    </td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:188;height:0;filter:alpha(Opacity=100);display:none;"  id=menu2> 
	   <form name="employ" action="" method=post target="il_menu">
	      <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=4" target="il_body" function_id="xxx" onclick="turn();"><img src="/images/hmuster.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=4" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a" >职位说明书</font></hrms:link>
	              </td>
	            </tr>	           
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>	
	</hrms:priv>
	 <hrms:priv func_id="2604">  
	<table cellpadding=0 cellspacing=0 width="188" class=menu_table index="3">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle3 onclick="menuChange(menu3,divHeight,menuTitle3,arrow3);">
	    <table cellpadding=0 cellspacing=0 width="100%">
	     <tr>
	     <td width="40">
	     </td>
	     <td>
	        <table cellpadding=0 cellspacing=0 width="100%">
	         <tr>
	           <td width="10" valign="bottom">
	             <span id=arrow3><img src="/images/darrow.gif" border=0></span>
	           </td>
	           <td align="left" valign="bottom">
	              &nbsp;<img src="/images/gzrws.png" border=0>
	           </td>
	         </tr>         
	        </table>	        
	     </td>
	     </tr>
	     </table>
	   </td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:188;height:0;filter:alpha(Opacity=100);display:none;"  id=menu3> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	           <hrms:priv func_id=""> 
	          <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=1&result=0" target="il_body"  onclick="turn();"><img src="/images/hmuster.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/general/card/searchcard.do?b_query=link&home=2&inforkind=1&result=0" target="il_body" onclick="turn();"><font id="a001" class="menu_a" >工作任务书</font></hrms:link>
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
	<hrms:priv func_id="0608"> 
	<table cellpadding=0 cellspacing=0 width="188" class=menu_table index="4">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle4 onclick="menuChange(menu4,divHeight,menuTitle4,arrow4);">
	    <table cellpadding=0 cellspacing=0 width="100%">
	     <tr>
	     <td width="40">
	     </td>
	     <td>
	        <table cellpadding=0 cellspacing=0 width="100%">
	         <tr>
	           <td width="10" valign="bottom">
	             <span id=arrow4><img src="/images/darrow.gif" border=0></span>
	           </td>
	           <td align="left" valign="bottom">
	              &nbsp;<img src="/images/zfyj.png" border=0>
	           </td>
	         </tr>         
	        </table>	      
	     </td>
	     </tr>
	     </table>
	    </td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:188;height:0;filter:alpha(Opacity=100);display:none;"  id=menu4> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	            <hrms:priv func_id="060801"> 
	           <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/achivement/achivementTask.do?br_init=int" target="il_body"  ><img src="/images/hmuster.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/achivement/achivementTask.do?br_init=int" target="il_body" ><font id="a001" class="menu_a">工作计划</font></hrms:link>
	              </td>
	            </tr> 
	            </hrms:priv>
	            <hrms:priv func_id="060802">                     
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/achivement/dataCollection/khplanMenu.do?b_query=link&planContext=zfyj" target="il_body" ><img src="/images/employ_data.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" ><a href="/performance/achivement/dataCollection/khplanMenu.do?b_query=link&planContext=zfyj" target="il_body"  ><font id="a001" class="menu_a">完成情况</font></a></td>
	            </tr> 
	           	 </hrms:priv>                    
	          </table>
		</form>
	   </div>
	 </td>
	  </tr>
	</table>	
	</hrms:priv>
	<hrms:priv func_id="060601">  
	<table cellpadding=0 cellspacing=0 width="188" class=menu_table index="5">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle5 onclick="menuChange(menu5,divHeight,menuTitle5,arrow5);">
	    <table cellpadding=0 cellspacing=0 width="100%">
	     <tr>
	     <td width="40">
	     </td>
	     <td>
	        <table cellpadding=0 cellspacing=0 width="100%">
	         <tr>
	           <td width="10" valign="bottom">
	             <span id=arrow5><img src="/images/darrow.gif" border=0></span>
	           </td>
	           <td align="left" valign="bottom">
	              &nbsp;<img src="/images/qtyj.png" border=0>
	           </td>
	         </tr>         
	        </table>	     
	     </td>
	     </tr>
	     </table>
	    </td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:188;height:0;filter:alpha(Opacity=100);display:none;"  id=menu5> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	          <hrms:priv func_id="">     
	            <tr>
	              <td  align="center" class="loginFont" >
	              <a href="/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&planContext=qtyj" target="il_body" ><img src="/images/zd.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <a href="/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&planContext=qtyj" target="il_body" ><font id="a001" class="menu_a">考核打分</font></a>
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
	<hrms:priv func_id="060601">  
	<table cellpadding=0 cellspacing=0 width="188" class=menu_table index="6">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle6 onclick="menuChange(menu6,divHeight,menuTitle6,arrow6);">
	    <table cellpadding=0 cellspacing=0 width="100%">
	     <tr>
	     <td width="40">
	     </td>
	     <td>
	        <table cellpadding=0 cellspacing=0 width="100%">
	         <tr>
	           <td width="10" valign="bottom">
	             <span id=arrow6><img src="/images/darrow.gif" border=0></span>
	           </td>
	           <td align="left" valign="bottom">
	              &nbsp;<img src="/images/lzxy.png" border=0>
	           </td>
	         </tr>         
	        </table>	        
	     </td>
	     </tr>
	     </table>
	     </td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:188;height:0;filter:alpha(Opacity=100);display:none;"  id=menu6> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing=3 align=center width="100%"  class="DetailTable" style="position:relative;top:10px;">
	          <tr>
	              <td  align="center" class="loginFont" ><a href="/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&planContext=lzxy" target="il_body"   ><img src="/images/zb.gif" border=0></a></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	               <a href="/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&planContext=lzxy" target="il_body"  ><font id="a001" class="menu_a">考核打分</font></a>
	              </td>
	            </tr>  
	           
	       </table>
	   </form>  
	   </div>
	 </td>
	  </tr>
	</table>	
	</hrms:priv> 
	 <hrms:priv func_id="060301,060401">   
	<table cellpadding=0 cellspacing=0 width="188" class=menu_table index="7">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle7 onclick="menuChange(menu7,divHeight,menuTitle7,arrow7);">
	     <table cellpadding=0 cellspacing=0 width="100%">
	     <tr>
	     <td width="40">
	     </td>
	     <td>
	         <table cellpadding=0 cellspacing=0 width="100%">
	         <tr>
	           <td width="10" valign="bottom">
	             <span id=arrow7><img src="/images/darrow.gif" border=0></span>
	           </td>
	           <td align="left" valign="bottom">
	              &nbsp;<img src="/images/khfk.png" border=0>
	           </td>
	         </tr>         
	        </table>	        
	     </td>
	     </tr>
	     </table>
	    </td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:188;height:0;filter:alpha(Opacity=100);display:none;"  id=menu7> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing="3" align="center" width="100%"  class="DetailTable" style="position:relative;top:10px;">
	       
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
	              <hrms:link href="/performance/kh_result/kh_result_orgtree.do?b_init=init&distinctionFlag=0&model=1" target="il_body" function_id="xxx" onclick="turn();"><font id="a001" class="menu_a">人员考核结果</font></hrms:link>
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
	 <hrms:priv func_id="060304">   
	<table cellpadding=0 cellspacing=0 width="188" class=menu_table index="8">
	  <tr style="cursor:hand;">
	    <td align="center" class=menu_title  id=menuTitle8 onclick="menuChange(menu8,divHeight,menuTitle8,arrow8);">
	     <table cellpadding=0 cellspacing=0 width="100%">
	     <tr>
	     <td width="40">
	     </td>
	     <td>
	         <table cellpadding=0 cellspacing=0 width="100%">
	         <tr>
	           <td width="10" valign="bottom">
	             <span id=arrow8><img src="/images/darrow.gif" border=0></span>
	           </td>
	           <td align="left" valign="bottom">
	              &nbsp;<img src="/images/zfpm.png" border=0>
	           </td>
	         </tr>         
	        </table>	        
	     </td>
	     </tr>
	     </table>
	    </td>
	  </tr>
	  <tr>
	    <td>
	   <div class=sec_menu style="width:188;height:0;filter:alpha(Opacity=100);display:none;"  id=menu8> 
	   <form name="static" action="" method="post">
	     <table cellpadding=2 cellspacing="3" align="center" width="100%"  class="DetailTable" style="position:relative;top:10px;">
	       
	            <tr>
	              <td  align="center" class="loginFont" ><hrms:link href="/performance/totalrank/totalrank.do?b_query=link" target="il_body" onclick="turn();"><img src="/images/tool.gif" border=0></hrms:link></td>
	            </tr>
	            <tr>
	              <td  align="center" class="loginFont" >
	                <hrms:link href="/performance/totalrank/totalrank.do?b_query=link" target="il_body" ><font id="a001" class="menu_a" onclick="turn();">总分排名</font></hrms:link>
	              </td>
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


                                                                              