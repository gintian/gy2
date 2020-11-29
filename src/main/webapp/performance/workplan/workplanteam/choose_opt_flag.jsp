<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
%>
<HTML>
<HEAD>
<TITLE></TITLE>
<hrms:themes />
<link href="/css/xtree.css" rel="stylesheet" type="text/css" > 
<script type="text/javascript">
	function ret(){
		var content = document.getElementById("contentId").value;
		var thevo=new Object();
	    thevo.flag="true";
	    thevo.spContent = content;
	    window.returnValue=thevo;
		window.close();
	}
</script>
</HEAD>
<body style="text-align: center;"> 
<table  border="0" align="center"> 
<tr>
	<td>							
		<fieldset style="width:80%">
			<legend>
				<bean:message key='kq.register.overrule' />
			</legend>
			<table  border="0" align="center" width="345">  
				<tr>
					
					<td align='center' height="50">
						<textarea id="contentId" rows="9" cols="36" >同意</textarea><br><br>							
					</td>
					
				</tr>
			</table>
		</fieldset>											
	</td>
</tr>
<tr>
   <td align='center' style="height:35px">
			<input type="button" class="mybutton" value='<bean:message key="button.ok"/>' onclick="ret();window.close();"/>&nbsp;
            <input type="button" class="mybutton" value='<bean:message key="button.cancel"/>' onclick="javascript:window.close()"/>	
   </td>
</tr>
</table>
<BODY>
</HTML>


