<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.actionform.competencymodal.PersonPostMatchingForm" %>
				 
<%
PersonPostMatchingForm personPostMatchingForm=(PersonPostMatchingForm)session.getAttribute("personPostMatchingForm");
String isShowPercentVal = personPostMatchingForm.getIsShowPercentVal();
String degreeflag = personPostMatchingForm.getDegreeflag();
%>
<script type="text/javascript" src="/competencymodal/person_post_matching/postMatching.js"></script>

<script language='javascript' >

//返回页面
function go_back()
{
	document.personPostMatchingForm.action="${personPostMatchingForm.returnURL}"; 	    		
	document.personPostMatchingForm.submit(); 	 
}	
function showLevel()
{
	if(document.getElementById('isShowLevel').checked)
		personPostMatchingForm.isShowPercentVal.value="2";
	else
		personPostMatchingForm.isShowPercentVal.value="0";
	document.personPostMatchingForm.action="/competencymodal/person_post_matching/person_post_matching.do?b_init=init";
	document.personPostMatchingForm.submit();
}
</script>
<body oncontextmenu='showMenu();return false;'>
<%
  int i=0;
 %>
<html:form action="/competencymodal/person_post_matching/person_post_matching">  

<html:hidden name="personPostMatchingForm" property="planId" />
<html:hidden name="personPostMatchingForm" property="object_id" />
<html:hidden name="personPostMatchingForm" property="postCode" />
<html:hidden name="personPostMatchingForm" property="postScopeDesc" />
<html:hidden name="personPostMatchingForm" property="postScope" />

<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td colspan="2" align="left" style="position:relative;">
	<span style='position:relative;top:-3px'>范围&nbsp;</span><input style='line-height:normal;' id="postScopevID" type="text" name="postScope.viewvalue" value="${personPostMatchingForm.postScopeDesc}" onchange="changeDegree();" class="inputtext"/>
	<span style="position:absolute;top:5px;">
<img src="/images/code.gif" onclick='selectTree("@K","postScope.viewvalue");' border="0" style="cursor:hand"/>
</span>
<input type="hidden" name="postScope.value" id="postScopeID" value="${personPostMatchingForm.postScope}"/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<div style="display:none;">
 匹配方案:<hrms:optioncollection name="personPostMatchingForm" property="macthingDegreeList" collection="list" />
						 <html:select name="personPostMatchingForm" property="matchingDegree" size="1" style="width:150px;" onchange="changeDegree();">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
&nbsp;匹配度等级:<hrms:optioncollection name="personPostMatchingForm" property="degreeGradeList" collection="list" />
						 <html:select name="personPostMatchingForm" property="degreeGradeId" size="1"  onchange="changeDegree();">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
</div>
&nbsp;<input type='button' value='<bean:message key="kq.search_feast.back"/>' onclick='go_back()' class="mybutton" />
</td>
</tr>
<tr>
<td width="100%" align="left" valign="top" nowrap>
<table width="298" align="left" cellspacing="0"  cellpadding="0">
	
<tr><td width="40%" align="center" valign="top" nowrap>
<table cellspacing="0"  cellpadding="0" width="100%">
	<tr><td width="100%" align="center" valign="top" nowrap colspan="2">&nbsp;</td></tr>
