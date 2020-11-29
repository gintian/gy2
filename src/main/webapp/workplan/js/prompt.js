var basic = basic || {};
basic.prompt = basic.prompt || {
	promptBoxId : "prompt-box", // 提示框id
	box : undefined // 提示框
};
basic.prompt.option = basic.prompt.option || {
	check : function() {return true;}, // 检查是否启用提示框
	beforeOpen : undefined, // 打开提示框前要干的事
	afterOpen : undefined, // 打开提示框后要干的事,需要返回值[候选人数组],用于向提示框内渲染候选人元素
	search : undefined, // 向数据库中查询人员
	afterPickUp : undefined, // 选人之后要做的事
	beforeClose : undefined, // 关闭提示框前要做的事情
	afterClose : undefined // 关闭提示框后要做的事情
};

/**
 * 弹出elmt绑定的提示框
 * @param option 非必需，在执行一些特定操作时执行的回调函数
 * @param target 非必需，唤出提示框的元素，提供了会根据该元素定位提示框
 */
basic.prompt.openPrompt = function(option, target) {
	basic.prompt.box = basic.prompt.box || document.getElementById(basic.prompt.promptBoxId);
	
	if (target) { // 如果传入了目标元素
		basic.util.lowerLeft(basic.prompt.box, target);
		basic.prompt.box.callerNode = target;
	}
	
	// 用option填充可配置的参数basic.prompt.option(如选人之后的回调函数)
	for (var prop in basic.prompt.option) {
		if (basic.prompt.option.hasOwnProperty(prop) && option[prop]) {
			basic.prompt.option[prop] = option[prop];
		}
	}

	// 弹出前验证
	if (!basic.prompt.option.check || !basic.prompt.option.check.call(null, basic.prompt.box.callerNode)) {
		return;
	}
	
	// 验证后弹出前的操作，如定位提示框
	if (basic.prompt.option.beforeOpen) {
		basic.prompt.option.beforeOpen.call(null, basic.prompt.box.callerNode);
	}
	
	// 占位符
	var input = Ext.query("input", basic.prompt.box)[0];
	var tip = input.getAttribute("tip");
	input.className += " promptBox-tip";
	input.value = tip || "";
	
	// 打开提示框并是输入框获得焦点
	basic.prompt.box.style.display = "block";
//	basic.prompt.box.getElementsByTagName("input")[0].focus(); // 提示框内的文本框获得焦点
//	basic.prompt.box.getElementsByTagName("input")[0].select(); // 提示框内的文本框获得焦点
	
	// 打开提示框后获取候选人
	if (basic.prompt.option.afterOpen) {
		var candidates = basic.prompt.option.afterOpen.call(null, basic.prompt.box.callerNode);
		basic.prompt.resetPrompt(candidates || []);
	} else {
		basic.prompt.resetPrompt([]);
	}
};

/** 关闭提示框 */
basic.prompt.closePrompt = function() {
	basic.prompt.box = basic.prompt.box || document.getElementById(basic.prompt.promptBoxId);
	basic.prompt.box.callerNode = null; // 取消发起者
	basic.prompt.box.style.display = "none";
	
	var ul = basic.prompt.box.getElementsByTagName("ul")[0];
	if (ul) {
		ul.innerHTML = "";
	}
};

/** 生成提示框的内部所有节点 */
basic.prompt.resetPrompt = function(data) {
	data = data || [];
	
	var ul = basic.prompt.box.getElementsByTagName("ul")[0];
	if (!ul) {
		return;
	}
	ul.innerHTML = "";
	
	/**
	<li style='cursor:pointer;' onclick="pickUp('Usr121')">
		<div class="clearfix">
			<span class="radius_img30">吴</span>
			<div class="smember_list_info" style="margin-top:5px;">
				<div class="smember_list_item_name">吴致萌<span>（产品管理部）</span></div>
				<div class="smember_list_item_email">wuzm@hjsoft.com.cn</div>
			</div>
		</div>
	</li>
	 */
	var strHtml="";
	for (var i = 0; i < data.length; i++) {
		var li = "";
		li += "<li style='cursor:pointer;' onclick=\"basic.prompt.pickUp(this, '" + data[i].id + "')\">";
		li += "<div class='clearfix'>";
		li += "<span class='candidatePhoto'><img src='" + data[i].photo + "' /></span>";
		li += "<div class='smember_list_info' style='margin-top:5px;'>";
		li += "<div class='smember_list_item_name'>" + data[i].name + "（" + data[i].unit + "）</div>";
		li += "<div class='smember_list_item_email'>" + data[i].email + "</div>";
		li += "</div>";
		li += "</div>";
		li += "</li>";
		
		strHtml += li;
	}
	ul.innerHTML = strHtml;
};

/** 选中候选负责人 */
basic.prompt.pickUp = function(li, id) {
	// 选人之后的操作
	if (basic.prompt.option.afterPickUp) {
		basic.prompt.option.afterPickUp.call(null, id, basic.prompt.box.callerNode);
	}
	
	// 关闭提示框前的操作
	if (basic.prompt.option.beforeClose) {
		basic.prompt.option.beforeClose.call(null, basic.prompt.box.callerNode);
	}
	
	this.closePrompt();
	
	// 关闭提示框后的操作
	if (basic.prompt.option.afterClose) {
		basic.prompt.option.afterClose.call(null, basic.prompt.box.callerNode);
	}
};

/** 提示框内文本框的onkeyup事件:查找匹配的候选责任人 */
basic.prompt.getCandidate = function(input) {
	if (!input) {
		return [];
	}
	
	var tip = input.getAttribute("tip") || "";
	
	if (input.value === "" || input.value === tip) {
		input.className += " promptBox-tip";
		input.value = tip;
		
		return [];
	} else {
		basic.global.removeClass(input, "promptBox-tip");
		
		if (basic.prompt.option.search) {
			return basic.prompt.option.search.call(null, input.value, basic.prompt.box.callerNode);
		} else {
			return [];
		}
	}
};
/** 提示框内文本框的onkeyup事件:查找匹配的候选责任人,但按键松开时不恢复文本框里的提示内容 */
basic.prompt.getCandidate1 = function(input) {
	if (!input) {
		return [];
	}
	if (input.value != ""){
		basic.global.removeClass(input, "promptBox-tip");
		
		if (basic.prompt.option.search) {
			return basic.prompt.option.search.call(null, input.value, basic.prompt.box.callerNode);
		} else {
			return [];
		}
	}
};

/** 提示框内文本框的onkeydown事件:如果在输入文字之前内容为空则清除提示文字 */
basic.prompt.clearTip = function(input) {
	if (!input) {return;}
	
	var tip = input.getAttribute("tip");
	if (!input.value || input.value === tip) {
		basic.global.removeClass(input, "promptBox-tip");
		input.value = "";
	}
};
/** 失去焦点恢复框内提示文字 */
basic.prompt.restoreTip = function(input) {
	if (!input) {return;}

	if (!input.value) {
		input.value = input.getAttribute("tip") || "";
		input.className += " promptBox-tip";
	}
};


