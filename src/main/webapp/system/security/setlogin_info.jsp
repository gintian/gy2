<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@page import="com.hjsj.hrms.actionform.sys.AccountForm"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.utils.ResourceFactory"%>
<script type="text/javascript">
//<!--
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
          left:290;
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
       </script>
<html:form action="/system/security/setlogin_info">
   <table align="center" style="margin-top:7px;" border="0">
      <tr>
        <td>
		      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" >
		          <tr>
		          	<td colspan="2">
		          	<hrms:codetoname codeid="UN" name="accountForm" codevalue="user_vo.string(b0110)" codeitem="codeitem"/>  	      
		          	<bean:write name="codeitem" property="codename" />&nbsp;
		          	<!-- 【5712】当a01信息集中的E0122未勾库时，到系统管理-账号管理，一点就报附件中的错误。   jingq add 2014.12.06 -->
		          	<% 
		          		AccountForm	accountForm = (AccountForm)session.getAttribute("accountForm"); 
		          		RecordVo user_vo = accountForm.getUser_vo();
		          		if(user_vo.hasAttribute("e0122")){
		          	%>
		          	<hrms:codetoname codeid="UM" name="accountForm" codevalue="user_vo.string(e0122)" codeitem="codeitem"/>  	      
		          	<bean:write name="codeitem" property="codename" />&nbsp;    
		          	<%} %>
		          	<hrms:codetoname codeid="@K" name="accountForm" codevalue="user_vo.string(e01a1)" codeitem="codeitem"/>  	      
		          	<bean:write name="codeitem" property="codename" />&nbsp;    
		          	</td>
		          </tr>		
		          <tr height="20">
		       		<td  align="left" class="TableRow" colspan="2"><bean:write name="accountForm" property="user_vo.string(a0101)" />&nbsp;</td>
		          </tr> 
		                      <tr class="list3">
		                	      <td align="right" nowrap ><bean:message key="label.username"/></td>
		                	      <td align="left" nowrap >
		                	      	<html:text name="accountForm" property="user_vo.string(username)" size="20" maxlength="${accountForm.userlen}" styleClass="text"/>    	      
		                              </td>
		                      </tr>
		                    
		              	      <tr class="list3">
		                	      <td align="right" nowrap ><bean:message key="label.new.password"/></td>
		                	      <td align="left"  nowrap>
		                	      	<html:password name="accountForm" onkeyup="checkPassWord(this)" onfocus="showPassWordCheckBox(this,1)" onblur="showPassWordCheckBox(this,0)" property="user_vo.string(userpassword)" size="20" maxlength="${accountForm.pwdlen}" styleClass="text" onkeypress="return dofilter(event);"/>    	      
		                      </tr> 
		                      
		                      <tr class="list3">
		                	      <td align="right" nowrap ><bean:message key="label.password.ok"/></td>
		                	      <td align="left"  nowrap>
		                	      	<html:password name="accountForm" property="user_vo.string(state)" size="20" maxlength="${accountForm.pwdlen}" styleClass="text" onkeypress="return dofilter(event);"/>    	      
		 	                      </td>
		                      </tr> 
		                      <hrms:priv func_id="0B4"> 
		                      <logic:notEqual name="accountForm" property="ip_addr" value=""> 
		                      <tr class="list3">
		                        <td align="right" nowrap >IP</td>
		                         <td align="left"  nowrap>
		                              <html:text name="accountForm" property="user_vo.string(${accountForm.ip_addr})" size="20" maxlength="15" styleClass="text"/>    	      
		                         </td> 
		                       </tr> 
		                       </logic:notEqual>  
		                     </hrms:priv> 
		                                                     
		          <tr class="list3">
		            <td align="center" colspan="2" style="height:35px">
		         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.accountForm.target='_self';return (document.returnValue && ifqrbc());">
		            		<bean:message key="button.save"/>
			 	</hrms:submit>
		         	<hrms:submit styleClass="mybutton" property="b_update" onclick="document.accountForm.target='_self';validate('R','user_vo.string(username)','名称');return (document.returnValue && ifqrbc());">
		            		<bean:message key="button.reset.password"/>
			 	</hrms:submit>
			 	<hrms:priv func_id="0B4"> 
		                <logic:notEqual name="accountForm" property="ip_addr" value=""> 
			 	<hrms:submit styleClass="mybutton" property="b_updateip" onclick="document.accountForm.target='_self';validate('R','user_vo.string(username)','名称');return (document.returnValue && ifqrbc());">
		            		<bean:message key="button.reset.IP"/>
			 	</hrms:submit>	
			 	</logic:notEqual>  
		                </hrms:priv>	
		         	<hrms:submit styleClass="mybutton" property="br_return">
		            		<bean:message key="button.return"/>
			 	</hrms:submit>       
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
					  <!-- 修改密码不能输入 英文逗号字符 33720 wangb 20180105 -->
		              <li id='charRule' class="checkList">允许的符号:~ ! @ # $ % ^ & * ( ) + " ' </li>
		              <li id='spaceRule' class="checkList">不允许有空格</li>
		           </ul>
		  </div>
        </td>
      </tr>
   </table>
</html:form>
