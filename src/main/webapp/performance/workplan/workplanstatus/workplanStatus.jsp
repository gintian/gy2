<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes />
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.workPlan.WorkplanstatusForm,
				 com.hrms.frame.dao.RecordVo,
				 org.apache.commons.beanutils.LazyDynaBean" %>
				 				 
<%
		WorkplanstatusForm workplanstatusForm = (WorkplanstatusForm)session.getAttribute("workplanstatusForm");	
  	    String codeid = workplanstatusForm.getCodeid();
  	    HashMap haveCycleMap = workplanstatusForm.getHaveCycleMap();
  	    String unitName = workplanstatusForm.getUnitName();
  	    String width = String.valueOf((haveCycleMap.size())*200);
  	    String cycle = workplanstatusForm.getCycle(); //  周期
  	  	String month = workplanstatusForm.getMonth(); //  如果是周报或日报，则获取月份
  	    String year = workplanstatusForm.getYear();   //获得年份
  	    String type = workplanstatusForm.getType();   //获得工作类型
  	       	      
%>
<html>
<head>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language='javascript'>

var IVersion=getBrowseVersion();

if(IVersion==8)
{
	document.writeln("<link href=\"/performance/workplan/workplanstatus/workplanTableLocked_8.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
	document.writeln("<link href=\"/performance/workplan/workplanstatus/workplanTableLocked.css\" rel=\"stylesheet\" type=\"text/css\">");
}
	
// 查询
function query()
{
	document.workplanstatusForm.action="/performance/workplan/workplanstatus.do?b_query=link";
	document.workplanstatusForm.submit();
}

// 反查
function reverseResult(state,report_statue,codeitemid)
{
	var cycle="<%=cycle%>";
	var year="<%=year%>";
	var type="<%=type%>";
	var month="<%=month%>";
	var unit="<%=unitName%>";
	reverse(state,report_statue,cycle,year,type,month,codeitemid);
}
function reverse(state,report_statue,cycle,year,type,month,codeitemid)
{
	var theurl = "/performance/workplan/workplanstatus_show.do?b_query=link`state="+state+"`report_statue="+report_statue+"`cycle="+cycle+"`year="+year+"`type="+type+"`month="+month+"`codeitemid="+codeitemid+"`isReset=1"; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    var return_vo= window.showModalDialog(iframe_url, "", 
        "dialogWidth:650px; dialogHeight:650px;resizable:no;center:yes;scroll:yes;status:no");
}

// 返回	
function goback()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("opt","back"); 
	var In_paramters="unitcode=${workplanstatusForm.codeid}"; 		
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'90100150018'},hashvo);
	
}	
function returnInfo(outparamters)
{
	var unitcode=outparamters.getValue("parent_unitcode");
	document.workplanstatusForm.action="/performance/workplan/workplanstatus.do?b_query=link&opt=3&codeid="+unitcode;
	document.workplanstatusForm.submit();
}
</script>
</head>

<style>
	body {TEXT-ALIGN: center;}
</style>
  
<body>
	<html:form action="/performance/workplan/workplanstatus">
     <table  width="100%">
     <tr><td style="height:35px">           
     	&nbsp;&nbsp;&nbsp;&nbsp;<strong><font size='2'><bean:message key="report.appealUnit"/>:&nbsp;${workplanstatusForm.unitName}</font></strong>
     </td>
     <td style="height:35px" align="right" nowrap>
		<bean:message key="workdiary.message.search.way"/>: 
		<html:select name="workplanstatusForm"  onchange="query();"  styleId="cycle" property="cycle" size="1">
	  		<html:optionsCollection property="cycleTypeList" value="dataValue" label="dataName"/>
		</html:select> 
		&nbsp;&nbsp;	
		<bean:message key="edit_report.year"/>: 
		<html:select name="workplanstatusForm"  onchange="query();"  styleId="year" property="year" size="1">
	  		<html:optionsCollection property="yearTypeList" value="dataValue" label="dataName"/>
		</html:select>	
		&nbsp;&nbsp;
		
		<% if(cycle.equalsIgnoreCase("2")){ %>
			<bean:message key="jx.khplan.quarter"/>:
			<html:select name="workplanstatusForm"  onchange="query();"  styleId="quarter" property="quarter" size="1">
		  		<html:optionsCollection property="quarterTypeList" value="dataValue" label="dataName"/>
			</html:select>	
			&nbsp;&nbsp;
		<%} %>
		<% if(cycle.equalsIgnoreCase("0") || cycle.equalsIgnoreCase("1")){ %>
			<bean:message key="log.teamwork.workplan.cmonth"/>:
			<html:select name="workplanstatusForm"  onchange="query();"  styleId="month" property="month" size="1">
		  		<html:optionsCollection property="monthTypeList" value="dataValue" label="dataName"/>
			</html:select>	
			&nbsp;&nbsp;
		<%} %>
		
		<bean:message key="log.teamwork.workplan.logtype"/>	
		<html:select name="workplanstatusForm"  onchange="query();"  styleId="type" property="type" size="1">
	  		<html:optionsCollection property="typeList" value="dataValue" label="dataName"/>
		</html:select>
		&nbsp;
	 </td></tr>
     
     <tr><td colspan='2'>
      <script language='javascript' >
			document.write("<div id=\"tbl-container\"  style='position:relative;left:5;height:"+(document.body.clientHeight-94)+";width:99%' >");
    </script>	
   	<table border="0" cellspacing="0" align="left" cellpadding="0" class="ListTable">
					 ${workplanstatusForm.tableHtml} 
   	</table>
   	</div>
   	</td></tr>
   	<tr><td  style='position:relative;left:15;'>
   	<% if(!codeid.equalsIgnoreCase("init")){ %>
   	
    <input type="button" class="mybutton" name="back" value="<bean:message key="kq.emp.button.return"/>" onclick="goback()" class="mybutton" >
   	
   	<% }else{ %>
   	<hrms:tipwizardbutton flag="report" target="il_body" formname="workplanstatusForm"/>
   	<%} %>
   	</td></tr>
   	</table>
   	<div id="date_panel">
   			
    </div>
   	
   	
   </html:form>
  </body>
</html>
