/**
 * 职称评审_委员会管理_学科组（评审会议调用）
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 * 
 * */
Ext.define('JobtitleSubjects.SubjectsForMeeting',{
	w0301:'',
	readonly:'1',
	subjects_picker_tableObj:'',
	constructor:function(config) {
		this.w0301 = config.w0301,
		this.readonly = config.readonly,
		subjectsGloble = {},
		//申报人分组id
		subjectsGloble.categoriesid = config.categoriesid,
		subjectsGloble.selectId = config.selectId;
		subjectsGloble.selectGroupId = config.selectGroupId,//审批人，选择的学科组ID，每个会议的分组只能选择一组审批人
		subjectsGloble.countSpanId = config.countSpanId,//审批人，这边关闭了界面需要同时更新上个界面对应的人数
		subjectsGloble.type = config.type,//类型，vote已投票的方式进入的
		subjectsGloble.isVote = false;//是否是投票方式
		subjectsGloble.ishistory = '0',
		subjectsGloble.group_id = '0',
		subjectsGloble.first_group_id = '0',
		subjectsGloble.groupsnum = 0;//当前关联的学科组数
		subjectsGloble.groupName = new HashMap();
		subjectsGloble.returnBackFunc = config.returnBackFunc;
		
		subjectsGloble.subjectslist=null; //学科组列表
		subjectsMeet_me = this;
		this.init();
	},
	// 初始化函数
	init:function() {
		var me = this;
		me.createCondition();
		me.createSelfCss();
		me.createMainPanel();// 加载主框架
		me.getSubjects();
		if(Ext.getCmp(subjectsGloble.first_group_id)){//展开第一个
			Ext.getCmp(subjectsGloble.first_group_id).expand(false);
		}
	},
	createCondition:function(){
		var me = this;
		if(subjectsGloble.type == "vote") {//这样做的目的是以后如果传过来不是vote等其他的，再下面要做很多修改判断，统一在这里判断，好维护，添加
			subjectsGloble.isVote = true;//投票方式不能添加学科组，不能删除学科组
		}
	},
	// 获取主页面
	createMainPanel:function(){
		var me = this;
		var contentPanel = Ext.widget('panel',{
			header:false,
			//style:'background-color:#ffffff;',
			id:'contentPanel',
			autoScroll:true,// 滚动条
			margin: '-5 0 0 30',
			border:false,
			items:[]
		});
	    Ext.create('Ext.window.Window', {
				id : 'subjects_show_window_id',
				title:zc.menu.subjectsshowtext+'评审成员',
				modal:true,
				layout:'fit',
				width:700,
				height:(window.screen.availHeight-150)*(subjectsGloble.isVote?0.5:0.8),//窗口的高(不含菜单)，150：菜单高度,
				border:false,
				items:[contentPanel],
				buttonAlign:'center',
				buttons : [{
					xtype : 'button',
					text : common.button.ok,
					margin:'20 10 0 0',
					handler : function() {
						/*if(subjectsGloble.returnBackFunc){
	            			//subjectsGloble.isVote 根据这个参数判断是否是编辑单个学科组
	            			subjectsGloble.returnBackFunc(subjectsGloble.subjectslist,subjectsGloble.isVote);
		            	}else{
		            		if(subjectsGloble.isVote && subjectsMeet_me.readonly == '0') {//投票的方式，选择评审人，只能选择但钱学科组的
				            	var map = new HashMap();
				            	map.put("w0301", me.w0301);
				            	map.put("categoriesid",subjectsGloble.categoriesid);
				            	map.put("selectGroupId",subjectsGloble.selectGroupId);
				            	Rpc({functionId:'ZC00002221',async:false,success:me.getBackSubjectsShow,scope:me},map);
			            	}
		            	}*/
						Ext.getCmp('subjects_show_window_id').close();
					}
				}],
				listeners: {
			        close: {
			            fn: function(a, o){
			            	//有回调函数时优先执行回调函数
	            			//subjectsGloble.isVote 根据这个参数判断是否是编辑单个学科组
	            			subjectsGloble.returnBackFunc(subjectsGloble.subjectslist,subjectsGloble.isVote);
			            }
			        }
				}
							
		}).show();
	},
	// 获取学科组
	getSubjects:function(){
		var me = this;
		var map = new HashMap();
		map.put("w0301", me.w0301);
		map.put("categoriesid",subjectsGloble.categoriesid);
		map.put("selectGroupId",subjectsGloble.selectGroupId);
		//map.put("type",subjectsGloble.type);//是从什么方式进来的，如果是投票的时候则删除其他组的内容，只显示
		Rpc({functionId:'ZC00002210',async:false,success:me.createSubjects,scope:me},map);
	},
	// 创建学科组区域
	createSubjects:function(data){
		var me = this;
		
		var contentPanel = Ext.getCmp("contentPanel");
		contentPanel.removeAll(true);
		var subjectslist = Ext.decode(data.responseText).subjectslist;
		subjectsGloble.subjectslist=subjectslist;
		subjectsGloble.groupsnum = subjectslist.length;
		for(var p in subjectslist){
			var subject = subjectslist[p];
			var group_name = subject.group_name;
			subjectsGloble.groupName += ':' + group_name;
			var group_id = subject.group_id;
			var b0110 = subject.b0110;
			//b0110 = b0110!=''?'('+ b0110 +')':'';
			b0110 = b0110 != '' ? '&nbsp;归属于&nbsp;'+ b0110 : '';
			if(subjectsGloble.first_group_id == '0'){
				subjectsGloble.first_group_id = group_id;
			}
			
			var tools = [{
				// 删除学科组
				xtype:'image',
				id:"del_"+group_id,
				//icon: '/images/new_module/listview.png',
				src: '/workplan/image/chahao.png',
				style:'cursor:pointer;',
				width:16,
				height:16,
				margin:'0 30 0 0',
				listeners: {
					click: {
						element: 'el', 
						fn: function(a, o){ me.openDeleteMsg(o); }
					}
				}
			}];
			if(this.readonly == '1' || subjectsGloble.isVote){//只读
				tools = '';
			}
			var title = group_name + b0110;
			var subjectPanel = Ext.widget('panel',{
				id:group_id,
				title:'<span title='+title+'>'+group_name+'</span>',
				//<input style='width:180px;margin:0px 10px 0px 0px;'></input><button style='background-color:#8BC3F6;border-radius:5px;cursor:pointer;'>保存</button></div>
				iconCls:'subjectTitleDown',
				//collapsible: true,
				collapsed : true,
				border:false,
				padding:'0 30 0 0',
				bodyPadding:10,
				//minHeight:200,
				layout: {
			        type: 'vbox',
			        align: 'stretch'
			    },
				tools:tools,
				listeners:{
					'beforeexpand':function(o){
			    		me.setIcon(o, 'up');//收起按钮
			    		me.getPerson(o);
			    	},
			    	'beforecollapse':function(o){
			    		me.setIcon(o, 'down');//展开按钮
			    	}
		        },
				items:[]
			});
			
			contentPanel.add(subjectPanel);
			contentPanel.add({hidden:true});
			
			//标题前图片点击事件添加
			var titleImg = Ext.select(".subjectTitleDown");
			var tmp = Ext.getDom(titleImg.elements[p].id);
			tmp.onclick = me.clickTitleIcon;
		}
		
		// 添加按钮
		var addImg = Ext.create('Ext.Img', {
			src: "/images/new_module/nocycleadd.png",
			id:'addsubject',
			style:'border-radius: 50%;cursor:pointer;',
			width : 50,
			height : 50,
			margin:'20 0 0 0',
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){ me.openSubjectWindow(); }
		        }
			}
		});
		
		if(this.readonly == '1' || subjectsGloble.isVote){//只读
			addImg = Ext.create('Ext.Img', {
				id:'addsubject'
			});
		}
		contentPanel.add(addImg);
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
		if(!Ext.util.CSS.getRule('.subjectTitleUp')){
			Ext.util.CSS.createStyleSheet(".subjectTitleUp{background-image: url(/images/new_module/collapse.png);cursor:pointer;}","up");
		}
		
		if(!Ext.util.CSS.getRule('.subjectTitleDown')){
			Ext.util.CSS.createStyleSheet(".subjectTitleDown{background-image: url(/images/new_module/expand.png);cursor:pointer;}","down");
		}
		
		if(!Ext.util.CSS.getRule('.delImg')){
			Ext.util.CSS.createStyleSheet(".delImg{ position:relative !important; left:47px !important; top:0px !important; }","delImg");
		}
	},
	// 刷新学科组内专家
	loadPerson:function(data, action){
		var me = this;
		
		var group_id = action.group_id;
		var obj = new Object();
		obj.id = group_id;
		me.getPerson(obj);
	},
	//提示是否删除
	openDeleteMsg:function(o){
		var me = this;
		var group_id = o.id.substring(4);
		Ext.Msg.confirm("提示信息","是否将该"+zc.menu.subjectsshowtext+"删除？",function(btn){ 
			 if(btn=='yes'){
				 me.deleteSubject(group_id);
			 }
		});
	},
	// 删除学科组
	deleteSubject:function(group_id){
		var me = this;
		var map = new HashMap();
		map.put("type", "3");//删除
		map.put("w0301", me.w0301);
		map.put("group_id", group_id);
		map.put("categoriesid",subjectsGloble.categoriesid);
		Rpc({functionId:'ZC00002212',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg !=""){
				Ext.showAlert(msg);
				return false;
			}else{
				me.getSubjects();
			}
		},scope:this},map);
	},
	// 获取专家
	getPerson:function(o){
		var me = this;
		
		var group_id = o.id;//学科组编号
		var map = new HashMap();
		map.put("w0301", me.w0301);
		map.put("group_id", group_id);
		
		map.put("categoriesid",subjectsGloble.categoriesid);
		Rpc({functionId:'ZC00002211',async:false,success:me.createPerson,groupId:group_id,scope:this},map);
	},
	// 创建专家区域
	createPerson:function(data, action){
		var me = this;
		
		var personList = Ext.decode(data.responseText).personList;
		var group_id = action.groupId;
		var subjectPanel = Ext.getCmp(group_id);//学科组
		subjectPanel.removeAll(true);
		
		
		
		var employPanel = Ext.widget('panel',{
			id:'emp_'+group_id,
			layout:{
				type:'hbox'
			},
			//minHeight:100,
			height:90,
			margin: 0,
			border:false,
			scrollable:'x',
			items:[]
		});
		
		var disemployPanel = Ext.widget('panel',{
			id:'dis_'+group_id,
			layout:{
				type:'hbox'
			},
//			minHeight:100,
			height:80,
			margin: 0,
			border:false,
			items:[{xtype: 'label',text: "未聘任："}]
		});
		
		
		
		for(var p in personList){
			//w0101、w0107、imgUsr、imgA0100、imgQuality、、
			var person = personList[p];
			var w0101 = person.w0101;
			var role = person.role;//角色
			var w0107 = person.w0107;
			var w0103 = person.w0103;//单位名称
			var w0105 = person.w0105;//部门
			var b0110 = person.b0110;//所属机构
			var roleText = "组员";
			if(role == 1){//组长
				roleText = "组长";
			}
			var w0111 = person.w0111;
			var flag = person.flag;
			var filePath = person.filePath.replace(/\//g,'`');
			var imgUsr = person.imgUsr;
			var imgA0100 = person.imgA0100;
			var imgQuality = person.imgQuality;
			var src = "";
			if(w0111 == "1"){//外部专家
				if(filePath.substring(filePath.length-1) != '`'){
					src = "/servlet/DisplayOleContent?filePath="+filePath+"&bencrypt=false&caseNullImg=/images/photo.jpg";
				} else {
					src = '/images/photo.jpg';
				}
			}else if(w0111 == "2"){//内部
				src = "/servlet/DisplayOleContent?nbase="+imgUsr+"&a0100="+imgA0100+"&quality="+imgQuality+"&caseNullImg=/images/photo.jpg";
			}
			
			var style = 'border-radius: 50%;cursor:pointer;';
			if(subjectsMeet_me.readonly == '1'){//只读
				style = 'border-radius: 50%;';
			}
			var task = new Ext.util.DelayedTask();
			var personImg = Ext.create('Ext.Img', {
				id:'pho_'+group_id+'_'+w0101,
			    src: src,
			    style:style,
			    width:50,
			    height:50,
			    padding:0,
			    margin:'0 20 0 0',
			    title:'姓名：'+w0107+'\n角色：'+roleText+'\n单位名称：'+w0103+ '\n部门名称：'+w0105+'' + '\n所属机构：'+b0110,
				listeners: {
					click: {
			        	element: 'el', 
			        	fn: function(a, o){
			        		task.delay(100, function(){
			        			if(subjectsMeet_me.readonly == '1'){//只读
			        				return false;
			        			}
			        			var id = o.id.substring(4);
			        			var group_id = id.split("_")[0];
			        			var w0101 = id.split("_")[1];
			        			me.openEditWindow(me.w0301, group_id, w0101, o.id);
			        		});
				        }
			        },
			        mouseover: {
			            element: 'el', 
			            fn: function(a, o){ me.showHideDelImg(o.id, "1"); }
			        },
					mouseout: {
			        	element: 'el', 
			        	fn: function(a, o){ me.showHideDelImg(o.id,"0"); }
			        }
				}
			});
			var delImg = Ext.create('Ext.Img', {
				src: "/workplan/image/remove.png",
				id:'del_'+group_id+'_'+w0101,
				style:'cursor:pointer;',
				width:20,
				height:20,
				hidden:true,
				cls:'delImg',
				listeners: {
			        click: {
			            element: 'el', 
			            fn: function(a, o, b, c){ me.deletePerson(o.id); }
			        }, mouseover: {
			            element: 'el', 
			            fn: function(a, o){ me.showHideDelImg(o.id, "1"); }
			        },
					mouseout: {
			        	element: 'el', 
			        	fn: function(a, o){ me.showHideDelImg(o.id,"0"); }
			        }
				}
			});
			
			if(this.readonly == '1'){//只读
				delImg = Ext.create('Ext.Img', {
					id:'del_'+group_id+'_'+w0101
				});
			}
			var w0107ShowText = this.convertStr(w0107);
			var person = Ext.widget('panel',{
				id:'per_'+group_id+'_'+w0101,
				layout:{
					type:'vbox',
					align:'center'
				},
				margin: 0,
				border:false,
				items:[personImg,{xtype: 'label',text: w0107ShowText,margin:0,maxWidth:50,height:18,style:'white-space:nowrap;'}, delImg]
			});
			employPanel.add(person);
			
		}
		var addImg = Ext.create('Ext.Img', {
			src: "/images/new_module/nocycleadd.png",
			id:'addP_'+group_id,
			style:'border-radius: 50%;cursor:pointer;',
			width : 50,
			height : 50,
			margin:0,
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){ me.openExpertPicker(o.id); }
		        }
			}
		});
		
		if(this.readonly == '1'){//只读
			addImg = Ext.create('Ext.Img', {
				id:'addP_'+group_id
			});
		}
		
		var addPanel = Ext.widget('panel',{
			layout:{
				type:'vbox'
			},
			margin: 0,
			border:false,
			items:[addImg]
		});
		employPanel.add(addPanel);
		
		subjectPanel.add(employPanel);
	},
	//显示/隐藏删除按钮
	showHideDelImg:function(phoId, state){
		var delId = phoId.substring(4);
		var delImg = Ext.getCmp("del_"+delId);
		if(state == "1"){
			if(Ext.isIE)//解决ie浏览器下，专家人数较多，滑动滚动条到最右后，滚动条自动滑回最左【43665】
				Ext.getDom("del_"+delId).style.display = "block";
			else
				delImg.show();
		}else{
			if(Ext.isIE)
				Ext.getDom("del_"+delId).style.display = "none";
			else
				delImg.hide();
		}
	},
	// 新增专家
	openExpertPicker:function(id){
		var me = this;
		
		var group_id = id.substring(5);
		subjectsGloble.group_id = group_id;
		var map = new HashMap();
		map.put("group_id", subjectsGloble.group_id);
		map.put("w0301", this.w0301);
		Rpc({functionId:'ZC00002214',async:false,success:function(form, action){
			Ext.require("ExpertPicker.ExpertPicker",function(){
				var re = Ext.create("ExpertPicker.ExpertPicker",{
					width:'800',
					height:'500',
					sql:Ext.decode(form.responseText).sql,//加载时sql
					orderBy:Ext.decode(form.responseText).orderBy,//排序
					searchText:'请输入单位名称、部门、姓名...',
					title:'请选择专家',
					callback:subjectsMeet_me.addPerson
				});
			});
		},scope:this},map);
		
	},
	// 新增专家
	addPerson:function(selected){
		var me = this;
		
		var map = new HashMap();
		map.put("type", "1");//新增
		map.put("w0301", subjectsMeet_me.w0301);
		map.put("group_id", subjectsGloble.group_id);
		map.put("personidList", selected);
		map.put("categoriesid", subjectsGloble.categoriesid);
		Rpc({functionId:'ZC00002213',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return false;
			}else{
				subjectsMeet_me.getSubjects();
				var currentPanel = Ext.getCmp(subjectsGloble.group_id);
				currentPanel.expand(false);
			}
		},scope:this},map);
	},
	// 删除专家
	deletePerson:function(id){
		var w0101 = id.substring(4).split("_")[1];//专家编号
		var group_id = id.substring(4).split("_")[0];//学科组编号
		var personList = new Array();//人员信息集
		personList.push(w0101);
		var map = new HashMap();
		map.put("type", "2");//删除
		map.put("w0301", subjectsMeet_me.w0301);
		map.put("group_id", group_id);
		map.put("personidList", personList);
		map.put("categoriesid", subjectsGloble.categoriesid);
		Rpc({functionId:'ZC00002213',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return false;
			}else{
				subjectsMeet_me.getSubjects();
				var currentPanel = Ext.getCmp(group_id);
				currentPanel.expand(false);
			}
		},w0101:w0101,scope:this},map);
	},
	//设定展开/收起按钮
	setIcon:function(o, state){
		var group_id = o.id;//学科组编号
		var subjectPanel = Ext.getCmp(group_id);
		if(state == "up"){
			subjectPanel.setIconCls("subjectTitleUp");
		}else{
			subjectPanel.setIconCls("subjectTitleDown");
		}
	},
	// 打开选专家页面
	openSubjectWindow:function(){
		var me = this;
		var map = new HashMap();
		map.put("w0301", me.w0301);
		Rpc({functionId:'ZC00002215',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			var jsonData = result.tableConfig;
			var obj = Ext.decode(jsonData);
			var tableObj = new BuildTableObj(obj);
			me.subjects_picker_tableObj = tableObj;
			var tableComp = tableObj.getMainPanel();
			
			Ext.create('Ext.window.Window', {
				id : 'subjects_picker_id',
				modal:true,
			    title: '请选择'+zc.menu.subjectsshowtext,
				modal: true,
				layout:'fit',
				//width:Ext.getBody().getWidth()*0.9,
				width:840,
				height:(window.screen.availHeight-150)*0.8,//窗口的高(不含菜单)，150：菜单高度,
				border:false,
				items:[tableComp],
				buttonAlign:'center',
				buttons : [{
					xtype : 'button',
					text : common.button.ok,
					margin:'20 10 0 0',
					handler : function() {me.enter();}
				},{
					xtype : 'button',
					text : "取消",
					margin:'20 0 0 0',
					handler : function() {Ext.getCmp('subjects_picker_id').close();}
				}]
			}).show();
		},scope:this},map);
	},
	// 编辑页面
	openEditWindow:function(w0301, gid, pid, id){
		var me = this;
		var info = subjectsMeet_me.getEditInfo(w0301, gid, pid);
		// 成员角色 数据源
		var store = new Ext.data.ArrayStore({
            fields: ['myId','displayText'],
            data: [[0, '组员'], [1, '组长']]
        });
		
		// 编辑窗口
		var formPanel = Ext.create('Ext.form.Panel', {
		    bodyPadding: '0 10 0 10',
		    margin:0,
			border:false,
		    layout: 'anchor',
		    defaults: {
		        anchor: '90%'
		    },
		    defaultType: 'textfield',
		    items: [{
		    	id:'username',
		        fieldLabel: '帐号',
		        labelWidth:60,
		        labelAlign:'right',
		        allowBlank: false,
		        margin:'20 0 20 0'
		    },{
		    	id:'password',
		        fieldLabel: '密码',
		        labelWidth:60,
		        labelAlign:'right',
		        margin:'0 0 20 0'
		    },{
		    	xtype:'combo',
		    	id:'role',
		    	fieldLabel: '成员角色',
		    	labelWidth:60,
		    	store:store,
		        forceSelection :true,
		        valueField: 'myId',
		        displayField: 'displayText',
		        editable:false,
		        labelAlign:'right',
//		        allowBlank: false,
		        margin:'0 0 20 0'
		    }],
		    buttonAlign:'center',
		    buttons: [{
		        text: '确定',
		        formBind: true, 
		        disabled: true,
		        handler: function() {
		            var form = this.up('form').getForm();
		            if (form.isValid()) {
		            	//确定
		            	subjectsMeet_me.editCommittee(form.getValues(), w0301, gid, pid);
		            	this.up('window').close();
		            }
		        }
		    },{
		        text: '取消',
		        handler: function() {
		            this.up('window').close();
		        }
		    }],
		    renderTo: Ext.getBody()
		});

		//曾经编辑过，需要有初始值
		formPanel.getForm().setValues(info);
		var i = 0;
		if(info.role == '1'){
			i = 1;
		}
		var selValue = store.data.items[i];
		Ext.getCmp('role').setValue(selValue);
		
		//获取展示位置
		var x = Ext.fly(id).getX();
		var y = Ext.fly(id).getY()+70;
		Ext.create('Ext.window.Window', {
			id:'subjectsEditWindow',
			header:false,
			modal:true,
//		    title: title,
			modal: true,
			layout:'fit',
			width:320,
			height:200,
			border:false,
			x:x,
			y:y,
			items:[formPanel],
			listeners:{
				'show':function(){
					Ext.getCmp("username").focus();//自动聚焦编辑框
				}
			}
		}).show();
		
	},
    // 获取评委会信息
    getEditInfo:function(w0301, gid, pid) {
		
		var info = '';
		var map = new HashMap();
		map.put('type', '4');
		map.put('w0301', w0301);
		map.put('group_id', gid);
		map.put('w0101', pid);
		Rpc({functionId:'ZC00002213',async:false,success:function(data){
			var me = this;
			info = Ext.decode(data.responseText).personInfo;
		},scope:this},map);
		
		return info;
	},
	//编辑评委会
	editCommittee:function(values, w0301, gid, pid){
		var me = this;
		var map = new HashMap();
		map.put("type", "3");
		map.put("w0301", w0301);
		map.put("group_id", gid);
		map.put("w0101", pid);
		map.put("username", values['username-inputEl']);
		map.put("password", values['password-inputEl']);
		map.put("role", values['role-inputEl']);
		Rpc({functionId:'ZC00002213',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(!Ext.isEmpty(msg)){
				Ext.showAlert(msg);
				return;
			} else {
				// 更新提示信息
				var img = Ext.getCmp('pho_'+gid+'_'+pid);
				var imgTitle = Ext.getCmp('pho_'+gid+'_'+pid).title;
				if(values['role-inputEl'] == 1) {//组长
					imgTitle = imgTitle.replace('组员', '组长');
					img.setTitle(imgTitle);
				} else if(values['role-inputEl'] == 0) {//组员
					imgTitle = imgTitle.replace('组长', '组员');
					img.setTitle(imgTitle);
				}
				
			}
		},scope:this},map);
	},
	//确定
	enter:function(){
		
		var selectData = this.subjects_picker_tableObj.tablePanel.getSelectionModel().getSelection();//获取数据
		var selectedList = new Array();//人员信息集
		for(var p in selectData){
			var group_id = selectData[p].data.group_id_e;
			selectedList.push(group_id);
		}
		
		for(var i=0; i<selectedList.length; i++){
			var group_id = selectedList[i];
			var map = new HashMap();
			map.put('type', '1');//新增评审会议的学科组
			map.put('w0301', subjectsMeet_me.w0301);
			map.put('group_id', group_id);
			map.put("categoriesid",subjectsGloble.categoriesid);
			Rpc({functionId:'ZC00002212',async:false,success:function(data){
				
			},scope:this},map);
		}
		this.getSubjects();
		if(Ext.getCmp(subjectsGloble.first_group_id)){//展开第一个
			Ext.getCmp(subjectsGloble.first_group_id).expand(false);
		}
		Ext.getCmp('subjects_picker_id').close();
	},
	// 把字符串转化成后面带省略号形式
	convertStr : function(str){
		var reStr = str;
		
		var maxwidth = 8;//字母排列的话最多占的个数
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
    }
});