<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.frame.utility.AdminCode" %>
<script language="javascript">
 
	
</script>
<%
	int i=0;
	
	try
	{
%>
<hrms:themes />
<html:form action="/report/actuarial_report/validate_rule">
<!-- 
<table width='100%' border="0" cellpmoding="0" cellpadding="0"><tr><td width='100%' >
 -->
<hrms:tabset name="pageset" width="100%" height="99%" type="true"> <!-- 400 -->
	  <hrms:tab name="" label="上下限" visible="true" url="/report/actuarial_report/validate_rule.do?b_query=query" >
      </hrms:tab>	
      <hrms:tab name="" label="差异金额和差异率" visible="true" url="/report/actuarial_report/validate_rule.do?b_search=query" >
      </hrms:tab>
	 <hrms:tab name="" label="指标分类" visible="true" url="/report/actuarial_report/validate_rule/target_sort.do?b_query=query" >
      </hrms:tab>
      <hrms:tab name="" label="审核公式" visible="true" url="/gz/templateset/spformula/sp_formula.do?b_query=link&opt=0&returnType=0&gz_module=3" >
      </hrms:tab>
</hrms:tabset>
<!-- 
</td>
</tr>
</table>
 -->
</html:form> 
<%
	}
	catch(OutOfMemoryError error)
	{
	}
	catch(Exception ex)
	{
	}
%>
