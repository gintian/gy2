var Global = new Object();
Global.clientWid=document.body.clientWidth;
Global.globalTemp;
Global.position = undefined;//拖动位置
Global.isParentFlow;//是否是上级流程
Global.hadInfoType1 = new Array();//角色
Global.hadInfoType2 = new Array();//岗位
Global.hadInfoType3 = new Array();//招聘成员
Global.hadInfoMem = new Array();//招聘成员
Global.indexAd1 = 0;//当前人数
Global.indexAd2 = 0;
Global.indexAd3 = 0;
Ext.Loader.setPath("EHR","/components");
Global.searchHj = function(nodeid,linkid,isParent) {
	var map = new HashMap();
	map.put("nodeid", nodeid);
	map.put("linkid", linkid);
	map.put("isParent", isParent);
	Global.isParentFlow = isParent;
	map.put("clientWid", Global.clientWid);
	Rpc( {
		functionId : 'ZP0000002320',
		success : Global.searchHjcz
	}, map);
}
Global.regName = function(value){
	var reg = /^[a-zA-Z]+[a-zA-Z0-9]*$/;
	if(reg.test(value))
		return true;
	else
		return "输入格式不正确";
}

Global.searchHjcz = function(response) {
	var value = response.responseText;
	var map = Ext.decode(value);
	var rzColumn = Ext.decode(map.rzColumn);
	var rzValue = Ext.decode(map.rzValue);
	var configs = {
		prefix : "rzRegister",
		editable : true,
		selectable : false,
		storedata : rzValue,
		clickToEdit:2,
		tablecolumns : rzColumn,
//		beforeBuildComp:Global.setDragHjzt,
		datafields : [ 'seq', 'sysName', 'custom_name', 'resume_modify', 'valid' ]
	};
	var rzRegister = new BuildTableObj(configs);
	var table = rzRegister.getMainPanel();
	rzRegister.renderTo('zphj');
}
Global.setDragHjzt = function(obj){
	obj.tableConfig.viewConfig = {
		plugins:{
			ptype:'gridviewdragdrop',
			dragText:'拖动调整顺序'
		},
		listeners:{
			drop:function(node,data,overModel,dropPosition,eOpts){
			var store = Ext.data.StoreManager.lookup("rzRegister_dataStore");
			var index = 1;
			store.each(function(rec){
				var splitStr = "id=\""+rec.data.seq+"seq\"";
				var splitStr2 = "id=\""+rec.data.seq+"\"";
				var temp1 = rec.get("resume_modify").split(splitStr)[0];
				var temp2 = rec.get("resume_modify").split(splitStr)[1];
				var temp3 = rec.get("valid").split(splitStr2)[0];
				var temp4 = rec.get("valid").split(splitStr2)[1];
				rec.set("resume_modify",temp1+"id=\""+index+"seq\""+temp2);
				rec.set("valid",temp3+"id=\""+index+"\""+temp4);
				rec.set("seq",index+"");
				index++;
			});
			var map = new HashMap();
			store.each(function(rec){
				if(Ext.getDom(rec.data.seq+"seq").nextSibling.value){
					id=Ext.getDom(rec.data.seq+"seq").nextSibling.value;
					seq=rec.data.seq;
					map.put(id,seq);
				}
			});
			var map1 = new HashMap();
			map1.put("seqs",map)
			map1.put("hjzt","true");
	    	Rpc( {
	    		functionId : 'ZP0000002351',
	    		success : Global.refreshTable("rzRegister")
	    	}, map1);
			}
		}
	}
}
Global.searchFunc = function(linkid,isParent) {
	var map = new HashMap();
	map.put("linkid", linkid);
	map.put("clientWid", Global.clientWid);
	map.put("isParent",isParent);
	Rpc( {
		functionId : 'ZP0000002324',
		success : Global.searchFucmc
	}, map);
}
Global.setDragParams = function(obj){
	obj.tableConfig.viewConfig = {
		plugins:{
			ptype:'gridviewdragdrop',
			dragText:'拖动调整顺序'
		},
		listeners:{
			beforedrop:function(node,data,overModel,dropPosition,eOpts){
				if(Ext.getDom(overModel.data.seq+"fuc").nextSibling.value=="")//排除和新增未保存记录的拖动
					return false;
				var seq = parseInt(data.records[0].data.seq,10);
				/**
				 * 判断拖动位置（因Ext自带拖动提供的dropPosition是根据光标位置进行判断的，不能满足这的需求）
				 */
				var overSeq = parseInt(overModel.data.seq);
				if(seq>overSeq)
					dropPosition = "before";
				else if(seq<overSeq)
					dropPosition = "after";
				Global.position = dropPosition;
			},
			drop:function(node,data,overModel,dropPosition,eOpts){
				dropPosition = Global.position;
				var record = data.records[0];
				var dragIds = Ext.getDom(record.get("seq")+"fuc").nextSibling.value;
				var dragSeqs = record.data.seq;
				var seq = overModel.data.seq;
				var leftSeqs = new Array();
				/**
				 * 更新前台store中的未拖动节点json值
				 */
				var index=1;
				var store = Ext.data.StoreManager.lookup("rzRegister2_dataStore");
				store.each(function(rec){
					var splitStr = "id=\""+rec.data.seq+"fuc\"";
					var temp1 = rec.get("valid").split(splitStr)[0];
					var temp2 = rec.get("valid").split(splitStr)[1];
					rec.set("valid",temp1+"id=\""+index+"fuc\""+temp2);
					rec.set("seq",index+"");
					index++;
				});
				
				var map = new HashMap();
				store.each(function(rec){
					if(Ext.getDom(rec.data.seq+"fuc").nextSibling.value){
						id=Ext.getDom(rec.data.seq+"fuc").nextSibling.value;
						seq=rec.data.seq;
						map.put(id,seq);
					}
				});
				var map1 = new HashMap();
				map1.put("seqs",map)
		    	Rpc( {
		    		functionId : 'ZP0000002351',
		    		success : Global.refreshTable
		    	}, map1);
				
			}
		}
	}
}
Global.searchFucmc = function(response) {
	var value = response.responseText;
	var map = Ext.decode(value);
	var rzColumn = Ext.decode(map.rzColumn);
	var rzValue = Ext.decode(map.rzValue);
	var configs = {
		prefix : "rzRegister2",
		editable : true,
		selectable : false,
		storedata : rzValue,
		clickToEdit:2,
		tablecolumns : rzColumn,
		datafields : [ 'seq', 'sysName','custom_name','methodName','ownflag',  'valid' ],
		beforeBuildComp:Global.setDragParams
	};
	//如果是上级流程屏蔽拖动
	if(Global.isParentFlow == "true")
		configs = {
					prefix : "rzRegister2",
					editable : false,
					selectable : false,
					storedata : rzValue,
					//clickToEdit:2,
					tablecolumns : rzColumn,
					datafields : [ 'seq', 'sysName','custom_name','methodName','ownflag',  'valid' ]
				  };
	var rzRegister = new BuildTableObj(configs);
	var table = rzRegister.getMainPanel();
	//var tablegrid = Ext.getCmp("rzRegister2_tablePanel");
	rzRegister.renderTo('funcs');
	//定义表格数据model
	Ext.define('TG.model.DataModel',{
		extend:'Ext.data.Model'
	});
}
Global.refreshTable=function(rzRegister){
	if("rzRegister"==rzRegister){
		var store =Ext.data.StoreManager.lookup("rzRegister_dataStore");
		store.commitChanges();
		Ext.getCmp("rzRegister_tablePanel").view.refresh();
	}else{
		var store =Ext.data.StoreManager.lookup("rzRegister2_dataStore");
		store.commitChanges();
		Ext.getCmp("rzRegister2_tablePanel").view.refresh();
	}
}
//保存流程环节状态
Global.updateStatus = function(){
	var updateR = Ext.data.StoreManager.lookup("rzRegister_dataStore").getUpdatedRecords();//获取table的Store对象中被更改但还未通过代理同步的数据（Model对象）
	if(updateR.length==0){
		return;
	}
		var ids = new Array();
		var seqs = new Array();
		var custom_names = new Array();
		var valids = new Array();
		var resume_modifys = new Array();
		Ext.each(updateR,function(record,index){//record对应每条变更的记录
			record.commit();
			seqs[index]=record.get("seq");
			custom_names[index]=record.get("custom_name");
			valids[index]=Ext.getDom(record.get("seq")).value;
			resume_modifys[index]=Ext.getDom(record.get("seq")+"seq").value;
			ids[index]=Ext.getDom(record.get("seq")).nextSibling.value;
		});
	    
		var hashvo = new ParameterSet();
		hashvo.setValue("ids",ids);
		hashvo.setValue("seqs",seqs);
		hashvo.setValue("custom_names",custom_names);
		hashvo.setValue("valids",valids);
		hashvo.setValue("resume_modify",resume_modifys);
		var request = new Request({method:"post",asynchronous:true,onSuccess:Global.upSuccess,functionId:"ZP0000002323"},hashvo);
}
//保存流程环节功能
Global.updateFuc = function(linkid){
	var store = Ext.data.StoreManager.lookup("rzRegister2_dataStore");
	/*
	 * 对新增的记录进行非空校验
	 */
	var addRecords = store.getNewRecords();
	var updateR = store.getModifiedRecords();//获取table的Store对象中所有新添加或被修改的records数据记录集. 
	if(!updateR||updateR.length==0){
		return;
	}
		var msg="";
		Ext.each(updateR,function(record){
			if(Ext.isEmpty(record.data.methodName)||Ext.isEmpty(record.data.custom_name))
				msg="第"+(record.data.seq)+"行操作按钮用户名称和方法名不能为空";
			if(!Ext.isEmpty(msg))
				return false;
		});
		if(!Ext.isEmpty(msg)){
			Ext.Msg.alert("提示信息",msg);
			return;
		}
		var ids = new Array();
		var seqs = new Array();
		var methodNames = new Array();
		var custom_names = new Array();
		var valids = new Array();
		Ext.each(updateR,function(record,index){//record对应每条变更的记录
			record.commit();
			seqs[index]=record.get("seq");
			methodNames[index]=record.get("methodName");
			custom_names[index]=record.get("custom_name");
			valids[index]=Ext.getDom(record.get("seq")+"fuc").value;
			ids[index]=Ext.getDom(""+record.get("seq")+"fuc").nextSibling.value;
		});
		var map = new HashMap();	
    	
    	map.put("ids",ids);
    	map.put("linkid",linkid);
    	map.put("seqs",seqs);
    	map.put("methodNames",methodNames);
    	map.put("custom_names",custom_names);
    	map.put("valids",valids);
    	Rpc( {
    		functionId : 'ZP0000002325',
    		success :Global.upFucSuccess
    	}, map);
}
Global.upSuccess= function (outparam){
	var msg = outparam.getValue("message");
	if(msg=="success"){
		//Ext.Msg.alert("操作提示","保存成功",function(){
			Ext.getCmp("rzRegister_tablePanel").view.refresh();//刷新table
		//});
	}
	if(msg=="failure"){
		Ext.Msg.alert("操作提示","保存失败",function(){
			Ext.getCmp("rzRegister_tablePanel").view.refresh();//刷新table
		});
	}
}

