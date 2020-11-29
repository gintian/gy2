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
<%@ page import="java.util.Date" %>
<%@ page import="com.hrms.frame.dao.utility.DateUtils" %>
<SCRIPT language="javascript" type="text/javascript" >
var menuURL="";
</SCRIPT>
<%
 
 String severDate=DateUtils.format(new Date(),"yyyy.MM.dd");
 String severTime=DateUtils.format(new Date(),"HH:mm:ss");
 %>
<%
	
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2; 
%>
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
    String sys_name=SystemConfig.getPropertyValue("sys_name");
    String view_time=SystemConfig.getPropertyValue("banner_viewTime");

    if(sys_name.length()==0)
    {
    	sys_name="贵州银行人力资源系统";
    }
    String value=SystemConfig.getPropertyValue("display_employee_info");
    boolean bvalue=false;
    if(value.length()==0||value.equalsIgnoreCase("true"))
    {
    	bvalue=true;
    }
    if(SystemConfig.isScrollWelcome()&&bvalue)
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
    boolean bself=true;
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
    	String a0100=userView.getA0100();
    	if(a0100==null||a0100.length()==0)
    	{
    		bself=false;
    	}
    }
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>

<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>

<script language="javascript" src="/js/constant.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=<%=flag%>;
	
</script>
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
   function winopen()
   {
      var url ="/templates/index/hrlogon4.do?logon.x=link";	
	  window.opener=null;//不会出现提示信息	
      parent.window.close();	
      newwin=window.open(url,"_blank","height="+screen.availWidth+",width="+screen.availWidth+",toolbar=no,top=0,left=0,location=no,directories=yes,status=no,menubar=no,scrollbars=yes,resizable=no","true");
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
    
  function islogout()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/emlogon4.jsp";
   		url="/servler/sys/logout?flag=25";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
		//window.opener=null;//不会出现提示信息
   		//parent.window.close();	
  	}
  }  
  function isclose()
  {
  	if(confirm("确定要退出吗？"))
  	{
   		var url = "/templates/close.jsp";
   		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
   		//parent.window.close();	
  	}
  }
  function mustclose()
  {
   		var url = "/templates/close.jsp";
   		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
  }
  function SetIEOpt()
   {
      var obj=document.getElementById('SetIE'); 
      if (obj != null)
      {
         obj.SetIEOptions('<%=url_p%>');      
      }     
   }
  function clearStatus()
  {
  	this.status="";
  }   
</script>
 <SCRIPT language="javascript" type="text/javascript" >
Date.prototype.todate = function(datetime) 
{

	return this;
}
function StringToDate(DateStr,TimeStr) 
{ 
　　
　　var arys= DateStr.split('.');
   var arts= TimeStr.split(':');
　  var myDate = new Date(arys[0],--arys[1],arys[2],arts[0],arts[1],arts[2]); 
   return myDate;
} 

function reloop(datestr,timestr){     
    var time =new StringToDate(datestr,timestr);
    if(isNaN(time))
      return;    
	var datetime =time;
	datetime.setUTCMilliseconds(time.getUTCMilliseconds()+1000);
	var month = time.getMonth()+1;
	var date = time.getDate();
	var year = time.getYear();
	var hour = time.getHours(); 
	var minute = time.getMinutes();
	var second = time.getSeconds();
					
	var day = time.getDay();
	if (minute < 10) 
	   minute="0"+minute;
	if (second < 10) 
	   second="0"+second; 
	var apm="AM"; 
	if (hour>12) 
	{
	    hour=hour-12;
	    apm="PM" ;
	}
	var weekday = 0;
	switch(time.getDay())
	{
		case 0:
		weekday = "星期日";
		break;
		case 1:
		weekday = "星期一";
		break;
		case 2:
		weekday = "星期二";
		break;
		case 3:
		weekday = "星期三";
		break;
		case 4:
		weekday = "星期四";
		break;
		case 5:
		weekday = "星期五";
		break;
		case 6:
		weekday = "星期六";
		break;
	}	
	datestr=datetime.getYear()+"."+(datetime.getMonth()+1)+"."+datetime.getDate();	
	timestr=datetime.getHours()+":"+datetime.getMinutes()+":"+datetime.getSeconds();
	document.getElementById("t").value=year+"年"+month+"月"+date+"日"+" "+hour+":"+minute+":"+second+apm;
	//setTimeout("reloop(\""+datetime+"\")",1000);
	setTimeout("reloop(\""+datestr+"\",\""+timestr+"\")",1000);
 }
 //签到											
	function netsingin(singin_flag)
	{
		 var ip_addr="";
		 ip_addr=getLocalIPAddressf();
    	 var hashvo=new ParameterSet();	
    	 hashvo.setValue("singin_flag",singin_flag);	
    	 hashvo.setValue("ip_addr",ip_addr);
        var request=new Request({method:'post',asynchronous:false,onSuccess:showReturn,functionId:'15502110200'},hashvo);
	}
	function showReturn(outparamters)
   {
      var mess=outparamters.getValue("mess");
      //var signflag=outparamters.getValue("signflag");
      alert(mess);
      if(mess.indexOf("成功")!=-1){
      	logonForm.action="/templates/menu/kq_employee_menu.do?b_search=link&module=5&sign=inio";
      	logonForm.target="i_body";
      	logonForm.submit();
      }
   }
        /**取得本地机器ip地址*/
