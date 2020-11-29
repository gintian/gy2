/****
 * 登记表工具栏菜单
 * 
 */
Ext.define('Card.CardToolBar',{
	extend:'Ext.toolbar.Toolbar',
	requires:['Card.CardTree'],
	border:false,
	width:'100%',
	height:40,
	dblist:undefined,
	inforkind:undefined,
	usedday:undefined,
	version:undefined,
	toolBarPanel:undefined,
	toolBarScope:undefined,
	initComponent:function(){
		this.callParent();
		this.createToolBar();
		
	},createToolBar:function(){
		var cardToolbar_me=this;
		var currentPdf=common.button.cardOneFile.replace("{0}","PDF").replace("{1}",(this.inforkind=='2')?common.button.cardOrg:common.button.cardPerson);//当前人员生成pdf
		var allPdf=common.button.cardAllFiles.replace("{0}","PDF").replace("{1}",(this.inforkind=='2')?common.button.cardOrg:common.button.cardPerson);//全部人员生成PDF
		var partPersonPdf=common.button.cardFiles.replace("{0}","PDF").replace("{1}",(this.inforkind=='2')?common.button.cardOrg:common.button.cardPerson);//部分人员生成PDF
		var onePersonOneDoc=common.button.cardDocument.replace("{0}",(this.inforkind=='2')?common.button.cardOneOrgFile:common.button.cardPersonFile);//一人一文档
		var allPersonDoc=common.button.cardDocument.replace("{0}",(this.inforkind=='2')?common.button.cardAllOrgFile:common.button.cardAllPersonFile);;//多人一文档
		if(this.inforkind=='4'||this.inforkind=='6'){
			currentPdf=common.button.cardOneFile.replace("{0}","PDF").replace("{1}",common.button.cardJob);
			allPdf=common.button.cardAllFiles.replace("{0}","PDF").replace("{1}",common.button.cardJob);
			partPersonPdf=common.button.cardFiles.replace("{0}","PDF").replace("{1}",common.button.cardJob);
			onePersonOneDoc=common.button.cardDocument.replace("{0}",common.button.cardOneJobFile);
			allPersonDoc=common.button.cardDocument.replace("{0}",common.button.cardAllJobFile);
		}
			
		var currentWord=currentPdf.replace("PDF","WORD");//当前人员生成Word
		var allWord=allPdf.replace("PDF","WORD");//全部人员生成Word
		var partPersonWord=partPersonPdf.replace("PDF","WORD");//部分人员生成Word
		
		//创建css 设置图标的大小
		Ext.util.CSS.createStyleSheet(".btn-Img-icon{background-repeat:no-repeat; background-size:100% 100%;-moz-background-size:100% 100%;}");
		Ext.util.CSS.createStyleSheet(".x-btn-wrap-default-toolbar-small.x-btn-arrow-right:after{background:url(/components/querybox/images/down_sign.png)!important;width:16px !important}");
		//.x-btn-wrap-default-toolbar-small.x-btn-arrow-right:after{}
		var viewHidden=false;
		var pdfHidden=false;
		var wordHidden=false;
		if(this.inforkind=='1'||this.inforkind=='2'||this.inforkind=='4'||this.inforkind=='6'){
			pdfHidden=cardGlobalBeanDefault.btnFunction.pdfBtn=='true'?false:true;
			viewHidden=cardGlobalBeanDefault.btnFunction.printView=='true'?false:true;
			wordHidden=cardGlobalBeanDefault.btnFunction.wordBtn=='true'?false:true;
		}
		if(this.inforkind!='7'&&this.inforkind!='10'&&(cardGlobalBeanDefault.cardFlag=='')){
			cardGlobalBeanDefault.queryflag=0;//普通导出只有条件查询
			//打印预览
			var printViewBtn=Ext.create('Ext.Button',{
						     text:common.button.cardPrintView,
						     width:100,
						     hidden:viewHidden,
						     icon:'/images/print.gif',
						     listeners:{
						        	'click':{
						        			Element:'el',
						        			fn:function(){
						        				cardToolbar_me.toolBarScope.showPrintCard();
						        			}
						        	}
						        }
					});
			var menusPdf=Ext.create('Ext.menu.Menu',{
				   width: 150,
				   plain: true,
				   floating: true,
				   items:[
					   {
						   text:currentPdf,
						   menu:[
							   {
								   text:'兼容Office',
								   handler: function() {
									   cardToolbar_me.toolBarScope.excecuteWord("false","pdf",{},'',0);
								   }
							   },
							   {
								   text:'兼容WPS',
								   handler: function() {
									   cardToolbar_me.toolBarScope.excecuteWord("false","pdf",{},'',1);
								   }
							   }
						   ]/*,
						   handler: function() {
							   cardToolbar_me.toolBarScope.excecuteWord("false","pdf");
						   }*/
					   },
					   {
						   text:allPdf,
						   menu:[
							   {
								text:onePersonOneDoc,//inforkind=='2'||inforkind=='4'||inforkind=='6'  一机构一文档
								menu:[
									{
										 text:'兼容Office',
										 handler: function() {
											   cardToolbar_me.toolBarScope.excecuteWord("all","pdf",{},'1',0);
										 }	
									},
									{
										text:'兼容WPS',
										handler: function() {
											   cardToolbar_me.toolBarScope.excecuteWord("all","pdf",{},'1',1);
										}
									}
								]/*,
								handler: function() {
									searchCard_me.excecuteWord("all","pdf",{},'1');
								} */  
							   },
							   {
									text:allPersonDoc,
									menu:[
										{
											text:'兼容Office',
											 handler: function() {
												   cardToolbar_me.toolBarScope.excecuteWord("all","pdf",{},'all',0);
											 }	
										},
										{
											text:'兼容WPS',
											handler: function() {
												   cardToolbar_me.toolBarScope.excecuteWord("all","pdf",{},'all',1);
											}
										}
									]/*,
									handler: function() {
										searchCard_me.excecuteWord("all","pdf",{},'all');
										   }   */
									   
								   	}
							    ]
					   },
					   {
						   text:partPersonPdf,
						   menu:[
							   {
								   text:onePersonOneDoc,
								   menu:[
									   {
											text:'兼容Office',
											 handler: function() {
												   cardToolbar_me.toolBarScope.excecuteWord("1","pdf",{},'1',0);
											 }	
										},
									   {
											text:'兼容WPS',
											handler: function() {
												   cardToolbar_me.toolBarScope.excecuteWord("1","pdf",{},'1',1);
											}
										}
								   ]/*,
								   handler: function() {
									searchCard_me.excecuteWord("1","pdf",{},'1');
								   } */  
							   },
							   {
								   text:allPersonDoc,
								   menu:[
									   {
											text:'兼容Office',
											 handler: function() {
												   cardToolbar_me.toolBarScope.excecuteWord("1","pdf",{},'all',0);
											 }	
										},
									   {
											text:'兼容WPS',
											handler: function() {
												   cardToolbar_me.toolBarScope.excecuteWord("1","pdf",{},'all',1);
											}
										}
								   ]/*,
								   handler: function() {
									searchCard_me.excecuteWord("1","pdf",{},'all');
								   } */  
							   }
						        ]
					   }
					   ]
		});
			
			var menusWord=Ext.create('Ext.menu.Menu',{
							width: 150,
						    plain: true,
						    floating: true,
						    items:[
							   {
								   text:currentWord,
								   menu:[
									   {
											text:'兼容Office',
											 handler: function() {
												   cardToolbar_me.toolBarScope.excecuteWord("false","word",{},'',0);
											 }	
										},
									   {
											text:'兼容WPS',
											handler: function() {
												   cardToolbar_me.toolBarScope.excecuteWord("false","word",{},'',1);
											}
										}
								   ]/*,
								   handler:function(){
									   cardToolbar_me.toolBarScope.excecuteWord("false","word");
								   }*/
							   },
							   {
								   text:allWord,
								   menu:[
									   {
										text:onePersonOneDoc,
										menu:[
											{
												text:'兼容Office',
												 handler: function() {
													   cardToolbar_me.toolBarScope.excecuteWord("all","word",{},'1',0);
												 }	
											},
											{
												text:'兼容WPS',
												handler: function() {
													   cardToolbar_me.toolBarScope.excecuteWord("all","word",{},'1',1);
												}
											}
										]/*,
										handler: function() {
											searchCard_me.excecuteWord("all","word",{},'1');
										   } */  
									   },
									   {
										text:allPersonDoc,
										menu:[
											{
												text:'兼容Office',
												 handler: function() {
													   cardToolbar_me.toolBarScope.excecuteWord("all","word",{},'all',0);
												 }	
											},
											{
												text:'兼容WPS',
												handler: function() {
													   cardToolbar_me.toolBarScope.excecuteWord("all","word",{},'all',1);
												}
											}
										]/*,
										handler: function() {
											searchCard_me.excecuteWord("all","word",{},'all');
											   }   */
										   
									   }
									    ]
							   },
							   {
								   text:partPersonWord,
								   menu:[
										   {
											text:onePersonOneDoc,
											menu:[
												{
													text:'兼容Office',
													 handler: function() {
														   cardToolbar_me.toolBarScope.excecuteWord("1","word",{},'1',0);
													 }	
												},
												{
													text:'兼容WPS',
													handler: function() {
														   cardToolbar_me.toolBarScope.excecuteWord("1","word",{},'1',1);
													}
												}
											]/*,
											handler: function() {
												searchCard_me.excecuteWord("1","word",{},'1');
											   }  */ 
										   },
										   {
											text:allPersonDoc,
											menu:[
												{
													text:'兼容Office',
													 handler: function() {
														   cardToolbar_me.toolBarScope.excecuteWord("1","word",{},'all',0);
													 }	
												},
												{
													text:'兼容WPS',
													handler: function() {
														   cardToolbar_me.toolBarScope.excecuteWord("1","word",{},'all',1);
													}
												}
											],
											handler: function() {
												searchCard_me.excecuteWord("1","word",{},'all');
												   }   
										    }
									    ]
							   }
							   ]
				});
			
			var menuPdf=Ext.create('Ext.Button',{
									width:100,
									height:23,
									text:common.button.toexport+'PDF',
									hidden:pdfHidden,
									icon:'/images/outpdf.png',
									iconCls:'btn-Img-icon',
									menu:menusPdf
									
									});
			var menuWord=Ext.create('Ext.Button',{
									width:120,
									height:23,
									text:common.button.toexport+'WORD',
									icon:'/images/outword.png',
									iconCls:'btn-Img-icon',
									hidden:wordHidden,
									menu:menusWord
									});
			this.add(this.getTreePanel());
			this.add(' ');
			if(Ext.isIE&&this.inforkind!='5'){//非IE隐藏打印预演，绩效隐藏打印预演
				if(this.version){
					if(!viewHidden){
						this.add(printViewBtn);
						this.add(' ');
					}
				}
			}
			if(!pdfHidden){
				this.add(menuPdf);
				this.add(' ');
			}
			if(!wordHidden){
				this.add(menuWord);
				this.add(' ');
			}
			
			if(this.inforkind!='5'&&cardGlobalBeanDefault.a0100==''){//绩效评估无需查询框
				this.add(this.creaSearchPanel());
				var searchBtn=Ext.create('Ext.Button',{
					width:50,
					height:23,
					text:common.button.search,
					listeners:{
						click: {
				            element: 'el', 
				            fn: function(){
				            	searchCard_me.search();
				            	}
				        }
					}
				});
				this.add(searchBtn);
				
			}
			
		}else if(this.inforkind=='10'){
			var map=new HashMap();
			map.put("flag","myCardScore");
			map.put("a0100",cardGlobalBeanDefault.a0100);//cardGlobalBeanDefault.a0100);
			Rpc({functionId:'CARD0000001',success:function(res){
		    	   var rs=Ext.decode(res.responseText);
		    	   this.createSalaryCard(rs);
		       		},scope:this},map);
		}else if(this.inforkind=='7'){
			var map=new HashMap();
			map.put("flag","salary");
			map.put("a0100",cardGlobalBeanDefault.a0100);//cardGlobalBeanDefault.a0100);
			Rpc({functionId:'CARD0000001',success:function(res){
		    	   var rs=Ext.decode(res.responseText);
		    	   if(rs.salaryMap.queryflag==0){//条件查询
		    		   this.creaConditionCard();
		    	   }else{//日期查询
		    		   this.createSalaryCard(rs);
		    	   }
		    	   
		       		},scope:this},map);
		}else if(cardGlobalBeanDefault.cardFlag){
			 this.creaConditionCard();
		}
	
	},getToolBar:function(){//加载工具栏
		if(cardGlobalBeanDefault.Callbackfunc){//关闭或返回回调事件
			this.add(' ');
			if(cardGlobalBeanDefault.Callbackfunc=='window.close')//针对弹出框展现登记表
				this.add({xtype:'button',width:50,text:common.button.close,handler:function(){top.close()}});
			else if(cardGlobalBeanDefault.Callbackfunc=="dxt"){
				this.add({xtype:'button',
						  width:50,
						  text:common.button.close,
						  listeners:{
							  click:function(){
								  	if(cardGlobalBeanDefault.inforkind=='1'){
								  		self.location.href="/general/tipwizard/tipwizard.do?br_employee=link";
									}else if(cardGlobalBeanDefault.inforkind=='4'){
										self.location.href="/general/tipwizard/tipwizard.do?br_orginfo=link";
									}
								  }
						  	  }
							});
			}else if(cardGlobalBeanDefault.Callbackfunc=='home5'){
				this.add({xtype:'button',
					  width:50,
					  text:common.button.close,
					  listeners:{
						  click:function(){
							  	if(cardGlobalBeanDefault.hcmFlag=='hcm'){
							  		self.location.href="/templates/index/hcm_portal.do?b_query=link";
								}else{
									self.location.href="/templates/index/portal.do?b_query=link";
								}
							  }
					  	  }
						});
			}else	
				this.add({xtype:'button',width:50,text:common.button.close,handler:function(){Ext.callback(cardGlobalBeanDefault.Callbackfunc);}});
		}
		return this;
	},getdbPanel:function(){//为人员时 加载人员库下拉选
		var dbStore=Ext.create("Ext.data.Store",{
    		fields:['id','dbname'],
    		data:this.dblist
    		});
    	
    	var dnComBox=Ext.create('Ext.form.ComboBox', {
		    fieldLabel:common.label.cardDbName,
		    labelWidth:50,
		    width:'100%',
		    id:'dbBox',
		    store: dbStore,
		    border:0,
		    style:'margin:2px 0 2px 0',
		    queryMode: 'local',
		    editable:false,
		    displayField: 'dbname',
		    valueField: 'id',
		    listeners:{
		    	select:function(combo,record,index){
		    		searchCard_me.getPersnList(record.data.id);
		    	}
		    }
    	});
    	if(dbStore.data.items.length>0){
    		dnComBox.setValue(dbStore.data.items[0].data.id,dbStore.data.items[0].data.dbname);
    		return dnComBox;
    	}
	},creaSearchPanel:function(){//创建查询框
		var emptyText="";
    	if(this.inforkind=='1'){
    		emptyText=common.label.cardMsg.replace("{0}",common.label.cardPersonNameMsg)+"...";//"请输入姓名...";
    	}else if(this.inforkind=='2'){
    		emptyText=common.label.cardMsg.replace("{0}",common.label.cardOrgNameMsg)+"...";//"请输入单位名称...";
    	}else if(this.inforkind=='4'){
    		emptyText=common.label.cardMsg.replace("{0}",common.label.cardJobNameMsg)+"...";//"请输入岗位名称...";
    	}else if(this.inforkind=='6'){
    		emptyText=common.label.cardMsg.replace("{0}",common.label.cardJobNameMsg)+"...";//"请输入岗位名称...";
    	}
		
		//查询图片
		var findImg=Ext.create('Ext.container.Container',{
								width:20,
								height:22,
								border:0,
								style:'border:1px solid #c5c5c5;border-right:none;background:url(/images/hcm/themes/gray/search_fdj2.png) no-repeat center left;'
		});
		//隐藏 输入框左右边框
		Ext.util.CSS.createStyleSheet('#search div{border-left:none !important;border-right:none !important;border-top:none !important;border-bottom:none !important;}');
		
		var searchText=Ext.create('Ext.form.field.Text',{
						    		name:'names',
						    		width:(this.inforkind=='1')?210:120,
						    		height:20,
						    		id:'search',
						    		enableKeyEvents :true,
						    		emptyText:emptyText,
						    		listeners:{//enter 键 查询
								 		specialkey:function(field, e){
								 			if (e.getKey() == e.ENTER) {
								 				searchCard_me.comSearch(field.lastValue)
						                    }
								 		}
								 	}
		});
		var container=Ext.create('Ext.container.Container',{
								width:(this.inforkind=='1')?230:140,
								border:1,
								style: {
								    borderColor: '#C5C5C5',
								    borderStyle: 'solid'
								},
								//height:'100%',
								layout:{
									type:'hbox',
									align:'center'
								},
								items:[findImg,searchText]
		});
		return container;
	},getTreePanel:function(){//登记表树结构
		
		//隐藏 输入框左右边框
		Ext.util.CSS.createStyleSheet('#treeText div{border-left:none !important;border-right:none !important;border-top:none !important;border-bottom:none !important;}');
		//emptyText 登记表 表名
		var tabidText=Ext.create('Ext.form.field.Text',{
					    		width:120,
					    		height:20,
					    		id:'treeText',
					    		enableKeyEvents :true,
					    		editable:false,
					    		border:0,
					    		//fieldStyle:'border-left:none;',
					    		emptyText:''
		})
		
		var rightImg=Ext.create('Ext.container.Container',{
								id:'treeArrows',
								width:20,
								height:22,
								style:'cursor:pointer;border:1px solid #c5c5c5;background:url(/components/querybox/images/down_sign.png) no-repeat center left;',	
								border:0,
								listeners:{
									click:{
										element: 'el', 
										fn:function(){
											treePanel.x=Ext.getCmp('treeArrows').getX()-120;
											treePanel.y=Ext.getCmp('treeArrows').getY()+22;
											if(treePanel.hidden){
												treePanel.setHidden(false);
											}else{
												treePanel.setHidden(true);
											}
										}
									}
								}
		});
		
		var treePanel=Ext.create('Ext.container.Container',{
									width:350,
									height:400,
									id:'card_treePanel',
									floating:true,
									hidden:true,
									border:1,
									layout:'fit',
									shadow: false,
									style:'z-index:99999;margin-top:-1px',
									listeners : {
						                'mouseenter':{  //鼠标滑过可以不监听子元素
						                    element:'el',
						                    fn:function(){
						                    	treePanel.setHidden(false);
						                    }
						                  },
						                  'mouseleave':{
						                    element:'el',
						                    fn:function(){
						                    	treePanel.setHidden(true);
						                    }
						                  }
						              },
									renderTo:Ext.getBody()
		});
		
		
		var tabidTextPanel=Ext.create('Ext.container.Container',{
										width:140,
										id:'treeTextpanel',
										border:1,
										style: {
										    borderColor: '#C5C5C5',
										    borderStyle: 'solid'
										},
										layout:'hbox',
										items:[tabidText,rightImg],
										listeners:{
											render:function(){
												Ext.create('Ext.tip.ToolTip', {
													target:'treeText',
													bodyStyle:"background-color:white;border:1px solid #c5c5c5;",
												    listeners:{
												    	beforeshow:function(tip){
											        	    var text=Ext.getCmp("treeText").getValue();
												        	tip.update("<div style='white-space:nowrap;background-color:#FFFFFF;" +
												        				"overflow:hidden;'>"+text+"</div>");
												    	}
												    }
												});
											}
										}
		});
		
		Ext.require('Card.CardTree',function(){
            Ext.create("Card.CardTree",{
					    tabid:cardGlobalBeanDefault.tabid,
						inforkind:(cardGlobalBeanDefault.inforkind=='10'?"1":cardGlobalBeanDefault.inforkind),
						treeScope:searchCard_me
            });
            
            if(cardTree_me.getTreePanel()&&cardTree_me.getTreePanel().getStore().data){
            	var cardid=cardTree_me.getFirstCard();
            	if(cardid=='0'){//空数据无树结构
            		Ext.getCmp('treeTextpanel').setHidden(true);
            		searchCard_me.loadBookMark(cardGlobalBeanDefault.tabid,true);
            	}else{
            		var data=cardTree_me.getTreePanel();
            		searchCard_me.loadBookMark(cardid.split(":")[0],true);
            		Ext.getCmp('treeText').setValue(cardid.split(":")[1]);
            		treePanel.add(data);
            		if(data.hidden){
            			tabidTextPanel.setHidden(true)
            		}
            		cardGlobalBeanDefault.tabid=cardid.split(":")[0];
            	}
            	
            }
    	},this);
		
		return tabidTextPanel;
	},createSalaryCard:function(res){//我的薪酬登记表  queryflag 按日期显示
		var rs=res.salaryMap; 
		var pdfHidden=true;
		var wordHidden=true;
		if(cardGlobalBeanDefault.inforkind!='10'){
			pdfHidden=cardGlobalBeanDefault.btnFunction.pdfBtn=='true'?false:true;
			wordHidden=cardGlobalBeanDefault.btnFunction.wordBtn=='true'?false:true;
		}else{
			pdfHidden=false;
			wordHidden=false;
		} 
		var queryflag=rs.queryflag;//0 条件查询 1 日期查询
		cardGlobalBeanDefault.queryflag=queryflag;
		cardGlobalBeanDefault.bizDate=rs.bizDate;
		if(this.inforkind=='10'){//我的积分
			cardGlobalBeanDefault.tabid=rs.tabid;
		}
		var toolbar =Ext.create('Ext.toolbar.Toolbar',{
								border:false,
								id:'childToolbar',
								width:'100%',
								height:40
						});
		
		var yearStore=Ext.create('Ext.data.Store',{//年
								 fields:['yearId','yearName'],
								 data:Ext.decode(rs.year)
								});
		var monthStore=Ext.create('Ext.data.Store',{//月
					    fields:['monthId','monthName'],
					    data:[{'monthId':'1','monthName':common.label.Jan},{'monthId':'2','monthName':common.label.Feb},
					    	  {'monthId':'3','monthName':common.label.Mar},{'monthId':'4','monthName':common.label.Apr},
					    	  {'monthId':'5','monthName':common.label.May},{'monthId':'6','monthName':common.label.June},
					    	  {'monthId':'7','monthName':common.label.July},{'monthId':'8','monthName':common.label.Aug},
					    	  {'monthId':'9','monthName':common.label.Sept},{'monthId':'10','monthName':common.label.Oct},
					    	  {'monthId':'11','monthName':common.label.Nov},{'monthId':'12','monthName':common.label.Dec},
					    	  {'monthId':'13','monthName':common.label.allYear}]
						});
		
		var  seasonStore=Ext.create('Ext.data.Store',{//季度
						fields:['seasonId','seasonName'],
						data:[{'seasonId':'1','seasonName':common.label.Spring},{'seasonId':'2','seasonName':common.label.Summer},
							  {'seasonId':'3','seasonName':common.label.fall},{'seasonId':'4','seasonName':common.label.winter},{'seasonId':'5','seasonName':common.label.allYear}]
						});
		
		var startTime=Ext.create('Ext.form.field.Date',{//开始时间
								fieldLabel: common.label.Time+':',
								id:'startDateId',
								labelWidth:50,
								labelAlign:'right',
								width:170,
								format : "Y-m-d",
					    		//id:'startID',
					    		emptyText:''
						});
		var endTime=Ext.create('Ext.form.field.Date',{//结束时间
								fieldLabel:common.label.to,
								id:'endDateId',
								labelWidth:10,
								width:120,
								format : "Y-m-d",
					    		//id:'endID',
					    		emptyText:''
						});
		
		var yearCom=Ext.create('Ext.form.ComboBox',{//年
							fieldLabel: common.label.Theyear+':',
							labelWidth:30,
							id:'yearComId',
							labelAlign:'right',
							store:yearStore,
							queryMode:'local',
							displayField:'yearName',
							valueField:'yearId',
							width:100
						});
		
		var monthCom=Ext.create('Ext.form.ComboBox',{//月
							fieldLabel: common.label.Themonth+':',
							labelWidth:30,
							id:'monthComId',
							labelAlign:'right',
							store:monthStore,
							queryMode:'local',
							displayField:'monthName',
							valueField:'monthId',
							width:100,
							listeners:{
						    	select:function(combo,record,index){
						    		var year=yearCom.getSelection().data.yearId;//年
						    		var month=record.data.monthId;
						    		var map=new HashMap();
										map.put("flag","salaryCount");
										map.put("a0100",cardGlobalBeanDefault.a0100);//cardGlobalBeanDefault.a0100);
										map.put("year",year);
										map.put("month",month);
										Rpc({functionId:'CARD0000001',success:function(res){
									    	   var res=Ext.decode(res.responseText);
									    	   var countBox=Ext.getCmp('countID');
										    	   if(res.count&&res.count!=''){
										    		   var conntStore=Ext.create('Ext.data.Store',{//次数
															  				 fields:['countId','countName'],
															  				 data:Ext.decode(res.count)
										  				});
										    		   countBox.setStore(conntStore);
										    		   countBox.setHidden(false);
										    		   countBox.setValue("all",common.label.allMonth);
										    	   }else{
										    		   countBox.setHidden(true);//无次数时隐藏次数
										    	   }
									    	   
									       		},scope:this},map);
									
						    	}
						    }
						});
		
		var seasonCom=Ext.create('Ext.form.ComboBox',{//季度
							fieldLabel: common.label.season+':',
							labelWidth:30,
							id:'seasonComId',
							labelAlign:'right',
							store:seasonStore,
							queryMode:'local',
							displayField:'seasonName',
							valueField:'seasonId',
							width:110
						});
		
		var countStroe=Ext.create('Ext.data.Store',{
							fields:['countId','countName'],
							data:[{'countId':'year','countName':common.label.year}]
						});
		if(this.inforkind!='10'){
			countStroe.add({'countId':'season','countName':common.label.season});
			countStroe.add({'countId':'month','countName':common.label.month});
			countStroe.add({'countId':'times','countName':common.label.Time});
		}
		var countComBox=Ext.create('Ext.form.ComboBox',{//统计方式
							fieldLabel: common.label.CountWay+':',
							id:'countComId',
							labelWidth:60,
							labelAlign:'right',
						    store: countStroe,
						    width:140,
						    queryMode: 'local',
						    displayField: 'countName',
						    valueField: 'countId',
						    listeners:{
		                        select:function(combo,record,index){// /*0代表安条件查询1代表安月时间查询2代表安时间段查询3.安时间季度查询 4 年*/
		                        	if(record.data.countId=='year'){
		                        		cardGlobalBeanDefault.queryflag="4";//全局参数 queryflag赋值，导出时使用
		                        		if(yearCom.hide){
		                        			yearCom.setHidden(false);
		                        		}
		                        		if(Ext.getCmp('countID')){
		                        			countCom.setHidden(true);
		                        		}
		                        		monthCom.setHidden(true);
		                        		seasonCom.setHidden(true);
		                        		startTime.setHidden(true);
		                        		endTime.setHidden(true);
		                        	}else if(record.data.countId=='season'){
		                        		cardGlobalBeanDefault.queryflag="3";
		                        		if(seasonCom.hide){
		                        			seasonCom.setHidden(false);
		                        		}
		                        		if(yearCom.hide){
		                        			yearCom.setHidden(false);
		                        		}
		                        		if(Ext.getCmp('countID')){
		                        			countCom.setHidden(true);
		                        		}
		                        		monthCom.setHidden(true);
		                        		startTime.setHidden(true);
		                        		endTime.setHidden(true);
		                        	}else if(record.data.countId=='month'){
		                        		cardGlobalBeanDefault.queryflag="1";
		                        		if(yearCom.hide){
		                        			yearCom.setHidden(false);
		                        		}
		                        		if(monthCom.hide){
		                        			monthCom.setHidden(false);
		                        		}
		                        		if(Ext.getCmp('countID')){
		                        			countCom.setHidden(false);
		                        		}
		                        		seasonCom.setHidden(true);
		                        		startTime.setHidden(true);
		                        		endTime.setHidden(true);
		                        	}else if(record.data.countId=='times'){
		                        		cardGlobalBeanDefault.queryflag="2";
		                        		if(startTime.hide){
		                        			startTime.setHidden(false);
		                        		}
		                        		if(endTime.hide){
		                        			endTime.setHidden(false);
		                        		}
		                        		if(Ext.getCmp('countID')){
		                        			countCom.setHidden(true);
		                        		}
		                        		yearCom.setHidden(true);
		                        		monthCom.setHidden(true);
		                        		seasonCom.setHidden(true);
		                        		
		                        	}
		                        		}
						    }
						});
		if(this.inforkind!='10')
			countComBox.setValue(countStroe.data.items[2].data.countId,countStroe.data.items[2].data.countName);
		else{
			countComBox.setValue(countStroe.data.items[0].data.countId,countStroe.data.items[0].data.countName);
			monthCom.setHidden(true);
		}
		
		//次数 针对按月查询  count
		//if(rs.count&&rs.count!=''){
			var conntStore=Ext.create('Ext.data.Store',{//次数
				 fields:['countId','countName'],
				 data:Ext.decode(rs.count)
				});
			var countCom=Ext.create('Ext.form.ComboBox',{//次数
							fieldLabel: common.label.NumeOfTime+':',
							labelWidth:30,
							labelAlign:'right',
							store:conntStore,
							queryMode:'local',
							id:'countID',
							displayField:'countName',
							valueField:'countId',
							width:110	
			});
			countCom.setValue('all',common.label.allMonth);
		//}
		
		/***
		 * 下拉选设置默认值
		 * */
		yearCom.setValue(yearStore.data.items[0].data.yearId,yearStore.data.items[0].data.yearName);//年
		monthCom.setValue(rs.month,monthStore.getById(rs.month));
		seasonCom.setValue(rs.season,seasonStore.getById(rs.season));
		startTime.setValue(rs.time);
		endTime.setValue(rs.time);
		
		this.add(this.getTreePanel());
		this.add(' ');
		this.add(countComBox);//默认显示 年月 查询
		this.add(' ');
		this.add(yearCom);
		this.add(' ');
		this.add(monthCom);
		
		seasonCom.setHidden(true);
		this.add(seasonCom);
		//---
		this.add(startTime);
		startTime.setHidden(true);
		//toolbar.add(text);
		//text.setHidden(true);
		this.add(endTime);
		endTime.setHidden(true);
		
		this.add(countCom);
		if(!rs.count||rs.count=='')
			countCom.setHidden(true);
		//导出按钮
		var menuPdf=Ext.create('Ext.Button',{
						width:100,
						text:common.button.toexport+'PDF',
						hidden:pdfHidden,
						icon:'/images/outpdf.png',
						iconCls:'btn-Img-icon',
						handler: function() {
							var startDate=Ext.getCmp('startDateId').rawValue;
							var endDate=Ext.getCmp('endDateId').rawValue;
							if(startDate!=''&&!Ext.Date.parse(startDate,'Y-m-d')||
							   endDate!=''&&!Ext.Date.parse(endDate,'Y-m-d')){
								 Ext.showAlert(common.label.errorDateMsg)
		    					 return
							}
							var dataBean={//存储日期参数bean
									year:Ext.getCmp('yearComId').getSelection()?Ext.getCmp('yearComId').getSelection().data.yearId:"",
									month:Ext.getCmp('monthComId').getSelection()?Ext.getCmp('monthComId').getSelection().data.monthId:"",
									season:Ext.getCmp('seasonComId').getSelection()?Ext.getCmp('seasonComId').getSelection().data.seasonId:"",
									count:Ext.getCmp('countID').getSelection()?(Ext.getCmp('countID').getSelection().data.countId=='all'?'11':Ext.getCmp('countID').getSelection().data.countId):"",
									startDate:startDate?startDate:"",
									endDate:endDate?endDate:""
							     };
							searchCard_me.excecuteWord("false","pdf",dataBean);
						   }
						});
		var menuWord=Ext.create('Ext.Button',{
						width:120,
						text:common.button.toexport+'WORD',
						icon:'/images/outword.png',
						hidden:wordHidden,
						iconCls:'btn-Img-icon',
						handler: function() {
							var startDate=Ext.getCmp('startDateId').rawValue;
							var endDate=Ext.getCmp('endDateId').rawValue;
							if(startDate!=''&&!Ext.Date.parse(startDate,'Y-m-d')||
							   endDate!=''&&!Ext.Date.parse(endDate,'Y-m-d')){
								 Ext.showAlert(common.label.errorDateMsg)
		    					 return
							}
							var dataBean={//存储日期参数bean
									year:Ext.getCmp('yearComId').getSelection()?Ext.getCmp('yearComId').getSelection().data.yearId:"",
									month:Ext.getCmp('monthComId').getSelection()?Ext.getCmp('monthComId').getSelection().data.monthId:"",
									season:Ext.getCmp('seasonComId').getSelection()?Ext.getCmp('seasonComId').getSelection().data.seasonId:"",
									count:Ext.getCmp('countID').getSelection()?(Ext.getCmp('countID').getSelection().data.countId=='all'?'11':Ext.getCmp('countID').getSelection().data.countId):"",
									startDate:Ext.getCmp('startDateId').value?Ext.getCmp('startDateId').value:"",
									endDate:Ext.getCmp('endDateId').value?Ext.getCmp('endDateId').value:""
							     };
							searchCard_me.excecuteWord("false","word",dataBean);
						   }
						});
		
		this.add(' ');
		var okBtn=Ext.create('Ext.Button',{//点击确定查询薪资
						text:common.button.ok,
						width:50,
						listeners:{
							click:function(){
								searchCard_me.changeTabid(cardGlobalBeanDefault.tabid);
							}
						}
						});
		
		this.add(okBtn);
		this.add(' ');
		this.add(menuPdf);
		this.add(' ');
		this.add(menuWord);
		this.add(toolbar);
		
	},creaConditionCard:function(){//薪酬表设置条件查询时显示内容
		var pdfHidden=false;
		var wordHidden=false;
		if(this.inforkind=='1'||this.inforkind=='2'||this.inforkind=='4'||this.inforkind=='6'){
			pdfHidden=cardGlobalBeanDefault.btnFunction.pdfBtn=='true'?false:true;
			wordHidden=cardGlobalBeanDefault.btnFunction.wordBtn=='true'?false:true;
		}
		this.add(this.getTreePanel());
		//导出按钮
		var menuPdf=Ext.create('Ext.Button',{
						width:100,
						text:common.button.toexport+'PDF',
						icon:'/images/outpdf.png',
						hidden:pdfHidden,
						iconCls:'btn-Img-icon',
						handler:function(){
							searchCard_me.excecuteWord("false","pdf");
						}
						});
		var menuWord=Ext.create('Ext.Button',{
						width:120,
						text:common.button.toexport+'WORD',
						hidden:wordHidden,
						icon:'/images/outword.png',
						iconCls:'btn-Img-icon',
						handler:function(){
							searchCard_me.excecuteWord("false","word");
						}
						});
		if(!pdfHidden){
			this.add(menuPdf);
			this.add(' ');
		}
		if(!wordHidden){
			this.add(menuWord);
		}
		
	}
});