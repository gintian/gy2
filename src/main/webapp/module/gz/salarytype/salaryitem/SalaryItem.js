/**
 * 薪资类别-薪资项目弹出框
 * lis 2015-10-20
 */
Ext.define('SalaryTypeUL.salaryitem.SalaryItem',{
    constructor:function(config){
        salaryitem_me = this;
        salaryitem_me.salaryid = config.salaryid;
        salaryitem_me.cname = config.cname;
        salaryitem_me.createSalary();
        salaryitem_me.globalVariable = true;//该全局变量为true时表示没有<input type=checkbox....>,为false就有 //sunj
    },
    createSalary:function()
    {
        //获得薪资项目数据store
        salaryitem_me.salaryItemStore = Ext.create('Ext.data.Store', {
            fields:['itemdesc','initflag','itemid','fieldid','itemtype'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000211',
                extraParams:{
                    salaryid:salaryitem_me.salaryid
                },
                reader: {
                    type: 'json',
                    root: 'salaryItemList'
                },
                timeout:999999
            },
            listeners:{
                load : function( store, records, successful, operation){
                    var responseText = Ext.util.JSON.decode(operation._response.responseText);
                    if(!Ext.isEmpty(responseText.errorMessage)) {
                        Ext.showAlert(responseText.errorMessage);
                    }
                }
            },
            autoLoad: true
        });

        //加载查询框
        var map = new HashMap();
        map.put("salaryid",salaryitem_me.salaryid);
        salaryitem_me.SearchBox = Ext.create("EHR.querybox.QueryBox",{
            //renderTo : "fastsearch1",
            //width:500,加了width导致多个条件的时候显示错位
            hideQueryScheme:true,
            emptyText:gz.msg.inputItemNameOrCode,//"请输入项目名称、项目代码"
            subModuleId:"salaryitems",
            customParams:map,
            funcId:"GZ00000211",
            success:salaryitem_me.reLoad
        });


        //生成薪资项目grid
        salaryitem_me.gridpanel = Ext.create('Ext.grid.Panel', {
            store: salaryitem_me.salaryItemStore,
            width: 790,
            height: 410,
            multiSelect:true,
            columnLines:true,
            rowLines:true,
            border:false,
            bufferedRenderer:false,//一起把数据去拿过来，不用假分页模式
            tbar:[{
                xtype:'panel',
                border:false,
                height:27,
                items:salaryitem_me.SearchBox
                //html:'<div id="fastsearch1"></div>'
            }],
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1
                })
            ],
            columns: [
                {
                    text:'<input name="selectall" type=checkbox id="selall" onclick="salaryitem_me.selectALL(this.checked);" />',
                    width:35,
                    menuDisabled:true,
                    sortable:false,
                    align:'center',
                    dataIndex:'fieldid',
                    renderer:function(value,data,record,rowIndex){
                        //在加载时将全局的标识值设置为true
                        if(rowIndex == 0) {
                            salaryitem_me.globalVariable = true;
                        }
                        if(record.data.initflag == '3'){
                            //只有当加载到最后一行，并且全局的值为true时将按钮至为disabled，checked设为false
                            if(salaryitem_me.salaryItemStore.data.length - 1 == rowIndex && salaryitem_me.globalVariable == true) {
                                document.getElementById("selall").checked = false;
                                document.getElementById("selall").disabled = true;
                            }
                            return "";
                        }else {
                            salaryitem_me.globalVariable = false;
                            //只要进入这里就代表可以全选
                            if(document.getElementById("selall").disabled == true) {
                                document.getElementById("selall").disabled = false;
                            }
                            return '<input name="itemid" type=checkbox id="it_'+value+'" />';
                        }
                    }
                },
                { text: gz.label.itemName,menuDisabled:true, dataIndex: 'itemdesc',flex: 2,sortable:false},//项目名称
                //处理方式
                { text: gz.label.processingMode,menuDisabled:true, dataIndex: 'initflag',flex: 1,renderer:salaryitem_me.dataSelect,field:'textfield',sortable:false},
                //项目代码
                { text: gz.label.projectCode,menuDisabled:true, dataIndex: 'itemid',flex: 1,sortable:false},
                //操作
                { text: gz.label.operation,menuDisabled:true, dataIndex: 'fieldid',flex: 1,align:'center',sortable:false,
                    renderer:function(value, metaData, record,rowIndex){
                        if(record.data.initflag == '3' || record.data.initflag == '0')
                            return "";

                        var html = '<a href="javascript:salaryitem_me.editImport(\''+value+'\',\''+record.data.initflag+'\',\''+record.data.itemdesc+'\')" >';
                        html = html + '<img src="/images/new_module/formula.gif" border=0></a>';
                        return html;
                    }}
            ],
            listeners:{
                'edit':function(editor, e, eOpts ){
                    e.record.commit();
                },
//            		'itemclick':function(view,record, item, index,e, eOpts){
//            			salaryitem_me.selected();
//            		},
                'cellclick':function(view, td, cellIndex,record, tr, rowIndex){
                    //alert(1);
                    if(cellIndex == 0 && record.data.initflag != '3'){
                        salaryitem_me.isSelectAll();
                    }
                }
            },
            renderTo:Ext.getBody()
        });



        //生成弹出得window
        var win=Ext.widget("window",{
            title : salaryitem_me.cname,
            width: 801,
            id:'pa',
            height: 490,
            minButtonWidth:45,
            resizable:false,
            border:true,
            modal:true,
            closeAction:'destroy',
            items: [
                salaryitem_me.gridpanel
            ],buttons:[
                {xtype:'tbfill'},
                {
                    text:common.button.insert,//新增
                    handler:function(){
                        var map = new HashMap();
                        map.put(1, "a,b,k");
                        map.put(2, "a,b,k,y,v,w");
                        var map2 = new HashMap();
                        map2.put('salaryid', salaryitem_me.salaryid);
                        Ext.require('EHR.selectfield.SelectField',function(){
                            Ext.create("EHR.selectfield.SelectField",{imodule:"0",comBoxDataInfoMap:map,dataMap:map2,title:gz.label.newItemField,saveCallbackfunc:salaryitem_me.saveItems});
                        })
                    }

                },
                {
                    text:common.button.todelete,//删除
                    handler:function(){
                        salaryitem_me.deleted();
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

        //编辑之前触发的事件
        salaryitem_me.gridpanel.on("beforeedit", function(edit,e){
            var data = e.record.get("initflag");//再从记录中获得这列的value
            if(e.column.dataIndex == 'initflag'){
                if(data != 3){
                    if(e.record.data.itemtype != 'N'){
                        e.column.setEditor({
                            xtype:'combo',
                            store: Ext.create('Ext.data.Store', {
                                fields: ['initFlag', 'name','itemdesc'],
                                data : [
                                    {"initFlag":"0", "name":gz.label.inputValue},//输入项
                                    {"initFlag":"2", "name":gz.label.importValue}//导入项
                                ],
                                autoLoad: true
                            }),
                            queryMode: 'local',
                            repeatTriggerClick : true,
                            editable: false,
                            forceSelection: true,
                            displayField: 'name',
                            valueField: 'initFlag',
                            listeners:{
                                'select':function(combo, records,opt){//改变数据处理方式
                                    var fieldid = e.record.data.fieldid;
                                    var itemdesc = e.record.data.itemdesc;
                                    var initflag = combo.getValue();
                                    if(combo.getValue()=='0'){//是输入项时
                                        var map = new HashMap();
                                        map.put("fieldid",fieldid);
                                        map.put("salaryid",salaryitem_me.salaryid);
                                        map.put("flag","0");

                                        Rpc({functionId:'GZ00000214',success:function(response,action){
                                                var success = Ext.decode(response.responseText).succeed;
                                                if (!success) {
                                                    Ext.showAlert(response.responseText.message);
                                                }
                                            }},map);
                                        return;
                                    }else{
                                        salaryitem_me.editImport(fieldid,initflag,itemdesc);
                                    }
                                }
                            }
                        });
                    }else{
                        e.column.setEditor({
                            xtype:'combo',
                            store: Ext.create('Ext.data.Store', {
                                fields: ['initFlag', 'name'],
                                data : [
                                    {"initFlag":"0", "name":gz.label.inputValue},//输入项
                                    {"initFlag":"1", "name":gz.label.accumulateValue},//积累项
                                    {"initFlag":"2", "name":gz.label.importValue}//导入项
                                ],
                                autoLoad: true
                            }),
                            queryMode: 'local',
                            repeatTriggerClick : true,
                            editable: false,
                            forceSelection: true,
                            displayField: 'name',
                            valueField: 'initFlag',
                            listeners:{
                                'select':function(combo, records,opt){//改变数据处理方式
                                    var fieldid = e.record.data.fieldid;
                                    var itemdesc = e.record.data.itemdesc;
                                    var initflag = combo.getValue();
                                    if(combo.getValue()=='0'){//是输入项时
                                        var map = new HashMap();
                                        map.put("fieldid",fieldid);
                                        map.put("salaryid",salaryitem_me.salaryid);
                                        map.put("flag","0");

                                        Rpc({functionId:'GZ00000214',success:function(response,action){
                                                var success = Ext.decode(response.responseText).succeed;
                                                if (!success) {
                                                    Ext.showAlert(response.responseText.message);
                                                }
                                            }},map);
                                        return;
                                    }else{
                                        salaryitem_me.editImport(fieldid,initflag,itemdesc);
                                    }
                                }
                            }
                        });
                    }
                }else{
                    return false;
                }
            }
        });
    },

    //重新加载表格数据
    reLoad:function(query){
        salaryitem_me.gridpanel.getStore().load({params:{where:query.where}});
    },

    //渲染"处理方式"显示数据
    dataSelect:function(value, cellmeta, record, rowIndex, columnIndex, store){
        var initFlag = value;

        if(initFlag == 0){
            value = gz.label.inputValue;
        }else if(initFlag == 1){
            value = gz.label.accumulateValue;
        }else if(initFlag == 2){
            value = gz.label.importValue;
        }else if(initFlag == 3){
            value = gz.label.sysValue;
        }
        return value;
    },

    //导入项或积累项弹出的复杂计算
    editImport:function(fieldid,initflag,itemdesc){
        var title = "";
        if(initflag=="1")
            title = "【"+itemdesc+"】累计项";
        else
            title = "【"+itemdesc+"】导入公式";
        var map = new HashMap();
        map.put("salaryid",salaryitem_me.salaryid);
        map.put("fieldid",fieldid);
        map.put("initflag",initflag);
        Ext.require('EHR.complexcondition.ComplexCondition',function(){
            Ext.create("EHR.complexcondition.ComplexCondition",{dataMap:map,imodule:"3",opt:"0",title:title,callBackfn:"salaryitem_me.saveComplexCond"});
        });
    },

    //删除
    deleted:function(){
        var records = Ext.query("input[type=checkbox]");
        var salarySetIDs = "";
        Ext.Array.each(records, function(record){
            if(record.id=="selall")
                return;
            if(record.checked){
                var fieldid = record.id.split('_')[1];
                salarySetIDs = salarySetIDs + "/" + fieldid;
            }
        });

        if(salarySetIDs.length==0){
            Ext.showAlert(gz.msg.selectDelItem);
            return;
        }
        Ext.showConfirm( gz.msg.isDelItem, function(button, text) {
            if (button == "yes") {

                var map = new HashMap();
                map.put("salarySetIDs",salarySetIDs);
                map.put("salaryid",salaryitem_me.salaryid);
                map.put("opt","2");

                Rpc({functionId:'GZ00000212',success:function(response,action){
                        var result = Ext.decode(response.responseText);
                        if (result.succeed) {
                            salaryitem_me.salaryItemStore.load();
                        } else {
                            Ext.showAlert(result.message);
                        }
                    }},map);
            }
        },common.button.promptmessage)
    },

    //全选
    selectALL:function(checked){
        var checkboxs = Ext.query("*[name=itemid]");
        Ext.each(checkboxs,function(checkbox,index){
            checkbox.checked=checked;
        })
    },
    //更新全选框选中状态
    isSelectAll:function(){
        var checkboxs = Ext.query("*[name=itemid]");
        var isSelect=true;
        Ext.each(checkboxs,function(checkbox,index){
            if(checkbox.checked==false)
                isSelect=false;
        })
        document.getElementById('selall').checked=isSelect;
    },

    //单击选中行，勾选checkbox(勾选框选中后无法取消 停用)
    selected:function(){
        var sel = salaryitem_me.gridpanel.getSelectionModel().getSelection();
        Ext.each(sel,function(record,index){
            if(record.get('initflag') != 3){
                if(Ext.getDom('it_'+record.get('fieldid'))){
                    Ext.getDom('it_'+record.get('fieldid')).checked=true;
                }
            }
        })
    },

    //保存新增的薪资项
    saveItems:function(salarySetIDs){
        var map = new HashMap();
        map.put("salarySetIDs",salarySetIDs);
        map.put("salaryid",salaryitem_me.salaryid);
        map.put("opt","1");

        Rpc({functionId:'GZ00000212',success:function(response,action){
                var success = Ext.decode(response.responseText).succeed;
                salaryitem_me.SearchBox.removeAllKeys();
                if (success) {
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

    //保存公式
    saveComplexCond:function(c_expr,heapFlag,initflag,fieldid){
        var map = new HashMap();
        map.put("salaryid",salaryitem_me.salaryid);
        map.put("heapFlag",heapFlag);
        map.put("formula",c_expr);
        map.put("module",'1');
        map.put("type",'1');
        map.put("flag",initflag);
        map.put("fieldid",fieldid);
        Rpc({functionId:'GZ00000214',async:false,success:function(form,action){
                var result = Ext.decode(form.responseText);
                if (!result.succeed) {
                    Ext.showAlert(result.message);
                }
            }},map);
    }
})