<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes />
				 				 
<html>
<head>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language='javascript'>

var IVersion=getBrowseVersion();

if(IVersion==8)
{
	document.writeln("<link href=\"/performance/evaluation/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
	document.writeln("<link href=\"../../css/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}

function showExplain(reasonsWhole)
{
	var infos=new Array();
	infos[0]=reasonsWhole;
	strurl="/performance/evaluation/showExplain.jsp"
	iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
	window.showModalDialog(iframe_url,infos,"dialogWidth=400px;dialogHeight=300px;resizable=no;scroll=no;status=no;");
}

</script>
</head>

<style>
	body {TEXT-ALIGN: center;}
	div#tbl-container {	
	width:700;
	height:450;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
</style>
  
	<body>
		<html:form action="/performance/evaluation/performanceEvaluation">
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
				
				<tr><td width="100%">
				 
				    ${evaluationForm.cardHtml}
				 
				 </td></tr>
			</table>
		
	   </html:form>
  </body>
</html>
