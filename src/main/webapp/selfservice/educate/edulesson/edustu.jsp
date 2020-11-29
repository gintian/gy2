<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>

<html:form action="/selfservice/educate/edulesson/edustu">
 <br>
 <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
 		<tr>
 		<td align="center" valign="center" nowrap colspan="10"  class="educationtitle">
 		<img src="/images/shimv.gif">&nbsp;<bean:message key="conlumn.infopick.educate.edulesson.edustu"/>&nbsp;<img src="/images/shimv1.gif">
 		</td>
 		</tr>
 </table>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
   	 
           <tr>
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="studentForm" fieldname="R4002" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="studentForm" fieldname="B0110" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="studentForm" fieldname="E0122" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="studentForm" fieldname="R4006" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="studentForm" fieldname="R4007" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="studentForm" fieldname="R4008" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="studentForm" fieldname="R4009" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
			<hrms:fieldtoname name="studentForm" fieldname="R4010" fielditem="fielditem"/>
			<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
	    
	    	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="studentForm" property="studentForm.list" indexes="indexes"  pagination="studentForm.pagination" pageCount="10" scope="session">
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
     		   <bean:write name="element" property="r4002" filter="true"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
            	   <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	   <bean:write name="codeitem" property="codename"/>
            	    &nbsp;
	    </td>
         
           
            <td align="left" class="RecordRow" nowrap>
            	   <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	   <bean:write name="codeitem" property="codename"/>
                   &nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="r4006" filter="true"/>&nbsp;
	    </td> 	                
            <td align="left" class="RecordRow" nowrap>
            	<bean:write name="element" property="r4007" filter="true"/>&nbsp;                  
	    </td>
	    <td align="right" class="RecordRow" nowrap>
	    	<bean:write name="element" property="r4008" filter="true"/>&nbsp;                  
	    </td>
	    <td align="left" class="RecordRow" nowrap>
	    	<bean:write name="element" property="r4009" filter="true"/>&nbsp;                  
	    </td>          
             <td align="right" class="RecordRow" nowrap>
	    	<bean:write name="element" property="r4010" filter="true"/>&nbsp;                  
	    </td>    
            	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="70%" align="center">
		<tr>
		   
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="studentForm" property="studentForm.pagination"
				nameId="studentForm" propertyId="roleListProperty">
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
