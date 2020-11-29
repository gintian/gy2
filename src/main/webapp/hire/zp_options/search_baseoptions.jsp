<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
   int i = 0;
%>

<html:form action="/hire/zp_options/search_baseoptions">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="hire.store.subset"/></td>
		 </tr> 
                  <tr class="trDeep1">
                     <td align="left" nowrap valign="center"><bean:message key="label.zp_options.baseoptions"/></td>
                     <td align="left"  nowrap valign="center">
                          <bean:write  name="baseOptionsForm" property="dbpre" filter="true"/>
                      </td>      
                   </tr>
                   <tr class="trShallow">
                     <td align="left" nowrap valign="center"><bean:message key="label.zp_options.subset"/></td>
                     <td align="left"  nowrap valign="center" width="500" style="word-break:break-all">
                          <bean:write  name="baseOptionsForm" property="strsql" filter="true"/>
                      </td>   
                   </tr>  
                 
           <tr>
              <td align="center"  nowrap colspan="4">
	 	      <hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr>                                                            
 </table>
</html:form>
