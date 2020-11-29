<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
</style>

<html:form action="/hire/zp_options/informationlist">
<br>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>&nbsp;
	    </td>
   		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="infoMlrframeForm" property="testQuestionForm.list" indexes="indexes"  pagination="testQuestionForm.pagination" pageCount="10" scope="session">
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
     		   <hrms:checkmultibox name="infoMlrframeForm" property="testQuestionForm.select" value="true" indexes="indexes"/>&nbsp;            	    		   
	    </td>            
            <td align="center" class="RecordRow" nowrap>
            <a href="/hire/zp_options/showtestquestion?a_testid=<bean:write name="element" property="string(test_id)" filter="true"/>" target="_blank">
                   <bean:write name="element" property="string(name)" filter="true"/></a>&nbsp;
	    </td>
         
            <td align="center" class="RecordRow" nowrap>
                    <a href="/hire/zp_options/upload_file.do?b_query=link&a_testid=<bean:write name="element" property="string(test_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>            	
            </td>
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="infoMlrframeForm" property="testQuestionForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
					<bean:write name="infoMlrframeForm" property="testQuestionForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="infoMlrframeForm" property="testQuestionForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="infoMlrframeForm" property="testQuestionForm.pagination"
				nameId="testQuestionForm" propertyId="testQuestionProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center">
                   <hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	   </hrms:submit>
         	   <hrms:submit styleClass="mybutton" property="b_delete">
            		<bean:message key="button.delete"/>
	 	   </hrms:submit>                 
         	   <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	   </hrms:submit>      	          	   	 	  
            </td>
          </tr>          
</table>

</html:form>
