<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<script language="JavaScript" src="/js/rec.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%

    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag = "";
    if (userView != null) {
        bosflag = userView.getBosflag();
    }
%>
 <hrms:themes />
  <%
     if ("hcm".equals(bosflag)) {
 %>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%
    }
%>

<html:form action="/gz/gz_accounting/setapp_date"> 
<table align="center"  valign="top"  width="390" class="setdatemragin">
<tr>
<td>

  <fieldset  align="center" style="width:100%;padding-top:8px;padding-bottom:8px">
   <legend><bean:message key="label.login.appdate"/></legend>
   
	<table border="0" cellspacing="0" align="center" cellpadding="0">
	<tr>
	<td align="center">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
 		  <td id="datepnl" >
    	  </td>
		</tr>
		</table>    
    </td>     
	</tr>
	</table>
	</fieldset>
</td>
</tr>
	<tr>
	  <td>
		 <table align="center">
    		<tr >
		  	  <td>
				<button name="new" Class="mybutton" onclick='getdatevalue();'><bean:message key="button.ok"/></button>
				<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  </td>
	    	</tr>
    	 </table>
	   </td>
	</tr>
</table>
</html:form>
<script type="text/javascript">
<!--
	createCalendarPanel($('datepnl'));
	var d= new Date();  
	var appdate='${accountingForm.appdate}';
	//alert(appdate);
	var ymd=appdate.split(".");
	d.setFullYear(ymd[0],ymd[1]-1,ymd[2]);
	setCalendarDate(d);
	function isSuccess()
	{
		
		top.returnValue="aaa";
		top.close();
	}
	function getdatevalue()
	{
		var value;
		value=_calendarControl_p.todayYear+".";//+_calendarControl_p.todayMonth+"."+_calendarControl_p.todayDay;
		if(_calendarControl_p.todayMonth<10)
		  value=value+"0";
		value=value+_calendarControl_p.todayMonth+".";  
		if(_calendarControl_p.todayDay<10)
		  value=value+"0";
		value=value+_calendarControl_p.todayDay  
		
	    if(value.length>0)
	    {
	    	SetAppdateCookie("appdate",value);
	    }    
		
		
		
		var hashvo=new ParameterSet();
		hashvo.setValue("appdate",value);
   		var request=new Request({method:'post',asynchronous:false,onSuccess:isSuccess,functionId:'3020070108'},hashvo);
	}
//-->
</script>

  