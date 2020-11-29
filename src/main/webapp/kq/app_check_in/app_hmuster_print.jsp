<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="javascript">
   function change_print()
   {
       appForm.action="/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=81&relatTableid=${appForm.relatTableid}";
       appForm.submit();      
   }  
</SCRIPT>
<html:form action="/kq/app_check_in/all_app_data"> 
                <html:hidden name="appForm" property="returnURL" styleClass="text"/>
                <html:hidden name="appForm" property="condition" styleClass="text"/>         
</html:form>
<script language="javascript">
  change_print();  
</script>