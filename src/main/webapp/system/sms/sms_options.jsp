<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/system/sms/sms_options">
<hrms:tabset name="sys_param" width="550" height="400" type="true"> 
      <hrms:tab name="param1" label="短信猫" visible="true" url="/system/sms/interface_param.do?b_query=link" >
      </hrms:tab> 
      <hrms:tab name="param2" label="短信网关" visible="true" url="/system/sms/interface_param_wg.do?b_query=link">
      </hrms:tab> 
      <hrms:tab name="param3" label="短信业务接口" visible="true" url="/system/sms/interface_param_yw.do?b_query=link&opt=select">
      </hrms:tab>      
</hrms:tabset> 
</html:form>
