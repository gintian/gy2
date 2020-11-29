/**
 * 
 */

Ext.define('EHR.exportPageSet.pageSet.PageTitle',{
	  constructor:function(config){
		  thistitle = this;
		  thistitle.result = config.result;
		  thistitle.createPanel();
	        },
			createPanel:function(){	
				  var pageFn=thistitle.result.title_fontface;//字体种类
				  var pageFz=thistitle.result.title_fontsize==''?'12':thistitle.result.title_fontsize + 'px';//字号大小
				  var pageFb;//字体粗细
				  var pageFi;//字体倾斜
				  var pageFus = "none";//下划线与删除线	
				  var pageFcolor=thistitle.result.title_color;//字体颜色
				  var sizeStore = Ext.create('Ext.data.Store', {
			      fields: ['dataName', 'dataValue'],
				  data : [
						 {dataName: "5 px", dataValue: "5"},
				         {dataName: "8 px", dataValue: "8"},
				         {dataName:"10 px", dataValue: "10"},
				         {dataName:"12 px", dataValue: "12"},
				         {dataName:"14 px", dataValue: "14"},
				         {dataName:"16 px", dataValue: "16"},
				         {dataName:"24 px", dataValue: "24"},
				         {dataName:"32 px", dataValue: "32"},
				         {dataName:"48 px", dataValue: "48"}				        
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
						 value: thistitle.result.title_color==""?"#000000":thistitle.result.title_color,  // 初始选择的颜色	 
						autoShow: true, 
					    listeners: {
					        select: function(picker) {		      
					            var color = '#' + picker.value;					           
					            Ext.getCmp('colorTitle').setFieldStyle('background-color:'+color+';');			           
			                    Ext.getCmp('colorTitle').setValue(color);                       
					        }
					    }
					});
					
					
					  
	  //字体类型名称下拉选择框		  				  	
	    var  title_fn = Ext.create('Ext.form.ComboBox', {
		fieldLabel :  '字体',//字体
		labelAlign : 'right',
		labelSeparator : '',
		editable : false,
		id : "title_fn",
		inputId:"title_fn-input",
		labelWidth : 30,
		emptyText : "请选择字体",
		store : fontStore,
		width : 150,
		padding : '5 0 5 0',
		queryMode : 'local',
		displayField : 'dataName',
		valueField : 'dataValue',
		listeners : {//选择一行后触发的事件 
			'select' : function(title_fn) {
				 pageFn = title_fn.getValue();		
				 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
												
			   }
		   }		
	});
	 //字体大小下拉选择框
	var title_fz = Ext.create('Ext.form.ComboBox', {
		fieldLabel : '大小',//大小
		labelAlign : 'right',
		labelSeparator : '',
		id : "title_fz",
		inputId:"title_fz-input",
		editable : false,
		labelWidth : 30,
		store : sizeStore,
		width : 160,
		padding : '5 0 5 10',
		queryMode : 'local',
		emptyText : "请选择大小",
		displayField : 'dataName',
		valueField : 'dataValue',
		listeners : {//选择一行后触发的事件 
			'select' : function(title_fz) {
				 pageFz = title_fz.getValue() + 'px';
				 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
								
			}
		}										
	});
    var titlePanel = Ext.widget({
		xtype : 'form',
		id : "titleid",
		border : false,
		padding : '5 5 5 5',		
		items : [ {
			padding : '5 5 5 5',
			xtype : 'fieldset',
			autoHeight : true,
			height : 330,
			title : '标题内容',
			items : [ 																					
				      {							
					    xtype : 'form',
					    layout:'absolute',  
						height : 180,						
						border : false,
						
                     items : [ 
						  {   
							x : 50,//横坐标为距父容器左边缘10像素的位置  
							y : 5,//纵坐标为距父容器上边缘10像素的位置  	  
							width : 410,
							height : 150,
							border : false,
							id : "titlepage",
							inputId:"titleTextarea",
							xtype : 'textarea',		
							emptyText : '请输入标题内容',
							fieldStyle:'font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';',
							listeners : {
							       click: {
		                                       element: 'el', 
											   fn: function(){											            
												   Ext.getCmp("objectp1").show();		
												   if(Ext.isIE){
													   exportPageSetScope.getCursorPosition("titlepage"); //获得光标位置
			                                       }
											   }
										},
									keyup : {
											element: 'el', //bind to the underlying el property on the panel
											fn: function(){ Ext.getCmp("objectp1").hide();	}
										}
								}													
						},
										{
											x : 50,//横坐标为距父容器左边缘10像素的位置  
											y : 155,//纵坐标为距父容器上边缘10像素的位置  
											xtype : 'panel',
											id:'objectp1',
											hidden:true,
											floating:true,
											border : false,
											html:
												   "<div id='object_title'>"+         
											   	"<select name='object_rate2' multiple='multiple' size='4'  style='width:200' onchange='thistitle.setSelectValue(this);'>"+    
													"<option value='&[年月]'>&nbsp;&[年月YYYY-MM]&nbsp;</option>"+
													"<option value='&[YYYY年YY月]'>&nbsp;&[YYYY年MM月]&nbsp;</option>"+
													"<option value='&[单位名称]'>&nbsp;&[单位名称]&nbsp;</option>"+
													"<option value='&[报表名称]'>&nbsp;&[报表名称]&nbsp;</option>"+											
												"</select>"+
											   "</div>",							  									 
										}]
							},
							{
								xtype : 'panel',
								layout : 'column',
								width : '100%',
								border : false,
								padding : '5 0 5 0',
								items : [ title_fn, title_fz ]
							},
							{
								xtype : "checkboxgroup",
								padding : '5 0 5 5',
								width : 300,
								columns : 4,
								flex : 1,
								id : "checkboxgroupTitle",
								items : [
										{
											boxLabel : '粗体',
											inputValue : "#fb[1]",
											id : "title_fb",																			
											listeners : {
												'change' : function() {
													if (Ext.getCmp('title_fb').checked) {
														 pageFb = "900";
														 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
													} else {
														 pageFb = 'normal';
														 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
													}
												}
											}
										},
										{
											boxLabel : '斜体',
											id : "title_fi",
											inputValue : "#fi[1]",
											listeners : {
												'change' : function() {
													if (Ext.getCmp('title_fi').checked) {
														 pageFi = "italic";
														 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
													} else {
														 pageFi = "normal";
														 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
													}
												}
											}
										},
										{
											boxLabel : '下划线',
											id : "title_fu",
											inputValue : "#fu[1]",
											listeners : {
												'change' : function() {
													if (Ext.getCmp('title_fs').checked) {
														if (Ext.getCmp('title_fu').checked) {
															 pageFus = "underline line-through";
															 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
														} else {
															 pageFus = "line-through";
															 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
														}
													} else {
														if (Ext.getCmp('title_fu').checked) {
															 pageFus = "underline";
															 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
														} else {
															 pageFus = "none";
															 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
														}
													}
												}
											}
										},
										{
											boxLabel : '删除线',
											id : "title_fs",
											inputValue : "#fs[1]",
											listeners : {
												'change' : function() {
													if (Ext.getCmp('title_fs').checked) {
														if (Ext.getCmp('title_fu').checked) {
															 pageFus = "underline line-through";
															 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
														} else {
															 pageFus = "line-through";
															 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
														}
													} else {
														if (Ext.getCmp('title_fu').checked) {
															 pageFus = "underline";
															 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
						                            } else {
															 pageFus = "none";
															 Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
														}
													}
												}
											}
										}]
							},							
							{
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
								id : "colorTitle",
								inputId:"colorTitle-input",
								fieldStyle : 'background-color:'+pageFcolor+';',
								listeners : {
									  click: {
                                            element: 'el', //bind to the underlying el property on the panel
								            fn: function(){ 								            
					                            Ext.getCmp("colorTpanel").show();								          								                         							               				                           					               								               
					                           }
								             },								        
								        'change' : function(field,newValue,oldValue){ 								                 
			                                    pageFcolor = newValue;
			                                    Ext.getCmp('colorTitle').setFieldStyle('background-color:'+pageFcolor+';');						              
			                                    Ext.getCmp('titlepage').setFieldStyle('font-family:'+pageFn+';font-size:'+pageFz+';font-weight:'+pageFb+';font-style:'+pageFi+';text-decoration:'+pageFus+';line-height:120%;color:'+pageFcolor+';');
			                                    Ext.getCmp("colorTpanel").hide();
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
								id:'colorTpanel',																									
								items : [colorPanel] 									 																																		
						      }]
					   }]
				} ]
			})		
			
			Ext.getBody().addListener('click', function(evt, el) {	        	    
					if ( "colorTitle-input" != el.id && "colorTpanel-input" != el.id  && "colorpanel-input" != el.id  && Ext.getCmp("colorTpanel") != undefined)
					 {  
						Ext.getCmp("colorTpanel").hide();																							 					
					 }	 					
				                                                        }
				        );	
			
			 // 标题页签数据填充   
	        Ext.getCmp("titlepage").setValue(thistitle.result.title_content);	
            Ext.getCmp('title_fn').setValue(thistitle.result.title_fontface==''?'仿宋体':thistitle.result.title_fontface);           
	        Ext.getCmp('title_fz').setValue(thistitle.result.title_fontsize==''?'14':thistitle.result.title_fontsize);	       
	        if(thistitle.result.title_fontblob=='#fb[1]')
	        {	       
	        	Ext.getCmp('title_fb').setValue(true);	        	
	        	pageFb = "900";
	        }else {
	        	pageFb = "normal";
	        }
	        if(thistitle.result.title_fontitalic=='#fi[1]')
	        {	        
	        	Ext.getCmp('title_fi').setValue(true);	       
	        	pageFi = "italic";
	        }else {
	        	pageFi = "normal";
	        }
	        if(thistitle.result.title_delline=='#fs[1]')
	        {
	        	Ext.getCmp('title_fs').setValue(true);
	        	pageFus = "line-through";
	        }
	        if(thistitle.result.title_underline=='#fu[1]')
	        {
	        	Ext.getCmp('title_fu').setValue(true);
	        	pageFus = "underline";
	        }
	        
	        if(thistitle.result.title_underline=='#fu[1]' && thistitle.result.title_delline=='#fs[1]') {
	        	pageFus = "underline line-through";
	        }
	        
	        if(thistitle.result.title_color==""){
	        	thistitle.result.title_color = "#000000"; 
	        	Ext.getCmp('colorTitle').setValue(thistitle.result.title_color);
            }else{
            	Ext.getCmp('colorTitle').setValue(thistitle.result.title_color);
            }	      
			Ext.getCmp('title').add(titlePanel);				
			Ext.getBody().addListener('click', function(evt, el){				 												
				                      var div = Ext.getCmp("objectp1");
				                      if (div && "object_title" != el.id && "titleTextarea" != el.id)
					                  Ext.getCmp("objectp1").hide();				 				
			                                                     }
			                         );			
			  },
			  setSelectValue:function(obj_select)
			  {
				  var values=obj_select.options[obj_select.selectedIndex].value;  				
				  var rulearea = Ext.getCmp('titlepage');   
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
				  Ext.getCmp('objectp1').hide();				  
			  },
			  postpone:function()
			  {					
								  
			  }
})



