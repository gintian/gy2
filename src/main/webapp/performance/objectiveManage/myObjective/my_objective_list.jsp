<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.actionform.performance.objectiveManage.myObjective.MyObjectiveForm" %>
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
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
      buttonClass="mybuttonBig";
   }
   String evaluate="org.performance.evaluate";
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
   {
     evaluate="org.performance.khtime";
   }
    String editDesc="设定";
    if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
    {
      editDesc="制订";
    }
     MyObjectiveForm myObjectiveForm = (MyObjectiveForm)session.getAttribute("myObjectiveForm");
	String returnflag=myObjectiveForm.getReturnflag();
	if(returnflag==null)
		returnflag="menu";
	String url_extends="";
	if(returnflag.equals("10"))
		url_extends="&returnflag=10";
	else
		url_extends="&returnflag=menu";
 %>
 <link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
<!--
var pa="-1";
function query()
{
   myObjectiveForm.action="/performance/objectiveManage/myObjective/my_objective_list.do?b_init=init&opt=2";
   myObjectiveForm.submit();
}
function myObjective(opt,mdplanid,mda0100)
{
  if(pa =="-1"||pa!=mdplanid+mda0100)
  {
     pa=mdplanid+mda0100;
     objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0<%=url_extends%>&entranceType=0&showHistoryTask=0&body_id=5&model=2&opt="+opt+"&planid="+mdplanid+"&object_id="+mda0100;
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
function exportObjectiveCard()
{
/***
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
    hashVo.setValue("model",'2');
	hashVo.setValue("body_id",'5');
	hashVo.setValue("underOpt",'1');
	hashVo.setValue("logo",'1');
	var In_parameters="opt=1";
	var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:export_ok,functionId:'9028000405'},hashVo);	
	***/		
	
   var records=document.getElementsByName("records");
   var num=0;
   var selectRecords="";
   
   var opt="";
   var body_id="";
   var  obj1 = document.getElementsByName("optflag");
   var  obj2 = "";
   if(records)
   {
      for(var i=0;i<records.length;i++)
      {
         if(records[i].checked)
         {
            num++;
            selectRecords+="/"+records[i].value;
            opt+=obj1[i].value+"`";
            body_id+="5"+"`";//本人主体类别5
            
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
    hashVo.setValue("model",'2');/// 1:团对 2:我的目标 3:目标制订 4.目标评估 5.目标结果 6:目标执行情况 7:目标卡代制订 8:评分调整
	hashVo.setValue("body_id",body_id);
	hashVo.setValue("underOpt",opt);
	hashVo.setValue("logo",'1');// 1:我的目标；2:员工目标；3:目标评分；4:目标执行情况；5:团队绩效
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
function searchReject(object_id,plan_id)
{
    var thecodeurl="/performance/objectiveManage/myObjective/my_objective_list.do?b_reject=link`opt=1`type=1`plan_id="+plan_id+"`object_id="+object_id; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	
	// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
	if(/msie/i.test(navigator.userAgent)){
		var retvo= window.showModalDialog(iframe_url, null, "dialogWidth:600px; dialogHeight:465px;resizable:no;center:yes;scroll:yes;status:no");
		return ;
	} else {
		function openWin(){
		    Ext.create("Ext.window.Window",{
		    	id:'searchreject_win',
		    	width:600,
		    	height:465,
		    	title:'退回',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	closeAction:'destroy',
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
		    }).show();
		}
		
		if(typeof window.Ext == 'undefined'){
			insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
				insertFile("/ext/ext6/ext-all.js","js" ,openWin);
			});
			
		} else {
			openWin();
		}
	}
	
}
function searchReject_close(){
	Ext.getCmp('searchreject_win').close();
}
function searchUntread(object_id,plan_id)
{
    var thecodeurl="/performance/objectiveManage/myObjective/my_objective_list.do?b_reject=link`opt=100`plan_id="+plan_id+"`object_id="+object_id; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
					        "dialogWidth:600px; dialogHeight:465px;resizable:no;center:yes;scroll:yes;status:no");			
	
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
function searchTask(){
	 var thecodeurl="/performance/objectiveManage/myObjective/searchmytask.do?b_searchtas=link&opt=init"; 
	  document.objectCardForm.action=thecodeurl;
	  document.objectCardForm.submit();
}

//-->
</script>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>
<html:form action="/performance/objectiveManage/myObjective/my_objective_list">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td align="center">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-bottom:5px;">
<tr>
<td align="left" style="height:20px">
<font class="<%=tt3CssName%>"><bean:message key="org.performance.evaluate"/></font>:
<html:select name="myObjectiveForm" property="year" size="1" onchange="query();">
			<html:optionsCollection property="yearList" value="dataValue" label="dataName"/>
		    </html:select>
		    <font class="<%=tt3CssName%>"><bean:message key="org.performance.year"/></font>
		    &nbsp;
		    <html:select name="myObjectiveForm" property="quarter" size="1" onchange="query();">
			<html:optionsCollection property="quarterList" value="dataValue" label="dataName"/>
		    </html:select>
		     <font class="<%=tt3CssName%>"><bean:message key="org.performance.quarter"/></font>
		    &nbsp;
		    <html:select name="myObjectiveForm" property="month" size="1" onchange="query();">
			<html:optionsCollection property="monthList" value="dataValue" label="dataName"/>
		    </html:select>
		     <font class="<%=tt3CssName%>"><bean:message key="org.performance.month"/></font>
		    &nbsp;
		   <font class="<%=tt3CssName%>"> <bean:message key="org.performance.planstatus"/></font>
		     <html:select name="myObjectiveForm" property="status" size="1" onchange="query();">
			<html:optionsCollection property="statusList" value="dataValue" label="dataName"/>
		    </html:select>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td align="center">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<td width="5%" align="center" class="TableRow" nowrap>
<input type="checkbox" name="allselect" id="select" onclick="selectAllRecord();"/>
</td>
<td width="35%" align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="org.performance.khname"/></font>
</td>
<td width="20%" align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="<%=evaluate%>"/></font>
</td>
<td width="20%" align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="org.performance.khgrade"/></font>
</td>
<td width="10%" align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>">状态</font>
</td>
<td width="10%" align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="column.operation"/></font>
</td>
</tr>
</thead>
<% int j=0; %>
 <hrms:extenditerate id="element" name="myObjectiveForm" property="myListForm.list" indexes="indexes"  pagination="myListForm.pagination" pageCount="15" scope="session">
 <%if(j%2==0){ %>
	     <tr class="trShallow" onClick="javascript:tr_onclick(this,'#E4F2FC')">
	     <%} else { %>
	     <tr class="trDeep" onClick="javascript:tr_onclick(this,'#E4F2FC')">
	     <%}%>
	     <input type="hidden" name="optflag" value="<bean:write name="element" property="opt"/> ">
	     <td align="center" class="RecordRow" nowrap>
	    <input type="checkbox" name="records" value="<bean:write name="element" property="record"/>"/>
	     </td>
	      <td align="left" class="RecordRow" nowrap>
	     &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="name"/></font>
	     </td>
	       <td align="left" class="RecordRow" nowrap>
	     &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="evaluate"/></font>
	     </td>
	       <td align="left" class="RecordRow" nowrap>
	     &nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="gradedesc"/></font>
	     </td>
	        <td align="center" class="RecordRow" nowrap>
	        <logic:equal value="07" property="sp_flag" name="element"><a href="javascript:searchReject('<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="mdplanid"/>');"><font class="<%=tt3CssName%>"><bean:write name="element" property="sp"/></font></a></logic:equal>
	     <logic:notEqual value="07" property="sp_flag" name="element"><font class="<%=tt3CssName%>">
	    <logic:equal value="03" property="sp_flag" name="element">
	    <logic:equal value="07" property="trace_flag" name="element">
	          <a href="javascript:searchReject('<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="mdplanid"/>');">  <bean:write name="element" property="tsp"/></a>
	     </logic:equal>
	     <logic:notEqual value="07" property="trace_flag" name="element">
	           <bean:write name="element" property="tsp"/>
	     </logic:notEqual>
	    </logic:equal>
	     <logic:notEqual value="03" property="sp_flag" name="element">
	     <bean:write name="element" property="sp"/>
	    </logic:notEqual>
	     </font></logic:notEqual>
	     </td>
	       <td align="center" class="RecordRow" nowrap>
	      <logic:equal value="0" name="element" property="status">
	      <a href="javascript:myObjective('<bean:write name="element" property="opt"/>','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>');"><font class="<%=tt3CssName%>">查看</font></a>
	      </logic:equal>
		<logic:equal value="1" name="element" property="status">
			<%-- 操作描述:1=确认, !1=自评 modify by lium--%>
			<logic:equal value="1" name="element" property="planOpt">
				<bean:define id="opDesc" value="确认"></bean:define>
			</logic:equal>
			<logic:notEqual value="1" name="element" property="planOpt">
				<bean:define id="opDesc" value="自评"></bean:define>
			</logic:notEqual>
			<a href="javascript:myObjective('<bean:write name="element" property="opt"/>','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>');"><font class="<%=tt3CssName%>">${opDesc }</font></a>
		</logic:equal>
	       <logic:equal value="2" name="element" property="status">
	       <a href="javascript:myObjective('<bean:write name="element" property="opt"/>','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>');"><font class="<%=tt3CssName%>"><%=editDesc%></font><!--  制订目标--></a>
	      </logic:equal>
	       <logic:notEqual value="4" name="element" property="status">
	      <logic:equal value="1" name="element" property="bs">
	       <a href="javascript:myObjective('1','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>');"><font class="<%=tt3CssName%>">调整</font></a>
	      </logic:equal>
	      </logic:notEqual>
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
		   ${myObjectiveForm.myListForm.pagination.current}
					<bean:message key="label.page.sum"/>
		   ${myObjectiveForm.myListForm.pagination.count}
					<bean:message key="label.page.row"/>
		   ${myObjectiveForm.myListForm.pagination.pages}
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
            <hrms:paginationlink name="myObjectiveForm" property="myListForm.pagination" nameId="myListForm" propertyId="myListProperty">
		   </hrms:paginationlink>
		   </p>
		   </td>
		</tr> 
</table>
</td>
</tr>
<tr>
<td align="left" style="padding-top:3px" style="height:35px">
<hrms:priv func_id="06070201"> 

<input type="button" name="export" class="<%=buttonClass%>" value="<bean:message key="info.appleal.state13"/>" onclick="exportObjectiveCard();"/>

</hrms:priv>
<hrms:priv func_id="06070215"> 
<input type="button" name="export" class="<%=buttonClass%>" value="查看下达任务" onclick="searchTask();"/>
</hrms:priv>
<% if(returnflag.equals("8")||returnflag.equals("10")){

	out.println("<input type='button' name='export' class='mybutton' value='返回' onclick='goback(\""+returnflag+"\")' />");

	}
%>
</td>
</tr>
</table>
</html:form>
</td>
</tr>
<tr>
<td>
<html:form action="/performance/objectiveManage/objectiveCard">
<input type="hidden" name="returnURL" value="/performance/objectiveManage/myObjective/my_objective_list.do?b_init=init2&entranceType=0&opt=3&year=${myObjectiveForm.year}&quarter=${myObjectiveForm.quarter}&month=${myObjectiveForm.month}&status=${myObjectiveForm.status}"/>
<input type="hidden" name="target" value="il_body"/>
</html:form>
</td></tr>
</table>

