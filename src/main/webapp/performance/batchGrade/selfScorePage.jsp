<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
    

  </head>
   <script language="JavaScript" src="/performance/singleGrade/grade.js"></script>
<hrms:themes />
    <style>

.ListTable_self {
    BACKGROUND-COLOR: #FFFFFF;
    border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
    
 }   


</style>
<script language='javascript' >
	
function showWindow(plan_id,object_id,mainbody_id)
{
   
	var win=open("/performance/markStatus/markStatusList.do?b_edit3=edit&opt=read&planID="+plan_id+"&objectID="+object_id+"&mainbodyID="+mainbody_id,"info");
}
</script>
  <body>
  <html:form action="/selfservice/performance/batchGrade">
   
<div id="date_panel"></div>
<table border='0'><tr><td>
${batchGradeForm.objectSelfScoreHtml}
</td></tr></table>
   
   
   </html:form>
   
  </body>
</html>
