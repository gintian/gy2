/** changxy 20160707
 * id为Ext组件设置的id url下载或展示的链接  src为图片的src路径 desc 描述信息    config配置项设置参数信息
 * url desc  src 这些参数都可以为空
 * Ext对象使用方式： 组件添加监听事件时使用
 *    new EHR.photoViewer.PhotoViewer(
 *               {dataList:
 *                       [{src:图片路径 ,desc:描述信息 ,Fn:回调函数 ,scope:作用域 ,event:监听事件名，没有默认为click ,params:参数类型数组 ,url:图片打开的servlet }]
 *                 connEle:监听对象 this
                 config:{  //窗口配置项
                    height:100,//悬浮展示面板的高度   配置项可加可不加  默认300*100
                    width:400//悬浮展示面板的宽度 
                 }
                });
 * 
 * 
 * 非Ext对象使用
 *   var test =  new EHR.photoViewer.PhotoViewer({
 *              dataList:[{src:图片路径 ,desc:描述信息 ,Fn:回调函数 ,scope:作用域 ,event:监听事件名，没有默认为click ,params:参数类型数组 ,url:图片打开的servlet }]
 *   			connEle:id,
                config:{
                      width:400,
                      height:100,
                }
              }
              );
     test.setConnection();
 * 
 * dataList:
 * [{src:图片路径 ,desc:描述信息 ,Fn:回调函数 ,scope:作用域 ,event:监听事件名，没有默认为click ,params: ,url:图片打开的servlet }]
 * 悬浮面板关闭监听事件切换
 * config:{
 *  linteners:null/closeImg不设置参数默认鼠标离开关闭监听事件
 *    添加关闭按钮手动点击关闭 
 * }
 * 
 * 左右滑动时无法自动关闭，需调用mousewheeldestroy()直接关闭  
 * */
