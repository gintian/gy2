<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData" %>
<html>
  <head>
    
  </head>
  
  <body>
   <html:form action="/gz/gz_accounting/in_out">
  	<br>
  	 <%
   
  	  	  AcountingForm acountingForm=(AcountingForm)session.getAttribute("accountingForm");	
  	      ArrayList  originalDataList=acountingForm.getOriginalDataList();
  	      ArrayList  oriDataList=acountingForm.getOriDataList();
  	      ArrayList  sameDataList=acountingForm.getSameDataList();
  	      int i=0;
  	      String b_showRelation=request.getParameter("b_showRelation");
  	      
  	     %>
  	  <table>
  	
  	  <tr><td>
  	 <table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
							   	  <thead>
							   	  
							        <tr>
							        <% if(b_showRelation.equals("no")){  %>
								        <logic:iterate   id="element" name="accountingForm" property="originalDataList"  >
								         <td align="center" class="TableRow" nowrap>
										    <bean:write name="element" property="dataValue" filter="true"/>
								         </td>         
								        </logic:iterate>
								    <% } else { %>
								     
								      <logic:iterate   id="element" name="accountingForm" property="sameDataList"  >
								         <td align="center" class="TableRow" nowrap>
										    <bean:write name="element" property="dataName" filter="true"/>
								         </td>         
								        </logic:iterate>
								     
								     <% } %>
								     
							         </tr>
							         
							   	  </thead>
					<logic:iterate   id="element" name="accountingForm" property="oriDataList"  >
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
					          
					          <%
					           if(b_showRelation.equals("no")){
						          for(int j=0;j<originalDataList.size();j++)
						          {
						          	CommonData data=(CommonData)originalDataList.get(j);
						          	String temp=data.getDataValue();
					          %>
							  <td align="left" class="RecordRow" nowrap>
								  &nbsp;<bean:write name="element" property="<%=temp%>" filter="true"/>							
							  </td>  	  
							 <%
							 	  }
							   }
							   else
							   {
							   	   for(int j=0;j<sameDataList.size();j++)
						           {
							          	CommonData data=(CommonData)sameDataList.get(j);
							          	String temp=data.getDataValue();
							        
							 %>
							  <td align="left" class="RecordRow" nowrap>
								  &nbsp;<bean:write name="element" property="<%=temp%>" filter="true"/>							
							  </td> 
							 
							 <% 	}
							 	}
							  %>
							 </tr>  	  
		            </logic:iterate>
		            </table>
  		</td></tr>
  		<tr><td>
  		<Input type='button' value='取 消'  class="mybutton" onclick='window.close()'  />
  		</td></tr>
  	
  
    </html:form>
  </body>
</html>
