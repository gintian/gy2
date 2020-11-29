
/**
 * 树形代码选择控件
 *
 * 用法：<img>元素上添加自定义属性
 *          1 plugin="codeselector"：必要的属性，此功能依赖于此属性
 *          2 codesetid：代码id；不能为空
 *          3 inputname：文本框名称,显示代码描述的文本框；不能为空
 *          4 afterfunc：选择代码成功后的回调函数,参数为1：value 2：text
 *          5 codesource：代码生成器类名称,比较复杂的或自定义的可以使用此属性，如果有此属性，下面属性都不需要了
 *          6 ctrltype：过滤类型
 *			    如果codesetid 为机构（UN、UM、@K）
 *			          0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
 *			          默认值为1
 *			    如果是普通代码类
 *			          0：不过滤，其他任意值（包括""）代表需要过滤（有效或在有效日期），默认过滤
 *          7 nmodule：业务模块id
 *          8 parentid：根节点id，如果设置此值，将加载此id的下级节点
 *
 *          9： expandTop 是否展开第一级节点  true   false
 *          10: onlySelectCodeset 是否限制只能选和codesetid相同的代码，例如codesetid=UN,不能选UM...
 *          11: isShowLayer 部门是否按层级显示,只限部门 不传或者'0'是不按层级,大于'1'的数字表示显示的层级
 *          12:multiple=true:多选，反之，为单选（缺省为单选）
 *          13:是否隐藏提示ID  isHideTip=true:隐藏提示ID，反之，显示（缺省为显示）
 *          14:targetID  传入需要将控件绑定到元素的id 有则按最新规则走，没有则按之前走
 *          15:editable 代码框输入值是否回填  true 不回填  false 回填 默认为true
 * <input>元素 name 命名规则：显示代码描述的inputname：xxx_view；保存代码值的input name：xxx_value.
 * 使用示例：
 * <input type="hidden" name="codeinput_value"/>
 * <input type="text" name="codeinput_view"/>
 * <img src="/../.." plugin="codetree" codesetid="UN" inputname="codeinput_view"/>
 * 选中代码后，值会自动填充到input里
 *
 * 兼容旧程序规则  wangb 20171117
 * 当<input>元素 name 命名不符合规则时： 在<img>元素中添加额外的属性valuename， valuename的值 : name属性值  注：属性type值为hidden的<input>元素的name属性值
 * 使用示例：
 * <input type="hidden" name="xxxxxxxx" />
 * <input type="text" name="codeinput。viewvalue" />
 * <img src="../../" plugin="codetree" codesetid="UN" inputname="codeinput。viewvalue" valuename="xxxxxxxx"  multi= true  multiple ="true"/>
 *
 *
 */