function getLocalIPAddressf()
{
    var obj = null;
    var rslt = "";   
    try
    {
        obj=document.getElementById('SetIE');
        	rslt = obj.GetIP();
        obj = null;
    }
    catch(e)
    {
    	//异常发生
    }
    return rslt;
}	
function change_agent(obj)
{
     document.getElementById("agentId").value=obj.value;
     sysForm.action="/selfservice/selfinfo/agent/agent4.do?b_agent=link"     
     sysForm.target="_parent";
     sysForm.submit();
}	
function index()
{
   sysForm.action="/selfservice/welcome/welcome.do?b_query=link";
   sysForm.target="i_body"
   sysForm.submit();
}							
</SCRIPT>
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
	width: 100%;
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
	position:absolute;left:10;
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
INPUT {
	font-size: 12px;
	border-style:none ;
    background-color:transparent;
}
#top_left4_1 {     
  float:right; 
}
</style>
<body style="margin:0 0 0 0" onbeforeunload="">
<form name="sysForm" action="" method=post>
<input type="hidden" name="agentId">
</form>
<form name="logonForm" method="post" action="/templates/index/hrlogon4.do">
<input type="hidden" name="username" value="<%=userName%>">
<input type="hidden" name="password" value="<%=pwd%>">
</form>
<div id="head-wrap">
<table width="100%" height="66" border="0" cellspacing="0" cellpadding="0"  id="top4">
<tr>
  <td width="100%" valign="top">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td id="top_left4" valign="top">
         <div id="top_left4_1">  
    	  
		  <% if(bself){%>
    	    <hrms:agent></hrms:agent>
    	   <%} %>
		  
    	 </div>
      </td>
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
			            <hrms:link href="javascript:index();"><img src="/images/home_1.gif" border="0" title='<bean:message key="label.banner.home"/>'></hrms:link>
		  			  <% if(bself){%>						
		        		<hrms:priv func_id="0B4" module_id=""> 
		        			<hrms:priv func_id="0B401" module_id=""> 
		        		     <!--<hrms:link href="/templates/menu/kq_employee_menu.do?b_search=link&module=5&sign=in" target="i_body" ><img src="/images/sign_in.gif" border=0 title="网上签到"></hrms:link>-->
		        		     <hrms:link href="javascript:netsingin('0');"><img src="/images/sign_in.gif" border=0 title="网上签到"></hrms:link> 
		        		     </hrms:priv> 
		        		     <hrms:priv func_id="0B405" module_id=""> 
		        		     <!--<hrms:link href="/templates/menu/kq_employee_menu.do?b_search=link&module=5&sign=out" target="i_body" ><img src="/images/sign_out.gif" border=0 title="网上签退"></hrms:link>-->
		        		     <hrms:link href="javascript:netsingin('1');"><img src="/images/sign_out.gif" border=0 title="网上签退"></hrms:link> 
		        		   </hrms:priv> 
		        		</hrms:priv>
		        	    <hrms:priv func_id="000106,3016" module_id=""> 
		        		 <hrms:link href="/selfservice/selfinfo/agent/agentinfo.do?b_search=link" target="il_body"><img src="/images/agent.gif" border=0 title="代理"></hrms:link> 
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
    		  			  <hrms:priv func_id="000107,3017" module_id="">	
    		  			<hrms:link href="javascript:SetIEOpt();"><img src="/images/set.gif" border=0 title="设置IE"></hrms:link>
    		  			</hrms:priv>
    		  			<!-- 
						<hrms:link href="../../help/ie_option.htm" target="_blank"><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			
    		  			
    	 				
						<hrms:link href="javascript:winopenhelp();"  ><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			

						<hrms:link href="/templates/index/employLogon.jsp"  target="_blank" onclick="isclose();"><img src="/images/reenter.gif" border=0 title="<bean:message key="label.banner.relogin"/>"></hrms:link>			
						-->
						<hrms:priv func_id="3013,000103" module_id=""> 	
						  <input name="reenter" type="image" src="/images/reenter.gif" title="<bean:message key="label.banner.relogin"/>" onclick="islogout();">
						</hrms:priv> 						  
						<input name="b_exit" type="image" src="/images/exit.gif" title="退出" onclick="isclose();">
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
				<MARQUEE id=m1 SCROLLAMOUNT="5" SCROLLDELAY="200" style="color:white">欢迎<%=buf.toString().trim()%>登录<%=sys_name%></MARQUEE>	      
  			  <%}%>	  
        	</td>
          </tr>
 </table>
 <div id="navm">
 	 <table  border="0" align="left" cellpadding="0" cellspacing="0" style="flolat:left">
  	 <tr>		
     		 <td width="27" align="left">
     		 <%if(view_time!=null&&view_time.equals("true")){ %>
     		     <INPUT id="t" name="time" type="text" size="27" >
     		 <%}else{ %>
     		    <INPUT id="t2" name="time" type="text" size="20" >
     		 <%} %>
     		 </td>
		     <hrms:priv func_id="11" module_id="0">     		
    			<td class=top_button_v width="68" id="myself_servie" onclick="javascript:changbutton('myself_servie')"><A href="/templates/menu/selfservice_menu.do?b_query=link&module=0" target="i_body">HR服务台</a></td>
    		     <script language="JavaScript">
                    if(menuURL=="")
                      menuURL="#";
                  </script>
    		</hrms:priv>    			
		  <hrms:priv func_id="" module_id="0">  
		    <% if(bself){%>	   
		    <hrms:priv func_id="01" module_id="0">   		   		   	
    			<td class=top_button_v width="68" id="myself_inform"  onclick="javascript:changbutton('myself_inform')" ><A href="/templates/menu/inform_menu.do?b_query=link&module=0" target="i_body">我的信息</a></td>
    		     <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/inform_menu.do?b_query=link&module=0";
                  </script>
    		</hrms:priv>
            <%}%>
    		</hrms:priv>
		<hrms:priv func_id="03" module_id="1">     		
    			<td class=top_button_v width="68" id="employee_inform"  onclick="javascript:changbutton('employee_inform')"><A href="/templates/menu/employee_menu.do?b_query=link&module=1"  target="i_body">员工信息</a></td>
    	        <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/employee_menu.do?b_query=link&module=1";
                  </script>
    	</hrms:priv>
		<hrms:priv func_id="05" module_id="1">     		
    			<td class=top_button_v width="68" id="org_inform"  onclick="javascript:changbutton('org_inform')"><A href="/templates/menu/organization_menu.do?b_query=link&module=1"  target="i_body">机构信息</a></td>
    	        <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/organization_menu.do?b_query=link&module=1";
                  </script>
    	</hrms:priv>    		
		<hrms:priv func_id="04" module_id="1">     			            			
	    		<td class=top_button_v width="68" id="static_menu"  onclick="javascript:changbutton('static_menu')"><A href="/templates/menu/all_static_menu.do?b_query=link&module=1"  target="i_body">统计分析</a></td>
	            <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/all_static_menu.do?b_query=link&module=";
                  </script>
	    </hrms:priv>   	
		<hrms:priv func_id="290" module_id="13">                   
                  <td class=top_button_v width="68" id="reportCenter" onclick="javascript:changbutton('reportCenter')"><A href="/templates/menu/tjb_m_menu.do?b_query=link&module=13" target='i_body'>报表管理</A></td> 
    	          <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/tjb_m_menu.do?b_query=link&module=13";
                  </script>
    	</hrms:priv>  
        <hrms:priv func_id="310" module_id="7">   
    		   <td class=top_button_v width="68" id="hire2" onclick="javascript:changbutton('hire2')"><A href="/templates/menu/hire_m_menu2.do?b_query=link&module=7" target='i_body'>招聘管理</A></td>    
               <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/hire_m_menu2.do?b_query=link&module=7";
               </script>
        </hrms:priv>     		    	
    	<hrms:priv func_id="323" module_id="21">                    
                  <td class=top_button_v width="68" id="train" onclick="javascript:changbutton('train')"><A href="/templates/menu/train_m_menu.do?b_query=link&module=21" target='i_body'>培训管理</A></td>                           
    	       <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/train_m_menu.do?b_query=link&module=21";
               </script>
    	</hrms:priv>	 
   	
		  <hrms:priv func_id="09" module_id="2">
    			<td class=top_button_v width="68" id="train_button"  onclick="javascript:changbutton('train_button')" ><A href="/templates/menu/train_menu.do?b_query=link&module=2" target="i_body">培训自助</a></td>
    	        <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/train_menu.do?b_query=link&module=2";
                 </script>
    	  </hrms:priv>


		  <hrms:priv func_id="06" module_id="3">
    			<td class=top_button_v width="68" id="performance_menu"  onclick="javascript:changbutton('performance_menu')" ><A href="/templates/menu/performance_menu.do?b_query=link&module=3" target="i_body">绩效考评</a></td>
    			<script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/performance_menu.do?b_query=link&module=3";
                </script>
    	 </hrms:priv>


		  <hrms:priv func_id="0A" module_id="4">
    			<td class=top_button_v width="68" id="zp_menu"  onclick="javascript:changbutton('zp_menu')" ><A href="/templates/menu/hire_employee_menu.do?b_query=link&module=4" target="i_body">招聘自助</a></td>
    	        <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/hire_employee_menu.do?b_query=link&module=4";
                  </script>
    	  </hrms:priv>    
		  <hrms:priv func_id="0B" module_id="5">
    			<td class=top_button_v width="68" id="kq_menu"  onclick="javascript:changbutton('kq_menu')" ><A href="/templates/menu/kq_employee_menu.do?b_query=link&module=5" target="i_body">考勤自助</a></td>
    	        <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/kq_employee_menu.do?b_query=link&module=5";
                </script>
    	  </hrms:priv>
  		  <hrms:priv func_id="0D" module_id="3">
    			<td class=top_button_v width="68" id="review_menu"  onclick="javascript:changbutton('review_menu')" ><A href="/templates/menu/leader_review_menu.do?b_query=link&module=3" target="i_body">干部考察</a></td>
    			<script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/leader_review_menu.do?b_query=link&module=3";
                </script>
    	  </hrms:priv>   
 		                   
		  <hrms:priv func_id="0C" module_id="22">
    			<td class=top_button_v width="68" id="ekq_menu"  onclick="javascript:changbutton('ekq_menu')" ><A href="/templates/menu/kq_dept_menu.do?b_query=link&module=22" target="i_body">部门考勤</a></td>
    	        <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/kq_dept_menu.do?b_query=link&module=22";
                </script>
    	  </hrms:priv>                       		
		  <hrms:priv func_id="0E">
    			<td class=top_button_v width="68" id="elearning_menu"  onclick="javascript:changbutton('elearning_menu')" ><A href="/templates/menu/elearning_menu.do?b_query=link&module=22" target="i_body">在线培训</a></td>
    	        <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/elearning_menu.do?b_query=link&module=22";
                  </script>
    	  </hrms:priv>
		  <hrms:priv func_id="0F" >
    			<td class=top_button_v width="68" id="jp_menu"  onclick="javascript:changbutton('jp_menu')" ><A href="/templates/menu/jp_menu.do?b_query=link" target="i_body">竞聘上岗</a></td>
    	        <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/jp_menu.do?b_query=link";
                  </script>
    	  </hrms:priv>        	    
		  <hrms:priv func_id="08,07" >      		  			
    	  		<td class=top_button_v width="68" id="systemoption"  onclick="javascript:changbutton('systemoption')"><A href="/templates/menu/securitymenu.do?b_query=link"  target="i_body">系统管理</a></td>
    	        <script language="JavaScript">
    		        if(menuURL=="")
                      menuURL="/templates/menu/securitymenu.do?b_query=link";
                 </script>
    	  </hrms:priv>
		  <hrms:priv func_id="2" module_id="0,1">     		
				<%
				 if(bexchange)
				 { 
				%>    		
    			<td class=top_button_v width="68" id="bussop"><A href="javascript:submitWinopen();"><bean:message key="label.hrms.os"/></a></td>
				<%}%>
    		</hrms:priv>
    		    <td>&nbsp;</td>
  	  </tr>	  	 	
 	  </table>

  </div>
  <hrms:priv func_id="000107,3017,0B4,0B401,0B405" module_id=""> 
 <div id='axc' style='display:none'/>
 
 <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 <script type="text/javascript">
 function InitAx()
 {
     if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
           return;
 }
 InitAx();
 </script>
</hrms:priv>
</body>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
	//__t.path="/system/gcodeselect.jsp";    
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>
<script language="JavaScript">
<%if(view_time!=null&&view_time.equals("true")){ 

%>
//reloop('<%=new Date().toGMTString() %>')
reloop('<%=severDate%>','<%=severTime%>');
<%}%>
function clickMenu(target_url)
{
  if(target_url!=""&&target_url!="#")
     window.open(target_url,"i_body"); 
}
clickMenu(menuURL);
</script>