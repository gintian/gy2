<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.kh_plan.ExamPlanForm,	
				 com.hrms.struts.constant.WebConstant" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/performance/kh_plan/defineTargetItems.js"></script>
<script>
function getItems(elementName)
{
		var items = document.getElementsByName(elementName);
		var itemStr='';
		for(var i=0;i<items.length;i++)
		{
			if(items[i].checked==true)
				itemStr+=items[i].value+',';
		}
		if(itemStr!='')
			itemStr=itemStr.substring(0,itemStr.length-1);
		return itemStr;
}
function getAllItems(elementName)
{
		var items = document.getElementsByName(elementName);
		var itemStr='';
		for(var i=0;i<items.length;i++)
		{
			itemStr+=items[i].value+',';
		}
		return itemStr;
}
function ok(){
   var str = getItems("bodyids");
   if(str==''){
	   alert("请选择主体类别！");
	   return ;
   }
   var allstr = getAllItems("bodyids");
   var thevo=new Object();
   thevo.bodyids=str;
   thevo.allbodyids=allstr;
   thevo.flag="true";
    parent.window.returnValue=thevo;

    if(window.showModalDialog) {
        parent.window.close();
    }else{
        window.top.opener.mainbodyGradeCtl_window_ok(thevo);
        window.open("about:blank","_top").close();
    }
}
</script>
<%
	ExamPlanForm myForm=(ExamPlanForm)session.getAttribute("examPlanForm");
	ArrayList setList = myForm.getMainbodyGradetypeList();
%>
<html:form action="/performance/kh_plan/mainbodyGradeCtl">
<%if(setList==null || "".equals(setList) || setList.size()==0) {%>
	请先设置相应的考核主体类别！
<%} else {%>
<table border="0" cellspacing="0" align="center" cellpadding="0">
		<tr>
		<td>
	<fieldset align="left" style="width:300;height:180">
	<legend>
			考核主体类别
	</legend>
	<table border="0" cellspacing="0" cellpadding="0">
		<logic:iterate id="element" name="examPlanForm" property="mainbodyGradetypeList">
		<tr>
			<td>
				&nbsp;&nbsp;&nbsp;&nbsp;<input name="bodyids" type="checkbox"
					id="<bean:write name="element" property="name" filter="true" />"
					value="<bean:write name="element" property="body_id" filter="true" />"
					<logic:notEqual name="element" property="selected"
											value="0">checked</logic:notEqual> />
				<input type="hidden" name="level"
					value='<bean:write name="element" property="level" filter="true" />'>
			</td>
			<td>
				&nbsp;&nbsp;&nbsp;&nbsp;<bean:write name="element" property="name" filter="true" />
			</td>
		</tr>
		</logic:iterate>
	</table>
	</fieldset>
	</td>
	</tr>
	</table>

	<table width="100%">
		<tr>
			<td align="center">

				<input type="button" id="b_ok" class="mybutton"
					value="<bean:message key='button.ok' />"
					onClick="ok();" />
			<script type="text/javascript">
				var theStatus = '${examPlanForm.status}';				
				if(theStatus=='5' || theStatus=='0')				
					document.getElementById("b_ok").disabled=false;
				else
					document.getElementById("b_ok").disabled=true;
			</script>
				<input type="button" class="mybutton"
					value="<bean:message key='button.cancel' />"
					onClick="parent.window.close();">
			</td>
		</tr>
	</table>
	<%} %>
</html:form>