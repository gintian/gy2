<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getManagePrivCodeValue();  
%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="../../inform.js"></script>
<script language="JavaScript">
function IsDigit(obj) {
	var decimalwidth = document.getElementById("decimalwidth").value;
	if(decimalwidth=="0"){
		if((event.keyCode >= 48) && (event.keyCode <= 57)){
			return true;
		}else{
			return false;
		}
	}else{
		if((event.keyCode >= 45) && (event.keyCode <= 57)){
			var values=obj.value;
			if((event.keyCode == 46) && (values.indexOf(".")!=-1))
				return false;
			if((event.keyCode == 45) && (values.indexOf("-")!=-1))
				return false;
			if((event.keyCode == 46) && (values.length==0))
				return false;	
		}else{
			return false;
		}
	}
}
</script>
<style> 
.sel { 
} 
.sel OPTGROUP{ 
	font-size: 12px; 
	font-style: normal; 
	font-weight: normal; 
	font-variant: normal; 
	background-color: #CCCCCC; 
} 
.sel option { 
	padding-left:0px; 
} 
</style>

<html:form action="/general/inform/emp/batch/alertind">
	<input type="hidden" name="codesetid">
	<input type="hidden" name="itemtype">
	<input type="hidden" name="decimalwidth">
	<center>
		<table width="390px" border="0" align="center">
			<tr>
				<td width="100%" height="193">
					<fieldset style="width:100%;">
						<legend>
							<bean:message key='infor.menu.batupdate_s' />
						</legend>
						<table width="100%" border="0" align="center">
							<tr>
								<td height="30" align="center">
									<table width="100%" border="0">
										<tr>
											<td>
												&nbsp;&nbsp;
												<bean:message key='field.label' />
											</td>
											<td>
												<hrms:optioncollection name="indBatchHandForm"
													property="indlist" collection="list1" />
												<html:select name="indBatchHandForm" property="itemid"
													onchange="checkType();change();" style="width:155px">
													<html:options collection="list1" property="dataValue"
														labelProperty="dataName" />
												</html:select>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td height="30" align="center">
									<span id="reference" style="display:none">
										<table width="100%" border="0">
											<tr>
												<td>
													<input type="radio" name="valuebutton"
														onclick="checkView(4);" value="3" />
													<bean:message key='infor.menu.reference.item' />
												</td>
												<td>
													<html:select name="indBatchHandForm" property="refvalue"
														style="width:155px">
														<html:optionsCollection property="refvaluelist"
															value="dataValue" label="dataName" />
													</html:select>
												</td>
											</tr>
										</table> </span>
								</td>
							</tr>
							<tr>
								<td height="30" align="center">
									<span id="addvaluename" style="display:none">
										<table width="100%" border="0">
											<tr>
												<td>
													<input type="radio" name="valuebutton"
														onclick="checkView(1);" value="1" />
													<bean:message key='infor.menu.add.value' />
												</td>
												<td>
													<div id="addvaluetext">
														<input type="text" class="textColorWrite" name="addvalue" maxlength="10"
															onkeypress="event.returnValue=IsDigit(this);"
															style="ime-mode:disabled">
													</div>
												</td>
											</tr>
										</table> </span>
								</td>
							</tr>
							<tr>
								<td height="30" align="center">
									<table width="100%" border="0" align="center">
										<tr>
											<td>
												<input type="radio" name="valuebutton"
													onclick="checkView(2);" value="2" checked />
												<bean:message key='infor.menu.alternative.value' />
											</td>
											<td>
												<div id="repvaluetext">
													<input type="text" class="textColorWrite" name="repvalue">
												</div>
												<div id="repvalueNtext" style="display:none">
													<input type="text" class="textColorWrite" name="repvaluen" maxlength="10"
														onkeypress="event.returnValue=IsDigit(this);"
														style="ime-mode:disabled">
												</div>
												<div id="repvaluecode" style="display:none">
													<input type="hidden" name="codeid.value">
													<input type="text" class="textColorWrite" name="codeid.hzvalue"
														style="width:150px;" readOnly>
													<span id='codeImg'></span>
												</div>
												<div id="repvaluetime" style="display:none">
													<input type="text" class="textColorWrite" name="time.value" extra="editor"
														onblur="timeCheck(this);"
														style="width:150px;font-size:10pt;text-align:left"
														dropDown="dropDownDate">
												</div>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td height="30" align="center">
									<span id="reducevaluename" style="display:none">
										<table width="100%" border="0">
											<tr>
												<td>
													<input type="radio" name="valuebutton"
														onclick="checkView(3);" value="0" />
													<bean:message key='infor.menu.reduce.value' />
												</td>
												<td>
													<div id="reducevlauetext" style="display:none">
														<input type="text" class="textColorWrite" name="reducevlaue" maxlength="10"
															onkeypress="event.returnValue=IsDigit(this);"
															style="ime-mode:disabled">
													</div>
												</td>
											</tr>
										</table> </span>
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr>
		</table>

		<table width="100%" border="0">
			<tr>
				<td height="40" width="100">
					<input type="radio" name="selectid" value="0" onclick="selectUpdate('0');" checked/>
					修改选择记录
				</td>
				<td width="100">
					<input type="radio" name="selectid" value="1" onclick="selectUpdate('1');"/>
					修改所有
				</td>
				<td  width="100">
					<span id="hisview" style="display:none">
					<logic:equal name="indBatchHandForm" property="history" value="1">
					<input type="checkbox" name="history" value="1" />
					<bean:message key='org.autostatic.mainp.update.history' />
					</logic:equal>
					</span>
				</td>
			</tr>

		</table>

					<table width="100%" border="0">
						<tr>
							<td align="center" valign="bottom">
								<input type="button" name="Submit"
									value="<bean:message key='button.ok'/>"
									onclick='saveAlert("${indBatchHandForm.setname}","${indBatchHandForm.a_code}","${indBatchHandForm.viewsearch}","${indBatchHandForm.dbname}","${indBatchHandForm.infor}","${indBatchHandForm.history}","${indBatchHandForm.inforflag}");'
									Class="mybutton">
								<input type="button" name="Submit2"
									value="<bean:message key='button.close'/>"
									onclick="window.close();" Class="mybutton">
							</td>
						</tr>
					</table>
			
	</center>
	<html:hidden name="indBatchHandForm" property="count" />
	<html:hidden name="indBatchHandForm" property="countall" />
	<html:hidden name="indBatchHandForm" property="strid"/>
	<html:hidden name="indBatchHandForm" property="secount"/>
