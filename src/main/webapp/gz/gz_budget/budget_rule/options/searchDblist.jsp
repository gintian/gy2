<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<script language="JavaScript" src="/gz/gz_budget/budget_rule/options/budgetOptions.js"></script>
<%
	String selectdb = ResourceFactory.getProperty("leaderteam.setdb.choicedb");
%>
<hrms:themes />
<html:form action="/gz/gz_budget/budget_rule/options">
<table width="285px" border="0" cellspacing="1"  align="center" cellpadding="1" class="RecordRow">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
		<%=selectdb %>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
	   	  <td width="100%" align="left" nowrap>
	   	  	<table>
		   	  	<logic:iterate id="db" name="budgetSysForm" property="selectDblist">
		          <tr>
		            <td nowrap>
		            	<logic:equal name="db" property="isCheck" value="1">
	           				<input type="checkbox"   name="dbstr" checked>
							<bean:write name="db" property="dbname" filter="true"/>
	           			</logic:equal>
	           			<logic:notEqual name="db" property="isCheck" value="1">
	           				<input type="checkbox"   name="dbstr">
							<bean:write name="db" property="dbname" filter="true"/>
	           			</logic:notEqual>
		                <input type="hidden" name="ids" value="<bean:write name='db' property='pre' filter='true'/>">
			    		<input type="hidden" name="dbname" value="<bean:write name='db' property='dbname' filter='true'/>">
		            </td>
		          </tr>
	        </logic:iterate>
        </table>
	      </td>
      </tr>
      <tr>
	      <td align="center" class="RecordRow" nowrap  colspan="3">
	      		<input type="button" name="btnreturn" value="<bean:message key="button.ok"/>" class="mybutton" onclick=" saveDblist();">
	     		<input type="button" name="btnreturn" value="<bean:message key="button.close"/>" class="mybutton" onclick=" window.close();">
	      </td>
      </tr>
</table>
</html:form>

