<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
<!--
body {overflow-y:hidden;}
-->
</style>
<%
	String sub_page=request.getParameter("sub_page");
 %>
<body onload='setPage(<%=sub_page%>)' >
<html:form action="/train/request/trainsData">

<hrms:tabset name="cardset" width="100%" height="100%" type="true"> 
		  <hrms:tab name="menu1" label="train.job.courseArrange" function_id="323306" visible="true" url="/train/request/trainsData.do?b_course=link&r3127=${courseTrainForm.r3127}&r3101=${courseTrainForm.r3101}&flag=1">
	      </hrms:tab>
		 <hrms:tab name="menu2" label="conlumn.infopick.educate.edulesson.stulesson" function_id="323307" visible="true" url="/train/request/trainCourse.do?b_query=link&r3127=${courseTrainForm.r3127}&r3101=${courseTrainForm.r3101}&r3115=${param.r3115}&r3116=${param.r3116}&flag=2">
	      </hrms:tab>
	      <hrms:tab name="menu3" label="conlumn.infopick.educate.edulesson.student" function_id="323308" visible="true" url="/train/request/trainRes.do?b_query=link&r3127=${courseTrainForm.r3127}&r3101=${courseTrainForm.r3101}&flag=3">
	      </hrms:tab>
	      <hrms:tab name="menu4" label="train.b_plan.request.train.resources" function_id="323309" visible="true" url="/train/request/trainStu.do?b_query=link&r3127=${courseTrainForm.r3127}&r3101=${courseTrainForm.r3101}&flag=4">
	      </hrms:tab>
</hrms:tabset>
</html:form>
</body>
	<script langugage='javascript' >
	
	function setPage(page)
	{
		if(page==2)
		{
		var obj=$('cardset');
		obj.setSelectedTab("menu2");
		}
		if(page==3)
		{
		var obj=$('cardset');
		obj.setSelectedTab("menu3");
		}
		if(page==4)
		{
		var obj=$('cardset');
		obj.setSelectedTab("menu4");
		}
	}

	</script>