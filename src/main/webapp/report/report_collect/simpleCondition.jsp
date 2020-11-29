<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<!-- <script language="JavaScript" src="/js/meizzDate.js"></script> -->
	<SCRIPT LANGUAGE=javascript>

	function changeContext()
	{
		var temp=reportCollectForm.paramname.value;
		var codeset=temp.split('§§');
		var In_paramters="codeSetid="+codeset[1]; 				
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03030000006'});
	}
	
	function returnInfo(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldList");	
		AjaxBind.bind(reportCollectForm.paramvalue,fieldlist);
	}
	
	function closeWindow()
	{
		var valWin = parent.Ext.getCmp('editCollect');
		if(valWin)
			valWin.close();
		else
			window.close();	
	}
	
	function enter()
	{
		var temp=reportCollectForm.paramname.value;
		
		
		var values=new Array();
		var i=0;
		var a_select=eval("document.reportCollectForm.paramvalue");
		for(var a=0;a<a_select.options.length;a++)
		{
			if(a_select.options[a].selected==true)
				values[i++]=a_select.options[a].value;
		}
		if(values.length==0)	//如果没选条件,则关闭窗口
			closeWindow();
		else					
		{
			var hashvo=new ParameterSet();			
			hashvo.setValue("codeset",temp);
			hashvo.setValue("values",values);
			var In_paramters="unitcode=${reportCollectForm.unitcode}"; 
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnResultInfo,functionId:'03030000007'},hashvo);
		}
	}
	
	
	function returnResultInfo(outparamters)
	{
		var unitcodeList=new Array();
		unitcodeList=outparamters.getValue("unitcodeList");			
		if(unitcodeList.length==0)
			closeWindow();
		else
		{
			parent.objectlist=unitcodeList;
			closeWindow();
		}
	}
	
	</script>
<hrms:themes />
<html:form action="/report/edit_collect/reportCollect">	
	<table  width="100%" align="left" border="0" cellpadding="0" cellspacing="0">	
	<tr>
	<td>
    <fieldset align="left" style="width:90%;">
        <legend ><bean:message key="report_collect.simpleConditionCollect"/></legend>
        <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          <tr > 
            <td width="319" height="24" align="center"> 
              <select name="paramname" style="width:99%"  onchange="changeContext()" >
				<logic:iterate id="element" name="reportCollectForm" property="commonsParamList"  >             
					<OPTION  value='<bean:write name="element" property="paramename" />§§<bean:write name="element" property="paramCode" />§§<bean:write name="element" property="paramscope" />§§<bean:write name="element" property="sortid" />' > <bean:write name="element" property="paramname" /></OPTION>
         		</logic:iterate>
              </select> </td>
          </tr>
          <tr > 
            <td height="300px" align="center"> 
              <select name="paramvalue" size="20" multiple style="width:99%;height:300px;">
                <logic:iterate id="element" name="reportCollectForm" property="codeItemList"  >             
					<OPTION  value='<bean:write name="element" property="codeitemid" />' > <bean:write name="element" property="codeitemdesc" /></OPTION>
         		</logic:iterate>
              </select>
              </td>
          </tr>		  
          
        </table>
	</fieldset>
	</td>
	</tr>
	<tr> 
            <td align="center"> 
            <br>
            <input type="button" name="b_update" value="<bean:message key="button.ok"/>"  onclick='enter()'  class="mybutton"> 
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <input type="reset" value="<bean:message key="button.clear"/>" class="mybutton"> </td>
          </tr>
	</table>
</html:form>
