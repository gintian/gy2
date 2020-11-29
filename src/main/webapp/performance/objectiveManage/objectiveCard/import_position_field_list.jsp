<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.hjsj.hrms.actionform.performance.objectiveManage.ObjectCardForm" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   String buttonClass="mybutton";
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
      buttonClass="mybuttonBig";
   }
 %>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<style>
div#tbl-container 
{
	 
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}

</style>

<script type="text/javascript">
<!--
function allselect(obj)
{
   var ele=document.getElementsByName("fieldid");
   for(var i=0;i<ele.length;i++)
   {
      if(obj.checked)
      {
         ele[i].checked=true;
      }
      else
         ele[i].checked=false;
   }
}
function SelectOK()
{
     var element=document.getElementsByName("fieldid");
     var el=document.getElementsByName("fid");
     var num=0;
     var selectids="";
     var sids="";
     for(var i=0;i<element.length;i++)
     {
        if(element[i].checked)
        {
           selectids+="`"+element[i].value;
           sids+="`"+el[i].value;
           num++;
        }
     }
     if(num==0)
     {
       alert("请选择要导入的指标！");
       return;
     }
    var hashvo=new ParameterSet();
    hashvo.setValue("i9999",selectids.substring(1));
    hashvo.setValue("itemid","${objectCardForm.itemid}");
    hashvo.setValue("plan_id","${objectCardForm.planid}");
    hashvo.setValue("object_id","${objectCardForm.object_id}");
    hashvo.setValue("model","${objectCardForm.model}");
    hashvo.setValue("body_id","${objectCardForm.body_id}");
    hashvo.setValue("p0400","<%=request.getParameter("p0400")%>");
    hashvo.setValue("importType","${objectCardForm.importType}");
    hashvo.setValue("sids",sids.substring(1));
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnSubOk,functionId:'30200710253'},hashvo);
     
}
function returnSubOk(outparameters)
{
	var importType=outparameters.getValue("importType");
	if(importType=='position')
      alert("已成功导入岗位职责指标！");
	else
	  alert("已成功导入部门职责指标！");
   
	if(parent && parent.parent && parent.parent.Ext && parent.parent.importPositionField_ok){
		parent.parent.importPositionField_ok(2);
	} else {
	    var obj = new Object();
	    obj.refresh=2;
	    returnValue=obj;
	    window.close();
	}
	
}
function closeWindow() {
	if(parent && parent.parent && parent.parent.Ext && parent.parent.importpositionfieldWinClose){
		parent.parent.importpositionfieldWinClose();
	} else {
	   var obj = new Object();
	   obj.refresh=1;
	   returnValue=obj;
	   window.close();
	}
}
//-->
</script>
<%
   ObjectCardForm objectCardForm=(ObjectCardForm)session.getAttribute("objectCardForm");
   ArrayList list = objectCardForm.getHeadList();
   String tableWidth=objectCardForm.getTableWidth();
   int tw=list.size()*80;
   String width="80";
   if(list.size()==3)
       width="120";
   if(list.size()==2)
      width="180";
   if(list.size()==1)
      width="360";
%>
<html:form action="/performance/objectiveManage/import_position_field_list">
<html:hidden property="importType"/>
<logic:equal value="0" name="objectCardForm" property="isHaveRecord">
<table width="90%" align="left" border="0" cellpadding="0" cellspacing="0" class="ListTable">
<tr>
<td width="100%" align="left">

<table><tr><td>&nbsp;</td><td>

<div id="tbl-container" style="width:830px;height:320px;">
<table align="left" border="0" cellpadding="0" cellspacing="0" class="ListTable">
<tr>
<td width="100%" align="center">
<table  align="center" border="0" cellpadding="0" cellspacing="0" class="ListTable">
<thead>
<tr  style="position:relative;top:expression(this.offsetParent.scrollTop-1);"   >
<td align="center" width="20px" class="TableRow">
<input type="checkbox" name="aselect" value="0" onclick="allselect(this);"/>
</td>
<logic:iterate id="element" name="objectCardForm" property="headList" >
 
 	<logic:equal name="element" property="itemtype" value="M">
 		<td align="center" height="20px" width="<%=(Integer.parseInt(width)+120)%>px" class="TableRow" nowrap>
 			<font class='<%=tt4CssName%>'><bean:write name="element" property="itemdesc"/></font>
 		</td>
 	</logic:equal> 
	<logic:notEqual name="element" property="itemtype" value="M">
		<td align="center" height="20px" width="<%=(width)%>px" class="TableRow" nowrap>
			<font class='<%=tt4CssName%>'><bean:write name="element" property="itemdesc"/></font>
 		</td>
	</logic:notEqual> 
 
  
