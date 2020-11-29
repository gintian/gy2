var basic = basic || {};
basic.global = {};

/** 判断两元素是否为同一元素 */
basic.global.isSameNode = function(a, b) {
	if (!a || !b) {
		alert("basic.global.isSameNode: 对比的两个节点不是有效地节点");
		return false;
	}
	
	if (a.isSameNode) {
		return a.isSameNode(b);
	} else {
		return a === b;
	}
};

/** 判断parent节点是否存在一个child子孙节点 */
basic.global.hasChild = function(parent, child) {
	if (!parent || !child) {
		alert("basic.global.hasChild do not accept any invalid nodes");
		return;
	}
	
	var children = parent.getElementsByTagName("*");
	var result = basic.global.each(children, function(node) {
		return basic.global.isSameNode(node, child);
	});
	
	return !!result;
};

/** 查询选种元素在数组中的位置 */
basic.global.indexOf = function(arr, item) {
	if (!arr) {
		alert("basic.global.indexOf: 无效的数组对象");
		return;
	}
	
	for (var i = 0; i < arr.length; i++) {
		if (item === arr[i]) {
			return i;
		}
	}
	
	return -1;
}

/** 遍历集合，为每一个元素调用指定函数 */
basic.global.every = function(arr, fn, _this) {
	if (!arr) {
		alert("basic.flobal.each: arr 不是一个有效地数组对象");
		return;
	}
	if (typeof fn !== "function") {
		alert("basic.global.each: fn 不是一个函数对象");
		return;
	}
	
	for (var i = 0; i < arr.length; i++) {
		_this = !_this ? arr[i] : _this;
		fn.call(_this, arr[i], i, arr);
	}
};

/** 遍历集合，在中断之前为每一个元素调用指定函数，中断的条件有指定函数来决定 */
basic.global.each = function(arr, fn, _this) {
	if (!arr) {
		alert("basic.global.each: arr 不是一个有效地数组对象");
		return;
	}
	if (typeof fn !== "function") {
		alert("basic.global.each: fn 不是一个函数对象");
		return;
	}
	
	for (var i = 0; i < arr.length; i++) {
		_this = !_this ? arr[i] : _this;
		if (fn.call(_this, arr[i], i, arr)) {
			return arr[i];
		}
	}
	
	return undefined;
};

/** 判断元素是否有指定的class */
basic.global.hasClass = function(elmt, clsName) {
	if (!elmt) {
		alert("basic.global.hasClass: elmt 不是一个有效的元素");
		return;
	}
	
	var arr = elmt.className.split(" ");
	for (var i = 0; i < arr.length; i++) {
		if (arr === clsName) {
			return true;
		}
	}
	
	return false;
};

/** 下一个元素,如果已经是最后的元素则返回null */
basic.global.next = function(curr, nodeType) {
	if (!curr) {
		alert("basic.global.nextElement: 不能接受一个无效的节点");
	}
	
	if (curr.nodeType != 1) {
		alert("basic.global.previousElement: 非元素节点是无意义的");
		return null;
	}

	nodeType = nodeType || 1; // 1-元素,2-属性,3-文本,8-注释,9-文档
	
	var next = curr.nextSibling;
	while (next && next.nodeType != nodeType) {
		next = next.nextSibling;
	}
	
	return next;
};

/** 前一个元素,如果已经是第一个元素则返回null */
basic.global.prev = function(curr, nodeType) {
	if (!curr) {
		alert("basic.global.previousElement: 不能接受一个无效的节点");
		return null;
	}
	
	if (curr.nodeType != 1) {
		alert("basic.global.previousElement: 非元素节点是无意义的");
		return null;
	}
	
	nodeType = nodeType || 1; // 1-元素,2-属性,3-文本,8-注释,9-文档
	
	var prev = curr.previousSibling;
	while (prev && prev.nodeType != nodeType) {
		prev = prev.previousSibling;
	}
	
	return prev;
};

/** 删除一个class */
basic.global.removeClass = function(elmt, clazz) {
	if (!elmt) {
		alert("basic.global.removeClass: 不能接受一个无效的节点");
		return null;
	}
	
	var cls = elmt.className.split(" ");
	for (var i = 0; i < cls.length; i++) {
		if (cls[i] === clazz) {
			cls.splice(i, 1);
		}
	}
	
	elmt.className = cls.join(" ");
};

/** 浏览器标识 */
basic.global.isIE = function() {
	return !!window.attachEvent;
};

