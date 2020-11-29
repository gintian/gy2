/**
 * 薪资分析 各月工资构成分析表
 * sunjian 2019-8-9
 */

Ext.define('AnalysisdataURL.GzStructureTable', {
    constructor: function (config) {
    	gzStt_me = this;
    	gzStt_me.imodule = config.imodule;
    	gzStt_me.rsid = config.rsid;
    	gzStt_me.rsdtlid = config.rsdtlid;
    	gzStt_me.edit_pow = config.edit_pow;//如果没有修改权限，取数范围也不让设置了
    	gzStt_me.tableName = config.tableName;
    	gzStt_me.dragColumnTip = false;// 页面拖拽的提示
    	gzStt_me.init();
    },
    init: function() {
    	//first-into
        var json = {};
		json.rsid = gzStt_me.rsid;
		json.rsdtlid = gzStt_me.rsdtlid;
		json.showNumberOfPeople = false;//显示每月人数
		json.collect = false;//按层级汇总
		json.lay = 0;
		json.year = "";
		json.salaryid = "";
		json.fieldid = "";
		json.codeitemid = "";
		json.isRefresh = false;
		json.transType = 1;//1:生成台账报表数据 2:导出excel
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
        Rpc({functionId: 'GZ00000711', async: false, success: gzStt_me.getTableOK}, map);
    },
    getTableOK: function (form, action) {
        var result = Ext.decode(form.responseText);
        var returnStr = result.returnStr;
        if(returnStr.return_code == "fail") {
        	Ext.showAlert(returnStr.return_msg);
        	return;
        }else {
	        var config = result.returnStr.return_data.dataTableConfig;
	        //4个需要显示的下拉框集合
	        var yearList = result.returnStr.return_data.yearList;
	        var salarySetList = result.returnStr.return_data.salarySetList;
	        var fieldList = result.returnStr.return_data.fieldList;
	        var codeItemList = result.returnStr.return_data.codeItemList;
	        gzStt_me.levelSum_list = result.returnStr.return_data.levelSum_list;//层级汇总
	        
	        //页面显示的
	        gzStt_me.not_enc_rsid = result.returnStr.return_data.not_enc_rsid;
	        gzStt_me.not_enc_rsdtlid = result.returnStr.return_data.not_enc_rsdtlid;
	        gzStt_me.year = result.returnStr.return_data.year;//年
	        gzStt_me.salaryid = result.returnStr.return_data.salaryid;
	        gzStt_me.fieldid = result.returnStr.return_data.fieldid;//分析项
	        gzStt_me.codeitemid = result.returnStr.return_data.codeitemid;//分类项
	        gzStt_me.showNumberOfPeople = result.returnStr.return_data.showNumberOfPeople;//是否显示每月人数
	        gzStt_me.collect = result.returnStr.return_data.collect;//是否按层级汇总 
	        gzStt_me.lay = result.returnStr.return_data.lay;//层级值
			var obj = Ext.decode(config);
			gzStt_me.gzSttObj = new BuildTableObj(obj);
			gzStt_me.addDataListGridListens(gzStt_me.gzSttObj);
			//创建store
			gzStt_me.yearListStore = Ext.create('Ext.data.Store', {//年份
	            fields: ['name', 'id'],
	            data: yearList
	        });
	
			gzStt_me.salarySetListStore = Ext.create('Ext.data.Store', {//类别名称
	            fields: ['cname', 'salaryid'],
	            data: salarySetList
	        });
	
			gzStt_me.fieldidListStore = Ext.create('Ext.data.Store', {//分析项
	            fields: ['name', 'id'],
	            data: fieldList
	        });
			
			gzStt_me.codeitemidListStore = Ext.create('Ext.data.Store', {//分类项
	            fields: ['name', 'id'],
	            data: codeItemList
	        });
			
			gzStt_me.layStore = Ext.create('Ext.data.Store', {
			    fields: ['name', 'id'],
			    data : gzStt_me.levelSum_list
			});
			//创建toolbar，展示
			gzStt_me.createToolBar();
			
			if(gzStt_me.year == '' && gzStt_me.fieldid == '' && gzStt_me.codeitemid == ''){
				//延迟打开
				setTimeout(function(){
					gzStt_me.setRange()
	            },300);
	        }
        }
    },
    createToolBar: function() {
    	var setRangeBut = Ext.widget("button", {
    		text: gz.label.analysisdata.setrange,//设置取数范围
    		height: 24,
    		padding: '0 0 0 2',
    		hidden: !gzStt_me.edit_pow,
    	    handler: function() {
    	    	gzStt_me.setRange();
    	    }
        });
    	
    	//年份
    	var combo_year = gzStt_me.createComBox('yearListId', 100, gzStt_me.yearListStore, 'name', 'id', "年", 'right', 10, gzStt_me.year, true);
    	//类别
    	var combo_salaryid = gzStt_me.createComBox('salarysetId', 200, gzStt_me.salarySetListStore, 'cname', 'salaryid', "类别", 'left', 
    												30, gzStt_me.salaryid, false);
    	//分析项目
        var combo_field = gzStt_me.createComBox('fieldListId', 230, gzStt_me.fieldidListStore, 'name', 'id', "分析项目", 'left', 50, gzStt_me.fieldid, false);
        //分类
        var combo_codeitem = gzStt_me.createComBox('codeItemListId', 200, gzStt_me.codeitemidListStore, 'name', 'id', "分类", 'left', 30, 
        											gzStt_me.codeitemid, false);
        
    	//显示每月人数
        var check_showNumberOfPeople = Ext.widget("checkboxfield", {
            boxLabel: gz.label.analysisdata.showMonthCount,//显示每月人数
            labelAlign: "right",
            name: 'showNumberOfPeopleName',
            width: 90,
            checked: gzStt_me.showNumberOfPeople,
            margin: '0 0 0 4',
            listeners: {
            	change: function (combo, records) {
	            	gzStt_me.reload("showNumberOfPeopleName", combo.value);
	            }
	        }
        });
        
        //按层级汇总
        var check_collect = Ext.widget("checkboxfield", {
            boxLabel: gz.label.analysisdata.levelSummary,//按层级汇总"
            labelAlign: "right",
            id: 'collectName',
            name: 'collectName',
            width: 80,
            checked: gzStt_me.collect,
            hidden:gzStt_me.levelSum_list.length <= 1?true : false,
            margin: '0 0 0 4',
            listeners: {
            	change: function (combo, records) {
            		gzStt_me.collect = combo.value;
	            	Ext.getCmp("layId").setHidden(!gzStt_me.collect);
	            	gzStt_me.reload("", false);
	            },
	            afterrender: function() {
	            	if(gzStt_me.levelSum_list.length <= 1) {
	            		this.hidden = true;
	        			Ext.getCmp("layId").setHidden(true);
	        			//ie下disabled无效问题
	        			//Ext.getDom('collectName-displayEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
	        		}
	            }
	        }
        });
        
        //按层级汇总-级数
        var combo_lay = gzStt_me.createComBox('layId', 70, gzStt_me.layStore, 'name', 'id', "", '', 5, gzStt_me.lay, true);
        
        gzStt_me.toolbar = Ext.create('Ext.toolbar.Toolbar', {
            border: 0,
            dock: 'top',
            items: [{
            	xtype: 'button',
            	text: gz.label.analysisdata.navigation,//功能导航
            	height: 24,
            	style: 'margin-right:5px;',
            	menu:{
            		items:[{
            			text: gz.label.outExcel,//导出excel
            			icon:"/images/export.gif",
            			handler:function(){
            				Ext.MessageBox.wait(common.msg.exporting+"...", common.msg.wait);
            				gzStt_me.export_excel();
            			}
            		},
	                {
	                    text: gz.label.analysisdata.pagesetting, //'页面设置'
	                    icon: '/images/img_o.gif',
	                    handler: function () {
	                    	gzStt_me.showpagesetting();
	                    }
	                }]
            	}
            },'-',setRangeBut,combo_year,combo_salaryid,combo_field,check_showNumberOfPeople,combo_codeitem,check_collect,combo_lay]
        });
        
        gzStt_me.gzSttObj.insertItem(gzStt_me.toolbar,0);
        gzStt_me.centerpanel = gzStt_me.gzSttObj.getMainPanel();
        gzStt_me.panel = Ext.create('Ext.container.Container',{
            border : 0,
            id: 'gzStructure_container',
            layout:'fit',  
            items: [
            	gzStt_me.centerpanel
            ]
        });
    },
    getMainView: function() {
    	return gzStt_me.panel;
    },
    createComBox: function(id, width, store, disField, vField, name, labelAlign, labelWidth,value, match_flag) {
    	return Ext.create('Ext.form.ComboBox', {
	        id: id,
	        width: width,
	        store: store,
	        displayField: disField,
	        valueField: vField,
	        editable: true,
	        //matchFieldWidth:match_flag,//长度超出了自适应
	        queryMode: 'local',
	        fieldLabel: name,//分析项目
	        labelAlign: labelAlign,
	        labelWidth: labelWidth,
	        hidden: "layId" == id?!gzStt_me.collect : false,//按层级汇总选项先隐藏
	        style: 'padding-left:' + ("layId" == id?'8':'20') + 'px;',
	        value: value,
	        listeners: {
	            select: function (combo, records) {
	            	gzStt_me.reload(id, combo.value);
	            }
	        }
    	});
    },
    
    reload: function(id, value){
    	//赋值，保证页面上永远是对的值
    	gzStt_me.changeValue(id, value);
    	
    	var json = {};
    	json.rsid = gzStt_me.rsid;
		json.rsdtlid = gzStt_me.rsdtlid;
		json.year = gzStt_me.year;
		json.salaryid = gzStt_me.salaryid;
		json.fieldid = gzStt_me.fieldid;
		json.codeitemid = gzStt_me.codeitemid;
		json.showNumberOfPeople = gzStt_me.showNumberOfPeople?gzStt_me.showNumberOfPeople:false;
		json.collect = gzStt_me.collect?gzStt_me.collect:false;
		json.lay = gzStt_me.lay?gzStt_me.lay:1;
		json.isRefresh = gzStt_me.isRefresh;
		json.transType = 1;//1:生成台账报表数据 2:导出excel
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'GZ00000711',async:false,success:gzStt_me.reloadStore},map);
    },
    reloadStore: function(form, action) {
    	var result = Ext.decode(form.responseText);
    	var isRefresh = result.returnStr.return_data.isRefresh;
    	if(isRefresh) {
    		Ext.getCmp("analysis_detail").close();
    		gzStt_me.getTableOK(form, action);
    		var mainView = gzStt_me.getMainView();
    		Ext.create("Ext.window.Window",{
    	        maximized:true,
    	        id: 'analysis_detail',
    	        layout:'fit',
    	        title:gz.label.analysisdata.orderDepartment+" --> "+gz.label.analysisdata.monthlywagecomposition,
    	        autoScroll:true,
    	        border:false,
    	        resizable :false,
    	        items:[mainView]
    	    }).show();
    	}else {
	        Ext.getCmp("gzStructure1_tablePanel").getStore().load();
    	}
    },
    changeValue: function(id, value) {
    	if(id == "codeItemListId" || id == "showNumberOfPeopleName" || id == "salarysetId" || id == "reload" || id == "yearListId") {//是否刷新整个页面，因为列要刷新
    		gzStt_me.isRefresh = true;
    	}else {
    		gzStt_me.isRefresh = false;
    	}
    	if(id == "yearListId") {
    		gzStt_me.year = value;
    	}else if(id == "salarysetId") {
    		gzStt_me.salaryid = value;
    	}else if(id == "fieldListId") {
    		gzStt_me.fieldid = value;
    	}else if(id == "codeItemListId") {
    		gzStt_me.codeitemid = value;
    	}else if(id == "layId") {
    		gzStt_me.lay = value;
    	}else if(id == "showNumberOfPeopleName") {
    		gzStt_me.showNumberOfPeople = value;
    	}
    },
    export_excel: function() {
    	var json = {};
    	json.rsid = gzStt_me.rsid;
		json.rsdtlid = gzStt_me.rsdtlid;
    	json.transType = 2;//1:生成台账报表数据 2:导出excel
    	json.tableName = gzStt_me.tableName;
    	var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
    	Rpc({functionId:'GZ00000711',async:false,success:function(form,action){
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
                imodule:gzStt_me.imodule,
                rsid:gzStt_me.not_enc_rsid,
                rsdtlid:gzStt_me.not_enc_rsdtlid,
                callBack:function(){
                    Ext.getCmp("setrangewin").close();
                    gzStt_me.reload('reload', '');
                }
            });
        OptAnalysisTable_me.setRange();
    },
    /**
     * 页面设置
     */
    showpagesetting: function() {
    	var json = {};
    	json.rsid = gzStt_me.rsid;
		json.rsdtlid = gzStt_me.rsdtlid;
		json.opt = "1";//显示每月人数
		json.transType = 3;//1:生成台账报表数据 2:导出excel
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
        Rpc({functionId : 'GZ00000711',success: function(form){
            var result = Ext.decode(form.responseText);
            Ext.create("EHR.exportPageSet.ExportPageSet",{
            	rsid:gzStt_me.rsid,
            	rsdtlid:gzStt_me.rsdtlid,
            	result:result,
            	callbackfn:'gzStt_me.savePageSet'
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
    	json.rsid = gzStt_me.rsid;
		json.rsdtlid = gzStt_me.rsdtlid;
		json.opt = "2";//显示每月人数
		json.transType = 3;//1:生成台账报表数据 2:导出excel
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(json));
        map.put("pagesetupValue",pagesetupValue);
        map.put("titleValue",titleValue);
        map.put("pageheadValue",pageheadValue);
        map.put("pagetailidValue",pagetailValue);
        map.put("textValueValue",textValueValue);
        Rpc({functionId : 'GZ00000711',success: function(form){
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
			json.rsid = gzStt_me.rsid;
			json.rsdtlid = gzStt_me.rsdtlid;
	    	json.codeitemid = dataIndex;
			json.submoduleid = "gzStructure";
			json.width = width+"";
			json.isshare = "0";//0 私有方案 1共有方案
			json.transType = 5;
			var map = new HashMap();
			map.put("jsonStr", JSON.stringify(json));
			
			Rpc({functionId:'GZ00000711',async:false,success:function(){},scope:gzStt_me},map);
		});
		grid.tablePanel.on('columnmove',function(ct, column, width, eOpts){
			gzStt_me.saveColumnMove(grid, column);
		});
		
		grid.tablePanel.on('columnlockmove',function(ct, column, width, eOpts){
			gzStt_me.saveColumnMove(grid, column);
		});
		
	},
    // 调整顺序
	saveColumnMove:function(grid, column) {
		// 为了解决每月人数的显示与不显示 ，列拖拽保存到栏目设置的问题，
		// 例：在没人数的情况下，调整一月和二月，需要做到让显示二月，二月人数，一月，一月人数这种的，
		// 但是如果显示了人数再自己乱调整顺序，这样就乱了，下次不显示人数，再调整把月份随便调整，后续程序处理起来非常困难，还容易出错
		// 经过协商这里拖拽暂时不支持了。如果第一次拖拽提示出来
		if(!gzStt_me.dragColumnTip) {
			Ext.showAlert(gz.label.analysisdata);
			gzStt_me.dragColumnTip = true;
		}
		/*var tablePanel = grid.tablePanel;
		var is_lock = column.isLocked()?'1':'0';
		var index = tablePanel.getColumnManager().getHeaderIndex(column);
		var nextcolumn = tablePanel.getColumnManager().getHeaderAtIndex(index+1);
		var nextid = "-1";
		if(nextcolumn && nextcolumn.dataIndex)
			nextid = nextcolumn.dataIndex;
		
		var json = {};
		json.rsid = gzStt_me.rsid,
		json.rsdtlid = gzStt_me.rsdtlid
        json.transType = "6";
        json.is_lock = is_lock;
        json.itemid = column.dataIndex;
        json.nextid = nextid;
        json.submoduleid = "gzStructure";
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(json));
        Rpc({
            functionId: 'GZ00000711', async: true, success: function (res) {
            	
            },
            scope: this
        }, map);*/
	}
})