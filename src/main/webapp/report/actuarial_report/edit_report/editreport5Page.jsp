<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.report.actuarial_report.edit_report.EditReport_actuaialForm,
				org.apache.commons.beanutils.LazyDynaBean,
				java.util.*" %>
<%
	EditReport_actuaialForm editReport_actuaialForm=(EditReport_actuaialForm)session.getAttribute("editReport_actuaialForm");
	ArrayList dataHeadList_u05=editReport_actuaialForm.getDataHeadList_u05();
	ArrayList dataList_u05=editReport_actuaialForm.getDataList_u05();
	String opt=editReport_actuaialForm.getOpt();
	String selfUnitcode=editReport_actuaialForm.getSelfUnitcode();
	String unitcode=editReport_actuaialForm.getUnitcode();
	String flagSub=editReport_actuaialForm.getFlagSub(); 
	String rootUnit= editReport_actuaialForm.getRootUnit();
	String isCollectUnit= editReport_actuaialForm.getIsCollectUnit();
	String is_over="0";
	for(int i=0;i<dataList_u05.size();i++)
 	{
 		LazyDynaBean abean =(LazyDynaBean)dataList_u05.get(i);		
   		 LazyDynaBean _abean=null;
   		 for(int j=1;j<dataHeadList_u05.size();j++)  
   		 {
   		  	String temp=(String)dataHeadList_u05.get(j); 
   		 	_abean =(LazyDynaBean)abean.get(temp);
   		 	String over=(String)_abean.get("over");
			if(!over.equals("0"))
			{
				is_over="1";
				break;
			}
	  	 }
	}
	
	
	
	
 %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title></title>
</head>
<script language='javascript' >
	var from_model='${editReport_actuaialForm.from_model}'
	function goback()
	{
		if(from_model=='edit')
		{
			document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportlist.do?b_query=lisk";
			document.editReport_actuaialForm.submit();
		}
		else
		{
			document.editReport_actuaialForm.action="/report/actuarial_report/report_collect.do?b_query=link&a_code=${editReport_actuaialForm.unitcode}";
			document.editReport_actuaialForm.submit();
		}
	}
	
	
	function save()
	{
		 alert("保存成功！");
		document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportlist.do?b_saveU5Desc=lisk";
		document.editReport_actuaialForm.submit();
	}
	
	function exportExcel()
	{
		var hashvo=new ParameterSet();	
	
	var return_vo= window.showModalDialog("/report/actuarial_report/edit_report/select.jsp", 'trainClass_win1', 
      				"dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
      		if(return_vo){	
		hashvo.setValue("cycle_id",'${editReport_actuaialForm.id}');
		hashvo.setValue("unitcode",'${editReport_actuaialForm.unitcode}');
		hashvo.setValue("report_id","U05");
		hashvo.setValue("flag",return_vo);
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'03060000216'},hashvo);
	
	}
	}
	function showfile(outparamters)
	{
			var fileName=outparamters.getValue("fileName");
			//var name=fileName.substring(0,fileName.length-1)+".xls";
			var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+fileName,"excel");
	
	}
	
function reject()
{
		var arguments=new Array();
		arguments[0]="";
		arguments[1]="驳回原因";  
	    var strurl="/gz/gz_accounting/rejectCause.jsp";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
	    if(ss)
		{
			var hashvo=new ParameterSet();	
			hashvo.setValue("cycle_id",'${editReport_actuaialForm.id}');
			hashvo.setValue("unitcode",'${editReport_actuaialForm.unitcode}');
			hashvo.setValue("report_id","U05");
			hashvo.setValue("cause",getEncodeStr(ss[0]));
			var request=new Request({method:'post',asynchronous:false,onSuccess:success,functionId:'03060000218'},hashvo);
		}
}

function success(outparamters)
{
			document.editReport_actuaialForm.action="/report/actuarial_report/report_collect.do?b_query=link&a_code=${editReport_actuaialForm.unitcode}";
			document.editReport_actuaialForm.submit();

}
function showwarning()
	{
		var thecodeurl ="/report/actuarial_report/edit_report/editreportlist.do?b_queryWarning=link";
		var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:600px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
				
	}
</script>

<hrms:themes />
<body>
<html:form action="/report/actuarial_report/edit_report/editreportlist">

<table><tr><td>&nbsp;</td><td>

表5&nbsp;人员变动及人均福利对照表 

 <table width="1150" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
