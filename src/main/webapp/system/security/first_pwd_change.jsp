<%@page import="com.hjsj.hrms.utils.AesEncryptUtil"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.hjsj.sys.ResourceFactory"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<html>
<head>
<%
String title = SystemConfig.getPropertyValue("frame_logon_title");
title=(title!=null&&title.length()!=0)?title:ResourceFactory.getProperty("frame.logon.title");
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String bosflag = "";
if(userView != null)
{
  bosflag = userView.getBosflag();
}

String username = userView.getUserName();
username = AesEncryptUtil.aesEncrypt(username);

%>
<title><%=title%></title>
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
<LINK rel="bookmark" href="favicon.ico"  type="image/x-icon">
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<script type='text/javascript' src='/ext/aes.js'></script>
<script type="text/javascript">
//<!--
	function save(){
		var newpwdEle = document.getElementsByName("newpwd")[0];
		var newpwdokEle = document.getElementsByName("newokpwd")[0];
		
		if(newpwdEle.value.toLowerCase()!=newpwdokEle.value.toLowerCase()){
         alert("<bean:message key="errors.sys.newpassword"/>");
         return false;       	
       }
		
		/*保存前对密码进行加密 guodd 2018-09-28*/
	   newpwdEle.value = encrypt(newpwdEle.value);
	   newpwdokEle.value = encrypt(newpwdokEle.value);
		var formaction = "/system/security/resetup_password.do?userkey="+encodeURIComponent("<%=username%>");
		<%
		if(bosflag.equalsIgnoreCase("hl"))
		{%>
			formaction+="&b_first_save=link";               
		<%}
		else  if(bosflag.equalsIgnoreCase("bi"))
		{%>
			formaction+="&b_first_save_b=link";        
		<%}
		else if(bosflag.equalsIgnoreCase("il"))
		{%>
			formaction+="&b_first_save_i=link";;           
		<%}  
		else if(bosflag.equalsIgnoreCase("epmgw"))
		{%>
			formaction+="&b_first_save_he=link";             
		<%}
		else if(bosflag.equalsIgnoreCase("hcm"))
		{%>
			formaction+="&b_first_save_hc=link";          
		<%}
		else{%>
		    formaction+="&b_first_save=link";
		<%}%>
		loginpwdForm.action = formaction;
		loginpwdForm.submit();
		
	}
	//AES-128-CBC加密模式，key需要为16位，key和iv可以一样
	function encrypt(data) {
        var key  = CryptoJS.enc.Utf8.parse('hjsoftjsencryptk');//编码格式utf-8
        var iv   = CryptoJS.enc.Utf8.parse('hjsoftjsencryptk');
        var srcs = CryptoJS.enc.Utf8.parse(data);
        var newData =  CryptoJS.AES.encrypt(srcs, key, {iv:iv,mode:CryptoJS.mode.CBC/*,padding:CryptoJS.pad.ZeroPadding*/}).toString();
        return encodeURIComponent(newData);
	}
   
   
   function dofilter(e){
      e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
      var key = window.event?e.keyCode:e.which;
      //alert(key);
      if(key==96)//过滤`
    	  return false;
   }
//-->
</script>

<style>
      .passWordCheck{
          background:url(/images/bubble-l.png) no-repeat left center;
          border:none;
          top:-115;
          left:315;
          width:300px;
          padding-left:10;
          position:relative;
          display:none;
      }
      .checkList{
         text-overflow:ellipsis;
         padding:2 0 2 10px;
         height:18px;
         width:270px;
         white-space: nowrap;
         background:url(/images/password.png) no-repeat 0 8
      }
   </style>
   <script>
       var numChecker = '1234567890';
       var wordChecker = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
       var charCheker = '%$#@!~^&*()+"\',';
       var checkResult = true;
       function showPassWordCheckBox(input,visible){
    	       var box = document.getElementById('passWordCheckBox');
    	       if(!visible && checkResult){
    	    	   	   box.style.display='none';
    	    	       return;
    	       }
    	       box.style.display='block';
       }
       function checkPassWord(input){
	       var value = input.value.split(''),b,
	       lengthRule = document.getElementById('lengthRule'),
	         charRule = document.getElementById('charRule'),
	         spaceRule = document.getElementById('spaceRule');
	         lengthRule.style.color = 'black';
         charRule.style.color = 'black'; 
         spaceRule.style.color = 'black';
         lengthRule.style.backgroundPosition='0 8';
         spaceRule.style.backgroundPosition='0 8';
         charRule.style.backgroundPosition='0 8';
         checkResult = true;
        if(value.length==0)
        	   return;
       
      var hasNum = false;
      var hasWord = false;
      var hasChar = false;
      var hasSpace = false;
      var wrongChar = false;
      var noRepeatChar = true;
      
      var prechar = '';
        
	   for(var i=0;i<value.length;i++){
	    	   b = value[i];
	    	   if(prechar.indexOf(b)>-1)
	    		   noRepeatChar = false;
	    	   prechar+=b;
	    	   
	    	   if(b==" "){
	    	    	   		hasSpace = true;
	    	   }else if(numChecker.indexOf(b)>-1){
	    	    	   		hasNum = true;
	    	   }else if(wordChecker.indexOf(b)>-1){
	    	    	     	hasWord = true;
    	       }else if(charCheker.indexOf(b)>-1){
    	    	     	hasChar = true;
    	       }else{
    	    	   		wrongChar = true;
    	       }
	    	       
	    }
	       
	       if(hasSpace){
	    	   	  spaceRule.style.color='red';
	    	      checkResult = false;
	    	      spaceRule.style.backgroundPosition='0 -25';
	       }
	       
	       if(wrongChar){
	    	      charRule.style.color='red';
	    	   	  charRule.style.backgroundPosition='0 -25';
	    	   	  checkResult = false;
	       }
	       
	       //高难度限制
	       var rule1 = pwdRule=='2' && hasNum && hasWord && hasChar && value.length>=pwdLength && noRepeatChar;
	       //中难度限制
	       var rule2 = pwdRule=='1' && hasNum && hasWord && value.length>=pwdLength;
	       //没有限制
	       var rule3 = pwdRule!='1' && pwdRule!='2';
	       
	       if(!(rule1 || rule2 || rule3)){
	       	    checkResult = false;
	       	    lengthRule.style.color='red';
	       	    lengthRule.style.backgroundPosition='0 -25';
	       }
	       
	       if(checkResult){
	    	       lengthRule.style.backgroundPosition='0 -8';
	    	       spaceRule.style.backgroundPosition='0 -8';
	    	       charRule.style.backgroundPosition='0 -8';
	       }
	       
	   
   }
   </script>