Global.isEdit=function(re){
	var ownflag = re.get("ownflag");
	if(ownflag==1){
		return false;
	}else{
		return true;
	}
}

Global.upFucSuccess= function (response){
	var result =Ext.decode(response.responseText);
	var msg = result.msg;
	if(msg=="success"){
		//Global.opSuccess(result.jsonStr);
		//Ext.getCmp("rzRegister2_tablePanel").view.refresh();//刷新table
	}
	if(msg=="failure"){
		Ext.Msg.alert("操作提示","保存失败",function(){
			Ext.getCmp("rzRegister2_tablePanel").view.refresh();//刷新table
		});
	}
}
Global.rerender = function(value){
	return value;
}
Global.addFunc = function(){
	var store =Ext.data.StoreManager.lookup("rzRegister2_dataStore");
	var seq = store.getCount();
	var record = store.getAt(seq-1);
	if(record)
		var id = Ext.getDom(""+record.data.seq+"fuc").nextSibling.value;
	
	if(record && Ext.isEmpty(id)){
		//防止上一条新增记录未保存就进行添加另一个记录
		Ext.Msg.alert("提示信息","已存在未编辑保存的新增按钮");
		return;
	}
	store.add({seq:seq+1,sysName:"自定义按钮",ownflag:"0",custom_name:"",valid:"<input type=\"checkbox\" onclick=\"change(this,0)\" id=\""+(seq+1)+"fuc\" value=\"1\" checked=\"checked\"/><input type=\"hidden\" value=''/>"});
}
/**
 * 删除可用操作
 */
