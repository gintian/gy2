<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%
	int i=0;
%>
<html:form action="/workflow/webclient/tasklist">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
            <tr >
            <td align="center" class="TableRow" nowrap>
				任务发启人&nbsp;
	        </td>
            <td align="center" class="TableRow" nowrap>
				任务号&nbsp;
	        </td>
            <td align="center" class="TableRow" nowrap>
                任务名称&nbsp;
            </td>
            <td align="center" class="TableRow" nowrap>
                任务开始时间&nbsp;
            </td>  
            <td align="center" class="TableRow" nowrap>
		操作            	
	    </td>		        	        	        
            </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="workListForm" property="taskListForm.list" indexes="indexes"  pagination="taskListForm.pagination" pageCount="10" scope="session">
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
                    <bean:write name="element" property="start_actorid" filter="true"/>&nbsp;
	    </td>
         
            <td align="center" class="RecordRow" nowrap>
                    <bean:write name="element" property="id" filter="true"/>&nbsp;
	        </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write name="element" property="name" filter="true"/>&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write name="element" property="start_date" filter="true"/>&nbsp;
            </td>  
            <td align="center" class="RecordRow" nowrap>
            	<a href="/workflow/webclient/signal_endofstate.do?b_viewtask=link&a_taskid=<bean:write name="element" property="id" filter="true"/>" >审阅</a>
	        </td>		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="workListForm" property="taskListForm.pagination.current" filter="true" />
					页

					共
					<bean:write name="workListForm" property="taskListForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="workListForm" property="taskListForm.pagination.pages" filter="true" />
					页
			</td>
	       <td  align="right" nowrap class="tdFontcolor">
		      <p align="right"><hrms:paginationlink name="workListForm" property="taskListForm.pagination"
				nameId="taskListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</html:form>
