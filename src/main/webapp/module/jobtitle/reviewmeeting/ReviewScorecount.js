/**
 * linbz add 2018/04/14
 * 分数统计
 */
Ext.define("ReviewMeetingURL.ReviewScorecount",{
	isFinished: false,
	constructor:function(config) {
		reviewScorecount_me = this;
		this.w0301_e = config.w0301_e;
		// 1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
		this.review_links = config.review_links;
		// =1 票数统计 	=2 分数统计
		this.evaluationType = config.evaluationType;
		// 所选的分组id串
		this.groupids = config.groupids;
		this.isFinished = config.isFinished;
		this.init();
	},
	//初始加载页面
	init:function() {
		var map = new HashMap();
		// =14 票数统计|分数统计
		map.put("opt", "14");
		map.put("w0301_e", reviewScorecount_me.w0301_e);
		map.put("review_links", reviewScorecount_me.review_links);
		map.put("evaluationType",reviewScorecount_me.evaluationType);
		map.put("groupids",reviewScorecount_me.groupids);
		map.put("isFinished",this.isFinished);
	    Rpc({functionId:'ZC00002316',async:false,success:this.getTableOK,scope:this}, map);
	},
	//加载表单
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		// 公示、投票环节显示申报材料表单上传的word模板内容
		this.support_word = result.support_word;
		reviewScorecount_me.subModuleId = result.subModuleId;
		
		var tableConfig=result.tableConfig;
		var title = "票数统计";
		if("2" == reviewScorecount_me.evaluationType)
			title = "分数统计";
		var countWin = Ext.getCmp('countWinid');
	    if(countWin)
	    	countWin.close();
	    if(reviewScorecount_me.review_links=="1"){
	    	title+="（"+zc.label.inReview+zc.editmeeting.mainview.segmentsps+"）";
	    }else if(reviewScorecount_me.review_links=="2"){
	    	title+="（"+zc.label.inExpert+zc.editmeeting.mainview.segmentsps+"）";
	    }else if(reviewScorecount_me.review_links=="3"){
	    	title+="（"+zc.label.exExpert+"）";
	    }else if(reviewScorecount_me.review_links=="4"){
	    	title+="（"+zc.label.inOther+zc.editmeeting.mainview.segmentsps+"）";
	    }
	    countWin = Ext.create('Ext.window.Window', {
			id : 'countWinid',
			modal : true,
			title: title,
			height: Ext.getBody().getHeight(),
			width: Ext.getBody().getWidth(),
			tools:[{id:'ReviewScorecount_schemeSetting',xtype:'toolbar',border:false}],
			border: false,
			draggable : false,
			resizable : false,
			layout:'fit'
		});
	    countWin.show();
	    
	    var obj = Ext.decode(tableConfig);
		obj.openColumnQuery = true;//方案查询可以查询自定义指标
		obj.columnNowrap = true;//表头不换行
		//单独处理三层表头  haosl 2018年8月2日 start
		var columns = obj.tablecolumns;
        var reg = /^c_\w+_[1-4]$/i;
        //记录需要移除的列的位置。
        var indexArr = [];
        var committee_hb=null,subject_hb=null,checkproficient_hb=null,college_hb=null;
        for(var i in columns){
            var childColumns = columns[i].childColumns;
            if(childColumns.length>=2){
                var cid = childColumns[0].columnId;
                if(!Ext.isEmpty(cid) && reg.test(cid)){
                    var index = cid.substring(cid.length-1);
                    var cname = columns[i].columnDesc.replace(/【([\s\S]*)】/,"");
                    columns[i].columnDesc = cname;
                    //记录要从员columns 中去掉的列的序号
                    indexArr.push(i);
                    if("1"==index){
                        if (committee_hb) {
                            var tmp = committee_hb.childColumns;
                            tmp.push(columns[i]);
                            committee_hb.childColumns = tmp;
                        }else{
                            committee_hb = {};
                            committee_hb.childColumns=[columns[i]];
                            committee_hb.columnId = "committee_hb";
                            committee_hb.columnRealDesc=zc.reviewfile.step1showtext;
                            committee_hb.columnType="A";
                            committee_hb.columnWidth=100;
                            committee_hb.columnDesc=committee_hb.columnRealDesc;
                            committee_hb.hintText=committee_hb.columnRealDesc;
                            committee_hb.editableValidFunc='false';
                            committee_hb.locked=false;
                            committee_hb.rendererFunc="";
                            committee_hb.queryable=false;
                        }
                    }else if("2"==index){
                        if (subject_hb){
                            var tmp = subject_hb.childColumns;
                            tmp.push(columns[i]);
                            subject_hb.childColumns = tmp;
                        }else {
                            subject_hb = {};
                            subject_hb.columnId = "subject_hb";
                            subject_hb.childColumns =[columns[i]];
                            subject_hb.columnRealDesc = zc.reviewfile.step2showtext;
                            subject_hb.columnType = "A";
                            subject_hb.columnWidth = 100;
                            subject_hb.columnDesc = subject_hb.columnRealDesc;
                            subject_hb.hintText = subject_hb.columnRealDesc;
                            subject_hb.editableValidFunc = 'false';
                            subject_hb.locked = false;
                            subject_hb.rendererFunc = "";
                            subject_hb.queryable = false;
                        }
                    }else if("3"==index){
                        if (checkproficient_hb){
                            var tmp = checkproficient_hb.childColumns;
                            tmp.push(columns[i]);
                            checkproficient_hb.childColumns = tmp;
                        }else {
                            checkproficient_hb = {};
                            checkproficient_hb.columnId = "checkproficient_hb";
                            checkproficient_hb.childColumns = [columns[i]];
                            checkproficient_hb.columnRealDesc = zc.reviewfile.step3showtext;
                            checkproficient_hb.columnType = "A";
                            checkproficient_hb.columnWidth = 100;
                            checkproficient_hb.columnDesc = checkproficient_hb.columnRealDesc;
                            checkproficient_hb.hintText = checkproficient_hb.columnRealDesc;
                            checkproficient_hb.editableValidFunc = 'false';
                            checkproficient_hb.locked = false;
                            checkproficient_hb.rendererFunc = "";
                            checkproficient_hb.queryable = false;
                        }
                    }else if("4"==index){
                        if (college_hb){
                            var tmp = college_hb.childColumns;
                            tmp.push(columns[i]);
                            college_hb.childColumns = tmp;
                        }else{
                            college_hb = {};
                            college_hb.columnId = "college_hb";
                            college_hb.childColumns = [columns[i]];
                            college_hb.columnRealDesc=zc.reviewfile.step4showtext;
                            college_hb.columnType="A";
                            college_hb.columnWidth=100;
                            college_hb.columnDesc=college_hb.columnRealDesc;
                            college_hb.hintText=college_hb.columnRealDesc;
                            college_hb.editableValidFunc='false';
                            college_hb.locked=false;
                            college_hb.rendererFunc="";
                            college_hb.queryable=false;
                        }
                    }
                }
            }
        }
        var temp = 0;
        for (var i in indexArr){
            columns.splice(indexArr[i]-temp, 1);
            temp++;
        }
        if (checkproficient_hb){
            columns.push(checkproficient_hb);
        }
        if (college_hb){
            columns.push(college_hb);
        }
        if (subject_hb){
            columns.push(subject_hb);
        }
        if (committee_hb){
            columns.push(committee_hb);
        }
		//单独处理三层表头  haosl 2018年8月2日 end
		var tableObj = new BuildTableObj(obj);
		tableObj.setSchemeViewConfig({//配置栏目设置参数
                            publicPlan:true,
                            sum:false,
                            lock:true,
                            merge:false,
                            pageSize:'20'
                        });
        var mainpanel = tableObj.getMainPanel();
        countWin.add(mainpanel);
        var store = Ext.data.StoreManager.lookup(reviewScorecount_me.subModuleId+"_dataStore");
		store.on("update",reviewScorecount_me.saveVoteResult);
	},
	// 增加栏目设置保存后的回调函数
	schemeSaveCallback:function(){
		reviewScorecount_me.init();
	},
	// 单元格中显示文本+title(投票信息)
	getApprovalPersonSetHtml:function(value, columnId, record){
		var html = value;
		
		var type = 0;//1：评委会 2：学科组 3：外部专家 4：学院任聘组
		if("w0553" == columnId || "w0549" == columnId || "w0551" == columnId){
			type = 1;
		} else if("w0547" == columnId || "w0543" == columnId || "w0545" == columnId){
			type = 2;
		} else if("w0531" == columnId || "w0527" == columnId || "w0529" == columnId){
			type = 3;
		} else if("w0567" == columnId || "w0563" == columnId || "w0565" == columnId){
			type = 4;
		}
		
		var expert_state = '';//1：赞成 2：反对 3：弃权
		if('w0553' == columnId || 'w0547' == columnId || 'w0531' == columnId || 'w0567' == columnId){
			expert_state = '1';
		} else if('w0549' == columnId || 'w0543' == columnId || 'w0527' == columnId || 'w0563' == columnId){
			expert_state = '2';
		} else if('w0551' == columnId || 'w0545' == columnId || 'w0529' == columnId || 'w0565' == columnId){
			expert_state = '3';
		}
		
		if(!Ext.isEmpty(value) && value >= 0){// 有投票且不是导入数据时，显示投票结果。
			var w0525 = record.data.w0525;
			
			if(!Ext.isEmpty(w0525) &&  
				((type == 1 && w0525.substring(2, 3) == '0') 
				|| (type == 2 && w0525.substring(1, 2) == '0') 
				|| (type == 3 && w0525.substring(0, 1) == '0') 
				|| (type == 4 && w0525.substring(3, 4) == '0'))){//不是导入数据
					
				var w0501 = record.data.w0501_safe_e;
				var key = w0501+"_"+type+"_"+expert_state;
				var title = JobTitleReviewFile.approvalPersonSet[key];
				if(Ext.isEmpty(title)){
					title = '';
				}
				html = "<span title='"+title+"'>"+value+"</span>";
			}
		}
	
		return html;
	},
	//外部专家，赞成
	w0531:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0531', Record);
	},
	//外部专家，反对
	w0527:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0527', Record);
	},
	//外部专家，弃权
	w0529:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0529', Record);
	},
	//专业学科组，赞成
	w0547:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0547', Record);
	},
	//专业学科组，反对
	w0543:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0543', Record);
	},
	//专业学科组，弃权
	w0545:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0545', Record);
	},
	//评委会，赞成
	w0553:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0553', Record);
	},
	//评委会，反对
	w0549:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0549', Record);
	},
	//评委会，弃权
	w0551:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0551', Record);
	},
	//学院任聘组，赞成
	w0567:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0567', Record);
	},
	//学院任聘组，反对
	w0563:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0563', Record);
	},
	//学院任聘组，弃权
	w0565:function(value, metaData, Record){
		return reviewScorecount_me.getApprovalPersonSetHtml(value, 'w0565', Record);
	},
	//学院聘任组已评数/未评数
	W0571:function (value, metaData, Record){
		var html = '';
		var w0501 = Record.data.w0501;//申报人主键序号
		var w0525  = Record.data.w0525;//导入标志
		var subObject = w0501+"_4";//被调研对象唯一标志
		var planId = Record.data.w0539;//内部评审问卷计划号
		
		var sum = value.split("/")[1];//总人数
		var evaluatedSum = value.split("/")[0];//已评人数
		
		if(evaluatedSum.toString().length == 1){
			evaluatedSum = evaluatedSum+" ";
		}
		if(sum.toString().length == 1){
			sum = " "+sum;
		}
		
		var text = Number(evaluatedSum)+"/"+sum;
		html = text;
		
		return html;
	},
	// 【评审材料word模板】渲染
	w0535:function(value, metaData, Record){
	    //w0536存放的是word模板的内容，将w0536的值显示到评审材料列（w0535）,w0536只用来存放word地址。
        var val = Record.data.w0535;
        var w0536 = Record.data.w0536;
        var html = "";
        if(!Ext.isEmpty(val) || !Ext.isEmpty(w0536)){
            if(jobtitle_reviewconsole.support_word && !Ext.isEmpty(w0536)){// 支持WORD模板
                html = "<a href=javascript:reviewScorecount_me.w0536Show('" + w0536 + "');><img src='/images/new_module/salaryitem.gif' border=0></a>";
            } else {
                html = "<a href=javascript:reviewScorecount_me.checkfile('" + val + "','" + Record.data.nbasea0100_e + "','"+w0536+"');><img src='/images/new_module/salaryitem.gif' border=0></a>";
            }
        }
		return html;
	},
	// 【鉴定专家】渲染
	checkProficient:function(value, metaData, Record){
		var w0523 = value.split("/")[1];
		if(w0523.length == 1)
			w0523 = " " + w0523;
		// 已评人数
		var evaluatedSum = value.split("/")[0];
		if(evaluatedSum.toString().length == 1)
			evaluatedSum = evaluatedSum+" ";
		// "已评审外部鉴定专家人数/外部鉴定专家人数"
		var text = Number(evaluatedSum)+"/"+w0523;
		
		return text;
	},
	// 【状态】渲染
	status:function(value, metaData, Record, d, e){
		//#17CE67绿、#FF0000红
		var color = '';
		var temp = "";
		if(value == '02'){
			color = '#FF0000';
			temp="未通过";
		}else if(value == '01'){
			color = '#17CE67';
			temp = "通过";
		}
		return html = "<label style='color:" + color + "'>" + temp + "</label>"; 
	},
	// 返回按钮
	callback:function(){
		var countWin = Ext.getCmp('countWinid');
	    if(countWin)
	    	countWin.close();
	},
	/**
	 * 保存手动维护的评审人投票结果
	 */
	saveVoteResult:function(store,record,datatype,modifiedFieldNames){
		var map = new HashMap();
		
		var needSave = false;
		var updateColumn = "";
		for(var i in modifiedFieldNames){
			var column = modifiedFieldNames[i];
			//W0533同行评议//W0557专业组//W0559评委会//w0569二级单位
			if(",w0533,w0557,w0559,w0569,".indexOf(","+column+",">-1)){
				needSave = true;
				updateColumn+=column+",";
				map.put(column,Ext.isEmpty(record.data[column])?"":record.data[column]);
			}
		}
		//需要保存时才保存
		if(needSave){
			//提交修改
			record.commit();
			var w0501_e = record.data.w0501_safe_e;
			map.put("w0501_e",w0501_e);
			map.put("opt", "23");
			updateColumn = updateColumn.substring(0,updateColumn.length-1);
			map.put("updateColumn",updateColumn);
		    Rpc({functionId:'ZC00002316',async:false,scope:this}, map);
		}
	},
    exportScoreDetails:function () {
        var map = new HashMap();
        map.put("w0301_e", reviewScorecount_me.w0301_e);
        map.put("review_links", reviewScorecount_me.review_links);
        map.put("evaluationType", reviewScorecount_me.evaluationType);
        map.put("groupids", reviewScorecount_me.groupids);
        map.put("subModuleId", reviewScorecount_me.subModuleId);
        Rpc({
            functionId: 'ZC00002317', async: false, success: function (form) {
                var result = Ext.decode(form.responseText);
                if (result.succeed) {
                    window.location.target = "_blank";
                    window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid=" + result.fileName;
                } else {
                    Ext.showAlert(result.message);//"导出失败！"
                }
            }
        }, map);
    },
    // 评审材料、送审材料
    checkfile:function(path, nbasea0100, w0536){
        if(jobtitle_reviewconsole.support_word && !Ext.isEmpty(w0536)){// 支持WORD模板
            this.w0536Show(w0536);
        } else {
            /** 解析path中的参数 */
            var tabid = "";
            var taskid = "";
            var taskid_validate = "";
            var index = path.indexOf("?");
            var paramStr =  path;
            if(index > -1){
                paramStr = path.substring(index+1);
            }
            var paramArray = new Array();
            paramArray = paramStr.split('&');
            for(var i=0; i<paramArray.length; i++){
                var param = paramArray[i];
                var key = param.split('=')[0];
                if(key == 'tabid'){
                    tabid = param.split('=')[1];
                } else if(key == 'taskid'){
                    taskid = param.split('=')[1];
                } else if(key == 'taskid_validate'){
                    taskid_validate = param.split('=')[1];
                }
            }
            if(Ext.isEmpty(taskid_validate)){// 获取taskid的校验code
                var map = new HashMap();
                map.put("type", '2');
                map.put("taskid", taskid);
                Rpc({functionId:'ZC00003022',async:false,success:function(res){
                    var result = Ext.decode(res.responseText);
                    taskid_validate = result.taskid_validate;

                }},map);
            }
            // 配置参数
            var obj={};
            obj.module_id="11";////调用模块标记：职称模块
            obj.return_flag="14";//返回模块标记：不需要返回关闭按钮
            obj.tab_id=tabid;//模板号
            obj.task_id=taskid;//任务号 除0以外需加密
            obj.approve_flag="0";//不启用审批
            obj.view_type="card";//卡片模式
            obj.card_view_type="1";//卡片模式下不要显示左边导航树
            obj.other_param="visible_title=0`visible_toolbar=0`object_id="+nbasea0100+"`taskid_validate="+taskid_validate;;//visible_title=0:不需要标题； visible_toolbar=0：不要按钮；object_id：对象nbase+a0100
            obj.callBack_init="reviewScorecount_me.showView";

            //获取业务模板名称
            var map = new HashMap();
            map.put("tabId", tabid);
            Rpc({functionId:'ZC00003018',async:false,success:function(){
                var result = Ext.decode(arguments[0].responseText);
                this.tabName = result.tabName;
                // 调用人事异动模板
                createTemplateForm(obj);
            },scope:this},map);
        }
    },
    // 显示人事异动模板
    showView:function(){
        var container = Ext.create('Ext.container.Container', {
            region: 'center',
            layout: 'fit',
            border: false,
            items: [templateMain_me.mainPanel]
        });
        var height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
        var width = window.parent.window.document.getElementById('center_iframe').offsetWidth;
        var reviewfile_showfile_win = Ext.create('Ext.window.Window', {
            title:reviewScorecount_me.tabName,
            id:'reviewfile_showfile_win',
            layout: 'border',
            modal: true,
            width:width,
            height:height,
            border:false,
            autoScroll:false,
            items: [container]
        }).show();
        if(reviewfile_showfile_win){
            window.onresize=function(){
                var height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
                var width = window.parent.window.document.getElementById('center_iframe').offsetWidth;
                reviewfile_showfile_win.setWidth(width);
                reviewfile_showfile_win.setHeight(height);
            }
        }
    },
    w0536Show:function(w0536){
		var servletpath = '/servlet/DisplayOleContent?filePath='+w0536+'&bencrypt=true'+'&openflag=true';
		var height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
		var width = window.parent.window.document.getElementById('center_iframe').offsetWidth;
		Ext.create('Ext.window.Window',{
	  		title:'申报材料',
	       	layout:'fit',
	        modal: true,
	        resizable: false,  
	        border:false,
	  		closeToolText : '',
	       	items:[{
	            xtype: 'panel',
	            border:false,
	           	html:'<iframe src="'+servletpath+'" width="'+(width-10)+'" height="'+(height-40)+'"></iframe>'
	        }]
		}).show();
	},
});
