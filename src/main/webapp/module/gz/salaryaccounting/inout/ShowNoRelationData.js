/**
 * 薪资发放-导入数据-对比页面-显示无对应数据的弹出框
 */
Ext.define('SalaryUL.inout.ShowNoRelationData',{
    constructor:function(config){
        noRelation_me = this;
        noRelation_me.fields = config.fields; //store的field
        noRelation_me.relationItem = config.relationItem;//store的field
        noRelation_me.salaryid = config.salaryid;
        noRelation_me.fileid=config.fileid;
        noRelation_me.initColumns();
    },

    initColumns:function(){
        noRelation_me.columns = new Array();
        for(var i=0;i<noRelation_me.fields.length;i++){
            var obj = new Object();
            obj.header=noRelation_me.fields[i].get('itemid');
            obj.dataIndex=noRelation_me.fields[i].get('itemid');
            obj.align=noRelation_me.fields[i].get('align');
            obj.menuDisabled=true;
            for(var j=0;j<noRelation_me.relationItem.length;j++){
                if(noRelation_me.relationItem[j].itemid==noRelation_me.fields[i].get('itemid')){
                    obj.locked=true;
                    break;
                }else{
                    obj.locked=false;
                }
            }
            noRelation_me.columns[i]=obj;
        }
        noRelation_me.createSalary();
    },
    createSalary:function()
    {
        var repeatStore = Ext.create('Ext.data.Store', {
            fields:noRelation_me.fields,
            proxy:{
                type: 'transaction',

                functionId:'GZ00000044',
                extraParams:{
                    salaryid:noRelation_me.salaryid,
                    fileid:noRelation_me.fileid,
                    relationItem:noRelation_me.relationItem
                },
                reader: {
                    type: 'json',
                    root: 'noRelationData'
                }
            },
            listeners:{
                load : function( store, records, successful, operation){
                    var responseText = Ext.util.JSON.decode(operation._response.responseText);
                    if(responseText.succeed == false) {
                        Ext.showAlert(responseText.message);
                    }else {
                        win.show();
                    }
                }
            },
            autoLoad: true
        });

        var panel = Ext.create('Ext.grid.Panel', {
            store: repeatStore,
            columns:noRelation_me.columns,
            height:"100%",
            width:"100%",
//    	    layout:'fit',
            columnLines:true,
            rowLines:true
        });
        var vs = Ext.getBody().getViewSize();
        var win = Ext.create('Ext.window.Window', {
            title: gz.label.noRelationData,
            width:1000,
            x:500,
            height:vs.height,
            layout:'fit',
            minButtonWidth:40,
            resizable:false,
            modal:true,
            items: panel
        });

    }
});