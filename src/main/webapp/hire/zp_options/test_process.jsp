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

<html:form action="/hire/zp_options/test_process">

<br>
<table width="60%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.name"/>
	    </td>	    
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>		    		        	        	        
           </tr>
   	  </thead>
   	   <hrms:extenditerate id="element" name="testProcessForm" property="testProcessForm.list" indexes="indexes"  pagination="testProcessForm.pagination" pageCount="10" scope="session">
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
          <logic:notEqual name="element" property="string(tache_id)" value="1">
             <logic:notEqual name="element" property="string(tache_id)" value="2">
                <logic:notEqual name="element" property="string(tache_id)" value="3">
                  <logic:notEqual name="element" property="string(tache_id)" value="4">
                   <hrms:checkmultibox name="testProcessForm" property="testProcessForm.select" value="true" indexes="indexes"/>  		   
	          </logic:notEqual>
	        </logic:notEqual>
	     </logic:notEqual>
	  </logic:notEqual>
	  </td>            
	  <td align="center" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(name)" filter="true"/>
	  </td>
	  <td align="center" class="RecordRow" nowrap>
                &nbsp;&nbsp;&nbsp;&nbsp;<a href="/hire/zp_options/add_change.do?b_query=link&a_id=<bean:write name="element" property="string(tache_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>&nbsp;&nbsp;&nbsp;&nbsp;            	
	   </td>
       </tr>
       </hrms:extenditerate>   	    	    	
        
</table>
<table  width="60%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="testProcessForm" property="testProcessForm.pagination.current" filter="true" />
				    <bean:message key="label.page.sum"/>
					<bean:write name="testProcessForm" property="testProcessForm.pagination.count" filter="true" />
				    <bean:message key="label.page.row"/>
					<bean:write name="testProcessForm" property="testProcessForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="testProcessForm" property="testProcessForm.pagination"
				nameId="testProcessForm" propertyId="testProcessProperty">
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
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
        
            </td>
          </tr>          
</table>

</html:form>
