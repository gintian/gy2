/**
 * 评审条件，首页跳转至该页面，只显示内容和附件
 * @createtime Nov 15, 2015
 * @author chent
 * 
 * */
 Ext.define('QualificationsURL.QualificationsForHome',{
 	requires : ['SYSF.FileUpLoad'],//加载上传控件js
 	height:500,//页面高度
 	width:800,//页面宽度
 	//构造器
 	constructor:function(config) {
 		qualifications_me = this;
 		var flag = true;
 		//屏蔽浏览器右键菜单
		Ext.getDoc().on("contextmenu", function(e){
			e.stopEvent();
		});
		
		// 窗口resize重新定位
		Ext.EventManager.onWindowResize(function(w,h){ 
			this.initPosition(); 
		},this,true); 
 		
		// 获取评审条件编号
		var conditionId = '';
		if(config.url.indexOf('id') > -1){
			conditionId = config.url.split('?')[1].split('&')[1].split('=')[1];
		}else{
			conditionId = config.url.split('?')[1].split('&')[1].split('=')[1];
			flag = false;
		}
		// 48487 获取宽高时完善校验
		var height = 500;
		var width = 800;
		// 优先通过ext获取
		if(Ext){
			//谷歌下可能对象没创建，高度是没有的
			height = Ext.getBody().getHeight() == 0?window.parent.window.document.getElementById('center_iframe').offsetHeight:Ext.getBody().getHeight();
			width = Ext.getBody().getWidth() == 0?window.parent.window.document.getElementById('center_iframe').offsetWidth:Ext.getBody().getWidth();
		}// 其次通过模板中center_iframe获取
		else if(window.parent.window.document.getElementById('center_iframe')){
			height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
			width = window.parent.window.document.getElementById('center_iframe').offsetWidth;
		}
		// 都没有则走默认值
		this.height = height;
		this.width = width;
		qualifications_me.flag = flag;
		
		// 获取评审条件信息
		var map = new HashMap();
		map.put("conditionid", conditionId);
	    Rpc({functionId:'ZJ100000207',success:this.setHTMLValue,scope:this},map);
	},
	
	//加载页面的值
	setHTMLValue:function(form){
		var conditioninfo = Ext.decode(form.responseText).conditioninfo;
		
		var zc_series = conditioninfo.zc_series;// 评审条件名称
		var description = conditioninfo.description;// 文本内容
		var attachmentlist = conditioninfo.attachmentlist;// 附件列表
 		
		// 页面展现
		this.createMainPage(zc_series, description, attachmentlist);
	},
	// 页面展现
	createMainPage:function(zc_series, description, attachmentlist){
		
		var viewPort = Ext.getCmp('qualification_viewport');
 		if(viewPort){
			Ext.destroy(viewPort);
		}
		
		// 创建评审条件区域
		var content = this.createContentPanel(zc_series, description);
		// 创建附件区域
		var attachment = undefined;
		if(attachmentlist.length > 0){
			attachment = this.createAttachmentPanel(attachmentlist);
		}
		
		var container = Ext.widget('container', {
			border:false,
			layout:{
				type:'vbox',
				align:'left'
			},
			items:[content, attachment]
			
		});
		
		Ext.widget('viewport', {
			id:'qualification_viewport',
    		border:false,
    		layout: {
				type: 'fit'
    		},
    		renderTo:Ext.getBody(),
    		items: [container]
		});
	},
	// 创建内容区域
	createContentPanel:function(zc_series, description){
		return Ext.widget("panel",{
			title:zc_series,
			id:'zc_series',
			width:this.width,
			height:this.height*0.8,
			border:false,
			scrollable:true,
			html:'<table align="center" style="width:97%;"><tr><td>'+description+'</td></tr></table>',
			tools: [{
				xtype:'tool',
				type:'close',
				handler:function(){
					if(qualifications_me.flag == true){
						window.history.go(-1);
					}else{
						var viewPort = Ext.getCmp('qualification_viewport');
				 		if(viewPort){
							Ext.destroy(viewPort);
						};
					}
					
				}
			}]
		});	
	},
	// 创建附件区域
	createAttachmentPanel:function(attachmentlist){
		
		var panel = Ext.widget('panel',{
    		title: '附件',
    		id:'attachment',
    		width:this.width,
    		height:this.height*0.2,
    		border:false,
    		layout:'hbox',
    		height:130,
    		scrollable:'x',
    		items:[]        		
		});
		
		for(var i=0; i<attachmentlist.length; i++){
			var attachmentName = attachmentlist[i].name;// 附件名称
			var showAttachmentName = this.convertAtStr(attachmentName);
			var src = attachmentlist[i].src;// 地址
			//20/3/12 xus vfs改造
			var fileid = attachmentlist[i].fileid;
//			var encryptname = attachmentlist[i].encryptname;//下载名称
//			var path = attachmentlist[i].path;//下载路径
			
			var container = Ext.widget('container', {
				height:'100%',
				width:80,
				border:false,
				style : 'cursor:pointer;',
				layout:{
					type:'vbox',
					align:'center'
				},
				items:[{
					xtype:'image',
					id:'img_'+i,
					title:attachmentName,
					src:src,
					width:45,
					height:45,
					margin:'10 0 0 0',
					border:false,
					listeners : {
				        click: {
				        	element: 'el', 
				        	fn: function(){
//				        		var id = this.id.substring(4);
//				        		var encryptname = Ext.getCmp('hiddenname_'+id).getValue();
//				        		var path = Ext.getCmp('hiddenpath_'+id).getValue();
//	                 			window.location.href = "/servlet/DisplayOleContent?bencrypt=true&filename="+encryptname+"&filePath="+path;
				        		window.location.href = "/servlet/vfsservlet?fileid="+fileid;
				        	}
				        }
					}
				},{
					xtype: 'label',
					maxWidth:80,
					text: showAttachmentName
				}
				/*,{//隐藏值，保存附件名
			        xtype: 'hiddenfield',
			        id:'hiddenname_'+i,
			        value: encryptname
			    },{//隐藏值，保存附件路径
			        xtype: 'hiddenfield',
			        id:'hiddenpath_'+i,
			        value: path
			    }*/
			    ]
			});
			
		    panel.add(container);
		}
		return panel;
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
    // 重新定位
    initPosition:function(){
		var height = window.parent.window.document.getElementById('center_iframe').offsetHeight;
		var width = window.parent.window.document.getElementById('center_iframe').offsetWidth;

		var zc_series =  Ext.getCmp('zc_series');
    	var attachment =  Ext.getCmp('attachment');
    	if(zc_series){
    		zc_series.setHeight(height*0.8);
    		zc_series.setWidth(width);
    	}
    	if(attachment){
    		attachment.setHeight(height*0.2);
    		attachment.setWidth(width);
    	}
    }
 });