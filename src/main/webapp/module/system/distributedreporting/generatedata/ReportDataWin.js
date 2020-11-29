/**
 * 上报数据window
 * wangbs 2019年6月12日
 */
Ext.define('GenaratedataURL.ReportDataWin', {
    extend: 'Ext.window.Window',
    layout: 'fit',
    width: 400,
    height: 200,
    modal: true,//遮罩
    resizable: false,//禁止拉伸
    closable: true,//允许关闭按钮
    title: dr.gd.reportdata,//上报数据
    constructor: function () {
        ReportDataWin = this;
        this.callParent();
        this.init();
    },
    init: function () {
        var me = this;
        var map = new HashMap();
        map.put("operaType", "echoReportDataWinInfo");
        Rpc({functionId:'SYS0000003101', success: function (res) {
            var returnMap = Ext.decode(res.responseText);
            var winDataMap = returnMap.winDataMap;
            var mainPanel = me.getMainPanel(winDataMap);
            me.add(mainPanel);
        }},map);
    },
    /**
     * 创建主体panel
     * @param winDataMap 回显的数据
     * @returns {Ext.Panel}
     */
    getMainPanel: function (winDataMap) {
        var me = this;
        var sendTime = winDataMap.sendTime;//最近成功上报数据时间
        var pkgType = winDataMap.pkgType;//上级导出的方案中的 1：全量  2：增量

        var successDesc = "";
        if (sendTime) {
            var sendType = winDataMap.sendType;//上报方式
            var dataType = winDataMap.dataType;//上报数据类型  全量||增量

            var sendTypeDesc = dr.gd.manualreport;//默认手工上报
            if (sendType == 1) {
                sendTypeDesc = dr.gd.database;
            }else if(sendType == 2) {
                sendTypeDesc = dr.gd.ftp;
            }else if(sendType == 3) {
                sendTypeDesc = dr.gd.webservice;
            }

            var dataTypeDesc = dr.gd.fulldose;//默认全量
            if (dataType == 2) {//增量
                dataTypeDesc = dr.gd.increment;
            }
            dataTypeDesc = dataTypeDesc + dr.gd.data;//默认全量数据
            successDesc = sendTime + " " + sendTypeDesc + "," + dataTypeDesc;
        }

        var mainPanel = Ext.create("Ext.Panel", {
            itemId: 'mainPanel',
            border: false,
            defaults: {
                margin: '0 0 10 25'
            },
            items: [{
                xtype: 'panel',
                hidden: successDesc ? false : true,
                border: false,
                layout: 'hbox',
                items:[{
                    xtype: 'component',
                    html: dr.gd.recentsuccessreportdata //最近成功上报数据为：
                },{
                    xtype: 'component',
                    html:successDesc
                }]
            },{
                xtype: 'component',
                hidden: successDesc ? true : false,
                html: dr.gd.recentnotsuccessreportdata //最近没有成功上报的数据
            }, {
                xtype: 'component',
                html: dr.gd.selectgeneratedateway//请选择生成数据包方式
            }, {
                xtype: 'radio',//全量
                itemId: 'fulldoseRadio',
                checked: pkgType == 1 ? true : false,
                boxLabel: dr.gd.fulldose,
                listeners:{
                    change: function (owner,newValue) {
                        if(newValue) {//选择了全量 隐藏datetimeselector组件
                            me.query("#notShowPicker")[0].show();
                            me.query("#showPicker")[0].hide();
                        }
                    }
                }
            }, {
                xtype: "panel",//增量行
                border: false,
                layout: 'hbox',
                items: [{
                    xtype: 'radio',
                    checked: pkgType == 2 ? true : false,
                    boxLabel: dr.gd.increment,
                    listeners:{
                        change: function (owner,newValue) {
                            if(newValue) {
                                me.query("#notShowPicker")[0].hide();
                                me.query("#showPicker")[0].show();
                            }
                        }
                    }
                }, {
                    xtype: 'component',//增量数据起始时间
                    margin: '4 0 0 15',
                    html: dr.gd.incrementdatastartdate
                }, {
                    xtype: 'component',//选择增量时间
                    itemId: "showPicker",
                    hidden: pkgType == 1 ? true : false,
                    html: '<input style="width:177px;margin-top:2px" type="text" readonly="readonly" ' +
                        'inputname="increment_view" name="increment_view" id="increment" ' +
                        'onclick="ReportDataWin.incrementInputOnclick()" plugin="datetimeselector" ' +
                        'format="Y-m-d H:i" afterfunc="ReportDataWin.selectDateAfter" spaceselect="false"/>'
                },{
                    xtype: 'component',//选择增量时间
                    hidden: pkgType == 1 ? false : true ,
                    itemId: "notShowPicker",
                    html: '<input style="width:177px;margin-top:2px" type="text" readonly="readonly"/>'
                }]
            }],
            buttonAlign: 'center',
            buttons: [{
                text: dr.gd.ok,//确定
                handler: function () {
                    var schemeType = "1";//默认全量
                    var dataStartTime = "";
                    var fulldoseRadio = mainPanel.query("#fulldoseRadio")[0];
                    var fulldoseCheck = fulldoseRadio.checked;
                    if(!fulldoseCheck){//选择的不是全量
                        schemeType = "2";//置为变量
                        if (!ReportDataWin.incrementDate) {//有增量变动时间
                            Ext.Msg.alert(dr.gd.tip, dr.gd.selectincrementdate);
                            return;
                        }
                        dataStartTime = ReportDataWin.incrementDate;
                    }
                    Ext.MessageBox.show({
                        title: dr.gd.tip,
                        msg: dr.gd.loadingwaitting,// "正在上报，请稍后...";
                        progress: true,
                        width: 300,
                        wait: true,
                        waitConfig: {interval: 600}
                    });

                    var map = new HashMap();
                    map.put("dataStartTime", dataStartTime);
                    map.put("schemeType", schemeType);
                    Rpc({functionId: 'SYS0000003201', success: function (res) {
                        me.close();//关闭window
                        Ext.MessageBox.hide();
                        var result = Ext.decode(res.responseText);
                        if(result.flag == false){
                            Ext.MessageBox.alert(dr.gd.tip,dr.gd.createdatapackageerrormsg);// 生成数据包失败！
                            return;
                        }
                        //刷新tablebulider数据
                        var vo = new HashMap();
                        vo.put("operaType", "search");
                        vo.put("searchPlan", GenerateReportData.labelFlag);
                        Rpc({functionId: 'SYS0000003101', success: function (res) {
                            var returnData = Ext.decode(res.responseText);
                            if (returnData.return_code=="success") {
                                Ext.getCmp('generatedata_tablePanel').getStore().reload();
                            } else {
                                Ext.MessageBox.alert(dr.gd.tip, dr.gd.refreshselectdataerrormsg);// 数据查询失败，请联系管理员！
                            }
                        }},vo);
                    }}, map);
                }
            },{
                text: dr.gd.cancel,//取消
                handler: function () {
                    me.close();
                }
            }]
        });
        return mainPanel;
    },
    /**
     * 日期文本框点击事件，选择增量时有效
     */
    incrementInputOnclick: function () {
        var idList = ["increment"];
        setDateEleConnect(idList);
    },
    /**
     * 选择日期后的赋值到全局变量中
     * @param dateValue
     */
    selectDateAfter: function (dateValue) {
        ReportDataWin.incrementDate = dateValue;
    }
});

