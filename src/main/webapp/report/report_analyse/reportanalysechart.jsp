<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.report.report_analyse.ReportAnalyseForm" %>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<body onResize="change()" style="overflow:auto;" >
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<%
String reload=request.getParameter("reload");
%>
<%
	ReportAnalyseForm reportAnalyseForm=(ReportAnalyseForm)session.getAttribute("reportAnalyseForm");
	String chartFlag=reportAnalyseForm.getChartFlag();
	String width=""+Integer.parseInt(reportAnalyseForm.getChartWidth())*1.1;
	if(width.indexOf(".")!=-1)
	width = width.substring(0,width.indexOf("."));
 %>
<script language="JavaScript" >
	var reload='<%=reload%>'
	if(reload!=='1'){
		change();
	}
	function change(changeFlag){
		var is = new Is();
		var available_width = 750;
		var available_height = 200; 
		
		var image_width = 750;
		var image_height = 200;
	    if(is.ns4||is.ns6) {
	        available_width=innerWidth;
	        available_height=innerHeight;
	    } else if(is.ie4||is.ie5||is.ieX) {
	    	//----liuy 修改报表浏览分析图显示不全 2014-7-31 begin
	        available_width=parent.ril_body2.document.body.clientWidth;
	        available_height=document.documentElement.clientHeight;
	        //----liuy 修改报表浏览分析图显示不全 2014-7-31 end
	    }
	    if(is.ie4 ||is.ie5||is.ieX||is.ns6|| is.ns4) {
	    	//----liuy 修改报表浏览分析图显示不全 2014-7-31 begin
	    	available_width = available_width*0.9;
	    	available_height = available_height*0.9;
	    	//----liuy 修改报表浏览分析图显示不全 2014-7-31 end
	    	available_width = ""+available_width;
	    	available_height =""+available_height;
	    	
	    	if(available_width.indexOf('.')!=-1){
	    		available_width = available_width.substring(0,available_width.indexOf('.'));
	    	}
	    	if(available_height.indexOf('.')!=-1){
	    		available_height = available_height.substring(0,available_height.indexOf('.'));
	    	}
//	    	available_height=available_height*1.4;
//		    	available_height =""+available_height;
//		    	if(available_height.indexOf('.')!=-1){
//		    		available_height = available_height.substring(0,available_height.indexOf('.'));
//		    	}
			image_width = (available_width);
			image_height = (available_height);
		
		}
	//	alert(parent.ril_body2.document.body.height);
	// var obj=parent.ril_body2.document.body.style;
	// parent.ril_body2.screen.availHeight=image_height;
	 // obj.height=image_height;
	
		var wid = image_width;
		var hei = image_height;
		//页面重定向//request.getParameter("tabid")==null||request.getParameter("tabid").equals("")?reportAnalyseForm.getReportTabid():
		parent.ril_body2.location.href ="/report/report_analyse/reportanalyse.do?reload=1&b_changeFrame=link&w="+$URL.encode(wid)+"&h="+$URL.encode(hei)+
						"&code="+$URL.encode('<%=(request.getParameter("code")==null?"":request.getParameter("code"))%>')+
						"&tabid="+$URL.encode('<%=(request.getParameter("tabid")==null?"":request.getParameter("tabid"))%>')+
						"&rc="+$URL.encode('<%=(request.getParameter("rc")==null?"":request.getParameter("rc"))%>')+
						"&unitcodes="+$URL.encode('<%=(request.getParameter("unitcodes")==null?"":request.getParameter("unitcodes"))%>');
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
	
	
</script>
<hrms:themes />
<form name="reportAnalyseForm" method="post" action="/report/report_analyse/reportanalyse.do">
	<input type="hidden" name="chartHeight" value="${reportAnalyseForm.chartHeight}" >
	<input type="hidden" name="chartWidth" value="${reportAnalyseForm.chartWidth}" >
<table align="left" width="50%" height="90%">
<tr height="10%">
	<td width="<%=width %>" >
		<bean:message key="report_collect.analyseFashion" /><select name='showFlag' onchange='setShowFlag()' >
			<option value='1'  <logic:equal name="reportAnalyseForm" property="showFlag" value="1">selected</logic:equal>  ><bean:message key="report_collect.analyseByTime" /></option>
			<option value='2'  <logic:equal name="reportAnalyseForm" property="showFlag" value="2">selected</logic:equal> ><bean:message key="report_collect.analyseByUN" /></option>
		</select>
		&nbsp;&nbsp;
		<bean:message key="report_collect.chartType" />
		<select name='char_type' onchange='setChartType()'  >
			<option value='1'  <logic:equal name="reportAnalyseForm" property="char_type" value="1">selected</logic:equal>  ><bean:message key="lable.performance.histogram" /></option>
			<option value='2'  <logic:equal name="reportAnalyseForm" property="char_type" value="2">selected</logic:equal> ><bean:message key="lable.performance.graph" /></option>
		</select>
	</td>
</tr>

<tr height="90%">
<td>
	<%--去掉纵向滚动条 wangbs 20190321--%>
<div id='wait' style='overflow:hidden;margin-bottom:10px'>
<table align="left" width="50%" height="100%">
<logic:notEqual name="reportAnalyseForm" property="chartFlag" value="no">
	<tr>
		<td align="left" nowrap colspan="5">
		<logic:equal  name="reportAnalyseForm" property="char_type"  value="1">
			<hrms:chart name="reportAnalyseForm" title="${reportAnalyseForm.chartTitle}" 
			scope="session" numDecimals="1" legends="list" orient="vertical" data="" width="${reportAnalyseForm.chartWidth}" height="${reportAnalyseForm.chartHeight+200}"
			chart_type="${reportAnalyseForm.chartType}"
			 isneedsum="false" >
			</hrms:chart>
		</logic:equal>
		<logic:equal  name="reportAnalyseForm" property="char_type"  value="2">
			<hrms:chart name="reportAnalyseForm" title="${reportAnalyseForm.chartTitle}" 
			scope="session" numDecimals="2" legends="dataMap" data="" width="${reportAnalyseForm.chartWidth}" height="${reportAnalyseForm.chartHeight+200}" 
			chart_type="${reportAnalyseForm.chartType}"
			 isneedsum="false" >
			</hrms:chart>
		</logic:equal>	
		</td>
	</tr>
</logic:notEqual>
	</table>
	</div>
	
	</td>
	
	</tr>
	</table>
</form>

<script language='javascript'>

function setChartType()
{
	if(<%=(request.getParameter("h"))%>!=null){
		document.reportAnalyseForm.action="/report/report_analyse/reportanalyse.do?b_changeGrid=link&code=<%=(request.getParameter("code"))%>&tabid=<%=(request.getParameter("tabid"))%>&rc=<%=URLEncoder.encode(request.getParameter("rc")==null?"":request.getParameter("rc"),"UTF-8")%>&w=<%=(request.getParameter("w"))%>&h=<%=(request.getParameter("h"))%>&unitcodes=<%=(request.getParameter("unitcodes"))%>";
		document.reportAnalyseForm.submit();
		}
}


function setShowFlag()
{
		parent.ril_body1.resetSelectGrid(document.reportAnalyseForm.showFlag.value);
		parent.mil_menu.refresh(document.reportAnalyseForm.showFlag.value);
		document.reportAnalyseForm.action="/report/report_analyse/reportanalyse.do?b_setShowflag="+document.reportAnalyseForm.showFlag.value+"&tabid=<%=(request.getParameter("tabid"))%>";
		document.reportAnalyseForm.submit();
}
	

</script>
</body>