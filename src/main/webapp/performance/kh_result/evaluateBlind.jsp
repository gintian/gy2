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
	document.writeln("<link href=\"../../css/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
	document.writeln("<link href=\"../../css/locked-column-new.css\" rel=\"stylesheet\" type=\"text/css\">");
}
</script>
</head>
  
<body>
	<html:form action="/performance/evaluation/performanceEvaluation">
		${khResultForm.blindHtml}
   </html:form>
  </body>
</html>
