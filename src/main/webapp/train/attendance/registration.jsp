<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@page import="com.hjsj.hrms.utils.OperateDate"%>
<%@page import="java.util.Date"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style>
<!--
.divStyle {
	overflow: auto;
	height: expression(document .     body .     clientHeight-160);
	width: expression(document .     body .     clientWidth-20);
	border-left:1px solid;
	border-right: 1px solid; 
	border-top: 1px solid;
}

.divStyle1 {
	width: expression(document .     body .     clientWidth-20);
	border: 1px solid;
}
-->
</style>
<script type="text/javascript">
<!--
var dh=0;
if(navigator.appVersion.indexOf('MSIE 6') != -1){
	dh=50;
}
function loadclass(){

		var hashvo=new ParameterSet(); 
		hashvo.setValue("classplan",document.getElementById("classplan").value);
		hashvo.setValue("flag","1"); 
    	var request=new Request({method:'post',onSuccess:showSelectOk,functionId:'2020020234'},hashvo);
}
function showSelectOk(outparamters){
	if(outparamters){
		var csp=document.getElementById("courseplan");
		csp.options.length = 0;
		var value1=outparamters.getValue("value");
		var text1=outparamters.getValue("text");
		if(value1!=null&&value1.length>0){
			var val1s=value1.split(",");
			var txs=text1.split(",");
			for(var i=0;i<val1s.length;i++){
				var varItem = new Option(txs[i],val1s[i]);
				csp.options.add(varItem);
				if(val1s[i]=="${trainAtteForm.courseplan}")
					csp.options[i].selected=true;
			}
		}
	}
}
function changeOk(){
	var courseplan = document.getElementById("courseplan").value;
	var emp_name = document.getElementById("emp_name").value;
	trainAtteForm.action="/train/attendance/registration.do?b_query=link&courseplan="+courseplan+"&emp_name="+emp_name;
	trainAtteForm.submit();
}
function changeOk1(){
	var courseplan = document.getElementById("courseplan").value;
	if(document.getElementById("query").value!="1"){
		var emp_name = document.getElementById("emp_name").value;
		trainAtteForm.action="/train/attendance/registration.do?b_query=link&courseplan="+courseplan+"&emp_name="+emp_name+"&query=1";
		trainAtteForm.submit();
	}
}
<!--  签到（退）  -->
function Reg(flag){
	var classplan = document.getElementById("classplan").value;
	var courseplan = document.getElementById("courseplan").value;
	if(courseplan != "" && courseplan.length > 0){
    	var target_url="/train/attendance/registration.do?b_view=link&classplan="+classplan+"&courseplan="+courseplan+"&flag="+flag;
    	//var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    	var layOut = "dialogWidth:350px;dialogHeight:" + (340+dh) + "px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes";
    	var vo=window.showModalDialog(target_url,classplan,layOut);
    	changeOk();
    }else{
    	alert("没有培训课程！");
    }
}
<!--  补签到（退）  -->
function Ret(){
	var classplan = document.getElementById("classplan").value;
	var courseplan = document.getElementById("courseplan").value;
	if(courseplan != "" && courseplan.length > 0){
    	var target_url="/train/attendance/pageregistration.do?b_ret=link`classplan="+classplan+"`courseplan="+courseplan;
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    	var layOut = "dialogWidth:500px;dialogHeight:" + (500+dh) + "px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes";
    	var vo=window.showModalDialog(iframe_url,1,layOut);
    	changeOk();
    }else{
    	alert("没有培训课程！");
    }
}

function delEmp(){
	var flag=false;
	var len=document.trainAtteForm.elements.length;
	for (var i = 0;i < len;i++){
		if (document.trainAtteForm.elements[i].type == "checkbox"
			&& document.trainAtteForm.elements[i].id != "all"){
			if(document.trainAtteForm.elements[i].checked == true){
				flag=true;
				i=len;
			}
		}
	}
	if(flag){
		if(confirm("确定要删除选择的记录！")){
			trainAtteForm.action="/train/attendance/registration.do?b_del=link";
			trainAtteForm.submit();
		}
	}else{
		alert("请选择要删除的刷卡记录！");
	}
}

function selAll(){
	var flag = document.getElementById("all").checked;
	var len=document.trainAtteForm.elements.length;
	for (var i = 0;i < len;i++){
		if (document.trainAtteForm.elements[i].type == "checkbox"){
			document.trainAtteForm.elements[i].checked = flag;
		}
	}
}

