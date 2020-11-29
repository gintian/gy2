/**
 * 重置业务日期
 */
Ext.define('SalaryUL.ReSetGzDate',{
        constructor:function(config){
			resetGzDate_me = this;      	
			resetGzDate_me.salaryid = config.salaryid;//薪资类别id
			var map = new HashMap();
			map.put("salaryid",resetGzDate_me.salaryid);
			map.put("opt","reset");	//操作类型是“重置业务日期”
		    Rpc({functionId:'GZ00000011',scope:resetGzDate_me,success: function(form,action){
					var result = Ext.decode(form.responseText);
					if(result.succeed){
						resetGzDate_me.theyear = result.theyear;
						resetGzDate_me.themonth = result.themonth; 
						resetGzDate_me.count = result.count;
						var salaryIsSubed = result.salaryIsSubed;//数据是否已经提交
						if(salaryIsSubed=='false'){
							Ext.Msg.confirm(common.button.promptmessage, gz.msg.isContinueReSetDate, function(button, text) {  
								if (button == "yes") {
									resetGzDate_me.createSalary(); 
								}
							})
						}else{
							resetGzDate_me.createSalary(); 
						}
						
					}else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
						Ext.showAlert(result.message);
					}
			    }},map);
        },
		 createSalary:function()  
		 {
        	resetGzDate_me.win=Ext.widget("window",{
		          title:gz.label.specifyAppDate,  
		          height:150,  
		          width:360,
		          resizable:false,
		          layout:'fit',
				  modal:true,
				  closeAction:'destroy',
		          items: [{
		          		xtype:'form',
		          		method:'post',
		          		border:false,
		          		layout:'absolute',	
		          		defaultType: "numberfield",
						align:'center',
		         		items: [{
		         			x:60,
		         			y:30,
		                    maxValue: 9999, // 最大值
		                    minValue: 1, // 最小值   
		                    width:60,
		                    name:'year',
		                    value:resetGzDate_me.theyear
		                },
		                {
		                    x:125,
		         			y:33,
					        xtype: 'label',
					        forId: 'myFieldId',
					        text: common.label.year
					    },
		                {
		                    x:140,
		         			y:30,
		                    maxValue: 12, // 最大值
		                    minValue: 1, // 最小值
		                    name:'month',
		                    width:60,
		                    value:resetGzDate_me.themonth
		                },
		                {
		                    x:205,
		         			y:33,
		         			width:60,
					        xtype: 'label',
					        forId: 'myFieldId',			        
					        text: common.label.month
					    },
					    {
		         			x:220,
		         			y:30,
		                    //maxValue: 12, // 最大值
		                    minValue: 1, // 最小值   
		                    width:60,
		                    name:'count',
		                    value:resetGzDate_me.count
		                },
		                {
		                    x:285,
		         			y:33,
		         			width:60,
					        xtype: 'label',
					        forId: 'myFieldId',			        
					        text: common.label.count
					    }],
						bbar:[
			          		{xtype:'tbfill'},
			          		{
			          			text:common.button.ok,
			          			handler:function(){
				          		 	var form = this.up('form').getForm();
									if(form.isValid()){
										Ext.MessageBox.wait(gz.label.isReSetDate, common.msg.wait)
										var map = new HashMap();
										if(form.findField('year').getValue()==null||form.findField('month').getValue()==null||form.findField('count').getValue()==null){
											Ext.showAlert("请正确填写年月标识及次数！");
											return;
										}
										
										map.put("year",form.findField('year').getValue()+'');
										map.put("month",form.findField('month').getValue()+'');
										map.put("count",form.findField('count').getValue()+'');
										map.put("salaryid",resetGzDate_me.salaryid);
									    Rpc({functionId:'GZ00000151',async:false,success:function(form,action){
										 		Ext.MessageBox.close();	
										 		var result = Ext.decode(form.responseText);			
										 		var ff_bosdate=result.ff_bosdate;
												var count = result.count;		
										 		resetGzDate_me.win.close();
												if(result.succeed){												
													accounting.appdate = ff_bosdate;//业务日期
													accounting.count = count;//次数
													accounting.reloadStore();
												}else{
													Ext.showAlert(result.message);
												}
										 }},map);
									}
			          			}
			          		},
			          		{
			          			text:common.button.cancel,
			          			handler:function(){
			          			resetGzDate_me.win.close();
			          			}
			          		},
			          		{xtype:'tbfill'}
			           ]
		          }]     
		    });                               
        	resetGzDate_me.win.show();  
		 }
 });
