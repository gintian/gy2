/**
 * 档案信息管理主界面调用js
 * 
 * @author chenxg
 */
Ext.Loader.loadScript({
	url : rootPath + '/components/tableFactory/ext_custom.js'
});

Ext.define('CertificateManageURL.CertificateManage', {
	requires:[
  	    'EHR.subsetview.SubSetView'
  	],
	constructor : function(config) {
		certificateManage = this;
		certificateManage.fieldSet = "";
		certificateManage.certName = "";
		certificateManage.recordFieldSet = "";
		certificateManage.personid = "";
		certificateManage.borrowPersonId = "";
		certificateManage.menuJson = "";
		certificateManage.borrowPiv;
		certificateManage.returnPiv;
		certificateManage.subSetViewGloble;
		certificateManage.certEndDateItemId;
		certificateManage.prefix = "certificateManage_0001";
		this.init();
	},
	// 初始化函数
	init : function() {
		 var map = new HashMap();
		 Rpc({functionId:'CF01050001',success:certificateManage.loadTable},map);
	},

	loadTable : function(response) {
		 var value = response.responseText;
		 var map = Ext.decode(value);
		 if(map.succeed){
			 if(!Ext.isEmpty(map.errorMsg)){
				 Ext.showAlert(map.errorMsg);
				 return;
			 }
			 
			 certificateManage.fieldSet = map.fieldSet;
			 certificateManage.certName = map.certName;
			 certificateManage.certCategoryItemId = map.certCategoryItemId;
			 certificateManage.certNOItemId = map.certNOItemId;
			 var pivMap = map.pivMap;
			 certificateManage.menuJson = pivMap.menuJson;
			 certificateManage.borrowPiv = pivMap.borrowPiv;
			 certificateManage.returnPiv = pivMap.returnPiv;

			 certificateManage.subSetViewGloble=Ext.create("EHR.subsetview.SubSetView",{
				 setName:certificateManage.fieldSet,
				 nbase:map.nbases,
				 schemeItemKey:'A',
				 subModuleId:'certificateManage_001',
				 privType:'10',
				 tableTitle:'档案管理',
				 personPickerNbase:map.nbases,
				 pickerIsPrivExpression:false,
				 customFilterCond:map.whereFilter,
				 ctrltype: '3',
				 nmodule:'10',
				 functionPriv:{
					 add: pivMap.addPiv, 
					 update: pivMap.updatePiv,
					 batchUpdate: pivMap.batchUpdatePiv,
					 del: pivMap.delPiv
				 },
				 funcParam:{
					table:map.fieldSet,
					item:map.certOrg
				 },
				 queryItem:"a0101,"+certificateManage.certName,
				 isScheme: pivMap.isScheme,
				 loadComplete:certificateManage.insertbuttons,
				 listeners:{
					 /**
					  * 保存方法
					  */
					 beforesave:function(object, value, oldValue){
						 var flag = false;
						 var map = new HashMap();
						 map.put("oldValue", oldValue);
						 map.put("value", value);
						 map.put("nbase", object.getNbase());
						 map.put("a0100", object.getCurrentObject());
						 map.put("i9999", object.getDataIndex());
						 Rpc({
							functionId:'CF01050014',async:false,success:function(response){
								var map	 = Ext.decode(response.responseText);
								if(map.succeed){
									if(Ext.isEmpty(map.errorMsg))
										flag = true;
									else
										Ext.showAlert(map.errorMsg);  
								}else{
									Ext.showAlert(map.errorMsg); 
								}
						 	}
						 },map);
						 
						 return flag;
					 }
				 }
			 });
			 
			 var viewport=Ext.create("Ext.container.Container",{
					renderTo:Ext.getBody(),
					width:'100%',
					height:'100%',
					items:certificateManage.subSetViewGloble
			 });
		 } else
			 Ext.showAlert(map.message);
	},

	insertbuttons: function() {
		var toolbar = Ext.getCmp(certificateManage.fieldSet + "_toolbar");
		if(!Ext.isEmpty(certificateManage.menuJson)){
			var menu = Ext.create('Ext.Button', {
				text : '功能导航',
				arrowAlign : 'right',
				menu : Ext.decode(certificateManage.menuJson)
			});
			
			toolbar.insert(0, menu);
		}
		
		var index = toolbar.items.items.length - 1;
		if(certificateManage.borrowPiv) {
			var borrowButton = Ext.create('Ext.Button', {
				text: '证书借阅',
				handler: certificateManage.checkCertificateInfo
			});
			
			toolbar.insert(index, borrowButton);
			index++;
		}
		
		if(certificateManage.returnPiv) {
			var returnButton = Ext.create('Ext.Button', {
				text: '证书归还',
				handler: certificateManage.searchTableInfo
			});
			
			toolbar.insert(index, returnButton);
			index++;
		}
	},
	
	importCertificateInfo: function (){
		var win = Ext.getCmp("importWin");
		if(win)
			win.close();
		
		win = Ext.create('Ext.window.Window', {
			id: 'importWin',
		    title: '导入证书信息',
		    height: 180,
		    width: 300,
		    modal:true,
		    layout: {
		        align: 'middle',
		        pack: 'center',
		        type: 'vbox'
			},
		    items: [{
			    layout: 'column',
			    border: false,
			    margin: '-20 0 0 0',
			    width: 200,	
			    items: [{
			        columnWidth: 0.7,
			        border: false,		        		            
			        html: "1、 下载模板文件",		        
			    },{
			        columnWidth: 0.3,
			        border: false,
			        items: {
			            xtype: 'button',
			            height:23,
			            text: '下载',		            		            
			            handler: certificateManage.exportTemplate
			        }
			    }]
		    },{
			    layout: 'column',
			    border: false,
			    margin: '30 0 0 0',
			    width: 200,	
			    items: [{
			        columnWidth: 0.7,
			        border: false,		        		            
			        html: "2、 请选择导入文件",		        
			    },{
			        columnWidth: 0.3,
			        border: false,
			        items: {
			            xtype: 'button',
			            id:'importcertificate',
			            height:23,
			            text: '浏览',		            		            
			            listeners:{
			            	afterrender: certificateManage.selectFile
			    		}
			        }
			    }]
		    }]
		});
		
		win.show();
	},
	
	CertifiExcelInfo: function (){
		 var map = new HashMap();
		 Rpc({functionId:'CF01050002',success:certificateManage.showCertificateBorrowed},map);
	},
	/**
     *  展现已借阅证书
     */
	showCertificateBorrowed:function(form){
		var result = Ext.decode(form.responseText);
		var tableConfig=result.tableConfig;
		var obj = Ext.decode(tableConfig);
	    certificateManage.certBorrowSubset = result.certBorrowSubset.toLowerCase();
		if(certificateManage.certBorrowSubset == "false"){
			Ext.showAlert("请设置借阅子集");  
		}
		
	    certificateManage.subModuleId = result.subModuleId;
	    certificateManage.certBorrowSubset = certificateManage.certBorrowSubset + "01"
		if(!Ext.util.Cookies.get(obj.cookiePre+"_" + certificateManage.subModuleId + "_filter"))
			Ext.util.Cookies.set(obj.cookiePre+"_" + certificateManage.subModuleId + "_filter",certificateManage.certBorrowSubset);
		
		var tableObj = new BuildTableObj(obj);
        var tablePanel = tableObj.getMainPanel();
        
        var win = Ext.create('Ext.window.Window',{
			layout: 'fit',
			id:"certWindow",
			header:false,
			renderTo : Ext.getBody(),
			maximized : true,
			closable : false,
		    border: 0, 
		    items: [tablePanel]
	    });

        win.show();
	},
	
	closeWin: function () {
		var win = Ext.getCmp('certWindow');
		if(win)
			win.close();
	},
	/**
	 *  下载模板文件
	 */
	exportTemplate: function () {
		var map = new HashMap();
		map.put("subModuleId", certificateManage.prefix);
		Rpc({functionId:'CF01050003',success:certificateManage.exportSucc},map);
	},
	
	exportSucc: function (response){
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(map.succeed){
			var fieldName = getDecodeStr(map.fileName);
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
		}
	},
	/**
	 * 浏览 上传
	 */
	selectFile : function (){
		Ext.require('SYSF.FileUpLoad', function(){
			var uploadObj = Ext.create("SYSF.FileUpLoad",{
				isTempFile:true,
				VfsModules:VfsModulesEnum.ZZ,
				VfsFiletype:VfsFiletypeEnum.doc,
				VfsCategory:VfsCategoryEnum.other,
				CategoryGuidKey:'',
				upLoadType:3,
				fileSizeLimit:'100MB',
				fileExt:"*.xls;*.xlsx;",
				buttonText:'',
				renderTo:"importcertificate",
				success:certificateManage.ImportData,
				isDelete:true,
				width:32,
				height:20
			});
			Ext.getDom("importcertificate").childNodes[1].style.marginTop = "-20px";
		});
	},

	ImportData:function(list){
		if(list.length < 0)
			return;
		
		var obj = list[0];
		if(obj){
			Ext.MessageBox.wait("", "正在导入……");	
			var map = new HashMap();
			map.put("fileid", obj.fileid);
			Rpc( {
				functionId : 'CF01050004',async:false,
				success : function (response){
					var valueMap = Ext.decode(response.responseText);
					Ext.MessageBox.close();
					if(valueMap.succeed){
						var errorMessage = valueMap.errorMessage;
						if(errorMessage)
							Ext.showAlert(errorMessage);
						else {
							var msgJson = valueMap.msgJson;
							if(msgJson) {
								var gridStore = Ext.create('Ext.data.Store', {
									fields:['primaryKey','message'],
									data: Ext.decode(msgJson),
									autoLoad: true
								});
									
								var grid = Ext.create('Ext.grid.Panel', {
								    store: gridStore,
								    id:"tipGrid_id",
								    columns: [
								        { text: '唯一性指标', dataIndex: 'primaryKey', height:30,width:'30%',sortable: false,menuDisabled:true},
								        { text: '提示信息', dataIndex: 'message', height:30,width:'69%',sortable: false,menuDisabled:true}
								    ],
								    height: 320,
								    width: "100%",
								    listeners : {  
								    	render : function(grid){
									    	Ext.create('Ext.tip.ToolTip', {
									    		target: grid.id,
											    delegate:"td",
											    trackMouse: true,
											    renderTo: document.body,
											    bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
									    	    listeners: {
												    beforeshow: function updateTipBody(tip) {
											            var div = tip.triggerElement.childNodes[0];
											            var title = "";
											            if (Ext.isEmpty(div))
											            	return false;
											        	    
												       	if(div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight){
												       		var havea = div.getElementsByTagName("a");
												            if(havea != null && havea.length > 0){
												            	title = havea[0].innerHTML;
												            } else 
												            	title = div.innerHTML;
												       		
												       		title = trimStr(title);
												       		if(Ext.isEmpty(title))
												       			return false;
												       		
												       		tip.update("<div style='WORD-BREAK:break-all;'>"+title+"</div>");
												       	}else
												       		return false;
											        }
											    }
									    	});
						            	}
								    }
								});
								
								var win = Ext.create('Ext.window.Window', {
								    title: '导入数据',
								    height: 400,
								    width: 630,
								    modal:true,
								    items: [grid],
								    buttonAlign: 'center',
								    buttons: [{
								    	text: CF_EXPORT_ERROR_MSG,
								    	handler:function(){
								    		certificateManage.exportMsgOut(msgJson);
									    }
									},{
								    	text: '关闭',
								    	handler:function(){
								    		win.close();
									    }
									}]
								});
									
								win.show();
							} else {
								var map = new HashMap();
								map.put("itemList", valueMap.itemList);
								map.put("mapsList", valueMap.mapsList);
								Rpc( {functionId : 'CF01050005',async:false,
									success : function (response){
										var msgMap = Ext.decode(response.responseText);
										var count = msgMap.count;
										var message = msgMap.msg;
										if(msgMap.succeed){
											if(Ext.isEmpty(message)) {
												Ext.getCmp("importWin").close();
												Ext.showAlert("成功导入" + count + "条数据！");
												certificateManage.subSetViewGloble.table.dataStore.reload();
											} else 
												Ext.showAlert(message);
											
										} else
											Ext.showAlert("数据导入失败！");
									}
								}, map);
							}
						}
					} else
						Ext.showAlert(valueMap.importMsg);
				}
			}, map);
		}
		
		var exportWin = Ext.getCmp('exportWinid');
	    if(exportWin)
	    	exportWin.close();
	    
	},
	
	//增加栏目设置保存后的回调函数
	schemeSaveCallback:function(){
		var certWindow = Ext.getCmp("certWindow");
		if(certWindow)
			certWindow.removeAll();
		certificateManage.CertifiExcelInfo();
	},
	
	ImportAttachment: function () {
		var map = new HashMap();
		Rpc( {functionId : 'CF01050006',
			success : certificateManage.showWin
		}, map);
	},
	
	showWin: function (response){
		var map = Ext.decode(response.responseText);
		var message = map.msg;
		if(!Ext.isEmpty(message)) {
			Ext.showAlert(message);
			return;
		}
		
		var mainSetJson = Ext.decode(map.mainSetJson);
		var mainSetStore = Ext.create('Ext.data.Store', {
			fields:['itemid','itemdesc'],
			data: mainSetJson,
			autoLoad: true
		});
		
		var subSetJson = Ext.decode(map.subSetJson);
		var subSetStore = Ext.create('Ext.data.Store', {
			fields:['itemid','itemdesc'],
			data: subSetJson,
			autoLoad: true
		});
	
		var firstFieldValue = map.firstField;
		var secondFieldValue = map.secondField;
		var firstField = Ext.create("Ext.form.field.ComboBox", {
			id : 'firstField',
			store: mainSetStore,
			width : 120,
			queryMode: 'local',
			displayField: 'itemdesc',
			valueField: 'itemid',
			labelSeparator: '',
			triggerAction : 'all',
			value: firstFieldValue
		});
		
		var secondField = Ext.create("Ext.form.field.ComboBox", {
			id : 'secondField',
			store: subSetStore,
			width : 120,
			queryMode: 'local',
			displayField: 'itemdesc',
			valueField: 'itemid',
			labelSeparator: '',
			triggerAction : 'all',
			value: secondFieldValue
		});
		
		var win = Ext.getCmp("imporAttachmenttWin");
		if(win)
			win.close();
		
		win = Ext.create('Ext.window.Window', {
			id: 'imporAttachmenttWin',
		    title: '导入证书附件',
		    height: 250,
		    width: 550,
		    modal:true,
		    layout: {
		        align: 'middle',
		        pack: 'center',
		        type: 'vbox'
			},
		    items: [{
			    layout: 'column',
			    border: false,
			    margin: '-20 0 0 0',
			    width: 500,	
			    height:24,
			    items: [{
			        columnWidth: 1,
			        border: false,		        		            
			        html: "1、 指定附件文件名规则：",		        
			    }]
		    },{
			    layout: 'column',
			    border: false,
			    margin: '10 0 0 0',
			    width: 460,	
			    height:24,
			    items: [firstField,{
			    	margin: '5 0 0 0',
			        border: false,		        		            
			        html: "_"
			    },secondField,{
			    	margin: '5 0 0 0',
			        border: false,		        		            
			        html: "_附件分类_文件名.后缀名"
			    }]
			},{
			    layout: 'column',
			    border: false,
			    margin: '10 0 0 0',
			    width: 500,	
			    items: [{
			        columnWidth: 1,
			        border: false,		        		            
			        html: "<span style='color:#afafb0'>（例1：110100199001011111_88888888_资格证书扫描件_一级建造师职业资格证书.jpg）</span>"
			            + "<span style='color:#afafb0'>（例2：110100199001011111_88888888.jpg）</span>",
			    }]
		    },{
				layout: 'column',
				border: false,
				margin: '10 0 0 0',
				width: 500,	
				items: [{
					columnWidth: 1,
					border: false,		        		            
					html: "2、 选择导入方式:",		        
				}]
			},{
				xtype:'radiogroup',
				id:'radioGroupId',
				width: 450,	
				margin: '10 0 0 0',
				columnWidth:0.5,
	            items: [{
	            	boxLabel: '更新同名附件，追加新附件',
	            	name: 'rb',
	            	inputValue: '1',
	            	checked:'true'
	            },{ 
	            	boxLabel: '删除全部附件后追加新附件', 
	            	name: 'rb', 
	            	inputValue: '2'
	            }]
			},{
				layout: 'column',
				border: false,
				margin: '10 0 0 0',
				width: 500,	
				items: [{
					columnWidth: 0.4,
					border: false,		        		            
					html: "3、请选择证书附件zip压缩包:",		        
				},{
			        columnWidth: 0.3,
			        border: false,
			        items: {
			            xtype: 'button',
			            id:'importAttachment',
			            text: '浏览',		
			            height:23,
			            listeners:{
			            	afterrender: certificateManage.selectZipFile
			    		}
			        }
				}]
			}]
		});
		
		win.show();
	},
	selectZipFile : function (){
		Ext.require('SYSF.FileUpLoad', function(){
			var uploadObj = Ext.create("SYSF.FileUpLoad",{
				isTempFile:true,
				VfsModules:VfsModulesEnum.ZZ,
				VfsFiletype:VfsFiletypeEnum.doc,
				VfsCategory:VfsCategoryEnum.other,
				CategoryGuidKey:'',
				upLoadType:3,
				fileSizeLimit:'100MB',
				fileExt:"*.zip;",
				buttonText:'',
				renderTo:"importAttachment",
				success:certificateManage.ImportAttachmentData,
				isDelete:true,
				width:32,
				height:20
			});
			Ext.getDom("importAttachment").childNodes[1].style.marginTop = "-20px";
		});
	},
	ImportAttachmentData:function(list){
		if(list.length < 0)
			return;
		
		var obj = list[0];
		if(obj){
			Ext.MessageBox.wait("", "正在导入……");	
			var firstFieldValue = Ext.getCmp("firstField").value;
			var secondFieldValue = Ext.getCmp("secondField").value;
			var importTpye = Ext.getCmp("radioGroupId").getValue().rb;
			var map = new HashMap();
			map.put("fileid", obj.fileid);
			map.put("firstField", firstFieldValue);
			map.put("secondField", secondFieldValue);
			map.put("importTpye", importTpye);
			Rpc( {
				functionId : 'CF01050008', async:false,
				success : function (response){
					Ext.MessageBox.close();
					var valueMap = Ext.decode(response.responseText);
					if(valueMap.succeed){
						var errorMessage = valueMap.errorMessage;
						if(errorMessage)
							Ext.showAlert(errorMessage);
						else {
							
							var msgJson = valueMap.info;
							if(msgJson) {
								var label = Ext.create('Ext.form.Label', {
									html: '<div style="border-bottom:1px solid #C5C5C5;padding: 5px 10px 5px 10px;height: 320px;width:500px;overflow: auto;">' + msgJson + '</div>'
								});
								
								var win = Ext.create('Ext.window.Window', {
								    title: '提示信息',
								    height: 400,
								    width: 500,
								    modal:true,
								    items: [label],
								    buttonAlign: 'center',
								    buttons: [{
								    	text: '关闭',
								    	handler:function(){
								    		win.close();
								    		certificateManage.subSetViewGloble.table.dataStore.reload();
									    }
									}]
								});
									
								win.show();
							}
						}
					} else
						Ext.showAlert(valueMap.importMsg);
				}
			}, map);
		}
		
		var exportWin = Ext.getCmp('imporAttachmenttWin');
	    if(exportWin)
	    	exportWin.close();
	    
	},
	
	searchTableInfo: function () {
		var map = new HashMap();
		Rpc({functionId: 'CF01050009', success: certificateManage.showReturnWin}, map);
	},
	
	showReturnWin: function (response) {
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(!map.succeed){
			Ext.showAlert("证书借阅记录子集存在异常，不能正常显示数据！");
			return;
		}
		
		var errorMsg = map.errorMsg;
		if(errorMsg) {
			Ext.showAlert(errorMsg);
			return;
		}
		
		var win = Ext.getCmp("returnWin");
		if(win)
			win.close();
		
		var borrowText = Ext.create('Ext.panel.Panel', {
			height: 30,
		    width: 750,
		    border: false,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'hbox'
			},
		    items:[{
		    	xtype: 'label',
		        forId: 'myFieldId',
		        width: 90,
		        html: '<div style="text-align:right;">借阅人<font style="color:red;">*</font></div>',
		        margin: '5 5 0 0'
		    },{
				xtype:'label',
				id:'personid',
				border:false,
				width:'auto',
				html:'',
				margin: '5 5 0 0'
			},{
				xtype:'label',
				html:'<a href="###" id="selMngLinkNameId" onclick="certificateManage.selectPerson(this,\'' + map.nbases +'\',2)">请选择借阅人</a>',
				margin: '5 0 0 0'
			}]
		});
		
		var returnText = Ext.create('Ext.form.field.Text', {
			id:'returnPerson',
			fieldLabel: '归还人<font style="color:red;">*</font>',
			labelAlign:'right',
			labelWidth:90,
			allowBlank:false
			
		});
		
		var returnDate = Ext.create('Ext.form.field.Date', {
			id:'returnDate',
			fieldLabel: '归还日期<font style="color:red;">*</font>',
			labelAlign:'right',
			labelWidth:90,
			value: new Date(),
			format:'Y-m-d',
			allowBlank:false
			
		});
		
		var returnDesc = Ext.create('Ext.form.field.TextArea', {
			id:'returnDesc',
			fieldLabel: '归还说明<font style="color:red;">*</font>',
			labelWidth:90,
			labelAlign:'right',
			width: 500,
			allowBlank:false,
			regex: /^[\s\S]*.*[^\s][\s\S]*$/,
			regexText:'该输入项为必填项'
			
		});
		
		certificateManage.recordFieldSet = map.recordFieldSet;
		var columns = Ext.decode(map.columns);
		var columnJson = Ext.decode(map.columnJson);
		var checkBox = Ext.create('Ext.selection.CheckboxModel'); 
		var store = Ext.create('Ext.data.Store', {
		    fields: columns,
		    id: "returnData_0001_store",
		    autoLoad : false,
		    proxy:{
				type:'transaction',
				functionId:'CF01050010',
				extraParams:{
					personid:'',
					nbase:''
				},
				reader:{
				   type:'json',
	               root:'data'
				}
			}
		});

		var gridPanel = Ext.create('Ext.grid.Panel', {
			id:"returnData_0001_grid",
		    store: store,
		    selModel:checkBox,
		    margin: '0 0 0 5',
		    columns: columnJson,
		    height: 300,
		    width: 600
		});
		
		var panel = Ext.create('Ext.panel.Panel', {
		    height: 300,
		    width: 750,
		    border: false,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'hbox'
			},
		    items:[{
		    	xtype: 'label',
		        forId: 'myFieldId',
		        text: '证书列表',
		        margin: '0 5 0 37'
		    },gridPanel]
		});
		
		win = Ext.create('Ext.window.Window', {
			id: 'returnWin',
		    title: '证书归还登记',
		    height: 550,
		    width: 750,
		    modal:true,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'vbox'
			},
			items:[borrowText, returnText, returnDate, returnDesc,panel],
			buttonAlign: 'center',
			buttons: [{
		    	text: '批量归还',
		    	handler:certificateManage.batchReturnCertificate
			},{
		    	text: '关闭',
		    	handler:function(){
		    		win.close();
			    }
			}],
			listeners: {
				beforeclose:function (){
					var picker = Ext.getCmp("person_picker_single_view");
					if(picker)
						picker.close();
				}
			}
		});
		
		win.show();
	},
	
	selectPerson:function (object, nbases, type) {
		var picker = new PersonPicker({
			isZoom:true,
			multiple : false,
			deprecate : [],
			nbases: nbases,
			selectByNbase: true,
			isPrivExpression: false,
			callback : function(person) {
				var personHtml = "";
				if(person.unit)
					personHtml += person.unit + "/";
				
				if(person.dept)
					personHtml += person.dept + "/";
				
				personHtml += person.name;
				if("1" == type) {
					Ext.getCmp("borrowPersonId").setHtml(personHtml);
					certificateManage.borrowPersonId = person.id;
				} else {
					Ext.getCmp("personid").setHtml(personHtml);
					Ext.getCmp("returnPerson").setValue(person.name);
					var store = Ext.data.StoreManager.lookup('returnData_0001_store');
					certificateManage.personid = person.id;
					Ext.apply(store.proxy.extraParams,{
						personid: person.id
					});
					
					store.load();
				}
			}
		}, object);
		
		picker.open();
	},
	
	batchReturnCertificate: function () {
		if(Ext.isEmpty(certificateManage.personid)){
			Ext.showAlert("请选择证书借阅人！");
			return;
		}
		
		var returnPerson = Ext.getCmp("returnPerson").getValue();
		if(Ext.isEmpty(returnPerson)){
			Ext.showAlert("证书归还人不能为空！");
			return;
		}
			
		var returnDate = Ext.getCmp("returnDate").getValue();
		if(Ext.isEmpty(returnDate)) {
			Ext.showAlert("证书归还日期不能为空！");
			return;
		}
		
		var returnDesc = Ext.getCmp("returnDesc").getValue();
		if(Ext.isEmpty(returnDesc)) {
			Ext.showAlert("证书归还说明不能为空！");
			return;
		}

		var tablePanel=Ext.getCmp('returnData_0001_grid');
		var selectRecord = tablePanel.getView().getSelectionModel().getSelection();
		if(selectRecord.length<1){
			  Ext.showAlert("请选择归还的证书！");
			return;
		}
		
		var certificateIds = "";
		for (var i=0;i<selectRecord.length;i++){
			var certificateNo = eval("selectRecord[" + i + "].data." + certificateManage.recordFieldSet + "03");
			var certificateClass = eval("selectRecord[" + i + "].data." + certificateManage.recordFieldSet + "01");
			if(!certificateNo || !certificateClass)
				continue;
			
			certificateIds += certificateClass + ":" + certificateNo + ",";
		}
		
		if(!certificateIds) {
			Ext.showAlert("无证书类别或编号的证书无法归还！");
			return;
		}
		
		var retunDateStr = Ext.util.Format.date(returnDate, "Y-m-d");
		Ext.showConfirm('确定要归还这些证书吗？', function(btn) {
			if (btn === 'yes') {
				var map = new HashMap();
				map.put("certificateIds", certificateIds);
				map.put("personId", certificateManage.personid);
				map.put("returnPerson", returnPerson);
				map.put("returnDate", retunDateStr);
				map.put("returnDesc", returnDesc);
				Rpc({functionId: 'CF01050011', success: certificateManage.rerurnSuccess}, map);
			} 
		});
	},
	
	rerurnSuccess: function (response) {
		var value = response.responseText;
		var map = Ext.decode(value);
		if(map.succeed){
			var errorMsg = map.errorMsg;
			if(!Ext.isEmpty(errorMsg)){
				Ext.showAlert(errorMsg);
				return;
			}
			 
			if("true" == map.flag) {
				Ext.showAlert("证书归还成功！");
				var store = Ext.data.StoreManager.lookup('returnData_0001_store');
				store.load();
				certificateManage.subSetViewGloble.table.dataStore.reload();
			}
		}
	},
	
	rendererFun: function(value,c,record) {
		var certificateNo = eval("record.data." + certificateManage.recordFieldSet + "03");
		var certificateClass = eval("record.data." + certificateManage.recordFieldSet + "01");
		return "<a href=\"###\" onclick=certificateManage.returnCertificate('"
			+ certificateClass + "','" + certificateNo + "')>"+value+"</a>";
	},
	
	returnCertificate: function (certificateClass, certificateNo) {
		if(!certificateNo || !certificateClass)
			return;
		
		var returnPerson = Ext.getCmp("returnPerson").getValue();
		if(Ext.isEmpty(returnPerson)){
			Ext.showAlert("证书归还人不能为空！");
			return;
		}
			
		var returnDate = Ext.getCmp("returnDate").getValue();
		if(Ext.isEmpty(returnDate)) {
			Ext.showAlert("证书归还日期不能为空！");
			return;
		}
		
		var returnDesc = Ext.getCmp("returnDesc").getValue();
		if(Ext.isEmpty(returnDesc)) {
			Ext.showAlert("证书归还说明不能为空！");
			return;
		}
		
		var retunDateStr = Ext.util.Format.date(returnDate, "Y-m-d");
		Ext.showConfirm('确定归还证书吗？', function(btn) {
			if (btn === 'yes') {
				var map = new HashMap();
				map.put("certificateIds", certificateClass + ":" + certificateNo);
				map.put("personId", certificateManage.personid);
				map.put("returnPerson", returnPerson);
				map.put("returnDate", retunDateStr);
				map.put("returnDesc", returnDesc);
				Rpc({functionId: 'CF01050011', success: certificateManage.rerurnSuccess}, map);
			} 
		});
	},
	
	checkCertificateInfo: function () {
		var tablePanel=Ext.getCmp(certificateManage.fieldSet + '_tablePanel');
		var selectRecord = tablePanel.getView().getSelectionModel().getSelection();
		if(!selectRecord || selectRecord.length < 1){
			Ext.showAlert("请选择要借阅的证书！");
			return;
		}
		
		var certificateIds = "";
		for (var i=0;i<selectRecord.length;i++){
			var certificateNo = eval("selectRecord[" + i + "].data." + certificateManage.certNOItemId);
			var certificateClass = eval("selectRecord[" + i + "].data." + certificateManage.certCategoryItemId);
			var nbase = eval("selectRecord[" + i + "].data.nbase");
			var certName = eval("selectRecord[" + i + "].data." + certificateManage.certName);
			if(!certificateNo || !certificateClass || !nbase)
				continue;
			
			certificateIds += certificateClass + ":" + certificateNo + ":" + nbase + ":" + certName + ",";
		}
		
		if(!certificateIds){
			Ext.showAlert("无证书类别或编号的证书无法借出！");
			return;
		}
		
		var map = new HashMap();
		map.put("certificateIds", certificateIds);
		Rpc({functionId: 'CF01050012', success: certificateManage.showBorrowWin}, map);
	},
	
	showBorrowWin: function (response) {
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(!map.succeed){
			Ext.showAlert("证书借阅子集存在异常，不能正常显示数据！");
			return;
		}
		
		var errorMsg = map.errorMsg;
		if(errorMsg) {
			Ext.showAlert(errorMsg);
		}
		
		var columns = Ext.decode(map.columns);
		var columnJson = Ext.decode(map.columnJson);
		var certificateJson = Ext.decode(map.certificateJson);
		if(Ext.isEmpty(columns) || Ext.isEmpty(columnJson) || Ext.isEmpty(certificateJson)) 
			return;
		
		var win = Ext.getCmp("borrowWin");
		if(win)
			win.close();
		
		var borrowPerson = Ext.create('Ext.panel.Panel', {
			height: 30,
		    width: 700,
		    border: false,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'hbox'
			},
		    items:[{
		    	xtype: 'label',
		        forId: 'myFieldId',
		        width: 90,
		        html: '<div style="text-align:right;">借阅人<font style="color:red;">*</font></div>',
		        margin: '5 5 0 0'
		    },{
				xtype:'label',
				id:'borrowPersonId',
				border:false,
				order:false,
				html:'',
				margin: '5 5 0 0'
			},{
				xtype:'label',
				html:'<a href="###" id="selBorrowNameId" onclick="certificateManage.selectPerson(this,\'' + map.nbases +'\',1)">请选择借阅人</a>',
				margin: '5 0 0 0'
			}]
		});
		
		var borrowDate = Ext.create('Ext.form.field.Date', {
			id:'borrowDate',
			fieldLabel: '借阅日期<font style="color:red;">*</font>',
			labelAlign:'right',
			labelWidth:90,
			height:23,
			value: new Date(),
			format:'Y-m-d',
			allowBlank:false
		});
		
		var estimateReturnDate = Ext.create('Ext.form.field.Date', {
			id:'returnDateId',
			fieldLabel: '预计归还日期<font style="color:red;">*</font>',
			labelAlign:'right',
			labelWidth:90,
			height:23,
			value: new Date(),
			format:'Y-m-d',
			margin: '0 0 0 100',
			allowBlank:false
		});
		
		var borrowDesc = Ext.create('Ext.form.field.TextArea', {
			id:'borrowDesc',
			fieldLabel: '借阅说明<font style="color:red;">*</font>',
			labelWidth:90,
			labelAlign:'right',
			width: 500,
			margin: '5 5 0 0',
			allowBlank:false,
			regex: /^[\s\S]*.*[^\s][\s\S]*$/,
			regexText:'该输入项为必填项'
			
		});
		// 非系统指标项
		var fieldItemsPanel = Ext.create('Ext.panel.Panel', {
			id:'fieldItemsPanel',
			margin: '5 5 0 0',
			border:0,
			width:680,
			defaults:{
				labelWidth:90
			},
			scrollable:true,
			layout:{
				type:'table',
				columns:2,
				tdAttrs:{
					align:'left'
				},
				tableAttrs:{
					width:'100%'
				}
			},
			items:[]
		});
		certificateManage.recordFieldSet = map.recordFieldSet;
		certificateManage.certEndDateItemId = map.certEndDateItemId;
		var store = Ext.create('Ext.data.Store', {
		    fields: columns,
		    id: "borrowData_0001_store",
		    data: certificateJson
		});

		var gridPanel = Ext.create('Ext.grid.Panel', {
			id:"borrowData_0001_grid",
			border: 1,
		    store: store,
		    columns: columnJson,
//		    margin: '0 0 0 5',
		    height: 140,
		    width: 600
		});
		
		var panel = Ext.create('Ext.panel.Panel', {
		    height: 160,
		    width: 705,
		    border: false,
		    margin: '5 5 0 0',
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'hbox'
			},
		    items:[{
			    	xtype: 'label',
			        forId: 'myFieldId',
			        width: 90,
			        html: '<div style="text-align:right;">证书列表</div>',
			        margin: '5 5 0 0'
			    }
		    	,gridPanel
		    	]
		});
		
		win = Ext.create('Ext.window.Window', {
			id: 'borrowWin',
		    title: '证书借阅登记',
//		    height: 550,
		    autoHeight: true,
		    maxHeight: 500,
		    width: 750,
		    modal:true,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'vbox'
			},
			items:[borrowPerson
				, {
					border:false,
					layout: {
				        align: 'top',
				        type: 'hbox'
					},
					items:[
						borrowDate
						, estimateReturnDate
					]}
				, borrowDesc
				, fieldItemsPanel
				, panel],
			buttonAlign: 'center',
			buttons: [{
		    	text: '确定',
		    	handler:certificateManage.borrowCertificate
			},{
		    	text: '关闭',
		    	handler:function(){
		    		win.close();
			    }
			}],
			listeners: {
				beforeclose:function (){
					var picker = Ext.getCmp("person_picker_single_view");
					if(picker)
						picker.close();
				},
				close:function(){
					// 50782 借阅窗口关闭后清空借阅人
					certificateManage.borrowPersonId = "";	
					win.destroy();
				}
			}
		});
		// 借阅子集非系统项指标集合
		certificateManage.fieldItems = map.fieldItems;
		certificateManage.setFieldItemsPanel(map.fieldItems);
		win.show();
	},
	/**
	 * 添加借阅子集其他指标
	 */
	setFieldItemsPanel:function(fieldList){
		
		var fieldItemsPanel=Ext.getCmp('fieldItemsPanel');
		fieldItemsPanel.removeAll ( true );
		//判断是否是同一行第一个子集
		var isFirstItem=false;
		var linePanel;
//		this.respon=respon;//respon.//respon.
		for(var i=0;i<fieldList.length;i++){
			var field=fieldList[i];
			var margin="5 0 5 0";
			isFirstItem=!isFirstItem;
			if(field.itemtype=="A"){
				if(field.codesetid=="0"){
					fieldItemsPanel.add({
						xtype:'textfield',
						id:field.itemid+"id",
						margin:margin,
						name:field.itemid,
						maxLength:field.itemlength,
//						allowBlank:field.allowblank,
						required:field.allowblank,
						fieldLabel:field.itemdesc,
						border:0,
						labelAlign:'right',
						labelWrap:true,
						clearIcon:true,
						value:field.value,
						listeners:{
							change:function( me, newValue, oldValue, eOpts ) {
//								me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
							}
						}
					});
				}else{
					fieldItemsPanel.add({
						xtype:'codecomboxfield',
						id:field.itemid+"id",
						margin:margin,
						name:field.itemid,
						required:field.allowblank,
						ctrltype:'1',//this.getCtrltype(),
						nmodule:'0',//this.getNmodule(),
//						allowBlank : false, 
						fieldLabel:field.itemdesc,
						codesetid:field.codesetid,
						onlySelectCodeset:false,
						border:0,
						labelAlign:'right',
						labelWrap:true,
						clearIcon:true,
						value:field.value,
						listeners:{
							change:function( me, newValue, oldValue, eOpts ) {
//								me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
							}
						}
					});
				}
			}else if(field.itemtype=="M"){
				if(!isFirstItem){
					fieldItemsPanel.add({
						xtype:'container'
					})
				};
				fieldItemsPanel.add({
					xtype:'textarea',
					id:field.itemid+"id",
					name:field.itemid,
					colspan:2,
					style:'',
					required:field.allowblank,
					maxLength:field.itemlength==10?Number.MAX_VALUE:field.itemlength,
//					allowBlank:field.allowblank,
					fieldLabel:field.itemdesc,
					msgTarget :'under',
					border:0,
					width:500,//'90%',
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
				isFirstItem=false;
			}else if(field.itemtype=="N"){
				fieldItemsPanel.add({
					xtype:'numberfield',
					id:field.itemid+"id",
					margin:margin,
					name:field.itemid,
					maxLength:field.itemlength,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					decimalPrecision:field.demicallength,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}else if(field.itemtype=="D"){
				var format='Y';
				if(field.itemlength=='4'){
					format='Y';
				}else if(field.itemlength=='7'){
					format='Y-m';
				}else if(field.itemlength=='10'){
					format='Y-m-d';
				}else if(field.itemlength=='16'){
		          	format = 'Y-m-d H:i';
				}else if(field.itemlength=='18'){
		            format = 'Y-m-d H:i:s';
				}
				fieldItemsPanel.add({
					xtype:'datetimefield',
					id:field.itemid+"id",
					margin:margin,
					name:field.itemid,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					format:format,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}else{
				fieldItemsPanel.add({
					xtype:'textfield',
					id:field.itemid+"id",
					margin:margin,
					name:field.itemid,
					maxLength:field.itemlength,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}
		}
	},
	rendererBorrowFun: function(value) {
		return "<a href=\"###\" onclick=certificateManage.removeCertificate(" + value + ")>取消</a>";
	},
	
	removeCertificate: function (id) {
		
		Ext.showConfirm('确定取消该证书吗？', function(btn) {
			if (btn === 'yes') {
				var store = Ext.data.StoreManager.lookup('borrowData_0001_store');
				var record = store.getById(id);
				store.remove(record);
				store.commitChanges();
				// 50778 若只有一个证书再点击取消 直接关闭借阅窗口
				if(0 == store.data.length){
					var win = Ext.getCmp("borrowWin");
					if(win)
						win.close();
				}
			}
		});
	},
	/**
	 * 借阅证书 确定
	 */
	borrowCertificate: function () {
		var browDate = Ext.getCmp("borrowDate").getValue();
		if(Ext.isEmpty(browDate)){
			Ext.showAlert("请填写借阅日期！");
			return;
		}
		var retunDate = Ext.getCmp("returnDateId").getValue();
		if(Ext.isEmpty(retunDate)){
			Ext.showAlert("请填写预计归还日期！");
			return;
		}
		
		if(!(retunDate > browDate)){
			Ext.showAlert("预计归还日期小于或等于借阅日期，请重新填写！");
			return;
		}
		
		var borrowDesc = Ext.getCmp("borrowDesc").value;
		if(Ext.isEmpty(borrowDesc)){
			Ext.showAlert("请填写借阅说明！");
			return;
		}
		
		var store = Ext.data.StoreManager.lookup('borrowData_0001_store');
		var records = [];
		store.each(function(record,index){
			records.push(record.data);
		});
		
		if(!records || records.length < 1) {
			Ext.showAlert("请选择要借阅的证书！");
			return;
		}
		
		if(Ext.isEmpty(certificateManage.borrowPersonId)){
			Ext.showAlert("请选择证书借阅人！");
			return;
		}
		
		var retunDateStr = Ext.util.Format.date(retunDate, "Y-m-d");
		var errorCert = "";
		for(var i = 0; i < records.length; i++) {
			var endDate = eval("records[" + i + "]." + certificateManage.certEndDateItemId);
			if(endDate && retunDateStr > endDate) {
				var certName = eval("records[" + i + "]." + certificateManage.certName);
				errorCert += certName + ",";
			}
		}
		
		if(!Ext.isEmpty(errorCert)) {
			errorCert = errorCert.substring(0, errorCert.length - 1);
			Ext.showAlert(errorCert + "等证书的预计归还日期超过了到期日期，不允许借出！");
			return;
		}
		// 校验其他指标 用户自定义其他的指标
		var list = new Array();
		var map = new HashMap();
		for(var i=0;i<certificateManage.fieldItems.length;i++){
			var field = certificateManage.fieldItems[i];
			var itemid = field.itemid;
			var idvalue = Ext.getCmp(itemid+'id').getValue();
			// 是否必填
			if(field.allowblank){
				if(Ext.isEmpty(idvalue)){
					Ext.showAlert(field.itemdesc+"为必填项，请重新填写！");
					return;
				}					
			}
			map = new HashMap();
			map.put("itemid", itemid);
			map.put("itemtype", field.itemtype);
			map.put("codesetid", field.codesetid);
			map.put("value", idvalue+"");
//			console.log(map);
			list.push(map);
		}
		
		var browDateStr = Ext.util.Format.date(browDate, "Y-m-d");
		Ext.showConfirm('确定要借阅这些证书吗？', function(btn) {
			if (btn === 'yes') {
				var map = new HashMap();
				map.put("records", records);
				map.put("personId", certificateManage.borrowPersonId);
				map.put("browDate", browDateStr);
				map.put("retunDate", retunDateStr);
				map.put("borrowDesc", borrowDesc);
				map.put("fieldsData", list);
				Rpc({functionId: 'CF01050013', success: certificateManage.borrowSuccess}, map);
			}
		});
	},
	
	borrowSuccess: function (response) {
		var map = Ext.decode(response.responseText);
		if(map.succeed){
			var errorMsg = map.errorMsg;
			if(!Ext.isEmpty(errorMsg)){
				Ext.showAlert(errorMsg);
				return;
			} else {
				Ext.showAlert("证书借阅成功！");
				Ext.getCmp("borrowWin").close();
				certificateManage.subSetViewGloble.table.dataStore.reload();
			}
		}
	},
	
	showCodeitemDesc: function (value) {
		var itemdesc = value.split("`")[1];
		return itemdesc;
	},
	
	exportDate: function () {
		var tablePanel=Ext.getCmp(certificateManage.fieldSet + '_tablePanel');
		var selectRecord = tablePanel.getView().getSelectionModel().getSelection();
		var certificateIds = "";
		if(selectRecord && selectRecord.length > 0){
			for (var i=0;i<selectRecord.length;i++){
				var certificateNo = eval("selectRecord[" + i + "].data." + certificateManage.certNOItemId);
				var certificateClass = eval("selectRecord[" + i + "].data." + certificateManage.certCategoryItemId);
				var nbase = eval("selectRecord[" + i + "].data.nbase");
				var certName = eval("selectRecord[" + i + "].data." + certificateManage.certName);
				if(!certificateNo || !certificateClass || !nbase)
					continue;
				
				certificateIds += certificateClass + ":" + certificateNo + ",";
			}
		}
		
		var map = new HashMap();
		map.put("certificateIds", certificateIds);
		Rpc({functionId: 'CF01050016', success: certificateManage.exportSuccess}, map);
	},
	
	exportSuccess: function(response) {
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			var fieldName = getDecodeStr(map.fileName);
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
		}
	},
	/**
	 * 导出错误信息
	 */
	exportMsgOut: function(msgJson){
		var map = new HashMap();
		map.put("msgJson", msgJson);
		Rpc({functionId: 'CF01050017', success: certificateManage.exportSuccess}, map);
	}
});