<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hjsj.hrms.actionform.performance.nworkplan.WeekWorkplanForm,
               org.apache.commons.beanutils.LazyDynaBean,
               java.util.ArrayList,
               com.hrms.struts.valueobject.UserView,
			   com.hrms.struts.constant.WebConstant
               "%>
<%
WeekWorkplanForm myForm=(WeekWorkplanForm)session.getAttribute("weekWorkplanForm");
String p0100 = myForm.getP0100();
String p0115 = myForm.getP0115();
String isChuZhang=myForm.getIsChuZhang();
String hyperlinkRecord = myForm.getHyperlinkRecord();
String hyperlinkP0100 = myForm.getHyperlinkP0100();
String personPage = myForm.getPersonPage();
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String opt="";
String returnurl="";
String isRead="";

if(userView != null){
	opt = (String)userView.getHm().get("opt"); 
	returnurl = (String)userView.getHm().get("returnurl"); 
	isRead = (String)userView.getHm().get("isRead"); 
}
%>               
<hrms:themes />
<style>
div#tbl-container 
{
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
strong
{
    font-size:18px;
}
.bh-clear{clear:both;height:0px;}
.bh-one{margin-bottom:0px;}
.m_inputline 
{
	width: 36px;
	height: 15px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 12px;
	text-align: right;
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
</style>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language="JavaScript" src="/performance/nworkplan/week/weekPlan.js"></script>
<script language="JavaScript" src="/performance/nworkplan/nworkplansp/workplansp.js"></script>
<script>
function tr_changeColor(objTr,bgcolor)
{
	/*
	if(curObjTr!=null)
		curObjTr.style.background=oldObjTr_c;
	curObjTr=objTr;
	oldObjTr_c=bgcolor;
	curObjTr.style.background='FFF8D2';
	* */
	if(curObjTr!=null)
		curObjTr.style.backgroundColor="";
	curObjTr=objTr;
	oldObjTr_c="FFF8D2";
	curObjTr.style.backgroundColor=bgcolor;		 
	//curObj.style.color='#ffdead'; 
}  
function testNum(obj,flag)
{
	if(obj.value!='' && !checkIsNum(obj.value))
	{ 
		alert('请输入正整数！');
		if(flag=='summarizeYear')
		   obj.value='${weekWorkplanForm.summarizeYear}';
		obj.focus();
	}
	else
	{
	   if(flag=='summarizeYear'){
			if(parseInt(obj.value)>9999)
				obj.value='9999';
			else if(parseInt(obj.value)<1)
				obj.value='1';
	   }else{
		    if(parseInt(obj.value)>9999)
				obj.value='12';
			else if(parseInt(obj.value)<1)
				obj.value='1';
	   }
	}
}
//提取
function collect(){
	 var st="${weekWorkplanForm.summarizeTime}";
	 if(st==''){
		 alert("没有上期计划数据!");
		 return;
	 }
	 var planYear_start=document.getElementById("planYear_start").value;
     var planMonth_start=document.getElementById("planMonth_start").value;
     var planDay_start=document.getElementById("planDay_start").value;
     var planYear_end=document.getElementById("planYear_end").value;
     var planMonth_end=document.getElementById("planMonth_end").value;
     var planDay_end=document.getElementById("planDay_end").value;
     //alert(planYear_start+"-"+planMonth_start+"-"+planDay_start+"----"+planYear_end+"-"+planMonth_end+"-"+planDay_end);
     if(planYear_start==''||planMonth_start==''||planDay_start==''||planYear_end==''||planMonth_end==''||planDay_end==''){
     	alert("计划起始时间和结束时间是必填项！");
     	return;
     }
     //如果修改了时间，要判断是否和部门其他人的时间一样
     var  iscontinue="0";
    // alert(global_sYear+"-"+global_sMonth+"-"+global_sDay+"--"+global_eYear+"-"+global_eMonth+"-"+global_eDay);
    // alert(isdeptother);
     if(isdeptother=='1'&&!(global_sYear*1==planYear_start*1&&global_sMonth*1==planMonth_start*1&&global_sDay*1==planDay_start*1&&global_eYear*1==planYear_end*1&&global_eMonth*1&&global_eDay*1==planDay_end*1)){
     	 iscontinue="1";
     	if(confirm("计划起始时间结束时间与部门其他人的计划区间不一致,部门其他人的区间为【"+global_sYear+"-"+global_sMonth+"-"+global_sDay+"--"+global_eYear+"-"+global_eMonth+"-"+global_eDay+"】\r\n点击确定将部门其他人的区间改为与自己的相同\r\n点击取消修改自己的区间与其他人相同")){
     		iscontinue="2";
     	}
     }
     if(iscontinue=="1"){
     	return;
     }
     var hashvo = new ParameterSet();
     hashvo.setValue("log_type","1");
     hashvo.setValue("personPage",'${weekWorkplanForm.personPage}');
     hashvo.setValue("isChuZhang",'${weekWorkplanForm.isChuZhang}');
     hashvo.setValue("state",'1');
     hashvo.setValue("summarizeYear",weekWorkplanForm.summarizeYear.value);
     hashvo.setValue("summarizeTime",weekWorkplanForm.summarizeTime.value);
     hashvo.setValue("planYear_start",planYear_start);
     hashvo.setValue("planMonth_start",planMonth_start);
     hashvo.setValue("planDay_start",planDay_start);
     hashvo.setValue("planYear_end",planYear_end);
     hashvo.setValue("planMonth_end",planMonth_end);
     hashvo.setValue("planDay_end",planDay_end);
   //  hashvo.setValue("type",type);
    // hashvo.setValue("record_num",record_num);
     hashvo.setValue("global_sYear",global_sYear);
     hashvo.setValue("global_sMonth",global_sMonth);
     hashvo.setValue("global_sDay",global_sDay);
     hashvo.setValue("global_eYear",global_eYear);
     hashvo.setValue("global_eMonth",global_eMonth);
     hashvo.setValue("global_eDay",global_eDay);
     hashvo.setValue("iscontinue",iscontinue);
     var request=new Request({method:'post',asynchronous:false,onSuccess:validateSuccC,functionId:'302001020615'},hashvo);
     
     
}
function validateSuccC(outparameters){
	var message = outparameters.getValue("message");
	if(message==''){
		var hashvo = new ParameterSet();
		var st="${weekWorkplanForm.summarizeTime}";
	     var planYear_start=document.getElementById("planYear_start").value;
	     var planMonth_start=document.getElementById("planMonth_start").value;
	     var planDay_start=document.getElementById("planDay_start").value;
	     var planYear_end=document.getElementById("planYear_end").value;
	     var planMonth_end=document.getElementById("planMonth_end").value;
	     var planDay_end=document.getElementById("planDay_end").value;
	     hashvo.setValue("personPage",'${weekWorkplanForm.personPage}');
	     hashvo.setValue("isChuZhang",'${weekWorkplanForm.isChuZhang}');
	     hashvo.setValue("state",'${weekWorkplanForm.state}');
	     hashvo.setValue("p0100",'${weekWorkplanForm.p0100}');
	     hashvo.setValue("summarizeYear",'${weekWorkplanForm.summarizeYear}');
	     hashvo.setValue("summarizeFields",'${weekWorkplanForm.summarizeFields}');
	     hashvo.setValue("planFields",'${weekWorkplanForm.planFields}');
	     hashvo.setValue("summarizeTime",st);
	     hashvo.setValue("planYear_start",planYear_start);
	     hashvo.setValue("planMonth_start",planMonth_start);
	     hashvo.setValue("planDay_start",planDay_start);
	     hashvo.setValue("planYear_end",planYear_end);
	     hashvo.setValue("planMonth_end",planMonth_end);
	     hashvo.setValue("planDay_end",planDay_end);
	     var request=new Request({method:'post',asynchronous:false,onSuccess:collectsuccess,functionId:'302001020649'},hashvo);
	}else{
		alert(message);
		return;
	}
}

function gatherMonthWork(log_type){
	 var planYear_start=document.getElementById("planYear_start").value;
     var planMonth_start=document.getElementById("planMonth_start").value;
     var planDay_start=document.getElementById("planDay_start").value;
     var planYear_end=document.getElementById("planYear_end").value;
     var planMonth_end=document.getElementById("planMonth_end").value;
     var planDay_end=document.getElementById("planDay_end").value;
     //alert(planYear_start+"-"+planMonth_start+"-"+planDay_start+"----"+planYear_end+"-"+planMonth_end+"-"+planDay_end);
     if(planYear_start==''||planMonth_start==''||planDay_start==''||planYear_end==''||planMonth_end==''||planDay_end==''){
     	alert("计划起始时间和结束时间是必填项！");
     	return;
     }
     //如果修改了时间，要判断是否和部门其他人的时间一样
     var  iscontinue="0";
    // alert(global_sYear+"-"+global_sMonth+"-"+global_sDay+"--"+global_eYear+"-"+global_eMonth+"-"+global_eDay);
    // alert(isdeptother);
     if(isdeptother=='1'&&!(global_sYear*1==planYear_start*1&&global_sMonth*1==planMonth_start*1&&global_sDay*1==planDay_start*1&&global_eYear*1==planYear_end*1&&global_eMonth*1&&global_eDay*1==planDay_end*1)){
     	 iscontinue="1";
     	if(confirm("计划起始时间结束时间与部门其他人的计划区间不一致,部门其他人的区间为【"+global_sYear+"-"+global_sMonth+"-"+global_sDay+"--"+global_eYear+"-"+global_eMonth+"-"+global_eDay+"】\r\n点击确定将部门其他人的区间改为与自己的相同\r\n点击取消修改自己的区间与其他人相同")){
     		iscontinue="2";
     	}
     }
     if(iscontinue=="1"){
     	return;
     }
     var hashvo = new ParameterSet();
     hashvo.setValue("log_type",log_type);
     hashvo.setValue("personPage","${weekWorkplanForm.personPage}");
     hashvo.setValue("isChuZhang","${weekWorkplanForm.isChuZhang}");
     hashvo.setValue("state",'1');
     hashvo.setValue("summarizeYear",weekWorkplanForm.summarizeYear.value);
     hashvo.setValue("summarizeTime",weekWorkplanForm.summarizeTime.value);
     hashvo.setValue("planYear_start",planYear_start);
     hashvo.setValue("planMonth_start",planMonth_start);
     hashvo.setValue("planDay_start",planDay_start);
     hashvo.setValue("planYear_end",planYear_end);
     hashvo.setValue("planMonth_end",planMonth_end);
     hashvo.setValue("planDay_end",planDay_end);
     //hashvo.setValue("type",type);
    // hashvo.setValue("record_num",record_num);
     hashvo.setValue("global_sYear",global_sYear);
     hashvo.setValue("global_sMonth",global_sMonth);
     hashvo.setValue("global_sDay",global_sDay);
     hashvo.setValue("global_eYear",global_eYear);
     hashvo.setValue("global_eMonth",global_eMonth);
     hashvo.setValue("global_eDay",global_eDay);
     hashvo.setValue("iscontinue",iscontinue);
     var request=new Request({method:'post',asynchronous:false,onSuccess:validateSuccG,functionId:'302001020615'},hashvo);
	 
}
function validateSuccG(outparameters){
	var message =outparameters.getValue("message");
	var log_type=outparameters.getValue("log_type");
	if(message==''){
		var st="${weekWorkplanForm.summarizeTime}";
	     var hashvo = new ParameterSet();
	     var planYear_start=document.getElementById("planYear_start").value;
	     var planMonth_start=document.getElementById("planMonth_start").value;
	     var planDay_start=document.getElementById("planDay_start").value;
	     var planYear_end=document.getElementById("planYear_end").value;
	     var planMonth_end=document.getElementById("planMonth_end").value;
	     var planDay_end=document.getElementById("planDay_end").value;
	     hashvo.setValue("log_type",log_type);
	     hashvo.setValue("personPage",'${weekWorkplanForm.personPage}');
	     hashvo.setValue("isChuZhang",'${weekWorkplanForm.isChuZhang}');
	     hashvo.setValue("state",'${weekWorkplanForm.state}');
	     hashvo.setValue("p0115",'02');
	     hashvo.setValue("p0100",'${weekWorkplanForm.p0100}');
	     hashvo.setValue("summarizeYear",'${weekWorkplanForm.summarizeYear}');
	     hashvo.setValue("summarizeFields",'${weekWorkplanForm.summarizeFields}');
	     hashvo.setValue("planFields",'${weekWorkplanForm.planFields}');
	     hashvo.setValue("summarizeTime",st);
	     hashvo.setValue("planYear_start",planYear_start);
	     hashvo.setValue("planMonth_start",planMonth_start);
	     hashvo.setValue("planDay_start",planDay_start);
	     hashvo.setValue("planYear_end",planYear_end);
	     hashvo.setValue("planMonth_end",planMonth_end);
	     hashvo.setValue("planDay_end",planDay_end);
	     var request=new Request({method:'post',asynchronous:false,onSuccess:gathersuccess,functionId:'302001020653'},hashvo);
	}else{
		alert(message);
		return
	}
	
}
function collectsuccess(outparamters){
    var message = outparamters.getValue("message");
    var alertmessage = "";
    if(message=='ok'){
        alertmessage="提取成功!";
	    alert(alertmessage);
	    weekWorkplanForm.action = '/performance/nworkplan/week/searchWeekWorkplan.do?b_query=link&init=1';
		weekWorkplanForm.submit();
    }else{
        alertmessage="上一周期没有计划!";
        alert(alertmessage);
        weekWorkplanForm.action = '/performance/nworkplan/week/searchWeekWorkplan.do?b_query=link&init=1';
		weekWorkplanForm.submit();
    }
}
function gathersuccess(outparamters){
    var message = outparamters.getValue("message");
    var summarizeTime=outparamters.getValue("summarizeTime");
    var alertmessage = "";
    if(message=='ok'){
        alertmessage="汇总成功!";
	    alert(alertmessage);
	    weekWorkplanForm.action = '/performance/nworkplan/week/searchWeekWorkplan.do?b_query=link&init=1&summarizeTime='+summarizeTime;
		weekWorkplanForm.submit();
    }else{
        alertmessage="本周期没有数据!";
        alert(alertmessage);
        weekWorkplanForm.action = '/performance/nworkplan/week/searchWeekWorkplan.do?b_query=link&init=1&summarizeTime='+summarizeTime;
		weekWorkplanForm.submit();
       // return false;
    }
}
 global_sYear="${weekWorkplanForm.planYear_start}";
 global_sMonth="${weekWorkplanForm.planMonth_start}";
 global_sDay="${weekWorkplanForm.planDay_start}";
 global_eYear="${weekWorkplanForm.planYear_end}";
 global_eMonth="${weekWorkplanForm.planMonth_end}";
 global_eDay="${weekWorkplanForm.planDay_end}";
 isdeptother="${weekWorkplanForm.isdeptother}";
</script>
<html:form action="/performance/nworkplan/week/searchWeekWorkplan">
<html:hidden name="weekWorkplanForm" property="hyperlinkRecord" style="hyperlinkRecord"/>
<html:hidden name="weekWorkplanForm" property="hyperlinkP0100" style="hyperlinkP0100"/>
<html:hidden property="isdeptother"/>
<div class="bh-one">
<table align="center" width="99%" border="0" cellSpacing="0" cellPadding="0" >
  <tr>
    <td align="left" width="40" nowrap>						
		<div class="m_frameborder">
            <html:text name="weekWorkplanForm" styleId="summarizeYear" style="font-weight:bolder;font-size:12px;" styleClass="m_inputline" property="summarizeYear" size="2" maxlength="4"
									    onkeypress="event.returnValue=IsDigit(this);" onblur="testNum(this,'summarizeYear')" />
	    </div>
	</td>
	<td align="left" width="20">																
		<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><button id="0_up" class="m_arrow" onmouseup="mincrease1('summarizeYear',9999,'1');">5</button></td></tr>
	        <tr><td><button id="0_down" class="m_arrow" onmouseup="msubtract1('summarizeYear',1,'1');">6</button></td></tr>
		</table>							
	</td>
	<td align="left" nowrap>
	<strong> 年</strong>
	    <html:select name="weekWorkplanForm" property="summarizeTime" size="1" onchange="changeList();" style="font-weight:bolder;font-size:12px;">
						  <html:optionsCollection property="summarizeTimeList" value="dataValue" label="dataName" />
					  </html:select>
	</td>
	<td align="left" width="600" nowrap>	
	<strong> 总结
	<logic:empty name="weekWorkplanForm" property="p0115">
	    &nbsp;&nbsp;&nbsp;未填
	</logic:empty>
	<logic:notEmpty name="weekWorkplanForm" property="p0115">
	    <hrms:codetoname codeid="23" name="weekWorkplanForm" 
										codevalue="p0115" codeitem="codeitem" scope="session" />
	    &nbsp;&nbsp;&nbsp;<bean:write name="codeitem" property="codename" />
	</logic:notEmpty>
    </strong>
    </td>
    <td align="right">
    <%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
	    <input class="mybutton" onclick="appiary('${weekWorkplanForm.planDataSize}','${weekWorkplanForm.summarizeDataSize}','${weekWorkplanForm.sp_relation}','${weekWorkplanForm.personPage}','${weekWorkplanForm.p0100}','1');"  type="button" value="报批" />&nbsp;
    <% }%>
    <input class="mybutton" type="button" value="导出" onclick="exportWeekWorkPlan('${weekWorkplanForm.p0100}','${weekWorkplanForm.summarizeTime}','${weekWorkplanForm.summarizeFields}','${weekWorkplanForm.planFields}')"/>&nbsp;
     <input onclick="searchMonthWorkInfo('${weekWorkplanForm.personPage}','1','${weekWorkplanForm.isChuZhang}','${weekWorkplanForm.belong_type}')" class="mybutton" type="button" value="查询"/>
    <%if(opt.equals("2")&&isRead.equals("2")){ %>
    &nbsp;
    <input onclick="reject('${weekWorkplanForm.p0100}')" class="mybutton" type="button" value="驳回"/>
    <%} %>
    <%if(opt.equals("2")){ %>
    &nbsp;
    <input onclick="goback('<%=returnurl %>')" class="mybutton" type="button" value="返回"/>
    <%} %>
    </td>
  </tr>
</table>

<div id="tbl-container" style="left: 5px; width: 99%; height: 194px; position: absolute;">
<table width="100%" align="center" class="ListTable"  border="0" cellSpacing="0" cellPadding="0">
 
      <thead>
		<tr>
			<td align="center" width="8%" class="TableRow" nowrap>
				序号
			</td>
			<logic:iterate id="fields" name="weekWorkplanForm" property="zongjieFieldsList" indexId="index">
			   <td align="center" 
               <%
	   	      LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("fields");
			  String aitemtype =   (String)abean.get("itemtype");
			  if(aitemtype.equals("M")){%>  
			       width="30%"
			   <%}else{ %>
			       width="10%"
			   <%} %>
               class="TableRow">
				<bean:write name="fields" property="itemdesc"/>
			   </td>
			</logic:iterate>
			<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
			<td align="center" width="15%" class="TableRow" nowrap>
				编辑
			</td>
			<td align="center" width="8%" class="TableRow" nowrap>
				顺序
			</td>
			<%} %>
		</tr>
	</thead>
	 <logic:iterate id="element" name="weekWorkplanForm" property="summarizeDataList" indexId="index">
	    <tr onClick="javascript:tr_changeColor(this,'#FFF8D2')"
	    <%
	    LazyDynaBean aabean=(LazyDynaBean)pageContext.getAttribute("element");  
	    String therecord_num = ","+(String)aabean.get("record_num")+",";
	    boolean ishypelink = hyperlinkRecord.indexOf(therecord_num)!=-1;
	    if(hyperlinkP0100.equalsIgnoreCase(p0100)&&!hyperlinkRecord.equals("")&&ishypelink){  %>
	       style="background:#FFF8D2"
	    <%}%>
	   >
		   <td align="center" width="8%" class="RecordRow" nowrap>
			 <bean:write name="element" property="count"/>
		   </td>
		    
		    <logic:iterate id="fields" name="weekWorkplanForm" property="zongjieFieldsList" indexId="index">
		       <bean:define id="itemid" name="fields" property="itemid"></bean:define>
					   <td 
					   <%
			   	      LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("fields");
					  String aitemtype =   (String)abean.get("itemtype");
					  String aitemid = (String)abean.get("itemid");
					  String aitemid_1 = (String)aabean.get(aitemid+"_1");
					  String avalue = (String)aabean.get(aitemid);
					  if(aitemtype.equals("M")){%>  
					       width="30%"
					   <%}else{ %>
					       width="10%"
					   <%} %>
                        onmouseover="outContent('<%=aitemid_1 %>')" onmouseout="UnTip()" align="center" class="RecordRow">
					    <%if(aitemtype.equals("M")&&avalue.length()>70){
					    	avalue = avalue.substring(0,70)+"...";
					    }
					    out.print(avalue);
					    %>
					   </td>
			</logic:iterate>
			<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
			   <td align="center" width="15%" class="RecordRow" nowrap>
			   &nbsp;
				 <img src="/images/edit.gif" style="cursor:hand;" title="编辑" onclick='addworkplan("2","2","<bean:write name='element' property='record_num'/>","${weekWorkplanForm.personPage}","${weekWorkplanForm.isChuZhang}")'/>
				&nbsp;
				 <img src="/images/del.gif" style="cursor:hand;" title="删除" onclick='deleteworkplan("<bean:write name='element' property='p0100'/>","<bean:write name='element' property='record_num'/>","1")' />
			    &nbsp;
			   </td>
			 <%} %>
			 <%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
			 <td align="center" width="8%" class="RecordRow" nowrap>
				<logic:notEqual name="element" property="count" value="1">
					&nbsp;<a href="javaScript:moveRecord('<bean:write name="element" property="p0100" filter="true"/>','<bean:write name="element" property="record_num" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','up','<bean:write name="element" property="log_type" filter="true"/>','1')">
					<img src="/images/up01.gif" width="12" height="17" border=0></a> 
				</logic:notEqual>
				<logic:equal name="element" property="count" value="1">																		
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</logic:equal>
				
				<logic:equal name="element" property="count" value="${weekWorkplanForm.summarizeDataSize}">																		
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</logic:equal>
				<logic:notEqual name="element" property="count" value="${weekWorkplanForm.summarizeDataSize}">
                     <a href="javaScript:moveRecord('<bean:write name="element" property="p0100" filter="true"/>','<bean:write name="element" property="record_num" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','down','<bean:write name="element" property="log_type" filter="true"/>','1')">
				  <img src="/images/down01.gif" width="12" height="17" border=0></a> &nbsp;
				</logic:notEqual>																			                      
		   </td>
		   <%} %>
		</tr>
	 </logic:iterate>
