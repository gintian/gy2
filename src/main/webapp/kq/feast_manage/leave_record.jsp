<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<% 
	int i=0;
	int j = 1;
%>
<html:form action="/kq/feast_manage/leave_record">
<table align="center">
  <tr>
    <td>
        <bean:message key="kq.feast.emp.detail.title"/>
    </td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    	
    <tr>
        <td align="center" class="TableRow" nowrap><bean:message key="label.serialnumber"/></td>
		<td align="center" class="TableRow" nowrap><bean:message key="b0110.label"/></td>
		<td align="center" class="TableRow" nowrap><bean:message key="e0122.label"/></td>
		<td align="center" class="TableRow" nowrap><bean:message key="label.title.name"/></td>       
        <td align="center" class="TableRow" nowrap><bean:message key="kq.strut.start"/></td>
        <td align="center" class="TableRow" nowrap><bean:message key="kq.strut.end"/></td>
        <td align="center" class="TableRow" nowrap><bean:message key="kq.class.leavesake"/></td>
        <td align="center" class="TableRow" nowrap><bean:message key="kq.feast.qj"/>/<bean:message key="kq.feast.xj"/></td>   
   </tr>  
     <hrms:extenditerate id="element" name="feastForm" property="recordListForm.list" indexes="indexes"  pagination="recordListForm.pagination" pageCount="13" scope="session">
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
                   &nbsp;<%=j %>&nbsp;
            </td>
          	<td align="center" class="RecordRow" nowrap>
                <hrms:codetoname codeid="UN" name="element" codevalue="string(b0110)" codeitem="codeitem" scope="page"/>         
	           &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
	        </td>
	        <td align="center" class="RecordRow" nowrap>
                <hrms:codetoname codeid="UM" name="element" codevalue="string(e0122)" codeitem="codeitem" scope="page"/>         
	           &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
	        </td>
	        <td align="center" class="RecordRow" nowrap>
				&nbsp;<bean:write  name="element" property="string(a0101)" filter="true"/>&nbsp;  
	        </td>             
            <td align="left" class="RecordRow" nowrap>    
                 &nbsp;<bean:write  name="element" property="string(q15z1)" filter="true"/>&nbsp;    
            </td>  
            <td align="left" class="RecordRow" nowrap>    
                  &nbsp;<bean:write  name="element" property="string(q15z3)" filter="true"/>&nbsp;   
            </td>                    
            <td align="left" class="RecordRow" nowrap> 
                   &nbsp;<bean:write  name="element" property="string(q1507)" filter="true"/>&nbsp;          
            </td>
            <td align="center" class="RecordRow" nowrap> 
            	<logic:equal name="element" property="String(q1519)" value="1">
            		<bean:message key="kq.feast.qj"/>
            	</logic:equal>
                <logic:notEqual name="element" property="String(q1519)" value="1">
                	<bean:message key="kq.feast.xj"/>
                </logic:notEqual>         
            </td>                          
         </tr>
         <%j++; %>
   </hrms:extenditerate>  
    
  </table>
  <table width="100%" align="center" class="RecordRowTop0"> 
    <tr>
       <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
          <bean:write name="feastForm" property="recordListForm.pagination.current" filter="true" />
          <bean:message key="label.page.sum"/>
          <bean:write name="feastForm" property="recordListForm.pagination.count" filter="true" />
          <bean:message key="label.page.row"/>
          <bean:write name="feastForm" property="recordListForm.pagination.pages" filter="true" />
          <bean:message key="label.page.page"/>
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="feastForm" property="recordListForm.pagination"
                   nameId="recordListForm">
           </hrms:paginationlink>
       </td>
    </tr>  
 </table>
  <table width="90%" align="center">
    <tr>
       <td align="center"  nowrap>                	 
           
	    <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="window.close();" class="mybutton">	     	                 	   	      
       </td>
    </tr>
</table>
</html:form>