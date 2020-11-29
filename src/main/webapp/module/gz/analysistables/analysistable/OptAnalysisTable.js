Ext.define("Analysistable.OptAnalysisTable", {
    imodule: undefined,	//薪资和保险区分标识  1：保险  否则是薪资
    rsid: undefined,		//报表种类编号
    opt: undefined,		//1:新增  2：编辑 3：编辑第二页数据 4:复制
    rsdtlid: undefined,	//报表编号(编辑时用到)
    name: undefined,		//报表名称(编辑时用到)
    nbase: undefined,	//人员库(编辑时用到)
    salaryids: '',//账套类别(编辑时用到)
    items: undefined,	//报表项目(编辑时用到)
    verifying: undefined,//含审批数据
    activeIndex: 1,
    pageindex: 1,
    callBack: undefined,
    afterSaveOpen:false,
    rsid_enc:'',//加密后的rsid,用于保存分析表后，打开分析表。
    rsdtlid_enc:'',//加密后的rsdtlid,用于保存分析表后，打开分析表。
    constructor: function (config) {
        OptAnalysisTable_me = this;
        Ext.apply(OptAnalysisTable_me, config);
    },
    /**
     * 初始化
     * 1、创建主页面flex=1
     * 2、创建标题、步骤按钮
     * 3、根据标识判断 创建/修改
     * 4、创建三个页面/两个页面，并隐藏未选中
     */
    getMainPanel: function () {
        var me = this;
        var title = "";
        if (me.opt == 1 || me.opt == 4) {
            title = OptAnalysisTable.title.replace("{0}", AnalysisTables.add);
        } else if (me.opt == 2) {
            title = OptAnalysisTable.title.replace("{0}", AnalysisTables.update);
        }
        var mainpanel;
        if(me.opt == 3){
        	mainpanel = this.getSecondPage();
        }else{
	        mainpanel = Ext.create("Ext.panel.Panel", {
	            id: 'infomainpanel',
	            title: title,
	            layout: 'border',
	            border: 0,
	            height: '100%',
	            width: '100%',
	            bodyStyle: 'background-color:#ffffff;',
	            items: me.getChildItems(),
	            bbar:['->', {
	                xtype: 'button',
	                id: 'prevBtn',
	                text: OptAnalysisTable.prev,
	                hidden: true,
	                width: 75,
	                margin: this.opt == 3 ? 0 : '10 0 10 10',
	                height: 22,
	                handler: function () {
	                    if (me.activeIndex == 1) {
	                        return;
	                    }
	                    if(me.activeIndex==2){
	                        var value = Ext.getCmp('tableName').getValue();
	                        if(Ext.isEmpty(value.replace(/\s*/g,""))){
	                            Ext.showAlert(OptAnalysisTable.mustfillText);
	                            return;
	                        }
	                        if(value.length>30){
	                            return;
	                        }
	                    }
	                    Ext.getCmp('stepview').previousStep();
	                    me.activeIndex--;
	                    me.changePage();
	                }
	            }, {
	                xtype: 'button',
	                id: 'nextBtn',
	                text: OptAnalysisTable.next,
	                width: 75,
	                height: 22,
	                margin: this.opt == 3 ? 0 : '10 0 10 10',
	                hidden: (this.opt == 3 || me.rsid == '12') ? true : false,
	                handler: function () {
	                    if (me.activeIndex == 3) {
	                        return;
	                    }
	                    if(me.activeIndex==2){
	                        var value = Ext.getCmp('tableName').getValue();
	                        if(Ext.isEmpty(value.replace(/\s*/g,""))){
	                            Ext.showAlert(OptAnalysisTable.mustfillText);
	                            return;
	                        }
	                        if(value.length>30){
	                            return;
	                        }
	                    }
	                    Ext.getCmp('stepview').nextStep();
	                    me.activeIndex++;
	                    me.changePage();
	                }
	            }, {
	                xtype: 'button',
	                id: 'saveBtn',
	                text: OptAnalysisTable.save,
	                hidden: (this.opt == 3 || me.rsid == '12') ? false : true,
	                width: 75,
	                height: 22,
	                margin: this.opt == 3 ? 0 : '10 0 10 10',
	                handler: function () {
	                    me.saveSalaryTemplate();
	                }
	            }, {
	                xtype: 'button',
	                id: 'backBtn',
	                text: OptAnalysisTable.back,
	                hidden: this.opt == 3 ? true : false,
	                width: 75,
	                margin: this.opt == 3 ? 0 : '10 0 10 10',
	                height: 22,
	                handler: function () {
	                    window.location.href = "/module/gz/analysistables/analysistable/AnalysisTables.html?imodule=" + me.imodule;
	                }
	            }, '->']
	        });
        }
        return mainpanel;
    },
    getChildItems: function () {
    	var me = this;
        //子组件容器
        var items = [];
        if (this.opt != 3 && me.rsid != '12') {
            items.push(this.getStepPanel());
        }
        if (this.opt == 1) {
            items.push(this.getFirstPage());//第一页
        } else {
            items.push(this.getSecondPage());//第二页
        }
        return items;
    },
    getStepPanel: function () {
        var me = this;
        //只有新增是显示第一步
        var steps = [];
        if (me.opt == 1) {//新增
            steps.push({name: OptAnalysisTable.select_template_type});
        }
        steps.push({name: OptAnalysisTable.set_num_cond});
        if (me.rsid != '12') {//薪资分析没有定义统计指标
            steps.push({name: OptAnalysisTable.define_static});
        }
        var stepview = {
            xtype: 'container',
            region: 'north',
            items: [{
                xtype: 'stepview',
                id: 'stepview',
                width: 900,
                margin: '9 0 0 10',
                height: 50,
                largeIcon:true,
                stepData: steps
            }]
        }

        return stepview;
    },
    //第一页主页
    getFirstPage: function () {
        var me = this;
        var pageHeight = Ext.getBody().getViewSize().height;
        var pageWidth = Ext.getBody().getViewSize().width;
        var typeName = me.imodule == 1 ? gz.label.insurance : gz.label.salary2;
        return Ext.widget("container", {
            id: 'picturesPanel',
            region: 'center',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            listeners: {
                afterrender: function () {
                    if (me.rsid == 14 || me.rsid == 5) {
                        me.setcheckedStyle("salaryImg");
                        Ext.getCmp("activeindexPic").setSrc("../../../../module/gz/analysistables/images/preview1" + (me.imodule == 1 ? "_insu" : "") + ".png");
                    } else if (me.rsid == 15 || me.rsid == 6) {
                        me.setcheckedStyle("countImg");
                        Ext.getCmp("activeindexPic").setSrc("../../../../module/gz/analysistables/images/preview2" + (me.imodule == 1 ? "_insu" : "") + ".png");
                    } else if (me.rsid == 16 || me.rsid == 7) {
                        me.setcheckedStyle("tableImg");
                        Ext.getCmp("activeindexPic").setSrc("../../../../module/gz/analysistables/images/preview3" + (me.imodule == 1 ? "_insu" : "") + ".png");
                    } else if (me.rsid == 10) {
                        me.setcheckedStyle("analysisImg");
                        Ext.getCmp("activeindexPic").setSrc("../../../../module/gz/analysistables/images/preview4.png");
                    } else if (me.rsid == 11) {
                        me.setcheckedStyle("unitImg");
                        Ext.getCmp("activeindexPic").setSrc("../../../../module/gz/analysistables/images/preview5.png");
                    }

                }
            },
            items: [{
                xtype: 'container',
                padding: '10 0 0 0',
                scrollable: true,
                flex: 0.45,
                items: [
                    {
                        xtype: 'container',
                        width: '100%',
                        layout: 'column',
                        items: [
                            {
                                xtype: 'container',
                                layout: {
                                    type: 'vbox',
                                    align: 'center'
                                },
                                columnWidth: 0.33,
                                style: 'text-align:center;',
                                height: 130,
                                margin: 10,
                                items: [
                                    {
                                        xtype: 'image',
                                        id: 'salaryImg',
                                        width: 100,
                                        height: 100,
                                        style: 'cursor:pointer;',
                                        margin: '8 0 3 0',
                                        src: '../../../../module/gz/analysistables/images/pay_muster1.png',
                                        listeners: {
                                            click: {
                                                element: 'el',
                                                fn: function (a, o) {

                                                    if (me.rsid == 14 || me.rsid == 5) {
                                                        return;
                                                    }
                                                    //保险
                                                    if (me.imodule == 1)
                                                        me.rsid = 14;
                                                    else
                                                        me.rsid = 5;
                                                    me.setcheckedStyle(o.id);
                                                    Ext.getCmp("activeindexPic").setSrc("../../../../module/gz/analysistables/images/preview1" + (me.imodule == 1 ? "_insu" : "") + ".png");
                                                }
                                            }
                                        }
                                    }, {
                                        xtype: 'label',
                                        text: OptAnalysisTable.user_salary_count.replace("{0}", typeName)
                                    }, {
                                        xtype: 'image',
                                        id: 'salaryImg_sel',
                                        cls: 'selected_cls',
                                        width: 22,
                                        height: 22,
                                        src: '../../../../module/gz/analysistables/images/selected.png'
                                    }
                                ]
                            },
                            {
                                xtype: 'container',
                                layout: {
                                    type: 'vbox',
                                    align: 'center'
                                },
                                height: 130,
                                style: 'text-align:center;cursor:pointer;',
                                columnWidth: 0.33,
                                margin: 10,
                                items: [
                                    {
                                        xtype: 'image',
                                        id: 'countImg',
                                        width: 100,
                                        height: 100,
                                        style: 'cursor:pointer;',
                                        margin: '8 0 3 0',
                                        src: '../../../../module/gz/analysistables/images/pay_muster2.png',
                                        listeners: {
                                            click: {
                                                element: 'el',
                                                fn: function (a, o) {
                                                    if (me.rsid == 15 || me.rsid == 6) {
                                                        return;
                                                    }
                                                    //保险
                                                    if (me.imodule == 1)
                                                        me.rsid = 15;
                                                    else
                                                        me.rsid = 6;
                                                    me.setcheckedStyle(o.id);
                                                    Ext.getCmp("activeindexPic").setSrc("../../../../module/gz/analysistables/images/preview2" + (me.imodule == 1 ? "_insu" : "") + ".png");
                                                }
                                            }
                                        }
                                    }
                                    , {
                                        xtype: 'label',
                                        text: OptAnalysisTable.salary_project_summary_count.replace("{0}", typeName)
                                    },
                                    {
                                        xtype: 'image',
                                        id: 'countImg_sel',
                                        cls: 'selected_cls',
                                        width: 22,
                                        height: 22,
                                        hidden: true,
                                        src: '../../../../module/gz/analysistables/images/selected.png'
                                    }
                                ]
                            },
                            {
                                xtype: 'container',
                                layout: {
                                    type: 'vbox',
                                    align: 'center'
                                },
                                height: 130,
                                style: 'text-align:center;cursor:pointer;',
                                columnWidth: 0.33,
                                margin: 10,
                                items: [
                                    {
                                        xtype: 'image',
                                        id: 'tableImg',
                                        width: 100,
                                        height: 100,
                                        style: 'cursor:pointer;',
                                        margin: '8 0 3 0',
                                        src: '../../../../module/gz/analysistables/images/pay_muster3.png',
                                        listeners: {
                                            click: {
                                                element: 'el',
                                                fn: function (a, o) {
                                                    //保险
                                                    if (me.imodule == 1)
                                                        me.rsid = 16;
                                                    else
                                                        me.rsid = 7;
                                                    me.setcheckedStyle(o.id);
                                                    Ext.getCmp("activeindexPic").setSrc("../../../../module/gz/analysistables/images/preview3" + (me.imodule == 1 ? "_insu" : "") + ".png");
                                                }
                                            }
                                        }
                                    }
                                    , {
                                        xtype: 'label',
                                        text: OptAnalysisTable.user_salary_project_summary_table.replace("{0}", typeName)
                                    }, {
                                        xtype: 'image',
                                        id: 'tableImg_sel',
                                        cls: 'selected_cls',
                                        width: 22,
                                        height: 22,
                                        hidden: true,
                                        src: '../../../../module/gz/analysistables/images/selected.png'
                                    }
                                ]
                            },
                            {
                                xtype: 'container',
                                layout: {
                                    type: 'vbox',
                                    align: 'center'
                                },
                                style: 'text-align:center;cursor:pointer;',
                                hidden: me.imodule == 1,//保险模块没有该分析表
                                columnWidth: 0.33,
                                margin: 10,
                                height: 130,
                                items: [
                                    {
                                        xtype: 'image',
                                        id: 'analysisImg',
                                        width: 100,
                                        height: 100,
                                        style: 'cursor:pointer;',
                                        margin: '8 0 3 0',
                                        src: '../../../../module/gz/analysistables/images/pay_muster4.png',
                                        listeners: {
                                            click: {
                                                element: 'el',
                                                fn: function (a, o) {
                                                    if (me.rsid == 10)
                                                        return;
                                                    me.rsid = 10;
                                                    me.setcheckedStyle(o.id);
                                                    Ext.getCmp("activeindexPic").setSrc("../../../../module/gz/analysistables/images/preview4.png");
                                                }
                                            }
                                        }
                                    }
                                    , {
                                        xtype: 'label',
                                        text: OptAnalysisTable.salary_total_contant_table
                                    },
                                    {
                                        xtype: 'image',
                                        id: 'analysisImg_sel',
                                        cls: 'selected_cls',
                                        width: 22,
                                        height: 22,
                                        hidden: true,
                                        src: '../../../../module/gz/analysistables/images/selected.png'
                                    }
                                ]
                            },
                            {
                                xtype: 'container',
                                layout: {
                                    type: 'vbox',
                                    align: 'center'
                                },
                                height: 130,
                                style: 'text-align:center;cursor:pointer;',
                                columnWidth: 0.33,
                                margin: 10,
                                hidden: me.imodule == 1,//保险模块没有该分析表
                                items: [
                                    {
                                        xtype: 'image',
                                        id: 'unitImg',
                                        width: 100,
                                        height: 100,
                                        style: 'cursor:pointer;',
                                        margin: '8 0 3 0',
                                        src: '../../../../module/gz/analysistables/images/pay_muster5.png',
                                        listeners: {
                                            click: {
                                                element: 'el',
                                                fn: function (a, o) {
                                                    if (me.rsid == 11)
                                                        return;
                                                    me.rsid = 11;
                                                    me.setcheckedStyle(o.id);
                                                    Ext.getCmp("activeindexPic").setSrc("../../../../module/gz/analysistables/images/preview5.png");
                                                }
                                            }
                                        }
                                    }
                                    , {
                                        xtype: 'label',
                                        text: OptAnalysisTable.unit_salary_project_summary
                                    },
                                    {
                                        xtype: 'image',
                                        id: 'unitImg_sel',
                                        cls: 'selected_cls',
                                        width: 22,
                                        height: 22,
                                        hidden: true,
                                        src: '../../../../module/gz/analysistables/images/selected.png'
                                    }
                                ]
                            }]
                    }
                ]
            }, {
                xtype: 'container',
                height: '100%',
                margin: '10 0 20 0',
                style: 'border-right:1px solid #e5e5e5;'
            },
                {
                    xtype: 'panel',
                    border: false,
                    height: '100%',
                    flex: 0.55,
                    scrollable: true,
                    title: OptAnalysisTable.activeindex,
                    bodyStyle: 'border:0',
                    layout: {
                        type: 'vbox',
                        align: 'center'
                    },
                    items: [
                        {
                            xtype: 'image',
                            id: 'activeindexPic',
                            width: pageWidth*0.5>700? 700 : pageWidth*0.5,
                            height: pageHeight*0.55>340? 340 : pageHeight*0.55,
                            margin: '10 0 0 0'
                        }]
                }]
        });
    },
    setcheckedStyle: function (id) {
        var arr = Ext.query(".selected_cls");
        id += "_sel";
        for (var i = 0; i < arr.length; i++) {
            if (arr[i].id == id) {
                Ext.getCmp(id).setVisible(true);
            } else {
                Ext.getCmp(arr[i].id).setVisible(false);
            }
        }
    },
    changePage: function () {
        if (this.activeIndex == 1) {
            Ext.getCmp('prevBtn').setHidden(true);

            Ext.getCmp('picturesPanel').setHidden(false);
            if (Ext.getCmp('numcondpage')) {
                Ext.getCmp('numcondpage').setHidden(true);
            }
        } else if (this.activeIndex == 2) {
            if (this.opt == 1) {
                Ext.getCmp('prevBtn').setHidden(false);
            } else {
                Ext.getCmp('prevBtn').setHidden(true);
            }
            Ext.getCmp('nextBtn').setHidden(false);
            Ext.getCmp('saveBtn').setHidden(true);
            var picturesPanel = Ext.getCmp('picturesPanel');
            if (picturesPanel) {
                picturesPanel.setHidden(true);
            }
            var defineStatiPanel = Ext.getCmp('defineStatiPanel');
            if (defineStatiPanel) {
                defineStatiPanel.setHidden(true);
            }
            var secondPage = Ext.getCmp('numcondpage');
            if (!secondPage) {
                Ext.getCmp("infomainpanel").add(this.getSecondPage());
            } else {
                secondPage.setHidden(false);
            }
        } else if (this.activeIndex == 3) {
            var tableName = Ext.getCmp("tableName").getValue();
            if (Ext.isEmpty(tableName)) {
                Ext.showAlert(OptAnalysisTable.mustfillText);
                this.activeIndex--;
                Ext.getCmp('stepview').previousStep();
                return;
            }
            var nbase = this.getNbases();
            if (Ext.isEmpty(nbase)) {
                Ext.showAlert(gz.label.please + OptAnalysisTable.select_nbase);
                this.activeIndex--;
                Ext.getCmp('stepview').previousStep();
                return;
            }
            var salaryIds = this.getSelectedTemp();
            if (salaryIds == '') {
                Ext.showAlert(OptAnalysisTable.select_salary_type.replace("{0}", this.imodule == 1 ? gz.label.insurance : gz.label.salary));
                this.activeIndex--;
                Ext.getCmp('stepview').previousStep();
                return;
            }
            ;

            //加载treeStore
            this.createTreeData(salaryIds);
            Ext.getCmp('prevBtn').setHidden(false);
            Ext.getCmp('nextBtn').setHidden(true);
            Ext.getCmp('saveBtn').setHidden(false);
            Ext.getCmp('numcondpage').setHidden(true);
            var defineStatiPanel = Ext.getCmp('defineStatiPanel');
            if (!defineStatiPanel) {
                Ext.getCmp("infomainpanel").add(this.getThirdPage());
            } else {
                defineStatiPanel.setHidden(false);
            }
        }
    },
    /**
     * 设置第二页
     */
    getSecondPage: function () {
        var me = this;
        if (me.opt == 2
            || me.opt == 4) {
            this.activeIndex = 2;
        }
        var map = new HashMap();
        map.put('transType', '1');
        var param = new HashMap();
        param.step = 2;
        param.imodule = this.imodule;
        param.rsid = this.rsid;
        param.rsdtlid = this.rsdtlid;
        param.opt = this.opt;
        map.put('param', param);
        var numcondpage = "";
        Rpc({
            functionId: 'GZ00000715',
            async: false,
            success: function (res) {
                var respon = Ext.decode(res.responseText);
                var return_data = respon.return_data;
                me.salaryids = return_data.salaryids;
                var tableConfig = Ext.decode(return_data.tableConfig);
                var nbaseList = return_data.nbaseList;
                var hasUnFinishPower = return_data.hasUnFinishPower;
                var nbaselength = nbaseList.length;
                var columns = nbaselength % 3 == 0 ? nbaselength / 3 : Math.ceil(nbaselength / 3);
                //不限制分页上限500
                tableConfig.limitPageSize=true;
                me.personListGrid = new BuildTableObj(tableConfig);
                // 给表格添加事件
                me.addGridListens(me.personListGrid);
                // 查询组件
                me.subModuleId = "optsalaryset";
                var map = new HashMap();
                map.put('imodule', this.imodule + "");
                me.SearchBox = Ext.create("EHR.querybox.QueryBox", {
                    emptyText: OptAnalysisTable.emptyText.replace("{0}", ""),
                    subModuleId: me.subModuleId,
                    customParams: map,
                    funcId: "GZ00000715",
                    hideQueryScheme: true,
                    queryBoxWidth:me.opt == 3 ?Ext.getBody().getViewSize().width*0.2:290,
                    success: me.searchSalarySetOK
                });
                // 表格toolbar
                var toolbar_data = Ext.create('Ext.toolbar.Toolbar', {
                    items: [me.SearchBox]
                });
                // 表格外的panel
                var gridpanel = Ext.create('Ext.panel.Panel', {
                    bodyStyle: 'border:0',
                    layout: 'fit',
                    border: false,
                    margin: '-15 0 10 105',
                    width: me.opt == 3 ? Ext.getBody().getViewSize().width*0.6-244 : 763,
                    height:me.opt == 3 ? Ext.getBody().getViewSize().height*0.8-226<170?170:Ext.getBody().getViewSize().height*0.8-226 : 320,
                    items: [me.personListGrid.getMainPanel()],
                    tbar: toolbar_data
                })
                numcondpage = Ext.widget("container", {
                    id: 'numcondpage',
                    region: 'center',
                    padding: '0 10',
                    border: 0,
                    height: '100%',
                    scrollable: true,
                    layout: {
                        align: 'center'
                    },
                    items: [{
                        xtype: 'textfield',
                        id: 'tableName',
                        allowBlank: false,
                        maxLength:30,
                        readOnly: me.rsid == '12' ? true : false,//薪资分析不可以编辑
                        hidden:me.opt == 3?true:false,
                        blankText: OptAnalysisTable.mustfillText,
                        fieldLabel: OptAnalysisTable.analy_table,
                        margin: me.rsid == '12'?'35 0 10 0':'10 0 10 0',//自定义报表特殊处理
                        width: 416,
                        value: respon.return_data.name + (me.opt == 4 ? "-1" : "")
                    },
                        {
                            xtype: 'checkboxgroup',
                            id: 'nbaseselect',
                            margin: '10 0 10 0',
                            columns: columns,
                            vertical: true,
                            scrollable :true,
                            width: me.opt == 3 ? Ext.getBody().getViewSize().width*0.6-136 :'100%',
                            defaults:{
                            	width:130
                            },
                            fieldLabel: OptAnalysisTable.select_nbase,
                            labelSeparator:'',
                            items: nbaseList
                        }, {xtype: 'label', text: OptAnalysisTable.select_type}, gridpanel,
                        {
                            xtype: 'checkboxgroup',
                            id: 'verifying',
                            hidden: !hasUnFinishPower,
                            style: 'margin-left:100px;',
                            items: {
                                boxLabel: OptAnalysisTable.iscomplatedata,
                                name: 'iscomplatedata',
                                inputValue: '1',
                                checked: respon.return_data.verifying == '1'
                            },
                        }]
                });
            }
        }, map);
        return numcondpage;
    },
    createTreeData: function (salaryIds) {
        //获取第二页选中的账套
        var salaryIds = this.getSelectedTemp();
        var map = new HashMap();
        map.put('transType', '1');
        var param = new HashMap();
        param.step = 3;
        param.imodule = this.imodule;
        param.rsid = this.rsid;
        param.rsdtlid = this.rsdtlid;
        param.opt = this.opt;
        param.salaryIds = salaryIds;
        map.put('param', param);
        Rpc({
            functionId: 'GZ00000715',
            async: true,
            success: function (res) {
                var respon = Ext.decode(res.responseText);
                var itemList = respon.return_data.itemList;

                var treePanel = Ext.getCmp("treePanel");
                var selectedPanel = Ext.getCmp("selectedPanel");
                //备选指标store
                var treeStore = Ext.create('Ext.data.TreeStore', {
                    fields: ['itemid', 'text'],
                    root: {
                        expanded: true,
                        text: "Root",
                        children: itemList
                    }
                });
                treePanel.setStore(treeStore);
                //已选指标
                var selectedItemList = respon.return_data.selectedItemList;
                var gridStore = Ext.create('Ext.data.Store', {
                    id: 'selectedStore',
                    fields: ['itemid', 'itemdesc'],
                    data: selectedItemList
                });

                selectedPanel.setStore(gridStore);

                treePanel.getView().refresh();
                selectedPanel.getView().refresh();

            }
        }, map);
    },
    /**
     * 设置第三页
     */

    getThirdPage: function () {
        var me = this;
        //创建第三页主页面
        var pageHeight = Ext.getBody().getViewSize().height;
        //创建备选指标面板
        var treepanel = Ext.widget("treepanel", {
            id: 'treePanel',
            width: 300,
            title: OptAnalysisTable.preliminary_item,
            height: pageHeight<550?pageHeight-200:400,
            rootVisible: false,
            viewConfig: {
                markDirty: false //不显示编辑后的三角
            },
            listeners: {
                //鼠标放置子集上显示超链接
                itemmouseenter: function (e, record) {
                    if (record.get('leaf') == false && record.get('text').indexOf(gz.label.addall) == '-1') {
                        record.set('text', record.get('text') + "<button id='addAllChilNodes' style='position:absolute;left:190px;cursor: pointer;border:none;background-color:#FFFACD;color:#0079ff;font-size: 12px;border-radius: 50%;width:60px;height:22px;'>" + gz.label.addall + "</button>");
                    }
                    var aBtn = document.getElementById('addAllChilNodes');

                    if (aBtn != null) {
                        aBtn.onclick = function () {
                            if (!record.isExpanded()) {
                                record.expand();
                            }
                            var children = record.childNodes;
                            for (var i = 0; i < children.length; i++) {
                                children[i].set("checked", true);
                            }
                            if (children.length > 0) {
                                me.addSelectedItems();
                            }
                        }
                    }
                },
               //鼠标移出子集上隐藏超链接
                itemmouseleave: function (e, record) {
                    if (record.get('leaf') == false && record.get('text').indexOf(gz.label.addall) != '-1') {
                        record.set('text', record.get('text').replace("<button id='addAllChilNodes' style='position:absolute;left:190px;cursor: pointer;border:none;background-color:#FFFACD;color:#0079ff;font-size: 12px;border-radius: 50%;width:60px;height:22px;'>" + gz.label.addall + "</button>", ""));
                    }
                }
            }
        });

        //上移按钮
        var orderUpMusBut = Ext.create('Ext.panel.Panel', {
            height: 22,
            width: 32,
            border: false,
            margin: '0,0,20,0',
            id: 'upButOfAdd',
            html: '<img title="' + OptAnalysisTable.itemup + '" src="../../../../module/gz/analysistables/images/up.png" onclick="OptAnalysisTable_me.upBut()"/>',
        });
        //下移按钮
        var orderDownMusBut = Ext.create('Ext.panel.Panel', {
            height: 22,
            width: 32,
            margin: '20,0,0,0',
            border: false,
            html: '<img title="' + OptAnalysisTable.itemdown + '" src="../../../../module/gz/analysistables/images/down.png" onclick="OptAnalysisTable_me.downBut()"/>',
            id: 'downButOfAdd'
        });
        //上移或下移按钮的panel
        var upOrdownBut = Ext.create('Ext.panel.Panel', {
            border: false,
            height: 100,
            width: 60,
            layout: {
                align: 'center',
                type: 'vbox'
            },
            style: 'margin-top:115px',
            items: [orderUpMusBut, orderDownMusBut]
        });
        //添加指标按钮
        var addMusBut = Ext.create('Ext.panel.Panel', {
            height: 22,
            width: 32,
            id: 'addButtonOfAdd',
            margin: '0,0,20,0',
            border: false,
            html: '<img title="' + OptAnalysisTable.itemadd + '" src="../../../../module/gz/analysistables/images/right.png" onclick="OptAnalysisTable_me.addSelectedItems()"/>'
        });
        //删除指标按钮
        var delMusBut = Ext.create('Ext.panel.Panel', {
            height: 22,
            width: 32,
            margin: '20 0 0 0',
            border: false,
            id: 'deleteButOfAdd',
            html: '<img title="' + AnalysisTables.dele + '" src="../../../../module/gz/analysistables/images/left.png" onclick="OptAnalysisTable_me.removeSelectedItems()"/>',
        });
        //删除指标按钮
        var removeAllBtn = Ext.create('Ext.panel.Panel', {
            height: 22,
            width: 32,
            margin: '20 0 0 0',
            border: false,
            id: 'removeAllBtn',
            html: '<img title="' + AnalysisTables.dele_all + '" src="../../../../module/gz/analysistables/images/remove_all.png" onclick="OptAnalysisTable_me.removeSelectedItems(\'all\')"/>',
        });
        //添加或删除按钮的panel
        var addOrdelBut = Ext.create('Ext.panel.Panel', {
            border: false,
            height: 150,
            width:80,
            layout: {
                align: 'center',
                type: 'vbox'
            },
            style: 'margin-top:115px',
            items: [addMusBut, delMusBut,removeAllBtn]
        });
        //已选指标panel
        var selectedPanel = Ext.widget("gridpanel", {
            id: 'selectedPanel',
            title: OptAnalysisTable.selected_item,
            height: pageHeight<550?pageHeight-200:400,
            columnLines: true,
            enableHdMenu: false,//是否显示表头的上下文菜单，默认为true
            enableColumnHide: false,//是否允许通过标题中的上下文菜单隐藏列，默认为true
            sortableColumns: false,
            selType: 'checkboxmodel',
            scrollable: "y",
            hideHeaders: true,
            columns: [
                {
                	flex:1,
                    //width: 280,
                    text: gz.label.FieldName,//指标名称
                    dataIndex: 'itemdesc',
                    align: 'left'
                }],
            width: 300
        });
        return Ext.widget("container", {
            region: 'center',
            id: 'defineStatiPanel',
            scrollable: true,
            layout: {
                align: 'center',
                type: 'hbox',
                pack: 'center'
            },
            items: [treepanel, addOrdelBut, selectedPanel, upOrdownBut]
        });
    },
    //添加已选指标
    addSelectedItems: function () {
        var treePanel = Ext.getCmp("treePanel");
        var datas = treePanel.getChecked();
        var selctedItems = this.getSelectedItems();
        if(datas.length==0){
            Ext.showAlert(OptAnalysisTable.noselected.replace("{0}",OptAnalysisTable.itemadd));
        }
        var records = new Array();
        for (var i = 0; i < datas.length; i++) {
            var itemid = datas[i].get("itemid");
            if (selctedItems.indexOf(itemid) > -1) {
                continue;
            } else {
                if (selctedItems.length > 0) {
                    selctedItems += ",";
                }
                selctedItems += itemid;
            }
            var text = datas[i].get("text");
            records.push({'itemid': itemid, 'itemdesc': text});
        }
        var selectedStore = Ext.data.StoreManager.lookup("selectedStore");
        if (selectedStore) {
            selectedStore.loadData(records, true)//追加方式。
        }

    },
    /**
     * flag 是 all 的时候全部移除
     * @param flag
     */
    removeSelectedItems: function (flag) {
        var selectedPanel = Ext.getCmp("selectedPanel");
        //移除已选指标
        var selectedStore = Ext.data.StoreManager.lookup("selectedStore");
        var gridData = selectedPanel.getSelectionModel().getSelection();
        if(flag=="all"){
            gridData = selectedStore.getRange();
        }
        if (gridData.length < 1) {
            Ext.showAlert(OptAnalysisTable.noselected.replace("{0}", AnalysisTables.dele));
            return;
        }

        var treePanel = Ext.getCmp("treePanel");
        var treeData = treePanel.getChecked();
        var seletedStr = "";
        for (var i = 0; i < gridData.length; i++) {
            if (i > 0) {
                seletedStr += ",";
            }
            seletedStr += gridData[i].get("itemid");
        }

        selectedStore.remove(gridData);
        //取消备选指标选中状态
        for (var i = 0; i < treeData.length; i++) {
            var itemid = treeData[i].get("itemid");
            if (seletedStr.indexOf(itemid) > -1) {
                treeData[i].set("checked", false);
            }
        }
    },
    //下移按钮功能
    downBut: function () {
        var grid = Ext.getCmp("selectedPanel");
        var records = grid.getSelectionModel().getSelection();
        if (records.length == 0) {
            Ext.showAlert(OptAnalysisTable.noselected.replace("{0}", OptAnalysisTable.itemdown));
            return;
        }
        var store = grid.getStore();
        //只调整最后选择的那条记录
        var record = records[records.length - 1];
        var itemid = record.get("itemid");
        var index = 0;
        for (var i = 0; i < store.getCount(); i++) {
            if (itemid == store.getAt(i).get('itemid')) {
                index = i;
                break;
            }
        }

        if (index < store.getCount() - 1) {
            store.removeAt(index);
            store.insert(index + 1, record);
            grid.getView().refresh(); // refesh the row number
            grid.getSelectionModel().select(index + 1);
        }
    },
    //上移按钮功能
    upBut: function () {
        var grid = Ext.getCmp("selectedPanel");
        var records = grid.getSelectionModel().getSelection();
        if (records.length == 0) {
            Ext.showAlert(OptAnalysisTable.noselected.replace("{0}", OptAnalysisTable.itemup));
            return;
        }
        var store = grid.getStore();
        //只调整最后选择的那条记录
        var record = records[records.length - 1];
        var itemid = record.get("itemid");
        var index = 0;
        for (var i = 0; i < store.getCount(); i++) {
            if (itemid == store.getAt(i).get('itemid')) {
                index = i;
                break;
            }
        }
        if (index > 0) {
            store.removeAt(index);
            store.insert(index - 1, record);
            grid.getView().refresh(); // refesh the row number
            grid.getSelectionModel().select(index - 1);
        }
    },
    //获取选中的账套
    getSelectedTemp: function () {
        var me = this;
        var salaryIds = '';
        if (me.salaryIdsMap) {
            for (var key in me.salaryIdsMap) {
                if (me.salaryIdsMap.hasOwnProperty(key)) {
                    if (me.salaryIdsMap[key] && me.salaryIdsMap[key] != '') {
                        if (salaryIds != '')
                            salaryIds += ',';
                        salaryIds += me.salaryIdsMap[key];
                    }
                }
            }
        }
        if (me.salaryids) {
            salaryIds += "," + me.salaryids;
        }
        //去掉无效空格
        if (salaryIds != '') {
            var salarys_ = "";
            var salaryidarr = salaryIds.split(",");
            for (var i = 0; i < salaryidarr.length; i++) {
                var salaryid = salaryidarr[i];
                //将有效的salaryid整合起来
                if (!Ext.isEmpty(salaryid) && salarys_.indexOf(salaryid) == -1) {
                    //以逗号分割多个salaryid
                    if (!Ext.isEmpty(salarys_))
                        salarys_ += ','
                    salarys_ += salaryid;
                }
            }
            salaryIds = salarys_;
        }
        return salaryIds;
    },
    searchSalarySetOK: function () {
        var store = Ext.data.StoreManager.lookup('optsalaryset1_dataStore');
        store.currentPage = 1;
        store.load();
    },
    addGridListens: function (grid) {
        var me = this;
        // 复选框初始选中
        grid.tablePanel.getStore().on('load', function (store, records) {
            var selectModel = grid.tablePanel.getSelectionModel();
            var arrRecords = new Array();
            var salarys=me.salaryids+",";
            if (records.length > 0) {
                for (var i = 0; i < records.length; i++) {
                    var record = records[i];
                    if (salarys.indexOf(record.get('salaryid')+",") > -1) {
                        arrRecords.push(record);
                        me.setSelectedTemp(record.store.currentPage, record.get('salaryid'), true);
                    }
                }
                selectModel.select(arrRecords, false, true);
            }
        }, me);
        // 复选框选中状态
        grid.tablePanel.on('cellclick', function (view, td, cellIndex, record, tr, rowIndex, e, eOpts) {
            if (cellIndex == 0) {
                var isSelect = grid.tablePanel.getSelectionModel().isSelected(record);
                var salaryid = record.data.salaryid;
                if (isSelect) {// 选中
                    if (me.salaryids.indexOf(salaryid) == -1) {
                        me.salaryids += "," + salaryid;
                        me.setSelectedTemp(record.store.currentPage, salaryid, true);
                    }
                } else {// 取消选中
                    if (me.salaryids.indexOf(salaryid) > -1) {
                        me.salaryids = replaceAll(me.salaryids, salaryid, "");
                        me.setSelectedTemp(record.store.currentPage, salaryid, false);
                    }
                }
            }
        }, me);
        // 点击列头事件
        grid.tablePanel.on('headerclick', function (ct, column, e, t, eOpts) {
            // 全选的时候
            if (column.getIndex() == 0) {
                var task = new Ext.util.DelayedTask(function () {
                    var sel = grid.tablePanel.getSelectionModel();
                    var records = [];
                    var currentPage = 0;
                    grid.tablePanel.getStore().getData().each(function (record, index) {
                        records.push(record.data);
                        currentPage = record.store.currentPage;
                    });
                    if (sel.getCount() == records.length) {// 证明全选了
                        for (var i = 0; i < records.length; i++) {
                            var record = records[i];
                            var salaryid = record.salaryid;
                            if (me.salaryids.indexOf(salaryid) == -1) {
                                me.salaryids += "," + salaryid;
                                me.setSelectedTemp(currentPage, salaryid, true);
                            }
                        }
                    } else if (sel.getCount() == 0) {// 全不选了
                        for (var i = 0; i < records.length; i++) {
                            var record = records[i];
                            var salaryid = record.salaryid;
                            if (me.salaryids.indexOf(salaryid) > -1) {
                                me.salaryids = replaceAll(me.salaryids, salaryid, "");
                                me.setSelectedTemp(currentPage, salaryid, false);
                            }
                        }
                    }
                });
                task.delay(1);
            }
        }, me);
        grid.tablePanel.on('afterrender', function (e, eOpts) {
        	if(me.opt == 3)
        		e.columns[1].width = Ext.getBody().getViewSize().width*0.6-340;
        }, me);
    },
    //设置页面选中的数据
    setSelectedTemp: function (page, salaryid, checkfalg) {
        var me = this;
        var records = me.personListGrid.tablePanel.getSelectionModel().getSelection();
        var salaryIds = '';
        if (me.salaryIdsMap) {
            if (me.salaryIdsMap[page])
                salaryIds = me.salaryIdsMap[page];
        }

        if (checkfalg) {
            if (salaryIds.indexOf(salaryid) == -1) {
                if (salaryIds.length > 0) {
                    salaryIds += ',';
                }
                salaryIds += salaryid;
            }
        } else {
            if (salaryIds.indexOf(salaryid) > -1) {
                if (salaryIds.length > 0) {
                    salaryIds = replaceAll(salaryIds, salaryid, "");
                }
            }
        }
        if (!me.salaryIdsMap) {
            me.salaryIdsMap = new HashMap();
        }
        me.salaryIdsMap.put(page, salaryIds);
    },
    /**
     * 获取人员库
     */
    getNbases: function () {
        var nbase = "";
        for (var i = 0; i < Ext.getCmp('nbaseselect').items.items.length; i++) {
            if (Ext.getCmp('nbaseselect').items.items[i].checked) {
                if (nbase.length > 0) {
                    nbase += ',';
                }
                nbase += Ext.getCmp('nbaseselect').items.items[i].name;
            }
        }
        return nbase;
    },
    /**
     * 获取已选指标
     */
    getSelectedItems: function () {
        var items = '';
        var selectedStore = Ext.data.StoreManager.lookup("selectedStore");
        for (var i = 0; i < selectedStore.getCount(); i++) {
            var itemid = selectedStore.getAt(i).get("itemid");
            if (i > 0) {
                items += ",";
            }
            items += itemid;
        }
        return items;
    },
    /**
     * 获取是否含审批数据状态
     */
    getVerifying: function () {
        if (Ext.getCmp('verifying').items.items[0].checked) {
            return '1';
        } else {
            return '0';
        }
    },
    /**
     * 保存分析表
     */
    saveSalaryTemplate: function () {
        var me = this;
        var tableName = Ext.getCmp("tableName").getValue();
        if (Ext.isEmpty(tableName)) {
            Ext.showAlert(OptAnalysisTable.mustfillText);
            return;
        }

        var nbase = this.getNbases();
        if (Ext.isEmpty(nbase)) {
            Ext.showAlert(gz.label.please + OptAnalysisTable.select_nbase);
            return;
        }
        var salaryIds = this.getSelectedTemp();
        if (salaryIds == '') {
            Ext.showAlert(OptAnalysisTable.select_salary_type.replace("{0}", this.imodule == 1 ? gz.label.insurance : gz.label.salary));
            return;
        }

        var name = Ext.getCmp('numcondpage').query('textfield')[0].value;
        var nbase = this.getNbases();
        this.salaryids = this.getSelectedTemp();
        var verifying = this.getVerifying();
        var map = new HashMap();
        map.put('transType', '2');
        var param = new HashMap();
        param.imodule = this.imodule;
        param.rsid = this.rsid;
        param.rsdtlid = this.rsdtlid;
        param.opt = this.opt;
        param.name = getEncodeStr(name);
        param.nbase = nbase;
        //薪资分析表只保存人员库和薪资账套
        if (this.rsid == 12) {
            param.items = "";
        } else if (this.opt != 3) {
            var items = this.getSelectedItems();
            if (items.length == 0) {
                Ext.showAlert(OptAnalysisTable.must_check_project.replace("{0}",this.imodule == 1 ? gz.label.insurance : gz.label.salary))
                return;
            }
            var itemArr = items.split(",");
            var selectedStore = Ext.data.StoreManager.lookup("selectedStore");

            var blank = [];
            for (var i = 0; i < itemArr.length; i++) {
                var rec = selectedStore.findRecord("itemid", itemArr[i]);

                blank.push(itemArr[i] + "`" + rec.get("itemdesc"));
            }
            param.items = blank.join(",");
        }
        param.salaryids = this.salaryids;
        param.verifying = verifying;
        map.put('param', param);
        Rpc({
            functionId: 'GZ00000715',
            async: false,
            success: function (res) {
                var respon = Ext.decode(res.responseText);
                if (respon.return_code == "success") {
                    if (me.callBack) {
                        me.callBack();
                    } else {
                        var mainpanel = Ext.getCmp('mainpanel');
                        mainpanel.destroy();
                        analysisTables_me.getMainView();
                    }
                    //如果是在打开分析表前进入配置页面，保存完成后自动打开分析表
                    if(me.afterSaveOpen){
                        if(me.opt == 1){
                            me.rsdtlid_enc = respon.return_data.rsdtlid_enc;
                            me.rsid_enc = respon.return_data.rsid_enc;
                        }
                        openAnalysisTable(me.rsid,me.rsdtlid_enc,me.rsid_enc,name,me.imodule,false,nbase,me.salaryids,verifying,me.rsdtlid);
                    }else{
                        Ext.showAlert(OptAnalysisTable.save + OptAnalysisTable.success);
                    }

                } else {
                    Ext.showAlert(OptAnalysisTable.save + OptAnalysisTable.fail);
                }
            }
        }, map);
    },
    setRange:function(){
        //将页面作为窗口展现出来
    	var me = this;
        var panel = me.getMainPanel();
        Ext.widget("window", {
            title : gz.label.analysisdata.setrange,
            id:'setrangewin',
            width:Ext.getBody().getViewSize().width*0.6,
            height:Ext.getBody().getViewSize().height*0.8,
            resizable : false,//是否可调整大小的
            autoScroll:true,
            border : false,//边框去掉
            modal : true,//模态窗口
            items : [panel],
            bbar:[{xtype:'tbfill'},{
                text: OptAnalysisTable.save,
                hidden: (me.opt == 3 || me.rsid == '12') ? false : true,
                width: 75,
                height: 22,
                margin: me.opt == 3 ? 0 : '10 0 10 10',
                handler: function () {
                	me.saveSalaryTemplate();
                }},{xtype:'tbfill'}]
        }).show();
    }
});
//全选方法
checkAll = function () {
    var alltemp = document.getElementsByName("isallcheck");
    var templates = document.getElementsByName("ischeck");
    if (alltemp[0].checked) {
        for (var i = 0; i < templates.length; i++) {
            templates[i].checked = true;
        }
    } else {
        for (var i = 0; i < templates.length; i++) {
            templates[i].checked = false;
        }
    }
}