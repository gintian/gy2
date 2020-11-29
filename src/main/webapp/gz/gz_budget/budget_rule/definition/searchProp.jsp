<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_budget.budget_rule.definition.BudgetDefForm,
				org.apache.commons.beanutils.LazyDynaBean" %>
<%
	BudgetDefForm budgetDefForm = (BudgetDefForm)session.getAttribute("budgetDefForm");
	LazyDynaBean bean = budgetDefForm.getBudgetBean();
	String tab_id = (String)bean.get("tab_id");
	String tab_name = (String)bean.get("tab_name");
%>
<style id=iframeCss>

.AutoTable{
   BORDER-BOTTOM: medium none; BORDER-COLLAPSE: collapse; 
   BORDER-LEFT: medium none; BORDER-RIGHT: medium none; BORDER-TOP: medium none; 
   TABLE-LAYOUT:fixed;   
   word-break:break-all;
}
.fixedheight{
height:300px;
vertical-align:top;
}
.RecordRowPer {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	margin-top:-4px;
	height:22;
}
</style>
<hrms:themes />
<script language="JavaScript" src="/gz/gz_budget/budget_rule/definition/budgetDef.js"></script>
<html:form action="/gz/gz_budget/budget_rule/definition">

<table align="center" width="700px" style="margin-top:60px;">
<tr align="center">
<td align="center">
<fieldset>


	<legend>
	<%=tab_name %><bean:message key="gz.budget.property"/>
	</legend>
	<div class="fixedheight">
		 <table width="80%" border="0" cellpadding="0" cellspacing="0" style="margin-right: 110px;" align="center" vlign="top" class="AutoTable">
			<tr class="list3">
		  		<td align="right" nowrap valign="middle">
			  		<bean:message key="gz.budget.budgettab.kind"/>&nbsp;
		  		</td>
		  		<td align="left" nowrap>
		  			<hrms:optioncollection name="budgetDefForm" property="kindList" collection="list" />
					<html:select name="budgetDefForm" property="budgetBean.budgetgroup" size="1" style="width:150px;">
				         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				    </html:select>
		  		</td>
     		</tr>
     		<tr class="list3">
     			<td align="right" nowrap valign="middle">
			  		<bean:message key="gz.budget.project.kind"/>&nbsp;
		  		</td>
		  		<td align="left" nowrap>
		  			<hrms:optioncollection name="budgetDefForm" property="codesetList" collection="list" />
					<html:select name="budgetDefForm" property="budgetBean.codesetid" size="1" style="width:150px;">
				         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				    </html:select>
		  		</td>
     		</tr>
     		<tr class="list3">
     			<td align="right" nowrap valign="middle">
     				<bean:message key="gz.budget.translate.code"/>&nbsp;
     			</td>
     			<td align="left" nowrap>
     				<html:text name="budgetDefForm" property="budgetBean.tabcode" styleClass="inputtext" style="width:150px;"/>
     			</td>
     		</tr>
     		<!-- 用工计划表没有执行率分析 --> 
     		<tr class="list3">
	     			<td>
	     				
	     			</td>
	     			<td align="left" nowrap valign="middle">
	     			<logic:equal name="budgetDefForm" property="tabType" value="4">
	     				<html:checkbox styleId="analyseflag" name="budgetDefForm" property="analyseFlag" value="1" style="margin-left: -4px;"/><bean:message key="gz.budget.analyse"/>
                    </logic:equal>
	     			</td>
	     		</tr>
                 		
     		<tr class="list3">
     			<td>
     				
     			</td>
     			<td align="left" nowrap valign="middle">
     				<html:checkbox styleId="bpflag" name="budgetDefForm" property="bpFlag" value="1" style="margin-left: -4px;"/><bean:message key="gz.budget.bp"/>
     			</td>
     		</tr>
     		<tr class="list3">
     			<td>
     				
     			</td>
     			<td align="left" nowrap valign="middle">
     				<html:checkbox styleId="validflag" name="budgetDefForm" property="validFlag" value="1" style="margin-left: -4px;"/><bean:message key="kq.emp.change.compare"/>
     			</td>
     			
     		</tr>	
		</table>
	</div>
</fieldset>
</td>
</tr>
</table>

<table  width="100%" align="center">
          <tr>
            <td align="center">
         		<input type="button" class="mybutton" value="<bean:message key='button.save'/>" onclick="saveProperty();">
         		<input type="button" class="mybutton" value="<bean:message key='button.return'/>" onclick="seq_ok();">
            </td>
          </tr>          
</table>

</html:form>
