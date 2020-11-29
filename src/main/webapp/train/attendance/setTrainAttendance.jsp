<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript"><!--
 	function retrunSans(){
		trainAtteForm.action="/train/setparam/project.do?b_query=link";
		trainAtteForm.target="il_body";
		trainAtteForm.submit();
	}
	function saveOk(){
		var card_no=document.getElementById("card_no").value;
		var leave_early=document.getElementById("leave_early").value;
		var late_for=document.getElementById("late_for").value;
		var hashvo=new ParameterSet(); 
		hashvo.setValue("card_no",card_no); 
		hashvo.setValue("leave_early",leave_early); 
		hashvo.setValue("late_for",late_for); 
    	var request=new Request({method:'post',onSuccess:showOk,functionId:'2020020232'},hashvo);
	}
	function showOk(outparamters){
		if(outparamters)
			alert("参数设置成功！");
	}  
--></script>
<html:form action="/train/attendance/setTrainAttendance">
<br>
<table width="500" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
            	培训考勤参数
            </td>            	        	        	        
           </tr>
   	  </thead>
	<tr>
		<td class="RecordRow" style="vertical-align: top;" nowrap>
			<table height="280" border="0" cellpadding="0" cellspacing="0" style="vertical-align: top;margin-left: 30px;">
				<tr>
					<td height="20">&nbsp;</td>
				</tr>
				<tr>
					<td height="60">
						培训卡号
					  <hrms:optioncollection name="trainAtteForm" property="attendancelist" collection="list"/> 
     	              <html:select name="trainAtteForm" property="card_no">
							<html:options collection="list" property="dataValue"
								labelProperty="dataName" />
						</html:select>
					</td>
				</tr>
				<tr>
					<td height="30">
						上课
						<input type="text" name="leave_early" class="TEXT6" onpropertychange='if(/[^\d*]/.test(this.value)) this.value=this.value.replace(/[^\d*]/,"")' value="${trainAtteForm.leave_early }" style="width:40px;text-align: right;" maxlength="3"/>
						分钟后签到算迟到
					</td>
				</tr>
				<tr>
					<td height="30">
						下课
						<input type="text" name="late_for" class="TEXT6" onpropertychange='if(/[^\d*]/.test(this.value)) this.value=this.value.replace(/[^\d*]/,"")' value="${trainAtteForm.late_for }" style="width:40px;text-align: right;" maxlength="3"/>
						分钟前签退算早退
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td align="center" class="RecordRow" nowrap style="height:35px;">
               <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="saveOk();">
               <!-- <input type="button" name="breturn" value='<bean:message key="button.return"/>' class="mybutton" onclick="retrunSans();"> -->
        </td>
	</tr>
</table>
</html:form>