Global.delFunc = function(linkid){
	var selectR = Ext.getCmp("rzRegister2_tablePanel").getSelectionModel().getSelection();
	if(selectR.length==0){
		Ext.Msg.alert("提示信息","请选择需要删除的记录");
		return false;
	}
	var record = selectR[0];
	/**
	 * 系统项不能删除
	 */
	if(record.data.ownflag=="1"){
		Ext.Msg.alert("提示信息","不能删除系统内置操作按钮");
		return;
	}
	Global.globalTemp=record;
	var id = Ext.getDom(""+record.data.seq+"fuc").nextSibling.value;
	/*
	 * 针对新增未保存记录删除
	 */
	if(Ext.isEmpty(id)){
		var store = Ext.data.StoreManager.lookup("rzRegister2_dataStore");
		var temIndex = parseInt(record.data.seq);
		
		var records = store.getRange(temIndex,store.getCount());//获得所有删除记录后面的数据
		Ext.each(records,function(rec){
			var tem = parseInt(rec.data.seq);
			var splitStr = "id=\""+tem+"fuc\"";
			//更新隐藏域对应的id
			var temp1 = rec.get("valid").split(splitStr)[0];
			var temp2 = rec.get("valid").split(splitStr)[1];
			rec.set("valid",temp1+"id=\""+(tem-1)+"fuc\""+temp2);
			rec.set("seq",(tem-1)+"");
		});
		store.remove(record);
		store.commitChanges();
		Ext.getCmp("rzRegister2_tablePanel").view.refresh();
		return;
	}
	/*
	 * 针对已保存的记录
	 */
	Ext.Msg.confirm("提示信息","确认删除选中的操作吗?",function(res){
		if(res=="yes"){
			var map = new HashMap();
			map.put("id",id);
			map.put("seq", record.data.seq);
			map.put("linkid",linkid);
	    	Rpc( {
	    		functionId : 'ZP0000002360',
	    		success : Global.delSuccess
	    	}, map);
		}else{
			return;
		}
	});
}
Global.delSuccess=function (response){
	var result =Ext.decode(response.responseText);
	var message = result.msg;
	if(message=="删除失败!"){
		Ext.Msg.alert("提示信息","删除失败!");
		return false;
	}else if(message=="删除成功!"){
		Ext.Msg.alert("提示信息","删除成功",function(){
			Global.opSuccess(result.jsonStr);
		});
	}
}
Global.opSuccess = function(records){
	var store =Ext.data.StoreManager.lookup("rzRegister2_dataStore");
	var totalRecords = records.substring(1,records.length-1).split(";");
	store.removeAll();
	Ext.each(totalRecords,function(temp,index){
		var obj = Ext.decode(temp);
		var newRecord = new TG.model.DataModel();
		var data = newRecord.data;
    	Ext.apply(data,obj);
    	store.insert(index,newRecord);
	});
	Ext.getCmp("rzRegister2_tablePanel").view.refresh();
}

