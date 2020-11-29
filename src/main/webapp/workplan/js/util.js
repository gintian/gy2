var basic = basic || {};
basic.util = basic.util || {};
var edit_p0803val;
var edit_p0841val;
var edit_p0837val;
// 元素的绝对位置和宽高
basic.util.getAbsoluteLocation = function(element) {
	if (arguments.length != 1 || element == null) {
		return {};
	}
	var offsetTop = element.offsetTop;
	var offsetLeft = element.offsetLeft;
	var offsetWidth = element.offsetWidth;
	var offsetHeight = element.offsetHeight;
	while (element = element.offsetParent) {
		offsetTop += element.offsetTop;
		offsetLeft += element.offsetLeft;
	}
	return {
		top : offsetTop,
		left : offsetLeft,
		width : offsetWidth,
		height : offsetHeight
	};
};

/**
 * 定位目标元素
 * @param srcElmt 目标元素
 * @param outline 坐标和尺寸(top, left, width, height)
 */
basic.util.locate = function(srcElmt, outline) {
	if (!!outline.left || outline.left === 0) {
		srcElmt.style.left = outline.left + "px";
	}
	if (!!outline.top || outline.top === 0) {
		srcElmt.style.top = outline.top + "px";
	}
	if (!!outline.width || outline.width === 0) {
		srcElmt.style.width = outline.width + "px";
	}
	if (!!outline.height || outline.height === 0) {
		srcElmt.style.height = outline.height + "px";
	}
};

/**
 * 将目标定位到参照元素左下角
 * @param srcElmt 目标元素
 * @param ref 参照元素或者参照元素的位置和尺寸(top, left, width, height)
 */
basic.util.lowerLeft = function(srcElmt, ref) {
	var outline;
	if (ref.getAttribute) { // 表示是参照元素
		outline = basic.util.getAbsoluteLocation(ref);
	} else { // 不是参照元素就当参照元素的位置和尺寸来对待
		outline = ref;
	}
	
	outline.top = outline.top + outline.height;
	outline.width = undefined;
	outline.height = undefined;
	
	basic.util.locate(srcElmt, outline);
};

/**
 * 将目标定位到参照元素右侧
 * @param srcElmt 目标元素
 * @param refLoc 位置和大小
 * @param offset 偏移量(x,y,a,b):坐标和宽高
 */
basic.util.right = function(srcElmt, refLoc, offset) {
	offset = offset || {};
	offset.x = offset.x || 0;
	offset.y = offset.y || 0;
	
	srcElmt.style.top = (refLoc.top + offset.y) + "px";
	srcElmt.style.left = (refLoc.left + refLoc.width + offset.x) + "px";
	if (offset.a) {
		srcElmt.style.width = offset.a + "px";
	}
	if (offset.b) {
		srcElmt.style.height = offset.b + "px";
	}
};

/** 用指定的数据，在目标元素内部渲染选中的项目 */
basic.util.renderSelectedItem = function(itemId, target) {
	var storeNode = document.getElementById(target.getAttribute("store")); // 存放选中项id的隐藏域
	var contentNode = document.getElementById(target.getAttribute("content")); // 存放选中项(span)的div
	if (!storeNode || !contentNode) {
		return;
	}
	
	var item = basic.util.getSelectedItem(itemId);
	if (!item) {return;}
	var itemHtml = basic.util.createSelectedItemSpan(item);
	if (target.getAttribute("override") === "append") {
		var items = storeNode.value.split(",");
		if (basic.global.indexOf(items, item.id) < 0) { // 隐藏域中选中的项不存在
			contentNode.innerHTML += itemHtml;
			storeNode.value += item.id + ",";
		}
	} else if (target.getAttribute("override") === "replace") {
		contentNode.innerHTML = itemHtml;
		storeNode.value = item.id;
		
		// 负责人需要跟换照片
		var photoNodeId = "photo-" + target.id.substring(target.id.indexOf("-") + 1);
		var photoNode = document.getElementById(photoNodeId);
		if (photoNode) {
			photoNode.src = item.photo;
		}
	}
	
	basic.util.refreshPromptPlaceHolder(target);
};

/** 创建候选人span
	<div class='director' onmouseover='basic.util.showDelSpan(this)' onmouseleave='basic.util.hideDelSpan(this)'>
		<div class="director-name">刘蒙</div>
		<div class="director-del" itemId='Usr122' onclick='basic.util.delSelectedItem(this)'>×</div>
	</div>
 */
basic.util.createSelectedItemSpan = function(item) {
	if (!item) {
		Ext.Msg.alert("提示信息","basic.util.createSelectedItemSpan: 数据无效");
		return;
	}
	
	var director = "";
	director += "<div class='director' onmouseover='basic.util.showDelSpan(this)' onmouseleave='basic.util.hideDelSpan(this)'>";
	director += "<div class='director-name'>" + item.name + "</div>";
	director += "<div class='director-del' itemId='" + item.id + "' onclick='basic.util.delSelectedItem(this)'>×</div>";
	director += "</div>";

	return director;
};

/** 删除选中的项，同时删掉隐藏域中保存的值 */
basic.util.delSelectedItem = function(del) {
	// 选中项目所在的目标元素
	var target = del.parentNode.parentNode.parentNode; // target -> contentNode -> span -> del
	var storeNode = document.getElementById(target.getAttribute("store")); // 存放选中项的id
	var contentNode = document.getElementById(target.getAttribute("content")); // 存放选中项id的隐藏域
	if (!storeNode || !contentNode) {
		return;
	}
	
	var items = storeNode.value.split(",");
	var index = basic.global.indexOf(items, del.getAttribute("itemId"));
	if (index > -1) {
		items.splice(index, 1); // 删掉指定的项目id
	}
	storeNode.value = items.join(","); // 转换成字符串
	
	contentNode.removeChild(del.parentNode);
};

/** 向数据库中查询符合条件的候选人 */
basic.util.searchFromDB = function(keyword, target) {
	var candidates;
	
	var staff;
	if (target) {
		staff = target.id.substring(0, target.id.indexOf("-"));
	}
	
	var hashvo = new ParameterSet();
	basic.biz.prepareStaticData(hashvo);
	hashvo.setValue("keyword", keyword);
	hashvo.setValue("staff", staff || "");
	new Request({
		asynchronous : false,
		onSuccess : function(data) {
			candidates = data.getValue("candidates") || [];
		},
		functionId : '9028000754'
	}, hashvo);
	
	return candidates;
};

