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
	String selfUnitcode=editReport_actuaialForm.getSelfUnitcode();
	String unitcode=editReport_actuaialForm.getUnitcode();
	ArrayList u03DataList=editReport_actuaialForm.getU03DataList();
	ArrayList dataHeadList=editReport_actuaialForm.getDataHeadList();
	String info=editReport_actuaialForm.getInfo();
	ArrayList compareDataList=editReport_actuaialForm.getCompareDataList();
	String opt=editReport_actuaialForm.getOpt();
	String reportStatus=editReport_actuaialForm.getReportStatus();
	String kmethod=editReport_actuaialForm.getKmethod();
	String t3_desc=editReport_actuaialForm.getT3_desc();
	String from_model=editReport_actuaialForm.getFrom_model();
	String isOver="0";
	String flagSub=editReport_actuaialForm.getFlagSub(); 
	String rootUnit= editReport_actuaialForm.getRootUnit();
	String isCollectUnit= editReport_actuaialForm.getIsCollectUnit();
	for(int i=0;i<compareDataList.size();i++)
 	{
 		LazyDynaBean abean =(LazyDynaBean)compareDataList.get(i);
 		LazyDynaBean _abean=null;
   		for(int j=3;j<dataHeadList.size();j++)  
   		{
   		  	String temp=(String)dataHeadList.get(j); 
   		 	_abean =(LazyDynaBean)abean.get(temp);
   		 	
   		 	String over=(String)_abean.get("over");
 			if(!over.equals("0"))
 			{
 				isOver="1";
 				break;
 			}
 		}
 	}
 		
 %>




<link rel="stylesheet" href="/css/css1.css" type="text/css">
<html>
<head>
<script language='javascript' >
	var from_model='${editReport_actuaialForm.from_model}'

	// opt: 1保存  2提交
	function save(opt)
	{
		
		var values="";
		for(var i=0;i<document.editReport_actuaialForm.elements.length;i++)
		{
			if(document.editReport_actuaialForm.elements[i].type=='text')
				values+="~"+document.editReport_actuaialForm.elements[i].name+":"+document.editReport_actuaialForm.elements[i].value;
		}
		 
		document.editReport_actuaialForm.current_values.value=values;
		document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportlist.do?b_saveReport3=save&sava_type="+opt;
		document.editReport_actuaialForm.submit();
	}

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
	
	function check_data(iteName)
	{
	  	var a_object=eval("document.editReport_actuaialForm."+iteName);		  	 	
	  	if(a_object.value!=''&&a_object.value.length>0)
	  	{
		  	if(!checkNUM3(a_object,15,5))
		  	{
		  		a_object.value="";
		  		a_object.focus();
		  		return;
		  	}
		  
	  	}
   }
   
   
   
function checkNUM3(NUM,len1,len2)
{
    var i,j,strTemp;
    var str1,str2;
    var n=0;
   
  
    strTemp="-0123456789.";
    if ( NUM.value.length== 0)
    {
        return true;
    }    
   
    var myReg =/^(-?\d+)(\.\d+)?$/
    if(!myReg.test(NUM.value))    
	{
 					alert(FORMATERROR+"！")
 				 	return false;
   
   	}

    if(NUM.value.indexOf(".")!=-1)
    {
     	str1 = NUM.value.substr(0,NUM.value.indexOf("."));
     	str2 = NUM.value.substr(NUM.value.indexOf(".")+1,NUM.value.length);    	
     	if(str1.length>len1)
     	  {
     	  	alert(REPORT_INFO17+len1);
     	  	return false;
     	  }
        if(str2.length>len2)
        {
        	alert(REPORT_INFO18+len2);
     	  	return false;
        }
    }
    else
    {
    	str1 = NUM.value;
    	if(str1.length>len1)
     	  {
     	  	alert(REPORT_INFO17+len1);
     	  	return false;
     	  }
    }   
    //说明是数字
    return true;
}