Global.addPerson = function(btn,linkid){
	if("addA1"==btn.id){//选角色
		Ext.require('EHR.rolepicker.RolePicker', function(){          
			Ext.create('EHR.rolepicker.RolePicker',
					{callBackFunc:function(records){
						var map = new HashMap();
						map.put("type", "1");
						map.put("infoList", records);
						for(var j = 0; j < records.length; j++){
							var obj = records[j];
							for (var i = 0; i < Global.hadInfoType1.length; i++) {
								var had_roleid = Global.hadInfoType1[i];
								if(had_roleid == obj.role_id_e){
									Ext.showAlert(obj.role_name+EXIST_POS);
									return;
								}
							}
						};
						map.put("hadInfo", Global.hadInfoType1);
						map.put("linkid", linkid);
				    	Rpc({functionId : 'ZP0000002329',success : Global.updSuccess}, map);},
				    multiple:true});
		},this);
	}else if("addA2"==btn.id){//选岗位
		var picker = new PersonPicker({
			addunit:false,
			adddepartment:false,
			addpost:true,
			multiple: true,
			text:'添加岗位',
			deprecate: Global.hadInfoType2,
			isPrivExpression:false,
			callback: function (cm) {
				var map = new HashMap();
				map.put("type", "2");
				map.put("infoList", cm);
				map.put("hadInfo", Global.hadInfoType2);
				map.put("linkid", linkid);
		    	Rpc({functionId : 'ZP0000002329',success : Global.updSuccess}, map);}
		}, btn);
		picker.open();
		
	}else if("addA3"==btn.id){//选人
		var picker = new PersonPicker({
			multiple: true,
			deprecate: Global.hadInfoType3,
			isPrivExpression:false,
			callback: function (cm) {
				var map = new HashMap();
				map.put("type", "3");
				map.put("infoList", cm);
				map.put("hadInfo", Global.hadInfoType3);
				map.put("linkid", linkid);
		    	Rpc({functionId : 'ZP0000002329',success : Global.updSuccess}, map);}
		}, btn);
		picker.open();
	}
};
//查询是否有param
Global.jugeArray =function(arr,param){
	var temp = -1;
	for(var i = 0;i<arr.length;i++){
		if(arr[i]==param){
			temp = i;
			break;
		}
	}
	return temp;
};

