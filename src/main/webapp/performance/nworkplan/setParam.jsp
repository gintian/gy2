<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.actionform.performance.nworkplan.NworkplanForm" %>
<% 
NworkplanForm nworkplanForm=(NworkplanForm)session.getAttribute("nworkplanForm");
String clientName =  nworkplanForm.getClientName();
%>

<style>

.TableRow_self 
{	
	margin-left:auto;
	margin-right:auto;
	background-position : center;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	height:30px;
	font-weight: bold;	
	valign:middle;
}
.RecordRow_self 
{
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:40px;
}
.m_frameborder 
{
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 40px;
	height: 20px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
}
.m_arrow 
{
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
.m_input 
{
	width: 18px;
	height: 15px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 12px;
	text-align: right;
}
.m_inputline 
{
	width: 36px;
	height: 15px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 12px;
	text-align: right;
}
</style>

<script LANGUAGE=javascript src="/js/function.js"></script>
<script type="text/javascript">

function testNum(obj)
{
	if(obj.value!='' && !checkIsNum(obj.value))
	{ 
		alert('请输入正整数！');
		obj.value='12';
		obj.focus();
	}
	else
	{
		if(parseInt(obj.value)>50)
			obj.value='50';
		else if(parseInt(obj.value)<1)
			obj.value='1';
	}
}
function mincrease(obj_name,theMax) 
{
	var objs =document.getElementsByName(obj_name);      
  	if(objs==null)
  		return false;
  	var obj=objs[0];
  	if(parseInt(obj.value)<theMax)
		obj.value = (parseInt(obj.value)+1)+'';
}
function msubtract(obj_name,theMin) 
{
    var objs =document.getElementsByName(obj_name);      
  	if(objs==null)
  		return false;
  	var obj=objs[0];
  	if(parseInt(obj.value)>theMin)
		obj.value = (parseInt(obj.value)-1)+'';
}
function radioSelectime()
{		
	if(document.getElementById('timeToday').checked==true)
		document.getElementById('timeTable').disabled=true;
	else
		document.getElementById('timeTable').disabled=false;
	if(document.getElementById('timeTomorrow').checked==true)
		document.getElementById('timeTable').disabled=false;
	else
		document.getElementById('timeTable').disabled=true;					
}
function IsDigit() 
{ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
//检验数字类型
function checkValue(obj)
{
  	if(obj.value.length>0)
  	{
  		if(!checkIsNum(obj.value))
  		{
  			alert('请输入正整数！');
  			obj.value='';
  			obj.focus();
  		}
  	} 
}

this.fObj = null;
var time_r = 0; 
function setFocusObj(obj,time_vv) 
{		
	this.fObj = obj;
	time_r = time_vv;		
}
function IsInputTimeValue() 
{	     
	event.cancelBubble = true;
    var fObj=this.fObj;		
    if (!fObj) return;		
    var cmd = event.srcElement.innerText=="5"?true:false;
    if(fObj.value==""||fObj.value.lenght<=0)
	fObj.value="0";
    var i = parseInt(fObj.value,10);		
    var radix=parseInt(time_r,10)-1;				
    if (i==radix&&cmd) {
    	i = 0;
    } else if (i==0&&!cmd) {
	   	i = radix;
    } else {
	   	cmd?i++:i--;
    }	
    if(i==0)
    {
	  	fObj.value = "00"
    }else if(i<10&&i>0)
    {
	  	fObj.value="0"+i;
    }else{
	  	fObj.value = i;
    }			
    fObj.select();
}

// 保存参数设置
function getTargetTraceItems(elementName)
{
	var items = document.getElementsByName(elementName);
	var itemStr='';
	for(var i=0;i<items.length;i++)
	{
		if(items[i].checked==true)
			itemStr+=items[i].value+',';
	}
	return itemStr;
}

function save()
{
	var plan_fields4 = document.getElementById("plan_fields4").value;
	var plan_fields3 = document.getElementById("plan_fields3").value;
	var plan_fields2 = document.getElementById("plan_fields2").value;
	var plan_fields1 = document.getElementById("plan_fields1").value;
	var plan_fields0 = document.getElementById("plan_fields0").value;
	var summarize_fields4 = document.getElementById("summarize_fields4").value;
	var summarize_fields3 = document.getElementById("summarize_fields3").value;
	var summarize_fields2 = document.getElementById("summarize_fields2").value;
	var summarize_fields1 = document.getElementById("summarize_fields1").value;
	var summarize_fields0 = document.getElementById("summarize_fields0").value;
	var errorstr = '';
	if(plan_fields4==''){
	      errorstr+='年工作计划指标\r\n';
	}
	if(summarize_fields4==''){
	      errorstr+='年工作总结指标\r\n';
	}
	if(plan_fields3==''){
	      errorstr+='季工作计划指标\r\n';
	}
	if(summarize_fields3==''){
	      errorstr+='季工作总结指标\r\n';
	}
	if(plan_fields2==''){
	      errorstr+='月工作计划指标\r\n';
	}
	if(summarize_fields2==''){
	      errorstr+='月工作总结指标\r\n';
	}
	if(plan_fields1==''){
	      errorstr+='周工作计划指标\r\n';
	}
	if(summarize_fields1==''){
	      errorstr+='周工作总结指标\r\n';
	}
	if(plan_fields0==''){
	      errorstr+='日工作计划指标\r\n';
	}
	if(summarize_fields0==''){
	      errorstr+='日工作总结指标\r\n';
	}
	if(errorstr!=''){
	      errorstr='请选择:\r\n'+errorstr;
	      alert(errorstr);
	      return false;
	}
	nworkplanForm.nbase.value=getTargetTraceItems("dbnameItems");
   	nworkplanForm.action="/performance/nworkplan/setParam.do?b_save=save";
   	nworkplanForm.submit();
}

// 日志指标设置
function dailyTargetSet()
{	  	
	var target_url="/performance/nworkplan/setParam.do?b_dailyTargetSet=link`oper=init";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, "", 
	              "dialogWidth:580px; dialogHeight:520px;resizable:no;center:yes;scroll:no;status:no");
	if(!return_vo)
		return false;	
	if(return_vo.flag=="true")
	{   		
		nworkplanForm.planTarget.value=return_vo.planTarget;
		nworkplanForm.summTarget.value=return_vo.summTarget;		
	}
}

function selectAllQuterPlan()
{
	if(document.getElementById("valid13").checked)
	{
		document.getElementById("period131").checked=true;
		document.getElementById("period132").checked=true;
		document.getElementById("period133").checked=true;
		document.getElementById("period134").checked=true;
	}
	else
	{
		document.getElementById("period131").checked=false;
		document.getElementById("period132").checked=false;
		document.getElementById("period133").checked=false;
		document.getElementById("period134").checked=false;
	}
}
function selectAllQuterSum()
{
	if(document.getElementById("valid23").checked)
	{
		document.getElementById("period231").checked=true;
		document.getElementById("period232").checked=true;
		document.getElementById("period233").checked=true;
		document.getElementById("period234").checked=true;
	}
	else
	{
		document.getElementById("period231").checked=false;
		document.getElementById("period232").checked=false;
		document.getElementById("period233").checked=false;
		document.getElementById("period234").checked=false;
	}
}
function selectAllMonthPlan()
{
	if(document.getElementById("valid12").checked)
	{
		document.getElementById("period121").checked=true;
		document.getElementById("period122").checked=true;
		document.getElementById("period123").checked=true;
		document.getElementById("period124").checked=true;
		document.getElementById("period125").checked=true;
		document.getElementById("period126").checked=true;
		document.getElementById("period127").checked=true;
		document.getElementById("period128").checked=true;
		document.getElementById("period129").checked=true;
		document.getElementById("period1210").checked=true;
		document.getElementById("period1211").checked=true;
		document.getElementById("period1212").checked=true;
	}
	else
	{
		document.getElementById("period121").checked=false;
		document.getElementById("period122").checked=false;
		document.getElementById("period123").checked=false;
		document.getElementById("period124").checked=false;
		document.getElementById("period125").checked=false;
		document.getElementById("period126").checked=false;
		document.getElementById("period127").checked=false;
		document.getElementById("period128").checked=false;
		document.getElementById("period129").checked=false;
		document.getElementById("period1210").checked=false;
		document.getElementById("period1211").checked=false;
		document.getElementById("period1212").checked=false;
	}
}
function selectAllMonthSum()
{
	if(document.getElementById("valid22").checked)
	{
		document.getElementById("period221").checked=true;
		document.getElementById("period222").checked=true;
		document.getElementById("period223").checked=true;
		document.getElementById("period224").checked=true;
		document.getElementById("period225").checked=true;
		document.getElementById("period226").checked=true;
		document.getElementById("period227").checked=true;
		document.getElementById("period228").checked=true;
		document.getElementById("period229").checked=true;
		document.getElementById("period2210").checked=true;
		document.getElementById("period2211").checked=true;
		document.getElementById("period2212").checked=true;
	}
	else
	{
		document.getElementById("period221").checked=false;
		document.getElementById("period222").checked=false;
		document.getElementById("period223").checked=false;
		document.getElementById("period224").checked=false;
		document.getElementById("period225").checked=false;
		document.getElementById("period226").checked=false;
		document.getElementById("period227").checked=false;
		document.getElementById("period228").checked=false;
		document.getElementById("period229").checked=false;
		document.getElementById("period2210").checked=false;
		document.getElementById("period2211").checked=false;
		document.getElementById("period2212").checked=false;
	}
}
function setWorkPlanItem(typeflag){
    var target_url="/performance/nworkplan/setParam.do?b_setValidFields=link`oper=init`typeflag="+typeflag;
    target_url+="`plan_fields"+typeflag+"="+document.getElementById("plan_fields"+typeflag).value;
    target_url+="`summarize_fields"+typeflag+"="+document.getElementById("summarize_fields"+typeflag).value;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var return_vo= window.showModalDialog(iframe_url, "", 
	              "dialogWidth:580px; dialogHeight:520px;resizable:no;center:yes;scroll:no;status:no");
	if(!return_vo)
		return false;	
	if(return_vo.flag=="true")
	{   		
		document.getElementById("plan_fields").value=return_vo.plan_fields;
		document.getElementById("summarize_fields").value=return_vo.summarize_fields;
		document.getElementById("plan_fields"+typeflag).value=return_vo.plan_fields;
		document.getElementById("summarize_fields"+typeflag).value=return_vo.summarize_fields;
	}
}
</script>
<%if(!clientName.equals("gw")){ %>	
<html:form action="/performance/nworkplan/setParam">
	<html:hidden name="nworkplanForm" property="nbase" styleId="nbase"/>	
	<html:hidden name="nworkplanForm" property="planTarget" styleId="planTarget"/>	
	<html:hidden name="nworkplanForm" property="summTarget" styleId="summTarget"/>			
	<br>
	<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">	
										
		<tr><td width='100%' style="border-top: 0px solid #8EC2E6;"  >		
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
			<thead>
		        <tr>
	                 <td align="center" width='20%' style='color:black' colspan="2" class="TableRow_self" nowrap><bean:message key="label.org.type_org"/></td>					 
					 <td align="center" width='5%' style='color:black' class="TableRow_self" nowrap><bean:message key="general.defini.isOk"/></td>					 					 
					 <td align="center" width='20%' style='color:black' class="TableRow_self" nowrap><bean:message key="general.defini.tianbaotimelimit"/></td>
					 <td align="center" width='25%' style='color:black' class="TableRow_self" nowrap><bean:message key="general.defini.tianbaocycle"/></td>
					 <td align="center" width='15%' style='color:black' class="TableRow_self" nowrap><bean:message key="general.defini.referenceinformationtable"/></td>						 
					 <td align="center" width='15%' style='color:black' class="TableRow_self" nowrap><bean:message key="general.defini.dayinexporttable"/></td>	
				</tr>
			 </thead>
		 				 	 
		        <tr>		       		          		          		          		          
		           <td align='left' class='RecordRow_self' rowspan='2' nowrap>&nbsp;年报</td>		                  		           	 					       		          		          		          		          
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作计划</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid14" name="nworkplanForm" property="valid14" value="1" />					  
		           </td>		           
		           <td align='center' class='RecordRow_self' nowrap>
                  	  上期末后<html:text property="prior_end14" styleId="prior_end14" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天           	  
                  	  本期初前<html:text property="current_start14" styleId="current_start14" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                   </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id14" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id14" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>		           		           	           		           		           		                  		           
	 			</tr>
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作总结</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid24" name="nworkplanForm" property="valid24" value="1" />
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>                  	  
                  	  本期末后<html:text property="current_end24" styleId="current_end24" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                  	  下期初前<html:text property="last_start24" styleId="last_start24" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天
                   </td>
                   <td align='center' class='RecordRow_self' nowrap>
                   	  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id24" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id24" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>		           	           		           		           		                  		           
	 			</tr>
	 			
	 			
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' rowspan='2' nowrap>&nbsp;季报</td>    		           	 					       		          		          		          		          
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作计划</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid13" name="nworkplanForm" property="valid13" onclick='selectAllQuterPlan()' value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                  	  上期末后<html:text property="prior_end13" styleId="prior_end13" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天           	  
                  	  本期初前<html:text property="current_start13" styleId="current_start13" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                   </td>
                   <td align='left' class='RecordRow_self' nowrap>
                   	  &nbsp;
                   	  <html:checkbox styleId="period131" name="nworkplanForm" property="period131" value="1" />1季度
                   	  <html:checkbox styleId="period132" name="nworkplanForm" property="period132" value="1" />2季度
                   	  <html:checkbox styleId="period133" name="nworkplanForm" property="period133" value="1" />3季度
                   	  <html:checkbox styleId="period134" name="nworkplanForm" property="period134" value="1" />4季度
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id13" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id13" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>	           		           		           		                  		           
	 			</tr>
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作总结</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid23" name="nworkplanForm" property="valid23" onclick='selectAllQuterSum()' value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>                	  
                  	  本期末后<html:text property="current_end23" styleId="current_end23" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                  	  下期初前<html:text property="last_start23" styleId="last_start23" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天
                   </td>
                   <td align='left' class='RecordRow_self' nowrap>
                   	  &nbsp;
                   	  <html:checkbox styleId="period231" name="nworkplanForm" property="period231" value="1" />1季度
                   	  <html:checkbox styleId="period232" name="nworkplanForm" property="period232" value="1" />2季度
                   	  <html:checkbox styleId="period233" name="nworkplanForm" property="period233" value="1" />3季度
                   	  <html:checkbox styleId="period234" name="nworkplanForm" property="period234" value="1" />4季度
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id23" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id23" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>	           		           		           		                  		           
	 			</tr>
	 			
	 			
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' rowspan='2' nowrap>&nbsp;月报</td>	
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作计划</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid12" name="nworkplanForm" property="valid12" onclick='selectAllMonthPlan()' value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                  	  上期末后<html:text property="prior_end12" styleId="prior_end12" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天           	  
                  	  本期初前<html:text property="current_start12" styleId="current_start12" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                   </td>
                   <td align='left' class='RecordRow_self' nowrap>
                   	  &nbsp;
                   	  <html:checkbox styleId="period121" name="nworkplanForm" property="period121" value="1" />1月
                   	  <html:checkbox styleId="period122" name="nworkplanForm" property="period122" value="1" />2月
                   	  <html:checkbox styleId="period123" name="nworkplanForm" property="period123" value="1" />3月
                   	  <html:checkbox styleId="period124" name="nworkplanForm" property="period124" value="1" />4月
                   	  &nbsp;
                   	  <html:checkbox styleId="period125" name="nworkplanForm" property="period125" value="1" />5月
                   	  &nbsp;
                   	  <html:checkbox styleId="period126" name="nworkplanForm" property="period126" value="1" />6月
                   	  <br>
                   	  &nbsp;
                   	  <html:checkbox styleId="period127" name="nworkplanForm" property="period127" value="1" />7月
                   	  <html:checkbox styleId="period128" name="nworkplanForm" property="period128" value="1" />8月
                   	  <html:checkbox styleId="period129" name="nworkplanForm" property="period129" value="1" />9月
                   	  <html:checkbox styleId="period1210" name="nworkplanForm" property="period1210" value="1" />10月
                   	  <html:checkbox styleId="period1211" name="nworkplanForm" property="period1211" value="1" />11月
                   	  <html:checkbox styleId="period1212" name="nworkplanForm" property="period1212" value="1" />12月
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id12" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id12" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>	           		           		           		                  		           
	 			</tr>
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作总结</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid22" name="nworkplanForm" property="valid22" onclick='selectAllMonthSum()' value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>                 	  
                  	  本期末后<html:text property="current_end22" styleId="current_end22" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                  	  下期初前<html:text property="last_start22" styleId="last_start22" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天
                   </td>
                   <td align='left' class='RecordRow_self' nowrap>
                   	  &nbsp;
                   	  <html:checkbox styleId="period221" name="nworkplanForm" property="period221" value="1" />1月
                   	  <html:checkbox styleId="period222" name="nworkplanForm" property="period222" value="1" />2月
                   	  <html:checkbox styleId="period223" name="nworkplanForm" property="period223" value="1" />3月
                   	  <html:checkbox styleId="period224" name="nworkplanForm" property="period224" value="1" />4月
                   	  &nbsp;
                   	  <html:checkbox styleId="period225" name="nworkplanForm" property="period225" value="1" />5月
                   	  &nbsp;
                   	  <html:checkbox styleId="period226" name="nworkplanForm" property="period226" value="1" />6月
                   	  <br>
                   	  &nbsp;
                   	  <html:checkbox styleId="period227" name="nworkplanForm" property="period227" value="1" />7月
                   	  <html:checkbox styleId="period228" name="nworkplanForm" property="period228" value="1" />8月
                   	  <html:checkbox styleId="period229" name="nworkplanForm" property="period229" value="1" />9月
                   	  <html:checkbox styleId="period2210" name="nworkplanForm" property="period2210" value="1" />10月
                   	  <html:checkbox styleId="period2211" name="nworkplanForm" property="period2211" value="1" />11月
                   	  <html:checkbox styleId="period2212" name="nworkplanForm" property="period2212" value="1" />12月
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id22" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id22" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>	           		           		           		                  		           
	 			</tr>
	 			
	 			
	 			
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' rowspan='2' nowrap>&nbsp;周报</td>	    	 					       		          		          		          		          
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作计划</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid11" name="nworkplanForm" property="valid11" value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                  	  上期末后<html:text property="prior_end11" styleId="prior_end11" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天           	  
                  	  本期初前<html:text property="current_start11" styleId="current_start11" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                   </td>
                   <td align='center' class='RecordRow_self' nowrap>
                   	  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id11" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id11" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>	           		           		           		                  		           
	 			</tr>
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作总结</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid21" name="nworkplanForm" property="valid21" value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>                 	  
                  	  本期末后<html:text property="current_end21" styleId="current_end21" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                  	  下期初前<html:text property="last_start21" styleId="last_start21" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天
                   </td>
                   <td align='center' class='RecordRow_self' nowrap>
                   	  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id21" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id21" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>	           		           		           		                  		           
	 			</tr>
	 			
	 			
	 			
	 			
	 			<tr>		       		          		          		          		          		                     		           
		           <td align='center' class='RecordRow_self' colspan="2" nowrap>&nbsp;日报</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid0" name="nworkplanForm" property="valid0" value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
                       <tr>                       
                         <td align="left" width="60" nowrap> 
                         <html:radio name="nworkplanForm" property="current_date" styleId="timeToday" onclick="radioSelectime()" value="0"/> 当日&nbsp;                     
                         </td>
                         <td valign="middle" width="60" nowrap> 
                            <html:radio name="nworkplanForm" property="current_date" styleId="timeTomorrow" onclick="radioSelectime()" value="1"/> 次日&nbsp;                         
                         </td>
                         <td valign="middle" align="left">
                           <table border="0" cellspacing="0" id="timeTable" align="left" valign="bottom" cellpadding="0">
                             <tr>
		                       <td width="40" nowrap style="background-color:#FFFFFF"> 
		                         <div class="m_frameborder">
		                         <input type="text" class="m_input" maxlength="2" name="limit_HH" value="${nworkplanForm.limit_HH}" onfocus="setFocusObj(this,24);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="limit_MM" value="${nworkplanForm.limit_MM}" onfocus="setFocusObj(this,60);">
		                         </div>
		                       </td>
		                       <td>
		                         <table border="0" cellspacing="2" cellpadding="0">
		                            <tr><td><button id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
		                            <tr><td><button id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
		                         </table>
                              </td>  
                              <td>
                               &nbsp;
                              </td>                   
                          </tr>
                         </table>
                        </td>
                       </tr>
                     </table>
                   </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id0" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id0" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>		           		           		           		                  		           
	 			</tr>


				<tr>
					<td align='left' class='RecordRow_self' colspan="7" nowrap>	
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
							<td align="left" width="250" nowrap>		
								&nbsp;审批关系：<html:select name="nworkplanForm" property="sp_relation" size="1" style="width:180px" >
										<html:optionsCollection property="sp_relationList" value="dataValue" label="dataName" />
									</html:select>	
							</td>
							<td align="left" width="150" nowrap> 				
									&nbsp;						
									审批层级：<html:select name="nworkplanForm" property="sp_level" size="1" >
										<html:optionsCollection property="sp_levelList" value="dataValue" label="dataName" />
									</html:select>
							</td>
							<td align="left" width="100" nowrap>			
									&nbsp;						
									<html:checkbox styleId="record_grade" name="nworkplanForm" property="record_grade" value="1" />
									纪实评分																								
							</td>					
							<td align="left" width="120" nowrap>
							&nbsp;													
								<bean:message key="log.workplan.defaultLines" />：
							</td>
							<td align="left" width="40" nowrap>						
								<div class="m_frameborder">
									<html:text name="nworkplanForm" styleClass="m_inputline" property="defaultLines" size="2"
										onkeypress="event.returnValue=IsDigit(this);" onblur="testNum(this)" />
								</div>
							</td>
							<td align="left">																
								<table border="0" cellspacing="2" cellpadding="0">
									<tr><td><button id="0_up" class="m_arrow" onmouseup="mincrease('defaultLines',50);">5</button></td></tr>
									<tr><td><button id="0_down" class="m_arrow" onmouseup="msubtract('defaultLines',1);">6</button></td></tr>
								</table>							
							</td>							
							</tr>
						</table>
					</td>					
				</tr>
				<tr>
					<td align='left' class='RecordRow_self' colspan="7" nowrap>				
					&nbsp;附件上传设置：<html:checkbox styleId="dailyPlan_attachment" name="nworkplanForm" property="dailyPlan_attachment" value="1" />
						工作计划&nbsp;
						<html:checkbox styleId="dailySumm_attachment" name="nworkplanForm" property="dailySumm_attachment" value="1" />
						工作总结
						&nbsp;&nbsp;
						
						<input type='button' class="mybutton" onclick='dailyTargetSet();' id="dailyTargetSet_bt" 
							value='<bean:message key="log.workplan.dailyrecordTargetSet"/>'/>									
					</td>
				</tr>
				<tr>
					<td align='left' class='RecordRow_self' colspan="7" nowrap>				
					&nbsp;人员库设置：<logic:iterate id="element" name="nworkplanForm" property="dbnameList">						
							<input name="dbnameItems" type="checkbox"  
								value="<bean:write name="element" property="pre" filter="true" />"
								<logic:notEqual name="element" property="selected"
								value="0">checked</logic:notEqual> />									
							<bean:write name="element" property="dbname" filter="true" />									
						</logic:iterate>		
					</td>
				</tr>
			 	  
	  	</table>
		</td></tr>
		
		
		
		<tr>
			<td align="center" style="height:70px">				
				<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save();" />			
			</td>
		</tr>
		
	</table>	

	
	<script type="text/javascript">
		radioSelectime();												  
	</script>
	
</html:form>
<%}else{ %>
	<html:form action="/performance/nworkplan/setParam" >
	<html:hidden name="nworkplanForm" property="nbase" styleId="nbase"/>	
	<html:hidden name="nworkplanForm" property="planTarget" styleId="planTarget"/>	
	<html:hidden name="nworkplanForm" property="summTarget" styleId="summTarget"/>	
			
	<html:hidden name="nworkplanForm" property="plan_fields0" styleId="plan_fields0"/>			
	<html:hidden name="nworkplanForm" property="summarize_fields0" styleId="summarize_fields0"/>			
	<html:hidden name="nworkplanForm" property="plan_fields1" styleId="plan_fields1"/>			
	<html:hidden name="nworkplanForm" property="summarize_fields1" styleId="summarize_fields1"/>			
	<html:hidden name="nworkplanForm" property="plan_fields2" styleId="plan_fields2"/>			
	<html:hidden name="nworkplanForm" property="summarize_fields2" styleId="summarize_fields2"/>			
	<html:hidden name="nworkplanForm" property="plan_fields3" styleId="plan_fields3"/>			
	<html:hidden name="nworkplanForm" property="summarize_fields3" styleId="summarize_fields3"/>			
	<html:hidden name="nworkplanForm" property="plan_fields4" styleId="plan_fields4"/>			
	<html:hidden name="nworkplanForm" property="summarize_fields4" styleId="summarize_fields4"/>
	<html:hidden name="nworkplanForm" property="plan_fields" styleId="plan_fields"/>	
    <html:hidden name="nworkplanForm" property="summarize_fields" styleId="summarize_fields"/>			
	<br>
	<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">	
										
		<tr><td width='100%' style="border-top: 0px solid #8EC2E6;"  >		
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
			<thead>
		        <tr>
	                 <td align="center" width='12%' style='color:black' colspan="2" class="TableRow_self" nowrap><bean:message key="label.org.type_org"/></td>					 
					 <td align="center" width='4%' style='color:black' class="TableRow_self" nowrap><bean:message key="general.defini.isOk"/></td>					 					 
					 <td align="center" width='4%' style='color:black' class="TableRow_self" nowrap>是否审批</td>					 					 
					 <td align="center" width='20%' style='color:black' class="TableRow_self" nowrap><bean:message key="general.defini.tianbaotimelimit"/></td>
					 <td align="center" width='25%' style='color:black' class="TableRow_self" nowrap><bean:message key="general.defini.tianbaocycle"/></td>
					 <td align="center" width='15%' style='color:black' class="TableRow_self" nowrap><bean:message key="general.defini.referenceinformationtable"/></td>						 
					 <td align="center" width='15%' style='color:black' class="TableRow_self" nowrap><bean:message key="general.defini.dayinexporttable"/></td>
					 <td align="center" width='5%' style='color:black' class="TableRow_self" nowrap>有效指标</td>
				</tr>
			 </thead>
		        <tr>		       		          		          		          		          
		           <td align='left' class='RecordRow_self' rowspan='2' nowrap>&nbsp;年报</td>		                  		           	 					       		          		          		          		          
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作计划</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid14" name="nworkplanForm" property="valid14" value="1" />					  
		           </td>		           
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="sp_flag14" name="nworkplanForm" property="sp_flag14" value="1" />					  
		           </td>		           
		           <td align='center' class='RecordRow_self' nowrap>
                  	  上期末后<html:text property="prior_end14" styleId="prior_end14" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天           	  
                  	  本期初前<html:text property="current_start14" styleId="current_start14" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                   </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id14" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id14" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td rowspan="2" align='center' class='RecordRow_self' nowrap>
		             <img style="cursor: pointer;" onclick="setWorkPlanItem('4')" src="/images/edit.gif" border="0"/>
		           </td>		           		           	           		           		           		                  		           
	 			</tr>
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作总结</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid24" name="nworkplanForm" property="valid24" value="1" />
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="sp_flag24" name="nworkplanForm" property="sp_flag24" value="1" />
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>                  	  
                  	  本期末后<html:text property="current_end24" styleId="current_end24" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                  	  下期初前<html:text property="last_start24" styleId="last_start24" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天
                   </td>
                   <td align='center' class='RecordRow_self' nowrap>
                   	  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id24" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id24" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           		           	           		           		           		                  		           
	 			</tr>
	 			
	 			
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' rowspan='2' nowrap>&nbsp;季报</td>    		           	 					       		          		          		          		          
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作计划</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid13" name="nworkplanForm" property="valid13" onclick='selectAllQuterPlan()' value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="sp_flag13" name="nworkplanForm" property="sp_flag13"  value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                  	  上期末后<html:text property="prior_end13" styleId="prior_end13" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天           	  
                  	  本期初前<html:text property="current_start13" styleId="current_start13" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                   </td>
                   <td align='left' class='RecordRow_self' nowrap>
                   	  &nbsp;
                   	  <html:checkbox styleId="period131" name="nworkplanForm" property="period131" value="1" />1季度
                   	  <html:checkbox styleId="period132" name="nworkplanForm" property="period132" value="1" />2季度
                   	  <html:checkbox styleId="period133" name="nworkplanForm" property="period133" value="1" />3季度
                   	  <html:checkbox styleId="period134" name="nworkplanForm" property="period134" value="1" />4季度
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id13" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id13" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td rowspan="2" align='center' class='RecordRow_self' nowrap>
		             <img style="cursor: pointer;" onclick="setWorkPlanItem('3')" src="/images/edit.gif" border="0"/>
		           </td>	           		           		           		                  		           
	 			</tr>
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作总结</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid23" name="nworkplanForm" property="valid23" onclick='selectAllQuterSum()' value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="sp_flag23" name="nworkplanForm" property="sp_flag23"  value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>                	  
                  	  本期末后<html:text property="current_end23" styleId="current_end23" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                  	  下期初前<html:text property="last_start23" styleId="last_start23" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天
                   </td>
                   <td align='left' class='RecordRow_self' nowrap>
                   	  &nbsp;
                   	  <html:checkbox styleId="period231" name="nworkplanForm" property="period231" value="1" />1季度
                   	  <html:checkbox styleId="period232" name="nworkplanForm" property="period232" value="1" />2季度
                   	  <html:checkbox styleId="period233" name="nworkplanForm" property="period233" value="1" />3季度
                   	  <html:checkbox styleId="period234" name="nworkplanForm" property="period234" value="1" />4季度
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id23" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id23" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
	 			</tr>
	 			
	 			
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' rowspan='2' nowrap>&nbsp;月报</td>	
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作计划</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid12" name="nworkplanForm" property="valid12" onclick='selectAllMonthPlan()' value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="sp_flag12" name="nworkplanForm" property="sp_flag12"  value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                  	  上期末后<html:text property="prior_end12" styleId="prior_end12" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天           	  
                  	  本期初前<html:text property="current_start12" styleId="current_start12" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                   </td>
                   <td align='left' class='RecordRow_self' nowrap>
                   	  &nbsp;
                   	  <html:checkbox styleId="period121" name="nworkplanForm" property="period121" value="1" />1月
                   	  <html:checkbox styleId="period122" name="nworkplanForm" property="period122" value="1" />2月
                   	  <html:checkbox styleId="period123" name="nworkplanForm" property="period123" value="1" />3月
                   	  <html:checkbox styleId="period124" name="nworkplanForm" property="period124" value="1" />4月
                   	  &nbsp;
                   	  <html:checkbox styleId="period125" name="nworkplanForm" property="period125" value="1" />5月
                   	  &nbsp;
                   	  <html:checkbox styleId="period126" name="nworkplanForm" property="period126" value="1" />6月
                   	  <br>
                   	  &nbsp;
                   	  <html:checkbox styleId="period127" name="nworkplanForm" property="period127" value="1" />7月
                   	  <html:checkbox styleId="period128" name="nworkplanForm" property="period128" value="1" />8月
                   	  <html:checkbox styleId="period129" name="nworkplanForm" property="period129" value="1" />9月
                   	  <html:checkbox styleId="period1210" name="nworkplanForm" property="period1210" value="1" />10月
                   	  <html:checkbox styleId="period1211" name="nworkplanForm" property="period1211" value="1" />11月
                   	  <html:checkbox styleId="period1212" name="nworkplanForm" property="period1212" value="1" />12月
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id12" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id12" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>	
		           <td rowspan="2" align='center' class='RecordRow_self' nowrap>
		             <img style="cursor: pointer;" onclick="setWorkPlanItem('2')" src="/images/edit.gif" border="0"/>
		           </td>          		           		           		                  		           
	 			</tr>
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作总结</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid22" name="nworkplanForm" property="valid22" onclick='selectAllMonthSum()' value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="sp_flag22" name="nworkplanForm" property="sp_flag22"  value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>                 	  
                  	  本期末后<html:text property="current_end22" styleId="current_end22" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                  	  下期初前<html:text property="last_start22" styleId="last_start22" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天
                   </td>
                   <td align='left' class='RecordRow_self' nowrap>
                   	  &nbsp;
                   	  <html:checkbox styleId="period221" name="nworkplanForm" property="period221" value="1" />1月
                   	  <html:checkbox styleId="period222" name="nworkplanForm" property="period222" value="1" />2月
                   	  <html:checkbox styleId="period223" name="nworkplanForm" property="period223" value="1" />3月
                   	  <html:checkbox styleId="period224" name="nworkplanForm" property="period224" value="1" />4月
                   	  &nbsp;
                   	  <html:checkbox styleId="period225" name="nworkplanForm" property="period225" value="1" />5月
                   	  &nbsp;
                   	  <html:checkbox styleId="period226" name="nworkplanForm" property="period226" value="1" />6月
                   	  <br>
                   	  &nbsp;
                   	  <html:checkbox styleId="period227" name="nworkplanForm" property="period227" value="1" />7月
                   	  <html:checkbox styleId="period228" name="nworkplanForm" property="period228" value="1" />8月
                   	  <html:checkbox styleId="period229" name="nworkplanForm" property="period229" value="1" />9月
                   	  <html:checkbox styleId="period2210" name="nworkplanForm" property="period2210" value="1" />10月
                   	  <html:checkbox styleId="period2211" name="nworkplanForm" property="period2211" value="1" />11月
                   	  <html:checkbox styleId="period2212" name="nworkplanForm" property="period2212" value="1" />12月
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id22" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id22" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>	           		           		           		                  		           
	 			</tr>
	 			
	 			
	 			
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' rowspan='2' nowrap>&nbsp;周报</td>	    	 					       		          		          		          		          
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作计划</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid11" name="nworkplanForm" property="valid11" value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="sp_flag11" name="nworkplanForm" property="sp_flag11" value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                  	  上期末后<html:text property="prior_end11" styleId="prior_end11" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天           	  
                  	  本期初前<html:text property="current_start11" styleId="current_start11" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                   </td>
                   <td align='center' class='RecordRow_self' nowrap>
                   	  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id11" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id11" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td rowspan="2" align='center' class='RecordRow_self' nowrap>
		             <img style="cursor: pointer;" onclick="setWorkPlanItem('1')" src="/images/edit.gif" border="0"/>
		           </td>	           		           		           		                  		           
	 			</tr>
	 			<tr>		       		          		          		          		          		           
		           <td align='left' class='RecordRow_self' nowrap>&nbsp;工作总结</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid21" name="nworkplanForm" property="valid21" value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="sp_flag21" name="nworkplanForm" property="sp_flag21" value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>                 	  
                  	  本期末后<html:text property="current_end21" styleId="current_end21" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天                 	  
                  	  下期初前<html:text property="last_start21" styleId="last_start21" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit();" size="2" />天
                   </td>
                   <td align='center' class='RecordRow_self' nowrap>
                   	  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id21" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id21" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>	           		           		           		                  		           
	 			</tr>
	 			
	 			
	 			
	 			
	 			<tr>		       		          		          		          		          		                     		           
		           <td align='center' class='RecordRow_self' colspan="2" nowrap>&nbsp;日报</td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="valid0" name="nworkplanForm" property="valid0" value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
		           	  <html:checkbox styleId="sp_flag0" name="nworkplanForm" property="sp_flag0" value="1" />					  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
                       <tr>                       
                         <td align="left" width="60" nowrap> 
                         <html:radio name="nworkplanForm" property="current_date" styleId="timeToday" onclick="radioSelectime()" value="0"/> 当日&nbsp;                     
                         </td>
                         <td valign="middle" width="60" nowrap> 
                            <html:radio name="nworkplanForm" property="current_date" styleId="timeTomorrow" onclick="radioSelectime()" value="1"/> 次日&nbsp;                         
                         </td>
                         <td valign="middle" align="left">
                           <table border="0" cellspacing="0" id="timeTable" align="left" valign="bottom" cellpadding="0">
                             <tr>
		                       <td width="40" nowrap style="background-color:#FFFFFF"> 
		                         <div class="m_frameborder">
		                         <input type="text" class="m_input" maxlength="2" name="limit_HH" value="${nworkplanForm.limit_HH}" onfocus="setFocusObj(this,24);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="limit_MM" value="${nworkplanForm.limit_MM}" onfocus="setFocusObj(this,60);">
		                         </div>
		                       </td>
		                       <td>
		                         <table border="0" cellspacing="2" cellpadding="0">
		                            <tr><td><button id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
		                            <tr><td><button id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
		                         </table>
                              </td>  
                              <td>
                               &nbsp;
                              </td>                   
                          </tr>
                         </table>
                        </td>
                       </tr>
                     </table>
                   </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="refer_id0" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>
		           <td align='center' class='RecordRow_self' nowrap>
                   	  <html:select name="nworkplanForm" property="print_id0" size="1" >
						  <html:optionsCollection property="personSheetList" value="dataValue" label="dataName" />
					  </html:select>
		           </td>	
		           <td  align='center' class='RecordRow_self' nowrap>
		             <img style="cursor: pointer;" onclick="setWorkPlanItem('0')" src="/images/edit.gif" border="0"/>
		           </td>	           		           		           		                  		           
	 			</tr>


				<tr>
					<td align='left' class='RecordRow_self' colspan="9" nowrap>	
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
							<td align="left" width="250" nowrap>		
								&nbsp;审批关系：<html:select name="nworkplanForm" property="sp_relation" size="1" style="width:180px" >
										<html:optionsCollection property="sp_relationList" value="dataValue" label="dataName" />
									</html:select>	
							</td>
							<td align="left" width="150" nowrap> 				
									&nbsp;						
									审批层级：<html:select name="nworkplanForm" property="sp_level" size="1" >
										<html:optionsCollection property="sp_levelList" value="dataValue" label="dataName" />
									</html:select>
							</td>
							<td align="left" width="100" nowrap>			
									&nbsp;						
									<html:checkbox styleId="record_grade" name="nworkplanForm" property="record_grade" value="1" />
									纪实评分																								
							</td>					
							<td align="left" width="120" nowrap>
							&nbsp;													
								<bean:message key="log.workplan.defaultLines" />：
							</td>
							<td align="left" width="40" nowrap>						
								<div class="m_frameborder">
									<html:text name="nworkplanForm" styleClass="m_inputline" property="defaultLines" size="2"
										onkeypress="event.returnValue=IsDigit(this);" onblur="testNum(this)" />
								</div>
							</td>
							<td align="left">																
								<table border="0" cellspacing="2" cellpadding="0">
									<tr><td><button id="0_up" class="m_arrow" onmouseup="mincrease('defaultLines',50);">5</button></td></tr>
									<tr><td><button id="0_down" class="m_arrow" onmouseup="msubtract('defaultLines',1);">6</button></td></tr>
								</table>							
							</td>							
							</tr>
						</table>
					</td>					
				</tr>
				<tr>
					<td align='left' class='RecordRow_self' colspan="9" nowrap>				
					&nbsp;附件上传设置：<html:checkbox styleId="dailyPlan_attachment" name="nworkplanForm" property="dailyPlan_attachment" value="1" />
						工作计划&nbsp;
						<html:checkbox styleId="dailySumm_attachment" name="nworkplanForm" property="dailySumm_attachment" value="1" />
						工作总结
						&nbsp;&nbsp;
						
						<input type='button' class="mybutton" onclick='dailyTargetSet();' id="dailyTargetSet_bt" 
							value='<bean:message key="log.workplan.dailyrecordTargetSet"/>'/>									
					</td>
				</tr>
				<tr>
					<td align='left' class='RecordRow_self' colspan="9" nowrap>				
					&nbsp;人员库设置：<logic:iterate id="element" name="nworkplanForm" property="dbnameList">						
							<input name="dbnameItems" type="checkbox"  
								value="<bean:write name="element" property="pre" filter="true" />"
								<logic:notEqual name="element" property="selected"
								value="0">checked</logic:notEqual> />									
							<bean:write name="element" property="dbname" filter="true" />									
						</logic:iterate>		
					</td>
				</tr>
			 	  
	  	</table>
		</td></tr>
		
		
		
		<tr>
			<td align="center" style="height:70px">				
				<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save();" />			
			</td>
		</tr>
		
	</table>	

	
	<script type="text/javascript">
		radioSelectime();												  
	</script>
	
</html:form>
<%}%>