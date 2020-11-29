Ext.define("mysalary.MySalaryParameter", {
    extend: 'Ext.panel.Panel',
    requires: ['mysalary.MySalarySetting'],
    // title: gz.label.mySalaryTitle,
    layout: {
        type: 'fit'
    },
    header:{
        xtype:'header',
        title: gz.label.mySalaryTitle,
        style:'borderStyle:hidden hidden hidden solid'//解决火狐浏览器缩放150 线条没有的 问题
    },
    salaryMainArr: [],
    constructor: function (config) {
        mysalary_me = this;
        this.callParent();
        this.init(config);
    },
    init: function (config) {
        var map = new HashMap();
        var json = {type: 'main'};
        var jsonStr = JSON.stringify(json);
        map.put("jsonStr", jsonStr);
        Rpc({
            functionId: 'GZ00000801',
            async: false,
            success: function (data) {
                var result = Ext.decode(data.responseText);
                if (result.return_code == 'success') {
                    mysalary_me.salaryMainArr = result.return_data;
                    mysalary_me.createSalaryMain();
                } else if (result.return_code == 'fail') {
                    Ext.Msg.alert(gz.label.tips, result.return_data.return_msg);
                }
            }
        }, map);
    },
    /**
     * 创建薪资配置主界面
     */
    createSalaryMain: function () {
        //创建按钮
        var createBtn = Ext.create('Ext.button.Button', {
            text: gz.label.create,
            scope: this,
            width: 40,
            margin: '0 0 0 0',
            handler: function () {
                Ext.create('mysalary.MySalarySetting', {
                    viewType: 'create'
                });
            }
        });
        //删除按钮
        var delBtn = Ext.create('Ext.button.Button', {
            text: gz.label.del,
            scope: this,
            width: 40,
            margin: '0 0 0 5',
            handler: function () {
                mysalary_me.delSalaryPlan();
            }
        });
        //toolbar工具栏
        var toolbar = Ext.create('Ext.toolbar.Toolbar', {
            border: 0,
            margin: '1 1 1 1',
            items: [createBtn, delBtn]
        });
        //grid面板 的Store
        var mianStore = Ext.create('Ext.data.Store', {
            storeId: 'mainStore',
            // 增加排序字段
            fields: ['id', 'name', 'salary_table', 'B0110_name', 'salary_table_name', 'role_name','norder'],
            // 自动加载
            data: mysalary_me.salaryMainArr
        });
        //界面的grid面板
        var salaryGrid = Ext.create("Ext.grid.Panel", {
            store: mianStore,
            itemId: "salaryGrid",
            bodyStyle:"margin-top:1px",
            enableColumnResize: true,//改变列宽
            enableColumnMove: false,//拖放列
            stripeRows: false,//表格是否隔行换色
            columnLines: true,//列分割线
            tbar: toolbar,
            viewConfig: {
                markDirty: false, //不显示编辑后的三角
                plugins: {//添加拖拽插件
                    ptype: 'gridviewdragdrop',
                    dragText: "调整顺序"//调整顺序
                },
            },
            selModel: Ext.create("Ext.selection.CheckboxModel", {
                mode: "multi",//multi,simple,single；默认为多选multi
                checkOnly: true,//如果值为true，则只用点击checkbox列才能选中此条记录
                enableKeyNav: true,
                getHeaderConfig: function() {
                    var me = this,
                        showCheck = me.showHeaderCheckbox !== false;
                    return {
                        xtype: 'gridcolumn',
                        ignoreExport: true,
                        isCheckerHd: showCheck,
                        text : '&#160;',
                        clickTargetName: 'el',
                        width: me.headerWidth,
                        sortable: false,
                        draggable: false,
                        resizable: false,
                        hideable: false,
                        menuDisabled: true,
                        level:me.level,
                        dataIndex: '',
                        // tdCls: me.tdCls,//去除checkbox 选中样式
                        cls: Ext.baseCSSPrefix + 'column-header-checkbox ',
                        defaultRenderer: me.renderer.bind(me),
                        editRenderer: me.editRenderer || me.renderEmpty,
                        locked: me.hasLockedHeader(),
                        processEvent: me.processColumnEvent
                    };
                }
            }),
            columns: [
                {
                    text: gz.label.programName,
                    dataIndex: 'name',
                    sortable: false,
                    hideable: false,
                    align: 'left',
                    width: 180,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        value = "<a href=\"javascript:mysalary_me.showViewById('" + record.data.id + "');\" >" + value + "<a/>";
                        return value;
                    }
                },// 方案名称
                {
                    text: gz.label.salaryScale,
                    dataIndex: 'salary_table_name',
                    sortable: false,
                    hideable: false,
                    align: 'left',
                    width: 180
                },//薪资表
                {
                    text: gz.label.affiliatedOrganization,
                    dataIndex: 'B0110_name',
                    sortable: false,
                    hideable: false,
                    align: 'left',
                    width: 200
                },// 所属组织
                {
                    text: gz.label.visibleRange,
                    dataIndex: 'role_name',
                    sortable: false,
                    hideable: false,
                    align: 'left',
                    width: 300
                }// 角色权限
            ],
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
                },
                drop: function () {
                    // 获取grid数据
                    var gridStore = salaryGrid.getStore().data.items;
                    var sortArray = [];//定义一个空的排序数组
                    for (var i = 0; i < gridStore.length; i++) {
                        gridStore[i].data.norder = i;
                        sortArray.push(gridStore[i].data.norder+'`'+gridStore[i].data.id);
                    }
                    var map = new HashMap();
                    map.put("type", "saveNorder");
                    map.put('sortArray', sortArray);
                    //保存数据到数据库
                    Rpc({
                        functionId: 'GZ00000805', success: function (response) {
                            var result = Ext.decode(response.responseText);
                            if (result.return_code == "success") {
                                var data = result.return_data.salaryScheme;
                                mysalary_me.salaryMainArr = data;
                            }
                        },
                    }, map);
                }
            }
        });
        mysalary_me.add(salaryGrid);
    },
    /**
     * 删除方案
     */
    delSalaryPlan: function () {
        var salaryGrid = mysalary_me.query("#salaryGrid")[0];
        var records = salaryGrid.getSelectionModel().getSelection();
        var idarr = [];
        if (records.length == 0) {
            Ext.Msg.alert(gz.label.tips, gz.label.selectOnePlans);
            return;
        }
        Ext.Msg.confirm(gz.label.tips, gz.label.isDelPlanSuccess, function (t) {
            if (t == 'yes') {
                for (var i = 0, len = records.length; i < len; i++) {
                    idarr.push(records[i].id);
                }
                var ids = idarr.join(",");
                var map = new HashMap();
                map.put("type", "delete");
                map.put("ids", ids);
                var delRecords = [];
                Rpc({
                    functionId: 'GZ00000805',
                    async: false,
                    success: function (data) {
                        var result = Ext.decode(data.responseText);
                        if (result.return_code == "success") {
                            //从方案列表数组里删除对应数据
                            var storeData = salaryGrid.getStore().getData();
                            for(var i = 0;i<storeData.items.length;i++){
                                for(var j = 0;j<idarr.length;j++){
                                    if(storeData.items[i].data.id == idarr[j]){
                                        delRecords.push(storeData.items[i]);//不能边循环边删除数组
                                    }
                                }
                            }
                            salaryGrid.getStore().remove(delRecords);
                            Ext.Msg.alert(gz.label.tips, gz.label.delPlanSuccess);

                        } else {
                            Ext.Msg.alert(gz.label.tips, result.return_msg);
                        }
                    }
                }, map);
            }
        });

    },
    /**
     * 显示方案
     */
    showViewById: function (id) {
        Ext.create('mysalary.MySalarySetting', {
            viewType: 'view',
            id: id
        });
    }
});