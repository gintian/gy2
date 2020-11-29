<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<hrms:themes></hrms:themes>
<script language="javascript">

  function changeFieldSet(){
  	var v = keyDefinitionForm.fieldSet.value;
  	var hashvo=new ParameterSet();
    hashvo.setValue("fieldsetid",v);
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'05601000028'},hashvo);					
  }
  
  function resultChangeFieldSet(outparamters){
  	var fielditemlist=outparamters.getValue("fielditemlist");
	AjaxBind.bind(keyDefinitionForm.fieldName,fielditemlist);
	var sel_vo=eval("document.keyDefinitionForm.fieldName");
    sel_vo.fireEvent("onchange");
    keyDefinitionForm.codeItemDescs.value="";
    keyDefinitionForm.codeItemValues.value="";
  }
  
  function changeFieldItem(){
  	 	var fieldsetid = keyDefinitionForm.fieldSet.value;
  	 	var itemid = keyDefinitionForm.fieldName.value;
	  	//alert(fieldsetid);
	  	var hashvo=new ParameterSet();
	  	hashvo.setValue("object" ,keyDefinitionForm.object.value);
	    hashvo.setValue("fieldsetid",fieldsetid);
	    hashvo.setValue("itemid",itemid);	  
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultChangeFieldItem,functionId:'05601000029'},hashvo);
  }
  
  function resultChangeFieldItem(outparamters){
  	var info= outparamters.getValue("info");
  	//alert(info);
  	AjaxBind.bind(keyDefinitionForm.dialog,info)
  	 keyDefinitionForm.codeItemDescs.value="";
    keyDefinitionForm.codeItemValues.value="";
  }
  
   function selectCodes(codeSetID){	
		 var re_vo=select_codeTree_dialog(codeSetID);		 	 
	 	 if(re_vo){
	 	 	var tmp=re_vo.content;
	    	var len=tmp.length;
	    	//alert(tmp);
	    	if(tmp.substring(0,len-1).indexOf("root")!=-1){
	    		alert("根目录不予选择！");
	    		return;
	    	}
	    	
	    	if(codeSetID=='UN'||codeSetID=='UM'||codeSetID=='@K'){//需要特殊处理
	    		var hashvo=new ParameterSet();
		    	hashvo.setValue("codeItemValue",tmp.substring(0,len-1)); 
				hashvo.setValue("codeSetID",codeSetID);
				hashvo.setValue("codeValue",re_vo.title);
		    	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,
					onSuccess:selectCodeResult,functionId:'05601000022'},hashvo);	
	    	}else{//正常操作
	    		var a_codeValues = eval("document.keyDefinitionForm.codeItemDescs");//机构组织的中文显示框
	   		    var a_codeItemValue = eval("document.keyDefinitionForm.codeItemValues");
	  			a_codeItemValue.value=tmp.substring(0,len-1);
	 			a_codeValues.value=re_vo.title;	
	    	}	
		 }

 	}
 	
	 	 function selectCodeResult(outparamters){
		 	var a_codeValues = eval("document.keyDefinitionForm.codeItemDescs");//机构组织的中文显示框
			var a_codeItemValue = eval("document.keyDefinitionForm.codeItemValues");
		 	var isTrue=outparamters.getValue("isTrue");
		 	var codeValue=outparamters.getValue("codeValue");
		 	var codeItemValue=outparamters.getValue("codeItemValue");
		 	var codeSetID=outparamters.getValue("codeSetID");
		 	if(isTrue==0)
		 	{
		 		if(codeSetID=='UN')
		 			alert("统计指标代码值有的不是单位！");
		 		if(codeSetID=='UM')
		 			alert("统计指标代码值有的不是部门！");
		 		if(codeSetID=='@K')
		 			alert("统计指标代码值有的不是职位！");
		 	}
		 	else
		 	{
		     	a_codeItemValue.value=codeItemValue;
			 	a_codeValues.value=codeValue;	
		 	}
		 }
		 
		 function hideSelect(dd,cc){
			  if(dd) {
			      cc.style.display="inline";		     
			  }else{
			      cc.style.display="none";			     
			   }
		 }
		 
		 function selectFielditem(obj1 , obj2 , flag ,type){
		 	//alert(flag);
		 	//alert(type);
		 	selectFieldItems(obj1,obj2,flag,type);
		 	//var info = selectFieldItem("ALL","NC");
		 	//alert(info);
		 	
		 }
		 
		  function tess(str){
		   	var tt=true;
		   	tt=validateNumeric(str.value);    
	       	if(tt==false){
	       	  alert("只能输入数字！");
	       	  str.value="";
	       	  str.focus();
	      	  return false;
	      	 } 
		 }
			 
		function validateNumeric(strValue){  
		   var  objRegExp =/(^-?\d\d*\.\d*$)|(^-?\d\d*$)|(^-?\.\d\d*$)/;  
		   return  objRegExp.test(strValue);  
		}
		
		 function save(){
		 	if(keyDefinitionForm.name.value == ""){
		 		alert("指标名称不能为空！");
		 		return;
		 	}
		 	if(keyDefinitionForm.desc.value == ""){
		 		alert("指标解释不能为空！");
		 		return;
		 	}
		 	if(keyDefinitionForm.standartValue.value == ""){
		 		alert("标准值不能为空！");
		 		return;
		 	}else if(keyDefinitionForm.standartValue.value<=0 || keyDefinitionForm.standartValue.value>99){
		 		alert("标准值应在1~99之间！");
		 		keyDefinitionForm.standartValue.value="";
		 		return;
		 	}
		 	if(keyDefinitionForm.controlValue.value == ""){
		 		alert("控制值不能为空！");
		 		return;
		 	}else if(keyDefinitionForm.controlValue.value<=0 || keyDefinitionForm.controlValue.value>99){
		 		alert("控制值应在1~99之间！");
		 		keyDefinitionForm.controlValue.value="";
		 		return;
		 	}
		 	if(keyDefinitionForm.codeItemDescs.value == ""){
		 		alert("统计项目代码值不能为空！");
		 		return;
		 	}
		 	if(keyDefinitionForm.oneFieldItem.value == ""){
		 		alert("计算公式不能为空！");
		 		return;
		 	}
		 	if( cc.style.display == "inline"){
		 		if(keyDefinitionForm.twoFieldItem.value == ""){
			 		alert("计算公式（被除数）不能为空！");
			 		return;
		 		}
		 	}
		 	
		 	keyDefinitionForm.action="/general/deci/definition/add_definition.do?b_addsave=save";
		 	keyDefinitionForm.submit();
		 }
		 
		 function cancel(){
		 	history.back();
		 }
		 
