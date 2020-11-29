var basic = basic || {};
basic.biz = {};
basic.biz.tmp = basic.biz.tmp || {}; // 存放临时对象，主要用于参数的传递
var re_scoring = false;
basic.biz.isEvaluationEdited = false;//上级评价是否被修改(当点击发布/删除时,认为被修改.置为true,用于向计划页面传递参数,是否重新渲染上级)

/** 切换待评价任务的开关状态 */
basic.biz.toggleEvaluate = function(checkbox) {
	if (checkbox.disabled) {
		return;
	}
	if (checkbox.checked) {
		document.getElementById("rankContainer").style.display = "table-row";
	} else {
		document.getElementById("rankContainer").style.display = "none";
	}
};

/** 保存之前关于权重的检查 */
basic.biz.beforeSave = function() {
	var msg;

	var hashvo = new ParameterSet();
	hashvo.setValue("action", "before");
	var rankNode = document.getElementById("edit-rank");
	if (rankNode) {
		hashvo.setValue("rank", rankNode.value);
	}
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	new Request({
				asynchronous : false,
				onSuccess : function(data) {
					msg = data.getValue("msg");
				},
				functionId : '9028000752'
			}, hashvo);
	return msg;
};

/** 保存指定的元素的值 
 *  taskEditFlag是点击任务详情页面外时,任务处于编辑状态(此时taskEditFlag=noReturn),对于已修改数据做的挽救保存,
 *  对于数据的校验,如果数据校验未通过,将其value置为"asOld",并在后台进行判断,某值为asOld时,不做更新处理
 *  
 *  */
basic.biz.save = function(elmt, taskEditFlag, isFromParent) {
	var rankNode = document.getElementById("edit-rank");
	if(taskEditFlag != "noReturn"){
		if (!basic.biz.validate(Ext.query("input[validator][id^=edit]"))) { // 校验未通过
			return;
		}
	}

	/**
	 * ################################# 保存前对权重做特殊检查
	 * ##########################################
	 */
	var toEvaluate = document.getElementById("toEvaluate");
	if (!!toEvaluate && !!rankNode) {
		if (toEvaluate.checked
				&& (rankNode.value === "0" || rankNode.value === "")) {
			if(taskEditFlag != "noReturn"){
				var position = getMsgPosition();
				//confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
				var scrollTop = parent.window.Ext.getBody().getScroll().top;
				Ext.Msg.alert("提示信息", "当前任务已设置为需要考核，必须设置权重！").alignTo(Ext.getBody(),"tl",[position.x,position.y]);
				//恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
				if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
					window.parent.document.documentElement.scrollTop = scrollTop;
				}else {//chrome、safari
					parent.window.Ext.getBody().setScrollTop(scrollTop);
				}
				return;
			}else{
				rankNode.value = "asOld";		
			}
		}
	}
	if(toEvaluate){
		toEvaluate.disabled = true;// 非编辑状态下不可勾选
	}
	// 判断输入的权重是否大于100，并且为整数
	if (rankNode.value != "") {
		var rank = Ext.num(rankNode.value, -1);
		if ((rank > 100) || (rank < 0)) {
			if(taskEditFlag != "noReturn"){
				Ext.Msg.alert("提示信息", "权重必须为数值型,范围为0-100");
				return;
			}else{
				rank = "asOld";
			}
		}
		var checkfloat = rank.toString().split(".");
		if (checkfloat[1]) {
			if(taskEditFlag != "noReturn"){
				Ext.Msg.alert("提示信息", "权重只能输入整数");
				return;
			}else{
				rank = "asOld";
			}
		}
	}

	// 保存之前的请求:关于上下级任务权重的提示信息
	if(taskEditFlag != "noReturn"){
		var msg = basic.biz.beforeSave();
		if (msg && !confirm(msg)) {
			return;
		}
	}
	

	/**
	 * ################################# 保存
	 * ##########################################
	 */
	var values = ""; // field1=value1|field2=value2|...
	var p0803s = document.getElementById("p0803s");
	var p0837s = document.getElementById("p0837s");
	var p0841s = document.getElementById("p0841s");
	// 验证日期合法标识
	var checkDate = true;
	if (!elmt) { // 指定元素为空，表示全部保存
		var normal = Ext.query("[edit=normal]");
		basic.global.every(normal, function(node) {
			var field; // director
			var value;
			switch (node.getAttribute("fieldType")) {
				case "prompt" : // 提示框,属性[edit=normal]在目标元素div上
					if (node.id === "addFollower") {
						break;
					}
					field = node.id.substring(5);
					value = Ext.query("input", node)[0].value || "";
					break;
				case "progress" :
					field = "p0835"; // 将进度加入参数中
					value = document.getElementById("p0835").innerHTML;
					break;
				case "Code" : // 代码类型的控件
					field = node.id;
					value = document.getElementById("store-" + node.id).value;
					break;
				default : // 其它普通的编辑元素,属性[edit=normal]在描述节点(文本节点)上
					field = node.id;
					var editElement = document
							.getElementById("edit-" + node.id);
					value = basic.global.trim(editElement.value);
					if (field == "p0801") {
						value = basic.biz.checkTaskName(value);
					}
					if (editElement.getAttribute("placeholder") === value)
						value = "";

					if (node.id === "rank") {
						if (!toEvaluate.checked)//
							value = "0";
					}
					break;
			}
			if (node.id == "p0803") {
				p0803s.value = value;
			}
			if (node.id == "p0837") {
				p0837s.value = value;
			}
			if (node.id == "p0841") {
				p0841s.value = value;
			}
			if (node.id == "p0813") {
				if (!workPlanCheckDate(value)) {
					if(taskEditFlag != "noReturn"){
						checkDate = false;
					}else{
						value = "asOld";
					}
				}
			}
			if (node.id == "p0815") {
				if (!workPlanCheckDate(value)) {
					if(taskEditFlag != "noReturn"){
						checkDate = false;
					}else{
						value = "asOld";
					}
				}
			}
			value = value || "";
			value = value.replace(/`/g, ""); // “`”被用来做字符串的分隔符，所以不允许将输入的`传入后台
			value = value.replace(/\r\n/g, "^");
			if ((field == "p0803" && value == "双击填写任务描述或关键行动")
					|| (field == "p0841" && value == "双击填写评价标准")
					|| (field == "p0837" && value == "双击填写进度说明")) {
				value = "";
			}
			if (field) {
				values += field + "=" + value + "`";
			}
				// if(field && (node.id == "p0803")){
				// values += field + "=" + p0803s.value + "`";
				// }
			});
	} else { // onchange事件注册输入框上，此时的参数是该输入框
		if (elmt.getAttribute("fieldType") === "progress") { // 进度条
			var field = elmt.getAttribute("fieldName"); // 进度的字段名和字段值在保存之前被设定到滚动条div上
			var value = elmt.getAttribute("fieldValue");
			values = field + "=" + value;
		} else {
			if (elmt.getAttribute("validator") && !basic.biz.validate([elmt])) { // 有校验器且校验失败时，返回
				return;
			}
			var field = elmt.id.substring(5); // edit-rank: 取“edit-”之后的部分
			var value = elmt.innerHTML || elmt.value;
			values = field + "=" + value;
		}
	}

	// 保存的请求
	// alert(document.getElementById("toEvaluate").checked);
	// alert(checkDate);
	if (!checkDate) {
		return;
	}
	var hashvo = new ParameterSet();
	hashvo.setValue("values", getEncodeStr(values));
	hashvo.setValue("taskEditFlag", taskEditFlag);
	hashvo.setValue("dispatchFlag", rankNode.value)
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	new Request({
				asynchronous : false,
				onSuccess : function(data) {
					if (!elmt) {
						basic.util.endEdit();
					}
		
					var p0801 = data.getValue("p0801");
					// var p0803=data.getValue("p0803");
					// var p0837=data.getValue("p0837");
					document.getElementById("p0801").title = p0801;// 保存完毕更新title的值
					//兼容IE支持placeHolder haosl 20160908
					document.getElementById("edit-p0803").innerText = (p0803s.value==""?"双击填写任务描述或关键行动":p0803s.value);
					//兼容IE支持placeHolder haosl 20160908
					document.getElementById("edit-p0837").innerText = (p0837s.value==""?"双击填写进度说明":p0837s.value);
					if (document.getElementById("toEvaluate").checked) {
						//兼容IE支持placeHolder haosl 20160908
						document.getElementById("edit-p0841").innerText = (p0841s.value==""?"双击填写评价标准":p0841s.value);
					}
					basic.biz.tmp.privilege = data.getValue("privilege");
					basic.biz.refreshTaskButtons();
					var Ele_addDivMsg1 = document.getElementById("addDivMsg1");
					if (Ele_addDivMsg1) {
						Ele_addDivMsg1.style.display = "none";
					}
					initTextarea();
					// 父页面调用是否刷新方法
					var Eleframe = window.parent.document
							.getElementById("taskFrame");
					if (Eleframe) {
						if ("planPage" == Eleframe.getAttribute("name")) {
							var p0700 = document.getElementById("param.p0700").value;
							var p0800 = document.getElementById("param.p0800").value;
							var p0723 = document.getElementById("param.p0723").value;
							var recordId = document.getElementById("param.recordId").value;
							isNeedToRefresh(p0700,p0800,p0723,recordId);
						}
						if ("summaryPage" == Eleframe.getAttribute("name")) {
							window.parent.window
									.getMyWorkTaskList(parent.task_type);
							window.parent.window.showtask(true);
						}
					}
					document.getElementById("editStartTime").style.display = "none";
					document.getElementById("editEndTime").style.display = "none";

					// 刷新当前任务进度或任务评价或任务日志显示框
					var show2 = document.getElementById("taskEvaluation")
							.getAttribute("show");
					var show3 = document.getElementById("operationlog")
							.getAttribute("show");
					if (show2 == "true")
						basic.biz.getEvaluation();
					else if (show3 == "true")
						basic.biz.operationLog();
					else{
						basic.biz.getProgress();
					}
					// 完成进度的进度条恢复到真正的值 chent 20160321 start
					var bar = Ext.getDom("progressBar");
					if ("always" === bar.getAttribute("edit")) { // always型编辑字段即点即存
						bar.setAttribute("fieldValue", basic.biz.tmp.p0835);
						Ext.getCmp('workplan_progressBar').setValue(basic.biz.tmp.p0835);
						Ext.getDom('progressBarTip').innerHTML = basic.biz.tmp.p0835+'%';
					}
					// 完成进度的进度条恢复到真正的值 chent 20160321 end
					if(isFromParent){//点击工作计划页面任意位置会任务详情页面，如果是编辑状态，则先保存，再回调关闭，ie9下会死掉 chent 20160324
						//如果页面没有保存完，关闭也会ie9会死掉 20161018 
						 setInterval(function(){parent.window.closeTaskEdit();},100);
					}
					//刷新表格视图  haosl 2017-07-29
					if(window.parent.refreshtreepanel)
						window.parent.refreshtreepanel();
				},
				functionId : '9028000752'
			}, hashvo);

	// basic.biz.operationLog();
};

/** 编辑 */
basic.biz.edit = function() {
	document.getElementById("cancelTask").style.display = "none";
	document.getElementById("delTask").style.display = "none";
	document.getElementById("editTask").style.display = "none";
	// document.getElementById("releaseTask").style.display = "none";
	document.getElementById("endEdit").style.display = "inline";
	document.getElementById("saveTask").style.display = "inline";
	document.getElementById("editStartTime").style.display = "inline";
	document.getElementById("editEndTime").style.display = "inline";
	document.getElementById("toEvaluate").disabled = false;
	document.getElementById("toEvaluate").disabled = false;
	var p0813Ele = document.getElementById("p0813");
	if (p0813Ele.getAttribute("edit") == "normal") {
		document.getElementById("char_zhi").innerHTML = "&nbsp;至&nbsp;";
	}
	basic.util.doEdit();
};

