<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">

 function allSelectOptions(obj)
 {
     var arr=document.getElementsByName("choose");
     if(arr)
     {
       for(var i=0;i<arr.length;i++)
       {
         if(obj.checked)
         {
            arr[i].checked=true;
         }
         else
         {
            arr[i].checked=false;
         }
       }
     }
 }
 function del(){
	 var checkbox = document.getElementsByName("choose");
	 var ids='',i;
	 for(i=0;i<checkbox.length;i++){
		 if(checkbox[i].checked==true){
			 ids += "'" + checkbox[i].value + "',";
		}
	 }

	 if(ids==null||ids.length<1){
		 alert(TRAIN_COURSE_ABILITY_DEL);
		 return;
	 }
	 
	 if(!confirm(TRAIN_COURSE_ABILITY_DELETE))
		 return;
	 
	 var hashvo = new ParameterSet();
	 hashvo.setValue("ids",ids);
	 hashvo.setValue("r5000","${courseForm.r5000}");
	 var request=new Request({method:'post',asynchronous:false,onSuccess:isParent,functionId:'20200130012'},hashvo);
 }
	function isParent(outparamters){
		if(outparamters){
		   var temp1=outparamters.getValue("flag");
		   if(flag = "yes"){
			   courseForm.action="/train/resource/course/showability.do?b_query=link&r5000=${courseForm.r5000}";
			   courseForm.submit();
		   }
	   	}
	} 
 function returnback(){
	 location.href="/train/resource/course.do?b_query=link&a_code=${courseForm.a_code}"
 }

</script>

<html:form action="/train/resource/course/showability">

<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">

<tr>
<td colspan="2" width='100%'>
<table width="100%" border="0" cellspacing="0" id="tb" align="center" cellpadding="0" class="ListTable">
   	  <thead>
		 <tr>
			 <td align="center" class="TableRow">
			 	<input type="checkbox" name="chk" value="1" onclick="allSelectOptions(this);"/>
			 </td>
			 <td align="center" class="TableRow" nowrap>
			  	<bean:message key="kh.field.num"/>
			 </td>
			 <td align="center" class="TableRow" nowrap>
			  	<bean:message key="kh.field.classname"/>
			 </td>			 
			 
			 <td align="center" class="TableRow" nowrap>
			  	<bean:message key="kh.field.flag"/>
			 </td>
		 </tr>
	 </thead>
	 <% int i=0; %>
	 <hrms:extenditerate id="element" name="courseForm" property="abilitylistForm.list" indexes="indexes"  pagination="abilitylistForm.pagination" pageCount="2000" scope="session">
		 <%if(i%2==0){ %>
	     <tr class="trShallow" id="<bean:write name="element" property="point_id"/>">
	     <%} else { %>
	     <tr class="trDeep" id="<bean:write name="element" property="point_id"/>">
	     <%}%>
	     <td align="center" class="RecordRow">
	     <input type="checkbox" width="3%" name="choose" value="<bean:write name="element" property="point_id"/>"/>
	     </td>
	     <td align="left" class="RecordRow">
	     &nbsp;&nbsp;<bean:write name="element" property="point_id"/>&nbsp;&nbsp;
	     </td>
	     <td width="67%" align="left" class="RecordRow">
	     &nbsp;&nbsp;<bean:write name="element" property="pointname" filter="false"/>&nbsp;&nbsp;
	     </td>	     
	     
	     <td width="10%" align="center" class="RecordRow" nowrap>
	     &nbsp;&nbsp;<bean:write name="element" property="validflag"/>&nbsp;&nbsp;
	     </td>
	     <% i++; %>
	 </hrms:extenditerate>
</table>
</td>
</tr>

<tr>
<td colspan="2">
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		   <td valign="bottom" class="tdFontcolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   ${courseForm.abilitylistForm.pagination.current}
					<bean:message key="label.page.sum"/>
		   ${courseForm.abilitylistForm.pagination.count}
					<bean:message key="label.page.row"/>
		   ${courseForm.abilitylistForm.pagination.pages}
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
            <hrms:paginationlink name="courseForm" property="abilitylistForm.pagination" nameId="abilitylistForm" propertyId="abilitylistProperty">
		   </hrms:paginationlink>
		   </p>
		   </td>
		</tr> 
</table>
</td>
</tr>
<tr>
<td width="50%">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr style="height:35">
	<td align="left" width="30%">
	   <logic:equal value="true" name="courseForm" property="pivflag">
	     <hrms:priv func_id="32306C2201" >
	         <input type="button" class="mybutton" onclick="del();" value="<bean:message key='train.course.delability'/>">
	     </hrms:priv>
	   </logic:equal>
	     <input type="button" class="mybutton" onclick="returnback();" value="<bean:message key='button.return'/>">
	</td>
</tr>
</table>
</td>
</tr>
</table>
</html:form>

