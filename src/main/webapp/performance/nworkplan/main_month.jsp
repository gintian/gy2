<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<hrms:tabset name="pageset" width="100%" height="100%" type="true">
   <%
	   String opt = request.getParameter("opt");
	   String belong_type = request.getParameter("belong_type");
	   //System.out.println(opt);
	   //System.out.println(belong_type);
   %>
   <% if(opt==null || opt.equals("")){%>
	<hrms:tab name="person" label="本人月计划与总结" visible="true" function_id="0AB0201"
		url="/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=init&personPage=0">
	</hrms:tab>
	<hrms:tab name="dept" label="部门月计划与总结" visible="true" function_id="0AB0202"
		url="/performance/nworkplan/searchMonthWorkplan.do?b_query=link&init=init&personPage=1">
	</hrms:tab>
   <% }else if(opt.equals("2")&&belong_type.equals("1")){%>
     <hrms:tab name="person" label="处室月计划与总结" visible="true" function_id=""
		url="/performance/nworkplan/searchMonthWorkplan.do?b_query=link13&personPage=0&opt=${param.opt}&isRead=${param.isRead}&p0100=${param.p0100}&returnurl=${param.returnurl}&date=${param.date}">
	</hrms:tab>
     
   <%}else if(opt.equals("2")&&belong_type.equals("2")){%> 
	<hrms:tab name="dept" label="部门月计划与总结" visible="true" function_id=""
		url="/performance/nworkplan/searchMonthWorkplan.do?b_query=link&personPage=1&opt=${param.opt}&isRead=${param.isRead}&p0100=${param.p0100}&returnurl=${param.returnurl}&date=${param.date}">
	</hrms:tab>  
   <%} %>
</hrms:tabset>