/** 结束编辑 */
basic.biz.revert = function() {
	document.getElementById("editStartTime").style.display = "none";
	document.getElementById("editEndTime").style.display = "none";
	basic.biz.refreshTaskButtons();
	// basic.util.endEdit();
	location.reload();
};
// =============================================================================
/** 取消任务 */
basic.biz.cancelOrDelete = function(action) {
	var hashvo = new ParameterSet();
	hashvo.setValue("action", action);
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	// 移除部门计划wusy
	if (action === "delDeptTask") {
		var objectid = document.getElementById("param.objectid").value;
		var p0800 = document.getElementById("param.p0800").value;
		hashvo.setValue("objectid", objectid);
		hashvo.setValue("p0800", p0800);
		function showResulta(btn) {
			if (btn == 'yes') {
				new Request({
					asynchronous : true,
					onSuccess : function(data) {
						window.parent.document.getElementById("taskFrame").style.display = "none";
						window.parent.getPlanTaskList();
					},
					functionId : '9028000756'
				}, hashvo);
			} else {
				return;
			}
		};
		Ext.MessageBox.confirm('提示信息', '确认移出部门计划吗？', showResulta).alignTo(
				Ext.getBody(), 't', [-100, 300]);
		// alignTo,弹窗相对位置调整
	}
	if (action === "cancel") {
		/** 弹出信息位置重新计算 chent 20160319 start */
		var position = getMsgPosition();
		//confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
		var scrollTop = parent.window.Ext.getBody().getScroll().top;
		
		Ext.Msg.confirm("提示信息","确认取消任务吗?",function(e){
			if (e == "yes") {
				new Request({
							asynchronous : true,
							onSuccess : function(data) {
								if (action === "cancel") {
									window.location.reload();
								} else if (action === "delete") {
									window.location.href = document
											.getElementById("goBack").href; // 回到计划页面
								}
							},
							functionId : '9028000756'
						}, hashvo);
			} else {
				return;
			}
		}).alignTo(Ext.getBody(),"tl",[position.x,position.y]);
		
		//恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
		if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
			window.parent.document.documentElement.scrollTop = scrollTop;
		}else {//chrome、safari
			parent.window.Ext.getBody().setScrollTop(scrollTop);
		}
		/** 弹出信息位置重新计算 chent 20160319 end */
	}

	else if (action === "delete") {
		var p0800 = document.getElementById("param.p0800").value;
		var othertask = document.getElementById("param.othertask").value;//lis 20160323
		var p0700 = document.getElementById("param.p0700").value;
		if (basic.biz.checkSubTask(p0800+"_"+othertask)) {
			Ext.MessageBox.show({
				title : "删除任务",
				msg : "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp该任务有子任务，是否同时删除子任务?",
				buttons : Ext.Msg.YESNOCANCEL,
				fn : function(e) {
					if (e == "no") {
						var hashvo = new ParameterSet();
						hashvo.setValue("oprType", "delTask");
						hashvo.setValue("deltype", "withoutChild");
						hashvo.setValue("plan_id", p0700);
						hashvo.setValue("task_ids", p0800+"_"+othertask);
						var request = new Request({
							method : 'post',
							asynchronous : false,
							onSuccess : function(data) {
								// 删除任务后,退回到主页面并刷新任务栏
								parent.document.getElementById("taskFrame").style.display = "none";
								var planPage = parent.document
										.getElementsByName("planPage");
								if (planPage[0]) {
									parent.getPlanTaskList();
								} else {
									parent.location.reload();
								}
								// window.location.href = document
								// .getElementById("goBack").href;
							},
							functionId : '9028000702'
						}, hashvo);
					} else if (e == "yes") {
						var hashvo = new ParameterSet();
						hashvo.setValue("action", action);
						basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
						new Request({
							asynchronous : false,
							onSuccess : function(data) {
								if (action === "cancel") {
									window.location.reload();
								} else if (action === "delete") {
									// 删除任务后,退回到主页面并刷新任务栏
									parent.document.getElementById("taskFrame").style.display = "none";
									var planPage = parent.document
											.getElementsByName("planPage");
									if (planPage[0]) {
										parent.getPlanTaskList();
									} else {
										parent.location.reload();
									}
									// window.location.href = document
									// .getElementById("goBack").href; // 回到计划页面
								}
							},
							functionId : '9028000756'
						}, hashvo);
					} else {
						return;
					}

				},
				width : 400,
				icon : Ext.MessageBox.INFO
			});
		} else {
			/*
			 * if (!confirm("确认删除吗?")) { return; } new Request( { asynchronous :
			 * true, onSuccess : function(data) { if (action === "cancel") {
			 * window.location.reload(); } else if (action === "delete") {
			 * window.location.href = document .getElementById("goBack").href; //
			 * 回到计划页面 } }, functionId : '9028000756' }, hashvo);
			 */

			function showResult(btn) {
				if (btn == 'yes') {
					new Request({
						asynchronous : true,
						onSuccess : function(data) {
							if (action === "cancel") {
								window.location.reload();
							} else if (action === "delete") {
								parent.document.getElementById("taskFrame").style.display = "none";
								parent.getPlanTaskList();
								// window.location.href = document
								// .getElementById("goBack").href; // 回到计划页面
							}
						},
						functionId : '9028000756'
					}, hashvo);
				} else {
					return;
				}
			};
			/** 弹出信息位置重新计算 chent 20160319 start */
			var position = getMsgPosition();
			//confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
			var scrollTop = parent.window.Ext.getBody().getScroll().top;
			
			Ext.Msg.confirm("提示信息","确认删除吗？",showResult).alignTo(Ext.getBody(),"tl",[position.x,position.y]);
			
			//恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
			if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
				window.parent.document.documentElement.scrollTop = scrollTop;
			}else {//chrome、safari
				parent.window.Ext.getBody().setScrollTop(scrollTop);
			}
			/** 弹出信息位置重新计算 chent 20160319 end */
		}
	}
};
basic.biz.checkSubTask = function(to_deleted_ids) {
	var hashvo = new ParameterSet();
	var p0700 = document.getElementById("param.p0700").value;
	var temp = false;
	hashvo.setValue("oprType", "checkSubTask");
	hashvo.setValue("plan_id", p0700);
	hashvo.setValue("task_ids", to_deleted_ids);
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : isHave,
				functionId : '9028000702'
			}, hashvo);
	function isHave(outparamters) {
		if (outparamters) {
			temp = (outparamters.getValue("flag") == "true");// 这样处理可以保证方法返回值是布尔类型的
		}
	}
	return temp;
}
/*
 * function delTask_ok(outparamters) { var info = outparamters.getValue("info");
 * info=getDecodeStr(info); if (info!=""){ alert(info); } else {
 * deleteRow(wpm.store.getRootNode()); resetSeq(wpm.store.getRootNode(),"");
 * //更改合计权重 var sum_rank = outparamters.getValue("sum_rank");
 * updateSumRank(sum_rank); } }
 */
// ===========================================================================================================
/**
 * 根据任务状态和审批状态选择性隐藏任务操作按钮:删除,取消,编辑 p0809 任务状态 (1:未开始, 2:进行中, 3:完成, 4:暂缓, 5:取消)
 * p0811 审批状态(任务) (01:起草, 02:已报批, 03:已批, 07:驳回, 10:废除) p0719 审批状态(计划) (0:起草,
 * 1:报批, 2:已批, 3:驳回, 4:启动【评估】, 5结束)
 */

basic.biz.refreshTaskButtons = function() {
	var taskBtns = Ext.query("#taskBtns a");
	taskBtns[taskBtns.length] = document.getElementById("transit");
	basic.global.every(taskBtns, function(anchor) {
				// if (anchor.id !== "goBack") {
				// anchor.style.display = "none";
				// }
				if (!anchor) {
					return;
				}
				if (anchor.getAttribute("visible") !== "true") {
					anchor.style.display = "none";
				}
			});

	var p = basic.biz.tmp.privilege;
	if (!p) {
		return;
	}

	if (p.deletable === "true") { // 删除按钮
		document.getElementById("delTask").style.display = "inline";
	}
	// 设置显示移除部门计划按钮wusy
	if (p.delDeptTask === "true") {// 移除部门计划按钮
		document.getElementById("delDeptTask").style.display = "inline";
	}
	if (p.cancelable === "true") { // 取消按钮
		document.getElementById("cancelTask").style.display = "inline";
	}
	if (p.editable === "true") { // 编辑按钮
		document.getElementById("editTask").style.display = "inline";
	}
	if (p.approveBtn) { // 报批或批准按钮
		document.getElementById("transit").style.display = "inline-block";
		//var img = Ext.query("img", document.getElementById("transit"))[0];
		var img = document.getElementById("transit_img");//lis 2016-03-10
		if (img) {
			if (p.approveBtn === "submit") {
				img.src = "/workplan/image/baopi.png";
			} else if (p.approveBtn === "approve") {
				img.src = "/workplan/image/pizhun.png";
			}
		}
	}

	if (p.taskChangedStatus === "0") { // 任务变更描述
		document.getElementById("taskChangedDesc").innerHTML = "";
	} else if (p.taskChangedDesc) {
		document.getElementById("taskChangedDesc").innerHTML = "（"
				+ p.taskChangedDesc + "）";
	}
	if (p.taskExecuteDesc) { // 任务住行状态描述
		document.getElementById("p0809").innerHTML = p.taskExecuteDesc;
	}
	if (p.taskExecuteStatus === "5") { // 任务被取消
		document.getElementById("p0835").innerHTML = "";
		document.getElementById("percentSign").style.display = "none";
	}
	if (p.taseExecuteDesc) { // 任务执行状态描述
		document.getElementById("transit").innerHTML = p.btnDesc;
	}
};

/** 添加子任务 */
basic.biz.addSubTask = function() {
	
	var personCF = Ext.getDom("param.personCF").value;
	var period_type = Ext.getDom("param.period_type").value;
	if(personCF == "false"){
		if (period_type == '1') {// 年
    		str = "年计划";
		} else if (period_type == '2') {// 半年
			str = "半年计划";
		} else if (period_type == '3') {// 季度
			str = "季度计划";
		} else if (period_type == '4') {// 月
			str = "月计划";
		} else if (period_type == '5') {// 周
			str = "周计划";
		}
		Ext.showAlert('您没有填写'+str+'的权限！');
		return;
	}
	var isCanFill = Ext.getDom("param.isCanFill").value;
	if(isCanFill=="false"){
		Ext.showAlert('计划不在填报期限内,不能填报！');
		return;
	}
	
	var inputs = Ext.query(".hj-zm-four-tianjia input[validator]");
	if (!basic.biz.validate(inputs)) { // 校验未通过
		return;
	}
	
	var task = document.getElementById("add-subtask-name");
	var director = Ext.query("#subtask-director")[0];
	var p0813 = document.getElementById("add-subtask-p0813");
	var p0815 = document.getElementById("add-subtask-p0815");
	var phP0813 = p0813.getAttribute("placeHolder");
	var phP0815 = p0815.getAttribute("placeHolder");
	var p0813v = p0813.value == phP0813 ? "" : p0813.value;
	var p0815v = p0815.value == phP0815 ? "" : p0815.value;
	// 验证日期格式
	if (!workPlanCheckDate(p0813v) || !workPlanCheckDate(p0815v)) {
		return;
	}
	//------获取父任务页面任务seq  start-----
	var selModel = parent.g_tree.getSelectionModel();
	parent.wpm.selNode = selModel.getLastSelected();
	var parentid = "";
	var seq = "";
	var othertask = "";
	var directorId = "";
	directorId = Ext.query("input", [],director)[0].value;
	if (parent.wpm.selNode != null) {
		othertask = parent.wpm.selNode.get('othertask');
		if("1" == othertask){//是穿透任务
			directorId = parent.wpm.selNode.get('objectid');
		}
		parentid = parent.wpm.selNode.get('p0800');
		if (parentid == "") {
			parent.wpm.selNode = parent.wpm.store.getRootNode();
		} else {
			seq = parent.wpm.selNode.get('seq');
		}
	} else {
		parent.wpm.selNode = parent.wpm.store.getRootNode();
	}
	var count = parent.wpm.selNode.childNodes.length + 1;
	if (seq != "")
		seq = seq + "." + count;
	else
		seq = count;
	if (parent.wpm.hasSumRow && (parent.wpm.selNode === parent.wpm.store.getRootNode())) {
		seq = count - 1;
	}
	
	//------获取父任务页面任务seq  end-------
	var map = new HashMap();
	map.put("taskName", getEncodeStr(task.value));
	map.put("director", directorId);
	map.put("p0813", getEncodeStr(p0813v));
	map.put("p0815", getEncodeStr(p0815v));
	map.put("p0700", getEncodeStr("1"));
	map.put("superiorEdit", superiorEdit+"");
	//将获取到的父任务页的seq放入map
	map.put("task_seq", seq+"");
	basic.biz.prepareStaticMapData(map); // p0700,p0723,p0800,objectid
	Rpc({functionId:'9028000753',async:false,success:function(response) {
		var result = Ext.decode(response.responseText); 
		if(result.succeed){
			var bean = result.subtask;
			if (bean) {
				basic.biz.createSubtaskTr(bean);
				//任务也添加子任务成功,计划页实时更新
				window.parent.window.addTask_ok(response);
			}
			
			task.value = "";
			if (basic.global.isIE()) {
				basic.global.refreshInputPlaceHolder(); // 将占位符显示出来
			}
		}else{
			var position = getMsgPosition();
			//confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
			var scrollTop = parent.window.Ext.getBody().getScroll().top;
			Ext.Msg.alert('提示信息',result.message).alignTo(Ext.getBody(),"tl",[position.x,position.y]);
			//恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
			if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
				window.parent.document.documentElement.scrollTop = scrollTop;
			}else {//chrome、safari
				parent.window.Ext.getBody().setScrollTop(scrollTop);
			}
		}	
	}},map);
	/*new Request({
				asynchronous : false,
				onSuccess : function(data) {
					var bean = data.getValue("subTask");
					if (bean) {
						basic.biz.createSubtaskTr(bean);
						//任务也添加子任务成功,计划页实时更新
						window.parent.window.addTask_ok(data);
					}

					task.value = "";
					*//**
					 * 除子任务名称外，其它信息保留 Ext.query("input", director)[0].value =
					 * ""; basic.util.reRender(director); p0813.value = "";
					 * p0815.value = ""; // 给子任务起始时间添加默认值 var d = new Date();
					 * document.getElementById("add-subtask-p0813").value =
					 * d.getFullYear() + "." + (d.getMonth() + 1) + "." +
					 * d.getDate();
					 *//*

					if (basic.global.isIE()) {
						basic.global.refreshInputPlaceHolder(); // 将占位符显示出来
					}
				},
				functionId : '9028000753'
			}, hashvo);*/
};