</table>
</div>
<div class="bh-clear"></div>
<div style="margin-top:194px;">
<table align="center" width="99%" border="0" cellSpacing="0" cellPadding="0">

<tr>
<td align="right">
<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
<input onclick="addworkplan('1','2','','${weekWorkplanForm.personPage}','${weekWorkplanForm.isChuZhang}')" class="mybutton" type="button" value="新增"/>&nbsp;
<%} %>
<%if(opt.equals("1")){ %>
<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
	<input onclick="collect()" class="mybutton" type="button" value="提取"/>&nbsp;
<%}%>
<%if((userView.hasTheFunction("0AB010102")&&personPage.equals("0")) || (userView.hasTheFunction("0AB010202")&&personPage.equals("1"))  ){ %>
<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
	<input onclick="gatherMonthWork('2')" class="mybutton" type="button" value="汇总总结"/>
	<%} %>
<%} %>	
<%} %>
</td>
</tr>
</table>
</div>
</div>



<div class="bh-clear"></div>	


<div class="bh-one">

<table align="center" width="99%" border="0" cellSpacing="0" cellPadding="0">
  <tr>
    <td align="left"  nowrap>	
	  &nbsp;
    </td>
  </tr>
</table>
<table align="center" width="99%" border="0" cellSpacing="0" cellPadding="0">
  <tr>
 <td align="left" width="100%" colspan="100">
 
 <table>
 <tr>
 <td align="right" width="20px" nowrap>
 <%if(p0115.equals("02")||p0115.equals("03")){ %>
 <html:text property="planYear_start" size="4"  maxlength="4" style="font-weight:bolder;font-size:12px;"/>
 <%}else{ %>
  <html:text property="planYear_start" size="4"  maxlength="4" style="font-weight:bolder;font-size:12px;"/>
 <%} %>
 </td>
 <td align="left" width="20px" nowrap>																
		<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><button id="0_up" class="m_arrow" onmouseup="mincrease('planYear_start',9999,'1');">5</button></td></tr>
	        <tr><td><button id="0_down" class="m_arrow" onmouseup="msubtract('planYear_start',1,'1');">6</button></td></tr>
		</table>						
  </td>
 <td>
 <strong>年</strong>&nbsp;<html:text property="planMonth_start" size="2" maxlength="2" style="font-weight:bolder;font-size:12px;"/>
 </td>
 <td align="left" width="20px" nowrap>																
		<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><button id="1_up" class="m_arrow" onmouseup="mincrease('planMonth_start',12,'1');">5</button></td></tr>
	        <tr><td><button id="1_down" class="m_arrow" onmouseup="msubtract('planMonth_start',1,'1');">6</button></td></tr>
		</table>						
  </td>
 <td>
 <strong>月</strong>&nbsp;<html:text property="planDay_start" size="2" maxlength="2" style="font-weight:bolder;font-size:12px;"/>
 </td>
  <td align="left" width="20px" nowrap>																
		<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><button id="2_up" class="m_arrow" onmouseup="mincrease('planDay_start',31,'1');">5</button></td></tr>
	        <tr><td><button id="2_down" class="m_arrow" onmouseup="msubtract('planDay_start',1,'1');">6</button></td></tr>
		</table>						
  </td>
  <td>
 <strong>日</strong>&nbsp;
 --&nbsp;&nbsp;<html:text property="planYear_end" size="4" maxlength="4" style="font-weight:bolder;font-size:12px;"/>
 </td>
 <td align="left" width="20px" nowrap>																
		<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><button id="5_up" class="m_arrow" onmouseup="mincrease('planYear_end',9999,'1');">5</button></td></tr>
	        <tr><td><button id="5_down" class="m_arrow" onmouseup="msubtract('planYear_end',1,'1');">6</button></td></tr>
		</table>						
  </td>
 <td><strong>年</strong>&nbsp;<html:text property="planMonth_end" size="2" maxlength="2" style="font-weight:bolder;font-size:12px;"/></td>
  <td align="left" width="20px" nowrap>																
		<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><button id="3_up" class="m_arrow" onmouseup="mincrease('planMonth_end',12,'1');">5</button></td></tr>
	        <tr><td><button id="3_down" class="m_arrow" onmouseup="msubtract('planMonth_end',1,'1');">6</button></td></tr>
		</table>						
  </td>
 <td>
 
 <strong>月</strong>&nbsp;<html:text property="planDay_end" size="2" maxlength="2" style="font-weight:bolder;font-size:12px;"/></td>
  <td align="left" width="20px" nowrap>																
		<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><button id="4_up" class="m_arrow" onmouseup="mincrease('planDay_end',31,'1');">5</button></td></tr>
	        <tr><td><button id="4_down" class="m_arrow" onmouseup="msubtract('planDay_end',1,'1');">6</button></td></tr>
		</table>						
  </td>
