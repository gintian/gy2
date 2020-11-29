<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
<!--
body {overflow-y:hidden;}
-->
</style>
<body style="overflow-y:hidden;">
<html:form action="/train/trainCosts/trainCosts">

<hrms:tabset name="cardset" width="100%" height="100%" type="true"> 
		  <hrms:tab name="menu1" label="train.b_plan.request.tariffs" visible="true" function_id="323503" url="/train/trainCosts/trainCosts.do?b_query=link&r2501=${trainCostsForm.r2501}&flag=1&b0110=${param.b0110}&e0122=${param.e0122}">
	      </hrms:tab>
		 <hrms:tab name="menu2" label="train.b_plan.request.cost.ratio" visible="true" function_id="323504" url="/train/trainCosts/trainCosts.do?b_chart=link&r2501=${trainCostsForm.r2501}&flag=2">
	      </hrms:tab>	
</hrms:tabset>
</html:form>
</body>
