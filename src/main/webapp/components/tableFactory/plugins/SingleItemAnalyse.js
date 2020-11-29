Ext.define("EHR.tableFactory.plugins.SingleItemAnalyse",{
	requires:["EHR.extWidget.proxy.TransactionProxy"],
	subModuleId:'',
	code:'root',
	chartType:11,
	analyseType:1,
	itemType:'',
    doChild:false,
	itemTypeFormat:'0',
	columns:undefined,
	activeColumn:undefined,
	firstAnalyse:true,
	
	itemTypeCmp:undefined,
	
	constructor:function(column,subModuleId,analyseBusiId,columns){
		this.activeColumn = column;
		this.subModuleId = subModuleId;
		this.columns = columns;
		this.loadData();
	},
	
	loadData:function(){
		
		this.createTreePanel();
		this.createAnalysePanel();
    },
    
    createTreePanel:function(req){
    	var me = this;
    	
    	var dataStore = {};
    	if(me.activeColumn.operationData){
    	    var opd = me.activeColumn.operationData;
    	    var data = [];
    	    for(var i=0;i<opd.length;i++)
    	       data.push({id:opd[i].dataValue,text:opt[i].dataName});
    	    dataStore.data = data;
    	}else{
    	    dataStore.proxy = {
    	    		type:'transaction',
			functionId:'ZJ100000131',
			extraParams:{
				codesetid:me.activeColumn.codesetid,
				nmodule:me.activeColumn.nmodule,
				ctrltype:me.activeColumn.ctrltype,
				codesource:me.activeColumn.codesource,
				checkroot:true,
				multiple:true
			}
    	    };
    	}
    	me.tree =  Ext.widget("treepanel",{
    		width:170,
    		rootVisible:false,
    		split:false,
    		region:'west',
    		border:false,
    		bodyStyle:"border-top:none",
    		store:dataStore/*{
    			proxy:{
		    		type:'transaction',
					functionId:'ZJ100000131',
					extraParams:{
							codesetid:me.activeColumn.codesetid,
							nmodule:me.activeColumn.nmodule,
							ctrltype:me.activeColumn.ctrltype,
							codesource:me.activeColumn.codesource,
							checkroot:true,
							multiple:true
					}
        		}
    		}*/,
    		listeners:{
    			checkchange:function(){
    				me.doAnalyse();
    			},
    			load:function(store){
    				if(me.firstAnalyse)
    				   me.doAnalyse();
    				me.firstAnalyse = false;
    			},
    			containercontextmenu:function(tree,e){
    				var root = this.getStore().getRootNode();
    				me.selectAllChild(tree,root,e);
    							
    			},
    			itemcontextmenu:function(tree,r,i,i2,e){
    				me.selectAllChild(tree,r,e);
    			}
    		}
    		
    	});
    },
    
    createAnalysePanel:function(){
    	var me = this;
    	var tools = new Array();
    	//数值型指标 数组
    	var numItems = new Array();
    	var column = undefined;
    	for(var i=0;i<me.columns.length;i++){
    		column = me.columns[i];
    		if(column.columnType!='N')
    			continue;
    		numItems.push({name:column.columnDesc,value:column.columnId,format:column.format});
    	}
    	//如果没有数值型指标，则不用显示统计方式和统计项目了，直接按照个数统计
    	if(numItems.length>0){
    		//统计方式选项
    		var analyseTypeCmp = Ext.widget('combo',{
        		fieldLabel:'统计方式',
        		labelSeparator:'', //去除label中的冒号 changxy20160525
        		labelWidth:60,
        		width:120,//修改下拉列表的宽度
        		editable:false,
        		 displayField: 'name',
        		 valueField: 'value',
        		 labelAlign:'right',
        		 value:1,
        		 store:{
        			 fields:['name','value'],
        			 data:[{name:'个数',value:1},{name:'最大',value:2},{name:'最小',value:3},{name:'平均',value:4},{name:'求和',value:5}]
        		 }
        	});
    		analyseTypeCmp.on('select',me.changeAnalyseType,me);
        	tools.push(analyseTypeCmp);
        	//统计项目（指标） 选项
        	this.itemTypeCmp =  Ext.widget('combo',{
        		fieldLabel:'统计项目',
        		labelWidth:60,
        		labelSeparator:'',//去除冒号
        		editable:false,
        		 displayField: 'name',
        		 valueField: 'value',
        		 labelAlign:'right',
        		 store:{
        			 fields:['name','value','format'],
        			 data:numItems
        		 },
        		 hidden:true
        	});
        	this.itemTypeCmp.on('select',me.changeItemType,me);
        	tools.push(this.itemTypeCmp);
    	}
    	
    	//图形选项
    	var chartTypeCmp = Ext.widget('combo',{
    		fieldLabel:'图形', 
    		labelSeparator:'',//去除label中的冒号 changxy20160525
    		labelWidth:60,
    		width:150,//修改下拉列表的宽度
    		editable:false,
    		 displayField: 'name',
    		 valueField: 'value',
    		 labelAlign:'right',
    		 value:11,
    		 store:{
    			 fields:['name','value'],
    			 data:[{name:'平面柱图',value:11},{name:'平面饼图',value:20},{name:'折线图',value:4}]
    		 }
    		
    	});
    	chartTypeCmp.on('select',me.changeChartType,me);
    	tools.push(chartTypeCmp);
    	
    	//包含下级 选项
    	var doChildCmp = Ext.widget("checkbox",{
    		boxLabel  : '包含下级',
    		style:'margin-left:20px', //缩小边距 防止 挤出 changxy
    		listeners:{
    			change:function(a){
    				me.doChild = a.checked;
    				me.loadChartData();
    			}
    		}
    	});
    	tools.push(doChildCmp);
    	this.analyse = Ext.widget("panel",{
    		region:'center',
    		border:1,
    		bodyStyle:'border-width:0 0 0 1px',
    		defaultType:'panel',
    		layout:{type:'vbox',align:'stretch'},
    		items:[
    		       {
    		    	   height:50,layout:{type:'hbox',pack:'center',align:'middle'},border:false,
    		    	   items:tools
    		       },
    		       {xtype:'component',id:'chartPanel',flex:10,style:'text-align:center;border:1px solid red;',padding:'20 10 10 10',border:false,html:'无数据'}
    		      ]
    	});
    },
    getTree:function(){
    	return this.tree;
    },
    getAnalyse:function(){
    	return this.analyse;
    },
    doAnalyse:function(){
    	var codesArray = this.tree.getChecked();
    	this.code = undefined;
    	if(codesArray.length<1){
    		this.dataXml='';
    		this.drawChart();
    		return;
    	}
    	this.code='';
    	for(var i=0;i<codesArray.length;i++){
    		this.code+=codesArray[i].data.id+",";
    	}
    	this.loadChartData();
    },
    setAnalyseData:function(req){
    	var me = this;
    	me.dataXml = Ext.decode(req.responseText).dataHtml;
    	if(me.dataXml)
    		me.dataXml = me.dataXml.replace(/option =/,'').replace(/(^\s*)|(\s*$)/g,"");
    	if(me.dataXml.length > 0){
			me.dataXml = me.dataXml.substring(0,me.dataXml.length-1);
		}
		if(!this.chart){
    	   //Ext.Loader.loadScript({url:'/echarts/shine.js'});
    	   var charset = Ext.Loader.config.scriptCharset;
		   Ext.Loader.config.scriptCharset="UTF-8";
    	   Ext.Loader.loadScript({url:'/echarts/echarts.min.js',onLoad:this.drawChart,scope:me});
    	   Ext.Loader.config.scriptCharset=charset;		
    	}else{
    		this.drawChart();
    	}
    	
    },
    drawChart:function(){
    	
    	/*if(!this.chart){
    		
    		this.chart =  echarts.init(Ext.getDom('chartPanel'));
    		
    		this.chart.setOption(Ext.decode(this.dataXml));
    		
    	}else{
    		this.chart.setOption(Ext.decode(this.dataXml));
    	}*/
		if(this.chart){
			this.chart.dispose();
		}
		if(this.dataXml==''){
			//加上这个提示信息IE兼容模式会出问题【60321】
			// Ext.getDom('chartPanel').innerHTML="无数据";
			return;
    	}
		this.chart =  echarts.init(Ext.getDom('chartPanel'));

		this.chart.setOption(Ext.decode(this.dataXml));
    },
    changeAnalyseType:function(combo,record){
    	if(record.data.value!=1 && this.itemTypeCmp){
    		this.itemTypeCmp.setVisible(true);
    		this.analyseType = record.data.value;
    		if(this.itemType=='')
    			return;
    		
    	}
    	if(record.data.value==1 && this.itemTypeCmp){
    		this.itemTypeCmp.setVisible(false);
    		this.analyseType = 1;
    	}
    	this.loadChartData();
    },
    changeItemType:function(combo,record){
    	this.itemType = record.data.value;
    	this.itemTypeFormat = record.data.format;
    	this.loadChartData();
    },
    changeChartType:function(combo,record){
    	this.chartType = record.data.value; //
    	this.loadChartData();
    },
    loadChartData:function(){
    	if(!this.code){
    		Ext.Msg.alert("提示信息","请选择统计指标！");
    		return;
    	}
    	if(this.analyseType!=1 && this.itemType.length<1){
    		Ext.Msg.alert("提示信息","请选择统计项目！");
    		return;
    	}
    	var req = new HashMap();
    	req.put("itemid",this.activeColumn.dataIndex);
    	req.put("itemdesc",this.activeColumn.text);
    	req.put("codesetid",this.activeColumn.codesetid);
    	req.put("subModuleId",this.subModuleId);
    	req.put("code",this.code);
    	req.put("chartType",this.chartType);
    	req.put("analyseType",this.analyseType);
    	req.put("doChild",this.doChild);
    	req.put("itemType",this.itemType);
    	req.put("itemTypeFormat",this.itemTypeFormat);
    	Rpc({functionId:'9030000002',success:this.setAnalyseData,scope:this},req);
    	
    },
    selectAllChild:function(tree,node,event){
    	var me = this;
    	Ext.create('Ext.menu.Menu',{
			items:[{
				text:'全选',
				handler:function(){
			    	for(var i=0;i<node.childNodes.length;i++){
			    		node.childNodes[i].set("checked",true);
    			    }
			    	me.doAnalyse();
				}
			},{
				text:'全撤',
				handler:function(){
			    	for(var i=0;i<node.childNodes.length;i++){
			    		node.childNodes[i].set("checked",false);
    			    }
			    	me.doAnalyse();
				}
			}]
		}).showAt(event.getXY());
    }
});