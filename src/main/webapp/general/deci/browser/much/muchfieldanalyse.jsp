<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.Calendar" %>
<html>
<head>
<script language="JavaScript" src="/js/validate.js"></script>
<link href="../../../../css/css1.css" rel="stylesheet" type="text/css">
</head>
<script language="javaScript">

	function change(){
		var cf = "${muchFieldAnalyseForm.changeFlag}";
		if(cf == "yes"){
			var v = muchFieldAnalyseForm.analyseType.value;
			if(v == 1){//横向
				document.getElementById("go").style.display="none";
				document.getElementById("datespace").style.display="none";
			}else if(v ==2){//纵向
				document.getElementById("go").style.display="inline";
				document.getElementById("datespace").style.display="inline";
			}
		}	
		
		if(check()){
			submitt();
		}
	}
	
	function check(){
		var cf = "${muchFieldAnalyseForm.changeFlag}";	
		var at = muchFieldAnalyseForm.analyseType.value;

		if(cf == "yes" && at =="2" ){//按年/月变化
			var sy = muchFieldAnalyseForm.startYear.value;
			var ey = muchFieldAnalyseForm.endYear.value;			
			var c = "${muchFieldAnalyseForm.changeFlagValue}";
			if(parseInt(sy) > parseInt(ey) ){
				alert("起始年必须小于或等于终止年!");
				return false;
			}
			
			if(c=="1"){//按月变化			
				var sm = muchFieldAnalyseForm.startMonth.value;
				var em = muchFieldAnalyseForm.endMonth.value;
				if(parseInt(sy) == parseInt(ey)){
					if(parseInt(sm) > parseInt(em)){
						alert("起始月必须小于或等于终止月!");
						return false;
					}
				}
			}
		}	
		
		return true;	
	}
	
	function changeDbpre(){
		if(check()){
			submitt();
		}
	}
	
	function changeSY(){
		if(check()){
			submitt();
		}
	}
	function changeSM(){
		if(check()){
			submitt();
		}
	}
	
	function changeEY(){
		if(check()){
			submitt();
		}
	}
	function changeEM(){
		if(check()){
			submitt();
		}
	}
	
	
	function submitt(){
		muchFieldAnalyseForm.target="ril_body2";
		muchFieldAnalyseForm.action="/general/deci/browser/much/muchfieldanalyse.do?b_analyse=link";
		muchFieldAnalyseForm.submit();
	}
	
	function load(){  	
		//页面重定向
		parent.ril_body2.location.href ="/general/deci/browser/much/muchfieldanalysechart.jsp";
		 
	}
	
	function set(){
		var chart = jfreechartSet(muchFieldAnalyseForm.chartTitle.value,"",muchFieldAnalyseForm.chartSets.value);
		if(chart != null){
			arrays = chart.split("`");
			var message = "";
			message += arrays[1];
			message += "`";
			message += arrays[6];
			message += "`";
			message += arrays[7];
			message += "`";
			message += arrays[3];
			message += "`";
			message += arrays[4];
			message += "`";
			var ss = arrays[2];
			if(ss.charAt(0)=="1"){
				message += "false";
				message += ",";
				sss = ss.split(",");
				message += sss[1];
				message += ",";
				message += sss[2];
				message += "`";
			}else{
				message += "true";
				message += "`";
			}			
			message += arrays[5];
			muchFieldAnalyseForm.chartSets.value=message;
			muchFieldAnalyseForm.target="ril_body2";
			muchFieldAnalyseForm.action="/general/deci/browser/much/muchfieldanalyse.do?b_analyse=link&chartParameters="+chart;
			muchFieldAnalyseForm.submit();
		}else{
			return;
		}
	}
</script>

