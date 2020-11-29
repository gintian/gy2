/**
 * 上报方式
 */
Ext.define('SetupschemeUL.CreateImportSchema',{
	schemeid:"",//id
	rowindx:"",//行号
	constructor:function(config) {
		createImportSchema_me = this;
		schemeid = config.schemeid;
		rowindx = config.rowindx;
		this.init(config);
	},
	init:function(config){
		var map = new HashMap();
		map.put("schemeid",schemeid);// 编号id
		map.put("type","getschemeparam");// 编号id
		Rpc({functionId : 'SYS0000003004',async:false,success:function(form) {
				var result = Ext.decode(form.responseText);
				if(!result.sflag){
					Ext.Msg.alert(setmatch_tips, setmatch_before_tips);// 请先定义数据标准
					return;
				}
				if(result.succeed){
					createImportSchema_me.dbconfig = result.dbconfig;
					createImportSchema_me.createImportSchemaWin();
				}else{
					Ext.Msg.alert(createinput_error,createinput_error_explain);//连接失败
					return;
				}
			}
		}, map);
	},
	/**
	 * 创建导入窗口window
	 */
	createImportSchemaWin : function(){
		//数据库里已经保存的配置信息
		var dbconfig = createImportSchema_me.dbconfig;
		var import_type = dbconfig.import_type;
		//接收方式的store
		var planStore = Ext.create("Ext.data.Store", {
				fields : ["Name", "Value"],
				data : [{Name : createinput_receptionbyhand,Value : "0"},{Name : createinput_middledb,Value : "1"},{Name : createinput_ftp,Value : "2"},{Name : createinput_webservice,Value : "3"}]
			});
		// 主窗口
		var win = Ext.create("Ext.window.Window", {
					title:'<p style="font-size: 15px;">'+createinput_createinput+'</p>',//创建导入方案
					layout : {
						type:'vbox',
						align:"left"
					},
					resizable : false,// 禁止缩放
					width : 540,
					modal:true,
					id:"inputwin",
					items : [{
						xtype:"combobox",
						store:planStore,
						id:"plancombo",
						editable : false,
						displayField : "Name",
						valueField : "Value",
						emptyText : createinput_plantype_tip,
						queryMode : "local",
						fieldLabel : createinput_plantype_labal,
						name : 'plantype',
						labelWidth:70,
						margin: '10 0 5 10',
						listeners: {
					        'select':function(combo,record, eOpts ) {
					        	mainform.removeAll(false);
					            var value = record.data.Value;
					            if(value=='0'){
									mainform.add(shougongPanel);
								}
					            if(value=='1'){
									mainform.add(dbform);
								} 
								if(value=='2'){
									mainform.add(ftpform);
								}
								if(value=='3'){
									mainform.add(wbsPanel);
								}
					        }
					    }
					}],
					buttonAlign: 'center',
					buttons:[{ 
					     text : createinput_sure,//确定
					     height: 25,
					     margin:"0 20 0 0",
					     handler :function(){
				    		var formValue = mainform.getValues();
				    		var import_type = Ext.getCmp("plancombo").getValue();
				    		if(import_type=='2'){
				    			if(formValue["ip"].length == 0||formValue["port"].length == 0||formValue["username"].length == 0){
									Ext.Msg.alert(createinput_tips, createinput_dbtestcontips);//测试前请完善连接配置
									return;
								}
				    		}else{
				    			for(index in formValue){
									if(formValue[index].length==0){
										Ext.Msg.alert(createinput_tips,createinput_urltips);//请完善连接配置
										return;
									}				     		
						     	}
				    		}
					     	var map = new HashMap();
					     	var reporttype = Ext.getCmp('reporttype').items;
					     	for(var i=0;i<reporttype.items.length;i++){
					     		var item = reporttype.items[i];
					     		if(item.checked){
					     			map.put("reporttype",item.inputValue);//上报类型 增量还是全量
					     			break;
					     		}
					     	}
					     	var isenable = Ext.getCmp('isenable');
					     	if(isenable.checked){
					     		map.put("isenable","1");//启用
					     	}else{
					     		map.put("isenable","0");//不启用
					     	}
					     	map.put("type","savescheme");//操作类型
					     	map.put("schemeids",schemeid);//编号id
					     	map.put("import_type",import_type);//上报方式
					     	
					     	if('0'==import_type){//手工上报方式
					     		//只保存类型。路径从constant表里面取
					     	}
					     	if('1'==import_type){//数据库的方式
					     		map.put("dbname",formValue["dbname"]);//数据库名
						     	map.put("dbtype",formValue["dbtype"]);//数据库类型
						     	map.put("dburl",formValue["dburl"]);//数据库链接
						     	map.put("dbusername",formValue["dbusername"]);//用户名
						     	map.put("password",formValue["password"]);//密码
						     	map.put("port",formValue["port"]);//端口号
					     	}
					     	if('2'==import_type){//FTP方式
					     		map.put("datapath",formValue["datapath"]);//路径
						     	map.put("ip",formValue["ip"]);//ip
						     	map.put("username",formValue["username"]);//用户名
						     	map.put("password",formValue["password"]);//密码
						     	map.put("port",formValue["port"]);//端口号
					     	}
					     	if('3'==import_type){//Webservice方式
						     	map.put("serviceUrl",formValue["serviceUrl"]);
					     	}
					    	Rpc({functionId : 'SYS0000003004',async : false,success : function(form, action) {
									var result = Ext.decode(form.responseText);
									if(result.issucceed){
										setup_scheme.reload();
										win.close();
									}else{
										Ext.Msg.alert(createinput_error,createinput_saveconfig_error);//保存配置出错
									}
								}
					    	},map);
					     }
				    },{
					     text : createinput_quit,//取消
					     height: 25,
					     margin:"0 0 0 20",
					     handler :function(){
					     	win.close();
					     }
				    }]
				});
		//设置默认以中间库的方式上报
		Ext.getCmp("plancombo").select(import_type);
		//主form 
		var mainform = Ext.create("Ext.form.Panel", {
					border : 0,
					id :'mainform',
					//height : 400,
					width : 510,
					margin:"0 0 0 10",
				});
		//数据库store
		var dbStore = Ext.create("Ext.data.Store", {
				fields : ["Name", "Value"],
				data : [{
					Name : "ORACLE",
					Value : "1"
				}, {
					Name : "MSSQL",
					Value : "2"
				}]
			});
		//手工上报panel
		var shougongPanel = Ext.create("Ext.form.Panel", {
			defaults : {
				anchor : '100%'
			},
			height:182,
			layout : {
				type:'vbox',
				align:"left"
			},
			items:[{
				border:0,
				html:createinput_datastorepath,//'数据存放目录'
				margin: '20 0 0 20'
			},{
				border:1,
				height:20,
				width:'85%',
				margin:"2 0 10 20",
				html:'<div style="color:#ADADAD">'+dbconfig.path+'<div>'
			}]
		});
		//ftp上报panel
		var ftpform = Ext.create("Ext.form.Panel", {
			xtype:"container",
			defaultType: 'textfield',
			layout:'column',
			defaults : {
				anchor : '100%'
			},
			height:182,
			items:[{
				columnWidth:1.2,
				xtype:"container",
				defaultType: 'panel',
				margin: '20 0 0 20',
				height:20,
				layout:'column',
				items:[{
					border:0,
					columnWidth:0.2,
					html:createinput_datastorepath,//'数据存放目录'
				},{
					border:true,
					height:20,
					columnWidth:0.80,
					margin:"0 0 0 14",
					html:'<div style="color:#ADADAD">'+dbconfig.path+'<div>'
				}]
			},{
				fieldLabel:createinput_ftpaddress,//'FTP地址'
				columnWidth:0.6,
				name : 'ip',
				value:dbconfig.ip,
				emptyText : createinput_ftpip,//请输入ip地址
				allowBlank: false,
				margin: '5 0 0 20'
			},{
				fieldLabel:createinput_dbport,
				columnWidth:0.6,
				allowBlank: false,
				emptyText : createinput_dbporttips,// 请输入端口号
				name : 'port',
				value:dbconfig.ftpport,
				margin: '5 0 0 20'
			},{
				fieldLabel:createinput_ftpusername,//'访问FTP用户'
				columnWidth:0.6,
				allowBlank:false,
				name : 'username',
				emptyText : createinput_dburlusernametips,//请输入用户名
				value:dbconfig.username,
				margin: '5 0 0 20'
			},{
				fieldLabel:createinput_dbpassword,//密码
				emptyText : createinput_dbpasswordtips,// 请输入密码
				columnWidth:0.6,
				name : 'password',
				inputType:'password', 
				value:dbconfig.ftppassword,
				allowBlank: true,
				margin: '5 0 0 20'
			},{
				fieldLabel:createinput_datasavepath,//'数据保存目录'
				columnWidth:1.2,
				allowBlank: true,
				name : 'datapath',
				value:dbconfig.datapath,
				emptyText : createinput_dataftppath,//请输入数据在ftp上的路径
				margin: '5 0 0 20'
			},{
				xtype:'button',
				text:createinput_dbtestcon,//"测试连接"
				columnWidth:0.3,
				height:23,
				margin: '5 0 0 20',
				handler: function() {
					var ftpValues = ftpform.getValues();
					if(ftpValues["ip"].length == 0||ftpValues["port"].length == 0||ftpValues["username"].length == 0){
						Ext.Msg.alert(createinput_tips, createinput_dbtestcontips);//测试前请完善连接配置
						return;
					}
					var map = new HashMap();
					map.put("type", "testftpconnection");// 操作方式
					map.put("ip", ftpValues["ip"]);// 数据库链接
					map.put("username",ftpValues["username"]);// 用户名
					map.put("password", ftpValues["password"]);// 密码
					map.put("port", ftpValues["port"]);// 端口号
					map.put("datapath", ftpValues["datapath"]);// 数据保存目录
					Rpc({
							functionId : 'SYS0000003004',
							async : false,
							success : function(form, action) {
								var result = Ext.decode(form.responseText);
								if (result.issucceed) {
									Ext.Msg.alert(createinput_tips, createinput_dbtestsucceed);//连接成功
								} else {
									Ext.Msg.alert(createinput_error, createinput_dbtestfail);//连接失败
								}
							}
						}, map
					);
			    }
			},{
				xtype:'container',
				border:0,
				html:createinput_ftpdescribe,//'(所有上报单位一致)'
				margin: '5 0 0 5'
			}]
		});
		//wbs上报panel
		var wbsPanel = Ext.create("Ext.form.Panel", {
			defaultType: 'textfield',
			layout:'column',
			defaults : {
				anchor : '100%'
			},
			height:182,
			items:[{
				columnWidth:1.0,
				xtype:"container",
				defaultType: 'panel',
				margin: '20 0 0 20',
				height:20,
				layout:'column',
				items:[{
					border:0,
					columnWidth:0.2,
					html:createinput_datastorepath,//'数据存放目录'
				},{
					border:true,
					height:20,
					columnWidth:0.75,
					margin:"0 0 0 14",
					html:'<div style="color:#ADADAD">'+dbconfig.path+'<div>'
				}]
			},{
				xtype:'container',
				border:0,
				columnWidth:0.7,
				html:createinput_wsdldescribe,//'下级上报数据,WebService地址(所有上报单位一致):'
				margin: '7 0 0 20'
			},{
				xtype:'button',
				text: createinput_dbtestcon,
				columnWidth:0.25,
				height:23,
				margin: '5 0 0 5',
				handler: function() {
					var wsdlValues = wbsPanel.getValues();
					for (index in wsdlValues) {
						if (wsdlValues[index].length == 0) {
							Ext.Msg.alert(createinput_tips, createinput_dbtestcontips);//测试前请完善连接配置
							return;
						}
					}
					var map = new HashMap();
					map.put("type", "testwsdlconnection");// 操作方式
					map.put("serviceUrl", wsdlValues["serviceUrl"]);//wsdl地址
					Rpc({
							functionId : 'SYS0000003004',
							async : false,
							success : function(form, action) {
								var result = Ext.decode(form.responseText);
								if (result.issucceed) {
									Ext.Msg.alert(createinput_tips, createinput_dbtestsucceed);//连接成功
								} else {
									Ext.Msg.alert(createinput_error, createinput_dbtestfail);//连接失败
								}
							}
						}, map
					);
			    }
			},{
				xtype:'textareafield',
				name:'serviceUrl',
				value:dbconfig.serviceUrl,
				columnWidth:0.95,
				margin: '5 0 0 20',
				height:90
			}]
		});
		//数据库配置form
		var dbform = Ext.create("Ext.form.Panel", {
					border:0,
					items : {
						xtype : 'fieldset',
						columnWidth : 0.5,
						title : createinput_dbmessage,//设置报送库信息
						collapsible : false,//不可滚动
//						padding:"10 0 10 0",
						width : 508,
						height:172,
						defaults : {
							anchor : '100%'
						},
						layout : {
							type:'hbox',
							align:"middle",
							pack:"center"
						},
						items : [{
							xtype:"container",
							defaultType: 'textfield',
							defaults : {
								anchor : '100%'
							},
							layout : {
								type:'anchor'
							},
							items : [{
								xtype:"combobox",
								store:dbStore,
								editable : false,
								displayField : "Name",
								valueField : "Value",
								emptyText : createinput_dbtypetips,//请选择数据库类型
								queryMode : "local",
								fieldLabel : createinput_dbtype,//数据库类型
								name : 'dbtype',
								labelWidth:70,
								margin: '10 0 5 0',
								value:dbconfig.dbtype
							}, {
								fieldLabel : createinput_dburl,//数据库地址
								emptyText : createinput_dburltips,//请输入数据库地址
								name : 'dburl',
								margin: '15 0 5 0',
								labelWidth:70,
								value:dbconfig.dburl
							},{
								fieldLabel : createinput_dburlusername,//用户名
								emptyText : createinput_dburlusernametips,//请输入用户名
								name : 'dbusername',
								margin: '15 0 5 0',
								labelWidth:70,
								value:dbconfig.dbusername
							},{
								xtype : "button",
								text : createinput_dbtestcon,//测试连接
								margin: '15 0 5 0',
								height:23,
								wide:45,
								handler : function() {
									var dbValues = dbform.getValues();
									for (index in dbValues) {
										if (dbValues[index].length == 0) {
											Ext.Msg.alert(createinput_tips, createinput_dbtestcontips);//测试前请完善连接配置
											return;
										}
									}
									var map = new HashMap();
									map.put("type", "testdbconnection");// 操作方式
									map.put("dbname", dbValues["dbname"]);// 数据库名
									map.put("dbtype", dbValues["dbtype"]);// 数据库类型
									map.put("dburl", dbValues["dburl"]);// 数据库链接
									map.put("dbusername",dbValues["dbusername"]);// 用户名
									map.put("password", dbValues["password"]);// 密码
									map.put("port", dbValues["port"]);// 端口号
									Rpc({
											functionId : 'SYS0000003004',
											async : false,
											success : function(form, action) {
												var result = Ext.decode(form.responseText);
												if (result.issucceed) {
													Ext.Msg.alert(createinput_tips, createinput_dbtestsucceed);//连接成功
												} else {
													Ext.Msg.alert(createinput_error, createinput_dbtestfail);//连接失败
												}
											}
										}, map
									);
								}
							}/*,{
								xtype : "button",
								text : createinput_createmidtable,//创建中间库表
								margin: '15 0 5 12',
								height:23,
								wide:45,
								handler : function() {
									Ext.Msg.confirm(createinput_tips, createinput_createmidtabletips,function(id){//一键创建中间库表结构，但是会清除现有中间库表结构与数据，是否继续？
										if("no"==id){//点否返回
											return;
										}
										var dbValues = dbform.getValues();
										for (index in dbValues) {
											if (dbValues[index].length == 0) {
												Ext.Msg.alert(createinput_tips, createinput_beforecreatemidtabletips);//创建库表结构前请完善连接配置
												return;
											}
										}
										var map = new HashMap();
										map.put("type", "testdbconnection");// 操作方式
										map.put("dbname", dbValues["dbname"]);// 数据库名
										map.put("dbtype", dbValues["dbtype"]);// 数据库类型
										map.put("dburl", dbValues["dburl"]);// 数据库链接
										map.put("dbusername",dbValues["dbusername"]);// 用户名
										map.put("password", dbValues["password"]);// 密码
										map.put("port", dbValues["port"]);// 端口号
										Rpc({
												functionId : 'SYS0000003027',
												async : false,
												success : function(form, action) {
													var result = Ext.decode(form.responseText);
													if (result.flag=="success") {
														Ext.Msg.alert(createinput_tips, createinput_beforecreatemidtable_succeed);// 创建成功
													} else {
														Ext.Msg.alert(createinput_tips, createinput_beforecreatemidtable_fail);// 创建失败
													}
												}
											}, map);
									});
								}
							}*/]
						}, {
							xtype:"container",
							defaultType: 'textfield',
							margin:"0 0 0 20",
							defaults : {
								anchor : '100%'
							},
							layout : 'anchor',
							items : [{
								fieldLabel : createinput_dbname,// 数据库名称
								emptyText : createinput_dbnametips,// 请输入数据库名称
								name : 'dbname',
								margin: '7 0 5 0',
								labelWidth:70,
								value:dbconfig.dbname
							}, {
								fieldLabel : createinput_dbport,// 端口号
								emptyText : createinput_dbporttips,// 请输入端口号
								name : 'port',
								margin: '15 0 5 0',
								labelWidth:70,
								value:dbconfig.port
							},{
								fieldLabel : createinput_dbpassword,// 密码
								emptyText : createinput_dbpasswordtips,// 请输入密码
								name : 'password',
								margin: '15 0 5 0',
								labelWidth:70,
								inputType:'password',  
								value:dbconfig.password
							},{
								xtype : "container",
								margin: '15 0 5 0',
								height:25
							}]
						}]
					}

				});
		// 上报方式：1全量，2增量
		var importType = Ext.create("Ext.form.FieldSet", {
					// Fieldset in Column 1 - collapsible via toggle button
					columnWidth : 0.5,
					title : createinput_inputtype,// 上报方式
					collapsible : false,
					defaultType : 'textfield',
					//width:'96%',
					width : 508,
					margin: '0 0 0 10',
					layout : 'anchor',
					items : [{
						xtype : 'fieldcontainer',
						defaultType : 'radiofield',
						id:'reporttype',
						layout : 'hbox',
						items : [{
							boxLabel : createinput_inputtype1,// 全量上报
							name : 'reporttype',
							id:"reporttype1",
							inputValue : '1',
							flex:1
						}, {
							boxLabel : createinput_inputtype2,// 增量上报
							name : 'reporttype',
							id:"reporttype2",
							inputValue : '2',
							flex:3
						}]
					}]
				});
				//设置默认选中的上报类型
				if(dbconfig.reporttype=="2"){
					Ext.getCmp("reporttype2").setValue(true);
				}else{
					Ext.getCmp("reporttype1").setValue(true);
				}
				//是否启用方案
			var isEnable = Ext.create("Ext.Container", {
					width:'97%',
					margin: '0 0 0 10',
					items : [{
						xtype : 'fieldcontainer',
						defaultType : 'checkboxfield',
						items : [{
							boxLabel : createinput_enable,// 启用
							id : 'isenable',
							value:dbconfig.isenable,
							inputValue : '1'
						}]
					}]
				});
			if(import_type=='0'){
				mainform.add(shougongPanel);
			}
			if(import_type=='1'){
				mainform.add(dbform);
			} 
			if(import_type=='2'){
				mainform.add(ftpform);
			}
			if(import_type=='3'){
				mainform.add(wbsPanel);
			}
			win.add(mainform);
 		    win.add(importType);//增量还是全量
			win.add(isEnable);//启用还是不启用
			win.show();
	}
});