</logic:iterate>
</tr>
</thead>
<% int i=0; %>
<hrms:extenditerate id="data" name="objectCardForm" property="positionFieldListForm.list" indexes="indexes"  pagination="positionFieldListForm.pagination" pageCount="20" scope="session">
     <%if(i%2==0){ %>
	     <tr class="trShallow">
	     <%} else { %>
	     <tr class="trDeep">
	     <%}
	     %>
	     <td align="center" width="20px" class="RecordRow">
	      <input type="checkbox" name="fieldid" value="<bean:write name="data" property="i9999"/>"/>
	      <input type="hidden" name="fid" value="<bean:write name="data" property="e01a1"/>"/> 
	     </td>
	    <%
	     for(int j=0;j<list.size();j++)
	     {
	        LazyDynaBean bean =(LazyDynaBean)list.get(j);
	        String itemtype=(String)bean.get("itemtype");
	        String itemid=(String)bean.get("itemid");
	        String itemid_1=(String)bean.get("itemid_1");
	        if(itemtype.equalsIgnoreCase("N"))
	        {
	     %>
	       <td align="right" width="<%=width%>px" class="RecordRow">
	       <font class='<%=tt3CssName%>'><bean:write name="data" property="<%=itemid%>"/></font>&nbsp;&nbsp;
	       </td>
	     <%
	        }else if(itemtype.equalsIgnoreCase("M")){
	     %>
	       <td align="left" width="<%=(Integer.parseInt(width)+120)%>px" class="RecordRow" title="<bean:write name="data" property='<%=(itemid+"_info")%>'  filter="true" />">
	       &nbsp;&nbsp;<font class='<%=tt3CssName%>'><bean:write name="data" property="<%=itemid%>"/></font>
	       </td>
	     
	     <%}else{
	  		
	     	 %>
	       <td align="left" width="<%=width%>px" class="RecordRow">
	       &nbsp;&nbsp;<font class='<%=tt3CssName%>'><bean:write name="data" property="<%=itemid%>"/></font>
	       </td>
	   <%
	        }
	     }
	    i++;
	    %>
	     </tr>
</hrms:extenditerate>
</table>
</td>
</tr>
<tr>
<td align="center" width="100%">
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		   <td valign="bottom" class="tdFontcolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   ${objectCardForm.positionFieldListForm.pagination.current}
					<bean:message key="label.page.sum"/>
		   ${objectCardForm.positionFieldListForm.pagination.count}
					<bean:message key="label.page.row"/>
		   ${objectCardForm.positionFieldListForm.pagination.pages}
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
            <hrms:paginationlink name="objectCardForm" property="positionFieldListForm.pagination" nameId="positionFieldListForm" propertyId="positionFieldListProperty">
		   </hrms:paginationlink>
		   </p>
		   </td>
		</tr> 
</table>
</td>
</tr>
</table>
</div>

</td></tr></table>


</td>
</tr>
<tr>
<td align="left">

<html:hidden name="objectCardForm" property="itemid"/>
<html:hidden name="objectCardForm" property="positionID"/>
&nbsp;&nbsp;
 <input type="button" class="<%=buttonClass%>" name="ok" value="<bean:message key="button.ok"/>" onclick="SelectOK();"/>
 <input type="button" class="<%=buttonClass%>" name="cancel" value="<bean:message key="button.close"/>" onclick="closeWindow();"/>
</td>
</tr>
</table>
</logic:equal>
<logic:equal value="1" name="objectCardForm" property="isHaveRecord">
<br>
<br>
<table width="90%" align="center" border="0" cellpadding="0" cellspacing="0">
<tr>
<td align="center">
<strong><bean:write name="objectCardForm" property="alertMessage"/></strong>
</td>
</tr>
<tr>
<td>
&nbsp;
</td>
</tr>
<tr>
<td align="center">
<input type="button" class="<%=buttonClass%>" name="cancel" value="<bean:message key="button.close"/>" onclick="closeWindow();"/>
</td>
</tr>
</table>
</logic:equal>
</html:form>