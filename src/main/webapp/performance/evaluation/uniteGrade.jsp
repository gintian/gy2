<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/dict.js"></script> 
<script language="JavaScript" src="/performance/evaluation/evaluation.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.evaluation.EvaluationForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.utils.PubFunc,
				 com.hrms.hjsj.sys.Des" %>
<style>

.myfixedDiv
{ 
	overflow:auto; 
	width:expression(document.body.clientWidth-10);
	height:expression(document.body.clientHeight-130);
	BORDER-BOTTOM: #94B6E6 0pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 0pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
} 
.TEXT_NB 
{
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	
}
.myfixedDiv2
{ 
	overflow:auto; 
	width:100%; 
	BORDER-BOTTOM: #94B6E6 0pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 0pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
} 
.RecordUnitGrade 
{
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	font-size: 12px;
	margin-top:-1px;
	height:22;
}
.RecordRowUnitGrade 
{
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}
</style>
<script language='javascript'>
<% 
	EvaluationForm myForm=(EvaluationForm)session.getAttribute("evaluationForm");
		String planid=myForm.getPlanid();
		planid=PubFunc.encryption(planid);
%>
	function go_back()
	{
		window.parent.location="/performance/evaluation/performanceEvaluation.do?b_int=link&plan_id=<%=planid%>";
	}
</script>
<% 
	ArrayList pointList = (ArrayList)myForm.getPointList();
%>
<html:form action="/performance/evaluation/performanceEvaluation1">
	<br>
	<table border="0" cellspacing="0" align="center" width="99%"  cellpadding="0">
		<tr>
			<td  nowrap class="RecordRowUnitGrade">
				<div class="myfixedDiv">
					<table width="100%" border="0" cellspacing="0" align="center" id='a_table'
						cellpadding="0" class="ListTableF">
						<tr class="fixedHeaderTr">	
						<%
							FieldItem fielditem = DataDictionary.getFieldItem("E0122");
						%>					
							<logic:equal name="evaluationForm" property="object_type" value="2">
								<td align="center" class="TableRow" nowrap>
									<bean:message key="b0110.label" />
								</td>
								<td align="center" class="TableRow" nowrap>										         
								 	<%=fielditem.getItemdesc()%>
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="e01a1.label"/>
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="hire.employActualize.name"/>
								</td>
							</logic:equal>
							<logic:notEqual name="evaluationForm" property="object_type" value="2">
								<td align="center" class="TableRow" nowrap>
									<bean:message key="label.query.unit" />|<%=fielditem.getItemdesc()%>
								</td>								
							</logic:notEqual>		
							<%
								for(int j=0;j<pointList.size();j++)
								{
									LazyDynaBean abean = (LazyDynaBean)pointList.get(j);
									String pointname = (String)abean.get("pointname");
									out.print(" <td align='center' class='TableRow' nowrap>"+pointname+"</td>");
								}
								int i = 0;
							%>	
						</tr>
						<hrms:extenditerate id="element" name="evaluationForm"
							property="rateListForm.list" indexes="indexes"
							pagination="rateListForm.pagination" pageCount="50"
							scope="session">
							<%
									if (i % 2 == 0)
									{
							%>
							<tr class="trShallow"
								onclick='tr_onclick(this,"#F3F5FC");'>
								<%
										} else
										{
								%>
							
							<tr class="trDeep"
								onclick='tr_onclick(this,"#E4F2FC");'>
								<%
										}
										i++;
								%>
							<logic:equal name="evaluationForm" property="object_type" value="2">
								<td align="left" class="RecordRow" nowrap>
									&nbsp;<bean:write name="element" property="b0110" filter="true" /> 				
								</td>
								<td align="left" class="RecordRow" nowrap>
									&nbsp;<bean:write name="element" property="e0122" filter="true" /> 				
								</td>
								<td align="left" class="RecordRow" nowrap>
									&nbsp;<bean:write name="element" property="e01a1" filter="true" /> 				
								</td>
								<td align="left" class="RecordRow" nowrap>
										&nbsp;<bean:write name="element" property="a0101" filter="true" /> 		
								</td>	
							</logic:equal>
							<logic:notEqual name="evaluationForm" property="object_type" value="2">
								<td align="left" class="RecordRow" nowrap>
										&nbsp;<bean:write name="element" property="a0101" filter="true" /> 		
								</td>								
							</logic:notEqual>
							<%
							    LazyDynaBean dataBean=(LazyDynaBean)pageContext.getAttribute("element");
								int index=((Integer)pageContext.getAttribute("indexes")).intValue();
								String object_id=(String)dataBean.get("object_id");
								String plan_id=(String)dataBean.get("plan_id");
								String EvalOutLimitStdScore = (String)dataBean.get("EvalOutLimitStdScore");
								for(int j=0;j<pointList.size();j++)
								{
									LazyDynaBean pointBean = (LazyDynaBean)pointList.get(j);
									String pointid = (String)pointBean.get("point_id");
									String maxScore = (String)pointBean.get("MaxScore");
									String pointPriv = 	(String)dataBean.get(pointid+"_priv");		
									String score = 	(String)dataBean.get("C_"+pointid);		
									out.println("<td align='center' class='RecordRow' nowrap>");
									if(pointPriv.equals("1"))				
										out.print("<input type='text' value='"+score+"' onblur=\"testRange('"+object_id+"',"+plan_id+",'"+pointid+"',"+maxScore+",this,'"+EvalOutLimitStdScore+"')\" onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'   name='rateList["+index+"].C_"+pointid+"'  class='TEXT_NB' size='8'  />");
									else
									{
										out.print("<input type='hidden' value='"+score+"' name='rateList["+index+"].C_"+pointid+"'/>");
										out.print(score);
									}
									out.println("</td>");
								}
							%>	
							</tr>
						</hrms:extenditerate>
					</table>
				</div>
				<div class="myfixedDiv2">
					<table width="100%"	class="RecordUnitGrade">
						<tr>
							<td valign="bottom" align="left" class="tdFontcolor">
								第
								<bean:write name="evaluationForm"
									property="rateListForm.pagination.current" filter="true" />
								页 共
								<bean:write name="evaluationForm"
									property="rateListForm.pagination.count" filter="true" />
								条 共
								<bean:write name="evaluationForm"
									property="rateListForm.pagination.pages" filter="true" />
								页
							</td>
							<td align="right" nowrap class="tdFontcolor">
								<p align="right">
									<hrms:paginationlink name="evaluationForm" nameId="rateListForm"  property="rateListForm.pagination" >
									</hrms:paginationlink>
							</td>
						</tr>
					</table>				
				</div>
			</td>
		</tr>
	</table>
	<table border="0" cellspacing="0" align="center" width="90%"  cellpadding="0">
		<tr>
			<td align="center" nowrap style="height:35px">				
   				<input type='button' class="mybutton" property="b_return" onclick='go_back()' value='<bean:message key="button.return"/>' />   
			</td>
		</tr>		
	</table>
</html:form>
