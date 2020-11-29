/**
 * 薪资发放-历史填报数据明细页面(明细及人员查询)
 *
 * 2018-08-28 zhanghua
 */

Ext.define('SalaryTemplateUL.SalaryDetails', {
    constructor: function (config) {
        SalaryDetails = this;
        SalaryDetails.imodule = config.imodule;//薪资和保险区分标识  1：保险  否则是薪资
        SalaryDetails.viewtype = config.viewtype;//model=0工资发放进入，=1数据上报
        SalaryDetails.salaryid = config.salaryid;
        SalaryDetails.appdate = config.appdate;//业务日期
        SalaryDetails.count = config.count;//次数
        SalaryDetails.manager = config.manager;// 是否共享用户   0：否或者管理员 1：是
        SalaryDetails.optType = config.optType;// detail 查看明细 queryPerson人员查询
        SalaryDetails.isNeedSalaryarchive = config.isNeedSalaryarchive;//是否涉及到归档表 0 没有 1有
        SalaryDetails.agencyFilter = config.agencyFilter == undefined ? "" : config.agencyFilter;//机构过滤
        SalaryDetails.filterYear=config.filterYear;
        //先加上这里，以后可以传更详细的时间
        SalaryDetails.stime_year=config.stime_year;
        SalaryDetails.stime_month=config.stime_month;
        SalaryDetails.etime_year=config.etime_year;
        SalaryDetails.etime_month=config.etime_month;
        Ext.MessageBox.wait("正在打开，请稍候...", "等待");
        var map = new HashMap();
        map.put("salaryid", SalaryDetails.salaryid);
        map.put("imodule", SalaryDetails.imodule);
        map.put("appdate", SalaryDetails.appdate);
        map.put("viewtype",SalaryDetails.viewtype);
        map.put("count", SalaryDetails.count);
        map.put("manager", SalaryDetails.manager);
        map.put("optType", SalaryDetails.optType);
        map.put("agencyFilter", SalaryDetails.agencyFilter);
        map.put("isNeedSalaryarchive", SalaryDetails.isNeedSalaryarchive);
        SalaryDetails.initMap = map;
        Rpc({
            functionId: 'GZ00000236', success: function (form, action) {
                Ext.MessageBox.close();//关闭遮罩
                var result = Ext.decode(form.responseText);
                if (result.succeed) {
                    SalaryDetails.init(result);
                } else {//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
                    Ext.showAlert(result.message);
                }
            }
        }, map);
    },
    init: function (result) {
        var conditions = result.tableConfig;
        var obj = Ext.decode(conditions);

        var lookStr=result.lookStr;

        var title = "";
        if (SalaryDetails.optType == "detail") {
        	if(SalaryDetails.appdate) {
        		var appdate = SalaryDetails.appdate.split("-");
        		title = appdate[0] + gz.label.year + appdate[1] + gz.label.month + " ";
        	}
            title += gz.label.detailedInformation;//"明细信息";
        } else {
            title = gz.label.personnelInquire;//"人员查询";
        }
        //生成弹出得window
        var win = Ext.widget("window", {
            id: "SalaryDetailsWin",
            maximized: true,//大小适应屏幕
            border: false,
            title: title,
            frame: false,
            resizable: false,
            tools: [{id: 'SalaryDetails_schemeSetting', xtype: 'toolbar', border: false}],
            draggable: false,
            layout: 'fit',
            modal: true,
            closeAction: 'destroy'
        });
        win.show();

        SalaryDetails.tableObj = new BuildTableObj(obj);
        SalaryDetails.tableObj.setSchemeViewConfig({//配置栏目设置参数
            publicPlan: result.isShowPublicPlan=='1'?true:false,
            sum: true,
            lock: true,
            merge: false,
            pageSize: '20'
        });
        var toolBar = Ext.getCmp("SalaryDetails_toolbar");

        //初始化日期选择

        var syear=SalaryDetails.filterYear;
        var eyear=SalaryDetails.filterYear;
        var smon=1;
        var emon=12;

        var button = Ext.create('Ext.Button', {
            text: gz.label.outExcel,//'导出excel',
            margin: '0 5 0 0',
            listeners: {
                click: function () {
                    SalaryDetails.ExportExcel();
                }
            }
        });
        toolBar.add(button);
        if(!SalaryDetails.stime_year && (SalaryDetails.filterYear==undefined||SalaryDetails.filterYear=='')){
            var date = new Date();
            syear = date.getFullYear();
            eyear = date.getFullYear();
            smon = date.getMonth() + 1;
            emon=smon;
        }
        //如果栏目设置保存的时候，记录下原先的
        if(SalaryDetails.stime_year) {
        	syear = SalaryDetails.stime_year;
        	smon = SalaryDetails.stime_month;
        	eyear = SalaryDetails.etime_year;
        	emon = SalaryDetails.etime_month;
        }
        if ("queryPerson" == SalaryDetails.optType) {
            var stime = Ext.create('Ext.container.Container', {
                width: 156,
                margin: '0 15 0 0',
                html: "<div style='padding-top: 3px'>业务日期：从 <a id = 'stime' href='javascript:SalaryDetails.clickMonthPicker(\"stime\");' ><span id='stime_year'>"
                    + syear + "</span>年 <span id='stime_month'>"
                    + smon + "</span>月 <img src='/workplan/image/jiantou.png' /></a></div>"
            });
            var etime = Ext.create('Ext.container.Container', {
                width: 100,
                margin: '0 15 0 0',
                html: "<div style='padding-top: 3px'>至 <a id = 'etime' href='javascript:SalaryDetails.clickMonthPicker(\"etime\");' ><span id='etime_year'>"
                    + eyear + "</span>年 <span id='etime_month'>"
                    + emon + "</span>月 <img src='/workplan/image/jiantou.png' /></a></div>"
            });



            toolBar.add(stime);
            toolBar.add(etime);
        }


        var map = new HashMap();
        map.put("optType", SalaryDetails.optType + "FILTER");
        map.put("salaryid", SalaryDetails.salaryid);
        map.put("isNeedSalaryarchive", SalaryDetails.isNeedSalaryarchive);
        if ("queryPerson" == SalaryDetails.optType) {
            map.put("stime_year", syear + "");
            map.put("stime_month", smon + "");
            map.put("etime_year", syear + "");
            map.put("etime_month", emon + "");
        }
        SalaryDetails.SearchBox = Ext.create("EHR.querybox.QueryBox", {
            hideQueryScheme: true,
            emptyText: lookStr,
            subModuleId: "SalaryDetails",
            customParams: map,
            funcId: "GZ00000236",
            success: SalaryDetails.reloadTable//重新加载数据列表
        });

        toolBar.add(SalaryDetails.SearchBox);
        win.add(SalaryDetails.tableObj.getMainPanel());
        Ext.getDom("SalaryDetails_toolbar").style.padding = "3px 0px 5px 0px";
        if(SalaryDetails.optType == 'queryPerson')
        	Ext.showAlert(gz.label.searchWarn);
    },
    reloadTable: function () {
        var store = Ext.data.StoreManager.lookup('SalaryDetails_dataStore');
        store.load();
    },
    closeSettingWindow: function () {
    	var map = SalaryDetails.initMap;
    	if(Ext.getDom('stime_year')) {
    		map.put("stime_year", Ext.getDom('stime_year').innerHTML + "");
    		map.put("stime_month", Ext.getDom('stime_month').innerHTML + "");
    		map.put("etime_year", Ext.getDom('etime_year').innerHTML + "");
    		map.put("etime_month", Ext.getDom('etime_month').innerHTML + "");
    	}
        Ext.getCmp('SalaryDetailsWin').destroy();
        SalaryDetails = null;
        Ext.require('SalaryTemplateUL.SalaryDetails', function () {
            Ext.create("SalaryTemplateUL.SalaryDetails", map);
        });

    },
    //日期选择控件展开
    clickMonthPicker: function (id) {
        var me = this;
        if (Ext.getCmp("winPicker") != undefined) {
            Ext.getCmp("winPicker").destroy();
        }
        // 显示日期
        var value = new Date();
        var year = Ext.getDom(id + '_year').innerHTML;
        var month = Ext.getDom(id + '_month').innerHTML;
        if (!Ext.isEmpty(year) && !Ext.isEmpty(month)) {
            value = [month - 1, parseInt(year)];

        }
        var x = Ext.get(id).getX() - 65;
        var y = Ext.get(id).getY() + 20;
        var win = Ext.create('Ext.window.Window', {
            id: 'winPicker',
            x: x,
            y: y,
            header: false,
            resizable: false,
            padding: 0,
            items: [{
                xtype: 'monthpicker',
                id: 'monthpicker',
                value: value,
                onSelect: function (picker, selected) {
                    var m = selected[0] + 1;
                    var y = selected[1];

                    Ext.getDom(id + '_year').innerHTML = y;
                    Ext.getDom(id + '_month').innerHTML = m;



                    var map = new HashMap();
                    map.put("salaryid", SalaryDetails.salaryid);
                    map.put("imodule", SalaryDetails.imodule);
                    map.put("optType", SalaryDetails.optType + "CHANGETIME");
                    map.put("isNeedSalaryarchive", SalaryDetails.isNeedSalaryarchive);
                    map.put("stime_year", Ext.getDom('stime_year').innerHTML + "");
                    map.put("stime_month", Ext.getDom('stime_month').innerHTML + "");
                    map.put("etime_year", Ext.getDom('etime_year').innerHTML + "");
                    map.put("etime_month", Ext.getDom('etime_month').innerHTML + "");
                    Rpc({
                        functionId: 'GZ00000236', success: function (form, action) {
                            var result = Ext.decode(form.responseText);
                            if (result.succeed) {
                                SalaryDetails.reloadTable();
                                SalaryDetails.isNeedSalaryarchive=result.isNeedSalaryarchive;
                                var map1 = SalaryDetails.SearchBox.customParams;
                                map1.put(id + '_year', y + "");
                                map1.put(id + '_month', m + "");
                                map1.put('isNeedSalaryarchive', SalaryDetails.isNeedSalaryarchive);
                            } else {
                                Ext.showAlert(result.message);
                            }
                        }
                    }, map);
                },
                listeners: {
                    okclick: 'onSelect',
                    monthdblclick: 'onSelect',
                    yeardblclick: 'onSelect',
                    cancelclick: function () {
                        this.setValue(new Date());
                    }
                }
            }]
        }).show();
        // 日期控件关闭
        Ext.getBody().addListener('click', function (evt, el) {
            if (!win.hidden && id != el.id && el.id.indexOf('monthpicker') < 0) {
                win.destroy();
            }
        });
    },
    //导出excel
    ExportExcel: function () {
        Ext.MessageBox.wait("正在导出，请稍候...", "等待");

        var map = new HashMap();
        map.put("salaryid", SalaryDetails.salaryid);
        map.put("imodule", SalaryDetails.imodule);
        map.put("appdate", SalaryDetails.appdate);
        map.put("viewtype", SalaryDetails.viewtype);
        map.put("count", SalaryDetails.count);
        map.put("manager", SalaryDetails.manager);
        map.put("isNeedSalaryarchive", SalaryDetails.isNeedSalaryarchive);
        map.put("optType", "ExportDetailsExcel");
        Rpc({
            functionId: 'GZ00000236', success: function (form, action) {
                Ext.MessageBox.close();
                var result = Ext.decode(form.responseText);
                if (result.succeed) {
                	var fieldName = getDecodeStr(result.fileName);
                    window.location.target = "_blank";
                    window.location.href = "/servlet/vfsservlet?fileid=" + fieldName +"&fromjavafolder=true";
                } else {
                    Ext.showAlert(result.message);
                }

            }
        }, map);
    }
});