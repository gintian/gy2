<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
 <%
      int ti = session.getMaxInactiveInterval();
    %>
<script type="text/javascript">
<!--
	function confirmsave(){
	var password1=configsysForm.password1.value;
	var password2=configsysForm.password2.value;
	if(password1!=password2){
	alert('<bean:message key="errors.sys.newpassword"/>');
	return;
	}
	if(window.confirm('<bean:message key="config.sys.info"/>\n\r'+getinfo())){
	
	    if(window.confirm('<bean:message key="config.sys.info.restart"/>')){
	   		window.close();
	    	jindu();
			configsysForm.action="/system/setup/configsys.do?b_update=link";
			configsysForm.submit();
		}
	}
	}
	function getinfo(){
	var sst= configsysForm.sst.value;
	var validateflag=configsysForm.validateflag.checked;
	var password1=configsysForm.password1.value;
	if(validateflag){
	validateflag='<bean:message key="general.defini.show"/>';
	}
	else{
	validateflag='<bean:message key="general.defini.nshow"/>';
	}
	var scrollwelcome=configsysForm.scrollwelcome.checked;
	if(scrollwelcome){
	scrollwelcome='<bean:message key="general.defini.show"/>';
	}else{
	scrollwelcome='<bean:message key="general.defini.nshow"/>';
	}
	var hjserverurl=configsysForm.hjserverurl.value;
	var hjserverport=configsysForm.hjserverport.value;
	var dbtype =configsysForm.dbtype.value;
	var dburl=configsysForm.dburl.value;
	var dbport=configsysForm.dbport.value;
	var dbuser=configsysForm.dbuser.value;
	var dbpassword=configsysForm.dbpassword.value;
	var message="";
	message=message+'<bean:message key="config.sys.info.sessiontime"/> '+sst+'\r\n';
	message=message+'<bean:message key="config.sys.info.showvalidate"/> '+validateflag+'\r\n';
	message=message+'<bean:message key="config.sys.info.scrollwelcome"/> '+scrollwelcome+'\r\n';
	message=message+'<bean:message key="config.sys.info.hjserverurl"/> '+hjserverurl+'\r\n';
	message=message+'<bean:message key="config.sys.info.hjserverport"/> '+hjserverport+'\r\n';
	message=message+'<bean:message key="config.sys.info.dbtype"/> '+dbtype+'\r\n';
	message=message+'<bean:message key="config.sys.info.dburl"/> '+dburl+'\r\n';
	message=message+'<bean:message key="config.sys.info.dbport"/> '+dbport+'\r\n';
	message=message+'<bean:message key="config.sys.info.dbuser"/> '+dbuser+'\r\n';
//	if(password1.length<1){
//	message=message+'<bean:message key="label.new.password"/> '+dbpassword+'\r\n';
//	}else{
//	message=message+'<bean:message key="label.new.password"/> '+password1+'\r\n';
//	}
	return message;
	}
	function jindu(){
	var waitInfo=eval("wait");
	waitInfo.style.display="block";
	window.close();
	}
	function back(){
		window.close();
	}
