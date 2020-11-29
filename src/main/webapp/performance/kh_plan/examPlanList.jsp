<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.kh_plan.ExamPlanForm,	
				 org.apache.commons.beanutils.LazyDynaBean,			 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.frame.dao.RecordVo,
				 com.hrms.struts.constant.WebConstant" %>
				 
				 
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="javascript" src="/performance/kh_plan/examPlan.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>

<script type="text/javascript">

var IVersion=getBrowseVersion();

if(IVersion==8)
{
  	document.writeln("<link href=\"/performance/kh_plan/kh_planTableLocked_8.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
  	document.writeln("<link href=\"/performance/kh_plan/kh_planTableLocked.css\" rel=\"stylesheet\" type=\"text/css\">");
}


</script>

<!--
<link href="/performance/kh_plan/kh_planTableLocked.css" rel="stylesheet" type="text/css">  
-->
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
div#tbl-container tr{
	line-height: normal;
}
.myfixedDiv
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
.Input_self
{  
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;                                                                  
  	font-size: 12px;                                              
  	letter-spacing: 1px;                      
  	text-align: right;                        
  	height: 90%;                                    
  	width: 80%; 
  	cursor: hand;                                     
} 

</style>
<hrms:themes></hrms:themes>
<% 
	ExamPlanForm myForm=(ExamPlanForm)session.getAttribute("examPlanForm");	
	String model = myForm.getModel();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	int dataSize = myForm.getSetlist().size();	
	int currentPage = myForm.getSetlistform().getPagination().getCurrent();
	int pagecount = myForm.getSetlistform().getPagination().getPageCount();
	int pages = myForm.getSetlistform().getPagination().getPages();
	int lastIndex = pagecount;//当前页的最后一条	

	if(pages>1&&currentPage<pages)
		lastIndex = pagecount;
	else if(pages>1&&currentPage==pages)		
		lastIndex=dataSize-pagecount*(currentPage-1);
	else if(pages==1)
		lastIndex = dataSize;
	boolean zp=true;	
	if(model.equals("1"))
		zp=false;
