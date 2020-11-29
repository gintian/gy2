<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>

<html:form action="/selfservice/educate/edulesson/eduitem">
 <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" >
 		<tr>
 		<td align="center" valign="center" nowrap colspan="10">
 		<h3><bean:message key="conlumn.infopick.educate.edulesson.eduiteminfo"/></h3>
 		</td>
 		</tr>
 </table>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
   	 
           <tr>
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="eduItemForm" fieldname="R1302" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="eduItemForm" fieldname="R1308" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="eduItemForm" fieldname="R1303" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="eduItemForm" fieldname="R1304" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="eduItemForm" fieldname="R1305" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="eduItemForm" fieldname="R1307" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
           
           	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="eduItemForm" property="eduItemForm.list" indexes="indexes"  pagination="eduItemForm.pagination" pageCount="10" scope="session">
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
            <td align="center" class="RecordRow" nowrap>
     		   <bean:write name="element" property="r1302" filter="true"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
            	<hrms:codetoname codeid="06" name="element" codevalue="r1308" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename"/>
                 &nbsp;
	    </td>
                
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="r1303" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="r1304" filter="true"/>&nbsp;
	    </td> 	                
            <td align="left" class="RecordRow" nowrap>
            	<bean:write name="element" property="r1305" filter="true"/>&nbsp;                  
	    </td>
	    <td align="left" class="RecordRow" nowrap>
	    	<bean:write name="element" property="r1307" filter="true"/>&nbsp;                  
	    </td>
    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="70%" align="center">
		<tr>
		   
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="eduItemForm" property="eduItemForm.pagination"
				nameId="eduItemForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>

<table  width="70%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
         	
        
            </td>
          </tr>          
</table>

</html:form>
