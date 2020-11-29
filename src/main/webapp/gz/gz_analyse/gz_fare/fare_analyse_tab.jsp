<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_analyse.GzAnalyseForm,java.util.ArrayList" %>
<%
	 GzAnalyseForm gzAnalyseForm = (GzAnalyseForm)session.getAttribute("gzAnalyseForm");  
	 String info=gzAnalyseForm.getInfo();
	 String chartkind1 = gzAnalyseForm.getChartkind();
 %>
 
 <% if(info!=null&&info.trim().length()>0){ %>
	<br><br><br><br><br><div align='center' ><%=info%></div>

 <% }else{ %>
  <script type="text/javascript">


	function test(name)
	{			
			var obj=$('fare_analyse');			
			obj.setSelectedTab("param"+name);
	}
	
	

</script>	
 <body  <%=(chartkind1!=null?"onload=\"test('"+chartkind1+"')\"":""  )%>   >	
<html:form action="/gz/gz_analyse/gz_fare/init_fare_analyse">
<hrms:tabset name="fare_analyse" width="100%" height="99%" type="true"> 
<hrms:priv func_id="32407301">
 <hrms:tab name="param3" label="总额使用情况表" visible="true" url="/gz/gz_analyse/gz_fare/init_fare_analyse.do?b_query=query&opt=second&chartkind=3&option=one">
      </hrms:tab>
      </hrms:priv>
      <hrms:priv func_id="32407302">
  <hrms:tab name="param4" label="月度总额使用情况图" visible="true" url="/gz/gz_analyse/gz_fare/init_fare_analyse.do?b_query=query&opt=second&chartkind=4&option=one">
      </hrms:tab>
      </hrms:priv>
      <hrms:priv func_id="32407303">
  <hrms:tab name="param1" label="年度总额使用情况图" visible="true" url="/gz/gz_analyse/gz_fare/init_fare_analyse.do?b_query=query&opt=second&chartkind=1&option=one">
      </hrms:tab>
      </hrms:priv>
      <hrms:priv func_id="32407304">
  <hrms:tab name="param2" label="计划总额,实发总额,剩余额对比分析图" visible="true" url="/gz/gz_analyse/gz_fare/init_fare_analyse.do?b_query=query&opt=second&chartkind=2&option=one">
      </hrms:tab>
 </hrms:priv>
</hrms:tabset>
</html:form>
</body>
<% } %>