function trainsearch(){
	var r4101=document.getElementById("courseplan").value;
	var thecodeurl ="/train/attendance/trainsearch.do?b_search=link&t_type=tr_cradtime&r4101="+r4101;
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:" + (360+dh) + "px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null&&return_vo.length>0){
    	document.getElementById("search").value=return_vo;
    	changeOk();//提交方法
    }
}
function exportExcel(){
	var courseplan = document.getElementById("courseplan").value;
	if(courseplan != "" && courseplan.length > 0){
		var hashvo=new ParameterSet();
		hashvo.setValue("classplan",document.getElementById("classplan").value);
	   	var request=new Request({method:'post',onSuccess:showExportInfo,functionId:'2020020245'},hashvo);
	}else{
		alert("没有培训课程！");
	}
}
function showExportInfo(outparamters){
	if(outparamters){
		var name=outparamters.getValue("filename");
		window.location.target="_blank";
		window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+name;
	}
}
//-->
</script>
<hrms:themes />
<html:form action="/train/attendance/registration">
	<input type="hidden" name="query" value="${trainAtteForm.query }"/>
	<input type="hidden" name="search" value="" />
	<table cellspacing="0" cellpadding="0" border="0"
		style="border-collapse: collapse;">
		<tr>
			<td>
				&nbsp;
				<!-- 培训班: -->
				<bean:message key="sys.res.trainjob"></bean:message>
				<hrms:optioncollection name="trainAtteForm" property="classplanlist"
					collection="list" /><span style="vertical-align: middle;">
				<html:select name="trainAtteForm" property="classplan"
					onchange="loadclass();changeOk();" styleId="classplan">
					<html:options collection="list" property="dataValue"
						labelProperty="dataName" />
				</html:select></span>
				<!-- 培训课程: -->
				<bean:message key="train.course.name"></bean:message><span style="vertical-align: middle;">
				<html:select name="trainAtteForm" property="courseplan"
					onchange="changeOk();" styleId="courseplan"></html:select></span>
				&nbsp;
				<!-- 签到（退）
				<html:select name="trainAtteForm" property="courseplan"
					onchange="changeOk();"></html:select>
					 -->
				<bean:message key="train.b_plan.reg.type"></bean:message><span style="vertical-align: middle;">
				<html:select name="trainAtteForm" property="regType"
					onchange="changeOk();">
					<html:option value="0">全部</html:option>
					<html:option value="1">签到</html:option>
					<html:option value="2">签退</html:option>
				</html:select></span>
				&nbsp;
				<!-- 姓名：-->
				<bean:message key="hire.employActualize.name"></bean:message>
				<html:text name="trainAtteForm" property="emp_name" size="8" styleClass="text4"></html:text>
				&nbsp;<span style="vertical-align: middle;">
				<input type="button" name="query" value='查询' class="mybutton"
					onclick="changeOk();" />
				<div style="height:3px;overflow: hidden;width: 10px;border-width: 0px;"></div></span>
			</td>
		</tr>
		<tr>
			<td style="border: 1px solid ;padding-left: 0px;">
				<div class="divStyle common_border_color">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" style="border-collapse: collapse;">
						<thead>
							<tr>
								<td class="TableRow" align="center" nowrap style="border-left: 0px;border-top: none;">
									<input type="checkbox" id="all" value="true" onclick="selAll()" />
								</td>
								<td class="TableRow" align="center" style="border-top: none;" nowrap>
									<bean:message key="b0110.label"/>
									<!-- 单位名称 -->
								</td>
								<td class="TableRow" align="center" style="border-top: none;" nowrap>
									<bean:message key="e0122.label"/>
									<!-- 部门名称 -->
								</td>
								<td class="TableRow" align="center" style="border-top: none;" nowrap>
									<bean:message key="label.title.name"/>
									<!-- 姓名 -->
								</td>
								<td class="TableRow" align="center" style="border-top: none;" nowrap>
									<bean:message key="kq.card.card_no"/>
									<!-- 卡号 -->
								</td>
								<td class="TableRow" align="center" style="border-top: none;" nowrap>
									<bean:message key="kq.card.work_date"/>
									<!-- 日期 -->
								</td>
								<td class="TableRow" align="center" style="border-top: none;" nowrap>
									<bean:message key="kq.card.work_time"/>
									<!-- 时间 -->
								</td>
								<td class="TableRow" align="center" style="border-top: none;" nowrap>
									<bean:message key="reporttypelist.sort"/>
									<!-- 类别 -->
								</td>
								<td class="TableRow" align="center" style="border-top: none;" style="border-right: 0px;" nowrap>
									<bean:message key="kq.card.status"/>
									<!-- 状态 -->
								</td>
							</tr>
						</thead>
						<hrms:paginationdb id="element"
							pagerows="${trainAtteForm.pagerows}" name="trainAtteForm"
							sql_str="trainAtteForm.sql_str" table=""
							where_str="trainAtteForm.cond_str"
							order_by="trainAtteForm.order_str"
							columns="trainAtteForm.columns" page_id="pagination"
							indexes="indexes">
							<tr>
								<td class="RecordRow" align="center" style="border-left: 0px;">
									<hrms:checkmultibox name="trainAtteForm"
										property="pagination.select" value="true" nameId="1"
										propertyId="1" indexes="indexes" />
								</td>
								<td class="RecordRow">
									&nbsp;
									<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="b0110" scope="page" />
									<bean:write name="b0110" property="codename" />
									&nbsp;
								</td>
								<td class="RecordRow">
									&nbsp;
									<hrms:codetoname codeid="UM" name="element" codevalue="e0122"
										codeitem="e0122" scope="page" uplevel="${trainAtteForm.uplevel}" />
									<bean:write name="e0122" property="codename" />
									&nbsp;
								</td>
								<td class="RecordRow">
									&nbsp;
									<bean:write name="element" property="a0101" filter="true" />
									&nbsp;
								</td>
								<td class="RecordRow">
									&nbsp;
									<bean:write name="element" property="${trainAtteForm.card_no}" filter="true" />
									&nbsp;
								</td>
								<td align="center" class="RecordRow">
									&nbsp;
									<%
										LazyDynaBean bean = (LazyDynaBean) element;
													String date = (String) bean.get("card_time");
													Date re_date = OperateDate.strToDate(date, "yyyy-MM-dd HH:mm:ss");
													out.print(OperateDate.dateToStr(re_date, "yyyy-MM-dd"));
									%>
									&nbsp;
								</td>
								<td align="center" class="RecordRow">
									&nbsp;
									<%
										out.print(OperateDate.dateToStr(re_date, "HH:mm"));
									%>
									&nbsp;
								</td>
								<td align="center" class="RecordRow">
									&nbsp;
									<logic:equal name="element" property="card_type" value="1">
										<!-- 签到 -->
										<bean:message key="train.b_plan.reg.on" />
									</logic:equal>
									<logic:equal name="element" property="card_type" value="2">
										<!-- 签退 -->
										<bean:message key="train.b_plan.reg.off" />
									</logic:equal>
									<logic:equal name="element" property="card_type" value="3">
										<!-- 补签到 -->
										<bean:message key="train.b_plan.ret.on" />
									</logic:equal>
									<logic:equal name="element" property="card_type" value="4">
										<!-- 补签退 -->
										<bean:message key="train.b_plan.ret.off" />
									</logic:equal>
									&nbsp;
								</td>
								<td align="center" class="RecordRow" style="border-right: 0px;">
									<bean:define id="leave" name="element" property="leave_early" />
									<bean:define id="late" name="element" property="late_for" />
									<%
										TrainAtteBo bo = new TrainAtteBo();
													late = late == null || "".equals(late) ? "0" : late;
													leave = leave == null || "".equals(leave) ? "0" : leave;
													double iLate = Double.parseDouble(late.toString());
													double iLeave = Double.parseDouble(leave.toString());
													String state = bo.getMsgBy((int) iLate, (int) iLeave);
													if ("0".equals(state)) {
														out.println("正常");
													} else if ("1".equals(state)) {
														out.println("迟到");
													} else if ("2".equals(state)) {
														out.println("早退");
													}
									%>
								</td>
							</tr>
						</hrms:paginationdb>
					</table>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="divStyle1 common_border_color" style="margin-left: 1px;">
					<table width="100%" align="center">
						<tr>
							<td valign="bottom" class="tdFontcolor">
								<hrms:paginationtag name="trainAtteForm"
									pagerows="${trainAtteForm.pagerows}" property="pagination"
									scope="page" refresh="true"></hrms:paginationtag>
							</td>
							<td align="right" nowrap class="tdFontcolor">
								<hrms:paginationdblink name="trainAtteForm"
									property="pagination" nameId="trainAtteForm" scope="page">
								</hrms:paginationdblink>
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
		<tr>
			<td align="left" style="padding-top: 5px;">
			  <hrms:priv func_id="323320200"> 
				<input type="button" name="b_add" value='签到' onclick="Reg('1');"
					class="mybutton" />
				<input type="button" name="b_add" value='签退' onclick="Reg('2');"
					class="mybutton" />
			  </hrms:priv>
			  <hrms:priv func_id="323320201"> 
				<input type="button" name="b_add" value='补签' onclick="Ret();"
					class="mybutton" />
			  </hrms:priv>
			  <hrms:priv func_id="323320202"> 
				<input type="button" name="b_del" value='删除' class="mybutton"  onclick='delEmp();'/>
			  </hrms:priv>
			  <hrms:priv func_id="323320203"> 
				<input type="button" name="b_excel" value='导出Excel' class="mybutton"
					onclick="exportExcel()" />
			  </hrms:priv>
			  <hrms:priv func_id="323320204"> 
				<input type="button" name="b_excel" value='条件查询' class="mybutton"
					onclick="trainsearch();" />
			  </hrms:priv>
			</td>
		</tr>
	</table>
</html:form>
<script>loadclass();changeOk1();</script>