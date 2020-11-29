<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,com.hjsj.hrms.actionform.performance.commend_table.CommendTableForm"%>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<link href="/performance/commend_table/commend.css" rel="stylesheet" type="text/css">
<%
CommendTableForm  commendTableForm = (CommendTableForm)session.getAttribute("commendTableForm");
 %>
<script type="text/javascript">
<!--
function exportExcel()
{
  var hashVo=new ParameterSet();
  hashVo.setValue("ext","excel");
  hashVo.setValue("b0110","${commendTableForm.unitCode}");
  var request=new Request({method:'post',asynchronous:false,onSuccess:export_tax_ok,functionId:'3020080029'},hashVo);	
    
}
function export_tax_ok(outparameters)
{
   var outName=outparameters.getValue("outName");
   var name=outName.substring(0,outName.length-1)+".xls";
 	//xus 20/4/30 vfs改造
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name,"excel");
}
//-->
</script>
<html:form action="/performance/commend_table/leadership_members">
<table width="75%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
<tr>
<td align="left" colspan="8">
<font class='tt4Noweight'><b>单位：${commendTableForm.unit}</b></font>&nbsp;&nbsp;<input type="button" name="ee" value="输出Excel" class="mybuttonBig" onclick="exportExcel();"/>
</td>
</tr>
<tr>
<td align="center" colspan="2" class="TableRow"><font class='tt4Noweight'>被测评人员情况</font></td><td align="center" rowspan="2" class="TableRow"><font class='tt4Noweight'>意见选项</font></td><td align="center" colspan="6" class="TableRow"><font class='tt4Noweight'>意见统计</font></td>
</tr>
<tr>
<td align="center" class="TableRow"><font class='tt4Noweight'>姓名</font></td><td align="center" class="TableRow"><font class='tt4Noweight'>职务</font></td><td align="center" class="TableRow"><font class='tt4Noweight'>总票数</font></td><td align="center" class="TableRow"><font class='tt4Noweight'>比例</font></td><td align="center" class="TableRow"><font class='tt4Noweight'>班子成员评价</font></td><td align="center" class="TableRow"><font class='tt4Noweight'>比例</font></td><td align="center" class="TableRow"><font class='tt4Noweight'>中层评价</font></td><td align="center" class="TableRow"><font class='tt4Noweight'>比例</font></td>
</tr>
<logic:iterate id="element" name="commendTableForm" property="oneList" indexId="index" offset="0">
<%int x=0; %>
<logic:iterate id="data" name="element" property="subList">
<tr>
<%if(x==0){ %>
<td align='center' valign='middle' class="RecordRow" rowspan='4'><font class='tt6'><bean:write name="element" property="a0101"/></font></td>
<td align='center' valign='middle' class="RecordRow" rowspan='4'><font class='tt6'><bean:write name="element" property="e01a1"/></font></td>
<%} %>
<td align='center' valign='middle' class="RecordRow"><font class='tt6'><bean:write name="data" property="desc"/></font></td>
<td align='center' valign='middle' class="RecordRow"><font class='tt6'><bean:write name="data" property="total"/></font></td>
<td align='center' valign='middle' class="RecordRow"><font class='tt6'><bean:write name="data" property="totalbl"/></font></td>
<td align='center' valign='middle' class="RecordRow"><font class='tt6'><bean:write name="data" property="bzcy"/></font></td>
<td align='center' valign='middle' class="RecordRow"><font class='tt6'><bean:write name="data" property="bzcybl"/></font></td>
<td align='center' valign='middle' class="RecordRow"><font class='tt6'><bean:write name="data" property="zccy"/></font></td>
<td align='center' valign='middle' class="RecordRow"><font class='tt6'><bean:write name="data" property="zccybl"/></font></td>
</tr>
<%x++; %>
</logic:iterate>
</logic:iterate>
</table>
</html:form>