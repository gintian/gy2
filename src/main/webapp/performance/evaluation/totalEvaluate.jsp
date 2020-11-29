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

function change()
{
	evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_totalEvaluate=link";
	evaluationForm.submit();
	//strurl="/performance/evaluation/performanceEvaluation.do?b_showCard=link`object_id="+object_id+"`scoreExplainFlag="+flag;
	//iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	//window.showModalDialog(iframe_url,arguments,"dialogWidth=800px;dialogHeight=600px;resizable=no;scroll=no;status=no;"); 
}
function total_close(){
	if(!window.showModalDialog){
		var win = parent.parent.Ext.getCmp('totalEvaluate_win');
   		if(win) {
    		win.close();
   		}
	}
	parent.window.close();
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
			<table align="center" style="width:780px;">
				<tr><td>
					<bean:message key="lable.appraisemutual.examineobject"/>
					<hrms:optioncollection name="evaluationForm" property="evaluate_object_list" collection="list" />
					<html:select name="evaluationForm" property="totalevaluateObject" size="1" style="width:152px;" onchange="change();">
						<html:options collection="list" property="dataValue" labelProperty="dataName"/>
					</html:select>
				</td></tr>	
				
				<tr><td>
				    <textarea  style="width:780px;height:450px;">${evaluationForm.evaluateHtml}</textarea>
				 </td></tr>
			</table>
		<table  width="50%" align="center">
          <tr>
            <td align="center">
            	<input type="button" class="mybutton" name="button" value="<bean:message key="button.close"/>" onclick="total_close();"	/>
            </td>
          </tr>          
		</table>
	   </html:form>
  </body>
</html>