/** 从cookie中查找候选人 */
basic.util.searchFromCookie = function() {
	var cookie = {
		id : "Usr111",
		name : "cookie",
		abbr : "存",
		unit : "无单位",
		email : "cookie@hjsoft.com.cn"
	};
	return [];
};

/** 显示指定负责人节点下的删除按钮 */
basic.util.showDelSpan = function(director) {
	var target = director.parentNode.parentNode; // director的最外层提示框父节点([prompt])
	if (target.id === "edit-director") { // 负责人不允许删除，只允许替换
		return;
	}
	if (target.getAttribute("status") === "done") { // 当前为非编辑状态时不做任何处理
		return;
	}
	
	var name = Ext.query(".director-name", director)[0];
	var del = Ext.query(".director-del", director)[0];
	
	var loc = basic.util.getAbsoluteLocation(name);
	var offset = {};
	if (basic.global.isIE()) {
		offset.b = 22;
	}
	
	basic.util.right(del, loc, offset);
	name.style.backgroundColor = "#ACF7F6"; 
	del.style.display = "block";
};

/** 关闭指定节点下的删除按钮 */
basic.util.hideDelSpan = function(director) {
	var name = Ext.query(".director-name", director)[0];
	name.style.backgroundColor = "";
	Ext.query(".director-del", director)[0].style.display = "none";
};

/** 刷新占位符，判断其出现与否 */
basic.util.refreshPromptPlaceHolder = function(target) {
	var targets = [];
	
	if (!target) { // target无效，表示刷新所有[prompt]元素的占位符
		targets = Ext.query("[prompt]");
	} else {
		targets[0] = target;
	}
	
	for (var i = 0; i < targets.length; i++) {
		var phNode = document.getElementById(targets[i].getAttribute("placeHolder"));
		if (!phNode) {
			continue;
		}
		var storeNode = document.getElementById(targets[i].getAttribute("store"));
		if (storeNode.value === "") {
			phNode.style.display = "inline";
		} else if (targets[i].getAttribute("edit") === "always" || targets[i].getAttribute("status") === "editing") { // 总是可编辑或者正在编辑状态，不显示占位符
			phNode.style.display = "none";
		}
	}
};

/** 重新渲染,用指定的数据，在目标元素内部渲染选中的项目 */
basic.util.reRender = function(target) {
	if (!target) {
		Ext.Msg.alert("提示信息","basic.util.reRender:无效的目标元素，重新渲染失败");
		return;
	}
	
	Ext.query("div", target)[0].innerHTML = ""; // 清空目标元素的contentNode
	
	var hidden = Ext.query("[type=hidden]", target)[0];
	
	var itemIds = hidden.value.split(",");
	hidden.value = "";
	for (var i = 0; i < itemIds.length; i++) {
		if (!itemIds[i]) {
			continue;
		}

		basic.util.renderSelectedItem(itemIds[i], target);
	}
	basic.util.refreshPromptPlaceHolder(target); // 恢复绑定的占位符
};

/** 根据选中项的id(数组)得到对应的选中项 */
basic.util.getSelectedItem = function(itemId) {
	if (!itemId) {
		Ext.Msg.alert("提示信息","basic.util.getSelectedItem: 选中项id无效");
		return undefined;
	}
	
	var item;
	var hashvo = new ParameterSet();
	hashvo.setValue("object_id", itemId);
	new Request({
		asynchronous : false,
		onSuccess : function(data) {
			item = data.getValue("people");
		},
		functionId : '9028000755'
	}, hashvo);
	
	return item;
};

/** 根据传入的bean（LazyDyncBean）为指定元素创建对应的输入框 */
basic.util.createEditFrame = function(srcElmt, bean) {
	if (!srcElmt || !bean) {
		return;
	}
	
	switch (bean.type) {
		case "Prompt" : basic.util.drawPromptBox(srcElmt, bean); break;
		case "Code" : basic.util.drawCodeBox(srcElmt, bean); break;
		case "D" : basic.util.drawDatePicker(srcElmt, bean); break;
		case "M" : basic.util.drawTextarea(srcElmt, bean); break;
		case "A" : basic.util.drawText(srcElmt, bean); break;
		case "N" : basic.util.drawNumber(srcElmt, bean); break;
		default: Ext.Msg.alert("提示信息","未知的数据类型: " + bean.type);
	}
};

/** 文本输入框 */
basic.util.drawText = function(srcElmt, bean) {
	if(bean.id === "p0801"){
		bean.value = getDecodeStr(bean.value);
	}
	srcElmt.innerHTML = bean.value || "";
	srcElmt.setAttribute("fieldType", bean.type);
	srcElmt.title = bean.value;//设置title
	if (!srcElmt.getAttribute("edit")) { // 元素原来就有edit属性，表示有预先定义的规则，不受任务状态约束
		srcElmt.setAttribute("edit", bean.edit); // 展示数据的元素需要加上编辑类型
	}
	if ("none" === bean.edit) {
		return;
	}
	var input = document.createElement("input");
	input.setAttribute("type", "text");
	input.setAttribute("fieldType", bean.type);
	input.setAttribute("desc", bean.desc);
	input.setAttribute("style", "display:none;");
	input.setAttribute("id", "edit-" + bean.id);
	input.setAttribute("value", bean.value);
	input.style.display = "none";
	input.maxLength = bean.length || "";
	if (bean.id === "p0801") {
		var len = srcElmt.parentNode.parentNode.clientWidth; // 人物名称编剧框所在的容器
//		srcElmt.style.width = (len - 20) + "px"; // 任务名称显示长度自适应
//		input.style.width = (len - 100) + "px";
		input.style.width = "90%";
		input.style.height = "20px";
		input.style.float = "left";
		input.style.marginTop = "2px";
		input.setAttribute("validator", "notnull");
		input.setAttribute("tip", "任务名称不能为空");
	}
	
	srcElmt.parentNode.insertBefore(input, basic.global.next(srcElmt));
	
	if ("always" === bean.edit) {
		srcElmt.style.display = "none";
		input.style.display = "inline";
		input.onchange = function() {
			basic.biz.save(this);
		};
	}
};

