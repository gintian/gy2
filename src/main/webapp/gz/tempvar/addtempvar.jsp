<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function savetemp(flag){
	var hashvo=new ParameterSet();
	var tempvarname = document.getElementById("tempvarname").value;
	if(tempvarname.length<1){
		alert("<bean:message key='gz.tempvar.input.tempname'/>");
		return;
	}
	if(/^\d+$/.test(tempvarname)){
		alert("临时变量名不允许全为数字！");
		return;
	}
	hashvo.setValue("tempvarname",tempvarname);
	
	var ntype = document.getElementById("ntype").value;
	hashvo.setValue("ntype",ntype);
	
	var fidlen = document.getElementById("fidlen").value;
	hashvo.setValue("fidlen",fidlen);
	
	var fiddec = document.getElementById("fiddec").value;
	hashvo.setValue("fiddec",fiddec);
	
	if(ntype=="1"){
		if(fidlen>38){
			alert("数值型总长度不能超过38位！");
			return;
		}
		if(fiddec*1>fidlen*1){
			alert("数值型位数不可大于其总长度！");
			return;
		}
	}
	if(fidlen==null||fidlen==undefined||fidlen==""){
		alert(INPUT_LENGTH+"!");
		return;
	}
	if(ntype=="1"&&(fiddec==null||fiddec==undefined||fiddec=="")){
		alert(INPUT_MEDIAN+"!");
		return;
	}
	
	if(parseInt(fidlen)<1){
		alert(INPUT_LENGTH_NONE+"!");
		return;
	}
	if(ntype=='1'&&(parseInt(fidlen)<parseInt(fiddec))){
		alert(LENGTH_NOT_LESS_MEDIAN+"!");
		return;
	}

	var codesetid = document.getElementById("codesetid").value;
	if(ntype=='4'){
		if(codesetid==null||codesetid.length<1){
			alert(SELECT_RELETED_CODEITEM+"!");
			return;
		}
	
		var arr = codesetid.split(":");
		if(arr.length!=2){
			return;
		}else{
			codesetid = arr[0];
		}
	}
	hashvo.setValue("codesetid",codesetid);
	
	var type = document.getElementById("type").value;
	hashvo.setValue("type",type);
	
	var nflag ="${tempvarForm.nflag}";
	hashvo.setValue("nflag",nflag);
	
	var cstate = document.getElementById("cstate").value;
	hashvo.setValue("cstate",cstate);
	
	var nid = document.getElementById("nid").value;
	hashvo.setValue("nid",nid);
	
	hashvo.setValue("flag",flag);
	var showflag = document.getElementById("showflag").value;
	hashvo.setValue("showflag",showflag);
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:showFieldList,functionId:'3020050003'},hashvo);
}
function showFieldList(outparamters){
		var base=outparamters.getValue("base");
		var showflag=outparamters.getValue("showflag");
		var nid=outparamters.getValue("nid");
		var ntype=outparamters.getValue("ntype");
		var setobj = new Object();
		setobj.showflag = showflag;
		setobj.nid = nid;
		setobj.ntype = ntype;
		if(base=='ok'){
			window.returnValue=setobj;
			window.close();
		}else if(base=='continue'){
			continueadd();
		}else if(base=='insert'){
			alert("<bean:message key='gz.tempvar.insert.db.failure'/>");
		}else if(base=='update'){
			alert("<bean:message key='gz.tempvar.alert.db.failure'/>");
		}else{
			alert(base);
		}
}
function changeCode(obj){
	var len=obj.value;
	var arr = len.split(":");
	if(arr.length==2)
		document.getElementById("fidlen").value=arr[1];
}
function change(){
	var selecttemp = document.getElementById("selecttemp").value;
	document.getElementById("ntype").value=selecttemp;
	document.getElementById("codesetid").value='';
	document.getElementById("fiddec").value='';
	if(selecttemp==1){
		hides("viewcode");
		hides("viewnull");
		toggles("viewNum");
		document.getElementById("fidlen").value=10;
		document.getElementById("fiddec").value=2;
		document.getElementById("fidlen").readOnly=false;
	}else if(selecttemp==2){
		hides("viewcode");
		hides("viewNum");
		toggles("viewnull");
		document.getElementById("fidlen").value=30;
		document.getElementById("fidlen").readOnly=false;
	}else if(selecttemp==3){
		hides("viewcode");
		toggles("viewnull");
		hides("viewNum");
		document.getElementById("fidlen").value=10
		document.getElementById("fidlen").readOnly=true;
	}else if(selecttemp==4){
		toggles("viewcode");
		hides("viewnull");
		hides("viewNum");
		document.getElementById("fidlen").value=10;
		document.getElementById("fiddec").value=0;
	}else{
		hides("viewcode");
		toggles("viewNum");
		hides("viewnull");
		document.getElementById("fidlen").value='';
		document.getElementById("fidlen").readOnly=false;
	}
}
function gzCodeOption(codeset,fidlen) {
    var thecodeurl,target_name,hidden_name,hiddenobj,fidlenobj;

    var oldInputs=document.getElementsByName(codeset);
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name+"id"; 
       
    var hiddenInputs=document.getElementsByName(hidden_name);
    
    if(hiddenInputs!=null){
    	hiddenobj=hiddenInputs[0];
    }
	
	var hiddentextarea = document.getElementsByName(fidlen);
	if(hiddentextarea!=null){
    	fidlenobj=hiddentextarea[0];
    }
	
    var theArr=new Array(oldobj,hiddenobj,fidlenobj); 
    thecodeurl="/gz/tempvar/viewcode.jsp"; 
    var popwin= window.showModelessDialog(thecodeurl,theArr, 
        "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:yes");
}
function returnViewtemp(){
	var cstate = document.getElementById("cstate").value;
	var type = document.getElementById("type").value;
	document.location.href="/gz/tempvar/viewtempvar.do?b_query=link&state="+cstate+"&type="+type;
} 
function continueadd(){
	var cstate = document.getElementById("cstate").value;
	var type = document.getElementById("type").value;
	var nflag = document.getElementById("nflag").value;
	var showflag=document.getElementById("showflag").value;
	document.tempvarForm.action="/gz/tempvar/addtempvar.do?b_query=link&type="+type+"&cstate="+cstate+"&flag=conadd&nflag="+nflag+"&showflag="+showflag;
	document.tempvarForm.submit();
}
function editChange(){
	var selecttemp = "${tempvarForm.ntype}";
	document.getElementById("selecttemp").value=selecttemp;
	if(selecttemp==1){
		hides("viewcode");
		hides("viewnull"); 
		toggles("viewNum");
		document.getElementById("fidlen").readOnly=false;
	}else if(selecttemp==2){
		hides("viewcode");
		hides("viewNum");
		toggles("viewnull");
		document.getElementById("fidlen").readOnly=false;
	}else if(selecttemp==3){
		hides("viewcode");
		toggles("viewnull");
		hides("viewNum");
		document.getElementById("fidlen").value=10
		document.getElementById("fidlen").readOnly=true;
	}else if(selecttemp==4){
		toggles("viewcode");
		hides("viewnull");
		hides("viewNum");
		document.getElementById("fidlen").readOnly=true;
	}else{
		hides("viewcode");
		toggles("viewNum");
		hides("viewnull");
		document.getElementById("fidlen").readOnly=false;
	}

	if(isIE6() ){
			var table1 = document.getElementById("table1");
			table1.style.cssText="margin-top:0px;margin-left=-3px;";
	}
}
function closeWin(){
	var checkclose = document.getElementById("checkclose").value;
	if(checkclose=="close"){
		window.returnValue=document.getElementById("showflag").value;
		window.close();
	}else{
		window.close();
	}
}
function isNum(i_value){
    re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}

