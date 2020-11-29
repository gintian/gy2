/**
 * 人员工资(保险)项目统计表
 * @author haosl
 */
Ext.define('AnalysisdataURL.EmployeePayStatMuster', {
    requires: ['EHR.exportPageSet.ExportPageSet'],
    rsid: '',//报表种类编号(加密)
    rsdtlid: '',//报表编号(加密)
    imodule:0,//0:薪资  1：保险
    tableName:'',//报表名称
    constructor: function (config) {
        EmployeePayStatSuster_me = this;
        this.rsid = config.rsid;
        this.rsdtlid = config.rsdtlid;
        this.imodule = config.imodule;
        this.tableName = config.tableName;
        this.edit_pow = config.edit_pow;//如果没有修改权限，取数范围也不让设置了
        this.yearList = new Array();
        //将建主页面容器
        if (!Ext.util.CSS.getRule('.totalBack')) {
            Ext.util.CSS.createStyleSheet(".totalBack{background-color:#efffde;}");
        }
    },
    /**
     * viewport 主页面
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
            functionId: 'GZ00000709', async: false, success: function (form) {
                var res = Ext.decode(form.responseText);
                var returnObj = Ext.decode(res.returnStr);
                var data = returnObj.return_data;
                var obj = data.tableConfig
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
                if(!data.hasSetRange && scope.edit_pow){
                    Ext.showAlert(gz.label.please+gz.label.analysisdata.setrange+"！");
                }
            },
            scope: this
        }, map);

        return tablePanel;
    },
    /**
     * 生成查询组件
     */
    getSearchView: function () {
        var scope = this;
        var toolbar = Ext.getCmp("employeePayStatSusterPif_toolbar");
        if (toolbar) {
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
                        xtype: 'radiofield',
                        name: 'statisMethod',
                        id: 'statisMethod1',
                        margin:'0 0 0 5',
                        checked: true,
                        listeners: {
                            change: function (radio,nVal) {
                                if(nVal){
                                	scope.hiddenOrShow(true);
                                    var year = Ext.getCmp("yearCombo").getValue();
                                    if (!Ext.isEmpty(year)) {
                                        scope.searchByScheme();
                                    }
                                }else {
                                	scope.hiddenOrShow(false);
                                }
                            }
                        }
                    }, {
                        xtype: 'combo',
                        id: 'yearCombo',
                        width: 70,
                        hideLabel: true,
                        displayField: 'value',
                        forceSelection: true,
                        queryMode: 'local',
                        editable: false,
                        store: store,
                        listeners: {
                            afterrender: function (combo) {
                            	scope.hiddenOrShow(true);
                                var date = new Date();
                                var year = date.getFullYear();
                                //下拉数据中，默认选中当前年度
                                if (scope.yearList.join(",").indexOf(year + "") > -1) {
                                    combo.select(year);
                                } else {
                                    combo.select(scope.yearList[0]);
                                }
                                combo.on("change", function (comb, year,oyear) {
                                    var checked1 = Ext.getCmp("statisMethod1").checked;
                                    if (!checked1) {
                                        Ext.getCmp("statisMethod1").setValue(true);
                                    }
                                    //自动根据年份查询数据
                                    if (!Ext.isEmpty(year) && !Ext.isEmpty(oyear))
                                        scope.searchByScheme();

                                })
                            }
                        }
                    }, {
                        xtype: 'label',
                        margin: '4 10 0 3',
                        text: gz.label.year
                    }, {
                        xtype: 'radio',
                        name: 'statisMethod',
                        id: 'statisMethod2',
                        height: 20,
                        boxLabel: gz.label.analysisdata.statisticalinterval,
                        boxLabelAlign: 'after',
                        labelWidth: 50,
                        listeners: {
                            change: function (radio,nVal) {
                                if(nVal){
                                    var startD = Ext.getCmp("startD").getValue();
                                    var endD = Ext.getCmp("endD").getValue();
                                    if (!Ext.isEmpty(startD) || !Ext.isEmpty(endD)) {
                                        scope.searchByScheme();
                                    }
                                }
                            }
                        }
                    }, {
                        xtype: 'label',
                        id: 'from_id',
                        margin: '4 3 0 5',
                        text: gz.label.analysisdata.from
                    }, {
                        xtype: 'datetimefield',
                        id: 'startD',
                        format: 'Y-m-d',
                        editable:false,
                        width: 130,
                        listeners: {
                            change: function () {
                                var checked2 = Ext.getCmp("statisMethod2").checked;
                                if (!checked2) {
                                    Ext.getCmp("statisMethod2").setValue(true);
                                }
                                var startD = Ext.getCmp("startD").getValue();
                                var endD = Ext.getCmp("endD").getValue();
                                if (!Ext.isEmpty(startD) || !Ext.isEmpty(endD)) {
                                    scope.searchByScheme();
                                }

                            }
                        }
                    }, {
                        xtype: 'label',
                        margin: '4 3 0 3',
                        id: 'to_id',
                        text:gz.label.to
                    }, {
                        xtype: 'datetimefield',
                        id: 'endD',
                        format: 'Y-m-d',
                        editable:false,
                        width: 130,
                        margin:'0 10 0 0',
                        listeners: {
                            change: function () {
                                var startD = Ext.getCmp("startD").getValue();
                                var endD = Ext.getCmp("endD").getValue();
                                if (!Ext.isEmpty(startD) || !Ext.isEmpty(endD)) {
                                    scope.searchByScheme();
                                }
                            }
                        }
                    }
                ]
            })
            toolbar.insert(2,"-");
            toolbar.insert(3,container);
        }
        toolbar.insert(5,{
            xtype:'checkbox',
            margin:'0 0 0 20',
            id: 'isShowTotal',
            boxLabel: gz.label.analysisdata.showtotal,
            listeners: {
                change: function () {
                    scope.searchByScheme();
                },

            }
        });
    },
    /**
     * 根据查询条件，刷新页面数据
     * @method ="1" 时间段或年份查询
     */
    searchByScheme: function () {
        //清空查询条件
        var querybox = Ext.getCmp('employeePayStatSusterPif_querybox');
        if(querybox){
            querybox.removeAllKeys();
        }
        var json = {};
        var scope = this;
        var flag = Ext.getCmp("isShowTotal").getValue();
        json.param = {};
        //isShowTotal =1 显示合计行 =2 不显示合计行
        json.param.isShowTotal = flag ? "1" : "2";
        json.transType = "3";
        json.param.rsdtlid = scope.rsdtlid;
        json.param.rsid = scope.rsid;
        var value1 = Ext.getCmp("statisMethod1").checked;
        if (value1) {
            json.param.statisMethod = "1";
            var year = Ext.getCmp("yearCombo").getValue()||"";
            json.param.year = year + "";
        } else {
            json.param.statisMethod = "2";
            var startD = Ext.getCmp("startD").getValue();
            var endD = Ext.getCmp("endD").getValue();
            if(Ext.isEmpty(startD) && Ext.isEmpty(endD)){
                return;
            }
            json.param.starttime = startD;
            json.param.endtime = endD;
        }
        var map = new HashMap();
        map.put("jsonStr", Ext.encode(json));
        Rpc({
            functionId: 'GZ00000709', async: true, success: function (form) {
                var res = Ext.decode(form.responseText);
                var returnObj = Ext.decode(res.returnStr);
                var data = returnObj.return_data;
                if(data.yearList && data.yearList.length>0){
                    var datas = [];
                    for(var i=0;i<data.yearList.length;i++){
                        datas.push({value:data.yearList[i]});
                    }
                    //选中年度下拉
                    var yearStore = Ext.data.StoreManager.lookup("yearStore");
                    yearStore.loadData(datas);
                    var year;
                    if(data.year) {
                    	year = data.year;
                    }else {
                    	var date = new Date();
	                    year = date.getFullYear();
                    }
                    //下拉数据中，默认选中当前年度
                    var yearCombo = Ext.getCmp("yearCombo");
                    if (data.yearList.join(",").indexOf(year + "") > -1) {
                        yearCombo.select(year);
                    } else {
                        yearCombo.select(data.yearList[0]);
                    }
                }
                var store = Ext.data.StoreManager.lookup('employeePayStatSusterPif_dataStore');
                store.currentPage = 1;
                store.load();
            },
            scope: this
        }, map);

    },
    /**
     * 调用页面设置控件
     */
    showpagesetting: function (rsid, rsdtlid) {
        var scope = this;
        //页面数据填充
        var json = {};
        json.param = {
            rsid: EmployeePayStatSuster_me.rsid,
            rsdtlid: EmployeePayStatSuster_me.rsdtlid,
            imodule : scope.imodule,
            //回显数据
            opt: "1"
        };
        json.transType = "4";
        var map = new HashMap();
        map.put("jsonStr", Ext.encode(json));	//opt等于"3"时为页面打开时数据的初始化

        Rpc({
            functionId: 'GZ00000709', success: function (form) {
                var result = Ext.decode(form.responseText);
                Ext.create("EHR.exportPageSet.ExportPageSet", {
                    rsid: scope.rsid,
                    rsdtlid: scope.rsdtlid,
                    result: result,
                    callbackfn: 'EmployeePayStatSuster_me.savePageSet'
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
    	var scope = this;
        var map = new HashMap();
        var json = {};
        json.param = {
            rsid: EmployeePayStatSuster_me.rsid,
            rsdtlid: EmployeePayStatSuster_me.rsdtlid,
            imodule : scope.imodule,
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
            functionId: 'GZ00000709', success: function (form) {
                var res = Ext.decode(form.responseText);
                var returnObj = Ext.decode(res.returnStr);
                if(returnObj.return_code == "fail") {
                    Ext.showAlert(gz.label.zxdeclare.saveFail);
                }/*else{
                    Ext.showAlert(gz.label.zxdeclare.saveSuccess);
                }*/
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
        json.param.imodule = scope.imodule;
        var map = new HashMap();
        map.put("jsonStr", Ext.encode(json));
        Rpc({
            functionId: 'GZ00000709', async: true, success: function (res) {
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
     * 设置取数范围
     */
    setRange:function(rsid,rsdtlid){
        //将页面作为窗口展现出来
        var me = this;
        var panel = Ext.create("Analysistable.OptAnalysisTable",{
                opt:3,
                imodule:me.imodule,
                rsid:rsid,
                rsdtlid:rsdtlid,
                callBack:function(){
                    Ext.getCmp("setrangewin").close();
                    me.searchByScheme();
                }
            });
		OptAnalysisTable_me.setRange();
    },
    
    hiddenOrShow: function(flag) {
    	Ext.getCmp("from_id").setHidden(flag);
	    Ext.getCmp("to_id").setHidden(flag);
	    Ext.getCmp("startD").setHidden(flag);
	    Ext.getCmp("endD").setHidden(flag);
    },
    
    addDataListGridListens:function(grid){
    	var me = this;
		grid.tablePanel.on('columnresize',function(ct, column, width, eOpts){
			var dataIndex = column.dataIndex;//修改的列codeitemid
			var json = {};
			json.param = {
	            rsid: EmployeePayStatSuster_me.rsid,
	            rsdtlid: EmployeePayStatSuster_me.rsdtlid,
	            imodule:me.imodule
	        };
	    	json.codeitemid = dataIndex;
			json.submoduleid = "employeePayStatSuster";
			json.width = width+"";
			json.isshare = "0";//0 私有方案 1共有方案
			json.transType = 5;
			var map = new HashMap();
			map.put("jsonStr", JSON.stringify(json));
			
			Rpc({functionId:'GZ00000709',async:false,success:function(){},scope:this},map);
		});
		grid.tablePanel.on('columnmove',function(ct, column, width, eOpts){
			EmployeePayStatSuster_me.saveColumnMove(grid, column);
		});
		
		grid.tablePanel.on('columnlockmove',function(ct, column, width, eOpts){
			EmployeePayStatSuster_me.saveColumnMove(grid, column);
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
            rsid: EmployeePayStatSuster_me.rsid,
            rsdtlid: EmployeePayStatSuster_me.rsdtlid
        };
        json.transType = "6";
        json.is_lock = is_lock;
        json.itemid = column.dataIndex;
        json.nextid = nextid;
        json.submoduleid = "employeePayStatSuster";
        var map = new HashMap();
        map.put("jsonStr", Ext.encode(json));
        Rpc({
            functionId: 'GZ00000709', async: true, success: function (res) {
            	
            },
            scope: this
        }, map);
	}
});