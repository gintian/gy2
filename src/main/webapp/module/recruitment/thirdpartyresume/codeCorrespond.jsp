<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.module.recruitment.thirdpartyresume.actionform.EmployResumeForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%
	int i=0;
	EmployResumeForm employResumeForm=(EmployResumeForm)session.getAttribute("importResumeForm");
	String resumeset=employResumeForm.getResumeset();
	String fieldset=employResumeForm.getFieldSet();
%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<style type="text/css">

</style>
<script language='javascript'>
function Refresh(obj){

var url = "/module/recruitment/thirdpartyresume.do?b_codeCorrespond=link&resumeID="+getEncodeStr(obj.value);
importResumeForm.action=url;
importResumeForm.submit(); 

}

function saveCode(){

	var count = document.getElementsByName("resumeinfo").length;
	var list="";

	for(var i=0;i<count;i++){
		var value = document.getElementsByName("resumeinfo")[i].value;
		var id = document.getElementsByName("resumeinfo")[i].id;
		var map=id+"="+value+"|";
		list=list+map
	}
	
	var hashvo=new ParameterSet();
	hashvo.setValue("list",list);
	hashvo.setValue("resumeID","${importResumeForm.resumeID}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfoRefresh,functionId:'ZP0000002609'},hashvo);
}

	function returnInfoRefresh(outparameters)
	{
	     importResumeForm.action='/module/recruitment/thirdpartyresume.do?b_codeCorrespond=link';
    	 importResumeForm.submit();
	}
	
	function autoCorrespond()
	{
	
	var hashvo=new ParameterSet();
	hashvo.setValue("resumeID","${importResumeForm.resumeID}");
	hashvo.setValue("commonvalue","${importResumeForm.commonvalue}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfoRefresh,functionId:'ZP0000002610'},hashvo);
	
	}
	
	function sub(o)
	{
	
	var pagenum=document.getElementById("pagenum").value;
	var zhengzhengshu=/^[0-9]*[1-9][0-9]*$/;///正整数
	if(!zhengzhengshu.test(pagenum)){
		alert("每页显示条数请输入正整数!");
		return;
	}
	}
	function back(){
		var resumeset="<%=resumeset%>";
		var fieldset="<%=fieldset%>"
		var url = "/module/recruitment/thirdpartyresume.do?b_itemCorrespond=link&resumeset="+getEncodeStr(resumeset)+"&fieldset="+fieldset;
		importResumeForm.action=url;
		importResumeForm.submit(); 
	}
	function goback(){
	     var url = "/module/recruitment/thirdpartyresume.do?b_defineScheme=link";
		importResumeForm.action=url;
		importResumeForm.submit();
	}
</script>

<br>
<base id="mybase" target="_self">

<html:form action="/module/recruitment/thirdpartyresume"  enctype="multipart/form-data" >
    <div style="margin-top:50px;">
	<fieldset align="center" style="width:700">
    <legend ><bean:message key="zp.resumeImport.selectCodeitem"/></legend>
	<table align='center' width="85%">
		<tr>
			<td colspan="4">
			<bean:message key="zp.resumeImport.codeCorrespond"/>
			<html:select name="importResumeForm" property="resumeID" size="1" onchange="Refresh(this)" >
            <html:optionsCollection property="clist" value="dataValue" label="dataName"/>
        	</html:select>
			<button onclick="autoCorrespond();" class="mybutton" style="margin-left:10px;"><bean:message key="zp.resumeImport.autoCorrespond"/></button>
			</td>
		</tr>
		<tr>
			<td colspan="4" >
				<table width="98%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:10px;">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
							<bean:message key="zp.resumeImport.resumeInfo"/>
							</td>
							<td align="center" class="TableRow" nowrap>
							<bean:message key="zp.resumeImport.codeitemid"/>
							</td>
							<td align="center" class="TableRow" nowrap>
							<bean:message key="zp.resumeImport.codeitemdesc"/>
							</td>
						</tr>
					</thead>
					<hrms:extenditerate id="element" name="importResumeForm" property="codeitemlistform.list" indexes="indexes"  pagination="codeitemlistform.pagination" scope="session">
          			<%
          			if(i%2==0)
          			{
          			%>
          			<tr class="trShallow">
          			<%}
          			else
          			{%>
          			<tr class="trDeep">
          			<%
          			}
          			i++;          
          			%>
          			<td align="left" class="RecordRow" nowrap>
          			<bean:write name="element" property="input" filter="false"/>
          			</td>
          			<td align="left" class="RecordRow" nowrap>
          			<bean:write name="element" property="ehritemid" filter="true"/>
          			</td>
          			<td align="left" class="RecordRow" nowrap>
          			<bean:write name="element" property="itemname" filter="true"/>
          			</td>
          			</tr>
          			</hrms:extenditerate>
				</table>
				<table  width="98%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="importResumeForm" property="codeitemlistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="importResumeForm" property="codeitemlistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="importResumeForm" property="codeitemlistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="importResumeForm" property="codeitemlistform.pagination"
				nameId="codeitemlistform" propertyId="codeitemlistProperty">
				</hrms:paginationlink>
				<input type='hidden' value="${importResumeForm.resumeID}"  name='resumeID'>
			</td>
		</tr>
		 
</table>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:10px;" >
	<tr>
		<td><bean:message key="zp.resumeImport.resumeInfock"/></td>
	</tr>
	<tr>
		<td><html:textarea name="importResumeForm" property="commonvalue" cols="96" rows="7"  readonly="true"/></td>
	</tr>
</table>
<table  width="50%" align="center">
          <tr>
            <td align="center">
            <input type="button" value="<bean:message key="button.save"/>" class="mybutton" onclick="saveCode()" />
            <logic:equal name="importResumeForm" property="from_flag" value="1"><!-- 从总的代码对应进入 -->
         <input type="button" value="<bean:message key="button.return"/>" class="mybutton" onclick="goback()" />	
	 	</logic:equal>
	 	<logic:equal name="importResumeForm" property="from_flag" value="2"><!-- 从个别的指标集的代码对应进入 -->
	 	         	<!--返回时发生错误，所以修改一下
	 	         	<hrms:submit styleClass="mybutton" property="br_return2">
            		<bean:message key="button.return"/>
	 				</hrms:submit>
	 				-->
	 				<input type="button" value="<bean:message key="button.return"/>" class="mybutton" onclick="back()" />	 	
	 	</logic:equal>
            </td>
          </tr>          
</table>
			</td>
		</tr>
		
	</table>
</fieldset>
</div>
</html:form>