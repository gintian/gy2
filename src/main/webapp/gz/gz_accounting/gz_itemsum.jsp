<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>				 
 <%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<script type="text/javascript">

function selectAll()
{		
	var objs=eval("document.accountingForm.targetCalcItemt");	
	var objectAll=document.getElementById("checkAll");
  	var item_ids=new Array();	
	if(objs.length)
	{
		for(var i=0;i<objs.length;i++)
		{		
			if(objectAll.checked)
			{
				objs[i].checked=true;
			}
			else
			{
				objs[i].checked=false;
			}
			item_ids[i]=objs[i].value;				
		}
	}		
	
	var hashvo=new ParameterSet();
	hashvo.setValue("opt","2");
	hashvo.setValue("itemids",item_ids);	
	hashvo.setValue("filterWhl",document.accountingForm.filterWhl.value);
	hashvo.setValue("salaryid","${accountingForm.salaryid}");
	hashvo.setValue("sp","${accountingForm.sp}");	
	hashvo.setValue("a_code","${accountingForm.a_code}");	
	hashvo.setValue("sql","${accountingForm.sql}");	
	var request=new Request({method:'post',asynchronous:false,onSuccess:salarySumAll,functionId:'3020070287'},hashvo);	
}
function salarySumAll(outparamters)
{
	var _sumsalary=outparamters.getValue("sumsalarys");
	var decwidth=outparamters.getValue("decwidths");	
	
	for(var i=0;i<_sumsalary.length;i++)
	{
		var salary=_sumsalary[i];
		var theObj2="targetCalcItems"+i;
		var decth=2;		
		changeItemSum(theObj2,salary,decth);
	}		
}

function sumsalary(theObj)
{
	var obj=document.getElementById(theObj).value;
	var hashvo=new ParameterSet();
	hashvo.setValue("opt","1");
	hashvo.setValue("itemid",obj);
	hashvo.setValue("theObj1",theObj);	
	hashvo.setValue("filterWhl",document.accountingForm.filterWhl.value);
//	hashvo.setValue("a_code","${accountingForm.a_code}");
	hashvo.setValue("salaryid","${accountingForm.salaryid}");	
	hashvo.setValue("sp","${accountingForm.sp}");
	hashvo.setValue("a_code","${accountingForm.a_code}");	
	hashvo.setValue("sql","${accountingForm.sql}");		
	var request=new Request({method:'post',asynchronous:false,onSuccess:salarySum,functionId:'3020070287'},hashvo);
}
function salarySum(outparamters)
{
	var _theObj1=outparamters.getValue("theObj1");
	var _sumsalary=outparamters.getValue("sumsalary");
	var decwidth=outparamters.getValue("decwidth");
	changeItemSum(_theObj1,_sumsalary,decwidth);
}
function changeItemSum(objec,sum1,decwidth1)
{		
	var theObj=document.getElementById(objec);
	var obj_tr = theObj.parentNode.parentNode;
	var theTable,newRow;
	if(theObj.checked)
	{
		theTable = document.getElementById("targetTraceTable");
		
		var objs = document.getElementsByTagName('input');
		for(var i=0;i<objs.length;i++)
		{					
			if(objs[i].type=="hidden" && objs[i].name=='targetCalcItems' && objs[i].value==theObj.value)
			{
				var temp_tr = objs[i].parentNode.parentNode;
				for(var x=0;x<theTable.rows.length;x++)
				{							     
					if(theTable.rows[x]==temp_tr) 
					{								
						theTable.deleteRow(x);
						break;
					}
				}
				 break;
			}
		}
				
		newRow=theTable.insertRow(theTable.rows.length);
		insertrow(newRow,obj_tr,"targetCalcItems",theObj,sum1,decwidth1);	 
	}						
	else
	{
		theTables = document.getElementById("targetTraceTable");
		var objs = document.getElementsByTagName('input'); 
		for(var i=0;i<objs.length;i++)
		{		
			if(objs[i].type=="hidden" && objs[i].name=='targetCalcItems' && objs[i].value==theObj.value)
			{
				var temp_tr = objs[i].parentNode.parentNode;
				for(var x=0;x<theTables.rows.length;x++)
				{							     
					if(theTables.rows[x]==temp_tr) 
					{	 						
						theTables.deleteRow(x);
						break;
					}
				}
				break;
			}
		}
		 
	}
}

