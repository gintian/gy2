<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hjsj.hrms.actionform.performance.objectiveManage.objectiveEvaluate.ObjectiveEvaluateForm" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant"%>
<%
	String isEpmLoginFlag="0";	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  isEpmLoginFlag=(String)userView.getHm().get("isEpmLoginFlag"); 
	  isEpmLoginFlag = (isEpmLoginFlag==null||isEpmLoginFlag.equals(""))?"0":isEpmLoginFlag;
	  hcmflag=userView.getBosflag();
	}

   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   String buttonClass="mybutton";
   boolean scoreStatus=false;
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
      buttonClass="mybuttonBig";
      scoreStatus=true;
   }
   String clientName = SystemConfig.getPropertyValue("clientName");
   
   boolean hasEvaluateRecord=false; //是否有评估的记录
 %>
 <link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<%  
ObjectiveEvaluateForm objectiveEvaluateForm = (ObjectiveEvaluateForm)session.getAttribute("objectiveEvaluateForm");
String returnflag=objectiveEvaluateForm.getReturnflag();
if(returnflag==null)
	returnflag="menu";
String url_extends="";
if(returnflag.equals("10"))
	url_extends="&returnflag=10";
else
	url_extends="&returnflag=menu";
String target="il_body";
String opt=request.getParameter("opt");
if(opt==null||opt.length()<1){
	opt="3";
}
String lt="&zglt=0";
String entranceType=objectiveEvaluateForm.getEntranceType();
if(!entranceType.equals("0"))
{
      target="_self";
      opt="4";
      lt="&zglt=4";
}
 String evaluate="org.performance.evaluate";
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
   {
     evaluate="org.performance.khtime";
   }
%>
<script type="text/javascript">
var pa="-1";
function query()
{
   var sort =document.getElementById("sort").value; 
   var order=document.getElementById("order").value;
   
   objectiveEvaluateForm.action="/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&opt=2&planid=${objectiveEvaluateForm.plan_id}&isSort="+sort+"&isOrder="+order;
   objectiveEvaluateForm.submit();
}
function ObjectiveEvaluate(opt,planid,a0100,body_id)
{
   if(pa =="-1"||pa!=planid+a0100)
   {
      document.getElementById("UL").value=document.getElementById("UL").value+"&scroll="+document.all.dataArea.scrollTop;
      pa=planid+a0100;
      var entranceType="${objectiveEvaluateForm.entranceType}";
      var pendingCode = "${objectiveEvaluateForm.pendingCode}";  //将代办编码传递到评分界面   2013.12.28  pjf
      objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query<%=lt%><%=url_extends%>&entranceType="+entranceType+"&pendingCode="+pendingCode+"&model=4&opt="+opt+"&planid="+planid+"&object_id="+a0100+"&body_id="+body_id;
      objectCardForm.submit();
   }
   else
   {
     window.setTimeout('setPAValue()',2000);   
   }
}

function setPAValue()
{
   pa="-1";
}
function selectAllRecord()
{
 var records=document.getElementsByName("records");
 var allselect=document.getElementById("select");
 if(records)
 {
     for(var i=0;i<records.length;i++)
     {
        if(allselect.checked)
        {
           records[i].checked=true;
        }
        else
        {
            records[i].checked=false;
        }
     }
 }
}
//批量复制
function allBatchCopy(){
   var records=document.getElementsByName("records");//records[i].value  考核对象-计划号-状态           status=0：查看 status=1：评估
   var num=0;
   var selectRecords="";
   if(records)
   {
      for(var i=0;i<records.length;i++)
      {
         if(records[i].checked)
         {
            var arr=records[i].value.split("-");
            if(arr[2]=='1'){
               num++;
               selectRecords+="/"+records[i].value;
             }
         }
      }
   }
   if(num==0)
   {
      alert("请选择可以复制的记录！");
      return;
   }
    var hashVo=new ParameterSet();
    hashVo.setValue("records",selectRecords.substring(1));
    hashVo.setValue("model","4"); 
	var request=new Request({method:'post',asynchronous:false,onSuccess:allBatchCopyOk,functionId:'9028000418'},hashVo);	
}

