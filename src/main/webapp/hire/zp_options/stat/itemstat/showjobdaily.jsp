<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.ResourceFactory,com.hjsj.hrms.actionform.hire.zp_options.itemstat.ShowDailyForm"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%>  
<%
 String columnName=ResourceFactory.getProperty("e01a1.label");
 ShowDailyForm showDailyForm = (ShowDailyForm)session.getAttribute("showDailyForm");
 String schoolPosition=showDailyForm.getSchoolPosition();
 if(schoolPosition!=null&&schoolPosition.length()>0) 
     columnName=ResourceFactory.getProperty("e01a1.major.label");
 %>
<script type="text/javascript">
<!--
function searchpic(name,command){
	var pic=$(name);
	pic[1].style.display=command;
}
function queryjob(){
    var d1 = document.getElementById("editor1").value;
    var d2 = document.getElementById("editor2").value;
    if(trim(d1).length<=0||trim(d2).length<=0)
    {
        alert(DESIGN_SEARCH_INTEVAL+"！");
        return;
    }
    if(checkDateTime(d1)&&checkDateTime(d2))
    {
    	showDailyForm.action="/hire/zp_options/stat/itemstat/showjobdaily.do?b_query=link";
	    showDailyForm.submit();
	}else{
	   alert(TIME_ERROR_INPUT_RIGHT_TIME);
	}
	
}
function jumpdep(){
	window.location.href="/hire/zp_options/stat/totalstat/showtotalstatresult.do?b_query=link";
}

//-->
</script>
<script type="text/javascript" language="javascript" src="/anychart/js/AnyChart.js"></script>
<script type="text/javascript" language="javascript" src="/ajax/basic.js"></script>
<style>
<!--
.TableRowNoBorder {
   background-image:url(../images/listTableHeader.jpg);
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #DCECFC; 
	font-size: 12px;  
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	height:22;
	font-weight: bold;	
	valign:middle;
}
-->
</style>
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_options/stat/itemstat/showjobdaily">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
    
%>
<br/>
<%
}
%>
<table border="0" cellpadding="0" cellspacing="0" align="center"   width="700px" style="margin-top:0px;">
<tr>
<td>
<table  border="0" cellpadding="0" cellspacing="0" align="center"  style="margin-left:0px;width:100%" class="RecordRow">
	<tr class="TableRow" border="0">
		<td class='TableRow' style='border-top:0px;border-left:0px;border-right:0px;' colspan='2' nowrap>
		  <table>
		  <tr>
			<td><%=columnName%>
				<html:select name="showDailyForm" property="jobid" onchange="queryjob();">
			 			<html:optionsCollection property="joblist" value="dataValue" label="dataName"/>
				</html:select>
		    </td>
		    <td>
			<bean:message key="hire.zp_options.search.time"/>&nbsp;&nbsp;
			<bean:message key="label.from"/>
			<input type="text" name="startime"  extra="editor" style="width:100px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate" value="${showDailyForm.startime}">
			<bean:message key="kq.init.tand"/>
			<input type="text" name="endtime"  extra="editor" style="width:100px;font-size:10pt;text-align:left" id="editor2"  dropDown="dropDownDate" value="${showDailyForm.endtime}">
			</td>
			<td>
			<BUTTON name="tfquery" class="mybutton" onclick="queryjob();" style="margin-top:3px; height:18px;"><bean:message key="lable.law_base_file_search.query"/></BUTTON>
			</td>
			</tr>
			</table>
		</td>
	</tr>
    <tr>

	<td align='left' width="50%" id='chart1' valign="top">
		<hrms:chart name="showDailyForm" title="" scope="session" legends="joblistview" data=""  width="480" height="460" chart_type="20" chartpnl="chart1"  numDecimals="0" labelIsPercent="0" xangle="45">
		</hrms:chart>
	</td>
	<td width="50%" align="left" valign="top">
	<br>
	<div style="height: 450px;overflow: auto;" class="complex_border_color">
		<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-left:-5px;">
			<tr>
				<td align="center" class="TableRow" style="border-top:none;" nowrap><bean:message key="kq.wizard.weeks"/></td>
				<td align="center" class="TableRow" style="border-top:none;" nowrap><bean:message key="hire.zp_options.date.received"/></td>
				<td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap><bean:message key="hire.zp_options.curriculum.vitae.volume"/></td>					
			</tr>
			 <hrms:extenditerate id="element" name="showDailyForm" property="recordListForm.list" indexes="indexes"  pagination="recordListForm.pagination" pageCount="20" scope="session">
			
			<tr>
				<td class="RecordRow" nowrap>
					<bean:write name="element" property="week"/>
				</td>
				<td class="RecordRow" nowrap>
					<bean:write name="element" property="date"/>
				</td>
				<td class="RecordRow" style="border-right:none;" align="right" nowrap>
					<bean:write name="element" property="num"/>
				</td>
			</tr>
		</hrms:extenditerate>
		</table></div>
		    <table  width="80%" align="center" style="border:1px solid;border-top: none;" class="common_border_color">
	    	<tr>
	    	   <td valign="middle" nowrap>
	   	            <bean:message key="label.page.serial"/>
	    	   <bean:write name="showDailyForm" property="recordListForm.pagination.current" filter="true"/>
	    				<bean:message key="label.page.sum"/>
	    	   <bean:write name="showDailyForm" property="recordListForm.pagination.count" filter="true"/>
		    			<bean:message key="label.page.row"/>
	    	   <bean:write name="showDailyForm" property="recordListForm.pagination.pages" filter="true"/>
	     				<bean:message key="label.page.page"/>
	    	   </td>
	    	   <td align="right" nowrap>
	    	   <p align="right">
	    	   <hrms:paginationlink name="showDailyForm" property="recordListForm.pagination" nameId="recordListForm" propertyId="recordListProperty">
		       </hrms:paginationlink>
	    	   </td>
	    	</tr> 
          </table>
<Br>&nbsp;
</td>
</tr>
</table>
</td>
</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" align="center" >
<tr>
<td align="left">
<logic:equal value="dxt" name="showDailyForm" property="returnflag">
<hrms:tipwizardbutton flag="retain" target="il_body" formname="showDailyForm"/> 
</logic:equal>	
</td>
</tr>
</table>
</html:form>

  	 


    