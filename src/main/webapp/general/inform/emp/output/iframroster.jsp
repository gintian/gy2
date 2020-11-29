<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html:form action="/general/inform/emp/output/iframroster">
	<logic:equal name="musterForm" property="checktype" value="open">
  	<iframe id="iframe_user" name="iframe_user" width="100%" height="580" src="/general/muster/muster_list.do?b_output=query&returncheck=1"></iframe>
	</logic:equal>
	<logic:equal name="musterForm" property="checktype" value="set">
  	<iframe id="iframe_user" name="iframe_user" width="100%" height="580" src="/general/muster/select_muster_fields.do?b_query=link&a_inforkind=1&returncheck=1"></iframe>
	</logic:equal>
	<logic:equal name="musterForm" property="checktype" value="reset">
  	<iframe id="iframe_user" name="iframe_user" width="100%" height="580" src="/general/muster/muster_list.do?b_fillout=query"></iframe>
	</logic:equal>
</html:form>