// 负责人、参与人和添加子任务的负责人元素的点击事件
basic.biz.popPrompt = function(e, target) {
	basic.prompt.openPrompt({
				beforeOpen : function() {
					basic.util.refreshPromptPlaceHolder(target); // 刷新目标绑定的占位符
				},
				check : function() {
					var edit = target.getAttribute("edit"); // 不可编辑则不执行任何操作
					if (edit === null || edit === "none"
							|| target.getAttribute("status") === "done") {
						return false;
					} else {
						return true;
					}
				},
				afterPickUp : basic.util.renderSelectedItem,
				search : basic.util.searchFromDB
			}, target);

};

/**
 * 创建子任务tr
 * <tr>
 * <td>刘蒙</td>
 * <td>社会主义和谐社会建设</td>
 * <td style="border-right: none;">2014年上半年</td>
 * </tr>
 */
basic.biz.createSubtaskTr = function(bean, p0700, p0800, p0723, objectid) {
	var subtask = document.getElementById("subtask");

	var tr = document.createElement("tr");

	var subtaskName = document.createElement("td");
	var div = document.createElement("div");
	div.title = getDecodeStr(bean["subtaskName"]);
	div.style.paddingRight = "7px";
	div.style.width = "300px";
	div.innerHTML = getDecodeStr(bean["subtaskName"]);
	subtaskName.appendChild(div);

	var director = document.createElement("td");
	director.innerHTML = bean["director"];

	var timeDesc = document.createElement("td");
	timeDesc.innerHTML = bean["timeDesc"];

	tr.appendChild(subtaskName);
	tr.appendChild(director);
	tr.appendChild(timeDesc);
	
	//没有计划填写权限，或者不在计划填报期限内，不允许穿透进去
	var personCF = Ext.getDom("param.personCF").value;
	var isCanFill = Ext.getDom("param.isCanFill").value;
	if(personCF=="false" || isCanFill=="false"){
		tr.style.backgroundColor="#FFFFFF"; 
		tr.style.cursor="auto"; 
	}
	if(personCF!="false" && isCanFill!="false"){
		tr.onclick = function() {
			var p0800 = document.getElementById("param.p0800").value;
			var taskreturn_url = document.getElementById("param.taskreturnurl").value;
			var return_url = document.getElementById("param.returnurl").value;
			taskreturn_url = taskreturn_url + "," + p0800;
			var url = bean["url"];
			url = url + "&taskreturnurl=" + taskreturn_url + "&returnurl="
					+ return_url;
			window.location.href = url;
		};
	}

	subtask.appendChild(tr);

	/*
	 * 暂不需要，但是为防万一，这段代码暂时保留 // 修正任务名称长度 var tdWidth = subtaskName.clientWidth;
	 * basic.tmpTdWidth = basic.tmpTdWidth || tdWidth; // 以td里内容为空时td的宽度为准
	 * 
	 * var len = bean["subtaskName"].length; var strWidth =
	 * basic.util.strWidth(bean["subtaskName"]).w; if (basic.tmpTdWidth <
	 * strWidth) { var l; if (Math.ceil(strWidth / len) >= 7) { l =
	 * basic.tmpTdWidth / 14.3; } else { l = basic.tmpTdWidth / 6.5; }
	 * subtaskName.innerHTML = bean["subtaskName"].substring(0, l) + "..."; }
	 * else { subtaskName.innerHTML = bean["subtaskName"]; }
	 */
};

/** 将四个参数:objectid,p0700,p0800,p0723传入后台 */
basic.biz.prepareStaticData = function(vo) {
	var hiddens = Ext.query("[type=hidden][id^=param]");
	basic.global.every(hiddens, function(hidden) {
				vo.setValue(hidden.id.substring(6), hidden.value);
			});
};

/** 将参数:objectid,p0700,p0800,p0723传入后台 */
basic.biz.prepareStaticMapData = function(map) {
	var hiddens = Ext.query("[type=hidden][id^=param]");
	basic.global.every(hiddens, function(hidden) {
					map.put(hidden.id.substring(6), hidden.value+"");
			});
};

/** 校验 */
basic.biz.validate = function(inputs) {
	inputs = inputs || [];

	var invalidInputs = [];

	basic.global.every(inputs, function(input) {
				var validator = input.getAttribute("validator");
				if (!validator) {
					return true;
				}
				
				if (!basic.biz[validator].call(null, input)) {
					invalidInputs[invalidInputs.length] = input;
				}
			});

	if (!invalidInputs.length) {
		return true;
	} else {
		var msg = "以下项目校验未通过:</br>";
		basic.global.every(invalidInputs, function(input, index) {
					msg += (index + 1) + "、" + input.getAttribute("desc")
							+ ": " + input.getAttribute("tip") + "</br>";
				});
		Ext.Msg.alert("提示信息", msg);
		// 验证不通过弹出警告后需将光标定位到输入框内
		invalidInputs[0].focus();
		// invalidInputs[0].select();
		return false;
	}
};

/** 非空验证 */
basic.biz.notnull = function(input) {
	var value = basic.global.trim(input.value || "");
	var ph = input.getAttribute("placeHolder");
	if (!ph) { // 没有占位符
		return !!value;
	} else {
		return (value !== ph && value !== "");
	}
};

/** 数字验证 */
basic.biz.regex = function(input) {
	var value = basic.global.trim(input.value || "");
	value = value === input.getAttribute("placeHolder") ? "" : value;

	if (!value) {
		return true;
	}

	var regexp = input.getAttribute("regexp") || "";
	var reg = new RegExp(regexp);

	return reg.test(value);
};

/** 发布任务 */
basic.biz.releaseTask = function() {
	var hashvo = new ParameterSet();
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	new Request({
				asynchronous : false,
				onSuccess : function(data) {
					document.getElementById("p0811").value = data
							.getValue("p0811");
					basic.biz.refreshTaskButtons();
				},
				functionId : '9028000757'
			}, hashvo);
}

/** ################################### 周报 ############################## */
/** 创建没有工作总结的报告(日,周,月,季) */
basic.biz.getReportNode = function(report) {
	report = report || {};

	var div = document.createElement("div");
	if (report.hasSummary === "true") {
		/*
		 * <div class="has-summary"> <p> <span>周报</span><a href="#">2014年5月第三周</a>
		 * <img src="image/xingxing.png" /> </p> <h2>本周工作总结</h2> <div>
		 * 1.楼市景气不见好转，不断有城市松绑“限购”，个别业主也开始失去信心“弃房断供”。
		 * 2.21世纪经济报道记者了解到，总部设于广东的某股份行杭州西湖支行近期起诉了同一楼盘的三位业主 </div> <h2>下周工作计划</h2>
		 * <div> 1.2010年该行向业主章某某发放了310万元的贷款用于购买该房屋，贷款期限20年，贷款利率为基准上浮10%。
		 * 2.不过从法律的角度，因为房价下跌而“弃房”拒还银行贷款似乎并不可取。 </div> </div>
		 */
		var div = document.createElement("div");
		div.className = "has-summary";
		var summary = getDecodeStr(report.summary).replace(/\n/g, '<br>');
		var plan = getDecodeStr(report.plan).replace(/\n/g, '<br>');
		var html = "";
		html += "<p>";
		html += "<span class='reportType'>" + report.typeDesc
				+ "报</span><a href='" + report.viewUrl + "'>" + report.period
				+ "</a>";
		html += "<span id='score-" + report.p0100 + "'></span>";
		html += "</p>";
		html += "<h2>本期工作总结</h2>";
		html += "<div>" + summary + "</div>";
		html += "<h2>下期工作计划</h2>";
		html += "<div>" + plan + "</div>";

		div.innerHTML = html;
	} else { // 没有总结
		/*
		 * <div class="no-summary"> <p><span>周报</span><a href="#">2014年5月第四周</a></p>
		 * <p><span>未提交</span><a href="#">提醒写工作总结</a></p> </div>
		 */
		div.className = "no-summary";
		var html = "";
		html += "<p><span class='reportType'>" + report.typeDesc
				+ "报</span><a href='" + report.viewUrl + "'>" + report.period
				+ "</a></p>";
		html += "<p><span class='reportType'>未提交</span>";
		if (report.type === "param") { // 类型为参数，表示需要调用发送提醒邮件函数
			html += "";
		} else if (report.type === "url") { // 类型为链接，表示跳转至总结界面填写总结
			html += "<a href='" + report.remindUrl + "'>" + report.remindText
					+ "</a>";
		}
		html += "</p>";

		div.innerHTML = html;
	}

	return div;
};

/** 获得报告周期的描述 */
basic.biz.getCycleDesc = function(cycle) {
	cycle = typeof cycle === "string" ? parseInt(cycle) : cycle;
	switch (cycle || 1) {
		case 1 :
			return "工作周报";
		case 2 :
			return "工作月报";
		case 3 :
			return "季度总结";
		case 5 :
			return "半年总结";
		case 4 :
			return "年度总结";
		default :
			return "";
	}
};

/**
 * 获取报告的周期
 * 
 * @params cycle 报告周期：0=日报, 1=周报, 2=月报, 3=季报, 4=年报, 5=半年报
 */
basic.biz.selectReportCycle = function() {
	var html = "";
	switch (wpm.period_type || "5") {
		case "1" : { // 年计划
			for(var p in wpm.cycle_function){
				if(!!wpm.cycle_function[p].s4)//周报启用
					html += "<li><a href='javascript:basic.biz.setCycle(1);basic.biz.getReports(0)'>工作周报</a></li>";
				if(!!wpm.cycle_function[p].s3)//月报启用
					html += "<li><a href='javascript:basic.biz.setCycle(2);basic.biz.getReports(0)'>工作月报</a></li>";
				if(!!wpm.cycle_function[p].s2)//季度总结启用
					html += "<li><a href='javascript:basic.biz.setCycle(3);basic.biz.getReports(0)'>季度总结</a></li>";
				if(!!wpm.cycle_function[p].s1)//半年总结启用
					html += "<li><a href='javascript:basic.biz.setCycle(5);basic.biz.getReports(0)'>半年总结</a></li>";
				if(!!wpm.cycle_function[p].s0)//年度总结启用
					html += "<li><a href='javascript:basic.biz.setCycle(4);basic.biz.getReports(0)'>年度总结</a></li>";
			}
			break;
		}
		case "2" : { // 半年计划
			for(var p in wpm.cycle_function){
				if(!!wpm.cycle_function[p].s4)//周报启用
					html += "<li><a href='javascript:basic.biz.setCycle(1);basic.biz.getReports(0)'>工作周报</a></li>";
				if(!!wpm.cycle_function[p].s3)//月报启用
					html += "<li><a href='javascript:basic.biz.setCycle(2);basic.biz.getReports(0)'>工作月报</a></li>";
				if(!!wpm.cycle_function[p].s2)//季度总结启用
					html += "<li><a href='javascript:basic.biz.setCycle(3);basic.biz.getReports(0)'>季度总结</a></li>";
				if(!!wpm.cycle_function[p].s1)//半年总结启用
					html += "<li><a href='javascript:basic.biz.setCycle(5);basic.biz.getReports(0)'>半年总结</a></li>";
			}
			break;
		}
		case "3" : { // 季计划
			for(var p in wpm.cycle_function){
				if(!!wpm.cycle_function[p].s4)//周报启用
					html += "<li><a href='javascript:basic.biz.setCycle(1);basic.biz.getReports(0)'>工作周报</a></li>";
				if(!!wpm.cycle_function[p].s3)//月报启用
					html += "<li><a href='javascript:basic.biz.setCycle(2);basic.biz.getReports(0)'>工作月报</a></li>";
				if(!!wpm.cycle_function[p].s2)//季度总结启用
					html += "<li><a href='javascript:basic.biz.setCycle(3);basic.biz.getReports(0)'>季度总结</a></li>";
			}
			break;
		}
		case "4" : { // 月计划
			for(var p in wpm.cycle_function){
				if(!!wpm.cycle_function[p].s4)//周报启用
					html += "<li><a href='javascript:basic.biz.setCycle(1);basic.biz.getReports(0)'>工作周报</a></li>";
				if(!!wpm.cycle_function[p].s3)//月报启用
					html += "<li><a href='javascript:basic.biz.setCycle(2);basic.biz.getReports(0)'>工作月报</a></li>";
			}
			break;
		}
		case "5" : { // 周计划
			for(var p in wpm.cycle_function){
				if(!!wpm.cycle_function[p].s4)//周报启用
					html += "<li><a href='javascript:basic.biz.setCycle(1);basic.biz.getReports(0)'>工作周报</a></li>";
			}
			break;
		}
		default :
			break;
	}
	if(!Ext.isEmpty(html)){
		dispalyDropdownBox("6", "<ul>" + html + "</ul>");
	}
};

