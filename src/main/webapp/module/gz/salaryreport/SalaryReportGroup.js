/**
 *薪资报表分组页面
 *zhaoxg 2016-4-15
 */
Ext.define('SalaryReport.SalaryReportGroup',{
    constructor:function(config){
        groupScope = this;
        groupScope.rsid=config.rsid;
        groupScope.rsdtlid=config.rsdtlid;
        var treeStore = Ext.create('Ext.data.TreeStore', {
            proxy:{
                type: 'transaction',
                functionId:'GZ00000503',
                extraParams:{
                    salaryid:config.salaryid_encrypt,
                    rsdtlid:config.rsdtlid
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            root: {
                // 根节点的文本
                id:'root',
                text:'分组范围',
                expanded: true,
                icon:'/images/add_all.gif'
            }
        });
        var tree = Ext.create('Ext.tree.Panel', {
            // 不使用Vista风格的箭头代表节点的展开/折叠状态
            useArrows: false,
            id:'groupPanel',
            height:300,
            width:430,
            store: treeStore, // 指定该树所使用的TreeStore
            rootVisible: true, // 指定根节点可见
            listeners:{
                'itemclick':function(view,record,item,index){
                    var records = view.getChecked();
                    groupScope.ids = [];
                    Ext.Array.each(records, function(rec){
                        groupScope.ids.push(rec.data.id);
                    });
                }
            }
        });
        var buttons = Ext.create('Ext.panel.Panel',{
            layout:'column',
            border:false,
            columnWidth:1,
            width:50,
            items:[{
                xtype:'button',
                columnWidth:1,
                text:'打开',
                id:'b_up',
                style:'margin-top:20px',
                listeners:{
                    'click':function(){
                        var obj = new Object();
                        obj.rsid = groupScope.rsid;
                        obj.rsdtlid = groupScope.rsdtlid;
                        obj.salaryid = salaryReportScope.salaryid_encrypt;
                        obj.gz_module = salaryReportScope.gz_module;
                        obj.model = salaryReportScope.model;
                        obj.bosdate = salaryReportScope.bosdate_encrypt;
                        obj.count = salaryReportScope.count_encrypt;
                        obj.title = config.theArr[0]+"-->"+config.theArr[1];
                        obj.groupvalues = groupScope.ids;
                        Ext.require('SalaryReport.OpenSalaryReport', function(){
                            Ext.create("SalaryReport.OpenSalaryReport",obj);
                        });
                        win.close();
                        /**		       			var vs = Ext.getBody().getViewSize();
                         var strurl="/gz/gz_accounting/report/open_gzbanner.do?b_query=query&screenWidth="+(vs.width-20)+"&screenHeight="+(vs.height-40)+"&boscount="+salaryReportScope.count+"&bosdate="+salaryReportScope.bosdate+"&rsid="+rsid+"&rsdtlid="+rsdtlid+"&salaryid="+salaryReportScope.salaryid+"&groupValues="+groupScope.ids+"&model="+salaryReportScope.model+"&pt="+salaryReportScope.rl;
                         Ext.require('SalaryReport.CreateWindow', function(){
							Ext.create("SalaryReport.CreateWindow",{title:config.theArr[0]+"-->"+config.theArr[1],url:strurl});
						});*/
                    }
                }
            },{
                xtype:'button',
                columnWidth:1,
                style:'margin-top:20px',
                text:'取消',
                id:'b_clo',
                listeners:{
                    'click':function(){
                        win.close();
                    }
                }
            }]
        });
        var win = Ext.widget("window",{
            title:'请选择第一分组项范围',
            height:350,
            width:500,
            layout:'fit',
            id:'group',
            modal:true,
            closeAction:'destroy',
            items: [{
                xtype:'panel',
                layout:'border',
                bodyStyle: 'background:#ffffff;',
                border:false,
                items:[
                    { region: "center",border:false,items:tree},
                    { region: "east",border:false,items:buttons}
                ]
            }]
        });
        win.show();
    }
})