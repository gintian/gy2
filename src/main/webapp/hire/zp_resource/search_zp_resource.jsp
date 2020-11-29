<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
   function resource_change()
   {
      zpresourceForm.action="/hire/zp_resource/search_zp_resource.do?b_query=link";
      zpresourceForm.submit();
   }
</script>

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
<html:form action="/hire/zp_resource/search_zp_resource">

<br>
<table width="60%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
   	  <tr class="trDeep">
   	     <td align="left" class="RecordRow" nowrap colspan="12">
		 <bean:message key="label.zp_resource.set"/>&nbsp;
                   <hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="false"  paraValue="1"
                      sql="select type_id,name from zp_resource_set where 1=? " collection="list" scope="page"/> 
            	     <html:select name="zpresourceForm" property="zpresourcevo.string(type_id)" size="1" onchange="resource_change();"> 
            	         <html:option value="#"><bean:message key="label.select.dot"/></html:option>
            	         <html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            	     </html:select>
            	     <hrms:submit styleClass="mybutton" property="b_repair">
            		<bean:message key="button.maintenance"/>
	 	     </hrms:submit>
            </td>  
   	  </tr>
           <tr>
            <td align="center" class="TableRow" nowrap>
		 <bean:message key="column.select"/>&nbsp;
            </td>         
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.name"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.area"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.scope"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.charge"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.phone"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.linkman"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.address"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.postalcode"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.http"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.description"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td> 	    		        	        	        
           </tr>
   	  </thead>
   	   <hrms:extenditerate id="element" name="zpresourceForm" property="zpresourceForm.list" indexes="indexes"  pagination="zpresourceForm.pagination" pageCount="10" scope="session">
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
	   	 <hrms:checkmultibox name="zpresourceForm" property="zpresourceForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>           
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(area)" filter="true"/>&nbsp;
	    </td> 
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(scope)" filter="true"/>&nbsp;
	    </td>
	    </td>
            <td align="right" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(charge)" filter="true"/>&nbsp;
	    </td>
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(phone)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(linkman)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(address)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(postalcode)" filter="true"/>&nbsp;
	    </td>	
	    <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(http)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap width="100" style="word-break:break-all">
                    <bean:write  name="element" property="string(description)" filter="false"/>&nbsp;
	    </td>                
	   <td align="center" class="RecordRow" nowrap>
		<a href="/hire/zp_resource/zp_resource.do?b_query=link&a_id=<bean:write name="element" property="string(resource_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	
	    </td>	   	    
               		        	        	        
          </tr>
        </hrms:extenditerate>    	
        
</table>
<table  width="60%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpresourceForm" property="zpresourceForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zpresourceForm" property="zpresourceForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="zpresourceForm" property="zpresourceForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpresourceForm" property="zpresourceForm.pagination"
				nameId="zpresourceForm" propertyId="zpresourceProperty">
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
        
            </td>
          </tr>          
</table>

</html:form>
