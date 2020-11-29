
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.report.auto_fill_report.ReportOptionForm"%>
<%
	int i = 0;
	 ReportOptionForm reportOptionForm=(ReportOptionForm)session.getAttribute("reportOptionForm");
	 String updateflag="0";
   if(reportOptionForm!=null&&reportOptionForm.getUpdateflag()!=null){
       updateflag = reportOptionForm.getUpdateflag();
   
   }
%>
	<style>
	#tbl-container {
			 
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
		margin-left:5px;
		overflow:auto;
		height:250px;
		width:95% 	
		
	}	
</style>
<html>

	<link href="/css/css1.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="/css/css1.css" type="text/css">
	<script language="javascript">
	 function saveSort(){ 
	  	var scopeid = $('scopeid').value;
	var waitInfo=eval("wait");
			waitInfo.style.display="block";
			var hashvo=new ParameterSet();
				var selectid=new Array();
	  	selectid[0]= '${staticStatementForm.tabid}';
			hashvo.setValue("selectid",selectid);
			hashvo.setValue("updateflag",<%=updateflag %>);			
			hashvo.setValue("scopeid",scopeid);			
			var In_paramters="flag=1"; 		
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'03010000002'},hashvo);
	  	}
  function 	  returnInfo2(outparamters){
  	window.returnValue="";
   		window.close();
  }	
  function scopeChange(){
		var scopeid = $('scopeid').value;
		if(scopeid!=""){
		window.returnValue=scopeid;
   		window.close();
   		}
		}
   </script>
	<body>
		<html:form action="/report/edit_report/editReport/staticStatement">
		<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					<bean:message key="report.reportlist.reportqushu"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
	</div>

	<br>
			<center>
			<div id="tbl-container">
				<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
					<tr>
						<td colspan="2">
							<bean:message key="report.owner.unit"/>：
							<bean:write name="staticStatementForm" property="scopeownerunit"
								filter="true" />
							<bean:message key="report.static.statement"/>：

							<hrms:optioncollection name="staticStatementForm"
								property="scopelist" collection="list" />
							<html:select name="staticStatementForm" property="scopeid"
								size="1" onchange="javascript:scopeChange()">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="report.organization.code"/>
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="report.organization.name"/>
						</td>
					</tr>
					<logic:iterate id="element" name="staticStatementForm"
						property="unitslist">
						<%
							if (i % 2 == 0) {
						%>
						<tr class="trShallow" onclick='tr_onclick(this,"#F3F5FC");'>
							<%
								} else {
							%>
						
						<tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");'>
							<%
								}
											i++;
							%>

							<td align="center" class="RecordRow" nowrap>
								&nbsp;
								<bean:write name="element" property="unitid" filter="false" />
								&nbsp;
							</td>
							<td align="center" class="RecordRow" nowrap>
								&nbsp;
								<bean:write name="element" property="unitname" filter="false" />
							</td>
						</tr>
					</logic:iterate>
					
				</table>
				</div>
				<table>
				<tr>
						<td colspan="2" align="center">
							<input type="button" value="<bean:message key='button.ok'/>"
								onclick="saveSort();" Class="mybutton">
							<input type="button" value="<bean:message key='button.cancel'/>"
								onclick="window.close();" Class="mybutton">
						</td>
					</tr>
				</table>
			</center>
		</html:form>

	</body>
</html>
