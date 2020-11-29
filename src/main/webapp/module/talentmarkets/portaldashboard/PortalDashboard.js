/**
 * 人才市场主页面
 * wangbs 2019-7-29
 */
Ext.define('Talentmarkets.portaldashboard.PortalDashboard',{
    extend:'Ext.panel.Panel',
    scrollable :'y',
    bodyStyle: 'backgroundColor:#F7F7F7',//背景色
    initComponent:function(){
        PortalDashboard = this;
        this.callParent();
        //改变window大小随之改变downPanel尺寸
        window.onresize = function () {
            PortalDashboard.resizeComponent();
        };
        this.loadData();
    },
    /**
     * 加载数据
     */
    loadData:function(){
        var map = new HashMap;
        map.put('operateType','all');
        Rpc({functionId: 'TM000000001', success: PortalDashboard.generatePageLayout, scope: this}, map);
    },
    /**
     * 选择机构后刷新统计图数据
     * @param res 后台返回数据
     */
    reloadData: function (res) {
        var me = this;
        var result = Ext.decode(res.responseText);
        var psnOrPosPrivMap = result.psnOrPosPrivMap;
        me.psnPriv = psnOrPosPrivMap.psnPriv;
        me.posPriv = psnOrPosPrivMap.posPriv;
        me.createChartView(result.chartOption);
    },
    /**
     * 生成页面布局
     * @param res 后台返回数据
     */
    generatePageLayout:function (res) {
        var me = this;
        var result = Ext.decode(res.responseText);
        if (result.return_code == "success") {
            var returnData = result.return_data;
            me.privOrgIdStr = returnData.privOrgIdStr;
            var psnOrPosPrivMap = returnData.psnOrPosPrivMap;
            me.psnPriv = psnOrPosPrivMap.psnPriv;
            me.posPriv = psnOrPosPrivMap.posPriv;
            var upPanel = me.createUpPanel(returnData.staticPsnCountMap);
            var downPanel = me.createDownPanel();

            me.add(upPanel);
            me.add(downPanel);

            setTimeout(function () {
                var chartData = returnData.compePosChartOption;
                me.createChartView(chartData);
            },1);
        }else{
            Ext.Msg.alert(tm.tip, eval(result.return_msg));
        }
    },
    /**
     * 创建顶部panel
     * @param staticPsnCountMap 数目统计map
     * @returns {Ext.Panel}
     */
    createUpPanel:function (staticPsnCountMap) {
        var me = this;
        var upPanel = Ext.create("Ext.Panel", {
            border: false,
            margin: '10 10 0 20',
            bodyStyle: 'backgroundColor:#F7F7F7',
            listeners:{
                resize:function () {
                    //自适应屏幕宽度放缩上方四个panel
                    for (var i = 0; i < 4; i++) {
                        var childPanel = PortalDashboard.query("#upPanel_" + (i + 1))[0];
                        var childPanelWidth = (Ext.getBody().getViewSize().width - 80) / 4;
                        childPanel.setWidth(childPanelWidth);
                    }
                }
            }
        });
        for (var i = 0; i < 4; i++) {
            var suffix = i + 1;
            var childPanel = me.createChildPanel(suffix, staticPsnCountMap);
            upPanel.add(childPanel);
        }
        return upPanel;
    },
    /**
     * 创建顶部的单个子panel
     * @param suffix 后缀
     * @param staticPsnCountMap 数据统计map
     * @returns {Ext.Panel}
     */
    createChildPanel: function (suffix, staticPsnCountMap) {
        var me = this;
        var currentPanelDesc = tm.currentCompeCount;
        var rightPanelColor = "#FFC600";
        var targetCount = staticPsnCountMap.currentUserCount;
        if (suffix == 2) {
            currentPanelDesc = tm.currentPositionCount;
            targetCount = staticPsnCountMap.currentPositionCount;
            rightPanelColor = "#A8DC28";
        } else if (suffix == 3) {
            currentPanelDesc = tm.historyCompeCount;
            targetCount = staticPsnCountMap.totalUserCount;
            rightPanelColor = "#00BAFF";
        } else if (suffix == 4) {
            currentPanelDesc = tm.historyPositionCount;
            targetCount = staticPsnCountMap.totalPositionCount;
            rightPanelColor = "#FF8C8C";
        }
        var childPanel = Ext.create("Ext.Panel", {
            border: false,
            xtype: 'panel',
            layout: "hbox",
            style: 'cursor:pointer;float:left',
            itemId: 'upPanel_' + suffix,
            minWidth: 220,
            height: 90,
            margin: '0 10 5 0',
            items: [{
                xtype: 'image',
                height: '100%',
                width: 80,
                src: '../images/upIcon' + suffix + '.png'
            }, {
                xtype: 'panel',
                border: false,
                bodyStyle: 'backgroundColor:' + rightPanelColor,//背景色
                flex: 1,
                height: '100%',
                layout: {
                    type: 'vbox',
                    pack: 'center',
                    align: 'right'//水平居右
                },
                items: [{
                    xtype: 'component',
                    margin: '0 0 0 15',
                    html: '<span style="font-size: 25px;font-weight: bold;">' + targetCount + '</span>'
                }, {
                    xtype: 'component',
                    margin: '0 0 0 15',
                    html: '<span style="font-size: 15px;font-weight: bold;">' + currentPanelDesc + '</span>'
                }]
            }],
            listeners: {
                element: 'el',
                click: function () {
                    if (suffix == 1) {//当前竞聘人次
                        if(me.psnPriv) {
                            window.location.href = '/module/talentmarkets/competition/Competitors.html?from=portal&status=current&pos=&firstStatus=&title=' + $URL.encode("当前竞聘情况");
                        }
                    }else if (suffix == 3) {//历史竞聘人次
                        if(me.psnPriv) {
                            window.location.href = '/module/talentmarkets/competition/Competitors.html?from=portal&status=history&pos=&firstStatus=&title=' + $URL.encode("历史竞聘情况");
                        }
                    }else if (suffix == 2) {//当前竞聘岗位数
                        if (me.posPriv) {
                            window.location.href = '/module/talentmarkets/competition/CompetitionJobs.html?from=portal&status=current&title=' + $URL.encode("当前竞聘岗位");
                        }
                    }else if (suffix == 4) {//历史竞聘岗位数
                        if (me.posPriv) {
                            window.location.href = '/module/talentmarkets/competition/CompetitionJobs.html?from=portal&status=history&title=' + $URL.encode("历史竞聘岗位");
                        }
                    }
                }
            }

        });
        return childPanel;
    },
    /**
     * 创建downPanel
     * @returns {Ext.Panel}
     */
    createDownPanel: function () {
        var me = this;
        var spacingHeight = 150;
        var downPanelHeight = Ext.getBody().getViewSize().height - spacingHeight;
        var chartContainerHeight = Ext.getBody().getViewSize().height - (spacingHeight + 60);
        var downPanel = Ext.create("Ext.Panel", {
            itemId: 'downPanel',
            height: downPanelHeight,
            margin: '5 20 0 20',
            style:'border:1px #C5C5C5 solid;',
            items: [{
                xtype: 'panel',
                border: false,
                items:[{
                    xtype: 'codecomboxfield',
                    itemId: 'orgCombo',
                    margin: '5 0 5 5',
                    width: 300,
                    codesetid: 'UM',
                    nmodule: "4",//组织机构业务范围
                    ctrltype: "3",
                    onlySelectCodeset: false,
                    emptyText: tm.selectOrgplease,//请选择组组织机构
                    afterCodeSelectFn: function (a, orgId) {
                        PortalDashboard.selectOrgAfterfunc(orgId);
                        me.selectOrgId = orgId;
                    },
                    listeners:{
                        render: function () {
                            // tbar改变颜色
                            // downPanel.query("toolbar")[0].setStyle({
                            //     backgroundColor: "#F0F0F0"
                            // });
                        },
                        keyup:{
                            element:'el',
                            fn: function () {
                                PortalDashboard.reLoadOrgData();
                            }
                        },
                        //ie兼容模式下有叉号，点击叉号的处理事件
                        mousedown:{
                            element: 'el',
                            fn: function () {
                                setTimeout(function () {
                                    PortalDashboard.reLoadOrgData();
                                },200);
                            }
                        }
                    }
                }]
            },{
                xtype: 'component',//展现柱状图的容器
                id: 'chartContainer',
                margin: '10 0 0 0',
                height: chartContainerHeight,
                listeners:{
                    resize:function(){
                        if(me.myChart){
                            //window大小改变时，统计图自适应当前页面大小
                            me.myChart.resize();
                        }
                    }
                }
            }]
        });
        return downPanel;
    },
    /**
     * 清空值时触发所有数据查询
     */
    reLoadOrgData: function () {
        var orgCombo = PortalDashboard.query("#orgCombo")[0];
        //清空值时触发所有数据查询
        if (PortalDashboard.selectOrgId && !orgCombo.getValue()) {
            PortalDashboard.selectOrgId = orgCombo.getValue();
            var map = new HashMap();
            map.put('orgIds', PortalDashboard.privOrgIdStr);
            map.put('operateType', 'orgData');
            Rpc({functionId: 'TM000000001', async:false, success: PortalDashboard.reloadData, scope: PortalDashboard}, map);
        }
        orgCombo.focus();
    },
    /**
     * 选择机构后的回调
     * @param orgIds 选择的机构id
     */
    selectOrgAfterfunc:function(orgIds){
        var map = new HashMap();
        map.put('orgIds',orgIds);
        map.put('operateType','orgData');
        Rpc({functionId : 'TM000000001',success : PortalDashboard.reloadData, scope: this}, map);
    },

    /**
     * 修改柱子颜色,添加折线图阴影
     * @param oneData 图例数据
     * @param color 线和数据的颜色
     * @returns {*}
     */
    modifyColor: function (oneData, color) {
        oneData.symbolSize = 8;//折点大小
        oneData.itemStyle = {
            normal: {
                lineStyle: {
                    color: color
                },
                color: color,
                label: {
                    show: true,
                    position: 'top'
                }
            }
        };
    },
    /**
     * 创建chart视图
     * @param chartData 视图需要的数据
     */
    createChartView:function(chartData){
        var me = this;
        //无数据时展示图片nodata.png
        if (chartData.xAxisData.length == 0) {
            Ext.getCmp("chartContainer").hide();

            me.query("#downPanel")[0].add({
                xtype: 'panel',
                width: '100%',
                itemId: 'noDataPanel',
                border: false,
                height: me.query("#downPanel")[0].height - 50,
                layout: {
                    type: 'vbox',
                    align: 'center',
                    pack: 'center'
                },
                items: [{
                    xtype: 'image',
                    height: 260,
                    width: 260,
                    src: '/images/nodata.png'
                },{
                    xtype: 'component',
                    html: "<span style='font-size: 20px'>" + tm.noDataTip + "</span>"
                }]
            });
            return;
        }

        //有数据隐藏无数据效果图，显示chart视图
        if (me.query("#noDataPanel")[0]) {
            me.query("#noDataPanel")[0].hide();
            Ext.getCmp("chartContainer").show();
        }
        //加密的岗位编号list用于点击柱子做数据穿透
        me.z8101List = chartData.seriesData[1].z8101List;
        //修改柱子颜色
        for (var i = 0; i < chartData.seriesData.length; i++) {
            var color = "#338DC9";
            if (i == 1) {
                color = "#EE7541";
            }
            var oneData = chartData.seriesData[i];
            me.modifyColor(oneData, color);
        }

        var legend = chartData.legend;
        if (me.myChart) {
            legend = me.myChart.getOption().legend;
        }

        //计算统计图左边距，处理文字换行，预防文字超出容器
        var gridLeft = 0;
        for (var i = 0; i < chartData.xAxisData.length; i++) {
            var psoName = chartData.xAxisData[i];
            if (i == 0 && psoName.length < 15) {
                gridLeft = psoName.length * 11 / 2;
            }

            if (psoName.length >= 15) {
                gridLeft = 80;
                chartData.xAxisData[i] = psoName.substring(0, 15) + "\n" + psoName.substring(15);
            }

            //解决IE浏览器文字底部被遮住
            chartData.xAxisData[i] = chartData.xAxisData[i] + "\n\n ";

            //加换行之后ie浏览器中，文字与边线贴得太近处理一下
            var version = getBrowseVersion();
            if (version < 10 && version != 0) {
                chartData.xAxisData[i] = {
                    value: chartData.xAxisData[i],
                    textStyle: {
                        padding: [10, 0, 0, 0]
                    }
                };
            }
        }

        var option = {
            tooltip: {
                trigger: 'axis',
                axisPointer : {
                    type : 'none'
                }
            },
            grid: {
                top: 30,
                left: gridLeft < 20 ? 20 : gridLeft,
                right: 110,
                containLabel: true
            },
            legend: legend,
            xAxis: [{
                type: 'category',//类目轴，适用于离散的类目数据，为该类型时必须通过 data 设置类目数据。
                boundaryGap: false,
                data: chartData.xAxisData,
                splitLine:{
                    show:true,
                    lineStyle:{
                    }
                },
                axisLine: {
                    show: false//隐藏坐标轴
                },
                axisTick: {
                    show: false//隐藏刻度线
                }
            }],
            yAxis: [{
                type: 'value',
                minInterval: 1,
                axisLine: {
                    show: false//隐藏坐标轴
                },
                axisTick: {
                    show: false//隐藏刻度线
                }
            }],
            series: chartData.seriesData
        };
        if (chartData.xAxisData.length > 6) {
            option.dataZoom = [{
                type: "slider",
                realtime: true,//实时刷新试图
                textStyle: false,
                zoomLock: true,//禁止缩放
                start: 0,
                end: parseInt(6 * 100 / chartData.xAxisData.length)//默认先展示8条占的% 剩余的拖动显示
            }];
        }
        //切换机构重新渲染echarts时 要清空原来的东西
        if(me.myChart) {
            me.myChart.clear();
            me.myChart.off('click');
        }
        me.myChart = echarts.init(document.getElementById('chartContainer'), 'shine');
        me.myChart.setOption(option);

        if (me.psnPriv) {
            me.myChart.on('click', function (param) {
                //应聘人数线做人员列表穿透
                if (param.seriesIndex == 1) {
                    var z8101 = me.z8101List[param.dataIndex];
                    window.location.href = '/module/talentmarkets/competition/Competitors.html?from=portal&status=current&pos=' + z8101;
                }
            });
        }
    },
    /**
     * 重置页面尺寸
     */
    resizeComponent: function () {
        var downPanel = PortalDashboard.query('#downPanel')[0];
            if(downPanel){
                downPanel.hide();
                setTimeout(function () {
                    var noDataPanel = PortalDashboard.query('#noDataPanel')[0];
                    var chartContainer = Ext.getCmp('chartContainer');
                    var hei = Ext.getBody().getViewSize().height;

                    var tempNum = 150;
                    downPanel.setHeight(hei - tempNum);//沾满屏幕剩余的高度
                    if (chartContainer) {
                        chartContainer.setHeight(hei - (tempNum + 60));
                    }
                    if (noDataPanel) {
                        noDataPanel.setHeight(hei - tempNum - 50);
                    }
                    downPanel.show();
                }, 150);
            }
    }
});