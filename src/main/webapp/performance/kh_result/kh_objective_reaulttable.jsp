<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
 %>  

 <script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<SCRIPT LANGUAGE=javascript src="/performance/objectiveManage/objectiveCard/objectiveCard.js"></SCRIPT>
<style>
<!--
.RecordRow_Result {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
.RecordRow_self_locked,.TableRow_2rows_objective_last{
    position:static !important;
}
.RecordRow_self_locked_last,.TableRow_2rows_objective{
    position:static !important;
}
.TableRow_head_locked,.RecordRow_self_locked_whole{
    position:static !important;
}
-->
</style>
<script type="text/javascript">
var aclientHeight=document.body.clientHeight;
var IVersion=getBrowseVersion();
</script>
<script type="text/javascript">
<!--
if(IVersion==8){
  document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard_8.css\" rel=\"stylesheet\" type=\"text/css\">");
}else{
  document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard.css\" rel=\"stylesheet\" type=\"text/css\">");
}
//-->
</script>
<hrms:themes />
<html:form action="/performance/kh_result/kh_result_figures">
<table align="center" border="0" width="98%" cellpmoding="0" cellspacing="0" cellpadding="0">
<tr>
<td class="RecordRow" style="border:0px;">
<script language='javascript' >
        var theWidth = document.body.clientWidth-45;
		document.write("<div id=\"tbl-container\" class=\"framestyle0\" style='position:absolute;height:"+(aclientHeight-50)+"px;width:"+theWidth+"px'  >");
</script>

	 ${khResultForm.cardHtml}
	 
<script language='javascript' >
		document.write("</div>");
</script>
<style type="text/css">
#tbl-container table {
	border-collapse: collapse;
}
</style>
</td>
</tr>
</table>
</html:form>