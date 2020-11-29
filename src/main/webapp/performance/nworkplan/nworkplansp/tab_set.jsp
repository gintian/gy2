<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
  String state = request.getParameter("state");

%>

<hrms:tabset name="pageset" width="100%" height="100%" type="true">
<%if(state.equals("1")){ %>
	<hrms:tab name="person" label="个人周计划与总结" visible="true" function_id="0AB050101" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=1&belong_type=0">
	</hrms:tab>
	<hrms:tab name="dept" label="处室周计划与总结" visible="true" function_id="0AB050102" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=1&belong_type=1">
	</hrms:tab>
     <hrms:tab name="dept2" label="部门周计划与总结" visible="true" function_id="0AB050103" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=1&belong_type=2">
	</hrms:tab>
 <%}else if(state.equals("2")){ %>
    <hrms:tab name="person" label="个人月计划与总结" visible="true" function_id="0AB050201" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=2&belong_type=0">
	</hrms:tab>
	<hrms:tab name="dept" label="处室月计划与总结" visible="true" function_id="0AB050202" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=2&belong_type=1">
	</hrms:tab>
     <hrms:tab name="dept2" label="部门月计划与总结" visible="true" function_id="0AB050203" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=2&belong_type=2">
	</hrms:tab>
 <%}else if(state.equals("3")){ %>
    <hrms:tab name="person" label="个人季计划与总结" visible="true" function_id="0AB050301" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=3&belong_type=0">
	</hrms:tab>
	<hrms:tab name="dept" label="处室季计划与总结" visible="true" function_id="0AB050302" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=3&belong_type=1">
	</hrms:tab>
     <hrms:tab name="dept2" label="部门季计划与总结" visible="true" function_id="0AB050303"  url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=3&belong_type=2">
	</hrms:tab>
 <%}else if(state.equals("4")){ %>
    <hrms:tab name="person" label="个人年计划与总结" visible="true" function_id="0AB050401" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=4&belong_type=0">
	</hrms:tab>
	<hrms:tab name="dept" label="处室年计划与总结" visible="true" function_id="0AB050402" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=4&belong_type=1">
	</hrms:tab>
     <hrms:tab name="dept2" label="部门年计划与总结" visible="true" function_id="0AB050403" url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?b_search=link&state=4&belong_type=2">
	</hrms:tab>
 <%} %>
</hrms:tabset>