/** 将报告的周期写入到锚的属性上 */
basic.biz.setCycle = function(cycle) {
	// 取消其他链接的选中状态
	basic.global.every(Ext.query(".hj-wzm-six-top-a"), function(node) {
				basic.global.removeClass(node, "hj-wzm-six-top-a");
			});
	document.getElementById("workReports").className += " hj-wzm-six-top-a";
	document.getElementById("workReports").setAttribute("cycle", cycle || 0);
};

/**
 * 查询某个人一段时期内(根据计划而定)的周报
 * 
 * @param action
 *            -1:前一段时期, 1:后一段时期, 0:默认时期
 * @params cycle 报告周期：0=日报, 1=周报, 2=月报, 3=季报, 4=年报, 5=半年报
 */
basic.biz.getReports = function(action) {
	if (wpm.nopriv == "1") {
		return;
	}

	var cycle = document.getElementById("workReports").getAttribute("cycle");
	document.getElementById("reportCycleDesc").innerHTML = basic.biz
			.getCycleDesc(cycle); // 更新页面上周报周期的描述
	var hashvo = new ParameterSet();
	hashvo.setValue("objectid", wpm.object_id);
	hashvo.setValue("p0700", wpm.p0700);
	hashvo.setValue("p0723", wpm.p0723);
	hashvo.setValue("period_year", wpm.period_year);
	hashvo.setValue("period_type", wpm.period_type);
	hashvo.setValue("period_month", wpm.period_month);
	hashvo.setValue("action", action || "0");
	hashvo.setValue("cycle", cycle || 0);
	var currPeriod = Ext.query(".report-period span")[0] || {};
	hashvo.setValue("currPeriod", currPeriod.innerHTML || ""); // 当前已查询时期
	new Request({
				asynchronous : false,
				onSuccess : function(data) {
					basic.biz.renderReports(data);
				},
				functionId : '9028000759'
			}, hashvo);
};

/** 获取一段时期内的报告 */
basic.biz.renderReports = function(data) {
	/*
	 * <div id="reportsInOnePeriod"> <div class="author-photo"><img
	 * src="image/datx.jpg" /></div> <div class="report-period"> <a
	 * href="#">David</a>的周报 <img src="image/zuo-jt.png"
	 * style="margin-left:100px;" /><span>2014年5月</span><img
	 * src="image/you-jt.png" /> </div> <div id="reports"></div> </div>
	 */
	var container = document.getElementById("tongzhi");
	container.innerHTML = "";
	var div = document.createElement("div");
	div.className = "reportsInOnePeriod";
	div.style.marginTop = "5px";
	var author = data.getValue("author") || {};
	var html = "";
	html += '<link href="style/stars.css" rel="stylesheet" />';
	html += '<div class="author-photo"><img class="img-circle" src="'
			+ (author.photo || "") + '" /></div>';
	html += '<div class="report-period">';
	html += (author.fullName || "") + '的' + (author.stateDesc || "") + '报';
	html += '<img src="/workplan/image/zuo-jt.png" onclick="basic.biz.getReports(-1)" style="margin-left:100px;" />';
	html += '<span>' + (author.period || "") + '</span>';
	html += '<img src="/workplan/image/you-jt.png" onclick="basic.biz.getReports(1)" />';
	html += '</div>';
	html += '<div id="reports"></div>';
	html += '</div>';
	div.innerHTML = html;

	container.appendChild(div);

	beans = data.getValue("reports") || [];

	basic.global.every(beans, function(bean) {
				var report = basic.biz.getReportNode(bean);
				var reports = document.getElementById("reports");
				reports.appendChild(report);

				// 用这种怪异的写法是因为有人将_score写入到全局变量里,无语的设计
				_score = _score || document.getElementById('score');
				_score.value = bean.score || "0";
				if (_score.value !== "-1") { // -1表示没有打分
					var starSpan = document.getElementById("score-"
							+ bean.p0100);
					if (starSpan) {
						initstar(starSpan);
					}
				}
			});

};

/** 发送提醒写周报邮件 */
basic.biz.remindReport = function(sParams, anchor) {
	var params = (sParams || "").split("`");
	var hashvo = new ParameterSet();
	basic.global.every(params, function(param) {
				var key = param.split("=")[0];
				var value = param.split("=")[1];
				if (key) {
					hashvo.setValue(key, value || "");
				}
			});
	new Request({
				asynchronous : true,
				onSuccess : function(data) {
					if (anchor) {
						var span = document.createElement("span");
						span.style.color = "#e5e5e5";
						span.innerHTML = anchor.innerHTML;
						anchor.parentNode.replaceChild(span, anchor);
					}
				},
				functionId : '9028000804'
			}, hashvo);
};
// 发送任务成员加密id拼成的字符串(日志操作) wusy
basic.biz.sendstaffids = function(staffids, role) {
	var hashvo = new ParameterSet();
	basic.biz.prepareStaticData(hashvo)
	hashvo.setValue("staff", "getStaffids");
	hashvo.setValue("staffids", staffids);
	hashvo.setValue("role", role);
	new Request({
				asynchronous : false,
				onSuccess : function(data) {
					// alert("nice");
				},
				functionId : '9028000760'
			}, hashvo);
};
/** ################################### 任务关注人 ############################## */
/** 弹出选择人提示框 */
// 添加任务相关成员
basic.biz.pickStaff = function(btn) {
	var p0700 = Ext.getDom("param.p0700").value;
	var p0723 = Ext.getDom("param.p0723").value;
	var objectid = Ext.getDom("param.objectid").value;
	var multiple = btn.parentNode.parentNode.id == "director" ? false : true;
	var text = btn.parentNode.parentNode.id == "member" ? "添加参与人" : "添加关注人";
	var staffids = "";
	// 获取需排除人员
	var except = [];
	var hashvo = new ParameterSet();
	var condition = "";
	if (!multiple) {// 选单人时，说明是选负责人，则要排除负责人
		hashvo.setValue("condition", "2");// 2：任务详情界面选负责人
		hashvo.setValue("p0905", "1");// 1、负责人
	} else {
		if (text == "添加参与人") {
			hashvo.setValue("condition", "3");// 3：任务详情界面选任务成员
			condition = "3";
		} else if (text == "添加关注人") {
			hashvo.setValue("condition", "4");// 4：任务详情界面添加关注人
			condition = "4";
		}
	}
	hashvo.setValue("type", "2");// 1、计划 2、任务 3、工作总结
	var p0800 = document.getElementById("param.p0800").value;
	hashvo.setValue("id", p0800);
	new Request({
				asynchronous : false,
				onSuccess : function(data) {
					except = data.getValue("except");
				},
				functionId : '9028000769'
			}, hashvo);
	var picker = new PersonPicker({
				multiple : multiple,
				deprecate : except,
				text : text,
				isPrivExpression:false,//不启用高级权限
				titleText : text.replace("添加", "选择"),
				callback : function(c) {
//=======haosl 20160630 ==修改批量保存，只访问一次交易类而不是多次访问交易类=====开始=========
					if (multiple) {
						for (var i = 0; i < c.length; i++) {
							var staffId = c[i].id;
							staffids += staffId + "`";
						}
						var map = new HashMap();
						map.put("staffids", staffids);
						map.put("btn",btn);
						basic.util.bantchSaveStaff(map);
//=======haosl 20160630 ==修改批量保存，只访问一次交易类而不是多次访问交易类=====结束=========
						var recordId = document.getElementById("param.recordId").value;
						isNeedToRefresh(p0700,p0800,p0723,recordId);
						basic.biz.sendstaffids(staffids, condition);//3:任务成员,4任务关注人
					} else {
						var staffId = c.id;
						basic.util.saveStaff(staffId, btn);
					}
				}
			}, btn);
	picker.open();
	//lis 20160628 bug：18076 
	btn.focus();
	btn.blur();
};
// 添加计划关注人
basic.biz.pickFollower = function(btn) {
	if (wpm.p0700 == "") {
		Ext.Msg.alert("提示信息", "此计划还未制订，不能添加关注人!");
		return;
	}
	// PersonPicker(btn)
	// .setCallback(function(c) {
	// var staffId=c.id;
	// basic.biz.addFollower(staffId);
	// }).open();
	// 获取需排除人员
	var except = [];
	var hashvo = new ParameterSet();
	hashvo.setValue("condition", "1");// 计划界面关注人@按钮
	hashvo.setValue("type", "1");
	hashvo.setValue("id", wpm.p0700);
	new Request({
				asynchronous : false,
				onSuccess : function(data) {
					except = data.getValue("except");
				},
				functionId : '9028000769'
			}, hashvo);
	var picker = new PersonPicker({
				multiple : true,
				deprecate : except,
				text : "添加关注人",
				titleText : "选择关注人",
				isPrivExpression:false,//不启用高级权限
				// nbases: 'usr,ret', // 人员库范围字符串，不传代表全部
				// orgid: '010101,0101,010201,01,02,03,01020101,0201,030201', //
				// 组织机构，不传代表全部
				// extend_str: "select a0100 from ${nbase}A01 where A0000 = 20",
				callback : function(c) {
					for (var i = 0; i < c.length; i++) {
						var staffId = c[i].id;
						basic.biz.addFollower(staffId);
					}
				}
			}, btn);
	picker.open();
};
basic.biz.addFollower = function(id) {
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "addFollower");
	hashvo.setValue("plan_id", wpm.p0700);
	hashvo.setValue("followerId", id);
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : search_ok,
				functionId : '9028000702'
			}, hashvo);

	function search_ok(outparamters) {
		var info = outparamters.getValue("info");
		info = getDecodeStr(info);
		var obj = eval("(" + info + ")");
		obj = obj.follower;
		var strhtml = "";
		for (var i = 0; i < obj.length; i++) {
			strhtml = strhtml + getDisplayFollowerItem(obj[i]);
		}
		if (strhtml != "") {
			var selobj = document.getElementById("followerdiv");
			selobj.innerHTML = selobj.innerHTML + strhtml;
		}
	}

}
// 计划页面新建任务是选择任务负责人
basic.biz.pickCyr = function(btn) {
	// PersonPicker(btn)
	// .setCallback(function(c) {
	// var staffId=c.id;
	// basic.biz.addCyr(staffId);
	// }).open();

	var p = new PersonPicker({
				multiple : false,
				isPrivExpression:false,//不启用高级权限
				callback : function(c) {
					var staffId = c.id;
					basic.biz.addCyr(staffId);
				}
			}, btn);
	p.open();
	//lis 20160628 bug：18075
	btn.focus();
	btn.blur();
};
basic.biz.addCyr = function(id) {
	var hashvo = new ParameterSet();
	hashvo.setValue("oprType", "addParticipant");
	hashvo.setValue("plan_id", wpm.p0700);
	hashvo.setValue("usrId", id);
	var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : add_ok,
				functionId : '9028000702'
			}, hashvo);

	function add_ok(outparamters) {
		var info = outparamters.getValue("info");
		info = getDecodeStr(info);
		var obj = eval("(" + info + ")");
		obj = obj.participant;
		var strhtml = "";
		for (var i = 0; i < obj.length; i++) {
			strhtml = getDisplayParticipantItem(obj[i]);
			if (strhtml != "") {
				var storeObj = document.getElementById("store-add-member");
				var contentObj = document.getElementById("content-add-member");

				var items = storeObj.value.split(",");
				//不需要提示，lis 20160627
				/*var index = basic.global.indexOf(items, obj[i].id);
				if (index > -1) {
					Ext.Msg.alert("提示信息", "不能重复添加参与人！")
					return;
				}*/
				if (contentObj.innerHTML == "负责人") {
					contentObj.innerHTML = "";
				}
				var modnum = items.length % 5;
				if ((items.length > 1) && (modnum == 1)) {
					// contentObj.innerHTML=contentObj.innerHTML+"</p>"
					// contentObj.style.height=contentObj.style.height+25;
				}
				contentObj.innerHTML = contentObj.innerHTML + strhtml;

				contentObj.innerHTML = strhtml;
				// 隐藏域
				// storeObj.value=storeObj.value+","+obj[i].id;
				storeObj.value = obj[i].id;
			}
		}
	}
}
// 任务详情页面是选择子任务任务负责人
basic.biz.pickSubTaskCyr = function(btn) {
	basic.util.refreshPromptPlaceHolder(btn); // 刷新目标绑定的占位符
	var edit = btn.getAttribute("edit"); // 不可编辑则不执行任何操作
	// PersonPicker(btn)
	// .setCallback(function(c) {
	// var staffId=c.id;
	// basic.util.renderSelectedItem(staffId,btn);
	// }).open();

	var picker = new PersonPicker({
				multiple : false,
				isPrivExpression:false,//不启用高级权限
				callback : function(c) {
					var staffId = c.id;
					basic.util.renderSelectedItem(staffId, btn);
				}
			}, btn);
	picker.open();
};
/*
 * basic.biz.popupStaffPromptBox = function(ev, target) { var e = ev ||
 * window.event; if (!target) { return; } var staff =
 * target.parentNode.parentNode.id;
 * 
 * basic.prompt.openPrompt( { check : function() { if (!target) { return false; }
 * 
 * var container = document.getElementById(staff); var edit =
 * container.getAttribute("edit"); if ("always,normal".indexOf(edit) > -1) { //
 * 可以编辑 return true; } else { return false; } }, afterOpen : function() { if
 * (staff === "director") { var memebers;
 * 
 * var hashvo = new ParameterSet(); basic.biz.prepareStaticData(hashvo); new
 * Request( { asynchronous : false, onSuccess : function(data) { members =
 * data.getValue("members") || []; }, functionId : '9028000762' }, hashvo);
 * 
 * return members || []; } else { return []; } }, afterPickUp :
 * basic.util.saveStaff, search : basic.util.searchFromDB }, target);
 * 
 * basic.global.stopBubble(e); };
 */

