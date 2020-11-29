<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
 int i=0;
%>
<html:form action="/selfservice/educate/infopick/searchinfopickdetail">

     
<table width="500" border="0" cellpadding="0"  cellspacing="0" class="ListTable" align="center">
 <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>          
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.infopick.advicestartdate"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.infopick.demandtype"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.infopick.timelength"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.infopick.exigenceprogram"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.infopick.manageradv"/>&nbsp;
	    </td>
	   
	    <td align="center" class="TableRow" nowrap>
		 <bean:message key="conlumn.infopick.leaderadv"/> &nbsp;          	
	    </td>
	     <td align="center" class="TableRow" nowrap>
		      <bean:message key="conlumn.infopick.leaderadv"/>&nbsp; 	
	    </td>
	     <td align="center" class="TableRow" nowrap>
		      <bean:message key="conlumn.infopick.demandgoal"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
          
	    
	    	    	    	    		        	        	        
           </tr>
        </thead>
  <tr> 
    	<td colspan="4" class="framestyle">
     <hrms:extenditerate id="element" name="infoPickForm" property="pickInfoDetaillst" indexes="indexes"  pagination="infoPickForm.pagination" pageCount="10" scope="session"> 
     <table width="500" border="0" cellpmoding="0" cellspacing="0" border="1" class="ListTable"  cellpadding="0"> 
  		
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
    		<td class="RecordRow" align="left" nowrap valign="center">
    		</td>
   		 <td class="RecordRow" > 
   		 <bean:write name="element" property="r2205" filter="true"/> 
    		</td>
    		
   		 <td class="RecordRow" > 
   			 <logic:equal name="element" property="r2203" value="1"> 
     			 <bean:message key="conlumn.infopick.naturaldemand"/>
     		  	</logic:equal> <logic:equal name="element" property="r2203" value="2"> 
      			<bean:message key="conlumn.infopick.itemdemand"/> 
      			</logic:equal> 
      		</td>
 		   		
    		 <td class="RecordRow" >&nbsp;
    		 <bean:write name="element" property="r2206" filter="true"/>
    		 </td>
    		
    		<td class="RecordRow" > 
    		<logic:equal name="element" property="r2207" value="1"> 
      		<bean:message key="conlumn.infopick.common"/> </logic:equal> 
      		<logic:equal name="element" property="r2207" value="2"> 
      		<bean:message key="conlumn.infopick.exigence"/> </logic:equal> 
      		<logic:equal name="element" property="r2207" value="3"> 
      		<bean:message key="conlumn.infopick.moreexigence"/> </logic:equal>
      		 <logic:equal name="element" property="r2207" value="4"> 
      		<bean:message key="conlumn.infopick.mostexigence"/> </logic:equal> &nbsp;
      		</td>
 	
   		 <td class="RecordRow" colspan="3">
   		 <bean:write name="element" property="r2208" filter="true"/>&nbsp;
   		 </td>
  	
    		<td class="RecordRow" colspan="3">
    		<bean:write name="element" property="r2209" filter="true"/>&nbsp;
    		</td>
  		
    		<td class="RecordRow" colspan="3"> 
    		<bean:write name="element" property="r2210" filter="true"/> 
    		</td>
 	 </tr>
</table>

     		  </hrms:extenditerate>
  </td>
          </tr>
                  
          <tr class="list3" height="10">
           	<td align="right" nowrap valign="top" colspan="4">&nbsp;</td> 
            
          </tr>                                                      
          <tr class="list3">
            	<td align="center" colspan="4">
         		 	
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>            
            </td>
          </tr>          
      </table>
</html:form>
