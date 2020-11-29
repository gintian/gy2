<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%int i=0;%>
<html:form action="/hire/zp_plan/search_short_pos">
<br>
  <table width="50%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    <tr>
      <td colspan="2" align="center" class="TableRow" nowrap><bean:message key="label.zp_plan.short_position"/></td>
    </tr>
    <tr>
    <td>  
    <hrms:extenditerate id="element" name="zpplanForm" property="shortPosForm.list" indexes="indexes"  pagination="shortPosForm.pagination" pageCount="10" scope="session">
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
                 <hrms:checkmultibox name="zpplanForm" property="shortPosForm.select" value="true" indexes="indexes"/>&nbsp;
              </td>            
              <td align="left" class="RecordRow" nowrap>                
                   <bean:write  name="element" property="name" filter="true"/>&nbsp;
              </td>                          	    		        	        	        
          </tr>
        </hrms:extenditerate>
        <table  width="52%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpplanForm" property="shortPosForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
					<bean:write name="zpplanForm" property="shortPosForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="zpplanForm" property="shortPosForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpplanForm" property="shortPosForm.pagination"
				nameId="shortPosForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
       
</td>
</tr>
</table>
<table align="center">
  <tr>
           <td align="center"  nowrap  colspan="2">
               <hrms:submit styleClass="mybutton" property="b_save">
	 	        <bean:message key="button.save"/>
	       </hrms:submit>
	        <hrms:submit styleClass="mybutton" property="br_return">
                  <bean:message key="button.return"/>
              </hrms:submit>
          </td>
  </tr>
</table>

</html:form>
