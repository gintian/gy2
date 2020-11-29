/**
 *批量修改
 */
Ext.define('EHR.subsetview.BatchUpdate',{
	config:{
		setName:'',
		nbase:'',
		selections:'',
	    conditions:'',//条件
	    columnfilter:'',//过滤条件
	    callback:''// 回调函数
    },
    constructor:function(config){
//    	conditions = '';
//    	columnfilter = '';
    	this.config = config;
        me=this;
        ZjGlobal = me;
        me.init();
    },
    init:function(){
    	var comp = this; 
        var width = 1400;
        var height = 650;
        var win = Ext.create('Ext.window.Window',{
            title:'批量修改',
            id:'itemBatchUpdateWinId',
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
                            fieldLabel:"指标名称",
                            labelSeparator:null,
                            id:'ItemNameId',
                            //style:'margin-top:10px',
                            labelAlign:'left',
                            labelWidth:60,
                            width:340,
                            typeAhead:true,
                            store:me.getItemStore(),
                            displayField:'itemdesc',
                            valueField:'itemid',
                            /**
                             * hm.put("fieldsetid", item.getFieldsetid());
		                    hm.put("itemid", item.getItemid());
		                    hm.put("itemdesc", item.getItemdesc());
		                    hm.put("itemtype", item.getItemtype());
		                    hm.put("codesetid", item.getCodesetid());
                             */
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
                            store:me.getItemStore(),
                            displayField:'itemdesc',
                            valueField:'itemid',
//                            displayField:'dataName',
//                            valueField:'dataValue',
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
                            html:'说明：替换框内可直接输入表达式。'
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
                        hidden:true,
                        text:'条件',
                        style:'margin-left:20px;margin-top:'+height*0.18,
                        listeners:{
                            'click':function(){
                                //调用定义条件插件。
                                Ext.Loader.setPath("DECN","../../../components/definecondition");
                                Ext.require('DECN.DefineCondition',function(){
                                    Ext.create("DECN.DefineCondition",{imodule:'0',conditions:'',afterfunc:'ZjGlobal.getCon'});
                                });
                            }
                        }
                    },{
                        xtype:'button',
                        text:'向导',
                        style:'margin-left:20px;margin-top:32px',
//                        style:'margin-left:20px;margin-top:10px',
                        listeners:{
                            'click':function(){
                                Ext.require('EHR.functionWizard.FunctionWizard',function(){
                                    Ext.create("EHR.functionWizard.FunctionWizard",{callbackfunc:'me.getfunctionWizard'});
                                });
                            }
                        }

                    }]
                }]
            }] ,
            bbar:[{xtype:'tbfill'},{
                text:'确定',
                margin:'0 10 0 0 ',
                listeners:{
                    'click':function(){
                       //参考项目选中值
                        var itemid = Ext.getCmp('ItemNameId').getValue();
                        var formula = Ext.getCmp('updateTextId').getValue();
                        //替换为 中 定义的公式内容
                        if(itemid==null||itemid==''){
                        	Ext.showAlert("指标名称不能为空！");
                        	return;
                        }
                        if(formula==null||formula.length==0)
                        {
                        	Ext.showAlert("替换值不能为空！");
                        	return;
                        }
                        
                        var dataInfo=[];
                        var selections = comp.config.selections;
            			for(var i=0;i<selections.length;i++){
            				if(selections[i].data.a0100_e)
            					key = selections[i].data.a0100_e;
            				else if(selections[i].data.b0110_e)
            					key = selections[i].data.b0110_e;
            				else if(selections[i].data.e0122_e)
            					key = selections[i].data.e0122_e;
            				if(selections[i].data.dataIndex)
            					dataIndex = selections[i].data.dataIndex;
            				else if(selections[i].data.i9999)
            					dataIndex = selections[i].data.i9999;
            				dataInfo.push({'key':key,'dataIndex':dataIndex});
            			}
                        var map = new HashMap();
                        map.put("type","batchSave");
                        map.put("setName",comp.config.setName);
                        map.put("nbase",comp.config.nbase);
                        map.put("dataInfo",dataInfo);
                        //页面过滤条件this.conditions
//                        map.put("whl",getEncodeStr(columnfilter));
                        map.put("itemid",getEncodeStr(itemid));
                        map.put("formula",getEncodeStr(formula));
                        Rpc({functionId:'ZJ100000255',async:false,success:function(response,action){
                                var result = Ext.decode(response.responseText);
                                var flag=result.flag;
                                if(flag==true){
                                    Ext.getCmp('itemBatchUpdateWinId').close();
                                  	// 增加回调
                                    comp.config.callback();
                                }else{
                                    Ext.showAlert(result.msg);
                                }
                            }},map);
                    }
                }
            },{
                text:'关闭',
                margin:'0 0 0 10 ',
                listeners:{
                    'click':function(){
                        win.close();
                    }
                }
            },{xtype:'tbfill'}]

        });
        win.show();


    },
    //获取指标store
    getItemStore:function(){
    	var me = this;
    	var templateStore = Ext.create('Ext.data.Store',{
    		//feildsetid=A0E,itemid=a0e01,itemdesc=证书类别
            fields:['itemid','itemdesc','codesetid'],
            proxy:{
                type: 'transaction',
                functionId:'ZJ100000255',
                extraParams:{
                    setName:me.config.setName,
                    nbase:me.config.nbase,
                    type:'loadStore'
                },
                reader: {
                    type: 'json',
                    root: 'data'
                }
            }
        });
//    	templateStore.load();
    	return templateStore;
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
    	ZjGlobal.conditions=value;
    }
});
