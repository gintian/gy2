<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>

<%
			boolean bv=true;
			//String value=SystemConfig.getPropertyValue("passwordrule");
				String     value=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORDRULE);
		    if(value==null||value.length()==0||value.equalsIgnoreCase("0"))
		    	bv=false;
		    String returnvalue = request.getParameter("returnvalue");
		    UserView userView=(UserView) pageContext.getSession().getAttribute(WebConstant.userView);
		    String bosflag = userView.getBosflag();
            String b_update = request.getParameter("b_edit");
		    String top = "5";
		    if(bosflag.equalsIgnoreCase("hcm")) {
		        if("link".equals(b_update))
                    top = "12";
		        else
		            top="42";
            }
%>
<script type='text/javascript' src='/ext/aes.js'></script>
<script type="text/javascript">
<!--
	function checkIsIntNum(value)
	{
		return /^[0-9]*[1-9][0-9]*$/.test(value);
	}
	function validate(pwd)
	{
	    var c,d;
	    var bflag=true;
	    var bnumber=false;
	    var bletter=false;
		for(var i=0;i<pwd.length;i++)
		{
			c=pwd.charAt(i);
			if(checkIsIntNum(c))
			{
			   bnumber=true;
			}
			else
			{
			   bletter=true;
			}
			for(var j=0;j<pwd.length;j++)
			{
			  d=pwd.charAt(j);
			  if(c==d&&(i!=j))
			  {
			    bflag=false;
			    break;
			  }
			}
			if(!bflag)
			  break;
		}
		return (bflag&&bnumber&&bletter);
	}
	function save(){
		var userP=document.getElementById("userP").value;
		var oldpwd=document.getElementsByName("oldpwd")[0].value;
		var newpwd=document.getElementsByName("newpwd")[0].value;
		var newokpwd=document.getElementsByName("newokpwd")[0].value;
		//xus 17-6-15 保存时密码不符合规则也会被保存。
		if(!checkResult){
			alert("密码不符合规则！保存失败！");
			return;
		}
		if(userP.toLowerCase()!=oldpwd.toLowerCase())
       {
         alert("<bean:message key="errors.sys.password"/>");
         return;       	
       }
		if(newpwd.toLowerCase()!=newokpwd.toLowerCase())
       {
         alert("<bean:message key="errors.sys.newpassword"/>");
         return;       	
       }
       <%if(bv){%>
       		/*if(!validate(newpwd))
       		{
       		    alert("<bean:message key="error.password.validate"/>");
       			return;
       		}*/
       	<%}%>
       	var hashvo=new ParameterSet();
       	hashvo.setValue("user_name","<%=request.getParameter("user_name") %>");
     	hashvo.setValue("userP",userP);
     	hashvo.setValue("oldpwd",oldpwd);
     	hashvo.setValue("newpwd",newpwd);
     	hashvo.setValue("newokpwd",newokpwd);
       	var request=new Request({asynchronous:false,onSuccess:update_object_ok,functionId:'1010010100'},hashvo);  
		
	}
   function update_object_ok(outparamters)
  {
  	var msg=outparamters.getValue("msg");
  	if(msg=='ok'){
  		alert("修改成功!");
  		window.close();
  	}else{
  		alert("修改失败!");
  	}
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
<html:form action="/system/security/resetup_password">
   <style>
      .passWordCheck{
          background:url(/images/bubble-l.png) no-repeat left center;
          border:none;
          top:<%=top%>;
          left:314px;
          width:230px;
          padding-left:10px;
          position:absolute ;
          margin: 0px;
          display: none;
      }
      .checkList{
         text-overflow:ellipsis;
         padding:2px 0px 2px 10px;
         height:18px;
         width:230px;
         white-space: nowrap;
         background:url(/images/password.png) no-repeat 0 8
      }
   </style>
   <script>
       var numChecker = '1234567890';
       var wordChecker = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
       //允许的字符中不包含英文逗号   33720 wangb 20180105
       var charCheker = '%$#@!~^&*()+"\'';
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
    	       
    	       var rule1 = pwdRule=='2' && hasNum && hasWord && hasChar && value.length>=pwdLength && noRepeatChar;
    	       var rule2 = pwdRule=='1' && hasNum && hasWord && value.length>=pwdLength;
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
       
       /*保存前对密码进行加密 guodd 2018-09-28*/
		function beforeSave(){
    	   if(checkResult){
    		   var oldpwdEle = document.getElementsByName("oldpwd")[0];
    		   var newpwdEle = document.getElementsByName("newpwd")[0];
    		   var newpwdokEle = document.getElementsByName("newokpwd")[0];
    		   //【54198】 加密后长度会边长，导致长度校验失败。此处将input最大长度加大。考虑密码长度不可能超过1000，此处设置为1000 guodd 2019-10-22
               oldpwdEle.maxLength = 1000;
               newpwdEle.maxLength = 1000;
               newpwdokEle.maxLength = 1000;
    		   oldpwdEle.value = encrypt(oldpwdEle.value);
    		   newpwdEle.value = encrypt(newpwdEle.value);
    		   newpwdokEle.value = encrypt(newpwdokEle.value);
    	   }
    	   return checkResult;
       }
     	//AES-128-CBC加密模式，key需要为16位，key和iv可以一样
   		function encrypt(data) {
   	        var key  = CryptoJS.enc.Utf8.parse('hjsoftjsencryptk');//编码格式utf-8
   	        var iv   = CryptoJS.enc.Utf8.parse('hjsoftjsencryptk');
   	        var srcs = CryptoJS.enc.Utf8.parse(data);
   	     	var newData =  CryptoJS.AES.encrypt(srcs, key, {iv:iv,mode:CryptoJS.mode.CBC/*,padding:CryptoJS.pad.ZeroPadding*/}).toString();
   	     	return encodeURIComponent(newData);
   		}
   </script>
                	      	  	      
  <!-- 修改系统管理修改密码界面  jingq upd   2014.5.29  -->
  <table width="490" border="0" cellpadding="0" cellspacing="0" align="center" valign="middle">
  	<tr>
  		<td style="position: relative">
            <%
                String passwordrule=SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordrule");
                String passwordlength = SysParamBo.getSysParamValue("SYS_SYS_PARAM","passwordlength");
            %>
            <script>
                var pwdRule = '<%=passwordrule%>';
                var pwdLength = '<%=passwordlength%>';
            </script>
            <div id="passWordCheckBox" class="passWordCheck" style="position: absolute">
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
                    <!-- 修改密码不能输入 英文逗号字符 33720 wangb 20180105 -->
                    <li id='charRule' class="checkList">允许的符号:~ ! @ # $ % ^ & * ( ) + " ' </li>
                    <li id='spaceRule' class="checkList">不允许有空格</li>
                </ul>
            </div>
  		<div>
         <table width="490" border="0" cellpadding="0" cellspacing="0"  class="ftable">
          <tr height="20">
       		<td align="left" class="TableRow" colspan="2"><bean:message key="label.password.setup"/>&nbsp;</td>
         	      
          </tr>
          <%
            	if("link".equals(b_update)){
            %>
                      <tr style="display: none">
                      <%}else{ %>
                      <tr>
                      <%} %>
                	      <td align="right" nowrap class="tdFontcolor"><bean:message key="label.sys.oldpwd"/></td>
                	      <td align="left" nowrap class="tdFontcolor"> 
                             <input type="password" name="oldpwd" class="text4" autocomplete="off" style="width:200px;" onkeypress="return dofilter(event);"  size="20" maxlength="${loginpwdForm.pwdlen}"  />
                              </td>
                      </tr>
            
                      <tr>
                	      <td align="right" nowrap class="tdFontcolor"><bean:message key="label.sys.newpwd"/></td>
                	      <td align="left" nowrap class="tdFontcolor" height=30>
                	      	<input type="password" name="newpwd" class="text4" autocomplete="off" style="width:200px;" onkeypress="return dofilter(event);" onkeyup="checkPassWord(this)" onfocus="showPassWordCheckBox(this,1)" onblur="showPassWordCheckBox(this,0)"   size="20" maxlength="${loginpwdForm.pwdlen}"  />  
                              
                              </td>
                      </tr>
                      <tr>
                	      <td align="right" nowrap class="tdFontcolor"><bean:message key="label.sys.newokpwd"/></td>
                	      <td align="left" nowrap class="tdFontcolor">  	      
                            <input type="password" name="newokpwd" class="text4" autocomplete="off"  style="width:200px;" onkeypress="return dofilter(event);"    size="20" maxlength="${loginpwdForm.pwdlen}"  />  
                            
                            
                              </td>
                      </tr>                                            
         
          <tr class="list3">
            <td colspan="2" align="center" valign="middle" style="height:35px;padding-top: 5px;">
            <%
            	if("link".equals(b_update)){
            %>
            	<input type=hidden id='userP' value="${loginpwdForm.oldpwd }"/>
               <button type="button" class=mybutton onclick="save()" ><bean:message key="button.save"/></button>&nbsp;
	       		<button type="button" class=mybutton onclick=window.close();><bean:message key="button.close"/></button>
	       <%}else{ %>
	       		<script type="text/javascript">
	       			document.getElementsByName("oldpwd")[0].value="";
	       			document.getElementsByName("newpwd")[0].value="";
	       			document.getElementsByName("newokpwd")[0].value="";
	       		</script>
	       		<!-- returnvalue=menuitem 个人设置修改密码 jingq add 2014.08.20 -->
	       		<%if("menuitem".equals(returnvalue)){ %>
	       		<hrms:submit styleClass="mybutton" onclick="return beforeSave();" style="margin-bottom:5px;" property="b_save" >
                    <bean:message key="button.save"/>
	       		</hrms:submit>
	       		<html:reset styleClass="mybutton" style="margin-bottom:5px;">
                    <bean:message key="button.clear"/>
	       		</html:reset> 
	       		<%} else { %>
	       		<hrms:submit styleClass="mybutton" onclick="return beforeSave();"  property="b_save" >
                    <bean:message key="button.save"/>
	       		</hrms:submit>
	       		<html:reset styleClass="mybutton">
                    <bean:message key="button.clear"/>
	       		</html:reset> 
	       		<%} %>
	       <%} %>
               	
            </td>
          </tr>  
  </table>
  </div>
 </td>
  	</tr>
  </table>
</html:form>
