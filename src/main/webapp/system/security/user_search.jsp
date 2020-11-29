<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>

<html:form action="/system/security/user_search">
<br>
<table width="50%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.username"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.fullname"/>&nbsp;
	    </td>
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.manage"/>            	
	    </td>	    
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.role.assign"/>            	
	    </td>	
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="loginpwdForm" property="userListForm.list" indexes="indexes"  pagination="userListForm.pagination" pageCount="10" scope="session">
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
                   <bean:write name="element" property="string(username)" filter="true"/>&nbsp;   		   
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(fullname)" filter="true"/>&nbsp;
	    </td>
            <td align="center" class="RecordRow" nowrap>
		<a href="/system/security/assignpriv.do?b_query=link&a_flag=0&a_tab=funcpriv&role_id=<bean:write name="element" property="string(username)" filter="true"/>"><img src="/images/assign_priv.gif" border=0></a>            	
	    </td>	    
            <td align="center" class="RecordRow" nowrap>
		<a href="/system/security/assign_role.do?b_query=link&a_userflag=0&a_roleid=<bean:write name="element" property="string(username)" filter="true"/>"><img src="/images/role_assign.gif" border=0></a>            	
	    </td>
	    

	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="50%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="loginpwdForm" property="userListForm.pagination.current" filter="true" />
					页
					共
					<bean:write name="loginpwdForm" property="userListForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="loginpwdForm" property="userListForm.pagination.pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="loginpwdForm" property="userListForm.pagination"
				nameId="userListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>


</html:form>
