<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript">
 function change()
   {
      annualPlanForm.action="/kq/kqself/apply/my_annual_apply.do?b_query=link";
      annualPlanForm.submit();
   }
  </script>  
<%
int i=0;
%>
<html:form action="/kq/kqself/apply/my_annual_apply">
<table  width="100%" align="center">
		 <tr >
          <td align="left" style="height: 25px;" nowrap valign="center">        
           <bean:message key="kq.deration_details.kqnd"/>        
           <hrms:optioncollection name="annualPlanForm" property="slist" collection="list" />
	          <html:select name="annualPlanForm" property="year" size="1" onchange="change();">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
             </html:select> 
           </td>
         </tr>
  </table>
  <div class="fixedDiv2">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>
    <tr>     
      <logic:iterate id="element" name="annualPlanForm"  property="tlist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center" class="TableRow" style="border-top:none;border-left: none;" nowrap>
                <bean:write name="element" property="itemdesc" />&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
        <bean:message key="kq.self.apply"/>            	
      </td>    
      <td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap>
        查看       	
      </td>   
    </tr>  
  </thead> 
<hrms:paginationdb id="element" name="annualPlanForm" sql_str="annualPlanForm.sql" table="" where_str="annualPlanForm.where" columns="${annualPlanForm.com}" order_by="annualPlanForm.order"  page_id="pagination"  indexes="indexes">
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

                     <td align="left" class="RecordRow" style="border-top:none;border-left: none;" nowrap>
                        <logic:notEqual name="tlist" property="codesetid" value="0">                        
                           <hrms:codetoname codeid="${tlist.codesetid}" name="element" codevalue="${tlist.itemid}" codeitem="codeitem" scope="page"/>  	      
                           &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                        </logic:notEqual>
                        <logic:equal name="tlist" property="codesetid" value="0">
                            &nbsp;<bean:write name="element" property="${tlist.itemid}" filter="false"/>&nbsp;                 
                        </logic:equal>                   
                     </td>
                        
            </logic:equal>    
          </logic:iterate>
          <bean:define id="q29011" name='element' property="q2901"/>
          <%
          	//参数加密
          	String str = "plan_id="+q29011+"&table=q31";
          %>
             <td align="center" class="RecordRow" style="border-top:none;" nowrap>
            	<a href="/kq/kqself/apply/my_one_annual_apply.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str)%>"><img src="/images/edit.gif" border=0></a>
	     </td>	
	     <td align="center" class="RecordRow" style="border-top:none;border-right: none;" nowrap>
            	<a href="/kq/kqself/apply/view_my_one_annual_apply.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str)%>"><img src="/images/view.gif" border=0></a>
	     </td>     
         </tr>
    </hrms:paginationdb>
</table>    
</div>
<div style="*width:expression(document.body.clientWidth-10);">
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
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
                <tr>
	            
	       </tr>
  </table>
</div>
</html:form>
