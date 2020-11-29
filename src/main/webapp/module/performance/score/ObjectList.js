/**
 * 左侧人员列表部分
 * @author zhanghua
 * @version1.0
 * 2018年4月24日 09:43:36
 */

Ext.define('Performance.score.ObjectList', {
    selectCallback: undefined,//选择时的回调事件
    renderId: "",//渲染目标
    template_Id: "",//模板id
    submitCallback: undefined,
    scoreCard:undefined,
    constructor: function (config) {
        var me = this;
        me.renderId = config.renderId;
        me.template_Id = config.template_Id;
        me.selectCallback = config.selectCallback;
        me.submitCallback = config.submitCallback;
        me.scoreCard=config.scoreCard;
        me.init();
    },
    init: function () {
        var me = this;
        var p_seq = 1, p_score = -1000, p_num = 0;
        var store = Ext.create('Ext.data.Store', {
            fields: ['objectKey', 'mainBodyKey', 'score', 'Status', 'Object_name', 'B0110', 'E0122'],
            proxy: {
                type: 'transaction',
                functionId: 'PM01010002',
                extraParams: {
                    model: scoreMain.model,
                    relation_Id: scoreMain.relation_Id,
                    object_List: scoreMain.object_List,
                    mainbody_Id: scoreMain.mainbody_Id,
                    template_Id: me.template_Id
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true,
            listeners: {
                'beforeload': function () {
                    p_seq = 1, p_score = -1000, p_num = 0;
                },
                'load': function (t_store, records) {
                    var record = null;
                    var view = Ext.getCmp(me.renderId + "_ObjectList_View");
                    if (scoreMain.selectedObjectId == undefined || scoreMain.selectedObjectId == '') {
                        scoreMain.selectedObjectId = records[0].data.object_id;
                        record = records[0];
                    } else
                        record = t_store.find('object_id', scoreMain.selectedObjectId);
                    if (view != undefined && record != undefined)
                        view.getSelectionModel().select(record);
                }
            }

        });


        var tpl = new Ext.XTemplate(
            '<tpl for=".">',
            '<div class="hj_item_event {[xindex % 2 === 0 ? "hj_item_odd" : ""]}">',
            '<table width="100%" height="100%">',
            '<tr  height="33%">',
            '<td rowspan="3" width="25%"> ',
            '<img src={imgPath} class="peopleImg_Cls" >',
            '</td>',
            '<td  colspan="2" class="objectList_nameCls" style="font-size: 12px ;font-weight: bold">{object_name}',//姓名
            '</td>',
            '<td  class="objectList_nameCls">{[this.getAttachment(values)]}',//评审材料
            '</td>',
            '</tr>',
            '<tr height="33%">',
            '<td class="objectList_nameCls" colspan="2">{depName}',
            '</td>',
            '<td class="objectList_nameCls" width="60px">',
            '<tpl if="status ==0"><p style="color:#FF9A64">' + performance.label.no_score + '</p></tpl>',//未评
            '<tpl if="status ==1"><p style="color: #6FA95A">' + performance.label.scoreing + '&nbsp;{[this.getPercentage(values)]}%</p></tpl>',//正评
            '<tpl if="status ==2"><p style="color: #A8A8A8">' + performance.label.scored + '</p></tpl>',//已提交
            '</td>',
            '</tr>',
            '<tr height="34%">',
            '<td class="objectList_nameCls" width="80" style="{[this.getStyle(values)]}">',
            performance.label.total_score + '：{score}',//总分
            '</td>',
            '<td class="objectList_nameCls" id="{objectKey}_score"  style="{[this.getStyle(values)]}">',
            performance.label.rank + '：{[this.getSeq(values)]}',//排名
            '</td>',
            '<td></td>',
            '</tr>',
            '</table>',
            '</div>',
            //'</div>',
            '</tpl>',
            {
                getSeq: function (values) {
                    var score = parseFloat(values.score);
                    if (p_score == -1000) {
                        p_num++;
                        p_score = score;
                        return p_seq;
                    }
                    if (score < p_score) {
                        p_num++;
                        //p_seq = p_num;
                        p_seq++;
                        p_score = score;
                        return p_seq;
                    } else if (score == p_score) {
                        p_num++;
                        return p_seq;
                    } else {
                        return p_seq;
                        //已经排好序传到前台的 不太可能会大。。。。
                    }

                },
                getStyle: function (values) {
                    if (values.status == 0)//隐藏未打分状态的评分和排名
                        return "visibility:hidden;";
                    else
                        return "";
                },
                getPercentage: function (values) {
                    var pointNum = parseFloat(values.pointNum);
                    var scorePointNum = parseFloat(values.scorePointNum);
                    if (scorePointNum == 0)
                        return 0;
                    return Math.round(scorePointNum / pointNum * 100);
                },
                getAttachment:function (values) {//拼接评审材料连接(职称评审)
                    var value = "";
                    var object_id = values.object_id;
                    if (scoreMain.object_attachment != undefined) {
                        var str="";
                        for(var i=0;i<scoreMain.object_attachment.length;i++){
                            if(scoreMain.object_attachment[i].a0100==object_id){
                                str=scoreMain.object_attachment[i].value;
                                break;
                            }
                        }

                        if (str != undefined&&str!="") {
                            var list = str.split("__");
                            if(list.length==4){
                                value="<a style='font-size:12px;' href=javascript:cardview_me.checkfile('"+list[0]+"','"+list[1]+"','"+list[2]+"','"+list[3]+"');>"+performance.label.reviewData+"</a>";//评审材料
                            }else{
                                value="<a style='font-size:12px;' href=javascript:cardview_me.checkfile('"+list[0]+"','"+list[1]+"','"+list[2]+"');>"+performance.label.proficientcheckfile+"</a>";//专家鉴定材料
                            }
                        }
                    }
                    return value;
                    
                }
            }
        );
        var selectRecord = undefined;
        var dataview = Ext.create("Ext.view.View", {
            // id: 'planView',
            itemSelector: 'div .hj_item_event',
            scrollable: 'y',
            id: me.renderId + "_ObjectList_View",
            tpl: tpl,
            border: false,
            deferEmptyText: false,
            overItemCls: 'overCls',
            selectedItemCls: 'selectedCls',
            store: store,
            multiSelect: false,
            listeners: {
                select: function (view, record, index) {
                    var lastSelectObjectKey = "";
                    if (selectRecord != undefined)
                        lastSelectObjectKey = selectRecord.data.objectKey;
                    me.fireSelect(view, record, index, lastSelectObjectKey, me.selectCallback);
                },
                deselect: function (view, selected) {
                    selectRecord = selected;
                },
                selectionchange: function (model, selected) {//实现点击空白处不取消选中状态
                    if (selectRecord != undefined && selectRecord != "" && selected.length == 0) {
                        model.select(selectRecord);
                    }
                    selectRecord = null;
                }
            }
        });
        dataview.getSelectionModel().allowDeselect

        var menu = Ext.create('Ext.menu.Menu', {
            items: [
                new Ext.menu.Item({
                    text: performance.button.exportExcelSelect,
                    handler:function () {
                        me.exportExcel(0);
                    }
                }),
                new Ext.menu.Item({
                    text: performance.button.exportExcelAll,
                    handler:function () {
                        me.exportExcel(1);
                    }
                })
            ]
        });



        var mainPanel = Ext.create('Ext.Panel', {
            layout: 'fit',
            height: '100%',
            width: '100%',
            border: false,
            id: me.renderId + "_ObjectList_mainPanel",
            tbar: [{
                text:performance.button.exportExcel,
                menu:menu
            },
                {
                    text: performance.button.submitAll,//'批量提交'
                    handler: function () {
                        var array = new Array();
                        var arrayA0100 = new Array();
                        var i = 0;
                        var isNeedHiddenButton = false;
                        var name="";
                        me.scoreCard.saveScore(me.scoreCard,scoreMain.selectedObjectKey,2);
                        Ext.each(dataview.store.data.items, function (record) {
                            if (record.data.status == 1 && record.data.scorePointNum == record.data.pointNum) {
                                array[i] = record.data.objectKey;
                                arrayA0100[i] = record.data.object_id;
                                i++;
                                if (record.data.objectKey == scoreMain.selectedObjectKey)
                                    isNeedHiddenButton = true;
                            }else if(record.data.status!=2){
                                name+=record.data.object_name+",";
                            }
                        });
                        if (array.length == 0) {
                            Ext.showAlert("没有可提交的数据！");
                            return;
                        }
                        Ext.showConfirm(performance.msg.submitAll, function (value) {//"提交后将不可修改，是否提交全部打分记录？"
                            if (value == 'yes') {
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
                                             var str=performance.msg.submit_success;
                                             if(name!=""){
                                            	 name = name.substr(0,name.length-1);
                                                 str=name+"未提交！";
                                             }
                                            Ext.showAlert(str);//"提交成功！"
                                            var fChangeSubmitStatus = me.changeSubmitStatus();
                                            fChangeSubmitStatus(array);
                                            if (isNeedHiddenButton) {
                                                Ext.getCmp(me.template_Id + '_SubmitScore').setHidden(true);
                                                Ext.getCmp(me.template_Id + '_SaveScore').setHidden(true);
                                                me.scoreCard.isEdit=false;
                                            }
                                            if (me.submitCallback != undefined) {
                                                Ext.callback(me.submitCallback, me, [arrayA0100])
                                            }
                                        } else {
                                            Ext.showAlert(result.message);
                                        }
                                    }
                                }, map);
                            }
                        });

                    }
                }, {
                    text: performance.label.rank,//排名
                    handler: function () {
                        Ext.getCmp(me.renderId + "_ObjectList_View").store.reload();
                    }
                }],
            items: dataview
        });

        Ext.getCmp(me.renderId).add(mainPanel);

    },
    //导出excel
    //type 0 导出选中数据， 1导出全部数据
    exportExcel:function (type) {
        var me=this;
        var dataview=Ext.getCmp(me.renderId + "_ObjectList_View");

        var arrayid = new Array();
        var arrayName = new Array();
        var i = 0;
        var array=null;
        if(type==0){
            array=dataview.getSelection();
        }else{
            array=dataview.store.data.items;
        }
        Ext.each(array, function (record) {
            arrayid[i] = record.data.objectKey;
            arrayName[i] = record.data.object_name;
            i++;
        });

        if (arrayid.length == 0) {
            arrayid[0] = scoreMain.selectedObjectKey;
            Ext.each(store.getData().items, function (record, index) {
                if (record.data.objectKey == scoreMain.selectedObjectKey) {
                    arrayName[0] = record.data.object_name;
                }
            });

        }
        var map = new HashMap();
        map.put("model", scoreMain.model);
        map.put("exportPreList", arrayid);
        map.put("exportPreNameList", arrayName);
        map.put("relation_Id", scoreMain.relation_Id);
        map.put("mainbody_Id", scoreMain.mainbody_Id);
        map.put("template_Id", me.template_Id);
        Rpc({
            functionId: 'PM01010006', success: function (form, action) {
                var result = Ext.decode(form.responseText);
                if (result.succeed) {
                    window.location.target = "_blank";
                    window.location.href = "/servlet/DisplayOleContent?filename=" + result.fileName;
                } else {
                    Ext.showAlert(performance.msg.export_error + result.message);//"导出失败！"
                }
            }
        }, map);
    },
    fireSelect: function (view, record, index, lastSelectRecord, callBackScope) {//列表选中事件


        scoreMain.selectedObjectKey = record.data.objectKey;
        scoreMain.selectedObjectId = record.data.object_id;
        var isEdit = true;
        if (record.data.status == 2)
            isEdit = false;
        if (callBackScope != undefined)//切换人时 刷新右侧菜单
            Ext.callback(callBackScope, this, [record.data.objectKey, lastSelectRecord, isEdit]);

    },
    getReloadStoreFunciton: function () {//更新左侧列表方法
        var me = this;
        return function (object_key, score, scorePointNum) {
            var store = Ext.getCmp(me.renderId + "_ObjectList_View").store;
            var record = store.findRecord("objectKey", object_key);
            if (record == undefined)
                return;
            var yScore = record.data.score;
            var yscorePointNum = record.data.scorePointNum;
            if (yScore == score && yscorePointNum == scorePointNum)
                return;
            record.set("score", score);
            record.set("status", 1);
            record.set("scorePointNum", scorePointNum);
            store.commitChanges();

            //排名
            var arr = new Array();
            Ext.Array.each(store.getData().items, function (item) {
                if (item.data.status != 0)
                    arr.push(item.data.score);
            });

            arr = me.quickSort(arr);
            if (Ext.isIE) {
                arr = me.unique(arr);//ie不支持 es6
            } else {
                arr = Array.from(new Set(arr));
            }
            var scoreMap = new HashMap();
            for (var i = 0; i < arr.length; i++) {
                scoreMap.put(arr[i], i + 1);
            }
            Ext.Array.each(store.getData().items, function (item, index, e) {
                var seq = Ext.getDom(item.data.objectKey + "_score").innerText;
                if (seq != '') {
                    seq = seq.replace(performance.label.rank + '：', '');
                    if (seq != scoreMap.get(item.data.score)) {
                        seq = scoreMap.get(item.data.score);
                        Ext.getDom(item.data.objectKey + "_score").innerText = performance.label.rank + '：' + seq;
                    }
                }
            });
        }
    },
    quickSort: function (arr) {//从大到小快速排序
        if (arr == undefined || arr.length <= 1)
            return arr;
        var minIndex = Math.floor(arr.length / 2);
        var minValue = arr.splice(minIndex, 1);
        var left = new Array();
        var right = new Array();

        for (var i = 0; i < arr.length; i++) {
            if (parseFloat(arr[i]) > parseFloat(minValue)) {
                left.push(arr[i]);
            } else {
                right.push(arr[i]);
            }
        }
        return this.quickSort(left).concat(minValue, this.quickSort(right));
    },
    changeSubmitStatus: function () {//更新提交状态
        var me = this;
        return function (arr) {
            var store = Ext.getCmp(me.renderId + "_ObjectList_View").store;

            for (var i = 0; i < arr.length; i++) {
                var object_key = arr[i];
                var record = store.findRecord("objectKey", object_key);
                if (record != undefined) {
                    if(Ext.getDom(object_key + "_score")!=undefined)
                        Ext.getDom(object_key + "_score").style.visibility = "visible";
                    var seq ="";
                    if(Ext.getDom(object_key + "_score")!=undefined)
                        seq=Ext.getDom(object_key + "_score").innerText;
                    record.set("status", 2);
                    store.commitChanges();
                    Ext.getDom(object_key + "_score").innerText = seq;
                }
            }

        }
    },
    unique: function (arr) {//数组去重方法
        var hash = [];
        for (var i = 0; i < arr.length; i++) {
            for (var j = i + 1; j < arr.length; j++) {
                if (arr[i] === arr[j]) {
                    ++i;
                }
            }
            hash.push(arr[i]);
        }
        return hash;
    }


});