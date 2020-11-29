<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%> 
<%
	int i=0;
%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<style type="text/css">

</style>
<script language='javascript'>
function change(num,obj){
	document.getElementById("codelist["+num+"].selected").value=obj.value;
}
function save(){
	if(document.getElementById("mainItem").value==""){
	alert("标识指标不能为空!");
	return;
	}

	var url = "/hire/employActualize/employResumeImport.do?b_save=link";
	employResumeForm.action=url;
	employResumeForm.submit(); 
}

function goToPage(resumeset,fieldset,num){
	var value = document.getElementById("codelist["+num+"].selected").value;
	if(fieldset!=value){
	alert("进行指标对应前,请保存修改!");
	return;
	}
	var url = "/hire/employActualize/employResumeImport.do?b_itemCorrespond=link&resumeset="+getEncodeStr(resumeset)+"&fieldset="+fieldset;
	employResumeForm.action=url;
	employResumeForm.submit(); 
}
	
	function codeCorrespond(){
		employResumeForm.action="/hire/employActualize/employResumeImport.do?b_codeCorrespond=link&from_flag=1&resumeID="
		employResumeForm.submit(); 
	
	
	}
	function changeitem(obj){
	document.getElementById("mainItem").value=obj.value;
	}
</script>

<base id="mybase" target="_self">

<html:form action="/hire/employActualize/employResumeImport"  enctype="multipart/form-data" >
	<table align='center' width="700px" style="margin-top:50px">
		<tr>
			<td>
			<bean:message key="zp.resumeImport.mainItem"/>
			</td>
			<td>
			<html:select name="employResumeForm" property="itemID" size="1" onchange="changeitem(this);">
            <html:optionsCollection property="itemIDList" value="dataValue" label="dataName"/>
        	</html:select>
        	<input type='hidden' name="mainItem" value="<bean:write name="employResumeForm" property="itemID" filter="true"/>"   />  
		
			</td>
			<td align='left'>
			<bean:message key="zp.resumeImport.secItem"/>&nbsp;&nbsp;
			<html:select name="employResumeForm" property="secitemID" size="1">
            <html:optionsCollection property="itemIDList" value="dataValue" label="dataName"/>
            </html:select>
			</td>
			<td>
			
			</td>
		</tr>
		<tr>
			<td>
			<bean:message key="zp.resumeImport.updatemode"/>
			</td>
			<td  colspan="3">
			<html:select name="employResumeForm" property="mode" size="1">
            <html:optionsCollection property="modeList" value="dataValue" label="dataName"/>
        	</html:select>
        	<bean:message key="zp.resumeImport.modeDetail"/>
        	</td>
		</tr>
		<tr>
			<td colspan="2" >
			<bean:message key="zp.resumeImport.correspondInfo"/>
			</td>
		</tr>
		<tr>
			<td colspan="4" >
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
							<bean:message key="zp.resumeImport.baseItems"/>
							</td>
							<td align="center" class="TableRow" nowrap>
							<bean:message key="zp.resumeImport.fielditems"/>
							</td>
							<td align="center" class="TableRow" nowrap>
							<bean:message key="zp.resumeImport.itemCorrespond"/>
							</td>
						</tr>
					</thead>
					<logic:iterate id="element" name="employResumeForm" property="codelist" indexId="index" >
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
                   <bean:write name="element" property="resumeset" filter="true"/>
	    			</td>
	    			<td align="left" class="RecordRow" nowrap>	
				     <html:select name="element" property="fselected" size="1"  style="width:100%"  onchange='<%="change("+index+",this)"%>'>
			  	  		<html:optionsCollection  name="employResumeForm" property="flist" value="dataValue" label="dataName" />
					 </html:select>
					<input type='hidden' name='<%="codelist["+index+"].selected"%>'    value="<bean:write name="element" property="fselected" filter="true"/>"   />  
		
	    			</td>
            		<td align="center" class="RecordRow" nowrap>
   					<bean:write name="element" property="edit" filter="false"/>
	   				</td> 
          			</tr>
          			</logic:iterate>
				</table>
				<table  width="98%" align="center" class="RecordRowP">
</table>
<table  width="50%" align="center">
          <tr>
            <td align="center">
	 	<input type="button" value="<bean:message key="jx.eval.codeaccord"/>" class="mybutton" onclick="codeCorrespond()" />
		<input type="button" class="mybutton" value="<bean:message key="button.save"/>" onclick="save()" />
         	<hrms:submit styleClass="mybutton" property="br_back" >
            <bean:message key="button.return"/>
	 	</hrms:submit>	 	
            </td>
          </tr>          
</table>
			</td>
		</tr>
		
	</table>

</html:form>