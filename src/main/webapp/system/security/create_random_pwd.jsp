<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes></hrms:themes>
<html:form action="/system/security/create_random_pwd">

<table width="70%" style="margin-top:8px;" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="accountForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;          	
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="account.label"/>            	
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.new.password"/>            	
	    </td>
  	    	    		        	        	        
           </tr>
   	  </thead>
         
          <hrms:extenditerate id="element" name="accountForm" property="accountlistForm.list" indexes="indexes"  pagination="accountlistForm.pagination" pageCount="20" scope="session">
          
	    <tr>
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;            
	    </td>
            <td align="left" class="RecordRow" nowrap>
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;     
	    </td>
            <td align="left" class="RecordRow" nowrap>
                 <bean:write name="element" property="a0101" filter="true"/>&nbsp;
	    </td>
            <td align="center" class="RecordRow" nowrap>
                 <bean:write name="element" property="username" filter="true"/>&nbsp;
	    </td>
            <td align="center" class="RecordRow" nowrap>
                 <bean:write name="element" property="password" filter="true"/>&nbsp;
	    </td>
           </tr>
           </hrms:extenditerate>
         <tr>
         <td colspan="8" class="RecordRow">
           <table  width="100%" align="center">
	    	<tr>
		       <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="accountForm" property="accountlistForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="accountForm" property="accountlistForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="accountForm" property="accountlistForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
		        	</td>
	                 <td  align="right" nowrap class="tdFontcolor">
		             <p align="right"><hrms:paginationlink name="accountForm" property="accountlistForm.pagination" nameId="accountlistForm" propertyId="accountlistProperty">
			    	</hrms:paginationlink>
		    	</td>
		   </tr>
         </table>
         </td>
         </tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
         	<hrms:submit styleClass="mybutton" property="b_send">
            		<bean:message key="button.send.password"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>	 	
            </td>
          </tr>          
</table>
</html:form>
