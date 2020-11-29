/**
 *考勤输出配置页面
 * @author haosl
 * @date 2019-7-8
 */
Ext.define('KqDataURL.KqExportSetting', {
    scheme_id:'',
    constructor:function(config){
        KqExportSetting = this;
        this.scheme_id = config.scheme_id;
        this.detailsVal = "";//用于回显日明细导出设置
        this.sumsVal = "";//用于回显汇总列导出设置
        this.searchExportScheme();//detailsVal,sumsVal 赋值
        this.showMainView();
    },
    /*
     *打开配置窗口
     */
    showMainView:function () {
        var me = this;
        var title1 = me.getMxItemsPanelTitle();
        var body1 = me.getColumns(1);
        var title2 = me.getCollectItemsPanelTitle();
        var body2 = me.getColumns(2);
        Ext.create("Ext.window.Window",{
            id:'mainView',
            width:750,
            height:600,
            modal:true,
            resizable:false,
            title:kq.label.exportConfig,
            layout:'vbox',
            items:[title1,body1,title2,body2],
            buttonAlign:'center',
            bbar:[
                '->',
                {
                    text:kq.button.ok,
                    width:75,
                    height:25,
                    style:'margin-right:5px;',
                    handler:me.saveExportScheme
                },
                {
                    text:kq.button.cancle,
                    width:75,
                    height:25,
                    handler:function(){
                        Ext.getCmp("mainView").close();
                    }
                },
                '->'
            ]
        }).show();
    },
    getMxItemsPanelTitle:function(){
       return Ext.create("Ext.container.Container",{
            id:'mxItemsPanel',
            height:12,
            style:'border-left:5px solid #5190ce;padding-left:5px;',
            html:"<span style='line-height:12px;'>"+kq.label.exportMxItems+"</span>",
            margin:'20 0 20 10',
            width:200
        })
    },
    getCollectItemsPanelTitle:function(){
       return Ext.create("Ext.container.Container",{
            id:'CollectItemsPanel',
            height:12,
            style:'border-left:5px solid #5190ce;padding-left:5px;',
            html:"<span style='line-height:12px;'>"+kq.label.exportCollectItems+"</span>",
            margin:'20 0 20 10',
            width:200
        })
    },
    /**
     *获得输出列
     * @param type
     *  =1 日明细列  =2 月汇总列
     */
    getColumns:function(type){
        var me = this;
        var map = new HashMap();
        var jsonstr = {};
        jsonstr.type='getExportColumns';
        map.put("jsonStr",JSON.stringify(jsonstr));
        var columns = [];
        var fzVal = ",";
        Rpc({functionId:'KQ00021201',async:false,success:function(form){
            var result = Ext.decode(form.responseText);
            var returnStr = eval(result.returnStr);
            var return_code = returnStr.return_code;
            var datalist = returnStr.return_data.datalist;
            if (return_code == 'fail') {
                Ext.showAlert(returnStr.return_msg);
            }
            if(type==1){
                fzVal += me.detailsVal;
            }else{
                fzVal += me.sumsVal;
            }
            fzVal+=",";
            for(var i in datalist){
                columns.push({
                    boxLabel:"<span title='"+datalist[i].dataValue+"'>"+datalist[i].dataValueJx+"</span>",
                    maxWidth:100,
                    name:datalist[i].dataIndex,
                    checked:fzVal.indexOf(","+datalist[i].dataIndex+",")>-1?true:false,
                    width:100
                });
            }
        }},map);
        return Ext.create("Ext.container.Container",{
            items:[
                {
                    id:type==1?'detail':'sum',
                    xtype: 'checkboxgroup',
                    margin:'0 0 0 20',
                    fieldLabel: kq.label.exportColumn,
                    labelWidth:60,
                    defaultType: 'checkboxfield',
                    columns:6,
                    vertical: false,
                    defaults:{
                        margin:'0 0 5 0'
                    },
                    items:columns
                }
            ]
        });
    },
    /**
     * 查询导出方案配置
     */
    searchExportScheme:function () {
        var me = this;
        var map = new HashMap();
        var jsonstr = {};
        jsonstr.type='getExportScheme';
        jsonstr.scheme_id = me.scheme_id;
        map.put("jsonStr",JSON.stringify(jsonstr));
        Rpc({functionId:'KQ00021201',async:false,success:function(form){
            var result = Ext.decode(form.responseText);
            var returnStr = eval(result.returnStr);
            var return_code = returnStr.return_code;
            var exportScheme = returnStr.return_data.exportScheme;
            if (return_code == 'fail') {
                Ext.showAlert(returnStr.return_msg);
            }
            me.detailsVal = exportScheme.detailsVal;
            me.sumsVal = exportScheme.sumsVal;
        }},map);
    },
    /**
     * 保存方案配置
     */
    saveExportScheme:function () {
        var me = this;
        var details = Ext.getCmp("detail").getChecked();
        var sums = Ext.getCmp("sum").getChecked();
        var detailsVal = "";
        var sumsVal = "";
        //获得日明细组中所有选中的复选框，并组装成xxx,xxx,xxx字符串保存到后台
        for(var i in details){
            var col = details[i];
            if(i>0) {
                detailsVal += ",";
            }
            detailsVal+= col.name;
        }
        //获得汇总组中所有选中的复选框，并组装成xxx,xxx,xxx字符串保存到后台
        for(var i in sums){
            var col = sums[i];
            if(i>0) {
                sumsVal += ",";
            }
            sumsVal+= col.name;
        }
        //保存到后台
        var map = new HashMap();
        var jsonstr = {};
        jsonstr.type='saveExportScheme';
        jsonstr.scheme_id = KqExportSetting.scheme_id;
        jsonstr.detailsVal=detailsVal;
        jsonstr.sumsVal=sumsVal;
        map.put("jsonStr",JSON.stringify(jsonstr));
        Rpc({functionId:'KQ00021201',async:false,success:function(form){
            var result = Ext.decode(form.responseText);
            var returnStr = eval(result.returnStr);
            var return_code = returnStr.return_code;
            if (return_code == 'fail') {
                Ext.showAlert(returnStr.return_msg);
                return;
            }
            Ext.getCmp("mainView").close();
        }},map);
    }
});