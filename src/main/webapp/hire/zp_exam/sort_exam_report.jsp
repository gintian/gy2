<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/hire/zp_exam/sort_exam_report">
<br>
  <table width="50%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    <tr>
      <td colspan="2" align="center" class="TableRow" nowrap><bean:message key="hire.sort.condition"/></td>
    </tr>
   <tr class="RecordRow" nowrap>
     <td align="left" nowrap valign="center" colspan="2"><bean:message key="label.zp_exam.sort_cond"/></td>
     <td align="left" nowrap valign="top">
     <logic:iterate  id="sortcondlist"   name="zpExamReportForm"  property="sortCondList"> 
         <tr class="RecordRow" nowrap>
           <td align="right" nowrap valign="center">
              <html:multibox name="zpExamReportForm" property="fieldsetvalue" value="${sortcondlist.columnname}"></html:multibox>
           </td>
           <td align="left" nowrap valign="center">
               <bean:write name="sortcondlist" property="columndesc" />&nbsp;
            </td>
         </tr>
         </logic:iterate>
   </td>
 </tr>
  <tr>
           <td align="center" class="RecordRow" nowrap  colspan="2">
               <hrms:submit styleClass="mybutton" property="b_save">
	 	        <bean:message key="label.zp_exam.sort"/>
	       </hrms:submit>
	        <html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset> 
	        <hrms:submit styleClass="mybutton" property="b_return"><bean:message key="button.return"/></hrms:submit>  
          </td>
  </tr>
</table>

</html:form>
