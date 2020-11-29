Ext.define('TemplateSetupUL.TemplateSetupFormula',{
	constructor:function(config){
	    thisSetup = this;
	    thisSetup.module=config.module;
	    thisSetup.tableid=config.tableid;
	    thisSetup.inforType = config.infor_type;//人员1、单位2、岗位3
	    thisSetup.groupId='';//公式组id
	    thisSetup.gzStore='';//公式组store
	    thisSetup.win='';//窗口
	    var map = new HashMap();
	    map.put("opt", "1");
	    Rpc({functionId:'MB00002007',async:false,success:thisSetup.init},map);//获得当前用户名
	},
	init:function(form,action){
		var result = Ext.decode(form.responseText);
		var userName = result.userName;
		var data=Ext.decode(result.data);
		//公式组数据
		thisSetup.gzStore = Ext.create('Ext.data.Store',
				{
					fields:[{name:'groupName'},{name:'groupId'},{name:'groupStat'},{name:'nSort'}],
					proxy:{
					    	type: 'transaction',
					        functionId:'MB00002007',
					        extraParams:{
				        		tableid:thisSetup.tableid,
				        		opt:'2'
					        },
					        reader: {
					            type: 'json',
					            root: 'data'         	
					        }
					},
					autoLoad: true
				});
		/*调用时， 该任务将在执行之前等待指定的一段时间。如果在等待的时间中， 再调用此任务时，原始调用将会被取消。 这样继续下去，对于每次迭代该函数只是被调用一次。*/
		 thisSetup.task = new Ext.util.DelayedTask();
		 thisSetup.dealClick = function(type,colIndex){
			 if(type=='click'&&colIndex=='1'){//单击公式组时执行
			 			var rows=thisSetup.formulaGrid.getSelectionModel().getSelection();
						//var groupId_array=new Array();//也可以如此定义数组
						/*for ( var i = 0; i < rows.length; i++) {
			    		     groupId_array[i]=rows[i].get('groupId');//获取公式组ID
			    			}*/
						var groupId_array = [];
			    		Ext.Array.each(rows ,function(record){//迭代一个数组
			              		groupId_array.push(record.data.groupId);   
			            });
			            if(groupId_array.length<1){
							Ext.showAlert(common.msg.pleseSelectVar+"！");
							return;
						}
  		         		Ext.Loader.setPath("EHR.defineformula",rootPath+"/components/defineformula");
  		         		if(thisSetup.groupId){
							Ext.require('EHR.defineformula.DefineFormula',function(){
								Ext.create("EHR.defineformula.DefineFormula",{module:thisSetup.module,formulaType:"1",id:thisSetup.tableid,infor_type:thisSetup.inforType,groupId:groupId_array.toString(),actflag:"alert"});
							});
						}
			 }else if(type=='click'&&colIndex=='2'){//不处理 有changeStat处理了
			 	
			 }
		};
		//公式组
		thisSetup.formulaGrid=new Ext.grid.GridPanel({
			border:false,
	        stateId:'formulaGrid',
	        border:false,
	        height:380,
	        stripeRows:true,//隔行换色
	        forceFit:true,//让每列自动填满表格，可以根据columns中设置的width按比例分配
    		store:thisSetup.gzStore,
    		enableHdMenu:false,//是否显示表头的上下文菜单，默认为true
    		enableColumnHide:false,//是否允许通过标题中的上下文菜单隐藏列，默认为true
    		enableColumnMove:false,//是否允许拖放列，默认为true
    		enableColumnResize:false,//是否允许改变列宽，默认为true
    		columnLines:true,//是否显示列分割线，默认为false
    		loadMask:true,//在store.load()完成之前是否显示遮罩效果，true会一直显示"Loading...",
    		viewConfig:{//拖拽插件
				plugins:{
					ptype:'gridviewdragdrop',
					dragText:common.label.DragDropData//拖放数据
				},
				listeners: {
	                beforedrop:function(node, data, overModel, dropPosition, dropHandlers) {//注：此处事件是gridviewdragdrop 的放置监听事件   
						var ori_itemid=data.records[0].get("groupId");
	    	    		var ori_seq=data.records[0].get('nSort');
	    	    		var to_itemid=overModel.get('groupId');
	    	    		var to_seq=overModel.get('nSort');
	    	    	    var map = new HashMap();
	    	    		map.put("id",thisSetup.tableid);
	    	    		map.put("module",thisSetup.module);
	    	    		map.put("ori_itemid",ori_itemid);
	    	    		map.put("ori_seq",ori_seq);
	    	    		map.put("to_itemid",to_itemid);
	    	    		map.put("to_seq",to_seq);
	    	    	    Rpc({functionId:'ZJ100000067',async:false,success:function(form,action){
	    	    	    	var result = Ext.decode(form.responseText);
	    	    			var succeed=result.succeed;
	    	    			if(succeed){
	    	    				//thisSetup.gzStore.load();
	    	    	        }
	    	    		}},map);
	               }
	            }
			},
			multiSelect:true,//支持多选
   			selModel:{
   				selType: 'checkboxmodel',
            	allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
            	pruneRemoved: true,//从存储的选项中删除时删除记录
    	        mode: "multi",     //"SINGLE"/"SIMPLE"/"MULTI"
    	        checkOnly: false,     //只能通过checkbox选择
    	        enableKeyNav: true//开启/关闭在网格内的键盘导航。
   	    	},
    	    plugins:{
    		        	ptype: 'cellediting',
    		        	clicksToEdit:2//设置鼠标点击2次进入编辑状态
    		         },
		    listeners: {
				"cellclick": function(view, td, cellIndex, record, tr, rowIndex, e, eOpts){
					if(td.className.indexOf('x-grid-item-focused')!=-1){
						thisSetup.groupId = record.get('groupId');
		                //var colIndex=e.position.colIdx;//获得点击列索引
		                thisSetup.task.delay(500, thisSetup.dealClick, this, ['click',cellIndex]);//调用dealClick方法之前会等待500秒
					}
   	    		},
				"celldblclick":function(view, td, cellIndex, record, tr, rowIndex, e, eOpts){ 
					 thisSetup.groupId = record.get('groupId');
	                 //var colIndex=e.position.colIdx;//获得点击列索引
				     thisSetup.task.delay(500, thisSetup.dealClick, this, ['double',cellIndex]);//调用dealClick方法之前会等待500秒
			     }
			},
    		columns:[
    		         //自动显示行号，也可以用new Ext.grid.RowNumberer()
    		         {	
    		        	header:'公式组名称',
         		        dataIndex:'groupName',
    		        	sortable:false,
    		        	align:'left',    		        	
    		        	width:300,
    		        	editor:{
    		        	   xtype:'textfield'
    		        	   //maxLength:20
    		        	   //allowBlank:false
    		         	},
    		         	renderer:function(v, cellmeta, record, rowIndex, columnIndex, store){
    		         		//渲染样式，lis 20160518
    		         		//cellmeta.tdAttr = 'style="width:339px;color:#1B4A98 !important;cursor:pointer"';
    		         		if(v)
    		         			return '<a href="###">'+v+'</a>';
    		         		else
    		         			return "";
    		         	}
    		         },
    		         {
    		        	 header:'公式组id',
    		        	 hidden:true,
    		        	 sortable:false,
    		        	 dataIndex:'groupId',
    		        	 align:'center'
    		         },
    		         {
    		        	 header:'序号',
    		        	 hidden:true,
    		        	 sortable:false,
    		        	 dataIndex:'nSort',
    		        	 align:'center'
    		         },
    		         {
    		         	 xtype:'checkcolumn',
    		        	 header:'状态',
    		        	 align:'center',
    		        	 dataIndex:'groupStat',
    		        	 width:40,
    		        	 sortable:false,
    		        	 listeners:{
    		        	 'checkchange':function (checkcolumn , rowIndex , checked , eOpts) {
	    		        	 	var record = thisSetup.gzStore.getAt(rowIndex);
	    		        	 	thisSetup.changeStat(checked,record.get('groupId'));
    		        	 }
    		        	 
    		        	 }
    		        	 /*renderer:function(v, cellmeta, record, rowIndex, columnIndex, store){
    		        	 	var groupId=record.get("groupId");//获取公式组ID
    		        	 	var checked="";
    		        	    if (v=="1")
    		        	      checked="checked"
   		        		    return '<input type="checkbox" onclick=thisSetup.changeStat(this,"'+groupId+'") '+checked+'/>';
    		        	    
    		         	}*/
    		         }
    		         ]
		});
		//编辑计算公式组
		thisSetup.formulaGrid.on('edit', function(editor, e){
			if(e.record.data.groupId){
				if(e.originalValue!=e.value){
					var map = new HashMap();
					map.put("cHz", e.value);
					map.put("groupId",e.record.data.groupId);
					map.put("opt", "4");
					map.put("tableid",thisSetup.tableid);
					Rpc({functionId:'MB00002007',async:false,success:function(form,action){
						var result=Ext.decode(form.responseText);
						var flag=result.flag;
						if(flag=='OK'){
							e.record.commit();
						}else{
							Ext.showAlert("编辑失败！");
							thisSetup.gzStore.load();
						}
					}},map);
				}
			}else{
				if(e.value)
					thisSetup.addCallBack(e.value);
			}
		});
		
		//window弹窗
		thisSetup.win=Ext.widget('window',{
			  title:'计算公式',
	          width:420,
	          height:450, 
	          layout:'fit',
	          resizable:false,//是否允许改变窗口大小
			  modal:true,//模态窗口,窗口遮住的页面不可编辑
			  closable:true,//是否显示关闭按钮
			  closeAction:'destroy',//控制按钮是销毁（destroy）还是隐藏（hide）
			  border: true,
			  plain:true,//true则主体背景透明，false则主体有小差别的背景色，默认为false
	          items: [{
	         		xtype:'panel',
	         		border:false,
					items:[thisSetup.formulaGrid]
	          }],
	          buttons:[
	         		{xtype:'tbfill'},
	         		{
	         			text:"新增",//新增
	         			handler:function(){
	         				var record = new Object();
	         				record.groupName = "";
	         				record.groupId = "";
	         				record.groupStat = "1";
	         				record.nSort = "1";
	         				thisSetup.gzStore.insert(thisSetup.gzStore.count(),record);
	         				var cellediting = thisSetup.formulaGrid.findPlugin('cellediting');
	         				cellediting.startEditByPosition({row: thisSetup.gzStore.count()-1,column: 1});
	         			}
	         		},
	         		{
	         			text:"删除",//删除
	         			handler:function(){
	         				var sel = thisSetup.formulaGrid.getSelectionModel().getSelection();
	         				if(sel.length > 0){
	         					Ext.showConfirm('确认删除选择的计算公式组？',function(btn){
	         						if(btn=='yes')
	         							thisSetup.delCallBack(sel);
	         						else
	         							return;
	         					});
	         				}else{
	         					Ext.showAlert('请选择计算公式组！');
	         				}
	         			}
	         		},
	         		{xtype:'tbfill'}
			 ]
		});
		thisSetup.win.show();
	},

	//新增公式组
	addCallBack:function(msg){
			var map = new HashMap();
			map.put("cHz", msg);
			map.put("opt", "3");
			map.put("tableid",thisSetup.tableid);
			Rpc({functionId:'MB00002007',async:false,success:function(form,action){
				var result=Ext.decode(form.responseText);
				thisSetup.groupId=result.groupId;
				thisSetup.gzStore.load({//重新加载数据后延时弹出计算公式窗口，防止新增输入名称直接回车后计算公式弹出框跑到后面  lis 20160802 
				    scope: this,
				    callback: function(records, operation, success) {
				        if(success){
							thisSetup.formulaGrid.getSelectionModel().select(records.length-1);
     						thisSetup.task.delay(100, thisSetup.dealClick, this, ['click','1']);//调用dealClick方法之前会等待500秒
				        }
				    }
				});
				/*Ext.Loader.setPath("EHR.defineformula","/components/defineformula");
				Ext.require('EHR.defineformula.DefineFormula',function(){
					Ext.create("EHR.defineformula.DefineFormula",{module:thisSetup.module,formulaType:"1",id:thisSetup.tableid,groupId:thisSetup.groupId,actflag:"add"});
				});*/
			}},map);
	},
	//删除
	delCallBack:function(sel){
			var groupId_array = [];
    		Ext.each(sel ,function(record){ 
              		groupId_array.push(record.data.groupId);   
              	});
            if(groupId_array.length<1){
				Ext.showAlert(common.msg.pleseSelectVar+"！");
				return;
			}
			var map = new HashMap();
			map.put("groupId_array", groupId_array);
			map.put("tableid",thisSetup.tableid);
			Rpc({functionId:'MB000020016',async:false,success:function(form,action){thisSetup.gzStore.load();}},map);
	},
	//拖拽公式，改变排列位置
	removeRecord:function(node, data, overModel, dropPosition, eOpts){
		var ori_itemid=data.records[0].get("groupId");
		var ori_seq=data.records[0].get('nSort');
		var to_itemid=overModel.get('groupId');
		var to_seq=overModel.get('nSort');
	    var map = new HashMap();
		map.put("id",thisSetup.tableid);
		map.put("module",thisSetup.module);
		map.put("ori_itemid",ori_itemid);
		map.put("ori_seq",ori_seq);
		map.put("to_itemid",to_itemid);
		map.put("to_seq",to_seq);
	    Rpc({functionId:'ZJ100000067',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
			var succeed=result.succeed;
			if(succeed){
				thisSetup.gzStore.load();
	        }
		}},map);
	},
	changeStat:function(checked,groupId){
		var state="0";
		if(checked)
			state="1";
		var map = new HashMap();
 		map.put("groupId",groupId);
 		map.put("tableid",thisSetup.tableid);
 		map.put("state",state);
 		map.put("opt","5");
 		Rpc({functionId:'MB00002007',async:false,success:function(form,action){
            var result = Ext.decode(form.responseText);
            var succeed=result.succeed;
            if(succeed){
                
            }
        }},map);
	}
});