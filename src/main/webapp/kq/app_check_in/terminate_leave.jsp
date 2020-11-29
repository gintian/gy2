<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript">
    var returnValue = false;
    function validate(){
	 	var tag=true; 
	  	var i=0;
       	if($F('sels')=="#"){
            alert('请选择假期类型!');
            return false;
       	}          
       	var b_flag = "0";
  		var objs = document.all.item("radio");
  		for(var i = 0;i<objs.length;i++){
  			if(objs[i].checked==true){
  				b_flag = objs[i].value;
  			}
  		}
  		if(b_flag == '2'){
          	var app_obj=document.getElementById("app_date"); 
          	var hz1=document.getElementById("hz1");
  	    	var hz3=document.getElementById("hz3");
          	var start_date=hz1.value;
          	var end_date=hz3.value;
          	if(app_obj.value==""||start_date==""||end_date==""){
             	alert('<bean:message key="kq.rest.unull"/>');
             	tag=false;
             	return tag;
          	}else{
             	if(!isDate(app_obj.value,"yyyy-MM-dd HH:mm")){
                	alert("申请日期时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                	tag=false;
                	return tag;
             	}else if(!isDate(start_date,"yyyy-MM-dd HH:mm")){
                 	alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                 	tag=false;
                 	return tag;
             	}else if(!isDate(end_date,"yyyy-MM-dd HH:mm")){
                 	alert("结束时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                 	tag=false;
                 	return tag;
             	}else if(start_date.length>0&&end_date.length>0){
               		var c="起始时间不能大于或等于终止时间！";           
               		if(start_date>=end_date){
                  		alert(c);
                  		tag=false;
                  		return tag;
               		}
             	}
           	}  
  		}
       	getTime('','${appForm.table }');
       	if(!returnValue)
       		return false;
       	if(trim($F('q1507'))==""){
            alert('请填写销假事由!');
            return false;
       	}
		return tag; 
    }       
    function approve(){
       if(validate()){
           //appForm.action="/kq/app_check_in/cancel_app.do?b_approve=link&smflag=03";
           appForm.action="/kq/app_check_in/fugle_posture.do?b_approve=link&smflag=03";
           appForm.submit();
       }
    }
    
    function init(table){
		var b_flag = "0";
		var objs = document.all.item("radio");
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		var tr1 = document.all.item("date_scope");
		var tr2 = document.all.item("time_scope");
		var z1 = document.getElementById("z1");
		var z3 = document.getElementById("z3");
		var hz1=document.getElementById("hz1");
	    var hz3=document.getElementById("hz3");
		if(b_flag=="0"){
    		tr1.style.display="";
    		tr2.style.display="none";
    		z1.style.display = "";
    		z3.style.display = "";
    		hz1.style.display = "none";
	    	hz3.style.display = "none";
		} else if (b_flag=="1"){
    		tr1.style.display="none";
    		tr2.style.display="";
    		z1.style.display = "";
    		z3.style.display = "";
    		hz1.style.display = "none";
	    	hz3.style.display = "none";
		} else if(b_flag=="2"){
    		tr1.style.display="none";
    		tr2.style.display="none";
    		z1.style.display = "none";
    		z3.style.display = "none";
    		hz1.style.display = "";
	    	hz3.style.display = "";
		}
		getTime(1, table);
	}
	//限制天数为整数 
    function IsDigit(obj) 
    { 
		if(obj)
    	  obj.value=obj.value.replace(/[^0-9]+/,''); 
    }
    //限制小时的输入 
      function IsDigitNegative()
    {
      return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
    }
	
	function getTime(reMsg, table){
    	var q1501 = document.getElementById("q1501").value;
    	var q1503 = document.getElementById("q1503").value;
	    var hashvo=new ParameterSet();     
	    hashvo.setValue("q1501",q1501);
	    hashvo.setValue("q1503",q1503);
	    hashvo.setValue("tableflag",table);
    	var b_flag = "0";
		var objs = document.all.item("radio");
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		hashvo.setValue("app_way",b_flag);
		if(!reMsg){
			hashvo.setValue("reMsg",reMsg);
		}else{
			hashvo.setValue("reMsg",reMsg);
		}
		
		if(b_flag == "0"){
			var date_count = document.getElementById("date_count").value;
			if(!date_count || !checkIsNum(date_count)){
				returnValue = false;
				alert(PLEASE_INPUT_CORRECT_DIGIT);
				document.getElementById("date_count").value = '1';
				return false;
			}
			hashvo.setValue("date_count",date_count);
		}else if(b_flag == "1"){
			var time_count = document.getElementById("hr_count").value;
			if(!time_count || !checkIsNum(time_count)){
				returnValue = false;
				alert(PLEASE_INPUT_CORRECT_DIGIT);
				document.getElementById("hr_count").value = '1';
				return false;
			}
			hashvo.setValue("time_count",time_count);
		}else if(b_flag == "2"){
			var z1 = document.getElementById("hz1").value;
			var z3 = document.getElementById("hz3").value;
			if(!isDate(z1,"yyyy-MM-dd HH:mm") || !isDate(z3,"yyyy-MM-dd HH:mm")){
				returnValue = false;
			 	return false;
			}
			hashvo.setValue("z1",z1);
			hashvo.setValue("z3",z3);
		}
    	var request=new Request({method:'post',onSuccess:setStartTime,functionId:'1510020027'},hashvo);
    } 	
    
function setStartTime(outparamters){
   	var st_date = outparamters.getValue("z1");
   	var end_date = outparamters.getValue("z3");
   	var err = outparamters.getValue("err");
   	var app_way = outparamters.getValue("app_way");
   	
	if(app_way == "2"){
		var hz1_obj=document.getElementById("hz1");
   		var hz3_obj=document.getElementById("hz3");
		hz1_obj.value =st_date;
		hz3_obj.value = end_date;
		
		
	}else{
	    var z1_obj=document.getElementById("z1");
   		var z3_obj=document.getElementById("z3");
		z1_obj.value = st_date;
		z3_obj.value = end_date;
	}
   	if(err != ""){
   		returnValue = false;
   	 document.getElementById("date_count").value = '1';
   		alert(err);
   	}else{
   		document.getElementById("sz1").value = st_date;
   		document.getElementById("sz3").value = end_date;
    	returnValue = true;
    }
}
    
function hback(table){
	appForm.action="/kq/app_check_in/all_app_data.do?b_search2=link&table=" + table +"&dotflag=1";
	appForm.submit();    
}
</script>
<script language="javascript">
   var outObject;
   var weeks="";
   var feasts ="";
   var turn_dates="";
   var week_dates="";
   function getdate(tt,flag)
   {
     outObject=tt;     
     var hashvo=new ParameterSet();     
     hashvo.setValue("date",tt.value);  
     hashvo.setValue("flag",flag);     		
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1510010020'},hashvo);
   }
   function showSelect(outparamters)
   { 
     var tes=outparamters.getValue("re_date");     
     outObject.value=tes;
   }
   function getKqCalendarVar()
   {
     var hashvo=new ParameterSet();       		
     var request=new Request({method:'post',onSuccess:setKqCalendarVar,functionId:'15388800008'},hashvo);
   }
   function setKqCalendarVar(outparamters)
   {
       weeks=outparamters.getValue("weeks");  
       feasts=outparamters.getValue("feasts");  
       turn_dates=outparamters.getValue("turn_dates");  
       week_dates=outparamters.getValue("week_dates");  
   }
</script>
<html:form action="/kq/app_check_in/fugle_posture"
	onsubmit="return validate()">
	<br>
	<br>
	<logic:equal name="appForm" property="table" value="Q11">
		<html:hidden styleId="q1501" name="appForm" property="cancelvo.string(q1101)" />
	</logic:equal>
	<logic:equal name="appForm" property="table" value="Q13">
		<html:hidden styleId="q1501" name="appForm" property="cancelvo.string(q1301)" />
	</logic:equal>
	<logic:equal name="appForm" property="table" value="Q15">
		<html:hidden styleId="q1501" name="appForm" property="cancelvo.string(q1501)" />
	</logic:equal>
	
	<table width="400" border="0" cellpadding="1" cellspacing="0"
		align="center">
		<tr>
			<td align=center class="TableRow" style="border-bottom: 0px;">
				<logic:equal name="appForm" property="table" value="Q11">
					撤销加班申请
				</logic:equal>
				<logic:equal name="appForm" property="table" value="Q13">
					撤销公出申请
				</logic:equal>
				<logic:equal name="appForm" property="table" value="Q15">
					销假申请
				</logic:equal>
				
			</td>
		</tr>
		<tr>
			<td width="100%" align=center class="RecordRow">
				<table>
					<tr>
						<td align="right" class="tdFontcolor" nowrap>
							<logic:equal name="appForm" property="table" value="Q11">
								加班类型
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								公出类型
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								请假类型
							</logic:equal>
						</td>
						<td align="left" class="tdFontcolor" nowrap>
							<hrms:optioncollection name="appForm" property="selist"
								collection="list" />
							<logic:equal name="appForm" property="table" value="Q11">
								<html:select styleId="q1503" name="appForm"
									property="cancelvo.string(q1103)" size="1" disabled="true">
									<html:options collection="list" property="dataValue"
										labelProperty="dataName" />
								</html:select>
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								<html:select styleId="q1503" name="appForm"
									property="cancelvo.string(q1303)" size="1" disabled="true">
									<html:options collection="list" property="dataValue"
										labelProperty="dataName" />
								</html:select>
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								<html:select styleId="q1503" name="appForm"
									property="cancelvo.string(q1503)" size="1" disabled="true">
									<html:options collection="list" property="dataValue"
										labelProperty="dataName" />
							</html:select>
							</logic:equal>
						</td>
					</tr>
					<tr>
						<td align="right" nowrap="nowrap">
							<bean:message key="kq.class.applyscope" />
							:
						</td>
						<td>
							<html:radio name="appForm" property="radio" onclick="init('${appForm.table }');"
								value="0"></html:radio>
							<bean:message key="kq.shift.relief.day" />
							<html:radio name="appForm" property="radio" onclick="init('${appForm.table }');"
								value="1"></html:radio>
							<bean:message key="kq.class.hour" />
							<html:radio name="appForm" property="radio" onclick="init('${appForm.table }');"
								value="2"></html:radio>
							<bean:message key="kq.time.space" />
						</td>
					</tr>
					<tr>
						<td align="right" class="tdFontcolor" nowrap>
							申请时间:
						</td>
						<td align="left" class="tdFontcolor" nowrap>
							<logic:equal name="appForm" property="table" value="Q11">
								<html:text name="appForm" styleId="app_date" disabled="true"
									property="cancelvo.string(q1105)" size="20" maxlength="20"
									styleClass="TEXT4" />
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								<html:text name="appForm" styleId="app_date" disabled="true"
									property="cancelvo.string(q1305)" size="20" maxlength="20"
									styleClass="TEXT4" />
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								<html:text name="appForm" styleId="app_date" disabled="true"
									property="cancelvo.string(q1505)" size="20" maxlength="20"
									styleClass="TEXT4" />
							</logic:equal>
						</td>
					</tr>
					<tr id="date_scope">
						<td align="right" class="tdFontcolor">
							<logic:equal name="appForm" property="table" value="Q11">
								撤销加班天数
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								撤销公出天数
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								销假天数
							</logic:equal>
						</td>
						<td nowrap="nowrap">
							<html:text name="appForm" property="date_count" size="10" maxlength="4"
								onchange="getTime('','${appForm.table }');" onkeyup="IsDigit(this);" style="text-align:right"
								styleClass="TEXT4"></html:text>
							(
							<bean:message key="kq.rest.day" />
							)
						</td>
					</tr>
					<tr id="time_scope">
						<td align="right" class="tdFontcolor">
							<logic:equal name="appForm" property="table" value="Q11">
								撤销加班小时
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								撤销公出小时
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								销假小时
							</logic:equal>
						</td>
						<td nowrap="nowrap">
							<html:text name="appForm" property="hr_count" size="10" maxlength="4"
								onchange="getTime('','${appForm.table }');" onkeypress="event.returnValue=IsDigitNegative();" style="text-align:right"
								styleClass="TEXT4"></html:text>
							<bean:message key="kq.class.hour" />
						</td>
					</tr>
					<tr>
						<td align="right" class="tdFontcolor" nowrap>
							起始时间:
						</td>
						<td align="left" class="tdFontcolor" nowrap>
							<logic:equal name="appForm" property="table" value="Q11">
								<html:text name="appForm" styleId="z1" disabled="true"
									property="cancelvo.string(q11z1)" size="20" maxlength="20"
									onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
									styleClass="TEXT4" />
								<html:hidden name="appForm" styleId="sz1" property="cancelvo.string(q11z1)"/>
								<bean:define id="hz1v" name="appForm" property="cancelvo.string(q11z1)"></bean:define>
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								<html:text name="appForm" styleId="z1" disabled="true"
									property="cancelvo.string(q13z1)" size="20" maxlength="20"
									onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
									styleClass="TEXT4" />
								<html:hidden name="appForm" styleId="sz1" property="cancelvo.string(q13z1)"/>
								<bean:define id="hz1v" name="appForm" property="cancelvo.string(q13z1)"></bean:define>
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								<html:text name="appForm" styleId="z1" disabled="true"
									property="cancelvo.string(q15z1)" size="20" maxlength="20"
									onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
									styleClass="TEXT4" />
								<html:hidden name="appForm" styleId="sz1" property="cancelvo.string(q15z1)"/>
								<bean:define id="hz1v" name="appForm" property="cancelvo.string(q15z1)"></bean:define>
							</logic:equal>
							<html:text name="appForm" styleId="hz1"
								property="scope_start_time" value="${hz1v}" size="20"
								maxlength="20"
								onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
								styleClass="TEXT4" />
						</td>
					</tr>
					<tr>
						<td align="right" class="tdFontcolor" nowrap>
							<logic:equal name="appForm" property="table" value="Q11">
								撤销加班时间
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								撤销公出时间
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								销假时间
							</logic:equal>
						</td>
						<td align="left" class="tdFontcolor" nowrap>
							<logic:equal name="appForm" property="table" value="Q11">
								<html:text name="appForm" styleId="z3" disabled="true"
									property="cancelvo.string(q11z3)" size="20" maxlength="20"
									onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
									styleClass="TEXT4" />
								<html:hidden name="appForm" styleId="sz3" property="cancelvo.string(q11z3)"/>
								<bean:define id="hz3v" name="appForm" property="cancelvo.string(q11z3)"></bean:define>
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								<html:text name="appForm" styleId="z3" disabled="true"
									property="cancelvo.string(q13z3)" size="20" maxlength="20"
									onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
									styleClass="TEXT4" />
								<html:hidden name="appForm" styleId="sz3" property="cancelvo.string(q13z3)"/>
								<bean:define id="hz3v" name="appForm" property="cancelvo.string(q13z3)"></bean:define>
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								<html:text name="appForm" styleId="z3" disabled="true"
									property="cancelvo.string(q15z3)" size="20" maxlength="20"
									onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
									styleClass="TEXT4" />
								<html:hidden name="appForm" styleId="sz3" property="cancelvo.string(q15z3)"/>
								<bean:define id="hz3v" name="appForm" property="cancelvo.string(q15z3)"></bean:define>
							</logic:equal>
							<html:text name="appForm" styleId="hz3" property="scope_end_time"
								value="${hz3v }" size="20" maxlength="20"
								onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
								styleClass="TEXT4" />
						</td>
					</tr>
					<tr>
						<td align="right" class="tdFontcolor" nowrap>
							<logic:equal name="appForm" property="table" value="Q11">
								撤销加班事由
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								撤销公出事由
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								销假事由
							</logic:equal>
						</td>
						<td align="left" class="tdFontcolor" nowrap>
							<logic:equal name="appForm" property="table" value="Q11">
								<html:textarea name="appForm" styleId="q1507" property='cancelvo.string(q1107)' cols="35" rows="4" styleClass="text5" />
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								<html:textarea name="appForm" styleId="q1507" property='cancelvo.string(q1307)' cols="35" rows="4" styleClass="text5" />
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								<html:textarea name="appForm" styleId="q1507" property='cancelvo.string(q1507)' cols="35" rows="4" styleClass="text5" />
							</logic:equal>
						</td>
					</tr>
					<tr>
						<td align="right" class="tdFontcolor" nowrap>
							审批结果:
						</td>
						<td align="left" class="tdFontcolor" nowrap>
							<logic:equal name="appForm" property="table" value="Q11">
								<hrms:codetoname codeid="30" name="appForm" codevalue='cancelvo.string(q11z0)' codeitem="codeitem" />
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								<hrms:codetoname codeid="30" name="appForm" codevalue='cancelvo.string(q13z0)' codeitem="codeitem" />
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								<hrms:codetoname codeid="30" name="appForm" codevalue='cancelvo.string(q15z0)' codeitem="codeitem" />
							</logic:equal>
							<bean:write name="codeitem" property="codename" />
						</td>
					</tr>
					<tr>
						<td align="right" class="tdFontcolor" nowrap>
							审批状态:
						</td>
						<td align="left" class="tdFontcolor" nowrap>
							<logic:equal name="appForm" property="table" value="Q11">
								<hrms:codetoname codeid="23" name="appForm" codevalue='cancelvo.string(q11z5)' codeitem="codeitem" />
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								<hrms:codetoname codeid="23" name="appForm" codevalue='cancelvo.string(q13z5)' codeitem="codeitem" />
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								<hrms:codetoname codeid="23" name="appForm" codevalue='cancelvo.string(q15z5)' codeitem="codeitem" />
							</logic:equal>
							<bean:write name="codeitem" property="codename" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table align="center">
					<tr class="list3">
						<td align="center" colspan="4" style="height: 35px;">
							<logic:equal name="appForm" property="table" value="Q11">
								<logic:notEqual name="appForm" property="cancelvo.string(q11z5)" value="03">
									<input type="button" name="btnreturn" value='<bean:message key="button.approve"/>'
										onclick="approve();" class="mybutton">
								</logic:notEqual>
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q13">
								<logic:notEqual name="appForm" property="cancelvo.string(q13z5)" value="03">
									<input type="button" name="btnreturn" value='<bean:message key="button.approve"/>'
										onclick="approve();" class="mybutton">
								</logic:notEqual>
							</logic:equal>
							<logic:equal name="appForm" property="table" value="Q15">
								<logic:notEqual name="appForm" property="cancelvo.string(q15z5)" value="03">
									<input type="button" name="btnreturn" value='<bean:message key="button.approve"/>'
										onclick="approve();" class="mybutton">
								</logic:notEqual>
							</logic:equal>
							<bean:define id="tableName" name="appForm" property="table"></bean:define>
							<input type="button" name="btnreturn"
								value='<bean:message key="kq.emp.button.return"/>'
								onclick="hback('<%=tableName.toString() %>');" class="mybutton">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>

<script type="text/javascript">
<!--
init('${appForm.table }');
//-->
</script>

