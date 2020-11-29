<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<%
	String tab1_name=ResourceFactory.getProperty("gz.budget.system.option");
	String tab2_name=ResourceFactory.getProperty("gz.budget.total.set");
	String tab3_name=ResourceFactory.getProperty("gz.budget.param.set");
%>
<style id=iframeCss>

.AutoTable{
   BORDER-BOTTOM: medium none; BORDER-COLLAPSE: collapse; 
   BORDER-LEFT: medium none; BORDER-RIGHT: medium none; BORDER-TOP: medium none; 
   TABLE-LAYOUT:fixed;   
   word-break:break-all;
}
.fixedheight{
height:280px;
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
<script language="JavaScript" src="/gz/gz_budget/budget_rule/options/budgetOptions.js"></script>
<script language="JavaScript">

	function changeCodeSet(){
		var v = budgetSysForm.rylb_codeset.value;
  		var hashvo=new ParameterSet();
  		  hashvo.setValue("setid",v);
  	 	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeCodeSet,functionId:'1010020318'},hashvo);					
    }
  
  function resultChangeCodeSet(outparamters){
  	var txrecordList=outparamters.getValue("txrecordList");
	AjaxBind.bind(budgetSysForm.txCode,txrecordList);
	
  }
  function showTX(){
    var temp = document.getElementById('createTXrecord').checked;

  	if(temp){
  		Element.show('txCodetd');
  	}else{
  		Element.hide('txCodetd');
  	}
  }
  	window.onload=showTX;
