/**
 * 证书管理 管理用户登录门户
 */
/**
 * 审批待办借阅证书窗口显示必填项
 */
Ext.override(Ext.form.field.Base,{
    initComponent:function(){
        if(this.required!==undefined && this.required){
			if(this.fieldLabel){
				this.fieldLabel = '<font color=red>*</font>' + this.fieldLabel;
			}
        }
        this.callParent(arguments);
    }
});

Ext.define('DashboardURL.ManagerDashboard',{
	
	constructor:function(config) {
		managerDashboard = this;
		// manager:管理员；employee:员工
		managerDashboard.roleType = config.roleType;
		managerDashboard.myChart = null;
		managerDashboard.flag = "1";
		managerDashboard.disabledStyle = "opacity:0.7;filter: progid:DXImageTransform.Microsoft.Alpha(Opacity=70);";
		this.init();
	},
	// 初始化函数
	init: function() {
		var map = new HashMap();
		// manager:管理员；employee:员工
		map.put("opt", managerDashboard.roleType);
		map.put("flag",",all,");
	    Rpc({functionId:'CF01030001',success:managerDashboard.loadeTable},map);
	},
	
	loadeTable: function(form){
		
		var result = Ext.decode(form.responseText);
		if(!result.succeed){
			
			if(!Ext.isEmpty(result.message)){
				Ext.showAlert(result.message);
				return;
			}
		}
		if("1"==result.cerFlag){
			Ext.showAlert("请配置证书相关参数！");
			return;
		}
		// 总数
		managerDashboard.certTotalNum = result.certTotalNum;
		managerDashboard.empCertTotalNum = result.empCertTotalNum;
		managerDashboard.empTotalNum = result.empTotalNum;
		// 逾期
		managerDashboard.overdueData = result.overdueData;
		// 待办
		managerDashboard.dealtData = result.dealtData;
		
		// 证书分布情况数据
		managerDashboard.cerdistData = result.cerdistData;
		managerDashboard.clickItemData = result.cerdistData;
		// 证书借阅分布初始等于1
		managerDashboard.borflag = "1";
		// 证书对应指标
		managerDashboard.cerFieldsetid = result.cerFieldsetid;
		// 点击类别分布穿透后导航
		managerDashboard.navigationToolHTML = "";
		managerDashboard.navigationToolList = [];
		
		var allPanel = Ext.create('Ext.panel.Panel', {
			id: 'panelid',
		    title: false,
		    border: false,
//		    layout:'vbox',//垂直布局
		    layout:'border',
		    autoScroll: true,
		    minWidth: 900,
		    autoHeight: true,//自动高度 
		    bodyStyle:'background:#f0f2f5',
		    items: [{
		    	region:'north',
		        width: '99%',
		        layout:'hbox',
		        height : 210,
		        margin: '10 10 0 10',
		        border: false,
		        bodyStyle:'background:#f0f2f5',//DBDBDB
	        	items:[{
	        		xtype : 'panel',
	        		width: '29%',
	        		height : '100%',
	        		margin: '0 5 0 0',
//	        		minWidth: 400,
	        		border: false,
	        		html:managerDashboard.getTotalPanel()
	        		}
	        	,{
	        		xtype:'panel',	
	        		width: '29%',
	        		height : '100%',
	        		border: false,
	        		margin: '0 5 0 5',
	    			html:managerDashboard.getOverduePanel()
	        	}
	        	,{
	        		xtype:'panel',	
	        		id: 'dealtPanelid',
	        		width: '42%',
	        		height : '100%',
//	        		minWidth: 400,
	        		border: false,
	        		margin: '0 0 0 5',
	    			html:managerDashboard.getBorrowDealtPanel()
	        	}]
		    }
		    ,{
		    	region:'center',
        		xtype : 'panel',
        		layout:'fit',
        		width: '99%',
        		margin: '10 10 10 10',
        		border: false,
        		items :[managerDashboard.getCertifcPanel(Ext.getBody().getHeight()-330)]
    		}],
    		listeners:{
    			'resize':function(){
    				// 44833 IE浏览器初始时获取不到页面高度导致柱状图高度不对
    				var bodyHeight = Ext.getBody().getHeight();
    				var heightNum = (Ext.getBody().getWidth()<900) ? (bodyHeight-330) : (bodyHeight-320);
   					Ext.getCmp('chartid').setHeight(heightNum);
    			}
	   		},
		    renderTo: Ext.getBody()
		});
		
		new Ext.Viewport({
			layout:'fit',
			autoScroll: true,
			id:"mainPanel1",
			items:allPanel
		});
		/**
		 * 监听统计图大小
		 */
		Ext.on('resize', function (width, height){
			var chartDiv = document.getElementById('chartid');
			var bool = width<900;
			var wvalue = (bool)?900:width;
			chartDiv.style.width = wvalue;
			chartDiv.firstChild.style.width = wvalue;
			var hvalue = (bool)?(height-330):(height-320);
			chartDiv.style.height = hvalue; 
			chartDiv.firstChild.style.height = hvalue; 
			if(null != managerDashboard.myChart && !Ext.isEmpty(managerDashboard.myChart))
				managerDashboard.myChart.resize();
		});
	},
	/**
	 * 证书各种情况panel
	 */
	getCertifcPanel:function(heightNum){
		var certifcPanel = Ext.create("Ext.container.Container",{
			width : '100%',
			height : '100%',
			layout : 'vbox',
			items : [
				managerDashboard.getTopTitleTags()
				,managerDashboard.titleTagsLine()
				,managerDashboard.navigationTool()
				,managerDashboard.createBarChart(heightNum)
			]
		});
		return certifcPanel;
	},
	/**
	 * 点击类别 穿透 导航
	 */
	navigationTool:function(){
		
		var panel = Ext.create("Ext.panel.Panel",{
			id : 'navigationPanelid',
			border: false,
			width:'98%',
    		height : 30,
    		layout:'fit',
//    		margin: '5 75 0 0',
    		hidden : true,
			items:[{
			    	   xtype: 'label',
			    	   id : 'navigationLabelid',
			    	   width:'98%',
			    	   height : 30,
			    	   border: false,
			    	   margin: '10 0 0 20',
//			    	   hidden : false,
					   html  : ""
			       }
			]
		});
		return panel;
	},
	/**
	 * 
	 */
	titleTagsLine:function(){
		var widthall = Ext.getBody().getWidth();
		//<div style='position: absolute;height:22px;width:0;border:solid 3px #78C5FF;margin:0 0 0 0;'></div>
		var line1 = "<div style='position: absolute;height:0;width:"+widthall+"px;border:solid 0.5px #DBDEE0;margin:0 0 0 0;'></div>";
		var line2 = "<div style='position: absolute;height:0;width:30px;border:solid 0.5px #DBDEE0;margin:0 0 0 0;'></div>";
		var line3 = "<div style='position: absolute;height:0;width:90px;border:solid 1px #78C5FF;margin:0 0 0 0;'></div>";
		var line3_1 = "<div style='position: absolute;height:0;width:90px;border:solid 0.5px #DBDEE0;margin:0 0 0 0;'></div>";
		
		var line4 = "<div style='position: absolute;height:0;width:46px;border:solid 0.5px #DBDEE0;margin:0 0 0 0;'></div>";
		
		var panel = Ext.create("Ext.panel.Panel",{
			border: false,
			width:'98%',
    		height : 2,
    		layout:'hbox',
			items:[{
			    	   xtype: 'label',
			    	   width:30,
			    	   height : 2,
			    	   border: false,
					   html  : line2
			       },{
			    	   id : 'cerFF',
			    	   xtype: 'label',
			    	   width:90,
			    	   height : 2,
			    	   border: false,
			    	   hidden : false,
					   html  : line3
			       },{
			    	   id : 'cerDB',
			    	   xtype: 'label',
			    	   width:90,
			    	   height : 2,
			    	   border: false,
			    	   hidden : true,
					   html  : line3_1
			       },{
			    	   xtype: 'label',
			    	   width:46,
			    	   height : 2,
			    	   border: false,
					   html  : line4
			       },{
			    	   id : 'browFF',
			    	   xtype: 'label',
			    	   width:90,
			    	   height : 2,
			    	   border: false,
			    	   hidden : true,
					   html  : line3
			       },{
			    	   id : 'browDB',
			    	   xtype: 'label',
			    	   width:90,
			    	   height : 2,
			    	   border: false,
			    	   hidden : false,
					   html  : line3_1
			       },{
			    	   xtype: 'label',
			    	   width:46,
			    	   height : 2,
			    	   border: false,
					   html  : line4
			       },{
			    	   id : 'expireFF',
			    	   xtype: 'label',
			    	   width:90,
			    	   height : 2,
			    	   border: false,
			    	   hidden : true,
					   html  : line3
			       },{
			    	   id : 'expireDB',
			    	   xtype: 'label',
			    	   width:90,
			    	   height : 2,
			    	   border: false,
			    	   hidden : false,
					   html  : line3_1
			       },{
			    	   xtype: 'label',
			    	   width:100,
			    	   height : 2,
			    	   border: false,
					   html  : line1
			       }]
		});
		return panel;
	},
	/**
	 * 证书各种情况panel
	 */
	createBarChart:function(heightNum){
		
		var barChart = Ext.create("Ext.container.Container",{
			id: 'chartid',
			border:1,
			width:'100%',
    		height : heightNum,
			layout:'fit' 
		});
		
		var charset = Ext.Loader.config.scriptCharset;
		Ext.Loader.config.scriptCharset="UTF-8";
		Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:this.drawChildChart,scope:this});
		Ext.Loader.config.scriptCharset=charset;
		
		return barChart;
	},
	/**
	 * 点击柱状图穿透事件
	 */
	throughClick:function(cloudid, typeName, type, flag){
		
		var map = new HashMap();
		// manager:管理员；employee:员工
		map.put("opt", managerDashboard.roleType);
		map.put("flag",",clickItem,");
		map.put("childItem", cloudid);
		// 借阅证书分布类别穿透
		if('borrowType' == type){
			
			map.put("flag",",borrowType,");
			map.put("borflag",managerDashboard.borflag);
			Rpc({functionId:'CF01030001',success:function(form){
					var result = Ext.decode(form.responseText);
					if(result.succeed){
						var typeflag = result.typeflag;
						if("1" == typeflag){
							// 加载导航条
							managerDashboard.getNavigationToolFunc(cloudid, typeName, type, flag);
							
							managerDashboard.clickItemData = result.datalist;
							managerDashboard.drawChildChart("borrowType");
						}else if("2" == typeflag){
							
							managerDashboard.showAllOverdue(result.datalist, typeName+"证书");
						}
					}
				}
			},map);
		}
		// 证书到期情况分布
		else if('exprieType' == type){
				
				map.put("flag",",exprieType,");
				Rpc({functionId:'CF01030001',success:function(form){
						var result = Ext.decode(form.responseText);
						if(result.succeed){
							managerDashboard.showCerInfo(result.exprieTypeButionData, typeName + "证书");
						}
					}
				},map);
			}
		// 证书分布类别穿透
		else{
			
			Rpc({functionId:'CF01030001',success:function(form){
					var result = Ext.decode(form.responseText);
					if(result.succeed){
						var typeflag = result.typeflag;
						if("1" == typeflag){
							// 加载导航条
							managerDashboard.getNavigationToolFunc(cloudid, typeName, type, flag);
							
							managerDashboard.clickItemData = result.datalist;
							managerDashboard.drawChildChart();
						}else if("2" == typeflag){
							
							managerDashboard.showCerInfo(result.datalist, typeName+"类别证书");
						}
					}
				}
			},map);
		}
		
	},
	/**
	 * 加载导航条
	 */
	getNavigationToolFunc:function(cloudid, typeName, type, flag){
		
		var typeInfo = "";
		// 借阅证书分布类别穿透
		if('borrowType' == type)
			typeInfo = "2";
		else
			typeInfo = "1";
		
		var firstHTML = "<a style='cursor:pointer;font-size: 14px;font-family:宋体;margin:0 5px 0 0;'" 
			+" onclick='managerDashboard.clickBution(\""+typeInfo+"\")'> 全部</a>" 
			+"<span style='font-size: 14px;margin:0 5px 0 5px;'>\></span>";
		
		// 点击导航条的分类
		if("navigation" == flag){
			
			var strhtml1 = "<a style='cursor:pointer;font-size: 14px;font-family:宋体;margin:0 5px 0 0;'" 
				+ " onclick='managerDashboard.throughClick(\"";
			var strhtml2 = "\",\"";
			var strhtml3 = 	"\",\"navigation\")'> ";
			var strhtml4 = "</a>";
			
			var strAllHtml = "";
			var newToollist = [];
			var toollist = managerDashboard.navigationToolList;
			for(var i=0;i<toollist.length;i++){
				
				var one = toollist[i];
				newToollist.push(one);
				if(cloudid==one.cloudid && typeName==one.typeName){
					strAllHtml += strhtml1+ one.cloudid +strhtml2+ one.typeName +strhtml2+ one.type +strhtml3
							+"<b>"+ one.typeName +"</b>"+strhtml4;
					break;
				}else{
					strAllHtml += strhtml1+ one.cloudid +strhtml2+ one.typeName +strhtml2+ one.type +strhtml3
							+ one.typeName +strhtml4;
				}
				strAllHtml += "<span style='font-size: 14px;margin:0 5px 0 5px;'>\></span>";
			}
			
			managerDashboard.navigationToolList = newToollist;
			managerDashboard.navigationToolHTML = firstHTML + strAllHtml;
			
			Ext.getCmp("navigationPanelid").setHidden(false);
			Ext.getCmp('navigationLabelid').setHtml(managerDashboard.navigationToolHTML);
		}
		// 正常点击分类 加载新的导航条
		else{
			
			var strhtml = "<a style='cursor:pointer;font-size: 14px;font-family:宋体;margin:0 5px 0 0;'" +
					" onclick='managerDashboard.throughClick(\""+cloudid+"\",\""+typeName+"\",\""+type+"\",\"navigation\")'> "
					+"<b>"+typeName+"</b></a>";
			
			if(!Ext.isEmpty(managerDashboard.navigationToolHTML)){
				managerDashboard.navigationToolHTML = managerDashboard.navigationToolHTML.replace(/\<b\>/g,"");
				managerDashboard.navigationToolHTML = managerDashboard.navigationToolHTML.replace(/\<\/b\>/g,"");
				managerDashboard.navigationToolHTML += "<span style='font-size: 14px;margin:0 5px 0 5px;'>\></span>"
			}
			else
				managerDashboard.navigationToolHTML = firstHTML+managerDashboard.navigationToolHTML;
			managerDashboard.navigationToolHTML += strhtml;
			
			var map = new HashMap();
			map.put("cloudid", cloudid);
			map.put("typeName", typeName);
			map.put("type", type);
			managerDashboard.navigationToolList.push(map);
			
			Ext.getCmp("navigationPanelid").setHidden(false);
			Ext.getCmp('navigationLabelid').setHtml(managerDashboard.navigationToolHTML);
		}
	},
	/**
	 * 绘图 统计分析图
	 */
	drawChildChart:function(type){
		managerDashboard.myChart = null;
		// 获取option对象
		var option = managerDashboard.getOption(managerDashboard.clickItemData);
		var myChart1 = echarts.init(document.getElementById('chartid'),'shine');
		// 先销毁再创建
		myChart1.dispose();
		myChart1 = echarts.init(document.getElementById('chartid'),'shine'); 
		myChart1.setOption(option);
		myChart1.on('click', function (param,e) {
			//如果个数为零直接返回
			var num=option.series[param.seriesIndex].data[param.dataIndex];
			if (num<1) {
				return;
			}
			// 控制台打印数据的名称
			var cloudid = option.series[param.seriesIndex].rawadata[param.dataIndex];
			managerDashboard.throughClick(cloudid, param.name, type);
			
		});
		managerDashboard.myChart = myChart1;
	},
	/**
	 * 获取柱状图option
	 */
	getOption:function(clickItemData){
		
		var xAxislist = [];
		var serieslist = [];
		var itemidlist = [];
		// 气泡效果数据 
		var markPointlist = [];
		// 分类数量
		var xAxisLen = xAxislist.length;
		var colorList = [];
		var colors = ['#338DC9','#EE7541','#2BD62B','#DBDC26','#8FbC8B','#D2B48C','#DC648A'
			,'#21B2AA','#B0C4DE','#DDA0DD','#9C9AFF','#9C3164','#FFB248','#1fcf03','#005eaa'
			,'#339ca8','#d9b014','#32a487','#333333','#FFB6C1','#FF69B4','#D8BFD8','#DDA0DD','#FF00FF'];
		
		var col = parseInt(xAxisLen/24) + 1;
		for(var i=0;i<col;i++){
			for(var j=0;j<24;j++){
				colorList.push(colors[j]);
			}
		}
		var map = new HashMap();
		for(var i=0;i<clickItemData.length;i++){
			var one = clickItemData[i];
			xAxislist.push(one.codeitemdesc);
			serieslist.push(one.count);
			itemidlist.push(one.codeitemid);
			
			map = new HashMap();
			map.put("xAxis", i);
			map.put("yAxis", one.count);
			//添加气泡颜色控制
			normalMap=new HashMap();
			itemStyleMap=new HashMap();
			normalMap.put("color",colorList[i])
			itemStyleMap.put("normal",normalMap);
			map.put("itemStyle",itemStyleMap);
			
			markPointlist.push(map);
		}
	
		
		var width = parseInt((100 / xAxisLen) * 5);
		if(width > 40 || xAxisLen > 8)
			width = 40;
		// 如果分类个数过多则斜着显示名称
		var rotateValue = 0;
		// 分类名称长度
		var maxLen = 10;
		if(xAxisLen > 8 || Ext.getBody().getWidth() < 800){
			rotateValue = -45;
			maxLen = 4;
		}
		// 滚动条按8个一组显示
		var endindex =  (8.0/xAxisLen)*100;
		// 底部边距
		var bottomValue = '5%';
		if(xAxisLen > 8)
			bottomValue = '12%';
		
		var option = {
			    color: ['#3398DB'],
			    tooltip : {
			        trigger: 'axis',
			        // 坐标轴指示器，坐标轴触发有效
			        axisPointer : {            
			        	// 默认为line(直线)，可选为：line(直线) | shadow(阴影) | none(无)
			            type : 'line',
			            z:10
			        }
			    },
			    grid: {
			        left: '2%',
			        right: '2%',
			        bottom: bottomValue,
			        containLabel: true
			    },
			    xAxis : [{
			            type : 'category',
			            data : xAxislist,
			            axisTick: {
			                alignWithLabel: true
			            },
			            axisLabel:{
						     interval:0,//横轴信息全部显示
						     rotate:rotateValue,//-45度角倾斜显示
						     margin:22,
						     formatter :function(value){
						    	 var ret = "";
						    	 var maxLength =maxLen;
						    	 var valLength = value.length;
						    	 var rowN = Math.ceil(valLength / maxLength);
						    	 if (rowN > 1){
						    		 for (var i = 0; i < rowN; i++) {
						    			 var temp = '';
						    			 var start = i * maxLength;
						    			 var end = start + maxLength;
						    			 temp = value.substring(start, end) + "\n";
						    			 ret += temp;
						    		 }
						    		 return ret;
						    	 }else {
						    		 return value;
						    	 }
						     }
						}
			        }],
			    yAxis : [{
			            type : 'value'
			        }],
		        series : [{
			            name: '证书数',
			            type: 'bar',
			            barWidth: width + "%",//'40%',//
			            data: serieslist,
			            rawadata: itemidlist,
			            label: {
			                normal: {
			                	fontSize: 25,
			                    show: false,
			                    position: 'top'
			                }
			            },
			            itemStyle: {
			            	normal: {
			            		color: function(params) {
			            			return colorList[params.dataIndex]
			            		}
			            	}
			            },
			            // 气泡
			            markPoint : {
			                 data : markPointlist,
					         label: {
					                normal: { 
					                	fontSize: 12, 
					                    show:true,
					                }
					            }
			            }
			        }]
			};
		//  超过8个分类增加x轴滚动条
		if(xAxisLen > 8){
			option.dataZoom ={ 
					zoomLock:true,
					realtime:true,
					height:20,
					textStyle:false,
					start:0,
					end:endindex
			};
		}
		
		return option;
	},
	/**
	 * 借阅情况
	 */
	borrowBution:function(borflag){
		
		managerDashboard.navigationToolHTML = "";
		managerDashboard.navigationToolList = [];
		Ext.getCmp("navigationPanelid").setHidden(true);
		
		var map = new HashMap();
		// manager:管理员；employee:员工
		map.put("opt", managerDashboard.roleType);
		map.put("flag", ",borrowBution,");
		map.put("borflag", borflag);
	    Rpc({functionId:'CF01030001',success:function(form){
	    	var result = Ext.decode(form.responseText);
		    if(result.succeed){
		    	managerDashboard.clickItemData = result.borrowButionData;
				managerDashboard.drawChildChart("borrowType");
		    }
	    },scope:this},map);
	    
	},
	/**
	 * 证书到期情况
	 */
	cerExprieBution:function(){
		
		var map = new HashMap();
		// manager:管理员；employee:员工
		map.put("opt", managerDashboard.roleType);
		map.put("flag", ",exprieBution,");
	    Rpc({functionId:'CF01030001',success:function(form){
	    	var result = Ext.decode(form.responseText);
		    if(result.succeed){
		    	managerDashboard.clickItemData = result.exprieButionData;
				managerDashboard.drawChildChart("exprieType");
		    }
	    },scope:this},map);
	    
	},
	/**
	 * 切换证书分布情况页签
	 */
	clickBution:function(flag){
		managerDashboard.flag = flag;
		// 证书分布情况
		if(1 == flag){
			managerDashboard.navigationToolHTML = "";
			managerDashboard.navigationToolList = [];
			Ext.getCmp("navigationPanelid").setHidden(true);
			
			managerDashboard.clickItemData = managerDashboard.cerdistData;
			managerDashboard.drawChildChart();
			
			Ext.getCmp("cerFF").setHidden(false);
			Ext.getCmp("cerDB").setHidden(true);
			Ext.getCmp("browFF").setHidden(true);
			Ext.getCmp("browDB").setHidden(false);
			Ext.getCmp("expireFF").setHidden(true);
			Ext.getCmp("expireDB").setHidden(false);
			
			Ext.getCmp("borrowBoxid").setHidden(true);
		}
		// 证书借阅情况
		else if(2 == flag){
			managerDashboard.navigationToolHTML = "";
			managerDashboard.navigationToolList = [];
			Ext.getCmp("navigationPanelid").setHidden(true);
			// 初始下拉框状态标识
			managerDashboard.borflag = "1";
			var borrowBox = Ext.getCmp("borrowBoxid");
			borrowBox.setValue(borrowBox.getStore().getAt(0));
			
			Ext.getCmp("cerFF").setHidden(true);
			Ext.getCmp("cerDB").setHidden(false);
			Ext.getCmp("browFF").setHidden(false);
			Ext.getCmp("browDB").setHidden(true);
			Ext.getCmp("expireFF").setHidden(true);
			Ext.getCmp("expireDB").setHidden(false);
			
			Ext.getCmp("borrowBoxid").setHidden(false);
			managerDashboard.borrowBution("1");
		}
		// 证书到期情况
		else if(3 == flag){
			managerDashboard.navigationToolHTML = "";
			managerDashboard.navigationToolList = [];
			Ext.getCmp("navigationPanelid").setHidden(true);
			
			Ext.getCmp("cerFF").setHidden(true);
			Ext.getCmp("cerDB").setHidden(false);
			Ext.getCmp("browFF").setHidden(true);
			Ext.getCmp("browDB").setHidden(false);
			Ext.getCmp("expireFF").setHidden(false);
			Ext.getCmp("expireDB").setHidden(true);
			
			Ext.getCmp("borrowBoxid").setHidden(true);
			managerDashboard.cerExprieBution();
		}
	},
	/**
	 * 各种情况title展现
	 */
	getTopTitleTags:function(){
		var typeStore = Ext.create('Ext.data.Store', {
    	    fields: ['codeitem', 'codename'],
    	    data : [
    	    		{codeitem: '1',   codename: '按证书类别统计'},
    	    		{codeitem: '2',    codename: '按预计归还日期统计'}
    	         ]
    	});
		
		var titleTags = Ext.create("Ext.container.Container",{
			id:'titleTags',
			margin:'5 0 0 5',
			layout:'hbox',
			region:'north',
			defaultType:'container',
			items:[{// 证书分布情况
				id:"curveTitle",
				width:112,
				height:32,
				margin:'10 10 0 15',
				html:'<div id="curveTitleid" style="height:30px;font-family:宋体;cursor:pointer;" >'
					+'<a style="font-size: 18px;" onclick="managerDashboard.clickBution(1)">证书分布情况</a></div>'
			},{// 证书借阅情况
				id:"borrowTitle",
				margin:'10 10 0 15',
				width:112,
				height:32,
				html:'<div id="borrowTitleid"  style="height:30px;font-family:宋体;cursor:pointer;" >'
					+'<a style="font-size: 18px;" onclick="managerDashboard.clickBution(2)" >证书借阅情况</a></div>'
			},{// 证书到期情况
				id:"exprieTitle",
				margin:'10 10 0 15',
				width:112,
				height:32,
				html:'<div id="exprieTitleid"  style="height:30px;font-family:宋体;cursor:pointer;" >'
					+'<a style="font-size: 18px;" onclick="managerDashboard.clickBution(3)" >证书到期情况</a></div>'
			},{//  证书借阅情况 下拉
				xtype : 'combo',
				id : "borrowBoxid",
	    	   	width : 200,
	    	   	height : 22,
	    	   	hidden : true,
	    	   	fieldLabel: false,
	    	   	editable : false,//不允许编辑 
	    	    store: typeStore,
	    	    queryMode: 'local',
	    	    displayField: 'codename',
	    	    valueField: 'codeitem',
	    	    margin : '11 0 0 15',
	    	    anchor: '100%',
	    	    listeners : {
					render : function(combo) {
						combo.setValue(combo.getStore().getAt(0));
					},
			        select: function (combo, record, index) {
			        	var selectValue = record.get('codeitem');
			        	managerDashboard.borflag = selectValue;
			        	managerDashboard.borrowBution(selectValue);  
			        },
					scope : this
				}
			}]
		});
		
		return titleTags;
	},
	/**
	 * 证书信息Store
	 */
	getCerData : function(cerInfoData) {

		var cerInfoStore = Ext.create('Ext.data.Store', {
			//'cerType', 
			fields : ['cerType', 'cerNum', 'cerName', 'certPer', 'certEndDate', 'certStat', 'certOrg'],
//			groupField : 'cerType',
			data : cerInfoData
		});
		return cerInfoStore;
	},
	/**
	 * 证书信息Grid
	 */
	cerInfoGrid : function(cerInfoData){
    	
    	var cerInfoGrid = Ext.create('Ext.grid.Panel', {
//			id:'loggrid',
			title : false,
			border : 1,
//			width : '100%',
			height: 400,
			width: 690,
//			selModel:Ext.create('Ext.selection.CheckboxModel',{mode:"SIMPLE"}),
			store : managerDashboard.getCerData(cerInfoData),
//			viewConfig: {
//                enableTextSelection: true
//            },
			columns : [{
						header : '证书类别',
						dataIndex : 'cerType',
						menuDisabled : true,
						sortable : false,
						flex : 1.5
					}, {
						header : '证书编号',
						dataIndex : 'cerNum',
						menuDisabled : true,
						sortable : false,
						flex : 1.5
					}, {
						header : '证书名称',
						dataIndex : 'cerName',
						menuDisabled : true,
						sortable : true,
						flex : 2
					}, {
						header : '证书所有人',
						dataIndex : 'certPer',
						menuDisabled : true,
						sortable : true,
						flex : 1.5
					}, {
						header : '到期时间',
						dataIndex : 'certEndDate',
						menuDisabled : true,
						sortable : false,
						flex : 1.2
					}, {
						header : '证书状态',
						dataIndex : 'certStat',
						menuDisabled : true,
						sortable : false,
						flex : 1
					}
					,{
						header : '所属机构',
						dataIndex : 'certOrg',
						menuDisabled : true,
						sortable : false,
//						renderer :function(value) {
//							return value.substring(11);
//						},
						flex : 1.5 
					}
					]
//    	,
//				plugins : [{}],
//				listeners:{}
		});
    	
    	return cerInfoGrid;
    	
    },
    /**
     * 显示证书信息表格控件对象
     */
	showCerInfo:function(tableConfig, typeName){
		
		var obj = Ext.decode(tableConfig);
		var tableObjGrid = new BuildTableObj(obj);
		var tablePanel = tableObjGrid.getMainPanel();
		
		var cerInfoWin = Ext.create('Ext.window.Window', {
			id : 'tablePanelid',
			modal : true,
			title: typeName,
			height: 450,
			width: 700,
			border: false,
			layout:'fit',
			items: [
//				managerDashboard.cerInfoGrid(tableConfig)
				tablePanel
				]
			
		});
		cerInfoWin.show();
	},

	/**
	 * 显示所有证书
	 */
	showAllCers:function(){
		
		var map = new HashMap();
		// manager:管理员；employee:员工
		map.put("opt", managerDashboard.roleType);
		map.put("flag", ",allCers,");
//		map.put("nbase", datainfo.nbase);
//		map.put("A0100", datainfo.A0100);
	    Rpc({functionId:'CF01030001',success:function(form){
	    	var result = Ext.decode(form.responseText);
		    if(result.succeed)
		    	managerDashboard.showCerInfo(result.tableConfig, "全部证书");//result.allCersData
	    },scope:this},map);
	},
	/**
     *  证书总数
     */
	getTotalPanel:function(){
		
		var numHtml = "<h2><a style='font-size:35px;cursor:pointer;color:#78C5FF;' onclick='managerDashboard.showAllCers()'>"
						+managerDashboard.certTotalNum+"</a></h2>";
		// 如果为0则显示暂无
		if(0 == managerDashboard.certTotalNum){
			numHtml = "<h2><span style='color:#979797;'>暂无</span></h2>";
		}
		
		return {
			   xtype: 'label',
			   border: false,
			   height: 200,
			   width: 300,
			   margin:'20 0 0 20',
			   layout:'hbox',
			   html  : "<div style='height:200px;width:100%;border:0px;margin:0 0 0 0;font-size: 18px;font-family:宋体;'>" //color:#979797;
					   	+"<div style='height:30px;width:330px;margin:12px 0 0 20px;'>" 
					   		+"证书总数"
						+"</div>"
//						+"</br>"//class='content' 
						+"<div style='height:80px;width:100%;border:0px;text-align:center;font-size:25px;color:#000000;'>"//本
						+numHtml
						+"</div></br>"
						+"<div style='margin:0 0 0 10px;height:0;width:95%;border:solid 1px #EAEDED;'></div>"
						+"<div style='margin:4px 0 0 20px;'>总人数"+managerDashboard.empTotalNum+"&nbsp;&nbsp;&nbsp;&nbsp;持证人数"
						+managerDashboard.empCertTotalNum+"</div>"
						//style='height:20;width:100%;text-align:bottom;'
						+"</div>"
		};
		
	},
	/**
     *  逾期未还数
     */
	getOverduePanel:function(){
		
		var numHtml = "<h2><a style='cursor:pointer;' onclick='managerDashboard.getOverdueTableConfig(1, \"逾期未还证书\")'>"
						+"<font style='font-size:35px;color:#78C5FF;'>"+managerDashboard.overdueData.length+"</font></a></h2>";
		var overclick = "<div style='float:right;margin:4px 20px 0 0;color:#78C5FF;'>" 
							+"<a style='font-size:18px;cursor:pointer;' onclick='managerDashboard.getOverdueTableConfig(1, \"逾期未还证书\")' >逾期详情</a>"
						+"</div>";
		
		// 如果为0则显示暂无
		if(0 == managerDashboard.overdueData.length){
			
			numHtml = "<h2><span style='color:#979797;'>暂无</span></h2>";
			overclick = "<div style='font-size:18px;float:right;margin:4px 20px 0 0;color:#979797;'>" 
				+"逾期详情"
				+"</div>";
		}
		
		return {
			   xtype: 'label',
			   border: false,
			   height: 200,
			   width: 300,
			   margin:'20 0 0 20',
			   layout:'hbox',
			   html  : "<div style='height:200px;width:100%;border:0px;margin:0 0 0 0;font-size: 18px;font-family:宋体;'>" //color:#979797;
				   	+"<div style='height:30px;width:330px;margin:12px 0 0 20px;'>" 
				   		+"逾期未还证书数"
					+"</div>"
//						+"</br>"//class='content' 
					+"<div style='height:80px;width:100%;border:0px;text-align:center;font-size:25px;color:#000000;'>"
					+ numHtml
					+"</div></br>"
					+"<div style='margin:0 0 0 10px;height:0;width:95%;border:solid 1px #EAEDED;'></div>"
					+ overclick
		};
		
	},
	/**
	 * 获取逾期未还数据
	 */
	getOverdueData : function(datalist) {

		var overdueStore = Ext.create('Ext.data.Store', {
					//			id:'storey',
					fields : ['certName', 'certPer', 'bowDate','returnDate',
							'perName'],
//					groupField : 'perName',
					data : datalist
				});
		return overdueStore;
	},
	/**
	 * 获取逾期未还数据表格
	 */
	overdueGrid : function(flag, datalist){
    	
    	var overdueGrid = Ext.create('Ext.grid.Panel', {
//			id:'loggrid',
			title : false,
			border : 1,
//			width : '100%',
			height: 400,
			width: 690,
//			selModel:Ext.create('Ext.selection.CheckboxModel',{mode:"SIMPLE"}),
			store : managerDashboard.getOverdueData(datalist),
//			viewConfig: {
//                enableTextSelection: true
//            },
			columns : [{
						header : '证书名称',
						dataIndex : 'certName',
						menuDisabled : true,
						sortable : false,
						flex : 3
					}, {
						header : '证书所有人',
						dataIndex : 'certPer',
						menuDisabled : true,
						sortable : false,
						flex : 2
					}, {
						header : '借阅时间',
						dataIndex : 'bowDate',
						menuDisabled : true,
						sortable : true,
						flex : 1.8
					}, {
						header : '预计归还时间',
						dataIndex : 'returnDate',
						menuDisabled : true,
						sortable : false,
						flex : 1.8
					}, {
						header : '借阅人',
						dataIndex : 'perName',
//						align : 'right',
						menuDisabled : true,
						sortable : false,
						scope : this,
						flex : 1.5
					}
					,{
						header : '提醒',
						dataIndex : 'remind',
						hidden : (1==flag) ? false : true,
//						renderer :function(value) {
//							return value.substring(11);
//						},
						flex : 1 
					}
					]
//    	,
//				plugins : [{}],
//				listeners:{}
		});
    	
    	return overdueGrid;
    	
    },
    
    getOverdueTableConfig:function(flag){
		
		var map = new HashMap();
		// manager:管理员；employee:员工
		map.put("opt", managerDashboard.roleType);
		map.put("flag", ",overdueCers,");
//		map.put("nbase", datainfo.nbase);
//		map.put("A0100", datainfo.A0100);
	    Rpc({functionId:'CF01030001',success:function(form){
	    	var result = Ext.decode(form.responseText);
		    if(result.succeed)//1, \"逾期未还证书\"
		    	managerDashboard.showAllOverdue(result.overdueTableConfig, "逾期未还证书");//result.allCersData
	    },scope:this},map);
	    
	},
    /**
     * 展示逾期未还全部数据
     */
	showAllOverdue:function(tableConfig, titleName, datalist){
		
//		if(1 == flag){
//			datalist = managerDashboard.overdueData;
//		}
		
		var obj = Ext.decode(tableConfig);
		var tableObjGrid = new BuildTableObj(obj);
		var tablePanel = tableObjGrid.getMainPanel();
		
		var overdueWin = Ext.create('Ext.window.Window', {
//			id : 'qxjWinid',
			modal : true,
			title: titleName,
			height: 450,
			width: 700,
			border: false,
			layout:'fit',
			items: [
//				managerDashboard.overdueGrid(flag, datalist)
				tablePanel
				]
			
		});
		overdueWin.show();
	},
	/**
	 * 逾期提醒 操作列渲染函数
	 */
	remindFunc:function(value, metaData, Record){
		
		var cerbrow = managerDashboard.cerFieldsetid.certBorrowSubset;
		var cerbrow05 = eval("Record.data."+cerbrow+"05");
		var cerbrow07 = eval("Record.data."+cerbrow+"07");
		var cerbrow09 = eval("Record.data."+cerbrow+"09");
		var cerbrow11 = eval("Record.data."+cerbrow+"11");
		var nbase = Record.data.nbase_e;
		var a0100 = Record.data.a0100_e;
		var a0101 = Record.data.a0101;
		
		return "<a href=javascript:void(0); onclick=managerDashboard.sendRemind(\""+cerbrow05+"\"" +
				",\""+cerbrow07+"\",\""+cerbrow09+"\",\""+cerbrow11+"\",\""+nbase+"\",\""+a0100+"\",\""+a0101+"\"); >提醒</a>";
	},
	/**
	 * 提醒操作
	 */
	sendRemind:function(cerbrow05, cerbrow07, cerbrow09, cerbrow11, nbase, a0100, a0101){
		
		Ext.showConfirm('确定提醒该借阅人归还吗？', function(btn) {
			if (btn == 'yes') {
				
				var map = new HashMap();
				// manager:管理员；employee:员工
				map.put("opt", managerDashboard.roleType);
				map.put("flag", ",remindCer,");
				map.put("nbase", nbase);
				map.put("A0100", a0100);
				map.put("A0101", a0101);
				map.put("cerName", cerbrow05);
				map.put("cerPerName", cerbrow07);
				map.put("borrowDate", cerbrow09);
				map.put("returnDate", cerbrow11);
				
				Rpc({functionId:'CF01030001',success:function(form){
					var result = Ext.decode(form.responseText);
					if(Ext.isEmpty(result.msg))
						Ext.showAlert("提醒成功！");
					else
						Ext.showAlert(result.msg);
					
				},scope:this},map);
			}
		}, this);
	},
	/**
     *  借阅待办
     */
	getBorrowDealtPanel:function(){
		
		var datagroup = "";
		datagroup = "<div style='margin:5px 0 0 40px;height:105px;width:100%;border:0px;color:#000000;'>" ;
		datagroup = datagroup    
				+ "<table width='90%' border='0' cellspacing='2' align='left' cellpadding='2' style='font-size: 13px;'>" 
		for(i=0;i<managerDashboard.dealtData.length;i++){
			if(i > 4)
				break;
			var datainfo = managerDashboard.dealtData[i];
			datagroup = datagroup    
						+ "<tr><td width='20px'>"
							+"<img style='margin: 0 5px 0 0;vertical-align: middle;' src='/images/hcm/themes/default/icon/icon6.png' />" 
						+ "</td><td>"
							+"<a style='font-size:13px;TEXT-DECORATION:none;cursor:pointer;' onclick='managerDashboard.addBorrowApp("+i+")'>"
							+ datainfo.info
							+ "</a>"
						+ "</td><td width='90px'>"
							+datainfo.createDate
						+ "</td></tr>"	
		}
		datagroup = datagroup + "</table></div>" 
					+"</br>"
					+"<div style='margin:6px 0 0 10px;height:0;width:95%;border:solid 1px #EAEDED;'></div>"
					+"<div style='float:right;margin:4px 20px 0 0;color:#78C5FF;'>" 
							+"<a  style='font-size: 18px;cursor:pointer;font-family:宋体;' onclick='managerDashboard.showAllDealt()' >查看全部</a></div>"
					+"</div>";
		// 如果没有待办 显示暂无
		if(0 == managerDashboard.dealtData.length){
			
			datagroup = "<div style='font-family:宋体;font-size:25px;color:#979797;text-align:center;height:90px;'><h2>暂无</h2></div>" 
				+"<div style='margin:10px 0 0 10px;height:0;width:95%;border:solid 1px #EAEDED;'></div>"
				+"<div style='font-family:宋体;font-size:18px;float:right;margin:4px 20px 0 0;color:#979797;'>" 
				+"查看全部</div>"
				+"</div>";
		}
		
		return {
			   xtype: 'label',
			   border: false,
			   height: 200,
			   width: 300,
			   margin:'20 0 0 20',
			   layout:'hbox',
			   html  : "<div style='height:200px;width:100%;border:0px;margin:0 0 0 0;'>" //color:#979797;
					   	+"<div style='height:30px;width:550px;margin:12px 0 0 20px;font-size: 18px;font-family:宋体;'>" 
					   		+"借阅待办"
						+"</div>"
						+datagroup
		};
		
	},
    /**
     * 展示待办全部数据
     */
	showAllDealt:function(){
		
		var allhtml = managerDashboard.getAllDealtDatas();
		var dealtWin = Ext.create('Ext.window.Window', {
			id : 'allDealtWinid',
			modal : true,
			title: "借阅待办",
			height: 450,
			width: 700,
			border: false,
			items: [{
				id : 'allDealtPanelid',
				xtype: 'panel',
				height: '100%',
				width: '100%',
				border: false,
				html: allhtml
				}]
			
		});
		dealtWin.show();
	},
	/**
	 * 获取待办窗口全部记录数据
	 */
	getAllDealtDatas : function(){
		
		
		var dealtgroup = "<table  width='90%' border='0' cellspacing='3' align='left' cellpadding='3' style='font-size: 14px;'>"
		for(i=0;i<managerDashboard.dealtData.length;i++){
			var datainfo = managerDashboard.dealtData[i];
			dealtgroup = dealtgroup
						+ "<tr><td width='20px'>"
							+"<img style='margin: 0 5px 0 0;vertical-align: middle;' src='/images/hcm/themes/default/icon/icon6.png' />" 
						+ "</td><td>"
							+"<a style='font-size:14px;TEXT-DECORATION:none;cursor:pointer;' onclick='managerDashboard.addBorrowApp("+i+")'>"
							+ datainfo.info
							+ "</a>"
						+ "</td><td width='90px'>"
							+datainfo.createDate
						+ "</td></tr>";
		}
		dealtgroup = dealtgroup + "</table>"	;  
			
		var allhtml = "<div style='OVERFLOW-Y: auto; OVERFLOW-X:hidden;height:420px;position:relative;margin:0 0 0 40px;border:0px;'>"	
					+dealtgroup
					+"</div>";
		return allhtml;
	},
	/**
	 * 获取待办数据
	 */
	getBorrowData : function(borrowCertificateData) {

		return Ext.create('Ext.data.Store', {
					fields : ['cerNum', 'cerName', 'cerType', 'cerPerName'],
//					groupField : 'perName',
					data : borrowCertificateData
				});
	},
	/**
     * 证书借阅申请窗口
     */
	addBorrowApp:function(index){
		
		var datainfo = managerDashboard.dealtData[index];
		var map = new HashMap();
		// manager:管理员；employee:员工
		map.put("opt", managerDashboard.roleType);
		map.put("flag", ",borrow,");
		map.put("nbase", datainfo.nbase);
		map.put("A0100", datainfo.A0100);
		map.put("borrowDate", datainfo.borrowDate);
		map.put("returnDate", datainfo.returnDate);
		map.put("borrowCause", datainfo.borrowCause);
	    Rpc({functionId:'CF01030001',success:function(form){
			var result = Ext.decode(form.responseText);
			if(result.succeed){
				
				managerDashboard.showBorrowWin(result.borrowCertificateData, result.userInfo, index, result.fieldItems);
			}
		},scope:this},map);
		
	},
	
	/**
	 * 借阅窗口
	 */
	showBorrowWin: function (records, userInfo, index, fieldItems){
		var win = Ext.getCmp("borrowWinId");
		if(win)
			win.close();
		
		var datainfo = managerDashboard.dealtData[index];
		// 借阅人
		var borrowPerson = Ext.create('Ext.panel.Panel', {
			height: 30,
		    width: 750,
		    border: false,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'hbox'
			},
		    items:[{
		    	xtype: 'label',
		        forId: 'myFieldId',
		        width: 100,
		        html: '<div style="text-align:right;"><font color=red>*</font>借阅人</div>',
		        margin: '5 5 0 0'
		    },{
				xtype:'panel',
				id:'borrowPersonId',
				border:false,
				html:'',
				margin: '5 5 0 0'
			},{
				xtype:'label',
				html:datainfo.A0101+"（"+userInfo+"）" ,
				margin: '5 0 0 0'
			}]
		});
		// 借阅日期
		var borrowDate = Ext.create('Ext.form.field.Text', {
			id:'borrowDateId',
			fieldLabel: '<font color=red>*</font>借阅日期',
			labelAlign:'right',
			labelWidth:100,
			height:23,
			value: datainfo.borrowDate,
			editable:false,
			disabled:true,
//			format:'Y-m-d',
			allowBlank:false,
			style:managerDashboard.disabledStyle

		});
		// 预计归还日期
		var estimateReturnDate = Ext.create('Ext.form.field.Text', {
			id:'returnDateId',
			fieldLabel: '<font color=red>*</font>预计归还日期',
			labelAlign:'right',
			labelWidth:100,
			height:23,
			value: datainfo.returnDate,
			editable:false,
			disabled:true,
//			format:'Y-m-d',
			margin: '0 0 0 100',
			allowBlank:false,
			style:managerDashboard.disabledStyle
		});
		// 借阅事由
		var borrowDesc = Ext.create('Ext.form.field.TextArea', {
			id:'borrowDescId',
			fieldLabel: '<font color=red>*</font>借阅事由',
			labelWidth:100,
			labelAlign:'right',
			width: 500,
			editable:false,
			disable:true,
			margin: '5 5 0 0',
			value: managerDashboard.getReplaceStr(getDecodeStr(datainfo.borrowCause)),
			allowBlank:false,
			style:managerDashboard.disabledStyle
		});

    	// 所选的借阅证书表格
	    var gridPanel = Ext.create('Ext.grid.Panel', {
    		id : 'borrowGridId',
			title : false,
			border : 1,
			height: 140,
			width: 550,
			store : managerDashboard.getBorrowData(records),
			columns : [{
						header : '证书编号',
						dataIndex : 'cerNum',
						menuDisabled : true,
						sortable : false,
						flex : 2
					}, {
						header : '证书名称',
						dataIndex : 'cerName',
						menuDisabled : true,
						sortable : false,
						flex : 2
					},{
						header : '证书类别',
						dataIndex : 'cerType',
						menuDisabled : true,
						sortable : false,
						flex : 2
					}, {
						header : '证书持有人',
						dataIndex : 'cerPerName',
						menuDisabled : true,
						sortable : false,
						flex : 1.5
					}]
		});
	    // 非系统指标项
		var fieldItemsPanel = Ext.create('Ext.panel.Panel', {
			id:'fieldItemsPanel',
			margin: '5 5 0 0',
			border:0,
			width:680,
			defaults:{
				labelWidth:100
			},
			scrollable:true,
			layout:{
				type:'table',
				columns:2,
				tdAttrs:{
					align:'left'
				},
				tableAttrs:{
					width:'100%'
				}
			},
			items:[]
		});
    	// 证书列表
		var panel = Ext.create('Ext.panel.Panel', {
		    height: 160,
		    width: 700,
		    border: false,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'hbox'
			},
			margin: '5 5 0 0',
		    items:[{
			    	xtype: 'label',
			        forId: 'myFieldId',
			        width: 100,
			        html: '<div style="text-align:right;">证书列表</div>',
			        margin: '5 5 0 0'
			    }
		    	,gridPanel]
		});
		// 审批意见
		var appOpinDesc = Ext.create('Ext.form.field.Text', {
			id:'appOpinId',
			fieldLabel: '<font color=red>*</font>审批意见',
			labelWidth:100,
			labelAlign:'right',
			width: 500,
			height:23,
			margin: '0 5 0 0',
			value: '',
			allowBlank:false
		});
		
		// 窗口初始
		win = Ext.create('Ext.window.Window', {
			id: 'borrowWinId',
		    title: '证书借阅登记',
		    autoHeight: true,
		    maxHeight:540,
		    width: 720,
		    modal:true,
		    layout: {
		        align: 'top',
		        pack: 'left',
		        type: 'vbox'
			},
			items:[borrowPerson
				, {
				border:false,
				layout: {
			        align: 'top',
			        type: 'hbox'
				},
				items:[
					borrowDate
					, estimateReturnDate
				]}
				, borrowDesc
				, fieldItemsPanel
				, panel
				, appOpinDesc],
			buttonAlign: 'center',
			buttons: [{
		    	text: '同意',
		    	handler:function(){
		    		managerDashboard.approveData(index, "03")
		    	}
			},{
		    	text: '退回',
		    	handler:function(){
		    		managerDashboard.approveData(index, "07")
		    	}
			},{
		    	text: '取消',
		    	handler:function(){
		    		win.close();
			    }
			}]
		});
		managerDashboard.setFieldItemsPanel(fieldItems);
		win.show();
	},
	/**
	 * 添加借阅子集其他指标
	 */
	setFieldItemsPanel:function(fieldList){
		var fieldItemsPanel=Ext.getCmp('fieldItemsPanel');
		fieldItemsPanel.removeAll ( true );
		//判断是否是同一行第一个子集
		var isFirstItem=false;
		var linePanel;
//		this.respon=respon;//respon.//respon.
		for(var i=0;i<fieldList.length;i++){
			var field=fieldList[i];
			var margin="5 0 5 0";
			isFirstItem=!isFirstItem;
			if(field.itemtype=="A"){
				if(field.codesetid=="0"){
					fieldItemsPanel.add({
						xtype:'textfield',
						id:field.itemid+"id",
						margin:margin,
						name:field.itemid,
						maxLength:field.itemlength,
//						allowBlank:field.allowblank,
						required:field.allowblank,
						fieldLabel:field.itemdesc,
						border:0,
						labelAlign:'right',
						labelWrap:true,
						clearIcon:true,
						editable:false,
						disabled:true,
						style:managerDashboard.disabledStyle,
						value:field.value,
						listeners:{
							change:function( me, newValue, oldValue, eOpts ) {
//								me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
							}
						}
					});
				}else{
					fieldItemsPanel.add({
						xtype:'codecomboxfield',
						id:field.itemid+"id",
						margin:margin,
						name:field.itemid,
						required:field.allowblank,
						ctrltype:'1',//this.getCtrltype(),
						nmodule:'0',//this.getNmodule(),
//						allowBlank : false, 
						fieldLabel:field.itemdesc,
						codesetid:field.codesetid,
						onlySelectCodeset:false,
						border:0,
						labelAlign:'right',
						labelWrap:true,
						clearIcon:true,
						disabled:true,
						style:managerDashboard.disabledStyle,
						value:field.value,
						listeners:{
							change:function( me, newValue, oldValue, eOpts ) {
//								me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
							}
						}
					});
				}
			}else if(field.itemtype=="M"){
				if(!isFirstItem){
					fieldItemsPanel.add({
						xtype:'container'
					})
				}
				fieldItemsPanel.add({
					xtype:'textarea',
					id:field.itemid+"id",
					name:field.itemid,
					colspan:2,
					required:field.allowblank,
					maxLength:field.itemlength==10?Number.MAX_VALUE:field.itemlength,
//					allowBlank:field.allowblank,
					fieldLabel:field.itemdesc,
					msgTarget :'under',
					border:0,
					width:500,//'90%',
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					editable:false,
					disabled:true,
					style:managerDashboard.disabledStyle,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
				isFirstItem=false;
			}else if(field.itemtype=="N"){
				fieldItemsPanel.add({
					xtype:'numberfield',
					id:field.itemid+"id",
					margin:margin,
					name:field.itemid,
					maxLength:field.itemlength,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					decimalPrecision:field.demicallength,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					readOnly:true,
					disabled:true,
					style:managerDashboard.disabledStyle,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}else if(field.itemtype=="D"){
				var format='Y';
				if(field.itemlength=='4'){
					format='Y';
				}else if(field.itemlength=='7'){
					format='Y-m';
				}else if(field.itemlength=='10'){
					format='Y-m-d';
				}else if(field.itemlength=='16'){
		          	format = 'Y-m-d H:i';
				}else if(field.itemlength=='18'){
		            format = 'Y-m-d H:i:s';
				}
				fieldItemsPanel.add({
					xtype:'datetimefield',
					id:field.itemid+"id",
					margin:margin,
					name:field.itemid,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					format:format,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					readOnly:true,
					disabled:true,
					style:managerDashboard.disabledStyle,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}else{
				fieldItemsPanel.add({
					xtype:'textfield',
					id:field.itemid+"id",
					margin:margin,
					name:field.itemid,
					maxLength:field.itemlength,
//					allowBlank:field.allowblank,
					required:field.allowblank,
					fieldLabel:field.itemdesc,
					border:0,
					labelAlign:'right',
					labelWrap:true,
					clearIcon:true,
					readOnly:true,
					disabled:true,
					style:managerDashboard.disabledStyle,
					value:field.value,
					listeners:{
						change:function( me, newValue, oldValue, eOpts ) {
//							me.ownerCt.ownerCt.ownerCt.doJudgeBtnShow();
						}
					}
				});
			}
		}
	},
	/**
	 * 审批待办
	 */
	approveData:function(index, flag){
		
		var appOpinValue = Ext.getCmp('appOpinId').getValue();
		if(Ext.isEmpty(appOpinValue)){
			Ext.showAlert("请填写审批意见！");
			return;
		}
		
		var flagValue = "同意";
		if("07" == flag)
			flagValue = "退回";
		
		Ext.showConfirm('确定'+flagValue+'该借阅申请吗？', function(btn) {
			if (btn == 'yes') {
				
				var datainfo = managerDashboard.dealtData[index];
				var map = new HashMap();
				// manager:管理员；employee:员工
				map.put("opt", managerDashboard.roleType);
				map.put("flag", ",borwApp,");
				map.put("nbase", datainfo.nbase);
				map.put("A0100", datainfo.A0100);
				map.put("borrowDate", datainfo.borrowDate);
				map.put("returnDate", datainfo.returnDate);
				map.put("borrowCause", datainfo.borrowCause);
				map.put("approveFlag", flag);
				map.put("appOpinValue", appOpinValue);	
				Rpc({functionId:'CF01030001',success:function(form){
					
					var result = Ext.decode(form.responseText);
					var dealtWin = Ext.getCmp('borrowWinId');
					if(dealtWin)
						dealtWin.close();
					if(result.succeed){
						managerDashboard.dealtData = result.dealtData;
						Ext.getCmp('dealtPanelid').setHtml(managerDashboard.getBorrowDealtPanel());
						// 待办window窗口
						if(Ext.getCmp('allDealtPanelid'))
							Ext.getCmp('allDealtPanelid').setHtml(managerDashboard.getAllDealtDatas());
					}
					
					managerDashboard.clickBution(managerDashboard.flag);
				},scope:this},map);
			}
		}, this);
		
	},
	//替换字符
 	getReplaceStr:function(content){
 	
 		content=replaceAll(content,"＜","<");
        content=replaceAll(content,"＞",">");
        content=replaceAll(content,"＇","'");
        content=replaceAll(content,"＂",'"');
        content=replaceAll(content,"；",";");
        
        return content;
 	}
	
});