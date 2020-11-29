<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/system/options/ad_authenticateset">
<table width="50%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:6px;">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="2">
		<bean:message key="system.options.adauthset"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	     <td width="20%" align="right" class="RecordRow" nowrap>
   	           <bean:message key="system.options.adcn"/>&nbsp;
   	     </td>
   	     <td width="80%" align="left" class="RecordRow" nowrap>
   	           <html:text  name="adAuthenticateForm" property="domain_name" styleClass="textColorWrite" size="30" style="width:300px;"/>
   	             
   	     </td>
   	  </tr>  
   	  <tr>
   	     <td width="20%" align="right" class="RecordRow" nowrap>
   	           <bean:message key="system.options.adserver"/>&nbsp;
   	     </td>
   	     <td width="80%" align="left" class="RecordRow" nowrap>
   	           <html:text  name="adAuthenticateForm" property="host" styleClass="textColorWrite" maxlength="50" size="30" style="width:300px;"/>
   	     </td>
   	  </tr>  
   	   <tr>
   	     <td width="20%" align="right" class="RecordRow" nowrap>
   	           <bean:message key="system.options.adport"/>&nbsp;
   	     </td>
   	     <td width="80%" align="left" class="RecordRow" nowrap>
   	          <html:text  name="adAuthenticateForm" property="port" styleClass="textColorWrite" maxlength="3" size="30" style="width:300px;"/>
   	     </td>
   	  </tr>  
   	   <tr>
   	     <td width="20%" align="right" class="RecordRow" nowrap>
   	           <bean:message key="system.options.ldaptype"/>&nbsp;
   	     </td>
   	     <td width="80%" align="left" class="RecordRow" nowrap>
   	          <html:text  name="adAuthenticateForm" property="ldaptype" styleClass="textColorWrite" maxlength="50" size="30" style="width:300px;"/>  
   	     </td>
   	  </tr>  
   	  <tr>
   	     <td width="20%" align="right" class="RecordRow" nowrap>
   	           <bean:message key="parttime.param.flag"/>&nbsp;
   	     </td>
   	     <td width="80%" align="left" class="RecordRow" nowrap>
   	           <html:radio name="adAuthenticateForm" property="ldapset" value="true"/>是
               <html:radio name="adAuthenticateForm" property="ldapset" value="false"/>否&nbsp;&nbsp;（设置完成后需重启服务器）
   	     </td>
   	  </tr>  
   	 <tr>	  
          <td align="center" class="RecordRow" nowrap  colspan="2" style="height: 35px">
               <hrms:submit styleClass="mybutton" property="b_save">
            		      <bean:message key="button.save"/>
	        </hrms:submit> 	         
          </td>
          </tr>   
</table>
</html:form>