/** 数字文本输入框 */
basic.util.drawNumber = function(srcElmt, bean) {
	srcElmt.innerHTML = bean.value || "";
	srcElmt.setAttribute("fieldType", bean.type);
	if (!srcElmt.getAttribute("edit")) { // 元素原来就有edit属性，表示有预先定义的规则，不受任务状态约束
		srcElmt.setAttribute("edit", bean.edit); // 展示数据的元素需要加上编辑类型
	}
	
	if ("none" === bean.edit) {
		return;
	}
	
	var input = document.createElement("input");
	input.type = "text";
	input.style.display = "none";
	input.id = "edit-" + bean.id;
	input.style.width = (parseInt(bean.length || "0") + parseInt(bean.deWidth || "0") + 1) * 8 + "px";
	input.style.paddingRight = "5px";
	/*if(bean.id == "rank")//权重与其他对其  lis 20160629
		input.style.marginLeft = "25px",*/
	input.value = bean.value;

	input.setAttribute("fieldType", bean.type);
	input.setAttribute("desc", bean.desc);
	input.setAttribute("maxlength", (parseInt(bean.length || "0") + parseInt(bean.deWidth || "0") + 1) + "");
	if (bean.id !== "p0835" && bean.id !== "rank") { // 完成进度、权重无需校验
		// 数字校验
		input.setAttribute("validator", "regex");
		if (bean.deWidth !== "0") { // 浮点数
			input.setAttribute("tip", " 整数位在" + bean.length + "位以内,小数位在" + bean.deWidth +"位以内");
			input.setAttribute("regexp", "^\\d{1," + bean.length + "}(\\.\\d{1," + bean.deWidth + "})?$");
		} else { // 整数
			input.setAttribute("tip", " 整数位在" + bean.length + "位以内,无小数位");
			input.setAttribute("regexp", "^\\d{1," + 2 + "}$");
		}
	}
	
	input.setAttribute("size", parseInt(bean.length) + parseInt(bean.deWidth));
	
	srcElmt.parentNode.insertBefore(input, basic.global.next(srcElmt, 3));
	
	if ("always" === srcElmt.getAttribute("edit")) {
		srcElmt.style.display = "none";
		input.style.display = "inline";
		input.onchange = function() {
			basic.biz.save(this);
		};
	}
};

/** 文本域输入框  用disabled属性后无法设置字体颜色,导致看不清,用readonly wusy */
basic.util.drawTextarea = function(srcElmt, bean) {
	srcElmt.setAttribute("fieldType", bean.type);
	if (!srcElmt.getAttribute("edit")) { // 元素原来就有edit属性，表示有预先定义的规则，不受任务状态约束
		srcElmt.setAttribute("edit", bean.edit); // 展示数据的元素需要加上编辑类型
	}
	
	var area = document.createElement("textarea");
	area.className = "hj-zm-bg6-pjbz";
	area.id = "edit-" + bean.id;
	area.style.display = "inline";
	/*if("p0841" == bean.id)//评价标准与其他对其  lis 20160629
		area.style.marginLeft = "25px",*/
	area.setAttribute("fieldType", bean.type);
	area.setAttribute("desc", bean.desc);
/*	area.oninput=function(){
		if(this.scrollHeight<=84){
			this.style.height='84px';
		}
		else{
			this.style.height=this.scrollHeight+'px';
		}
		};*/
	
	// 特殊处理
	if (bean.id === "p0803"){ // 任务描述
		area.setAttribute("placeHolder", "任务描述或关键行动");
	} else {
		area.setAttribute("placeHolder", bean.desc);
	}
	
	bean.value = getDecodeStr(bean.value);
	area.value = bean.value || "";
	if ("none" === bean.edit) {
		//area.disabled = true;
		area.readOnly = true;
		area.setAttribute("readonly", "readonly");
		area.style.color = '#444444';
		area.style.backgroundColor = '#EEEEEE';
	}
	
	// 文本域双击进入编辑状态
	var dbclkHint;
	if ("always,normal".indexOf(srcElmt.getAttribute("edit")) > -1) { // 当前用户对该字段有可编辑的权限
		dbclkHint = "双击填写" + (area.getAttribute("placeHolder") || ""); // 文本域内的提示文字:任务描述||评价标准||...

		srcElmt.parentNode.ondblclick = function(e) {
			e = e || window.event;
			var target = e.target || e.srcElement;
			
			if (target.nodeName === "TEXTAREA") {
				var editBtn = document.getElementById("editTask");//取得编辑按钮element
				if (editBtn && editBtn.style.display !== "none") {
					//解决safari不能双击编辑 chent 2015/09/02 start
					//editBtn.click();
					if(editBtn.click){
						editBtn.click();
					}else{
						try{
							var evt = document.createEvent('Event');
							evt.initEvent('click',true,true);
							editBtn.dispatchEvent(evt);
						}catch(e){
							alert(e);
						}
					}
					//解决safari不能双击编辑 chent 2015/09/02 end
					
					// 文本域双击进入编辑状态后，将光标定位在当前文本框(先失去光标,在获取光标,解决ie莫名其妙的点退格键后退问题wusy)
					area.blur();
					setTimeout(function() {
						area.focus();//因为在area.blur()方法中延时了100毫秒，这里需要延时比100大
					}, 120);
					area.readOnly = false;
				}	
			}
		};
	} else {
		dbclkHint = area.getAttribute("placeHolder") || ""; // 文本域内的提示文字:任务描述||评价标准||...
	}
	
	area.setAttribute("placeHolder", dbclkHint);
	
	srcElmt.parentNode.insertBefore(area, basic.global.next(srcElmt, 3));
	
	if ("always" === srcElmt.getAttribute("edit")) {
		srcElmt.style.display = "none";
		area.style.display = "inline";
		area.onchange = function() {
			basic.biz.save(this);
		};
	} else if ("normal" === srcElmt.getAttribute("edit")) {
		//area.disabled = true;
		area.readOnly = true;
		area.setAttribute("readonly", "readonly");
		area.style.color = '#444444';
		area.style.backgroundColor = '#EEEEEE';
	}
	basic.global.compatPlaceHolder(area);// 让IE8及以下支持placeHolder属性
};

