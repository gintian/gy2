<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hrms.hjsj.sys.Des"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation"%>
<%@ page import="java.util.Date" %>
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
    String css_url="/css/css1.css";
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    if(userView==null)
      System.out.println("null");
    bexchange=userView.isBexchange();
	status=userView.getStatus(); 
    StringBuffer buf=new StringBuffer();
    String sys_name=SystemConfig.getPropertyValue("sys_name");
    if(sys_name.length()==0)
    {
    	sys_name="贵州银行人力资源系统";
    }
    String value=SystemConfig.getPropertyValue("display_employee_info");
    String view_time=SystemConfig.getPropertyValue("banner_viewTime");
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
    String isopen_1="";
    String isopen_2="";
    try
    {
         SubsysOperation subsysOperation=new SubsysOperation();
	     HashMap map = subsysOperation.getMap();
	     isopen_1=(String)map.get("37");//37是人事异动
	     isopen_2=(String)map.get("34");//34是薪资管理  ,38劳动合同，39保险福利
    }catch(Exception e)
    {
    
    }
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">

<script language="JavaScript">

var preId="";//一级菜单
var preTopId="";//按钮
var autologon="0";
<%if(request.getParameter("autologon")!=null&&request.getParameter("autologon").equalsIgnoreCase("true")){%>
    autologon="1";
 <%}%>
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
      var url ="/templates/index/emlogon4.do?logon.x=link";	
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
  
  function islogout()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/hrlogon4.jsp";
   		url="/servler/sys/logout?flag=21";
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
  function submitWinopen()
  {
    logonForm.action="/templates/index/emlogon4.do?logon.x=link";
    logonForm.target="_blank";
	logonForm.submit();	
	window.opener=null;//不会出现提示信息
   	parent.window.close();	
  }  
   function SetIEOpt()
   {
      obj=document.getElementById('SetIE'); 
      if (obj != null)
      {
         obj.SetIEOptions('<%=url_p%>');      
      }     
   }
  function clearStatus()
  {
  	this.status="";
  }
  var menuN="";
