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


<script language="javascript">

    function save(){
	     var vo=new Object();
	     vo.yearnum=budgetAllocationForm.yearnum.value;
	     vo.budgettype=budgetAllocationForm.budgettype.value;
	     vo.firstmonth=budgetAllocationForm.firstmonth.value;
	     vo.bb203=budgetAllocationForm.bb203.value;
     	 var hashvo = new ParameterSet();
	     hashvo.setValue("vo",vo);
	     var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'302001020515'},hashvo); 
    }
     function save_ok(outparamters){
      	 var checkBudgetType=outparamters.getValue("checkBudgetType");
      	 if(checkBudgetType!=null&&checkBudgetType!=""){
      	 	alert(checkBudgetType);
      	 	return;
      	 }
      	 var a=budgetAllocationForm.bb203.value;
	     if(a.length==0){
	     	alert(GZ_TOTAL_BUDGET);
	     	return;
	     }
      	 var vo=new Object();
	     vo.yearnum=budgetAllocationForm.yearnum.value;
	     vo.budgettype=budgetAllocationForm.budgettype.value;
	     vo.firstmonth=budgetAllocationForm.firstmonth.value;
	     vo.bb203=budgetAllocationForm.bb203.value;
      	 window.returnValue=vo;
	   	 window.close();    
	}
	  function IsDigit() 
	  { 
	    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
	  }
	  
	  function change() 
	  { 
      	var budgettype=budgetAllocationForm.budgettype.value
	    var type="1";
	    if(budgettype==2)
	    {
			type=2;
	    }
	    var hashvo=new ParameterSet(); 
		hashvo.setValue("type",type); 
   		var request=new Request({asynchronous:false,onSuccess:showSelectList,functionId:'302001020506'},hashvo);
	   
	  }
	function showSelectList(outparamters)
 	{
	 	var firstMonthlist=outparamters.getValue("firstMonthlist");
		AjaxBind.bind(budgetAllocationForm.firstmonth,firstMonthlist);
 	}

</script>
<html:form action="/gz/gz_budget/budget_allocation/budget_allocation" >
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

      <table width="450" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" style="margin-left:-5px;">
          <tr height="20">
       		<td align="left" class="TableRow" colspan="2">&nbsp;<bean:message key="gz.budget.budget_allocation.newBudget"/>&nbsp;</td>
          </tr> 
          
	      <tr class="list3">
	   	      <td align="right" nowrap ><bean:message key="gz.budget.budget_allocation.yearnum"/>&nbsp;</td><!--預算年度  -->
	   	      <td align="left" nowrap >		
				<hrms:optioncollection name="budgetAllocationForm" property="yearNumlist" collection="list" />
					<html:select name="budgetAllocationForm" property="yearnum"   size="1" style="width:150px;">
						<html:options collection="list"  property="dataValue" labelProperty="dataName"/>
					</html:select>
	          </td>
	      </tr>
                      
          <tr class="list3">
          	<td align="right" nowrap valign="top"><bean:message key="gz.budget.budget_allocation.type"/>&nbsp;</td><!--预算类别  -->
            <td align="left"  nowrap>
            	<hrms:optioncollection name="budgetAllocationForm" property="budgetTypelist" collection="list" />
					<html:select name="budgetAllocationForm" property="budgettype" size="1" onchange="change()"  style="width:150px;">
						<html:options collection="list"  property="dataValue" labelProperty="dataName"/>
					</html:select>
            </td>
          </tr>
          <tr class="list3">
   	      	<td align="right" nowrap ><bean:message key="gz.budget.budget_allocation.startmonth"/>&nbsp;</td><!-- 开始月份 -->
   	      	<td align="left"  nowrap>
   	 	 		<hrms:optioncollection name="budgetAllocationForm" property="firstMonthlist" collection="list" />
					<html:select name="budgetAllocationForm" property="firstmonth" size="1" style="width:150px;">
						<html:options collection="list"  property="dataValue" labelProperty="dataName"/>
					</html:select>
			</td>
         </tr>
       	 <tr class="list3">
      		<td align="right" nowrap ><bean:message key="gz.budget.budget_allocation.zonge"/>&nbsp;</td><!--  预算总额 -->
      		<td align="left" nowrap >
      	  	<html:text name="budgetAllocationForm" property="bb203" onkeypress="event.returnValue=IsDigit();" maxlength="8" styleClass="inputtext" style="width:150px;"></html:text>(万)
        	</td>
     	</tr>
                      
	</table>
	<table align="center">
    	<tr class="list3">
          <td align="center"  style="height:35px">   
          	<input type="button" class="mybutton" value="<bean:message key="button.save"/>" onclick="save()" />
		    <input type="button" class="mybutton" value="<bean:message key="button.cancel"/>" onclick="window.close()" />
          </td>
        </tr>          
     </table>
</html:form>


