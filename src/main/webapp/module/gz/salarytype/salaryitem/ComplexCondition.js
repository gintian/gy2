/**
 * 薪资类别-薪资项目积累项或导入项弹出框
 * lis 2015-11-5
 */
Ext.define('SalaryTypeUL.salaryitem.ComplexCondition',{
    constructor:function(config){
        condition_me = this;
        condition_me.salaryid = config.salaryid;
        module = config.module;
        callBackfn = config.callBackfn;
        condition_me.fieldid = config.fieldid;
        condition_me.initflag = config.initflag;//2:导入项，1:积累项
        condition_me.formula ="";
        condition_me.heapFlag ="";
        condition_me.fieldSetId ="";
        var map = new HashMap();
        map.put("fieldid",condition_me.fieldid);
        map.put("salaryid",condition_me.salaryid);
        Rpc({functionId:'GZ00000213',async:false,success:function(form,action){
                var result = Ext.decode(form.responseText);
                if(result.succeed){
                    condition_me.formula = decode(result.formula);
                    condition_me.heapFlag = result.heapFlag;
                    condition_me.fieldid = result.fieldid;
                    condition_me.itemtype = result.itemtype;
                    condition_me.fieldSetId =result.fieldsetid;
                }else {
                    Ext.showAlert(result.message);
                }
            }},map);

        condition_me.init();
    },
    init:function()
    {
        var formula = Ext.create('Ext.panel.Panel', {
            border:false,
            //width:600,
            buttonAlign:'right',
            items:[{
                border:false,
                xtype:'textareafield',
                name:'formula',
                id:'shry',
                value:condition_me.formula,
                width:590,
                height:180
            }],
            buttons: [{text:common.button.functionGuide,handler:function(){condition_me.functionWizard();}}]//函数向导
        });

        var buttons = Ext.create('Ext.panel.Panel', {
            border : false,
            items:[{
                xtype:'fieldset',
                title:common.label.operationaSymbol,
                layout:'column',
                width:250,
                height:150,
                style:'algin:center',
                items:[
                    {xtype:'button',text:'0',width:25,height:25,style:'margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}} },
                    {xtype:'button',text:'1',width:25,height:25,style:'margin-left:4px;margin-top:5px',handler: function () { condition_me.symbol('shry','1');}},
                    {xtype:'button',text:'2',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'3',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'4',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'(',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:common.button.If,width:54,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},

                    {xtype:'button',text:'5',width:25,height:25,style:'margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'6',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'7',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'8',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'9',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:')',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:common.button.Else,colspan:2,width:54,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},

                    {xtype:'button',text:'+',width:25,height:25,style:'margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'-',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'*',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'/',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'\\',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'%',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:common.button.And,width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:common.button.Or,width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},

                    {xtype:'button',text:'=',width:25,height:25,style:'margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'>',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'<',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'<>',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'<=',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'>=',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:'~',width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
                    {xtype:'button',text:common.button.Not,width:25,height:25,style:'margin-left:4px;margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}}
                ]
            }]
        });

        var conditionStore = null;
        if(condition_me.initflag == '2'){//导入项
            conditionStore = Ext.create('Ext.data.Store', {
                fields:['dataName','dataValue'],
                data : [
                    {"dataValue":"0", "dataName":gz.label.currentRecord},//当前记录
                    {"dataValue":"1", "dataName":gz.label.firstInMonth},//月内最初第一条
                    {"dataValue":"2", "dataName":gz.label.lastInMonth},//月内最近第一条
                    {"dataValue":"3", "dataName":gz.label.lessFirstInMonth},//小于本次月内最初第一条
                    {"dataValue":"4", "dataName":gz.label.lesslastInMonth},//小于本次月内最近第一条
                    {"dataValue":"5", "dataName":gz.label.sameCountInMonth},//同月同次
                    {"dataValue":"6", "dataName":gz.label.deductedIssued}//扣减同月已发金额
                ]
            });
        }else{//积累项
            conditionStore = Ext.create('Ext.data.Store', {
                fields:['dataName','dataValue'],
                data : [
                    {"dataValue":"0", "dataName":gz.label.notCumulative},//不累积
                    {"dataValue":"1", "dataName":gz.label.accumulationInMonth},//月内累积
                    {"dataValue":"2", "dataName":gz.label.accumulatedInQuarter},//季度内累积
                    {"dataValue":"3", "dataName":gz.label.accumulatedYears},//年内累积
                    {"dataValue":"4", "dataName":gz.label.accumulationUnconditional},//无条件累积
                    {"dataValue":"5", "dataName":gz.label.accumulationSCInQuarterr},//季度内同次累积
                    {"dataValue":"6", "dataName":gz.label.accumulationSCInYear},//年内同次累积
                    {"dataValue":"7", "dataName":gz.label.accumulationInSame},//同次累积
                    {"dataValue":"8", "dataName":gz.label.lessAccumulationInMonth}//小于本次的月内累积
                ]
            });
        }

        //子集数据store
        var fieldSetStore = Ext.create('Ext.data.Store',
            {
                fields:['dataName','dataValue'],
                proxy:{
                    type: 'transaction',
                    functionId:'GZ00000213',
                    extraParams:{
                        flag:'1'
                    },
                    reader: {
                        type: 'json',
                        root: 'list'
                    }
                }
            });

        //指标数据store
        var fieldItemStore = Ext.create('Ext.data.Store',
            {
                fields:['dataName','dataValue'],
                proxy:{
                    type: 'transaction',
                    functionId:'GZ00000213',
                    extraParams:{
                        flag:'2'
                    },
                    reader: {
                        type: 'json',
                        root: 'list'
                    }
                }
            });

        //代码数据store
        var codeItemStore = Ext.create('Ext.data.Store',
            {
                fields:['dataName','dataValue'],
                proxy:{
                    type: 'transaction',
                    functionId:'GZ00000213',
                    extraParams:{
                        flag:'3'
                    },
                    reader: {
                        type: 'json',
                        root: 'list'
                    }
                }
            });

        // 方式
        var heapFlagCom = Ext.widget({
            xtype:'combobox',
            fieldLabel: gz.label.pattern,//方式
            labelAlign:'right',
            store: conditionStore,
            displayField: 'dataName',
            valueField: 'dataValue',
            editable:false,
            queryMode:'local',
            labelWidth:40,
            width:250,
            value:condition_me.heapFlag,
            renderTo: Ext.getBody()
        });
        heapFlagCom.select(condition_me.heapFlag);

        //子集下拉框
        var fieldSetCom = Ext.widget({
            id:'fieldSet_id',
            xtype:'combobox',
            fieldLabel:common.label.fieldset,//子集
            labelAlign:'right',
            store:fieldSetStore,
            displayField:'dataName',
            valueField:'dataValue',
            editable:false,
            queryMode:'local',
            labelWidth:40,
            width:250,
            //style:'margin-top:10px',
            listeners:{
                select:function(combo,ecords){
                    fieldItemStore.load({
                        params:{
                            value:combo.value,
                            flag:'2'
                        },
                        callback: function(record, option, succes){
                        }
                    });
                }
            },
            renderTo: Ext.getBody()
        });

        if(condition_me.initflag == '1'){//如果是积累项则只显示指标下拉框
            fieldItemStore.load({
                params:{
                    value:condition_me.fieldSetId,
                    flag:'2'
                },
                callback: function(records, option, succes){
                }
            });
            fieldSetCom.hide();
        }else{
            fieldSetStore.load(function(records, option, succes){
                if(records.length>1){
                    condition_me.fieldSetId = records[1].data.dataValue;
                    fieldSetCom.select(condition_me.fieldSetId);

                    fieldItemStore.load({
                        params:{
                            value:condition_me.fieldSetId,
                            flag:'2'
                        },
                        callback: function(records, option, succes){
                        }
                    });

                }
            });
        };

        //指标下拉框
        var fieldItemCom = Ext.widget({
            id:'fieldItem_id',
            xtype:'combobox',
            fieldLabel:common.label.item,//指标
            store:fieldItemStore,
            displayField:'dataName',
            valueField:'dataValue',
            editable:false,
            queryMode:'local',
            labelWidth:40,
            width:250,
            labelAlign:'right',
            //style:'margin-top:5px',
            listeners:{
                select:function(combo,records){
                    condition_me.symbol('shry',records[0].data.dataName);
                    codeItemStore.load({
                        params:{
                            value:combo.value,
                            flag:'3'
                        },
                        callback: function(record, option, succes){
                            if(record.length>1){
                                Ext.getCmp('codeItem_id').show();
                            }else{
                                Ext.getCmp('codeItem_id').hide();
                            }
                        }
                    });
                }
            },
            renderTo: Ext.getBody()
        });

        //代码下拉框
        var codeItemCom = Ext.widget({
            id:'codeItem_id',
            xtype:'combobox',
            fieldLabel:common.label.code,
            store:codeItemStore,
            displayField:'dataName',
            valueField:'dataValue',
            editable:false,
            queryMode:'local',
            hidden:true,
            labelWidth:40,
            width:250,
            labelAlign:'right',
            //style:'margin-top:10px',
            listeners:{
                select:function(combo,records){
                    condition_me.symbol('shry',records[0].data.dataName);
                }
            },
            renderTo: Ext.getBody()
        });

        var comBoxItem = Ext.create('Ext.panel.Panel', {
            border:false,
            items:[{
                xtype:'fieldset',
                title:gz.label.referenceItem,//参考项目
                layout: {
                    type: 'vbox',
                    align: 'stretch',
                    pack :'center'
                },
                defaults:{
                    margin:'5,0,0,0'
                },
                width:300,
                height:150,

                items:[heapFlagCom,fieldSetCom,fieldItemCom,codeItemCom]
            }]
        });

        var expression = Ext.create('Ext.panel.Panel', {
            width: 591,
            height: 400,
            id:'expression',
            layout: 'border',
            border:false,
            bodyStyle: 'background:#ffffff;',
            items: [
                { region: "north",border:false,height:215,items:formula,style: {
                        marginTop: '1px'
                    }},
                { region: "west",border:false,  width: 300,items:comBoxItem},
                { region: "center",border:false,margin: '0 0 0 40',items:buttons},
                { region: "east",border:false, width: 1}

            ]
        });

        var title = "";
        if(condition_me.initflag=="1")
            title = gz.label.defineAccumulae;//定义积累项
        else
            title = gz.label.ImportCalFormula;//导入计算公式项
        condition_me.win=Ext.widget("window",{
            title:title,
            //height:450,
            width:600,
            layout:'fit',
            bodyStyle: 'background:#ffffff;',
            modal:true,
            resizable:false,
            closeAction:'destroy',
            items: [{
                xtype:'panel',
                border:false,
                items:[expression]
            }],
            bbar: [
                {xtype:'tbfill'},
                {
                    xtype:'button',text:common.button.ok,//确定
                    handler:function(){
                        condition_me.checkComplexCond();
                    }
                },
                { type: 'button', text:common.button.cancel,
                    handler:function(){
                        condition_me.win.close();
                    }
                },
                {xtype:'tbfill'}
            ]
        });
        condition_me.win.show();
        if(condition_me.formulaType=='2')
            Ext.getCmp('computeCond').hide();
    },

    //将所选指标、代码填充到文本框
    symbol:function(exprId,strexpr){
        Ext.getCmp(exprId).focus();
        if(Ext.isIE10m){
            var element = document.selection;
            if (element!=null) {
                var rge = element.createRange();
                if (rge!=null)
                    rge.text=strexpr;
            }
        }else{
            var rulearea = Ext.getCmp(exprId);
            var rulevalue = rulearea.getValue();
            var start = rulearea.inputEl.dom.selectionStart;
            var end = rulearea.inputEl.dom.selectionEnd;
            var oriValue = rulearea.getValue().toString();
            rulearea.setValue(oriValue.substring(0,start) + strexpr + oriValue.substring(end));
        }
    },

    //校验公式条件
    checkComplexCond:function(){
        var itemtype = condition_me.itemtype;
        var c_expr = Ext.getCmp("shry").getValue();
        var map = new HashMap();
        if(Ext.String.trim(c_expr).length>0)
        {
            c_expr = c_expr.replace( /\r/g, "!" );
            c_expr = c_expr.replace( /\n/g, "`" );
            //规范字符串
            for(var i = 0 ; i<c_expr.length; i++){
                if(c_expr.charAt(i) == "\""){
                    c_expr = c_expr.replace("\"" , "'");
                }
            }

            map.put("c_expr",getEncodeStr(c_expr));
            if(itemtype==null||itemtype=='0')
            {
                map.put("ntype","2");
                map.put("module","2");
            }
            else if(itemtype=='4')//工资类别定义人员范围时应为逻辑型的
            {
                map.put("ntype","4");
                map.put("module","1");
            }
            else
            {
                map.put("module","1");
                var ntype="2";
                if(itemtype=="N")
                    ntype="1";
                if(itemtype=="D")
                    ntype="3";
                map.put("ntype",ntype);
            }
            //检验
            Rpc({functionId:'ZJ100000077',async:false,success: function(form,action){
                    var result = Ext.decode(form.responseText);
                    var base = result.base;
                    if(base == 'ok'){
                        condition_me.saveComplexCond(c_expr);
                    }else{
                        Ext.showAlert(getDecodeStr(result.base));
                        return;
                    }
                }},map);
        }else{
            condition_me.saveComplexCond("");
        }
    },

    //保存公式
    saveComplexCond:function(c_expr){
        var map = new HashMap();
        map.put("salaryid",condition_me.salaryid);
        map.put("heapFlag",condition_me.heapFlag);
        map.put("formula",getEncodeStr(c_expr));
        map.put("module",'1');
        map.put("type",'1');
        map.put("flag",condition_me.initflag);
        map.put("fieldid",condition_me.fieldid);
        Rpc({functionId:'GZ00000214',async:false,success:function(form,action){
                var success = Ext.decode(form.responseText).succeed;
                if (!success) {
                    Ext.showAlert(response.responseText.message);
                }else{
                    condition_me.win.close();
                }
            }},map);
    },

    functionWizard:function(){//函数向导
        Ext.require('EHR.functionWizard.FunctionWizard',function(){
            Ext.create("EHR.functionWizard.FunctionWizard",{keyid:condition_me.salaryid,opt:"1",checktemp:'salary',mode:'xzgl_jsgs',callbackfunc:'condition_me.getfunctionWizard'});
        })
    },
    getfunctionWizard:function(obj){//函数向导回调函数，用来接收返回值
        condition_me.symbol('shry',obj);
    }
});