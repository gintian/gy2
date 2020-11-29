/**
 * 薪资发放-导入数据-对比页面-重复数据弹出页面
 */
Ext.define('SalaryUL.inout.ShowReapeatData',{
    constructor:function(config){
        me3 = this;
        me3.fields = new Array();//表格表头
        me3.relationItem = config.relationItem;//关联数据
        me3.salaryid = config.salaryid; //薪资类别名称
        me3.fileid = config.fileid;
        me3.initColumns();
    },
    //初始化列
    initColumns:function(){
        me3.columns = new Array();
        var obj = null;
        for(var i=0;i<me3.relationItem.length;i++){
            me3.fields[i] = "a" + i;
            obj = new Object();
            obj.header=me3.relationItem[i].itemid;
            obj.align=me3.relationItem[i].align;
            obj.dataIndex="a" + i;
            obj.menuDisabled=true;
            me3.columns[i]=obj;
        }
        obj = new Object();
        obj.header="个数";
        obj.dataIndex="acount";
        obj.menuDisabled=true;
        obj.align="right";
        me3.columns[me3.columns.length]=obj;

        me3.fields[me3.fields.length] = "acount";
        me3.createSalary();
    },
    createSalary:function()
    {
        var repeatStore = Ext.create('Ext.data.Store', {
            fields:me3.fields,
            proxy:{
                type: 'transaction',

                functionId:'GZ00000045',
                extraParams:{
                    salaryid:me3.salaryid,
                    fileid:me3.fileid,
                    relationItem:me3.relationItem
                },
                reader: {
                    type: 'json',
                    root: 'repeatDataList'
                }
            },
            autoLoad: true
        });

        //放置表格的panel
        var panel = Ext.widget("gridpanel",{
            store: repeatStore,
            height:"100%",
            width:"100%",
            columnLines:true,
            rowLines:true,
            columns: me3.columns
        });

        var vs = Ext.getBody().getViewSize();
        var win = Ext.create('Ext.window.Window', {
            title: gz.button.repeatData,
            width:1000,
            x:500,
            height:vs.height,
            layout:'fit',
            minButtonWidth:40,
            resizable:false,
            modal:true,
            items: panel
        }).show();
    }
});