function allBatchCopyOk(outparameters){
    var infor=outparameters.getValue("info");
	if(infor=="ok"){
		alert("复制成功!");
	}
	if(infor=="none"){
		alert("无符合条件的复制数据!");
		return;
	}
   var sort =document.getElementById("sort").value; 
   var order=document.getElementById("order").value
   
   objectiveEvaluateForm.action="/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&opt=2&planid=${objectiveEvaluateForm.plan_id}&isSort="+sort+"&isOrder="+order;
   objectiveEvaluateForm.submit();
}
function exportObjectiveCard()
{
   var records=document.getElementsByName("records");
   var num=0;
   var selectRecords="";
   
   var opt="";
   var body_id="";
   var  obj1 = document.getElementsByName("optflag");
   var  obj2 = document.getElementsByName("levelflag");
   if(records)
   {
      for(var i=0;i<records.length;i++)
      {
         if(records[i].checked)
         {
            num++;
            selectRecords+="/"+records[i].value;
            opt+=obj1[i].value+"`";
            body_id+=obj2[i].value+"`";
            
         }
      }
   }
   if(num==0)
   {
      alert("请选择记录！");
      return;
   }
    var hashVo=new ParameterSet();
    hashVo.setValue("records",selectRecords.substring(1));
    hashVo.setValue("model",'4');
	hashVo.setValue("body_id",body_id);
	hashVo.setValue("underOpt",opt);
	hashVo.setValue("logo",'3');
	var In_parameters="opt=1";
	var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:export_ok,functionId:'9028000405'},hashVo);			
		  
}
function export_ok(outparameters)
{
  var fileName=outparameters.getValue("fileName");
//var win=open("/servlet/DisplayOleContent?filename="+fileName,"excel");
  //20/3/6 xus vfs改造
  var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true");
}
function changeSort(param)
{
  if(param=='0')
  {
    document.getElementById("sort").value="1";
  }
  if(param=='1')
  {
     document.getElementById("sort").value="0";
  }
  query();
} 
function changeOrder(param)
{
   if(param=='0')
  {
    document.getElementById("order").value="1";
  }
  if(param=='1')
  {
     document.getElementById("order").value="0";
  }
  query();
}
function scorlValue(Yvalue)
{
  document.getElementById("dataArea").scrollTop=Yvalue;
}

function goback(returnflag)
{
    var isEpmLoginFlag = "<%=isEpmLoginFlag %>";
    if(isEpmLoginFlag=="1"){
           window.location='/templates/index/subportal.do?b_query=link';
    }else{
		if(returnflag=="8"){
       		if('<%=hcmflag%>'=="hcm"){
 	      		 window.location='/templates/index/hcm_portal.do?b_query=link';      		
       		}else{
 	       		window.location='/templates/index/portal.do?b_query=link';      		
       		}
		}
		else if(returnflag=="10"){
		   window.location='/general/template/matterList.do?b_query=link';
		}
	}
}



