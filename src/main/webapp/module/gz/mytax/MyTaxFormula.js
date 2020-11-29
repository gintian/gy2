/**
 *    计算公式组件 zhaoxg add
 *    调用方法：
 Ext.require('EHR.defineformula.DefineFormula',function(){
			Ext.create("EHR.defineformula.DefineFormula",{module：thisScope.module,id:id,formulaType:thisScope.formulaType});
		})
 *    参数说明：thisScope.module：模块标识  1：薪资  2：薪资总额  3：人事异动  4...其他
 id：主键标识   薪资则为薪资类别号；  人事异动为公式组号；  其他。。。根据各自模块自行设置   在交易类中区分即可
 **/
Ext.define('formulacombo', {
    requires: ['EHR.extWidget.proxy.TransactionProxy'],
    extend: 'Ext.form.field.ComboBox',
    xtype: 'formulacombo',
    createPicker: function () {
        var me = this;
        var picker = this.callParent(arguments);
        me.fieldlength = me.bodyEl.getWidth();
        me.resize(picker, me.fieldlength);
        picker.on('itemclick', function (e, record) {
            me.fireEvent('selectclick', me, record);
        });
        return picker;
    },
    onExpand: function () {
        var me = this;
        var picker = me.picker;
        me.resize(picker, me.fieldlength);
    },
    resize: function (picker, fieldlength) {
        var items = picker.store.data.items;
        var pickerlength = 0;
        for (var i = 0; i < items.length; i++) {

            var item = items[i].data.name;
            if (item != "") {
                var one = (item.split('#!#')[0].length + 1) * 8;
                var two = "0";
                if (item.split('#!#').length > 1) {
                    two = item.split('#!#')[1].length * 16;
                }
                var itemlength = parseInt(one) + parseInt(two);
                if (itemlength > pickerlength) {
                    pickerlength = itemlength;
                }
            }
        }
        if (pickerlength < fieldlength)
            pickerlength = fieldlength;
        picker.setWidth(pickerlength);
    }
});
Ext.define('mytax.MyTaxFormula', {
    requires: ['EHR.extWidget.proxy.TransactionProxy'],
    constructor: function (config) {
        thisScope = this;
        thisScope.id = config.id;//薪资类别id,人事异动模版id
        thisScope.formulaType = config.formulaType;//1:是计算公式，2:是审核公式，lis添加
        thisScope.module = config.module;//模块号
        thisScope.callBackfn = config.callBackfn;
        thisScope.infor_type = config.infor_type;//姓名、单位、岗位
        thisScope.selectionStart = 0;//光标起选中始位置
        thisScope.selectionEnd = 0;//光标选中结束位置
        thisScope.selectionIndex = 0;//光标位置
        thisScope.callbackfunc = config.callbackfunc;//光标位置
        thisScope.hzname = "";
        thisScope.range = null;//计算公式选中的range对象
        this.itemdata = config.itemdata;
        this.itemType = config.itemType;
        this.calcFormat = config.calcFormat;
        thisScope.init();

    },
    init: function () {
        var width_1 = 28;
        var width_2 = 32;
        var width_3 = 28;
        var width_4 = 59;
        var style_1 = "margin-left:3px;margin-top:5px";
        var buttons = Ext.widget('container', {
            items: [{
                xtype: 'fieldset',
                title: common.label.operationaSymbol,
                layout: 'vbox',
                width: 260,
                height: 150,
                padding: '0 0 0 6',
                style: 'algin:center',
                items: [
                    {
                        xtype: 'container',
                        items: [
                            {
                                xtype: 'button',
                                text: '0',
                                width: width_1,
                                height: 25,
                                style: 'margin-top:5px',
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '1',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                handler: function () {
                                    thisScope.symbol('shry', '1');
                                }
                            },
                            {
                                xtype: 'button',
                                text: '2',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '3',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '4',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '(',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: common.button.If,
                                width: width_4,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'container',
                        items: [
                            {
                                xtype: 'button',
                                text: '5',
                                width: width_1,
                                height: 25,
                                style: 'margin-top:5px',
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '6',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '7',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '8',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '9',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: ')',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: common.button.Else,
                                colspan: 2,
                                width: width_4,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'container',
                        items: [
                            {
                                xtype: 'button',
                                text: '+',
                                width: width_1,
                                height: 25,
                                style: 'margin-top:5px',
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '-',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '*',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '%',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '/',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '\\',
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: common.button.And,
                                width: width_3,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: common.button.Or,
                                width: width_3,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'container',
                        items: [
                            {
                                xtype: 'button',
                                text: '=',
                                width: 25,
                                height: 25,
                                style: 'margin-top:5px',
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '>',
                                width: 25,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '<',
                                width: 25,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '<>',
                                width: width_2,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '<=',
                                width: width_2,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '>=',
                                width: width_2,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: '~',
                                width: 25,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: common.button.Not,
                                width: width_1,
                                height: 25,
                                style: style_1,
                                listeners: {
                                    "click": function () {
                                        thisScope.symbol('shry', this.text);
                                    }
                                }
                            }
                        ]
                    }
                ]
            }]
        });

        var formula = Ext.create('Ext.panel.Panel', {
            border: false,
            items: [{
                border: false,
                xtype: 'textareafield',
                name: 'formula',
                id: 'shry',
                fieldStyle: 'line-height:18px;height:220px;',
                width: 500,
                height: 220,
                enableKeyEvents: true,
                value:thisScope.calcFormat,
                listeners: {
                    afterrender: function (textarea) {
                        if (Ext.isIE) {//ie 下绑定mouseleave事件
                            textarea.getEl().on("mouseleave", function () {
                                //thisScope.getCursorPosition();//获得光标位置  lis update2016-7-5
                            })
                        }
                    },
                    change: function () {
                        if (Ext.isIE) {
                            //thisScope.getCursorPosition();//获得光标位置   大局观告诉我 改变不需要这玩意  zhaoxg update2016-5-10
                        }
                    },
                    keyup: function (textarea, e) {
                        if (Ext.isIE) {
                            thisScope.getCursorPosition();//获得光标位置     lis update2016-7-5
                        }
                    },
                    click: {
                        element: 'el',
                        fn: function () {
                            if (Ext.isIE) {
                                thisScope.getCursorPosition(); //获得光标位置
                            }
                        }
                    }
                }
            }],
            buttons: [
                "->",
                // {
                //     xtype: 'button', text: common.button.functionGuide, handler: function () {
                //         thisScope.functionWizard();
                //     }
                // },//函数向导
                {
                    xtype: 'button', text: common.button.formulaSave,//公式保存
                    handler: function () {
                        thisScope.saveFormula();
                    }
                }
            ]
        });
        thisScope.itemStore = Ext.create('Ext.data.Store',
            {
                fields: ['name', 'id'],
                data: this.itemdata
            });
        var codeStore = Ext.create('Ext.data.Store',
            {
                fields: ['name', 'id'],
                proxy: {
                    type: 'transaction',
                    functionId: 'GZ00000901',
                    reader: {
                        type: 'json',
                        root: 'data'
                    }
                }
            });
        var itemCombox = {
            id: 'item_combobox',
            xtype: 'formulacombo',
            fieldLabel: common.label.items,//项目
            labelSeparator: '',//去掉后面的冒号
            store: thisScope.itemStore,
            displayField: 'name',
            editable: false,
            valueField: 'id',
            queryMode: 'local',
            labelAlign: 'right',
            labelWidth: 40,
            width: 200,
            style: 'margin-top:15px',
            matchFieldWidth: false,
            listeners: {
                'selectclick': function (combo, ecords) {
                    Ext.getCmp('code_combobox').reset();
                    var comValue = combo.getValue();
                    if ('newcreate' == comValue) {
                        thisScope.viewTempVar();
                    } else {
                        //var itemid = comValue.split(":");
                        //出现冒号截取
                        thisScope.symbol('shry', comValue.substring(comValue.indexOf(":") + 1, comValue.length));
                        codeStore.load({
                            params: {
                                itemid: combo.value,
                                type: 'getFormulaCodeData'
                            },
                            callback: function (record, option, succes) {
                                if (record.length > 1) {
                                    Ext.getCmp('code_combobox').show();
                                } else {
                                    Ext.getCmp('code_combobox').hide();
                                }
                            }
                        });
                    }
                }
            }
        };
        var codeCombox = {
            id: 'code_combobox',
            xtype: 'formulacombo',
            fieldLabel: common.label.code,//代码型
            labelSeparator: '',//去掉后面的冒号
            store: codeStore,
            displayField: 'name',
            editable: false,
            valueField: 'id',
            queryMode: 'local',
            hidden: true,//只有选中代码型的项目才会显示
            labelWidth: 40,
            width: 200,
            labelAlign: 'right',
            style: 'margin-top:15px',
            matchFieldWidth: false,
            listeners: {
                'selectclick': function (combo, ecords) {
                    var itemid = combo.getValue();
                    thisScope.symbol('shry', "\"" + itemid + "\"");
                }
            }
        };

        var items = new Array();
        items.push(itemCombox);
        items.push(codeCombox);
        var itempanel = Ext.create('Ext.panel.Panel', {
            border: false,
            items: [{
                xtype: 'fieldset',
                id: 'fieldset',
                title: common.label.referenceItems,//参考项目
                layout: 'column',
                width: 230,
                height: 150,
                items: items
            }]
        });
        thisScope.expression = Ext.create('Ext.panel.Panel', {
            width: 500,
            height: 420,
            id: 'expression',
            layout: 'border',
            border: false,
            bodyStyle: 'background:#ffffff;',
            items: [
                {region: "west", border: false, width: 240, items: itempanel},
                {
                    region: "north", border: false, height: 262, items: formula, style: {
                        marginTop: '3px'
                    }
                },
                {region: "center", border: false, width: 260, items: buttons}
            ]
        });

        var bodyPanel = Ext.create('Ext.panel.Panel', {
            height: 425,
            border: false,
            bodyStyle: 'background:#ffffff;',
            layout: {
                pack: 'center',
                align: 'middle',
                type: 'vbox'
            },
            items: [
                {id: 'centerPanel', border: false, items: [thisScope.expression]}
            ]
        });
        var title = common.label.computeFormula;//计算公式
        var win = Ext.widget("window", {
            title: title,
            height: 475,
            width: 550,
            itemId:'formulaWindow',
            minButtonWidth: 40,
            layout: 'fit',
            bodyStyle: 'background:#ffffff;',
            modal: true,
            resizable: false,
            closeAction: 'destroy',
            //复写beginDrag方法，解决下拉框弹出时拖动造成页面混乱
            beginDrag: function () {
                // thisScope.cellediting.completeEdit();
                Ext.each(itempanel.query('combobox'), function (combox, index) {
                    combox.collapse();
                });
            },
            items: [bodyPanel],
            // buttons: [
            //     {xtype: 'tbfill'},
            //     {
            //         text: common.button.close,//关闭
            //         handler: function () {
            //             thisScope.expression.destroy();
            //             win.destroy();
            //
            //         }
            //     },
            //     {xtype: 'tbfill'}
            // ]
        });
        win.show();
    },

    symbol: function (exprId, strexpr) {
        var rulearea = Ext.getCmp(exprId);
        var myField = rulearea.inputEl.dom;
        var startPos = 0;//光标选中内容起始位置
        var endPos = 0;//光标选中内容结束位置
        var selectionIndex = 0;//光标位置
        if (myField.selectionStart || myField.selectionStart == '0') {
            startPos = myField.selectionStart;
            endPos = myField.selectionEnd;
            // 保存scrollTop，为了换行
            var restoreTop = myField.scrollTop;
            //写入选中内容
            rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));

            if (restoreTop > 0) {//换行
                myField.scrollTop = restoreTop;
            }
            myField.focus();
            myField.selectionStart = startPos + strexpr.length;
            myField.selectionEnd = startPos + strexpr.length;
        }else {
            var element = document.selection;
            if(Ext.isIE && element != null) {//对于ie下非兼容模式下，还是得这样写，否则定位有问题
                //写入选中内容
                var sel = null;
                startPos = thisScope.selectionStart;
                endPos = thisScope.selectionEnd;
                selectionIndex = thisScope.selectionIndex
                myField.focus();
                rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));

                var index = selectionIndex + strexpr.length;
                var range = myField.createTextRange();
                range.move("character", index);//移动光标
                range.select();//选中
                thisScope.selectionIndex = index;
                thisScope.selectionStart = startPos + strexpr.length;
                thisScope.selectionEnd = endPos + strexpr.length;
            }else
                myField.value += strexpr;
        }
    },

    //获得光标位置
    getCursorPosition: function () {
        var rulearea = Ext.getCmp('shry');
        var el = rulearea.inputEl.dom;//得到当前textarea对象
        if (Ext.isIE) {
            el.focus();
            if (document.selection != null) {
                //IE11不支持document.selection，用document.getSelection()替代了document.selection.createRange().text:
                var r = document.selection.createRange(); //返回当前网页中的选中内容的TextRange对象
                thisScope.range = r;
                if (r == null) {
                    thisScope.selectionStart = 0;
                }
                var re = el.createTextRange(); //选中内容
                var rc = re.duplicate(); //所有内容
                try {
                    //定位到指定位置
                    re.moveToBookmark(r.getBookmark());
                    //【为了保持选区】rc的开始端不动，rc的结尾放到re的开始
                    rc.setEndPoint('EndToStart', re);
                } catch (e) {
                    //表格控件点击刷新页面按钮后，此时鼠标焦点拿不到 lis 20160704
                }
                var text = rc.text;
                text = text.replace(/[\r]/g, " ");//替换回车符 lis 20160701
                thisScope.selectionIndex = text.length; //光标位置
                thisScope.selectionStart = rc.text.length;
                thisScope.selectionEnd = thisScope.selectionStart + re.text.length;
            } else {
                thisScope.selectionIndex = el.selectionStart; //光标位置
                thisScope.selectionStart = el.selectionStart;
                thisScope.selectionEnd = el.selectionEnd;
            }
        }
    },
    //保存公式
    saveFormula: function () {
        var c_expr = Ext.getCmp("shry").getValue();
        var map = new HashMap();
        map.put("c_expr",getEncodeStr(c_expr));
        map.put("type",'checkFormula');
        map.put("itemType",thisScope.itemType);
        Rpc({functionId:'GZ00000901',async:false,success: function(form,action){
                var result = Ext.decode(form.responseText);
                var info=result.return_data.info;
                if(info=="ok"){
                    Ext.callback(thisScope.callbackfunc,null,[c_expr]);
                    Ext.ComponentQuery.query('#formulaWindow')[0].close();
                }else{
                    if(info.length<4){
                        var formula=Ext.getCmp("shry").getValue();
                        Ext.showAlert(formula+common.label.syntaxError+"！");//{
                    }else{
                        Ext.showAlert(info);
                    }
                }
            }},map);
    },

    //临时变量弹出框     lis   2015-10-17
    viewTempVar: function (salaryid) {
        Ext.require('EHR.defineformula.DefineTempVar', function () {
            Ext.create("EHR.defineformula.DefineTempVar", {
                module: '1',
                id: thisScope.id,
                type: '1',
                callBackfn: 'thisScope.reflashItem'
            });
        })
    },
    //新增完临时变量后刷新项目列表  zhaoxg add 2016-3-4
    reflashItem: function () {
        thisScope.itemStore.load();
    },
    //函数向导
    functionWizard: function () {
        Ext.require('EHR.functionWizard.FunctionWizard', function () {
            Ext.create("EHR.functionWizard.FunctionWizard", {
                // keyid: thisScope.id,
                opt: "8",
                checktemp: 'salary',
                mode: 'xzgl_jsgs',
                inforType: '1',
                callbackfunc: 'thisScope.getfunctionWizard'
            });
        });
    },

    //函数向导回调函数，用来接收返回值
    getfunctionWizard: function (obj) {
        thisScope.symbol('shry', obj);
    }

});