</script>
<hrms:themes />
<html:form action="/gz/gz_budget/budget_rule/options">
<br>
<br>
<hrms:tabset name="pageset" width="700px;" height="400"  type="false"> 
	
 		<hrms:tab name="tab1" label="<%=tab1_name %>" visible="true">
  			<table width="90%" height='100%' align="center"> 
				<tr>
					<td valign="top" align='center'>
				  		<div style="overflow:auto;width:100%;height:360px;border:0px;" >
							<table width="70%" border="0px" cellspacing="0" align="center" cellpadding="0" class="ListTable">
								<tr class="list3">
					     			<td align="right" nowrap valign="middle">
					     				<bean:message key="gz.budget.budgettab.kind.name"/>&nbsp;
					     			</td>
					     			<td align="left" nowrap>
					     				<html:text name="budgetSysForm" property="kindstr" styleClass="inputtext" style="width:152px;"/>
					     			</td>
					     			<td>
					     			</td>
					     		</tr>
					     		<tr class="list3">
					     			<td align="right" nowrap valign="middle">
								  		<bean:message key="gz.budget.personkind.codeset"/>&nbsp;
							  		</td>
							  		<td align="left" nowrap>
							  			<hrms:optioncollection name="budgetSysForm" property="rylbList" collection="list" />
										<html:select name="budgetSysForm" property="rylb_codeset" size="1" style="width:152px;" onchange="changeCodeSet();">
									         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
									    </html:select>
							  		</td>
							  		<td>
					     			</td>
					     		</tr>
					     		<tr class="list3">
					     			<td align="right" nowrap valign="middle">
								  		<bean:message key="gz.budget.person.unit.belong_to"/>&nbsp;
							  		</td>
							  		<td align="left" nowrap>
							  			<hrms:optioncollection name="budgetSysForm" property="unitList" collection="list" />
										<html:select name="budgetSysForm" property="unitmenu" size="1" style="width:152px;">
									         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
									    </html:select>
									    (<bean:message key="gz.budget.bindcodeset"/>)
							  		</td>
							  		<td>
					     			</td>
					     		</tr>
					     		 <tr>
									   <td align="right" nowrap valign="top">
										   <bean:message key="gz.budget.person.base"/>&nbsp;
									    </td>
										<td align="left" style="width:150px;" nowrap>
										    <html:textarea name="budgetSysForm" property="dblist" cols="55" rows="3" style="display:none"/>
										    <html:textarea name="budgetSysForm" property="dblist_name" cols="55" rows="3" readonly='true' />
										</td>
										<td align="left" nowrap valign="top" style="padding-left: 5px;">
											<input type="button" value="<bean:message key='leaderteam.leaderparam.dbsetting'/>" class="mybutton" onclick="setDBList();">
										</td>
							     </tr>
							     <tr>
									   <td align="right" nowrap valign="top">
										   <bean:message key="gz.budget.person.range"/>&nbsp;
									    </td>
										<td align="left" nowrap>
										    <html:textarea name="budgetSysForm" property="range" cols="55" rows="3" readonly='true' />
										</td>
										<td align="left" nowrap valign="top" style="padding-left: 5px;">
											<input type="button" value="<bean:message key='menu.gz.options'/>" class="mybutton" onclick="complexCondition();">
										</td>
							     </tr>
							     <tr>
									   <td align="right" nowrap valign="top">
										   <bean:message key="gz.budget.unit.belong_to"/>&nbsp;
									    </td>
										<td align="left" nowrap>
											<html:textarea name="budgetSysForm" property="units" cols="55" rows="3" style="display:none" />
										    <html:textarea name="budgetSysForm" property="units_name" cols="55" rows="3" readonly='true' />
							    		</td>
										<td align="left" nowrap valign="top" style="padding-left: 5px;">
											<input type="button" value="<bean:message key='menu.gz.options'/>" class="mybutton" onclick="getorg();">
										</td>
							     </tr>
							     <tr>
							     	<td>
							     	</td>
							     	<td align="left" nowrap>
							     		<html:checkbox styleId="createTXrecord" name="budgetSysForm" property="createTXrecord" value="1" style="margin-left:-4px;" onclick="showTX();"/><bean:message key="gz.budget.retperson.data"/>
							     	</td>
							     </tr>
							      <tr>
							     	<td>
							     		
							     	</td>
							     	<td align="left" id="txCodetd" style="display:none;" nowrap>
							     		&nbsp;&nbsp;&nbsp;&nbsp;
							     		<bean:message key='gz.budget.change.by.year.txperson'/>&nbsp;
										<hrms:optioncollection name="budgetSysForm" property="txrecordList" collection="list" />
										<html:select name="budgetSysForm" property="txCode" size="1" styleId="txCode" >
									         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
									    </html:select>
							     	</td>
							     </tr>
							     <tr>
							     	<td>
							     	</td>
							     	<td align="left" nowrap>
							     		<html:checkbox styleId="datatoze" name="budgetSysForm" property="datatoze" value="1" style="margin-left:-4px;"/><bean:message key="gz.budget.publish.totalsalary"/>
							     	</td>
							     	<td>
					     			</td>
							     </tr>							    
							      
							</table>  
						</div>
					</td>
				</tr>				
			</table>          	
 		</hrms:tab>
 	
 	
	 	<hrms:tab name="tab2" label="<%=tab2_name %>" visible="true">
  			<table width="80%" height='100%' align="center"> 
				<tr>
					<td valign="top" align='center'>
				  		<div style="overflow:auto;width:100%;height:320px;border:0px;" >
							<table align="center" width="100%">
							<tr align="center">
							<td align="center">
								<div class="fixedheight">
									 <table width="95%" border="0" cellpadding="0" cellspacing="0" align="center" vlign="top" class="ListTable">
										<tr class="list3">
									  		<td align="right" nowrap valign="middle">
										  		<bean:message key="gz.budget.total.subset"/>&nbsp;
									  		</td>
									  		<td align="left" nowrap>
									  			<hrms:optioncollection name="budgetSysForm" property="budgetSetList" collection="list" />
												<html:select name="budgetSysForm" property="ysze_set" size="1" style="width:150px;" onchange="getNewField1();">
											         <html:options collection="list" property="dataValue" labelProperty="dataName" />
											    </html:select>
											    (<bean:message key="gz.budget.change.by.year"/>)
									  		</td>
									  		<td>
									  		</td>
									  		
							     		</tr>
							     		<tr class="list3">
							     			<td align="right" nowrap valign="top" width="10%">
										  		<bean:message key="gz.budget.index.field"/>&nbsp;
									  		</td>
									  		<td align="left" nowrap valign="top" width="10%">
									  			<hrms:optioncollection name="budgetSysForm" property="budgetIndexList" collection="list" />
												<html:select name="budgetSysForm" property="ysze_idx_menu" size="1" style="width:150px;">
											         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
											    </html:select>
											    (<bean:message key="system.integer.field"/>)
									  		</td>
									  		<td>
									  		</td>
							     		</tr>
							     		<tr class="list3">
							     			<td align="right" nowrap valign="middle">
										  		<bean:message key="gz.budget.total.field"/>&nbsp;
									  		</td>
									  		<td align="left" nowrap>
									  			<hrms:optioncollection name="budgetSysForm" property="budgetTotalList" collection="list" />
												<html:select name="budgetSysForm" property="ysze_ze_menu" size="1" style="width:150px;">
											         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
											    </html:select>
									  		</td>
									  		<td>
									  		</td>
							     		</tr>
							     		<tr class="list3">
							     			<td align="right" nowrap valign="middle">
										  		<bean:message key="gz.budget.spfield"/>&nbsp;
									  		</td>
									  		<td align="left" nowrap>
									  			<hrms:optioncollection name="budgetSysForm" property="spStatusList" collection="list" />
												<html:select name="budgetSysForm" property="ysze_status_menu" size="1" style="width:150px;">
											         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
											    </html:select>
											    (<bean:message key="gz.budget.change.by.year"/>，<bean:message key="kjg.title.guanliancode"/>23)
									  		</td>
									  		<td>
									  		</td>
									  		
							     		</tr>
									</table>
								</div>
							</td>
							</tr>
							</table>  
						</div>
					</td>
				</tr>				
			</table>          	
 		</hrms:tab>

		<hrms:tab name="tab3" label="<%=tab3_name %>" visible="true">
  			<table width="60%" height='100%' align="center" border="0"> 
				<tr>
					<td valign="top" align='center' >
				  		<div style="overflow:auto;width:100%;height:320px;border:0px;" >
							<table align="center" width="100%">
							<tr align="center">
							<td align="center">
								<div class="fixedheight">
									 <table width="65%" border="0" cellpadding="0" cellspacing="0" align="center" vlign="top" class="ListTable">
										<tr class="list3">
									  		<td align="right" nowrap valign="middle">
										  		<bean:message key="gz.budget.param.subset"/>&nbsp;
									  		</td>
									  		<td align="left" nowrap>
									  			<hrms:optioncollection name="budgetSysForm" property="budgetParamSetList" collection="list" />
												<html:select name="budgetSysForm" property="ysparam_set" size="1" style="width:150px;" onchange="getNewField2();">
											         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
											    </html:select>
											    (<bean:message key="gz.budget.change.by.year"/>)
									  		</td>
									  		<td>
									  		</td>
									  		
							     		</tr>
							     		<tr class="list3">
							     			<td align="right" nowrap valign="middle">
										  		<bean:message key="gz.budget.index.field"/>&nbsp;
									  		</td>
									  		<td align="left" nowrap>
									  			<hrms:optioncollection name="budgetSysForm" property="budgetIndexFieldList" collection="list" />
												<html:select name="budgetSysForm" property="ysparam_idx_menu" size="1" style="width:150px;">
											         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
											    </html:select>
											    (<bean:message key="system.integer.field"/>)
									  		</td>
									  		<td>
									  		</td>
							     		</tr>
							     		<tr class="list3">
							     			<td align="right" nowrap valign="middle">
										  		<bean:message key="gz.budget.newemployee.in_month"/>&nbsp;
									  		</td>
									  		<td align="left" nowrap>
									  			<hrms:optioncollection name="budgetSysForm" property="employeeList" collection="list" />
												<html:select name="budgetSysForm" property="ysparam_newmonth_menu" size="1" style="width:150px;">
											         <html:options collection="list" property="dataValue" labelProperty="dataName"/>
											    </html:select>
											    (<bean:message key="system.integer.field"/>)
									  		</td>
									  		<td>
									  		</td>
							     		</tr>
									</table>
								</div>
							</td>
							</tr>
							</table>  
						</div>
					</td>
				</tr>				
			</table>
 		</hrms:tab>
 	</hrms:tabset>

<table  width="100%" align="center">
          <tr>
            <td align="center">
         		<input type="button" class="mybutton" value="<bean:message key="button.save"/>" onclick="saveSystemParams();" >
            </td>
          </tr>          
</table>

</html:form>

