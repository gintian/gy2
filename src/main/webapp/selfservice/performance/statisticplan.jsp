<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<jsp:useBean id="statisticForm" class="com.hjsj.hrms.actionform.performance.StatisticForm" scope="session"/>
<HTML>
<%
 
 int i=0;
 
 
 
%>
<script>
	function doSubmit(value)
	{
		if(value=="#")
		{
			alert('请选择活动计划');
			return false;
		}
		else
		{
		location.href="/selfservice/performance/statistic.do?b_query=link&planNum="+value+"&planFlag=2";
		}
	}
		
</script>
<br>
<br>
<br>
<center>
<body> 
<html:form action="/selfservice/performance/statistic" method="get">
      <table border="0" cellpadding="0" cellspacing="0" align="center">
      <tr>
            <td colspan="4" class="framestyle">
     <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
  		<tr class="list3" >
   			<td align="center" nowrap width="100" valign="top">查询的活动计划</td>
    			<td align="left" nowrap valign="top">
        		
          		 	<hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="true"  paraValue="7"
                  		sql="select plan_id,name from per_plan where status=? and plan_id in  select plan_id from per_mainbody where object_id=mainbody_id and mainbody_id=${userView.userId()}" collection="list" scope="page"/> 
                  		
                  		<html:select name="statisticForm" property="planNum" size="1" onchange="doSubmit(this.value);"> 
            			<html:option value="#">请选择</html:option>
            			<html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            			</html:select>
            			            			
      			</td>
  		</tr>
  		
    </table>
    </td>
    </tr>
    </table>
       <%
       	    statisticForm.clearDrawingFlag();
       %> 	  
   
</html:form>

  </center>
  
  </body>
  </html>