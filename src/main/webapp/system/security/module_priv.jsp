<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
	function test()
	{
		var ddd=document.getElementById("aaa");
		ddd.setAttribute("checked",true); 
		//ddd.checked=true;
	}
//-->
</script>

<html:form action="/system/security/module_priv">
<hrms:tabset name="pageset" width="85%" height="480" type="true"> 
<hrms:tab name="tab1" label="业务用户" visible="${aboutForm.p_module}" url="/system/security/operuser_module.do?b_query=link">
</hrms:tab>
<hrms:tab name="tab2" label="自助用户" visible="${aboutForm.e_module}" url="/system/security/employ_module.do?b_query=link">
</hrms:tab>
</hrms:tabset>
<table  width="50%" align="left">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 		</hrms:submit>
            </td>
          </tr>          
</table>
</html:form>
