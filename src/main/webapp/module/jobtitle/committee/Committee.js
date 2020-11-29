/**
 * 职称评审_委员会管理_聘委会
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 * 
 * */
Ext.define('JobtitleCommittee.Committee',{
	addVersion:false,//新增权限
	editVersion:false,//编辑权限
	deleteVersion:false,//删除权限
	showHistoryVersion:false,
	committeePanelIsExpand:true,//聘委会区域是否是展开状态
	constructor:function(config) {
		committee_me = this;
		committeeGloble = {};
		committeeGloble.ishistory = '0';//是否显示历史
		committeeGloble.currentCommitteeId = '';//当前选中的聘委会编号
		committeeGloble.pageNum = 0;//总页数
		committeeGloble.currentPage = 1;//当前页码
		committeeGloble.currentPageClickState = '';//当前触发翻页的按钮  1、左 2、右
		
		leader = [{'dataValue':'0','dataName':'否'}, {'dataValue':'1','dataName':'是'}];
		this.init();
	},
	// 初始化函数
	init:function() {
		var me = this;

		me.getCommitteeList();
		me.createSelfCss();
		var committee_id = me.getFirstCommitteeId();
		me.getCommitteePerson(committee_id, committeeGloble.ishistory);
	},
	// 第一条会议记录
	getFirstCommitteeId:function(){
		var committee_id = '';//聘委会编号
		var committeeList = committeeGloble.committeeList;
		for(var p in committeeList){
			var committee = committeeList[p];
			committee_id = committee.committee_id;
			committeeGloble.currentCommitteeId = committee_id;
			break;
		}
		
		return committee_id;
	},
	// 获取聘委会列表信息
	getCommitteeList:function(){
		var map = new HashMap();
		Rpc({functionId:'ZC00002101',async:false,success:function(data){
			committeeGloble.committeeList = Ext.decode(data.responseText).committeeList;
			this.addVersion = Ext.decode(data.responseText).addVersion;
			this.editVersion = Ext.decode(data.responseText).editVersion;
			this.deleteVersion = Ext.decode(data.responseText).deleteVersion;
			this.showHistoryVersion = Ext.decode(data.responseText).showHistoryVersion;
			
		},scope:this},map);
	},
	// 加载人员信息
	getCommitteePerson:function(committee_id, ishistory){
		var me = this;
		
		var map = new HashMap();
		map.put("committee_id", committee_id);
		map.put("ishistory", ishistory);
	    Rpc({functionId:'ZC00002102', async:false, success:committee_me.getPersonOK, scope:this},map);
	},
	// 获取主页面
	getPersonOK:function(form, action){
		var me = this;
		
		if(typeof committee_tableObj !== 'undefined' && committee_tableObj){
			committee_me.loadTable();
			return ;
		}

		var result = Ext.decode(form.responseText);
		var jsonData = result.tableConfig;
		var obj = Ext.decode(jsonData);
        committee_me.func=result.func;
        committee_me.orgid=result.orgid;
		obj.openColumnQuery = true;	//haosl 20161014 方案查询可以查询自定义指标
		committee_tableObj = new BuildTableObj(obj);
		//评委会上方图标自适应
		committee_tableObj.mainPanel.on("resize",function(){
			me.autoResize();
		});
		var leftImg = Ext.create('Ext.Img', {
			id:'leftImg',
			src: "/images/new_module/left.png",
			style:'cursor:pointer;',
			width : 20,
			height:60,
			margin:'20 0 0 0',
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){ committee_me.leftPage(); }//新建聘委会
		        }
			}
		});
		var rightImg = Ext.create('Ext.Img', {
			id:'rightImg',
			src: "/images/new_module/right.png",
			style:'cursor:pointer;',
			width : 20,
			height:60,
			margin:'20 0 0 0',
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){ committee_me.rightPage(); }//新建聘委会
		        }
			}
		});
		var wholePanel = Ext.widget('panel',{
			id:'wholePanel',
			height:90,
			layout:{
				type:'hbox'
			},
			padding:0,
			margin: 0,
			border:false,
			bodyBorder:false,
			style:'border-bottom:1px solid #C5C5C5;',
			dockedItems: [{
			    xtype: 'toolbar',
			    dock: 'left',
			    items: [
			        leftImg
			    ]
			},{
			    xtype: 'toolbar',
			    dock: 'right',
			    items: [
			        rightImg
			    ]
			}
			],
			items:[]
		});
		committee_tableObj.insertItem(wholePanel, 0);
		
		committee_me.getCommittee();
		
		committee_me.createHistoryPanel();// 是否显示历史
		
		
		var collapseOrExpand = Ext.create('Ext.Img', {//展开、收起按钮
			src: "/images/new_module/collapse_gray.png",
			id:'collapseOrExpand',
			width:16,
			height:16,
			scope:this,
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(){
		            	var img = Ext.getCmp(arguments[1].id);
		            	var wholePanel = Ext.getCmp('wholePanel');
		            	if(me.committeePanelIsExpand){//展开状态
		            		wholePanel.setHeight(0);//收起
		            		img.setSrc('/images/new_module/expand_gray.png');
		            		me.committeePanelIsExpand = false;
		            	} else {//收起状态
		            		wholePanel.setHeight(100);//展开
		            		img.setSrc('/images/new_module/collapse_gray.png');
		            		me.committeePanelIsExpand = true;
		            	}
		            	
		            }
		        }
			}
		});
		committee_tableObj.bodyPanel.addTool(collapseOrExpand);//在栏目设置按钮后面
		
		committee_tableObj.tablePanel.findPlugin('cellediting').on("edit",function(edit,e){
			if(e.field == 'start_date') {//判断起始时间不能大于结束时间
				var end_date = e.record.data.end_date;
				var start_date = e.value;
				if(end_date != '' && end_date != '' && end_date < start_date) {
					committee_me.loadStore();
					Ext.showAlert(zc.label.endThanStart);
					return ;
				}
			}else if(e.field == 'end_date'){//判断结束时间不能大于起始时间
				var start_date = e.record.data.start_date;
				var end_date = e.value;
				if(start_date != '' && end_date != '' && end_date < start_date) {
					committee_me.loadStore();
					Ext.showAlert(zc.label.endThanStart);
					return ;
				}
			}
		});
	},
	loadStore:function() {
		var store = Ext.data.StoreManager.lookup('jobtitle_committee_dataStore');
		store.load();
	},
	// 创建聘委会区域
	getCommittee:function(){
		var me = this;
		
		var top = Ext.getCmp('topPanel');
		if(top){
			Ext.getCmp('wholePanel').remove(top);
		}
		var topPanel = Ext.widget('panel',{
			//style:'background-color:#ffffff;',
			id:'topPanel',
			layout:{
				type:'hbox'
			},
			height:100,
			width:'100%',
			autoScroll:false,// 滚动条
			padding:'10 0 0 0',
			margin: 0,
			border:false,
			items:[]
		});
		var committeePanel =  Ext.widget('panel',{
			id:'committeePanel',
			layout:{
				type:'hbox'
			},
			margin:'0 0 2 0',
			border:false,
			items:[]
		});
		//自动计算评委会图标布局
		me.autoResize();
		
		var addImg = Ext.create('Ext.Img', {
			id:'addImg',
			src: "/images/new_module/nocycleadd.png",
			style:'cursor:pointer;',
			width : 50,
			height : 50,
			margin:0,
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){ me.openEditCommitteeWindow("1"); }//新建聘委会
		        }
			}
		});
		
		if(!me.addVersion){//新增聘委会权限
			addImg = Ext.create('Ext.Img', {
				id:'addImg'
			});
		}
		
		var addPanel = Ext.widget('panel',{
			margin: '0 0 0 10',
			border:false,
			items:[addImg]
		});
		topPanel.add(committeePanel);
		topPanel.add(addPanel);
		
		if(committeePanel.items.items.length == 0){//聘委会为空时，如果加入了空panel，IE下仍会占位。把addpanel挤到右边
			committeePanel.setVisible(false);
		}
		var x = Ext.fly('wholePanel').getWidth();
		Ext.getCmp('wholePanel').insert(1, topPanel);
		if(committeeGloble.currentPageClickState == '1'){
			topPanel.setPosition(-x, 0, false);
			topPanel.setPosition(0, 0, true);
		} else if(committeeGloble.currentPageClickState == '2'){
			topPanel.setPosition(2*x, 0, false);
			topPanel.setPosition(0, 0, true);
		}
	},
	// 编辑页面 type: 1新建 2编辑
	openEditCommitteeWindow:function(type, cid){
		Ext.Loader.setConfig({
			enabled: true,
			paths: {
				"CommoncompURL":"/module/jobtitle/commoncomp"
			}
		});
		Ext.require("CommoncompURL.CommonCompEdit",function(){
			Ext.create("CommoncompURL.CommonCompEdit",{
				opt:type,
				pageType:"committee",
				theId:cid
			}).show();
		});
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
	// 复写样式，不影响总体Css
	createSelfCss:function(){
		Ext.util.CSS.createStyleSheet(".delImg{ position:relative !important; left:50px !important; top:0px !important; }","delImg");
		Ext.util.CSS.createStyleSheet(".ellipsis{display:block;white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }","ellipsis");
	},
	// 聘委会显示历史
	committeeHistoty:function(v){
		var me = this;
		if(v){
			committeeGloble.ishistory = "1";
		}else{
			committeeGloble.ishistory = "0";
		}
		var map = new HashMap();
		map.put("ishistory", committeeGloble.ishistory);
		Rpc({functionId:'ZC00002110',async:false,success:me.loadTable,scope:this},map);
	},
	// 专家显示历史
	personHistoty:function(id, state){
		var me = this;
		
		var isHistory = "0";
		var group_id = id.substring(4);
		if(state){
			isHistory = "1";
		}
		var map = new HashMap();
		map.put("group_id", group_id);
		map.put("ishistory", isHistory);
		Rpc({functionId:'ZC00002205',async:false,success:me.loadPerson,group_id:group_id,scope:this},map);
	},
	// 重新加载数据列表
	loadTable:function(form){
		var store = Ext.data.StoreManager.lookup('jobtitle_committee_dataStore');
		store.currentPage=1;
		store.load();
	},
	// 刷新聘委会内专家
	loadPerson:function(data, action){
		var me = this;
		
		var group_id = action.group_id;
		var obj = new Object();
		obj.id = group_id;
		me.getPerson(obj);
	},
	// 编辑聘委会
	modifySubject:function(o){
		
		var group_id = o.id.substring(4).split("_")[0];
		var group_name = o.id.substring(4).split("_")[1];
		var cp = Ext.getCmp(group_id);//当前的聘委会
		cp.setTitle("<input id='ipt_"+group_id+"' style='width:290px;margin:0px 10px 0px 0px;' value='"+group_name+"'></input><button id='sav_"+group_id+"' class='x-btn-default-small x-btn-inner-center' onclick='committee_me.saveSubject(this);' style='background-color:#F9F9F9;cursor:pointer;'>保存</button></div>");
	},
	// 保存聘委会
	saveSubject:function(o){
		var me = this;
		
		var group_id = o.id.substring(4);//聘委会编号
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
			}
		},name:name,scope:this},map);
	},
	// 获取专家
	getPerson:function(o){
		var me = this;
		
		var group_id = o.id;//聘委会编号
		var map = new HashMap();
		map.put("group_id", group_id);
		Rpc({functionId:'ZC00002202',async:false,success:me.createPerson,groupId:group_id,scope:this},map);
	},
	//显示/隐藏删除按钮
	showHideDelImg:function(phoId, state){
		var delId = phoId.substring(4);
		var delImg = Ext.getCmp("del_"+delId);
		if(state == "1"){
			delImg.show();
		}else{
			delImg.hide();
		}
	},
	// 新增专家
	addPerson:function(selected){
		var me = this;
		
		var map = new HashMap();
		map.put("type", "1");//新增
		map.put("committee_id", committeeGloble.currentCommitteeId);
		map.put("personidList", selected);
		Rpc({functionId:'ZC00002104',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return ;
			}else{
				committeeGloble.currentPageClickState = '';//更新点击状态 空
				committee_me.getCommitteePerson(committeeGloble.currentCommitteeId, committeeGloble.ishistory);
			}
		},scope:this},map);
	},
	// 删除聘委会
	deleteCommittee:function(id){
		var committee_id = id.substring(4);
		var committeeList = new Array();//人员信息集
		committeeList.push(committee_id);
		var map = new HashMap();
		map.put("type", "3");//删除
		map.put("committee_id", committee_id);
		Rpc({functionId:'ZC00002103',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return ;
			}else{
				// 需要删除的是当前选中的，需重新定位应该选中的聘委会
				if(committeeGloble.currentCommitteeId == committee_id){
					var tmpId = '';
					var committeePanel = Ext.getCmp('committeePanel');
					var committeeList = committeePanel.query('panel');
					var len = committeeList.length;
					for(var i=0; i<len; i++){
						var id = committeeList[i].id.split('_')[1];
						if(id == committee_id){
							if(i > 0){//不是第一个说明前面有，则定位到前一个
								tmpId = committeeList[i-1].id.split('_')[1];
							}else{
								if(i != (len-1)) {//是第一个并且不是最后一个，定位到后面一个
									tmpId = committeeList[i+1].id.split('_')[1];
								} else {//只有这一个
									if(committeeGloble.currentPage > 1) {
										committeeGloble.currentPage -= 1;//当前页码要减一
									}
								}
							}
						}
						
					}
					committeeGloble.currentCommitteeId = tmpId;// 重新定位
				}
				committeeGloble.currentPageClickState = '';
				//刷新评委会列表 2017-06-28
				committee_me.getCommitteeList();
				committee_me.getCommittee();
				committee_me.getCommitteePerson(committeeGloble.currentCommitteeId, committeeGloble.ishistory);
			}
		},committee_id:committee_id,scope:this},map);
	},
	//设定展开/收起按钮
	setIcon:function(o, state){
		var group_id = o.id;//聘委会编号
		var subjectPanel = Ext.getCmp(group_id);
		if(state == "up"){
			subjectPanel.setIconCls("subjectTitleUp");
		}else{
			subjectPanel.setIconCls("subjectTitleDown");
		}
	},
	// 是否显示历史
	createHistoryPanel:function(){
		var me = this;
		
		var checkedState = false;
		if(committeeGloble.ishistory == "1"){
			checkedState = true;
		}
		
		var checkBox = Ext.widget('checkboxfield', {
			id:'ishistory',
		    checked  : checkedState,
		    handler : function(o,v) {me.committeeHistoty(v);}
		});
		var hisLabel = Ext.widget('label', {
			text:'显示历史',
			style: 'padding-top:3px;',
		     listeners: {
				click: {
		            element: 'el', 
		            fn: function(a, o){ 
		            	var checkBox = Ext.getCmp("ishistory");
		            	var setValue = true;
		            	if(committeeGloble.ishistory == "1"){
		            		setValue = false;
		            	}
		            	checkBox.setValue(setValue);
					}
		        }
		     }
		});
		
		if(me.showHistoryVersion){//显示历史权限
			var toolBar = Ext.getCmp("jobtitle_committee_toolbar");
			toolBar.add("->");
			toolBar.add(checkBox);
			toolBar.add(hisLabel);
		}
	},
    // 获取聘委会信息
    getCommitteeInfo:function(committee_id, type) {
		
		var info = '';
		
		var map = new HashMap();
		map.put('type', type);
		map.put('committee_id', committee_id);
		Rpc({functionId:'ZC00002106',async:false,success:function(data){
			var me = this;
			info = Ext.decode(data.responseText).committeeInfo;
		},scope:this},map);
		
		return info;
	},
	// 增加聘委会
	addCommittee:function(committee_id, committee_name, b0110name){
		var me = this;
		
		var committeePanel = Ext.getCmp('committeePanel');
		var task = new Ext.util.DelayedTask();
		
		var committeeImg = Ext.create('Ext.Img', {
			id:'pho_'+committee_id,
		    src: "/images/new_module/committee.png",
		    style:'cursor:pointer;',
		    width:40,
		    height:40,
		    margin:'0 15 0 0',
		    title:committee_name+" 归属于 "+b0110name,
			 listeners: {
				click: {
		            element: 'el',
		            fn: function(a, o){
						var cid = o.id.substring(4);
		            	if(committeeGloble.currentCommitteeId == cid){
		            		return;
		            	}
		            	//切换评委会的时候，同时清空查询条件[36155]
		            	Ext.getCmp('jobtitle_committee_querybox').removeAllKeys();
		            	var phoOld = Ext.getCmp("pho_"+committeeGloble.currentCommitteeId);
		            	if(phoOld){
							phoOld.setSrc("/images/new_module/committee.png");//移除选中样式
		            	}
						
						committeeGloble.currentCommitteeId = cid;
						committeeGloble.currentPageClickState = '';//更新点击状态 空
						
						var phoNew = Ext.getCmp("pho_"+committeeGloble.currentCommitteeId);
						phoNew.setSrc("/images/new_module/committee-sel.png");//添加选中样式
						
						task.delay(300, me.getCommitteePerson, this, [cid, committeeGloble.ishistory]);
					}
		        },
		        dblclick: {
		        	element: 'el', 
		        	fn: function(a, o){
		        		if(!me.editVersion) {//没有编辑权限
		        			return ;
		        		}
			        	var cid = o.id.substring(4); 
			        	task.delay(300, me.openEditCommitteeWindow, this, ["2", cid]);
			        }
		        },
		        mouseover: {
		            element: 'el', 
		            fn: function(a, o){ if(!me.deleteVersion){return ;} me.showHideDelImg(o.id, "1"); }
		        },
				mouseout: {
		        	element: 'el', 
		        	fn: function(a, o){ if(!me.deleteVersion){return ;} me.showHideDelImg(o.id,"0"); }
		        }
			}
		});
		var delImg = Ext.create('Ext.Img', {
			id:'del_'+committee_id,
			src: "/workplan/image/remove.png",
			style:"cursor:pointer;",
			width:20,
			height:20,
			hidden:true,
			cls:'delImg',
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
		
		var showName = this.convertStr(committee_name);
		
		var committee = Ext.widget('panel',{
			id:'com_'+committee_id,
			layout:{
				type:'vbox',
				align:'center'
			},
			width:85,
			margin: '0 5 0 0',
			border:false,
			items:[committeeImg,{
					xtype: 'label',
					id:"comName_"+committee_id,
					text:showName,
					maxWidth:85,
					height:30,
					margin:'0 0 4 0',
					//cls:'ellipsis',//该属性控制溢出后显示...
					style:'word-break:break-all;'
				}, 
				delImg]
		});
		
		// 聘委会选中状态
		if(committeeGloble.currentCommitteeId == committee_id){
//			var pointTopImg = Ext.create('Ext.Img', {
//				id:'poi_'+committee_id,
//				src: "/images/new_module/pointtop.png",
//				width:15,
//				height:15,
//				cls:'pointTopImg'
//			});
//			committee.add(pointTopImg);
			var pho = Ext.getCmp("pho_"+committeeGloble.currentCommitteeId);
			pho.setSrc("/images/new_module/committee-sel.png");
		}
		
		committeePanel.add(committee);
	},
	//新增专家
	openExpertPicker:function(){
		
		if(committeeGloble.currentCommitteeId != null && committeeGloble.currentCommitteeId != ''){
			
			var map = new HashMap();
			map.put('memberType', 'committee');
			map.put('committee_id', committeeGloble.currentCommitteeId);
			Rpc({functionId:'ZC00002208',async:false,success:function(form, action){
				Ext.require("ExpertPicker.ExpertPicker",function(){
					var re = Ext.create("ExpertPicker.ExpertPicker",{
						width:'800',
						height:'500',
						sql:Ext.decode(form.responseText).sql,//加载时sql
						orderBy:Ext.decode(form.responseText).orderBy,//排序
						searchText:'请输入单位名称、部门、姓名',
						title:'请选择专家',
						callback:committee_me.addPerson,
                        supportPersonPicker:true,//启用手工引入
                        supportImportExpertsFilter:true//启用条件引入
					});
				});
			},scope:this},map);
		}
	},
    //添加外部专家
    addExpert:function(){
        var storeid="jobtitle_committee_dataStore";
        var store=Ext.data.StoreManager.lookup(storeid);
        var tablePanel = Ext.getCmp("jobtitle_committee_tablePanel");
        var columns=tablePanel.columns;
        var record =committee_me.getNewRecord(columns);
        record.w0109='1`是';//添加外部专家，默认设置为可聘任  haosl add 2017-8-28
        record.w0111='1`是';
        record.b0110 = committee_me.func;
        record.changestate='add';
        record.committee_id_e=committeeGloble.currentCommitteeId;
        store.insert(store.getCount(),record);
        var cellediting = tablePanel.findPlugin('cellediting');//获得编辑器组件
        var selectRecords = store.getNewRecords();//获得新增记录的数组
        var lastRecord = selectRecords[selectRecords.length-1];//最后新增的记录
        tablePanel.getSelectionModel().select([lastRecord]);	//选中最后新增的记录
        committee_me.sortstore(store);
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
            if(committee_me.orgid==''){//登录人职称管理权限
                return false;
            }else if(committee_me.orgid=='UN`'){
                return true;
            }else{
                var orgarr = committee_me.orgid.split(',');
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
    // 撤销专家校验、提示
	deletePerson:function(record){
		
		var selectData = committee_tableObj.tablePanel.getSelectionModel().getSelection();//获取数据
		if(selectData.length == 0){
			Ext.showAlert("没有选中数据！");
			return ;
		}
		var selectedList = new Array();//人员信息集
		for(var p in selectData) {
			var w0101 = selectData[p].data.w0101_e;
			selectedList.push(w0101);
		}
		Ext.Msg.confirm('提示信息','是否删除选中'+zc.menu.committeeshowtext+'成员？',function(btn){ 
			 if(btn=='yes'){
			 	committee_me.deletePersonAfter(selectedList);
			 }
		});
		
	},
	// 删除专家
	deletePersonAfter:function(selectedList){
		var msg = '';
		var map = new HashMap();
		map.put("type", '2');//删除
		map.put('committee_id', committeeGloble.currentCommitteeId);
		map.put('personidList', selectedList);
		Rpc({functionId:'ZC00002104',async:false,success:function(form,action){
			msg = Ext.decode(form.responseText).msg;
			if(msg && msg != ''){
				Ext.showAlert(msg);
			}else{
				Ext.showAlert(Ext.decode(form.responseText).message);
				committeeGloble.currentPageClickState = '';//更新点击状态：空
			}
			if(Ext.decode(form.responseText).succeed){
				var store = Ext.data.StoreManager.lookup('jobtitle_committee_dataStore');
				store.load();
			}
		},scope:this},map);
		
		return msg;
	},
	// 导出
	expportData:function(){
		var map = new HashMap();
		map.put("committee_id", committeeGloble.currentCommitteeId);
	    Rpc({functionId:'ZC00002111',success:function(form,action){
			var result = Ext.decode(form.responseText);	
	    	window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+result.fileName;
		 }},map);
	},
	// 随机抽取
	randomSelectionr:function(){
		if(committeeGloble.currentCommitteeId != null && committeeGloble.currentCommitteeId != ''){
			
			Ext.require('ExpertPicker.RandomSelection',function(){
				var re = Ext.create('ExpertPicker.RandomSelection',{
					addCallback:committee_me.addPerson,
					rollBackCallback:committee_me.deletePersonAfter,
					committeeId:committeeGloble.currentCommitteeId
				});
			});
		}
	},
	// 左翻页
	leftPage:function(){
		if(committeeGloble.currentPage == 1){//当前页码等于1，左翻页 禁用
			return ;
		}
		
		committeeGloble.currentPage = committeeGloble.currentPage - 1;
		committeeGloble.currentPageClickState = '1';//更新点击状态 左翻页
		committee_me.getCommittee();
	},
	// 右翻页
	rightPage:function(){
		if(committeeGloble.currentPage == committeeGloble.pageNum){//当前页码等于总页码，右翻页 禁用
			return ;
		}
		
		committeeGloble.currentPage = committeeGloble.currentPage + 1;
		committeeGloble.currentPageClickState = '2';//更新点击状态 右翻页
		committee_me.getCommittee();
	},
	// 把字符串转化成后面带省略号形式
	convertStr : function(str){
		var reStr = str;
		
		var maxwidth = 28;//字母排列的话最多占的个数
		var index = 0;
		var useWidth = 0;
		for(i=0; i<str.length; i++){
			 if(this.checknum(str.charAt(i))) {//字母或数字
			 	useWidth += 1;
			 } else {//汉字
			 	useWidth += 2;//每个汉字占宽度约为字母的2倍
			 }
			 if(useWidth >= maxwidth && index == 0){
			 	index = i;
			 }
		} 
		//checknum
		if(useWidth > maxwidth){
			reStr = str.substring(0, index);
			reStr += '...';
		}
		return reStr;
	},
	// 判断是否是字母或数字
	checknum : function(value) {
		var flg = false;
        var Regx = /^[A-Za-z0-9]*$/;
        if (Regx.test(value)) {
            flg =  true;
        }
        return flg;
    },
    // 开始时间/结束时间
    dateFormat : function(value){
    	if(Ext.isEmpty(value)){
    		return value;
    	}
    	value = value.replace(/\-/g,'/');
    	var date=new Date(value); 
 		return Ext.Date.format(date,'Y-m-d');
    },
    sortstore:function(store)
	{
		for(var i=0;i<store.getCount();i++){
	       var rec = store.getAt(i);
	       rec.set('index',i);
	    }
	},
	//提示是否删除
	openDeleteMsg:function(id){
		var me = this;
		Ext.Msg.confirm("提示信息","是否将该"+zc.menu.committeeshowtext+"删除？",function(btn){ 
			 if(btn=='yes'){
				 me.deleteCommittee(id);
			 }
		});
	},
	/**
	 * 设置组长
	 */
	setRole:function(){
		var value = arguments[0];
		var record = arguments[2];
		var id = "check_" + record.data.w0101_e;
		var flag = record.data.flag;
		var checked = '';
		var disabled="disabled='disabled'";
		if(value == 1){
			checked = "checked=checked";//选中
		}
		if(flag && flag.length>0 && flag.split("`")[0]=="1"){
			disabled = '';
		}
		if(value == 1){
			checked = "checked=checked";//选中
		}
		var html = "<input id='"+id+"' "+disabled+" onclick='committee_me.setCommitteeLeader(this);' type='checkbox' "+checked+" name='checkbox'/>" +
						"<label style='position:relative;top:-2px;' for='" +id+"'>设置" +
					"</label>";
		return html;
	},
	//设置组长 haosl 201060903
	setCommitteeLeader:function(obj){
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
		map.put("committee_id", committeeGloble.currentCommitteeId);
		map.put('personidList', personList);
		Rpc({functionId:'ZC00002104',async:true,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(!Ext.isEmpty(msg)){
				Ext.showAlert(msg);
				return ;
			}
		},scope:this},map);
	},
	/**
	 * 自动计算评委会布局
	 */
	autoResize:function(){
		var committeeList = committeeGloble.committeeList;

		var totalNum = committeeList.length;//聘委会总个数
		var x = Ext.fly('wholePanel').getWidth();
		var num = (x-75-30-30)/80;//每页个数
		num = Math.floor(num);//向下取整
		var pageNum = totalNum/num;//页数
		pageNum = Math.ceil(pageNum);//向上取整
		if(pageNum == 0){//如果算出页数为0，则设置成1
			pageNum = 1;
		}
		committeeGloble.pageNum = pageNum;
		if(committeeGloble.pageNum<committeeGloble.currentPage)
			committeeGloble.currentPage = 1;
		var startNum = num * (committeeGloble.currentPage - 1);
		var endNum = num * committeeGloble.currentPage - 1;
		var committeePanel = Ext.getCmp('committeePanel');
		if(committeePanel){
			committeePanel.removeAll(true);
		}
		for(var i=0; i<committeeList.length; i++){
			if(i >= startNum && i <= endNum){
				var committee = committeeList[i];
				var committee_id = committee.committee_id;//聘委会编号
				var committee_name = committee.committee_name;//名称
				var b0110name = committee.b0110name;//名称
				committee_me.addCommittee(committee_id, committee_name, b0110name);
			}
		}
		
		// 左右翻页的显示与隐藏
		var leftImg = Ext.getCmp('leftImg');
		var rightImg = Ext.getCmp('rightImg');
		if(committeeGloble.pageNum == 1){//【总页数】==1
			leftImg.setVisible(false);
			rightImg.setVisible(false);
		}else{
			if(committeeGloble.currentPage == 1){//【当前页码】== 1
				leftImg.setVisible(false);
				rightImg.setVisible(true);
				
			}else if(committeeGloble.pageNum == committeeGloble.currentPage){//【当前页码】==【总页数】
				leftImg.setVisible(true);
				rightImg.setVisible(false);
			
			}else{
				leftImg.setVisible(true);
				rightImg.setVisible(true);
				
			}
		}
	},
    //判断所属部门的指标是否可以修改
    b0110EditableValid:function (record) {


        var b0110 = record.data.b0110;//每一个专家的所属单位
        if(committee_me.orgid==''){//登录人职称管理权限
            return false;
        }else if(committee_me.orgid=='UN`'){
            return true;
        }else{
            var orgarr = committee_me.orgid.split(',');
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