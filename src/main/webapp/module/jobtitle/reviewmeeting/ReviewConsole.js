/**
 * 职称评审_投票评分控制台
 * @createtime 
 * 
 * */
Ext.define('ReviewMeetingURL.ReviewConsole', {
	requires:["ReviewMeetingURL.CodeSelectPicker"],
	w0301_e:'',
	review_links:'',
	isFinished : false,//当前环节是否结束，结束了只能查看
	constructor : function(config) {// 构造方法
		jobtitle_reviewconsole = this;
		this.w0301_e = config.w0301_e;
		this.review_links = config.review_links;//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
		this.isFinished = config.isFinished;
		this.endSegment = config.endSegment;
		this.evaluationType = config.evaluationType;//1:投票  2：评分
		//创建修改评审条件，只显示删除按钮，不显示进度，状态，名额列（二级单位和评委会不显示评审人列）//2：发起评审正常显示
		this.enterType = config.enterType;//是从什么入口进来的，1：创建修改评审会议条件，2：发起评审界面
		this.userType = config.userType;//是从什么账号类型1：随机，2：选择人员
		this.queue = 0;
		this.updateRandomValue = new HashMap();//仅用于保存随机人数的时候修改文本框<cate_id,人数>
	},
	getTableConfig : function() {
		var map = new HashMap();
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
		map.put("evaluationType",jobtitle_reviewconsole.evaluationType);
		map.put("enterType",jobtitle_reviewconsole.enterType);
		map.put("userType",jobtitle_reviewconsole.userType);
		map.put("isFinished",jobtitle_reviewconsole.isFinished);
		map.put("screenWidth",Ext.getBody().getViewSize().width);
		Rpc({functionId : 'ZC00002315',async : false,success : jobtitle_reviewconsole.showTableGrid,scope:jobtitle_reviewconsole}, map);
		
		return this.mainPanel;
		
	},
	showTableGrid:function(form){
		var responseText = Ext.decode(form.responseText);
		var flag=responseText.succeed;
		if(flag==true){
			if(!jobtitle_reviewconsole.isFinished)//如果是true说明是从查看按钮过来的，如果不是是发起评审，这样根据权限判断
				jobtitle_reviewconsole.isFinished = !responseText.readOnlyOperate;//如果没有会议安排权限，则只能看，有就能进行操作
			jobtitle_reviewconsole.personmap = responseText.personmap;
			jobtitle_reviewconsole.categoriesmap = responseText.categoriesmap;
			jobtitle_reviewconsole.randomCountMap = responseText.randomCountMap;//获取随机账号的拼接，<加密（categories_id）,数量>
			jobtitle_reviewconsole.groupMap = responseText.groupMap;//<group_name, group_id+"_"+count>所有的，用于展示下拉框选择
			jobtitle_reviewconsole.cateIdGroupIdMap = responseText.cateIdGroupIdMap;//<categories_id,group_id>当前分组对应的group_id
			jobtitle_reviewconsole.w0575codesetid = responseText.w0575codesetid;
			jobtitle_reviewconsole.ctrl_param = responseText.ctrl_param;
			jobtitle_reviewconsole.meettingName = responseText.meettingName;
			jobtitle_reviewconsole.support_word = responseText.support_word;//是否支持导出word
			jobtitle_reviewconsole.intervalsArray = new Array();
			jobtitle_reviewconsole.positionPersonMap = new HashMap();//每个人的位置key:categories_id_e，value:Map
			var conditions = responseText.tableConfig;
			var obj = Ext.decode(conditions);
			obj.beforeBuildComp = function(grid) {
				grid.tableConfig.viewConfig.plugins = {
					ptype: 'gridviewdragdrop',
					dragText: ''
				};
				//添加拖拽事件
				grid.tableConfig.viewConfig.listeners = {
					beforedrop: jobtitle_reviewconsole.dropRecord,
					mousedown: {
			        	element: 'el', 
			        	fn: function(a, o){ jobtitle_reviewconsole.hiddenAllTable(false); }
			        },
				}
			}
			
			var tableGrid = new BuildTableObj(obj);
			this.mainPanel = tableGrid.getMainPanel();
			tableGrid.tablePanel.findPlugin('cellediting').on("edit",function(edit,e){
				if(e.value!=e.originalValue)
				{
					if(e.field == "c_number") {
						jobtitle_reviewconsole.saveInfo(false,true,e.record.data.categories_id_e);
					}else {
						jobtitle_reviewconsole.saveInfo(false);
					}
		    	}	
				e.record.commit();
			});
			window.setInterval(function(){//30秒后台请求一次，修改投票数
				var oldCate_id = '';
				var map = new HashMap();
				map.put("opt", '20');
				map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
				map.put("review_links", jobtitle_reviewconsole.review_links);
				map.put("evaluationType", jobtitle_reviewconsole.evaluationType);
				Rpc({functionId : 'ZC00002316',async:false,success:function(form){
					var personVoteDataMap = Ext.decode(form.responseText).personVoteDataMap;
					jobtitle_reviewconsole.categoriesmap = Ext.decode(form.responseText).categoriesmap;
					for(var key in personVoteDataMap) {//<w0501,categoriesid_submitnum_expertnum_expertAlreadyCount>
						var value = personVoteDataMap[key].split("_");
						var categoriesid = value[0]; 
						if(categoriesid != oldCate_id) {//投票的修改每个人的投票赞成数等
							oldCate_id = categoriesid;
							if(Ext.getDom(categoriesid)) {
								Ext.getDom(categoriesid).innerHTML = value[1]+'/'+value[2];
								if(jobtitle_reviewconsole.categoriesmap[categoriesid])
									Ext.getDom(categoriesid).title = jobtitle_reviewconsole.categoriesmap[categoriesid];
							}
						}
						//d_'+categories_id_e+'_'+w0511+'  '('+expert_already_count+'/'+expert_count+')'
						if(jobtitle_reviewconsole.evaluationType == '1' && Ext.getDom('d_'+categoriesid+'_'+key)) {
							Ext.getDom('d_'+categoriesid+'_'+key).innerHTML = '('+value[3]+'/'+value[2]+')';
						}
						
					}
				},scope:this}, map);           
		    }, 15000);
		}else {
			Ext.showAlert(responseText.message);
		}
		
	},
	dropRecord : function(node,data,model,dropPosition,dropHandlers){
		var ori_categories=data.records[0].get("categories_id_e");
		var ori_seq=data.records[0].get('seq');
		var to_categories=model.get('categories_id_e');
		var to_seq=model.get('seq');
		var map = new HashMap();
		map.put("ori_categories",ori_categories);
		map.put("ori_seq",ori_seq);
		map.put("to_seq",to_seq);
		map.put("userType",jobtitle_reviewconsole.userType);
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
		map.put("opt", '24');
	    Rpc({functionId:'ZC00002316',success:function(form,action){
	    	jobtitle_reviewconsole.reloadPersonMap();
			jobtitle_reviewconsole.loadStore();	
	    }}, map);
	},
	// 同步申报人员分类表（zc_personnel_categories）表
	asyncTableCategories : function(){
		var map = new HashMap();
		map.put("opt", '12');
		Rpc({functionId : 'ZC00002316',async:false,success:function(form){
			var errorcode = Ext.decode(form.responseText).errorcode;
			if(errorcode == 1){
				// 留出同步失败的接口，先不提示
			}
		},scope:this}, map);
	},
	// 应选人数设置
	setPersonNum : function(ids){
		var map = new HashMap();
		map.put("opt", '5');
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
		map.put("ids", ids);
		Rpc({functionId : 'ZC00002316',async:false,success:function(form){
			var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
			if(errorcode == 0){
				jobtitle_reviewconsole.getTableConfig();
			} else {
				// 留出失败的接口，先不提示
			}
		},scope:this}, map);
	},
	// 新建分组
	createCategorie:function(){
		jobtitle_reviewconsole.createCategorie_after();
		jobtitle_reviewconsole.saveInfo(false);
	},
	createCategorie_after : function() {
		var map = new HashMap();
		map.put("opt", "2");
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
		map.put("userType",jobtitle_reviewconsole.userType);
		map.put("categories_name", '');
		Rpc({
			functionId : 'ZC00002316',
			async : false,
			success : function(form) {
				var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
				if (errorcode == 0) {
					jobtitle_reviewconsole.loadStore(-1);
				}
			},
			scope : this
		}, map);
		
	},
	// 保存
	saveInfo : function(showmsg, checkPersonNum,categories_id_e){
		var store = jobtitle_reviewconsole.getReviewfile_console_dataStore();
	    var updaterecord = [];
	    var recordList = store.data.items;
		//var updateList = store.getUpdatedRecords();//修改过的数据
		for(var i=0;i<recordList.length;i++){
			var record = recordList[i].data;
			// 校验应选人数：应选人数不能大于所选人数、不能小于0
			if(checkPersonNum) {
				if(!Ext.isEmpty(categories_id_e) && categories_id_e==record.categories_id_e) {
					var msg = jobtitle_reviewconsole.checkPersonNum(record);
					if(!Ext.isEmpty(msg)){
						jobtitle_reviewconsole.loadStore();
						Ext.showAlert(msg);
						return ;
					}
				}
			}
			
			updaterecord.push(record);
		}
    	var map = new HashMap();
		map.put("opt", "3");
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
        map.put("savedata",updaterecord);
        map.put("userType",jobtitle_reviewconsole.userType);
        Rpc({functionId:'ZC00002316',sync:false,scope:this,success:function(form){
				var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
				if (errorcode == 0) {
					jobtitle_reviewconsole.reloadPersonMap();
					//jobtitle_reviewconsole.loadStore();
					if(showmsg) {
						Ext.showAlert(zc.msg.savesuccess);
					}
				}else {
					if(showmsg) {
						Ext.showAlert(zc.msg.savedefaul);
					}
				}
			
		}},map);
	},
	// 刷新
	loadStore : function(y){
		var tablePanel = Ext.getCmp('jobtitle_reviewfile_console_'+jobtitle_reviewconsole.review_links+'_tablePanel');
		var scrollY = tablePanel.getView().getScrollY();
		if(!Ext.isEmpty(y)){
			scrollY = y;
		}
		
		var store = jobtitle_reviewconsole.getReviewfile_console_dataStore();
		store.on('load', function(){
			tablePanel.getView().setScrollY(scrollY);
		}, this, {single:true});
		store.load();
	},
	// 添加分组-自定义校验
	validateCategorieName : function(){
		var flag = true;

		var inputName = this.getValue();
		var store = jobtitle_reviewconsole.getReviewfile_console_dataStore();
		var records = store.data.items;
		for(var i=0; i<records.length; i++){
			var name = records[i].data.name;
			if(name == inputName){
				flag = zc.reviewconsole.nameRepeatedUse;
				break;
			}
		}
		return flag;
	},
	// 申报人
	renderperson : function(value ,metaData){
		//metaData.tdStyle = 'height:100%;';
		var categories_id_e = arguments[2].data.categories_id_e;
		var approval_state = arguments[2].data.approval_state;
		var expert_count = arguments[2].data.expertnum;
		var expert_already_count = arguments[2].data.expert_already_count;
		var tablePanelColumns = Ext.getCmp('jobtitle_reviewfile_console_'+jobtitle_reviewconsole.review_links+'_tablePanel').columns;
		var c_level = tablePanelColumns[arguments[4]].dataIndex.split('_')[1];
		var key = categories_id_e+"_"+c_level;
		//一行能显示多少人
		var num_row = Math.round(tablePanelColumns[arguments[4]].cellWidth/85) - 1;
		jobtitle_reviewconsole.heightMap = new HashMap();//仅有renderperson和createPerson方法使用，动态给div赋予height(key:categories_id_e_queue_num,value:height)
		// 人员显示
		var el = document.createElement('div');
		var html = '<div style="float:left;padding-left:50px;width:98%;" id="' + categories_id_e + '_queue_1">';
			html += jobtitle_reviewconsole.createPerson(key, approval_state,categories_id_e,c_level,expert_count, num_row, categories_id_e + "_queue_1");
			
		if(approval_state == '1' || approval_state == '2' ||approval_state == '4' || jobtitle_reviewconsole.isFinished){//执行中、结束状态，不能再加人
			html += '</div>';
			el.innerHTML = html;
			for(var i = 0; i < el.childNodes.length; i++) {
				var id_temp = el.childNodes[i].id;
				if(id_temp != "" && jobtitle_reviewconsole.heightMap[id_temp]) {
					el.childNodes[i].style.height = jobtitle_reviewconsole.heightMap[id_temp];
				}
			}
			return el.innerHTML;
		}
		var iconStylePosition = "";
		if(jobtitle_reviewconsole.personmap[key]) {
			iconStylePosition = "margin-top:5px;";
		}else {
			iconStylePosition = "margin-left:12px;";
		}
		
		//html += jobtitle_reviewconsole.queue == 0?'<span style="float:left;padding-top:30px;margin-left:-50px;width:50px;height:50px;text-align:center;font-size:15px;">'+1+'、</span>':'';
		html += '<table style="border:none;float:left;margin:5px 0 0 0;position: absolute;top:' + jobtitle_reviewconsole.finally_top + 'px;left:' + jobtitle_reviewconsole.finally_left + 'px;"><tr><td>';
			html += "<a id='a_"+categories_id_e+"_"+c_level+"' onmousedown='javascript:jobtitle_reviewconsole.selectperson(\""+categories_id_e+"\",\""+c_level+"\","+(jobtitle_reviewconsole.queue == 0?1:jobtitle_reviewconsole.queue)+");'>" +
						"<img style='width:44px;margin-left:12px;height:44px;cursor:pointer;" + iconStylePosition + "' src='/images/new_module/nocycleadd.png' border=0>" +
					"</a>";
			html += '</td></tr></table>';
		html += '</div>';
		
		if(jobtitle_reviewconsole.queue == 0) {//如果分组里面没有人员，则就在这return
			el.innerHTML = html;
			el.childNodes[0].style.height = "55px";
			return el.innerHTML;
		}
		
		/*html += '<div style="float:left;padding:12px 0 0 50px;width:98%;">';
			html += '<table style="border:none;float:left;margin:5px 0 0 0;"><tr><td>';
			html += "<a id='a_"+categories_id_e+"_"+c_level+"' onclick='javascript:jobtitle_reviewconsole.selectperson(\""+categories_id_e+"\",\""+c_level+"\","+(parseInt(jobtitle_reviewconsole.queue)+1)+");'>" +
						"<img style='width:44px;height:44px;margin-left:12px;cursor:pointer;' src='/images/new_module/nocycleadd.png' border=0>" +
					"</a>";
			html += '</td></tr></table>';
		html += '</div></div>';*/
		el.innerHTML = html;
		//动态赋予高度
		for(var i = 0; i < el.childNodes.length; i++) {
			var id_temp = el.childNodes[i].id;
			if(id_temp != "" && jobtitle_reviewconsole.heightMap[id_temp]) {
				el.childNodes[i].style.height = jobtitle_reviewconsole.heightMap[id_temp];
			}
		}
		return el.innerHTML;
	},
	// 评审人
	reviewPerson : function(value ,metaData){
		//metaData.tdStyle = 'height:100%;';
		var categories_id_e = arguments[2].data.categories_id_e;
		var approval_state = arguments[2].data.approval_state;
		var expert_count = arguments[2].data.expertnum;
		var expert_already_count = arguments[2].data.expert_already_count;
		var tablePanelColumns = Ext.getCmp('jobtitle_reviewfile_console_'+jobtitle_reviewconsole.review_links+'_tablePanel').columns;
		var c_level = tablePanelColumns[arguments[4]].dataIndex.split('_')[1];
		var key = categories_id_e+"_"+c_level;
		var selectId = "h_"+categories_id_e;
		var countSpanId = "b_"+categories_id_e+"_"+c_level;
		//选择需要显示的，找到对应的数量，一起拼接
		var htmlTable = "";
		var style = "";
		var readonly = "1";
		var count = 0;//当前需要显示的数量
		if(approval_state == '1' || approval_state == '2' || approval_state == '4' || jobtitle_reviewconsole.isFinished){//进行中/结束时，不能修改
			var group_name = "";
			
			for(var keyV in jobtitle_reviewconsole.groupMap) {
				var group_id = jobtitle_reviewconsole.groupMap[keyV].split("_")[0];
				if(group_id == jobtitle_reviewconsole.cateIdGroupIdMap[categories_id_e]) {
					count = jobtitle_reviewconsole.groupMap[keyV].split("_")[1];
					group_name = keyV;
				}
			}
			htmlTable += "<span style='text-align:center;font-size:13px;vertical-align:middle;'>"+group_name+"</span>";
			style = "float:left;margin:16px 0 0 90px;";
		}else {
			var width = "100px;";
			var select_width = "94px";
			style = "float:left;margin:11px 0 0 25px;";
			if(Ext.getBody().getViewSize().width < 1100 && Ext.getBody().getViewSize().width > 900) {
				style = "float:left;margin:11px 0 0 5px;";
			}else if(Ext.getBody().getViewSize().width < 900) {
				width = "65px;"
				select_width = "62px";
				style = "float:left;margin:16px 0 0 0;";
			}
			//由于select框和ext的拖拽drag冲突了，在谷歌下select展示不出来，ie下点两次才展示，这里给模拟了一个假的选择框
			//由于这是表格控件的，这里没法写对象，暂时先写个假的下拉框
			var html_tab = "";
			var current_val = "";
			html_tab += "<table id='select_table_" + categories_id_e + "' name='select_table'"+
							" style='min-width:" + width + ";border:1px solid #a8a8a8;position:fixed;display:none;background-color:white;'>";
			html_tab += 	"<tr><td name='select_"+categories_id_e+"' id='' style='min-width:"+select_width+";height:15px;' " +
							" onmouseover='jobtitle_reviewconsole.change(this,\"select_"+categories_id_e+"\")'" +
							" onmousedown='jobtitle_reviewconsole.changeCount(\""+selectId+"\",\""+countSpanId+"\",\""+categories_id_e+"\",this.id)'></td></tr>";
			for(var keyV in jobtitle_reviewconsole.groupMap) {
				var group_id = jobtitle_reviewconsole.groupMap[keyV].split("_")[0];
				var sty = "";
				if(group_id == jobtitle_reviewconsole.cateIdGroupIdMap[categories_id_e]) {//新增的时候为空
					//应该显示的人数和当前
					count = jobtitle_reviewconsole.groupMap[keyV].split("_")[1];
					current_val = keyV;
					sty = "color:white;background-color:rgb(14, 158, 243);";
				}
				html_tab += "<tr><td name='select_"+categories_id_e+"' id='"+jobtitle_reviewconsole.groupMap[keyV]+"' " +
						" style='min-width:"+select_width+";height:15px;font-size:13px;" + sty + "'" +
						" onmouseover='jobtitle_reviewconsole.change(this,\"select_"+categories_id_e+"\")'" +
						" onmousedown='jobtitle_reviewconsole.changeCount(\""+selectId+"\",\""+countSpanId+"\",\""+categories_id_e+"\",this.id)'>" + keyV;//PubFunc.encrypt(group_id)+"_"+count
				
				html_tab += "</td></tr>";
			}
			html_tab += "</table>";
			readonly = "0";
			
			htmlTable += "<span onclick='jobtitle_reviewconsole.showSelect(true, \"select_table_" + categories_id_e + "\");' style='margin-right: -16px;'>" +
							" <input type='text' readonly='true' id='"+selectId+"' " +
								"style='font-size:13px;width:"+width+"vertical-align:middle;cursor:pointer;' value='" + jobtitle_reviewconsole.getSubStr(current_val, 12) + "'/>"+
							" <img src='/images/down_sign.png' style='position: relative;right: 23px;top: 5px;cursor:pointer;' />" +
						 "</span>";
			htmlTable += html_tab;
		}
		htmlTable += "&nbsp;(&nbsp;<span id='"+countSpanId+"'>" + count + "</span>" + zc.label.person + "&nbsp;)";
		var html = "<div ><div style='" + style + "' >"+htmlTable+"</div>";
			html += '<div style="float:right;"><table style="border:none;"><tr><td>';
			//如果是创建会议的时候进来的审批人不能再进行添加，删除了，只能选择不同的学科组//没有启动的也不能有添加
			if(jobtitle_reviewconsole.enterType == '2' && !jobtitle_reviewconsole.isFinished && (approval_state == '0' || approval_state == '3')) {
				// 如果选项为空，不显示增加
				var display_ = Ext.isEmpty(current_val)?"display:none;":"";
				html += "<a style=\"cursor:pointer;text-align:center;\" onmousedown='javascript:jobtitle_reviewconsole.openSubjectsPage(\""+categories_id_e+"\",\""+countSpanId+"\",\""+selectId+"\",\""+readonly+"\");'>" +
						"<img style='width:20px;height:20px;margin:12px 18px 0 0;cursor:pointer;" + display_ + "' src='/images/new_module/dealto_green.gif' border=0 />" +
					"</a>";
			}
			html += '</td></tr></table></div>';
		html += '</div>';
		
		return html;
	},
	
	// 随机人数审批人界面,这个根据不同的表来获得，不用表格控件的，而且页面展示的也是有人数的字
	reviewRandomPerson : function(value ,metaData){
		var categories_id_e = arguments[2].data.categories_id_e;
		var approval_state = arguments[2].data.approval_state;
		var tablePanelColumns = Ext.getCmp('jobtitle_reviewfile_console_'+jobtitle_reviewconsole.review_links+'_tablePanel').columns;
		var c_level = tablePanelColumns[arguments[4]].dataIndex.split('_')[1];
		var randomId = "e_"+categories_id_e;
		if(jobtitle_reviewconsole.randomCountMap[categories_id_e]) {
			randomMapValue = jobtitle_reviewconsole.randomCountMap[categories_id_e]
		}else {
			randomMapValue = 0;
		}
		var html = "";
		if(approval_state == '1' || approval_state == '2' || approval_state=="4" || jobtitle_reviewconsole.isFinished){//启动/结束时，不能修改
			html = "<span style='margin-right:10px;text-align:center;'>" + randomMapValue + "</span>" + zc.label.person;
		}else {
			html = "<input type='text' onchange='javascript:jobtitle_reviewconsole.putChageValueToMap(this.value,\"" + categories_id_e + "\")' id=" + randomId + " style='width:120px;height:25px;margin-right:10px;text-align:center;' value="+randomMapValue+">" + zc.label.person;
		}
		
		return html;
	},
	//将修改的值放到map中，这样后面就可以进行保存
	putChageValueToMap:function(value,categories_id) {
		var map = new HashMap();
		map.put("opt", "3");
		map.put("review_links", jobtitle_reviewconsole.review_links);
		map.put("categories_id",categories_id);
		map.put("value",value);
        Rpc({functionId:'ZC00002316',sync:false,scope:this,success:function(form){
			var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
			if (errorcode == 0) {
				jobtitle_reviewconsole.reloadPersonMap();
				//jobtitle_reviewconsole.loadStore();
			}else {
				Ext.showAlert(zc.msg.savedefaul);
			}
		
		}},map);
	},
	//在选择其他学科组等的时候切换数值
	changeCount:function(selectId,countSpanId,categories_id_e,value) {
		if(value == '') {
			Ext.getDom(countSpanId).innerHTML = '0';
		}else {
			Ext.getDom(countSpanId).innerHTML = value.split("_")[1];
		}
		var map = new HashMap();
		map.put("opt", '19');
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("categories_id_e", categories_id_e);
		map.put("group_id",value == ''?value:value.split("_")[0]);
		map.put("count",value == ''?'0':value.split("_")[1]);
		Rpc({functionId : 'ZC00002316',async : false,success : function(form){
			jobtitle_reviewconsole.getBackSubjectsShow();
		},scope:this}, map);
	},
	//下拉框选择的时候改变颜色和背景
	change:function(obj, name) {
		var select_ = Ext.query('td[name='+name+']');
		for(var i = 0; i < select_.length; i++) {
			select_[i].style.backgroundColor = "white";
			select_[i].style.color = "black";
		}
		obj.style.backgroundColor = "rgb(14, 158, 243)";
		obj.style.color = "white";
	},
	//显示隐藏下拉框
	showSelect:function(flag, categor_id) {
		if(!flag || Ext.getDom(categor_id).style.display == "block") {
			Ext.getDom(categor_id).style.display="none";
		}else{
			jobtitle_reviewconsole.hiddenAllTable(true);
			Ext.getDom(categor_id).style.display="block";
		}
	},
	//鼠标点击的时候隐藏所有的下拉框
	hiddenAllTable:function(flag) {
		var select_ = Ext.query('table[name=select_table]');
		for(var i = 0; i < select_.length; i++) {
			if(select_[i].style.display == "block" && !flag) {
				//因为有onmousedown事件，还有onclick事件，如果在选择的范围内，就可以不触发onmousedown的事件，否则永远触发不了onclick事件
				var div = select_[i];
				var x=event.clientX;
			    var y=event.clientY;
			    var divx1 = div.offsetLeft;
			    var divy1 = div.offsetTop;
			    var divx2 = div.offsetLeft + div.offsetWidth;
			    var divy2 = div.offsetTop + div.offsetHeight;
			    if( x < divx1 || x > divx2 || y < divy1 || y > divy2){
			    	select_[i].style.display = "none";
			    }
			}else {
				select_[i].style.display = "none";
			}
		}
	},
	//打开评审成员页面
	openSubjectsPage:function(categories_id_e,countSpanId,selectId,readonly){
		if(readonly == "1") {//只读
			Ext.require('JobtitleSubjects.SubjectsForMeeting', function(){
				RevewFileGlobal = Ext.create("JobtitleSubjects.SubjectsForMeeting", {type:"vote",w0301:jobtitle_reviewconsole.w0301_e,categoriesid:categories_id_e,readonly:readonly});
			});
		}else if(readonly == "0") {
			jobtitle_reviewconsole.categoriesid = categories_id_e;
			jobtitle_reviewconsole.selectGroupId = jobtitle_reviewconsole.cateIdGroupIdMap[categories_id_e];
			var selectGroupId = document.getElementById(selectId).value.split("_")[0];
			Ext.require('JobtitleSubjects.SubjectsForMeeting', function(){
				RevewFileGlobal = Ext.create("JobtitleSubjects.SubjectsForMeeting", {type:"vote",w0301:jobtitle_reviewconsole.w0301_e,readonly:readonly,categoriesid:categories_id_e,countSpanId:countSpanId,selectGroupId:jobtitle_reviewconsole.selectGroupId,selectId:selectId,returnBackFunc:jobtitle_reviewconsole.returnBackFunc});
			});
		}
	},
	returnBackFunc:function() {
    	var map = new HashMap();
    	map.put("w0301", jobtitle_reviewconsole.w0301);
    	map.put("categoriesid",jobtitle_reviewconsole.categoriesid);
    	map.put("selectGroupId",jobtitle_reviewconsole.selectGroupId);
    	Rpc({functionId:'ZC00002221',async:false,success:jobtitle_reviewconsole.getBackSubjectsShow,scope:this},map);
	},
	getBackSubjectsShow:function() {
		jobtitle_reviewconsole.reloadPersonMap();
		jobtitle_reviewconsole.loadStore();
	},
	selectperson :function(categories_id_e,c_level,queue){
		//jobtitle_reviewconsole.saveInfo(false);
		
		jobtitle_reviewconsole.queue = queue;//参数和jobtitle_reviewconsole.queue是区别的，因为有不同的分组，只能通过传参获取，全局的标识当前对应的，用于关闭栏目设置等
		jobtitle_reviewconsole.categories_id_e = categories_id_e;
		jobtitle_reviewconsole.c_level = c_level;
		
		var map = new HashMap();
		map.put("opt", '13');
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
		map.put("evaluationType",jobtitle_reviewconsole.evaluationType);
		Rpc({functionId : 'ZC00002316',async : false,success : function(form){
			var responseText = Ext.decode(form.responseText);
			var flag = responseText.succeed;
			if(flag) {
				var tableGrid = new BuildTableObj(responseText.tableConfig);
				var vs = Ext.getBody().getViewSize();
				Ext.create('Ext.window.Window', {
					title:zc.reviewconsole.chooseApplicant+"<span style='color:red;'>"+zc.reviewconsole.choosePerson+"</span>",
					id:'selectperson',
					layout: 'fit',
					modal: true,
					width:vs.width,
					height:vs.height,
					border:false,
					autoScroll:false,
				    items: [tableGrid.getMainPanel()],
				    buttonAlign:'center',
				    buttons:[{
						xtype : 'button',
						text : common.button.ok,
						margin:'20 10 0 0',
						handler : function() {jobtitle_reviewconsole.addperson(categories_id_e, c_level,queue);}
					},{
						xtype : 'button',
						text : zc.label.cancel,
						margin:'20 0 0 0',
						handler : function() {this.up('window').close()}
					}]
				}).show();
			}else {
				Ext.showAlert(responseText.message);
			}
			
		},scope:this}, map);
	},
	addperson:function(categories_id_e, c_level, queue){
		var list = new Array();//人员信息集
		
		var selectData = Ext.getCmp('jobtitle_reviewfile_console_selperson_tablePanel').getSelectionModel().getSelection();//获取数据
		for(var p in selectData){
			if(selectData.hasOwnProperty(p)){
				var map = new HashMap();
				map.put("a0100", selectData[p].data.a0100_e);
				map.put("ins_id", selectData[p].data.ins_id_e);
				map.put("tabid", selectData[p].data.tabid_e);
				map.put("task_id", selectData[p].data.task_id_e);
				map.put("basePre", selectData[p].data.basepre_e);
				list.push(map);
			}
		}
		
		/** 获取的是选择的数据 */
		if(list.length == 0){//如果没选，不允许【确定】
			Ext.showAlert("请选择申报人");
			return ;
		}
		
		var map = new HashMap();
		map.put("opt", "10");
		map.put("categories_id_e", categories_id_e);
		map.put("c_level", c_level);
		map.put("infoList", list);
		map.put("evaluationType",jobtitle_reviewconsole.evaluationType);
		map.put("queue", queue);
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
		Rpc({
			functionId : 'ZC00002316',
			async : false,
			success : function(form) {
				var response = Ext.decode(form.responseText);
				var errorcode = response.errorcode;
				var personCount = response.personCount;//用于刷新右上角的数字，因为有可能删除一个分组，这里采用后台传过来刷新
				if (errorcode == 0) {
					jobtitle_reviewconsole.reloadPersonMap();
					jobtitle_reviewconsole.loadStore();
					Ext.getDom("personCount_"+jobtitle_reviewconsole.review_links).innerHTML = personCount;
					Ext.getCmp('selectperson').close();
					/*Ext.showAlert('添加成功！',function(){
					});*/
				}else {
					Ext.showAlert(zc.reviewconsole.addFailed);
				}
			},
			scope : this
		}, map);
		
	},
	// 操作渲染
	renderoperation: function(){
		var categories_id_e = arguments[2].data.categories_id_e;
		var approval_state = arguments[2].data.approval_state;
		var name = arguments[2].data.name;
		var selectId = "";
		if(jobtitle_reviewconsole.userType == '1' && jobtitle_reviewconsole.review_links == '2') {
			selectId = "e_"+categories_id_e;
		}else {
			selectId = "h_"+categories_id_e;
		}
		
		var startStyle = '',reStartStyle = '',stopStyle = '', delStyle='', finishStyle='', width='';
		var style = 'background-image:url(/images/new_module/reviewdiff.png);width:16px;height:16px;border:0;margin:0 5px 0 0;float:left;cursor:pointer;';
		if(jobtitle_reviewconsole.enterType == '1') {//创建会议的时候
			width = '70px';
			startStyle = 'display:none;';
			reStartStyle = 'display:none;';
			stopStyle = 'display:none;';
			delStyle = 'background-image:url(/images/new_module/reviewdiff.png);width:16px;height:16px;border:0;cursor:pointer;display: inline-block;';
			style = '';//只有删除图标
		}else {
			if(approval_state == '1'){//启动/结束时，显示重新启动图标
				if(!jobtitle_reviewconsole.isFinished){
					stopStyle = 'display:inline;';
					reStartStyle = 'display:inline;';
				}else{
					stopStyle = 'display:none;';
					reStartStyle = 'display:none;';
					finishStyle = 'display:none;';
				}
				width = '70px';
				countStartStyle = 'display:none;';
				startStyle = 'display:none;';
				delStyle = 'display:none;';
			} else if(approval_state == '2'
						|| approval_state == '4'){
				width = '10px';
				startStyle = 'display:none;';
				reStartStyle = 'display:none;';
				countStartStyle = 'display: none;';
				stopStyle = 'display:none;';
				delStyle = 'display:none;';
				finishStyle = 'display:none;';
			}else{
				
				if(!jobtitle_reviewconsole.isFinished)
					startStyle = 'display:inline;';
				else {
					startStyle = 'display:none;';
					finishStyle = 'display:none;';
					delStyle = 'display:none;';
				}
				width = '70px';
				if(approval_state == '0') {//未启动状态没有结束图标
					width = '50px';
					finishStyle = 'display:none;';
				}
				reStartStyle = 'display:none;';
				stopStyle = 'display:none;';
				countStartStyle = 'display:none;';
			}
		}
		var isVoteOrScore = "";
		if(jobtitle_reviewconsole.evaluationType == '2') {
			isVoteOrScore = zc.editmeeting.mainview.score;
		}else {
			isVoteOrScore = zc.editmeeting.mainview.vote;
		}
		var html = '<div id="icon_all_'+categories_id_e+'" style="width:'+ width +';text-align:center;margin:0 auto;">';
		
		html += "<div id='icon_start_"+categories_id_e+"' style='background-position: 0 0;"+startStyle+style+"' title='" + zc.reviewconsole.startVote + isVoteOrScore + "' onclick=javascript:jobtitle_reviewconsole.operation(1,'"+categories_id_e+"','"+getEncodeStr(name)+"','"+selectId+"');>" +
				"</div>";
				
		html += "<div id='icon_restart_"+categories_id_e+"' style='background-position:16px 0;"+reStartStyle+style+"' title='" + zc.reviewconsole.restartVote + "' onclick=javascript:jobtitle_reviewconsole.operation(2,'"+categories_id_e+"','"+getEncodeStr(name)+"','"+selectId+"');>" +
				"</div>";
				
		html += "<div id='icon_stop_"+categories_id_e+"' style='background-position:16px 32px;"+stopStyle+style+"' title='" + zc.reviewconsole.stopVote + isVoteOrScore + "' onclick=javascript:jobtitle_reviewconsole.operation(6,'"+categories_id_e+"','"+getEncodeStr(name)+"');>" +
				"</div>";
				
		html += "<div id='icon_del_"+categories_id_e+"' style='display:inline;background-position: 16px 48px;"+delStyle+style+"' title='" + zc.label.dele + "' onclick=javascript:jobtitle_reviewconsole.operation(4,'"+categories_id_e+"');>" +
				"</div>";
		html += "<div id='icon_finish_"+categories_id_e+"' style='display:inline;background-position: 16px 16px;"+finishStyle+style+"' title='" + zc.label.finish +isVoteOrScore+ "' onclick=javascript:jobtitle_reviewconsole.operation(7,'"+categories_id_e+"');>" +
				"</div>";

		//统计现在不要了，通过定时来获取
		//html += "<div id='icon_count_"+categories_id_e+"' style='background-position:0px 32px;"+countStartStyle+style+"' title='" + zc.reviewconsole.voteCount + "' onclick=javascript:jobtitle_reviewconsole.operation(5,'"+categories_id_e+"');>" +
		//		"</div>";
		
		html += '</div>';
		
		return html;
	},
	// 进度
	progress : function(value, metaData, record){
		var categories_id_e = record.data.categories_id_e;
		var expertnum = record.data.expertnum;
		var submitnum = record.data.submitnum?record.data.submitnum:0;
		
		var title = jobtitle_reviewconsole.categoriesmap[categories_id_e];
		if(Ext.isEmpty(title)){
			title = '';
		}
		if(Ext.isEmpty(expertnum)){
			expertnum = 0;
		}
		var html = '';
		html = "<span title='"+title+"' id='"+categories_id_e+"'>"+submitnum+'/'+expertnum+"</span>";
		return html;
	},
	// 操作点击
	operation : function(opt, categories_id_e, name, selectId){
		var scoreOrVote = "";
		if(jobtitle_reviewconsole.evaluationType == '2') {
			scoreOrVote = zc.reviewconsole.scoreWork;
		}else {
			scoreOrVote = zc.reviewconsole.voteWork;
		}
		if(name) {
			name = getDecodeStr(name);
		}
		if(name) {
			name = getDecodeStr(name);
		}
		if(name) {
			name = getDecodeStr(name);
		}
		if(name) {
			name = getDecodeStr(name);
		}
		if(opt == 1 || opt == 2){// 启动/重新启动
			// check 
			name = getDecodeStr(name);
			if(Ext.isEmpty(name)){
				Ext.showAlert(zc.reviewconsole.enterName);
				return ;
			}
			if(jobtitle_reviewconsole.review_links == '2' && opt == 1) {//重新启动的不需要取判断是否选人了
				var store = jobtitle_reviewconsole.getReviewfile_console_dataStore();
				var recordList = store.data.items;
				if(jobtitle_reviewconsole.userType == '1') {
					for(var i=0;i<recordList.length;i++){
						var record = recordList[i].data;
						if(record.categories_id_e == categories_id_e) {
							if(Ext.isEmpty(record.expertnum) || record.expertnum == '0') {
								Ext.showAlert("评审人不能为0！");
								return;
							}
						}
					}
				}else if(jobtitle_reviewconsole.userType == '2' && (Ext.isEmpty(Ext.getDom(selectId)) || Ext.getDom(selectId).value == '')) {
					Ext.showAlert("请选择评审人！");
					return;
				}
				
			}
			var personflag = false;
			for(var p in jobtitle_reviewconsole.personmap){
				if(jobtitle_reviewconsole.personmap.hasOwnProperty(p)){
					if(p.indexOf(categories_id_e+'_') == 0){
						var personArray = jobtitle_reviewconsole.personmap[p];
						if(personArray.length > 0){
							personflag = true;
						}
					}
				}
			}
			if(!personflag){
				Ext.showAlert(zc.reviewconsole.chooseApplicant);
				return ;
			}
			//判断赞成名额是否大于申报人总数
			var store = jobtitle_reviewconsole.getReviewfile_console_dataStore();
			var recordList = store.data.items;
			for(var i=0;i<recordList.length;i++){
				var record = recordList[i].data;
				if(record.categories_id_e == categories_id_e) {
					var msg = jobtitle_reviewconsole.checkPersonNum(record);
					if(!Ext.isEmpty(msg)){
						//jobtitle_reviewconsole.loadStore();
						Ext.showAlert(msg);
						return ;
					}
				}
			}
			// start是否（重新）启动XXXX的投票工作？
			
			var confirmInfo = zc.label.is +(opt == 2?zc.label.restart:'')+ zc.label.start +name+scoreOrVote;
			Ext.showConfirm(confirmInfo, function(btn){
				if(btn == 'yes'){
					var dataList = new Array();
					for(var p in jobtitle_reviewconsole.personmap){
						if(jobtitle_reviewconsole.personmap.hasOwnProperty(p)){
							if(p.indexOf(categories_id_e+'_') == 0){
								var personArray = jobtitle_reviewconsole.personmap[p];
								for(var index in personArray){
									if(personArray.hasOwnProperty(index)){
										var person = personArray[index];
				
										var map = new HashMap();
										map.put("userid", person.w0501);//申报人主键序号加密
										dataList.push(map);
									}
								}
							}
						}
						
					}
					jobtitle_reviewconsole.startReview(dataList, categories_id_e, opt);
					jobtitle_reviewconsole.reloadPersonMap();
					jobtitle_reviewconsole.loadStore();
				}
			}, this);
			
		} else if(opt == 3){// 投票分析
			// 暂不处理
			
		} else if(opt == 4){// 删除分类
			var store = jobtitle_reviewconsole.getReviewfile_console_dataStore();
			var recordList = store.data.items;
			for(var i=0;i<recordList.length;i++){
				var record = recordList[i].data;
				if(record.categories_id_e == categories_id_e && (record.approval_state == '1' || record.approval_state == '2'  || record.approval_state=="4" || jobtitle_reviewconsole.isFinished)) {
					Ext.showAlert(zc.reviewconsole.cannotDelInStartting);
					return;
				}
			}
			Ext.showConfirm(zc.reviewconsole.deleteGroup, function(btn){
				if(btn == 'yes'){
					jobtitle_reviewconsole.categories_id_e = categories_id_e; 
					jobtitle_reviewconsole.deleteCategories();
					//jobtitle_reviewconsole.saveInfo(false);
				}
			}, this);
		} else if(opt == 5){//统计票数
			jobtitle_reviewconsole.reloadPersonMap();
			jobtitle_reviewconsole.loadStore();
		} else if(opt == 6){//暂停
			//是否暂停XXXX的投票工作？
			Ext.showConfirm(zc.label.is + zc.label.stop+name+scoreOrVote, function(btn){
				if(btn == 'yes'){
					var map = new HashMap();
					map.put("opt", '8');
					map.put("categories_id_e", categories_id_e);
					Rpc({
						functionId : 'ZC00002316',
						async : false,
						success : function(form) {
							var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
							if (errorcode == 0) {
								jobtitle_reviewconsole.loadStore();
							}else {
							}
						},
						scope : this
					}, map);
				}
			
			}, this);
		} else if(opt == 7){//置为已结束
			//是否置为已结束状态？
			Ext.showConfirm(zc.reviewconsole.setFinish, function(btn){
				if(btn == 'yes'){
					var map = new HashMap();
					map.put("opt", '22');
					map.put("categories_id_e", categories_id_e);
					map.put("evaluationType", jobtitle_reviewconsole.evaluationType);
					map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
					map.put("review_links", jobtitle_reviewconsole.review_links);
					map.put("type", "1");//校验是否存在未完成投票的人
					Rpc({
						functionId : 'ZC00002316',
						async : false,
						success : function(form) {
							var data = Ext.decode(form.responseText);
							if(!Ext.isEmpty(data.confirmMsg)){
								Ext.showConfirm(data.confirmMsg,function(btn){
									if(btn == 'yes'){
										var map = new HashMap();
										map.put("opt", '22');
										map.put("categories_id_e", categories_id_e);
										map.put("evaluationType", jobtitle_reviewconsole.evaluationType);
										map.put("type", "0");//校验是否存在未完成投票的人
										map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
										map.put("review_links", jobtitle_reviewconsole.review_links);
										Rpc({
											functionId : 'ZC00002316',
											async : false,
											success : function(form) {
												var res = Ext.decode(form.responseText);
												if(res.succeed){
													jobtitle_reviewconsole.reloadPersonMap();
													jobtitle_reviewconsole.loadStore();
												}else{
													Ext.showAlert(res.message);
												}
											}
										}, map)
									}
								});
							}else{
								jobtitle_reviewconsole.reloadPersonMap();
								jobtitle_reviewconsole.loadStore();	
							}
							
						},
						scope : this
					}, map);
				}
			
			}, this);
		}
		
	},
	//opt:1:启动，2：重启
	startReview : function(idlist, categories_id, opt){
    	var map = new HashMap();
    	map.put("opt","6");
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
		map.put("evaluationType",jobtitle_reviewconsole.evaluationType);
		map.put("userType",jobtitle_reviewconsole.userType);
		map.put("operateType", opt);
		if(idlist != undefined){
			map.put("idlist", idlist);
			map.put("categories_id", categories_id);
		} else {
			map.put("idlist", idlist);
		}
			
		Rpc({functionId:'ZC00002316',async:false,success: function(form){
			var data = Ext.decode(form.responseText);
			if(data.msg)
				Ext.showAlert(data.msg);
			else
				Ext.showAlert(data.message);
			if(Ext.getCmp('checklist')){
				Ext.getCmp('checklist').close();
			}
			jobtitle_reviewconsole.loadStore();
		}},map);
    },
	deleteCategories:function(){
		var map = new HashMap();
			map.put("opt", '4');
			map.put("categories_id_e", jobtitle_reviewconsole.categories_id_e);
			map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
			map.put("review_links", jobtitle_reviewconsole.review_links);
			Rpc({
				functionId : 'ZC00002316',
				async : false,
				success : function(form) {
					var response = Ext.decode(form.responseText);
					var errorcode = response.errorcode;
					var personCount = response.personCount;//用于刷新右上角的数字，因为有可能删除一个分组，这里采用后台传过来刷新
					var flag = response.flag;
					if(flag == 1) {
						if (errorcode == 0) {
							jobtitle_reviewconsole.loadStore(-1);
							Ext.getDom("personCount_"+jobtitle_reviewconsole.review_links).innerHTML = personCount;
						}
					} else if(flag == 0)
						Ext.showAlert(zc.reviewconsole.cannotDelInStartting);
					jobtitle_reviewconsole.reloadPersonMap();
					jobtitle_reviewconsole.loadStore();
				},
				scope : this
			}, map);
	},
	approval_state : function(value){
		var categories_id_e = arguments[2].data.categories_id_e;
		var html = '<span id='+categories_id_e+'_state>';
		
		if(value == '0' || value == ''){
			html += zc.label.on + zc.label.start;//未启动
		} else if(value == '1'){
			html += zc.label.ing + zc.label.start;//已启动
		} else if(value == '2'){
			html += zc.label.ing + zc.label.finish;//已结束
		} else if(value == '3'){
			html += zc.label.ing + zc.label.stop;//已暂停
		}else if(value == '4'){
			html += zc.label.ing + zc.label.achived;//已归档
		}
		html += '</span>';
		
		return html;
	},
	createPerson:function(key, approval_state, categories_id_e, c_level, expert_count, num_row, id_div){
		var mouseAction = '';
		if(approval_state == '1' || approval_state == '2' || approval_state=="4" || jobtitle_reviewconsole.isFinished){//执行中、结束状态，不能再删除人
			mouseAction = '';
		}else {
			mouseAction = 'onMouseOver="jobtitle_reviewconsole.showHideDel(1,this);" onMouseOut="jobtitle_reviewconsole.showHideDel(2,this);"'
		}
		
		var personArray = jobtitle_reviewconsole.personmap[key];
		
		jobtitle_reviewconsole.positionPersonMap_temp = new HashMap();//key:id,value:count+"_"+top+"_"+left
		var html = '';
		var nextGroup = '0';
		jobtitle_reviewconsole.queue = 0;
		var top = 0;//为了后面的拖拽
		var height = 95;//每个div的高度
		var left = 20;
		var count = 1;
		jobtitle_reviewconsole.personWidth = 88;//一个人员的宽度
		jobtitle_reviewconsole.personTop = 95;//一个人的高度
		jobtitle_reviewconsole.offset_left = 20;//第一个人的偏移量
		var personSum = jobtitle_reviewconsole.getMapLength(personArray);//总共有多少人
		for(var p in personArray){
			if(personArray.hasOwnProperty(p)){
				var person = personArray[p];
				var w0503 = person.w0503;//nbs
				var w0505 = person.w0505;//a0100
				var w0511 = person.w0511;//a0101
				var imgpath = person.imgpath;//imgpath
				var w0501 = person.w0501;//w0501
				var w0507 = person.w0507;//UN
				var w0509 = person.w0509;//UM
				var w0513 = person.w0513;//现聘
				var w0515 = person.w0515;//申报
				jobtitle_reviewconsole.queue = person.queue;//申报人分组
				var expert_already_count = person.expert_already_count;//赞成票数有几个
				var ispass = person.ispass;//赞成票数有几个
				
				var pass_style = 'display:none;';
				if(ispass =='01' && (approval_state == '1' || approval_state == '2' || approval_state=="4" || jobtitle_reviewconsole.isFinished)){
					pass_style = 'display:block;';
				}
				
				if(approval_state == '1' || approval_state == '2' || approval_state=="4" || jobtitle_reviewconsole.isFinished){
					
				}else {//不是启动、结束状态，赞成人数隐藏。显示0
					expert_already_count = 0;
				}
				
				var title = '';
					title += zc.label.name + ': '+w0511+'\n';
					title += zc.label.b0110 + ': '+w0507+'\n';
					title += zc.label.e0122 + ': '+w0509+'\n';
					title += zc.label.currenttitle + ': '+w0513+'\n';
					title += zc.label.applytitle + ': '+w0515+'\n';
				
				var id = key +'_'+w0501+'_'+person.queue;
				
				if(nextGroup != jobtitle_reviewconsole.queue) {
					if(jobtitle_reviewconsole.queue != 1) {//申报人组类的分组
						if((approval_state == '0' || approval_state == '3') && !jobtitle_reviewconsole.isFinished){//执行中、结束状态，不能再加人
							
							html += '<table style="border:none;float:left;margin:5px 0 0 0;position: absolute;top: ' + top + 'px;left: ' + left +'px;"><tr><td>';
							html += "<a id='a_"+categories_id_e+"_"+c_level+"' onmousedown='javascript:jobtitle_reviewconsole.selectperson(\""+categories_id_e+"\",\""+c_level+"\","+nextGroup+");'>" +
										"<img style='width:44px;height:44px;margin-left:12px;margin-top:5px;cursor:pointer;' src='/images/new_module/nocycleadd.png' border=0>" +
									"</a>";
							html += '</td></tr></table>';
						}
						html += '</div>';
						html += '<div style="float:left;padding:12px 0 0 50px;width:98%;" id="' + categories_id_e + "_queue_" + jobtitle_reviewconsole.queue + '">';
						if(count%num_row == 1) {
							top += 57;
							height = height - 35;
						}else {
							top += 97;
						}
						jobtitle_reviewconsole.heightMap.put(id_div, height + "px");
						height = 97;
						left = jobtitle_reviewconsole.offset_left;
						count = 1;
					}
					id_div = categories_id_e + "_queue_" + jobtitle_reviewconsole.queue;
					//html += '<div><img src="/images/new_module/index.png" style="float:left;margin:27px 0 0 -34px;" /><span style="color:#fff;float:left;padding-top:30px;margin-left:-50px;width:50px;height:50px;text-align:center;font-size:15px;">'+jobtitle_reviewconsole.queue+'</span></div>'
					nextGroup = jobtitle_reviewconsole.queue;
				}
				
				var moveHTML = "";
				if(approval_state == 0 || approval_state == 3) {
					moveHTML = 'onmousedown="jobtitle_reviewconsole.mouse_down(this.id, '+num_row+')" onmouseup="jobtitle_reviewconsole.mouse_up(this.id, ' + num_row +')"';
				}
				//是否是结束状态或者1：进行中：2：结束，4：归档，jobtitle_reviewconsole.isFinished：如果是true说明是从查看按钮过来的，如果不是是发起评审，这样根据权限判断
				var showScale = false;
				if(jobtitle_reviewconsole.evaluationType=='1' && (approval_state == '1' || approval_state == '2' || approval_state=="4" || jobtitle_reviewconsole.isFinished)) {
					showScale = true;
				}
				//如果姓名过长，这里做下处理
				var len_ = jobtitle_reviewconsole.getStrLength(w0511);
				var title_ = showScale?((len_>6)?w0511:""):((len_>8)?w0511:"");
				w0511 = (len_>6)?(showScale?jobtitle_reviewconsole.getSubStr(w0511, 6)+'...':((len_>8)?jobtitle_reviewconsole.getSubStr(w0511, 8)+'...':w0511)):w0511;
				html += '<table ' + moveHTML + ' id="pmove_'+categories_id_e+'_'+w0501+'" style="width: '+(showScale?jobtitle_reviewconsole.personWidth:66)+'px;border:none;float:left;position: absolute;top: ' + top + 'px;left: ' + left +'px;">';
					html += '<tr><td style="position:relative;" id="td_'+id+'" '+mouseAction+' title="'+title+'">' +
							'	<img width=50 height=50 style="border-radius: 50%;margin:5px" src='+imgpath+'>' +
							'	<img id="del_'+id+'" onmousedown="jobtitle_reviewconsole.delPerson(this);" width=20 height=20 src=/workplan/image/remove.png style="cursor:pointer;position:absolute;top:0;left:40px;display:none;">' +
							'	<img width=20 height=20 src=/images/new_module/xuanzhong.png style="position:absolute;left:20px;top:40px;width:24px;height:24px;'+pass_style+'">'+
							'</td></tr>';
					html += '<tr><td style="'+(showScale?'':'text-align:center;')+'font-size:13px;" title=' + title_ + '>'+w0511+'<span id=d_'+categories_id_e+'_'+w0501+'>' ;
								if(showScale){//启动和结束状态时，名称后增加（1/2）
									html += '('+expert_already_count+'/'+expert_count+')';
								}
							html +='</span></td></tr>';
				html += '</table>';
				
				jobtitle_reviewconsole.positionPersonMap_temp.put("pmove_"+categories_id_e+"_"+w0501, count+"_"+top+"_"+left);
				if(personSum != count || (approval_state == '0' || approval_state == '3')) {
					left += jobtitle_reviewconsole.personWidth;
					if(++count && count%num_row == 1) {
						left = jobtitle_reviewconsole.offset_left;
						top += jobtitle_reviewconsole.personTop;
						height += jobtitle_reviewconsole.personTop;
					}
				}
			}
		}
		jobtitle_reviewconsole.positionPersonMap.put(categories_id_e, jobtitle_reviewconsole.positionPersonMap_temp);
		jobtitle_reviewconsole.heightMap.put(id_div, height + "px");
		jobtitle_reviewconsole.finally_top = top;
		jobtitle_reviewconsole.finally_left = left;
		return html;
	},
	//移动代码start
	mouse_down: function(id, num_row) {
		if(Ext.util.CSS.getRule(".x-dd-drop-nodrop div.x-dd-drop-icon"))
	    	  Ext.util.CSS.updateRule(".x-dd-drop-nodrop div.x-dd-drop-icon","background-image","url(images/dd/drop-yes.gif)");
		var cate_id = id.split("_")[1];
		jobtitle_reviewconsole.isMove = true;
		var ori_left = Ext.getDom(id).style.left;//移动的出时位置
		ori_left = ori_left.substring(0, ori_left.length-2);
		var ori_top = Ext.getDom(id).style.top;
		ori_top = ori_top.substring(0, ori_top.length-2);
		var ori_clientX = event.clientX;
		var ori_clientY = event.clientY;
		
		var temp_map = jobtitle_reviewconsole.positionPersonMap[cate_id];
		for(var key in temp_map) {
			if(key == id) {
				var count = Number(temp_map[key].split("_")[0]);
				jobtitle_reviewconsole.ori_count = count;
				jobtitle_reviewconsole.previous_count = count;
			}
		}
		
		document.onmousemove = function(e) { //鼠标移动事件
			if(!jobtitle_reviewconsole.isMove) {
				if(Ext.util.CSS.getRule(".x-dd-drop-nodrop div.x-dd-drop-icon"))
					Ext.util.CSS.updateRule(".x-dd-drop-nodrop div.x-dd-drop-icon","background-image","url(images/dd/drop-no.gif)");
				return;
			}
            e = e || window.event;
            var x_move = e.clientX; //鼠标移动X的坐标
            var y_move = e.clientY; //鼠标移动Y的坐标
            //如果鼠标没移动，别移动啦
            if(x_move == ori_clientX && y_move == ori_clientY) {
            	return;
            }
            //移动的坐标减去按下的坐标 = 移动的距离
            var moveY = Number(ori_top) + y_move - ori_clientY;
            var moveX = Number(ori_left) + x_move - ori_clientX;
            moveX = moveX<Number(jobtitle_reviewconsole.offset_left)?Number(jobtitle_reviewconsole.offset_left):moveX;
            moveY = moveY<0?0:moveY;
            jobtitle_reviewconsole.move(moveX, moveY, id, num_row);
        }
		//ie需要在这写鼠标放下事件
		document.onmouseup = function(e) {
			jobtitle_reviewconsole.mouse_up(id);
		}
	},
	mouse_up: function(id) {
		if(jobtitle_reviewconsole.isMove) {
			var cate_id = id.split("_")[1];
			jobtitle_reviewconsole.isMove = false;
			var temp_map = jobtitle_reviewconsole.positionPersonMap[cate_id];
			for(var key in temp_map) {
				if(key == id) {
					Ext.getDom(id).style.top = temp_map[key].split("_")[1] + "px";
			        Ext.getDom(id).style.left = temp_map[key].split("_")[2] + "px";
			        var map = new HashMap();
					map.put("ori_categories", key.split("_")[1]);
					map.put("w0501_e", key.split("_")[2]);
					map.put("to_seq", temp_map[key].split("_")[0]);
					map.put("opt", "25");
					Rpc({functionId : 'ZC00002316',async : false,success : null,scope:this},map);
				}
			}
		}
	},
	//移动ori_count:原先的位置，num_row：每行有多少个
	move: function(moveX, moveY, id, num_row) {
		var cate_id = id.split("_")[1];
		//初始位置，第几个人
		var ori_count = Number(jobtitle_reviewconsole.ori_count);
		//算出第几列，如果移到一半需要特殊处理
		var x = Math.ceil((moveX-jobtitle_reviewconsole.offset_left)/jobtitle_reviewconsole.personWidth) + 
						((moveX-jobtitle_reviewconsole.offset_left)%jobtitle_reviewconsole.personWidth>Number(jobtitle_reviewconsole.personWidth/2)?1:0) + 
						(((moveX-jobtitle_reviewconsole.offset_left)%jobtitle_reviewconsole.personWidth==0?1:0));
		//算出是第几行，如果移到一半需要加1
		var y = Math.floor(moveY/Number(jobtitle_reviewconsole.personTop)) + ((moveY%Number(jobtitle_reviewconsole.personTop))>Number(jobtitle_reviewconsole.personTop/2)?1:0)
		//根据几行*一行多少+第几列=最终的位置
		var finally_count = y*num_row + x;//要到的位置count
		var map = jobtitle_reviewconsole.positionPersonMap[cate_id];//每个人对应的count_top_left
		finally_count = finally_count < jobtitle_reviewconsole.getMapLength(map)?finally_count:Number(jobtitle_reviewconsole.getMapLength(map));
		//如果原始的位置和最终的位置不一样，即移动了
		if((jobtitle_reviewconsole.previous_count != finally_count) && (finally_count > 0) && (finally_count != ori_count)) {
			jobtitle_reviewconsole.previous_count = finally_count;
			var map_temp = new HashMap();
			jobtitle_reviewconsole.map_move_temp = new HashMap();
			for(var key in map) {
				if(map.hasOwnProperty(key)) {
					if(key == id) {
						continue;
					}
					var count = Number(map[key].split("_")[0]);
					var top = Number(map[key].split("_")[1]);
					var left = Number(map[key].split("_")[2]);
					//下移，需要考虑当前位置到最终位置之间的人所有的位置-1，也就是left增加
					if(finally_count > ori_count && count > ori_count && count <= finally_count) {//下移
						//如果当前的人位置和其中的某个人的位置一样了，则将原始的位置付给这个人，使其位置正确
						if(finally_count == count) {
							var top_temp1 = Ext.getDom(key).style.top;
							var left_temp1 = Ext.getDom(key).style.left;
							jobtitle_reviewconsole.ori_count = count;
							map_temp.put(id, count+"_"+top_temp1.substring(0,top_temp1.length-2)+"_"+left_temp1.substring(0,left_temp1.length-2));//后加的，会覆盖
						}
						if(count != ori_count && count%num_row == 1) {//如果是最左侧的，在下移的过程中会进入上一行最后一个
							Ext.getDom(key).style.top = Number(top) - jobtitle_reviewconsole.personTop + "px";
							Ext.getDom(key).style.left = (Number(jobtitle_reviewconsole.offset_left) + ((num_row-1)*jobtitle_reviewconsole.personWidth)) + "px";
						}else{
							Ext.getDom(key).style.left = Number(left) - jobtitle_reviewconsole.personWidth + "px";
						}
						count--;
						
					//上移，需要考虑最终位置到当前位置之间的人位置+1，也就是left增加	
					}else if(finally_count < ori_count && count < ori_count && count >= finally_count){//上移
						if(finally_count == count) {
							var top_temp1 = Ext.getDom(key).style.top;
							var left_temp1 = Ext.getDom(key).style.left;
							jobtitle_reviewconsole.ori_count = count;
							map_temp.put(id, count+"_"+top_temp1.substring(0,top_temp1.length-2)+"_"+left_temp1.substring(0,left_temp1.length-2));//后加的，会覆盖
						}
						if(count != ori_count && count%num_row == 0) {//如果是最右侧的，在上移的过程中会进入下一行第一个
							Ext.getDom(key).style.top = Number(top) + jobtitle_reviewconsole.personTop + "px";
							Ext.getDom(key).style.left = jobtitle_reviewconsole.offset_left + "px";
						}else{
							Ext.getDom(key).style.left = Number(left) + jobtitle_reviewconsole.personWidth + "px";
						}
						if(count == ori_count) {//上移的时候将count赋予为最后移动的位置，left和top因为先循环，所以值已经改变，这样直接getDom就行
							count = finally_count;
						}else {
							count++;
						}
					}
					var top_temp = Ext.getDom(key).style.top;
					var left_temp = Ext.getDom(key).style.left;
					map_temp.put(key, count+"_"+top_temp.substring(0,top_temp.length-2)+"_"+left_temp.substring(0,left_temp.length-2));
				}
			}
			//控制速度的
			jobtitle_reviewconsole.speedX = 4;
			jobtitle_reviewconsole.speedY = 2;
			jobtitle_reviewconsole.positionPersonMap.put(cate_id,map_temp);
		}
		Ext.getDom(id).style.left = moveX + "px";
        Ext.getDom(id).style.top = moveY + "px";
	},
	//移动代码end
	reloadPersonMap : function(){
		var map = new HashMap();
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
		map.put("evaluationType",jobtitle_reviewconsole.evaluationType);
		map.put("enterType",jobtitle_reviewconsole.enterType);
		map.put("userType",jobtitle_reviewconsole.userType);
		map.put("isFinished",jobtitle_reviewconsole.isFinished);
		map.put("screenWidth",Ext.getBody().getViewSize().width);
		Rpc({functionId : 'ZC00002315',async : false,success : function(form){
			var responseText = Ext.decode(form.responseText);
			jobtitle_reviewconsole.personmap = responseText.personmap;
			jobtitle_reviewconsole.categoriesmap = responseText.categoriesmap;
			jobtitle_reviewconsole.randomCountMap = responseText.randomCountMap;//获取随机账号的拼接，<加密（categories_id）,数量>
			jobtitle_reviewconsole.groupMap = responseText.groupMap;//<group_name, group_id+"_"+count>所有的，用于展示下拉框选择
			jobtitle_reviewconsole.cateIdGroupIdMap = responseText.cateIdGroupIdMap;//<categories_id,group_id>当前分组对应的group_id
			jobtitle_reviewconsole.support_word = responseText.support_word;//是否支持导出word
		},scope:this}, map);
	},
	getReviewfile_console_dataStore:function(){
		return Ext.data.StoreManager.lookup('jobtitle_reviewfile_console_'+jobtitle_reviewconsole.review_links+'_dataStore');
	},
	showHideDel:function(state, td){
		var arr = td.id.split('_');
		var delId = 'del_'+arr[1]+'_'+arr[2]+'_'+arr[3]+"_"+arr[4];
		if(Ext.getDom(delId)){
			if(state == 1){
				Ext.getDom(delId).style.display = 'block';
			} else if(state == 2){
				Ext.getDom(delId).style.display = 'none';
			}
		}
	},
	delPerson:function(delImg){
		Ext.showConfirm(zc.reviewconsole.confirmDelete, function(btn){
				if(btn == 'yes'){
					var arr = delImg.id.split('_');
					var categories_id_e = arr[1];
					var c_level = arr[2];
					var w0501 = arr[3];
					var queue = arr[4];
					
					var map = new HashMap();
					map.put("opt", '11');
					map.put("categories_id_e", categories_id_e);
					map.put("c_level", c_level);
					map.put("w0501", w0501);
					map.put("queue", queue);
					map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
					map.put("review_links", jobtitle_reviewconsole.review_links);
					
					Rpc({functionId : 'ZC00002316',async : false,success : function(form){
						var response = Ext.decode(form.responseText);
						var errorcode = response.errorcode;
						var personCount = response.personCount;//用于刷新右上角的数字，因为有可能删除一个分组，这里采用后台传过来刷新
						if (errorcode == 0) {
							jobtitle_reviewconsole.reloadPersonMap();
							jobtitle_reviewconsole.loadStore();
							Ext.getDom("personCount_"+jobtitle_reviewconsole.review_links).innerHTML = personCount;
						}else {
						}
					},scope:this}, map);
				}
		});
	},
	checkCell : function(record) {
		var approval_state = record.data.approval_state;
		if(approval_state == '1' || approval_state == '2' || approval_state=="4" || jobtitle_reviewconsole.isFinished){//执行中、结束状态，不能编辑
			return false;
		}else {
			return true
		}
	},
	// 校验应选人数：应选人数不能大于所选人数、不能小于0
	checkPersonNum : function(record){
		var msg = '';
		
		for(var p in record){
			if(p.indexOf('c_') == 0){
				var c_level = p.split('_')[1];
				var inputNum = record[p];//应选人数
				var categories_id_e = record.categories_id_e;
				
				var p = c_level;
				if(c_level == 'number'){
					p = 'person';
				}
				var key = categories_id_e+"_"+p;
				var personArray = jobtitle_reviewconsole.personmap[key];
				if(personArray){
					var personNum = personArray.length;//申报人数
					if(inputNum < 0){
						msg = zc.reviewconsole.shouldThanZero;//'应选人数不能小于0！'
					} else if(inputNum > personNum){
						msg = zc.reviewconsole.shouldThan;//'应选人数不能大于申报人数！'
					}
				}
			}
			
		}
		
		return msg;
	},
	closeSettingWin:function() {
		//Ext.getCmp('reviewconsole_schemeSetting').destroy();
		Ext.getCmp('selectperson').destroy();
		jobtitle_reviewconsole.selectperson(jobtitle_reviewconsole.categories_id_e,jobtitle_reviewconsole.c_level,jobtitle_reviewconsole.queue);
	},
	/**
	 * 分数统计|票数统计
	 * type 为空时 分数|票数统计  不为空时 导出代表作
	 */
	scoreCount:function(type){
		// 获得分组选项
		var map = new HashMap();
		map.put("opt","18");
		map.put("type","2");//2代表查询
		map.put("w0301_e",this.w0301_e);
		map.put("userType",jobtitle_reviewconsole.userType);
		map.put("review_links",this.review_links);
		Rpc({
			functionId : 'ZC00002316',
			success : function(res){
				var resultObj = Ext.decode(res.responseText);
				var config = resultObj.msg;
				var cateIdWithNameList = resultObj.cateIdWithNameList;
				var items = new Array();
				for(var i = 0; i < cateIdWithNameList.length; i++) {
					var array = cateIdWithNameList[i].split("_");
					var obj = new Object();
					if(array[1]=="")//分组名为空不可选
						continue;
					obj.selected = '0';
					obj.dataValue = array[0];
					var cat_name = "";
					//可能名称中包含_这里不能直接取array[1]
					for(var j = 1; j < array.length; j++) {
						cat_name += "_" + array[j];
					}
					obj.dataName = cat_name.substring(1,cat_name.length);
					items.push(obj);
				}
				
			// 选择显示的组名--选择列表指标
			Ext.require('NoticePath.SelectNoticeField', function() {
				Ext.create("NoticePath.SelectNoticeField", {
					alternativetitle : zc.reviewconsole.waittingGroup,
					hasSelectTitle : zc.reviewconsole.hasSelectGroup,
					title : zc.reviewconsole.showGroup,
					preItems : items,
					//type==1 导出代表作
					callBackFunc : type===1?"jobtitle_reviewconsole.outMasterpiece":'jobtitle_reviewconsole.selectNoticeFieldScoreCount'
				});
			});
		
			},scope:this
		}, map);
		
	},
	outMasterpiece:function(selectedItems){//选择分组后导出代表作
		//将集合取出分组id
		var groupids = "";
		for(var i = 0; i < selectedItems.length; i++) {
			var valueid = selectedItems[i].get("dataValue");
			if(Ext.isEmpty(valueid))
				continue;
			if(i == 0)
				groupids = valueid;
			else
				groupids += "," + valueid;
		}
		if(Ext.isEmpty(groupids))
			return;
		
		var map = new HashMap();
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
		map.put("evaluationType", jobtitle_reviewconsole.evaluationType);
		map.put("groupids", groupids);
	    map.put("isFinished",jobtitle_reviewconsole.isFinished);
	    map.put("masterpieceType","1");
	    Ext.MessageBox.wait("", "正在导出，请稍候...");
	    Rpc({functionId:'ZC00005011',success:function(res){
			Ext.MessageBox.close();
			res=Ext.decode(res.responseText);
			if(res.flag){
				var url = "/servlet/vfsservlet?fromjavafolder=true&fileid="+res.filename;
					var win=open(url,"zip");
			}else{
				Ext.showAlert(res.emsg);
			}
		}},map);
	    
	},
	selectNoticeFieldScoreCount:function(selectedItems){
		//将集合取出分组id
		var groupids = "";
		for(var i = 0; i < selectedItems.length; i++) {
			var valueid = selectedItems[i].get("dataValue");
			if(Ext.isEmpty(valueid))
				continue;
			if(i == 0)
				groupids = valueid;
			else
				groupids += "," + valueid;
		}
		if(Ext.isEmpty(groupids))
			return;
		
		var map = new HashMap();
		map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
		map.put("review_links", jobtitle_reviewconsole.review_links);
		map.put("evaluationType", jobtitle_reviewconsole.evaluationType);
		map.put("groupids", groupids);
	    map.put("isFinished",jobtitle_reviewconsole.isFinished)
		Ext.require('ReviewMeetingURL.ReviewScorecount', function(){
			SalaryTemplateGlobal = Ext.create("ReviewMeetingURL.ReviewScorecount", map);
		});
	},
	/**
	 * 分数统计归档|票数统计归档
	 */
	countResultsArchiving:function(){
		// 37641 归档前需校验该阶段是否有分组或申报人
		var personflag = false;
		for(var p in jobtitle_reviewconsole.personmap){
			if(jobtitle_reviewconsole.personmap.hasOwnProperty(p)){
				var personArray = jobtitle_reviewconsole.personmap[p];
				if(personArray.length > 0){
					personflag = true;
				}
			}
		}
		if(!personflag){
			Ext.showAlert(zc.reviewconsole.resultData.noPerson);
			return;
		}
		//判断是否为最后一个阶段
		var msg = zc.reviewconsole.resultData.confirmMsg;
		if(jobtitle_reviewconsole.review_links == jobtitle_reviewconsole.endSegment){
			msg = zc.reviewconsole.resultData.confirmMsg2;
		}
		Ext.showConfirm(msg,function(btn){
		 	if(btn == 'yes'){
		 		var map = new HashMap();
		 		// 16：票数归档 || 分数归档
				map.put("opt", "16");
				map.put("w0301_e", jobtitle_reviewconsole.w0301_e);
				map.put("review_links", jobtitle_reviewconsole.review_links);
				map.put("evaluationType", jobtitle_reviewconsole.evaluationType);
				// 判断评审流程走完了吗
				map.put("type","0");
				Rpc({functionId:'ZC00002316',async:false,success:function(res){
					 var resultObj = Ext.decode(res.responseText);
					 var type = resultObj.type;
					 var msg = resultObj.msg;
					 if(type == '1'){
						 Ext.showConfirm(msg,function(btn){
							if(btn == 'yes'){
								var vo = new HashMap();
								// 16：票数归档 || 分数归档
								vo.put("opt", "16");
								vo.put("w0301_e", jobtitle_reviewconsole.w0301_e);
								vo.put("review_links", jobtitle_reviewconsole.review_links);
								vo.put("evaluationType", jobtitle_reviewconsole.evaluationType);
								// 直接归档
								vo.put("type","1");
								Rpc({functionId:'ZC00002316',async:false,success:function(resp){
									var resultObj = Ext.decode(resp.responseText);
									var msg = resultObj.msg;
									if(!Ext.isEmpty(msg)){
										Ext.showAlert(msg);
									}else{
										Ext.showAlert(resultObj.message);
										return;
									}
									if(msg.indexOf('归档成功') > -1)
					 					jobtitle_reviewconsole.isFinished = true;
									var win = Ext.getCmp("startReviewWin");
									win.removeAll(true);
									win.add(jobtitle_reviewconsole.getTableConfig());
									
								}},vo);	
							}
					 	});
					 }else{
					 	if(!Ext.isEmpty(msg)){
							Ext.showAlert(msg);
					 	}else{
							Ext.showAlert(resultObj.message);
							return;
						}
					 	var win = Ext.getCmp("startReviewWin");
					 	if(msg.indexOf('归档成功') > -1)
					 		jobtitle_reviewconsole.isFinished = true;
						win.removeAll(true);
						win.add(jobtitle_reviewconsole.getTableConfig());
					 }
				}},map);
		 		
		 	}
		 });
	},
	/**
	 * 保存通过率配置（=1 通过率按2/3控制）
	 */
	saveRateControl:function(){
		var map = new HashMap();
		map.put("opt","17");
		map.put("w0301_e",this.w0301_e);
		map.put("review_links",this.review_links);
		
		var checked = Ext.getDom("approve_"+this.review_links).checked;
		map.put("rate_control",checked?"1":"2");
		Rpc({functionId : 'ZC00002316',async : false,success : function(form){
			var result = Ext.decode(form.responseText);
			
		},scope:this},map);
	},
	showExportExcelColumns:function(exportType){
		var tablePanel = Ext.getCmp('jobtitle_reviewfile_console_'+jobtitle_reviewconsole.review_links+'_tablePanel');
		var dataLen = tablePanel.store.data.length;
		if(dataLen == 0) {
			Ext.showAlert(zc.reviewconsole.noDataToExport);
			return;
		}
		
		jobtitle_reviewconsole.exportType=exportType;
		if(this.review_links=='1'||this.review_links=='4'){
			jobtitle_reviewconsole.exportExcel('noCateIds');
			return;
		}
		var map = new HashMap();
		map.put("opt","21");
		map.put("type","2");//2代表查询
		map.put("exportType",exportType+"");
		map.put("w0301_e",this.w0301_e);
		map.put("review_links",this.review_links);
		map.put("userType",jobtitle_reviewconsole.userType);
		Rpc({
			functionId : 'ZC00002316',
			success : function(res){
				var resultObj = Ext.decode(res.responseText);
				var personCateList = resultObj.personCateList;
				if(personCateList.length==0){
					Ext.showAlert(zc.reviewconsole.noStartedGroups);
					return;
				}
				var items = new Array();
				for(var i = 0; i < personCateList.length; i++) {
					var array = personCateList[i].split("_");
					var obj = new Object();
					obj.selected = '0';
					obj.dataValue = array[0];
					var cat_name = "";
					//可能名称中包含_这里不能直接取array[1]
					for(var j = 1; j < array.length; j++) {
						cat_name += "_" + array[j];
					}
					obj.dataName = cat_name.substring(1,cat_name.length);
					items.push(obj);
				}
				
			// 选择显示的组名--选择列表指标
			Ext.require('NoticePath.SelectNoticeField', function() {
				Ext.create("NoticePath.SelectNoticeField", {
					title : zc.reviewconsole.showGroup,
					alternativetitle : zc.reviewconsole.waittingGroup,
					hasSelectTitle : zc.reviewconsole.hasSelectGroup,
					preItems : items,
					callBackFunc : 'jobtitle_reviewconsole.exportExcel'
				});
			});
		
			},scope:this
		}, map);
	},
	exportExcel:function(data){
		var encodeCateIds="";
		if(data=="noCateIds")
			encodeCateIds=data;
		else{
			for(var i=0;i<data.length;i++){
				encodeCateIds+=data[i].data.dataValue;
				if(i<data.length-1)
					encodeCateIds+=",";
			}
		}
		var map = new HashMap();
		map.put("opt","15");
		map.put("w0301_e",jobtitle_reviewconsole.w0301_e);
		map.put("review_links",jobtitle_reviewconsole.review_links);
		map.put("evaluationType",jobtitle_reviewconsole.evaluationType);
		map.put("type",jobtitle_reviewconsole.exportType);
		map.put("encodeCateIds",encodeCateIds);
		Rpc({functionId : 'ZC00002316',async : false,success : function(form){
			var result = Ext.decode(form.responseText);	
			if(result.msg==null||result.msg==""){
			if(result.succeed){
				window.location.target="_blank";
				window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+result.fileName;
				}else{
					Ext.showAlert(result.message);
				}
	     	}else{
	     	     Ext.showAlert(result.msg);
	     	}
		},scope:jobtitle_reviewconsole},map);
	},
	notice:function() {
		//默认选中：部门、单位名称、姓名、现聘职务、申报职务
		var defaultSelectedItems = ',w0509,w0507,w0511,w0513,w0515,';
		//排除指标：评审环节、评审状态、评审材料、送审材料、二级单位评议组、已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、
		//同行专家、基本达到人数、未达到人数、已达到人数、赞成人数占比、状态、学科组已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、参会人数、
		//已评数/总数、赞成人数、反对人数、弃权人数、赞成人数占比、状态、评价表、同行专家评价表
		var exceptItems = ',w0555,w0573,w0535_,w0537_,committeename,w0571,w0567,w0563,w0565,collegeagree,w0569' +
				',checkproficient,w0527,w0529,w0531,w0533,proficientagree,group_id,w0521,w0523,w0547,w0543,w0545,subjectsagree' +
				',w0557,w0519,w0517,w0553,w0549,w0551,committeeagree,w0559,w0539,w0541,';

		var map = new HashMap();
		map.put("opt","18");
		map.put("type","2");//2代表查询
		map.put("w0301_e",this.w0301_e);
		map.put("evaluationType",this.evaluationType);
		map.put("review_links",this.review_links);
		map.put("userType",jobtitle_reviewconsole.userType);
		var tablePanel = Ext.getCmp('jobtitle_reviewfile_console_'+jobtitle_reviewconsole.review_links+'_tablePanel');
		//公示材料右侧列表排序
		Rpc({
			functionId : 'ZC00002316',
			success : function(res){
				jobtitle_reviewconsole.reloadPersonMap();
				var resultObj = Ext.decode(res.responseText);
				var config = resultObj.msg;
				var nextItems = new Array();
				var columns = tablePanel.columns;
				var w05ItemMap = resultObj.w05ItemMap;
				var cateIdWithNameList = resultObj.cateIdWithNameList;
				jobtitle_reviewconsole.items = new Array();
				for(var i = 0; i < cateIdWithNameList.length; i++) {
					var array = cateIdWithNameList[i].split("_");
					var obj = new Object();
					obj.selected = '0';
					obj.dataValue = array[0];
					var cat_name = "";
					//可能名称中包含_这里不能直接取array[1]
					for(var j = 1; j < array.length; j++) {
						cat_name += "_" + array[j];
					}
					obj.dataName = cat_name.substring(1,cat_name.length);
					jobtitle_reviewconsole.items.push(obj);
				}
				
				// 是否配置过指标顺序，如果为空则为没有保存过。走默认情况
				if(Ext.isEmpty(config)) {
					
					var maps = new HashMap();
					for(var dataIndex in w05ItemMap){
						if(exceptItems.indexOf(','+dataIndex+',') > -1){// 排除指标
							continue ;
						}
						var obj = new Object();
						obj.selected = '0';
						obj.dataValue = dataIndex;
						obj.dataName = w05ItemMap[dataIndex];
						if(defaultSelectedItems.indexOf(dataIndex) > -1){//默认选中
							obj.selected = 1;
						}
						if(Ext.isEmpty(obj.dataName)){//如果指标被隐藏或者已不存在，则会dataname会为空，则不显示出来。
							continue ;
						}
						nextItems.push(obj);
					}
				}else {
					config = ","+config+",";
					var maps = new HashMap();
					for(var dataIndex in w05ItemMap){
						if(config.indexOf(","+dataIndex+",") > -1){//默认选中加入map中，以便排序
							maps.put(dataIndex,w05ItemMap[dataIndex]);
						}
					}
					//将constant中正确的排序加入items中
					var msgArray = config.split(",");
					for(var i = 0; i < msgArray.length; i++) {
						
						var obj = new Object();
						obj.dataValue = msgArray[i];
						obj.dataName = maps.get(msgArray[i]);
						if(Ext.isEmpty(obj.dataName)){//如果指标被隐藏或者已不存在，则会dataname会为空，则不显示出来。
							continue ;
						}
						obj.selected = '1';
						nextItems.push(obj);
					}
					for(var dataIndex in w05ItemMap){
						//if(exceptItems.indexOf(','+dataIndex+',') > -1
						//		||config.indexOf(dataIndex) > -1){// 排除指标
						//	continue ;
						//}
						if(config.indexOf(","+dataIndex+",") > -1) {//如果在config里面，上面的已经给添加了，这里不需要
							continue;
						}
						var obj = new Object();
						obj.selected = '0';
						obj.dataValue = dataIndex;
						obj.dataName = w05ItemMap[dataIndex];
						
						if(Ext.isEmpty(obj.dataName)){//如果指标被隐藏或者已不存在，则会dataname会为空，则不显示出来。
							continue ;
						}
						nextItems.push(obj);
					}
				}
				// 公告维护--选择列表指标
				Ext.require('NoticePath.SelectNoticeField', function() {
					Ext.create("NoticePath.SelectNoticeField", {
						title : zc.reviewconsole.showGroup,
						alternativetitle : zc.reviewconsole.waittingGroup,
						hasSelectTitle : zc.reviewconsole.hasSelectGroup,
						preItems : jobtitle_reviewconsole.items,
						nextItems : nextItems,
						callBackFunc : 'jobtitle_reviewconsole.selectNoticeFieldCallBackFunc'
					});
				});
			},scope:this
		}, map);
	},
	// 公告维护
	selectNoticeFieldCallBackFunc:function(selectedItems,groupValue,nextStr){
		//将集合取出dataValue
		var list = "";
		for(var i = 0; i < selectedItems.length; i++) {
			if(i == 0)
				list = selectedItems[i].get("dataValue");
			else
				list += "," + selectedItems[i].get("dataValue");
		}
		
		var map = new HashMap();
		map.put("list",list);
		map.put("type","1");//1代表新增修改
		// 保存/更新公示材料指标顺序，顺便取出默认通知对象。通知对象为业务范围对应机构。
		var unitIdByBusiList = new Array();// 通知对象集合
		Rpc({functionId:'ZC00003021',async:false,success:function(res){
			var result = Ext.decode(res.responseText);
			unitIdByBusiList = result.unitIdByBusiList;
			jobtitle_reviewconsole.unitIds = result.unitIds;
			
		}},map);
		
		var store = jobtitle_reviewconsole.getReviewfile_console_dataStore();
	    var recordList = store.data.items;
	    
		var contentHtml = jobtitle_reviewconsole.getContentHtml(nextStr, selectedItems);
		// 默认通知对象
		var default_notice_object = new Array();
		var len = unitIdByBusiList.length;
		if(len > 0){
			for(var i=0; i<len; i++){
				var obj = unitIdByBusiList[i];
				default_notice_object.push(obj);
			}
		}
		
		// 公告维护
		Ext.require('NoticePath.Notice',function(){
			Ext.create('NoticePath.Notice', {
				title : '职称公告维护',
				notice_name : '关于' + jobtitle_reviewconsole.meettingName + '的通知',
				notice_content : contentHtml,
				notice_time : '5',
				notice_seq : '1',
				notice_object : '',
				isApproved : true,
				default_notice_object:default_notice_object,
				unitIds:jobtitle_reviewconsole.unitIds
			});
		});
		
	},
	// 公告内容html
	getContentHtml:function(nextStr, selectedItems){
		jobtitle_reviewconsole.index = 0;
		Ext.util.CSS.createStyleSheet(".noBorder div{border-color:#ffffff;}","card_css");
		/** 职称评审人员信息列表表格html */ 
		//border:none 覆盖掉ckeditor 的自带样式
		var widthNum = 650/(selectedItems.length+2);//加2，其中1个是序号列；1个是详情列。
		var thStyle = "border:none;background:#e7e7e7;height:42px; line-height:42px;font-family:'微软雅黑'; font-size:14px;color:#666;width:"+widthNum+"px;max-width:"+widthNum+"px;";
		
		var tableHtml = "";
		tableHtml+='<div class="hj-wzm-table">';
		tableHtml+='<table style="border:1px solid #e5e6e8;border-bottom:none;" width="100%" border="0" cellpadding="0" cellspacing="0">';
		
		// 表格列头
		tableHtml+='<tr>';
		tableHtml+='<th style="'+thStyle+'"; font-size:14px;color:#666;" scope="col">序号</th>';
		var seqItemflag = true;//根据这个标记找第一个排名指标，依据它排序
		var seqItem = "";
		for(var j=0; j<selectedItems.length; j++){
			var text = selectedItems[j].get('dataName');
			var itemName = selectedItems[j].get('dataValue');
			tableHtml+='<th style="'+thStyle+'" scope="col">'+text+'</th>';
			if(seqItemflag && itemName.match(/c_\w+_seq/i)){
				seqItemflag = false;
				seqItem = itemName;
			}
		}
		tableHtml+='<th style="'+thStyle+'" scope="col">详情</th>';
		tableHtml+='</tr>';
		var index = 0;
		// 表格数据
		if(!Ext.isEmpty(seqItem)){
			var personlist = new Array();
			for(var key in jobtitle_reviewconsole.personmap){
				personlist = personlist.concat(jobtitle_reviewconsole.personmap[key]);
			}
			//将人员按排名指标排序
			for(var i = 0; i < personlist.length; i++) {
				var personi = personlist[i];
				for(var j =i+1;j<personlist.length;j++){
					var personj = personlist[j];
					if(personi[seqItem]>personj[seqItem]){
						var temp = personlist[j];
						personlist[j] = personlist[i];
						personlist[i] = temp;
					}
				}
			}
			tableHtml += jobtitle_reviewconsole.generateHtml(nextStr,personlist,index,selectedItems);
		}else{
			for(var key = 0; key < jobtitle_reviewconsole.items.length; key++) {
				var personMap = jobtitle_reviewconsole.personmap[jobtitle_reviewconsole.items[key].dataValue+"_person"];
				tableHtml += jobtitle_reviewconsole.generateHtml(nextStr,personMap,index,selectedItems);
			}
		}
		tableHtml += '</table>';
		tableHtml += '</div >';
		
		/** 公告内容=文字+表格 */
		var d = new Date();
		var contentHtml = '';
		contentHtml+=
			'<div >' +
				'<p>' +
					'全体：<br />' +
					'关于' + jobtitle_reviewconsole.meettingName + '的通知，详细如下：<br />' +
				'</p>' +tableHtml+
				'<p style="text-align:right;">' +
					//'<span">' +//此处加span标签 会引起插入分割线时，ie报错  haosl   20170401   delete
						/*'以上，如有异议请及时提示<br />' +
						'单位名称&nbsp;&nbsp;&nbsp;&nbsp;<br />' +*/
						d.getFullYear()+'年'+(d.getMonth()+1)+'月'+d.getDate()+'日' +
					//'</span>' +
				'</p>' +
			'</div>';
		return contentHtml;
	},
	/**
	 * 校验账号和密码只能输入字母和数字
	 */
	validfunc:function(value){
		if(value == 0) {
			return "只支持输入大于0的值";
		}else {
			var reg = /^[0-9]*$/g;
			if(!reg.test(value))
				return "只支持输入大于0的值";
			else 
				return true;
		}
			
	},
	/**
	 * 校验账号和密码只能输入字母和数字名额，因为名额可以为空，单独写一个
	 */
	validfuncOther:function(value){
		if(value == '') {
			return true;
		}else if(value == 0) {
			return "只支持输入大于0的值";
		}else {
			var reg = /^[0-9]*$/g;
			if(!reg.test(value))
				return "只支持输入大于0的值";
			else 
				return true;
		}
			
	},
	//生辰公示内容表格数据
	generateHtml:function(nextStr,personMap,index,selectedItems){
		var tableHtml = "";
		var widthNum = 650/(selectedItems.length+2);//加2，其中1个是序号列；1个是详情列。
		var tdStyle = "border:none;text-align:center; height:42px; line-height:42px;border-bottom:1px solid #e5e6e8;width:"+widthNum+"px;max-width:"+widthNum+"px;"
		for(var i = 0; i < personMap.length; i++) {
				var person = personMap[i];
				if(nextStr.indexOf(person.categories_id) == -1) {//不再选择的分组里面的不显示
					continue;
				}
				jobtitle_reviewconsole.index = jobtitle_reviewconsole.index+1;
				tableHtml+='<tr>';
				tableHtml+='<td style="'+tdStyle+'">'+jobtitle_reviewconsole.index+'</td>';
				for(var w=0; w<selectedItems.length; w++){
					var itemid = selectedItems[w].get('dataValue');
					var text = person[itemid];
					if(typeof text == 'string' && text.indexOf('`') > -1){
						text = text.split('`')[1];
					}
					tableHtml+='<td style="'+tdStyle+'">'+(text==null?'':text)+'</td>';
					
				}
				var nbasea0100 = person.nbasea0100_e;
				var w0535 = person.w0535;
				var w0536 = person.w0536;
				var isWord = false;
				if(jobtitle_reviewconsole.support_word && !Ext.isEmpty(w0536)){// 支持WORD模板
					isWord = true;
				}
				
				var path = '';
				if(isWord){
					path = encodeURIComponent(w0536);
				} else {
					path = encodeURIComponent(w0535);
				}
				
				//var url = this.getTemplateFileUrl(path, nbasea0100, nbasea0100_1);
		//			tableHtml+="<td style='text-align:center;'><a onclick=window.open('"+url+"');><img src='/images/new_module/icon1.png' /></a></td>";//评审材料
				//添加contenteditable属性，ckeditor中设置这个属性就是只读
				tableHtml+="<td style='"+tdStyle+"' contenteditable='false'>";//评审材料
				if(!Ext.isEmpty(w0535)){// 链接为空时，不加详情图标
					//haosl update 20170420 为链接添加timestamp当前时间的时间戳，区别相同链接
					var ahref = "/module/jobtitle/reviewfile/ViewTemplate.html?path="+path+"&user="+nbasea0100+"&timestamp="+new Date().getTime();
					if(isWord){
						ahref += "&isword=true";
					}
					tableHtml+="<a href='"+ahref+"'><img src='/images/new_module/icon1.png' /></a>";
				}
				tableHtml+="</td>";
				
				tableHtml+='</tr>';
			}
		return tableHtml;
	},
	getMapLength: function(map) {
		var count = 0;
		for (var key in map) {
			if (map.hasOwnProperty(key)) {
				count++;
			}
		}
		return count;
	},
	//将所有双字节变成2个单字节
	getStrLength:function(str){
		
        var n=str.replace(/[^\u0000-\u00ff]/g,"aa").length;
        if(n==null)
            n=0;
        return n;
    },
    //记下单个字符串的长度，和字符串变成双字节的长度，这样可以有效的截取中英文都存在的情况下长度截取问题
    getSubStr:function(str, len) {
    	var singleString = 0;//记下循环的字符串长度
		var doubleString = 0;//双字节，如果长度超出24的，截取单个字节的singleString长度
		for (var i = 0; i < str.length; i++) {//遍历字符串
		      if(/[^\u0000-\u00ff]/g.test(str[i])) {
		    	  doubleString = doubleString + 2;
		      }else {
		    	  doubleString++;
		      }
		      if(doubleString <= len)
		    	  singleString++;
		      else
		    	  break;
	    }
		return str.substr(0, singleString);
    }
});
