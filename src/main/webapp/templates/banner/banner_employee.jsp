<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hrms.hjsj.sys.Des"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String pwd=null;
    boolean bexchange=false;    
    int status=0;
    boolean bmanager=false;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	status=userView.getStatus();
	bmanager=userView.isManager();
    bexchange=userView.isBexchange();
    StringBuffer buf=new StringBuffer();
    if(SystemConfig.isScrollWelcome())
    {
    	String orgid=userView.getUserOrgId();
    	String deptid=userView.getUserDeptId();
    	String posid=userView.getUserPosId();
    	buf.append(AdminCode.getCodeName("UN",orgid));
    	buf.append(" ");
    	buf.append(AdminCode.getCodeName("UM",deptid));
    	buf.append(" ");
    	buf.append(AdminCode.getCodeName("@K",posid));
    	buf.append(" ");
    	buf.append(userView.getUserFullName());
    }
    if(status==4)
    {
    	userName=userView.getS_userName();
    	if(userView.isBEncryPwd())
    	{
    		Des des=new Des();
    		pwd=des.DecryPwdStr(userView.getS_pwd());
    	}
    	else    	
    		pwd=userView.getS_pwd();	
    }
    else
    {
    	userName=userView.getUserName();
    	if(userView.isBEncryPwd())
    	{
    		Des des=new Des();
    		pwd=des.DecryPwdStr(userView.getPassWord());
    	}
    	else       	
    		pwd=userView.getPassWord();
    }
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

   function winopen(userID,password)
   {
   	var url = "/templates/index/UserLogon.do?logon.x=link&username="+userID+"&password="+password;
	newwin=window.open(url,"_blank","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
	window.opener=null;//不会出现提示信息
   	parent.window.close();	
  }
  
  function isclose()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/employLogon.jsp";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
		//window.opener=null;//不会出现提示信息
   		//parent.window.close();	
  	}
  }
  
  function refresh()
  {
  	window.location.replace("banner_employee.jsp");
  }
  
  /*setInterval(refresh,20*60*1000);*/
  
</script>

