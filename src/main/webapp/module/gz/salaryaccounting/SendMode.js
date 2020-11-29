/**
*发送通知方式选择弹出框
*zhanghua 2016-6-29 backFun:发送通知回调函数
*/
 Ext.define("SalaryUL.SendMode",{
 	constructor:function(config){
 		SendModeScope=this;
 		SendModeScope.backFun=config.backFun;
 		SendModeScope.corpid=config.corpid;
 		SendModeScope.mobile=config.mobile;
 		SendModeScope.dd_corpid=config.dd_corpid;//钉钉
 		SendModeScope.init();
 	},
 	init:function(){
 		var rangeFieldset = Ext.widget({
				xtype:'fieldset',
				title:'范围',
				padding:'8 0 0 5',
				items:[{
						xtype:'radiogroup',
					    id:'selectrange',
					    height: 30,
					    columns:2,
						items:[{
								//xtype:'radio',
								name:'range',
								checked:true,
								boxLabel:'选中记录',
								inputValue:'1',
								width:120
							},{
								//xtype:'radio',
								name:'range',
								boxLabel:'全部记录',
								inputValue:'2',
								width:120
							}]
					}]
				//}]
 		});
 		var modeFieldset = Ext.widget({
				xtype:'fieldset',
				title:'方式',
				padding:'8 0 0 5',
				items:[{
						xtype:'radiogroup',
					    id:'selectmode',
					    height: 30,
					    columns:4,  
					    items:[{
							name:'mode',
							boxLabel:'邮件',
							checked:true,
							inputValue:'0',
							width:60
						}
						,{
							name:'mode',
							id:'mobile',
							boxLabel:'短信',
							disabled:SendModeScope.mobile==0?true:false,//判断短信参数是否设置
							inputValue:'1',
							width:60
						},{
							name:'mode',
							id:'corpid',
							boxLabel:'微信',
							disabled:SendModeScope.corpid==0?true:false,//判断微信参数是否设置
							inputValue:'2',
							width:60
						},{
							name:'mode',
							id:'dd_corpid',
							boxLabel:'钉钉',
							disabled:SendModeScope.dd_corpid==0?true:false,//判断钉钉参数是否设置
							inputValue:'3',
							width:60
						}]
					}],
				listeners:{
		        	afterrender:function(combo){
		        		//在低版本ie下opacity失效问题导致透明度效果没有 sunjian 2017-7-4暂时这样写，等以后有更好的解决方案再修改
		        		if(SendModeScope.mobile=="0") {
		        			Ext.getDom('mobile-displayEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
	            			Ext.getDom('mobile-boxLabelEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
		        		}
		        		if(SendModeScope.corpid=="0") {
		        			Ext.getDom('corpid-displayEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
	            			Ext.getDom('corpid-boxLabelEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
		        		}
		        		if(SendModeScope.dd_corpid=="0") {
		        			Ext.getDom('dd_corpid-displayEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
	            			Ext.getDom('dd_corpid-boxLabelEl').setAttribute('style','filter:progid:DXImageTransform.Microsoft.Alpha(Opacity=30);');
		        		}
       			  	}
	            }
 		});
// 		var mobileRadio=Ext.widget({
// 							xtype:'radio',
//							name:'mode',
//							boxLabel:'短信',
//							disabled:true,
//							//hidden:true,
//							//SendModeScope.mobile==0?true:false,
//							inputValue:'1',
//							width:80
// 		});
// 		var corpidRadio=Ext.widget({
// 							xtype:'radio',
//							name:'mode',
//							boxLabel:'微信',
//							//hidden:true,
//							//SendModeScope.mobile==0?true:false,
//							inputValue:'2',
//							width:80
// 		});
// 		
// 		
// 		if(SendModeScope.mobile==1)
// 			Ext.getCmp('selectmode').add(mobileRadio);
// 		if(SendModeScope.corpid==1)
// 			Ext.getCmp('selectmode').add(corpidRadio);
 		var mainWin=Ext.widget("window",{
			title:'发送通知',
			height:220,  
			width:300,
			layout:'fit',
			resizable: false,
//			alwaysOnTop:true,
			bodyBorder:false,
			
			modal:true,
			closeAction:'destroy',
			items:[{
				xtype:'panel',
          		border:false,
          		items:[rangeFieldset,modeFieldset]
			}],
			buttons:[{xtype:'tbfill'},{
      			xtype: 'button',
		        text: '确定',
		        handler:function(){
		        	var selectrange=Ext.getCmp('selectrange').getValue();
		        	var selectmode=Ext.getCmp('selectmode').getValue();
		        	var rangedata=selectrange.range;
		        	var modeadata=selectmode.mode;		        	
		        	Ext.callback(eval(SendModeScope.backFun),null,[modeadata,rangedata]);
		        	mainWin.close();
				}
      		},{
      			xtype: 'button',
		        margin:'5 0 0 5',
		        text: '取消',
		        handler:function(){
		        	mainWin.close();
		        }
      		},{xtype:'tbfill'}]
 		});
 		mainWin.show();
 	}
 	
 })