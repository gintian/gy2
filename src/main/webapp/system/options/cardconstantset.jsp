<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/system/options/cardconstantset" style="margin-left:-2px;">
<hrms:tabset name="cardset" width="100%" height="100%" type="true"> 
    <%-- 
     <hrms:tab name="record_cardset" label="表格方式" visible="true" function_id="300152,070201" url="/ykcard/cardconstantset.do?b_cardset=set">
    </hrms:tab>		
    <hrms:tab name="record_cardset" label="列表方式" visible="true"  function_id="300154,070202" url="/ykcard/recordconstantset.do?b_search=set">
    </hrms:tab>
    --%>
    <hrms:tab name="record_cardset" label="其他设置" visible="true"  function_id="" url="/ykcard/otherconstantset.do?b_search=set">
    </hrms:tab>
</hrms:tabset>

</html:form>
