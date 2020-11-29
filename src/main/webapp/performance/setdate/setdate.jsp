<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes />
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.police.PoliceForm" %>
<%
PoliceForm policeForm = (PoliceForm)session.getAttribute("policeForm");
String flag=policeForm.getFromFlag();
String str=""; 
if(flag!=null&&flag.equals("kh"))
{
	str=" style='display:none' ";
	 
}
 %>


<style>
<!--
.hand { cursor:pointer;
}
-->
</style>
<script type="text/javascript">
<!--
	function saveSet() {
		policeForm.action="/performance/setdate/setdate.do?b_save=link&save=save";
		policeForm.target="il_body";
		policeForm.submit();	
	}
//-->
</script>
<html:form action="/performance/setdate/setdate"><br/><br/>
	<table width="50%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
		<thead>
           <tr>
            	<td align="center" class="TableRow" colspan="2" nowrap><bean:message key="police.customization.setdate"/></td>	    	    	    
           </tr>
		</thead>
		
        <tbody>  
        	<tr class="trShallow"   <%=str%> >
            	<td align="right" class="RecordRow" nowrap><bean:message key="police.workinfo.menu.news"/>&nbsp; </td>            
            	<td align="left" class="RecordRow" nowrap>
            		<logic:equal value="0" name="policeForm" property="news">
            			<input id="newsyear" type="radio" name="news" checked="checked" value="0" class="hand"/><label class="hand" for="newsyear"><bean:message key="police.lable.year"/></label>
            		</logic:equal>
            		<logic:notEqual value="0" name="policeForm" property="news">
            			<input id="newsyear" type="radio" name="news" value="0" class="hand"/><label class="hand" for="newsyear"><bean:message key="police.lable.year"/></label>
            		</logic:notEqual>
            		&nbsp;&nbsp;&nbsp;
            		<logic:equal value="2" name="policeForm" property="news">
            			<input id="newsweek" type="radio" name="news" checked="checked" value="2" class="hand"/><label class="hand" for="newsweek"><bean:message key="police.lable.week"/></label>
            		</logic:equal>
            		<logic:notEqual value="2" name="policeForm" property="news">
            			<input id="newsweek" type="radio" name="news" value="2" class="hand"/><label class="hand" for="newsweek"><bean:message key="police.lable.week"/></label>
            		</logic:notEqual>
            		&nbsp;&nbsp;&nbsp;
            		<logic:equal value="1" name="policeForm" property="news">
            			<input id="newsmonth" type="radio" name="news" checked="checked" value="1" class="hand"/><label class="hand" for="newsmonth"><bean:message key="police.lable.month"/></label>
            		</logic:equal>
            		<logic:notEqual value="1" name="policeForm" property="news">
            			<input id="newsmonth" type="radio" name="news" value="1" class="hand"/><label class="hand" for="newsmonth"><bean:message key="police.lable.month"/></label>
            		</logic:notEqual>
            	</td>	    	    	    		        	        	        
          	</tr>
          	<tr class="trShallow" <%=str%>  >
            	<td align="right" class="RecordRow" nowrap><bean:message key="police.workinfo.orgtask"/>&nbsp; </td>            
            	<td align="left" class="RecordRow" nowrap>
            		<logic:equal value="0" name="policeForm" property="orgtask">
            			<input id="taskyear" type="radio" name="orgtask" checked="checked" value="0" class="hand"/><label class="hand" for="taskyear"><bean:message key="police.lable.year"/></label>
            		</logic:equal>
            		<logic:notEqual value="0" name="policeForm" property="orgtask">
            			<input id="taskyear" type="radio" name="orgtask" value="0" class="hand"/><label for="taskyear" class="hand"><bean:message key="police.lable.year"/></label>
            		</logic:notEqual>
            		&nbsp;&nbsp;&nbsp;
            		<logic:equal value="2" name="policeForm" property="orgtask">
            			<input id="taskweek" type="radio" name="orgtask" checked="checked" value="2" class="hand"/><label class="hand" for="taskweek"><bean:message key="police.lable.week"/></label>
            		</logic:equal>
            		<logic:notEqual value="2" name="policeForm" property="orgtask">
            			<input id="taskweek" type="radio" name="orgtask" value="2" class="hand"/><label class="hand" for="taskweek"><bean:message key="police.lable.week"/></label>
            		</logic:notEqual>
            		&nbsp;&nbsp;&nbsp;
            		<logic:equal value="1" name="policeForm" property="orgtask">
            			<input id="taskmonth" type="radio" name="orgtask" checked="checked" value="1" class="hand"/><label class="hand" for="taskmonth"><bean:message key="police.lable.month"/></label>
            		</logic:equal>
            		<logic:notEqual value="1" name="policeForm" property="orgtask">
            			<input id="taskmonth" type="radio" name="orgtask" value="1" class="hand"/><label class="hand" for="taskmonth"><bean:message key="police.lable.month"/></label>
            		</logic:notEqual>
            	</td>	    	    	    		        	        	        
          	</tr>
          	<tr class="trShallow">
            	<td align="right" class="RecordRow" nowrap><bean:message key="police.workinfo.persontask"/>&nbsp; </td>            
            	<td align="left" class="RecordRow" nowrap>
            		<logic:equal value="0" name="policeForm" property="persontask">
            		&nbsp;
            			<input id="persontaskyear" type="radio" name="persontask" checked="checked" value="0" class="hand"/><label class="hand" for="persontaskyear"><bean:message key="police.lable.year"/></label>
            		</logic:equal>
            		<logic:notEqual value="0" name="policeForm" property="persontask">
            		&nbsp;
            			<input id="persontaskyear" type="radio" name="persontask" value="0" class="hand"/><label class="hand" for="persontaskyear"><bean:message key="police.lable.year"/></label>
            		</logic:notEqual>
            		&nbsp;&nbsp;&nbsp;
            		<logic:equal value="2" name="policeForm" property="persontask">
            			<input id="persontaskweek" type="radio" name="persontask" checked="checked" value="2" class="hand"/><label for="persontaskweek" class="hand"><bean:message key="police.lable.week"/></label>
            		</logic:equal>
            		<logic:notEqual value="2" name="policeForm" property="persontask">
            			<input id="persontaskweek" type="radio" name="persontask" value="2" class="hand"/><label for="persontaskweek" class="hand"><bean:message key="police.lable.week"/></label>
            		</logic:notEqual>
            		&nbsp;&nbsp;&nbsp;
            		<logic:equal value="1" name="policeForm" property="persontask">
            			<input id="persontaskmonth" type="radio" name="persontask" checked="checked" value="1" class="hand"/><label class="hand" for="persontaskmonth"><bean:message key="police.lable.month"/></label>
            		</logic:equal>
            		<logic:notEqual value="1" name="policeForm" property="persontask">
            			<input id="persontaskmonth" type="radio" name="persontask" value="1" class="hand"/><label for="persontaskmonth" class="hand"><bean:message key="police.lable.month"/></label>
            		</logic:notEqual>
            	</td>	    	    	    		        	        	        
          	</tr>
          	<tr>
            	<td align="center" style="height:40px" colspan="2" nowrap>
            		<input class="mybutton" type="button" name="bu" value="<bean:message key="button.save"/>"  onclick="saveSet()"/>
            	</td>            	    	    	    		        	        	        
          	</tr>
		</tbody>        
	</table>
</html:form>
