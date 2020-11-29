/**
 * 薪资分析 各月工资构成分析表
 * sunjian 2019-8-9
 */

Ext.define('AnalysisdataURL.GzItemSummaryTable', {
    constructor: function (config) {
    	gzIst_me = this;
    	gzIst_me.appointtime = true;//默认按照指定日期查找
    	gzIst_me.fromYear = new Date();//默认显示的日期
    	gzIst_me.endYear = new Date();
    	gzIst_me.rsid = config.rsid;
    	gzIst_me.rsdtlid = config.rsdtlid;
    	gzIst_me.imodule = config.imodule;
    	gzIst_me.edit_pow = config.edit_pow;//如果没有修改权限，取数范围也不让设置了
    	gzIst_me.tableName = config.tableName;
    	gzIst_me.init(config);
    },
    init: function(config) {
    	//first-into
        var json = {};
		json.rsid = gzIst_me.rsid;
		json.rsdtlid = gzIst_me.rsdtlid;
		json.nbases = "Usr,";//显示每月人数
		json.month = "";
		json.year = "";
		json.fromYear = "";
		json.endYear = "";
		json.appointtime = gzIst_me.appointtime;
		json.transType = 1;//1:生成台账报表数据 2:导出excel
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
        Rpc({functionId: 'GZ00000713', async: false, success: gzIst_me.getTableOK}, map);
    },
    getTableOK: function (form, action) {
        var result = Ext.decode(form.responseText);
        var config = result.returnStr.return_data.dataTableConfig;
        //1个需要显示的下拉框集合
        var yearList = result.returnStr.return_data.yearList;
        
        //页面显示的
        gzIst_me.not_enc_rsid = result.returnStr.return_data.not_enc_rsid;
        gzIst_me.not_enc_rsdtlid = result.returnStr.return_data.not_enc_rsdtlid;
        gzIst_me.year = result.returnStr.return_data.year;//年
        gzIst_me.month = result.returnStr.return_data.month;
        gzIst_me.dateJson = result.returnStr.return_data.dateJson;
        
		var obj = Ext.decode(config);
		gzIst_me.gzSttObj = new BuildTableObj(obj);
		gzIst_me.addDataListGridListens(gzIst_me.gzSttObj);
		//创建store
		gzIst_me.yearListStore = Ext.create('Ext.data.Store', {//年份
            fields: ['name', 'id'],
            data: yearList
        });

		gzIst_me.monthListStore = Ext.create('Ext.data.Store', {//月
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

		//创建toolbar，展示
		gzIst_me.createToolBar();
    },
    
    createToolBar: function() {
    	var setRangeBut = Ext.widget("button", {
    		text: gz.label.analysisdata.setrange,
    		height: 24,
    		padding: '0 0 0 2',
    		hidden: !gzIst_me.edit_pow,
    	    handler: function() {
    	    	gzIst_me.setRange();
    	    }
        });
    	
    	var app_radio = Ext.widget("radiofield", {
        	name : 'statistics',
        	style : 'padding-left:20px;',
        	checked: gzIst_me.appointtime,
	        listeners: {
	            change: function (combo, records) {
	            	gzIst_me.reload("statistics", combo.value);
	            }
	        }
        });
    	
    	
    	//年份
    	var combo_year = gzIst_me.createComBox('yearId', 100, gzIst_me.yearListStore, 'name', 'id', "年", 'right', 10, gzIst_me.year, true);
    	//月份
    	var combo_month = gzIst_me.createComBox('monthId', 120, gzIst_me.monthListStore, 'name', 'id', "月份", 'left', 
    												25, gzIst_me.month, false);
        
    	//
        var sta_radio = Ext.widget("radiofield", {
        	name : 'statistics',
        	checked: !gzIst_me.appointtime,
        	label: gz.label.analysisdata.statisticalinterval,
            style : 'padding-left:20px;',
            labelAlign: 'right'
        });
        
        var sta_label = Ext.widget("label", {
        	text: gz.label.analysisdata.statisticalinterval
        });
        
        var datePanel = Ext.create('Ext.panel.Panel', {
			width: '100%',
			id: 'date_pan',
			layout:'hbox',
			border:false,
			padding:'0 0 0 5',
			hidden: gzIst_me.appointtime,
			items: [{
		        xtype: 'label',
		        text: gz.label.analysisdata.from,
		        padding:'3 2 0 5',
		    },{
				xtype: 'datefield',
				anchor: '100%',
				editable: false,
	            name: 'fromYear',
	            id: 'fromYear',
	            width: 110,
	            format: 'Y-m-d',
	            value: gzIst_me.fromYear,
	            listeners: {
		            change: function (combo, records) {
		            	gzIst_me.reload("fromYear", combo.value);
		            }
		        }
		    },{
		        xtype: 'label',
		        text: gz.label.to,
		        padding:'3 2 0 5',
		    },{
		    	xtype: 'datefield',
		    	anchor: '100%',
		    	editable: false,
	            name: 'endYear',
	            id: 'endYear',
	            width: 110,
	            format: 'Y-m-d',
	            value: gzIst_me.endYear,
	            listeners: {
		            change: function (combo, records) {
		            	gzIst_me.reload("endYear", combo.value);
		            }
		        }
		    }]
		});
        
        var toolbar = Ext.create('Ext.toolbar.Toolbar', {
            border: 0,
            id: "toolbar",
            dock: 'top',
            height: 35,
            items: [{
            	xtype: 'button',
            	text: gz.label.analysisdata.navigation,//功能导航
            	height: 24,
            	menu:{
            		items:[{
            			text: gz.label.outExcel,//导出excel
            			icon:"/images/export.gif",
            			handler:function(){
            				Ext.MessageBox.wait(common.msg.exporting+"...", common.msg.wait);
            				gzIst_me.export_excel();
            			}
            		},
	                {
            			text: gz.label.analysisdata.pagesetting, //'页面设置'
	                    icon: '/images/img_o.gif',
	                    handler: function () {
	                    	gzIst_me.showpagesetting();
	                    }
	                }]
            	}
            },'-',setRangeBut,app_radio,{
				xtype:"label",
				width:80,
				style:"margin:0 10px 0 10px;",
				html:"<a href='javascript:gzIst_me.selectMonth()' id='date' >" + gzIst_me.year + gz.label.year + gzIst_me.month + gz.label.month + "</a>"
							+ "<img src='/workplan/image/jiantou.png' id='dateImg' style='cursor:pointer;padding-left: 5px;' onclick='gzIst_me.selectMonth();'/> "
			},"-",sta_radio,sta_label,datePanel]
        });
    	gzIst_me.gzSttObj.insertItem(toolbar, 0);
    	gzIst_me.centerpanel = gzIst_me.gzSttObj.getMainPanel();
    	gzIst_me.panel = Ext.create('Ext.container.Container',{
            border : 0,
            layout:'fit',  
            items: [
            	gzIst_me.centerpanel
            ]
        });
    },
    
    selectMonth: function() {
    	var win = Ext.getCmp('win');
		if(win) {
			// win重新创建，不会影响性能，防止通过设置取数范围，导致这里不对
			win.destroy();
		}
		var SetItemGloble = Ext.create("EHR.attendanceMonth.AttendanceMonthComp",{
			totalData:gzIst_me.dateJson,
			border: false,
			currentYear: gzIst_me.year,
			currentMonth: gzIst_me.month,
			onMonthSelected: function (value) {
				document.getElementById("date").innerText = value.year + gz.label.year + value.desc;
				gzIst_me.year = value.year;
				gzIst_me.month = value.monthOrder + "";
				gzIst_me.reload("", "");
				win.hide();
			}
		});
		
		win = Ext.create('Ext.window.Window', {
			id: 'win',
			header: false,
			x: Ext.get("date").getX() - 50,
			y: Ext.get("date").getY() + 15,
			width: 265,
			height: 170,
			items: [SetItemGloble],
			listeners: {
				render: function () {
		            document.getElementById("win").onmouseout = function () {
		                var s = event.toElement || event.relatedTarget;
		                if (s == undefined || !this.contains(s))
		                	win.hide();
		            };
		        }
			}
		});
		
		win.show();
    },
    
    getMainView: function() {
    	return gzIst_me.panel;
    },
    
    createComBox: function(id, width, store, disField, vField, name, labelAlign, labelWidth,value, match_flag) {
    	return Ext.create('Ext.form.ComboBox', {
	        id: id,
	        width: width,
	        store: store,
	        displayField: disField,
	        valueField: vField,
	        editable: true,
	        matchFieldWidth:match_flag,//长度超出了自适应
	        queryMode: 'local',
	        fieldLabel: name,//分析项目
	        labelAlign: labelAlign,
	        labelWidth: labelWidth,
	        hidden: "layerId" == id?!gzIst_me.collect : false,//按层级汇总选项先隐藏
	        style: "yearId" == id?"":'padding-left:20px;',
	        value: value,
	        listeners: {
	            select: function (combo, records) {
	            	gzIst_me.reload(id, combo.value);
	            }
	        }
    	});
    },
    
    reload: function(id, value){
    	//赋值，保证页面上永远是对的值
    	var is_refresh = gzIst_me.changeValue(id, value);
    	if(is_refresh) {
	    	var json = {};
	    	json.rsid = gzIst_me.rsid;
			json.rsdtlid = gzIst_me.rsdtlid;
			json.nbases = "Usr,";//显示每月人数
			json.month = gzIst_me.month;
			json.year = gzIst_me.year;
			json.fromYear = gzIst_me.fromYear.getFullYear()+'-'+(gzIst_me.fromYear.getMonth()+1)+'-'+gzIst_me.fromYear.getDate();
			json.endYear = gzIst_me.endYear.getFullYear()+'-'+(gzIst_me.endYear.getMonth()+1)+'-'+gzIst_me.endYear.getDate();
			json.appointtime = gzIst_me.appointtime,
			json.transType = 1;//1:生成台账报表数据 2:导出excel
			var map = new HashMap();
			map.put("jsonStr", JSON.stringify(json));
		    Rpc({functionId:'GZ00000713',async:false,success:gzIst_me.reloadStore},map);
    	}
    	gzIst_me.closeWinYearMonth();
    },
    reloadStore: function(form, action) {
    	Ext.getCmp("GzItemSummary1_" + gzIst_me.rsdtlid + "_tablePanel").getStore().load();
    	
    	var result = Ext.decode(form.responseText);
    	gzIst_me.yearList = result.returnStr.return_data.yearList;//代码值
    	gzIst_me.year = result.returnStr.return_data.year;//代码值
    	gzIst_me.month = result.returnStr.return_data.month;//月
    	gzIst_me.dateJson = result.returnStr.return_data.dateJson;//日期控件
    	gzIst_me.yearListStore.setData(gzIst_me.yearList);
        Ext.getCmp("yearId").setValue(gzIst_me.year);//代码值
        
        Ext.getDom("date").innerText = gzIst_me.year + gz.label.year + gzIst_me.month + gz.label.month;
        gzIst_me.closeWinYearMonth();
    },
    changeValue: function(id, value) {
    	if(id == "yearId") {
    		gzIst_me.year = value;
    	}else if(id == "monthId") {
    		gzIst_me.month = value;
    	}else if(id == "statistics") {
    		gzIst_me.appointtime = value;//是否按照指定日期查找
    		Ext.getCmp("date_pan").setHidden(gzIst_me.appointtime);
    	}else if(id == "fromYear") {
    		gzIst_me.fromYear = value;
    	}else if(id == "endYear") {
    		gzIst_me.endYear = value;
    	}
    	//选择了按照统计区间，对应的选择年月其实无效
    	if(id != "statistics" && !gzIst_me.appointtime && (id == "yearId" || id == "monthId")) {
    		return false;
    	}
    	return true;
    },
    export_excel: function() {
    	var json = {};
    	json.transType = 2;//1:生成台账报表数据 2:导出excel
    	json.rsid = gzIst_me.rsid;
		json.rsdtlid = gzIst_me.rsdtlid;
		json.tableName = gzIst_me.tableName;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
    	Rpc({functionId:'GZ00000713',async:false,success:function(form,action){
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
                imodule:gzIst_me.imodule,
                rsid:gzIst_me.not_enc_rsid,
                rsdtlid:gzIst_me.not_enc_rsdtlid,
                callBack:function(){
                    Ext.getCmp("setrangewin").close();
                    gzIst_me.reload('', '');
                }
            });
        OptAnalysisTable_me.setRange();
    },
    /**
     * 页面设置
     */
    showpagesetting: function() {
    	var json = {};
    	json.rsid = gzIst_me.rsid;
		json.rsdtlid = gzIst_me.rsdtlid;
		json.opt = "1";//显示每月人数
		json.transType = 3;//1:生成台账报表数据 2:导出excel
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
        Rpc({functionId : 'GZ00000713',success: function(form){
            var result = Ext.decode(form.responseText);
            Ext.create("EHR.exportPageSet.ExportPageSet",{
            	rsid:gzIst_me.rsid,
            	rsdtlid:gzIst_me.rsdtlid,
            	result:result,
            	callbackfn:'gzIst_me.savePageSet'
            });
        }}, map);
    },
    // 点击其他按钮的时候关闭下拉的时间框
    closeWinYearMonth:function() {
    	var win = Ext.getCmp('win');
		if(win) {
			win.hide();
		}
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
    	json.rsid = gzIst_me.rsid;
		json.rsdtlid = gzIst_me.rsdtlid;
		json.opt = "2";//显示每月人数
		json.transType = 3;//1:生成台账报表数据 2:导出excel
		json.pagesetupValue = pagesetupValue;
		json.titleValue = titleValue;
		json.pageheadValue = pageheadValue;
		json.pagetailValue = pagetailValue;
		json.textValueValue = textValueValue;
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(json));
        map.put("pagesetupValue",pagesetupValue);
        map.put("titleValue",titleValue);
        map.put("pageheadValue",pageheadValue);
        map.put("pagetailidValue",pagetailValue);
        map.put("textValueValue",textValueValue);
        Rpc({functionId : 'GZ00000713',success: function(form){
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
			json.rsid = gzIst_me.rsid;
			json.rsdtlid = gzIst_me.rsdtlid;
	    	json.codeitemid = dataIndex;
			json.submoduleid = "GzItemSummary_" + gzIst_me.rsdtlid;
			json.width = width+"";
			json.isshare = "0";//0 私有方案 1共有方案
			json.transType = 5;
			var map = new HashMap();
			map.put("jsonStr", JSON.stringify(json));
			
			Rpc({functionId:'GZ00000713',async:false,success:function(){},scope:gzIst_me},map);
		});
		grid.tablePanel.on('columnmove',function(ct, column, width, eOpts){
			gzIst_me.saveColumnMove(grid, column);
		});
		
		grid.tablePanel.on('columnlockmove',function(ct, column, width, eOpts){
			gzIst_me.saveColumnMove(grid, column);
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
		json.rsid = gzIst_me.rsid,
		json.rsdtlid = gzIst_me.rsdtlid
        json.transType = "6";
        json.is_lock = is_lock;
        json.itemid = column.dataIndex;
        json.nextid = nextid;
        json.submoduleid = "GzItemSummary_" + gzIst_me.rsdtlid;
        var map = new HashMap();
        map.put("jsonStr", JSON.stringify(json));
        Rpc({
            functionId: 'GZ00000713', async: true, success: function (res) {
            	
            },
            scope: this
        }, map);
	}
})