String tar=userView.getBosflag();
%>
<body onload="setScrollTop();">
<html:form action="/performance/kh_plan/examPlanList">

	<html:hidden name="examPlanForm" property="busitype" styleId="busitype" />
	<html:hidden name="examPlanForm" property="startDate" styleId="startDate" />
	<html:hidden name="examPlanForm" property="endDate" styleId="endDate" />
	<html:hidden name="examPlanForm" property="model" styleId="model" />
	<html:hidden name="examPlanForm" property="paramStr" styleId="paramStr" />
	<html:hidden name="examPlanForm" property="scrollValue" styleId="scrollValueId"/>
	<input type="hidden" id="planId" />
	<input type="hidden" id='review_status'>


				<table border="0" cellspacing="0" cellpadding="0" width="100%"  >
					<tr>
						<td align="left"  nowrap style="height:35;vertical-align: center;">
							&nbsp;&nbsp;<bean:message key="jx.khplan.spstatus" />
							<html:select name="examPlanForm" property="spStatus" size="1"
								onchange="search_kh_data(this);">
								<html:option value="all">
									<bean:message key="label.all" />
								</html:option>
								<html:option value="0">
									<bean:message key="hire.jp.pos.draftout" />
								</html:option>
								<html:option value="1">
									<bean:message key="info.appleal.state1" />
								</html:option>
								<html:option value="2">
									<bean:message key="label.hiremanage.status3" />
								</html:option>
								<html:option value="3">
									<bean:message key="button.issue" />
								</html:option>
								
								<logic:equal name="examPlanForm" property="busitype" value="0">
									<html:option value="8">
										<bean:message key="performance.plan.distribute" />
									</html:option>
								</logic:equal>	
								
								<html:option value="4">
									<bean:message key="gz.formula.implementation" />
								</html:option>
								<html:option value="5">
									<bean:message key="lable.performance.status.pause" />
								</html:option>
								<html:option value="6">
									<bean:message key="jx.khplan.Appraisal" />
								</html:option>
								<html:option value="7">
									<bean:message key="label.hiremanage.status6" />
								</html:option>								
							</html:select>
							&nbsp;
							<bean:message key="jx.khplan.timeframe" />
							<html:select name="examPlanForm" property="timeInterval" size="1"
								onchange="search_kh_data(this);" styleId="timeInterval">
								<html:option value="all">
									<bean:message key="label.all" />
								</html:option>
								<html:option value="1">
									<bean:message key="jx.khplan.currentyear" />
								</html:option>
								<html:option value="2">
									<bean:message key="jx.khplan.currentquarter" />
								</html:option>
								<html:option value="3">
									<bean:message key="jx.khplan.timeduan" />
								</html:option>
							</html:select>
								<span id="datepnl">							
								<bean:message key="label.from" /> 
						
							<input type="text" name="start_date"
									value="${examPlanForm.startDate}" extra="editor"
									style="width:100px;font-size:10pt;text-align:left" id="editor1"
									dropDown="dropDownDate"> 	
						
								<bean:message key="label.to" />
					
							<input type="text" name="end_date"
									value="${examPlanForm.endDate}" extra="editor"
									style="width:100px;font-size:10pt;text-align:left" id="editor2"
									dropDown="dropDownDate">  													
								</span>				
							<bean:message key="general.mediainfo.name" />
							<html:text name="examPlanForm" property="qname" onkeypress="EnterPress(event)" onkeydown="EnterPress()" styleClass="inputtext"/>														
							
							<logic:equal name="examPlanForm" property="busitype" value="0">
								&nbsp;
								<bean:message key="jx.khplan.khmethod" />
								<html:select name="examPlanForm" property="qmethod" size="1"	onchange="search_kh_data(this);">
									<html:option value="all">
										<bean:message key="label.all" />
									</html:option>
									<html:option value="1">
										<bean:message key="jx.khplan.khmethod1" />
									</html:option>
									<html:option value="2">
										<bean:message key="jx.khplan.khmethod2" />
									</html:option>
								</html:select>
								&nbsp;
								<bean:message key="jx.khplan.objectype" />
								<html:select name="examPlanForm" property="qobject_type" size="1"	onchange="search_kh_data(this);">
									<html:option value="all">
										<bean:message key="label.all" />
									</html:option>
									<html:option value="1">
										<bean:message key="jx.khplan.team" />
									</html:option>
									<html:option value="2">
										<bean:message key="label.query.employ" />
									</html:option>
									<html:option value="3">
										<bean:message key="jx.khplan.unit" />
									</html:option>
									<html:option value="4">
										<bean:message key="column.sys.dept" />
									</html:option>
								</html:select>
							</logic:equal>							
							
							&nbsp;
							<input type="button"
									onclick="search_kh_data(this);" class="mybutton"
									value="<bean:message key="button.query"/>">
						</td>
					</tr>
				</table>

		<%	int i = 0;String classname="t_cell_locked common_border_color";%>					
 			<script language='javascript' >
				document.write("<div id=\"tbl-container\"  style='position:absolute;left:5;height:"+(document.body.clientHeight-135)+";width:99%'  >");
 			</script> 
				<table width="100%" border="0" cellspacing="0" id='a_table'  align="center" cellpadding="0">
		 			<thead>
						<tr>
			   				<td align="center"   class='t_cell_locked3 common_border_color' height="25"   nowrap >
								&nbsp;<input type="checkbox" name="selbox" onclick="batch_select(this, 'setlistform.select');">&nbsp;
							</td>
							<td align="center"   class='t_cell_locked3 common_border_color' height="25"   nowrap >
								&nbsp;<bean:message key="lable.zp_plan.plan_id" />&nbsp;
							</td>
							<td align="center"   class='t_cell_locked3 common_border_color' height="25"   nowrap >
								&nbsp;<bean:message key="column.name" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="column.sys.status" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="lable.resource_plan.org_id" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.cycle" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.khtimeqj" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
							   &nbsp;<logic:equal name="examPlanForm" property="busitype" value="0">
							   		<bean:message key="jx.khplan.template" />
							   </logic:equal>
							   <logic:equal name="examPlanForm" property="busitype" value="1">
							   		<bean:message key="jx.khplan.templatenl" />
							   </logic:equal>&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.planparam" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.gathertype" />&nbsp;
							</td>
							
							<logic:equal name="examPlanForm" property="busitype" value="0">
								<td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
									&nbsp;<bean:message key="jx.khplan.objectype" />&nbsp;
								</td>
							</logic:equal>
							
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.plantype" />&nbsp;
							</td>
							
							<logic:equal name="examPlanForm" property="busitype" value="0">
							   	<td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
									&nbsp;<bean:message key="jx.khplan.khmethod" />&nbsp;
								</td>
							</logic:equal>
							
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.plandescrip" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.khmb" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.khcontent" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.khprograme" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.khresultapply" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.agreedate" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.agreeuser" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.agreeidea" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.approveresult" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.creator" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="jx.khplan.createdate" />&nbsp;
							</td>
							   <td align="center"   class='t_header_locked2 common_border_color'     height="25"   nowrap >
								&nbsp;<bean:message key="label.order" />&nbsp;
							</td>							   
						</tr>
						<hrms:extenditerate id="element" name="examPlanForm"
							property="setlistform.list" indexes="indexes"
							pagination="setlistform.pagination" pageCount="25"
							scope="session">
							<bean:define id="nid" name="element" property="plan_id" />
							<bean:define id="status" name="element" property="status" />
							<%
									if (i % 2 == 0)
									{
							%>
							<tr class="trShallow"
								onclick='setCvalue("${nid}","${status}");tr_onclick_self(this,"#F3F5FC");'>
								<%
										} else
										{
								%>
							
							<tr class="trDeep"
								onclick='setCvalue("${nid}","${status}");tr_onclick_self(this,"#E4F2FC");'>
								<%
										}
										i++;
										if(lastIndex==i)
											classname = "t_cell_locked_b common_border_color";
											LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
											String plan_id= (String)abean.get("plan_id");
							
								%>
								<td align="center" class="<%=classname %>" nowrap style="border-top:0px;">
									<Input type='hidden' id="${nid}" />
									<hrms:checkmultibox name="examPlanForm" property="setlistform.select" value="true" indexes="indexes" />
									<Input type='hidden' value='<bean:write name="element" property="plan_id" filter="true"/>' />
									<Input type='hidden' value='<bean:write name="element" property="status" filter="true"/>' />
								
								</td>
								<td align="right" class="<%=classname%>" nowrap>
									&nbsp;<bean:write name="element" property="plan_id" filter="true" />&nbsp;
								</td>
								<td align="left"  class="<%=classname%>"  nowrap>
									&nbsp;<a href="javascript:edit('<%=plan_id%>');">
										<bean:write name="element" property="name" filter="false" /> 
										</a>&nbsp;
								</td>
								<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:equal name="element" property="status" value="0">
										<bean:message key="hire.jp.pos.draftout" />
									</logic:equal>
									<logic:equal name="element" property="status" value="1">
										<bean:message key="info.appleal.state1" />
									</logic:equal>
									<logic:equal name="element" property="status" value="2">
										<bean:message key="label.hiremanage.status3" />
									</logic:equal>
									<logic:equal name="element" property="status" value="3">
										<bean:message key="button.issue" />
									</logic:equal>
									<logic:equal name="element" property="status" value="4">
										<bean:message key="gz.formula.implementation" />
									</logic:equal>
									<logic:equal name="element" property="status" value="5">
										<bean:message key="lable.performance.status.pause" />
									</logic:equal>
									<logic:equal name="element" property="status" value="7">
										<bean:message key="label.hiremanage.status6" />
									</logic:equal>
									<logic:equal name="element" property="status" value="6">
										<bean:message key="jx.khplan.Appraisal" />
										<logic:equal name="element" property="feedback" value="1">
										【结果反馈】
										</logic:equal>
									</logic:equal>
									<logic:equal name="element" property="status" value="8">
										<bean:message key="performance.plan.distribute" />
									</logic:equal>	
									&nbsp;				
								</td>
								<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<hrms:codetoname codeid="UN" name="element"
										codevalue="b0110" codeitem="codeitem_un" scope="page" />
									<hrms:codetoname codeid="UM" name="element"
										codevalue="b0110" codeitem="codeitem_um" scope="page" />
									<logic:notEqual name="element" property="b0110"
										value="HJSJ">
										<bean:write name="codeitem_un" property="codename" />
										<bean:write name="codeitem_um" property="codename" />
									</logic:notEqual>
									<logic:equal name="element" property="b0110"
										value="HJSJ">
										<bean:message key="jx.khplan.hjsj" />
									</logic:equal>
									&nbsp;
								</td>
								<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:equal name="element" property="cycle" value="0">
										<bean:message key="jx.khplan.yeardu" />
									</logic:equal>
									<logic:equal name="element" property="cycle" value="1">
										<bean:message key="jx.khplan.halfyear" />
									</logic:equal>
									<logic:equal name="element" property="cycle" value="2">
										<bean:message key="jx.khplan.quarter" />
									</logic:equal>
									<logic:equal name="element" property="cycle" value="3">
										<bean:message key="jx.khplan.monthdu" />
									</logic:equal>
									<logic:equal name="element" property="cycle" value="7">
										<bean:message key="jx.khplan.indefinetime" />
									</logic:equal>
									&nbsp;
								</td>
								<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:equal name="element" property="cycle" value="0">
										<bean:write name="element" property="theyear"
											filter="true" />
										<bean:message key="datestyle.year" />
									</logic:equal>
									<logic:equal name="element" property="cycle" value="1">
										<logic:equal name="element" property="thequarter"
											value="1">
											<bean:write name="element" property="theyear"
												filter="true" />
											<bean:message key="datestyle.year" />&nbsp;<bean:message
												key="report.pigeonhole.uphalfyear" />
										</logic:equal>
										<logic:equal name="element" property="thequarter"
											value="2">
											<bean:write name="element" property="theyear"
												filter="true" />
											<bean:message key="datestyle.year" />&nbsp;<bean:message
												key="report.pigeonhole.downhalfyear" />
										</logic:equal>
									</logic:equal>
									<logic:equal name="element" property="cycle" value="2">
										<logic:equal name="element" property="thequarter"
											value="01">
											<bean:write name="element" property="theyear"
												filter="true" />
											<bean:message key="datestyle.year" />&nbsp;<bean:message
												key="report.pigionhole.oneQuarter" />
										</logic:equal>
										<logic:equal name="element" property="thequarter"
											value="02">
											<bean:write name="element" property="theyear"
												filter="true" />
											<bean:message key="datestyle.year" />&nbsp;<bean:message
												key="report.pigionhole.twoQuarter" />
										</logic:equal>
										<logic:equal name="element" property="thequarter"
											value="03">
											<bean:write name="element" property="theyear"
												filter="true" />
											<bean:message key="datestyle.year" />&nbsp;<bean:message
												key="report.pigionhole.threeQuarter" />
										</logic:equal>
										<logic:equal name="element" property="thequarter"
											value="04">
											<bean:write name="element" property="theyear"
												filter="true" />
											<bean:message key="datestyle.year" />&nbsp;<bean:message
												key="report.pigionhole.fourQuarter" />
										</logic:equal>
									</logic:equal>
									<logic:equal name="element" property="cycle" value="3">
										<bean:write name="element" property="theyear"
											filter="true" />
										<bean:message key="datestyle.year" />&nbsp;
										<logic:equal name="element" property="themonth" value="01">
											<bean:message key="date.month.january" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="02">
											<bean:message key="date.month.february" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="03">
											<bean:message key="date.month.march" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="04">
											<bean:message key="date.month.april" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="05">
											<bean:message key="date.month.may" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="06">
											<bean:message key="date.month.june" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="07">
											<bean:message key="date.month.july" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="08">
											<bean:message key="date.month.auguest" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="09">
											<bean:message key="date.month.september" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="10">
											<bean:message key="date.month.october" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="11">
											<bean:message key="date.month.november" />
										</logic:equal>
										<logic:equal name="element" property="themonth"
											value="12">
											<bean:message key="date.month.december" />
										</logic:equal>
									</logic:equal>
									<logic:equal name="element" property="cycle" value="7">
										<bean:write name="element" property="start_date" format="yyyy.MM.dd"
											filter="true" /> －<bean:write name="element"
											property="end_date" format="yyyy.MM.dd" filter="true" />
									</logic:equal>
									&nbsp;
								</td>
								<logic:equal name="examPlanForm" property="busitype" value="0">
									<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
										<a href="javascript:getTemplate('<bean:write name="element" property="template_id" filter="true"/>','<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>');"><img
											<logic:equal name="element" property="status"
												value="0">	src="/images/edit.gif" 
											</logic:equal>	
										
											<logic:notEqual name="element" property="status" value="0">												
												
												src="/images/view.gif" 
											
											</logic:notEqual>
												border=0> </a>
									</td>
								</logic:equal>
								<logic:notEqual name="examPlanForm" property="busitype" value="0">
									<td align="center" width="85" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
										<table width='100%'  >
											<tr>
												<td>
													&nbsp;<a href="javascript:getTemplate('<bean:write name="element" property="template_id" filter="true"/>','<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>');"><img
														<logic:equal name="element" property="status"
															value="0">	src="/images/edit.gif" 
														</logic:equal>													
														<logic:notEqual name="element" property="status" value="0">																										
															src="/images/view.gif" 													
														</logic:notEqual>
															border=0> 
													</a>&nbsp;
												</td>
												<td id="per_<%=plan_id%>" >
													&nbsp;<bean:write name="element" property="byModelName" filter="false" />&nbsp;
												</td>
											</tr>										
										</table>																										
									</td>
								</logic:notEqual>
								<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									<a onclick="getPlanParameter('<bean:write name="element" property="plan_id" filter="true"/>',
										'<bean:write name="element" property="object_type" filter="true"/>',
										'<bean:write name="element" property="status" filter="true"/>',
										'<bean:write name="element" property="template_id" filter="true"/>',
										'<bean:write name="element" property="gather_type" filter="true"/>')">
										<img
										<logic:equal name="element" property="status" value="0">	
											src="/images/edit.gif" 
										</logic:equal>	
										<logic:equal name="element" property="status" value="5">	
											src="/images/edit.gif" 
										</logic:equal>	
										
										<%
											LazyDynaBean bean = (LazyDynaBean)pageContext.getAttribute("element");
											String statusType = (String)bean.get("status");
											if((!statusType.equals("0")) && (!statusType.equals("5"))){
										%>
											src="/images/view.gif" 
										<% }%>
										
										<%-- 
										<logic:notEqual name="element" property="status" value="0">												
											<logic:notEqual name="element" property="status" value="5">	
												src="/images/view.gif" 
											</logic:notEqual>	
										</logic:notEqual>
										--%>
										
										BORDER="0" style="cursor:hand;">
										
									</a>
								</td>
								<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:equal name="element" property="gather_type"
										value="1">
										<bean:message key="label.module.jd" />
									</logic:equal>
									<logic:equal name="element" property="gather_type"
										value="0">
										<bean:message key="jx.khplan.internet" />
									</logic:equal>
									<logic:equal name="element" property="gather_type"
										value="2">
										<bean:message key="label.module.internetandjd" />
									</logic:equal>
									&nbsp;
								</td>
								
								<logic:equal name="examPlanForm" property="busitype" value="0">
									<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
										&nbsp;
										<logic:equal name="element" property="object_type"
											value="1">
											<bean:message key="jx.khplan.team" />
										</logic:equal>
										<logic:equal name="element" property="object_type"
											value="2">
											<bean:message key="task.selectobject.personnel" />
										</logic:equal>
										<logic:equal name="element" property="object_type"
											value="3">
										<bean:message key="jx.khplan.unit" />
										</logic:equal>
										<logic:equal name="element" property="object_type"
											value="4">
										<bean:message key="column.sys.dept" />
										</logic:equal>
										&nbsp;
									</td>
								</logic:equal>								
								
								<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:equal name="element" property="plan_type"
										value="0">
										<bean:message key="jx.khplan.norecordname" />
									</logic:equal>
									<logic:equal name="element" property="plan_type"
										value="1">
										<bean:message key="jx.khplan.recordname" />
									</logic:equal>
									&nbsp;
								</td>																
								
								<logic:equal name="examPlanForm" property="busitype" value="0">
									<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<!-- 
										<logic:equal name="element" property="status" value="0">
											<html:select name="element" property="method" size="1"
												styleId="m_${nid}"
												onchange="changeMethod(this.value,'${nid}');">
												<html:option value="1">
													<bean:message key="jx.khplan.khmethod1" />
												</html:option>
												<html:option value="2">
													<bean:message key="jx.khplan.khmethod2" />
												</html:option>
											</html:select>
										 </logic:equal>
										 	<logic:notEqual name="element" property="status"
											value="0">
											<html:select name="element" property="method" size="1"
												disabled="true" styleId="m_${nid}">
												<html:option value="1">
													<bean:message key="jx.khplan.khmethod1" />
												</html:option>
												<html:option value="2">
													<bean:message key="jx.khplan.khmethod2" />
												</html:option>
											</html:select>
										</logic:notEqual>
									 -->
										<logic:equal name="element" property="method" value="1">
											<bean:message key="jx.khplan.khmethod1" />
										</logic:equal>
										<logic:equal name="element" property="method" value="2">
											<bean:message key="jx.khplan.khmethod2" />
										</logic:equal>
										&nbsp;																
									</td>
								</logic:equal>
								<html:hidden name="element" property="method" styleId="m_${nid}"/>
								
								<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:notEqual name="element" property="descript" value="">
										<logic:equal name="element" property="status" value="0">
											<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','descript')">
										</logic:equal>
										<logic:equal name="element" property="status" value="5">
											<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','descript')">
										</logic:equal>
										
										<logic:notEqual name="element" property="status" value="0">
											<logic:notEqual name="element" property="status" value="5">
												<img src="/images/view.gif" BORDER="0" style="cursor:hand;"
													onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','descript')">
											</logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
									&nbsp;
								</td>
								<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:notEqual name="element" property="target" value="">
										<logic:equal name="element" property="status" value="0">
											<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','target')">
										</logic:equal>
										<logic:equal name="element" property="status" value="5">
											<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','target')">
										</logic:equal>
										
										<logic:notEqual name="element" property="status" value="0">
											<logic:notEqual name="element" property="status" value="5">
												<img src="/images/view.gif" BORDER="0" style="cursor:hand;"
													onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','target')">
											</logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
									&nbsp;
								</td>
								<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:notEqual name="element" property="content" value="">
										<logic:equal name="element" property="status" value="0">
											<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','content')">
										</logic:equal>
										<logic:equal name="element" property="status" value="5">
											<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','content')">
										</logic:equal>
										
										<logic:notEqual name="element" property="status" value="0">
											<logic:notEqual name="element" property="status" value="5">
												<img src="/images/view.gif" BORDER="0" style="cursor:hand;"
													onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','content')">
											</logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
									&nbsp;
								</td>
								<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:notEqual name="element" property="flow" value="">
										<logic:equal name="element" property="status" value="0">
											<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','flow')">
										</logic:equal>
										<logic:equal name="element" property="status" value="5">
											<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','flow')">
										</logic:equal>
										
										<logic:notEqual name="element" property="status" value="0">
											<logic:notEqual name="element" property="status" value="5">
												<img src="/images/view.gif" BORDER="0" style="cursor:hand;"
													onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','flow')">
											</logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
									&nbsp;
								</td>
								<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:notEqual name="element" property="result" value="">
										<logic:equal name="element" property="status" value="0">
											<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','result')">
										</logic:equal>
										<logic:equal name="element" property="status" value="5">
											<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
												onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','result')">
										</logic:equal>
										
										<logic:notEqual name="element" property="status" value="0">
											<logic:notEqual name="element" property="status" value="5">
												<img src="/images/view.gif" BORDER="0" style="cursor:hand;"
													onclick="updateBigField('<bean:write name="element" property="plan_id" filter="true"/>','<bean:write name="element" property="status" filter="true"/>','result')">
											</logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
									&nbsp;
								</td>
								<td align="right" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;<bean:write name="element" property="agree_date" filter="true"/>&nbsp;
								</td>
								<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;<bean:write name="element" property="agree_user" filter="true"/>&nbsp;
								</td>
								<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;<bean:write name="element" property="agree_idea" filter="false"/>&nbsp;
								</td>
								<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;
									<logic:equal name="element" property="approve_result"
										value="0">
										<bean:message key="label.nagree" />
									</logic:equal>
									<logic:equal name="element" property="approve_result"
										value="1">
										<bean:message key="label.agree" />
									</logic:equal>
									&nbsp;
								</td>
								<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;<bean:write name="element" property="create_user" filter="true" />&nbsp;
								</td>
								<td align="right" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
									&nbsp;<bean:write name="element" property="create_date" filter="true" />&nbsp;
								</td>
								<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
							
									<input type="text" style="display:none"
									value="<bean:write name='element' property='a0000' filter='true' />"
									onkeypress="event.returnValue=IsDigit(this);"
									onchange="changeA0000(this,'<bean:write name='element' property='plan_id' filter='true' />')"/>	
								
									<logic:notEqual name="element" property="count" value="1">
										&nbsp;<a href="javaScript:moveRecord('<bean:write name="element" property="plan_id" filter="true"/>','up',this)">
										<img src="../../images/up01.gif" width="12" height="17" border=0></a> 
									</logic:notEqual>
									<logic:equal name="element" property="count" value="1">																		
									  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</logic:equal>									
									<%										
										String count = (String)bean.get("count");
										if(Integer.parseInt(count)==dataSize){
									%>
									  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<% }else{%>																				                      
				                      <a href="javaScript:moveRecord('<bean:write name="element" property="plan_id" filter="true"/>','down',this)">
									  <img src="../../images/down01.gif" width="12" height="17" border=0></a> &nbsp;
				                    <% }%>					
								</td>																
							</tr>
						</hrms:extenditerate>
					</table>
				</div>
				<div id="date_panel">
					<select name="date_box" multiple="multiple" size="2"
						style="width: 80px;" onchange="setSelectValue();">
						<option value="0">
							<bean:message key='jx.khplan.zpmode' />
						</option>
						<option value="1">
							<bean:message key='jx.khplan.spmode' />
						</option>
					</select>
				</div>
    </div> 
    <%    
    if(tar.equalsIgnoreCase("hl"))
    {
%>
    <script language='javascript' >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-100)+";width:99%'  >");
	</script>
