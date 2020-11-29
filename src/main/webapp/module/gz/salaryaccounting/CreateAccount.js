 Ext.define('SalaryUL.CreateAccount',{
        constructor:function(config){
        	this.salaryid = config.salaryid;
        	var thisScope = this;      	
			var map = new HashMap();
			map.put("salaryid",salaryid);
		    Rpc({functionId:'GZ00000011',success: function(form,action){
					var result = Ext.decode(form.responseText);
					if(result.succeed){
						var theyear=result.theyear;
						var themonth=result.themonth;
						this.theyear = theyear;
						this.themonth = themonth;
						thisScope.createSalary(); 
					}else{//捕捉后台抛出的异常 throw new GeneralException(ResourceFactory.getProperty("xxxxx"));
						Ext.showAlert(result.message);
					}
			    }},map);
        },
		 createSalary:function()  
		 {
		   		win=Ext.widget("window",{
		          title:'新建',  
		          height:150,  
		          width:300,
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
			         		x:15,
			         		y:33,
			         		 xtype: 'label',
			         		 text:'指定业务日期'
		         		},{
		         			x:90,
		         			y:30,
		                    maxValue: 9999, // 最大值
		                    minValue: 1, // 最小值   
		                    width:60,
		                    name:'year',
		                    value:theyear
		                },
		                {
		                    x:155,
		         			y:33,
					        xtype: 'label',
					        forId: 'myFieldId',
					        text: '年'
					    },
		                {
		                    x:170,
		         			y:30,
		                    maxValue: 12, // 最大值
		                    minValue: 1, // 最小值
		                    name:'month',
		                    width:60,
		                    value:themonth
		                },
		                {
		                    x:235,
		         			y:33,
		         			width:60,
					        xtype: 'label',
					        forId: 'myFieldId',			        
					        text: '月'
					    }],
					    bbar:[
			          		{xtype:'tbfill'},
			          		{
			          			text:'确定',
			          			style:'margin-right:3px',
			          			handler:function(){		          					
			          					var form = this.up('form').getForm();
										if(form.isValid()){
											if(form.findField('year').getValue()==null||form.findField('month').getValue()==null){
												Ext.showAlert("请正确填写年月标识！");
												return;
											}
											Ext.MessageBox.alwaysOnTop=true;
											if('Z~30DuTtqmt~33kPAATTP~33HJDPAATTP'==accounting.imodule){
												Ext.MessageBox.wait("正在新建保险表，请稍候...", "等待");
											}
											else
												Ext.MessageBox.wait("正在新建工资表，请稍候...", "等待");
											var map = new HashMap();
											map.put("year",form.findField('year').getValue()+'');
											map.put("month",form.findField('month').getValue()+'');
											map.put("salaryid",salaryid);
										    Rpc({functionId:'GZ00000012',timeout:10000000,success:function(form,action){
											 		Ext.MessageBox.close();	
											 		var result = Ext.decode(form.responseText);			
													if(result.succeed){
														var salaryid=result.salaryid;
														var ff_bosdate=result.ff_bosdate;
														var count = result.count;
														if(typeof(ff_bosdate)=='undefined'){
															win.close();
														}else{
															accounting.appdate = ff_bosdate;//业务日期
															accounting.count = count;//次数
															win.close();
															accounting.reloadStore();
													    }
													}else{
														/*Ext.MessageBox.show({
								    					title : common.button.promptmessage,  
								    					buttons: Ext.Msg.OK,
								    					msg : result.message+"&nbsp;&nbsp;&nbsp;", 
								    					icon: Ext.MessageBox.INFO  
								    				});*/
														Ext.showAlert(result.message+"&nbsp;&nbsp;&nbsp;");
													}
											 }},map);
										}
			          			}
			          		},
			          		{
			          			text:'取消',
			          			style:'margin-left:3px',
			          			handler:function(){
			          				win.close();
			          			}
			          		},
			          		{xtype:'tbfill'}
			           ]
		          }]     
		    });                               
		    win.show();  
		 }
 });
