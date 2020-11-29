/**
 * 
 */
Ext.define('EHR.exportPageSet.pageSet.PageText',{
	  constructor:function(config){
		  thistext= this;
		  thistext.result = config.result;
		  thistext.createPanel();
			},
			createPanel:function(){	
				var pheadColor=thistext.result.phead_fc;//字体颜色
				var textColor=thistext.result.text_fc;//字体颜色

				    var sizeStore = Ext.create('Ext.data.Store', {
					    fields: ['dataName', 'dataValue'],
					 data : [
						 {dataName: "5 px",  dataValue: "5"},
				         {dataName: "8 px",  dataValue: "8"},
				         {dataName:"10 px",  dataValue: "10"},
				         {dataName: "12 px", dataValue: "12"},
				         {dataName: "14 px", dataValue: "14"},
				         {dataName: "16 px", dataValue: "16"},
				         {dataName: "24 px", dataValue: "24"},
				         {dataName: "32 px", dataValue: "32"},
				         {dataName: "48 px", dataValue: "48"}				        
				     ]
					});
				  
				    var  colorPanel = Ext.create('Ext.picker.Color', {
					    value: thistext.result.phead_fc==""?"#000000":thistext.result.phead_fc,  // 初始选择的颜色		   
					    listeners: {
					        select: function(picker, selColor) {		      
					            var color = '#' + selColor;					           
					            Ext.getCmp('phead_fc').setFieldStyle('background-color:'+color+';');			           
			                    Ext.getCmp('phead_fc').setValue(color);                       
					        }
					    }
					});  
				    
				    var  colorTxPanel = Ext.create('Ext.picker.Color', {
					    value: thistext.result.text_fc==""?"#000000":thistext.result.text_fc,  // 初始选择的颜色		   
					    listeners: {
					        select: function(picker, selColor) {		      
					            var color = '#' + selColor;					           
					            Ext.getCmp('text_fc').setFieldStyle('background-color:'+color+';');			           
			                    Ext.getCmp('text_fc').setValue(color);                       
					        }
					    }
					});  
				    
					var fontStore = Ext.create('Ext.data.Store', {
					    fields: ['dataName', 'dataValue'],
					 data : [
						 {dataName: "楷体_GB2312",dataValue: "楷体_GB2312"},
				         {dataName: "方正舒体", dataValue: "方正舒体"},
				         {dataName: "仿宋体",   dataValue: "仿宋体"},
				         {dataName: "华文彩云", dataValue: "华文彩云"},
				         {dataName: "华文仿宋", dataValue: "华文仿宋"},
				         {dataName: "华文细黑", dataValue: "华文细黑"},
				         {dataName: "华文行楷", dataValue: "华文行楷"},
				         {dataName: "华文中宋", dataValue: "华文中宋"},
				         {dataName: "隶书",   dataValue: "隶书"},
				         {dataName: "幼圆",   dataValue: "幼圆"}
				     ]
					});	  	
					//正文							
					var text_fn = Ext.create('Ext.form.ComboBox', {
						fieldLabel : '字体',//字体
						labelAlign : 'right',
						labelSeparator : '',
						editable : false,
						id : "text_fn",
						inputId:"text_fn-input",
						labelWidth : 30,
						emptyText : "请选择字体",
						store : fontStore,
						width : 150,
						padding : '5 0 5 0',
						queryMode : 'local',
						displayField : 'dataName',
						valueField : 'dataValue'
					});
					var phead_fn = Ext.create('Ext.form.ComboBox', {
						fieldLabel : '字体',//字体
						labelAlign : 'right',
						labelSeparator : '',
						editable : false,
						id : "phead_fn",
						inputId:"phead_fn-input",
						labelWidth : 30,
						emptyText : "请选择字体",
						store : fontStore,
						width : 150,
						padding : '5 0 5 0',
						queryMode : 'local',
						displayField : 'dataName',
						valueField : 'dataValue'
					});
					var text_fz = Ext.create('Ext.form.ComboBox', {
						fieldLabel : '大小',//大小
						labelAlign : 'right',
						labelSeparator : '',
						editable : false,
						id : "text_fz",
						inputId:"text_fz-input",
						labelWidth : 30,
						store : sizeStore,
						width : 160,
						padding : '5 0 5 10',
						queryMode : 'local',
						emptyText : "请选择大小",
						displayField : 'dataName',
						valueField : 'dataValue'
					});
					var phead_fz = Ext.create('Ext.form.ComboBox', {
						fieldLabel : '大小',//大小
						labelAlign : 'right',
						labelSeparator : '',
						editable : false,
						id : "phead_fz",
						inputId:"phead_fz-input",
						labelWidth : 30,
						store : sizeStore,
						width : 160,
						padding : '5 0 5 10',
						queryMode : 'local',
						emptyText : "请选择大小",
						displayField : 'dataName',
						valueField : 'dataValue'
					});
					var textPanel = Ext.widget({
						xtype : 'form',
						id : "textid",
						border : false,
						padding : '5 5 5 5',
						items : [{
							xtype : 'panel',
							width : '100%',
							border : false,
							items : [ {
								padding : '5 5 5 5',
								xtype : 'fieldset',
								autoHeight : true,
								height : 150,
								title : '表头信息',
								items : [ {
									xtype : 'panel',
									layout : 'column',
									width : 'auto',
									border : false,
									padding : '5 0 5 0',
									items : [phead_fn, phead_fz]
								}, {
									xtype : "checkboxgroup",
									id : "textCheckboxgroup",
									padding : '5 0 5 5',
									width : 300,
									columns : 3,
									flex : 1,
									items : [ {
										boxLabel : '粗体',
										id : "phead_fb",
										inputValue : "#fb[1]"
									}, {
										boxLabel : '斜体',
										id : "phead_fi",
										inputValue : "#fi[1]"
									}, {
										boxLabel : '下划线',
										id : "phead_fu",
										inputValue : "#fu[1]"
									}

									]
								}, {
									/*xtype : 'textfield',
									width : 280,
									padding : '5 0 5 7',
									id : "phead_fc",
									inputId:"phead_fc-input",
									labelWidth : 30,
									fieldStyle : 'background-color: #000000;',
									fieldLabel : gz.page.color,*/
									 xtype : 'form',
									 layout:'absolute', 
									 height : 60,	
								     border : false,										
				          items : [
									{								
						        	    	xtype : 'label',
											padding : '5 0 5 7',
											text : '颜色'
										  },  
										{								
											x : 30,//横坐标为距父容器左边缘10像素的位置  
											y : 0,//纵坐标为距父容器上边缘10像素的位置  
											xtype : 'textfield',
											readOnly:true,
											width : 280,
											padding : '5 0 5 7',								
											id : "phead_fc",
											inputId:"phead_fc-input",
											fieldStyle : 'background-color:'+pheadColor+';',							
											listeners : {
												  click: {
			                                            element: 'el', //bind to the underlying el property on the panel
											            fn: function(){ 								            
								                            Ext.getCmp("colorPhpanel").show();								          								                         							               				                           					               								               
								                        }
											      },								        
											      'change' : function(field,newValue,oldValue){ 								                 
        	                                            pheadColor = newValue;									        	                                       
                                                        Ext.getCmp('phead_fc').setFieldStyle('background-color:'+pheadColor+';');						              											                                                       
                                                        Ext.getCmp("colorPhpanel").hide();
                                                  } 
											 }	
										},{																 							
											width:150,
										    height:90,
										    border : false,							   
											hidden:true,
											floating:true,
											x : 36,
											y : 26,							
											xtype : 'panel',
											id:'colorPhpanel',																									
											items : [colorPanel] 									 																																		
									      }]
								} ]

							}, {
								xtype : 'panel',
								width : '100%',
								border : false,
								items : [ {
									padding : '5 5 5 5',
									xtype : 'fieldset',
									autoHeight : true,
									height : 150,
									title : '内容信息',
									items : [ {
										xtype : 'panel',
										layout : 'column',
										width : 'auto',
										border : false,
										padding : '5 0 5 0',
										items : [ text_fn, text_fz ]

									}, {
										xtype : "checkboxgroup",
										id : "hiCheckboxgroup",
										padding : '5 0 5 5',
										width : 300,
										columns : 3,
										flex : 1,
										items : [ {
											boxLabel : '粗体',
										    id : "text_fb",
											inputValue : "#fb[1]"
										}, {
											boxLabel : '斜体',
											id : "text_fi",
											inputValue : "#fi[1]"
										}, {
											boxLabel : '下划线',
											id : "text_fu",
											inputValue : "#fu[1]"
										}

										]
									}, {
										/*xtype : 'textfield',
										width : 280,
										padding : '5 0 5 7',
										labelWidth : 30,
										id : "text_fc",
										inputId:"text_fc-input",
										fieldStyle : 'background-color: #000000;',
										fieldLabel : gz.page.color,*/
										 xtype : 'form',
										 layout:'absolute', 
										 height : 60,	
									     border : false,										
					          items : [
										{								
							        	    	xtype : 'label',
												padding : '5 0 5 7',
												text : '颜色'
											  },  
											{								
												x : 30,//横坐标为距父容器左边缘10像素的位置  
												y : 0,//纵坐标为距父容器上边缘10像素的位置  
												xtype : 'textfield',
												readOnly:true,
												width : 280,
												padding : '5 0 5 7',								
												id : "text_fc",
												inputId:"text_fc-input",
												fieldStyle : 'background-color:'+textColor+';',							
												listeners : {
													  click: {
				                                            element: 'el', //bind to the underlying el property on the panel
												            fn: function(){ 								            
												                            Ext.getCmp("colorTfpanel").show();								          								                         							               				                           					               								               
												                           }
												             },								        
												        'change' : function(field,newValue,oldValue){ 								                 
												        	                                            textColor = newValue;
												                                                        Ext.getCmp('text_fc').setFieldStyle('background-color:'+textColor+';');												                                                      
											                                                            Ext.getCmp("colorTfpanel").hide();
												                                                    } 
												        
												            }	
											},{																 							
												width:150,
											    height:90,
											    border : false,							   
												hidden:true,
												floating:true,
												x : 36,//横坐标为距父容器左边缘10像素的位置  
												y : 26,//纵坐标为距父容器上边缘10像素的位置  							
												xtype : 'panel',
												id:'colorTfpanel',																									
												items : [colorTxPanel] 									 																																		
										      }]
									} ]

								} ]
							} ]
						} ]
					})
					
					 Ext.getBody().addListener('click', function(evt, el) {		   
			          if ( "phead_fc-input" != el.id && "colorPhpanel-input" != el.id  && Ext.getCmp("colorPhpanel") != undefined)
				       {  
					     Ext.getCmp("colorPhpanel").hide();																							 					
				       }
				      if ( "text_fc-input" != el.id && "colorTfpanel-input" != el.id && Ext.getCmp("colorTfpanel") != undefined )
				       {  
					     Ext.getCmp("colorTfpanel").hide();																							 					
				       }	 
					 })
					 
					//正文页签数据填充
			        Ext.getCmp('text_fn').setValue(thistext.result.text_fn==''?'仿宋体':thistext.result.text_fn);  		        
			        Ext.getCmp('text_fz').setValue(thistext.result.text_fz==''?'10':thistext.result.text_fz);                	               
	                if(thistext.result.text_fc==""){
	                	thistext.result.text_fc = "#000000"; 
	    	        	Ext.getCmp('text_fc').setValue(thistext.result.text_fc);
	                }else{
	                	Ext.getCmp('text_fc').setValue(thistext.result.text_fc);
	                }	
			        if(thistext.result.text_fb=='#fb[1]'){
			        	Ext.getCmp('text_fb').setValue(true);
			        }
			        if(thistext.result.text_fi=='#fi[1]'){
			        	Ext.getCmp('text_fi').setValue(true);
			        }
			        if(thistext.result.text_fu=='#fu[1]'){
			        	Ext.getCmp('text_fu').setValue(true);
			        }		        
			        Ext.getCmp('phead_fn').setValue(thistext.result.phead_fn==''?'仿宋体':thistext.result.phead_fn);  		        
			        Ext.getCmp('phead_fz').setValue(thistext.result.phead_fz==''?'10':thistext.result.phead_fz);                	              
	                if(thistext.result.phead_fc==""){
	                	thistext.result.phead_fc = "#000000"; 
	    	        	Ext.getCmp('phead_fc').setValue(thistext.result.phead_fc);
	                }else{
	                	Ext.getCmp('phead_fc').setValue(thistext.result.phead_fc);
	                }	
	                
	                if(thistext.result.phead_fb=='#fb[1]'){
			        	Ext.getCmp('phead_fb').setValue(true);
			        }
			        if(thistext.result.phead_fi=='#fi[1]'){
			        	Ext.getCmp('phead_fi').setValue(true);
			        }
			        if(thistext.result.phead_fu=='#fu[1]'){
			        	Ext.getCmp('phead_fu').setValue(true);
			        }	      		        
			        Ext.getCmp('text').add(textPanel);	
			  }
})
