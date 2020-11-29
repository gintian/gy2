<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
   int i = 0;
%>

<html:form action="/hire/zp_resource/add_zp_resource">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="hire.resource.class"/></td>
		 </tr> 
		  <tr>
		    <td><html:hidden name="zpresourceForm" property="type_id_type" styleClass="text6"/></td>
		 </tr>
                  <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.type_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpresourceForm" property="zpresourceSetvo.string(type_id)" disabled = "true"  styleClass="text6"/>
                     </td> 
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.name"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="zpresourceForm" property="zpresourceSetvo.string(name)" styleClass="text6"/>
                     </td> 
                 </tr>
                <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.status"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                           <select name="status">
                                <option value="0"><bean:message key="label.zp_resource.status0"/></option>
                                <option value="1"><bean:message key="label.zp_resource.status1"/></option>
                           </select>
                      </td>
                          
                   </tr>   
              <tr>
              <td align="center"  nowrap colspan="4">
	 	     <hrms:submit styleClass="mybutton" property="b_save"><bean:message key="button.save"/></hrms:submit>
	 	     <html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>
	 	      <hrms:submit styleClass="mybutton" property="b_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr>                                                        
          
          </table>     
        </td>
      </tr>         
        <tr class="list3">
            <td align="center" colspan="2">
		&nbsp           
            </td>
          </tr>   

          <tr>
          <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
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
		<bean:message key="label.zp_resource.status"/>&nbsp;
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
            <td align="left" class="RecordRow" nowrap>
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
	    <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(description)" filter="true"/>&nbsp;
	    </td>                
	    <td align="center" class="RecordRow" nowrap>
	         <logic:equal name="element" property="string(status)" value="0">
                      <bean:message key="label.zp_resource.status0"/>&nbsp;
                 </logic:equal>
                  <logic:notEqual name="element" property="string(status)" value="0">
                      <bean:message key="label.zp_resource.status1"/>&nbsp;
                 </logic:notEqual>
	    </td>
	   <td align="center" class="RecordRow" nowrap>
	     <logic:equal name="zpresourceForm" property="flag_set" value="1">
	        <logic:equal name="zpresourceForm" property="flag_mid" value="1">
		   <a href="/hire/zp_resource/add_resource.do?b_query=link&a_id=<bean:write name="element" property="string(type_id)" filter="true"/>&resource_id=<bean:write name="element" property="string(resource_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	
	        </logic:equal>
	     </logic:equal>
	     <logic:notEqual name="zpresourceForm" property="flag_set" value="1">
		   <a href="/hire/zp_resource/add_resource.do?b_query=link&a_id=<bean:write name="element" property="string(type_id)" filter="true"/>&resource_id=<bean:write name="element" property="string(resource_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	
	     </logic:notEqual>
	    </td>	   	    
               		        	        	        
          </tr>
        </hrms:extenditerate>
   	    
     </table>
     <table  width="70%" align="center">
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
     <table  width="70%" align="center">
          <tr>
            <td align="center">
            <logic:equal name="zpresourceForm" property="flag_set" value="1">
              <logic:equal name="zpresourceForm" property="flag_mid" value="0">
         	<hrms:submit styleClass="mybutton" property="b_add" disabled = "true">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_delete" disabled = "true">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	    </logic:equal>
	 	<logic:notEqual name="zpresourceForm" property="flag_mid" value="0">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_delete">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	      </logic:notEqual>	
	    </logic:equal> 
	   <logic:notEqual name="zpresourceForm" property="flag_set" value="1">
         	<hrms:submit styleClass="mybutton" property="b_add" >
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_delete">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	    </logic:notEqual>		
         	
        
            </td>
          </tr>          
    </table>
       
    </tr>            
 </table>
</html:form>