//修改ext默认图标src。防止连接www.sencha.com
Ext.BLANK_IMAGE_URL='data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';
Ext.onReady(function(){
	Ext.Loader.setPath("EHR","/components");
	Ext.tip.QuickTipManager.init();
	//选择img元素，并且plugin属性为“codetree”
	var imgEles = Ext.query('img[plugin=codeselector]');
	if(imgEles.length<1){
		return;
	}
	var codeSelector = window.codeSelector = new CodeSelector();
	for(var i=0;i<imgEles.length;i++){
		var ele       = imgEles[i],
			inputName = ele.getAttribute("inputname"),
			//添加 valuename 属性值 wangb 20171117
			valueName = ele.getAttribute("valuename")||inputName.substring(0,inputName.length-4)+"value",
			viewEles  = document.getElementsByName(inputName),
			valueEles = document.getElementsByName(valueName);

		if(viewEles.length<1 || valueEles.length<1)
			continue;

		var param = {
			valueEle:valueEles[0],
			viewEle:viewEles[0],
			handlerEle:ele,
			multiple:ele.getAttribute("multiple")=='true',
			codesetid:ele.getAttribute("codesetid"),
			afterfunc:ele.getAttribute("afterfunc"),
			codesource:ele.getAttribute("codesource"),
			nmodule:ele.getAttribute("nmodule"),
			ctrltype:ele.getAttribute("ctrltype"),
			expandTop:ele.getAttribute("expandTop")=='true',
			searchtext:"",
			parentid:ele.getAttribute("parentid")?ele.getAttribute("parentid").length:undefined,
			onlySelectCodeset:ele.getAttribute("onlySelectCodeset")=='true',
			isShowLayer:ele.getAttribute("isShowLayer")?ele.getAttribute("isShowLayer"):'0',
			isHideTip:ele.getAttribute("isHideTip")=='true',//add by xiegh on date20180109 是否隐藏提示信息
			targetID:ele.getAttribute("targetID"),//add by haosl 增加绑定目标元素id
			editable:ele.getAttribute("editable")=='false'?false:true//add by wangb 20180726 回填属性
		};

		ele.style.cursor='pointer';
		//绑定鼠标点击事件
		Ext.EventManager.addListener(ele,'click','start',codeSelector,param);
		Ext.EventManager.addListener(viewEles[0],'keyup','doSearch',codeSelector,param);
		Ext.EventManager.addListener(viewEles[0],'focus','saveData',codeSelector,param);
	}
	//初始化事件
	codeSelector.initEvent();
});

/**
 *主动关联 代码选择器
 * @param idList 要绑定codeselector的元素id
 */
function setEleConnect(idList){
	if(!window.codeSelector)
		window.codeSelector = new CodeSelector();
	var codeSelector = window.codeSelector;
	for(var index=0;index<idList.length;index++){
		var ele    = document.getElementById(idList[index]);
		if(!ele)continue;

		var  inputName = ele.getAttribute("inputname"),
			//添加 valuename 属性值 wangb 20171117
			valueName = ele.getAttribute("valuename")||inputName.substring(0,inputName.length-4)+"value",
			viewEles  = document.getElementsByName(inputName),
			valueEles = document.getElementsByName(valueName);

		if(viewEles.length<1 || valueEles.length<1)
			continue;

		var  param = {
			valueEle:valueEles[0],
			viewEle:viewEles[0],
			handlerEle:ele,
			multiple:ele.getAttribute("multiple")=='true',
			codesetid:ele.getAttribute("codesetid"),
			afterfunc:ele.getAttribute("afterfunc"),
			codesource:ele.getAttribute("codesource"),
			nmodule:ele.getAttribute("nmodule"),
			ctrltype:ele.getAttribute("ctrltype"),
			expandTop:ele.getAttribute("expandTop")=='true',  //控制是否展开所有节点 false 不展开|true 展开 ， 针对树形结构节点少的情况
			searchtext:"",
			parentid:ele.getAttribute("parentid")?ele.getAttribute("parentid").length:undefined,
			onlySelectCodeset:ele.getAttribute("onlySelectCodeset")=='true',
			isShowLayer:ele.getAttribute("isShowLayer")?ele.getAttribute("isShowLayer"):'0',
			isHideTip:ele.getAttribute("isHideTip")=='true',//add by xiegh on date20180109 是否隐藏提示信息
			targetID:ele.getAttribute("targetID"),//add by haosl 增加绑定目标元素id
			editable:ele.getAttribute("editable")=='false'?false:true//add by wangb 20180726 回填属性
		};

		ele.style.cursor='pointer';
		//绑定鼠标点击事件
		Ext.EventManager.addListener(ele,'click','start',codeSelector,param/*[inputName,codesetid,afterfunc,codesource,nmodule,ctrltype,'']*/);
		Ext.EventManager.addListener(viewEles[0],'keyup','doSearch',codeSelector,param/*[inputName,codesetid,afterfunc,codesource,nmodule,ctrltype]*/);
		Ext.EventManager.addListener(viewEles[0],'focus','saveData',codeSelector,param);
	}
	//初始化事件
	codeSelector.initEvent();
}


