<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%
  int i=0;
%>
<html:form action="/workflow/webclient/processlist">
<table width="70%" border="1" align="center" cellspacing="1" cellpadding="1" class="ListTable">
   	  <thead>
          <tr>
            <td align="center" class="TableRow" nowrap>
		流程号&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
                流程名称&nbsp;
            </td>
            <td align="center"  class="TableRow" nowrap>
                流程描述&nbsp;
            </td>  
            <td align="center" class="TableRow" nowrap>
		操作            	
	    </td>		        	        	        
          </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="workListForm" property="processListForm.list" indexes="indexes"  pagination="processListForm.pagination" pageCount="10" scope="session">
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
                    <bean:write name="element" property="id" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write name="element" property="name" filter="true"/>&nbsp;
            </td>
            <td align="left"  class="RecordRow" nowrap>
                    <bean:write name="element" property="description" filter="true"/>&nbsp;
            </td>  
            <td align="center" class="RecordRow" nowrap>
            	<a href="<bean:write name="element" property="edit_eform_path" filter="true"/>?b_viewprocess=link&a_processid=<bean:write name="element" property="id" filter="true"/>" >发启流程</a>
	    </td>		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table	width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="workListForm" property="processListForm.pagination.current" filter="true" />
					页

					共
					<bean:write name="workListForm" property="processListForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="workListForm" property="processListForm.pagination.pages" filter="true" />
					页
			</td>
	       <td  align="right" nowrap class="tdFontcolor">
		      <p align="right"><hrms:paginationlink name="workListForm" property="processListForm.pagination"
				nameId="processListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</html:form>