<html:form action="/system/security/resetup_password">
  <br>
  <br>  
  <table align="center">
     <tr>
        <td>
        		<table width="500" border="0" cellpadding="0" cellspacing="0"  class="ftable">
		          <tr height="20">
		       		<td align="left" class="TableRow" colspan="2">&nbsp;首次密码修改&nbsp;</td>
		         	      
		          </tr> 
		            
		                      <tr>
		                	      <td align="right" nowrap class="tdFontcolor"><bean:message key="label.sys.newpwd"/>:</td>
		                	      <td align="left" nowrap class="tdFontcolor"><!-- 【8238】首次登录，必须修改密码，密码符和其他地方不统一  jingq add 2015.04.03 -->
		                	      	<html:password name="loginpwdForm" property="newpwd" onkeyup="checkPassWord(this)" onfocus="showPassWordCheckBox(this,1)" onblur="showPassWordCheckBox(this,0)" size="20" maxlength="${loginpwdForm.pwdlen}" styleClass="text4" style="width:180;" onkeypress="return dofilter(event);"/>    	      
		                              </td>
		                      </tr>
		                      <tr>
		                	      <td align="right" nowrap class="tdFontcolor"><bean:message key="label.sys.newokpwd"/>:</td>
		                	      <td align="left" nowrap class="tdFontcolor">
		                	      	<html:password name="loginpwdForm" property="newokpwd" size="20" maxlength="${loginpwdForm.pwdlen}" styleClass="text4" style="width:180;" onkeypress="return dofilter(event);"/>    	      
		                              </td>
		                      </tr>                                            
		         
		          <tr class="list3">
		            <td colspan="2" align="center" style="height:35px">
			       		<script type="text/javascript">
		                    document.getElementsByName("newpwd")[0].value="";
		                    document.getElementsByName("newokpwd")[0].value="";
		                </script>
		                <button type="button" class="mybutton" onclick="if(!checkResult) return false;save();"><bean:message key="button.save"/></button>
		            </td>
		          </tr>  
		     </table>
        
            <%
		      String passwordrule=SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordrule");
		      String passwordlength = SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordlength");
		   %>
		   <script>
		       var pwdRule = '<%=passwordrule%>';
		       var pwdLength = '<%=passwordlength%>';
		   </script>
		  <div id="passWordCheckBox" class="passWordCheck">
		           <ul style="background-color:white;border:1px #b0b0b0 solid; border-left:none;min-height:60;margin:0px;padding-left:2px;list-style:none;">
		              <li id='lengthRule' class="checkList">
							   <%
		                             if("2".equals(passwordrule)){
		                            	 String msg = ResourceFactory.getProperty("error.password.validate.strong").replace("{0}", SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordlength")).replaceAll("\"'","");
		                             %>
		                              长度不少于<%=passwordlength%>位,必须包含字母、数字和符号且不重复
		                              <%}else if("1".equals(passwordrule)) {
		                            	  String msg = ResourceFactory.getProperty("error.password.validate.moderate").replace("{0}", SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordlength"));
		                              %>
		                              长度不少于<%=passwordlength%>位,必须包含字母和数字
		                              <%}else{%>
		                                新密码强度不做要求
		                              <%} %>
					  </li>
		              <li id='charRule' class="checkList">允许的符号:~ ! @ # $ % ^ & * ( ) + " ' ,</li>
		              <li id='spaceRule' class="checkList">不允许有空格</li>
		           </ul>
		  </div>
        
        </td>
     </tr>
  </table>
  
 
</html:form>