function batchSub()
{
	var records=document.getElementsByName("records");
   var num=0;
   var selectRecords="";
   if(records)
   {
  /** for(var i=0;i<records.length;i++)
      {
	      var arr=records[i].value.split("-");
	      var unchecked = new Array();
	      if(!records[i].checked){
	      	unchecked[unchecked.length +1] = arr[1];
	      }
      }
   */
      for(var i=0;i<records.length;i++)
      {
         if(records[i].checked)
         {
            var arr=records[i].value.split("-");
            /**for(var j=0;j<unchecked.length;j++){
            	if(unchecked[j]==arr[i]){
            		alert("同一计划中有未打分或者未勾选的人员!");
            		return;
            	}
            }*/
            if(arr[2]=='1'){
               num++;
               selectRecords+="/"+records[i].value;
             }
         }
      }
   }
   if(num==0)
   {
      alert("请选择可提交记录！");
      return;
   }
    var hashVo=new ParameterSet();
    hashVo.setValue("records",selectRecords.substring(1));
    hashVo.setValue("model","4"); 
    hashVo.setValue("flag","0");
	var request=new Request({method:'post',asynchronous:false,onSuccess:validateOK,functionId:'9028000416'},hashVo);			
}
function validateOK(outparameters){
  var isHas = outparameters.getValue("isHas");
  var records=outparameters.getValue("records");
  var model=outparameters.getValue("model");
  if(isHas=='1')
  {
      if(!confirm("确认执行批量提交功能?"))
   		return;
   	var hashVo=new ParameterSet();
    hashVo.setValue("records",records); 
    hashVo.setValue("model",model);
    hashVo.setValue("flag","1");
	var request=new Request({method:'post',asynchronous:false,onSuccess:sub_ok,functionId:'9028000416'},hashVo);	
  }else{
      alert("没有可提交的记录!");
      return;
  }
}
function sub_ok(outparameters)
{
	var info=getDecodeStr(outparameters.getValue("info"));
	if(info.length>0)
		alert(info);
	var sort =document.getElementById("sort").value; 
    var order=document.getElementById("order").value
	objectiveEvaluateForm.action="/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&opt=2&planid=${objectiveEvaluateForm.plan_id}&isSort="+sort+"&isOrder="+order;
    objectiveEvaluateForm.submit();
}
function searchUntread(object_id,plan_id)
{
    var thecodeurl="/performance/objectiveManage/myObjective/my_objective_list.do?b_reject=link`opt=100`plan_id="+plan_id+"`object_id="+object_id;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
//	var retvo= window.showModalDialog(iframe_url, null,
//					        "dialogWidth:600px; dialogHeight:465px;resizable:no;center:yes;scroll:yes;status:no");
	var config = {
	    width:600,
	    height:465,
        type:'2'
    }
	modalDialog.showModalDialogs(iframe_url,"searchUntreadWin",config);

}
function allBatchSub(){
var hashVo=new ParameterSet();
var sort =document.getElementById("sort").value; 
var order=document.getElementById("order").value
 	hashVo.setValue("plan_id","${objectiveEvaluateForm.plan_id}");
 	hashVo.setValue("sort",sort);
 	hashVo.setValue("order",order);
var request=new Request({method:'post',asynchronous:false,onSuccess:allBatchValidateOK,functionId:'9028000417'},hashVo);	

}
function allBatchValidateOK(outparameters){
  var records=outparameters.getValue("records");
  var info=getDecodeStr(outparameters.getValue("info"));
  if(info.length>0){
		alert(info);
		return;
	}
  if(!confirm("确认执行全部提交功能?"))
  	return;
  if(records.length<1){
  	alert("没有可提交的计划！");
  	return;
  }
  var hashVo=new ParameterSet();
  	hashVo.setValue("records",records); 
	hashVo.setValue("model","4");
	hashVo.setValue("flag","1");
	var request=new Request({method:'post',asynchronous:false,onSuccess:sub_ok,functionId:'9028000416'},hashVo);	
}
function setdataAreaHeight(){
	var dataArea = document.getElementById("dataArea")
	var divheight = document.body.clientHeight-125
	dataArea.style.height=divheight+"px";
}
</script>
<html>

