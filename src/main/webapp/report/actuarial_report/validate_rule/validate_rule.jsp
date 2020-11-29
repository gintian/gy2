<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<%@ page
	import="java.util.*,com.hjsj.hrms.actionform.report.actuarial_report.validate_rule.ValidateRuleForm,com.hrms.frame.dao.RecordVo,org.apache.commons.beanutils.LazyDynaBean"%>


<%
	ValidateRuleForm validateRuleForm = (ValidateRuleForm) session
			.getAttribute("validateRuleForm");
%>
<html>
	<head>
		<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
		<script language="JavaScript" src="/js/validate.js"></script>
		<script language="JavaScript" src="/js/function.js"></script>
		<script language='javascript'>


	
	function returnInfo(outparamters)
	{
	     var context=outparamters.getValue("error");
		 if(context!=""){
		 alert(context);
		 return;
		 }else{
			alert("保存成功!");
	 	 }
	}
	
	function onsave(){
	
  		var paramcopy = document.getElementsByName("paramcopy")[0].value;
  		//alert(paramcopy);
  		 var hashvo=new ParameterSet();
  		var ss=paramcopy.split(",");
  		for(var i=0;i<ss.length;i++){
  		hashvo.setValue(ss[i],trim(document.getElementsByName(ss[i])[0].value)); 
  		}
  		
	  var In_paramters="unitcode=unitcode"; 		
	  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03060000405',submit_form:true},hashvo);

	
	}

	
	</script>
	</head>
	<style>
.selfTableRow {
	background-color: #C0DEF6;
	font-size: 12px;
	text-align: center;
	vertical-align: middle;
	align: center;
	border: inset 1px #94B6E6;
	COLOR: #103B82;
	BORDER-BOTTOM: #94B6E6 1pt solid;
	BORDER-LEFT: #94B6E6 1pt solid;
	BORDER-RIGHT: #94B6E6 1pt solid;
	BORDER-TOP: #94B6E6 1pt solid;
}
</style>
	<body>

		<form name="validateRuleForm" method="post"
			action="/report/actuarial_report/validate_rule.do">
			<html:hidden styleId="paramcopy" name="validateRuleForm"
				property="paramcopy" />
			

						<table width="90%" border="0" cellspacing="0" align="center"
							cellpadding="0" class="ListTable1">
							<br>
							${validateRuleForm.tableHtml}
						</table>
				<table width="90%">
				<tr>
					<td align='left' width="20px">
					</td>
					<td align="center">
						<input type="button" name="b_add2"
							value="<bean:message key="lable.menu.main.save"/>"
							class="mybutton" onClick="onsave()">
					</td>
				</tr>
			</table>



		</form>
	</body>
</html>
