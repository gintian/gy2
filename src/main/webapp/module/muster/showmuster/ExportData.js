/**
 * 简单花名册导出EXCEL选择指标框
 */
Ext.define('SetupschemeUL.ExportData',{
    constructor:function(config){
        export_me = this;
        tabid=config.tabid;
        moduleID=config.moduleID;
        flag = config.flag;//1.导出PDF2.导出Excel
        totalCount = config.totalCount;
        columns = config.columns;
        export_me.createSelectFieldWindow();
    },
    compare:function(property){
        return function(a,b){
            var value1 = a[property];
            var value2 = b[property];
            return value1 - value2;
        }
    },
    createSelectFieldWindow:function(){
    	columns.sort(export_me.compare('fullColumnIndex'))
    	var store = Ext.create('Ext.data.Store', {
            fields:['dataIndex','text','fullColumnIndex'],
            data:columns
        });
        //生成导出项目表格
        var panel = Ext.create('Ext.grid.Panel', {
            store: store,
            width: 270,
            height: 345,
            forceFit:true,
        	viewConfig: {
				plugins: {
					ptype: "gridviewdragdrop",
					dragText: musterAdd.dropSort
				}
			},
            selModel: Ext.create("Ext.selection.CheckboxModel", {
                mode: "multi",//multi,simple,single；默认为多选multi
                checkOnly: true,//如果值为true，则只用点击checkbox列才能选中此条记录
                allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
                enableKeyNav: true
            }),
            columns: [
                { text: export_indexName,menuDisabled:true, dataIndex: 'text',width: 337}
            ]
        });
        //生成弹出得window
        var win=Ext.widget("window",{
            title :flag ==export_titleJudge?export_titleExcel:export_titlePDF,
            width:280,
            height:420,
            border:false,
            bodyStyle: 'background:#ffffff;',
            modal:true,
            closeAction:'destroy',
            items: [panel],
            buttons:[
                {xtype:'tbfill'},
                {
                    text:common.button.ok,//确定
                    handler:function(){
                    	var itemids = export_me.getItemId(panel);//将要导出的项目的id组成字符串
                        if(trim(itemids)==""){
                            Ext.showAlert(export_NoIndexMsg);
                            return;
                        }else{
                        	win.close();
                        	Ext.MessageBox.wait(common.msg.exporting+"...", common.msg.wait);
                            export_me.exportExcelOrPdf(itemids);
                        }
                    }
                },{
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
        var itemidflag = document.getElementsByName("text");
        for(var i=0;i<itemidflag.length;i++){
            itemidflag[i].checked=obj.checked;
        }
    },
    //将要导出的项目的id组成字符串
    getItemId:function(grid){
        var itemids="";
        var sel = grid.getSelectionModel().getSelection();
        Ext.Array.each(sel,function(record,index){
        	itemids+=record.get('dataIndex')+",";
        })
        return itemids;
    },
    //导出excel或PDF
    exportExcelOrPdf:function(itemids){
        var map = new HashMap();	
		map.put("totalCount",totalCount);
		map.put("tabid", tabid);	
		map.put("flag",flag);//=2,代表导出EXCEL
		map.put("moduleID",moduleID);
		map.put("itemids",itemids);
		map.put("musterType",musterType);
    	Rpc({functionId : 'MM01020004',success:export_me.showFieldList},map);         
    },
    showFieldList:function(form,action){
		var result = Ext.decode(form.responseText);
		if(Ext.MessageBox){
			Ext.MessageBox.close();
		}
		var outName=result.outName;
		var flag=result.flag;
		window.open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true");
	},
});