<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
      // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    int status=0;
    boolean bmanager=false;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	status=userView.getStatus();
	bmanager=userView.isManager();
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<style>
A:link {
	COLOR:black; TEXT-DECORATION: none;font-size: 12px	
}
A:visited {
	COLOR: black; TEXT-DECORATION: none;font-size: 12px
}
A:active {
	COLOR: black;TEXT-DECORATION: none;font-size: 12px
}
A:hover {
	COLOR: black; TEXT-DECORATION:none;font-size: 12px
}
</style>
<script language="JavaScript1.2">
var preId='';//一级菜单
var preTopId='';//按钮

function show(id)
{
   eval("window."+id+".style.backgroundImage='url(/images/top_title_a.gif)'");   
   eval("window."+id+"_menu.style.visibility='visible'"); 
   if(id!=preId&&preId!="")
   {
     eval("window."+preId+".style.backgroundImage='url(/images/top_title_v.gif)'");   
     eval("window."+preId+"_menu.style.visibility='hidden'");
   } 
    preId=id;	   
}

function changbutton(id) 
{
   eval("window."+id+".style.backgroundImage='url(/images/top_button_a.gif)'");  
   if(preTopId!="") 
   {      
      if(id!=preTopId) 
      {
         eval("window."+preTopId+".style.backgroundImage='url(/images/top_button_v.gif)'");      	
      }   
   }
   preTopId=id;
}

</script>

<body topmargin="0" bottommargin="0" style="margin:0 0 0 0">
<table  border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
   <tr>
	<td valign="top"> 
	  <table  border="0" align=left cellpadding="0" cellspacing="0" width="100%">
           <tr> 
             <td class="bgToptitleLeft" width="185"></td>
             <td class="bgToptitleRight"></td>
           </tr> 
             <tr align=right class="bgToptitle_b_h">
           <!--  <tr align=right class="bgToptitle">   
             <tr align=right class="bgToptitle_b_j"> -->
   	      <td valign="bottom" colspan="2">
          	 
	      </td>
	   </tr> 
          </table>
        </td>
   </tr>

   <tr class=bgTopmenu>
     <td valign="center">
	<div id="employee_menu" style="visibility: visible">
 	 <table  border="0"  align=right cellpadding="0" cellspacing="0"  width="100%">
  	 <tr>		
		<td width=180 >&nbsp;</td>
     		<td width="46"></td>	
		<td class=top_button_v width="68" id="hireselfinfo_menu"  ><A href="/hire/zp_persondb/personinfoenroll.do?b_update=link" target="i_body">个人信息</a></td>
    	  	<td class=top_button_v width="68" id="hire_pos_menu"   ><A href="/hire/zp_person/search_zp_position.do?b_query=link" target="i_body">招聘职位</a></td>
    		<td>&nbsp;</td>
  	  </tr>	  	 	
 	  </table>
	</div>	
     </td>
  </tr>
</table>
</body>