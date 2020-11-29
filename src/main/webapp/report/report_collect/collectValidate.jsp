<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<!-- <script language="JavaScript" src="/js/meizzDate.js"></script> -->
	<SCRIPT LANGUAGE=javascript>

	var n=-1;
	var info=parent.info;
	
	//展现表内校验结果
	function validate()
	{
		f1.area.value="";
		if(info[0].length==0)
		{
			f1.area.value=REPORT_INFO40+"！ ";
		}
		else
		{
			for(var i=0;i<info[0].length;i++)
				f1.area.value+=info[0][i]+"\n";
		
		}
	}

	
	function next()
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("unitcode",info[3]);
		var unitcode=new Array();
		if(info[4]!='3'&&info[4]!='7')
		{
			for(var a=0;a<info[1].length;a++)
				unitcode[a]=info[1][a];
		}	
		hashvo.setValue("unitcodeList",unitcode);
		var tablist=new Array();
		for(var a=0;a<info[2].length;a++)
			tablist[a]=info[2][a];
		hashvo.setValue("tabidList",tablist);
		var In_paramters="operate="+info[4]; 
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03030000004'},hashvo);
		
	}
	
	function returnInfo(outparamters)
	{
		var info=outparamters.getValue("info");
		var formulaInfo=outparamters.getValue("formulaInfo");
		if(info=="success"&&formulaInfo.length==0)	 
			alert(COLLECTFINISHED+"!");
		else 
		{
			if(formulaInfo.length>0)
			{
				for(var a=0;a<formulaInfo.length;a++)
				{
					var a_info=formulaInfo[a];
					var arr=a_info.split("#");
					for(var b=0;b<arr.length;b++)
					{
						var a_arr=arr[b].split("@");
						alert(a_arr[0]+" \n"+a_arr[1]);
					}
				
				}
			}	
			alert(COLLECTFAILURE+"!");
		}
		closeWindow();
	}
	
	
	function closeWindow()
	{
		var valWin = parent.Ext.getCmp('editCollect');
		if(valWin)
			valWin.close();
		else
			window.close();	
	
	}
	
	
</script>
<html>
<HEAD>
<title>上报信息
</title>

<link href="/css/css1.css" rel="stylesheet" type="text/css">

</HEAD>
<hrms:themes />
<style>
.DetailTable{
	width:expression(document.body.clientWidth-10);
}
</style>
<body onload="validate()"  >
<form name='f1' style="width: 100%;height: 100%;">
		<table  width="100%" height="100%" border="0" cellpmoding="0" class="DetailTable"  cellpadding="0">   
		        <tr>  
		         <td width="100%" height="100%" align='center' >
						<textarea name='area' rows='15' cols='90' style="height:99%;width:100%;">
						</textarea>
		         </td>
		         </tr>
		         <tr>
		         	<td align="center">
						<INPUT type='button' value=' <bean:message key="edit_report.continue"/> ' class='mybutton' onclick='next()'  >			
						<INPUT type='button' value=' <bean:message key="button.cancel"/> ' class='mybutton' onclick='closeWindow()'  >
		         	</td>
		         </tr>
		 </table>
</form>

</body>
</html>
