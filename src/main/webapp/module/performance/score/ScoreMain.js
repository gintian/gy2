/**
 * 考评打分主体窗口部分
 * @author zhanghua
 * @version1.0
 * 2018年4月24日 09:43:36
 */

Ext.define('Performance.score.ScoreMain', {
    requires: [
        'Performance.score.ObjectList',
        'Performance.score.ScoreCard'
    ],
    model: '',//模块ID 1:职称评审
    relation_Id: '',//考核计划标识 格式：模块ID_自定义内容例：职称评审格式设置为 模块ID_评审会议ID_环节ID
    object_List: '',//加密的人员a0100，如果不传，则取当前计划下全部人员
    submitCallback: undefined,//数据提交成功时的回调
    object_attachment: undefined,//人员附件
    constructor: function (config) {
        scoreMain = this;
        scoreMain.model = config.model;//模块ID 1:职称评审
        scoreMain.mainbody_key = "";
        scoreMain.relation_Id = config.relation_Id;
        scoreMain.template_Id = "";
        scoreMain.selectedObjectKey = "";//当前选中的人员key
        scoreMain.submitCallback = config.submitCallback;
        scoreMain.selectedObjectId = "";
        scoreMain.object_attachment = config.object_attachment;

        if (config.object_List != null && config.object_List != '')
            scoreMain.object_List = config.object_List;
        else
            scoreMain.object_List = new Array();

        Ext.util.CSS.swapStyleSheet("one", rootPath + "/module/performance/score/ObjectListCss.css");
        Ext.Loader.loadScript({
            url: rootPath + '/module/performance/performance_resource_zh_CN.js', scope: this,
            onLoad: function () {
                var map = new HashMap();
                map.put("model", scoreMain.model);
                map.put("object_List", scoreMain.object_List);
                map.put("relation_Id", scoreMain.relation_Id);
                Rpc({
                    functionId: 'PM01010001', success: function (form, action) {//获取模板信息
                        var result = Ext.decode(form.responseText);
                        if (result.succeed) {
                            scoreMain.init(result);
                        } else {
                            Ext.showAlert(result.message);
                        }

                    }
                }, map);
            }
        });


    },
    init: function (result) {
        var me = this;
        scoreMain.tabs = Ext.create('Ext.tab.Panel', {
            minTabWidth: 150,
            tabHeight: 90,
            layout: 'fit',
            id:'scoretabs',
            tabBar: {
                height: 25,     //tab bar高度
                defaults: {
                    height: 23  //tab 里的title的高度
                }
            },
            listeners: {
                'tabchange': function (newTab) {
                    scoreMain.template_Id = newTab.activeTab.itemId;
                    var view = Ext.getCmp(scoreMain.template_Id + "_Table_List_ObjectList_View");
                    if (view != undefined && me.selectedObjectId != undefined) {
                        var store = view.store;
                        var record = store.find('object_id', me.selectedObjectId);
                        if (record != undefined)
                            view.getSelectionModel().select(record);
                        var t = view.getSelection()[0];
                        if (t != undefined) {
                            scoreMain.selectedObjectKey = t.data.objectKey;
                        }
                    }
                }
            }
        });

        var template_infoList = result.template_Info;
        for (var i = 0; i < template_infoList.length; i++) {
            var template_info = template_infoList[i];
            var tabPanel = Ext.create("Ext.Panel", {
                title: template_info.template_Name,//适用范围
                itemId: template_info.template_Id,
                bodyPadding: 10,
                layout: 'fit',
                items: [
                    {
                        xtype: 'panel',
                        layout: 'border',
                        border: false,
                        items: [{
                            xtype: 'panel',
                            width: 300,
                            layout: 'fit',
                            bodyStyle: 'border-width: 1px 0px 1px 1px',
                            region: 'west',
                            id: template_info.template_Id + "_Table_List"
                        }, {
                            xtype: 'panel',
                            region: 'center',
                            layout: 'fit',
                            id: template_info.template_Id + "_Table_Card"
                        }

                        ]
                    }
                ],
                listeners: {
                    'show': function (e) {
                        if (Ext.getCmp(e.itemId + "_Table_List_ObjectList_mainPanel") == undefined) {
                            var ScoreCardView = Ext.create("Performance.score.ScoreCard", {
                                renderId: e.itemId + "_Table_Card",
                                template_Id: e.itemId,
                                submitCallback: me.submitCallback
                            });
                            var reloadStoreFunction = ScoreCardView.getReloadStoreFunction();
                            var ObjectList = Ext.create("Performance.score.ObjectList", {
                                renderId: e.itemId + "_Table_List",
                                template_Id: e.itemId,
                                selectCallback: reloadStoreFunction,
                                submitCallback: me.submitCallback,
                                scoreCard:ScoreCardView
                            });
                            ScoreCardView.saveCallback = ObjectList.getReloadStoreFunciton();
                            ScoreCardView.submitChangeStatusCallback = ObjectList.changeSubmitStatus();
                        }
                    }
                }
            });
            scoreMain.tabs.add(tabPanel);
        }

        //生成弹出得window
        var win = Ext.widget("window", {
            id: "scoreMainWin",
            maximized: true,//大小适应屏幕
            border: false,
            header: false,//去除上方关闭按钮
            closable: false,
            frame: false,
            resizable: false,
            draggable: false,
            layout: 'fit',
            modal: true,
            closeAction: 'destroy',
            items: [scoreMain.tabs]
        });
        win.show();
        scoreMain.template_Id = template_infoList[0].template_Id;
        scoreMain.tabs.setActiveItem(0);
        var ScoreCardView = Ext.create("Performance.score.ScoreCard", {
            renderId: template_infoList[0].template_Id + "_Table_Card",
            template_Id: template_infoList[0].template_Id,
            submitCallback: me.submitCallback
        });
        var reloadStoreFunction = ScoreCardView.getReloadStoreFunction();

        var ObjectList = Ext.create("Performance.score.ObjectList", {
            renderId: template_infoList[0].template_Id + '_Table_List',
            template_Id: template_infoList[0].template_Id,
            selectCallback: reloadStoreFunction,
            submitCallback: me.submitCallback,
            scoreCard:ScoreCardView
        });
        ScoreCardView.saveCallback = ObjectList.getReloadStoreFunciton();
        ScoreCardView.submitChangeStatusCallback = ObjectList.changeSubmitStatus();
    }


});