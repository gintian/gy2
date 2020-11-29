Ext.define('mytax.MyTaxMain', {
    extend: 'Ext.panel.Panel',
    layout: 'vbox',
    // title:gz.label.mySalaryTitle,
    items: [{
        xtype: 'panel',
        layout: 'hbox',
        border: false,
        itemId: 'topPanel',
        margin: '3 0 10 20',
        items: [
            {
                xtype: 'component',
                html: gz.label.yearOfOwnership,
                margin: '4 0 0 0'
            }, {
                xtype: 'panel',
                id: 'selectYearPanel',
                //width:'100%',
                layout: 'hbox',
                border: false,
                margin: '0 0 0 5',
                items: [
                    {
                        xtype: 'component',
                        itemId: 'yearLabel',
                        style: 'font-size:16px',
                        margin: '0 0 0 5',
                        listeners: {
                            element: 'el',
                            click: function () {
                                myTaxMain.yearPanel.showBy(Ext.getCmp('selectYearPanel'), 'tc-bc');
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
                                myTaxMain.yearPanel.showBy(Ext.getCmp('selectYearPanel'), 'tc-bc');
                            }
                        }
                    }
                ]

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
        myTaxMain = this;
        myTaxMain.callParent();
        myTaxMain.initData();
        var tipsContainer = myTaxMain.query('#tipsContainer')[0];
        var tipsComponent = tipsContainer.query('#tipsComponent')[0];
        if (myTaxMain.result.isSelfServiceUser == '0') {//非自助用户
            myTaxMain.remove(myTaxMain.query('#topPanel')[0]);
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.isSelfTaxServiceUserTips + '</span>');
            return;
        }
        if (myTaxMain.result.items && myTaxMain.result.items.length == 0) {//没有配置我的个税
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.noHaveTaxSetting + '</span>');
            myTaxMain.remove(myTaxMain.query('#topPanel')[0]);
            return;
        }
        if (myTaxMain.result.noData) {//没有数据
            tipsContainer.setHidden(false);
            tipsComponent.setHtml('<span style="font-size: 14px;color: #5c5c5c">' + gz.label.noHaveData + '</span>');
            myTaxMain.remove(myTaxMain.query('#topPanel')[0]);
            return;
        }
        Ext.getCmp('selectYearPanel').query('#yearLabel')[0].setHtml('<a href="javascript:void(0)">' + myTaxMain.result.year + '' + gz.label.year + '</a>');
        myTaxMain.currentSelectYear = myTaxMain.result.year;
        myTaxMain.createYearPanel();
        myTaxMain.createGridPanel();

    },
    initData: function (year, schemeId) {
        if (!year) {
            year = '';
        }
        var map = new HashMap();
        map.put("type", "main");
        map.put("year", year + '');
        Rpc({
            functionId: 'GZ00000902',
            async: false,
            success: function (data) {
                var result = Ext.decode(data.responseText);
                if (result.return_code == 'success') {
                    myTaxMain.result = result.return_data;
                    if (year) {
                        myTaxMain.result.values = result.return_data.values;
                    }
                } else if (result.return_code == 'fail') {
                    Ext.Msg.alert(gz.label.tips, result.return_msg);
                }
            }
        }, map);
    },
    /**
     * 创建年份选择Panel
     */
    createYearPanel: function () {
        var yearList = myTaxMain.result.yearList;
        var yearHtml = '';
        for (var i = 0; i < yearList.length; i++) {
            yearHtml = yearHtml + '<li> <a  id="' + yearList[i]
                + '" onclick="' + 'myTaxMain.selectPeriodYear(' + yearList[i]
                + ')' + '" href="javascript:void(0)" >' + yearList[i]
                + '年</a></li>';

        }
        yearHtml = '<ul>' + yearHtml + '</ul>'
        myTaxMain.yearPanel = Ext.create('Ext.panel.Panel', {
            html: '<div id="yearHtml">' + yearHtml + '</div>',
            width: 86,
            maxHeight:300,
            id: 'floatPanel',
            floating: true,
            tabIndex: '-1',
            focusable: true,
            scrollable: 'y',
            listeners: {
                blur: function (t) {
                    myTaxMain.hideYearPanel();
                }
            }
        });

    },
    /**
     * 选择年份点击事件
     * @param year
     */
    selectPeriodYear: function (year) {
        myTaxMain.hideYearPanel();
        myTaxMain.currentSelectYear = year;
        var yearLabel = '<a href="javascript:void(0)">' + year + '' + gz.label.year + '</a>'
        Ext.getCmp('selectYearPanel').query('#yearLabel')[0].setHtml(yearLabel);
        myTaxMain.initData(year);
        var gridStore = Ext.StoreManager.lookup('taxMainGridStore');
        gridStore.loadData(myTaxMain.result.values, false);
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
        var storeFields = [{name: 'month', type: 'string'}];
        var columns = [
            {
                text: gz.label.calculateTaxTime.substr(0, 4),//发薪日期
                sortable: false,
                hideable: false,
                width: 80,
                align: 'left',
                locked: true,
                dataIndex: 'month',
                renderer: function (value) {
                    return myTaxMain.convertDesc(value);
                }
            }
        ];
        var fields = myTaxMain.result.fields;
        for (var i = 0; i < myTaxMain.result.items.length; i++) {
            var key = myTaxMain.result.items[i].itemid;
            var itemType = myTaxMain.result.items[i].itemType;
            var format = myTaxMain.result.items[i].format;
            var childColumns = {
                text: fields[key],
                sortable: false,
                hideable: false,
                dataIndex: key,
                width: 80,
                align: itemType === 'N' ? 'right' : 'left',
                xtype: itemType === 'N' ? 'numbercolumn' : '',
                format: format
            };
            columns.push(childColumns);
            storeFields.push({name: key, type: itemType === 'N' ? 'number' : 'string'});
        }

        var gridStore = Ext.create('Ext.data.Store', {
            storeId: 'taxMainGridStore',
            fields: storeFields,
            data: myTaxMain.result.values
        });
        var gridPanel = Ext.create('Ext.grid.Panel', {
            store: gridStore,
            width: '100%',
            flex: 1,
            itemId: 'taxMainGridPanel',
            bodyStyle: "margin-top:1px",//解决火狐放大到150%导致最顶部的边线缺失
            enableColumnResize: true,//禁止改变列宽
            enableColumnMove: false,//禁止拖放列
            stripeRows: false,//表格是否隔行换色
            columnLines: true,//列分割线
            viewConfig: {
                markDirty: false //不显示编辑后的三角
            },
            selModel: {
                selType: 'rowmodel'
            },
            columns: columns,
            listeners: {
                render: function (panel) {
                    Ext.create('Ext.tip.ToolTip', {
                        target: panel.body,
                        delegate: "td > div.x-grid-cell-inner",
                        shadow: false,
                        trackMouse: true,
                        maxWidth: 800,//最大显示宽度设置为800，文本宽度超过800显示不全解决不了，高度超过显示的最大高度同样也显示不全解决不了  27734 wangb 20170517
                        renderTo: Ext.getBody(),
                        bodyStyle: "background-color:white;border:1px solid #c5c5c5;",
                        listeners: {
                            beforeshow: function updateTipBody(tip) {
                                var div = tip.triggerElement;//.childNodes[0];
                                if (Ext.isEmpty(div))
                                    return false;
                                if (div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight - 4) {
                                    //div 里面内容不允许自动换行，滚动条隐藏 27250 27734  wangb 2017-4-24
                                    tip.update("<div style='white-space:nowrap;overflow:hidden;'>" + div.innerHTML + "</div>");
                                } else
                                    return false;
                            }
                        }
                    });
                }
            }
        });
        myTaxMain.add(gridPanel);
        // gridStore.load();

    }
    ,
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
    }
});