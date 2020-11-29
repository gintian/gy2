<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/hire/zp_options/baseoptions">
<br>
  <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    <tr>
      <td colspan="2" align="center" class="TableRow" nowrap><bean:message key="hire.appoint.subset"/></td>
    </tr>
    <tr class="RecordRow" nowrap>
      <td align="left" nowrap valign="center"><bean:message key="label.zp_options.baseoptions"/></td>
      <td align="left"  nowrap valign="center">
           <hrms:importgeneraldata showColumn="DBName" valueColumn="Pre" flag="false"  paraValue="1"
                 sql="select pre,dbname from dbname where 1=?" collection="list" scope="page"/> 
            	 <html:select name="baseOptionsForm" property="userBase" size="1"> 
            	    <html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            	</html:select>

     </td> 

   </tr>
   <tr class="RecordRow" nowrap>
     <td align="left" nowrap valign="center"><bean:message key="label.zp_options.subset"/></td>
     <td align="left" nowrap valign="center">
     <logic:iterate  id="subsetlist"   name="baseOptionsForm"  property="subSetList"> 
         <tr class="RecordRow" nowrap>
           <td align="right" nowrap valign="center">
              <html:multibox name="baseOptionsForm" property="fieldsetvalue" value="${subsetlist.fieldsetid}"></html:multibox>
           </td>
           <td align="left" nowrap valign="center">
               <a href="/hire/zp_options/search_field_item.do?b_query=link&a_id=<bean:write name="subsetlist" property="fieldsetid" filter="true"/>"><bean:write name="subsetlist" property="fieldsetdesc" />&nbsp;
            </td>
         </tr>
         </logic:iterate>
   </td>
 </tr>
  <tr>
           <td align="center" class="RecordRow" nowrap  colspan="2">
               <hrms:submit styleClass="mybutton" property="b_save">
	 	        <bean:message key="button.save"/>
	       </hrms:submit>
	        <html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>  
          </td>
  </tr>
</table>

</html:form>
