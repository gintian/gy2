<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript">
   function change_print()
   {
       feastForm.action="/general/muster/hmuster/select_muster_name.do?b_query=link&nFlag=81&relatTableid=${feastForm.relatTableid}";
       feastForm.submit();       
   }  
</SCRIPT>
<html:form action="/kq/feast_manage/managerdata"> 
                <html:hidden name="feastForm" property="returnURL" styleClass="text"/>
                <html:hidden name="feastForm" property="condition" styleClass="text"/>         
</html:form>
<script language="javascript">
  change_print();  
</script>