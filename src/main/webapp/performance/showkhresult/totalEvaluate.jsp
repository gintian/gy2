<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<style>
.RecordRow_l {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

	height:22;
}

.RecordRow_r {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

	height:22;
}

.RecordRow_lrt {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	font-size: 12px;

	height:22;
}
</style>


<body  >
<html:form action="/performance/showkhresult/showTotalEvaluate">
<br><br>
<div align='center'><font size=2>${totalEvaluateForm.title}</font></div>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
	<tr>
		<td  class="RecordRow"  colspan='3' >
		
		<Br>&nbsp;
		<a href='/performance/showkhresult/showTotalEvaluate.do?b_query=link&planid=<%=request.getParameter("planid")%>&objectid=<%=request.getParameter("objectid")%>&type=5'>饼图</a>
		&nbsp; 
		<a href='/performance/showkhresult/showTotalEvaluate.do?b_query=link&planid=<%=request.getParameter("planid")%>&objectid=<%=request.getParameter("objectid")%>&type=11'>直方图</a>
		<Br>&nbsp;
		</td>
	</tr>
	
	
	<tr>
		<td valign='top' colspan='2' height='10' width='10%' class="RecordRow"  >
			<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" >
					<tr>
						<td align='center' class="RecordRow_lrt"  colspan='2' >
						得票情况
						</td>
					</tr>
					<logic:iterate id="element" name="totalEvaluateForm"  property="totalEvaluateInfoList" indexId="index">
					<tr>
						<td  class="RecordRow_l" width='50%' ><bean:write  name="element" property="dataName" filter="true"/></td>
						<td  class="RecordRow_r" width='50%' ><bean:write  name="element" property="dataValue" filter="true"/></td>
					</tr>
					</logic:iterate>	
			</table>
		</td>
	    <td  width='80%' align='center' class="RecordRow"  >
	    
	    	<hrms:chart name="totalEvaluateForm" title="" isneedsum="false" scope="session" legends="totalEvaluateInfoList" data=""  width="550" height="400" chart_type="${totalEvaluateForm.type}">
			</hrms:chart>
	    	
	    
	    </td>
	</tr>
	
	
	
	<tr>
		<td  width='10%'  class="RecordRow"  >评语及意见</td>
	    <td   class="RecordRow" colspan='2' >
	    	<br>
	    	<table width='100%'>
	    	<% int i=1;  %>
		    <logic:iterate id="element" name="totalEvaluateForm"  property="remarkList" indexId="index">
				<tr><td width='5%' valign='top' > &nbsp; <%=i%>.</td><td><bean:write  name="element"  filter="false"/></td></tr>
		    <% i++; %>
		    </logic:iterate>
		    
		    </table>
	    	&nbsp;
	    </td>
	</tr>
</table>
</html:form>
</body>
</html>
