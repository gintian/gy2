<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hjsj.hrms.actionform.performance.kh_result.*"%>
<script type="text/javascript">
<!--

function draw(charttype)
{
      khResultForm.action="/performance/kh_result/kh_result_overallrating.do?b_init=link&opt=2&charttype="+charttype;
      khResultForm.submit();
}
//-->
</script>
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
<% KhResultForm khResultForm=(KhResultForm)session.getAttribute("khResultForm");
   String wholeEval=khResultForm.getWholeEval();
   String descriptiveWholeEval=khResultForm.getDescriptiveWholeEval();

%>
<html:form action="/performance/kh_result/kh_result_overallrating">
<br><br>
<logic:equal value="0" name="khResultForm" property="wholeEval">
<logic:equal value="1" name="khResultForm" property="descriptiveWholeEval">
<div align='center'><font size='4' style="font-weight:bold">${khResultForm.viewsTitle}</font></div>
</logic:equal>
</logic:equal>
<logic:equal value="1" name="khResultForm" property="wholeEval">
<div align='center'><font size='4' style="font-weight:bold">${khResultForm.viewsTitle}</font></div>
</logic:equal>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
<logic:equal value="1" name="khResultForm" property="wholeEval">
	<tr>
		<td  class="RecordRow"  colspan='3' >
		   
		<Br>&nbsp;
		<a href='javascript:draw("20")'>饼图</a>
		&nbsp; 
		<a href='javascript:draw("11")'>直方图</a>
		<Br>&nbsp;
		</td>
	</tr>
	
	
	<tr>
		<td valign='top' colspan='2' height='10' width='15%' class="RecordRow"  >
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
					<tr>
						<td align='center' class="RecordRow_lrt common_border_color"  colspan='2' >
						<font style="font-weight:bold"><bean:message key="lable.performance.votecase"/></font>
						</td>
					</tr>
					<logic:iterate id="element" name="khResultForm"  property="sumRatingList" indexId="index">
					<tr>
						<td align='left' class="RecordRow_l common_border_color" width='55%' >&nbsp;<bean:write  name="element" property="dataName" filter="true"/></td>
						<td align='right' class="RecordRow_r common_border_color" width='45%' >&nbsp;<bean:write  name="element" property="dataValue" filter="true"/></td>
					</tr>
					</logic:iterate>	
			</table>
		</td>
	    <td  width='85%' align='center' class="RecordRow"  >
	    
	    	<hrms:chart name="khResultForm" title="" isneedsum="false" scope="session" legends="overallRatingList"
	    		 data=""  width="450" height="350" chart_type="${khResultForm.charttype}" biDesk="true">
			</hrms:chart>
	    	
	    
	    </td>
	</tr>
	</logic:equal>
	<logic:equal value="1" name="khResultForm" property="descriptiveWholeEval">
	<tr>
		<td  width='15%'  class="RecordRow" colspan='2' ><font style="font-weight:bold"><bean:message key="lable.performance.viewsandreviews"/></font></td>
	    <td   class="RecordRow"  >
	    	<br>
	    	<table width='100%'>
	    	<% int i=1;  %>
		    <logic:iterate id="element" name="khResultForm"  property="reviewsAndViewsList" indexId="index">
				<tr><td width='5%' valign='top' > &nbsp; <%=i%>.</td><td><bean:write  name="element"  filter="false"/></td></tr>
		    <% i++; %>
		    </logic:iterate>
		    
		    </table>
	    	&nbsp;
	    	<html:hidden name="khResultForm" property="planid"/>
		    <html:hidden name="khResultForm" property="object_id"/>
		    <html:hidden name="khResultForm" property="distinctionFlag"/>
	    </td>
	</tr>
		</logic:equal>
</table>


</html:form> 