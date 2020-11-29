<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,				 			 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.frame.dao.RecordVo,
				 com.hrms.struts.constant.WebConstant,org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,
				 com.hrms.hjsj.sys.Des" %>
				 
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript" src="/performance/kh_plan/examPlan.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="JavaScript" src="/performance/implement/implement.js"></script>

<style>

.myfixedDiv
{ 
	overflow:auto; 
	width:100%; 
	height:expression(document.body.clientHeight-180);
	BORDER-BOTTOM: #94B6E6 0pt solid;
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
} 
.myfixedDiv2
{ 
	overflow:auto; 
	width:100%;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid ; 
} 
.MyRerdRowP 
{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	margin-top:-1px;
	height:22;
}
.MyListTable 
{
	border:0px solid #C4D8EE;
	border-collapse:collapse; 
	BORDER-BOTTOM: medium none; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    margin-top:-1px;
    margin-left:-1px;
    margin-right:-1px;
}
.myfixedDiv3
{ 
	overflow:auto; 
	width:100%; 
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
    BORDER-LEFT: #C4D8EE 0pt solid; 
    BORDER-RIGHT: #C4D8EE 0pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid ; 
} 
</style>

<hrms:themes />
<script type="text/javascript">

var currentPlanid = '<%=request.getParameter("currentPlanid")%>';

//设置滚动条的值
function setScrollTop()
{
	var scrollValue="${performancePlanForm.scrollValue}";
	var obj = document.getElementById("recordlist");//获取div对象
    //本地没有问题，但是测试哪里始终有问题。所以在此修改。 haosl 2019年6月6日
    var aclientHeight= Ext.getBody().getViewSize().height;
    obj.style.height = (aclientHeight - 100) + 'px';
    obj.scrollTop=scrollValue;
}
function searchPlans()
{
	document.performancePlanForm.action="/performance/kh_plan/performPlanList.do?b_query=query";
	document.performancePlanForm.submit();
}

function jxOper(plan_idz,plan_id)
{
	var busitype = '${performancePlanForm.busitype}';
	var urlz = "&plan_id=" + plan_idz + "&busitype=" + busitype;
	var url = "&plan_id=" + plan_id + "&busitype=" + busitype;
	var obj = document.getElementById("recordlist");
	var scrollValue=obj.scrollTop;
	if('${performancePlanForm.jxmodul}'=='1')
		url = "/performance/implement/performanceImplement.do?b_int=link"+urlz+"&scrollValue="+scrollValue;
	else if('${performancePlanForm.jxmodul}'=='2') {
		var obtype = "";
		if('${performancePlanForm.busitype}'=='0'){
			obtype = document.getElementsByName("object_type")[0].value;
		}
		url = "/performance/evaluation/performanceEvaluation.do?b_int=link"+urlz+ "&obtype=" + obtype;
	}
	else if('${performancePlanForm.jxmodul}'=='3')
		url = "/performance/implement/dataGather.do?b_query=query&fromUrl=1"+url;
	window.location=url;
}
/* 兼容fireEvent方法 */
function myFireEvent(el) {
	var evt;
	if (document.createEvent) {
		evt = document.createEvent("MouseEvents");
		evt.initMouseEvent("click", true, true, window,
				0, 0, 0, 0, 0, false, false, false, false, 0, null);
		el.dispatchEvent(evt);
	} else if (el.fireEvent) { // IE
		el.fireEvent("onclick");
	}
}
</script>

