<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="Javascript" src="/gz/salary.js"></script>
<html:form action="/gz/gz_accountingt/bankdisk/selectBankItem">

<table width='98%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="2">
		<bean:message key="gz.bankdisk.selectdataproject"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	  <td align="center" class="RecordRow" nowrap colspan="2">
   	   <div style="overflow:auto;width:100%;height:300px;">
   	  <table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <% int i=0;%>
   	  <logic:iterate id="element" name="bankDiskForm" property="allList" offset="0">
   	  <% if(i%2==0){%>
   	    <tr class="trShallow">
   	  <%}else{%>
   	   <tr class="trDeep">
   	  <%}%>
   	  <td width="10%" align="center" class='RecordRow' style="border-top:0px;">
   	  <logic:equal name="element" property="isSelect" value="1">
   	  <input type="checkbox" name="itemidArray" value="<bean:write name="element" property="itemid"/>" checked/>
   	  </logic:equal>
   	  <logic:equal name="element" property="isSelect" value="0">
   	   <input type="checkbox" name="itemidArray" value="<bean:write name="element" property="itemid"/>"/>
   	  </logic:equal>
   	  </td>
   	  <td align="left" class='RecordRow' style="border-top:0px;">
   	  &nbsp;&nbsp;<bean:write name="element" property="itemdesc"/>&nbsp;&nbsp;
   	  </td>
   	  </tr>
   	  <% i++;%>
   	  </logic:iterate>
   	  </table>
   	  </div>
   	  </td>
   	  </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="2" style="height:35px;">
              <html:button styleClass="mybutton" property="b_next" onclick="bankdisk_selectBankitem()">
            		      <bean:message key="reporttypelist.confirm"/>
	      </html:button> 	
	      <input type="button" name="back" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">       
          <input type="hidden" name="rightFields" value=""> 
          <input type="hidden" name="salaryid" value="${bankDiskForm.salaryid}">
          <input type="hidden" name="code" value="${bankDiskForm.code}">
          <input type="hidden" name="tableName" value="${bankDiskForm.tableName}">
          <input type="hidden" name="bank_id" value="${bankDiskForm.bank_id}">
          </td>
          </tr>  
          
</table>

</html:form>
