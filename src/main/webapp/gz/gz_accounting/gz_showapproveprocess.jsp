<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<style type="text/css"> 

</style>
<html:form action="/gz/gz_accounting/gz_showapprove">
<table  width="97%"  border="0" align="right">
<tr>
<td>
<table  width="100%" border="0" cellspacing="0"    align="left" cellpadding="0" class="ListTable">
   
  <thead>
  <tr>	
    
    	 <td   align="center" class="TableRow" nowrap><bean:message key="menu.gz.process" /></td>    
  </tr>
  </thead>
  <tr>	
    	<td align="center" class="RecordRow" nowrap>
    	  <html:textarea name="accountingForm" property="appprocess"  cols="40" rows="13"  readonly="true" styleId="textboxMul"  />
    </td>
  
  </tr>
</table>
</td>
</tr>
<tr>
<td>
<table  width="100%" border="0" cellspacing="0">
  <tr>
   <td align="center">
   <html:button styleClass="mybutton" property="b_cancel" onclick="window.close();">
            		   <bean:message key="button.close"/>
	      			</html:button> 	
   </td>
   </tr>
</table>
</td>
</tr>
</table>
</html:form>


