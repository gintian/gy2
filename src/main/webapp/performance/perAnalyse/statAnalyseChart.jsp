<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
  <head>
  <hrms:themes />
  <style>
	.RecordRow {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;

	height:30;
    }
	
	
	.RecordRow_select {
	border: inset 2px #94B6E6;
	BORDER-BOTTOM: #94B6E6 2pt solid; 
	BORDER-LEFT: #94B6E6 2pt solid; 
	BORDER-RIGHT: #94B6E6 2pt solid; 
	BORDER-TOP: #94B6E6 2pt solid;
	font-size: 12px;

	height:30;
    }
	
	
	div#tbl-container {
	width:100%;
	overflow: auto;
	 
	}


</style>
<hrms:themes />
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
   <script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
 <SCRIPT LANGUAGE=javascript src="/performance/perAnalyse/perAnalyse.js"></SCRIPT>
 <script type="text/javascript" src="/js/constant.js"></script>
  <script language='javascript' >
	function changePlan()
	{
		document.perAnalyseForm.action="/performance/perAnalyse.do?b_statAnalyse=query"	
		document.perAnalyseForm.submit();
	}
	
	
	
	function reverseResult(id)
	{
		document.perAnalyseForm.action="/performance/perAnalyse.do?b_reverse=query&id="+id;	
		document.perAnalyseForm.submit();
	}
	
	function executeExcel2()
	{
		var planid = document.perAnalyseForm.planIds.value;
		if(planid=='')
			return;
		var hashvo=new ParameterSet();
		hashvo.setValue("model","11");
		hashvo.setValue("planid",planid);
		var request=new Request({method:'post',asynchronous:false,onSuccess:showFile,functionId:'9026000014'},hashvo);
	}
	
  </script>
  </head>
  
  <body >
   <html:form action="/performance/perAnalyse">
    &nbsp;<bean:message key="kh.field.plan"/>:&nbsp;
	<html:select name="perAnalyseForm" property="planIds" size="1" onchange="changePlan()">
  	 <html:optionsCollection property="perPlanList" value="dataValue" label="dataName"/>
	</html:select>
	
	&nbsp;&nbsp; <input type='button' value='<bean:message key="general.inform.muster.output.excel"/>' onclick="executeExcel2()" class="mybutton" />
 	<logic:equal name="perAnalyseForm" property="fromModule" value="analyse">
								<logic:equal name="perAnalyseForm" property="busitype" value="0">	
								<hrms:tipwizardbutton flag="performance" target="il_body" formname="perAnalyseForm"/> 
								</logic:equal>	
								<logic:equal name="perAnalyseForm" property="busitype" value="1">	
								<hrms:tipwizardbutton flag="capability" target="il_body" formname="perAnalyseForm"/> 
								</logic:equal>	
	</logic:equal>
	
    &nbsp;&nbsp;<bean:message key="lable.performance.evaluation.perObjectNum"/>：${perAnalyseForm.objectNum}     
  
  <div  id="tbl-container" style='position:absolute;left:5px;top:40px;'  >
  
   ${perAnalyseForm.statHtml}
  
  </div> 
  
  
  
  
   </html:form>
  <script type="text/javascript">
      var theHeight = document.body.clientHeight-40;
      var theWidth = document.body.clientWidth-10;
      document.getElementById("tbl-container").style.height=theHeight+'px';
      document.getElementById("tbl-container").style.width=theWidth+'px';
  </script>
  </body>
</html>
