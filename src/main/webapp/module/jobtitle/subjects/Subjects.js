/**
 * 职称评审_委员会管理_学科组
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 * 
 * */
Ext.define('JobtitleSubjects.Subjects',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
	addVersion : false,         //新建学科组权限
	editVersion : false,        //编辑学科组权限
	deleteVersion : false,      //删除学科组权限
	addPersonVersion : false,   //新增成员权限
	deletePersonVersion : false,//删除成员权限
	showHistoryVersion : false, //显示历史权限
	setLeaderVersion : false,	//设置组长权限   haosl 20190903
	randomSelectionVersion:false,//专家抽选权限  haosl 20160903
	loadMask:'',
	constructor : function(config) {
		subjects_me = this;
		subjectsGloble = {},
		subjectsGloble.ishistory = '0',//是否显示历史
		subjectsGloble.year = '',//显示某年的学科组
		subjectsGloble.group_id = '0',//当前操作的学科组编号
		subjectsGloble.first_group_id = '0',//第一个学科组编号，默认展开第一个时用
		this.init();
	},
	// 初始化函数
	init : function() {
		//屏蔽浏览器右键菜单
		Ext.getDoc().on("contextmenu", function(e){
			e.stopEvent();
		});
		this.createSelfCss();
		this.createMainPanel();// 加载主框架
		this.getSubjects();
	},
	// 获取主页面
	createMainPanel : function(){
		var me = this;
		
		var topPanel = Ext.widget('panel',{
			//style:'background-color:#ffffff;',
			autoScroll:false,// 滚动条
			margin: '20 0 10 35',
			border:false,
			layout: {
		        type: 'hbox',
		        align: 'stretch'
		    },
			items:[{
					xtype : 'textfield',
			        id : 'subjectName',
			        maxLength:50,
			        maxLengthText:'最大50位',
			        emptyText:'输入'+zc.menu.subjectsshowtext+'名称',
			        width : 300,
			        height : 25,
			        margin:'0 10 2 0'
				},{
					xtype : 'button',
					text : '新建'+zc.menu.subjectsshowtext,
					//style:'background-color:#F9F9F9;',
//					width:75,
					height:25,
					margin:0,
					handler : function() {
						if(Ext.getCmp('subjectName').isValid()) {
							me.newSubject();
						}
					}
			}]
		});
		var contentPanel = Ext.widget('panel',{
			//style:'background-color:#ffffff;',
			id:'contentPanel',
			autoScroll:false,// 滚动条
//			layout:{
//				type:'accordion',// 手风琴
//				fill:true,
//				collapseFirst:false,
//				//hideCollapseTool:true,//隐藏收缩按钮
//				titleCollapse:false,//允许通过点击标题栏的任意位置来展开/收缩
//				multi:true,
//				animate: true
//			},
			//collapseFirst:true,
			margin: '0 30 0 30',
			border:false,
			items:[]			
		});
		
		/*var listview = undefined;
		listview = Ext.widget('label',{
			xtype : 'label',
			id:'listview',
			text : zc.msg.listview,
			margin:'4 10 0 0',
			style:'cursor:pointer;color::#22549b;',
			listeners : {
				click:{
					element:'el',
					fn:function() {
						window.location.href="/module/jobtitle/subjects/Subjects.html";
					}
				},
		        scope:this
			}
		});*/
		
		//创建时间的数据源
		var store = Ext.create('Ext.data.Store', {
			fields: ['value','name'],
			proxy:{
				type: 'transaction',
		        functionId:'ZC00002201',
				extraParams:{
					ishistory:subjectsGloble.ishistory
				},
				 reader: {
					  type: 'json',
					  root: 'yearList'
				}
			},
			autoLoad: true
		});
		var comboflag = subjectsGloble.ishistory=='1'?false:true;
		var tools = Ext.widget('container', {
			layout: {
		        type: 'hbox'
		    },
			items: [{
	    	xtype: 'combo',
	    	id:'create_time',
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
						me.loadMask = new Ext.LoadMask(Ext.getCmp('subjectsviewport'), {
							msg:"请稍后…"
						});
						me.loadMask.show();
						subjectsGloble.year = v;
						me.subjectsHistoty(v);
					}
				}
			}
		},{
	        xtype: 'checkboxfield',
	        boxLabel:'显示历史',
	        id:'ishistory',
	        margin:'0 10 0 0',
	        //handler : function(o,v) {me.subjectsHistoty(v); },
			listeners: {
				change: {
					fn: function(a, v){
						me.loadMask = new Ext.LoadMask(Ext.getCmp('subjectsviewport'), {
							msg:"请稍后…"
						});
						me.loadMask.show();
						me.subjectsHistoty(v);}
				}
			}
	     },{
	     	xtype: 'image',
	     	src: '/images/new_module/listview.png',
	     	id:'listview',
			width:16,
			height:16,
			title:zc.msg.listview,
			margin:'5 10 0 0',
			style:'cursor:pointer;',
			scope:this,
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(){
		            	window.location.href="/module/jobtitle/subjects/Subjects.html";
		            }
		        }
			}
	     }]
		});
			     
		var mainPanel = Ext.widget('panel',{
				title:zc.menu.subjectsshowtext + '成员',
				autoScroll:true,// 滚动条
				border:false,
				tools: '',
				items:[]
		});
		
		mainPanel.addTool(tools);
		mainPanel.add(topPanel);
		mainPanel.add(contentPanel);
		
		Ext.create('Ext.container.Viewport', {
			id:'subjectsviewport',
			style:'backgroundColor:white',
			layout:'fit',
			items:[mainPanel]
	    });
	},
	//新建学科组
	newSubject:function(){
		var me = this;
		
		if(!this.addVersion){//无权限
			Ext.showAlert("对不起，无操作权限！");
			return ;
		}
		
		var name = Ext.get("subjectName-inputEl").dom.value;
		if(!this.checkSubjectName(name)){//名称校验
			Ext.get("subjectName-inputEl").dom.value = '';
			return ;
		}
		var map = new HashMap();
		map.put("type", "1");
		map.put("subjectsName", keyWord_filter(name));
		Rpc({functionId:'ZC00002203',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg !=""){
				Ext.showAlert(msg);
				return ;
			}else{
				Ext.getCmp("subjectName").reset();
				subjectsGloble.first_group_id = '0';
				me.getSubjects();
			}
		},scope:this},map);
	},
	// 获取学科组
	getSubjects:function(){
		var me = this;
		var map = new HashMap();
		map.put("ishistory", subjectsGloble.ishistory);
		map.put("year", subjectsGloble.year);
		Rpc({functionId:'ZC00002201',async:false,success:me.createSubjects,scope:this},map);
	},
	// 创建学科组区域
	createSubjects:function(data){
		var me = this;
		
		var contentPanel = Ext.getCmp("contentPanel");
		if(contentPanel)
			contentPanel.removeAll(true);//haosl 20170314  将清空容器内的自组件提前避免组件id重复 导致页面报错  
		var subjectsArr = [];
		var result = Ext.decode(data.responseText);
		
		this.addVersion = result.addVersion;				  //新建学科组权限
		this.editVersion = result.editVersion;				  //编辑学科组权限
		this.deleteVersion = result.deleteVersion;			  //删除学科组权限
		this.addPersonVersion = result.addPersonVersion;	  //新增成员权限
		this.deletePersonVersion = result.deletePersonVersion;//删除成员权限
		this.showHistoryVersion = result.showHistoryVersion;  //显示历史权限
		this.setLeaderVersion = result.setLeaderVersion		  //设置组长权限
		this.randomSelectionVersion = result.randomSelectionVersion	 //专家抽取权限
		
		var subjectslist = result.subjectslist;
		for(var p in subjectslist){
			var subject = subjectslist[p];
			var group_name = subject.group_name;
			var create_time = subject.create_time;
			var b0110 = subject.b0110;
			//b0110 = b0110!=''?'('+ b0110 +')':'';
			b0110 = b0110 != '' ? '&nbsp;归属于&nbsp;'+ b0110 : '';
			var group_id = subject.group_id;
			if(subjectsGloble.first_group_id == '0'){
				subjectsGloble.first_group_id = group_id;
			}
			var isCheck = false;
			if(subject.ishistory == "1"){
				isCheck = true;
			}
			
			var marginRight = '53';//不任聘时不显示编辑和删除按钮，需重新计算右边距
			var state = subject.state;
			if(state == '1'){//任聘
				marginRight = '10';
			}
			var his_check = {//haosl 20160906
				xtype: 'checkboxfield',
				id:'his_'+group_id,
				boxLabel:'历史',
				checked : isCheck,
				margin:'0 '+marginRight+' 0 10',
				handler : function(o, v) {me.personHistoty(o.id, v);}
			}
			var tools = [];
			var mod_img = {//haosl 20160906
					// 修改学科组
					xtype:'image',
					id:"mod_"+group_id,
					alt:group_name,
					//icon: '/images/new_module/edit.png',
					src: '/workplan/image/edit.png',
					style:'cursor:pointer;',
					margin:'0 10 0 0',
					width:16,
					height:16,
					listeners: {
						click: {
							element: 'el', 
							fn: function(a, o){ me.modifySubject(o); }
						}
					}
				};
			var del_img = {// 删除学科组//haosl 20160906
				xtype:'image',
				id:"del_"+group_id,
				//icon: '/images/new_module/listview.png',
				src: '/workplan/image/chahao.png',
				style:'cursor:pointer;',
				width:16,
				height:16,
				listeners: {
					click: {
						element: 'el', 
						fn: function(a, o){ me.openDeleteMsg(o); }
					}
				}
			}
			
			//haosl 20160906修改
			if(this.showHistoryVersion){
				tools.push(his_check);
			}
			if(state == '1'){ //任聘标识为任聘的学科组
				if(this.editVersion){
					tools.push(mod_img);
				}
				if(this.deleteVersion){
					tools.push(del_img);
				}
			};
			var title = group_name + b0110;
			title = '<span title='+title+'>'+group_name+'</span>&nbsp;&nbsp;';
			if(me.randomSelectionVersion){	//专家抽取权限控制  haosl 20160903
				if(state == '1'){
					title = group_name + b0110;
					title = '<span title='+title+'>'+group_name+'</span>&nbsp;&nbsp;<a id="tit_'+group_id+'" style="cursor:pointer;color:#22549b;" onclick="subjects_me.randomSelection(this.id);">专家抽取</a>';
				}
			}
			var subjectPanel = Ext.widget('panel',{
				id:group_id,
				title:title,
				iconCls:'subjectTitleDown',
				//collapsible: true,
				collapsed : true,
				border:false,
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
		        html:'<input id="hidden_'+group_id+'" type="hidden" value='+state+'></input>',
				items:[]
			});
			subjectsArr[p]=subjectPanel;
			
		}
		/*haosl20161125  优化   以数组作容器，在学科组面板全部创建好后，通过数组一次性添加到contentPanel中
		  防止频繁向pannel中添加，导致页面加载缓慢
		*/
		if(contentPanel)
			contentPanel.add(subjectsArr);
		var titleImg = Ext.select(".subjectTitleDown");
		for(var p in subjectslist){
			//标题前图片点击事件添加
			var tmp = Ext.getDom(titleImg.elements[p].id);
			tmp.onclick = me.clickTitleIcon;
		}
		if(Ext.getCmp(subjectsGloble.first_group_id)){//展开第一个
			Ext.getCmp(subjectsGloble.first_group_id).expand(false);
		}
		// 关闭等待
		if(me.loadMask){
			me.loadMask.hide();
		}
	},
	randomSelection:function(id){
		
		var group_id = id.substring(4);
		subjectsGloble.group_id = group_id;
		Ext.require('ExpertPicker.RandomSelection',function(){
			var re = Ext.create('ExpertPicker.RandomSelection',{
				addCallback:subjects_me.addPerson,
				committeeId:group_id
			});
		});
	},
	//点击展开事件
	clickTitleIcon:function(a){
		var subjectPanel = Ext.getCmp("contentPanel").queryById(this.id.split("_")[0]);
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
			Ext.util.CSS.createStyleSheet(".delImg{ position:relative !important; left:40px !important; top:0px !important; }","delImg");
		}
		
		if(!Ext.util.CSS.getRule('.zuzImg')){
			Ext.util.CSS.createStyleSheet(".zuzImg{ position:relative !important; left:3px !important; top:0px !important; }","zuzImg");
		}
	},
	// 学科组显示历史
	subjectsHistoty:function(v){
		var me = this;
		
		if(!this.showHistoryVersion){//无权限
			if(me.loadMask){//没有权限时 隐藏遮罩层 haosl 20160901
				me.loadMask.hide();
			}
			var checkBox = Ext.getCmp("ishistory");
			checkBox.setValue(false);
			Ext.showAlert("对不起，无操作权限！");
			return ;
		}
		
		if(v){
			subjectsGloble.ishistory = "1";
			var combo = Ext.getCmp("create_time");
            combo.setHidden(false);
		}else{
			subjectsGloble.ishistory = "0";
			var combo = Ext.getCmp("create_time");
            combo.setHidden(true);
            subjectsGloble.year = '';
		}
		var map = new HashMap();
		map.put("ishistory", subjectsGloble.ishistory);
		map.put("year", subjectsGloble.year);
		Rpc({functionId:'ZC00002201',async:true,success:me.createSubjects,scope:this},map);
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
		var group_id = o.id.substring(4).split("_")[0];
		var group_name = o.alt;
		var titleHtml = Ext.getCmp(group_id).getTitle();
		var tmpDiv = document.createElement("div");
		tmpDiv.innerHTML = titleHtml;
		
		var title = tmpDiv.childNodes[0].getAttribute('title');
		if(title==null)
			title='';
		var b0110Name = title.split('归属于')[1];
		b0110Name = b0110Name ? b0110Name : '';
		var cp = Ext.getCmp(group_id);//当前的学科组
		cp.setTitle("<input id='ipt_"+group_id+"' style='width:290px;margin:0px 10px 0px 0px;' value='"+group_name+"'></input><a id='ali_"+group_id+"' onclick='subjects_me.saveSubject(this);' style='font-weight:normal;color:#1B4A98;cursor:pointer;' b0110name='"+b0110Name+"'>保存</a></div>");
	},
	// 保存学科组
	saveSubject:function(o){
		var me = this;
		var group_id = o.id.substring(4);//学科组编号
		var b0110Name = o.getAttribute('b0110name');//归属部门
		var iptid = "ipt_"+group_id;
		var name = Ext.get(iptid).dom.value;//新名称
		if(!this.checkSubjectName(name)){
			return ;
		}
		
		var map = new HashMap();
		map.put("type", "2");//修改
		map.put("group_id", group_id);
		map.put("subjectsName", keyWord_filter(name));
		Rpc({functionId:'ZC00002203',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg !=""){
				Ext.showAlert(msg);
				return ;
			}else{
				var subjectPanel = Ext.getCmp(group_id);
				var b0110Name = action.b0110Name ? '&nbsp;归属于&nbsp;'+ action.b0110Name : '';
				var title = '<span title='+title+'>'+name+'</span>&nbsp;&nbsp;';
				if(me.randomSelectionVersion){	//专家抽取权限控制 
					title += '<a id="tit_'+group_id+'" style="cursor:pointer;color:#22549b;" onclick="subjects_me.randomSelection(this.id);">专家抽取</a>';
				}
				subjectPanel.setTitle(title);
				
				var modifyButton = Ext.getCmp('mod_'+group_id);
				modifyButton.setAlt(name);
				//me.getSubjects();
			}
		},name:name,b0110Name:b0110Name,scope:this},map);
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
		map.put("group_id", group_id);
		Rpc({functionId:'ZC00002203',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg !=""){
				Ext.showAlert(msg);
				return ;
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
		map.put("group_id", group_id);
		Rpc({functionId:'ZC00002202',async:false,success:me.createPerson,groupId:group_id,scope:this},map);
	},
	// 创建专家区域
	createPerson:function(data, action){
		var me = this;
		
		var personList = Ext.decode(data.responseText).personList;
		var group_id = action.groupId;

		var state = Ext.getDom('hidden_'+group_id).value;//学科组状态 =1 历史  =0 飞历史
		
		var subjectPanel = Ext.getCmp("contentPanel").queryById(group_id);//学科组
		subjectPanel.removeAll(true);
		
		var checkBox = Ext.getCmp("his_"+group_id);
		var checkState = false;
		if(checkBox){
			checkState = checkBox.getValue();
		}
		
		
		var employPanel = Ext.widget('panel',{
			id:'emp_'+group_id,
			layout:{
				type:'column'
			},
			//minHeight:100,
			minHeight:100,
			margin: 0,
			border:false,
			//scrollable:'x',
			items:[]
		});
		
		if(checkState){
			employPanel.add({xtype: 'label',text: "已聘任："});
		}

		var disemployPanel = Ext.widget('panel',{
			id:'dis_'+group_id,
			layout:{
				type:'column'
			},
//			minHeight:100,
			minHeight:80,
			margin: 0,
			border:false,
			items:[{xtype: 'label',text: "未聘任："}]
		});
		
		var personMap = {};//记录专家id对应的专家名称
		for(var p in personList){
			//w0101、w0107、imgUsr、imgA0100、imgQuality、、
			var person = personList[p];
			var w0101 = person.w0101;
			var w0107 = person.w0107;
			
			personMap[w0101] = w0107;
			
			var w0103 = person.w0103;//单位名称
			var w0105 = person.w0105==null?'':person.w0105;//部门
			var info = person.info;
			var b0110 = person.b0110;//所属机构
			var w0111 = person.w0111;
			var flag = person.flag;
			var role = person.role;
//			var filePath = person.filePath.replace(/\//g,'`');
			var imgUsr = person.imgUsr;
			var imgA0100 = person.imgA0100;
			var imgQuality = person.imgQuality;
			
			//xus 20/5/18 vfs改造 职称评审人员图片从vfs中获取
			var src = '/images/photo.jpg';
			if(person.fileid && person.fileid != 'null' && person.fileid != ''){
				src = "/servlet/vfsservlet?fileid="+person.fileid;
			}
			
			/*if(w0111 == "1"){//外部专家
				if(filePath.substring(filePath.length-1) != '`'){
					src = "/servlet/DisplayOleContent?filePath="+filePath+"&bencrypt=false&caseNullImg=/images/photo.jpg&imageResize=55`55";
				} else {
					src = "/servlet/DisplayOleContent?filePath=123123&bencrypt=false&caseNullImg=/images/photo.jpg&imageResize=55`55";
					//src = '/images/photo.jpg';
				}
			}else if(w0111 == "2"){//内部
				src = "/servlet/DisplayOleContent?nbase="+imgUsr+"&a0100="+imgA0100+"&quality="+imgQuality+"&caseNullImg=/images/photo.jpg&imageResize=55`55";
			}*/
			var personImg = Ext.create('Ext.Img', {
				id:'pho_'+group_id+'_'+w0101,
				style:{
					backgroundImage:'url('+src+');background-size:100% 100%;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src="'+src+'",  sizingMethod="scale");'+(flag==1?'cursor:pointer;':'')
				},
				src:rootPath+'/module/system/personalsoduku/images/55.png',
//			    src: src,
//			    style:'border-radius: 50%;',
			    width:50,
			    height:50,
			    padding:0,
			    //margin:'0 15 0 0',
			    //title:'姓名：'+w0107+'\n单位名称：'+w0103+ '\n部门名称：'+w0105+'' + '\n所属机构：'+b0110,
			    title:info,
				listeners: {
			    	click: {
			            element: 'el', 
			            fn: function(a, o, b, c){
			            	var id = o.id.substring(4);
			            	if(me.setLeaderVersion){//设置组长权限控制 并且为已聘任才可设置组长 haosl 20160903
					            	me.showHideZuzImg(id, "1");
			            	}
			            	me.showHideDelImg(id,"0");
			            }
			        },
			    	mouseover: {
			            element: 'el', 
			            fn: function(a, o){ 
			            	if(me.deletePersonVersion) {
			            		var id = o.id.substring(4);
			            		me.showHideDelImg(id, "1");
			            	}
		            	}
			        },
					mouseout: {
			        	element: 'el', 
			        	fn: function(a, o){
			        		var id = o.id.substring(4);
			        		if(me.deletePersonVersion) {
			        			me.showHideDelImg(id,"0");
			        		}
			        		me.showHideZuzImg(id,"0");
			        	}
			        }
				}
			});
			
			//设置组长
			var zuzImg = undefined;
			if(flag == "1" && state=="1"){//flag 任聘标识
				zuzImg = Ext.create('Ext.Img', {
					src: "/workplan/image/zuz.png",
					id:'zuz_'+group_id+'_'+w0101,
					style:'cursor:pointer;',
					hidden:true,
					cls:'zuzImg',
					listeners: {
				        click: {
				            element: 'el', 
				            fn: function(a, o, b, c){
				            	var w0101 = o.id.substring(4).split("_")[1];//专家编号
				            	var w0107 = personMap[w0101];
				            	var group_id = o.id.substring(4).split("_")[0];//学科组编号
				            	me.openZuzPersonMsg(w0101,group_id,w0107); 
			            	}
				        }, 
				        mouseover: {
				            element: 'el', 
				            fn: function(a, o){if(me.setLeaderVersion){me.showHideZuzImg(o.id.substring(4), "1");} }//设置组长权限控制 haosl 20160903
				        },
						mouseout: {
				        	element: 'el', 
				        	fn: function(a, o){ me.showHideZuzImg(o.id.substring(4),"0"); }
				        }
					}
				});
			}
			var delImg = undefined;
			if(flag == "1" && state=="1"){//flag 任聘标识
				delImg = Ext.create('Ext.Img', {
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
							fn: function(a, o, b, c){ me.openDeletePersonMsg(o.id); }
						}, mouseover: {
							element: 'el', 
							fn: function(a, o){ me.showHideDelImg(o.id.substring(4), "1"); }
						},
						mouseout: {
							element: 'el', 
							fn: function(a, o){ me.showHideDelImg(o.id.substring(4),"0"); }
						}
					}
				});
			}
			var w0107ShowText = this.convertStr(w0107);
			if(role==1)
				w0107ShowText = w0107ShowText + '(组长)';
			var w0107TitleText = w0107ShowText;
			w0107ShowText = me.setString(w0107ShowText,12);//多于6个字则截取
			var person = Ext.widget('panel',{
				id:'per_'+group_id+'_'+w0101,
				//style:'position:relative;',
				layout:{
					type:'vbox',
					align:'center'
				},
				minHeight:90,
				minWidth:60,
				margin: 0,
				border:false,
				margin:'0 15 0 0',
				items:[
					personImg,
					{xtype: 'label',html: '<span id="'+group_id+'_'+w0101+'" title="'+w0107TitleText+'">'+w0107ShowText+'</span>',margin:0,maxWidth:100,height:18,style:'white-space:nowrap;'},
					delImg,
					zuzImg
				]	
			});
			if(flag == "1"){//任聘
				employPanel.add(person);
			}else{
				disemployPanel.add(person);
			}
			
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
		if(!this.addPersonVersion || state!="1"){//无权限、改学科组已被删除
			addImg = Ext.create('Ext.Img', {
				id:'addsubject'
			});
		}
		
		var addPanel = Ext.widget('panel',{
			layout:{
				type:'vbox'
			},
			border:false,
			items:[addImg]
		});
		employPanel.add(addPanel);
		
		subjectPanel.add(employPanel);
		if(checkState){
			subjectPanel.add(disemployPanel);
		}
	},
	//截取字符串
	setString:function(str, len) {
		var strlen = 0;
	    var s = "";
	    for (var i = 0; i < str.length; i++) {
	        if (str.charCodeAt(i) > 128) {
	            strlen += 2;
	        } else {
	            strlen++;
	        }
	        s += str.charAt(i);
	        if (strlen > len) {
	            return s+"...";
	        }
	    }
	    return s;
	},
	//显示/隐藏设置组长按钮
	showHideZuzImg:function(id, state){
		var zuzImg = Ext.getCmp("zuz_"+id);
		if(zuzImg){
			if(state == "1"){
				zuzImg.show();
			}else{
				zuzImg.hide();
			}
		}
	},
	//显示/隐藏删除按钮
	showHideDelImg:function(id, state){
		var delImg = Ext.getCmp("del_"+id);
		if(delImg){
			if(state == "1"){
				delImg.show();
			}else{
				delImg.hide();
			}
		}
	},
	// 新增专家
	openExpertPicker:function(id){
		var me = this;
		
		var group_id = id.substring(5);
		subjectsGloble.group_id = group_id;
		var map = new HashMap();
		map.put('memberType', 'subject');
		map.put("group_id", subjectsGloble.group_id);
		Rpc({functionId:'ZC00002208',async:false,success:function(form, action){
			Ext.require("ExpertPicker.ExpertPicker",function(){
				var re = Ext.create("ExpertPicker.ExpertPicker",{
					width:'800',
					height:'500',
					sql:Ext.decode(form.responseText).sql,//加载时sql
					orderBy:Ext.decode(form.responseText).orderBy,//排序
					searchText:'请输入单位名称、部门、姓名...',
					title:'请选择专家',
					callback:subjects_me.addPerson
				});
			});
		},scope:this},map);
		
	},
	// 新增专家
	addPerson:function(selected){
		var me = this;
		
		var map = new HashMap();
		map.put("type", "1");//新增
		map.put("group_id", subjectsGloble.group_id);
		map.put("personidList", selected);
		Rpc({functionId:'ZC00002204',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return ;
			}else{
				subjects_me.getSubjects();
				var currentPanel = Ext.getCmp("contentPanel").queryById(subjectsGloble.group_id);
				currentPanel.expand(false);
			}
		},scope:this},map);
	},
	//设置专家为组长
	setZuzPerson:function(w0101,group_id,w0107){
		var me = this;
		var personList = new Array();//人员信息集
		personList.push(w0101);
		var map = new HashMap();
		map.put("type", "3");//修改
		map.put("group_id", group_id);
		map.put("personidList", personList);
		map.put("role", '1');
		Rpc({functionId:'ZC00002204',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg != ""){
				Ext.showAlert(msg);
				return ;
			}else{
				//haosl update 2017-07-05 学科组内专家太多时，IE8下设置组长会报“脚本执行时间过长”的错误，所以优化了下   start
				var w0107ShowText = this.convertStr(w0107);
					w0107ShowText = w0107ShowText + '(组长)';
				var w0107TitleText = w0107ShowText;
				w0107ShowText = me.setString(w0107ShowText,12);//多于6个字则截取
				var spans = Ext.query("span[id^='"+group_id+"']");
				for(var i in spans){
					var text = spans[i].innerText;	
					if(text.indexOf("(组长)")>-1){
						spans[i].title=text.replace(/\(组长\)/g,"");
						spans[i].innerText=text.replace(/\(组长\)/g,"");
					}
				}
				var personpanel = Ext.getCmp("per_"+group_id+"_"+w0101);
				personpanel.items.get(1).setHtml('<span id="'+group_id+'_'+w0101+'" title="'+w0107TitleText+'">'+w0107ShowText+'</span>');//设置组长后回写页面
				//haosl update 2017-07-05 学科组内专家太多时，IE8下设置组长会报“脚本执行时间过长”的错误，所以优化了下   end
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
		map.put("group_id", group_id);
		map.put("personidList", personList);
		Rpc({functionId:'ZC00002204',async:false,success:function(form,action){
			var msg = Ext.decode(form.responseText).msg;
			if(msg && msg != ""){
				var succeed = Ext.decode(form.responseText).succeed;
				Ext.showAlert(msg,function(){
					if(succeed){
						//刷新已聘任   haosl add 20170408
						var w0101 = action.w0101;
						var personpanel = Ext.getCmp("per_"+group_id+"_"+w0101);
						if(personpanel!=null){
							var parent = personpanel.findParentByType('panel');
							parent.remove(personpanel,false);
						}
					}
				});
			}else{
				Ext.showAlert(Ext.decode(form.responseText).message);
			}
		},w0101:w0101,scope:this},map);
	},
	//设定展开/收起按钮
	setIcon:function(o, state){
		var group_id = o.id;//学科组编号
		var subjectPanel = Ext.getCmp("contentPanel").queryById(group_id);
		if(state == "up"){
			subjectPanel.setIconCls("subjectTitleUp");
		}else{
			subjectPanel.setIconCls("subjectTitleDown");
		}
	},
	//提示是否设置为组长
	openZuzPersonMsg:function(w0101,group_id,w0107){
		var me = this;
		Ext.Msg.confirm("提示信息","是否将该成员设置为组长？",function(btn){ 
			 if(btn=='yes'){
				 me.setZuzPerson(w0101,group_id,w0107);
			 }
		});
	},
	//提示是否删除
	openDeletePersonMsg:function(id){
		var me = this;
		Ext.Msg.confirm("提示信息","是否将该成员删除？",function(btn){ 
			 if(btn=='yes'){
				 me.deletePerson(id);
			 }
		});
	},
	//学科组名称校验
	checkSubjectName:function(name){
		var flg = true;
		var trimName = trim(name);
		if(trimName=="" || trimName==null || trimName == '输入'+zc.menu.subjectsshowtext+'名称'){
			if(Ext.isEmpty(name) || name == '输入'+zc.menu.subjectsshowtext+'名称'){
				Ext.showAlert(zc.menu.subjectsshowtext+"名称不能为空！");
			}else{
				Ext.showAlert("请不要只输入空格！");
			}
			flg =  false;
		}
		return flg;
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