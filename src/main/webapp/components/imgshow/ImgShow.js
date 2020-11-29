/**
显示图片
lis 2016-06-21
**/
Ext.define('EHR.imgshow.ImgShow',{
       constructor:function(config){
			var img_me = this;
			img_me.filePath = config.filePath;// 文件路径
			img_me.srcfilename = config.srcfilename;// 图片原始名称
			this.init();
	    },
	    // 初始化获取相关参数
	    init:function(){
	    	var img_me = this;
	    	img_me.current = 0;
	    	img_me.rotation = 0;
	    	img_me.real_width = 0;
			img_me.real_height = 0;
	    	var src = "/servlet/vfsservlet?fromjavafolder=true&fileid=" + img_me.filePath;
        	var img = Ext.create('Ext.Img', {
        		id:'image'+img_me.srcfilename,
			    src: src,
			    width:'100%',
			    height:'100%',
			    border:0
			});
        	var width_win = document.body.clientWidth*0.7;
			var height_win = document.body.clientHeight*0.9;
			var imgName = "";// 图片名称
			if(img_me.srcfilename)
			    imgName = img_me.srcfilename.substring(0,img_me.srcfilename.lastIndexOf("."));
	    	var win = Ext.create('Ext.window.Window', {
			    title: imgName,
			    id:'OnlinepreviewImage',// 固定Id 用于悬浮展示图片控件 changxy 20160926 id固定不可改变
			    width:width_win,
			    height:height_win,
			    layout:'fit',
			    //maximizable:true,//放大按钮
			    modal:true,
		    	resizable :false,
			    closeAction:'destroy',
			    items: [{
	                	xtype:'container',
	                	id:'container_'+img_me.srcfilename,
	                	autoScroll:true,
	                	border:0,
	                	layout:{  
	    	             	type:'hbox',  
	    	             	pack:'center',  
	    	              	align:'middle'  
	    	            },
	                	items:img
	             }],
                 buttonAlign:'center',
                 bbar: [
                  { xtype: 'tbfill' },{xtype:'button',text:'放大',handler:function(){
                	  // 每次放大百分之10
                	  img_me.largeOrReduce('1');
                  }},{xtype:'button',text:'缩小',handler:function(){
                	  // 每次缩小百分之10
                	  img_me.largeOrReduce('0');
                   }},{xtype:'button',text:'旋转',handler:function(){
                 	  // 每次缩小百分之10
                 	  img_me.ratate();
                    }},
                    { xtype: 'button', text: common.button.downLoad ,handler:function(){
                 		 var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+ img_me.filePath
		                +"&displayfilename="+$URL.encode(img_me.srcfilename),"");	
                  }},
                  { xtype: 'button', text: common.button.close,handler:function(){
                 		 win.close();
                 		 if(Ext.getCmp('image'+img_me.srcfilename))
                 			Ext.getCmp('image'+img_me.srcfilename).destroy();
                  }},
				   { xtype: 'tbfill' }
				 ],
				 listeners:{
						render:function(){
							this.mon(Ext.getDoc(), {
				                mousewheel: this.lagreOrReduce,
				                scope: this
				            });
						}
					},
				 lagreOrReduce: function(e) {
			        var type = e.getWheelDelta();
			        var flag='1';
			        if(type<0)
			        	flag='0';
			        img_me.largeOrReduce(flag);
			    }
			 }).show();
	    	 var container_width = document.getElementById('container_'+img_me.srcfilename+'-innerCt').style.width+'';
			 document.getElementById('container_'+img_me.srcfilename+'-innerCt').style.width=container_width.substring(0,container_width.indexOf("px"))-1;
			 document.getElementById('container_'+img_me.srcfilename+'-targetEl').style.width=container_width.substring(0,container_width.indexOf("px"))-1;
	    	 var image = document.getElementById('image'+img_me.srcfilename);
			 var maximgW = document.body.clientWidth*0.6;
		     var maximgH = document.body.clientHeight*0.8;
			 var real_width,real_height;
			 if (image.naturalWidth) {
					real_width = image.naturalWidth;
				　　real_height = image.naturalHeight;
					img_me.resize(maximgW,real_width,maximgH,real_height,image);
			 }else {
				　　var nImg = new Image();
				　　nImg.src = image.src;
				　　if(nImg.complete) { // 图片已经存在于浏览器缓存
						real_width  = nImg.width;
						real_height = nImg.height;
						img_me.resize(maximgW,real_width,maximgH,real_height,image);
				　　}else{
				　　　　nImg.onload = function () {
				　　　　　　real_width = nImg.width;
				　　　　　　real_height = nImg.height;
							img_me.resize(maximgW,real_width,maximgH,real_height,image);
				　　　　}
				　　}
		      }
		      //火狐单独处理
			  if(/Firefox/i.test(navigator.userAgent)){
				 var winImage = document.getElementById('OnlinepreviewImage');
			     if (winImage.addEventListener) {
			    	 winImage.addEventListener('DOMMouseScroll', function(event) {
			        	 var value = event.wheelDelta || -event.detail;
			             var wheelDelta = Math.max(-1, Math.min(1, value));
			             var flag='1';
				         if(wheelDelta<0)
				        	 flag='0';
			             img_me.largeOrReduce(flag);
			         }, false);
			     }
			 }
	    },
	    /**
		 * 缩放图片
		 */
	    resize:function(maximgW,real_width,maximgH,real_height,image){
	    	var img_me = this;
	    	if(maximgW<=real_width){
				 real_height = (maximgW/real_width)*real_height;
				 real_width = maximgW;
			 }
			 else if(maximgH<=real_height){
				 real_width = (maximgH/real_height)*real_width;
				 real_height = maximgH;
			 }
	    	 var width_win = document.body.clientWidth*0.7;
			 var height_win = document.body.clientHeight*0.9;
	    	 image.style.height=real_height;
			 image.style.width=real_width;
			 var left = (width_win-real_width)/2;
			 var top = (height_win-70-real_height)/2;
			 image.style.left=left;
			 image.style.top=top;
			 img_me.real_width = real_width;
			 img_me.real_height = real_height;
	    },
	    /**
		 * 放大或者缩小图片
		 */
	    largeOrReduce:function(flag){
	    	var img_me = this;
	    	var image = document.getElementById('image'+img_me.srcfilename);
      	    var width = image.width;
      	    var height = image.height;
      	    var height_l=0,width_l=0;
      	    if(flag=='1'){
      	    	height_l = height*(1+0.1);
      	    	width_l = width*(1+0.1);
	    	}else{
      	    	height_l = height*(1-0.1);
      	    	width_l = width*(1-0.1);
      	    }
      	    if(flag=='1'&&height>img_me.real_height*10)// 最大放大到百分之1000
       		    return;
      	    else if(flag=='0'&&height<img_me.real_height*0.1)// 最小缩放到百分之10
           		return;
      	    var img = Ext.getCmp('image'+img_me.srcfilename);
      	    img.setHeight(height_l);
      	    img.setWidth(width_l);
      	    if(Ext.isIE){
	      	    if(img_me.rotation==1||img_me.rotation==3){
	      	    	var winwidth = document.body.clientWidth*0.7;
	      	    	var winheight = document.body.clientHeight*0.9;
      	    		image.style.left=(winwidth-height_l)/2;
 			    	image.style.top = (winheight-width_l)/2;
	 		    }
      	    }
	    },
	    /**
	     * 旋转图片
	     */
	    ratate:function(){
	    	var img_me = this;
	    	var image = document.getElementById('image'+img_me.srcfilename);
	    	if(Ext.isIE){
	    		if(img_me.rotation==3)
	    			img_me.rotation=0;
	    		else
	    			img_me.rotation++;
	    		 image.style.filter = 'progid:DXImageTransform.Microsoft.BasicImage(rotation='+img_me.rotation+')';
	    		 var winwidth = document.body.clientWidth*0.7;
	    		 var winheight = document.body.clientHeight*0.9;
	    		 var imgheight = image.height;
	    		 var imgwidth = image.width;
	    		 if(img_me.rotation==1||img_me.rotation==3){
    				 image.style.left = (winwidth-imgheight)/2; 
    				 image.style.top = (winheight-imgwidth)/2;
	    		 }
	    		 if(img_me.rotation==0||img_me.rotation==2){
	    			 image.style.left = (winwidth-imgwidth)/2;
	    			 image.style.top = (winheight-imgheight)/2;
	    		 }
	    	}else{
	    		img_me.current += 90;
		    	image.style.transform = 'rotate('+img_me.current+'deg)';
	    	}
	    }
});