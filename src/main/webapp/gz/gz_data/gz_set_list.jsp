<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<hrms:themes />
<html:form action="/gz/gz_data/gz_set_list">
<br>
<table align='center' width='90%' ><tr><td>

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
   	  <thead>
        <tr>
         <td align="center" class="TableRow" nowrap >
		   <bean:message key="report.number"/>
	     </td>          
         <td align="center" class="TableRow" nowrap >
		     <bean:message key="label.gz.salarytype"/>	    
	     </td>         
         <td align="center" class="TableRow" nowrap >
		    <bean:message key="label.gz.appdate"/>
	     </td>
         <td align="center" class="TableRow" nowrap >
			<bean:message key="label.gz.count"/>
	     </td>
         <td align="center" class="TableRow" nowrap >
			<bean:message key="label.gz.operation"/>
         </td>                          		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="salaryDataForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="15" scope="session">
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
            <bean:write name="element" property="salaryid" filter="true"/>
	    </td>        
            <td align="left" class="RecordRow" nowrap>
                  <a href="/gz/gz_data/gz_org_tree.do?b_query=link&salaryid=<bean:write name="element" property="salaryid" filter="true"/>"> <bean:write name="element" property="cname" filter="true"/> </a>
	    </td>
         
            <td align="left" class="RecordRow">
                    <bean:write  name="element" property="appdate" filter="true"/>
            </td>
            <td align="center" class="RecordRow" nowrap>
     		 <bean:write  name="element" property="count" filter="true"/>
	    </td>   
            <td align="center" class="RecordRow" nowrap>
				<a href="/gz/gz_data/gz_org_tree.do?b_query=link&salaryid=<bean:write name="element" property="salaryid" filter="true"/>"><img src="/images/edit.gif" border=0></a>
			</td>		 	    	            
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="100%"  class='RecordRowP' align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="salaryDataForm" property="setlistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="salaryDataForm" property="setlistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="salaryDataForm" property="setlistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="salaryDataForm" property="setlistform.pagination"
				nameId="setlistform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</td></tr></table>

</html:form>