<tr>
<td align="center" width="100%" colspan="2">
<table cellspacing="0"  cellpadding="0" width="100%" class="ListTable">
	<tr>
		<%
			 	FieldItem fielditem = DataDictionary.getFieldItem("E0122");			  				 	 				 
		%>
		<logic:equal name="personPostMatchingForm" property="objType" value="2">
			<td align="center" class="TableRow" width="80" nowrap><bean:message key="b0110.label"/></td>
			<td align="center" class="TableRow" width="100" nowrap><%=fielditem.getItemdesc()%></td>
			<td align="center" class="TableRow" width="100" nowrap><bean:message key="e01a1.label"/></td>
			<td align="center" class="TableRow" width="80" nowrap><bean:message key="hire.employActualize.name"/></td>
		</logic:equal>	
		<logic:equal name="personPostMatchingForm" property="objType" value="1">
			<td align="center" class="TableRow" width="200" nowrap><bean:message key="e01a1.label"/></td>
		</logic:equal>
		<%if("4".equals(degreeflag)){ %>
		<td align="center" class="TableRow" width="80" nowrap>总分</td>
		<td align="center" class="TableRow" width="80" nowrap>匹配度</td>
		<td align="center" class="TableRow" width="80" nowrap>匹配结果</td>
		<%}else if("5".equals(degreeflag)){ %>
		<td align="center" class="TableRow" width="80" nowrap>总分</td>
		<td align="center" class="TableRow" width="80" nowrap>等级</td>
		<%} %>
		
	</tr>
  
  <hrms:extenditerate id="element" name="personPostMatchingForm" property="matchingListForm.list" indexes="indexes"  pagination="matchingListForm.pagination" pageCount="21" scope="session">
   <logic:equal name="personPostMatchingForm" property="objType" value="2">
     <logic:equal value="${personPostMatchingForm.object_id}" name="element" property="codeitemid">     
			 <tr id="<bean:write name="element" property="codeitemid"/>" onclick='queryObject("${personPostMatchingForm.objE01A1}","<bean:write name="element" property="codeitemid"/>","${personPostMatchingForm.planId}","${personPostMatchingForm.objType}");' style="background-color:#FFF8D2;">
		 </logic:equal>	
		 <logic:notEqual value="${personPostMatchingForm.object_id}" name="element" property="codeitemid">
			 <tr id="<bean:write name="element" property="codeitemid"/>" onclick='queryObject("${personPostMatchingForm.objE01A1}","<bean:write name="element" property="codeitemid"/>","${personPostMatchingForm.planId}","${personPostMatchingForm.objType}");' style="background-color:white;">
		 </logic:notEqual>     
   </logic:equal>
   <logic:equal name="personPostMatchingForm" property="objType" value="1">
     <logic:equal value="${personPostMatchingForm.postCode}" name="element" property="codeitemid">
			 <tr id="<bean:write name="element" property="codeitemid"/>" onclick='queryObject("<bean:write name="element" property="codeitemid"/>","${personPostMatchingForm.object_id}","${personPostMatchingForm.planId}","${personPostMatchingForm.objType}");' style="background-color:#FFF8D2;">
		 </logic:equal>	
		 <logic:notEqual value="${personPostMatchingForm.postCode}" name="element" property="codeitemid">
			 <tr id="<bean:write name="element" property="codeitemid"/>" onclick='queryObject("<bean:write name="element" property="codeitemid"/>","${personPostMatchingForm.object_id}","${personPostMatchingForm.planId}","${personPostMatchingForm.objType}");' style="background-color:white;">
     </logic:notEqual>
   </logic:equal> 
    <%if(i==0){ %>
    <input type="hidden" name="temp" id="temp_str" value="<bean:write name="element" property="codeitemid"/>"/>
    <%} %>
    
    <logic:equal name="personPostMatchingForm" property="objType" value="2">
			<td class="RecordRow" align="left" nowrap>&nbsp;<bean:write name='element' property="b0110"/></td>
			<td class="RecordRow" align="left" nowrap>&nbsp;<bean:write name='element' property="e0122"/></td>
			<td class="RecordRow" align="left" nowrap>&nbsp;<bean:write name='element' property="e01a1"/></td>
			<td class="RecordRow" align="left" nowrap>&nbsp;<bean:write name='element' property="a0101"/></td>
		</logic:equal>	
		<logic:equal name="personPostMatchingForm" property="objType" value="1">
			<td class="RecordRow" align="left" nowrap>&nbsp;<bean:write name='element' property="postname"/></td>
		</logic:equal> 	   
    <%if("4".equals(degreeflag)){ %>
    <td class="RecordRow" align="right" nowrap><bean:write name='element' property="score"/>&nbsp;</td>
    <td class="RecordRow" align="right" nowrap><bean:write name='element' property="degree"/>&nbsp;</td>
    <td class="RecordRow" align="left" nowrap>&nbsp;<bean:write name='element' property="degreedesc"/></td>
    <%}else if("5".equals(degreeflag)){ %>
    <td class="RecordRow" align="right" nowrap><bean:write name='element' property="score"/>&nbsp;</td>
    <td class="RecordRow" align="left" nowrap>&nbsp;<bean:write name='element' property="level"/></td>
    <%} %>
   </tr>
   <%i++; %>
   </hrms:extenditerate>
   <tr>
<logic:equal name="personPostMatchingForm" property="objType" value="2">
	<%if("5".equals(degreeflag)){ %>
	<td class="RecordRow" colspan="6">
	<%}else{ %>
	<td class="RecordRow" colspan="7">
	<%} %>
</logic:equal>	
<logic:equal name="personPostMatchingForm" property="objType" value="1">
	<%if("5".equals(degreeflag)){ %>
	<td class="RecordRow" colspan="3">
	<%}else{ %>
	<td class="RecordRow" colspan="4">
	<%} %>	
</logic:equal>
    <table  width="298" align="center">
		<tr>
		   <td valign="bottom" class="tdFontolor" nowrap>第
		   <bean:write name="personPostMatchingForm" property="matchingListForm.pagination.current" filter="true"/>
		   页
		   共
		   <bean:write name="personPostMatchingForm" property="matchingListForm.pagination.count" filter="true"/>
		   条
		   共
		   <bean:write name="personPostMatchingForm" property="matchingListForm.pagination.pages" filter="true"/>
		   页
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="personPostMatchingForm" property="matchingListForm.pagination" nameId="matchingListForm" propertyId="matchingListProperty">
		   </hrms:paginationlink>
		   </td>
		</tr> 
</table>
</td>

</tr>
<tr>
<td style="height:35px">



</td>
</tr>
</table>
</td>
</tr>
</table>
</td>

<td width="60%" valign="top" align="center" nowrap>
<iframe src="" width="650" height="600" scrolling="auto" frameborder="0" name="main"></iframe>	
</td>
</tr>
</table>
 <input type='hidden' name='isShowPercentVal'  value="${personPostMatchingForm.isShowPercentVal}" />
<div id='menu_' onblur='hiddenElement()'  style="background:#ffffff;width:130px;height:100px " class="complex_border_color" >
<table>	
<tr><td><input type='checkbox' id='isShowLevel'  onclick='showLevel()' <%=(isShowPercentVal.equals("2")?"checked":"")%>   /></td><td><bean:message key="jx.analyse.levelval"/></td></tr><!-- 按级别显示 -->
</table>
</div>
<script language='javascript'>
	document.getElementById('menu_').style.display="none";
</script>
</html:form>
</body>
<script language='javascript' >

var objType = "${personPostMatchingForm.objType}";	
var tt = document.getElementById("temp_str");
if(tt)
{
   var value = tt.value;
   var objE01A1 = "${personPostMatchingForm.objE01A1}";
   var object_id = "${personPostMatchingForm.object_id}";
   var plan_id = "${personPostMatchingForm.planId}";
   
   if(objType=="1")
   	  queryObject(value,object_id,plan_id,objType);
   else if(objType=="2")
   	  queryObject(objE01A1,value,plan_id,objType);
}

</script>