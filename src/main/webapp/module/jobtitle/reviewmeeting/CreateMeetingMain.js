/**
 * 创建|编辑评审会议，评审人员设置页面
 * haosl
 * 2018-4-13
 */
Ext.define("ReviewMeetingURL.CreateMeetingMain",{
	w0301:"",
	readOnly:false,
	w0321:'',
	requires:['EHR.extWidget.field.DateTimeField','EHR.extWidget.field.CodeTreeCombox'],
	constructor:function(config) {
		meetingMain = this;
		//初始化参数设置中勾选的测评表
		this.assessmentTables = config.assessmentTables;
		this.w0301 = config.w0301;
		this.readOnly = config.readOnly;
		this.w0321 = config.w0321;
		//是否可编辑  noCanEdit =true 不可编辑   noCanEdit=false 可编辑
		this.noCanEdit = this.readOnly || this.w0321=="05"||this.w0321=='06';//进行中和结束的会议同样不可以编辑
		//会议表单数据
		this.meetingRecord = config.meetingRecord;

		if(!Ext.util.CSS.getRule('.noBorder2 .x-form-trigger-default')){
			Ext.util.CSS.createStyleSheet(".noBorder2 .x-form-trigger-default{background-image: url('/module/jobtitle/images/reviewmeeting/down.png') !important;" +
				"background-position:0px 3px !important;" +
				"border-left:0px;border-top:0px;border-right:0px;border-color:#c5c5c5;" +
				"width:16px;height:15px;}","aaa");
		}
		if(!Ext.util.CSS.getRule('.noBorder2 .x-form-trigger-over')){
			Ext.util.CSS.createStyleSheet(".noBorder2 .x-form-trigger-over{background-image: url('/module/jobtitle/images/reviewmeeting/down.png') !important;" +
				"background-position:0px 3px !important;" +
				"border-left:0px;border-top:0px;border-right:0px;border-color:#c5c5c5;" +
				"width:16px;height:15px;}","bbb");
		}
		if(!Ext.util.CSS.getRule('.noBorder2 .x-form-trigger-focus')){
			Ext.util.CSS.createStyleSheet(".noBorder2 .x-form-trigger-focus{background-image: url('/module/jobtitle/images/reviewmeeting/down.png') !important;" +
				"background-position:0px 3px !important;" +
				"border-left:0px;border-top:0px;border-right:0px;border-color:#c5c5c5;" +
				"width:16px;height:15px;}","ccc");
		}
		if(!Ext.util.CSS.getRule('.noBorder2 .x-form-text-wrap-default')){
			Ext.util.CSS.createStyleSheet(".noBorder2 .x-form-text-wrap-default{border-top:0px;border-left:0px;border-right:0px;border-color:#c5c5c5;}","card_css");
		}
		if(!Ext.util.CSS.getRule('.noBorder2 .x-form-text-wrap-focus')){
			Ext.util.CSS.createStyleSheet(".noBorder2 .x-form-text-wrap-focus{border-top:0px;border-left:0px;border-right:0px; border-color:#c5c5c5;}","card_css");
		}
	},
	/**
	 * 会议维护界面
	 */
	getMainView:function(){
		var me = this;
		Ext.util.CSS.createStyleSheet(".x-form-cb-label{margin-top:3px !important;}","zz");
		var tab_1 = Ext.getCmp("tab_1");
		if(tab_1){
			return tab_1;
		}
		var voteDefaultStore = Ext.create("Ext.data.Store",{
			fields:['value','disName'],
			data:[
				{'value':'0','disName':"　"},
				{'value':'1','disName':"       "+zc.label.agree},
				{'value':'2','disName':"       "+zc.label.disagree},
				{'value':'3','disName':"       "+zc.label.giveup}]
		});

		return Ext.create("Ext.form.Panel",{
		width:'100%',
		id:'tab_1',
		layout:'vbox',
		region:'center',
		border:true,
		margin:'0 0 10 0',
		autoScroll : true,
		trackResetOnLoad:true,
		buttonAlign:'center',
		listeners:{
			afterrender:function(panel){
				//组件渲染成功后回显数据
				if(meetingMain.meetingRecord){
					panel.getForm().loadRecord(new Ext.data.Model(meetingMain.meetingRecord));
					//回显测评表
					var segments = meetingMain.meetingRecord.segments;
					for(var i in segments){
						var segment = segments[i];//环节号
						if(segment!="3"){
							var templateName = meetingMain.meetingRecord["evaluationType_"+segment+"_templateName"];
							if(!Ext.isEmpty(templateName)){
								var target = Ext.getCmp("evaluationType_"+segment+"_2_container");
								if(target)
									target.setHtml(templateName);
							}
						}
					}
				}
			}
		},
		fbar:[{
			 text: zc.editmeeting.mainview.next,
			 id:'nextbtn',
	         handler: function() {
		            //校验表单是否修改
	         	var form = this.up("form").getForm();
	         	//保存评审会议有错时，不往下走
	         	if(me.next(form)){
		         	//创建评审人设置页面
					ReviewMeetingSetting.jumpTabByIndex(2);
	         	}
	         }
			},
	        {
	         text:zc.editmeeting.mainview.cancle,
	          id:'canclebtn',
	         handler:function(){
	         	if(ReviewMeetingSetting){
	         		ReviewMeetingSetting.destroy();
	         		//只读时，不需要刷新列表页面
	         		if(!ReviewMeetingSetting.noCanEdit){
		         		var meetingStore = Ext.data.StoreManager.lookup("meetingStore");
		         		if(meetingStore){
		         			meetingStore.load({
							params:{
								scheme:ReviewMeetingPortal.schemeArray.join(",")
							}})
		         		}
	         		}
         		}
	         }
	        }],
		defaults:{//默认配置
			xtype:'textfield',
			labelAlign:'right',
			margin:"20 0 0 0",
			labelPad:20,
			readOnly:this.noCanEdit,
			labelWidth:120
		},
		padding:'0 0 0 5',
		items:[{
	        fieldLabel: "<font style='color:#FF0000;'>*&nbsp;&nbsp;</font>"+zc.editmeeting.mainview.name,
	        name: 'w0303',
	        size:80,//输入框长度
	        maxLength:50,
	        emptyText:zc.editmeeting.mainview.emptytext+zc.editmeeting.mainview.name,
	        allowBlank: false,
	        invalidText:'ccc',
	        listeners:{
	        	blur:function(textf){
		        	//校验不能为空格
	        		var value = textf.getValue();
		        	value = value.replace(/^\s+|\s+$/g, '');
		        	if(value.length==0)
		        		textf.setValue("");

	        }

	        }
		},{
			xtype:'textareafield',
			size:100,//输入框长度
			fieldLabel: zc.editmeeting.mainview.desc,
	        emptyText:zc.editmeeting.mainview.emptytext+zc.editmeeting.mainview.desc,
			name: 'w0305',
	        grow:true,
	        growMin:150,
	        growMax:220
		},{
        	xtype:'datetimefield',
        	name:'w0309',
        	fieldLabel:"<font style='color:#FF0000;'>*&nbsp;&nbsp;</font>"+zc.editmeeting.mainview.stratd,
        	format: 'Y-m-d',
        	emptyText:zc.editmeeting.mainview.emptytext+zc.editmeeting.mainview.stratd,
        	allowBlank :false
	    },{
        	xtype:'datetimefield',
        	name:'w0311',
        	fieldLabel:"<font style='color:#FF0000;'>*&nbsp;&nbsp;</font>"+zc.editmeeting.mainview.endd,
        	format: 'Y-m-d',
        	emptyText:zc.editmeeting.mainview.emptytext+zc.editmeeting.mainview.endd,
        	allowBlank :false
        },{
        	xtype:'container',
        	border:false,
        	height:150,
        	defaultType:'container',
        	layout:'hbox',
        	items:[{
        		width:120,
        		margin:'0 20 0 0',
        		height:'100%',
				style:'text-align:right',
				padding:'50 0 0 0',
        		html: "<font style='color:#FF0000;'>*&nbsp;&nbsp;</font>"+zc.editmeeting.mainview.segssetting
        	},{
        		xtype:'container',
        		height:'100%',
        		layout:{
        			type:'vbox',
        			pack:'left'
        		},
        		defaults:{
        			xtype:'container',
        			margin:'6 0 0 0',
        			layout:'hbox'
    			},
        		items:[
        			{
        				//同行专家
        				items:[{
        					id:'exExpert_cbox',
        					xtype:'checkboxfield',
        					width:110,
        					readOnly:this.noCanEdit,
	        				name:'segments',
	        				inputValue:'3',
	        				boxLabel:zc.label.exExpert,
	        				listeners:{
	        					click:{
		        					element:'el',
		        					fn:function(e){
		        						if(e.target.tagName=="INPUT" || e.target.tagName=="SPAN"){
		        							meetingMain.segmentBoxClick('exExpert_cbox','3');
		        						}
		        					}
	        					}
	        				}
        				}]

        			},{
        				id:'subcommitee',
        				//二级单位
        				items:[{
        					xtype:'checkboxfield',
        					width:110,
        					id:'subcommitee_cbox',
        					readOnly:this.noCanEdit,
	        				name:'segments',
	        				inputValue:'4',
	        				boxLabel:zc.label.inOther+zc.editmeeting.mainview.segmentsps,
	        				listeners:{
	        					change : me.segmentsChecked,
	        					click:{
		        					element:'el',
		        					fn:function(e){
		        						if(e.target.tagName=="INPUT" || e.target.tagName=="SPAN"){
		        							meetingMain.segmentBoxClick('subcommitee_cbox','4');
		        						}
		        					}
	        					}
	        				}
        				},{
        					xtype:'container',
        					layout:'hbox',
        					id:'evaluationType_4',
        					items:[
        					{
        						xtype:'radiofield',
								width:45,
        						margin:'0 0 0 10',
        						id:'evaluationType_4_1',
        						name:'evaluationType_4',
        						readOnly:this.noCanEdit,
        						checked:true,
        						inputValue:'1',
        						boxLabel:zc.editmeeting.mainview.vote
        					},{
									xtype:'container',
									items:[{
										xtype:'combo',
										fieldLabel: '',
										name:'voteDefault_4',
										id:"evaluationType_4_voteDefault",
										labelSeparator:'',
										width:85,
										valueField: 'value',
										store:voteDefaultStore,
										value:me.meetingRecord['voteDefault_4'],
										emptyText:'可选默认值',
										displayField: 'disName',
										editable:false,
										cls:'noBorder2'
									}]

							},{
        						xtype:'radiofield',
        						margin:'0 10 0 10',
								width:45,
        						name:'evaluationType_4',
        						id:'evaluationType_4_2',
        						readOnly:this.noCanEdit,
        						inputValue:'2',
        						boxLabel:zc.editmeeting.mainview.score,
        						listeners:{
	        						change : me.evaluationTypeChange,
	        						afterrender:me.evaluationTypeChange
	        					}
        					},{

        						xtype:'container',
        						id:'evaluationType_4_assessmentText',
        						layout:'hbox',
        						items:[{xtype:'label',margin:'3 0 0 0',text:"（"},{
        						xtype:'label',
        						margin:'3 0 0 0',
        						style:'font-weight:bold;color:#1B4A98;cursor:pointer;',
        						title:zc.label.singleclick+zc.editmeeting.mainview.choose+zc.label.assessmentTablePn,
        						text:zc.label.assessmentTablePn+zc.editmeeting.mainview.choose+"：",
        						listeners:{
        							click: {
							            element: 'el',
							            fn: function(){
							            	//只读时不能设置测评表
						           	 		meetingMain.assessmentTablePn("evaluationType_4_2_container",'subcommite_tablesid','4');
							            }
							        }
        						}
        					},{
        						id:'subcommite_tablesid',
						        name : 'evaluationType_4_template',//存放二级单位测评表id
						        xtype : 'hiddenfield'
        					},{
        						//用于显示测评表
        						xtype:'container',
        						margin:'3 0 0 0',
        						id:'evaluationType_4_2_container'
        					},{xtype:'label',margin:'3 0 0 0',text:"）"}]
        					}]
        				}]
        			},{
        				id:'subject',
        				//专业组
        				items:[{
        					xtype:'checkboxfield',
        					width:110,
        					id:'subject_cbox',
        					readOnly:this.noCanEdit,
	        				name:'segments',
	        				inputValue:'2',
	        				boxLabel:zc.label.inExpert+zc.editmeeting.mainview.segmentsps,
	        				listeners:{
	        					change : me.segmentsChecked,
	        					click:{
		        					element:'el',
		        					fn:function(e){
		        						if(e.target.tagName=="INPUT" || e.target.tagName=="SPAN"){
		        							meetingMain.segmentBoxClick('subject_cbox','2');
		        						}
		        					}
	        					}
	        				}
        				},{
        					xtype:'container',
        					id:'evaluationType_2',
        					layout:'hbox',
        					items:[
        					{
        						xtype:'radiofield',
        						margin:'0 0 0 10',
								width:45,
        						readOnly:this.noCanEdit,
        						name:'evaluationType_2',
        						checked:true,
        						id:'evaluationType_2_1',
        						inputValue:'1',
        						boxLabel:zc.editmeeting.mainview.vote
        					},{
									xtype:'container',
									items:[{
										xtype:'combo',
										fieldLabel: '',
										name:'voteDefault_2',
										id:"evaluationType_2_voteDefault",
										labelSeparator:'',
										width:85,
										valueField: 'value',
										store:voteDefaultStore,
										emptyText:'可选默认值',
										displayField: 'disName',
										editable:false,
										value:me.meetingRecord['voteDefault_2'],
										cls:'noBorder2'
									}]

							},{
        						xtype:'radiofield',
								width:45,
        						name:'evaluationType_2',
        						readOnly:this.noCanEdit,
        						id:'evaluationType_2_2',
        						inputValue:'2',
        						margin:'0 10 0 10',
        						boxLabel:zc.editmeeting.mainview.score,
        						listeners:{
	        						change : me.evaluationTypeChange,
	        						afterrender:me.evaluationTypeChange
	        					}
        					},{

        						xtype:'container',
        						id:'evaluationType_2_assessmentText',
        						layout:'hbox',
        						items:[{xtype:'label',margin:'3 0 0 0',text:"（"},{
        						xtype:'label',
        						margin:'3 0 0 0',
        						style:'font-weight:bold;color:#1B4A98;cursor:pointer;',
        						title:zc.label.singleclick+zc.editmeeting.mainview.choose+zc.label.assessmentTablePn,
        						text:zc.label.assessmentTablePn+zc.editmeeting.mainview.choose+"：",
        						listeners:{
        							click: {
							            element: 'el',
							            fn: function(){
						            	 	meetingMain.assessmentTablePn("evaluationType_2_2_container","subjects_tablesid",'2');
							            }
							        }
        						}
        					},{
        						id:'subjects_tablesid',
						        name : 'evaluationType_2_template',//存放专业组测评表id
						        xtype : 'hiddenfield'
        					},{
        						//用于显示测评表
        						xtype:'container',
        						margin:'3 0 0 0',
        						id:'evaluationType_2_2_container'
        					},{xtype:'label',margin:'3 0 0 0',text:"）"}]
        					}]
        				}]
        			},{
        				id:'commitee',
        				//评委会
        				items:[{
        					xtype:'checkboxfield',
        					width:110,
	        				name:'segments',
	        				id:'commitee_cbox',
	        				inputValue:'1',
	        				readOnly:this.noCanEdit,
	        				boxLabel:zc.label.inReview+zc.editmeeting.mainview.segmentsps,
	        				listeners:{
	        					change : me.segmentsChecked,
	        					click:{
		        					element:'el',
		        					fn:function(e){
		        						if(e.target.tagName=="INPUT" || e.target.tagName=="SPAN"){
		        							meetingMain.segmentBoxClick('commitee_cbox','1');
		        						}
		        					}
	        					}
	        				}
        				},{
        					xtype:'container',
        					id:'evaluationType_1',
        					layout:'hbox',
        					items:[
        					{
        						xtype:'radiofield',
        						margin:'0 0 0 10',
								width:45,
        						id:'evaluationType_1_1',
        						checked:true,
        						readOnly:this.noCanEdit,
        						name:'evaluationType_1',
        						inputValue:'1',
        						boxLabel:zc.editmeeting.mainview.vote
        					},{
								xtype:'container',
								items:[{
									xtype:'combo',
									fieldLabel: '',
									labelSeparator:'',
									width:85,
									name:'voteDefault_1',
									valueField: 'value',
									id:"evaluationType_1_voteDefault",
									store:voteDefaultStore,
									emptyText:'可选默认值',
									displayField: 'disName',
									editable:false,
									value:me.meetingRecord['voteDefault_1'],
									cls:'noBorder2'
								}]

							},{
        						xtype:'radiofield',
        						name:'evaluationType_1',
								width:45,
        						readOnly:this.noCanEdit,
        						id:'evaluationType_1_2',
        						inputValue:'2',
        						margin:'0 10 0 10',
        						boxLabel:zc.editmeeting.mainview.score,
        						listeners:{
	        						change : me.evaluationTypeChange,
	        						afterrender:me.evaluationTypeChange
	        					}
        					},{xtype:'container',
        						id:'evaluationType_1_assessmentText',
        						layout:'hbox',
        						items:[{xtype:'label',margin:'3 0 0 0',text:"（"},{
        						xtype:'label',
        						margin:'3 0 0 0',
        						style:'font-weight:bold;color:#1B4A98;cursor:pointer;',
        						title:zc.label.singleclick+zc.editmeeting.mainview.choose+zc.label.assessmentTablePn,
        						text:zc.label.assessmentTablePn+zc.editmeeting.mainview.choose+"：",
        						listeners:{
        							click: {
							            element: 'el',
							             fn: function(){
						            		 meetingMain.assessmentTablePn("evaluationType_1_2_container","commitee_tablesid",'1');
							            }
							        }
        						}

        					},{
        						id:'commitee_tablesid',
						        name : 'evaluationType_1_template',//存放评委会测评表id
						        xtype : 'hiddenfield'
        					},{
        						//用于显示测评表
        						xtype:'container',
        						margin:'3 0 0 0',
        						id:'evaluationType_1_2_container'
        					},{xtype:'label',margin:'3 0 0 0',text:"）"}]}]

        				}]
        			}
        		]
        	}]
        },{
        	xtype:'codecomboxfield',
        	size:50,
        	margin:"20 0 5 0",
        	name:'b0110',
        	ctrltype:'3',
        	nmodule:'9',
        	onlySelectCodeset:false,
        	codesetid:"UM",
        	emptytext:zc.editmeeting.mainview.tochoose+zc.editmeeting.mainview.organization,
        	fieldLabel:"<font style='color:#FF0000;'>*&nbsp;&nbsp;</font>"+zc.editmeeting.mainview.organization,
        	allowBlank:false,
        	editable:false
        }]
		});
	},
	/**
	 * 评审环节复选框勾选取消加校验
	 */
	segmentBoxClick:function(cid,segIndex){
		if(this.noCanEdit)//不可编辑时直接return
			return;
		var temp = ['3','4','2','1'];
		var segments  = meetingMain.meetingRecord.segments;
		if(!segments)//创建会议是不校验，重新编辑会议才校验
			return;
		var cbox = Ext.getCmp(cid);
		//1、当前勾选环节为进行中或结束的不允许取消
		var curState = meetingMain.meetingRecord['segmentStatus_'+segIndex];
		if(curState=="1"||curState=="2"){
			cbox.setValue(true);
			if(!this.noCanEdit)//不可编辑时就不提示了
				Ext.showAlert(zc.editmeeting.mainview.error.cancleSegments);
			return;
		}
		//2、判断当前勾选阶段后面是否有参加过评审的环节，有则不可取消
		var index = Ext.Array.indexOf(temp,segIndex,0);
		if(index>-1){
			for(var i=index;i<temp.length;i++){
				var state = meetingMain.meetingRecord['segmentStatus_'+temp[i]];
				if(state=="1"||state=="2"){
					cbox.setValue(!cbox.getValue());
					Ext.showAlert(zc.editmeeting.mainview.error.addSegments);
					break;
				}
			}
		}

//		
	},
	/**
	 * 评审环节的选中取消事件处理
	 * @param {} cbox
	 * @param {} newVal
	 */
	segmentsChecked:function(cbox,newVal){
		setTimeout(function(){
			var boxid = cbox.id;
		if(boxid=='subcommitee_cbox'){
			//选中二级单位时，专业组和评委会则置为不可选中状态
			if(newVal){
				Ext.getCmp("subject_cbox").setValue(false);
				Ext.getCmp("commitee_cbox").setValue(false);
				Ext.getCmp("subject").setDisabled(true);
				Ext.getCmp("commitee").setDisabled(true);
			}else{
				Ext.getCmp("subject").setDisabled(false);
				Ext.getCmp("commitee").setDisabled(false);
			}
		}else if(boxid=='subject_cbox'||boxid=='commitee_cbox'){
			var otherBox = "";
			if(boxid=='subject_cbox'){
				otherBox = Ext.getCmp("commitee_cbox");
			}else{
				otherBox = Ext.getCmp("subject_cbox");
			}

			if(newVal){//当前选中
				Ext.getCmp("subcommitee_cbox").setValue(false);
				Ext.getCmp("subcommitee").setDisabled(true);
			}else{//当前未选中
				if(!otherBox.getValue())//判断 时候另一个处于选中状态
					Ext.getCmp("subcommitee").setDisabled(false);
			}

		}
		},20);

	},
	/**
	 * 投票评分的选中取消事件处理
	 * @param {} cbox
	 * @param {} newVal
	 */
	evaluationTypeChange : function(cbox){
		var perTemplates = Ext.getCmp(cbox.name+"_assessmentText");
		var	checked = cbox.checked;
		if(perTemplates){
			if(checked){
				//显示测评表
				perTemplates.setHidden(false);
			}else{
				//隐藏测评表
				perTemplates.setHidden(true);
			}
		}
		var voteDefault = Ext.getCmp(cbox.name+"_voteDefault");
		if(voteDefault){
			if(checked){
				//投票默认值不可选
				voteDefault.setReadOnly(true);
			}else{
				//投票默认值可选
				voteDefault.setReadOnly(false);
			}
		}

	},
	/**
	 * 调用测评表配置
	 *
	 * segment: 评审环节号
	 */
	assessmentTablePn:function(targetId,hiddenInputId,segment){
		if(Ext.isEmpty(meetingMain.assessmentTables)){
			Ext.showAlert(zc.editmeeting.mainview.error.noassessmenttables);
			return;
		}
		// 该会议，阶段已选模板表
		var selectTabids = Ext.getCmp(hiddenInputId).getValue();
		var config = {};
		config.selectTabids = selectTabids;
		config.value = meetingMain.assessmentTables;
		config.selectType="4";//展现已勾选的测评表
		config.saveButHiddenflag=meetingMain.noCanEdit;
		config.success = function(selectids, selectidTexts){
			var text = "";
			var tabids = "";
			if(selectidTexts.length>0){
				var selectidTextArr = selectidTexts.substring(0,selectidTexts.length-1).split(",");
				for(var i in selectidTextArr){
					var tabName =selectidTextArr[i].split("|")[1];
					tabName = tabName.substring(tabName.indexOf("]")+1);
					var id=selectidTextArr[i].split("|")[0];
					text+="【<font style='font-weight:bold;'>"+id+"</font>】"+tabName+"、";
					tabids+=id+",";
				}
				if(text.length>0){
					text = text.substring(0,text.length-1);
					tabids = tabids.substring(0,tabids.length-1);
				}
			}
			var oldIds = Ext.getCmp(hiddenInputId).getValue();
			if(oldIds!=tabids){
				var curState = meetingMain.meetingRecord['segmentStatus_'+segment];
				if(curState=="1" && tabids.length>0){//进行中的阶段修改测评表提示
					Ext.showConfirm(zc.editmeeting.mainview.error.editAssessmentTable,function(flag){
						if(flag=="yes"){
							var map = new HashMap();
				 			map.put("opt","7");
				 			map.put("w0301",meetingMain.w0301);
					 		map.put("review_links",segment);
					 		map.put("newTemplate_Id",tabids.split(","));
							map.put("oldTemplate_Id",oldIds.length==0?[]:oldIds.split(","));
					 		//opt=1 创建会议  =2 编辑会议  =3 初始化
							Rpc({functionId:'ZC00002313',async:false,success:function(res){
						 		var result = Ext.decode(res.responseText);
						 		if(result.succeed){
						 			Ext.getCmp(hiddenInputId).setValue(tabids);
									//显示选择的测评表
									Ext.getCmp(targetId).setHtml(text);
					 			}else{
					 				Ext.showAlert(result.message);
					 			}
						 	}},map);

						}
					})
				}else if(curState=="0" || !curState){
					Ext.getCmp(hiddenInputId).setValue(tabids);
					//显示选择的测评表
					Ext.getCmp(targetId).setHtml(text);
				}
			}
		}
		Ext.require('ConfigFileURL.AssessmentTable', function(){
			SalaryTemplateGlobal = Ext.create("ConfigFileURL.AssessmentTable",config);
		});

	},
	/**
	 * 校验表单的有效性
	 * return false|true
	 * @param {} form
	 */
	checkMeetingForm : function(form,values,noConfirm){
		if(form.isValid()){
			//校验评审环节配置项
			if(!values.segments ||values.segments=="undefined"){
				if(!noConfirm){
					Ext.showAlert(zc.editmeeting.mainview.error.nosegments);
				}
				return false;
			}else{
				var segments = values.segments.split(",");
				var alertmsg = "";
				var index = 1;
				for(var i in segments){
					var segment = segments[i];//环节号： 1,2,3,4
					////非同行环节校验
					if(segment=='3')
						continue;
					var evaluationType = values['evaluationType_'+segment];
					var evaluationTypeTemplate = values['evaluationType_'+segment+"_template"];
				 	if(evaluationType[0]==2 && Ext.isEmpty(evaluationTypeTemplate)){
				 		var segmentname = "";
				 		switch(segment){
				 			case '1':
				 				segmentname = zc.label.inReview+zc.editmeeting.mainview.segmentsps;
				 				break;
				 			case '2':
				 				segmentname = zc.label.inExpert+zc.editmeeting.mainview.segmentsps;
				 				break;
				 			case '4':
				 				segmentname = zc.label.inOther+zc.editmeeting.mainview.segmentsps;
				 				break;
				 		}
				 		alertmsg+="    "+(index++)+"、"+segmentname+"<br />";
				 	}
				}
				if(alertmsg.length>0){
					if(!noConfirm){
						Ext.showAlert(zc.editmeeting.mainview.error.noassessmenttables3+"<br/><br/>"+alertmsg);
					}
					return false;//勾选评分时，必须选择测评表
				}
			}

		}else{
			return false;
		}

		return true;
	},
	/**
	 * 下一步
	 * @param {} form
	 * noConfirm 是否显示提示信息
	 */
	next:function(form,noConfirm,fromflag){
		//只读页面不需要保存数据
		if(this.noCanEdit){
			ReviewMeetingSetting.jumpTabByIndex(2);
			return false;
		}
		var values = form.getFieldValues();
		//判断是否需要保存会议
     	if(form.getValues().segments instanceof Array){
     		values.segments=form.getValues().segments.join(",");
     	}else{
     		values.segments = form.getValues().segments+"";
     	}
 		var isValid = meetingMain.checkMeetingForm(form,values,noConfirm);
 		if(!isValid)return false;
 		//校验开始时间不能大于结束日期
 		var w0309s = values.w0309;
 		var w0311s = values.w0311;
 		var w0309 = new Date(w0309s.replace(/-/g,"/"));
	    var w0311 = new Date(w0311s.replace(/-/g,"/"));
 		if(w0309>w0311){
 			if(!noConfirm){
 				Ext.showAlert(zc.editmeeting.mainview.error.timesmsg);
 			}
 			return false;
 		}

 		var map = new HashMap();
 		var record = meetingMain.meetingRecord;
 		if(!record || Ext.isEmpty(record.w0301)){
 			map.put("opt","1");
 		}else{
 			map.put("opt","2");
 			map.put("w0301",record.w0301);
 		}
 		map.put("values",values);

 		//opt=1 创建会议  =2 编辑会议  =3 初始化
		Rpc({functionId:'ZC00002313',async:false,success:function(res){
	 		var result = Ext.decode(res.responseText);
	 		if(!result.succeed){
	 			if(!noConfirm){
		 			Ext.showAlert(result.message);
	 			}
	 			return false;
 			}
 			//给父页面回传数据，方便后续创建页面
			ReviewMeetingSetting.meetingRecord = result.meetingData;
			meetingMain.meetingRecord = result.meetingData;
			meetingMain.w0301 = result.meetingData.w0301;
 			//重新生成申报人分组的tab标签
			if(fromflag=='titleclick'){
				ReviewMeetingSetting.jumpTabByIndex(2);
			}
	 	},scope:this},map);

	 	return true;
	}
});