/**
*薪资报表定义
*zhaoxg 2016-4-19
*/
Ext.define('SalaryReport.SalaryReportDefine',{
    constructor:function(config){
    	salaryReportDefineScope = this;
    	salaryReportDefineScope.rsdtlid = config.rsdtlid;
    	salaryReportDefineScope.salaryid = config.salaryid;
        salaryReportDefineScope.opt=config.opt;
	    var map = new HashMap();
		map.put("salaryid",config.salaryid);
		map.put("rsdtlid",config.rsdtlid);
		map.put("opt",config.opt);
	    Rpc({functionId:'GZ00000505',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){
				salaryReportDefineScope.data=result.data;
				salaryReportDefineScope.salaryReportName=result.salaryReportName;//报表名
				salaryReportDefineScope.isGroup = result.isGroup;//是否分组 or  按第一分组词分页打印
				salaryReportDefineScope.isPrintWithGroup = result.isPrintWithGroup;//分组分页打印
				salaryReportDefineScope.f_groupItem = result.f_groupItem;//第一分组指标
				salaryReportDefineScope.f_groupItemList=result.f_groupItemList;//第一分组列表
				salaryReportDefineScope.s_groupItem = result.s_groupItem;//第二分组指标
				salaryReportDefineScope.s_groupItemList = result.s_groupItemList;//第二分组列表
				salaryReportDefineScope.ownerType = result.ownerType;//0 公有 1 私有
				salaryReportDefineScope.reportStyleID = result.reportStyleID;//表类id
				salaryReportDefineScope.reportDetailID = result.reportDetailID;//报表id
	  		  	salaryReportDefineScope.init();
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
	},
	init:function(){
   		var store = Ext.create('Ext.data.Store', {
			fields:['itemid','itemdesc','isSelected'],
			data:salaryReportDefineScope.data
		});
		var panel = Ext.create('Ext.grid.Panel', {
			store: store,
			width: 348,
	    	height: 345,
	    	bufferedRenderer:false,//一起把数据去拿过来，不用假分页模式
	    	style:'margin:5px 5px 5px 5px;',
			border : 1,
	    	columnLines:true,
			rowLines:true,
			columns: [
				{
					header:'<input name="formulaflag1" type=checkbox id="selall" onclick="salaryReportDefineScope.selectALL(this);" checked/>',
					flex:10,
					menuDisabled:true,
					sortable:false,
					xtype:'templatecolumn',
					align:'center',
					tpl:'<input name="formulaflag" type=checkbox id={itemid} useflag={isSelected} onclick="salaryReportDefineScope.isSelectALL();"/>'
				},
				{ text: '项目名称',menuDisabled:true, dataIndex: 'itemdesc',flex:90,sortable:false}
			]
		});

		salaryReportDefineScope.radioPanel = Ext.create('Ext.panel.Panel', {
			border : false,
			bodyBorder:false,
			layout: 'vbox',
			id:"jiben",
			style:'margin-top:5px',
			items:[
					{
	        			xtype: 'fieldcontainer',
			            defaultType: 'radiofield',
			            layout: 'hbox',
			            align:'right',
			            width:350,
			            defaults:{flex:1},
		            	items: [
			            		//{xtype:'tbfill'},
			            {
			            	xtype : 'textfield',
					        name: 'itemname',
					        flex:3,
					        id:'texnameid',
					        maxLength:30,
							height:23,
					        allowBlank:false,
					        maxLengthText:'名称长度过长！',
					        value:salaryReportDefineScope.salaryReportName,
					        emptyText: '请输入名称',
					        style:'margin-left:10px',
			              	listeners:{
				            	'change':function(th,newvalue){
				                    	 salaryReportDefineScope.salaryReportName = newvalue;
				            	}
				            }
					    },
             			{
					        id:'pub',
					        name: 'mode',
					        width:50,
					        inputValue: '0',
					        style:'margin-left:10px',
					        checked:salaryReportDefineScope.ownerType=="0"?true:false,
					        boxLabel: '公有'
					    },
					    {
					        id:'pri',
					        name: 'mode',
					        width:50,
					        inputValue: '1',
					        checked:salaryReportDefineScope.ownerType=="1"?true:false,
					        boxLabel: '私有',
      						listeners:{
				            	'change':function(th,newvalue){
				            		var mode = "0";
				   					if(newvalue){
				   						mode = "1";
				   					}else{
				   						mode = "0";
				   					}
									salaryReportDefineScope.ownerType = mode;
				            	}
				            }
					    }
		            ]
		         },panel
		     ]
		})
		var css_template_tab="#templatetab-body {border-width: 0px 1px 1px 1px;}";//消除tab上边框
			Ext.util.CSS.createStyleSheet(css_template_tab,"tab_css");

		var tabs = Ext.create('Ext.tab.Panel', {
			layout:'auto',
			id:'templatetab',
			plain: true,
       	    items: [
       	        {
       	            title: "基本信息",
       	            itemId: 'information',
       	            listeners: { activate: salaryReportDefineScope.addTabPage }
       	        },
       	        {
       	        	title: "其他选项",
       	        	itemId: 'other',
       	        	hidden:salaryReportDefineScope.reportStyleID=="1"?true:false,
       	        	listeners: { activate: salaryReportDefineScope.addTabPage }
       	        }
       	    ]
       	});
	   	var win=Ext.widget("window",{
          title:'报表定义',
          height:500,
          width:370,
          layout:'fit',
		  modal:true,
		  resizable:false,
		  closeAction:'destroy',
          items: [{
         		xtype:'panel',
         		border:false,
				items:[tabs],
				buttons:[
	          		{xtype:'tbfill'},
	          		{
	          			text:'确定',
	          			handler:function(){
//	          				if(!Ext.getCmp("texnameid").isValid())
//	          					return;
	          				if(trim(Ext.getCmp("texnameid").value)==''){
	          					Ext.showAlert(common.msg.SalaryReportNameNotNull);
	          					return;
	          				}

		            		 if(!new RegExp(/^[^\\/\\:\\*\\?\"<> ]+$/).test(salaryReportDefineScope.salaryReportName)){//验证字符开头
		                            Ext.showAlert('名称不能包含空格和以下特殊字符 /\:*?"<>');
		                            return;
		                     }
	          				var rights = new Array();
	          				var formulaflag = document.getElementsByName("formulaflag");
							for(var i=0;i<formulaflag.length;i++){
								if(formulaflag[i].checked){
									rights.push(formulaflag[i].id);
								}
							}
							if(rights.length<=0){
								Ext.showAlert(common.msg.selectSalaryItems);
								return;
							}

							if((salaryReportDefineScope.reportStyleID=="2"||salaryReportDefineScope.reportStyleID=="12")&&
									(salaryReportDefineScope.f_groupItem==""&&salaryReportDefineScope.isPrintWithGroup=='1')){
								Ext.showAlert("请选择分组指标!");
								return;
							}

							if((salaryReportDefineScope.reportStyleID=="3"||salaryReportDefineScope.reportStyleID=="13")&&salaryReportDefineScope.f_groupItem==""){
								Ext.showAlert("请选择分组指标!");
								return;
							}
							var map = new HashMap();
							map.put("salaryid",salaryReportDefineScope.salaryid);
							map.put("salaryReportName",salaryReportDefineScope.salaryReportName);
							map.put("isGroup",salaryReportDefineScope.isGroup);
							map.put("isPrintWithGroup",salaryReportDefineScope.isPrintWithGroup);
							map.put("f_groupItem",salaryReportDefineScope.f_groupItem);
							map.put("s_groupItem",salaryReportDefineScope.s_groupItem);
							map.put("ownerType",salaryReportDefineScope.ownerType);
							map.put("reportStyleID",salaryReportDefineScope.reportStyleID);
							map.put("reportDetailID",salaryReportDefineScope.reportDetailID);
							map.put("right_fields",rights);
							map.put("actionType","saveDefine");
						    Rpc({functionId:'GZ00000506',async:false,success:function(form,action){
						    	var result = Ext.decode(form.responseText);
						    	var flag=result.succeed;
								if(flag==true){
                                    var text=result.salaryReportName;
                                    var tabid=result.reportDetailID;

									win.close();
									var treeStore = Ext.getCmp("reportTreePanel").getStore();

									var parentNode;
									if("add"==salaryReportDefineScope.opt){
                                        parentNode = treeStore.getNodeById("m"+salaryReportDefineScope.rsdtlid);
                                        var newNode="";
                                        newNode=parentNode.createNode(newNode);
                                        newNode.set("text",text);
                                        newNode.set("leaf",true);
                                        newNode.set("commonRepot","0");
                                        newNode.set("id",tabid);
                                        newNode.set("rsid",salaryReportDefineScope.rsdtlid);
                                        newNode.set("iconCls",'treeiconCls');
                                        parentNode.appendChild(newNode);

									}else if("edit"==salaryReportDefineScope.opt){
                                        parentNode = treeStore.getNodeById(salaryReportDefineScope.rsdtlid);
                                        parentNode.set("text",text);

                                        //同步更新薪资发放页面常用报表下拉列表
                                        var parentPage=undefined;
                                        if(salaryReportScope.model=='0'){
                                            parentPage=accounting;
										}
                                        else if(salaryReportScope.model=='1'){
                                            parentPage=spCollectScope;
										}else if(salaryReportScope.model == '3' ||salaryReportScope.model == '4' ){
											parentPage=SalaryHistoryData;
										}
										if(parentPage!=undefined){
                                            if(parentPage.commonreportlist.length==1){
                                                Ext.getCmp("common_Report_button").setText(text);
                                            }
                                            Ext.each(parentPage.commonreportlist,function (record) {
												if(record.id==salaryReportDefineScope.rsdtlid){
                                                    record.text=text;
												}
                                            });
										}
									}
                                    treeStore.commitChanges();
						  		}else{
									Ext.showAlert(result.message);
								}
						    }},map);
	          			}
	          		},
	          		{
	          			text:'取消',
	          			handler:function(){
	          				win.close();
	          			}
	          		},
	          		{xtype:'tbfill'}
	           ]
          }]
	    });
	    win.show();
    },
    //初始化薪资项目勾选项
   	selectFormula:function(){
		var formulaflag = document.getElementsByName("formulaflag");
		for(var i=0;i<formulaflag.length;i++){
			if(formulaflag[i].getAttribute("useflag")=="1"){
				formulaflag[i].checked=true;
			}else{
				document.getElementById("selall").checked=false;
			}
		}
	},
	isSelectALL:function(){
		var checkboxs = Ext.query("*[name=formulaflag]");
		var isbool=true;
		Ext.each(checkboxs,function(checkbox,index){
			if(!checkbox.checked)
				isbool=false;
		});
		document.getElementById("selall").checked=isbool;
	},
	//全选全撤
	selectALL:function(obj){
	    var checkboxs = Ext.query("*[name=formulaflag]");
		Ext.each(checkboxs,function(checkbox,index){
			checkbox.checked=obj.checked;
		});
	},
	//添加页签内容
	addTabPage:function(a){
		var templatetab = Ext.getCmp('templatetab');
		if(a.itemId=="information"){
			templatetab.child('#'+a.itemId).removeAll(false);
			templatetab.child('#'+a.itemId).add(salaryReportDefineScope.radioPanel);
			salaryReportDefineScope.selectFormula();
		}else if(a.itemId=="other"){
			templatetab.child('#'+a.itemId).removeAll(true);
			if(salaryReportDefineScope.reportStyleID=="2"||salaryReportDefineScope.reportStyleID=="12"){
				var itemStore = Ext.create('Ext.data.Store',
				{
					fields:['name','id'],
					data:salaryReportDefineScope.f_groupItemList
				})
				var panel = Ext.create('Ext.panel.Panel', {
					border : false,
					layout: 'auto',
					id:"qita",
					height:395,
					padding:'0 0 0 5',
					items:[
						{
				   			xtype:'checkbox',
				   			id:'isPrintWithGroup',
					        name: 'isPrintWithGroup',
					        checked:salaryReportDefineScope.isPrintWithGroup=="1"?true:false,
					        boxLabel: '是否分组',
					        listeners:{
				            	'change':function(th,newvalue){
				            		if(newvalue){
				            			salaryReportDefineScope.isPrintWithGroup="1";
										Ext.getCmp('item_combobox').enable();
										Ext.getCmp('isGroup').enable();
										Ext.getDom('isGroup-displayEl').removeAttribute('style');
										Ext.getDom('isGroup-boxLabelEl').removeAttribute('style');
				            		}else{
				            			salaryReportDefineScope.isPrintWithGroup="0";
				            			Ext.getCmp('item_combobox').disable();
				            			Ext.getCmp('isGroup').disable();
				            			//在低版本ie下opacity失效问题导致透明度效果没有
				            			Ext.getDom('isGroup-displayEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
				            			Ext.getDom('isGroup-boxLabelEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
				            		}
				            	}
				            }
				   		},{
					 	   	xtype:'fieldset',
					        title:"分组项",
					        layout:'column',
					        width:350,
					        height:150,
					        layout: 'vbox',
					   		items:[{
					   			id:'item_combobox',
					   			xtype:'combobox',
					   			fieldLabel:"分组指标",
					   			store:itemStore,
					   			displayField:'name',
                                editable:false,
					   			disabled:salaryReportDefineScope.isPrintWithGroup=="1"?false:true,
					   			valueField:'id',
					   			queryMode:'local',
					   			labelWidth:100,
					   			width:250,
					   			style:'margin-top:30px',
					   			matchFieldWidth:false,
					   			value:salaryReportDefineScope.f_groupItem,
					   			listeners:{
					   				select:function(combo,ecords){
										salaryReportDefineScope.f_groupItem = combo.getValue();
									}
								}
					   		},{
					   			xtype:'checkbox',
					   			id:'isGroup',
						        name: 'isGroup',
						        style:'margin-top:20px',
						        checked:salaryReportDefineScope.isGroup=="1"?true:false,
						        disabled:salaryReportDefineScope.isPrintWithGroup=="1"?false:true,
						        boxLabel: '分组分页打印',
						        listeners:{
						        	afterrender:function(combo){
						        		//在低版本ie下opacity失效问题导致透明度效果没有 sunjian 2017-7-4
						        		if(salaryReportDefineScope.isPrintWithGroup=="0") {
						        			Ext.getDom('isGroup-displayEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
					            			Ext.getDom('isGroup-boxLabelEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
						        		}
				       			  	},
					            	'change':function(th,newvalue){
					            		if(newvalue){
					            			salaryReportDefineScope.isGroup="1";
					            		}else
					            			salaryReportDefineScope.isGroup="0";
					            	}
					            }
				   			}]
					   	}
				    ]
				})
				templatetab.child('#'+a.itemId).add(panel);
			}else if(salaryReportDefineScope.reportStyleID=="3"||salaryReportDefineScope.reportStyleID=="13"){
				var itemStore = Ext.create('Ext.data.Store',
				{
					fields:['name','id'],
					data:salaryReportDefineScope.f_groupItemList
				})
				var itemStore1 = Ext.create('Ext.data.Store',
				{
					fields:['name','id'],
					data:salaryReportDefineScope.s_groupItemList
				})
				var panel = Ext.create('Ext.panel.Panel', {
					border : false,
					layout: 'auto',
					id:"qita1",
					height:395,
					padding:'0 0 0 5',
					items:[
						{
				   			xtype:'checkbox',
				   			id:'isPrintWithGroup',
					        name: 'isPrintWithGroup',
					        checked:salaryReportDefineScope.isPrintWithGroup=="1"?true:false,
					        boxLabel: '按第一分组词分页打印',
				        	listeners:{
				            	'change':function(th,newvalue){
				            		if(newvalue){
				            			salaryReportDefineScope.isPrintWithGroup="1";
				            		}else
				            			salaryReportDefineScope.isPrintWithGroup="0";
				            	}
				            }
				   		},{
					 	   	xtype:'fieldset',
					        title:"分组项",
					        layout:'column',
					        width:350,
					        height:150,
					        layout: 'vbox',
					   		items:[{
					   			id:'item_combobox1',
					   			xtype:'combobox',
					   			fieldLabel:"第一分组词",
					   			store:itemStore,
					   			displayField:'name',
					   			isDisabled:true,
                                editable:false,
                                valueField:'id',
					   			queryMode:'local',
					   			labelWidth:100,
					   			width:250,
					   			style:'margin-top:30px',
					   			listConfig:{width:145},
					   			matchFieldWidth:false,
					   			value:salaryReportDefineScope.f_groupItem,
					   			listeners:{
					   				'select':function(combo,ecords){
					   					if(salaryReportDefineScope.s_groupItem==combo.getValue()){
					   						Ext.showAlert("第一分组指标和第二分组指标不能相同！");
					   						Ext.getCmp('item_combobox1').setValue(salaryReportDefineScope.f_groupItem);
					   						return;
					   					}
										salaryReportDefineScope.f_groupItem = combo.getValue();
									}
								}
					   		},{
					   			id:'item_combobox2',
					   			xtype:'combobox',
					   			fieldLabel:"第二分组词",
					   			store:itemStore1,
					   			displayField:'name',
					   			isDisabled:true,
                                editable:false,
					   			valueField:'id',
					   			queryMode:'local',
					   			labelWidth:100,
					   			listConfig:{width:145},
					   			width:250,
					   			style:'margin-top:30px',
					   			matchFieldWidth:false,
					   			value:salaryReportDefineScope.s_groupItem==''?0:salaryReportDefineScope.s_groupItem,
					   			listeners:{
					   				'select':function(combo,ecords){
					   					if(salaryReportDefineScope.f_groupItem==combo.getValue()){
					   						Ext.showAlert("第一分组指标和第二分组指标不能相同！");
					   						Ext.getCmp('item_combobox2').setValue(salaryReportDefineScope.s_groupItem);
					   						return;
					   					}
										salaryReportDefineScope.s_groupItem = combo.getValue();
									}
								}
					   		}]
					   	}
				    ]
				})
				templatetab.child('#'+a.itemId).add(panel);
			}
		}
	}
})
