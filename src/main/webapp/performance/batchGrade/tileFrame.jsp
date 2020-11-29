<%@ page contentType="text/xml; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.actionform.performance.batchGrade.BatchGradeForm,
				 java.util.*,
				 com.hrms.struts.taglib.CommonData"%>
<script language='javascript'>

			

			function show(plan_id)
			{
				var obj=eval("a"+plan_id)
				
				if(obj.style.display=='none')
				{
					obj.innerHTML="<Iframe src=\"/selfservice/performance/batchGrade.do?b_query=link&plan_id="+plan_id+"\" width=\"800\" height=\"400\" scrolling=\"no\" 		    frameborder=\"0\"></iframe>";
					obj.style.display="block"
					flag=1;
				}
				else
				{
					obj.style.display="none"
					flag=0;
				}

			}



</script>




<html:form action="/selfservice/performance/batchGrade">

	<table  border='0'>
	
		   <%   
		   
		   	BatchGradeForm batchGradeForm=(BatchGradeForm)session.getAttribute("batchGradeForm");
		   	ArrayList dblist=(ArrayList)batchGradeForm.getDblist();
		   	for(int i=0;i<dblist.size();i++)
		   	{
		   			CommonData data=(CommonData)dblist.get(i);
		   %>
	
	
	
			<tr><td align='left' >
				<a href='javascript:show(<%=(data.getDataValue())%>)'><%=(data.getDataName())%></a>
			</td></tr>
			<tr><td id='a<%=(data.getDataValue())%>' style="display:none;" ></td></tr>
		    <%
		    }
		    %>		
			
			
	</table>








</html:form>