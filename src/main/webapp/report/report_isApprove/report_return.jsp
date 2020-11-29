<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.report.report_isApprove.Report_isApproveForm" %>
<%
	Report_isApproveForm report_isApproveForm = (Report_isApproveForm)session.getAttribute("report_isApproveForm");
%>

<HTML>
<HEAD>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" > 
<script type="text/javascript">
	function ret(){
		var rdos = document.getElementById("content2");
		if(rdos.value==""){
			alert("请输入您的意见！");
			return;
		}
		var s2 = rdos.value;
        while(s2.indexOf("\r\n")!=-1) 
	     { 
	    	s2=s2.replace('\r\n','&&');
	     }
	     
	     while(s2.indexOf("\"")!=-1) 
	     { 
	    	s2=s2.replace('\"','“');
	     }
	     
	     while(s2.indexOf("\'")!=-1) 
	     { 
	    	s2=s2.replace('\'','‘');
	     }
		returnValue = s2;
		parent.win1 = s2;
		windowClose();
	}
	
	function windowClose(){
		var win = parent.Ext.getCmp("reportApprove");
		if(win)
			win.close();
		else
			window.close();
	}
</script>
</HEAD>
<body style="text-align: center;"> 
<html:form action="/report/report_isApprove/reportIsApprove">
 <table>
 <div style="width: 100%; margin-top:2px; font-weight:bold; height: 19px; background-color: #F4F7F7; text-align: left;border:1px">&nbsp;&nbsp;&nbsp;审批意见：</div>
 <tr>
 <td>
<div>
	<html:textarea name="report_isApproveForm"  property="content" cols="80" rows="15"  disabled="true"></html:textarea>
</div>
</td>
</tr>
<tr>
<td align="center">
	<input type="button" class="mybutton" value='<bean:message key="button.ok"/>' onclick="ret();"/>&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="button" class="mybutton" value='<bean:message key="button.cancel"/>' onclick="windowClose()"/>
</td>
</tr>
<tr>
<td>
<%if(report_isApproveForm.getFlag().equals("2")){ %>
<span style="width: 100%; margin-top:4px; font-weight:bold; height: 19px;line-height: 19px; background-color: #F4F7F7; text-align: left;border:1px">&nbsp;&nbsp;&nbsp;批准意见：</span>
<%}else if(report_isApproveForm.getFlag().equals("1")){ %>
<span style="width: 100%; margin-top:4px; font-weight:bold; height: 19px;line-height: 19px;background-color: #F4F7F7; text-align: left;border:1px">&nbsp;&nbsp;&nbsp;驳回意见：</span>
<%} %>
<div>
	<html:textarea name="report_isApproveForm"  property="content2" styleId="content2" cols="80" rows="15"></html:textarea>
</div>
</td>
</tr>
</table>
</html:form>
<BODY>
</HTML>


