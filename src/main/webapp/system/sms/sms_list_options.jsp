<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/system/sms/sms_list_options" style="margin-left:-2px;">
<hrms:tabset name="sys_param" width="100%" height="100%" type="true"> 
      <hrms:tab name="param1" label="发件箱" visible="true" url="/system/sms/mail_list.do?b_query=link&state=-1" >
      </hrms:tab> 
      <hrms:tab name="param2" label="收件箱" visible="true" url="/system/sms/mail_list_comma.do?b_query=link">
      </hrms:tab>      
</hrms:tabset>
</html:form>