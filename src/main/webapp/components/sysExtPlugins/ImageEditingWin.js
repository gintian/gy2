/**
 * 照片显示编辑上传控件
 * hej add 2015/12/3
 */
Ext.define("SYSP.ImageEditingWin",{
	extend: 'Ext.window.Window',
	requires:["SYSF.FileUpLoad"],
	xtype:'imageediting',
	id:'imagewin',
	resizable:false,
	bodyStyle : 'background-color:#F5F5F5',
	//图片高度
	imageheight:120,
	buttonimgheight:13,
	buttonimgwidth:13,
	modal:true,
	//图片宽度
	imagewidth:85,
    //默认1为通过路径查找图片，2通过人员库前缀和人员编号
    flag:'1',
    //是否有上传功能  默认1有 2 没有
    isupload:'1',
    //系统设置图片保存根目录
    fileRootPath:'',
    //vfs文件唯一标识
    fileid:'',
    //文件名称
    filename:'',
    //文件路径
    filePath:'',
    //文件上传路径
    uploadPath:'',
    //唯一标识
    perguid:'',
    //是否加密
    bencrypt:'',
    //是否是移动端
    mobile:'',
    //人员库前缀（加密）
    nbase:'',
    //人员库编号（加密）
    a0100:'',
    //专家库编号
    w0101:'',
    //图片质量（"h"：原图  "l"：低分辨率图片 加密）
    quality:'',
    //上传图需要修改成的图片名
    imagename:'',
    //窗口状态
    imageopenflag:true,
    //回掉函数
    callback:Ext.emptyFn,
    //创建标识
    createflag:false,
    //记录上次打开窗口大小
    record:[],
    pubRealPath:'',
    title:'照片',
    //上传照片限制大小
    fileSizeLimit:'',
	onRender : function() {
		this.callParent();
		this.createImageWin();
	},
	createImageWin:function(){
		var me= this;
		if(me.record.length>0){
			me.imageheight=me.record[0];
			me.imagewidth=me.record[1];
			me.buttonimgheight = me.record[2];
			me.buttonimgwidth = me.record[3];
		}
		me.minHeights=120;
		me.minWidths=85;
		me.maxHeights=300;
		me.maxWidths=200;
	    me.createflag=true;//创建过
		me.imageopenflag = true;
		me.add(me.createimage(me.imageheight,me.imagewidth));
		me.addDocked(me.createbbar());
		me.on("beforeclose",function(){
			me.imageopenflag = false;
			me.record.splice(0,me.record.length);
			me.record.push(me.imageheight);
			me.record.push(me.imagewidth);
			me.record.push(me.buttonimgheight);
			me.record.push(me.buttonimgwidth);
			Ext.callback(me.callback)
		});
	},
	/**
	 * 底部工具栏
	 * @return {}
	 */
	createbbar:function(){
		var me = this;
		me.bbar=Ext.widget('toolbar',{dock:'bottom',border:0,items:me.createtoolitem()});
		return me.bbar;
	},
	/**
	 * 创建工具items
	 * @return {}
	 */
	createtoolitem:function(){
		var me = this;
		var items = undefined;
		if(me.isupload=='1'){//有上传功能
			items = [{xtype:'image',src:'/components/sysExtPlugins/images/reduce.png',title:'缩小',flag:'reduce',
		                      height:me.buttonimgheight,width:me.buttonimgwidth,style : {cursor : 'pointer'},listeners : {
											render : function() {
												this.getEl().on('click', function() {
												     var largeimage = Ext.ComponentQuery.query("image[flag='enlarge']")[0];
												     var imagepanel = this.ownerCt.ownerCt.items.get(0);
												     var win = this.ownerCt.ownerCt;
												     if(imagepanel.width>me.minWidths){
												     	me.zoomImage(imagepanel, 0.8, true);
												     	me.zoomImage(this, 0.8, true,1);
												     	me.zoomImage(largeimage, 0.8, true,1);
												     }
													 if(imagepanel.width==me.minWidths){
													 	this.setDisabled(true);
													 }
													 if(imagepanel.width<me.maxWidths){
													 	largeimage.setDisabled(false);
													 }
												}, this);
											}
										}},{ xtype: 'tbfill' },
		                    {xtype:'label',text:'上传照片',style : {cursor : 'pointer',color:'blue',align:'center'},listeners : {
								 render:function(){
								 	this.getEl().on("click", function() {
								 		 var imagewin = Ext.getCmp("imagewin");
								 	     me.uploadImage(imagewin.items.get(0));
								 	});
								 }}
						},{ xtype: 'tbfill' },{xtype:'image',title:'放大',src:'/components/sysExtPlugins/images/enlarge.png',
							height:me.buttonimgheight,width:me.buttonimgwidth,flag:'enlarge',
								style : {cursor : 'pointer'},listeners : {
											render : function() {
												this.getEl().on('click', function() {
													var reduceimage = Ext.ComponentQuery.query("image[flag='reduce']")[0];
													var imagepanel = this.ownerCt.ownerCt.items.get(0);
													var win = this.ownerCt.ownerCt;
													if(imagepanel.width<me.maxWidths){
												     	me.zoomImage(this, 0.8, false,1);
												     	me.zoomImage(reduceimage, 0.8, false,1);
												     	me.zoomImage(imagepanel, 0.8, false);
												    }
													if(imagepanel.width==me.maxWidths){
														this.setDisabled(true);
													}
													if(imagepanel.width>me.minWidths){
														reduceimage.setDisabled(false);
													}
												}, this);
											}
										}}];
		}else if(me.isupload=='2'){
			items=[{xtype:'image',src:'/components/sysExtPlugins/images/reduce.png',title:'缩小',flag:'reduce',
	                      height:me.buttonimgheight,width:me.buttonimgwidth,style : {cursor : 'pointer'},listeners : {
										render : function() {
											this.getEl().on('click', function() {
												var largeimage = Ext.ComponentQuery.query("image[flag='enlarge']")[0];
												     var imagepanel = this.ownerCt.ownerCt.items.get(0);
													 var win = this.ownerCt.ownerCt;
													 if(imagepanel.width>me.minWidths){
												     	me.zoomImage(imagepanel, 0.8, true);
												     	me.zoomImage(this, 0.8, true,1);
												     	me.zoomImage(largeimage, 0.8, true,1);
												     }
													 if(imagepanel.width==me.minWidths){
													 	this.setDisabled(true);
													 }
													 if(imagepanel.width<me.maxWidths){
													 	largeimage.setDisabled(false);
													 }
											}, this);
										}
									}},{ xtype: 'tbfill' },
	                 {xtype:'image',title:'放大',src:'/components/sysExtPlugins/images/enlarge.png',flag:'enlarge',
	                       height:me.buttonimgheight,width:me.buttonimgwidth,style : {cursor : 'pointer'},listeners : {
										render : function() {
											this.getEl().on('click', function() {
												var reduceimage = Ext.ComponentQuery.query("image[flag='reduce']")[0];
												var imagepanel = this.ownerCt.ownerCt.items.get(0);
												var win = this.ownerCt.ownerCt;
												if(imagepanel.width<me.maxWidths){
												     	me.zoomImage(this, 0.8, false,1);
												     	me.zoomImage(reduceimage, 0.8, false,1);
												     	me.zoomImage(imagepanel, 0.8, false);
												    }
												if(imagepanel.width==me.maxWidths){
													this.setDisabled(true);
												}
												if(imagepanel.width>me.minWidths){
													reduceimage.setDisabled(false);
												}
											}, this);
										}
									}}
					]
		}
		return items;
	
	},
	/**
	 * 创建图片
	 * @return {}
	 */
	createimage:function(height,width){
	var me = this;
	var image=undefined;
	//20/3/11 xus vfs改造
	var setsrc = "/images/photo.jpg";
	if(me.fileid && me.fileid != '' ){
		setsrc = "/servlet/vfsservlet?fileid="+me.fileid;
	}
	image ={xtype:'image',height:height,width:width,minHeight:me.minHeights,minWidth:me.minWidths,maxWidth:me.maxWidths,maxHeight:me.maxHeights
			,src:setsrc};
//	if(me.flag=='1'){//通过路径
//		var setsrc = "/servlet/DisplayOleContent?time="+new Date().getTime()+"&filePath=";
//		if(me.pubRealPath!=''){
//		    setsrc+= me.pubRealPath;
//		}
//		if(me.mobile!=''){
//			setsrc+= "&mobile="+me.mobile;
//		}
//		if(me.filename!=''){
//			setsrc+= "&filename="+me.filename;
//		}
//		if(me.perguid!=''){
//			setsrc+= "&perguid="+me.perguid;
//		}
//		if(me.bencrypt!=''){
//			setsrc+= "&bencrypt="+me.bencrypt;
//		}
//		setsrc+="&caseNullImg=/images/photo.jpg";
//		image ={xtype:'image',height:height,minHeight:me.minHeights,minWidth:me.minWidths,maxWidth:me.maxWidths,maxHeight:me.maxHeights,
//	    				          width:width,src:setsrc};
//	}
//	if(me.flag=='2'){
//		var setsrc = "/servlet/DisplayOleContent?nbase=";
//		if(me.nbase!=''){
//			setsrc+= me.nbase;
//		}
//		if(me.a0100!=''){
//			setsrc+= "&a0100="+me.a0100;
//		}
//		if(me.quality!=''){
//			setsrc+= "&quality="+me.quality;
//		}
//		setsrc+="&caseNullImg=/images/photo.jpg";
//		image ={xtype:'image',height:height,width:width,minHeight:me.minHeights,minWidth:me.minWidths,maxWidth:me.maxWidths,maxHeight:me.maxHeights
//	    				,src:setsrc};
//	}
	
	return image;
	},
	/**
	 * 上传照片
	 */
	uploadImage:function(obj){
		var me= this;
		var savePath = '';
		if(me.filePath!=''){
//			var index = me.filePath.lastIndexOf("\/");
//			me.filePath = me.filePath.substring(0,index+1);
			savePath = me.filePath;
		}
		if(me.uploadPath!=''){
			savePath = me.uploadPath;
		}
		var uploadObj= Ext.create("SYSF.FileUpLoad",{
			renderTo:Ext.getBody(),
			emptyText:"请选择文件",
			upLoadType:1,
			height: 30,
			fileExt:"*.jpg;*.jpeg;*.png;*.bmp",
			savePath:savePath,
			//是否为临时文件 true是，false不是
		    isTempFile:false,
		    //关联VfsFiletypeEnum 文件类型 例：VfsFiletypeEnum.doc
		    VfsFiletype:VfsFiletypeEnum.multimedia,
		    //关联VfsModulesEnum 模块id 例：VfsModulesEnum.CARD
		    VfsModules:VfsModulesEnum.ZC,
		    //关联VfsCategoryEnum 文件所属类型 例：VfsCategoryEnum.personnel
		    VfsCategory: VfsCategoryEnum.other,
		    //所属类型guidkey
		    CategoryGuidKey: '',
		    filetag:'',
			success:function(list){
				if(list.length!=0){
					//20/3/11 xus vfs改造                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
				    var valuestr='';
					var filename = list[0].filename;  //编码后文件名
					var fileid = list[0].fileid
//					var id = list[0].id;              //文件唯一标识      
//					var localname=list[0].localname;  //原始文件名 
//					var path = list[0].path;          //文件上传路径
//					var successed=list[0].successed;  //是否成功标识
                	var vo = new HashMap();
//					vo.put("imgname",me.imagename);
//					vo.put("filename",filename);
					vo.put("fileid",fileid);
					vo.put("w0101",me.w0101);
					//haosl 如果选择文件后没有成功，则不走后台上传逻辑  20170616
//					if(!successed){
//						win.close();
//						return;
//					}
					var setsrc = "/images/photo.jpg";
					if(fileid && fileid != ''){
						setsrc = "/servlet/vfsservlet?fileid="+fileid;
					}
					obj.setSrc(setsrc);
					//保存到w01表
					Rpc({functionId:'101001004833331',scope:this,success:function(res){
//						var resultObj = Ext.decode(res.responseText);
//						var imagename = resultObj.imagename;
//						var filepath = resultObj.pubFilepath;
//						obj.setSrc("/servlet/DisplayOleContent?time="+new Date().getTime()+"&filePath="+filepath+"&mobile=1&bencrypt=true&filename="+imagename+"&caseNullImg=/images/photo.jpg");
					}},vo);
					win.close();
				}
			},
			//回调方法，失败
 			error:function(){
  				Ext.MessageBox.show({  
					title : '文件上传',  
					msg : "文件上传失败 ！", 
					icon: Ext.MessageBox.INFO  
			    })
 			},
			fileSizeLimit:me.fileSizeLimit,
			isDelete:true
		});
		
		var win=Ext.widget("window",{
	   			title: '图片上传',
	            modal:true,
	            border:false,
            	width:380,
	   			height: 120,
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
	   	win.show();
	},
	/**
	 * 放大照片或者缩小照片
	 */
	zoomImage:function(el, offset, type ,flag){
	  var me = this;
	  var width = el.getWidth();
      var height = el.getHeight();
      var nwidth = type ? (width * offset) : (width / offset);
      var nheight = type ? (height * offset) : (height / offset);
      if(flag!=1){
	      me.imageheight = nheight;
	      me.imagewidth = nwidth;
      }
      var left = type ? -((nwidth - width) / 2) : ((width - nwidth) / 2);
      var top = type ? -((nheight - height) / 2) : ((height - nheight) / 2);
      if(flag==1){
      	me.buttonimgheight = nheight;
      	me.buttonimgwidth = nwidth;
      	el.setWidth(nwidth);
      	el.setHeight(nheight);
      }else{
      	el.animate({
                  to: {
            width: nwidth,
            height: nheight,
            left:left,
            top:top
        }
            }, null, null, 'backBoth', 'motion');
      }
	},
	/**
	 * 窗口唯一，只有图片切换
	 * @param {} flag
	 * @param {} vo
	 */
	changeImage:function(flag,vo){
		var me = this;
		if(flag=='2'){
			me.flag = flag;
			me.nbase = vo.nbase;
			me.a0100 = vo.a0100;
			me.quality = vo.quality;
			me.isupload = vo.isupload;
			me.fileid = vo.fileid;
			var image = me.createimage(me.imageheight,me.imagewidth);
			me.removeAll(true);
			me.bbar.removeAll();
			me.bbar.add(me.createtoolitem());
			me.add(image);
		}
		else if(flag=='1'){
			me.flag = flag;
//			me.filePath = vo.filePath;
//			me.pubRealPath = vo.pubRealPath;
			me.imagename = vo.imagename;
			me.mobile = vo.mobile;
			me.filename = vo.filename;
			me.perguid = vo.perguid;
			me.bencrypt = vo.bencrypt;
			me.isupload = vo.isupload;
			me.fileid = vo.fileid;
			var image = me.createimage(me.imageheight,me.imagewidth);
			me.removeAll(true);
			me.bbar.removeAll();
			me.bbar.add(me.createtoolitem());
			me.add(image);
		}
	}
})