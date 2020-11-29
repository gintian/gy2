<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>


<html>
	<head>
		<title><bean:message key="org.maip.formula.definition" /></title>
	</head>
	<link href="../../css/css1.css" rel="stylesheet" type="text/css">
	<hrms:themes/>
	<script language="javascript">

		function exprCheck(){
			 if(event.keyCode == 13 || event.keyCode == 32){
			 	 event.returnValue = "";
			 }
		}

		
		function save(){
			//alert("进入保存");
			if(iff.exprName.value==""){
				alert(NAME_NOT_EMPTY+"！");
				iff.exprName.focus();
				return;
			}
			if(iff.exprName.value.indexOf("\"")!=-1){
				alert("名称不能有双引号"+"！");
				iff.exprName.focus();
				return;
			}
			if(iff.exprName.value.indexOf("'")!=-1){
				alert("名称不能有单引号"+"！");
				iff.exprName.focus();
				return;
			}
			if(iff.leftExpr.value == ""){
				alert(LEFTEXPR_NONULL+"！");
				iff.leftExpr.focus();
				return ;
			}
			if(iff.rightExpr.value == ""){
				alert(RIGHTEXPR_NONULL+"！");
				iff.rightExpr.focus();
				return ;
			}
			
			var flag = 2;
			if(iff.formulaType[1].checked){
				flag = 3;	
			}else if(iff.formulaType[2].checked){
				flag = 4;	
			}
			//alert(flag);
			//alert(iff.rightExpr.value);
			var hashvo=new ParameterSet();		
			hashvo.setValue("reportType",iff.reportType.value);	
			hashvo.setValue("expid",iff.expid.value);	
		    hashvo.setValue("tabid",iff.tabid.value);
		    hashvo.setValue("formulaType",flag);
		    hashvo.setValue("exprName",iff.exprName.value);
		    hashvo.setValue("leftExpr",iff.leftExpr.value);
		    hashvo.setValue("rightExpr",iff.rightExpr.value);    
		    
		   	var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:saveResult,functionId:'03020000037'},hashvo);			
		}
		
		function saveResult(outparamters){
			var info = outparamters.getValue("info");
			if(info == "ok"){
				//refurbish1()
				close1();
			}else{
				alert(info);
			}
		}
		
		function close1(){
			window.opener=null;//不会出现提示信息
			var valWin = parent.Ext.getCmp('addformulaWin');
   			if(valWin)
   				valWin.close();
   			else
   				window.close();	
		}
		
		
		function refurbish1(){
			var url="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=${editFormulaForm.returnFlag}&status=${editFormulaForm.status}";
			window.opener.location.href=url;
			window.opener=null;//不会出现提示信息
			close1();	
		}
	</script>

	<body >
		<form name="iff" action="/report/edit_report/editformula.do" method="post">
		 <fieldset align="center" style="width: 94%;margin:auto;">
   			 <legend><bean:message key="edit_report.reportsFormular" /></legend>
			<table width="90%" border="0" cellspacing="1" align="center" cellpadding="1">
				<tr>
				<logic:equal name="editFormulaForm" property="typeFlag" value="-1">
					<TD colspan ="2">
						<input TYPE="radio" NAME="formulaType" value="2" checked >
						<bean:message key="edit_report.rowFormula" />&nbsp;&nbsp;
						<input TYPE="radio" NAME="formulaType" value="3">
						<bean:message key="edit_report.columnFormula" />&nbsp;&nbsp;
						<input TYPE="radio" NAME="formulaType" value="4">
						<bean:message key="edit_report.gridFormula" />
					</TD>
				</logic:equal>
				
				<logic:equal name="editFormulaForm" property="typeFlag" value="2">
					<TD colspan ="2">
						<input TYPE="radio" NAME="formulaType" value="2" checked >
						<bean:message key="edit_report.rowFormula" />&nbsp;&nbsp;
						<input TYPE="radio" NAME="formulaType" value="3">
						<bean:message key="edit_report.columnFormula" />&nbsp;&nbsp;
						<input TYPE="radio" NAME="formulaType" value="4">
						<bean:message key="edit_report.gridFormula" />
					</TD>
				</logic:equal>
				
				<logic:equal name="editFormulaForm" property="typeFlag" value="3">
					<TD colspan ="2">
						<input TYPE="radio" NAME="formulaType" value="2"  >
						<bean:message key="edit_report.rowFormula" />&nbsp;&nbsp;
						<input TYPE="radio" NAME="formulaType" value="3" checked>
						<bean:message key="edit_report.columnFormula" />&nbsp;&nbsp;
						<input TYPE="radio" NAME="formulaType" value="4">
						<bean:message key="edit_report.gridFormula" />
					</TD>
				</logic:equal>
				
				<logic:equal name="editFormulaForm" property="typeFlag" value="4">
					<TD colspan ="2">
						<input TYPE="radio" NAME="formulaType" value="2"  >
						<bean:message key="edit_report.rowFormula" />&nbsp;&nbsp;
						<input TYPE="radio" NAME="formulaType" value="3" >
						<bean:message key="edit_report.columnFormula" />&nbsp;&nbsp;
						<input TYPE="radio" NAME="formulaType" value="4" checked>
						<bean:message key="edit_report.gridFormula" />
					</TD>
				</logic:equal>
				
				</tr>
				<tr>
					<TD >
						<bean:message key="userlist.username" />&nbsp;
					</TD>
					<td>
						<INPUT type="text" name="exprName" class="text4" style="width:298px" size="44" maxlength="50" value="${editFormulaForm.exprName}">
					</TD>
				</tr>
				<tr>
					<TD >
						<bean:message key="edit_report.left" />&nbsp;
					</td>
					<TD>
						<TEXTAREA NAME="leftExpr" style="overflow-y:hidden;width:298px" ROWS="2" COLS="46" onkeydown="exprCheck()">${editFormulaForm.leftExpr}</TEXTAREA>
					</TD>
				</tr>
				<tr>
					<TD>
						<bean:message key="edit_report.right" />&nbsp;
					</TD>
					<TD>
						
						<TEXTAREA NAME="rightExpr" style="overflow-y:hidden;width:298px" ROWS="5" COLS="46" onkeydown="exprCheck()">${editFormulaForm.rightExpr}</TEXTAREA>
			
					</TD>					
				</tr>

				</table>
			</fieldset>
			<table width="90%" border="0" cellspacing="1" align="center" cellpadding="1" style="margin-top: 3px;">
				<tr align="center">
					<td colspan="2">
						<input type="hidden" name="reportType" value="${editFormulaForm.flag}"> 
						<input type="hidden" name="expid" value="${editFormulaForm.expid}"> 
						<input type="hidden" name="tabid" value="${editFormulaForm.tabid}"> 
						<input TYPE="button" value="<bean:message key="options.save" />" class="mybutton" onClick="save()">
						<input TYPE="button" value="<bean:message key="kq.register.kqduration.cancel" />" class="mybutton" style="margin-left: -2px;" onClick="close1()">
					</td>

				</tr>
			</table>
	</body>
</html>
