<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import='com.hrms.struts.utility.JSMessage' %>
<%
	String errorMsg = (String)request.getAttribute("errorMsg");
%> 

<hrms:themes></hrms:themes>

  <table width="310" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable"  style="margin-top:10px;">
          <tr>
       		<td align="left" class="TableRow">&nbsp;<bean:message key="label.information"/>&nbsp;</td>
          </tr> 
                    <tr >
              	      <td align="left" valign="middle" nowrap style="height:120"><%=errorMsg%></td>
                    </tr> 
  </table> 

