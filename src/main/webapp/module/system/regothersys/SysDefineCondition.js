 /*
 *
 *调用示例：
 *	Ext.Loader.setPath("DECN","/components/definecondition");
 *	Ext.require('DECN.DefineCondition',function(){
 *		Ext.create("DECN.DefineCondition",{primarykey:primarykey,opt:"2",mode:'temp',afterfunc:'GzGlobal.getCon'});
 *	});
 * primarykey 为传递的参数
 * imodule 模块号   0时薪资发放--批量修改;   1：薪资发放计算公式;3:薪资属性提成工资数据范围  2:人事异动模块批量修改-条件组件
 * dataMap:HashMap对象，存放不同模块需要的参数，key:setid(子集id)已经使用
 * afterfunc 为回调函数，函数名自定义，参数为conTextareaId文本域的值。
 * 
 * opt:'',//函数向导模块标识   1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
 * mode:'', //函数向导 “xzgl_jsgs”具体功能点标识，xzgl_jsgs代表薪资计算公式调用
 *
 */
 Ext.Loader.loadScript({url:'/components/codeSelector/codeSelector.js'});
 Ext.define('defineUL.SysDefineCondition',{
    constructor:function(config){
    	me = this;
    	me.dataMap = config.dataMap;//一个HashMap对象，存放不同模块需要的参数，key:setid(子集id)已经使用
    	me.saveText = common.button.saveConditions;
    	if(me.dataMap){
    		if(me.dataMap.saveText)
    			me.saveText = me.dataMap.saveText;
    		if(me.dataMap.itemsetid)
    			me.itemsetid = me.dataMap.itemsetid;
    	}
    	me.selectionStart = 0;//光标起选中始位置
		me.selectionEnd = 0;//光标选中结束位置
		me.selectionIndex = 0;//光标位置
		
		opt = config.opt;
		method = config.method;
		conditions = config.conditions;
		afterfunc = config.afterfunc;
    	this.init();
    },
    init:function(){
    	//取得参考项目中项目的store
	 	var store = Ext.create('Ext.data.Store',{
		 	fields:['dataName','dataValue'],
			proxy:{
				type: 'transaction',
				functionId:'SYS00000003',
				extraParams:{
					method:"init",
					opt:opt,
					primarykey:"",
					imodule:"",
					conditions:conditions,
					itemsetid:""
					},
				reader: {
					 type: 'json',
					root: 'itemlist'         	
				 }
			}
			//autoLoad: true//自动加载
		});
			
		//小键盘样式
		var style = 'margin-top:3px;margin-left:1px';
		var style1 = 'margin-top:3px;margin-left:1px';
		//小键盘宽度
		var width = 23;
		var width2 = 27;
		//定义弹出的窗口
	 	conWin = Ext.create('Ext.Window',{
	 			title:common.label.conditionStr,//'条件表达式'
			 	width: 600,  
			    height:500,  
			    resizable: false, 
			    modal: true,
			    border:false,
			    items:[{
			    	xtype:'panel',
			    	anchor:'90%',
			    	border:false,
			    	style:'margin-left:20px;margin-top:10px',
			    	items:[{
			    		xtype:'textarea',//定义条件的文本域
			    		width: 550,  
			    		height:230,
			    		fieldStyle:'line-height:18px;',
			    		id:'conTextareaId',
			    		value:getDecodeStr(conditions),
			    		enableKeyEvents:true,
			    		listeners:{
			    			'render':function(){
//				    				this.setValue(getDecodeStr(conditions));
			    			},
			        		afterrender:function(textarea){
			        			if(getBrowseVersion()&& getBrowseVersion()!=10){//ie 下绑定mouseleave事件
			        				textarea.getEl().on("mouseleave",function(){
			        					//me.getCursorPosition();//获得光标位置
			        				})
			        			}
			        		},
		        			change:function(){
			        			if(getBrowseVersion()&& getBrowseVersion()!=10){
			        				me.getCursorPosition();//获得光标位置
			        			}
		        			},
		        			keyup:function(textarea,e){
			        			if(getBrowseVersion()&& getBrowseVersion()!=10){
			        				me.getCursorPosition();//获得光标位置     lis update2016-7-5
			        			}
		        			},
			        		 click: {
			                    element: 'el',
			                    fn: function(){ 
				        			if(getBrowseVersion()&& getBrowseVersion()!=10){
				        				//me.getCursorPosition(); //获得光标位置
				        			}
			        			}
			                }
			    		}
			    	}],
			    	//函数向导、条件保存 按钮
			    	buttons:[{xtype:'tbfill'},{
			    		text:common.button.functionGuide,//'函数向导'
			    		handler:function(){
							Ext.require('EHR.functionWizard.FunctionWizard',function(){
								Ext.create("EHR.functionWizard.FunctionWizard",{opt:'0',callbackfunc:'me.condit'});
							});
			    	}
			    		
			    	},{
			    		text:me.saveText,
			    		style:'margin-right:21px',
			    		listeners:{
			    			'click':function(){
			    				conditions = Ext.getCmp('conTextareaId').getValue();
			    				var map = new HashMap();
			    				map.put("method","check");
			    				map.put("opt",opt);
								map.put("c_expr",getEncodeStr(conditions));
								Rpc({functionId:'SYS00000003',async:false,success:me.resultCheckExpr},map);
			    			}
			    		}
			    	}]
			    },{
			    	xtype:'panel',
			    	layout:'table',
			    	height:220,
			    	width: 580,
			    	border:false,
			    	style:'margin-left:20px',
			    	items:[{
			    		xtype:'fieldset',
			    		title:common.label.referenceItems,//参考项目
			    		height:140,
			    		width:257,
			    		items:[{
			    			xtype:'combo',
			    			fieldLabel:common.label.items,//项目
				    		labelAlign:'left',
							labelWidth:28,
							width:230,
							style:'margin-top:10px'  ,
							id:'conComboItemId',
							store:store,
					        displayField:'dataName', 
				 	    	valueField:'dataValue',
				 	    	minChars : 1, 
				 	    	labelSeparator:null,
				 	    	forceSelection : true ,
				 	    	editable:false,
				 	    	queryParam : 'singer.singerName',
				 	    	triggerAction : 'all',
				 	    	listeners:{
				 	    		'select':function(com){
		    					   var arr = com.getValue().split("#!#");
				 	    		   me.symbol("conTextareaId",arr[1]);
				 	    		   var  codesetid = arr[2];
				 	    		   if(codesetid != '' && codesetid != null && codesetid!='0'){
				 	    			   var codeCom = Ext.getCmp('codesetid');
				 	    			   if(codeCom){//若存在 代码类下拉框则重新复制 若不存在 则新建
				 	    				  codeCom.codesetid = codesetid;
					 	    			   codeCom.setRawValue("");
					 	    			   codeCom.getPicker().getStore().proxy.extraParams.codesetid = codesetid;
					 	    			   codeCom.getPicker().getStore().load();//xuj update 重新显示代码项恢复默认展开状态 2015-1-21
				 	    			   }else{
				 	    				  codeCom=Ext.create('EHR.extWidget.field.CodeTreeCombox',{
				 	    					  	border : false,
												id:'codesetid',
												editable:false,
												codesetid :codesetid,
												maxPickerWidth:196,
												width : 197,
												listeners:{
									 	    		'select':function(pick,value){
														me.symbol("conTextareaId","\""+pick.getValue().split('`')[0]+"\"");
													}
									 	    	}
				 	    				  });
				 	    				 Ext.getCmp('codeId').add(codeCom);
				 	    			   }
				 	    			  Ext.getCmp('codeId').show();
				 	    			   
				 	    			   
				 	    		   }else{
				 	    			   Ext.getCmp('codeId').hide();
				 	    		   }
			    				}
				 	    	}
			    		},{
							//代码
							xtype : 'panel',
							style:'margin-top:20px'  ,
							id:'codeId',
							border : false,
							hidden:true,
							layout : 'table',
							items : [{
										xtype : 'panel',
										style : 'width:33px',
										border : false,
										html : common.label.code//代码
									}]
						}]
			    	},{
				    	xtype:'fieldset',
			    		title:common.label.operationaSymbol,//运算符号
			    		height:140,
			    		width:290,
			    		layout:'form', 
			    		style:'margin-left:5px;algin:center',
			    		items:[
			    			{
		        				xtype:'container',
								width:270,
		        				items:[
									{xtype:'button',text:'0',style:style,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}} },
									{xtype:'button',text:'1',style:style,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'2',style:style,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'3',style:style,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'4',style:style,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'+',style:style,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'*',style:style,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'\\',style:style,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'且',style:style,width:width2,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'非',style:style,width:width2,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
								]
							},
						   {
							   xtype:'container',
							   width:270,
							   items:[
									{xtype:'button',text:'5',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'6',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'7',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'8',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'9',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'-',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'/',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'%',style:style1,width:width2,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'或',style:style1,width:width2,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'~',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
                       			]
							},
						   {
								xtype:'container',
								width:270,
							    items:[
									{xtype:'button',text:'(',style:style1,width:19,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:')',style:style1,width:19,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'=',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'>',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'<',style:style1,width:width,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'<>',style:style1,width:32,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'<=',style:style1,width:32,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'>=',style:style1,width:32,listeners:{"click":function(){  me.symbol("conTextareaId",this.text);}}},
									{xtype:'button',text:'包含',style:style1,width:38,listeners:{"click":function(){  me.symbol("conTextareaId",' Like ');}}}
								]
						   }	
			    		  ]
						}]
			    }]
	 	});
		
	 	conWin.show();
	 },
		 
	//将所选指标、代码填充到文本框
	symbol:function(exprId, strexpr) {
		var rulearea = Ext.getCmp(exprId);
		var myField = rulearea.inputEl.dom;
		var startPos = 0;//光标选中内容起始位置
		var endPos = 0;//光标选中内容结束位置
		var selectionIndex = 0;//光标位置
		//IE support
		if (getBrowseVersion()&& getBrowseVersion()!=10) {
			var sel = null;
			startPos = me.selectionStart;
			endPos = me.selectionEnd;
			selectionIndex = me.selectionIndex;
			myField.focus();
			//写入选中内容
			//rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
			if(startPos==0 && endPos==0){
				rulearea.setValue(myField.value + strexpr);
				var index = selectionIndex + (myField.value + strexpr).length;
				var range = myField.createTextRange();
				range.move("character", index);//移动光标
				range.select();//选中
				me.selectionIndex = index;
				me.selectionStart = startPos + (myField.value + strexpr).length;
				me.selectionEnd = endPos + (myField.value + strexpr).length;
			}else{
				rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
				var index = selectionIndex + strexpr.length;
				var range = myField.createTextRange();
				range.move("character", index);//移动光标
				range.select();//选中
				me.selectionIndex = index;
				me.selectionStart = startPos + strexpr.length;
				me.selectionEnd = endPos + strexpr.length;
			}
			
		}
		//MOZILLA/NETSCAPE support 
		else if (myField.selectionStart || myField.selectionStart == '0') {
			startPos = myField.selectionStart;
			endPos = myField.selectionEnd;
			// 保存scrollTop，为了换行
			var restoreTop = myField.scrollTop;
			//写入选中内容
			if(startPos==0 && endPos==0){
				rulearea.setValue(myField.value + strexpr);
				if (restoreTop > 0) {//换行
					myField.scrollTop = restoreTop;
				}
				myField.focus();
				myField.selectionStart = (myField.value + strexpr).length;
				myField.selectionEnd = (myField.value + strexpr).length;
			}else{
				rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
				if (restoreTop > 0) {//换行
					myField.scrollTop = restoreTop;
				}
				myField.focus();
				myField.selectionStart = startPos + strexpr.length;
				myField.selectionEnd = startPos + strexpr.length;
			}
			//rulearea.setValue(myField.value.substring(0, startPos) + strexpr + myField.value.substring(endPos, myField.value.length));
			
		}else {
			myField.value += strexpr;
	    }
	} ,
	
	//获得光标位置
	getCursorPosition:function () { 
		var rulearea = Ext.getCmp('conTextareaId');
   		var el = rulearea.inputEl.dom;//得到当前textarea对象
   		if(getBrowseVersion()&& getBrowseVersion()!=10){
   		    el.focus(); 
   		    var r = document.selection.createRange(); //返回当前网页中的选中内容的TextRange对象
   		    if (r == null) { 
   		    	me.selectionStart = 0; 
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
	        me.selectionIndex = text.length; //光标位置
   		    me.selectionStart = rc.text.length; 
   		    me.selectionEnd = me.selectionStart + re.text.length;
   		  } 
	},
	
	//保存条件表达式校验成功以后
	resultCheckExpr:function(response){
		var value = response.responseText;
		var map = Ext.decode(value);
		var info = map.info;
		var opt = map.opt;
		if(info=="ok"){
			if(afterfunc)
				Ext.callback(eval(afterfunc),null,[getEncodeStr(conditions),opt,method]);
			conWin.close();
		}else{
			Ext.showAlert(getDecodeStr(info));
		}
	},
	
	getCodeitem:function(value,text){
		me.symbol("conTextareaId","\""+value+"\"");
	},
	
	condit:function(strexpr){
		Ext.getCmp("conTextareaId").focus();
		if(getBrowseVersion()&& getBrowseVersion()!=10){
			var element = document.selection;
			if (element!=null) {
				var rge = element.createRange();
				if (rge!=null)	
				rge.text=strexpr;
			}
		}else{
		    var rulearea = Ext.getCmp("conTextareaId");
	        var rulevalue = rulearea.getValue();
	        var start = rulearea.inputEl.dom.selectionStart;
	        var end = rulearea.inputEl.dom.selectionEnd;
	        var oriValue = rulearea.getValue().toString();  
	        rulearea.setValue(oriValue.substring(0,start) + strexpr + oriValue.substring(end)); 
	    }
	}
		
		
 });
