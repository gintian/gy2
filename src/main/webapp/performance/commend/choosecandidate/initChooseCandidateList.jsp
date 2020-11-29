<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.commend.choosecandidate.ChooseCandidateForm,org.apache.commons.beanutils.LazyDynaBean,java.util.*"%>
<script type="text/javascript">
<!--
function sub(){
if(confirm("确认提交，提交后只能查看，不能修改")){
var num=0;
var count="${chooseCandidateForm.ctrl_param}";
var ids="";
var choose = document.getElementsByName("choose_per");
if(choose.length==null||choose.length==0){
return;
}
for(var j=0;j<choose.length;j++){
if(choose[j].checked){
ids+=","+choose[j].value;
num++;
}
}

if(num>count){
alert("推荐的人数不能大于规定的人数 ["+count+"]");
return;
}
if(num==0){
alert("请选择候选人或选择弃权");
return;
}
chooseCandidateForm.action="/performance/commend/choosecandidate/initChooseCandidateList.do?b_save=save&ids="+ids+"&opt=select";
chooseCandidateForm.submit();
}else{
return;
}
}
function changeCandidate(){
var v=chooseCandidateForm.p0201.value;
 chooseCandidateForm.action="/performance/commend/choosecandidate/initChooseCandidateList.do?b_change=change&p0201="+v;
 chooseCandidateForm.submit();
}
function disclaim(){
if(confirm("确定弃权吗")){
chooseCandidateForm.action="/performance/commend/choosecandidate/initChooseCandidateList.do?b_save=save&opt=disselect";
chooseCandidateForm.submit();
}
else{
return;
}
}

//-->
</script>
<html:form action="/performance/commend/choosecandidate/initChooseCandidateList">	
<br>
<br>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td align="center" class="RecordRow" width="10%" nowrap >
<bean:message key="label.commend.commend"/>
</td><!-- 【5799】干部考察：点击后备推荐,显示的表格内容表格线太粗了 jingq upd 2015.01.13 -->
<td  align="center" class="RecordRow" nowrap style="padding-top:2px;padding-bottom:2px;">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<tr>
	<td colspan="3" width="100%"  class="RecordRow" >
	<logic:equal name="chooseCandidateForm" property="onlyOne" value="0" ><bean:message key="label.commend.norecord"/></logic:equal>

	
	<logic:equal name="chooseCandidateForm" property="onlyOne" value="1">
	 
	<bean:write name="chooseCandidateForm" property="p0203"/>
	<input type="hidden" name="p0201" value="<bean:write name="chooseCandidateForm" property="p0201"/>"/>
	<br><bean:message key="label.commend.maxcommend"/><bean:write name="chooseCandidateForm" property="ctrl_param"/><bean:message key="label.commend.person"/>
	</logic:equal>
	
	
	<logic:equal name="chooseCandidateForm" property="onlyOne" value="2">
	<hrms:optioncollection name="chooseCandidateForm" property="commendList" collection="list" />
						 <html:select name="chooseCandidateForm" property="p0201" size="1" onchange="changeCandidate();">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
				        <br>
				       <bean:message key="label.commend.maxcommend"/><bean:write name="chooseCandidateForm" property="ctrl_param"/><bean:message key="label.commend.person"/>
	</logic:equal>

	</td>
	</tr>
	</table>
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:2px;">
	<tr class="TableRow">
	<td colspan="1"  align="center" class="TableRow" nowrap>
	<bean:message key="label.serialnumber"/>
	</td>
	<td colspan="1" align="center" class="TableRow" nowrap>
	<bean:message key="label.commend.p_name"/>
	</td>
	<td colspan="1"  align="center" class="TableRow" nowrap>
	单位
	</td>
	<td colspan="1" align="center" class="TableRow" nowrap>
	部门
	</td>
	<logic:equal name="chooseCandidateForm" property="isNull" value="no">
	<td colspan="1" align="center" class="TableRow" nowrap>
推荐职务
	</td>
	</logic:equal>
	 <logic:equal name="chooseCandidateForm" property="isSubmit" value="2">
	<TD colspan="1" align="center" class="TableRow" nowrap>
	<bean:message key="label.commend.iscommend"/>
	</TD>
	</logic:equal>
	</tr>
	<% int n=0;%>
  <logic:iterate id="element" name="chooseCandidateForm" property="candidateList" offset="0">
   <tr>
   <td align="right" class="RecordRow" nowrap>
   <%=n+1%></td>
   <td align="left" class="RecordRow" nowrap>
   <bean:write name="element" property="a0101"/>
   </td>
   <td align="left" class="RecordRow" nowrap>
   <bean:write name="element" property="b0110"/>
   </td>
   <td align="left" class="RecordRow" nowrap>
   <bean:write name="element" property="e0122"/>
   </td>
   <logic:equal name="chooseCandidateForm" property="isNull" value="no">
    <td align="left" class="RecordRow" nowrap>
   <bean:write name="element" property="commend_field"/>
   </td>
   </logic:equal>
   <logic:equal name="chooseCandidateForm" property="isSubmit" value="2">
  <logic:equal name="element" property="choosed" value="0">
  <td align="center" class="RecordRow" nowrap>

  <input type="checkbox" name="choose_per" value="<bean:write name="element" property="p0300"/>"/>
   </td>
  </logic:equal>
  <logic:equal name="element" property="choosed" value="1">
  <td align="center" class="RecordRow" nowrap>
  <input type="checkbox" name="choose_per" value="<bean:write name="element" property="p0300"/>"/>
   </td>
  </logic:equal>
  </logic:equal>
  <!--  
   <logic:notEqual name="chooseCandidateForm" property="isSubmit" value="2">
  <logic:equal name="element" property="choosed" value="0">
  <td align="center" class="RecordRow" nowrap>

  <input type="checkbox" name="choose_per" value="<bean:write name="element" property="p0300"/>" disabled="false"/>
   </td>
  </logic:equal>
  <logic:equal name="element" property="choosed" value="1">
  <td align="center" class="RecordRow" nowrap>
  <input type="checkbox" name="choose_per" value="<bean:write name="element" property="p0300"/>" disabled="false"/>
   </td>
  </logic:equal>
  </logic:notEqual>
  -->
  
  
    </tr>
  <% n++;%>

  </logic:iterate>
  <input type='hidden' name='codesetid' value="${chooseCandidateForm.codesetid}">
</table>
<logic:equal name="chooseCandidateForm" property="isSubmit" value="2">
<logic:notEqual name="chooseCandidateForm" property="size" value="0">

<table border='0' width="100%" cellspacing="0"  align="center" cellpadding="0">
	<tr><td height="35px;">
	
	<button type="button" name="" class="mybutton" onclick="sub();"><bean:message key="button.submit"/></button>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<button type="button" name="disc" class="mybutton" onclick="disclaim();">弃权</button>
	
	</td></tr>
	</table>
</logic:notEqual>
	</logic:equal>
	</td>
	</tr>
</table>	
</html:form>