/** 日期输入框 */
basic.util.drawDatePicker = function(srcElmt, bean) {
	srcElmt.innerHTML = bean.value || "";
	srcElmt.setAttribute("fieldType", bean.type);
	if (!srcElmt.getAttribute("edit")) { // 元素原来就有edit属性，表示有预先定义的规则，不受任务状态约束
		srcElmt.setAttribute("edit", bean.edit); // 展示数据的元素需要加上编辑类型
	}
	
	if ("none" === bean.edit) {
		return;
	}
	
	var input = document.createElement("input");
	input.setAttribute("type", "text");
	input.setAttribute("fieldType", bean.type);
	input.setAttribute("desc", bean.desc);
	input.setAttribute("fieldType", bean.type);
	input.setAttribute("id", "edit-" + bean.id);
	input.setAttribute("value", bean.actualValue || "");
	input.setAttribute("actualValue", bean.actualValue || "");
	input.setAttribute("name", bean.desc);
	input.style.display = "none";
//	input.readOnly = true;

	// 日期控件：<input type="text" extra="editor" dropDown="dropDownDate">
	input.setAttribute("extra", "editor");
	input.setAttribute("dropDown", "dropDownDate");
	input.setAttribute("dataType", "simpledate");
	input.setAttribute("itemlength", "10");
	
	// 日期校验
	input.setAttribute("validator", "regex");
	input.setAttribute("tip", " 日期格式：yyyy.mm.dd");
	input.setAttribute("regexp", "^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$");
	
	srcElmt.parentNode.insertBefore(input, basic.global.next(srcElmt, 3));
	
	if ("always" === bean.edit) {
		srcElmt.style.display = "none";
		input.style.display = "inline";
	}
};

/** 代码选择框 */
basic.util.drawCodeBox = function(srcElmt, bean) {
	srcElmt.innerHTML = bean.value || "";
	srcElmt.setAttribute("fieldType", bean.type);
	srcElmt.setAttribute("valueDesc", bean.value); // 该指标项对应的描述
	if (!srcElmt.getAttribute("edit")) { // 元素原来就有edit属性，表示有预先定义的规则，不受任务状态约束
		srcElmt.setAttribute("edit", bean.edit); // 展示数据的元素需要加上编辑类型
	}
	
	if ("none" === bean.edit) {
		return;
	}
	
	// 隐藏域,存放实际值
	var input = document.createElement("input");
	input.setAttribute("type", "hidden");
	input.setAttribute("id", "store-" + bean.id);
	input.setAttribute("value", bean.actualValue || "");
	
	// Ext.form.ComboBox代码选择控件
	var div = document.createElement("div");
	div.setAttribute("fieldType", bean.type);
	div.setAttribute("fieldName", bean.id);
	div.id = "edit-" + bean.id;
	
	srcElmt.parentNode.insertBefore(div, basic.global.next(srcElmt, 3));
	srcElmt.parentNode.appendChild(input);
	
	var comboBox = new Ext.form.ComboBox({
		store : new Ext.data.SimpleStore({
			fields : ['value', 'text'],
			data : basic.util.getCodeItems(bean.codeSetId)
		}),
		valueField : 'value',
		displayField : 'text',
		typeAhead : true,
		mode : 'local',
		triggerAction : 'all',
		selectOnFocus : true,
		editable : false,
		selectOnFocus : true,
		autoScroll : true,
		value : bean.actualValue,
		matchFieldWidth : true,
		renderTo: div.id
	}).on('select', function(combo) {
		input.value = combo.getValue();
		var text = Ext.query("[type=text]", div)[0]; // 下拉框内的输入框
		text.value = text.value.replace(/&nbsp;/g, "");
		
		srcElmt.setAttribute("valueDesc", combo.getRawValue());
    });
	
	if ("always" === bean.edit) {
		srcElmt.style.display = "none";
		input.style.display = "inline";
	}
};

/** 选人提示框
<div prompt="prompt-box" class="prompt-box-div" id="director" override="replace"
		content="content-director" store="store-director" placeHolder="placeholder-director">
	<font id="placeholder-director">请选择负责人</font>
	<input type="hidden" id="store-director" />
	<div id="content-director"></div>
</div>
 */
basic.util.drawPromptBox = function(srcElmt, bean) {
	// 此处应显示人员对应的图片
	if (bean.photo) { // 包含了照片路径
		var photoNode = document.getElementById("photo-" + bean.id);
		if (photoNode) { // 预留了照片节点
			photoNode.src = bean.photo;
			photoNode.alt = bean.desc;
		}
	}
		
	// 占位符
	var font = document.createElement("font");
	font.setAttribute("id", "placeholder-" + bean.id);
	font.style.display = "none";
//	font.innerHTML = bean.desc;
	font.innerHTML = "";
	
	// 存放id的隐藏域
	var hidden = document.createElement("input");
	hidden.setAttribute("type", "hidden");
	hidden.setAttribute("id", "store-" + bean.id);
	hidden.setAttribute("value", bean.value);
	hidden.value = bean.value;
	
	// 像是候选项目的区域
	var content = document.createElement("div");
	content.setAttribute("id", "content-" + bean.id);
	
	var div = document.createElement("div");
	div.setAttribute("fieldType", "prompt");
	div.setAttribute("desc", bean.desc);
	div.setAttribute("id", "edit-" + bean.id);
	div.setAttribute("override", bean.override);
	div.setAttribute("store", "store-" + bean.id);
	div.setAttribute("content", "content-" + bean.id);
	div.setAttribute("placeHolder", "placeholder-" + bean.id);
	div.className = "prompt-box-div";
	div.style.display = "inline-block";
	div.appendChild(font);
	div.appendChild(hidden);
	div.appendChild(content);

	if (!div.getAttribute("edit")) { // 元素原来就有edit属性，表示有预先定义的规则，不受任务状态约束
		div.setAttribute("edit", bean.edit); // 展示数据的元素需要加上编辑类型
	}
	
	if ("always" === bean.edit) {
		div.className += " prompt-box-div-edit";
		div.setAttribute("status", "editing");
	} else {
		div.setAttribute("status", "done");
	}
	
	srcElmt.parentNode.insertBefore(div, basic.global.next(srcElmt, 3));
};

