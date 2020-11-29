/**
 * 
 */
Ext.define('EHR.exportPageSet.pageSet.PageTail',{
	  constructor:function(config){
		  thispagetail= this;
		  thispagetail.result = config.result;
		  thispagetail.createPanel();
			},
			createPanel:function(){	
			    var ptFn=thispagetail.result.tail_fontface;//字体种类
				var ptFz=thispagetail.result.tail_fontsize==''?'12':thispagetail.result.tail_fontsize + 'px';//字号大小
				var ptFb;//字体粗细
			    var ptFi;//字体倾斜
				var ptFus = "none";//下划线与删除线	
				var ptFcolor=thispagetail.result.tail_fc;//字体颜色				
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
				  
					var fontStore = Ext.create('Ext.data.Store', {
					    fields: ['dataName', 'dataValue'],
					 data : [
						 {dataName: "楷体_GB2312",    dataValue: "楷体_GB2312"},
				         {dataName: "方正舒体",    dataValue: "方正舒体"},
				         {dataName: "仿宋体", dataValue: "仿宋体"},
				         {dataName: "华文彩云", dataValue: "华文彩云"},
				         {dataName: "华文仿宋", dataValue: "华文仿宋"},
				         {dataName: "华文细黑", dataValue: "华文细黑"},
				         {dataName: "华文行楷", dataValue: "华文行楷"},
				         {dataName: "华文中宋", dataValue: "华文中宋"},
				         {dataName: "隶书", dataValue: "隶书"},
				         {dataName:"幼圆", dataValue: "幼圆"}
				     ]
					});	  
					
					var  colorPanel = Ext.create('Ext.picker.Color', {
					    value: thispagetail.result.tail_fc==""?"#000000":thispagetail.result.tail_fc,  // 初始选择的颜色	
					    listeners: {
					        select: function(picker) {	
					            var color = '#' + picker.value;                 		          
					            Ext.getCmp('colorTail').setFieldStyle('background-color:'+color+';');			           
			                    Ext.getCmp('colorTail').setValue(color);                      
					        }
					    }
					});
					
					//字体类型下拉选择框				
					var tail_fn = Ext.create('Ext.form.ComboBox', {
						fieldLabel : '字体',//字体
						labelAlign : 'right',
						labelSeparator : '',
						editable : false,
						id : "tail_fn",
						inputId:"tail_fn-input",
						labelWidth : 30,
						emptyText : "请选择字体",
						store : fontStore,
						width : 150,
						padding : '5 0 5 0',
						queryMode : 'local',
						displayField : 'dataName',
						valueField : 'dataValue',
						listeners : {//选择一行后触发的事件 
							'select' : function(tail_fn) {						
							    ptFn = tail_fn.getValue();					 
								Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
								Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
								Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');				
							}
						}
					});
					//字号大小下拉框
					var tail_fz = Ext.create('Ext.form.ComboBox', {
						fieldLabel : '大小',//大小
						labelAlign : 'right',
						labelSeparator : '',
						editable : false,
						id : "tail_fz",
						inputId:"tail_fz-input",
						labelWidth : 30,
						store : sizeStore,
						width : 160,
						padding : '5 0 5 10',
						queryMode : 'local',
						emptyText : "请选择大小",
						displayField : 'dataName',
						valueField : 'dataValue',
						listeners : {//选择一行后触发的事件 
							'select' : function(tail_fz) {						
								ptFz = tail_fz.getValue() + 'px';	
							    var tail_left_value = Ext.getCmp('tail_left').getValue();
							    var tail_center_value = Ext.getCmp('tail_center').getValue();
							    var tail_right_value = Ext.getCmp('tail_right').getValue();
							    Ext.getCmp('tail_left').setValue('');
							    Ext.getCmp('tail_center').setValue('');
							    Ext.getCmp('tail_right').setValue('');
								Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
								Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
								Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
								Ext.getCmp('tail_left').setValue(tail_left_value);
							    Ext.getCmp('tail_center').setValue(tail_center_value);
							    Ext.getCmp('tail_right').setValue(tail_right_value);
							}
						}
					});

					var pagetailPanel = Ext.widget({
								xtype : 'form',
								id : "pagetailid",
								border : false,
								padding : '5 5 5 5',
								items : [ {
									padding : '5 5 5 5',
									xtype : 'fieldset',
									autoHeight : true,
									height : 320,
									title : '表尾内容',
									items : [
										{											
										    xtype : 'form',
										    layout:'absolute',  
											height : 180,
											id : "taillcr",
											border : false,												
			                             items : [
			                            	  {
				          							xtype : 'label',
				          							padding : '20 0 5 7',
				          							text : '下左内容'
				          						}, 
			                            	 {
											   x : 90,//横坐标为距父容器左边缘5像素的位置  
												y : 0,//纵坐标为距父容器上边缘5像素的位置  	  
												width : thispagetail.result.isExcel=='0'?350:300,
												height : 55,
												xtype : 'textarea',
												id : "tail_left",
												inputId:"tlTextarea",
												fieldStyle:'font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';',
												listeners : {
													  click: {
			                                                element: 'el', //bind to the underlying el property on the panel
												            fn: function(){ Ext.getCmp("objectp5").show();
												            	if(Ext.isIE){
																   exportPageSetScope.getCursorPosition("tail_left"); //获得光标位置
												            	}	
												            }
												        },
												        keyup : {
															element: 'el', //bind to the underlying el property on the panel
															fn: function(){ Ext.getCmp("objectp5").hide();	}
														}
												}
											},														
											{
												x : 90,//横坐标为距父容器左边缘10像素的位置  
												y : 55,//纵坐标为距父容器上边缘10像素的位置  
												xtype : 'panel',
												id:'objectp5',
												hidden:true,
												floating:true,
												border : false,
												html:
													   "<div id='object_panel5'>"+         
												   	"<select name='object_rate' multiple='multiple' size='6'  style='width:200' onchange='thispagetail.tlSelectValue(this);'>"+    
														"<option value='&[页码]'>&nbsp;&[页码]&nbsp;</option>"+
														"<option value='&[总行数]'>&nbsp;&[总行数]&nbsp;</option>"+
														"<option value='&[制作人]'>&nbsp;&[制作人]&nbsp;</option>"+
														"<option value='&[YYYY年YY月]'>&nbsp;&[YYYY年YY月]&nbsp;</option>"+	
														"<option value='&[时间]'>&nbsp;&[时间HH:MM:SS]&nbsp;</option>"+
														"<option value='&[日期]'>&nbsp;&[日期YYYY-MM-DD]&nbsp;</option>"+	
													"</select>"+
												   "</div>" ,																						
											},{   
												x : 400,
												y : 20,
												width : 350,
												height : 20,
												xtype : 'checkbox',
												boxLabel: '仅尾页显示',
												hidden: thispagetail.result.isExcel=='0'?true:false,
												id : "lFootOnlyShow",
												name: 'footShow',
												inputValue: 'lFootChecked'
											},{
												y : 60,
												xtype : 'label',
												padding : '20 0 5 7',
			          							text : '下中内容'
			          						  }, 
											{   x : 90,//横坐标为距父容器左边缘10像素的位置  
												y : 60,//纵坐标为距父容器上边缘10像素的位置  
												width : thispagetail.result.isExcel=='0'?350:300,
												height : 55,
												xtype : 'textarea',
												id : "tail_center",
												inputId:"tcTextarea",									
												fieldStyle:'font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';',
												listeners : {
													          click: {
			                                                            element: 'el', //bind to the underlying el property on the panel
												                        fn: function(){ Ext.getCmp("objectp6").show();
												                        	if(Ext.isIE){
																			   exportPageSetScope.getCursorPosition("tail_center"); //获得光标位置
															            	}
												                        }
												                     },
											                  keyup : {
																		element: 'el', //bind to the underlying el property on the panel
																		fn: function(){ Ext.getCmp("objectp6").hide();	}
																	}
									
												            }
											},{
												
												x : 90,//横坐标为距父容器左边缘10像素的位置  
												y : 115,//纵坐标为距父容器上边缘10像素的位置  
												xtype : 'panel',
												id:'objectp6',
												hidden:true,
												floating:true,
												border : false,
												html:
													   "<div id='object_panel6'>"+         
												   	"<select name='object_rate' multiple='multiple' size='6'  style='width:200' onchange='thispagetail.tcSelectValue(this);'>"+    
														"<option value='&[页码]'>&nbsp;&[页码]&nbsp;</option>"+
														"<option value='&[总行数]'>&nbsp;&[总行数]&nbsp;</option>"+
														"<option value='&[制作人]'>&nbsp;&[制作人]&nbsp;</option>"+
														"<option value='&[YYYY年YY月]'>&nbsp;&[YYYY年YY月]&nbsp;</option>"+	
														"<option value='&[时间]'>&nbsp;&[时间HH:MM:SS]&nbsp;</option>"+
														"<option value='&[日期]'>&nbsp;&[日期YYYY-MM-DD]&nbsp;</option>"+	
													"</select>"+
												   "</div>" ,
											
											
											},{   
												x : 400,
												y : 78,
												width : 350,
												height : 20,
												xtype : 'checkbox',
												boxLabel: '仅尾页显示',
												hidden: thispagetail.result.isExcel=='0'?true:false,
												id : "mFootOnlyShow",
												name: 'footShow',
												inputValue: 'mFootChecked'
											},{
												y : 120,
												xtype : 'label',
												padding : '20 0 5 7',
			          							text : '下右内容'
			          						}, 
											{
												x : 90,//横坐标为距父容器左边缘10像素的位置  
												y : 120,//纵坐标为距父容器上边缘10像素的位置  
												width : thispagetail.result.isExcel=='0'?350:300,
												height : 55,
												xtype : 'textarea',
												id : "tail_right",
												inputId:"trTextarea",
												fieldStyle:'font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';',
												listeners : {
													  click: {
			                                                   element: 'el', //bind to the underlying el property on the panel
												               fn: function(){
												            	   Ext.getCmp("objectp7").show();
												            	   if(Ext.isIE){
																	   exportPageSetScope.getCursorPosition("tail_right"); //获得光标位置
													            	}
												            	             }
												              },																										
												             },
											          keyup : {
																element: 'el', //bind to the underlying el property on the panel
																fn: function(){ Ext.getCmp("objectp7").hide();	}
															}
											},									
											{
												x : 90,//横坐标为距父容器左边缘10像素的位置  
												y : 175,//纵坐标为距父容器上边缘10像素的位置  
												xtype : 'panel',
												id:'objectp7',
												hidden:true,
												floating:true,
												border : false,
												html:
													   "<div id='object_panel7'>"+         
												   	"<select name='object_rate' multiple='multiple' size='6'  style='width:200' onchange='thispagetail.trSelectValue(this);'>"+    
														"<option value='&[页码]'>&nbsp;&[页码]&nbsp;</option>"+
														"<option value='&[总行数]'>&nbsp;&[总行数]&nbsp;</option>"+
														"<option value='&[制作人]'>&nbsp;&[制作人]&nbsp;</option>"+
														"<option value='&[YYYY年YY月]'>&nbsp;&[YYYY年YY月]&nbsp;</option>"+	
														"<option value='&[时间]'>&nbsp;&[时间HH:MM:SS]&nbsp;</option>"+
														"<option value='&[日期]'>&nbsp;&[日期YYYY-MM-DD]&nbsp;</option>"+	
													"</select>"+
												   "</div>" ,
											
											},{   
												x : 400,
												y : 135,
												width : 350,
												height : 20,
												xtype : 'checkbox',
												boxLabel: '仅尾页显示',
												hidden: thispagetail.result.isExcel=='0'?true:false,
												id : "rFootOnlyShow",
												name: 'footShow',
												inputValue: 'rFootChecked'
											},{		
											    width:150,
										        height:90,
										        border : false,
										        id:'colorTapanel',
												hidden:true,
												floating:true,	
												x : 42,//横坐标为距父容器左边缘10像素的位置  
												y : 280,//纵坐标为距父容器上边缘10像素的位置  	 
											    items : [colorPanel]											 																																		
									      }
											]						
						                  },																					
											{
												xtype : 'panel',
												layout : 'column',
												width : '100%',
												border : false,
												padding : '5 0 5 0',
												items : [ tail_fn, tail_fz ]

											},
											{
												//字体粗细，倾斜，下划线和删除线的设置
												xtype : "checkboxgroup",
												id : "ptCheckboxgroup",
												padding : '5 0 5 5',
												width : 300,
												columns : 4,
												flex : 1,
												items : [
														{
															boxLabel : '粗体',
															inputValue : "#fb[1]",
															id : "pt_fb",
															listeners : {
																'change' : function() {
																	if (Ext.getCmp('pt_fb').checked) {
																		ptFb = "900";						 
																		Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																		Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																		Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																	} else {
																		ptFb = 'normal';					 
																		Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																		Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																		Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																	}
																}

															}
														},
														{
															boxLabel : '斜体',
															id : "pt_fi",
															inputValue : "#fi[1]",
															listeners : {
																'change' : function() {
																	if (Ext.getCmp('pt_fi').checked) {
																		ptFi = "italic";					 
																		Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																		Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																		Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																	} else {
																		ptFi = "normal";					 
																		Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																		Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																		Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																	}
																}
															}
														},
														{
															boxLabel : '下划线',
															id : "pt_fu",
															inputValue : "#fu[1]",
															listeners : {
																'change' : function() {																
																	if (Ext.getCmp('pt_fs').checked) {
																		if (Ext.getCmp('pt_fu').checked) {
																			ptFus = "underline line-through";							 
																			Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																		} else {
																			ptFus = "line-through";								 
																			Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																		}
																	} else {
																		if (Ext.getCmp('pt_fu').checked) {
																			ptFus = "underline";							 
																			Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																		} else {
																			ptFus = "none";								 
																			Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																		}
																	}
																}
															}
														},
														{
															boxLabel : '删除线',
															id : "pt_fs",
															inputValue : "#fs[1]",
															listeners : {
																'change' : function() {
																	if (Ext.getCmp('pt_fu').checked) {
																		if (Ext.getCmp('pt_fs').checked) {
																			var ptFus = 'underline line-through';					 
																			Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																		} else {
																			ptFus = "underline";					 
																			Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																		}
																	} else {
																		if (Ext.getCmp('pt_fs').checked) {
																			ptFus = "line-through";					 
																			Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																		} else {
																			ptFus = "none";					 
																			Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																			Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
																		}
																	}
																}
															}
														} ]
											},
											{
												xtype : 'textfield',
												width : 310,
												padding : '5 0 5 7',
												labelWidth : 30,
												readOnly:true,
												id : "colorTail",
												inputId:"colorTail-input",
												fieldStyle : 'background-color:'+ptFcolor+';',
												fieldLabel : '颜色',
												listeners : {
											          click: {
				                                                element: 'el', //bind to the underlying el property on the panel
										                        fn: function(){ 						              
										                                        Ext.getCmp("colorTapanel").show();			              						                                          					             						             					               					              				              
										                                      }
										                     },
										                     'change' : function(field,newValue,oldValue){ 								                 
				                                                 ptFcolor = newValue;
				                                                 Ext.getCmp('colorTail').setFieldStyle('background-color:'+ptFcolor+';');
				                                                 Ext.getCmp('tail_left').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																 Ext.getCmp('tail_center').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');
																 Ext.getCmp('tail_right').setFieldStyle('font-family:'+ptFn+';font-size:'+ptFz+';font-weight:'+ptFb+';font-style:'+ptFi+';text-decoration:'+ptFus+';line-height:120%;color:'+ptFcolor+';');	
				 												 Ext.getCmp("colorTapanel").hide();
				                                             } 
										            }	
											} ]
								} ]

							})
		   Ext.getBody().addListener('click', function(evt, el) {		   
			   if ( "colorTail-input" != el.id && "colorTapanel-input" != el.id  && Ext.getCmp("colorTapanel") != undefined)
				 {  
					Ext.getCmp("colorTapanel").hide();																							 					
				 }	 								      
				 var div = Ext.getCmp("objectp5");
				 if (div && "object_panel5" != el.id && "object_panel6" != el.id && "object_panel7" != el.id && "trTextarea" != el.id && "tlTextarea" != el.id && "tcTextarea" != el.id)
				    {Ext.getCmp("objectp5").hide();
				     Ext.getCmp("objectp6").hide();
				     Ext.getCmp("objectp7").hide();
				    }
				 if( "trTextarea" == el.id )
				    {Ext.getCmp("objectp5").hide();
				     Ext.getCmp("objectp6").hide();				   
				    }else if( "tlTextarea" == el.id )
				    {Ext.getCmp("objectp7").hide();
				     Ext.getCmp("objectp6").hide();				   
				    }else if( "tcTextarea" == el.id )
				    {Ext.getCmp("objectp5").hide();
				     Ext.getCmp("objectp7").hide();				   
				    }
			});									       	    
											
					// 页尾页签数据填充			            
			        Ext.getCmp('tail_left').setValue(thispagetail.result.tail_left);  
	                Ext.getCmp('tail_center').setValue(thispagetail.result.tail_center);  
			        Ext.getCmp('tail_right').setValue(thispagetail.result.tail_right);
			        Ext.getCmp('tail_fn').setValue(thispagetail.result.tail_fontface==''?'仿宋体':thispagetail.result.tail_fontface);  
	                Ext.getCmp('tail_fz').setValue(thispagetail.result.tail_fontsize==''?'10':thispagetail.result.tail_fontsize);  
	                if(thispagehead.result.tail_flw_hs=='lFootChecked'){
	        			Ext.getCmp('lFootOnlyShow').setValue(true);	
	        		}
	        		if(thispagehead.result.tail_fmw_hs=='mFootChecked'){
	        			Ext.getCmp('mFootOnlyShow').setValue(true);	
	        		}
	        		if(thispagehead.result.tail_frw_hs=='rFootChecked'){
	        			Ext.getCmp('rFootOnlyShow').setValue(true);	
	        		}
	                if(thispagetail.result.tail_fontblob=='#fb[1]'){
			        	Ext.getCmp('pt_fb').setValue(true);
			        	ptFb = "900";
			        }else {
			        	ptFb = "normal";
			        }
			        if(thispagetail.result.tail_fontitalic=='#fi[1]'){
			        	Ext.getCmp('pt_fi').setValue(true);
			        	ptFi = "italic";
			        }else {
			        	ptFi = "normal";
			        }
			        if(thispagetail.result.tail_delline=='#fs[1]'){
			        	Ext.getCmp('pt_fs').setValue(true);
			        	ptFus = "line-through";
			        }
			        if(thispagetail.result.tail_underline=='#fu[1]'){
			        	Ext.getCmp('pt_fu').setValue(true);
			        	ptFus = "underline";
			        }
			        
			        if(thispagetail.result.tail_underline=='#fu[1]' && thispagetail.result.tail_delline=='#fs[1]') {
			        	ptFus = "underline line-through";
			        }
			        Ext.getCmp('colorTail').setValue(thispagetail.result.tail_fc);
			        if(thispagetail.result.tail_fc==""){
			        	thispagetail.result.tail_fc = "#000000"; 
			        	  Ext.getCmp('colorTail').setValue(thispagetail.result.tail_fc);  		   
			        }else{
			        	  Ext.getCmp('colorTail').setValue(thispagetail.result.tail_fc);  		   
			        }	 
			        Ext.getCmp('pagetail').add(pagetailPanel);					
			  },
			  tlSelectValue:function(obj_select)
			  {
				  var values=obj_select.options[obj_select.selectedIndex].value;  	     	                     	                
				  var rulearea = Ext.getCmp('tail_left');     
				  var myField = rulearea.inputEl.dom;
				  var element = document.selection;//判断兼容性，如果兼容模式下，其实element不为undefined
				  if (Ext.isIE && element) {
						var sel = null;
						startPos = exportPageSetScope.selectionStart;
						endPos = exportPageSetScope.selectionEnd;
						selectionIndex = exportPageSetScope.selectionIndex;
						myField.focus();
						var rge = exportPageSetScope.range;
						if (rge!=null&&rge.offsetLeft!=0)//用户点击过计算公式部分 offsetLeft==0说明该对象未被渲染到当前页面上
						{ 
							rge.text=values;
							rge.select();
						}
						else
						{//用户未点击过计算公式部分需要创建一个range。让用户选择的选项在开头插入
							var element = document.selection;
							if (element!=null) {
								var rge = element.createRange();
								if (rge!=null)	
								{ 
									rge.text=values;
									rge.select();
								}
							}
						}
						exportPageSetScope.range=rge;
				  }else {
					  var start = rulearea.inputEl.dom.selectionStart;
					  var end = rulearea.inputEl.dom.selectionEnd;
					  var oriValue = rulearea.getValue().toString();
					  rulearea.setValue(oriValue.substring(0, start) + values + oriValue.substring(end));
					  //     Ext.getCmp('titlepage').blur();
					  for(var i=0;i<obj_select.options.length;i++){
				    	  obj_select.options[i].selected = false;
				      }
					  myField.focus();
					  myField.selectionStart = start + values.length;
					  myField.selectionEnd = start + values.length;	
				  }
				  Ext.getCmp('objectp5').hide();
				  
			  },
			  tcSelectValue:function(obj_select)
			  {
				  var values=obj_select.options[obj_select.selectedIndex].value;  	     	                     	                
				  var rulearea = Ext.getCmp('tail_center');       
				  var myField = rulearea.inputEl.dom;
				  if (Ext.isIE) {
						var sel = null;
						startPos = exportPageSetScope.selectionStart;
						endPos = exportPageSetScope.selectionEnd;
						selectionIndex = exportPageSetScope.selectionIndex;
						myField.focus();
						var rge = exportPageSetScope.range;
						if (rge!=null&&rge.offsetLeft!=0)//用户点击过计算公式部分 offsetLeft==0说明该对象未被渲染到当前页面上
						{ 
							rge.text=values;
							rge.select();
						}
						else
						{//用户未点击过计算公式部分需要创建一个range。让用户选择的选项在开头插入
							var element = document.selection;
							if (element!=null) {
								var rge = element.createRange();
								if (rge!=null)	
								{ 
									rge.text=values;
									rge.select();
								}
							}
						}
						exportPageSetScope.range=rge;
				  }else {
					  var start = rulearea.inputEl.dom.selectionStart;
					  var end = rulearea.inputEl.dom.selectionEnd;
					  var oriValue = rulearea.getValue().toString();
					  rulearea.setValue(oriValue.substring(0, start) + values + oriValue.substring(end));
					  //     Ext.getCmp('titlepage').blur();
					  for(var i=0;i<obj_select.options.length;i++){
				    	  obj_select.options[i].selected = false;
				      }
				  }
				  Ext.getCmp('objectp6').hide();
				  
			  },
			  trSelectValue:function(obj_select)
			  {
				  var values=obj_select.options[obj_select.selectedIndex].value;  	     	                     	                
				  var rulearea = Ext.getCmp('tail_right');  
				  var myField = rulearea.inputEl.dom;
				  if (Ext.isIE) {
						var sel = null;
						startPos = exportPageSetScope.selectionStart;
						endPos = exportPageSetScope.selectionEnd;
						selectionIndex = exportPageSetScope.selectionIndex;
						myField.focus();
						var rge = exportPageSetScope.range;
						if (rge!=null&&rge.offsetLeft!=0)//用户点击过计算公式部分 offsetLeft==0说明该对象未被渲染到当前页面上
						{ 
							rge.text=values;
							rge.select();
						}
						else
						{//用户未点击过计算公式部分需要创建一个range。让用户选择的选项在开头插入
							var element = document.selection;
							if (element!=null) {
								var rge = element.createRange();
								if (rge!=null)	
								{ 
									rge.text=values;
									rge.select();
								}
							}
						}
						exportPageSetScope.range=rge;
				  }else {
					  var start = rulearea.inputEl.dom.selectionStart;
					  var end = rulearea.inputEl.dom.selectionEnd;
					  var oriValue = rulearea.getValue().toString();
					  rulearea.setValue(oriValue.substring(0, start) + values + oriValue.substring(end));
					  //     Ext.getCmp('titlepage').blur();
					  for(var i=0;i<obj_select.options.length;i++){
				    	  obj_select.options[i].selected = false;
				      }
				  }
				  Ext.getCmp('objectp7').hide();
				  
			  }  
})
