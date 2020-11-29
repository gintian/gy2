<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hjsj.hrms.actionform.performance.objectiveManage.setUnderlingObjective.SetUnderlingObjectiveForm" %>
<%@ page import="
				com.hrms.hjsj.sys.DataDictionary,
				com.hrms.hjsj.sys.FieldItem,
				com.hjsj.hrms.utils.PubFunc,
				com.hrms.struts.constant.SystemConfig" %>
<%
   	String tt4CssName="ttNomal4";
   	String tt3CssName="ttNomal3";
   	String buttonClass="mybutton";
   	boolean flag2=true;
 
   	if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   	{
      	tt4CssName="tt4";
      	tt3CssName="tt3";
      	buttonClass="mybuttonBig";
      	flag2=false;
   	}
    String editDesc="设定";
    if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
    {
      editDesc="制订";
    }
   	boolean flag=true;
   	if(SystemConfig.getPropertyValue("isVisibleUN").equalsIgnoreCase("false"))
   	{
     	flag=false;
   	}
	SetUnderlingObjectiveForm setUnderlingObjectiveForm = (SetUnderlingObjectiveForm)session.getAttribute("setUnderlingObjectiveForm");
	String returnflag=setUnderlingObjectiveForm.getReturnflag();
	if(returnflag==null)
		returnflag="menu";
	String url_extends="";
	
	url_extends="&returnflag=menu";		
		
	String target="mil_body";
	String lt="&zglt=0";
	String md_planid=setUnderlingObjectiveForm.getPlan_id();
 
	String url_p=SystemConfig.getServerURL(request);
	String object_type = setUnderlingObjectiveForm.getObject_type();	
      
%>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript">

<!--
var pa="-1";
function query()
{
   setUnderlingObjectiveForm.action="/performance/objectiveManage/setUnderlingObjective/underling_objective_view_list.do?b_view=init&opt=2";
   setUnderlingObjectiveForm.submit();
}
function unberlingObjective(opt,planid,a0100,level)
{
   if(pa =="-1"||pa!=planid+a0100)
   {
      pa=planid+a0100;
      var entranceType="${setUnderlingObjectiveForm.entranceType}";
      objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query<%=lt%><%=url_extends%>&entranceType=0&body_id=1&model=6&opt="+opt+"&planid="+planid+"&object_id="+a0100;
      objectCardForm.submit();
   }
    else
   {
     window.setTimeout('setPAValue()',2000);   
   }
}
function searchReject(object_id,plan_id)
{
    var thecodeurl="/performance/objectiveManage/myObjective/my_objective_list.do?b_reject=link`opt=1`type=2`plan_id="+plan_id+"`object_id="+object_id; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
					        "dialogWidth:600px; dialogHeight:465px;resizable:no;center:yes;scroll:yes;status:no");			
	
}
function setPAValue()
{
   pa="-1";
}
function allSelect(obj)
{
   var arr=document.getElementsByName("oid");
   if(arr)
   {
      for(var i=0;i<arr.length;i++)
      {
        if(obj.checked)
            arr[i].checked=true;
        else
            arr[i].checked=false;
      }
   }
}

