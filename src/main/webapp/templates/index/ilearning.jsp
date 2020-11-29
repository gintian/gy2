<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.hjsj.sys.ResourceFactory"%>
<html>
<head>
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
<LINK rel="Bookmark" href="favicon.ico">
<%
String title = SystemConfig.getPropertyValue("frame_logon_title");
title=(title!=null&&title.length()!=0)?title:ResourceFactory.getProperty("frame.logon.title");
 %>
<title><%=title%></title>

<link rel="stylesheet" href="/css/login.css" type="text/css">
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>

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

#logo_ilearning{
    width:714px;
    height:160px;
    background-image:url(/images/ilearning/login_logo.gif);                            
    background-repeat:no-repeat;    
    margin-left:auto;
    margin-right:auto;
    margin-bottom:0;
    position:absolute;bottom:0;
    position:absolute;left:30%;
    }
-->
</style>

</head>
   <script language="JavaScript1.2">

     function winopen()
     {
   	var userID=document.logonForm.username.value;
   	var password = document.logonForm.password.value;
   	var appdate=document.logonForm.appdate.value;
   	appdate=appdate.replace(/\-/g,".");
   	var url = "/templates/index/ilearning.do?logon.x=link&username="+userID+"&password="+password+"&appdate="+appdate;
	newwin=window.open(url,"_blank","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
    if (document.all){
		newwin.moveTo(-4,-4);
		newwin.resizeTo(screen.width+6,screen.height-20);
	}
	newwin.location=url;
	window.opener=null;//不会出现提示信息
    self.close();
    }
    
    function pf_ChangeFocus() 
    { 
      key = window.event.keyCode;
      if ( key==0xD && event.srcElement.tagName!='TEXTAREA') /*0xD*/
      {
   		if(event.srcElement.name!="logon")      
   		   window.event.keyCode=9;
      }
    }   
    function up_ChangeFocus()
    {
       if(event.srcElement.name=="logon")    
       {
         document.logonForm.submit();
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
      if(document.logonForm.appdate)
      	document.logonForm.appdate.value=strvalue.replace(/\./g,"-");
    } 
    
    function setAppdate()
    {
      var strvalue;
      strvalue=document.logonForm.appdate.value;
      strvalue=strvalue.replace(/\-/g,".");
      setCookie("appdate",strvalue);
    }   

   </script>

<body onKeyDown="return pf_ChangeFocus();" onload="getAppdate();">
<html:form focus="username" action="/templates/index/ilearning" >
<div id="box">
		<div id="header">
			<div id="logo_ilearning"></div>
		</div>
		<div id="logoncenter">
		  <div id="content">
					<div class="login">
						<div class="line">
						  <img src="../../images/login/line.gif" alt=" " />
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
	                            if(SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("false")&&(!SystemConfig.isValidateCode()))
	                            {%>
	                              <label style="margin-left:10px;">
	                                  <input name="logon" type="image" src="../../images/login/c1.gif" TABINDEX="3">  
	                                </label>
	                            <%} %>						      
						     </td>
						   </tr>
                         <%  if(SystemConfig.getPropertyValue("dusi_date_display")==null||SystemConfig.getPropertyValue("dusi_date_display").equals("")||SystemConfig.getPropertyValue("dusi_date_display").equalsIgnoreCase("true")){ %>
						   
						   <tr>
						     <td>
						       业务日期
						     </td>
						     <td  colspan="2">&nbsp;
						        <input class="labbox" type="text" name="appdate" maxlength="10" size="20" onfocus="setday(this);/*inittime(true);*/" TABINDEX="4">
						     </td>
						      <td>
						      <%
                            if(!SystemConfig.isValidateCode())
                            {%>
                              <label style="margin-left:10px;">
                                  <input name="logon" type="image" src="../../images/login/c1.gif" TABINDEX="3">  
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
					           <img align="absMiddle" title="换一张" onclick="validataCodeReload()" src="/servlet/vaildataCode?channel=1&codelen=6&bosflag=hr" id="vaildataCode">
						     </td>
						      <td>
						        <label style="margin-left:10px;">
                                  <input name="logon" type="image" src="../../images/login/c1.gif" TABINDEX="3">  
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
	if(frmname!=""&&(frmname!="0"&&frmname!="1"))
	{
	    alert(SYS_LBL_SESSION);	
		var newwin=window.open(window.location,"_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
		window.opener=null;
   	    self.close();		
	}
	
</script>
</body>
</html>
