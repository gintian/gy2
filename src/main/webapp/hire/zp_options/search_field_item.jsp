<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>

<html:form action="/hire/zp_options/search_field_item">
<br>
  <table width="65%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    <tr>
      <td colspan="1" align="center" class="TableRow" nowrap><bean:message key="lable.channel_detail.choose"/></td>
      <td colspan="1" align="center" class="TableRow" nowrap><bean:message key="kq.wizard.target"/></td>
    </tr>
     <logic:iterate  id="fielditemlist"   name="baseOptionsForm"  property="fieldItemList"> 
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
      <td align="center" class="RecordRow" nowrap> 
              <html:multibox name="baseOptionsForm" property="fielditemvalue" value="${fielditemlist.itemid}"></html:multibox>
           </td>
           <td align="left" class="RecordRow" nowrap> 
               <bean:write name="fielditemlist" property="itemdesc" />
            </td>
         </tr>
         </logic:iterate>
  <tr>
           <td align="center" class="RecordRow" nowrap  colspan="2">
               <hrms:submit styleClass="mybutton" property="b_save">
	 	        <bean:message key="button.save"/>
	       </hrms:submit>
	        <hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit> 
          </td>
  </tr>
</table>

</html:form>
