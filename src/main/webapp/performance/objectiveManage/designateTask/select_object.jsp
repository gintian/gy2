<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
<SCRIPT LANGUAGE="javascript" src="/performance/objectiveManage/designateTask/designate.js"></SCRIPT>
<script type="text/javascript">
function closeWin(){
	if(window.showModalDialog){
		parent.window.close();
	}else{
		parent.parent.Ext.getCmp("newDesignateTTWin").close();
	}
}
function query()
{
   designateTaskForm.action="/performance/objectiveManage/select_object.do?b_init=init2&opt=init2"; 
   designateTaskForm.submit();
}
<%if(request.getParameter("isClose")!=null&&request.getParameter("isClose").equals("1")){%>
  if(window.showModalDialog){
	  window.returnValue="1";
  }else{
	  parent.parent.newDesignateTT_callback("1");
  }
  closeWin();
<%}%>
</script>
<body>
<html:form action="/performance/objectiveManage/select_object"> <br>
<html:hidden name="designateTaskForm" property="plan_id"/> 
<html:hidden name="designateTaskForm" property="objectid"/> 
<html:hidden name="designateTaskForm" property="p0400"/> 
<html:hidden name="designateTaskForm" property="qzfp"/> 
<html:hidden name="designateTaskForm" property="fromflag"/> 
<html:hidden name="designateTaskForm" property="p0401"/> 
<html:hidden name="designateTaskForm" property="p0407"/> 
<html:hidden name="designateTaskForm" property="type"/> 
<html:hidden name="designateTaskForm" property="taskid"/> 
<html:hidden name="designateTaskForm" property="group_id"/> 
<html:hidden name="designateTaskForm" property="task_type"/> 
<input type="hidden" name='hiddenStr' value="" id="hstr"/>
<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="ListTable">
<tr>
  <td width="10%" align="left" nowrap>考核计划：</td><td align="left"> 
   <html:select name="designateTaskForm" property="to_plan_id" size="1" onchange="query();">
			<html:optionsCollection property="planList" value="dataValue" label="dataName"/>
		    </html:select>
  </td>
</tr>
<tr>
  <td width="10%" align="left" nowrap>项目分类： </td><td align='left'>
   <html:select name="designateTaskForm" property="to_itemid" size="1" onchange="query();">
			<html:optionsCollection property="itemList" value="dataValue" label="dataName"/>
		    </html:select>
</td>
</tr>
<tr><td colspan="2" class="RecordRow">
<div id="dataArea" style='overflow:auto;width:100%;height:400'>
<table width="100%" border="0" cellspacing="0"  style="margin-top:-1" align="center" cellpadding="0" class="ListTable">
<thead>
<tr style="position:relative;top:expression(this.offsetParent.scrollTop-1);">
  <td align="center" class="TableRow" nowrap>
<input type="checkbox" name="allselect" id="select" onclick="selectAllRecord(this);"/>
</td>
  <td align="center" class="TableRow" nowrap>
 部门
  </td>
  <td align="center" class="TableRow" nowrap>
姓名
  </td>
</tr>
</thead>
 <hrms:extenditerate id="element" name="designateTaskForm" property="objectListForm.list" indexes="indexes"  pagination="objectListForm.pagination" pageCount="1000" scope="session">
 <tr>
 <td align="center" class="RecordRow">
 <logic:equal value="1" name="element" property='visibleCheckBox'>
 <logic:equal value="1" name="element" property="isChecked">
 <input type="checkbox" name="selids" value="<bean:write name="element" property="hiddenStr"/>" checked/>
 </logic:equal>
 <logic:equal value="0" name="element" property="isChecked">
 <input type="checkbox" name="selids" value="<bean:write name="element" property="hiddenStr"/>"/>
 </logic:equal>
 </logic:equal>
 </td>
 <td align="left" class="RecordRow">&nbsp;<bean:write name="element" property="e0122"/></td>
 <td align="left" class="RecordRow">&nbsp;<bean:write name="element" property="a0101"/></td>
 </tr>
 </hrms:extenditerate>
 </table>
 </div>
 </td>
 </tr>
 <tr>
<td class="RecordROw" colspan="2">
    <table  width="100%" align="center">
		<tr>
		   <td valign="bottom" class="tdFontolor" nowrap>第
		   <bean:write name="designateTaskForm" property="objectListForm.pagination.current" filter="true"/>
		   页
		   共
		   <bean:write name="designateTaskForm" property="objectListForm.pagination.count" filter="true"/>
		   条
		   共
		   <bean:write name="designateTaskForm" property="objectListForm.pagination.pages" filter="true"/>
		   页
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="designateTaskForm" property="objectListForm.pagination" nameId="objectListForm" propertyId="objectListProperty">
		   </hrms:paginationlink>
		   </td>
		</tr> 
</table>
</td>
</tr>
<tr><td colspan="2" style="padding-top:3px">
<input type="button" value="<bean:message key="button.ok"/>" onclick="selectOk();" class="mybutton"/>
&nbsp;
<input type="button" value="<bean:message key="button.close"/>" onclick="closeWin();" class="mybutton"/>
</td></tr>
</table>
</html:form>
</body>
</html>