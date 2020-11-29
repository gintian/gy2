<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
  String flag = request.getParameter("flag");
%>
<html:form action="/system/logonuser/su_info">
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable" style="margin-top:10px;">
          <tr height="20">
       		<td  align="left" class="TableRow">&nbsp;<bean:message key="label.information"/>&nbsp;</td>
         	      
          </tr> 
         
          <tr>
        	
         <%  
         //bug 16391 根据不同权限提示不同的信息  update by hej  2016/2/4
         if("nogroup".equals(flag)) {
         %>
              	     <td align="center" nowrap style="height:60px"><bean:message key="infor.sys.privgroup"/></td>
         <% } else if("noself".equals(flag)){%>
         			<td align="center" nowrap style="height:60px"><bean:message key="infor.sys.privself"/></td>
         <%} else{%>
         <td align="center" nowrap style="height:60px"><bean:message key="infor.sys.priv"/></td>
         <%} %>
          </tr> 
  </table>
 
</html:form>
