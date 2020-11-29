<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/kq/kq.js"></script>
<script language="JavaScript">

   function change(){
      kqPlanInfoForm.action="/kq/kqself/plan/searchone_noput_data.do?b_search=link&select_flag=1";
      kqPlanInfoForm.submit();
   }
   function goback()
   {
       kqPlanInfoForm.action="/kq/kqself/plan/searchone.do?b_query=link&plan_id=${kqPlanInfoForm.plan_id}&dtable=q31";
       kqPlanInfoForm.target="il_body";
       kqPlanInfoForm.submit();
   }
   function viewAll()
   {
       kqPlanInfoForm.action="/kq/kqself/plan/searchone_noput_data.do?b_search=link&select_flag=0";
       kqPlanInfoForm.submit();
   }
 </script>   
<%
int i=0;
%>
<html:form action="/kq/kqself/plan/searchone_noput_data">
<table width="100%" border="0" cellspacing="0" cellpadding="0" >
<tr> 
  <td>
     <table width="30%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>
       <td>
         <html:select name="kqPlanInfoForm" property="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
            </html:select>
       </td>
       <td align= "left" nowrap>
           &nbsp;<bean:message key="label.title.name"/>&nbsp;
           <input type="text" name="select_name" value="${kqPlanInfoForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">
           
           <input type="button" name="btnreturn" value="<bean:message key="button.query"/>" onclick="change();" class="mybutton">			
           
       </td>
    </tr>
</table>
  </td>
</tr>
<tr>
<td>
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>
    <tr>       	    
               <td align="center" class="TableRow" nowrap>
                  <bean:message key="label.dbase"/>
               </td>
                 <td align="center" class="TableRow" nowrap>
                 <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="B0110" fielditem="fielditem"/>
	             <bean:write name="fielditem" property="dataValue" />&nbsp;
               </td>
                <td align="center" class="TableRow" nowrap>
                  <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
               </td>
                <td align="center" class="TableRow" nowrap>
                   <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
               </td> <td align="center" class="TableRow" nowrap>
                 <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;   
               </td>
               </tr>  
  </thead> 
<hrms:paginationdb id="element" name="kqPlanInfoForm" sql_str="kqPlanInfoForm.sql" table="" where_str="" columns="${kqPlanInfoForm.com}" order_by="order by i,b0110,e0122,a0000" page_id="pagination"  indexes="indexes">
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
                    <hrms:codetoname codeid="@@" name="element" codevalue="nbase" codeitem="codeitem" scope="page"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td> 
                 <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td> 
                 <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td> 
                 <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td>  
                 <td align="left" class="RecordRow" nowrap>                      
                     &nbsp;<bean:write name="element" property="a0101" filter="true"/>&nbsp;
            </td> 
         </tr>
    </hrms:paginationdb>
   </table>
   </td>
  </tr>
 <tr>
 <td> 
<table  width="100%" align="center" class="RecordRowP">
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
		          <p align="right"><hrms:paginationdblink name="kqPlanInfoForm" property="pagination" nameId="kqPlanInfoForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    
  </table>
 </td>
 </tr>
 <tr>
  <td>
  	<table>
  		<tr>
  			<td>
			    <input type="button" name="btnreturn" value="返回" onclick="goback();" class="mybutton">	
  			</td>
  		</tr>
  	</table>
  </td>
 </tr>
</table>


</html:form>
<script language="javascript">
hide_nbase_select('select_pre');
</script>		
				