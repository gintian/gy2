/**
 * 上报数据界面js
 */
Ext.define('ReportdataUL.ReportData',{
	requires: ["SYSF.FileUpLoad"],
	reportData:'',
	tableObj:'',
	constructor:function(config) {
		reportData=this;
		reportData.init();
	},
	init:function(){
		 var map = new HashMap();
		 map.put("value","all");
		 Rpc({functionId:'SYS0000003036',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					reportData.createTableOK(result,form,action);
				}else{
					Ext.MessageBox.alert(hint_information,result.message);
				}
			}},map);
	},
	createTableOK:function(result,form,action){
		Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}","underline");
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		tableObj = new BuildTableObj(obj);
		var toolbar  = Ext.create('Ext.toolbar.Toolbar',{//查询方案toolbar
			border:0,
			dock:'top',
			items: [{
				xtype: 'label',
				text: dr.gd.selectPlan,//查询方案
				style: 'margin-left:5px'
			}
			,reportData.createLabel("all",dr.gd.all), '-'
			,reportData.createLabel("today",dr.gd.today)
			,reportData.createLabel("week",dr.gd.sevendays)
			,reportData.createLabel("month",dr.gd.thirtydays)]
		});
		tableObj.insertItem(toolbar,0);
	},
	/**
	 * 查询方案后面的label
	 * @param labelFlag 哪一个查询方案
	 * @param labelDesc 方案描述
	 * @returns {Ext.Component}
	 */
	createLabel:function(labelFlag,labelDesc){
		var label = Ext.create("Ext.Component", {
			html: labelDesc,
			style: 'cursor:pointer;color:#1B4A98;margin-left:5px;margin-right:5px;',
			listeners:{
				element: "el",
				click:function(){
					var map = new HashMap();
					map.put('value',labelFlag);
					Rpc({functionId: 'SYS0000003036', success: function (form,action) {
						Ext.getCmp(reportData.labelId).removeCls('scheme-selected-cls');
						label.addCls('scheme-selected-cls');
						reportData.labelId = label.id;
						reportData.labelFlag = labelFlag;
						reportData.reload();
					}},map);
				}
			}
		});
		if(labelFlag=="all"){//初始化样式
			label.addCls('scheme-selected-cls');
			reportData.labelId = label.id;//用于移除样式
			reportData.labelFlag = labelFlag;//用于删除日志时，知道传哪个方案
		}
		return label;
	},
	reload:function(){
		Ext.getCmp('reportdata001_tablePanel').getStore().reload();
	},
	//上报数据导入数据包
	importDataZip:function(){
		var fileUpload = Ext.create("SYSF.FileUpLoad",{//创建上传附件组件
            upLoadType:1,//单文件上传
            fileExt:"*.zip",
            readInputWidth: 300,
			fileSizeLimit: "10GB",
            emptyText:dr.rt.selectdadazip,//请选择上传的数据包
			isTempFile: true,
			VfsFiletype:VfsFiletypeEnum.doc,
			VfsModules:VfsModulesEnum.TB,
			VfsCategory: VfsCategoryEnum.other,
			CategoryGuidKey: '',
            success:function(list){
                if(list.length!=0){
                    var map = new HashMap();
                    map.put("operaType", "importDataZip");
                    map.put("file", list[0]);
                    Rpc({functionId: 'SYS0000003037', success: function (res) {
                        var returnData = Ext.decode(res.responseText);
                        var return_code = returnData.return_code;
                        if (return_code == "success") {
                        	reportData.reload();
                        	win.close();
                        } else {
                            Ext.Msg.alert(dr.gd.tip, return_code);//请上传符合规范的文件！
                        }
                    }}, map);
                }
            },
            //回调方法，失败
            error:function(){
                Ext.MessageBox.alert(dr.gd.tip, dr.gd.uploaderrormsg);// 文件上传失败！
            }
        });
	    var win = Ext.create('Ext.window.Window', {
            title: dr.rt.dadazip,//"上传数据包"
            layout: 'vbox',
            width: 375,
            height: 120,
            id: 'importDataWindow',
            modal: true,
            items: [{
                xtype: 'tbtext',
                margin: '20 0 0 20',
                html: ""
            }, fileUpload]
        });
        win.show();
	},
    //删除
	deleteLogRecord:function(){
		var grid = Ext.getCmp('reportdata001_tablePanel');
		var record=grid.getSelectionModel().getSelection();
		if(record.length<=0){
			Ext.MessageBox.alert(hint_information,please_select_the_delete_records);
			return;
		}
		Ext.Msg.confirm(hint_information,whether_delete_it,function(btn){ 
			if(btn=="yes"){ 
				// 确认触发，继续执行后续逻辑。 1:启用；0：未启用
			    var map = new HashMap();
			    var ids = "";
			    var exper="";
			    for ( var i = 0; i < record.length; i++) {
			    	var temp =record[i].data;
			    	ids+=temp.id+",";
			    }
			    map.put("ids",ids);
			    Rpc({functionId:'SYS0000003013',async:false,success:function(form,action){Ext.getCmp('datacontrol001_tablePanel').getStore().reload();}},map);
			} 
		});
	},
	//下载日志
	downloadingLog:function(value,metaData,Record){
		var id = Record.data.id;
		return "<a onclick='reportData.downloadingLogOK("+id+");' href='javascript:void(0);' >"+downloading_log+"</a>";
	},
	downloadingLogOK:function(id){
		Ext.MessageBox.wait("", in_the_process_of_exporting);
		var map = new HashMap();
	    map.put('id',id+'');
	    Rpc({functionId:'SYS0000003014',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					if(result.flag){
						Ext.MessageBox.close();	
						var fieldName = result.fileName;
						window.location.href = "/servlet/vfsservlet?fileid=" + fieldName + "&fromjavafolder=true";
					}else{
						Ext.MessageBox.close();	
						Ext.Msg.alert(hint_information,no_erroneous_log_records);
					}
				}else{
					Ext.MessageBox.close();	
					Ext.Msg.alert(hint_information,result.message);
				}
		}},map);
	}
});