function checkNuN(obj){
 	if(!isNum(obj.value)){
 		obj.value='';
 		return;
 	}
}
function IsDigitStr() {
	if(event.keyCode==34||event.keyCode==39){
		return false;
	}
}	
function checkData(obj) {
	var CheckData = /<|>|'|;|"|'/;
	if ( CheckData.test(obj.value) ) {
        alert("用户名包含非法字符，请不要使用特殊字符！");
        obj.value="";   
   	}
}   	  		 		
</script>
<base target="_self"> 
<html:form action="/gz/tempvar/addtempvar">
<table id="table1" width="390px;" height="300" border="0" align="center" style="margin-left:-3px;margin-top:-3px;">

		<html:hidden name="tempvarForm" property="type"/>
		<html:hidden name="tempvarForm" property="cstate"/>
		<html:hidden name="tempvarForm" property="nid"/>
		<html:hidden name="tempvarForm" property="checkclose"/>
		<html:hidden name="tempvarForm" property="nflag"/>
		<html:hidden name="tempvarForm" property="showflag"/>

  <tr> 
    <td>
    	<table id="table2" width="100%"  border="0" align="center" style="margin-left:-3px;margin-top:-5px;">
    	<tr>
    		<td>
    			<fieldset style="width:100%;height:270">
    			<legend><bean:message key="kq.wizard.variable"/></legend>
    			<table width="100%"  border="0" align="center">
    				<tr>
    					<td width="70" align="right">&nbsp;</td>
    					<td>&nbsp;</td>
    				</tr>
    				<tr> 
    					<td width="100" align="right"><bean:message key="gz.tempvar.tempname"/>&nbsp;&nbsp;</td>
    					<td><html:text name="tempvarForm" property="tempvarname" onblur="checkData(this);" maxlength="40" onkeypress="event.returnValue=IsDigitStr();" styleClass="inputtext"/></td>
    				</tr>
    				<tr>
    					<td align="right"><bean:message key="label.org.type_org"/>&nbsp;&nbsp;</td>
    					<td>
    						<select name="selecttemp" onchange="change();">
    							<option value="1"><bean:message key="kq.formula.countt"/></option>
    							<option value="2"><bean:message key="kq.formula.charat"/></option>
    							<option value="3"><bean:message key="gz.tempvar.date"/></option>
    							<option value="4"><bean:message key="gz.tempvar.code"/></option>
    						</select>
    						<html:hidden name="tempvarForm" property="ntype"/>
    					</td>
    				</tr>
    				<tr>
    					<td align="right"><bean:message key="report.parse.len"/>&nbsp;&nbsp;</td>
    					<td>
    						<logic:equal name="tempvarForm" property="tempvarname" value="">
    							<html:text name="tempvarForm" property="fidlen" value="10" maxlength="4" onkeyup="checkNuN(this);" styleClass="inputtext"/>
    						</logic:equal>
    						<logic:notEqual name="tempvarForm" property="tempvarname" value="">
    							<html:text name="tempvarForm" property="fidlen" maxlength="4" onkeyup="checkNuN(this);" styleClass="inputtext"/>
    						</logic:notEqual>
    						
    						
    					</td>
    				</tr>
    			</table>
    			<span id="viewNum">
    			<table width="100%"  border="0" align="center">
    				<tr>
    					<td width="100" align="right"><bean:message key="gz.tempvar.median"/>&nbsp;&nbsp;</td>
    					<td>
    						<logic:equal name="tempvarForm" property="tempvarname" value="">
    							<html:text name="tempvarForm" property="fiddec" maxlength="2" onkeyup="checkNuN(this);" styleClass="inputtext"/>
    						</logic:equal>
    						<logic:notEqual name="tempvarForm" property="tempvarname" value="">
    							<html:text name="tempvarForm" property="fiddec" maxlength="2" onkeyup="checkNuN(this);" styleClass="inputtext"/>
    						</logic:notEqual>
    					</td>
    				</tr>
    			</table>
    			</span>
    			<span id="viewcode">
    			<table width="100%"  border="0" align="center">
    				<tr>
    					<td width="27%" align="right"><bean:message key="gz.tempvar.code.object"/>&nbsp;&nbsp;</td>
    					<td>
         					<html:select name="tempvarForm" property="codesetid" onchange="changeCode(this);" style="width:150;font-size:9pt" >
			 					<html:optionsCollection property="codelist" value="dataValue" label="dataName" />
							</html:select>
    					</td>
    				</tr>
    			</table>
    			</span>
    			<span id="viewnull">
    			<table width="100%"  border="0" align="center">
    				<tr>
    					<td height="25">&nbsp;</td>
    					<td>&nbsp;</td>
    				</tr>
    			</table>
    			</span>
    			</fieldset>
    		</td>
    	</tr>
		<tr>
			<td>
		    	<table width="100%"  border="0" align="center">

    				<tr>
    					<td align="center">
    						<input type="button" value="<bean:message key='button.save'/>" onclick="savetemp(0);" Class="mybutton">
    						<logic:equal name="tempvarForm" property="tempvarname" value="">
    							<input type="button" value="<bean:message key='button.save'/>&<bean:message key='edit_report.continue'/>" onclick="savetemp(1);" Class="mybutton">
    						</logic:equal>
    						<input type="button" value="<bean:message key='button.close'/>" onclick="closeWin();" Class="mybutton">
    					</td>
    				</tr>
    			</table>
    		</td>
    	</tr>
    	</table>
    </td>
  </tr>
</table>
<script language="javascript">
editChange(); 
window.onbeforeunload = function() { 
	var n = window.event.screenX - window.screenLeft; 
	var b = n > document.documentElement.scrollWidth-20; 
	if(b && window.event.clientY < 0 || window.event.altKey) {
		var checkclose = document.getElementById("checkclose").value;
		if(checkclose=="close"){
			window.returnValue="111";
			window.close();
		}else{
			window.close();
		} 
	}
}   		 		
</script>
</html:form>
