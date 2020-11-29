<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/daily_registerdata">
<script language="javascript">
  
   function if_change(status)
   {
      if(status!="0")
      {
        window.close();
      }
   }
   if_change('<bean:write name="dailyRegisterForm"  property="changestatus"/>');
</script>
<%
int i=0;
%>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
  <td align="center">
       <logic:equal name="dailyRegisterForm" property="changestatus" value="1">	 
             <bean:message key="kq.emp.change.add.message"/>
       </logic:equal>
       <logic:equal name="dailyRegisterForm" property="changestatus" value="0">	 
            <bean:message key="kq.emp.change.leave.message"/>
       </logic:equal>
  </td>
</tr>
</table>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    	
    <tr>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.search_feast.select"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.emp.name"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.b0110"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.emp.change.e0122"/></td>
      <td align="center" class="TableRow" nowrap>
         <logic:equal name="dailyRegisterForm" property="changestatus" value="0">	 
             <bean:message key="kq.emp.change.add.date"/>
         </logic:equal>
         <logic:equal name="dailyRegisterForm" property="changestatus" value="1">	 
            <bean:message key="kq.emp.change.leave.date"/>
         </logic:equal>
         <html:hidden name="dailyRegisterForm" property="changestatus" styleClass="text"/>
      </td> 
  </tr>   
  <hrms:extenditerate id="element" name="dailyRegisterForm" property="recordListForm.list" indexes="indexes"  pagination="recordListForm.pagination" pageCount="20" scope="session">
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
               <hrms:checkmultibox name="dailyRegisterForm" property="recordListForm.select" value="true" indexes="indexes"/>&nbsp;
            </td>  
            <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(a0101)" filter="true"/>&nbsp;
            </td>  
            <td align="left" class="RecordRow" nowrap>    
                  <hrms:codetoname codeid="UN" name="element" codevalue="string(b0110)" codeitem="codeitem" scope="page"/> 
                   <bean:write name="codeitem" property="codename" />      
            </td>                    
            <td align="left" class="RecordRow" nowrap> 
                   <hrms:codetoname codeid="UM" name="element" codevalue="string(e0122)" codeitem="codeitem" scope="page"/>  	      
          	    <bean:write name="codeitem" property="codename" />             
             </td>   
             <td align="left" class="RecordRow" nowrap>              
                   <bean:write  name="element" property="string(change_date)" filter="true"/>&nbsp;
             </td>
         </tr>
  </hrms:extenditerate>  
  </table>
  <table width="70%" align="center">
    <tr>
       <td valign="bottom" class="tdFontcolor">第
          <bean:write name="dailyRegisterForm" property="recordListForm.pagination.current" filter="true" />
          页
          共
          <bean:write name="dailyRegisterForm" property="recordListForm.pagination.count" filter="true" />
          条
          共
          <bean:write name="dailyRegisterForm" property="recordListForm.pagination.pages" filter="true" />
          页
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="dailyRegisterForm" property="recordListForm.pagination"
                   nameId="recordListForm">
           </hrms:paginationlink>
       </td>
    </tr>
 </table>
 <table width="50%" align="center">
    <tr>
       <td width="60%" align="center"  nowrap>
	   <hrms:submit styleClass="mybutton" property="b_leavechange">
               <bean:message key="kq.emp.change.emp.leave"/>
	   </hrms:submit>
	   <input type="button" name="btnreturn" value='<bean:message key="kq.emp.change.jump"/>' onclick="window.close();" class="mybutton">   			      
        </td>
    </tr>
</table>
</html:form>