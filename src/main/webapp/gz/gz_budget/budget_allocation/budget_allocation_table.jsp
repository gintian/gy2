<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<html>
<head>
<script language="javascript" src="/js/dict.js"></script>
<script language="javascript" src="/gz/gz_budget/budget_allocation/budgetAllocation.js"></script>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
</head>

 <script type="text/javascript">
 

 </script>

<script type="text/javascript">
	var selectedUnit = "${budgetAllocationForm.selectedUnit}";  // 机构树选中单位
   	var dataset_tableid="table${budgetAllocationForm.tab_name}"; // 数据集 table id
	
</script>
<body>
<html:form action="/gz/gz_budget/budget_allocation/budget_allocation" >
<%if("hl".equals(hcmflag)){ %>
<table>
<%}else{ %>
<table style="margin-top:-5px;">
<%} %>
<tr><td   nowrap>
<bean:message key="gz.budget.budgeting.currentys"/>：${budgetAllocationForm.currentBudgetDesc}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="column.sys.status"/>：${budgetAllocationForm.budgetStatusDesc}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${budgetAllocationForm.amountDesc}
</td>
</tr>
<tr><td>
<hrms:dataset name="budgetAllocationForm" property="fieldList" scope="session" setname="${budgetAllocationForm.tab_name}"  
 setalias="ysze_set" pagerows="50" readonly="false" buttons="movefirst,prevpage,nextpage,movelast" editable="true" 
 select="true" sql="${budgetAllocationForm.sql}"  >
   <hrms:commandbutton name="newBudget" hint="" function_id="324200101" refresh="false" type="selected" setname="${budgetAllocationForm.tab_name}"  onclick="add_budget()">
     <bean:message key="gz.budget.budget_allocation.newBudget"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="delBudget" hint="" function_id="324200102" refresh="false" type="selected" setname="${budgetAllocationForm.tab_name}" onclick="delete_budget()">
     <bean:message key="gz.budget.budget_allocation.delBudget"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="setParams" functionId="" function_id="324200103" refresh="false" type="selected" setname="${budgetAllocationForm.tab_name}" onclick="setParams_budget()">
     <bean:message key="gz.budget.budget_allocation.setParams"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="distribute" hint="" refresh="false" function_id="324200104" type="selected" setname="${budgetAllocationForm.tab_name}" onclick="distributeBudget()">
     <bean:message key="gz.budget.budget_allocation.distribute"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="reject" hint="" refresh="false" function_id="324200105" type="selected" setname="${budgetAllocationForm.tab_name}" onclick="rejectBudget()">
     <bean:message key="gz.budget.budget_allocation.reject"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="insert" hint="" refresh="false" function_id="324200106" type="selected" setname="${budgetAllocationForm.tab_name}" onclick="addBudgetUnit()">
     <bean:message key="button.insert"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="delete" hint="" refresh="false" function_id="324200107" type="selected" setname="${budgetAllocationForm.tab_name}" onclick="delBudgetUnit()">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="save" hint="" functionId="302001020505" function_id="324200108" refresh="false" type="all-change" setname="${budgetAllocationForm.tab_name}" onclick="">
     <bean:message key="button.save"/>
   </hrms:commandbutton>

</hrms:dataset>
</td></tr>    

</table>
	<input type="hidden" name="budget_id" value="${budgetAllocationForm.budget_id}" />
	<input type="hidden" name="budgetStatus" value="${budgetAllocationForm.budgetStatus}" /><!-- 有必要，budgetAllocation el表达式获不得值 用budgetAllocationForm.budgetStatus.value获值 -->
	<input type="hidden" name="b0110" value="${budgetAllocationForm.b0110}" />
</html:form>
</body>
</html>