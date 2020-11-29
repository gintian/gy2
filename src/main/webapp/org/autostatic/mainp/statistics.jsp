<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<style type="text/css">
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 13px; 
 PADDING-BOTTOM: 13px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 2px; 
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn3 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 PADDING-TOP: 0px; 
 PADDING-BOTTOM: 0px;
 line-height:18px;
 BORDER-BOTTOM: #C0C0C0 1px solid;
  border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
</style>
<script language="javascript">
var posArr="";
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
function change(){
    var fieldname=document.getElementById("fieldname").value;
	var in_paramters="fieldname=2-"+fieldname;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'1602010219'});
}
function showFieldList(outparamters){
		var usedlist=outparamters.getValue("usedlist");
		AjaxBind.bind(projectForm.left_fields,usedlist);
		var codesetid_arr= document.getElementsByName("left_fields");
		if(codesetid_arr!=null){
			var codesetid_arr_vo = codesetid_arr[0];
			if(codesetid_arr_vo.options.length>0){
				codesetid_arr_vo.options[i].selected = true;
				selectmethod();
			}else{
				hides("wizard_method");	
				toggles("wizard_stat");
				hides("saveformula_brilliant");
				toggles("saveformula_dark");	
				document.getElementById("formula").value="";
				document.getElementById("expresion").value="";
			}
		}else{
			hides("wizard_method");	
			hides("saveformula_brilliant");	
			document.getElementById("formula").value="";
			document.getElementById("expresion").value="";
		}
}
function addproject(){
    var fieldsetid = document.getElementById("fieldname").value;
    var thecodeurl ="/org/autostatic/mainp/addProject.do?b_query=link&fieldsetid="+fieldsetid+"&type=2"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:350px; dialogHeight:230px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null&&return_vo.length>1){ 
		var fieldname = document.getElementById("fieldname").value;
		var fileitem ="fieldname="+fieldname+"-"+return_vo+"-2";
		var request=new Request({method:'post',asynchronous:false,parameters:fileitem,onSuccess:addShowFieldList,functionId:'1602010212'});
  		document.getElementById("fieldid").value=return_vo.toUpperCase();
  		toggles("saveformula_brilliant");	
		hides("saveformula_dark");
  	}else{
  		return ;
  	}
}
function addShowFieldList(outparamters){
		var usedlist=outparamters.getValue("usedlist");
		var itemid = outparamters.getValue("itemid");
		AjaxBind.bind(projectForm.left_fields,usedlist);
		var lefts= document.getElementsByName("left_fields");
		if(lefts==null){
			return;
		}else{
			var left_vo = lefts[0];
			for(var i=0;i<left_vo.options.length;i++){
				if(left_vo.options[i].value==itemid){
					left_vo.options[i].selected=true;
					selectmethod();
					break;
				}
			}
		}
}
function function_Wizard(){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&infor=7&salaryid=&tableid=&salarytemp=&checktemp=sjld&mode=tjxm"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	symbol(return_vo);
  	 	toggles("saveformula_brilliant");	
		hides("saveformula_dark");
  	}else{
  		return ;
  	}
}
function statConditions(){
	var fielditemid=checkfieldid();
	if(fielditemid==null){
		alert("<bean:message key='org.autostatic.mainp.select.item'/>");
		return;
	}
	var expresion = document.getElementById("expresion").value;
    var thecodeurl ="/org/autostatic/mainp/setstatecond.do?b_query=link&expresion="+$URL.encode(expresion); 
    var return_vo= window.showModalDialog(thecodeurl,"", 
             "dialogWidth:700px; dialogHeight:440px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	return_vo=return_vo=='no'?"":return_vo;
    	document.getElementById("expresion").value=getEncodeStr(return_vo);
    	toggles("saveformula_brilliant");	
		hides("saveformula_dark");
    }
}
function delProject(){
	var fielditemid=checkfieldid();
    if(fielditemid==null){
		alert("<bean:message key='org.autostatic.mainp.select.item'/>");
		return;
	}
	var desc=checkfielddesc();	
	if(confirm("<bean:message key='org.autostatic.mainp.del.statis.pro'/>"+desc+"?")){
    	var fieldname=document.getElementById("fieldname").value;
		var in_paramters="fieldname="+fieldname+"-"+fielditemid+"-2";
   		var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'1602010206'});
	}
}
function symbol(cal){
	var b = document.getElementById("shry").readOnly;
	if(b==true){
		return ;
	}
	document.getElementById("formula").focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=cal;
	}
}
function statmethods(){
	var stat = document.getElementById("stat_methods").value;
	if(stat==0){
		hides("wizard_method");
		toggles("wizard_stat");
		document.getElementById("formula").value="";
		document.getElementById("shry").readOnly=true;
	}else{
		hides("wizard_stat");
		toggles("wizard_method");
		document.getElementById("shry").readOnly=false;
	}
	toggles("saveformula_brilliant");	
	hides("saveformula_dark");
}
function selectmethod(){
	var fielditemid=checkfieldid();
	var fieldid = document.getElementById("fieldid").value;
	document.getElementById("stat_methods").value = 0;
	var pars="itemid="+fielditemid;  
    var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:clearmula,functionId:'1602010218'});
	statmethods();
	if(fieldid!=null){
		if(fieldid. toLowerCase()==fielditemid.toLowerCase()){
			toggles("saveformula_brilliant");	
			hides("saveformula_dark");
		}else{
			hides("saveformula_brilliant");	
			toggles("saveformula_dark");
		}
	}else{
		hides("saveformula_brilliant");	
		toggles("saveformula_dark");
	}
} 	
function savemula(){
	var fielditemid=checkfieldid();
	if(fielditemid==null){
		alert("<bean:message key='org.autostatic.mainp.select.project'/>");
		return;
	}
	var formula=document.getElementById("formula").value;
	document.getElementById("fieldid").value='';
	var forma=document.getElementById("stat_methods").value;
	var itemid = document.getElementById("fieldid").value;
	if(forma!="0"){
		formula = formula.replace(/(^\s*)/g, "");
		formula = formula.replace(/(\s*$)/g, "");
		if(formula.length<1){
			alert("公式不能为空!");
		}
	}
  	var hashvo=new ParameterSet();
	hashvo.setValue("c_expr",getEncodeStr(formula));
	hashvo.setValue("itemid",fielditemid);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'1602010217'},hashvo);				
}

