<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hjsj.hrms.actionform.performance.nworkplan.MonthWorkplanForm,
               org.apache.commons.beanutils.LazyDynaBean,
               java.util.ArrayList,
               com.hrms.struts.valueobject.UserView,
			   com.hrms.struts.constant.WebConstant
               "%>
<%
MonthWorkplanForm myForm=(MonthWorkplanForm)session.getAttribute("monthWorkplanForm");
String p0100 = myForm.getP0100();
String p0115 = myForm.getP0115();
String isChuZhang = myForm.getIsChuZhang();
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
<script language="JavaScript" src="/performance/nworkplan/nworkplan.js"></script>
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
		if(flag=='currentyear')
		   obj.value='${monthWorkplanForm.currentYear}';
		else if(flag=='currentmonth')
		   obj.value='${monthWorkplanForm.currentMonth}';
		obj.focus();
	}
	else
	{
	   if(flag=='currentyear'){
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
function changetime(obj,flag){
    if(obj.value!='' && !checkIsNum(obj.value))
	{ 
		alert('请输入正整数！');
		if(flag=='currentyear')
		   obj.value='${monthWorkplanForm.currentYear}';
		else if(flag=='currentmonth')
		   obj.value='${monthWorkplanForm.currentMonth}';
		obj.focus();
		return false;
	}
	else
	{
	   if(flag=='currentyear'){
			if(parseInt(obj.value)>9999){
				obj.value='9999';
				return false;
			}
			else if(parseInt(obj.value)<1){
				obj.value='1';
				return false;
			}
	   }else{
		    if(parseInt(obj.value)>9999){
				obj.value='12';
				return false;
		    }
			else if(parseInt(obj.value)<1){
				obj.value='1';
				return false;
			}
	   }
	   
	}
	document.forms[0].action = '/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=1';
    document.forms[0].submit();
}
function addworkplan(type,log_type,record_num){
    var theurl = "/performance/nworkplan/searchMonthWorkplan.do?b_addorupdate=link`log_type="+log_type+"`type="+type+"`record_num="+record_num+"`addflag=1";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    var return_vo= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:510px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    //if(return_vo){
        //var obj = new Object();
        //obj.saveflag = return_vo.saveflag;
       // obj.optflag = return_vo.optflag;
        //if((obj.optflag=='cancel' && obj.saveflag=='2')||obj.optflag=='save'){
	        monthWorkplanForm.action = '/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=1';
	        monthWorkplanForm.submit();
        //}
    //}
}
function collect(){
     var hashvo = new ParameterSet();
     hashvo.setValue("personPage",'${monthWorkplanForm.personPage}');
     hashvo.setValue("isChuZhang",'${monthWorkplanForm.isChuZhang}');
     hashvo.setValue("state",'${monthWorkplanForm.state}');
     hashvo.setValue("p0100",'${monthWorkplanForm.p0100}');
     hashvo.setValue("currentYear",'${monthWorkplanForm.currentYear}');
     hashvo.setValue("currentMonth",'${monthWorkplanForm.currentMonth}');
     hashvo.setValue("summarizeFields",'${monthWorkplanForm.summarizeFields}');
     hashvo.setValue("planFields",'${monthWorkplanForm.planFields}');
     var request=new Request({method:'post',asynchronous:false,onSuccess:collectsuccess,functionId:'302001020642'},hashvo);
}
function gatherMonthWork(log_type){
     var hashvo = new ParameterSet();
     hashvo.setValue("log_type",log_type);
     hashvo.setValue("personPage",'${monthWorkplanForm.personPage}');
     hashvo.setValue("isChuZhang",'${monthWorkplanForm.isChuZhang}');
     hashvo.setValue("state",'${monthWorkplanForm.state}');
     hashvo.setValue("p0115",'02');
     hashvo.setValue("p0100",'${monthWorkplanForm.p0100}');
     hashvo.setValue("currentYear",'${monthWorkplanForm.currentYear}');
     hashvo.setValue("currentMonth",'${monthWorkplanForm.currentMonth}');
     hashvo.setValue("summarizeFields",'${monthWorkplanForm.summarizeFields}');
     hashvo.setValue("planFields",'${monthWorkplanForm.planFields}');
     var request=new Request({method:'post',asynchronous:false,onSuccess:gathersuccess,functionId:'302001020647'},hashvo);
}
function collectsuccess(outparamters){
    var message = outparamters.getValue("message");
    var alertmessage = "";
    if(message=='ok'){
        alertmessage="提取成功!";
	    alert(alertmessage);
	    monthWorkplanForm.action = '/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=1';
		monthWorkplanForm.submit();
    }else{
        alertmessage="上一周期没有计划!";
        alert(alertmessage);
        return false;
    }
}
function gathersuccess(outparamters){
    var message = outparamters.getValue("message");
    var alertmessage = "";
    if(message=='ok'){
        alertmessage="汇总成功!";
	    alert(alertmessage);
	    monthWorkplanForm.action = '/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=1';
		monthWorkplanForm.submit();
    }else{
        alertmessage="本周期没有数据!";
        alert(alertmessage);
        return false;
    }
}

</script>
<html:form action="/performance/nworkplan/searchMonthWorkplan">
<html:hidden name="monthWorkplanForm" property="hyperlinkRecord" style="hyperlinkRecord"/>
<html:hidden name="monthWorkplanForm" property="hyperlinkP0100" style="hyperlinkP0100"/>
<div class="bh-one">
<table align="center" width="99%" border="0" cellSpacing="0" cellPadding="0" >
  <tr>
    <td align="left" width="40" nowrap>						
		<div class="m_frameborder">
            <html:text name="monthWorkplanForm" styleId="currentYear" styleClass="m_inputline" property="currentYear" size="2"
									    onchange="changetime(this,'currentyear')" onkeypress="event.returnValue=IsDigit(this);" onblur="changetime(this,'currentyear')" />
	    </div>
	</td>
	<td align="left" width="20">																
		<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><button id="0_up" class="m_arrow" onmouseup="mincrease('currentYear',9999,'2');">5</button></td></tr>
	        <tr><td><button id="0_down" class="m_arrow" onmouseup="msubtract('currentYear',1,'2');">6</button></td></tr>
		</table>							
	</td>
	<td align="left" width="50" nowrap>	
	    <strong >年 第</strong>
	</td>
	<td align="left" width="40" nowrap>						
		<div class="m_frameborder">
            <html:text name="monthWorkplanForm" styleId="currentMonth" styleClass="m_inputline" property="currentMonth" size="2"
									   onchange="changetime(this,'currentmonth')" onkeypress="event.returnValue=IsDigit(this);" onblur="changetime(this,'currentmonth')" />
	    </div>
	</td>
	<td align="left" width="20">																
		<table border="0" cellspacing="2" cellpadding="0">
			<tr><td><button id="0_up" class="m_arrow" onmouseup="mincrease('currentMonth',12,'2');">5</button></td></tr>
	        <tr><td><button id="0_down" class="m_arrow" onmouseup="msubtract('currentMonth',1,'2');">6</button></td></tr>
		</table>							
	</td>
	<td align="left" width="600" nowrap>	
	<strong>月 总结(${monthWorkplanForm.currentTime})
	<logic:empty name="monthWorkplanForm" property="p0115">
	    &nbsp;&nbsp;&nbsp;未填
	</logic:empty>
	<logic:notEmpty name="monthWorkplanForm" property="p0115">
	    <hrms:codetoname codeid="23" name="monthWorkplanForm" 
										codevalue="p0115" codeitem="codeitem" scope="session" />
	    &nbsp;&nbsp;&nbsp;<bean:write name="codeitem" property="codename" />
	</logic:notEmpty>
    </strong>
    </td>
    <td align="right">
    <%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
	    <input class="mybutton" onclick="appiary('${monthWorkplanForm.planDataSize}','${monthWorkplanForm.summarizeDataSize}','${monthWorkplanForm.sp_relation}','${monthWorkplanForm.personPage}','${monthWorkplanForm.p0100}','2');" type="button" value="报批" />&nbsp;
    <% }%>
    <input onclick="exportMonthWorkPlan('${monthWorkplanForm.p0100}','${monthWorkplanForm.currentYear}','${monthWorkplanForm.currentMonth}','${monthWorkplanForm.nextYear}','${monthWorkplanForm.nextMonth}')" class="mybutton" type="button" value="导出"/>&nbsp;
    <input onclick="searchMonthWorkInfo('${monthWorkplanForm.personPage}','2','${monthWorkplanForm.isChuZhang}','${monthWorkplanForm.belong_type}')" class="mybutton" type="button" value="查询"/>
    <%if(opt.equals("2")&&isRead.equals("2")){ %>
    &nbsp;
    <input onclick="reject('${monthWorkplanForm.p0100}')" class="mybutton" type="button" value="驳回"/>
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
			<logic:iterate id="fields" name="monthWorkplanForm" property="zongjieFieldsList" indexId="index">
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
			<%} %>
			<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
			<td align="center" width="8%" class="TableRow" nowrap>
				顺序
			</td>
			<%} %>
		</tr>
	</thead>
	 <logic:iterate id="element" name="monthWorkplanForm" property="summarizeDataList" indexId="index">
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
		    
		    <logic:iterate id="fields" name="monthWorkplanForm" property="zongjieFieldsList" indexId="index">
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
			 <a onclick='addworkplan("2","2","<bean:write name='element' property='record_num'/>")'><img
					src="/images/edit.gif" border=0 style="cursor:pointer"> 
			 </a>
			 <a onclick='deleteworkplan("<bean:write name='element' property='p0100'/>","<bean:write name='element' property='record_num'/>","2")'><img
					src="/images/del.gif" border=0 style="cursor:pointer"> 
			 </a>
		   </td>
		   <%} %>
		   
		   
		   
			 <%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
			 <td align="center" width="8%" class="RecordRow" nowrap>
				<logic:notEqual name="element" property="count" value="1">
					&nbsp;<a
					  href="javaScript:moveRecord('<bean:write name="element" property="p0100" filter="true"/>','<bean:write name="element" property="record_num" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','up','<bean:write name="element" property="log_type" filter="true"/>','2')">
					<img src="../../images/up01.gif" width="12" height="17" border=0></a> 
				</logic:notEqual>
				<logic:equal name="element" property="count" value="1">																		
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</logic:equal>
				
				<logic:equal name="element" property="count" value="${monthWorkplanForm.summarizeDataSize}">																		
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</logic:equal>
				<logic:notEqual name="element" property="count" value="${monthWorkplanForm.summarizeDataSize}">
                     <a 
					  href="javaScript:moveRecord('<bean:write name="element" property="p0100" filter="true"/>','<bean:write name="element" property="record_num" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','down','<bean:write name="element" property="log_type" filter="true"/>','2')">
				  <img src="../../images/down01.gif" width="12" height="17" border=0></a> &nbsp;
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
<input onclick="addworkplan('1','2','')" class="mybutton" type="button" value="新增"/>&nbsp;
<%} %>
<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
<%if(!(personPage!=null && personPage.equals("1"))){ %>
	<input onclick="collect()" class="mybutton" type="button" value="提取"/>&nbsp;
<%} %>
<%} %>
<%if( opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03")) ){ %>
<%if((userView.hasTheFunction("0AB020102")&&personPage.equals("0")) || (userView.hasTheFunction("0AB020202")&&personPage.equals("1"))  ){ %>
	<input onclick="gatherMonthWork('2')" class="mybutton" type="button" value="汇总总结"/>
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
	    <strong >第${monthWorkplanForm.nextMonth}月 计划(${monthWorkplanForm.nextTime})</strong>
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
			<logic:iterate id="fields" name="monthWorkplanForm" property="jihuaFieldsList" indexId="index">
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
			<%} %>
			<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>
			<td align="center" width="8%" class="TableRow" nowrap>
				顺序
			</td>
			<%} %>
		</tr>
	</thead>
	<logic:iterate id="element" name="monthWorkplanForm" property="planDataList" indexId="index">
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
		    
		    <logic:iterate id="fields" name="monthWorkplanForm" property="jihuaFieldsList" indexId="index">
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
			 <a onclick='addworkplan("2","1","<bean:write name='element' property='record_num'/>")'><img
					src="/images/edit.gif" border=0 style="cursor:pointer"> 
			 </a>
			 <a onclick='deleteworkplan("<bean:write name='element' property='p0100'/>","<bean:write name='element' property='record_num'/>","2")'><img
					src="/images/del.gif" border=0 style="cursor:pointer"> 
			 </a>
		   </td>
		   <%} %>
				   
				   
			<%if(opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03"))){ %>	   
			 <td align="center" width="8%" class="RecordRow" nowrap>
				<logic:notEqual name="element" property="count" value="1">
					&nbsp;<a href="javaScript:moveRecord('<bean:write name="element" property="p0100" filter="true"/>','<bean:write name="element" property="record_num" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','up','<bean:write name="element" property="log_type" filter="true"/>','2')">
					<img src="../../images/up01.gif" width="12" height="17" border=0></a> 
				</logic:notEqual>
				<logic:equal name="element" property="count" value="1">																		
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</logic:equal>
				
				<logic:equal name="element" property="count" value="${monthWorkplanForm.planDataSize}">																		
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</logic:equal>
				<logic:notEqual name="element" property="count" value="${monthWorkplanForm.planDataSize}">
                     <a 
                      href="javaScript:moveRecord('<bean:write name="element" property="p0100" filter="true"/>','<bean:write name="element" property="record_num" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','down','<bean:write name="element" property="log_type" filter="true"/>','2')">
				  <img src="../../images/down01.gif" width="12" height="17" border=0></a> &nbsp;
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
<input onclick="addworkplan('1','1','')"  class="mybutton" type="button" value="新增"
/>&nbsp;
<%} %>
<%if( opt.equals("1")&&!(p0115.equals("02")||p0115.equals("03")) ){ %>
<%if((userView.hasTheFunction("0AB020101")&&personPage.equals("0")) || (userView.hasTheFunction("0AB020201")&&personPage.equals("1"))  ) {%>
	<input onclick="gatherMonthWork('1')" class="mybutton" type="button" value="汇总计划"/>
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