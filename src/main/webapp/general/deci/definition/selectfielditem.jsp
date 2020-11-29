<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>

<script language="javascript">

	
      var s,sv,v,n;
	  function changeFieldSet(){
	  	var v = keyDefinitionForm.set.value;
	  	var hashvo=new ParameterSet();
	    hashvo.setValue("fieldsetid",v);
	 // 获取参数传递的对象
	 	if(getBrowseVersion()){
	    	hashvo.setValue("fielditemflag",top.dialogArguments);
	    }else{
	    	if(parent.parent.selectFieldReturnValue){
	  			var win = parent.parent.Ext.getCmp('select_item');
		    	hashvo.setValue("fielditemflag",win.parameter);
		  	}
	    }
	    hashvo.setValue("party",'${keyDefinitionForm.party }');
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'05601000027'},hashvo);					
	  }
	  
	  function resultChangeFieldSet(outparamters){
	  	var fielditemlist=outparamters.getValue("fielditemlist");
		AjaxBind.bind(keyDefinitionForm.itemid,fielditemlist);
	  }
	  
	  function save(){
	  	s = keyDefinitionForm.set.value;
	  	sv ="";
	  	for(var i=0 ; i<keyDefinitionForm.set.options.length; i++ ){
	  		if(keyDefinitionForm.set.options[i].selected == true){
	  			sv = keyDefinitionForm.set.options[i].text;
	  		}
	  	}	
	  	
	  	v = keyDefinitionForm.itemid.value;
	  	if(v==null||v==""){
	  		 clearvalue();
	  	}
	  	n ="";
	  	for(var i=0 ; i<keyDefinitionForm.itemid.options.length; i++ ){
	  		if(keyDefinitionForm.itemid.options[i].selected == true){
	  			n = keyDefinitionForm.itemid.options[i].text;
	  		}
	  	}

	  	//如果是党团工会设置界面调用则不需验证 xuj 2010-2-5
		<logic:equal value="party" name="keyDefinitionForm" property="party">
	  		//returnValue=s+'/'+sv+'/'+v+'/'+n;
			//window.close();
			extWinValueAndClose(s+'/'+sv+'/'+v+'/'+n);
	  	</logic:equal>
	  	//var hashvo=new ParameterSet();
	   // hashvo.setValue("sequence_name",s+'.'+v);
	  		//var request=new Request({method:'post',asynchronous:false,
			//parameters:'',onSuccess:isHaveSequenceName,functionId:'05601000035'},hashvo);					
	  	//returnValue=s+'/'+sv+'/'+v+'/'+n;
		//window.close();
		extWinValueAndClose(s+'/'+sv+'/'+v+'/'+n);
	  }
	  function isHaveSequenceName(outparamters){
	  	if(outparamters.getValue("flag")=='1'){
	  		alert("请您重新选择关联指标，此指标已添加！！！");
	  	}else{
		  	//returnValue=s+'/'+sv+'/'+v+'/'+n;
			//window.close();
			extWinValueAndClose(s+'/'+sv+'/'+v+'/'+n);
		}
	  }
	  function cancel(){
	  	//window.close();
	  	extWinValueAndClose();
	  }
	  function clearvalue(){
	  		//returnValue='/'+sv+'//';
			//window.close();
			extWinValueAndClose('/'+sv+'//');
	  }
	  //ie和非ie 浏览器 弹窗 返回数据 和关闭弹窗  wangb 20190319 
	  function extWinValueAndClose(obj){
	  	if(parent.parent.selectFieldReturnValue){
	  			parent.parent.selectFieldReturnValue(obj);
	  			var win = parent.parent.Ext.getCmp('select_item');
	  			win.close();
	  			return;
	  	}
	  	if(obj)
	  		parent.window.returnValue=obj;
	  	window.close();
	  }
</script>

<div class="fixedDiv3">
<FORM name="keyDefinitionForm" method="post" >
	<fieldset align="center" style="width:290;height:110;valign:middle;">
		<table border="0" height="100%" cellspacing="0" align="center" valign="middle" cellpadding="0">
			<tr>
				<td align="right" nowrap valign="bottom">
					<bean:message key="field_result.fieldset" />
				</td>
				<td align="left" nowrap valign="bottom" style="padding-left:5px;">
					<hrms:optioncollection name="keyDefinitionForm" property="fieldSetList" collection="list" />
					<html:select name="keyDefinitionForm" property="set" size="1" onchange="changeFieldSet();"  style="width:180px">
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
					</html:select>

				</td>
			</tr>
			<tr>
				<td align="right" nowrap valign="middle">
					<bean:message key="field_result.fielditem"/>
				</td>
				<td align="left" nowrap valign="middle" style="padding-left:5px;">
					<hrms:optioncollection name="keyDefinitionForm" property="fieldItemList" collection="list" />
					<html:select name="keyDefinitionForm" property="itemid" size="1"  style="width:180px">
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
					</html:select>
				</td>
			</tr>
		</table>
	</fieldset>
    <div align="center" style="padding-top: 5px;">
		<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save()"/>
		<input type="button" class="mybutton" value="<bean:message key='button.cancel' />" onClick="cancel()"/>
    </div>
	</form>
</div>	
<script>
	if(getBrowseVersion()==10){//ie11 浏览器 样式问题修改 wangb 20190321
		setTimeout(function(){
			var fieldset = document.getElementsByTagName('fieldset')[0];
			fieldset.style.margin = '0 auto';
		},100);
	}
</script>