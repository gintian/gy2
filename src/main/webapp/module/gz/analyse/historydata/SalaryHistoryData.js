/**
 * 薪资历史数据首页面
 * 2020-1-13
 */
Ext.define('GzAnalyse.historydata.SalaryHistoryData', {
    extend: 'Ext.Panel',
    layout: 'fit',
    requires:['GzAnalyse.historydata.SalaryHistoryDataScope','GzAnalyse.historydata.SalaryHistoryTemplate'],
    initComponent: function () {
    	this.callParent();
        Ext.util.CSS.createStyleSheet(".scheme-selected-cls{border-bottom: 1px solid #1b4a98;text-decoration: none;}", "underline");
        SalaryHistoryData = this;
    	SalaryHistoryData.salaryId = undefined;//薪资类别号
    	SalaryHistoryData.salaryName = undefined;//薪资类别名称
    	SalaryHistoryData.appdate = undefined;//业务日期
    	SalaryHistoryData.appdate_encrypt = undefined;//加密业务日期
    	SalaryHistoryData.count = undefined;//当前次数
    	SalaryHistoryData.count_encrypt = undefined;//当前次数（加密）
        SalaryHistoryData.model = '3';//history = 3未归档 achieve = 4已归档默认未归档
    	SalaryHistoryData.transType = "init";// 页面区分 init:初始化  history：未归档 achieve：归档
        SalaryHistoryData.gz_module = undefined;//区别薪资0和保险1 (加密)
        SalaryHistoryData.tablesubModuleId = undefined;//薪资类别编号salary_xxx
        SalaryHistoryData.viewtype = undefined;//  固定：0  页面区分 0:薪资发放  1:审批  2:上报参数 加密
        SalaryHistoryData.dateList = undefined;//日期组建数据
        SalaryHistoryData.loadData();
    },
    loadData: function () {
        var map = new HashMap();
        map.put("salaryId",SalaryHistoryData.salaryId);
        map.put("salary_date",SalaryHistoryData.appdate);
        map.put("isSelectDate",SalaryHistoryData.isSelectDate);
        map.put("isChangeCount",SalaryHistoryData.isChangeCount);
        map.put("count",SalaryHistoryData.count);
        map.put("transType",SalaryHistoryData.transType);
        map.put("gz_module","0");
 		map.put("viewtype","0");
 	    Rpc({functionId:'GZ00001301',async:false,success:SalaryHistoryData.getTableOK},map);
    },
    //保存栏目设置处理函数
    saveSchemeCallBack:function(){
        SalaryHistoryData.isSelectDate = true;
        SalaryHistoryData.isChangeCount = true;
        SalaryHistoryData.loadData();
    },
    getTableOK:function(res){
		var result = Ext.decode(res.responseText);
		var flag=result.return_code;
		var returnMsgCode=result.return_msg;
		if(flag == 'fail'){
		    if(returnMsgCode == 'GzAnalyse.historydata.getCountError'){
		        Ext.Msg.alert(gz.historyData.tip,gz.historyData.msg.getCountError);
            }else if(returnMsgCode == 'GzAnalyse.historydata.getSalaryIdError'){
		        Ext.Msg.alert(gz.historyData.tip,gz.historyData.msg.getSalaryIdError);
            }else if(returnMsgCode == 'GzAnalyse.historydata.getColumnsFieldListError'){
		        Ext.Msg.alert(gz.historyData.tip,gz.historyData.msg.getColumnsFieldListError);
            }else if(returnMsgCode === 'noPrivData'){
		        Ext.Msg.alert(gz.historyData.tip,gz.historyData.msg.noPrivData);
            }else{
		        Ext.Msg.alert(gz.historyData.tip,gz.historyData.msg.dataError);
            }
            return;
        }
        var conditions=result.return_data.tableConfig;
        var obj = Ext.decode(conditions);
        obj.openColumnQuery = true;
        if (flag == "success" && SalaryHistoryData.tableObj) {
            SalaryHistoryData.appdate = result.appdate;
            SalaryHistoryData.count = result.count;
            SalaryHistoryData.countList = result.countList;
            SalaryHistoryData.count_encrypt = result.return_data.count_encrypt;
            SalaryHistoryData.appdate_encrypt = result.return_data.appdate_encrypt;
            //切换日期组件数据
            SalaryHistoryData.dateList = result.return_data.dateList;
            SalaryHistoryData.selectYear = SalaryHistoryData.appdate.substring(0, 4) + gz.label.year;
            SalaryHistoryData.selectMonthOrder = parseInt(SalaryHistoryData.appdate.substring(SalaryHistoryData.appdate.indexOf("-") + 1, SalaryHistoryData.appdate.lastIndexOf("-")), 10);
            //切换日期标志
            SalaryHistoryData.isSelectDate = false;
            //切换次数标志
            SalaryHistoryData.isChangeCount = false;
            //重新渲染表格组件
            SalaryHistoryData.remove(Ext.getCmp('SalaryHistoryData_mainPanel'));
            SalaryHistoryData.tableObj = new BuildTableObj(obj);
            SalaryHistoryData.tableObj.insertItem(SalaryHistoryData.getTopMenu(SalaryHistoryData.countList), 0);
            SalaryHistoryData.add(SalaryHistoryData.tableObj.getMainPanel());
            SalaryHistoryData.querybox();
        } else if (flag == "success") {
            SalaryHistoryData.salaryId = result.return_data.salaryId_encrypt;
            SalaryHistoryData.transType = "history";
            SalaryHistoryData.appdate = result.appdate;//业务日期
            SalaryHistoryData.appdate_encrypt = result.return_data.appdate_encrypt;
            SalaryHistoryData.count = result.count;//次数
            SalaryHistoryData.count_encrypt = result.return_data.count_encrypt;
            SalaryHistoryData.countList = result.countList;
            SalaryHistoryData.dateList = result.return_data.dateList;//切换日期组件数据
            SalaryHistoryData.reportList = result.return_data.reportList;//工资报表
            SalaryHistoryData.salaryTypeList = result.return_data.salaryTypeList;

            //报表输出用到的参数
            SalaryHistoryData.gz_module = result.gz_module;
            SalaryHistoryData.tablesubModuleId = result.tablesubModuleId;
            SalaryHistoryData.viewtype = result.viewtype;
            SalaryHistoryData.commonreportlist = SalaryHistoryData.reportList;//薪资常用报表数据

            //切换日期标志
            SalaryHistoryData.isSelectDate = false;
            //切换次数标志
            SalaryHistoryData.isChangeCount = false;
            SalaryHistoryData.selectYear = SalaryHistoryData.appdate.substring(0, 4) + gz.label.year;
            SalaryHistoryData.selectMonthOrder = parseInt(SalaryHistoryData.appdate.substring(SalaryHistoryData.appdate.indexOf("-") + 1, SalaryHistoryData.appdate.lastIndexOf("-")), 10);
            SalaryHistoryData.tableObj = new BuildTableObj(obj);

            SalaryHistoryData.tableObj.insertItem(SalaryHistoryData.getTopMenu(SalaryHistoryData.countList), 0);
            SalaryHistoryData.add(SalaryHistoryData.tableObj.getMainPanel());
            SalaryHistoryData.querybox();
        }
        if (flag === 'success') {
            //不延时调获取不到dom对象
            setTimeout(function () {
		        //隐藏tablebuilder组件锁列区域的滚动条
                if (Ext.getDom("SalaryHistoryData_tablePanel-locked-body")) {
                    Ext.getDom("SalaryHistoryData_tablePanel-locked-body").childNodes[0].style['overflow-x'] = "hidden";
                }
            }, 10);
        }
    },
    getTopMenu:function(countList){
    	var toolbar  = Ext.create('Ext.toolbar.Toolbar',{
			border:0,
			id:"toolbar",
			height:25,
			items:[{
				xtype:'label',
				text: gz.historyData.DataSource//'数据来源：'
			},{
				xtype:'label',
                html: '<a href="javascript:SalaryHistoryData.changeHisAch(\'' + "achieve" + '\');" style="color:#1b4a98">'
                    + gz.historyData.archive + '</a> ',//归档
                margin: '0 5 0 0',
                cls: SalaryHistoryData.transType == 'achieve' ? 'scheme-selected-cls' : '',
                schemeId:"achieve",
                itemId:'achieve'
            },{
				xtype:'label',
                html: '<a href="javascript:SalaryHistoryData.changeHisAch(\'' + "history" + '\');" style="color:#1b4a98">'
                    + gz.historyData.notArchive + '</a> ',//未归档
                margin: '0 10 0 5',
                cls: SalaryHistoryData.transType == 'history' ? 'scheme-selected-cls' : '',
                schemeId:"history",
                itemId: 'history'
            },'-'
			]
		});
        var name = SalaryHistoryData.salaryTypeList[0].cname;
        if(SalaryHistoryData.salaryName){
            name = SalaryHistoryData.salaryName;
        }
    	//添加切换薪资类别组件
        if (SalaryHistoryData.salaryTypeList.length > 0) {
            var salaryTypePanel = Ext.create('Ext.form.Panel', {
                border: false,
                style: 'cursor:pointer',
                layout: {
                    type: 'hbox',
                    align: 'center'
                },
                margin: '0 10 0 5',
                items: [{
                    xtype:'component',
                    itemId: 'salaryTypeName',
                    style: 'color:#1b4a98',
                    html: name
                },{
                    xtype:'image',
                    hidden: SalaryHistoryData.salaryTypeList.length > 1 ? false : true,
                    width: 7,
                    height: 6,
                    src: '/workplan/image/jiantou.png'
                }],
                listeners:{
                    element: 'el',
                    click: function () {
                        SalaryHistoryData.changeSalaryId();
                    }
                }
            });
            toolbar.add(salaryTypePanel);
        }
        //添加切换日期组件
    	var datePanel = Ext.create('Ext.form.Panel', {
    	    id:'datePanel',
			border:false,
            style: 'cursor:pointer',
            layout: {
                type: 'hbox',
                align: 'center'
            },
            items:[{
    	        itemId:'dateLabel',
                xtype:'component',
                style: 'color:#1b4a98',
                html: SalaryHistoryData.appdate.substring(0,4)+gz.label.year+
                    parseInt((SalaryHistoryData.appdate.substring(SalaryHistoryData.appdate.indexOf('-')+1,SalaryHistoryData.appdate.lastIndexOf('-'))),10)
                    +gz.label.month
            },{
    	        itemId:'dateImg',
                xtype:'image',
                width: 7,
                height: 6,
                src: '/workplan/image/jiantou.png'
            }],
            listeners:{
                element: 'el',
                click: function () {
                    //点击显示日期组件
                    SalaryHistoryData.changeAppdate();
                }
            }
		});
		if(SalaryHistoryData.appdate){
			toolbar.add(datePanel);
		}

		//添加切换发放次数面板
    	var countPanel = Ext.create('Ext.form.Panel', {
    	    id:'countPanel',
            layout: {
                type: 'hbox',
                align: 'center'
            },
            border: false,
            margin: '0 10 0 10',
            items: [{
                xtype: 'component',
                html:'<span>'+gz.label.ffCount+'：</span>'
            },{
                xtype: 'panel',
    	        id:'realCountPanel',
                layout: {
                    type: 'hbox',
                    align: 'center'
                },
                style: countList.length > 1 ? 'cursor:pointer;' : '',
                border:false,
                items:[{
                    xtype:'component',
                    itemId:'countComponent',
                    style:'color:#1b4a98',
                    html:SalaryHistoryData.count
                },{
                    itemId:'dateImg',
                    xtype:'image',
                    width: 7,
                    height: 6,
                    src: '/workplan/image/jiantou.png'
                }]
            }]
		});
        if (countList.length > 1) {
            Ext.getCmp('realCountPanel').on({
                click:function () {
                    //发放次数大于1时，增加点击显示浮动选择框
                    SalaryHistoryData.showFloatCountPanel(countList);
                },
                element:'el'
            });
            toolbar.add(countPanel);
        }
    	return toolbar;
    },
    querybox:function(){
    	var params = new Object();
		params.appdate = SalaryHistoryData.appdate_encrypt;
		params.salaryId = SalaryHistoryData.salaryId;
		params.count = SalaryHistoryData.count_encrypt;
		params.transType = SalaryHistoryData.transType;
		Ext.getCmp("SalaryHistoryData_querybox").setCustomParams(params);
    },
    /**
     * 常用报表按钮点击事件
     */
    salaryReport:function(){
        if (!SalaryHistoryData.appdate || !SalaryHistoryData.count) {
            Ext.Msg.alert(common.button.promptmessage, gz.historyData.msg.selectBosdateAndCount);//请选择业务日期和发放次数
            return;
        }
        if (SalaryHistoryData.reportList.length === 1) {
            var record = SalaryHistoryData.reportList[0];
            var tabid = record.id;
            var rsid = record.rsid;
            if ("0" === rsid) {
                SalaryHistoryData.showCustom(tabid, gz.label.userDefinedTable, record.text);//'用户自定义表'
            } else {
                var title = "";
                if ("1" === rsid) {
                    title = gz.label.payroll;// "工资条"
                } else if ("2" === rsid) {
                    title = gz.label.payrollSignature;//"工资发放签名表"
                } else if ("3" === rsid) {
                    title = gz.label.salarySummary;//"工资汇总表"
                } else if ("4" === rsid) {
                    title = gz.label.salaryReportAnalysis;//"人员结构分析表"
                }
                SalaryHistoryData.openSalaryReport(rsid, tabid, title, record.text);
            }
            return;
        }
    	var window = Ext.getCmp("commonReportWin");
        if (!window) {
            var tpl = new Ext.XTemplate(
                '<tpl for=".">',
                '<div  style="white-space:nowrap;  height: auto;width:auto;cursor:pointer;margin-bottom: 3px;margin-left: 3px;margin-right: 3px;margin-bottom: 5px" >' +
                ' {text} </div>',
                '</tpl>'
            );

            //方案数据store
            var commonReportListStore = Ext.create('Ext.data.Store', {
                storeId: 'commonReportListStore',
                fields: ['text', 'id'],
                data: SalaryHistoryData.reportList
            });
            var dataView = Ext.create('Ext.view.View', {
                itemSelector: 'div',
                scrollable: 'y',
                tpl: tpl,
                layout: 'fit',
                deferEmptyText: false,
                overItemCls: 'commonReportComboOverCls',
                border: false,
                selectedItemCls: 'commonReportComboSelectedCls',
                store: commonReportListStore,
                multiSelect: false,
                listeners: {
                    select: function (me, record) {
                    	var data=record.data;
                    	var tabid=data.id;
                    	var rsid=data.rsid;
                        if ("0" === rsid) {
                            SalaryHistoryData.showCustom(tabid, gz.label.userDefinedTable, data.text);//'用户自定义表'
                            window.hide();
                        } else {
                            var title = "";
                            if ("1" === rsid) {
                                title = gz.label.payroll;// "工资条"
                            } else if ("2" === rsid) {
                                title = gz.label.payrollSignature;//"工资发放签名表"
                            } else if ("3" === rsid) {
                                title = gz.label.salarySummary;//"工资汇总表"
                            } else if ("4" === rsid) {
                                title = gz.label.salaryReportAnalysis;//"人员结构分析表"
                            }

                            SalaryHistoryData.openSalaryReport(rsid, tabid, title, data.text);
                            window.hide();
                        }
                        me.clearSelections();
                        dataView.refresh();
                    }
                }
            });
            var btnX=Ext.getCmp('common_Report_button').getX();
            var btnY=Ext.getCmp('common_Report_button').getY()+21;

            window = Ext.widget("window", {
                layout: 'fit',
                x: btnX,
                y: btnY,
                minWidth: 150,
                maxHeight: 400,
                scrollable: true,
                header: false,
                modal: false,
                id: 'commonReportWin',
                border: false,
                closeAction: 'destroy',
                items: [dataView],
                listeners: {
                    "render": function () {
                        document.getElementById("commonReportWin").onmouseout = function (e) {
                            if (e == undefined) {
                                e = event;
                            }
                            var s = e.toElement || e.relatedTarget;
                            if (s == undefined || !this.contains(s)) {
                                window.hide();
                            }
                        };
                        //移出常用报表按钮方法
                        document.getElementById("common_Report_button").onmouseout = function (e) {
                            if (e == undefined) {
                                e = event;
                            }
                            var left = this.getBoundingClientRect().left;
                            var top = this.getBoundingClientRect().top;

                            if (!(e.clientX > left && e.clientY + 15 > (top + this.offsetHeight))) {
                                var s = e.toElement || e.relatedTarget;
                                if (s == undefined || !this.contains(s)) {
                                    window.hide();
                                }
                            }
                        };
                    }
                }
            });
            window.show();
        } else {
            if (window.hidden == false) {
                window.hide();
            } else {
                var store=Ext.StoreMgr.get('commonReportListStore');
                //store.load(SalaryHistoryData.reportList);
                store.setData(SalaryHistoryData.reportList);
                window.show();
            }
        }
    },
    /**
     * 薪资报表穿透
     */
    openSalaryReport: function (rsid, tabid, title, text) {
            var param = {};
            param.rsid = rsid;
            param.rsdtlid = tabid;
            param.salaryid = SalaryHistoryData.salaryId;
            param.gz_module = SalaryHistoryData.gz_module;
            param.model = SalaryHistoryData.model;
            param.bosdate = SalaryHistoryData.appdate_encrypt;
            param.count = SalaryHistoryData.count_encrypt;
            param.title = title + '-->' + text;

            Ext.require('SalaryReport.OpenSalaryReport', function () {
                Ext.create("SalaryReport.OpenSalaryReport", param);
            });
    },
    /**
     * 用户自定义报表输出
     */
    showCustom: function (tabid, title, text) {
        var strurl = "/gz/gz_accounting/report/open_gzbanner.do?b_report=link" +
            "&checksalary=salary&opt=int" +
            "&salaryid=" + SalaryHistoryData.tablesubModuleId.split("_")[1] +
            "&tabid=" + tabid +
            "&a_code=" +
            "&subModuleId=" + SalaryHistoryData.tablesubModuleId +
            "&gz_module=" + SalaryHistoryData.gz_module + "&reset=1" +
            "&model=" + SalaryHistoryData.model +
            "&boscount=" + SalaryHistoryData.count +
            "&bosdate=" + SalaryHistoryData.appdate + "&pageRows=init";

        Ext.require('SalaryReport.CreateWindow', function () {
            Ext.create("SalaryReport.CreateWindow", {title: title + "-->" + text, url: strurl});
        });
    },
    //浏览归档,还原数据
    changeHisAch:function(transTypeChange){
        var selectComponent = Ext.ComponentQuery.query('component[cls=scheme-selected-cls]')[0];
        if (selectComponent.schemeId != transTypeChange) {//说明当前点击的和有选中样式的不是同一个
            selectComponent.removeCls('scheme-selected-cls');
            selectComponent.cls = '';
            var itemid = '#' + transTypeChange;
            var schemeLabel = SalaryHistoryData.query(itemid)[0];
            schemeLabel.addCls('scheme-selected-cls');
            schemeLabel.cls = "scheme-selected-cls"
        }
        //切换日期标志
        SalaryHistoryData.isSelectDate = false;
        //切换次数标志
        SalaryHistoryData.isChangeCount = false;
    	SalaryHistoryData.transType = transTypeChange;
    	if('achieve'==transTypeChange){
            SalaryHistoryData.model = '4';
        }else {
            SalaryHistoryData.model = '3';
        }
    	SalaryHistoryData.loadData();
    },
    /**
     * 切换薪资账套window表格
     */
    changeSalaryId:function(){
        Ext.create('GzAnalyse.historydata.SalaryHistoryTemplate', {
            title: gz.historyData.changeSalaryType
        }).show();
    },
    //修改日期
    changeAppdate:function(){
        if (Ext.getCmp("floatPanel")) {
            if (Ext.getCmp("floatPanel").hidden == false) {
                Ext.getCmp("floatPanel").hide();
            }else {
                //切换条件之后刷新日期数据
                Ext.getCmp('dateComp').setTotalData(SalaryHistoryData.dateList);
                Ext.getCmp('dateComp').setCurrentYear(SalaryHistoryData.selectYear);
                Ext.getCmp('dateComp').setCurrentMonth(SalaryHistoryData.selectMonthOrder);
                Ext.getCmp('dateComp').initCurrentYear();
                Ext.getCmp('dateComp').reloadComp();
                Ext.getCmp("floatPanel").showBy(Ext.getCmp('datePanel'),'tc-bc');
            }
        }else{
            var currentYear = SalaryHistoryData.appdate.substring(0,4)+gz.label.year;
            var currentMonth = parseInt(SalaryHistoryData.appdate.substring(SalaryHistoryData.appdate.indexOf("-")+1,SalaryHistoryData.appdate.lastIndexOf("-")),10);
            var dateComp = Ext.create('EHR.attendanceMonth.AttendanceMonthComp',{
                //totalData数据类型：[{year:'2018',monthOrder:1,desc:'1月',state:0},...]//state状态：0：已办；1：进行中；2：代办
                margin:'0 0 0 6',
                totalData: SalaryHistoryData.dateList,
                currentYear:currentYear,//当前年
                currentMonth:currentMonth,//当前月（传monthOrder属性）
                border: false,
                id: 'dateComp',
                onMonthSelected: function (value) {
                    if (value.monthOrder < 10){
                        SalaryHistoryData.appdate = value.year.substring(0,4) + '-0'+ value.monthOrder + '-01';
                    }else{
                        SalaryHistoryData.appdate = value.year.substring(0,4) + '-'+ value.monthOrder + '-01';
                    }
                    SalaryHistoryData.isSelectDate = true;
                    SalaryHistoryData.loadData();
                    Ext.getCmp('datePanel').query('#dateLabel')[0].setHtml(value.year + value.desc);
                    SalaryHistoryData.selectYear = value.year;
                    SalaryHistoryData.selectMonthOrder = value.monthOrder;
                    floatPanel.hide();
                },
                reloadMonthPanel:function () {
                    var currentyear = this.getCurrentYear();
                    if(SalaryHistoryData.selectYear != currentyear){
                        this.setCurrentMonth('');
                    }else{
                        this.setCurrentMonth(SalaryHistoryData.selectMonthOrder);
                    }
                    this.items.items[1].removeAll(true,true);
                    var lineList = this.getMonthHtml();
                    for(var i = 0;i<lineList.length;i++){
                        this.items.items[1].add(lineList[i]);
                    }
                    this.refreshTitlePanel();
                }
            });
            var floatPanel = Ext.create('Ext.panel.Panel',{
                id:'floatPanel',
                height:175,
                width:270,
                floating:true,
                tabIndex:'-1',
                focusable:true,
                items:[dateComp],
                listeners:{
                    blur:function(){
                        var floatPanel=Ext.getCmp('floatPanel');
                        if(floatPanel && !floatPanel.hidden){
                            if(!SalaryHistoryData.isMouseOver){
                                floatPanel.hide();
                            }
                        }
                    },
                    render: function () {
                        document.getElementById("floatPanel").onmouseout = function (e) {
                            SalaryHistoryData.isMouseOver = true;
                            if (e == undefined) {
                                e = event;
                            }
                            var s = e.toElement || e.relatedTarget;
                            if (s == undefined || !this.contains(s)) {
                                floatPanel.hide();
                                SalaryHistoryData.isMouseOver = false;
                            }
                        };
                    }
                }
            });
            floatPanel.showBy(Ext.getCmp('datePanel'),'tc-bc');
        }
    },
    //获取次数浮动面板
    showFloatCountPanel:function(countList){
        var countHtml = SalaryHistoryData.createfloatCountPanelHtml(countList);
        if (Ext.getCmp('floatCountPanel')) {
            Ext.getCmp('floatCountPanel').setHtml('<div id="countHtml">' + countHtml + '</div>');
            Ext.getCmp('floatCountPanel').showBy(Ext.getCmp('realCountPanel'), 'tc-bc');
        }else{
            var floatCountPanel = Ext.create('Ext.panel.Panel',{
                id:'floatCountPanel',
                width:50,
                floating:true,
                tabIndex:'-1',
                focusable:true,
                html: '<div id="countHtml">' + countHtml + '</div>',
                listeners:{
                    blur:function(){
                        var floatCountPanel=Ext.getCmp('floatCountPanel');
                        if(floatCountPanel){
                            floatCountPanel.hide();
                        }
                    }
                }
            });
            floatCountPanel.showBy(Ext.getCmp('realCountPanel'), 'tc-bc');
        }
    },
    //还原数据
    historyData:function(){
        if(SalaryHistoryData.tableObj.dataStore.data.items.length<=0){
            if(SalaryHistoryData.appdate!=""&&SalaryHistoryData.count!=""){
                Ext.Msg.alert(gz.historyData.tip,gz.historyData.msg.noRevertPriv);
            } else {
                Ext.Msg.alert(gz.historyData.tip,gz.historyData.revertDataNo);
            }
        } else {
            Ext.create('GzAnalyse.historydata.SalaryHistoryDataScope',{title:gz.historyData.revert_title}).show();
        }
    },
    //归档数据
    achieveData:function(){
        if(SalaryHistoryData.tableObj.dataStore.data.items.length<=0){
            if(SalaryHistoryData.appdate!=""&&SalaryHistoryData.count!=""){
                Ext.Msg.alert(gz.historyData.tip,gz.historyData.msg.noArchivePriv);
            } else {
                Ext.Msg.alert(gz.historyData.tip,gz.historyData.achieveDataNo);
            }
        } else {
            Ext.create('GzAnalyse.historydata.SalaryHistoryDataScope', {title: gz.historyData.archive_title}).show();
        }
    },
    //删除数据
    deleteData:function(){
    	Ext.create('GzAnalyse.historydata.SalaryHistoryDataScope',{title:gz.historyData.delete_title}).show();
    },
    //报表输出
    emportReport:function(){
        if (!SalaryHistoryData.appdate || !SalaryHistoryData.count) {
            Ext.Msg.alert(common.button.promptmessage, gz.historyData.msg.selectBosdateAndCount);//请选择业务日期和发放次数
            return;
        }
        Ext.require('SalaryReport.SalaryReport', function(){
            Ext.create("SalaryReport.SalaryReport", {
                salaryid: SalaryHistoryData.salaryId,
                gz_module: SalaryHistoryData.gz_module,
                appdate: SalaryHistoryData.appdate_encrypt,
                count: SalaryHistoryData.count_encrypt,
                model: "3",//薪资历史数据
                salaryHistoryModel: SalaryHistoryData.model,//归档、未归档
                tablesubModuleId: SalaryHistoryData.tablesubModuleId,
                viewtype: SalaryHistoryData.viewtype
            });
        });
    },
    //创建选择发放次数浮动面板的html
    createfloatCountPanelHtml:function (countList) {
        var countHtml = '';
        for (var i = 0; i < countList.length; i++) {
            var count = countList[i];
            countHtml = countHtml + '<li> <a  id="count_' + count
                + '" onclick="' + 'SalaryHistoryData.selectCount(' + count
                + ')' + '" href="javascript:void(0)" >' + count
                + '</a></li>';
        }
        countHtml = '<ul>' + countHtml + '</ul>';
        return countHtml;
    },
    //点击次数后刷新数据
    selectCount:function (count) {
        SalaryHistoryData.count = count+'';
        SalaryHistoryData.isChangeCount = true;
        Ext.getCmp('realCountPanel').query('#countComponent')[0].setHtml(count);
        SalaryHistoryData.loadData();
        Ext.getCmp('floatCountPanel').hide();
    }
});
