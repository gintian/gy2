<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_budget.BudgetingForm" %>
<%
BudgetingForm bform=(BudgetingForm)session.getAttribute("budgetingForm");
String tab_type=bform.getTab_type();
String tableName=bform.getTableName();
String canImport=bform.getCanImport();
String unitSpflag=bform.getUnitSpflag();
 %>
<script language="javascript" src="/js/dict.js"></script> 
<script language="javascript" src="/gz/gz_budget/budgeting/budgeting.js"></script>
<script language='javascript'>
	var queryhidden=0;
	function visiblequery(){
	   if(queryhidden==0) {
	      var queryblank=document.getElementById("tblname");
	      if(queryblank)   queryblank.style.display="block";
	      queryhidden=1;

      	var obj=document.getElementById("querydesc");
      	obj.innerHTML="[&nbsp;<a href=\"javascript:visiblequery();\" >查询隐藏&nbsp;</a>]&nbsp;&nbsp;&nbsp;";
	   }
	   else
	   {
	       var queryblank=document.getElementById("tblname");
	       if(queryblank)     queryblank.style.display="none";
	      queryhidden=0;
      	var obj=document.getElementById("querydesc");
      	obj.innerHTML="[&nbsp;<a href=\"javascript:visiblequery();\" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;";
	   }
	}
	function queryR(){
		var txtname=document.getElementById("txtname");
		var value=txtname.value;
		
		 budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_search=int&tab_id=2&personname="+getEncodeStr(value);
		 budgetingForm.submit(); 


	}
</script>
<html:form action="/gz/gz_budget/budgeting/budgeting_table">
<html:hidden property="tab_id" name="budgetingForm"/>
<html:hidden property="tableName" name="budgetingForm"/>
 <span>&nbsp;${budgetingForm.infoStr}</span> 
  <logic:equal name="budgetingForm" property="tableName" value="SC01">
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<span id="querydesc" > &nbsp;&nbsp;[&nbsp;<a href="javascript:visiblequery();" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;</span> 
</logic:equal >
 <table style="align:left;display:none" id ="tblname">
 	<tr>
		<td>
 	 		&nbsp;&nbsp;&nbsp;姓名：<input type="text" id="txtname" value="" class="inputtext"/>&nbsp;&nbsp;&nbsp;
 		</td>
 		<td>
 			<input type="button" name="query" value="查询" class="mybutton" onclick="queryR();"/>&nbsp;&nbsp;&nbsp;
 		</td>
	</tr>
</table>
 <logic:equal name="budgetingForm" property="tableName" value="SC01">
	<hrms:dataset name="budgetingForm" property="fieldlist" scope="session" setname="${budgetingForm.tableName}" 
	pagerows="${budgetingForm.pagerows}" setalias="x" readonly="false"  editable="true" select="false" sql="${budgetingForm.sql}" buttons="bottom">
	   <%if(tab_type.equalsIgnoreCase("2")){ %>
	   		<%if(unitSpflag.equalsIgnoreCase("04")||unitSpflag.equalsIgnoreCase("07")){ %>
			   <hrms:commandbutton name="init" hint="" function_id="" refresh="true" type="selected" setname="${budgetingForm.tableName}" onclick="initperson()">
			     <bean:message key="gz.budget.budgeting.init"/>
			   </hrms:commandbutton>
			      <hrms:commandbutton name="addman" hint="" function_id="" refresh="true" type="selected" setname="${budgetingForm.tableName}" onclick="addmen()">
			     <bean:message key="gz.info.addmen"/>
			   </hrms:commandbutton>
		   <%} %>
	   
	   <%} %>
	   <hrms:commandbutton name="save" functionId="302001020203" function_id="324200204" refresh="false" type="all-change" setname="${budgetingForm.tableName}">
	     <bean:message key="button.save"/>
	   </hrms:commandbutton>
	      <hrms:commandbutton name="download" hint=""  refresh="true" function_id="324200205" type="selected" setname="${budgetingForm.tableName}" onclick="downloadTemplate('${budgetingForm.tab_id}',0);">
	     <bean:message key="button.download.template"/>
	   </hrms:commandbutton>   
	   
	   <%if(canImport.equalsIgnoreCase("true")){ %>
	      <hrms:commandbutton name="import" hint="" function_id="324200206" refresh="true" type="selected" setname="${budgetingForm.tableName}" onclick="imports('${budgetingForm.tab_id}');">
	     <bean:message key="import.tempData"/>
	   </hrms:commandbutton>
	   <%} %>
	</hrms:dataset>

	
</logic:equal>	
<logic:notEqual name="budgetingForm" property="tableName" value="SC01">
	<hrms:dataset name="budgetingForm" property="fieldlist" scope="session" setname="${budgetingForm.tableName}" 
		pagerows="100" setalias="x" readonly="false"  editable="true" select="false" sql="${budgetingForm.sql}" buttons="">
	   <%if(tab_type.equalsIgnoreCase("2")){ %>
	   <hrms:commandbutton name="init" hint="" function_id="" refresh="true" type="selected" setname="${budgetingForm.tableName}" onclick="">
	     <bean:message key="gz.budget.budgeting.init"/>
	   </hrms:commandbutton>
	      <hrms:commandbutton name="addman" hint="" function_id="" refresh="true" type="selected" setname="${budgetingForm.tableName}" onclick="">
	     <bean:message key="gz.info.addmen"/>
	   </hrms:commandbutton>
	   <%} %>
	   <hrms:commandbutton name="save" functionId="302001020203" function_id="324200204" refresh="false" type="all-change" setname="${budgetingForm.tableName}">
	     <bean:message key="button.save"/>
	   </hrms:commandbutton>
	      <hrms:commandbutton name="download" hint=""  refresh="true" function_id="324200205" type="selected" setname="${budgetingForm.tableName}" onclick="downloadTemplate('${budgetingForm.tab_id}',0);">
	     <bean:message key="button.download.template"/>
	   </hrms:commandbutton>
	    <%if(!tab_type.equalsIgnoreCase("3")){ %>
	   <hrms:commandbutton name="batchdownload" hint=""  refresh="true" function_id="324200205" type="selected" setname="${budgetingForm.tableName}" onclick="downloadTemplate('${budgetingForm.tab_id}',1);">
	     <bean:message key="button.download.batchtemplate"/>
	   </hrms:commandbutton>
	    <%} %>
	   <%if(canImport.equalsIgnoreCase("true")){ %>
	      <hrms:commandbutton name="import" hint="" function_id="324200206" refresh="true" type="selected" setname="${budgetingForm.tableName}" onclick="imports('${budgetingForm.tab_id}');">
	     <bean:message key="import.tempData"/>
	   </hrms:commandbutton>
	   <%} %>	   
	</hrms:dataset>

</logic:notEqual>

</html:form>