//导出目标卡
function downLoadTarget()
{	/**
	var records=document.getElementsByName("oid");	
	var plan_id="${setUnderlingObjectiveForm.plan_id}";	
    var num=0;
    var selectTargetCalcItemts="";
    if(records)
    {
      	for(var i=0;i<records.length;i++)
      	{     		
	    	if(records[i].checked)
	        {
	        	num++;
	            selectTargetCalcItemts+="/"+records[i].value+"-"+plan_id;
	        }	        
      	}
   	}  	
   	if(num==0)
   	{
      	alert("请选择记录！");
      	return;
   	} 
 	var hashvo=new ParameterSet();     
	hashvo.setValue("records",selectTargetCalcItemts.substring(1));
//	hashvo.setValue("plan_id","${setUnderlingObjectiveForm.plan_id}");	     
	hashvo.setValue("model",'6');
	hashvo.setValue("body_id",'1');
	hashvo.setValue("underOpt",'0');
	hashvo.setValue("logo",'4');
	var In_parameters="opt=1";
	var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:export_ok,functionId:'9028000405'},hashvo);	
	**/
	
   var records=document.getElementsByName("oid");
   var bacthdata=document.getElementsByName("bacthdata");
	var plan_id="<%=md_planid%>";	
    var num=0;
    var selectTargetCalcItemts="";
   
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
        	var t=bacthdata[i].value.split("`");
            var _obj_id=t[1];
            var _plan_id=t[0];
            selectTargetCalcItemts+="/"+_obj_id+"-"+_plan_id;
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
    hashVo.setValue("records",selectTargetCalcItemts.substring(1));
    hashVo.setValue("model",'6');/// 1:团对 2:我的目标 3:目标制订 4.目标评估 5.目标结果 6:目标执行情况 7:目标卡代制订 8:评分调整
	hashVo.setValue("body_id",body_id);
	hashVo.setValue("underOpt",opt);
	hashVo.setValue("logo",'4');// 1:我的目标；2:员工目标；3:目标评分；4:目标执行情况；5:团队绩效
	var In_parameters="opt=1";
	var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:export_ok,functionId:'9028000405'},hashVo);			
		     	  	
}
function export_ok(outparameters)
{
	var fileName=outparameters.getValue("fileName");
//	var win=open("/servlet/DisplayOleContent?filename="+fileName,"excel");
	//20/3/6 xus vfs改造
	var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true");
}
/**
 * 判断当前浏览器是否为ie6
 * 返回boolean 可直接用于判断 
 * @returns {Boolean}
 */