<body onload="setScrollTop();">
<html:form action="/performance/kh_plan/performPlanList">	
		<table border="0" cellspacing="0" align="center" width="80%" style="margin-top:-2px;" cellpadding="0" >
		<tr>		
			<td align="left" nowrap>
				<div class="myfixedDiv3">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
						<tr >
							<td align="left" nowrap height="35" >
								<bean:message key="jx.khplan.spstatus" />
								<html:select name="performancePlanForm" property="status" size='1'	onchange="searchPlans();">
									<html:option value="all">
										<bean:message key="label.all" />
									</html:option>
									<html:optionsCollection property="statusList" value="dataValue" label="dataName" />
								</html:select>
								
								<bean:message key="general.mediainfo.name" />
								<html:text name="performancePlanForm" property="name" onkeypress="EnterPress(event)" onkeydown="EnterPress()" styleClass="inputtext"/>
								
								
								<logic:equal name="performancePlanForm" property="busitype" value="0">
									<logic:notEqual name="performancePlanForm" property="jxmodul" value="3">
										<bean:message key="jx.khplan.khmethod" />
										<html:select name="performancePlanForm" property="method" size="1"	onchange="searchPlans();">
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
									
									</logic:notEqual>
									
									<bean:message key="jx.khplan.objectype" />
									<html:select name="performancePlanForm" property="object_type" size="1"	onchange="searchPlans();">
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
							<!-- 【5005】能力素质：评估实施、数据采集、评估计算页面的查询按钮离的太远了  jingq upd 2014.11.24
							</td>
							<td valign="middle" align="left"> -->	
								<input type="button" onclick="searchPlans();" Class="mybutton"
										value="<bean:message key="button.query"/>"  style="margin-left:5px;"> 
							<!-- </td>
							<td align="left" width="50%"> -->
								<logic:equal name="performancePlanForm" property="jxmodul" value="1">	
									<logic:equal name="performancePlanForm" property="busitype" value="0">
										<hrms:priv func_id="326030132">																	
											<input type="button" onclick="distributeORstart('distribute','no');" Class="mybutton"
													value="<bean:message key="performance.plan.batchDistribute"/>" style="margin-left:-3px;"> 	
										</hrms:priv>
										<hrms:priv func_id="326030133">		
											<input type='button' id='setButton' value='<bean:message key='performance.plan.batchStart'/>'
													onclick="showSelectBox(this);"  style="margin-left:-3px;"
													 class="mybutton" />
													
													<%--
													onblur="Element.hide('date_panel');Element.hide('date_panel2');" class="mybutton" />
													--%>
										</hrms:priv>		
									<%--	
										<input type="button" onclick="distributeORstart('start','no');" Class="mybutton"
												value="<bean:message key="performance.implement.start"/>"> 
									--%>
									</logic:equal>	
								</logic:equal>	
								<logic:equal name="performancePlanForm" property="busitype" value="0">	
								<hrms:tipwizardbutton flag="performance" target="il_body" formname="performancePlanForm"/> 
								</logic:equal>	
								<logic:equal name="performancePlanForm" property="busitype" value="1">	
								<hrms:tipwizardbutton flag="capability" target="il_body" formname="performancePlanForm"/> 
								</logic:equal>	
							</td>
						</tr>
					</table>
				</div>
			</td>			
		</tr>
		<tr>
			<td  nowrap >
				<div id="recordlist" class="myfixedDiv common_border_color" style="margin-bottom:-1px;margin-top:-1px">
					<table width="100%" border="0" cellspacing="0" align="center" id='a_table' 
						cellpadding="0"  class="ListTable" style="margin-top: -1px;">
						<%
						int i = 0;
						%>

						<tr class="fixedHeaderTr1" style="top:0px">	
							<logic:equal name="performancePlanForm" property="jxmodul" value="1">					
								<td align="center" class="TableRow_right common_background_color common_border_color" nowrap>	
								<%--
									<input type="checkbox" name="check" id='checkAll' value="1" onclick='selectAll();validaPlan();' />	
								--%>									
									<input type="checkbox" name="selbox" onclick="batch_select(this, 'setlistform.select');">	
																				
								</td>
							</logic:equal>	
							
							<td align="center" class="TableRow_right common_background_color common_border_color" nowrap>
								<bean:message key="column.sys.status" />
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="lable.zp_plan.plan_id" />
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="column.name" />
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="jx.khplan.cycle" />
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="jx.khplan.khtimeqj" />
							</td>
							
							<logic:equal name="performancePlanForm" property="busitype" value="0">
								<td align="center" class="TableRow" nowrap>
									<bean:message key="jx.khplan.objectype" />
								</td>
								<logic:notEqual name="performancePlanForm" property="jxmodul" value="3">
									<td align="center" class="TableRow" nowrap>
										<bean:message key="jx.khplan.khmethod" />
									</td>
								</logic:notEqual>
							</logic:equal>
							
							<td align="center" class="TableRow_left common_background_color common_border_color" nowrap>
								<bean:message key="label.gz.operation" />
							</td>
						</tr>
						<hrms:extenditerate id="element" name="performancePlanForm"
							property="setlistform.list" indexes="indexes"
							pagination="setlistform.pagination" pageCount="25"
							scope="session">
							<bean:define id="nid" name="element" property="string(plan_id)" />
							<bean:define id="status" name="element" property="string(status)" />
							<%
									if (i % 2 == 0)
									{
							%>
							<tr class="trShallow"
								onclick='tr_onclick(this,"#F3F5FC");'>
								<%
										} else
										{
								%>
							
							<tr class="trDeep"
								onclick='tr_onclick(this,"#E4F2FC");'>
								<%
										}
										i++;
								%>
								
								<logic:equal name="performancePlanForm" property="jxmodul" value="1">
									<td align="center" style="border-top:0px;" class="RecordRow_right" nowrap>
									<%--
										<input name="targetCalcItemt" type="checkbox" onclick='validaPlan();' 
											value="<bean:write name="element" property="string(plan_id)" filter="true"/>"/>	
									--%>																																			
										<Input type='hidden' id="${nid}" />
										<hrms:checkmultibox name="performancePlanForm"
											property="setlistform.select" value="true" indexes="indexes" />											
										<Input type='hidden' value='<bean:write name="element" property="string(plan_id)" filter="true"/>' />
										<Input type='hidden' value='<bean:write name="element" property="string(method)" filter="true"/>' />
																										
									</td>
								</logic:equal>	
								
								<td align="left" style="border-top:0px;" class="RecordRow_right" nowrap>
										<bean:write name="element" property="string(status)" filter="true" /> 								
								</td>
								<td align="center" style="border-top:0px;" class="RecordRow" nowrap>
										<bean:write name="element" property="string(plan_id)" filter="true" /> 								
								</td>
								<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
										<bean:write name="element" property="string(name)" filter="false" /> 								
								</td>
								<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
									<logic:equal name="element" property="string(cycle)" value="0">
										<bean:message key="jx.khplan.yeardu" />
									</logic:equal>
									<logic:equal name="element" property="string(cycle)" value="1">
										<bean:message key="jx.khplan.halfyear" />
									</logic:equal>
									<logic:equal name="element" property="string(cycle)" value="2">
										<bean:message key="jx.khplan.quarter" />
									</logic:equal>
									<logic:equal name="element" property="string(cycle)" value="3">
										<bean:message key="jx.khplan.monthdu" />
									</logic:equal>
									<logic:equal name="element" property="string(cycle)" value="7">
										<bean:message key="jx.khplan.indefinetime" />
									</logic:equal>
								</td>
								<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
									<logic:equal name="element" property="string(cycle)" value="0">
										<bean:write name="element" property="string(theyear)"
											filter="true" />
										<bean:message key="datestyle.year" />
									</logic:equal>
									<logic:equal name="element" property="string(cycle)" value="1">
										<logic:equal name="element" property="string(thequarter)"
											value="1">
											<bean:write name="element" property="string(theyear)"
												filter="true" />
											<bean:message key="datestyle.year" /><bean:message
												key="report.pigeonhole.uphalfyear" />
										</logic:equal>
										<logic:equal name="element" property="string(thequarter)"
											value="2">
											<bean:write name="element" property="string(theyear)"
												filter="true" />
											<bean:message key="datestyle.year" /><bean:message
												key="report.pigeonhole.downhalfyear" />
										</logic:equal>
									</logic:equal>
									<logic:equal name="element" property="string(cycle)" value="2">
										<logic:equal name="element" property="string(thequarter)"
											value="01">
											<bean:write name="element" property="string(theyear)"
												filter="true" />
											<bean:message key="datestyle.year" /><bean:message
												key="report.pigionhole.oneQuarter" />
										</logic:equal>
										<logic:equal name="element" property="string(thequarter)"
											value="02">
											<bean:write name="element" property="string(theyear)"
												filter="true" />
											<bean:message key="datestyle.year" /><bean:message
												key="report.pigionhole.twoQuarter" />
										</logic:equal>
										<logic:equal name="element" property="string(thequarter)"
											value="03">
											<bean:write name="element" property="string(theyear)"
												filter="true" />
											<bean:message key="datestyle.year" /><bean:message
												key="report.pigionhole.threeQuarter" />
										</logic:equal>
										<logic:equal name="element" property="string(thequarter)"
											value="04">
											<bean:write name="element" property="string(theyear)"
												filter="true" />
											<bean:message key="datestyle.year" /><bean:message
												key="report.pigionhole.fourQuarter" />
										</logic:equal>
									</logic:equal>
									<logic:equal name="element" property="string(cycle)" value="3">
										<bean:write name="element" property="string(theyear)"
											filter="true" />
										<bean:message key="datestyle.year" />
										<logic:equal name="element" property="string(themonth)" value="01">
											<bean:message key="date.month.january" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="02">
											<bean:message key="date.month.february" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="03">
											<bean:message key="date.month.march" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="04">
											<bean:message key="date.month.april" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="05">
											<bean:message key="date.month.may" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="06">
											<bean:message key="date.month.june" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="07">
											<bean:message key="date.month.july" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="08">
											<bean:message key="date.month.auguest" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="09">
											<bean:message key="date.month.september" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="10">
											<bean:message key="date.month.october" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="11">
											<bean:message key="date.month.november" />
										</logic:equal>
										<logic:equal name="element" property="string(themonth)"
											value="12">
											<bean:message key="date.month.december" />
										</logic:equal>
									</logic:equal>
									<logic:equal name="element" property="string(cycle)" value="7">
										<bean:write name="element" property="date(start_date)" format="yyyy.MM.dd"
											filter="true" /> －<bean:write name="element"
											property="date(end_date)" format="yyyy.MM.dd" filter="true" />
									</logic:equal>
								</td>
																
								<logic:equal name="performancePlanForm" property="busitype" value="0">
									<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
										<logic:equal name="element" property="string(object_type)"
											value="1">
											<bean:message key="jx.khplan.team" />
										</logic:equal>
										<logic:equal name="element" property="string(object_type)"
											value="2">
											<bean:message key="task.selectobject.personnel" />
										</logic:equal>
										<logic:equal name="element" property="string(object_type)"
											value="3">
										<bean:message key="jx.khplan.unit" />
										</logic:equal>
										<logic:equal name="element" property="string(object_type)"
											value="4">
										<bean:message key="column.sys.dept" />
										</logic:equal>
									</td>
									
									<logic:notEqual name="performancePlanForm" property="jxmodul" value="3">								
										<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
											<logic:equal name="element" property="string(method)" value="1">
												<bean:message key="jx.khplan.khmethod1" />
											</logic:equal>
											<logic:equal name="element" property="string(method)" value="2">
												<bean:message key="jx.khplan.khmethod2" />
											</logic:equal>						
										</td>
									</logic:notEqual>
								</logic:equal>								
								<%
											RecordVo abean=(RecordVo)pageContext.getAttribute("element");
											String plan_id = (String)abean.getString("plan_id");		
											plan_id=PubFunc.encryption(plan_id);
								%>
								<td align="center" style="border-top:0px;" class="RecordRow_left" nowrap>
									<img BORDER="0" style="cursor:hand;"
										onclick="jxOper('<%=plan_id%>','<bean:write name="element" property="string(plan_id)" filter="true"/>')"
																								
										<logic:equal name="element" property="string(status)" value="结束">	
											src="/images/view.gif" 
										</logic:equal>																								
										<%
											RecordVo vo = (RecordVo)pageContext.getAttribute("element");
											String statusType = (String)vo.getString("status");
											if((!statusType.equals("edit")) && (!statusType.equals("5"))){
										%>
											src="/images/edit.gif" 
										<% }%>	
										
										<%--
										<logic:notEqual name="element" property="string(status)" value="edit">												
											<logic:notEqual name="element" property="string(status)" value="5">	
											src="/images/edit.gif" 
										</logic:notEqual>	
										</logic:notEqual>
										--%>																																																>
								</td>
								</tr>
						</hrms:extenditerate>
					</table>
				</div>
				<div id="date_panel">
					<select name="date_box" multiple="multiple" size="2"
						style="width:110" onchange="setSelectOptionValue('2');">
						<option value="0">
							<bean:message key='performance.implement.startScore' />
						</option>
						<option value="1">
							<bean:message key='performance.implement.startResult' />
						</option>
					</select>
				</div>
				
				<%--
				<div id="date_panel2">
					<select name="date_box2" multiple="multiple" size="1"
						style="width:110" onchange="setSelectOptionValue('1');">
						<option value="0">
							<bean:message key='performance.implement.startScore' />
						</option>												
					</select>
				</div>
				--%>
				
				<div class="myfixedDiv2 common_border_color">
					<table width="100%"	class="MyRerdRowP">
						<tr>
							<td valign="bottom" style="line-height:40px;" align="left" class="tdFontcolor">
								第
								<bean:write name="performancePlanForm"
									property="setlistform.pagination.current" filter="true" />
								页 共
								<bean:write name="performancePlanForm"
									property="setlistform.pagination.count" filter="true" />
								条 共
								<bean:write name="performancePlanForm"
									property="setlistform.pagination.pages" filter="true" />
								页
							</td>
							<td align="right" nowrap class="tdFontcolor">
								<p align="right">
									<hrms:paginationlink name="performancePlanForm"
										property="setlistform.pagination" nameId="setlistform"
										propertyId="roleListProperty">
									</hrms:paginationlink>
							</td>
						</tr>
					</table>				
				</div>
			</td>
		</tr>
	</table>
	
