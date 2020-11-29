Ext.define('KqCardDataURL.KqCardData',{
	constructor:function(config) {
		KqCardData = this;
		flag = false;
		KqCardData.prefix="kqCardData_01";
		KqCardData.importType = '';
		this.init();
	},
	
	init: function () {
		var map = new HashMap();
	    Rpc({functionId:'KQ00021601',success:KqCardData.showPanel},map);
	},
	//展现页面布局
	showPanel: function (response) {
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			if("true" != map.errorMessage) {
				Ext.showAlert(map.errorMessage);
				return;
			}
			
			var conditions=map.tableConfig;
			var tableConfig = Ext.decode(conditions);
			if(!Ext.util.Cookies.get(tableConfig.cookiePre+"_" + KqCardData.prefix + "_filter"))
				Ext.util.Cookies.set(tableConfig.cookiePre+"_" + KqCardData.prefix + "_filter","e0122");
			
			KqCardData.table = new BuildTableObj(tableConfig);
			var tablePanel = KqCardData.table.getMainPanel();
			
			new Ext.Viewport( {
				id:"holidayPort",
				layout : "fit",
				items:[tablePanel]
			});
			
			KqCardData.createSearchPanel(Ext.decode(map.fieldArray));
		} else {
			Ext.showAlert(map.message);
		}
			
	},
	//切换日期查询数据
	searchCardData:function (){
		var sDate = Ext.getCmp("fromDate").value;
		var eDate = Ext.getCmp("toDate").value;
		if(sDate.getTime() > eDate.getTime()) {
			Ext.showAlert(kq.card.errorTimemsg);
			return;
		}
		
		sDate = Ext.Date.format(sDate,"Y.m.d H:i");
		eDate = Ext.Date.format(eDate,"Y.m.d H:i");
		var param = {
				sDate: sDate,
				eDate: eDate
		};
		var map = new HashMap();
		map.put("type", "searchAction");
		map.put("param", Ext.encode(param));
	    Rpc({functionId:'KQ00021601',success:KqCardData.reloadTable},map);
	},
	//重新加载表格
	reloadTable: function(response) {
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			if("true" == map.errorMessage) 
				Ext.getCmp(KqCardData.prefix + '_tablePanel').getStore().reload();
			else
				Ext.showAlert(map.errorMessage);
		}else {
			Ext.showAlert(map.message);
		}
	},
	//删除数据
	deleteData: function () {
		var selectRecord = Ext.getCmp(KqCardData.prefix + '_tablePanel').getView().getSelectionModel().getSelection();
		if(selectRecord.length<1){
			Ext.showAlert(kq.card.deleteDataEmpty);
			return;
		} 

		Ext.Msg.confirm(kq.card.tip, kq.card.deleteDataMsg, function(btn) {
			if (btn != 'yes')
				return;
			
			var deleteDatas = new Array();
			for(var i = 0; i < selectRecord.length; i++){
				var data = selectRecord[i].data;
				var deleteData = {
						nbase : data.nbase_e,
						a0100 : data.a0100_e,
						cardtime : data.cardtime
				};
				
				deleteDatas.push(deleteData);
			}
			
			var map = new HashMap();
			map.put("type", "deleteAction");
			map.put("param", Ext.encode(deleteDatas));
			Rpc({functionId:'KQ00021601',success:KqCardData.reloadTable},map);
		});
	},
	// 数据分析
	dataAnalysis: function () {
		var map = new HashMap();
		Ext.require('KqCardDataURL.KqCardDataAnalysis', function(){
			Ext.create("KqCardDataURL.KqCardDataAnalysis", map);
		});
	},
	// 查询控件
	createSearchPanel: function(fieldsArray){
		var me = this;
		var map = new HashMap();
		
		me.SearchBox = Ext.create("EHR.querybox.QueryBox",{
			id:'queryBox',
			hideQueryScheme:false,
			emptyText:kq.card.searchEmptyText,
			subModuleId:KqCardData.prefix,
			customParams:map,
			funcId:"KQ00021602",
			fieldsArray:fieldsArray,
			success:function (){Ext.getCmp(KqCardData.prefix + '_tablePanel').getStore().reload({page:1});}
		});
		
		Ext.getCmp('kqCardData_01_toolbar').add(me.SearchBox);
	},
	//导出excel
	exportCardData: function () {
		var selectRecord = Ext.getCmp(KqCardData.prefix + '_tablePanel').getView().getSelectionModel().getSelection();
		var cardDatas = new Array();
		for(var i = 0; i < selectRecord.length; i++){
			var data = selectRecord[i].data;
			var cardData = {
					nbase : data.nbase_e,
					a0100 : data.a0100_e,
					cardtime : data.cardtime
			};
			
			cardDatas.push(cardData);
		}
		Ext.MessageBox.wait("", kq.card.exportMsg);	
		var map = new HashMap();
		map.put("type", "exportAction");
		map.put("param", Ext.encode(cardDatas));
		Rpc({functionId:'KQ00021601',success:KqCardData.exportSucc},map);
	},
	//导出excel
	exportSucc: function (response){
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(map.succeed){
			Ext.MessageBox.close();
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fileid="+map.fileName+"&fromjavafolder=true";
		} else {
			Ext.showAlert(map.message);
		}
	},
	//下载模板、导入数据
	importCardData: function () {
		var importWin = Ext.getCmp('importWinid');
	    if(importWin)
	    	importWin.close();
		
	    importWin = Ext.create('Ext.window.Window', {
			id: 'importWinid',
		    title: kq.card.importTitle,
		    height: 180,
		    width: 320,
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
			    width: 240,	
			    items: [{
			        columnWidth: 0.5,
			        border: false,		        		            
			        html: kq.card.downloadTemplate,		        
			    },{
			        columnWidth: 0.5,
			        border: false,
			        items: [{
			            xtype: 'button',
			            text: kq.card.singleColumn,		            		            
			            handler: function (){
			            	KqCardData.exportTemplate("1");
			            }
			        },{
			            xtype: 'button',
			            text: kq.card.multipleColumn,	
			            margin:'0 0 0 5',
			            handler: function (){
			            	KqCardData.exportTemplate("2");
			            }
			        }]
			    }]
		    },{
			    layout: 'column',
			    border: false,
			    margin: '30 0 0 0',
			    width: 240,	
			    items: [{
			        columnWidth: 0.5,
			        border: false,		        		            
			        html: kq.card.selectFile,		        
			    },{
			        columnWidth: 0.5,
			        border: false,
			        items: {
			            xtype: 'button',
			            id:'importCardData',
			            text: kq.card.browse,		            		            
			            listeners:{
			            	afterrender: KqCardData.selectFile
			    		}
			        }
			    }]
		    }]
		});
		
	    importWin.show();
	},
	
	exportTemplate: function (importType) {
		Ext.MessageBox.wait("", kq.card.exportMsg);	
		var map = new HashMap();
		map.put("type", "exportTemplateAction");
		map.put("importType", importType);
		Rpc({functionId:'KQ00021601',success:KqCardData.exportSucc},map);
	},
	
	selectFile: function(){
		Ext.require('SYSF.FileUpLoad', function(){
			var uploadObj = Ext.create("SYSF.FileUpLoad",{
				isTempFile:true,
				VfsModules:VfsModulesEnum.KQ,
				VfsFiletype:VfsFiletypeEnum.doc,
				VfsCategory:VfsCategoryEnum.other,
				CategoryGuidKey:'',
				upLoadType:3,
				fileSizeLimit:'500MB',
				fileExt:"*.xls;*.xlsx;",
				buttonText:'',
				renderTo:"importCardData",
				success:KqCardData.importData,
				isDelete:true,
				width:32,
				height:20
			});
			Ext.getDom("importCardData").childNodes[1].style.marginTop = "-20px";
		});
	},
	
	importData: function (list) {
		if(list.length < 0)
			return;
		
		var obj = list[0];
		if(obj){
			Ext.MessageBox.wait("", kq.card.importMsg);	
			var map = new HashMap();
			map.put("type", "importTemplateAction");
			map.put("fileid", obj.fileid);
			Rpc({functionId : 'KQ00021601',
				success : function (response){
					var valueMap = Ext.decode(response.responseText);
					Ext.MessageBox.close();
					if(valueMap.succeed){
						var errorMessage = valueMap.errorMessage;
						if("true" != errorMessage)
							Ext.showAlert(errorMessage);
						else {
							var msgJson = valueMap.msgJson;
							if(msgJson) {
								var gridStore = Ext.create('Ext.data.Store', {
									fields:['cardNo','message'],
									data: Ext.decode(msgJson),
									autoLoad: true
								});
								
								var grid = Ext.create('Ext.grid.Panel', {
									store: gridStore,
									columns: [
										{ text: kq.card.cardNo, dataIndex: 'cardNo', height:30,width:'30%' },
										{ text: kq.card.tipMsg, dataIndex: 'message', height:30,width:'69%' }
									],
									height: 320,
									width: "100%",
									listeners : {  
							    		render : function(gridPanel){
									    	Ext.create('Ext.tip.ToolTip', {
									    		target: gridPanel.id,
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
									title: kq.card.importTitle,
									height: 400,
									width: 500,
									modal:true,
									items: [grid],
									buttonAlign: 'center',
									buttons: [{
										text: kq.button.close,
										handler:function(){
											win.close();
										}
									}]
								});
								
								win.show();
							} else {
								var map = new HashMap();
								map.put("type", "saveCardDataAction");
								Rpc( {functionId : 'KQ00021601',
									success : function (response){
										var msgMap = Ext.decode(response.responseText);
										if("true" == msgMap.errorMessage){
											Ext.MessageBox.close();
											var count = msgMap.count;
											if(msgMap.succeed && !Ext.isEmpty(count)){
												var msg = kq.card.importCount;
												msg = msg.replace("{0}", count);
												Ext.showAlert(msg);
												Ext.getCmp(KqCardData.prefix + '_tablePanel').getStore().reload();
											} else
												Ext.showAlert(kq.card.importFail);
										} else
											Ext.showAlert(msgMap.errorMessage);
											
									}
								}, map);
							}
						}
					} else
						Ext.showAlert(valueMap.message);
				}
			}, map);
		}
		
		var importWin = Ext.getCmp('importWinid');
	    if(importWin)
	    	importWin.close();
	}
	
});