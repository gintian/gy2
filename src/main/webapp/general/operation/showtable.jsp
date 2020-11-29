<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<html:form action="/general/operation/showtable">
	<hrms:tabset   height="100%" name="aaa" type="true">
	    <hrms:tab name="a" label="system.operation.template" visible="true"  url="/general/operation/table.do?b_query=link&usertype=0">     
		</hrms:tab>

	</hrms:tabset>
		<!--
	< name="b" label="固定表单" visible="true" url="/general/operation/table.do?b_query=link&usertype=1">     
		 -->	
</html:form>	




  	 


    