function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
  	var forma=document.getElementById("stat_methods").value;
	if(info=="ok"||forma==0){
		b = true;
		var fielditemid=checkfieldid();
		if(fielditemid==null){
			alert("<bean:message key='org.autostatic.mainp.select.item'/>");
			return;
		}
		var formula=document.getElementById("formula").value;
		var expression=document.getElementById("expresion").value;
		if(expression.length<1){
			expression="|";
		}
		expression = getDecodeStr(expression);
		var InCludeChild=0;
		var stat_methods = document.getElementById("stat_methods").value;
		if(document.getElementById("includechild").checked==true){
			InCludeChild=1;
		}
		var infor = fielditemid+",2::"+stat_methods+"::"+formula+"|0::"+expression+"::"+InCludeChild+posArr ;
		var pars="formula="+getEncodeStr(infor);  
    	var request=new Request({method:'post',asynchronous:false,parameters:pars,functionId:'1602010211'});
    	toggles("saveformula_dark");	
    	hides("saveformula_brilliant");
		
	}else{
		alert(getDecodeStr(info));
	}
} 	
function clearmula(outparamters){
	var expre = outparamters.getValue("info");
	var includechild = outparamters.getValue("includechild");
	if(includechild!=null&&includechild.length>0){
		if(includechild.length>10){
			posArr = includechild.substring(1);
			includechild=includechild.substring(0,1);
		}else{
			posArr="";
		}
	}else{
		posArr="";
	}
	if(getDecodeStr(expre)!="::::"){
		var arr = getDecodeStr(expre).split("::");
		document.getElementById("formula").value=arr[0];
		document.getElementById("expresion").value=getEncodeStr(arr[1]);
		document.getElementById("stat_methods").value=arr[2];
	}else{
		document.getElementById("formula").value="";
		document.getElementById("expresion").value="";
		document.getElementById("stat_methods").value=0;
	}
	if(includechild=='1'){
		document.getElementById("includechild").checked=true;
	}else{
		document.getElementById("includechild").checked=false;
	}
}
function checkfieldid(){
	var fielditemid;
	var lefts= document.getElementsByName("left_fields");
	var left_vo = lefts[0];
	if(lefts==null){
		return;
	}else{
		for(var i=0;i<left_vo.options.length;i++){
			if(left_vo.options[i].selected){
				fielditemid =left_vo.options[i].value;
				continue;
			}
		}
		if(fielditemid!=null){
			return fielditemid.toUpperCase();
		}else{
			return;
		}
	}
}
function checkfielddesc(){
	var desc;
	var lefts= document.getElementsByName("left_fields");
	var left_vo = lefts[0];
	if(lefts==null){
		return;
	}else{
		for(var i=0;i<left_vo.options.length;i++){
			if(left_vo.options[i].selected){
				desc =left_vo.options[i].text;
				continue;
			}
		}
		if(desc!=null){
			return desc;
		}else{
			return;
		}
	}
}
function changeSetid(){
    var fieldsetid=document.getElementById("setid").value;
	var in_paramters="fieldsetid="+fieldsetid;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldSetList,functionId:'3020050011'});
}
function showFieldSetList(outparamters){
	var itemlist=outparamters.getValue("itemlist");
	AjaxBind.bind(projectForm.itemid_arr,itemlist);
	changeCode();
}
function changeCode(){
   var itemid=getItemid().split(":");
   var in_paramters="itemid="+itemid[0];
   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}