/** 执行编辑，将可编辑的字段的输入或选择框切换出来 */
basic.util.doEdit = function() {
	var normals = Ext.query("[edit=normal]");
	
	basic.global.every(normals, function(normal) {
		if (normal.getAttribute("fieldType") === "prompt") { // 对于待提示框的元素，显示元素即储值元素
			if (normal.id === "addFollower") {return;}
			normal.className += " prompt-box-div-edit";
			normal.setAttribute("status", "editing"); // 将当前元素置为编辑中状态
		} else if (normal.getAttribute("fieldType") === "progress") { // 进度条
			var edit = normal.getAttribute("edit");
			if (edit === "normal") {
				normal.setAttribute("status", "editing");
			}
		} else { // 其它可编辑元素
			var editNode = document.getElementById("edit-" + normal.id); // 存放实际值的元素
			if (editNode) {
				editNode.style.backgroundColor = "#FFF";
				
				//解决谷歌浏览器双击选中状态    lis 20160323 start
				if(normal.getAttribute("fieldType")=="D" || normal.id == 'rank'){
					editNode.style.display = "inline-block";
				}else{
					editNode.style.display = "block";
				}
				//end
				editNode.readOnly = false;
				
				normal.style.display = "none";
				if(editNode.id == "edit-p0803"){
					editNode.style.marginLeft = '-60px';
					edit_p0803val = editNode.innerHTML;
				}
				if(editNode.id == "edit-p0841"){
					edit_p0841val = editNode.innerHTML;
				}
				if(editNode.id == "edit-p0837"){
					edit_p0837val = editNode.innerHTML;
				}
			}
		}
		// 所有备注型文本编辑时自动扩展高度 chent 20160321 start
		if(normal.getAttribute("fieldType") === "M"){
			var elm = document.getElementById("edit-"+normal.id);
			autoTextarea(elm);
		}
		// 所有备注型文本编辑时自动扩展高度 chent 20160321 end
	});
	
	// 如果权重的编辑区域折叠，先验证其能否编辑，再决定是否展开
	/*var toEvaluate = document.getElementById("toEvaluate");
	if (!toEvaluate.disabled) {
		toEvaluate.checked = true;
		basic.biz.toggleEvaluate(toEvaluate);
	}*/
	
	// 将时间安排的“到”显示出来
	var p0813Ele = document.getElementById("p0813");
	//alert(p0813Ele.getAttribute("edit"));
	if(p0813Ele.getAttribute("edit") == "normal"){
		document.getElementById("char_zhi").innerHTML = "&nbsp;至&nbsp;";
	}
	if(p0813Ele.getAttribute("edit") == "none"){
		document.getElementById("editStartTime").style.display = "none";
		document.getElementById("editEndTime").style.display = "none";
	}
	// 让Textarea在输入内容时自适应大小
	//adjustTextarea();// 删除掉 自动扩展高度在上面已做 chent 20160321
	basic.global.compatPlaceHolder();// 让IE8及以下支持placeHolder属性
	 
};

/** 结束编辑，将可编辑的字段恢复成展现的状态 */
basic.util.endEdit = function() {
	var normals = Ext.query("[edit=normal]");
	
	basic.global.every(normals, function(normal) {
		if (normal.getAttribute("fieldType") === "prompt") { // 选人提示框
			basic.global.removeClass(normal, "prompt-box-div-edit");
			normal.setAttribute("status", "done"); // 将当前元素置为已编辑状态
		} else if (normal.getAttribute("fieldType") === "progress") { // 进度条
			var edit = normal.getAttribute("edit");
			if (edit === "normal") {
				normal.setAttribute("status", "done");
			}
		} else { // 其它可编辑元素
			var editNode = document.getElementById("edit-" + normal.id); // 存放实际值的元素
			if (editNode) {
				editNode.style.display = "none";
				
				if (normal.getAttribute("fieldType") === "D") {
					if (editNode.value !== "") {
						var arr = editNode.value.split(".");
						normal.innerHTML = arr[0] + "年" + arr[1] + "月" + arr[2] + "日";
					} else {
						normal.innerHTML = "";
					}
				} else if (normal.getAttribute("fieldType") === "Code") { // 代码型输入框
					normal.innerHTML = normal.getAttribute("valueDesc");
				} else if (normal.getAttribute("fieldType") === "M") { // 文本域输入框
					//editNode.disabled = true;
					editNode.readOnly = true;
					editNode.style.backgroundColor = '#EEEEEE';
					normal.innerHTML = "";
					editNode.style.display = "inline";
					if(editNode.id == "edit-p0803"){
						editNode.style.marginLeft = '0px';
						editNode.innerHTML = edit_p0803val;
					}
					if(editNode.id == "edit-p0841"){
						editNode.innerHTML = edit_p0841val;
					}
					if(editNode.id == "edit-p0837"){
						editNode.innerHTML = edit_p0837val;
					}
				} else {
					if (normal.id === "rank") {editNode.value = editNode.value === "0" ? "" : editNode.value;}
					normal.innerHTML = editNode.value;
				}
				
				normal.style.display = "";
			}
		}
	});
	
	// 如果权重的编辑区域展开，将其折叠
	var toEvaluate = document.getElementById("toEvaluate");
	if (!document.getElementById("edit-rank") || !document.getElementById("edit-rank").value) {
		toEvaluate.checked = false;
		basic.biz.toggleEvaluate(toEvaluate);
	}
	
	// p0813、p0815都没有值的时候（任务起止时间）将“至”去掉
	if (!document.getElementById("p0813").innerHTML && !document.getElementById("p0815").innerHTML) {
		document.getElementById("char_zhi").innerHTML = "";
	} else {
		document.getElementById("char_zhi").innerHTML = "&nbsp;至&nbsp;";
	}
};