<%
    }else if(tar.equalsIgnoreCase("hcm")){
    %>
    <script language='javascript' >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-90)+";width:99%'  >");
	</script>
<% 
    }
    %>

	<table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" align="left" class="tdFontcolor">
				第
				<bean:write name="examPlanForm"
					property="setlistform.pagination.current" filter="true" />
				页 共
				<bean:write name="examPlanForm"
					property="setlistform.pagination.count" filter="true" />
				条 共
				<bean:write name="examPlanForm"
					property="setlistform.pagination.pages" filter="true" />
				页
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="examPlanForm"
						property="setlistform.pagination" nameId="setlistform"
						propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>

	<table width="100%">
		<tr>
			<td align="center" style="height:35px">
				<input type='button' class="mybutton" property="b_add" onclick='add()'
					value='<bean:message key="button.insert"/>' />				
					
				<logic:equal name="examPlanForm" property="busitype" value="0">	
					<hrms:priv func_id="3260205">				
						<input type='button' class="mybutton" property="b_delete"
							onclick='delqc()'
							value='<bean:message key="button.delete"/>' />
					</hrms:priv>
					<hrms:priv func_id="3260206">
						<input type='button' class="mybutton" property="b_delhistory"
							onclick='delHistory()'
							value='<bean:message key="performanc.plan.delhistory"/>' />
					</hrms:priv>
					<input type="button"
						value="<bean:message key='jx.khplan.saveas'/>"
						onclick="saveas();" class="mybutton" />
					<hrms:priv func_id="3260201">
						<logic:equal name="examPlanForm" property="model" value="1">
							<input type="button"
								value="<bean:message key='button.appeal'/>"
								onclick='appeal("${examPlanForm.model}");' class="mybutton" />
						</logic:equal>
					</hrms:priv>
					<hrms:priv func_id="3260203">
						<logic:notEqual name="examPlanForm" property="model" value="1">
							<input type="button"
								value="<bean:message key='jx.khplan.zp'/>"
								onclick='appeal("${examPlanForm.model}");' class="mybutton" />
						</logic:notEqual>
					</hrms:priv>
					<logic:equal name="examPlanForm" property="model" value="1">
						<hrms:priv func_id="3260202">
							<input type="button"
								value="<bean:message key='jx.khplan.review'/>"
								onclick="review();" class="mybutton" />
						</hrms:priv>
					</logic:equal>
					<hrms:priv func_id="3260204">
						<input type="button"
							value="<bean:message key='button.issue'/>"
							onclick="issue();" class="mybutton" />
					</hrms:priv>
				</logic:equal>
				
				<!-- // 区分 绩效管理和能力素质 模块的功能授权  -->
				<logic:equal name="examPlanForm" property="busitype" value="1">	
					<hrms:priv func_id="36030105">				
						<input type='button' class="mybutton" property="b_delete"
							onclick='delqc()'
							value='<bean:message key="button.delete"/>' />
					</hrms:priv>
					<hrms:priv func_id="36030106">
						<input type='button' class="mybutton" property="b_delhistory"
							onclick='delHistory()'
							value='<bean:message key="performanc.plan.delhistory"/>' />
					</hrms:priv>
					<input type="button"
						value="<bean:message key='jx.khplan.saveas'/>"
						onclick="saveas();" class="mybutton" />
					<hrms:priv func_id="36030101">
						<logic:equal name="examPlanForm" property="model" value="1">
							<input type="button"
								value="<bean:message key='button.appeal'/>"
								onclick='appeal("${examPlanForm.model}");' class="mybutton" />
						</logic:equal>
					</hrms:priv>
					<hrms:priv func_id="36030103">
						<logic:notEqual name="examPlanForm" property="model" value="1">
							<input type="button"
								value="<bean:message key='jx.khplan.zp'/>"
								onclick='appeal("${examPlanForm.model}");' class="mybutton" />
						</logic:notEqual>
					</hrms:priv>
					<logic:equal name="examPlanForm" property="model" value="1">
						<hrms:priv func_id="36030102">
							<input type="button"
								value="<bean:message key='jx.khplan.review'/>"
								onclick="review();" class="mybutton" />
						</hrms:priv>
					</logic:equal>
					<hrms:priv func_id="36030104">
						<input type="button"
							value="<bean:message key='button.issue'/>"
							onclick="issue();" class="mybutton" />
					</hrms:priv>
				</logic:equal>
				
				
				<input type='button' id='setButton' value='<bean:message key='button.orgmapset'/>'
					onclick="showDateSelectBox(this);"
					onblur="" class="mybutton" />
				
				
				<!--//为了统一风格 修改设置按钮
					<input type="button"  extra="button"   menu="p1" value="<bean:message key='button.orgmapset'/>"  />				
					<hrms:menubar menu="p1" id="menubar1" container="" visible="false">
						 <hrms:menuitem name="p11" label="jx.khplan.zpmode" icon="" url="setMode(0)"  function_id="" checked="《%=zp %>"/>
						 <hrms:menuitem name="p12" label="jx.khplan.spmode" icon="" url="setMode(1)"  function_id="" checked="《%=!zp %>"/> 			
	  				</hrms:menubar>
  				-->
				<!--//为了统一风格 修改返回按钮
					<hrms:tipwizardbutton flag="performance" target="il_body" formname="examPlanForm"/>  
				-->
		<%
				if(userView.getBosflag()!=null&&(userView.getBosflag().equals("hl")||userView.getBosflag().equals("hcm"))){		
		%>			  
				<logic:equal name="examPlanForm" property="returnflag" value="dxt">
								<logic:equal name="examPlanForm" property="busitype" value="0">	
								<hrms:tipwizardbutton flag="performance" target="il_body" formname="examPlanForm"/> 
								</logic:equal>	
								<logic:equal name="examPlanForm" property="busitype" value="1">	
								<hrms:tipwizardbutton flag="capability" target="il_body" formname="examPlanForm"/> 
								</logic:equal>

				</logic:equal>
		<%
			}	
		%>			
			</td>
		</tr>
	</table>
