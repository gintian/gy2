/**
*调用例子：
*var map = new HashMap();//map中存放需要传递的参数
*var ids=Ext.getCmp('textfield1').getValue();
*map.put('batch_id',batchId);
*//*functionId为交易类号，交易类主要作用是加载store，传递数据方式为"this.getFormHM().put("data", list)",fields为['itemid','itemdesc']
*//*items为 页面上已经存在的考场号，allowDel 是否允许对已加载的考场号做删除处理。
*Ext.require('ExamHallUL.AddExamHall', function(){
*	Ext.create("ExamHallUL.AddExamHall",{data:map,functionId:'0000002546',desc:'考场',items:items,allowDel:false,afterfunc:'examhall_me.addExamHall'});
*});
* 
**/
Ext.define('ExamHallUL.AddExamHall',{
	/**需要传递的参数，var map = new HashMap()定义**/
	data:'',
	/**回调函数，参数为value，格式为,itemid`itemdesc**/
	afterfunc:'',
	/**交易类编号**/
	functionId:'',
	/**搜索框及左侧标题栏显示的标题内容**/
	desc:'',
	/**是否允许删除**/
	allowDel:'',
	/**items**/
	items:'',
	/**搜索框中标题**/
	textdesc:'',
	constructor : function(config) {
		addexamhall=this;
		data = config.data;
		functionId = config.functionId;
		desc = config.desc;
		items = config.items;
		allowDel = config.allowDel;
		afterfunc=config.afterfunc;
		textdesc="请输入"+desc+"名称";
		addexamhall.init();
	},
	init : function() {
	     var store = Ext.create('Ext.data.Store', {
			fields:['itemid','itemdesc'],
			proxy:{
				type:'transaction',
			    functionId:functionId,
			    extraParams:{
			    	data:data,
				    items:items,
				    searchtext:''
				},
				reader: {
					type: 'json',
					root: 'data'         	
				}
			}
		});
		store.load();
		var width=600;
		var panel = Ext.create('Ext.grid.Panel', {
			height:288,
			width:width*0.46,
			hideHeaders:true,
			autoScroll:true,
			store:store,
			id:'selectGrid',
			//selType: "checkboxmodel",
			columnLines:true,
			rowLines:true,
			columns: [{ 
				text: '项目名称', 
				dataIndex: 'itemdesc',
				id:'itemdesc',
				sortable:false,//列排序功能禁用
				menuDisabled:true,
				flex:5,
				renderer: function(value, metaData, record, rowIndex, colIndex, store) { 
					//渲染考场
					metaData.style="background: #F2F2F2";
					var itemid = record.data.itemid;
					var idz = "add_"+itemid;
					var id ="doc_"+itemid;
      				var val = '<div id='+id+' style="float:left;height:30px;line-height:30px;" title="'+value+'">'
      				+'<img src="/module/recruitment/image/unit.png" >&nbsp;&nbsp;'+(value.length>14?value.substring(0,14)+'…':value)+'</div>'
      				+'<div style="float:right;height:30px;line-height:30px;"><a id='+idz+' style="display:none">添加</a></div>';
      				return val;
   				}
			},{
				text: 'id', 
				dataIndex: 'itemid',
				id:'itemid',
				sortable:false,//列排序功能禁用
				menuDisabled:true,
				flex:5
			}],
			listeners:{
				'render':function(){
					Ext.getCmp('itemid').hide();
					if(allowDel){
							var arr = items.substring(1).split(",");
							var itemid='';
							var itemdesc='';
							for(var i=0;i<arr.length;i++){
								itemid=arr[i].split("`")[0];
								itemdesc=arr[i].split("`")[1];
								addexamhall.insertIntoPanel(itemid,itemdesc,2);
							}
							
					}
				},
				'itemclick':function(obj,record,item,index){
					var itemid=record.data.itemid;
					var itemdesc = record.data.itemdesc;
					addexamhall.insertIntoPanel(itemid,itemdesc,1);
					//清除grid点击即为选中
					//panel.getSelectionModel().deselect([index]);
				},
				//grid中column的鼠标悬停事件
				'itemmouseenter':function(obj,record,item,index,e){
					item.firstChild.firstChild.style.backgroundColor='#CFE6FF';
					addexamhall.onMouseover(record.data.itemid,1,3);
				},
				'itemmouseleave':function(value,record,item,index){
					item.firstChild.firstChild.style.backgroundColor='#F2F2F2';
					addexamhall.onMouseleave(record.data.itemid,1,3);
				}
			}
		});	
		var win = Ext.create('Ext.Window',{
			title:'请选择',
			width:580,
			height:450,
			layout:'table',
			modal: true,
			id:'addWindow',
			resizable: false,  
			items:[{
				xtype:'panel',
				layout:'column',
				border:false,
				style:'margin-left:10px;margin-top:10px',
				width:width*0.46,
				items:[{
						  xtype:'panel',
						  width:width*0.46,
						  height:32,
						  bodyStyle:'border:1px #5BB0FF solid;',
						  layout:'table',
						  html:'<input id="textfieldKey" value="'+textdesc+'" onfocus="addexamhall.clearTip()" onblur="addexamhall.searchExamHallList();" onkeydown="if(event.keyCode==13){addexamhall.searchExamHallList();}" '
						  +'type="text" style="border:0;height:28px;line-height:28px;width:'+width*0.45+'px; background:none;position:absolute;z-index:1;" >'
						  +'<img src="/module/recruitment/image/clear.png" style="position:absolute;z-index:100000;margin-left:248px;width:17px;height:17px;margin-top:5px" '
						  +' onclick="addexamhall.deletetextKey();">'
					},{
						xtype:'panel',
						bodyStyle:'background:#4695d0',
						style:'margin-top:5px;',
						width:width*0.46,
						html:'<a style="height:30px;line-height:30px;color:white">&nbsp;&nbsp;'+desc+'</a>'
				},panel]
			},{
				xtype:'panel',
				width:1,
				style:'margin-left:10px;margin-top:42px',
				bodyStyle: 'border-width:0 0 0 1px;',
				height:320
			},{
				xtype:'panel',
				border:false,
				style:'margin-left:10px;margin-top:20px',
				items:[{
					xtype:'panel',
					border:false,
					html:'<div style="float:left"><a style="color:#A1A1A1;">已选</a></div><div style="float:right"><a style="color:#549FE3 !important" href="javaScript:addexamhall.deleteSelectExamHall();">清空已选</a></div>'
				},{
					xtype:'panel',
					style:'margin-top:5px',
					width:width*0.45-10,
					id:'selectPanel',
					autoScroll:true,
					height:320
				},{
					xtype:'textfield',
					hidden:true,
					id:'addHallId'
				}]
			}],
			buttonAlign:'center', 
			bbar:[{xtype:'tbfill'},'->' ,
			{
				xtype:'button',
			 	text:'<font style="color:#549FE3">添加</font>',
			 	id:'buttonid',
			 	style:'margin-right:6px;',
			 	listeners:{
					'click':function(){
						var value=Ext.getCmp('addHallId').getValue();
						value = value.substring(1);
						if(afterfunc)
							Ext.callback(eval(afterfunc),null,[getEncodeStr(value)]);
						win.close();
					},
					'render':function(){
						Ext.getCmp('buttonid').btnEl.setStyle('width',"40px");
						Ext.getCmp('buttonid').btnEl.setStyle('height',"20px");
					}
			 	}
			}
			/**,
			{
				xtype:'panel',
				border:false,
				html:'<input type="button" value="添加" style="margin-right:6px; width:50px;height:25px;background:url(/module/recruitment/image/bggg.png) repeat; border:none; color:#fff;" onclick="addexamhall.addSubmit()">'
			}**/
			]
		});
		win.show();
	},
	//添加按钮提交
	addSubmit:function(){
		var value=Ext.getCmp('addHallId').getValue();
		value = value.substring(1);
		if(afterfunc)
			Ext.callback(eval(afterfunc),null,[getEncodeStr(value)]);
		Ext.getCmp('addWindow').close();
	},
	//将选中的数据加载到右侧panel中
	insertIntoPanel:function(itemid,itemdesc,flag){
		var idz = 'addDiv_'+itemid;
		var val = '<div style="float:left;height:30px;line-height:30px;" title="'+itemdesc+'">'
					+'<img src="/module/recruitment/image/unit.png" >&nbsp;&nbsp;'+(itemdesc.length>14?itemdesc.substring(0,14)+'…':itemdesc)+'</div>'
					+'<div style="float:right;height:30px;line-height:30px;">'
					+'<a id='+idz+' style="display: none;margin-right:5px;" href="javaScript:addexamhall.toDeselectHall(\''+itemid+'\',\''+itemdesc+'\')">删除</a></div>';
		//防止重复添加
		if(Ext.isDefined(Ext.getCmp('addPanel_'+itemid))){
			return;					
		}
	    //将选中的数据加载到右侧panel中 
	    var addPanel = Ext.create('Ext.form.Panel',{
			html:val,
			border:false,
			bodyStyle:flag==1?'':'background:#F2F2F2',
			style:'margin-left:10px;margin-top:5px;',
			id:'addPanel_'+itemid,
			listeners:{
				'afterrender':function(){
					Ext.get('addPanel_'+itemid).on("mouseover",function(){
						addexamhall.onMouseover(itemid,2,flag);
					});
					Ext.get('addPanel_'+itemid).on("mouseout",function(){
						addexamhall.onMouseleave(itemid,2,flag);
					});
					if(flag==1)
						document.getElementById('add_'+itemid).innerHTML='已添加';
					var str = itemid+"`"+itemdesc;
					var value = Ext.getCmp('addHallId').getValue()+","+str;
					Ext.getCmp('addHallId').setValue(value);
				
				}
			}
		});
		Ext.getCmp('selectPanel').insert(addPanel);
	},
	//清除搜索框中的提示
	clearTip:function(){
		var value = document.getElementById("textfieldKey").value;
		if(value==textdesc){
			document.getElementById("textfieldKey").value='';
		}
	},
	//删除搜索框中值
	deletetextKey:function(){
		document.getElementById("textfieldKey").value=textdesc;
		addexamhall.searchExamHallList();
	},
	//鼠标悬停
	onMouseover:function(itemid,flag,flag2){
		if(flag==1){
			document.getElementById('add_'+itemid).style.display='block';
		}else{
			//FFF8D2
			Ext.getCmp('addPanel_'+itemid).setBodyStyle("background:#CFE6FF");
			document.getElementById('addDiv_'+itemid).style.display='block';
		}
	},
	onMouseleave:function(itemid,flag,flag2){
		if(flag==1){
			document.getElementById('add_'+itemid).style.display='none';
		}else{
			document.getElementById('addDiv_'+itemid).style.display='none';
			if(flag2==2){
				Ext.getCmp('addPanel_'+itemid).setBodyStyle("background:#F2F2F2");
			}else{
				Ext.getCmp('addPanel_'+itemid).setBodyStyle("background:#FFF");
			}
		}
	},
	//删除已选定的hall
	toDeselectHall:function(itemid,itemdesc){
		var value=Ext.getCmp('addHallId').getValue();
		if(Ext.isEmpty(value)){
			return;
		}
		value=value.replace(","+itemid+'`'+itemdesc,'');
		Ext.getCmp('addHallId').setValue(value);
		Ext.getCmp('addPanel_'+itemid).destroy();
		if(!Ext.isEmpty(document.getElementById('add_'+itemid))){
			document.getElementById('add_'+itemid).innerHTML='添加';
		}
		items=items.replace(","+itemid+"`"+itemdesc,"");
		addexamhall.searchExamHallList();
	},
	//清除已选
	deleteSelectExamHall:function(){
		var arr = Ext.getCmp('addHallId').getValue().substring(1).split(",");
		for(var i=0;i<arr.length;i++){
			var itemid = arr[i].split("`")[0];
			var itemdesc = arr[i].split("`")[1];
			addexamhall.toDeselectHall(itemid,itemdesc);
		}
	},
	//搜索框查询
	searchExamHallList:function(){
		var value=document.getElementById("textfieldKey").value;
		if(value==''){
			document.getElementById("textfieldKey").value=textdesc;
		}
		if(value==textdesc){
			value='';
		}
		var extraParams={data:data,items:items,searchtext:value}
		var store = Ext.getCmp('selectGrid').getStore();
		store.getProxy().extraParams=extraParams;
		store.load();
		store.on('load',function(){
			for(var i=0;i<store.getCount();i++){
				var itemid = store.getAt(i).get("itemid");
				if(Ext.isDefined(Ext.getCmp('addPanel_'+itemid))){
						document.getElementById('add_'+itemid).innerHTML='已添加';				
				}
			}
		});
	}
});