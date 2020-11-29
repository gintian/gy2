Ext.define("Standard.StandardStructure", {
    extend: 'Ext.panel.Panel',
    requires: ['EHR.stepview.StepView', 'Standard.SelectTreeFieldCombox', 'Standard.StandardStructureDetail'],
    hfactorSelectReocrd: {},//横向指标选择数据
    vfactorSelectReocrd: {},//纵向指标选择数据
    afterEnterFunc: undefined,
    viewType: '',//create新建 struct调整结构
    operateType: '', //init 初始化数据 transform转换数据格式
    initComponent: function () {
        StandardStructure = this;
        if (StandardStructure.viewType == 'struct') {
            var param = new HashMap();
            param.put("pkg_id", StandardStructure.pkg_id);
            param.put("stand_id", StandardStructure.stand_id);
            param.put("init_type", StandardStructure.operateType);
            param.put("standStructInfor", StandardStructure.standStructInfor);
            Rpc({
                functionId: 'GZ00001211',
                async: false,
                success: function (data) {
                    var result = Ext.decode(data.responseText);
                    if (result.returnCode == 'success') {
                        StandardStructure.standStructInfor = result.returnData.standStructInfor;
                    } else if (result.returnCode == 'fail') {
                        if (result.returnMsgCode == 'initStandStructError') {
                            Ext.Msg.alert(gz.label.tips, gz.standard.initStandStructError);
                        }
                    }
                }
            }, param);
        }
        StandardStructure.createStandardStructureWindow();

    },
    //下一步跳转方法及数据缓存
    changeStep: function (index) {
        var standardStructureContainer = Ext.getCmp('standardStructureWindow').query("#standardStructureContainer")[0];
        var verticalStandardStructureDetail = Ext.getCmp('verticalStandardStructureDetail');
        var horizontalStandardStructureDetail = Ext.getCmp('horizontalStandardStructureDetail');
        var resultPanel = Ext.getCmp('resultPanel');
        if (index == 0) {
            Ext.getCmp("previousStep").setHidden(true);
            Ext.getCmp("nextStep").setHidden(false);
            Ext.getCmp("ok").setHidden(true);
            verticalStandardStructureDetail.setHidden(true);
            if (horizontalStandardStructureDetail) {
                horizontalStandardStructureDetail.isInit = false;
                horizontalStandardStructureDetail.setHidden(false);
            } else {
                standardStructureContainer.add(Ext.create('Standard.StandardStructureDetail', {
                    prefix: 'horizontal',
                    standStructInfor: StandardStructure.standStructInfor,
                    viewType: StandardStructure.viewType,
                    isInit: true
                }));
            }
        } else if (index == 1) {
            Ext.getCmp("previousStep").setHidden(false);
            Ext.getCmp("nextStep").setHidden(false);
            Ext.getCmp("ok").setHidden(true);
            horizontalStandardStructureDetail.setHidden(true);
            if (resultPanel) {
                resultPanel.setHidden(true);
            }
            if (verticalStandardStructureDetail) {
                verticalStandardStructureDetail.isInit = false;
                verticalStandardStructureDetail.setHidden(false);
            } else {
                standardStructureContainer.add(Ext.create('Standard.StandardStructureDetail', {
                    prefix: 'vertical',
                    standStructInfor: StandardStructure.standStructInfor,
                    viewType: StandardStructure.viewType,
                    isInit: true
                }));
            }
        } else if (index == 2) {
            Ext.getCmp("previousStep").setHidden(false);
            Ext.getCmp("nextStep").setHidden(true);
            Ext.getCmp("ok").setHidden(false);
            verticalStandardStructureDetail.setHidden(true);
            if (!resultPanel) {
                standardStructureContainer.add(this.getResultPanel());
            }else {
                resultPanel.setHidden(false);
            }
        }
    },
    //创建过度总页面
    createStandardStructureWindow: function () {
        StandardStructure.stepview = Ext.widget("stepview", {
            listeners: {
                stepchange: function (stepview, step) {
                    StandardStructure.changeStep(StandardStructure.stepview.currentIndex);
                }
            },
            height: 30,
            freeModel: false,
            stepData: [{name:gz.standard.sdst.firstStepname}, {name:gz.standard.sdst.secondStepname}, {name:gz.standard.sdst.thirdStepname}]
        });
        var StandardStructurebWindow = Ext.create('Ext.window.Window', {
            //title:'机构调整',
            title: StandardStructure.viewType == "struct" ? gz.standard.sdst.titleStruct:gz.standard.sdst.titleCreate,
            height: 492,
            width: 590,
            id: 'standardStructureWindow',
            resizable: false,
            scrollable: 'y',
            buttonAlign: 'center',
            modal:true,
            buttons: [{
                text:OptAnalysisTable.prev,
                id: 'previousStep',
                hidden: true,
                height: 22,
                handler: function () {
                    StandardStructure.stepview.previousStep();
                }
            }, {
                text:OptAnalysisTable.next,
                id: 'nextStep',
                hidden: false,
                height: 22,
                formBind: true,
                handler: function () {
                    if (!StandardStructure.verifyValue()) {
                        return;
                    }
                    StandardStructure.stepview.nextStep();
                }
            }, {
                text:gz.standard.confirm,
                id: 'ok',
                hidden: true,
                height: 22,
                handler: function (t, recond) {
                    var resultFormPanel = Ext.getCmp('resultPanel');
                    var values = resultFormPanel.getValues();
                    var name = values.standardName;
                    var item = values.resultField;
                    var flag = true;
                    if(RegExp(/[(\')]+/).test(name)){
                        resultFormPanel.query('#standardName')[0].setValue('');
                        Ext.Msg.alert(gz.standard.tip,gz.standard.sd.containSpecial);
                        return
                    }
                    if(StandardStructure.viewType == 'create'){
                        if (!item) {
                            Ext.Msg.alert(gz.standard.tip,gz.standard.sdst.tipValueRCom);
                            return;
                        } else {
                            item = item.split("`")[0];
                        }
                    }else {
                        item = StandardStructure.standStructInfor.item.split("`")[0];
                    }
                    if (!name) {
                        Ext.Msg.alert(gz.standard.tip, gz.standard.sdst.tipValueRName);
                        return;
                    }
                    var horizontalStandardStructureDetail = Ext.getCmp('horizontalStandardStructureDetail');
                    var hresult = horizontalStandardStructureDetail.getResult();
                    var hfactor = hresult.factorItemId;
                    var s_hfactor = hresult.sfactorItemId;
                    var hcontent = hresult.factorContent;
                    var verticalStandardStructureDetail = Ext.getCmp('verticalStandardStructureDetail');
                    var vresult = verticalStandardStructureDetail.getResult();
                    var vfactor = vresult.factorItemId;
                    var s_vfactor = vresult.sfactorItemId;
                    var vcontent = vresult.factorContent;
                    
                    var data = new HashMap();
                    data.put("name", name);
                    data.put("item", item);
                    data.put("hfactor", hfactor);
                    data.put("s_hfactor", s_hfactor);
                    data.put("hcontent", hcontent);
                    data.put("vfactor", vfactor);
                    data.put("s_vfactor", s_vfactor);
                    data.put("vcontent", vcontent);
                    if (StandardStructure.afterEnterFunc) {
                        Ext.callback(StandardStructure.afterEnterFunc, StandardStructure, [data]);
                    }
                    StandardStructurebWindow.close();
                }
            }],
            items: [StandardStructure.stepview, {
                xtype: 'container',
                width: '100%',
                flex: 1,
                border: false,
                margin: '12 0 0 17',
                itemId: 'standardStructureContainer',
                items: [Ext.create('Standard.StandardStructureDetail', {
                    prefix: 'horizontal',
                    standStructInfor: StandardStructure.standStructInfor,
                    isInit: true,
                    viewType: StandardStructure.viewType
                })]
            }]
        });
        StandardStructurebWindow.show();
    },
    getResultPanel: function () {
        var resultPanel = Ext.create('Ext.form.Panel', {
                border: false,
                layout: 'vbox',
                id: 'resultPanel',
                border: false,
                margin: '20 0 0 0',
                items: [{
                    width: 270,
                    height:24,
                    layout: 'hbox',
                    border:false,
                    items:[{
                    	width: 220,
                    	height:22,
                    	emptyText: gz.standard.sdst.resultComemptyText,
                        codeFilter: false,
                        name: 'resultField',
                        disabledCls: '.disable{opacity:1 !important}',
                        source: 'A`K`B',
                        xtype: 'selectTreeFieldCombox',
                        filterTypes:'A,M,D',
                        listeners: {
                            render: function (t) {
                                if (StandardStructure.viewType == 'struct') {
                                    t.setValue(StandardStructure.standStructInfor.item);
                                    t.setDisabled(true);
                                }
                            }
                        }
                    },{
                    	 xtype: 'component',
                         margin: '4 0 0 3',
                         html: '<font color="red"> * </font>'
                    }]
                }, {
                    width: 270,
                    height:24,
                    layout: 'hbox',
                    margin:'12 0 0 0',
                    border:false,
                    items:[{
                    	width: 220,
                    	height:22,
                    	emptyText: gz.standard.sdst.resultNamemptyText,
                        itemId:'standardName',
                        name: 'standardName',
                        xtype: 'textfield',
                        listeners: {
                            render: function (t) {
                                if (StandardStructure.viewType == 'struct') {
                                    t.setValue(StandardStructure.standStructInfor.name);
                                }
                            },
                            change: function ( t, newValue, oldValue, eOpts )  {
                            	var containSpecial = RegExp(/[(\ )(\~)(\!)(\@)(\#)(\￥)(\`) (\$)(\%)(\^)(\&)(\*)(\()(\))(\-)(\_)(\+)(\=) (\[)(\])(\{)(\})(\|)(\\)(\;)(\:)(\")(\‘)(\’)(\“)(\”)(\,)(\.)(\/) (\<)(\>)(\?)(\)]+/);
                                if(containSpecial.test(newValue)){
                                    t.setValue(oldValue);
                					Ext.Msg.alert(gz.standard.tip,gz.standard.sd.containSpecial);
                            	}
                            }
                        }
                    },{
                    	 xtype: 'component',
                         margin: '4 0 0 3',
                         html: '<font color="red"> * </font>'
                    }],
                }]
            }
        );
        return resultPanel;
    },
    /**
     * 检验数据格式是否满足要求
     */
    verifyValue: function () {
        var checkFlag = true;
        var result = undefined;
        var hfactor = undefined;
        var s_hfactor = undefined;
        var vfactor = undefined;
        var s_vfactor = undefined;
        var msg = "";
        var verticalStandardStructureDetail = Ext.getCmp('verticalStandardStructureDetail');
        var horizontalStandardStructureDetail = Ext.getCmp('horizontalStandardStructureDetail');
        if (StandardStructure.stepview.currentIndex == 0) {
            result = horizontalStandardStructureDetail.getResult();
        } else if (StandardStructure.stepview.currentIndex == 1) {
            result = verticalStandardStructureDetail.getResult();
            var hresult = horizontalStandardStructureDetail.getResult();
            var hfactor = hresult.factorItemId;
            var s_hfactor = hresult.sfactorItemId;
            var vfactor = result.factorItemId;
            var s_vfactor = result.sfactorItemId;
            if(!hfactor&&!s_hfactor&&!vfactor&&!s_vfactor){
            	msg = gz.standard.sdst.tipValueEmpty;
            	checkFlag = false;
            }
        }
        var factorItemId = result.factorItemId;
        var sfactorItemId = result.sfactorItemId;
        var isSelectFactorItem = result.isSelectFactorItem;
        var isSelectsFactorItem = result.isSelectsFactorItem;
        
        if (factorItemId) {
            if (!isSelectFactorItem) {
                msg = gz.standard.sdst.tipValuefactor;
                checkFlag = false;
            }
        }
        if (sfactorItemId&&StandardStructure.viewType == "create") {
            if (!isSelectsFactorItem) {
                msg = gz.standard.sdst.tipValues_factor;
                checkFlag = false;
            }
        }
        if (!checkFlag) {
            Ext.Msg.alert(gz.standard.tip, msg);
        }
        return checkFlag
    }
});