Ext.define("EHR.photoViewer.PhotoViewer",{
	hidden:true,
    connEle:undefined,
    mouseOnWindow:'',
    currentPageClickState:'',//当前触发翻页状态
    totalpage:0,
    currentPage:1,
    defaultwidth:300,
    defalutheight:100,
    ImgNum:null,
    dataList:null,//信息集合
    config:{
    width:null,//展开宽度
    height:null,//展开高度
    floatImgWidth:null,//展示图片宽
    floatImgHeight:null,//展示图片高
    ImgNum:null,//每页显示图片数
    listeners:null//设置监听事件
    },
    page:null,
    constructor:function(config){
    	Ext.apply(this,config);
    	this.initCom();
    },mouselisdestory:function(e,t,o){//监听鼠标点击是否是悬浮框内，不在则销毁面板
    	    var me=this;
            var winpanel= Ext.getCmp(me.Windowpanel);
            if(winpanel && !winpanel.owns(t)){//winpanel 悬浮面板对象   t，鼠标点击后选中的html 判断t是否是悬浮面板的子元素
               if(!Ext.getCmp('OnlinepreviewImage'))//OnlinepreviewImage 在线预览图片控件ID
                    me.mousewheeldestroy();
          }else
              return;
                
        },/*mouseoverlis:function(e,t,o){//监听鼠标点击是否是悬浮框内，不在则销毁面板  取消mouseover鼠标监听  改用监听鼠标 mousedown mouseup （drag 拖动） 都销毁悬浮面板
    	   var me=this;
            var winpanel= Ext.getCmp(me.Windowpanel);
           // console.log((winpanel && !winpanel.owns(t)));
            if(winpanel && !winpanel.owns(t)){//winpanel 悬浮面板对象   t，鼠标点击后选中的html 判断t是否是悬浮面板的子元素
               if(!Ext.getCmp('OnlinepreviewImage')){//OnlinepreviewImage 在线预览图片控件ID
               var task= new Ext.util.DelayedTask(function(){
                     me.setConnection(me.connEle);
                });
                task.delay(100);
               }
          }
          Ext.getDoc.removeListener("mouseover",me.mouselisdestory,me)
           return;
        },*/
        initCom:function(){
    	var me=this;
    	// 改用监听鼠标 mousedown mouseup （drag 拖动） 都销毁悬浮面板
    	Ext.getDoc().on('mousedown',me.mouselisdestory,me);
    	Ext.getDoc().on('mouseup',me.mouselisdestory,me);
    	Ext.getDoc().on("drag",me.mouselisdestory,me);
        this.setConnection(this.connEle);
        
    },mousewheeldestroy:function(){//滑动关闭
    	 var me =this;
    	 if(Ext.getCmp('OnlinepreviewImage'))//预览图片鼠标滚动时取消监听销毁
            return;
    	 Ext.each( Ext.ComponentQuery.query('panel[id^=_panelphoto]'),function(e){e.destroy();});//遍历之前清除之前重复点击创建的窗口元素，每次创建只加载一个
         Ext.each( Ext.ComponentQuery.query('container[id^=_panelWindowId]'),function(e){e.destroy();});
         return;
    },
    setConnection:function(eleConfs){
    	var me=this;
    	var bodyEle=Ext.getBody().dom.children;
        me.Zindex=0;
        for(var i=0;i<bodyEle.length;i++){
            if(bodyEle[i].style.zIndex&&(bodyEle[i].style.zIndex>me.Zindex)){
                    me.Zindex=bodyEle[i].style.zIndex;            
            }
        }  
    	Ext.getDoc().on("mousewheel",me.mousewheeldestroy,me);//鼠标滚动监听
    	if((typeof eleConfs.id)!="undefined"){ //判断是否包含id //使用ext对象
        	var com=Ext.get(eleConfs.id);
        	if(me.config.listeners==null){//当没有有监听事件时
                 com.on("mouseleave",me.showviewGetXYDom,me,{show:false,conf:'',id:eleConfs});
        	}else{
        	 Ext.each( Ext.ComponentQuery.query('panel[id^=_panelphoto]'),function(e){e.destroy();});//遍历之前清除之前重复点击创建的窗口元素，每次创建只加载一个
             Ext.each( Ext.ComponentQuery.query('container[id^=_panelWindowId]'),function(e){e.destroy();});
        	}
        	var param=new Object();
             param.id=eleConfs.id;
             param.show=true;
             param.config=me.dataList;
             me.showviewGetXYDom('','',param);
    	}else{//使用dom对象
    	     var com = Ext.get(''+eleConfs+'');
             if(me.config.listeners==null){
             com.on("mouseleave",me.showviewGetXYDom,me,{show:false,conf:'',id:eleConfs});
             }else{
             Ext.each( Ext.ComponentQuery.query('panel[id^=_panelphoto]'),function(e){e.destroy();});//遍历之前清除之前重复点击创建的窗口元素，每次创建只加载一个
             Ext.each( Ext.ComponentQuery.query('container[id^=_panelWindowId]'),function(e){e.destroy();});
             }
             var param=new Object();
             param.id=eleConfs;
             param.show=true;
             param.config=me.dataList;
             me.showviewGetXYDom('','',param);
    	}
    },showviewGetXYDom:function(show,conf,param){//获取xy坐标宽度
    	   var me=this;
           //获取浏览器的高度和宽度
           me._Panelwidth=null;
           me._PanelHeight=null;
           me.x=Ext.get(''+param.id+'').getX();
           me.y=Ext.get(''+param.id+'').getY()+Ext.get(''+param.id+'').getHeight();
           var height = document.body.clientHeight;
           var width = document.body.clientWidth;
         if(me.config.width==null&&me.config.height==null&&me.config.ImgNum!=null&&me.config.ImgNum!=""){//如果设置每张图片大小
           me.ImgNum=me.config.ImgNum;
           me._Panelwidth=me.config.floatImgWidth*me.config.ImgNum+me.config.ImgNum*17+60;//左右图片宽度为50，最右边图片边距10
           me._PanelHeight=me.config.floatImgHeight+40+20;//40描述栏高//20 上下padding 
         }else{//设置展现面板宽度和高度
            me._Panelwidth=me.config.width==null||me.config.width==""?me.defaultwidth:me.config.width;
            me._PanelHeight=me.config.height==null||me.config.height==""?me.defalutheight:me.config.height;
            me.config.floatImgHeight=me._PanelHeight-45;
            me.config.floatImgWidth=me.config.floatImgHeight*3/4  //图片宽高比3:
            me.ImgNum=( me._Panelwidth-50)/(me.config.floatImgWidth+20);//parseInt(pageNum)  每页个数
         } 
            if((me.x+me._Panelwidth)>width){//组件位置+悬浮面板宽度>浏览器宽度
                  me.x=width-me._Panelwidth;//面板所在位置x
                  me.ArrowImgX=Ext.get(''+param.id+'').getX()-me.x;
            }
            if((me.y+me._PanelHeight)>height){//组件高度超出窗口
                me.Top=Ext.get(''+param.id+'').getY()-me._PanelHeight-40;
            }
            me.y=me.y+14;
            me.showOrhide(param.show,param.config,param.id);
    },showOrhide:function(showorhide,photoArr,key){
    	     var me=this;
             var dealPanel=Ext.getCmp(me.panelId);
             var windowPanel=Ext.getCmp(me.Windowpanel);
             if(dealPanel){//防止点击后多次加载相同数据
                if(showorhide){
                    dealPanel.close();
                    windowPanel.destroy();
                    me.currentPage=1;
                    me.currentPageClickState='';
                    me.page=0;
                }else{
                    var task= new Ext.util.DelayedTask(function(){
                    if(!me.mouseOnWindow){
                       dealPanel.close();
                       windowPanel.destroy();
                       me.currentPage=1;
                       me.currentPageClickState='';
                       me.page=0;
                     }
                     });
                     task.delay(300);
                     } 
             }
             if(!showorhide){
                 return;
             }
              me.showViewer(photoArr,key)        
    },showViewer:function(photoArr,key){ //ext对象调用空间使用
            var me=this;
            me.leftImgId=Ext.id('','_leftImg');
            var leftImg=Ext.create('Ext.Img',{
                      id:me.leftImgId,//'leftImg',
                      width:14,
                      height:33,
                      shadow:false,
                      style:'margin-top:'+(me.config.floatImgHeight+18)/2+'px; margin-left:5px;',//cursor:pointer;//;me.currentPage (me.currentPage!=1)?'cursor:pointer;margin-top:'+(me.config.floatImgHeight+18)/2+'px; margin-left:5px;':
                      src:'/images/new_module/left1.png',
                      listeners:{
                        'click':{
                            element:'el',
                            fn:function(){me.leftPage();}
                            
                        }
                      }
                  });
           me.rightImgId=Ext.id('','_rightImg');
           var rightImg=Ext.create('Ext.Img',{
                      id:me.rightImgId,//'rightImg',
                      width:14,
                      height:33,
                      shadow:false,
                      style:'margin-top:'+(me.config.floatImgHeight+18)/2+'px;',//(me.totalpage>1||me.totalpage>me.currentPage) (me.totalpage>1&&me.totalpage>me.currentPage)?'cursor:pointer;margin-top:'+(me.config.floatImgHeight+18)/2+'px;cursor:pointer;':
                      src:'/images/new_module/right1.png',
                      listeners:{
                            click:{
                            element:'el',
                            fn:function(){
                            me.rightPage();
                            }
                        }
                      }
                  });
          var container=Ext.widget('container',{//展现内容
                    layout:'hbox',
                    border:false
          })
         
         //动态生成id
          me.rightPanel=Ext.id('','_righttoolbar');
          me.leftPanel=Ext.id('','_lefttoolbar');
          me.panelArrowsId=Ext.id('','_panelArrow');
          var marginheight=-(me._PanelHeight+13)
          var panelArrows=Ext.create('Ext.Img',{
                  width:24,
                  height:14,
                  border:false,
                  style:'z-index:'+(me.Zindex+9998)+'',//与关闭按钮一致
                  margin:(me.Top?'-1 0 0 '+(me.ArrowImgX?me.ArrowImgX:5)+'':''+(marginheight-8)+' 0 0 '+(me.ArrowImgX?me.ArrowImgX:5)+''),
                  id:me.panelArrowsId,
                  shadow:false//,
                  //src:(me.Top?"/images/photoView.png":"/images/bottom.png")//,
                
              });
              if(me.Top)
                    panelArrows.setSrc("/images/photoView.png");   
              else
                    panelArrows.setSrc("/images/bottom.png"); 
          me.panelId=Ext.id('','_panelphoto');
          var panelWindow=Ext.widget('panel',{
                  id:me.panelId,
                  width:me._Panelwidth,
                  height:me._PanelHeight+8,//40描述栏高//20 上下padding 
                  border:true,
                  style:'margin-top:14px',
                  bodyStyle:';border-left:0px;border-right:0px;;',
                  header:false,
                  shadow:false,//无阴影
                  layout:{
                      type:'hbox' 
                  },
                  closeAction:'destroy',
                  dockedItems:[{
                      xtype:'toolbar',
                      id:me.rightPanel,
                      width:25,
                      dock:'right',
                      items:[rightImg]
                  },{
                      xtype:'toolbar',
                      id:me.leftPanel,
                      width:25,
                      dock:'left',
                      items:[leftImg]
                  }],
                  items:[container],
                  listeners:{
                      'mouseenter':{  //鼠标滑过可以不监听子元素
                        element:'el',
                        fn:function(){
                            me.mouseOnWindow=true;
                        }
                      },
                      'mouseleave':{
                        element:'el',
                        fn:function(){
                        	if(me.config.listeners==null){
                                me.mouseOnWindow=false;
                                me.showOrhide(false);
                        	}
                        }
                      }
                  }
              });
          //关闭图片按钮
          var closeImg=Ext.create('Ext.Img',{
          	   width:16,
          	   height:14,
          	   style:'z-index:'+(me.Zindex+9998)+';cursor:pointer;',
          	   src:'/images/close2.gif',
          	   margin:(me.Top?''+(marginheight-5)+' 0 0 '+(me._Panelwidth-18)+'':'1 0 0 '+(me._Panelwidth-18)+''),
          	   shadow:false,
          	   hidden:me.config.listeners!=null?false:true,
          	   listeners:{
          	     'click':{
          	          element:'el',
                      fn:function(){
                       /* me.mouseOnWindow=false;
                        me.showOrhide(false);*/
                      	me.mousewheeldestroy();
                  }
          	     }
          	   }
          });    
              
          me.Windowpanel=Ext.id('','_panelWindowId');
          var panel=Ext.widget('container',{
               id:me.Windowpanel,
               x:me.x,
               y:(me.Top?me.Top:(me.y-14)),
               layout:{
                    type:'vbox',
                    align:'align'
               },
               border:false,
               style:'border:1px solid;z-index:'+(me.Zindex+9999)+'',
               bordyStyle:'opacity:0;filter:alpha(opacity=0);',
               floating : true,
               height:me._PanelHeight+40,
               width:me._Panelwidth,
               bodyStyle:'opacity:0;',
               shadow: false,
               closeAction :'destroy',
               renderTo:Ext.getBody(),
               items:[panelWindow,panelArrows,closeImg]
                   
          });
          me.showPage(container,photoArr);
    },showPage:function(container,photoArr){//分页
           var me=this;
             //集合分页
           me.container=container;//设置全局变量存储
           me.photoArr=photoArr;
           me.container.removeAll(true);//移除翻页之前的元素
           me.total=photoArr.length;//总数        
           //总页数
           var pageNum=me.ImgNum;
           //向下取整
           if(pageNum.toString().indexOf('.',0)>-1)//有小数情况
                pageNum=pageNum.toString().substring(0,pageNum.toString().indexOf('.',0));
           if(pageNum==0){
                me.totalpage=1; //页数为0则为第一页
           }else{
                me.totalpage=me.total/pageNum;// parseInt(me.totalpage)
                me.totalpage=me.totalpage.toString().indexOf('.',0)>-1?parseInt(me.totalpage.toString().substring(0,me.totalpage.toString().indexOf('.',0)))+1:me.totalpage; //总页数
           }
            var startnum=pageNum*(me.currentPage-1);//起始数
            var endnum=pageNum*me.currentPage-1;
            for(var i=0;i<photoArr.length;i++){//数据解析
                if(i>=startnum&&i<=endnum){
                me.container.add(me.createImgdetil(photoArr[i]));
                if(photoArr[i].event==null){
                  Ext.getCmp(me.detailImgId).getEl().on("click",me.ImgEventListeners,me,{obj:photoArr[i]});
                 }else if(photoArr[i].event){
                  Ext.getCmp(me.detailImgId).getEl().on(""+photoArr[i].event+"",me.ImgEventListeners,me,{obj:photoArr[i]});  
                 }else if(photoArr[i].url!=null){
                  Ext.getCmp(me.detailImgId).getEl().on("click",me.ImgEventListeners,me,{obj:photoArr[i]});
                 }
                }
            }
            var leftImg=Ext.getCmp(me.leftImgId);
            var rightImg=Ext.getCmp(me.rightImgId);
            if(me.totalpage>1&&me.totalpage>me.currentPage)//可以翻页的情况下添加手型样式
                rightImg.setStyle('cursor','pointer');
            else
                rightImg.setStyle('cursor','');
            if(me.currentPage!=1)
                leftImg.setStyle('cursor','pointer');
            else
                leftImg.setStyle('cursor','');
            
            if(me.currentPageClickState=='1'){
               container.setPosition(-container.getWidth(),0,false);
               container.setPosition(0,0,true);
            }else if(me.currentPageClickState=='2'){
               container.setPosition(2*container.getWidth(),0,false)
               container.setPosition(0,0,true);
            }
    },leftPage:function(){
            var me=this;
            if(me.currentPage==1){
               return;
            }
            me.currentPage=me.currentPage-1;
            me.currentPageClickState='1'; //左翻页
            me.showPage(me.container,me.photoArr);
   },rightPage:function(){//右翻页
            var me=this;
            if(me.currentPage==me.totalpage){
               return;
            }
           me.currentPage=me.currentPage+1;
           me.currentPageClickState='2';
           me.showPage(me.container,me.photoArr);
   },createImgdetil:function(obj){//生成详情子元素
            var me=this;
            me.detailImgId=Ext.id('','_detailImg');
            var detailImg=Ext.create('Ext.Img',{
                    width:me.config.floatImgWidth,//展开图片宽度
                    height:me.config.floatImgHeight,
                    margin:'0 10 0 10',
                    id:me.detailImgId,
                    style:(obj.url!=null||obj.Fn!=null)?'cursor:pointer;':'',//当为url或者有回调函数时添加手势
                    src:obj.src
                });
              var panel = Ext.widget('panel',{
                            border:false,
                            header:false,
                            margin:'18 0 10 0',
                            //width:me.config.floatImgWidth,
                            style:'border-width:0px;',
                            layout:{
                                type:'vbox',
                                align:'center'
                            },
                        items:[detailImg,{
                            xtype:'label',
                            html:'<span style="text-align:center;border-width:none;" title="'+obj.desc+'">'+me.dealText(obj.desc)+'</span>',
                            width:60,
                            maxHeight:38,
                            border:'0 0 0 0',
                            style:'text-align:center;word-break:break-all;'
                            
                        }]
        });
        return panel;
        },ImgEventListeners:function(event,fn,params){//判断是url链接打开还是回调
        	var me=this;
          if(params.obj.url!=null){
           	window.open(params.obj.url);
          }
          if(params.obj.Fn!=null){
          	if(params.obj.scope)
            	Ext.callback(eval(params.obj.Fn),params.obj.scope,params.obj.params); 
        	else
            	Ext.callback(eval(params.obj.Fn),null,params.obj.params); 
            	
          }
        },dealText:function(text){//处理文本折行
            var me=this;
            var str=null;
            var num=0;
            var Regx = /^[A-Za-z]*$/;
            var NumRegx = /^[0-9]*$/;
            var realLength=0;//字符与汉字所占像素
            var subStringText=null;
            for(var i=0;i<text.length;i++){
               if(Regx.test(text.charAt(i))){//英文
                    realLength+=7;
                    num++;
               }else if(NumRegx.test(text.charAt(i))){//数字
               	    realLength+=9;
                    num++;
               }else{//中文
                    realLength+=12;
                    num++;
                }
                if(realLength>=120){
                 subStringText=text.substring(0,num-1);
                 text=subStringText+"...";       
                 break;
                }
            }
            return text;    
        }
    
});
	