/** 让低版本IE支持placeHolder属性 */
basic.global.compatPlaceHolder = function(elmt) {
	if (!basic.global.isIE()) {return;} // 新浏览器自带占位符支持
	
	var phs = [];
	if (!elmt) {
		phs = Ext.query("input[type=text][placeHolder],textarea[placeHolder]"); // 所有带placeHolder属性的输入框和文本域
	} else {
		phs[phs.length] = elmt;
	}
	
	basic.global.every(phs, function(input) {
		basic.global.refreshInputPlaceHolder(input);
		
		var ph = input.getAttribute("placeHolder");
		var focus = function() {
			if (!input.value || input.value === ph) { // 无值或者置为placeHolder
				if(input.getAttribute("readonly")=="") //haosl 20160907
					input.value = "";
				else
					input.value = ph;	//haosl 20160907
			}
			basic.global.removeClass(input, "place-holder-compat");
		};
		var blur = function() {
			setTimeout(function() {
				if (!input.value || input.value === ph) { // 值有效且不与占位符相等
					input.value = ph;
					input.className += " place-holder-compat";
				}
			}, 100);
		};
		
		if(!(input.getAttribute("readonly") === "readonly")){
			Ext.get(input).on("focus", focus);
			Ext.get(input).on("blur", blur);
		}
	});
};

/** 刷新占位符 */
basic.global.refreshInputPlaceHolder = function(input) {
	var refresh = function(input) {
		var ph = input.getAttribute("placeHolder");
		if (!input.value || input.value === ph) { // 无值或者置为placeHolder
			input.value = input.value || ph;
			input.className += " place-holder-compat";
		} else {
			basic.global.removeClass(input, "place-holder-compat");
		}
	};
	
	if (input) { // input有效，表示刷新指定的输入框的占位符
		refresh(input);
	} else { // 无效，表示刷新所有输入框的占位符
		var phs = Ext.query("input[type=text][placeHolder]"); // 所有带placeHolder属性的输入框
		basic.global.every(phs, refresh);
	}
};

/** 去除字符创前后的空格符 */
basic.global.trim = function(str) {  
    return str.replace(/(^\s*)|(\s*$)/g, "");
};

/** 停止冒泡 */
basic.global.stopBubble = function(e) {
	if (!e) {return;}
	if (e.stopPropagation) { // W3C阻止冒泡方法  
        e.stopPropagation();  
    } else {  
        e.cancelBubble = true; // IE阻止冒泡方法  
    }
};

/** 获取查询参数 */
basic.global.getParameter = function(key) {
	var search = location.search.substring(1);
	var reg = new RegExp(key + "=[^&]*", "g");
	var result = reg.exec(search);
	
	if (!result) {return null;}
	
	return result[0].substring(result[0].indexOf("=") + 1);
};

/** 将HTML格式的字符串转换成JS的字符串 */
basic.global.formatText = function(text) {
	if (!text) {return "";}
	
	text = text.replace(new RegExp("<br/>","gi"), "\n");
	text = text.replace(new RegExp("<br />","gi"), "\n");
	text = text.replace(new RegExp("<br>","gi"), "\n");
	text = text.replace(new RegExp("&nbsp;","gi"), " ");
	text = text.replace(new RegExp("&gt;","gi"), ">");
	text = text.replace(new RegExp("&lt;","gi"), "<");
	text = text.replace(new RegExp("&amp;","gi"), "&");
	
	return text;
};

/**登录超时的处理,每个ajax的回调方法中应该先调用此方法(从mainpanel.jsp中截取 wusy) * */

basic.global.logonOut = function (){
     var map = new HashMap();
     map.put("module", -1);
     map.put("auth_lock","true");
     Rpc({functionId:'1010010206',success:authorize},map); 
	 
}	
function authorize(response){
		var value=response.responseText;
		var map=Ext.decode(value);
		if(map.succeed==false){
			alert(map.message);	
	         if(map.message.indexOf("会话超时")!=-1){
	             var newwin=window.open(window.location,"_top","toolbar=no,location=no,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
	             window.opener=null;
	             self.close();
	             return;
	         }
		}
}

/**验证日期格式是否正确,日期为空("")默认正确格式,验证的格式为2015.06.13,如需验证2015-06-03,把所有(.)替换成(-)即可  wusy*/
function workPlanCheckDate(date){  
    var result = date.match(/((^((1[8-9]\d{2})|([2-9]\d{3}))(.)(10|12|0?[13578])(.)(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))(.)(11|0?[469])(.)(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))(.)(0?2)(.)(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)(.)(0?2)(.)(29)$)|(^([3579][26]00)(.)(0?2)(.)(29)$)|(^([1][89][0][48])(.)(0?2)(.)(29)$)|(^([2-9][0-9][0][48])(.)(0?2)(.)(29)$)|(^([1][89][2468][048])(.)(0?2)(.)(29)$)|(^([2-9][0-9][2468][048])(.)(0?2)(.)(29)$)|(^([1][89][13579][26])(.)(0?2)(.)(29)$)|(^([2-9][0-9][13579][26])(.)(0?2)(.)(29)$))/);
    //alert(date);
    if(date != ""){
   	 if(result==null){
         Ext.Msg.alert("提示信息","请输入正确的日期格式!");   
            return false;
        }else{
       	 return true;
        }
    }else{
   	 return true;
    }

}
