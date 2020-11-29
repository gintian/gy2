/**
 * 职称评审_委员会管理_学科组列表模式
 * @createtime 2016-7-27
 * @author liuy
 * 
 * */
Ext.define('JobtitleSubjects.SubjectsList',{
	addVersion:false,//新增权限
	editVersion:false,//编辑权限
	deleteVersion:false,//删除权限
	showHistoryVersion:false,//显示历史
	subjectsListPanelIsExpand:true,//学科组区域是否是展开状态
	constructor:function(config) {
		subjectsList_me = this;
		subjectsListGloble = {};
		subjectsListGloble.ishistory = '0';//是否显示历史(学科组)
		subjectsListGloble.year = '',//显示某年的学科组
		subjectsListGloble.isshowall = '0';//是否显示历史（学科组成员）
		subjectsListGloble.currentGroup_Id = '';//当前选中的学科组编号
		subjectsListGloble.pageNum = 0;//总页数
		subjectsListGloble.currentPage = 1;//当前页码
		subjectsListGloble.currentPageClickState = '';//当前触发翻页的按钮  1、左 2、右
		subjectsListGloble.historyState="1"//历史学科组 标记  =1 非历史  0=历史
		subjectsListGloble.subjectslist="";
		leader = [{'dataValue':'0','dataName':'否'}, {'dataValue':'1','dataName':'是'}];
		this.init();
	},
	//初始化函数
	init:function() {
		var me = this;
		me.getSubjectsList();//获得所有学科组，并为全局变量subjectsListGloble.subjectslist赋值
		me.createSelfCss();
		var group_id = me.getFirstGroupId();
		me.getSubjectsPerson(group_id, subjectsListGloble.isshowall);
	},
	//第一条学科组记录
	getFirstGroupId:function(){
		var group_id = '';//学科组编号
		var groupList = subjectsListGloble.subjectslist;
		for(var p in groupList){
			var group = groupList[p];
			group_id = group.group_id;
			subjectsListGloble.currentGroup_Id = group_id;
			break;
		}
		return group_id;
	},
	//获取学科组列表信息
	getSubjectsList:function(){
		var map = new HashMap();
		map.put("ishistory", subjectsListGloble.ishistory);
		map.put("year", subjectsListGloble.year);
		Rpc({functionId:'ZC00002201',async:false,success:function(data){
			subjectsListGloble.subjectslist = Ext.decode(data.responseText).subjectslist;
			this.addVersion = Ext.decode(data.responseText).addVersion;
			this.editVersion = Ext.decode(data.responseText).editVersion;
			this.deleteVersion = Ext.decode(data.responseText).deleteVersion;
			this.showHistoryVersion = Ext.decode(data.responseText).showHistoryVersion;
		},scope:this},map);
	},
	// 加载人员信息
	getSubjectsPerson:function(group_id, isshowall){
		var me = this;
		var map = new HashMap();
		map.put("group_id", group_id);
		map.put("isshowall", isshowall);
	    Rpc({functionId:'ZC00002217', async:false, success:subjectsList_me.getPersonOK, scope:this},map);
	},
	// 获取主页面
	getPersonOK:function(form, action){
		var me = this;
		var result = Ext.decode(form.responseText);
		subjectsListGloble.historyState = result.state;


        subjectsList_me.func=result.func;
        subjectsList_me.orgid=result.orgid;

		if(typeof subjectsList_tableObj !== 'undefined' && subjectsList_tableObj){
			if(Ext.getCmp("subjectsList_newPerson")){
				var randomChoose = Ext.getCmp("subjectsList_randomChoose");//haosl 20160903
				var newPerson = Ext.getCmp("subjectsList_newPerson");
				var deletePerson = Ext.getCmp("subjectsList_deletePerson");
				var saveInfo = Ext.getCmp("subjectsList_saveInfo");
				if(subjectsListGloble.historyState=="1"){
					if(newPerson)
						newPerson.setDisabled(false);
					if(randomChoose)
						randomChoose.setDisabled(false);
					if(deletePerson)
						deletePerson.setDisabled(false);
					if(saveInfo)
						saveInfo.setDisabled(false);
				}else{
					if(newPerson)
						newPerson.setDisabled(true);
					if(randomChoose)
						randomChoose.setDisabled(true);
					if(deletePerson)
						deletePerson.setDisabled(true);
					if(saveInfo)
						saveInfo.setDisabled(true);
				}
			}
			subjectsList_me.loadTable();
			return ;
		}
		var jsonData = result.tableConfig;
		var obj = Ext.decode(jsonData);
		obj.openColumnQuery = true;//haosl 20161014 方案查询可以查询自定义指标
		subjectsList_tableObj = new BuildTableObj(obj);
		//自动适应 haosl 2016-06-28
		subjectsList_tableObj.mainPanel.on("resize",function(){
			subjectsList_me.autoResize();
		});
		
		var leftImg = Ext.create('Ext.Img', {
			id:'leftImg',
			src: "/images/new_module/left.png",
			style:'cursor:pointer;',
			width :20,
			height:60,
			margin:'20 0 0 0',
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){ subjectsList_me.leftPage(); }//左翻页
		        }
			}
		});

		var rightImg = Ext.create('Ext.Img', {
			id:'rightImg',
			src: "/images/new_module/right.png",
			style:'cursor:pointer;',
			width :20,
			height:60,
			margin:'20 0 0 0',
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){ subjectsList_me.rightPage(); }//右翻页
		        }
			}
		});
		
		var wholePanel = Ext.widget('panel',{
			id:'wholePanel',
			height:90,
			layout:{type:'hbox'},
			padding:0,
			margin: 0,
			border:false,
			bodyBorder:false,
			style:'border-bottom:1px solid #C5C5C5;',
			dockedItems: [{
			    xtype: 'toolbar',
			    dock: 'left',
			    items: [leftImg]
			},{
			    xtype: 'toolbar',
			    dock: 'right',
			    items: [rightImg]
			}],
			items:[]
		});
		
		subjectsList_tableObj.insertItem(wholePanel, 0);
		subjectsList_me.getSubjects();
		subjectsList_me.createShowAllPanel();//是否显示全部
		
		
		//创建时间的数据源
		var store = Ext.create('Ext.data.Store', {
			fields: ['value','name'],
			proxy:{
				type: 'transaction',
		        functionId:'ZC00002201',
				extraParams:{
					ishistory:subjectsListGloble.ishistory
				},
				 reader: {
					  type: 'json',
					  root: 'yearList'
				}
			},
			autoLoad: true
		});
		var comboflag = subjectsListGloble.ishistory=='1'?false:true;
		var tools = Ext.widget('container', {
			layout: {
		        type: 'hbox'
		    },
			items: [{
		    	xtype: 'combo',
		    	id:'create_time_history',
		    	store:store,
		    	editable:false,
		    	fieldLabel:'创建时间',
		    	labelWidth:50,
		    	labelAlign:'right',
		    	hidden:comboflag,
		        forceSelection :true,
		        valueField: 'value',
		        displayField: 'name',
		        margin:'0 10 0 0',
		        listeners: {
					change: {
						fn: function(a, v){
							subjectsListGloble.year = v;
							subjectsListGloble.currentPage = 1;
							//刷新学科组列表 haosl 2017-06-28
							subjectsList_me.getSubjectsList();
							me.getSubjects();
						}
					}
				}
			},{
		        xtype: 'checkboxfield',
		        boxLabel:'显示历史',
		        id:'ishistory',
		        margin:'0 10 0 0',
				listeners: {
					change: {
						fn: function(a, v){
							//没有显示历史的权限时，提示用户没有权限  haosl 20160901
							if(!subjectsList_me.showHistoryVersion){
								var checkBox = Ext.getCmp("ishistory");
								checkBox.setValue(false);
								Ext.showAlert("对不起，无操作权限！");
								return ;
							}
							if(v){
								subjectsListGloble.ishistory = "1";
								var combo = Ext.getCmp("create_time_history");
					            combo.setHidden(false);
							}else{
								subjectsListGloble.ishistory = "0";
								var combo = Ext.getCmp("create_time_history");
					            combo.setHidden(true);
					            subjectsListGloble.year = "";
							}
							//刷新学科组列表 haosl 2017-06-28
							subjectsList_me.getSubjectsList();
							me.getSubjects();
							//重新定位到第一个  haosl 20170620 start
							var old_group_id = subjectsListGloble.currentGroup_Id;
							me.getFirstGroupId();//方法内改变了当前学科组id
							me.getSubjectsPerson(subjectsListGloble.currentGroup_Id, subjectsListGloble.isshowall);
							var pho = Ext.getCmp("pho_"+subjectsListGloble.currentGroup_Id);
							var phoOld = Ext.getCmp("pho_"+old_group_id);
							if(phoOld)
								phoOld.setSrc("/images/new_module/committee.png");//移除选中样式
							if(pho)
								pho.setSrc("/images/new_module/committee-sel.png");
							//重新定位到第一个  haosl 20170620 end
						}
					}
				}
		     }]
		});
		
		var cardview = Ext.create('Ext.Img', {//卡片模式
			src: "/images/new_module/cardview.png",
			id:'cardview',
			width:16,
			height:16,
			title:zc.msg.cardview,
			margin:'5 5 0 0',
			style:'cursor:pointer;',
			scope:this,
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(){
		            	subjectsList_tableObj.getMainPanel().destroy();
	                    Ext.require('JobtitleSubjects.Subjects',function(){
	                        var req = Ext.create('JobtitleSubjects.Subjects',{});
	                    });
		            }
		        }
			}
		});
		
		var collapseOrExpand = Ext.create('Ext.Img', {//展开、收起按钮
			src: "/images/new_module/collapse_gray.png",
			id:'collapseOrExpand',
			width:16,
			height:16,
			margin:'0 0 0 5',
			scope:this,
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(){
		            	var img = Ext.getCmp(arguments[1].id);
		            	var wholePanel = Ext.getCmp('wholePanel');
		            	if(me.subjectsListPanelIsExpand){//展开状态
		            		wholePanel.setHeight(0);//收起
		            		img.setSrc('/images/new_module/expand_gray.png');
		            		me.subjectsListPanelIsExpand = false;
		            	} else {//收起状态
		            		wholePanel.setHeight(100);//展开
		            		img.setSrc('/images/new_module/collapse_gray.png');
		            		me.subjectsListPanelIsExpand = true;
		            	}
		            }
		        }
			}
		});
		subjectsList_tableObj.bodyPanel.queryById('titleBar').add(tools).add(cardview);
		subjectsList_tableObj.bodyPanel.addTool(collapseOrExpand);//在栏目设置按钮后面
		
		subjectsList_tableObj.tablePanel.findPlugin('cellediting').on("edit",function(edit,e){
			if(e.field == 'start_date') {//判断起始时间不能大于结束时间
				var end_date = e.record.data.end_date;
				var start_date = e.value;
				if(end_date != '' && end_date != '' && end_date < start_date) {
					me.loadStore();
					Ext.showAlert(zc.label.endThanStart);
					return ;
				}
			}else if(e.field == 'end_date'){//判断结束时间不能大于起始时间
				var start_date = e.record.data.start_date;
				var end_date = e.value;
				if(start_date != '' && end_date != '' && end_date < start_date) {
					me.loadStore();
					Ext.showAlert(zc.label.endThanStart);
					return ;
				}
			}
		});
	},
	loadStore:function() {
		var store = Ext.data.StoreManager.lookup('jobtitle_subject_dataStore');
		store.load();
	},
	// 组长列渲染
	role:function(){
		var value = arguments[0];
		var record = arguments[2];
		var id = "check_" + record.data.w0101_e;
		var flag = record.data.flag;
		var checked = '';
		var disabled="disabled='disabled'";
		if(value == 1){
			checked = "checked=checked";//选中
		}
		//非历史学科组下的可聘任专家才能维护组长
		if(flag && flag.length>0 && flag.split("`")[0]=="1" && subjectsListGloble.historyState=="1"){
			disabled = '';
		}
		var html = "<input id='"+id+"' "+disabled+" style='float:left;' onclick='subjectsList_me.setGrouLeader(this);' type='checkbox' "+checked+" name='checkbox'/>" +
						"<label style='float:left;margin-left:3px;margin-top:3px;' for='" +id+"'>设置" +
					"</label>";
		return html;
		
	},
	//左翻页
	leftPage:function(){
		if(subjectsListGloble.currentPage == 1){//当前页码等于1，左翻页 禁用
			return ;
		}
		subjectsListGloble.currentPage = subjectsListGloble.currentPage - 1;
		subjectsListGloble.currentPageClickState = '1';//更新点击状态 左翻页
		subjectsList_me.getSubjects();
	},
	//右翻页
	rightPage:function(){
		if(subjectsListGloble.currentPage == subjectsListGloble.pageNum){//当前页码等于总页码，右翻页 禁用
			return ;
		}
		subjectsListGloble.currentPage = subjectsListGloble.currentPage + 1;
		subjectsListGloble.currentPageClickState = '2';//更新点击状态 右翻页
		subjectsList_me.getSubjects();
	},
	//创建学科组区域
	getSubjects:function(){
		var me = this;
		var top = Ext.getCmp('topPanel');
		if(top){
			Ext.getCmp('wholePanel').remove(top);
		}
		var topPanel = Ext.widget('panel',{
			id:'topPanel',
			layout:{type:'hbox'},
			height:100,
			width:'100%',
			autoScroll:false,//滚动条
			padding:'10 0 0 0',
			margin: 0,
			border:false,
			items:[]
		});
		var subjectsPanel =  Ext.widget('panel',{
			id:'subjectsPanel',
			layout:{type:'hbox'},
			margin:'0 0 2 0',
			border:false,
			items:[]
		});
		
		var addImg = Ext.create('Ext.Img', {
			id:'addImg',
			src:"/images/new_module/nocycleadd.png",
			style:'cursor:pointer;',
			width:50,
			height:50,
			margin:0,
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){ me.openEditSubjectsWindow("1",""); }//新建学科组
		        }
			}
		});
		//自动计算布局 haosl 2017-06-28
		subjectsList_me.autoResize();
		
		if(!me.addVersion){//新增学科组权限
			addImg = Ext.create('Ext.Img', {
				id:'addImg'
			});
		}
		var addPanel = Ext.widget('panel',{
			margin: '0 0 0 10',
			border:false,
			items:[addImg]
		});
		topPanel.add(subjectsPanel);
		topPanel.add(addPanel);
		
		if(subjectsPanel.items.items.length == 0){//学科组为空时，如果加入了空panel，IE下仍会占位。把addpanel挤到右边
			subjectsPanel.setVisible(false);
		}
		
		Ext.getCmp('wholePanel').insert(1, topPanel);
		var x = Ext.fly('wholePanel').getWidth();
		if(subjectsListGloble.currentPageClickState == '1'){
			topPanel.setPosition(-x, 0, false);
			topPanel.setPosition(0, 0, true);
		} else if(subjectsListGloble.currentPageClickState == '2'){
			topPanel.setPosition(2*x, 0, false);
			topPanel.setPosition(0, 0, true);
		}
	},
	//是否显示全部
	createShowAllPanel:function(){
		var me = this;
		var checkedState = false;
		if(subjectsListGloble.isshowall == "1")
			checkedState = true;
		var checkBox = Ext.widget('checkboxfield', {
			id:'isshowall',
		    checked:checkedState,
		    handler:function(o,v) {me.subjictShowAll(v);}
		});
		var hisLabel = Ext.widget('label', {
			text:'显示历史',
			style: 'padding-top:3px;',
		     listeners: {
				click: {
		            element: 'el', 
		            fn: function(a, o){ 
		            	var checkBox = Ext.getCmp("isshowall");
		            	var setValue = true;
		            	if(subjectsListGloble.isshowall == "1")
		            		setValue = false;
		            	checkBox.setValue(setValue);
					}
		        }
		     }
		});
		
		if(me.showHistoryVersion){//显示历史权限
			var toolBar = Ext.getCmp("jobtitle_subject_toolbar");
			toolBar.add("->");
			toolBar.add(checkBox);
			toolBar.add(hisLabel);
		}
	},
	//学科组显示全部专家
	subjictShowAll:function(v){
		var me = this;
		if(v)
			subjectsListGloble.isshowall = "1";
		else
			subjectsListGloble.isshowall = "0";
		var map = new HashMap();
		map.put("isshowall", subjectsListGloble.isshowall);
		Rpc({functionId:'ZC00002219',async:false,success:me.loadTable,scope:this},map);
	},
	// 增加学科组
	addSubjects:function(group_id, group_name, b0110name, state){
		var me = this;
		var subjectsPanel = Ext.getCmp('subjectsPanel');
		var task = new Ext.util.DelayedTask();
		b0110name = b0110name ? ' 归属于 '+ b0110name : '';
		var committeeImg = Ext.create('Ext.Img', {
			id:'pho_'+group_id,
		    src: "/images/new_module/committee.png",
		    style:'cursor:pointer;',
		    width:40,
		    height:40,
		    margin:'0 15 0 0',
		    title:group_name+b0110name,
			 listeners: {
				click: {
		            element: 'el',
		            fn: function(a, o){
						var cid = o.id.substring(4);
		            	if(subjectsListGloble.currentGroup_Id == cid)
		            		return;
		            	var phoOld = Ext.getCmp("pho_"+subjectsListGloble.currentGroup_Id);
		            	if(phoOld)
							phoOld.setSrc("/images/new_module/committee.png");//移除选中样式
						subjectsListGloble.currentGroup_Id = cid;
						subjectsListGloble.currentPageClickState = '';//更新点击状态 空
						var phoNew = Ext.getCmp("pho_"+subjectsListGloble.currentGroup_Id);
						phoNew.setSrc("/images/new_module/committee-sel.png");//添加选中样式
						task.delay(300, me.getSubjectsPerson, this, [cid, subjectsListGloble.isshowall]);
					}
		        },
		        dblclick: {
		        	element: 'el', 
		        	fn: function(a, o){
		        		if(state==0)//无效学科组（历史学科组）不能编辑
		        			return ;
		        		if(!subjectsList_me.editVersion)//没有编辑权限不能编辑 haosl 20160902
		        			return;
		        		var cid = o.id.substring(4);
			        	task.delay(300, me.openEditSubjectsWindow, this, ["2", cid]);
			        }
		        },
		        mouseover: {
		            element: 'el', 
		            fn: function(a, o){ 
		            	if(state==0)
		            		return; 
		            	if(!subjectsList_me.deleteVersion)//没有删除权限则不显示删除图标 haosl 20160902
		        			return;
		            	me.showHideDelImg(o.id, "1"); 
		            }
		        },
				mouseout: {
		        	element: 'el', 
		        	fn: function(a, o){ if(state==0){return ;} me.showHideDelImg(o.id,"0"); }
		        }
			}
		});
		var delImg = Ext.create('Ext.Img', {
			id:'del_'+group_id,
			src: "/workplan/image/remove.png",
			style:"cursor:pointer;",
			width:20,
			height:20,
			hidden:true,
			cls:'delImg1',
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){ me.openDeleteMsg(o.id); }
		        }, 
		        mouseover: {
		            element: 'el', 
		            fn: function(a, o){ if(!me.deleteVersion){return;} me.showHideDelImg(o.id, "1"); }
		        },
				mouseout: {
		        	element: 'el', 
		        	fn: function(a, o){ if(!me.deleteVersion){return;} me.showHideDelImg(o.id,"0"); }
		        }
			}
		});
		var showName = this.convertStr(group_name);
		var subjects = Ext.widget('panel',{
			id:'com_'+group_id,
			layout:{
				type:'vbox',
				align:'center'
			},
			width:85,
			margin: '0 5 0 0',
			border:false,
			items:[committeeImg,{
					xtype: 'label',
					id:"subName_"+group_id,
					text:showName,
					maxWidth:85,
					height:30,
					margin:'0 0 4 0',
					style:'word-break:break-all;'
				}, 
				delImg]
		});
		
		// 学科组选中状态
		if(subjectsListGloble.currentGroup_Id == group_id){
			var pho = Ext.getCmp("pho_"+subjectsListGloble.currentGroup_Id);
			pho.setSrc("/images/new_module/committee-sel.png");
		}
		
		subjectsPanel.add(subjects);
	},
	//提示是否删除
	openDeleteMsg:function(id){
		var me = this;
		Ext.Msg.confirm("提示信息","是否将该"+zc.menu.subjectsshowtext+"删除？",function(btn){ 
			 if(btn=='yes'){
				 me.deleteSubjects(id);
			 }
		});
	},
	//复写样式,不影响总体CSS
	createSelfCss:function(){
		Ext.util.CSS.createStyleSheet(".delImg1{ position:relative !important; left:50px !important; top:0px !important; }","delImg1");
	},
	//显示/隐藏删除按钮
	showHideDelImg:function(phoId, state){
		var delId = phoId.substring(4);
		var delImg = Ext.getCmp("del_"+delId);
		if(state == "1")
			delImg.show();
		else
			delImg.hide();
	},
	// 编辑页面
	openEditSubjectsWindow:function(type,cid){
		Ext.Loader.setConfig({
			enabled: true,
			paths: {
				"CommoncompURL":"/module/jobtitle/commoncomp"
			}
		});
		Ext.require("CommoncompURL.CommonCompEdit",function(){
			Ext.create("CommoncompURL.CommonCompEdit",{
				opt:type,
				pageType:"subjects",
				theId:cid
			}).show();
		});
	},
	// 删除学科组
	deleteSubjects:function(id){
		var group_id = id.substring(4);
		var subjectslist = new Array();//人员信息集
		subjectslist.push(group_id);
		var map = new HashMap();
		map.put("type", "3");//删除
		map.put("group_id", group_id);
		Rpc({functionId:'ZC00002203',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return ;
			}else{
				// 需要删除的是当前选中的，需重新定位应该选中的学科组
				if(subjectsListGloble.currentGroup_Id == group_id){
					var tmpId = '';
					var subjectsPanel = Ext.getCmp('subjectsPanel');
					var subjectslist = subjectsPanel.query('panel');
					var len = subjectslist.length;
					for(var i=0; i<len; i++){
						var id = subjectslist[i].id.split('_')[1];
						if(id == group_id){
							if(i > 0){//不是第一个说明前面有，则定位到前一个
								tmpId = subjectslist[i-1].id.split('_')[1];
							}else{
								if(i != (len-1)) {//是第一个并且不是最后一个，定位到后面一个
									tmpId = subjectslist[i+1].id.split('_')[1];
								} else {//只有这一个
									if(subjectsListGloble.currentPage > 1) {
										subjectsListGloble.currentPage -= 1;//当前页码要减一
									}
								}
							}
						}
					}
					subjectsListGloble.currentGroup_Id = tmpId;// 重新定位
				}
				subjectsListGloble.currentPageClickState = '';
				//刷新学科组列表 haosl 2017-06-28
				subjectsList_me.getSubjectsList();
				subjectsList_me.getSubjects();
				subjectsList_me.getSubjectsPerson(subjectsListGloble.currentGroup_Id, subjectsListGloble.isshowall);
			}
		},group_id:group_id,scope:this},map);
	},
	//点击展开事件
	clickTitleIcon:function(a){
		var subjectPanel = Ext.getCmp(this.id.split("_")[0]);
		subjectPanel.toggleCollapse();
	},
	// 初始化函数
	refreshTable:function() {
		var me = this;
		
		var map = new HashMap();
		map.put("schemeType", schemeState);//默认选中【进行中】
	    Rpc({functionId:'ZC00003001',async:false,success:me.getTableOK,scope:this},map);
	},
	// 重新加载数据列表
	loadTable:function(form){
		var store = Ext.data.StoreManager.lookup('jobtitle_subject_dataStore');
		store.currentPage=1;
		store.load();
	},
	// 刷新学科组内专家
	loadPerson:function(data, action){
		var me = this;
		var group_id = action.group_id;
		var obj = new Object();
		obj.id = group_id;
		me.getPerson(obj);
	},
	// 编辑学科组
	modifySubject:function(o){
		alert("modifySubject");
		var group_id = o.id.substring(4).split("_")[0];
		var group_name = o.id.substring(4).split("_")[1];
		var cp = Ext.getCmp(group_id);//当前的学科组
		cp.setTitle("<input id='ipt_"+group_id+"' style='width:290px;margin:0px 10px 0px 0px;' value='"+group_name+"'></input><button id='sav_"+group_id+"' class='x-btn-default-small x-btn-inner-center' onclick='committee_me.saveSubject(this);' style='background-color:#F9F9F9;cursor:pointer;'>保存</button></div>");
	},
	// 保存学科组
	saveSubject:function(o){
		var me = this;
		
		var group_id = o.id.substring(4);//学科组编号
		var iptid = "ipt_"+group_id;
		var name = Ext.get(iptid).dom.value;//新名称
		if(name=="" || name==null){
			Ext.showAlert(zc.menu.committeeshowtext+"名称不能为空！");
			return ;
		}
		
		var map = new HashMap();
		map.put("type", "2");//修改
		map.put("group_id", group_id);
		map.put("subjectsName", name);
		Rpc({functionId:'ZC00002203',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg !=""){
				Ext.showAlert(msg);
				return ;
			}else{
				var subjectPanel = Ext.getCmp(group_id);
				subjectPanel.setTitle(action.name);
				//me.getSubjects();
			}
		},name:name,scope:this},map);
	},
	// 获取专家
	getPerson:function(o){
		var me = this;
		
		var group_id = o.id;//学科组编号
		var map = new HashMap();
		map.put("group_id", group_id);
		Rpc({functionId:'ZC00002202',async:false,success:me.createPerson,groupId:group_id,scope:this},map);
	},
	//新增专家
	openExpertPicker:function(){
		if(subjectsListGloble.currentGroup_Id != null && subjectsListGloble.currentGroup_Id != ''){
			var map = new HashMap();
			map.put('memberType', 'subject');
			map.put('group_id', subjectsListGloble.currentGroup_Id);
			Rpc({functionId:'ZC00002208',async:false,success:function(form, action){
				Ext.require("ExpertPicker.ExpertPicker",function(){
					var re = Ext.create("ExpertPicker.ExpertPicker",{
						width:'800',
						height:'500',
						sql:Ext.decode(form.responseText).sql,//加载时sql
						orderBy:Ext.decode(form.responseText).orderBy,//排序
						searchText:'请输入单位名称、部门、姓名',
						title:'请选择专家',
						callback:subjectsList_me.addPerson,
                        supportPersonPicker:true,//启用手工引入
                        supportImportExpertsFilter:true//启用条件引入
					});
				});
			},scope:this},map);
		}
	},
	// 新增专家
	addPerson:function(selected){
		var me = this;
		var map = new HashMap();
		map.put("type", "1");//新增
		map.put("group_id", subjectsListGloble.currentGroup_Id);
		map.put("personidList", selected);
		Rpc({functionId:'ZC00002204',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return ;
			}else{
				subjectsListGloble.currentPageClickState = '';//更新点击状态 空
				subjectsList_me.getSubjectsPerson(subjectsListGloble.currentGroup_Id, subjectsListGloble.isshowall);
			}
		},scope:this},map);
	},
	//专家抽取
	randomSelectionr:function(){
		if(subjectsListGloble.currentGroup_Id != null && subjectsListGloble.currentGroup_Id != ''){
			Ext.require("ExpertPicker.RandomSelection",function(){
				var re = Ext.create("ExpertPicker.RandomSelection",{
					addCallback:subjectsList_me.addPerson,
					committeeId:subjectsListGloble.currentGroup_Id
				});
			});
		}
	},
	//设置组长 haosl 20170522
	setGrouLeader:function(obj){
		var role = '';
		var w0101 = obj.id.substring(6);
		var personList = new Array();//人员信息集
		personList.push(w0101);
		if(obj.checked){
			role = "1";
			var radlioArray = Ext.query('input[type=checkbox]');
			if(radlioArray.length > 0){
				for(var i=0; i<radlioArray.length; i++){
					var radio = radlioArray[i];
					if(radio.id != obj.id){
						Ext.getDom(radio.id).checked = "";
					}
				}
			}
		}else {
			role = "0";
		}
		
		var map = new HashMap();
		map.put("type", '3');//更新组长
		map.put("role",role);
		map.put("group_id", subjectsListGloble.currentGroup_Id);
		map.put('personidList', personList);
		Rpc({functionId:'ZC00002204',async:true,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(!Ext.isEmpty(msg)){
				Ext.showAlert(msg);
				return ;
			}
		},scope:this},map);
	},
	//撤销专家
	deletePerson:function(record){
		var selectData = subjectsList_tableObj.tablePanel.getSelectionModel().getSelection();//获取数据
		if(selectData.length == 0){
			Ext.showAlert("没有选中数据！");
			return ;
		}
		var selectedList = new Array();//人员信息集
		for(var p in selectData) {
			var w0101 = selectData[p].data.w0101_e;
			selectedList.push(w0101);
		}
		Ext.Msg.confirm('提示信息','是否撤销选中'+zc.menu.subjectsshowtext+'成员？',function(btn){ 
			 if(btn=='yes'){
				var map = new HashMap();
				map.put("type", '2');//删除
				map.put('group_id', subjectsListGloble.currentGroup_Id);
				map.put('personidList', selectedList);
				Rpc({functionId:'ZC00002204',async:false,success:function(form,action){
					var msg = Ext.decode(form.responseText).msg;
					var store = Ext.data.StoreManager.lookup('jobtitle_subject_dataStore');
					if(msg && msg != ''){
						Ext.showAlert(msg,function(){
							//haosl add 将专家置为未聘任状态后列表中移除专家
							var succeed = Ext.decode(form.responseText).succeed;
							if(succeed)
								store.load();
						});
					}else{
						Ext.showAlert(Ext.decode(form.responseText).message);
						subjectsListGloble.currentPageClickState = '';//更新点击状态：空
					}
				},scope:this},map);
			 }
		});
	},
	//导出
	expportData:function(){
		var map = new HashMap();
		map.put("committee_id", committeeGloble.currentCommitteeId);
	    Rpc({functionId:'ZC00002111',success:function(form,action){
			var result = Ext.decode(form.responseText);	
	    	window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+result.fileName;
		 }},map);
	},
	//把字符串转化成后面带省略号形式
	convertStr : function(str){
		var reStr = str;
		var maxwidth = 28;//字母排列的话最多占的个数
		var index = 0;
		var useWidth = 0;
		for(i=0; i<str.length; i++){
			 if(this.checknum(str.charAt(i)))//字母或数字
			 	useWidth += 1;
			 else//汉字
			 	useWidth += 2;//每个汉字占宽度约为字母的2倍
			 if(useWidth >= maxwidth && index == 0)
			 	index = i;
		} 
		//checknum
		if(useWidth > maxwidth){
			reStr = str.substring(0, index);
			reStr += '...';
		}
		return reStr;
	},
	//判断是否是字母或数字
	checknum:function(value) {
		var flg = false;
        var Regx = /^[A-Za-z0-9]*$/;
        if (Regx.test(value))
            flg =  true;
        return flg;
    },// 开始时间/结束时间
    dateFormat : function(value){
    	if(Ext.isEmpty(value)){
    		return value;
    	}
    	value = value.replace(/\-/g,'/');
    	var date=new Date(value); 
 		return Ext.Date.format(date,'Y-m-d');
    },
    checkCell:function(){
    	var state = subjectsListGloble.historyState;
    	if(state=="1")
    		return true;
    	else
    		return false;
    	
    },
    /**
     * 自动计算学科组布局
     */
    autoResize:function(){
    	
    	var me = this;
    	
    	var subjectsList = subjectsListGloble.subjectslist;
		var totalNum = subjectsList.length;//学科组总个数
		
		var x = Ext.fly('wholePanel').getWidth();
		var num = (x-75-30-30)/80;//每页个数
		num = Math.floor(num);//向下取整
		
		var pageNum = totalNum/num;//页数
		pageNum = Math.ceil(pageNum);//向上取整
		
		if(pageNum == 0)//如果算出页数为0，则设置成1
			pageNum = 1;
		subjectsListGloble.pageNum = pageNum;
		var startNum = num * (subjectsListGloble.currentPage - 1);
		var endNum = num * subjectsListGloble.currentPage - 1;
		//清除掉所有学科组图标
		var subjectsPanel = Ext.getCmp("subjectsPanel");
		if(subjectsPanel)
			subjectsPanel.removeAll(true);
		for(var i=0; i<subjectsList.length; i++){
			if(i >= startNum && i <= endNum){
				var subjects = subjectsList[i];
				var group_id = subjects.group_id;//学科组编号
				var group_name = subjects.group_name;//名称
				var b0110name = subjects.b0110;//名称
				var state = subjects.state;
				me.addSubjects(group_id, group_name, b0110name, state);
			}
		}
		
		//左右翻页的显示与隐藏
		var leftImg = Ext.getCmp('leftImg');
		var rightImg = Ext.getCmp('rightImg');
		if(subjectsListGloble.pageNum == 1){//【总页数】==1
			leftImg.setVisible(false);
			rightImg.setVisible(false);
		}else{
			if(subjectsListGloble.currentPage == 1){//【当前页码】== 1
				leftImg.setVisible(false);
				rightImg.setVisible(true);
			}else if(subjectsListGloble.pageNum == subjectsListGloble.currentPage){//【当前页码】==【总页数】
				leftImg.setVisible(true);
				rightImg.setVisible(false);
			}else{
				leftImg.setVisible(true);
				rightImg.setVisible(true);
			}
		}
		
    }, //添加外部专家
    addExpert:function(){
        var storeid="jobtitle_subject_dataStore";
        var store=Ext.data.StoreManager.lookup(storeid);
        var tablePanel = Ext.getCmp("jobtitle_subject_tablePanel");
        var columns=tablePanel.columns;
        var record =subjectsList_me.getNewRecord(columns);
        record.w0109='1`是';//添加外部专家，默认设置为可聘任  haosl add 2017-8-28
        record.w0111='1`是';
        record.b0110 = subjectsList_me.func;
        record.changestate='add';
        record.group_id_e=subjectsListGloble.currentGroup_Id;
        store.insert(store.getCount(),record);
        var cellediting = tablePanel.findPlugin('cellediting');//获得编辑器组件
        var selectRecords = store.getNewRecords();//获得新增记录的数组
        var lastRecord = selectRecords[selectRecords.length-1];//最后新增的记录
        tablePanel.getSelectionModel().select([lastRecord]);	//选中最后新增的记录
        subjectsList_me.sortstore(store);
        store.commitChanges();
        cellediting.startEdit(lastRecord,1);					//新增记录获得编辑组件获得光标

    },

    /**
     *  对store中的数据进行index赋值
     */
    sortstore:function(store)
    {
        for(var i=0;i<store.getCount();i++){
            var rec = store.getAt(i);
            rec.set('index',i);
        }
    },
    /**
     * 返回新增的记录 空记录
     */
    getNewRecord:function(columns)
    {
        var strRecord ="I9999:'-1'";
        for(var i=0;i<columns.length;i++)
        {
            var column=columns[i].dataIndex;
            var strFieldValue=column+":"+"''";
            strRecord=strRecord+","+strFieldValue;
        }
        strRecord="{"+strRecord+"}";
        var record= Ext.decode(strRecord);
        return record;
    },

