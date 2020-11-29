/**
*	计算公式组件 zhaoxg add
*	调用方法：		
		Ext.require('EHR.defineformula.DefineFormula',function(){
			Ext.create("EHR.defineformula.DefineFormula",{module：thisScope.module,id:id,formulaType:thisScope.formulaType});
		})
*	参数说明：thisScope.module：模块标识  1：薪资  2：薪资总额  3：人事异动  4...其他
			id：主键标识   薪资则为薪资类别号；  人事异动为公式组号；  其他。。。根据各自模块自行设置   在交易类中区分即可
**/
Ext.define('formulacombo',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	extend:'Ext.form.field.ComboBox',
	xtype: 'formulacombo',
	createPicker:function(){
		var me = this;
		var picker = this.callParent(arguments);
		me.fieldlength = me.bodyEl.getWidth();
		me.resize(picker,me.fieldlength);
		picker.on('itemclick',function(e,record){
			me.fireEvent('selectclick',me,record);
		});
		return picker;
	},
	onExpand: function() {
		var me = this;
		var picker = me.picker;
		me.resize(picker,me.fieldlength);
	},
	resize:function(picker,fieldlength){
		var items = picker.store.data.items;
		var pickerlength = 0;
		for(var i=0;i<items.length;i++){
			
			var item = items[i].data.name;
			if(item!=""){
				var one = (item.split('#!#')[0].length+1)*8;
				var two = "0";
				if(item.split('#!#').length>1){
					two = item.split('#!#')[1].length*16;
				}
				var itemlength = parseInt(one)+parseInt(two);
				if(itemlength>pickerlength){
					pickerlength = itemlength;
				}
			}
		}
		if(pickerlength<fieldlength)
			pickerlength = fieldlength;
		picker.setWidth(pickerlength);
	}
});
Ext.define('EHR.defineformula.DefineFormula',{
        constructor:function(config){
			thisScope = this;
			thisScope.id = config.id;//薪资类别id,人事异动模版id
			thisScope.formulaType = config.formulaType;//1:是计算公式，2:是审核公式，lis添加
        	thisScope.module = config.module;//模块号
        	thisScope.groupId=config.groupId;//人事异动-公式组id,gaohy
        	thisScope.callBackfn = config.callBackfn;
        	thisScope.infor_type = config.infor_type;//姓名、单位、岗位
        	thisScope.itemid = '';
        	thisScope.itemname = '';
        	thisScope.useflag = '';
        	thisScope.fields = '';//要显示的公式列表头
        	thisScope.columns = '';
        	//thisScope.data = '';
        	thisScope.standard = '';
        	thisScope.ratetable = '';
        	thisScope._formulaPanel = '';//计算公式的panel
        	thisScope._spFormulaPanel = '';//定义审核公式的panel
        	thisScope.addOrUpdate = '';//新增和修改标记
        	thisScope.store = '';//公式数据store
        	thisScope.indexNum = 0; //选中行的索引
        	thisScope.selectionStart = 0;//光标起选中始位置
    		thisScope.selectionEnd = 0;//光标选中结束位置
    		thisScope.selectionIndex = 0;//光标位置
    		thisScope.orderByIndexNum=0;//排序选中的行索引
    		thisScope.hzname = "";
    		thisScope.orderByWin=''; //排序窗口 liuyz
    		thisScope.orderByFlag=0; //排序标识 liuyz
    		thisScope.range=null;//计算公式选中的range对象
    		//假期管理-假期类型
    		thisScope.hoildayType=config.hoildayType;
    		//假期管理-假期年份
    		thisScope.hoildayYear=config.hoildayYear;
    		// 拖拽功能是否显示 =0 显示，=1不显示；默认显示
    		thisScope.dragDropFlag = config.dragDropFlag;
			var map = new HashMap();
			map.put("id",thisScope.id);
			map.put("module",thisScope.module);
			map.put("flag","1");//获取fields标识
			map.put("formulaType",thisScope.formulaType);//获取fields标识
		    Rpc({functionId:'ZJ100000061',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					thisScope.fields=result.fields;
					thisScope.initColumns();
					thisScope.init(); 
				}else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
					Ext.showAlert(result.message);//{  
	                   /* title : common.button.promptmessage,  
	                    msg : result.message,  
	                    buttons: Ext.Msg.OK,
	                    icon: Ext.MessageBox.INFO  
					});*/
				}
		    }},map);     	
        },
        initColumns:function(){
        	thisScope.columns = new Array();
     		var optionStore = Ext.create('Ext.data.Store', {
			    fields: ['runflag', 'name'],
			    data : [
			        {"runflag":"0", "name":common.label.formula},
			        {"runflag":"1", "name":common.label.standardTable},
			        {"runflag":"2", "name":common.label.taxRateTable}
			    ]
			});
			var box = Ext.create('Ext.form.ComboBox', {//执行列所用的下拉框
			    store: optionStore,
			    queryMode: 'local',
			    repeatTriggerClick : true,
			    displayField: 'name',
			    valueField: 'runflag',
				listeners:{
	   				select:function(combo,records){
	   					if(thisScope.itemid==undefined||thisScope.itemid==''){
                            combo.setValue("0");
	   						Ext.showAlert(common.msg.FormulaNameCanNotNull);//公式名称不能为空
							return;
						}
						var runflag = combo.getValue();
						var map = new HashMap();
						map.put("id",thisScope.id);
						map.put("itemid",thisScope.itemid);
						map.put("module",thisScope.module);
						map.put("useflag",thisScope.useflag);
						map.put("runflag",runflag);
						map.put("formulaType",thisScope.formulaType);
						//假期管理-假期类型
						map.put("hoildayType", thisScope.hoildayType);
			    		//假期管理-假期年份
						map.put("hoildayYear", thisScope.hoildayYear);
					    Rpc({functionId:'ZJ100000063',async:false,success:thisScope.cellclickOK},map);							
					},
					specialkey: function(field, e){
	                    // e.HOME, e.END, e.PAGE_UP, e.PAGE_DOWN,
	                    // e.TAB, e.ESC, arrow keys: e.LEFT, e.RIGHT, e.UP, e.DOWN
	                    if (e.getKey() == e.UP||e.getKey() == e.DOWN) {
	                    	if(e.getKey() == e.DOWN&&thisScope.store.count()-1!=thisScope.indexNum){
	                    		thisScope.indexNum = thisScope.indexNum+1;
	                    	}else if(e.getKey() == e.UP&&thisScope.indexNum!=0){
	                    		thisScope.indexNum = thisScope.indexNum-1;
	                    	}
	                    	var gridPanel = null;
							if(thisScope.formulaType=="2")
								gridPanel = thisScope._spFormulaPanel;
							else
								gridPanel = thisScope._formulaPanel;
							var selMod = gridPanel.getSelectionModel();
							selMod.select(thisScope.indexNum, true);
//	                        thisScope.store.load();
	                    }
	                }
				}
			});
			
			//名称列所用的下拉框数据
			var addStore = Ext.create('Ext.data.Store',
			{
				fields:['name','id'],
				proxy:{
				    	type: 'transaction',
				    	functionId:'ZJ100000069',
				        extraParams:{
			        		id:thisScope.id,
			        		opt:'1',
			        		module:thisScope.module
				        },
				        reader: {
				            type: 'json',
				            root: 'data'         	
				        }
				},
				autoLoad: true
			});
			
			//名称列所用的编辑框的下拉框,新增或修改
			var addbox = Ext.create('Ext.form.ComboBox', {
			    store: addStore,
			    queryMode: 'local',
			    repeatTriggerClick : true,
			    displayField: 'name',
			    valueField: 'id',
				listeners:{
	   				select:function(combo,records){
						var newitemid = combo.getValue();
						var selMod = null;
						if(thisScope.formulaType=="2")
							selMod = thisScope._spFormulaPanel.getSelectionModel();
						else
							selMod = thisScope._formulaPanel.getSelectionModel();
						var records = selMod.getSelection();
						var map = new HashMap();
						if(thisScope.module=='3'){//人事异动,计算公式序号 lis 20160824
							var seq = records[0].get("seq");
							map.put("seq",seq);
						}
						map.put("id",thisScope.id);
						map.put("formulaitemid",newitemid);
						map.put("module",thisScope.module);
						map.put("addOrUpdate",thisScope.addOrUpdate);
						map.put("oleitemid",thisScope.itemid);
						map.put("itemname", thisScope.itemname);//指标编码,gaohy
						map.put("groupid", thisScope.groupId);//人事异动-公式组id,gaohy
						if(thisScope.hzname)
							map.put("hzname",thisScope.hzname);//原有公式名字 zhaoxg add 2016-5-25 为了记住
					    Rpc({functionId:'ZJ100000070',success:function(form,action){
					    	var result = Ext.decode(form.responseText);
							var succeed=result.succeed;
							var info="";
							if(thisScope.module=='3'){//人事异动,gaohy
								info=result.info;
							}
							if(succeed){
								/*if(thisScope.module=='3'&&info=="noRepet"){//重复的时候就不增加,gaohy
									if(thisScope.addOrUpdate=="new"){
//										var a = thisScope.store.count()-1;
//										thisScope.indexNum=thisScope.indexNum-1;
										thisScope.store.remove(thisScope.store.getAt(thisScope.indexNum));
										if(thisScope.formulaType=="2")
											selMod = thisScope._spFormulaPanel.getSelectionModel();
										else
											selMod = thisScope._formulaPanel.getSelectionModel();
										if(thisScope.store.count() > 0){
											selMod.select(0, true);
										}
									}else if(thisScope.addOrUpdate=="update"){
										var tempsel = thisScope.store.getAt(thisScope.indexNum);
										for(var key in result.storeList[0]){
											tempsel.set(key,result.storeList[0][key]);
										}
									}
									thisScope.showFormulaContent();
									var tip = Ext.getCmp("content_tip");
    		     					if(tip){
    		     						tip.destroy();
    		     					}
    		     					Ext.showAlert("项目不能重复！");
								}*/
								if(thisScope.addOrUpdate=="new"){
									thisScope.store.remove(thisScope.store.getAt(thisScope.indexNum));
									thisScope.store.insert(thisScope.indexNum,result.storeList);
									
									if (thisScope.store.count() > 0){
									    selMod.select(thisScope.indexNum, true);
									}
								}else if(thisScope.addOrUpdate=="update"){
									var tempsel = thisScope.store.getAt(thisScope.indexNum);
									for(var key in result.storeList[0]){
										tempsel.set(key,result.storeList[0][key]);
									}
								};
//								thisScope.store.load();
					        }
						}},map);							
					},
					specialkey: function(field, e){
	                    // e.HOME, e.END, e.PAGE_UP, e.PAGE_DOWN,
	                    // e.TAB, e.ESC, arrow keys: e.LEFT, e.RIGHT, e.UP, e.DOWN
	                    if (e.getKey() == e.UP||e.getKey() == e.DOWN) {
	                    	if(e.getKey() == e.DOWN&&thisScope.store.count()-1!=thisScope.indexNum){
	                    		thisScope.indexNum = thisScope.indexNum+1;
	                    	}else if(e.getKey() == e.UP&&thisScope.indexNum!=0){
	                    		thisScope.indexNum = thisScope.indexNum-1;
	                    	}
	                    	var gridPanel = null;
							if(thisScope.formulaType=="2")
								gridPanel = thisScope._spFormulaPanel;
							else
								gridPanel = thisScope._formulaPanel;
							var selMod = gridPanel.getSelectionModel();
							selMod.select(thisScope.indexNum, true);
//	                        thisScope.store.load();
	                    }
	                }
				}
			});
			var text = Ext.create('Ext.form.field.Text', {
			     name: 'name',
			     allowBlank: false,
			     listeners:{
			     	specialkey: function(field, e){
	                    // e.HOME, e.END, e.PAGE_UP, e.PAGE_DOWN,
	                    // e.TAB, e.ESC, arrow keys: e.LEFT, e.RIGHT, e.UP, e.DOWN
	                    if (e.getKey() == e.UP||e.getKey() == e.DOWN) {
	                    	if(e.getKey() == e.DOWN&&thisScope.store.count()-1!=thisScope.indexNum){
	                    		thisScope.indexNum = thisScope.indexNum+1;
	                    	}else if(e.getKey() == e.UP&&thisScope.indexNum!=0){
	                    		thisScope.indexNum = thisScope.indexNum-1;
	                    	}
	                    	var gridPanel = null;
							if(thisScope.formulaType=="2")
								gridPanel = thisScope._spFormulaPanel;
							else
								gridPanel = thisScope._formulaPanel;
							var selMod = gridPanel.getSelectionModel();
							selMod.select(thisScope.indexNum, true);
	                    }
	                }
			     }
			});
			//公式列表
     		for(var i=0;i<thisScope.fields.length;i++){
				var obj = new Object();
				if(thisScope.fields[i]=='useflag'){
					obj.header=common.label.state;//状态
					obj.flex=10;
					obj.menuDisabled=true;
					obj.xtype='templatecolumn';
					obj.align='center';
					obj.tpl='<input name="formulaflag" type=checkbox id={useflag} itemid={itemid} onclick="thisScope.alertUseFlag(this);"/>';
					thisScope.columns[i]=obj;
				}else if(thisScope.fields[i]=='validflag'){
					obj.header=common.label.enable;//启用
					obj.flex=10;
					obj.menuDisabled=true;
					obj.xtype='templatecolumn';
					obj.align='center';
					obj.tpl='<input name="formulaflag" type=checkbox id={itemid} validflag={validflag} onclick="thisScope.alertValidflag(this);"/>';
					thisScope.columns[i]=obj;
				}else if(thisScope.fields[i]=='hzname'){
					obj.text=common.label.name;//名称
					obj.flex=50;
					obj.menuDisabled=true;
					obj.sortable=false;
					obj.dataIndex='hzname';
					obj.renderer=function(v) {
						if(v == null)
							return;
						if(v.indexOf("#!#")!=-1){
							return v.split("#!#")[1];
						}else{
							return v;
						}
					};
					obj.editor=addbox;
					thisScope.columns[i]=obj;
				}else if(thisScope.fields[i]=='runflag'){
					obj.text=common.label.execute;//执行
					obj.flex=25;
					obj.menuDisabled=true;
					obj.dataIndex='runflag';
					obj.renderer=function(v) {
	    				if(v=='0')return common.label.formula;
	    				else if(v=='1')return common.label.standardTable;
	    				else if(v=='2') return common.label.taxRateTable;
	    				else return common.label.formula;

					};
					obj.editor=box;
					thisScope.columns[i]=obj;
				}else if(thisScope.fields[i]=='spname'){
					obj.text=common.label.name;//名称
					obj.flex=50;
					obj.menuDisabled=true;
					obj.sortable=false;
					obj.dataIndex='spname';
					obj.renderer=function(v,colum,record) {
							return v;
					};
					obj.editor=text;
					thisScope.columns[i]=obj;
				}
			}
        },
        cellclickOK:function(form,action){
			var result = Ext.decode(form.responseText);
			var runflag = result.runflag;
			var standid = result.standid;
			var formulavalue = result.formulavalue;//计算公式
			var centerPanel = Ext.getCmp('centerPanel');
			centerPanel.removeAll(false);
			var taxRateTable = Ext.getCmp('taxDetailId');//税率表
			var standardTable = Ext.getCmp('standard');//标准表
			if(taxRateTable){
				taxRateTable.destroy();
				taxDetail_me = null;
			}
			if(standardTable){
				standardTable.destroy();
				satndard_me = null;
			}
			
			if(runflag==0){//数据类型是公式
				centerPanel.add(thisScope.expression);
				Ext.getCmp("shry").setValue(formulavalue);
			}else if(runflag==1){//标准表
				thisScope.initStandardTable(standid,thisScope.itemname,thisScope.id,thisScope.itemid,runflag);
			}else if(runflag==2){//税率表
				thisScope.initTaxDetailTable(standid,thisScope.itemid)
			}else{
				centerPanel.add(thisScope.expression);
				Ext.getCmp("shry").setValue(formulavalue);
			}
			
			//???????? lis 20160510 start 
			var gridPanel = null;
			if(thisScope.formulaType=="2")
				gridPanel = thisScope._spFormulaPanel;
			else
				gridPanel = thisScope._formulaPanel;
			var rows = gridPanel.getSelectionModel().getSelection();
			if(rows.length > 0)
				gridPanel.getView().focusRow(rows[0]);
			//lis 20160510 end
		},
		
        init:function()
		{
			thisScope.store = Ext.create('Ext.data.Store', {
				fields:thisScope.fields,
				proxy:{
				    	type: 'transaction',
				    	functionId:'ZJ100000061',
				        extraParams:{
			        		id:thisScope.id,
			        		groupid:thisScope.groupId,//人事异动-公式组id,gaohy
			        		module:thisScope.module,
			        		formulaType:thisScope.formulaType,
			        		flag:"2"//获取data数据标识
				        },
				        reader: {
				            type: 'json',
				            root: 'data'         	
				        }
				},
				autoLoad: true
			});

			thisScope.store.on('load',function(){
				thisScope.selectFormula();
				var selMod = null;
				if(thisScope.formulaType=="2")
					selMod = thisScope._spFormulaPanel.getSelectionModel();
				else
					selMod = thisScope._formulaPanel.getSelectionModel();
				if (thisScope.store.count() > 0){
				    selMod.select(thisScope.indexNum, true);
				    var record = thisScope.store.getAt(thisScope.indexNum);
				    thisScope.itemid = record.get("itemid");
		    		if(thisScope.formulaType=="2"){
		    			thisScope.itemname = record.get("spname");
		    			thisScope.useflag = record.get("validflag");
		    		}
		    		else{
		    			thisScope.itemname = record.get("itemname");
		    			thisScope.useflag = record.get("useflag");
		    		}
		    		
    				if(thisScope.itemid==''){
    					thisScope.addOrUpdate = 'new';
		    		}else{
		    			thisScope.addOrUpdate = 'update';
		    		}
/**					var map = new HashMap();
					map.put("id",thisScope.id);
					map.put("itemid",thisScope.itemid);
					map.put("itemname", thisScope.itemname);
					map.put("groupid",thisScope.groupId);//人事异动-公式组id,gaohy
					map.put("module",thisScope.module);
					map.put("useflag",thisScope.useflag);
					map.put("runflag",'');
					map.put("formulaType",thisScope.formulaType);
				    Rpc({functionId:'ZJ100000063',async:false,success:thisScope.cellclickOK},map);*/
				}
			});
			thisScope.cellediting = Ext.create('Ext.grid.plugin.CellEditing', {
	            clicksToEdit: 2
	        });
			// 渲染拖拽功能
			var viewConfig = {
				plugins:{
					ptype:'gridviewdragdrop',
					dragText:common.label.DragDropData//拖放数据
				},
				listeners: {
	                beforedrop:thisScope.removeRecord
	            }
			};
			// =1 拖拽功能不显示
			if(1 == thisScope.dragDropFlag) 
				viewConfig = {};
			// 计算公式数据显示panel
			thisScope._formulaPanel = Ext.create('Ext.grid.Panel', {
					store: thisScope.store,
					width: 243,
			    	height: 370,
			    	border:false,
			    	columnLines:true,
					rowLines:true,
					columns: thisScope.columns,
					bufferedRenderer:false,//一起把数据去拿过来，不用假分页模式
					viewConfig:viewConfig,
					plugins: [
				        thisScope.cellediting
				    ],
				    listeners:{
				    	'select':function(rowmode,record,rowIndex){//选中时显示当前计算公式的公式
				    		thisScope.indexNum = rowIndex;
				    		thisScope.itemid = record.get("itemid");
				    		thisScope.itemname = record.get("itemname");
				    		thisScope.useflag = record.get("useflag");
				    		thisScope.hzname = record.get("hzname");
				    		if(thisScope.itemname==''){//人事异动没有itemid列
				    			thisScope.addOrUpdate = 'new';
				    		}else{
				    			thisScope.addOrUpdate = 'update';
				    		}
				    		thisScope.showFormulaContent(record.get("seq"));
				    		if(thisScope.module=='3') {//人事异动选择完函数更新参考项目
					    		//对于统计表单子集函数需要取对应的子集的指标
					    		Ext.getCmp("item_combobox").reset();
					    		Ext.getCmp('code_combobox').hide();
					    		var shry = Ext.getCmp("shry");
								if(shry != null) {
									shry = getEncodeStr(shry.value);
								}else {
									shry = "";
								}
					    		thisScope.itemStore.load({
									params:{formula:shry}
								});
				    		}
				    		var tip = Ext.getCmp('content_tip');
				    		if(thisScope.itemname){
				    			if(tip){
				    				tip.destroy();
				    			}
							}else{
								var rulearea = Ext.getCmp('shry');
								if(rulearea){
									rulearea.setValue("");
									thisScope.expression.disable();
									if(tip){
									}else{
										tip = Ext.create('Ext.tip.ToolTip', {
											id:"content_tip",
											shadow:false,
											trackMouse: true,
											bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
											target: "centerPanel",
											html: "请编辑左侧计算公式名称！"
										});
									}
								}
							}
				    	}
				    }
				});

			//审批公式数据显示panel
			thisScope._spFormulaPanel = Ext.create('Ext.grid.Panel', {
				store: thisScope.store,
				width: 243,
		    	height: 370,
		    	border:false,
		    	columnLines:true,
				rowLines:true,
    			selModel:{
    				selType: 'checkboxmodel',
	            	allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
	            	pruneRemoved: true,//从存储的选项中删除时删除记录
	    	        mode: "multi",     //"SINGLE"/"SIMPLE"/"MULTI"
	    	        checkOnly: false,     //只能通过checkbox选择
	    	        enableKeyNav: true
    	    	},
				columns: thisScope.columns,
				viewConfig:viewConfig,
				plugins: [
					        Ext.create('Ext.grid.plugin.CellEditing', {
					            clicksToEdit: 1
					        })
					    ],
				 listeners:{
			    	'cellclick':function(grid,td,columnIndex,records,tr,rowIndex,e ){
						if(rowIndex != thisScope.indexNum){
							var record = grid.getStore().getAt(rowIndex);
							thisScope.itemid = record.get("itemid");
							thisScope.itemname = record.get("spname");
							var map = new HashMap();
							map.put("itemid",thisScope.itemid);
							map.put("groupid",thisScope.groupId);//人事异动-公式组id,gaohy
							map.put("module",thisScope.module);
							map.put("formulaType",thisScope.formulaType);
							Rpc({functionId:'ZJ100000063',async:false,success:thisScope.cellclickOK},map);
						}
						if(columnIndex==2){//审核公式第二类是启用列。点击掉用修改启用状态方法
							var record = grid.getStore().getAt(rowIndex);
							var formulaflag = document.getElementById(record.get("itemid"));
							thisScope.alertValidflag(formulaflag);
						}
					},
					'select':function(rowmode,record,rowIndex){//选中时显示当前计算公式的公式
						thisScope.indexNum = rowIndex;
						thisScope.itemid = record.get("itemid");
						thisScope.itemname = record.get("spname");
						var map = new HashMap();
						map.put("itemid",thisScope.itemid);
						map.put("groupid",thisScope.groupId);//人事异动-公式组id,gaohy
						map.put("module",thisScope.module);
						map.put("formulaType",thisScope.formulaType);
					    Rpc({functionId:'ZJ100000063',async:false,success:thisScope.cellclickOK},map);
					}
				}
			});
			
			thisScope._spFormulaPanel.on('edit', function(editor, e) {
				thisScope.saveSpFormula2(e.record.get("itemid"),e.value);
			    // 编辑完成后，提交更改
			    e.record.commit();
			});
			

			var width_1 = 28;
			var width_2 = 32;
			var width_3 = 28;
			var width_4 = 59;
			var style_1 = "margin-left:3px;margin-top:5px";
			var buttons = Ext.widget('container', {
				items:[{
		    	   xtype:'fieldset',
		           title:common.label.operationaSymbol,
		           layout:'vbox',
		           width:260,
		           height:150,
		           padding:'0 0 0 6',
		           style:'algin:center',
		           items:[
		               {
			        	   xtype:'container',
			        	   items:[
									{xtype:'button',text:'0',width:width_1,height:25,style:'margin-top:5px',listeners:{"click":function(){ thisScope.symbol('shry',this.text);}} },
									{xtype:'button',text:'1',width:width_1,height:25,style:style_1,handler: function () { thisScope.symbol('shry','1');}},
									{xtype:'button',text:'2',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'3',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'4',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'(',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:common.button.If,width:width_4,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}}
			        	          ]
		               },
		               {
			        	   xtype:'container',
			        	   items:[
									{xtype:'button',text:'5',width:width_1,height:25,style:'margin-top:5px',listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'6',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'7',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'8',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'9',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:')',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:common.button.Else,colspan:2,width:width_4,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}}
			        	          ]
		               },
		               {
			        	   xtype:'container',
			        	   items:[
									{xtype:'button',text:'+',width:width_1,height:25,style:'margin-top:5px',listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'-',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'*',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'%',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'/',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'\\',width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:common.button.And,width:width_3,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:common.button.Or,width:width_3,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}}
			        	          ]
		               },
		               {
			        	   xtype:'container',
			        	   items:[
									{xtype:'button',text:'=',width:25,height:25,style:'margin-top:5px',listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'>',width:25,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'<',width:25,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'<>',width:width_2,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'<=',width:width_2,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:'>=',width:width_2,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},                      		
									{xtype:'button',text:'~',width:25,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}},
									{xtype:'button',text:common.button.Not,width:width_1,height:25,style:style_1,listeners:{"click":function(){ thisScope.symbol('shry',this.text);}}}  
			        	          ]
		               }
		           	]
				}]
			});
		
			var formula = Ext.create('Ext.panel.Panel', {
					border:false,
			        items:[{
			         	border:false,
						xtype:'textareafield',
						name:'formula',
						id:'shry',
						fieldStyle:'line-height:18px;height:220px;',
						width:500,
						height:220,
						enableKeyEvents:true,
						listeners:{
			        		afterrender:function(textarea){
			        			if(Ext.isIE){//ie 下绑定mouseleave事件
			        				textarea.getEl().on("mouseleave",function(){
			        					//thisScope.getCursorPosition();//获得光标位置  lis update2016-7-5
			        				})
			        			}
			        		},
			        		change:function(){
			        			if(Ext.isIE){
			        				//thisScope.getCursorPosition();//获得光标位置   大局观告诉我 改变不需要这玩意  zhaoxg update2016-5-10
			        			}
			        		},
			        		keyup:function(textarea,e){
			        			if(Ext.isIE){
			        				thisScope.getCursorPosition();//获得光标位置     lis update2016-7-5
			        			}
			        		},
			        		 click: {
			                    element: 'el',
			                    fn: function(){ 
				        			if(Ext.isIE){
				        				thisScope.getCursorPosition(); //获得光标位置
				        			}
			        			}
			                }
			        	}
			        }],
					buttons:[
						"->",
						{xtype:'button',text:common.button.functionGuide,handler:function(){thisScope.functionWizard();}},//函数向导
						{xtype:'button',text:common.button.computeCondition,id:'computeCond',handler:function(){thisScope.createComputeCond();}},//计算公式
						{xtype:'button',text:common.button.formulaSave,//公式保存
						  handler:function(){
							thisScope.saveFormula();
							}
						}
					]
			   	});
				thisScope.itemStore = Ext.create('Ext.data.Store',
				{
					fields:['name','id'],
					proxy:{
					    	type: 'transaction',
					    	functionId:'ZJ100000062',
					        extraParams:{
				        		id:thisScope.id,
				        		opt:'1',
				        		module:thisScope.module,
				        		formulaType:thisScope.formulaType
					        },
					        reader: {
					            type: 'json',
					            root: 'data'         	
					        }
					},
					autoLoad: true
				});
				var codeStore = Ext.create('Ext.data.Store',
				{
					fields:['name','id'],
					proxy:{
					    	type: 'transaction',
					        functionId:'ZJ100000062',
					        reader: {
					            type: 'json',
					            root: 'data'         	
					        }
					}
				});
				
				
				var fieldSetStore = Ext.create('Ext.data.Store', {
					fields:['name','id'],
					proxy:{
					    	type: 'transaction',
					        functionId:'ZJ100000062',
					        extraParams:{
				        		id:thisScope.id,
				        		opt:'0',
				        		module:thisScope.module,
				        		formulaType:thisScope.formulaType
					        },
					        reader: {
					            type: 'json',
					            root: 'data'         	
					        }
					}
				});
				
				 var fieldSetCombox = {
				     id:'fieldSet_combobox',
				   	 xtype:'formulacombo',
				   	 fieldLabel:common.label.fieldsets,
				   	 labelSeparator :'',//去掉后面的冒号
				   	 store:fieldSetStore,
				   	 displayField:'name',
				   	 editable:false,
				   	 valueField:'id',
				   	 queryMode:'local',
				   	 labelAlign:'right',
				   	 labelWidth:40,
				   	 width:200,
				   	 style:'margin-top:15px',
				   	 matchFieldWidth:false,
				   	 value:"A01",
				   	 listeners:{
				   	 	'selectclick':function(combo,ecords){
							Ext.getCmp('item_combobox').reset();
							var comValue = combo.getValue();
							thisScope.itemStore.load({
								params:{
									itemid:combo.value,
									opt:'1',
									module:thisScope.module
								},
								
								callback: function(record, option, succes){
									if(record.length>1)
										Ext.getCmp('item_combobox').show(); 
									else
										Ext.getCmp('item_combobox').hide(); 
								}
							});
						}
					}
				 };
				
				var itemCombox = {
			   			id:'item_combobox',
			   			xtype:'formulacombo',
			   			fieldLabel:common.label.items,//项目
			   			labelSeparator :'',//去掉后面的冒号
			   			store:thisScope.itemStore,
			   			displayField:'name',
			   			editable:false,
			   			valueField:'id',
			   			queryMode:'local',
			   			labelAlign:'right',
			   			labelWidth:40,
			   			width:200,
			   			style:'margin-top:15px',
			   			matchFieldWidth:false,
			   			listConfig : {
				            maxHeight : 200,
				            getInnerTpl : function() {
				                return '<div data-qtip="{id}">{name}</div>';
				            }
				        },
			   			listeners:{
			   				'selectclick':function(combo,ecords){
								Ext.getCmp('code_combobox').reset();
								var comValue = combo.getValue();
								if('newcreate' == comValue){
									thisScope.viewTempVar();
								}else{
									//var itemid = comValue.split(":");
									//出现冒号截取
									thisScope.symbol('shry',comValue.substring(comValue.indexOf(":")+1,comValue.length));
									codeStore.load({
										params:{
										itemid:combo.value,
										opt:'2',
										module:thisScope.module
									},
									callback: function(record, option, succes){
										if(record.length>1){
											Ext.getCmp('code_combobox').show(); 
										}else{
											Ext.getCmp('code_combobox').hide(); 
										}
									}
									});
								}
							}
						}
			   		};
				
				var codeCombox = {
			   			id:'code_combobox',
			   			xtype:'formulacombo',
			   			fieldLabel:common.label.code,//代码型
			   			labelSeparator :'',//去掉后面的冒号
			   			store:codeStore,
			   			displayField:'name',
			   			editable:false,
			   			valueField:'id',
			   			queryMode:'local',
			   			hidden:true,//只有选中代码型的项目才会显示
			   			labelWidth:40,
			   			width:200,
			   			labelAlign:'right',
			   			style:'margin-top:15px',
			   			matchFieldWidth:false,
			   			listConfig : {//设置下拉框高度
				            maxHeight : 200,
				            getInnerTpl : function() {
				                return '<div data-qtip="{id}">{name}</div>';
				            }
				        },
			   			listeners:{
			   				'selectclick':function(combo,ecords){
								var itemid = combo.getValue();
			    				thisScope.symbol('shry',"\""+itemid+"\"");
							}
						}
			   		};
				
				var items = new Array();
				if("4" == thisScope.module){
					fieldSetStore.load();
					items.push(fieldSetCombox);
				}
				
				items.push(itemCombox);
				items.push(codeCombox);
				
				var itempanel = Ext.create('Ext.panel.Panel', {
					border:false,
					items:[{
				 	   	xtype:'fieldset',
				 	    id:'fieldset',
				        title:common.label.referenceItems,//参考项目
				        layout:'column',
				        width:230,
				        height:150,
				   		items:items
				   		}]
				   	});

				thisScope.ratetable = Ext.create('Ext.panel.Panel', {
						id:'ratetable',
						border : false,
						items:[{
					 	   	xtype:'fieldset',
					        title:common.label.taxRateTable,//税率表
					        layout:'column',
					        width:470,
					        height:390,	
					   		items:[{
								html:"<iframe id='iframe_rate' name='iframe_rate' width='430' height='360' src=''></iframe>"
							}],
					   		buttons:[
				          		{xtype:'tbfill'},
				          		{
				          			text:'计算',
				          			handler:function(){

				          			}
				          		},          		
				          		{
				          			text:'取消',
				          			handler:function(){
				          			
				          			}
				          		},
				          		{xtype:'tbfill'}
				           ]
				   		}]
				   	});
   	
				   	var panel = null;
				   	if(thisScope.formulaType=="2")//1:是计算公式，2:是审核公式，lis添加
				   		panel = thisScope._spFormulaPanel;
				   	else
				   		panel = thisScope._formulaPanel;
				    var formulaPanel = Ext.create('Ext.panel.Panel', {
						border : false,
						items:[{
					 	   	xtype:'fieldset',
					        title:common.label.formulaList,//公式列表
					        layout:'column',
							width: 270,
					    	height: 420,
					   		items:[{
					   			xtype:'panel',
					   			width: 245,
						    	height: 400,
					   			items:[panel],
								dockedItems:[
									{
										xtype:'toolbar',
										id:'bottomToolbar',
										dock:'bottom',
										border : false,
										items:[{xtype:'tbfill'},{
												text:common.button.insert,//新增
												handler:function(){
												if(thisScope.formulaType=="2")//1:是计算公式，2:是审核公式，lis添加
													thisScope.addSpFormula();
												else
													thisScope.addComputeCond(thisScope.store);
												}
											},{
												text:common.button.edit,
												hidden:thisScope.formulaType=="2"?false:true,
												handler:function(){
													var record = thisScope._spFormulaPanel.getSelectionModel().getSelection()[0];
													if(record)
														thisScope.editSpFormula();
													else
														Ext.showAlert(common.msg.selectEditFormula);//'请选择要编辑的公式！'
												}
											},{
												text:common.button.todelete,//删除
												handler:function(){
													thisScope.deleteFormula();
												}
											},
											{
												//liuyz 调整顺序弹窗
												text:common.button.movenextpre,
												handler:function(){
													thisScope.orderbyFormula();
												}
											},
											{xtype:'tbfill'}]
									}
								]
					   		}]
				   		}]
				   	});	

				    if("4" == thisScope.module)
				    	Ext.getCmp("bottomToolbar").setHidden(true);
				    
				    thisScope.expression = Ext.create('Ext.panel.Panel', {
						width: 500,
						height: 420,
						id:'expression',		
						layout: 'border',
						border:false,
						bodyStyle: 'background:#ffffff;',
						items: [
				             { region: "west",border:false, width:240,items:itempanel},
				             { region: "north",border:false,height:262,items:formula,style: {
								marginTop: '8px'
				        	 }},
				             { region: "center",border:false,width:260,items:buttons}
						]
					});
			
					var bodyPanel = Ext.create('Ext.panel.Panel', {
						height: 425,
						layout: 'border',
						border:false,
						bodyStyle: 'background:#ffffff;',
						items: [
				              { region: "west",border:false, width:280,items:formulaPanel},
				              { region: "center",id:'centerPanel',border:false,items:[thisScope.expression]}
						]
					});
					var title = "";
					if(thisScope.formulaType=="2")
						title = common.label.spFormula;//审批公式
					else
						title = common.label.computeFormula;//计算公式
				   var win=Ext.widget("window",{
				          title:title,  
				          height:500,  
				          width:800,
				          minButtonWidth:40,
				          layout:'fit',
				          bodyStyle: 'background:#ffffff;',
						  modal:true,
						  resizable:false,
						  closeAction:'destroy',
						  //复写beginDrag方法，解决下拉框弹出时拖动造成页面混乱
					      beginDrag:function(){
					      		   thisScope.cellediting.completeEdit( );
					        	   Ext.each(itempanel.query('combobox'),function(combox,index){
					        	   		combox.collapse();
					        	   });
					  	  },
						  items: [bodyPanel],
				          buttons:[
					         		{xtype:'tbfill'},
					         		{
					         			text:common.button.close,//关闭
					         			handler:function(){
						          			if(thisScope.module=='3' && thisScope.formulaType != "2"){//人事异动,计算公式 lis edit 20160612
						          				if(panel.getStore().getCount() != 0){
							         					Ext.showConfirm(common.msg.isCheckAllFormula,function(value){//校验所有计算公式是否有公式表达式
							         						if(value == "yes"){
							         							var map = new HashMap();
																map.put("id",thisScope.id);
																map.put("module",thisScope.module);
																map.put("groupId",thisScope.groupId);//这俩人事异动专用 ↓									
																
															    Rpc({functionId:'ZJ100000097',success:function(form,action){
															    	var result = Ext.decode(form.responseText);
																	var succeed=result.succeed;
																	if(succeed){
																		var info = result.info;
																		if(info == "ok"){
																			thisScope.expression.destroy();
																			win.destroy();
																			 
																		}else{
																			Ext.showAlert(decode(info));
																		}
																	}else{
																		Ext.showAlert(result.message);
																	}
																}},map);
							         						}else{
							         							thisScope.expression.destroy();
							         							win.destroy();
							         							
							         						}
							         					})
						          				}else{
						          					//Ext.showAlert(common.msg.formulaCanNotNull);
						          					thisScope.expression.destroy();
						          					win.destroy();
						          					
						          				}
				          					}else{
				          						thisScope.expression.destroy();
				          						win.destroy();
				          						
				          					}
					         			}
					         		},
					         		{xtype:'tbfill'}
					          	],
				          	listeners: {
				          		'beforeclose':function(component){
				          			if(thisScope.module=='3' && thisScope.formulaType != "2"){//人事异动,计算公式 lis edit 20160612
				          				if(panel.getStore().getCount() != 0){
					         					Ext.showConfirm(common.msg.isCheckAllFormula,function(value){//校验所有计算公式是否有公式表达式
					         						if(value == "yes"){
					         							var map = new HashMap();
														map.put("id",thisScope.id);
														map.put("module",thisScope.module);
														map.put("groupId",thisScope.groupId);//这俩人事异动专用 ↓									
														
													    Rpc({functionId:'ZJ100000097',success:function(form,action){
													    	var result = Ext.decode(form.responseText);
															var succeed=result.succeed;
															if(succeed){
																if(result.info == "ok"){
																	thisScope.expression.destroy();
																	win.destroy();
																}else{
																	Ext.showAlert(common.msg.hasFormulaNoCond);
																}
															}else{
																Ext.showAlert(result.message);
															}
														}},map);
					         						}else{
					         							thisScope.expression.destroy();
					         							win.destroy();
					         						}
					         					})
				          				}else{
				          					thisScope.expression.destroy();
				          					win.destroy();
				          				}
		          					}else{
		          						thisScope.expression.destroy();
		          						win.destroy();
		          					}
				          			return false;
				          		}
				          	}
				    });             
				    win.show();  
				    if(thisScope.formulaType=='2' || "4" == thisScope.module)
				    	Ext.getCmp('computeCond').hide();

		},
		
		symbol:function(exprId,strexpr){
			var rulearea = Ext.getCmp(exprId);
			var myField = rulearea.inputEl.dom;
			var startPos = 0;//光标选中内容起始位置
			var endPos = 0;//光标选中内容结束位置
			var selectionIndex = 0;//光标位置
			//五项专项附加扣除指标当名称和系统内置得名称一致时，定义公式用到这5个指标时，程序自动加上中括号。
			if(strexpr == common.label.znjy || strexpr == common.label.jxjy || strexpr == common.label.zfzj ||
					strexpr == common.label.zfdk || strexpr == common.label.sylr) {
				strexpr = "[" + strexpr + "]";
			}
			//ie非兼容模式 按照原来的判断方法不对，去掉“Ext.isIE”的判断。  19/3/13 xus
			if (myField.selectionStart || myField.selectionStart == '0') {
				startPos = myField.selectionStart;
				endPos = myField.selectionEnd;
				// 保存scrollTop，为了换行
				var restoreTop = myField.scrollTop;
				//写入选中内容
				rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
				
				if (restoreTop > 0) {//换行
					myField.scrollTop = restoreTop;
				}
				myField.focus();
				myField.selectionStart = startPos + strexpr.length;
				myField.selectionEnd = startPos + strexpr.length;	
			}else {
				var element = document.selection;
				if(Ext.isIE && element != null) {//对于ie下非兼容模式下，还是得这样写，否则定位有问题
					//写入选中内容
					myField.focus();
					var rge = thisScope.range;
					if (rge!=null&&rge.offsetLeft!=0)//用户点击过计算公式部分 offsetLeft==0说明该对象未被渲染到当前页面上
					{ 
						rge.text=strexpr;
						rge.select();
					}
					else
					{//用户未点击过计算公式部分需要创建一个range。让用户选择的选项在开头插入
						var element = document.selection;
						if (element!=null) {
							var rge = element.createRange();
							if (rge!=null)	
							{ 
								var offsetLeft = rge.offsetLeft
								// 如果有值，插入到最后一位
								if(offsetLeft == 0 && myField && myField.value) {
									rge.moveEnd("character", myField.value.length);
									rge.moveStart("character", myField.value.length);
								}
								
								rge.text=strexpr;
								rge.select();
							}
						}
					}
					thisScope.range=rge;
					
				}else
					myField.value += strexpr;
		    }
			//ie非兼容模式 按照原来的判断方法不对，去掉“Ext.isIE”的判断。  19/3/13 xus end
			/**  ie非兼容模式 按照原来的判断方法不对
			//IE support
			if (Ext.isIE) {
				var element = document.selection;
				if (element==null) {
					var sel = null;
					startPos = thisScope.selectionStart;
					endPos = thisScope.selectionEnd;
					selectionIndex = thisScope.selectionIndex;
					myField.focus();
					var rge = thisScope.range;
					if (rge!=null&&rge.offsetLeft!=0)//用户点击过计算公式部分 offsetLeft==0说明该对象未被渲染到当前页面上
					{ 
						rge.text=strexpr;
						rge.select();
					}
					else
					{//用户未点击过计算公式部分需要创建一个range。让用户选择的选项在开头插入
						var element = document.selection;
						if (element!=null) {
							var rge = element.createRange();
							if (rge!=null)	
							{ 
								rge.text=strexpr;
								rge.select();
							}
						}
					}
					thisScope.range=rge;
				}else{
					//写入选中内容
					var sel = null;
					startPos = thisScope.selectionStart;
					endPos = thisScope.selectionEnd;
					selectionIndex = thisScope.selectionIndex
					myField.focus();
					rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
					
					var index = selectionIndex + strexpr.length;
					var range = myField.createTextRange();
					range.move("character", index);//移动光标
					range.select();//选中
					thisScope.selectionIndex = index;
					thisScope.selectionStart = startPos + strexpr.length;
					thisScope.selectionEnd = endPos + strexpr.length;
				}
			}
			//MOZILLA/NETSCAPE support 
			else if (myField.selectionStart || myField.selectionStart == '0') {
				startPos = myField.selectionStart;
				endPos = myField.selectionEnd;
				// 保存scrollTop，为了换行
				var restoreTop = myField.scrollTop;
				//写入选中内容
				rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
				
				if (restoreTop > 0) {//换行
					myField.scrollTop = restoreTop;
				}
				myField.focus();
				myField.selectionStart = startPos + strexpr.length;
				myField.selectionEnd = startPos + strexpr.length;
			}else {
				myField.value += strexpr;
		    }
			**/
		},
		
		//获得光标位置
		getCursorPosition:function () {
			var rulearea = Ext.getCmp('shry');
	   		var el = rulearea.inputEl.dom;//得到当前textarea对象
	   		if(Ext.isIE){
	   		    el.focus(); 
	   		    if(document.selection!=null)
	   		    {
		   		    //IE11不支持document.selection，用document.getSelection()替代了document.selection.createRange().text:
		   		    var r = document.selection.createRange(); //返回当前网页中的选中内容的TextRange对象
		   		    thisScope.range=r;
		   		    if (r == null) {
		   		    	thisScope.selectionStart = 0; 
		   		    }
		   		    var re = el.createTextRange(); //选中内容
		   		    var rc = re.duplicate(); //所有内容 
		   		    try{
			   		    //定位到指定位置
			   		    re.moveToBookmark(r.getBookmark());  
		   		    	//【为了保持选区】rc的开始端不动，rc的结尾放到re的开始
		   		    	rc.setEndPoint('EndToStart', re); 
		   		    }catch(e){
		   		    	//表格控件点击刷新页面按钮后，此时鼠标焦点拿不到 lis 20160704
		   		    }
		   		    var text = rc.text;
		   		    text = text.replace(/[\r]/g," ");//替换回车符 lis 20160701   	
		   		    thisScope.selectionIndex = text.length; //光标位置
		   		    thisScope.selectionStart = rc.text.length; 
		   		    thisScope.selectionEnd = thisScope.selectionStart + re.text.length;
	   		    }
	   		    else
	   		    {
	   		    	thisScope.selectionIndex = el.selectionStart; //光标位置
		   		    thisScope.selectionStart = el.selectionStart; 
		   		    thisScope.selectionEnd = el.selectionEnd;
	   		    }
	   		}
		},
		
		//计算公式勾选框，勾选操作
		alertUseFlag:function(obj){
			if(obj.getAttribute("itemid")==null){
				obj.checked=false;
				Ext.showAlert(common.msg.notHaveFormulaName);
				return;
			}
				
			var useflag="0";
		    if(obj.checked){
		    	useflag="1";
		    }
		    var map = new HashMap();
			map.put("id",thisScope.id);
			map.put("module",thisScope.module);
			map.put("itemid",obj.getAttribute("itemid"));
			map.put("flag",useflag);
			map.put("batch","0");
			map.put("formulaType",thisScope.formulaType);
		    Rpc({functionId:'ZJ100000064',async:false},map);
		},
		
		//审批公式勾选框，勾选操作
		alertValidflag:function(obj){
			var useflag="0";
		    if(obj.checked){
		    	useflag="1";
		    }
		    var map = new HashMap();
			map.put("id",thisScope.id);
			map.put("module",thisScope.module);
			map.put("itemid",obj.getAttribute("id"));
			map.put("flag",useflag);
			map.put("batch","0");
			map.put("formulaType",thisScope.formulaType);
		    Rpc({functionId:'ZJ100000064',async:false},map);
		},
		//选中状态
		selectFormula:function(){
			var formulaflag = document.getElementsByName("formulaflag");
			if(thisScope.formulaType=="2"){
				for(var i=0;i<formulaflag.length;i++){
					if(formulaflag[i].getAttribute("validflag")=="1"){
						formulaflag[i].checked=true;
					}
				}
			}else{
				for(var i=0;i<formulaflag.length;i++){
					if(formulaflag[i].id=="1"){
						formulaflag[i].checked=true;
					}
				}
			}
		},
		
		//拖拽公式，改变排列位置
		removeRecord:function(node,data,model,dropPosition,dropHandlers){
			var ori_itemid=data.records[0].get("itemid");
			var ori_seq=data.records[0].get('seq');
			var to_itemid=model.get('itemid');
			var to_seq=model.get('seq');
		    var map = new HashMap();
			map.put("id",thisScope.id);
			map.put("module",thisScope.module);
			map.put("ori_itemid",ori_itemid);
			map.put("ori_seq",ori_seq);
			map.put("to_itemid",to_itemid);
			map.put("to_seq",to_seq);
			
			map.put("groupId",thisScope.groupId);//这俩人事异动专用 ↓
			map.put("flag","1");//这个位置的参数只针对人事异动的公式有效  用来区分是公式还是公式组  zhaoxg add 2016-5-28
			
			map.put("formulaType",thisScope.formulaType);
		    Rpc({functionId:'ZJ100000067',success:function(form,action){
		    	var result = Ext.decode(form.responseText);
				var succeed=result.succeed;
				if(succeed){
					var i=0;
					thisScope.store.each(function(record){
						if(record.get('itemid')==ori_itemid){
							thisScope.indexNum = i;
						}
						i = i+1;
					});
//					thisScope.indexNum = model.index;
					thisScope.store.load();
		        }
			}},map);
		},
		//计算条件
		createComputeCond:function(){
			var map = new HashMap();
			map.put("id",thisScope.id);
			map.put("module",thisScope.module);
			map.put("itemid",thisScope.itemid);
			map.put("groupid",thisScope.groupId);//人事异动-公式组id,gaohy
			Ext.Loader.setPath("DECN","/components/definecondition");
			Rpc({functionId:'ZJ100000071',success: function(form,action){
				var result = Ext.decode(form.responseText);
				var conditions=result.conditions;
				if(thisScope.module=='1'){
					Ext.require('DECN.DefineCondition',function(){
						var thisCondition=Ext.create("DECN.DefineCondition",{primarykey:thisScope.id,opt:"1",checktemp:'salary',mode:'xzgl_jsgs',imodule:'0',conditions:conditions,afterfunc:'thisScope.saveComputeCond'});
					});
				}else if(thisScope.module=='3'){//人事异动,gaohy
					Ext.require('DECN.DefineCondition',function(){
						var thisCondition=Ext.create("DECN.DefineCondition",{primarykey:thisScope.id,opt:"2",checktemp:'temp',mode:'','itemid':thisScope.itemname,imodule:'2',conditions:conditions,afterfunc:'thisScope.saveComputeCond'});
					});
				}
			}},map);
		},
		//保存计算公式条件
		saveComputeCond:function(obj){
				var map = new HashMap();
					map.put("id",thisScope.id);
					map.put("itemid",thisScope.itemid);
					map.put("groupid",thisScope.groupId);//人事异动-公式组id,gaohy
					map.put("module",thisScope.module);
					map.put("conditions",obj);
					Rpc({functionId:'ZJ100000072',async:false,success:function(form,action){
						Ext.showAlert(common.msg.saveSucess+"！");//{  
							/*title : common.button.promptmessage,  
							msg : common.msg.saveSucess+"！",  
							buttons: Ext.Msg.OK,
							icon: Ext.MessageBox.INFO  
						});*/
					}},map);
		},
		//新增计算公式
		addComputeCond:function(store){
			//每次只能新增一个
			/*
			var checkNode = document.getElementsByName('formulaflag');
			for(var i=0;i<checkNode.length;i++){
				if(checkNode[i].getAttribute("itemid") == null || checkNode[i].getAttribute("itemid") ==''){
					Ext.showAlert(common.msg.noSaveFormula);//{  
					//return;
				}
				
			}
			*/
			var filedname ="itemid";
            if (thisScope.module=="3"){//人事异动
                filedname ="itemname";
            }
            var bReturn=false;
			store.each(function(record){
		      if(record.get(filedname) == null || record.get(filedname) ==''){
	                    Ext.showAlert(common.msg.noSaveFormula); 
	                    bReturn=true;
              }
            });
            if(bReturn){
               return;
            }
			var fieldMap = new HashMap();
			Ext.Array.each(thisScope.fields,function(name,index){
				fieldMap.put(name,"");
			});
			var count = store.count();
			store.insert(count,fieldMap);//在Ext4，此处插入obj就可以，Ext6要插入数据才可以，gaohy
			thisScope.addOrUpdate = 'new';
			thisScope.indexNum = count;
			var sel = thisScope._formulaPanel.getSelectionModel();
			sel.select(thisScope.indexNum, true);//定位最后一行	
			thisScope.cellediting.cancelEdit();
			if(thisScope.module=="1"){
				thisScope.cellediting.startEditByPosition({row:thisScope.indexNum,column:1});
			}else if(thisScope.module=="3"){
				thisScope.cellediting.startEditByPosition({row:thisScope.indexNum,column:0});
			}
		},
		//编辑审核公式
		editSpFormula:function(){
			var record = thisScope._spFormulaPanel.getStore().getAt(thisScope.indexNum);
			thisScope.addSpFormula(record);
		},
		//新增审核公式
		addSpFormula:function(record){
			var spId = "";
			var spFormulaName = "";
			var spComment = "";
			if(record){
				spId = record.get("itemid");
				spFormulaName = record.get('spname');
				spComment = record.get('information');
			}
			
			var newFormulaWin = Ext.widget("window",{
		          title:common.label.spFormula,  
		          height:312,  
		          width:430,
		          resizable:false,
				  modal:true,
				  closeAction:'destroy',
				  items: [{
				  		xtype:'panel',
		         		border:false,
						items:[{
					        xtype: 'textfield',
					        id:'spFormulaName',
					        width:400,
					        fieldLabel: common.label.formulaName,
					        labelSeparator :'',
					        labelAlign : "right",
					        labelWidth:70,
					        value:spFormulaName,
					        allowBlank: false  // 表单项非空
					    	},{
				         	border:false,
							xtype:'textareafield',
							fieldLabel: common.label.spTips,
							labelSeparator :'',
							labelAlign : "right",
							labelWidth:70,
							id:'spComment',
							value:spComment,
							width:400,
							height:200
				        }],
				       	bbar:[
			         		{xtype:'tbfill'},
			         		{
			         			xtype: 'button',
			         			text:common.button.ok,
			         			margin:'0 10 0 0',
			         			handler:function(){
//			         				if(!Ext.getCmp("spFormulaName").isValid())
//	          							return;
			         				thisScope.saveSpFormula(record,newFormulaWin);
			         			}
			         		},
			         		{
			         			xtype: 'button',
			         			text:common.button.cancel,
			         			handler:function(){
			         				newFormulaWin.close();
			         			}
			         		},
			         		{xtype:'tbfill'}
			          	]
			          }]
		    });             
			newFormulaWin.show(); 
			},
		
		//保存计算公式
		saveSpFormula:function(record,newFormulaWin){
				var spId = "";
				if(record)
				   spId = record.get('itemid');
				var spFormulaName = Ext.getCmp('spFormulaName').getValue();
				if(trim(spFormulaName)==''){
					Ext.showAlert(common.msg.FormulaNameCanNotNull);
					return;
				}
 				var spComment = Ext.getCmp('spComment').getValue();
 				var map = new HashMap();
 				map.put("id",thisScope.id);
 				map.put("formulaitemid",spId);
 				map.put("module",thisScope.module);
 				map.put("spFormulaName",spFormulaName);
 				map.put("spComment",spComment);
 				map.put("formulaType",thisScope.formulaType);
 			    Rpc({functionId:'ZJ100000070',async:false,success:function(form,action){
 			    	var result = Ext.decode(form.responseText);
					var succeed=result.succeed;
					if(succeed){
						newFormulaWin.close();
						
						if(spId==''){
							if(thisScope.store.count()>0)
								thisScope.indexNum = thisScope.store.count();
							thisScope.store.insert(thisScope.indexNum,result.storeList);
							var selMod = thisScope._spFormulaPanel.getSelectionModel();
							selMod.select(thisScope.indexNum, true);
//							thisScope.store.load();	
						}else{
							record.set("spname",spFormulaName);
							record.set("information",spComment);
							var selMod = thisScope._spFormulaPanel.getSelectionModel();
						    selMod.select(thisScope._spFormulaPanel.getStore().getCount());
						}
					}else{
						Ext.showAlert(common.msg.saveFailed+"！");//{  
		                    /*title : common.button.promptmessage,//提示信息
		                    msg : common.msg.saveFailed+"！",  
		                    buttons: Ext.Msg.OK,
		                    icon: Ext.MessageBox.INFO  
						});*/
					}
 				}},map);
		},
		
		//保存计算公式
		saveSpFormula2:function(spId,spFormulaName){
 				var map = new HashMap();
 				map.put("id",thisScope.id);
 				map.put("formulaitemid",spId);
 				map.put("module",thisScope.module);
 				map.put("spFormulaName",spFormulaName);
 				map.put("formulaType",thisScope.formulaType);
 			    Rpc({functionId:'ZJ100000070',async:false,success:function(form,action){
 			    	var result = Ext.decode(form.responseText);
					var succeed=result.succeed;
					if(!succeed){
						Ext.showAlert(common.msg.saveFailed+"！");//{  
		                    /*title : common.button.promptmessage,  
		                    msg : common.msg.saveFailed+"！",  
		                    buttons: Ext.Msg.OK,
		                    icon: Ext.MessageBox.INFO  
						});*/
					}
 				}},map);
		},
		
		//删除公式
		deleteFormula:function(){
			if(thisScope.id==null&&thisScope.id.length<1){
				return;
			}
			var gridPanel = null;
			if(thisScope.formulaType=="2")
				gridPanel = thisScope._spFormulaPanel;
			else
				gridPanel = thisScope._formulaPanel;
			var rows = gridPanel.getSelectionModel().getSelection();
			if(rows.length==0){
				Ext.showAlert(common.msg.selectDelFormula);//'请选择要删除的公式！'
				return;
			}
			var hzname="";
			var arrTmp = thisScope.hzname.split("#!#");
			if (arrTmp.length==2){
			   hzname=arrTmp[1];
			   if (hzname!=""){
			       hzname="["+hzname+"]";
			   }
			}else if(arrTmp.length==1){
				hzname=arrTmp[0];
			   if (hzname!=""){
			       hzname="["+hzname+"]";
			   }
			}
			 
			Ext.showConfirm(common.label.isDeleteSelected+""+hzname+common.label.formula+"？     ",
				function(v){
					if(v=='yes'){
						var gridPanel = null;
						if(thisScope.formulaType=="2")
							gridPanel = thisScope._spFormulaPanel;
						else
							gridPanel = thisScope._formulaPanel;
						var rows = gridPanel.getSelectionModel().getSelection();
						var itemid_array=new Array();
						var itemid = '';
						var itemname = '';
						var seq = '';
    					for ( var i = 0; i < rows.length; i++) {
    						if(thisScope.formulaType=="2"){
    							itemid = rows[i].get('itemid');
    							//itemname = rows[i].get('spname');
    						}else{
    							itemid = rows[i].get('itemname');
    							seq = rows[i].get('seq');
    							//itemname = rows[i].get('itemname');
    						}
    						if(itemid!="")
    							itemid_array[i] = itemid;
    		     			gridPanel.getStore().remove(rows[i]);
    					}
    					if(itemid_array.length == 0){
    						gridPanel.getSelectionModel().select(0);
    						thisScope.showFormulaContent(seq);
    						var tip = Ext.getCmp("content_tip");
    		     			if(tip){
    		     				tip.destroy();
    		     			}
    						return;
    					}
						var map = new HashMap();
						map.put("id",thisScope.id);
						map.put("itemid_array",itemid_array);
						map.put("itemid",thisScope.itemid);
						map.put("module",thisScope.module);
						map.put("itemname", thisScope.itemname)
						map.put("seq", seq)
						map.put("groupid", thisScope.groupId);//人事异动-公式组id,gaohy
						map.put("formulaType",thisScope.formulaType);
					    Rpc({functionId:'ZJ100000068',async:false,success: function(form,action){
								var result = Ext.decode(form.responseText);
								var succeed=result.succeed;
								if(succeed){
									thisScope.indexNum = 0;
									thisScope.store.load();
									Ext.getCmp("shry").setValue("");
								}else{
									Ext.showAlert(common.label.deleteFailed+"！");//{  
					                    /*title : common.button.promptmessage,  
					                    msg : common.label.deleteFailed+"！",  
					                    buttons: Ext.Msg.OK,
					                    icon: Ext.MessageBox.INFO  
									});*/
								}
						}},map);
					}else{
						return;
					}				
				}
			);
		},
		//调整顺序弹窗设置 liuyz
		orderbyFormula:function(){
			var records = []; 
			thisScope.store.each(function(r){ records.push(r.copy()); }); 
			//在公式列表没有数据的时候不能调整顺序sunj
			if(Ext.isEmpty(records)){
				Ext.showAlert(common.msg.emptyalert);
				return;
			}else {
				var orderByStore = new Ext.data.Store({ recordType: thisScope.store.recordType }); 
				orderByStore.add(records);
				var orderColumns = [];
				if(thisScope.formulaType==2)
				{
					orderColumns.push(thisScope.columns[0]);
				}
				else
				{
					if(thisScope.module==1)
					{
						orderColumns.push(thisScope.columns[1]);
					}
					if(thisScope.module==3)
					{
						orderColumns.push(thisScope.columns[0]);
					}
				}
				thisScope.orderByWin=Ext.widget('window',{
					height:400,
					width:300,
					resizable:false,
					modal: true,
					border:0,
					title:common.button.movenextpre,
					items:[{
					  	 	xtype:'grid',
					  	 	width:290,
					        height:330,
					  	 	id:'orderByGrid',
					  	 	scrollable :true,
					  	 	rowLines:true,
					  	 	selModel: {
							    selection: "rowmodel",
							    mode: "MULTI"
							},
					  	 	columns:orderColumns,
					  	 	store:orderByStore
					  	 }
					   ],
					 bbar:[{xtype:'tbfill'},{
								text:common.button.ok,//确定
								margin:'0 5 0 0',
								handler:function(){
								thisScope.saveOrderByStore(orderByStore)
								}
							},{
								text:common.button.previous,//上移
								margin:'0 5 0 0',
								handler:function(){
									thisScope.orderByUp(orderByStore)
								}
							},{
								text:common.button.next,//下移
								margin:'0 5 0 0',
								handler:function(){
									thisScope.orderByDown(orderByStore)
								}
							},{
								text:common.button.close,//关闭
								handler:function(){
									thisScope.orderByClose();
								}
							},{xtype:'tbfill'}]
				});
				thisScope.orderByWin.show();
				Ext.getCmp('orderByGrid').getSelectionModel().selectRange(0,0); 
			}
		},
		//保存公式
		saveFormula:function(){
			if(thisScope.module=='1'&&(thisScope.itemid==null||thisScope.itemid.length<1||thisScope.id==null||thisScope.id.length<1)){
				Ext.showAlert(common.msg.selectFormulaVar);
				return;					
			}
			//人事异动,gaohy
			if(thisScope.module=='3'&&thisScope.formulaType=="1"&&(thisScope.groupId==null||thisScope.id==null||thisScope.groupId.length<1||thisScope.id.length<1||thisScope.itemname==null||thisScope.itemname.length<1)){
				Ext.showAlert(common.msg.selectFormulaVar);
				return;					
			}
			if(thisScope.module=='3'&&thisScope.formulaType=="2"&&(thisScope.itemid==null||thisScope.itemid.length<1||thisScope.id==null||thisScope.id.length<1)){
				Ext.showAlert(common.msg.selectFormulaVar);
				return;
			}
			var c_expr = Ext.getCmp("shry").getValue();
			if(thisScope.module=='3' && thisScope.formulaType != "2"){//人事异动,计算公式 lis edit 20160612
				if(!!!c_expr){
					Ext.showAlert(common.msg.exprCanNotNull);
					return;
				}
			}
			var map = new HashMap();
			map.put("id",thisScope.id);
			map.put("module",thisScope.module);
			map.put("itemid",thisScope.itemname);
			map.put("c_expr",getEncodeStr(c_expr));
		    Rpc({functionId:'ZJ100000065',async:false,success: function(form,action){
					var result = Ext.decode(form.responseText);
					var info=result.info;
					if(info=="ok"){
						var map = new HashMap();
						if(thisScope.module=='3'&&thisScope.formulaType=="1"){//是人事异动 lis 20160711
							var gridPanel = thisScope._formulaPanel;
							var rows = gridPanel.getSelectionModel().getSelection();
							var seq = rows[0].get("seq");
							map.put("seq",seq);
						}
						map.put("id",thisScope.id);
						map.put("module",thisScope.module);
						map.put("itemid",thisScope.itemid);
						map.put("groupid",thisScope.groupId);//人事异动-公式组id,gaohy
						map.put("itemname",thisScope.itemname);
						map.put("formula",getEncodeStr(Ext.getCmp("shry").getValue()));
						map.put("formulaType",thisScope.formulaType);
						//假期管理-假期类型
						map.put("hoildayType", thisScope.hoildayType);
			    		//假期管理-假期年份
						map.put("hoildayYear", thisScope.hoildayYear);
					    Rpc({functionId:'ZJ100000066',async:false,success:function(form,action){
					    	Ext.showAlert(common.msg.saveSucess+"！");
							}},map);
					}else{
						if(info.length<4){
							var formula=Ext.getCmp("shry").getValue();
							Ext.showAlert(formula+common.label.syntaxError+"！");//{  
			                    /*title : common.button.promptmessage,  
			                    msg : "<div style='white-space:pre-wrap;*white-space:pre;*word-wrap:break-word;'>" + formula+common.label.syntaxError+"！</div>",  
			                    buttons: Ext.Msg.OK,
			                    icon: Ext.MessageBox.WARNING  
							});*/
						}else{
							Ext.showAlert(info);
							/*Ext.MessageBox.show({  
			                    title : common.button.promptmessage,  
			                    msg : "<div style='white-space:pre-wrap;*white-space:pre;*word-wrap:break-word;'>" + info + "</div>",  
			                    buttons: Ext.Msg.OK,
			                    icon: Ext.MessageBox.WARNING  
							});*/
						}
					}
			    }},map);
		},
		
		//税率表
		initTaxDetailTable:function(taxid,itemid){
			Ext.require('EHR.defineformula.TaxDetail',function(){
				Ext.create("EHR.defineformula.TaxDetail",{id:thisScope.id,taxid:taxid,itemid:itemid});
			})
		},
		
		//标准表
		initStandardTable:function(standardID,itemname,salaryid,itemid,runflag){
			Ext.require('EHR.defineformula.Standard',function(){
				Ext.create("EHR.defineformula.Standard",{standardID:standardID,itemname:itemname,salaryid:salaryid,itemid:itemid,runflag:runflag});
			})
		},
		
		//临时变量弹出框     lis   2015-10-17
		viewTempVar:function(salaryid){
			Ext.require('EHR.defineformula.DefineTempVar',function(){
				Ext.create("EHR.defineformula.DefineTempVar",{module:'1',id:thisScope.id,type:'1',callBackfn:'thisScope.reflashItem'});
			})
		},
		//新增完临时变量后刷新项目列表  zhaoxg add 2016-3-4
		reflashItem:function(){
			thisScope.itemStore.load();
		},
		//函数向导
		functionWizard:function(){
			if(thisScope.module=='1'){
				Ext.require('EHR.functionWizard.FunctionWizard',function(){
					Ext.create("EHR.functionWizard.FunctionWizard",{keyid:thisScope.id,opt:"1",checktemp:'salary',mode:'xzgl_jsgs',inforType:thisScope.infor_type,callbackfunc:'thisScope.getfunctionWizard'});
				});
			}else if(thisScope.module=='3'){//人事异动,gaohy
				Ext.require('EHR.functionWizard.FunctionWizard',function(){
					Ext.create("EHR.functionWizard.FunctionWizard",{keyid:thisScope.id,opt:"2",checktemp:'temp',mode:'rsyd_jsgs',inforType:thisScope.infor_type,callbackfunc:'thisScope.getfunctionWizard'});
				});
			} else if(thisScope.module=='4'){
				//考勤假期管理
				Ext.require('EHR.functionWizard.FunctionWizard',function(){
					Ext.create("EHR.functionWizard.FunctionWizard",{keyid:thisScope.id,opt:"7",checktemp:'temp',mode:'kqjq_jsgs',inforType:thisScope.infor_type,callbackfunc:'thisScope.getfunctionWizard'});
				});
			}
		},
		
		//函数向导回调函数，用来接收返回值
		getfunctionWizard:function(obj){
			thisScope.symbol('shry',obj);
			if(thisScope.module=='3') {//人事异动选择完函数更新参考项目
				Ext.getCmp("item_combobox").reset();
	    		Ext.getCmp('code_combobox').hide();
	    		var shry = Ext.getCmp("shry");
				if(shry != null) {
					shry = getEncodeStr(shry.value);
				}else {
					shry = "";
				}
	    		thisScope.itemStore.load({
					params:{formula:shry}
				});
			}
		},
		
		//显示右侧公式内容
		showFormulaContent:function(seq){
			thisScope.expression.enable();
			var map = new HashMap();
			map.put("id",thisScope.id);
			map.put("itemid",thisScope.itemid);
			map.put("groupid",thisScope.groupId);//人事异动-公式组id,gaohy
			map.put("itemname", thisScope.itemname);//人事异动需要,gaohy
			if(!!!seq)
				seq = "";
			map.put("seq", seq);//人事异动需要,lis
			map.put("module",thisScope.module);
			map.put("runflag",'');
			map.put("formulaType",thisScope.formulaType);
			//假期管理-假期类型
			map.put("hoildayType", thisScope.hoildayType);
    		//假期管理-假期年份
			map.put("hoildayYear", thisScope.hoildayYear);
		    Rpc({functionId:'ZJ100000063',async:false,success:thisScope.cellclickOK},map);
		},
		//重新排序
		sortOrderByRecords:function(records,grid)
		{
			for(var num=0;num<records.length-1;num++)
			{
				for(var numY=0;numY<records.length-1-num;numY++)
				{
					var firstIndex=grid.getStore().findBy(function(record){return record==records[numY];});
					var secIndex=grid.getStore().findBy(function(record){return record==records[numY+1];});
					if(firstIndex>secIndex)
					{
						var recordTemp=records[numY+1];
						records[numY+1]=records[numY];
						records[numY]=recordTemp;
					}
				}
			}
			return records;
		},
		//上移功能 liuyz
		orderByUp:function(store){
			var grid=Ext.getCmp('orderByGrid');
			var selectRecord=grid.getSelectionModel().getSelection();
			selectRecord=thisScope.sortOrderByRecords(selectRecord,grid);
			var startIndex=grid.getStore().findBy(function(record){return record==selectRecord[0];});
			var insIndex=startIndex-1;
			if(startIndex!=0)
			{
				var record_1=store.getAt(startIndex-1);
				
				for(var num=0;num<selectRecord.length;num++)
				{
					store.remove(selectRecord[num]);
					store.insert(insIndex,selectRecord[num]);
					insIndex++;
				}
				grid.getView().refresh();  
				startIndex=startIndex-1;
		        grid.getSelectionModel().selectRange(startIndex,insIndex-1); 
		        grid.getView().focusRow(startIndex);  
		        var items =document.getElementsByName('formulaflag');
		        Ext.each(items,function(item){
		           if(item.id==1)
		              item.checked=true;
		        });
			}
		},
		//下移功能 liuyz
		orderByDown:function(store){
			var grid=Ext.getCmp('orderByGrid');
			var selectRecord=grid.getSelectionModel().getSelection();
			selectRecord=thisScope.sortOrderByRecords(selectRecord,grid);
			var startIndex=grid.getStore().findBy(function(record){return record==selectRecord[selectRecord.length-1];});
			var insIndex=startIndex+1;
			if(startIndex!=store.getCount()-1)
			{
				var record_1=store.getAt(startIndex+1);
				
				for(var num=selectRecord.length-1;num>=0;num--)
				{
					store.remove(selectRecord[num]);
					store.insert(insIndex,selectRecord[num]);
					insIndex--;
				}
				grid.getView().refresh();  
				startIndex=startIndex+1;
		        grid.getSelectionModel().selectRange(insIndex+1,startIndex); 
		        grid.getView().focusRow(startIndex);  
		        var items =document.getElementsByName('formulaflag');
		        Ext.each(items,function(item){
		           if(item.id==1)
		              item.checked=true;
		        });
			}	
		},
		//保存计算公式排序 liuyz
		saveOrderByStore:function(store)
		{
			var map = new HashMap();	
			var sorting='';
			if(thisScope.formulaType!=2)
			{
				if(thisScope.module==3)
				{
					for(var i=0;i<store.getCount();i++)
					{
						sorting+=store.getAt(i).get("seq")+"_"+store.getAt(i).get("itemname")+",";
					}
				}
				if(thisScope.module==1)
				{
					for(var i=0;i<store.getCount();i++)
					{
						sorting+=i+"_"+store.getAt(i).get("itemid")+",";
					}
				}
			}
			if(thisScope.formulaType==2)
			{
				for(var i=0;i<store.getCount();i++)
				{
					sorting+=i+"_"+store.getAt(i).get("itemid")+",";
				}
			}
			map.put('sorting',getEncodeStr(sorting));
			map.put('groupid',thisScope.groupId);
			map.put('module',thisScope.module);
			map.put('formulaType',thisScope.formulaType);
			map.put('id',thisScope.id);			
			map.put('itemname',thisScope.itemname);			
			//保存数据到数据库
			Rpc({functionId:'ZJ100000098',success:thisScope.saveOrderByStoreOk},map);
		},
		//保存成功 liuyz
		saveOrderByStoreOk:function(form,action){
			var result = Ext.decode(form.responseText);
			if(result.info=='ok')
			{
				thisScope.store.load();
			}
			//关闭窗口
			thisScope.orderByWin.close();
			
		},
		//关闭窗口 liuyz
		orderByClose:function(){
				thisScope.orderByWin.close();
		}
 });