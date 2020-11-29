/**
 * 维护评审会议主界面
 * haosl 
 * 2018-4-13
 */
Ext.define("ReviewMeetingURL.ReviewMeetingSetting",{
	extend:"Ext.window.Window",
	requires:['ReviewMeetingURL.CreateMeetingMain',
				'ReviewMeetingURL.ReviewConsole'],
	layout:'border',
	bodyStyle:'background:#FFFFFF',
	overflowY:'auto',
	overflowX:'hidden',
	border:false,
	resizable :false,
	maximized:true,
	listeners:{
		beforedestroy:function(){
			//保存会议编辑页面或者评审人员设置页面
			var tab1 = Ext.getCmp("tab_1");
			var tab2 = Ext.getCmp("tab_2");
			if(tab1 && !tab1.isHidden()){
				meetingMain.next(tab1.getForm(),true);
			}else if(tab2){
				this.saveExpertInfo(true);
			}
			//刷新会议数据
     		var meetingStore = Ext.data.StoreManager.lookup("meetingStore");
     		if(meetingStore){
     			meetingStore.load({
				params:{
					scheme:ReviewMeetingPortal.schemeArray.join(",")
				}})
     		}
		},
		close:function(){
			//刷新会议数据
     		var meetingStore = Ext.data.StoreManager.lookup("meetingStore");
     		if(meetingStore){
     			meetingStore.load({
				params:{
					scheme:ReviewMeetingPortal.schemeArray.join(",")
				}})
     		}
		}
	},
	constructor:function(config) {
		ReviewMeetingSetting  = this;
		
		//存储创建的申报人分组页面对象，为了还原全局变量jobtitle_reviewconsole
		ReviewMeetingSetting.reviewConsoleMap = {};
		//初始化   记录会议信息
		this.meetingRecord = "";
		ReviewMeetingSetting.callParent(arguments);
	//	this.createStyle();
		this.init(config);
	},
	//初始加载页面
	init:function(config) {
		var me = this;
		this.initMeeting();
		me.titleInfoMap={};
		me.titleInfoMap.checkedIndex=-1;
		me.titleInfoMap.indexArr=[1,2];//存放index
		//页面是否可编辑
		this.readOnly = config.readOnly;
		this.w0321 = config.w0321;//会议状态
		
		//是否可编辑  noCanEdit =true 不可编辑   noCanEdit=false 可编辑
		this.noCanEdit = this.readOnly || this.w0321=="05"||this.w0321=='06';//进行中和结束的会议同样不可以编辑
		
		Ext.EventManager.onWindowResize(function(){
			var width = Ext.getBody().getWidth();
			var height = Ext.getBody().getHeight();
			ReviewMeetingSetting.setWidth(width);
			ReviewMeetingSetting.setHeight(height);
		},this);
		
		this.getTopTitleTags();
		
		this.w0301 = config.w0301||"";
		 
		if(Ext.isEmpty(this.w0301)){
			this.setTitle(zc.editmeeting.mainview.paneltitle);
		}else{
			if(this.noCanEdit){
				this.setTitle(zc.editmeeting.mainview.viewtitle);
			}else{
				this.setTitle(zc.editmeeting.mainview.edittitle);
			}
			
		}
		
		//跳转到会议主题维护界面
		this.jumpTabByIndex(1);
	},
	
	createStyle:function(){
		Ext.util.CSS.createStyleSheet(".x-toolbar-default{border:0px !important;}","border1");
		Ext.util.CSS.createStyleSheet(".x-grid-body{border-left-width:0px !important;border-right-width:0px !important;}","border2");
		Ext.util.CSS.createStyleSheet(".x-grid-header-ct{border-left-width:0px !important;border-right-width:0px !important;}","border3");
	},
	/**
	 * 初始化界面需要的一些会议参数
	 */
	initMeeting:function(){
		var map = new HashMap();
		map.put("opt","3");
		//w0301 在创建会议时，w0301为空
		map.put("w0301",this.w0301);
		//初始化测评表和所属机构
		Rpc({functionId:'ZC00002313',async:false,success:function(res){
	 		var result = Ext.decode(res.responseText);
	 		ReviewMeetingSetting.assessmentTables = result.per_templates;
	 		ReviewMeetingSetting.meetingRecord = result.meetingData;
	 	},scope:this},map);
	},
	/**
	 * 会议维护界面
	 */
	getTopTitleTags:function(){
		var me = this;
		var titleTags = Ext.create("Ext.container.Container",{
			id:'titleTags',
			margin:'5 0 0 5',
			layout:'hbox',
			region:'north',
			defaultType:'container',
			items:[{//创建会议主题
				id:"title_1",
				width:180,
				height:28,
				cls:'title_bg_start title_bg_checked',
				html:'<div class="titleTags_item" onclick="ReviewMeetingSetting.jumpTabByIndex(1,\'titleclick\');">1.&nbsp;&nbsp;'+zc.editmeeting.mainview.meetingsubject+'</div>'
			},{//评审人员设置
				id:"title_2",
				width:180,
				height:28,
				cls:'title_bg_unchecked',
				html:'<div class="titleTags_item" onclick="ReviewMeetingSetting.jumpTabByIndex(2,\'titleclick\');">2.&nbsp;&nbsp;'+zc.editmeeting.mainview.expertsetting+'</div>'
			}]
		})
		ReviewMeetingSetting.add(titleTags);  
		//动态生成申报人分组的tab标题
	//	me.createTabsBySegments();
		
	},
	/**
	 * 动态生成申报人分组的tab标题
	 */
	createTabsBySegments:function(){
		//动态生成申报人分组title
		var record = ReviewMeetingSetting.meetingRecord;
		
		var titleTags = Ext.getCmp("titleTags");
		if(record){
			titleTags.remove("title_3",true);
			titleTags.remove("title_4",true);
			titleTags.remove("title_5",true);
			var segments = record.segments;
			this.titleInfoMap.indexArr = [1,2];
			for(var i=0 ; segments && i<segments.length;i++){
				var segment = segments[i];//评审环节号
				var index = i+3;
				//存放生成的title按钮
				this.titleInfoMap.indexArr[index-1]=index;//存放index
				var titleName = index+".&nbsp;&nbsp;"+zc.editmeeting.mainview.reportercategories;
				switch(segment){
					case '1':
						titleName = index+".&nbsp;&nbsp;"+zc.label.inReview+zc.editmeeting.mainview.voterule;//评委会
						break;
					case '2':
						titleName += "("+zc.label.inExpert+")";//专业组
						break;
					case '3':
						titleName += "("+zc.label.exExpert+")";//同行
						break;
					case '4':
						titleName += "("+zc.label.inOther+")";//二级单位
						break;
				}		
				var container =	Ext.create("Ext.container.Container",{
					id:"title_"+index,
					width:180,
					height:28,
					cls:'title_bg_unchecked',
					html:'<span class="titleTags_item" onclick="ReviewMeetingSetting.jumpTabByIndex('+index+',\'titleclick\')">'+titleName+'</span>'
				});
				
				titleTags.add(container);
				
			}
		}
		ReviewMeetingSetting.add(titleTags);  
	},
	/**
	 * 响应 tab标题的点击事件
	 * @param {} index
	 */
	clickTitleFunc:function(index){
		var me = this;
		if(index==me.titleInfoMap.checkedIndex){
			return;	
		}
		//设置标题选中效果  haosl
		me.setTitleChecked(index);
	},
	/**
	 * 设置点击后的样式
	 * @param {} cmp
	 * @param {} index
	 */
	setTitleChecked:function(index){
		var me = this;
		me.removeTitleCls();
		me.titleInfoMap.checkedIndex=index;//选中title的index
		var indexArr = me.titleInfoMap.indexArr;
		for(var i=0;i<indexArr.length;i++){
			var index_ = i+1;
			var endIndex = indexArr.length;
			var cmp = Ext.getCmp("title_"+index_);
			if(!cmp)continue; 
			if(index_==index){
				//设置选中标题的样式
				if(index_!=endIndex){
					cmp.addCls('title_bg_checked');
				}else{//选中标题如果是最后一个时的样式
					cmp.addCls('title_bg_checked_end');
				}
			}else{
				//设置非选中标题的样式
				if(index_==index+1){
					cmp.addCls(['title_bg_unchecked']);
				}else{
					cmp.addCls("title_bg");
				}
			}
		}
	},
	/**
	 * 移除所有title的样式
	 * @param {} cmp
	 */
	removeTitleCls:function(){
		var me = this;
		var clsArr =['title_bg_checked','title_bg_checked_end','title_bg_unchecked','title_bg']; 
		//同时移除之前选中title的样式
		var checkedIndex = me.titleInfoMap.checkedIndex;
		var indexArr = me.titleInfoMap.indexArr;
		for(var i=0;i<indexArr.length;i++){
			var index = i+1;
			var cmp = Ext.getCmp("title_"+index);
			if(cmp)
				cmp.removeCls(clsArr);
		}
	},
	/**
	 * 创建|编辑会议页面
	 * @param {} w0301
	 */
	getCenterPanel:function(w0301){
		var CreateMeetingMain = Ext.create("ReviewMeetingURL.CreateMeetingMain",{
			w0301:w0301,
			assessmentTables:ReviewMeetingSetting.assessmentTables,
	 		meetingRecord:ReviewMeetingSetting.meetingRecord,
	 		readOnly:this.readOnly,
	 		w0321:this.w0321
		});
		return CreateMeetingMain.getMainView();
	},
	/**
	 * 评审成员设置页面
	 */
	createPersonSettingPage:function(){
		var tab_2 = Ext.create("Ext.form.Panel",{
			width:'100%',
			id:'tab_2',
			layout:'vbox',
			region:'center',
			border:true,
			margin:'0 0 10 0',
			padding:'0 0 0 5',
			overflowY:'auto',
			overflowX:'hidden',
			trackResetOnLoad:true,
			buttonAlign:'center',
			listeners:{
				//数据回显
				afterrender:function(formPanel){
					var expertSetting = ReviewMeetingSetting.getExpertSetingData();
					formPanel.getForm().loadRecord(new Ext.data.Model(expertSetting));
					if(expertSetting.subjectlist)
						ReviewMeetingSetting.showSubjectsInfo(expertSetting.subjectlist);
					//禁用一些配置
					ReviewMeetingSetting.isCanEditSegmentsData();
				}
			},
			defaults:{//默认配置
				xtype:'container',
				margin:'20 30 0 30',
				padding:'0 2 0 0',
				width:'96%',
				style: {
		            background: 'none'
		        }
				
			},
			fbar:[{
					 text: zc.editmeeting.mainview.previous,
			         handler: function() {
			            //返回上一步
			         	ReviewMeetingSetting.jumpTabByIndex(1,"titleclick");
			         }
				},/*{
				 text: zc.editmeeting.mainview.next,
		         handler:function(){
		         	var flag = ReviewMeetingSetting.saveExpertInfo();//保存评审人员设置
		         	//创建申报人分组页面
		         	if(flag){
						ReviewMeetingSetting.jumpTabByIndex(3);
		         	}
		         }
			},*/{
		         text:zc.editmeeting.mainview.complete,
		         handler:function(){
		         	var flag = ReviewMeetingSetting.saveExpertInfo();
	         		if(flag){//保存时没报错，则关闭页面
	         			ReviewMeetingSetting.destroy();
	         		}
		         }
	        }]
		});
	//得到启用的评审阶段
	var segments = ReviewMeetingSetting.meetingRecord.segments;
	 /*评委会或二级单位store*/
	var committeStore = undefined;
	for(var i=0; i<segments.length;i++){
		var index = i+1;
		var segment = segments[i];
		var imgsrc = "../../../module/jobtitle/images/reviewmeeting/index.png";
		var fieldsetTitle = zc.editmeeting.mainview.choose;
		var container = "";
		if("1" == segment
			||"4" ==segment){//评委会、二级单位
			if(1==segment){
				fieldsetTitle += zc.label.inReview;
			}else{
				fieldsetTitle += zc.label.inOther;
			}
			//加载二级单位和评委会
			if(!committeStore){
				 committeStore = Ext.create("Ext.data.Store",{
					id:'committeStore',
					fields:['committee_id','committee_name'],
					proxy:{
						type: 'transaction',
				        functionId : 'ZC00002313',
				        extraParams:{
				        	opt:'4'
				        },
					 	reader: {
						  type : 'json',
						  totalProperty:'totalCount',
						  root : 'datas'         	
						}
					},
					autoLoad:true
				});
			}
			container = Ext.create("Ext.container.Container",{
					bodyStyle:'background:#FFFFFF',
					layout:'border',
					height:160,
					items:[
					{
						xtype:'container',
						width:22,
						margin:'10 15 0 0',
						region:'west',
						style:{
							backgroundImage:"url('"+imgsrc+"')",
							backgroundRepeat:"no-repeat",
							position:'relative'
						},
						items:[{
						xtype:'label',
						style:'color:#FFFFFF;left: 6px;font-size: 16px;position: absolute',
						text:index
						}]
					},{
						xtype:'fieldset',
						title:fieldsetTitle,
						padding:'10 0 0 10',
						region:'center',
						layout:'hbox',
						items:[{
							xtype:'container',
							layout:'vbox',
							items:[{
									xtype:'radiofield',
									id:'usertype_'+segment+"_r1",
	        						name:'usertype_'+segment,
	        						readOnly:this.noCanEdit,
	        						width:120,
	        						inputValue:'1',
	        						checked:true,
	        						boxLabel:zc.editmeeting.mainview.assessor.randomuser,
	        						listeners:{
	        							change:function(radio,nVal){
	        								var  id = radio.name;
	        								if(!nVal){
	        									Ext.getCmp(id+"_input").setDisabled(true);
	        									Ext.getCmp(id+"_combox").setDisabled(false);
	        								}else{
	        									Ext.getCmp(id+"_input").setDisabled(false);
	        									Ext.getCmp(id+"_combox").setDisabled(true);
	        								}
	        							},
	        							afterrender:function(radio){
	        								var  id = radio.name;
	        								if(radio.checked){
	        									Ext.getCmp(id+"_input").setDisabled(false);
	        									Ext.getCmp(id+"_combox").setDisabled(true);
	        								}else{
	        									Ext.getCmp(id+"_input").setDisabled(true);
	        									Ext.getCmp(id+"_combox").setDisabled(false);
	        								}
	        							}
	        						}
								},{
									xtype:'radiofield',
									width:120,
									id:'usertype_'+segment+"_r2",
									readOnly:this.noCanEdit,
	        						name:'usertype_'+segment,
	        						inputValue:'2',
	        						boxLabel:zc.editmeeting.mainview.choose+zc.label.inReview
							}]
						},{
							xtype:'container',
							layout:'vbox',
							items:[
								{
									xtype:'container',
									layout:'hbox',
									height:30,
									items:[{
									xtype:'textfield',
									height:22,
									readOnly:this.noCanEdit,
									regex:/^([1-9][0-9]?|100)$/,
									regexText:zc.editmeeting.mainview.assessor.numberformmsg,
									id:'usertype_'+segment+"_input",
									name:'usertype_'+segment+"_1",
									margin:'0 5 0 0',
									size:6,
									listeners:{
										afterrender:function(textf){
											if(Ext.isIE9 || Ext.isIE8){
												var inputEl = document.getElementById("usertype_"+segment+"_input-inputEl");
												if(inputEl)
													inputEl.style.height='90%';
											}
										}
									 }
									},{
										xtype:'label',
										text:zc.label.person
									}]
									
								},{
									xtype:'combobox',//comb  选择评委会、二级单位
									size:30,
									id:'usertype_'+segment+"_combox",
									readOnly:this.noCanEdit,
									name:'usertype_'+segment+"_2",
									store:committeStore,
									editable:false,
									displayField:'committee_name',
									valueField:'committee_id'
								}
							]
						}]
					}]
			})
		}else if("2" == segment){//专业组
			fieldsetTitle += zc.label.inExpert;
			container = Ext.create("Ext.container.Container",{
					bodyStyle:'background:#FFFFFF',
					layout:'border',
					height:180,
					items:[
					{
						xtype:'container',
						width:22,
						margin:'10 15 0 0',
						region:'west',
						style:{
							backgroundImage:"url('"+imgsrc+"');background-repeat:no-repeat;"					
						},
						items:[{
						xtype:'label',
						style:'color:#FFFFFF;left: 6px;font-size: 16px;position: absolute',
						text:index
						}]
					},{
						xtype:'fieldset',
						region:'center',
						title:fieldsetTitle,
						padding:'10 0 0 10',
						layout:'hbox',
						items:[{
							xtype:'container',
							layout:'vbox',
							items:[{
								xtype:'radiofield',
								width:120,
								id:'usertype_'+segment+"_r1",
								readOnly:this.noCanEdit,
        						name:'usertype_'+segment,
        						inputValue:'1',
        						checked:true,
        						boxLabel:zc.editmeeting.mainview.assessor.randomuser,
        						listeners:{
	        							change:function(radio,nVal){
	        								if(nVal){
	        									Ext.getCmp("chooseSubjects").setDisabled(true);
	        								}else{
	        									Ext.getCmp("chooseSubjects").setDisabled(false);
	        								}
	        							},
	        							afterrender:function(radio){
	        								if(radio.checked){
	        									Ext.getCmp("chooseSubjects").setDisabled(true);
	        								}else{
	        									Ext.getCmp("chooseSubjects").setDisabled(false);
	        								}
	        							}
        						}
							},{
								xtype:'radiofield',
								id:'usertype_'+segment+"_r2",
        						name:'usertype_'+segment,
        						width:120,
        						readOnly:this.noCanEdit,
        						inputValue:'2',
        						boxLabel:fieldsetTitle
							}]
						},{
							xtype:'container',
							layout:'vbox',
							items:[
								{
									xtype:'container',
									height:35
								},{
									id:'subjectIds',
									name:'subjectIds',
									xtype:'hiddenfield'
								},{
									xtype:'container',
									id:'chooseSubjects',
									width:500,
									height:90,
									style :'border:1px solid #c5c5c5;',
									items:[{
										id:'subjectNameText',
										xtype:'container',
										style:'color:#0000FF;line-height:23px;'
									},{
										xtype:'image',
										width:50,
										style:'cursor:pointer;',
										height:50,
										alt:fieldsetTitle,
										src:'/images/new_module/nocycleadd.png',
										listeners:{
		        							click: {
									            element: 'el',
									            fn: function(){
									            	if(!ReviewMeetingSetting.readOnly){
										            	var w0301 = ReviewMeetingSetting.meetingRecord.w0301
										           	 	ReviewMeetingSetting.openSubjectsPage(w0301);
									            	}
									            }
									        }
		        						}
									}]
								}
							]
						}]
					}]
			})
		}else if("3" == segment){//同行专家
			fieldsetTitle += zc.label.exExpert;
			container = Ext.create("Ext.container.Container",{
					bodyStyle:'background:#FFFFFF',
					layout:'border',
					height:160,
					items:[
					{
						xtype:'container',
						width:22,
						margin:'10 15 0 0',
						region:'west',
						style:{
							backgroundImage:"url('"+imgsrc+"');background-repeat:no-repeat;"				
						},
						items:[{
						xtype:'label',
						style:'color:#FFFFFF;left: 6px;font-size: 16px;position: absolute',
						text:index
						}]
					},{
						xtype:'fieldset',
						region:'center',
						layout:'hbox',
						title:fieldsetTitle,
						padding:'10 0 0 10',
						items:[{
							xtype:'radiofield',
							readOnly:this.noCanEdit,
							id:'usertype_'+segment+"_r1",
    						name:'usertype_'+segment,
    						width:120,
    						inputValue:'1',
    						checked:true,
    						boxLabel:zc.editmeeting.mainview.assessor.randomuser
						},{
							xtype:'container',
							height:25,
							layout:'hbox',
							items:[{
							xtype:'textfield',
							height:22,
							readOnly:this.noCanEdit,
							regex:/^([1-9][0-9]?|100)$/,
							id:'usertype_'+segment+"_input",
							regexText:zc.editmeeting.mainview.assessor.numberformmsg,
							name:'usertype_'+segment+"_1",
							margin:'0 5 0 0',
							size:6,
							listeners:{
								afterrender:function(textf){
									if(Ext.isIE9 || Ext.isIE8){
										var inputEl = document.getElementById("usertype_3_input-inputEl");
										if(inputEl)
											inputEl.style.height='90%';
									}
								}
							 }
							},{
								xtype:'label',
								text:zc.label.person
							}]
						}]
					}]
			})
		}
		tab_2.add(container);
	}
	return tab_2;
	},
	/**
	 * 创建申报人分组页面
	 * @param {} index
	 * @param {} w0301
	 */
	createReviewConsole:function(index,w0301){
		
		var record = ReviewMeetingSetting.meetingRecord;
		//已配置的评审环节
		var segments = record.segments;
		
		if(Ext.isEmpty(segments)){
			return;
		}
		//index为页面上的tab标题序号，-3是要从已配置的评审环节（record.segments）找到对应的环节
		var curSegment = segments[index-3];
		/*var tab = Ext.getCmp('tab_'+index);
		if(tab){
			*//**
			 * ReviewConsole 页面使用了jobtitle_reviewconsole 作为全局变量存储自身的作用域，但是reviewConsole 会多次创建，
			 * 导致jobtitle_reviewconsole的作用域始终指向的是最后一次创建的ReviewConsole对象，
			 * 所以在此掉用之前创建的页面时，需要吧jobtitle_reviewconsole的对象指向调用的页面
			 *//*
			jobtitle_reviewconsole = ReviewMeetingSetting.reviewConsoleMap["reviewConsole_"+index];
			return tab;
		}*/
		var reviewConsole = Ext.create("ReviewMeetingURL.ReviewConsole",{
			w0301_e:w0301,
			userType:record['usertype_'+curSegment],
			evaluationType:record['evaluationType_'+curSegment],
			review_links:curSegment,
			enterType:'1'//创建评审条件入口
		})
		ReviewMeetingSetting.reviewConsoleMap["reviewConsole_"+index]=reviewConsole;
		return  Ext.create("Ext.form.Panel",{
				width:'100%',
				id:'tab_'+index,
				layout:'fit',
				region:'center',
				border:true,
				margin:'0 0 10 0',
				padding:'0 0 0 5',
				autoScroll : true,
				buttonAlign:'center',
				items:[reviewConsole.getTableConfig()],
				fbar:[{
						 text: zc.editmeeting.mainview.previous,
				         handler: function() {
//					            //返回上一步
//				         	reviewConsole.saveInfo();
				         	ReviewMeetingSetting.jumpTabByIndex(ReviewMeetingSetting.titleInfoMap.checkedIndex-1);
				         }
					},{
					 text: zc.editmeeting.mainview.next,
					 hidden:index==segments.length+2,
			         handler:function(){
			         	ReviewMeetingSetting.jumpTabByIndex(index+1,w0301);
			         }
				},{
			         text:(index==segments.length+2)?zc.editmeeting.mainview.complete:zc.editmeeting.mainview.cancle,
			         handler:function(){
			         	if(ReviewMeetingSetting){
			         		ReviewMeetingSetting.destroy();
			         		meetingStore = Ext.data.StoreManager.lookup("meetingStore");
			         		if(meetingStore){
			         			meetingStore.load({
								params:{
									scheme:ReviewMeetingPortal.schemeArray.join(",")
								}})
			         		}
		         		}
			         }
		        }]
			});
	},
	/**
	 * 
	 * @param {} w0301  id
	 * @param {} selectGroupId  选中学科组id 可选
	 */
	openSubjectsPage:function(w0301,selectGroupId,type){
		
		var readonly = '0';
		if(this.noCanEdit){
			readonly = '1';
		}
		Ext.require('JobtitleSubjects.SubjectsForMeeting', function(){
			RevewFileGlobal = Ext.create("JobtitleSubjects.SubjectsForMeeting",
			{
				w0301:w0301,
				readonly:readonly,
				selectGroupId:selectGroupId,
				type:type,
				returnBackFunc:ReviewMeetingSetting.showSubjectsInfo
			});
		});
	},
	showSubjectsInfo:function(subjectlist,singleEdit){
		//编辑单个学科组
		if(singleEdit){
			var subject = subjectlist[0];
			var subjectId = subject.group_id;
			var subjectSpan = Ext.getDom("subject_"+subjectId);
			if(subjectSpan){
				if(subject.pnumber=="0" || !subject.pnumber)
					Ext.removeNode(subjectSpan.parentNode);
				else
					subjectSpan.innerHTML = subject.group_name+"("+subject.pnumber+" "+zc.label.person+")";
			}
			return;
		}
		var w0301 = ReviewMeetingSetting.meetingRecord.w0301;
		var subjectsContainer = Ext.getCmp("subjectNameText");
		var html = "";
		var subjectIds = "";
		for(var i=0;i<subjectlist.length;i++){
			var subject = subjectlist[i];
			html+="<div style='cursor:pointer;display:inline-block;' title='"+zc.editmeeting.mainview.editgroupexperts+"' onclick='ReviewMeetingSetting.openSubjectsPage(\""+w0301+"\",\""+subject.group_id+"\",\"vote\")'>";
			html+="<span  id='subject_"+subject.group_id+"'>"+subject.group_name+"("+subject.pnumber+" "+zc.label.person+")</span>";
			if(i<subjectlist.length-1)
				html+="、";
			html+="</div>";
			subjectIds+=subject.group_id+",";
		}
		if(subjectIds.length>0)
			subjectIds = subjectIds.substring(0,subjectIds.length-1);
		Ext.getCmp("subjectIds").setValue(subjectIds);
		subjectsContainer.setHtml(html);
	},
	/**
	 * curIndex  
	 * fromflag
	 * 	titleclick、tab的标题点击
	 *  btnclick、页面的上一步、下一步按钮
	 * 	将要跳转的tab的角标
	 * 通过tab标题的index，实现类似Tabset的跳转效果
	 */
	jumpTabByIndex:function(curIndex,fromflag){
		var w0301 = Ext.isEmpty(this.w0301)?ReviewMeetingSetting.meetingRecord.w0301:this.w0301;
			//为创建评审会议时，不允许通过 tab标题点击
		if(fromflag=="titleclick"){
			/**
			 * 点击标题在评审会议编辑和评审人员设置页面切换时，需要同步保存一下
			 */
			var tab1 = Ext.getCmp("tab_1");
			var tab2 = Ext.getCmp("tab_2");//评审成员界面
			if(tab1 && !tab1.isHidden() && !this.noCanEdit){
				//如果是评审会议编辑界面跳转道评审成员界面需要保存数据
				var flag = meetingMain.next(tab1.getForm(),false,"titleclick");
				if(!flag)//为false 时证明有错不能往下走
					return;
			}else if(tab2 && !this.noCanEdit){
				//如果是评审会议编辑界面跳转道评审成员界面需要保存数据
				this.saveExpertInfo(true);
			}
		}
		var checkeIndex = this.titleInfoMap.checkedIndex;
		if(checkeIndex>-1){
			var tab = Ext.getCmp("tab_"+checkeIndex)
			if(checkeIndex<2){
				//评审会议编辑页面
				tab.setHidden(true);
			}else{
				//评审人员设置页面和申报人分组设置需要每次都创建
				tab.destroy();
			}
		}
		
		//为tab标题添加选中效果
		this.clickTitleFunc(curIndex);
		var compent = "";
		
		if(curIndex==1){//维护会议主题页面
			compent = this.getCenterPanel(this.w0301);
		}else if(curIndex==2){//设置评审成员界面
			compent = this.createPersonSettingPage(curIndex);
		}/*else if(curIndex>2){//申报人分组界面
			compent = this.createReviewConsole(curIndex,w0301);
		}*/
		//评审会议编辑页面不需要每次创建
		if(curIndex==1 && compent.isHidden()){
			compent.setHidden(false);
		}else{//评审人员设置页面和申报人分组设置需要每次都创建
			this.add(compent);
		}
	},
	/**
	 * 下一步
	 * noConfirm 是否显示提示信息
	 * 保存评审人员设置
	 */
	saveExpertInfo:function(noConfirm){
		if(this.noCanEdit){
			return true;
		}
		var form = Ext.getCmp("tab_2").getForm();
		if(!form.isValid())
			return false;
		var values = form.getFieldValues();
		var record = ReviewMeetingSetting.meetingRecord;
		if(record){
			var segments = record.segments;
			for(var i=0 ; segments && i<segments.length;i++){
				var segment = segments[i];
				switch(segment){
					case '1':
					case '4':
						var temp = zc.label.inReview+zc.editmeeting.mainview.segmentsps;
						if(segment=='4')
							temp = zc.label.inOther+zc.editmeeting.mainview.segmentsps;
						var usertype = values["usertype_"+segment];
						if(usertype){
							if(usertype=="1" && Ext.isEmpty(values["usertype_"+segment+"_1"]) && !noConfirm){
								Ext.showAlert(temp+zc.editmeeting.mainview.assessor.numbernotallowblank);
								return false;
							}
							if(usertype=="2" && Ext.isEmpty(values["usertype_"+segment+"_2"]) && !noConfirm){
								Ext.showAlert(zc.editmeeting.mainview.tochoose+temp+"！");
								return false;
							}
						}
						break;
					case '2':
						//专业组 zc.label.inExpert+;
						var usertype = values["usertype_2"];
						if(usertype){
							if(usertype=="2" && Ext.isEmpty(Ext.getCmp("subjectIds").value) && !noConfirm){
								Ext.showAlert(zc.editmeeting.mainview.tochoose+ zc.label.inExpert+"！");
								return false;
							}
						}
						break;
					case '3':
						//同行
						var usertype = values["usertype_3"];
						if(usertype){
							if(Ext.isEmpty(values["usertype_3_1"]) && !noConfirm){
								Ext.showAlert(zc.label.exExpert+zc.editmeeting.mainview.assessor.numbernotallowblank);
								return false;
							}
						}
						break;
					
				}
			}
		}
		
		//提交数据
		var map = new HashMap();
		var record = ReviewMeetingSetting.meetingRecord;
		var w0301 = record.w0301;
		map.put("opt","5");//保存评审人员设置
		map.put("w0301",w0301)//保存评审人员设置
		map.put("values",values);
		Rpc({functionId:'ZC00002313',async:false,success:function(res){
	 		var result = Ext.decode(res.responseText);
	 		if(!result.succeed && !noConfirm){
	 			Ext.showAlert(result.message);
	 			return false;
 			}
 			ReviewMeetingSetting.meetingRecord = result.meetingData;
	 	},scope:this},map);
	 	
	 	return true;
	},
	/**
	 * 获得评审人员设置，用于回显页面
	 */
	getExpertSetingData:function(){
		var expertSetting  = "";
		var record = ReviewMeetingSetting.meetingRecord;
		var w0301 = record.w0301;
		var map = new HashMap();
		map.put("opt","6");
		map.put("w0301",w0301);
		Rpc({functionId:'ZC00002313',async:false,success:function(res){
	 		var result = Ext.decode(res.responseText);
	 		if(!result.succeed){
	 			Ext.showAlert(result.message);
	 			return;
 			}
			expertSetting = result.expertSetting;
	 	},scope:this},map);
	 	return expertSetting;
	},
	/**
	 * 根据评审阶段的状态判断是否可以修改
	 */
	isCanEditSegmentsData:function(){
		var record = ReviewMeetingSetting.meetingRecord;
		
		var segments  = meetingMain.meetingRecord.segments;
		if(!segments)//创建会议是不校验，重新编辑会议才校验
			return;
		for(var i in segments){
			var segIndex = segments[i];
			var curState = record['segmentStatus_'+segIndex];//评审阶段的状态
			if(curState=="2"){
				Ext.getCmp("usertype_"+segIndex+"_r1").setReadOnly(true);
				if(segIndex=="1" || segIndex=="4"){//评委会、二级单位
					Ext.getCmp("usertype_"+segIndex+"_r2").setReadOnly(true);
					Ext.getCmp('usertype_'+segIndex+"_input").setReadOnly(true);
					Ext.getCmp('usertype_'+segIndex+"_combox").setReadOnly(true);
				}else if(segIndex=="2"){
					Ext.getCmp("usertype_"+segIndex+"_r2").setReadOnly(true);
					Ext.getCmp("chooseSubjects").setDisabled(true);
				}else if(segIndex=="3"){
					Ext.getCmp('usertype_'+segIndex+"_input").setReadOnly(true);
				}
			}
			else if(curState=="1"){
				if(segIndex=="1" || segIndex=="4"){//评委会、二级单位
					var checked = Ext.getCmp("usertype_"+segIndex+"_r1").getValue();
					Ext.getCmp("usertype_"+segIndex+"_r1").setReadOnly(true);
					Ext.getCmp("usertype_"+segIndex+"_r2").setReadOnly(true);
					if(checked){
						Ext.getCmp('usertype_'+segIndex+"_combox").setReadOnly(true);
					}else{
						Ext.getCmp('usertype_'+segIndex+"_input").setReadOnly(true);
					}
				}else if(segIndex=="2"){
					var checked = Ext.getCmp("usertype_"+segIndex+"_r1").getValue();
					Ext.getCmp("usertype_"+segIndex+"_r1").setReadOnly(true);
					Ext.getCmp("usertype_"+segIndex+"_r2").setReadOnly(true);
					if(checked){
						Ext.getCmp("chooseSubjects").setDisabled(true);
					}
				}
			}
		}
	}
});