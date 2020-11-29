Ext.define("QuestionnaireTemplate.QuestionnaireBuilder",{
	extend:'Ext.panel.Panel',
	requires:['QuestionnaireTemplate.QuestionItem'],
	xtype:'questionnairebuilder',
	layout:'fit',
	bodyStyle:'background-color:white;z-index:2;',//处理放大缩小浏览器 缺线问题， wangb 20190524
	tools:[],
	/**问卷id**/
	qnId:undefined,
	/**计划id**/
	planId:undefined,
	/**问卷名称**/
	qnName:undefined,
	/**问卷类型**/
	qnType:undefined,
	/**问卷所属单位**/
	qnB0110:undefined,
	/**是否共享**/
	qnShare:undefined,
	/**模版另存名称**/
	qnTemplateName:'',
	/**图片保存路径**/
	imageSavePath:undefined,
	/**是否显示导航，如果设置为true，则进入原始数据分析页面**/
	hideNavigation:false,
	/**返回按钮方法**/
	backButtonFn:undefined,
	/**页面图片路径前缀**/
	imagePrefix:rootPath+'/module/system/questionnaire/',
	/**被调查对象，不传则取表中所有数据**/
	subObject:undefined,
	border:1,
	
	
	/**试卷变动标识**/
	changedState:false,
	
	initComponent: function() {
		this.callParent();
        this.questionnaireBegin();
    },
    //开始生成页面
    questionnaireBegin:function(){
    	var me = this;
    	if(me.hideNavigation){
    		me.addTool([{
    			xtype:'box',
    			id:'chartButton',
    			margin:'0 10',
    			style:'font-size:14px !important;cursor:pointer;',
	    		html:'<a style="font-size:14px !important;">'+QN.template.chartAnalysis+'</a>',
	    		listeners:{
    				render:function(){
    					this.getEl().on('click',function(){
    						me.chartAnalysis();
    					});
    				}
    			}
	    	},{
    			xtype:'box',
    			id:'dataButton',
    			margin:'0 10',
    			style:'font-size:14px !important;cursor:pointer;',
	    		html:'<a style="font-size:14px !important;">'+QN.template.dataAnalysis+'</a>',
	    		listeners:{
    				render:function(){
    					this.getEl().on('click',function(){
    						me.dataAnalysis();
    					});
    				}
    			}
	    	},{
    			xtype:'box',
    			id:'returnButton',
    			margin:'0 10',
    			hidden:me.backButtonFn?false:true,
    			style:'font-size:14px !important;cursor:pointer;',
	    		html:'<a style="font-size:14px !important;">'+QN.template.returnBack+'</a>',
	    		listeners:{
    				render:function(){
    					this.getEl().on('click',function(){
    						var fn = me.backButtonFn;
							fn();
    					});
    				}
    			}
	    	}]);
    		me.dataAnalysis();
    	} else {
    		//创建问卷头
    		me.createHeader();
    		me.createDesign();
    	}
    },
    /**
     * 创建页面顶部toolbar
     */
    createHeader:function(){
    	var me = this;
    	
    	var leftPanel = Ext.create("Ext.Container",{
    		hidden:me.hideNavigation,
    		margin:'10 0 0 10',
    		flex:1,
    		layout:'vbox',
    		defaults:{
    			xtype:'container',
    			layout:{
    				type:'hbox',
    				align:'middle'
    			}
    		},
    		items:[{
        		padding:'0 0 5 10',
        		defaults:{
        			xtype:'image',
        			autoEl:'div',
        			height:24,
        			listeners:{
        				render:function(){
        					this.getEl().on('click',function(){
        						if(this.getId()=='designImg'){
        							me.designTemplate();
        						} else if(this.getId()=='recycleImg'){
        							me.questionnaireRecycle();
        						} else if(this.getId()=='analysisImg'){
        							var img = me.toolbar.queryById('recycleImg');
        							if(img.src.indexOf('nopitchon')!=-1)
        								return;
        							me.analysisTemplate();
        						}
        					},this);
        				}
        			}
        		},
        		items:[{id:'designImg',width:24,style:'cursor:pointer;',src:me.imagePrefix+'images/pitchon.png'},
        		       {width:75,imgCls:'questionnaire_img',src:me.imagePrefix+'images/excessive.png'},
        		       {id:'recycleImg',width:24,style:'cursor:pointer;',src:me.imagePrefix+'images/nopitchon.png'},
        		       {width:75,imgCls:'questionnaire_img',src:me.imagePrefix+'images/excessive.png'},
        		       {id:'analysisImg',width:24,style:'cursor:pointer;',src:me.imagePrefix+'images/nopitchon.png'}]
        	},{
    			defaults:{
    				xtype:'label',
    				style:'cursor:pointer;',
    				listeners:{
        				render:function(){
        					this.getEl().on('click',function(){
        						if(this.text==QN.template.questionnaireDesign){
        							me.designTemplate();
        						} else if(this.text==QN.template.questionnaireRecycle){
        							me.questionnaireRecycle();
        						} else {
        							var img = me.toolbar.queryById('recycleImg');
        							if(img.src.indexOf('nopitchon')!=-1)
        								return;
        							me.analysisTemplate();
        						}
        					},this);
        				}
        			}
    			},
    			items:[{margin:'0 0 0 0',text:QN.template.questionnaireDesign},
    			       {margin:'0 0 0 50',text:QN.template.questionnaireRecycle},
    			       {margin:'0 0 0 50',text:QN.template.questionnaireAnalysis}]
    		}]
    	});
    /*	var buttonPanel = Ext.create("Ext.Container",{
            height:30,
            margin:'0 0 10 0',
            layout:{
                type:'hbox',
                align:'middle',
                pack:'center'
            },
            items:[{
                xtype:'button',
                scope:this,
                style:'font-size:14px !important;cursor:pointer;',
                text:QN.template.questionnaireSave,
                handler:function(){
                    this.saveTemplate("0",false);
                }
            }]
        });*/
    	var rightPanel = Ext.create("Ext.Container",{
    		layout:'hbox',
    		defaults:{
    			xtype:'box',
    			margin:'0 10',
    			style:'font-size:14px !important;',
    			listeners:{
    				render:function(){
    					this.getEl().on('click',function(){
    						if(this.id=='publishButton'){//发布问卷
    							Ext.Msg.confirm(QN.template.msgTitle,"是否发布问卷？",function(button){
    								if(button=='yes'){
    								    Ext.getCmp('returnButton').setVisible(false);
    									me.saveTemplate("1",false);
    								}
    							});
    						} else if(this.id=='previewButton'){//预览问卷
    							me.previewTemplate();
    						} else if(this.id=='chartButton'){//图表分析
    							me.chartAnalysis();
    						} else if(this.id=='dataButton'){//原始数据
    							me.dataAnalysis();
    						} else if(this.id=='returnButton'){
    							var fn = me.backButtonFn;
    							fn();
    						} else if(this.id=='templateButton'){//另存模板
    							me.saveasTemplate();
    						}else if(this.id=='SaveButton'){
    						    me.saveTemplate("0",false);
    						}
    					});
    				}
    			}
    		},
    		items:[
    		       {id:'SaveButton',style:'cursor:pointer;',html:'<a style="font-size:14px !important;">'+QN.template.questionnaireSave+'</a>'},//保存
    			   {id:'previewButton',style:'cursor:pointer;',html:'<a style="font-size:14px !important;">'+QN.template.previewQuestionnaire+'</a>'},
    		       {id:'publishButton',style:'cursor:pointer;',html:'<a style="font-size:14px !important;">'+QN.template.publishQuestionnaire+'</a>'},
    		       {id:'templateButton',style:'cursor:pointer;',html:'<a style="font-size:14px !important;">'+QN.template.saveasTemplate+'</a>'},
    		       {id:'chartButton',style:'cursor:pointer;',html:'<a style="font-size:14px !important;">'+QN.template.chartAnalysis+'</a>',hidden:true},
    		       {id:'dataButton',style:'cursor:pointer;',html:'<a style="font-size:14px !important;">'+QN.template.dataAnalysis+'</a>',hidden:true},
    		       {id:'returnButton',style:'cursor:pointer;',html:'<a style="font-size:14px !important;">'+QN.template.returnBack+'</a>',hidden:me.backButtonFn?false:true}]
    	});
    	me.toolbar = Ext.widget("toolbar",{
    		layout:{
    			type:'hbox',
    			align:'middle',
    			pack:'center'
    		},
    		dock:'top',
    		items:[leftPanel,"->",rightPanel]
    	});
		me.addDocked(me.toolbar);
    },
    //创建问卷页面
    createQuestionnaire:function(template,data){
    	var me = this;
    	//
    	var menuPanel = me.createMenu(data);
    	
    	var mainPanel = me.createMain(template);
    	
    	me.designPanel = Ext.widget('container',{
    		layout:'hbox',
    		items:[menuPanel,mainPanel]
    	});
    	
    	me.add(me.designPanel);
    },
    /**
     * 创建左侧题目类型
     * @param data
     * @returns
     */
    createMenu:function(data){
    	var commons = this.createPanel(data.common);
    	//常用题型
    	var commonPanel = Ext.create("Ext.panel.Panel",{
    		id:'commonpanel',
    		border:1,
    		title:QN.template.commonQuestions,
    		collapsible:true,
    		hideHeaders:true,
    		layout:'vbox',
    		items:commons,
    		listeners:{
    			beforeexpand:function(panel){
    				scorePanel.collapse();
    			}
    		}
    	});
    	
    	var scores = this.createPanel(data.score);
    	//评分题
    	var scorePanel = Ext.create("Ext.panel.Panel",{
    		id:'scorepanel',
    		border:1,
    		title:QN.template.scoreQuestions,
    		margin:'10 0 0 0',
    		collapsible:true,
    		hideHeaders:true,
    		collapsed:true,
    		layout:'vbox',
    		items:scores,
    		listeners:{
    			beforeexpand:function(panel){
    				commonPanel.collapse();
    			}
    		}
    	});
    	
    	var menuPanel = Ext.create("Ext.Container",{
    		region:'west',
    		padding:10,
    		layout:'vbox',
    		defaults:{
    			width:200
    		},
    		items:[commonPanel,scorePanel]
    	});
    	
    	return menuPanel;
    },
    /**
     * 创建问卷显示区域
     * @param template
     * @returns
     */
    createMain:function(template){
    	if(!template.instruction)
    		template.instruction = QN.template.instruction;
    	if(!template.finishmsg)
    		template.finishmsg = QN.template.finishmsg;
    	if(!template.advanceendmsg)
    		template.advanceendmsg = QN.template.advanceendmsg;
    	//问卷名称描述面板
    	var descriptionPanel = Ext.create("Ext.Container",{
    		id:'descriptionpanel',
    		height:50,
    		border:1,
    		style:{
    			borderColor:'#C5C5C5',
    			borderStyle:'solid'
    		},
    		layout:{
    			type:'hbox',
    			align:'middle',
    			pack:'center'
    		},
    		items:{id:'qnlongname',xtype:'labeleditor',style:'font-size:16px !important;',text:template.qnname,longText:template.qnlongname}
    	});
    	
    	//提示面板
    	var promptPanel = Ext.create("Ext.Container",{
    		id:'promptpanel',
    		minHeight:80,
    		border:1,
    		style:{
    			borderColor:'#C5C5C5',
    			borderStyle:'solid'
    		},
    		margin:'20 0 0 0',
    		padding:'5 5 5 5',
    		layout:{
    			type:'vbox',
    			align:'stretch',
    			pack:'middle'
    		},
    		items:{id:'instruction',xtype:'labeleditor',style:'font-size:15px !important;',text:template.instruction}
    	});
    	
    	var questionlist = template.questionList;
    	var list = new Array();
    	for ( var i = 0; i < questionlist.length; i++) {
    		var obj = questionlist[i];
    		var typekind = obj.typekind;
    		if((typekind==5||typekind==6)&&!this.imageSavePath)
    			this.imageSavePath = obj.imageSavePath;
			list.push({xtype:'questionitem',margin:'10 0 0 0',questionObj:obj,imageSavePath:this.imageSavePath});
		}
    	//题目面板
    	this.quesPanel = Ext.create("Ext.panel.Panel",{
    		id:'quesPanel',
    		minHeight:100,
    		border:0,
    		layout:{
    			type:'vbox',
    			align:'stretch'
    		},
    		items:list
    	});
    	
    	var comboStore = Ext.create("Ext.data.Store",{
    		fields:['value','name'],
    		data:[{'value':'1','name':QN.template.normalCompletion},{'value':'2','name':QN.template.endAdvance}]
    	});
    	
    	//警告面板
    	var warnPanel = Ext.create("Ext.Container",{
    		height:100,
    		border:1,
    		style:{
    			borderColor:'#C5C5C5',
    			borderStyle:'solid'
    		},
    		margin:'20 0 10 0',
    		padding:'0 5 0 5',
    		layout:'vbox',
	    	items:[{
	    		xtype:'container',
	    		layout:'hbox',
	    		items:[{
	    			xtype:'combobox',
	    			margin:'20 0 20 20',
	    			store:comboStore,
	    			valueField:'value',
	    			displayField:'name',
	    			triggerAction:'all',//每次下拉均显示全部选项
	    			listWidth:105,//数据显示框长度
	    			width:90,//下拉框长度
	    			value:QN.template.normalCompletion,//默认选中
	    			listeners:{
	    				select:function(combo,record,index){
	    					if(record.data.value=="1"){
	    						Ext.getCmp('advance').setVisible(false);
	    						Ext.getCmp('finish').setVisible(true);
	    					} else {
	    						Ext.getCmp('finish').setVisible(false);
	    						Ext.getCmp('advance').setVisible(true);
	    					}
	    				}
	    			}
	    		},{
	    			xtype:'label',
	    			margin:'20 0 0 5',
	    			text:QN.template.warningTitle
	    		}]
    		},{
        		id:'finish',
            	xtype:'labeleditor',
            	width:'99%',
            	style:'font-size:15px !important;',
            	text:template.finishmsg
            },{
            	id:'advance',
            	xtype:'labeleditor',
            	hidden:true,
            	width:'99%',
            	style:'font-size:15px !important;',
            	text:template.advanceendmsg
            }]});
    	
    	
    	
    	var mainPanel = Ext.create("Ext.Container",{
    		region:'center',
    		autoScroll:true,
    		id    :'mainPanelID',//xiegh 20170418 bug27014
    		height:'100%',
    		flex:1,
    		layout:{
    			type:'vbox',
    			align:'stretch'
    		},
    		padding:10,
    		items:[descriptionPanel,promptPanel,this.quesPanel,warnPanel]
    	});
    	
    	return mainPanel;
    },
    /**
     * 根据数据创建panel
     * @param data
     * @returns {Array}
     */
    createPanel:function(data){
    	var me = this;
    	var items = new Array();
		for ( var i = 0; i < data.length; i++) {
			var obj = data[i];
			if(obj.typeKind=="1")
				obj.img = me.imagePrefix+'images/radioquestion.png';
			else if(obj.typeKind=="2")
				obj.img = me.imagePrefix+'images/checkboxquestion.png';
			else if(obj.typeKind=="3")
				obj.img = me.imagePrefix+'images/fieldtextquestion.png';
			else if(obj.typeKind=="4")
				obj.img = me.imagePrefix+'images/mulfieldtextquestion.png';
			else if(obj.typeKind=="5")
				obj.img = me.imagePrefix+'images/picradioquestion.png';
			else if(obj.typeKind=="6")
				obj.img = me.imagePrefix+'images/piccheckboxquestion.png';
			else if(obj.typeKind=="7")
				obj.img = me.imagePrefix+'images/matrixradio.png';
			else if(obj.typeKind=="8")
				obj.img = me.imagePrefix+'images/matrixcheckbox.png';
			else if(obj.typeKind=="9")
				obj.img = me.imagePrefix+'images/descriptionquestion.png';
			else if(obj.typeKind=="10")
				obj.img = me.imagePrefix+'images/pagebreak.png';
			else if(obj.typeKind=="11")
				obj.img = me.imagePrefix+'images/cutoffline.png';
			else if(obj.typeKind=="12")
				obj.img = me.imagePrefix+'images/scoringquestions.png';
			else if(obj.typeKind=="13")
				obj.img = me.imagePrefix+'images/scalequestion.png';
			else if(obj.typeKind=="14")
				obj.img = me.imagePrefix+'images/matrixscoring.png';
			else if(obj.typeKind=="15")
				obj.img = me.imagePrefix+'images/matrixscale.png';
			var cls = "cursor:pointer";
			if(i!=0)
				cls = "border-top:1px solid #C5C5C5 !important;cursor:pointer;";
			var item = Ext.create("Ext.panel.Panel",{
				width:200,
				height:35,
				border:0,
				checked:false,
				style:cls,
				padding:'0 0 0 10',
				layout:{
					type:'hbox',
					align:'middle'
				},
				items:[{
					xtype:'image',
					width:24,
					height:24,
					src:obj.img
				},{
					xtype:'label',
					margin:'0 0 0 5',
					style:'cursor:pointer',
					text:obj.typeName,
					itemId:obj.typeKind
				}],
				listeners:{
	    			render:function(){
	    				this.getEl().on({
		    				'dblclick':{
		    					fn:function(){
			    					var typeKind = this.items.items[1].itemId;
			    					var continueFn = function(typekind,imageSavePath){
				    					var indexItem = me.quesPanel.query('questionitem[isChecked=true]');
				    					var index = undefined;
				    					for ( var i = 0; i < indexItem.length; i++) {
											index = me.quesPanel.items.indexOf(indexItem[i]);
										}
				    					var item = undefined;
				    					if(typeKind=="5"||typeKind=="6"){
				    						item = Ext.widget('questionitem',{xtype:'',margin:'10 0 0 0',
				    							questionType:parseInt(typeKind),imageSavePath:imageSavePath});
				    					} else {
				    						item = Ext.widget('questionitem',{xtype:'',margin:'10 0 0 0',
				    							questionType:parseInt(typeKind)});
				    					}
				    					if(index>=0){
				    						me.quesPanel.insert(index,item);
				    					} else {
				    						me.quesPanel.add(item);
				    					}
				    					me.quesPanel.ownerCt.setScrollY(item.getLocalY());
				    					item.orderQuestionNumber(me.quesPanel);
			    					};
			    					if(typeKind=="5"||typeKind=="6")
			    						me.saveTemplate("2", true, continueFn, typeKind);
			    					else
			    						me.saveTemplate("2", false, continueFn, typeKind);
			    				},
			    				scope:this
			    			},
		    				'click':{
		    					fn:function(){
		    						if(this.checked==false){
		    							var item = me.designPanel.query('panel[checked=true]');
			    						for ( var i = 0; i < item.length; i++) {
											if(item[i].checked){
												item[i].checked = false;
												item[i].removeCls('itemCheck');
												item[i].removeBodyCls('itemCheck');
												break;
											}
										}
		    							this.checked = true;
		    							this.addCls('itemCheck');
			    						this.addBodyCls('itemCheck');
		    						} else {
		    							this.checked = false;
		    							this.removeCls('itemCheck');
		    							this.removeBodyCls('itemCheck');
		    						}
		    					},
		    					scope:this
		    				}
	    				});
	    			}
	    		}
			});
			items.push(item);
		}
		return items;
    },
    /**
     * 保存或发布问卷
     * @param param =0 保存问卷 =1 发布问卷 =2 自动保存，不提示保存成功 =3存为模板
     * @param flag true 需要创建图片保存路径 false不需要
     * @param continueFn 执行添加题目的方法
     * @param typekind 题目类型
     */
    saveTemplate:function(param,flag,continueFn,typekind){
    	var me = this;
    	//自动保存时，如果上一次保存未完成，跳过此次保存
    	if(param==2 && me.saving)
    	    return;
    	    
    	/*获取问卷参数*/
    	var quesObj = new Object();
    	quesObj.qnid = me.qnId;
    	quesObj.planid = me.planId;
    
	if(param==3){
    	   quesObj.qnname = me.qnTemplateName;
    	   quesObj.qnlongname = me.qnTemplateName;
    	   //zhangh 2020-1-17【55988】将问卷存为模板时，不选择问卷分类，查看模板时，点到问卷分类文本框会显示一个n
		   if(me.qnType == null){
			  me.qnType='';
		   }
    	   quesObj.qntype = me.qnType;
    	   quesObj.qnshare = me.qnShare;
    	   quesObj.qnb0110 = me.qnB0110;
    	}else{
    	   quesObj.qnname = Ext.getCmp('qnlongname').text;
    	   quesObj.qnlongname = Ext.getCmp('qnlongname').longText;
    	}
    	quesObj.instruction = Ext.getCmp('instruction').longText;
    	quesObj.finishmsg = Ext.getCmp('finish').longText;
    	quesObj.advanceendmsg = Ext.getCmp('advance').longText;
    	var items = me.quesPanel.items.items;
    	var arr = new Array();
    	for ( var i = 0; i < items.length; i++) {
			arr.push(items[i].getQuestionObject());
		}
    	quesObj.questionList = arr;
    	var vo = new HashMap();
    	vo.put("questionnaire", quesObj);
    	vo.put("param", param);
    	if(flag&&!me.imageSavePath)
    		vo.put("flag", true);
    	else
    		vo.put("flag", false);
    	//添加正在保存标记，保存成功后重置为false表示保存完成。当保存未完成时，其他保存请求不执行。防止疯狂重复添加某道题时后台报错
    	me.saving = true;
    	Rpc({functionId:'QN30000002',success:function(res){
    		me.saving = false;
    		var resultObj = Ext.decode(res.responseText);
    		if(resultObj.errorMsg){
    		     Ext.showAlert(resultObj.errorMsg);
    		     //xiegh 2017/3/27 当问卷名字相同时，使返回键可见
    		     Ext.getCmp('returnButton').setVisible(true);
    		     return;
    		}
    		me.qnId = resultObj.qnid;
    		me.planId = resultObj.planid;
    		if(resultObj.imageSavePath)
    			me.imageSavePath = resultObj.imageSavePath;
    		if(continueFn)
    			continueFn(typekind,me.imageSavePath);
    		if(param=="0"||param=="3"){
    			Ext.Msg.alert(QN.template.messageTitle,QN.template.messageSave);
    		} else if(param=="1"){
    			me.questionnaireRecycle();
    			Ext.getCmp('returnButton').setVisible(true);
    		}
    	}}, vo);
    
    },
    /**
     * 预览问卷
     */
    previewTemplate:function(){
    	var me = this;
    	var continueFn = function(){
    		window.open(rootPath+"/module/system/questionnaire/template/PreviewQn.html?qnId="+(me.qnId==null?"":me.qnId),"_blank","height="+(document.body.offsetHeight+60)+",width="+(document.body.offsetWidth+170)+",top=0,left=0,toolbar=yes,menubar=yes,scrollbars=yes, resizable=yes,location=yes, status=yes");
    	};
    	me.saveTemplate("2", false, continueFn, null);
    },
    /**
     * 问卷回收
     */
    questionnaireRecycle:function(){
    	var me = this;
    	
    	var recycleImg = me.toolbar.queryById('recycleImg');
    	if(recycleImg.src.indexOf('nopitchon')==-1)
    		return;
    	me.setTitle(QN.template.recycle);
		recycleImg.setSrc(me.imagePrefix+'images/pitchon.png');
		me.toolbar.queryById('designImg').setSrc(me.imagePrefix+'images/nopitchon.png');
		var analysisImg = me.toolbar.queryById('analysisImg');
		var flag = false;
		if(analysisImg.src.indexOf('nopitchon')==-1)
			flag = true;
		analysisImg.setSrc(me.imagePrefix+'images/nopitchon.png');
		
		me.toolbar.queryById('chartButton').setVisible(false);
        me.toolbar.queryById('SaveButton').setVisible(false);//发布问卷时隐藏保存按钮		
		me.toolbar.queryById('dataButton').setVisible(false);
		me.toolbar.queryById('previewButton').setVisible(false);
		me.toolbar.queryById('publishButton').setVisible(false);
		me.toolbar.queryById('templateButton').setVisible(false);
		
		if(flag){
			Ext.require('QuestionnaireRecovery.RecoveryQn',function(){
				me.recoveryPanel = Ext.create("QuestionnaireRecovery.RecoveryQn",{qnId:me.qnId,planId:me.planId});
				me.removeAll(true);
				me.add(me.recoveryPanel);
			});
		} else {
			me.saveTemplate("1", false, function(){
				Ext.require('QuestionnaireRecovery.RecoveryQn',function(){
					me.recoveryPanel = Ext.create("QuestionnaireRecovery.RecoveryQn",{qnId:me.qnId,planId:me.planId});
					me.removeAll(true);
					me.add(me.recoveryPanel);
				});
			});
		}
    },
    /**
     * 跳转到问卷设计
     */
    designTemplate:function(){
    	var me = this;
    	
    	var designImg = me.toolbar.queryById('designImg');
    	if(designImg.src.indexOf('nopitchon')==-1)
    		return;
    	me.setTitle(QN.template.design);
    	designImg.setSrc(me.imagePrefix+'images/pitchon.png');
		me.toolbar.queryById('recycleImg').setSrc(me.imagePrefix+'images/nopitchon.png');
		me.toolbar.queryById('analysisImg').setSrc(me.imagePrefix+'images/nopitchon.png');
		
		me.toolbar.queryById('chartButton').setVisible(false);
		me.toolbar.queryById('dataButton').setVisible(false);
		me.toolbar.queryById('previewButton').setVisible(true);
		me.toolbar.queryById('publishButton').setVisible(true);
    	
		me.createDesign();
    },
    /**
     * 创建问卷设计页面
     */
    createDesign:function(){
    	var me = this;
    	me.removeAll(true);
    	
    	var vo = new HashMap();
    	vo.put("qnid", me.qnId);
    	vo.put("qnname", me.qnName);
    	Rpc({functionId:'QN30000001',success:function(res){
    		var resultObj = Ext.decode(res.responseText);
    		var type = Ext.decode(resultObj.questionType);
    		var template = Ext.decode(resultObj.qn_template);
        	//生成试卷
    		me.createQuestionnaire(template,type);
    	}},vo);
    },
    /**
     * 结果分析
     */
    analysisTemplate:function(){
    	var me = this;
		
		var analysisImg = me.toolbar.queryById('analysisImg');
    	if(analysisImg.src.indexOf('nopitchon')==-1)
    		return;
    	me.setTitle(QN.template.analysis);
    	analysisImg.setSrc(me.imagePrefix+'images/pitchon.png');
		me.toolbar.queryById('designImg').setSrc(me.imagePrefix+'images/nopitchon.png');
		me.toolbar.queryById('recycleImg').setSrc(me.imagePrefix+'images/nopitchon.png');
		
		me.toolbar.queryById('chartButton').setVisible(true);
		me.toolbar.queryById('dataButton').setVisible(true);
		
		me.dataAnalysis();
    },
    /**
     * 图表分析
     */
    chartAnalysis:function(){
    	var me = this;
    	
    	me.removeAll(true);
    	Ext.require('QuestionnaireAnalysis.ChartAnalysis',function(){
    		me.chartAnalysisPanel = Ext.create("QuestionnaireAnalysis.ChartAnalysis",{qnId:me.qnId,planId:me.planId,subObject:me.subObject});
        	me.add(me.chartAnalysisPanel);
    	});
    },
    /**
     * 原始数据分析
     */
    dataAnalysis:function(){
    	var me = this;
    	
    	me.removeAll(true);
    	var vo = new HashMap();
    	vo.put("qnid", me.qnId);
    	vo.put("planid", me.planId);
    	vo.put("subobject", me.subObject?me.subObject:'');
    	Rpc({functionId:'QN60000001',success:function(res){
    		var resultObj = Ext.decode(res.responseText);
    		var obj = Ext.decode(resultObj.returnstr);
    		obj.columnNowrap = true;// bug 36767 标题不换行显示        wangb 20180704
    		me.dataAnalysisPanel = new BuildTableObj(obj).getMainPanel();
    		
    		me.add(me.dataAnalysisPanel);
    	}},vo);
    },
    /**
     * 另存模板窗口
     */
    saveasTemplate:function(){
    	var me = this;
    	var store = Ext.create('Ext.data.Store',{
			fields:['name','value'],
			data:[{'name':'调查','value':'1'},{'name':'投票','value':'2'},{'name':'测评','value':'3'},{'name':'表单','value':'4'}]
		});
    	me.tempWin = Ext.widget('window',{
    		title:QN.template.saveasTemplate,
    		height:265,
    		width:345,
    		resizable:false,
    		modal:true,
    		layout:{type:'vbox',
    		        align:'center',
    		        pack:'center'},
    		items:[{xtype:'textfield',
    				id:'templatename',
    				fieldLabel:'模板名称',
    				padding:'10 0 10 0',
    				width:210,
					//限制名称输入最大长度为18，和修改时保持一致
					maxLength:18,
    				allowBlank:false,
            		allowOnlyWhitespace:false,
    				labelWidth: 55},{
				xtype:'combobox',
				id:'qnsep',
				store:store,
				fieldLabel:'问卷分类',
				padding:'10 0 10 0',
				width:210,
				labelWidth: 55,
				value:'1',
				displayField:'name',
				valueField:'value'
        	},{
					xtype:'container',
					border:false,
					layout:'table',
					padding:'10 0 10 0',
					items:[{
							xtype:'box',
							width:60,
							border:false,
							html:'所属机构:'
						},{
							xtype:'codecomboxfield',
							id:'b0110',
							border:false,
							codesetid:"UN",
							ctrltype:"3",
							nmodule:"4",
							readOnly:false,
							width:150,
							value:QN_global.funcpriv
						}]
	        	},{
					xtype:'container',
					border:false,
					layout:'table',
					padding:'10 0 10 0',
					items:[{
							xtype:'box',
							width:60,
							border:false,
							html:'共享状态:'
						},{
							xtype:'radiogroup',
							id:'share',
							width:150,
//							margin:'0 0 0 10',
							columns:2,
							vertical:true,
							items:[{boxLabel:'公有',name:'share',inputValue:'1',checked:true},{boxLabel:'私有',name:'share',inputValue:'0'}],
							listeners:{
								'change':function(item,value) {
									if (value.share == '0') {
										Ext.getCmp('b0110').setValue("");
										Ext.getCmp('b0110').setDisabled(true);
									}
									else {
										Ext.getCmp('b0110').setDisabled(false);
									}
								}
							}
						}]}],
						buttonAlign: 'center',
						buttons:[{text:'确定',handler:function(){
									me.qnType = Ext.getCmp('qnsep').getValue();
									me.qnB0110 = Ext.getCmp('b0110').getValue();
									me.qnShare = Ext.getCmp('share').getValue().share;
									me.qnTemplateName = Ext.getCmp('templatename').getValue();
									if(!me.qnTemplateName||!(me.qnTemplateName.replace(/(^\s*)|(\s*$)/g, ""))){
										return;
									}
									if(me.qnTemplateName.length > 18){
										return;
									}
									me.saveTemplate("3",false);
									me.tempWin.close();
						}},{text:'取消',handler:function(){
									me.tempWin.close();
						}}]
    	}).show();
    	Ext.getCmp("templatename").focus();
    }
});
function analysisCheckRenderFn(value, metaData, record, rowIndex, colIndex, store, view){
	var itemid = metaData.column.dataIndex,
		typekind = itemid.substring(0,itemid.indexOf('_'))+'_typekind',
		kind = record.data[typekind];
	if(kind=='2'||kind=='6'||kind=='8'){
		if(value=='1')
			return '<img src="'+rootPath+'/module/system/questionnaire/images/check.png"></img>';
		else
			return '<img src="'+rootPath+'/module/system/questionnaire/images/uncheck.png"></img>';
	} else {
		if(value=='1')
			return '<img src="'+rootPath+'/module/system/questionnaire/images/radiochecked.png"></img>';
		else
			return '<img src="'+rootPath+'/module/system/questionnaire/images/radiouncheck.png"></img>';
	}
}