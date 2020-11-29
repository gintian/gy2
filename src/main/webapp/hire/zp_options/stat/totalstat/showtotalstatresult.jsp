<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hjsj.hrms.actionform.hire.zp_options.totalstat.TotalStatForm"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
<script language="JavaScript" src="/ajax/basic.js"></script>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%> 
<script type="text/javascript">
<!--
var i=0;
function showpos(){
var d1 = document.getElementById("editor1").value;
    var d2 = document.getElementById("editor2").value;
    if(trim(d1).length<=0||trim(d2).length<=0)
    {
        alert(APPOINT_COUNT_INTERVAL+"！");
        return;
    }
    if(checkDateTime(d1)&&checkDateTime(d2))
    {
    	totalStatForm.action="/hire/zp_options/stat/totalstat/showtotalstatresult.do?b_query=link";
        totalStatForm.submit();
	}else{
	   alert(COUNT_TIME_FORMAT_WRONG+"!");
	}

}
function jumpjob(){
	window.location.href="/hire/zp_options/stat/itemstat/showjobdaily.do?b_query=link";
}
//-->
</script>
<style>
<!--
.TableRowNoBorder {
   background-image:url(../images/listTableHeader.jpg);
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #DCECFC; 
	font-size: 12px;  
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	height:22;
	font-weight: bold;	
	valign:middle;
}
-->
</style>
<%
TotalStatForm totalStatForm = (TotalStatForm)session.getAttribute("totalStatForm");
ArrayList aList=totalStatForm.getAllList();
String schoolPosition=totalStatForm.getSchoolPosition();
String columnName="e01a1.label";
if(schoolPosition!=null&&schoolPosition.length()>0)
    columnName="e01a1.major.label";
 %>
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_options/stat/totalstat/showtotalstatresult"> 
<table align="center">
<tr>
<td>
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<br/>
<%
}
%>
	<table border="0" cellspacing="0" align="left" cellpadding="0"  class="RecordRow"  style="margin-top:-1px;">
	<tr>
	<td class="TableRow" align="left" colspan='2' nowrap>
		<table>
		<tr>
		<td>
		<bean:message key="org.orginfo.organname"/>&nbsp;&nbsp;<html:hidden name="totalStatForm" property='orgid' />&nbsp;
		</td>
		<td>  
		   <html:text name="totalStatForm" property='org' styleClass="textColorWrite" onchange="fieldcode(this,2);"/>
		</td>
		<td> 
		   <img  src="/images/code.gif" onclick='openInputCodeDialogText("UM","org","orgid");'style="margin-top:3px;"/>
	    </td>
	    <td>
		<bean:message key="hire.count.time"/>&nbsp;&nbsp;
		
		<bean:message key="label.from"/> <input type="text" name="startime"  extra="editor" style="width:95px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate" value="${totalStatForm.startime}">
		          <bean:message key="label.to"/> <input type="text" name="endtime"  extra="editor" style="width:95px;font-size:10pt;text-align:left" id="editor2"  dropDown="dropDownDate" value="${totalStatForm.endtime}">
		</td>
		<td>
		 &nbsp;<BUTTON name="tfquery" class="mybutton" onclick="showpos();" style="margin-top:3px;"><bean:message key="hire.generate.chart"/></BUTTON>
		</td>
		</tr>          
		</table>
		
	</td>
	</tr>
	
	<%
	for(int i=0;i<aList.size();i++){ 
	   ArrayList showlist = (ArrayList)aList.get(i);
	   totalStatForm.setShowresumelist(showlist);
	%>
	
	<tr>
	<td width="50%" nowrap><br>
		<table border="0" cellpadding="0" cellspacing="0" align="left" class="ListTable" >
		<tr>
		<td id='<%="pnl_"+i %>'>
		<hrms:chart name="totalStatForm" title="" scope="session" legends="showresumelist" data=""  width="460" height="430" chart_type="20"  chartpnl='<%="pnl_"+i %>' >
				</hrms:chart>
		</td>
		</tr>
		</table>
		</td>
		<%if(i==0){ %>
		<td width="50%" valign="top" align="left" nowrap>
		<br>
		<table border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable" width="90%">
	    <tr >
	    <td class="TableRow" nowrap ><bean:message key="<%=columnName%>"/>
	    </td>
	    <td class="TableRow" nowrap><bean:message key="hire.apply.amount"/>
	    </td>
	    <td class="TableRow" nowrap><bean:message key="hire.firstchoice.amount"/>
	    </td>
	    </tr>
	    <hrms:paginationdb id="element" name="totalStatForm" sql_str="totalStatForm.selectsql" table="" where_str="totalStatForm.wheresql" columns="totalStatForm.column"  pagerows="10" page_id="pagination" indexes="indexes">	
		<bean:define id="z0311" name="element" property="z0311"></bean:define>
	    <tr>
	    <td class="RecordRow">
	    <hrms:codetoname codeid="@K" name="element" codevalue="z0311" codeitem="codeitem" scope="page"/>         
        <bean:write name="codeitem" property="codename" />&nbsp;
        <logic:equal value="" name="codeitem" property="codename">
         ${z0311}
        </logic:equal>
	    </td>
	    <td align="right" class="RecordRow">
	    <logic:notEqual value="" name="element" property="allnum">
	    	    <bean:write name="element" property="allnum"/>
	    </logic:notEqual>
	    <logic:equal value="" name="element" property="allnum">
	    0
	    </logic:equal>
	    </td>
	    
        <td align="right" class="RecordRow">
        <logic:notEqual value="" name="element" property="firstnum">
        <bean:write name="element" property="firstnum"/>
        </logic:notEqual>
        <logic:equal value="" name="element" property="firstnum">
	    0
	    </logic:equal>
        </td>
	    </tr>
	    </hrms:paginationdb>
	    <tr>
	    <td class='RecordRow' colspan='3'>
	    <hrms:paginationdblink name="totalStatForm" property="pagination" nameId="browseRegisterForm" scope="page">
		</hrms:paginationdblink>
	    </td>
	    </tr>
	    </table>
	</td>
	<%} %>
	</tr>
<%} %>
	</table>

</td>
</tr>
<tr>
<td align="center">
<logic:equal value="dxt" name="totalStatForm" property="returnflag">
<hrms:tipwizardbutton flag="retain" target="il_body" formname="totalStatForm"/> 	
</logic:equal>
</td>
</tr>
</table>
</html:form>
