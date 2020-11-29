var Global = new Object();
Global.fromName='通知模板';
var email_array= new Array();
var formula_array= new Array();//存放的是未存入后台的公式
var fieldid = 1;
/*
保存
*/
 Global.addTemplate = function(){
 	 if(document.getElementById('buttonSave').innerHTML == '编辑'){
 		Ext.getCmp('buttonPanelId').setVisible(true);
 		fieldid = Ext.getCmp('tempalteFieldId').getValue();//获取数据库中最大的fieldid+1
 		subModuleValue = Ext.getCmp('subModuleId').getValue();
 		//当模板为招聘批次通知时，插入指标，插入，修改公式按钮禁用 
	   	if(subModuleValue == "92"){
	   		Ext.getCmp("fielditemid").disable();
	   		Ext.getCmp("formulaaddid").disable();
	   		Ext.getCmp("formulaeditid").disable();
	   	}else{
	   		Ext.getCmp("fielditemid").enable();
	   		Ext.getCmp("formulaaddid").enable();
	   		Ext.getCmp("formulaeditid").enable();
	   	}
	   	
	    if(subModuleValue == "80"){
   		    Ext.getCmp("formulaAttachId").disable();
   	 	}
 		
 	 	//系统内置模板不允许更改模板类别
 	 	if(Ext.getCmp('ownflagId').getValue()=='1'){
 	 		Ext.getCmp('subModuleId').setReadOnly(true);
 	 	}else{
	 		Ext.getCmp('subModuleId').setReadOnly(false);
	    	Ext.getCmp('subModuleId').setFieldStyle('background:white'); 
	    	Ext.getCmp('other_flagId').setReadOnly(false);
	    	Ext.getCmp('other_flagId').setFieldStyle('background:white'); 
 	 	}
    	Ext.getCmp('templateName').setReadOnly(false);
    	Ext.getCmp('returnAddress').setReadOnly(false);
    	Ext.getCmp('emailName').setReadOnly(false);
    	Ext.getCmp('contentId').setReadOnly(false);
    	if(Global.isShowAttachId == 0) //编辑的时候显示问题   附件
 			Ext.getCmp('formulaAttachId').setVisible(true);
 		else
 			Ext.getCmp('formulaAttachId').setVisible(false);
    	if(Global.isShowItem == 0) //指标
    		Ext.getCmp('fielditemid').setVisible(true);
    	else
 			Ext.getCmp('fielditemid').setVisible(false);
    	if(Global.isShowInsertFormula == 0) //插入公式
    		Ext.getCmp('formulaaddid').setVisible(true);
    	else
 			Ext.getCmp('formulaaddid').setVisible(false);
    	if(Global.isShowModifyFormula == 0) //修改公式
    		Ext.getCmp('formulaeditid').setVisible(true);
    	else
 			Ext.getCmp('formulaeditid').setVisible(false);
    	Ext.getCmp('templateName').setFieldStyle('background:white');  
    	Ext.getCmp('returnAddress').setFieldStyle('background:white');  
    	Ext.getCmp('emailName').setFieldStyle('background:white');  
    	document.getElementById('buttonSave').innerHTML = '保存';
	 }else if(document.getElementById('buttonSave').innerHTML == '保存'){
	 	var subModule = Ext.getCmp('subModuleId').getValue();
	 	var other_flag = Ext.getCmp('other_flagId').getValue();
		var name = Ext.getCmp('templateName').getValue();
		var returnAddress = Ext.getCmp('returnAddress').getValue();
		var subject = Ext.getCmp('emailName').getValue();
		var tempalteId = Ext.getCmp('tempalteId').getValue();
 		if(name.replace(/[\u4E00-\u9FA5]/g,'aa').length>30){
 			name = Global.cut_str(name,15);
 			Ext.getCmp('templateName').setValue(name);
        }
 		
		var content = Ext.getCmp('contentId').getValue();
		//禁止超链接在预览模式时触发
		if(Ext.isDefined(Ext.getCmp('buttonTextfieldId'))){
			content=content.replace(/href="##"/g,'');
			content=content.replace(/value/g,'href');
		}
		var ownflag = Ext.getCmp('ownflagId').getValue();
		var hashvo=new ParameterSet();
		//显示模板类别的时候才需要判断模板类别是否为空 haosl update 20190505
		if(Global.isShowModuleType!=1 && (subModule==null||trim(subModule).length==0))
	    {
	        Ext.MessageBox.alert("提示信息","请输入模板类别");
	        return;
	    }
		
		if("90"==subModule && (other_flag==null||trim(other_flag).length==0))
        {
			Ext.MessageBox.alert("提示信息","请输入招聘环节");
			return;
        }
		
		if(name==null||trim(name).length==0)
        {
			Ext.MessageBox.alert("提示信息","请输入模板名称");
			return;
        }
		
	    if(subject==null||trim(subject).length==0)
        {
	    	Ext.MessageBox.alert("提示信息","请输入邮件标题");
	    	return;
        }
	      if(returnAddress!=''){
	      	var reg= returnAddress.search("[a-zA-Z0-9]+@[a-zA-Z0-9]+.[a-zA-Z0-9]{2,4}");
	      	if(reg<0){
		      	Ext.MessageBox.alert("提示信息","请输入正确的邮箱格式");
		      	return;
	      	}
	      }
	  	Ext.getCmp('contentId').setReadOnly(true);
		Ext.getCmp('subModuleId').setReadOnly(true);
	  	Ext.getCmp('other_flagId').setReadOnly(true);
	  	Ext.getCmp('templateName').setReadOnly(true);
	  	Ext.getCmp('returnAddress').setReadOnly(true);
	  	Ext.getCmp('emailName').setReadOnly(true);
	  	Ext.getCmp('subModuleId').setFieldStyle('background:#E6E6E6'); 
	  	Ext.getCmp('other_flagId').setFieldStyle('background:#E6E6E6'); 
	  	Ext.getCmp('templateName').setFieldStyle('background:#E6E6E6');  
	  	Ext.getCmp('returnAddress').setFieldStyle('background:#E6E6E6');  
	  	Ext.getCmp('emailName').setFieldStyle('background:#E6E6E6'); 
		if(tempalteId=="undefined"){
			tempalteId='';
		}
		if(ownflag=="undefined" || ownflag==''){
			ownflag='0';
		}
		if(subModule=="undefined" || subModule==null){//如果没有类别，则让类别为-1
			subModule='-1';
		}
		
		
		var map = new HashMap();
		map.put("subModule",subModule);
		if(other_flag!=null){
			map.put("other_flag",other_flag);
		}
		map.put("name",name);
		map.put("returnAddress",returnAddress);
		map.put("subject",subject);
		map.put("tempalteId",tempalteId);
		map.put("ownflag",ownflag);
		map.put("content",content);
		map.put("email_array",email_array);
		map.put("opt", Global.opt + "");//判断是什么模块进入的，暂时9：绩效,没填为招聘
		Rpc( {
			functionId : 'ZP0000002343',
			success : Global.toLoad
		}, map);
	 }
}
 
 
//截取30个字节长度的字符串
 Global.cut_str = function (str, len){
     var char_length = 0;
     for (var i = 0; i < str.length; i++){
         var son_str = str.charAt(i);
         encodeURI(son_str).length > 2 ? char_length += 1 : char_length += 0.5;
         if (char_length >= len){
             var sub_len = char_length == len ? i+1 : i;
             return str.substr(0, sub_len);
         }
     }
 }

