<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/js/validate.js"></script>
<html:form action="/kq/register/browse_registerdata">
<logic:equal name="browseRegisterForm" property="error_flag" value="0">
 <SCRIPT LANGUAGE="JavaScript">
 function read_Pigeonflag(flag)
   {
      if(flag=="true")
      {
         alert("归档成功");          
      }else if(flag=="false")
      {
         alert("归档失败，请重试！");
      }
      browseRegisterForm.action="/kq/register/browse_registerdata.do?b_query=link&code=${browseRegisterForm.code}&kind=${browseRegisterForm.kind}";
      browseRegisterForm.submit();
   }
 read_Pigeonflag("${browseRegisterForm.pigeonhole_flag}");
</script>
</logic:equal>
 <logic:notEqual name="browseRegisterForm" property="error_flag" value="0">
<script language="javascript">
var error_str=kqErrorProcess('<bean:write name="browseRegisterForm"  property="error_flag"/>','<bean:write name="browseRegisterForm"  property="error_message"/>','<bean:write name="browseRegisterForm"  property="error_return"/>');
document.write(error_str);
</script>
</logic:notEqual>
</html:form>