/**
 * ############################################## 任务评价
 * #########################################
 */
/** 任务进展 */
basic.biz.getProgress = function() {
	basic.biz.shiftClass("taskProgress");
	initMessageContentlist("2", document.getElementById("param.p0800").value);
};

basic.biz.operationLog = function() {
	basic.biz.shiftClass("operationlog");
	initLogHistoryList("2", document.getElementById("param.p0800").value);
};

/** 查询任务评价 */
basic.biz.getEvaluation = function() {
	basic.biz.shiftClass("taskEvaluation");
	var hashvo = new ParameterSet();
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	new Request({
				asynchronous : true,
				onSuccess : function(data) {
					basic.biz.refreshEvaluation(data);
				},
				functionId : '9028000764'
			}, hashvo);
};

/** 刷新评价标签 */
basic.biz.refreshEvaluation = function(data) {
	/** 发送原始Ajax请求评价页面 */
	var xhr;
	if (window.XMLHttpRequest) { // code for IE7+, Firefox, Chrome, Opera,
		// Safari
		xhr = new XMLHttpRequest();
	} else { // code for IE6, IE5
		xhr = new ActiveXObject("Microsoft.XMLHTTP");
	}

	xhr.open("GET", "/workplan/plan_task/evaluation.jsp", true);
	xhr.send();
	xhr.onreadystatechange = function() {
		if (xhr.readyState == 4 && xhr.status == 200) {
			document.getElementById("tongzhi").innerHTML = xhr.responseText;
			basic.biz.renderEvaluations(data);
			// 评价文本框高度自适应
//			var evaldescription = document
//					.getElementById("evaluation-description");
//			adapt.listen(evaldescription, 80);
			/*if (basic.biz.tmp.needEvaluate === "true") { // 被邀请来的人可以进行评价
				basic.biz.doEvaluate();
			}*/
			setTimeout(function() {
						window.scrollBy(0, 300);
					}, 200);
		}
	}
};

/**
 * 修改上级评分后,对应计划页面的评分要做对应的修改(仅限直接上级修改下级评价)
 */
basic.biz.reRenderScore = function(role){
	//alert(window.parent._role);//计划页面是否是直接上级查看下级计划的标识workplan.js中
	if(role=='1' && basic.biz.isEvaluationEdited){//是直接上级且在任务页面修改了评价,计划页面对应要重新渲染星星评分
		var p0800 = document.getElementById("param.p0800").value;
		var p0700 = document.getElementById("param.p0700").value;
		var p0723 = document.getElementById("param.p0723").value;
		var objectid = document.getElementById("param.objectid").value;
		window.parent.reloadScore(p0700, p0723, objectid, p0800);//lis 20160627 edit
/*		basic.biz.isEvaluationEdited = false;
		var p0800 = Ext.getDom('param.p0800').value;
		var eleId = "evaluation"+p0800;
		var hashvo = new ParameterSet();
		basic.biz.prepareStaticData(hashvo);
		hashvo.setValue("commandStr", "selectDirecSupEval");
		var request = new Request({
					method : 'post',
					asynchronous : false,
					onSuccess : function(outparamters) {
						var averageScore = outparamters.getValue("directSupScore");
							console.log(averageScore)
						if (averageScore != "") {
							window.parent.initstar(eleId, averageScore);
						}
					},
					functionId : '9028000765'
				}, hashvo);*/
	}
}

/**
 * 删除评价
 * @param {} thisEle
 * @param {} len
 */
basic.biz.delScore = function(thisEle, len){
	 
	/** 弹出信息位置重新计算 linbz 20161012 start */
	var position = getMsgPosition();
	//elp = Ext.getBody();//报错5007
	
	//confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
	var scrollTop = parent.window.Ext.getBody().getScroll().top;
	
	var a = Ext.Msg.confirm("提示信息","您确定要删除该评价吗？",function(e){ 
		if(e == "yes"){
			//deleteObject = thisEle.parentNode.parentNode.parentNode;
			basic.biz.isEvaluationEdited = true;//将上级评价被修改标识置为true
			var evaluationId = thisEle.getAttribute('evaluationId');
			var hashvo = new ParameterSet();
			basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
			hashvo.setValue("commandStr", "delEvaluation");
			hashvo.setValue("evaluationId", evaluationId);
			new Request({
						asynchronous : true,
						onSuccess : function(data) {
							var role = data.getValue('role');
							basic.biz.getEvaluation();
							basic.biz.reRenderScore(role);
						},
						functionId : '9028000765'
					}, hashvo);
		}else{
			return;
		}
	}).alignTo(Ext.getBody(),"tl",[position.x,position.y]);
	
	//恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
	if(Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
		window.parent.document.documentElement.scrollTop = scrollTop;
	}else {//chrome、safari
		parent.window.Ext.getBody().setScrollTop(scrollTop);
	}
	/** 弹出信息位置重新计算 linbz 20161012 end */
}

/**
 * 重新评价
 */
basic.biz.reScore = function(thisEle, len,score){
	var textVal = thisEle.getAttribute("eValue");
	var evaluationId = thisEle.getAttribute("evaluationId");
	document.getElementById("scoreNew").style.display = "inline";
	document.getElementById("evaluationId").value = evaluationId;
	var evaldescription = document.getElementById("newScorEvaluation");
	initstar2("newScore", score);
	adapt.listen(evaldescription, 80);
	evaldescription.value = textVal;//不能用innerHtml,lis 20160318
	document.getElementById("score_0").style.display = "none";
	if(len==1){
		document.getElementById('showSupName').style.display = 'none';
	}
}

/**
 * 重新评价
 */
basic.biz.memberReScore = function(thisEle,score){
	var textVal = thisEle.getAttribute("eValue");
	var evaluationId = thisEle.getAttribute("evaluationId");
	document.getElementById("scoreNew").style.display = "inline";
	document.getElementById("evaluationId").value = evaluationId;
	var evaldescription = document.getElementById("newScorEvaluation");
	initstar2("newScore", score);
	adapt.listen(evaldescription, 80);
	evaldescription.innerText = textVal;
	document.getElementById("member_0").style.display = "none";
	document.getElementById("memberSpan").style.display = "none";
}

/**
 * 任务成员的评价加载
 * @param {} memberEvaluations
 */
basic.biz.innerMemEvHTML = function(who,memberEvaluations){
	var memEvaEle = document.getElementById('memEvaHtml');
	memEvaEle.innerHTML = "";
	var htmlStr = "";
	for(var k=0; k<memberEvaluations.length; k++){
			if(memberEvaluations[k].role != 'superior'){
				htmlStr += "<span id='memberSpan' style='margin-left:10px;'>负责人和参与人的评价</span>";
				break;
			}
	}
	htmlStr += "<br />";
	htmlStr += "<br />";
	for(var k=0; k<memberEvaluations.length; k++){
		if(memberEvaluations[k].role == 'superior'){
			continue;
		}
		htmlStr += "<div id='member_" + k + "' style='float:left;width:100%;'>";
		htmlStr += "<div class='scoreLeft' style='float:left;width:15%; margin-left:10px;'>";
		htmlStr += "<span>"+memberEvaluations[k].evaluator_name+"<span>";
		htmlStr += "</div>";
		htmlStr += "<div class='scoreRight' style='float:left; width:68%;'>";
		if(memberEvaluations[k].score){
			htmlStr += "<span>" + memberEvaluations[k].date + "发布的评价</span>";
			htmlStr += "&nbsp;&nbsp;&nbsp;&nbsp;<a id='delEvala' evaluationId='"
			htmlStr += memberEvaluations[k].evaluationId + "' style='cursor:pointer;display:none;' href='javascript:void(0)' "
			htmlStr += "onclick='basic.biz.delScore(this," + memberEvaluations.length + ")'>删除</a>&nbsp;&nbsp;&nbsp;&nbsp;"
			htmlStr += "<a id='reSccoreid' evaluationId='"+memberEvaluations[k].evaluationId+"' style='cursor:pointer;display:none;' href='javascript:void(0)' "
			htmlStr += "onclick='basic.biz.memberReScore(this,"+memberEvaluations[k].score+")' eValue='" + decode(memberEvaluations[k].editDescription) + "'>重新打分</a>";
			htmlStr += "<br />";
			htmlStr += "<span id='scoreEva"+k+"'></span>";
			htmlStr += "<br />";
			htmlStr += "<p>"+memberEvaluations[k].description+"</p>";
		}else{
			//haosl	20160709	start 
			if(memberEvaluations[k].invite){
			var str = "邀请他（她）评价";
			if(memberEvaluations[k].sex=='1'){
				str = "邀请他评价";
			}else if(memberEvaluations[k].sex=='2'){
				str = "邀请她评价";
			}
			htmlStr += "<a href='javascript:void(0)' evaluatorId='"
						+ memberEvaluations[k].evaluator_id
			+ "' onclick='basic.biz.invite(this)'>"+ str +"</a>"
			}
			//haosl 20160709	end
		}
		htmlStr += "</div>";
		htmlStr += "</div>";
		htmlStr += "<br />";
		htmlStr += "<br />";
		htmlStr += "<br />";
		htmlStr += "<br />";
	}
	
	memEvaEle.innerHTML = htmlStr;
	
	if(who=='member'){
		var delEvala = document.getElementById('delEvala');
		var reSccore = document.getElementById('reSccoreid');
		if(delEvala){
			delEvala.style.display = 'inline';
		}
		if(reSccore){
			reSccore.style.display = 'inline';
		}
	}
	
	for(var k=0; k<memberEvaluations.length; k++){
		var id = "scoreEva"+k;
		var ele = document.getElementById(id);
		if(ele){
			var scoreNum = memberEvaluations[k].score;
			initstar(id, scoreNum);
		}
	}
}

/**
 * 上级评价加载
 */
