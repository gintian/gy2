<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.evaluation.EvaluationForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<html>
  <head>
   <%
		EvaluationForm evaluationForm=(EvaluationForm)session.getAttribute("evaluationForm");
		ArrayList yScoreNGradeList = (ArrayList)evaluationForm.getYScoreNGradeList();
		
		int n=0;
   %>		
  </head>
<style>

 #tbl-container 
 {			 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	overflow:auto;
	height:300px;
	width:100% 			
 }
 .TableRow_self {
	
	margin-left:auto;
	margin-right:auto;
	background-position : center;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:25px;
	font-weight: bold;	
	valign:middle;
}
</style>

<script language='javascript' >

</script>
 
<body>
	<html:form action="/performance/evaluation/performanceEvaluation">
	<html:hidden name="evaluationForm" property="object_type" />
		<br/>
		<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
			<tr> 
				<td>
					<bean:message key="jx.evaluation.performanceYScoreNgrade"/>
				</td>
			</tr>			
			<tr><td width='100%' style="border-top: 1px solid #8EC2E6;"  >	
			<div id="tbl-container">					
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
					<thead>
        				<tr style="position:relative;top:expression(this.offsetParent.scrollTop-1);">       	        	  
			
			 				<td align="center" style='color:black'    class="TableRow_self" nowrap><bean:message key="lable.performance.perObject"/></td>
			 				<td align="center" style='color:black'    class="TableRow_self" nowrap><bean:message key="label.kh.template.total"/></td>
			 				<td align="center" style='color:black'    class="TableRow_self" nowrap><bean:message key="jx.param.degreepro"/></td>
			
						</tr>
	 				</thead>
	 	 
	 
	 				<%
						 for(int i=0;i<yScoreNGradeList.size();i++)
						 {
							LazyDynaBean abean=(LazyDynaBean)yScoreNGradeList.get(i);
					 		String a0101=(String)abean.get("a0101");
					 		String score=(String)abean.get("score");
					 		String resultdesc=(String)abean.get("resultdesc");
					 		
							if(n%2==0)
						    {  
						    	out.println("<tr class='trShallow'>");   
						    }else{
						    	out.println("<tr class='trDeep'>");
							}		
								out.println("<td align='center' class='RecordRow' nowrap>&nbsp;");
						 		out.print(a0101);
						  		out.print("</td>");
						  		
						  		out.println("<td align='right' class='RecordRow' nowrap>&nbsp;");
						 		out.print(score);
						  		out.print("</td>");
						  		
						  		out.println("<td align='center' class='RecordRow' nowrap>&nbsp;");
						 		out.print(resultdesc);
						  		out.print("</td>");
							
							
					  		out.print("</tr>");
					 	}   			        	 		           
					%>	
	 				
	  			</table>
	  		</div>
			</td></tr>	
		</table>	

		<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">	
			<tr>
				<td align="center" style="height:35px">				
					<input type="button" name="ok" class="mybutton" value="<bean:message key="button.ok"/>" onclick="parent.window.close()"/>
					<input type="button" name="cancel" class="mybutton" value="<bean:message key="button.cancel"/>" onclick='parent.window.close()'/>
				</td>
			</tr>
		</table>
		
  	</html:form> 
  </body>
</html>
