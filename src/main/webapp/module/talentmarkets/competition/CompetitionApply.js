Ext.define('Talentmarkets.competition.CompetitionApply', {
	extend: 'Ext.panel.Panel',
    layout: 'fit',
    //title: tm.contendApply.title,
    bodyPadding: '0 0 0 5',
    initComponent: function () {
        CompetitionApply = this;
      //  Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}", "underline");
        this.callParent();
        this.init();
    },
    init: function(type){
    	var vo = new HashMap();
    	vo.put("type",type? type:"main");
    	Rpc({functionId: 'TM000000014', success: this.loadData, scope: this}, vo);
    },
    loadData: function(resp){
    	CompetitionApply.removeAll(true,true);
    	var result = Ext.decode(resp.responseText);
    	var return_code = result.returnStr.return_code;
        if (return_code == 'fail') {//失败的话弹出提示信息,不再往下执行
            var return_msg = result.returnStr.return_msg;
            Ext.Msg.alert(tm.tip,tm.contendApply.error[return_msg]);
            return;
        }
        var return_data = result.returnStr.return_data;
        CompetitionApply.competitiveJobsNum = return_data.competitiveJobsNum;
        CompetitionApply.executingJobsNum =  return_data.executingJobsNum;
        CompetitionApply.params = return_data.params;
        CompetitionApply.competition_type = return_data.competition_type;
        CompetitionApply.apply_flag = return_data.applyFlag;
        var tableConfig = result.returnStr.return_data.gridconfig;
        var configObj = Ext.decode(tableConfig);
        /*
        configObj.beforeBuildComp = function (grid) {
            grid.tableConfig.selModel={selType:'checkboxmodel',checkOnly:true};
        };
        */
        CompetitionApply.tableObj = new BuildTableObj(configObj);
        CompetitionApply.loadTableTool();
        CompetitionApply.add(CompetitionApply.tableObj.getMainPanel());
        CompetitionApply.gridStore = CompetitionApply.tableObj.tablePanel.getStore();
    },
    /**
     * 添加表格顶部工具栏
     */
    loadTableTool:function(){
    	var org_flag = true;
    	var selects = ['color:#000 !important;','color:#000 !important;','color:#000 !important;'];
    	if(CompetitionApply.competition_type == 'competitive_jobs'){
    		selects[0] = "color:#00AAEE !important;padding-bottom:2px; border-bottom:2px #00AAEE solid;";
    		org_flag = false;
    	}else if(CompetitionApply.competition_type == 'myapplication'){
    		selects[1] = "color:#00AAEE !important;padding-bottom:2px; border-bottom:2px #00AAEE solid;";
    	}else {
    		selects[2] = "color:#00AAEE !important;padding-bottom:2px; border-bottom:2px #00AAEE solid;";
    	}
   		var toolbar = Ext.widget("toolbar",{
			border:0,
    		dock:'top',
    		height:40,
    		items:[{
    			xtype:'component',
    			border:0,
    			style:CompetitionApply.competitiveJobsNum? 'background:url(/images/new_module/disagree_unchecked.png) no-repeat right center' : '',
    			html:'<a id="competitive_jobs" onclick="CompetitionApply.changeApplyType(\'competitive_jobs\')" href="javascript:void(0);" style="font-size:16px !important;'+selects[0]+'">'+tm.contendApply.competitive_jobs+'</a>'+
    				'<div onclick="CompetitionApply.changeApplyType(\'competitive_jobs\')" style="font-size:16px;color:white;width:20px;text-align:center;float:right;cursor:pointer;">'+CompetitionApply.competitiveJobsNum+'</div>'
			},{
				xtype:'component',
				border:0,
				width:20
			},{
				xtype:'component',
				border:0,
				style:CompetitionApply.executingJobsNum? 'background:url(/images/new_module/disagree_unchecked.png) no-repeat right center' : '',
    			html:'<a id="myapplication" onclick="CompetitionApply.changeApplyType(\'myapplication\')" href="javascript:void(0);" style="font-size:16px !important;'+selects[1]+'">'+tm.contendApply.myapplication+'</a>'+
    				'<div onclick="CompetitionApply.changeApplyType(\'myapplication\')" style="font-size:16px;color:white;width:20px;text-align:center;float:right;cursor:pointer;">'+CompetitionApply.executingJobsNum+'</div>'
			},{
				xtype:'component',
				border:0,
				width:20
			},{
				xtype:'component',
				border:0,
    			html:'<a id="history_application" onclick="CompetitionApply.changeApplyType(\'history_application\')" href="javascript:void(0);" style="font-size:16px !important;'+selects[2]+'">'+tm.contendApply.history_application+'</a>'
			},{
				xtype:'component',
				border:0,
				width:20
			},{
				xtype:'codecomboxfield',
				border: false,
	            width: 250,
	            itemId: 'orgId',
	            onlySelectCodeset:false,
	            codesetid: "@K",
	            emptyText: tm.contendApply.competitionOrgTip ,
	            ctrltype: "0",
	            editable: false,
	            allowBlank: true,
	            hidden:org_flag,
	            value:CompetitionApply.org_id? CompetitionApply.org_id+'`'+CompetitionApply.org_desc:'',
	            listeners: {
            		afterrender: function () {
               			// this.setValue("",true); //初始化赋值
            		},
            		select: function (a, b) {
            			CompetitionApply.org_id = a.value;
            			CompetitionApply.org_desc = a. rawValue;
            			var vo = new HashMap();
    					vo.put("type","competitive_jobs");
    					vo.put("orgId",CompetitionApply.org_id);
    					Rpc({functionId: 'TM000000014', success: CompetitionApply.loadData, scope: CompetitionApply}, vo);
            		}
        		},
        		onFieldMutation:function(e){
        			/*基于业务特殊处理*/
        			if(CompetitionApply.org_id && !this.getRawValue()){
        				CompetitionApply.org_id = '';
        				var vo = new HashMap();
    					vo.put("type","competitive_jobs");
    					vo.put("orgId",CompetitionApply.org_id);
    					Rpc({functionId: 'TM000000014', success: CompetitionApply.loadData, scope: CompetitionApply}, vo);
        				return;
        			}
        			
        			var me = this;
			        /*只有在输入的时候才执行搜索
			                           判断规则：是键盘输入(keyup事件判断)，并且不是特殊控制键(enter\shift\ctrl\tab)时，执行搜索
			        */
			        if(e.type!='keyup'){
			            return;
			        }
			        if(e.isSpecialKey() && e.getKey()!=e.BACKSPACE)
			            return;
			             
			        //搜索时重置数据   
			        //18/9/14 xus复制文本框值的时候也会清空value，不对
			//        this.value = '';
			        this.treeStore.proxy.extraParams.searchtext = encodeURI(this.getRawValue());
			        this.treeStore.load();
			         
			        //更新picker的焦点属性
			        this.updatePickerFocus();
			         
			        if(!this.isExpanded && this.treeStore.proxy.extraParams.searchtext){//xiegh 20170517
			           this.expand(true);
			        }
            	}
			}]
    	});
    	CompetitionApply.tableObj.tablePanel.addDocked(toolbar);
    },
    /**
     * 切换竞聘类型
     * @param type 类型  competitive_jobs 竞聘岗位  myapplication 我的竞聘  history_application 历史竞聘
     */ 
    changeApplyType:function(type){
    	CompetitionApply.org_id = '';
        CompetitionApply.org_desc = '';
    	/*
    	if(type == CompetitionApply.competition_type){
    		return;
    	}
    	*/
    	CompetitionApply.competition_type = type;
    	var competitive_jobs = document.getElementById('competitive_jobs');
    	//competitive_jobs.style.setProperty('color','#000','important');
    	
    	competitive_jobs.style.color="#000";
    	competitive_jobs.style.borderBottom="";
    	var myapplication = document.getElementById('myapplication');
//    	myapplication.style.setProperty('color','#000','important');
    	myapplication.style.color="#000";
    	myapplication.style.borderBottom="";
    	var history_application = document.getElementById('history_application');
//    	history_application.style.setProperty('color','#000','important');
    	history_application.style.color="#000";
    	history_application.style.borderBottom="";
    	if(CompetitionApply.competition_type == 'competitive_jobs'){
	    	competitive_jobs.style.color="#00AAEE";
    //		competitive_jobs.style.setProperty('color','#00AAEE','important');
    		competitive_jobs.style.borderBottom="2px #00AAEE solid";
    	}else if(CompetitionApply.competition_type == 'myapplication'){
//    		myapplication.style.setProperty('color','#00AAEE','important');
	    	myapplication.style.color="#00AAEE";
    		myapplication.style.borderBottom="2px #00AAEE solid";
    	}else {
//    		history_application.style.setProperty('color','#00AAEE','important');
	    	history_application.style.color="#00AAEE";
    		history_application.style.borderBottom="2px #00AAEE solid";
    	}
    	var vo = new HashMap();
    	vo.put("type",CompetitionApply.competition_type);
    	Rpc({functionId: 'TM000000014', success: this.loadData, scope: this}, vo);
    },
    /**
     * 竞聘岗位列渲染
     */
    renderJobsColumnFunc :function(value, metaData, record, rowIndex, colIndex, store, view){
    	var displayValue = "";
        var realValue = "";
        if(value){
            realValue = value.split("`")[0];
            displayValue = value.split("`")[1];
        }
        var html = displayValue;
        if(CompetitionApply.params.postDetailRnameId){
            var e01a1_e = "";
            var vo = new HashMap();
            vo.put("operateType", "encryptE01a1");
            vo.put("e01a1", realValue);
            Rpc({
                functionId: 'TM000000002', success: function (res) {
                    var resData = Ext.decode(res.responseText);
                    var return_code = resData.return_code;
                    if (return_code == "success") {
                        e01a1_e = resData.return_data.e01a1_e;
                    } else {
                        Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.getApproveDataFail);
                        return;
                    }
                }, scope: CompetitionApply, async: false
            }, vo);
            html = "<a href=javascript:CompetitionApply.jobsColumnClick('" + e01a1_e + "','"+displayValue+"');>" + displayValue + "</a>";
        }
        return html;
    },
    /**
     * 岗位名称点击事件处理函数  用于展现岗位说明书
     * @param e01a1_e
     */
    jobsColumnClick:function (e01a1_e,value) {
        if(!CompetitionApply.params.postDetailRnameId){
            //'请先配置应聘简历登记表！';
            Ext.Msg.alert(tm.tip,tm.contendPos.msg.notSetPostDetailRname);
            return;
        }
        var src = '/module/card/cardCommonSearch.jsp?inforkind=6&fieldpriv=1&cardFlag=1&a0100=' + e01a1_e + '&tabid=' + CompetitionApply.params.postDetailRnameId;
        var registrationFormWin = Ext.create('Ext.window.Window', {
            width: '100%',
            height: Ext.getBody().getViewSize().height,
            title: value,//岗位详情登记表
            modal: true,//遮罩
            resizable: false,//禁止拉伸
            draggable: false,//紧张拖拽
            html: '<iframe id="iframeId" frameborder="0" width="100%" height="100%" src="' + src + '"></iframe>',
            listeners: {
                resize: function () {
                    //浏览器放缩时，重新计算高度，否则没有滚动条
                    if (registrationFormWin) {
                        registrationFormWin.setHeight(Ext.getBody().getViewSize().height);
                    }
                }
            }
        }).show();

        //隐藏toolbar
        var interval = setInterval(function(){
            var iframeExt = document.getElementById("iframeId").contentWindow.Ext;
            if(iframeExt){
                var cardPanel = iframeExt.getCmp('cardtabPanelId');
                if (cardPanel) {
                    var ownerCt = cardPanel.ownerCt;
                    ownerCt.getDockedItems()[0].hide();
                    ownerCt.setBodyStyle({border:0});
                    clearInterval(interval);
                }
            }
        },100);
    },
    /**
     * 操作列数据补充
     * 竞聘岗位  值：报名    我的竞聘 值 撤回&撤销
     */
    operateRenderFunc:function(value, metaData){
    	var rowIndex = metaData.rowIndex;
		if(CompetitionApply.competition_type == 'competitive_jobs'){
			if(CompetitionApply.apply_flag){
				value = '<a onclick="CompetitionApply.applyClick(\''+rowIndex+'\')" style="color:#1B4A98;" href="javascript:void(0);">'+tm.contendApply.apply+'</a>';
			}
		}else{
	    	var template;
	    	var state_type = 'end';
	    	if(CompetitionApply.competition_type=='myapplication' && metaData.record.data.z8303 && metaData.record.data.z8303.split('`')[0] == '01'){
	    		state_type = 'executing';
	    	}
			var vo = new HashMap();
	    	vo.put('type','search_template');
	    	vo.put('state_type',state_type);
	    	vo.put('z8101',metaData.record.data.z8101);
	    	Rpc({functionId: 'TM000000014', async:false, success: function(resp){
	    		var result = Ext.decode(resp.responseText);
	            var return_msg_code = result.returnStr.return_code;
	            if(return_msg_code == 'fail'){
		            var return_msg = result.returnStr.return_msg;
            		Ext.Msg.alert(tm.tip,tm.contendApply.error[return_msg]);
		            return;
	            }
	            var return_data  = result.returnStr.return_data;
	            template = return_data.templateData.searchTemplate;
	    	}},vo);
	    	if(!template.ins_id || !template.task_id){
	    		return '';
	    	}
	    	if(CompetitionApply.competition_type == 'myapplication'){
				value = "<a onclick=CompetitionApply.lookOverFunc('"+template.tabid+"','"+template.ins_id+"','"+template.task_id_e+"','0','1','1','run') style='color:#1B4A98;' href='javascript:void(0);'>查看</a>";
				if(template.recallflag =='1'){
					value = value + "&nbsp;&nbsp;<a onclick=CompetitionApply.cancelClick('"+template.tabid+"','"+template.ins_id+"','"+template.task_id+"','4') style='color:#1B4A98;' href='javascript:void(0);'>撤回</a>";
				}
				if(template.cancelflag =='1'){
					value = value + "&nbsp;&nbsp;<a onclick=CompetitionApply.cancelClick('"+template.tabid+"','"+template.ins_id+"','"+template.task_id+"','5') style='color:#1B4A98;' href='javascript:void(0);'>撤销</a>";
				}
				    	
	    	}else{
	    		value = "<a onclick=CompetitionApply.lookOverFunc('"+template.tabid+"','"+template.ins_id+"','"+template.task_id_e+"','"+template.recallflag+"','','0','stop') style='color:#1B4A98;' href='javascript:void(0);'>查看</a>";
	    	}
		}	
    	return value;
    },
    /**
     * 报名操作
     * @param rowIndex 行号
     */
    applyClick : function(rowIndex){
    	if(CompetitionApply.params.laveJobNum == 0){
    		Ext.Msg.alert(tm.tip,tm.contendApply.competingFail);
    		return;
    	}
    	if(CompetitionApply.params.laveJobNum == 1){
    		Ext.Msg.confirm(tm.tip,tm.contendApply.competeLastOne,function(btn){
				if(btn == 'yes'){
					CompetitionApply.applyFunc(rowIndex);
    			}
    		});
    	}else{
    		Ext.Msg.confirm(tm.tip,tm.contendApply.lave+CompetitionApply.params.laveJobNum+tm.contendApply.jobNum,function(btn){
    			if(btn == 'yes'){
    				CompetitionApply.applyFunc(rowIndex);
    			}
    		});
    	}
    },
    /**
     * 跳转至报名办理页面
     * @param rowIndex 行号
     */
    applyFunc : function(rowIndex){
    	var record = CompetitionApply.gridStore.getAt(rowIndex);
    	var template = CompetitionApply.params.records[0];
    	template.z8101 = record.data.z8101;
    	template.b0110 = record.data.b0110.split('`')[0];
    	template.e0122 = record.data.e0122.split('`')[0];
    	template.e01a1 = record.data.e01a1.split('`')[0];
    	var templateVo = new HashMap();
		templateVo.put("templateType",CompetitionApply.params.templateType);
		templateVo.put("records",CompetitionApply.params.records);
		Rpc({functionId: 'TM000000009', success: function(resp){
		    var result = Ext.decode(resp.responseText);
            var return_code = result.return_code;
            if(return_code == 'fail'){
            	var return_msg_code = result.return_msg_code;
            	if (return_msg_code == 'noSetingData') {
	                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.pelaseSeting);
	            } else if (return_msg_code == 'notSetReleasePostTemplatePlan') {
	                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.jobNoSetPlan);
	            } else if (return_msg_code == 'initTempTemplateTableError') {
	                Ext.Msg.alert(tm.contendPos.msg.title, tm.contendPos.initTempTemplateTableError);
	            } else if (tm.contendApply.error[return_msg_code]) {
	            	Ext.Msg.alert(tm.contendPos.msg.title, tm.contendApply.error[return_msg_code]);
	            } else {
	            	Ext.Msg.alert(tm.contendPos.msg.title, return_msg_code);
	            }
	            return;
            }
            var return_data = result.return_data;
            var tabid = return_data.tabid;
			var templateObj = new Object();
			templateObj.sys_type="1";
			templateObj.tab_id=tabid;
			templateObj.return_flag="5";
			templateObj.module_id="9";
			templateObj.approve_flag="1";
			templateObj.task_id="0";
		    templateObj.card_view_type="1";
	    	templateObj.view_type="card";
            templateObj.callBack_init = "CompetitionApply.tempFunc";
            templateObj.callBack_close = "CompetitionApply.goBack";
            Ext.require('TemplateMainUL.TemplateMain', function () {
                Ext.create("TemplateMainUL.TemplateMain", {templPropety: templateObj});
            });
		}, scope: this}, templateVo);
    },
    /**
     * 查看岗位申报单子
     */
    lookOverFunc:function(tabid,ins_id,task_id,recallflag,actor_type,browseprint,type){
    	var templateObj = new Object();
		templateObj.sys_type="1";
		templateObj.tab_id=tabid;
		templateObj.return_flag="3";
		templateObj.module_id=actor_type==4?"1":"9";
		templateObj.approve_flag="0";
		templateObj.task_id=task_id;
		templateObj.sp_flag="1";
		templateObj.ins_id=ins_id;
		templateObj.callBack_init = "CompetitionApply.tempFunc";
        templateObj.callBack_close = "CompetitionApply.goBack";
        if(type == 'stop' && recallflag === '1'){//终止
			templateObj.other_param="isDelete=true";
        }else{//运行中
			templateObj.other_param="recallflag="+recallflag+"`browseprint="+browseprint;
        }
  		Ext.require('TemplateMainUL.TemplateMain', function(){
			TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
		});
    }, 
    /**
     * 撤回&撤销 操作
     * 
     * opt 4 撤回 5 撤销
     */
    cancelClick : function(tabid,ins_id,taskid,opt){
    	var json_str = {};
    	json_str.tabid = tabid;
    	json_str.ins_id = ins_id;
    	json_str.taskid = taskid;
    	json_str.opt = opt;
    	json_str.infor_type ="1";
    	json_str.ischeck = "1";
    	CompetitionApply.cancelAndRetractResp(json_str);
    },
    /**
	 * 撤回&撤销操作处理
	 *  {  
     *     “tabid”:”1”     //模板id 
	 *	   "ins_id":"101"  //实例ID
	 *	   "taskid":"20"   //任务ID 
	 *	   "ischeck":""再次撤单标记 默认1，（撤单后提示是否覆盖时 确认后回传默认数据为0）
	 *	   "opt":"4" 4:撤单  5 撤销
	 *	   "infor_type":1人员 2单位 3岗位
	 *	}  
	 */
    cancelAndRetractResp:function(json_str){
		var param = JSON.stringify(json_str);
		var vo = new HashMap();
		vo.put("type","retract");
    	vo.put("param",param);
    	Rpc({
            functionId: 'TM000000014', success: function (res) {
            	var resultObj = Ext.decode(res.responseText);
            	/*
            	 * success:true/false  (type=1,2,3 为true|false type为空),
                 * type:1:无法撤回,2：模板有起草单据，提示是否覆盖,3：成功,
                 * msg:处理信息
                 */
            	var obj = Ext.decode(resultObj.returnStr.return_data.jsonstr);
            	if(obj.opt =='5'){//撤销
            		if(obj && obj.success && ojb.success == 'false'){ 
            			Ext.Msg.alert(tm.tip,tm.contendApply.not_cancel);
            		}else{//obj返回值为{} 说明撤销成功 
            			var vo = new HashMap();
				    	vo.put("type",CompetitionApply.competition_type);
				    	Rpc({functionId: 'TM000000014', success: CompetitionApply.loadData, scope: CompetitionApply}, vo);
            		}
            		return;
            	}
            	//撤回返回结果值
            	if(obj.success){
            		if(obj.type == 1){
            			Ext.Msg.alert(tm.tip,tm.contendApply.not_retract);
            		}else if( obj.type == 2 ){
						json_str.ischeck = "0";
						CompetitionApply.cancelAndRetractResp(json_str);
            		}else if(obj.type == 3){
            			var vo = new HashMap();
				    	vo.put("type",CompetitionApply.competition_type);
				    	Rpc({functionId: 'TM000000014', success: CompetitionApply.loadData, scope: CompetitionApply}, vo);
            		}
            	}else{
            		if(obj && obj.success && ojb.success == 'false'){ 
            			Ext.Msg.alert(tm.tip,obj.msg);
            		}else{//撤回覆盖成功
            			var vo = new HashMap();
				    	vo.put("type",CompetitionApply.competition_type);
				    	Rpc({functionId: 'TM000000014', success: CompetitionApply.loadData, scope: CompetitionApply}, vo);
            		}
            	}
            }
    	},vo);
    },
    tempFunc:function(){
    	CompetitionApply.removeAll();
        CompetitionApply.add(templateMain_me.mainPanel);
    },
    goBack:function(){
    	CompetitionApply.init(CompetitionApply.competition_type);
    	//window.location.reload();
    }
});