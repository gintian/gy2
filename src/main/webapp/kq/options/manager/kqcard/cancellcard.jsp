<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<style type="text/css">
.fixedDiv20
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-19); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
   /* BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; */
}
</style>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<script language="javascript">   
  function saveCard()
  {
       cancellationCardForm.action="/kq/options/manager/cancellcard.do?b_cancell=link";
    //   cancellationCardForm.target="rr";
       cancellationCardForm.submit();       
  }
</script>
<%
  int i=0; 
 %>
<html:form action="/kq/options/manager/cancellcard">
<div  class="fixedDiv2" style="height: 100%;border: none">
   	    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >   	    
           <tr class="fixedHeaderTr">      
               <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
               </td>  
               <td align="center" class="TableRow" nowrap="nowrap">
		<bean:message key="kq.card.card_no"/>&nbsp;
               </td>
  			</tr>
   	     <hrms:paginationdb id="element" name="cancellationCardForm" sql_str="cancellationCardForm.sql" table="" where_str="cancellationCardForm.where" columns="cancellationCardForm.column" order_by="cancellationCardForm.orderby" pagerows="10" page_id="pagination">
	     <%
               if(i%2==0){ 
             %>
             <tr class="trShallow">
             <%
               }else{
             %>
             <tr class="trDeep">
             <%}
             %>
              <td align="center" class="RecordRow" nowrap>               
               <hrms:checkmultibox name="cancellationCardForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
              </td>                 
              <td align="center" class="RecordRow" nowrap>               
               <bean:write name="element" property="card_no" filter="true"/>
              </td>               
             <%i++;%>  
	     </tr>	     
          </hrms:paginationdb>
                   
  <tr>
   <td colspan="2">
     <table width="100%" class="RecordRowP" align="center">
      <tr>
       <td valign="bottom" class="tdFontcolor">
          第<bean:write name="pagination" property="current" filter="true" />页
          共<bean:write name="pagination" property="count" filter="true" />条
          共<bean:write name="pagination" property="pages" filter="true" />页
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationdblink name="cancellationCardForm" property="pagination" nameId="cancellationCardForm" scope="page">
             </hrms:paginationdblink>
       </td>
      </tr>
    </table> 
     
   </td>
 </tr>
   	  <tr>
   	   <td align="center"  nowrap style="height:35px;border:none" colspan="2"> 
   	     <input type="button" name="btnreturn" value='<bean:message key="button.delete"/>' onclick="saveCard();" class="mybutton">						      
             <input type="button" name="b_next" value="<bean:message key="button.close"/>" onclick="window.close();" class="mybutton">	      	       
   	   </td>
   	  </tr>
</table>
</div>
</html:form>
