<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>

  </head>
   <script LANGUAGE=javascript src="/js/function.js"></script>
   <script LANGUAGE=javascript src="/performance/achivement/achivementTask/achievement.js"></script> 
   <script language='javascript' >
   		function closeWin(){
   			if(window.showModalDialog){
   				parent.window.close();
   			}else{
   				parent.parent.Ext.getCmp("batchUpdateWin").close();
   			}
   		}
   		<% 
   			String callbackFunc = request.getParameter("callbackFunc");
  			if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("1")){  
	   			String target_id = request.getParameter("target_id");
	   			String callbackFunc2 = request.getParameter("callbackFunc");
   		%>
   			var target_id = "<%=target_id%>";
	   		if(window.showModalDialog){
				parent.window.returnValue = "1";
			}else{
				eval(<%=callbackFunc2%>)("1",target_id);
			}
			closeWin();
   		<% } %>
   		var callbackFunc = "<%=callbackFunc%>";
   </script>
  <body>
   
 <html:form action="/performance/achivement/achivementTask">
  <Br>
   <table align="center"  width="90%">
	<tr>
	 <td>
  		<fieldset align="center" style="width:100%;">
   			<legend><bean:message key="menu.gz.batch.update"/></legend>	 
				<table width="100%" border="0" cellspacing="0" align="left" cellpadding="2">
				 
				  <tr>
				    <td align="right" width='15%' >
				    <Br>
				      <bean:message key="label.performance.perPoint"/>
				    </td>
				    <td width='85%'>
				    <br>
				    &nbsp; 
				       <html:select name="achievementTaskForm" property="pointId" size="1"  onchange="" style="width:85%" >
					   		<html:optionsCollection property="selectedPointList" value="dataValue" label="dataName"/>
					   </html:select>  
					 </td>
				  </tr>
				  <tr>
				    <td  heigth='50'  align="right" valign="top"><bean:message key="label.gz.update.src"/></td>
				    <td>
				    	&nbsp;&nbsp;<input type='text' name='point_value'  onblur='checkValue(this)'   value=''  class="inputtext"/>
				    </td>
				  </tr>
				 <tr>
				    <td colspan='2' >&nbsp;</td>
				 </tr>
				</table>
   			
   			
   		</fieldset>
	</td>
	</tr>
	
	<tr><td align='center' > 
			<Input type='button' value='<bean:message key="button.ok"/>'  class="mybutton"  onclick="batchUpdate('<%=(request.getParameter("target_id"))%>')"  />
			<Input type='button' value='<bean:message key="button.cancel"/>'   class="mybutton"  onclick='closeWin()'  />	
	</td></tr>
	
	</table>
   
   
   
 </html:form>
   
  </body>
</html>
