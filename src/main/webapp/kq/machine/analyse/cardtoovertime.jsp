<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/validateDate.js"></script>
<script language="javascript" src="/kq/kq.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

<%
	String sysSpState = ""; 
	try {
	    sysSpState = SystemConfig.getProperty("overtime_tran_spstate");
	} catch (Exception e) {
	    
	}
    sysSpState = null == sysSpState ? "" : sysSpState;
%>

<style type="text/css">
body {
	margin: 0px;
}

.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 41px;
	height: 20px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
}

.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}

.m_input {
	width: 12px;
	height: 15px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}

input_text {
	border: inset 1px #000000;
	BORDER-BOTTOM: #FFFFFF 0pt dotted;
	BORDER-LEFT: #FFFFFF 0pt dotted;
	BORDER-RIGHT: #FFFFFF 0pt dotted;
	BORDER-TOP: #FFFFFF 0pt dotted;
}
</style>
<html:form action="/kq/machine/analyse/cardtoovertime">
	<script language="javascript">
	function change(){
		if(!validate(eval("document.dataAnalyseForm.start_date"),"起始日期")){
        	return false;
		}
		
		if(!validate(eval("document.dataAnalyseForm.end_date"),"结束日期")){
			return false;
     	}

		//判断日期
	      var dd = eval("document.dataAnalyseForm.start_date");
	      var ks = dd.value;
	      var jsd = eval("document.dataAnalyseForm.end_date");
	      var js = jsd.value;
	      ks=replaceAll(ks,"-",".");
	      js=replaceAll(js,"-",".");
	      if(ks>js)
	      {
	        alert(KQ_CHECK_TIME_HINT);
	        return false;
	      }

	      //日期相同，判断时间
	      if(ks==js)
	      {
	          var startHH = eval("document.dataAnalyseForm.start_hh");
	          var intStartHH = parseInt(startHH.value);
	          var endHH = eval("document.dataAnalyseForm.end_hh");
	          var intEndHH = parseInt(endHH.value);
	          if(intStartHH>intEndHH)
	          {
	              alert(KQ_CHECK_TIME_HINT);
	              return false;
	          }

	          if(intStartHH==intEndHH)
	          {
	              var startMM = eval("document.dataAnalyseForm.start_mm");
	              var intStartMM = parseInt(startMM.value);
	              var endMM = eval("document.dataAnalyseForm.end_mm");
	              var intEndMM = parseInt(endMM.value);
	              if(intStartMM>intEndMM)
	              {
	                 alert(KQ_CHECK_TIME_HINT);
	                 return false;
	              }
	          }               
	      }       
		dataAnalyseForm.action="/kq/machine/analyse/cardtoovertime.do?b_search=link&select_flag=1";
		dataAnalyseForm.submit();
	}
	  
	function MusterInitData(){
		hide_nbase_select('select_pre');
	}
	
	this.fObj = null;
	var time_r=0; 
	function setFocusObj(obj,time_vv){		
		this.fObj = obj;
		time_r=time_vv;		
	}
	
	function IsInputTimeValue(){	     
		event.cancelBubble = true;
		var fObj=this.fObj;		
		if (!fObj) return;		
    	var cmd = event.srcElement.innerText=="5"?true:false;
    	if(fObj.value==""||fObj.value.lenght<=0)
			fObj.value="0";
    	var i = parseInt(fObj.value,10);		
    	var radix=parseInt(time_r,10)-1;				
    	if (i==radix&&cmd){
        	i = 0;
    	} else if (i==0&&!cmd) {
 			i = radix;
    	} else {
 			cmd?i++:i--;
    	}	
    	if(i==0){
			fObj.value = "00"
    	}else if(i<10&&i>0){
			fObj.value="0"+i;
    	}else{
			fObj.value = i;
    	}			
    	fObj.select();
    }
    
	var checkflag = "false";
	function selAll(){
		var len=document.dataAnalyseForm.elements.length;
		var i;
		if(checkflag == "false"){
	        for (i=0;i<len;i++){
				if (document.dataAnalyseForm.elements[i].type=="checkbox"){
					document.dataAnalyseForm.elements[i].checked=true;
				}
			}
	        checkflag = "true";
		}else{
			for (i=0;i<len;i++){
				if (document.dataAnalyseForm.elements[i].type=="checkbox"){
					document.dataAnalyseForm.elements[i].checked=false;
				}
			}
        	checkflag = "false";    
		}      
	}
	 
	function addAppClass(){
		var emp_arr = new Array();
		var a = 0;
		var b = 1;
		var len = document.dataAnalyseForm.elements.length;
		var isSelect = false;
		
		for(var i = 0;i < len;i++){
			if(document.dataAnalyseForm.elements[i].type=="checkbox" && document.dataAnalyseForm.elements[i].name != "aa"){
				if(document.dataAnalyseForm.elements[i].checked == true){
					isSelect = true;
					var aCheckBox = document.dataAnalyseForm.elements[i];
					var endIndex = aCheckBox.name.indexOf("]");
		            var checkIndex = parseInt(aCheckBox.name.substring(18,endIndex)) + 1;
		            emp_arr[a++] = document.getElementById("ID_" + checkIndex).value;
				}
				
			}
		}

		if(!isSelect){
            alert(PLASE_SELECT_RECORD);
            return false;
        }

		var templateid = "";
        var templateObj = document.getElementById("overtime_template");
        if (null != templateObj){
        	templateid = templateObj.value;
            if (templateid==""){
	            alert("请选择业务模板！");
	            return false;
            }
        }

        var spState="";
        <% if ("".equals(sysSpState)) { %>
        var spStateObj = document.getElementById("spstate");
        if (null != spStateObj) {
            spState=spStateObj.value;
        }
        <% } else { %>
        spState = "<%=sysSpState%>";
        <% } %>

		if(confirm("您确定要将所选记录转为加班申请吗？")){ 
			var hashvo = new ParameterSet();
			hashvo.setValue("emp_list", emp_arr);
			hashvo.setValue("templateid", templateid);
			hashvo.setValue("spstate", spState);
			var request = new Request({method:'post',asynchronous:true,onSuccess:returnInfo,functionId:'152110013125'}, hashvo);
			//dataAnalyseForm.action="/kq/machine/analyse/cardtoovertime.do?b_addapp=link&templateid=" + templateid + "&spstate=" + spState;
	        //dataAnalyseForm.submit();
		}
	}
	function returnInfo(outparamters){
		var mess = outparamters.getValue("errorMess");
		
		if(mess != null && mess != ""){
			alert(mess);
		}
		window.location.href = "/kq/machine/analyse/cardtoovertime.do?b_search=link";
	}	
	function IsDigit() 
	{ 	     
		var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;

	    return ((keyCode >= 48) && (keyCode <= 57)); 
	} 
	function checkvalue(obj,a0100,nbase,start,end,fact_time)
	{
		check_obj = obj;
		var changevalue = check_obj.value;
		var object = new ParameterSet();
		object.setValue("timeLen",changevalue);
		object.setValue("a0100",a0100);
		object.setValue("nbase",nbase);
		object.setValue("start",start);
		object.setValue("end",end);
		object.setValue("fact_time",fact_time);
		var request=new Request({method:'post',asynchronous:false,onSuccess:editOk,functionId:'152110013126'},object);
	}
	function editOk(outparamters)
	{
	      var check_flag=outparamters.getValue("flag");
	      var check_mess=outparamters.getValue("mess");
	      if(check_flag=="false")
	      {
	         alert(check_mess);         
	         check_obj.focus(); 
	         check_obj.value = "";
	      }	
	}