Global.updSuccess = function(outparamters){
	var value = outparamters.responseText;
	var map = Ext.decode(value);
	if(map.search){
		Global.show("1",map.roleList,map.hadrole,map.linkid);
		Global.show("2",map.posList,map.hadpos,map.linkid);
		Global.show("3",map.empList,map.hademp,map.linkid);
		Global.hadInfoMem = map.hadmem;
		for(var i=0;i<Global.hadInfoMem.length;i++){
			var id = Global.hadInfoMem[i];
			Ext.getDom("zp"+id).checked=true;
		}
	}else{
		Global.show(map.type,map.list,map.hadInfo,map.linkid);
	}
	
};

Global.show = function(type,list,hadInfo,linkid){
	if("1"==type){
		Global.hadInfoType1 = hadInfo; 
	}
	else if("2"==type){
		Global.hadInfoType2 = hadInfo; 
	}
	else if("3"==type){
		Global.hadInfoType3 = hadInfo;
	}
	for(var i=0;i<list.length;i++){
		var elem3 = "";
		var divid = "";
		var photo = "";
		var info = list[i];
		var elem1 = Ext.getDom("addTd"+type);
		var elem2 = Ext.getDom("addA"+type);
		if("1"==type){
			Global.indexAd1++;
			divid = "diva"+Global.indexAd1;
			photo = "../../images/role.png";
		}else if("2"==type){
			Global.indexAd2++;
			divid = "divb"+Global.indexAd2;
			photo = "../../images/post.png";
		}else if("3"==type){
			Global.indexAd3++;
			divid = "divc"+Global.indexAd3;
			photo = info.photo;
		}
		elem3 = document.createElement("div");
		if("3"==type){
			elem3.className="hj-nmd-dl";
		}else{
			elem3.className="hj-nmd-dl2";
		}
    	var memid = info.id;
    	var name = info.name;
    	var mousHtml = 'onmouseover="Global.toRemove(\''+divid+'\')" onmouseleave="Global.toChan(\''+divid+'\')"';
    	var html='<img id="'+divid+'" class="deletePic" '+'onclick=\'Global.deleteP'+'(this,"'+memid+'","'+type+'","'+linkid+'")\''+' fullName="'+name+'" style="width: 20px; height: 20px;display:none;" src="../../workplan/image/remove.png" />';
    	var innerHtml = '<dl '+mousHtml+'><dt title="'+name+'">';
    	if("3"==type){
    		innerHtml =innerHtml+'<img class="img-circle" src="'+photo+'" />';
    	}else{
    		innerHtml =innerHtml+'<img src="'+photo+'" />';
    	}
    	innerHtml =innerHtml+html+'</dt><dd>'+name+'</dd></dl>';
    	elem3.innerHTML=innerHtml;
//    	elem1.insertBefore(elem3,elem2);
    	if(elem2!=null)
    		elem1.insertBefore(elem3,elem2);
    	else
    		elem1.appendChild(elem3);
	}
}

Global.toRemove=function(par){
	var a =Ext.getDom(par);
	a.style.display="";
};

Global.toChan=function(par) {
	var a =Ext.getDom(par);
	a.style.display="none";
};

showCzr=function(linkid){
	var map = new HashMap();
	map.put("linkid",linkid);
	Rpc({functionId : 'ZP0000002330',success : Global.updSuccess}, map);
}

Global.deleteP=function(elem,id,type,linkid){
	Ext.Msg.confirm("提示信息","确认要删除成员吗?",function(btn){ 
		if(btn=="yes"){ 
			var hadInfo = new Object();
			var addtdelem = Ext.getDom("addTd"+type);
			var b =elem.parentNode.parentNode.parentNode;
			var arrNode = b.childNodes;
			if("1"==type){
				Global.hadInfoType1.remove(id);
				hadInfo = Global.hadInfoType1;
			}
			else if("2"==type){
				Global.hadInfoType2.remove(id);
				hadInfo = Global.hadInfoType2;
			}
			else if("3"==type){
				Global.hadInfoType3.remove(id);
				hadInfo = Global.hadInfoType3;
			}
			addtdelem.removeChild(b);
			var map = new HashMap();
			map.put("hadInfo", hadInfo);
			map.put("type", type);
			map.put("function", "del");
			map.put("linkid", linkid);
			Rpc({functionId : 'ZP0000002329'}, map);
		} 
	});
};

Global.checkfun=function(obj,linkid){
	var id = obj.id.substring(2);
	var map = new HashMap();
	if(obj.checked)
		Global.hadInfoMem.push(id);
	else
		Global.hadInfoMem.remove(id);
	map.put("hadInfo", Global.hadInfoMem);
	map.put("linkid", linkid);
	map.put("type", "check");
	Rpc({functionId : 'ZP0000002329',success : Global.updSuccess}, map);
	
}