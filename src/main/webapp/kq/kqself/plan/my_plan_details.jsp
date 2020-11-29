<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->

<%
int i=0;
%>
<html:form action="/kq/kqself/plan/my_plan_details">
<br><br>
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
  <thead>
    <tr>
      <logic:iterate id="element" name="kqPlanInfoForm"  property="tlist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center" class="TableRow" nowrap>
                <bean:write name="element" property="itemdesc" />&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>
    </tr>  
  </thead>
  
<hrms:paginationdb id="element" name="kqPlanInfoForm" sql_str="kqPlanInfoForm.sql" table="" where_str="kqPlanInfoForm.where" columns="${kqPlanInfoForm.com}" page_id="pagination"  indexes="indexes">
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
            <logic:iterate id="tlist" name="kqPlanInfoForm"  property="tlist" indexId="index">
             <logic:equal name="tlist" property="visible" value="true">
                  <logic:notEqual name="tlist" property="itemtype" value="D">
                     <td align="left" class="RecordRow" nowrap>
                        <logic:notEqual name="tlist" property="codesetid" value="0">
                           <hrms:codetoname codeid="${tlist.codesetid}" name="element" codevalue="${tlist.itemid}" codeitem="codeitem" scope="page"/>  	      
                           <bean:write name="codeitem" property="codename" />&nbsp;                    
                        </logic:notEqual>
                        <logic:equal name="tlist" property="codesetid" value="0">
                            <bean:write name="element" property="${tlist.itemid}" filter="false"/>&nbsp;                 
                        </logic:equal>                   
                     </td>
                    </logic:notEqual>
                    <logic:equal name="tlist" property="itemtype" value="D">
                       <td align="center" class="RecordRow" nowrap>
                           <bean:write name="element" property="${tlist.itemid}" filter="false"/>&nbsp;   
                       </td>
                    </logic:equal>    
            </logic:equal>    
          </logic:iterate>
         </tr>
    </hrms:paginationdb>
</table>    

<table  width="80%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="kqPlanInfoForm" property="pagination" nameId="kqPlanInfoForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    
  </table>
<table  width="50%" align="center">
          <tr>  
           <td align="center">
            <hrms:submit styleClass="mybutton" property="b_back">	<bean:message key="button.return"/> </hrms:submit>
           </td>
          </tr>          
</table>
</html:form>
