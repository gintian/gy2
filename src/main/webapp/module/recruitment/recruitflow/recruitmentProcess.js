var Global = new Object();
Global.flowid=undefined;
Global.position = undefined;//拖动位置
Global.searchHj = function(flowid) {
	var clientWid = document.body.clientWidth;
	Global.flowid=flowid;
	var map = new HashMap();
	map.put("flowid", flowid);
	map.put("clientWid", clientWid);
	Rpc( {
		functionId : 'ZP0000002312',
		success : Global.searchZphj
	}, map);
}
Global.setDragParams = function(obj){
	obj.tableConfig.viewConfig = {
			plugins:{
				id:'dragDrop',
				ptype:'gridviewdragdrop',
				dragText:'拖动调整顺序'
			},
			listeners:{
				beforedrop:function(node,data,overModel,dropPosition,dropFunction){
					var overNode = overModel.get("nodeid");
					var a=true;
					/**
					 * 获取拖动节点最大和最小序号
					 */
					var minSeq = 0;
					var maxSeq = 0;
					Ext.each(data.records,function(record,index){
						var temSeq = parseInt(record.data.seq);
						if(index==0){
							minSeq = temSeq;
							maxSeq = temSeq;
						}else if(temSeq>maxSeq)
							maxSeq = temSeq;
						else if(temSeq<minSeq)
							minSeq = temSeq;
					});
					/**
					 * 判断拖动位置（因Ext自带拖动提供的dropPosition是根据光标位置进行判断的，不能满足这的需求）
					 */
					var overSeq = parseInt(overModel.data.seq);
					if(minSeq>overSeq)
						dropPosition = "before";
					else if(maxSeq<overSeq)
						dropPosition = "after";
					Global.position = dropPosition;
					
					if(dropPosition=="before"){
						Ext.each(data.records,function(record){
							/*var moveNode = record.get("nodeid");
							if(parseInt(moveNode)>parseInt(overNode)){
								a=false;
								return false;
							}*/
							var moveNode = record.get("nodeid");
							var xh = overModel.get("seq");
							if(xh == 1 && "01,02".indexOf(moveNode) == -1){
								a=false;
								return false;
							}
						});
					}
					if(dropPosition=="after"){
						Ext.each(data.records,function(record){
							/*var moveNode = record.get("nodeid");
							if(parseInt(moveNode)<parseInt(overNode)){
								a=false;
								return false;
							}*/
							var xh = overModel.get("sysName");
							if(xh == "入职"){
								a=false;
								return false;
							}
						});
					}
					return a;
				},
				drop:function(node,data,overModel,dropPosition,eOpts){
					dropPosition = Global.position;
					var dragIds = new Array();
					var dragSeqs = new Array();
					var leftSeqs = new Array();
					var leftIds = new Array();
					//var overId = Ext.getDom(overModel.data.seq).nextSibling.value;
					var seq = overModel.data.seq;
					var upSeq = parseInt(seq);
					var maxseq = 0;
					var minseq=0;
					Ext.each(data.records,function(record,index){
						if(parseInt(record.data.seq)>maxseq)
							maxseq=parseInt(record.data.seq);
						if(index==0)
							minseq=parseInt(record.data.seq);
						else if(parseInt(record.data.seq)<minseq)
							minseq=parseInt(record.data.seq);
						dragIds[index]=Ext.getDom(record.get("seq")).nextSibling.value;
						dragSeqs[index]=record.data.seq;
						
					});
					
					var inde = 0;
					var updateSeqs = new Array();
					var store = Ext.data.StoreManager.lookup("rzRegister_dataStore");
					/**
					 * 由于下面迭代更改了序号，所以在更改序号前获取到id和 获取未拖动节点需更新的序号（增加或减少数值）
					 */
					store.each(function(record){
						var tem = Ext.getDom(record.get("seq")).nextSibling.value;
						if(!Ext.Array.contains(dragIds,tem)){//循环非拖动record
							leftIds[inde]=Ext.getDom(record.data.seq).nextSibling.value;
							var temSeq=0;
							var currentSeq = parseInt(record.get("seq"));
							for(var i=0;i<dragSeqs.length;i++){
								if(dropPosition=="after"){//计算当前节点后面有几个拖动的节点,往前拖动
									if(currentSeq<minseq||currentSeq>upSeq){//除去在拖动节点位置之前和拖动位置之后的节点
										continue;}
									if(currentSeq>parseInt(dragSeqs[i])){
										temSeq+=1;
									}
								}
								if(dropPosition=="before"){//计算当前节点后面有几个拖动的节点,往前拖动
									if(currentSeq>maxseq||currentSeq<upSeq)//除去在拖动节点位置之前和最大节点之后的节点
										continue;
									if(currentSeq<parseInt(dragSeqs[i]))
										temSeq+=1;
								}
							}
							leftSeqs[inde]=record.data.seq;
							updateSeqs[inde]=temSeq;
							inde+=1;
						}
					});
					/**
					 * 手动更新前台store中未拖动节点的序号和valid
					 
					inde=0;
					store.each(function(record,index){
						if(Ext.Array.contains(leftSeqs,record.data.seq)){//循环非拖动record
							var currentSeq = parseInt(leftSeqs[inde]);
							var temSeq = parseInt(updateSeqs[inde]);
							var splitStr = "id=\""+record.data.seq+"\"";
							if(dropPosition=="before"){
								//更新序号
								var seqRes = currentSeq+temSeq;
								record.set("seq",seqRes+"");
								//更新隐藏域对应的id
								var temp1 = record.get("valid").split(splitStr)[0];
								var temp2 = record.get("valid").split(splitStr)[1];
								record.set("valid",temp1+"id=\""+seqRes+"\""+temp2);
							}else{
								//更新序号
								var seqRes = currentSeq-temSeq;
								record.set("seq",seqRes+"");
								//更新隐藏域对应的id
								var temp1 = record.get("valid").split(splitStr)[0];
								var temp2 = record.get("valid").split(splitStr)[1];
								record.set("valid",temp1+"id=\""+seqRes+"\""+temp2);
							}
							inde+=1;
						}
					});*/
					
					/**
					 * 更新前台store中的json值
					 */
					var dragUpSeqs = new Array();
					Ext.each(data.records,function(record,index){
						var tempSe;
						if(dropPosition=="before"){
							tempSe = upSeq;
						}
						else{
							var seqRes = upSeq-data.records.length+1;
							tempSe = seqRes;
						}
						upSeq+=1;
						dragUpSeqs[index]=tempSe;
					});
					var map = new HashMap();
					map.put("dragIds",dragIds);
					map.put("leftIds",leftIds);
					map.put("dragUpSeqs",dragUpSeqs);
					map.put("updateSeqs",updateSeqs);
					map.put("position",dropPosition);
			    	map.put("flowid", Global.flowid);
			    	Rpc( {
			    		functionId : 'ZP0000002326',
			    		success : Global.upSuccess
			    	}, map);
					
				}
			}
	}
}
Global.searchZphj = function(response) {
	var value = response.responseText;
	var map = Ext.decode(value);
	var rzColumn = Ext.decode(map.rzColumn);
	var rzValue = Ext.decode(map.rzValue);
	//是否上级流程
	var isParent = map.isParent;
	
	var configs;
	if(isParent == "true")
		configs = {
				prefix : "rzRegister",
				editable : true,
				selectable : true,
				tdMaxHeight:85,
				storedata : rzValue,
				tablecolumns : rzColumn,
				datafields : [ 'seq', 'sysName', 'custom_name','remark','org_flag', 'valid' ,'nodeid']
			};
	else
		configs = {
				prefix : "rzRegister",
				editable : true,
				selectable : true,
				tdMaxHeight:85,
				storedata : rzValue,
				tablecolumns : rzColumn,
				clickToEdit:2,
				beforeBuildComp:Global.setDragParams,
				datafields : [ 'seq', 'sysName', 'custom_name','remark','org_flag','valid' ,'nodeid']
		};
	
	var rzRegister = new BuildTableObj(configs);
	var table = rzRegister.getMainPanel();
	rzRegister.tablePanel.manageHeight=true;
	rzRegister.tablePanel.minHeight=200;
	rzRegister.renderTo('zphj');
	//定义表格数据model
	Ext.define('TG.model.DataModel',{
		extend:'Ext.data.Model'
	});
}

