<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*"%>
<script language="JavaScript" src="/js/validate.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
 <%
	String formulaname=request.getParameter("formulaname");
	if (formulaname==null) formulaname="";
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
<table align="center" width="395px;" style="margin-left:-1px;">
	<tr>
		<td>
		  <fieldset align="left" style="width:100%;">
		   <legend><bean:message key="button.update"/><bean:message key="train.quesType.type_name"/></legend>
			<table width="98%" border="0" cellspacing="0" align="center" cellpadding="0">				
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td width="30%" align="right">
						请输入名称：
				     </td>  
					 <td width="70%" align="left">
						<input id="formulaname" type="text" name="formulaname" class="inputtext" style="width:200px" value="<%=formulaname%>">
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


  