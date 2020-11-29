
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*" %>
<html>
  <head>

    
<hrms:themes />
     
     </head>
  <body>
    <html:form action="/performance/commend/insupportcommend/executingVoteAnalyse">
	<Br>
	<br>
	<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
	 <tr>
            <td align="center" class="TableRow" nowrap>
             <bean:message key="label.commend.i_name"/>
             </td>
            <td align="center" class="TableRow" nowrap>
	       <bean:message key="label.commend.unit"/>
	   	 	</td>
            <td align="center" class="TableRow" nowrap>
	        <bean:message key="label.commend.p_name"/>
	   		 </td>
            <td align="center" class="TableRow" nowrap>
	        <bean:message key="label.commend.um"/>
	    	</td>            
		    <td align="center" class="TableRow" nowrap>
			<bean:message key="label.commend.vote"/>
		    </td>
		     	        	        
         </tr>
   	  </thead>
   	  <% int i=0;%>
   	   <logic:iterate id="element" name="inSupportCommendForm" property="executeVoteAnalyseList"  offset="0"> 
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
            &nbsp;<bean:write name="element" property="p0203" />&nbsp; 
	         
	   	 	</td>
            <td align="center" class="RecordRow" nowrap>
	          &nbsp;<bean:write name="element" property="b0110" />&nbsp;	 
	   		 </td>
            <td align="center" class="RecordRow" nowrap>
            &nbsp;<bean:write name="element" property="a0101" />&nbsp;
	    	</td>            
		    <td align="center" class="RecordRow" nowrap>
			&nbsp;<bean:write name="element" property="e0122" />&nbsp;
		    </td>	
		    <td align="center" class="RecordRow" nowrap>
			&nbsp;<bean:write name="element" property="p0304" />&nbsp;
		    </td>	             
         </tr>
   	     
   	     </logic:iterate>
   	     
 	</table> 
<table  width="80%" border="0" align="center">
          <tr>
            <td align="center"> 
             <button class="mybutton" name="" onclick="window.close();"> <bean:message key="button.close"/></button>
         	</td>
         </tr>
</table>

  
   	  </html:form>
    
  </body>
</html>