Global.upSuccess= function(response){
	var result = Ext.decode(response.responseText);
	var records = result.records;
	Global.reloadData(records);
}
Global.rerender = function(value){
	return value;
}
Global.refreshTable=function(){
	var store =Ext.data.StoreManager.lookup("rzRegister_dataStore");
	store.commitChanges();
	//store.reload();
	//Ext.getCmp("rzRegister_tablePanel").view.refresh();
}
/**
 * 点击上移下移
 */
Global.move=function(flag){
	var table = Ext.getCmp("rzRegister_tablePanel");
	var selectRecord = table.getSelectionModel().getSelection();
	var store = Ext.data.StoreManager.lookup("rzRegister_dataStore");
	var totalRec = store.data.length;
	var othLinkId = "";
	if(selectRecord.length<=0){
		Ext.Msg.alert("提示信息","请选择需要移动的记录");
		return;
	}
	if(selectRecord.length>1){
		Ext.Msg.alert("提示信息","请选择一条需要移动的记录");
		return;
	}
	if(selectRecord[0].data.seq=="1"&&flag=="1"){
		Ext.Msg.alert("提示信息","已是第一条记录");
		return;
	}
	if(selectRecord[0].data.seq==totalRec&&flag=="0"){
		Ext.Msg.alert("提示信息","已是最后一条记录");
		return;
	}
	var moveNodeId = parseInt(selectRecord[0].data.nodeid);
	var seq = selectRecord[0].data.seq;
	var msg="";
	if(flag=="1"){
		var preRecord = store.getAt(parseInt(seq)-1-1);
		if(parseInt(preRecord.data.nodeid)<moveNodeId){
			msg = "不能进行上移";
		}
		othLinkId = Ext.getDom(parseInt(selectRecord[0].data.seq)-1+"").nextSibling.value;
	}
	else{
		var nextRecord = store.getAt(parseInt(seq));
		if(parseInt(nextRecord.data.nodeid)>moveNodeId){
			msg = "不能进行下移";
		}
		othLinkId = Ext.getDom(parseInt(selectRecord[0].data.seq)+1+"").nextSibling.value;
	}
	if(msg!=""){
		return;
	}
	var map = new HashMap();
	map.put("seq",seq);
	map.put("flag",flag);
	map.put("othLinkId",othLinkId);
	map.put("linkid", Ext.getDom(selectRecord[0].data.seq).nextSibling.value);
	Rpc( {
		functionId : 'ZP0000002327',
		success : Global.moveSuccess
	}, map);
}
Global.moveSuccess=function(response){
	Ext.get("zphj").setHTML("");
	Global.searchHj(Global.flowid);
}
Global.opSuccess = function(flag,records,seq){
	if(flag=="del"){
		/*store.each(function(record){
			var seq = parseInt(record.data.seq);
			var count=0;
			Ext.each(globalTemp,function(rec,index){
				var delSeq = parseInt(rec.data.seq);
				if(seq>delSeq){
					count+=1;	
				}
			});
			var res = (seq-count)+"";
			var splitStr = "id=\""+record.data.seq+"\"";
			//更新隐藏域对应的id
			var temp1 = record.get("valid").split(splitStr)[0];
			var temp2 = record.get("valid").split(splitStr)[1];
			record.set("valid",temp1+"id=\""+res+"\""+temp2);
			
			record.set("seq",res);
		});
		store.remove(globalTemp);
		store.commitChanges();
		Ext.getCmp("rzRegister_tablePanel").view.refresh();*/
		Global.reloadData(records);
	}
	if(flag=="insert"){
		Global.reloadData(records);
	}
}
/**
 * 重新加载表格 
 */
Global.reloadData = function(records){
	var store =Ext.data.StoreManager.lookup("rzRegister_dataStore");
	var totalRecords = records.substring(1,records.length-1).split("≮");
	store.removeAll();
	
	Ext.each(totalRecords,function(temp,index){
		var obj = Ext.decode(temp);
		var newRecord = new TG.model.DataModel();
		var data = newRecord.data;
    	Ext.apply(data,obj);
    	store.insert(index,newRecord);
	});
	Ext.getCmp("rzRegister_tablePanel").view.refresh();
}

