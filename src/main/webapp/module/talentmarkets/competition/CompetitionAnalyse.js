Ext.define("Talentmarkets.competition.CompetitionAnalyse", {
    extend : 'Ext.panel.Panel',
	requires : ['EHR.extWidget.proxy.TransactionProxy','EHR.orgTreePicker.OrgTreePicker'],
	layout : 'vbox',
	bodyStyle:'z-index:2',
	title: tm.contendPosAnalyse.title,//'竞聘分析'
	// 初始化函数
	initComponent:function(){
		var me = this;
		CompetitionAnalyse = this;
		this.callParent();
		window.onresize = function () {
			CompetitionAnalyse.resizeComponent();
		};
		// 加载初始化数据
		this.initData();
		// 获取初始柱状图
		setTimeout(function(){
			// 获取初始化柱状图数据数据
			var initChartsData = CompetitionAnalyse.returnInitData;
			var chartData = {
				seriesData: [{
					data: initChartsData.return_data.seriesNeedData,
					name: tm.needPsnCount
				}, {
					data: initChartsData.return_data.seriesDCData,
					name: tm.declarePsnCount
				}, {
					data: initChartsData.return_data.seriesEmployData,
					name: tm.employPsnCount
				}],
				xAxisData: initChartsData.return_data.xAxisData
			};
			me.createchartsPanel(chartData);
		},3);
	},
	// 初始化数据方法
	initData : function(){
		var me = this;
		var initDataMap = new HashMap;
		initDataMap.put('operaType','init');
		Rpc({functionId : 'TM000000010', async: false, success : me.recordInitData,scope : me}, initDataMap)
	},
	// 返回的初始化数据
	recordInitData : function(result){
		var me = this;
    	CompetitionAnalyse.returnInitData = Ext.decode(result.responseText);
    	if (!CompetitionAnalyse.returnInitData.succeed) {
			Ext.Msg.alert(tm.tip, tm.errorMsg);// 未能成功获取数据
    	}
		//加载下部的Panel
		me.createDownPanel();
	},
	// 点击某某单位
    selectOrgClick:function(id){
    	var idList=[id];
    	setEleConnect(idList);
    },
	/**
	 * 重新请求数据
	 */
	selectOrgAfterfunc: function () {
		var me = this;
		if (Ext.getCmp('startDate').activeErrors || Ext.getCmp('endDate').activeErrors) {
			Ext.Msg.alert(tm.tip, tm.contendPosAnalyse.selectFillDate);//'请输入正确的归属日期！';
			return false;
		}
		var startDate = Ext.getCmp('startDate').getValue();
		var endDate = Ext.getCmp('endDate').getValue();

		if (startDate && endDate && startDate > endDate) {
			Ext.Msg.alert(tm.tip, tm.dateError);//结束日期不能早于开始日期
			return false;
		}

		if (startDate && !endDate) {
			//取当前日期作为查询数据范围的截止日期，对竞聘岗位的创建时间进行过滤
			endDate = new Date();
		}else if(!startDate && endDate){
			startDate = "1900-01-01";
		}else if (!startDate && !endDate) {
			startDate = "";
			endDate = "";
		}

		if (startDate && typeof startDate == 'object') {
			startDate = Ext.util.Format.date(startDate, 'Y-m-d');
		}
		if (endDate && typeof endDate == 'object') {
			endDate = Ext.util.Format.date(endDate, 'Y-m-d');
		}

		var orgId = me.query("#selectOrgCombo")[0].getValue();
		if (orgId && orgId.indexOf("`") > -1) {
			orgId = orgId.split("`")[0];
		}
		var map = new HashMap;
		map.put('startDate', startDate);
		map.put('endDate', endDate);
		map.put('orgId', orgId);
		map.put('operaType', 'orgIdData');
		Rpc({functionId: 'TM000000010', success: CompetitionAnalyse.recordData}, map);
		return true;
	},
    // 返回的数据
    recordData : function(result){
    	var returnStr = Ext.decode(result.responseText);
    	if (!returnStr.succeed) {
			Ext.Msg.alert(tm.tip, tm.errorMsg);// 未能成功获取数据数据
    	}else{
			if (returnStr.return_data.xAxisData.length > 0) {
				Ext.getCmp("mainEchart").show();
				if(CompetitionAnalyse.query("#noDataPanel")[0]) {
					CompetitionAnalyse.query("#noDataPanel")[0].hide();
				}

				// 获取初始化柱状图数据数据
				var chartData = {
					seriesData: [{
						data: returnStr.return_data.seriesNeedData,
						name: tm.needPsnCount
					}, {
						data: returnStr.return_data.seriesDCData,
						name: tm.declarePsnCount
					}, {
						data: returnStr.return_data.seriesEmployData,
						name: tm.employPsnCount
					}],
					xAxisData: returnStr.return_data.xAxisData
				};
				CompetitionAnalyse.createchartsPanel(chartData);
			}else {
				CompetitionAnalyse.addNoDataPanel(returnStr.return_data);
			}
    	}
    },
	//清空值时触发查询所有机构的数据
	reLoadOrgData: function () {
		var orgCombo =CompetitionAnalyse.query("#selectOrgCombo")[0];

		var dateFlag = true;
		if (CompetitionAnalyse.selectOrgId && !orgCombo.getValue()) {
			dateFlag = CompetitionAnalyse.selectOrgAfterfunc();
		}
		if (dateFlag) {
			CompetitionAnalyse.selectOrgId = orgCombo.getValue();
		}else{
			orgCombo.setValue(CompetitionAnalyse.selectOrgId);
			orgCombo.focus();
		}
	},
	// 创建下部的Panel
	createDownPanel:function(){
		var me = this;
		me.selectOrgId = "";
		var codecomboxfield = Ext.widget("codecomboxfield", {
			width: 300,
			itemId: "selectOrgCombo",
			margin: '0 20 0 0',
			codesetid: 'UM',
			nmodule: "4",//组织机构业务范围
			ctrltype: "3",
			onlySelectCodeset: false,
			emptyText: tm.selectOrgplease,//请选择组组织机构
			afterCodeSelectFn: function () {
				var dateFlag = CompetitionAnalyse.selectOrgAfterfunc();
				if (dateFlag) {
					me.selectOrgId = codecomboxfield.getValue();
				} else {
					codecomboxfield.setValue(me.selectOrgId);
				}
			},
			listeners: {
				keyup:{
					element:'el',
					fn: function () {
						CompetitionAnalyse.reLoadOrgData();
					}
				},
				//ie兼容模式下有叉号，点击叉号的处理事件
				mousedown:{
					element: 'el',
					fn: function () {
						setTimeout(function () {
							CompetitionAnalyse.reLoadOrgData();
						},200);
					}
				}
			}
		});
		// 选择时间的panel
		var datePanel = Ext.create('Ext.Panel', {
			layout : {
				type: 'hbox',
				align: 'center'
			},
			margin: '0 0 0 40',
			height : 60,
			border : false,
			items : [codecomboxfield,{
				xtype : 'datefield',
				format : 'Y-m-d',
				labelAlign : 'right',
				labelWidth: 14,
				id : 'startDate',
				value : '',
				fieldLabel : tm.from // 从
			},{
				xtype : 'datefield',
				format : 'Y-m-d',
				labelAlign : 'right',
				labelWidth: 14,
				value : '',
 				id : 'endDate',
				fieldLabel : tm.to // 至
			},{
				xtype : 'button',
				text : tm.contendPos.select,//'查询',
				margin : '0 0 0 20',
				handler : function(){
					CompetitionAnalyse.selectOrgAfterfunc();
				}
			}]
		});
		// 获取当前页面高度
		var hei = Ext.getBody().getViewSize().height;
		//下部的 Panel
		var downInfoPanel = Ext.create('Ext.panel.Panel',{
			height: hei - 50,
			width: '100%',
			itemId : 'downInfoPanel',
			border:false,
			items: [datePanel, {
				xtype: 'component',
				id: 'mainEchart',
				height: hei - 180,
				margin: '0 10 0 0',
				listeners: {
					resize: function () {
						if (me.chart) {
							me.chart.resize();
						}
					}
				}
			}]
		});
		me.add(downInfoPanel);
	},
	//添加无数据panel
	addNoDataPanel: function (chartData) {
		var me = this;
		//无数据时展示图片nodata.png
		if (chartData.xAxisData.length == 0) {
			Ext.getCmp("mainEchart").hide();
			if(me.query("#noDataPanel")[0]) {
				me.query("#noDataPanel")[0].show();
			}else {
				me.query("#downInfoPanel")[0].add({
					xtype: 'panel',
					width: '100%',
					itemId: 'noDataPanel',
					border: false,
					height: me.query("#downInfoPanel")[0].height,
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
			}
		}
	},
	/**
	 * 改变线条颜色并获得图例数据option
	 * @param oneData 图例数据
	 * @param color 线的色值
	 * @returns {*}
	 */
	getOneSeriesData: function (oneData, color) {
		oneData.type = 'line';
		oneData.smooth = true;
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
		return oneData;
	},
	// 柱状图的Panel
	createchartsPanel: function (chartData) {
		var me = this;
		me.addNoDataPanel(chartData);

		//所有柱子数据数组
		var seriesDataArr = [];
		for (var i = 0; i < chartData.seriesData.length; i++) {
			var color = "#338DC9";
			if(i == 1) {
				color = "#EE7541";
			}else if (i == 2) {
				color = "#2BD62B";
			}

			var oneData = me.getOneSeriesData(chartData.seriesData[i], color);
			seriesDataArr.push(oneData);
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
			xAxis: {
				type: 'category',
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
					show: false//隐藏刻度
				}
			},
			yAxis: {
				type: 'value',
				minInterval: 1,
				axisLine: {
					show: false//隐藏坐标轴
				},
				axisTick: {
					show: false//隐藏刻度
				}
			},
			tooltip: {
				trigger: 'axis',
				axisPointer: {
					type: 'none'
				}
			},
			//标识信息，点击对应选项可隐藏显示此列数据
			legend: {
				data: [tm.needPsnCount, tm.declarePsnCount, tm.employPsnCount], // 需求人数，申报人数，录用人数
				x: "right",
				y: "center",
				orient: "vertical"
			},
			grid: {
				top: 30,
				bottom: 50,
				left: gridLeft < 20 ? 20 : gridLeft,
				right: 110,
				containLabel: true
			},
			//图表下部的滚动条
			dataZoom: [{
				start: 0,
				realtime: true,
				xAxisIndex: [0],
				zoomLock: true,//禁止缩放
				textStyle: false,//滚动条不显示文字
				show: chartData.xAxisData.length > 6 ? true : false,
				bottom: 0,
				end: chartData.xAxisData.length == 0 ? 0 : 6 * 100 / chartData.xAxisData.length
			}],
			series: seriesDataArr
		};

		//切换机构重新渲染echarts时 要清空原来的东西
		if(me.myChart) {
			me.myChart.clear();
		}
		me.myChart = echarts.init(document.getElementById("mainEchart"),'shine');
		me.myChart.setOption(option);
	},
	/**
	 * 重置页面尺寸
	 */
	resizeComponent: function () {
		var downInfoPanel = CompetitionAnalyse.query('#downInfoPanel')[0];
		if(downInfoPanel){
			var noDataPanel = CompetitionAnalyse.query('#noDataPanel')[0];
			var mainEchartContainer = Ext.getCmp('mainEchart');
			var hei = Ext.getBody().getViewSize().height;
			downInfoPanel.setHeight(hei - 50);//沾满屏幕剩余的高度
			if (mainEchartContainer) {
				mainEchartContainer.setHeight(hei - 180);
			}
			if (noDataPanel) {
				noDataPanel.setHeight(hei - 50);
			}
		}
	},
	/**
	 * 重置页面尺寸
	 */
	resizeComponent: function () {
		var downInfoPanel = CompetitionAnalyse.query('#downInfoPanel')[0];
		if(downInfoPanel){
			var noDataPanel = CompetitionAnalyse.query('#noDataPanel')[0];
			var mainEchartContainer = Ext.getCmp('mainEchart');
			var hei = Ext.getBody().getViewSize().height;
			downInfoPanel.setHeight(hei - 50);//沾满屏幕剩余的高度
			if (mainEchartContainer) {
				mainEchartContainer.setHeight(hei - 180);
			}
			if (noDataPanel) {
				noDataPanel.setHeight(hei - 50);
			}
		}
	}
});

