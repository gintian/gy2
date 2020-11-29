<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.gz.voucher.VoucherForm" %>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.lang.*"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script type="text/javascript">
function symbol(cal){
	var clsflag=document.getElementById("clsflag").value;
	if(clsflag==1 || clsflag==3){
			if(document.getElementById("c_itemsql").pos!=null){
			if(document.getElementById("c_itemsql").pos.text.length>0){
				document.getElementById("c_itemsql").pos.text+=cal;
			}else{
				document.getElementById("c_itemsql").pos.text=cal;
			}
		}else{
			document.getElementById("c_itemsql").value +=cal;
		}
	}
	else{
			if(document.getElementById("c_where").pos!=null){
			if(document.getElementById("c_where").pos.text.length>0){
				document.getElementById("c_where").pos.text+=cal;
			}else{
				document.getElementById("c_where").pos.text=cal;
			}
		}else{
			document.getElementById("c_where").value +=cal;
		}
	}
	
}
function function_Wizard(){
    var thecodeurl ="";
    	thecodeurl="/org/autostatic/mainp/function_Wizard.do?b_query=link&salaryid=all"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	symbol(return_vo);
  	}else{
  		return ;
  	}
}
function changeSalarySet()
{
     var salaryid=financial_voucherForm.salaryid.value;
     var hashvo=new ParameterSet();
     var pn_id=document.getElementById("pn_id").value;
	var fl_id=document.getElementById("fl_id").value;
     hashvo.setValue("salaryid",salaryid);
     hashvo.setValue("pn_id",pn_id);
	hashvo.setValue("fl_id",fl_id);
   	 var In_paramters="itemflag=1"; 	
	 var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:changeSalarySetOk,functionId:'3020073015'},hashvo);
}
function changeSalarySetOk(outparameters)
{
   Element.hide('cid');
   var fielditemlist=outparameters.getValue("salaryItemList");
   AjaxBind.bind(financial_voucherForm.itemdesc,fielditemlist);
}
function changeFieldItem(src){
  	 var m =src;
  	 symbol(m);
  	 var hashvo=new ParameterSet();
   	 hashvo.setValue("itemDesc",m);
   	 var In_paramters="flag=1"; 	
	 var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeFieldItem,functionId:'1010020310'},hashvo);		
}
function resultChangeFieldItem(outparamters){
  	var info=outparamters.getValue("info");
  	if(info == "error"){
  		 Element.hide('cid');
  	}else{
  		Element.show('cid');
  		AjaxBind.bind(financial_voucherForm.codeitemId,info);
  	}
}
function changeCodeItem(){
  	var m = document.financial_voucherForm.codeitemId.value;
  	symbol('"'+m+'"');
}

function saveCheckExpr(){
  	var clsflag=document.getElementById("clsflag").value;
  	if (clsflag=="3") clsflag="1";
  	var salaryid=financial_voucherForm.salaryid.value;
  	var hashvo=new ParameterSet();
  	var c_itemsql="";
  	if(clsflag==1 || clsflag==3){
  		 c_itemsql= document.financial_voucherForm.c_itemsql.value;
  	}else{
  			 c_itemsql=document.financial_voucherForm.c_where.value;
	}
	hashvo.setValue("c_expr",getEncodeStr(c_itemsql));   
	hashvo.setValue("salaryid",salaryStr);
	hashvo.setValue("clsflag",clsflag);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
	parameters:In_paramters,onSuccess:resultSaveCheckExpr,functionId:'3020060020'},hashvo);					
}
function resultSaveCheckExpr(outparamters){
	var clsflag=document.getElementById("clsflag").value;
  	var info = outparamters.getValue("info");
  	info = getDecodeStr(info);
  	var m="";
  	if(clsflag==1 || clsflag==3){
  		 m=  document.financial_voucherForm.c_itemsql.value;
  		 m=getEncodeStr(m);
  	}else{
  		m=  document.financial_voucherForm.c_where.value;
  		m=getEncodeStr(m);
  	}
  	
  	if(info=="ok"){
		returnValue=m;
		window.close();
	}else{
		alert(info);
	}
}	
function checkExpr(){
	var clsflag=document.getElementById("clsflag").value;
	if (clsflag=="3") clsflag="1";
	var c_itemsql="";
  	if(clsflag==1 || clsflag==3){
  		 c_itemsql= document.financial_voucherForm.c_itemsql.value;
  	}else{
  			 c_itemsql=document.financial_voucherForm.c_where.value;
	}	
  	var hashvo=new ParameterSet();
  	var salaryid=financial_voucherForm.salaryid.value;
	hashvo.setValue("c_expr",getEncodeStr(c_itemsql));
	hashvo.setValue("salaryid",salaryStr);
	hashvo.setValue("clsflag",clsflag);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
	parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020060020'},hashvo);		
}
function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
  	info = getDecodeStr(info);
	if(info=="ok"){
		b = true;
		alert("<bean:message key='gz.acount.formula.success'/>");
	}else{
		alert(info);
	}
}

