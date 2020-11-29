<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,				 
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
				 
<% 	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}     
 %>			 
<script type="text/javascript">
<!--
function interview(id,planid,object_id,body,oper)
{
  khResultForm.object_id.value=object_id;
  khResultForm.planid.value=planid;
  khResultForm.body.value=body;
  khResultForm.oper.value=oper;
  khResultForm.from_flag.value="1";
  khResultForm.action="/performance/kh_result/kh_result_figures.do?b_interview=link&id="+id;
  khResultForm.submit();
}
function changeList(obj)
{
   var id="-1";
   for(var i=0;i<obj.options.length;i++)
   {
      if(obj.options[i].selected)
      {
         id=obj.options[i].value;
         break;
      }
   }
   performanceInterviewForm.action="/performance/interview/search_interview_list.do?b_init=init&opt=1&plan_id="+id+"&type=0";
   performanceInterviewForm.submit();
}
//-->
</script>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" >
<tr>
<td>
<html:form action="/performance/interview/search_interview_list">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">

<html:hidden property="objectid" name="performanceInterviewForm"/>
<html:hidden property="plan_id" name="performanceInterviewForm"/>
<html:hidden property="type" name="performanceInterviewForm"/>
<logic:equal value="0" name="performanceInterviewForm" property="type">
<tr>
<td align=left colspan=6 style="height:20px;padding-bottom:2px;">  <!-- bug 35605 格线重叠 -->
考核计划：<html:select name="performanceInterviewForm" property="plan_id" size="1" onchange="changeList(this);">
			<html:optionsCollection property="planList" value="dataValue" label="dataName"/>
		    </html:select>&nbsp;
		    &nbsp;&nbsp;
</td>
</tr>
</logic:equal>
<tr>
<td>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
 <td align="center" class="TableRow" nowrap>
  &nbsp;考核名称&nbsp;
  </td>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<bean:message key="b0110.label"/>&nbsp;
  </td>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<%
	    	FieldItem fielditem = DataDictionary.getFieldItem("E0122");
	    %>	         
		<%=fielditem.getItemdesc()%>&nbsp;
  </td>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<bean:message key="e01a1.label"/>&nbsp;
  </td>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<bean:message key="hire.employActualize.name"/>&nbsp;
  </td>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<bean:message key="kh.field.opt"/>&nbsp;
  </td>
</tr>
</thead>
<% int j=0; %>
 <hrms:extenditerate id="element" name="performanceInterviewForm" property="personListForm.list" indexes="indexes"  pagination="personListForm.pagination" pageCount="15" scope="session">
 <%if(j%2==0){ %>
	     <tr class="trShallow">
	     <%} else { %>
	     <tr class="trDeep">
	     <%}%>
	      <td align="left" class="RecordRow" nowrap>
	     &nbsp;<bean:write name="element" property="name"/>&nbsp;
	     </td>
	     <td align="left" class="RecordRow" nowrap>
	     &nbsp;<bean:write name="element" property="b0110"/>&nbsp;
	     </td>
	      <td align="left" class="RecordRow" nowrap>
	     &nbsp;<bean:write name="element" property="e0122"/>&nbsp;
	     </td>
	      <td align="left" class="RecordRow" nowrap>
	     &nbsp;<bean:write name="element" property="e01a1"/>&nbsp;
	     </td>
	      <td align="left" class="RecordRow" nowrap>
	     &nbsp;<bean:write name="element" property="a0101"/>&nbsp;
	     </td>
	      <td align="center" class="RecordRow" nowrap>
	    <a href="javascript:interview('<bean:write name="element" property="id"/>','<bean:write name="element" property="planid"/>','<bean:write name="element" property="a0100"/>','<bean:write name="element" property="body"/>','<bean:write name="element" property="oper"/>');">
	   <logic:equal value="1" name="performanceInterviewForm" property="type">
	   查看
	   </logic:equal>
	   <logic:equal value="0" name="performanceInterviewForm" property="type">
	    <logic:equal value="1" name="element" property="oper">
	    <logic:equal value="-1" name="element" property="id">
	     面谈
	    </logic:equal>
	    <logic:notEqual value="-1" name="element" property="id">
	       <logic:equal value="0" name="element" property="status">
	        面谈
	       </logic:equal>
	       <logic:equal value="1" name="element" property="status">
	       已面谈
	       </logic:equal>
	    </logic:notEqual>
	    </logic:equal>
	    
	     <logic:equal value="0" name="element" property="oper">
	   查看
	    </logic:equal>
	    </logic:equal>
	    </a>
	     </td>	     
	     </tr>
	     <% j++; %>
	     </hrms:extenditerate>
</table>
</td>
</tr>
<td align="center">
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		   <td valign="bottom" class="tdFontcolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   ${performanceInterviewForm.personListForm.pagination.current}
					<bean:message key="label.page.sum"/>
		   ${performanceInterviewForm.personListForm.pagination.count}
					<bean:message key="label.page.row"/>
		   ${performanceInterviewForm.personListForm.pagination.pages}
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
            <hrms:paginationlink name="performanceInterviewForm" property="personListForm.pagination" nameId="personListForm" propertyId="personListProperty">
		   </hrms:paginationlink>
		   </p>
		   </td>
		</tr> 
</table>
</td> 
<tr>
</tr>
<logic:equal value="1" name="performanceInterviewForm" property="type">
<tr>
<td align="left">
<input type="button" name="vv" value="<bean:message key="button.close"/>" onclick="window.close();" class="mybutton"/>
</td>
</tr>
</logic:equal>
</table>
</html:form>
</td>
</tr>
<tr>
<td>
<html:form action="/performance/kh_result/kh_result_figures">
<input type="hidden" name="object_id" value=""/>
<input type="hidden" name="planid" value=""/>
<input type="hidden" name="body" value=""/>
<input type="hidden" name="oper" value=""/>
<input type="hidden" name="from_flag" value=""/>
</html:form>
</td></tr>
</table>