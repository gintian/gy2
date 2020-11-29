<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	String e01a1=request.getParameter("e01a1");
	String z0301=request.getParameter("z0301");
	
	String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";
%>
<html:form action="/hire/zp_person/search_pos_template">
<table>
<tr>
<td>
      <hrms:ykcard name="cardTagParamForm" property="cardparam" nid="<%=e01a1%>"  cardtype="ZP_POS_TEMPLATE2" disting_pt="javascript:screen.width" userpriv="noinfo" havepriv="0"  istype="1"    browser="<%=browser %>"/>
</td>
</tr>

</table>
<table>
  <tr>
   <td>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="/hire/zp_person/apply_zp_position.do?b_query=link&zp_pos_id_value=<%=z0301%>&edition=2"><bean:message key="hire.zp_persondb.applypos"/></a>
    </td>
 </tr>
 <tr>
  <td>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="/hire/zp_persondb/applyaccount.do" target="i_body"><bean:message key="hire.zp_persondb.register"/></a>    
  </td>
  </tr>
 </table>
</html:form>
