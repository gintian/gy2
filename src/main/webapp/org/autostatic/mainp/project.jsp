<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	String flag = request.getParameter("flag"); 
	String param=request.getParameter("param");
	param=param==null?"":param;
%>
<html:form action="/org/autostatic/mainp/project">
<div class="fixedDiv3">
<hrms:tabset name="sys_param" width="100%" height="485" type="true"> 
	<%if(param.equals("orgpre")){ %>
      <hrms:tab name="param1" label="org.maip.statistics" visible="true" url="/org/autostatic/mainp/statistics.do?b_query=link&param=orgpre">
      </hrms:tab>	
      <hrms:tab name="param2" label="org.maip.comp.project" visible="true" url="/org/autostatic/mainp/calculation.do?b_query=link&param=orgpre">
      </hrms:tab>
      <hrms:tab name="param3" label="org.maip.project.summary" visible="true" url="/org/autostatic/mainp/summary.do?b_query=link&param=orgpre">
      </hrms:tab>    
    <%}else{ %>
    	<hrms:tab name="param1" label="org.maip.statistics" visible="true" url="/org/autostatic/mainp/statistics.do?b_query=link&param=">
      </hrms:tab>	
      <hrms:tab name="param2" label="org.maip.comp.project" visible="true" url="/org/autostatic/mainp/calculation.do?b_query=link&param=">
      </hrms:tab>
      <hrms:tab name="param3" label="org.maip.project.summary" visible="true" url="/org/autostatic/mainp/summary.do?b_query=link&param=">
      </hrms:tab>  
    <%} %>              
</hrms:tabset> <!-- 【7759】组织机构/编制管理，设置项目界面，出现了双滚动条了，不对。  jingq upd 2015.03.02 -->
<table align="center" cellpadding="0" cellspacing="0" border="0"><tr><td height="35px;">

<%if(flag.equals("0")){%>
<center>
<button name="return" class="mybutton" onclick="backSet();" style="width:45" style="font-size:10pt">
<bean:message key='reportcheck.return'/></button></center> 
<script language="javascript">
function backSet(){
	document.location.href="/org/autostatic/confset/subsetconfset.do?br_query=link";
}	
</script>
<%}else{%>
<center><button name="return" class="mybutton" onclick="window.close();" style="width:45" style="font-size:10pt">
<bean:message key='lable.welcomeboard.close'/></button></center> 
<%}%>

</td></tr></table>
</div>
</html:form> 
