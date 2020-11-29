<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" href="/css/hcm/themes/default/content.css" type="text/css">
<hrms:themes />
<%
	String itemid = request.getParameter("itemid");
%>
<script type="text/javascript" src="/module/utils/js/template.js"></script>
<script language="javascript">
function symbol(cal){
	if(document.getElementsByName("formula")[0].pos!=null){
		if(document.getElementsByName("formula")[0].pos.text.length>0){
			document.getElementsByName("formula")[0].pos.text+=cal;
		}else{
			document.getElementsByName("formula")[0].pos.text=cal;
		}
	}else{
		document.getElementsByName("formula")[0].value +=cal;
	}
}

function function_Wizard(){
	var checkflag = '${complexQueryForm.checkflag}';
    var thecodeurl ="";
    if(checkflag=="1"){
    	var itemid = document.getElementsByName("itemid")[0].value;
    	thecodeurl="/org/autostatic/mainp/function_Wizard.do?b_query=link&infor=6&itemid="+itemid; 
    }else
    	thecodeurl="/org/autostatic/mainp/function_Wizard.do?b_query=link&salaryid=all"; 
    /*
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	symbol(return_vo);
  	}else{
  		return ;
  	}
  	*/
  	//如果不这样弄，会影响薪资模块
  	if(window.showModalDialog) {
  		var return_vo= window.showModalDialog(thecodeurl, "", 
        	"dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
		if(return_vo!=null){
		 	symbol(return_vo);
		}else{
			return ;
		}
  	}else {
	  	parent.Ext.create('Ext.window.Window',{
			id:'function_Wizard',
			title:'向导',
			width:420,
			height:420,
			resizable:false,
			modal:true,
			autoScroll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
			renderTo:parent.Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo!=null){
	  	 				symbol(this.return_vo);
	  				}
				}
			}
		});
  	}
}
function checkExpr(){
	var formula = document.complexQueryForm.formula.value;
	//if(formula == ""){
  	//	alert("<bean:message key='gz.acount.formula.not.null'/>");
  	//	return ;
  	//}else{
  		var checkflag = '${complexQueryForm.checkflag}';
		var itemid = document.getElementsByName("itemid")[0].value;
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",getEncodeStr(formula));
	    hashvo.setValue("itemid",itemid);
	    if(checkflag != '1')//checkflag在formulaTrans中可看出，当flag!=1的时候，会查询salaryset，其他不会
            hashvo.setValue("itemsetid","gz_amount");//薪资总额计算公式标识 20170308 dengcan
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020060020'},hashvo);	
 // 	}
  	
}
function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
  	info = getDecodeStr(info);
	if(info=="ok"){
		b = true;
		if(parent.Ext && parent.Ext.getCmp('ps_parameter')) {
			Ext.showAlert("<bean:message key='gz.acount.formula.success'/>");
		}else {//薪资一些模块没有兼容性改造，这里还是用alert
			alert("<bean:message key='gz.acount.formula.success'/>");
		}
	}else{
		if(parent.Ext && parent.Ext.getCmp('ps_parameter')) {
			Ext.showAlert(info);
		}else {
			alert(info);
		}
	}
}
  
function changeSalarySet()
{
     var salaryid=complexQueryForm.salaryid.value;
     var hashvo=new ParameterSet();
     hashvo.setValue("salaryid",salaryid);
   	 var In_paramters="flag=1"; 	
	 var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:changeSalarySetOk,functionId:'3020080008'},hashvo);
}
function changeSalarySetOk(outparameters)
{
   Element.hide('cid');
   var fielditemlist=outparameters.getValue("itemList");
   AjaxBind.bind(complexQueryForm.fieldItemId,fielditemlist);
}
function changeFieldSet(){
	var v = complexQueryForm.fieldSetId.value;
  	var hashvo=new ParameterSet();
    hashvo.setValue("setid",v);
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'1010020308'},hashvo);					
}
  
function resultChangeFieldSet(outparamters){
  	Element.hide('cid');
  	var fielditemlist=outparamters.getValue("itemList");
	AjaxBind.bind(complexQueryForm.fieldItemId,fielditemlist);
	
}
  
