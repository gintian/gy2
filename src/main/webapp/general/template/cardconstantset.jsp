<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/general/template/cardconstantset">
<hrms:tabset name="cardset" width="80%" height="400" type="true"> 
	<hrms:tab name="record_cardset" label="表格方式" function_id="070201,30012" visible="true" url="/ykcard/cardconstantset.do?b_cardset=set">
    </hrms:tab>	
	<hrms:tab name="table_cardset" label="记录方式"  function_id="30014,070202" visible="true" url="/ykcard/cardconstantset.do?b_cardset1=set">
    </hrms:tab>	
</hrms:tabset>

</html:form>
