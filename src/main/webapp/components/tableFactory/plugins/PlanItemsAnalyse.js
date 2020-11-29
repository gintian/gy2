Ext.define("EHR.tableFactory.plugins.PlanItemsAnalyse",{
	extend:'Ext.panel.Panel',
	requires:["EHR.extWidget.proxy.TransactionProxy","EHR.extWidget.field.CodeTreeCombox","EHR.extWidget.field.DateTimeField"],
	layout:{
		type:'vbox',
		align:'stretch'
	},
	border:1,
	autoScroll:true,
	bodyStyle:'border:none',
	
	//自定义属性
	subModuleId:undefined,
	analyseBusiId:undefined,
	columns:undefined,
	columnDescs:undefined,
	mainPanel:undefined,
	
	existItems:undefined,
	
	//代码型列
	codeItems:undefined,
	
	
	//数值型列
	numItems:undefined,
	
	components:undefined,
	contextPanel:undefined,
	contextParam:undefined,
	updateConfig:undefined,
	deleteItems:undefined,
	selectCond:undefined,
	selectedPlanValue:undefined,
	
	initComponent:function(){
	    this.callParent(arguments);
	    this.codeItems=new Array();
	    this.numItems=new Array();
	    this.components=new Array();
	    this.deleteItems=new Array();
	    this.contextPanel = {};
	    this.contextParam = {};
	    
	    this.initExsitItems();
	    this.initSelecItems();
	    Ext.define("analysePlanModel",{
			extend:'Ext.data.Model',
			fields:["name","value"],
			idProperty:'value'
		});
		Ext.define("condFactorModel",{
			extend:'Ext.data.Model'
		});
		
	    this.startCom();
	    
	},
	initExsitItems:function(){
		var me = this;
		me.existItems = new Array();
		me.existItemsMap = {};
		var values = new HashMap();
		values.put("subModuleId",me.subModuleId);
		Rpc({functionId:'ZJ100000008',success:function(reqs){
		    var req = Ext.decode(reqs.responseText);
			var statics = req.statics;
			Ext.each(statics,function(s){
				me.existItemsMap[s.value] = s.condList;
				delete s.condList;
				me.existItems.push(s);
			});
		}},values);
	},
	//获取代码型列 和 数值型列
	initSelecItems:function(){
		var column=undefined;
		var combocolumns = new Array();
		var columnInfo = {};
		for(var i=0;i<this.columns.length;i++){
			column = this.columns[i];
			if(column.fieldsetid.length<1)
				continue;
			var combocolumn = {name:column.columnDesc,itemid:column.columnId,codesetid:column.codesetId};
			columnInfo[column.columnId] = {desc:column.columnDesc,codesetid:column.codesetId,columnType:column.columnType,format:column.format,operationData:column.operationData};
			combocolumns.push(combocolumn);
			if(column.columnType=='A' && column.codesetId.length>1 && column.codesetId!='0'){
				//changxy
				this.codeItems.push({name:column.columnDesc,value:column.columnId,codesetid:column.codesetId,codesource:column.codesource,ctrltype:column.ctrltype,nmodule:column.nmodule/*,used:false*/});
				//this.codeItems[column.dataIndex] = {name:column.text,value:column.dataIndex/*,used:false*/};
			    continue;
			}
			if(column.columnType=='N'){
				this.numItems.push({name:column.columnDesc,value:column.columnId});
				continue;
			}
		}
		this.columns = combocolumns;
		this.columnDescs = columnInfo;
	},
	
	//开始
	startCom:function(){
		var me = this;
		// 创建 功能控制区 （按钮、条件选择）
		var toolbar = me.createFunctionTool();
		this.planSettingPanel = me.createPlanSettingPanel();
		
		this.addDocked(toolbar);
		this.add(this.planSettingPanel);
		/*
		this = Ext.widget("panel",{
			
			layout:{
				type:'vbox',
				align:'stretch'
			},
			border:1,
			autoScroll:true,
			bodyStyle:'border:none',
			dockedItems:toolbar,
			items:this.planSettingPanel
		});
		*/
		
	},
	
	/**
	 * 创建 按钮和条件选择 工具栏
	 * @returns
	 */
	createFunctionTool:function(){
		var me = this;
		 var toolbar  = Ext.widget('toolbar',{
			 //border:0,
			 dock: 'top',
			 items:[{
					xtype:'combo',
					fieldLabel:'方案名称',
					labelSeparator:'',
					labelWidth:60,
					width:300,
					displayField:'name',
					valueField:'value',
					store:{
						model:'analysePlanModel',
						proxy:{
		        			type: 'transaction',
		        			autoLoad:true,
		        			functionId:'ZJ100000003',
		        			extraParams:{
		        				subModuleId:me.subModuleId,
		        				searchType:'plan'
		        			},
		        			reader:{
		        				root:'plans'
		        				//idProperty:"value"
		        			}
		        		}
					},
					listeners:{
						select:function(){
							me.selectAnalysePlan(this.value);
						}
						
					}
				},{xtype:'component',width:50},{
					text:'新建',
					handler:function(){
						if(me.chartPanel){
							me.chartPanel.removeAll(true);
							me.chartPanel.collapse();
						}
						if(me.tablePanel){
							me.tablePanel.removeAll(true);
							me.tablePanel.collapse();
						}
						me.planSettingPanel.expand();	
						me.cleanAnalysePlan(true,true);
					},
					scope:me
				},{
					text:'保存',
					handler:function(){
						me.saveAnalyseConfig();
					},
					scope:me
				},{
					text:'统计',
					disabled:true,
					handler:function(){
						me.doAnalyse();
					},
					scope:me
				},{
					text:'另存',
					hidden:true,
					handler:function(){
						me.saveAnalyseAsNew();
					},
					scope:me
				},{
					text:'删除',
					handler:function(){
							me.deleteAnalysePlan();
					}
				}]
		 });
		 return toolbar;
	},
	/**
	 * 创建 统计参数设置 panel
	 * @returns
	 */
	createPlanSettingPanel:function(){
		var me = this;
		me.contextPanel.HPanel = me.create_H_or_V_Panel('H');
		me.contextPanel.VPanel = me.create_H_or_V_Panel('V');
		me.contextPanel.cPanel = me.createAnalyseTypePanel();
		var settingPanel = Ext.widget('panel',{
			title:'规则',
			//autoScroll:true,
			scrollable:'x',
			layout:{
				type:'table',
				tableAttrs:{align:'center'},
				columns:2,
				tdAttrs:{
					valign:'top'
				}
			},
			bodyStyle:"text-align:center;padding-bottom:10px;",
			//collapsible:true,//17962 不折叠直接展开  wangb 20170517
			manageHeight:true,
			items:[me.contextPanel.HPanel,me.contextPanel.cPanel,me.contextPanel.VPanel]
		});
		return settingPanel;
	},
	
	create_H_or_V_Panel:function(direction){
		var me = this;
		var title = direction=='V'?'纵向':'横向';
		return Ext.widget('panel',{
			title:title,
			width:820,
			direction:direction,
			manageHeight:true,
			minHeight:250,
			margin:10,
			level:0,
			layout:'hbox',
			bodyStyle:'border-bottom:none',
			dockedItems:{
			   xtype:'toolbar',
			   border:1,
			   dock:'bottom',
			   items:['->',{
				   xtype:'image',
				   width:16,
				   height:16,
				   title:'添加一级统计项',
				   src:'/images/add.gif',
					   listeners:{
						   render:function(){
								this.getEl().on('click',me.addItem,me,this.up('panel'));
							}
					   }
			   }]
			}
		});
	},
	
	// 创建 统计 方式 和统计项目 panel
	createAnalyseTypePanel:function(){
		var me = this;
		return Ext.widget('panel',{
			width:250,
		    height:250,
		    margin:10,
		    title:'结果',
		    items:[{
		    	xtype:'combo',
		    	fieldLabel:'统计方式',
        		labelWidth:60,
        		comFlag:'analyseType',
        		margin:'30 10 0 10',
        		 editable:false,
        		 displayField: 'name',
        		 valueField: 'value',
        		 labelAlign:'right',
        		 value:1,
        		 store:{
        			 fields:['name','value'],
        			 data:[{name:'个数',value:1},{name:'最大',value:2},{name:'最小',value:3},{name:'平均',value:4},{name:'求和',value:5}]
        		 },
        		 listeners:{
        			 select:function(){
        				 if(this.value!=1)
        				     this.nextSibling('combo').setVisible(true);
        				 else
        					 this.nextSibling('combo').setVisible(false); 
        				 
        				 me.query("toolbar>button[text=统计]")[0].setDisabled(true);
        			 }
        		 }
		    	
		    },{
		    	xtype:'combo',
		    	fieldLabel:'统计项目',
        		labelWidth:60,
        		comFlag:'itemType',
        		margin:'30 10 0 10',
        		 editable:false,
        		 hidden:true,
        		 displayField: 'name',
        		 valueField: 'value',
        		 labelAlign:'right',
        		 store:{
        			 fields:['name','value'],
        			 data:this.numItems
        		 },
        		 listeners:{
        			 select:function(){
        				 me.query("toolbar>button[text=统计]")[0].setDisabled(true);
        			 }
        		 
        		 }
		    	
		    }]
		    
		});
	},
	
	//插入条件
	addItem:function(e,E,p){
		var me = this;
		
        var contextPanel = me.contextPanel[p.direction+"Panel"];
        var width = 0;
        contextPanel.items.each(function(p){
        	width+=p.getWidth();
        });
        if(p.level==0 || p.query("panel[direction="+p.direction+"]").length>0){
        	if(width+252>800){
        		Ext.Msg.alert('提示信息','每个方向最多添加3个维度！');
    			return;
        	}
        }
		var planId = me.query("toolbar>combo")[0].getValue();
		
		var optionStr = '';
		Ext.each(me.codeItems,function(code){
			//changxy
			//optionStr+="<option value='"+code.value+"' codesetid='"+code.codesetid+"'>"+code.name+"</option>";
			optionStr+="<option value='"+code.value+"' codesetid='"+code.codesetid+"' codesource='"+code.codesource+"' ctrltype='"+code.ctrltype+"' nmodule='"+code.nmodule+"' >"+code.name+"</option>";
		});
		Ext.each(me.existItems,function(code){
			if(planId && planId.length>0 && code.isplan)
				return;
			optionStr+="<option value='"+code.value+"'>"+code.name+"</option>";
		});
		var btnHandler = function(){
			var selectEl = Ext.getDom('fieldSelector');
			var selectedItems = selectEl.selectedOptions;
			if(!selectedItems){
				selectedItems = new Array();
				if(selectEl.selectedIndex>=0){
					for(var i = selectEl.selectedIndex;i<selectEl.options.length;i++){
						if(selectEl.options[i].selected)
							selectedItems.push(selectEl.options[i]);
					}
				}
			}
			var fstPanels = new Array();
			var pItems =p.items.items.length;

			/**优化条件个数判断，条件个数限制通过是否超宽限制*/
			var addWidth = selectedItems.length*252;
			if(p.level==1 && pItems==1){
			    addWidth = addWidth-252;
            }
			if(width+addWidth>800){
                Ext.showAlert('每个方向最多添加3个维度！');
                return;
            }
			/*
			if(selectedItems.length+ pItems > 3){
				Ext.showAlert('每个方向最多添加3个维度！');
    			return;
			}
			*/
			for(var k=0;k<selectedItems.length;k++){
				var panel = {xtype:'panel',direction:p.direction,margin:10,level:p.level+1};
				panel.tools=[{type:'close',handler:
					function(){
						this.toolOwner.close();
					}
				}];
				
				var childPanel = null;
				if(selectedItems[k].value.split('_').length>1){
					childPanel = me.createItemPanel(selectedItems[k].value,me.existItemsMap[selectedItems[k].value]);
				}else{
					//changxy 增加两个列参数 ctrlcype 和 nmodule  codesource
					childPanel = me.createCodeTree(selectedItems[k].value,selectedItems[k].getAttribute("codesetid"),selectedItems[k].getAttribute("nmodule"),selectedItems[k].getAttribute("ctrltype"),selectedItems[k].getAttribute("codesource"));
				}
				
				if(p.level==0){
					panel.manageWidth=true;
				    panel.manageHeight=true;
				    panel.minHeight=230;
				    panel.minWidth=232;
					panel.layout={
							type:'table',
//							tableAttrs: {
//						        style: {
//						            width: '100%'
//						        }
//						    },

							columns:3
					};
					panel.bodyStyle='border-bottom:none';
					panel.dockedItems={
						   xtype:'toolbar',
						   border:1,
						   dock:'bottom',
						   items:['->',{
							   xtype:'image',
							   width:16,
							   height:16,
							   title:'添加二级统计项',
							   src:'/images/add.gif',
								   listeners:{
									   render:function(){
											this.getEl().on('click',me.addItem,me,this.up('panel'));
										}
								   }
						   }]
						};
				}else{
					panel.height=230;
				    panel.width=230;
					panel.layout='fit';
					childPanel.upItemid = true;
				}
				panel.title=selectedItems[k].text;
				panel.titleStr = selectedItems[k].text;
				panel.itemid=selectedItems[k].value;
				
				
				panel.items=childPanel;
				fstPanels.push(panel);
				
			}
			p.add(fstPanels);
			this.up('window').close();
			me.query("toolbar>button[text=统计]")[0].setDisabled(true);
		};
		Ext.widget('window',{
			title:'选择分析维度',
			height:300,
			width:300,
			modal:true,
			bbar:['->',{text:'确定',handler:btnHandler,style:'margin-right:20px'},{text:'取消',handler:function(){this.up('window').close();}},{ xtype: 'tbfill' }],
			style:'text-align:center',
			bodyPadding:10,
			html:'<select id="fieldSelector" style="border:1px #c5c5c5 solid;width:270px;padding:5px" multiple="multiple" size="13" >'+optionStr+'</select>'
		}).show();
	},
	
	// 创建树选择panel
	createCodeTree:function(itemid,codesetid,nmodule,ctrltype,codesource){
	    var activeColumn  = this.columnDescs[itemid];
	    	var dataStore = {};
	    	if(activeColumn.operationData){
	    	    var opd = activeColumn.operationData;
	    	    var data = [];
	    	    for(var i=0;i<opd.length;i++)
	    	       data.push({id:opd[i].dataValue,text:opt[i].dataName});
	    	    dataStore.data = data;
	    	}else{
	    	    dataStore.proxy = {
	    	    		type:'transaction',
        			functionId:'9030000003',
        			extraParams:{
        				itemid:itemid,
        				codesetid:codesetid,
        				analyseBusiId:this.analyseBusiId,
        				fromFlag:'planAnalyse',
        				autoCheck:false,
        				nmodule:nmodule,
        				ctrltype:ctrltype,
        				codesource:codesource
        			}
	    	    };
	    	}
		return {
			xtype:'treepanel',
			border:0,
			itemid:itemid,
			bodyStyle:'border-top:none',
			colspan:3,
			height:200,
			width:230,
			panelFlag:'insertPanel',
			rootVisible:false,
			store:dataStore/*{
				proxy:{
	        			type:'transaction',
	        			functionId:'9030000003',
	        			extraParams:{
	        				itemid:itemid,
	        				codesetid:codesetid,
	        				analyseBusiId:this.analyseBusiId,
	        				fromFlag:'planAnalyse',
	        				autoCheck:false,
	        				nmodule:nmodule,
	        				ctrltype:ctrltype,
	        				codesource:codesource
	        			}
        			}
			}*/
		};
	},
	createItemPanel:function(staticId,condList){
		var condPanel =  {
			xtype:'panel',panelFlag:'insertPanel',staticId:staticId,colspan:3,hidden:false,width:230,height:200,border:0,layout:'vbox',
			items:[]
		};
		
		for(var i=0;i<condList.length;i++){
			condPanel.items.push({
				xtype:'container',
				width:'100%',
				height:30,
				style:'border:1px solid #C5C5C5;line-height:30px;text-align:left',
				margin:'5 5 0 5',
				html:"&nbsp;"+condList[i]
			});
		}
		return condPanel;
	},
	//执行统计分析
	doAnalyse:function(){
		var me = this;
		
		//if(!me.getAnalyseConfig())return;
		var planId = me.query("toolbar>combo")[0].getValue();
		if(planId != me.selectedPlanValue){
			Ext.Msg.alert("提示信息","请先保存方案！");
			return;
		}
		//执行统计分析
		var values = new HashMap();
		values.put("subModuleId",me.subModuleId);
		values.put("planId",planId);
		Rpc({functionId:'ZJ100000002',success:me.analyseResult,scope:me},values);
		
		//this.planSettingPanel.collapse();
	},
	
	/**
	 * 收集 方案设置
	 * @returns {Boolean}
	 */
	getAnalyseConfig:function(){
		var me = this;
		var error = false; 
		var msg = "";
		var hitems = me.contextPanel.HPanel.query('panel[panelFlag=insertPanel]');
		var vitems = me.contextPanel.VPanel.query('panel[panelFlag=insertPanel]');
		if(hitems.length<1 && vitems.length<1){
			Ext.Msg.alert("提示信息","必须选择横向或纵向统计指标！"); //changxy 20160728
			return false;
		}
		//获取横向 统计列参数
		var h = new Array();
		Ext.each(hitems,function(item){
			if(item.staticId){
			    var obj = {itemid:item.staticId,child:new Array()};
			    if(item.upItemid){
				    	var last = h[h.length-1];
					last.child.push(obj);
			    }else
			    		h.push(obj);
				return true;
			}
			if(item.getChecked().length==0){
				error = true;
				msg = "请选择 <"+item.ownerCt.titleStr+"> 统计代码项！";
				return false;
			}
			var itemObj = {itemid:item.itemid,code:new Array(),child:new Array()};
			Ext.each(item.getChecked(),function(record){
				itemObj.code.push(record.data.id);
			});
			if(item.upItemid){
				var last = h[h.length-1];
				last.child.push(itemObj);
			}else{
				h.push(itemObj);
			}
			
		});
		
		if(error){
			Ext.Msg.alert("提示信息",msg);
			return false;
		}
		me.contextParam.h = h;
		//获取纵向 统计列参数
		var v = new Array();
		Ext.each(vitems,function(item){
			if(item.staticId){
			    var obj = {itemid:item.staticId,child:new Array()};
			    if(item.upItemid){
				    	var last = v[v.length-1];
					last.child.push(obj);
			    }else
			    		v.push(obj);
				return true;
			}
			if(item.getChecked().length==0){
				error = true;
				msg = "请选择 <"+item.ownerCt.titleStr+"> 统计代码项！";
				return false;
			}
			var itemObj = {itemid:item.itemid,code:new Array(),child:new Array()};
			Ext.each(item.getChecked(),function(record){
				itemObj.code.push(record.data.id);
			});
			if(item.upItemid){
				var last = v[v.length-1];
				last.child.push(itemObj);
			}else{
				v.push(itemObj);
			}
		});
		
		if(error){
			Ext.Msg.alert("提示信息",msg);
			return false;
		}
		
		me.contextParam.v = v;
		//获取统计参数
		var analyseType = me.contextPanel.cPanel.items.items[0].value;
		
		var itemType = me.contextPanel.cPanel.items.items[1].value;
		if(analyseType!=1 && !itemType){
			Ext.Msg.alert("提示信息","请选择统计项目！");
			return false;
		}
			
		me.contextParam.analyseType=analyseType;
		me.contextParam.itemType=itemType;
		
		return true;
	},
	
	analyseResult:function(reqs){
		var me = this;
		var req = Ext.decode(reqs.responseText);
		this.charts = req.charts;
		if(this.chartPanel){
			this.chartPanel.removeAll(true);
			this.chartPanel.expand();
			this.drawChart();
		}else{
			this.chartPanel = Ext.widget("panel",{
				id:'chartPanel',
				//collapsible:true,//17962 不折叠直接展开  wangb 20170517
				minHeihgt:400,
				manageHeight:true,
				margin:'10 0 0 0',
				title:'分析图',
				layout:{
					type:'table',
					columns:2,
					tableAttrs:{
						width:'100%'
					},
					tdAttrs:{
						align:'center',
						style:'padding:10px 10px 10px 10px;'
					}
				}
			});
			this.add(this.chartPanel);
			var charset = Ext.Loader.config.scriptCharset;
			Ext.Loader.config.scriptCharset="UTF-8";
			Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:this.drawChart,scope:this});
			Ext.Loader.config.scriptCharset=charset;		
		}
		
		//return;
		var tableHtml = req.table;
		if(this.tablePanel){
			this.tablePanel.removeAll(true);
			this.tablePanel.expand();
			this.tablePanel.add({
				xtype:'image',src:'/components/tableFactory/tableGrid-theme/images/exportExcel.jpg',
				margin:'0 10 10 0',width:40,height:40,title:'导出Excel',style:'cursor:pointer',
				listeners:{
					render:function(){
						this.getEl().on('click',function(){
							var values = new HashMap();
							values.put("subModuleId",me.subModuleId);
							Rpc({functionId:'ZJ100000006',success:me.exportExcel,scope:me},values);
						});
					}
				}
			},{border:0,html:tableHtml});
		}else{
			//var tableHtml = req.table;
			this.tablePanel = Ext.widget("panel",{
				id:'tablePanel',manageHeight:true,autoScroll:true,//collapsible:true, //17962 不折叠直接展开  wangb 20170517
				minHeight:200,bodyPadding:10,layout:'fit',margin:'10 0 0 0',minHeight:300,title:'分析表',
				layout:'vbox',
				items:[{
					xtype:'image',src:'/components/tableFactory/tableGrid-theme/images/exportExcel.jpg',
					margin:'0 10 10 0',width:40,height:40,title:'导出Excel',style:'cursor:pointer',
					listeners:{
						render:function(){
							this.getEl().on('click',function(){
								var values = new HashMap();
								values.put("subModuleId",me.subModuleId);
								Rpc({functionId:'ZJ100000006',success:me.exportExcel,scope:me},values);
							});
						}
					}
				},{
					border:0,
					html:tableHtml
				}]
			});
			this.add(this.tablePanel);
		}
		
	},
	
	exportExcel:function(reqs){
	var req = Ext.decode(reqs.responseText);
		var fileName = req.filename;
//		window.open("/servlet/DisplayOleContent?filename="+fileName);	
		//20/3/16 xus vfs改造
		window.open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true");
	},
	/**
	 * 绘图 统计分析图
	 */
	drawChart:function(){
		var me = this;
		var i=0;
		var width = 500,
			height=250,
		    colspan = 1;
		if(me.charts.length==1){
			width = 800;
			height=300;
			colspan = 2;
		}
		Ext.each(me.charts,function(data){
			me.chartPanel.add({id:i+'chart',xtype:'component',width:width,height:height,colspan:colspan});
			
			var option;
			
			if(data.length>0 && data[0].xAxisData){
			
				var dataObj = data[0];
				var legendData = dataObj.legendData;
				
				var seriesData = [];
				for(var k=0;k<legendData.length;k++){
					seriesData.push({
						name:legendData[k],
					    data:dataObj[legendData[k]],
					    barGap: 0,
					    type:'bar'
					});
				}
				
			
				option = {
				    tooltip: {
				        trigger: 'axis',
				        axisPointer: {
				            type: 'shadow'
				        }
				    },
				    legend: {
				        data: legendData
				    },
				    
				    calculable: true,
				    xAxis: [
				        {
				            type: 'category',
				            axisTick: {show: false},
				            data: dataObj.xAxisData
				        }
				    ],
				    yAxis: [
				        {
				            type: 'value'
				        }
				    ],
				    series: seriesData
				};
			
			
			}else{
				/*var legendData = [];
				for(var k=0;k<data.length;k++){
					legendData.push(data[k].name);
				}*/
			
				option = {
				    tooltip : {
				        trigger: 'item',
				        formatter: "{a} <br/>{b} : {c} ({d}%)"
				    },
				   /* legend: {
				        orient: 'vertical',
				        left: 'left',
				        data: legendData
				    },*/
				    series : [
				        {
				        		name:'',
				            type: 'pie',
				            radius : '55%',
				            center: ['50%', '60%'],
				            data:data,
				            itemStyle: {
				                emphasis: {
				                    shadowBlur: 10,
				                    shadowOffsetX: 0,
				                    shadowColor: 'rgba(0, 0, 0, 0.5)'
				                }
				            }
				        }
				    ]
				};
			}
			
			
			var myChart = echarts.init(document.getElementById(i+'chart'));
			myChart.setOption(option);
			
			/*
			var chart = new AnyChart("/anychart/swf/AnyChart.swf");
            chart.wMode='transparent';
            chart.width=width;
            chart.height=height;
            chart.setData(chartXml);
            chart.write(i+'chart');
            */
            i++;
		});
	},
	//保存统计条件
	saveAnalyseConfig:function(){
		
		var me = this;
		
		var planId = me.query("toolbar>combo")[0].getValue();
		if(me.selectedPlanValue){
			if(!planId || planId.length<1){
				Ext.Msg.alert("提示信息","请设置方案名称！");
				return;
			}
			var planName = planId;
			if(me.selectedPlanValue == planId)
				planName = me.query("toolbar>combo")[0].getRawValue();
			me.updatePlanConfig(me.selectedPlanValue,planName);
			return;
		}
			
		if(!me.getAnalyseConfig())return;
		Ext.widget("window",{
			title:"方案保存",
			 width:300,
			 height:200,
			 modal:true,
			 bodyPadding:30,
		     items:[{
		    	 xtype:'textfield',
		    	 labelWidth:60,
		    	 labelAlign:'right',
		    	 allowBlank:false,
		    	 allowOnlyWhitespace:false,
		    	 emptyText:'请输入方案名称',
		    	 width:200,
		    	 listeners:{
		    		 change:function(c,n,o){
		    			 if(Ext.util.Format.trim(n).length>0)
		    				 c.ownerCt.query('#saveButton')[0].setDisabled(false);
		    			 else
		    				 c.ownerCt.query('#saveButton')[0].setDisabled(true);
		    		 }
		    	 }
		     },{xtype:'checkbox',boxLabel:'是否共享',margin:'10 0 10 135'}],
		     bbar:["->",{text:'确定',itemId:'saveButton',disabled:true,margin:'0 10 0 0',handler:function(){
		    	 var window = this.up("window");
		    	 var planName = window.query("textfield")[0].getValue();
		    	 var is_share = window.query("checkbox")[0].checked?'1':'0';
		    	 var values = new HashMap();
			    values.put("settingParam",me.contextParam);
		 		values.put("subModuleId",me.subModuleId);
		 		values.put("planName",planName);
		 		values.put("isShare",is_share);
		     Rpc({functionId:'ZJ100000004',success:me.saveComplete,scope:me},values);
		 		window.close();
		     }},{text:'取消',handler:function(){this.up("window").close();}},{ xtype: 'tbfill' }]
		}).show();
		return;
		
	},
	/**
	 * 保存动作执行完后的操作
	 * @param req
	 */
	saveComplete:function(reqs){
        var req = Ext.decode(reqs.responseText);
		if(!req.result){
			Ext.Msg.alert("提示信息","保存失败！");
			return;
		}

		var planConfig = req.planConfig;
		//清空 所有 设置
		this.cleanAnalysePlan(false,false);
		this.setConfigByPlan(planConfig);
		var model = new analysePlanModel();
		model.set("value",planConfig.planId);
		model.set("name",planConfig.name);
		this.query("toolbar>combo")[0].store.add(model);
		this.query("toolbar>combo")[0].setValue(planConfig.planId);
		this.selectedPlanValue = planConfig.planId;
		this.query("toolbar>button[text=统计]")[0].setDisabled(false);
		
		this.initExsitItems();
	},
	
	/**
	 * 更新方案保存
	 */
	updatePlanConfig:function(planId,planName){
		var me = this;
		
		var hitems = me.contextPanel.HPanel.query('panel');
		var vitems = me.contextPanel.VPanel.query('panel');
		if(hitems.length<1 && vitems.length<1){
			Ext.Msg.alert("提示信息","必须选择横向或纵向统计指标！");//changxy 20160729
			return false;
		}
		
		  me.updateConfig = null;
		  var updateConfig = me.updateConfig;
		  updateConfig = {
				  deleteCond:new Array(),//用来保存删除的条件（选择指标 panel下具体的条件）
				  insertCond:new Array(),//新增的条件
				  updateCond:new Array(),//修改的条件
				  insertItems:new Array(),// 新增统计项（选择的指标）
				  deleteItems:new Array(),//删除的统计项
		          updateItems:new Array() //修改的统计项（修改名称的统计项）
		  };
		  
		  
		  //查找 删除的统计条件
	      var deleteCondComp = this.contextPanel.HPanel.query("panel[updateType=delete]");
	      deleteCondComp = deleteCondComp.concat(this.contextPanel.VPanel.query("panel[updateType=delete]"));
	      
	      for(var i=0;i<deleteCondComp.length;i++){
	    	  var cond = deleteCondComp[i];
	    	  if(cond.ownerCt.isVisible(true)){
	    		  var flag = false;
	    		  cond.ownerCt.items.each(function(c){
	    			  if(flag)
	    				  return false;
	    			  flag = c.isVisible();
	    		  });
	    		  if(!flag){
		    		  Ext.Msg.alert("提示信息","请选择 <"+cond.ownerCt.ownerCt.titleStr+"> 统计代码项！");
		    		  return ;
	    		  }
	    	  }
	    		  
	    	  updateConfig.deleteCond.push(cond.staticItemId);
	      }
	      
	    //查找 新增的统计条件
	      var insertCondComp = this.contextPanel.HPanel.query("panel[updateType=insert]");
	      insertCondComp = insertCondComp.concat(this.contextPanel.VPanel.query("panel[updateType=insert]"));
	      for(var i=0;i<insertCondComp.length;i++){
	    	  var cond = insertCondComp[i];
	    	  if(!cond.isVisible(true))
	    		  continue;
	    	  updateConfig.insertCond.push({ownerStaticId:cond.ownerStaticId,factor:cond.factor,condName:cond.condName,expr:cond.expr});
	      }
	    //查找 修改的统计条件
	      var updateCondComp = this.contextPanel.HPanel.query("panel[updateType=update]");
	      updateCondComp = updateCondComp.concat(this.contextPanel.VPanel.query("panel[updateType=update]"));
	      for(var i=0;i<updateCondComp.length;i++){
	    	     var cond = updateCondComp[i];
		    	  updateConfig.updateCond.push({staticItemId:cond.staticItemId,condName:cond.condName,factor:cond.factor,expr:cond.expr});
	      }
	      
	      var newItems = this.contextPanel.HPanel.query("treepanel");
	      newItems = newItems.concat(this.contextPanel.VPanel.query("treepanel"));
	      for(var i=0;i<newItems.length;i++){
	    	  var item = newItems[i];
	    	  if(!item.ownerCt.isVisible(true))
	    		  continue;
	    	  if(item.ownerCt.level==1){
	    		  var iitem = {};
	    		  iitem.itemid = item.itemid;
	    		  iitem.direction = item.ownerCt.direction;
	    		  iitem.conds = new Array();
	    		  var checked = item.getChecked();
	    		  if(checked.length<1){
	    			  Ext.Msg.alert("提示信息","请选择 <"+item.ownerCt.titleStr+"> 统计代码项！");
	    			  return;
	    		  }
	    		  Ext.each(checked,function(c){
	    			  iitem.conds.push(c.data.id);
	    		  });
	    		  if(item.ownerCt.items.getCount()>1){
	    			  iitem.child = new Array();
	    			  item.ownerCt.items.each(function(citem){
	    				  if(citem.xtype=='treepanel')
	    					  return true;
	    				  var ciitem = {};
	    				  ciitem.itemid = citem.itemid;
	    				  ciitem.direction = citem.ownerCt.direction;
	    				  ciitem.conds = new Array();
	    	    		  var cchecked = citem.items.getAt(0).getChecked();
	    	    		  if(cchecked.length<1){
	    	    			  Ext.Msg.alert("提示信息","请选择 <"+citem.ownerCt.titleStr+"> 统计代码项！");
	    	    			  return;
	    	    		  }
	    	    			  
	    	    		  Ext.each(cchecked,function(c){
	    	    			  ciitem.conds.push(c.data.id);
	    	    		  });
	    	    		  iitem.child.push(ciitem);
	    			  });
	    		  }
	    		  updateConfig.insertItems.push(iitem);
	    	  }else if(item.ownerCt.ownerCt.staticId){
	    		  var iitem ={};
	    		  iitem.upStaticId = item.ownerCt.ownerCt.staticId;
	    		  iitem.itemid = item.itemid;
	    		  iitem.direction = item.ownerCt.direction;
	    		  iitem.conds = new Array();
	    		  var checked = item.getChecked();
	    		  if(checked.length<1){
	    			  Ext.Msg.alert("提示信息","请选择 <"+item.ownerCt.titleStr+"> 统计代码项！");
	    			  return;
	    		  }
	    		  Ext.each(checked,function(c){
	    			  iitem.conds.push(c.data.id);
	    		  });
	    		  updateConfig.insertItems.push(iitem);
	    	  }
	      }
	      
	      //var deleteItems = this.contextPanel.HPanel.query("panel[deleteFlag=true]");
	      //deleteItems = deleteItems.concat(this.contextPanel.VPanel.query("panel[deleteFlag=true]"));
	      
	      //Ext.each(me.deleteItems,function(item){
	    	  updateConfig.deleteItems = me.deleteItems;
	    	  me.deleteItems = new Array();
	     // });
	      
	      
	    	  var updateItems = this.contextPanel.HPanel.query("panel[titleUpdate=true]");
	    	  		updateItems = updateItems.concat(this.contextPanel.VPanel.query("panel[titleUpdate=true]"));
	    	  for(var i=0;i<updateItems.length;i++){
	    		  var panel = updateItems[i];
	    		  updateConfig.updateItems.push({staticId:panel.staticId,staticName:panel.titleStr});
	    	  }
	    	  
	        //获取统计参数
			var analyseType = me.contextPanel.cPanel.items.items[0].value;
			
			var itemType = me.contextPanel.cPanel.items.items[1].value;
			if(analyseType!=1 && !itemType){
				Ext.Msg.alert("提示信息","请选 择统计项目！");
				return false;
			}
			
		  	updateConfig.analyseType = analyseType;
		  	updateConfig.itemType = itemType;
	      
	      var updateHandler = function(reqs){
	    	//清空 所有 设置
	    		var req = Ext.decode(reqs.responseText);
	  		  me.cleanAnalysePlan(false,false);
		    	  me.setConfigByPlan(req.settingParam);
		    	  me.query("toolbar>button[text=统计]")[0].setDisabled(false);
		    	  me.deleteItems = new Array();
		    	  me.initExsitItems();
		    	  me.selectCond = undefined;
	      };
	        var values = new HashMap();
			    values.put("updateParam",updateConfig);
		 		values.put("subModuleId",me.subModuleId);
		 		values.put("planName",planName);
		 		values.put("planId",planId);
		     Rpc({functionId:'ZJ100000005',success:updateHandler},values);
	 		
	 		me.query("combo")[0].getStore().getById(planId).set("name",planName);
	 		me.query("combo")[0].setValue(planId);
	},
	
	/**
	 * 清空设置
	 * @param isCleanPlanSelector   是否清空方案选择select的值
	 * @param isCleanAnalayType     是否还原统计方式和统计项目的值
	 */
	cleanAnalysePlan:function(isCleanPlanSelector,isCleanAnalayType){
		//清空 所有 设置
		this.contextPanel.HPanel.removeAll(true);
		this.contextPanel.VPanel.removeAll(true);
		//for(var key in this.codeItems){
		//	this.codeItems[key].used=false;
		//}
		if(isCleanPlanSelector){
		   this.query("toolbar>combo")[0].setValue("");
		   this.selectedPlanValue=undefined;
		}
		   
		if(isCleanAnalayType){
			this.contextPanel.cPanel.query('combo[comFlag=analyseType]')[0].setValue(1);
			this.contextPanel.cPanel.query('combo[comFlag=itemType]')[0].setValue("");
			this.contextPanel.cPanel.query('combo[comFlag=itemType]')[0].setVisible(false);
		}
		delete this.contextParam.h;
		delete this.contextParam.v;
		this.query("toolbar>button[text=统计]")[0].setDisabled(true);
		//this.selectCond=undefined;
	},
	saveAnalyseAsNew:function(){
		
	},
	
	/**
	 * 获取方案设置参数
	 * @param planId
	 */
	selectAnalysePlan:function(planId){
		
		var me = this;
		me.selectedPlanValue = planId;
		me.deleteItems = new Array();
		var requsetHandler = function(reqs){
			var req = Ext.decode(reqs.responseText);
			me.cleanAnalysePlan(false,true);
			me.setConfigByPlan(req.settingParam);
			me.query("toolbar>button[text=统计]")[0].setDisabled(false);
			me.doAnalyse();
		};
		
		var values = new HashMap();
		values.put("subModuleId",me.subModuleId);
 		values.put("searchType","planconfig");
 		values.put("planId",planId);
		Rpc({functionId:'ZJ100000003',success:requsetHandler},values);
	},
	
	/**
	 * 根据 方案设置 刷新界面
	 * @param planConfig
	 */
	setConfigByPlan:function(planConfig){
		var h = planConfig.h;
		var hComs = new Array();
		for(var i=0;i<h.length;i++){
		   	var hitem = h[i];
		   	var panel = this.createPlanItemPanel(hitem);
		   	var child = hitem.child;
		   	for(var k=0;child && k<child.length;k++){
		   		var childItem = child[k];
		   		var childPanel = this.createPlanItemPanel(childItem);
		   		panel.items.push(childPanel);
		   	}
		   	hComs.push(panel);
		}
		
		this.contextPanel.HPanel.add(hComs);
		
		var v = planConfig.v;
		var vComs = new Array();
		for(var i=0;i<v.length;i++){
		   	var vitem = v[i];
		   	var panel = this.createPlanItemPanel(vitem);
		   	var child = vitem.child;
		   	for(var k=0;child && k<child.length;k++){
		   		var childItem = child[k];
		   		var childPanel = this.createPlanItemPanel(childItem);
		   		panel.items.push(childPanel);
		   	}
		   	vComs.push(panel);
		}
		this.contextPanel.VPanel.add(vComs);
		
		this.contextPanel.cPanel.query('combo[comFlag=analyseType]')[0].setValue(planConfig.analyseType);
		var itemTypeCmp = this.contextPanel.cPanel.query('combo[comFlag=itemType]')[0];
		if(planConfig.analyseType!=1)
			itemTypeCmp.setVisible(true);
		itemTypeCmp.setValue(planConfig.itemType);
	},
	
	/**
	 * 保存后创建新的条件panel
	 * @param itemconfig
	 * @returns {___anonymous17845_18003}
	 */
	createPlanItemPanel:function(itemconfig){
		var me = this;
			var panel = {xtype:'panel',direction:itemconfig.direction,margin:10,level:itemconfig.level,title:itemconfig.itemName,titleStr:itemconfig.itemName,itemid:itemconfig.itemid,staticId:itemconfig.staticId};
			panel.listeners={
					afterrender:function(){
						var textComp = this.getHeader().items.getAt(0);
						//this.getHeader().insert(0,{xtype:'textfield',hidden:true,width:titleText.getWidth()});
						textComp.getEl().on('click',function(){
							var me = this;
							var value = me.ownerCt.titleStr;
							var text = Ext.widget('textfield',{
								value:value,margin:'7 0 0 0',
								listeners:{
									blur:function(){
										var titleText = this.value;
										titleText   =   titleText.replace(/^\s+|\s+$/g,"");
										if(titleText.length>0){
											me.ownerCt.titleStr = titleText;
											me.ownerCt.titleUpdate=true;
											me.items.getAt(2).getEl().setHTML(titleText);
										}
										
										me.items.getAt(2).setVisible(true);
										me.remove(me.items.getAt(1));
										me.remove(me.items.getAt(0));
									}
								}
							});
							me.insert(0,text);
							text.focus();
							me.insert(1,{xtype:'tbfill'});
							me.items.getAt(2).setVisible(false);
						},this.getHeader());
					}
			};
			panel.tools=[{
					xtype:'image',src:'/components/tableFactory/tableGrid-theme/images/add.png',style:'cursor:pointer;',width:14,height:14,margin:'0 3 0 0',  //添加鼠标指向样式 changxy 20160728
					listeners:{
						render:function(){
							this.getEl().on('click',me.addCode,me,this.up('panel'));
						}
					}
				},{
					xtype:'image',src:'/components/tableFactory/tableGrid-theme/images/edit.png',style:'cursor:pointer;',width:14,height:14,margin:'0 3 0 0',//添加鼠标指向样式 changxy 20160728
					listeners:{
						render:function(){
							this.getEl().on('click',me.editCondFactor,me);
						}
					}
				},{
					type:'close',
					handler:function(){
						//me.codeItems[this.toolOwner.itemid].used=false;
						//var level = this.toolOwner.level;
						//if(level==1){
						//   var items = this.toolOwner.query('panel[itemid]');
						//   Ext.each(items,function(cmp){
						//	   me.codeItems[cmp.itemid].used=false;
						//   });
						   
						//}
						
						me.query("toolbar>button[text=统计]")[0].setDisabled(true);
						me.deleteItems.push(this.toolOwner.staticId);
						this.toolOwner.close();//setVisible(false);
						//this.toolOwner.deleteFlag='true';
						
					}
			}];
			
			if(itemconfig.level==1){
				panel.bodyStyle='border-bottom:none';
				panel.dockedItems={
					   xtype:'toolbar',
					   border:1,
					   dock:'bottom',
					   items:['->',{
						   xtype:'image',
						   width:16,
						   height:16,
						   title:'添加二级统计项',
						   src:'/images/add.gif',
							   listeners:{
								   render:function(){
										this.getEl().on('click',me.addItem,me,this.up('panel'));
									}
							   }
					   }]
				};
				panel.layout={
						type:'table',
//						tableAttrs: {
//					        style: {
//					            width: '100%'
//					        }
//					    },
						columns:3
				};
				
				panel.manageWidth=true;
			    panel.manageHeight=true;
			    panel.minHeight=230;
			    panel.minWidth=232;
			}else{
				panel.layout="fit";
				panel.height=230;
			    panel.width=230;
			}
			panel.items = new Array();
			var itemsPanel = {
					xtype:'panel',height:200,width:230,layout:'vbox',border:0,colspan:3,autoScroll:true,items:new Array()
			};
			for(var i=0;i<itemconfig.conds.length;i++){
				var cond = itemconfig.conds[i];
				var condPanel = {
						xtype:'panel',
						width:'100%',
						height:30,
						bodyStyle:'border-width:1px 1px 1px 1px',
						margin:'5 5 0 5',
					    staticItemId:cond.staticItemId,
					    ownerStaticId:itemconfig.staticId,
					    listeners:{
					    	render:function(){
					    		var thiscond = this;
					    		this.getEl().on("click",function(ev,el){
					    			if(me.selectCond && me.selectCond.getEl()){
					    				me.selectCond.items.getAt(0).getEl().setStyle("background-color","white");
						    		}
					    			thiscond.items.getAt(0).getEl().setStyle("background-color","rgb(255, 248, 210)");
					    			me.selectCond = thiscond;
					    		});
					    		
					    	}
					    },
					    factor:cond.factor,
					    expr:cond.expr,
					    condName:cond.condName,
					    codedesc:cond.codedesc,
					    layout:{
					    	type:'hbox',
					    	align:'middle'
					    },
					    items:[{
					    		xtype:'label',text:cond.condName,flex:.9,padding:5,style:'text-align:left'
					    	},{
					    		xtype:'image',src:'/images/del.gif',width:16,height:16,margin:'0 5 0 0',
					    		listeners:{
					    			render:function(){
					    				this.getEl().on('click',function(){
					    					//this.ownerCt.ownerCt.remove(this.ownerCt,true); 
					    					this.ownerCt.updateType="delete";
					    					this.ownerCt.setVisible(false);
					    					me.query("toolbar>button[text=统计]")[0].setDisabled(true);
					    					if(this.ownerCt===me.selectCond){
					    						me.selectCond = undefined;
					    					}
					    				},this);
					    			}
					    		}
					    	}]
				};
				itemsPanel.items.push(condPanel);
			}
			panel.items.push(itemsPanel);
			//me.codeItems[itemconfig.itemid].used=true;
			return panel;
	},
	addCode:function(e,E,p){
		var me = this;
		var store = {
			    fields:['indexNum','condItem','symbol','value'],
			    data:[]
			};
			var condGrid = Ext.widget("gridpanel",{
				store:store,
			    height:200,
			    forceFit:true,
			    columnLines:true,
			    sortableColumns:false,
			    enableColumnMove:false,
			    enableColumnResize:false,
			    columns:[{dataIndex:'indexNum',text:'序号',flex:1,align:'center'},{
			    		dataIndex:'condItem',text:'指标名称',menuDisabled:true,flex:3.5,
			    		editor:{
			    			xtype:'combo',
			    			displayField:'name',
			    			editable:false,
			    			valueField:'itemid',
			    			store:{
			    				fields:["name","itemid"],
			    				data:me.columns
			    			}
			    		},renderer:function(value){
			    			if(value)
			    			   return me.columnDescs[value].desc;
			    			else
			    				return '';
			    		}
			    },{
			    		dataIndex:'symbol',text:'关系符',menuDisabled:true,align:'center',flex:1.5,
			    		editor:{
			    			xtype:'combo',
			    			displayField:'value',
			    			editable:false,
			    			width:40,
			    			valueField:'value',
			    			store:{
			    				fields:["value"],
			    				data:[{value:'='},{value:'<>'},{value:'>'},
			    				      {value:'>='},{value:'<'},{value:'<='}]
			    			}
			    		}
			    },{
			    		dataIndex:'value',text:'值',menuDisabled:true,flex:3,
			    		editor:{
			    			xtype:'textfield'
			    		},
			    		renderer:function(value){
			    			if(value && Ext.isString(value) &&  value.indexOf('`')>-1)
			    				return value.split('`')[1];
			    			else
			    				return value;
			    		}
			    		
			    },{
			    		text:'删除',menuDisabled:true,flex:1,align:'center',
			    		renderer:function(value,meta){
			    			return '<img src="/images/del.gif" title="删除"/>';
			    		}
			    }],
			    viewConfig:{
			    	   markDirty:false
			    }, 
			    plugins:{
			    	    ptype:'cellediting',
			    	    clicksToEdit: 1,
			    	    listeners:{
			    	    	    beforeedit:function(editor,context){
			    	    	    	   if(context.field != 'value' || !context.record.get("condItem"))
			    	    	    		   return true;
			    	    	    	   var editItem =  me.columnDescs[context.record.get("condItem")];
			    	    	    	   if(editItem.columnType == 'A' && editItem.codesetid != '0'){
			    	    	    	     context.column.setEditor({xtype:'codecomboxfield',codesetid:editItem.codesetid,inputable:true});//.codesetid=codesetid;
			    	    	    	   }else if(editItem.columnType == 'N'){
			    	    	    		   context.column.setEditor({xtype:'numberfield'});
			    	    	    	   }else if(editItem.columnType == 'D'){
			    	    	    		   context.column.setEditor({xtype:'datetimefield',format:editItem.format});
			    	    	    	   }else{
			    	    	    		   context.column.setEditor({xtype:'textfield'});
			    	    	    	   }
			    	    	    	
			    	    	    },
			    	    	    edit:function(editor,context){
			    	    	    	     if(context.field == 'condItem' && context.originalValue != context.value)
			    	    	    	    	      context.record.set("value","");
			    	    	    }
			    	    }
			    },
			    listeners:{
			    	   cellclick:function(a,b,index,record){
			    		   if(index!=4)
			    			   return;
			    		   if(a.getStore().getCount()==1){
			    			   Ext.Msg.alert("提示信息","不能删除全部统计条件！");
			    			   return;
			    		   }
			    		   var indexNum = record.get('indexNum');
			    		   a.getStore().remove(record); 
			    		   a.getStore().each(function(r){
			    			   var num = r.get('indexNum');
			    			  if(num>indexNum)
			    				  r.set('indexNum',num-1);
			    		   });
			    	   }
			    }
			});
		Ext.widget("window",{
			title:'条件维护',
			width:500,
			height:450,
			resizable:false,
			bodyPadding:10,
			modal:true,
			layout:{
				type:'vbox',
				align:'stretch'
			},
			items:[{border:false,items:{xtype:"textfield",fieldLabel:'名称',labelSeparator:'',beforeLabelTextTpl:'<font color="red"> * </font>',width:300,labelAlign:'right',labelWidth:40,allowBlank:false,allowOnlyWhitespace:false}},
			       condGrid,
			       {
				      border:false,
				      layout:'hbox',
				      margin:'5 0 5 0',
				      items:[{xtype:'label',text:'表达式'},{xtype:'tbfill'},
				             {xtype:'image',src:'/images/add.gif',height:16,width:16,title:'添加条件',
				    	             listeners:{
				    	            	     render:function(){
				    	            	    	      this.getEl().on('click',function(){
				    	            	    	    	     var store = this.up('window').query('gridpanel')[0].getStore();
					    	            	    	    	  var insertRow = new condFactorModel();
					    	            	    	    	  insertRow.set('indexNum',store.getCount()+1);
					    	            	    	    	  insertRow.set('symbol','=');
					    	            	    	    	  store.add(insertRow);
				    	            	    	      },this);
				    	            	     }
				    	             }
				    	         }]
			       },{
			    	      xtype:'textareafield',
			    	      height:50,
			    	      beFocused:false,
			    	      listeners:{
			    	    	      focus:function(){
			    	    	    	      this.beFocused=true;
			    	    	      }
			    	      }
			       },{
			    	      border:false,
			    	      layout:'hbox',
			    	      items:[{
			    	    	     xtype:'button',
			    	    	     margin:'5 5 5 0',
			    	    	     text:'且',handler:function(){
			    	    	    	      var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
			    	    	    	      me.editCondExprStr(textarea,"*");
			    	    	     }
			    	      },{
			    	    	  	 xtype:'button',
			    	    	  	 margin:5,
			    	    	     text:'或',handler:function(){
			    	    	    	 	var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
			    	    	    	 	me.editCondExprStr(textarea,"+");
			    	    	     }
			    	      },
			    	      {
			    	    	  	 xtype:'button',
			    	    	  	margin:5,
			    	    	     text:'非',handler:function(){
			    	    	    	    var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
		    	    	    	        me.editCondExprStr(textarea,"!");
			    	    	     }
			    	      },
			    	      {
			    	    	  	 xtype:'button',
			    	    	  	margin:5,
			    	    	     text:'(',handler:function(){
			    	    	    	    var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
		    	    	    	        me.editCondExprStr(textarea,"(");
			    	    	     }
			    	      },{
			    	    	  	 xtype:'button',
			    	    	  	margin:5,
			    	    	     text:')',handler:function(){
			    	    	    	    var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
		    	    	    	        me.editCondExprStr(textarea,")");
			    	    	     }
			    	      }]
			       }
			       
			],
			bbar:['->',{text:'确定',margin:'0 10 0 0',handler:function(){
				var window = this.ownerCt.ownerCt;

				  var condName = window.query('textfield')[0].getValue(); 
				  if(Ext.util.Format.trim(condName).length<1){ 
					  Ext.Msg.alert('提示信息	','请输入条件名！');
					  return;
				  }
					  
				  
				  var expr = window.query('textareafield')[0].getValue();
				  expr = expr?expr:'';
				  if(expr.length<1){//changxy
    			  Ext.showAlert("非法表达式！")
				   return;
				   }
				  var str = "*+!()";
				  for(var k = 1;k<=condGrid.getStore().getCount();k++)
					  str+=k;
				  for(var i=0;i<expr.length;i++){
					  if(str.indexOf(expr.charAt(i))==-1){
						  Ext.Msg.alert("提示信息	","非法表达式！");
						  return;
					  }
				  }
				  var factor = '';
				  var codedesc={};
				  condGrid.getStore().each(function(r){
					  var condItem = r.get('condItem');
					  if(!condItem || condItem.length<1)
						  return;
					  var value = r.get('value');//.split('`')[0];
					  var realValue = value;
					  if(Ext.isString(value) && value.split('`').length>1){
						  if(value.split('`')[0].indexOf("*")==value.split('`')[0].length-1)
							  realValue = value.split('`')[0];
						  else{
							  realValue = value.split('`')[0]+"*";
						  }
						  codedesc[r.get("condItem")+r.get("symbol")+realValue]=value.split('`')[1];
					  }
						  
					  factor += r.get("condItem")+r.get("symbol")+realValue+"`";
					  
				  });
				  if(factor.length<1){
					  Ext.Msg.alert('提示信息','必须设置统计条件！');
					  return;
				  }
				  var condPanel = {
							xtype:'panel',
							width:'100%',
							height:30,
							bodyStyle:'border-width:1px 1px 1px 1px',
							margin:'5 5 0 5',
						    updateType:'insert',
						    ownerStaticId:p.staticId,
						    condName:condName,
						    listeners:{
							    	render:function(){
							    		var thiscond = this;
							    		this.getEl().on("click",function(ev,el){
							    			if(me.selectCond && me.selectCond.getEl()){
							    				me.selectCond.items.getAt(0).getEl().setStyle("background-color","white");
								    		}
							    			thiscond.items.getAt(0).getEl().setStyle("background-color","rgb(255, 248, 210)");
							    			me.selectCond = thiscond;//保存选中的条件
							    		});
							    		
							    	}
						    },
						    factor:factor,
						    expr:expr,
						    codedesc:codedesc,
						    layout:{
							    	type:'hbox',
							    	align:'middle'
						    },
						    items:[{
						    		xtype:'label',text:condName,flex:.9,padding:5,style:'text-align:left'
						    	},{
						    		xtype:'image',src:'/images/del.gif',width:16,height:16,margin:'0 5 0 0',
						    		listeners:{
						    			render:function(){
						    				this.getEl().on('click',function(){
						    						this.ownerCt.ownerCt.remove(this.ownerCt,true); 
						    					me.query("toolbar>button[text=统计]")[0].setDisabled(true);
						    				},this);
						    			}
						    		}
						    	}]
					};
				  p.items.getAt(0).add(condPanel);
				  window.close();
			}},{text:'取消',handler:function(){
			    this.ownerCt.ownerCt.close();
			}},{xtype:'tbfill'}]
			
		}).show();
		
		return;
	},
	
	editCondExprStr:function(textarea,str){
		var obj = textarea.inputEl.dom;
		var value = textarea.getValue();
		if(!textarea.beFocused){
			textarea.setValue(value+symbol);
			return;
		}
			
		obj.focus();
		if (document.selection) {
			var sel = document.selection.createRange();
			sel.text = str;
		} else if (typeof obj.selectionStart == 'number' && typeof obj.selectionEnd == 'number') {
			var startPos = obj.selectionStart,
			endPos = obj.selectionEnd,
			cursorPos = startPos,
			tmpStr = obj.value;
			obj.value = tmpStr.substring(0, startPos) + str + tmpStr.substring(endPos, tmpStr.length);
			cursorPos += str.length;
			obj.selectionStart = obj.selectionEnd = cursorPos;
		} else {
			obj.value += str;
		}
		
		//textarea.setValue(value);
	},
	
	
	deleteAnalysePlan:function(){
		var me = this;
		var planId = this.query("toolbar>combo")[0].getValue();
		var store = me.query("toolbar>combo")[0].getStore();
		var record = store.getById(planId);
		if(!planId || planId.length<1 || !record){
			Ext.Msg.alert("提示信息","请选择删除方案！");
			return;
		}
		Ext.Msg.confirm("提示信息","确认删除吗？",function(id){if(id=='yes'){
			var removePlan=function(op,su){
				//var store = me.query("toolbar>combo")[0].getStore();
				//var record = store.getById(planId);
				if(me.chartPanel){
					me.chartPanel.removeAll(true);
					me.chartPanel.collapse();
				}
				if(me.tablePanel){
					me.tablePanel.removeAll(true);
					me.tablePanel.collapse();
				}
				store.remove(record);
				me.cleanAnalysePlan(true, true);
				me.planSettingPanel.expand();
				me.initExsitItems();
			};
			
			var values = new HashMap();
			values.put("subModuleId",me.subModuleId);
	 		values.put("planId",planId);
			Rpc({functionId:'ZJ100000007',success:removePlan},values);
		}});
		
		
		
	},
	editCondFactor:function(){
		var me = this;
		var cond = this.selectCond;
		if(!cond){
			Ext.Msg.alert("提示信息","请选择修改项目!");
			return;
		}
		var factors = cond.factor.split('`');
		var dataArray = new Array();
		var codedesc = cond.codedesc;
		var f = undefined;
		var symbol = '=';
		for(var i=0;i<factors.length;i++){
			var d = {};
		    f = factors[i];
		    if(f=='')
		    	  continue;
			if(f.indexOf('<>')>-1){
				symbol = '<>';
			}else if(f.indexOf('>=')>-1){
				symbol = '>=';
			}else if(f.indexOf('<=')>-1){
				symbol = '<=';
			}else if(f.indexOf('<')>-1){
				symbol = '<';
			}else if(f.indexOf('>')>-1){
				symbol = '>';
			}else if(f.indexOf('=')>-1){  //changxy 20160616
			    symbol = '=';
			}
			d.indexNum = i+1;
			d.condItem = f.split(symbol)[0];
			var desc = codedesc[f];
			var value = f.split(symbol).length==2?f.split(symbol)[1]:'';
			if(desc){
				value = value+"`"+desc;
			}
			d.symbol = symbol;
			d.value = value;//+"`";
			dataArray.push(d);
		}
		var store = {
		    fields:['indexNum','condItem','symbol','value'],
		    data:dataArray
		};
		var condGrid = Ext.widget("gridpanel",{
			store:store,
		    height:200,
		    forceFit:true,
		    columnLines:true,
		    sortableColumns:false,
		    enableColumnMove:false,
		    enableColumnResize:false,
		    columns:[{dataIndex:'indexNum',text:'序号',flex:1,align:'center'},{
		    		dataIndex:'condItem',text:'指标名称',menuDisabled:true,flex:3.5,
		    		editor:{
		    			xtype:'combo',
		    			displayField:'name',
		    			valueField:'itemid',
		    			editable:false,
		    			store:{
		    				fields:["name","itemid"],
		    				data:me.columns
		    			}
		    		},renderer:function(value){
		    			if(value)
		    			   return me.columnDescs[value].desc;
		    			else
		    				return '';
		    		}
		    },{
		    		dataIndex:'symbol',text:'关系符',menuDisabled:true,align:'center',flex:1.5,
		    		editor:{
		    			xtype:'combo',
		    			displayField:'value',
		    			editable:false,
		    			width:40,
		    			valueField:'value',
		    			store:{
		    				fields:["value"],
		    				data:[{value:'='},{value:'<>'},{value:'>'},
		    				      {value:'>='},{value:'<'},{value:'<='}]
		    			}
		    		}
		    },{
		    		dataIndex:'value',text:'值',menuDisabled:true,flex:3,
		    		editor:{
		    			xtype:'textfield'
		    		},
		    		renderer:function(value){
		    			if(value && Ext.isString(value) && value.indexOf('`')>-1)
		    				return value.split('`')[1];
		    			else
		    				return value;
		    		}
		    		
		    },{
		    		text:'删除',menuDisabled:true,flex:1,align:'center',
		    		renderer:function(value,meta){
		    			return '<img src="/images/del.gif" title="删除"/>';
		    		}
		    }],
		    viewConfig:{
		    	   markDirty:false
		    }, 
		    plugins:{
		    	    ptype:'cellediting',
		    	    clicksToEdit: 1,
		    	    listeners:{
		    	    	    beforeedit:function(editor,context){
		    	    	    	   if(context.field != 'value' || !context.record.get("condItem"))
		    	    	    		   return true;
		    	    	    	   var editItem =  me.columnDescs[context.record.get("condItem")];
		    	    	    	   if(editItem.columnType == 'A' && editItem.codesetid != '0'){
		    	    	    	     context.column.setEditor({xtype:'codecomboxfield',codesetid:editItem.codesetid,inputable:true});//.codesetid=codesetid;
		    	    	    	   }else if(editItem.columnType == 'N'){
		    	    	    		   context.column.setEditor({xtype:'numberfield'});
		    	    	    	   }else if(editItem.columnType == 'D'){
		    	    	    		   context.column.setEditor({xtype:'datetimefield',format:editItem.format});
		    	    	    	   }else{
		    	    	    		   context.column.setEditor({xtype:'textfield'});
		    	    	    	   }
		    	    	    	
		    	    	    },
		    	    	    edit:function(editor,context){
		    	    	    	     if(context.field == 'condItem' && context.originalValue != context.value)
		    	    	    	    	      context.record.set("value","");
		    	    	    }
		    	    }
		    },
		    listeners:{
		    	   cellclick:function(a,b,index,record){
		    		   if(index!=4)
		    			   return;
		    		   if(a.getStore().getCount()==1){
		    			   Ext.Msg.alert("提示信息","不能删除全部统计条件！");
		    			   return;
		    		   }
		    		   var indexNum = record.get('indexNum');
		    		   a.getStore().remove(record); 
		    		   a.getStore().each(function(r){
		    			   var num = r.get('indexNum');
		    			  if(num>indexNum)
		    				  r.set('indexNum',num-1);
		    		   });
		    	   }
		    }
		});
		Ext.widget("window",{
			title:'条件维护',
			width:500,
			height:450,
			resizable:false,
			bodyPadding:10,
			modal:true,
			layout:{
				type:'vbox',
				align:'stretch'
			},
			items:[{border:false,items:{xtype:"textfield",fieldLabel:'名称',labelSeparator:'',beforeLabelTextTpl:'<font color="red"> * </font>',value:cond.condName,width:300,labelAlign:'right',labelWidth:40,allowBlank:false,allowOnlyWhitespace:false}},
			       condGrid,
			       {
				      border:false,
				      layout:'hbox',
				      margin:'5 0 5 0',
				      items:[{xtype:'label',text:'表达式'},{xtype:'tbfill'},
				             {xtype:'image',src:'/images/add.gif',height:16,width:16,title:'添加条件',
				    	             listeners:{
				    	            	     render:function(){
				    	            	    	      this.getEl().on('click',function(){
				    	            	    	    	     var store = this.up('window').query('gridpanel')[0].getStore();
					    	            	    	    	  var insertRow = new condFactorModel();
					    	            	    	    	  insertRow.set('indexNum',store.getCount()+1);
					    	            	    	    	  insertRow.set('symbol','=');
					    	            	    	    	  store.add(insertRow);
					    	            	    	    	  //add by xiegh on date 20180129 bug:33891
					    	            	    	    	  var count = store.getCount();
					    	            	    	    	  var expr = count==1?"1":"*"+count;
					    	            	    	    	  var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
					    	            	    	    	  var textareavalue = textarea.inputEl.dom.value;
					    	            	    	    	  textarea.setValue(textareavalue+expr);
				    	            	    	      },this);
				    	            	     }
				    	             }
				    	         }]
			       },{
			    	      xtype:'textareafield',
			    	      height:50,
			    	      beFocused:false,
			    	      value:cond.expr,
			    	      listeners:{
			    	    	      focus:function(){
			    	    	    	      this.beFocused=true;
			    	    	      }
			    	      }
			       },{
			    	      border:false,
			    	      layout:'hbox',
			    	      items:[{
			    	    	     xtype:'button',
			    	    	     margin:'5 5 5 0',
			    	    	     text:'且',handler:function(){
			    	    	    	      var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
			    	    	    	      me.editCondExprStr(textarea,"*");
			    	    	     }
			    	      },{
			    	    	  	 xtype:'button',
			    	    	  	 margin:5,
			    	    	     text:'或',handler:function(){
			    	    	    	 	var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
		    	    	    	        me.editCondExprStr(textarea,"+");
			    	    	     }
			    	      },
			    	      {
			    	    	  	 xtype:'button',
			    	    	  	margin:5,
			    	    	     text:'非',handler:function(){
			    	    	    	    var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
		    	    	    	        me.editCondExprStr(textarea,"!");
			    	    	     }
			    	      },
			    	      {
			    	    	  	 xtype:'button',
			    	    	  	margin:5,
			    	    	     text:'(',handler:function(){
			    	    	    	    var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
		    	    	    	        me.editCondExprStr(textarea,"(");
			    	    	     }
			    	      },{
			    	    	  	 xtype:'button',
			    	    	  	margin:5,
			    	    	     text:')',handler:function(){
			    	    	    	    var textarea = this.ownerCt.ownerCt.query('textareafield')[0];
		    	    	    	        me.editCondExprStr(textarea,")");
			    	    	     }
			    	      }]
			       }
			       
			],
			bbar:['->',{text:'确定',margin:'0 10 0 0',handler:function(){
				var window = this.ownerCt.ownerCt;

				  var condName = window.query('textfield')[0].getValue(); 
				  if(Ext.util.Format.trim(condName).length<1){ 
					  Ext.Msg.alert('提示信息','请输入条件名！');
					  return;
				  }
					  
				  
				  var expr = window.query('textareafield')[0].getValue();
				  
				  var str = "*+!()";
				  for(var k = 1;k<=condGrid.getStore().getCount();k++)
					  str+=k;
				  
				  for(var i=0;i<expr.length;i++){
					  if(str.indexOf(expr.charAt(i))==-1){
						  Ext.Msg.alert("提示信息","非法表达式！");
						  return;
					  }
				  }
				  for(var k = 1;k<=condGrid.getStore().getCount();k++){
					  if(expr.indexOf(k)==-1){
							  Ext.Msg.alert("提示信息","非法表达式！");
							  return;
					  }
				  }
				  cond.condName = condName;
				  cond.query('label')[0].setText(cond.condName);
				  cond.expr = expr;
				  var factor = '';
				  
				  condGrid.getStore().each(function(r){
					  var condItem = r.get('condItem');
					  if(!condItem || condItem.length<1)
						  return;
					  var value = r.get('value');//.split('`')[0];
					  var realValue = value;
					  if(Ext.isString(value) && value.split('`').length>1){
						  if(value.split('`')[0].indexOf("*")==value.split('`')[0].length-1)
							  realValue = value.split('`')[0];
						  else{
							  realValue = value.split('`')[0]+"*";
						  }
						  cond.codedesc[r.get("condItem")+r.get("symbol")+realValue]=value.split('`')[1];
					  }else{
						  delete cond.codedesc[condItem];
					  }
						  
					  factor += r.get("condItem")+r.get("symbol")+realValue+"`";
				  });
				  if(factor.length<1){
					  Ext.Msg.alert('提示信息','必须设置统计条件！');
					  return;
				  }
				  cond.factor = factor;
				  if(cond.updateType!='insert')
				        cond.updateType='update';
				  window.close();
			}},{text:'取消',handler:function(){
			    this.ownerCt.ownerCt.close();
			}},{xtype:'tbfill'}]
			
		}).show();
	},
	getMainPanel:function(){
		return this;
	}
	
});