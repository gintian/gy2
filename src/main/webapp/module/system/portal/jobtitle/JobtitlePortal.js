/**
 * 职称评审_首页门户
 * @createtime Nov 23, 2016 9:07:55 AM
 * @author chent
 * 
 * */
if(Ext.define)
Ext.define('JobtitlePortalURL.JobtitlePortal', {
	extend :'Ext.panel.Panel',
	alias: 'widget.jobtitleportal', 
	style:'border-width:0px;',
	border:false,
	width:600,//该渲染区域的宽，默认600
	height:200,//该渲染区域的高，默认200
	isLowPixel:false,//是否是低分辨率屏幕1024及以下均为低
	currentMouseIsOverWindow:false,//当前鼠标是否在详情的window上
	currentSelected:'',//当前操作的项目id
	pageNum:0,//总页数
	currentPage:1,//当前所在的页码
	currentPageClickState :'',//当前触发翻页的按钮  1、左 2、右
	messageTextMap:'',
    constructor:function(){
    	jobtitleportal_me = this;
    	if(window.screen.width <= 1024){// 1024宽及以下都算低分辨率
    		this.isLowPixel = true;
    	}
    	//职称评审_首页门户自适应修改 haosl add 20170505 start
    	window.onresize = function(){
    		setTimeout(function(){
    			var mainContainer = Ext.getCmp("mainContainer");//child
        		jobtitleportal_me.width = Ext.getBody().getWidth()/2-30;
        		if(!!mainContainer){
        			mainContainer.setWidth(jobtitleportal_me.width);
        			var child = mainContainer.query("[id^=pan_]");
        			for(index in child){
        				child[index].setWidth(parseInt(jobtitleportal_me.width/4));
        			}
        		}
    		},100);
    	}
    	//职称评审_首页门户自适应修改 haosl add 20170505 end
    	this.width = Ext.getBody().getWidth()/2-30;
    	this.height = 200;
    	this.callParent(arguments);
    	this.getInfoList();
    	
    	var tool = Ext.getCmp('tol0236');//隐藏【more..】按钮
    	if(tool){
    		tool.hide();
    	}
    },
    // 获取项目信息
    getInfoList:function(){
    	var map = new HashMap();
		Rpc({functionId:'ZC00005003',async:false,success:function(form,action){
			jobtitleportal_me.messageTextMap = Ext.decode(form.responseText).infomap;
		},scope:this},map);
    	
    	
    	var map1 = new HashMap();
		Rpc({functionId:'ZC00005001',async:false,success:function(form,action){
			var infolist = Ext.decode(form.responseText).infolist;
	    	jobtitleportal_me.createMainPanel(infolist);
			
		},scope:this},map1);
    },
    // 创建面板区域
    createMainPanel:function(infolist){

		var mainContainer = new Ext.Container({
    		id:'mainContainer',
    		border:false,
    		height:this.height,
    		layout:{
    			type:'hbox'
    		},
    		items:[]
    	});
		var imgSize = 72;//适配低分辨率
		if(this.isLowPixel){
			imgSize = 48;
		}
    	if(!Ext.isEmpty(jobtitleportal_me.messageTextMap) && !Ext.isEmpty(jobtitleportal_me.messageTextMap.text)) {
    		
			var text = jobtitleportal_me.messageTextMap.text;
			var num = jobtitleportal_me.messageTextMap.num;
			var url = jobtitleportal_me.messageTextMap.url;
			var nbasea0100 = jobtitleportal_me.messageTextMap.nbasea0100;
			var beforelabel = Ext.widget('label', {
				text:text.substring(0, text.indexOf('{num}')),
				style:'font-size:16px;'
			});
			var numlabel = Ext.widget('label', {
				text:num,
				margin:'0 5 0 5',
				style:'color:blue;cursor:pointer;font-size:16px;',
				listeners:{
					click:{
						element:'el',
						fn:function(){
							jobtitleportal_me.checkfile(url, nbasea0100);
						}
					}
				}
			});
			var afterlabel = Ext.widget('label', {
				text:text.substring(text.indexOf('{num}')+5),
				style:'font-size:16px;'
			});
			
			var marginLeft = (jobtitleportal_me.width/4/2)-(imgSize/2);
			
			var msgContainer = Ext.widget('container', {
				margin:'10 0 0 '+marginLeft,
				items:[beforelabel, numlabel, afterlabel]
			});
			jobtitleportal_me.add(msgContainer);
    	}
		
    	for(var i=0; i<infolist.length; i++){
    		var id = infolist[i].id;
    		var name = infolist[i].name;
    		var src = infolist[i].src;
    		
	    	var img = new Ext.Img({ // 图片
	    		id : 'img_'+id,
	    		src: src,
	    		style:'cursor:pointer;',
	    		width:imgSize,
	    		height:imgSize,
				listeners:{
			    	mouseover:{
			            element: 'el', 
			            fn: function(){
			            	jobtitleportal_me.currentSelected = this.id.substring(4);// 当操作的项目
			            	jobtitleportal_me.getDetailInfoList(this);
		            	}
			        },
					mouseout:{
			        	element: 'el', 
			        	fn: function(a, o){jobtitleportal_me.showOrHideDetail(false);}
			        }
			    }
	    	});
	    	
	    	
	    	var width = parseInt(this.width/4);
	    	var marginTop = (this.height/2)-15-(imgSize/2)-20;//15：文字的高度；20:偏上20个像素
	    	var panel = Ext.widget('panel',{
				id:'pan_'+id,
				style:'border-width:0px;',
				margin:marginTop+ ' 0 0 0',
				width:width,
				border:false,
				header:false,
				layout:{
					type:'vbox',
					align:'center'
				},
				items:[img, {xtype:'label',margin:'3 0 0 0',style:'line-height:20px;',text:name}]
			});
    		
    		mainContainer.add(panel);
    	}
    	this.add(mainContainer);
    },
    // 显示和隐藏详情：showOrHide为false的话obj和infolist可以不传。obj为当前选择节点，用来定位；infolist为详细信息。
    showOrHideDetail:function(showOrHide, obj, infolist){
    	
		/**
		 *  打开详情时：1、如果已经打开，则先无条件关闭
		 *  		 2、如果没有打开，则直接打开
		 *  关闭时：1、如果已经打开，则继续判断鼠标是否在详情的window上，如果在，则不关闭
		 *  	  2、没有打开，就直接return，停止。
    	*/
    	var detailWin = Ext.getCmp('detailWin'); // 是否已经打开详情
		if(detailWin){
	    	if(showOrHide){
    			detailWin.close();
    			jobtitleportal_me.currentPageClickState = '';
    			jobtitleportal_me.pageNum = 0;
    			jobtitleportal_me.currentPage = 1;
	    	} else {
	    		var task = new Ext.util.DelayedTask(function(){
	    			if(!jobtitleportal_me.currentMouseIsOverWindow){
			    		detailWin.close();
			    		jobtitleportal_me.currentPageClickState = '';
			    		jobtitleportal_me.pageNum = 0;
    					jobtitleportal_me.currentPage = 1;
	    			}
				});
				task.delay(300);
	    	}
		}
    	
    	if(!showOrHide){// 如果是关闭，则停止
    		return ;
    	}
    	var id = obj.id;
    	var X = obj.getX();
    	var Y = obj.getY();
    	var width = obj.getWidth();
    	
    	var leftImg = Ext.create('Ext.Img', {
			id:'leftImg',
			src: "/images/new_module/left1.png",
			style:'cursor:pointer;',
			width : 30,
			height:60,
			margin:'20 0 0 0',
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){jobtitleportal_me.leftPage();}
		        }
			}
		});
		
		var rightImg = Ext.create('Ext.Img', {
			id:'rightImg',
			src: "/images/new_module/right1.png",
			style:'cursor:pointer;',
			width : 30,
			height:60,
			margin:'20 0 0 0',
			listeners: {
		        click: {
		            element: 'el', 
		            fn: function(a, o){jobtitleportal_me.rightPage();}
		        }
			}
		});
    	
		var animateContainer = Ext.widget('container', {
			layout:'hbox',
			border:false
		});

		var detail = Ext.widget('panel',{
			id:'detailWin',
			layout:{
				type:'hbox'
			},
			x:Ext.getCmp('mainContainer').getX()+30,
			y:Y+width+25,
			height:110,
			border:false,
			floating:true,
			width:jobtitleportal_me.width-60,
			height:106,
			renderTo:Ext.getBody(),
			bodyStyle:'background-color:#f5f5f5;z-index:1;',
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
			items:[animateContainer],
			listeners:{
				mouseover:{
		        	element: 'el', 
		        	fn: function(){
		        		jobtitleportal_me.currentMouseIsOverWindow = true;
		        	
		        	}
		        },
				mouseout:{
		        	element: 'el', 
		        	fn: function(){
		        		jobtitleportal_me.currentMouseIsOverWindow = false; 
		        		jobtitleportal_me.showOrHideDetail(false);
		        	}
		        },
		        close:{
		        	fn:function(){
		        		var detailWinPoint = Ext.getCmp('detailWinPoint');
		        		if(detailWinPoint){
		        			detailWinPoint.destroy();
		        		}
		        	}
		        }
			}
		});
		var point = Ext.create('Ext.Img', {
			id:'detailWinPoint',
			src: "/images/new_module/jiantou.png",
			floating:true,
			width:24,
			height:14,
			x:X+(width/2)-12,
			y:Y+width+12,
			renderTo:Ext.getBody()
		});
		// 翻页效果
		jobtitleportal_me.animateAction(animateContainer, infolist);
	},
	// 获取详情栏数据
    getDetailInfoList:function(obj) {
    	var id = obj.id.substring(4);
    	var map = new HashMap();
    	map.put("id", id);
		Rpc({functionId:'ZC00005002',async:false,success:function(form,action){
			var infolist = Ext.decode(form.responseText).infolist;
			if(infolist.length > 0){
				if(infolist.length == 1){// 如果详情只有一个，则直接给当前图标绑定，不显示详情框
					obj.on('click',function(){
						var info = infolist[0];
						var numid = this.id.substring(4);
		            	var id = info.id;
		            	jobtitleportal_me.clickEvent(numid, id);
					});
					return ;
					
				}else {
	    			jobtitleportal_me.showOrHideDetail(true, action.obj, infolist);
				}
	    	} else {
	    		return ;
	    	}
			
		},scope:this,obj:obj},map);
    },
    // 添加详情栏
    addDetailImg:function(id, name, src){
    	var imgSize = 50;
		if(this.isLowPixel){//适配低分辨率
			imgSize = 33;
		}
    	var img = new Ext.Img({ // 图片
    		id : 'detailImg_'+id,
    		src: src,
    		title:name,
    		style:'cursor:pointer;',
    		width:imgSize,
    		height:imgSize,
			listeners:{
		    	click:{
		            element: 'el', 
		            fn: function(){
		            	var numid = jobtitleportal_me.currentSelected;
		            	var id = this.id.substring(10);
		            	jobtitleportal_me.clickEvent(numid, id);
					}
		        },
		       mouseover:{
		            element: 'el', 
		            fn: function(){
		            	Ext.getCmp(this.id).setSrc('/images/new_module/tubiao1.png');
	            	}
		        },
				mouseout:{
		        	element: 'el', 
		        	fn: function(a, o){
						Ext.getCmp(this.id).setSrc('/images/new_module/tubiao.png');
					}
		        }
		    }
    	});
    	
    	var showText = jobtitleportal_me.convertStr(name);
    	var panel = Ext.widget('panel',{
			id:'detailPan_'+id,
			border:false,
			header:false,
			margin:'8 0 0 0',
			width:imgSize+40,
			style:'border-width:0px;',
			bodyStyle:'background-color:#f5f5f5;',
			layout:{
				type:'vbox',
				align:'center'
			},
			items:[img,{
				xtype:'label',
				html:'<span title="'+name+'">'+showText+'</span>',
				maxWidth:imgSize+30,
				height:36,
				margin:'3 0 4 0',
				style:'word-break:break-all;'
			}]
		});
		
		return panel;
    },
    // 详情栏中图标翻页效果
    animateAction:function(animateContainer, infolist){
    	jobtitleportal_me.animateContainer = animateContainer;// 临时存储一下，翻页时需要用到该组件
    	jobtitleportal_me.infolist = infolist;// 临时存储一下，翻页时需要用详情信息
    	
    	animateContainer.removeAll();
    	var totalNum = infolist.length;//总个数
		
		var x = jobtitleportal_me.width-60;
		var imgSize = 50;
		if(jobtitleportal_me.isLowPixel){//适配低分辨率
			imgSize = 33;
		}
		var num = (x-30-30)/(imgSize+40);//每页个数
		if(num.toString().indexOf('.') > -1){
			num = num.toString().split('.')[0];
		}
		
		var pageNum = totalNum/num;//页数
		if(pageNum.toString().indexOf('.') > -1){
			pageNum = parseInt(pageNum.toString().split('.')[0])+1;
		}
		if(pageNum == 0){//如果算出页数为0，则设置成1
			pageNum = 1;
		}
		jobtitleportal_me.pageNum = pageNum;
		var startNum = num * (jobtitleportal_me.currentPage - 1);
		var endNum = num * jobtitleportal_me.currentPage - 1;
		
		for(var i=0; i<infolist.length; i++) {
			if(i >= startNum && i <= endNum){
	    		var info = infolist[i];
	    		var id = info.id;
	    		var name = info.name;
				var src = info.src;
				animateContainer.add(jobtitleportal_me.addDetailImg(id, name, src));
			}
		}
		
		// 左右翻页的显示与隐藏
		var leftImg = Ext.getCmp('leftImg');
		var rightImg = Ext.getCmp('rightImg');
		if(jobtitleportal_me.pageNum == 1){//【总页数】==1
			leftImg.setVisible(false);
			rightImg.setVisible(false);
		}else{
			if(jobtitleportal_me.currentPage == 1){//【当前页码】== 1
				leftImg.setVisible(false);
				rightImg.setVisible(true);
				
			}else if(jobtitleportal_me.pageNum == jobtitleportal_me.currentPage){//【当前页码】==【总页数】
				leftImg.setVisible(true);
				rightImg.setVisible(false);
			
			}else{
				leftImg.setVisible(true);
				rightImg.setVisible(true);
				
			}
		}
		if(jobtitleportal_me.currentPageClickState == '1'){
			animateContainer.setPosition(-animateContainer.getWidth(), 0, false);
			animateContainer.setPosition(0, 0, true);
		} else if(jobtitleportal_me.currentPageClickState == '2'){
			animateContainer.setPosition(2*animateContainer.getWidth(), 0, false);
			animateContainer.setPosition(0, 0, true);
		}
    },
	// 左翻页
	leftPage:function(){
		if(jobtitleportal_me.currentPage == 1){//当前页码等于1，左翻页 禁用
			return ;
		}
		
		jobtitleportal_me.currentPage = jobtitleportal_me.currentPage - 1;
		jobtitleportal_me.currentPageClickState = '1';//更新点击状态 左翻页
		jobtitleportal_me.animateAction(jobtitleportal_me.animateContainer, jobtitleportal_me.infolist);
	},
	// 右翻页
	rightPage:function(){
		if(jobtitleportal_me.currentPage == jobtitleportal_me.pageNum){//当前页码等于总页码，右翻页 禁用
			return ;
		}
		
		jobtitleportal_me.currentPage = jobtitleportal_me.currentPage + 1;
		jobtitleportal_me.currentPageClickState = '2';//更新点击状态 右翻页
		jobtitleportal_me.animateAction(jobtitleportal_me.animateContainer, jobtitleportal_me.infolist);
	},
	// 详情栏点击事件
	clickEvent:function(numid, id){
		if(numid == 'num1'){
			url = '/module/qualifications/Qualifications.html?b_query=link&id='+id;
		} else {
			url = '/module/template/templatemain/templatemain.html?b_query=link&sys_type=1&return_flag=11&approve_flag=1&module_id=9&card_view_type=1&view_type=card&task_id=0&tab_id='+id;
		}
		window.location.href = url;
	},
	// 把字符串转化成后面带省略号形式
	convertStr : function(str){
		var reStr = str;
		
		var maxwidth = 24;//字母排列的话最多占的个数
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
    // 申报模板的链接。评审材料、送审材料
	checkfile:function(path, nbasea0100){
		
		/** 解析path中的参数 */
		var tabid = "";
		var taskid = "";
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
				tabid = param.split('=')[1];//模板号
			} else if(key == 'taskid'){
				taskid = param.split('=')[1];//任务号 除0以外需加密
			}
		}
		
		/** 配置参数 说明
		var obj={};
		module_id="11";////调用模块标记：职称模块
		return_flag="14";//返回模块标记：不需要返回关闭按钮
		approve_flag="0";//不启用审批
		view_type="card";//卡片模式
		card_view_type="1";//卡片模式下不要显示左边导航树
		other_param="visible_title=0`visible_toolbar=0`object_id="+nbasea0100;//visible_title=0:不需要标题； visible_toolbar=0：不要按钮；object_id：对象nbase+a0100
		callBack_init="JobTitleRevewFile.showView";
		*/
		
		var other_value ="visible_title=1`visible_toolbar=1`object_id="+nbasea0100;
		other_value=getEncodeStr(other_value);
		var url = '/module/template/templatemain/templatemain.html?b_query=link&return_flag=11&approve_flag=0&module_id=11&card_view_type=1&task_id='+taskid+'&tab_id='+tabid+'&other_param='+other_value;
		window.location.href = url;
	}
});