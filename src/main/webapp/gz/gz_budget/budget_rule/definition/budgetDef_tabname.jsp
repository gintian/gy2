<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
 <%
	String formulaname=request.getParameter("tabname");
	String mode=request.getParameter("mode");
	if (formulaname==null) formulaname="";
	if (mode==null) mode="add";
%>

<script language='javascript' >
	function ok()
	{
		var txtformulaname=document.getElementById('formulaname');
		var formulaname='';
		if (txtformulaname==null){
			return;
		}
		formulaname=txtformulaname.value;
	
		if (formulaname==''){		
			alert(NAME_NOT_EMPTY);
			return;
		}
		var retvo=new Object();	
		retvo.success=1;
		retvo.formulaname=formulaname;		
	    window.returnValue=retvo;
		window.close();
	}
	


</script>

<table align="center" width="395px;" height="90%">
	<tr>
		<td>
		  <fieldset align="left" style="width:100%;height:140px;">
		   <legend>
		   <% if (mode.equals("add")) {%>
		   		<bean:message key="button.new.add"/><bean:message key="gz.budget.budget_examination.budgetTable"/>
		   <%}%> 
		   <% if (mode.equals("update")) {%>
		   		<bean:message key="button.update"/><bean:message key="gz.budget.budget_examination.budgetTable"/>
		   <%}%> 
		   <% if (mode.equals("saveas")) {%>
		   		<bean:message key="jx.khplan.saveas"/><bean:message key="gz.budget.budget_examination.budgetTable"/>
		   <%}%> 
		   
		   
		   </legend>
			<table width="98%" border="0" cellspacing="0" align="center" cellpadding="0">				
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td width="30%" align="right">
						请输入名称：
				     </td>  
					 <td width="70%" align="left">
						<input id="formulaname" type="text" name="formulaname" style="width:200px" class="inputtext" value="<%=formulaname%>">
				     </td>     
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>
			</table>
			</fieldset>
		</td>
	</tr>
	<tr>
		<td>
			<table align="center">
	    		<tr >
			  	  <td>
					<button name="compute" Class="mybutton" onclick="ok();">
					<bean:message key="button.ok"/></button>
				  	<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
				  </td>
		    	</tr>
	    	</table>
		</td>
	</tr>
</table>


  