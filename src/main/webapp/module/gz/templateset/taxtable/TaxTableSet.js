Ext.define('TaxTable.TaxTableSet',{
	requires:['SYSF.FileUpLoad','TaxTable.TaxTableDetail'],
	extend:'Ext.panel.Panel',
	id:"TaxTableSet",
	xtyle:'TaxTableSet',
	layout:'fit',
	bodyStyle:'border:0px solid #ffffff;',
	initComponent:function(){
		TaxTable_me = this;
		this.callParent();
		this.loadData();
	},
	loadData:function(){
		var paramMap = new HashMap();
		Rpc({functionId:'GZ00001001',success:this.getTableOK,scope:this},paramMap);
	},
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var return_code = result.returnStr.return_code;
		if(return_code == 'success'){
			var tableConfig = result.returnStr.return_data.getTableConfig;
			TaxTable_me.editableFlag = Ext.decode(result.returnStr.return_data.editable);
			var obj = Ext.decode(tableConfig);
			//加载表格之前做列的处理和布局修改
			obj.beforeBuildComp = function (grid) {
				grid.tableConfig.dockedItems.pop();
				var columns = grid.tableConfig.columns;
				for(var i in columns){
					columns[i].menuDisabled=true;
					columns[i].sortable=false;
					columns[i].draggable=false;
				}
				//修改cellediting插件为双击修改
				grid.tableConfig.plugins.pop();
				grid.tableConfig.plugins.push({ptype:'cellediting',clicksToEdit:2});
	        };
			TaxTable_me.tableObj = new BuildTableObj(obj);
			TaxTable_me.gridPanel = TaxTable_me.tableObj.tablePanel;
			TaxTable_me.gridStore = TaxTable_me.gridPanel.getStore();
			TaxTable_me.gridPanel.addListener('render',TaxTable_me.setUnEditable);
			TaxTable_me.add(TaxTable_me.tableObj.getMainPanel());
			TaxTable_me.gridPanel.addListener('cellclick',TaxTable_me.onCellClick);
			TaxTable_me.gridPanel.addListener('beforeedit',TaxTable_me.setDateBeforeInput);
			TaxTable_me.gridPanel.addListener('validateedit',TaxTable_me.valiDataAfterInput);
		}else{
			var return_msg = result.returnStr.return_msg;
			Ext.Msg.alert(gz.taxTableHomePage.msg.tip,eval(return_msg));
			return;
		}
	},
	//渲染名称列
	rendDescFunc:function(value, metaData, record, rowIndex, colIndex, store, view){
		//无编辑权限,渲染纯文本
		if(TaxTable_me.editableFlag == false){
			return value;
		}
		var taxid = record.data.taxid;
		var description = record.data.description.replace(/\s+/g,'%20').replace(/'/g,'\\\'').replace(/>/g,'');
		var k_base = record.data.k_base;
		var taxModeCode = record.data.param.split("`")[0];
		var html = "<a href=javascript:TaxTable_me.toDetilPage('"+taxid+"','"+description+"','"+k_base+"','"+taxModeCode+"');>" + value + "</a>";
		return html;
	},
	//新增跳转到明细表
	addNewRecordFunc:function(){
		var taxDetilPanel = Ext.create("TaxTable.TaxTableDetail");
		TaxTable_me.tableObj.getMainPanel().hide();
		TaxTable_me.add(taxDetilPanel);
	},
	deleteRecordFunc:function(){
		var selectedItems = TaxTable_me.gridPanel.getSelectionModel().selected.items;
		if(selectedItems.length == 0){
			Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.noneSeleted);
			return;
		}
		Ext.Msg.confirm(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.deleteTaxTable,function(btn){
			if(btn == 'yes'){
				var ids = '';
				for(var i = 0; i < selectedItems.length;i++){
					ids += selectedItems[i].data.taxid +",";
				}
				ids = ids.substring(0,ids.length-1);
				var paramMap = new HashMap();
				paramMap.put('ids',ids);
				Rpc({functionId:'GZ00001003',success:TaxTable_me.deleteOK,scope:this},paramMap);
			}
		});
	},
	deleteOK:function(res){
		var result = Ext.decode(res.responseText);
		var return_code = result.returnStr.return_code;
		var selectedItems = TaxTable_me.gridPanel.getSelectionModel().selected.items;
		if(return_code == 'success'){
			TaxTable_me.gridStore.remove(selectedItems);
		}else{
			var return_msg = result.returnStr.return_msg;
			//导出占用信息txt
			window.open("/servlet/vfsservlet?fileid=" + result.returnStr.return_data.file_name + "&fromjavafolder=true");
			Ext.Msg.alert(gz.taxTableHomePage.msg.tip,eval(return_msg));
		}
	},
	saveFunc:function(){
		var allParamMap = new HashMap();
		var parameterList = new Array();
		var parameterMap;
		var gridStore = TaxTable_me.gridStore;
		var modifiedRecords = gridStore.getModifiedRecords();
		//没有修改过数据
		if(modifiedRecords.length == 0){
			Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.noneModifiedRecords);
			return;
		}
		var repeated = '';
		for(var i = 0;i < modifiedRecords.length;i++){
			var internalId = modifiedRecords[i].internalId;
			var modifiedDescription = modifiedRecords[i].data.description;
			gridStore.each(function(record){
				//税率表名称不重复
				if(modifiedDescription != record.get('description')){
					//continue
					return true;
				}
				//这条记录不是自己
				if(record.internalId != internalId){
					repeated += modifiedDescription + ',';
					//break
					return false;
				}
			});
		}
		if(repeated.length != 0){
			Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.repeatdTaxTablePre+repeated.slice(0,repeated.length - 1)+gz.taxTableHomePage.msg.repeatdTaxTableSuf);
			return;
		}
		//匹配&nbsp，null，和各种非法字符。
		var strTestWSave = /&nbsp|null|[<>?"{}.\[\]]/ig;
		for(var i = 0;i < modifiedRecords.length;i++){
			parameterMap = {};
			//组装修改过的记录的信息
			var taxid = modifiedRecords[i].data.taxid;
			var description = modifiedRecords[i].data.description;
			var k_base = modifiedRecords[i].data.k_base;
			var param = modifiedRecords[i].data.param;
			var taxModeCode = modifiedRecords[i].data.param.split("`")[0];
			//不能通过正则验证不保存
			parameterMap['taxid'] = taxid;
			if(description.replace(/^\s+/,'').replace(/\s+$/,'') == ''){
				Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.stringPureBlank);
				return;
			}else if(strTestWSave.test(description)){
				Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.stringIllegal);
				return;
			}
			//去掉前后空格
			parameterMap['description'] = description.replace(/^\s+/,'').replace(/\s+$/,'');
			parameterMap['k_base'] = k_base;
			parameterMap['taxModeCode'] = taxModeCode;
			parameterList.push(parameterMap);
		}
		allParamMap.put('params',parameterList);
		Rpc({functionId:'GZ00001002',success:TaxTable_me.saveOK,scope:this},allParamMap);
	},
	saveOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var result = Ext.decode(form.responseText);
		var return_code = result.returnStr.return_code;
		if(return_code == 'success'){
			TaxTable_me.loadData();
			TaxTable_me.gridStore.reload();
			Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.saveTaxTableSuccess);
		}else{
			var return_msg = result.returnStr.return_msg;
			Ext.Msg.alert(gz.taxTableHomePage.msg.tip,return_msg);
		}
	},
	//跳转到明细页面
	toDetilPage:function(taxid_e,description_f,k_base_f,taxModeCode_f){
		var detilPagePanel = Ext.create("TaxTable.TaxTableDetail",{
			taxid:taxid_e,
			description:description_f,
			k_base:k_base_f,
			taxModeCode:taxModeCode_f
		});
		TaxTable_me.tableObj.getMainPanel().hide();
		TaxTable_me.add(detilPagePanel);
	},
	//编辑之前设置内容
	setDateBeforeInput:function(editor,content,eOpts){
		content.column.currentRecord = content.record;
	},
	//校验输入数据
	valiDataAfterInput:function(editor, content, eOpts){
		if(content.field == 'description'){
			if(content.value.length == 0){
				Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.stringIsNull);
				content.cancel = true;
			}
		}else if(content.field == 'k_base'){
			if(content.originalValue != null){
				if(typeof content.value != 'number'){
					Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.numIllegal);
					content.cancel = true;
				}else if(content.value < 0){
					Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.numLessThanZero);
					content.cancel = true;
				}
			}else {
				content.cancel = true;
			}
		}
	},
	//计税方式下拉框设置只能选择不能编辑
	setUnEditable:function(tablePanel){
		var paramColumn = tablePanel.getColumnManager().getHeaderByDataIndex("param");
		if (paramColumn) {
			var editor = paramColumn.getEditor();
			editor.editable = false;
		}
	},
	//处理单击事件
	onCellClick:function(panel,td,cellIndex,record,tr,rowIndex,e,eOpts){
		//单击第一列，不能编辑
		if(cellIndex == 1 || cellIndex == 0){
			return;
		}
		//单击其余列，开始编辑
		var cellediting = TaxTable_me.gridPanel.findPlugin('cellediting');
		cellediting.startEditByPosition({
			row:rowIndex,
			column:cellIndex
		});
	},
	importFunc:function(){
		var fileUpLoad = Ext.create("SYSF.FileUpLoad", {
            margin: '10 0 0 22',
            upLoadType: 1,
            readInputWidth: 302,
            fileExt: "*.xls",
			isTempFile: true,
			VfsModules: VfsModulesEnum.GZ,
			VfsFiletype: VfsFiletypeEnum.other,
			VfsCategory: VfsCategoryEnum.other,
			CategoryGuidKey: '',
            success: function (files) {
                var file = files[0];
                var param = new HashMap();
                param.put('fileid', file.fileid);
                //Ext.Msg.wait("正在导入数据");
                Rpc({
                    functionId: 'GZ00001005', success: function (resp) {
                        var result = Ext.decode(resp.responseText);
                        var return_code = result.return_code;
                        var return_msg = result.return_Msg;
                        var return_data = result.return_data;
                        fileUpLoad.info.hide();
                        if (return_code == 'fail') {//失败的话弹出提示信息,不再往下执行
                            Ext.Msg.hide();
                        	win.close();
                            Ext.Msg.alert(gz.taxTableHomePage.msg.tip,eval(return_msg));
                            return;
                        }
                    	if(return_data.taxList.length == 0){
                    		//Ext.Msg.hide();
                    		Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.importSuccess);
                    		win.close();
                    		//刷新前台数据
                    		TaxTable_me.loadData();
                    		Ext.getCmp("taxTable_tablePanel").store.reload();
                    		return;
                    	}
                		Ext.Msg.hide();
                		win.close();
                		TaxTable_me.importSuccess(return_code,return_data);
                    }
                }, param);
            },
        });
        var win = Ext.create('Ext.window.Window', {
            title: gz.taxTableHomePage.msg.importTitle,
            layout: 'vbox',
            width: 410,
            height: 140,
            id: 'importDataWindow',
            modal: true,
            items: [{
                xtype: 'tbtext',
                margin: '10 0 0 10',
                html: gz.taxTableHomePage.msg.importHtml
            }, fileUpLoad]
        });
        win.show();
	},
	importSuccess:function(return_code,return_data){
		valuelist = return_data.taxList;
		if(valuelist.length>0){
			var data = [];
			for(var i = 0;i<valuelist.length;i++){
				var map = new HashMap();
				var value = valuelist[i];
				var taxid = value.taxid;
				map.put('taxid',taxid);
				var description = valuelist[i].description;
				map.put('taxname',description);
				map.put('type',gz.taxTableHomePage.msg.importTypeAdd);
				data.push(map);
			}
			var importStore = Ext.create('Ext.data.Store', {
				field:['taxid','taxname','type'],
				data : data
			});
			var importGrid = Ext.create('Ext.grid.Panel', {
				id : 'importGrid',
				rowLines : true,
				columnLines:true,
				bufferedRenderer : false,
				plugins: {
			        ptype: 'cellediting',
			        clicksToEdit: 1
			    },
				store : importStore,
				selModel: {
					selType: 'checkboxmodel'
				},
				columns:[{
					hidden:true, 
					dataIndex : 'taxid',
					menuDisabled : true,
					sortable : false
				},{
					header : gz.taxTableHomePage.msg.importTaxName,
					dataIndex : 'taxname',
					flex:2,
					menuDisabled : true,
					sortable : false
				},{
					header : gz.taxTableHomePage.msg.importType,
					dataIndex : 'type',
					editable :true,
					editor : { 
						xtype : 'combo',
						editable : false, // 是否可编辑
						store : [gz.taxTableHomePage.msg.importTypeAdd,gz.taxTableHomePage.msg.importTypeUpdate]
					},
					flex:1,
					menuDisabled : true,
					sortable : false
				}]
			});
			TaxTable_me.win2 = Ext.create('Ext.window.Window', {
				resizable:false,
				layout: 'fit',
				title: gz.taxTableHomePage.msg.importTypeTitle,
				width: 400,
				height: 290,
				items:importGrid,
				modal: true,
				buttonAlign:'center',
				buttons:[{
					text:gz.taxTableHomePage.msg.importButtonName,
					xtype:'button',
					handler:function(){
						TaxTable_me.importButton();
					}
				}]
			});
			TaxTable_me.win2.show();
		}
	},
	importButton:function(){
		var records = Ext.getCmp("importGrid").getSelectionModel().getSelection();//获取选中行
		var paramList = [];
		var count = 0;
		for(var i=0;i<records.length;i++){
			var record = records[i];
			var taxid = record.get('taxid');
			var type = record.get('type');
			for(var j=0;j<valuelist.length;j++ ){
				var id = valuelist[j].taxid;
				if(taxid == id){
					if(type == gz.taxTableHomePage.msg.importTypeUpdate){//覆盖
						type='0';
						count++;
					}else{//追加
						type='1';
					}
					valuelist[j].type=type;
					paramList.push(valuelist[j]);
				}
			}
		};
		if(paramList.length==0){
			Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.importMsg);
			return;
		}
		if(count>0){
			Ext.Msg.confirm(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.importCoverMsg,function(op){
				if(op=='yes'){
					TaxTable_me.importConfirm(paramList);
				}
			});
		}else{
			TaxTable_me.importConfirm(paramList);
		}
	},
	importConfirm:function(paramList){
		var map = new HashMap();
		map.put('taxList',paramList);
		Rpc({
			functionId: 'GZ00001005', success: function (resp) {
				var result = Ext.decode(resp.responseText);
				var return_code = result.return_code;
				var return_msg = result.return_Msg;
				if(return_code=="success"){
					Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.importSuccess);
					TaxTable_me.win2.close();
					//刷新前台数据
					TaxTable_me.loadData();
					Ext.getCmp("taxTable_tablePanel").store.reload();
					return;
				}
				Ext.Msg.alert(gz.taxTableHomePage.msg.tip,eval(return_msg));
				TaxTable_me.win2.close();
			}
		},map);
	},
	exportFunc:function(){
		var records = Ext.getCmp("taxTable_tablePanel").getSelectionModel().getSelection();//获取选中行
		var ids = '';
		for(var i=0;i<records.length;i++){
			var record = records[i];
			var taxid = record.get('taxid');
			ids = ids + taxid;
			if(i<records.length-1){
				ids = ids +',';
			}
		}
    	if(ids.length==0){
			Ext.Msg.alert(gz.taxTableHomePage.msg.tip,gz.taxTableHomePage.msg.exportSelectNull);
    		return;
    	}
    	var param = new HashMap();
    	param.put('ids',ids);
    	Rpc({
    		functionId:'GZ00001004', async: false,success:function(res){
    			var result = Ext.decode(res.responseText);
    			var return_data = result.return_data;
    			var return_code = result.return_code;
    			var return_msg = result.return_Msg;
				if (return_code == 'success') {
					window.open("/servlet/vfsservlet?fileid=" + return_data.file_name + "&fromjavafolder=true");
				} else {
    				Ext.Msg.alert(gz.taxTableHomePage.msg.tip,eval(return_msg));
				}
    		}
    	},param);
	}
});
