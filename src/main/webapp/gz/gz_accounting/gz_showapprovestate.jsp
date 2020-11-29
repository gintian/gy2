<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<html:form action="/gz/gz_accounting/gz_showapprove">
<table  width="97%"  border="0" align="right">
<tr>
<td>
<fieldset style="width:100%;height:230;align:center">
<table  width="100%"  border="0" cellspacing="0"    align="center" cellpadding="0" class="ListTable">
   
   <thead>
  <tr>	
    
    	 <td width='30%'  align="center" class="TableRow_right" style="border-top:0px;" nowrap><bean:message key="menu.gz.personnum" /></td>    
    	 <td width='70%'  align="center" class="TableRow_left" style="border-top:0px;" nowrap><bean:message key="menu.gz.state" /></td>    
  </tr>
  </thead>
  
   <logic:iterate id="element"  name="accountingForm"  property="statelist" >
   <tr>
 		<td align="center" class="RecordRow_right" nowrap>
	   			 <bean:write name="element" property="personnums" filter="false"/>
	   		</td>
	   		 <td align="center" class="RecordRow_left" nowrap>
	   			 <bean:write name="element" property="itemdesc" filter="false"/>
	   		</td>
	   		 </tr>
   </logic:iterate>
  
 
 
</table>
</fieldset>
</td>
</tr>
<tr>
<td>
<table  width="100%" border="0" cellspacing="0"    >
  <tr>
 
   <td align="center" colspan="2">
   <html:button styleClass="mybutton" property="b_cancel" onclick="window.close();">
            		      <bean:message key="button.close"/>
	      			</html:button> 	
   </td>
   </tr>
</table>
</td>
</tr>
</table>
<script language="JavaScript">

</script>
</html:form>


