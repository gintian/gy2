<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>

<html>
<head>
<%
String title=SystemConfig.getPropertyValue("frame_logon_title")!=null?SystemConfig.getPropertyValue("frame_logon_title").toString():"eHR";
 %>
<title><%=title%></title>
<style type="text/css">
<!--
.tabpos {
	position: absolute;
	left: 520px;
	top: 280px;
}
#wrapper{
	width:1000px;
	margin:0 auto;
	text-align:left;
}
-->
</style>
<link rel="stylesheet" href="./css/login.css" type="text/css">
<script language="JavaScript" src="/js/newcalendar.js"></script>
<script language="JavaScript" src="/ajax/basic.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
</head>
   <script language="JavaScript1.2">

     function winopen()
     {
   	var userID=document.logonForm.username.value;
   	var password = document.logonForm.password.value;
   	var appdate=document.logonForm.appdate.value;
   	appdate=appdate.replace(/\-/g,".");
   	var url = "/templates/index/hrlogon.do?logon.x=link&username="+userID+"&password="+password+"&appdate="+appdate;
	newwin=window.open(url,"_blank","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
    if (document.all){
		newwin.moveTo(-4,-4);
		newwin.resizeTo(screen.width+6,screen.height-20);
	}
	newwin.location=url;
	window.opener=null;//不会出现提示信息
    self.close();
    }
    
    function pf_ChangeFocus(e) 
    { 
      e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
      var key = window.event?e.keyCode:e.which;
      var t=e.target?e.target:e.srcElement;
      if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
      {
   		if(t.name!="logon")      
   		   if(window.event)
   		   	e.keyCode=9;
   		   else
   		   	e.which=9;
      }
    }   
    function up_ChangeFocus(e)
    {
    	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
      var t=e.target?e.target:e.srcElement;
       if(t.name=="logon")    
       {
         //document.logonForm.submit();
         var obj=document.getElementById("logon");	
           obj.click();
       }
         
    } 
    /*设置计算截止日期*/
    function getAppdate()
    {
      var strvalue;
      var now = new Date();        
      strvalue=getCookie("appdate");
      if(strvalue==null)
      {
    	strvalue=getDateString(now,".");
    	setCookie("appdate",strvalue);
      }
      <%  if(SystemConfig.getPropertyValue("dusi_date_display")==null||SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("")||SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("true")){ %>
          document.logonForm.appdate.value=strvalue.replace(/\./g,"-");
       <%}%>
    } 
    
    function setAppdate()
    {
      var strvalue;
      strvalue=document.logonForm.appdate.value;
      strvalue=strvalue.replace(/\-/g,".");
      setCookie("appdate",strvalue);
    }   

   </script>

<body background="" onKeyDown="return pf_ChangeFocus(event);" onKeyUp="up_ChangeFocus(event);" onload="getAppdate();">
<html:form focus="username" action="/templates/index/hrlogon" >
<div id="box">
		<div id="header">
			<div id="logo"></div>
		</div>
		<div id="logoncenter">
		  <div id="content">
					<div class="login">
						<div class="line">
						  <img src="./images/syslogin/line.gif" alt=" " />
						 </div>						
						 <table height="100%" class="text" border="0" cellspacing="0"  cellpadding="0">
						   <tr>
						     <td>
						       用户名
						     </td>
						     <td  colspan="2">&nbsp;
						       <input class="labbox" type="text" name="username" size="20" TABINDEX="1">
						     </td>
						     <td>
						     </td>
						   </tr>
						   <tr>
						     <td>
						       密　码
						     </td>
						     <td  colspan="2">&nbsp;
						       <input class="labbox" type="password" name="password" maxlength="20" size="20" TABINDEX="2">
						     </td>
						      <td>
						      <%
	                            if(SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("false")&&(!SystemConfig.isValidateCode())&&!(SystemConfig.getPropertyValue("retrieving_password")!=null&&SystemConfig.getPropertyValue("retrieving_password").equalsIgnoreCase("true")))
	                            {%>
	                              <label style="margin-left:10px;">
	                                  <input name="logon" type="image" src="./images/syslogin/c1.gif" TABINDEX="3">  
	                                </label>
	                            <%} %>	
	                            <%
	                            //String retrieving_password=SystemConfig.getPropertyValue("retrieving_password");
	                            String     retrieving_password=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.RETRIEVING_PASSWORD);
	                                if(retrieving_password.equalsIgnoreCase("true"))
	                                {
                                 %>
                                 	<label style="margin-left:14px;">
                                       <a href="###" onclick="getPassword('1');" style="font-size: 12px;color: white">忘记密码</a>
                               		</label>
                               <%} %>					      
						     </td>
						     </tr>
						     <%
	                            if(SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("false")&&(!SystemConfig.isValidateCode())&&(SystemConfig.getPropertyValue("retrieving_password")!=null&&SystemConfig.getPropertyValue("retrieving_password").equalsIgnoreCase("true")))
	                            {%>
						     <tr>
						     <td>
						     &nbsp;
						     </td>
						     <td  colspan="2">&nbsp;
						     </td>
						      <td>
	                              <label style="margin-left:10px;">
	                                  <input name="logon" type="image" src="./images/syslogin/c1.gif" TABINDEX="3">  
	                                </label>
						     </td>
						   </tr>
						    <%} %>	
                         <%  if(SystemConfig.getPropertyValue("dusi_date_display")==null||SystemConfig.getPropertyValue("dusi_date_display").equals("")||SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("true")){ %>
						   
						   <tr>
						     <td>
						       业务日期
						     </td>
						     <td  colspan="2">&nbsp;
						        <input class="labbox" type="text" name="appdate" maxlength="10" size="20" onclick="calendar(this);" onfocus="/*inittime(true);*/" TABINDEX="3">
						     </td>
						      <td>
						      <%
                            if(!SystemConfig.isValidateCode())
                            {%>
                              <label style="margin-left:10px;">
                                  <input name="logon" type="image" src="./images/syslogin/c1.gif" TABINDEX="4">  
                                </label>
                            <%} %>
						     </td>
						   </tr>
                          <% } %>						   
						    <%
                            if(SystemConfig.isValidateCode())
                            {%>
						   <tr>
						     <td>
						       校验码
						     </td>
						     <td>&nbsp;
						       <input class="labbox1" type="text" name="validatecode" maxlength="20" size="20">&nbsp;
					         </td>
					         <td>
					           <hrms:validatecode codelen="6"></hrms:validatecode>
						     </td>
						      <td>
						        <label style="margin-left:10px;">
                                  <input name="logon" type="image" src="./images/syslogin/c1.gif" TABINDEX="3">  
                                </label>
						     </td>
						   </tr>
						   <%} %>
						 </table>						
					     
					</div>
			</div>
		</div>
		<div id="footer">
			<div id="copyright">

			</div>
  </div>
</div>


<div class="copyright"></div>



</html:form>
<script type="text/javascript">
	var frmname=window.name;
	if(frmname!=""&&(frmname!="0"&&frmname!="1"&&frmname!="iphone"))
	{
	    alert(SYS_LBL_SESSION);	
		var newwin=window.open(window.location,"_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
		window.opener=null;
   	    self.close();		
	}
	
</script>
</body>
</html>
