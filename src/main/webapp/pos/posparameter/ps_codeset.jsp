<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/pos/posparameter/ps_codeset">
<br>
<table width="50%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="pos.posparameter.ps_code"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
          <tr>
            <td align="center" class="RecordRow" nowrap>
     	       <bean:message key="pos.posparameter.selectps_code"/>
    	           <hrms:importgeneraldata showColumn="codesetdesc" valueColumn="codesetid" flag="true" paraValue="" 
                      sql="select codesetid,codesetdesc from codeset where codesetid<>'@K' and codesetid<>'UN' and codesetid<>'UM'" collection="list" scope="page"/>
                   <html:select name="posCodeParameterForm" property="ps_code" size="1">
                       <html:option value="#"><bean:message key="label.select.dot"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                </html:select>
	    </td>                  	        	        
          </tr>          
          <tr>
          <td align="center" class="RecordRow" nowrap>
              &nbsp;&nbsp;<input type="submit" name="b_save" class="mybutton" value="&nbsp;<bean:message key='button.ok'/>&nbsp;">
          </td>
          </tr>   
</table>
</html:form>