</div>

<script>
	hideElement('date_panel');
		
	var timeInterval = document.getElementById('timeInterval').value;	
	if(timeInterval!='3')
	{
		hideElement('datepnl');
	}
			
	var srcobj = $('setButton');
	var pos=getAbsPosition(srcobj);


    with($('date_panel'))
	{
		style.position="absolute";
    	style.left=pos[0]-1;
		style.top=pos[1]-1+srcobj.offsetHeight;
		style.width=80;
    }

	var currentPlanid = '${examPlanForm.planSelect}';
	var busitype = '${examPlanForm.busitype}';	
	var table = document.getElementById('a_table');	
	if(table.rows.length>1)
	{
	 	var isHavaPlanid=false;
		if(currentPlanid=='')
            myfireEvent(table.rows[1]);
		else
		{
			for(var i=1;i<table.rows.length;i++)
			{
				if(busitype!=null && busitype.length>0 && busitype=='1')
				{
					if(ltrim(rtrim(table.rows[i].cells[1].innerText))==currentPlanid) 
					{
                        myfireEvent(table.rows[i]);
						isHavaPlanid=true;
						break;
					}					
				}else
				{
					if(ltrim(rtrim(table.rows[i].cells[1].innerText))==currentPlanid) 
					{
                        myfireEvent(table.rows[i]);
						isHavaPlanid=true;
						break;
					}
				}
			}	
		}
		if(isHavaPlanid==false)
            myfireEvent(table.rows[1]);
	}

	//设置滚动条的值
	function setScrollTop()
	{
	    var scrollValue='${examPlanForm.scrollValue}';
	    var scrollTopValue = '${examPlanForm.scrollTopValue}';
	    var obj = document.getElementById("tbl-container");//获取div对象
	    obj.scrollLeft=scrollValue;
	    obj.scrollTop = scrollTopValue;
	}
	function cheScrollValue(){
		var obj = document.getElementById("tbl-container");
	    document.getElementById("scrollValue").scrollValue=obj.scrollLeft;
	}
    function myfireEvent(el){
        if (document.createEvent) { // DOM Level 2 standard
            var evt;
            evt = document.createEvent("MouseEvents");
            evt.initEvent("click", true, true);
            el.dispatchEvent(evt);
        } else if (el.fireEvent) { // IE
            el.fireEvent('onclick');
        }
    }
	function EnterPress(e){ //传入 event 
		var e = e || window.event; 
		if(e.keyCode == 13){ 
			search_kh_data(this);
		} 
	} 
			var aa=document.getElementsByTagName("input");
		for(var i=0;i<aa.length;i++){
			if(aa[i].type=="text"){
				aa[i].className="inputtext";
			}
		}
</script>
</html:form>
</body>