<body onload="load()" style="padding-top: 10px;">
	<form name="muchFieldAnalyseForm" method="post" action="">
		<table width="90%" border="0" cellspacing="1" align="left" cellpadding="1" >
		<tr>
			<td>
			<logic:equal name="muchFieldAnalyseForm" property="dbFlag" value="A">
					<bean:message key="menu.base"/>
		    		<hrms:optioncollection name="muchFieldAnalyseForm" property="dbList" collection="list" />
		         	<html:select name="muchFieldAnalyseForm" property="dbpre" size="1" onchange="changeDbpre()">
		         		<html:options collection="list" property="dataValue" labelProperty="dataName"/>
		   		 	</html:select>				         
			</logic:equal>
			<bean:message key="muchfieldanalyse.analysetype"/>
			<select name="analyseType" onChange="change()">
		      <option value="1"><bean:message key="muchfieldanalyse.heanalyse"/></option>
		      <option value="2"><bean:message key="muchfieldanalyse.zoanalyse"/></option>
	      	</select> 
	      	<input type="hidden" name="changeFlagValue" value="${muchFieldAnalyseForm.changeFlagValue}">
	      	<input type="hidden" name="itemName" value="${muchFieldAnalyseForm.itemName}">
	      	<input type="hidden" name="changeFlag" value="${muchFieldAnalyseForm.changeFlag}">
	      	<input type="hidden" name="itemid" value="${muchFieldAnalyseForm.itemid}">
			<input type="hidden" name="dbFlag" value="${muchFieldAnalyseForm.dbFlag}">  

   			<input type="hidden" name="chartSets" value="${muchFieldAnalyseForm.chartSets}">
			<input type="hidden" name="chartTitle" value="${muchFieldAnalyseForm.chartTitle}">
			
			<logic:equal name="muchFieldAnalyseForm" property="changeFlag" value="yes">
			<bean:message key="kq.init.tscope"/>
			<%Calendar c = Calendar.getInstance();int year = c.get(Calendar.YEAR);int month = c.get(Calendar.MONTH);%>
				
				<select name="startYear" onChange="changeSY()">	
					<% 
						for(int i=(year-10);i<(year+10); i++){
							if(i == year){
							%>
								<option value="<%=i%>" selected><%=i%><bean:message key="datestyle.year"/></option>
							<%
							}else{
							%>
								<option value="<%=i%>"><%=i%><bean:message key="datestyle.year"/></option>
							<%
							}
						}%>
				</select>
				<logic:equal name="muchFieldAnalyseForm" property="changeFlagValue" value="1">
				<select name="startMonth"  onChange="changeSM()">
					<%
						for(int j =1; j<=12; j++){
							if(j == month+1){
							%>
								<option value="<%=j%>" selected><%=j%><bean:message key="datestyle.month"/></option>
							<%
							}else{
							%>
								<option value="<%=j%>" ><%=j%><bean:message key="datestyle.month"/></option>
							<%
							}
					}%>
				</select>	
				</logic:equal>	
				<div id="go"style="display:none" >---</div>		
				<div id="datespace" style="display:none" >
					<select name="endYear"  onChange="changeEY()">
						<% 
						for(int i=(year-10);i<(year+10); i++){
							if(i == year){
							%>
								<option value="<%=i%>" selected><%=i%><bean:message key="datestyle.year"/></option>
							<%
							}else{
							%>
								<option value="<%=i%>"><%=i%><bean:message key="datestyle.year"/></option>
							<%
							}
						}%>
					</select>
					<logic:equal name="muchFieldAnalyseForm" property="changeFlagValue" value="1">
					<select name="endMonth"  onChange="changeEM()">
						<%for(int j =1; j<=12; j++){
							if(j == month+1){
							%>
								<option value="<%=j%>" selected><%=j%><bean:message key="datestyle.month"/></option>
							<%
							}else{
							%>
								<option value="<%=j%>" ><%=j%><bean:message key="datestyle.month"/></option>
							<%
							}
					}%>
					</select>
					</logic:equal>	
				</div>
							
			</logic:equal>		
			<!-- input type="button" value="<bean:message key="button.orgmapset"/>" onclick="set()" class="mybutton"> 暂时先屏蔽吧	 -->	 	 
			</td>
		</tr>		
		</table>
	</form>
</body>
</html> 
