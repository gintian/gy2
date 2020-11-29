/**
 * 考核打分部分
 * @author zhanghua
 * @version1.0
 * 2018年4月24日 09:43:36
 */

Ext.define('Performance.score.ScoreCard', {
    saveCallback: undefined,//保存回调
    renderId: '',//渲染位置
    template_Id: '',//模板id
    isEdit: true,//是否可编辑
    submitCallback: undefined,
    submitChangeStatusCallback: undefined,
    constructor: function (config) {
        var me = this;
        me.saveCallback = config.saveCallback;
        me.renderId = config.renderId;
        me.template_Id = config.template_Id;
        me.submitCallback = config.submitCallback;
        me.submitChangeStatusCallback = config.submitChangeStatusCallback;
        me.init();
    },
    init: function () {
        var me = this;
        var editRowNum = 0;
        var columns = [
            {text: 'id', hidden: true, dataIndex: 'id',hideable:false},//主键
            {text: 'scoreMode', hidden: true, dataIndex: 'scoreMode',hideable:false},
            {
                xtype: 'treecolumn',
                text: performance.colunm.point_Name, //'任务名称'
                sortable: false, width: 250, dataIndex: 'text',
                renderer: function (value, meta, record) {
                    if (record.childNodes.length > 0) {
                        meta.tdStyle = 'border-width: 0px 0px 0px 0px;font-size: 12px;font-weight: bold';
                    } else if (record.data.scoreMode == '1') {
                        meta.tdStyle = 'background-color:#F2F2F2;';
                    }
                    return value;
                }
            },
            {
                text: performance.colunm.description,// '指标解释'
                sortable: false, width: 350, dataIndex: 'description',
                renderer: function (value, meta, record) {
                    meta.style = 'white-space:normal;word-break:break-all;padding:5px 5px 5px 5px;line-height:20px;';
                    if (record.childNodes.length > 0)
                        meta.tdStyle = 'border-width: 0px 0px 0px 0px;';
                    else if (record.data.scoreMode == '1') {
                        meta.tdStyle = 'background-color:#F2F2F2;';
                    }
                    if (value != undefined) {
                        value = value.replace(/\r\n/g, "<br>");
                        value = value.replace(/\n/g, "<br>");
                    }
                    return value;
                }
            },
            {
                text: performance.colunm.total_score,//'标准分'
                align: 'center', sortable: false, width: 100, dataIndex: 'totalScore',
                renderer: function (value, meta, record) {
                    if (record.childNodes.length > 0)
                        meta.tdStyle = 'border-width: 0px 0px 0px 0px;';
                    else if (record.data.scoreMode == '1') {
                        meta.tdStyle = 'background-color:#F2F2F2;';
                    }
                    return value;
                }
            },
            {
                text: performance.colunm.Score,//'评价'
                align: 'center',
                sortable: false,
                width: 100,
                dataIndex: 'Score',
                xtype: 'numbercolumn',
                editor: {
                    xtype: 'numberfield',
                    allowBlank: true,
                    mouseWheelEnabled:false,//去掉鼠标滑轮增减
                    validator: function (value) {
                        var record = store.getAt(editRowNum);
                        var maxValue = 0;
                        var minValue = 0;
                        value = parseFloat(value);
                        if (record.data.totalScore > 0) {
                            maxValue = parseFloat(record.data.totalScore);//设置最大值
                        } else {
                            minValue = parseFloat(record.data.totalScore);//设置最小值
                        }
                        if (isNaN(value)) {
                            return performance.msg.scoreCannotBeEmpty;//分数不能为空！
                        } else if (value >= minValue && value <= maxValue) {
                            return true;
                        } else if (value > maxValue) {
                            return performance.msg.scoreCannotBegreaterMax + maxValue;//分数不能大于最大值
                        } else {
                            return performance.msg.scoreCannotLessMin + minValue;//分数不能小于最小值
                        }

                    },
                    listeners: {
                        'focus': function () {
                            this.selectText();
                        },
                        'specialkey': function (field, e) {
                            if (e.getKey() == e.ENTER) {//回车时打开下一个可编辑行
                                var p = Ext.getCmp(me.template_Id + '_ScoreTreePanel');
                                var plug = p.getPlugins()[0];
                                var selectedRecord = p.getSelection()[0];
                                var store = p.store;
                                var rowNum = store.indexOf(selectedRecord);
                                var i = 0;
                                for (i = rowNum + 1; i < store.getData().length; i++) {
                                    var r = store.getAt(i);
                                    if (r.isLeaf() == true && r.data.scoreMode != '1') {
                                        break;
                                    }
                                }
                                if (i != store.getData().length) {
                                    plug.completeEdit();//完成编辑 如果不这样会导致验证方法取标准分错误的取得下面一行的分
                                    p.getSelectionModel().select(i);
                                    plug.startEditByPosition({
                                        row: i,
                                        column: 5
                                    });
                                    return false;
                                }
                            }
                        }
                    }
                },
                renderer: function (value, meta, record) {
                    if (record.childNodes.length > 0)
                        meta.tdStyle = 'border-width: 0px 0px 0px 0px ';
                    else if (record.data.scoreMode == '1') {
                        meta.tdStyle = 'background-color:#F2F2F2;';
                    }
                    return value;
                }
            }
        ];

        var store = Ext.create('Ext.data.TreeStore', {
            // root: {
            //     expanded: true,
            //     children: data
            // }
        });
        var mainPanel = Ext.create('Ext.tree.Panel', {
            width: 500,
            height: 300,
            autoScroll: true,
            layout: 'fit',
            id: me.template_Id + '_ScoreTreePanel',
            store: store,
            rootVisible: false, // 指定根节点可见
            border: false,
            columnLines: true,
            useArrows: true,
            stripeRows: true,
            rowLines: true,
            enableColumnMove: false,
            sortableColumns: false,
            columns: columns,
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1,
                    listeners: {
                        'beforeedit': function (editor, context, eOpts) {
                            var recordData = context.record;
                            if (!me.isEdit || recordData.isLeaf() == false || recordData.data.scoreMode == '1')
                                return false;
                            editRowNum = context.rowIdx;
                        }
                    }
                })
            ],
            tbar: [{
                text: performance.button.save,//'保存'
                hidden: true,
                id: me.template_Id + '_SaveScore',
                handler: function () {
                    me.saveScore(me, scoreMain.selectedObjectKey,0);
                }
            }, {
                text: performance.button.submit,// '提交'
                hidden: true,
                id: me.template_Id + '_SubmitScore',
                handler: function () {
                    var canSubmit = true;
                    me.saveScore(me, scoreMain.selectedObjectKey, 2);
                    Ext.each(store.getData().items, function (record, index) {
                        if (record.isLeaf() == true && (record.data.Score == ''||record.data.Score==undefined) && parseFloat(record.data.Score) != 0) {
                            canSubmit = false;
                        }
                    });
                    if (!canSubmit) {
                        Ext.showAlert(performance.msg.canNotSubmit);//未完成评分，不能提交！
                        return;
                    }

                    Ext.showConfirm(performance.msg.submit, function (value) {//"提交后将不可修改，是否提交打分记录？"
                        if (value == 'yes') {
                            var array = new Array();
                            var arrayA0100 = new Array();
                            array[0] = scoreMain.selectedObjectKey;
                            arrayA0100[0] = scoreMain.selectedObjectId;
                            var map = new HashMap();
                            map.put("model", scoreMain.model);
                            map.put("submitPreList", array);
                            map.put("relation_Id", scoreMain.relation_Id);
                            map.put("mainbody_Id", scoreMain.mainbody_Id);
                            map.put("template_Id", me.template_Id);
                            map.put("object_List", scoreMain.object_List);
                            Rpc({
                                functionId: 'PM01010005', success: function (form, action) {
                                    var result = Ext.decode(form.responseText);
                                    if (result.succeed) {
                                        me.isEdit = false;

                                        Ext.getCmp(me.template_Id + '_SubmitScore').setHidden(true);
                                        Ext.getCmp(me.template_Id + '_SaveScore').setHidden(true);
                                        if (me.submitChangeStatusCallback != undefined) {//提交成功后需要同步刷新左侧列表
                                            Ext.callback(me.submitChangeStatusCallback, me, [array]);
                                        }
                                        if (me.submitCallback != undefined) {
                                            Ext.callback(me.submitCallback, me, [arrayA0100])
                                        }
                                        Ext.showAlert(performance.msg.submit_success);//"提交成功！"
                                    } else {
                                        Ext.showAlert(result.message);
                                    }
                                }
                            }, map);
                        }
                    });
                }
            }, {
                text: performance.button.back,//'返回'
                handler: function () {
                    Ext.getCmp("scoretabs").destroy();
                    Ext.getCmp("scoreMainWin").destroy();

                }
            }],
            listeners: {
                // 'itemclick': function (e, record, item, index) {//点击行时 打开或者关闭树节点
                //     if (record.isLeaf() == false) {
                //         me.operTreeNode(e.panel, record);
                //     }
                // },
                'cellclick': function (table, td, cellindex, record, tr, rowindex) {
                    if (cellindex == 5) {
                        this.getPlugins()[0].startEditByPosition({
                            row: rowindex,
                            column: 5
                        });
                    }

                },
                'beforedestroy': function () {
                    me.saveCallback == undefined;
                    me.saveScore(me, this.objectKey, 3);
                }
            }
        });
        mainPanel.expandAll();
        Ext.getCmp(this.renderId).add(mainPanel);
        //store.commitChanges();
    },
    getReloadStoreFunction: function () {//刷新打分页面数据方法
        var me = this;
        return function (object_Key, oldObject_Key, isEdit) {
            me.isEdit = isEdit;
            var panel = Ext.getCmp(me.template_Id + '_ScoreTreePanel');
            panel.objectKey = object_Key;
            if (object_Key == oldObject_Key) {
                return;
            }
            if (me.isEdit) {
                Ext.getCmp(me.template_Id + '_SubmitScore').setHidden(false);
                Ext.getCmp(me.template_Id + '_SaveScore').setHidden(false);
            } else {
                Ext.getCmp(me.template_Id + '_SubmitScore').setHidden(true);
                Ext.getCmp(me.template_Id + '_SaveScore').setHidden(true);

            }
            var mask = new Ext.LoadMask( Ext.getCmp("scoreMainWin"), {
                msg:performance.msg.pleasewait
            });
            mask.show();
            if (oldObject_Key != undefined && oldObject_Key != '')
                me.saveScore(me, oldObject_Key, 1);//刷新页面之前 自动保存

            var map = new HashMap();
            map.put("model", scoreMain.model);
            map.put("object_Id", object_Key);
            map.put("relation_Id", scoreMain.relation_Id);
            map.put("mainbody_Id", scoreMain.mainbody_Id);
            map.put("template_Id", me.template_Id);
            Rpc({
                functionId: 'PM01010003',async: false, success: function (form, action) {
                    var result = Ext.decode(form.responseText);
                    if (result.succeed) {
                        var root = Ext.create('Ext.data.TreeModel', {
                            expanded: true,
                            children: result.data
                        });
                        panel.store.setRoot(root);
                        panel.expandAll();
                        panel.store.commitChanges();
                        panel.objectKey = object_Key;

                    } else {
                        Ext.showAlert(result.message);
                    }


                }
            }, map);
            mask.hide();  //隐藏
        }
    },
    // operTreeNode: function (panel, record) {
    //     if (record.data.expanded == true) {
    //         panel.collapseNode(record);
    //     } else {
    //         panel.expandNode(record);
    //     }
    // },
    //type:0 点击保存,1翻页,2提交,3退出
    saveScore: function (me, object_Id,type) {//保存评分
        var store = Ext.getCmp(me.template_Id + '_ScoreTreePanel').store;
        var dataList = new Array();

        Ext.each(store.getModifiedRecords(), function (record, index) {
            var map = new HashMap();
            map.put('id', record.data.id);
            map.put('score', record.data.Score);
            dataList[index] = map;
        });
        if (dataList.length > 0) {
            var mask=undefined;
            if(type==0){
                mask= new Ext.LoadMask( Ext.getCmp("scoreMainWin"), {
                    msg:performance.msg.pleasewait//正在处理数据 请等待
                });
                mask.show();
            }

            var map = new HashMap();
            map.put("model", scoreMain.model);
            map.put("objectKey", object_Id);
            map.put("relation_Id", scoreMain.relation_Id);
            map.put("mainbody_Id", scoreMain.mainbody_Id);
            map.put("template_Id", me.template_Id);
            map.put("dataList", dataList);
            Rpc({
                functionId: 'PM01010004', async: false, success: function (form, action) {
                    var result = Ext.decode(form.responseText);
                    if (result.succeed) {
                        if (type==0||type==2) {
                            //store.commitChanges();
                            //注意 此处无法直接使用store.commitChanges() 否则会在多次点击保存后引发页面未知异常。因此改为后台刷新store数据的方式
                            var map1 = new HashMap();
                            map1.put("model", scoreMain.model);
                            map1.put("object_Id", object_Id);
                            map1.put("relation_Id", scoreMain.relation_Id);
                            map1.put("mainbody_Id", scoreMain.mainbody_Id);
                            map1.put("template_Id", me.template_Id);
                            Rpc({
                                functionId: 'PM01010003',async: false, success: function (form1) {
                                    store.load();
                                    result = Ext.decode(form1.responseText);
                                    if (result.succeed) {
                                        var root = Ext.create('Ext.data.TreeModel', {
                                            expanded: true,
                                            children: result.data
                                        });
                                        store.setRoot(root);
                                        Ext.getCmp(me.template_Id + '_ScoreTreePanel').expandAll();
                                        store.commitChanges();
                                        store.objectKey = object_Id;

                                    } else {
                                        if(mask!=undefined){
                                            mask.hide();
                                        }
                                        Ext.showAlert(result.message);
                                    }


                                }
                            }, map1);
                            if(mask!=undefined){
                                mask.hide();
                            }
                            if(type==0) {
                                Ext.showAlert(performance.msg.save_success);//"保存成功！"
                            }
                        }
                        if (type!=3 && me.saveCallback != undefined) {//保存成功后需要同步刷新左侧列表
                            var score = 0;
                            var scorePointNum = 0;
                            Ext.each(store.getData().items, function (record, index) {
                                if (record.isLeaf() == true && (!isNaN(parseFloat(record.data.Score)))) {
                                    score += parseFloat(record.data.Score);
                                    scorePointNum++;
                                }
                            });
                            score = Math.round(score*100)/100;
                            Ext.callback(me.saveCallback, me, [object_Id, score, scorePointNum]);
                        }

                    } else {
                        if(mask!=undefined){
                            mask.hide();
                        }
                        Ext.showAlert(result.message);
                    }

                }
            }, map);


        }

    }


});