</script>
<body onLoad="init()">
<html:form action="/general/deci/definition/add_definition">
	<fieldset align="center" style="width:50%;">
		<table border="0" cellspacing="0" align="left" cellpadding="5">
			<input type="hidden" name="factorid" value="${keyDefinitionForm.factorid}" />
			<input type="hidden" name="operateFlag" value="${keyDefinitionForm.operateFlag}" />
			<input type="hidden" name="typeid" value="${keyDefinitionForm.typeid}" />
			<input type="hidden" name="object" value="${keyDefinitionForm.object}" />
			<input type="hidden" name="dialog" value="${keyDefinitionForm.dialog}" />

			<input type="hidden" name="codeItemValues" value="${keyDefinitionForm.codeItemValues}" />
			<input type="hidden" name="oneFieldItemValue" value="${keyDefinitionForm.oneFieldItemValue}" />
			<input type="hidden" name="twoFieldItemValue" value="${keyDefinitionForm.twoFieldItemValue}" />

			<tr style="height:30px;">
				<td align="right" nowrap valign="middle">
					<bean:message key="general.defini.target" />
				</td>
				<td align="left" nowrap valign="middle">
					<input type="text" name="name" value="${keyDefinitionForm.name}" class="text4" style="width:400px;">
				</td>
			</tr>
			<tr style="height:30px;">
				<td align="right" nowrap valign="middle">
					<bean:message key="general.defini.explain" />
				</td>
				<td align="left" nowrap valign="middle">
					<input type="text" name="desc" value="${keyDefinitionForm.desc}" class="text4" style="width:400px;">
				</td>
			</tr>
			<tr style="height:30px;">
				<td align="right" nowrap valign="middle">
					<bean:message key="general.defini.standard" />
				</td>
				<td align="left" nowrap valign="middle">
					<INPUT type="text" name="standartValue" onchange="tess(this);" value="${keyDefinitionForm.standartValue}" class="text4" style="width:400px;"/>
				</td>
			</tr>
			<tr style="height:30px;">
				<td align="right" nowrap valign="middle">
					<bean:message key="general.defini.control" />
				</td>
				<td align="left" nowrap valign="middle">
					<INPUT type="text" name="controlValue" onchange="tess(this);" value="${keyDefinitionForm.controlValue}" class="text4" style="width:400px;"/>
				</td>
			</tr>
			<tr style="height:30px;">
				<td align="right" nowrap valign="middle">
					<bean:message key="general.defini.xname" />
				</td>
				<td align="left" nowrap valign="middle">
					<hrms:optioncollection name="keyDefinitionForm" property="setList" collection="list" />
					<html:select name="keyDefinitionForm" property="fieldSet" size="1" onchange="changeFieldSet();" style="width:400px;">
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
					</html:select>

				</td>
			</tr>
			<tr style="height:30px;">
				<td align="right" nowrap valign="middle">

				</td>
				<td align="left" nowrap valign="middle">
					<hrms:optioncollection name="keyDefinitionForm" property="itemList" collection="list" />
					<html:select name="keyDefinitionForm" property="fieldName" size="1" onchange="changeFieldItem();" style="width:400px;">
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
					</html:select>
				</td>
			</tr>

			<tr style="height:30px;">
				<td align="right" nowrap valign="middle">
					<bean:message key="general.defini.code" />
				</td>
				<td align="left" nowrap valign="middle">
				<table cellpadding="0" cellspacing="0" border="0"><tr><td>
					<html:text name="keyDefinitionForm" property="codeItemDescs" size="20" styleClass="TEXT4" readonly="true" value="${keyDefinitionForm.codeItemDescs}"  onfocus="selectCodes(keyDefinitionForm.dialog.value)" style="width:400px;"/>
					</td>
					<td align="left" nowrap valign="middle" style="padding-left:5px;padding-top:5px;">
					<img src="/images/code.gif" onclick="selectCodes(keyDefinitionForm.dialog.value)"/>
					</td>
					</tr></table>
				</td>
			</tr>

			<tr style="height:30px;">
				<td align="right" nowrap valign="middle">
					<bean:message key="general.defini.method" />
				</td>
				<td align="left" nowrap valign="middle">
					<html:select name="keyDefinitionForm" property="staticMethod" size="1" style="width:400px;">
						<html:option value="1">
							<bean:message key="kq.formula.sum" />
						</html:option>
						<html:option value="2">
							<bean:message key="kq.formula.max" />
						</html:option>
						<html:option value="3">
							<bean:message key="kq.formula.min" />
						</html:option>
						<html:option value="4">
							<bean:message key="kq.formula.average" />
						</html:option>
					</html:select>
				</td>
			</tr>
			<tr style="height:30px;">
				<td align="left" nowrap valign="middle">
					<bean:message key="kq.item.count" />

					<html:checkbox name="keyDefinitionForm" property="box" value="1" onclick="hideSelect(this.checked,cc);" />
					<bean:message key="general.defini.prop" />

				</td>
				<td align="left" nowrap valign="middle">
					<table cellpadding="0" cellspacing="0" border="0">
					<tr><td align="left" nowrap valign="middle">
					<INPUT type="text" name="oneFieldItem" size="10" value="${keyDefinitionForm.oneFieldItem}"  onfocus="selectFielditem(keyDefinitionForm.oneFieldItem,keyDefinitionForm.oneFieldItemValue,keyDefinitionForm.object.value,'D,N')" readonly class="text4" style="width:400px;"/>
					</td><td  align="left" nowrap valign="middle" style="padding-left:5px;padding-top:5px;">
					<img src="/images/code.gif" onclick="selectFielditem(keyDefinitionForm.oneFieldItem,keyDefinitionForm.oneFieldItemValue,keyDefinitionForm.object.value,'D,N')" />
					</td><td align="left" valign="middle" nowrap>
					<div id="cc" style="display:none">
						<table><tr><td align="left" valign="middle" nowrap>
						/
						<INPUT type="text" name="twoFieldItem" size="10" value="${keyDefinitionForm.twoFieldItem}"  onfocus="selectFielditem(keyDefinitionForm.twoFieldItem,keyDefinitionForm.twoFieldItemValue,keyDefinitionForm.object.value,'D,N')" readonly class="text4" style=""/>
						</td><td>
						<img src="/images/code.gif" onclick="selectFielditem(keyDefinitionForm.twoFieldItem,keyDefinitionForm.twoFieldItemValue,keyDefinitionForm.object.value,'D,N')" align="absmiddle"/>
						</td></tr></table>
					</div>
					</td></tr></table>
				</td>

			</tr>
		</table>
	</fieldset>
	<table width="100%">
		<tr>
			<td align="center" colspan="2" height="35px;">
				<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save();" />
				<input type="button" class="mybutton" value="<bean:message key='button.return' />" onClick="cancel()" />
			</td>
		</tr>
	</table>
</html:form>

<script language="javascript">
  
  function init(){
  	var operateFlag = "${keyDefinitionForm.operateFlag}";
  	//alert(operateFlag);
  	if(operateFlag == "1"){
  	}else{
  		var box = "${keyDefinitionForm.box}";
  		//alert(box);
  		if(box == "1"){
  			 cc.style.display="inline";
  		}
  		var fieldSet = "${keyDefinitionForm.fieldSet}";
  		//alert(fieldSet);
  		for(var i=0 ; i<keyDefinitionForm.fieldSet.options.length; i++ ){
	  		if(keyDefinitionForm.fieldSet.options[i].text ==fieldSet){
	  			keyDefinitionForm.fieldSet.options[i].selected = true;
	  		}
	  	}	
  		var fieldName = "${keyDefinitionForm.fieldName}";
  		//alert(fieldName);
  		for(var i=0 ; i<keyDefinitionForm.fieldName.options.length; i++ ){
	  		if(keyDefinitionForm.fieldName.options[i].text ==fieldName){
	  			keyDefinitionForm.fieldName.options[i].selected = true;
	  		}
	  	}	
  	}
  	
  }
  </script>
</body>