function changeFieldItem(){
  	 var m = document.complexQueryForm.fieldItemId.value;
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
  		AjaxBind.bind(complexQueryForm.codeItemId,info);
  	}
}
  
function changeCodeItem(){
  	var m = document.complexQueryForm.codeItemId.value;
  	symbol('"'+m+'"');
}

function saveCheckExpr(){
  	var formula = document.complexQueryForm.formula.value;
	//if(formula == ""){
  	//	alert("<bean:message key='gz.acount.formula.not.null'/>");
  	//	return ;
  //	}else{
  		var itemid = document.getElementsByName("itemid")[0].value;
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",getEncodeStr(formula));
	    hashvo.setValue("itemid",itemid);
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultSaveCheckExpr,functionId:'3020060020'},hashvo);					
  	
  //	}
}
function resultSaveCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
  	info = getDecodeStr(info);
  	var m = document.complexQueryForm.formula.value;
  	if(info=="ok"){
  		 if(parent.Ext && parent.Ext.getCmp('ps_parameter')){
  			var win = parent.Ext.getCmp('ps_parameter');
  			win.obj = m;
  			win.close();
  		 }else{
			returnValue=m;
			window.close();
  		 }
	}else{
		if(parent.Ext && parent.Ext.getCmp('ps_parameter')) {
			Ext.showAlert(info);
		}else {
			alert(info);
		}
	}
}

function cancel(){
  if(parent.Ext && parent.Ext.getCmp('ps_parameter')){
  	parent.Ext.getCmp('ps_parameter').close();
  	return;
  }
  window.close();
}
</script>
<body>
	<form name="complexQueryForm" method="post" action="">
		<table border="0" align="center" style="margin-left:-3px;margin-top:-5">
			<tr>
				<td align="center">
					<fieldset style="border-width: 1px;">
						<legend><bean:message key="kq.item.count"/></legend>
						<table border="0" align="center">
							<tr>
								<td rowspan="3" valign="top">
									<html:textarea name="complexQueryForm" property="formula" cols="77" rows="10" 
									style="width:485px; width:470px!important; border-width: 1px;"
									onclick="this.pos=document.selection.createRange();" styleId="shry" >
									</html:textarea>
								</td>
								<td valign="top">
									<input type="button" name="Submit" value="<bean:message key='button.sys.warn.guide'/>" class="mybutton" onClick="function_Wizard();"><br><br>
									<input type="button" name="bto" value="<bean:message key='kq.formula.check'/>" class="mybutton" onClick="checkExpr()"><br><br>
									<input type="button" name="Submit" value="<bean:message key='button.ok'/>" class="mybutton" onClick="saveCheckExpr()"><br><br>
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
									<fieldset style="border-width: 1px;">
										<legend><bean:message key="gz.acount.reference.project"/></legend>
										<table border="0">
										<logic:equal value="1" name="complexQueryForm" property="type">
										<tr>
												<td>
													<bean:message key="sys.res.gzset"/>
													<hrms:optioncollection name="complexQueryForm" property="salarysetlist" collection="list"/>
													<html:select name="complexQueryForm" property="salaryid" size="1" onchange="changeSalarySet();" style="width:200">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
													</html:select>
													
												</td>
												
											</tr>
											</logic:equal>
											<tr>
												<td>
													<bean:message key="static.target"/>
													<hrms:optioncollection name="complexQueryForm" property="fieldItemList" collection="list"/>
													<html:select name="complexQueryForm" property="fieldItemId" size="1" onchange="changeFieldItem();" style="width:200">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
													</html:select>
													
												</td>
												
											</tr>
											
											<tr>
												<td>
													<div id="cid">
													&nbsp;&nbsp;&nbsp;<bean:message key="gz.acount.code.project"/>
													<html:select name="complexQueryForm" property="codeItemId" size="1" onchange="changeCodeItem();" style="width:200">
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
									<fieldset style="border-width: 1px;">
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

							</tr>

							<tr>
								<td>
									<input type="hidden" name="itemid" value="<%=itemid%>">
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr>



		</table>
	</form>
</body>
<script language="javaScript">
	Element.hide('cid');
</script>