function insertrow(newRow,CopyRow,thename,checkObj,sum1,decwidth1)
{
	var tabstr = "";
	var salary = "";
	myNewCell=newRow.insertCell(0);
	myNewCell.className = "RecordRow";
	myNewCell.align="left";	
	tabstr="<input type=\"hidden\" name=\""+thename+"\" value=\""+checkObj.value+"\"/>";		
	myNewCell.innerHTML = tabstr+CopyRow.cells[1].innerHTML;	
	/* 薪资发放-项目合计样式调整 xiaoyun 2014-9-26 start */
	myNewCell.style.cssText="border-left:none;";	
	/* 薪资发放-项目合计样式调整 xiaoyun 2014-9-26 end */	
	myNewCell=newRow.insertCell(1);
	myNewCell.className = "RecordRow";
	/* 薪资发放-项目合计样式调整 xiaoyun 2014-9-26 start */
	myNewCell.style.cssText="border-right:none;";
	/* 薪资发放-项目合计样式调整 xiaoyun 2014-9-26 end */
	if(sum1!=null && sum1!=0)
		salary=sum1;
	else if(decwidth1==2){
		salary='0.00';
	}else
		salary='0';
	myNewCell.innerHTML = salary+"&nbsp;&nbsp;";
	myNewCell.align="right";
}
function goback()
{
	window.close();	
}	
</script>

<style type="text/css">

#scroll_box {
    border: 0px solid #94B6E6;
    BORDER-BOTTOM:#94B6E6 1pt solid;
    BORDER-TOP: #94B6E6 1pt solid;
    height: 360px;               
    overflow: auto;            
    margin: 1em 0;
}
</style>

<script language="Javascript" src="/gz/salary.js"/></script>

<html:form action="/gz/gz_accounting/itemsum">

<center>
<%if("hl".equals(hcmflag)){ %>
<table width="100%" height="440" border="0" align="center">
<%}else{ %>
<table width="645px" height="440" border="0" align="center" style="margin-top:-5px;margin-left:-3px;">
<%} %>

<tr>
<td>
<fieldset style="width:100%;">
<legend><bean:message key="label.gz.lookitemsum"/></legend><!-- 查看项目合计 -->
<table width="100%" height="400" border="0" align="center">
  <tr> 
    <td align="center" width="40%"> 
    <fieldset style="width:100%;">
    <legend><bean:message key="label.gz.itemback"/></legend>  <!-- 备选项目 -->
          <div id="scroll_box">
			<table id='targetDefineTable' width="100%" border="0" class="ListTable1">
				<thead>
				<tr style="position:relative;top:expression(this.offsetParent.scrollTop-1);">
            		<td align="center" class="TableRow" nowrap style="border-left:0px;">
  						<input type="checkbox" name="check" id='checkAll' value="1" onclick='selectAll();' />
  					</td>  					
					<td align="center" class="TableRow" nowrap><bean:message key="kh.field.fieldname"/>&nbsp;</td><!-- 名称 -->
				</tr>
				</thead>
								
				<logic:iterate id="element" name="accountingForm" property="itemSumList" indexId="index">
					<tr>
						<td  align="center" class="RecordRow_right" nowrap width="15%">
							<input id="targetCalcItems<%=index %>" onclick="sumsalary('targetCalcItems<%=index %>');"  name="targetCalcItemt" type="checkbox" value="<bean:write name="element" property="itemid" filter="true" />"/>																			
						</td>
						<td  align="left" class="RecordRow" nowrap>&nbsp;&nbsp;
							<bean:write name="element" property="itemdesc" filter="true" />
						</td>																							
					</tr>				
				</logic:iterate>				
			</table>
		  </div>	
      </fieldset>
    </td>
       
    <td align="center"> 
      <fieldset style="width:100%;">
      <legend><bean:message key="label.gz.selecteditem"/></legend><!-- 已选项目 -->
      <div id="scroll_box">
      <table id='targetTraceTable' width="100%" border="0" style="border-right: none;" class="ListTable1">
      	<thead>
			<tr style="position:relative;top:expression(this.offsetParent.scrollTop-1);">
				<td align="center" width="50%" class="TableRow" style="border-left: none;" nowrap ><bean:message key="column.name"/>&nbsp;</td> 	<!-- 名称 -->				
				<td align="center" width="50%" class="TableRow" style="border-right: none;" nowrap ><bean:message key="label.gz.sumvalue"/>&nbsp;</td><!-- 合计值 -->
			</tr>
		</thead>
				                         
      </table> 
      </div>
     </fieldset>
    </td>
  </tr>
</table>
</fieldset>
</td>
</tr>
	<tr align="center">
		<td>
			<input type='button'  class="mybutton" value=' 取消 '  onclick='goback()' />
		</td>
	</tr>
</table>
</center> 
<html:hidden name="accountingForm" property="filterWhl" />
<html:hidden name="accountingForm" property="sp" />
<html:hidden name="accountingForm" property="a_code" />
<html:hidden name="accountingForm" property="sql" />
</html:form>
