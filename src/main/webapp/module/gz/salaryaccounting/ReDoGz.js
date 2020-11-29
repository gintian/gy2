/**
 * 薪资重发
 */
Ext.define('SalaryUL.ReDoGz',{
    bosdate:'',
    constructor:function(config){
        reDoGz_me = this;
        reDoGz_me.salaryid = config.salaryid;

        var map = new HashMap();
        map.put("salaryid",reDoGz_me.salaryid);
        Rpc({functionId:'GZ000000152',async:false,success:function(form,action){
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                    if(result.salaryIsSubed=="false")
                    {
                        Ext.showConfirm(gz.msg.isContinue, function(optional){
                            if(optional=='yes')
                                reDoGz_me.createSalary();
                        },this)
                    }else{
                        reDoGz_me.createSalary();
                    }
                }else{
                    Ext.showAlert(result.message);
                }
            }},map);
    },
    createSalary:function()
    {
        //导入excel数据store
        var dateStore = Ext.create('Ext.data.Store', {
            fields: ['dataValue', 'dataName'],
            proxy:{
                type: 'transaction',
                functionId:'GZ000000153',
                extraParams:{
                    salaryid:reDoGz_me.salaryid,
                    bosdate:this.bosdate
                },
                reader: {
                    type: 'json',
                    root: 'datelist'
                }
            },
            autoLoad: true
        });

        //导入excel数据store
        var countStore = Ext.create('Ext.data.Store', {
            fields: ['dataValue', 'dataName'],
            proxy:{
                type: 'transaction',
                functionId:'GZ000000153',
                extraParams:{
                    salaryid:reDoGz_me.salaryid,
                    appdate:appdate,
                    oper:'count'
                },
                reader: {
                    type: 'json',
                    root: 'countlist'
                }
            },
            autoLoad: true
        });

        var dateSelect = Ext.widget('combobox', {
            width:180,
            labelAlign:'left',
            labelWidth:60,
            fieldLabel: common.label.appDate,
            store: dateStore,
            editable:false,
            queryMode: 'local',
            displayField: 'dataValue',
            valueField: 'dataName',
            listeners:{
                select:function(combo,records,options){
                    countSelect.clearValue();
                    countStore.load({params:{bosdate:combo.getValue()}});
                }},
            renderTo: Ext.getBody()
        });

        var countSelect = Ext.widget('combobox', {
            width:150,
            labelAlign:'left',
            labelWidth:60,
            fieldLabel: gz.label.ffCount,
            store: countStore,
            editable:false,
            queryMode: 'local',
            displayField: 'dataValue',
            valueField: 'dataName',
            renderTo: Ext.getBody()
        });

        //监听load事件
        dateStore.on('load', function(){
            dateSelect.select(dateStore.getAt(0));
        });
        //监听load事件
        countStore.on('load', function(){
            if(countStore.getCount()>0)
                countSelect.select(countStore.getAt(countStore.getCount()-1));
        });

        reDoGz_me.win=Ext.widget("window",{
            title:gz.label.specifyAppDate,
            height:150,
            resizable:false,
            width:470,
            modal:true,
            closeAction:'destroy',
            layout: {
                type: 'vbox',
                align: 'stretch',
                pack :'center'
            },
            items: [{
                xtype:'panel',
                border:false,
                layout:{
                    type:'hbox',
                    pack:'center'
                },
                defaults:{
                    margin:'15,0,0,0'
                },
                items:[dateSelect,countSelect]
            }],
            bbar:[
                {xtype:'tbfill'},
                {
                    text:common.button.ok,
                    handler:function(){
                        Ext.MessageBox.wait(gz.label.redoGzIng, common.msg.wait)
                        var dataValue = dateSelect.getValue();
                        var countValue = countSelect.getValue();
                        var map = new HashMap();
                        map.put("salaryid",reDoGz_me.salaryid);
                        map.put("bosdate",dataValue);
                        map.put("count",countValue);
                        Rpc({functionId:'GZ000000154',async:false,success:function(form,action){
                                var result = Ext.decode(form.responseText);
                                reDoGz_me.win.close();
                                if(result.succeed){
                                    var ff_bosdate=result.ff_bosdate;
                                    var count = result.count;
                                    Ext.accounting.appdate = ff_bosdate;//业务日期
                                    Ext.accounting.count = count;//次数
                                    accounting.appdate=ff_bosdate;
                                    accounting.count=count;
                                    accounting.reloadStore();
                                }else{
                                    Ext.showAlert(result.message);
                                }
                            }},map);
                    }
                },
                {
                    text:common.button.cancel,
                    handler:function(){
                        reDoGz_me.win.close();
                    }
                },
                {xtype:'tbfill'}
            ]
        });
        reDoGz_me.win.show();
    }
});
