//Ext.onReady(function() {
//	//为document添加点击事件，如果事件元素在提示框之外，则隐藏提示框
//	personPickerIsOpen = false;//选人控件是否已经打开，仅用于单选选人 chent
//	Ext.get(document).on("click", function(e) {
//		
//		var win =  Ext.getCmp('person_picker_single_view')
//		if (PersonPicker.alive() && personPickerIsOpen) {
//	        if (typeof(personpicker_timer) == "undefined") {
//	        }else {
//				clearInterval(personpicker_timer);
//	        }
//			win.close();
//			personPickerIsOpen = false;
//			return ;
//		}
//		if(win){
//			personPickerIsOpen = true;
//		}
//	});
//});

var PersonPicker = (function () {
//	document.writeln('<link rel="stylesheet" type="text/css" href="/components/personPicker/PersonPicker.css">');
//	document.writeln("<script type='text/javascript' src='/ext/rpc_command.js'></script>");
	
//haosl update 2018年7月13日  修改样式加载的方式，使用document.writeln有时会加载不上css样式文件(如通过动态写入personpicker.js文件) start
	//加载样式文件和必要的js文件
	var headEl = document.getElementsByTagName('head')[0];
	//样式文件
	var styleEl = document.createElement('link');
	styleEl.setAttribute('type','text/css');
	styleEl.setAttribute('rel','stylesheet');
	styleEl.setAttribute('href','/components/personPicker/PersonPicker.css');
	headEl.appendChild(styleEl);
	//js文件  防止重复引用rpc低版本浏览器报错
	if(!Rpc){
		var jsEl = document.createElement('script');
		jsEl.setAttribute('type','text/javascript');
		jsEl.setAttribute('src','/ext/rpc_command.js');
		headEl.appendChild(jsEl);
	}
	
//haosl update 2018年7月13日  修改样式加载的方式，使用document.writeln有时会加载不上css样式文件 end
	var instance; // 保存实例，关闭的时候执行instance.close();
	
	/** 常量 */
	var GET = "GET";
	var POST = "POST";
	var QUIRKS = document.compatMode == "BackCompat" ? true : false; // 怪异模式(BackCompat)
	//var BODY = QUIRKS ? document.getElementsByTagName("body")[0] : document.documentElement;
	var BODY = QUIRKS ? document.documentElement : document.documentElement;//人事异动主界面是怪异模式
	var EMPTY_FUNCTION = function () {};
	var IE = /msie/i.test(navigator.userAgent) ? true : false;
	var FF = !IE ? true : false;
	var IE7 = /msie 7.0/i.test(navigator.userAgent) ? true : false;
	var IE8 = /msie 8.0/i.test(navigator.userAgent) ? true : false;
	var IE9 = /msie 9.0/i.test(navigator.userAgent) ? true : false;
	var PLACEHOLDER = ""; // 输入框内没有文字时显示的文本
	var TEMPLATE_TD = "__tplTd__"; // 模板内包裹其他页面的td,用来获得页面scrollTop
	var ONLYNAME = "";	//唯一标识指标名称
	var multiple_innerHTML;
	var single_innerHTML;
	(function () {
		ajax({
			method: GET,
			async: true,
			url: "/components/personPicker/frame-multiple.jsp",
			callback: function (xhr) {
				multiple_innerHTML = xhr.responseText;
				var sindex = multiple_innerHTML.indexOf("{uniqueness-value}")+18;
				var eindex = multiple_innerHTML.indexOf("{uniqueness-value}",sindex+1);
				if(sindex>-1 && eindex >-1)
					ONLYNAME = multiple_innerHTML.substring(sindex,eindex);
			}
		});
		ajax({
			method: GET,
			async: true,
			url: "/components/personPicker/frame-single.jsp",
			callback: function (xhr) {
				single_innerHTML = xhr.responseText;
				var sindex = single_innerHTML.indexOf("{uniqueness-value}")+18;
				var eindex = single_innerHTML.indexOf("{uniqueness-value}",sindex+1);
				if(sindex>-1 && eindex >-1)
					ONLYNAME = single_innerHTML.substring(sindex,eindex);
			}
		});
	})();
	
	/** ####################################### 对外接口 ############################################ */
	/** 构造函数 */
	function PersonPicker(option, target) {
//		if (instance) {instance.close();}

		this.target = $(target); // 目标对象，触发弹出提示框的元素
		this.keywordtaskQueue = new Array();//关键字查询任务队列，顺序查询后台
		this.pickerElement = null; // 选人框的节点
		this.contentNbs = "";//区分人员库时，记录当前操作的人员库
		this.uniqueness = "";
		this.data = { // 数据
			person: [],
			unit: [],
			checked: [],
			candidate: []
		};
		
		this.option = { // 默认配置
			multiple: true, // 是否多选
			recommend: [], // 推荐显示的人员
			deprecate: [], // 不赞成出现的人员
			callback: EMPTY_FUNCTION, // 确认选择的操作
            beforeFinishVerifyFunc:EMPTY_FUNCTION,//触发回调事件前校验数据方法，方法内需返回true或false，若不为true 不调用回调方法
			text: "", // 人员类别，如“任务负责人”，添加按钮的文字
			titleText:"",//标题，不 传则显示“请选择”
			nbases: '', // 人员库范围。空为默认走认证库。如：Usr,Ret
			orgid: '', // 组织机构，空为默认全部。如：010102,020202 
			extend_str: '', // 扩展查询语句，如：select a0100 from ${nbase}A01 where a0102='01'或
				//select a0100 from xxx where lower(nbase)='${nbase}' and  a0102='01'   注：${nbase}   为人员库变量，组装SQL时会自动替换成人员库 前缀小写
			addunit:false, //是否可以添加单位。Ps：该参数启用时不能选人
			adddepartment:false, //是否可以添加部门。Ps：该参数启用时不能选人
			isSelfUser:true,//是否选择自助用户，否则选业务用户，默认自助用户  zhaoxg add 20105-9-7
			isMiddle:false, //是否居中显示，只针对单人控件有效，默认定位到触发焦点位置 否则屏幕中间  zhaoxg add 20105-9-7
			header:false,//是否显示标题框，只针对单选控件有效
			isPrivExpression:true,//是否启用人员范围（含高级条件），默认启用
			recruitmentSpecial:false,//显示已聘用/未聘用人数。注意：只有招聘模块调用时可用，招聘专有。
			addpost:false, //是否可以添加岗位。Ps：该参数启用时不能选人 chent 20161102
			multipleAndSingle:false, //用多选的表现方式来单选 chent 20161220
			validateSsLOGIN:true,//是否启用认证库校验 chent 20170313
			selfUserIsExceptMe:true,//业务用户时是否排除自己默认排除。chent 20170329
			selectByNbase:false,//是否按不同人员库显示 chent 20170419
			defaultSelected:[],//默认已选。形式：人员：['Usr00000024', 'Usr00000027']（'Usr00000024'需加密）;机构：['UN0101', 'UM0102']（'UN0101'需加密）;业务用户：[‘加密后的账号1’,‘加密后的账号2’]。说明:1、只有多选时有效。  chent 20170509 
			isZoom:true//是否启用界面自动缩放 by haosl 2018-3-29

		};
		instance = this;
		extend(this.option, option);
		if(this.option.addunit === true || this.option.adddepartment === true || this.option.addpost === true){
			PLACEHOLDER = "输入名称";
		} else {
			PLACEHOLDER = "输入姓名/拼音简码/email";
            PLACEHOLDER += Ext.isEmpty(ONLYNAME) || PLACEHOLDER.indexOf(ONLYNAME)>-1?"":"/"+ONLYNAME+"...";
		}
		
		//instance.data.unit.hasAdded = {};
	}
	
	PersonPicker.prototype = {
		open: function () { // 弹出提示框
			addClass($("body>table td")[0], TEMPLATE_TD);
			if (this.option.multiple === true) {
				multiple();
			} else {
				single();
			}
			subordinate();
			if (this.option.recommend && this.option.recommend.length > 0) {
				if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
					recommend([]);
				}else if(instance.option.addunit === false && instance.option.adddepartment === false  && instance.option.addpost === false){
					recommend(this.option.recommend);
				}
				showCandidate();
			}
			listen();
			//延时设置单位和人员节点的宽度  haosl 2017-9-1
			resetNodeWidth();
			// 开启定时任务：关键字查人
			startTimer();
		},
		close: function () { // 关闭提示框
			hide();
			this.pickerElement.parentNode.removeChild(this.pickerElement);
			instance = null;
			removeClass($("body>table td")[0], TEMPLATE_TD);
			endTimer();
		},
		closeOnClick: function (e) { // 响应点击事件关闭提示框
			e = e || window.event;
			var t = e.target || e.srcElement;
			
			var p = this.pickerElement;
			var target = this.target;
			// 如果点击事件不是发生在提示框内
			if (!hasChild(p, t) && !isSameNode(p, t) && !isSameNode(target, t) && !hasChild(target, t)) {
				this.close();
			}
		}
	};
	
	PersonPicker.alive = function () {
		
		return (!!instance && !instance.option.multiple);
	};
	
	PersonPicker.getInstance = function () {
		return instance;
	};

	/** ####################################### 控件-多选 ############################################ */
	function multiple() {
		// 打开选多人时，如果有选单人控件，先把选单人控件关闭
		var win = Ext.getCmp('person_picker_single_view');
		if(win) {
			if (typeof(personpicker_timer) == "undefined") {
	        }else {
				clearInterval(personpicker_timer);
	        }
			win.close();
//			personPickerIsOpen = false;
		}
		instance.pickerElement = multipleFrame();
		var parent = BODY;//scrollableParent(instance.target);//工作计划页面的任务列表中调用时显示错误 chent 20160215
		var scrollTop = 0;
		if (parent.nodeName == "BODY" || hasClass(parent, TEMPLATE_TD)) {
			scrollTop += BODY.scrollTop;
		} else {
			scrollTop += parent.scrollTop;
		}
		// 计算垂直居中的top
		if(scrollTop == 0){
			scrollTop = getScrollTop();//获取滚动条高度
		}
		//parent.appendChild(instance.pickerElement);
		var viewWidth = 560, viewHeight = 430;
		if(instance.option.isZoom && isNeedZoom()){//是否需要缩放
			viewWidth = viewWidth * 0.75;
			viewHeight = viewHeight * 0.75 + 10;
		}
		var screenh = window.screen.availHeight-150;//窗口的高(不含菜单)，150：菜单的高，但是无法计算出来，暂时这么写
		var y = scrollTop + screenh / 2 - 215;//215：选人控件高度的一半
		y = parseInt(y);
		var personPickerWin = Ext.create('Ext.window.Window', {
			    id:'person_picker_multiple_view',
			    title:instance.option.titleText || "请选择",
			    closeToolText : '',
				modal: true,
				width:viewWidth,
				height:viewHeight,
				margin:0,
				padding:0,
				border:false,
				resizable:false,
				y:y,
				autoScroll:false,
			    items: [{  
                    header:false, 
                    html : '<div id="windowDiv" style="width:'+viewWidth+'px;height:'+viewHeight+'px;"></div>', 
                    border:false 
                }],
                listeners: {
					beforeclose:function(){
		            	if (typeof(personpicker_timer) == "undefined") {
				        }else {
							clearInterval(personpicker_timer);
				        }
					}
		        }
		});
		personPickerWin.show();
		//var proxy = new Ext.dd.DDProxy(instance.pickerElement.id); 
		// 如果产生滚动条的元素不在当前页面(模板页内)，取body的scrollTop
		//personPickerWin.setY(0, false);
		Ext.getDom("windowDiv").appendChild(instance.pickerElement);
		// instance.pickerElement.style.top = (scrollTop + BODY.clientHeight / 2 - 200) + "px";
		$(".PersonPicker-Multiple-AddBtn")[0].innerHTML = instance.option.text || "添加人员";
		//$("#titleText strong")[0].innerHTML = instance.option.titleText || "请选择";
//		show();
		// 如果是选择业务用户，则“组织机构”改为“业务用户”
		if(instance.option.isSelfUser == false) {
			Ext.getDom('PersonPicker-Main-Organization').children[0].innerHTML = '业务用户';
		}
	}
	function show()  // 显示遮罩层
	{ 
		// 在body中追加一个div作为一个遮罩层
		if(!document.getElementById("hidebg")){
			var div=document.createElement("div");
			div.id="hidebg";
			BODY.appendChild(div);
		}
	   var hideobj=document.getElementById("hidebg");
	   hideobj.style.height = BODY.scrollHeight+"px";  // 设置隐藏层的高度为当前页面高度
	   hideobj.style.width = BODY.scrollWidth+"px";  // 设置隐藏层的高度为当前页面高度
	   hideobj.style.display="block";  // 显示遮罩层
	} 
	
	function hide()  //隐藏遮罩层
	{ 
		if(document.getElementById("hidebg"))
		   document.getElementById("hidebg").style.display="none"; 
	} 
	/** 多选框的界面 */
	function multipleFrame() {
		var div = document.createElement("div");
		div.id = "PersonPicker-Multiple";
		div.className = "PersonPicker-Multiple";
		var innerHTML = '';
		if(!Ext.isEmpty(ONLYNAME))
			innerHTML = multiple_innerHTML.replace('{search-input-text}{uniqueness-value}'+ONLYNAME+'{uniqueness-value}', PLACEHOLDER);
		else
			innerHTML = multiple_innerHTML.replace('{search-input-text}', PLACEHOLDER);
		div.innerHTML = innerHTML;
		if(Ext.isIE){
			div.style.marginLeft = "-5px";
			div.style.marginTop = "-5px";
		}
		if(instance.option.isZoom && isNeedZoom()){//是否需要缩放
			if(Ext.firefoxVersion == 0){//IE、chrome、Safari、edge
				div.style.zoom = "75%";
			}else{
				div.style = "transform: scale(0.75, 0.75);transform-origin:0 0;";//Firefox
			}
		}
		return div;
	}
	
	/** ####################################### 控件-单选 ############################################ */
	function single() {
		instance.pickerElement = singleFrame();
		
		var x=undefined, y=undefined;
		if(!instance.option.isMiddle){//定位到屏幕中间
			//var outline = lowerLeft(instance.pickerElement, instance.target);
			x = Ext.get(instance.target).getXY()[0];
			y = Ext.get(instance.target).getXY()[1]+30;
			//personPickerWin.setXY([x, y], false);
		}
		
		// 浏览器显示效果不同的兼容 
		var marginValue = 0;
		if(Ext.isIE) { //IE
			marginValue = '-5 0 0 -5';
		} else { //firefox、chrome、safari
			marginValue = '-1 0 0 -1';
		}
		
		
		var win = Ext.getCmp('person_picker_single_view');
		if(win) {
			if (typeof(personpicker_timer) == "undefined") {
	        }else {
				clearInterval(personpicker_timer);
	        }
			win.close();
		}
		
		var winheight = 340;
		if(instance.option.header === true){//启用标题时，高度增加
			winheight += 40;
		}
		var personPickerWin = Ext.create('Ext.window.Window', {
			    id:'person_picker_single_view',
			    closeToolText : '',
				width:280,
				height:winheight,
//				modal:true,
				margin:0,
				padding:0,
				border:false,
				title:'请选择',
				header:instance.option.header,
				autoScroll:false,
				resizable:false,
				//style:'border-width:0px;margin:"-5px 0 0 -5px";padding:0px;',
				x:x,
				y:y,
			    items: [{
                    header:false, 
                    border:false,
                    margin:marginValue,
                    padding:0,
                    html : '<div id="windowDiv" style="width:280px;height:340px;"></div>' 
                	}],
            	listeners:{
					render:function(){
						this.mon(Ext.getDoc(), {
			                mousedown: this.hiddenIf,
			                scope: this
			            });
					},
					beforeclose:function(){
		            	if (typeof(personpicker_timer) == "undefined") {
				        }else {
							clearInterval(personpicker_timer);
				        }
					}
				},
				hiddenIf: function(e) {
			        var me = this;
			        if (!me.isDestroyed && !e.within(me.bodyEl, false, true) && !me.owns(e.target)) {
			        	if (typeof(personpicker_timer) == "undefined") {
				        }else {
							clearInterval(personpicker_timer);
				        }
			        	me.close();
			        }
			    }

		}).show();
		Ext.getDom("windowDiv").appendChild(instance.pickerElement);
		//var parent = scrollableParent(instance.target);
//		parent.appendChild(instance.pickerElement);
		// 如果是选择业务用户，则“组织机构”改为“业务用户”
		if(instance.option.isSelfUser == false) {
			Ext.getDom('PersonPicker-Main-Organization').children[0].innerHTML = '业务用户';
		}
	}
	
	/** 单选框的界面 */
	function singleFrame() {
		var div = document.createElement("div");
		div.id = "PersonPicker-Single";
		div.className = "PersonPicker-Main PersonPicker-Single";
		var innerHTML = "";
		if(!Ext.isEmpty(ONLYNAME))
			innerHTML = single_innerHTML.replace('{search-input-text}{uniqueness-value}'+ONLYNAME+'{uniqueness-value}', PLACEHOLDER);
		else
			innerHTML = single_innerHTML.replace('{search-input-text}', PLACEHOLDER);
		div.innerHTML = innerHTML;
		return div;
	}
	
	/** ####################################### 控件-主面板 ############################################ */
	/** 切换组织机构和候选人面板 */
	function showOrg() {
		$("#PersonPicker-Main-Candidate")[0].style.display = "none";
		$("#PersonPicker-Main-Organization")[0].style.display = "block";
	}
	function showCandidate() {
		$("#PersonPicker-Main-Organization")[0].style.display = "none";
		$("#PersonPicker-Main-Candidate")[0].style.display = "block";
	}
	
	/** ####################################### 控件-主面板-候选人 ############################################ */
	/** 关键字查询候选人
	 * @param isDefault 是否是默认显示的人员，用于区分在定时任务中没有关键字的情况下是默认候选人还是清空了关键字
	 */
	function query(hashvo, isDefault) {
		var curTask = instance.keywordtaskQueue[0];//从队列中取出当前执行的任务
		if(curTask)
			curTask.rstatus = "1";
		hashvo.put("isSelfUser", instance.option.isSelfUser);
		hashvo.put("isPrivExpression", instance.option.isPrivExpression);
		hashvo.put("extend_str", instance.option.extend_str || "");
		hashvo.put("addpost", instance.option.addpost);// 是否添加岗位 chent 20170216
		hashvo.put("validateSsLOGIN", instance.option.validateSsLOGIN);//是否启用认证库校验 chent 20170313
		hashvo.put("selfUserIsExceptMe", instance.option.selfUserIsExceptMe);//业务用户时是否排除自己。chent 20170329
		hashvo.put("selectByNbase", instance.option.selectByNbase);//是否按不同人员库显示 chent 20170419
		Rpc({
			asynchronous : true,
			success : function(data) {
				var result = Ext.decode(data.responseText);
				instance.data.candidate = result.candidates || [];
				if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
					fillUpUnit(instance.data.candidate);
					var a = $(".PersonPicker-Main-Candidate-Photo img");
					for (var i=0; i < a.length; i++) {
						a[i].style.width = '22px';
						a[i].style.height = '22px';
						//a[i].style.border-radius='0';
					}
				}else if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
					fillUp(instance.data.candidate);
				}
				
				if (isDefault) {instance.data.candidate.isDefault = true;}
				instance.keywordtaskQueue.splice(0,1);//删除运行玩的记录；
			},
			functionId : '9028000767'
		}, hashvo);
	}
	
	/** 填充选人框的候选栏 */
	function fillUp(candidate) {
		var ul = $("#PersonPicker-Main-Candidate ul")[0];
		ul.innerHTML = "";
		for (var i = 0; i < candidate.length; i++) {
			var li = document.createElement("li");
			li.data = candidate[i];
			
			var span = document.createElement("span");
			span.className = "PersonPicker-Main-Candidate-Photo";
			var img = document.createElement("img");
			var div = document.createElement("div");
			div.className = "PersonPicker-Main-Candidate-Info";
			var div1 = document.createElement("div");
			div1.style.color = "#444444";
			var div2 = document.createElement("div");
			div1.style.fontSize = "11px";
			var div3 = document.createElement("div");
			var title = candidate[i].dept;
			if(instance.option.selectByNbase){//区分人员库时,title显示人员库
				title = candidate[i].dbname;
			}
			div1.title = candidate[i].name + "（" + title + "）";
			var divText = "";
			if(!Ext.isEmpty(candidate[i].dept)){
				divText = candidate[i].shortDept +(candidate[i].shortDept !== candidate[i].dept ? "..." : "")
			}else if(!Ext.isEmpty(candidate[i].unit)){
				divText = candidate[i].shortUnit +(candidate[i].shortUnit !== candidate[i].unit ? "..." : "")
			}
			
			//区分人员库时,人员后面显示  人员库/部门  haosl  2017-8-19
			if(!Ext.isEmpty(candidate[i].dbname)){
				divText = candidate[i].dbname+(Ext.isEmpty(divText)?"":"/"+divText);
			}
			if(divText != ''){
				divText = "（" + divText + "）"
			}
			div1.innerHTML = candidate[i].shortName + divText;
			div2.innerHTML = candidate[i].email
			div3.innerHTML = candidate[i].onlyName;
			img.src = candidate[i].photo;
			


			
			div.appendChild(div1);
			if(!Ext.isEmpty(candidate[i].onlyName)
                && candidate[i].name != candidate[i].onlyName
                && candidate[i].email != candidate[i].onlyName){
				div.appendChild(div3);
			}
			//email为空时不加email的div，并且把上一排div垂直居中 chent 20180317 add
			if(!Ext.isEmpty(candidate[i].email)){
				div.appendChild(div2);
			}
			if(Ext.isEmpty(candidate[i].email) && Ext.isEmpty(candidate[i].onlyName)){
				div1.style.position = "relative";
				div1.style.top = "12px";
			}
			
			span.appendChild(img);
			li.appendChild(span);
			li.appendChild(div);
			
			ul.appendChild(li);
		}
	}

	/** 填充选择框的候选栏 */
	function fillUpUnit(candidate) {
		var ul = $("#PersonPicker-Main-Candidate ul")[0];
		ul.innerHTML = "";
		
		for (var i = 0; i < candidate.length; i++) {
			var li = document.createElement("li");
			li.data = candidate[i];
			
			var span = document.createElement("span");
			span.className = "PersonPicker-Main-Candidate-Photo";
			span.style.marginTop="10px";
			var img = document.createElement("img");
			if(candidate[i].rawType === "UN"){
				img.src = "/components/personPicker/image/unit.png";
			}else if(candidate[i].rawType === "UM"){
				img.src = "/components/personPicker/image/dept.png";
			} else if (candidate[i].rawType === "@K") {
				img.src = "/images/pos_l.gif";
			}
			
			var div = document.createElement("div");
			div.className = "PersonPicker-Main-Candidate-Info";
			var div1 = document.createElement("div");
			div1.title = candidate[i].name;
			div1.innerHTML = candidate[i].name;
			div1.style.paddingTop="12px";
			var div2 = document.createElement("div");
			div2.innerHTML = "";
			
			div.appendChild(div1);
			div.appendChild(div2);
			span.appendChild(img);
			li.appendChild(span);
			li.appendChild(div);
			
			ul.appendChild(li);
		}
	}
	
	/** 显示默认候选人 */
	function recommend(arr) {
//		var hashvo = new ParameterSet();
//		hashvo.setValue("recommend", arr || []);
		var hashvo = new HashMap();
		hashvo.put("recommend", arr || []);
		query(hashvo, true);
	}
	
	/** 选择某一个候选人 */
	function pickUp(data) {

		if (instance.option.multiple === false) { // 单选时，传递选中候选人给callback，然后关闭选人框

			if (instance.option.callback) {
				data.id = data.id.replace(/＠/g, "@");
				var flag = true;
				if(instance.option.beforeFinishVerifyFunc!=EMPTY_FUNCTION){
                    flag = instance.option.beforeFinishVerifyFunc.call(null,data);
				}
				if(flag){
                    instance.option.callback.call(null, data);
                }
			}
//			instance.close();
			var win = Ext.getCmp('person_picker_single_view');
			if(win){
				if (typeof(personpicker_timer) == "undefined") {
		        }else {
					clearInterval(personpicker_timer);
		        }
		        //haosl add 20170417 OKR工作计划页面选人时（双击选择的时候，容易误选中别的任务）start
		        var task = new Ext.util.DelayedTask();
		        task.delay(200, function(){
					win.close()
				}, this);
				//haosl add 20170417 end 
//				personPickerIsOpen = false;
				return;
			}
		} else { // 多选时，选中表格中追加一行数据
			if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
				checkInUnit(data);
			}else if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
				checkIn(data);
			}
			
		}
	}
	
	/** ####################################### 控件-主面板-关键字 ############################################ */
	/** 根据文本框的内容决定占位符出现与否 */
	function clearTip(input) {
		if (input.value === PLACEHOLDER) {input.value = "";}
	}
	function restoreTip (input) {
		if (!input.value) {input.value = PLACEHOLDER;}
	}
	
	/** ············································· 定时任务 ···················································· */
	//var timer; // 定时任务
	var prevKeyword = ""; // 上一次查询的关键字
	
	/** 关键字文本框获得焦点时
	 * · 清空tip
	 * · 设定定时任务：每隔若干毫秒查询后台
	 */
	function startTimer() {
		var input = $(".PersonPicker-Main-Keyword input")[0];
		personpicker_timer = setInterval(function () { // 定时任务，定时查询后台
			var keyword = trim(input.value);
			if (keyword == prevKeyword) {
				if(instance.keywordtaskQueue.length==0){
					return;
				}
			}else{
				if (keyword != "" && keyword != PLACEHOLDER){
					var task = new Object();
					task.rstatus = "0"//运行状态  "0" 等待（未执行） "1"执行中
					task.keyword = keyword;
					task.totalTimes = 0;//如果该任务超时（2000ms），则废弃该任务
					instance.keywordtaskQueue.push(task);//将任务加入任务队列
				}else {
					prevKeyword = keyword; // 替换上一次查询的关键字
					if (instance.option.recommend && instance.option.recommend.length > 0) {
						if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
							recommend([]);
						}else if(instance.option.addunit === false && instance.option.adddepartment === false  && instance.option.addpost === false){
							recommend(instance.option.recommend);
						}
						showCandidate();
						return;
					}
					if (!instance.data.candidate.isDefault) {
						fillUp([]);
						instance.data.candidate = [];
						showOrg();
						return;
					} 
				}
			}
			prevKeyword = keyword; // 替换上一次查询的关键字
			var curTask = instance.keywordtaskQueue[0];//从队列中取出当前执行的任务
			if(curTask && curTask.rstatus == "1"){//等待任务结束，在执行队列中的下一个任务 haosl 
				curTask.totalTimes += 500;
				if(curTask.totalTimes<=2000){//如果任务耗时没有超过2秒，则继续等待任务执行完成，
					return;
				}else{
					//超时则直接执行下一条任务。
					instance.keywordtaskQueue.splice(0,1);
					if(instance.keywordtaskQueue.length==0)
						return;
					curTask = instance.keywordtaskQueue[0];
				}
			}
			// ----------------------
			if(curTask){
				if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
					var hashvo = new HashMap();
					hashvo.put("keyword", curTask.keyword);
					var addunit = instance.option.addunit?"1":"0 ";
					var adddepartment = instance.option.adddepartment?"1":"0";
					hashvo.put("addunit", addunit);
					hashvo.put("adddepartment", adddepartment);
					hashvo.put("orgid", instance.option.orgid);
					var de = instance.option.deprecate || []; // 排除的单位部门和岗位
					if (de && de.length > 0) {
						hashvo.put("deprecate", de);
					}
					query(hashvo);
				}else if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
					var hashvo = new HashMap();
					if(curTask)
						hashvo.put("keyword", curTask.keyword);
					hashvo.put("orgid", instance.option.orgid);
					hashvo.put("nbases", instance.option.nbases);
					var de = instance.option.deprecate || []; // 排除的人员
					if (de && de.length > 0) {
						hashvo.put("deprecate", de);
					}
					query(hashvo);
				}
			}
			showCandidate();
		}, 500);
	}
	
	/** 关键字文本框失去焦点时
	 * · 看情况恢复tip
	 * · 取消定时任务定时任务
	 */
	function endTimer() {
		prevKeyword = "";
		if (typeof(personpicker_timer) == "undefined") {
        }else {
			clearInterval(personpicker_timer);
        }
	}
	
	/** ············································· 定时任务结束 ···················································· */
	
	/** ####################################### 控件-主面板-组织机构 ############################################ */
	/** 在单位下追加下属单位和员工 */
	function appendSubordinate(unitNode, unit, person) {
		
		//【39471 】评估实施中指定考核主体，没有职位的人应该在组织机构的最下面，防止没有职位的人太多，选择机构的时候不方便
		every(unit.concat(person), function () {
		//every(person.concat(unit), function () {
			var node;
			if (this.type === "person") {
				if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
					node = createPersonNode(this);
				}
			} else if (this.type === "unit") {
				node = createUnitNode(this);
			}
			if(node != "" && node != null){
				$("div", unitNode)[0].appendChild(node);
			}
		});
	}
	
	/** 单位节点 */
	function createUnitNode(unit) {
		var div = document.createElement("div");
		div.setAttribute("attachTo", unit.attachTo || "");
		div.data = unit;
		
		var p = document.createElement("p");
		if (instance.option.multiple === true) {
			p.onmousemove = globalMouseOverOrMoveHandler;
			p.onmouseover = globalMouseOverOrMoveHandler;
			p.onmouseout = globalMouseOutHandler;
		}
		p.setAttribute("tabIndex", "-1");
		p.setAttribute("hideFocus", "true");
		p.setAttribute("type", "unit");
		
		var level = parseInt(unit.level) || 1;
		level = level <= 0 ? 1 : level;
		var pl = 10 + 15 * (level - 1);
		p.style.paddingLeft = pl + "px";
		p.style.whiteSpace="nowrap";
		var img = document.createElement("img");
		if (unit.rawType === "UN") {
			img.src = "/components/personPicker/image/unit.png";
		} else if (unit.rawType === "UM") {
			img.src = "/components/personPicker/image/dept.png";
		} else if (unit.rawType === "@K") {
			img.src = "/images/pos_l.gif";
		}
		var label = document.createElement("label");
		label.innerHTML = unit.name || "";

		p.appendChild(img);
		p.appendChild(label);
		p.title = unit.name || "";

		if(instance.option.recruitmentSpecial){// 显示已聘用/未聘用人数，招聘专有
			var label1 = document.createElement("label");
			var has_exam = unit.has_exam;
			var no_exam = unit.no_exam;
			if(no_exam == 0){
				return ;
			}
			label1.innerHTML = '('+no_exam+'/'+has_exam+')';
			p.appendChild(label1);
		}

		if (instance.option.multiple === true) {
			var span = document.createElement("span");
			span.className = "AnchorBtn PersonPicker-Main-Organization-AddAll";
			if(instance.option.addunit === true && unit.rawType === "UN"){
				span.innerHTML = "添加";
			}else if(instance.option.adddepartment === true && unit.rawType === "UM"){
				span.innerHTML = "添加";
			}else if(instance.option.addpost === true && unit.rawType === "@K"){
				span.innerHTML = "添加";
			}else if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false && !instance.option.multipleAndSingle){
				//当配置了单人选择以多人界面展现的时候，不展示添加
				//此处增加判断，如果是人员库节点后面不显示“添加所有”的链接  haosl 2017-11-22
				if(!(instance.option.selectByNbase && unit.level=="0")){
					span.innerHTML = "添加所有";
				}
			}
			p.appendChild(span);
		}

		div.appendChild(p);
		div.appendChild(document.createElement("div"));
		
		return div;
	}
	
	/** 人员节点 */
	function createPersonNode(person) {
		
		var p = document.createElement("p");
		p.data = person;
		if (instance.option.multiple === true) {
			p.onmousemove = globalMouseOverOrMoveHandler;
			p.onmouseover = globalMouseOverOrMoveHandler;
			p.onmouseout = globalMouseOutHandler;
		}
		p.id = "PersonPicker-Person-" + (person.id || "");
		p.setAttribute("tabIndex", "-1");
		p.setAttribute("hideFocus", "true");
		p.setAttribute("type", "person");
		
		var level = parseInt(person.level) || 1;
		level = level <= 0 ? 1 : level;
		var pl = 10 + 15 * (level - 1);
		p.style.paddingLeft = pl + "px";
		
		var img = document.createElement("img");
		img.src = "/components/personPicker/image/" + (person.gender || "male") + ".png";
		var label = document.createElement("label");
		p.style.whiteSpace="nowrap";
		var desc = "";
		if(instance.option.isSelfUser){
			desc = (person.shortName || "")
				+ "(" + person.shortPost + (person.shortPost !== person.post ? "..." : "") // 名称过长将阶段的部分用省略号代替
				+ ")";
		}else{
			desc = (person.shortName || "");
		}
		if(desc.indexOf("()") > -1){//如果岗位为空的话，名字后面的括号不显示 chent 20160215
			var len = desc.length;
			desc = desc.substring(0, len-2);
		}
		label.innerHTML = desc;
		
		p.appendChild(img);
		p.appendChild(label);
		if(instance.option.isSelfUser){
			p.title = (person.name || "") + (person.post==""? "":"（" + person.post + "）");
		}else{
			p.title = (person.name || "");
		}
		if (instance.option.multiple === true) {
			var span = document.createElement("span");
			if(isAvailable(person)){
				span.className = "AnchorBtn";
				span.innerHTML = "添加";	
			}else{
				span.className = "AnchorBtn PersonPicker-Main-Organization-Checked";
				span.innerHTML = "已添加";
			}
			p.appendChild(span);
		}
		return p;
	}
	
	/** 查询下级单位或部门，以及人员。unitNode为空表示最高级单位 */
	function subordinate(unitNode, fn) {
		var hashvo = new HashMap();

		unitNode = unitNode || $("#PersonPicker-Main-Organization")[0];
		
		var d = unitNode.data || {};
		var id = d.id || "";
		if(!Ext.isEmpty(id)){
			id = id.replace(/＠/g, "@");
		}
		hashvo.put("attachTo", id);
		hashvo.put("ancester", d.ancester || "");
		hashvo.put("level", d.level || "");
		hashvo.put("nbases", instance.option.nbases || "");
		hashvo.put("orgid", instance.option.orgid || "");
		hashvo.put("extend_str", instance.option.extend_str || "");
		hashvo.put("isSelfUser", instance.option.isSelfUser);
		hashvo.put("isPrivExpression", instance.option.isPrivExpression);
		hashvo.put("deprecate", instance.option.deprecate);
		hashvo.put("recruitmentSpecial", instance.option.recruitmentSpecial);
		hashvo.put("addunit", instance.option.addunit);
		hashvo.put("adddepartment", instance.option.adddepartment);
		hashvo.put("addpost", instance.option.addpost);
		hashvo.put("validateSsLOGIN", instance.option.validateSsLOGIN);//是否启用认证库校验 chent 20170313
		hashvo.put("selfUserIsExceptMe", instance.option.selfUserIsExceptMe);//业务用户时是否排除自己。chent 20170329
		hashvo.put("selectByNbase", instance.option.selectByNbase);//是否按不同人员库显示 chent 20170419
		if(instance.option.selectByNbase){//区分人员库时，传当前操作的人员库
			hashvo.put("contentNbs", instance.contentNbs);
		}
		hashvo.put("defaultSelected", instance.option.defaultSelected);//默认已选
		
		Rpc({
			asynchronous : true,
			success : function(data) {
				var result = Ext.decode(data.responseText);
				unitNode.hasQueried = true;
				
				var unit = result.unit || [];
				var person = result.person || [];
				appendDistinct(instance.data.unit, unit);
				if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
					appendDistinct(instance.data.person, person);
				}
				appendSubordinate(unitNode, unit, person);
				//初始化已选 chent 20170509 start 
				if(instance.option.multiple && instance.option.defaultSelected.length > 0){//多选时才生效
					var defaultSelectedList = result.defaultSelectedList || [];
					if(defaultSelectedList.length == 0){//只有初始化时添加，第二次及以后则不处理。
						return ;
					}
					for(var i=0; i<defaultSelectedList.length; i++){
						var obj = defaultSelectedList[i];
						if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
							checkInUnit(obj);
						}else if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
							checkIn(obj);
						}
					}
				}
				//初始化已选 chent 20170509 end
				focusOnFirst(unitNode);
				adjustOverflow(); // IE需要手动适应一下
				if (fn) {
					if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
						if(person.length > 500){
								Ext.Msg.alert('提示信息', '超过500人，单次添加的上限为500人！');
								return ;
						}
						fn(person);
					}
				}
			},
			functionId : '9028000768'
		}, hashvo);
	}

	/** 选中某一个单位节点，包含以下步骤：
	 * 1、折叠同级其它已展开的单位
	 * 2、清除上级的选中样式
	 * 3、展开单位节点的下属机构
	 * 4、查询下级
	 */
	function selectUnit(unitNode, fn) {
		if(instance.option.selectByNbase && unitNode.data.level == 0){//区分人员库时，记录当前展开的是哪个人员库
			instance.contentNbs = unitNode.data.id;
		}

		if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
			if (fn) {
				checkInUnit(unitNode);
			}
		}
		var person = some(instance.data.person, function () {
			return this.attachTo === unitNode.data.id || this.realattach === unitNode.data.id;
		});
		if (unitNode.getAttribute("display") === "unfold") {
			if (fn) {
				if(person.length > 500){
					Ext.Msg.alert('提示信息', '超过500人，单次添加的上限为500人！');
					return ;
				}
				fn(person);
			} else {
				fold(unitNode);
			}
		} else {
			foldSibling(unitNode);
			every($(".bgSelected"), function () {
				removeClass(this, "bgSelected");
			});
			//点击添加 或者添加全部时不展开下级单位或人员
			if(!fn){
				unfold(unitNode);
			}

			// 如果查询过，从缓存中取数据
			if (!unitNode.hasQueried) {
				//点击添加 或者添加全部时不展开下级单位或人员
				if(!fn){
					subordinate(unitNode, fn);
				}
			} else if (fn) {
				if(person.length > 500){
					Ext.Msg.alert('提示信息', '超过200人，单次添加的上限为500人！');
					return ;
				}
				fn(person);
			}
		}
	}

	/**
	 * 当节点的内容超过了div的宽度，水平方向出现滚动条后节点的宽度 需要重新设置一下
	 *
	 * haosl 2017-9-1
	 */
	function resetNodeWidth(unitNode){
		//重置宽度
        if(Ext.getDom("PersonPicker-Main-Organization")){
            Ext.getDom("PersonPicker-Main-Organization").scrollLeft = 0;
        }
        if(Ext.getDom("PersonPicker-Main-Organization_NodeDiv1")){
            Ext.getDom("PersonPicker-Main-Organization_NodeDiv1").style.width="100%";
        }
		if(Ext.getDom("PersonPicker-Main-Organization_NodeDiv2")){
            Ext.getDom("PersonPicker-Main-Organization_NodeDiv2").style.width="100%";
        }
		//设置宽度
		setTimeout(function(){
			var scrollWidth = Ext.getDom("PersonPicker-Main-Organization").scrollWidth;
			if(scrollWidth-20<=260){
				return;
			}
            if(Ext.getDom("PersonPicker-Main-Organization_NodeDiv1")) {
                Ext.getDom("PersonPicker-Main-Organization_NodeDiv1").style.width = scrollWidth + "px";
            }
            if(Ext.getDom("PersonPicker-Main-Organization_NodeDiv2")){
                Ext.getDom("PersonPicker-Main-Organization_NodeDiv2").style.width=scrollWidth+"px";
            }
			if(!unitNode)
				return;
			var personDiv = $("div", unitNode)[0];
			if(personDiv){
				every($("p[type='person']",personDiv),function(){
					this.style.width = scrollWidth+"px";
				});
			}
		},200);
	}


	/** 展开依附于单位下的所有元素，包含以下操作:
	 * 1。设定单位节点display属性为unfold
	 * 2、为单位下的p节点添加选中样式
	 * 3、设定单位下的div节点display样式为block
	 * 4、单位下第一个子单位或人员出现在视线内
	 */
	function unfold(unitNode) {
		unitNode.setAttribute("display", "unfold");
		$("p", unitNode)[0].className += " bgSelected";
		$("div", unitNode)[0].style.display = "block";
		focusOnFirst(unitNode);
	}

	/** 对依附于该单位节点下的所有子单位，执行操作:
	 * 1。设定单位节点display属性为fold
	 * 2、隐藏单位节点的子单位div
	 * 3、移除单位节点的选中样式
	 */
	function fold(unitNode) {
		every($("[display=unfold]", unitNode).concat(unitNode), function () {
			this.setAttribute("display", "fold");
			$("div", this)[0].style.display = "none";
			removeClass($("p", this)[0], "bgSelected");
		});
		adjustOverflow(); // IE需要手动适应一下
	}

	/** 关闭同级其它已展开的单位 */
	function foldSibling(unitNode) {
		var attachTo = unitNode.data.attachTo || "";
		every($(".PersonPicker-Main-Organization [display=unfold][attachTo='" + attachTo + "']"), function () {
			if (!isSameNode(this, unitNode)) {
				fold(this);
			}
		});
	}

	/** 单位下第一个子单位或员工p元素获得焦点,使其出现在在视野范围内 */
	function focusOnFirst(unitNode) {
		var first = $("div p", unitNode)[0];
		if (first) {
			first.focus();
		}
		adjustOverflow(); // IE需要手动适应一下
	}

	/** ie下滚动条显示异常 */
	function adjustOverflow() {
		if (FF) {return;}

		setTimeout(function(){
			every($("#PersonPicker-Main-Organization, .PersonPicker-Multiple-CheckedPerson"), function () {
				if (this.scrollHeight > this.clientHeight) {
					this.style.overflowY = "scroll";
				} else {
					this.style.overflowY = "hidden";
				}
			});
		},400);

	}

	/** ####################################### 控件-已选人员 ############################################ */

	/** 添加多个人员 */
	function checkAll(arr) {
		every(arr, function (item) {
			if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false ){
				//分库选人时，判断选中人员是否为当前应用库，不是则不添加。为了解决 bug 29072   2017-08-07 haosl
				if(instance.option.selectByNbase){
					if(instance.contentNbs === item.nbase){
						checkIn(item);
					}
				}else {
					checkIn(item);
				}
			}
		});
	}

	/** 判断当前人员能否被添加进以选名单
	 * · 已选人员不能重复添加
	 * · deprecate内的人员不能添加
	 * @param person
	 * @returns
	 */
	function isAvailable(person) {
		// 将查询出的结果转换成boolean类型
		var result = !!one(instance.data.checked.concat(instance.option.deprecate), function (p) {
			if (typeof p === "string") {
				return p === person.id;
			} else {
				return p.id === person.id;
			}
		});
		return !result;
	}

	/** 判断当前机构能否被添加进以选名单
	 * · 已选人员不能重复添加
	 * · deprecate内的人员不能添加
	 * @param person
	 * @returns
	 */
	function isAvailableUnit(unit) {
		// 将查询出的结果转换成boolean类型
		var result = !!one(instance.data.checked.concat(instance.option.deprecate), function (p) {
			if (typeof p === "string") {
				return p === unit.data.id;
			} else {
				return p.id === unit.data.id;
			}
		});
		return !result;
	}

	/** 选中单个人员（从候选人或者组织机构） */
	function checkIn(person) {
		if (isAvailable(person)) {
			var arr = instance.data.checked;
			arr[arr.length] = person;
			var pnode = $(".PersonPicker-Multiple-CheckedPerson table>tbody")[0];
			if(instance.option.multipleAndSingle){//单选
				var arr  = $(".PersonPicker-Multiple-CheckedPerson-Remove");
				if(arr.length > 0){
					var dom_a = arr[0];
					dom_a.click();
				}
			}
			pnode.appendChild(checkedPersonNode(person));
			_afterCheckIn(person);
		}
		adjustOverflow(); // IE需要手动适应一下
	}
	/** 选中机构 */
	function checkInUnit(unit) {
		if (isAvailableUnit(unit)) {
			var arr = instance.data.checked;
			arr[arr.length] = unit.data;
			var pnode = $(".PersonPicker-Multiple-CheckedPerson table>tbody")[0];
			if(instance.option.multipleAndSingle){//单选
				var arr  = $(".PersonPicker-Multiple-CheckedPerson-Remove");
				if(arr.length > 0){
					var dom_a = arr[0];
					dom_a.click();
				}
			}
			pnode.appendChild(checkedUnitNode(unit));

			var a = $(".PersonPicker-Multiple-CheckedPerson img");
			for (var i=0; i < a.length; i++) {
				a[i].style.width = '20px';
				a[i].style.height = '20px';
				//a[i].style.border-radius='0';
			}

			//a.style.border-radius = '0%';
			_afterCheckInUnit(unit);
		}
		adjustOverflow(); // IE需要手动适应一下
	}
	
	/** 对应人员节点(组织机构下)添加选中的样式 */
	function _afterCheckIn(person) {
		//因修改了加密方式，person.id中包含:: 是class 选择器的关键字，所以给用getElementById 的方式修改
		var pNode = document.getElementById("PersonPicker-Person-"+person.id)
		if(pNode){
			addClass(pNode, "PersonPicker-Main-Organization-Checked");
			var anchorBtn = $("span", pNode)[0];
			addClass(anchorBtn, "PersonPicker-Main-Organization-Checked");
//			if(anchorBtn.id=="")
//			{
				anchorBtn.innerHTML = "已添加";
//			}
		}
	}
	
	/** 对应机构/部门节点(组织机构下)添加选中的样式 */
	function _afterCheckInUnit(unit) {
		//var pNode = $(".PersonPicker-Multiple p[type=unit]")[0];
		//addClass(pNode, "PersonPicker-Main-Organization-Checked");
		var pNode = '';
		try{
			pNode = $("p", unit)[0];
			
		}catch(e){}
		if(pNode == ''){
			return ;
		}
		addClass(pNode, "PersonPicker-Main-Organization-Checked");
		var anchorBtn = $("span", unit)[0];
		addClass(anchorBtn, "PersonPicker-Main-Organization-Checked");
		if(unit.data.isquery!="1" && anchorBtn.id=="")
		{
			anchorBtn.innerHTML = "已添加";
		}
	}
	
	/** 从选中人员名单中删掉指定员工，并删除对应节点 */
	function checkOut(tr) {
		var person = tr.data;
		var arr = instance.data.checked;
		if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
			var i = arrayIndexOfUnit(arr, person);
		}else if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
			var i = arrayIndexOf(arr, person);
		}
		if (i > -1) {arr.splice(i, 1);}
		
		tr.parentNode.removeChild(tr);
		/*
		var id = person.attachTo;
		if(instance.data.unit.hasAdded[id] && instance.data.unit.hasAdded[id].isAdded)
			instance.data.unit.hasAdded[id].isAdded = false;//执行删除操作时将已添加状态重置为未添加。 haosl
		*/
		adjustOverflow(); // IE需要手动适应一下
		if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
			_afterCheckOutUnit(person);
		}else if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
			_afterCheckOut(person);
		}
		
	}

	/** 取消对应人员节点(组织机构下)的选中的样式 */
	function _afterCheckOut(person) {
		var pNode = document.getElementById("PersonPicker-Person-" + person.id )
		removeClass(pNode, "PersonPicker-Main-Organization-Checked");
		var anchorBtn = $("span", pNode)[0];
		removeClass(anchorBtn, "PersonPicker-Main-Organization-Checked");
		if(anchorBtn.id=="")
		{
			anchorBtn.innerHTML = "添加";
		}
	}
	
	/** 取消对应单位/部门节点(组织机构下)的选中的样式 */
	function _afterCheckOutUnit(unit) {
		
		var pNode = '';
		try{
			pNode = $("p", unit)[0];
			
		}catch(e){}
		if(pNode == ''){
			return ;
		}
		removeClass(pNode, "PersonPicker-Main-Organization-Checked");
		var anchorBtn = $("span", unit)[0];
		removeClass(anchorBtn, "PersonPicker-Main-Organization-Checked");
		if(unit.data.isquery!="1" && anchorBtn.id=="")
		{
			anchorBtn.innerHTML = "添加";
		}
	}
	
	/** 创建已选人员tr，追加到.PersonPicker-Multiple-CheckedPerson下 */
	function checkedPersonNode(person) {
		var tr = document.createElement("tr");
		tr.data = person;
		tr.onmousemove = globalMouseOverOrMoveHandler;
		tr.onmouseover = globalMouseOverOrMoveHandler;
		tr.onmouseout = globalMouseOutHandler;
		
		var td1 = document.createElement("td");
		td1.className = "taCenter";
		td1.innerHTML = '<img src="' + person.photo + '" />';
		
		var td2 = document.createElement("td");
		if(instance.option.isSelfUser){
			td2.title = person.name + "（"+person.dept+"）";
			td2.innerHTML = person.shortName + (person.shortDept ? "（"+person.shortDept+"）" : "");
		}else{
			td2.title = person.name;
			td2.innerHTML = person.shortName;
		}
		
		
		var td3 = document.createElement("td");
		td3.innerHTML = '<a class="AnchorBtn PersonPicker-Multiple-CheckedPerson-Remove">删除</a>';
		
		tr.appendChild(td1);
		tr.appendChild(td2);
		tr.appendChild(td3);
		
		return tr;
	}
	/** 创建已选单位或部门tr，追加到.PersonPicker-Multiple-CheckedPerson下 */
	function checkedUnitNode(unit) {
		var tr = document.createElement("tr");
		tr.data = unit;
		tr.onmousemove = globalMouseOverOrMoveHandler;
		tr.onmouseover = globalMouseOverOrMoveHandler;
		tr.onmouseout = globalMouseOutHandler;
		
		var td1 = document.createElement("td");
		td1.className = "taCenter";
		if(unit.data.rawType == 'UM'){
			td1.innerHTML = '<img src="/components/personPicker/image/dept.png" />';
		} else if(unit.data.rawType == 'UN'){
			td1.innerHTML = '<img src="/components/personPicker/image/unit.png" />';
		} else if (unit.data.rawType == '@K') {
			td1.innerHTML = '<img src="/images/pos_l.gif" />';
		}
		var td2 = document.createElement("td");
		td2.title = unit.data.name;
		td2.innerHTML = unit.data.shortName+(unit.data.shortName !== unit.data.name ? "..." : "");
		
		var td3 = document.createElement("td");
		td3.innerHTML = '<a class="AnchorBtn PersonPicker-Multiple-CheckedPerson-Remove">删除</a>';
		
		tr.appendChild(td1);
		tr.appendChild(td2);
		tr.appendChild(td3);
		
		return tr;
	}
	
	/** 清空已选人员 */
	function clearChecked() {
		instance.data.checked = [];
		var tr = $(".PersonPicker-Multiple-CheckedPerson tbody tr");
		every(tr, function () {
			checkOut(this);
//			this.parentNode.removeChild(this);
		});
		adjustOverflow(); // IE需要手动适应一下
	}
	
	/** ####################################### 监听 ############################################ */
	/** 给特定元素添加事件，比如输入框的focus和blur，人员或单位的点击事件 */
	function listen() {
		var frame;
		if (instance.option.multiple === true) { // 多选
			frame = $("#PersonPicker-Multiple")[0];
		} else if (instance.option.multiple === false) { // 单选
			frame = $("#PersonPicker-Single")[0];
		}
		
		frame.onclick = globalClickHandler;
		
		// 关键字输入框
		var input = $(".PersonPicker-Main-Keyword input")[0];
		input.onfocus = bind(clearTip, this, input);
		input.onblur = bind(restoreTip, this, input);
	}

	/** 点击事件，组织机构上点击人员或部门(全选)、关键字选人、已选人员的删除 */
	function globalClickHandler(e) {
		e = e || window.event;
		var target = e.srcElement || e.target;
		
		if (isSameNode(target, $(".PersonPicker-Main-Keyword img")[0])) { // 清空关键字按钮
			$(".PersonPicker-Main-Keyword input")[0].value = PLACEHOLDER;
			showOrg();
		} else if (isNode(target, ".PersonPicker-Main-Candidate li")) { // 点在候选人栏里的li上
			if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
				pickUp(target);
			}else if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
				pickUp(target.data);
			}
		} else if (hasParent(target, ".PersonPicker-Main-Candidate li")) { // 点在候选人栏里的li元素内部
			var li = getParent(target, ".PersonPicker-Main-Candidate li");
			if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
				pickUp(li);
			}else if(instance.option.addunit === false && instance.option.adddepartment === false && instance.option.addpost === false){
				pickUp(li.data);
			}
		} else if (isNode(target, ".PersonPicker-Multiple-CheckedPerson-Remove")) { // 已选人员的删除按钮
			checkOut(getParent(target, ".PersonPicker-Multiple-CheckedPerson tr"));
		} else if (isNode(target, "#clearChecked")) { // 清空已选人员
			clearChecked();
		} else if (isNode(target, "#PersonPicker-Main-Organization [type=unit]")) { // 单位内的p元素上
			selectUnit(target.parentNode);
			resetNodeWidth(target.parentNode);
		} else if (isNode(target, ".PersonPicker-Main-Organization-AddAll")) { // 单位名称后面的“添加全部按钮”
			selectAll(target.parentNode.parentNode, checkAll);
		} else if (hasParent(target, "#PersonPicker-Main-Organization [type=unit]")) { // 单位的p元素内部
			selectUnit(target.parentNode.parentNode);
			resetNodeWidth(target.parentNode.parentNode);
		} else if (isNode(target, "#PersonPicker-Main-Organization [type=person]")) { // 人员的p元素上
			pickUp(target.data);
		} else if (hasParent(target, "#PersonPicker-Main-Organization [type=person]")) { // 人员的p元素内部
			pickUp(target.parentNode.data);
		} else if (isNode(target, ".PersonPicker-Multiple-AddBtn")) { // 添加按钮上
			if (instance.option.callback) {
				for(var idx=0; idx<instance.data.checked.length; idx++){
					instance.data.checked[idx].id = instance.data.checked[idx].id.replace(/＠/g, "@");
				}
                var flag = true;
                if(instance.option.beforeFinishVerifyFunc!=EMPTY_FUNCTION){
                    flag = instance.option.beforeFinishVerifyFunc.call(null,instance.data.checked);
                }
                if(flag){
                    instance.option.callback.call(null, instance.data.checked);
                    Ext.getCmp('person_picker_multiple_view').close();
                }
			}
//			instance.close();
		}
		
		e.cancelBubble = true;
		if (e.stopPropagation) {
			e.stopPropagation();
		}
	}
	
	/** 单位、人员和已选人员的mouseover和mousemove事件 */
	function globalMouseOverOrMoveHandler() {
		if($(".AnchorBtn", this)[0])
			$(".AnchorBtn", this)[0].style.visibility = "visible";
	}
	
	/** 单位、人员和已选人员的mouseout事件 */
	function globalMouseOutHandler() {
		if($(".AnchorBtn", this)[0])
			$(".AnchorBtn", this)[0].style.visibility = "hidden";
	}
	
	/** ####################################### 功能 ############################################ */
	
	/** 数组中指定元素的索引 */
	function arrayIndexOf(arr, e, fromIndex) {
		var i = fromIndex || 0;
		
		for (; i < arr.length; i++) {
			if (arr[i].id === e.id) {
				return i;
			}
		}
		
		return -1;
	}
	
	/** 数组中指定元素的索引 */
	function arrayIndexOfUnit(arr, e, fromIndex) {
		var i = fromIndex || 0;
		
		for (; i < arr.length; i++) {
			if (arr[i].id === e.data.id) {
				return i;
			}
		}
		
		return -1;
	}
	
	/** 向集合中追加不重复的记录 */
	function appendDistinct(src, ds) {
		for (var i = 0; i < ds.length; i++) {
			var key = ds[i].id;
			if (!src[key]) {
				src[src.length] = ds[i];
				src[key] = ds[i];
			}
		}
	}
	
	/** 向dest添加或覆盖src的属性 */
	function extend(dest, src) {
		var prop;
		for (prop in src) {
			if (src.hasOwnProperty(prop)
					&& !Ext.isEmpty(src[prop])) {//防止前天传入的值为空或者undefined haosl add 2017-10-25
				dest[prop] = src[prop];
			}
		}
	}
	
	/** ajax */
	function ajax(opt) {
		var xhr;
		if (window.XMLHttpRequest) { // Mozilla, Safari, IE7+ ...
			xhr = new XMLHttpRequest();
		} else if (window.ActiveXObject) { // IE 6 and older
			xhr = new ActiveXObject("Microsoft.XMLHTTP");
		}
		
		var tpl = {
			method: "",
			url: "",
			async: true,
			data: {},
			callback: EMPTY_FUNCTION
		};
		
		extend(tpl, opt);
		
		xhr.onreadystatechange = function () {
			if (xhr.readyState === 4) {
				if (xhr.status === 200) {
					if (tpl.callback) {
						tpl.callback.call(null, xhr);
					}
				} else {
					alert('There was a problem with the request.');
				}
			}
		};
		
		xhr.open(tpl.method, tpl.url, tpl.async);
		if (POST === tpl.method) {
			xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		}
		xhr.send();
	}
	
	/** 根据参数获取目标元素 */
	function $(e, parentNode) {
		return !e || typeof e !== "string" ? e : Ext.query(e, true, parentNode);
	}
	
	/** 添加指定class */
	function addClass(node, className) {
		if (!node) {return;}
		node.className +=  " " + className;
	}
	
	/** 删除元素的指定class */
	function removeClass(node, className) {
		if (!node) {return;}
		node.className = node.className.replace(new RegExp("\\b" + className + "\\b", "g"), "");
	}
	
	/** 判断元素是否包含的指定class */
	function hasClass(node, className) {
		if (!node) {return false;}
		return new RegExp("\\b" + className + "\\b", "g").test(node.className);
	}
	
	/** 创建闭包环境 */
	function bind(fn, thisObj) {
		if (!fn || typeof fn !== "function") {return null;}
		
		var args = [];
		if (arguments[2]) {
			for (var i = 2; i < arguments.length; i++) {
				args[args.length] = arguments[i];
			}
		}
		
		return (function() {
			fn.apply(thisObj, args);
		});
	}
	
	/** 计算后的样式 */
	function style(elmt, type) {
		if (IE) {
			return elmt.currentStyle[type];
		} else {
			return window.getComputedStyle(elmt)[type];
		}
	}

	/** 第一个会产生滚动条的父元素 */
	function scrollableParent(elmt) {
		var result;
		
		for (var e = elmt.offsetParent; e && e.nodeName != "BODY"; e = e.offsetParent) {
			var overflow = style(e, "overflow");
			if (overflow === 'scroll' || overflow === 'auto') {
				result = e;
				break;
			}
		}
		return result || getParent(elmt, "body");
	}

	/** 获取元素的位置和尺寸 */
	function absLoc(e) {
		if (arguments.length !== 1 || e === null) {
			return {};
		}
		var offsetTop = e.offsetTop;
		var offsetLeft = e.offsetLeft;
		var offsetWidth = e.offsetWidth;
		var offsetHeight = e.offsetHeight;
		
		while (e = e.offsetParent) {
			if (e.nodeName == "BODY") {break;}
				
			var overflow = style(e, "overflow");
			if (overflow === 'scroll' || overflow === 'auto') {break;}
			
			offsetTop += e.offsetTop;
			offsetLeft += e.offsetLeft;
		}
		
		return {
			top : offsetTop,
			left : offsetLeft,
			width : offsetWidth,
			height : offsetHeight
		};
	}

	/**
	 * 将目标定位到参照元素左下角
	 * @param srcElmt 目标元素
	 * @param ref 参照元素或者参照元素的位置和尺寸(top, left, width, height)
	 */
	function lowerLeft(srcElmt, ref) {
		var outline;
		if (ref.getAttribute) { // ref是DOM元素
			outline = absLoc(ref);
		} else { // ref是参照元素的位置和尺寸(top, left, width, height)
			outline = ref;
		}
		// 选人控件：单选时要动态的在输入框上方或下方展示 chent 2015/09/02 start
		var scrollTop = 0;
		var parent = scrollableParent(instance.target);
		// 如果产生滚动条的元素不在当前页面(模板页内)，取body的scrollTop
		if (parent.nodeName == "BODY" || hasClass(parent, TEMPLATE_TD)) {
			scrollTop += BODY.scrollTop;
		} else {
			scrollTop += parent.scrollTop;
		}
		// 选人控件： 多选时要显示在页面中间 chent 2015/09/18 start
		// 计算垂直居中的top
		if(scrollTop == 0){
			scrollTop = getScrollTop();//获取滚动条高度
		}
		var screenh = window.screen.availHeight;//窗口的高
		var domHeight = outline.top-scrollTop+130;//元素距浏览器顶部高
		if((domHeight + outline.height + 342)>screenh){//342:单人选人控件的高
			var top = outline.top - 342;
			if(top > 0){
				outline.top = top;
			}else{
				outline.top = outline.top + outline.height;
			}
		}else {
			outline.top = outline.top + outline.height;
		}
		// 选人控件：单选时要动态的在输入框上方或下方展示 chent 2015/09/02 end
		outline.width = undefined;
		outline.height = undefined;
		
		return outline;
		//locate(srcElmt, outline);
	}

	/**
	 * 定位目标元素
	 * @param srcElmt 目标元素
	 * @param outline 坐标和尺寸(top, left, width, height)
	 */
	function locate(srcElmt, outline) {
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
	}
	
	/** 去除字符创前后的空格符 */
	function trim(str) {
		var s = str || "";
	    return s.replace(/(^\s*)|(\s*$)/g, "");
	}
	
	/** 判断两元素是否为同一元素 */
	function isSameNode(a, b) {
		if (!a || !b) {
			return false;
		}
		
		if (a.isSameNode) {
			return a.isSameNode(b);
		} else {
			return a === b;
		}
	}
	
	/** 判断child节点是否存在一个parent祖先节点 */
	function hasParent(child, parent) {
		if (!child || !parent) {return false;}
		
		if ((typeof parent) === "string") {
			var s = "_4Parent_";
			try {
				child.setAttribute(s, "");
				return $(parent + " [" + s + "]").length > 0;
			} catch (e) {
			} finally {
				child.removeAttribute(s);
			}
		} else {
			for (var p = child.parentNode; p; p = p.parentNode) {
				if (p.nodeType == 9) {break;}
				
				if (isSameNode(parent, p)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/** 判断node节点是否在path路径上 */
	function isNode(node, path) {
		if (!node) {return false;}
		
		var s = "_4Parent_";
		try {
			node.setAttribute(s, "");
			return $(trim(path) + "[" + s + "]").length > 0;
		} catch (e) {
		} finally {
			node.removeAttribute(s);
		}
	}
	
	/** 查询符合表达式的最近的祖先节点 */
	function getParent(childNode, parentExpr) {
		var allParent = $(parentExpr);
		
		function helper(node) {
			return isSameNode(node, p);
		}
		
		for (var p = childNode.parentNode; p; p = p.parentNode) {
			if (p.nodeType !== 1) {break;}
			
			if (one(allParent, helper)) {
				return p;
			}
		}
	}

	/** 判断parent节点是否存在一个child子孙节点 */
	function hasChild(parent, child) {
		if (!parent || !child) {
			return false;
		}
		
		var children = parent.getElementsByTagName("*");
		var result = one(children, function(node) {
			return isSameNode(node, child);
		});
		
		return !!result;
	}

	/** 遍历集合中所有的元素，为每一个元素执行fn，直到fn返回true或全部元素遍历结束 */
	function one(arr, fn, _this) {
		if (!arr) {return;}

		for (var i = 0; i < arr.length; i++) {
			var thisObj = !_this ? arr[i] : _this;
			if (fn.call(thisObj, arr[i], i, arr) === true) {
				return arr[i];
			}
		}

		return undefined;
	}
	
	/** 遍历集合中所有的元素，为每一个元素执行fn，返回fn执行为true的全部元素的集合 */
	function some(arr, fn, _this) {
		if (!arr) {return [];}

		var _arr = [];
		for (var i = 0; i < arr.length; i++) {
			var thisObj = !_this ? arr[i] : _this;
			if (fn.call(thisObj, arr[i], i, arr) === true) {
				_arr[_arr.length] =  arr[i];
			}
		}

		return _arr;
	}
	
	/** 遍历集合中所有的元素，为每一个元素执行fn */
	function every(arr, fn, _this) {
		var rt = [];
		
		if (!arr) {return rt;}
		
		for (var i = 0; i < arr.length; i++) {
			var thisObj = !_this ? arr[i] : _this;
			fn.call(thisObj, arr[i], i, arr);
		}
	}
	function getScrollTop() {  
        var scrollPos = 0;
        if (window.pageYOffset) {
        	scrollPos = window.pageYOffset;
        } else if(window.parent.pageYOffset){
        	scrollPos = window.parent.pageYOffset;
        } else if (document.compatMode && document.compatMode != 'BackCompat') { 
        	if(IE) {
        		scrollPos = document.documentElement.scrollTop; 
        	}else {
        		scrollPos = document.body.scrollTop; 
        	}
        	
        	if(scrollPos==0) {//为0时继续取父页面滚动条，兼容工作计划——任务详情页面
        		
	    		if(IE) {
	    			scrollPos = window.parent.document.documentElement.scrollTop;
	    		} else {
	    			scrollPos = window.parent.document.body.scrollTop;
	    		}
	    	}
        }
        return scrollPos;   
	}
	// 多选选人控件是否需要收缩
	function isNeedZoom() {
		var flag = false;
		var bodyWidth = Ext.getBody().getWidth();
		var screenh = window.screen.availHeight-150;//窗口的高(不含菜单)，150：菜单高度
		if(screenh < (430+100) || bodyWidth < (560+100)){//窗口可见高度小于多选选人控件高度。560、430:多选选人控件的高宽、100是为了不要正好卡到临界值。
			flag = true;
		}
       
		return flag;   
	}
	
	//初始化
	function init(){
	}
	/**
	 * 添加全部
	 */
	function selectAll(unitNode,fn){
		//选单位
		if(instance.option.addunit === true || instance.option.adddepartment === true || instance.option.addpost === true){
			selectUnit(unitNode,fn);
		}else{//选人单独处理
			unitNode = unitNode || $("#PersonPicker-Main-Organization")[0];
			var map = new HashMap();
			var d = unitNode.data || {};
			var id = d.id || "";
			//instance.data.unit.hasAdded[id] = unitNode;
			if(!Ext.isEmpty(id)){
				id = id.replace(/＠/g, "@");
			}
			map.put("attachTo", id);
			map.put("ancester", d.ancester || "");
			map.put("level", d.level || "");
			map.put("nbases", instance.option.nbases || "");
			map.put("orgid", instance.option.orgid || "");
			map.put("extend_str", instance.option.extend_str || "");
			map.put("isSelfUser", instance.option.isSelfUser);
			map.put("isPrivExpression", instance.option.isPrivExpression);
			map.put("deprecate", instance.option.deprecate);
			map.put("validateSsLOGIN", instance.option.validateSsLOGIN);//是否启用认证库校验 chent 20170313
			map.put("selfUserIsExceptMe", instance.option.selfUserIsExceptMe);//业务用户时是否排除自己。chent 20170329
			map.put("selectByNbase", instance.option.selectByNbase);//是否按不同人员库显示 chent 20170419
			if(instance.option.selectByNbase){//区分人员库时，传当前操作的人员库
				map.put("contentNbs", instance.contentNbs);
			}
			map.put("defaultSelected", instance.option.defaultSelected);//默认已选
			Rpc({functionId:'21111111114',async:true,success:function(data){
				var result = Ext.decode(data.responseText);
				var person = result.person || [];
				if(!result.succeed){
					Ext.showAlert(result.message);
					return;
				}
				if(fn)
					fn(person);
			}},map);
		}
	}
	return PersonPicker;
})();