function cancel(){
  window.close();
}
</script>
<body>

	<html:form  action="/gz/voucher/searchvoucherdate">
		<html:hidden property ="pn_id"/>
		<html:hidden property ="fl_id"/>
		<html:hidden property ="clsflag"/>
		<table border="0" align="center" style="width:515px;margin-left: -2px;">
			<tr>
				<td align="center">
					<fieldset>
						<logic:equal name="financial_voucherForm" property="clsflag" value="1">
							<legend><bean:message key="kq.item.count"/></legend>
						</logic:equal>
						<logic:equal name="financial_voucherForm" property="clsflag" value="2">
							<legend><bean:message key="gz.formula.crond.formula.expression"/></legend>
						</logic:equal>
						<logic:equal name="financial_voucherForm" property="clsflag" value="3">
							<legend>本币计算公式</legend>
						</logic:equal>
						<table border="0" align="center" width="500px;"> 
							<tr>
								<td rowspan="3" valign="top">
									<logic:equal name="financial_voucherForm" property="clsflag" value="1">
										<html:textarea name="financial_voucherForm" property="c_itemsql" cols="60" rows="10" onclick="this.pos=document.selection.createRange();" styleId="shry" style="width:445px;">
										</html:textarea>
									</logic:equal>
									<logic:equal name="financial_voucherForm" property="clsflag" value="2">
										<html:textarea name="financial_voucherForm" property="c_where" cols="60" rows="10" onclick="this.pos=document.selection.createRange();" styleId="shry" style="width:445px;" >
										</html:textarea>
									</logic:equal>
									<logic:equal name="financial_voucherForm" property="clsflag" value="3">
										<html:textarea name="financial_voucherForm" property="c_itemsql" cols="60" rows="10" onclick="this.pos=document.selection.createRange();" styleId="shry" style="width:445px;">
										</html:textarea>
									</logic:equal>
								</td>
								<td valign="top" style="padding-top: 2px;">
									<input type="button" name="Submit" value="<bean:message key='button.sys.warn.guide'/>" class="mybutton" onClick="function_Wizard();"><br><br>
									<input type="button" name="bto" value="<bean:message key='kq.formula.check'/>" class="mybutton" onClick="checkExpr()"><br><br>
									<input type="button" name="save" value="<bean:message key='button.ok'/>" class="mybutton" onClick="saveCheckExpr()"><br><br>
									<input type="button" name="Submit" value="<bean:message key='button.return'/>"  class="mybutton" onClick="cancel()">
								</td>
							</tr>
							<tr>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr>
								<td>
									
								</td>
							</tr>
							<tr>
								<td>
									<fieldset>
										<legend><bean:message key="gz.acount.reference.project"/></legend>
										<table border="0">
										<tr>
												<td>
													<bean:message key="sys.res.gzset"/>
													<hrms:optioncollection name="financial_voucherForm" property="salarySetList" collection="list"/>
													<html:select name="financial_voucherForm" property="salaryid" size="1" onchange="changeSalarySet();" style="width:200">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
													</html:select>
													
												</td>
												
											</tr>
											<tr>
												<td>
													<bean:message key="static.target"/>
													<hrms:optioncollection name="financial_voucherForm" property="salaryItemList" collection="list"/>
													<html:select name="financial_voucherForm" property="itemdesc" size="1" onchange="changeFieldItem(this.options[this.selectedIndex].innerText);" style="width:200">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
													</html:select>
													
												</td>
												
											</tr>
											
											<tr>
												<td>
													<div id="cid">
													&nbsp;&nbsp;&nbsp;<bean:message key="gz.acount.code.project"/>
													<html:select name="financial_voucherForm" property="codeitemId" size="1" onchange="changeCodeItem();" style="width:200">
														<option></option>
													</html:select>
													</div>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
								<td>
									
								</td>
							</tr>
							<tr>
								<td rowspan="3" align="center">
									<fieldset>
										<legend>计算符号</legend>
										<table width="100%" border="0">
                                        <tr>
                                        <td><input type="button" name="Submit4" value="0" class="smallbutton" onclick="symbol(0)" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit42" value="1" class="smallbutton" onclick="symbol(1)" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit43" value="2" class="smallbutton" onclick="symbol(2)" style="height:22px;width:100%;font-size:8pt"></td>
                                        <td><input type="button" name="Submit44" value="3" class="smallbutton" onclick="symbol(3)" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit45" value="4" class="smallbutton" onclick="symbol(4)" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit46" value="(" class="smallbutton" onclick="symbol('(')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit477" value="=" class="smallbutton" onclick="symbol('=')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4763" value="&gt;=" class="smallbutton" onclick="symbol('&gt;=')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4753" value="<bean:message key='kq.formula.not'/>" class="smallbutton" onclick="symbol('<bean:message key='kq.formula.not'/>')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4764" value="~" class="smallbutton" onclick="symbol('~')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit46" value="<bean:message key='kq.wizard.thing'/>" class="smallbutton" onclick="symbol('<bean:message key='kq.wizard.thing'/>')" style="height:22px;width:100%;font-size:9pt"></td>
                                        </tr>
                                        <tr>
                                        <td><input type="button" name="Submit47" value="5" class="smallbutton" onclick="symbol(5)" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit472" value="6" class="smallbutton" onclick="symbol(6)" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit473" value="7" class="smallbutton" onclick="symbol(7)" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit474" value="8" class="smallbutton" onclick="symbol(8)" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit475" value="9" class="smallbutton" onclick="symbol(9)" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit476" value=")" class="smallbutton" onclick="symbol(')')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4722" value="&gt;" class="smallbutton" onclick="symbol('&gt;')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4754" value="&lt;=" class="smallbutton" onclick="symbol('&lt;=')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4752" value="<bean:message key='kq.formula.even'/>" class="smallbutton" onclick="symbol('<bean:message key='kq.formula.even'/>')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit46" value="<bean:message key='kq.formula.if'/>" class="smallbutton" onclick="symbol('<bean:message key='kq.formula.if'/>')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4764" value="<bean:message key='kq.formula.fou'/>" class="smallbutton" onclick="symbol('<bean:message key='kq.formula.fou'/>')" style="height:22px;width:100%;font-size:9pt"></td>
                                        </tr>
                                        <tr>
                                        <td><input type="button" name="Submit47" value="+" class="smallbutton" onclick="symbol('+')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit472" value="-" class="smallbutton" onclick="symbol('-')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit473" value="*" class="smallbutton" onclick="symbol('*')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit474" value="/" class="smallbutton" onclick="symbol('/')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit475" value="\" class="smallbutton" onclick="symbol('\\')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit476" value="%" class="smallbutton" onclick="symbol('%')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4732" value="&lt;"  class="smallbutton" onclick="symbol('&lt;')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4742" value="&lt;&gt;" class="smallbutton" onclick="symbol('&lt;&gt;')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4762" value="<bean:message key='kq.formula.or'/>" class="smallbutton" onclick="symbol('<bean:message key='kq.formula.or'/>')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit46" value="<bean:message key='kq.formula.then'/>" class="smallbutton" onclick="symbol('<bean:message key='kq.formula.then'/>')" style="height:22px;width:100%;font-size:9pt"></td>
                                        <td><input type="button" name="Submit4764" value="<bean:message key='kq.formula.end'/>" class="smallbutton" onclick="symbol('<bean:message key='kq.formula.end'/>')" style="height:22px;width:100%;font-size:9pt"></td>
                                        </tr>
                                        </table>
									</fieldset>
								</td>
								<td>
									
								</td>
							</tr>
							<tr>
								<td>
									
								</td>
							</tr>
							<tr>
								<td>
									&nbsp;
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr>
		</table>
	</html:form>
</body>
<script language="javaScript">
  <%
	  VoucherForm voucherForm=(VoucherForm)session.getAttribute("financial_voucherForm");
	  ArrayList list = voucherForm.getSalarySetList();
	  String privflag = (String)request.getParameter("privflag");
	  String salaryitem = "";
	  for(int i =0;i<list.size();i++){
		  CommonData data = (CommonData)list.get(i);
		  String value = data.getDataValue();
		  if(value.length()>0)
		  	salaryitem = salaryitem+","+value;
	  }
	  if(salaryitem.length()>0)//update by xiegh on 20170927 bug31779
	 	 salaryitem  = salaryitem.substring(1);
  %>
  	salaryStr ='<%=salaryitem%>';
  	<%if("3".equals(privflag)){%>
		document.getElementsByName("save")[0].disabled=true;
	<%}%>
	Element.hide('cid');
</script>
