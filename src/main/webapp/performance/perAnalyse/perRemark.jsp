<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.PerAnalyseForm,
                 com.hrms.struts.taglib.CommonData,
                 org.apache.commons.beanutils.LazyDynaBean" %>
<html>
  <head>
    <hrms:themes />
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >

  </head>
  <script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <script type="text/javascript" src="/js/constant.js"></script>
  <script language='javascript' >
  	function  getPerRemark(codeitemid)
  	{
  		document.perAnalyseForm.action="/performance/perAnalyse.do?b_perRemark=query&codeitemid="+codeitemid;
  		document.perAnalyseForm.submit();
  	
  	}
  	
  	function changePlan()
  	{
  		document.perAnalyseForm.action="/performance/perAnalyse.do?b_perRemark0=query";
		document.perAnalyseForm.target="detail";
		document.perAnalyseForm.submit();
  	}
  	
  	
  	function executeFile()
  	{
  		if('${perAnalyseForm.planIds}'!='')
  			window.open("/performance/perAnalyse/download.jsp");
  	}
  	
  
  </script>
  <body>
  <html:form action="performance/perAnalyse">  
    &nbsp;<font size='2'><bean:message key="kh.field.plan"/>:</font>
	<html:select name="perAnalyseForm" property="planIds" size="1" onchange="changePlan()">
  	 <html:optionsCollection property="perPlanList" value="dataValue" label="dataName"/>
	</html:select>&nbsp;&nbsp;&nbsp; <input type='button' value='<bean:message key="pos.report.relations.export"/>' onclick="executeFile()" class="mybutton" />
	<logic:equal name="perAnalyseForm" property="fromModule" value="analyse">
		<hrms:tipwizardbutton flag="performance" target="il_body" formname="perAnalyseForm"/> 
	</logic:equal>
	
	<Br><Br>
	<logic:notEqual name="perAnalyseForm"  property="objectName"  value="">
    <table><TR><TD align='left'><bean:message key="lable.performance.perObject"/>：${perAnalyseForm.objectName}</TD></TR>
    <tr><td>
    <html:textarea name="perAnalyseForm" readonly="true" cols="150" rows="30"   property="remark" ></html:textarea>
  	</td></tr></table>
  	</logic:notEqual>
  	<script language='javascript'>
	  	//if(!getBrowseVersion()) {//非IE，将iframe的il_body置为和ie一样的name  detail
        if(parent.parent.frames["il_body"])
			parent.parent.frames["il_body"].name = "detail";
		//}
	</script>
  </html:form>
  </body>
</html>
