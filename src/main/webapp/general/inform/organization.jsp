<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/general/inform/organization">

<hrms:codetree codesetid="@K" setname="organization" pagerows="1000" url="/general/inform/inform_interface.do?b_query=link" target="mil_body">

</hrms:codetree> 

<input type="button" name="测试" value="测试" onclick="test();"> 
</html:form>
<script language="javascript">

function test()
{
   var node=treeorganization.getCurrentNode();
	alert(node.getLabel());
   var rec=node.getRecord();
	alert(rec.getValue("codeitemid"));
}

</script>