/** ####################### 进度条 ######################### */
/** 初始化进度条,bean为p08.p0835的相关信息 */
basic.util.progressBarWidget = function(pace, handler) {
	handler = handler || {};
	var trigger = {
		init : handler.init || undefined, // 进度条的初始化操作,如进度
		beforeDrag : handler.beforeDrag || undefined, // 拖动之前的动作,如判断激活拖动操作的条件,返回false则不执行拖拽
		afterDragged : handler.afterDragged || undefined // 拖动完成后的操作,如判断激活拖动结束操作的条件,返回false则不做处理
	};
	
	var bar = document.getElementById("progressBar");
	if (!bar) { // 未找到进度条元素
		return;
	}
	
	var barLoc = basic.util.getAbsoluteLocation(bar);
	var barTip = document.getElementById("progressBarTip");
	var h3 = Ext.query('h3', bar)[0]; // 宽度表示进度,值来自数据库p0835,
	h3.style.width = (parseInt(pace) * 2) + "px"; // 初始化进度
	var h4 = Ext.query('h4', bar)[0]; // 表示拖动时的进度,最终要与h3同步
	
	if (trigger.beforeDrag) {
		trigger.init.call(null, bar); // 执行指定的初始化函数
	}

	// 将h4适应h3的宽度
	var adaptH4ForH3 = function() {
		h4.style.width = h3.clientWidth + "px";

		var h3PCT = Math.round(h3.clientWidth / bar.clientWidth * 100); // h3的宽度占bar的百分比
		barTip.innerHTML = h3PCT + "%";
	};
	adaptH4ForH3();
	
	bar.onmousemove = function(e) {
		if (trigger.beforeDrag && trigger.beforeDrag.call(null, bar) === false) { // 执行指定的拖拽前校验
			return;
		}
		
		e = e || window.event;

		var offsetX = e.offsetX || e.pageX - barLoc.left; // 鼠标距bar左侧的距离
		var h4PCT = Math.round((offsetX || 0) / bar.clientWidth * 100); // h4的宽度占bar的百分比
		h4PCT = h4PCT > 100 ? 100 : h4PCT;

		h4.style.width = h4PCT + '%';
		barTip.innerHTML = h4PCT + "%";
	};
	
	bar.onmouseleave = function() {
		adaptH4ForH3();
	};
	
	bar.onclick = function(e) {
		e = e || window.event;
		
		var offsetX = e.offsetX || e.pageX - barLoc.left; // 鼠标距bar左侧的距离
		var h3PCT = Math.round((offsetX || 0) / bar.clientWidth * 100); // h3的宽度占bar的百分比

		if (trigger.afterDragged && trigger.afterDragged.call(null, bar, h3PCT) === false) { // 执行指定的拖拽完成后校验
			return;
		}
		
		h3.style.width = h3PCT + '%';
		adaptH4ForH3();
	};
};

/** 根据代码集id查找所有代码项 */
basic.util.getCodeItems = function(codeSetId) {
	var arr;
	
	var hashvo = new ParameterSet();
	hashvo.setValue("codeSetId", codeSetId);
	new Request({
		asynchronous : false,
		onSuccess : function(data) {
			arr = data.getValue("code-" + codeSetId);
		},
		functionId : '9028000758'
	}, hashvo);
	
	return arr;
};

/** ################################### 任务关注人 #################################### */
/* 显示关注人的删除元素 */
basic.util.showStaffDelNode = function(staff) {
	if (!staff || staff.id.indexOf("director") > -1) {return;}
	
	var edit = document.getElementById(staff.id.substring(0, staff.id.indexOf("-"))).getAttribute("edit");
	if ("always,normal".indexOf(edit) === -1) {
		return;
	}
	
	var del = staff.getElementsByTagName("div")[0];
	if (del && "always,normal".indexOf(edit) > -1) {
		del.style.display = "block";
	}
};

/* 隐藏删除元素 */
basic.util.hideStaffDelNode = function(staff) {
	var del = staff.getElementsByTagName("div")[0];
	if (del) {
		del.style.display = "none";
	}
};

