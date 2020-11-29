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
	ArrayList dataHeadList=editReport_actuaialForm.getDataHeadList();
	ArrayList u04DataList=editReport_actuaialForm.getU04DataList();
	String reportStatus=editReport_actuaialForm.getReportStatus();
	String opt=editReport_actuaialForm.getOpt();
	String selfUnitcode=editReport_actuaialForm.getSelfUnitcode();
	String unitcode=editReport_actuaialForm.getUnitcode();
	String flagSub=editReport_actuaialForm.getFlagSub(); 
	String rootUnit= editReport_actuaialForm.getRootUnit();
 %>
<html>


<script language='javascript' >
	var opt='${editReport_actuaialForm.opt}'

	// opt: 1保存  2提交
	function save(opt)
	{
		
		var values="";
		for(var i=0;i<document.editReport_actuaialForm.elements.length;i++)
		{
			if(document.editReport_actuaialForm.elements[i].type=='text')
				values+="~"+document.editReport_actuaialForm.elements[i].name+":"+document.editReport_actuaialForm.elements[i].value;
		}
			var hashvo=new ParameterSet();   	     
 		 hashvo.setValue("current_values",values);
 		  hashvo.setValue("unitcode",'${editReport_actuaialForm.unitcode}');
 		   hashvo.setValue("id",'${editReport_actuaialForm.id}');
 		     hashvo.setValue("opt",opt);
 		var request=new Request({asynchronous:false,onSuccess:issuccess2,functionId:'03060000240'},hashvo);
		
		
	}
 function issuccess2(outparamters)
    {
    	var flag=outparamters.getValue("info");
    	var opt=outparamters.getValue("opt");
    	var values=outparamters.getValue("values");
    	if(flag==""){
    	
    	document.editReport_actuaialForm.current_values.value=values;
		document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportlist.do?b_saveReport4=save&sava_type="+opt;
		document.editReport_actuaialForm.submit();
    	}else{
    	alert(flag);
    	}
     
    }
	function goback()
	{
		if(opt=='1')
		{	
			document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportlist.do?b_query=lisk";
			document.editReport_actuaialForm.submit();
		}
		else
			history.go(-1);
	}
	
	function check_data(iteName)
	{
	  	var a_object=eval("document.editReport_actuaialForm."+iteName);		  	 	
	  	if(a_object.value!=''&&a_object.value.length>0)
	  	{
		  	if(!checkNUM3(a_object,15,0))
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
	hashvo.setValue("flag",return_vo);	
	hashvo.setValue("cycle_id",'${editReport_actuaialForm.id}');
	hashvo.setValue("unitcode",'${editReport_actuaialForm.unitcode}');
	hashvo.setValue("report_id","U04");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'03060000216'},hashvo);
}
}

function showfile(outparamters)
{
		var fileName=outparamters.getValue("fileName");
		//var name=fileName.substring(0,fileName.length-1)+".xls";
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+fileName,"excel");//xiegh add 20170718 后台交易类已经给了.xls 这里为什么还要加.xls 不报错才怪

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
			hashvo.setValue("report_id","U04");
			hashvo.setValue("cause",getEncodeStr(ss[0]));
			var request=new Request({method:'post',asynchronous:false,onSuccess:success,functionId:'03060000218'},hashvo);
		}
}

function success(outparamters)
{
			document.editReport_actuaialForm.action="/report/actuarial_report/report_collect.do?b_query=link&a_code=${editReport_actuaialForm.unitcode}";
			document.editReport_actuaialForm.submit();

}
</script>



<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title></title>
</head>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<body>
<html:form action="/report/actuarial_report/edit_report/editreportlist">

<table style="margin-top: -3px; margin-left: -10px;"><tr><td>&nbsp;</td><td>

表4&nbsp;人员统计表
<table width="750" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable" >
<thead>
    <tr class="trDeep" > 
    	<td align="center" class="TableRow" width='50' width=''  nowrap rowspan="2" >&nbsp;年度&nbsp;</td>
   		<td align="center" class="TableRow" width='140'  nowrap  >人员分类</td> 
   		<td align="center" class="TableRow" width='140'  nowrap  >离休人员</td>
   		<td align="center" class="TableRow" width='140'  nowrap >退休人员</td>
   		<td align="center" class="TableRow" width='140'  nowrap  >内退人员</td>
   		<td align="center" class="TableRow" width='140'  >遗属</td>
    </tr>
 </thead>
 
 <% 
 	for(int i=0;i<u04DataList.size();i++)
 	{
 		LazyDynaBean abean =(LazyDynaBean)u04DataList.get(i);
 		String current=(String)abean.get("current");
 		String U0401=(String)abean.get("U0401");
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
   		 			align="left";
   		 		out.println("<td class='RecordRow' align='"+align+"' height='35' nowrap>&nbsp;"+(String)abean.get(temp)+"&nbsp;</td>");
   		 	}
   		 	else
   		 	{
   		 		if(j<2)
	   		 		out.println("<td class='RecordRow' align='left' height='35' nowrap>&nbsp;"+(String)abean.get(temp)+"&nbsp;</td>");
   		 		else
   		 		{
   		 			out.println("<td class='RecordRow' align='left' height='35' nowrap>");
   		 			
   		 			String desc="";
   		 			if(reportStatus.equals("1")||opt.equals("0"))
   		 				desc="readOnly";
   		 			out.println("<input type='text' "+desc+" onblur=\"check_data('"+temp+"_"+U0401+"');\"   size='18' value='"+((String)abean.get(temp))+"' name='"+temp+"_"+U0401+"' />");
   		 			out.println("</td>");
   		 		}
   		 	}
   		 }  

 		 out.println("</tr>");
 	}
  %>
 
 </table>
 
 </td></tr>
 <tr valign="top" align="center"><td >&nbsp;</td><td valign="top" align="center">

 <input type='hidden' name='current_values'  value='' />
 <% if(opt.equals("1")&&!reportStatus.equals("1")){ %>
 <input type='button' id="button_goback" value='&nbsp;保存&nbsp;' onclick='save(1);' class="mybutton">
 <% } %>
  <input type='button' id="button_goback" value='输出excel' onclick='exportExcel();' class="mybutton"  style="margin-left: -2px;">
  
  <logic:equal  name="editReport_actuaialForm"  property="reportStatus" value="1">
 	 <logic:equal  name="editReport_actuaialForm"  property="cycleStatus" value="04">
 	 <% if(rootUnit.equals("1")||(!selfUnitcode.equals(unitcode)&&!flagSub.equals("1"))){ %>
 	<input type='button' id="button_goback" value=' 驳回 ' onclick='reject();' class="mybutton"  style="margin-left: -2px;">
 	 <% } %>
 	 </logic:equal>
  </logic:equal>
  
 <input type='button' id="button_goback" value='&nbsp;返回&nbsp;' onclick='goback();' class="mybutton"  style="margin-left: -2px;">
 
</td></tr></table>
 
</html:form>

<script language='javascript' >
<% 
if(request.getParameter("sava_type")!=null){
	if(request.getParameter("sava_type").equals("1"))
			out.print("alert('保存成功!');");
	else if(request.getParameter("sava_type").equals("2"))
			out.print("alert('提交成功!');");
}
 %>

</script>

</body>
</html>