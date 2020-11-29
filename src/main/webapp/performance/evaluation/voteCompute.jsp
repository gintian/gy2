<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/train/resource/trainResc.js"></script>
<script language='javascript'>
<% 
if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("close")){
	out.print("callBack_vote()");
}
%>

	function save()
	{
		evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_savevotecalcu=link&opt=close";
		evaluationForm.submit();
	}	
	
	function callBack_vote(){
		var thevo=new Object();
		thevo.flag="true";
		if(window.showModalDialog){
			parent.window.returnValue=thevo;
		}else {
			parent.parent.voteCompute2_ok(thevo);
		}
		closeWin();
	}
	
	function closeWin(){
		if(window.showModalDialog){
			parent.window.close();
		}else {
			parent.parent.voteCompute2_close();
		}
	}
</script>
<html:form action="/performance/evaluation/performanceEvaluation">
	
	<table width="90%" border="0" cellspacing="0" align="center" cellpadding="1" class="ListTable">
		<thead>
			<tr>
				<td colspan="3" align="left">
					<bean:message key='performance.evaluation.votestatis' />
				</td>
				<%
				int i = 0;
				%>			
			<tr>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="label.serialnumber" />
				</td>	
				<td align="center" class="TableRow" nowrap>
					<bean:message key="performance.implement.examinebodytype" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="label.kh.template.qz" />
				</td>			
			</tr>
		</thead>
			<logic:iterate id="element" name="evaluationForm" property="planbodylist" indexId="index">
			<%
					if (i % 2 == 0)
					{
			%>
			<tr class="trShallow">
				<%
						} else
						{
				%>
			
			<tr class="trDeep">
				<%
						}
						i++;
				%>
				<td align="right" class="RecordRow" nowrap>
			  		<%=i%>
	        	</td> 
	        	<td align="left" class="RecordRow" nowrap>
				 <bean:write name="element" property="name" filter="true"/>
				</td>
				<td align="right" class="RecordRow" nowrap>
					<%-- 确认的主体权重为0，且不允许修改 by 刘蒙 --%>
					<bean:define id="voterank" value="${element.map.voterank }"></bean:define>
					<bean:define id="isDisabled" value=""></bean:define>
					<logic:equal name="element" property="pbOpt" value="1">
						<bean:define id="isDisabled" value="true"></bean:define>
					</logic:equal>
				 	<html:text name="evaluationForm" property='<%="planbodylist[" + index + "].voterank"%>' disabled="${isDisabled }"
				 		onkeypress="event.returnValue=IsDigit(this);" onblur='isNumber(this);' />
				</td>				
			</tr>
		</logic:iterate>
		<tr>
			<td colspan="3" align="center" class="RecordRow">
					<bean:message key='performance.evaluation.totalDecimal' />
					<html:text name="evaluationForm" property="voteScoreDecimal" size="10" onkeypress="event.returnValue=IsDigit2(this);" onblur='isNumber(this);'/>
					&nbsp;&nbsp;
					<bean:message key='performance.evaluation.voteDecimal' />
					<html:text name="evaluationForm" property="voteDecimal" size="10" onkeypress="event.returnValue=IsDigit2(this);" onblur='isNumber(this);'/>
			</td>
		</tr>
	</table>
	<table width="90%" border="0" cellspacing="0" align="center" cellpadding="1" class="ListTable">
		<tr>
			<td  align="center">
	<input type='button' class="mybutton" 	onclick='save();' 	value='<bean:message key="button.ok"/>'/>
	<input type='button' class="mybutton" 	onclick='closeWin();' 	value='<bean:message key="button.cancel"/>'/>
			</td>
		</tr>
	</table>
</html:form>