function CodeSelector(){
	this.store = Ext.create('Ext.data.TreeStore',{
		autoLoad:false,
		fields: ['text','id','codesetid','itemdesc','layerdesc','selectable'],
		proxy: Ext.create("EHR.extWidget.proxy.TransactionProxy",{
			extraParams:{
			},
			reader:{
				type:'json',
				root:'children'
			},
			functionId:'ZJ100000131'
		}),
		listeners:{
			load:function(store,records){
				if(store.proxy.extraParams.multiple){
					var recordList = records;
					if(Ext.getVersion().version.indexOf("4.")==0){
						recordList = records.childNodes;
					}

					for(var i=0;i<recordList.length;i++){
						if(this.selectedValue.indexOf("|"+recordList[i].get("id")+"|")!=-1)
							recordList[i].set("checked",true);
					}
				}
			}
		}
	});
}

CodeSelector.prototype.loadData = function(params){
	var me = this;
	Ext.apply(me.store.proxy.extraParams,params);
	var selectedValue = "|";
	Ext.each(me.context.selectedNode,function (node) {
		selectedValue+=node.value+"|";
	});
	me.store.selectedValue = selectedValue;
	me.store.load();
}



CodeSelector.prototype.start = function(evt,evtEle,opt,searchText){
	var me = this;
	opt.searchtext = searchText||"";
	if(!me.initContextData(opt)){
		return;
	}
	me.showSelector();
}

CodeSelector.prototype.initContextData = function(opt){
	if(this.context){
		this.context.selector.close();
		delete this.context;
	}

	var me = this;
	rawValue = opt.viewEle.value,
	value =  opt.valueEle.value;

	if(me.oldData && me.oldData.viewEle == opt.viewEle){
		rawValue = me.oldData.oldViewValue;
		value = me.oldData.oldValue;
	}

	var selectedNode = [];
	if(value && value.length>0 && rawValue && rawValue.length>0){
		var valueList = value.split("|");
		var nameList = rawValue.split("|");
		for(var i=0;i<valueList.length;i++){
			selectedNode.push({
				value:valueList[i],
				name:nameList[i]
			})
		}
	}
	me.context = {
		selectedNode:selectedNode,
		oldViewValue:rawValue,
		oldValue:value
	};
	Ext.apply(me.context,opt);
	return true;
}