function changeCodeValue(){
   var itemid=getItemid().split(":");
   symbol(itemid[1]);
   var in_paramters="itemid="+itemid[0];
   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
   togglesSave();
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist!=null&&codelist.length>1){
		toggles("viewcode");
		AjaxBind.bind(projectForm.codesetid_arr,codelist);
	}else{
		hides("viewcode");
	}
}
function getItemid(){
	var itemid="";
	var itemid_arr= document.getElementsByName("itemid_arr");
	var itemid_arr_vo = itemid_arr[0];
	if(itemid_arr==null){
		return "";
	}else{
		for(var i=0;i<itemid_arr_vo.options.length;i++){
			if(itemid_arr_vo.options[i].selected){
				itemid =itemid_arr_vo.options[i].text;
				continue;
			}
		}
		return itemid;
	}
}
function getCodesid(){
	var codeid="";
	var codesetid_arr= document.getElementsByName("codesetid_arr");
	if(codesetid_arr==null){
		return;
	}else{
		var codesetid_arr_vo = codesetid_arr[0];
		for(var i=0;i<codesetid_arr_vo.options.length;i++){
			if(codesetid_arr_vo.options[i].selected){
				codeid =codesetid_arr_vo.options[i].value;
				continue;
			}
		}
		symbol("\""+codeid+"\"");
		togglesSave();
	}
}
function togglesSave(){	
    hides("saveformula_dark");
    toggles("saveformula_brilliant");	
}	
function sortItem(chsort){
	var fielditemid="";
	var nextitemid="";
	var affteritemid="";
	var lefts= document.getElementsByName("left_fields");
	var left_vo = lefts[0];
	if(lefts==null){
		return;
	}else if(left_vo.options.length<2){
		return;
	}else{
		var chflag="0";
		for(var i=0;i<left_vo.options.length;i++){
			if(left_vo.options[i].selected){
				fielditemid =left_vo.options[i].value;
				chflag="1";
			}else{
				if(chflag=="1"){
					nextitemid = left_vo.options[i].value;
					break;
				}
				affteritemid = left_vo.options[i].value;
			}
		}
		if(chsort=="down"&&(nextitemid==null||nextitemid.length<1)){
			return;
		}
		if(chsort=="up"&&(affteritemid==null||affteritemid.length<1)){
			return;
		}
		
		var hashvo=new ParameterSet();
		hashvo.setValue("itemid",fielditemid);
		hashvo.setValue("sortitem",chsort);
		hashvo.setValue("nextitemid",nextitemid);
		hashvo.setValue("affteritemid",affteritemid);
		var request=new Request({method:'post',asynchronous:false,functionId:'1602010227'},hashvo);		
	}
}		 	  		 			
</script>

<html:form action="/org/autostatic/mainp/statistics">
<table border="0">
 <tr>
 	<td>
		<input type="hidden" name="expresion">
		<input type="hidden" name="fieldid">
	</td>
