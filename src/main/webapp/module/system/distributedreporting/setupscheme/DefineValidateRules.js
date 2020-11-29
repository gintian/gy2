/**
 * 定义校验规则js
 */
Ext.define('SetupschemeUL.DefineValidateRules',{
     constructor:function(config){//构造方法
    	defineValidateRules=this;
    	defineValidateRules.init(config);
     },
	 init:function(config){
		 defineValidateRules.createValidateRulesWindow();
	 },
	 /**
	  * 定义校验规则
	  */
	 createValidateRulesWindow:function(){
		if(!Ext.getCmp("validateRulesWindow")){
			var validateRulesPanel = defineValidateRules.createValidateRulesPanel();
			var validateRulesWindow=Ext.create('Ext.window.Window', {
			    id:'validateRulesWindow',
			    title:"<div align='left' >"+data_validation_rule+"</div>",//style='background:#f3f3f3;font-size:14px;'
			    height: 400,
			    resizable : false,//禁止缩放
			    width: 700,
			    modal:true,
			    layout: 'vbox',
			    buttonAlign: 'center',
			    tbar:[{
			    	 text : newly_added,
			    	 height: 22,
				     handler :function(){
				    	 defineValidateRules.add('add');
				     }
			    },{
			    	 text : modify,
			    	 height: 22,
				     handler :save
			    },{
			    	 text : deletes,
			    	 height: 22,
				     handler :remove
			    }],
			    items: [
			    	validateRulesPanel
			    ],
			    buttons:[{ 
				     text : sure,
				     height: 22,
				     handler :function(){
				     	var records=validateRulesPanel.getStore().getModifiedRecords();
				     	var parameter ="";
				     	if(records.length>0){
				     		Ext.each(records,function(record){
					     		var	checkId =record.get("checkId");
					     		var	forcestate =record.get("forcestate")==true?1:0;
					     		var	valid =record.get("valid")==true?1:0;
					     		var	condition =record.data.condition==""?null:record.data.condition;
					     		parameter=checkId+","+forcestate+","+valid+","+condition
					     	});
					     	var map = new HashMap();
					     	//map.put("parameter",getEncodeStr(parameter));
					     	map.put("parameter",parameter);
					     	map.put("flag","update");//修改校验规则部分数据
					     	Rpc({functionId:'SYS0000003016',success: function(form,action){
								var result = Ext.decode(form.responseText);
								if(result.succeed){
									if(validateRulesWindow){
										validateRulesWindow.close();
									}
									/*Ext.getCmp('validateRulesPanel').getStore().reload();
									Ext.MessageBox.alert(hint_information,setmatch_save_succeed);*/
									
								}else{
									Ext.MessageBox.alert(hint_information,result.message);
								}
						    }},map);
				     	}else{
				     		if(validateRulesWindow){
								validateRulesWindow.close();
							}
				     		//Ext.MessageBox.alert(hint_information,setmatch_save_succeed);
				     	}
				     }
			    },{ 
				     text : createinput_quit,
				     height: 22,
				     handler :function(){
				     	Ext.getCmp("validateRulesWindow").close();
				     }
			    }]
	 	  }).show();
	  }
	  //修改
	  function save(){
	  	var grid = Ext.getCmp('validateRulesPanel');
		var record=grid.getSelectionModel().getSelection();
		if(record.length<=0){
			Ext.MessageBox.alert(hint_information,please_select_a_rule_that_needs_to_be_modified);
			return;
		}
		if(record.length>1){
			Ext.MessageBox.alert(hint_information,you_can_only_choose_one_rule);
			return;
		}
		var data = record[0].data;
		var checkedid = data.checkId;
		defineValidateRules.add(checkedid);
	  }
	  //删除
	  function remove(){
		var grid = Ext.getCmp('validateRulesPanel');
		var record=grid.getSelectionModel().getSelection();
		if(record.length<=0){
			Ext.MessageBox.alert(hint_information,please_select_the_rules_that_need_to_be_deleted);
			return;
		}
		Ext.Msg.confirm(hint_information,whether_delete_it,function(btn){ 
			if(btn=="yes"){ 
				// 确认触发，继续执行后续逻辑。
			    var map = new HashMap();
			    var checkedids = "";
			    var exper="";
			    for ( var i = 0; i < record.length; i++) {
			    	var temp =record[i].data;
			        checkedids+=temp.checkId+",";
			    }
			    map.put("checkedids",checkedids);
			    map.put("flag","del");//删除校验规则
			    Rpc({functionId:'SYS0000003016',success: function(form,action){
					var result = Ext.decode(form.responseText);
					if(result.succeed){
						Ext.getCmp('validateRulesPanel').getStore().reload();
					}else{
						Ext.MessageBox.alert(hint_information,result.message);
					}
			    }},map);
			} 
		});
	  }
	 },
	 //新增和修改规则
	 add:function(flag){
  	    var name = revise_rules;
  	    var checkname = undefined;
  	    var checkField = undefined;
  	    var condition = undefined;
  	    var forcestate = undefined;
  	    var valid = undefined;
  	    if("add"==flag){
  	    	name = add_rules;
  	    }
	  	Ext.create('Ext.window.Window', {
		  	id:'addValidateRulesWindow',
		    title: name,
		    height: 400,
		    resizable : false,//禁止缩放
		    width:560,
		    modal:true,
		    layout: {
					align: 'center',
					type: 'vbox'
			},
		    buttonAlign: 'center',
		    items: [{
		    	xtype: 'fieldset',
				height:185,
				width:520,
				title:setting_rules,
				layout: 'vbox',
				items:[/*{
			    	border:false,
			    	html:'<div style="color:red">'+rule_field_info+'</div>',
			    	style:'margin-left:0px;'
			    },*/{
					xtype: 'textfield',
					style:'margin-top:5px;',
					fieldLabel : rules_name,
					allowBlank:false,
					width:495,
					emptyText : please_input_the_rule_name,
					id:'checkname',
					//regex:/^\S{0,30}$/,
					//regexText: rule_names_must_length
				},{
					xtype: 'textfield',
					fieldLabel : check_field,
					emptyText : please_input_check_field,
					
					style:'margin-top:5px;',
					width:495,
					id:'checkField',
					listeners : {
                        render: function (field, p) {
                             Ext.QuickTips.init();
                             Ext.QuickTips.register({
                            	 target: field.el,
                            	 text: rule_field_info
                             })
                          }
                   }
				},{
					xtype: 'textarea',
					fieldLabel : checkout_condition,
					height: 80,
					style:'margin-top:5px;',
					width:495,
					emptyText : please_input_checkout_condition,
					id:'condition'
				}]
		    },{
		    	xtype: 'fieldset',
				height:50,
				width:520,
				title:whether_or_not_compulsory,
				defaultType: 'radio',
				id:'forcestates',
				defaults: {
                    flex: 1
                },
				layout: 'hbox',
				items:[
					{ boxLabel: compulsion, name: 'forcestate', inputValue: 1},
                    { boxLabel: not_compulsion, name: 'forcestate', inputValue: 0, checked: true} //, checked: true
                ]
		    },{
		    	xtype: 'fieldset',
				height:50,
				width:520,
				title:is_it_enabled,
			    id:'valids',
				defaultType: 'radio',
				defaults: {
                    flex: 1
                },
				layout: 'hbox',
				items:[
					{ boxLabel: enable, name: 'valid', inputValue: 1},
                    { boxLabel: not_enable, name: 'valid', inputValue: 0,checked: true}//
                ]
		    }],
		    buttons:[{ 
			     text : sure,
			     height: 22,
			     handler :function(){
			     	var checkname = Ext.getCmp("checkname").getValue();
			     	if(checkname.length>30){
			     		Ext.Msg.alert(hint_information,rule_names_must_length);
			     		return;
			     	}
			     	if(""==checkname){
			     		Ext.Msg.alert(hint_information,rule_names_must_not_be_empty);
			     		return;
			     	}else{
				     	var checkField = Ext.getCmp("checkField").getValue();
				     	if(checkField.length>100){
				     		Ext.Msg.alert(hint_information,rule_fields_must_length);
				     		return;
				     	}
				     	var condition = Ext.getCmp("condition").getValue();
				     	var forcestates = Ext.getCmp('forcestates').items;
				     	var forcestatevalue = 0;
				     	for(var i = 0; i < forcestates.length; i++){
                          if(forcestates.get(i).checked  == true){
                              forcestatevalue = forcestates.get(i).inputValue;
                          }
                        }
                        var valids = Ext.getCmp('valids').items;
				     	var validvalue = 0;
				     	for(var i = 0; i < valids.length; i++){
                          if(valids.get(i).checked  == true){
                              validvalue = valids.get(i).inputValue;
                          }
                        }
                        var map = new HashMap();
                        map.put("checkname",checkname);
                        map.put("checkField",checkField);
                        //map.put("condition",getEncodeStr(condition));
                        map.put("condition",condition);
                        map.put("forcestate",forcestatevalue);
                        map.put("valid",validvalue);
                        if("add"==flag){
                        	map.put("flag","add");//新增校验规则
                        	Rpc({functionId:'SYS0000003016',success: function(form,action){
								var result = Ext.decode(form.responseText);
								if(result.succeed){
									Ext.getCmp("addValidateRulesWindow").close();
									Ext.getCmp('validateRulesPanel').getStore().reload();
								}else{
									Ext.MessageBox.alert(hint_information,result.message);
								}
							}},map);
			     		}else{
			     			map.put("checkId",flag);
			     			map.put("flag","updataAll");//修改校验规则
			     			Rpc({functionId:'SYS0000003016',success: function(form,action){
								var result = Ext.decode(form.responseText);
								if(result.succeed){
									Ext.getCmp("addValidateRulesWindow").close();
									Ext.MessageBox.alert(hint_information,amend_the_success);
									Ext.getCmp('validateRulesPanel').getStore().reload();
								}else{
									Ext.MessageBox.alert(hint_information,result.message);
								}
							}},map);
			     		}
			     	}
			     }
		    },{ 
			     text : cancel,
			     height: 22,
			     handler :function(){
			     	Ext.getCmp("addValidateRulesWindow").close();
			     }
		    }]
	    }).show();
	    if("add"!=flag){
  	    	var map = new HashMap();
  	    	map.put("checkId",flag);
  	    	Rpc({functionId:'SYS0000003018',success: function(form,action){
					var result = Ext.decode(form.responseText);
					if(result.succeed){
						checkname =result.checkname;
						checkField =result.checkField;
						condition =result.condition;
						forcestate =result.forcestate;
						valid =result.valid;
						checkId = result.checkname;
						if(checkname!="undefined"){
					    	Ext.getCmp("checkname").setValue(checkname);
					    }
					    if(checkField != undefined){
					    	Ext.getCmp("checkField").setValue(checkField);
					    }
					    if(checkField != undefined){
					    	Ext.getCmp("condition").setValue(condition);
					    }
					    if(forcestate != undefined){
					    	var forcestates = Ext.getCmp('forcestates').items;
					    	for(var i = 0; i < forcestates.length; i++){
				                  if(forcestates.get(i).inputValue  == forcestate){
				                      forcestates.get(i).setValue(true);
				                  }
				            }
					    }
					    if(valid != undefined){
					    	var valids = Ext.getCmp('valids').items;
					    	for(var i = 0; i < valids.length; i++){
				                  if(valids.get(i).inputValue  == valid){
				                     valids.get(i).setValue(true);
				                  }
				           }
					    }
					}else{
						Ext.MessageBox.alert(hint_information,result.message);
					}
			}},map);
	    }
     },
     createValidateRulesPanel:function(){
	  	var validateRulesStore = Ext.create('Ext.data.Store', {
	             storeId: 'validateRulesStoreId',
	             fields:['checkId','checkname','checkField','condition','forcestate','valid'],
	             proxy:{
	            	 type: 'transaction',
				     functionId:'SYS0000003015',
	                 reader: {
	                     type: 'json',
	                     root: 'list'
	                 }
	             },
	             autoLoad: true
	        });
	  	var validateRulesPanel =Ext.create('Ext.grid.Panel', {
				store:validateRulesStore,
				width:670,
				style:'margin-top:10px;margin-left:9px;',
				selType: 'checkboxmodel',//添加复选框列
			 	height:250,
			 	id:'validateRulesPanel',
			 	border:true,
			 	scrollable:"y",
			 	bufferedRenderer:false,
			 	multiSelect:true,//
			 	//menuDisabled:true,
			 	enableHdMenu:false,//是否显示表头的上下文菜单，默认为true
			 	enableColumnHide:false,//是否允许通过标题中的上下文菜单隐藏列，默认为true
			 	forceFit:true,
			 	hideHeaders:false,
			 	plugins:[  
	             	Ext.create('Ext.grid.plugin.CellEditing',{  
			                     clicksToEdit:1 //设置单击单元格编辑  
			        })  
			    ],
			 	columnLines:true,//显示grid.Panel数据列之间的竖线
			 	listeners: {
			        rowdblclick :function(grid, rowIndex, e) {
			           var record=grid.getSelectionModel().getSelected();
			           var data = record.items[0].data;
					   var checkedid = data.checkId;
					   defineValidateRules.add(checkedid);
			        },
			    	render : function(panel){							
						Ext.create('Ext.tip.ToolTip', {
						    target: panel.body,
						    delegate:"td > div.x-grid-cell-inner",
						    shadow:false,
						    id:'validateRulesPanel_celltip',
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
			    },
				columns: [
				    {text:'id',dataIndex:'checkId',hidden:true},
				    { text: rules_name, dataIndex: 'checkname',xtype : 'gridcolumn',align : 'left',width:150,sortable:false},
				    { text: check_field,dataIndex: 'checkField',align : 'left',width: 100,sortable:false},
				    { text: checkout_condition, dataIndex: 'condition',align : 'left',width: 200,editor:new Ext.form.TextField(),sortable:false},
				    { text: compulsion, dataIndex: 'forcestate',align : 'center',xtype : 'checkcolumn',width:50,sortable:false},
				    { text: enable, dataIndex: 'valid',align : 'center',xtype : 'checkcolumn',width:50,sortable:false}
				],
				renderTo:Ext.getBody()
			});
		return validateRulesPanel;
	  }
 });