<td> 
 <strong>日&nbsp;&nbsp;计划</strong>&nbsp;
	</td>
	</tr>
	</table>
	</strong>
	</td>
  </tr>
</table>
<div id="tbl-container" style="left: 5px; width: 99%; height: 194px; position: absolute;">
<table width="100%" align="center" class="ListTable"  border="0" cellSpacing="0" cellPadding="0">

      <thead>
		<tr>
			<td align="center" width="8%" class="TableRow" nowrap>
				序号
			</td>
			<logic:iterate id="fields" name="weekWorkplanForm" property="jihuaFieldsList" indexId="index">
			   <td  align="center" 
                <%
	   	      LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("fields");
			  String aitemtype =   (String)abean.get("itemtype");
			  if(aitemtype.equals("M")){%>  
			       width="30%"
			   <%}else{ %>
			       width="10%"
			   <%} %>
             class="TableRow">
				<bean:write name="fields" property="itemdesc"/>
			   </td>
			</logic:iterate>
			<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
			<td align="center" width="15%" class="TableRow" nowrap>
				编辑
			</td>
			
			<td align="center" width="8%" class="TableRow" nowrap>
				顺序
			</td>
			<%} %>
		</tr>
	</thead>
	<logic:iterate id="element" name="weekWorkplanForm" property="planDataList" indexId="index">
	   <tr onClick="javascript:tr_changeColor(this,'#FFF8D2')"
	    <%
	    LazyDynaBean aabean=(LazyDynaBean)pageContext.getAttribute("element");  
	    String therecord_num = ","+(String)aabean.get("record_num")+",";
	    boolean ishypelink = hyperlinkRecord.indexOf(therecord_num)!=-1;
	    if(hyperlinkP0100.equalsIgnoreCase(p0100)&&!hyperlinkRecord.equals("")&&ishypelink){  %>
	       style="background:#FFF8D2"
	    <%}%>
	   >
		   <td align="center" width="8%" class="RecordRow" nowrap>
			 <bean:write name="element" property="count"/>
		   </td>
		    
		    <logic:iterate id="fields" name="weekWorkplanForm" property="jihuaFieldsList" indexId="index">
					   <td 
					   <%
			   	      LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("fields");
					  String aitemtype =   (String)abean.get("itemtype");
					  String aitemid = (String)abean.get("itemid");
					  String aitemid_1 = (String)aabean.get(aitemid+"_1");
					  String avalue = (String)aabean.get(aitemid);
					  if(aitemtype.equals("M")){%>  
					       width="30%"
					   <%}else{ %>
					       width="10%"
					   <%} %>
                        onmouseover="outContent('<%=aitemid_1 %>')" onmouseout="UnTip()" align="center" class="RecordRow">
					    <%if(aitemtype.equals("M")&&avalue.length()>70){
					    	avalue = avalue.substring(0,70)+"...";
					    }
					    out.print(avalue);
					    %>
					   </td>
			</logic:iterate>
			<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
				   <td align="center" width="15%" class="RecordRow" nowrap>
				   &nbsp;
					 <img src="/images/edit.gif" style="cursor:hand;" title="编辑" onclick='addworkplan("2","1","<bean:write name='element' property='record_num'/>","${weekWorkplanForm.personPage}","${weekWorkplanForm.isChuZhang}")'/>
					 &nbsp;
					 <img src="/images/del.gif" style="cursor:hand;" title="删除" onclick='deleteworkplan("<bean:write name='element' property='p0100'/>","<bean:write name='element' property='record_num'/>","1")'/>
				   &nbsp;
				   </td>
		   
			 <td align="center" width="8%" class="RecordRow" nowrap>
				<logic:notEqual name="element" property="count" value="1">
					&nbsp;<a href="javaScript:moveRecord('<bean:write name="element" property="p0100" filter="true"/>','<bean:write name="element" property="record_num" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','up','<bean:write name="element" property="log_type" filter="true"/>','1')">
					<img src="/images/up01.gif" width="12" height="17" border=0></a> 
				</logic:notEqual>
				<logic:equal name="element" property="count" value="1">																		
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</logic:equal>
				
				<logic:equal name="element" property="count" value="${weekWorkplanForm.planDataSize}">																		
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</logic:equal>
				<logic:notEqual name="element" property="count" value="${weekWorkplanForm.planDataSize}">
                      <a href="javaScript:moveRecord('<bean:write name="element" property="p0100" filter="true"/>','<bean:write name="element" property="record_num" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','down','<bean:write name="element" property="log_type" filter="true"/>','1')">
				  <img src="/images/down01.gif" width="12" height="17" border=0></a> &nbsp;
				</logic:notEqual>							
		   </td>
		    <%} %>		   
		</tr>
	 </logic:iterate>