function exportExcel()
{
	var hashvo=new ParameterSet();	
	
	var return_vo= window.showModalDialog("/report/actuarial_report/edit_report/select.jsp", 'trainClass_win1', 
      				"dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
      		if(return_vo){	
	hashvo.setValue("cycle_id",'${editReport_actuaialForm.id}');
	hashvo.setValue("unitcode",'${editReport_actuaialForm.unitcode}');
	hashvo.setValue("report_id","U03");
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
			hashvo.setValue("report_id","U03");
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
<title></title>
</head>
<body>
<hrms:themes />
<html:form action="/report/actuarial_report/edit_report/editreportlist">

<table style="margin-top: -3px; margin-left: -10px;">
<tr valign="top"><td>&nbsp;</td><td valign="top">

表3&nbsp;财务信息
<table width="1100" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
<thead>
    <tr class="trDeep" > 
    	<td align="center" class="TableRow" width='60' width=''  nowrap rowspan="2" >&nbsp;年度&nbsp;</td>
   		<td align="center" class="TableRow" width='170'  nowrap rowspan="2" >填报周期名称</td>
   		<td align="center" class="TableRow" width='150'  nowrap rowspan="2" >人员分类</td> 
   		<td align="center" class="TableRow" width='200'  colspan="2" >离休人员</td>
   		<td align="center" class="TableRow" width='200'  colspan="2" >退休人员</td>
   		<td align="center" class="TableRow" width='200'  colspan="2" >内退人员</td>
   		<td align="center" class="TableRow" width='120'  >遗属</td>
    </tr>
    <tr class="trDeep" >      
      <td align="center" class="TableRow" width='100'     >医疗报销费用</td>
      <td align="center" class="TableRow" width='100'     >除医疗报销费用外的其他费用</td>
      <td align="center" class="TableRow" width='100'     >医疗福利</td> <%//医疗报销费用 %>
      <td align="center" class="TableRow" width='100'     >除医疗福利费用外的其它费用</td><%//医疗报销费用 %>
      <td align="center" class="TableRow" width='100'     >医疗福利</td><%//医疗报销费用 %>
      <td align="center" class="TableRow" width='100'     >除医疗福利费用外的其它费用</td><%//医疗报销费用 %>
      <td align="center" class="TableRow"    >遗属各项福利费用</td>
    </tr>
 </thead>
 
 <% 
 	for(int i=0;i<u03DataList.size();i++)
 	{
 		LazyDynaBean abean =(LazyDynaBean)u03DataList.get(i);
 		String current=(String)abean.get("current");
 		String U0301=(String)abean.get("U0301");
 		if(i%2==0)
  		   out.println("<tr class='trShallow'>");
     	else
   		   out.println("<tr class='trDeep'>");
   		   
   		 for(int j=0;j<dataHeadList.size();j++)  
   		 {
   		  	String temp=(String)dataHeadList.get(j); 
   		 	if(current.equals("0"))
   		 	{
   		 		String align="left";
   		 		if(j>=3)
   		 			align="right";
   		 		out.println("<td class='RecordRow' align='"+align+"' height='35' nowrap>&nbsp;"+(String)abean.get(temp)+"&nbsp;</td>");
   		 	}
   		 	else
   		 	{
   		 		if(j<3)
	   		 		out.println("<td class='RecordRow' align='left' height='35' nowrap>&nbsp;"+(String)abean.get(temp)+"&nbsp;</td>");
   		 		else
   		 		{
   		 			out.println("<td class='RecordRow' align='left' height='35' nowrap>");
   		 			
   		 			String desc="";
   		 			if(reportStatus.equals("1")||opt.equals("0")||from_model.equals("collect"))
   		 				desc="readOnly";
   		 			out.println("<input type='text' "+desc+" class='text4' onblur=\"check_data('"+temp+"_"+U0301+"');\"   size='12' value='"+(String)abean.get(temp)+"' name='"+temp+"_"+U0301+"' />");
   		 			out.println("</td>");
   		 		}
   		 	}
   		 }  
  %>
 		
 
 
 <%
 		 out.println("</tr>");
 	}
  %>
 </table>
 
 </td></tr>
 
 <% if(request.getParameter("b_saveReport3")!=null&&request.getParameter("b_saveReport3").equals("save")){ %>
 <tr><td colspan='2' valign='top' >
 
  <% 
 		if(!info.equals("1"))
 		{
 			out.println("&nbsp;<font color='red'><b>&nbsp;保存成功!&nbsp;&nbsp;");
 			if(t3_desc==null||t3_desc.trim().length()==0)
	 			out.print("如要上报数据，需对警告内容进行文字解释,并点击保存按钮。");
	 		out.print("</b></font>");
 %>		
 		<br>&nbsp;&nbsp;
 		<table><tr><td valign='top' >&nbsp;警告解释:</td><td>
 		<html:textarea  name="editReport_actuaialForm"  rows='10' cols='60' property="t3_desc" />	
 		</td></tr></table>
 <% 		
 		}
	
 		if(info.equals("1"))
 		{
 			if(request.getParameter("sava_type")!=null&&request.getParameter("sava_type").equals("1"))
 				out.println("<font color='red'><b>&nbsp;&nbsp;保存成功!");
 				
 			if(isOver.equals("1"))
			{
			%>   
			<tr><td colspan='2' valign='top' >
			   &nbsp;&nbsp;
			   <table><tr><td valign='top' >&nbsp;警告解释:</td><td>
			   <html:textarea  name="editReport_actuaialForm"  rows='10' cols='60' property="t3_desc" />	
			   </td></tr></table>
			</td></tr>   
			<% 
			 }	
 		}
%>
</td></tr> 
<%		
   }
   else if(isOver.equals("1"))
   {
%>   
<tr><td colspan='2' valign='top' >
   &nbsp;&nbsp;
   <table><tr><td valign='top' >&nbsp;警告描述:</td><td>
   <html:textarea  name="editReport_actuaialForm"  rows='10' cols='60' property="t3_desc" />	
   </td>
   
   </tr></table>
</td></tr>   
<% 
   }
   
   if(kmethod.equals("0")){
  %>

<tr><td>&nbsp;</td><td>

 <table width="1100" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable" >
<thead>
    <tr class="trDeep" > 
    	<td align="center" class="TableRow" width='230' width=''  nowrap rowspan="2" >比较</td>
   		<td align="center" class="TableRow" width='185'  nowrap rowspan="2" >人员分类</td>
   		<td align="center" class="TableRow" width='200'  colspan="2" >离休人员</td>
   		<td align="center" class="TableRow" width='200'  colspan="2" >退休人员</td>
   		<td align="center" class="TableRow" width='200'  colspan="2" >内退人员</td>
   		<td align="center" class="TableRow" width='120'  >遗属</td>
    </tr>
    <tr class="trDeep" >      
      <td align="center" class="TableRow" width='100'     >医疗报销费用</td>
      <td align="center" class="TableRow" width='100'     >除医疗报销费用外的其他费用</td>
      <td align="center" class="TableRow" width='100'     >医疗福利</td> <%//医疗报销费用 %>
      <td align="center" class="TableRow" width='100'     >除医疗福利费用外的其它费用</td><%//医疗报销费用 %>
      <td align="center" class="TableRow" width='100'     >医疗福利</td><%//医疗报销费用 %>
      <td align="center" class="TableRow" width='100'     >除医疗福利费用外的其它费用</td><%//医疗报销费用 %>
      <td align="center" class="TableRow" width='100'     >除医疗报销费用外的其他费用</td>
    </tr>
 </thead>
 	
 <% 
 	for(int i=0;i<compareDataList.size();i++)
 	{
 		LazyDynaBean abean =(LazyDynaBean)compareDataList.get(i);
 		String name=(String)abean.get("name");
 		String person_type=(String)abean.get("person_type");
 		if(i%2==0)
  		   out.println("<tr class='trShallow'>");
     	else
   		   out.println("<tr class='trDeep'>");
   		  
   		 out.println("<td class='RecordRow' align='left' height='35' >&nbsp;"+name+"&nbsp;</td>");
   		 out.println("<td class='RecordRow' align='left' height='35' >&nbsp;"+person_type+"&nbsp;</td>");  
   		 LazyDynaBean _abean=null;
   		 for(int j=3;j<dataHeadList.size();j++)  
   		 {
   		  	String temp=(String)dataHeadList.get(j); 
   		 	_abean =(LazyDynaBean)abean.get(temp);
   		 	
   		 	String over=(String)_abean.get("over");
   		 	String value=(String)_abean.get("value");
   		 	String desc=(String)_abean.get("desc");
   		 	if(over.equals("0"))
 	  		 	out.println("<td class='RecordRow' align='right' height='35' nowrap>"+value+"&nbsp;</td>");
   			else
   				out.println("<td class='RecordRow' bgColor='orangered' title='"+desc+"' style='cursor: hand'  align='right' height='35' nowrap>"+value+"&nbsp;</td>");	 	
   		 }  
  %>
 		
 
 
 <%
 		 out.println("</tr>");
 	}
  %>
 
 
 </table>
</td></tr>
 
<% } %>
 
 <tr valign="top" align="center"><td>&nbsp;</td><td valign="top" align="center">
 <input type='hidden' name='current_values'  value='' />
 <% if(opt.equals("1")&&!reportStatus.equals("1")){ %>
 <input type='button' id="button_save" value='&nbsp;保存&nbsp;' onclick='save(1);' class="mybutton">
 <% } %>
 <input type='button' id="button_out" value='输出excel' onclick='exportExcel();' class="mybutton" style="margin-left: -2px;">

 	 <logic:equal  name="editReport_actuaialForm"  property="reportStatus" value="1">
 	  <logic:equal  name="editReport_actuaialForm"  property="cycleStatus" value="04">
 	 <% if(rootUnit.equals("1")||(!selfUnitcode.equals(unitcode)&&!flagSub.equals("1"))){ %>
 	<input type='button' id="button_goback" value=' 驳回 ' onclick="reject()" class="mybutton" style="margin-left: -2px;">
 	 <% } %>
 	 </logic:equal>
 	 </logic:equal>
 	 <%if(isCollectUnit.equals("1")&&kmethod.equals("0")){ %>
<input type='button' id="reject1" value='警告信息列表'  onclick='showwarning();' class="mybutton" style="margin-left: -2px;">
<% } %>
 <input type='button' id="button_goback" value='&nbsp;返回&nbsp;' onclick='goback();' class="mybutton" style="margin-left: -2px;">
 
 
 </td></tr></table>
 
</html:form>

</body>
</html>