</html:form>
</body>
<script>
	Element.hide('date_panel');
//	Element.hide('date_panel2');
	var srcobj = $('setButton');
	var pos=getAbsPosition(srcobj);
	with($('date_panel'))
	{
		style.position="absolute";
    	style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=110;
    }


	var table = document.getElementById('a_table');	
	if(table.rows.length>1)
	{
		if(currentPlanid=='null')
			// table.rows[1].fireEvent("onclick");
			myFireEvent(table.rows[1]);
		else
		{
			for(var i=1;i<table.rows.length;i++)
			{
				if('${performancePlanForm.jxmodul}'=='1')
				{
					if(ltrim(rtrim(table.rows[i].cells[2].innerText))==currentPlanid) 
					{
						// table.rows[i].fireEvent("onclick");
						myFireEvent(table.rows[i]);
						break;
					}
				}else
				{
					if(ltrim(rtrim(table.rows[i].cells[1].innerText))==currentPlanid) 
					{
						// table.rows[i].fireEvent("onclick");
						myFireEvent(table.rows[i]);
						break;
					}
				}
			}	
		}			
	}
	function EnterPress(e){ //传入 event 
		var e = e || window.event; 
		if(e.keyCode == 13){ 
			searchPlans();
		} 
		return false;
	} 
</script>
