<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
    <script language="javascript" src="/js/constant.js"></script>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<SCRIPT LANGUAGE=javascript>

	var n=-1;
	var info=parent.reportValInfo;
	var a_bean=new Array();

	if(info!="null")
	{
		if(info.indexOf("#")==-1)
			a_bean[0]=info;
		else
			a_bean=info.split("#");  //校验结果数组
	
	}
	
	
	
	//展现表内校验结果
	function inner_validate()
	{
		n++;
		if(n>=a_bean.length)
		{
			f1.area.value=VALIDATEFINISHED+'！';
		}
		else
		{	
				
			var bean_info_arr=a_bean[n].split("@");
			if(bean_info_arr[2]!="null")		//语法错误
			{
				f1.area.value=bean_info_arr[0]+"\n"+bean_info_arr[2];
			}
			else
			{
				f1.area.value=bean_info_arr[0]+"\n"+bean_info_arr[1];	
			}
		}
		
	}
	
	function closeWindow()
	{
		var valWin = parent.Ext.getCmp('reportValidate');
		if(valWin)
			valWin.close();
		else
			window.close();	
	
	}
	
	
	</script>
<HEAD>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes>
</HEAD>
<style>
.DetailTable{
	width:expression(document.body.clientWidth-10);
}
</style>
<body bgcolor="#F7FAFF"   >
<form name='f1'>
		<table  width="100%"  height="95%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   
 
		        <tr>  
		         <td width="100%" height="100%" align='center' >
						<TEXTAREA   name='area' rows='10' cols='45' style="height:99%;width:100%;" >
						</TEXTAREA>
		         </td>
		         </tr>
				<tr>
					<td align="center">
						<INPUT type='button' value=' <bean:message key="edit_report.continue"/> '  class='mybutton' onclick='inner_validate()'  style="margin-top: -1px;">			
						<INPUT type='button' value=' <bean:message key="button.cancel"/> ' class='mybutton' onclick='closeWindow()'  style="margin-top: 3px;">
					</td>
				</tr>
		 </table>
</form>

</body>
