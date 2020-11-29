<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript">
  function goback()
   {
       annualPlanForm.action="/kq/kqself/apply/my_annual_apply.do?b_query=link";
        annualPlanForm.submit();
   }
 </script> 
<%
int i=0;
%>
<html:form action="/kq/kqself/apply/view_my_one_annual_apply">
<br><br>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>
    <tr>     
      <logic:iterate id="element" name="annualPlanForm"  property="tlist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center" class="TableRow" nowrap>
                <bean:write name="element" property="itemdesc" />&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>      
    </tr>  
  </thead> 
<hrms:paginationdb id="element" name="annualPlanForm" sql_str="annualPlanForm.sql" table="" where_str="annualPlanForm.where" columns="${annualPlanForm.com}" order_by=""  page_id="pagination"  indexes="indexes">
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
           
          <logic:iterate id="tlist" name="annualPlanForm"  property="tlist" indexId="index">
             <logic:equal name="tlist" property="visible" value="true">

                     <td align="left" class="RecordRow" nowrap>
                        <logic:notEqual name="tlist" property="codesetid" value="0">                        
                           <hrms:codetoname codeid="${tlist.codesetid}" name="element" codevalue="${tlist.itemid}" codeitem="codeitem" scope="page"/>  	      
                           <bean:write name="codeitem" property="codename" />&nbsp;                            
                        </logic:notEqual>
                        <logic:equal name="tlist" property="codesetid" value="0">
                            <bean:write name="element" property="${tlist.itemid}" filter="false"/>&nbsp;                 
                        </logic:equal>                   
                     </td>
                        
            </logic:equal>    
          </logic:iterate>
         </tr>
    </hrms:paginationdb>
</table>    

<table  width="80%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	      <td  align="right" nowrap class="tdFontcolor">
		      <p align="right"><hrms:paginationdblink name="annualPlanForm" property="pagination" nameId="annualPlanForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
  </table>
<table  width="80%" align="center">
	<tr>
		<td>
		  <input type="button" name="br_approve" value='<bean:message key="button.return"/>' class="mybutton" onclick="goback();"> 
		</td>
	</tr>
</table>
</html:form>
