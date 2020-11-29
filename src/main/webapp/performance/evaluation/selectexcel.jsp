<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
<script language="JavaScript" src="evaluation.js"></script>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<% 
	String planid=request.getParameter("planid");
	String object_type=request.getParameter("object_type");
	 %>
<script type="text/javascript">
	///修改陈总提的问题 把下载模板按钮放到批量导入页面
	function exportTemplate1(){
		var plan_id="<%=planid%>";
		var object_type="<%=object_type%>";
		var hashvo=new ParameterSet();
		hashvo.setValue("plan_id",plan_id);
		hashvo.setValue("object_type",object_type);
		var request=new Request({method:'post',asynchronous:false,onSuccess:outIsOk,functionId:'9024000298'},hashvo);
		
	}
	function outIsOk(outparamters)
	{

	var filename=outparamters.getValue("filename");
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;
	/*
	var filename=outparamters.getValue("filename");
	window.open("/servlet/DisplayOleContent?filename="+filename+".xls","xls");*/
	}
	function sub(){
		var fileEx = evaluationForm.file.value;
		if(fileEx == "")
        {
        	alert("<bean:message key='jx.import.select'/>！");
        	return ;
        }
    	var isRightPath = validateUploadFilePath(fileEx);
		if(!isRightPath)
			return;
        flag=true;
		var temp=fileEx;
		while(flag)
    	{
	    	temp=temp.substring(temp.indexOf(".")+1)
	    	if(temp.indexOf(".")==-1)
	    		flag=false;
    	}
        if(temp.toLowerCase()=='xls' || temp.toLowerCase()=='xlsx')
    	{
    		//var strurl="/performance/evaluation/performanceEvaluation.do?b_import=init&plan_id=<%=planid%>";
    		document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_import=init&plan_id=<%=planid%>&opt=new";
    		//window.open("/performance/evaluation/performanceEvaluation.do?b_import=init&plan_id=<%=planid%>",'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=200,left=250,width=596,height=354');
    		//var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
			//var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=500px;dialogHeight=280px;resizable=yes;scroll=no;status=no;");
			//evaluationForm.target="_self";
			document.evaluationForm.submit();
			//window.open("/performance/evaluation/performanceEvaluation.do?b_import=init&plan_id=<%=planid%>","_blank");
    	}
    	else
    	{
    		alert("<bean:message key='jx.import.error'/>！");
    		return;
    	}
	}
	function close_sel() {

  	  	parent.window.close();
 	}
</script>
<link rel="stylesheet" type="text/css" href="../../ajax/skin.css"></link>
<hrms:themes />
<title>批量导入</title>
</head>
<body>
<form action="/performance/evaluation/performanceEvaluation.do" method="post" name="evaluationForm" enctype="multipart/form-data">

<fieldset  style="height:100px;">
<legend>
批量导入
</legend>
<table  border="0" cellspacing="0"  align="center" cellpadding="0" style="top:20px;" width=90%' class="ListTable"  style="margin-top:20px" >
	<bean:message key="lable.performance.prompt1"/>

	<tr valign="middle">
		<td class="RecordRow" width="30%" align="right">
	请选择导入文件:
		</td>
		<td class="RecordRow" width="70%" align="left">
		&nbsp;&nbsp;<input type="file" name="file" class="inputtext">
		</td>
	</tr>
	</table>
</fieldset>	

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
	<td width="100%" align="center" height="35px;">
        <input type="button" class="mybutton" name="button" value="<bean:message key="button.download.template"/>" onclick="exportTemplate1();"	/>
 		<input type="button" class="mybutton" onclick="sub();" value="确定">
 		<input type="button" class="mybutton" onclick="close_sel()" value="关闭">
</td>
</tr>
</table>
</form>
</body>
</html>