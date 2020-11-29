Ext.override(Ext.form.field.Text,{
	validator:function (value) {
		if(value.indexOf('<')!=-1 || value.indexOf('>')!=-1 ){
			return "不允许包含<、>符号";
		}
		return true;
	}
});
/**
 * 问卷调查全局变量
 */
Ext.define("GlobalVariable", {
	myQuestionnaire : undefined,
	viewport : undefined,
	statusRender : function(value) {
		if (value == '0')
			return "未发布";
		else if (value == '1')
			return "运行中";
		else if (value == '2')
			return "暂停";
		else if (value == '3')
			return "结束";
		else
			return "";
	},
	// 创建
	createNewQN : function(param) {
		QN_global.viewport.removeAll(false);
		Ext.require('QuestionnairePlan.CreatePlan', function() {
					Ext.create("QuestionnairePlan.CreatePlan", {
								container : QN_global.viewport,
								funcpriv : param.funcpriv,
								goBackFn : function() {
									QN_global.viewport.remove(this.mainPanel,
											true);
									QN_global.viewport
											.add(QN_global.myQuestionnaire.tableObj
													.getMainPanel());
								}
							});

				});
	},
	//查看模板 changxy 
	seeTemplates:function(params){
		QN_global.viewport.removeAll(false);
		Ext.require('QuestionnairePlan.SeeTemplate',function(){
		          QN_global.seeTemplate=Ext.create("QuestionnairePlan.SeeTemplate", {
                                container : QN_global.viewport,
                                funcpriv : params.funcpriv,
                                goBackFn : function() {
                                   QN_global.viewport.remove(this.tableObj.getMainPanel(),
                                            true);
                                    QN_global.viewport
                                            .add(QN_global.myQuestionnaire.tableObj
                                                    .getMainPanel());
                                }
                            });
		});
	},
	// 通过状态快速查询
	searchQuestionnaireStatus : function(status) {
		var value = new HashMap();
		value.put("status", status);
		value.put("subModuleId",
				this.myQuestionnaire.tableObj.config.subModuleId);
		Rpc({
					functionId : 'QN10000005',
					async : false,
					success : function() {
						// this.myQuestionnaire.tableObj.tablePanel.getStore().reload();
						QN_global.refreshMyQuestionnaire();
					},
					scope : this
				}, value);
		var select = Ext.query(".myselect");
		for (var i = 0; i < select.length; i++) {
			if (select[i].name == status) {
				select[i].style.textDecoration = "underline";
			} else
				select[i].style.textDecoration = "none";
		}

	},
	// 删除和清空
	deleteOrCleanPlan : function(param, selectData) {
		var qn = QN_global.myQuestionnaire;
		var selectListData = qn.tableObj.tablePanel.getSelectionModel().getSelection();// 获取列表选中的数据
		var selectCardData=qn.tableObj.tablePanel.cardView.getSelectionModel().getSelection();// 获取卡片选中的数据
		if(selectListData==null||selectListData==''){
		  selectData=selectCardData;
		}else{
		  selectData=selectListData;
		}
		if (selectData.length < 1) {
			Ext.Msg.alert("提示信息", "请选择数据！");
			return;
		}
		var ids = "";
		var flag=true;
		var action=""; //取出parameter中的action，根据不同的事件提示 changxy 20160625
		if(param!=null||param!="")
		  action=param.action;
		if (selectData.length > 0) {
			for (var i = 0; i < selectData.length; i++) {
				if (selectData[i].data.status =="1") {
					if(action=="delete")
					   Ext.Msg.alert("提示信息","选中的项目存在运行中状态不可删除！");
					if(action=="clean")
					   Ext.Msg.alert("提示信息","选中的项目存在运行中状态不可清空！");
					flag=false;
				}else{
				    ids += selectData[i].data.planid;
                    if (i < selectData.length - 1) {
                        ids += ",";
                    }
				}

			}

		}
		if(flag){
		QN_global.doDeleteOrClean(param.action, ids);
		}
	},

	// 操作成功
	busiCallBack : function() {
		this.tableObj.tablePanel.getStore().reload();
	},
	//

	actionRender : function(value, meta, record) {
		var html ="";
		    html += "<a href='javascript:QN_global.previewTemplate(\""
				+ record.get("qnid") + "\",true);'>预览<a/>&nbsp;&nbsp;&nbsp;";
		          
		if (record.get("status") == "0" || record.get("status") == '2') {
			if (record.get("connnumber") == 1&&QN_global.Renderconfig.designTemplate==1)//设计权限
				html += "<a href='javascript:QN_global.designTemplate(\""
						+ record.get("planid") + "\",\"" + record.get("qnid")
						+ "\");'>设计<a/>&nbsp;&nbsp;&nbsp;";
			if(QN_global.Renderconfig.starts==1)
			    html += "<a href='javascript:QN_global.changePlanStatus(\"start\",\""
					+ record.get("planid")
					+ "\",\""
					+ record.get("qnid") 
					+ "\")'>发布<a/>&nbsp;&nbsp;&nbsp;"
			if(QN_global.Renderconfig.cleanPlan==1)	
			  html+="<a href='javascript:QN_global.doDeleteOrClean(\"clean\",\""
					+ record.get("planid")
					+ "\")'>清空</a>&nbsp;&nbsp;&nbsp;"
					
			if(QN_global.Renderconfig.deletePlan==1)    
			  html+= "<a href='javascript:QN_global.doDeleteOrClean(\"delete\",\""
					+ record.get("planid") + "\")'>删除</a>"
					;
		} else if (record.get("status") == "1") {
			if(QN_global.Renderconfig.recoverycount==1)
			html += "<a href='javascript:QN_global.setRecoveryConfig(\""
					+ record.get("planid") 
					+ "\",\""
					+ record.get("qnid")
					+ "\","
					+ record.get("recoverycount")
					+ ")'>收集配置</a>&nbsp;&nbsp;&nbsp;"
			
			if(QN_global.Renderconfig.analysisPlanData==1)
			html +=	"<a href='javascript:QN_global.analysisPlanData(\""
					+ record.get("planid")
					+ "\",\""
					+ record.get("qnid")
					+ "\")'>分析</a>&nbsp;&nbsp;&nbsp;"
			    
			if(QN_global.Renderconfig.purse==1) 
			html += "<a href='javascript:QN_global.changePlanStatus(\"pause\",\""
					+ record.get("planid")
					+ "\",\""
					+ record.get("qnid")
					+ "\")'>清空数据并暂停</a>&nbsp;&nbsp;&nbsp;"
			    
			    
			if(QN_global.Renderconfig.stops==1)
			html += "<a href='javascript:QN_global.changePlanStatus(\"stop\",\""
					+ record.get("planid") 
					+ "\",\"" 
					+ record.get("qnid")
					+ "\")'>结束</a>";
			
		} else {
			/*
			if(QN_global.Renderconfig.starts==1)
			html += "<a href='javascript:QN_global.changePlanStatus(\"start\",\""
                    + record.get("planid")
                    + "\",\""
                    + record.get("qnid")
                    + "\")'>发布<a/>&nbsp;&nbsp;&nbsp;"
            if(QN_global.Renderconfig.cleanPlan==1) 		
			html += "<a href='javascript:QN_global.doDeleteOrClean(\"clean\",\""
					+ record.get("planid")
					+ "\")'>清空</a>&nbsp;&nbsp;&nbsp;"
            */
            if(QN_global.Renderconfig.analysisPlanData==1)        
			html += "<a href='javascript:QN_global.analysisPlanData(\""
					+ record.get("planid")
					+ "\",\""
					+ record.get("qnid")
					+ "\")'>分析</a>&nbsp;&nbsp;&nbsp;"
			if(QN_global.Renderconfig.deletePlan==1) 		
		    html +=	"<a href='javascript:QN_global.doDeleteOrClean(\"delete\",\""
					+ record.get("planid") + "\")'>删除</a>";
		}
		return html;
	},
	setRecoveryConfig : function(planid, qnid, recoverycount) {
		if (!recoverycount)
			recoverycount = 0;
		Ext.require("QuestionnaireRecovery.RecoveryQn", function() {
			var configPanel = Ext.create("QuestionnaireRecovery.RecoveryQn", {
				planId : planid,
				qnId : qnid,
				title : '收集配置',
				tools : [{
							xtype : 'box',
							html : '已回收' + recoverycount + '份问卷',
							style : 'padding-right:100px'
						}, {
							text : '结束调查',
							xtype : 'button',
							margin : '0 10 0 0',
							handler : function() {
							    var callback = function(){
							    		QN_global.viewport.remove(this.up('panel', 2),
										true);
									QN_global.analysisPlanData(planid, qnid);
							    };
								QN_global.changePlanStatus('stop',planid, qnid,callback,this);
								// QN_global.viewport.add(QN_global.myQuestionnaire.tableObj.getMainPanel());
								// QN_global.refreshMyQuestionnaire();
							}
						}, {
							text : '返回',
							xtype : 'button',
							handler : function() {
								QN_global.viewport.remove(this.up('panel', 2),
										true);
								QN_global.viewport
										.add(QN_global.myQuestionnaire.tableObj
												.getMainPanel());
							}
						}]
			});
			QN_global.viewport.removeAll(false);
			QN_global.viewport.add(configPanel);
		});
	},
	changePlanStatus : function(action, planid, qnid,callback,scope) {
	
	    var success = false;
		var handle = function() {
			var map = new HashMap();
			map.put("planid", planid);
			map.put("qnid", qnid);
			map.put("action", action);
			Rpc({
						functionId : 'QN10000006',
						async:false,
						success : function(re) {
							var pa = Ext.decode(re.responseText);
							if (pa.error) {
								Ext.Msg.alert("提示信息", pa.error);
								return;
							}
							if(callback){
							    Ext.callback(callback,scope);
							}else
								QN_global.myQuestionnaire.tableObj.tablePanel
										.getStore().reload();
						}
					}, map);
		};
		//点击结束提示信息
		if (action == 'stop') {
			Ext.Msg.confirm("提示信息","确认结束计划？",function(btn){
				if(btn!='yes')
			  		return;
				handle();
			});
			return;
		}
		//点击暂停提示信息
		if (action == 'pause') {
            Ext.Msg.confirm("提示信息","确认暂停计划？",function(btn){
	            if(btn!='yes')
	              return;
	            handle();
            });
            return;
        }
        

		Ext.Msg.confirm("提示信息", "是否发布问卷？", function(btn) {
					if (btn != 'yes')
						return;
					else
						handle();
		});

	},
	designTemplate : function(planid, qnid) {
		QN_global.viewport.removeAll(false);
		Ext.require('QuestionnaireTemplate.QuestionnaireBuilder', function() {
					var QuestionnaireBuilder = Ext.create(
							"QuestionnaireTemplate.QuestionnaireBuilder", {
								title : QN.template.design,
								planId : planid,
								qnId : qnid,
								backButtonFn : function() {
									QN_global.viewport
											.remove(
													QN_global.viewport
															.child('questionnairebuilder'),
													true);
									QN_global.viewport
											.add(QN_global.myQuestionnaire.tableObj
													.getMainPanel());
									QN_global.myQuestionnaire.tableObj.tablePanel
											.getStore().reload();
								}
							});
					QN_global.viewport.add(QuestionnaireBuilder);
				});
	},
	analysisPlanData : function(planid, qnid) {
		Ext.require('QuestionnaireTemplate.QuestionnaireBuilder', function() {
					QN_global.viewport.removeAll(false);
					var AnalysisBuilder = Ext.create(
							"QuestionnaireTemplate.QuestionnaireBuilder", {
								title : QN.template.analysis,
								planId : planid,
								qnId : qnid,
								hideNavigation : true,
								backButtonFn : function() {
									QN_global.viewport
											.remove(
													QN_global.viewport
															.child('questionnairebuilder'),
													true);
									QN_global.viewport
											.add(QN_global.myQuestionnaire.tableObj
													.getMainPanel());
									QN_global.myQuestionnaire.tableObj.tablePanel
											.getStore().reload();
								}
							});
					QN_global.viewport.add(AnalysisBuilder);
				});
	},
	doDeleteOrCleanCard:function(action,planids,struts){ //卡片删除提示
	   if(struts=="1"){
	   Ext.Msg.alert("提示信息","选中的项目是运行中状态不可删除！");
	   }else{
	       QN_global.doDeleteOrClean(action, planids);
	   }
	},
	doDeleteOrClean : function(action, planids) {
		var map = new HashMap();
		map.put("planids", planids);
		map.put("action", action);
		var mesStr = QN.plan.confirmDelete;
		if (action == 'clean')
			mesStr = QN.plan.confirmClean;

		Ext.Msg.confirm("提示信息", mesStr, function(btn) {
					if (btn == "yes") {
						var qn = QN_global.myQuestionnaire;
						Rpc({
									functionId : 'QN10000004',
									async : false,
									success : QN_global.busiCallBack,
									scope : qn
								}, map);
					}
				});
	},
	plannameRenderFunc:function(value,m,record){//名称列链接
		if(record.get('status')==0)//有设计权限
		     if(QN_global.Renderconfig.designTemplate==1)//权限状态判断
		 return "<a  href='javascript:QN_global.designTemplate(\""+ record.get("planid") + "\",\"" + record.get("qnid")+ "\");'>"+value+"<a/>";
		      else
		      return "<p>"+value+"</p>";
		 if(record.get('status')==1)//收集配置
		      if(QN_global.Renderconfig.recoverycount==1)//权限
		          return "<a href='javascript:QN_global.setRecoveryConfig(\""+ record.get("planid")  + "\",\""+ record.get("qnid")+ "\",\""+ record.get("recoverycount") +"\")'>"+value+"</a>";
		        else 
		          return "<p>"+value+"</p>";
		 if(record.get('status')==2)//设计
		      if(QN_global.Renderconfig.designTemplate==1)
		          return "<a href='javascript:QN_global.designTemplate(\""+ record.get("planid") + "\",\"" + record.get("qnid") + "\");'>"+value+"<a/>";
		      else
		          return "<p>"+value+"</p>";
		  if(record.get('status')==3)//分析
		      if(QN_global.Renderconfig.analysisPlanData==1)
		      return "<a href='javascript:QN_global.analysisPlanData(\""+ record.get("planid") + "\",\""+ record.get("qnid")+ "\")'>"+value+"</a>";
              else
              return "<p>"+value+"</p>";
	},
	templateNameRenderFn : function(value, m, record) {
		return "<a href=\"javascript:QN_global.previewTemplate('"
				+ record.get('qnid') + "');\">" + value + "</a>";
	},
	seeTmplateNameRenderFn:function(value, m, record){//查看问卷模板
		return "<a href=\"javascript:QN_global.previewTemplate('"
                + record.get('qnid') + "',true);\">&nbsp;&nbsp;&nbsp;&nbsp;预览</a>";
	},
	previewTemplate : function(qnid, douse) {
		var title = '<a href="javascript:QN_global.showQuestionBuilder(\''
				+ qnid + '\')">使用此问卷</a>';
		if (douse)
			title = "预览问卷";
		Ext.require('QuestionnaireTemplate.PreviewTemplate', function() {
			var preview = Ext.create("QuestionnaireTemplate.PreviewTemplate", {
			            panwidth:document.body.clientWidth * 0.7,
						qnId : qnid,
						border : 0
					});
			var previewWindow = Ext.getCmp("previewWindow");
			if (previewWindow) {
				previewWindow.removeAll(true);
				previewWindow.add(preview);
				previewWindow.setTitle(title);
				previewWindow.qnId = qnid;
				previewWindow.header.setHeight(37);
				previewWindow.setVisible(true);
			} else
				previewWindow = Ext.widget("panel", {
					layout : {
						type : 'vbox',
						align : 'stretch'
					},
					modal:true,
					id : 'previewWindow',
					qnId:qnid,
					title : title,
					shadow : false,
					closeAction : "hide",
					autoScroll : true,
					x : document.body.clientWidth * 0.2,
					y : 0,
					tools : [{
						xtype:'button',text:'PDF',
						icon:rootPath+"/images/PDF.png",
						handler:function(){
							var vo = new HashMap();
							vo.put("qnid",this.toolOwner.qnId);
							Rpc({functionId:'QN40000003',async:false,success:QN_global.downLoadQuestionnaire},vo);
						}
						
					},{
								type : 'close',
								handler : function() {
									this.up('panel').close();
								}
							}],
					height : "100%",
					width : "80%",
					floating : true,
					items : preview,
					listeners : {
						render : function() {
							Ext.EventManager.on(window, "resize", function() {
										this
												.setPosition([
														document.body.clientWidth
																* 0.2, 0]);
										this.setSize("80%", "100%");
									}, this);
						}
					}
				}).show();
				previewWindow.header.setHeight(37);
			//previewWindow.zIndexManager._showModalMask(previewWindow);
		});

	},
	downLoadQuestionnaire:function(resp){
		var name = Ext.decode(resp.responseText).name;
		//xus 20/3/2 vfs改造
//		 window.open("/servlet/DisplayOleContent?openflag=true&filename="+$URL.encode(name));
		window.open("/servlet/vfsservlet?fileid="+name+"&fromjavafolder=true");
	},
	showQuestionBuilder : function(qnid) {
		Ext.getCmp('previewWindow').close(true);
		QN_global.viewport.removeAll(true);
		var QuestionnaireBuilder = Ext.create(
				"QuestionnaireTemplate.QuestionnaireBuilder", {
					title : this.qnname,
					qnId : qnid,
					qnName : this.qnname,
					backButtonFn : function() {
						QN_global.viewport.remove(QN_global.viewport
										.child('questionnairebuilder'), true);
						QN_global.viewport
								.add(QN_global.myQuestionnaire.tableObj
										.getMainPanel());
						QN_global.myQuestionnaire.tableObj.tablePanel
								.getStore().reload();
					}
				});
		this.viewport.add(QuestionnaireBuilder);
	},

	searchTemplateLib : function(state) {
		Ext.getDom('template_all').style.textDecoration='none';
		Ext.getDom('template_1').style.textDecoration='none';
		Ext.getDom('template_2').style.textDecoration='none';
		Ext.getDom('template_3').style.textDecoration='none';
		Ext.getDom('template_4').style.textDecoration='none';
		Ext.getDom('template_'+state).style.textDecoration='underline';


		var values = new HashMap();
		values.put("state", state);
		Rpc({
					functionId : 'QN20000002',
					success : function() {
						QN_global.viewport.query('gridpanel')[0].getStore()
								.reload();
					}
				}, values);
	},
	refreshMyQuestionnaire : function() {
		QN_global.myQuestionnaire.tableObj.tablePanel.getStore().reload();
	},
	questionDescRender : function(value, meta, record) {
		return record.get('qnum') + '题，' + record.get('page') + '页';
	},
	questionInfoRender : function(value, meta, record) {
		return '引用' + record.get('linknum') + '次，收集' + record.get('recount')
				+ '份';
	},
	validLibraryEdit:function(record){
		return record.get('canedit')==1;
	},
	//模板列表所属机构编辑验证
	b0110ValidLibraryEdit:function(record){
		//可以编辑且公有的可以选择所属机构
		return record.get('canedit')==1&&record.get("isshare")=="1";
	}

});