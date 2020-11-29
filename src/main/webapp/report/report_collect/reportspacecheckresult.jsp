<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
	function download(){
		df.submit();
	}
//-->
</script>
<hrms:themes />
<body>
<br>
<div class="splitpage"> 
	<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
	<bean:write name="reportCollectForm" property="reportSpaceCheckResult" filter="false" />
	</table>
</div>
<br>
<FORM name="df" action="/report/report_collect/download.jsp" method="post">
<table width="60%" border="0" >
  <tr >
    <td align="center" >
	  <input type="button" value="<bean:message key="reportcheck.download"/>" Class="mybutton" onclick="download()">
	  <input type="button" name="b_add" value="<bean:message key="reportcheck.return"/>" class="mybutton" onClick="history.back();">	
      <input type="hidden" name="message" value="${reportCollectForm.reportSpaceCheckResult}"/>
    </td>
  </tr>   
</table>
</form>
</body>   