<head>
<title>title</title>
</head>
<body onload="javascript:setdataAreaHeight()">
<base id="mybase" target="_self">
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>
<html:form action="/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td align="left">
<table width="100%" style='margin:0 0 5px 0' border="0" cellspacing="0"  align="center" cellpadding="0">
<td align="left" <logic:notEqual  name="objectiveEvaluateForm" property="entranceType" value="0"> style="display=none" </logic:notEqual> style="height:20px">
<font class="<%=tt3CssName%>"><bean:message key="org.performance.evaluate"/></font>:
<html:select name="objectiveEvaluateForm" property="year" size="1" onchange="query();"><!-- 年 -->
			<html:optionsCollection property="yearList" value="dataValue" label="dataName"/>
		    </html:select>
		    <font class="<%=tt3CssName%>"><bean:message key="org.performance.year"/></font>
		    &nbsp;
		    <html:select name="objectiveEvaluateForm" property="quarter" size="1" onchange="query();"><!-- 季 -->
			<html:optionsCollection property="quarterList" value="dataValue" label="dataName"/>
		    </html:select>
		     <font class="<%=tt3CssName%>"><bean:message key="org.performance.quarter"/></font>
		    &nbsp;
		    <html:select name="objectiveEvaluateForm" property="month" size="1" onchange="query();"><!-- 月 -->
			<html:optionsCollection property="monthList" value="dataValue" label="dataName"/>
		    </html:select>
		     <font class="<%=tt3CssName%>"><bean:message key="org.performance.month"/></font>
		    &nbsp;
		    <font class="<%=tt3CssName%>"><bean:message key="org.performance.status"/></font>:<!-- 状态 -->
		     <html:select name="objectiveEvaluateForm" property="status" size="1" onchange="query();">
			<html:optionsCollection property="statusList" value="dataValue" label="dataName"/>
		    </html:select>
</td>
</table>
</td>
</tr>
<tr>
<td class="RecordRow" style="padding: 0px" nowrap>
<div id="dataArea" style='overflow:auto;width:100%;height:1000px'>
<table width="100%" border="0" cellspacing="0"  style="margin-top:-1" align="center" cellpadding="0" class="ListTable">
<thead>
<tr id="myFixedTr">

  <td align="center" class="TableRow" style="border-left: 0px" nowrap>
<input type="checkbox" name="allselect" id="select" onclick="selectAllRecord();"/>
</td>
<logic:notEqual value="5" name="objectiveEvaluateForm" property="entranceType">
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.khname"/></font>&nbsp;
  </td>
  </logic:notEqual>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.unorum"/></font>&nbsp;
  </td>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="hire.employActualize.name"/></font>&nbsp;
  </td>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="<%=evaluate%>"/></font>&nbsp;
  </td>
 <!--   <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.khgrade"/></font>&nbsp;
  </td> --> 
   <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>">排名<logic:equal value="1" name="objectiveEvaluateForm" property="isSort"><a href='javascript:changeOrder("${objectiveEvaluateForm.isOrder}");'><img src="/images/sort.gif" border="0"/></a></logic:equal></font>&nbsp;
  </td>
  <%
  if(clientName!= null && clientName.equalsIgnoreCase("gwyjy")){
   %>
   <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.ljpm" /></font>&nbsp;
  </td>
   <%} %>
   <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>">总分<!--  <a href='javascript:changeSort("${objectiveEvaluateForm.isSort}");' style="cursor:hand"><logic:equal value="1" name="objectiveEvaluateForm" property="isSort">(取消排名)</logic:equal><logic:equal value="0" name="objectiveEvaluateForm" property="isSort">(按总分排名)</logic:equal></a>--></font>&nbsp;
  </td>
  <logic:equal name="objectiveEvaluateForm"  property="showWholeEvaluate" value="true">
  	 <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"> <bean:message key="org.performance.zt" /> </font>&nbsp;
  	</td>
  </logic:equal>
  <% if(scoreStatus){ %>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>">打分状态</font>&nbsp;
  </td>
  <%} %>
  <td align="center" class="TableRow" style="border-right: 0px" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="kh.field.opt"/></font>&nbsp;<!-- 操作 -->
  </td>
