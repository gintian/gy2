Ext.define('mysalary.MySalaryMain', {
    extend: 'Ext.panel.Panel',
    layout: 'vbox',
    border:false,
    requires:['mysalary.MySalarySheet'],
    // title:gz.label.mySalaryTitle,
    items: [{
        xtype: 'panel',
        layout: 'hbox',
        border: false,
        width:'100%',
        itemId: 'topPanel',
        margin: '5 0 10 15',
        items: [
            {
                xtype: 'component',
                html: gz.label.yearOfOwnership+"：",
                itemId:'yearOfOwnership',
                margin: '4 0 0 0'
            },
            {
                xtype: 'panel',
                id: 'selectYearPanel',
                //width:'100%',
                layout: 'hbox',
                border: false,
                margin: '0 0 0 3',
                items: [
                    {
                        xtype: 'component',
                        itemId: 'yearLabel',
                        style: 'font-size:16px',
                        // margin: '0 0 0 5',
                        listeners: {
                            element: 'el',
                            click: function () {
                                var yearHtml = mySalaryMain.createYearPanelHtml();
                                if (!yearHtml || mySalaryMain.result.noData) {
                                    return;
                                }
                                mySalaryMain.yearPanel.showBy(Ext.getCmp('selectYearPanel'), 'tc-bc');
                            }
                        }
                    }, {
                        xtype: 'image',
                        src: '/workplan/image/jiantou.png',
                        margin: '10 0 0 0',
                        width: 7,
                        height: 6,
                        listeners: {
                            element: 'el',
                            click: function (t) {
                                var yearHtml = mySalaryMain.createYearPanelHtml();
                                if (!yearHtml || mySalaryMain.result.noData) {
                                    return;
                                }
                                mySalaryMain.yearPanel.showBy(Ext.getCmp('selectYearPanel'), 'tc-bc');
                            }
                        }
                    }
                ]

            }, {
                xtype: 'component',
                itemId:'selectSchemeComponent',
                margin: '3 0 0 10',
                html: ""
            }
        ]
    }, {
        xtype: 'container',
        hidden: true,
        itemId: 'tipsContainer',
        width: '100%',
        margin: '25 0 0 0',
        layout: {
            type: 'vbox',
            align: 'center'
        },
        items: [
            {
                xtype: 'image',
                width: 142,
                height: 142,
                src: '/module/gz/mysalary/images/nomeeting.png'
            }, {
                xtype: 'component',
                itemId: 'tipsComponent',
                html: ''
            }
        ]
    }],
    initComponent: function () {
        mySalaryMain = this;
        mySalaryMain.callParent();
        mySalaryMain.initData();
        Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}", "underline");
        //创建方案查询文字
        if (mySalaryMain.result) {
            var schemes = mySalaryMain.result.schemes;
            var topPanel = mySalaryMain.query('#topPanel')[0];
            if(schemes&&schemes.length>1){
                if (Ext.isArray(schemes)) {
                    mySalaryMain.schemeId = schemes[0].id;
                    for (var i = 0; i < schemes.length; i++) {
                        // (function (i) {
                        var html = "";
                        var id = schemes[i].id;
                        if (mySalaryMain.errorSchemeId == id) {//如果薪酬方案出错禁止该薪酬方案的点击事件
                            html = '<span style="color:#C5C5C5" > ' + schemes[i].name + '</span>'
                        } else {
                            html = '<span style="color:#1B4A98" onclick="javascript:void(0);mySalaryMain.schemeClick(\'' + id +  '\',\'' + schemes[i].id + '\')">' + schemes[i].name + '</span>'//用span标签时 一定要写javascript:void(0)否则傻逼的ie会有各种毛病
                        }
                        var schemeLabel = Ext.create('Ext.Component', {
                            html: html,
                            schemeId: schemes[i].id,
                            itemId: schemes[i].id,
                            style: 'cursor:pointer',
                            margin: i==0?'3 0 0 7':'3 0 0 18',
                            cls: i == 0 ? 'scheme-selected-cls' : ''
                        });
                        topPanel.add(schemeLabel);
                        mySalaryMain.query('#selectSchemeComponent')[0].setHtml(gz.label.mySalaryPlan+"：");
                    }
                }
            }
        }
        //员工薪酬进入时，添加返回按钮
        if(a0100 && nbase){
         	topPanel.add({
        		xtype:'component',
        		flex:1
        	});
           	topPanel.add({
        		xtype:'panel',
        		border:0,
        		margin: '4 20 0 0',
        		html:'<a href="javascript:void(0);" onclick="window.history.go(-1);">'+gz.label.back+'</a>'
        	});
        }
        var tipsContainer = mySalaryMain.query('#tipsContainer')[0];
        var tipsComponent = tipsContainer.query('#tipsComponent')[0];
        if (mySalaryMain.isNotView){
        	mySalaryMain.isNotView = false;
      //  	mySalaryMain.remove(mySalaryMain.query('#topPanel')[0]);
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.isNotviewTable + '</span>');
        	return;
        }
        if (mySalaryMain.isNotHaveSchemes) {
        	mySalaryMain.isNotHaveSchemes = false;
        //    mySalaryMain.remove(mySalaryMain.query('#topPanel')[0]);
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.noSchemeForThisPerson + '</span>');
            return;
        }
        if(mySalaryMain.schemesNotConfigFieldError){
        	mySalaryMain.schemesNotConfigFieldError = false;
        	//mySalaryMain.remove(mySalaryMain.query('#topPanel')[0]);
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.GZSchemeFiledConfigError + '</span>');
            return;
        }
        if (!mySalaryMain.result || mySalaryMain.result.isSelfServiceUser == '0') {//非自助用户
            mySalaryMain.remove(mySalaryMain.query('#topPanel')[0]);
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.isSelfServiceUserTips + '</span>');
            return;
        }
        Ext.getCmp('selectYearPanel').query('#yearLabel')[0].setHtml('<a href="javascript:void(0)">' + mySalaryMain.result.year + '' + gz.label.year + '</a>');
        // mySalaryMain.currentSelectYear = mySalaryMain.result.year;
        mySalaryMain.createYearPanel();
        mySalaryMain.createGridPanel();

    },
    schemeClick: function (schemeId,schemeNewId) {
        mySalaryMain.schemeId = schemeNewId;
        var selectComponent = Ext.ComponentQuery.query('component[cls=scheme-selected-cls]')[0];
        if (selectComponent.schemeId != schemeId) {//说明当前点击的和有选中样式的不是同一个
            selectComponent.removeCls('scheme-selected-cls');
            selectComponent.cls = '';
            var itemid = '#' + schemeId;
            var schemeLabel = mySalaryMain.query(itemid)[0];
            schemeLabel.addCls('scheme-selected-cls');
            schemeLabel.cls = "scheme-selected-cls"
        }
        // mySalaryMain.currentSelectYear = 'queryYear';

        mySalaryMain.initData('', schemeId);
        var tipsContainer = mySalaryMain.query('#tipsContainer')[0];
        var tipsComponent = tipsContainer.query('#tipsComponent')[0];
        if (mySalaryMain.isNotView){
        	mySalaryMain.isNotView = false;
        	mySalaryMain.remove(mySalaryMain.query('#salaryMainGridPanel')[0]);
      //  	mySalaryMain.remove(mySalaryMain.query('#topPanel')[0]);
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.isNotviewTable + '</span>');
        	return;
        }
        if (mySalaryMain.isNotHaveSchemes) {
        	mySalaryMain.isNotHaveSchemes = false;
        	 mySalaryMain.remove(mySalaryMain.query('#salaryMainGridPanel')[0]);
        //    mySalaryMain.remove(mySalaryMain.query('#topPanel')[0]);
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.noSchemeForThisPerson + '</span>');
            return;
        }
        if(mySalaryMain.schemesNotConfigFieldError){
        	mySalaryMain.schemesNotConfigFieldError = false;
        	 mySalaryMain.remove(mySalaryMain.query('#salaryMainGridPanel')[0]);
        	//mySalaryMain.remove(mySalaryMain.query('#topPanel')[0]);
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.GZSchemeFiledConfigError + '</span>');
            return;
        }
    },
    initData: function (year, schemeId) {
        if (!year) {
            year = '';
        }
        var map = new HashMap();
        map.put("type", "Main");
        map.put("year", year + '');
        map.put("a0100",a0100);
        map.put("nbase",nbase);
        map.put('schemeId', schemeId);//说明是点击方案查询
        Rpc({
            functionId: 'GZ000000810',
            async: false,
            success: function (data) {
                var result = Ext.decode(data.responseText);
                mySalaryMain.result = result.return_data;
                if (mySalaryMain.result) {
                    if (mySalaryMain.result.schemes) {
                        mySalaryMain.schemes = Ext.Array.clone(mySalaryMain.result.schemes);
                    }
                }
                if (result.return_code == 'success') {
                    if (schemeId) {
                        mySalaryMain.remove(mySalaryMain.query('#salaryMainGridPanel')[0]);
                        mySalaryMain.createGridPanel();
                        if (!mySalaryMain.result.noData) {
                            if (document.getElementById("yearHtml")) {
                                var yearHtml = mySalaryMain.createYearPanelHtml();
                                document.getElementById("yearHtml").innerHTML = yearHtml;
                            }
                        }
                    }
                } else if (result.return_code == 'fail') {
					if (result.return_msg == 'getViewError'){
						mySalaryMain.isNotView = true;
						return;
					}
                    if (result.return_msg == 'getSchemesError') {
                        mySalaryMain.isNotHaveSchemes = true;
                        return;
                    }
                    if (result.return_msg == 'GZSchemeFiledConfigError'){
                    	mySalaryMain.schemesNotConfigFieldError = true;
                    	return;
                    }
                    // Ext.Msg.alert(gz.label.tips, result.return_msg);
                    var tipsContainer = mySalaryMain.query('#tipsContainer')[0];
                    var tipsComponent = tipsContainer.query('#tipsComponent')[0];
                    tipsContainer.setHidden(false);
                    tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + result.return_msg + '</span>');
                    var schemeName = "";
                    if (!schemeId) {
                        schemeId = mySalaryMain.result.schemes[0].id;
                        schemeName = mySalaryMain.result.schemes[0].name;
                    } else {
                        for (var i = 0; i < mySalaryMain.result.schemes; i++) {
                            if (schemeId == mySalaryMain.result.schemes[i].id) {
                                schemeName = mySalaryMain.result.schemes[i].name;
                            }
                        }
                    }
                    mySalaryMain.errorSchemeId = schemeId;
                    mySalaryMain.result.year = new Date().getFullYear();
                    var itemid = '#' + schemeId;
                    var schemeName = "";
                    for (var i = 0; i < mySalaryMain.schemes.length; i++) {
                        if (schemeId == mySalaryMain.schemes[i].id) {
                            schemeName = mySalaryMain.schemes[i].name;
                        }
                    }
                    if (mySalaryMain.query(itemid)[0]) {
                        mySalaryMain.query(itemid)[0].setHtml('<span style="color:#C5C5C5" > ' + schemeName + '</span>');
                    }
                    if(mySalaryMain.query('#salaryMainGridPanel')[0]){
                        mySalaryMain.remove(mySalaryMain.query('#salaryMainGridPanel')[0]);
                    }
                }
            }
        }, map);
        if (mySalaryMain.result.year&&mySalaryMain.schemeId&&mySalaryMain.mySalarySheet&&mySalaryMain.mySalarySheet.items){
            mySalaryMain.createMySalarySheet('1月');
        } else if(mySalaryMain.mySalarySheet){
            mySalaryMain.mySalarySheet.close();
        }
    },
    /**
     * 创建年份选择Panel
     */
    createYearPanel: function () {
        // var yearList = mySalaryMain.result.yearList;
        // if (!yearList) {
        //     return;
        // }
        // var yearHtml = '';
        // for (var i = 0; i < yearList.length; i++) {
        //     yearHtml = yearHtml + '<li> <a  id="' + yearList[i]
        //         + '" onclick="' + 'mySalaryMain.selectPeriodYear(' + yearList[i]
        //         + ')' + '" href="javascript:void(0)" >' + yearList[i]
        //         + '年</a></li>';
        //
        // }
        var yearHtml = mySalaryMain.createYearPanelHtml();
        // yearHtml = '<ul style="list-style-type:none;margin-left: -19px;margin-top: 0px">' + html + '</ul>'
        mySalaryMain.yearPanel = Ext.create('Ext.panel.Panel', {
            html: '<div id="yearHtml">' + yearHtml + '</div>',
            width: 86,
            maxHeight:300,
            //height: 150,
            id: 'floatPanel',
            floating: true,
            tabIndex: '-1',
            focusable: true,
            scrollable: 'y',
            bodyStyle:"border:0px solid rgb(172,172,172);border-left:none;border-top:none;",//border-color:"+templatenavigation.headerColor,
            style:{//解决火狐150%有滚动条
                border:'1px solid rgb(172,172,172);border-left:none;border-top:none;'
            },
            listeners: {
                blur: function (t) {
                    mySalaryMain.hideYearPanel();
                }
                // beforeshow: function (t) {
                //     if (!yearHtml || mySalaryMain.result.noData) {//薪酬方案没有数据时yearpanel 不予以显示
                //         return false;
                //     } else {
                //         return true;
                //     }
                // }
            }
        });

    },
    /**
     * 选择年份点击事件
     * @param year
     */
    selectPeriodYear: function (year) {
        mySalaryMain.hideYearPanel();
        // mySalaryMain.currentSelectYear = year;
        var yearLabel = '<a href="javascript:void(0)">' + year + '' + gz.label.year + '</a>'
        Ext.getCmp('selectYearPanel').query('#yearLabel')[0].setHtml(yearLabel);
        var currentScheme = Ext.ComponentQuery.query('component[cls=scheme-selected-cls]')[0];
        if (currentScheme){
            var schemeId = currentScheme.schemeId;
        } else {
            var schemeId = mySalaryMain.schemeId;
        }

        mySalaryMain.initData(year, schemeId);
        var gridStore = Ext.StoreManager.lookup('salaryMainGridStore');
        gridStore.loadData(mySalaryMain.result.values, false);

    },
    /**
     * 隐藏年份选择panel
     */
    hideYearPanel: function () {
        var floatPanel = Ext.getCmp('floatPanel');
        if (floatPanel) {
            floatPanel.hide();
        }
    },
    /**
     * 创建gridpanel
     */
    createGridPanel: function () {
        if (!mySalaryMain.result.fields && !mySalaryMain.result.noData) {
            return;
        }
        var tipsContainer = mySalaryMain.query('#tipsContainer')[0];
        var tipsComponent = tipsContainer.query('#tipsComponent')[0];
        if (mySalaryMain.result.noData) {//没有数据
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.noDataForThisScheme + '</span>');
            // Ext.getCmp('selectYearPanel').query('#yearLabel')[0].setHtml('<span style="font-size: 12px;">' + new Date().getFullYear() + '' + gz.label.year + '<span>');
            Ext.getCmp('selectYearPanel').setHidden(true);
            mySalaryMain.query('#yearOfOwnership')[0].setHidden(true);
            if (mySalaryMain.result.schemes) {
                if (mySalaryMain.result.schemes.length == 1) {
                    mySalaryMain.remove(mySalaryMain.query('#topPanel')[0]);
                }
            }
            // if (document.getElementById("yearHtml")) {
            //     document.getElementById("yearHtml").innerHTML = "";
            // }
            return;
        } else {
            tipsContainer.setHidden(true);
            mySalaryMain.createYearPanel();
            Ext.getCmp('selectYearPanel').query('#yearLabel')[0].setHtml('<a href="javascript:void(0)">' + mySalaryMain.result.year + '' + gz.label.year + '</a>');
            Ext.getCmp('selectYearPanel').setHidden(false);
            mySalaryMain.query('#yearOfOwnership')[0].setHidden(false);
        }
        var storeFields = [{name: 'month', type: 'string'}];
        var columns = [
            {
                text: gz.label.payDate,//发薪日期
                sortable: false,
                width: 80,
                align: 'left',
                locked:true,
                dataIndex: 'month',
                menuDisabled:true,
                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                    var number = value.replace(/[^0-9]/g,"");
                    var valueDesc = mySalaryMain.convertDesc(number);
                    var res = '<span style="cursor:pointer;color:#1B4A98;" onclick="javascript:void(0);mySalaryMain.createMySalarySheet(\'' + value + '\')">'+valueDesc+'</span>';
                    return res;
                }

            }
        ];
        var selfColumns = mySalaryMain.result.fields.items;
        for (var i = 0; i < selfColumns.length; i++) {
            var fields = selfColumns[i].fields;
            var fieldList = selfColumns[i].fieldList;//指标数组
            if (fieldList.length == 1){
                for (var j = 0; j < fieldList.length; j++) {
                    var childColumns = {};
                    for (var fieldKey in fieldList[j]){
                        if (fieldList[j][fieldKey] == 'N'){
                            childColumns = {
                                text: selfColumns[i].name,
                                sortable: true,
                                dataIndex: fieldKey,
                                width: 80,
                                align: 'right',
                                xtype: 'numbercolumn',
                                format: '0,000.00',
                                summaryType: 'sum',
                                menuDisabled:true,
                                summaryRenderer: function (value, summaryData, dataIndex) {
                                    if (value == 0 && mySalaryMain.result.zeroItemCtrl == 1) {  //为零项不显示
                                        this.setHidden(true);
                                    }
                                    return value.toFixed(2);//保留两位小数  四舍五入
                                }
                            };
                            storeFields.push({name: fieldKey, type: 'number'});
                        }else {
                            childColumns = {
                                text: selfColumns[i].name,
                                sortable: true,
                                dataIndex: fieldKey,
                                width: 80,
                                align: 'left'
                            };
                            storeFields.push({name: fieldKey, type: 'string'});
                        }
                    }
                }
                columns.push(childColumns);
                continue;
            }else {
                var parentColumns = {
                    text: selfColumns[i].name,
                    sortable: true,
                    align: 'left',
                    columns: [],
                    menuDisabled:true//禁止正序倒序功能性按钮
                };
                for (var j = 0; j < fieldList.length; j++) {
                    var childColumns = {};
                    for (var fieldKey in fieldList[j]){
                        if (fieldList[j][fieldKey] == 'N'){
                            childColumns = {
                                text: fields[fieldKey],
                                sortable: true,
                                dataIndex: fieldKey,
                                width: 80,
                                align: 'right',
                                xtype: 'numbercolumn',
                                format: '0,000.00',
                                summaryType: 'sum',
                                menuDisabled:true,
                                summaryRenderer: function (value, summaryData, dataIndex) {
                                    if (value == 0 && mySalaryMain.result.zeroItemCtrl == 1) {  //为零项不显示
                                        this.setHidden(true);
                                    }
                                    return value.toFixed(2);//保留两位小数  四舍五入
                                }
                            };
                            parentColumns.columns.push(childColumns);
                            storeFields.push({name: fieldKey, type: 'number'});
                        }else {
                            childColumns = {
                                text: fields[fieldKey],
                                sortable: true,
                                dataIndex: fieldKey,
                                width: 80,
                                align: 'left'
                            };
                            parentColumns.columns.push(childColumns);
                            storeFields.push({name: fieldKey, type: 'string'});
                        }
                    }
                }
                columns.push(parentColumns);
            }

        }
        var fieldColumns =[{"payable":"1"},{"taxable":"1"},{"incometax":"1"},{"realpay":"1"}];

        for(var key in mySalaryMain.result.fields){
        	if (key == 'items') {
                continue;
            }
        	var value = mySalaryMain.result.fields[key];
        	for(var i=0 ; i < fieldColumns.length ; i++){
        		if(fieldColumns[i][value]){
        			fieldColumns[i][value]=key;
        			break;
        		}
        	}
        }
        for(var i=0 ; i < fieldColumns.length ; i++){
        	for (var key in fieldColumns[i]) {
        	    if (fieldColumns[i][key]=='1'||fieldColumns[i][key]=='none'){
        	        continue;
                }
                var text = mySalaryMain.convertFieldDesc(key);
                columns.push({
                    text: text,
                    sortable: true,
                    dataIndex: fieldColumns[i][key],
                    width: 80,
                    align: 'right',
                    xtype: 'numbercolumn',
                    format: '0,000.00',
                    summaryType: 'sum',
                    menuDisabled:true,
                    summaryRenderer: function (value, summaryData, dataIndex) {
                        // 四个固定项暂不控制为零项不显示
                        // if(value == 0 && mySalaryMain.result.zeroItemCtrl == 1){
                        //     this.setHidden(true);
                        // }
                        return value.toFixed(2);//保留两位小数  四舍五入
                    }
                });
                storeFields.push({name: fieldColumns[i][key], type: 'number'});
            }

        }
        // 判断columns的长度，以及items薪资结构的长度，从而判断是否显示日期列
        if (columns.length === 1 &&mySalaryMain.result.fields.items.length === 0){
            columns = [];
            Ext.Msg.alert(gz.label.tips, gz.label.emptyScheme);
        }

        var gridStore = Ext.create('Ext.data.Store', {
            storeId: 'salaryMainGridStore',
            fields: storeFields,
            data: mySalaryMain.result.values
        });

        var gridPanel = Ext.create('Ext.grid.Panel', {
            store: gridStore,
            width: '100%',
            flex: 1,
            itemId: 'salaryMainGridPanel',
            // enableColumnResize: false,//禁止改变列宽
            enableColumnMove: false,//禁止拖放列
            stripeRows: false,//表格是否隔行换色
            columnLines: true,//列分割线
            bodyStyle:'margin-top:1px',
            features: [{
                ftype: 'summary',
                dock: 'bottom'
                // remoteRoot:'summaryData'
            }],
            viewConfig: {
                markDirty: false //不显示编辑后的三角
            },
            selModel: {
                selType: 'rowmodel'
            },
            columns: columns,
        });
        mySalaryMain.add(gridPanel);

    }
    ,
    /**
     * 转换成字符描述
     */
    convertFieldDesc: function (value) {
        var text = '';
        if (value == 'payable') {
            text = gz.label.payable;
        } else if (value == 'taxable') {
            text = gz.label.taxableAmount;
        } else if (value == 'incometax') {
            text = gz.label.personalIncomeTax;
        } else if (value == 'realpay') {
            text = gz.label.realWage;
        }
        return text;
    },
    createYearPanelHtml: function () {
        var yearList = mySalaryMain.result.yearList;
        if (!yearList) {
            return '';
        }
        var yearHtml = '';
        for (var i = 0; i < yearList.length; i++) {
            yearHtml = yearHtml + '<li> <a  id="' + yearList[i]
                + '" onclick="' + 'mySalaryMain.selectPeriodYear(' + yearList[i]
                + ')' + '" href="javascript:void(0)" >' + yearList[i]
                + '年</a></li>';

        }
        // yearHtml = '<ul style="list-style-type:none;margin-left: -19px;margin-top: 0px">' + yearHtml + '</ul>'
        yearHtml = '<ul>' + yearHtml + '</ul>'
        return yearHtml;
    },
    /**
     * 转换成字符描述
     */
    convertDesc: function (value) {
        var text = '';
        if (value == '1') {
            text = gz.label.january;
        } else if (value == '2') {
            text = gz.label.february;
        } else if (value == '3') {
            text = gz.label.march;
        } else if (value == '4') {
            text = gz.label.april;
        } else if (value == '5') {
            text = gz.label.may;
        } else if (value == '6') {
            text = gz.label.june;
        } else if (value == '7') {
            text = gz.label.july;
        } else if (value == '8') {
            text = gz.label.august;
        } else if (value == '9') {
            text = gz.label.september;
        } else if (value == '10') {
            text = gz.label.october;
        } else if (value == '11') {
            text = gz.label.november;
        } else if (value == '12') {
            text = gz.label.december;
        }
        return text;
    },
    createMySalarySheet:function (month){
        if (mySalaryMain.mySalarySheet){
            mySalaryMain.mySalarySheet.close();
        }
        var titleDesc = mySalaryMain.result.year + gz.label.year + month + gz.label.payroll;
        mySalaryMain.mySalarySheet = Ext.create("mysalary.MySalarySheet",{
            year : mySalaryMain.result.year,
            title: {
                xtype:'title',
                text:titleDesc,
                height:30,
                style:'font-size: 12px;font-weight: normal;'
            },
            schemeId : mySalaryMain.schemeId,
            month : month,
            zeroItemCtrl:mySalaryMain.result.zeroItemCtrl,
            height:mySalaryMain.body.lastBox.height
        }).show();
    }
});
