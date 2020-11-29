<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html:form action="/hire/zp_release_pos/search_pos_template">

<%
String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";
 %>
<tr>
<td>
      <hrms:ykcard name="zpreleasePosForm" property="cardparam" nid="00000013" cardtype="ZP_POS_TEMPLATE" disting_pt="javascript:screen.width"  browser="<%=browser %>"/>
</td>
</tr>
</html:form>