<body topmargin="0" bottommargin="0" style="margin:0 0 0 0">
<table  border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="banner">
   <tr>
	<td valign="top"> 
	  <table  border="0" align=left cellpadding="0" cellspacing="0" width="100%">
           <tr> 
             <td class="bgToptitleLeft" width="185"></td>
             <td class="bgToptitleRight" valign="bottom">
           	  <table  border="0" align="right" cellpadding="0" cellspacing="0" width="280">
                 	 <tr align="left">
          	     	 	<td valign="center">
          	     	 	</td>
          	     	 </tr>
                     <tr align="left">
	          	      <td nowrap>
					<hrms:link href="/selfservice/welcome/welcome.do?b_query=link" target="i_body" ><img src="/images/home_1.gif" border=0 title="<bean:message key="label.banner.home"/>"></hrms:link>
		  			<% if(status==4){%>						
		        		<hrms:priv func_id="0B4" module_id=""> 
		        			<hrms:priv func_id="0B401" module_id=""> 
		        		     <hrms:link href="/templates/menu/kq_employee_menu.do?b_query=link&module=5&sign=in" target="i_body" ><img src="/images/sign_in.gif" border=0 title="网上签到"></hrms:link>
		        		    </hrms:priv> 
		        		    <hrms:priv func_id="0B405" module_id=""> 
		        		     <hrms:link href="/templates/menu/kq_employee_menu.do?b_query=link&module=5&sign=out" target="i_body" ><img src="/images/sign_out.gif" border=0 title="网上签退"></hrms:link>
		        		   </hrms:priv> 
		        		</hrms:priv>
		        	<% }%>	
		        		<hrms:priv func_id="000105,3015" module_id=""> 						
							<hrms:link href="/system/sms/send_sms.do" target="i_body" ><img src="/images/sms.gif" border=0 title="<bean:message key="label.sms.send"/>"></hrms:link>
    		  			</hrms:priv>  		        	
    		  			<hrms:priv func_id="000104,3014" module_id=""> 						
							<hrms:link href="/selfservice/app_news/appmessage.do?b_apptag=link" target="i_body" ><img src="/images/msg.gif" border=0 title="<bean:message key="tab.label.mymsg"/>"></hrms:link>
    		  			</hrms:priv>
		        		<hrms:priv func_id="000101,3010" module_id=""> 						
							<hrms:link href="/system/security/resetup_password.do" target="i_body" ><img src="/images/keylock.gif" border=0 title="<bean:message key="label.mail.password"/>"></hrms:link>
    		  			</hrms:priv> 							

		        		<hrms:priv func_id="3011,000102" module_id=""> 						
							<hrms:link href="/system/security/about_hrp.do?b_query=link&status=2" target="i_body" ><img src="/images/version.gif" border=0 title="<bean:message key="label.banner.product"/>"></hrms:link>
    		  			</hrms:priv> 	
						<hrms:link href="../../help/ie_option.htm" target="_blank"><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			
    		  			
    	 				<!-- 
						<hrms:link href="javascript:winopenhelp();"  ><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			

						<hrms:link href="/templates/index/employLogon.jsp"  target="_blank" onclick="isclose();"><img src="/images/reenter.gif" border=0 title="<bean:message key="label.banner.relogin"/>"></hrms:link>			
						-->
						<hrms:priv func_id="3013,000103" module_id=""> 	
						  <input name="reenter" type="image" src="/images/reenter.gif" title="<bean:message key="label.banner.relogin"/>" onclick="isclose();">
						</hrms:priv> 						  
						<input name="b_exit" type="image" src="/images/exit.gif" title="退出" onclick="parent.window.close();">
			          </td>
          	   		</tr>           	      
              </table> 
             </td>
           </tr> 
	   <% if(status==0){%>
            <tr align=right class="bgToptitle">
           <%}
            else 
            {%>
            	<%if(bmanager){%>
                  <tr align=right class="bgToptitle">   
                <%}else {%>  
                  <tr align=right class="bgToptitle"> 
                <%}%>                               	  
           <%}%>  
	      <td valign="bottom" colspan="2">
	      <%if(SystemConfig.isScrollWelcome()){%>
<MARQUEE id=m1 direction="left" BEHAVIOR="SCROLL" SCROLLAMOUNT="5" SCROLLDELAY="200" width="200" height=20>欢迎(<%=buf.toString().trim()%>)登录贵州银行人力资源系统</MARQUEE>
	      <%}%>
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
     		<td width="16"></td>
		<hrms:priv func_id="11" module_id="0">     		
    			<td class=top_button_v width="68" id="myself_servie"  onclick="javascript:changbutton('myself_servie')" ><A href="/templates/menu/selfservice_menu.do?b_query=link&module=0" target="i_body">HR服务台</a></td>
    		</hrms:priv>    			
		<hrms:priv func_id="" module_id="0">  
		  <% if(status==4){%>	   
		    <hrms:priv func_id="01" module_id="0">   		   		   	
    			<td class=top_button_v width="68" id="myself_inform"  onclick="javascript:changbutton('myself_inform')" ><A href="/templates/menu/inform_menu.do?b_query=link&module=0" target="i_body">我的信息</a></td>
    		    </hrms:priv>
                  <%}%>
    		</hrms:priv>
		<hrms:priv func_id="03" module_id="1">     		
    			<td class=top_button_v width="68" id="employee_inform"  onclick="javascript:changbutton('employee_inform')"><A href="/templates/menu/employee_menu.do?b_query=link&module=1"  target="i_body">员工信息</a></td>
    	</hrms:priv>
		<hrms:priv func_id="05" module_id="1">     		
    			<td class=top_button_v width="68" id="org_inform"  onclick="javascript:changbutton('org_inform')"><A href="/templates/menu/organization_menu.do?b_query=link&module=1"  target="i_body">机构信息</a></td>
    	</hrms:priv>    		
		<hrms:priv func_id="04" module_id="1">     			            			
	    		<td class=top_button_v width="68" id="static_menu"  onclick="javascript:changbutton('static_menu')"><A href="/templates/menu/all_static_menu.do?b_query=link&module=1"  target="i_body">统计分析</a></td>
	    	</hrms:priv>
		<hrms:priv func_id="290" module_id="13">                   
                  <td class=top_button_v width="68" id="reportCenter" onclick="javascript:changbutton('reportCenter')"><A href="/templates/menu/tjb_m_menu.do?b_query=link&module=13" target='i_body'>报表管理</A></td> 
    	</hrms:priv>  

    		  <hrms:priv func_id="310" module_id="7">   
    		   <td class=top_button_v width="68" id="hire2" onclick="javascript:changbutton('hire2')"><A href="/templates/menu/hire_m_menu2.do?b_query=link&module=7" target='i_body'>招聘管理</A></td>    
    		  </hrms:priv>     		    	

	        <% if(status==4){%>	    	
		  <hrms:priv func_id="09" module_id="2">
    			<td class=top_button_v width="68" id="train_button"  onclick="javascript:changbutton('train_button')" ><A href="/templates/menu/train_menu.do?b_query=link&module=2" target="i_body">培训自助</a></td>
    		  </hrms:priv>
		  <hrms:priv func_id="06" module_id="3">
    			<td class=top_button_v width="68" id="performance_menu"  onclick="javascript:changbutton('performance_menu')" ><A href="/templates/menu/performance_menu.do?b_query=link&module=3" target="i_body">绩效考评</a></td>
    			
    		  </hrms:priv>
  		  
		  <hrms:priv func_id="0A" module_id="4">
    			<td class=top_button_v width="68" id="zp_menu"  onclick="javascript:changbutton('zp_menu')" ><A href="/templates/menu/hire_employee_menu.do?b_query=link&module=4" target="i_body">招聘自助</a></td>
    		  </hrms:priv>    
		  <hrms:priv func_id="0B" module_id="5">
    			<td class=top_button_v width="68" id="kq_menu"  onclick="javascript:changbutton('kq_menu')" ><A href="/templates/menu/kq_employee_menu.do?b_query=link&module=5" target="i_body">考勤自助</a></td>
    		  </hrms:priv>
  		         		  
                  <%}%>  
		  <hrms:priv func_id="0D" module_id="3">
    			<td class=top_button_v width="68" id="review_menu"  onclick="javascript:changbutton('review_menu')" ><A href="/templates/menu/leader_review_menu.do?b_query=link&module=3" target="i_body">干部考察</a></td>
    			
    		  </hrms:priv>   
 		                   
		  <hrms:priv func_id="0C" module_id="22">
    			<td class=top_button_v width="68" id="ekq_menu"  onclick="javascript:changbutton('ekq_menu')" ><A href="/templates/menu/kq_dept_menu.do?b_query=link&module=22" target="i_body">部门考勤</a></td>
    	  </hrms:priv>                       		
		  <hrms:priv func_id="0E">
    			<td class=top_button_v width="68" id="elearning_menu"  onclick="javascript:changbutton('elearning_menu')" ><A href="/templates/menu/elearning_menu.do?b_query=link&module=22" target="i_body">在线培训</a></td>
    	  </hrms:priv>
		  <hrms:priv func_id="0F" >
    			<td class=top_button_v width="68" id="jp_menu"  onclick="javascript:changbutton('jp_menu')" ><A href="/templates/menu/jp_menu.do?b_query=link" target="i_body">竞聘上岗</a></td>
    	  </hrms:priv>        	    
		<hrms:priv func_id="08,07" >      		  			
    			<td class=top_button_v width="68" id="systemoption"  onclick="javascript:changbutton('systemoption')"><A href="/templates/menu/securitymenu.do?b_query=link"  target="i_body">系统管理</a></td>
    		</hrms:priv>
		  <hrms:priv func_id="2" module_id="0">     		
				<%
				 if(bexchange)
				 { 
				%>    		
    			<td class=top_button_v width="68" id="bussop"><A href="javascript:winopen('<%=userName%>','<%=pwd%>');"><bean:message key="label.hrms.os"/></a></td>
				<%}%>
    		</hrms:priv>
	 		   			
    		<td>&nbsp;</td>
  	  </tr>	  	 	
 	  </table>
	</div>	
     </td>
  </tr>
</table>

</body>