/** 保存新的任务成员   计划页和任务页都会用到这个方法,用p0800区分*/
basic.util.saveStaff = function(staffId, staffAddBtn, p0800,p0700,p0723,objectid,rowIndex) {
	if (!staffAddBtn) {return;}
	var staff = staffAddBtn.id.substring(0, staffAddBtn.id.indexOf("-"));
	var vo = new ParameterSet();
	var p0800temp = p0800;
	basic.biz.prepareStaticData(vo);
	if(p0800 != undefined){
		vo.setValue("p0800", p0800);
		vo.setValue("p0700", p0700);
		vo.setValue("p0723", p0723);
		vo.setValue("objectid", objectid);
	}
	vo.setValue("staffid", staffId);
	vo.setValue("staff", staff || "");
	new Request({
		method: 'post',
		asynchronous: true,
		onSuccess: function(data) {
			var staffBean = data.getValue("bean");
			if (!staffBean) {return;}
			if(p0800temp != undefined){
				if(staff=="director"){
					Ext.getDom("prin"+rowIndex).innerHTML = staffBean.fullName;
					/*
				 	 * 通过选中行来修改store中的数据
				 	 */
					var selModel =g_tree.getSelectionModel();
					var node =selModel.getSelection();
					if(node.length>0){
					    var data = node[0].data;
					    data.principal = staffBean.fullName;
					}
				}else if(staff=="member"){
					var str = Ext.getDom("particspan"+rowIndex).innerHTML;
					if(str==""){
						Ext.getDom("particspan"+rowIndex).innerHTML = staffBean.fullName;
					}else if(str.length>0 && str.indexOf("...")>0){
						
					}else if(str.length>0 && str.indexOf("、")>0 && str.indexOf("...")<0){
						Ext.getDom("particspan"+rowIndex).innerHTML = str+"...";
					}else if(str.length>0 && str.indexOf("、")<0){
						Ext.getDom("particspan"+rowIndex).innerHTML = str + "、" + staffBean.fullName;
					}
				}
			}else{
				var display = document.getElementById(staff + "-display");
				if (staff === "director") {// 负责人只能有一个
					if(display){
						display.innerHTML = "";
					}
	
					var newFollower = data.getValue("newFollower");
					if (newFollower) {
						var disFollower = document.getElementById("follower-display");
						disFollower.appendChild(basic.util.createStaffNode(newFollower, "follower"));
					}
				}
				
				//刷新任务列表  lis 20160317 start
				var othertask = document.getElementById("param.othertask").value;//1：穿透任务，0：非穿透任务
				if(p0800temp != undefined){
					isNeedToRefresh(p0700,p0800temp,p0723);
				}else{
					var p0700 = document.getElementById("param.p0700").value;
					var p0723 = document.getElementById("param.p0723").value;
					var p0800 = document.getElementById("param.p0800").value;
					if (staff === "director") {
						if(othertask == "1"){//是穿透任务
							p0700 =  data.getValue("directorP0700");
							p0723 =  data.getValue("p0723");
							p0800 =  data.getValue("p0800");
						}
						//修改任务负责人后刷新任务列表 lis 20160319
						isNeedToRefresh(p0700,p0800,p0723);
					}else if(staff=="member"){
						//删除任务成员后刷新任务列表 lis 20160321
						isNeedToRefresh(p0700,p0800,p0723);
					}
				}
				//lis 20160317 end
				
				if(staffBean != null){
					if(display){
						display.appendChild(basic.util.createStaffNode(staffBean, staff));
					}
				}
				var toRemove = data.getValue("toRemove");
				if (toRemove) {
					// 更新负责人后有可能会连带删除对应的参与人或关注人
					//linbz 20170317  22622 页面移除需要删除的人员信息
					if(document.getElementById("member-" + toRemove)){
						var delmem = document.getElementById("member-" + toRemove);
						var staffm = delmem.id.substring(0, delmem.id.indexOf("-"));
						var staffNodem = delmem.parentNode.parentNode;
						document.getElementById(staffm + "-display").removeChild(staffNodem);
						basic.util.removeStaff(delmem, true);
					}
					if(document.getElementById("follower-" + toRemove)){
						var delfoll = document.getElementById("follower-" + toRemove);
						var stafff = delfoll.id.substring(0, delfoll.id.indexOf("-"));
						var staffNodef = delfoll.parentNode.parentNode;
						document.getElementById(stafff + "-display").removeChild(staffNodef);
						basic.util.removeStaff(delfoll, true);
					}
				}
				
				var removeFollower = data.getValue("removeFollower");
				if (removeFollower) {
					// 更新任务成员后后有可能会连带删除对应关注人
					basic.util.removeStaff(document.getElementById("follower-" + removeFollower), true);
				}
				
				//保存新的任务成员后需要将报批按钮显示
				basic.biz.tmp.privilege = data.getValue("privilege");
				var p = basic.biz.tmp.privilege;
				if (p.approveBtn) { // 报批或批准按钮
					document.getElementById("transit").style.display = "inline-block";
					var img = Ext.query("img", document.getElementById("transit"))[0];
					    img.src = "/workplan/image/baopi.png";
				}
				//上级修改下级任务wusy
				var info = data.getValue("approveInfo");
				if("approved" == info){
					document.getElementById("transit").style.display = "none";
					location.reload();
				}
			}
			if(window.getPlanAndBtnStatus){
				getPlanAndBtnStatus();//更新计划和按钮状态
			}else if(window.parent && window.parent.window.getPlanAndBtnStatus){//工作计划页面调用任务详情界面的情况
				window.parent.window.getPlanAndBtnStatus();//更新计划和按钮状态
			}
		},
		functionId:'9028000760'
	}, vo);
};

/**
 * 批量保存人员
 * haosl
 */
basic.util.bantchSaveStaff = function(map){
	var staffAddBtn = map.get('btn');
	var staffids = map.get('staffids');
	var p0700 = document.getElementById("param.p0700").value;
	var p0723 = document.getElementById("param.p0723").value;
	var p0800 = document.getElementById("param.p0800").value;
	var objectid = document.getElementById("param.objectid").value;
	if(!staffAddBtn){return;}
	var staff = staffAddBtn.id.substring(0, staffAddBtn.id.indexOf("-"));
	var param = new HashMap();
	param.put("staff", staff || "");
	param.put("staffids", staffids);
	param.put("p0800",p0800);
	param.put("p0700",p0700);
	param.put("p0723",p0723);
	param.put("objectid",objectid);
	Rpc({functionId:'9028000760',success:addStaffOK},param);
};

/**
 * 批量添加任务成员成功的回调
 * haosl
 * 
 * 20160701
 */
function addStaffOK(data){
	var jsonObj = Ext.decode(data.responseText);
	var staffBeans = jsonObj.beans;
	if (staffBeans==null || staffBeans.length==0) {return;}
	//刷新任务列表
	var othertask = document.getElementById("param.othertask").value;//1：穿透任务，0：非穿透任务

	var p0700 = document.getElementById("param.p0700").value;
	var p0723 = document.getElementById("param.p0723").value;
	var p0800 = document.getElementById("param.p0800").value;
	//删除任务成员后刷新任务列表
	isNeedToRefresh(p0700,p0800,p0723);
	if(jsonObj.staff=="member"){
		var display = document.getElementById("member-display");
		if(display){
			for(var i=0&&staffBeans!=null;i<staffBeans.length;i++){
				var staffBean = staffBeans[i];
				display.appendChild(basic.util.createStaffNode(staffBean, "member"));
			}
		}
		var removeFollowers = jsonObj.removeFollowers;
		if (removeFollowers) {
			// 更新任务成员后后有可能会连带删除对应关注人
			for(var i = 0;i<removeFollowers;i++){
				basic.util.removeStaff(document.getElementById("follower-" + removeFollowers[i]), true);
			}
		}
		//保存新的任务成员后需要将报批按钮显示
		basic.biz.tmp.privilege = jsonObj.privilege;//31459 其他地方都是jsonObj.获取，不知这为何用data.getValue("privilege");
		var p = basic.biz.tmp.privilege;
		if (p.approveBtn) { // 报批或批准按钮
			document.getElementById("transit").style.display = "inline-block";
			var img = Ext.query("img", document.getElementById("transit"))[0];
			    img.src = "/workplan/image/baopi.png";
		}
		//上级修改下级任务
		var info = jsonObj.approveInfo;
		if("approved" == info){
			document.getElementById("transit").style.display = "none";
			location.reload();
		}
		if(window.getPlanAndBtnStatus){
			getPlanAndBtnStatus();//更新计划和按钮状态
		}else if(window.parent && window.parent.window.getPlanAndBtnStatus){//工作计划页面调用任务详情界面的情况
			window.parent.window.getPlanAndBtnStatus();//更新计划和按钮状态
		}
	}else if(jsonObj.staff=="follower"){
		var disFollower = document.getElementById("follower-display");
		if(disFollower){
			for(var i=0&&staffBeans!=null;i<staffBeans.length;i++){
				var staffBean = staffBeans[i];
				disFollower.appendChild(basic.util.createStaffNode(staffBean, "follower"));
			}
		}
	}
};
/** 删除人员
 * @param delNode 删除按钮,id=staff-objectId
 * @param enforceable 是否强制删除
 */