</tr>
</thead>
<% int j=0; %>
 <hrms:extenditerate id="element" name="objectiveEvaluateForm" property="personListForm.list" indexes="indexes"  pagination="personListForm.pagination" pageCount="40" scope="session">
 <%if(j%2==0){ %>
	     <tr class="trShallow" onClick="javascript:tr_onclick(this,'#E4F2FC')">
	     <%} else { %>
	     <tr class="trDeep" onClick="javascript:tr_onclick(this,'#E4F2FC')">
	     <%}%>
	     <input type="hidden" name="optflag" value="<bean:write name="element" property="opt"/> ">
	     <input type="hidden" name="levelflag" value="<bean:write name="element" property="level"/> ">
	       <td align="center" class="RecordRow" style="border-left: 0px" nowrap>
	    <input type="checkbox" name="records" value="<bean:write name="element" property="record"/>"/>
	     </td>
	     <logic:notEqual value="5" name="objectiveEvaluateForm" property="entranceType">
	     <td align="left" class="RecordRow" nowrap>
	      &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="name"/></font>&nbsp;
	     </td>
	     </logic:notEqual>
	      <td align="left" class="RecordRow" nowrap>
	      &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="b0110"/></font>&nbsp;
	     </td>
	      <td align="left" class="RecordRow" nowrap>
	      &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="a0101"/></font>&nbsp;
	     </td>
	      <td align="left" class="RecordRow" nowrap>
	      &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="evaluate"/></font>&nbsp;
	     </td>
	      <!--  
	      <td align="left" class="RecordRow" nowrap>
	      &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="gradedesc"/></font>&nbsp;
	     </td>  -->
	       <td align="right" class="RecordRow" nowrap>
	      &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="seq"/></font>&nbsp;&nbsp;
	     </td>
	      <logic:equal name="objectiveEvaluateForm"  property="showAccumulativRank" value="true">
  	 		<td align="center" class="RecordRow" nowrap>
 			&nbsp;<font class="<%=tt3CssName%>"> <bean:write name="element" property="avgScoreSeq"/> </font>&nbsp;
  			</td>
 		</logic:equal>
	      <td align="right" class="RecordRow" nowrap>
	      &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="score"/></font>&nbsp;&nbsp;
	     </td>
	     <logic:equal name="objectiveEvaluateForm"  property="showWholeEvaluate" value="true">
  	 		<td align="center" class="RecordRow" nowrap>
 			&nbsp;<font class="<%=tt3CssName%>"> <bean:write name="element" property="wholeEvaluateName"/> </font>&nbsp;
  			</td>
 		</logic:equal>
	     <% if(scoreStatus){ %>
	       <td align="left" class="RecordRow" nowrap>
	       &nbsp;<font class="<%=tt3CssName%>">
	     <logic:equal value="0" name="element" property="scorestatus">
	     未打分
	     </logic:equal>
	     <logic:equal value="2" name="element" property="scorestatus">
	     已提交
	     </logic:equal>
	     <logic:equal value="8" name="element" property="scorestatus">
	     已完成
	     </logic:equal>
	      <logic:equal value="1" name="element" property="scorestatus">
	     已保存&nbsp;<img src="/images/gif007.gif" border="0"/>
	     </logic:equal>
	     </font>
	     </td>
	     <%} %>
	      <td align="center" class="RecordRow" style="border-right: 0px" nowrap>
	    <logic:equal value="0" name="element" property="status">
	    <a href="javascript:ObjectiveEvaluate('<bean:write name="element" property="opt"/>','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">查看</font></a>
	    </logic:equal>
	    <logic:equal value="1" name="element" property="status">
		    <% hasEvaluateRecord=true; %>
		    <!-- 评估 -->
		    <a href="javascript:ObjectiveEvaluate('<bean:write name="element" property="opt"/>','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');">
		    	<font class="<%=tt3CssName%>">
		    		<bean:define id="optKey" value="${element.map.plan_id }${element.map.body_id }"></bean:define>
		    		<bean:message key='${objectiveEvaluateForm.optMap[optKey] }'/>
		    	</font>
		    </a>
	    </logic:equal>
	     <logic:equal value="1" name="element" property="isScoreUntread">
	      <a href="javascript:searchUntread('<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="mdplanid"/>')"/>[退回]</a>
	      </logic:equal>
	     </td>	  
	     </tr>
	     <% j++; %>
	     </hrms:extenditerate>
