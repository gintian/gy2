/**
 * 职称评审_差额投票
 * @createtime August 24, 2017 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('ReviewFileURL.ReviewDiff', {
	requires:["ReviewFileURL.CodeSelectPicker"],
	w0301_e:'',
	review_links:'',
	startReviewFunc:'',
	constructor : function(config) {// 构造方法
		jobtitle_reviewdiff = this;
		this.w0301_e = config.w0301_e;
		this.review_links = config.review_links;
		this.startReviewFunc = config.startReviewFunc;
		
		this.getTableConfig();
	},
	getTableConfig : function() {
		var map = new HashMap();
		map.put("type", '7');
		map.put("w0301_e", jobtitle_reviewdiff.w0301_e);
		map.put("review_links", jobtitle_reviewdiff.review_links);
		Rpc({functionId : 'ZC00003023',async : false,success : jobtitle_reviewdiff.showTableGrid,scope:jobtitle_reviewdiff}, map);
	},
	showTableGrid:function(form){
		var reviewfilediffwin = Ext.getCmp('reviewfilediffwin');
		if(reviewfilediffwin){
			reviewfilediffwin.removeAll();
		}
		
		var responseText = Ext.decode(form.responseText);
		jobtitle_reviewdiff.personmap = responseText.personmap;
		jobtitle_reviewdiff.categoriesmap = responseText.categoriesmap;
		jobtitle_reviewdiff.w0575codesetid = responseText.w0575codesetid;
		jobtitle_reviewdiff.ctrl_param = responseText.ctrl_param;
		var tableGrid = new BuildTableObj(responseText.tableConfig);
		if(reviewfilediffwin){
			reviewfilediffwin.add(tableGrid.getMainPanel());
			return ;
		}
		
		
		var title = '';
		if(jobtitle_reviewdiff.review_links == '1'){
			title = '评委会阶段';
		}else if(jobtitle_reviewdiff.review_links == '2'){
			title = '学科组阶段';
		}else if(jobtitle_reviewdiff.review_links == '3'){
			title = '同行专家阶段';
		}else if(jobtitle_reviewdiff.review_links == '4'){
			title = '二级单位评议阶段';
		}
		
		Ext.create('Ext.window.Window', {
			title:title+'投票',
			id:'reviewfilediffwin',
			layout: 'fit',
			modal: true,
			width:document.documentElement.clientWidth,
			height:document.documentElement.clientHeight,
			border:false,
			autoScroll:false,
		    items: [tableGrid.getMainPanel()],
		    listeners:{
	         	beforeshow:function(){
	         		jobtitle_reviewdiff.asyncTableCategories();// 同步申报人员分类表
	         		
	         		// 栏目设置只需要保存列宽即可，现把栏目这是按钮隐藏掉。
	         		var diff_schemeBtn = Ext.getCmp('jobtitle_reviewfile_diff_'+jobtitle_reviewdiff.review_links+'_schemeBtn');
	         		if(diff_schemeBtn){
	         			diff_schemeBtn.hide();
	         		}
	         	},
	         	render:function(){
	         		/*window.setInterval(function(){//20秒刷新进度、状态
						var map = new HashMap();
						map.put("type", '15');
						map.put("w0301_e", jobtitle_reviewdiff.w0301_e);
						map.put("review_links", jobtitle_reviewdiff.review_links);
						Rpc({functionId : 'ZC00003023',async:false,success:function(form){
							var progressstatemap = Ext.decode(form.responseText).progressstatemap;
							
							var store = jobtitle_reviewdiff.getReviewfile_diff_dataStore();
							
							for(var p in progressstatemap){
								if(progressstatemap.hasOwnProperty(p)){
									var progress = progressstatemap[p].split('_')[0];
									var approval_state = progressstatemap[p].split('_')[1];
									
									var record = store.findRecord('categories_id_e', p);
									if(record){
										record.set('progress', progress);
										record.set('approval_state', approval_state);
										//record.commit();
									}
								}
							}
							
						},scope:this}, map);          
			        }, 20000);*/
			        
			        Ext.EventManager.onWindowResize(function(w,h){ 
						var reviewfilediffwin = Ext.getCmp('reviewfilediffwin');
						if(reviewfilediffwin){
							var width = window.parent.window.document.getElementById('center_iframe').offsetWidth;
							var height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
							reviewfilediffwin.setWidth(width);
							reviewfilediffwin.setHeight(height);
						}
					},this); 
	         	},
	         	scope:this
	         }
		}).show();
	},
	// 同步申报人员分类表（zc_personnel_categories）表
	asyncTableCategories : function(){
		var map = new HashMap();
		map.put("type", '12');
		Rpc({functionId : 'ZC00003023',async:false,success:function(form){
			var errorcode = Ext.decode(form.responseText).errorcode;
			if(errorcode == 1){
				// 留出同步失败的接口，先不提示
			}
		},scope:this}, map);
	},
	// 应选人数设置
	setPersonNum : function(ids){
		var map = new HashMap();
		map.put("type", '5');
		map.put("w0301_e", jobtitle_reviewdiff.w0301_e);
		map.put("review_links", jobtitle_reviewdiff.review_links);
		map.put("ids", ids);
		Rpc({functionId : 'ZC00003023',async:false,success:function(form){
			var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
			if(errorcode == 0){
				jobtitle_reviewdiff.getTableConfig();
			} else {
				// 留出失败的接口，先不提示
			}
		},scope:this}, map);
	},
	// 新建分组
	createCategorie:function(){
		var store = jobtitle_reviewdiff.getReviewfile_diff_dataStore();
		store.on('load', jobtitle_reviewdiff.createCategorie_after, this, {single:true});
		jobtitle_reviewdiff.saveInfo(false);
	},
	createCategorie_after : function() {
		
		var map = new HashMap();
		map.put("type", "2");
		map.put("w0301_e", jobtitle_reviewdiff.w0301_e);
		map.put("review_links", jobtitle_reviewdiff.review_links);
		map.put("categories_name", '');
		Rpc({
			functionId : 'ZC00003023',
			async : false,
			success : function(form) {
				var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
				if (errorcode == 0) {
					jobtitle_reviewdiff.loadStore(-1);
				}
			},
			scope : this
		}, map);
		
		/*var formPanel = Ext.create('Ext.form.Panel', {
			width:320,
			//width:320,
		    //bodyPadding: '20',
		    margin:'0 auto',
			border:false,
		    layout: 'anchor',
		    defaults: {
		        anchor: '80%'
		    },
		    defaultType: 'textfield',
		    items: [{
		    	id:'categoriename',
		        fieldLabel: "分组名称",
		        labelSeparator:'',
		        beforeLabelTextTpl:"<font color='red'> * </font>",
		        labelAlign:'right',
		        maxLength:50,
		        allowBlank: false,
		        margin:'15 0 15 0',
                validator: jobtitle_reviewdiff.validateCategorieName// 校验重名
		    }],
		    buttonAlign:'center',
		    buttons : [{
				text : '确定',
				handler : function() {
					var value = (Ext.getDom('categoriename-inputEl').value)
							.replace(/(^\s*)|(\s*$)/g, "");// 名称不能为空或只为空格
					if (Ext.isEmpty(value)) {
						Ext.getDom('categoriename-inputEl').value = '';
					}
					var form = this.up('form').getForm();
					if (form.isValid()) {
						var values = form.getValues();
						var me = this;
						var map = new HashMap();
						map.put("type", "2");
						map.put("w0301_e", jobtitle_reviewdiff.w0301_e);
						map.put("review_links", jobtitle_reviewdiff.review_links);
						map.put("categories_name",keyWord_filter(values['categoriename-inputEl']));
						Rpc({
							functionId : 'ZC00003023',
							async : false,
							success : function(form) {
								var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
								if (errorcode == 0) {
									jobtitle_reviewdiff.loadStore();
									Ext.showAlert('添加成功！');
									this.up('window').close();
								}else {
									Ext.showAlert('添加失败！');
								}
							},
							scope : this
						}, map);
					}
				}
			}, {
				text : '取消',
				handler : function() {
					this.up('window').close();
				}
			}]
		});
		
		Ext.create('Ext.window.Window', {
			title : '新建分组',
			width : 350,
			height : 140,
			layout:{
				type:'fit'
			},
			modal : true,
			border : false,
			items : [formPanel]
		}).show();*/
	},
	// 保存
	saveInfo : function(showmsg){
		var store = jobtitle_reviewdiff.getReviewfile_diff_dataStore();
	    var updaterecord = [];
		var updateList = store.getUpdatedRecords();//修改过的数据
    	if(updateList.length>0){
    		for(var i=0;i<updateList.length;i++){
    			var record = updateList[i].data;
    			
    			// 校验应选人数：应选人数不能大于所选人数、不能小于0
    			var msg = jobtitle_reviewdiff.checkPersonNum(record);
    			if(!Ext.isEmpty(msg)){
    				Ext.showAlert(msg);
    				return ;
    			}
    			
				updaterecord.push(record);
			}
    	}
    	
    	var map = new HashMap();
		map.put("type", "3");
		map.put("w0301_e", jobtitle_reviewdiff.w0301_e);
		map.put("review_links", jobtitle_reviewdiff.review_links);
        map.put("savedata",updaterecord);
        Rpc({functionId:'ZC00003023',sync:false,scope:this,success:function(form){
				var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
				if (errorcode == 0) {
					jobtitle_reviewdiff.loadStore();
					if(showmsg){
						Ext.showAlert('保存成功！');
					}
				}else {
					if(showmsg){
						Ext.showAlert('保存失败！');
					}
				}
			
		}},map);
	},
	// 刷新
	loadStore : function(y){
		var tablePanel = Ext.getCmp('jobtitle_reviewfile_diff_'+jobtitle_reviewdiff.review_links+'_tablePanel');
		var scrollY = tablePanel.getView().getScrollY();
		if(!Ext.isEmpty(y)){
			scrollY = y;
		}
		
		var store = jobtitle_reviewdiff.getReviewfile_diff_dataStore();
		store.on('load', function(){
			tablePanel.getView().setScrollY(scrollY);
		}, this, {single:true});
		store.load();
	},
	// 添加分组-自定义校验
	validateCategorieName : function(){
		var flag = true;

		var inputName = this.getValue();
		var store = jobtitle_reviewdiff.getReviewfile_diff_dataStore();
		var records = store.data.items;
		for(var i=0; i<records.length; i++){
			var name = records[i].data.name;
			if(name == inputName){
				flag = "该名称已被使用！";
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
		var expert_count = arguments[2].data.expert_count;
		var expert_already_count = arguments[2].data.expert_already_count;
		var tablePanelColumns = Ext.getCmp('jobtitle_reviewfile_diff_'+jobtitle_reviewdiff.review_links+'_tablePanel').columns;
		var c_level = tablePanelColumns[arguments[4]].dataIndex.split('_')[1];
		var key = categories_id_e+"_"+c_level;
		
		
		
		
		// 人员显示
		var html = '<div style="float:left;">';
			html += jobtitle_reviewdiff.createPerson(key, approval_state);
		
		if(approval_state == '1' || approval_state == '2'){//执行中、结束状态，不能再加人
			return html;
		}
		//html += '</div>';
		// 添加显示
		//html += '<div style="float:left;cursor:pointer;">';
		html += '<table style="border:none;float:left;margin:5px 0 0 0;"><tr><td>';
			html += "<a id='a_"+categories_id_e+"_"+c_level+"' onclick='javascript:jobtitle_reviewdiff.selectperson(this);'>" +
						"<img style='width:48px;height:48px;' src='/images/new_module/nocycleadd.png' border=0>" +
					"</a>";
			html += '</td></tr></table>';
		html += '</div>';
		
		return html;
	},
	selectperson :function(el){
		jobtitle_reviewdiff.saveInfo(false);
		
		var categories_id_e = el.id.split('_')[1];
		var c_level = el.id.split('_')[2];
		
		var map = new HashMap();
		map.put("type", '13');
		map.put("w0301_e", jobtitle_reviewdiff.w0301_e);
		map.put("review_links", jobtitle_reviewdiff.review_links);
		Rpc({functionId : 'ZC00003023',async : false,success : function(form){
			var responseText = Ext.decode(form.responseText);
			var tableGrid = new BuildTableObj(responseText.tableConfig);
			
			Ext.create('Ext.window.Window', {
				title:'请选择申报人',
				id:'selectperson',
				layout: 'fit',
				modal: true,
				width:800,
				height:500,
				border:false,
				autoScroll:false,
			    items: [tableGrid.getMainPanel()],
			    buttonAlign:'center',
			    buttons:[{
					xtype : 'button',
					text : common.button.ok,
					margin:'20 10 0 0',
					handler : function() {jobtitle_reviewdiff.addperson(categories_id_e, c_level);}
				},{
					xtype : 'button',
					text : "取消",
					margin:'20 0 0 0',
					handler : function() {this.up('window').close()}
				}]
			}).show();
			
		},scope:this}, map);
	},
	addperson:function(categories_id_e, c_level){
		var w0501_eList = new Array();//人员信息集

		
		var selectData = Ext.getCmp('jobtitle_reviewfile_diff_selperson_tablePanel').getSelectionModel().getSelection();//获取数据
		for(var p in selectData){
			if(selectData.hasOwnProperty(p)){
				var w0501_e = selectData[p].data.w0501_e;
				w0501_eList.push(w0501_e);
			}
		}
		/** 获取的是选择的数据 */
		if(w0501_eList.length == 0){//如果没选，不允许【确定】
			return ;
		}
		
		var map = new HashMap();
		map.put("type", "10");
		map.put("categories_id_e", categories_id_e);
		map.put("c_level", c_level);
		map.put("w0501_eList", w0501_eList);
		Rpc({
			functionId : 'ZC00003023',
			async : false,
			success : function(form) {
				var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
				if (errorcode == 0) {
					jobtitle_reviewdiff.reloadPersonMap();
					jobtitle_reviewdiff.loadStore();
					Ext.getCmp('selectperson').close();
					/*Ext.showAlert('添加成功！',function(){
					});*/
				}else {
					Ext.showAlert('添加失败！');
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
		
		var startStyle = '',reStartStyle = '',stopStyle = '';
		if(approval_state == '1' || approval_state == '2'){//启动/结束时，显示重新启动图标
			startStyle = 'display:none;';
			reStartStyle = 'display:inline;';
			stopStyle = 'display:inline;';
			if(approval_state == '2'){
				stopStyle = 'display:none;';
			}
		} else {
			startStyle = 'display:inline;';
			reStartStyle = 'display:none;';
			stopStyle = 'display:none;';
		}

		var html = '<div>';
		var style = 'background-image:url(/images/new_module/reviewdiff.png);width:16px;height:16px;border:0;margin:0 5px 0 0;float:left;cursor:pointer;';
		
		html += "<div style='background-position: 0 0;"+startStyle+style+"' title='启动投票' onclick=javascript:jobtitle_reviewdiff.operation(1,'"+categories_id_e+"','"+name+"');>" +
				"</div>";
				
		html += "<div style='background-position:16px 0;"+reStartStyle+style+"' title='重新启动' onclick=javascript:jobtitle_reviewdiff.operation(2,'"+categories_id_e+"','"+name+"');>" +
				"</div>";
				
		html += "<div style='background-position:16px 32px;"+stopStyle+style+"' title='暂停投票' onclick=javascript:jobtitle_reviewdiff.operation(6,'"+categories_id_e+"','"+name+"');>" +
				"</div>";
		/*html += "<a title='投票分析（暂不可用）' href=javascript:jobtitle_reviewdiff.operation(3);>" +
					"<img style='background-position: 0 16px;"+style+"'>" +
				"</a>";*/
				
		html += "<div style='display:inline;background-position: 16px 48px;"+style+"' title='删除' onclick=javascript:jobtitle_reviewdiff.operation(4,'"+categories_id_e+"');>" +
				"</div>";
		
		html += "<div style='background-position:0px 32px;"+reStartStyle+style+"' title='统计票数' onclick=javascript:jobtitle_reviewdiff.operation(5,'"+categories_id_e+"');>" +
				"</div>";
		
		html += '</div>';
		
		return html;
	},
	// 进度
	progress : function(value, metaData, record){
		var categories_id_e = record.data.categories_id_e;
		var expertnum = record.data.expertnum;
		var submitnum = record.data.submitnum;
		
		var title = jobtitle_reviewdiff.categoriesmap[categories_id_e];
		if(Ext.isEmpty(title)){
			title = '';
		}
		
		var html = '';
		html = "<span title='"+title+"'>"+submitnum+'/'+expertnum+"</span>";
		return html;
	},
	// 操作点击
	operation : function(opt, categories_id_e, name){
		if(opt == 1 || opt == 2){// 启动/重新启动
			// check 
			if(Ext.isEmpty(name)){
				Ext.showAlert('请输入分组名称！');
				return ;
			}
			var personflag = false;
			for(var p in jobtitle_reviewdiff.personmap){
				if(jobtitle_reviewdiff.personmap.hasOwnProperty(p)){
					if(p.indexOf(categories_id_e+'_') == 0){
						var personArray = jobtitle_reviewdiff.personmap[p];
						if(personArray.length > 0){
							personflag = true;
						}
					}
				}
			}
			if(!personflag){
				Ext.showAlert('请选择申报人！');
				return ;
			}
			
			// start
			var confirmInfo = '是否'+(opt == 2?'重新':'')+'启动'+name+'的投票工作？';
			Ext.showConfirm(confirmInfo, function(btn){
				if(btn == 'yes'){
					var dataList = new Array();
					for(var p in jobtitle_reviewdiff.personmap){
						if(jobtitle_reviewdiff.personmap.hasOwnProperty(p)){
							if(p.indexOf(categories_id_e+'_') == 0){
								var personArray = jobtitle_reviewdiff.personmap[p];
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
					jobtitle_reviewdiff.startReviewFunc(dataList, categories_id_e);
					jobtitle_reviewdiff.reloadPersonMap();
					jobtitle_reviewdiff.loadStore();
				}
			}, this);
			
		} else if(opt == 3){// 投票分析
			// 暂不处理
			
		} else if(opt == 4){// 删除分类
			Ext.showConfirm('确认删除该分组？', function(btn){
				if(btn == 'yes'){
					jobtitle_reviewdiff.categories_id_e = categories_id_e; 
					var store = jobtitle_reviewdiff.getReviewfile_diff_dataStore();
					store.on('load', jobtitle_reviewdiff.deleteCategories, this, {single:true});
					jobtitle_reviewdiff.saveInfo(false);
				}
			}, this);
		} else if(opt == 5){//统计票数
			jobtitle_reviewdiff.reloadPersonMap();
			jobtitle_reviewdiff.loadStore();
		} else if(opt == 6){//暂停
			Ext.showConfirm('是否暂停'+name+'的投票工作？', function(btn){
				if(btn == 'yes'){
					var map = new HashMap();
					map.put("type", '8');
					map.put("categories_id_e", categories_id_e);
					Rpc({
						functionId : 'ZC00003023',
						async : false,
						success : function(form) {
							var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
							if (errorcode == 0) {
								jobtitle_reviewdiff.loadStore();
							}else {
							}
						},
						scope : this
					}, map);
				}
			
			}, this);
		}
		
	},
	deleteCategories:function(){
		var map = new HashMap();
			map.put("type", '4');
			map.put("categories_id_e", jobtitle_reviewdiff.categories_id_e);
			Rpc({
				functionId : 'ZC00003023',
				async : false,
				success : function(form) {
					var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
					if (errorcode == 0) {
						jobtitle_reviewdiff.loadStore();
						//Ext.showAlert('删除成功！');
					}else {
						//Ext.showAlert('删除失败！');
					}
				},
				scope : this
			}, map);
	},
	approval_state : function(value){
		var html = '';
		
		if(value == '0'){
			html = '未启动';
		} else if(value == '1'){
			html = '已启动';
		} else if(value == '2'){
			html = '已结束';
		} else if(value == '3'){
			html = '已暂停';
		}
		
		return html;
	},
	createPerson:function(key, approval_state, expert_count, expert_already_count){
		var mouseAction = '';
		if(approval_state == '1' || approval_state == '2'){//执行中、结束状态，不能再删除人
			mouseAction = '';
		}else {
			mouseAction = 'onMouseOver="jobtitle_reviewdiff.showHideDel(1,this);" onMouseOut="jobtitle_reviewdiff.showHideDel(2,this);"'
		}
		
		var personArray = jobtitle_reviewdiff.personmap[key];
		
		var html = '';
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
				var expert_count = person.expert_count;//一共几个投票人
				var expert_already_count = person.expert_already_count;//赞成票数有几个
				var ispass = person.ispass;//赞成票数有几个
				
				var pass_style = 'display:none;';
				if(ispass =='01' && (approval_state == '1' || approval_state == '2')){
					pass_style = 'display:block;';
				}
				
				if(approval_state == '1' || approval_state == '2'){
					
				}else {//不是启动、结束状态，赞成人数隐藏。显示0
					expert_already_count = 0;
				}
				
				var title = '';
					title += '姓名：'+w0511+'\n';
					title += '单位：'+w0507+'\n';
					title += '部门：'+w0509+'\n';
					title += '现聘职称：'+w0513+'\n';
					title += '申报职称：'+w0515+'\n';
				
				var id = key +'_'+w0501;
				html += '<table style="border:none;float:left;">';
					html += '<tr><td style="position:relative;" id="td_'+id+'" '+mouseAction+' title="'+title+'">' +
							'	<img width=50 height=50 style="border-radius: 50%;margin:5px" src='+imgpath+'>' +
							'	<img id="del_'+id+'" onclick="jobtitle_reviewdiff.delPerson(this);" width=20 height=20 src=/workplan/image/remove.png style="cursor:pointer;position:absolute;top:0;left:40px;display:none;">' +
							'	<img width=20 height=20 src=/images/new_module/xuanzhong.png style="position:absolute;left:20px;top:40px;width:24px;height:24px;'+pass_style+'">'+
							'</td></tr>';
					html += '<tr><td style="text-align:center;font-size:13px;">'+w0511+'<span >' ;
								if(approval_state == '1' || approval_state == '2'){//启动和结束状态时，名称后增加（1/2）
									html += '('+expert_already_count+'/'+expert_count+')';
								}
							html +='</span></td></tr>';
				html += '</table>';
			}
		}
		return html;
	},
	reloadPersonMap : function(){
		var map = new HashMap();
		map.put("type", '7');
		map.put("w0301_e", jobtitle_reviewdiff.w0301_e);
		map.put("review_links", jobtitle_reviewdiff.review_links);
		Rpc({functionId : 'ZC00003023',async : false,success : function(form){
			var responseText = Ext.decode(form.responseText);
			jobtitle_reviewdiff.personmap = responseText.personmap;
			jobtitle_reviewdiff.categoriesmap = responseText.categoriesmap;
		},scope:this}, map);
	},
	getReviewfile_diff_dataStore:function(){
		return Ext.data.StoreManager.lookup('jobtitle_reviewfile_diff_'+jobtitle_reviewdiff.review_links+'_dataStore');
	},
	showHideDel:function(state, td){
		var arr = td.id.split('_');
		var delId = 'del_'+arr[1]+'_'+arr[2]+'_'+arr[3];
		if(Ext.getDom(delId)){
			if(state == 1){
				Ext.getDom(delId).style.display = 'block';
			} else if(state == 2){
				Ext.getDom(delId).style.display = 'none';
			}
		}
	},
	delPerson:function(delImg){
		Ext.showConfirm('确认删除该申报人？', function(btn){
				if(btn == 'yes'){
					var arr = delImg.id.split('_');
					var categories_id_e = arr[1];
					var c_level = arr[2];
					var w0501 = arr[3];
					
					var map = new HashMap();
					map.put("type", '11');
					map.put("categories_id_e", categories_id_e);
					map.put("c_level", c_level);
					map.put("w0501", w0501);
					Rpc({functionId : 'ZC00003023',async : false,success : function(form){
						var errorcode = Ext.decode(Ext.decode(form.responseText).errorcode);
						if (errorcode == 0) {
							jobtitle_reviewdiff.reloadPersonMap();
							jobtitle_reviewdiff.loadStore();
						}else {
						}
					},scope:this}, map);
				}
		});
	},
	checkCell : function(record) {
		var approval_state = record.data.approval_state;
		if(approval_state == '1' || approval_state == '2'){//执行中、结束状态，不能编辑
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
				var inputNum = record[p];
				var categories_id_e = record.categories_id_e;
				
				var p = c_level;
				if(c_level == 'number'){
					p = 'person';
				}
				var key = categories_id_e+"_"+p;
				var personArray = jobtitle_reviewdiff.personmap[key];
				if(personArray){
					var personNum = personArray.length;
					if(inputNum < 0){
						msg = '应选人数不能小于0！';
					} else if(inputNum > personNum){
						msg = '应选人数不能大于申报人数！';
					}
				}
			}
			
		}
		
		return msg;
	}
});
