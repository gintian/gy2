/**
 * 薪资类别-薪资属性-其他参数
 * lis 
 * 2015-12-08
 */
Ext.define('SalaryTypeUL.salaryproperty.SalaryOtherParam',{
        constructor:function(config){
			salaryOtherParam_me = this;
			salaryOtherParam_me.commissionFlag = config.commissionFlag;//xiegh 20170412 add 提成权限标识
			salaryOtherParam_me.salaryid = config.salaryid;
			var result = config.result;
			salaryOtherParam_me.imodule=config.imodule;// 0:薪资  1:保险
			salaryOtherParam_me.moneyType = result.moneyType;//货币
			salaryOtherParam_me.moneyTypeList = result.moneyTypeList;//货币种类
			salaryOtherParam_me.amount_ctrl = result.amount_ctrl;//是否进行总额控制
			salaryOtherParam_me.amount_ctrl_ff = result.amount_ctrl_ff;//控制薪资发放
			salaryOtherParam_me.amount_ctrl_sp = result.amount_ctrl_sp;//控制薪资审批
			salaryOtherParam_me.ctrl_type = result.ctrl_type;//控制类型
			salaryOtherParam_me.verify_ctrl = result.verify_ctrl;//公式审核控制
			salaryOtherParam_me.verify_ctrl_ff = result.verify_ctrl_ff;//控制薪资发放
			salaryOtherParam_me.verify_ctrl_sp = result.verify_ctrl_sp;//控制薪资审批
			salaryOtherParam_me.a01z0Flag = result.a01z0Flag;//是否显示发现标识
			salaryOtherParam_me.bonusItemFld = result.bonusItemFld;//奖金项目
			salaryOtherParam_me.bonusItemFldList = result.bonusItemFldList;//奖金项目
			salaryOtherParam_me.field_priv = result.field_priv;//非写指标参与计算
			salaryOtherParam_me.read_field = result.read_field;//读权限指标允许重新导入
			salaryOtherParam_me.collect_je_field = result.collect_je_field;//汇总审批发放金额指标
			salaryOtherParam_me.number_field_list = result.number_field_list;//汇总审批发放金额指标数据集合
			
			salaryOtherParam_me.royalty_valid = result.royalty_valid;//提成薪资
			salaryOtherParam_me.royalty_setid = result.royalty_setid;//提成数据子集 id
			salaryOtherParam_me.strExpression = decode(result.strExpression);//数据范围条件公式表达式
			salaryOtherParam_me.royalty_date = result.royalty_date;//计划日期指标
			salaryOtherParam_me.royalty_period = result.royalty_period;//周期
			salaryOtherParam_me.royalty_relation_fields = result.royalty_relation_fields;//关联指标
			salaryOtherParam_me.fieldList = result.fieldList;//提成薪资-审批关系数据集合
			salaryOtherParam_me.dateList = result.dateList;//提成薪资-计划日期指标数据集合
			
			salaryOtherParam_me.priecerate_valid = result.priecerate_valid;//计件薪资
			salaryOtherParam_me.priecerateFields = result.priecerateFields;
			salaryOtherParam_me.priecerate_expression_str = decode(result.priecerate_expression_str);
			salaryOtherParam_me.priecerate_period = result.priecerate_period;
			salaryOtherParam_me.priecerate_firstday = result.priecerate_firstday;
			salaryOtherParam_me.createSalary(result); 
        },
		 createSalary:function(result)  
		 {
        	//货币种类数据store
        	var moneyStore = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
        	Ext.each(salaryOtherParam_me.moneyTypeList,function(obj,index){
        		moneyStore.insert(index,[{dataName: trim(obj.dataName),dataValue: obj.dataValue}]);
        	});
        	
        	// 货币种类下拉框
        	var moneyCombox = Ext.create('Ext.form.ComboBox', {
         	    store: moneyStore,
         	    width:250,
         	    editable:false,
         	    name:'moneyType',
         	    value:salaryOtherParam_me.moneyType,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	var padding = "2 0 0 10"
        	var style = "margin-bottom:3;";	
        	//货币种类
        	var moneyTypeFieldset = Ext.widget({
				xtype:'fieldset',
				height: 47,  
				title:gz.label.selectMoney,//选用货币
				hidden:salaryOtherParam_me.imodule=='0'?false:true,
				padding:padding,
				style:style,
				items:moneyCombox
			});
			var amount_ctrl_ffText=salaryOtherParam_me.imodule==0?gz.label.controlSalaryPay:gz.label.controlInsurancePay;
			var amount_ctrl_spText=salaryOtherParam_me.imodule==0?gz.label.controlSalarySp:gz.label.controlInsuranceSp;
        	var checkWidth = 100;
        	//薪资总额控制
        	var amountPanel = Ext.widget({
        		xtype:'panel',
        		border:false,
        		hidden:salaryOtherParam_me.amount_ctrl==1?false:true,
        		layout:'vbox',
        		padding:'3 0 0 0',
        		items:[{
		            xtype      : 'fieldcontainer',
		            fieldLabel : gz.label.controlScope,//控制范围
		            labelAlign:'right',
		            labelSeparator :'',
	        		labelWidth:76,
		            defaultType: 'checkbox',
		            defaults: {
		                flex: 1
		            },
		            layout: 'hbox',
		            items: [
		                {
		                    boxLabel  : amount_ctrl_ffText,//控制薪资发放
		                    name      : 'amount_ctrl_ff',
		                    inputValue: '1',
		                    width:checkWidth,
		                    checked:salaryOtherParam_me.amount_ctrl_ff==1?true:false,
		                    id        : 'amount_ctrl_ff'
		                }, {
		                    boxLabel  : amount_ctrl_spText,//控制薪资审批
		                    name      : 'amount_ctrl_sp',
		                    inputValue: '1',
		                    width:checkWidth,
		                    checked:salaryOtherParam_me.amount_ctrl_sp==1?true:false,
		                    id        : 'amount_ctrl_sp'
		                }
		            ]
		        },{
		            xtype      : 'fieldcontainer',
		            fieldLabel : gz.label.controlMode,//控制方式
		            labelAlign:'right',
		            labelSeparator :'',
	        		labelWidth:76,
		            layout: 'hbox',
		            items: [
		                {
		                	xtype:'radio',
		                    boxLabel  : gz.label.forcedControl,//强制控制
		                    name      : 'ctrl_type',
		                    inputValue: '1',
		                    width:checkWidth,
							checked:salaryOtherParam_me.ctrl_type==1?true:false
		                }, {
		                	xtype:'radio',
		                    boxLabel  : gz.label.preWarningControl,//预警控制
		                    name      : 'ctrl_type',
		                    inputValue: '0',
		                    width:checkWidth,
							checked:salaryOtherParam_me.ctrl_type==0?true:false
		                }
		            ]
		        }]
        	});
        	
        	//薪资总额控制
        	var ammoutFieldset = Ext.widget({
				xtype:'fieldset',
				height: salaryOtherParam_me.amount_ctrl==1?99:49,  
				title:gz.label.salaryAmountControl,//薪资总额控制
				hidden:salaryOtherParam_me.imodule=='0'?false:true,
				padding:padding,
				style:style,
				items:[{
					xtype:'panel',
					border:false,
					layout:'hbox',
					items:[{
						xtype     : 'checkbox',
						boxLabel  : gz.label.isControlAmount,//是否进行总额控制
						name:'amount_ctrl',
						checked: salaryOtherParam_me.amount_ctrl==1?true:false,
	                    inputValue: "1",
	                    listeners:{
							'change':function(field,newValue,oldValue){
								if(newValue){
									amountPanel.show();
									ammoutFieldset.setHeight(99);
								}else{
									amountPanel.hide();
									ammoutFieldset.setHeight(49);
								}
							}
					 }
					 }]
					},amountPanel]
			});
        	
        	//审核公式控制
        	var verifyPanel = Ext.widget({
        		xtype:'panel',
        		hidden: salaryOtherParam_me.verify_ctrl==1?false:true,
        		border:false,
        		layout:'vbox',
        		items:[{
		            xtype      : 'fieldcontainer',
		            fieldLabel : gz.label.controlScope,//控制范围
		            labelAlign:'right',
		            labelSeparator :'',
	        		labelWidth:76,
		            layout: 'hbox',
		            items: [
		                {
		                	xtype     : 'checkbox',
		                    boxLabel  : amount_ctrl_ffText,//控制薪资发放
		                    name      : 'verify_ctrl_ff',
		                    inputValue: '1',
		                    width:checkWidth,
							checked:salaryOtherParam_me.verify_ctrl_ff==1?true:false,
		                    id        : 'verify_ctrl_ff'
		                }, {
		                	xtype     : 'checkbox',
		                    boxLabel  : amount_ctrl_spText,//控制薪资审批
		                    name      : 'verify_ctrl_sp',
		                    inputValue: '1',
		                    width:checkWidth,
							checked:salaryOtherParam_me.verify_ctrl_sp==1?true:false,
		                    id        : 'verify_ctrl_sp'
		                }
		            ]
		        }]
        	});
        	
        	//审核公式控制
        	var verifyFieldset = Ext.widget({
				xtype:'fieldset',
				height: salaryOtherParam_me.verify_ctrl==1?74:49,  
				title:gz.label.auditFormulaControl,//审核公式控制
				padding:padding,
				style:style,
				items:[{
					xtype:'panel',
					border:false,
					layout:'hbox',
					items:[{
						xtype     : 'checkbox',
						boxLabel  : gz.label.isAuditFormulaControl,//是否进行审核公式控制
						name:'verify_ctrl',
						checked      : salaryOtherParam_me.verify_ctrl==1?true:false,
	                    inputValue: "1",
	                    id:'verifyid',
	                    listeners:{
	    					'change':function(field,newValue,oldValue){
	    						if(newValue){
	    							verifyPanel.show();
	    							verifyFieldset.setHeight(74);
	    						}else{
	    							verifyPanel.hide();
	    							verifyFieldset.setHeight(49);
	    						}
	    					}
	    			}
				},{
						xtype:'button',
						margin:'0 0 0 20',
						text:gz.button.setAuditReportField,//设置审核报告输出指标
						handler:function(){
						 	var map = new HashMap();
						 	map.put("a01z0Flag",Ext.getCmp("a01z0Flag").checked?"1":"0");
							map.put("salaryid",salaryOtherParam_me.salaryid);
							map.put("param_flag","verify");
							Rpc({functionId:'GZ00000232',success:function(response,action){
								var result = Ext.decode(response.responseText);
								if (result.succeed) {
									var leftlist = result.leftlist;
									var rightlist = result.rightlist;
									 Ext.require('EHR.selectfield.SelectField',function(){
										 Ext.create("EHR.selectfield.SelectField",{imodule:"0",leftDataList:leftlist,rightDataList:rightlist,title:gz.button.setAuditReportField,saveCallbackfunc:salaryOtherParam_me.saveItems});
									 })
								} else {  
									Ext.showAlert(result.message+"！");
								}
							}},map);
					   }
					}]},verifyPanel]
			});
        	
        	//停发标示控制
        	var a01z0Fieldset = Ext.widget({
				xtype:'fieldset',
				title:gz.label.StopSignControl,//停发标示控制
				padding:padding,
				style:style,
				items:[{
					xtype:'panel',
					border:false,
					layout:'hbox',
					height: 25,
					items:[{
						xtype     : 'checkbox',
						boxLabel  : gz.label.showStopSign,//显示停发标识
						name:'a01z0Flag',
						width:checkWidth,
						checked      : salaryOtherParam_me.a01z0Flag==1?true:false,
	                    inputValue: "1",
	                    id:'a01z0Flag'
					}]
			 }]
			 });
        	
        	//奖金项目数据store
        	var bonusStore = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
        	if(salaryOtherParam_me.bonusItemFld){
        		Ext.each(salaryOtherParam_me.bonusItemFldList,function(obj,index){
        			bonusStore.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        		});
        	}
        	
        	// 奖金项目下拉框
        	var bonusCombox = Ext.create('Ext.form.ComboBox', {
         	    store: bonusStore,
         	    width:250,
         	    editable:false,
         	    name:'bonusItemFld',
         	    value:salaryOtherParam_me.bonusItemFld,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	//奖金项目面板
        	var bonusFieldset = Ext.widget({
				xtype:'fieldset',
				title:gz.label.bonusItems,//奖金项目
				hidden:salaryOtherParam_me.bonusItemFld==null?true:false,
				padding:padding,
				style:style,
				items:[{
					xtype:'panel',
					border:false,
					layout:'hbox',
					height: 29,
					items:[bonusCombox]
			 }]
			 });
        	
        	//指标权限控制
        	var fieldPrivFieldset = Ext.widget({
				xtype:'fieldset',
				title:gz.label.fieldPriControl,//'指标权限控制'
				padding:padding,
				style:style,
				items:[{
					xtype:'panel',
					border:false,
					layout:'vbox',
					height: 51,
					items:[{
						xtype     : 'checkbox',
						boxLabel  : gz.label.field_priv,//非写指标参与计算
						name:'field_priv',
						checked      : salaryOtherParam_me.field_priv==1?true:false,
	                    inputValue: "1",
	                    id:'field_priv'
					},
					{
						xtype     : 'checkbox',
						boxLabel  : gz.label.read_field,//读权限指标允许重新导入
						name:'read_field',
						checked      : salaryOtherParam_me.read_field==1?true:false,
	                    inputValue: "1",
	                    id:'read_field'
					}]
				}]
			 });
        	
        	//汇总审批发放金额指标数据store
        	var collectStore = Ext.create('Ext.data.Store', {
        	    fields: ['dataName', 'dataValue']
        	});
        	
        	if(salaryOtherParam_me.collect_je_field){
        		Ext.each(salaryOtherParam_me.number_field_list,function(obj,index){
        			collectStore.insert(index,[{dataName: obj.dataName,dataValue: obj.dataValue}]);
        		});
        	}
        	
        	// 汇总审批发放金额指标下拉框
        	var collectCombox = Ext.create('Ext.form.ComboBox', {
         	    store: moneyStore,
         	    width:250,
         	    editable:false,
         	    name:'collect_je_field',
         	    value:salaryOtherParam_me.collect_je_field,
         	    queryMode: 'local',
         	    displayField: 'dataName',
         	    valueField: 'dataValue'
        	});
        	
        	//汇总审批发放金额指标
        	var collectFieldset = Ext.widget({
				xtype:'fieldset',
				height: 49,  
				hidden:salaryOtherParam_me.collect_je_field==null?true:false,
				title:gz.label.collect_je_field,//汇总审批发放金额指标
				padding:padding,
				style:style,
				items:collectCombox
			});
        	
        	//计件薪资和提成薪资
        	var panel = Ext.widget({
				xtype:'panel',
				border:false,
				hidden:salaryOtherParam_me.imodule=='0'&&salaryOtherParam_me.commissionFlag?false:true,//xiegh 20170412 add提成权限标识
				layout:'vbox',
				items:[{
					xtype:'panel',
					border:false,
					width:200,
					layout:'hbox',
					items:[{
						xtype     : 'checkbox',
						boxLabel  : gz.label.royaltySalary,//提成薪资
						name:'royalty_valid',
						checked      : salaryOtherParam_me.royalty_valid==1?true:false,
	                    inputValue: "1",
	                    id:'royalty_valid',
	                    listeners:{
							'change':function(field,newValue,oldValue){
								if(newValue){
									panel.queryById('setButtonId1').show();
								}else{
									panel.queryById('setButtonId1').hide();
								}
							}
						}
					},{
						xtype:'button',
						itemId:'setButtonId1',
						margin:'2 0 0 10',
						height:'20',
						padding:'1',
						text:common.button.set,//设置
						hidden:salaryOtherParam_me.royalty_valid==1?false:true,
						handler:function(){
							//薪资属性-提成薪资
				        	Ext.require('SalaryTypeUL.salaryproperty.RoyaltyValid', function(){
				        		var scopePanel = Ext.create("SalaryTypeUL.salaryproperty.RoyaltyValid",{salaryid:salaryProperty_me.salaryid,result:result});
				    		});
						}
					}]
				}
				/**,
				{
					xtype:'panel',
					border:false,
					layout:'hbox',
					items:[{
							xtype     : 'checkbox',
							boxLabel  : gz.label.priecerateSalary,//计件薪资
							name:'priecerate_valid',
							checked      : salaryOtherParam_me.priecerate_valid==1?true:false,
						    inputValue: "1",
						    id:'priecerate_valid',
						    listeners:{
								'change':function(field,newValue,oldValue){
									if(newValue){
										panel.queryById('setButtonId2').show();
									}else{
										panel.queryById('setButtonId2').hide();
									}
								}
							}
						},{
							xtype:'button',
							text:common.button.set,//设置
							itemId:'setButtonId2',
							margin:'3 0 0 10',
							height:'20',
							padding:'1 1 1 1',
							hidden:salaryOtherParam_me.priecerate_valid==1?false:true,
							handler:function(){
								//薪资属性-计件薪资
					        	Ext.require('SalaryTypeUL.salaryproperty.PriecerateValid', function(){
					        		var scopePanel = Ext.create("SalaryTypeUL.salaryproperty.PriecerateValid",{salaryid:salaryProperty_me.salaryid,result:result});
					    		});
							}
						}]
				}*/
				]
			 });
        	
        	var scopePanel = Ext.widget({
        		xtype:'panel',
        		border:false,
        		items:[moneyTypeFieldset,ammoutFieldset,verifyFieldset,a01z0Fieldset,bonusFieldset,fieldPrivFieldset,collectFieldset,panel]
        	});
        	//将当前panel渲染到tab页
        	salaryProperty_me.tabs.child('#otherId').add(scopePanel);
		},
		
		//保存审核报告输出指标
		saveItems:function(items){
//			if(items==null||items=='')
//				return;
			var map = new HashMap();
			map.put("salaryid",salaryOtherParam_me.salaryid);
			map.put("rightvalue",items);
			map.put("param_flag","saveverify");
			Rpc({functionId:'GZ00000232',success:function(response,action){
				var result = Ext.decode(response.responseText);
				if (!result.succeed) {
					Ext.showAlert(result.message+"！");
				} 
			}},map);
		}
 });