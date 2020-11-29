<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
<title></title>
<script type="text/javascript">
function open(){
	alert("wer");
}
function goback(){
	document.myObjectiveForm.action="/performance/objectiveManage/myObjective/my_objective_list.do?b_init=init2&entranceType=0&opt=3&year=${myObjectiveForm.year}&quarter=${myObjectiveForm.quarter}&month=${myObjectiveForm.month}&status=${myObjectiveForm.status}";
	document.myObjectiveForm.submit();
}
function search(){
	var tt=document.getElementsByName('record');
	var reg=/^(\d{1,4}|\d{2})[.\-\/](2-([1-9]\b|1\d|2[0-8])|([13578]|1[02]|0[1-9])[.\-\/]([1-9]\b|0[1-9]\b|[12]\d|3[01])|([469]|11)-([1-9]\b|[12]\d|30))$/;
	for(var i=0;i<tt.length;i++){
		var record=tt[i];
		if(tt[1].checked){
			var start=document.getElementById("startdate").value;
			var end=document.getElementById("enddate").value;
			if((start==null||trim(start).length==0)&&(end==null||trim(end).length==0)){
				alert("请选择日期，最少选择一个！");
				return;
			}
			if(start!=null&&trim(start).length!=0){
				if(!reg.test(start)){
					alert("请输入正确的起始日期！");
					return;
				}
			}
			if(end!=null&&trim(end).length!=0){
				if(!reg.test(end)){
					alert("请输入正确的截止日期！");
					return;
				}
			}
			if((start==null||trim(start).length==0)&&(end!=null||trim(end).length!=0)){
			
			}else if((start!=null||trim(start).length!=0)&&(end==null||trim(end).length==0)){
			
			}else if((start!=null||trim(start).length!=0)&&(end!=null||trim(end).length!=0)){
				if(start>end){
			
					alert("起始日期不能大于截止日期,请重新输入起始或截止日期！");
					return;
				}
			}
		}
		if(tt[0].checked){
			var days=document.getElementById("latest").value;
			var r = /^[0-9]*[1-9][0-9]*$/;	
			if(isqcode(days)){
				days=zuzhuang(days);
				
				if(r.test(days)){
				
				}else{
					alert("天数请输入正整数！");
					return;
				}
			}else{
				if(r.test(days)){
				
				}else{
					alert("天数请输入正整数！");
					return;
				}
			}
		}
	}
	document.myObjectiveForm.action="/performance/objectiveManage/myObjective/searchmytask.do?b_searchtas=link&opt=search";
	document.myObjectiveForm.submit();
}
function selectqwer(obj){

	var firstday=document.getElementsByName("record")[0];
	var times=document.getElementsByName("record")[1];
	if(obj.value=='1'){
		obj.checked=true;
		times.cheked=false;

		var start=document.getElementById("startdate");
		start.value='';
		var end=document.getElementById("enddate");
		end.value='';	
	}	
	if(obj.value=='2'){
		obj.checked=true;
		var latest=document.getElementsByName("latest")[0];
		firstday.cheked=false;	
			
	}
}
function isqcode(Str){
	for(var   i=0;i <Str.length;i++) 
    { 
     	 strCode=Str.charCodeAt(i); 
         if((strCode> 65248)||(strCode==12288)) 
      	 { 
           return true; 
         } 
    } 
}
function zuzhuang(str){
	var   result= "";
    for   (var   i   =   0;   i   <   str.length;   i++) 
    { 
       if(str.charCodeAt(i)==12288) 
       { 
            result+=   String.fromCharCode(str.charCodeAt(i)-12256); 
            continue; 
       } 
    if(str.charCodeAt(i)> 65280   &&   str.charCodeAt(i) <65375) 
          result+=String.fromCharCode(str.charCodeAt(i)-65248); 
    else 
       result+=String.fromCharCode(str.charCodeAt(i)); 
    }       
    return result; 
}
</script>
</head>
<body>

