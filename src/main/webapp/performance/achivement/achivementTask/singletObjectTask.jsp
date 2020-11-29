<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.achivement.AchievementTaskForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.constant.WebConstant" %>
 
<html>
  <head>
   <%
	AchievementTaskForm achievementTaskForm=(AchievementTaskForm)session.getAttribute("achievementTaskForm");
	ArrayList objectCycleList=(ArrayList)achievementTaskForm.getObjectCycleList();	
	ArrayList objectPointList=(ArrayList)achievementTaskForm.getObjectPointList();		
 %>		
  </head>
<style>
	#tbl-container {
			 
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
		margin-left:100px;
		overflow:auto;
		height:500px;
		width:90% 	
		
	}	
</style>
  
<style>
 .TEXT_NB { 
		BACKGROUND-COLOR:transparent;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: medium none; 
		BORDER-RIGHT: medium none; 
		BORDER-TOP: medium none;	
}
 .TableRow_self {
		background-position : center left;
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
<script LANGUAGE=javascript src="/js/function.js"></script> 
<script LANGUAGE=javascript src="/performance/achivement/achivementTask/achievement.js"></script> 
  
<body>
<html:form action="/performance/achivement/achivementTask/singletObjectTask">	
<br>

<div id="tbl-container">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	 <thead>
        <tr style="position:relative;top:expression(this.offsetParent.scrollTop-1);">   
        	<td align="center" style='color:black' class="TableRow_self" nowrap>
        		<bean:message key="kh.field.field_n"/>\<bean:message key="jx.khplan.khqujian"/>
        	</td> 			
			 <%
			
			  for(int i=0;i<objectCycleList.size();i++){
	       		LazyDynaBean abean=(LazyDynaBean)objectCycleList.get(i);
	       		String str=((String)abean.get("cycle_str"));			  
			 	out.print(" <td align='center'  style='color:black'    class='TableRow_self' >"+str+"</td>");
			 }			 
			 int n=0;
			 %>
		</tr>
	 </thead>
	 	 	 
	 <%
		 for(int j=0;j<objectPointList.size();j++){
			CommonData d=(CommonData)objectPointList.get(j);
			if(n%2==0)
		    {  
		    	out.println("<tr class='trShallow'>");   
		    }else{
		    	out.println("<tr class='trDeep'>");
			}		
			out.println("<td align='left' class='RecordRow' nowrap>");
	 		out.print(d.getDataName());
	  		out.print("</td>");
			
			for(int i=0;i<objectCycleList.size();i++){			 	
	 			out.println("<td align='center' class='RecordRow' nowrap>");
	 			LazyDynaBean abean=(LazyDynaBean)objectCycleList.get(i);
	 			String index=(String)abean.get("index");
	 					
	 			String _value=(String)abean.get(d.getDataValue());
	 			if(_value.equals("no"))
	 			{
	 				out.print("<hr style='color:black size:1px' width='20'/>");
	 				out.print("<input type='hidden' value='"+(String)abean.get(d.getDataValue())+"' name='objectCycleList["+index+"]."+d.getDataValue()+"'/>");
	 			}
	 			else
		 			out.print("<input type='text' value='"+(String)abean.get(d.getDataValue())+"' onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);' onblur='checkValue(this)' name='objectCycleList["+index+"]."+d.getDataValue()+"' class='TEXT_NB' size='8'/>");
	 			out.print("</td>");			
			}
	  		out.print("</tr>");
	 	}   			        	 		           
	 %>	 
</table>
</div>
<table  width="90%" align="center">
     <tr>
         <td align="center">
             <hrms:submit styleClass="mybutton" onclick="saveDataObject()">
            		<bean:message key="button.save"/>
	 	     </hrms:submit>
         	
         	 <hrms:submit styleClass="mybutton" onclick="cancelObject()">
            		<bean:message key="button.return"/>
	 	     </hrms:submit>       
         </td>
    </tr>          
</table>
</html:form>
</body>
<script>
<% if(request.getParameter("b_save")!=null){%>
	
	alert('保存成功');
//	goback();
	
<%}%>
</script>
</html>
