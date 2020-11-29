/**
 * 资格评审_在线投票系统
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('JobtitleCardView.CardView',{
	requires:['Ext.MessageBox', 'EHR.extWidget.proxy.TransactionProxy'],
	pageHeight : Ext.getBody().getHeight(),
	pageWidth : Ext.getBody().getWidth(),
	store:undefined,
	tabName : '',//当前显示的模板名称，弹出window的标题用
	constructor : function(config) {
		cardview_me = this;
		this.store = this.getCardViewStore();
		this.isshowrefresh = this.getConfig();//提交后，是否显示刷新按钮。true：显示刷新按钮 false：直接退出
		this.support_word = this.getSupportWord();// 公示、投票环节显示申报材料表单上传的word模板内容
		this.categoriesnummap = this.getCategoriesnummap();
		this.categoriesmap = this.getCategoriesmap();
		this.categorieslist = this.getCategoriesList();
		this.levellist = this.getLevelList();
		
		this.init();
	},
	// 初始化函数
	init:function(){
		// 初始化变量
		cardViewGlobal = {};
		cardViewGlobal.w0501 = '';// 记录当前操作时的申请人，答完卷时回调用
		cardViewGlobal.w0301 = '';// 记录当前操作时的申请人的会议，答完卷时回调用
		this.store.on('load', this.drowPage, this, {single:true});//初始化页面
		this.store.on('load', this.initCheck, this, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
		this.store.load();
		this.initEvent();
	},
	// 初始化绑定事件
	initEvent:function(){
		// 登录按钮
		var buttonArray = Ext.query('input[type=button]');
		if(buttonArray.length > 0){
			var button = buttonArray[0];//投票按钮
			Ext.getDom(button).onclick = function() {
				
				var items = cardview_me.store.data.items;
				var msg = cardview_me.submitCheck(items);
				if(Ext.isEmpty(msg)){
					// 校验后，通过： 显示结果确认
					cardview_me.showResultPage(items);
					Ext.getDom('bh-wzm-index-all').style.display='none';
					Ext.getBody().dom.style.overflowY = 'hidden';
					//Ext.getBody().dom.style.overflowY = 'hidden';
				} else {
					alert(msg.substring(0, msg.length-1)+'还没有评价，请评价后再提交！');
				}
			}
			
			var buttonrefresh = buttonArray[1];//刷新按钮
			Ext.getDom(buttonrefresh).onclick = function() {
				if(cardview_me.checkIsHaveNewData()){
					cardview_me.categoriesnummap = cardview_me.getCategoriesnummap();
		    		cardview_me.categoriesmap = cardview_me.getCategoriesmap();
		    		cardview_me.categorieslist = cardview_me.getCategoriesList();
					cardview_me.levellist = cardview_me.getLevelList();
		    		cardview_me.store.on('load', cardview_me.drowPage, cardview_me, {single:true});//初始化页面
					cardview_me.store.on('load', cardview_me.initCheck, cardview_me, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
					cardview_me.store.load();
	        	}
			}
		}
		
		
		// 重新定位，页面resize后
		Ext.getBody().el.on('resize', function(){ 
			var sizeObj = arguments[1];
			var w = sizeObj.width;
			var h = sizeObj.height;
			var task = new Ext.util.DelayedTask(function(){
				this.pageWidth = w;
				this.pageHeight = h;
				
				// 重置评审材料
				var showfile = Ext.getCmp('reviewfile_cardview_showfile_win');
				if(showfile){
					showfile.setHeight(this.pageHeight);
					showfile.setWidth(this.pageWidth);
				}
				
				// 重置结果确认
				var resultpanel = Ext.getCmp('resultpanel');
				if(resultpanel){
					resultpanel.setHeight(h);
					resultpanel.setWidth(w);
					resultpanel.setTop(Ext.getBody().el.getScrollTop());
				}
				// 动态设置卡片的css
				this.autoLoadDivCss();
			}, this);
			task.delay(200);
		},this); 
		
		window.setInterval(function(){//10分钟后台请求一次空交易，避免会话超时
			var map = new HashMap();
			map.put("type", '3');
		    Rpc({functionId:'ZC00003009',async:false,success:function(){
		    	return;
		    },scope:this},map);            
        }, 600000);
	},
	// 更新审批状态 
	updateCommentRs:function(state, w0501, w0301, categories_id){
		if(state == "0"){
			return;
		}
		var map = new HashMap();
		map.put("type", "1");//审批状态
		map.put("state", state);
		map.put("w0501", w0501+"");
		map.put("w0301", w0301+"");
		map.put("categories_id", categories_id+"");
	    Rpc({functionId:'ZC00003011',async:false,success:function(form,action){
	    	if(state == '1'){
	    		cardview_me.w0501 = w0501;
	    		cardview_me.w0301 = w0301;
	    		cardview_me.categories_id = categories_id;
	    		var items = cardview_me.store.data.items;
	    		for(var i=0; i<items.length; i++){
					var record = items[i];
					if(record.data.w0501 == w0501 && record.data.w0301==w0301 && record.data.categories_id==categories_id){
						var approvalState = record.data.approvalState;
						cardview_me.approvalState = approvalState;
					}
				}
	    		cardview_me.store.on('load', cardview_me.checkAgreeNumForApproval, undefined, {single:true});//赞成人数校验
	    	}
	    	
	    	cardview_me.store.load();
    	}},map);
	},
	// 投票过程差额投票校验
	checkAgreeNumForApproval :function(){
		
		var msg = '';
		msg = cardview_me.checkAgreeNum();
		
		// 如果不符合要求，要把选项置为原来选择的项目上
		if(!Ext.isEmpty(msg)){
			//var msgpre = '您所赞成的人数不符合要求，如下：<br>';
			if(!Ext.isEmpty(cardview_me.approvalState)){
				var idpre = 'agree_';
				if(cardview_me.approvalState == '1'){
					idpre = 'agree_';
				} else if(cardview_me.approvalState == '2'){
					idpre = 'against_';
				} else if(cardview_me.approvalState == '3'){
					idpre = 'abstentions_';
				}
				var label = Ext.get(idpre+cardview_me.w0501);
				if(label){
					if(cardview_me.approvalState == '1'){
						label.dom.checked = false; 
					}else{
						label.dom.checked = true; 
					}
				}
				var f_label = Ext.get('f_'+idpre+cardview_me.w0501);
				if(f_label){
					if(cardview_me.approvalState == '1'){
						f_label.dom.checked = false; 
					}else{
						f_label.dom.checked = true; 
					}
				}
			}else{
				var label = Ext.get('agree_'+cardview_me.w0501);
				if(label){
					label.dom.checked = false; 
				}
				var f_label = Ext.get('f_'+'agree_'+cardview_me.w0501);
				if(f_label){
					f_label.dom.checked = false; 
				}
			}
			if(cardview_me.approvalState == '1'){
				cardview_me.approvalState = '';
			}
			cardview_me.updateCommentRs(cardview_me.approvalState, cardview_me.w0501, cardview_me.w0301, cardview_me.categories_id);
			alert(/*msgpre + */msg);
			return ;
		}
	
	},
	// 差额投票校验
	checkAgreeNum:function(){
		var msg = '';
		var items = cardview_me.store.data.items;
		for(var p in cardview_me.categoriesnummap){
			var config_categories_id = p.split('_')[0];
			var config_c_level = p.split('_')[1];
			var config_num = cardview_me.categoriesnummap[p].split('_')[0];
			var config_showname = cardview_me.categoriesnummap[p].split('_')[1];
			var nameStr = '';
			var num = 0;
			for(var i=0; i<items.length; i++){
				var record = items[i];
				var categories_id = record.data.categories_id;
				var c_level = record.data.c_level;
				var w0511 = record.data.w0511;
				var approvalState = record.data.approvalState;
				if(config_categories_id==categories_id && config_c_level==c_level){
					nameStr += (w0511+'，');
					if(approvalState=='1'){
						num ++;
					}
				}
			}
			if(!Ext.isEmpty(nameStr)){
				nameStr = nameStr.substring(0, nameStr.length-1);
			}
			var level_name = config_showname;
			if(config_showname.indexOf('-') > -1){
				level_name = config_showname.split('-')[1];
			}
			if(num > config_num) {
				//msg += ('【'+config_showname+'】要求赞成人数'+config_num+"，您赞成人数"+num+"。<br>");
				msg += level_name+/*'（'+nameStr+'）'+*/'最多给'+config_num+'人投赞成票！';
			}
		}
		return msg;
	},
	// 打开鉴定意见
	questionnaire:function(type, w0539, w0541, w0539_qnid, w0541_qnid, w0501, w0301, expertState, w0501_safe, expertName, subObject){
		
		/**
		 * 关键参数说明：
		 * type:专家类型 1：评委会专家 2：学科组 3：外部鉴定专家
		 * expertState：专家状态标识 0|1|null|''：待审 2：已审 3：已提交
		 * w0539:内部评审问卷计划号：聘委会、学科组专家用
		 * w0541:专家鉴定问卷计划号：外部鉴定专家用
		 * w0539_qnid：问卷计划号对应的问卷号
		 * w0541_qnid：问卷计划号对应的问卷号
		 * chent
		 **/
		
		// 如果专家类型都没有，直接不让答题
		if(type == "" || type == null){
			alert('鉴定意见未制定!');
			return ;
		}
		
		cardViewGlobal.w0501 = w0501;// 记录当前操作时的申请人，答完卷时回调用
		cardViewGlobal.w0301 = w0301;// 记录当前操作时的申请人的会议，答完卷时回调用
		
		// 配置问卷的信息
		var suerveyid = "";
		var qnId = "";
		var title = '鉴定意见';
		if(type == "1" || type == "2" || type == "4"){// 评委会、学科组、学院任聘组的问卷配置
			if(w0539 == "" || w0539 == null){
				alert('鉴定意见未制定!');
				return ;
			}
			suerveyid = w0539;
			qnId = w0539_qnid;
			title = '评审意见';
		} else {// 外部鉴定专家问卷配置
			if(w0541 == "" || w0541 == null){
				alert('鉴定意见未制定!');
				return ;
			}
			suerveyid = w0541;
			qnId = w0541_qnid;
		}
		isPreview = false;// 预览
		if(expertState == "2" || expertState == "3"){//已审 
			isPreview = true;
		}else if(Ext.isEmpty(expertState) || expertState == "1" || expertState == "0"){//待审
			isPreview = false;
		}
		var planid = "";
		planid2 = suerveyid;
		cip = '';
		//--/ehr/hrms/module/system/questionnaire/template/AnswerQn.jsp
		module = 'jobtitle';
		w0501 =w0501;
		w0301 = w0301;
		var url = escape(window.location.href);
		
		window.location.href = '../../../system/questionnaire/mobile/index.jsp' +
	        		'?qnId='+qnId+'&planid='+suerveyid+'&mainObject='+expertName+'&subObject='+subObject+"&module="+module+"&w0501="+w0501+"&w0301="+w0301+"&isPreview="+isPreview+"&url="+url;
		
	},
	// 调查问卷回调，更新专家状态标识
	questionRs:function(state){
		if(state == "" || state == "0"){
	    	Ext.destroy(Ext.getCmp('qnWin'));
		    var task = new Ext.util.DelayedTask(function(){
			}, this);
			task.delay(1000);
			return ;
		} else if(state == "1"){//交卷
			var map = new HashMap();
			map.put("type", "2");//专家状态标识
			map.put("state", "2");
			map.put("w0501", cardViewGlobal.w0501);
			map.put("w0301", cardViewGlobal.w0301);
		    Rpc({functionId:'ZC00003011',async:false,success:function(form,action){
		    	
				cardview_me.store.on('load', function(){
					
					// 未评=》已评
					var span = Ext.get('span_'+cardViewGlobal.w0501);
					if(span){
						span.setText('(已评)');
					}
					// 更新问卷链接
					var ques = Ext.get('ques_'+cardViewGlobal.w0501);
					if(ques){
						var personinfo = this.data.items;
						var info = '';
						for(var i=0; i<personinfo.length; i++){
							var data = personinfo[i].data;
							if(data.w0501 == cardViewGlobal.w0501){
								info = data;
							}
						}
						var type= info.type;
						var w0539= info.w0539;
						var w0541= info.w0541;
						var w0539_qnid= info.w0539_qnid;
						var w0541_qnid= info.w0541_qnid;
						var w0501= info.w0501;
						var w0301= info.w0301;
						var expertState= info.expertState;
						var w0501_safe= info.w0501_safe;
						var expertName= info.expertName;
						var subObject= info.subObject;
						var href = "javascript:cardview_me.questionnaire('"+type+"','"+w0539+"','"+w0541+"','"+w0539_qnid+"','"+w0541_qnid+"','"+w0501+"','"+w0301+"','"+expertState+"','"+w0501_safe+"','"+expertName+"','"+subObject+"')";
						ques.dom.href = href; 
					}
				},undefined,{single:true});
				cardview_me.store.load();
				
				var task = new Ext.util.DelayedTask(function(){
					Ext.destroy(Ext.getCmp('qnWin'));
				}, this);
				task.delay(1000);
				return ;
		    }},map);
		}
		
		Ext.getBody().dom.style.overflowY = 'auto';
		// 显示投票按钮,不显示刷新按钮。
		var buttonArray = Ext.query('input[type=button]');
		if(buttonArray.length > 0){
			var button = buttonArray[0];
			Ext.getDom(button).style.display = 'inline';
			
			var buttonrefresh = buttonArray[1];
			Ext.getDom(buttonrefresh).style.display = 'none';
		}
	},
	// 评审材料
	checkfile:function(path, nbasea0100, nbasea0100_1, type, w0536){
		if(this.support_word && !Ext.isEmpty(w0536) && w0536 != 'null'){// 支持WORD模板
			var servletpath = '/servlet/DisplayOleContent?filePath='+w0536+'&bencrypt=true'+'&openflag=true&t='+new Date();
			var win = open(servletpath, "pdf");
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
			obj.sys_type = '1';
			obj.module_id="11";////调用模块标记：职称模块
			obj.return_flag="14";//返回模块标记：不需要返回关闭按钮
			obj.tab_id=tabid;//模板号
			obj.task_id=taskid;//任务号 除0以外需加密
			obj.approve_flag="0";//不启用审批
			obj.view_type="card";//卡片模式
			obj.card_view_type="1";//卡片模式下不要显示左边导航树
			obj.other_param="visible_title=0`visible_toolbar=0`object_id="+nbasea0100+"`taskid_validate="+taskid_validate;//visible_title=0:不需要标题； visible_toolbar=0：不要按钮；object_id：对象nbase+a0100
			obj.callBack_init="cardview_me.showView";
	//		
	//		//获取业务模板名称
	//		var map = new HashMap();
	//		map.put("tabId", tabid);
	//	    Rpc({functionId:'ZC00003018',async:false,success:function(){
	//	    	var result = Ext.decode(arguments[0].responseText);
	//	    	this.tabName = result.tabName;
	//			// 调用人事异动模板 
	//			createTemplateForm(obj);
	//	    },scope:this},map);
			
			// 获取排除页签
			var map = new HashMap();
			map.put("tabId", tabid);
			map.put("getconfig", true);//获取材料公示配置信息
			map.put("type", type);//0：公示 1：聘委会 2：学科组 3：同行专家 4：二级单位
		    Rpc({functionId:'ZC00003018',async:false,success:function(){
		    	var result = Ext.decode(arguments[0].responseText);
		    	var configStr = result.configStr;
		    	if(!Ext.isEmpty(configStr)){
		    		obj.other_param += ('`noshow_pageno='+configStr)
			    }
			    var map = new HashMap();
			    cardview_me.initPublicParam(map,obj);
				map.put("infor_type", '1');
				map.put("flag", '1');
				map.put("object_id", nbasea0100_1);
				map.put("cur_task_id", taskid);
				map.put("downtype", '1');//0 一人一文档压缩下载  1 多人一文档直接下载
				map.put("outtype", '0');// 0 pdf 1 word
				Rpc({
					functionId : 'MB000020014',
					async : false,
					success : function(form) {
						var result = Ext.decode(form.responseText);
						if (result.succeed) {
							var judgeisllexpr = result.judgeisllexpr;
							if (judgeisllexpr != null && judgeisllexpr != "1")
								alert(judgeisllexpr);
							else {
								var filename = result.filename;
								var url = "/servlet/vfsservlet?fromjavafolder=true&fileid=" + filename;
								var win = open(url, "pdf");
		//						win.document.title = 'sfsfsff';
							}
						} else {
							alert(result.message);
						}
					}
				}, map);   
		    },scope:this},map);
		}
	},
    // 下载人员模板
    doRequestUsingPost : function(tp_id,questionid,current_id,tabid,ins_id,taskid,sp_batch,batch_task,pre,a0100) {
    	//'{tp_id}','{questionid}','{current_id}','{tabid}','{ins_id}','{taskid}','{sp_batch}','{batch_task}','{pre}','{a0100}'
    	var map = new HashMap();
			map.put("tp_id", tp_id);
		    Rpc({functionId:'ZC00003013',async:false,success:function(form,action){
		    	var templatefile = '';
		    	var result = Ext.decode(form.responseText);
				templatefile = result.templatefile;
				var url = "/servlet/OutputTemplateDataServlet?templatefile=" + templatefile + "&questionid=" + questionid + "&current_id=" + current_id + "&tabid=" + tabid 
					+ "&ins_id=" + ins_id + "&taskid=" + taskid + "&sp_batch=" + sp_batch + "&batch_task=" + batch_task 
					+ "&pre=" + pre + "&a0100=" + a0100;
				var win = window.open(url,"_blank");
		    }},map);
	},
    // 获取数据集
    getCardViewStore : function() {
    	var store = Ext.create('Ext.data.Store', {
			fields:['w0301','w0501','w0501_safe','w0511','w0513','w0515','w0533','w0535','w0537','w0539','w0541','w0539_qnid','w0541_qnid','imgpath','type','expertState','approvalState','expertName','nbasea0100_safe','tp_id', 'tabid', 'ins_id', 'taskid', 'sp_batch', 'batch_task','pre','a0100','questionid','current_id','subObject'],
			proxy:{
				type: 'transaction',
		        functionId:'ZC00003009',
				extraParams:{
				},
				 reader: {
					  type: 'json',
					  root: 'personinfo'         	
				}
			},
			autoLoad: true
		});
		
		return store;
	},
	// 显示人事异动模板
	showView:function(){
        var container = Ext.create('Ext.container.Container', {
        	region: 'center',
		    layout: 'fit',
		    border: false,
		    items: [templateMain_me.mainPanel]
		});
		Ext.create('Ext.window.Window', {
			title:cardview_me.tabName,
			id:'reviewfile_cardview_showfile_win',
			layout: 'border',
			modal: true,
			width:cardview_me.pageWidth,
			height:cardview_me.pageHeight,
			border:false,
			autoScroll:false,
		    items: [container]
		}).show();
	},
	// 对页面数据进行提交
	submitResult:function(){
		// 先进行差额校验
		var msg = cardview_me.checkAgreeNum();
		if(!Ext.isEmpty(msg)){
			alert(msg);
			return ;
		}
		
		var items = cardview_me.store.data.items;
		
		var objArr = [];
		for(var i=0; i<items.length; i++) {
			var record = items[i];
			var w0501 = record.data.w0501;
			var w0301 = record.data.w0301;
			var categories_id = record.data.categories_id;
			
			var m = new HashMap();
			m.put('w0501', w0501);
			m.put('w0301', w0301);
			m.put('categories_id', categories_id);
			objArr.push(m);
		}
		var map = new HashMap();
		map.put("type", '2');//更新“专家状态标识(expert_state)”
		map.put("state", '3');//页面已提交
		map.put("objArr", objArr);
	    Rpc({functionId:'ZC00003011',async:false,success:function(form,action){
		
			// 隐藏投票按钮，显示刷新按钮。
			var buttonArray = Ext.query('input[type=button]');
			if(buttonArray.length > 0){
				var button = buttonArray[0];
				Ext.getDom(button).style.display = 'none';
				
				var buttonrefresh = buttonArray[1];
				if(cardview_me.isshowrefresh){
					Ext.getDom(buttonrefresh).style.display = 'inline';
				}
			}
			
			// 页面上的radio不可用
			var radlioArray = Ext.query('input[type=radio]');
			if(radlioArray.length > 0){
				for(var i=0; i<radlioArray.length; i++){
					var radio = radlioArray[i];
					Ext.getDom(radio).disabled = true;
				}
				
				alert('提交成功！');
				// 配置了"显示刷新"参数时，提交后显示刷新按钮，否则直接退出。 chent 20171212 modify
				if(!cardview_me.isshowrefresh){
					cardview_me.goToLogon();
				}else {
					cardview_me.closeResultPage();
				}
			}
	    }}, map);
	},
	// 回到登录页面
	goToLogon:function(){
		window.location.href = '../../logon/index/Logon.html'
	},
	// 页面初始化校验
	initCheck:function(){
		var items = this.store.data.items;
		if(items.length == 0){//没有
			if(!this.isshowrefresh){
				alert('您已提交本轮投票结果！');
				this.goToLogon();
			}else {
				var buttonArray = Ext.query('input[type=button]');
				if(buttonArray.length > 0){
					// 不显示投票按钮。
					var button = buttonArray[0];
					Ext.getDom(button).style.display = 'none';
					
					// 显示刷新按钮
					var buttonrefresh = buttonArray[1];
					Ext.getDom(buttonrefresh).style.display = 'inline';
				}
			}
			return ;
		}
		// 校验是否已提交 ：判断全部数据，都是已提交了才算已提交
		var isSubmit = true;
		var onlyready = false;
		for(var i=0; i<items.length; i++) {
			var record = items[i];
			var expertState = record.data.expertState;
			var type = record.data.type;
			if(expertState != '3'){//3:已提交
				isSubmit = false;
			}
			
			var usetype = record.data.usetype;
			if(usetype == 1){//查看账号
				onlyready = true;
			}
			
		}
		// 查看账号时：1、不显示问卷 2、不显示赞成反对弃权 3、不显示投票按钮。
		if(onlyready){
			// 1、不显示问卷 
			var liArray = Ext.query('td[type=agereeli]');
			if(liArray.length > 0){
				for(var i=0; i<liArray.length; i++){
					var li = liArray[i];
					Ext.getDom(li).style.display = 'none';
				}
			}
			
			// 2、不显示赞成反对弃权
			var tableArray = Ext.query('table[type=agereeli]');
			if(tableArray.length > 0){
				for(var i=0; i<tableArray.length; i++){
					var table= tableArray[i];
					Ext.getDom(table).style.display = 'none';
				}
			}

			var buttonArray = Ext.query('input[type=button]');
			if(buttonArray.length > 0){
				// 3、不显示投票按钮。
				var button = buttonArray[0];
				Ext.getDom(button).style.display = 'none';
				
				// 4、不显示刷新按钮
				var buttonrefresh = buttonArray[1];
				Ext.getDom(buttonrefresh).style.display = 'none';
			}
		}else {//投票账号 显示投票按钮
			var buttonArray = Ext.query('input[type=button]');
			if(buttonArray.length > 0){
				// 显示投票按钮。
				var button = buttonArray[0];
				Ext.getDom(button).style.display = 'inline';
				// 不显示刷新按钮
				var buttonrefresh = buttonArray[1];
				Ext.getDom(buttonrefresh).style.display = 'none';
			}
		}
		
		// 如果都是已提交状态则 ： 1、不显示【保存】【提交】按钮  2、赞成反对弃权不可修改。
		if(isSubmit){
			var buttonArray = Ext.query('input[type=button]');
			if(buttonArray.length > 0){
				// 不显示投票按钮。
				var button = buttonArray[0];
				Ext.getDom(button).style.display = 'none';
				// 显示刷新按钮
				var buttonrefresh = buttonArray[1];
				Ext.getDom(buttonrefresh).style.display = 'inline';
			}
			// 页面上的radio不可用
			var radlioArray = Ext.query('input[type=radio]');
			if(radlioArray.length > 0){
				for(var i=0; i<radlioArray.length; i++){
					var radio = radlioArray[i];
					Ext.getDom(radio.id).disabled = true;
				}
			}
		}
	},
	// 点击【提交】按钮时校验
	submitCheck:function(items){
		var msg = "";
		for(var i=0; i<items.length; i++) {
			var record = items[i];
			// 校验问卷是否已审 ，如果没有配置，则不校验
			var type = record.data.type;
			var w0511 = record.data.w0511;
			if(type == '1' || type == '2' || type == '4') {// 评审会专家、学科组专家、学院任聘组
				var w0539 = record.data.w0539;
				var w0539_qnid = record.data.w0539_qnid;
				if(!Ext.isEmpty(w0539) && !Ext.isEmpty(w0539_qnid)) {// 配置了问卷
					var expertState = record.data.expertState;
					if(expertState != '2') {//2：已审 
						msg += w0511+',';
						continue ;
					}
				}
			} else if(type == '3') {// 外部鉴定专家
				var w0541 = record.data.w0541;
				var w0541_qnid = record.data.w0541_qnid;
				if(!Ext.isEmpty(w0541) && !Ext.isEmpty(w0541_qnid)) {// 配置了问卷
					var expertState = record.data.expertState;
					if(expertState != '2') {//2：已审 
						msg += w0511+',';
						continue ;
					}
				}
			}
			
			// 校验赞成、反对、弃权项是否填写
			var approvalState = record.data.approvalState;
			if(Ext.isEmpty(approvalState)){
				msg += w0511+',';
				continue ;
			}
		}
		
		return msg;
	},
	// 生成页面元素
	drowPage:function(){
		var cardviewDiv = Ext.getDom("cardviewmain");//先移除所有已有数据
		if(cardviewDiv){
			cardviewDiv.innerHTML = '';  
		}
		
		for(var ii=0; ii<cardview_me.categorieslist.length; ii++){
			var categoriesid = cardview_me.categorieslist[ii];
			
			for(var w in cardview_me.categoriesmap){
				var _config_categories_id = w.split('_')[0];
				var _categories_name = w.split('_')[1];
				if(categoriesid != _config_categories_id){
					continue ;
				}
				// 先看这个分组有没有人,没有人直接不写标题了
				var num = 0;
				for(var p in cardview_me.categoriesnummap){
					var config_categories_id = p.split('_')[0];
					var config_c_level = p.split('_')[1];
						
					var personinfo = cardview_me.store.data.items;
					for(var i=0; i<personinfo.length; i++){
						var info = personinfo[i].data;
						
						var categories_id = info.categories_id;
						var c_level = info.c_level;
						if(_config_categories_id==config_categories_id && config_categories_id==categories_id && config_c_level==c_level){
							num++;
						}
					}
				}
				if(num == 0){
					continue ;
				}
				var innerHTMLValue = '';
				innerHTMLValue += '<span style="color:#fff;">'+_categories_name+'</span>'+'：';
				
				var levelArray = cardview_me.categoriesmap[w];
				for(var ll=0; ll<cardview_me.levellist.length; ll++){
					var levelid = cardview_me.levellist[ll];
					for(var i=0; i<levelArray.length; i++){
						var level = levelArray[i];
						var _level = level.split('_')[0];
						if(_level != levelid){
							continue ;
						}
						var _level_person_num = level.split('_')[1];
						var _level_num = level.split('_')[2];
						var _level_name = level.split('_')[3];
						
						if(_level_person_num == 0){
							continue ;
						}
						
						innerHTMLValue += '<span style="color:#fff;">申报'+_level_name+_level_person_num+'人；'/*+'</span>人，最多给<span style="color:#007aff;">'+_level_num+'</span>人投赞成票；'*/;
					}
				}
				innerHTMLValue = innerHTMLValue.substring(0, innerHTMLValue.length-1);
				innerHTMLValue += '。';
				
				var div = document.createElement("div");
				div.innerHTML = innerHTMLValue;
				div.style.clear = 'both';
				div.style.border = 'none';
				//div.style.float = 'left';
				div.style.margin = '0px 5px 0px 5px';
				div.style.padding = '8px 0 8px 20px';
				div.style.fontSize = '14px';
				div.style.color = '#fff';
				div.style.backgroundColor = '#007EFF';
				div.style.borderRadius = '5px';
				cardviewDiv.appendChild(div);
	
				/*var br = document.createElement("br");
				br.style = 'clear:both;overflow:hidden;';
				cardviewDiv.appendChild(br);
				*/
				
				for(var jj=0; jj<cardview_me.levellist.length; jj++){
					var levelid = cardview_me.levellist[jj];
					
					for(var p in cardview_me.categoriesnummap){
						var config_categories_id = p.split('_')[0];
						var config_c_level = p.split('_')[1];
						if(levelid != config_c_level){
							continue ;
						}	
						
						var personinfo = cardview_me.store.data.items;
						var first = true;
						for(var i=0; i<personinfo.length; i++){
							var info = personinfo[i].data;
							
							var categories_id = info.categories_id;
							var c_level = info.c_level;
							if(_config_categories_id==config_categories_id && config_categories_id==categories_id && config_c_level==c_level){
								if(first){
									first = false;
									var level_name = '';
									for(var iii=0; iii<levelArray.length; iii++){
										var level = levelArray[iii];
										var _level = level.split('_')[0];
										var _level_person_num = level.split('_')[1];
										var _level_num = level.split('_')[2];
										var _level_name = level.split('_')[3];
										
										if(c_level == _level){
											level_name = _level_name;
											break;
										}
									}
									var innerHTMLValue = '';
									innerHTMLValue += '<span style="color:#007aff;">'+level_name+'</span>'+'';
									var div = document.createElement("div");
									div.innerHTML = innerHTMLValue;
									div.style.clear = 'both';
									div.style.border = 'none';
									div.style.margin = '10px 5px 0px 5px';
									div.style.padding = '8px 0px 8px 20px';
									div.style.fontSize = '14px';
									div.style.color = '#333';
									//div.style.backgroundColor = '#F7F7F7';
									if(!Ext.isEmpty(level_name)){
										cardviewDiv.appendChild(div);
									}
								}
								var card = cardview_me.createCard(info);
								
								cardviewDiv.appendChild(card);
							}
						}
					}
				}
			}
		}
		
		
	},
	// 生成人员卡片
	createCard:function(info){
		var expertState= info.expertState;
		var w0541= info.w0541;
		var ins_id= info.ins_id;
		var taskid= info.taskid;
		var type= info.type;
		var w0501_safe= info.w0501_safe;
		var w0301= info.w0301;
		var w0511= info.w0511;
		var w0539_qnid= info.w0539_qnid;
		var w0513= info.w0513;
		var w0537= info.w0537;
		var disagreetext= info.disagreetext;
		var w0539= info.w0539;
		var tabid= info.tabid;
		var subObject= info.subObject;
		var w0535= info.w0535;
		var w0536= info.w0536;
		var w0541_qnid= info.w0541_qnid;
		var w0533= info.w0533;
		var expertName= info.expertName;
		var nbasea0100_safe= info.nbasea0100_safe;
		var nbasea0100_safe_1= info.nbasea0100_safe_1;
		var w0515= info.w0515;
		var approvalState= info.approvalState;
		var questionid= info.questionid;
		var giveuptext= info.giveuptext;
		var w0501= info.w0501;
		var pre= info.pre;
		var a0100= info.a0100;
		var imgpath= info.imgpath;
		var agreetext= info.agreetext;
		var batch_task= info.batch_task;
		var tp_id= info.tp_id;
		var sp_batch= info.sp_batch;
		var w0513itemtext= info.w0513itemtext;
		var w0515itemtext= info.w0515itemtext;
		var categories_id= info.categories_id;
		var c_level= info.c_level;
		
		var html = '';
			html+='<table width="90%" border="0">';
				html+='<tr>';
					html+='<td rowspan="5"><img src='+imgpath+' /></td>';
					html+='<td class="hj-min-yi">'+w0511+'</td>';
				html+='</tr>';
				html+='<tr>';
					html+='<td class="hj-min-er">'+w0513itemtext+'：'+w0513+'</td>';
				html+='</tr>';
				html+='<tr>';
					html+='<td class="hj-min-er">'+w0515itemtext+'：'+w0515+'</td>';
				html+='</tr>';
				html+='<tr>';
					var applyfileText = '',applyfileTextHref = '';
					if(type == 1 || type == 2 || type == 4){
						applyfileText = '申报材料';
						if(!Ext.isEmpty(w0535) || !Ext.isEmpty(w0536)){
							applyfileTextHref = "<a href=javascript:cardview_me.checkfile('"+w0535+"','"+nbasea0100_safe+"','"+nbasea0100_safe_1+"','"+type+"','"+w0536+"');>评审材料</a>";
						}
					} else if(type == 3){
						applyfileText = '鉴定材料';
						if(!Ext.isEmpty(w0537)){
							applyfileTextHref = "<a href=javascript:cardview_me.checkfile('"+w0537+"','"+nbasea0100_safe+"','"+nbasea0100_safe_1+"','"+type+"');>"+zc.label.proficientcheckfile+"</a>";
						}
					}
					html+='<td class="hj-min-er">'+applyfileText+'：'+applyfileTextHref+'</td>';
				html+='</tr>';
				html+='<tr>';
					var text = "";
					if(expertState == 2 || expertState == 3){//页面已提交、问卷已答
						text = '(已评)';
					} else {
						text = '(未评)';
					}
					var proficientcommentHref = '';
					if(type == 1 || type == 2 || type == 4){
						if(!Ext.isEmpty(w0539) && !Ext.isEmpty(w0539_qnid)){
							proficientcommentHref = "专家评价：<a id='ques_"+w0501+"' href=javascript:cardview_me.questionnaire('"+type+"','"+w0539+"','"+w0541+"','"+w0539_qnid+"','"+w0541_qnid+"','"+w0501+"','"+w0301+"','"+expertState+"','"+w0501_safe+"','"+expertName+"','"+subObject+"');>评审意见</a><span id='span_"+w0501+"'>"+text;
						}
					}else if(type == 3){
						if(!Ext.isEmpty(w0541) && !Ext.isEmpty(w0541_qnid)){
							proficientcommentHref = "专家评价：<a id='ques_"+w0501+"' href=javascript:cardview_me.questionnaire('"+type+"','"+w0539+"','"+w0541+"','"+w0539_qnid+"','"+w0541_qnid+"','"+w0501+"','"+w0301+"','"+expertState+"','"+w0501_safe+"','"+expertName+"','"+subObject+"');>"+zc.label.checkcomment+"</a><span id='span_"+w0501+"'>"+text;
						}
					}
					html+='<td type=agereeli class="hj-min-er">'+proficientcommentHref+'</td>';
				html+='</tr>';
			html+='</table>';
			html+='<div class="hj-wzm-pj-min-one-yi">';
				html+='<table type=agereeli border="0">';
					html+='<tr>';
						var agreeCheckState = '', disaplayCheckState = '', giveupCheckState = '';
						if(approvalState == 1){
							agreeCheckState = 'checked=checked';
						}else if(approvalState == 2){
							disaplayCheckState = 'checked=checked';
						}else if(approvalState == 3){
							giveupCheckState = 'checked=checked';
						}
						html+='<td width="17"><input id="agree_'+w0501+'" name="commentRadio_'+w0501+'" type="radio" value="1" '+agreeCheckState+' onclick=cardview_me.updateCommentRs(this.value,"'+w0501+'","'+w0301+'","'+categories_id+'");></td>';
						html+='<td width="70" align="left"><label for="agree_'+w0501+'">'+agreetext+'</label></td>';
						
						html+='<td width="17"><input id="against_'+w0501+'" name="commentRadio_'+w0501+'" type="radio" value="2" '+disaplayCheckState+' onclick=cardview_me.updateCommentRs(this.value,"'+w0501+'","'+w0301+'","'+categories_id+'");></td>';
						html+='<td width="70"><label for="against_'+w0501+'">'+disagreetext+'</label></td>';
						
						html+='<td width="17"><input id="abstentions_'+w0501+'" name="commentRadio_'+w0501+'" type="radio" value="3" '+giveupCheckState+' onclick=cardview_me.updateCommentRs(this.value,"'+w0501+'","'+w0301+'","'+categories_id+'");></td>';
						html+='<td width="70"><label for="abstentions_'+w0501+'">'+giveuptext+'</label></td>';
					html+='</tr>';
				html+='</table>';
			html+='</div>';
			
		var div = document.createElement("div");
		div.className = "hj-wzm-pj-min-one";
		// 动态设置卡片的css
		div.style.marginLeft = cardview_me.getMarginLeft();
		div.innerHTML = html;
		
		return div;
	},
	// 显示结果确认页面
	showResultPage:function(items){
		var html = '';
		html+='<div id="bh-wzm-tpjg-all">';
	        
		for(var ii=0; ii<cardview_me.categorieslist.length; ii++){
				var categoriesid = cardview_me.categorieslist[ii];
				
				for(var w in cardview_me.categoriesmap){
					var _config_categories_id = w.split('_')[0];
					var _categories_name = w.split('_')[1];
					if(categoriesid != _config_categories_id){
						continue ;
					}
					// 先看这个分组有没有人,没有人直接不写标题了
					var num = 0;
					for(var p in cardview_me.categoriesnummap){
						var config_categories_id = p.split('_')[0];
						var config_c_level = p.split('_')[1];
							
						var personinfo = cardview_me.store.data.items;
						for(var i=0; i<personinfo.length; i++){
							var info = personinfo[i].data;
							
							var categories_id = info.categories_id;
							var c_level = info.c_level;
							if(_config_categories_id==config_categories_id && config_categories_id==categories_id && config_c_level==c_level){
								num++;
							}
						}
					}
					if(num == 0){
						continue ;
					}
					var innerHTMLValue = '';
					innerHTMLValue += '<span style="color:#fff;">'+_categories_name+'</span>'+'：';
					
					var levelArray = cardview_me.categoriesmap[w];
					for(var ll=0; ll<cardview_me.levellist.length; ll++){
						var levelid = cardview_me.levellist[ll];
						for(var i=0; i<levelArray.length; i++){
							var level = levelArray[i];
							var _level = level.split('_')[0];
							if(_level != levelid){
								continue ;
							}
							var _level_person_num = level.split('_')[1];
							var _level_num = level.split('_')[2];
							var _level_name = level.split('_')[3];
							
							if(_level_person_num == 0){
								continue ;
							}
							
							innerHTMLValue += '<span style="color:#fff;">申报'+_level_name+_level_person_num+'人；'/*+'</span>人，最多给<span style="color:#007aff;">'+_level_num+'</span>人投赞成票；'*/;
						}
					}
					innerHTMLValue = innerHTMLValue.substring(0, innerHTMLValue.length-1);
					innerHTMLValue += '。';
					
					var div = document.createElement("div");
					div.innerHTML = innerHTMLValue;
					div.style.clear = 'both';
					div.style.border = 'none';
					//div.style.float = 'left';
					div.style.margin = '15px 0 0px 5px';
					div.style.margin = '0px 0 15px 5px';
					div.style.padding = '8px 0 8px 20px';
					div.style.fontSize = '14px';
					div.style.color = '#333';
					div.style.backgroundColor = '#F7F7F7';
					var div = '<div style="clear:both;border-radius:5px;border:none;margin:0px 5px 0px 5px;padding:8px 0 8px 20px;color:#fff;background-color:#007EFF;">'+innerHTMLValue;
						div += '</div>';
					html += div;
					/*var br = document.createElement("br");
					br.style = 'clear:both;overflow:hidden;';
					cardviewDiv.appendChild(br);
					*/
					
					for(var jj=0; jj<cardview_me.levellist.length; jj++){
						var levelid = cardview_me.levellist[jj];
						
						for(var p in cardview_me.categoriesnummap){
							var config_categories_id = p.split('_')[0];
							var config_c_level = p.split('_')[1];
							if(levelid != config_c_level){
								continue ;
							}	
							
							var personinfo = cardview_me.store.data.items;
							var first = true;
							for(var i=0; i<personinfo.length; i++){
								var info = personinfo[i];
								
								var categories_id = info.data.categories_id;
								var c_level = info.data.c_level;
								if(_config_categories_id==config_categories_id && config_categories_id==categories_id && config_c_level==c_level){
									if(first){
										first = false;
										var level_name = '';
										for(var iii=0; iii<levelArray.length; iii++){
											var level = levelArray[iii];
											var _level = level.split('_')[0];
											var _level_person_num = level.split('_')[1];
											var _level_num = level.split('_')[2];
											var _level_name = level.split('_')[3];
											
											if(c_level == _level){
												level_name = _level_name;
												break;
											}
										}
										var innerHTMLValue = '';
										innerHTMLValue += '<span style="color:#007EFF;">'+level_name+'</span>'+'';
										
										var div = '<div style="clear:both;border:none;margin:10px 5px 0px 5px;padding:8px 0px 8px 20px;color:#007EFF;">'+innerHTMLValue;
											div += '</div>';
										
										if(!Ext.isEmpty(level_name)){
											html += div;
										}
									}
									html += cardview_me.getSubmitPeapleHtml(info);
								}
							}
						}
					}
				}
			}
		
//		for(var i=0; i<items.length; i++) {
//			var record = items[i];
//			html += cardview_me.getSubmitPeapleHtml(record);
//		}
		
		// 按钮html
		html += cardview_me.getSubmitButtonHtml();
		
		html += '</div>';
	    Ext.widget('panel', {
	    	id:'resultpanel',
	    	fullscreen:true,
	    	top:Ext.getBody().el.getScrollTop(),
	    	left:0,
	    	width:cardview_me.pageWidth,
	    	height:cardview_me.pageHeight,
		    html: html,
		    scrollable:'y',
		    renderTo:Ext.getBody()
		});
	},
	// 关闭结果确认页面
	closeResultPage:function(){
		cardview_me.updateRadio();
		Ext.getDom('bh-wzm-index-all').style.display = "block";
		Ext.getBody().dom.style.overflowY = 'auto';
		Ext.destroy(Ext.getCmp("resultpanel"));
	},
	// 更新赞成、反对、弃权选项
	updateRadio:function(){
		cardview_me.store.on('load', function(){
			var personinfo = this.data.items;
			for(var i=0; i<personinfo.length; i++){
				var info = personinfo[i].data;
				var w0501= info.w0501;
				var approvalState= info.approvalState;
				
				var agreeCheckState = '', disaplayCheckState = '', giveupCheckState = '';
				if(approvalState == 1){
					agreeCheckState = 'checked';
				}else if(approvalState == 2){
					disaplayCheckState = 'checked';
				}else if(approvalState == 3){
					giveupCheckState = 'checked';
				}
				var agree = Ext.get('agree_'+w0501);
				var against = Ext.get('against_'+w0501);
				var abstentions = Ext.get('abstentions_'+w0501);
				if(agree){
					agree.dom.checked = agreeCheckState; 
				}
				if(against){
					against.dom.checked = disaplayCheckState; 
				}
				if(abstentions){
					abstentions.dom.checked = giveupCheckState; 
				}
			}
			
		},undefined,{single:true});
		cardview_me.store.load();
	},
	// 获取材料参数
	initPublicParam:function(map,templPropety) {
	    map.put("sys_type",templPropety.sys_type);
	    map.put("module_id",templPropety.module_id);
	    map.put("return_flag",templPropety.return_flag);
	    map.put("approve_flag",templPropety.approve_flag);    
	    map.put("tab_id",templPropety.tab_id);     
	    map.put("task_id",templPropety.task_id);
	    map.put("view_type",templPropety.view_type);
	    map.put("infor_type",templPropety.infor_type);
	    map.put("other_param",templPropety.other_param);
	},
	// 获取参数：是否显示刷新按钮
	getConfig : function(){
		var isshowrefresh = false;
		
		var map = new HashMap();
		map.put("type", '1');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	isshowrefresh = result.isshowrefresh;
	    },scope:this},map);
	    
	    return isshowrefresh;
	},
	// "公示、投票环节显示申报材料表单上传的word模板内容"参数
	getSupportWord : function(){
		var support_word = false;
		
		var map = new HashMap();
		map.put("type", '3');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	support_word = result.support_word;
	    },scope:this},map);
	    
	    return support_word;
	},
	// 点击刷新时判断是否有新的申报人
	checkIsHaveNewData : function(){
		var isExist = true;
		
		var map = new HashMap();
		map.put("type", '2');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	isexist = result.isexist;
	    },scope:this},map);
	    
	    return isExist;
	},
	// 获取分组人员信息
	getCategoriesnummap : function(){
		var categoriesnummap = '';
		
		var map = new HashMap();
		map.put("type", '4');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	categoriesnummap = result.categoriesnummap;
	    }},map);
	    
	    return categoriesnummap;
	},
	// 获取分组信息
	getCategoriesmap : function(){
		var categoriesmap = '';
		
		var map = new HashMap();
		map.put("type", '5');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	categoriesmap = result.categoriesmap;
	    }},map);
	    
	    return categoriesmap;
	},
	// 获取分组顺序
	getCategoriesList : function(){
		var categorieslist = '';
		
		var map = new HashMap();
		map.put("type", '6');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	categorieslist = result.categorieslist;
	    }},map);
	    
	    return categorieslist;
	},
	// 获取职级顺序
	getLevelList : function(){
		var levellist = '';
		
		var map = new HashMap();
		map.put("type", '7');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	levellist = result.levellist;
	    }},map);
	    
	    return levellist;
	},
	// 提交、取消按钮html片段
	getSubmitButtonHtml:function(){
		var html = '';
		
		html+='<div class="bh-clear"></div>';
	        html+='<div class="hj-wzm-tpjg-anniu">';
	        	html+='<input type="button" class="hj-wzm-tpjg-tijiao" value="提　　交" onclick=javascript:cardview_me.submitResult();>';
	            html+='<input type="button" class="hj-wzm-tpjg-quxiao" value="取　　消" onclick=javascript:cardview_me.closeResultPage();>';
	        html+='</div>';
	        
	    html+='</div>';
	    
	    return html;
	},
	// 结果确认页面人员html片段
	getSubmitPeapleHtml:function(record){
		var html = '';
		
		var w0511 = record.data.w0511;
		var agreetext = record.data.agreetext;
		var disagreetext = record.data.disagreetext;
		var giveuptext = record.data.giveuptext;
		var approvalState = record.data.approvalState;
		var w0501 = record.data.w0501;
		var w0301 = record.data.w0301;
		var categories_id = record.data.categories_id;
		var c_level = record.data.c_level;
		
		html+='<div class="bh-wzm-tpjg-yi" style="margin-left:'+cardview_me.getMarginLeft()+';">';
        	html+='<h2>'+w0511+'</h2>';
        	html+='<div class="hj-wzm-pj-tpjg-xuan">';
                	html+='<table border="0">';
                      html+='<tr>';
                        var agreeCheckState = '', disaplayCheckState = '', giveupCheckState = '';
						if(approvalState == 1){
							agreeCheckState = 'checked=checked';
						}else if(approvalState == 2){
							disaplayCheckState = 'checked=checked';
						}else if(approvalState == 3){
							giveupCheckState = 'checked=checked';
						}
						html+='<td width="17" align="left"><input id="f_agree_'+w0501+'" name="commentRadio1_'+w0501+'" type="radio" value="1" '+agreeCheckState+' onclick=cardview_me.updateCommentRs(this.value,"'+w0501+'","'+w0301+'","'+categories_id+'");></td>';
						html+='<td width="70" align="left"><label for="f_agree_'+w0501+'">'+agreetext+'</label></td>';
						
						html+='<td width="17"><input id="f_against_'+w0501+'" name="commentRadio1_'+w0501+'" type="radio" value="2" '+disaplayCheckState+' onclick=cardview_me.updateCommentRs(this.value,"'+w0501+'","'+w0301+'","'+categories_id+'");></td>';
						html+='<td width="70" align="left"><label for="f_against_'+w0501+'">'+disagreetext+'</label></td>';
						
						html+='<td width="17"><input id="f_abstentions_'+w0501+'" name="commentRadio1_'+w0501+'" type="radio" value="3" '+giveupCheckState+' onclick=cardview_me.updateCommentRs(this.value,"'+w0501+'","'+w0301+'","'+categories_id+'");></td>';
						html+='<td width="70" align="left"><label for="f_abstentions_'+w0501+'">'+giveuptext+'</label></td>';
                      html+='</tr>';
                    html+='</table>';
              html+='</div>';
        html+='</div>';
        
        return html;
	},
	// 动态设置卡片的css
	autoLoadDivCss : function(){
		// 投票页面人员卡片div
		var cardArray = Ext.query('div.hj-wzm-pj-min-one');
		for(var i=0; cardArray && i<cardArray.length; i++){
			var card = cardArray[i];
			card.style.marginLeft = cardview_me.getMarginLeft();
		}
		// 结果确认页面卡片div
		cardArray = Ext.query('div.bh-wzm-tpjg-yi');
		for(var i=0; cardArray && i<cardArray.length; i++){
			var card = cardArray[i];
			card.style.marginLeft = cardview_me.getMarginLeft();
		}
	},
	// 获取卡片调整后的左边距
	getMarginLeft :function(){
		// 屏幕适配后的左边距
		var marginLeft = '0px';

		// 人员卡片的显示宽度
		var cardWidth = 324;
		// 如果只有一列则需要居中显示
		if(parseInt(cardview_me.pageWidth / cardWidth) == 1){
			// marginleft设定为屏幕适配后右侧剩余的宽度的一半
			var outWidth = cardview_me.pageWidth % cardWidth;
			marginLeft = outWidth/2+'px';
			
		}
		return marginLeft;
	}
});