<html:form action="/performance/objectiveManage/myObjective/searchmytask">
<html:hidden name="objectCardForm" property="returnURL"/>
	<table width='80%' align=center class="ListTable">
		<tr>
		<td>
		<logic:equal name='myObjectiveForm' property='record' value="1">
		
		<input type='radio' value='1' name='record' onclick="selectqwer(this);" checked>按最近日期<input type='text' style='width:30px;'id=latest name='latest' value='<bean:write name="myObjectiveForm" property="latest"/>' class="inputtext">天&nbsp;&nbsp;&nbsp;&nbsp;<input type='radio' value='2' name='record' onclick="selectqwer(this);">按时间段从	<input type="text" name="startdate" size="14"   value='<bean:write name="myObjectiveForm" property="startdate" />' style="BACKGROUND-COLOR:#F8F8F8;border: 1pt solid #A8C4EC;width:150px"  extra="editor"  id='startdate'  dropDown="dropDownDate" class="inputtext"/>至<input type="text" name="enddate" size="14"  value='<bean:write name="myObjectiveForm" property="enddate" />' style="BACKGROUND-COLOR:#F8F8F8;border: 1pt solid #A8C4EC;width:150px"  extra="editor"  id='enddate'  dropDown="dropDownDate" class="inputtext"/> 
		&nbsp;&nbsp;<input type='button' class='mybutton' value='查询' onclick='search()'><input type='button' class='mybutton' value='返回' onclick='goback();'>
		</logic:equal>
		<logic:equal name='myObjectiveForm' property='record' value="2">
		
		<input type='radio' value='1' name='record' onclick="selectqwer(this);" >按最近日期<input type='text' style='width:30px;' name='latest' value='<bean:write name="myObjectiveForm" property="latest"/>'  class="inputtext">天&nbsp;&nbsp;&nbsp;&nbsp;<input type='radio' value='2' name='record' onclick="selectqwer(this);" checked>按时间段从	<input type="text" name="startdate" size="14"   value='<bean:write name="myObjectiveForm" property="startdate" />' style="BACKGROUND-COLOR:#F8F8F8;border: 1pt solid #A8C4EC;width:150px"  extra="editor"  id='startdate'  dropDown="dropDownDate" class="inputtext"/>至<input type="text" name="enddate" size="14"  value='<bean:write name="myObjectiveForm" property="enddate" />' style="BACKGROUND-COLOR:#F8F8F8;border: 1pt solid #A8C4EC;width:150px"  extra="editor"  id='enddate'  dropDown="dropDownDate" class="inputtext"/> 
		&nbsp;&nbsp;<input type='button' class='mybutton' value='查询' onclick='search()'><input type='button' class='mybutton' value='返回' onclick='goback();'>
		</logic:equal>
		</td>
		</tr>
		<tr>
		<td>
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
			<tr>
			<td width="5%" align="center" class="TableRow" nowrap>
				序号
			</td>
			<td width="30%" align="center" class="TableRow" nowrap>
			任务内容
			</td>
			<td width="25%"align="center" class="TableRow" nowrap>
				附件
			</td>
			<td width="10%"align="center" class="TableRow" nowrap>
			下达人
			</td>
				<td width="20%"align="center" class="TableRow" nowrap>
					下达时间
				</td>
				<td width="10%"align="center" class="TableRow" nowrap>
				任务类型
				</td>
				
					</tr>
					<hrms:extenditerate id="element" name="myObjectiveForm" property="taskListForm.list" indexes="indexes"  pagination="taskListForm.pagination" pageCount="15" scope="session">
						 <tr class="trDeep">
						 	<td align="center" class="RecordRow" nowrap>
					 		 	<bean:write name="element" property="id"/>
					 		 </td>
					 		 <td align="center" class="RecordRow" style="word-break:break-all">
					 		 	<bean:write name="element" property="content"/>
					 		 </td>
					 		  <td align="left" class="RecordRow" nowrap>
					 		 	
					 		 	<logic:notEqual name='element' property='articlename' value="-1">
					 		 	<logic:iterate id="ele" name="element" property="namelist">
					 		 	<a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="ele" property="datavalue" />'  target="_blank"  border='0' >
					 		 		<img src="/images/detail.gif" border="0">
					 		 			<bean:write name="ele" property="dataname"/>
					 		 			<br>
					 		 			</a>
					 		 	</logic:iterate>
					 		 	
					 		 		
					 		 	</logic:notEqual>
					 		 
					 		 </td>
					 		  <td align="center" class="RecordRow" nowrap>
					 		  		<bean:write name="element" property="xdpeople"/>
					 		 </td>
					 		  <td align="center" class="RecordRow" nowrap>
					 		  		<bean:write name="element" property="Create_date"/>
					 		 </td>
					 		  <td align="center" class="RecordRow" nowrap>
					 		  		<bean:write name="element" property="TASK_TYPE"/>
					 		 </td>
						 </tr>
					</hrms:extenditerate>
					
		</table>
	
		<tr>
			<td>			
				<table  width="100%" align="center" class="RecordRowP">
						<tr>
						   <td valign="bottom" class="tdFontcolor" nowrap>
						            <bean:message key="label.page.serial"/>
						   ${myObjectiveForm.taskListForm.pagination.current}
									<bean:message key="label.page.sum"/>
						   ${myObjectiveForm.taskListForm.pagination.count}
									<bean:message key="label.page.row"/>
						   ${myObjectiveForm.taskListForm.pagination.pages}
									<bean:message key="label.page.page"/>
						   </td>
						   <td align="right" class="tdFontcolor" nowrap>
						   <p align="right">
				            <hrms:paginationlink name="myObjectiveForm" property="taskListForm.pagination" nameId="taskListForm" propertyId="asdf">
						   </hrms:paginationlink>
						   </p>
						   </td>
						</tr> 
				</table>
			</td>
		</tr>
	</table>
</html:form>
</body>
</html>