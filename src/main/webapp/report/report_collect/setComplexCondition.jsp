<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<!-- <script language="JavaScript" src="/js/meizzDate.js"></script> -->
	<SCRIPT LANGUAGE=javascript>
	
	function check()
	{
		
		var relation=new Array();
		var paramename=new Array();
		var operate=new Array();
		var codeValue=new Array();
		var a=0;
		if(document.reportCollectForm.relation.length)
		{
			for(var i=0;i<document.reportCollectForm.relation.length;i++)
			{
			
				var a_relation=document.reportCollectForm.relation[i].value;
				var a_paramename=document.reportCollectForm.paramename[i].value;
				var a_operate=document.reportCollectForm.operate[i].value;
				var name=$("aa"+(i+1)+".value");	
				relation[a]=a_relation;
				paramename[a]=a_paramename;
				operate[a]=a_operate;
				codeValue[a]=name.value;
				a++;
			}
		}
		else
		{
				var a_relation=document.reportCollectForm.relation.value;
				var a_paramename=document.reportCollectForm.paramename.value;
				var a_operate=document.reportCollectForm.operate.value;
				var name=$("aa1.value");	
				relation[0]=a_relation;
				paramename[0]=a_paramename;
				operate[0]=a_operate;
				codeValue[0]=name.value;
				a++;
		
		}	
		if(relation.length==0)
		{
			alert(SELECTCONDITION+"！");
			return;
		}
		
		
		var hashvo=new ParameterSet();
		 hashvo.setValue("relation",relation);
		 hashvo.setValue("paramename",paramename);
		 hashvo.setValue("operate",operate);
		 hashvo.setValue("codeValue",codeValue);
		 hashvo.setValue("unitcode","${reportCollectForm.unitcode}");
		 In_paramters='flag=1';
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03030000010'},hashvo);		
	}
	
	
	
	function returnInfo(outparamters)
	{
	
		var unitcodeList=new Array();		
		unitcodeList=outparamters.getValue("unitList");			
		//alert(unitcodeList.length);
		if(unitcodeList.length==0)
			closeWindow();
		else
		{		
			parent.objectlist=unitcodeList;
			closeWindow();
		}
	
	}
	
	function closeWindow()
	{
		var valWin = parent.Ext.getCmp('editCollect');
		if(valWin)
			valWin.close();
		else
			window.close();	
	
	}
	
	//上一步
	function pre_phase()
	{
		reportCollectForm.action="/report/edit_collect/reportCollect.do?br_prePhase=link";
      	reportCollectForm.submit();
	}
	
	
	
	
	</SCRIPT>
<base id="mybase" target="_self">	
<hrms:themes />	
<html:form action="/report/edit_collect/reportCollect">	
		<table width="98%"  align="center">
		<tr >
		<td>
    <fieldset  style="width:95%;">
        <legend ><bean:message key="report_collect.complexConditionCollect"/></legend>
        <div style="overflow-y: auto; height:300px">
        <table border="0" cellspacing="0" width="100%" height="296px" align="center" cellpadding="0" >
          
          <tr > 
            <td> 
               <table border="0"  cellspacing="0" width="100%" class="ListTable"  cellpadding="2" align="center">
                <tr> 
                  <td colspan="4"> 
                  <br>
                  <table border="0"  cellspacing="0" width="97%" class="ListTable1"  cellpadding="2" align="center">
                      <tr> 
                        <td width="16%" align="center" nowrap class="TableRow"><bean:message key="label.query.logic"/></td>
                        <td width="29%" align="center" nowrap class="TableRow"><bean:message key="kq.formula.parameter"/></td>
                        <td width="13%" align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
                        <td width="42%" align="center" nowrap class="TableRow"><bean:message key="edit_report.compareValue"/></td>
                      </tr>
          				<% int i=0; %>
          				<logic:iterate id="element" name="reportCollectForm" property="selectedParamList"  > 
               	 
                      <tr> 
                        <td align="center" class="RecordRow" nowrap > 
                         <% if(i++==0){ %>
                         	<input type='hidden' name='relation' value='*' />
                         <% } else { %>
                          <select name="relation" size="1">
                            <option value="*" selected="selected"><bean:message key="kq.wizard.even"/></option>
                            <option value="+"><bean:message key="kq.wizard.and"/></option>
                          </select> 
                          <% } %>&nbsp;
                          </td>
                        <td align="center" class="RecordRow" nowrap ><input type='hidden' name='paramename' value='<bean:write name="element" property="paramename" />§§<bean:write name="element" property="paramscope" />§§<bean:write name="element" property="sortid" />§§<bean:write name="element" property="paramname" />' />    <bean:write name="element" property="paramname" /></td>
                        <td align="center" class="RecordRow" nowrap > <select name="operate" size="1" style="width:100%">
                            <option value="=" selected="selected">=</option>
                            <option value="&gt;">&gt;</option>
                            <option value="&gt;=">&gt;=</option>
                            <option value="&lt;">&lt;</option>
                            <option value="&lt;=">&lt;=</option>
                            <option value="&lt;&gt;">&lt;&gt;</option>
                          </select> </td>
                       
                        <td align="left" class="RecordRow" nowrap>
                         <input type='hidden' name="aa<%=i%>.value" />
                         <input type="text" name="aa<%=i%>.hzvalue"   size="24" value="" readOnly class="TEXT4"> 
                          <img  src="/images/code.gif"  style="vertical-align:middle;" onclick='openCondCodeDialog("<bean:write name="element" property="paramCode" />","aa<%=i%>.hzvalue");'   />
                          </td>
                       
                      </tr>
                    
                    	 </logic:iterate>
                    
                      <!-- 查询定义才出现此选项 -->
                      <tr> 
                        <td align="center" nowrap class="RecordRow" colspan="4">&nbsp;</td>
                      </tr>
                    </table></td>
                </tr>
                <tr> 
                  <td height="15" colspan="4"></td>
                </tr>
              </table>	            				
			</td>
          </tr>		  
        </table>
        </div>
	</fieldset>
	</td>
	</tr>
	<tr>
	<td align="center" style="padding-top:10px">
	 <input type="button" value="<bean:message key="button.query.pre"/>" class="mybutton" onclick='pre_phase()'  />             
            <input type="reset" value="<bean:message key="button.clear"/>" class="mybutton">          
            <input type="button" name="b_update" value="<bean:message key="button.ok"/>"  onclick='check()'  class="mybutton"> 
	</td>
	</tr>
	</table>
</html:form>