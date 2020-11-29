/**
 * 薪资发放-导入数据-弹出上传文件框
 * lis 2015-11-25
 */
Ext.Loader.setPath("SYSF","../../../components/fileupload");
Ext.define('SalaryTypeUL.ImportSalaryType',{
    requires:['SYSF.FileUpLoad'],//加载上传控件js
    constructor:function(config){
        importSalaryType_me = this;
        importSalaryType_me.imodule=config.imodule;// 0:薪资  1:保险
        this.createSalary(config);
    },
    createSalary:function(config)
    {
        //上传控件
        var uploadObj = Ext.create("SYSF.FileUpLoad",{
        	isTempFile:true,
			VfsModules:VfsModulesEnum.GZ,
			VfsFiletype:VfsFiletypeEnum.other,
			VfsCategory:VfsCategoryEnum.other,
			CategoryGuidKey:'',
            upLoadType:1,
            fileExt:"*.zip;",
            height: 30,
            //回调方法，失败
            error:function(){
                Ext.showAlert(common.msg.uploadFailed+"！");
            },
            success:function(list){
                var fileid= list[0].fileid;
                importSalaryType_me.showInitData(fileid)
            }
        });

        //上传导入弹出框
        importSalaryType_me.win=Ext.widget("window",{
            title: gz.msg.selectImportSalaryTypeFile,//请选择需导入的类别文件
            modal:true,
            border:false,
            width:380,
            height: 120,
            closeAction:'destroy',
            resizable:false,
            items:[{
                xtype: 'panel',
                border:false,
                layout:{
                    type:'vbox',
                    padding:'15 0 0 35', //上，左，下，右
                    pack:'center',
                    align:'middle'
                },
                items:[uploadObj]
            }]
        }).show();
    },

    showInitData:function(fileid){
        importSalaryType_me.win.close();
        var states = Ext.create('Ext.data.Store', {
            fields: ['isrepeat', 'name'],
            data : [
                {"isrepeat":"0", "name":gz.label.addite},//追加
                {"isrepeat":"1", "name":gz.label.cover}//"覆盖"
            ]
        });

        var combo = Ext.create('Ext.form.ComboBox', {
            store: states,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'isrepeat',
            renderTo: Ext.getBody()
        });

        importSalaryType_me.importStore = Ext.create('Ext.data.Store', {
            fields:['id','name','isrepeat','oldid'],
            proxy:{
                type:'transaction',
                functionId:'GZ00000221',
                extraParams:{
                	fileid:fileid
                },
                reader: {
                    type: 'json',
                    root: 'salarySetList'
                }
            },
            autoLoad: true
        });

        importSalaryType_me.gridPanel = Ext.create('Ext.grid.Panel', {
            store: importSalaryType_me.importStore,
            width: '100%',
            height: 377,
            multiSelect:true,
            columnLines:true,
            rowLines:true,
            border:false,
            selModel: Ext.create("Ext.selection.CheckboxModel", {
                mode: "multi",//multi,simple,single；默认为多选multi
                checkOnly: false,//如果值为true，则只用点击checkbox列才能选中此条记录
                allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
                enableKeyNav: true
            }),
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1
                })
            ],
            columns: [
                { text: gz.label.typeName,menuDisabled:true, dataIndex: 'name',flex: 5,field: 'textfield'
                },
                //操作
                { text: gz.label.operation,menuDisabled:true, dataIndex: 'isrepeat',flex: 1,align:'center',editor:combo,
                    renderer:function(value, metaData, record){
                        if(value == '0')
                            return gz.label.addite;//追加
                        else return gz.label.cover;//覆盖
                    }}
            ],
            renderTo:Ext.getBody(),
            listeners: {
                'beforeedit': function(editor,e){
                    if(e.field == 'isrepeat'){
                        if(e.record.get('oldid') == ''){//追加
                            return false;
                        }
                    }
                },
                'edit':function(editor,e){
                    //修改薪资类别名称，如果是追加则要判断是否重名
                    if(e.field  == 'name'){
                        importSalaryType_me.isRepeatName(e.value,e.record.get("id",e.record.get('isrepeat')));
                    }
                }}
        });

        //导入
        importSalaryType_me.dataWin=Ext.widget("window",{
            title:importSalaryType_me.imodule== 0?gz.label.salaryType:"险种类别",
            modal:true,
            border:true,
            resizable:false,
            minButtonWidth:45,
            width:510,
            height: 450,
            closeAction:'destroy',
            items:[importSalaryType_me.gridPanel],
            buttons:[
                {xtype:'tbfill'},
                {
                    text:common.button.toimport,//导入
                    handler:function(){
                        var salarySetIDs = "";
                        var isRepeat = false;
                        var records = importSalaryType_me.gridPanel.getSelectionModel().getSelection();
                        if(records.length == 0){
                            Ext.showAlert(gz.msg.selectImportSalaryType);//"请选择要导入的薪资类别"
                            return;
                        }
                        var salaryData = new Array();
                        Ext.Array.each(records, function(record) {
                            salaryData.push(record.data);

                            if(record.data.isrepeat == '1')
                                isRepeat = true;
                        });

                        if(isRepeat){
                            Ext.showConfirm(gz.msg.overClearSalaryType,function(but){
                                if(but == 'yes'){
                                    importSalaryType_me.importData(salaryData,fileid);
                                }
                            });
                        }else{
                            importSalaryType_me.importData(salaryData,fileid);
                        }
                    }

                },
                {
                    text:common.button.toreturn,//取消
                    handler:function(){
                        importSalaryType_me.dataWin.close();
                    }
                },{xtype:'tbfill'}
            ]
        }).show();
    },

    //导入薪资类别
    importData:function(salaryData,fileid){
        var map = new HashMap();
        map.put("salaryData",salaryData);
        map.put("gz_module","0");
        map.put("fileid",fileid);
        map.put("salaryid","-1");
        map.put("isAdd","1");
        Rpc({functionId:'GZ00000222',success:function(response,action){
                var result = Ext.decode(response.responseText);
                if (result.succeed) {
                    var repeatNames = result.repeatNames;
                    if(repeatNames != 0){
                        Ext.showAlert(gz.msg.isNotUniq);//该名称已存在，请重新命名
                    }else{
                        importSalaryType_me.dataWin.close();
                        salarytype_me.reLoad();
                        var filename=result.filename;
                        if(filename!=null && filename!=""){
                        	filename = getDecodeStr(filename);
                        	var win=open("/servlet/vfsservlet?fileid=" + filename +"&fromjavafolder=true","txt");
                        }
                    }
                }else{
                    Ext.showAlert(result.msg);
                }
            }},map);
    },

    //验证重名
    isRepeatName:function(name,id,isrepeat){
        var flag = true;
        var map = new HashMap();
        map.put("name",getEncodeStr(name));
        map.put("gz_module","0");
        map.put("salaryid",id);

        if(isrepeat == '0')
            map.put("type","1");//追加
        else
            map.put("type","0");//覆盖
        Rpc({functionId:'GZ00000218',success:function(response,action){
                var result = Ext.decode(response.responseText);
                if (result.succeed) {
                    if(result.msg != '0'){//有重复名称
                        Ext.showAlert(result.msg);//有重复名称
                    }else
                        flag = false;
                }
            }},map);
        return flag;
    }

});
