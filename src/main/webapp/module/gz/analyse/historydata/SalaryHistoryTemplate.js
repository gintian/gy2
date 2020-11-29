/**
 * 薪资类别页面
 * 2020-1-13
 * liuyd
 */
Ext.define('GzAnalyse.historydata.SalaryHistoryTemplate', {
    requires: ['SalaryReport.SalaryReport'],
    extend: 'Ext.window.Window',
    layout: 'fit',
    width: 510,
    height: 500,
    modal: true,
    resizable: false,
    initComponent: function () {
        SalaryTemplate = this;
        SalaryTemplate.callParent();
        SalaryTemplate.loadData();
    },
    loadData: function () {
        var map = new HashMap();
        Rpc({
            functionId: 'GZ00001308', success: function (res) {
                var resultData = Ext.decode(res.responseText);
                var return_code = resultData.returnStr.return_code;
                if (return_code === 'success') {
                    var tableConfig = resultData.returnStr.return_data.getTableConfig;
                    var obj = Ext.decode(tableConfig);
                    //加载表格之前做列的处理和布局修改
                    obj.beforeBuildComp = function (grid) {
                        var columns = grid.tableConfig.columns;
                        columns[1].border = 0;
                        for (var i = 0; i < columns.length; i++) {
                            columns[i].menuDisabled = true;
                            columns[i].sortable = false;
                            columns[i].draggable = false;
                            columns[i].resizable = false;
                        }
                    };
                    SalaryTemplate.tableObj = new BuildTableObj(obj);

                    var mainPanel = Ext.create('Ext.Panel', {
                        layout: 'fit',
                        border: false,
                        buttonAlign: 'center',
                        buttons: [{
                            text: gz.historyData.confirm,
                            handler: SalaryTemplate.switchSalaryTemplate
                        }, {
                            text: gz.historyData.cancel,
                            handler: function () {
                                SalaryTemplate.close();
                            }
                        }]
                    });
                    mainPanel.add(SalaryTemplate.tableObj.getMainPanel());
                    SalaryTemplate.add(mainPanel);
                    SalaryTemplate.query('querybox')[0].setQueryBoxWidth(478);
                    //火狐放大到150%缺线
                    Ext.getCmp("switchSalaryTemplate_pagingtool").setHeight(32);
                } else {
                    Ext.Msg.alert(gz.historyData.tip, eval(resultData.returnStr.return_msg));
                }
            }, scope: this
        }, map);
    },
    /**
     * gridpanel的行双击事件
     */
    rowdbclick: function (value, record) {
        SalaryTemplate.passParameterSalaryId(record.data);
        SalaryTemplate.close();
    },
    switchSalaryTemplate: function () {
        var select = Ext.getCmp('switchSalaryTemplate_tablePanel').getSelectionModel().getSelection();
        if (select.length === 0) {
            //请选中薪资类别！
            Ext.Msg.alert(gz.historyData.tip, gz.historyData.msg.selectSalaryType);
            return;
        }
        SalaryTemplate.passParameterSalaryId(select[0].data);
        SalaryTemplate.close();
    },
    //去除薪资类别列右边框
    hideRightBorder: function (value, metaData) {
        metaData.tdStyle = 'border-right:0px !important';
        return value;
    },
    //切换薪资类别向主页面传salaryid
    passParameterSalaryId : function (recordData) {
        var map = new HashMap();
        map.put('salaryId',recordData.salaryidjiami);
        map.put('transType',SalaryHistoryData.transType);
        Rpc({
            functionId: 'GZ00001309', success: function (res) {
                var result = Ext.decode(res.responseText);
                var return_code = result.return_code;
                if (return_code === "success") {
                    var conditions = result.return_data.tableConfig;
                    var obj = Ext.decode(conditions);
                    SalaryHistoryData.salaryId = result.return_data.salaryId_encrypt;
                    SalaryHistoryData.remove(Ext.getCmp('SalaryHistoryData_mainPanel'));
                    obj.openColumnQuery = true;
                    SalaryHistoryData.tableObj = new BuildTableObj(obj);
                    SalaryHistoryData.count = result.count;//次数
                    SalaryHistoryData.countList = result.countList;//次数list
                    SalaryHistoryData.appdate = result.appdate;//业务日期
                    if (SalaryHistoryData.appdate) {
                        SalaryHistoryData.selectYear = SalaryHistoryData.appdate.substring(0, 4) + gz.label.year;
                        SalaryHistoryData.selectMonthOrder = parseInt(SalaryHistoryData.appdate.substring(SalaryHistoryData.appdate.indexOf("-") + 1, SalaryHistoryData.appdate.lastIndexOf("-")));
                    }
                    SalaryHistoryData.tableObj.insertItem(SalaryHistoryData.getTopMenu(SalaryHistoryData.countList),0);
                    SalaryHistoryData.add(SalaryHistoryData.tableObj.getMainPanel());

                    SalaryHistoryData.appdate_encrypt = result.return_data.appdate_encrypt;
                    SalaryHistoryData.count_encrypt = result.return_data.count_encrypt;
                    SalaryHistoryData.dateList = result.return_data.dateList;//切换日期组件数据
                    SalaryHistoryData.reportList = result.return_data.reportList;//工资报表

                    SalaryHistoryData.tablesubModuleId = result.tablesubModuleId;
                    SalaryHistoryData.count_encrypt = result.return_data.count_encrypt;
                    SalaryHistoryData.commonreportlist = SalaryHistoryData.reportList;//薪资常用报表数据
                    SalaryHistoryData.querybox(result);

                    SalaryHistoryData.salaryName = recordData.cname;
                    SalaryHistoryData.query("#salaryTypeName")[0].setHtml(recordData.cname);
                    //隐藏tablebuilder组件锁列区域的滚动条
                    if (Ext.getDom("SalaryHistoryData_tablePanel-locked-body")) {
                        Ext.getDom("SalaryHistoryData_tablePanel-locked-body").childNodes[0].style['overflow-x'] = "hidden";
                    }
                } else {
                    //"薪资历史数据请求失败！"
                    Ext.Msg.alert(gz.historyData.tip, gz.historyData.msg.dataError);
                }
            }, scope: this
        }, map);
    }
});
