<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hjsj.hrms.actionform.hire.zp_options.statestat.StateStatForm"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
<%
StateStatForm stateStatForm=(StateStatForm)session.getAttribute("stateStatForm");
String schoolPosition = stateStatForm.getSchoolPosition();
String columnName="e01a1.label";
if(schoolPosition!=null&&schoolPosition.length()>0)
    columnName="e01a1.major.label";

 %>
 <%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    
%>
<script type="text/javascript">
<!--
var i=0;
function showpos(){
stateStatForm.action="/hire/zp_options/stat/statestat/showstateresult.do?b_query=link";
stateStatForm.submit();
}
//-->
</script>
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_options/stat/statestat/showstateresult"> 
<%
if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<br>
<%
}
%>
<table  border="0" cellspacing="0" align="center" cellpadding="0"  style="margin-top:0px;width:700px;">
<tr>
<td width="100%">

	<table  border="0" cellspacing="0" align="left" cellpadding="0"  width="100%" class="RecordRow"  >
	<tr>
	<td  class="TableRow"  nowrap>
		<bean:message key="workdiary.message.resume.state.total"/>&nbsp;<bean:write name="stateStatForm" property="zp_fullname"/>&nbsp;
	</td>
	</tr>
	<tr>
	<td heigth='60' class="RecordRow" nowrap>
	<bean:message key="hire.employActualize.resumeState"/>&nbsp;&nbsp;<html:select name="stateStatForm" property="codeitemid" onchange='showpos()' style="width:150">
				
				 
				 <html:optionsCollection property="codelist" value="dataValue" label="dataName"/>
			</html:select>
	
	<bean:message key="<%=columnName%>"/>&nbsp;&nbsp;<html:select name="stateStatForm" property="zp_pos_id" onchange='showpos()'>
				
				 
				 <html:optionsCollection property="zp_poslist" value="dataValue" label="dataName"/>
			</html:select>
		
	
	</td>
	</tr>
	<tr>
	<td class="" width="80%" nowrap >
	
	<table border='0' >
	
	<tr align="left">
	<td id='pnl1' valign="top">
	<hrms:chart name="stateStatForm" title="${stateStatForm.zp_fullname}" scope="session" legends="alist" data=""  width="550" height="450" chart_type="20" chartpnl="pnl1">
				</hrms:chart>
	<Br>&nbsp;
	</td>
	<td align="center" valign="top" width="20%">
	<table border="0" cellpadding="0" cellspacing="0" align="center"   class="ListTable" >
	<tr>
	<td class="TableRow" align="center" width="80" nowrap><bean:message key="column.sys.status"/>
	</td>
	<td class="TableRow" align="center" width="80" nowrap><bean:message key="lable.hiremanage.subamount"/>
	</td>
	</tr>
	<logic:iterate id="element" name="stateStatForm" property="retlist">
	<tr>
	<td class="RecordRow" nowrap>
	<bean:write name="element" property="dataName"/>
	</td>
	<td class="RecordRow"  align="right" nowrap>
	<bean:define id='num' name="element" property="dataValue"/>
	<bean:write name="element" property="dataValue"/>
	</td>
	<script type="text/javascript">
		
		var n=${num};
		i=i+parseInt(n);
		
	</script>
	</tr>
	</logic:iterate>
	<tr>
	<td class="RecordRow" nowrap><bean:message key="workdiary.message.total"/>
	</td>
	<td class="RecordRow"  align="right" nowrap>
	<script type="text/javascript">
	document.write(i);
	</script>
	
	</td>
	</tr>
	
	</table>
	
	</td>
	</tr>
	
	</table>
	
	</td>
	</tr>	

</td>
</tr>
<tr>
<td align="center">
<logic:equal value="dxt" name="stateStatForm" property="returnflag">
<hrms:tipwizardbutton flag="retain" target="il_body" formname="stateStatForm"/> 
</logic:equal>
</td>
</tr>
</table>
</td>
</tr>
</table>
</html:form>

  	 


    