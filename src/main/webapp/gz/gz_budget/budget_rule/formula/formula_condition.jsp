<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" href="/gz/gz_budget/budget_rule/formula/budget_formula.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/gz/gz_budget/budget_rule/formula/budget_formula.js"></script>
<script language="javascript">
function function_Wizard(){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&checktemp=ysgs&mode=ysgl"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	symbol_condition(return_vo);
  	}else{
  		return ;
  	}
}

</script>
<html:form action="/gz/gz_budget/budget_rule/formula">
<table width="515px;" height="350" border="0" align="center" style="margin-left:-3px;margin-top:-3px;" >
<tr>
<html:hidden name="budgetformulaForm" property="formula_id"/> 
<html:hidden name="budgetformulaForm" property="tj_type"/> 
<bean:define id="formula_id" name='budgetformulaForm' property='formula_id'/>
<bean:define id="tj_type" name='budgetformulaForm' property='tj_type'/>
<td align="center">  
    <fieldset align="center" style="width:100%;">
	<legend><bean:message key='gz.formula.crond.formula.expression'/></legend> 
		<table width="99%" border="0">
        	<tr> 
          		<td colspan="2" align="center"> 
            		<html:textarea name="budgetformulaForm"  property="cond_value"  onclick="this.pos=document.selection.createRange();"  cols="85" rows="9" styleId="shry"></html:textarea> 
            	</td>
        	</tr>
        	<tr> 
          		<td height="21" colspan="2" align="right"> 
          			<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick="function_Wizard();" Class="mybutton"> 
            		<input type="button" value="<bean:message key='org.maip.formula.save.cond'/>" onclick="savecond('${budgetformulaForm.formula_id}','${budgetformulaForm.tj_type}');" Class="mybutton">&nbsp; 
            	</td>
        	</tr>
        	<tr> 
          		<td width="50%" align="center"> 
          		 <fieldset  align="top" style="width:100%;height=110;left:0;"> <!-- modify by xiaoyun 2014-9-13 -->
				 <legend><bean:message key='org.maip.reference.projects'/></legend> 
            		<table width="100%" border="0" >
            			<tr >
            				<td>
            			     <span id="condsetview" >
            					<table width="100%"  border="0" >
              						<tr> 
                						<td height="23"><bean:message key='gz.budget.budget_examination.budgetTable'/>
											<html:select name="budgetformulaForm" property="cond_setid" onchange="CondChangeSet('${budgetformulaForm.formula_id}');" style="width:165">
			 									<html:optionsCollection property="cond_setlist" value="dataValue" label="dataName" />
											</html:select>
                 						</td>
              						</tr>
            					</table>
            				  </span>	
            				</td>
            			</tr>
            			<tr >
            				<td>
                    			<table width="100%" border="0" >
              						<tr> 
                						<td height="23"><bean:message key='gz.budget.formula.colproject'/>
											<select name="cond_itemlist" onchange="CondChangeCol('${budgetformulaForm.formula_id}','${budgetformulaForm.tj_type}');"  style="width:165;font-size:9pt">
             								</select>
                 						</td>
              						</tr>
            					</table>
            				</td>
            			</tr>
            			<tr >
            				<td>
            					<span id="condcodeview" style="display:none">
            					<table width="100%" border="0" >
              						<tr> 
                						<td height="23"><bean:message key='gz.budget.formula.codeItem'/>
											<select name="cond_codelist" onchange="CondChangeCode('${budgetformulaForm.formula_id}');"  style="width:165;font-size:9pt">
             								</select>
                 						</td>
              						</tr>
            					</table>
            					</span>
            				</td>
            			</tr>
            		</table>
            		</fieldset>
          		</td>
          		<td width="50%">
          		<fieldset align="center" style="width:100%;height=110">
				 <legend><bean:message key='gz.formula.operational.symbol'/></legend> 
					<table width="100%" border="0">
              			<tr> 
              				<td>
              				<table width="100%" border="0">
              				<tr>
                				<td><input type="button"  value="0" onclick="symbol_condition(0);" class="btn2 common_btn_bg"></td>
                				<td><input type="button"  value="1" onclick="symbol_condition(1);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="2" onclick="symbol_condition(2);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="3" onclick="symbol_condition(3);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="4" onclick="symbol_condition(4);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="+" onclick="symbol_condition('+');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="*" onclick="symbol_condition('*');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="\" onclick="symbol_condition('\\');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="<bean:message key='general.mess.and'/>" onclick="symbol_condition('<bean:message key='general.mess.and'/>');" class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="<bean:message key='kq.wizard.not'/>" onclick="symbol_condition('<bean:message key='kq.wizard.not'/>');" class="btn1 common_btn_bg"> </td>
              				</tr>
              				<tr> 
                				<td><input type="button"  value="5" onclick="symbol_condition(5);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="6" onclick="symbol_condition(6);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="7" onclick="symbol_condition(7);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="8" onclick="symbol_condition(8);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="9" onclick="symbol_condition(9);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="-" onclick="symbol_condition('-');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="/" onclick="symbol_condition('/');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="%" onclick="symbol_condition('%');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="<bean:message key='kq.wizard.and'/>" onclick="symbol_condition('<bean:message key='kq.wizard.and'/>');" class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="~" onclick="symbol_condition('~');" class="btn2 common_btn_bg"> </td>
              				</tr>
              				<tr> 
              					<td><input type="button"  value="(" onclick="symbol_condition('(');" class="btn2 common_btn_bg"> </td>
              					<td><input type="button"  value=")" onclick="symbol_condition(')');" class="btn2 common_btn_bg"> </td>
               		 			<td><input type="button"  value="=" onclick="symbol_condition('=');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&gt;" onclick="symbol_condition('&gt;');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;" onclick="symbol_condition('&lt;');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;&gt;" onclick="symbol_condition('&lt;&gt;');" class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;=" onclick="symbol_condition('&lt;=');"class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="&gt;=" onclick="symbol_condition('&gt;=');"class="btn1 common_btn_bg"> </td>
                				<td colspan="2"><input type="button"  value="<bean:message key='kq.wizard.contain'/>" onclick="symbol_condition('Like');" class="btn3 common_btn_bg"></td>
              				</tr>
            			</table>
            			</td>
            		</tr>
            		</table>
            		</fieldset>
          		</td>
        	</tr>
      </table>
      </fieldset>
    </td></tr>
</table>
<script language="javascript">
var formula_id = '${budgetformulaForm.formula_id}';
var tj_type = '${budgetformulaForm.tj_type}';
defSetCondView(formula_id,tj_type);
</script>

</html:form>