</script>
	<table width="100%" border="0" cellspacing="0" align="left"
		cellpadding="0" style="margin-top:5px;">
		<tr>
			<td>
				<table width="50%" border="0" cellspacing="0" align="left"
					cellpadding="0">
					<tr style="padding-bottom: 5px">
						<td nowrap  align="left">
							<html:select name="dataAnalyseForm" property="select_pre" styleId="select_pre" size="1" onchange="change();">
								<html:optionsCollection property="kq_list" value="dataValue" label="dataName" />
							</html:select>
							&nbsp;&nbsp;
						</td>
						<td align="center" width="40" nowrap>
							范围&nbsp;<input type="hidden" name="dateValue" id="dateValue">
						</td>
						<td align="left" width="170" nowrap>
							<table border="0" cellspacing="0" align="left" cellpadding="0">
								<tr>
									<td>
										<input type="text" name="start_date" class="inputtext" extra="editor" dataType="simpledate"
											value="${dataAnalyseForm.start_date}"
											style="width: 100px; font-size: 10pt; text-align: left"
											id="editor1" onclick='saveCurrDateValue(this);getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);restoreDateValue(this,kq_duration)">
									</td>
									<td width="40" nowrap style="background-color: #FFFFFF";>
										<div <div class="m_frameborder inputtext">
											<input type="text" class="m_input" maxlength="2"
												name="start_hh" value="${dataAnalyseForm.start_hh}"
												onfocus="setFocusObj(this,24);">
											<font color="#000000"><strong>:</strong> </font>
											<input type="text" class="m_input" maxlength="2"
												name="start_mm" value="${dataAnalyseForm.start_mm}"
												onfocus="setFocusObj(this,60);">
										</div>
									</td>
									<td>
										<table border="0" cellspacing="2" cellpadding="0">
											<tr><td><button id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
		         							<tr><td><button id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
						<td align="center" nowrap>
							-&nbsp;
						</td>
						<td align="left" width="170" nowrap>

							<table border="0" cellspacing="0" align="left" cellpadding="0">
								<tr>
									<td>
										<input type="text" class="inputtext" name="end_date" extra="editor" dataType="simpledate"
											value="${dataAnalyseForm.end_date}" 
											style="width: 100px; font-size: 10pt; text-align: left"
											id="editor1" onclick='saveCurrDateValue(this);getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);restoreDateValue(this,kq_duration)">
									</td>
									<td width="40" nowrap style="background-color: #FFFFFF">
										<div <div class="m_frameborder inputtext">
											<input type="text" class="m_input" maxlength="2"
												name="end_hh" value="${dataAnalyseForm.end_hh}"
												onfocus="setFocusObj(this,24);">
											<font color="#000000"><strong>:</strong> </font>
											<input type="text" class="m_input" maxlength="2"
												name="end_mm" value="${dataAnalyseForm.end_mm}"
												onfocus="setFocusObj(this,60);">
										</div>
									</td>
									<td>
										<table border="0" cellspacing="2" cellpadding="0">
											<tr><td><button id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
		         							<tr><td><button id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
						<td align="center" width="40" nowrap>
							姓名
						</td>
						<td align="left" width="100" nowrap>
							<input class="text4" type="text" name="select_name" value=""
								style="width: 100px; font-size: 10pt; text-align: left">
						</td>
						<td align="left" nowrap>
							&nbsp;
							<input type="button" name="br_return" value='查询' class="mybutton"
								onclick="change();">
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
								&nbsp;
								<input type="checkbox" name="aa" value="true" onclick="selAll()">
								&nbsp;
							</td>
							<logic:iterate id="element" name="dataAnalyseForm"
								property="fieldList" indexId="index">
								<logic:equal name="element" property="visible" value="true">
									<td align="center" class="TableRow" nowrap>
										&nbsp;
										<bean:write name="element" property="itemdesc" />
										&nbsp;
									</td>
								</logic:equal>
							</logic:iterate>
						</tr>
					</thead>
					<%
					int i = 0;
					%>
					<hrms:paginationdb id="element" name="dataAnalyseForm"
						sql_str="${dataAnalyseForm.strSql}" table="" where_str=""
						columns="${dataAnalyseForm.column}"
						order_by="${dataAnalyseForm.order}" pagerows="${dataAnalyseForm.pagerows}"
						page_id="pagination">
						<%
						if (i % 2 == 0) {
						%>
						<tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
							<%
							} else {
							%>
						
						<tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
							<%
									}
									i++;
							%>
							<bean:define id="nbase" name="element" property="nbase" scope="page"></bean:define>     
					        <bean:define id="a0100" name="element" property="a0100" scope="page"></bean:define> 
					        <bean:define id="start" name="element" property="begin_date" scope="page"></bean:define>     
					        <bean:define id="end" name="element" property="end_date" scope="page"></bean:define>
					        <bean:define id="fact_time" name="element" property="time_len" scope="page"></bean:define>
					        <input type="hidden" id="ID_<%=i %>" value="${nbase}`${a0100}`${start}`${end}"/>
							<td align="center" class="RecordRow" nowrap>
								&nbsp;
								<hrms:checkmultibox name="dataAnalyseForm"
									property="pagination.select" value="true" indexes="indexes" />
								&nbsp;
							</td>
							<logic:iterate id="info" name="dataAnalyseForm"
								property="fieldList">
								<logic:equal name="info" property="visible" value="true">
									<logic:equal name="info" property="itemtype" value="A">
										<logic:notEqual name="info" property="codesetid" value="0">
											<td align="left" class="RecordRow" nowrap>
												<logic:equal name="info" property="codesetid" value="UM">
													<hrms:codetoname codeid="UM" name="element"
														codevalue="${info.itemid}" codeitem="codeitem"
														scope="page" uplevel="${dataAnalyseForm.uplevel}" />  	      
          	                 						&nbsp;<bean:write name="codeitem"
														property="codename" />&nbsp;
												</logic:equal>
												<logic:notEqual name="info" property="codesetid" value="UM">
													<hrms:codetoname codeid="${info.codesetid}" name="element"
														codevalue="${info.itemid}" codeitem="codeitem"
														scope="page" />  	      
                             						&nbsp;<bean:write name="codeitem"
														property="codename" />&nbsp; 
                           						</logic:notEqual>
											</td>
										</logic:notEqual>
										<logic:equal name="info" property="codesetid" value="0">
											<td align="left" class="RecordRow" nowrap>
												&nbsp;
												<bean:write name="element" property="${info.itemid}"
													filter="false" />
											</td>
										</logic:equal>
									</logic:equal>
									<logic:equal name="info" property="itemtype" value="D">
										<td align="left" class="RecordRow" nowrap>
											<bean:write name="element" property="${info.itemid}"
												filter="false" />
											&nbsp;
										</td>
									</logic:equal>
									<logic:equal name="info" property="itemtype" value="N">
										<logic:notEqual name="info" property="itemid" value="q11xx">
											<td align="center" class="RecordRow" nowrap>
												<logic:equal name="info" property="itemid" value="time_len">
													<bean:write name="element" property="${info.itemid}" />分钟
												</logic:equal>
												<logic:notEqual name="info" property="itemid"
													value="time_len">
													<bean:write name="element" property="${info.itemid}" />
												</logic:notEqual>
											</td>
										</logic:notEqual>
										<logic:equal name="info" property="itemid" value="q11xx">
											<td align="center" class="RecordRow">
												
												<html:text name="element" property="${info.itemid}" maxlength="5" 
														size="8" styleClass="text" style="text-align:right;" 
														onkeypress="event.returnValue=IsDigit();" 
														onchange="checkvalue(this,'${a0100}','${nbase}','${start}','${end}','${fact_time}');" />
												
											</td>
										</logic:equal>
									</logic:equal>
								</logic:equal>
							</logic:iterate>
						</tr>
					</hrms:paginationdb>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="dataAnalyseForm" pagerows="${dataAnalyseForm.pagerows}"
								property="pagination" scope="page" refresh="true">
							</hrms:paginationtag>
						</td>
						<td align="right" class="tdFontcolor">
							<hrms:paginationdblink name="dataAnalyseForm" property="pagination" nameId="dataAnalyseForm" scope="page">
							</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="left" height="40px;" valign="bottom">
			&nbsp;
			<!--  
				<logic:equal name="dataAnalyseForm" property="haveOvertimeTemplates" value="true">
				<bean:message key="sys.label.template"/>
		        <hrms:optioncollection name="dataAnalyseForm" property="overtimeTemplates" collection="templates"/>
		          <html:select name="dataAnalyseForm" property="overtimeTemplateId" styleId="overtime_template" size="1">
		              <html:options collection="templates" property="dataValue" labelProperty="dataName"/>
		          </html:select>
		        </logic:equal>
		    -->
		    <% if ("".equals(sysSpState)) { %>
		    转为
			  <hrms:optioncollection name="dataAnalyseForm" property="overtimeSpList" collection="list"/>
	          <html:select name="dataAnalyseForm" property="overtimeSpState" styleId="spstate" size="1">
	              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	          </html:select>
	        <% } %>
          
				<input type="button" class="mybutton" onclick="addAppClass();" name="br_addapp" value="转成加班申请">
				<hrms:tipwizardbutton flag="workrest" target="il_body" formname="dataAnalyseForm"/>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
MusterInitData();

</script>