//-->
</script>
<html:form action="/system/setup/configsys"> 
<div id='wait' style='position:absolute;top:350;left:350;display:none;overflow: auto;'>
		<table border="1" width="40%" cellspacing="0" cellpadding="4" style="border-collapse: collapse" bgcolor="#F7FAFF" height="87" align="center">
			<tr>
				<td bgcolor="#057AFC" style="font-size:12px;color:#ffffff" height=24>
					<bean:message key="config.sys.info.message"/>：
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center nowrap>
					<bean:message key="config.sys.info.resterinfo"/>					
				</td>
			</tr>
		</table>
	</div>
	<br/><br/><br/><br/><br/><br/>
	<logic:equal value="1" name="configsysForm" property="inflag">
	<hrms:tabset  width="60%" height="300" name="aaa" type="false">
	<hrms:tab name="a" label="基础参数" visible="true">     
	<br/>  
	<table>
	<tr>
	<td>
	<bean:message key="config.sys.info.sessiontime"/>
	</td>
	<td>
	
	<input type="text" name="sst" value="<%=ti/60%>"/>
	</td>
	</tr>
	<tr>
	<td algin="right">
	<bean:message key="config.sys.info.showvalidate"/>
	</td>
	<td>
	<logic:equal value="false" name="configsysForm" property="validateflag">
	<input type="checkbox" name="validateflag" />
	</logic:equal>
	<logic:equal value="true" name="configsysForm" property="validateflag">
	<input type="checkbox" name="validateflag"  checked="checked"/>
	</logic:equal>
	</td>
	</tr>
	<tr>
	<td algin="right">
	<bean:message key="config.sys.info.scrollwelcome"/>
	</td>
	<td>
	<logic:equal value="false" name="configsysForm" property="scrollwelcome">
	<input type="checkbox" name="scrollwelcome" />
	</logic:equal>
	<logic:equal value="true" name="configsysForm" property="scrollwelcome">
	<input type="checkbox" name="scrollwelcome"  checked="checked"/>
	</logic:equal>
	</td>
	</tr>
	<tr>
	<td>
	<bean:message key="config.sys.info.hjserverurl"/>
	</td>
	<td><input type="text" name="hjserverurl" value="${configsysForm.hjserverurl}"/>
	</td>
	</tr>
	<tr>
	<td>
	<bean:message key="config.sys.info.hjserverport"/>
	</td>
	<td>
	<input type="text" name="hjserverport" value="${configsysForm.hjserverport}"/>
	</td>
	</tr>
	</table>
	</hrms:tab>
	<hrms:tab name="b" label="数据库参数" visible="true">      
	<br/> 
	<table>
	<tr>
	<td>
	<bean:message key="config.sys.info.dbtype"/>
	</td>
	<td>
	<bean:write name="configsysForm" property="selstr" filter="false"/>
	
	</td>
	</tr>
	<tr>
	<td>
	<bean:message key="config.sys.info.dbname"/>:
	</td>
	<td>
	<input type="text" name="dbname" value="${configsysForm.dbname}"/>
	</td>
	</tr>
	<tr>
	<td>
	<bean:message key="config.sys.info.dburl"/>
	</td>
	<td>
	<input type="text" name="dburl" value="${configsysForm.dburl}"/>
	</td>
	</tr>
	<tr>
	<td>
	<bean:message key="config.sys.info.dbport"/>
	</td>
	<td>
	<input type="text" name="dbport" value="${configsysForm.dbport}"/>
	</td>
	</tr>
	<tr>
	<td>
	<bean:message key="config.sys.info.dbuser"/>
	</td>
	<td>
	<input type="text" name="dbuser" value="${configsysForm.dbuser}"/>
	</td>
	</tr>
	<tr>
	<td>
	<bean:message key="label.sys.oldpwd"/>：
	</td>
	<td>
	<input class="shuoming" type="password" name="dbpassword" value="${configsysForm.dbpassword}" disabled="true"/>
	</td>
	</tr>
	<tr>
	<td>
	<bean:message key="label.sys.newpwd"/>：
	</td>
	<td>
	<input class="shuoming" type="password" name="password1" value=""/>
	</td>
	</tr>
		<tr>
	<td>
	<bean:message key="label.sys.newokpwd"/>：
	</td>
	<td>
	<input class="shuoming" type="password" name="password2" value=""/>
	</td>
	</tr>
	</table>
	</hrms:tab>
	</hrms:tabset>
	<input type="hidden" name="flag" value="${configsysForm.flag}"/>
	<table width="50%" align="center">
		<tr>
			<td align="center">
					
					<button name="bu_update" class="mybutton" onclick="confirmsave();"><bean:message key="button.ok" /></button>
					&nbsp;
					<button name="bu_update" class="mybutton" onclick="back();"><bean:message key="config.sys.setup.logonout" /></button>
			</td>
		</tr>
	</table>
</logic:equal>
<logic:notEqual value="1" name="configsysForm" property="inflag">
<table width="40%" align="center">
<tr>
<td>
<bean:message key="error.user.password"/>
</td>
</tr>
<tr>
<td>
<hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="button.return" />
					</hrms:submit>
</td>
</tr>
</table>
</logic:notEqual>

</html:form>

  	 


    