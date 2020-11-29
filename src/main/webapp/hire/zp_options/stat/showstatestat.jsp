<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hjsj.hrms.actionform.hire.zp_options.ShowstatestatForm"%>
<%
 ShowstatestatForm showstatestatForm=(ShowstatestatForm)session.getAttribute("showstatestatForm");
 String schoolPosition=showstatestatForm.getSchoolPosition();
 String name="各岗位人数统计";
 if(schoolPosition!=null&&schoolPosition.trim().length()>0)
    name="各岗位|专业人数统计";
 %>
<script language="javascript"> 
function showpos(height){
	showstatestatForm.height.value=height;
	showstatestatForm.action="/hire/zp_options/stat/showstatestat.do?b_query=link";
	showstatestatForm.submit();
}
</script>
<html:form action="/hire/zp_options/stat/showstatestat">
<hrms:tabset name="sys_param" width="99.5%" height="100%" type="true"> 
      <hrms:tab name="param1" label="hire.zp_options.day.statistics" visible="true" url="/hire/zp_options/stat/itemstat/showjobdaily.do?b_query=link&returnflag=${showstatestatForm.returnflag}&init=0">
      </hrms:tab>	
      <hrms:tab name="param2" label="hire.resume.total" visible="true" url="/hire/zp_options/stat/totalstat/showtotalstatresult.do?b_query=link&returnflag=${showstatestatForm.returnflag}&init=0">
      </hrms:tab>  
      <hrms:tab name="param3" label="<%=name%>" visible="true" url="/hire/zp_options/stat/positionstat/positionstat.do?b_query=link&returnflag=${showstatestatForm.returnflag}&init=0">
      </hrms:tab>                    
</hrms:tabset> 
</html:form>
