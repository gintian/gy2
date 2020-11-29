/**
标准表
lis 2016-01
**/
Ext.define('EHR.defineformula.Standard',{
		requires:['EHR.extWidget.proxy.TransactionProxy'],
       constructor:function(config){
			satndard_me = this;
			satndard_me.salaryid = config.salaryid;//薪资类别id
			satndard_me.itemid = config.itemid;//计算公式id
			satndard_me.standardID = config.standardID;//标准表id
			satndard_me.itemname = config.itemname;//计算公式名称
			satndard_me.runflag = config.runflag;//执行类别
			this.init();
	    },
	    
	    //初始化获取相关参数
	    init:function(){
	    	//加载样式
	    	Ext.util.CSS.removeStyleSheet('whiteLine');
	    	Ext.util.CSS.createStyleSheet(".standard-table{border-collapse: separate;border-style: solid;"
	    				+"border-width: 1px 0 0;"
	    				+"border-color: #ededed;"
	    				+"color: #000;"
	    				+"font: normal 12px/13px 微软雅黑,宋体,tahoma,arial,verdana,sans-serif;"
	    				+"background-color: #fff;}","whiteLine");//表格样式
	    	Ext.util.CSS.removeStyleSheet('header');
	    	Ext.util.CSS.createStyleSheet(".standard-header-ct{border: 1px solid #c5c5c5;"
    				+"border-bottom-color: #c5c5c5;"
    				+"background-image: none;"
    				+"background-color: #f9f9f9;}","header");//行样式
	    	Ext.util.CSS.removeStyleSheet('columnheader');
	    	Ext.util.CSS.createStyleSheet(".standard-column-header{text-align: center;"
    				+"padding: 4px 6px 5px;"
    				+"white-space: nowrap;"
    				+"position: relative;"
    				+"overflow: hidden;"
    				+"border-right: 1px solid #c5c5c5;"
    				+"border-bottom: 1px solid #c5c5c5;"
    				+"color: black;"
    				+"font: normal 12px/13px 微软雅黑,宋体,tahoma,arial,verdana,sans-serif;"
    				+"outline: 0;"
    				+"background-image: none !important;"
    				+"background-color: #fafafa;}","columnheader");//列头单元格样式
	    	Ext.util.CSS.removeStyleSheet('cell');
	    	Ext.util.CSS.createStyleSheet(".standard-grid-cell{border-style: solid;"
	    			+"border-color: #d0d0d0;"
	    			+"border-width: 0 1px 1px 0;"
	    			+"overflow: hidden;"
	    			+"vertical-align: middle;"
    				+"height: 30px;}","cell");//单元格样式

	    	var map = new HashMap();
			map.put("standardID",satndard_me.standardID);
		    Rpc({functionId:'ZJ100000087',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					satndard_me.initPanel(result);
					
				}else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
					Ext.MessageBox.show({  
	                    title : common.button.promptmessage,  
	                    msg : result.message,  
	                    buttons: Ext.Msg.OK,
	                    icon: Ext.MessageBox.INFO  
					});
				}
		    }},map); 
	    },
	    
	    //初始化标准表panel
	    initPanel:function(result){
	    	var panelWidth = 200;
	    	var panelPadding = '10';
	    	var standardTable = Ext.getCmp('standard');//标准表
	    	if(standardTable){//如果存在则销毁
				standardTable.destroy();
			}
	    	//横向子栏目指标
	    	var s_hfactor_name = result.s_hfactor_name;
	    	if(s_hfactor_name != "")
	    		s_hfactor_name = "| " + s_hfactor_name;
	    	//纵向子栏目指标
	    	var s_vfactor_name = result.s_vfactor_name;
	    	if(s_vfactor_name != "")
	    		s_vfactor_name = "| " + s_vfactor_name;
	    	
	    	var standardTable = Ext.widget('fieldset', {
		    	id:'standard',		
		    	title:common.label.standardTable,//标准表
		    	layout:'vbox',
		    	width:500,
			    height:420,	
				items:[
				       {
				    		xtype:'container',
				    		padding:'3 0 0 0',
				    		width:480,
				    		layout: {
				    	        type: 'hbox',
				    	        align: 'stretch'
				    	    },
				    		items:[{
				    			xtype:'container',//标准表下拉框
				    			id: 'tbarId'
				    			
				    		},{
				    			xtype:'tbfill'
				    		},
			    	          {
			    	        		  xtype:'label',
			    	        		  hidden:satndard_me.standardID==""?true:false,
			    	        		  text:common.label.resultField+result.desc //结果指标
			    	          } ]
				    		
				       },
				       {
				 	   	xtype:'panel',
				 	   	width:477,
				 	   	height:330,	
				 	   	scrollable:true,
				        html:result.gzStandardItemHtml
				       },{
				    	   xtype:'panel',
				    	   padding:'5 0 0 0',
				    	   height:40,
				    	   width:485,
				    	   border:false,
				    	   hidden:satndard_me.standardID==""?true:false,
				    	   layout: {
				    	        type: 'vbox',
				    	        align: 'stretch',
				    	        pack:'left'
				    	    },
				    	   items:[
				    	          {
				    	        	  xtype:'label',
			    	        		  text:common.label.lrtyperow+result.hfactor_name+s_hfactor_name//横向：
				    	          },
				    	          {
				    	        	  xtype:'label',
			    	        		  text:common.label.lrtypeline+result.vfactor_name+s_vfactor_name//纵向：
				    	          }
				    	   ]
				       }
				]
				
		    });
			var centerPanel = Ext.getCmp('centerPanel');
			centerPanel.add(standardTable);
			this.initComBoBox(result.gzStandardName);
	    },
	    
	    //当前指标对应的标准表list
	    initComBoBox:function(gzStandardName){
	    	//代码类数据store
			var standardComStore = Ext.create('Ext.data.Store',
					{
						fields:['dataName','dataValue'],
						proxy:{
						    	type: 'transaction',
						    	functionId:'ZJ100000088',
						        extraParams:{
						        	itemname:satndard_me.itemname
						        },
						        reader: {
						            type: 'json',
						            root: 'standardlist'         	
						        }
						},
						autoLoad: true     
					});
			
			//数据类型的下拉框对象
			var standardComBox = Ext.create('Ext.form.ComboBox', {
					id:'ntype_id',
					store:standardComStore,
					value:gzStandardName,
					editable:false,
					displayField:'dataName',
					valueField:'dataValue',
					queryMode:'local',
					listeners:{
						'select':function(combox,records){//选择数据类型时清空代码类
								satndard_me.standardID = combox.getValue();
								satndard_me.init(); 
								satndard_me.saveStandard();
						}
					}
			});
			
			Ext.getCmp('tbarId').add(standardComBox);
	    },
	    
	    saveStandard:function(){
	    	var map = new HashMap();
			map.put("salaryid",satndard_me.salaryid);
			map.put("itemid",satndard_me.itemid);
			map.put("standardid",satndard_me.standardID);
			map.put("runflag",satndard_me.runflag);
		    Rpc({functionId:'ZJ100000089',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					
				}else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
					Ext.MessageBox.show({  
	                    title : common.button.promptmessage,  
	                    msg : result.message,  
	                    buttons: Ext.Msg.OK,
	                    icon: Ext.MessageBox.INFO  
					});
				}
		    }},map); 
	    }
       
});