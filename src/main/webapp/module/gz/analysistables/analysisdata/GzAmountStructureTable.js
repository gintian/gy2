/**
 * 薪资分析 各月工资构成分析表
 * sunjian 2019-8-9
 */

Ext.define('AnalysisdataURL.GzAmountStructureTable', {
    constructor: function (config) {
    	gzAst_me = this;
    	gzAst_me.selectAll = true;
    	gzAst_me.rsid = config.rsid;
    	gzAst_me.rsdtlid = config.rsdtlid;
    	gzAst_me.imodule = config.imodule;
    	gzAst_me.edit_pow = config.edit_pow;//如果没有修改权限，取数范围也不让设置了
    	gzAst_me.tableName = config.tableName;
    	gzAst_me.init(config);
    },
    init: function(config) {
    	//first-into
        var json = {};
		json.rsid = gzAst_me.rsid;
		json.rsdtlid = gzAst_me.rsdtlid;
		json.year = "";
		json.fieldid = "";
		json.month = "";
		json.codevalue = "";
		json.fieldListId = "";
		json.selectAll = gzAst_me.selectAll;
		json.transType = 1;//1:生成台账报表数据 2:导出excel
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
        Rpc({functionId: 'GZ00000712', async: false, success: gzAst_me.getTableOK}, map);
    },
    getTableOK: function (form, action) {
        var result = Ext.decode(form.responseText);
        var config = result.returnStr.return_data.dataTableConfig;
        //3个需要显示的下拉框集合
        var yearList = result.returnStr.return_data.yearList;
        gzAst_me.fieldList = result.returnStr.return_data.fieldListId;
        
        //页面显示的
        gzAst_me.not_enc_rsid = result.returnStr.return_data.not_enc_rsid;
        gzAst_me.not_enc_rsdtlid = result.returnStr.return_data.not_enc_rsdtlid;
        gzAst_me.year = result.returnStr.return_data.year;//年
        gzAst_me.month = result.returnStr.return_data.month;//年
        gzAst_me.field = result.returnStr.return_data.fieldid;//薪资项
        gzAst_me.codevalue = result.returnStr.return_data.codevalue;//代码值
        gzAst_me.codesetid = result.returnStr.return_data.codesetid;//代码类
        
		var obj = Ext.decode(config);
		obj.columnNowrap = true;//表头不换行
		gzAst_me.gzSttObj = new BuildTableObj(obj);
		gzAst_me.addDataListGridListens(gzAst_me.gzSttObj);
		//创建store
		gzAst_me.yearListStore = Ext.create('Ext.data.Store', {//年份
            fields: ['name', 'id'],
            data: yearList
        });

		gzAst_me.monthListStore = Ext.create('Ext.data.Store', {//月份
            fields: ['name', 'id'],
            data : [
		        {"name":"1", "id":"1"},
		        {"name":"2", "id":"2"},
		        {"name":"3", "id":"3"},
		        {"name":"4", "id":"4"},
		        {"name":"5", "id":"5"},
		        {"name":"6", "id":"6"},
		        {"name":"7", "id":"7"},
		        {"name":"8", "id":"8"},
		        {"name":"9", "id":"9"},
		        {"name":"10", "id":"10"},
		        {"name":"11", "id":"11"},
		        {"name":"12", "id":"12"}
		    ]
        });

		gzAst_me.fieldidListStore = Ext.create('Ext.data.Store', {//薪资项
            fields: ['name', 'id'],
            data: gzAst_me.fieldList
        });
		
		//创建toolbar，展示
		gzAst_me.createToolBar();
    },
    
    createToolBar: function() {
    	var setRangeBut = Ext.widget("button", {
    		text: gz.label.analysisdata.setrange,
    		height: 24,
    		padding: '0 0 0 2',
    		hidden: !gzAst_me.edit_pow,
    	    handler: function() {
    	    	gzAst_me.setRange();
    	    }
        });
    	
    	//年份
    	var combo_year = gzAst_me.createComBox('yearListId', 100, gzAst_me.yearListStore, 'name', 'id', gz.label.year, 'right', 10, gzAst_me.year, false, false);
    	//截止月份
    	var combo_month = gzAst_me.createComBox('monthListId', 140, gzAst_me.monthListStore, 'name', 'id', gz.label.analysisdata.deadlineMonth, 'left', 
    												50, gzAst_me.month, false, false);
        
    	//显示每月人数
        var radio_showData = Ext.widget("radiogroup", {
            vertical: true,
            width: 100,
            items: [
                { boxLabel: gz.label.all, name: 'show_data', inputValue: '1', checked: gzAst_me.selectAll, style: 'width:50px;' },
                { boxLabel: gz.label.part, name: 'show_data', inputValue: '2', checked: !gzAst_me.selectAll, style: 'padding-left:5px;width:100px;' }
            ],
            listeners: {
            	change: function (obj, newValue, oldValue, eOpts) {
	            	if(newValue.show_data == '2') {
	            		gzAst_me.selectAll = false;
	            		Ext.getCmp("fieldId").setHidden(false);
	            		Ext.getCmp("codeId").setHidden(false);
	            	}else {
	            		gzAst_me.selectAll = true;
	            		Ext.getCmp("fieldId").setHidden(true);
	            		Ext.getCmp("codeId").setHidden(true);
	            	}
	            	gzAst_me.reload();
	            }
	        }
        });
        
        //薪资项
        var combo_field = gzAst_me.createComBox('fieldId', 150, gzAst_me.fieldidListStore, 'name', 'id', "", 'left', 50, gzAst_me.field, false, gzAst_me.selectAll);
        
        //代码值
        //var combo_code = gzAst_me.createComBox('codeId', 220, gzAst_me.codeValueListStore, 'name', 'id', "", 'left', 50, gzAst_me.codevalue, false, gzAst_me.selectAll);
        
        var combo_code = gzAst_me.getCodeCobo(gzAst_me.codevalue, gzAst_me.codesetid);
        
        var toolbar = Ext.create('Ext.toolbar.Toolbar', {
            border: 0,
            id: "toolbar",
            dock: 'top',
            height: 35,
            items: [{
            	xtype: 'button',
            	text: gz.label.analysisdata.navigation,
            	height: 24,
            	menu:{
            		items:[{
            			text: gz.label.outExcel,//导出excel
            			icon:"/images/export.gif",
            			handler:function(){
            				Ext.MessageBox.wait(common.msg.exporting+"...", common.msg.wait);
            				gzAst_me.export_excel();
            			}
            		},
	                {
            			text: gz.label.analysisdata.pagesetting, //'页面设置'
	                    icon: '/images/img_o.gif',
	                    handler: function () {
	                    	gzAst_me.showpagesetting();
	                    }
	                }]
            	}
            },'-',setRangeBut,combo_year,combo_month,radio_showData,combo_field,combo_code]
        });
    	gzAst_me.gzSttObj.insertItem(toolbar, 0);
    	gzAst_me.centerpanel = gzAst_me.gzSttObj.getMainPanel();
    	gzAst_me.panel = Ext.create('Ext.container.Container',{
            border : 0,
            layout:'fit',  
            items: [
            	gzAst_me.centerpanel
            ]
        });
    },
    // 显示层级代码项
    getCodeCobo: function(codevalue, codesetid, selectAll) {
    	if(Ext.getCmp("codeId")) {
    		Ext.getCmp("codeId").destroy();
    	}
    	return Ext.widget('codecomboxfield', {
			id : 'codeId',
			value : codevalue,
			width : 220,
			codesetid : codesetid,
			hidden: gzAst_me.selectAll,
			style: 'padding-left:20px;',
			labelAlign: 'left',
			nmodule : '1',
			ctrltype : '3',
			afterCodeSelectFn : function(a, value) {
				gzAst_me.reload('codeId', value);
			}
		});
    },
    getMainView: function() {
    	return gzAst_me.panel;
    },
    
    createComBox: function(id, width, store, disfield, vfield, name, labelAlign, labelWidth,value, match_flag, hidden) {
    	return Ext.create('Ext.form.ComboBox', {
	        id: id,
	        width: width,
	        store: store,
	        displayField: disfield,
	        valueField: vfield,
	        editable: true,
	        matchfieldWidth:match_flag,//长度超出了自适应
	        queryMode: 'local',
	        fieldLabel: name,//分析项目
	        labelAlign: labelAlign,
	        labelWidth: labelWidth,
	        hidden: hidden,
	        style: 'padding-left:20px;',
	        value: value,
	        listeners: {
	            select: function (combo, records) {
	            	gzAst_me.reload(id, combo.value);
	            }
	        }
    	});
    },
    
    reload: function(id, value){
    	//赋值，保证页面上永远是对的值
    	gzAst_me.changeValue(id, value);
    	
    	var json = {};
    	json.rsid = gzAst_me.rsid;
		json.rsdtlid = gzAst_me.rsdtlid;
		json.year = gzAst_me.year;
		json.fieldid = gzAst_me.field;
		json.month = gzAst_me.month;
		json.codevalue = gzAst_me.codevalue;
		json.fieldListId = gzAst_me.fieldListId;
		json.selectAll = gzAst_me.selectAll;
		json.transType = 1;//1:生成台账报表数据 2:导出excel
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'GZ00000712',async:false,success:gzAst_me.reloadStore},map);
    },
    reloadStore: function(form, action) {
    	Ext.getCmp("gzAmountStructure1_" + gzAst_me.rsdtlid + "_tablePanel").getStore().load();
    	
    	if(gzAst_me.isRefresh) {
	    	var result = Ext.decode(form.responseText);
	    	var yearList = result.returnStr.return_data.yearList;
	    	gzAst_me.year = result.returnStr.return_data.year;//年
	    	gzAst_me.fieldList = result.returnStr.return_data.fieldListId;
	        gzAst_me.field = result.returnStr.return_data.fieldid;//薪资项
	        gzAst_me.codevalue = result.returnStr.return_data.codevalue;//代码值
	        gzAst_me.codesetid = result.returnStr.return_data.codesetid;//代码值
	        
	        gzAst_me.yearListStore.setData(yearList);
	        gzAst_me.fieldidListStore.setData(gzAst_me.fieldList);
	        Ext.getCmp("yearListId").setValue(gzAst_me.year);//代码值
	        Ext.getCmp("fieldId").setValue(gzAst_me.field);//代码值
			Ext.getCmp("codeId").setValue(gzAst_me.codevalue);//代码值
    	}
    },
    changeValue: function(id, value) {
    	if(id == "yearListId") {
    		gzAst_me.isRefresh = false;
    		gzAst_me.year = value;
    	}else if(id == "fieldId") {
    		gzAst_me.isRefresh = true;
    		gzAst_me.field = value;
    		gzAst_me.codevalue = "";//如果切换薪资项，则先把代码置为空
    		if(value) {
    			var codeCom = gzAst_me.getCodeCobo(gzAst_me.codevalue, value.split("`")[1]);
    			var toolbar = Ext.getCmp('toolbar');
   				toolbar.insert(toolbar.items.items.length,codeCom);
    		}
    	}else if(id == "monthListId") {
    		gzAst_me.isRefresh = false;
    		gzAst_me.month = value;
    	}else if(id == "codeId") {
    		gzAst_me.isRefresh = false;
    		gzAst_me.codevalue = value;
    	}else if(id == "reload"){
    		gzAst_me.isRefresh = true;
    	}else {
    		gzAst_me.isRefresh = false;
    	}
    },
    export_excel: function() {
    	var json = {};
    	json.rsid = gzAst_me.rsid;
		json.rsdtlid = gzAst_me.rsdtlid;
    	json.transType = 2;//1:生成台账报表数据 2:导出excel
    	json.tableName = gzAst_me.tableName;
    	var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
    	Rpc({functionId:'GZ00000712',async:false,success:function(form,action){
            Ext.MessageBox.close();
            var result = Ext.decode(form.responseText);
            if(result.succeed){
            	var fieldName = getDecodeStr(result.returnStr.return_data.fileName);
                window.location.target="_blank";
                window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
            }else{
                Ext.showAlert(result.message);
            }

        }},map);
    },
    /**
     * 设置取数范围
     */
    setRange:function(){
        //将页面作为窗口展现出来
        var panel = Ext.create("Analysistable.OptAnalysisTable",{
                opt:3,
                imodule:gzAst_me.imodule,
                rsid:gzAst_me.not_enc_rsid,
                rsdtlid:gzAst_me.not_enc_rsdtlid,
                callBack:function(){
                    Ext.getCmp("setrangewin").close();
                    gzAst_me.reload('reload', '');
                }
            });
        OptAnalysisTable_me.setRange();
    },
    /**
     * 页面设置
     */
    showpagesetting: function() {
    	var json = {};
    	json.rsid = gzAst_me.rsid;
		json.rsdtlid = gzAst_me.rsdtlid;
		json.opt = "1";//显示每月人数
		json.transType = 3;//1:生成台账报表数据 2:导出excel
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
        Rpc({functionId : 'GZ00000712',success: function(form){
            var result = Ext.decode(form.responseText);
            Ext.create("EHR.exportPageSet.ExportPageSet",{
            	rsid:gzAst_me.rsid,
            	rsdtlid:gzAst_me.rsdtlid,
            	result:result,
            	callbackfn:'gzAst_me.savePageSet'
            });
        }}, map);
    },
    /**
     * 保存页面设置
     * @param pagesetupValue
     * @param titleValue
     * @param pageheadValue
     * @param pagetailValue
     * @param textValueValue
     * @param type
     */
    savePageSet:function(pagesetupValue,titleValue,pageheadValue,pagetailValue,textValueValue,type) {
    	var json = {};
    	json.rsid = gzAst_me.rsid;
		json.rsdtlid = gzAst_me.rsdtlid;
		json.opt = "2";//显示每月人数
		json.transType = 3;//1:生成台账报表数据 2:导出excel
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(json));
        map.put("pagesetupValue",pagesetupValue);
        map.put("titleValue",titleValue);
        map.put("pageheadValue",pageheadValue);
        map.put("pagetailidValue",pagetailValue);
        map.put("textValueValue",textValueValue);
        Rpc({functionId : 'GZ00000712',success: function(form){
        	var result = Ext.decode(form.responseText);
            var returnObj = result.returnStr;
            if(returnObj.return_code == "success") {
                //Ext.showAlert(gz.label.zxdeclare.saveSuccess);
            }else{
                Ext.showAlert(gz.label.zxdeclare.saveFail);
            }
        }}, map);
    },
    
    addDataListGridListens:function(grid){
		grid.tablePanel.on('columnresize',function(ct, column, width, eOpts){
			var dataIndex = column.dataIndex;//修改的列codeitemid
			var json = {};
			json.rsid = gzAst_me.rsid;
			json.rsdtlid = gzAst_me.rsdtlid;
	    	json.codeitemid = dataIndex;
			json.submoduleid = "gzAmountStructure_" + gzAst_me.rsdtlid;
			json.width = width+"";
			json.isshare = "0";//0 私有方案 1共有方案
			json.transType = 5;
			var map = new HashMap();
			map.put("jsonStr", JSON.stringify(json));
			Rpc({functionId:'GZ00000712',async:false,success:function(){},scope:gzAst_me},map);
		});
		grid.tablePanel.on('columnmove',function(ct, column, width, eOpts){
			gzAst_me.saveColumnMove(grid, column);
		});
		
		grid.tablePanel.on('columnlockmove',function(ct, column, width, eOpts){
			gzAst_me.saveColumnMove(grid, column);
		});
		
	},
    // 调整顺序
	saveColumnMove:function(grid, column) {
		var tablePanel = grid.tablePanel;
		var is_lock = column.isLocked()?'1':'0';
		var index = tablePanel.getColumnManager().getHeaderIndex(column);
		var nextcolumn = tablePanel.getColumnManager().getHeaderAtIndex(index+1);
		var nextid = "-1";
		if(nextcolumn && nextcolumn.dataIndex)
			nextid = nextcolumn.dataIndex;
		
		var json = {};
		json.rsid = gzAst_me.rsid,
		json.rsdtlid = gzAst_me.rsdtlid
        json.transType = "6";
        json.is_lock = is_lock;
        json.itemid = column.dataIndex;
        json.nextid = nextid;
        json.submoduleid = "gzAmountStructure_" + gzAst_me.rsdtlid;
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(json));
        Rpc({
            functionId: 'GZ00000712', async: true, success: function (res) {
            	
            },
            scope: this
        }, map);
	}
})