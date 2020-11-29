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
		function leftExprCheck() {
			 //alert(event.keyCode);
			 //只能是数值，退格，删除
	 		if(!(event.keyCode >= 48 && event.keyCode <= 57 || event.keyCode == 8 || event.keyCode==46 ||
	 		   (event.keyCode>=96 && event.keyCode <=105) )){	 
	 			event.returnValue = "";
	 		}

		}
		function rightExprCheck(){
			 if(event.keyCode == 13 || event.keyCode == 32){
			 	 event.returnValue = "";
			 }
		}
		function excludeExprCheck() {
			 //alert(event.keyCode);
			 //只能是数值，退格，删除,逗号
	 		if(!(event.keyCode >= 48 && event.keyCode <= 57 || event.keyCode == 8 || event.keyCode==46 ||event.keyCode==188 ||
	 		   (event.keyCode>=96 && event.keyCode <=105) )){	 
	 			event.returnValue = "";
	 		}

		}
		
		function save(){
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
			
			var flag = 0;
			if(iff.formulaType[1].checked){
				flag = 1;	
			}
			if(iff.decimal.value!=""){
			var decimal2 = iff.decimal.value;
			var temp ="0123456789";
			if(temp.indexOf(decimal2)==-1){
			alert("请输入数字");
			return;
			}
			}
			//alert(iff.tabid.value);
			var hashvo=new ParameterSet();		
			hashvo.setValue("expid",iff.expid.value);
		    hashvo.setValue("tabid",iff.tabid.value);
		    hashvo.setValue("formulaType",flag);
		    hashvo.setValue("exprName",iff.exprName.value);
		    hashvo.setValue("leftExpr",iff.leftExpr.value);
		    hashvo.setValue("rightExpr",iff.rightExpr.value);  
		    hashvo.setValue("npercent",iff.decimal.value);
		    hashvo.setValue("excludeexpr",iff.excludeexpr.value);    
		   	var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:saveResult,functionId:'03020000037'},hashvo);			
		}
		
		function saveResult(outparamters){
			var info = outparamters.getValue("info");
			if(info == "ok"){
				//alert("公式效验成功");
				//refurbish1();	
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
			editFormulaForm.action="/report/edit_report/editReport.do?b_initFormula=initFormula&flag=${editFormulaForm.returnFlag}&status=${editFormulaForm.status}";
			editFormulaForm.submit();
			//window.opener=null;//不会出现提示信息
			close1();
		}
function upadd(valueadd){
	var addValue  = document.getElementById(valueadd).value;
	if(addValue.length<1){
		addValue = 0;
	}
	if(addValue>=4){
		document.getElementById(valueadd).value=4;
		return;
	}

    document.getElementById(valueadd).value = parseInt(addValue)+1
}

function inputDecValue(){//xiegh 20170627 add 表内计算  设置小数位数 支持手动输入 
	var decValue  = document.getElementsByName('decimal')[0].value;
	if(decValue>4||decValue<0||('0123456789'.indexOf(decValue) == -1)){
		alert('您输入的位数有问题，小数位数只能在0到4之间，请重新输入！');
		document.getElementsByName('decimal')[0].value = 0;
	}
}
function downcut(valuecut){
	var cutValue  = document.getElementById(valuecut).value;
	if(cutValue.length<1){
		cutValue = 0;
	}
	if(cutValue<=0){
		document.getElementById(valuecut).value=0;
		return;
	}
    document.getElementById(valuecut).value = parseInt(cutValue)-1
}
function changecol(){
	 document.getElementById("row").style.display='block';
   	 document.getElementById("col").style.display='none';
}
function changerow(){
	 document.getElementById("row").style.display='none';
   	 document.getElementById("col").style.display='block';
}
	</script>
	<style>
	.m_arrow {
	width: 16px;
	height: 10px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 3px;
	padding-bottom:2px;
	padding-top:0px;
	cursor: default;
	}
	</style>

	<body >
		<form name="iff" action="/report/edit_report/editformula.do" method="post" >
   		 <fieldset align="center" style="width: 94%; margin:auto;">
   			 <legend><bean:message key="edit_report.innerFormula" /></legend>
   			<table border="0" align="center">
   		<tr>
   		<td>	 
			<table width="90%" cellspacing="2" border="0" align="center" cellpadding="2">
				<tr valign="middle">	
				<logic:equal name="editFormulaForm" property="typeFlag" value="-1">
					<td colspan="1" align="right">
					
						<INPUT TYPE="radio" NAME="formulaType" style="vertical-align: middle;" value="0" checked onclick="changerow()">
						<bean:message key="edit_report.rowFormula" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<INPUT TYPE="radio" NAME="formulaType" style="vertical-align: middle;" value="1"  onclick="changecol()">
						<bean:message key="edit_report.columnFormula" />
						&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="rowcheckanalyse.paichu" />	
						<INPUT type="text" class="text4" name="excludeexpr" style="vertical-align: middle;" size="8" onkeydown="excludeExprCheck()" value="${editFormulaForm.excludeexpr}">
						</td>
						<TD id='col' style='display:block;' align="left">
						<bean:message key="rowcheckanalyse.col" />
						</TD>
						<TD id='row' style='display:none;' align="left">
						<bean:message key="colcheckanalyse.row" />		
						</TD>
				</logic:equal>
				<logic:equal name="editFormulaForm" property="typeFlag" value="0">
					<TD colspan="2" align="right">
						<INPUT TYPE="radio" NAME="formulaType" style="vertical-align: middle;" value="0" checked onclick="changerow()">
						<bean:message key="edit_report.rowFormula" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<INPUT TYPE="radio" NAME="formulaType" style="vertical-align: middle;" value="1" onclick="changecol()">
						<bean:message key="edit_report.columnFormula" />
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="rowcheckanalyse.paichu" />	
						<INPUT type="text" class="text4" name="excludeexpr" style="vertical-align: middle;" size="8" onkeydown="excludeExprCheck()" value="${editFormulaForm.excludeexpr}">
					</TD>
						<TD id='col' style='display:block;' align="left">
						<bean:message key="rowcheckanalyse.col" />
						</TD>
						<TD id='row' style='display:none;' align="left">
						<bean:message key="colcheckanalyse.row" />		
						</TD>
				</logic:equal>
				<logic:equal name="editFormulaForm" property="typeFlag" value="1">
					<TD colspan="2" align="right">
						<INPUT TYPE="radio" NAME="formulaType" style="vertical-align: middle;" value="0" onclick="changerow()" >
						<bean:message key="edit_report.rowFormula" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<INPUT TYPE="radio" NAME="formulaType" style="vertical-align: middle;" value="1" checked  onclick="changecol()">
						<bean:message key="edit_report.columnFormula" />	
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="rowcheckanalyse.paichu" />	
						<INPUT type="text" class="text4" name="excludeexpr" style="vertical-align: middle;" size="8" onkeydown="excludeExprCheck()" value="${editFormulaForm.excludeexpr}">
					</TD>
						<TD id='col' style='display:none;'>
						<bean:message key="rowcheckanalyse.col" />
						</TD>
						<TD id='row' style='display:block;'>
						<bean:message key="colcheckanalyse.row" />		
						</TD>
				</logic:equal>
				
				</tr>
				</table>
				<table width="90%" cellspacing="2" border="0" align="center" cellpadding="2">
				<tr valign="middle">
					<td width="80px" align="right"><bean:message key="kq.shift.relief.name" /></td>
					<TD align="left">
						<INPUT type="text" name="exprName" style="vertical-align: middle;width:261px" size="41" class="text4" maxlength="50" value="${editFormulaForm.exprName}">
					</TD>
				</tr>
				<tr>
					<td width="80px" align="right"><bean:message key="edit_report.left" /></td>
					<TD align="left">
						<INPUT type="text" name="leftExpr" style="vertical-align: middle;width:261px" size="41" class="text4" onkeydown="leftExprCheck()" value="${editFormulaForm.leftExpr}">
					</TD>
				</tr>
				<tr>
					<td width="80px" align="right"><bean:message key="edit_report.right" /></td>
					<TD >
						<TEXTAREA NAME="rightExpr" style="overflow-y:hidden;width:100%;" style="vertical-align: middle;resize:none" ROWS="3" COLS="40"  onkeydown="rightExprCheck()">${editFormulaForm.rightExpr}</TEXTAREA>
					</TD>
				</tr>
				<tr valign="middle">
					<td width="80px" align="right">小数位</td>
					<td style="padding-left:0">
						<table>
							<tr>
								<td align="left">
									<input type="text" class="textColorRead"  onkeyup="inputDecValue()"  name="decimal" id="decimal" value="${editFormulaForm.npercent}" maxlength="1" style="width:40; " >
								</td>
								<td align="left">
									<table border="0" cellspacing="0" cellpadding="0" align="left" >
		      							<tr><td><button id="y_up" class="m_arrow" onclick="upadd('decimal');return false;"><img src="/images/button_vert1.gif"></img></button></td></tr>
		      							<tr><td><button id="y_down" class="m_arrow" onclick="downcut('decimal');return false;"><img src="/images/button_vert2.gif"></img></button></td></tr>
				          			</table>
				          		</td>
	          				</tr>
						</table>
					</td>
				</tr>
			</fieldset>
			
			<table width="90%" border="0" cellspacing="1" align="center" cellpadding="1" style="margin-top: 2px;">
				<tr align="center">
					<TD colspan="2">
						<INPUT type="hidden" name="expid" value="${editFormulaForm.expid}"> 
						<INPUT type="hidden" name="tabid" value="${editFormulaForm.tabid}"> 
						<INPUT TYPE="button" value="<bean:message key="updateunitinfo.save" />" class="mybutton" onClick="save()">
						<INPUT TYPE="button" value="<bean:message key="kq.register.kqduration.cancel" />" class="mybutton" onClick="close1()">
					</TD>
				</tr>
			</table>
		
	
	</body>
</html>
