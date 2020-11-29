<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<SCRIPT LANGUAGE=javascript src="/performance/perAnalyse/perAnalyse.js"></SCRIPT>
<script type="text/javascript" src="/js/constant.js"></script>  
<title>Insert title here</title>
<style>
div {
BORDER-BOTTOM: #94B6E6 1pt solid; 
BORDER-LEFT: #94B6E6 1pt solid; 
BORDER-RIGHT: #94B6E6 1pt solid; 
BORDER-TOP: #94B6E6 1pt solid ; 	
width: 800px;
height: 600px;
overflow: auto;
}

</style>
<hrms:themes />
</head>
<body>
<html:form action="performance/perAnalyse">
<br>
<fieldset align="center">
	<table width="100%">
	<tr>
	<td align='left' ><bean:message key="jx.khplan.cycle"/>：
		  <html:select name="perAnalyseForm" property="period"  onchange='selectPlan()'   size="1">
								   <html:optionsCollection property="periodList" value="dataValue" label="dataName"/>
		</html:select>  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<bean:message key="menu.performance.template"/>：
		  <html:select name="perAnalyseForm" property="stencilId"  onchange='selectPlan()'     size="1">
								   <html:optionsCollection property="stencilList" value="dataValue" label="dataName"/>
		</html:select>  
	</td>
	</tr>
	<tr><td >
	<div class="common_border_color">
		<table width="100%" border="0" cellspacing="0"  id='a_table' align="left" cellpadding="0" class="ListTable">
	   	  <thead>
	        <tr  style="position:relative;top:expression(this.offsetParent.scrollTop-1);"  >
		         <td align="center" width='8%' class="TableRow_right" nowrap><input type="checkbox" name="selbox"
										onclick="batch_select(this, 'plan_id');"></td>
				<td align="center" width='10%'  class="TableRow" nowrap>
								<bean:message key="lable.zp_plan.plan_id" />
				</td>
		         <td align="center" width='20%' class="TableRow" nowrap><bean:message key="kh.field.fieldname"/></td>
		         <td align="center" width='20%' class="TableRow" nowrap><bean:message key="menu.performance.template"/></td>
		         <td align="center" width='12%' class="TableRow" nowrap><bean:message key="jx.khplan.cycle"/></td>
		         <td align="center" width='30%' class="TableRow_left" nowrap><bean:message key="jx.khplan.timeframe"/></td>
		  	</tr>
		  </thead>
		  <%  int i=0; %>
   	  
	   	  <logic:iterate id="element" name="perAnalyseForm" property="perPlanList" >
	   	  	<% i++;
	   	  	   if(i%2==1){ %>
	   	  	   <tr class='trShallow' >
	   	  	   <% } else { %>	   
	   	  	   	<tr class='trDeep'  >
	   	  	   <% } %>	
		 		   <td align="center" class="RecordRow_right" nowrap>
		 		  		<!-- <logic:equal name="element" property="select" value="1">  checked  </logic:equal> -->
					  	<input type='checkbox' name='plan_id'  value='<bean:write name="element" property="plan_id" filter="true"/>'  />
			       </td>   
			       <td align="left" class="RecordRow" nowrap>
					  	&nbsp;<bean:write name="element" property="plan_id" filter="true"/>
			       </td>   
		 		   <td align="left" class="RecordRow" nowrap>
		 			<bean:write name="element" property="name" filter="true"/>
		 		   </td>
		 			<td align="left" class="RecordRow" nowrap>
		 			<bean:write name="element" property="templateName" filter="true"/>
		 		   </td>
		 		   <td align="left" class="RecordRow" nowrap>
		 			<bean:write name="element" property="cycle" filter="true"/>
		 		   </td>
		 		   <td align="left" class="RecordRow_left" nowrap>
		 			<bean:write name="element" property="timeScope" filter="true"/>
		 		   </td>
		     	</tr>
		 	</logic:iterate>
		 </table>
	</div>
	</td></tr>
	<tr><td colspan='2' align='center' >
		<input type='button'  class="mybutton" value='<bean:message key="reporttypelist.confirm"/>'  onclick='setPlans(${perAnalyseForm.busitype})'   />
		<input type='button'  class="mybutton" value='<bean:message key="kq.register.kqduration.cancel"/>'  onclick='parent.window.close()'  />
	</td></tr>
	</table>


</fieldset>

</html:form>
</body>
</html>
<script>
	var busitype = '${perAnalyseForm.busitype}';

	var plansSel =  getCookie('plansSel');
	if(busitype!=null && busitype.length>0 && busitype=='1')
		plansSel =  getCookie('modalPlansSel');
	<%
	//单指标趋势分析 如果没有在高级中确定过计划 高级首次进入哪些计划被选中
	if( request.getParameter("cookiFlag")!=null && request.getParameter("cookiFlag").equalsIgnoreCase("query0")){%>
		 plansSel =  getCookie('plansSel2');
		 if(busitype!=null && busitype.length>0 && busitype=='1')
			plansSel =  getCookie('modalPlansSel2');
	<%}%>
	if(plansSel==null)
		plansSel='';
	var temps=document.getElementsByName("plan_id");
	if(plansSel!='')
		plansSel=','+plansSel+','

	for(var i=0;i<temps.length;i++)
	{
		if(plansSel!=null && plansSel.indexOf(','+temps[i].value+',')>-1)
			temps[i].checked=true;
	}
	function selectPlan()
	{
		document.perAnalyseForm.action="/performance/perAnalyse.do?b_intPlanList=query&opt=1&cookiFlag=	<%=request.getParameter("cookiFlag")%>";
		document.perAnalyseForm.submit();
	}
</script>