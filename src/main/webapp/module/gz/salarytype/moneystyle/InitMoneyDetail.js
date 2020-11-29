/**
 * 薪资类别-币种详细
 * lis 2015-12-3
 */
Ext.define('SalaryTypeUL.moneystyle.InitMoneyDetail',{
    constructor:function(config){
        moneyDetail_me = this;
        moneyDetail_me.nstyleid = config.nstyleid;
        moneyDetail_me.createSalary();
    },
    createSalary:function()
    {
        //获得薪资项目数据store
        moneyDetail_me.moneyDetailStore = Ext.create('Ext.data.Store', {
            fields:['nstyleid','nitemid','cname','beforenitemid'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000226',
                extraParams:{
                    nstyleid:moneyDetail_me.nstyleid
                },
                reader: {
                    type: 'json',
                    root: 'moneyDetailList'
                }
            },
            autoLoad: true
        });

        //生成薪资项目grid
        moneyDetail_me.gridpanel = Ext.create('Ext.grid.Panel', {
            store: moneyDetail_me.moneyDetailStore,
            width: 685,
            height: 370,
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
                    clicksToEdit: 1
                })
            ],
            columns: [
                //货币名称
                { text: gz.label.moneyName,menuDisabled:true, dataIndex: 'cname',flex: 2,sortable:false,
                    field: {
                        xtype: 'textfield',
                        validator: function (value) {
                            if(moneyDetail_me.getStrLength(value)>10){
                                return gz.msg.currencyNameMaxLength.replace("{0}","10");//"名称最大程度为10位";
                            }else{
                                return true;
                            }
                        },
                        allowBlank: false
                    }},
                //面值
                { text: gz.label.moneyNum,menuDisabled:true, dataIndex: 'nitemid',flex: 1,align:'right',sortable:false,
                    field: {
                        xtype:'numberfield',
                        maxLength:8,
                        maxLengthText:'最大数值长度为8位',
                        hideTrigger:true,
                        //allowBlank: false,
                        regex: /^(-?\d+)(\.\d+)?$/,
                        regexText: gz.msg.inputNum
                    }}
            ],
            listeners:{
                'edit':function(editor, e, eOpts ){
                    if(e.value!=null&&trim(e.value.toString())!=''){
                        if(moneyDetail_me.yData!=e.value)//若原值等于现值 不保存
                            moneyDetail_me.saveMoneyDetail(e.record.data);
                        e.record.commit();
                    }else{
                        Ext.showAlert("货币名称或面值不能为空！");
                        moneyDetail_me.moneyDetailStore.reload();
                        return;
                    }

                },
                'beforeedit':function(editor,e){
                    moneyDetail_me.yData=e.value;
                }
            },
            buttonAlign:'center',
            minButtonWidth:50
        });

        //生成弹出得window
        var win=Ext.widget("window",{
            title:gz.label.moneyMaintain,
            width: 700,
            height: 445,
            buttonAlign:'center',
            minButtonWidth:45,
            resizable:false,
            border:true,
            modal:true,
            closeAction:'destroy',
//			  alwaysOnTop:true,
            items: [
                moneyDetail_me.gridpanel
            ],buttons:[
                { text: common.button.insert,handler:function(){//新增
                        moneyDetail_me.mewMoneyDetailType();
                    } },
                { text: common.button.todelete,handler:function(){//删除
                        var sel = moneyDetail_me.gridpanel.getSelectionModel().getSelection();
                        if(sel.length == 0){
                            Ext.showAlert(gz.msg.selectDeleteRecord);
                            return;
                        }
                        Ext.showConfirm(gz.msg.isDeleteRecord,//确定删除当前记录吗
                            function(but){
                                if(but == 'yes'){
                                    var selectIDs = "";
                                    Ext.Array.each(sel,function(record,index){
                                        selectIDs = selectIDs + "#" + record.get('nitemid');
                                    })
                                    var map = new HashMap();
                                    map.put("selectIDs",selectIDs);
                                    map.put("styleid",moneyDetail_me.nstyleid);
                                    Rpc({functionId:'GZ00000228',success:function(response,action){
                                            var result = Ext.decode(response.responseText);
                                            if (result.succeed) {
                                                moneyDetail_me.gridpanel.getStore().load();
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

    //创建新币种
    mewMoneyDetailType:function(){
        var panel = Ext.create('Ext.form.Panel', {
            bodyPadding: 5,
            border:false,
            width: 320,

            layout: 'anchor',
            defaults: {
                anchor: '100%'
            },
            buttonAlign:'center',
            defaultType: 'textfield',
            items: [{
                name: 'nstyleid',
                value:moneyDetail_me.nstyleid,
                hidden:true
            },{
                name: 'beforenitemid',
                hidden:true
            },{
                fieldLabel: common.label.name,
                name: 'cname',
                labelWidth:30,
                allowBlank: false,
                validator: function (value) {
                    if(moneyDetail_me.getStrLength(value)>10){
                        return gz.msg.currencyNameMaxLength.replace("{0}","10");//"名称最大程度为10位";
                    }else{
                        return true;
                    }
                }
            },{
                xtype:'numberfield',
                hideTrigger:true,
                fieldLabel: gz.label.denomination,
                name: 'nitemid',
                labelWidth:30,
                maxLength:8,
                maxLengthText:'最大数值长度为8位',
                allowBlank: false,
                regex: /^(-?\d+)(\.\d+)?$/,
                regexText: gz.msg.inputNum
            }],
            minButtonWidth:50,
            bbar: [  { xtype: 'tbfill' },{
                text: common.button.save,//保存
                formBind: true,
                disabled: true,
                handler: function() {
                    var form = this.up('form').getForm();
                    if (form.isValid()) {
                        var formValues = form.getValues();
                        moneyDetail_me.saveMoneyDetail(formValues);
                        win.hide();
                    }
                }
            },{
                text: common.button.savereturn,//保存&继续
                formBind: true,
                disabled: true,
                handler: function() {
                    var form = this.up('form').getForm();
                    if (form.isValid()) {
                        var formValues = form.getValues();
                        moneyDetail_me.saveMoneyDetail(formValues);
                        form.reset();
                    }
                }
            },{
                text: common.button.cancel,
                handler: function() {
                    win.hide();
                }
            }, { xtype: 'tbfill' }],
            renderTo: Ext.getBody()
        });

        var win = Ext.create('Ext.window.Window', {
            title: gz.label.newMoneyNum,
            resizable:false,
            border:false,
//			    alwaysOnTop:true,
            modal:true,
            items: panel
        }).show();
    },

    //保存
    saveMoneyDetail:function(formValues){
        var map = new HashMap();
        map.put("formValues",formValues);
        var myMask = new Ext.LoadMask({
            id:'mask',
            msg    : 'Please wait...',
            target : moneyDetail_me.gridpanel
        });
        myMask.show();

        Rpc({functionId:'GZ00000227',async:false,success:function(response,action){
                var result = Ext.decode(response.responseText);
                var sb = result.sb;
                if (result.succeed&&sb.length==0) {
                    if(moneyDetail_me.gridpanel.getStore()!=null){
                        myMask.destroy();
                        moneyDetail_me.gridpanel.getStore().load();
                    }
                }else if(result.succeed&&sb.length>0){

                    Ext.showAlert(sb);
                    myMask.destroy();
                    moneyDetail_me.moneyDetailStore.reload();
                }else{
                    Ext.showAlert(result.message);
                    myMask.destroy();
                    moneyDetail_me.moneyDetailStore.reload();
                }
            }},map);
    },
    getStrLength:function(str){
        var n=str.replace(/[^\u0000-\u00ff]/g,"aa").length;
        if(n==null)
            n=0;
        return n;
    }
});