</tr> 
</table>
<table width="100%" height="352" border="0">
  <tr> 
    <td width="35%" rowspan="4" align="center" >
     <fieldset align="center" style="width:100%;height:100%;margin: 0 0 0 10;">
      <legend><bean:message key="org.maip.statistics"/></legend>
      <table width="97%" height="340" border="0" >
        <tr> 
          <td>
			<html:select name="projectForm" property="fieldname" style="width:240" onchange="change();">
			 	<html:optionsCollection property="fieldlist" value="dataValue" label="dataName" />
			</html:select>
		  </td>
		  <td rowspan="2" valign="middle">
		  	 <table width="100%"  border="0">
		  	 <tr>
		  	 	<td>
		  			<input type="button" value="上移" onclick="sortItem('up');upItem($('left_fields'));" Class="mybutton">
		  		</td>
		  	</tr>
		  	<tr>
		  	 	<td height="30">&nbsp;</td>
		  	</tr>
		  	<tr>
		  	 	<td>
		  			<input type="button" value="下移" onclick="sortItem('down');downItem($('left_fields'));" Class="mybutton">
		  		</td>
		  	</tr>
		  	</table>
		  </td>
        </tr>
        <tr> 
          <td align="center">
          	<bean:message key="conlumn.investigate_item.name"/><br>
			 <select name="left_fields" multiple="multiple" onchange="selectmethod();" style="height:250px;width:100%;font-size:9pt">
             </select>
			<input name="newproject" type="button" id="newproject" value=' <bean:message key="button.insert"/> ' onclick="addproject();" Class="mybutton">
       		<input name="delproject" type="button" id="delproject" value=' <bean:message key="button.delete"/> ' onclick="delProject();" Class="mybutton">
		  </td>
        </tr>
      </table>
      </fieldset></td>
    <td width="43%" align="center">
     <fieldset align="center" style="width:100%;">
      <legend><bean:message key="org.maip.formula.definition"/></legend>
      <table width="97%" border="0">
        <tr> 
          <td height="27" colspan="2" align="center"><bean:message key="kq.formula.fashion"/>
          	<html:select name="projectForm" property="stat_methods" onchange="statmethods();" style="width:120">
			 	<html:optionsCollection property="statlist" value="dataValue" label="dataName" />
			</html:select>  
		  </td>
          <td width="33%">
          	<html:checkbox name="projectForm" property="includechild" onclick="toggles('saveformula_brilliant');hides('saveformula_dark');">
          		<bean:message key="org.maip.contain.lower"/> 
          	</html:checkbox> 
           </td>
        </tr>
        <tr> 
          <td colspan="3" align="center"> 
          	<html:textarea name="projectForm" property="formula" onclick="this.pos=document.selection.createRange();" onkeydown="toggles('saveformula_brilliant');hides('saveformula_dark');" cols="40" rows="6" styleId="shry" readonly="true"></html:textarea> 
          </td>
        </tr>
        <tr> 
          <td width="35%" height="20" align="right"> 
          	<span id='wizard_method'>
          		<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick="function_Wizard();" Class="mybutton"> 
          	</span>
          	<span id='wizard_stat'> 
          		<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' Class="mybutton" disabled="disabled"> 
          	</span>
          </td>
          <td width="20%" align="center"> <input name="statistics" type="button" id="statistics" value='<bean:message key="makeupanalyse.stat"/>' onclick="statConditions();" Class="mybutton"> 
          </td>
          <td align="left"> 
            <span id='saveformula_brilliant'>
          		<input name="saveformula" type="button" id="saveformula" value='<bean:message key="org.maip.formula.preservation"/>' onclick="savemula();" Class="mybutton">
          	</span>
          	<span id='saveformula_dark'> 
          		<input name="saveformula" type="button" id="saveformula" value='<bean:message key="org.maip.formula.preservation"/>' Class="mybutton" disabled="disabled"> 
          	</span>
          </td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
  <tr> 
    <td height="90" align="center"> 
      <fieldset align="center" style="width:100%;height:90">
      <legend><bean:message key="org.maip.reference.projects"/></legend>
      <table width="98%" height="50" border="0">
        <tr> 
          <td height="25" align="center"><bean:message key="menu.table"/>&nbsp;&nbsp;
         		<html:select name="projectForm" property="setid" onchange="changeSetid();" style="width:200;font-size:9pt" >
			 			<html:optionsCollection property="setlist" value="dataValue" label="dataName" />
				</html:select>
		  </td>
        </tr>
        <tr> 
          <td height="25" align="center"><bean:message key="menu.field"/>&nbsp;&nbsp;
				<select name="itemid_arr" onchange="changeCodeValue();"  style="width:200;font-size:9pt">
             	</select>
		  </td>
        </tr>
        <tr> 
          <td height="25" align="center">
         		<span id="viewcode">
         		<table width="100%" border="0" align="center">
              		<tr>
              			<td align="right" width="25%"><bean:message key="conlumn.codeitemid.caption"/>
         				</td>
         				<td align="left">&nbsp;
              				<select name="codesetid_arr" onchange="getCodesid();" style="width:200;font-size:9pt">
             				</select>
         				</td>
         			</tr>
         		</table>
         		</span>
		  </td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
  <tr> 
    <td height="60" rowspan="2" align="center"> <fieldset align="center" style="width:100%;">
      <legend><bean:message key="org.maip.operators"/></legend>
      <table width="85%" border="0">
        <tr> 
          <td width="10%" rowspan="1" align="center"> <input type="button" name="button0" value="0" onclick="symbol('0');togglesSave();" class="mybutton" style="width:25px;height: 20px;"> 
          </td>
          <td width="10%" height="24" align="center"> <input name="button1" type="button" class="mybutton" id="button1" onclick="symbol('1');togglesSave();" value="1" style="width:25px;height: 20px;"> 
          </td>
          <td width="10%" align="center"> <input name="button2" type="button" class="mybutton" id="button2" onclick="symbol('2');togglesSave();" value="2" style="width:25px;height: 20px;"> 
          </td>
          <td width="10%" align="center"> <input name="button3" type="button" class="mybutton" id="button3" onclick="symbol('3');togglesSave();" value="3" style="width:25px;height: 20px;"> 
          </td>
          <td width="10%" align="center"> <input name="button4" type="button" class="mybutton" id="button4" onclick="symbol('4');togglesSave();" value="4" style="width:25px;height: 20px;"> 
          </td>
          <td width="10%" align="center"> <input name="button5" type="button" class="mybutton" id="button5" onclick="symbol('5');togglesSave();" value="5" style="width:25px;height: 20px;"> 
          </td>
          <td width="10%" align="center"> <input name="button6" type="button" class="mybutton" id="button6" onclick="symbol('6');togglesSave();" value="6" style="width:25px;height: 20px;"> 
          </td>
          <td width="10%" align="center"> <input name="button7" type="button" class="mybutton" id="button7" onclick="symbol('7');togglesSave();" value="7" style="width:25px;height: 20px;"> 
          </td>
          <td width="20%"><input name="button8" type="button" class="mybutton" id="button8" onclick="symbol('8');togglesSave();" value="8" style="width:25px;height: 20px;"></td>
        </tr>
        <tr> 
          <td align="center"> <input name="button9" type="button" class="mybutton" id="button9" onclick="symbol('9');togglesSave();" value="9" style="width:25px;height: 20px;"> 
          </td>
          <td align="center"> <input name="button10" type="button" class="mybutton" id="button10" onclick="symbol('.');togglesSave();" value="." style="width:25px;height: 20px;"> 
          </td>
          <td  align="center"> <input name="button11" type="button" class="mybutton" id="button11" onclick="symbol('+');togglesSave();" value="+" style="width:25px;height: 20px;"> 
          </td>
          <td align="center"> <input name="button12" type="button" class="mybutton" id="button12" onclick="symbol('-');togglesSave();" value="-" style="width:25px;height: 20px;"> 
          </td>
          <td align="center"> <input name="button13" type="button" class="mybutton" id="button13" onclick="symbol('*');togglesSave();" value="*" style="width:25px;height: 20px;"> 
          </td>
          <td align="center"> <input name="button14" type="button" class="mybutton" id="button14" onclick="symbol('/');togglesSave();" value="/" style="width:25px;height: 20px;"></td>
          <td align="center"> <input name="button14" type="button" class="mybutton" id="button14" onclick="symbol('%');togglesSave();" value="%" style="width:25px;height: 20px;"></td>
          <td align="center"> <input name="button15" type="button" class="mybutton" id="button15" onclick="symbol('(');togglesSave();" value="(" style="width:25px;height: 20px;"> 
          </td>
          <td> <input name="button16" type="button" class="mybutton" id="button16" onclick="symbol(')');togglesSave();" value=")" style="width:25px;height: 20px;"></td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
</table>
<script language="javascript">
change();
changeSetid();
</script>
</html:form>