/**
 * 工资|保险 项目分类统计台帐
 */
Ext.define("ItemGroupMusterURL.ItemGroupMuster",{
	requires:['EHR.exportPageSet.ExportPageSet'],
	constructor:function(config) {
		itemgroupmuster_me = this;
		itemgroupmuster_me.callBackFunc = config.callBackFunc;
		itemgroupmuster_me.edit_pow = config.edit_pow;
		itemgroupmuster_me.init(config);
	},
	init:function(config){
		itemgroupmuster_me.rsdtlid = config.rsdtlid;
		itemgroupmuster_me.rsid = config.rsid;
		itemgroupmuster_me.imodule = config.imodule;
		var map = new HashMap();
		itemgroupmuster_me.tYear = "";
		itemgroupmuster_me.group = "0";
		itemgroupmuster_me.chartType = "column";
		itemgroupmuster_me.codeitem = "";
		itemgroupmuster_me.codevalue = "";
		itemgroupmuster_me.incloudLowLevel = '0';
		itemgroupmuster_me.showMx = '1';
		itemgroupmuster_me.accumulate = "0";
		itemgroupmuster_me.tableName = config.tableName;
		map.put("year",itemgroupmuster_me.tYear);
		map.put("rsdtlid",itemgroupmuster_me.rsdtlid);
		map.put("rsid",itemgroupmuster_me.rsid);//5、	rsid=6: 人员工资台账   rsid =14:人员保险台账
		map.put("transType","1");
		map.put("codeitem",itemgroupmuster_me.codeitem);//分类指标
		map.put("codevalue",itemgroupmuster_me.codevalue);//分类指标值
		map.put("intoflag",'0');
		map.put("incloudLowLevel",itemgroupmuster_me.incloudLowLevel);
		map.put("accumulate",itemgroupmuster_me.accumulate);
		map.put("showMx",itemgroupmuster_me.showMx);
		map.put("group",itemgroupmuster_me.group);
		Rpc({functionId:'GZ00000708',async:false,success:itemgroupmuster_me.getTableDataOk,scope:itemgroupmuster_me},map);
	},
	getTableDataOk:function(form,action){
		var responseText = Ext.decode(form.responseText);
		var returnObj = Ext.decode(responseText.returnStr);
        var return_code = returnObj.return_code;
        var result = returnObj.return_data;
		var flag = result.flag;
		var yearselectjson = result.yearselectjson;
		if(flag=='1'){
			var store = Ext.data.StoreManager.lookup('itemgroupmusterdata1_'+itemgroupmuster_me.rsdtlid+'_dataStore'); 
			store.currentPage=1;
			store.load();
			itemgroupmuster_me.echartDataList = result.echartDataList;
			itemgroupmuster_me.changeChartType(0,itemgroupmuster_me.chartType);
		}else{
			var dataListObj = result.tableConfig;	
			itemgroupmuster_me.dataListGrid = new BuildTableObj(dataListObj);
			itemgroupmuster_me.addGridListens(itemgroupmuster_me.dataListGrid);
			itemgroupmuster_me.mainPanel = itemgroupmuster_me.dataListGrid.getMainPanel();
			var salaryItemList = result.salaryItemList;
			itemgroupmuster_me.echartDataList = result.echartDataList;
			itemgroupmuster_me.rsid_d = result.rsid_d;
			itemgroupmuster_me.rsdtlid_d = result.rsdtlid_d;
			//薪资账套代码指标
			var salaryItemStore = Ext.create('Ext.data.Store', {
				id:'salaryItemStore',
				fields:['itemdesc','itemcode'],
				data:salaryItemList
			});
			var toolbar_data = Ext.create('Ext.toolbar.Toolbar', {
				id:'employee_toolbar',
				height:35,
				border:0,
				items:[{
					height:24,
			        text: gz.label.analysisdata.navigation,
			        menu:
			        {
			            items: [
			                {
			                    text: gz.label.outExcel,
			                    icon: '/images/export.gif',
			                    handler: function () {
			                    	itemgroupmuster_me.exportExcel();
			                    }
			                }/*, {
			                    text: '导出PDF',
			                    handler: function () {
			                    }
			                }*/,
			                {
			                    text: gz.label.analysisdata.pagesetting,
			                    icon: '/images/img_o.gif',
			                    handler: function () {
			                    	itemgroupmuster_me.showpagesetting(itemgroupmuster_me.rsid,itemgroupmuster_me.rsdtlid);
			                    }
			                }
			            ]
			        }
			    },'-',{xtype:'button',text:gz.label.analysisdata.setrange,height:24,handler:function(){itemgroupmuster_me.setRange()},hidden:!itemgroupmuster_me.edit_pow},'-',
			    {xtype:'checkbox',boxLabel:gz.label.analysisdata.showmxornot,height:24,margin:'0 5 0 40',width:110,id:'checkbox1',listeners:{
			    	change:function(e, newValue, oldValue, eOpts){
			    		if(newValue){
			    			itemgroupmuster_me.showMx = "0";
			    		}else
			    			itemgroupmuster_me.showMx = "1";
			    		itemgroupmuster_me.queryData();
			    	}
			    }},{xtype:'checkbox',boxLabel:gz.label.analysisdata.accumulate,height:24,margin:'0 5 0 0',width:90,id:'checkbox2',listeners:{
			    	change:function(e, newValue, oldValue, eOpts){
			    		if(newValue){
			    			itemgroupmuster_me.accumulate = "1";
			    		}else
			    			itemgroupmuster_me.accumulate = "0";
			    		itemgroupmuster_me.queryData();
			    	}
			    }},
			    {xtype:'checkbox',boxLabel:gz.label.analysisdata.groupornot,height:24,margin:'0 40 0 0',width:60,id:'checkbox4',checked:true,listeners:{
			    	change:function(e, newValue, oldValue, eOpts){
			    		if(!newValue){
			    			itemgroupmuster_me.group = "1";
			    			Ext.getCmp('combo1').show();
			    			var itemcode = Ext.getCmp('combo1').getValue();
			    			if(itemcode==null)
			    				itemcode = '';
			    			itemgroupmuster_me.codeitem = itemcode;
			    			if(itemcode!=''){
			    				itemgroupmuster_me.codeitem = itemcode.split("`")[1];
			    				if(Ext.getCmp('combo2')){
			    					Ext.getCmp('combo2').show();
			    					if(Ext.getCmp('checkbox3')){
				   						Ext.getCmp('checkbox3').show();
				   					}
			    					var itemvalue = Ext.getCmp('combo2').getValue();
			    					var checkbox3 = Ext.getCmp('checkbox3').getValue();
			    					if(checkbox3){
			    						itemgroupmuster_me.incloudLowLevel = "1";
			    					}
			    					if(itemvalue!=''){
			    						itemgroupmuster_me.codevalue = itemvalue.split("`")[0];
			    						itemgroupmuster_me.queryData();
			    					}else{
			    			    		itemgroupmuster_me.codevalue = "-1";
			    					}
			    				}
			    			}else{
			    				itemgroupmuster_me.codevalue = "-1";
			    			}
			    		}else{
			    			itemgroupmuster_me.group = "0";
			    			itemgroupmuster_me.codeitem = "";
    			    		itemgroupmuster_me.codevalue = "";
    			    		itemgroupmuster_me.incloudLowLevel = "0";
			    			Ext.getCmp('combo1').hide();
			    			if(Ext.getCmp('combo2'))
			    				Ext.getCmp('combo2').hide();
			    			if(Ext.getCmp('checkbox3')){
		   						Ext.getCmp('checkbox3').hide();
		   					}
			    			itemgroupmuster_me.queryData();
			    		}
			    	}
			    }},
			    {xtype:'combo',id:'combo1',margin:'0 5 0 0',store:salaryItemStore,width:150,
			    	queryMode: 'local',editable:false,displayField: 'itemdesc',valueField: 'itemcode',hidden:true,
			    listeners:{
					afterrender:function(combo){
		     		},
	   				select:function(combo,records){
	   					itemgroupmuster_me.itemcode = combo.getValue().split("`")[0];
	   					itemgroupmuster_me.codeitem = combo.getValue().split("`")[1];
	   					itemgroupmuster_me.codevalue = "";
	   					if(Ext.getCmp('combo2')){
	   						Ext.getCmp('combo2').destroy();
	   					}
	   					if(Ext.getCmp('checkbox3')){
	   						Ext.getCmp('checkbox3').show();
	   					}
		   				var combo2 = Ext.widget('codecomboxfield',{
		   					 id:'combo2',
		             		 value:"",
		             		 width:150,
		             		 codesetid:itemgroupmuster_me.itemcode,
		             		 nmodule:'1',
		             		 ctrltype:'3',
		             		 afterCodeSelectFn:function(a,value){
		             			var codeitem = Ext.getCmp('combo1').getValue().split("`")[1];
	    			    		var codevalue = value;
	    			    		itemgroupmuster_me.codeitem = codeitem;
	    			    		itemgroupmuster_me.codevalue = codevalue;
	    			    		itemgroupmuster_me.queryData();
		        		 	 }
		   				});
		   				var toolbar = Ext.getCmp('employee_toolbar');
		   				toolbar.insert(toolbar.items.items.length-1,combo2);
					}
				}},
			    {xtype:'checkbox',boxLabel:gz.label.analysisdata.incloudLowLevel,height:24,margin:'0 0 0 5',width:150,id:'checkbox3',hidden:true,listeners:{
			    	change:function(e, newValue, oldValue, eOpts){
			    		if(newValue){
			    			itemgroupmuster_me.incloudLowLevel = "1";
			    		}else
			    			itemgroupmuster_me.incloudLowLevel = "0";
			    		itemgroupmuster_me.queryData();
			    	}
			    }}]});
			//年份选择下拉
			var selectStore = Ext.create('Ext.data.Store', {
				id:'selectStore',
				fields:['name','id'],
				data:yearselectjson
			});
			var selectPanel = Ext.create('Ext.form.ComboBox', {
			    store: selectStore,
			    id:'selectPanel',
			    queryMode: 'local',
			    repeatTriggerClick : true,
			    margin:'0 5 0 0',
			    labelAlign:'right',
			    labelWidth:30,
			    displayField: 'name',
			    valueField: 'id',
			    editable:false,
			    width:90,
			    fieldStyle:'height:20px;',
				listeners:{
					afterrender:function(combo){
						var count = selectStore.getCount();
						if(count>0){
							var id = selectStore.getAt(0).get('id');
							if(id)
								combo.setValue(id);
							else
								combo.setValue(itemgroupmuster_me.tYear);
						}
						itemgroupmuster_me.tYear = combo.getValue()+"";
		     		},
	   				select:function(combo,records){
	   					itemgroupmuster_me.tYear = combo.getValue()+"";
	                	itemgroupmuster_me.queryData();
					}
				}
			});
			toolbar_data.insert(4,selectPanel);
			//toolbar_data.insert(5,gz.label.analysisdata.particularyear);
			var toppanel = Ext.create('Ext.panel.Panel',{
				region:'center',
				id:'toppanel',
				border:0,
				margin:'-2 0 0 0',
				layout:'fit',
				items:[itemgroupmuster_me.mainPanel]
			})
			//得到图表数据
			itemgroupmuster_me.chartPanel = itemgroupmuster_me.getchartPanel(itemgroupmuster_me.echartDataList);
			itemgroupmuster_me.displayButton = Ext.widget("image",{
				xtype : 'image',
				src : rootPath+ "/components/querybox/images/downbig.png",
				width : 100,
				height : 8,
				style : 'cursor:pointer',
				listeners : {
					click : {
						element : 'el',
						fn : function() {
							if (itemgroupmuster_me.chartPanel.collapsed) {
								itemgroupmuster_me.chartPanel.expand();
								if(Ext.isIE)
									itemgroupmuster_me.changeChartType(0,itemgroupmuster_me.chartType);
								itemgroupmuster_me.displayButton.setSrc(rootPath+ "/components/querybox/images/downbig.png");
							} else {
								itemgroupmuster_me.chartPanel.collapse();
								itemgroupmuster_me.displayButton.setSrc(rootPath+ "/components/querybox/images/upbig.png");
							}

						},
						scope : itemgroupmuster_me
					}
				}
			});
			var bottompanel = Ext.create('Ext.panel.Panel',{
				id:'southpanel',
				region:'south',
				border:0,
				layout:'fit',
				width:'100%',
				items:[{
                	xtype:'container',
                	layout:{
                		type:'hbox',
        		    	pack:'center'
                	},
                	items:itemgroupmuster_me.displayButton
                },itemgroupmuster_me.chartPanel]
			});
			
			itemgroupmuster_me.panel = Ext.create('Ext.panel.Panel',{
	            border : 0,
	            layout:'border',
	            items: [
	            	toppanel,bottompanel
	            ],
	            tbar:toolbar_data
	        });
			if(itemgroupmuster_me.callBackFunc){
	            Ext.callback(eval(itemgroupmuster_me.callBackFunc),null,[itemgroupmuster_me.panel]);
			}
			
		}
	},
	/**
	 * 得到echart图表
	 */
	getchartPanel:function(echartDataList){
		textField = new Array(),
		valueField = new Array(),
		panel = new Object();
		itemgroupmuster_me.dataStore = Ext.create("Ext.data.Store",{
			fields:new Array(),
			data:echartDataList
		});
		
		var table = undefined;
		var chart = undefined;
		var panel = Ext.create("Ext.panel.Panel",{
			id:'chart_panel',
			width:'80%',
			bodyStyle:'border-top:0px;',
			header:false,
			columnChart:chart,
			piechartdata:echartDataList,
			chartType:itemgroupmuster_me.chartType,//统计图类型column、line、pie
			showChart:true,//是否显示统计图
			showType:'data',//=data提示显示数据，=percentage显示百分比
			layout:{
				type:'vbox',
				align:'center'
			},
			items:[{
					xtype:'component',
					id:'chartPanel',
					style:'text-align:center;border:2px solid red;',
					padding:'0 10 0 10',
					margin:'5 0 5 0',
					width:'99%',
					height:250,
					border:false,
					listeners : {
						'afterrender' : function() {
							itemgroupmuster_me.createChart(echartDataList,0,false,panel.chartType);
						}
					}
				}]
		});
		
		var toolbar = this.createMenu();
		panel.addDocked(toolbar);
		return panel;
	},
	createMenu:function(){
		var toolbar = Ext.widget("toolbar",{
			dock:'top',
			buttonAlign:'center',
			items:[{xtype:'radio',width:60,margin:'0 5 0 0',boxLabel:gz.label.analysisdata.columnchart,checked:true,listeners:{change:function(e, newValue, oldValue, eOpts){
				if(newValue){
					itemgroupmuster_me.chartType = "column";
					itemgroupmuster_me.changeChartType(0,'column');
				}
			}}},{xtype:'radio',width:60,boxLabel:gz.label.analysisdata.linechart,listeners:{change:function(e, newValue, oldValue, eOpts){
				if(newValue){
					itemgroupmuster_me.chartType = "line";
					itemgroupmuster_me.changeChartType(0,'line');
				}
			}}}]
		});
		return toolbar;
	},
	changeChartType:function(index,charttype){
		var panel = Ext.getCmp('chart_panel');
		itemgroupmuster_me.chart =  echarts.init(Ext.getDom('chartPanel'));
		if(itemgroupmuster_me.chart)
			itemgroupmuster_me.chart.dispose();
    	var data = itemgroupmuster_me.echartDataList;
		var vo = new HashMap();
		vo.put("rsdtlid",itemgroupmuster_me.rsdtlid);
		vo.put("rsid",itemgroupmuster_me.rsid);
		vo.put('showpercent',panel.showType);
		vo.put('name','');
		vo.put('data',data);
		vo.put('type',charttype);
		vo.put("transType","2");
		Rpc({functionId:'GZ00000708',async:false,success:function(form,action){
			var responseText = Ext.decode(form.responseText);
			var returnObj = Ext.decode(responseText.returnStr);
	        var return_code = returnObj.return_code;
	        var result = returnObj.return_data;
			var dataXml = result.dataHtml;
    		if(dataXml)
    			dataXml = dataXml.replace(/option =/,'').replace(/(^\s*)|(\s*$)/g,"");
    		if(dataXml.length > 0)
    			dataXml = dataXml.substring(0,dataXml.length-1);
			if(!itemgroupmuster_me.chart){
				var charset = Ext.Loader.config.scriptCharset;
				Ext.Loader.config.scriptCharset="UTF-8";
	    	    Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:function(){
	    	    	itemgroupmuster_me.drawChart(index,dataXml);
	    	    },scope:me});
	    	    Ext.Loader.config.scriptCharset=charset;		
			}
	    	else
	    		itemgroupmuster_me.drawChart(index,dataXml);
		},scope:itemgroupmuster_me}, vo);	
		
		var chartType = panel.chartType;
		panel.chartType = charttype;	
	},
	createChart:function(data,index,flag,type){
		var chart = undefined;
		var panel = Ext.getCmp('chart_panel');
		var vo = new HashMap();
		vo.put("rsdtlid",itemgroupmuster_me.rsdtlid);
		vo.put("rsid",itemgroupmuster_me.rsid);
		vo.put('data',data);
		vo.put('type',type);
		vo.put('showpercent',panel.showType);
		vo.put("transType","2");
		Rpc({functionId:'GZ00000708',async:true,success:function(form,action){
			var responseText = Ext.decode(form.responseText);
			var returnObj = Ext.decode(responseText.returnStr);
	        var return_code = returnObj.return_code;
	        var result = returnObj.return_data;
			var dataXml = result.dataHtml;
    		if(dataXml)
    			dataXml = dataXml.replace(/option =/,'').replace(/(^\s*)|(\s*$)/g,"");
    		if(dataXml.length > 0)
    			dataXml = dataXml.substring(0,dataXml.length-1);
			if(!itemgroupmuster_me.chart){
				var charset = Ext.Loader.config.scriptCharset;
				Ext.Loader.config.scriptCharset="UTF-8";
	    	    Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:function(){
	    	    	itemgroupmuster_me.drawChart(index,dataXml);
	    	    },scope:itemgroupmuster_me});
	    	    Ext.Loader.config.scriptCharset=charset;		
			}
	    	else
	    		itemgroupmuster_me.drawChart(index,dataXml);
		},scope:itemgroupmuster_me}, vo);		
	},
	drawChart:function(index,dataXml){
		itemgroupmuster_me.chart  = echarts.init(Ext.getDom('chartPanel'));
		if(dataXml&&dataXml!='')
			itemgroupmuster_me.chart.setOption(Ext.decode(dataXml));
    },
	queryData:function(){
		var map = new HashMap();
		map.put("year",itemgroupmuster_me.tYear);
		map.put("rsdtlid",itemgroupmuster_me.rsdtlid);
		map.put("rsid",itemgroupmuster_me.rsid);
		map.put("transType","1");
		map.put("codeitem",itemgroupmuster_me.codeitem);//分类指标
		map.put("codevalue",itemgroupmuster_me.codevalue);//分类指标值
		map.put("intoflag",'1');
		map.put("incloudLowLevel",itemgroupmuster_me.incloudLowLevel);
		map.put("accumulate",itemgroupmuster_me.accumulate);
		map.put("showMx",itemgroupmuster_me.showMx);
		map.put("group",itemgroupmuster_me.group);
		Rpc({functionId:'GZ00000708',async:false,success:itemgroupmuster_me.getTableDataOk,scope:itemgroupmuster_me},map);
	},
	/**
     * 调用页面设置控件
     */
    showpagesetting:function(rsid,rsdtlid){
        var map = new HashMap();
        map.put("rsid",itemgroupmuster_me.rsid);
        map.put("rsdtlid",itemgroupmuster_me.rsdtlid);
        map.put("opt","1");
        map.put("transType","3");
        Rpc({functionId : 'GZ00000708',success: function(form){
            var result = Ext.decode(form.responseText);
            Ext.create("EHR.exportPageSet.ExportPageSet",{rsid:itemgroupmuster_me.rsid,rsdtlid:itemgroupmuster_me.rsdtlid,
            	result:result,callbackfn:'itemgroupmuster_me.savePageSet'});
        }}, map);
    },
    /**
     * 保存页面设置
     * @param pagesetupValue
     * @param titleValue
     * @param pageheadValue
     * @param pagetailValue
     * @param textValueValue
     * @param type
     */
    savePageSet:function(pagesetupValue,titleValue,pageheadValue,pagetailValue,textValueValue,type) {
        var map = new HashMap();
        map.put("rsid",itemgroupmuster_me.rsid);
        map.put("rsdtlid",itemgroupmuster_me.rsdtlid);
        map.put("opt","2");
        map.put("transType","3");
        map.put("pagesetupValue",pagesetupValue);
        map.put("titleValue",titleValue);
        map.put("pageheadValue",pageheadValue);
        map.put("pagetailidValue",pagetailValue);
        map.put("textValueValue",textValueValue);
        Rpc({functionId : 'GZ00000708',success: function(form){
            var result = Ext.decode(form.responseText);
        }}, map);
    },
    /**
     * 导出Excel
     */
    exportExcel:function(){
        var map = new HashMap();
        map.put("year",itemgroupmuster_me.tYear);
		map.put("rsdtlid",itemgroupmuster_me.rsdtlid);
		map.put("rsid",itemgroupmuster_me.rsid);
		map.put("transType","4");
		map.put("codeitem",itemgroupmuster_me.codeitem);//分类指标
		map.put("codevalue",itemgroupmuster_me.codevalue);//分类指标值
		map.put("incloudLowLevel",itemgroupmuster_me.incloudLowLevel);
		map.put("accumulate",itemgroupmuster_me.accumulate);
		map.put("showMx",itemgroupmuster_me.showMx);
		map.put("group",itemgroupmuster_me.group);
		map.put("chartType",itemgroupmuster_me.chartType);
		map.put("tableName",itemgroupmuster_me.tableName);
        Rpc({
            functionId: 'GZ00000708', async: true, success: function (form,action) {
            	var responseText = Ext.decode(form.responseText);
    			var returnObj = Ext.decode(responseText.returnStr);
    	        var return_code = returnObj.return_code;
    	        var result = returnObj.return_data;
                var fieldName = getDecodeStr(result.fileName);
                window.location.target="_blank";
                window.location.href = "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true";
            },
            scope: this
        }, map);

    },
    getMainView:function(){
		return itemgroupmuster_me.panel;
	},
	/**
     * 设置取数范围
     */
    setRange:function(){
        //将页面作为窗口展现出来
        var panel = Ext.create("Analysistable.OptAnalysisTable",{
                opt:3,
                imodule:itemgroupmuster_me.imodule,
                rsid:itemgroupmuster_me.rsid_d,
                rsdtlid:itemgroupmuster_me.rsdtlid_d,
                callBack:function(){
                    Ext.getCmp("setrangewin").close();
                    itemgroupmuster_me.tYear = "";
                	var map = new HashMap();
            		map.put("year",itemgroupmuster_me.tYear);
            		map.put("rsid",itemgroupmuster_me.rsid);
            		map.put("rsdtlid",itemgroupmuster_me.rsdtlid)
            		map.put("transType","5");
            		Rpc({functionId:'GZ00000708',async:false,success:function(form,action){
            			var responseText = Ext.decode(form.responseText);
            		    var returnObj = Ext.decode(responseText.returnStr);
            	        var result = returnObj.return_data;
            	        var yearselectjson = result.yearselectjson;
            	        var salaryItemList = result.salaryItemList;
            	        var yearStore = Ext.data.StoreManager.lookup("selectStore");
            	        var salaryItemStore = Ext.data.StoreManager.lookup("salaryItemStore");
            	        yearStore.loadData(yearselectjson);
            	        salaryItemStore.loadData(salaryItemList);
            	        var yearCombo = Ext.getCmp("selectPanel");
            	        var combo1 = Ext.getCmp("combo1");
            	        var combo2 = Ext.getCmp("combo2");
            	        if(combo2){
            	        	combo2.setValue("");
            	        }
            	        itemgroupmuster_me.codeitem = "";
			    		itemgroupmuster_me.codevalue = "";
			    		if(itemgroupmuster_me.group=='1')
			    			itemgroupmuster_me.codevalue = "-1";
            	        var checkbox3 = Ext.getCmp("checkbox3");
            	        var count = yearStore.getCount();
            	        if(count>0){
            	        	yearCombo.select(yearStore.data.items[0]);
            	        	itemgroupmuster_me.tYear = yearStore.data.items[0].id;
            	        }else{
            	        	yearCombo.select("");
            	        	itemgroupmuster_me.tYear = "-1";
            	        }
            			itemgroupmuster_me.queryData();
            		},scope:itemgroupmuster_me},map);
                }
            });
        OptAnalysisTable_me.setRange();
    },
    addGridListens:function(grid){
    	grid.tablePanel.on('columnresize',function(ct, column, width, eOpts){
			var dataIndex = column.dataIndex;//修改的列codeitemid
			var map = new HashMap();
			map.put("codeitemid",dataIndex);
			map.put("submoduleid","itemgroupmusterdata_"+itemgroupmuster_me.rsdtlid);
			map.put("width",width+"");
			map.put("isshare","0");//0 私有方案 1共有方案
			map.put("transType","6");
			Rpc({functionId:'GZ00000708',async:false,success:function(){},scope:itemgroupmuster_me},map);
		});
    	grid.tablePanel.on('columnmove',function(ct, column, width, eOpts){
    		itemgroupmuster_me.saveColumnMove(grid, column);
		});
		
		grid.tablePanel.on('columnlockmove',function(ct, column, width, eOpts){
			itemgroupmuster_me.saveColumnMove(grid, column);
		});
    },
 // 调整顺序
	saveColumnMove:function(grid, column) {
		var tablePanel = grid.tablePanel;
		var is_lock = column.isLocked()?'1':'0';
		var index = tablePanel.getColumnManager().getHeaderIndex(column);
		var nextcolumn = tablePanel.getColumnManager().getHeaderAtIndex(index+1);
		var nextid = "-1";
		if(nextcolumn && nextcolumn.dataIndex)
			nextid = nextcolumn.dataIndex;
		
	    var map = new HashMap();
	    map.put("submoduleid","itemgroupmusterdata_"+itemgroupmuster_me.rsdtlid);
	    map.put("nextid", nextid);
	    map.put("transType", "7");
	    map.put("is_lock", is_lock);
	    map.put("itemid", column.dataIndex);
	    map.put("rsid", itemgroupmuster_me.rsid);
	    map.put("rsdtlid", itemgroupmuster_me.rsdtlid);
	    Rpc({
	        functionId: 'GZ00000708', async: true, success: function (res) {
	        	
	        },
	        scope: this
	    }, map);
	}
})