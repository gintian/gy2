var Global = new Object();
Global.fromName='通知模板';
//查询通知模板类别
Global.getSubModuleName = function(subModule){
	if("10"==subModule){
		subModule="接受职位申请通知";
	}else if("11"==subModule){
		subModule="拒绝职位申请通知";
	}else if("20"==subModule){
		subModule="面试安排通知（申请人）";
	}else if("30"==subModule){
		subModule="面试安排通知（面试官）";
	}else if("40"==subModule){
		subModule="面试通知（通过）";
	}else if("50"==subModule){
		subModule="面试通知（淘汰）";
	}else if("60"==subModule){
		subModule="Offer";
	}else if("70"==subModule){
		subModule="入职通知（管理人员）";
	}else if("80"==subModule){
		subModule="简历评价通知（评价人）";
	}else if("81"==subModule){
		subModule="转发简历通知";
	}else if("82"==subModule){
		subModule="简历中心通知";
	}else if("90"==subModule){
		subModule="其它通知模板";
	}else if("91"==subModule){
		subModule="职位推荐模板";
	}else if("92"==subModule){
		subModule="招聘批次通知模板";
	}
	return subModule;
}
Global.showOwnflag=function(ownflag){
	if(ownflag=='1'){
		ownflag='系统内置';
	}
	if(ownflag=='0'){
		ownflag='用户自定义';
	}
	return ownflag;
}
Global.showCodeItemDesc=function(b0110){
    var codeitemdesc='';
	if(b0110=='' || b0110 == null || b0110 == 'HJSJ'){
		b0110='公共模板';
	}else{
		var hashvo=new ParameterSet();
		hashvo.setValue("codeitemid",b0110);
		var request=new Request({
			asynchronous:false,
			onSuccess:function(outparamters){
					b0110 = outparamters.getValue('codeitemdesc');
				} ,
			functionId:'ZP0000002339'},hashvo); 
	}
	return b0110;
}
function trim(str){ //删除左右两端的空格
　　     return str.replace(/(^\s*)|(\s*$)/g, "");
}

//快速查询
Global.fastSearch = function(){
	var elem = Ext.getCmp('boxtext');
	var config = tablegrid.getTableConfig();
	var subModule = elem.getValue();
	subModule = trim(subModule).toLowerCase();
	var arr = new Array();
	var i = 0;
	arr[i] = "1";
	i++;
	if("接受职位申请通知".indexOf(subModule)>=0){
		arr[i] = "10";
		i++;
	}
	if("拒绝职位申请通知".indexOf(subModule)>=0){
		arr[i] = "11";
		i++;
	}
    if("面试安排通知（申请人）".indexOf(subModule)>=0){
		arr[i] = "20";
		i++;
	}
	if("面试安排通知（面试官）".indexOf(subModule)>=0){
		arr[i] = "30";
		i++;
	}
	if("面试通知（通过）".indexOf(subModule)>=0){
		arr[i] = "40";
		i++;
	}
	if("面试通知（淘汰）".indexOf(subModule)>=0){
		arr[i] = "50";
		i++;
	}
	if("Offer".toLowerCase().indexOf(subModule)>=0){
		arr[i] = "60";
		i++;
	}
	if("入职通知（管理人员）".indexOf(subModule)>=0){
		arr[i] = "70";
		i++;
	}
	if("简历评价通知（评价人）".indexOf(subModule)>=0){
		arr[i] = "80";
		i++;
	}
	i = 0;
	 var map = new HashMap();
	map.put("fastText",trim(elem.getValue()));
	map.put("tablekey",config.tablekey);
	map.put("arr",arr);
	Rpc( {
			functionId : 'ZP0000002341',
			success :Global.toLoad
		}, map);
};

//回调函数
Global.toLoad = function(response){
var store = Ext.data.StoreManager.lookup('tablegrid_dataStore');
	store.reload();
};

//新增
Global.insertEmailTemplate = function(param){
	//Ext.getCmp(param.targetid).setDisabled(true);
    window.location.href = '/recruitment/emailtemplate/emailTemplateList.do?b_add=link';
};

