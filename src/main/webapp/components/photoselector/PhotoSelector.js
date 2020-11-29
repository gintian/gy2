/**
 * wangb 20180525
 * 创建示例：
 * {
 *		xtype:'photoselector',
 *		width:'100%',
 *		maxPhone:3,
 *		fileType:'P',  p 照片   F 附件  
 *		photoSize:5,
 *      filesort:filesort,
 *      photoBtn:true  默认显示上传相机按钮
 *		direction:'horizontal', //跟layout 布局同步 hbox 值为horizontal，  vbox 值为vertical
 *		layout:'hbox'
 * }
 * 移动端上传附件组件 附件格式 image图片 支持显示 doc附件
 * 参数 photoBtn:true,//默认显示上传按钮
 * 参数 maxPhone 默认上传1张图片，最多允许上传4张图片，通过传递参数maxPhone控制图片上传数量
 * 参数 photos 格式等同this.fileList ， 用来显示图片，最大显示4张图片
 * 参数 direction 用来控制图片显示方向，默认水平布局，跟layout布局同步  vertical 垂直显示 horizontal 水平显示
 * 参数 photoSize 用来控制上传图片大小 默认不控制  单位MB
 * 参数 filesort 用来控制主集|子集附件 文件上传分类   主集 个人|公共    
 * 返回数据格式 Array
 * this.fileList =[{
 *   imgId:imgId, //图片唯一标识 同时也是上传图片的名称
 *   name:filename, //图片的真实名称
 *   filetype:filetype,//图片类型 png jpg gif
 *   filesort:filesort,//上传文件分类
 *   fileData:imgFile,//图片数据  base64格式 只用来显示
 *   file:file, // input file对象 上传文件对象
 *   url:newValue, // 上传图片的本地路径
 *   i9999:'-1', //新增附件
 *   state:'D' //删除标识
 * },{...}]
 */
