<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>
<html:form action="/selfservice/infomanager/askinv/searchoutline">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">

   	  <thead>
   	  <tr><td align="center" valign="center" nowrap colspan="10"><h3><bean:message key="conlumn.investigate_point.maintopic"/></h3></td></tr>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate_item.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate_point.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate_point.status"/>&nbsp;
	    </td>
                      	   	    	    	    
           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
	     
           	    	    		        	        	        
           </tr>
   	  </thead>
   	  
          <hrms:extenditerate id="element" name="outlineForm" property="outlineForm.list" indexes="indexes"  pagination="outlineForm.pagination" pageCount="10" scope="session">
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
     		   <hrms:checkmultibox name="outlineForm" property="outlineForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                  <bean:write name="outlineForm"  property="itemName" />&nbsp; 
	    </td>
         
            <td align="left" class="RecordRow" nowrap>
                  <bean:write name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <logic:equal name="element" property="string(status)" value="1">
	    	   <bean:message key="datestyle.yes"/>
	    	   </logic:equal>
	    	   <logic:equal name="element" property="string(status)" value="0">
	    	   <bean:message key="datesytle.no"/>
	    	   </logic:equal>&nbsp;
	    </td>
                        	    	    
           
            <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/infomanager/askinv/addoutline.do?b_query=link&pointid=<bean:write name="element" property="string(pointid)" filter="true"/>&status=<bean:write name="element" property="string(status)" filter="true"/>"><img src="/images/edit.gif" border=0></a>
	    </td>
	     
           	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="outlineForm" property="outlineForm.pagination.current" filter="true" />
					页
					共
					<bean:write name="outlineForm" property="outlineForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="outlineForm" property="outlineForm.pagination.pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="outlineForm" property="outlineForm.pagination"
				nameId="outlineForm" propertyId="roleListProperty">
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
