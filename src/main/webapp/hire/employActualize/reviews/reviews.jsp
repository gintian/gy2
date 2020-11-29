<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<script language="JavaScript" src="/js/function.js"></script>

<script language='javascript'>
function viewSubmit(){
	
	if(trim(document.reviewsForm.title.value).length==0)
	{
		alert(TITLE_IS_MUST_FILL+"！");
		return;
	}
	if(trim(document.reviewsForm.content.value).length==0)
	{
		alert(CONTENT_IS_MUST_FILL+"！");
		return;
	}
	if(IsOverStrLength(document.reviewsForm.title.value,60))
	{
			alert(TITLE_LENGTH_IS_NOT_LONGER_THAN_THIRTY+"！");
			return;
    }
    if(IsOverStrLength(document.reviewsForm.content.value,400))
	{
			alert(CONTENT_LENGTH_IS_NOT_LONGER_THAN_TWOH+"！");
			return;
    }
	reviewsForm.action="/hire/employActualize/reviews/reviews.do?b_add=add&add=1";
	reviewsForm.submit();
}
function clearValue()
{
  document.reviewsForm.content.value="";
  document.reviewsForm.title.value="";
}
<%
	if(request.getParameter("add")!=null&&request.getParameter("add").equals("1"))
	{
		out.println("returnValue=1;");
		out.print("window.close();");
		
	}
%>
function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
}
</script>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<%  
}
%>
<base id="mybase" target="_self">
<body onload="setTPinput()">
<html:form action="/hire/employActualize/reviews/reviews">
<table width="90%" border="0" cellspacing="0"  cellpadding="0" style="margin-top:3px;">
	<tr>
		<td><h3>
		<bean:message key="hire.new.comment"/>
		</h3></td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
</table>

<table width="90%" border="0" cellspacing="1"  cellpadding="1" style="margin-top:-10px;">
	<tr>
		<td><html:image src='/images/icon_speaker.gif' /></td>
		<td><bean:message key="hire.message"/></td>
		<td>&nbsp;</td>
	</tr>
</table>
<table width="540px" border="0" cellspacing="0"  cellpadding="0">
<tr>
<td class="RecordRow">
<table align="center">
	<tr>
		<td width="20%" align="right"><bean:message key="hire.title"/>&nbsp;&nbsp;</td>
		<td>
			<html:text name="reviewsForm" property="title"  size="46">
			</html:text>
		</td>
	</tr>

	<tr>
		<td width="20%" align="right"><bean:message key="hire.level"/>&nbsp;&nbsp;</td>
		<td>
		<html:select name="reviewsForm" property="level" size="1">
                              <html:optionsCollection property="levelList" value="dataValue" label="dataName"/>
        </html:select> 
		</td>
	</tr>

	<tr>
		<td width="20%" valign='top' align="right">  <bean:message key="hire.content"/>&nbsp;&nbsp;</td>
		<td>
			<html:textarea name="reviewsForm" property="content" cols="50" rows="8">
			</html:textarea>
		</td>
	</tr>
	
</table>
</td>
</tr>
</table>
<table width="90%" border="0" cellspacing="0"  cellpadding="0" align="center" style="margin-top:5px;">
<tr>
		<td align='center'>

			<button class="mybutton" name="dsf" onclick="viewSubmit();" style="margin-left:0px;">
				<bean:message key="button.save"/>
			</button>
		<input type="button" class="mybutton" onclick="clearValue();" value="<bean:message key="button.clear"/>" style="margin-left:0px;"/>
		<input type="button" class="mybutton" value="<bean:message key="button.close"/>" onclick="window.close();" style="margin-left:0px;"/>
		</td>
	</tr>
	</table>
	<div id='a0100Info'>
	
	</div>
	
	<html:hidden name="reviewsForm" property="info_id"/>
	<script language="JavaScript">
 		 var infos=dialogArguments;
 		 var obj=eval("a0100Info");
 		 obj.innerHTML="<input type='hidden' name='a0100' value='"+infos[0]+"' />";
 		 
 		 
 	</script> 
</html:form>
</body>