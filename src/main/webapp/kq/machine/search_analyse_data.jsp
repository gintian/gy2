<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/validate.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript">
   function cancelQ()
   {
      dataAnalyseForm.action="/kq/machine/search_card.do?b_query=link&action=search_card_data.do&target=mil_body&a_code=${dataAnalyseForm.a_code}&nbase=${dataAnalyseForm.nbase}";
      dataAnalyseForm.target="il_body";
      dataAnalyseForm.submit();
   }   
   function submitQ()
   {
     if(confirm("是要将数据分析的结果导入到考勤日明细中?"))
     {
        dataAnalyseForm.action="/kq/machine/analyse_card_data.do?b_submit=link";
        dataAnalyseForm.target="il_body";
        dataAnalyseForm.submit();
     }
   }
   function deleteQ()
   {
      dataAnalyseForm.action="/kq/machine/analyse_card_data.do?b_delete=link";
      dataAnalyseForm.submit();
   } 
</script>
 <% int i=0;%>
<html:form action="/kq/machine/analyse_card_data">
<table width="90">
 <tr>
  <td> 
     <table width="70%" border="0" cellspacing="1"  align="left" cellpadding="1">
       <tr>
         <td>
         原始数据分析报告
         <html:hidden name="dataAnalyseForm" property="temp_Table" styleClass="text"/>
         <html:hidden name="dataAnalyseForm" property="kq_cardno" styleClass="text"/>
         <html:hidden name="dataAnalyseForm" property="kq_type" styleClass="text"/>
          </td>         
        </tr>
       </table> 
  </td>
 </tr>
 <tr>
  <td width="100%">
    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" >
      <thead>
         <tr>
            <td align="center" class="TableRow" nowrap>
		&nbsp;<bean:message key="column.select"/>&nbsp;
            </td>  
            <logic:iterate id="element"    name="dataAnalyseForm"  property="fieldList" indexId="index"> 
                <logic:equal name="element" property="visible" value="true">
                 <td align="center" class="TableRow" nowrap>
                  &nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp; 
                 </td>
              </logic:equal>
           </logic:iterate>                    	        
         </tr>         
      </thead> 
      <hrms:paginationdb id="element" name="dataAnalyseForm" sql_str="dataAnalyseForm.strSql" table="" where_str="dataAnalyseForm.whereStr" columns="dataAnalyseForm.column" order_by="dataAnalyseForm.order" pagerows="18" page_id="pagination">
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}i++;          
          %>  
          <td align="center" class="RecordRow" nowrap>   
                <hrms:checkmultibox name="dataAnalyseForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
           </td>
           <logic:iterate id="info" name="dataAnalyseForm"  property="fieldList">  
             <logic:equal name="info" property="visible" value="true">
                 <logic:equal name="info" property="itemtype" value="A">
                     <logic:notEqual name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap>
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                          </td>  
                      </logic:notEqual>
                      <logic:equal name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap>
                               &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                           </td> 
                      </logic:equal>
                 </logic:equal>
                 <logic:equal name="info" property="itemtype" value="N">
                   <td align="center" class="RecordRow" nowrap> 
                      <bean:write name="element" property="${info.itemid}"/>
                    </td> 
                 </logic:equal>
             </logic:equal>
           </logic:iterate>
          </tr>
        </hrms:paginationdb>
    </table>
  </td>
 </tr> 
  <tr>
   <td>
     <table  width="70%" align="center">
       <tr>
          <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
             第<bean:write name="pagination" property="current" filter="true" />页
             共<bean:write name="pagination" property="count" filter="true" />条
             共<bean:write name="pagination" property="pages" filter="true" />页
	  </td>
	  <td  width="80%" align="left" nowrap class="tdFontcolor">
	     <p align="left"><hrms:paginationdblink name="dataAnalyseForm" property="pagination" nameId="dataAnalyseForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	</tr>	
     </table>
   </td>
 </tr>
 <tr>
    <td>
        <table  width="70%" align="center">
          <tr>
           <td>
              <input type="button" name="Submit46" value="<bean:message key="kq.register.kqduration.ok"/>" class="mybutton" onclick="submitQ();" >
            &nbsp;&nbsp; 
             <input type="button" name="Submit4764" value="<bean:message key="kq.register.kqduration.cancel"/>" class="mybutton" onclick="cancelQ();" >
             &nbsp;&nbsp; 
             <input type="button" name="Submit4764" value="<bean:message key="kq.emp.change.emp.leave"/>" class="mybutton" onclick="deleteQ();" >
           </td>
          </tr>
        </table>
    </td>
 </tr>
</table>  
</html:form>