basic.biz.innerSupEvHTML = function(who, superEvaluations){
	if(superEvaluations.length==0){
		return;
	}
	var showName = '';
	var superEvlEle = document.getElementById('superEvaHtml');
	superEvlEle.innerHTML = "";
	var htmlStr = "";
	if(who=='self'){
		//************************************************************
		for(var i=0; i<superEvaluations.length; i++){
			showName = superEvaluations[i].evaluator_name;
			htmlStr += "<div style='float:left;width:100%;border:1px;'>";//每条评价用一个div包起来
			//左边显示名字
			htmlStr += "<div class='scoreLeft' style='float:left;width:15%; margin-left:10px;'>";
			htmlStr += "<span>"+showName+"</span>";	
			htmlStr += "</div>";
			//右边  日期,分数,评价
			//先包一个大div
			htmlStr += "<div class='scoreRight' style='float:left;width:68%'>";
			//******************************
			if(superEvaluations[i].role != 'superior'){
					break;
			}
			htmlStr += "<div id='score_"+i+"' style='width:300;height:100;'>";
			htmlStr += "<span id='lastScoreValue'>"+superEvaluations[i].date+"发布的评价</span>";
			htmlStr += "<br />";
			htmlStr += "<span id='scoreEv"+i+"'></span>";
			htmlStr += "<br />";
			htmlStr += "<p>"+superEvaluations[i].description+"</p>";
			htmlStr += "</div>";
			htmlStr += "<br />";
			htmlStr += "<br />";
			//******************************
			
			htmlStr += "</div>";
		}
		superEvlEle.innerHTML = htmlStr;
		for(var k=0; k<superEvaluations.length; k++){
			var id = "scoreEv"+k;
			var ele = document.getElementById(id);
			if(ele){
				var scoreNum = superEvaluations[k].score;
				initstar(id, scoreNum);
			}
		}
		//************************************************************
	}else if(who=='super' || who=='otherMember'){
		var haveLoaderEval = false;//是否有登陆人(上级)的评价
		var haveOtherSupEval = false;//是否有除登陆人之外别人(上级)的评价
		var starIds = [];
		var starNums = [];
		var startNum = 0;
		
		for(var k=0; k<superEvaluations.length; k++){
			if(superEvaluations[k].role == 'superior' && superEvaluations[k].isLoader==='yes'){
				haveLoaderEval = true;
			}
			if(superEvaluations[k].role == 'superior' && superEvaluations[k].isLoader==='no'){
				haveOtherSupEval = true;
			}
			if(haveLoaderEval && haveOtherSupEval){
				break;
			}
		}
		if(superEvaluations.length>0){
			if(haveLoaderEval){//登陆人的评价在最前,并显示为我的评价
				htmlStr += "<div id='showSupName' class='scoreLeft' style='float:left;width:15%; margin-left:10px;'>";
				htmlStr += "<span>"+"我的评价"+"</span>";	
			
				htmlStr += "</div>";
				htmlStr += "<div class='scoreRight' style='float:left;width:68%'>";
				for(var k=0; k<superEvaluations.length; k++){
					if(superEvaluations[k].isLoader==='no'){
						continue;
					}
					htmlStr += "<div id='score_"+k+"' style='width:300;height:100;'>";
					htmlStr += "<span id='lastScoreValue'>"+superEvaluations[k].date+"发布的评价</span>";
					if(startNum==0){
						htmlStr += "&nbsp;&nbsp;&nbsp;&nbsp;<a id='delEvala' evaluationId='"+superEvaluations[k].evaluationId+"' style='cursor:pointer;display:none;' href='javascript:void(0)' onclick='basic.biz.delScore(this,"+superEvaluations.length+")'>删除</a>&nbsp;&nbsp;&nbsp;&nbsp;<a id='reSccoreid' evaluationId='"+superEvaluations[k].evaluationId+"' style='cursor:pointer;display:none;' href='javascript:void(0)' onclick='basic.biz.reScore(this,"+superEvaluations.length+","+superEvaluations[k].score+")' eValue='"+decode(superEvaluations[k].editDescription)+"'>重新打分</a>";
						startNum++;
					}
					htmlStr += "<br />";
					htmlStr += "<span id='scoreEv"+k+"'></span>";
					starIds[starIds.length] = "scoreEv"+k;
					starNums[starNums.length] = superEvaluations[k].score;
					htmlStr += "<br />";
					htmlStr += "<p>"+superEvaluations[k].description+"</p>";
					htmlStr += "</div>";
					htmlStr += "<br />";
					htmlStr += "<br />";
				}
				htmlStr += "</div>";
				//----------------------------
			}
			//***********不是登陆人的的上级************
			if(haveOtherSupEval){
				htmlStr += "<div style='float:left;width:100%;margin-left:10px;'>";
				htmlStr += "<span>其他上级的评价</span>"
				htmlStr += "</div>";
				htmlStr += "<br />"
				htmlStr += "<br />"
				for(var i=0; i<superEvaluations.length; i++){
				if(superEvaluations[i].role != 'superior' || superEvaluations[i].isLoader==='yes'){
					continue;
				}
				showName = superEvaluations[i].evaluator_name;
				htmlStr += "<div style='float:left;width:100%;border:1px;'>";//每条评价用一个div包起来
				//左边显示名字
				htmlStr += "<div class='scoreLeft' style='float:left;width:15%; margin-left:10px;'>";
				htmlStr += "<span>"+showName+"</span>";	
				htmlStr += "</div>";
				//右边  日期,分数,评价
				//先包一个大div
				htmlStr += "<div class='scoreRight' style='float:left;width:68%'>";
				//******************************
				htmlStr += "<div style='width:300;height:100;'>";
				htmlStr += "<span id='lastScoreValue'>"+superEvaluations[i].date+"发布的评价</span>";
				htmlStr += "<br />";
				htmlStr += "<span id='otherScoreEv"+i+"'></span>";
				starIds[starIds.length] = "otherScoreEv"+i;
				starNums[starNums.length] = superEvaluations[i].score;
				htmlStr += "<br />";
				htmlStr += "<p>"+superEvaluations[i].description+"</p>";
				htmlStr += "</div>";
				htmlStr += "<br />";
				htmlStr += "<br />";
				//******************************
				
				htmlStr += "</div>";
			}
		}
			//***********别的上级****************
	}
	superEvlEle.innerHTML = htmlStr;
	//上级渲染星星
	for(var p=0; p<starIds.length; p++){
		var id = starIds[p];
		var ele = document.getElementById(starIds[p]);
		if(ele){
			var scoreNum = starNums[p];
			initstar(id, scoreNum);
		}
		
	}
	
	}
	if(who=='super' || who=='otherMember'){
		var delEvala = document.getElementById('delEvala');
		var reSccore = document.getElementById('reSccoreid');
		if(delEvala){
			delEvala.style.display = 'inline';
		}
		if(reSccore){
			reSccore.style.display = 'inline';
		}
	}
	
}

/**
 * 返回 上级评价时打分和评价文本框的html代码
 * 
 */
basic.biz.addEvaluationsHTML = function(who, name, haveLoaderEval){
	var scoNewEle = document.getElementById('scoreNew');
	scoNewEle.innerHTML = "";
	var htmlStr = "";
	htmlStr = "<span style='width:100%;margin-left:10px;'>"+"对"+name+"的任务完成情况进行评价"+"</span>";
	htmlStr += "<br />";
	htmlStr += "<br />";
	htmlStr += "<div style='width:15%;float:left;margin-left:10px;'>";
	htmlStr += "<span>"+"打分"+"</span>";
	htmlStr += "</div>"
	htmlStr += "<div style='width:68%;float:left'>";
	htmlStr += "<span id='newScore'>"+"</span>";
	htmlStr += "</div>";
	htmlStr += "<br />";
	htmlStr += "<br />";
	htmlStr += "<div style='width:15%;float:left;margin-left:10px;'>";
	htmlStr += "<span>"+"评语"+"</span>";
	htmlStr += "</div>";
	htmlStr += "<div style='width:80%;float:left;'>";
	htmlStr += "<textarea class='hj-zm-bg6-pjbz' id='newScorEvaluation' style='display: inline-block; color: rgb(68, 68, 68); height: 88px; resize: none; overflow-y: hidden; background-color: rgb(255, 255, 255); width: 105%;'>"+"</textarea>";
	htmlStr += "</div>";
	htmlStr += "<input type='hidden' value='' id='evaluationId'>";
	htmlStr += "<br />";
	htmlStr += "<div style='width:100%;float:left;'>";
	htmlStr += "<div style='width:15%;float:left;margin-left:10px;'>";
	htmlStr += "<br />";
	htmlStr += "</div>";
	htmlStr += "<div style='width:80%;float:left;'>";
	htmlStr += "<span id='evalueationCheckSpan' style='display:none;'><input type='checkbox' style='margin-left:0px;' id='evalueationChecked'/>添加新评价记录</span>";
	htmlStr += "<input class='hj-wzm-five-fabu' onclick=\"basic.biz.publishEvaluation('task')\" style='cursor:pointer; float:right; margin-right:-25px;width:65px;' id='publishEvalEle' type='button' value='发布评价'/>";
	htmlStr += "</div>";
	htmlStr += "</div>";
	scoNewEle.innerHTML = htmlStr;
	if((who=='otherMember' || who=='super' || basic.biz.tmp.needEvaluate === "true") && !haveLoaderEval){
		scoNewEle.style.display = 'inline';
		initstar("newScore", "-1");
		var evaldescription = document.getElementById("newScorEvaluation");
		adapt.listen(evaldescription, 80);
	}
	if(haveLoaderEval && who=='super'){
		document.getElementById('evalueationCheckSpan').style.display = 'inline';
	}
	
}

/** 渲染 */
basic.biz.renderEvaluations = function(data) {
//	alert(loaderId);
	var evaluations = data.getValue("visible") || [];
	//上级评价集合
	var se = 0;
	var superEvaluations = [];//上级评价
	//任务成员评价集合
	var me = 0;
	var memberEvaluations = [];//任务成员评价
	var who = data.getValue("who");//who指当前登录用户和被查看人的关系 self:查看的自己,  super:上级, director:任务负责人,  member:任务成员/关注人
	if(evaluations.length>0){
		for(var i=0; i<evaluations.length; i++){
			//将上级评价和任务成员的评价分为两个集合
			if(evaluations[i].role=='superior'){
				superEvaluations[se] = evaluations[i];
				se++;
			}
			if(evaluations[i].role=='dm'||evaluations[i].role=='follower' || evaluations[i].role=='myself'){
				memberEvaluations[me] = evaluations[i];
				me++;
			}
		}
	}
	var object = data.getValue("object") || "";
	//document.getElementById("evaluation-object").innerHTML = object;
	var tzEle = document.getElementById("tongzhi");
	var htmlStr = "";
	htmlStr += "<br />";
	htmlStr += "<br />";
	//打分写评价的div
	htmlStr += "<div id='scoreNew' style='width:100%;display:none;'></div>";
	htmlStr += "<br />";
	htmlStr += "<br />";
	//上级评价列表展示
	htmlStr += "<div id='superEvaHtml' style='width:100%;'></div>";
	htmlStr += "<br />";
	htmlStr += "<br />";
	//任务成员评价列表展示
	htmlStr += "<div id='memEvaHtml' style='float:left; width:100%;'></div>";
	
	tzEle.innerHTML = htmlStr;
	var haveLoaderEval = false;//是否有登陆人(上级)的评价
	for(var k=0; k<superEvaluations.length; k++){
			if(superEvaluations[k].role == 'superior' && superEvaluations[k].isLoader==='yes'){
				haveLoaderEval = true;
			}
			if(haveLoaderEval){
				break;
			}
	}
	if (basic.biz.tmp.needEvaluate === "true") { // 被邀请来的人可以进行评价
		if(memberEvaluations.length == 0)//未评价过，显示评价
			basic.biz.addEvaluationsHTML(who,object, false);
		else
			basic.biz.addEvaluationsHTML(who,object, true);
		//basic.biz.doEvaluate();
	}else{
		basic.biz.addEvaluationsHTML(who,object, haveLoaderEval);
		basic.biz.innerSupEvHTML(who, superEvaluations);
	}
	basic.biz.innerMemEvHTML(who,memberEvaluations);
	
//	basic.global.every(evaluations, function(evaluation) {
//		if (evaluation.needEvaluate === "true") { // 需要评价
//			basic.biz.doEvaluate();
//		} else { // 不需要评价，表示已评价(有记录)或需要发送邀请
//			var role = document.getElementById("eva-" + evaluation.role);
//			if (role) {
//				role.appendChild(basic.biz.createEvaluationNode(evaluation,
//						evaluation.invite === "true", evaluation.role));
//				// 评价的星星
//				_score = _score || document.getElementById('score');
//				_score.value = !evaluation.score || evaluation.score === "-1"
//						? "0"
//						: evaluation.score;
//				var starSpan = document.getElementById("score-"
//						+ evaluation.evaluator_id);
//				if (starSpan) {
//					initstar(starSpan);
//				}
//
//				role.style.display = "table-row-group";
//				if (role.id.indexOf("myself") > -1) {
//					document.getElementById("doEvaluate").style.display = "none"; // 表示本人已评价(数据库中有记录),则将评价输入框屏蔽
//				}
//			}
//		}
//	});
	
};

/** 创建评价节点 */
basic.biz.createEvaluationNode = function(evaluation, invite, role) {
	var tr = document.createElement("tr");

	var evaluatorTD = document.createElement("td");
	if (!invite) {
		evaluatorTD.style.verticalAlign = "top";
	}
	evaluatorTD.title = evaluation.evaluator_fullName;
	if (role === "myself") {
		evaluatorTD.innerHTML = "我的评价";
	} else {
		evaluatorTD.innerHTML = evaluation.evaluator_name;
	}

	var detailTD = document.createElement("td");

	var html = "";

	if (invite) { // 需要发送邀请
		/*
		 * <tr> <td title="邓灿">邓灿</td> <td><a href="javascript:void(0)">邀请他评价</a></td>
		 * </tr>
		 */

		var sex = null;
		switch (evaluation.sex) {
			case "1" :
				sex = "他";
				break;
			case "2" :
				sex = "她";
				break;
			default :
				sex = "他";
		}
		html += "<a href='javascript:void(0)' evaluatorId='"
				+ evaluation.evaluator_id
				+ "' onclick='basic.biz.invite(this)'>邀请" + sex + "评价</a>";
	} else {
		/*
		 * <tr> <td title="刘蒙">刘蒙</td> <td> <div id="score"> <img
		 * src="/workplan/image/xingxing.png" /> </div> <p>工作干的不错，继续努力</p>
		 * </td> </tr>
		 */
		html += "<div><span score='" + evaluation.score + "' id='score-"
				+ evaluation.evaluator_id + "'></span>";
		if (evaluation.reScore === "true") {
			html += "&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:basic.biz.doEvaluate()'>重新打分</a>";
		}
		html += "</div>";
		// html += "<div id='wrap' align='left' style='width:auto; float:left'>"
		// + evaluation.description + "</div>";
		html += "<p style='word-wrap:break-word;word-break:break-all;line-height:normal;'>"
				+ evaluation.description + "</p>";
		// html += "<div id='wrap' style='line-width:normal;'>" +
		// evaluation.description + "</div>";
		// html += "<p id='wrap' style='line-height:normal;'>" +
		// evaluation.description + "</p>";
	}

	detailTD.innerHTML = html;
	tr.appendChild(evaluatorTD);
	tr.appendChild(detailTD);

	return tr;
};

