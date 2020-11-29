/**
 *批量引入
 */
Ext.define('SalaryUL.BatchImport',{
    constructor:function(config){
        batchImportScope=this;
        batchImportScope.salaryid = config.salaryid;//薪资id
        batchImportScope.imodule = config.imodule;//模块号 1：保险  否则是薪资
        batchImportScope.viewtype = config.viewtype;//0 薪资发放 1 薪资审批
        batchImportScope.appdate = config.appdate;//业务日期
        batchImportScope.count = config.count;//次数
        batchImportScope.type=config.type;
        //对引入方式初始化赋值，年、月为业务日期
        var map = new HashMap();
        map.put("appdate",batchImportScope.appdate);
        map.put("opt","1");
        Rpc({functionId:'GZ00000091',async:false,success:function(response){
                var value = response.responseText;
                var map = Ext.decode(value);
                this.thisyear = map.year;
                this.thismonth = map.month;
            }},map);
        this.init();
    },
    init:function(){
        var store = Ext.create('Ext.data.Store', {
            fields:['itemid','itemdesc'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000091',
                extraParams:{
                    salaryid:batchImportScope.salaryid,
                    appdate:batchImportScope.appdate,
                    type:batchImportScope.type,
                    opt:'2'
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true
        });
        store.load();

        var panel = Ext.create('Ext.grid.Panel', {
            //title:'请指定需要引入项目',
            width:450,
            height:320,
            store:store,
            id:'panelId',
            selType: "checkboxmodel",
            columnLines:true,
            rowLines:true,
            columns: [{
                text: '项目名称',
                dataIndex: 'itemdesc',
                id:'itemdesc',
                sortable:false,//列排序功能禁用
                menuDisabled:true,
                flex:5
            },{
                text: '薪资id',
                dataIndex: 'itemid',
                id:'itemid',
                sortable:false,//列排序功能禁用
                menuDisabled:true,
                flex:5
            }],
            listeners:{
                'render':function(){
                    Ext.getCmp('itemid').hide();
                },
                'afterrender':function(){
                    //重新导入时，引入方式为档案数据，引入方式不用显示
                    if(batchImportScope.type=='1'){
                        Ext.getCmp('importtypeid').hide();
                        Ext.getCmp('rd3').checked=true;
                        Ext.getCmp('panelId').height=385;
                    }
                }
            }
        });

        var win = Ext.create('Ext.window.Window',{
            title:batchImportScope.type=='0'?'请选择需引入的项目和引入方式':'重新导入',
            id:'batchImportWinId',
            width: 460,
            height: 460,
            resizable: false,
            modal: true,
            border:false,
            items:[{
                xtype:'panel',
                layout:'column',
                border:false,
//	                      	style:'margin-left:10px;margin-top:3px',
                items:[{
                    xtype:'fieldset',
                    title:'引入方式',
                    width:450,
                    id:'importtypeid',
                    height:50,
                    items:[{
                        defaultType:'radiofield',
                        layout:'table',
                        name:'rd',
                        fieldLabel:'公式类型',
                        border:false,
                        width:450,
                        labelAlign:'left',
                        labelWidth:50,
                        labelSeparator:null,
                        items:[
                            { boxLabel: '同月上次', name: 'rd',id:'rd1', inputValue: '1',width:75,checked:true},
                            { boxLabel: '上月同次', name: 'rd',id:'rd2', inputValue: '2',width:75 },
                            { boxLabel: '', name: 'rd', id:'rd4',inputValue: '4',width:18},
                            {
                                xtype:'numberfield',
                                name: "year",
                                labelAlign:'left',
                                maxValue:2030,
                                minValue:2001,
                                width:55,
                                value:thisyear,
                                id:'currentyear',
                                labelWidth:1,
                                hideTrigger: false,
                                keyNavEnabled: true,
                                mouseWheelEnabled: true,//鼠标滚动的效果
                                labelSeparator:null,
                                step: 1,
                                listeners:{
                                    'focus':function(){
                                        //选中此组件时，rd3的单选框同时也被选中
                                        Ext.getCmp('rd4').setValue(true);
                                    }
                                }
                            },{
                                xtype:'label',
                                text:'年',
                                style:'margin-right:5px'
                            },{
                                xtype:'numberfield',
                                name: "month",
                                maxValue: 12,
                                minValue: 1,
                                width:40,
                                id:'currentmonth',
                                value: thismonth,
                                hideTrigger: false,
                                labelSeparator:null,
                                keyNavEnabled: true,
                                mouseWheelEnabled: true,//鼠标滚动的效果
                                step: 1,
                                listeners:{
                                    'focus':function(){
                                        //选中此组件时，rd3的单选框同时也被选中
                                        Ext.getCmp('rd4').setValue(true);
                                    }
                                }
                            },{
                                xtype:'label',
                                text:'月',
                                style:'margin-right:5px'
                            },{
                                xtype:'numberfield',
                                name: "count",
                                id:'currentcount',
                                minValue: 1,
                                maxValue:20,
                                width:40,
                                value: 1,
                                hideTrigger: false,
                                keyNavEnabled: true,
                                mouseWheelEnabled: true,//鼠标滚动的效果
                                step: 1,
                                listeners:{
                                    'focus':function(){
                                        //选中此组件时，rd4的单选框同时也被选中
                                        Ext.getCmp('rd4').setValue(true);
                                    }
                                }
                            },{
                                xtype:'label',
                                text:'次',
                                style:'margin-right:10px'
                            },
                            { boxLabel: '档案数据', name: 'rd',id:'rd3', inputValue: '3'}]
                    }]
                },panel]
            }] ,
            buttonAlign:'center',
            buttons:[{
                text:'确定',
                listeners:{
                    'click':function(){
                        var importtype = '';
                        var year = '';
                        var month = '';
                        var count1 = '';
                        var busiDateSome = new Array();//某年月 次的组成的数组
                        if(Ext.getCmp('rd1').checked){
                            importtype='1';
                        }
                        if(Ext.getCmp('rd2').checked){
                            importtype='2';
                        }
                        if(Ext.getCmp('rd4').checked){
                            importtype='4';
                            year = Ext.getCmp('currentyear').getValue();
                            month = Ext.getCmp('currentmonth').getValue();
                            count1 = Ext.getCmp('currentcount').getValue();
                            busiDateSome[0]=year+'';
                            busiDateSome[1]=month+'';
                            busiDateSome[2]=count1+'';

                            if(year==null||month==null||count1==null)
                            {
                                Ext.showAlert( "年月次数不能为空!");
                                return;
                            }

                        }
                        if(Ext.getCmp('rd3').checked){
                            importtype='3';
                        }
                        var items = batchImportScope.getSelect(Ext.getCmp('panelId'),'itemid');
                        if(items.length==0){
                            Ext.showAlert( "请选择需引入的项目名称!");
                            return;
                        }
                        var map = new HashMap();
                        map.put("salaryid",batchImportScope.salaryid);
                        map.put("items",items);
                        map.put("importtype",importtype);
                        map.put("busiDateSome",busiDateSome);
                        map.put("count",batchImportScope.count);
                        map.put("appdate",batchImportScope.appdate);
                        map.put("viewtype",batchImportScope.viewtype);
                        Ext.MessageBox.wait("正在导入数据，请稍候...", "等待");
                        LRpc({functionId:'GZ00000092',success:batchImportScope.importOK},map);
                    }
                }

            },{
                text:'取消',
                listeners:{
                    'click':function(){
                        win.close();
                    }
                }
            }]

        });
        win.show();


    },
    getSelect:function (grid, col) { //获取选中grid的列
        var st= new Array();
        for (var i = 0; i < grid.getSelectionModel().getSelection().length; i++) {
            if(grid.getSelectionModel().getSelection()[i].get(col)!=null && grid.getSelectionModel().getSelection()[i].get(col).length>0){
                st[i]=grid.getSelectionModel().getSelection()[i].get(col);
            }
        }
        return st;
    },
    importOK:function(form,response){
        Ext.MessageBox.close();
        Ext.getCmp('batchImportWinId').close();
        var result = Ext.decode(form.responseText);
        if(result.viewtype=='0')
            accounting.loadStore();
        else if(result.viewtype=='1')
            spCollectScope.reload();
    }

});