//删除
Global.deleteEmailTemplate = function(metaData,Record){
	if(Record.length<=0){
		Ext.MessageBox.alert("提示信息","请勾选模板！");
		return;
	}
	Ext.Msg.confirm("提示信息","确认要删除模板吗？",function(btn){ 
		if(btn=="yes"){ 
		// 确认触发，继续执行后续逻辑。 
		Global.functionOfTemplate(Record);
		} 
	});
};
//添加checkbox
Global.addvalid = function(value,c,record){
	var html = "";
	if(0==value)
		html = "<input type='checkbox' onclick='change(this.id,"+record.data.id+")' id='"+record.data.id+"boxId'>";
	else
		html = "<input type='checkbox' onclick='change(this.id,"+record.data.id+")' id='"+record.data.id+"boxId' checked='checked'>";
	return html;
	
}
function change(id,obj){
	var check = Ext.getDom(id).checked;
	var map = new HashMap();
	map.put("templateid",obj);
	map.put("check", check);
    Rpc({functionId:'ZP0000002341',async:false},map);
}
function indexof(arr,flag){
 var arrflag=''; 
 for(var i in arr)
 {
  if(arr[i]==flag){
 	 arrflag=true;
 	 return arrflag;
  }
 }
  return arrflag;
}
Global.functionOfTemplate =function(Record){
	var arr = new Array();
	var array = new Array();
	var i=0;
	for ( var int = 0; int < Record.length; int++) {
		var temp =Record[int].data;
		array[int] = temp.ownflag;
		if(array[int]=='0'){
			arr[i] = temp.id;
			i++;
		}
	}
	/*
	*当待删除的模板中含有系统内置模板时，给予提示”待删除的模板中包含的系统模板将不会被删除，确定删除？“，如返回值是yes，则删除用户自定义模板，
	*点击取消则不删除；如果待删除模板都是系统内置模板，则给予提示"系统内置模板都不能删除"
	*/
		if(indexof(array,'1')==true){
			if(indexof(array,'0')==true){
				Ext.Msg.confirm("提示信息","待删除的模板中包含的系统模板将不会被删除，确定删除？",function(btn){ 
					if(btn=="yes"){ 
						var store = Ext.data.StoreManager.lookup('tablegrid_dataStore');
						var hashvo=new ParameterSet();
						hashvo.setValue("arr",arr);
						hashvo.setValue("currentPage", store.currentPage);
						hashvo.setValue("pageSize",store.pageSize);
						var request=new Request({asynchronous:false,onSuccess:Global.toLoad ,functionId:'ZP0000002345'},hashvo); 
					} 
				});
			}else{
				Ext.MessageBox.alert("提示信息","系统内置模板不能删除");
			}
		}else{
					var store = Ext.data.StoreManager.lookup('tablegrid_dataStore');
					var hashvo=new ParameterSet();
					hashvo.setValue("arr",arr);
					hashvo.setValue("currentPage", store.currentPage);
					hashvo.setValue("pageSize",store.pageSize);
					var request=new Request({asynchronous:false,onSuccess:Global.toLoad ,functionId:'ZP0000002345'},hashvo); 
		}
	
}


Global.toReload = function(outparamters){
	var store = Ext.data.StoreManager.lookup('tablegrid_dataStore');
	store.currentPage = outparamters.getValue('currentPage');
	store.reload();
	
};
//双击行进入编辑页面
   function actionRow(){
   		var subModule = arguments[1].data.sub_module;
		var name = arguments[1].data.name;
		var returnAddress = arguments[1].data.return_address;
		var subject = arguments[1].data.subject;
		var content = arguments[1].data.content;
		var template_id = arguments[1].data.id;
    //	var html ="/recruitment/emailtemplate/emailTemplateList.do?b_add=link&subModule="+subModule+"&name="+name+"&returnAddress="+returnAddress+"&template_id="+template_id+"&subject="+subject+"&content="+content+"&sign=1";
  		var html ="/recruitment/emailtemplate/emailTemplateList.do?b_add=link&template_id="+template_id+"";
  		return Global.toDisable(this,html);
    }
//编辑
Global.editEmailTemplate = function(value,metaData,Record){
	var subModule = Record.data.sub_module;
	var name = Record.data.name;
	var returnAddress = Record.data.return_address;
	var subject = Record.data.subject;
	var content = Record.data.content;
	var template_id = Record.data.id;
	var store = Ext.data.StoreManager.lookup('tablegrid_dataStore');
	//var html = "/recruitment/emailtemplate/emailTemplateList.do?b_add=link&subModule="+subModule+"&name="+name+"&returnAddress="+returnAddress+"&template_id="+template_id+"&subject="+subject+"&content="+content+"&sign=1";
	var html ="/recruitment/emailtemplate/emailTemplateList.do?b_add=link&template_id="+template_id+"";
	return "<a onclick='Global.toDisable(this,\""+html+"\")' href='javascript:void(0);' >"+value+"</a>";
}

Global.toDisable= function(even,html){
	even.disabled=true;
	window.location.href=html;
};