/** 执行评价 */
basic.biz.doEvaluate = function() {
	//re_scoring = true;
	document.getElementById("doEvaluate").style.display = "table";
	document.getElementById("eva-myself").style.display = "none";

	// 从页面查找当前用户上一次评价的说明以及分数
	var span = Ext.query("span", document.getElementById("eva-myself"))[0];
	var p = Ext.query("p", document.getElementById("eva-myself"))[0];
	var score = "-1"; // 上一次评分的分数
	var description; // 上一次评分的描述
	if (span) {
		score = span.getAttribute("score");
	}
	if (p) {
		description = basic.global.formatText(p.innerHTML);
	}

	_score = _score || document.getElementById('score');
	_score.value = "-1";
	initstar(document.getElementById("evaluation-score"));

	document.getElementById("evaluation-description").value = description || "";
};

/** 发表任务评价 */
basic.biz.publishEvaluation = function(sourceStr) {
	//alert(scoreNumb);//stars.js中设置的一个全局变量,即分数
//	alert(sourceStr);//sourceStr 区分是任务页调用还是计划页调用
	basic.biz.isEvaluationEdited = true;//将上级评价被修改标识置为true
	var publishBtn = document.getElementById('publishEvalEle');
	if(sourceStr=='task'){
		if(publishBtn){
			publishBtn.style.display = "none";
		}
		var hashvo = new ParameterSet();
		basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
		var commandStr = "updateEvaluation";//更新评价
		var taskCheckEle = document.getElementById('evalueationChecked');
		var taskCheckPaEle = document.getElementById('evalueationCheckSpan');
		var taskPageCheck = false;
		var planPageCheck = false;
		if(taskCheckEle){
			taskPageCheck = taskCheckEle.checked;
		}
		//计划页面的记得也要加上
		re_scoring = taskPageCheck || planPageCheck;//计划页面直接修改的那个checkbox
		var evaluationId = document.getElementById("evaluationId").value;
		if(evaluationId == ""){//新增评价
			commandStr = "addEvaluation";//新评价
		}else{//重新评价
			if(re_scoring){//勾选新增
				commandStr = "addEvaluation";//新评价
			}
		}
		/*if(taskCheckPaEle.style.display == 'none'){
			commandStr = "addEvaluation";//新评价
		}else{
			if(re_scoring){
				commandStr = "addEvaluation";//新评价
			}
		}*/
	}else if(sourceStr=='plan'){
		
	}
	hashvo.setValue("commandStr", commandStr);
	hashvo.setValue("score", scoreNumb);
	hashvo.setValue("description", getEncodeStr(document
					.getElementById("newScorEvaluation").value));
	new Request({
				asynchronous : true,
				onSuccess : function(data) {
					basic.biz.getEvaluation();
//					basic.biz.tmp.needEvaluate = null; // 清除临时对象的“是否评价”参数，防止可重复发表的bug
//					document.getElementById("nowEvaluate").style.display = "none";
//					basic.biz.getEvaluation(); // 重新渲染评价
//					basic.biz
//							.refreshTopScore(document.getElementById("score").value); // 刷新顶部分值
					re_scoring = false;
					var role = data.getValue('role');
					basic.biz.reRenderScore(role);
					
				},
				functionId : '9028000765'
			}, hashvo);
};

/** 刷新页面顶部分数 */
basic.biz.refreshTopScore = function(score) {
	if (!score) {
		return;
	}
	_score = _score || document.getElementById("score");
	_score.value = score;
	if (_score.value >= 0) {
		initstar(document.getElementById("evaluationScore"));
	}
};

/**
 * 发送任务评价邀请
 * 
 * @param anchor
 *            发送邀请的链接
 */
basic.biz.invite = function(anchor) {
	var evaluatorId = anchor.getAttribute("evaluatorId");
	if (!evaluatorId) {
		return;
	}

	var hashvo = new ParameterSet();
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	hashvo.setValue("evaluatorid", evaluatorId);
	new Request({
				asynchronous : true,
				onSuccess : function(data) {
					var span = document.createElement("span");
					span.className = "grey";
					span.innerHTML = anchor.innerHTML;
					anchor.parentNode.replaceChild(span, anchor);
				},
				functionId : '9028000766'
			}, hashvo);
}

/** 点击事件发生在.hj-wzm-six-top内时，移动hj-wzm-six-top-a类至事件源 */
basic.biz.shiftClass = function(id) {
	// var e = ev || window.event;
	// var target = e.target || e.srcElement;
	var target = document.getElementById(id);

	// 将node对象的show属性设置为false
	basic.global.every(Ext.query("[show=true]"), function(node) {
				if (node) {
					node.setAttribute("show", "false");
				}
			});
	// 将当前node对象的show属性设置为true
	document.getElementById(id).setAttribute("show", "true");
	// if (target.nodeName === "A") {
	// 取消其他链接的选中状态
	basic.global.every(Ext.query(".hj-wzm-six-top a"), function(node) {
				if (node) {
					basic.global.removeClass(node, "hj-wzm-six-top-a");
				}
			});
	target.className = " hj-wzm-six-top-a";

	if (target.id === "taskProgress") { // 点击的是任务进展标签
		document.getElementById("processBarContainer").style.display = "block";
	} else {
		document.getElementById("processBarContainer").style.display = "none";
	}

	// document.getElementById("tongzhi").innerHTML = "";
	// }

	// basic.global.stopBubble(e);
};

/** ############################### 任务界面加载信息 ################################ */
/** step1: 1、计划信息; 2、[load=init]; 3、进度条; 4、对被查看人的评分 */
basic.biz.step1 = function() {
	var hashvo = new ParameterSet();
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	// 这些字段字段只属于P08表，不与其它表或字段产生依赖,可以直接从数据库中取值
	var initFields = "";
	var init = Ext.query("[load=init]");
	basic.global.every(init, function(node) {
				initFields += node.id + ",";
			});
	hashvo.setValue("init", initFields);
	hashvo.setValue("step", "1");
	new Request({
		asynchronous : false,
		onSuccess : function(data) {
			/** ####################### 计划信息 ####################### */
			var planInfo = data.getValue("planInfo");
			if (planInfo) {
				document.getElementById("planDesc").innerHTML = planInfo["planDesc"];
				document.getElementById("planPhoto").src = planInfo["planPhoto"];
			}

			/** ####################### 对被查看人的评分 ####################### */
			var superiorEvaluation = document.getElementById("param.superiorEvaluation").value;
			basic.biz.refreshTopScore(superiorEvaluation);

			/**
			 * ####################### [load=init] #######################
			 */
			basic.global.every(init, function(elmt) {
						var bean = data.getValue(elmt.id);
						basic.util.createEditFrame(elmt, bean);
					});
			// initDocument(); // 不执行的话日期选择框弹不出来
			// 解决任务状态被挤出行 任务状态为取消时,edit-p0801不存在,noCalcleP0801为null wusy
			var noCalcleP0801 = document.getElementById("edit-p0801");
			if (noCalcleP0801) {
				document.getElementById("edit-p0801").style.width = "80%";
			}
			// 从绩效进入任务详情界面,显示返回按钮 wusy
			/**改成跟okr样式一样,此段代码废弃
			var isgoback = window.parent.document.getElementById("iframe_task");// 获取计划页面(workplan_main.jsp)iframe
			var isgoback2 = window.parent.document
					.getElementById("iframe_main");// 获取绩效页面(cardPage.jsp)iframe
			// alert(isgoback);
			if ((isgoback == null) && isgoback2) {
				document.getElementById("goBack").style.display = "block";
			}
			*/
			// 让IE8及以下支持placeHolder属性
			if (basic.global.isIE()) {
				basic.global.compatPlaceHolder();
			}
			// 如果任务进度为0则隐藏%
			if (document.getElementById("p0835").innerHTML === "0") {
				document.getElementById("p0835").innerHTML = "";
				document.getElementById("percentSign").style.display = "none";
			} else {
				document.getElementById("percentSign").style.display = "inline";
			}

			// p0813、p0815都没有值的时候（任务起止时间）将“至”去掉
			if (!document.getElementById("p0813").innerHTML
					&& !document.getElementById("p0815").innerHTML) {
				document.getElementById("char_zhi").innerHTML = "";
			} else {
				document.getElementById("char_zhi").innerHTML = "&nbsp;至&nbsp;";
			}

			/** ####################### 进度条 ####################### */
			var p0835Bean = data.getValue("p0835");
			basic.biz.tmp.p0835 = p0835Bean.value;//保存 初始时的完成进度，避免拖拽之后没有发表，进度条显示的是假的 chent 20160321
			var bar = document.getElementById("progressBar");
			if (!bar.getAttribute("edit")) { // 元素原来就有edit属性，表示有预先定义的规则，不受任务状态约束
				bar.setAttribute("edit", p0835Bean.edit);
			}
			var validate = function() { // 拖拽前和拖拽结束后的校验,检验进度条能否拖动
				var edit = bar.getAttribute("edit") || "";
				if ("always,normal".indexOf(edit) < 0) { // edit=always|normal且status=editing状态才可以拖拽
					return false;
				}
				return true;
			};

			var draggable = validate();
			/*
			 * var validate = function(bar) { // 拖拽前和拖拽结束后的校验,检验进度条能否拖动 var edit =
			 * bar.getAttribute("edit") || ""; var status =
			 * bar.getAttribute("status") || ""; if
			 * ("always,normal".indexOf(edit) < 0 || status !== "editing") { //
			 * edit=always|normal且status=editing状态才可以拖拽 return false; } return
			 * true; };
			 */

			/** 进度条拖拽完成之后触发的事件 */
			var afterDraged = function(_bar, pace) {
				var bar = document.getElementById("progressBar");
				pace = pace || 0;
				document.getElementById("progressBarTip").innerHTML = pace
						+ "%";
				if (draggable) {
					document.getElementById("p0835").innerHTML = pace;
					if (pace === 0) {
						document.getElementById("p0809").innerHTML = "未开始";
						document.getElementById("p0835").innerHTML = "";
						document.getElementById("percentSign").style.display = "none";
					} else if (pace === 100) {
						document.getElementById("p0809").innerHTML = "完成";
						document.getElementById("percentSign").style.display = "inline";
					} else {
						document.getElementById("p0809").innerHTML = "进行中";
						document.getElementById("percentSign").style.display = "inline";
					}
					if ("always" === bar.getAttribute("edit")
							|| performanceStr == "1") { // always型编辑字段即点即存
														// 绩效进入时，进度条不隐藏 chent 20150923 start
						bar.setAttribute("fieldName", "p0835");
						bar.setAttribute("fieldValue", pace);
						var msgContent = document.getElementById("msgContent");
						if (msgContent) {
							msgContent.value = "任务进度:" + pace + "%";
						}
					}
				}
			}

			// 渲染进度条，并添加事件
			Ext.require('Ext.slider.*');
			Ext.onReady(function() {
						Ext.create('Ext.slider.Single', {
									id:'workplan_progressBar',
									renderTo : 'progressBar',
									hideLabel : true,
									disabled : !draggable,
									width : 214,
									increment : 10,
									minValue : 0,
									maxValue : 100,
									value : parseInt(p0835Bean.value || "0"),
									listeners : {
										// 'changecomplete':function(slider,
										// newValue){afterDraged(slider,
										// newValue);}
										// //进度条完成触发
										'changecomplete' : afterDraged
										// 进度条完成触发
									}
								});

					});
			// 如果没触发进度条的点击事件，手动将进度条的fieldValue赋上数据库中原来的值
			document.getElementById("progressBar").setAttribute("fieldValue",
					parseInt(p0835Bean.value || "0"));
			/*
			 * basic.util.progressBarWidget(p0835Bean.actualValue || 0, { init :
			 * function(bar) { if (!bar.getAttribute("edit")) { //
			 * 元素原来就有edit属性，表示有预先定义的规则，不受任务状态约束 bar.setAttribute("edit",
			 * p0835Bean.edit); }
			 * 
			 * bar.setAttribute("status", "done"); // 默认处于编辑结束状态 if
			 * (bar.getAttribute("edit") === "always") {
			 * bar.setAttribute("status", "editing"); // 始终处于编辑状态 } },
			 * beforeDrag : validate, afterDragged : function(bar, pace) { pace =
			 * pace || 0;
			 * 
			 * var valid = validate(bar); if (valid) {
			 * document.getElementById("p0835").innerHTML = pace; if (pace ===
			 * 0) { document.getElementById("p0809").innerHTML = "未开始";
			 * document.getElementById("p0835").innerHTML = "";
			 * document.getElementById("percentSign").style.display = "none"; }
			 * else if (pace === 100) {
			 * document.getElementById("p0809").innerHTML = "完成";
			 * document.getElementById("percentSign").style.display = "inline"; }
			 * else { document.getElementById("p0809").innerHTML = "进行中";
			 * document.getElementById("percentSign").style.display = "inline"; }
			 * 
			 * if ("always" === bar.getAttribute("edit")) { // always型编辑字段即点即存
			 * bar.setAttribute("fieldName", "p0835");
			 * bar.setAttribute("fieldValue", pace); var msgContent =
			 * document.getElementById("msgContent"); if (msgContent) {
			 * msgContent.value = "任务进度:" + pace + "%"; } //basic.biz.save(bar); //
			 * 保存进度交由发表来完成 } }
			 * 
			 * return valid; } });
			 */
		},
		functionId : '9028000751'
	}, hashvo);
};

