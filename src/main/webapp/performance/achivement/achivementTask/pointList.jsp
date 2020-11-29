<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.achivement.AchievementTaskForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<%
	AchievementTaskForm achievementTaskForm=(AchievementTaskForm)session.getAttribute("achievementTaskForm");
	ArrayList pointList=achievementTaskForm.getPointList();
	
 %>					 
<html>
  <head>
  
  </head>
  <script LANGUAGE=javascript src="/performance/achivement/achivementTask/achievement.js"></script> 
  <script language="JavaScript" src="/js/validate.js"></script>
  <script language="JavaScript" src="/js/function.js"></script>
  <script language='javascript' >
   function closeWin(){
  	 if(window.showModalDialog){
  	 	parent.window.close();
  	 }else{
  	 	parent.parent.Ext.getCmp("setPointSortWin").close();
  	 }
   } 
  <%  
   		if(request.getParameter("b_savePointSort")!=null&&request.getParameter("b_savePointSort").equals("save"))
   		{
   			String target_id = request.getParameter("target_id");
   		%>
   			if(window.showModalDialog){
   				parent.window.returnValue = "1";
   			}else{
   				parent.parent.setPointSort_ok("1",<%=target_id%>);
   			}
   			closeWin();
  <%}%>
  
  </script>
  <body>
    <html:form action="/performance/achivement/achivementTask">
    <Br>
    
    <table style='width:100%;height:100%' valign='top'> 
	 	<tr> <td   valign="top">
		    <fieldset align="center" style="width:90%;">
    							 <legend ><bean:message key="menu.gz.sortitem"/></legend>
    			<table valign="top" ><tr>
    			<td>
    					<select name='right_fields' multiple="multiple" size="18"   style="width:225px;font-size:9pt"  >	 	 						
	 	 						<%
	 	 							for(int i=0;i<pointList.size();i++)
	 	 							{
	 	 								CommonData d=(CommonData)pointList.get(i);
	 	 								out.println("<option value='"+d.getDataValue()+"' >"+d.getDataName()+"</option>");
	 	 							}
	 	 						 %>
	 	 				</select> 
    			</td>
    			<td valign='middle' align='right' >
    				&nbsp;&nbsp;<Input type='button' value='<bean:message key="kq.shift.cycle.up"/>'  class="mybutton"  onclick="upItem(document.getElementsByName('right_fields')[0]);"  /><br><br>
    				&nbsp;&nbsp;<Input type='button' value='<bean:message key="kq.shift.cycle.down"/>'  class="mybutton"  onclick="downItem(document.getElementsByName('right_fields')[0])"  /><br><br>
    				&nbsp;&nbsp;<Input type='button' value='<bean:message key="reporttypelist.confirm"/>'  class="mybutton"  onclick='subPointSort("${achievementTaskForm.target_id}")'  /><br><br>
    				&nbsp;&nbsp;<Input type='button' value='<bean:message key="kq.register.kqduration.cancel"/>'  class="mybutton"  onclick='closeWin()'  /><br><br>
    			</td>
    			</tr></table>
    		
    		</fieldset>
    	</td></tr>
    </table>
    </html:form>
  </body>
</html>