</script>
<SCRIPT language="javascript" type="text/javascript" >
function reloop(datetime){   
	var time = new Date(datetime);	
	datetime =time;
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
	document.getElementById("t").value=year+"年"+month+"月"+date+"日"+" "+hour+":"+minute+":"+second+apm;
	setTimeout("reloop(\""+datetime+"\")",1000);
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
	width: 451px;
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
</style>
<body style="margin:0 0 0 0" onclick="clearStatus()" onbeforeunload="">
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
      <td id="top_left4">&nbsp;</td>
    </tr>
  </table>
  </td>
    <td width="74%">
    <table width="100%" border="0"  cellspacing="0" cellpadding="0" id="top_right4">
      <tr height="30">
        <td  valign="top">
           <table border="0" align="left" cellpadding="0" cellspacing="2">
            <tr>
			    <td width="40" style="color:black;valign:middle"><%=verdesc%></td> 
			    <td align="left">	
			            <hrms:priv func_id="260" module_id="11">  		    
						   <hrms:link href="/templates/menu/employ_m_menu4.do?b_query=link&module=11" target="i_body" ><img src="/images/home_1.gif" border=0 title="<bean:message key="label.banner.home"/>"></hrms:link>
		        		</hrms:priv>
		        		<hrms:priv func_id="3012" module_id=""> 						
							<hrms:link href="/system/options/portaltailor.do?b_search=link" target="i_body" ><img src="/images/desktop.gif" border=0 title="<bean:message key="label.portal.options"/>"></hrms:link>			
    		  			</hrms:priv> 
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
							<hrms:link href="/system/security/about_hrp.do?b_query=link&status=1" target="i_body" ><img src="/images/version.gif" border=0 title="<bean:message key="label.banner.product"/>"></hrms:link>
    		  			</hrms:priv> 
    		  			<hrms:priv func_id="000107,3017" module_id=""> 	
    		  			<hrms:link href="javascript:SetIEOpt();"><img src="/images/set.gif" border=0 title="设置IE"></hrms:link>
    		  			</hrms:priv>
    		  			<!-- 
						<hrms:link href="../../help/ie_option.htm" target="_blank"><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			
    	 				
						<hrms:link href="javascript:winopenhelp();"  ><img src="/images/help.gif" border=0 title="<bean:message key="label.sys.help"/>"></hrms:link>			
						<hrms:link href="/templates/index/UserLogon.jsp"  target="_blank" onclick="parent.window.close();"><img src="/images/reenter.gif" border=0 title="<bean:message key="label.banner.relogin"/>"></hrms:link>			
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
     		  <td width="16">
     		     <%if(view_time!=null&&view_time.equals("true")){ %>
     		        <INPUT id="t" name="time" type="text" size="27" >
     		     <%}else{ %>
     		        <INPUT id="t2" name="time" type="text" size="20" >
     		     <%} %>
     		  </td>
		      <hrms:priv func_id="230,231,2310,23011,23060,23051,23052,25011,231101,23061,23064,23050,23057,23056,23058,25050,23062,25062" module_id="11">     		      
                  <td class=top_button_v width="68" id="organization"  onclick="javascript:changbutton('organization')"><A href="/templates/menu/org_m4_menu.do?b_query=link&module=11" target='i_body'>组织机构</A></td>
                  <script language="JavaScript">
                    if(menuN=="")
                      menuN="organization";
                  </script>
    		  </hrms:priv> 

		      <hrms:priv func_id="260" module_id="11">      		                   
                  <td class=top_button_v width="68" id="employee" onclick="javascript:changbutton('employee')"><A href="/templates/menu/employ_m_menu4.do?b_query=link&module=11" target='i_body'>人员管理</A></td>
    		       <script language="JavaScript">                    
                      menuN="employee";
                  </script>
    		  </hrms:priv>
		      <hrms:priv func_id="350" module_id="31">      		                   
                  <td class=top_button_v width="68" id="party" onclick="javascript:changbutton('party')"><A href="/templates/menu/party_m_menu.do?b_query=link&module=31" target='i_body'>党团管理</A></td>
    		       <script language="JavaScript">   
                     if(menuN=="")    		                        
                       menuN="party";
                  </script>
    		  </hrms:priv>      		             
                  <!-- 
                  <td class=top_title_v width="67" id="salary" onclick="show('salary')"><A href="/sys/downjnlp?app=4" target='i_body'>工资管理</A></td> 
                  -->
    		  <hrms:priv func_id="310" module_id="7">   
    		   <td class=top_button_v width="68" id="hire2" onclick="javascript:changbutton('hire2')"><A href="/templates/menu/hire_m_menu2.do?b_query=link&module=7" target='i_body'>招聘管理</A></td>    
    		   <script language="JavaScript">
                    if(menuN=="")
                      menuN="hire2";
                  </script>
    		  </hrms:priv>                    
		      <hrms:priv func_id="320" module_id="20">                   
	              <td class=top_button_v width="68" id="rsbd" onclick="javascript:changbutton('rsbd')">
	              <%if(isopen_1!=null&&isopen_1.equals("true")) {%>
	                 <A href="/general/template/search_bs_sort.do?b_search=link&type=1&res_flag=7&module=20" target='i_body'>人事异动</A>
	              <%}else {%>
	                 <A href="/general/template/search_bs_tree.do?b_query=link&type=1&res_flag=7&module=20" target='i_body'>人事异动</A>
	               <%} %> 
	              </td>      
	              <script language="JavaScript">
                    if(menuN=="")
                      menuN="rsbd";
                  </script>                     
    		  </hrms:priv>    
		      <hrms:priv func_id="270" module_id="6">                   
                  <td class=top_button_v width="68" id="kq" onclick="javascript:changbutton('kq')"><A href="/templates/menu/kq_m_menu.do?b_query=link&module=6" target='i_body'>考勤休假</A></td>                           
    		      <script language="JavaScript">
                    if(menuN=="")
                      menuN="kq";
                  </script>
    		  </hrms:priv>
		      <hrms:priv func_id="326" module_id="9">                    
                  <td class=top_button_v width="68" id="jx" onclick="javascript:changbutton('jx')"><A href="/templates/menu/per_m_menu.do?b_query=link&module=9&moduleflag=bs" target='i_body'>绩效管理</A></td>                           
    		      <script language="JavaScript">
                    if(menuN=="")
                      menuN="jx";
                  </script>
    		  </hrms:priv>	    		  
		      <hrms:priv func_id="324" module_id="8">                    
                  <td class=top_button_v width="68" id="gz" onclick="javascript:changbutton('gz')"><A href="/templates/menu/gz_m_menu.do?b_query=link&module=8&isopen=<%=isopen_2%>&moduleflag=bs" target='i_body'>薪资管理</A></td>                           
    		      <script language="JavaScript">
                    if(menuN=="")
                      menuN="gz";
                  </script>
    		  </hrms:priv>	
		      <hrms:priv func_id="325" module_id="14">                    
                  <td class=top_button_v width="68" id="ins" onclick="javascript:changbutton('ins')"><A href="/templates/menu/ins_m_menu.do?b_query=link&module=14&moduleflag=bs" target='i_body'>保险管理</A></td>                           
    		      <script language="JavaScript">
                    if(menuN=="")
                      menuN="ins";
                  </script>
    		  </hrms:priv>	 
    		  <hrms:priv func_id="327" module_id="28">                    
                  <td class=top_button_v width="68" id="gz_ins" onclick="javascript:changbutton('gz_ins')"><A href="/templates/menu/gz_ins_menu.do?b_query=link&module=28" target='i_body'>薪资福利</A></td>                           
    		      <script language="JavaScript">
                    if(menuN=="")
                      menuN="gz_ins";
                  </script>
    		  </hrms:priv>
    		  <hrms:priv func_id="323" module_id="10">                    
                  <td class=top_button_v width="68" id="train" onclick="javascript:changbutton('train')"><A href="/templates/menu/train_m_menu.do?b_query=link&module=10&moduleflag=bs" target='i_body'>培训管理</A></td>                           
    		      <script language="JavaScript">
                    if(menuN=="")
                      menuN="train";
                  </script>
    		  </hrms:priv>	 	   		  	  
    		  <!-- 
		      <hrms:priv func_id="240" module_id="7">     		                      
                  <td class=top_button_v width="68" id="hire" onclick="javascript:changbutton('hire')"><A href="/templates/menu/hire_m_menu.do?b_query=link&module=7" target='i_body'>招聘管理</A></td>    
    		  </hrms:priv> 
    		   -->


                  <!--                       
                  <td class=top_title_v width="68" id="performance" onclick="show('performance')"><A href="/sys/downjnlp?app=3" target='i_body'>绩效考核</A></td>                          
                  <td class=top_title_v width="68" id="train" onclick="show('train')"><A href="/sys/downjnlp?app=2" target='i_body'>培训管理</A></td>                          
                  -->
                 
                  <!-- 
                  <td class=top_title_v width="68" id="taskCenter" onclick="show('taskCenter')"><A href="/templates/menu/task_m_menu.do" target='i_body'>业务流程</A></td>
		      <hrms:priv func_id="280" module_id="12">                   
                  <td class=top_button_v width="68" id="rule" onclick="javascript:changbutton('rule')"><A href="/templates/menu/rule_m_menu.do?b_query=link&module=12" target='i_body'>制度政策</A></td>
    		  </hrms:priv>    
 
    		                    -->
   		                    
    		  <hrms:priv func_id="340,280" module_id="12">                   
                  <td class=top_button_v width="68" id="arch" onclick="javascript:changbutton('arch')"><A href="/templates/menu/document_m_menu.do?b_query=link&module=12" target='i_body'>文档管理</A></td>
    		      <script language="JavaScript">
                    if(menuN=="")
                      menuN="arch";
                  </script>
    		  </hrms:priv>               

              
		      <hrms:priv func_id="321" module_id="18">                   
	              <td class=top_button_v width="68" id="abroad_menu" onclick="javascript:changbutton('abroad_menu')"><A href="/templates/menu/gri_abroad_m_menu.do?b_query=link&module=18" target='i_body'>出国政审</A></td>                           
    		      <script language="JavaScript">
                    if(menuN=="")
                      menuN="abroad_menu";
                  </script>
    		  </hrms:priv> 
		      <hrms:priv func_id="322" module_id="19">                   
	              <td class=top_button_v width="68" id="deci_menu" onclick="javascript:changbutton('deci_menu')"><A href="/templates/menu/deci_m_menu.do?b_query=link&module=19" target='i_body'>领导决策</A></td>                           
    		      <script language="JavaScript">    		     
                    if(menuN=="")
                      menuN="deci_menu";
                  </script>
    		  </hrms:priv> 
		      <hrms:priv func_id="290" module_id="13">                   
                  <td class=top_button_v width="68" id="reportCenter" onclick="javascript:changbutton('reportCenter')"><A href="/templates/menu/tjb_m_menu.do?b_query=link&module=13" target='i_body'>报表管理</A></td> 
    		      <script language="JavaScript">
                    if(menuN=="")
                      menuN="reportCenter";
                  </script>
    		  </hrms:priv>      		  
		      <hrms:priv func_id="990" module_id="">                   
                  <td class=top_button_v width="68" id="busiManage" onclick="javascript:changbutton('busiManage')"><A href="/templates/menu/busi_m_menu.do?b_query=link&module=-1"  target="i_body">后台业务</a></td>
    		      <script language="JavaScript">
                    if(menuN=="")
                      menuN="busiManage";
                  </script>
    		  </hrms:priv>                  
		      <hrms:priv func_id="300" module_id="11">                   
                 <td class=top_button_v width="68" id="systemManage" onclick="javascript:changbutton('systemManage')"><A href="/templates/menu/sys_m_menu.do?b_query=link&module=11"  target="i_body">系统管理</a></td>
    		     <script language="JavaScript">
                    if(menuN=="")
                      menuN="systemManage";
                  </script>
    		  </hrms:priv>   
                   
				<%
				 if(bexchange)
				 { 
				%>                          
				  <td class=top_button_v width="68" id="selfCenter" onclick="javascript:changbutton('selfCenter')"><A href="javascript:winopen();"><bean:message key="label.selfservice.os"/></A></td>
				<%}%>	
				
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
<script language="JavaScript">
      function clickMenu(name)
      {
         if(name==""||autologon=="1")
           return false;
         var target_url="";         
         if(name=="employee")
         {
            target_url="/templates/menu/employ_m_menu4.do?b_query=link&module=11";
         }else if(name=="organization")
         {
             target_url="/templates/menu/org_m4_menu.do?b_query=link&module=11";
             
         }else if(name=="hire2")
         {
             target_url="/templates/menu/hire_m_menu2.do?b_query=link&module=7";
         }else if(name=="rsbd")
         {
            <%if(isopen_1!=null&&isopen_1.equals("true")) {%>
	            target_url="/general/template/search_bs_sort.do?b_search=link&type=1&res_flag=7&module=20";
	        <%}else {%>
	            target_url="/general/template/search_bs_tree.do?b_query=link&type=1&res_flag=7&module=20";
	        <%} %> 
         }else if(name=="kq")
         {
            target_url="/templates/menu/kq_m_menu.do?b_query=link&module=6";
         }else if(name=="gz")
         {
            target_url="/templates/menu/gz_m_menu.do?b_query=link&module=8&isopen=<%=isopen_2%>&moduleflag=bs";
         }else if(name=="jx")
         {
            target_url="/templates/menu/per_m_menu.do?b_query=link&module=9&moduleflag=bs";
         }else if(name=="ins")
         {
            target_url="/templates/menu/ins_m_menu.do?b_query=link&module=14&moduleflag=bs";
         }else if(name=="gz_ins")
         {
            target_url="/templates/menu/gz_ins_menu.do?b_query=link&module=16";
         }else if(name=="train")
         {
            target_url="/templates/menu/train_m_menu.do?b_query=link&module=21";
         }else if(name=="arch")
         {
            target_url="/templates/menu/document_m_menu.do?b_query=link&module=12";
         }else if(name=="abroad_menu")
         {
            target_url="/templates/menu/gri_abroad_m_menu.do?b_query=link&module=18";
         }else if(name=="deci_menu")
         {
            target_url="/templates/menu/deci_m_menu.do?b_query=link&module=19";
         }else if(name=="reportCenter")
         {
            target_url="/templates/menu/tjb_m_menu.do?b_query=link&module=13";
         }else if(name=="busiManage")
         {
            target_url="/templates/menu/busi_m_menu.do?b_query=link&module=-1";
         }else if(name=="systemManage")
         {
            target_url="/templates/menu/sys_m_menu.do?b_query=link&module=11";
         }else if(name=="party")
         {
            target_url="/templates/menu/party_m_menu.do?b_query=link&module=31";
         }          
         if(target_url!="")
           window.open(target_url,"i_body");         
      }      
      clickMenu(menuN);
</script>
<script language="JavaScript">
<%if(view_time!=null&&view_time.equals("true")){ %>
reloop('<%=new Date().toGMTString() %>')
<%}%>
</script>