/**
 *批量修改
 */
Ext.define('SalaryUL.BatchUpdate',{
    salaryid:'',
    imodule:'',//模块号
    conditions:'',//条件
    columnfilter:'',//过滤条件
    constructor:function(config){
        this.salaryid = config.salaryid;
        this.imodule = config.imodule;
        conditions='';
        columnfilter = '';
        me=this;
        me.init();
    },
    init:function(){
        //选择模板的store，需要从后台查询。
        var templateStore = Ext.create('Ext.data.Store',{
            fields:['dataName','dataValue','codesetid'],
            proxy:{
                type: 'transaction',

                functionId:'GZ00000101',
                extraParams:{
                    salaryid:salaryid,
                    imodule:imodule,
                    opt:'1'
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true
        });
        templateStore.load();
        //参考项目的store
        var store = Ext.create('Ext.data.Store',{
            fields:['dataName','dataValue'],
            proxy:{
                type: 'transaction',
                functionId:'GZ00000101',
                extraParams:{
                    salaryid:salaryid,
                    imodule:imodule,
                    opt:'1'
                },
                reader: {
                    type: 'json',
                    root: 'data2'
                }
            },
            autoLoad: true
        });
        store.load();

        var codeStore = Ext.create('Ext.data.Store',{
            fields:['name','id'],
            proxy:{
                type: 'transaction',

                functionId:'GZ00000101',
                extraParams:{
                    salaryid:salaryid,
                    codeType:'1',
                    opt:'1'
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad:false
        });
        var width = 1400;
        var height = 650;
        var win = Ext.create('Ext.window.Window',{
            title:'批量修改',
            id:'batchUpdateWinId',
            width: 430,
            height: height*0.7,
            resizable: false,
            modal: true,
            border:false,
            items:[{
                xtype:'panel',
                layout:'hbox',
                border:false,
                style:'margin-left:10px;margin-top:3px',
                items:[{
                    xtype:'panel',
                    layout:'column',
                    width:340,
                    border:false,
                    items:[
                        {
                            xtype:'combo',
                            fieldLabel:gz.label.gzItems,
                            labelSeparator:null,
                            id:'gzItemId',
                            //style:'margin-top:10px',
                            labelAlign:'left',
                            labelWidth:60,
                            width:340,
                            typeAhead:true,
                            store:templateStore,
                            displayField:'dataName',
                            valueField:'dataValue',
                            minChars : 1,
                            forceSelection : true ,
                            queryParam : 'singer.singerName',
                            triggerAction : 'all',
                            listeners:{
                                'select':function(c,record){
                                    var codesetid=record.data.codesetid;
                                    var codeCombox=Ext.getCmp("codeItemId");
                                    if (codesetid != undefined&&codesetid!='0') {
                                        codeCombox.treeStore.getProxy().extraParams.codesetid=codesetid;
                                        codeCombox.treeStore.load();
                                        codeCombox.setValue("");
                                        codeCombox.show();
                                    }else{
                                        codeCombox.hide();
                                    }


                                }
                            }
                        },{
                            xtype:'textarea',
                            fieldLabel:'替换成',
                            id:'updateTextId',
                            style:'margin-top:10px',
                            labelAlign:'left',
                            labelSeparator:null,
                            labelWidth:60,
                            width:340,
                            height:height*0.35
                        },{
                            xtype:'combo',
                            fieldLabel:'参考项目',
                            id:'referItemId',
                            name:'referItemName',
                            labelSeparator:null,
                            style:'margin-top:10px',
                            labelAlign:'left',
                            labelWidth:60,
                            typeAhead:true,
                            width:340,
                            store:store,
                            displayField:'dataName',
                            valueField:'dataValue',
                            minChars : 1,
                            forceSelection : true ,
                            queryParam : 'singer.singerName',
                            triggerAction : 'all',
                            listeners:{
                                'select':function(){
                                    var text  =  this.getDisplayValue();
                                    me.symbol('updateTextId',text);
                                }
                            }
                        },{
                            xtype:'codecomboxfield',
                            fieldLabel:'系统代码',
                            id:'codeItemId',
                            labelWidth:60,
                            editable:false,
                            style:'margin-top:10px',
                            labelSeparator:null,
                            maxPickerWidth:196,
                            width:340,
                            hidden:true,
                            ctrltype: "3",
                            nmodule : '1',
                            listeners:{
                                'select':function(){
                                    var value=this.getValue();
                                    if(value.indexOf('`')>-1){
                                        value=value.substring(0,value.indexOf('`'));
                                    }
                                    var text  =  '"'+value+'"';
                                    me.symbol('updateTextId',text);
                                }
                            }
                        },
                        {
                            xtype:'panel',
                            border:false,
                            style:'margin-left:30;margin-top:10px',
                            width:400,
                            html:'说明：替换框内直接输入表达式。<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如：可将“月奖金” 替换成 “基本工资*0.1”'
                        }]
                },{
                    xtype:'panel',
                    width:width*0.07,
                    layout:'column',
                    columnWidth:1,
                    buttonAlign:'left',
                    border:false,
                    items:[{
                        xtype:'button',
                        text:'条件',
                        style:'margin-left:20px;margin-top:'+height*0.18,
                        listeners:{
                            'click':function(){
                                //调用定义条件插件。
                                Ext.Loader.setPath("DECN","../../../components/definecondition");
                                Ext.require('DECN.DefineCondition',function(){//xiegh 20170414 add opt:1 bug 27053
                                    Ext.create("DECN.DefineCondition",{primarykey:salaryid,imodule:'0',opt:'1',conditions:conditions,afterfunc:'GzGlobal.getCon'});
                                });
                            }
                        }
                    },{
                        xtype:'button',
                        text:'向导',
                        style:'margin-left:20px;margin-top:10px',
                        listeners:{
                            'click':function(){
                                Ext.require('EHR.functionWizard.FunctionWizard',function(){
                                    Ext.create("EHR.functionWizard.FunctionWizard",{keyid:salaryid,opt:"1",checktemp:'salary',mode:'xzgl_jsgs',callbackfunc:'me.getfunctionWizard'});
                                });
                            }
                        }

                    }]
                }]
            }] ,
            bbar:[{xtype:'tbfill'},{
                text:'确定',
                listeners:{
                    'click':function(){
                        //参考项目选中值
                        var itemid = Ext.getCmp('gzItemId').getValue();
                        //替换为 中 定义的公式内容
                        var formula = Ext.getCmp('updateTextId').getValue();
                        if(itemid==null||itemid==''){
                            var str=imodule=='Z~30DuTtqmt~33kPAATTP~33HJDPAATTP'?'保险项目':'薪资项目';
                            Ext.showAlert(str+"不能为空！");
                            return;
                        }
                        if(formula==null||formula.length==0)
                        {
                            Ext.showAlert("替换值不能为空！");
                            return;
                        }
                        var map = new HashMap();
                        map.put("cond",conditions);
                        map.put("salaryid",salaryid);
                        //页面过滤条件
                        map.put("whl",getEncodeStr(columnfilter));
                        map.put("itemid",getEncodeStr(itemid));
                        map.put("formula",getEncodeStr(formula));
                        Rpc({functionId:'GZ00000102',async:false,success:function(response,action){
                                var result = Ext.decode(response.responseText);
                                var flag=result.succeed;
                                if(flag==true){
                                    Ext.getCmp('batchUpdateWinId').close();
                                    accounting.loadStore();
                                }else{
                                    Ext.showAlert(result.message);
                                }
                            }},map);
                    }
                }
            },{
                text:'关闭',
                listeners:{
                    'click':function(){
                        win.close();
                    }
                }
            },{xtype:'tbfill'}]

        });
        win.show();


    },
    //函数向导回调函数，用来接收返回值
    getfunctionWizard:function(obj){
        me.symbol('updateTextId',obj);
    },

    //获取运算符号值，并写入到文本域中
    symbol:function(editor,strexpr){
        Ext.getCmp(editor).focus();
        if(Ext.isIE){
            var element = document.selection;
            if (element!=null) {
                var rge = element.createRange();
                if (rge!=null)
                    rge.text=strexpr;
            }
        }else{
            var rulearea = Ext.getCmp(editor);
            var rulevalue = rulearea.getValue();
            var start = rulearea.inputEl.dom.selectionStart;
            var end = rulearea.inputEl.dom.selectionEnd;
            var oriValue = rulearea.getValue().toString();
            rulearea.setValue(oriValue.substring(0,start) + strexpr + oriValue.substring(end));
        }
    },
    //调用条件定义控件的回调函数
    getCon:function(value){
        conditions=value;
    }
});
