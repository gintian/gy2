<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.report.edit_report.EditReportAnalyseForm" %>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<body onResize="change()" style="overflow:auto;" >
<script language="JavaScript" >

	function change(){
		var is = new Is();
		var available_width = 750;
		var available_height = 120; 
		
		var image_width = 750;
		var image_height = 120;
		
	    if(is.ns4||is.ns6) {
	        available_width=innerWidth;
	        available_height=innerHeight;
	    } else if(is.ie4||is.ie5||is.ieX) {
	        available_width=document.body.clientWidth;
	        available_height=document.body.clientHeight;
	    }
	    if(is.ie4 ||is.ie5||is.ieX||is.ns6|| is.ns4) {
	    	
	    	available_width = available_width*0.9*0.5;
	    	available_height = available_height*0.9*0.5;
	    	
	    	available_width = ""+available_width;
	    	available_height =""+available_height;
	    	
	    	if(available_width.indexOf('.')!=-1){
	    		available_width = available_width.substring(0,available_width.indexOf('.'));
	    	}
	    	if(available_height.indexOf('.')!=-1){
	    		available_height = available_height.substring(0,available_height.indexOf('.'));
	    	}
				
			image_width = (available_width);
			image_height = (available_height);

		}
		
		var wid = image_width;
		var hei = image_height;
		//页面重定向
		parent.ril_body2.location.href ="/report/edit_report/reportdisplay.do?b_changeFrame2=link&w="+wid+"&h="+hei;
	}
	
	function Is() {
	    var agent   = navigator.userAgent.toLowerCase();
	    this.major  = parseInt(navigator.appVersion);
	    this.minor  = parseFloat(navigator.appVersion);
	    this.ns     = ((agent.indexOf('mozilla')   != -1) &&
	                  (agent.indexOf('spoofer')    == -1) &&
	                  (agent.indexOf('compatible') == -1) &&
	                  (agent.indexOf('opera')      == -1) &&
	                  (agent.indexOf('webtv')      == -1));
	    this.ns2    = (this.ns && (this.major      ==  2));
	    this.ns3    = (this.ns && (this.major      ==  3));
	    this.ns4    = (this.ns && (this.major      ==  4));
	    this.ns6    = (this.ns && (this.major      >=  5));
	    this.ie     = (agent.indexOf("msie")       != -1);
	    this.ie3    = (this.ie && (this.major      <   4));
	    this.ie4    = (this.ie && (this.major      ==  4) &&
	                  (agent.indexOf("msie 5.0")   == -1));
	    this.ie5    = (this.ie && (this.major      ==  4) &&
	                  (agent.indexOf("msie 5.0")   != -1));
	    this.ieX    = (this.ie && !this.ie3 && !this.ie4);
	}
	
	function setShowFlag()
{
		parent.ril_body1.resetSelectGrid(document.editReportAnalyseForm.showFlag.value);
		if(document.editReportAnalyseForm.showFlag.value=="2"){
		var thecodeurl ="/report/edit_report/reportanalyse.do?br_selectSatement=init"; 
	 	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
		var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:"+screen.width/2+"px; dialogHeight:"+screen.height/2+"px;resizable:yes;center:yes;scroll:yes;status:no");
		if(return_vo){
		document.editReportAnalyseForm.scopeids.value = return_vo;
		parent.ril_body1.changeGrid(1,1);
		}else{
		document.editReportAnalyseForm.scopeids.value="";
		//parent.ril_body1.changeGrid(1,1);
		}
		}else{
		parent.ril_body1.changeGrid(1,1);
		}
		//document.editReportAnalyseForm.action="/report/edit_report/reportanalyse.do?b_setShowflag="+document.reportAnalyseForm.showFlag.value;
		//document.editReportAnalyseForm.submit();
}
function setChartType()
{
		var code =parent.ril_body1.document.ra.codeFlag.value;
		document.editReportAnalyseForm.action="/report/edit_report/reportdisplay.do?b_changeGrid2=link&code="+parent.ril_body1.document.ra.codeFlag.value+"&tabid="+parent.ril_body1.document.ra.reportTabid.value+"&rc=<%=(request.getParameter("rc"))%>&scopeid="+parent.ril_body1.document.ra.scopeid2.value;
		document.editReportAnalyseForm.submit();
}
</script>
<form name="editReportAnalyseForm" method="post" action="/report/edit_report/reportanalyse.do">
<%
	EditReportAnalyseForm editReportAnalyseForm=(EditReportAnalyseForm)session.getAttribute("editReportAnalyseForm");
	String width=""+Integer.parseInt(editReportAnalyseForm.getChartWidth())*1.1;
	if(width.indexOf(".")!=-1)
	width = width.substring(0,width.indexOf("."));
	
 %>
<input type="hidden" name="scopeids"
							value="<bean:write name="editReportAnalyseForm" property="scopeids" filter="true" />">
<logic:notEqual name="editReportAnalyseForm" property="chartFlag" value="no">
	<table align="left" width="50%" height="90%">
	<logic:notEqual name="editReportAnalyseForm" property="use_scope_cond2" value="1" >
	<tr>
	<td align='right'width="<%=width %>" >
		<bean:message key="report_collect.analyseFashion" />:<select name='showFlag' onchange='setShowFlag()' >
		<option value='1'  <logic:equal name="editReportAnalyseForm" property="showFlag" value="1">selected</logic:equal>  ><bean:message key="report_collect.analyseByTime" /></option>
		<option value='2'  <logic:equal name="editReportAnalyseForm" property="showFlag" value="2">selected</logic:equal> >按统计口径分析</option>
	</select>
	&nbsp;&nbsp;
	<bean:message key="report_collect.chartType" />:
	<select name='char_type' onchange='setChartType()'  >
		<option value='1'  <logic:equal name="editReportAnalyseForm" property="char_type" value="1">selected</logic:equal>  ><bean:message key="lable.performance.histogram" /></option>
		<option value='2'  <logic:equal name="editReportAnalyseForm" property="char_type" value="2">selected</logic:equal> ><bean:message key="lable.performance.graph" /></option>
	</select>
	</td>
	
	</tr>
	</logic:notEqual>
	<tr>
	<td>
	<div id='wait' style='overflow:hidden;overflow-y:scroll'>
<table align="left" width="50%" height="100%">
	<tr>
		<td align="left" nowrap colspan="5">
			
		<logic:equal  name="editReportAnalyseForm" property="char_type"  value="1">
			<hrms:chart name="editReportAnalyseForm" title="${editReportAnalyseForm.chartTitle}" 
			scope="session" numDecimals="1" legends="list" data="" width="${editReportAnalyseForm.chartWidth}" height="${editReportAnalyseForm.chartHeight}" 
			chart_type="29"
			 isneedsum="false" >
			</hrms:chart>
		</logic:equal>
		<logic:equal  name="editReportAnalyseForm" property="char_type"  value="2">
			<hrms:chart name="editReportAnalyseForm" title="${editReportAnalyseForm.chartTitle}" 
			scope="session" numDecimals="2" legends="dataMap" data="" width="${editReportAnalyseForm.chartWidth}" height="${editReportAnalyseForm.chartHeight}" 
			chart_type="11"
			 isneedsum="false" >
			</hrms:chart>
		</logic:equal>	
		</td>
	</tr>
	</table>
	</div>
	</td>
	</tr>
	</table>
</logic:notEqual>
</form>