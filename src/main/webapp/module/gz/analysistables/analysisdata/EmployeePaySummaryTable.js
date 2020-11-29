/**
 * 人员工资|保险汇总表
 * @author haosl
 */
Ext.define('AnalysisdataURL.EmployeePaySummaryTable', {
    requires: ['EHR.exportPageSet.ExportPageSet'],
    rsid: '',//报表种类编号(加密)
    rsid_dec:'',//报表种类编号(未加密)
    rsdtlid_dec:'',//报表编号(未加密)
    rsdtlid: '',//报表编号(加密)
    imodule:0,//0:薪资  1：保险
    tableName:'',//报表名称
    constructor: function (config) {
        employeePaySummaryTable_me = this;
        this.rsid = config.rsid;
        this.rsdtlid = config.rsdtlid;
        this.imodule=config.imodule;//0:薪资  1：保险
        this.tableName = config.tableName;
        this.edit_pow = config.edit_pow;//如果没有修改权限，取数范围也不让设置了
        this.yearList = new Array();
        this.itemList = new Array();
        this.hasSetRange = true;//是否设置过取数范围 默认是。
        //将建主页面容器
        if (!Ext.util.CSS.getRule('.totalBack')) {
            Ext.util.CSS.createStyleSheet(".totalBack{background-color:#efffde;}");
        }
    },
    /**
     * 返回 主页面
     */
    getMainView: function () {
       var me = this;
       return Ext.widget("container", {
            layout: 'fit',
            items: [
                this.getTablePanel()
            ],
            listeners: {
                //渲染表头组件部分
                'afterrender': function () {
                    me.getSearchView();
                }
            }
        })
    },
    /**
     * 生成查询组件
     */
    getSearchView: function () {
        var scope = this;
        var toolbar = Ext.getCmp("employeePaySummaryPif_toolbar");
        //创建薪资项目sotre
        Ext.create("Ext.data.Store", {
            fields: ["value", "name"],
            id: "objectiveStore",
            data: scope.itemList
        })
        var data = [];
        var year_ = new Array();
        for(var i in scope.yearList){
        	var year__ = scope.yearList[i];
        	if(Ext.Array.indexOf(year_, year__) == -1) {
        		data.push({value:year__});
                year_.push(year__);
        	}
            
        }
        var store = Ext.create("Ext.data.Store",{
            id:'yearStore',
            autoLoad :true,
            fields:['value'],
            data:data
        })
         var container = Ext.create("Ext.container.Container", {
            layout: 'hbox',
            items: [
                {
                    xtype: 'combo',
                    id: 'yearCombo',
                    width: 60,
                    height: 22,
                    margin:'0 0 0 5',
                    hideLabel: true,
                    displayField: 'value',
                    forceSelection: true,
                    queryMode: 'local',
                    editable: false,
                    store: store,
                    listeners: {
                        afterrender: function (combo) {
                            var date = new Date();
                            var year = date.getFullYear();
                            //下拉数据中，默认选中当前年度
                            if (scope.yearList.join(",").indexOf(year + "") > -1) {
                                combo.select(year);
                            } else {
                                combo.select(scope.yearList[0]);
                            }
                            combo.on("change", function (comb, year,oyear) {
                                var itemV = Ext.getCmp("itemCombo").getValue();
                                //oyear为空时证明是第一次进页面。
                                if (!Ext.isEmpty(oyear) && !Ext.isEmpty(itemV)) {
                                    //自动根据年份查询数据
                                    scope.searchByScheme(false);
                                }


                            })

                            if(!scope.hasSetRange && scope.edit_pow){
                                //延时打开设置取数范围的窗口。
                                setTimeout(function(){
                                    scope.setRange(scope.rsid_dec,scope.rsdtlid_dec)
                                },300);
                            }
                        }
                    }
                }, {
                    xtype: 'label',
                    margin: '4 10 0 3',
                    text: gz.label.year
                }, {
                    xtype: 'combo',
                    id: 'itemCombo',
                    width: 150,
                    height: 22,
                    queryMode: 'local',
                    store: Ext.data.StoreManager.lookup("objectiveStore"),
                    valueField: 'value',
                    displayField: 'name',
                    hideLabel: true,
                    forceSelection: true,
                    editable: false,
                    listeners: {
                        afterrender: function (combo) {
                                var store = combo.getStore();
                                if(store.getCount()>0){
                                    combo.select(store.getAt(0));
                                }
                            },
                        change:function(combo,itemid,oitemid){
                            if (!Ext.isEmpty(oitemid)) {
                                //自动根据年份查询数据
                                scope.searchByScheme(false);
                            }
                        }
                    }
                }
            ]
        })
        toolbar.insert(2,"-");
        toolbar.insert(3,container);
    },
    /**
     * 创建表格
     */
    getTablePanel: function () {
        var me = this;
        var map = new HashMap();
        var json = {};
        json.transType = '1';
        var param = {};
        param.rsid = me.rsid;
        param.rsdtlid = me.rsdtlid;
        param.imodule = me.imodule;
        json.param = param;
        map.put("jsonStr", Ext.encode(json));
        var tablePanel;
        Rpc({
            functionId: 'GZ00000710', async: false, success: function (form) {
                var res = Ext.decode(form.responseText);
                var returnObj = Ext.decode(res.returnStr);
                var data = returnObj.return_data;
                var obj = data.tableConfig;
                obj.openColumnQuery = true;//haosl 2017-07-31 方案查询可以查询自定义指标
                obj.beforeBuildComp = function (grid) {
                    grid.tableConfig.viewConfig.stripeRows=false;
                    grid.tableConfig.viewConfig.getRowClass=function (record) {//CSS class name to add to the row.获得一行的css样式 ?
                        if (isNaN(record.get('seq'))) {
                            return 'totalBack';
                        }
                    }
                }
                var tableObj = new BuildTableObj(obj);
                me.addDataListGridListens(tableObj);
                tablePanel = tableObj.getMainPanel();
                this.yearList = data.yearList;
                this.itemList = data.itemList;
                this.hasSetRange = data.hasSetRange;
                this.rsid_dec = data.rsid_dec;
                this.rsdtlid_dec = data.rsdtlid_dec;
            },
            scope: this
        }, map);

        return tablePanel;
    },
    /**
     * 调用页面设置控件
     */
    showpagesetting: function () {
        var scope = this;
        //页面数据填充
        var json = {};
        json.param = {
            rsid: scope.rsid,
            rsdtlid: scope.rsdtlid,
            //回显数据
            opt: "1"
        };
        json.transType = "4";
        var map = new HashMap();
        map.put("jsonStr", Ext.encode(json));	//opt等于"3"时为页面打开时数据的初始化

        Rpc({
            functionId: 'GZ00000710', success: function (form) {
                var result = Ext.decode(form.responseText);
                Ext.create("EHR.exportPageSet.ExportPageSet", {
                    rsid: scope.rsid,
                    rsdtlid: scope.rsdtlid,
                    result: result,
                    callbackfn: 'employeePaySummaryTable_me.savePageSet'
                });
            }
        }, map);
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
    savePageSet: function (pagesetupValue, titleValue, pageheadValue, pagetailValue, textValueValue, type) {
        var map = new HashMap();
        var json = {};
        json.param = {
            rsid: employeePaySummaryTable_me.rsid,
            rsdtlid: employeePaySummaryTable_me.rsdtlid,
            opt: "2"
        };
        json.transType = "4";
        map.put("jsonStr", Ext.encode(json));
        map.put("pagesetupValue", pagesetupValue);
        map.put("titleValue", titleValue);
        map.put("pageheadValue", pageheadValue);
        map.put("pagetailidValue", pagetailValue);
        map.put("textValueValue", textValueValue);
        Rpc({
            functionId: 'GZ00000710', success: function (form) {
                var res = Ext.decode(form.responseText);
                var returnObj = Ext.decode(res.returnStr);
                if(returnObj.return_code == "fail") {
                    Ext.showAlert(gz.label.zxdeclare.saveFail);
                }else{
                    //Ext.showAlert(gz.label.zxdeclare.saveSuccess);
                }
            }
        }, map);
    },
    /**
     * 导出Excel
     */
    exportExcel: function () {
        var json = {};
        var scope = this;
        json.param = {};
        json.transType = "2";
        json.param.rsdtlid = scope.rsdtlid;
        json.param.rsid = scope.rsid;
        json.param.tableName = scope.tableName;
        var year = Ext.getCmp("yearCombo").getValue();
        var salaryitem = Ext.getCmp("itemCombo").getValue();
        json.param.year = year;
        json.param.salaryitem = salaryitem;
        var map = new HashMap();
        map.put("jsonStr", Ext.encode(json));
        Rpc({
            functionId: 'GZ00000710', async: true, success: function (res) {
                var result = Ext.decode(res.responseText);
                var data = Ext.decode(result.returnStr);
                if (data.return_code == "success") {
                    var filename = data.return_data.fileName;
                    filename = getDecodeStr(filename);
                    window.location.target = "_blank";
                    window.location.href = "/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true";
                } else {
                    Ext.showAlert(data.return_msg);
                }
            },
            scope: this
        }, map);

    },
    /**
     * 根据查询条件，刷新页面数据
     * @isDefCheck true 刷新下拉项并重新选中
     *
     */
    searchByScheme: function (isDefCheck) {
        //清空查询条件
        var querybox = Ext.getCmp('employeePaySummaryPif_querybox');
        if(querybox){
            querybox.removeAllKeys();
        }
        var json = {};
        var scope = this;
        json.param = {};
        json.transType = "3";
        json.param.rsdtlid = scope.rsdtlid;
        json.param.rsid = scope.rsid;
        var year = Ext.getCmp("yearCombo").getValue();
        var salaryitem = Ext.getCmp("itemCombo").getValue();
        json.param.year = year ||"";
        json.param.salaryitem = salaryitem||"";
        var map = new HashMap();
        map.put("jsonStr", Ext.encode(json));
        Rpc({
            functionId: 'GZ00000710', async: true, success: function (form) {
                var res = Ext.decode(form.responseText);
                var returnObj = Ext.decode(res.returnStr);
                var data = returnObj.return_data;

                //是否需要刷新store 并重新选中下拉项
                if(isDefCheck){
                    //选中年度下拉
                    var yearStore = Ext.data.StoreManager.lookup("yearStore");
                    var datas = [];
                    //下拉数据中，默认选中当前年度
                    var yearCombo = Ext.getCmp("yearCombo");
                    if(data.yearList && data.yearList.length>0){
                        for(var i=0;i<data.yearList.length;i++){
                            datas.push({value:data.yearList[i]});
                        }
                        yearStore.loadData(datas);
                        var date = new Date();
                        var year = date.getFullYear();
                        if (data.yearList.join(",").indexOf(year + "") > -1) {
                            yearCombo.select(year);
                        } else {
                            yearCombo.select(data.yearList[0]);
                        }
                    }else{
                        yearStore.loadData(datas);
                        yearCombo.setValue(null);
                    }
                    var itemCombo = Ext.getCmp("itemCombo");
                    var objectiveStore = Ext.data.StoreManager.lookup("objectiveStore");
                    if(data.itemList && data.itemList.length>0) {
                        //下拉数据中，默认选中当前年度
                        objectiveStore.loadData(data.itemList);
                        //选中年度下拉
                        itemCombo.select(objectiveStore.getAt(0));
                    }else{
                        objectiveStore.loadData([]);
                        itemCombo.setValue(null);
                    }
                }


                var store = Ext.data.StoreManager.lookup('employeePaySummaryPif_dataStore');
                store.currentPage = 1;
                store.load();
            },
            scope: this
        }, map);

    },
    /**
     * 设置取数范围
     */
    setRange:function(rsid,rsdtlid){
        //将页面作为窗口展现出来
        var me = this;
        var panel = Ext.create("Analysistable.OptAnalysisTable",{
                opt:3,
                imodule:this.imodule,
                rsid:rsid,
                rsdtlid:rsdtlid,
                callBack:function(){
                    Ext.getCmp("setrangewin").close();
                    me.searchByScheme(true);
                }
            });
        OptAnalysisTable_me.setRange();
    },
    
    addDataListGridListens:function(grid){
    	var me = this;
		grid.tablePanel.on('columnresize',function(ct, column, width, eOpts){
			var dataIndex = column.dataIndex;//修改的列codeitemid
			var json = {};
			json.param = {
	            rsid: me.rsid,
	            rsdtlid: me.rsdtlid,
	            imodule:me.imodule
	        };
	    	json.codeitemid = dataIndex;
			json.submoduleid = "employeePaySummaryTable";
			json.width = width+"";
			json.isshare = "0";//0 私有方案 1共有方案
			json.transType = 5;
			var map = new HashMap();
			map.put("jsonStr", JSON.stringify(json));
			
			Rpc({functionId:'GZ00000710',async:false,success:function(){},scope:this},map);
		});
		grid.tablePanel.on('columnmove',function(ct, column, width, eOpts){
			employeePaySummaryTable_me.saveColumnMove(grid, column);
		});
		
		grid.tablePanel.on('columnlockmove',function(ct, column, width, eOpts){
			employeePaySummaryTable_me.saveColumnMove(grid, column);
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
		json.param = {
            rsid: employeePaySummaryTable_me.rsid,
            rsdtlid: employeePaySummaryTable_me.rsdtlid
        };
        json.transType = "6";
        json.is_lock = is_lock;
        json.itemid = column.dataIndex;
        json.nextid = nextid;
        json.submoduleid = "employeePaySummaryTable";
        var map = new HashMap();
        map.put("jsonStr", Ext.encode(json));
        Rpc({
            functionId: 'GZ00000710', async: true, success: function (res) {
            	
            },
            scope: this
        }, map);
	}
});