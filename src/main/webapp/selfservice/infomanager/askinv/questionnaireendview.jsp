<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>


<html:form action="/selfservice/infomanager/askinv/questionnaire" >
	  
         <table width="500" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
  		<tr>
    		<td class="RecordRow" nowrap colspan="2"><bean:message key="lable.topicname"/>:
   		 <bean:write name="questionnaireForm" property="name" filter="true"/>
   		 </td>
  		</tr>  
        
   		<logic:iterate  id="element" name="questionnaireForm" property="itemwhilelst"  scope="session">
    		<tr class="trDeep"> 
    		   <td  class="RecordRow" nowrap colspan="2">&nbsp;
    		   	<bean:write name="element" property="itemName"/>
    		   </td>
 		 </tr>
  		 <logic:iterate id="test" name="element" property="endviewlst" >
 		 <tr>
    		<td class="RecordRow" nowrap> 
    			<bean:write name="test" property="pointName" filter="true"/>
    		</td>
    		<td class="RecordRow" nowrap>&nbsp;
    			<bean:write name="test" property="sumNum" filter="true"/>
    			 <br>
        	 </td>
  		</tr>
 		</logic:iterate>
  		 <tr>
  		 <td colspan="2" align="center">
   			<hrms:chart name="element" title="" scope="page" legends="picList" data="" width="500" height="200" chart_type="11">
   			</hrms:chart>
   		</td>
   		</tr>
    
  		</logic:iterate>
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
