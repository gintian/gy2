 Ext.define('TemplateBatchUL.UpdateSingleFieldItem',{
     constructor:function(config){//构造方法
 		thisSingle=this;
 		thisSingle.cond='';
 		this.batchtempl=config.batchtempl;
    	thisSingle.tab_id=this.batchtempl.tab_id;
    	thisSingle.ins_id=this.batchtempl.ins_id;
    	thisSingle.infor_type=this.batchtempl.infor_type;
    	thisSingle.task_id=this.batchtempl.task_id;
		thisSingle.view_type=this.batchtempl.view_type;
		thisSingle.imppeople='';//是否启用选人组件
    	var map = new HashMap();
    	map.put("tab_id",thisSingle.tab_id);		
		map.put("ins_id",thisSingle.ins_id);		
		map.put("infor_type",thisSingle.infor_type);
		map.put("task_id", thisSingle.task_id);
		map.put("transType", "init");
		map.put("allNum", config.allNum);
    	Rpc({functionId:'MB00002004',async:false,success:thisSingle.init},map);//初始化指标
     },
	 init:function(form,action){
    	var result = Ext.decode(form.responseText);
    	if(!result.succeed){
			var message = result.message;
			if(message&&message.indexOf("拆分审批")!=-1){
				templateTool_me.checkSpllit(message);
			}
		}
    	var targitemlist = result.targitemlist;//变化后指标
    	var ref_itemlist = result.ref_itemlist;//参考项目
    	thisSingle.lastpropertylist  = result.lastpropertylist;
		var width = 1400;
	    var height = 650;
	    var win = Ext.create('Ext.window.Window',{
	  	    title:'批量修改单个指标',
	 		id:'batchUpdateWinId',
	 		width: 430,  
	        height: height*0.7,  
	        resizable: false,  
	        modal: true,
	        border:false,
	       	items:[{
	       		xtype:'panel',
              	layout:'hbox',  
              	border:false,
              	style:'margin-left:10px;margin-top:3px',
	       		items:[{
	       			xtype:'panel',
			       	layout:'column',
			       	width:340,
			       	border:false,
			       	items:[
			        {
			      	   xtype:'combo',
			           fieldLabel:'项目',
			           labelSeparator:null,
			           id:'itemId',
			           labelAlign:'left',
			           labelWidth:60,
			           width:340,
			           typeAhead:true,
			           store:new Ext.data.SimpleStore({
                			 fields:['value', 'text'],
               				 data:targitemlist
           			   }),
			           valueField:'value', 
		 	    	   displayField:'text',
		 	    	   minChars : 1, 
		 	    	   forceSelection : true ,
		 	    	   queryParam : 'singer.singerName',
		 	    	   triggerAction : 'all',
		 	    	   listeners:{
		 	    	   		'select':function(){
	 	    	   				var itempanel =  Ext.getCmp('itemId');
	 	    	   				var codeItemId= this.getValue();
	 	    	   				for(var i=0;i<thisSingle.lastpropertylist.length;i++){
	 	    	   					var fieldmap = thisSingle.lastpropertylist[i];
	 	    	   					var fieldname = fieldmap.fieldname;
	 	    	   					if(codeItemId==fieldname){
		 	    	   					var type = fieldmap.type;
		 	    	   					var codesetid = fieldmap.codesetid;
		 	    	   					var limit_manage_priv = fieldmap.limit_manage_priv;
		 	    	   					var nmodule = fieldmap.nmodule;
		 	    	   					var codesetValid = fieldmap.codesetValid;
										var codesource = fieldmap.codesource;
										thisSingle.imppeople=fieldmap.imppeople;//获取是否启用选人组价
		 	    	   					if(type=='A' && codesetid && codesetid.length>0 && codesetid!='0'){
			 	    	   					var editor = Ext.widget('codecomboxfield',{
				 	    	               		itemId:'valueBox',
				 	    	               		margin:'0 0 0 0',
				 	    	               		width:275,
				 	    	                    codesetid:codesetid,
				 	    	                    nmodule:nmodule==undefined?"0":nmodule,
				 	    	                    ctrltype:limit_manage_priv==undefined?"0":limit_manage_priv,
				 	    	                    codesource:codesource==undefined?"":codesource,
				 	    	                    onlySelectCodeset:codesetValid==undefined?true:codesetValid,
				 	    	                    afterCodeSelectFn:function(a,value){
				 	    	                    	var text  =  '"'+value+'"';
								 	    	   		thisSingle.symbol('updateTextId',text);
				 	    	                    }
			 	    	   					});
			 	    	   					itempanel.ownerCt.child('#codeItemId').removeAll(true);
			 	    	   					itempanel.ownerCt.child('#codeItemId').add([{xtype:'label',text:'系统代码',margin:'2 5 2 0',width:60},editor]);
			 	    	   					itempanel.ownerCt.child('#codeItemId').show();
		 	    	   					}else{
		 	    	   						itempanel.ownerCt.child('#codeItemId').hide();
		 	    	   					}
		 	    	   					break;
	 	    	   					}
	 	    	   				}
		 	    	   		}
		 	    	   }
			        },{
			        	xtype:'textarea',
			        	fieldLabel:'替换成',
			        	id:'updateTextId',
			        	style:'margin-top:10px',
			            labelAlign:'left',
			            labelSeparator:null,
			            labelWidth:60,
			            width:340,
						height:height*0.3,
						listeners:{
							'focus':function( obj, event, eOpts){
								if(thisSingle.imppeople){//启用选人组件弹出选人
									if(templateMain_me.templPropety.isValidOnlyname==undefined||templateMain_me.templPropety.isValidOnlyname=='false'){
										Ext.showAlert("请设置并且启用唯一性指标！");
										return false;
									}
								   var	 defaultSelectedPerson=new Array();
									if(obj.value!=undefined&&obj.value!=''){
										var hashvo = new HashMap();
										   hashvo.put("ids",obj.value.substring(1,obj.value.length-1));
										   hashvo.put("tabid",templateMain_me.templPropety.tab_id);
										Rpc( {functionId : 'MB00002030',async:false,success:function(form,action){//
											   var result = Ext.decode(form.responseText);	
											   if(!result.resultValue.succeed){
												   Ext.showAlert(result.resultValue.Msg);
												   return;
											   }else{
												   defaultSelectedPerson=result.resultValue.value;
											   }
											}}, hashvo);
									}
									var temIsPrivExpression="";
									var isPrivExpression=false;
									var filter_factor="";
									var orgId="";
									/*if(templateMain_me.templPropety.orgId)//bug 43518 启用选人组件不应控制范围
										orgId=templateMain_me.templPropety.orgId;
									else
										orgId="";
									if(templateMain_me.templPropety.filter_by_factor==1)
									{
										temIsPrivExpression=templateMain_me.templPropety.isPrivExpression;
										filter_factor=templateMain_me.templPropety.filter_factor;
									}
									if(temIsPrivExpression!=null&&typeof(temIsPrivExpression)!='underfined'&&!temIsPrivExpression)////是否启用人员范围
									{
										isPrivExpression=temIsPrivExpression;
										orgId='';
									}*/
									var f = document.getElementById("getHandTemp");
									var p = new PersonPicker({
										addunit:false, //是否可以添加单位
										adddepartment:false, //是否可以添加部门
										multiple: true,//为true可以多选
										orgid:orgId,
										isPrivExpression:isPrivExpression,//是否启用人员范围（含高级条件）
										extend_str:"template/"+templateMain_me.templPropety.tab_id,
										validateSsLOGIN:false,//是否启用认证库
										selectByNbase:true,//是否按不同人员库显示
										deprecate :'',//不显示的人员
										defaultSelected:defaultSelectedPerson,
										nbases:templateMain_me.templPropety.nbases,
										text: "确定",
										callback: function (c) {
											var staffids = "";
											var errerMsg="";
											for (var i = 0; i < c.length; i++) {
												if(c[i].onlyName==undefined||c[i].onlyName==''){
													if(errerMsg.length>0){
														errerMsg+="、";
													}
													errerMsg+=c[i].name;
												}else{
													staffids += c[i].name + ":"+c[i].onlyName+"、";
												}
											}
											obj.value=staffids.substring(0,staffids.length-1);
											thisSingle.symbolEmpValue("updateTextId","'"+staffids.substring(0,staffids.length-1)+"'");	
											if(errerMsg.length>0){
												Ext.showAlert(errerMsg+"的唯一性指标值为空，不能保存。");
											}
										}
									}, f);
									p.open();
									return false;
								}
							}
						}
			        },{
			        	xtype:'combo',
			        	fieldLabel:'参考项目',
			        	id:'referItemId',
			        	name:'referItemName',
			        	labelSeparator:null,
			        	style:'margin-top:10px',
			            labelAlign:'left',
			            labelWidth:60,
			            typeAhead:true,
			            width:340,
			            store:new Ext.data.SimpleStore({
		                    fields:['value', 'text'],
		                    data:ref_itemlist
		                }),
			            valueField:'value',
			            displayField:'text',
		 	    	    minChars : 1, 
		 	    	    forceSelection : true ,
		 	    	    queryParam : 'singer.singerName',
		 	    	    triggerAction : 'all',
		 	    	    listeners:{
		 	    	    	'select':function(){
			 	    	   		var text  =  this.getDisplayValue();
			 	    	   		thisSingle.symbol('updateTextId',text);
		 	    	   	}
		 	    	   }
			       },{
			        	xtype:'panel',
			        	itemId:'codeItemId',
			        	style:'margin-top:10px',
			        	layout:'hbox',
			            hidden:true,
			            border:0,
			            width:340
		            },{ 
		            	id   : 'selection', 
		            	fieldLabel:'修改记录',
		            	labelWidth:60,
		            	labelSeparator:null,
		            	labelAlign:'left',
		            	xtype: 'radiogroup', 
		            	items : [{   
		            		 labelAlign:'left',
			            	 boxLabel: '全部记录 ',  
			            	 width:80,
			            	 name: 'standAnswer',  
			            	 inputValue:'0',  
			            	 checked : true  
		            	  },{   
		            		 labelAlign:'left',
		            		 width:80,
			            	 boxLabel: '选中记录',  
			            	 name: 'standAnswer',  
			            	 inputValue:'1'  
		            	  }]  

		            },{
			       		xtype:'panel',
			       		border:false,
			       		style:'margin-left:30;margin-top:10px',
			       		html:'说明：替换框内直接输入表达式。<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如：可将“月奖金” 替换成 “基本工资*0.1”'
			       }]},{
		        	xtype:'panel',
		        	width:width*0.07,
		        	layout:'column',
		        	columnWidth:1,
		        	buttonAlign:'left',
		        	border:false,
		        	items:[{
		        		xtype:'button',
		        		text:'条件',
						style:'margin-left:20px;margin-top:0px',
						hidden:!thisSingle.imppeople=='true',
		        		listeners:{
		        			'click':function(){
		        				//调用定义条件插件。
		        				Ext.Loader.setPath("DECN",rootPath+"/components/definecondition");
		        				Ext.require('DECN.DefineCondition',function(){
									Ext.create("DECN.DefineCondition",{primarykey:thisSingle.tab_id,opt:"2",checktemp:'temp',mode:'',imodule:'2',conditions:thisSingle.cond,afterfunc:'thisSingle.condiTions'});
								});
		        			}
		        		}
		        	},{
		        		xtype:'button',
		        		text:'向导',
		        		style:'margin-left:20px;margin-top:10px',
		        		hidden:!thisSingle.imppeople=='true',
		        		listeners:{
		        			'click':function(){
		        					Ext.Loader.setPath("EHR.functionWizard","/components/functionWizard");
					    			Ext.require('EHR.functionWizard.FunctionWizard',function(){
					    				Ext.create("EHR.functionWizard.FunctionWizard",{keyid:thisSingle.tab_id,opt:"2",mode:'',callbackfunc:'thisSingle.guide'});
					    			});
		        			}
		        		}
		        		
		        	}]
		        }]
	       	}] ,
	        bbar:[{xtype:'tbfill'},{
	        	text:'确定',
	        	margin:'0 15 0 0',
	        	listeners:{
	        		'click':function(){ 
    					var select = Ext.getCmp('selection'); 
	          			var _itemId = Ext.getCmp('itemId'); 
	          			var m = Ext.getCmp('updateTextId'); 
	          			var selected=select.lastValue.standAnswer;
	          			var itemid=_itemId.getValue();  
	          			var formula=m.getValue();  
	          			if(!!!itemid||!!!formula){
	          				thisSingle.resultvalue();
	          				//win.close();
	          			}else{
		          			var map = new HashMap();
		          			map.put("tab_id",thisSingle.tab_id);		
		        			map.put("ins_id",thisSingle.ins_id);		
		        			map.put("infor_type",thisSingle.infor_type);
		        			map.put("task_id", thisSingle.task_id); 
		        			map.put("selected", selected);
		        			map.put("itemid", itemid);
		        			map.put("formula", formula); 
		        			map.put("cond", thisSingle.cond);
		        			map.put("transType", "ok"); 
		        			Rpc({functionId:'MB00002004',async:false,success:function(form){
		        					var result = Ext.decode(form.responseText);
		        					if(result.succeed){
	          		    					templateTool_me.refreshCurrent();
	          		    					win.close();
		        							Ext.showAlert("修改成功！");
		        					}else{
		        							Ext.showAlert(result.message);
		        					}
		        			}},map);//单指标修改
	          			}
	        		}
	        	}
	        },{
	        	text:'关闭',
	        	listeners:{
	        		'click':function(){
	        			win.close();
	        		}
	        	}
	        },{xtype:'tbfill'}]
		});
		win.show();
	 },
	 //获取运算符号值，并写入到文本域中
	 symbol:function(editor,strexpr){
		Ext.getCmp(editor).focus();
		if(document.selection){
			var element = document.selection;
			if (element!=null) {
				var rge = element.createRange();
				if (rge!=null)	
				rge.text=strexpr;
			}
		}else{
		    var rulearea = Ext.getCmp(editor);
	        var rulevalue = rulearea.getValue();
	        var start = rulearea.inputEl.dom.selectionStart;
	        var end = rulearea.inputEl.dom.selectionEnd;
	        var oriValue = rulearea.getValue().toString();  
	        rulearea.setValue(oriValue.substring(0,start) + strexpr + oriValue.substring(end)); 
	    }
	 },
	 symbolEmpValue:function(editor,strexpr){
		Ext.getCmp(editor).focus();
		if(document.selection){
			var rulearea = Ext.getCmp(editor);
	        rulearea.setValue(strexpr); 
		}else{
		    var rulearea = Ext.getCmp(editor);
	        rulearea.setValue(strexpr); 
	    }
	 },
	 //填写提示
	 resultvalue:function(){
		 Ext.showAlert("指标或修改值不能为空！");
	 },
	 //保存选择的条件
	 condiTions:function(obj){
		 thisSingle.cond = obj;
	 },
	 //把向导值显示在文本域中
	 guide:function(obj){
		 thisSingle.symbol("updateTextId",obj);
	 }
 });