Ext.define("EHR.photoselector.PhotoSelector",{
	requires:['EHR.photoselector.SelCarousel'],
	extend:'Ext.Panel',
	xtype:'photoselector',
	config:{
//		width:'100%',
		photoBtn:true,//默认显示上传按钮
		maxPhone:9,//默认上传1张图片，最多允许上传4张图片
		photos:undefined,//显示图片
		direction:'horizontal',//图片显示方向   vertical 垂直显示               horizontal 水平显示
		fileType:'P',
		photoSize:99,
		filesort:'',
		layout:{
			type:'hbox'
		},
		items:[{
			xtype:'button',
			itemId:'uploadPanel',
			width:120,
			height:52,
			border:0,
			html:'<div style="background:url(/components/photoselector/images/uploadfile.png) no-repeat; background-size:40px 40px;width:40;height:40px;font-size:14px;color:grey;padding-top:8px;padding-left:44px;">上传附件</div>'
		}]
	},
	initialize:function(){
		
		this.callParent();
		this.fileList = [];//上传图片数据
		if(!(this.getPhotoBtn())){
			this.child('button').hide();
		}
		this.child('button').on('tap',this.uploadIcons,this);
		if(this.config.fileType == 'P')
			this.child('button').setHtml('<div style="background:url(/components/photoselector/images/camera.png) no-repeat; background-size:40px 40px;width:40;height:40px;font-size:14px;color:grey;padding-top:8px;padding-left:44px;">上传图片</div>');
		this.setFileBtn();
		if(this.config.photos){
			this.fileList = this.config.photos;
			for(var i = 0 ; i < this.fileList.length ; i++){
				if( i >= this.config.maxPhone)
					continue;
				if(this.config.direction == 'horizontal')
					this.insert(this.getItems().length-2,this.getHorizontalPictures(this.fileList[i]));
				else
					this.insert(this.getItems().length-2,this.getVerticalPictures(this.fileList[i]));

			}
			if(this.fileList && this.config.maxPhone <= this.fileList.length ){
				this.child('button').hide();
				//销毁上传附件组件
				this.uploadfile.destroy();
				this.uploadfile=undefined;
				return;
			}
		}
	},
	/**添加 页面上传图片按钮*/
	setFileBtn:function(){
		var me = this;
		//if(me.config.maxPhone > 4)
			//me.config.maxPhone = 4;
		me.setFileComponent();
	},
	/**添加上传附件  页面显示上传图片 图片上传数据保存到 fileList 中*/
	setFileComponent:function(){
		var me = this;
		me.uploadfile = Ext.create('Ext.field.File',{
			hidden:true,
			accept: me.config.fileType=='P'? 'image':'',// 不限制上传文件类型
			listeners:{
				change:function(t,newValue,oldValue,e){
					//获取img 所有数据
					var file = me.uploadfile.element.dom.getElementsByTagName('input')[0].files[0];
					//获取文件名称
					var filename = newValue.substring(newValue.lastIndexOf('\\')+1,newValue.lastIndexOf('.'));
					//获取图片格式 png jpg gif
					var filetype = newValue.substring(newValue.lastIndexOf('.')+1);
					var imgType = '';
					var tip = "文件";
					if(me.config.fileType == 'P'){
						imgType = ",png,jpg,jpeg,bmp,";
						tip = "照片";
					}else{
						imgType = ",png,jpg,jpeg,bmp,doc,docx,xlsx,xls,pdf,ppt,pptx,txt,zip,rar,";
					}
					if(!file){
						//销毁上传附件组件
						me.uploadfile.destroy();
						me.uploadfile=undefined;
						return;
					}
					Ext.Msg.defaultAllowedConfig.showAnimation = false;
					Ext.Msg.defaultAllowedConfig.hideAnimation = false;
					if(!file.size){
						Ext.Msg.alert('提示',tip+'内容为空！');
						//销毁上传附件组件
						me.uploadfile.destroy();
						me.uploadfile=undefined;
						return;
					}	
						
					if(imgType.indexOf(','+filetype.toLowerCase()+',') ==-1){
						Ext.Msg.alert('提示',"不支持该"+tip+"类型，请重新上传");
						//销毁上传附件组件
						me.uploadfile.destroy();
						me.uploadfile=undefined;
						return;
					}
					/*
					if(filename.indexOf('.')>-1){
						Ext.Msg.alert('提示',tip+'名不允许带.字符！');
						//销毁上传附件组件
						me.uploadfile.destroy();
						me.uploadfile=undefined;
						return;
					}
					*/
					if(file.size/1024.0/1024.0 > me.config.photoSize){
						Ext.Msg.alert('提示',tip+'大小不允许超过'+me.config.photoSize+'MB!');
						//销毁上传附件组件
						me.uploadfile.destroy();
						me.uploadfile=undefined;
						return;
					}
					//创建读取文件的对象
					var reader = new FileReader();
					//正式读取文件
					reader.readAsDataURL(file);
					//为文件读取成功设置事件
					reader.onload=function(e) {
						imgFile = e.target.result;
						var timestamp=new Date().getTime();
						for(var i=0 ; i<me.fileList.length ; i++){
							if(me.fileList[i].fileData == imgFile /*me.fileList[i].url == newValue*/ ){
								Ext.Msg.alert('提示',tip+'已存在！');
								//销毁上传附件组件
								me.uploadfile.destroy();
								me.uploadfile=undefined;
								return;
							}
							if(me.fileList[i].name == filename)
								filename = filename +timestamp;
						}
						var imgId = "img"+timestamp;
						var imgInfo ={imgId:imgId,name:filename+'.'+filetype,filetype:filetype,fileData:imgFile,url:newValue,i9999:'-1',file:file,filesort:me.config.filesort};
						me.fileList.push(imgInfo);
						if(me.config.direction == 'horizontal')
							me.insert(me.getItems().length-2,me.getHorizontalPictures(imgInfo));
						else
							me.insert(me.getItems().length-2,me.getVerticalPictures(imgInfo));
						if(me.config.maxPhone <= me.fileList.length){
							me.child('button').hide();
						}
						//销毁上传附件组件
						me.uploadfile.destroy();
						me.uploadfile=undefined;
					};
				}
			}
		});
		this.add(me.uploadfile);
	},
	/**点击页面显示按钮  调用上传组件*/
	uploadIcons:function(){
		if(!this.uploadfile)
			this.setFileComponent();
		this.uploadfile.element.dom.getElementsByTagName('input')[0].click();
	},
	/**
	 * 获取上传图片和删除图片  水平显示
	 * imgInfo 图片数据信息集合
	 *
	 */
	getHorizontalPictures:function(imgInfo){
		var me = this;
		imgInfo.fileData = imgInfo.fileData ==undefined ? imgInfo.url:imgInfo.fileData;
		var image = Ext.create('PhotoSelector.Carousel',{
			indicator: false,
			width:40,
			height:40,
			imgId:imgInfo.imgId,
			url:imgInfo.url,
			style:'margin:10px 0px 10px 10px;',
			items:[{
				xtype:'button',
				width:40,
				height:40,
				border:0,
				style:'border-radius:0px;',//上传图片取消边框圆角
				padding:0,
				imgName:imgInfo.name,
				imgData:imgInfo.fileData,
				align:'left',
				html:'<img src="'+imgInfo.fileData+'" width="40px" height="40px"/>',
				listeners:{
					tap:me.previewIcon,scope:me
				}
			},{
				xtype:'image',
				width:40,
				height:40,
				src:'/components/photoselector/images/delIcon.png',
				listeners:{
					tap:me.delIcon,scope:me
				}
			}],
			listeners:{
				activeitemchange:me.toggleIcon,scope:me
			}
		});
		return image;
	},
	/**
	 * 获取上传图片和删除图片  垂直显示 ,支持显示非图片附件 例如：doc格式附件  仅垂直方向支持
	 * imgInfo 附件数据信息集合
	 */
	getVerticalPictures:function(imgInfo){
		var me = this;
		imgInfo.fileData = imgInfo.fileData ==undefined ? imgInfo.url:imgInfo.fileData;
		var imgPanel;
		var imgType = ",png,jpg,jpeg,bmp,";
		if(!imgInfo.filetype || imgInfo.filetype == undefined ){
			imgInfo.filetype='';
		}
		if(imgType.indexOf(','+imgInfo.filetype.toLowerCase()+',') != -1){ //图片类型
			imgPanel = Ext.create('Ext.Panel',{
				layout:{
					type:'hbox',
					align:'center'
				},
				imgId:imgInfo.imgId,
				url:imgInfo.url,
//				style:'margin-top:10px;',
				style:'margin-top:6px;margin-bottom:6px',
				items:[{
					xtype:'button',
					width:40,
					height:40,
					border:0,
					style:'border-radius:0px;margin-left:10px;',//上传图片取消边框圆角
					padding:0,
					imgName:imgInfo.name,
					imgData:imgInfo.fileData,
					align:'left',
					html:'<img src="'+imgInfo.fileData+'" width="40px" height="40px"/>',
					listeners:{
						tap:me.previewIcon,scope:me
					}
				},{
					xtype:'image',
					width:20,
					height:20,
					right:'4%',
					hidden:!this.getPhotoBtn(),
					style:'margin-top:10px;',
					src:'/components/photoselector/images/close.png',
					listeners:{
						tap:me.delIcon,scope:me
					}
				}]
			});
		}else{//非图片类型
			imgPanel = Ext.create('Ext.Panel',{
				layout:{
					type:'hbox',
					align:'center'
				},
				imgId:imgInfo.imgId,
				url:imgInfo.url,
				style:'margin-top:10px;margin-bottom:10px;',
				items:[{
					xtype:'component',
					padding:0,
					align:'left',
					html:'<a style="text-decoration:none;font-size:16px;color:#0099ff;" href="#">'+imgInfo.name+'</a>'
				},{
					xtype:'image',
					width:20,
					height:20,
                    hidden:!this.getPhotoBtn(),
					right:'4%',
					src:'/components/photoselector/images/close.png',
					listeners:{
						tap:me.delIcon,scope:me
					}
				}]
			});
		}
		return imgPanel;
	},
	/**左右滑动图片  事件*/
	toggleIcon:function(t){
		if(t.getActiveIndex() == 0)
			return;
		var me = this;
		var imgId = t.config.imgId;
		var index =-1;
		for(var i = 0 ; i < me.getItems().getCount()-1 ; i++){
			var imageId = me.getItems().items[i].config.imgId;
			if(imageId && imgId == imageId)
				continue;
			if(me.getItems().items[i].getActiveIndex() == 0)
				continue;
			index = i;
		}
		if(index != -1)
			me.getItems().items[index].previous();
	},
	/**删除上传图片 事件*/
	delIcon:function(t){
		var me = this;
		var imgId = t.getParent().config.imgId;
		for(var i=0 ; i < me.fileList.length ; i++){
			if(me.fileList[i].imgId != imgId)
				continue;
			if(me.fileList[i].i9999 == '-1'){
				me.fileList.splice(i,1);
            }
			else{
				me.fileList[i].state='D';
            }
		}
		t.getParent().destroy();
		if(me.config.maxPhone > me.fileList.length)
			me.child('button').show();
	},
	/**预览图片 点击事件*/
	previewIcon:function(t){
		var me = this;
		var priviewPanel = Ext.create('Ext.Panel',{
			id:'previewIcon',
			top:0,
			left:0,
			width:'100%',
			height:'100%',
			showAnimation:{
				type:'slide',
				direction:'up',
				duration:250
			},
			hideAnimation:{
				type:'slideOut',
				direction:'down',
				duration:250
			},
			layout:'fit',
			items:[{
				xtype:'titlebar',
				docked:'top',
				title: t.config.imgName,
				style:'border-bottom:1px solid #c5c5c5;',
				items:[{
					text:'关闭',
					handler:function(){
						Ext.getCmp('previewIcon').destroy();
					}
				}]
			},{
				xtype:'image',
				style:'text-align:center',
				src:t.config.imgData
			}]
		});
		Ext.Viewport.add(priviewPanel);
	}
});