</table>
</div>
</td>
</tr>
<tr>
<td align="center">
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		   <td valign="bottom" class="tdFontcolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   ${objectiveEvaluateForm.personListForm.pagination.current}
					<bean:message key="label.page.sum"/>
		   ${objectiveEvaluateForm.personListForm.pagination.count}
					<bean:message key="label.page.row"/>
		   ${objectiveEvaluateForm.personListForm.pagination.pages}
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
            <hrms:paginationlink name="objectiveEvaluateForm" property="personListForm.pagination" nameId="personListForm" propertyId="personListProperty">
		   </hrms:paginationlink>
		   </p>
		   </td>
		</tr> 
</table>
</td> 

</tr>
<tr>

<td align="left" style="padding-top:3px" style="height:20px">
<html:hidden name="objectiveEvaluateForm" property="isSort" styleId="sort"/>
<html:hidden name="objectiveEvaluateForm" property="isOrder" styleId="order"/>
<hrms:priv func_id="06070401">  
<input type="button" name="export" class="<%=buttonClass%>" value="<bean:message key="info.appleal.state13"/>" onclick="exportObjectiveCard();"/>
</hrms:priv>
<hrms:priv func_id="06070406,06070112"> 
<% 

	if(!(clientName!=null&&clientName.equalsIgnoreCase("gwyjy"))){
       if(hasEvaluateRecord){ %>
<input type="button" name="sub" class="<%=buttonClass%>" value="批量提交" onclick="batchSub();"/>
<% 		}
	} %>
</hrms:priv>
<hrms:priv func_id="06070408"> 
 
<input type="button" name="sub" class="<%=buttonClass%>" value="全部提交" onclick="allBatchSub();"/>
 
</hrms:priv>
<hrms:priv func_id="06070409"> 
 
<input type="button" name="sub" class="<%=buttonClass%>" value="<bean:message key="lable.performance.batch.copy.lower.score"/>" onclick="allBatchCopy();"/><!-- 批量复制下属评分 -->
 
</hrms:priv>
<logic:notEqual value="0" name="objectiveEvaluateForm" property="entranceType">
     <input type='button' name='clo' class="<%=buttonClass%>" value="<bean:message key="button.close"/>" onclick="window.close();"/>
</logic:notEqual>
<logic:equal value="0" name="objectiveEvaluateForm" property="entranceType">
<% 
	String bosflg = userView.getBosflag();
	if("bi".equalsIgnoreCase(bosflg)) {
 		out.println("<input type='button' name='export' class="+buttonClass+" value='返回' onclick=\"window.location.href='/templates/index/bi_portal.do?b_query=link'\")' />");
 	}else if(returnflag.equals("8")||returnflag.equals("10")){

	out.println("<input type='button' name='export' class="+buttonClass+" value='返回' onclick='goback(\""+returnflag+"\")' />");

	}
 %>
</logic:equal>


</td>

</tr>
</table>
</html:form>
</td>
</tr>
<tr>
<td>
<html:form action="/performance/objectiveManage/objectiveCard">
<%if(request.getParameter("plan_id")!=null){ %>
<input type="hidden" id="UL" name="returnURL" value="/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&plan_id=${objectiveEvaluateForm.plan_id}&returnflag=10&opt=1&entranceType=0&isSort=0";/>
<%} else { %>
<input type="hidden" id="UL" name="returnURL" value="/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init2&planid=${objectiveEvaluateForm.plan_id}&isSort=${objectiveEvaluateForm.isSort}&entranceType=${objectiveEvaluateForm.entranceType}&opt=<%=opt%>&month=${objectiveEvaluateForm.month}&quarter=${objectiveEvaluateForm.quarter}&year=${objectiveEvaluateForm.year}&status=${objectiveEvaluateForm.status}"/>
<%} %>
<input type="hidden" name="target" value="<%=target%>"/>
</html:form>
</td></tr>
</table>
</body>
<script type="text/javascript">
	scorlValue("<%=request.getParameter("scroll")==null?"0":request.getParameter("scroll")%>");
	
	if(/msie/i.test(navigator.userAgent)){//该样式只在ie下生效
		document.getElementById("myFixedTr").className="fixedHeaderTr";
	}
</script>
</html>
