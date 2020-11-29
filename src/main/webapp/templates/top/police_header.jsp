<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hrms.hjsj.sys.Des"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    String prl=request.getProtocol();
    int idx=prl.indexOf("/");
    prl=prl.substring(0,idx);
    String url_p=prl+"://"+aurl+":"+port;
    String userName = null;
    String pwd=null;
    boolean bexchange=false;    
    int status=0;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock"); 
    boolean bflag=lockclient.isBtest();
    String verdesc="";
    if(bflag)
    {
    	verdesc=ResourceFactory.getProperty("label.sys.about.test");
    }     
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
<link rel="stylesheet" href="/css/css1.css" type="text/css">

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
 function SetIEOpt()
   {
      obj=document.getElementById('SetIE'); 
      if (obj != null)
      {
         obj.SetIEOptions('<%=url_p%>');      
      }     
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

  function submitWinopen()
  {
    logonForm.action="/templates/index/hrlogon4.do?logon.x=link";
    logonForm.target="_blank";
	logonForm.submit();	
	window.opener=null;//不会出现提示信息
   	parent.window.close();	
  } 
    
  function isclose()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/policelogon.jsp";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
		//window.opener=null;//不会出现提示信息
   		//parent.window.close();	
  	}
  }  
  function mustclose()
  {
   		var url = "/templates/index/policelogon.jsp";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
  } 
  function clearStatus()
  {
  	this.status="";
  }
  var menuN="";
</script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style type="text/css">
#top4 {
	background-image:url(../../images/b-bj.jpg);
	background-repeat: repeat-x;
	height: 114px;
	width: 100%;
	background-position:  top;
}
#top_left4 {
	float: left;
	height: 114px;
	width: 598px;
	background-image: url(../../images/b-left.jpg);
	background-repeat: no-repeat;
}
#top_right4 {
	background-image: url(../../images/b-right.jpg);
	background-repeat: no-repeat;
	float: right;
	height: 114px;
	width: 430px;
}
#nav4{
	position:absolute;top:35;
	position:absolute;left:800;
	height: 30px;
	float: right;
	font-size:12px;	
	width:100%;
	line-height: 30px;
}

#navm {
	position:absolute;top:65;
	position:absolute;left:120;
	height: 30px;
	float: right;
	font-size:12px;	
	width:100%;
	line-height: 30px;
}

#gundong4 {
	width: 330px;
	float: left;
	font-size:12px;
	padding-top: 8px;
	padding-bottom: 10px;
	height: 16px;
	padding-left: 337px;
	color: #000000;
}
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
<body style="margin:0 0 0 0" onbeforeunload="">
<form name="logonForm" method="post" action="/templates/index/hrlogon4.do">
<input type="hidden" name="username" value="<%=userName%>">
<input type="hidden" name="password" value="<%=pwd%>">
</form>
<div id="head-wrap">
<table width="100%" height="66" border="0" cellspacing="0" cellpadding="0"  id="top4">
<tr>
  <td width="26%" valign="top">
  <table width="598" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td id="top_left4">&nbsp;</td>
    </tr>
  </table>
  </td>
    <td width="74%">
    <table width="100%" border="0"  cellspacing="0" cellpadding="0" id="top_right4">
      <tr height="30">
        <td  valign="top">
           <table border="0" align="right" cellpadding="0" cellspacing="2">
            <tr>
			    <td width="40" style="color:black;valign:middle"><%=verdesc%></td> 
			    <td align="left">	
			            <hrms:link href="/selfservice/welcome/policecredit.do?br_query=link" target="i_body" ><img src="/images/home_1.gif" border=0 title="<bean:message key="label.banner.home"/>"></hrms:link>
		  			
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
    		  			<hrms:link href="javascript:SetIEOpt();"><img src="/images/set.gif" border=0 title="设置IE"></hrms:link>
    		  			<!-- 	
						<hrms:link href="../../help/ie_option.htm" target="_blank"><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			
    		  			<hrms:link href="javascript:winopenhelp();"  ><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			

						<hrms:link href="/templates/index/employLogon.jsp"  target="_blank" onclick="isclose();"><img src="/images/reenter.gif" border=0 title="<bean:message key="label.banner.relogin"/>"></hrms:link>			
						-->
						<hrms:priv func_id="3013,000103" module_id=""> 	
						  <input name="reenter" type="image" src="/images/reenter.gif" title="<bean:message key="label.banner.relogin"/>" onclick="isclose();">
						</hrms:priv> 						  
						<input name="b_exit" type="image" src="/images/exit.gif" title="退出" onclick="parent.window.close();">
			         </td>
			    <td>&nbsp;</td>
            </tr>
         </table>
        </td>
      </tr>
      <tr height="36">
        <td  valign="top">

		</td>
      </tr>
    </table></td>
  </tr>
</table>
</div>

 <table width="300" border="0" align="left" cellpadding="0" cellspacing="0" style="margin-right:10px;position:absolute;right:20px;top:35px">
          <tr>
            <td height="36" valign="top">
    		  <%if(SystemConfig.isScrollWelcome()){%>
				<MARQUEE id=m1 SCROLLAMOUNT="5" SCROLLDELAY="200" style="color:white">欢迎<%=buf.toString().trim()%>登录贵州银行人力资源系统</MARQUEE>
  			  <%}%>	  
        	</td>
          </tr>
 </table>
 <div id="navm">
 	 <table  border="0" align="left" cellpadding="0" cellspacing="0" style="flolat:left">
  	 <tr>		
     		      <td width="16"></td>
		     
    		    <td>&nbsp;</td>
  	  </tr>	  	 	
 	  </table>

  </div>  
<div id='axc' style='display:none'/>
</body>
 <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 <script type="text/javascript">
 function InitAx()
 {
     if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
           return;
 }
 InitAx();
 </script>