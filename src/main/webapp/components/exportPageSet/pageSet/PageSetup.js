/**
 * 
 */
Ext.define('EHR.exportPageSet.pageSet.PageSetup',{
    constructor:function(config){
	    thispagesetup= this;
		thispagesetup.result = config.result;	 
		thispagesetup.createPanel();
			},
			createPanel:function(){								  				
				  var paperSizeStore = Ext.create('Ext.data.Store', {
				  fields: ['dataName', 'dataValue'],
				  data : [
						 {dataName: 'A3',    dataValue: 'A3'},
				         {dataName: 'A4',    dataValue: 'A4'},
				         {dataName: 'A5', dataValue: 'A5'},
				         {dataName: 'B5', dataValue: 'B5'},
				         {dataName: '16开', dataValue: '16开'},
				         {dataName: '32开', dataValue: '32开'},
				         {dataName: '自定义', dataValue: 'self'}
				     ]
					});								  
	
				  var pageType = Ext.create('Ext.form.ComboBox', {
				      fieldLabel: '大小',//纸张大小
					  labelAlign:'right',
					  labelSeparator :'',
					  emptyText : "请选择",
					  id:"pagetype",
					  inputId:"pagetype-input",
					  labelWidth:30,
				 	  store: paperSizeStore,
				 	  width:158,
				 	  padding:'5 0 5 0',
				 	  queryMode: 'local',
				 	  hiddenName:"faId",
				 	  displayField: 'dataName',
				 	  valueField: 'dataValue',	    	
					listeners : {//选择一行后触发的事件 
										'select' : function(pageType) {
											var pageMode = pageType.getValue();
											if (pageMode == "A3") {
												Ext.getCmp('pagewidth').setReadOnly(true);
												Ext.getCmp('pageheight').setReadOnly(true);
												Ext.getCmp('pagewidth').setValue(297);
												Ext.getCmp('pageheight').setValue(420);
											}
											if (pageMode == "A4") {

												Ext.getCmp('pagewidth').setReadOnly(true);
												Ext.getCmp('pageheight').setReadOnly(true);
												Ext.getCmp('pagewidth').setValue(210);
												Ext.getCmp('pageheight').setValue(297);
											}
											if (pageMode == "A5") {
												readOnly: true;
												Ext.getCmp('pagewidth').setReadOnly(true);
												Ext.getCmp('pageheight').setReadOnly(true);
												Ext.getCmp('pagewidth').setValue(148);
												Ext.getCmp('pageheight').setValue(210);
											}
											if (pageMode == "B5") {
												readOnly: true;
												Ext.getCmp('pagewidth').setReadOnly(true);
												Ext.getCmp('pageheight').setReadOnly(true);
												Ext.getCmp('pagewidth').setValue(182);
												Ext.getCmp('pageheight').setValue(257);
											}
											if (pageMode == "16开") {
												Ext.getCmp('pagewidth').setReadOnly(true);
												Ext.getCmp('pageheight').setReadOnly(true);
												Ext.getCmp('pagewidth').setValue(184);
												Ext.getCmp('pageheight').setValue(260);
											}
											if (pageMode == "32开") {
												Ext.getCmp('pagewidth').setReadOnly(true);
												Ext.getCmp('pageheight').setReadOnly(true);
												Ext.getCmp('pagewidth').setValue(130);
												Ext.getCmp('pageheight').setValue(184);
											}
											if (pageMode == "self") {
												Ext.getCmp('pagewidth').setReadOnly(false);
												Ext.getCmp('pageheight').setReadOnly(false);
												Ext.getCmp('pagewidth').setDisabled(false);
												Ext.getCmp('pageheight').setDisabled(false);
												Ext.getCmp('widthMM').setDisabled(false);
												Ext.getCmp('heightMM').setDisabled(false);
												Ext.getCmp('pagewidth').setValue();
												Ext.getCmp('pageheight').setValue();
											}
											if(pageMode != 'self') {
												Ext.getCmp('pagewidth').setDisabled(true);//置灰
												Ext.getCmp('pageheight').setDisabled(true);
												Ext.getCmp('widthMM').setDisabled(true);
												Ext.getCmp('heightMM').setDisabled(true);
											}
										}
									}
								});					
								var pagesetupPanel = Ext.widget({
									xtype : 'form',
									id : "pagesetupid",
									border : false,
									padding : '5 0 5 0',
									inputId:"pagesetupid-input",
									items : [ {
										xtype : 'fieldset',
										autoHeight : true,
										title : '纸张',
										height : 150,										
										items : [ {
											xtype : 'panel',
											width : '100%',
											border : false,
											items : [ pageType ]
										}, {
											layout : 'column',
											border : false,
											items : [ {
												xtype : 'textfield',
												padding : '5 0 5 7',
												width : 150,
												labelWidth : 23,											  
												regex:/^[1-9]\d*$/, 
												regexText:"只能输入正整数！",
												id : "pagewidth",
												inputId : "pagewidth-input",
												fieldLabel : '宽'

											}, {
												xtype : 'label',
												padding : '5 20 5 0',
												id : 'widthMM',
												text : 'mm'
											}, {
												xtype : 'textfield',
												width : 150,
												padding : '5 0 5 0',
												labelWidth : 20,
												id : "pageheight",
												inputId : "pageheight-input",
												regex:/^[1-9]\d*$/, 											
												regexText:"只能输入正整数!",
												fieldLabel : '高'

											}, {
												xtype : 'label',
												padding : '5 20 5 0',
												id : 'heightMM',
												text : 'mm'
											} ]
										}, {
											xtype : 'radiogroup',
											fieldLabel : '排列',
											padding : '5 0 5 7',
											id : 'page_range',
											inputId : "page_range-input",
											items : [ {
												boxLabel : '纵向',
												name : 'Orientation',
												inputValue : '0',
												checked : true
											}, {
												boxLabel : '横向',
												name : 'Orientation',
												inputValue : '1'
											} ]

										} ]
									}, {
										layout : 'form',
										xtype : 'fieldset',
										padding : '5 0 5 3',
										height : 150,
										autoHeight : true,
										title : '页面边距',
										items : [ {
											layout : 'column',
											border : false,
											items : [ {
												xtype : 'textfield',
												id : "pagetop",
												inputId:"pagetop-input",
												padding : '5 0 5 7',
												width : 150,
												labelWidth :20,
												regex: /^[0-9]\d*$/,
												regexText:"请输入非负整数！",
												fieldLabel : '上'
											}, {
												xtype : 'label',
												padding : '5 20 5 0',
												text : 'mm'
											}, {
												xtype : 'textfield',
												padding : '5 0 5 0',
												width : 150,
												labelWidth : 20,
												id : "pagebottom",
												inputId : "pagebottom-input",
												regex: /^[0-9]\d*$/,
												regexText:"请输入非负整数！",
												fieldLabel : '下'
											}, {
												xtype : 'label',
												padding : '5 20 5 0',
												text : 'mm'
											} ]
										}, {

											layout : 'column',
											border : false,
											items : [ {
												xtype : 'textfield',												
												id : "pageleft",
												inputId : "pageleft-input",
												regex: /^[0-9]\d*$/,
												regexText:"请输入非负整数！",
												padding : '5 0 5 7',
												width : 150,
												labelWidth : 20,
												fieldLabel : '左'
											}, {
												xtype : 'label',
												padding : '5 20 5 0',
												text : 'mm'
											}, {
												xtype : 'textfield',											
												padding : '5 0 5 0',
												width : 150,
												labelWidth : 20,									
												regex: /^[0-9]\d*$/,
												regexText:"请输入非负整数！",											
												id : "pageright",
												inputId : "pageright-input",
												fieldLabel : '右'
											}, {
												xtype : 'label',
												padding : '5 0 5 0',
												text : 'mm'
											} ]

										} ]
									} ]
								});
								Ext.getCmp('pagetype').setValue( thispagesetup.result.Pagetype);
					            Ext.getCmp('pageleft').setValue( thispagesetup.result.Left==""?'25':thispagesetup.result.Left);  
						        Ext.getCmp('pagetop').setValue( thispagesetup.result.Top==""?'21':thispagesetup.result.Top);
						        Ext.getCmp('pageright').setValue( thispagesetup.result.Right==""?'25':thispagesetup.result.Right);  
					            Ext.getCmp('pageheight').setValue( thispagesetup.result.Height);  
						        Ext.getCmp('pagewidth').setValue( thispagesetup.result.Width);
						        if(thispagesetup.result.Pagetype != 'self') {
							        Ext.getCmp('pagewidth').setReadOnly(true);
									Ext.getCmp('pageheight').setReadOnly(true);
									Ext.getCmp('pagewidth').setDisabled(true);
									Ext.getCmp('pageheight').setDisabled(true);
						        }
						        Ext.getCmp('pagebottom').setValue( thispagesetup.result.Bottom==""?'21':thispagesetup.result.Bottom);  
						        Ext.getCmp('page_range').setValue({Orientation: [ thispagesetup.result.Orientation]});
			                    Ext.getCmp('pagesetup').add(pagesetupPanel);	
			  }
			  
})