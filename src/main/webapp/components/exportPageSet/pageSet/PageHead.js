/**

 * 
 */

Ext.define('EHR.exportPageSet.pageSet.PageHead',{
    constructor:function(config){
	    thispagehead = this;
		thispagehead.result = config.result;
		thispagehead.createPanel();
	},
	createPanel:function(){
		var phFn=thispagehead.result.head_fontface;//字体种类		
		var phFz=thispagehead.result.head_fontsize==""?"12":thispagehead.result.head_fontsize + 'px';//字号大小
		var phFb;//字体粗细
	    var phFi;//字体倾斜
	    var phFus = "none";//下划线与删除线	
		var phFcolor=thispagehead.result.head_fc;//字体颜色
	    var sizeStore = Ext.create('Ext.data.Store', {
		fields: ['dataName', 'dataValue'],
		data : [
				{dataName: "5 px",    dataValue: "5"},
				{dataName: "8 px",    dataValue: "8"},
				{dataName:"10 px", dataValue: "10"},
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
		    value: thispagehead.result.head_fc==""?"#000000":thispagehead.result.head_fc,  // 初始选择的颜色		   
		    listeners: {
		        select: function(picker) {		      
		            var color = '#' + picker.value;                 		          
		            Ext.getCmp('colorHead').setFieldStyle('background-color:'+color+';');			           
                    Ext.getCmp('colorHead').setValue(color);                       
		        }
		    }
		});
		
		
		//字体类型名称下拉选择框	
		var head_fn = Ext.create('Ext.form.ComboBox', {
			fieldLabel : '字体',//字体
			labelAlign : 'right',
			labelSeparator : '',
			id : "head_fn",
			inputId:"head_fn-input",
			editable : false,
			labelWidth : 30,
			emptyText : "请选择字体",
			store : fontStore,
			width : 150,
			padding : '5 0 5 0',
			queryMode : 'local',
			displayField : 'dataName',
			valueField : 'dataValue',
			listeners : {//选择一行后触发的事件 
				'select' : function(head_fn) {
				    phFn = head_fn.getValue();					 
					Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
					Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
					Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');				
				}
			}
		});
		
		
	
		
		//字体大小下拉选择框
		var head_fz = Ext.create('Ext.form.ComboBox', {
			fieldLabel : '大小',//大小
			labelAlign : 'right',
			labelSeparator : '',
			id : "head_fz",
			inputId:"head_fz-input",
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
				'select' : function(head_fz) {
				    phFz = head_fz.getValue() + 'px';	
				    var head_left_value = Ext.getCmp('head_left').getValue();
				    var head_center_value = Ext.getCmp('head_center').getValue();
				    var head_right_value = Ext.getCmp('head_right').getValue();
				    Ext.getCmp('head_left').setValue('');
				    Ext.getCmp('head_center').setValue('');
				    Ext.getCmp('head_right').setValue('');
					Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
					Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
					Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
				    Ext.getCmp('head_left').setValue(head_left_value);
				    Ext.getCmp('head_center').setValue(head_center_value);
				    Ext.getCmp('head_right').setValue(head_right_value);
				}
			}
		});
		
	
		var pageHeadPanel = Ext.widget({
			xtype : 'form',
			id : "pageheadid",
			border : false,
			padding : '5 5 5 5',
			items : [ {
				padding : '5 5 5 5',
				xtype : 'fieldset',
				autoHeight : true,
				height : 320,
				title : '表头内容',
				items : [
					{											
						xtype : 'form',
						layout:'absolute',  
						height : 180,
						id : "headlcr",
						border : false,												
						items : [  
							{
								xtype : 'label',
								padding : '20 0 5 7',
								text : '上左内容'
							},   
							{
								x : 90,//横坐标为距父容器左边缘5像素的位置  
								y : 0,//纵坐标为距父容器上边缘5像素的位置  	  
								width : thispagehead.result.isExcel=='0'?350:300,
								height : 55,
								xtype : 'textarea',
								id : "head_left",																	
								inputId:"hlTextarea",
								fieldStyle:'font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';',
								listeners : {
									click: {
										element: 'el', //bind to the underlying el property on the panel
										fn: function(){ 
											Ext.getCmp("objectH_left").show();	
											if(Ext.isIE){
											   exportPageSetScope.getCursorPosition("head_left"); //获得光标位置
							            	}
										}
									},
									keyup : {
										element: 'el', //bind to the underlying el property on the panel
										fn: function(){ Ext.getCmp("objectH_left").hide();	}
									},
									blur: function(){
										//setTimeout( 1000);
										//Ext.getCmp("objectH_left").hide()

									} 


								}
							},														
							{
								x : 90,//横坐标为距父容器左边缘10像素的位置  
								y : 55,//纵坐标为距父容器上边缘10像素的位置  
								xtype : 'panel',
								id:'objectH_left',
								hidden:true,
								floating:true,
								border : false,
								html:
									"<div id='object_head'>"+         
									"<select name='object_rate' multiple='multiple' size='6'  style='width:200' onchange='thispagehead.hlSelectValue(this);'>"+    
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
								boxLabel: '仅首页显示',
								hidden: thispagehead.result.isExcel=='0'?true:false,
								id : "lHeadOnlyShow",
								name: 'homeShow',
								inputValue: 'lHeadChecked'
							},{
								y : 60,
								xtype : 'label',
								padding : '20 0 5 7',
								text : '上中内容'
							}, 
							{   
								x : 90,//横坐标为距父容器左边缘10像素的位置  
								y : 60,//纵坐标为距父容器上边缘10像素的位置  
								width : thispagehead.result.isExcel=='0'?350:300,
								height : 55,
								xtype : 'textarea',
								id : "head_center",																	
								inputId:"hcTextarea",
								fieldStyle:'font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';',												
								listeners : {
									click: {
										element: 'el', //bind to the underlying el property on the panel
										fn: function(){ 
											Ext.getCmp("objectH_center").show();	
											if(Ext.isIE){
											   exportPageSetScope.getCursorPosition("head_center"); //获得光标位置
							            	}
										}
									},	
									keyup : {
										element: 'el', //bind to the underlying el property on the panel
										fn: function(){ Ext.getCmp("objectH_center").hide();	}
									}
								}
							},{

								x : 90,//横坐标为距父容器左边缘10像素的位置  
								y : 115,//纵坐标为距父容器上边缘10像素的位置  
								xtype : 'panel',
								id:'objectH_center',
								hidden:true,
								floating:true,
								border : false,
								//'<div id="object_head"></div>' ,
								html:  
									"<div id='object_head'>"+         
									"<select name='object_rate' multiple='multiple' size='6'  style='width:200' onchange='thispagehead.hcSelectValue(this);'>"+    
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
								boxLabel: '仅首页显示',
								hidden: thispagehead.result.isExcel=='0'?true:false,
								id : "mHeadOnlyShow",
								name: 'homeShow',
								inputValue: 'mHeadChecked'
							},{
								y : 120,
								xtype : 'label',
								padding : '20 0 5 7',
								text : '上右内容'
							}, 
							{
								x : 90,//横坐标为距父容器左边缘10像素的位置  
								y : 120,//纵坐标为距父容器上边缘10像素的位置  
								width : thispagehead.result.isExcel=='0'?350:300,
								height : 55,
								xtype : 'textarea',
								id : "head_right",																
								inputId:"hrTextarea",
								fieldStyle:'font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';',
								listeners : {
									click: {
										element: 'el', //bind to the underlying el property on the panel
										fn: function(){ 
											Ext.getCmp("objectp4").show();	
											if(Ext.isIE){
											   exportPageSetScope.getCursorPosition("head_right"); //获得光标位置
							            	}
										}
									},
									keyup : {
										element: 'el', //bind to the underlying el property on the panel
										fn: function(){ Ext.getCmp("objectp4").hide();	}
									}
								}
							},

							{
								x : 90,//横坐标为距父容器左边缘10像素的位置  
								y : 175,//纵坐标为距父容器上边缘10像素的位置  
								xtype : 'panel',
								id:'objectp4',
								hidden:true,
								floating:true,
								border : false,
								html:
									"<div id='object_head'>"+         
									"<select name='object_rate' multiple='multiple' size='6'  style='width:200' onchange='thispagehead.hrSelectValue(this);'>"+    
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
								boxLabel: '仅首页显示',
								hidden: thispagehead.result.isExcel=='0'?true:false,
								id : "rHeadOnlyShow",
								name: 'homeShow',
								inputValue: 'rHeadChecked'
							},{		
								    width:150,
							        height:90,
							        border : false,
							        id:'colorHpanel',
									hidden:true,
									floating:true,	
									x : 42,//横坐标为距父容器左边缘10像素的位置  
									y : 280,//纵坐标为距父容器上边缘10像素的位置  	 
								    items : [colorPanel]											 																																		
						      }]
					},
					{
						xtype : 'panel',
						layout : 'column',
						width : '100%',
						border : false,
						padding : '5 0 5 0',
						items : [ head_fn, head_fz ]

					},
					{
						//字体粗细，倾斜，下划线和删除线的设置
						xtype : "checkboxgroup",
						id : "phCheckboxgroup",
						padding : '5 0 5 5',
						width : 300,
						columns : 4,
						flex : 1,						
						items : [
							{
								boxLabel : '粗体',
								inputValue : "#fb[1]",
								id : "ph_fb",
								listeners : {
									'change' : function() {
										if (Ext.getCmp('ph_fb').checked) {
											phFb = "900";								 
											Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
											Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
											Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
										} else {
											phFb = 'normal';							 
											Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
											Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
											Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
										}
									}

								}
							},
							{
								boxLabel : '斜体',
								id : "ph_fi",
								inputValue : "#fi[1]",
								listeners : {
									'change' : function() {
										if (Ext.getCmp('ph_fi').checked) {
											phFi = "italic";						 
											Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
											Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
											Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
										} else {
											phFi = "normal";								 
											Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
											Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
											Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
										}
									}
								}
							},
							{
								boxLabel : '下划线',
								id : "ph_fu",
								inputValue : "#fu[1]",
								listeners : {
									'change' : function() {
										if (Ext.getCmp('ph_fs').checked) {
											if (Ext.getCmp('ph_fu').checked) {
												phFus = "underline line-through";							 
												Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
											} else {
												phFus = "line-through";								 
												Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
											}
										} else {
											if (Ext.getCmp('ph_fu').checked) {
												phFus = "underline";							 
												Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
											} else {
												phFus = "none";								 
												Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
											}
										}
									}
								}
							},
							{
								boxLabel : '删除线',
								id : "ph_fs",
								inputValue : "#fs[1]",
								listeners : {
									'change' : function() {
										if (Ext.getCmp('ph_fu').checked) {
											if (Ext.getCmp('ph_fs').checked) {
												phFus = "underline line-through";								 
												Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
											} else {
												phFus = "underline";							 
												Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
											}
										} else {
											if (Ext.getCmp('ph_fs').checked) {
												phFus = "line-through";							 
												Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
											} else {
												phFus = "none";							 
												Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
												Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
											}
										}
									}
								}
							} ]
					},
					{
						
						layout : 'column',
						border : false,
				items : [ {
							xtype : 'label',
							padding : '5 12 5 7',
							text : '颜色'						
					   },						
		             {      
						xtype : 'textfield',		         
						width : 280,
						id : "colorHead",
						inputId:"colorHead-input",												
						readOnly:true,
						fieldStyle : 'background-color:'+phFcolor+';',						
						listeners : {
							click: {
								element: 'el', 
						        fn: function(){ 						              
						        	Ext.getCmp("colorHpanel").show();			              						                                          					             						             					               					              				              
						        }
						 },
						    'change' : function(field,newValue,oldValue){ 								                 
						    	phFcolor = newValue;
                                Ext.getCmp('colorHead').setFieldStyle('background-color:'+phFcolor+';');						              
                                Ext.getCmp('head_left').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
 								Ext.getCmp('head_center').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');
 							    Ext.getCmp('head_right').setFieldStyle('font-family:'+phFn+';font-size:'+phFz+';font-weight:'+phFb+';font-style:'+phFi+';text-decoration:'+phFus+';line-height:120%;color:'+phFcolor+';');	
 								Ext.getCmp("colorHpanel").hide();
                             } 
					  }
				    }]
					}] 

			} ]

		})
		Ext.getBody().addListener('click', function(evt, el) {					
			if ("colorHead-input" !=el.id && "colorHpanel-input" != el.id && "colorpanel-input" != el.id && Ext.getCmp("colorHpanel") != undefined)
			 { 
				 Ext.getCmp("colorHpanel").hide();					
			 }			
			var div = Ext.getCmp("objectH_left");
			if (div && "object_head" != el.id  && "hrTextarea" != el.id && "hlTextarea" != el.id && "hcTextarea" != el.id)
			{
			    Ext.getCmp("objectH_left").hide();
			    Ext.getCmp("objectH_center").hide();
			    Ext.getCmp("objectp4").hide();
			}
			if( "hrTextarea" == el.id )
			{
				Ext.getCmp("objectH_left").hide();
			    Ext.getCmp("objectH_center").hide();				   
			}else if( "hlTextarea" == el.id )
			{
				Ext.getCmp("objectp4").hide();
			    Ext.getCmp("objectH_center").hide();				   
			}else if( "hcTextarea" == el.id )
			{
				Ext.getCmp("objectH_left").hide();
			    Ext.getCmp("objectp4").hide();				   
			}
		});
		// 页头页签数据填充		
		Ext.getCmp('head_left').setValue(thispagehead.result.head_left);  
		Ext.getCmp('head_center').setValue(thispagehead.result.head_center);  
		Ext.getCmp('head_right').setValue(thispagehead.result.head_right);
		Ext.getCmp('head_fn').setValue(thispagehead.result.head_fontface==''?'仿宋体':thispagehead.result.head_fontface);  
		Ext.getCmp('head_fz').setValue(thispagehead.result.head_fontsize==""?"10":thispagehead.result.head_fontsize);
		if(thispagehead.result.head_flw_hs=='lHeadChecked'){
			Ext.getCmp('lHeadOnlyShow').setValue(true);	
		}
		if(thispagehead.result.head_fmw_hs=='mHeadChecked'){
			Ext.getCmp('mHeadOnlyShow').setValue(true);	
		}
		if(thispagehead.result.head_frw_hs=='rHeadChecked'){
			Ext.getCmp('rHeadOnlyShow').setValue(true);	
		}
		if(thispagehead.result.head_fontblob=='#fb[1]'){
			Ext.getCmp('ph_fb').setValue(true);			
			phFb = "900";
        }else {
        	phFb = "normal";
        }
		if(thispagehead.result.head_fontitalic=='#fi[1]'){
			Ext.getCmp('ph_fi').setValue(true);		
			phFi = "italic";
        }else {
        	phFi = "normal";
        }
		if(thispagehead.result.head_delline=='#fs[1]'){
			Ext.getCmp('ph_fs').setValue(true);
			phFus = "line-through";
		}
		if(thispagehead.result.head_underline=='#fu[1]'){
			Ext.getCmp('ph_fu').setValue(true);
			phFus = "underline";
        }
		if(thispagehead.result.head_underline=='#fu[1]' && thispagehead.result.head_delline=='#fs[1]') {
			phFus = "underline line-through";
        }
		if(thispagehead.result.head_fc==""){
        	thispagehead.result.head_fc = "#000000"; 
        	Ext.getCmp('colorHead').setValue(thispagehead.result.head_fc);  
        }else{
        	Ext.getCmp('colorHead').setValue(thispagehead.result.head_fc);  
        }	 
		Ext.getCmp('pagehead').add(pageHeadPanel);	
	},
	hlSelectValue:function(obj_select)
	{
		var values=obj_select.options[obj_select.selectedIndex].value;  	     	                     	                
		var rulearea = Ext.getCmp('head_left');         
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
		Ext.getCmp('objectH_left').hide();				  
	},
	hcSelectValue:function(obj_select)
	{
		var values=obj_select.options[obj_select.selectedIndex].value;  	     	                     	                
		var rulearea = Ext.getCmp('head_center');   
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
		Ext.getCmp('objectH_left').hide();				  
	},
	hrSelectValue:function(obj_select)
	{
		var values=obj_select.options[obj_select.selectedIndex].value;  	     	                     	                
		var rulearea = Ext.getCmp('head_right');   
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
		Ext.getCmp('objectp4').hide();				  
	}
})
