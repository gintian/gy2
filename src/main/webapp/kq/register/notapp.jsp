<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/notapp">


<%
int i=0;
%>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
  <td align="center">
      <bean:message key="kq.register.notapp.e0122"/>
  </td>
</tr>
</table>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    	
    <tr>
    
       
       <td align="center" class="TableRow" nowrap><bean:message key="label.dbase"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.b0110"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.e0122"/></td>
       
    
  </tr>   
  <hrms:extenditerate id="element" name="browseRegisterForm" property="notAppListForm.list" indexes="indexes"  pagination="notAppListForm.pagination" pageCount="10" scope="session">
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
                  &nbsp; <bean:write  name="element" property="string(nbase)" filter="true"/>&nbsp;
            </td>  
            <td align="left" class="RecordRow" nowrap>    
                  <hrms:codetoname codeid="UN" name="element" codevalue="string(b0110)" codeitem="codeitem" scope="page"/> 
                  &nbsp; <bean:write name="codeitem" property="codename" />      
            </td>                    
            <td align="left" class="RecordRow" nowrap> 
                   <hrms:codetoname codeid="UM" name="element" codevalue="string(e0122)" codeitem="codeitem" scope="page"/>  	      
          	  &nbsp;  <bean:write name="codeitem" property="codename" />             
             </td>             
         </tr>
  </hrms:extenditerate>  
  </table>
  <table width="70%" align="center">
   
    <tr>
       <td valign="bottom" class="tdFontcolor">第
          <bean:write name="browseRegisterForm" property="notAppListForm.pagination.current" filter="true" />
          页
          共
          <bean:write name="browseRegisterForm" property="notAppListForm.pagination.count" filter="true" />
          条
          共
          <bean:write name="browseRegisterForm" property="notAppListForm.pagination.pages" filter="true" />
          页
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="browseRegisterForm" property="notAppListForm.pagination"
                   nameId="notAppListForm">
           </hrms:paginationlink>
       </td>
    </tr>
    <tr>
    <td align="center" colspan="2">      
      <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">						      
    </td>
  </tr>
 </table>
 
</html:form>