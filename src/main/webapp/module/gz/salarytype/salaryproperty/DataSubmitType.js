/**
 * 薪资类别-薪资属性-数据提交方式
 * lis 2015-12-08
 */
Ext.define('SalaryTypeUL.salaryproperty.DataSubmitType',{
    constructor:function(config){
        dataSubmitType_me = this;
        dataSubmitType_me.salaryid = config.salaryid;
        var result = config.result;
        dataSubmitType_me.buf = result.buf;//高级按钮
        dataSubmitType_me.fieldsetlist = result.fieldsetlist;//人员范围
        dataSubmitType_me.isUpdateSet = result.isUpdateSet;//限制用户管理范围
        dataSubmitType_me.subNoShowUpdateFashion = result.subNoShowUpdateFashion;//共享方式
        dataSubmitType_me.subNoPriv = result.subNoPriv;//人员库
        dataSubmitType_me.allowEditSubdata = result.allowEditSubdata;//人员库

        dataSubmitType_me.createSalary();
    },
    createSalary:function()
    {
        var states = Ext.create('Ext.data.Store', {
            fields: ['typeName', 'type'],
            data : [
                {"typeName":gz.label.noChangeRecord, "type":"2"},//当前记录不变
                {"typeName":gz.label.updateThisRecord, "type":"0"},//更新当前记录
                {"typeName":gz.label.newRecord, "type":"1"}//新增记录
            ]
        });

        //数据提交类别
        var typeCombox = Ext.create('Ext.form.ComboBox', {
            store: states,
            editable: false, //总共就三种方式，只能选择，不让修改了
            queryMode: 'local',
            displayField: 'typeName',
            valueField: 'type',
            name:'sss'
        });

        //数据提交store
        var store = Ext.create('Ext.data.Store', {
            fields:['setid','name','type']
        });

        Ext.each(dataSubmitType_me.fieldsetlist,function(obj,index){
            store.insert(index,[{setid:obj.setid,name: obj.name,type: obj.type}]);
        });

        //数据提交表格
        dataSubmitType_me.gridpanel = Ext.create('Ext.grid.Panel', {
            store:store,
            height: 270,
            //forceFit:true,
            border:true,
            columnLines:true,
            rowLines:true,
            buttonAlign:'left',
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1
                })
            ],
            columns: [
                { text: gz.label.fieldSetName,menuDisabled:true, dataIndex: 'name',flex:1},//子集名称
                { text: gz.label.operation,menuDisabled:true, dataIndex: 'type',flex:1,editor:typeCombox,//操作
                    renderer:function(v,s,record){
                        var opt = "1";
                        if(v == '0'){
                            opt = gz.label.updateThisRecord;
                        }else if(v == '1'){
                            opt = gz.label.newRecord;
                        }else{
                            opt = gz.label.noChangeRecord;
                        }
                        return opt;
                    }}

            ],
            listeners: {
                'edit':function(){
                    var flag=false;
                    store.each(function(record){
                        if(record.get('type') == "0"){
                            flag = true;
                        }
                    });
                    if(flag){
                        checkPanel.queryById('advanceId').show();
                    }else{
                        checkPanel.queryById('advanceId').hide();
                    }
                }
            }
        });

        var checkPanel = Ext.widget({
            xtype:'panel',
            border:false,
            padding:'5 0 0 0',
            layout:'vbox',
            items:[{
                xtype:'button',
                itemId:'advanceId',
                hidden:dataSubmitType_me.isUpdateSet=='none'?true:false,
                text:gz.label.senior,//高级
                handler:function(){
                    var setIds = "";
                    store.each(function(record){
                        if(record.get('type')=="0")
                            setIds += "/" + record.get('setid')
                    });
                    dataSubmitType_me.advanceUpdate(setIds);
                }
            },{
                xtype     : 'checkbox',
                padding:'5 0 0 0',
                boxLabel  : gz.label.submitNoShowOpt,//提交时不显示数据操作方式设置
                name      : 'subNoShowUpdateFashion',
                inputValue: '1',
                id:'subNoShowUpdateFashion',
                checked:dataSubmitType_me.subNoShowUpdateFashion==1?true:false
            },
                {
                    xtype     : 'checkbox',
                    boxLabel  : gz.label.submitNoJudgmentSetAndFieldPriv,//数据提交入库不判断子集及指标权限
                    name      : 'subNoPriv',
                    inputValue: '1',
                    id:'subNoPriv',
                    checked:dataSubmitType_me.subNoPriv==1?true:false
                },
                {
                    xtype     : 'checkbox',
                    boxLabel  : gz.label.allowEditData,//允许修改已归档数据
                    name      : 'allowEditSubdata',
                    inputValue: '1',
                    id:'allowEditSubdata',
                    checked:dataSubmitType_me.allowEditSubdata==1?true:false
                }]
        });

        var scopePanel = Ext.widget({
            xtype:'panel',
            border:false,
            items:[dataSubmitType_me.gridpanel,checkPanel]
        });

        //将当前panel渲染到tab页
        salaryProperty_me.tabs.child('#dataSubmitId').add(scopePanel);
    },

    //当存在更新当前数据时弹出“高级”选择框
    advanceUpdate:function(setIds){

        var states = Ext.create('Ext.data.Store', {
            fields: ['name', 'flag'],
            data : [
                {"name":gz.label.accumulationUpdate, "flag":"0"},//累加更新
                {"name":gz.label.replaceUpdate, "flag":"1"}//替换更新
            ]
        });

        //据提交类别男男女女


        var updateTypeCombox = Ext.create('Ext.form.ComboBox', {
            store: states,
            queryMode: 'local',
            name:'flag',
            displayField: 'name',
            valueField: 'flag'
        });
        if(!!!dataSubmitType_me.updateObj)
            dataSubmitType_me.updateObj=new Array();
        //子集数据store
        var store = Ext.create('Ext.data.Store', {
            fields:['itemid','itemdesc','flag'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000021',
                extraParams:{
                    salaryid:dataSubmitType_me.salaryid,
                    updateObj:dataSubmitType_me.updateObj,
                    sets:setIds
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true
        });

        //生成子集表格
        var gridpanel = Ext.create('Ext.grid.Panel', {
            store:store,
            height: 305,
            //forceFit:true,
            border:true,
            columnLines:true,
            rowLines:true,
            buttonAlign:'left',
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1
                })
            ],
            columns: [
                { text: gz.label.FieldName,menuDisabled:true, dataIndex: 'itemdesc',flex:2},//指标名称
                { text: gz.label.updateType,menuDisabled:true, dataIndex: 'flag',flex:1,editor:updateTypeCombox,//更新方式
                    renderer:function(value){
                        var opt = "";
                        if(value == '0')
                            opt = gz.label.accumulationUpdate;//累加更新
                        else
                            opt = gz.label.replaceUpdate;//替换更新
                        return opt;
                    }
                }

            ]
        });

        var win = Ext.widget({
            title:gz.label.updateSeniorSet,//更新高级设置
            xtype:'window',
            height: 400,
            width: 400,
            minButtonWidth:50,
            layout: 'fit',
            items:gridpanel,
            buttons:[
                {xtype:'tbfill'},
                {
                    text:common.button.ok, //确定
                    handler:function(){
                        dataSubmitType_me.enter(gridpanel);
                        win.close();
                    }

                },
                {
                    text:common.button.cancel, //取消
                    handler:function(){
                        win.close();
                    }
                },{xtype:'tbfill'}
            ]
        }).show();
    },

    //“高级”弹出框，单击确定，保存记录
    enter:function(grid)
    {
        var updateObj = new Array();
        grid.getStore().each(function(record,index){
            var map=new HashMap();
            map.put("itemid",record.get('itemid'));
            map.put("itemdesc",record.get('itemdesc'));
            map.put("flag",record.get('flag'));
            updateObj[index]=map;
        })
        dataSubmitType_me.updateObj = updateObj;
    }
});