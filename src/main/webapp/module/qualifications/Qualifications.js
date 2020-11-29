/**
 * 评审条件
 * @createtime Nov 15, 2015
 * @author liubq
 * 
 * */
 Ext.define('QualificationsURL.Qualifications',{
 	totalPageNum:0,
 	pageNum:1,
 	requires:['SYSF.FileUpLoad'],//加载上传控件js
 	conditionObj:undefined,//评审条件数据
 	//构造器
 	constructor:function(config) {
 		bigMe = this;
 		qualificationGloble={};//用来存值
 		qualificationGloble.conditionid = "";
 		qualificationGloble.conditionitemsid = "conditions0";
 		qualificationGloble.conditionitems = {};
 		qualificationGloble.attchmentitems = {};
 		qualificationGloble.contextitems = {};
 		qualificationGloble.geshu =0;
 		qualificationGloble.isAdd ="F";
 		qualificationGloble.htl ="";
 		qualificationGloble.addVersion = false;//创建权限
 		qualificationGloble.editVersion = false;//编辑权限
 		qualificationGloble.deleteVersion = false;//删除权限
 		this.width=document.documentElement.clientWidth;//panel的宽度
		this.height=document.documentElement.clientHeight;//panel的长度
		this.module_type=config.module_type;//职称为1;证照为2
		this.fromUrl=true;//从连接进来
		this.init();
	},
	
 	// initialize function 初始化数据
	init:function(conditionid,conditionitemsid,isAdd) {
		var me = this;
		//屏蔽浏览器右键菜单
		Ext.getDoc().on("contextmenu", function(e){
			e.stopEvent();
		});
		var map = new HashMap();
		map.put("width",me.width);
		map.put("pageNum",this.pageNum);
		map.put("totalPageNum",this.totalPageNum);
		map.put("conditionitemsid",conditionitemsid);
		map.put("conditionid",conditionid);
		map.put("fromUrl",me.fromUrl);
		map.put("module_type",me.module_type);
	    Rpc({functionId:'ZJ100000200',success:me.setHTMLValue,scope:this},map);
	},
	
	//加载页面的值
	setHTMLValue:function(form, action){
		var me = this;
		me.fromUrl=false;
		this.conditionObj = Ext.decode(form.responseText);
		me.module_type = this.conditionObj.module_type;
		if(this.conditionObj.isAdd!=null){
			qualificationGloble.isAdd = this.conditionObj.isAdd;
		}
		qualificationGloble.geshu = this.conditionObj.geshu;
		qualificationGloble.conditionid= this.conditionObj.conditionid;
		qualificationGloble.conditionitemsid = this.conditionObj.conditionitemsid;
		this.totalPageNum=this.conditionObj.totalPageNum,
 		this.pageNum=this.conditionObj.pageNum;
 		qualificationGloble.addVersion = this.conditionObj.addVersion;//创建权限
 		qualificationGloble.editVersion = this.conditionObj.editVersion;//编辑权限
 		qualificationGloble.deleteVersion = this.conditionObj.deleteVersion;//删除权限
 		
 		var viewPort = Ext.getCmp('qualification');
 		if(viewPort){
			Ext.destroy(viewPort);
		}
		this.itemsCreate();
		this.displayF();
		// 关闭等待
		if(bigMe.loadMask){
			bigMe.loadMask.hide();
		}
	},
	
	//加载上中下三个items
	itemsCreate:function(){
		var me = this;
		var conditionitems1 = new Array();//条件列表部分
		var attchmentitems1 = new Array();//附件列表部分
		var contextitems1 = new Array();//内容部分
			
		//条件列名称以及列表
			var lengths = 0;
			var l = 0;
			var geshu = 0;
			geshu = qualificationGloble.geshu;
			if(this.conditionObj.conditions!=null){
			if(this.conditionObj.conditions.length>qualificationGloble.geshu){
				if(this.conditionObj.conditions.length/bigMe.pageNum==qualificationGloble.geshu){
					lengths=qualificationGloble.geshu;
				}else{
					l=qualificationGloble.geshu*(bigMe.pageNum-1);
					if(bigMe.pageNum!=bigMe.totalPageNum){
						lengths=qualificationGloble.geshu;
					}else{
						lengths=this.conditionObj.conditions.length-qualificationGloble.geshu*(bigMe.pageNum-1);
					}
				}
			}else{
				lengths=this.conditionObj.conditions.length;
			}
			}	
			conditionitems1.push(//左翻页
				{
				id:'qianyebuttons',
				xtype:'container',
				height:'100%',
				width:20,
				border:false,
				margin:'0 5 0 5',
				items:[
				{id:'qianyebutton',xtype:'image',src:'/images/new_module/left.png',margin:'20 0 0 0',width:20,height:60,border:false,style : {cursor : 'pointer'}}//,//▽
				],
				listeners : {
					click: {
			            element: 'el', 
			            fn: function(){
			            	bigMe.pageNum = bigMe.pageNum -1;
			            	bigMe.init("","");				
			            }
			        }
				}
				});
			for(var i=l;i<(lengths+l);i++){
				var src = "/images/new_module/qualificationsUNselect.png";
				var zc_series = "";
				if(qualificationGloble.conditionid==this.conditionObj.conditions[i].condition_id){
					src = "/images/new_module/qualificationsSelect.png";
				}
				
					zc_series = this.convertStr(this.conditionObj.conditions[i].zc_series);
					var delImg = Ext.create('Ext.Img', {//删除按钮
						id:'del_'+i,
						src: "/workplan/image/remove.png",
						style:'cursor:pointer;',
						name:zc_series,
						width:20,
						height:20,
						hidden:true,
						cls:'delImg',
						//margin:'-70 -40 100 40'
						listeners:{
							click: {
					            element: 'el', 
					            fn: function(){ 
					            	var conditionid = Ext.getCmp(this.id).up('container').down('image').id.substring(4);
//					            	var name = Ext.getCmp(this.id).name;
					            	Ext.Msg.confirm("提示信息", qualification.module["del_rule_msg_"+me.module_type], function (button) { 
									 	if (button == "yes") {  
						            		var map = new HashMap();
									 		map.put("conditionid",conditionid);
									 		Rpc({functionId:'ZJ100000203',success:function(form,action){
									 			bigMe.init("","");
		                   					},scope:me},map);
									 	}
									 });
								}
					        }
						}
				    });
				    if(this.conditionObj.conditions[i].flag=="true"){
				    	var task = new Ext.util.DelayedTask();
					    conditionitems1.push({
							xtype:'container',
							id:'conditions'+i,
							height:'100%',
							width:80,
							layout:{
								type:'vbox',
								align:'center'
							},
							items:[{
									xtype:'image',
									id:'img_'+this.conditionObj.conditions[i].condition_id,
									margin:'10 0 0 0',
									width:40,
									height:40,
									src:src,
									title:''+this.conditionObj.conditions[i].zc_series,
									border:false,
									listeners : {
										click:{
											element: 'el', 
						           			fn: function(){
						           				
												bigMe.loadMask = new Ext.LoadMask(Ext.getCmp('qualification'), {
													msg:"请稍候…"
												});
						           				task.delay(200, function(){
						           				
													bigMe.loadMask.show();
													var container = Ext.getCmp(this.id).up('container');
							            			var a = (container.id).substr(10, container.id.length);
													var b = 0;
													b=parseInt(a);
													bigMe.init(bigMe.conditionObj.conditions[b].condition_id,container.id,"");
						           				}, this, []);
						           				
						           				
						           				
						            		}
										},
										dblclick:{
											element: 'el', 
						           			fn: function(){
						           				task.delay(200, function(){
							           				if(!qualificationGloble.editVersion){//无编辑权限
							           					return ;
							           				}
							           				var container = Ext.getCmp(this.id).up('container');
							           				//by haosl 20170621   解决问题： 不定位到当前评审条件直接双击，修改名称时，不是修改的当前双击的评审条件
							           				var condition_id = this.id.substr(4,this.id.length);
							           				var titlename = Ext.getCmp(container.id).items.getAt(0).title;//评审条件名称
							            			bigMe.editConditionName(titlename,condition_id);
						           				}, this, []);
						            		}
										}
									}
								},{
									xtype: 'label',
									maxWidth:80,
									style:'word-wrap:break-word;',
									html: '<span title='+zc_series+'>'+zc_series+'</span>',
									margin:'0 0 0 0'
								},
								delImg
							],
							border:false,
							style : {cursor : 'pointer'},
							listeners : {
								mouseover: {
						            element: 'el', 
						            fn: function(){
						            	if(!qualificationGloble.deleteVersion) {
						            		return;
						            	}
						            	me.showHideDelImg(this.id, "1");
						            		
						            }
						        },
						        mouseout: {
						        	element: 'el', 
						        	fn: function(){ if(!qualificationGloble.deleteVersion) {return;} me.showHideDelImg(this.id,"0");}
						        }
							}
						});
					} else {
						conditionitems1.push({
							id:'conditions'+i,
							height:'100%',
							xtype:'container',
							width:80,
							layout:
							{
							type:'vbox',
							align:'center'
							},
							items:[
							{xtype:'image',id:'img_'+this.conditionObj.conditions[i].condition_id,margin:'10 0 0 0',height:40,width:40,src:src,title:''+this.conditionObj.conditions[i].zc_series,border:false},
							{xtype: 'label',width:80,style:{"text-align":"center"},text: zc_series,margin:'0 0 0 0'},
							delImg
							],
							border:false,
							listeners : {
									render : function() {
											this.getEl().on('click', function() {
											for(var j =0;j<Ext.get("conditionitems-targetEl").dom.childNodes.length;j++){							
												Ext.get("conditionitems-targetEl").dom.childNodes[j].style.backgroundColor="";
											}
											var a = (this.id).substr(10, this.id.length);
											var b = 0;
											b=parseInt(a);
											bigMe.init(bigMe.conditionObj.conditions[b].condition_id,this.id);
											},this);
										}
							},
							style : {cursor : 'pointer'}
							}
						);
					}

			}
			var addimg = {
				xtype:'image',
				title:qualification.module["add_title_"+me.module_type],
				src:'/images/new_module/nocycleadd.png',
				width:50,
				height:50,
				border:false,
				listeners : {
					click : {
						element:'el',
						fn:function() {
							if(!qualificationGloble.addVersion){//无创建权限
								return ;
							}
							me.addCondition();
						}
					}
				}
			};
			if(!qualificationGloble.addVersion){//无创建权限
				addimg = undefined;
			}
			conditionitems1.push(
				{
				id:'addcondition',
				xtype:'container',
				height:'100%',
				width:50,
				layout:
				{
				type:'vbox',
				align:'center'
				},
				style: "cursor:pointer",
				margin:'10,0,0,0',
				items:[addimg],
				border:false
				//style : {cursor : 'pointer'},
				}
				);
				conditionitems1.push(
				{
				id:'houyebuttons',
				xtype:'container',
				height:'100%',
				width:20,
				border:false,
				margin:'0 5 0 5',
				items:[
				{id:'houyebutton',xtype:'image',src:'/images/new_module/right.png',margin:'20 0 0 0',width:20,height:60,border:false,style : {cursor : 'pointer'}}//,//▽
				],
				listeners : {
					click: {
			            element: 'el', 
			            fn: function(){
			            	bigMe.pageNum = bigMe.pageNum +1;
			            	bigMe.init("","");				
			            }
			        }
				}
				});
				
				
		//附件名称以及列表
		if(this.conditionObj.conditions!=null&&this.conditionObj.conditions.length>0){
			var k =0;
			for(var a= 0;a<this.conditionObj.conditions.length;a++){
				if(qualificationGloble.conditionid==this.conditionObj.conditions[a].condition_id){
					k=a;
					break;
				}
			}
			if(qualificationGloble.conditionid==this.conditionObj.conditions[k].condition_id){
				var attachlist = this.conditionObj.conditions[k].attachmentlist;
				for(var h = 0; h<attachlist.length; h++){
					var showName = this.convertAtStr(attachlist[h].name);
					var delImg = Ext.create('Ext.Img', {
						id:'delattach_'+h,
						src: "/workplan/image/remove.png",
						style:'cursor:pointer;',
						width:20,
						height:20,
						fileid:attachlist[h].fileid,
						hidden:true,
						cls:'delImg',
						//margin:'-70 -40 100 40'
						listeners:{
							click: {
					            element: 'el', 
					            fn: function(event,delImg){ 
					            	 //20/3/6 xus vfs改造 
					            	 var fileid = this.el.component.fileid;
									 Ext.Msg.confirm("提示信息", qualification.del_file_msg, function (button) { 
									  var map2 = new HashMap();
									 	if (button == "yes") {		 		
									 		map2.put("fileid",fileid);
									 		map2.put("conditionid",qualificationGloble.conditionid);
									 		Rpc({functionId:'ZJ100000204',success:function(form,action){
									 			//只是为了刷新附件列表没有必要初始化数据 haosl 20170407 delete
									 			//bigMe.init(qualificationGloble.conditionid,qualificationGloble.conditionitemsid);
									 			//删除附件后，移除附件图片。haosl 20170407 add
									 			var isdelete = Ext.decode(form.responseText).isdelete;
									 			if(isdelete){
									 				var index = delImg.id.split("_")[1];
									 				//得到评审条件附件列表的父容器
									 				var attchmentitemsPanel = Ext.getCmp("attchmentitemsPanel");
								 					attchmentitemsPanel.remove(Ext.getCmp("attachment"+index));
									 			}else
									 				 Ext.showAlert(qualification.del_file_fail);
									 		},scope:this},map2);
									 	}
									 });
								}
					        },
					        mouseover: {
					            element: 'el', 
					            fn: function(){
					            	me.showHideDelaImg(this.id, "1"); 
					            }
					        },
					        mouseout: {
					        	element: 'el', 
					        	fn: function(){ 
					        		me.showHideDelaImg(this.id,"0"); 
					        	}
					        }
						}
				    });
				    attchmentitems1.push({
						id:'attachment'+h,
						xtype:'container',
						height:'100%',
						width:80,
						border:false,
						style : {
							cursor : 'pointer'
						},
						layout:{
							type:'vbox',
							align:'center'
						},
						items:[{
							xtype:'image',
							id:'attachmImg'+h,
							title:attachlist[h].name,
							src:attachlist[h].src,
							fileid:attachlist[h].fileid,
							width:45,
							height:45,
							margin:'10 0 0 0',
							border:false,
							listeners : {
								mouseover: {
						            element: 'el', 
						            fn: function(){
						            	if(bigMe.conditionObj.conditions[k].flag == "false"){//没有操作权限
						            		return ;
						            	}
						            	if(!qualificationGloble.deleteVersion) {// 没有删除权限
						            		return ;
						            	} 
						            	me.showHideDelaImg(this.id, "1"); 
						            }
						        },
						        mouseout: {
						        	element: 'el', 
						        	fn: function(){ 
						        		if(bigMe.conditionObj.conditions[k].flag == "false"){//没有操作权限
						            		return ;
						            	}
						        		if(!qualificationGloble.deleteVersion) {// 没有删除权限
						        			return;
						        		} 
						        		me.showHideDelaImg(this.id,"0"); 
						        	}
						        },
						        click: {
						        	element: 'el', 
						        	fn: function(){
//				                 		window.location.href = "/servlet/DisplayOleContent?bencrypt=true&filename="+attachlist[(this.id).substring(10)].encryptname+"&filePath="+attachlist[(this.id).substring(10)].path;
						        		//20/3/6 xus vfs改造
						        		window.location.href = "/servlet/vfsservlet?fileid="+this.component.fileid;
						        	}
						        }
							}
						},{
							xtype: 'label',
							maxWidth:80,
							text: showName
						},delImg ]
					});
				}		
			}
				
			// 新增附件
			var addAttchmentImg = undefined;
			if(this.conditionObj.conditions[k].flag == "true"){//有操作权限
				addAttchmentImg = Ext.widget('image',{
					title:qualification.add_file,
					src:'/images/new_module/nocycleadd.png',
					width:50,
					height:50,
					margin:'10 0 0 0',
					border:false,
					listeners : {
						click:{
							element:'el',
							fn:function(){
								if(Ext.getCmp("editbutton").hidden){
									Ext.Msg.confirm("提示信息", qualification.module["save_rule_msg_"+me.module_type], function (button) { 
										if (button == "yes") {
											me.saveContent();
										}else{
											me.cancelContent();
										}
										me.addAttachment();
									});
								}else{
									me.addAttachment();
								}
							}
						}
					}
				});
			}
			
			attchmentitems1.push({
				id:'addattchment',
				height:'100%',
				xtype:'panel',
				layout:{
					type:'vbox',
					align:'center'
				},
				margin:'5,0,5,0',
				items:[addAttchmentImg],				
				//width:'5%',
				border:false,
				style : {cursor : 'pointer'}
			});
		}	
		
		//内容列表
		var text ="";
		var config = {};
    	config.width = this.width;
    	config.height = document.documentElement.clientHeight-338;
    	config.language = "zh-cn";
    	config.resize_enabled = false;//是否使用“拖动以改变大小”功能
    	config.removePlugins = "elementspath";//去掉底部路径显示栏
    	config.image_previewText = ' ';//图片预览区域显示内容
    	config.baseFloatZIndex = 19900;//保证弹出菜单在最上层，不会被Ext菜单遮住
    	
    	config.toolbar = [
		[ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Bold', 'Italic',"Image","Format","FontSize","TextColor" ,"Link" ,"Unlink","CodeSnippet"],['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock']];
		var CKEditor = Ext.create("EHR.ckEditor.CKEditor",{
			id:'ckeditorid',
			border:false,
			hidden:'true',
			width:'100%',
			height:'100%',
			//height:document.documentElement.clientHeight-338,
			//ckEditorConfig:config
			functionType:'standard'
		});
		var editbuttonHiddenFlag = true;
		if(this.conditionObj.conditions.length!=0){
			var b =0;
			for(var a= 0;a<this.conditionObj.conditions.length;a++){
				if(qualificationGloble.conditionid==this.conditionObj.conditions[a].condition_id){
					b=a;
					break;
				}
			}
			if(this.conditionObj.conditions[b].flag=="true")
				editbuttonHiddenFlag = false;
			else
				editbuttonHiddenFlag = true;
		}else
			editbuttonHiddenFlag = true;
		var editbutton = new Ext.widget("button",{
			id:'editbutton',
			xtype:'button',
			text:qualification.edit,
			hidden:editbuttonHiddenFlag,
			margin:'2 2 2 2',
			style : {cursor : 'pointer'},
			listeners : {
				render : function() {
					this.getEl().on('click', function() {
						me.editContent();
					});
				}
			}
		});
		var savebutton = new Ext.widget("button",{
			id:'savebutton',
			xtype:'button',
			text:qualification.save,
			style : {cursor : 'pointer'},
			listeners : {
				render : function() {
					this.getEl().on('click', function() {
						me.saveContent();
					});
				}
			}
		});
		var cancelbutton = new Ext.widget("button",{
			id:'cancelbutton',
			xtype:'button',
			text:qualification.cancel,
			margin:'0 0 0 2',
			style : {cursor : 'pointer'},
			listeners : {
				render : function() {
					this.getEl().on('click', function() {
						me.cancelContent();
					});
				}
			}
		});
		var afteredit = new Ext.widget("panel",{
				id:'afteredit',
				layout:'hbox',
				border:false,
				margin:'2 2 0 2',
				items:[savebutton,cancelbutton]
				});
		var htm = (qualificationGloble.conditionitemsid).substring(10);
		if(bigMe.conditionObj.conditions.length>0){
			 qualificationGloble.htl = bigMe.conditionObj.conditions[htm].description;
		}
		var contenxttext1 = new Ext.widget("panel",{
				id:'contenxttext',				
				width:'100%',
				height:'100%',
				border:false,
				html:'<div style="margin-left:10px;margin-top:10px;">'+qualificationGloble.htl+'</div>'
				});	
				
		if(!qualificationGloble.editVersion){//编辑权限
			CKEditor = undefined;
			//控制编辑权限 haosl 20160902
			savebutton.hide();
			cancelbutton.hide();
			editbutton.hide();
		}
		if(this.conditionObj.conditions.length>0){
			contextitems1.push(
				{
				id:'textcontenxtbuttons',
				border:false,
				height:25,
				layout:'hbox',
				items:[
				editbutton,afteredit
				]
				},
				{
				id:'textcontenxt',
				width:'100%',
				height:'100%',
				flex:90,
				bodyStyle:"overflow-x:hidden;overflow-y:auto",
				xtype:'panel',
				border:false,
				items:[CKEditor,contenxttext1]
				}
				);	
		}else{
			contextitems1.push({id:'textcontenxt',margin:'5 0 0 0',html:'&nbsp;'+qualification.module["add_rulename_"+me.module_type],border:false});	
		}
		Ext.getCmp('afteredit').setVisible(false);
		
		qualificationGloble.conditionitems =conditionitems1;
		qualificationGloble.attchmentitems =attchmentitems1;
		qualificationGloble.contextitems = contextitems1;
	},
	
	//编辑按钮
	editContent:function(){
		Ext.getCmp("textcontenxt").bodyStyle="";
		if(Ext.getDom("ckeditorid"))
			Ext.getDom("ckeditorid").style.height=document.documentElement.clientHeight-288;
//		var i = (295 - (qualificationGloble.htl).length)/18;
//		var gtl = "";
//		for(j=0;j<(i-8);j++){
//			gtl+="<br>";
//		}
		if((qualificationGloble.htl).length == 0){
			var gtl = "";
			for(i = 0;i < 5;i++){
				gtl+="<br>";
			}
			qualificationGloble.htl = qualificationGloble.htl + gtl;
		}
		Ext.getCmp("ckeditorid").setValue(qualificationGloble.htl);
		Ext.getCmp("ckeditorid").setHidden(false);
		Ext.getCmp('editbutton').hidden = true;	//haosl 20160830 Ext.getCmp('editbutton').setHidden(true)时有问题
		Ext.getCmp('contenxttext').setHidden(true);  
		Ext.getCmp('afteredit').setHidden(false); 
	},
	
	//取消
	cancelContent:function(){
		Ext.getCmp("textcontenxt").bodyStyle="overflow-x:hidden;overflow-y:auto";
		Ext.getCmp("ckeditorid").setHidden(true);
		Ext.getCmp('editbutton').setHidden(false);
		Ext.getCmp('afteredit').setHidden(true);
		Ext.getCmp('contenxttext').setHidden(false);
	},
	
	//保存内容
	saveContent:function(){
		var map3 = new HashMap();
		qualificationGloble.htl = Ext.getCmp('ckeditorid').getHtml();
		map3.put("conditionid",qualificationGloble.conditionid);
		//html数据传输需要加密  haosl 2017-06-26 update
		map3.put("texthtml",getEncodeStr(qualificationGloble.htl));
		Rpc({functionId:'ZJ100000205',success:function(){
			document.getElementById("contenxttext-innerCt").innerHTML = '<div style="margin-left:10px;margin-top:10px;">'+qualificationGloble.htl+'</div>';
			Ext.getCmp("textcontenxt").bodyStyle="overflow-x:hidden;overflow-y:auto";
			Ext.getCmp("ckeditorid").setHidden(true);
			Ext.getCmp('editbutton').setHidden(false);
			Ext.getCmp('afteredit').setHidden(true);
			Ext.getCmp('contenxttext').setHidden(false);
		},scope:this},map3);	
	},
	
	//删除图标的显示与隐藏
	showHideDelImg:function(phoId, state){
		var delId = phoId.substring(10);
		var delImg = Ext.getCmp("del_"+delId);
		if(state == "1"){
			delImg.show();
		}else{
			delImg.hide();
		}
	},
	
	//附件删除图标的显示与隐藏
	showHideDelaImg:function(phoId, state){
		var delId = phoId.substring(10);
		var delImg = Ext.getCmp("delattach_"+delId);
		if(state == "1"){
			delImg.show();
		}else{
			delImg.hide();
		}
	},
	
	//展现页面
	displayF:function(){
		var me = this;
		new Ext.Viewport({
			id:'qualification',
    		border:false,
    		layout: {
       		 type: 'vbox',
      		 align: 'center'
    		},
    		renderTo:Ext.getBody(),
    		items: [{
        		xtype: 'panel',
    			id:'conditionitems',
    			border:false,
				style:'border-bottom:1px solid #C5C5C5;',
        		title:qualification.module["title_"+me.module_type],//haosl 20170425 update
        		width: '100%',
	        	height:130,
	        	bodyStyle:"overflow-x:auto;overflow-y:hidden",
        		layout:'hbox',
        		items:qualificationGloble.conditionitems
    		},
    		{
        		xtype: 'panel',
        		title: '',
        		width: '100%',
        		flex:68,
        		border:false,
        		layout:'vbox',
        		items:qualificationGloble.contextitems,
        		listeners:{
        			resize: {
			        	fn: function(){}
			        }
        		}
    		},
    		{
    			id:'attchmentitemsPanel',
        		xtype: 'panel',
        		title: '<font style="font-size:12px">附件</font>',
        		border:false,
        		width: '100%',
        		layout:'hbox',
        		height:130,
        		scrollable:'x',
        		items:qualificationGloble.attchmentitems        		
    		}]
		});
		if(bigMe.totalPageNum<2){
			Ext.getCmp("qianyebutton").setHidden(true);
			Ext.getCmp("houyebutton").setHidden(true);
		}else{
			if(bigMe.pageNum==1){
				Ext.getCmp("qianyebutton").setHidden(true);
			}
			if(bigMe.pageNum==bigMe.totalPageNum){ 
				Ext.getCmp("houyebutton").setHidden(true);
			}
		}
		if(Ext.getCmp('poi_'+qualificationGloble.conditionid)){
			Ext.getCmp('poi_'+qualificationGloble.conditionid).show();
		}
		if(qualificationGloble.isAdd =='T'){
			qualificationGloble.isAdd = 'F';
			bigMe.editContent();
		}
	},
	
	//点击按钮新增评审条件
	addCondition:function(){
		var me = this;
		   var win = Ext.create('Ext.window.Window', {
				title	  : qualification.module["add_title_"+me.module_type], 
				width	  : 350, 
				height	  : 120,
				modal	  : true, 
				border    : false, 
				layout: {
			        align: 'middle',
			        pack: 'center',
			        type: 'vbox'
				},
				items:[{xtype:'textfield',id:'zcname',width:200,emptyText:'请输入名称',margin:'15 0 20 0'}],
				buttonAlign:'center',
				listeners:{
					'show':function(){
						Ext.getCmp("zcname").focus();//自动聚焦编辑框 haosl 20161102
					}
				},
				buttons:[{
                		text: qualification.sure,
            			handler: function () {
               				var value = (document.getElementById('zcname-inputEl').value).replace(/(^\s*)|(\s*$)/g, "");
               				if(value.length == 0){
               					var inputValue = Ext.get("zcname-inputEl").dom.value;
                   				if(inputValue.length > 0){
                   					Ext.showAlert(qualification.empty_msg);
                   				}else{
                   					Ext.showAlert(qualification.edit_title);
                   				}
                   				Ext.get("zcname-inputEl").dom.value = "";
                   				return ;
                			} 
               				if(value.length>0 && value.length<=75){
               			    	var map1 = new HashMap();
               			    	value = keyWord_filter(value);
               					map1.put("zc_series",value);
               					map1.put("module_type",me.module_type);
               					Rpc({functionId:'ZJ100000201',async:false,success:function(form,action){
                   					win.close();
                   					bigMe.init(Ext.decode(form.responseText).condition_id);
               					},scope:this},map1);
               				} else if(value.length > 75){
               					Ext.showAlert(qualification.max_msg);
               					return ;
               				}
               				
            			}
        			},{
        			text: qualification.cancel,
        			handler: function () {
        				win.close();
        			}
    			}]
			}); 
			win.show();
	},
	
	//上传附件
	addAttachment:function(){
		var me = this;
	 		var vo = new HashMap();
			vo.put("type","judge");
	  		Rpc({functionId:'ZJ100000202',success:function(form,action){
	  			var me = this;
	  			var fileSizeLimit = Ext.decode(form.responseText).fileSizeLimit;
	  			//所有路径不对的，在上传了文件之后判断吧，否则可能出现点击了不上传这种，出现了空文件夹
				/*var pathToF = true;
				if(Ext.decode(form.responseText).pathToF!=null){
					pathToF =Ext.decode(form.responseText).pathToF;
				}
				if(!pathToF){
					var msge = Ext.decode(form.responseText).msge;
					Ext.showAlert(msge);
                    return;
                }*/    				   
			//上传控件
		   	var uploadObj = Ext.create("SYSF.FileUpLoad",{
		   				upLoadType:1,
		   				fileExt:"*.xls;*.xlsx;*.doc;*.docx;*.ppt;*.pptx;*.pdf;*.txt",
		   				cls:'',
		   				fileSizeLimit:fileSizeLimit,
		   				height: 30,
		   				//是否为临时文件 true是，false不是
		   			    isTempFile:false,
		   			    //关联VfsFiletypeEnum 文件类型 例：VfsFiletypeEnum.doc
		   			    VfsFiletype:VfsFiletypeEnum.doc,
		   			    //关联VfsModulesEnum 模块id 例：VfsModulesEnum.CARD
		   			    VfsModules:VfsModulesEnum.ZC,
		   			    //关联VfsCategoryEnum 文件所属类型 例：VfsCategoryEnum.personnel
		   			    VfsCategory: VfsCategoryEnum.other,
		   			    //所属类型guidkey
		   			    CategoryGuidKey: '',
		   			    //文件扩展标识（特殊情况才需要，平常可传空字符，不为空时，长度不得少于6位）
		   			    filetag:qualificationGloble.conditionid,
		   				//回调方法，失败
		   				error:function(list){
			   				Ext.MessageBox.show({  
								title : common.button.promptmessage,  
								msg :list[0].msg, 
								buttons: Ext.Msg.OK,
								icon: Ext.MessageBox.INFO  
							})
		   				},
		   				success:function(list){
		   					var me = this;
		   					var fileid = list[0].fileid;
		   					var successed = list[0].successed;
		   					var filename = list[0].filename;
		   					var localname = list[0].localname;
		   					var k =0;
		   					for(var a= 0;a<bigMe.conditionObj.conditions.length;a++){
		   						if(qualificationGloble.conditionid==bigMe.conditionObj.conditions[a].condition_id){
		   							k=a;
		   							break;
		   						}
		   					}
		   					var attachlist = bigMe.conditionObj.conditions[k].attachmentlist;
		   					var flag = false;
		   					Ext.Array.each(attachlist, function(obj, index, countriesItSelf) {
		   						var name = obj.name;
		   						if(localname==name){
		   							flag = true;
		   						}
		   					});
		   					bigMe.init(qualificationGloble.conditionid,qualificationGloble.conditionitemsid);
		   					/*if(successed){
		   						Ext.Msg.confirm("提示信息", qualification.file_exist, function (button) { 
		   							if (button == "yes") {
		   								var map1 = new HashMap();
		   								map1.put("list",list);
		   								map1.put("conditionid",qualificationGloble.conditionid);
		   								Rpc({functionId:'ZJ100000202',success:function(form,action){
		   									var result = Ext.decode(form.responseText);
		   							    	var flag=result.succeed;
		   									if(flag==true){
		   										bigMe.init(qualificationGloble.conditionid,qualificationGloble.conditionitemsid);
		   									}else {
		   										Ext.showAlert(result.message);
		   									}
		   								},scope:this},map1);	
		   							}
		   						});
		   					}else{
		   						var map1 = new HashMap();
		   						map1.put("list",list);
		   						map1.put("conditionid",qualificationGloble.conditionid);
		   						Rpc({functionId:'ZJ100000202',success:function(form,action){
		   							var result = Ext.decode(form.responseText);
   							    	var flag=result.succeed;
   									if(flag==true){
   										bigMe.init(qualificationGloble.conditionid,qualificationGloble.conditionitemsid);
   									}else {
   										Ext.showAlert(result.message);
   									}
		   						},scope:this},map1);	
		   					}*/
			   				win2.close();
		   				}
	   				});
			var win2=Ext.widget("window",{
	   			title: qualification.upload_file,
	            modal:true,
	            border:false,
            	width:380,
	   			height: 120,
				bodyStyle : 'background-color:#FFFFFF',
	            closeAction:'destroy',
	            items:[{
	                xtype: 'panel',
	                border:false,
	         		layout:{  
    	             	type:'vbox',  
    	             	padding:'15 0 0 35', //上，左，下，右 
    	             	pack:'center',  
    	              	align:'middle'  
    	            },
	                items:[uploadObj]
	            }]
	    	}); 
	   		win2.show();
	  	},scope:this},vo);
      },    
		// 把字符串转化成后面带省略号形式
	convertStr : function(str){
		var reStr = str;
		var maxwidth = 23;//字母排列的话最多占的个数
		var index = 0;
		var useWidth = 0;
		for(i=0; i<str.length; i++){
			var code = str.charAt(i);
			 if(this.checknum(code)) {//字母或数字
				 //大写字母占得位置更多
				 if(/^[A-Z]*$/.test(code))
					 useWidth+=1.4;
				 else
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
	// 把字符串转化成后面带省略号形式
	convertAtStr : function(str){
		var reStr = str;
		
		var maxwidth = 9;//字母排列的话最多占的个数
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
        var Regx = /^[A-Za-z0-9]*$/;
        if (Regx.test(value)) {
            return true;
        }
        else {
            return false;
        }
    },
    
    //修改评审条件名称
    editConditionName:function(name,conditionid){
    	var me = this;
	 	var win = new Ext.Window({ 
			title	  : qualification.module["edit_rulename_"+me.module_type], 
			width	  : 350,
			height	  : 120,	
			modal	  : true, 
			border    : false, 
			items:[{xtype:'textfield',id:'zcname2',fieldLabel:'新名称',labelSeparator:'',labelAlign:'right',margin:'15 0 20 0',value:name,
				listeners:{
					'focus':function(){
						this.selectText();  //选中文本
					}
				}}],
			buttonAlign:'center',
			listeners:{
				'show':function(){
					Ext.getCmp("zcname2").focus();//自动聚焦编辑框 haosl 20161102
				}
			},
			buttons:[{
        		text: qualification.sure,
    			handler: function () {
    				
    				var value = (document.getElementById('zcname2-inputEl').value).replace(/(^\s*)|(\s*$)/g, "");
    				if(value.length == 0){
       					var inputValue = Ext.get("zcname2-inputEl").dom.value;
           				if(inputValue.length > 0){
           					Ext.showAlert(qualification.empty_msg);
           				}else{
           					Ext.showAlert(qualification.edit_title);
           				}
           				Ext.get("zcname2-inputEl").dom.value = "";
           				return ;
        			} 
       				if(value.length>0 && value.length<=75){
           			    var map1 = new HashMap();
       					value = keyWord_filter(value);
						map1.put("zc_series2",value);
						map1.put("conditionid",conditionid);
       				   Rpc({functionId:'ZJ100000206',success:function(form,action){
           				   win.close();
           				   bigMe.init(conditionid,qualificationGloble.conditionitemsid);
       				   },scope:this},map1);	
       				} else if(value.length > 75){
       					Ext.showAlert(qualification.max_msg);
       					return ;
       				}
    			}
			},{
    			text: qualification.cancel,
    			handler: function () { win.close(); }
			}]
		}).show();; 
    }
 });