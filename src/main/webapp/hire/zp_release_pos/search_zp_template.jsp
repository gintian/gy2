<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
<html:form action="/hire/zp_release_pos/search_zp_template">
<table>
<tr>
<td>
      <hrms:ykcard name="cardTagParamForm" property="cardparam" nid="${cardTagParamForm.a0100}"  cardtype="ZP_POS_TEMPLATE" disting_pt="javascript:screen.width" userpriv="noinfo" havepriv="0"  istype="1" browser="<%=browser %>"/>
      
</td>
</tr>

</table>
<table>
<tr>
<td>
    <a href="/hire/zp_release_pos/apply_zp_pos.do?b_query=link&zp_pos_id_value=${cardTagParamForm.a0100}"> <bean:message key="hire.zp_persondb.applypos"/></a>
      
</td>
</tr>
<tr>
<td>
     <bean:message key="hire.recommend.friend"/>     
</td>
</tr>
</table>
</html:form>