<thead>
    <tr class="trDeep" > 
    	<td align="center" class="TableRow" width='150' width=''  nowrap rowspan="2" >项目</td>
   		<td align="center" class="TableRow" width='250'  colspan="3" >原有离休人员</td>
   		<td align="center" class="TableRow" width='250'  colspan="3" >原有退休人员</td>
   		<td align="center" class="TableRow" width='250'  colspan="3" >原有内退人员</td>
   		<td align="center" class="TableRow" width='150' colspan="2"  >原有遗属</td>
    </tr>
    <tr class="trDeep" >    
	  <td align="center" class="TableRow" width='50'     >人数</td>  
      <td align="center" class="TableRow" width='100'     >人均医疗福利水平(元/年/人)</td>
      <td align="center" class="TableRow" width='100'     >除医疗福利外其他人均福利水平(元/年/人)</td>
      <td align="center" class="TableRow" width='50'     >人数</td>  
      <td align="center" class="TableRow" width='100'     >人均医疗福利水平(元/年/人)</td>
      <td align="center" class="TableRow" width='100'     >除医疗福利外其他人均福利水平(元/年/人)</td>
      <td align="center" class="TableRow" width='50'     >人数</td>  
      <td align="center" class="TableRow" width='100'     >人均医疗福利水平(元/年/人)</td>
      <td align="center" class="TableRow" width='100'     >除医疗福利外其他人均福利水平(元/年/人)</td>
      <td align="center" class="TableRow" width='50'     >人数</td>  
      <td align="center" class="TableRow" width='100'  >除医疗福利外其他人均福利水平(元/年/人)</td>
    </tr>
 </thead>
 
  <% 
 	for(int i=0;i<dataList_u05.size();i++)
 	{
 		LazyDynaBean abean =(LazyDynaBean)dataList_u05.get(i);
 		String name=(String)abean.get("item_name");
 		if(i%2==0)
  		   out.println("<tr class='trShallow'>");
     	else
   		   out.println("<tr class='trDeep'>");
   		  
   		 out.println("<td class='RecordRow' align='left' height='35' >&nbsp;"+name+"</td>");  
   		 LazyDynaBean _abean=null;
   		 for(int j=1;j<dataHeadList_u05.size();j++)  
   		 {
   		  	String temp=(String)dataHeadList_u05.get(j); 
   		 	_abean =(LazyDynaBean)abean.get(temp);
   		 	
   		 	String over=(String)_abean.get("over");
   		 	String value=(String)_abean.get("value");
   		 	String desc=(String)_abean.get("desc");
   		 	if(over.equals("0"))
 	  		 	out.println("<td class='RecordRow' align='right' height='35' nowrap>"+value+"&nbsp;</td>");
   			else
   				out.println("<td class='RecordRow' bgColor='orangered' title='"+desc+"' style='cursor: hand'  align='right' height='35' nowrap>"+value+"&nbsp;</td>");	 	
   		 }  
 		 out.println("</tr>");
 	}
  %>
</table>

<% if(is_over.equals("1")){ %>
&nbsp;&nbsp;
 		<table><tr><td valign='top' >警告描述:</td><td>
<html:textarea  name="editReport_actuaialForm"  rows='10' cols='60' property="t5_desc" />	
		</td></tr></table>
<% } %>

<br>
<% if(is_over.equals("1")){ %>
<input type='button' id="button_save" value='&nbsp;保存&nbsp;' onclick='save();' class="mybutton">

<% } %>
<input type='button' id="button_out" value='输出excel' onclick='exportExcel();' class="mybutton">
 <logic:equal  name="editReport_actuaialForm"  property="reportStatus" value="1">
  <logic:equal  name="editReport_actuaialForm"  property="cycleStatus" value="04">	
 	 <% if(rootUnit.equals("1")||(!selfUnitcode.equals(unitcode)&&!flagSub.equals("1"))){ %>
 	<input type='button' id="button_back" value=' 驳回 ' onclick='reject();' class="mybutton">
 	 <% } %>
  </logic:equal>
  </logic:equal>
   <%if(isCollectUnit.equals("1")){ %>
<input type='button' id="reject1" value='警告信息列表'  onclick='showwarning();' class="mybutton">
<%} %>
 <input type='button' id="button_goback" value='&nbsp;返回&nbsp;' onclick='goback();' class="mybutton">

</td></tr></table>
  
</html:form>
</body>
</html>