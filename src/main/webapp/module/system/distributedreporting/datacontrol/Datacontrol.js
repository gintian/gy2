/**
 * 数据接收日志界面js
 */
Ext.define('DatacontrolUL.Datacontrol',{
	data_control:'',
	tableObj:'',
	constructor:function(config) {
		data_control=this;
        data_control.init();
	},
	init:function(){
		 var map = new HashMap();
		 map.put("value","all");
		 Rpc({functionId:'SYS0000003012',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					data_control.createTableOK(result,form,action);
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
			,data_control.createLabel("all",dr.gd.all), '-'
			,data_control.createLabel("today",dr.gd.today)
			,data_control.createLabel("week",dr.gd.sevendays)
			,data_control.createLabel("month",dr.gd.thirtydays)]
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
					Rpc({functionId: 'SYS0000003012', success: function (form,action) {
						Ext.getCmp(data_control.labelId).removeCls('scheme-selected-cls');
						label.addCls('scheme-selected-cls');
						data_control.labelId = label.id;
						data_control.labelFlag = labelFlag;
						Ext.getCmp('datacontrol001_tablePanel').getStore().reload();
					}},map);
				}
			}
		});
		if(labelFlag=="all"){//初始化样式
			label.addCls('scheme-selected-cls');
			data_control.labelId = label.id;//用于移除样式
			data_control.labelFlag = labelFlag;//用于删除日志时，知道传哪个方案
		}
		return label;
	},
    //删除
	deleteLogRecord:function(){
		var grid = Ext.getCmp('datacontrol001_tablePanel');
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
	acceptStatus:function(value,metaData,Record){
		var status = Record.data.status;
		var value = reception_failure;
		if(1==status){
			value = receive_success;
		}
		return value;
	},
	//下载日志
	downloadingLog:function(value,metaData,Record){
		var id = Record.data.id;
		return "<a onclick='data_control.downloadingLogOK("+id+");' href='javascript:void(0);' >"+downloading_log+"</a>";
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
