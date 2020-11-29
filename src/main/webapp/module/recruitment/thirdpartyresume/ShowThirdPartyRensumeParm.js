/**
 * 项目信息管理主界面调用js
 * 
 * @author chenxg
 */
Ext.define('ShowThirdPartyRensumeParmURL.ShowThirdPartyRensumeParm', {
	requires:[
	    'EHR.extWidget.field.CodeSelectField',
	    'EHR.extWidget.proxy.TransactionProxy',
		'EHR.tableFactory.TableController',
		'SYSF.FileUpLoad'
	],
	constructor : function(config) {
		Ext.util.CSS.createStyleSheet(".x-tab-bar-top>.x-tab-bar-strip-default {height: 0px;}");
		thirdPartyRensume = this;
		var interval = "";
		var tabName = "";
		this.init();
	},
	// 初始化函数
	init : function() {
		var map = new HashMap();
		map.put("ds", "s");
		Rpc( {
			functionId : 'ZP0000002601',
			success : thirdPartyRensume.tabPanelLoad
		}, map);
	},

	tabPanelLoad : function(parm) {
		var value = parm.responseText;
		var paramMap = Ext.decode(value);
		if (paramMap.succeed) {
			var tabs = Ext.decode(paramMap.tabs);
			if (Ext.isEmpty(tabs))
				return;

			var tabPanel = Ext.create('Ext.tab.Panel', {
				title : '简历导入',
				renderTo : Ext.getBody(),
				bodyStyle: 'border-width: 0px 1px 1px 1px;',
				width : "50%",
				height : "100%",
				listeners : {
					beforetabchange : function(tabs, newTab, oldTab) {
						thirdPartyRensume.searchParam(newTab.id);
						tabName = newTab.id;
					}
				},
				tabBar : {
					height : 100,
					width: 150,
					defaults : {
						height : 100,
						width: 150
					}
				},
				items : tabs
			});

			Ext.create('Ext.container.Viewport', {
				id:'importResume',
				layout : 'fit',
				items : [ tabPanel ]
			}).show();
			
			thirdPartyRensume.searchParam(paramMap.tabId);
		}
	},

	searchParam: function (tabId) {
		var map = new HashMap();
		map.put("name", tabId);
		Rpc( {
			functionId : 'ZP0000002602',
			success : thirdPartyRensume.thirdPartyParm
		}, map);
	},
	
	thirdPartyParm : function(param) {
		var value = param.responseText;
		var map = Ext.decode(value);
		   tabName =  map.tabId;
		if("BeiSen" == map.tabId)
			thirdPartyRensume.beiSenPanelParam(map);
		
		if("DaYee" == map.tabId) {
			thirdPartyRensume.dayeePanelParam(map);
		//	thirdPartyRensume.selectFile();
		}
			
	},
	
	beiSenPanelParam: function (map) {
		var beisenPanel = Ext.getCmp("beisenPanel");
		if(beisenPanel) {
			Ext.getCmp("apiAddress").value = map.apiAddress;
			Ext.getCmp("tenantId").value = map.tenantId;
			Ext.getCmp("token").value = map.token;
			Ext.getCmp("statusId").value = map.statusId;
		} else {
			//地址
			var apiAddress = Ext.create('Ext.form.field.Text', {
				id : 'apiAddress',
				name : 'apiAddress',
				width: 500,
				margin : '10 20 0 -40',
				fieldLabel : '地址',
				labelSeparator: '',
				labelAlign : 'right',
				value: map.apiAddress,
				allowBlank : false
			});
	        //租户编号
			var tenantId = Ext.create('Ext.form.field.Text', {
				id : 'tenantId',
				name : 'tenantId',
				width: 500,
				margin : '20 20 0 -40',
				fieldLabel : '租户编号',
				labelSeparator: '',
				labelAlign : 'right',
				value: map.tenantId,
				allowBlank : false
			});
	        //验证客户是否有相应的资源访问权限
			var token = Ext.create('Ext.form.field.Text', {
				id : 'token',
				name : 'token',
				width: 500,
				margin : '20 0 0 -40',
				fieldLabel : 'token',
				labelSeparator: '',
				labelAlign : 'right',
				value: map.token,
				allowBlank : false
			});
			//阶段id和状态ID的编码
			var statusId = Ext.create('Ext.form.field.Text', {
				id : 'statusId',
				name : 'statusId',
				width: 500,
				margin : '20 20 0 -40',
				fieldLabel : '阶段和状态编码',
				labelSeparator: '',
				labelAlign : 'right',
				value: map.statusId,
				allowBlank : false
			});
			//阶段ID或编码			
			var desc = Ext.create('Ext.form.field.Display', {
				id : 'desc',
				name : 'desc',
				width: 450,
				border:false,
				fieldStyle: {
					'margin-left': '75px',
					'line-height': '20px'
		        },
				value: "（格式为：阶段1:状态1,状态2,...,状态n;阶段2:状态1,状态2,...,状态n;）",
			});
	
			var save = Ext.create('Ext.Button', {
				text : '保存',
				style:'margin-top:10px',
				handler : function() {
					var Address = apiAddress.value;
					var tenantid = tenantId.value;
					var tokenValue = token.value;
					var statusValue = statusId.value;
					
					if(Ext.isEmpty(Address)) {
						Ext.Msg.alert(IMPORT_RESUME_MSG, apiAddress.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
					
					if(Ext.isEmpty(tenantid)){
						Ext.Msg.alert(IMPORT_RESUME_MSG, tenantId.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
	
					if(Ext.isEmpty(tokenValue)){
						Ext.Msg.alert(IMPORT_RESUME_MSG, token.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
					
					if(Ext.isEmpty(statusValue)){
						Ext.Msg.alert(IMPORT_RESUME_MSG, statusId.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
					
					var paramMap = new HashMap();
					paramMap.put("name", tabName);
					paramMap.put("apiAddress", Address);//地址
					paramMap.put("tenantId", tenantid);//租户编号
					paramMap.put("token", tokenValue);
					paramMap.put("statusId", statusValue);//状态ID或编码
					Rpc( {
						functionId : 'ZP0000002603',
						success : function (param){
							var value = param.responseText;
							var returnMap = Ext.decode(value);
							if(returnMap.succeed) {
								Ext.showAlert("保存成功！");
							}
						}
					}, paramMap);
				}
			});
	
			var definitionScheme = {};
			if("true" == map.defineShow){
				definitionScheme = Ext.create('Ext.Button', {
					text : '定义方案',
					style:'margin-top:10px',
					handler : thirdPartyRensume.defineRensume
				});
			}
	
			var importResume = {};
			if("true" == map.importShow){
				var importResume = Ext.create('Ext.Button', {
					text : '导入简历',
					style:'margin-top:10px',
					handler : function() {
						var Address = apiAddress.value;
						var tenantid = tenantId.value;
						var tokenValue = token.value;
						var statusValue = statusId.value;
						
						if(Ext.isEmpty(Address)) {
							 Ext.Msg.alert(IMPORT_RESUME_MSG, apiAddress.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
							 return;
						}
						
						if(Ext.isEmpty(tenantid)){
							 Ext.Msg.alert(IMPORT_RESUME_MSG, tenantId.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
							 return;
						}
		
						if(Ext.isEmpty(tokenValue)){
							 Ext.Msg.alert(IMPORT_RESUME_MSG, token.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
							 return;
						}
						
						if(Ext.isEmpty(statusValue)){
							 Ext.Msg.alert(IMPORT_RESUME_MSG, statusId.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
							 return;
						}
						
						var win = Ext.getCmp("importData");
						if (win != null)
							win.close();
						
						win = Ext.create('Ext.window.Window', {
						    title: '导入简历',
						    id: 'importData',
						    height: 150,
						    width: 300,
						    modal: true,
						    style:{'align':'center'},
						    items: [{
						    	 xtype: 'datefield',
						    	 id: 'startDate',
						    	 style:'margin-top:10px',
						         fieldLabel: '开始日期',
						         labelAlign:'right',
						         labelWidth:85,
						         format: 'Y-m-d',
						         name: 'startDate'
						    },{
						    	 xtype: 'datefield',
						    	 id:'endDate',
						    	 style:'margin-top:10px',
						         fieldLabel: '结束日期',
						         labelAlign:'right',
						         labelWidth:85,
						         format: 'Y-m-d',
						         name: 'endDate'
						    }],
						    buttonAlign:'center',
						    buttons: [
						              { xtype: "button", text: "确定", handler: function () {
							            	  var startDate, endDate;	
							            	  var startdate = Ext.getCmp("startDate").value;
							  				  if(!Ext.isEmpty(startdate))
							  					  startDate = Ext.Date.format(startdate,'Y-m-d');
							  				  else {
							  					  Ext.showAlert("开始时间不能为空！");
												  return;
							  				  }
							  				  
							  				  var enddate = Ext.getCmp("endDate").value;
							  				  if(!Ext.isEmpty(enddate))
							  					  endDate = Ext.Date.format(enddate,'Y-m-d');
							  				  else {
							  					  Ext.showAlert("结束时间不能为空！");
												  return;
							  				  }
							  				  
							  				  Ext.MessageBox.wait("", "正在导入……");	
							  				  thirdPartyRensume.interval = setInterval(function () {
							  					  var amap = new HashMap();
									      		  Rpc( {
									      			  functionId : 'ZP0000002612',
									      			  success : thirdPartyRensume.showImportInfor
									      		  }, amap);
							  				  }, 10000);
							  				  
							  				  var map = new HashMap();
							  				  map.put("tabName", tabName)
								      		  map.put("startDate", startDate);
								      		  map.put("endDate", endDate);
								      		  Rpc( {
								      			  functionId : 'ZP0000002611',
								      			  success : function (param){
									      				var value = param.responseText;
														var map = Ext.decode(value);
														var msg = getDecodeStr(map.msg);
														if("bsUrl" == msg) {
															Ext.Msg.alert(IMPORT_RESUME_MSG, apiAddress.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
											                return false;
											            } else if("tenantId" == msg) {
															Ext.Msg.alert(IMPORT_RESUME_MSG, tenantId.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
											                return false;
											            } else if("token" == msg) {
															Ext.Msg.alert(IMPORT_RESUME_MSG, token.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
											                return false;
											            }
								      			  }
								      		  }, map); 
								      		  
								      		win.close();
						              	  }
						              },
						              { xtype: "button", text: "取消", handler: function () { win.close();} }
						          ]
						}).show();
					}
				});
			}
			var toolbar = [];
			toolbar.push(save);
			if("true" == map.defineShow)
				toolbar.push(definitionScheme);
			
			if("true" == map.importShow)
				toolbar.push(importResume);
			
			beisenPanel = Ext.create('Ext.panel.Panel', {
				width : 600,
				id: 'beisenPanel',
				margin : '30% 30% 0 0',
				height: 320,
				title : '参数设置',
				border : false,
				shadow: false,
				layout: {
			        align: 'middle',
			        pack: 'center',
			        type: 'vbox'
				},
				bbar : toolbar,
				items : [ apiAddress, tenantId, token, statusId, desc ]
			});
	
			var beisen = Ext.getCmp("BeiSen");
			beisen.add(beisenPanel);
		}
	},
	defineRensume:function(){
		var map = new HashMap();
		map.put("name",tabName);
		Ext.require('ShowThirdPartyRensumeParmURL.DefineResumeImportScheme', function(){
			Ext.create("ShowThirdPartyRensumeParmURL.DefineResumeImportScheme", map);
		});
	},
	showImportInfor: function (param) {
		var value = param.responseText;
		var map = Ext.decode(value);
		var showInfor = getDecodeStr(map.showInfor);
		if (!showInfor)
			return;
		
		Ext.MessageBox.close();
		var win = Ext.getCmp("importData");
		if (win != null)
			win.close();
		
		if(Ext.isEmpty(showInfor))
			return;
		
		var inforWin = Ext.create('Ext.window.Window', {
		    title: IMPORT_RESUME_MSG,
		    id: 'showImportInfor',
		    height: 400,
		    width: 600,
		    modal: true,
		    buttonAlign:'center',
		    html:showInfor
		});
		inforWin.show();
		
		var deleteMap = new HashMap();
		deleteMap.put("deleteInfor", "1");
		Rpc( {
			  functionId : 'ZP0000002612',
			  success : function (a){}
		  }, deleteMap); 
		clearInterval(thirdPartyRensume.interval);
	},
	
	dayeePanelParam: function (map) {
		var dayeePanel = Ext.getCmp("dayeepanel");
		if(dayeePanel) {
			Ext.getCmp("apiurl").value = map.apiUrl;
			Ext.getCmp("corpCode").value = map.corpCode;
			Ext.getCmp("username").value = map.userName;
			Ext.getCmp("password").value = map.passWord;
		} else {
			var apiUrl = Ext.create('Ext.form.field.Text', {
				id : 'apiurl',
				name : 'apiurl',
				width: 500,
				margin : '-10 20 0 -40',
				fieldLabel : '地址',
				labelSeparator: '',
				labelAlign : 'right',
				value: map.apiUrl,
				allowBlank : false
			});
			
			var corpCode = Ext.create('Ext.form.field.Text', {
				id : 'corpCode',
				name : 'corpCode',
				width: 500,
				margin : '20 20 0 -40',
				fieldLabel : 'corpCode',
				labelSeparator: '',
				labelAlign : 'right',
				value: map.corpCode,
				allowBlank : false
			});
	
			var userName = Ext.create('Ext.form.field.Text', {
				id : 'username',
				name : 'username',
				width: 500,
				margin : '20 20 0 -40',
				fieldLabel : '用户名',
				labelSeparator: '',
				labelAlign : 'right',
				value: map.userName,
				allowBlank : false
			});
	
			var passWord = Ext.create('Ext.form.field.Text', {
				id : 'password',
				name : 'password',
				width: 500,
				margin : '20 0 0 -40',
				fieldLabel : '密码',
				labelSeparator: '',
				labelAlign : 'right',
				value: map.passWord,
				allowBlank : false
			});
	
			var save = Ext.create('Ext.Button', {
				text : '保存',
				handler : function() {
					var apiurl = apiUrl.value;
					var corpcode = corpCode.value;
					var username = userName.value;
					var password = passWord.value;
					if(Ext.isEmpty(apiurl)) {
						Ext.Msg.alert(IMPORT_RESUME_MSG, apiUrl.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
					
					if(Ext.isEmpty(corpcode)) {
						Ext.Msg.alert(IMPORT_RESUME_MSG, corpCode.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
					
					if(Ext.isEmpty(username)){
						Ext.Msg.alert(IMPORT_RESUME_MSG, userName.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
	
					if(Ext.isEmpty(password)){
						Ext.Msg.alert(IMPORT_RESUME_MSG, passWord.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
					
					var paramMap = new HashMap();
					paramMap.put("name", map.tabId);
					paramMap.put("apiurl", apiurl);
					paramMap.put("corpcode", corpcode);
					paramMap.put("userName", username);
					paramMap.put("passWord", password);
					Rpc( {
						functionId : 'ZP0000002603',
						success : function (param){
							var value = param.responseText;
							var returnMap = Ext.decode(value);
							if(returnMap.succeed) {
								Ext.showAlert("保存成功！");
							}
						}
					}, paramMap);
				}
			});
	
			var definitionScheme = Ext.create('Ext.Button', {
				text : '定义方案',
				handler : thirdPartyRensume.defineRensume
				
			});
	
			var importResume = Ext.create('Ext.Button', {
				id:'importResumeId',
				text : '导入简历',		
				handler : function() {
					var apiurl = apiUrl.value;
					var corpcode = corpCode.value;
					var username = userName.value;
					var password = passWord.value;
					if(Ext.isEmpty(apiurl)) {
						Ext.Msg.alert(IMPORT_RESUME_MSG, apiUrl.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
					
					if(Ext.isEmpty(corpcode)) {
						Ext.Msg.alert(IMPORT_RESUME_MSG, corpCode.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
					
					if(Ext.isEmpty(username)){
						Ext.Msg.alert(IMPORT_RESUME_MSG, userName.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
	
					if(Ext.isEmpty(password)){
						Ext.Msg.alert(IMPORT_RESUME_MSG, passWord.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
						return;
					}
					
					Ext.MessageBox.wait("", "正在导入……");	
					setInterval(function () {
						var amap = new HashMap();
						Rpc( {
							functionId : 'ZP0000002612',
							success : thirdPartyRensume.showImportInfor
						}, amap);
					}, 10000);
					
					var map = new HashMap();
					map.put("tabName", tabName)
					Rpc( {
						functionId : 'ZP0000002611',
						success : function (param){
							var value = param.responseText;
							var map = Ext.decode(value);
							var msg = getDecodeStr(map.msg);
							if("apiurl" == msg) {
								Ext.Msg.alert(IMPORT_RESUME_MSG, apiUrl.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
				                return false;
				            } else if("corpcode" == msg) {
								Ext.Msg.alert(IMPORT_RESUME_MSG, corpCode.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
				                return false;
				            } else if("userName" == msg) {
								Ext.Msg.alert(IMPORT_RESUME_MSG, userName.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
				                return false;
				            } else if("passWord" == msg) {
								Ext.Msg.alert(IMPORT_RESUME_MSG, passWord.fieldLabel + IMPORT_RESUME_PARAM_NOTNULL);
				                return false;
				            }
						}
					}, map); 
				}
			});
	
			dayeePanel = Ext.create('Ext.panel.Panel', {
				width : 600,
				id:'dayeepanel',
				margin : '30% 30% 0 0',
				height: 300,
				title : '参数设置',
				border : false,
				shadow: false,
				layout: {
			        align: 'middle',
			        pack: 'center',
			        type: 'vbox'
				},
				bbar : [ save, definitionScheme, importResume ],
				items : [ apiUrl, corpCode, userName, passWord ]
			});
	
			var DaYee = Ext.getCmp("DaYee");
			DaYee.add(dayeePanel);
		}
	},
	
	uploadFile: function (list) {
		if(list.length < 0)
			return;
		
		Ext.MessageBox.wait("", "正在导入数据");	
		setInterval(function () {
			  var amap = new HashMap();
    		  Rpc( {
    			  functionId : 'ZP0000002612',
    			  success : thirdPartyRensume.showImportInfor
    		  }, amap);
		  }, 10000);
		
		var obj = list[0];
		if(obj){
			var map = new HashMap();
			map.put("path", obj.path);
			map.put("filename", obj.filename);
			map.put("tabName", tabName);
			Rpc( {
				functionId : 'ZP0000002611',
				success : function(a){}
			}, map);
		}
	},
	selectFile: function () {
		var fileupload = Ext.getCmp("fileupload");
		if(fileupload)
			return;
		
		Ext.require('SYSF.FileUpLoad', function(){
			var uploadObj = Ext.create("SYSF.FileUpLoad",{
				id:'fileupload',
				upLoadType:3,
				fileSizeLimit:'500MB',
				fileExt:"*.zip;",
				buttonText:'',
				renderTo:"importResumeId",
				success:thirdPartyRensume.uploadFile,
				isDelete:true,
				width:55,
				height:20
			});
		});
		
		Ext.getDom("importResumeId").childNodes[1].style.marginTop = "-20px";
	}

});