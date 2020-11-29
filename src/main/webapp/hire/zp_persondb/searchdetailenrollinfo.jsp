<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	  int i=0;
%>
<script language="javascript">
    function exeButtonAction(actionStr,target_str)
   {
       target_url=actionStr;
       window.open(target_url,target_str); 
   }
   
  function goback()
  {
  		zppersondbForm.action="/templates/menu/hire_m_menu2.do?b_query=link&module=7";
  		zppersondbForm.target="i_body";
  		zppersondbForm.submit();
  	
  }
</script>
<html:form action="/hire/zp_persondb/searchdetailenrollinfo">
<br>
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
  <tr width="100%" align="center">
    <td align="center" width="100%" colspan="4">    
      <bean:write  name="zppersondbForm" property="existusermessage"/>&nbsp;   
     </td>
    </tr>
   </table>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
             <td align="center" class="TableRow" nowrap>
              <bean:message key="column.select"/>&nbsp;
             </td>
            <logic:iterate id="element"    name="zppersondbForm"  property="zpfieldlist"> 
              <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc"/>&nbsp; 
              </td>
             </logic:iterate>  
               <td align="center" class="TableRow" nowrap>
		  <bean:message key="label.edit"/>            	
               </td> 
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="zppersondbForm" property="zppersondbForm.list" indexes="indexes"  pagination="zppersondbForm.pagination" pageCount="10" scope="session">
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
               <hrms:checkmultibox name="zppersondbForm" property="zppersondbForm.select" value="true" indexes="indexes"/>&nbsp;
            </td>
            <logic:iterate id="info"    name="zppersondbForm"  property="zpfieldlist">            
              <td align="left" class="RecordRow" nowrap>                
                   <bean:write  name="element" property="string(${info.itemid})" filter="true"/>&nbsp;
              </td>
             </logic:iterate>
              <td align="center" class="RecordRow" nowrap>
            	<a href="/hire/zp_persondb/personinfoenroll.do?b_enroll=link&a0100=${zppersondbForm.a0100}&i9999=<bean:write  name="element" property="string(i9999)" filter="true"/>&actiontype=update"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	      </td>
                           	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zppersondbForm" property="zppersondbForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zppersondbForm" property="zppersondbForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="zppersondbForm" property="zppersondbForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zppersondbForm" property="zppersondbForm.pagination"
				nameId="zppersondbForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="left">
          <tr>
            <td align="center">
                   <html:hidden name="zppersondbForm" property="actiontype" value="new"/>
                   <html:hidden name="zppersondbForm" property="a0100" value="${zppersondbForm.a0100}"/>
                    	   <hrms:submit styleClass="mybutton" property="b_new">
            		<bean:message key="button.insert"/>
	 	   </hrms:submit>  
         	   <hrms:submit styleClass="mybutton" property="b_delete">
            		 <bean:message key="button.delete"/>
	 	   </hrms:submit>  	 
	 	    <logic:equal name="zppersondbForm" property="isHandWork" value="1">
	          		<input type='button' value="<bean:message key="button.return"/>" onclick="goback()"  class="mybutton" />
	   		 </logic:equal>
	 	      
	 	<!--<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/info/editselfinfo.do?br_return=link','il_body')"> -->
	     </td>
          </tr>          
 </table>
</html:form>