CodeSelector.prototype.showSelector = function(){
	var me = this;
	me.loadData({
		codesetid  : me.context.codesetid,
		codesource : me.context.codesource,
		nmodule    : me.context.nmodule,
		ctrltype   : me.context.ctrltype,
		parentid   : me.context.handlerEle.getAttribute("parentid"),
		searchtext : encodeURI(me.context.searchtext),
		onlySelectCodeset:me.context.onlySelectCodeset,
		multiple:me.context.multiple,
		isShowLayer:me.context.isShowLayer,
		isHideTip:me.context.isHideTip
	})

	var width    = me.context.viewEle.offsetWidth,//+evtEle.offsetWidth,//获取文本框+图片宽度
		height   = document.body.clientHeight>800?270:200,//270;
		minWidth =document.body.clientHeight>800?230:180;

	if(width<minWidth) //人事异动当输入框太窄时，代码选择框不能用 dengcan
		width=minWidth;

	var selector = Ext.widget('treepanel',{
		width:width,
		height:height,
		store:me.store,
		floating:true,
		style:'z-index:100000000',
		shadow:false,
		rootVisible: false
	});

	if(me.context.multiple){
		selector.addDocked({
			xtype:'toolbar',
			dock:'bottom',
			style:{background:'white',aligin:'right'},
			items:['->',{
				xtype:'box',
				html:'<button class="mybutton">确定</button>',
				listeners:{
					click:me.multipleSelected,
					element: 'el',
					scope:me
				}
			},{
				xtype:'box',
				html:'<button class="mybutton">清空</button>',
				listeners:{
					click:me.cleanCheckedValue,
					element: 'el',
					scope:me
				}
			},{
				xtype:'box',
				html:'<button class="mybutton">取消</button>',
				listeners:{
					click:me.cancelSelect,
					element: 'el',
					scope:me
				}
			},'->']
		});

		selector.on("checkchange",me.updateMultipleSelectedValue,me);
	}else{
		selector.on({
			"itemclick":{fn:me.singleSelected,scope:me},
			"keyup":{
				element:'el',
				scope:me,
				fn:function(e){
					var me = this;
					//  判断是否为enter ie浏览器 e.keyCode == 13 非ie浏览器e.button == 12
					if(e.keyCode == 13){
						var node = me.context.selector.getSelection( )[0];
						if(!node)
							return;
						var text = node.get("itemdesc");
						var id =  node.get("id");
						me.context.viewEle.value = text;
						me.context.valueEle.value = id;
						var codeCallBack = me.context.afterfunc;
						if (codeCallBack && codeCallBack.length > 0)
							Ext.callback(eval(codeCallBack), null, [ id, text, true ]);//true:enter键触发 || false:鼠标点击触发
						me.finish_destory(true);
						return;
					}
				}
			}
		});
	}

	//渲染selector
	selector.render(document.body);
	//定位
	//haosl add 2018-4-12  这里是为了可以选择代码控件绑定到自己制定的元素上
	if(me.context.targetID && Ext.get(me.context.targetID)){
		selector.alignTo(Ext.get(me.context.targetID),'tl-bl?',undefined);
	}else{
		selector.alignTo(me.context.viewEle,'tl-bl?',undefined);
	}

	me.context.selector = selector;
};

CodeSelector.prototype.updateMultipleSelectedValue = function(node,checked){
	var me = this,
		nodes = me.context.selectedNode;
	if(checked){
		nodes.push({
			value:node.get("id"),
			name:node.get("itemdesc")
		});
	}else{
		for(var i=0;i<nodes.length;i++){
			if(node.get("id")==nodes[i].value){
				nodes.splice(i,1);
				break;
			}
		}
	}
};

/*多选模式选中处理*/
CodeSelector.prototype.multipleSelected=function(){
	var checked = this.context.selectedNode;
	var ids = "";
	var texts = "";
	for(var i=0;i<checked.length;i++){
		ids+="|"+checked[i].value;
		texts+="|"+checked[i].name;
	}
	ids = ids.substring(1);
	texts = texts.substring(1);
	this.context.viewEle.value  = texts;
	this.context.valueEle.value = ids;
	if(this.context.afterfunc)
		Ext.callback(eval(this.context.afterfunc),null,[ids,texts,false]);

	this.finish_destory(true);
};
CodeSelector.prototype.cleanCheckedValue=function(){
	this.context.viewEle.value   = "";
	this.context.valueEle.value = "";
	if(this.codeCallBack)
		Ext.callback(eval(this.codeCallBack),null,[ids,texts,false]);

	this.finish_destory(true);
}
/*取消选择按钮*/
CodeSelector.prototype.cancelSelect=function(){
	this.finish_destory(true);
};

CodeSelector.prototype.singleSelected = function(a,record){
	var me = this,
		codesetid = me .context.codesetid,
		onlySelectCodeset = me.context.onlySelectCodeset,
		viewEle = me.context.viewEle,
		valueEle = me.context.valueEle,
		isShowLayer = me.context.isShowLayer,
		codeCallBack = me.context.afterfunc;

	if(codesetid == 'UN' || codesetid == 'UM' || codesetid == '@K'){
		if(onlySelectCodeset && record.data.codesetid!=codesetid){//特殊处理UM UN @K 走原参数控制
			return;
		}
	}else if(record.data.selectable == 'false'){
		return;
	}

	//给text框赋值
	var text = "";
	if(record.data.layerdesc!=null&&record.data.layerdesc!=""&&parseInt(isShowLayer)>0&&codesetid.toUpperCase()=="UM"){
		text = record.data.layerdesc;
	}else if(record.data.itemdesc!=null&&record.data.itemdesc!=""){
		text = record.data.itemdesc;
	}else{
		text=record.data.text;
	}
	viewEle.value  = text;
	valueEle.value = record.data.id;
	if(codeCallBack)
		Ext.callback(eval(codeCallBack),null,[record.data.id,text,false]);
	//销毁控件释放资源
	me.finish_destory(true);
}

