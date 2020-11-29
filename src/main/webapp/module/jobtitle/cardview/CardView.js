/**
 * 资格评审_在线投票系统
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('JobtitleCardView.CardView',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	tabName : '',//当前显示的模板名称，弹出window的标题用
	pageHeight : Ext.getBody().getViewSize().height,
	pageWidth : Ext.getBody().getViewSize().width,
	store:undefined,
	constructor : function(config) {
		cardview_me = this;
		this.useType = config.useType;
		cardview_me.queue = config.queue;
		this.store = this.getCardViewStore();
		this.isshowrefresh = this.getConfig();//提交后，是否显示刷新按钮。true：显示刷新按钮 false：直接退出
		this.support_word = this.getSupportWord();// 公示、投票环节显示申报材料表单上传的word模板内容
		this.categoriesnummap = this.getCategoriesnummap();
		this.categoriesmap = this.getCategoriesmap();
		this.categorieslist = this.getCategoriesList();
		this.levellist = this.getLevelList();
		this.queueMap = this.queueMap();
		cardview_me.currentQueueMap = new HashMap();
		this.init();
	},
	// 初始化函数
	init:function(){
		// 屏蔽浏览器右键菜单
//		Ext.getDoc().on("contextmenu", function(e){
//			e.stopEvent();
//		});
		// 加载名片样式
		this.createSelfCss();
		// 初始化变量
		cardViewGlobal = {};
		cardViewGlobal.w0501 = '';// 记录当前操作时的申请人，答完卷时回调用
		cardViewGlobal.w0301 = '';// 记录当前操作时的申请人的会议，答完卷时回调用
		var text = "";
		if(cardview_me.useType=="3") {
			text = zc.label.score;
		}else {
			text = zc.label.vote;
		}
		// 渲染名片区域
		Ext.widget('panel',{
			id:'cardPanel',
			renderTo: 'cardview',
			border:false,
			height:this.pageHeight,
			scrollable:'y',
			//items:[this.cardViewList()],//名片生成
			html:'<div id="personDiv" style="'+(this.pageHeight-15)+'px;margin-top:15px;"></div>',
			buttonAlign:'center',
			buttons:[/*{
					text:'保存',id:'cardviewsave',handler:function(){Ext.Msg.alert('提示信息', '保存成功！');}
				},*/{
			        id:'cardviewsubmit',
					text:'<span style="color:#FFFFFF;font-size:large;line-height:30px;">'+text+'</span>',
					cls:'submitbtn',
					hidden:true,
					width:287,
					height:40,
					border:false,
			        handler:function(){
			        	if(cardview_me.useType=="3") {
			        		//打分的时候通过后台找到需要打分的人A0100的集合，和需要的加密串等
			        		var items = cardview_me.store.data.items;
			        		var map = new HashMap();
			        		map.put("type", "9");//状态
			        		map.put("w0301", items[0].data.w0301);
			        		map.put("queueMap", cardview_me.currentQueueMap);
			        		map.put("reviewlink", items[0].data.type);
			        	    Rpc({functionId:'ZC00003009',async:false,success:function(form,action){
			        	    	var result = Ext.decode(form.responseText);
			        	    	var model = result.model;
			        	    	var object_List = result.object_List;
			        	    	var relation_Id = result.relation_Id;
			        	    	var need_parameter = result.need_parameter;
			        	    	Ext.require('Performance.score.ScoreMain',function(){
			        	    		Ext.create('Performance.score.ScoreMain',{object_attachment:need_parameter,model:model,object_List:object_List,relation_Id:relation_Id,submitCallback:cardview_me.submitBackCallback});
			        	    	});
			            	}},map);
			        	    
			        	}else {
				        	cardview_me.categoriesnummap = cardview_me.getCategoriesnummap();
				    		cardview_me.categoriesmap = cardview_me.getCategoriesmap();
				    		cardview_me.categorieslist = cardview_me.getCategoriesList();
							cardview_me.levellist = cardview_me.getLevelList();
				    		//cardview_me.store.on('load', cardview_me.drowPage, undefined, {single:true});//初始化页面
							//cardview_me.store.on('load', cardview_me.initCheck, undefined, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
				        	cardview_me.store.on('load', cardview_me.submitCheck, undefined, {single:true});//初始化页面
				        	cardview_me.store.load();
			        	}
					}
				},{
			        id:'cardviewrefresh',
					text:'<span style="color:#FFFFFF;font-size:large;line-height:30px;">'+zc.label.refresh+'</span>',
					cls:'submitbtn',
					hidden:true,
					width:287,
					height:40,
					border:false,
			        handler:function(){
			        	this.refreshPage(true);
					},
					scope:this
				}],
				listeners:{
					afterrender:{
						fn:function(){
							this.store.on('load', this.drowPage, undefined, {single:true});//初始化页面
							this.store.on('load', this.initCheck, undefined, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
							this.store.load();
						},
						scope:this
					}
				}
		});
		
		// 重新定位，页面resize后
		Ext.EventManager.onWindowResize(function(w,h){ 
			var task = new Ext.util.DelayedTask(function(){
				this.pageHeight = window.parent.frameElement.clientHeight - 80;
				this.pageWidth = Ext.getBody().getViewSize().width;
				// 同时修改外层的iframe
				window.parent.Ext.getDom('khframe').height = this.pageHeight;
				// 重置名片区域宽和高
				var cardPanel = Ext.getCmp('cardPanel');
				if(cardPanel){
					cardPanel.setHeight(this.pageHeight);
					cardPanel.setWidth(this.pageWidth);
				}
				
				// 重新设置问卷页面
				var qnWin = Ext.getCmp('qnWin');
				if(qnWin){
					qnWin.setHeight(this.pageHeight);
					qnWin.setWidth(this.pageWidth);
				}
				//重置确认框宽和高
				var resultWindow = Ext.getCmp("resultWindow");
				if(resultWindow){
					resultWindow.setWidth(this.pageWidth*0.5);
					resultWindow.setHeight(this.pageHeight*0.7);
					
				}
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
	
	getScoreData:function(clickCateId) {
		var items = cardview_me.store.data.items;
		var scoreGroup = new HashMap();//如果有多个组的时候这里仅传当前分组和批次的代码
		var map = new HashMap();
		if(clickCateId && !Ext.isEmpty(clickCateId)) {
			cardview_me.clickScoreCateId = clickCateId;
			scoreGroup.put(clickCateId,cardview_me.currentQueueMap[clickCateId]);
			map.put("queueMap", scoreGroup);
		}else {
			map.put("queueMap", cardview_me.currentQueueMap);
		}
		map.put("type", "9");//状态
		map.put("w0301", items[0].data.w0301);
		map.put("reviewlink", items[0].data.type);
	    Rpc({functionId:'ZC00003009',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var model = result.model;
	    	var object_List = result.object_List;
	    	var relation_Id = result.relation_Id;
            var need_parameter = result.need_parameter;
	    	Ext.require('Performance.score.ScoreMain',function(){
	    		Ext.create('Performance.score.ScoreMain',{object_attachment:need_parameter,model:model,object_List:object_List,relation_Id:relation_Id,submitCallback:cardview_me.submitBackCallback});
	    	});
    	}},map);
	},
	// 复写样式，不影响总体Css
	createSelfCss:function(){
		Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi{position:relative;width:260px;float:left;border:1px solid #F4F4F4;margin:0px 10px 15px 10px;}","card_css");
		Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-left{width:26px;float:left;}","card_left_css");
		Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-center{position:relative;height:155px;float:left;}","card_center_css");
		Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-center ul{position:absolute;float:left;margin-top:15px;margin-left:90px;}","card_center_ul_css");
		Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-center ul li{font-size:15px;line-height:21px;color:#787878;list-style-type:none;white-space:nowrap; overflow:hidden;}","card_center_li_css");
		Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-center ul li a{color:#22549b;text-decoration:none;}","card_center_a_css");
		Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-center img{border:1px #F4F4F4 solid;width:80px;height:110px;position:absolute;top:15px;left:5px;}","card_center_img_css");
		Ext.util.CSS.createStyleSheet(".hj-wzm-top-yi-right{width:32px;float:right;}","card_right_css");
		
		
		Ext.util.CSS.createStyleSheet("body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,table,tr,td,img,div,dl,dt,dd,span{margin:0;padding:0; border:none;}","card_css");
		Ext.util.CSS.createStyleSheet("body{color:#4C4C4C;font-size:12px;font-family:'宋体';}","card_css");
		Ext.util.CSS.createStyleSheet("ul,ol{list-style-type:none;}","card_css");
		Ext.util.CSS.createStyleSheet("select,input,img{vertical-align:middle;}","card_css");
		Ext.util.CSS.createStyleSheet("a{text-decoration:none;color:#606060;}","card_css");
		Ext.util.CSS.createStyleSheet(".clearit{clear:both; font-size:0;height:0;width:0;padding:0;margin:0;border:0;}","card_css");
		Ext.util.CSS.createStyleSheet(".bh-space{height:10px;clear:both;}","card_css");
		
		Ext.util.CSS.createStyleSheet(".nav-hover{position:absolute;top:15px;left:221px;width:24px;height:24px;z-index:2;}","card_css21");
		//因为矩阵选择题的border被其他样式覆盖，此处定义内联样式，用于显示h矩阵选择题边框  haosl 2017-07-14
		Ext.util.CSS.createStyleSheet("table[style*='border-collapse'][id$='answerTable'] td{border:1px solid #c5c5c5;padding:5px}","table_css");
	},
	submitBackCallback:function() {
		var items = cardview_me.store.data.items;
		var map = new HashMap();
		map.put("type", "10");//审批状态
		if(cardview_me.clickScoreCateId && !Ext.isEmpty(cardview_me.clickScoreCateId)) {
			map.put("categories_id", cardview_me.clickScoreCateId);
		}else {
			map.put("categories_id", items[0].data.categories_id);
		}
		map.put("w0301", items[0].data.w0301);
		map.put("reviewlink", items[0].data.type);
	    Rpc({functionId:'ZC00003009',async:false,success:function(form,action){
    	
	    }},map);
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
	    		var c_level = '';
	    		var items = cardview_me.store.data.items;
	    		for(var i=0; i<items.length; i++){
					var record = items[i];
					
					if(record.data.w0501_safe == w0501 && record.data.w0301_safe==w0301 && record.data.categories_id==categories_id){
						cardview_me.approvalState = record.data.approvalState;
						c_level = record.data.c_level;
					}
				}
	    		cardview_me.categoriesnum_key = categories_id+'_'+c_level;
	    		cardview_me.categoriesnummap = cardview_me.getCategoriesnummap();
	    		cardview_me.store.on('load', cardview_me.checkAgreeNum, undefined, {single:true});//赞成人数校验这个应该可以在之前做校验的，以后可以看看为什么在这里做校验
	    	}
	    	cardview_me.store.load();
    	}},map);
	},
	//对每个分组进行校验
	checkAgreeNum :function(){
		// 差额投票校验
		var msg = '';
		var items = cardview_me.store.data.items;
		var config_num = cardview_me.categoriesnummap[cardview_me.categoriesnum_key].split('_')[0];
		var config_showname = cardview_me.categoriesnummap[cardview_me.categoriesnum_key].split('_')[1];
		var num = cardview_me.categoriesnummap[cardview_me.categoriesnum_key].split('_')[2];//别的分组已经投票的人数已经投票的人数
		if(config_num == '')
			return;
		var nameStr = '';
		var agreetext = '';
		for(var i=0; i<items.length; i++){
			var record = items[i];
			var categories_id = record.data.categories_id;
			var c_level = record.data.c_level;
			var w0511 = record.data.w0511;
			agreetext = record.data.agreetext;
			var approvalState = record.data.approvalState;
			if(cardview_me.categories_id==categories_id){
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
			msg += level_name+/*'（'+nameStr+'）'+*/zc.cardview.more+config_num+zc.cardview.doVote + agreetext + zc.cardview.vote;
		}
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
				var f_label = Ext.getCmp('f_'+idpre+cardview_me.w0501);
				if(f_label){
					if(cardview_me.approvalState == '1'){
						f_label.setValue(false);
					}else{
						f_label.setValue(true);
					}
				}
			}else{
				var label = Ext.get('agree_'+cardview_me.w0501);
				if(label){
					label.dom.checked = false; 
				}
				var f_label = Ext.getCmp('f_'+'agree_'+cardview_me.w0501);
				if(f_label){
					f_label.setValue(false);
				}
			}
			if(cardview_me.approvalState == '1'){
				cardview_me.approvalState = '';
			}
			cardview_me.updateCommentRs(cardview_me.approvalState, cardview_me.w0501, cardview_me.w0301, cardview_me.categories_id);
			Ext.showAlert(/*msgpre + */msg);
			return ;
		}
	
	},
	//提交的时候总的校验
	checkAgreeNumForSubmit :function(){
		// 差额投票校验
		var msg = '';
		var items = cardview_me.store.data.items;
		var msg_map = new HashMap();
		for(var p in cardview_me.categoriesnummap){
			var config_categories_id = p.split('_')[0];
			var config_c_level = p.split('_')[1];
			var config_num = cardview_me.categoriesnummap[p].split('_')[0];
			var config_showname = cardview_me.categoriesnummap[p].split('_')[1];
			var num = cardview_me.categoriesnummap[p].split('_')[2];//已经投票的人数
			if(msg == '' && config_num == '')
				continue;
			var nameStr = '';
			var agreetext = '';
			for(var i=0; i<items.length; i++){
				var record = items[i];
				var categories_id = record.data.categories_id;
				var c_level = record.data.c_level;
				var w0511 = record.data.w0511;
				agreetext = record.data.agreetext;
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
				msg = level_name+/*'（'+nameStr+'）'+*/zc.cardview.more+config_num+zc.cardview.doVote+ agreetext + zc.cardview.vote;'<br>';
				msg_map.put(config_categories_id, msg)
			}
		}
		// 按照顺序提示，map顺序是乱的
		msg = '';
		for(var i = 0; i < cardview_me.categorieslist.length; i++) {
			var msg_ = msg_map[cardview_me.categorieslist[i]]
			msg += msg_?msg_:''
		}
		if(!Ext.isEmpty(msg)){
			Ext.showAlert(/*msgpre + */msg);
			return false;
		}
		return true;
	
	},
	// 打开鉴定意见
	questionnaire:function(type, w0539, w0541, w0539_qnid, w0541_qnid, w0501, w0301, expertState, w0501_safe, expertName, subObject, categories_id){
		
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
			Ext.showAlert(zc.cardview.notDraft);
			return ;
		}
		
		cardViewGlobal.w0501 = w0501_safe;// 记录当前操作时的申请人，答完卷时回调用
		cardViewGlobal.w0301 = w0301;// 记录当前操作时的申请人的会议，答完卷时回调用
		cardViewGlobal.categories_id_current_ques = categories_id;
		// 配置问卷的信息
		var suerveyid = "";
		var qnId = "";
		var title = zc.cardview.checkView;
		if(type == "1" || type == "2" || type == "4"){// 评委会、学科组、学院任聘组的问卷配置
			if(w0539 == "" || w0539 == null){
				Ext.showAlert(zc.cardview.notDraft);
				return ;
			}
			suerveyid = w0539;
			qnId = w0539_qnid;
			title = zc.cardview.reviewView;
		} else {// 外部鉴定专家问卷配置
			if(w0541 == "" || w0541 == null){
				Ext.showAlert(zc.cardview.notDraft);
				return ;
			}
			suerveyid = w0541;
			qnId = w0541_qnid;
		}
		if(expertState == "2" || expertState == "3"){//已审
			suerveyid = undefined;//已审不需要配置此项
			
		}else if(Ext.isEmpty(expertState) || expertState == "1" || expertState == "0"){//待审
			qnId = undefined;//待审不需要配置此项
		}
		
		// 请求问卷
		Ext.require("QuestionnaireTemplate.PreviewTemplate",function(){
			var re = Ext.create("QuestionnaireTemplate.PreviewTemplate",{
				//height:757,
				panwidth:cardview_me.pageWidth-100,
				border:true,
				//width:800,
				qnId:qnId,
				suerveyid:suerveyid,
				mainObject:expertName,//调研对象唯一标志
				subObject:subObject,//被调研对象唯一标志
				callback:cardview_me.questionRs
			});
			//document.documentElement.style.overflowY = 'hidden'
			var qnWin = Ext.create('Ext.window.Window', {
				id : 'qnWin',
			    title: title,
				modal: true,
				//width:Ext.getBody().getWidth()*0.9,
				//height:(window.screen.availHeight-150)*0.9,//窗口的高(不含菜单)，150：菜单高度,
				width:cardview_me.pageWidth,
				height:cardview_me.pageHeight,
				border:false,
				autoScroll:true,
				closeAction:'destroy',
				bodyStyle:'background-color:#ffffff;',
				draggable:false,
				scrollable:'y',
			    //padding:'20 100 20 100',
			    layout:{
			    	type: 'vbox',
                    align: 'center'
			    },
			    items: re//,
//		        listeners:{
//			         beforeclose: function(combo, data){
//				    	document.documentElement.style.overflowY = 'auto';
//			         }
//			    }
			}).show();
		});
	},
	// 调查问卷回调，更新专家状态标识
	questionRs:function(state,forwardFlag){
		if(state == "" || state == "0"){
		    Ext.getCmp('qnWin').close();
			return ;
		} else if(state == "1"){//交卷
			var map = new HashMap();
			map.put("type", "2");//专家状态标识
			map.put("state", "2");
			map.put("w0501", cardViewGlobal.w0501);
			map.put("w0301", cardViewGlobal.w0301);
			map.put("categories_id", cardViewGlobal.categories_id_current_ques);
		    Rpc({functionId:'ZC00003011',async:false,success:function(form,action){
		    	
				cardview_me.store.on('load', function(){
					
					// 未评=》已评
					var span = Ext.getDom('span_'+cardViewGlobal.w0501);
					if(span){
						span.innerHTML = zc.cardview.assess;
					}
					// 更新问卷链接
					var ques = Ext.get('ques_'+cardViewGlobal.w0501);
					if(ques){
						var personinfo = this.data.items;
						var info = '';
						for(var i=0; i<personinfo.length; i++){
							var data = personinfo[i].data;
							if(data.w0501_safe == cardViewGlobal.w0501){
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
						var w0301_safe= info.w0301_safe;
						var expertName= info.expertName;
						var subObject= info.subObject;
						var href = "javascript:cardview_me.questionnaire('"+type+"','"+w0539+"','"+w0541+"','"+w0539_qnid+"','"+w0541_qnid+"','"+w0501+"','"+w0301_safe+"','"+expertState+"','"+w0501_safe+"','"+expertName+"','"+subObject+"','"+info.categories_id+"')";
						ques.dom.href = href; 
					}
				},undefined,{single:true});
				
				cardview_me.store.load();
				if('true'==forwardFlag)//xiegh 20170717 add bug:29800
					Ext.getCmp('qnWin').close();
				return ;
		    }},map);
		}
	},
	// 评审材料
	checkfile:function(path, nbasea0100, type, w0536){
		if(this.support_word && w0536 != 'null' && !Ext.isEmpty(w0536)){// 支持WORD模板
			var servletpath = '/servlet/DisplayOleContent?filePath='+w0536+'&bencrypt=true'+'&openflag=true';
			
			var height = this.pageHeight;
			var width = this.pageWidth;
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
			obj.other_param="visible_title=0`visible_toolbar=0`object_id="+nbasea0100+"`taskid_validate="+taskid_validate;//visible_title=0:不需要标题； visible_toolbar=0：不要按钮；object_id：对象nbase+a0100
			obj.callBack_init="cardview_me.showView";
			
			//获取业务模板名称
			var map = new HashMap();
			map.put("tabId", tabid);
			map.put("getconfig", true);//获取材料公示配置信息
			map.put("type", type);//0：公示 1：聘委会 2：学科组 3：同行专家 4：二级单位
		    Rpc({functionId:'ZC00003018',async:false,success:function(){
		    	var result = Ext.decode(arguments[0].responseText);
		    	this.tabName = result.tabName;
		    	var configStr = result.configStr;
		    	if(!Ext.isEmpty(configStr)){
		    		obj.other_param += ('`noshow_pageno='+configStr)
			    }
				// 调用人事异动模板 
				createTemplateForm(obj);
				
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
					queue:cardview_me.queue,
					type:'0',
					useType:cardview_me.useType
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
		var showfile_win = Ext.create('Ext.window.Window', {
				title:cardview_me.tabName,
				id:'reviewfile_cardview_showfile_win',
				layout: 'border',
				modal: true,
				width:cardview_me.pageWidth,
				height:cardview_me.pageHeight,
				border:false,
				autoScroll:false,
				closable:false,
				tools: [{
						xtype:'button',
						text:zc.label.back,
						handler:function(){
							Ext.getCmp('reviewfile_cardview_showfile_win').close();
							if(Ext.util.CSS.getRule(".x-grid-cell-inner"))
			    	    		Ext.util.CSS.updateRule(".x-grid-cell-inner","max-height","");
						},
						scope:this
					}],
			    items: [container]//,
			}).show();
		//人事异动模板展示自适应
		if(showfile_win){
			window.onresize=function(){
				var height =Ext.getBody().getViewSize().height;
				var width =Ext.getBody().getViewSize().width;
				showfile_win.setWidth(width);
				showfile_win.setHeight(height);
			}
		}
	},
	// 对页面数据进行提交
	submitResult:function(){
		
		var items = cardview_me.store.data.items;
		
		var objArr = [];
		for(var i=0; i<items.length; i++) {
			var record = items[i];
			var w0501_safe = record.data.w0501_safe;
			var w0301_safe = record.data.w0301_safe;
			var categories_id = record.data.categories_id;
			
			var m = new HashMap();
			m.put('w0501', w0501_safe);
			m.put('w0301', w0301_safe);
			m.put('categories_id', categories_id);
			objArr.push(m);
		}
		var map = new HashMap();
		map.put("type", '2');//更新“专家状态标识(expert_state)”
		map.put("state", '3');//页面已提交
		map.put("objArr", objArr);
	    Rpc({functionId:'ZC00003011',async:false,success:function(form,action){
	    	
	    	// 【保存】【提交】按钮隐藏，【刷新】显示
			var cardviewsave = Ext.getCmp('cardviewsave');
			var cardviewsubmit = Ext.getCmp('cardviewsubmit');
			var cardviewrefresh = Ext.getCmp('cardviewrefresh');
			if(cardviewsave){
				cardviewsave.hide();
			}
			if(cardviewsubmit){
				cardviewsubmit.hide();
			}
			if(cardviewrefresh && cardview_me.isshowrefresh){
				cardviewrefresh.show();
			}
			
			// 页面上的radio不可用
			var radlioArray = Ext.query('input[type=radio]');
			if(radlioArray.length > 0){
				for(var i=0; i<radlioArray.length; i++){
					var radio = radlioArray[i];
					Ext.getDom(radio.id).disabled = true;
				}
				
				Ext.showAlert(zc.label.submitSuccess, function(){
					/*if(!this.isshowrefresh){
						this.goToLogon();
					}else{*/
						var resultWindow = Ext.getCmp('resultWindow');
						if(resultWindow){
							resultWindow.close();
						}
						cardview_me.store.on('load', cardview_me.initCheck, undefined, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
						cardview_me.store.load();
						//}
				}, cardview_me);
			}
	    }},map);
		
		
	},
	goToLogon:function(){
		window.top.location = '../hcmlogon.html'
	},
	// 更新赞成、反对、弃权选项
	updateRadioState:function(){
		
		//cardview_me.store.on('load', function(){
			var personinfo = cardview_me.store.data.items;
			for(var i=0; i<personinfo.length; i++){
				var info = personinfo[i].data;
				var w0501_safe= info.w0501_safe;
				var approvalState= info.approvalState;
				
				var agreeCheckState = '', disaplayCheckState = '', giveupCheckState = '';
				if(approvalState == 1){
					agreeCheckState = 'checked';
				}else if(approvalState == 2){
					disaplayCheckState = 'checked';
				}else if(approvalState == 3){
					giveupCheckState = 'checked';
				}
				var agree = Ext.get('agree_'+w0501_safe);
				var against = Ext.get('against_'+w0501_safe);
				var abstentions = Ext.get('abstentions_'+w0501_safe);
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
			
		//},undefined,{single:true});
		//cardview_me.store.load();
	},
	// 页面初始化校验
	initCheck:function(){
		var items = cardview_me.store.data.items;
		if(items.length == 0){//没有
			/*if(!cardview_me.isshowrefresh){
				Ext.showAlert(zc.cardview.haveSubmited, function(){
					cardview_me.goToLogon();
				});
			}else {*/
				var cardviewsave = Ext.getCmp('cardviewsave');
				var cardviewsubmit = Ext.getCmp('cardviewsubmit');
				var cardviewrefresh = Ext.getCmp('cardviewrefresh');
				if(cardviewsave){
					cardviewsave.hide();
				}
				if(cardviewsubmit){
					cardviewsubmit.hide();
				}
				if((cardview_me.categorieslist.length == 1 || cardview_me.useType != "3") && cardviewrefresh){
					cardviewrefresh.show();
				}
			//}
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
			
			if(type == 3){//外部专家时，不区分账号类型，全部可以投票 chent 20161017
				onlyready = false;
			}
		}
		
		// 如果都是已提交状态则 ： 1、不显示【保存】【提交】按钮  2、赞成反对弃权不可修改。
		if(isSubmit){
			// 【保存】【提交】按钮隐藏，【刷新】显示
			var cardviewsave = Ext.getCmp('cardviewsave');
			var cardviewsubmit = Ext.getCmp('cardviewsubmit');
			var cardviewrefresh = Ext.getCmp('cardviewrefresh');
			if(cardviewsave){
				cardviewsave.hide();
			}
			if(cardviewsubmit){
				cardviewsubmit.hide();
			}
			if((cardview_me.categorieslist.length == 1 || cardview_me.useType != "3") && cardviewrefresh){
				cardviewrefresh.show();
			}
			// 页面上的radio不可用
			var radlioArray = Ext.query('input[type=radio]');
			if(radlioArray.length > 0){
				for(var i=0; i<radlioArray.length; i++){
					var radio = radlioArray[i];
					Ext.getDom(radio.id).disabled = true;
				}
			}
			return;
		}
		
		// 查看账号时：1、不显示问卷 2、不显示赞成反对弃权 3、不显示投票按钮。
		if(onlyready){
			// 1、不显示问卷 2、不显示赞成反对弃权
			var liArray = Ext.query('li[type=agereeli]');
			if(liArray.length > 0){
				for(var i=0; i<liArray.length; i++){
					var li = liArray[i];
					Ext.getDom(li).style.display = 'none';
				}
			}

			// 3、不显示投票按钮，不显示刷新按钮
			var cardviewsave = Ext.getCmp('cardviewsave');
			var cardviewsubmit = Ext.getCmp('cardviewsubmit');
			var cardviewrefresh = Ext.getCmp('cardviewrefresh');
			if(cardviewsave){
				cardviewsave.hide();
			}
			if(cardviewsubmit){
				cardviewsubmit.hide();
			}
			if(cardviewrefresh){
				cardviewrefresh.hide();
			}
		} else {
			// 显示投票按钮，不显示刷新按钮
			var cardviewsave = Ext.getCmp('cardviewsave');
			var cardviewsubmit = Ext.getCmp('cardviewsubmit');
			var cardviewrefresh = Ext.getCmp('cardviewrefresh');
			if((cardview_me.categorieslist.length == 1 || cardview_me.useType != "3") && cardviewsave){
				cardviewsave.show();
			}
			if((cardview_me.categorieslist.length == 1 || cardview_me.useType != "3") && cardviewsubmit) {
				cardviewsubmit.show();
			}
			if(cardviewrefresh){
				cardviewrefresh.hide();
			}
		}
	},
	// 点击【提交】按钮时校验
	submitCheck:function(){
		// 校验赞成人数
		if(!cardview_me.checkAgreeNumForSubmit()){
			return ;
		}
		
		// 校验提交情况
		var isDone = true;
		var items = cardview_me.store.data.items;
		var msg = '';
		for(var i=0; i<items.length; i++) {
			var record = items[i].data;
			// 校验问卷是否已审 ，如果没有配置，则不校验
			var type = record.type;
			var w0511 = record.w0511;
			/*if(type == '1' || type == '2' || type == '4') {// 评审会专家、学科组专家、学院任聘组
				var w0539 = record.data.w0539;
				var w0539_qnid = record.data.w0539_qnid;
				if(!Ext.isEmpty(w0539) && !Ext.isEmpty(w0539_qnid)) {// 配置了问卷
					var expertState = record.data.expertState;
					if(expertState != '2') {//2：已审
						isDone = false;
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
						isDone = false;
						msg += w0511+',';
						continue ;
					}
				}
			}*/
			
			// 校验赞成、反对、弃权项是否填写
			var approvalState = record.approvalState;
			if(Ext.isEmpty(approvalState)){
				isDone = false;
				msg += w0511+',';
				continue ;
			}
		}
		// 校验后，通过：1、显示最终确认单  2、则更新专家状态标识（expert_state）为已提交（3）
		if(isDone){
			
			var container = Ext.widget('container', {
				layout:{
					type:'vbox'
				},
				scrollable : 'y',
				items:[]
			});
			
			/*if (type == '3') { // 同行专家不进行分组，所以要单独处理 chent 20180130 add
				var personinfo = cardview_me.store.data.items;
				var perContainer = Ext.widget('container', {
							width : '100%',
							layout : {
								type : 'table',
								columns : 2,
								tableAttrs : {
									style : {
										width : '100%'
									}
								},
								tdAttrs : {
									style : {
										width : '50%'
									}
								}
							},
							border : false,
							scrollable : 'y',
							items : []
						});
				for (var i = 0; i < personinfo.length; i++) {
					var info = personinfo[i];

					var fieldcontainer = cardview_me.getSubmitPagefieldContainer(info);
					perContainer.add(fieldcontainer);
				}
			} else {*/
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
					//('+zc.label.queue+(cardview_me.queue?cardview_me.queue:'1')+')批次去掉，现在没有批次的概念
					innerHTMLValue += _categories_name+'： ';
					
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
							innerHTMLValue += zc.label.declare+_level_name+num+zc.label.person+';'/*+'</span>人，最多给<span style="color:#007aff;">'+_level_num+'</span>人投赞成票；'*/;
						}
					}
					innerHTMLValue = innerHTMLValue.substring(0, innerHTMLValue.length-1);
					innerHTMLValue += '。';
					var titlelabel = Ext.widget('label', {
						text:innerHTMLValue,
						width:'100%',
						margin:'0 0 8 5',
						padding:'8 0 8 5',
						style:'font-size:14px;color:#007aff;background-color:#F7F7F7;'
						
					});
					container.add(titlelabel);
		
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
									
										var label = Ext.widget('label', {
											text:level_name,
											width:'100%',
											margin:'0 0 8 5',
											padding:'8 0 8 20',
											style:'font-size:14px;color:#007aff;'
											
										});
										if(!Ext.isEmpty(level_name)){
											container.add(label);
										}
									}
									
									var perContainer = Ext.getCmp('perCon_'+categories_id+c_level);
									if(!perContainer){
										perContainer = Ext.widget('container', {
											id:'perCon_'+categories_id+c_level,
											width:'100%',
											layout:{
												type:'table',
												columns: 2,
												tableAttrs: {
											            style: {
											                width: '100%'
											            }
										        },
										        tdAttrs : {
										        	style: {
										                width: '50%'
										            }
										        }
											},
											border:false,
											scrollable : 'y',
											items:[]
										});
										container.add(perContainer);
									}
									
									var fieldcontainer = cardview_me.getSubmitPagefieldContainer(info);
									perContainer.add(fieldcontainer);
								}
							}
						}
					}
				}
			}

			
			//}
			
			var confirmWindow = Ext.widget('window', {
				id:'resultWindow',
				title:zc.label.confirmResult,
				closeToolText : '',
				width:cardview_me.pageWidth*0.5,
				minWidth:500,
				height:cardview_me.pageHeight*0.7,  
				resizable: false,  
				modal: true,
				border:false,
				bodyStyle: 'background:#ffffff;',
				layout: {
		            type: 'fit'
		        },
		        items:[container],
		        buttonAlign:'center',
		        buttons:[{
		        	id:'okbtn',
		        	text:'<span style="border:0px;font-size:16px;line-height:20px;color:#FFFFFF">'+zc.label.confirm+'</span>',
		        	cls:'okbtn',
		        	width:137,
					height:40,
					border:false,
		        	handler:function(){
		        		cardview_me.submitResult();
		        	}
		        },{
		        	id:'cancelbtn',
		        	text:'<span style="border:0px;font-size:16px;line-height:20px;color:#FFFFFF">'+zc.label.cancel+'</span>',
		        	cls:'cancelbtn',
		        	width:137,
					height:40,
					border:false,
		        	handler:function(){
		        		confirmWindow.close();
		        	}
		        }],
		        listeners:{
		        	close:function(){
		        		cardview_me.updateRadioState();
		        	}
		        }
			}).show();
		} else {
			Ext.showAlert(msg.substring(0, msg.length-1)+zc.cardview.unFinish, function(){
				cardview_me.refreshPage();
			});
		}
	},
	// 生成页面元素
	drowPage:function(){
		var cardviewDiv = Ext.getDom("personDiv");//先移除所有已有数据
		if(cardviewDiv){
			cardviewDiv.innerHTML = '';  
		}
		
		var onlyready = false;// 是不是审查账号
		var items = cardview_me.store.data.items;
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
			
			if(type == 3){//外部专家时，不区分账号类型，全部可以投票 chent 20161017
				onlyready = false;
			}
		}
		//if(!onlyready){//同行专家投票不分组，直接加载人员卡片 chent 20180130 update
			for(var ii=0; ii<cardview_me.categorieslist.length; ii++){
				var categoriesid = cardview_me.categorieslist[ii];
				for(var w in cardview_me.categoriesmap){
					var _config_categories_id = w.split('_')[0];
					var _categories_name = w.split('_')[1];
					var categories = cardview_me.categoriesmap[w]+"";
					var _categories_queue = categories.split('_')[4];
					if(categoriesid != _config_categories_id){
						continue ;
					}
					cardview_me.currentQueueMap.put(_config_categories_id,cardview_me.queue);
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
					innerHTMLValue += '<span style="color:#007aff;">'+_categories_name+'</span>';
					
					var levelArray = cardview_me.categoriesmap[w];
					/*for(var ll=0; ll<cardview_me.levellist.length; ll++){
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
							
							innerHTMLValue += '<span style="color:#007aff;">申报'+_level_name+_level_person_num+'人；'+'</span>人，最多给<span style="color:#007aff;">'+_level_num+'</span>人投赞成票；';
						}
					}
					innerHTMLValue = innerHTMLValue.substring(0, innerHTMLValue.length-1);
					innerHTMLValue += '。';
					*/
					if(parseInt(_categories_queue) > 1) {//只有一个批次不显示了
						for(var i = 1; i <= parseInt(_categories_queue); i++) {
							if(i == 1) {
								innerHTMLValue += '：';
							}
							innerHTMLValue += "<div style='position:relative;width:20px;height:20px;display:inline;padding-left:5px;'>";
							if(cardview_me.queue == i) {
								innerHTMLValue += "<img id='img_"+_config_categories_id + "_"+i+"' src='/images/new_module/checked.png'>";
							}else{
								innerHTMLValue += "<img id='img_"+_config_categories_id + "_"+i+"' src='/images/new_module/unchecked.png'>";
							}
							innerHTMLValue += "<span style='padding-left:5px;color:#9a9a9a;position:absolute;bottom:8px;left:5px;width:10px;height:10px;cursor:pointer;color:#ffffff;' onclick='javascript:cardview_me.findOtherQueue(\"" + _config_categories_id + "\",\"" + i + "\")'>" + i +
								"</span></div>";
						}
					}
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
										innerHTMLValue += '<span style="color:#007aff;">&nbsp;&nbsp;'+level_name+'</span>'+'';
										var div = document.createElement("div");
										div.innerHTML = innerHTMLValue;
										div.style.clear = 'both';
										div.style.border = 'none';
										div.style.margin = '0px 0 15px 5px';
										div.style.padding = '0px 0 0px 20px';
										div.style.fontSize = '14px';
										div.style.color = '#333';
										//div.style.backgroundColor = '#F7F7F7';
										if(!Ext.isEmpty(level_name)){
											cardviewDiv.appendChild(div);
										}
										
										var div = document.createElement("div");
										div.id = categories_id;//在每个申报人分组外面包一层div，这样点击切换批次的时候可以直接刷新
										cardviewDiv.appendChild(div);
										var cardviewChidlDiv = Ext.getDom(categories_id);
									}
									var card = cardview_me.createCard(info);
									
									cardviewChidlDiv.appendChild(card);
									
								}
							}
						}
					}
					//cardviewDiv.innerHTML = "</div>";
					//只有在是打分并且是多个分组的时候才显示
					if(cardview_me.categorieslist.length > 1 && cardview_me.useType=="3") {
						var htmlButton="<div class='submitbtn' onclick='javascript:cardview_me.getScoreData(\""+categoriesid+"\");' style='clear:both;margin:0 auto;cursor:pointer;margin-bottom:10px;text-align: center;width:287px;height:40px;'>";
						htmlButton+='<span style="color:#FFFFFF;font-size:large;line-height:30px;">'+zc.label.score+'</span>';
						htmlButton+='</div>';
						var div = document.createElement("div");
						div.innerHTML = htmlButton;
						cardviewDiv.appendChild(div);
					}
				}
			}
		//} 
	},
	// 生成人员卡片
	createCard:function(info){
		var expertState= info.expertState;
		var w0541= info.w0541;
		var ins_id= info.ins_id;
		var taskid= info.taskid;
		var type= info.type;
		var w0301_safe= info.w0301_safe;
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
		var w0515= info.w0515;
		var approvalState= info.approvalState;
		var questionid= info.questionid;
		var giveuptext= info.giveuptext;
		var w0501= info.w0501;
		var a0100= info.a0100;
		var imgpath= info.imgpath;
		var pre= info.pre;
		var agreetext= info.agreetext;
		var batch_task= info.batch_task;
		var tp_id= info.tp_id;
		var sp_batch= info. sp_batch;
		var w0513itemtext= info.w0513itemtext;
		var w0515itemtext= info.w0515itemtext;
		
		var categories_id= info.categories_id;
		var c_level= info.c_level;
		var html = '';
		html+='<div class="clear"></div>';
		html+='<div class="hj-wzm-top-yi-left"></div>';
		html+='<div class="hj-wzm-top-yi-center">';
			html+='<ul>';
				html+='<li style="margin:0 0 5px 0;"><label style="font-weight:bold;font-size:large;">'+w0511+'</label></li>';
				html+='<li style="width:170px;white-space:nowrap; overflow:hidden;text-overflow:ellipsis;">'+w0513itemtext+'：<label style="font-weight:bold;" title='+w0513+' >'+w0513+'</label></li>';
				html+='<li style="width:170px;white-space:nowrap; overflow:hidden;text-overflow:ellipsis;">'+w0515itemtext+'：<label style="font-weight:bold;" title='+w0515+' >'+w0515+'</label></li>';
				if(type == 1 || type == 2 || type == 4){
					html+='<li>'+zc.label.applyfile+'：';
						if(!Ext.isEmpty(w0535) || !Ext.isEmpty(w0536)){
							html+="<a style='font-size:15px;' href=javascript:cardview_me.checkfile('"+w0535+"','"+nbasea0100_safe+"','"+type+"','"+w0536+"');>评审材料</a>";
						}
					html+="</li>";
				} else if(type == 3){
						if(!Ext.isEmpty(w0537)){
							html+='<li>'+zc.label.checkfile+'：';
							html+="<a style='font-size:15px;' href=javascript:cardview_me.checkfile('"+w0537+"','"+nbasea0100_safe+"','"+type+"');>"+zc.label.proficientcheckfile+"</a>";
						}else if(!Ext.isEmpty(w0535) || !Ext.isEmpty(w0536)) {
							html+='<li>'+zc.label.applyfile+'：';
							html+="<a style='font-size:15px;' href=javascript:cardview_me.checkfile('"+w0535+"','"+nbasea0100_safe+"','"+type+"','"+w0536+"');>评审材料</a>";
						}else {
							html+='<li>'+zc.label.checkfile+'：';
						}
					html+="</li>";
				}
				var text = "";
				if(expertState == 2 || expertState == 3){//页面已提交、问卷已答
					text = '(已评)';
				} else {
					text = '(未评)';
				}
				
				html+="<li type=agereeli>";
				if(type == 1 || type == 2 || type == 4){
					if(!Ext.isEmpty(w0539) && !Ext.isEmpty(w0539_qnid)){
						html+=zc.label.proficientcomment+"：";
						html+="<a id='ques_"+w0501_safe+"' style='font-size:15px;' href=javascript:cardview_me.questionnaire('"+type+"','"+w0539+"','"+w0541+"','"+w0539_qnid+"','"+w0541_qnid+"','"+w0501+"','"+w0301_safe+"','"+expertState+"','"+w0501_safe+"','"+expertName+"','"+subObject+"','"+categories_id+"');>评审意见</a><span style='font-size:12px;' id='span_"+w0501_safe+"'>"+text+"</span>";
					}else {
						html+="&nbsp;";
					}
				}else if(type == 3){
					if(!Ext.isEmpty(w0541) && !Ext.isEmpty(w0541_qnid)){
						html+=zc.label.proficientcomment+"：";
						html+="<a id='ques_"+w0501_safe+"' style='font-size:15px;' href=javascript:cardview_me.questionnaire('"+type+"','"+w0539+"','"+w0541+"','"+w0539_qnid+"','"+w0541_qnid+"','"+w0501+"','"+w0301_safe+"','"+expertState+"','"+w0501_safe+"','"+expertName+"','"+subObject+"','"+categories_id+"');>"+zc.label.checkcomment+"</a><span style='font-size:12px;' id='span_"+w0501_safe+"'>"+text+"</span>";
					}else {
						html+="&nbsp;";
					}
				}
				html+='</li>';
				
				if(cardview_me.useType == '2') {
					var left = '0',marginleft='0';
					var agreetextLen = agreetext.length;
					var disagreetextLen = disagreetext.length;
					var giveuptextLen = giveuptext.length;
					if(agreetextLen + disagreetextLen + giveuptextLen >= 12){
						left = '-80';
					} else {
						left = '-50';
					}
					
					var agreeCheckState = '', disaplayCheckState = '', giveupCheckState = '';
					if(approvalState == 1){
						agreeCheckState = 'checked=checked';
					}else if(approvalState == 2){
						disaplayCheckState = 'checked=checked';
					}else if(approvalState == 3){
						giveupCheckState = 'checked=checked';
					}
					
					var disabled = expertState==3?'disabled':'';//可能二级单位有一个组，没有结束，又重新新建一个组，这样会导致判断出这个账号不是结束状态，导致已提交的还能选择
					html+='<li type="agereeli" style="position:absolute; left:'+left+'px;">';
						html+='<label for="agree_'+w0501_safe+'"><input id="agree_'+w0501_safe+'" '+disabled+' name="commentRadio_'+w0501_safe+'" type="radio" value="1" '+agreeCheckState+' onclick=cardview_me.updateCommentRs(this.value,"'+w0501_safe+'","'+w0301_safe+'","'+categories_id+'");><span style="font-size:15px;position:relative;top:2px;">'+agreetext+'</span></input></label>';
						html+='<label style="margin-left:'+marginleft+'px;" for="against_'+w0501_safe+'"><input id="against_'+w0501_safe+'" '+disabled+' name="commentRadio_'+w0501_safe+'" type="radio" value="2" '+disaplayCheckState+' onclick=cardview_me.updateCommentRs(this.value,"'+w0501_safe+'","'+w0301_safe+'","'+categories_id+'");><span style="font-size:15px;position:relative;top:2px;">'+disagreetext+'</span></input></label>';
						html+='<label style="margin-left:'+marginleft+'px;" for="abstentions_'+w0501_safe+'"><input id="abstentions_'+w0501_safe+'" '+disabled+' name="commentRadio_'+w0501_safe+'" type="radio" value="3" '+giveupCheckState+' onclick=cardview_me.updateCommentRs(this.value,"'+w0501_safe+'","'+w0301_safe+'","'+categories_id+'");><span style="font-size:15px;position:relative;top:2px;">'+giveuptext+'</span></input></label>';
							
					html+='</li>';
				}
					
			html+='</ul>';
			html+='<img src='+imgpath+'>';
		html+='</div>';
		html+='<div class="hj-wzm-top-yi-right"></div>';
		
		var div = document.createElement("div");
		div.className = "hj-wzm-top-yi";
		div.innerHTML = html;
		
		return div;
		/*var cardviewDiv = Ext.getDom("personDiv");
		cardviewDiv.appendChild(div);*/
	},
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
	checkIsHaveNewData : function(){
		var isExist = false;
		
		var map = new HashMap();
		map.put("type", '2');
		map.put("useType",cardview_me.useType);
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	isExist = result.isexist;
	    },scope:this},map);
	    
	    return isExist;
	},
	getCategoriesnummap : function(){
		var categoriesnummap = '';
		
		var map = new HashMap();
		map.put("queue",cardview_me.queue);
		map.put("type", '4');
		map.put("useType",cardview_me.useType);
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	categoriesnummap = result.categoriesnummap;
	    }},map);
	    
	    return categoriesnummap;
	},
	getCategoriesmap : function(){
		var categoriesmap = '';
		
		var map = new HashMap();
		map.put("queue",cardview_me.queue);
		map.put("type", '5');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	categoriesmap = result.categoriesmap;
	    }},map);
	    
	    return categoriesmap;
	},
	getCategoriesList : function(){
		var categorieslist = '';
		
		var map = new HashMap();
		map.put("useType",cardview_me.useType);
		map.put("type", '6');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	categorieslist = result.categorieslist;
	    }},map);
	    
	    return categorieslist;
	},
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
	queueMap : function(){
		var levellist = '';
		var map = new HashMap();
		map.put("type", '8');
	    Rpc({functionId:'ZC00003009',async:false,success:function(){
	    	var result = Ext.decode(arguments[0].responseText);
	    	queueMap = result.queueMap;
	    	cardview_me.approvalCount = result.approvalCount;
	    }},map);
	    
	    return queueMap;
	},
	refreshPage:function(isShowMsg){
		if(this.checkIsHaveNewData()){
    		this.categoriesnummap = this.getCategoriesnummap();
    		this.categoriesmap = this.getCategoriesmap();
    		this.categorieslist = this.getCategoriesList();
			this.levellist = this.getLevelList();
    		this.store.on('load', this.drowPage, this, {single:true});//初始化页面
			this.store.on('load', this.initCheck, undefined, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
			this.store.load();
    	} else {
    		if(!!isShowMsg){
    			Ext.showAlert(zc.cardview.haveNotNew);
    		}
    	}
	},
	getSubmitPagefieldContainer : function(record){
		var w0511 = record.data.w0511;
		var agreetext = record.data.agreetext;
		var disagreetext = record.data.disagreetext;
		var giveuptext = record.data.giveuptext;
		var approvalState = record.data.approvalState;
		var expertState = record.data.expertState;
		var w0501 = record.data.w0501;
		var w0301 = record.data.w0301;
		var w0501 = record.data.w0501;
		var w0301_safe = record.data.w0301_safe;
		var w0501_safe = record.data.w0501_safe;
		var categories_id = record.data.categories_id;
		var c_level = record.data.c_level;
	
		return  Ext.widget('container', {
            style:'margin:0 auto;',
            margin:'0 0 0 10',
            height:55,
			layout:{
            	type:'vbox',
            	align:'left'
            },
            items : [{
				xtype : 'label',
				text : w0511 + "",
				width : 150,
				style : {
					fontSize : '16px'
				}
					// padding:'3 10 0 0'
				}, {
				xtype : 'form',
				border:false,
				items : [{
					xtype : 'fieldcontainer',
					defaultType : 'radio',
					layout : {
						type : 'hbox'
					},
					border : 1,
					items : [{
						xtype : 'radio',
						id : 'f_agree_' + w0501_safe,
						boxLabel : '<span style="font-size:14px;position:relative;top:-2px;">'
								+ agreetext + '</span>',
						inputValue : '1',
						disabled : expertState == '3' ? true : false,
						checked : approvalState == '1' ? true : false,
						padding : '0 20 0 0',
						listeners : {
							change : function() {
								if (!arguments[1]) {
									return;
								}
								var hiddenfieldArray = Ext
										.getCmp(arguments[0].id)
										.up('container').query('hiddenfield');
								var w0501 = hiddenfieldArray[0].getValue();
								var w0301 = hiddenfieldArray[1].getValue();
								var categories_id = hiddenfieldArray[2]
										.getValue();
								cardview_me.updateCommentRs('1', w0501_safe, w0301_safe,
										categories_id);
							}
						}
					}, {
						xtype : 'radio',
						id : 'f_against_' + w0501_safe,
						boxLabel : '<span style="font-size:14px;position:relative;top:-2px;">'
								+ disagreetext + '</span>',
						inputValue : '2',
						disabled : expertState == '3' ? true : false,
						checked : approvalState == '2' ? true : false,
						padding : '0 20 0 0',
						listeners : {
							change : function() {
								if (!arguments[1]) {
									return;
								}
								var hiddenfieldArray = Ext
										.getCmp(arguments[0].id)
										.up('container').query('hiddenfield');
								var w0501 = hiddenfieldArray[0].getValue();
								var w0301 = hiddenfieldArray[1].getValue();
								var categories_id = hiddenfieldArray[2]
										.getValue();
								cardview_me.updateCommentRs('2', w0501_safe, w0301_safe,
										categories_id);
							}
						}
					}, {
						xtype : 'radio',
						id : 'f_abstentions_' + w0501_safe,
						boxLabel : '<span style="font-size:14px;position:relative;top:-2px;">'
								+ giveuptext + '</span>',
						inputValue : '3',
						disabled : expertState == '3' ? true : false,
						checked : approvalState == '3' ? true : false,
						listeners : {
							change : function() {
								if (!arguments[1]) {
									return;
								}
								var hiddenfieldArray = Ext
										.getCmp(arguments[0].id)
										.up('container').query('hiddenfield');
								var w0501 = hiddenfieldArray[0].getValue();
								var w0301 = hiddenfieldArray[1].getValue();
								var categories_id = hiddenfieldArray[2]
										.getValue();
								cardview_me.updateCommentRs('3', w0501_safe, w0301_safe,
										categories_id);
							}
						}
					}, {
						xtype : 'hiddenfield',
						value : w0501
					}, {
						xtype : 'hiddenfield',
						value : w0301
					}, {
						xtype : 'hiddenfield',
						value : categories_id
					}]
				}]
			}]
		});
	},
	findOtherQueue : function(categories_id,queue) {
		var map = new HashMap();
		map.put("categories_id", categories_id);
		map.put("queue",queue);
		map.put("type","0");
		map.put("useType",cardview_me.useType);
	    Rpc({functionId:'ZC00003009',async:false,success:function(form){
	    	var result = Ext.decode(form.responseText);
	    	var personinfo = result.personinfo;
	    	Ext.getDom(categories_id).innerHTML = "";//将当前页面置空，
	    	var currentCardViewDiv = Ext.getDom(categories_id);
	    	var oldQueue = cardview_me.currentQueueMap[categories_id];
	    	
	    	cardview_me.currentQueueMap.put(categories_id,queue);
	    	for(var i=0; i<personinfo.length; i++){
				var info = personinfo[i];
				var card = cardview_me.createCard(info);
				
				currentCardViewDiv.appendChild(card);
			}
			if(oldQueue != queue) {
				var newImgId = "img_" + categories_id + "_" + queue;
				var oldImgId = "img_" + categories_id + "_" + oldQueue;
				//标签替换
				Ext.get(newImgId).dom.src = '/images/new_module/checked.png';
				Ext.get(oldImgId).dom.src = '/images/new_module/unchecked.png';
			}
	    	cardview_me.queue = queue;
	    	cardview_me.store = this.getCardViewStore();
	    	cardview_me.store.on('load', this.initCheck, undefined, {single:true});// 页面初始化时判断是否显示【保存】【提交】按钮、页面是否可操作
	    },scope:this},map);
	}
});