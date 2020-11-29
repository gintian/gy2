<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	int i=0;
	UserView userView = (UserView)session.getAttribute(WebConstant.userView);
	String bosflag = userView.getBosflag();
%>
<script language="javascript">
<!--
	function editsort(id)
	{
	    operationsortForm.action = "/system/param/operationsort.do?b_sortinfo=link&operationid="+id;
	    operationsortForm.submit();
	}
	function eidtflag(moduleid,id)
	{
		var checkbox = document.getElementById(id);
		var hashvo=new ParameterSet();
		if(checkbox.checked)
		{
	        hashvo.setValue("checked","true");
        }else{
        	hashvo.setValue("checked","false");
        }
        hashvo.setValue("operationid",moduleid);
        var request=new Request({method:'post',onSuccess:eidtflag_ok,functionId:'1012010016'},hashvo);
		
	}
	function eidtflag_ok(outparamters)
	{
		
	}
//-->
</script>
<html:form action="/system/param/operationsort"><!-- 系统管理，参数设置，hr页面，内容与顶部之间间隔10px   jingq  add 2014.12.23 -->
	<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" <%if(!"hcm".equals(bosflag)){ %>style="margin-top:10px;"<%} %>>
   	 	 <THEAD>
   	 	 	<tr>
   	 	 		<td align="center" class="TableRow" width="10%">
   	 	 			<bean:message key="train.evaluationStencil.no" />
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="20%">
   	 	 			<bean:message key="system.operation.template" />
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="50%">
   	 	 			<bean:message key="system.operation.type" />
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="10%">
   	 	 			<bean:message key="parttime.param.flag" /><bean:message key="kh.field.flag" />
   	 	 		</td>
   	 	 		<td align="center" class="TableRow" width="10%">
   	 	 			<bean:message key="button.orgmapset" />
   	 	 		</td>
   	 	 	</tr>
   	 	 </THEAD>
   	 	 <hrms:extenditerate id="element" name="operationsortForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="300" scope="session">
   	 	 	<%
   	 	 		if(i%2==0){
   	 	 	%>
   	 	 		<tr class="trDeep">
   	 	 	<%
   	 	 		}else{
   	 	 	%>
   	 	 		<tr class="trShallow">
   	 	 	<%
   	 	 		}
   	 	 		i++;
   	 	 	%>
			   		<td align="right" class="RecordRow" width="10%">
			   			<bean:write name="element" property="id" filter="false"/>&nbsp;
				    </td>            
			   		<td align="left" class="RecordRow" width="20%">
			   			<bean:write name="element" property="name" filter="false"/>&nbsp;
				    </td>
				    <td align="left" class="RecordRow" width="50%">
				    	<bean:write name="element" property="text" filter="false"/>&nbsp;
				    </td>
				    <td align="center" class="RecordRow" width="10%">
				    	<logic:notEqual name="element" property="flag" value="0">
				    		&nbsp;
				    	</logic:notEqual>
				    	<logic:equal name="element" property="flag" value="1">
				    		<logic:equal name="element" property="check" value="true">
				    			<INPUT TYPE=checkbox ID=<%=i%> checked onclick="eidtflag('<bean:write name="element" property="id" filter="false"/>','<%=i%>')">
				    		</logic:equal>
				    		<logic:equal name="element" property="check" value="false">
				    			<INPUT TYPE=checkbox ID=<%=i%> onclick="eidtflag('<bean:write name="element" property="id" filter="false"/>','<%=i%>')" >
				    		</logic:equal>
				    	</logic:equal>
				    </td> 
				    <td align="center" class="RecordRow" width="10%">
				    	<img src="/images/edit.gif" onclick="editsort('<bean:write name="element" property="id" filter="false"/>')" style="cursor:hand"/>
				    </td>            
				</tr> 
		</hrms:extenditerate>
   	 	
   	 </table>
   	 
</html:form>
