/**
 * 薪资类别-薪资项目弹出框-弹出添加薪资项目
 * lis 2015-10-20
 */
Ext.define('SalaryTypeUL.salaryitem.AddSalaryItem',{
    constructor:function(config){
        addsalaryitem_me = this;
        addsalaryitem_me.salaryid = config.salaryid;
        addsalaryitem_me.createSalary();
    },
    createSalary:function()
    {
        //子集数据store
        var comBoxStore = Ext.create('Ext.data.Store',
            {
                fields:['dataName','dataValue'],
                proxy:{
                    type: 'transaction',
                    functionId:'GZ00000212',
                    extraParams:{
                        salaryid:addsalaryitem_me.salaryid,
                        opt:'1'
                    },
                    reader: {
                        type: 'json',
                        root: 'list'
                    }
                },
                autoLoad: true
            });

        //子集列表数据store
        var comBox = Ext.widget('combo',{
            store:comBoxStore,
            width: 280,
            queryMode: 'local',
            repeatTriggerClick : true,
            editable: false,
            forceSelection: true,
            displayField: 'dataName',//显示的值
            valueField: 'dataValue',//隐藏的值
            listeners:{
                select:function(combo,records){
                    addsalaryitem_me.changeSelect(combo.getValue());
                }
            }
        });

        var store = Ext.create('Ext.data.Store', {
            fields:['dataName','dataValue'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000212',
                extraParams:{
                    opt:'2'
                },
                reader: {
                    type: 'json',
                    root: 'list'
                }
            }
        });

        //生成薪资项目表格
        addsalaryitem_me.gridpanel = Ext.create('Ext.grid.Panel', {
            store:store,
            height: 339,
            border:true,
            multiSelect:true,
            forceFit:true,
            hideHeaders:true,
            bufferedRenderer:false,//一起把数据去拿过来，不用假分页模式
            columns: [
                { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataName'},//项目名称
                { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataValue',hidden:true}

            ],
            listeners:{
                'celldblclick':function(grid, td,cellIndex,record,tr,rowIndex,e){
                    addsalaryitem_me.addMode();
                }
            },
            renderTo:Ext.getBody()
        });

        //目标数据store
        var aimstore = Ext.create('Ext.data.Store', {
            fields:['dataName','dataValue']
        });

        //存放目标数据的grid
        addsalaryitem_me.aimgridpanel = Ext.create('Ext.grid.Panel', {
            store:aimstore,
            height: 430,
            border:true,
            multiSelect:true,
            forceFit:true,
            bufferedRenderer:false,//一起把数据去拿过来，不用假分页模式
            hideHeaders:true,
            columns: [
                { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataName'},//项目名称
                { text: gz.label.itemName,menuDisabled:true, dataIndex: 'dataValue',hidden:true}

            ],
            listeners:{
                'celldblclick':function(grid, td,cellIndex,record,tr,rowIndex,e){
                    addsalaryitem_me.delMode();
                }
            },
            renderTo:Ext.getBody()
        });

        //生成弹出得window
        addsalaryitem_me.win=Ext.widget("window",{
            title : gz.label.newItemField,
            width: 650,
            height: 450,
            minButtonWidth:45,
            border:true,
            bodyStyle: 'background:#ffffff;',
            modal:true,
            closeAction:'destroy',
            layout: 'border',
            items: [{
                region:'west',
                xtype: 'panel',
                border:false,
                margin: '5',
                width: 280,
                items:[comBox,addsalaryitem_me.gridpanel]
            },{
                region: 'east',
                xtype: "panel",
                border:false,
                margin: '5',
                width: 280 ,
                layout: 'fit',
                items:[addsalaryitem_me.aimgridpanel]

            },{
                region: 'center',
                xtype: 'panel',
                border:false,
                width: 50,
                layout: {
                    type: 'vbox',
                    align: 'center',
                    pack :'center'
                },
                defaults:{
                    margin:'5,0,0,0'
                },
                items:[
                    {
                        xtype:'button',
                        text:common.button.addfield, //添加
                        handler:function(){
                            addsalaryitem_me.addMode();
                        }
                    },
                    {
                        xtype:'button',
                        text:common.button.todelete, //删除
                        handler:function(){
                            addsalaryitem_me.delMode();
                        }
                    }
                ]
            }],
            buttons:[
                {xtype:'tbfill'},
                {
                    text:common.button.ok, //确定
                    handler:function(){
                        addsalaryitem_me.saveItems();
                    }
                },
                {
                    text:common.button.cancel, //取消
                    handler:function(){
                        addsalaryitem_me.win.close();
                    }
                },{xtype:'tbfill'}
            ]
        });
        addsalaryitem_me.win.show();

        //下拉框初始化显示第一个
        comBoxStore.on('load',function(store,records,options){
            if(records.length>1){
                comBox.select(records[1].data.dataValue);
                addsalaryitem_me.changeSelect(records[1].data.dataValue);
            }
        });
    },

    //将左侧勾选的子集指标添加到右侧panel
    addMode:function(){
        var records = addsalaryitem_me.gridpanel.getSelectionModel().getSelection();
        if(records.length == 0){
            Ext.showAlert(gz.msg.selectAddObj);
            return;
        };
        Ext.Array.each(records, function(record, index, countriesItSelf) {
            var modeValue = record.get('dataValue');
            //生成要插入的model对象
            var aimMode = {
                dataName:record.get('dataName'),
                dataValue:record.get('dataValue')
            };
            var rowlength = addsalaryitem_me.aimgridpanel.getStore().data.length;
            var isAdd = true;
            addsalaryitem_me.aimgridpanel.getStore().each(function(item,index,count){ //遍历每一条数据
                if(item.get('dataValue').indexOf(modeValue)!="-1"){
                    isAdd = false;
                    return;
                }
            })
            if(isAdd)
                addsalaryitem_me.aimgridpanel.getStore().insert(rowlength, aimMode);//将选中对象数据插入到指定位置
        });
    },

    //删除右侧已选指标
    delMode:function(){
        var records = addsalaryitem_me.aimgridpanel.getSelectionModel().getSelection();
        if(records.length == 0){
            Ext.showAlert(gz.msg.selectDelObj);
            return;
        }
        Ext.Array.each(records, function(record) {
            addsalaryitem_me.aimgridpanel.getStore().remove(record);
        });
    },

    //保存右侧已经选好的薪资项目
    saveItems:function(){
        var salarySetIDs = ""
        addsalaryitem_me.aimgridpanel.getStore().each(function(item,index,count){ //遍历每一条数据
            salarySetIDs = salarySetIDs + "/" + item.get('dataValue');
        });
        var map = new HashMap();
        map.put("salarySetIDs",salarySetIDs);
        map.put("salaryid",addsalaryitem_me.salaryid);
        map.put("opt","3");

        Rpc({functionId:'GZ00000212',success:function(response,action){
                var success = Ext.decode(response.responseText).succeed;
                if (success) {
                    addsalaryitem_me.win.close();
                    salaryitem_me.salaryItemStore.load({
                        scope: salaryitem_me,
                        callback: function(records, operation, success) {
                            var sel = salaryitem_me.gridpanel.getSelectionModel();
                            sel.select(salaryitem_me.salaryItemStore.count()-1, true);//定位最后一行
                        }
                    });
                } else {
                    Ext.showAlert(response.responseText.message);
                }
            }},map);
    },

    changeSelect:function(fieldSetid){
        //获得薪资项目数据store
        addsalaryitem_me.gridpanel.getStore().load({
            params:{
                salaryid:addsalaryitem_me.salaryid,
                fieldSetid:fieldSetid,
                opt:'2'
            }
        });
    }
})