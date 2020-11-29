<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.report.auto_fill_report.ReportListForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script type="text/javascript">
<!--
	function download(){
		df.target='_blank';
		df.submit();
	}
	
	function cancel(){
		history.back();
	}
	
//-->
</script>
<body>
	<!-- <div class="splitpage" > -->
	<div style="margin-top: 6px;text-align: center;">
			<bean:write name="reportListForm" property="reportInnerCheckResult_t" filter="false" />
	</div>
	<form name="df" action="download.jsp" style="margin-top:0px;" method="post">
		<table width="75%" align="center" border="0">
		  <tr align="center">
		    <td align="center" >
			  <logic:equal name="reportListForm" property="downLoadFlag" value="show">
			 	 <input type="button" value="<bean:message key="reportcheck.download"/>" Class="mybutton" onclick="download()">
		      	  <input type="hidden" name="message" value="${reportListForm.reportInnerCheckResult}"/>
		      </logic:equal>
		      <input type="button" name="b_add" value="<bean:message key="reportcheck.return"/>" class="mybutton" style="margin-left: -6px;" onClick="history.back();">	
		    </td>
		  </tr>   
		</table>
	</form>
</body>
