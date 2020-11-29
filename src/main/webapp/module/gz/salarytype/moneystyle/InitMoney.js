/**
 * 薪资类别-币种维护
 * lis 2015-12-1
 */
Ext.define('SalaryTypeUL.moneystyle.InitMoney',{
    constructor:function(config){
        money_me = this;
        money_me.createSalary();
    },
    createSalary:function()
    {
        //获得薪资项目数据store
        money_me.moneyStore = Ext.create('Ext.data.Store', {
            fields:['nstyleid','cname','ctoken','cunit', 'nratio'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000223',
                extraParams:{
                    salaryid:money_me.salaryid
                },
                reader: {
                    type: 'json',
                    root: 'moneyList'
                }
            },
            autoLoad: true
        });

        //生成薪资项目grid
        money_me.gridpanel = Ext.create('Ext.grid.Panel', {
            store: money_me.moneyStore,
            width: 784,
            height: 391,
            multiSelect:true,
            columnLines:true,
            rowLines:true,
            border:false,
            selModel: Ext.create("Ext.selection.CheckboxModel", {
                mode: "multi",//multi,simple,single；默认为多选multi
                checkOnly: false,//如果值为true，则只用点击checkbox列才能选中此条记录
                allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
                enableKeyNav: true
            }),
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1,
                    listeners:{
                        'edit':function(editor, e, eOpts ){
                            money_me.saveMoney(e.record.data,"2");
                            e.record.commit();
                        }
                    }
                })
            ],
            columns: [
                //名称
                { text: common.label.name,menuDisabled:true, dataIndex: 'cname',flex: 2,sortable:false,
                    field: {
                        xtype: 'textfield',
                        allowBlank: false,
                        validator: function (value) {
                            if(money_me.getStrLength(value)>40){
                                return gz.msg.currencyNameMaxLength.replace("{0}","40");//"名称最大程度为40位";
                            }else{
                                return true;
                            }
                        }
                    }},
                //符号
                { text: gz.label.symbol,menuDisabled:true, dataIndex: 'ctoken',flex: 1,sortable:false,
                    field: {
                        xtype: 'textfield',
                        allowBlank: false,
                        validator: function (value) {
                            if(money_me.getStrLength(value)>4){
                                return gz.msg.currencyNameMaxLength.replace("{0}","4");//"最大程度为4位";
                            }else{
                                return true;
                            }
                        }
                    }},
                //单位
                { text: gz.label.unit,menuDisabled:true, dataIndex: 'cunit',flex: 1,sortable:false,
                    field: {
                        xtype: 'textfield',
                        allowBlank: false,
                        validator: function (value) {
                            if(money_me.getStrLength(value)>4){
                                return gz.msg.currencyNameMaxLength.replace("{0}","4");//"最大程度为4位";
                            }else{
                                return true;
                            }
                        }
                    }},
                //汇率
                { text: gz.label.exchangeRate,menuDisabled:true, dataIndex: 'nratio',flex: 1,align:'right',sortable:false,
                    field: {
                        xtype:'numberfield',
                        hideTrigger:true,
                        maxLength:12,
                        maxLengthText:gz.msg.NumTooLong,
                        allowBlank: false,
                        regex: /^(-?\d+)(\.\d+)?(e|E)?((-|\+)?\d+)?$/,
                        regexText: gz.msg.rateError
                    }},
                //货币维护
                { text:gz.label.moneyMaintain,menuDisabled:true, dataIndex: 'nstyleid',flex: 1,align:'center',sortable:false,
                    renderer:function(value, metaData, record){
                        if(record.data.initflag == '3' || record.data.initflag == '0')
                            return "";
                        var html = '<a href="javaScript:money_me.initMoneyDetail(\''+value+'\')" >';
                        html = html + '<img src="/images/new_module/salaryitem.gif" border=0></a>';
                        return html;
                    }}
            ],
            buttonAlign:'center',
            minButtonWidth:50,
            renderTo:Ext.getBody()
        });

        //生成弹出得window
        var win=Ext.widget("window",{
            title:gz.label.moneyTypeMaintain,//币种维护
            width: 796,
            height: 465,
            buttonAlign:'center',
            minButtonWidth:45,
            resizable:false,
            border:true,
            modal:true,
            closeAction:'destroy',
            //alwaysOnTop:true,
            items: [
                money_me.gridpanel
            ],buttons:[
                { text: common.button.insert,handler:function(){//新增
                        money_me.mewMoneyType();
                    } },
                { text: common.button.todelete,handler:function(){//删除
                        var sel = money_me.gridpanel.getSelectionModel().getSelection();
                        if(sel.length == 0){
                            Ext.showAlert(gz.msg.selectDeleteRecord);//请选择要删除的记录
                            return;
                        }
                        Ext.showConfirm(gz.msg.isDeleteRecord,//确定删除当前记录吗
                            function(but){
                                if(but == 'yes'){
                                    var selectIDs = "";
                                    Ext.Array.each(sel,function(record,index){
                                        selectIDs = selectIDs + "#" + record.get('nstyleid');
                                    })
                                    var map = new HashMap();
                                    map.put("selectIDs",selectIDs);

                                    Rpc({functionId:'GZ00000225',success:function(response,action){
                                            var result = Ext.decode(response.responseText);
                                            if (result.succeed) {
                                                money_me.gridpanel.getStore().load();
                                            }else{
                                                Ext.showAlert(result.message);
                                            }
                                        }},map);
                                }
                            },this);
                    } },
                { text: common.button.cancel,handler:function(){
                        win.hide();
                    } }
            ]
        });
        win.show();


    },

    ////创建新币种
    mewMoneyType:function(){
        var panel = Ext.create('Ext.form.Panel', {
            bodyPadding: 5,
            border:false,
            width: 320,

            layout: 'anchor',
            defaults: {
                anchor: '100%'
            },

            defaultType: 'textfield',
            items: [{
                name: 'nstyleid',
                hidden:true
            },{
                fieldLabel: common.label.name,//符号
                name: 'cname',
                labelWidth:30,
                labelSeparator :'',//去掉后面的冒号
                allowBlank: false,
                validator: function (value) {
                    if(money_me.getStrLength(value)>40){
                        return gz.msg.currencyNameMaxLength.replace("{0}","40");//"最大程度为40位";
                    }else{
                        return true;
                    }
                }
            },{
                fieldLabel: gz.label.symbol,//符号
                name: 'ctoken',
                labelWidth:30,
                labelSeparator :'',//去掉后面的冒号
                allowBlank: false,
                validator: function (value) {
                    if(money_me.getStrLength(value)>4){
                        return gz.msg.currencyNameMaxLength.replace("{0}","4");//"最大程度为4位";
                    }else{
                        return true;
                    }
                }
            },{
                fieldLabel: gz.label.unit,//单位
                name: 'cunit',
                labelWidth:30,
                labelSeparator :'',//去掉后面的冒号
                allowBlank: false,
                validator: function (value) {
                    if(money_me.getStrLength(value)>4){
                        return gz.msg.currencyNameMaxLength.replace("{0}","4");//"最大程度为4位";
                    }else{
                        return true;
                    }
                }
            },{
                xtype:'numberfield',
                hideTrigger:true,
                fieldLabel: gz.label.exchangeRate,//汇率
                labelSeparator :'',//去掉后面的冒号
                name: 'nratio',
                labelWidth:30,
                value:1,//默认汇率为1
                maxLength:16,
                maxLengthText:gz.msg.NumTooLong,
                allowBlank: false,
                regex: /^(-?\d+)(\.\d+)?(e|E)?((-|\+)?\d+)?$/,
                regexText: gz.msg.rateError
            }],
            minButtonWidth:50,
            buttonAlign:'center',
            bbar: [  { xtype: 'tbfill' },{
                xtype: 'button',
                text: common.button.save,//保存
                formBind: true,
                disabled: true,
                handler: function() {
                    var form = this.up('form').getForm();
                    if (form.isValid()) {
                        var formValues = form.getValues();
                        money_me.saveMoney(formValues,"1");
                        win.hide();
                    }
                }
            },{
                xtype: 'button',
                text: common.button.savereturn,//保存&继续
                formBind: true,
                disabled: true,
                handler: function() {
                    var form = this.up('form').getForm();
                    if (form.isValid()) {
                        var formValues = form.getValues();
                        money_me.saveMoney(formValues,"1");
                        form.reset();
                    }
                }
            },{
                xtype: 'button',
                text: common.button.cancel,
                handler: function() {
                    win.hide();
                }
            }, { xtype: 'tbfill' }],
            renderTo: Ext.getBody()
        });

        var win = Ext.create('Ext.window.Window', {
            title: gz.label.newMoney,
            resizable:false,
            border:false,
//			    alwaysOnTop:true,
            modal:true,
            items: panel
        }).show();
    },

    //保存币种,type:1是保存，2是编辑
    saveMoney:function(formValues,type){
        if(trim(formValues.cname)==''){
            Ext.showAlert('币种名称不能为空！');
            money_me.moneyStore.reload();
            return;
        }

        if(formValues.ctoken.indexOf(' ')!=-1){
            Ext.showAlert('符号中不能包含空格！');
            money_me.moneyStore.reload();
            return;
        }

        if(formValues.cunit.indexOf(' ')!=-1){
            Ext.showAlert('单位中不能包含空格！');
            money_me.moneyStore.reload();
            return;
        }
        if(money_me.getStrLength(formValues.cunit)>4){
            Ext.showAlert('单位中输入内容过长！');
            money_me.moneyStore.reload();
            return;
        }
        var map = new HashMap();
        map.put("formValues",formValues);
        Rpc({functionId:'GZ00000224',success:function(response,action){
                var result = Ext.decode(response.responseText);
                if (!result.succeed) {
                    Ext.showAlert(result.message);
                }else{
                    if(type=="1")//保存
                        money_me.moneyStore.load();
                }
            }},map);
    },

    getStrLength:function(str){//获取字节长度
        var n=str.replace(/[^\u0000-\u00ff]/g,"aa").length;
        if(n==null)
            n=0;
        return n;
    },
    //货币维护
    initMoneyDetail:function(nstyleid){
        Ext.require('SalaryTypeUL.moneystyle.InitMoneyDetail', function(){
            Ext.create("SalaryTypeUL.moneystyle.InitMoneyDetail",{nstyleid:nstyleid});
        });
    },
    getStrLength:function(str){
        var n=str.replace(/[^\u0000-\u00ff]/g,"aa").length;
        if(n==null)
            n=0;
        return n;
    }
});