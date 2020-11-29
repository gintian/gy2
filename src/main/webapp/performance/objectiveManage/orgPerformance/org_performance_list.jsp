<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@page import="com.hjsj.hrms.actionform.performance.objectiveManage.orgPerformance.OrgPerformanceForm,com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.SystemConfig,
				com.hrms.struts.constant.WebConstant" %>
<%
   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   String buttonClass="mybutton";
   boolean flag=true;
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
      buttonClass="mybuttonBig";
      flag=false;
   }
   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
   String evaluate="org.performance.evaluate";
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
   {
     evaluate="org.performance.khtime";
   }
   
   OrgPerformanceForm orgPerformanceForm = (OrgPerformanceForm)session.getAttribute("orgPerformanceForm");
	String returnflag=orgPerformanceForm.getReturnflag();
	if(returnflag==null)
		returnflag="menu";
	String url_extends="";
	if(returnflag.equals("10"))
		url_extends="&returnflag=10";
	else
		url_extends="&returnflag=menu";
		
	boolean hasEvaluateRecord=false; //是否有评估的记录
 
	String url_p=SystemConfig.getServerURL(request);
	String status=orgPerformanceForm.getStatus();
   
 %>
 <link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
<!--
var pa="-1";
var a0100="<%=userView.getA0100()%>";
function query()
{
   orgPerformanceForm.action="/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&opt=2";
   orgPerformanceForm.submit();
}
function orgPerformance(opt,planid,a0100,body_id)
{
 if(pa =="-1"||pa!=planid+a0100)
   {
      pa=planid+a0100;
      objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0<%=url_extends%>&entranceType=0&showHistoryTask=0&model=1&opt="+opt+"&planid="+planid+"&object_id="+a0100+"&body_id="+body_id;
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
function searchReject(object_id,plan_id)
{
    var thecodeurl="/performance/objectiveManage/myObjective/my_objective_list.do?b_reject=link`opt=1`type=2`plan_id="+plan_id+"`object_id="+object_id; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
					        "dialogWidth:600px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");			
	
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
function exportObjectiveCard()
{
	/****
   var records=document.getElementsByName("records");
   var num=0;
   var selectRecords="";
   if(records)
   {
      for(var i=0;i<records.length;i++)
      {
         if(records[i].checked)
         {
            num++;
            selectRecords+="/"+records[i].value;
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
    hashVo.setValue("model","3");
	hashVo.setValue("body_id",'1');
	hashVo.setValue("underOpt",'1');
	hashVo.setValue("logo",'5');
	var In_parameters="opt=1";
	var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:export_ok,functionId:'9028000405'},hashVo);	
	***/
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
    hashVo.setValue("model",'1');/// 1:团对 2:我的目标 3:目标制订 4.目标评估 5.目标结果 6:目标执行情况 7:目标卡代制订 8:评分调整
	hashVo.setValue("body_id",body_id);
	hashVo.setValue("underOpt",opt);
	hashVo.setValue("logo",'5');// 1:我的目标；2:员工目标；3:目标评分；4:目标执行情况；5:团队绩效
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

function goback(returnflag)
{
	if(returnflag=="8"){
       		if("hcm"=='<%=hcmflag%>'){
 	      		 window.location='/templates/index/hcm_portal.do?b_query=link';      		
       		}else{
 	       		window.location='/templates/index/portal.do?b_query=link';      		
       		}
	}
	else if(returnflag=="10"){
	   window.location='/general/template/matterList.do?b_query=link';
	}
}


function batchSub()
{
	var records=document.getElementsByName("records");
   var num=0;
   var selectRecords="";
   if(records)
   {
      for(var i=0;i<records.length;i++)
      {
         if(records[i].checked)
         {
            num++;
            selectRecords+="/"+records[i].value;
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
    hashVo.setValue("model","1");
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
	 orgPerformanceForm.action="/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&opt=2";
     orgPerformanceForm.submit();
	
}
function bacthSp()
{
   var records=document.getElementsByName("records");
   var bacthdata=document.getElementsByName("bacthdata");
   var num=0;
   var selectRecords="";
   var spable=0;
   if(records)
   {
      for(var i=0;i<records.length;i++)
      {
         if(records[i].checked)
         {
            num++;
            selectRecords+="/"+bacthdata[i].value;
            var t=bacthdata[i].value.split("`");
            var sp_flag=t[2];
			var curruser=t[3];
			var status=t[5];
			if(sp_flag=='02'&&curruser==a0100&&status!='5')
			{
			    spable++;
			}
         }
      }
   }
   if(num==0)
   {
      alert("请选择记录!");
      return;
   }
   if(spable==0)
   {
      alert("没有可审批数据!");
      return;
   }
    if(!confirm("确认执行批量审批?"))
      return;
    
    var isTargetCardTemp="${orgPerformanceForm.isTargetCardTemp}";
    var isEmail="0";
    if(isTargetCardTemp=="true") {
        if(confirm("是否发送邮件?"))
             isEmail="1";
    }
  
    var hashVo=new ParameterSet();
    hashVo.setValue("ids",selectRecords.substring(1)); 
    hashVo.setValue("isEmail",isEmail);
    if(document.getElementById("hostname")!=null)
    hashVo.setValue("url_p",document.getElementById("hostname").href);
	var request=new Request({method:'post',asynchronous:false,onSuccess:bacth_ok,functionId:'90100170020'},hashVo);			
}
function bacth_ok(outparameters)
{
  var info = outparameters.getValue("info");
  if(info=='0')
  {
    alert("批量审批成功！");
    orgPerformanceForm.action="/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&opt=2";
    orgPerformanceForm.submit();
	
  }
  else if(info=='1')
  {
    alert("没有可审批数据！");
    return;
  }else{
      alert(getDecodeStr(info));
      return;
  }
  
}
function searchUntread(object_id,plan_id)
{
    var thecodeurl="/performance/objectiveManage/myObjective/my_objective_list.do?b_reject=link`opt=100`plan_id="+plan_id+"`object_id="+object_id; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	//var retvo= window.showModalDialog(iframe_url, null,
	//				        "dialogWidth:600px; dialogHeight:465px;resizable:no;center:yes;scroll:yes;status:no");
	var config = {
	    width:600,
        height:465,
        type:'2'
    }
	modalDialog.showModalDialogs(iframe_url,'untreadWin',config)
	
}
//-->
</script>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>
<html:form action="/performance/objectiveManage/orgPerformance/org_performance_list">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<logic:equal value="-1" name="orgPerformanceForm" property="plan_id">
<tr>
<td align="center"><a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-bottom:5px;">

<tr>
<td align="left" style="height:20px"> 
<font class="<%=tt3CssName%>"><bean:message key="org.performance.evaluate"/></font>:
<html:select name="orgPerformanceForm" property="year" size="1" onchange="query();">
			<html:optionsCollection property="yearList" value="dataValue" label="dataName"/>
		    </html:select>
		    <font class="<%=tt3CssName%>"><bean:message key="org.performance.year"/></font>
		    &nbsp;
		    <html:select name="orgPerformanceForm" property="quarter" size="1" onchange="query();">
			<html:optionsCollection property="quarterList" value="dataValue" label="dataName"/>
		    </html:select>
		    <font class="<%=tt3CssName%>"> <bean:message key="org.performance.quarter"/></font>
		    &nbsp;
		    <html:select name="orgPerformanceForm" property="month" size="1" onchange="query();">
			<html:optionsCollection property="monthList" value="dataValue" label="dataName"/>
		    </html:select>
		    <font class="<%=tt3CssName%>"> <bean:message key="org.performance.month"/></font>
		    &nbsp;
		    <font class="<%=tt3CssName%>">计划<bean:message key="org.performance.status"/></font>:
		     <html:select name="orgPerformanceForm" property="status" size="1" onchange="query();">
			<html:optionsCollection property="statusList" value="dataValue" label="dataName"/>
		    </html:select>
		    &nbsp;
		    <font class="<%=tt3CssName%>">目标卡<bean:message key="org.performance.status"/></font>:
		     <html:select name="orgPerformanceForm" property="spStatus" size="1" onchange="query();">
			<html:optionsCollection property="spStatusList" value="dataValue" label="dataName"/>
		    </html:select>
</td>
</tr>
</table>
</td>
</tr>
</logic:equal>
<tr>
<td align="center">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<td width="5%" align="center" class="TableRow" nowrap>
<input type="checkbox" name="allselect" id="select" onclick="selectAllRecord();"/>
</td>
<td width="20%" align="center" class="TableRow" nowrap>
&nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.khname"/></font>&nbsp;
</td>
<td width="15%" align="center" class="TableRow" nowrap>
&nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.unorum"/></font>&nbsp;
</td>
<td width="15%" align="center" class="TableRow" nowrap>
&nbsp;<font class="<%=tt4CssName%>"><bean:message key="<%=evaluate%>"/></font>&nbsp;
</td>
<td width="15%" align="center" class="TableRow" nowrap>
&nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.khgrade"/></font>&nbsp;
</td>
<%if(flag){ %>
<td width="10%" align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="org.performance.status"/></font>
</td>
<%} %>
<%if(status.equals("4")||status.equals("6")||status.equals("7")||status.equals("-2")){ %>
<td width="10%" align="center" class="TableRow" nowrap>
&nbsp;<font class="<%=tt4CssName%>">总分</font>&nbsp;
</td>
<%} %>
<td width="10%" align="center" class="TableRow" nowrap>
&nbsp;<font class="<%=tt4CssName%>"><bean:message key="column.operation"/></font>&nbsp;
</td>
</tr>
</thead>
<% int j=0; %>
 <hrms:extenditerate id="element" name="orgPerformanceForm" property="planListForm.list" indexes="indexes"  pagination="planListForm.pagination" pageCount="15" scope="session">
 <%if(j%2==0){ %>
	     <tr class="trShallow" onClick="javascript:tr_onclick(this,'#E4F2FC')">
	     <%} else { %>
	     <tr class="trDeep" onClick="javascript:tr_onclick(this,'#E4F2FC')">
	     <%}%>
	     <input type="hidden" name="optflag" value="<bean:write name="element" property="opt"/> ">
	     <input type="hidden" name="levelflag" value="<bean:write name="element" property="level"/> ">
	       <td align="center" class="RecordRow" nowrap>
	    <input type="checkbox" name="records" value="<bean:write name="element" property="records"/>"/>
	    <input type="hidden" name="bacthdata" value="<bean:write name="element" property="bacthdata"/>"/>
	     </td>
	      <td align="left" class="RecordRow" nowrap>
	    &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="name"/></font>
	     </td>
	       <td align="left" class="RecordRow" nowrap>
	     &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="b0110"/></font>
	     </td>
	       <td align="left" class="RecordRow" nowrap>
	     &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="evaluate"/></font>
	     </td>
	       <td align="left" class="RecordRow" nowrap>
	     &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="gradedesc"/></font>
	     </td>
	     <%if(flag){ %>
	      <td align="center" class="RecordRow" nowrap>
	      <logic:equal value="1" name="element" property="isReject">
	       <a href="javascript:searchReject('<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="mdplanid"/>')">
	    <font class="<%=tt3CssName%>"><bean:write name="element" property="sp_flag"/></font>
	    </a>
	      </logic:equal>
	       <logic:equal value="0" name="element" property="isReject">
	      <logic:equal value="07" name="element" property="spf">
	      <a href="javascript:searchReject('<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="mdplanid"/>')">
	    <font class="<%=tt3CssName%>"><bean:write name="element" property="sp_flag"/></font>
	    </a>
	    </logic:equal>
	    <logic:notEqual value="07" name="element" property="spf">
	    
	     <font class="<%=tt3CssName%>">
	      <logic:equal value="03" name="element" property="spf">
	         <logic:equal value="07" name="element" property="trace_flag">
         	    <a href="javascript:searchReject('<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="mdplanid"/>')"> <bean:write name="element" property="tsp"/></a>
         	 </logic:equal>
         	 <logic:notEqual value="07" name="element" property="trace_flag">
         	     <bean:write name="element" property="tsp"/>
         	 </logic:notEqual>
	     </logic:equal>
	       <logic:notEqual value="03" name="element" property="spf">
	     <bean:write name="element" property="sp_flag"/>
	     </logic:notEqual>
	     </font>
	    </logic:notEqual>
	    </logic:equal>
	     </td>
	     <%} %>
	     <%if(status.equals("4")||status.equals("6")||status.equals("7")||status.equals("-2")){ %>
<td align="center" class="RecordRow" nowrap>
 <bean:write name="element" property="score"/>
</td>
<%} %>
	       <td align="center" class="RecordRow" nowrap>
	      <logic:equal value="6" name="element" property="flag">
	      <logic:equal value="1" name="element" property="currsp">
	      	      <a href="javascript:orgPerformance('1','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">
   <logic:equal value="01" property="spf" name="element">
	       	    设定
	       	    </logic:equal>
	       	     <logic:equal value="07" property="spf" name="element">
	       	    设定
	       	    </logic:equal>
	       	    <logic:equal value="02"  property="spf" name="element">
	       	    审核
	       	    </logic:equal>
</font></a>
	      </logic:equal>
	       <logic:notEqual value="1" name="element" property="currsp">
	       <logic:equal value="2" name="element" property="currsp">
	       <a href="javascript:orgPerformance('0','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">评价</font></a>
	        </logic:equal>
	        <logic:notEqual value="2" name="element" property="currsp">
	           <a href="javascript:orgPerformance('0','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">查看</font></a>
	      </logic:notEqual>
	      </logic:notEqual>
	      </logic:equal>
	      <logic:equal value="7" name="element" property="flag">
	      <logic:equal value="1" name="element" property="currsp">
	      	      <a href="javascript:orgPerformance('1','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">
	      	       <logic:equal value="01" property="spf" name="element">
	       	    设定
	       	    </logic:equal>
	       	     <logic:equal value="07" property="spf" name="element">
	       	    设定
	       	    </logic:equal>
	       	    <logic:equal value="02"  property="spf" name="element">
	       	    审核
	       	    </logic:equal>
	      	      </font></a>
	      </logic:equal>
	       <logic:notEqual value="1" name="element" property="currsp">
	       		<logic:equal value="2" name="element" property="currsp">
		           <a href="javascript:orgPerformance('0','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">评价</font></a>
		      	</logic:equal>
		       	<logic:notEqual value="2" name="element" property="currsp">
		           <a href="javascript:orgPerformance('0','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">查看</font></a>
		      	</logic:notEqual>
	      </logic:notEqual>
	      </logic:equal>
	       <logic:equal value="8" name="element" property="flag">
	       			<% hasEvaluateRecord=true; %>
	                <a href="javascript:orgPerformance('2','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">
	                	<logic:equal name="element" property="pbOpt" value="1"><bean:message key="lable.performance.confirm"/></logic:equal>
	                	<logic:notEqual name="element" property="pbOpt" value="1"><bean:message key="lable.performance.assessment"/></logic:notEqual>
	                </font></a>
	       </logic:equal>
	       <logic:equal value="1" name="element" property="currsp">
			<img src="/images/new0.gif" border="0"/>
			</logic:equal>			  
	        
			<logic:equal value="1" name="element" property="bs">
			 <a href="javascript:orgPerformance('1','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">调整</font></a>
			 <img src="/images/new0.gif" border="0"/>
			</logic:equal>
			  <logic:equal value="1" name="element" property="isScoreUntread">
	      <a href="javascript:searchUntread('<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="mdplanid"/>')"/>[退回]</a>
	      </logic:equal>
	     </td>
 <% j++; %>
 </hrms:extenditerate>
</table>
</td>
</tr>
<tr>
<td>

<table  width="100%" align="center" class="RecordRowP">
		<tr>
		   <td valign="bottom" class="tdFontcolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   ${orgPerformanceForm.planListForm.pagination.current}
					<bean:message key="label.page.sum"/>
		   ${orgPerformanceForm.planListForm.pagination.count}
					<bean:message key="label.page.row"/>
		   ${orgPerformanceForm.planListForm.pagination.pages}
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
            <hrms:paginationlink name="orgPerformanceForm" property="planListForm.pagination" nameId="planListForm" propertyId="planListProperty">
		   </hrms:paginationlink>
		   </p>
		   </td>
		</tr> 
</table>
</td>
</tr>

<tr>
<td align="left" style="padding-top:3px" style="height:35px"> 
<hrms:priv func_id="06070108"> 
<input type="button" name="export" class="<%=buttonClass%>" value="<bean:message key="info.appleal.state13"/>" onclick="exportObjectiveCard();"/>
</hrms:priv>
<hrms:priv func_id="06070406,06070112"> 
<% if(hasEvaluateRecord){ %>
<input type="button" name="sub" class="<%=buttonClass%>" value="批量提交" onclick="batchSub();"/>
<% } %>
</hrms:priv>
<hrms:priv func_id="06070115"> 
<input type="button" name="bact" class="<%=buttonClass%>" value="批量审批" onclick="bacthSp();"/>
</hrms:priv>
<logic:notEqual value="-1" name="orgPerformanceForm" property="plan_id">

	<% if(returnflag.equals("8")||returnflag.equals("10")){

	out.println("<input type='button' name='export' class='mybutton' value='返回' onclick='goback(\""+returnflag+"\")' />");

	}
 %>
</logic:notEqual>
</td>
</tr>
</table>
</html:form>
</td>
</tr>
<tr>
<td>
<html:form action="/performance/objectiveManage/objectiveCard">
<logic:notEqual value="-1" name="orgPerformanceForm" property="plan_id">
 <input type="hidden" name="returnURL" value="/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init2&entranceType=4&opt=4&planid=${orgPerformanceForm.plan_id}&year=${orgPerformanceForm.year}&month=${orgPerformanceForm.month}&quarter=${orgPerformanceForm.quarter}&status=${orgPerformanceForm.status}&spStatus=${orgPerformanceForm.spStatus}"/>
 <input type="hidden" name="target" value="_self"/>
</logic:notEqual>
<logic:equal value="-1" name="orgPerformanceForm" property="plan_id">
<input type="hidden" name="returnURL" value="/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init2&entranceType=0&opt=3&year=${orgPerformanceForm.year}&month=${orgPerformanceForm.month}&quarter=${orgPerformanceForm.quarter}&status=${orgPerformanceForm.status}&spStatus=${orgPerformanceForm.spStatus}"/>
 <input type="hidden" name="target" value="il_body"/>
 </logic:equal>
 
</html:form>



</td></tr>
</table>
