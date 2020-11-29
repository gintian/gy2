Ext.define('SetupschemeUL.MusterClass',{
	id:'musterClass',
	// 构造
	musterType:undefined,
	moduleID:undefined,
	addMusterStylePriv:undefined,// 是否具有增加花名册分类功能的权限
	delMusterStylePriv:undefined,// 是否具有删除花名册分类功能的权限
    constructor:function(config){
    	musterClass = this;	
    	musterType = config.musterType;
    	moduleID = config.moduleID;
		musterClass.init();
		musterManage.isshow=1;// 显示
		musterClass.styleid = "";
		musterClass.tiptext = "";
    },
    init:function(){ 
    	 var map = new HashMap();
		 map.put("type","getPriv");
		 map.put("moduleID",moduleID);
		 map.put("musterType",musterType);
		 Rpc({functionId:'MM01010003',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					delMusterStylePriv = result.delMusterStylePriv;
					addMusterStylePriv = result.addMusterStylePriv;
				}
		 }},map);
    	musterClass.createGridPanel();
    },
	createGridPanel : function(){
		if(Ext.util.CSS.getRule(".x-grid-cell-inner")){
			Ext.util.CSS.updateRule(".x-grid-cell-inner","padding-bottom","2px");
		}
   		var me = this;
   		var dbclick = false;// 标志单击或双击 false：单击，true：双击
   		var create = true;// 标志是否创建菜单window
   		var height = 0;// 确定高度
   		var styleid = "";// 单击时分类id
   		var db_styleid = "";// 双击修改时分类id
   		var tiptext = '';
   		var enter = 0;// 鼠标进入删除按钮标志
   		var preIndex = 0;// 鼠标放置行的索引
   		
   		Ext.QuickTips.init();
   		Ext.tip.QuickTipManager.init();
 
   		// 删除图标面板
  		var picbox = Ext.create('Ext.panel.Panel',{
   				id:'picbox',
   				xtype:'panel',
   				width:20,
   				height:20,
   				border:false,
   				hidden:true,
   				html:'<div style="cursor:pointer"><img src="/images/muster/delete.png" class="img-xys" onclick = musterClass.deleteFn()> </div>',
   				layout:'absolute',
   				x:1,
   				y:0
   		});
   		// grid表格的数据域
   		me.store = Ext.create('Ext.data.Store',{	
			id:'gridStore',
			fields:['styledesc','styleid','creator','creationTime','mouseEnter'],
    		proxy:{
		    	type: 'transaction',
		        functionId:'MM01010003',
		        extraParams:{
		        	type:'main'	,
		        	musterType:musterType
		        },			       
		        reader: {
		            type: 'json',
		            root: 'styleClass'
		        },
		        async:false
			},	       
	        autoLoad:true,
	        listeners:{
				load : function( me, records, successful, operation, eOpts ) {
					
					if(records.length <= 6){
						height = records.length * 31;
					}else{
						height = 6 * 31;
					}
					// height = records.length * 31;
					if(create == true){
						create = false;	
						musterClass.createWin(height,grid,picbox);// 创建菜单
					}
				}
	        }	
   		});
   		var grid = Ext.create('Ext.grid.Panel',{
			width:153,
			id:'grid',
			border:false,
			header:false,
			cellTip : true,
			autoHeight: true,
			viewConfig: {
				stripeRows: false,// 去除斑马线效果
			　　   markDirty: false// 去除编辑单元格后左上方的红三角
			},
			enableColumnHide:false,// /隐藏列
			sortableColumns:false,// /隐藏排序
			rowLines:false,				
			columnLines : false, 
			scrollable:false,
			hideHeaders : true,
			enableKeyEvents: false,
			bodyStyle:'border-color:#ffffff;',
			store:me.store,
		    columns: [
		        { text: 'styledesc',  dataIndex: 'styledesc',width:153,editor:{xtype:'textfield',width:153,enableKeyEvents: true,listeners:{
		        	specialkey:function(field,e) {
		        		if (e.getKey()==Ext.EventObject.ENTER){  
		        			musterManage.edit = 1;                   	
               			}
					}
		        }}}		       
		    ],		    
			plugins:[
				Ext.create('Ext.grid.plugin.CellEditing', {
	            	ptype: 'cellediting',
	            	clicksToEdit: 2,
	            	listeners:{
	            		edit:function(editor, e){// 实现双击修改分类名
	            			if(e.rowIdx == 0){
	            				Ext.showAlert(musterStyle.editSeeAll);
	            				grid.getSelection()[0].set('styledesc' ,me.trim(e.originalValue));
	            				dbclick = false;	
	            				grid.getSelectionModel().clearSelections();// 清除表格记录选中状态
								grid.getView().refresh();
	            				return;
	            			}
	            			if(me.trim(e.value) == ''){
	            				grid.getSelection()[0].set('styledesc' ,me.trim(e.originalValue));
	            				Ext.showAlert(musterStyle.editNotNull);	            				
	            				dbclick = false;	
	            				grid.getSelectionModel().clearSelections();// 清除表格记录选中状态
								grid.getView().refresh();
	            			}else{
	            				if(e.originalValue != e.value){
	            					grid.getSelection()[0].set('styledesc' ,me.trim(e.originalValue));
	            					for(var i=0;i<Ext.getCmp('grid').getStore().data.items.length;i++){
										var preValue = Ext.getCmp('grid').getStore().data.items[i].data.styledesc;
										if(me.trim(e.value) == me.trim(preValue)){
											if(e.rowIdx != i){
												Ext.showAlert(musterStyle.addHasExist);
												grid.getSelectionModel().clearSelections();
												grid.getView().refresh();
												dbclick = false;
												return;
											}else{
												grid.getSelectionModel().clearSelections();
												grid.getView().refresh();
												dbclick = false;
												return;
											}											
										}
									}
	            					if(me.trim(e.value).length > 15){
	            						Ext.showAlert(musterStyle.addMaxLengthText);
	            						grid.getSelectionModel().clearSelections();
										grid.getView().refresh();
										dbclick = false;
	            						return;
	            					}
		            				var map = new HashMap();
						    		map.put("type","save_lstyle");
									map.put("styleid",db_styleid);
									map.put("styledesc",me.trim(e.value));
									map.put("moduleID",moduleID);
									map.put("musterType",musterType);
									Rpc({functionId:'MM01010003',success: function(form,action){
										var musterName = me.trim(e.value);
										musterManage.loadStore();
										Ext.showAlert(musterStyle.editSucceed + musterName);
										dbclick = false;									
									}},map);
									grid.getSelection()[0].set('styledesc' ,me.trim(e.value));
									grid.getSelectionModel().clearSelections();
									grid.getView().refresh();
		            			}else{
		            				me.store.reload();
		            				dbclick = false;		            				
		            			}
	            			}
	            			
	            		}            		
	            	}
	        	})
			],
			listeners:{				
				cellclick: function( me, td, cellIndex, record, tr, rowIndex, e, eOpts) {
		    		styleid = record.data.styleid;
		    		if("-1"!=styleid){
		    			musterManageStyleid  = styleid;
		    		}else{
		    			musterManageStyleid = undefined;
		    		}
		    		var map = new HashMap();
		    		map.put("type","search_lstyle");
					map.put("styleid",record.data.styleid);
					map.put("moduleID",moduleID);
					map.put("musterType",musterType);
					Rpc({functionId:'MM01010003',success: function(form,action){
						musterManage.loadStore();
					}},map);
		    	},
				celldblclick  : function( me, td, cellIndex, record, tr, rowIndex, e, eOpts ) {					
					dbclick = true;
					db_styleid = record.data.styleid;
				},
				itemmouseleave:function( me, record, item, index, e, eOpts ) {
					preIndex = index;			
					setTimeout(function () {						
						if(enter == 1){
							picbox.hide();
						}
					},2800);
					enter = 1;
				},
				itemmouseenter: function(view, record, item, index, e, eOpts ){
					musterManage.musterFlag = 1;
					styleid = record.data.styleid;
					musterClass.styleid = record.data.styleid;
					musterClass.tiptext = record.data.styledesc;
					mouseEnter = true;
					if(preIndex - index == 1 || index - preIndex == 1){
						enter = 0;				
					}else{
						enter = 1;
					}					
					var count = 0;
					for(var i = 0;i<record.store.sorters.$sortable.items.length;i++){
						if(record.data.styleid == record.store.sorters.$sortable.items[i].data.styleid){
							count = i;						
						}
					}				
					var y = count * 30 + 8;
					picbox.setPosition(0,y);
					if(index != 0){
						picbox.show();
					}	
					if(preIndex == 1 && index == 0){
						picbox.hide();
						// enter = 1;
					}
					tiptext = record.data.styledesc;
					
				},
				itemclick : function( me, record, item, index, e, eOpts ) {	
					musterManage.musterFlag = 0;
					setTimeout(function () {
                        // 在此写单击事件要执行的代码
						if(dbclick == false){
							if (Ext.getCmp('win')){
								grid.getSelectionModel().clearSelections();
								grid.getView().refresh();
								Ext.getCmp('win').destroy();
								musterManage.menuFocus = true;							
							}						
						}else{
							return;
						}                      
                    }, 300);
				},					
				render : function(panel){							
					Ext.create('Ext.tip.ToolTip', {
					    target: panel.body,
					    delegate:"td > div.x-grid-cell-inner",
					    shadow:false,
					    id:me.subModuleId+'_celltip',
					    trackMouse: true,
					    maxWidth:800,//最大显示宽度设置为800，文本宽度超过800显示不全解决不了，高度超过显示的最大高度同样也显示不全解决不了  27734 wangb 20170517
					    renderTo: Ext.getBody(),
					    bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
					    listeners: {
					        beforeshow: function updateTipBody(tip) {
					        	    var div = tip.triggerElement;//.childNodes[0];
					        	    if (Ext.isEmpty(div))
					        	    	return false;
						        	if(div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight-4){
						        		//div 里面内容不允许自动换行，滚动条隐藏 27250 27734  wangb 2017-4-24
						        		tip.update("<div style='white-space:nowrap;overflow:hidden;'>"+div.innerHTML+"</div>");
						        	}else
						        		return false;
					        }
					    }
		    		});
				}
			}
		});	
   		// debugger
    },
    addStyleWin:function(){
    	// 新增分类的window
		var addStyleWin = Ext.create('Ext.window.Window',{
			title:muster_creatWinAddTitle,
			width:635,
			height:230,
			closable: true,
			resizable:false,
			modal:true,
			layout:{
				align:'middle',
				pack:'center',
				type:'hbox'
			},			
			items:[{
				id:'win_textfield',
				layout:'form',
				width:470,
				// height:25,
				xtype:'textfield',
				maxLength:15,
				allowBlank:false,
				fieldStyle:'line-height:36px;height:36px;',
				emptyText:muster_emptyText,
				maxLengthText:musterStyle.addMaxLengthText
			}],
			buttonAlign:'center',
			buttons:{
				items:[{				
					xtype:'button',			
					text:muster_sure,
					height:30,
					width:95,
					margin:'0 30 20 0',
					handler:function(){
						musterClass.addStyle(addStyleWin);
					}
				},{
					xtype:'button',			
					text:muster_cancel,
					height:30,
					width:95,
					margin:'0 0 20 0',				
			        handler: function() {			            
				        addStyleWin.destroy();				        
			        }
				}]
			},
			listeners:{
				destroy:function(){
					//Ext.getCmp('win').destroy();
				    musterManage.menuFocus = true;
				}
			}
	
		});
		addStyleWin.show();
		// return addStyleWin;
    },
    createWin:function(height,grid,picbox){
        var me = this;  
       // debugger
        var gridHeight = grid.store.data.items.length * 30;
		var pic  = Ext.create('Ext.panel.Panel',{
   			width:20,
   			height:gridHeight, 
   			border:false,
   			style:'margin-top:0px;',
   			items:[picbox],
   			listeners:{
   				deactivate:function(){
   					picbox.hide(); 				
   				}
   			}
   		});
	
   		var top  = Ext.create('Ext.panel.Panel',{
   			// autoHeight:true,
   			id:'top',
   			height:height,
   	   		width:190,
   	   		border:false,
   	   		scrollable:true,
   	   		style:'margin-top:0px;',
   	   		layout:'hbox',  			
			items:[grid],
			listeners:{
				show:function(){
					//Ext.getCmp('style_button').blur(true);
				},
				expand:function(){
				},
				render:function(p){						
					musterManage.musterFlag = 1;
					p.body.on('scroll', function(){
						//debugger
						musterManage.scroll = 1;					
		            }, p);
				}
			}
   		});
   		
   		// 如果有删除的权限,则添加删除分类按钮
   		if(delMusterStylePriv){
   			top.add(pic);
   		}
   	    var addMusterStyple_btn = Ext.create('Ext.panel.Panel',{
			id:'addBtn',
			border:false,
			height:25,
			width:190,
			style:'text-align:center;',
			html:'<div style="cursor:pointer"><img src="/images/muster/add.png" onclick = musterClass.addStyleWin()></div>'
		});
	    var panel = Ext.create('Ext.panel.Panel',{
   	   		id:'panel',
   	   		// autoHeight:false,
   	   		width:200,
   	   		border:false,
   	   		style:'margin-top:0px;border:1px solid #ffffff;',
   	   		items:[top]
   	    }); 
	    // 如果有添加花名册分类的权限,则添加增加分类按钮
   		if(addMusterStylePriv){
   			panel.add(addMusterStyple_btn);
   		}

	    if (!Ext.getCmp('win')) {
			Ext.create('Ext.window.Window',{
				id:'win',
				layout:'absolute',
				header:false,
			    width:200,
				height:height + 40,
				border:false,
			 	closeAction : 'destroy',			
				resizable:false,
				x:1,
				y:65,		
				items:[panel],
				listeners:{
					beforeshow:function(){
						if(grid.getStore().data.items.length == 0){
							panel.remove(top);
							Ext.getCmp('addBtn').setHeight(40);
						}					
					},
					show:function(){
						Ext.getCmp('style_button').focus(true);		
					}
				}
			});
		}
		if (Ext.getCmp('win')){				
			Ext.getCmp('win').show();
			//Ext.getCmp('style_button').focus(true);			
			musterManage.menuFocus = false;
			Ext.getCmp('win').on('focusleave',musterClass.focusleave);
			Ext.getCmp('style_button').on('blur',musterManage.musterClose);
		}		
	 },
	 focusleave:function(){
		 if(musterManage.edit == 0){
			 Ext.getCmp('win').un('focusleave',musterClass.focusleave);// 移除监听事件
			 Ext.getCmp('win').destroy();
			 musterManage.menuFocus = true;
		 }
	 	
	 },
	 deleteStyle:function(styleid,tiptext){// 删除花名册分类
		var map = new HashMap();
		map.put("type","del_style_check");
		map.put("styleid",styleid);
		// map.put("moduleID",moduleID);
		map.put("musterType",musterType);
		Rpc({functionId:'MM01010003',success: function(form,action){
			var result = Ext.decode(form.responseText);
			var musterNum = result.styleMuster.length;
			if(musterNum > 0){
				Ext.showAlert(musterStyle.deleteNotAllow);
				//Ext.getCmp('win').destroy();
				musterManage.menuFocus = true;
			}else{
				Ext.MessageBox.buttonText={
				    yes:muster_sure,
				    no:muster_cancel
				}
				Ext.MessageBox.confirm(hint_information,muster_deleteClassify1 + tiptext + muster_deleteClassify2,callBack);
				function callBack(id){
        	    	if(id == muster_yes){	
        	   			var map = new HashMap();
			    		map.put("type","delete_lstyle");
						map.put("styleid",styleid);			
						Rpc({functionId:'MM01010003',success: function(form,action){
							//Ext.showAlert(musterStyle.deleteSucceed);
							musterManage.menuFocus = true;
						}},map);
        	    	}else{
        	    		//Ext.getCmp('win').destroy();
				        musterManage.menuFocus = true;
        	    	}				
			    }								
			}
		}},map);			   	   			
	 },
	 addStyle:function(addStyleWin){// 新增花名册分类
		var me = this;
		var style_text = Ext.getCmp('win_textfield').getValue() + '';
		var styleMap = new HashMap();
		var exist = false;
		var length = '';
		styleMap.put("type","check");//检查
		styleMap.put("musterType",musterType);
		styleMap.put("style_text",style_text);
		Rpc({functionId:'MM01010003',async:false,success:function(res){
			var resultData = Ext.decode(res.responseText);
			var styleClass = resultData.styleClass;
			length = styleClass.length;
			exist = resultData.result;
		}},styleMap);
		if(exist){
			Ext.showAlert(musterStyle.addHasExist);
			return;
		}
		if(length >= 150){
			Ext.showAlert(musterStyle.styleFull);
			return;
		}
		var map = new HashMap();
		if(me.trim(style_text) == ''){
			Ext.getCmp('win_textfield').setValue('');
			Ext.showAlert(musterStyle.addTextNotNull);
		}else if(me.trim(style_text).length > 15){
			Ext.showAlert(musterStyle.addMaxLengthText);
		}else{
			map.put('styledesc',me.trim(style_text));
			map.put('type','add_lstyle');
			map.put("musterType",musterType);
			Rpc({functionId:'MM01010003',async:false,success:function(result){
				var response = Ext.decode(result.responseText);	
				if("1"==response.state){
					Ext.showAlert("花名册分类最多150个！");
				}
				addStyleWin.hide();								
				//Ext.getCmp('win').setHeight(Ext.getCmp('win').getHeight() + 30);
				// Ext.getCmp('win').destroy();
				musterManage.menuFocus = true;
			},failure:function(result){Ext.showAlert(musterStyle.addFailed);}},map);
			// this.up('window').close();
			addStyleWin.destroy();								
		}				
	 },
	 trim:function(str){
		 return str.replace(/(^\s*)|(\s*$)/g, "");
	 },
	 deleteFn:function(){
		 musterClass.deleteStyle(musterClass.styleid,musterClass.tiptext);
	 }
});