</table>
</div>
<div style="margin-top:204px;">
<table align="center" width="99%" border="0" cellSpacing="0" cellPadding="0">
<tr>
<td align="right">
<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
<input onclick="addworkplan('1','1','','${weekWorkplanForm.personPage}','${weekWorkplanForm.isChuZhang}')"  class="mybutton" type="button" value="新增"/>&nbsp;
<%} %>
<%if(opt.equals("1")){ %>
<%if((userView.hasTheFunction("0AB010101")&&personPage.equals("0")) || (userView.hasTheFunction("0AB010201")&&personPage.equals("1"))  ) {%>
<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
	<input onclick="gatherMonthWork('1')" class="mybutton" type="button" value="汇总计划"/>
	<%} %>
<%} %>	
<%} %>
</td>
</tr>
</table>
</div>
</div>
</html:form>
<html:form action="/performance/nworkplan/queryMonthWorkPlan">
 <html:hidden name="queryMonthWorkPlanForm" property="backurl" style="backurl"/>
 <html:hidden name="queryMonthWorkPlanForm" property="personPage" style="personPage"/>
 <html:hidden name="queryMonthWorkPlanForm" property="isChuZhang" style="isChuZhang"/>
 <html:hidden name="queryMonthWorkPlanForm" property="state" style="state"/>
 <html:hidden name="queryMonthWorkPlanForm" property="belong_type" style="belong_type"/>
</html:form>