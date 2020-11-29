<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
	function download(){
		df.target='_blank';
		df.submit();
	}
//-->
</script>
<body>
<div class="splitpage" style="margin-top: 6px;"> 
	<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
		<tr>
			<td align="center">
				<bean:write name="reportListForm" property="reportSpaceCheckResult_t" filter="false" />
			</td>
		</tr>
	</table>
</div>
<form name="df" action="download.jsp" style="margin-top:0px;" method="post">
	<table width="80%" border="0"  align="center">
	  <tr >
	    <td align="center" >
		  <logic:equal name="reportListForm" property="downLoadFlag" value="show">
		 	 <input type="button" value="<bean:message key="reportcheck.download"/>" Class="mybutton" onclick="download()">
	      	  <input type="hidden" name="message" value="${reportListForm.reportSpaceCheckResult}"/>
	      </logic:equal>
	      <input type="button" name="b_add" value="<bean:message key="reportcheck.return"/>" class="mybutton" style="margin-left: -6px;" onClick="history.back();">	
	    </td>
	  </tr>   
	</table>
</form>
</body>   


