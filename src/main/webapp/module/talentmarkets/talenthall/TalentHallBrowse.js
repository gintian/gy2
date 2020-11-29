Ext.define('Talentmarkets.talenthall.TalentHallBrowse', {
    extend: 'Ext.panel.Panel',
    layout: 'fit',
    initComponent: function () {
        this.callParent();
        this.addTools();
        this.init();
    },
    /**
     * 添加工具栏按钮
     */
    addTools: function () {
        var me = this;
        me.tools = [{
            type: 'close',
            handler: function () {
                TalentHallMain.remove(me);
                me.destroy();
                TalentHallMain.query("#mainPanel")[0].show();
            }
        }];
    },
    init: function () {
        var talentHallBrowse = this;
        //通过点击浏览情况按钮进来
        if (this.viewType == 'browseDetails') {
            this.setTitle(tm.talentHall.Msg.resumeBrowsing);
        } else if (this.viewType == 'browseTimes') {//浏览人次
            this.setTitle(tm.talentHall.Msg.whoView + this.a0101 + tm.talentHall.Msg.resume);
        }
        var map = new HashMap();
        map.put("operateType", "getGridConfig");
        map.put("viewType", this.viewType);
        map.put("z8501", this.z8501);
        Rpc({
            functionId: 'TM000000201', success: function (res) {
                var resData = Ext.decode(res.responseText);
                var return_code = resData.return_code;
                var return_msg = resData.return_msg_code;
                if (return_code == "fail") {
                    // Ext.Msg.alert(tm.contendPos.msg.title, tm.talentHall.Msg.changeAprovalStatusError);
                } else {
                    var tableConfig = resData.return_data.gridConfig;
                    var configObj = Ext.decode(tableConfig);
                    configObj.beforeBuildComp = function (gridconfig) {
                        var columns = gridconfig.tableConfig.columns;
                        for (var i = 0; i < columns.length; i++) {
                            var column = columns[i];
                            column.menuDisabled = true;
                            column.sortable = false;
                        }
                    };
                    var tableObj = new BuildTableObj(configObj);
                    talentHallBrowse.add(tableObj.getMainPanel());
                }
            }, scope: this, async: false
        }, map);
    }
});