</html:form>
<script language="javascript">

function selectUpdate(selectid){
	if(selectid=="0")
		viewHide('hisview');
	else
		viewToggle('hisview');
}

function change(){
    var itemid=document.getElementById("itemid").value;
    var arr = itemid.split(":");
    if(arr.length!=3){
    	return;
    }
	var in_paramters="itemid="+arr[0];
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'1010092005'});
}
function showFieldList(outparamters){
	var codesetid=outparamters.getValue("codesetid");
	var itemtype=outparamters.getValue("itemtype");
	var decimalwidth=outparamters.getValue("decimalwidth");
	var refvaluelist=outparamters.getValue("refvaluelist");
	AjaxBind.bind(indBatchHandForm.refvalue,refvaluelist);

	if(itemtype=='D'){
		viewHide('repvaluetext');
		viewHide('repvaluecode');
		viewHide('repvalueNtext');
		viewToggle('repvaluetime');
	}else if(itemtype=='N'){
		viewToggle('repvalueNtext');
		viewHide('repvaluecode');
		viewHide('repvaluetime');
		viewHide('repvaluetext');
	}else if(itemtype=='M'){
		viewToggle('repvaluetext');
		viewToggle('reference');
		viewHide('repvalueNtext');
		viewHide('repvaluecode');
		viewHide('repvaluetime');
	}else{
		if(codesetid!=null&&codesetid.length>0){
			viewHide('repvaluetext');
			viewHide('repvalueNtext');
			viewToggle('repvaluecode');
			var a='';
			if("UM,UN,@K".indexOf(codesetid)==-1)
				 a="<img id=\"b\" src=\"/images/code.gif\" align=\"absmiddle\" onclick='openCodeDialog(\"codesetid\",\"codeid.hzvalue\");'/>&nbsp;";

			else
				 a="<img id=\"a\" src=\"/images/code.gif\" align=\"absmiddle\" onclick='openInputCodeDialogOrgInputPos($F(\"codesetid\"),\"codeid.hzvalue\",\"<%=manager%>\",1);'/>&nbsp;";
			codeImg.innerHTML=a;
			viewHide('repvaluetime');
		}else{
			viewToggle('repvaluetext');
			viewHide('repvalueNtext');
			viewHide('repvaluecode');
			viewHide('repvaluetime');
		}
	}
	document.getElementById("codesetid").value=codesetid;
	document.getElementById("itemtype").value=itemtype;
	document.getElementById("decimalwidth").value=decimalwidth;
	var valuebutton = document.all.valuebutton;
	valuebutton[2].checked=true;
}
checkType();
change();
</script>