basic.util.removeStaff = function(delNode, enforceable) {
	if (!delNode) {return;}
	var staff = delNode.id.substring(0, delNode.id.indexOf("-"));
	var desc = staff === "member" ? "任务成员" : "关注人";
	if (!enforceable){
		/** 弹出信息位置重新计算 chent 20160319 start */
		var position = getMsgPosition();
		//confirm弹出前，计算当前滚动条位置，放置alert弹出后滚动条位置改变
		var scrollTop = parent.window.Ext.getBody().getScroll().top;
		Ext.Msg.confirm("提示信息","将 " + (delNode.getAttribute("fullName") || "其") + " 从" + desc + "中移出？",function(e){
				if (e == "yes") {
						var staffId = delNode.id.substring(delNode.id.indexOf("-") + 1);
	
						var vo = new ParameterSet();
						basic.biz.prepareStaticData(vo);
						vo.setValue("staff", staff);
						vo.setValue("staffid", staffId);
						new Request({
							method: 'post',
							asynchronous: true,
							onSuccess: function(data) {
								if (data.getValue("removed") === "ok") {
									var staffNode = delNode.parentNode.parentNode;
									document.getElementById(staff + "-display").removeChild(staffNode);
									var p0700 = document.getElementById("param.p0700").value;
									var p0800 = document.getElementById("param.p0800").value;
									var p0723 = document.getElementById("param.p0723").value;
									var recordId = document.getElementById("param.recordId").value;
									isNeedToRefresh(p0700,p0800,p0723,recordId);
								}
							},
							functionId:'9028000761'
						},vo);
				} else {
					return;
				}
			}).alignTo(Ext.getBody(),"tl",[position.x,position.y]);
			//恢复confirm弹出前的滚动条位置 兼容IE、firefox、chrome、safari chent 20160319
        /*【46890】7.6程序：工作计划在删除有多个层级的任务中的任务成员的时候界面跳动，建议不要闪动了
        修改：如果滚动条在0的位置的时候就不重新定位了  haosl  2019年5月22日*/
        if (scrollTop>0) {
            if (Ext.isIE || Ext.firefoxVersion != 0) {//IE、firefox
                window.parent.document.documentElement.scrollTop = scrollTop;
            } else {//chrome、safari
                parent.window.Ext.getBody().setScrollTop(scrollTop);
            }
        }
			/** 弹出信息位置重新计算 chent 20160319 end */
	}
};

/* 创建人员节点 */
basic.util.createStaffNode = function(bean, staff) {
	if (!bean) {return null;}
	
	var dl = document.createElement("dl");
	dl.setAttribute("onmouseover", "basic.util.showStaffDelNode(this)");
	dl.setAttribute("onmouseout", "basic.util.hideStaffDelNode(this)");//safari浏览器不支持onmouseleave chent20150908
	dl.id = staff + "-node";
	
	var html = "";
	html += "<dt title='" + (bean.fullName || "") + "'>";
	html += '	<img class="img-circle" src="' + bean.photo + '" />';
	html += '	<div onclick="basic.util.removeStaff(this)" fullName="' + bean.fullName + '" id="' + staff + '-' + bean.id + '">';
	html += '		<img style="width:20px;height:20px" src="/workplan/image/remove.png">';
	html += '	</div>';
	html += '</dt>';
	html += '<dd>' + bean.abbr + '</dd>';
	
	dl.innerHTML = html;
	
	return dl;
};

/** 获取字符串在页面中的宽度 */
basic.util.strWidth = function(str) {
	var d = document.getElementById("strWidth");
	d.innerHTML = str;
	return {w:d.clientWidth, h:d.clientHeight};
}

//处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外
function forbidBackSpace(e) {
    var ev = e || window.event; //获取event对象 
    var obj = ev.target || ev.srcElement; //获取事件源 
    var t = obj.type || obj.getAttribute('type'); //获取事件源类型 
    //获取作为判断条件的事件类型 
    var vReadOnly = obj.readOnly;
    var vDisabled = obj.disabled;
    //处理undefined值情况 
    vReadOnly = (vReadOnly == undefined) ? false : vReadOnly;
    vDisabled = (vDisabled == undefined) ? true : vDisabled;
    //当敲Backspace键时，事件源类型为密码或单行、多行文本的， 
    //并且readOnly属性为true或disabled属性为true的，则退格键失效 
    var flag1 = ev.keyCode == 8 && (t == "password" || t == "text" || t == "textarea") && (vReadOnly == true || vDisabled == true);
    //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效 
    var flag2 = ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea";
//    alert(flag1);
//    alert(flag2);
    //判断 
    if (flag2 || flag1) return false;
}
// 任务详情页面调用工作计划页面的刷新列表的方法，如果没有工作计划页面则不刷新 chent
function isNeedToRefresh(p0700,p0800,p0723){
	if(window.parent && window.parent.window.isNeedToRefresh){//工作计划页面调用任务详情界面的情况
		var recordId = document.getElementById("param.recordId").value;
		window.parent.window.isNeedToRefresh(p0700,p0800,p0723,recordId);
	}else{//工作总结 
		//暂不处理
	}
}


