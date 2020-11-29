<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>
<html:form action="/selfservice/propose/searchconsulant">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.man"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.date"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.consult"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.reply.man"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.reply.date"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.reply.content"/>&nbsp;
	    </td>		    	    	    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.view"/>            	
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.reply"/>            	
	    </td>	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="consulantForm" property="consulantForm.list" indexes="indexes"  pagination="consulantForm.pagination" pageCount="10" scope="session">
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
     		   <hrms:checkmultibox name="consulantForm" property="consulantForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(createuser)" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(createtime)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(ccontent)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(replyuser)" filter="true"/>&nbsp;
	    </td> 	                
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(replytime)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(rcontent)" filter="true"/>&nbsp;
	    </td>
	    	    
            <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/propose/viewconsulant.do?b_query=link&a_id=<bean:write name="element" property="string(id)" filter="true"/>"><img src="/images/view.gif" border=0></a>
	    </td>
            <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/propose/addconsulant.do?b_query=link&a_id=<bean:write name="element" property="string(id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>
	    </td>
            <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/propose/replyconsulant.do?b_query=link&a_id=<bean:write name="element" property="string(id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>
	    </td>	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="consulantForm" property="consulantForm.pagination.current" filter="true" />
					页
					共
					<bean:write name="consulantForm" property="consulantForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="consulantForm" property="consulantForm.pagination.pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="consulantForm" property="consulantForm.pagination"
				nameId="consulantForm" propertyId="roleListProperty">
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
        
            </td>
          </tr>          
</table>

</html:form>
