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
       
    String css_url="/css/css1.css";
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    bexchange=userView.isBexchange();
	status=userView.getStatus(); 
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
    if(status==0)
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
<link rel="stylesheet" href="/css/css1.css" type="text/css">
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

<script language="JavaScript">
var preId="";//一级菜单
var preTopId="";//按钮

function show(id)
{
   eval("window."+id+".style.backgroundImage='url(/images/top_title_a.gif)'");   
   if(id!=preId&&preId!="")
   {
     eval("window."+preId+".style.backgroundImage='url(/images/top_title_v.gif)'");   
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
   //
}
	
   
   function winopen(userID,password)
   {
   	var url = "/templates/index/employLogon.do?logon.x=link&username="+userID+"&password="+password;
	newwin=window.open(url,"_blank","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
	window.opener=null;//不会出现提示信息
   	parent.window.close();	
  }

   function winopenhelp()
   {
   	var url = "/help/hrphelp.do?b_search=link";
	newwin=window.open(url,"_blank","height=500,width=700,toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
  }
  function download_cs()
  {
  	 parent.location.href='/sys/downjnlp?app=1&ctrl=-1';
  }
  
  function isclose()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/UserLogon.jsp";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
		//window.opener=null;//不会出现提示信息
   		//parent.window.close();	
  	}
  }  
  
  
  function clearStatus()
  {
  	this.status="";
  }
</script>

<body style="margin:0 0 0 0"  onclick="clearStatus()">
<table  border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="hr5_header">
  <tr>
    <td width="521" rowspan="2" background="/images/logo.jpg"></td>
    <td>
          	  <table  border="0" align="right" cellpadding="0" cellspacing="0" width="400">
                 	 <tr align="left">
          	     	 	<td valign="center">
          	     	 	</td>
          	     	 </tr>
                     <tr align="left">
	          	      <td>
						<hrms:link href="/templates/index/portal.do?b_query=link" target="il_body" ><img src="/images/home_1.gif" border=0 title="<bean:message key="label.banner.home"/>"></hrms:link>
		        		<hrms:priv func_id="3012" module_id=""> 						
							<hrms:link href="/system/options/portaltailor.do?b_search=link" target="il_body" ><img src="/images/desktop.gif" border=0 title="<bean:message key="label.portal.options"/>"></hrms:link>			
    		  			</hrms:priv> 
		        		<hrms:priv func_id="000105,3015" module_id=""> 						
							<hrms:link href="/system/sms/send_sms.do" target="il_body" ><img src="/images/sms.gif" border=0 title="<bean:message key="label.sms.send"/>"></hrms:link>
    		  			</hrms:priv>     		  										
    		  			<hrms:priv func_id="000104,3014" module_id=""> 						
							<hrms:link href="/selfservice/app_news/appmessage.do?b_apptag=link" target="il_body" ><img src="/images/msg.gif" border=0 title="<bean:message key="tab.label.mymsg"/>"></hrms:link>
    		  			</hrms:priv>	
		        		<hrms:priv func_id="000101,3010" module_id=""> 						
							<hrms:link href="/system/security/resetup_password.do" target="il_body" ><img src="/images/keylock.gif" border=0 title="<bean:message key="label.mail.password"/>"></hrms:link>
    		  			</hrms:priv> 		
				
		        		<hrms:priv func_id="3011,000102" module_id=""> 						
							<hrms:link href="/system/security/about_hrp.do?b_query=link&status=1" target="il_body" ><img src="/images/version.gif" border=0 title="<bean:message key="label.banner.product"/>"></hrms:link>
    		  			</hrms:priv> 	
						<hrms:link href="../../help/ie_option.htm" target="_blank"><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			
    	 				<!-- 
						<hrms:link href="javascript:winopenhelp();"  ><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			
						<hrms:link href="/templates/index/UserLogon.jsp"  target="_blank" onclick="parent.window.close();"><img src="/images/reenter.gif" border=0 title="<bean:message key="label.banner.relogin"/>"></hrms:link>			
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
  <tr>
    <td align="right">
	      <%if(SystemConfig.isScrollWelcome()){%>
<MARQUEE id=m1 direction="left" BEHAVIOR="SCROLL" SCROLLAMOUNT="5" SCROLLDELAY="200" width="300" height=20>欢迎(<%=buf.toString().trim()%>)登录贵州银行人力资源系统</MARQUEE>
	      <%}%>	     
    </td>
  </tr>   	

</table>
</body>