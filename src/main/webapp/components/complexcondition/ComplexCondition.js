 /*
 *复杂条件组件
 *lis
 *2015-12-29
 *
 *调用示例：
 	Ext.require('EHR.complexcondition.ComplexCondition',function(){
 		Ext.create("EHR.complexcondition.ComplexCondition",{imodule:imodule,opt:"2",callBackfn:'GzGlobal.getCon'});
 	});
 * imodule 模块号，3：薪资类别（已经使用）,11：考勤排班
 * 
 * opt 调用标示，0：薪资类别-薪资项目（已经使用）,1:薪资类别=薪资属性-复杂条件
 * dataMap:HashMap对象，存放不同模块需要的参数,最后回调方法会将此回传回去
 * title:弹出框title
 * callBackfn 为回调函数，函数名自定义，参数为conTextareaId文本域的值。
 *	itemType 返回数据类型
 *	formula 公式表达式
 */
Ext.define('EHR.complexcondition.ComplexCondition',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	itemType:'',//返回数据类型
    formula:'',//公式表达式
    title:'',//弹出框title
    opt:-1,//调用标示
    inforKindFlag:'',//下拉列表需要查询哪些子集数据，以逗号分隔，可以只写A 或者K 例如 A01,K01,K 兼容旧代码 为""时查全部
    dataMap:null,
    imodule:'',
    fieldItem_List:null,//传入可选择的指标 格式Array: ['A0177:XXXXX','A0137:XXXXX']
    constructor:function(config){
		condition_me = this;
		condition_me.imodule = config.imodule;
		condition_me.opt = config.opt;
		condition_me.title = config.title;
		condition_me.dataMap = config.dataMap;
		condition_me.callBackfn = config.callBackfn;
        condition_me.inforKindFlag = config.inforKindFlag?config.inforKindFlag:"";
        condition_me.imodule = config.imodule;
        condition_me.formula = config.formula;
        condition_me.itemtype = config.itemtype;
        condition_me.fieldItem_List=config.fieldItem_List;

		var dataMap = config.dataMap;
		if(condition_me.imodule=='3') {
            if (config.dataMap) {
                condition_me.salaryid = dataMap.salaryid;//薪资类别id
                condition_me.fieldid = dataMap.fieldid;//薪资项目id
                condition_me.initflag = dataMap.initflag;//2:导入项，1:积累项
                condition_me.itemtype = dataMap.itemtype;//数据类型
                condition_me.formula = dataMap.express;//公式表达式
            }
        }
        if(condition_me.fieldItem_List!=null){
			var list=new Array();
			Ext.each(condition_me.fieldItem_List,function (str,index) {
				list[index]={dataName:str,dataValue:str.split(':')[0]};
            });
            condition_me.fieldItem_List=list;
		}
		
    	condition_me.heapFlag ="";
    	condition_me.selectionStart = 0;//光标起选中始位置
		condition_me.selectionEnd = 0;//光标选中结束位置
		condition_me.selectionIndex = 0;//光标位置
		
    	var map = new HashMap();
		map.put("fieldid",condition_me.fieldid);
		map.put("salaryid",condition_me.salaryid);
		map.put("imodule",condition_me.imodule);
		map.put("opt",condition_me.opt);
		map.put("initflag",condition_me.initflag);
		map.put("inforKindFlag",condition_me.inforKindFlag);
	    Rpc({functionId:'ZJ100000093',async:false,success:function(form,action){
	    	 var result = Ext.decode(form.responseText);  
	    	 if(result.succeed){
	    		 if(result.formula)
	    			 condition_me.formula = decode(result.formula);
	    		 condition_me.heapFlag = result.heapFlag;
	    		 if(result.itemtype)
	    			 condition_me.itemtype = result.itemtype;
	    		 condition_me.fieldSetList = result.fieldSetList;//子集数据集合
	    		 condition_me.fieldItemList = result.fieldItemList;//指标数据集合

				 if(condition_me.fieldItem_List!=null)
	    		 	condition_me.fieldItemList = condition_me.fieldItem_List;//指标数据集合

	    		 condition_me.codeItemList = result.codeItemList;//代码数据集合
	    	 }else {
				Ext.showAlert(result.message);  
			 }
		}},map);
	    
	    this.init(); //初始化显示窗口
	    this.initFormulaPanel();//初始化公式表达式面板
	    this.initButtons();//初始化计算按钮面板
	    this.initComBox();//初始化下拉框面板
    },
    
    /**--------------------- 页面显示 start --------------------*/
    init:function()
	{
		//显示面板
		var expression = Ext.create('Ext.panel.Panel', {
			width: 591,
			height: 400,
			id:'expression',		
			layout: 'border',
			border:false,
			bodyStyle: 'background:#ffffff;',
			items: [
	             { region: "north",border:false,id:'northId',height:215,style: {
					marginTop: '1px'
	        	 }},
	        	 { region: "west",border:false,width:312,id:'westId'},
	             { region: "center",border:false,margin: '0 0 0 10',id:'centerId'},
	        	 { region: "east",border:false, width: 1}
	        	 
			]
		});

		//弹出窗口
		condition_me.win=Ext.widget("window",{
	          title:condition_me.title,  
	          width:600,
	          //animateTarget :'pa',
			  modal:true,
			  resizable:false,
			  closeAction:'destroy',
			  items: [{
		  		xtype:'panel',
         		border:false,
				items:[expression]
	          }],
	          bbar: [
	                 {xtype:'tbfill'},
         			 {
     					xtype:'button',text:common.button.ok,//确定
     					style:'margin-right:5px',
	                	handler:function(){
	                	 	 condition_me.checkComplexCond();
						}
	         		 },
	                 { 
	                 	type: 'button', text:common.button.cancel,
	         			handler:function(){
                	 		condition_me.win.close();
     					} 
	                 },
	         		{xtype:'tbfill'}
	         ]
	    });   
		
		condition_me.win.on('show', function(){
			condition_me.win.getEl().setStyle('z-index','99999');
		})
		condition_me.win.show();   
	},

	//初始化存放表达式panel
	initFormulaPanel:function(){
		//显示公式表达式的panel
		var formula = Ext.create('Ext.panel.Panel', {
			border:false,
			buttonAlign:'right',
	        items:[{
	         	border:false,
				xtype:'textareafield',
				name:'formula',
				id:'shry',
				value:condition_me.formula,
				width:590,
				height:180,
				enableKeyEvents:true,
				listeners:{
	        		afterrender:function(textarea){
	        			if(Ext.isIE){//ie 下绑定mouseleave事件
	        				textarea.getEl().on("mouseleave",function(){
	        					//condition_me.getCursorPosition();//获得光标位置
	        				})
	        			}
	        		},
	        		change:function(){
	        			if(Ext.isIE){
	        				//condition_me.getCursorPosition();//获得光标位置   
	        			}
	        		},
	        		keyup:function(textarea,e){
	        			if(Ext.isIE){
	        				condition_me.getCursorPosition();//获得光标位置     lis update2016-7-5
	        			}
	        		},
	        		 click: {
	                    element: 'el',
	                    fn: function(){ 
		        			if(Ext.isIE){
		        				condition_me.getCursorPosition(); //获得光标位置
		        			}
	        			}
	                }
	        	}
	        }],
	        buttons: [
	                  //常用条件
	                  {
	                	  text:common.button.commonConditions,//常用条件
	                	  hidden:condition_me.imodule=="3"&&condition_me.opt=="1"?false:true,
	                	  handler:function(){
		                	  Ext.require('EHR.complexcondition.GeneralCondition',function(){
		              			Ext.create("EHR.complexcondition.GeneralCondition");
		              		  })
	                  	  }
	                  },
	                  //保存条件
	                  {
	                	  text:common.button.saveConditions, //"保存条件"
	                	  hidden:condition_me.imodule=="3"&&condition_me.opt=="1"?false:true,
	                	  handler:function(){
	                	    var map = new HashMap();
	                	    //验证公式是否为空
	                	     if( Ext.util.Format.trim(Ext.getCmp('shry').getValue()).length==0){
                                Ext.showAlert(common.msg.formulanoNull);
                                return;
                            }
	                	    map.put("c_expr",encode(Ext.getCmp('shry').getValue()));
	                	    map.put("type","2");
	                	    map.put("module","2");
	                     	Rpc({functionId:'ZJ100000077',success:function(form,action){
	                     		var result = Ext.decode(form.responseText);
	            				var base = result.base;
	            				if(base == 'ok'){
	            					condition_me.saveFormula();
	            				}else{
	            					Ext.showAlert(getDecodeStr(result.base));
	            				}
	                     	}},map);  
	                  	  }
	                  },
	                  //函数向导
	                  {
	                	  text:common.button.functionGuide,
						  height:22,
	                	  handler:function(){condition_me.functionWizard();}
	                  }
	                  ]
	   	});
		Ext.getCmp('northId').add(formula);
	},
	
	//参考项目中下拉框
	initComBox:function(){
		//子集数据store
		var fieldSetStore = Ext.create('Ext.data.Store',
		{
			fields:['dataName','dataValue']
		});
		//指标数据store
		var fieldItemStore = Ext.create('Ext.data.Store',
		{
			fields:['dataName','dataValue'],
			proxy:{
			    	type: 'transaction',
			    	functionId:'ZJ100000093',
			        extraParams:{
		        		imodule:condition_me.imodule,
		        		opt:condition_me.opt,
		        		flag:'1'
			        },
			        reader: {
			            type: 'json',
			            root: 'list'         	
			        }
			}
		});

        //代码数据store
        var codeItemStore = Ext.create('Ext.data.Store',
            {
                fields: ['dataName', 'dataValue'],
                proxy: {
                    type: 'transaction',
                    functionId: 'ZJ100000093',
                    extraParams: {
                        imodule: condition_me.imodule,
                        opt: condition_me.opt,
                        flag: '2'
                    },
                    reader: {
                        type: 'json',
                        root: 'list'
                    }
                }
            });
		
		 Ext.each(condition_me.fieldSetList,function(obj,index){
			 fieldSetStore.insert(index,{dataName:obj.dataName,dataValue:obj.dataValue});
		 });
		 
		 Ext.each(condition_me.fieldItemList,function(obj,index){
			 fieldItemStore.insert(index,{dataName:obj.dataName,dataValue:obj.dataValue});
		 });
		 
		 Ext.each(condition_me.codeItemList,function(obj,index){
			 codeItemStore.insert(index,{dataName:obj.dataName,dataValue:obj.dataValue});
		 });
		 
		 var width = 230;
		 var labelWidth = 30;
			
		// 格式下拉框
		var heapFlagCom = null;
		if(condition_me.imodule == "3"&&condition_me.opt == "0") {
            var conditionStore = null;
            if(condition_me.initflag == '2'){//导入项
                conditionStore = Ext.create('Ext.data.Store', {
                    fields:['dataName','dataValue'],
                    data : [
                        {"dataValue":"0", "dataName":common.label.currentRecord},//当前记录
                        {"dataValue":"1", "dataName":common.label.firstInMonth},//月内最初第一条
                        {"dataValue":"2", "dataName":common.label.lastInMonth},//月内最近第一条
                        {"dataValue":"3", "dataName":common.label.lessFirstInMonth},//小于本次月内最初第一条
                        {"dataValue":"4", "dataName":common.label.lesslastInMonth},//小于本次月内最近第一条
                        {"dataValue":"5", "dataName":common.label.sameCountInMonth},//同月同次
                        {"dataValue":"6", "dataName":common.label.deductedIssued}//扣减同月已发金额
                    ]
                });
            }else{//积累项
                conditionStore = Ext.create('Ext.data.Store', {
                    fields:['dataName','dataValue'],
                    data : [
                        {"dataValue":"0", "dataName":common.label.notCumulative},//不累积
                        {"dataValue":"1", "dataName":common.label.accumulationInMonth},//月内累积
                        {"dataValue":"2", "dataName":common.label.accumulatedInQuarter},//季度内累积
                        {"dataValue":"3", "dataName":common.label.accumulatedYears},//年内累积
                        {"dataValue":"4", "dataName":common.label.accumulationUnconditional},//无条件累积
                        {"dataValue":"5", "dataName":common.label.accumulationSCInQuarterr},//季度内同次累积
                        {"dataValue":"6", "dataName":common.label.accumulationSCInYear},//年内同次累积
                        {"dataValue":"7", "dataName":common.label.accumulationInSame},//同次累积
                        {"dataValue":"8", "dataName":common.label.lessAccumulationInMonth}//小于本次的月内累积
                    ]
                });
            }

            heapFlagCom = Ext.widget({
                xtype: 'combobox',
                fieldLabel: common.label.pattern,//格式
                labelSeparator: '',//去掉后面的冒号
                labelAlign: 'right',
                store: conditionStore,
                displayField: 'dataName',
                valueField: 'dataValue',
                margin: '5,0,0,0',
                editable: false,
                queryMode: 'local',
                labelWidth: labelWidth,
                width: width,
                value: condition_me.heapFlag,
                renderTo: Ext.getBody(),
                listeners: {
                    select: function (combo, record) {
                        condition_me.heapFlag = combo.getValue();
                    }
                }
            });
            heapFlagCom.select(condition_me.heapFlag);
        }
		
		//子集下拉框初始化显示的第一个值
		var fieldSetName = "";
		if(condition_me.fieldSetList)
			fieldSetName = condition_me.fieldSetList[0].dataName;
		//子集下拉框
		var fieldSetCom = Ext.widget({
   			id:'fieldSet_id',
   			xtype:'combobox',
   			fieldLabel:common.label.fieldset,//子集
   			labelAlign:'right',
   			store:fieldSetStore,
   			displayField:'dataName',
   			valueField:'dataValue',
			hidden:condition_me.fieldItem_List==null?false:true,
   			value:fieldSetName,
   			labelSeparator :'',//去掉后面的冒号
   			margin:'5,0,0,0',
   			editable:false,
   			queryMode:'local',
   			labelWidth:labelWidth,
   			width:width,
   			listeners:{
   				select:function(combo,record){
					fieldItemStore.load({
						params:{
							value:combo.value,
							flag:'1'
						},
						callback: function(record, option, succes){
							fieldItemCom.setValue("");
							Ext.getCmp('codeItem_id').hide(); 
						}
					});
				}
			},
			renderTo: Ext.getBody()
   		});
		
		if(condition_me.initflag == '1'){//如果是积累项则只显示指标下拉框
			fieldSetCom.hide();
		}
		
		//指标下拉框
		var fieldItemCom = Ext.widget({
   			id:'fieldItem_id',
   			xtype:'combobox',
   			fieldLabel:common.label.item,//指标
   			store:fieldItemStore,
   			displayField:'dataName',
   			valueField:'dataValue',
   			editable:false,
   			margin:'5,0,0,0',
   			queryMode:'local',
   			labelWidth:labelWidth,
   			labelSeparator :'',//去掉后面的冒号
   			width:width,
   			labelAlign:'right',
   			listeners:{
   				select:function(combo,record){
   					var dataName = record.get('dataName');
					condition_me.symbol('shry',dataName.substring(dataName.indexOf(":")+1,dataName.length));
   					codeItemStore.load({
						params:{
							value:combo.value,
							flag:'2'
						},
						callback: function(record, option, succes){
							codeItemCom.setValue("");
							if(record.length>1){
								Ext.getCmp('codeItem_id').show(); 
							}else{
								Ext.getCmp('codeItem_id').hide(); 
							}
						}
					});
				}
			},
			renderTo: Ext.getBody()
   		});
		
		//代码下拉框
		var codeItemCom = Ext.widget({
		   			id:'codeItem_id',
		   			xtype:'combobox',
		   			fieldLabel:common.label.code,
		   			labelSeparator :'',//去掉后面的冒号
		   			store:codeItemStore,
		   			displayField:'dataName',
		   			valueField:'dataValue',
		   			editable:false,
		   			queryMode:'local',
		   			margin:'5,0,0,0',
		   			hidden:true,
		   			labelWidth:labelWidth,
		   			width:width,
		   			labelAlign:'right',
		   			listeners:{
		   				select:function(combo,record){
		    				condition_me.symbol('shry','"'+record.data.dataValue+'"');
						}
					},
					renderTo: Ext.getBody()
		   		});
		
		//存放参考项目中下拉框panel
		var comBoxItem = Ext.create('Ext.panel.Panel', {
			border:false,
			items:[{
		 	   	xtype:'fieldset',
		        title:common.label.referenceItems,//参考项目
		        layout: {
        	        type: 'vbox',
        	        align: 'stretch',
        	        pack :'center'
        	    },
		        width:310,
		        height:150,
		
		   		items:[heapFlagCom,fieldSetCom,fieldItemCom,codeItemCom]
		   		}]
		   	}); 
		Ext.getCmp('westId').add(comBoxItem);
	},
	
	//计算符号按钮
	initButtons:function(){
		var width_1 = 25;
		var width_2 = 32;
		var width_3 = 38;
		var width_4 = 61;
		var style_1 = "margin-left:2px;margin-top:5px";
		var buttons = Ext.widget('container', {
			items:[{
	    	   xtype:'fieldset',
	           title:common.label.operationaSymbol,
	           layout:'vbox',
	           width:260,
	           height:150,
	           padding:'0 0 0 18',
	           style:'algin:center',
	           items:[
	               {
		        	   xtype:'container',
		        	   items:[
								{xtype:'button',text:'0',width:width_1,height:25,style:'margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}} },
								{xtype:'button',text:'1',width:width_1,height:25,style:style_1,handler: function () { condition_me.symbol('shry','1');}},
								{xtype:'button',text:'2',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'3',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'4',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'(',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'>',width:25,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:common.button.Not,width:width_2,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry'," " + this.text + " ");}}}  
		        	          ]
	               },
	               {
		        	   xtype:'container',
		        	   items:[
								{xtype:'button',text:'5',width:width_1,height:25,style:'margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'6',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'7',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'8',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'9',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:')',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'<',width:25,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:common.button.And,width:width_2,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry'," " + this.text + " ");}}}		        	          ]
	               },
	               {
		        	   xtype:'container',
		        	   items:[
								{xtype:'button',text:'+',width:width_1,height:25,style:'margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'-',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'*',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'%',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'/',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'\\',width:width_1,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'~',width:25,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:common.button.Or,width:width_2,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry'," " + this.text + " ");}}}
		        	          ]
	               },
	               {
		        	   xtype:'container',
		        	   items:[
								{xtype:'button',text:'=',width:width_3,height:25,style:'margin-top:5px',listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'<>',width:width_3,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'<=',width:width_3,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},
								{xtype:'button',text:'>=',width:width_3,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry',this.text);}}},                      		
								{xtype:'button',text:common.button.Like,width:width_4,height:25,style:style_1,listeners:{"click":function(){ condition_me.symbol('shry'," Like ");}}}
							]
	               }
	           	]
			}]
		});
		Ext.getCmp('centerId').add(buttons);
	},
	
	/**--------------------- 页面显示 end --------------------*/
	
	//将所选指标、代码填充到文本框
	symbol:function(exprId, strexpr) {
		var rulearea = Ext.getCmp(exprId);
		var myField = rulearea.inputEl.dom;
		var startPos = 0;//光标选中内容起始位置
		var endPos = 0;//光标选中内容结束位置
		var selectionIndex = 0;//光标位置
		//IE support
		if (Ext.isIE) {
			var sel = null;
			startPos = condition_me.selectionStart;
			endPos = condition_me.selectionEnd;
			selectionIndex = condition_me.selectionIndex;
			myField.focus();
			//写入选中内容
			rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
			
			var index = selectionIndex + strexpr.length;
			var range = myField.createTextRange();
			range.move("character", index);//移动光标
			range.select();//选中
			condition_me.selectionIndex = index;
			condition_me.selectionStart = startPos + strexpr.length;
			condition_me.selectionEnd = endPos + strexpr.length;
		}
		//MOZILLA/NETSCAPE support 
		else if (myField.selectionStart || myField.selectionStart == '0') {
			startPos = myField.selectionStart;
			endPos = myField.selectionEnd;
			// 保存scrollTop，为了换行
			var restoreTop = myField.scrollTop;
			//写入选中内容
			rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
			
			if (restoreTop > 0) {//换行
				myField.scrollTop = restoreTop;
			}
			myField.focus();
			myField.selectionStart = startPos + strexpr.length;
			myField.selectionEnd = startPos + strexpr.length;
		}else {
			myField.value += strexpr;
	    }
	} ,
	
	//获得光标位置
	getCursorPosition:function () { 
		var rulearea = Ext.getCmp('shry');
   		var el = rulearea.inputEl.dom;//得到当前textarea对象
   		if(Ext.isIE){
   		    el.focus();
   		    if(document.selection!=null)
		    {
	   		    var r = document.selection.createRange(); //返回当前网页中的选中内容的TextRange对象
	   		    if (r == null) { 
	   		    	condition_me.selectionStart = 0; 
	   		    } 
	   		    var re = el.createTextRange(), //选中内容
	   		        rc = re.duplicate(); //所有内容
		   		    
				try{
		   		    //定位到指定位置
		   		    re.moveToBookmark(r.getBookmark());    		   
	   		    	//【为了保持选区】rc的开始端不动，rc的结尾放到re的开始
	   		    	rc.setEndPoint('EndToStart', re); 
	   		    }catch(e){
	   		    	//表格控件点击刷新页面按钮后，此时鼠标焦点拿不到 lis 20160704
	   		    }
	
	   		    var text = rc.text;
	   		    text = text.replace(/\r/g,'');//替换回车符 lis 20160701
	   		    condition_me.selectionIndex = text.length; //光标位置
	   		    condition_me.selectionStart = rc.text.length; 
	   		    condition_me.selectionEnd = condition_me.selectionStart + re.text.length;
		    }
   		    else
		    {
   		    	condition_me.selectionIndex = el.selectionStart; //光标位置
   		    	condition_me.selectionStart = el.selectionStart; 
   		    	condition_me.selectionEnd = el.selectionEnd;
		    }
		} 
	},
	
	//校验公式条件
	checkComplexCond:function(){
		var itemtype = condition_me.itemtype;
		var c_expr = Ext.getCmp("shry").getValue();
		var map = new HashMap();
		if(Ext.String.trim(c_expr).length>0)
	  	{
	  		c_expr = c_expr.replace( /\r/g, "!" ); 
			c_expr = c_expr.replace( /\n/g, "`" ); 	
			//规范字符串
//			for(var i = 0 ; i<c_expr.length; i++){
//				if(c_expr.charAt(i) == "\""){
//					c_expr = c_expr.replace("\"" , "'");
//				}
//			}
			map.put("c_expr",getEncodeStr(c_expr));
			map.put("fieldItem_List",condition_me.fieldItem_List);
			if(itemtype==null||itemtype=='0')
			{
				map.put("ntype","2");
				map.put("module","2");
			}
			else if(itemtype=='4')//工资类别定义人员范围时应为逻辑型的
			{
			    map.put("ntype","4");
				map.put("module","1");
			}
			else
			{
				map.put("module","1");
				var ntype="2";
				if(itemtype=="N")
					ntype="1";
				if(itemtype=="D")
					ntype="3";
				map.put("ntype",ntype);
			}
			//检验
			Rpc({functionId:'ZJ100000077',async:false,success: function(form,action){
				var result = Ext.decode(form.responseText);
				var base = result.base;
				if(base == 'ok'){
					condition_me.win.close();
					condition_me.saveComplexCond(c_expr);
				}else{
					Ext.showAlert(decode(result.base));
					return;
				}
		    }},map);
	  	}else{
	  		condition_me.win.close();
	  		condition_me.saveComplexCond("");
	  	}
	},
		//判断输入字符长度
	fucCheckLength:	function (strTemp){
	    var i,sum;
	    sum=0;
	    for(i=0;i<strTemp.length;i++){
	        if ((strTemp.charCodeAt(i)>=0) && (strTemp.charCodeAt(i)<=255)){
	            sum=sum+1;
	        }else{
	            sum=sum+2;
	        }
	    }
	    return sum;
	},
	//条件保存
	saveFormula:function(){
		var panel = Ext.widget('form', {
	//	    bodyPadding: 5,
		    border:false,
		    minButtonWidth:50,
		    width: 350,
		    // 表单域 Fields 将被竖直排列, 占满整个宽度
		    layout: 'anchor',
		    defaults: {
		        anchor: '100%'
		    },
		    items: [{
		    	xtype:'textfield',
		        fieldLabel: '',
		        itemId:'cname_id',
		        allowBlank: false
		    }],

		    // 重置 和 保存 按钮.
		    buttons: [{
		        text: common.button.ok,
		        formBind: true, //only enabled once the form is valid
		        handler: function() {
		        	var length=condition_me.fucCheckLength(Ext.util.Format.trim(panel.getComponent("cname_id").getValue()));
		        	if(length>20)
		        	{
		        		Ext.showAlert(common.msg.FormulaNameTooLong);  
		        		return;
		        	}
		        	if(length==0){
		        		Ext.showAlert(common.msg.FormulaNameNotNull);
		        		return;
		        	}
	            	var map = new HashMap();
	        		map.put("name",panel.getComponent("cname_id").getValue());
	        		map.put("expr",getEncodeStr(Ext.getCmp("shry").getValue()));
	        		Rpc({functionId:'ZJ100000096',success:function(response,action){
	        			var result = Ext.decode(response.responseText);  
	        			if (result.succeed) {
	        				if(result.msg != '0'){//有重复名称
	        					Ext.showAlert(result.msg);  
	        				}
	        			} else {
	        				Ext.showAlert(result.message+"！");  
	        			}
	        		}},map);
	        		win.close();
		        }
		    },{
		        text: common.button.cancel,
		        handler: function() {
		            win.close();
		        }
		    }],
		    renderTo: Ext.getBody()
		});
		
		var win = Ext.create('Ext.window.Window', {
		    title: "请输入条件名称",//请输入另存的薪资类别名称
		    height: 100,
		    resizable:false,
		    width: 400,
		    layout: 'fit',
		    modal:true,
		    items: [panel]
		});
		win.show();
	},

	//保存公式
	saveComplexCond:function(c_expr){
		if(condition_me.callBackfn)
			Ext.callback(eval(condition_me.callBackfn),null,[getEncodeStr(c_expr),condition_me.heapFlag,condition_me.initflag,condition_me.fieldid,condition_me.dataMap]);
	},
	
	//函数向导
	functionWizard:function(){
		var topt=1;
		var tmode="";
		if(condition_me.imodule==3){
            topt=1;
            tmode="xzgl_jsgs";
		}else if(condition_me.imodule==11){
            topt=7;
		}
        topt=topt+'';


		Ext.require('EHR.functionWizard.FunctionWizard',function(){//这块的函数向导不需要卡薪资类别，取权限内的全部子集
			Ext.create("EHR.functionWizard.FunctionWizard",{keyid:"",opt:topt,checktemp:'salary',mode:tmode,callbackfunc:'condition_me.getfunctionWizard'});
		})
	},
	
	//函数向导回调函数，用来接收返回值
	getfunctionWizard:function(obj){
		condition_me.symbol('shry',obj);
	}
});