//回调函数
Global.toLoad = function(response){
	var value = response.responseText;
	var map = Ext.decode(value);
	Ext.getCmp('tempalteId').setValue(map.tempalteId);
	document.getElementById('buttonSave').innerHTML = '编辑';
	Ext.getCmp('buttonPanelId').setVisible(false);
	Ext.getCmp('formulaAttachId').setVisible(true);
	//email_array='';
    formula_array='';
//	Ext.MessageBox.alert("提示信息","保存成功");
	
	//window.location.href='/recruitment/emailtemplate/emailTemplateList.do?b_query=link';
};
/*返回 ，返回模板主列表页面*/
Global.cancel = function(){
	var content = Ext.getCmp('contentId').getValue();
	if(content!='' && email_array!='' && formula_array!=''){
		Ext.Msg.confirm("提示信息","有尚未保存的数据，确认返回主页？",function(btn){ 
			if(btn=="yes"){ 
				// 确认触发，继续执行后续逻辑。 
				window.location.href='/recruitment/emailtemplate/emailTemplateList.do?b_query=ret';//返回的时候不通过传参，直接去getFormHM取需要的参数
			} 
		});
	}else{
		window.location.href='/recruitment/emailtemplate/emailTemplateList.do?b_query=ret';
	}
	//window.history.go(-1);
}

  function isIE() { //ie?  
    if (!!window.ActiveXObject || "ActiveXObject" in window)  
        return true;  
    else  
        return false;  
 } 
  