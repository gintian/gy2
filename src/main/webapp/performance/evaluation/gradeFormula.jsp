<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="evaluation.js"></script>
<html:form action="/performance/evaluation/performanceEvaluation">
<script>
	function setDis(theObj)
	{
		if(theObj.checked)
		{
			if(theObj.value=='0')
				evaluationForm.procedureName.disabled=true;
			else if(theObj.value=='1')
				evaluationForm.procedureName.disabled=false;
		}
	}
	function saveExpr()
	{
	    var gradeFormulas ;
		for(var i=0;i<evaluationForm.gradeFormula.length;i++)
		{
			if (evaluationForm.gradeFormula[i].checked==true)
				gradeFormulas = evaluationForm.gradeFormula[i].value;
		}
		var isReCalcu='no';
		if(confirm(IS_RECALCU_FORMULA))
			isReCalcu='ok';
		var hashvo=new ParameterSet();
		hashvo.setValue("gradeFormula",gradeFormulas);
		hashvo.setValue("isReCalcu",isReCalcu);
		hashvo.setValue("planid",evaluationForm.planid.value);
		hashvo.setValue("procedureName",document.getElementById("procedureName").value);
		var request=new Request({method:'post',onSuccess:showresult,functionId:'9024000022'},hashvo);
	}
	function showresult(outparamters)
	{
		var isCorrect=outparamters.getValue("isCorrect");
		var isReCalcu=outparamters.getValue("isReCalcu");
		if(isCorrect==1)
		{
			var thevo=new Object();
			window.returnValue=isReCalcu;
			window.close();
		}
	}
</script>
<table width="100%" height="200" border="0" align="center">
  <tr> 
    <td width="90%" >
    <table border="0" align="center" width="96%">
    <tr><td>
    <fieldset align="center" style="width:100%;">
	<legend></legend> 
		<table width="100%" border="0">
        	<tr> 
          		<td> 
            		<html:radio name="evaluationForm" property="gradeFormula"  value="0" onclick="setDis(this);" />
						<bean:message key='jx.evalution.GradeFormula.default' />
            	</td>
        	</tr>
        	<tr> 
          		<td> 
            		<html:radio name="evaluationForm" property="gradeFormula"  value="1" onclick="setDis(this);" />
						<bean:message key='jx.evalution.GradeFormula.useprocedure' />							
            	</td>
        	</tr>
        	<tr> 
          		<td  align="center"> 
          			<table width="100%" border="0">
        				<tr> 
          					<td> 
          						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key='jx.evalution.GradeFormula.inputProcedureName' />:
								<html:text name="evaluationForm" property="procedureName" size='30' styleId="procedureName"></html:text>
          					</td>
        				</tr>
        			
        				<tr> 
          					<td> 
          						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key='jx.evalution.GradeFormula.Introductions' /><br>
          						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key='jx.evalution.GradeFormula.Introductions1' /><br>
          						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key='jx.evalution.GradeFormula.Introductions2' /><br>
          						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key='jx.evalution.GradeFormula.Introductions3' /><br>
          						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key='jx.evalution.GradeFormula.Introductions4' /><br>
          					</td>
        				</tr>	
      				</table>
          		</td>
        	</tr>
      </table>
      </fieldset>
      </td></tr>
      </table>
     
    </td>
    <td width="30%" align="center">
    	<table border="0" align="center">
    		<tr height="40">
    			<td align="center">
    				<input type="button" name="save" value="&nbsp;<bean:message key="button.ok"/>&nbsp;" class="mybutton" onclick="saveExpr()">&nbsp;&nbsp;&nbsp;
    			</td>
    		</tr>
    		<tr height="40">
    			<td align="center">
    				<input type="button" name="cancel" value="&nbsp;<bean:message key="button.cancel"/>&nbsp;" class="mybutton" onclick="window.close()">&nbsp;&nbsp;&nbsp;
    			</td>
    		</tr>
    		<tr height="80">
    			<td align="center">
    				
    			</td>
    		</tr>
    	</table>
    </td>
  </tr>
</table>
	<html:hidden name="evaluationForm" property="planid"/>
</html:form>
<script>
	if($F('gradeFormula')=="0")
		evaluationForm.procedureName.disabled=true;
</script>
	