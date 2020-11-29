
/**
 * 问卷调查图表分析
 */
Ext.define("QuestionnaireAnalysis.ChartAnalysis",{
	extend:'Ext.panel.Panel',
	xtype:'chartanalysis',
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	qnId:undefined,
	planId:undefined,
	subObject:undefined,
	imagePrefix:rootPath+'/module/system/questionnaire/',
	border:0,
	layout:{
		type:'vbox',
		align:'stretch'
	},
	overflowY:'auto',
	bodyStyle:'border-top:1px solid #c5c5c5 !important;',
	
	quesIndex:0,
	initComponent:function(){
		this.callParent();
		this.initData();
	},
	/**
	 * 加载数据
	 */
	initData:function(){
		var vo = new HashMap();
		vo.put("qnid", this.qnId);
		vo.put("planid", this.planId);
		vo.put("subobject", this.subObject?this.subObject:"");
		Rpc({functionId:'QN60000002',scope:this,success:function(res){
			var resultObj = Ext.decode(res.responseText);
			var results = eval("("+resultObj.results+")");
			this.createChartAnalysis(results);
		}},vo);
	},
	/**
	 * 开始创建图表分析页面
	 */
	createChartAnalysis:function(objs){
		var me = this;
		//var list = new Array();
		var toolbar = Ext.widget("toolbar",{
			border:0,
    		dock:'top',
    		items:[{
				xtype:'button',
				text:QN.template.analysisAllExport,
				scope:this,
				handler:function(){
					this.exportData(0,'all');
				}
			}]
    	});
    	
    	me.addDocked(toolbar);
    	
    	var doPage = false;
    	this.questionObjs = objs;
    	if(objs.length>10){
    	    doPage = true;
    	}
		var list = me.getQuestionCmpList();
		if(doPage){
		   var page =  Ext.widget('container',{
		        items:list,
		        pageIndex:1
		    });
		    me.add(page);
		    me.addDocked({xtype:'toolbar',style:'border:none',dock:'bottom',items:['->',{text:'上页',disabled:true,handler:me.prePage,scope:me},{xtype:'box',width:30},{text:'下页',handler:me.nextPage,scope:me},'->']});
		    return;
		}
		me.add(list);
		//验证 类型是 矩阵单选题、矩阵多选题和矩阵量表题 21390  wangb 201705178
		for(var i=0 ; i<objs.length ; i++){
			//序号数量超过10条 在页面就隐藏 图形类型和显示设置按钮 并且隐藏统计图 21390  wangb 201705178
			if(objs[i].tabledata !=null && objs[i].tabledata.length>10 && (objs[i].typekind==7 || objs[i].typekind==8 || objs[i].typekind==15 )){
				me.items.items[i].dockedItems.items[0].items.items[3].setHidden(true);//隐藏 图形类型按钮 21390  wangb 201705178
				me.items.items[i].dockedItems.items[0].items.items[4].setHidden(true);//隐藏 显示设置按钮 21390  wangb 201705178
				me.items.items[i].dockedItems.items[0].items.items[4].menu.items.items[0].setChecked(true);// 隐藏统计图 复选框选中 21390  wangb 201705178
				me.showOrHideChart(i+1,me.items.items[i].dockedItems.items[0].items.items[4].menu.items.items[0]);// 隐藏统计图 21390  wangb 201705178
			}
		}
	},
	
	prePage:function(button){
		 button.ownerCt.items.items[3].setDisabled(false);
	     var currentPage = this.child('container[hidden=false][pageIndex]');
	     currentPage.setVisible(false);
	     var prePageIndex = currentPage.pageIndex-1;
	     var prePage = this.child('container[hidden=true][pageIndex='+prePageIndex+']');
	     prePage.setVisible(true);
	     if(prePage.pageIndex==1)
	         button.setDisabled(true);
	},
	nextPage:function(button){
	     if(button)
	          button.ownerCt.items.items[1].setDisabled(false);
	     var currentPage = this.child('container[hidden=false][pageIndex]');
	     currentPage.setVisible(false);
	     var nextPageIndex = currentPage.pageIndex+1;
	     var nextPage = this.child('container[hidden=true][pageIndex='+nextPageIndex+']');
	     if(nextPage){
	          nextPage.setVisible(true);
	          if(button && nextPage.lastPage)
	          	button.setDisabled(true);
	          return nextPage;
	     }
	     
	     var list = this.getQuestionCmpList();
	     var page =  Ext.widget('container',{
		        items:list,
		        pageIndex:currentPage.pageIndex+1
		    });
		    if(this.quesIndex==this.questionObjs.length){
		        page.lastPage = true;
		        if(button)
		        	button.setDisabled(true);
		    }
		    this.add(page);
		    return page;
	},
	getQuestionCmpList:function(){
           var list = new Array();
           for (; this.quesIndex < this.questionObjs.length; this.quesIndex++) {
				var obj = this.questionObjs[this.quesIndex];
				var panel = undefined;
				if(obj.typekind=="1"||obj.typekind=="2"||obj.typekind=="5"
					||obj.typekind=="6"||obj.typekind=="12"||obj.typekind=="13"){
					panel = this.createSelectAnalysis(obj,this.quesIndex+1);
				} else if(obj.typekind=="3"||obj.typekind=="4"){
					panel = this.createFillAnalysis(obj,this.quesIndex+1);
				} else if(obj.typekind=="7"||obj.typekind=="8"||obj.typekind=="14"||obj.typekind=="15"){
					panel = this.createMatrixAnalysis(obj, this.quesIndex+1);
				}
				list.push(panel);
				if((this.quesIndex+1)%10==0){
				      this.quesIndex++;
				      break;
				}
			}
			return list;
	},
	/**
	 * 生成（图片）单（多）选题、打分题、量表题图表分析
	 * @param obj
	 * @param index当前为第几道题
	 */
	createSelectAnalysis:function(obj,index){
		var me = this,
		textField = new Array(),
		valueField = new Array(),
		panel = new Object();
		
		me.dataStore = Ext.create("Ext.data.Store",{
			fields:obj.allfield,
			data:obj.data
		});
		
		var table = this.createTable(obj.data, obj.typekind, null, obj.itemid);
		var chart = undefined;
		var panel = Ext.create("Ext.panel.Panel",{
			id:'questionnaire_panel_'+index,
			width:'80%',
			border:1,
			margin:'5 100 5 100',
			columnChart:chart,
			piechartdata:obj.data,
			table:table,
			chartType:'column',//统计图类型column、line、pie
			showChart:true,//是否显示统计图
			showType:'data',//=data提示显示数据，=percentage显示百分比
			layout:{
				type:'vbox',
				align:'center'
			},
			defaults:{
				margin:'0 0 10 0'
			},//
			items:[{
					xtype:'component',
					id:'chartPanel'+index,
					//flex:10,
					style:'text-align:center;border:2px solid red;',
					padding:'0 10 0 10',
					margin:'5 0 5 0',
					width:'99%',
					height:300,
					border:false,
					//html:'无数据',
					listeners : {
						'afterrender' : function() {
							me.createChart(obj.data,index,false,obj.maximum,panel.chartType,obj.name);
						}
					}
				},table]
		});
		
	/*	if(obj.typekind=="5"||obj.typekind=="6")
			chart = this.createChart(obj.data,index,true,obj.maximum);
		else
			chart = this.createChart(obj.data,index,false,obj.maximum);*/
		var toolbar = this.createMenu(obj.name, obj.typekind, index,obj.countsize);
		
		panel.addDocked(toolbar);
		
		return panel;
	},
	/**
	 * 生成（多项）填空题图表分析
	 * @param obj
	 * @param index
	 * @returns
	 */
	createFillAnalysis:function(obj,index){
		var table = this.createTable(null, obj.typekind, obj.column, obj.itemid);
		var panel = Ext.create("Ext.panel.Panel",{
			id:'questionnaire_panel_'+index,
			width:'80%',
			table:table,
			margin:'5 100 5 100',
			bodyPadding:'10 0 10 0',
			border:1,
			layout:{
				type:'vbox',
				align:'center'
			},
			items:[table]
		});
		var toolbar = this.createMenu(obj.name, obj.typekind, index,obj.countsize);
		panel.addDocked(toolbar);
		return panel;
	},
	/**
	 * 生成矩阵题图表分析
	 * @param obj
	 * @param index
	 * @returns
	 */
	createMatrixAnalysis:function(obj,index){
		var me = this;
		//var chart = this.createMatrixChart(obj.chartfield,obj.allfield,obj.chartdata,index,obj.maximum);
		me.dataStore = Ext.create("Ext.data.Store",{
			fields:obj.allfield,
			data:obj.data
		});
		var chart = undefined;
		var table = this.createTable(obj.tabledata, obj.typekind, obj.tablefield, obj.itemid);
		var panel = Ext.create("Ext.panel.Panel",{
			id:'questionnaire_panel_'+index,
			width:'80%',
			margin:'5 100 5 100',
			columnChart:chart,
			piechartdata:obj.chartdata,
			table:table,
			chartType:'column',//统计图类型column、line、pie
			showChart:true,//是否显示统计图
			showType:'data',//=data提示显示数据，=percentage显示百分比
			border:1,
			layout:{
				type:'vbox',
				align:'center'
			},
			defaults:{
				margin:'10 0 10 0'
			},
			items:[{
					xtype:'component',
					id:'chartPanel'+index,
					//flex:10,
					style:'text-align:center;border:2px solid red;',
					padding:'0 10 0 10',
					margin:'5 0 5 0',
					width:'99%',
					height:350,
					border:false,
					//html:'无数据',
					listeners : {
						'afterrender' : function() {
							me.createGroupChart(obj.chartdata,obj.chartfield,index,false,obj.maximum,panel.chartType,obj.name);
						}
					}
				},table]
		});
		var toolbar = this.createMenu(obj.name, obj.typekind, index,obj.countsize);
		panel.addDocked(toolbar);
		return panel;
	},
	/**
	 * 创建顶部菜单
	 * @param name 标题
	 * @param index 题目序号
	 * @param type 题目类型
	 */
	createMenu:function(name,type,index,countsize){
		var me = this;
		var tools = [{
			xtype:'box',width:35,height:35,border:false,
			style:'background:url('+me.imagePrefix+'images/backtitleid.png) no-repeat center;text-align:center;'+
					'vertical-align:middle;',
			html:'<font style="color:white;font-size:15px;line-height:35px;">Q'+index+'</font>'
		},{
			xtype:'label',text:name,margin:'0 10 0 10',flex:4
		},{
				xtype:'label',
				width:100,
				html:QN.template.questionCountSize+'<font style="color:green;">'+countsize+'</font>'
			}];
		if(type=="1"||type=="2"||type=="5"||type=="6"||type=="13"){
			tools.push({
				xtype:'button',text:QN.template.analysisType,
				menu:{
					defaults:{
						checked:false,
						group:'item'+index,
						checkedCls:'x-menu-item-checked',
						groupCls:'x-menu-item-unchecked'
					},
					items:[{text:QN.template.analysisColumn,checked:true,
								handler:function(){me.changeChartType(index,'column');}},
					       {text:QN.template.analysisLine,
								handler:function(){me.changeChartType(index,'line');}},
					       {text:QN.template.analysisPie,
								handler:function(){me.changeChartType(index,'pie');}}]
				}
			});
		} else if(type=="7"||type=="8"||type=="12"||type=="14"||type=="15"){
			tools.push({
				xtype:'button',text:QN.template.analysisType,
				menu:{
					defaults:{
						checked:false,
						group:'item'+index,
						checkedCls:'x-menu-item-checked',
						groupCls:'x-menu-item-unchecked'
					},
					items:[{text:QN.template.analysisColumn,checked:true,
								handler:function(){me.changeGroupChartType(index,'column');}},
					       {text:QN.template.analysisLine,
								handler:function(){me.changeGroupChartType(index,'line');}}]
				}
			});
		}
		if(type!="3"&&type!="4"&&type!="12"&&type!="14"){
			tools.push({
				xtype:'button',text:QN.template.analysisDisplay,
				menu:{
					defaults:{
						checked:false
					},
					items:[{text:QN.template.hideChart,
								handler:function(){me.showOrHideChart(index,this);}},
							{text:QN.template.hideTable,
								handler:function(){me.showOrHideTable(index,this);}}/*,
							{text:QN.template.showPercentage,
								handler:function(){me.showDataOrPercentage(index,this);}}*/]
				}
			});
		}
		if(type=="12"||type=="14"){//矩阵 打分题不需要设置百分比，只要设置值 changxy 20160711
		  tools.push({
                xtype:'button',text:QN.template.analysisDisplay,
                menu:{
                	defaults:{ // 复选框不显示问题 bug 39732  wangb 20180820
						checked:false
					},
                    items:[{text:QN.template.hideChart,
                                handler:function(){me.showOrHideChart(index,this);}},
                            {text:QN.template.hideTable,
                                handler:function(){me.showOrHideTable(index,this);}}
                            ]
                }
            });
		}
		tools.push({xtype:'button',margin:'0 5 0 0',text:QN.template.analysisExport,scope:this,handler:function(){this.exportData(index)}});
		var toolbar = Ext.widget("toolbar",{
			dock:'top',
			buttonAlign:'center',
			items:tools
		});
		return toolbar;
	},
	
	/**
	*生成矩阵题分组统计图
	*
	*/
	createGroupChart:function(data,field,index,flag,maximum,type,name){
		var me = this,chart = undefined;
		//if(index == 8) debugger
		var panel = this.queryById('questionnaire_panel_'+index);
		var vo = new HashMap();
		vo.put('data',data);
		vo.put('field',field);
		vo.put('type',type);
		vo.put('group',"1");
		vo.put('showpercent',panel.showType);
		vo.put('name',name);
		Rpc({functionId:'QN60000006',async:true,success:function(res){
			var me=this;
			var resultObj = Ext.decode(res.responseText);
			var dataXml = resultObj.dataHtml;
    		if(dataXml)
    			dataXml = dataXml.replace(/option =/,'').replace(/(^\s*)|(\s*$)/g,"");
    		if(dataXml.length > 0)
    			dataXml = dataXml.substring(0,dataXml.length-1);
			if(!this.chart){
				var charset = Ext.Loader.config.scriptCharset;
				Ext.Loader.config.scriptCharset="UTF-8";
	    	    Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:function(){
	    	    	me.drawChart(index,dataXml);
	    	    },scope:me});
	    	    Ext.Loader.config.scriptCharset=charset;		
			}
	    	else
	    		me.drawChart(index,dataXml);
		},scope:this}, vo);		
	},
	/**
	 * 生成非矩阵题统计图
	 * @param data
	 * @param index
	 * @param flag =true 为图片单（多）选题
	 * @param maximum y轴最大值
	 * @returns
	 */
	createChart:function(data,index,flag,maximum,type,name){
		var me = this,chart = undefined;
		//if(index == 8) debugger
		var panel = this.queryById('questionnaire_panel_'+index);
		var vo = new HashMap();
		vo.put('data',data);
		vo.put('type',type);
		vo.put('showpercent',panel.showType);
		vo.put('name',name);
		Rpc({functionId:'QN60000006',async:true,success:function(res){
			var me=this;
			var resultObj = Ext.decode(res.responseText);
			var dataXml = resultObj.dataHtml;
    		if(dataXml)
    			dataXml = dataXml.replace(/option =/,'').replace(/(^\s*)|(\s*$)/g,"");
    		if(dataXml.length > 0)
    			dataXml = dataXml.substring(0,dataXml.length-1);
			if(!this.chart){
				var charset = Ext.Loader.config.scriptCharset;
				Ext.Loader.config.scriptCharset="UTF-8";
	    	    Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:function(){
	    	    	me.drawChart(index,dataXml);
	    	    },scope:me});
	    	    Ext.Loader.config.scriptCharset=charset;		
			}
	    	else
	    		me.drawChart(index,dataXml);
		},scope:this}, vo);		
	},
    drawChart:function(index,dataXml){
		this.chart  = echarts.init(Ext.getDom('chartPanel'+index));
		this.chart.setOption(Ext.decode(dataXml));
    },
	/**
	 * 矩阵类型统计图
	 * @param chartfield 统计图fields
	 * @param datafield dataStore fields
	 * @param data
	 * @param index 第几道题
	 * @param maximum y轴最大值
	 * @returns
	 */
	createMatrixChart:function(chartfield,allfield,data,index,maximum){
		var me = this,
			textField = new Array(),
			valueField = new Array();
		me.dataStore = Ext.create("Ext.data.Store",{
			fields:allfield,
			data:data
		});
		
		var rotationflag=false;
        for(var i=0;i<data.length;i++){//取data中matrixname长度大于5的倾斜 changxy 20160818
              if(data[i].matrixname.length>5){
                        rotationflag=true;
                        break;
              	}
        }
		for ( var i = 0; i < chartfield.length; i++) {
			valueField.push(chartfield[i].value);
			textField.push(chartfield[i].text);
		}
		var chart = Ext.create("Ext.chart.CartesianChart",{
			//width:valueField.length*data.length*100,
			width:'90%',
			maxWidth:(me.getWidth()-200)*0.9,
			//minWidth:100,
			height:300,
			animation:true,//动画
			store:me.dataStore,
			textField:textField,
			/*legend:{
				docked:'bottom'
			},
			insetPadding:{
				left:20,
				right:20
			},*/
			axes:[{
				type:'numeric',
				position:'left',
				fields:valueField,
				grid:true,
				minimum:0,
				maximum:maximum
			},{
				type:'category',
				position:'bottom',
				//文字旋转
				label:{
					font:'12px 微软雅黑',
					rotation:rotationflag?-45:0
				},renderer:function(axis,label,layoutContext,lastLabel){//changxy 20160818 生成统计图选项折行
                    if(label.length>10){
                    	if(label.length>20)
                            label=label.substring(0,10)+'\n'+label.substring(11,20)+"...";
                        else
                            label=label.substring(0,10)+'\n'+label.substring(11,label.length);
                    }
                    return label;
                },
				fields:['matrixname']
			}],
			series:[{
				type:'bar',
				highlight:true,
				tooltip:{
					trackMouse:true,
					shadow:false,
					bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
					renderer:function(tip,record,ctx){
						var num = -1;
						for ( var i = 0; i < valueField.length; i++) {
							if(valueField[i]==ctx.field){
								num = i;
								break;
							}
						}
						var showType = Ext.getCmp('questionnaire_panel_'+index).showType;
						if(showType=="data"){
							tip.update(textField[num]+"<br>值:"+record.get(ctx.field));
						} else {
							var total = 0;
							for(rec in data){
								var obj=data[rec];
                                total += obj[valueField[num]];  //changxy  20160709 //valueField[num]==["Q19_1", "Q19_2"]
                                
                            };
							tip.update(textField[num]+"<br>占比:"+Math.round(record.get(ctx.field)/total*100)+"%");
						}
					}
				},
				style:{
	                opacity:0.80
	            },
				stacked:false,//图例是否叠加在一个上
				label:{
					display:'outside',//显示位置
					field:valueField,
					orientation:'horizontal',//horizontal水平  vertical垂直
					color:'#333'
				},
				xField:'matrixname',
				yField:valueField,
				title:textField
			}]
		});
		return chart;
	},
	/**
	 * 生成表格
	 * @param data 数据
	 * @param type 题目类型
	 * @param col 字段
	 * @param itemid
	 * @returns
	 */
	createTable:function(data,type,col,itemid){
		var me = this,
			columns = new Array(),
			tableWidth = '90%',
			maxWidth = me.getWidth(),
			bbar;
		
		if(type=="1"||type=="2"||type=="13"){
			//tableWidth = '90%';
			columns = [{text:QN.template.selectName,width:200,dataIndex:'dataname',align:'center',menuDisabled:true,sortable:false},
			           {text:QN.template.selectValue,width:200,dataIndex:'datavalue',align:'center',menuDisabled:true,sortable:false}];
		} else if(type=="12"){
			var fields = ['max','min','avg'], datas = {};
			for ( var i = 0; i < data.length; i++) {
				if(data[i].dataname==QN.template.maxScore){
					datas.max = data[i].datavalue;
				} else if(data[i].dataname==QN.template.minScore){
					datas.min = data[i].datavalue;
				} else {
					datas.avg = data[i].datavalue;
				}
			}
			this.dataStore = Ext.create("Ext.data.Store",{
				fields:fields,
				data:[datas]
			});
			//tableWidth = '90%';
			columns = [{text:QN.template.maxScore,width:200,dataIndex:'max',align:'center',menuDisabled:true,sortable:false},
			           {text:QN.template.minScore,width:200,dataIndex:'min',align:'center',menuDisabled:true,sortable:false},
			           {text:QN.template.avgScore,width:200,dataIndex:'avg',align:'center',menuDisabled:true,sortable:false}];
		} else if(type=="3"||type=="4"){
			var fields = [];
			//if(col.length>2)
			//	tableWidth = col.length*200;
			//else
				//tableWidth = '90%';
			for ( var i = 0; i < col.length; i++) {
				if(type!="3"){
					fields.push(col[i].value);
					columns.push({text:col[i].text,tooltipType:'title',tooltip:col[i].text,width:200,dataIndex:col[i].value,align:'center',menuDisabled:true,sortable:false});
				} else {
					fields.push(col[i]);
					columns.push({text:QN.template.writeTitle,width:500,dataIndex:col[i],align:'center',menuDisabled:true,sortable:false});
				}
			}
			this.dataStore = Ext.create("Ext.data.Store",{
				fields:fields,
				pageSize:5,
				proxy:{
					type:'transaction',
					functionId:'QN60000005',
					extraParams:{
						qnid:me.qnId,
						itemid:itemid,
						subobject:me.subObject?me.subObject:"",
						type:type
					},
					reader:{
						type:'json',
						root:'dataobj',
						totalProperty:'totalCount'
					}
				},
				autoLoad:true
			});
			bbar = {
				xtype:'pagingtoolbar',
				hidden:true,
				store:this.dataStore,
				displayInfo:true,
				pageSize:5,
				updateInfo : function(){
			        var me = this,
			            displayItem = me.child('#displayItem'),
			            store = me.store,
			            pageData = me.getPageData(),
			            count, msg;
			
			        if (displayItem) {
			            count = store.getCount();
			            if (count === 0) {
			                msg = me.emptyMsg;
			            } else {
			                msg = Ext.String.format(
			                    me.displayMsg,
			                    pageData.fromRecord,
			                    pageData.toRecord,
			                    pageData.total
			                );
			            }
			            displayItem.setText(msg);
			        }
			        if(pageData.total>5)
			       		me.setVisible(true);
			    }
			};
		} else if(type=="5"||type=="6"){
			//tableWidth = '90%';
			columns = [{text:QN.template.selectName,width:200,xtype:'templatecolumn',tpl:'{imgurl}',align:'center',menuDisabled:true,sortable:false},
			           {text:QN.template.selectValue,width:200,dataIndex:'datavalue',align:'center',menuDisabled:true,sortable:false}];
		} else if(type=="7"||type=="8"||type=="14"||type=="15"){
			var tabcol = new Array();
			for ( var i = 0; i < col.length; i++) {
				columns.push({text:col[i].text,tooltipType:'title',tooltip:col[i].text,width:200,dataIndex:col[i].value,align:'center',menuDisabled:true,sortable:false});
				tabcol.push(col[i].value);
			}
			//tableWidth = '90%';
			this.dataStore = Ext.create("Ext.data.Store",{
				fields:tabcol,
				data:data
			});
		}
		var table = Ext.create("Ext.grid.Panel",{
			store:this.dataStore,
			autoScroll:true,
			columnLines:true,
			columns:columns,
			width:tableWidth,
			minHeight:100,
			maxWidth:maxWidth*0.9,
			enableHdMenu:false,//是否显示表头的上下文菜单
			enableColumnHide:false,//是否允许通过表头的上下文菜单隐藏列
			enableColumnMove:false,//是否允许拖放列
			typeKind:type,
			itemid:itemid,
			listeners:{
				afterrender:function(a){
					var view = a.getView();
					Ext.create('Ext.tip.ToolTip', {
					    target:view.el,
					    delegate:'td',
					    trackMouse:true,
					    renderTo:Ext.getBody(),
					    bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
					    listeners:{
					        beforeshow:function updateTipBody(tip) {
					        	var div = tip.triggerElement.childNodes[0];
					        	if (Ext.isEmpty(div))
					        		return false;
					        	tip.update("<div style='WORD-BREAK:break-all;'>"+div.innerHTML+"</div>");
					        }
					    }
					});
				}
			},
			bbar:bbar
		});
		return table;
	},
	/**
	*矩阵图 切换 分组柱状图或分组折线图  wangb 20180816
	*/
	changeGroupChartType:function(index,charttype){
		var me = this/*,chart = undefined*/;
		var panel = Ext.getCmp('questionnaire_panel_'+index);
		this.chart =  echarts.init(Ext.getDom('chartPanel'+index));
		if(this.chart)
    		this.chart.dispose();
    	var obj = this.questionObjs[index-1];
    	var chartdata = obj.chartdata;
    	var chartfield = obj.chartfield;
		var vo = new HashMap();
		vo.put('showpercent',panel.showType);
		if(chartdata){
			vo.put('data',chartdata);
			vo.put('field',chartfield);
			vo.put('group','1');
		}else{
			vo.put('data',obj.data);
		}
		vo.put('type',charttype);
		vo.put('name',obj.name);
		Rpc({functionId:'QN60000006',async:false,success:function(res){
			var me=this;
			var resultObj = Ext.decode(res.responseText);
			var dataXml = resultObj.dataHtml;
    		if(dataXml)
    			dataXml = dataXml.replace(/option =/,'').replace(/(^\s*)|(\s*$)/g,"");
    		if(dataXml.length > 0)
    			dataXml = dataXml.substring(0,dataXml.length-1);
			if(!this.chart){
				var charset = Ext.Loader.config.scriptCharset;
				Ext.Loader.config.scriptCharset="UTF-8";
	    	    Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:function(){
	    	    	me.drawChart(index,dataXml);
	    	    },scope:me});
	    	    Ext.Loader.config.scriptCharset=charset;		
			}
	    	else
	    		me.drawChart(index,dataXml);
		},scope:this}, vo);	
		
		var chartType = panel.chartType;
		panel.chartType = charttype;
	},
	/**
	 * 根据传入的图表类型参数 加载不同的图表
	 * @param index
	 * @param type
	 */
	changeChartType:function(index,charttype){
		var me = this/*,chart = undefined*/;
		var panel = Ext.getCmp('questionnaire_panel_'+index);
		this.chart =  echarts.init(Ext.getDom('chartPanel'+index));
		if(this.chart)
    		this.chart.dispose();
    	var obj = this.questionObjs[index-1];
    	var data = obj.data;
		var vo = new HashMap();
		vo.put('showpercent',panel.showType);
		vo.put('data',data);
		vo.put('type',charttype);
		vo.put('name',obj.name);
		Rpc({functionId:'QN60000006',async:false,success:function(res){
			var me=this;
			var resultObj = Ext.decode(res.responseText);
			var dataXml = resultObj.dataHtml;
    		if(dataXml)
    			dataXml = dataXml.replace(/option =/,'').replace(/(^\s*)|(\s*$)/g,"");
    		if(dataXml.length > 0)
    			dataXml = dataXml.substring(0,dataXml.length-1);
			if(!this.chart){
				var charset = Ext.Loader.config.scriptCharset;
				Ext.Loader.config.scriptCharset="UTF-8";
	    	    Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:function(){
	    	    	me.drawChart(index,dataXml);
	    	    },scope:me});
	    	    Ext.Loader.config.scriptCharset=charset;		
			}
	    	else
	    		me.drawChart(index,dataXml);
		},scope:this}, vo);	
		
		var chartType = panel.chartType;
		panel.chartType = charttype;	
	},
	/**
	 * 生成折线图
	 * @param width 宽度
	 * @param yfield y轴字段
	 * @param dataStore 数据
	 * @param index
	 * @param maximum 最大分数
	 * @param textField tooltip显示的字段
	 * @returns
	 */
	createLineChart:function(yfield, dataStore, index, maximum, textField){
		var series = new Array(),
			size = dataStore.getCount(),
			xfield;
		if(yfield instanceof Array){//y轴为array，为矩阵类型的折线图
			xfield = 'matrixname';
			for ( var i = 0; i < yfield.length; i++) {
				series.push({type:'line',marker:{type:'circle',fx:{duration:200,easing:'backOut'}},tooltip:{
					trackMouse:true,
					shadow:false,
					bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
					renderer:function(tip, record, ctx){
						var showType = Ext.getCmp('questionnaire_panel_'+index).showType,
							num = -1;
						for ( var i = 0; i < yfield.length; i++) {
							if(yfield[i]==ctx.field){
								num = i;
								break;
							}
						}
						if(showType=="data"){
							tip.update(textField[num]+"<br>值:"+record.get(ctx.field));
						} else {
							var total = 0;
							dataStore.each(function(rec){
								total +=rec.get(ctx.field); //rec.get('datavalue'); 矩阵打分题是当前行某一项的选择次数占当前行的总选择次数 changxy
							});
							tip.update(textField[num]+"<br>占比:"+Math.round(record.get(ctx.field)/total*100)+"%");
						}
					}
				},xField:xfield,yField:yfield[i],title:textField[i]});
			}
		} else {
			xfield = 'dataname';
			series.push({type:'line',marker:{type:'circle',fx:{duration:200,easing:'backOut'}},tooltip:{
				trackMouse:true,
				shadow:false,
				bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
				renderer:function(tip, record, ctx){
					var showType = Ext.getCmp('questionnaire_panel_'+index).showType;
					if(showType=="data"){
						tip.update(record.get(xfield)+"<br>值:"+record.get(yfield));
					} else {
						var total = 0;
						dataStore.each(function(rec){
							total += rec.get('datavalue');
						});
						tip.update(record.get(xfield)+"<br>占比:"+Math.round(record.get(yfield)/total*100)+"%");
					}
				}
			},xField:xfield,yField:yfield,title:textField});
		}
		
		var rotationflag=false;
        for(var i=0;i<dataStore.getData().items.length;i++){//dataname大于5则折行  changxy 20160818
        	//上面的判断都说了 有两种情况   matrixname 和dataname,这里判断需要兼容下 add by xiegh on date 20180313 bug35392
            if((dataStore.getData().items[i].data.matrixname && dataStore.getData().items[i].data.matrixname.length>5) || (dataStore.getData().items[i].data.dataname && dataStore.getData().items[i].data.dataname.length)){//没有dataname属性 换成没有matrixname属性 29348 wangb 20170703 
                rotationflag=true;
                break;
            }
        }
		var lineChart = Ext.create("Ext.chart.CartesianChart",{
			width:'90%',
			height:300,
			animation:true,//动画
			store:dataStore,
			insetPadding:{
				top:10,
				bottom:10,
				left:20,
				right:30
			},
			axes:[{
				type:'numeric',
				position:'left',
				fields:yfield,
				grid:true,
				minimum:0,
				maximum:maximum
			},{
				type:'category',
				position:'bottom',
				label:{
					font:'12px 微软雅黑',
                    rotation:rotationflag?-45:0
                },
				renderer:function(axis,label,layoutContext,lastLabel){//changxy 20160818 生成统计图选项折行  超出20字后面省略
					if(label.length>10){
						if(label.length>20)
					       label=label.substring(0,10)+'\n'+label.substring(11,20)+"...";
					    else
					       label=label.substring(0,10)+'\n'+label.substring(11,label.length);
                    }
                    return label;
                },
				fields:xfield
			}],
			series:series
		});
		return lineChart;
	},
	/**
	 * 生成饼图
	 * @param store
	 */
	createPieChart:function(data,index){
		var arrayData = new Array();
		for(var i in data){
			if(data[i].datavalue!="0")
				arrayData.splice(0,0,data[i]);
			else
				arrayData.push(data[i]);
		}
		var store = Ext.create("Ext.data.Store",{
			fields:['dataname','datavalue'],
			data:arrayData
		});
		var pieChart = Ext.create("Ext.chart.PolarChart",{
			width:'90%',
			height:400,
			insetPadding:5,
			innerPadding:5,
			animation:true,
			store:store,
			interactions:['rotate'],
			series:[{
				type:'pie',
				angleField:'datavalue',
				tooltip:{
					trackMouse:true,
					shadow:false,
					bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
					renderer:function(tip, record){
					   var showType = Ext.getCmp('questionnaire_panel_'+index).showType;
					   if(showType=="data"){
					   
						    tip.update(record.get("dataname")+"<br>值:"+record.get("datavalue"));
					   } else {
							var total = 0;
							store.each(function(rec){
								total += rec.get('datavalue');
							});
							tip.update(record.get('dataname')+"<br>占比:"+Math.round(record.get('datavalue')/total*100)+"%");
						}
					}
				},/**/
				highlight:true,
				label:{
					field:'dataname',
					orientation :'vertical',
					calloutLine:{
						length:40,
						width:2
					},
					renderer:function(text){ //饼状图内容折行 changxy
                    if(text.length>10){
                    	if(text.length>20)
                         text=text.substring(0,10)+'\n'+text.substring(11,20)+"...";
                        else
                         text=text.substring(0,10)+'\n'+text.substring(11,text.length);
                    }
                    return text;
                    }
				}
			}]
		});
		return pieChart;
	},
	/**
	 * 导出数据
	 * @param index
	 * @param type
	 */
	exportData:function(index,type){
		var objs = new Array();
		if(type=="all"){
			//var arr = this.items.items;
			var currentPageIndex = this.child('container[hidden=false][pageIndex]');
			/*
			var lastPage;
		    if(this.quesIndex!=this.questionObjs.length){
		          while(true){
		              var page = this.nextPage();
		              if(page.lastPage){
		                    break;
		              }
		              lastPage=page;
		          }
		    }*/
		   var arr = this.query('panel[id^=questionnaire_panel_]');
			for ( var i = 1; i <= arr.length; i++) {
			    var arrIndex = arr[i-1].id.replace('questionnaire_panel_','');//下标获取不对 wangb 20180817 bug 39695
				objs.push(this.getTableData(arrIndex));
			}
			/*
			if(lastPage){
			     lastPage.setVisible(false);
			     currentPageIndex.setVisible(true);
			}*/
		} else {
			objs.push(this.getTableData(index));
		}
		var vo = new HashMap();
		vo.put("data", objs);
		Rpc({functionId:'QN60000003',success:function(res){
			var resultObj = Ext.decode(res.responseText);
			var name = resultObj.filename;
			//xus 20/3/2 vfs改造
//			window.open("/servlet/DisplayOleContent?filename="+name,"_self");
			window.open("/servlet/vfsservlet?fileid="+name+"&fromjavafolder=true");
		}}, vo);
	},
	/**
	 * 获取表格的数据
	 * @param index
	 * @returns {___obj0}
	 */
	getTableData:function(index){	
		var obj = new Object(),
			panel = this.queryById('questionnaire_panel_'+index),			
			chartType = panel.chartType,
			chart;			
		if(chartType=="column")
			chart = panel.columnChart;
		else if(chartType=="line")
			chart = panel.lineChart;
		else if(chartType=="pie")
			chart = panel.pieChart;
		//if(chart)
		//	obj.stream = chart.getImage('stream');
			obj.chartType=chartType;//添加导出报表类型 changxy
		obj.name = 'Q'+index+ panel.getDockedItems('toolbar[dock="top"]')[0].items.items[1].text;//设置表格标题号 changxy
		var table = panel.table;
		obj.type = table.typeKind;
		obj.itemid = table.itemid;
		obj.qnid = this.qnId;
		var columns = table.columns;
		var col = new Array();
		var order = new Array();
		for ( var i = 0; i < columns.length; i++) {
			col.push(columns[i].text);
			order.push(columns[i].dataIndex==null?"":columns[i].dataIndex);
		}
		obj.column = col;
		obj.order = order;
		var arr = new Array();
		var datas = table.getStore().getRange();
		for ( var i = 0; i < datas.length; i++) {
			arr.push(datas[i].getData());
		}
		obj.data = arr;
		return obj;	
	},
	/**
	 * 显示或隐藏统计图
	 * @param index
	 * @param menu
	 */
	showOrHideChart:function(index,menu){
		var text = menu.text;
		var panel = this.queryById('questionnaire_panel_'+index);
		var chartType = panel.chartType;
		var button = panel.getDockedItems('toolbar[dock="top"]')[0].items.items[2];//图片类型按钮
		var panelObj = Ext.getCmp('chartPanel'+index);
		if(!menu.checked){
			panelObj.setHidden(false);//hide();
			panel.showChart = true;
			button.setDisabled(false);
		} else {
			panelObj.setHidden(true);
			panel.showChart = false;
			button.setDisabled(true);
		}
	},
	/**
	 * 显示或隐藏表格
	 * @param index
	 * @param menu
	 */
	showOrHideTable:function(index,menu){
		var text = menu.text;
		var panel = this.queryById('questionnaire_panel_'+index);
		if(/*text==QN.template.showTable*/ !menu.checked){
			//menu.setText(QN.template.hideTable);
			if(panel.showChart)
				panel.insert(1,panel.table);
			else
				panel.insert(0,panel.table);
		} else {
			//menu.setText(QN.template.showTable);
			panel.remove(panel.table,false);
		}
	},
	/**
	 * 显示数据或百分比
	 * @param index
	 * @param menu
	 */
	showDataOrPercentage:function(index,menu){
		var text = menu.text,me = this;
		var panel = this.queryById('questionnaire_panel_'+index);
		if(!menu.checked){
			panel.showType = "data";
		} else {
			panel.showType = "percentage";
		}
		
		
		this.chart =  echarts.init(Ext.getDom('chartPanel'+index));
		if(this.chart)
    		this.chart.dispose();
    	var obj = this.questionObjs[index-1];
    	var data = obj.data;
		var vo = new HashMap();
		vo.put('data',data);
		vo.put('type',panel.chartType);
		vo.put('name',obj.name);
		vo.put('showpercent',panel.showType);
		Rpc({functionId:'QN60000006',async:false,success:function(res){
			var me=this;
			var resultObj = Ext.decode(res.responseText);
			var dataXml = resultObj.dataHtml;
    		if(dataXml)
    			dataXml = dataXml.replace(/option =/,'').replace(/(^\s*)|(\s*$)/g,"");
    		if(dataXml.length > 0)
    			dataXml = dataXml.substring(0,dataXml.length-1);
			if(!this.chart){
				var charset = Ext.Loader.config.scriptCharset;
				Ext.Loader.config.scriptCharset="UTF-8";
	    	    Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:function(){
	    	    	me.drawChart(index,dataXml);
	    	    },scope:me});
	    	    Ext.Loader.config.scriptCharset=charset;		
			}
	    	else
	    		me.drawChart(index,dataXml);
		},scope:this}, vo);	
	}
});