function isIE6() 
{ 
	if(navigator.appName == "Microsoft Internet Explorer") 
	{ 
		if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
		{ 
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}
// 反查结果
function reverseResult(opt,plan_id,object_id,alreadyCaseMainbody)
{			              
	var thecodeurl="/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_reverseResult=link`opt="+opt+"`plan_id="+plan_id+"`object_id="+object_id+"`alreadyCaseMainbody="+alreadyCaseMainbody; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20180105
	var iTop; 
	var iLeft;
	if(isIE6()){
	    //var retvo = window.showModalDialog(iframe_url, null, 
			 //  "dialogWidth:690px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no");
	    iTop = (window.screen.availHeight - 30 - 420) / 2;  //获得窗口的垂直位置
		iLeft = (window.screen.availWidth - 10 - 690) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width=690px,height=420px,resizable=no,scrollbars=yes,status=no,left="+iLeft+",top="+iTop);
	}else{
	   // var retvo = window.showModalDialog(iframe_url, null, 
		//	   "dialogWidth:680px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	    iTop = (window.screen.availHeight - 30 - 400) / 2;  //获得窗口的垂直位置
		iLeft = (window.screen.availWidth - 10 - 680) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width=680px,height=400px,resizable=no,scrollbars=yes,status=no,left="+iLeft+",top="+iTop);
	}
	
}

// 回顾催办
function sendMessageOrEmail()
{	 	
	var records=document.getElementsByName("oid");
    var num=0;
    var selectItemts="";
    if(records)
    {
      	for(var i=0;i<records.length;i++)
      	{
         	if(records[i].checked)
         	{
            	num++;
            	selectItemts+=","+records[i].value;
         	}
      	}
   	}
   	if(num==0)
   	{
      	alert("请您选择记录！");
      	return;
   	} 
   	 	   
	var url="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_initMail=link`opt=4`plan_id="+'${setUnderlingObjectiveForm.plan_id}'+"`to_a0100="+selectItemts;
    	url+="`isAll=1";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url)
    //window.showModalDialog(iframe_url,"","dialogWidth=700px;dialogHeight=550px;resizable=yes;scroll=yes;status=no;");
   
    //兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
    var iTop = (window.screen.availHeight - 30 - 700) / 2;  //获得窗口的垂直位置
    var iLeft = (window.screen.availWidth - 10 - 550) / 2; //获得窗口的水平位置 
    window.open(iframe_url,"","width=700px,height=550px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop); 	  
}

//-->
</script>
<hrms:themes />

<html>
	<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
	<head>
		<title>title</title>
	</head>
	
	<body>
		<base id="mybase" target="_self">
		<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
			<tr>
				<td>
					<html:form action="/performance/objectiveManage/setUnderlingObjective/underling_objective_view_list">
						<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
							<tr>
								<td align="left">
									<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
									    <%--bug 36071 下拉框 与表格边框线重叠 修改 td高度 20 改30px  wangb 20180328--%>
										<td align="left" style="height:30px"> 
											<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
											<html:hidden property="a0100" name="setUnderlingObjectiveForm"/>
											<html:hidden property="plan_id" name="setUnderlingObjectiveForm"/>
											<html:hidden property="posid" name="setUnderlingObjectiveForm"/>
											<font class="<%=tt3CssName%>"><bean:message key="org.performance.status"/></font>:
											<html:select name="setUnderlingObjectiveForm" property="status" size="1" onchange="query();">
												<html:optionsCollection property="statusList" value="dataValue" label="dataName"/>
		    								</html:select>
										</td>
									</table>
								</td>
							</tr>
							<tr>
								<td  valign="top">
									<div class="tbl-container complex_border_color" style='overflow:auto;width:100%;height:400;'>
										<table width="100%" border="0" cellspacing="0"  style="margin-top:-1" align="center" cellpadding="0" class="ListTable">
										
										<%if(object_type!=null&&!object_type.equals("2")){ %>
										
											<thead>
												<tr>													
													<td class="TableRow_right common_background_color" rowspan="2" align="center">
													  <input type="checkbox" name="as" value="0" onclick="allSelect(this);" title="全选"/>
													</td>
													
													<td align="center" rowspan="2" class="TableRow_right common_background_color" nowrap>
													&nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.unorum"/></font>&nbsp;
													</td>
													
													<td align="center" rowspan="2" class="TableRow_right common_background_color" nowrap>
													&nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.khgrade"/></font>&nbsp;
													</td>
													
													<td align="center" colspan="2" class="TableRow_right common_background_color" nowrap>
													  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="jx.performance.targetCardPerformCase"/></font>&nbsp;
													</td>													
													
													<td align="center" rowspan="2" class="TableRow_right common_background_color" nowrap>
													&nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.status"/></font>&nbsp;
													</td>
													
													<td align="center" rowspan="2" class="TableRow_left common_background_color" width="95" nowrap>
													&nbsp;<font class="<%=tt4CssName%>"><bean:message key="column.operation"/></font>&nbsp;
													</td>
												</tr>
												<tr>
													<td align="center" class="TableRow_right common_background_color" style="height:30px;" nowrap>
														&nbsp;<font class="<%=tt4CssName%>"><bean:message key="jx.performance.alreadyTargetCardPerformCase"/></font>&nbsp;
													</td>
													<td align="center" class="TableRow_right common_background_color" nowrap>
													    &nbsp;<font class="<%=tt4CssName%>"><bean:message key="jx.performance.noAlreadyTargetCardPerformCase"/></font>&nbsp;
													</td>
												</tr>
											</thead>
											<% int j=0; %>
											<hrms:extenditerate id="element" name="setUnderlingObjectiveForm" property="personListForm.list" indexes="indexes"  pagination="personListForm.pagination" pageCount="15" scope="session">
											<%if(j%2==0){ %>
												     <tr class="trShallow">
												   <%} else { %>
												     <tr class="trDeep">
												   <%}%>
												     	     <input type="hidden" name="optflag" value="<bean:write name="element" property="opt"/> ">
	    													 <input type="hidden" name="levelflag" value="<bean:write name="element" property="level"/> ">
											             <input type="hidden" name="bacthdata" value="<bean:write name="element" property="bacthdata"/>"/>
												     <td class="RecordRow_right" align="center">          
											             <input type="checkbox" name="oid" value="<bean:write name="element" property="m_a0100"/>"/>
											             <input type="hidden" name="alevel" value="<bean:write name="element" property="level"/>"/>          
											         </td>
												     
												     <td align="left" class="RecordRow" nowrap>
												     	&nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="b0110"/></font>
												     </td>
												     <td align="left" class="RecordRow" nowrap>
												     	&nbsp;&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="gradedesc"/></font>
												     </td>
													 <td align="center" class="RecordRow" nowrap>
														<logic:notEqual name="element" property="alreadyPerformCase"  value="0">
														 	<a href="javascript:reverseResult('already',
														 									  '<bean:write name="element" property="planid" filter="true"/>',
																							  '<bean:write name="element" property="a0100" filter="true"/>',																						 																						  
																							  '<bean:write name="element" property="alreadyCaseMainbody" filter="true"/>')" >							 								 									
													     		<font class="<%=tt3CssName%>"><bean:write name="element" property="alreadyPerformCase"/></font>
													     	</a>
													     </logic:notEqual>
													     <logic:equal name="element" property="alreadyPerformCase"  value="0">
													     	<font class="<%=tt3CssName%>"><bean:write name="element" property="alreadyPerformCase"/></font>
													     </logic:equal>
												     </td>
												     <td align="center" class="RecordRow" nowrap>
												     	<logic:notEqual name="element" property="noAlreadyPerformCase"  value="0">
													        <a href="javascript:reverseResult('noAlready',
														 									  '<bean:write name="element" property="planid" filter="true"/>',
																							  '<bean:write name="element" property="a0100" filter="true"/>',																						 																						  
																							  '<bean:write name="element" property="alreadyCaseMainbody" filter="true"/>')" >	
													     		<font class="<%=tt3CssName%>"><bean:write name="element" property="noAlreadyPerformCase"/></font>
													     	</a>
													     </logic:notEqual>
													     <logic:equal name="element" property="noAlreadyPerformCase"  value="0">
													     	<font class="<%=tt3CssName%>"><bean:write name="element" property="noAlreadyPerformCase"/></font>
													     </logic:equal>
												     </td>
													 
												     <td align="left" class="RecordRow" nowrap>
												     	&nbsp;<font class="<%=tt3CssName%>">

												      		<bean:write name="element" property="sp_flag"/>

												      	</font>
												     </td>
											
												     <td align="center" class="RecordRow_left" nowrap>
												       <a href="javascript:unberlingObjective('0','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">查看</font></a>
												     </td>	
											<% j++; %>
											</hrms:extenditerate>
											
										<%}else{ %>
										
											<thead>
												<tr>											
													<td class="TableRow_right common_background_color" align="center" rowspan="2">
													  <input type="checkbox" name="as" value="0" onclick="allSelect(this);" title="全选"/>
													</td>
													
													<%if(flag){ %>
													  <td align="center" rowspan="2" class="TableRow_right common_background_color" nowrap>
													  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="b0110.label"/></font>&nbsp;
													  </td>
													  <%} %>
													  <td align="center" rowspan="2" class="TableRow_right common_background_color" nowrap>
													  &nbsp;<font class="<%=tt4CssName%>">
													  <%
															FieldItem fielditem = DataDictionary.getFieldItem("E0122");
													  %>	         
															<%=fielditem.getItemdesc()%></font>&nbsp;
													  </td>
													  <td align="center" rowspan="2" class="TableRow_right common_background_color" nowrap>
													  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="e01a1.label"/></font>&nbsp;
													  </td>
													  <td align="center" rowspan="2" class="TableRow_right common_background_color" nowrap>
													  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="hire.employActualize.name"/></font>&nbsp;
													  </td>
													
													  <td align="center" colspan="2" class="TableRow_right common_background_color" nowrap>
													  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="jx.performance.targetCardPerformCase"/></font>&nbsp;
													  </td>
													  
													  <td align="center" rowspan="2" class="TableRow_right common_background_color" nowrap>
													  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.status"/></font>&nbsp;
													  </td>
													
													  <td align="center" rowspan="2" class="TableRow_left common_background_color" nowrap>
													  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="kh.field.opt"/></font>&nbsp;
													  </td>
												</tr>
												<tr>
													<td align="center" class="TableRow_right common_background_color" style="height:30px;" nowrap>
														&nbsp;<font class="<%=tt4CssName%>"><bean:message key="jx.performance.alreadyTargetCardPerformCase"/></font>&nbsp;
													</td>
													<td align="center" class="TableRow_right common_background_color" nowrap>
													    &nbsp;<font class="<%=tt4CssName%>"><bean:message key="jx.performance.noAlreadyTargetCardPerformCase"/></font>&nbsp;
													</td>
												</tr>
											</thead>
										<% int j=0; %>
									 		<hrms:extenditerate id="element" name="setUnderlingObjectiveForm" property="personListForm.list" indexes="indexes"  pagination="personListForm.pagination" pageCount="15" scope="session">
										 	<%if(j%2==0){ %>
											     <tr class="trShallow">
											<%} else { %>
											     <tr class="trDeep">
											<%}%>
															 <input type="hidden" name="optflag" value="<bean:write name="element" property="opt"/> ">
	    													 <input type="hidden" name="levelflag" value="<bean:write name="element" property="level"/> ">
										                      <input type="hidden" name="bacthdata" value="<bean:write name="element" property="bacthdata"/>"/>
										     <td class="RecordRow_right" align="center">          
									             <input type="checkbox" name="oid" value="<bean:write name="element" property="m_a0100"/>"/>
									             <input type="hidden" name="alevel" value="<bean:write name="element" property="level"/>"/>          
									         </td>
									
										     <%if(flag){ %>
										     <td align="left" class="RecordRow" nowrap>
										     &nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="b0110"/></font>&nbsp;
										     </td>
										     <%} %>
										      <td align="left" class="RecordRow" nowrap>
										     &nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="e0122"/></font>&nbsp;
										     </td>
										      <td align="left" class="RecordRow" nowrap>
										     &nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="e01a1"/></font>&nbsp;
										     </td>
										      <td align="left" class="RecordRow" nowrap>
										     &nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="a0101"/></font>&nbsp;
										     </td>
									
											 <td align="center" class="RecordRow" nowrap>
											 	<logic:notEqual name="element" property="alreadyPerformCase"  value="0">
												 	<a href="javascript:reverseResult('already',
														 							  '<bean:write name="element" property="planid" filter="true"/>',
																					  '<bean:write name="element" property="a0100" filter="true"/>',																						 																						  
																					  '<bean:write name="element" property="alreadyCaseMainbody" filter="true"/>')" >
													 	<font class="<%=tt3CssName%>"><bean:write name="element" property="alreadyPerformCase"/></font>
													</a>
												</logic:notEqual>
												<logic:equal name="element" property="alreadyPerformCase"  value="0">
											     	<font class="<%=tt3CssName%>"><bean:write name="element" property="alreadyPerformCase"/></font>
											     </logic:equal>
											 </td>
											 <td align="center" class="RecordRow" nowrap>
											 	<logic:notEqual name="element" property="noAlreadyPerformCase"  value="0">
												 	<a href="javascript:reverseResult('noAlready',
														 							  '<bean:write name="element" property="planid" filter="true"/>',
																					  '<bean:write name="element" property="a0100" filter="true"/>',																						 																						  
																					  '<bean:write name="element" property="alreadyCaseMainbody" filter="true"/>')" >
													 	<font class="<%=tt3CssName%>"><bean:write name="element" property="noAlreadyPerformCase"/></font>
													</a>
												</logic:notEqual>
												<logic:equal name="element" property="noAlreadyPerformCase"  value="0">
											     	<font class="<%=tt3CssName%>"><bean:write name="element" property="noAlreadyPerformCase"/></font>
											     </logic:equal>
											 </td>
												     
										     <td align="left" class="RecordRow" nowrap>
										     	&nbsp;<font class="<%=tt3CssName%>">

											      		<bean:write name="element" property="sp_flag"/>

										      	</font>
										     </td>
									
										     <td align="center" class="RecordRow_left" nowrap>
										       		<a href="javascript:unberlingObjective('0','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">查看</font></a>
										     </td>	     
										   </tr>
										   <% j++; %>
										  </hrms:extenditerate>
									<%} %>
								</table>
							</div>
							
							
						<table  width="100%" align="center" class="PersonRecordRow common_border_color">
							<tr>
							   <td valign="bottom" class="tdFontcolor" nowrap>
							            <bean:message key="label.page.serial"/>
							   ${setUnderlingObjectiveForm.personListForm.pagination.current}
										<bean:message key="label.page.sum"/>
							   ${setUnderlingObjectiveForm.personListForm.pagination.count}
										<bean:message key="label.page.row"/>
							   ${setUnderlingObjectiveForm.personListForm.pagination.pages}
										<bean:message key="label.page.page"/>
							   </td>
							   <td align="right" class="tdFontcolor" nowrap>
							   <p align="right">
					            <hrms:paginationlink name="setUnderlingObjectiveForm" property="personListForm.pagination" nameId="personListForm" propertyId="personListProperty">
							   </hrms:paginationlink>
							   </p>
							   </td>
							</tr> 
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">	
							<tr>
								<td align="left" style="height:35px">
									<hrms:priv func_id="06070701">
										<input type="button" name="downLoad" class="mybutton" value="<bean:message key="info.appleal.state13"/>" onclick="downLoadTarget();"/>			
									</hrms:priv>
									<hrms:priv func_id="06070702">
										<input type="button" name="downLoad" class="mybutton" value="<bean:message key="jx.performance.cardPerformCaseSendMessage"/>" onclick="sendMessageOrEmail();"/>			
									</hrms:priv>								
								</td>
							</tr>
						</table>
					</td>
				</tr>

			</table>
		</html:form>
	</td>
</tr>
	<tr>
		<td>
			<html:form action="/performance/objectiveManage/objectiveCard">
				<input type="hidden" name="returnURL" value="/performance/objectiveManage/setUnderlingObjective/underling_objective_view_list.do?b_view=init&entranceType=${setUnderlingObjectiveForm.entranceType}&opt=3&posid=${setUnderlingObjectiveForm.posid}&plan_id=${setUnderlingObjectiveForm.plan_id}&a0100=${setUnderlingObjectiveForm.a0100}&status=${setUnderlingObjectiveForm.status}"/>
				<input type="hidden" name="target" value="<%=target%>"/>
			</html:form>
		</td>
	</tr>
</table>
</body>

<script type="text/javascript">
	if(!getBrowseVersion()){//兼容非IE浏览器  wangb  20171208
		var mybase = document.getElementById('mybase');
		var tempTd = mybase.parentNode;//取消 模板td的 滚动条
		tempTd.style.overflow = '';
		var tblContainer = document.getElementsByClassName('tbl-container')[0];//不设置width 样式 和table宽度设置小点不显示div水平滚动条
		tblContainer.style.width = '';
		var taltable = tblContainer.getElementsByTagName('table')[0];
		taltable.setAttribute('width','99.8%');
	}
</script>

</html>