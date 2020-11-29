Ext.define("FilePathSetting", {
	extend: 'Ext.panel.Panel',
	config: {
		border:0,
		scrollable: true,
		layout: {
			type: 'vbox',
			align: 'stretch'
		}
	},
	//初始化组件
	initComponent: function () {
		this.callParent();
		this.initMain();
		this.loadParams();
		this.loadProgress();
	},
	//加载参数
	loadParams:function(){
		this.params = {
			TYPE: "0",
			PATH: "",
			URL: "",
			SALTKEY: "",
			PORT:"",
			FTPURL: "",
			USER: "",
			PWD: "",
			CLASS: "",
			MULTIMEDIA: "",
			DOC: "",
			VIDEOSTREAMS: "",
			ASYN: "",
			OTHER: ""
		}

		var me = this;
		var vo = new HashMap();
		vo.put("transType", "loadSyncConfig");
		Rpc({
			functionId: 'SYS00007001', async: false, success: function (res) {
				var resultObj = Ext.decode(res.responseText);
				if(resultObj.returnStr && resultObj.returnStr.return_data && resultObj.returnStr.return_data.FILESETTING){
					var rpcTotalParams = resultObj.returnStr.return_data.FILESETTING;
					if(rpcTotalParams.FILESIZECONTROL){
						if(rpcTotalParams.FILESIZECONTROL.MULTIMEDIA){
							me.params.MULTIMEDIA =  parseInt(rpcTotalParams.FILESIZECONTROL.MULTIMEDIA)+"";
						}
						if(rpcTotalParams.FILESIZECONTROL.DOC){
							me.params.DOC =  parseInt(rpcTotalParams.FILESIZECONTROL.DOC)+"";
						}
						if(rpcTotalParams.FILESIZECONTROL.VIDEOSTREAMS){
							me.params.VIDEOSTREAMS =  parseInt(rpcTotalParams.FILESIZECONTROL.VIDEOSTREAMS)+"";
						}
						if(rpcTotalParams.FILESIZECONTROL.ASYN){
							me.params.ASYN = parseInt(rpcTotalParams.FILESIZECONTROL.ASYN)+"";
						}
						if(rpcTotalParams.FILESIZECONTROL.OTHER){
							me.params.OTHER =  parseInt(rpcTotalParams.FILESIZECONTROL.OTHER)+"";
						}
					}
					if(rpcTotalParams.FILEPATH){
						if(rpcTotalParams.FILEPATH.TYPE){
							me.params.TYPE =  rpcTotalParams.FILEPATH.TYPE;
						}
						if(rpcTotalParams.FILEPATH.PARAMETER){
							if(rpcTotalParams.FILEPATH.PARAMETER.PATH){
								me.params.PATH =  rpcTotalParams.FILEPATH.PARAMETER.PATH;
							}
							if(rpcTotalParams.FILEPATH.PARAMETER.URL){
								if(me.params.TYPE == "1"){
									me.params.URL =  rpcTotalParams.FILEPATH.PARAMETER.URL;
								}else if(me.params.TYPE == "2"){
									me.params.PORT =  rpcTotalParams.FILEPATH.PARAMETER.PORT;
									me.params.FTPURL =  rpcTotalParams.FILEPATH.PARAMETER.URL;
								}
							}
							if(rpcTotalParams.FILEPATH.PARAMETER.SALTKEY){
								me.params.SALTKEY =  rpcTotalParams.FILEPATH.PARAMETER.SALTKEY;
							}
							if(rpcTotalParams.FILEPATH.PARAMETER.USER){
								me.params.USER =  rpcTotalParams.FILEPATH.PARAMETER.USER;
							}
							if(rpcTotalParams.FILEPATH.PARAMETER.PWD){
								me.params.PWD =  rpcTotalParams.FILEPATH.PARAMETER.PWD;
							}
							if(rpcTotalParams.FILEPATH.PARAMETER.CLASS){
								me.params.CLASS =  rpcTotalParams.FILEPATH.PARAMETER.CLASS;
							}
						}
					}
				}
				me.fillParams();
			}
		}, vo);
//         me.fillParams();
	},
	//填充参数
	fillParams:function(){
		Ext.getCmp("savetype").setValue(this.params.TYPE);
		Ext.getCmp("path").setValue(this.params.PATH);
		Ext.getCmp("url").setValue(this.params.URL);
		Ext.getCmp("saltkey").setValue(this.params.SALTKEY);
		Ext.getCmp("ftpport").setValue(this.params.PORT);
		Ext.getCmp("ftpurl").setValue(this.params.FTPURL);
		Ext.getCmp("user").setValue(this.params.USER);
		Ext.getCmp("pwd").setValue(this.params.PWD);
		Ext.getCmp("class").setValue(this.params.CLASS);
		Ext.getCmp("media").setValue(this.params.MULTIMEDIA);
		Ext.getCmp("file").setValue(this.params.DOC);
		Ext.getCmp("courseware").setValue(this.params.VIDEOSTREAMS);
//		Ext.getCmp("integration").setValue(this.params.ASYN);
		Ext.getCmp("other").setValue(this.params.OTHER);
	},
	//初始化页面
	initMain: function(){
		var me = this;
		var store = Ext.create('Ext.data.Store', {
			fields: ['abbr', 'name'],
			data: [
				{"abbr": "0", "name": FilePathSetting.localdisk},
//				{"abbr": "1", "name": FilePathSetting.fileservice},
				{"abbr": "2", "name": 'FTP'+FilePathSetting.service},
				{"abbr": "3", "name": FilePathSetting.other}
			]
		});
		this.add({
			xtype:'container',
			margin:'30 50 0 50',
//    		height:150,
//    		flex:0.4,
			items:[{
				xtype:'container',
				style:'border-left:5px solid #5190ce',
				padding:'0 0 0 10',
				html:'<b>'+FilePathSetting.filesave+'</b>'
			},{
				xtype: 'combo',
				id: "savetype",
				margin: '5 0 0 0',
				fieldLabel:FilePathSetting.savetype,
				labelWidth: 100,
				width: 400,
				margin: '20 0 20 20',
				displayField: 'name',
				valueField: 'abbr',
				store: store,
				editable: false,
				queryMode: 'local',
//                value:this.params.TYPE,
				listeners:{
					change:function( comp, newValue, oldValue, eOpts ){
						me.changeSaveType(newValue);
					}
				}
			},{
				//存储路径
				xtype: 'textfield',
				fieldLabel: FilePathSetting.saveurl,
				labelWidth: 100,
				width: 400,
				margin: '20 0 20 20',
				hidden:true,
				id: 'path'
//                value:this.params.PATH
			},{
				//服务器地址
				xtype: 'textfield',
				fieldLabel: FilePathSetting.webporturl,
				labelWidth: 100,
				width: 400,
				margin: '20 0 20 20',
				hidden:true,
				id: 'url'
//                value:this.params.URL
			},{
				//密钥
				xtype: 'textfield',
				fieldLabel: FilePathSetting.secret,
				labelWidth: 100,
				width: 400,
				margin: '20 0 20 20',
				hidden:true,
				id: 'saltkey'
//            	value:this.params.SALTKEY
			},{
				//服务器地址
				xtype: 'textfield',
				fieldLabel: FilePathSetting.webporturl,
				labelWidth: 100,
				width: 400,
				margin: '20 0 20 20',
				hidden:true,
				id: 'ftpurl'
//                value:this.params.URL
			},{
				//端口号
				xtype: 'textfield',
				fieldLabel: FilePathSetting.ftpport,
				labelWidth: 100,
				width: 400,
				margin: '20 0 20 20',
				hidden:true,
				id: 'ftpport'
//                value:this.params.URL
			},{
				//用户名
				xtype: 'textfield',
				fieldLabel: FilePathSetting.username,
				labelWidth: 100,
				width: 400,
				margin: '20 0 20 20',
				hidden:true,
				id: 'user'
//                value:this.params.USER
			},{
				//密码
				xtype: 'textfield',
				fieldLabel: FilePathSetting.pwd,
				inputType:'password',
				labelWidth: 100,
				width: 400,
				margin: '20 0 20 20',
				hidden:true,
				id: 'pwd'
//                value:this.params.PWD
			},{
				//类路径
				xtype: 'textfield',
				fieldLabel: FilePathSetting.classurl,
				labelWidth: 100,
				width: 400,
				margin: '20 0 20 20',
				hidden:true,
				id: 'class'
//                value:this.params.CLASS
			}
//			,{
//				//按钮栏
//				xtype:'container',
//				margin: '20 0 20 20',
//				items:[{
//					//保存
//					xtype:'button',
//					id:'file_save_btn',
//					width: 100,
//					style: 'border-radius: 2px;',
//					layout: {
//						align: 'center'
//					},
//					text:FilePathSetting.save,
//					listeners:{
//						click:function( btn, e, eOpts ){
//							me.saveUrl();
//						}
//					}
//				}
//				,{
//					//测试连接
//					xtype:'button',
//					id:'file_testconnect_btn',
//					margin:'0 0 0 50',
//					width: 100,
//					style: 'border-radius: 2px;',
//					layout: {
//						align: 'center'
//					},
//					text:FilePathSetting.testconnect,
//					listeners:{
//						click:function( btn, e, eOpts ){
//							me.testUrl();
//						}
//					}
//				}
//				]
//			}
		]
		});
		this.add({
			xtype:'container',
//			flex:0.5,
			margin:'30 50 0 50',
			items:[{
				xtype:'container',
				style:'border-left:5px solid #5190ce',
				padding:'0 0 0 10',
				html:'<b>'+FilePathSetting.fileaxsize+'</b>'
			},{
				xtype:'container',
				layout:'hbox',
				margin: '20 0 20 20',
				items:[{
					//多媒体
					xtype: 'numberfield',
					fieldLabel: FilePathSetting.media,
					labelWidth: 100,
					width: 200,
					id: 'media',
					minValue: 1,
					decimalPrecision:0,
					hideTrigger: true,
			        keyNavEnabled: false,
			        mouseWheelEnabled: false
//                value:this.params.MULTIMEDIA
				},{
					xtype:'container',
					margin: '5 0 5 10',
					html:'MB',
				}]
			},{
				xtype:'container',
				layout:'hbox',
				margin: '20 0 20 20',
				items:[{
					//文档
					xtype: 'numberfield',
					fieldLabel: FilePathSetting.file,
					cls:'under_line_input_cls',
					labelWidth: 100,
					width: 200,
					id: 'file',
					minValue: 1,
					decimalPrecision:0,
					hideTrigger: true,
			        keyNavEnabled: false,
			        mouseWheelEnabled: false
	//                value:this.params.DOC
				},{
					xtype:'container',
					margin: '5 0 5 10',
					html:'MB',
				}]
			},{
				xtype:'container',
				layout:'hbox',
				margin: '20 0 20 20',
				items:[{
					//培训课件
					xtype: 'numberfield',
					fieldLabel: FilePathSetting.courseware,
					baseCls:'under_line_input_cls',
					labelWidth: 100,
					width: 200,
					id: 'courseware',
					minValue: 1,
					decimalPrecision:0,
					hideTrigger: true,
			        keyNavEnabled: false,
			        mouseWheelEnabled: false
	//                value:this.params.VIDEOSTREAMS
				},{
					xtype:'container',
					margin: '5 0 5 10',
					html:'MB',
				}]
			},
			/*{
				xtype:'container',
				layout:'hbox',
				margin: '20 0 20 20',
				items:[{
					//分布集成
					xtype: 'numberfield',
					fieldLabel: FilePathSetting.integration,
					baseCls:'under_line_input_cls',
					labelWidth: 100,
					width: 200,
					id: 'integration',
					minValue: 1,
					decimalPrecision:0,
					hideTrigger: true,
			        keyNavEnabled: false,
			        mouseWheelEnabled: false
	//                value:this.params.ASYN
				},{
					xtype:'container',
					margin: '5 0 5 10',
					html:'MB',
				}]
			},*/
			{
				xtype:'container',
				layout:'hbox',
				margin: '20 0 20 20',
				items:[{
					//其他
					xtype: 'numberfield',
					fieldLabel: FilePathSetting.other,
					baseCls:'under_line_input_cls',
					labelWidth: 100,
					width: 200,
					id: 'other',
					minValue: 1,
					decimalPrecision:0,
					hideTrigger: true,
			        keyNavEnabled: false,
			        mouseWheelEnabled: false
	//                value:this.params.OTHER
				},{
					xtype:'container',
					margin: '5 0 5 10',
					html:'MB',
				}]
			},{
				xtype:'container',
				margin: '20 0 20 0',
				items:[{
					xtype:'button',
					id:'limit_save_btn',
					width: 100,
					margin: '0 20',
					style: 'border-radius: 2px;',
					layout: {
						align: 'center'
					},
					text:FilePathSetting.save,
					listeners:{
						click:function( btn, e, eOpts ){
							me.saveParam();
						}
					}
				},{
					xtype:'button',
					id:'112',
					width: 100,
					margin: '0 20',
					style: 'border-radius: 2px;',
					layout: {
						align: 'center'
					},
					text:'文件迁移',
					listeners:{
						click:function( btn, e, eOpts ){
							Ext.MessageBox.confirm("提示信息","确定进行文件迁移？",function( button,text ){
									if( button == 'yes'){
										var msgbox=Ext.Msg.show({
											title:"VFS文件迁移",
											msg:"提示内容",
											closable:false,
											width:300,
											modal:true,
											progress:true
										});
										var flag = false;
										var percent = 0;
										var des = '';
										var current = '';
										var task = {
											run:function () {
												var vo = new HashMap();
												vo.put("type","queryMove");
												Rpc({
													functionId: 'SYS00007003', async: false, success: function (res) {
														var resultObj = Ext.decode(res.responseText);
														percent = resultObj.returnStr.return_data.percent;
														des = resultObj.returnStr.return_data.des;
														current = resultObj.returnStr.return_data.current;
														if(percent==1){
															Ext.TaskManager.stop(task);
															msgbox.hide();
															Ext.showAlert("迁移完成！");
															if(resultObj.returnStr.return_data.error){
																//迁移完成后，自动下载迁移失败文件Excel
																var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid=pqGATiaNmE3gNtng8w1w43QoIyIKgRobehU0xeB3TwUc");
															}
														}else{
															if(flag){
																current += '...';
															}
															msgbox.updateProgress(percent,des,current);
															flag = !flag;
														}
													}
												}, vo);
											},
											interval:1000
										};
										//开始文件迁移
										me.fileMove();
										//定时执行查询进度的任务
										Ext.TaskManager.start(task);
									}
								}
							);

						}
					}
				},{
					xtype:'button',
					id:'22',
					width: 100,
					margin: '0 20',
					style: 'border-radius: 2px;',
					layout: {
						align: 'center'
					},
					text:'一键还原',
					listeners:{
						click:function( btn, e, eOpts ){
							Ext.MessageBox.confirm("提示信息","确定进行一键还原？",function( button,text ){
									if( button == 'yes'){
										var msgbox=Ext.Msg.show({
											title:"VFS一键还原",
											msg:"提示内容",
											closable:false,
											width:300,
											modal:true,
											progress:true
										});
										var flag = false;
										var percent = 0;
										var des = '';
										var current = '';
										var task = {
											run:function () {
												var vo = new HashMap();
												vo.put("type","queryRecovery");
												Rpc({
													functionId: 'SYS00007003', async: false, success: function (res) {
														var resultObj = Ext.decode(res.responseText);
														percent = resultObj.returnStr.return_data.percent;
														des = resultObj.returnStr.return_data.des;
														current = resultObj.returnStr.return_data.current;
														if(percent==1){
															Ext.TaskManager.stop(task);
															msgbox.hide();
															Ext.showAlert("还原完成！");
															if(resultObj.returnStr.return_data.error){
																//还原完成后，自动下载还原失败文件Excel
																var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid=3RasMFAWdYfjtsDbX5rKmwoIyIKgRobehU0xeB3TwUc");
															}
														}else{
															if(flag){
																current += '...';
															}
															msgbox.updateProgress(percent,des,current);
															flag = !flag;
														}
													}
												}, vo);
											},
											interval:1000
										};
										//开始一键还原
										me.oneKeyRecovery();
										//定时执行查询进度的任务
										Ext.TaskManager.start(task);
									}
								}
							);
						}
					}
				}]
			}]
		});
	},
	//改变存储方式
	changeSaveType:function(type){
		if(type == "0"){
			Ext.getCmp("path").setHidden(false);
			Ext.getCmp("url").setHidden(true);
			Ext.getCmp("saltkey").setHidden(true);
			Ext.getCmp("ftpport").setHidden(true);
			Ext.getCmp("ftpurl").setHidden(true);
			Ext.getCmp("user").setHidden(true);
			Ext.getCmp("pwd").setHidden(true);
			Ext.getCmp("class").setHidden(true);
		}else if(type == "1"){
			Ext.getCmp("path").setHidden(true);
			Ext.getCmp("url").setHidden(false);
			Ext.getCmp("saltkey").setHidden(false);
			Ext.getCmp("ftpport").setHidden(true);
			Ext.getCmp("ftpurl").setHidden(true);
			Ext.getCmp("user").setHidden(true);
			Ext.getCmp("pwd").setHidden(true);
			Ext.getCmp("class").setHidden(true);
		}else if(type == "2"){
			Ext.getCmp("path").setHidden(true);
			Ext.getCmp("url").setHidden(true);
			Ext.getCmp("saltkey").setHidden(true);
			Ext.getCmp("ftpport").setHidden(false);
			Ext.getCmp("ftpurl").setHidden(false);
			Ext.getCmp("user").setHidden(false);
			Ext.getCmp("pwd").setHidden(false);
			Ext.getCmp("class").setHidden(true);
		}else if(type == "3"){
			Ext.getCmp("path").setHidden(true);
			Ext.getCmp("url").setHidden(true);
			Ext.getCmp("saltkey").setHidden(true);
			Ext.getCmp("ftpport").setHidden(true);
			Ext.getCmp("ftpurl").setHidden(true);
			Ext.getCmp("user").setHidden(true);
			Ext.getCmp("pwd").setHidden(true);
			Ext.getCmp("class").setHidden(false);
		}
	},
	//保存文件路径
//	saveUrl:function(){
//		var vo = new HashMap();
//		var params = new HashMap();
//		var filepath = new HashMap();
//		var type = Ext.getCmp('savetype').getValue();
//		filepath.put("TYPE",type);
//		var parameter = new HashMap();
//		if(type == "0"){
//			parameter.put("PATH",Ext.getCmp("path").getValue());
//		}else if(type == "1"){
//			parameter.put("URL",Ext.getCmp("url").getValue());
//			parameter.put("SALTKEY",Ext.getCmp("saltkey").getValue());
//		}else if(type == "2"){
//			parameter.put("URL",Ext.getCmp("ftpurl").getValue());
//			parameter.put("PORT",Ext.getCmp("ftpport").getValue());
//			parameter.put("USER",Ext.getCmp("user").getValue());
//			parameter.put("PWD",Ext.getCmp("pwd").getValue());
//		}else if(type == "3"){
//			parameter.put("CLASS",Ext.getCmp("class").getValue());
//		}
//		filepath.put("PARAMETER",parameter);
//		params.put("FILEPATH",filepath);
//		vo.put("type","FILEPATH");
//		vo.put("params",JSON.stringify(params));
//
//
//		Rpc({
//			functionId: 'SYS00007002', async: false, success: function (res) {
//				var resultObj = Ext.decode(res.responseText);
//				var success = resultObj.returnStr.return_code;
//				if(success = "success"){
//					Ext.MessageBox.alert(FilePathSetting.informessage, FilePathSetting.save + FilePathSetting.success);
//				}else{
//					Ext.MessageBox.alert(FilePathSetting.informessage, FilePathSetting.save + FilePathSetting.fail);
//				}
//			}
//		}, vo);
//	},
	//保存存储路径及附件限制
	saveParam:function(){
		var vo = new HashMap();
		var params = new HashMap();
		//路径
		var filepath = new HashMap();
		var type = Ext.getCmp('savetype').getValue();
		filepath.put("TYPE",type);
		var parameter = new HashMap();
		//保存前，对必填项进行校验
		if(type == "0"){
			if(Ext.getCmp("path").getValue()==''){
				Ext.showAlert(FilePathSetting.pathblank);
				return;
			}
			parameter.put("PATH",Ext.getCmp("path").getValue());
		}else if(type == "1"){
			if(Ext.getCmp("url").getValue()==''){
				Ext.showAlert(FilePathSetting.urlblank);
				return;
			}
			if(Ext.getCmp("saltkey").getValue()==''){
				Ext.showAlert(FilePathSetting.keyblank);
				return;
			}
			parameter.put("URL",Ext.getCmp("url").getValue());
			parameter.put("SALTKEY",Ext.getCmp("saltkey").getValue());
		}else if(type == "2"){
			if(Ext.getCmp("ftpurl").getValue()==''){
				Ext.showAlert(FilePathSetting.urlblank);
				return;
			}
			if(Ext.getCmp("ftpport").getValue()==''){
				Ext.showAlert(FilePathSetting.portblank);
				return;
			}
			if(Ext.getCmp("user").getValue()==''){
				Ext.showAlert(FilePathSetting.userblank);
				return;
			}
			if(Ext.getCmp("pwd").getValue()==''){
				Ext.showAlert(FilePathSetting.passwordblank);
				return;
			}
			parameter.put("URL",Ext.getCmp("ftpurl").getValue());
			parameter.put("PORT",Ext.getCmp("ftpport").getValue());
			parameter.put("USER",Ext.getCmp("user").getValue());
			parameter.put("PWD",Ext.getCmp("pwd").getValue());
		}else if(type == "3"){
			if(Ext.getCmp("class").getValue()==''){
				Ext.showAlert(FilePathSetting.classblank);
				return;
			}
			parameter.put("CLASS",Ext.getCmp("class").getValue());
		}
		filepath.put("PARAMETER",parameter);
		params.put("FILEPATH",filepath);

		//限制
		var filesizecontrol = new HashMap();
		//zhangh 默认以MB为单位，不需要额外加单位，否则后台转int判断大小时，还得统一去单位
		filesizecontrol.put("MULTIMEDIA",Ext.getCmp("media").getValue());
		filesizecontrol.put("DOC",Ext.getCmp("file").getValue());
		filesizecontrol.put("VIDEOSTREAMS",Ext.getCmp("courseware").getValue());
//		filesizecontrol.put("ASYN",Ext.getCmp("integration").getValue());
		filesizecontrol.put("OTHER",Ext.getCmp("other").getValue());
		params.put("FILESIZECONTROL",filesizecontrol);
//		vo.put("type","FILESIZECONTROL");
		vo.put("params",JSON.stringify(params));
		if(Ext.getCmp("media").getValue()==null){
			Ext.showAlert(FilePathSetting.mediablank);
			return;
		}
		if(Ext.getCmp("file").getValue()==null){
			Ext.showAlert(FilePathSetting.fileblank);
			return;
		}
		if(Ext.getCmp("courseware").getValue()==null){
			Ext.showAlert(FilePathSetting.coursewareblank);
			return;
		}
		if(Ext.getCmp("other").getValue()==null){
			Ext.showAlert(FilePathSetting.otherblank);
			return;
		}

		Rpc({
			functionId: 'SYS00007002', async: false, success: function (res) {
				var resultObj = Ext.decode(res.responseText);
				var success = resultObj.returnStr.return_code;
				if(success == "success"){
					Ext.MessageBox.alert(FilePathSetting.informessage, FilePathSetting.save + FilePathSetting.success);
				}else{
					var msg = FilePathSetting.save + FilePathSetting.fail;
					if(resultObj.returnStr.return_msg && resultObj.returnStr.return_msg != ''){
						msg = resultObj.returnStr.return_msg;
					}
					Ext.MessageBox.alert(FilePathSetting.informessage, msg);
				}
			}
		}, vo);
	},
	//初始化时加载进度信息
	loadProgress:function(){
		this.loadMoveProgress();
		this.loadRecoveryProgress();
	},
	loadMoveProgress:function(){
		var msgbox = undefined;
		var flag = false;
		var percent = 0;
		var percentTemp = 0;
		var des = '';
		var current = '';
		var vo = new HashMap();
		vo.put("type","queryMove");
		Rpc({
			functionId: 'SYS00007003', async: false, success: function (res) {
				var resultObj = Ext.decode(res.responseText);
				percent = resultObj.returnStr.return_data.percent;
				des = resultObj.returnStr.return_data.des;
				current = resultObj.returnStr.return_data.current;
				//0代表未开始，1代表已完成
				if(percent!=1 &&percent!=0){
					msgbox = Ext.Msg.show({
						title:"VFS文件迁移",
						msg:"提示内容",
						closable:false,
						width:300,
						modal:true,
						progress:true
					});
					var task = {
						run:function () {
							Rpc({
								functionId: 'SYS00007003', async: false, success: function (res) {
									var resultObj = Ext.decode(res.responseText);
									percent = resultObj.returnStr.return_data.percent;
									des = resultObj.returnStr.return_data.des;
									current = resultObj.returnStr.return_data.current;
									if(percent==1 && percentTemp !=0){
										Ext.TaskManager.stop(task);
										msgbox.hide();
										Ext.showAlert("迁移完成！");
										if(resultObj.returnStr.return_data.error){
											//迁移完成后，自动下载迁移失败文件Excel
											var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid=pqGATiaNmE3gNtng8w1w43QoIyIKgRobehU0xeB3TwUc");
										}
									}else{
										if(percentTemp ==0){
											percentTemp = percent;
										}
										if(flag){
											current += '...';
										}
										msgbox.updateProgress(percent,des,current);
										flag = !flag;
									}
								}
							}, vo);
						},
						interval:1000
					};
					//定时执行查询进度的任务
					Ext.TaskManager.start(task);
				}
			}
		}, vo);

	},
	loadRecoveryProgress:function(){
		var msgbox = undefined;
		var flag = false;
		var percent = 0;
		var percentTemp = 0;
		var des = '';
		var current = '';
		var vo = new HashMap();
		vo.put("type","queryRecovery");
		Rpc({
			functionId: 'SYS00007003', async: false, success: function (res) {
				var resultObj = Ext.decode(res.responseText);
				percent = resultObj.returnStr.return_data.percent;
				des = resultObj.returnStr.return_data.des;
				current = resultObj.returnStr.return_data.current;
				//0代表未开始，1代表已完成
				if(percent!=1 &&percent!=0){
					msgbox = Ext.Msg.show({
						title:"VFS一键还原",
						msg:"提示内容",
						closable:false,
						width:300,
						modal:true,
						progress:true
					});
					var task = {
						run:function () {
							Rpc({
									functionId: 'SYS00007003', async: false, success: function (res) {
									var resultObj = Ext.decode(res.responseText);
									percent = resultObj.returnStr.return_data.percent;
									des = resultObj.returnStr.return_data.des;
									current = resultObj.returnStr.return_data.current;
									if(percent==1 && percentTemp !=0){
										Ext.TaskManager.stop(task);
										msgbox.hide();
										Ext.showAlert("还原完成！");
										if(resultObj.returnStr.return_data.error){
											//还原完成后，自动下载还原失败文件Excel
											var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid=3RasMFAWdYfjtsDbX5rKmwoIyIKgRobehU0xeB3TwUc");
										}
									}else{
										if(percentTemp ==0){
											percentTemp = percent;
										}
										if(flag){
											current += '...';
										}
										msgbox.updateProgress(percent,des,current);
										flag = !flag;
									}
								}
							}, vo);
						},
						interval:1000
					};
					//定时执行查询进度的任务
					Ext.TaskManager.start(task);
				}
			}
		}, vo);
	},
	//文件迁移
	fileMove:function(){
		var vo = new HashMap();
		vo.put("type","move");
		Rpc({
			functionId: 'SYS00007003', async: true, success: function (res) {
				var resultObj = Ext.decode(res.responseText);
			}
		}, vo);
	},
	//一键还原
	oneKeyRecovery:function(){
		var vo = new HashMap();
		vo.put("type","recovery");
		Rpc({
			functionId: 'SYS00007003', async: true, success: function (res) {
				var resultObj = Ext.decode(res.responseText);
			}
		}, vo);
	}
});