<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style type="text/css"> 
.gztable {
 	border-right:#7b9ebd 1px solid;
 	border-left:#7b9ebd 1px solid;
 	border-top:#7b9ebd 1px solid;
 	border-bottom:#7b9ebd 1px solid;
 	word-break: break-all; 
 	word-wrap:break-word;
}

</style>
<script language="javascript">
		if("${param.oper}"=="close")
		{
			var thevo=new Object();
			thevo.flag="true";
			window.returnValue=thevo;
			window.close();
		 }
		 function save()
		 {	 
		 	courseTrainForm.action="/train/request/permisView.do?b_save=link&stuids=${param.stuids}&oper=close";
			courseTrainForm.submit();
		 }		
</script>
<body>
	<html:form action="/train/request/permisView">
		<table  cellspacing="0" align="center" cellpadding="2" class="gztable common_border_color">
			<tr >
				<td>
		<table border="0" cellspacing="0" align="center" cellpadding="3" >
			<tr>
				<td  nowrap width="70" align="right">
					<bean:message key='jx.khplan.approveresult' />
				</td>
				<td nowrap>
					<input type="text" id="result" readonly="readonly" class="text4">
				</td>
			</tr>
			<tr>
				<td valign="top" nowrap width="70" align="right">
					<logic:equal name="courseTrainForm" property="type" value="pz">
							<bean:message key='kq.register.overrule' />					
					</logic:equal>
					<logic:equal name="courseTrainForm" property="type" value="bh">
							<bean:message key='kq.registr.argue' />						
					</logic:equal>
				</td>
				<td class="common_border_color" nowrap>
					<html:textarea name="courseTrainForm" styleId="permisView"
						property="permisView" cols="40" rows="13"></html:textarea>
				</td>
			 </tr>
		
		</table>
				</td>
			</tr>
		 <tr>
			 	<td class="common_border_color">
			 		<table width="100%" border="0" cellspacing="0"  cellpadding="0">
			 			<tr>
			 				<td>
			 			&nbsp;&nbsp;<bean:write name="courseTrainForm" property="info"/>
			 				</td>			 
				   		 </tr>
					</table>
			 	</td>			 
			 </tr>
		</table>
		<br>
		<table border="0" cellspacing="0" align="center" cellpadding="2" width="50%">
			<tr>
				<td align="center">
					<input type="button" class="mybutton" id="okButton" 
						value="&nbsp;<bean:message key='button.ok' />&nbsp;"
						onClick="save();" />
				
					<input type="button" class="mybutton"
						value="&nbsp;<bean:message key='button.cancel' />&nbsp;"
						onClick="window.close();">
				</td>
			</tr>
		</table>
	</html:form>
	<script>
		var spResult = $('result');
		if("${courseTrainForm.type}"=="pz")
			spResult.value="<bean:message key='info.appleal.state5'/>";
		else
			spResult.value="<bean:message key='info.appleal.state6'/>";
	</script>
</body>