/** step2: 1、负责人; 2、参与人; 3、关注人; 4、父任务; 5、子任务 */
var superiorEdit;
basic.biz.step2 = function() {
	var hashvo = new ParameterSet();
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	hashvo.setValue("step", "2");
	new Request({
				asynchronous : true,
				onSuccess : function(data) {
					/**
					 * ####################### 负责人、参与人、关注人
					 * #######################
					 */
					var staffs = [];
					staffs[staffs.length] = document.getElementById("director");
					staffs[staffs.length] = document.getElementById("member");
					staffs[staffs.length] = document.getElementById("follower");
					basic.global.every(staffs, function(staff) {
								if (!staff) {
									return;
								}

								var addBtn = document.getElementById(staff.id
										+ "-addBtn");
								var edit = data.getValue(staff.id + "_edit")
										|| "none";
								staff.setAttribute("edit", edit);
								var addBtnParent = addBtn.parentNode;
								if ("always,normal".indexOf(edit) === -1) { // 没有编辑的权限将添加按钮隐藏
									addBtnParent.removeChild(addBtn);
								}
								// 上级人员可更改下级人员任务 wusy
								// var superiorEdit =
								// data.getValue("superiorEdit");
								// alert(superiorEdit);
								// //1:是上级且下属本人创建的任务 (与员工本人的操作权限一致，可修改所有信息)
								// 2:是上级且上级分派的任务（上级本人计划中创建的）可修改任务所有属性，但修改权重是下属计划的权重
								// //3:是上级且上级分派的任务（上级在其他下属计划中创建的）可修改任务所有属性，但修改权重是下属计划的权重
								// //4:是上级且上级创建的任务（在下属计划中创建）可修改任务所有属性
								// //5: 是上级且其他人分派的任务 不可修改
								// //0:不是上级

								var beans = data.getValue(staff.id) || [];
								basic.global.every(beans, function(bean) {
											document
													.getElementById(staff.id
															+ "-display")
													.appendChild(basic.util
																	.createStaffNode(
																			bean,
																			staff.id));
										});
							});
					// 上级查看下级任务,在下级计划中创建任务,负责人栏默认显示下级姓名
					superiorEdit = data.getValue("superiorEdit");
					var thisTaskDirector = "";
					var othertask = data.getValue("othertask");
					var director = data.getValue("director");
					if("1" == othertask){//是穿透任务
						thisTaskDirector = director[0].fullName;
					}else{
						thisTaskDirector = data.getValue("thisTaskDirector");
					}
					var textNode = document.createTextNode(thisTaskDirector);
					// alert(textNode);
					var divNode = document.getElementById("director-name");
					divNode.appendChild(textNode);

					/** ####################### 父任务 ####################### */
					var parentTask = document.getElementById("parentTask");
					var ptBean = data.getValue("parentTask");
					//没有计划填写权限，或者不在计划填报期限内，显示查看父任务
					var personCF = Ext.getDom("param.personCF").value;
					var isCanFill = Ext.getDom("param.isCanFill").value;
					if (!ptBean || personCF=="false" || isCanFill=="false") {
						document.getElementById("ptCaption").innerHTML = "";
						document.getElementById("ptName").innerHTML = "";
					} else {
						document.getElementById("ptCaption").innerHTML = "查看父任务";
						var p0801str = ptBean["p0801"];
						p0801str = getDecodeStr(p0801str);
						document.getElementById("ptName").title = p0801str;
						if (p0801str.length > 10) {
							p0801str = p0801str.substring(0, 10) + "...";
						}
						document.getElementById("ptName").innerHTML = p0801str;
						document.getElementById("ptName").href = ptBean["url"];
					}

					/** ####################### 子任务 ####################### */
					var subTask_add = data.getValue("subTask_add");
					if ("always,normal".indexOf(subTask_add) === -1) {
						document.getElementById("subTaskBtn").disabled = true;
					}

					var subtasks = data.getValue("subtasks");
					var p0700 = data.getValue("p0700");
					var p0800 = data.getValue("p0800");
					var p0723 = data.getValue("p0723");
					var objectid = data.getValue("objectid");
					for (var i = 0; i < subtasks.length; i++) {
						basic.biz.createSubtaskTr(subtasks[i], p0700, p0800,
								p0723, objectid);
					}

				},
				functionId : '9028000751'
			}, hashvo);
};

/** step3: 1、待评价的任务; 2、动态展现的字段; 3、能否删除,取消,发布以及任务变更状态 */
basic.biz.step3 = function() {
	var hashvo = new ParameterSet();
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	hashvo.setValue("step", "3");
	new Request({
		asynchronous : false,
		onSuccess : function(data) {
			/** ####################### 待评价的任务 ####################### */
			var toEvaluate = document.getElementById("toEvaluate");
			toEvaluate.disabled = true;// 非编辑状态下不可勾选
			var rank = document.getElementById("rank");
			basic.util.createEditFrame(rank, data.getValue("rank"));
			if (!document.getElementById("edit-rank")
					|| !document.getElementById("edit-rank").value) {
				if (!rank.innerHTML) {
					toEvaluate.checked = false;
					if (data.getValue("rank").edit === "none") {
						toEvaluate.disabled = true;
					}
					document.getElementById("rankContainer").style.display = "none"; // 隐藏权重和评分说明所在div
				} else {
					toEvaluate.checked = true;
					document.getElementById("rankContainer").style.display = "table-row";
				}
			} else {
				toEvaluate.checked = true;
				document.getElementById("rankContainer").style.display = "table-row"; // 显示权重和评分说明所在div
			}

			/** ####################### 动态展现的字段 ####################### */
			var dyncBeans = data.getValue("dync");
			var region = document.getElementById("dynamicFieldRegion"); // 动态字段将要显示的区域
			for (var i = 0; i < dyncBeans.length; i++) {
				var tr = document.createElement("tr");

				var td_desc = document.createElement("td");
				if (dyncBeans[i].type === "M") {
					td_desc.className += "labelForTextarea";
				}
				td_desc.innerHTML = " " + (dyncBeans[i].desc || "");

				var td_value = document.createElement("td");
				var value = document.createElement("span");
				value.setAttribute("id", dyncBeans[i].id);

				td_value.appendChild(value);
				tr.appendChild(td_desc);
				tr.appendChild(td_value);

				region.appendChild(tr);
				basic.util.createEditFrame(value, dyncBeans[i]);
			}
			document.getElementById("edit-p0803").style.width = '100%';
			document.getElementById("edit-p0803").style.position = 'relative';
			document.getElementById("edit-p0803").style.left = '54px';
			/**
			 * #######################对非动态展现的字段动态添加其描述 #######################
			 */
			var staticBeans = data.getValue("staticFields");
			for (var i = 0; i < staticBeans.length; i++) {
				if (staticBeans[i].itemid == "p0823") {
					document.getElementById("tasktype").innerHTML = staticBeans[i].itemdesc;
				} else if (staticBeans[i].itemid == "p0803") {
					// document.getElementById("taskdesc").innerHTML =
					// staticBeans[i].itemdesc;
				} else if (staticBeans[i].itemid == "rank") {
					document.getElementById("taskrank").innerHTML = staticBeans[i].itemdesc;
				} else if (staticBeans[i].itemid == "p0841") {
					document.getElementById("taskEvaluateStandard").innerHTML = staticBeans[i].itemdesc;
				}

			}

			/**
			 * ####################### 能否删除,取消,发布 #######################
			 */
			basic.biz.tmp.privilege = data.getValue("privilege");
			basic.biz.refreshTaskButtons();
		},
		functionId : '9028000751'
	}, hashvo);
};

basic.biz.isHaveTask = function() {
	var hashvo = new ParameterSet();
	var temp = true;
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	new Request({
				asynchronous : false,
				onFailure : function(data) {
					temp = false;
				},
				functionId : '9028000751'
			}, hashvo);
	return temp;
};

/** 任务状态迁移 */
basic.biz.transit = function() {
	var hashvo = new ParameterSet();
	basic.biz.prepareStaticData(hashvo); // p0700,p0723,p0800,objectid
	new Request({
				asynchronous : true,
				onSuccess : function(data) {
					basic.biz.tmp.privilege = data.getValue("privilege");
					basic.biz.refreshTaskButtons();
				},
				functionId : '9028000763'
			}, hashvo);
}
/*----textarea自适应高度----------------------------*/
var observe;
if (window.attachEvent) {
	observe = function(element, event, handler) {
		element.attachEvent('on' + event, handler);
	};
} else {
	observe = function(element, event, handler) {
		element.addEventListener(event, handler, false);
	};
}

function resize(text) {
	if (text == null) {
		return;
	}
	var parentElement = text.parentNode;
	if (84 < text.scrollTop + text.scrollHeight) {
		// parentElement.style.height = text.scrollTop + text.offsetHeight+'px';
		// text.style.height = text.scrollTop + text.offsetHeight+'px';
		parentElement.style.height = text.scrollHeight + 'px';
		text.style.height = parentElement.style.height;
	} else {
		parentElement.style.height = "84px";
		text.style.height = "84px";
	}
}

var autoTextarea = function(elem, maxHeight) {
	// extra = extra || 0;
	var isFirefox = !!document.getBoxObjectFor || 'mozInnerScreenX' in window
	var isOpera = !!window.opera && !!window.opera.toString().indexOf('Opera')
	var addEvent = function(type, callback) {
		elem.addEventListener
				? elem.addEventListener(type, callback, false)
				: elem.attachEvent('on' + type, callback);
	}
	getStyle = elem.currentStyle ? function(name) {
		var val = elem.currentStyle[name];
		return val;
	} : function(name) {
		return getComputedStyle(elem, null)[name];
	};

	var minHeight = parseFloat(getStyle('height'));
	// 此处有ie不兼容问题（原因未知），故人为做特殊处理
	if (minHeight <= 0) {
		minHeight = 84;
	}

	elem.style.resize = 'none';
	var change = function() {
		var scrollTop, height, padding = 0, style = elem.style;
		if (elem._length === elem.value.length)
			return;
		elem._length = elem.value.length;
		if (!isFirefox && !isOpera) {
			padding = parseInt(getStyle('paddingTop'))
					+ parseInt(getStyle('paddingBottom'));
		};
		// scrollTop = document.body.scrollTop ||
		// document.documentElement.scrollTop;
		elem.style.height = minHeight + 'px';
		if (elem.scrollHeight > 84) {
			if (maxHeight && elem.scrollHeight > maxHeight) {
				height = maxHeight - padding;
				style.overflowY = 'auto';
			} else {
				height = elem.scrollHeight - padding;
				style.overflowY = 'hidden';
			};
			style.height = height + 'px';
			// scrollTop += parseInt(style.height) - elem.currHeight;
			// document.body.scrollTop = scrollTop;
			// document.documentElement.scrollTop = scrollTop;
			// elem.currHeight = parseInt(style.height);
			elem.parentNode.style.height = parseInt(style.height);// 当textarea的大小发生变化的时候，其父节点tr也要随着其相应的变化
		} else {
			elem.style.height = "84px";
			elem.parentNode.style.height = elem.style.height;
		};
	};
	addEvent('propertychange', change);
	addEvent('input', change);
	addEvent('focus', change);
	// addEvent('click', change);
	addEvent('keyup', change);
	addEvent('mouseout', change);// 当鼠标离开某对象范围时触发此事件
	change();

};

/**
 * 进入页面时初始化Textarea大小
 */
function initTextarea() {
//	var p0803 = document.getElementById("edit-p0803");
//	var p0841 = document.getElementById("edit-p0841");
//	var p0837 = document.getElementById("edit-p0837");
//	resize(p0803);
//	resize(p0841);
//	resize(p0837);

	var normals = Ext.query("[edit=normal]");
	basic.global.every(normals, function(normal) {
		if (normal.getAttribute("fieldType") === "M") { // 文本域输入框
			var elm = document.getElementById("edit-"+normal.id);
			resize(elm);
		}
	});
}

/**
 * 让Textarea在输入内容时自适应大小
 */
function adjustTextarea() {
	var p0803 = document.getElementById("edit-p0803");
	var p0841 = document.getElementById("edit-p0841");
	var p0837 = document.getElementById("edit-p0837");
	autoTextarea(p0803);
	autoTextarea(p0841);
	autoTextarea(p0837);
}

/*----textarea自适应高度----------------------------*/
/** 解决当任务名称过长（大概超过100个汉字时），页面样式错乱问题* */
basic.biz.adjustStyle = function() {
	var p0801 = document.getElementById("p0801");
	if (p0801.scrollWidth > 320) {
		p0801.style.width = "315px";
	} else {
		p0801.style.width = "auto";
	}
}
basic.biz.checkTaskName = function(str) {
	var tmp = str.replace(/\%/g, "％");
	var tmp1 = tmp.replace(/\^/g, "＾");
	var tmp2 = tmp1.replace(/\&/g, "＆");
	var tmp3 = tmp2.replace(/\\/g, "＼");

	return tmp3;
}