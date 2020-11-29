/**
 * 薪资发放-导入数据-对比页面-读取对应方案弹出框
 */
Ext.define('SalaryUL.inout.ReadRelation',{
    extend:'Ext.window.Window',
    win:'',//页面要生成的窗口
    relation_me:'',
    cellEdit:'',
    constructor:function(config){
        relation_me = this;
        relation_me.salaryid = config.salaryid;
        this.createSalary();
    },
    createSalary:function()
    {
        //对应方案的数据集
        var store = Ext.create('Ext.data.Store', {
            fields:['id', 'name','seq'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000046',
                extraParams:{
                    salaryid:relation_me.salaryid
                },
                reader: {
                    type: 'json',
                    root: 'schemeList'
                }
            },
            autoLoad: true
        });
        //显示对应方案的panel
        var relationGrid = Ext.widget("gridpanel",{
            id:'relationPanel',
            store: store,
            rowLines:true,
            columnLines:true,
            width: 250,
            height:330,
            flex: 1,
            buttonAlign : 'center',
            bodyStyle: 'background:#ffffff;',
            multiSelect : true,
            selModel: Ext.create("Ext.selection.CheckboxModel", {
                mode: "multi",//multi,simple,single；默认为多选multi
                checkOnly: false,//如果值为true，则只用点击checkbox列才能选中此条记录
                allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
                enableKeyNav: true
            }),
            columns: [
                {header: 'id', dataIndex: 'id', hidden: true},
                {text: '方案名称', dataIndex: 'name',menuDisabled:true,flex: 2,
                    field: {
                        xtype: 'textfield',
                        allowBlank: false
                    }
                }
            ],
            selType: 'cellmodel',
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    id:'cellId',
                    clicksToEdit: 1,
                    listeners:{
                        'edit':function(editor, e){
                            relation_me.saveName(editor,e);//保存修改方案名称
                        }
                    }
                })
            ]
        });

        //弹出窗口
        win=Ext.widget("window",{
            title: gz.label.importEelation,//导入方案
            layout:'anchor',
            minButtonWidth:30,//为fbar按钮默认宽度
            modal:true,
            resizable:false,
            border:false,
            bodyStyle: 'background:#ffffff;',
            closeAction:'destroy',
            items: [{
                xtype:'panel',
                border:false,
                layout:{
                    type:'hbox',
                    padding:'5',
                    pack:'end',
                    align:'stretch'
                },
                items:[relationGrid]
            }],
            fbar: [{xtype:'tbfill'},{
                xtype: 'button',
                text:common.button.ok,//确定
                handler:function(){
                    var records  = relationGrid.getSelectionModel().getSelection();
                    var store = relationGrid.getStore();
                    if (records.length == 0) {
                        Ext.showAlert(gz.msg.selectPlan);
                    }else if(records.length > 1){
                        Ext.showAlert(gz.msg.selectOnePlan);
                    }else{
                        win.close();
                        import_me.readRelation(records[0].get('id'));
                    }
                }
            },
                {
                    xtype: 'button',
                    text:common.button.todelete,
                    handler:function(){
                        relation_me.deleteRelation(relationGrid);//删除方案
                    }
                },
                {
                    xtype: 'button',
                    text:common.button.cancel,
                    handler:function(){
                        win.close();
                    }
                },{xtype:'tbfill'}]
        });
        win.show()
    },

    //上下移动方案
    remove:function(node,data,model,dropPosition,dropHandlers){
        var panel = Ext.getCmp('relationPanel');
        if(data.records.length>1){
            Ext.showAlert(gz.msg.forbidMultiMove);
            panel.getStore().load();
        }else{
            var ori_id=data.records[0].get("id");
            var ori_seq = data.records[0].get('seq');
            var to_id=model.get('id');
            var to_seq = model.get('seq');
            var map = new HashMap();
            map.put("ori_id",ori_id);
            map.put("ori_seq",ori_seq);
            map.put("to_id",to_id);
            map.put("to_seq",to_seq);
            map.put("oper","move");
            map.put("salaryid",relation_me.salaryid);
            Rpc({functionId:'GZ00000047',success:function(response,action){
                    var success = Ext.decode(response.responseText).succeed;
                    if (!success) {
                        panel.getStore().load();
                        Ext.showAlert(gz.msg.moveFalse);
                    }
                }},map);
        };
    },

    //保存方案修改后的名称
    saveName:function(editor,e){
        var record = e.record;
        var originalValue = e.originalValue;
        var name=record.get('name');
        if(trim(name)==''){
            Ext.showAlert(gz.msg.nameRelationNotNull);
            Ext.getCmp('relationPanel').store.reload();
            return;
        }
        if(relation_me.fucCheckLength(name)>30){
            Ext.showAlert(gz.msg.NameTooLong);
            return;
        }
        if (Ext.util.Format.trim(originalValue) != Ext.util.Format.trim(record.get('name'))) {//有被修改的数据
            var map = new HashMap();
            map.put("id",record.get('id'));
            map.put("name",record.get('name'));
            map.put("salaryid",relation_me.salaryid);
            map.put("oper","0");
            Rpc({functionId:'GZ00000047',success:function(response,action){
                    var result = Ext.decode(response.responseText);

                    if (result.succeed) {
                        if(result.msg == "0")
                            record.commit();// 向store提交修改数据，页面效果
                        else{
                            Ext.showAlert(gz.msg.isNotUniq);
                        }
                    } else {
                        Ext.showAlert(gz.msg.changeFailed+"！");
                    }
                }},map);
        }
    },
    //删除选中的方案
    deleteRelation:function(relationGrid){
        var records  = relationGrid.getSelectionModel().getSelection();
        var store = relationGrid.getStore();
        if (records.length == 0) {
            Ext.showAlert(gz.msg.selectDelRelation);
        }else{
            Ext.Msg.confirm(common.button.promptmessage, gz.msg.confirmDelRelation, function(button, text) {
                if (button == "yes") {
                    var ids = [];
                    Ext.each(records ,function(record){
                        ids.push(record.data.id);
                    });

                    var map = new HashMap();
                    map.put("ids",ids);
                    map.put("oper","1");
                    map.put("salaryid",relation_me.salaryid);
                    Rpc({functionId:'GZ00000047',success:function(response,action){
                            var success = Ext.decode(response.responseText).succeed;
                            if (success) {
                                Ext.Array.each(records, function(record) {
                                    store.remove(record);// 后台删除后页面也删除
                                });
                            } else {
                                Ext.showAlert(common.label.deleteFailed+"！");
                            }
                        }},map);
                }
            })
        }
    },
    fucCheckLength:	function (strTemp){
        var i,sum;
        sum=0;
        for(i=0;i<strTemp.length;i++){
            if ((strTemp.charCodeAt(i)>=0) && (strTemp.charCodeAt(i)<=255)){
                sum=sum+1;
            }else{
                sum=sum+2;
            }
        }
        return sum;
    }
});