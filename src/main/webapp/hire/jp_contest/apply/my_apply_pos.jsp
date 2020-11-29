<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*" %>
<%@ page import="com.hjsj.hrms.actionform.hire.jp_contest.JingPinForm" %>	
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>	

<script type="text/javascript">
function to_return()
{	
	jingpinForm.action="/hire/jp_contest/apply/apply_jp_pos.do?b_search=link";
	jingpinForm.submit(); 
}
</script>
<html:form action="/hire/jp_contest/apply/my_apply_pos"> 
<hrms:tabset width="70%" height="400" name="apply_jp" type="true">
	<%
       	JingPinForm jpForm =(JingPinForm)session.getAttribute("jingpinForm");
       	ArrayList applylist = jpForm.getAllApplyPos();
       	for(int i=0;i<applylist.size();i++){
       		LazyDynaBean abean = (LazyDynaBean)applylist.get(i);
       		String z0700 = (String)abean.get("z0700");
       		String postion = (String)abean.get("postion");
       		String url = "/hire/jp_contest/apply/apply_jp_pos.do?b_query=link&z0700="+z0700;
	%>	
		 <hrms:tab name="param1" label="<%=postion%>" visible="true" url="<%=url%>">
      	 </hrms:tab>	
	<%}%>	
</hrms:tabset>
<br>
<center><button name="return" class="mybutton" onclick="to_return();" style="width:45" style="font-size:10pt"><bean:message key="button.return"/></button></center> 
</html:form> 
