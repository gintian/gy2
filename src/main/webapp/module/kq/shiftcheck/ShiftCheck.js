/**
 * 排班审查
 */
Ext.define('ShiftCheckURL.ShiftCheck',{
	requires:['ShiftURL.ShiftGrid'],
	constructor:function(config) {
		shiftCheck = this;
		shiftCheck.year='';
		shiftCheck.month='';
		shiftCheck.weekIndex = '';
		shiftCheck.weekList = null;
		shiftCheck.selectArray = [];
		shiftCheck.addArray = [];
		shiftCheck.removeArray = [];
		shiftCheck.dateJson;
		shiftCheck.myChart = null;
		shiftCheck.pageRows = 20;
		this.init();
	},
	
	init: function () {
		var json = {};
		json.type = "all";
		json.year = shiftCheck.year;
		json.month = shiftCheck.month;
		json.weekIndex = shiftCheck.weekIndex;
		//页面第一次加载数据标识
		json.firstFlag = "1";
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ00021401',success:shiftCheck.showPanel},map);
	},
	
	showPanel: function (response) {
		var map = Ext.decode(response.responseText);
		if(map.succeed){
			shiftCheck.type = 'shiftlist';
			shiftCheck.privs = map.privs;
			shiftCheck.orglist = map.orgOndutylist[0];
			shiftCheck.datelist = map.orgOndutylist[1];
			shiftCheck.serieslist = map.orgOndutylist[2];
			
//			shiftCheck.shiftCheckSql = map.shiftCheckSql;
			shiftCheck.weekList = map.weekList;
			
			var columns = Ext.decode(getDecodeStr(map.column));
			var columnJson = Ext.decode(getDecodeStr(map.columnJson));
			shiftCheck.year = map.year;
			shiftCheck.month = map.month;
			shiftCheck.weekIndex = map.weekIndex;
			shiftCheck.dateJson = Ext.decode(map.dateJson);
			shiftCheck.pageRows = map.pageRows;
			var comboBoxStore = Ext.create('Ext.data.Store', {
				id: 'schemeListStore',
	            fields: ['id', 'name'],
	            autoLoad : true,
	            data: map.weekList
	        });
			
			var comboBox = Ext.create('Ext.form.field.ComboBox', {
				id:'comboBoxId',
	            fieldLabel: '',
	            labelSeparator: '',
	            store: comboBoxStore,
	            width: 210,
	            forceSelection: true,
	            valueField: 'id',
	            displayField: 'name',
	            shadow: false,
	            editable: false,
	            allowBlank: false,
	            value:shiftCheck.weekIndex,
	            cls: 'comboxStyle',
	            listeners: {
	                select:function (combo, record) {
	                	shiftCheck.weekIndex = record.data.id;
	                    shiftCheck.reloadTable(shiftCheck.type);
	                }
	            }
	        });
			
			var tbar1 = new Ext.Toolbar({
				id:'toolbar1',
				height:25,
				padding:'0 0 0 5',
				border:false,
				items:[{
					xtype: "label",
					text: kq.shift.queryProgram+"："
				},{
					xtype:"label",
					width:75,
					html:"<a href='javascript:shiftCheck.selectMonth()' id='date' >" 
						+ shiftCheck.year + kq.dataAppeal.year + shiftCheck.month + kq.dataAppeal.month+"</a>"
						+ "<img src='/workplan/image/jiantou.png' style='cursor:pointer' onclick='shiftCheck.selectMonth();'/> "
				},"-", comboBox]
			}); 
			// 选择列表 图形页签
			var buttons = Ext.create('Ext.panel.Panel', {
				width: 180,
				height: 40,
				border: false,
				layout: {
			        align: 'center',
			        pack: 'center',
			        type: 'hbox'
				},
				items: [{
							xtype: 'label',
							id: 'sellistid',
							border: false,
							width: 90,
							html  : '<div class="button-group-inner button-group-inner-select" onclick="shiftCheck.selectiveFunc(\'sellistid\')">'
						   		+'<font class="button-group-font button-group-font-select">'+kq.shift.list+'</font></div>'
				     },{
				    	 	xtype: 'label',
				    	 	id: 'selchartid',
				    	 	border: false,
				    	 	width: 90,
				    	 	html  : '<div class="button-group-inner" onclick="shiftCheck.selectiveFunc(\'selchartid\')">'
						   		+'<font class="button-group-font">'+kq.shift.chart+'</font></div>' 
				     }]
			});
			var tbarflag = true;
			if("1"==shiftCheck.privs.workAnalysisTablepriv || "1"==shiftCheck.privs.shiftTablepriv)
				tbarflag = false;
			var tbar2 = new Ext.Toolbar({
				id: 'toolbar2',
				height: 25,
				padding: '0 0 0 5',
				border: false,
				hidden: tbarflag,
				items: [{
					xtype:"button",
					text: kq.label.exportDesc,
					arrowAlign: 'right',
					menu:[{
						text: kq.shift.workAnalysisTable,//'工作分析表',
						hidden: ("0"==shiftCheck.privs.workAnalysisTablepriv),
						handler: function() {
							shiftCheck.exportTemplate("workAnalysisTable")
						}
					},{
						text: kq.shift.table,//'排班表',
						hidden: ("0"==shiftCheck.privs.shiftTablepriv),
						handler: function() {
							shiftCheck.exportTemplate("shiftTable")
						}
					}]
				}]
			}); 
			shiftCheck.createSearchPanel('');
			
			var north = new Ext.Toolbar({
				title: kq.shift.check,//'排班审查',
				id : 'north',
				items:[{
						xtype : 'panel',
						layout: 'vbox',
						border : false,
						items:[tbar1, tbar2]
					},
					,'->'
					,buttons
					],
				region : 'north',
				border : false			
			});

			var gird = Ext.create('ShiftURL.ShiftGrid', {
				dataType: "shiftCheck",
				columns: columnJson,
				fields: columns,
				year:shiftCheck.year,
				month:shiftCheck.month,
				pageSize:shiftCheck.pageRows,
				weekIndex: shiftCheck.weekIndex
//				dataSql:shiftCheck.shiftCheckSql
			 });
			
			var center = new Ext.Panel({
				xtype : 'panel',
				id : 'center',
				region : 'center',
				layout : 'fit',
				width: '100%',
				height: '100%',
				style:'background-color: #FFFFFF;',
				border : false,
				items:[
					gird
					,shiftCheck.createBarChart(Ext.getBody().getHeight()-150)
					]
			});
			var mainPanel = new Ext.Panel({
				id:"port",
				title: '<ul><li style="margin-top: -5px;float: left;">' + kq.shift.check
						+ "</li><li style='margin-top: -5px;float: right;'><img onclick='shiftCheck.schemeSetting()' id='schemeSetId'" +
						"src='/components/tableFactory/tableGrid-theme/images/Settings.png' style='cursor:pointer' title='" + kq.label.schemeSetDesc + "'/>"
						+ "</li></ul>",
				border : false,
				width: '100%',
				height: '100%',
				layout : "border",
				items:[north,center]
			});
			new Ext.Viewport({
				layout : "fit",
				items:[mainPanel]
			});
			/**
			 * 监听窗口大小
			 */
			Ext.on('resize', function (width, height){
//			Ext.EventManager.onWindowResize(function(){  
				var chartDiv = document.getElementById('chartid');
				var wvalue = width;//Ext.getBody().getWidth();
				chartDiv.style.width = wvalue;
				chartDiv.firstChild.style.width = wvalue;
				var hvalue = height-150;//Ext.getBody().getHeight()-150;
				chartDiv.style.height = hvalue; 
				chartDiv.firstChild.style.height = hvalue; 
				if(null != shiftCheck.myChart && !Ext.isEmpty(shiftCheck.myChart))
					shiftCheck.myChart.resize();
			});
		} else {
			Ext.showAlert(map.message);
		}
	},
	/**
	 * 选择 列表 图形事件
	 */
	selectiveFunc:function(id){
		// 已选列表标识
		var selectbool = ("sellistid" == id);  
		// 列表
		var listhtml = '<div class="button-group-inner'+ (selectbool ? ' button-group-inner-select' : '') 
					+'" onclick="shiftCheck.selectiveFunc(\'sellistid\')">'
					+'<font class="button-group-font '+ (selectbool ? ' button-group-font-select' : '') +'">'+kq.shift.list+'</font></div>';
		// 图形
		var charthtml = '<div class="button-group-inner'+ (selectbool ? '' : ' button-group-inner-select') 
					+'" onclick="shiftCheck.selectiveFunc(\'selchartid\')">'
					+'<font class="button-group-font'+ (selectbool ? '' : ' button-group-font-select') +'">'+kq.shift.chart+'</font></div>';
	   	// 重新为已选待选panel赋值
		Ext.getCmp("sellistid").setHtml(listhtml);
		Ext.getCmp("selchartid").setHtml(charthtml);
		// 选择列表
		if(selectbool){
  		  	shiftCheck.type = 'shiftlist';
  		  	Ext.getCmp("queryBox").setHidden(false);
  		  	Ext.getCmp("chartid").setHidden(true);
  		  	shiftCheck.reloadTable(shiftCheck.type);
  		  	document.getElementById("schemeSetId").style.display = "block";
		}// 选择图形
		else{
  		  	shiftCheck.type = 'shiftchart';
  		  	Ext.getCmp("shiftManage_0001_shiftGrid").destroy();
  		  	Ext.getCmp("queryBox").removeAllKeys();
  		  	Ext.getCmp("queryBox").setHidden(true);
  		  	Ext.getCmp("chartid").setHidden(false);
  		  	shiftCheck.reloadTable(shiftCheck.type);
  		    document.getElementById("schemeSetId").style.display = "none";
  	  	}
	},
	/**
	 * 选择月
	 */
    selectMonth: function () {
    	var win = Ext.getCmp('win');
		if(win) {
			win.show();
			return;
		}
		
		var SetItemGloble = Ext.create("EHR.attendanceMonth.AttendanceMonthComp",{
			totalData:shiftCheck.dateJson,
			border: false,
			currentYear: shiftCheck.year,
			currentMonth: Number(shiftCheck.month),
			onMonthSelected: function (value) {
				document.getElementById("date").innerText = value.year + kq.shift.year + value.desc;
				shiftCheck.year = value.year;
				shiftCheck.month = value.monthOrder + "";
				shiftCheck.weekIndex = '1';
				shiftCheck.reloadTable(shiftCheck.type);
				win.hide();
			}
		});
		
		win = Ext.create('Ext.window.Window', {
			id: 'win',
			header: false,
			x: Ext.get("date").getX() - 50,
			y: Ext.get("date").getY() + 15,
			width: 258,
			height: 170,
			items: [SetItemGloble],
			listeners: {
				"render": function () {
		            document.getElementById("win").onmouseout = function () {
		                var s = event.toElement || event.relatedTarget;
		                if (s == undefined || !this.contains(s))
		                    win.hide();
		            };
		        }
			}
		});
		win.show();
		// 点击window外
        Ext.getBody().addListener('click', function(evt, el) {
        	// 如果是点击下拉按钮则隐藏
        	if (!win || "comboBoxId-trigger-picker"==el.id)
        		win.hide();
        });
    },
    
    // 查询控件
	createSearchPanel: function(fieldsArray){
		var map = new HashMap();
		shiftCheck.SearchBox = Ext.create("EHR.querybox.QueryBox",{
			id:'queryBox',
			hideQueryScheme:false,
			emptyText: kq.shift.inputNameGno,//'请输入姓名、工号...',
			subModuleId:'shiftManage_0001',
			customParams:map,
			funcId:"KQ00021303",
			hideQueryScheme: true,
			fieldsArray:fieldsArray,
			success:shiftCheck.reloadTable
		});
		
		Ext.getCmp('toolbar2').add(shiftCheck.SearchBox);
	},
	
	reloadTable: function (type) {
		var json = {};
		json.type = shiftCheck.type;
		json.year = shiftCheck.year;
		json.month = shiftCheck.month;
		json.weekIndex = shiftCheck.weekIndex;
		json.weekList = shiftCheck.weekList;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ00021401',success:shiftCheck.reloadData},map);
	},
	
	reloadData: function (response) {
		var map	 = Ext.decode(response.responseText);
		if(map.succeed){
			shiftCheck.type = map.type;
			if("shiftchart" == map.type){
				
				shiftCheck.orglist = map.orgOndutylist[0];
				shiftCheck.datelist = map.orgOndutylist[1];
				shiftCheck.serieslist = map.orgOndutylist[2];
				shiftCheck.weekList = map.orgOndutylist[3];
				
				var comboBoxStore = Ext.create('Ext.data.Store', {
		            fields: ['id', 'name'],
		            autoLoad : true,
		            data: shiftCheck.weekList
		        });
				Ext.getCmp("comboBoxId").setStore(comboBoxStore);
				Ext.getCmp('comboBoxId').setValue(shiftCheck.weekIndex);
				// 隐藏导出
      		  	Ext.getCmp("toolbar2").setHidden(true);
				// 加载图形
				shiftCheck.drawChildChart();
			}else if("shiftlist" == map.type){
				
				if(Ext.getCmp("shiftManage_0001_shiftGrid"))
					Ext.getCmp("shiftManage_0001_shiftGrid").destroy();
				Ext.getCmp("queryBox").setHidden(false);
      		  	Ext.getCmp("chartid").setHidden(true);
				// 显示导出
      		  	Ext.getCmp("toolbar2").setHidden(false);
				shiftCheck.weekList = map.weekList;
				shiftCheck.weekIndex = map.weekIndex;
				
				var comboBoxStore = Ext.create('Ext.data.Store', {
		            fields: ['id', 'name'],
		            autoLoad : true,
		            data: map.weekList
		        });
				Ext.getCmp("comboBoxId").setStore(comboBoxStore);
				Ext.getCmp('comboBoxId').setValue(map.weekIndex);
				
//				shiftCheck.shiftCheckSql = map.shiftCheckSql;
				var columns = Ext.decode(getDecodeStr(map.column));
				var columnJson = Ext.decode(getDecodeStr(map.columnJson));
				shiftCheck.pageRows = map.pageRows;
				var gird = Ext.create('ShiftURL.ShiftGrid', {
					dataType: "shiftCheck",
					columns: columnJson,
					fields: columns,
					year:shiftCheck.year,
					month:shiftCheck.month,
					pageSize:shiftCheck.pageRows,
					weekIndex: shiftCheck.weekIndex
//					dataSql:shiftCheck.shiftCheckSql
				});
				Ext.getCmp('center').add(gird);
			}
		}
	},
	/**
	 * 创建统计图对象
	 */
	createBarChart:function(heightNum){
		
		var barChart = Ext.create("Ext.container.Container",{
			id: 'chartid',
			border:1,
			width:'100%',
    		height : heightNum,//500,
			layout:'fit' 
		});
		
		var charset = Ext.Loader.config.scriptCharset;
		Ext.Loader.config.scriptCharset="UTF-8";
		Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:this.drawChildChart,scope:this});
		Ext.Loader.config.scriptCharset=charset;
		
		return barChart;
	},
	/**
	 * 绘图 统计分析图
	 */
	drawChildChart:function(){
		shiftCheck.myChart = null;
		// 获取option对象
		var option = shiftCheck.getOption();
		var myChart1 = echarts.init(document.getElementById('chartid'));//,'shine'
		// 先销毁再创建
		myChart1.dispose();
		myChart1 = echarts.init(document.getElementById('chartid')); 
		myChart1.setOption(option);
		shiftCheck.myChart = myChart1;
	},
	/**
	 * 统计图主体
	 */
	getOption:function(){
		// 参照员工管理统计的颜色样式
		var colorlist = ['#338DC9','#EE7541','#2BD62B','#DBDC26','#8FbC8B','#D2B48C','#DC648A','#21B2AA','#B0C4DE','#DDA0DD','#9C9AFF'
			,'#9C3164','#FFB248','#1fcf03','#005eaa','#339ca8','#d9b014','#32a487','#333333','#FFB6C1','#FF69B4','#D8BFD8','#DDA0DD','#FF00FF'];
		
		var orgdescs = [];
		var seriesData = [];
		for(i=0;i<shiftCheck.orglist.length;i++){
			var orgStr = shiftCheck.orglist[i];
			var orgid = orgStr.split("`")[0];
			var orgdesc = orgStr.split("`")[1];
			orgdescs.push(orgdesc);
			var mapv = shiftCheck.serieslist[orgid];
			var obj = {
		        name: orgdesc,
		        type: 'line',
		        data: mapv
	    	};
			seriesData.push(obj);
		}
		
		var	option = {
			    tooltip: {
			        trigger: 'axis',
			        confine: true
			    },
			    // 线标识
			    legend: {
			    	type:'scroll',
			    	right: '5%',
			    	left: '5%',
			        data:orgdescs
			    },
			    color: colorlist,
//			    backgroundColor:'#FF6FA7',
			    toolbox: {
			    	right: '2%',
			        feature: {
			            saveAsImage: {
			            	title: kq.label.down
			            }
			        }
			    },
			    xAxis: {
			        name: kq.shift.date,//'日期',
			        type: 'category',
			        data: shiftCheck.datelist
			    },
			    yAxis: {
			        name: kq.shift.ondutyNum,//'出勤人数',
			        type: 'value'
			    },
			    series: []
			};
		option.series = seriesData;
		
		return option;
	},
	/**
	 * 导出工作分析表workAnalysisTable/排班表shiftTable
	 */
	exportTemplate : function(type){
		var json = {};
		json.type = type;
		json.year = shiftCheck.year;
		json.month = shiftCheck.month;
		json.weekIndex = shiftCheck.weekIndex;
//		json.dataSql = shiftCheck.shiftCheckSql;
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ00021402',success:shiftCheck.exportSucc},map);
	},
	exportSucc: function (response){
		var value = response.responseText;
		var map	 = Ext.decode(value);
		if(map.succeed){
			var fieldName = getDecodeStr(map.fileName);
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
		}
	},
	
	schemeSetting: function(showPublicPlan) {
		Ext.require("EHR.tableFactory.plugins.SchemeSetting",function(){
			var window = new EHR.tableFactory.plugins.SchemeSetting({
					subModuleId:'shiftCheck_001',
					showPublicPlan:showPublicPlan,
					schemeItemKey:'A',
					itemKeyFunctionId:'',
					showPageSize:true,
					viewConfig: {
						publicPlan: true,
						order: false,
						merge: false,
						sum: false
					},
					closeAction:shiftCheck.closeSettingWindow
			});
		});
	},
	
	closeSettingWindow: function (){
		var map = new HashMap();
		var json = {};
		json.type = "changeSubmoudleId";
		var map = new HashMap();
		map.put("jsonStr", JSON.stringify(json));
	    Rpc({functionId:'KQ00021401',success:shiftCheck.searchrTableSucc},map);
	},
	
	searchrTableSucc: function (response) {
		var map = Ext.decode(response.responseText);
		if(map.succeed){
			shiftCheck.reloadTable();
		} else {
			Ext.showAlert(map.message);
		}
	}
});