//判断w01的指标是否可以修改
    w01EditableValid:function (record) {

            var b0110 = record.data.b0110;//每一个专家的所属单位
            if(subjectsList_me.orgid==''){//登录人职称管理权限
                return false;
            }else if(subjectsList_me.orgid=='UN`'){
                return true;
            }else{
                var orgarr = subjectsList_me.orgid.split(',');
                for(var i=0;i<orgarr.length;i++){
                    var org = orgarr[i];
                    if(b0110){
                        var index = b0110.indexOf('`');
                        b0110 = b0110.substring(0,index);
                        if(org.length>b0110.length){
                            return false;
                        }else{
                            return true;
                        }
                    }
                    else{
                        return false;
                    }
                }
            }
    },
    //判断所属单位的指标是否可以修改
    b0110EditableValid:function (record) {


            var b0110 = record.data.b0110;//每一个专家的所属单位
            if(subjectsList_me.orgid==''){//登录人职称管理权限
                return false;
            }else if(subjectsList_me.orgid=='UN`'){
                return true;
            }else{
                var orgarr = subjectsList_me.orgid.split(',');
                for(var i=0;i<orgarr.length;i++){
                    var org = orgarr[i];
                    if(b0110){
                        var index = b0110.indexOf('`');
                        b0110 = b0110.substring(0,index);
                        if(org.length>b0110.length){
                            return false;
                        }else{
                            return true;
                        }
                    }
                    else{
                        return false;
                    }
                }
            }

    }
});