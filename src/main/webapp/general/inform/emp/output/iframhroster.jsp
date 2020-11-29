<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html:form action="/general/inform/emp/output/iframhroster">
  	<iframe id="iframe_user" name="iframe_user" width="100%" height="380" 
  	src="/general/inform/emp/output/printhroster.do?b_query=link&a_code=${outPrintForm.a_code}&dbname=${outPrintForm.dbname}&infor=${outPrintForm.inforkind}&flag=${outPrintForm.result}"></iframe>
</html:form>
