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
<script language="javascript">

</script>
<html:form action="/hire/zp_resource/zp_resource_set">

<br>
<table width="60%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.type_id"/>&nbsp;
	    </td>
            <td width="10%" align="center" class="TableRow" nowrap>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="label.zp_resource.name"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;            	
	    </td>	    	    
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>		    		        	        	        
           </tr>
   	  </thead>
   	   <hrms:extenditerate id="element" name="zpresourceForm" property="zpresourceSetForm.list" indexes="indexes"  pagination="zpresourceSetForm.pagination" pageCount="10" scope="session">
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
                   <hrms:checkmultibox name="zpresourceForm" property="zpresourceSetForm.select" value="true" indexes="indexes"/> &nbsp; 		   
	    </td>            
            <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(type_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
	    <td align="center" class="RecordRow" nowrap>
                 <a href="/hire/zp_resource/add_resource_type.do?b_query=link&a_id=<bean:write name="element" property="string(type_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>            	
	    </td>
       </tr>
       </hrms:extenditerate>   	    	    	
        
</table>
<table  width="60%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpresourceForm" property="zpresourceSetForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
					<bean:write name="zpresourceForm" property="zpresourceSetForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="zpresourceForm" property="zpresourceSetForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpresourceForm" property="zpresourceSetForm.pagination"
				nameId="zpresourceSetForm" propertyId="zpresourceSetProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="60%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
        
            </td>
          </tr>          
</table>

</html:form>
