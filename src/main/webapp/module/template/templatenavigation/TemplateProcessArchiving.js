/**
 * 流程归档
 */
Ext.define('TemplateNavigation.TemplateProcessArchiving',{
	constructor:function(config){
		templateProcessArchiving = this;
		templateProcessArchiving.callBackFunc = config.callBackFunc;
		templateProcessArchiving.init();
	},
	init:function(){
		//得到列表数据
		var map = new HashMap();
		map.put("transType","0");//查询归档列表
	    Rpc({functionId:'MB00006022',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var conditions=result.tableConfig;
			var obj = Ext.decode(conditions);
			templateProcessArchiving.templateObj = new BuildTableObj(obj);
	    	var pageHeight = Ext.getBody().getViewSize().height;
	        var pageWidth = Ext.getBody().getViewSize().width;
	        var map = new HashMap();
	        templateProcessArchiving.SearchBox = Ext.create("EHR.querybox.QueryBox", {
                emptyText: MB.PROCESSARCHIVING.QUERYTEXT,
                subModuleId: "processarchiving",
                customParams: map,
                funcId: "MB00006022",
                hideQueryScheme: true,
                queryBoxWidth:290,
                success: templateProcessArchiving.searchArchiveOK
            });
            // 表格toolbar
            var toolbar_data = Ext.create('Ext.toolbar.Toolbar', {
            	border:0,
                items: [templateProcessArchiving.SearchBox]
            });
			var win = Ext.widget('window',{
				id:'archivewin',
				title:MB.PROCESSARCHIVING.PROCESSARCHIV,
				height:pageHeight*0.7,
				width:pageWidth*0.5<605?605:pageWidth*0.5,
				resizable : false,
	            autoScroll:false,
	            border : false,
	            modal : true,
	            layout:'fit',
	            items:[templateProcessArchiving.templateObj.getMainPanel()],
	            tbar:toolbar_data,
	            buttonAlign:'center',
	            buttons:[{
					text : MB.PROCESSARCHIVING.ARCHIVING,
					width : 75,
					height : 22,
					margin:'0 10 0 0',
					handler : function() {
						var selectRecords = templateProcessArchiving.templateObj.tablePanel.getSelectionModel().getSelection();
                    	if(selectRecords.length<1){
                    		Ext.showAlert(MB.PROCESSARCHIVING.SELECTARCHIVEDATA);
                			return;
                    	}
                    	var tabids = "";
                    	for(var i=0;i<selectRecords.length;i++){
            				var tabid = selectRecords[i].data.tabid;
            				tabids+=tabid+",";
            			}
                    	tabids=tabids.substring(0,tabids.length-1);
						templateProcessArchiving.selectProcessDate(tabids);
					}
				}, {
					text : MB.PROCESSARCHIVING.CANCEL,
					width : 75,
					height : 22,
					handler : function() {
						win.close();
					}
				}]
			});
			win.show();
	    }},map);
	},
	searchArchiveOK:function(){
		var store = Ext.data.StoreManager.lookup('processarchiving1_dataStore');
		store.currentPage=1;
		store.load();
	},
	showArchivingTime:function(value, metaData, Record){
		var value_ = value;
		if(value==""){
			value_ = MB.PROCESSARCHIVING.NOARCHIVE;
		}else{
			value = replaceAll(value,".","-");
			var valuearr = value.split("-");
			value_ = valuearr[0]+common.label.year+valuearr[1]+common.label.month+valuearr[2]+common.label.day+MB.PROCESSARCHIVING.PREVIOUSDATA;
		}
		return value_;
	},
	selectProcessDate:function(tabids){
		var win = Ext.widget('window',{
			id:'selectdatewin',
			title:MB.PROCESSARCHIVING.PROCESSARCHIV,
			resizable : false,
            autoScroll:true,
            border : false,
            modal : true,
            height:220,
            width:350,
            bbar:[{xtype : 'tbfill'},{
				text : MB.PROCESSARCHIVING.SURE,
				width : 75,
				height : 22,
				margin:'0 10 0 0',
				handler : function() {
					templateProcessArchiving.processArchiving(tabids);
				}
			}, {
				text : MB.PROCESSARCHIVING.CANCEL,
				width : 75,
				height : 22,
				handler : function() {
					win.close();
				}
			}, {
				xtype : 'tbfill'
			}],
			items:[{xtype:'panel',layout:'vbox',margin:'10 0 10 20',
				border:0,items:[
					{xtype:'radio',id:'all',boxLabel:MB.PROCESSARCHIVING.ALL,name:'selectdate',margin:'0 0 0 20'},
					{xtype:'panel',border:0,layout:'hbox',items:[{xtype:'radio',id:'part',
						name:'selectdate',margin:'0 0 0 20',checked:true},{
				        xtype: 'datefield',
				        allowBlank:false,
				        id:'processdate',
				        width:110,
				        format: 'Y-m-d',
				        margin:'0 5 0 5'
				    },{xtype:'label',text:MB.PROCESSARCHIVING.PREENDPROCESSARCHIVING},{xtype:'label',margin:'0 0 0 5',text:MB.PROCESSARCHIVING.AGODATA}]},
				    {xtype:'label',margin:'10 20 0 20',width:260,html:MB.PROCESSARCHIVING.WARNINGTEXT,style:'color:red;'},
				    {xtype:'label',margin:'2 20 0 20',width:260,html:MB.PROCESSARCHIVING.WARNINGTEXT1},
				    {xtype:'label',margin:'2 20 0 20',width:260,html:MB.PROCESSARCHIVING.WARNINGTEXT2}
			    ]}
			]
		});
		win.show();
	},
	processArchiving:function(tabids){
		var processdate='';
		var all = Ext.getCmp('all').getValue();
		var part = Ext.getCmp('part').getValue();
		if(part){
			processdate = Ext.getCmp('processdate').getValue();
			processdate = Ext.Date.format(processdate, "Y-m-d");
			if(processdate==''||processdate=='null'){
				Ext.showAlert(MB.PROCESSARCHIVING.SELECTARCHIVEDATE);
				return;
			}else{
				var date = new Date();
				if(Ext.getCmp('processdate').getValue()>date){
					Ext.showAlert(MB.PROCESSARCHIVING.SHOWMESSAGEBIGDATE);
					return;
				}
			}
		}else{
			processdate = 'all';
		}
		Ext.showConfirm(MB.PROCESSARCHIVING.CONGIRMMESSAGE,
                function(btn){
                    if(btn=="yes"){
                    	Ext.MessageBox.wait(MB.PROCESSARCHIVING.WAITMESSAGE1, common.msg.wait);
                    	var map = new HashMap();
                		map.put("transType","1");//归档数据
                		map.put("processdate",processdate);
                		map.put("tabids",tabids);
                		LRpc({functionId:'MB00006022',async:true,success:function(form,action){
                	    	Ext.MessageBox.close();
                	    	var responseText = Ext.decode(form.responseText);
                			var returnObj = Ext.decode(responseText.returnStr);
                	        var return_code = returnObj.return_code;
                	        var return_msg = returnObj.return_msg;
                	        if(return_code =="failed"){
                	        	Ext.showAlert(return_msg);
                	        }else{
                	        	var store = Ext.data.StoreManager.lookup('processarchiving1_dataStore');
                    	    	store.load();
                    	    	Ext.getCmp('selectdatewin').close();
                    	    	if(templateProcessArchiving.callBackFunc){
                    	            Ext.callback(eval(templateProcessArchiving.callBackFunc),null,[]);
                    			}
                	        }
                	    }},map);
                    }
                    else {
                        return;
                    }
                } 
         ); 
	},
	getNowFormatDate:function() {
        var date = new Date();
        var seperator1 = "-";
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentdate = year + seperator1 + month + seperator1 + strDate;
        return currentdate;
    }
})