CodeSelector.prototype.doSearch = function(evt,evtEle,opt){
	var me = this;
	clearTimeout(me.timer);

	if(evt.keyCode == 40){
		if(!me.context || me.context.multiple)
			return;
        var selection = me.context.selector.getSelection( );
        if(selection.length>0)
        	return;
		var record = me.store.data.items[0];
		if(record)
			me.context.selector.getSelectionModel().select(record);
		me.context.selector.focus();
		return;
	}

	if(evt.keyCode == 13){
		if(!me.context || me.context.multiple)
			return;
		var record = me.store.data.items[0];
		me.singleSelected(me.context.selector,record);
		return;
	}


	me.timer = setTimeout(function(){
		if(me.context) {
			me.loadData({
				parentid: me.context.handlerEle.getAttribute("parentid"),
				searchtext: encodeURI(evtEle.value.replace(me.context.oldViewValue,""))
			});
		}else{
			opt.parentid = opt.handlerEle.getAttribute("parentid");
			me.start(evt,evtEle,opt,evtEle.value);
		}
	},500);
};

CodeSelector.prototype.saveData = function(evt,evtEle,opt){
	this.oldData = {
		oldValue:opt.valueEle.value,
		oldViewValue:opt.viewEle.value,
		viewEle:opt.viewEle
	};
};

/**
 * 初始化事件
 */
CodeSelector.prototype.initEvent = function(){
	var me = this;

	//页面单击事件
	Ext.getDoc().on("mousedown",function(e,t,o){
		if(!me.context || t==me.context.viewEle || me.context.selector.owns(t))
			return;
		//如果selector存在 && 触发单击事件元素不是文本框输入框  && 触发单击事件元素 不是 selector和selector的子元素   则销毁selector
		me.finish_destory(false);
	});
	//鼠标滚动分 火狐和其他浏览器 滚动事件不同  27706 wangb1 20170517
	if(Ext.isGecko){
		Ext.getDoc().dom.addEventListener("DOMMouseScroll",function(ev){
			if(me.context && !me.context.selector.owns(ev.target))
				me.finish_destory(false);
		});
	}else{
		Ext.getDoc().on("mousewheel",function(a,b,c){
			if(c.context && !c.context.selector.owns(b))
				c.finish_destory(false);
		},window,me);
	}
};

CodeSelector.prototype.finish_destory = function(beselect){
	var me = this,
		valueEle = me.context.valueEle,
		viewEle = me.context.viewEle,
		oldViewValue = me.context.oldViewValue;
	if(!beselect){
		if(me.context.multiple){
			if(!me.context.editable){
				viewEle.value  = oldViewValue;
			}
		}else{
			if(viewEle.value.length==0)
				valueEle.value="";
			else if(!me.context.editable){//当鼠标没有选择接电视 defaulttext为undefined，此时不予赋值 add by xiegh bug36102
				viewEle.value = oldViewValue;
			}
		}
	}
	me.context.selector.close();
	//Ext4的 treestore.removeAll()有bug guodd 2020-05-06
	if(Ext.getVersion().version.indexOf("4.")==0){
		me.store.getRootNode().removeAll();
	}else{
		me.store.removeAll();
	}
	delete me.context;
	delete me.oldData;
}
