/**
 * 薪资发放-导出数据-弹出薪资项目列表框
 */
Ext.define('SalaryUL.inout.ExportData',{
    constructor:function(config){
        export_me = this;
        export_me.salaryid = config.salaryid;
        export_me.flag = config.flag;
        export_me.appdate = config.appdate;
        export_me.count = config.count;
        export_me.imodule=config.imodule;
        export_me.cound = config.cound;
        export_me.typeflag = config.typeflag;//typeflag代表1.导出还是2.下载模板 sunjian
        this.createSalary();
    },
    createSalary:function()
    {
        //获得薪资项目数据store
        var store = Ext.create('Ext.data.Store', {
            fields:['itemid','itemdesc'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000041',
                extraParams:{
                    salaryid:export_me.salaryid,
                    imodule:export_me.imodule,
                    typeflag:export_me.typeflag,
                    flag:export_me.flag
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true
        });
        //生成导出项目表格
        var panel = Ext.create('Ext.grid.Panel', {
            store: store,
            width: 390,
            height: 410,
            forceFit:true,
            selModel: Ext.create("Ext.selection.CheckboxModel", {
                mode: "multi",//multi,simple,single；默认为多选multi
                checkOnly: false,//如果值为true，则只用点击checkbox列才能选中此条记录
                allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
                enableKeyNav: true
            }),
            columns: [
                { text: gz.label.gzItems,menuDisabled:true, dataIndex: 'itemdesc',width: 337}
            ]
        });

        //生成弹出得window
        var win=Ext.widget("window",{
            title : gz.label.selectExpItems,
            width:Ext.getBody().getViewSize().height < 530?390:400,
            height:Ext.getBody().getViewSize().height < 530?420:515,
            border:false,
            bodyStyle: 'background:#ffffff;',
            modal:true,
            closeAction:'destroy',
            items: [{
                xtype: 'fieldcontainer',
                fieldLabel : export_me.typeflag == '2'?"":gz.label.exportPattern,
                defaultType: 'radiofield',
                labelAlign:'rigth',
                labelWidth:60,
                margin: '3 0 3 3',
                id: 'exportType',
                width: export_me.typeflag == '2'?370:260,
                height: 25,
                defaults: {
                    flex: 1
                },
                layout: 'hbox',
                items: [
                    {
                        boxLabel  : 'Excel'+gz.label.pattern,
                        hidden:export_me.typeflag == '2'?true:false,
                        checked : true,
                        name      : 'a',
                        inputValue: '1',
                        id        : 'radio1'
                    }, {
                        boxLabel  : 'xml'+gz.label.pattern,
                        hidden:export_me.typeflag == '2'?true:false,
                        name      : 'a',
                        inputValue: '2',
                        id        : 'radio2'
                    }, {
                        boxLabel  : gz.label.highVersion,
                        hidden:export_me.typeflag == '2'?false:true,
                        checked : true,
                        name      : 'a',
                        inputValue: 'xlsx',
                        id        : 'radio3'
                    }, {
                        boxLabel  : gz.label.lowVersion,
                        hidden:export_me.typeflag == '2'?false:true,
                        name      : 'a',
                        inputValue: 'xls',
                        id        : 'radio4'
                    }
                ]
            },panel

            ],buttons:[
                {xtype:'tbfill'},
                {
                    text:common.button.ok,//确定
                    handler:function(){
                        var itemids = export_me.getItemId(panel);//将要导出的项目的id组成字符串
                        if(trim(itemids)==""){
                            Ext.showAlert(gz.label.selectExpItems+"！");
                            return;
                        }else{
                            Ext.MessageBox.wait(common.msg.exporting+"...", common.msg.wait);
                            export_me.exportData(itemids);
                            win.close();
                        }
                    }
                },
                {
                    text:common.button.cancel,//取消
                    handler:function(){
                        win.close();
                    }
                },{xtype:'tbfill'}
            ]
        });

        win.show();
    },

    //全部选择或取消
    selectALL:function(obj){
        var itemidflag = document.getElementsByName("itemid");
        for(var i=0;i<itemidflag.length;i++){
            itemidflag[i].checked=obj.checked;
        }
    },

    //将要导出的项目的id组成字符串
    getItemId:function(grid){
        var itemids="";
        var sel = grid.getSelectionModel().getSelection();
        Ext.Array.each(sel,function(record,index){
            itemids+="/"+record.get('itemid');
        })
        return itemids;
    },

    //导出excel
    exportData:function(itemids){
        //如果typeflag = 2表示下载模板
        if(export_me.typeflag == 2) {
        	var radio3 = Ext.getCmp('radio3');
            var radio4 = Ext.getCmp('radio4');
            var value;
            if(radio3.getValue())
                value = radio3.inputValue;
            if(radio4.getValue())
                value = radio4.inputValue;
            
            var map = new HashMap();
            map.put("type_format",value);
            map.put("itemids",itemids);
            map.put("salaryid",export_me.salaryid);
            map.put("flag",export_me.flag);
            map.put("appdate",export_me.appdate);
            map.put("count",export_me.count);
            map.put("cound",export_me.cound);
            Rpc({functionId:'GZ00000049',async:false,success:function(form,action){
                    Ext.MessageBox.close();
                    var result = Ext.decode(form.responseText);
                    if(result.succeed){
                    	var outName = getDecodeStr(result.outName);
                        window.location.target="_blank";
                        window.location.href = "/servlet/vfsservlet?fileid="+ outName +"&fromjavafolder=true";
                    }else{
                        Ext.showAlert("导出失败！错误信息："+result.message);
                    };
                }},map);
        }else {
            var radio1 = Ext.getCmp('radio1');
            var radio2 = Ext.getCmp('radio2');
            var value;
            if(radio1.getValue())
                value = radio1.inputValue;
            if(radio2.getValue())
                value = radio2.inputValue;
            var map = new HashMap();
            map.put("itemids",itemids);
            map.put("type",value);
            map.put("salaryid",export_me.salaryid);
            map.put("flag",export_me.flag);
            map.put("appdate",export_me.appdate);
            map.put("count",export_me.count);
            map.put("imodule",export_me.imodule);
            map.put("cound",export_me.cound);
            Rpc({functionId:'GZ00000042',success:function(form,action){
                    Ext.MessageBox.close();
                    var result = Ext.decode(form.responseText);
                    if(result.succeed){
                    	var fileName = getDecodeStr(result.fileName);
                        window.location.target="_blank";
                        window.location.href = "/servlet/vfsservlet?fileid="+ fileName +"&fromjavafolder=true";
                    }else{
                        Ext.showAlert(result.message);
                    }

                }},map);
        }
    }
});