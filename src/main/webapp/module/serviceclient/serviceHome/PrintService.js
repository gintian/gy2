Ext.define("ServiceClient.serviceHome.PrintService",{
	extend:'Ext.container.Container',
	serviceId:undefined,
	ins_id:undefined,
	task_id:undefined,
	templateId:undefined,
	templateType:undefined,
	checkValue:'',
	ifCheck:'',//服务须知启用标识
	maxView:3,
	minView:1,
	layout:'border',
	filename:'',
	readyFlag:false,
	ip:undefined,
	/*用于缓存页面显示参数*/
	viewData:undefined,
	//复写原组建方法
	initComponent:function(){
		var me = this;
		//此句代码为超类里的代码，不可删除
		this.callParent();
		//添加首页按钮
        var homeArea = Ext.getCmp('goHome');
        var area = Ext.create('Ext.container.Container',{
             id:'homeContainer',
             style:'cursor:pointer',
             layout:'hbox',
             items:[{
                        xtype:'image',
                        style:'margin-top:8px;margin-right:6px',
                        src:'/module/serviceclient/images/home.png',
                        width:16,height:16
                    },{
                        xtype:'component',
                        style:'font-size:20px;color:white;margin-right:8px',
                        html:sc.home.gohome
                    }],
                    listeners:{
                        click:me.backHome,
                        element:'el',
                        scope:me
                    }
        })
        homeArea.add(area);
		//初始化一下显示参数变量
		this.viewData = {};
		//调用后台交易类，查询数据
		var vo = new HashMap();
		var number = 'Initialize';
		vo.put('number',number);
		vo.put('serviceId',this.serviceId);
		vo.put('ins_id',this.ins_id);
		vo.put('task_id',this.task_id);
		vo.put('templateId',this.templateId);
		vo.put('ip',this.ip);
		vo.put('templateType',this.templateType);
		Rpc({functionId:'SC000000004',success:this.renderData,scope:this},vo);
	},
	initSecond:function(){
	    this.remove( Ext.getCmp('printControlArea'));
		var vo = new HashMap();
		vo.put('serviceId',this.serviceId);
		vo.put('ins_id',this.ins_id);
		vo.put('task_id',this.task_id);
		vo.put('templateId',this.templateId);
		vo.put('ip',this.ip);
		Rpc({functionId:'SC000000004',success:this.renderSecondData,scope:this},vo);
	},
	renderData:function(res){
		Ext.MessageBox.show({   
			title:sc.home.tip,   
			msg:sc.home.loadTip, //正在加载,请稍候...  
			progress:true,   
			width:300,   
			wait:true,   
			waitConfig:{interval:600},   
			closable:true 
		});
		var serviceConfig = Ext.decode(res.responseText);
		
		//创建数据显示区域
		this.createMainView(serviceConfig);//zuo容器
		//创建打印控制区域
		this.createPrintArea(serviceConfig);//you
	},
	renderSecondData:function(res){
		var serviceConfig = Ext.decode(res.responseText);
		//创建打印控制区域
		this.createPrintArea(serviceConfig);//you
	},
	/**
	 * 显示区域，用于显示pdf和控制翻页、缩放等
	 */
	createMainView:function(config){
		var me = this;
		me.filename = config.filename;
		var box = Ext.widget('container',{
			region:'center',
			id:'container_box',
			layout:{
				type:'vbox',
				align:'stretch'
			},
			items:[
			    //创建pdf显示容器  
			    me.createTemplateViewer(config.filename,config.fileError),//中pdf
			    //创建功能按钮容器
			    me.createFunctionController(config.fileError)//xia按钮容器
			   /* //创建打印数据保存区域
			    me.createPrintDataBox(config.printList)*/
			]
		});
		me.add(box);
	},
	/**
	 * 创建模板pdf显示区域，使用pdf.js插件实现
	 */
	createTemplateViewer:function(filename,fileError){
		var me = this;
		if(fileError){
			Ext.MessageBox.hide();
			Ext.Msg.alert(sc.home.tip,sc.home.loadDataError);//加载数据失败,请联系系统管理员!
			return;
		}
		var tmpView = Ext.widget('component',{
            flex:1,
            style:'margin-top:10px;',
            //pdf显示使用canvas实现
            //html:'<table align="center"><tr><td><canvas id="templateCanvas"></canvas></td></tr></table>'
            //pdf显示使用canvas实现   div套canvas实现图形拖拽
			html:'<table align="center" style="position:relative"><tr><td><div id="cover" style="cursor:pointer;position:relative"><canvas id="templateCanvas" style="position:relative"></canvas></div></td></tr></table>'
        });
		tmpView.on('render',function(){
            //js部分
            var divObj=Ext.getDom("cover");
            var moveFlag=false;
            //拖拽函数
            divObj.onmousedown=function(e){
            	ServiceClientSecurity.resetTime();
                moveFlag=true;
                var clickEvent=window.event||e;
                var mwidth=clickEvent.clientX-divObj.offsetLeft;
                var mheight=clickEvent.clientY-divObj.offsetTop;
                document.onmousemove=function(e){
                    var moveEvent=window.event||e;
                    if(moveFlag){
                        divObj.style.left=moveEvent.clientX-mwidth+"px";
                        divObj.style.top=moveEvent.clientY-mheight+"px";
                        divObj.onmouseup=function(){
                            moveFlag=false;
                        }
                    }
                }
            };
            
			me.viewData.canvas = Ext.getDom('templateCanvas');
			me.viewData.canvasContext = me.viewData.canvas.getContext('2d');
			filename = decode(filename);
			//调用PDFJS插件
			PDFJS.getDocument("/servlet/vfsservlet?fromjavafolder=true&fileid="+filename).then(function(pdf){
				me.viewData.pdfObj = pdf;
				me.viewData.totalCount = pdf.numPages;
				if(me.viewData.totalCount==1){
				    Ext.getCmp('beforePage').hide();
				    Ext.getCmp('afterPage').hide();
				}
				me.viewData.currentPage = 1;
				me.viewData.scale = 1.0;
				me.renderPdfPage(1,1.0);
			});
		});
		//创建pdf插件
		var printObject = document.getElementById("createPDF");
		if (printObject != undefined && printObject != null) {//判断pdf对象是否存在，如果存在就删除该对象
			var parentNode = printObject.parentNode;
			parentNode.removeChild(printObject);
		}
		var printObject = document.createElement("object");
		var printSrc = window.rootPath +"/servlet/DisplayOleContent?filename="+filename;//pdf路径
	    try {
	        printObject.id = "createPDF";
	        printObject.classid = "CLSID:CA8A9780-280D-11CF-A24D-444553540000";
	        printObject.width = 1;
	        printObject.height = 1;
	        printObject.src = printSrc;
	        document.body.appendChild(printObject);
	    } catch (e) {
	    }
		return tmpView;
	},
	
	renderPdfPage:function(pageNum,scale){
		var me = this;
		if(me.viewData.totalCount>1&&pageNum ==1){
			 Ext.getDom('beforePage').src = 'images/grayPrePage.png';
		}else{
			Ext.getDom('beforePage').src = 'images/prePage.png';
		}
		if(me.viewData.totalCount==pageNum&&me.viewData.totalCount!=1){
			Ext.getDom('afterPage').src = 'images/grayNextPage.png';
		}else{
			Ext.getDom('afterPage').src = 'images/nextPage.png';
		}
		me.viewData.pdfObj.getPage(pageNum).then(function(page){
			var viewport = page.getViewport(scale);
		    me.viewData.canvas.height = viewport.height;
		    me.viewData.canvas.width = viewport.width;

		    var renderContext = {
		      canvasContext: me.viewData.canvasContext,
		      viewport: viewport
		    };
		    var pageRendering = page.render(renderContext);
		    var completeCallback = pageRendering._internalRenderTask.callback;
		    pageRendering._internalRenderTask.callback = function (error) {
		    	    Ext.MessageBox.hide();
                    var el = Ext.getCmp('afterPage').getEl();
                    el.on('click',me.nextPage,me);
             };
		});
	},
	/**创建放大、缩小、上页下页控制按钮区域，控制pdf显示
	 */
	createFunctionController:function(fileError){
		if(fileError){
			return;
		}
		var me = this;
		var controller = Ext.widget('container',{
			height:70,
			style:'background:white;background-color:#f9f9f9',
			layout:{
				type:'hbox',
				align:'middle',
				pack:'center'
			},
			items:[{
				xtype:'image',
				src:'images/zoomIn.png',
				margin:'0 12 0 12',
				listeners:{
					click:me.zoomIn,
					element:'el',
					scope:me
				}
			},{
				xtype:'image',
				src:'images/zoomOut.png',
				margin:'0 12 0 12',
				listeners:{
					click:me.zoomOut,
					element:'el',
					scope:me
				}
			},{
				xtype:'image',
				id:'beforePage',
				src:'images/prePage.png',
				margin:'0 12 0 12',
				listeners:{
					click:me.prePage,
					element:'el',
					scope:me
				}
			},{
				xtype:'image',
				src:'images/nextPage.png',
				id:'afterPage',
				listeners:{
					render:function(){
					   Ext.getCmp('afterPage').getEl().on('click',me.nextPage,me);
					}
				}
			}]
		
		});
		
		return controller;
	},
	
	createPrintDataBox:function(fileList){
		var me =this;
		var innerHtml = "";
		var outName = "";
		for(var i=0;i<fileList.length;i++){
			outName = decode(fileList[i]);
			innerHtml += "<img src='/servlet/vfsservlet?fromjavafolder=true&fileid="+outName+"' id='page"+(i+1)+"' width=0/>";
		}
		me.readyFlag = true;
		return Ext.widget('component',{
			height:0,
			style:'border-left:1px #dedede solid',
			html:innerHtml
		});
	},
	//放大
	zoomOut:function(){
		ServiceClientSecurity.resetTime();
		if((this.viewData.scale+0.1)>=this.maxView)
			return;
		this.viewData.scale = this.viewData.scale+0.1;
		this.renderPdfPage(this.viewData.currentPage,this.viewData.scale);
		
	},
	//缩小
	zoomIn:function(){
		ServiceClientSecurity.resetTime();
		if((this.viewData.scale)<=this.minView)
			return;
		this.viewData.scale = this.viewData.scale-0.1;
		this.renderPdfPage(this.viewData.currentPage,this.viewData.scale);
	},
	//上页
	prePage:function(){
		ServiceClientSecurity.resetTime();
		if(this.viewData.currentPage==1)
			return;
		this.viewData.currentPage = this.viewData.currentPage-1;
		this.renderPdfPage(this.viewData.currentPage,this.viewData.scale);
		
	},
	//下页
	nextPage:function(){
	   ServiceClientSecurity.resetTime();
       if(this.viewData.currentPage==this.viewData.totalCount)
       	    return;
       var el = Ext.getCmp('afterPage').getEl();
       el.un('click',this.nextPage,this);
       this.viewData.currentPage = this.viewData.currentPage+1;
       this.renderPdfPage(this.viewData.currentPage,this.viewData.scale);
	},
	/**
	 * 创建打印控制区域
	 * serviceConfig:
	 * {
	 *  //将模板转为pdf，将经过Pubfunc.encrypt加密的文件名称传到前端。
		filename:”xxxxxxxx”,
		//打印数据
		printPages:['xxxx','xxxxxx'],
		//服务描述
		description:’xxxxxxxx’,
		//剩余可打印份数
		canPrintCount:1,
		printPrice:0.5
	 * }
	 */
	createPrintArea:function(serviceConfig){
		var me = this;
		var describe = serviceConfig.description;//服务须知
		/*if(!describe || describe.length<1 || describe.trim()==""){
           describe = '';
        }else {
        describe = '3.'+ describe;
        }*/
		if(!describe || describe.length<1 || describe.trim()==""){
            describe = '';
        }else {
            describe = describe;
        }
		var count = parseInt(serviceConfig.canPrintCount);
		if(count > 3){
			  count = 3;
		}
		if(!serviceConfig.description){
	          serviceConfig.description = sc.home.descriptionTip;//该服务没有描述！！
	    }
		var pageCount = parseInt(serviceConfig.pageCount);
		me.pageCount = pageCount;
		var showNum = [];
		if(count){
			for(var i = 0 ; i < count ; i++){
                showNum[i]={boxLabel: (i+1)+sc.setting.part,margin:'0 15 0 5',name:'number',inputValue:(i+1)};
            }
			if(!this.checkValue.number||count<this.checkValue.number){
				this.checkValue.number = 1;
			    showNum[0]={boxLabel: (1)+sc.setting.part,margin:'0 15 0 5',name:'number',inputValue:(1),checked:'checked'};
            }else{
        		showNum[this.checkValue.number-1]={boxLabel: (this.checkValue.number)+sc.setting.part,margin:'0 15 0 5',name:'number',inputValue:(this.checkValue.number),checked:'checked'};
            }
		}
		var printer =  Ext.widget('container',{
			id:'printControlArea',
			width:250,
			style:'background-color:#f9f9f9',
			region:'east',
			layout:{
				type:'vbox',
				align:'stretch'
			},
			items:[{
				xtype:'container',
				flex:1,
				//style:'background:white',
				items:[{  
				     itemId: 'card-0',  
				     border:0,
				     margin:'30 40 10 40',
				     //bodyStyle:'background:transparent',
				     html: '<p align="center" style="background-color:#f9f9f9"><font size="4" color="red"  face='+sc.home.attention+'>'+sc.home.attention+'</font></p>'
			    },{
			        xtype:'component',
			        style:'margin-left:40px',
				    hidden:me.ifCheck=='1' ? true:false,
			        html:'<p style="font-size:14px;background-color:#f9f9f9">'+sc.home.Notice+'</p><p style="font-size:14px;background-color:#f9f9f9">'+sc.home.NoticeBefore+serviceConfig.canPrintCount+sc.home.NoticeAfter+'</p>'
			    },{
			    	xtype:'component',
			    	style:'margin-left:3px;margin-right:2px',
			    	hidden:me.ifCheck=='0' ? true:false,
			        html:describe
			    }]
			},{
				xtype:'container',
				height:165,
				itemId:'btu',
				//style:'background:white',
				items:[{
					xtype:'panel',
					layout:{
						type:'vbox',
						align:'middle',
						pack:'end'
					},
					border:0,
					bodyStyle:'background:#f9f9f9',
					items:[{
					    xtype:'component',
					    hidden:me.ifCheck=='1' && count!=0 ? false:true,
					    html:'<p style="font-size:14px;background-color:#f9f9f9">'+sc.home.remainBefore+serviceConfig.canPrintCount+sc.home.NoticeAfter+'</p>'
					},{
				        xtype: 'radiogroup',
				        columns: serviceConfig.canPrintCount,
				        itemId:'showNum',
				        vertical: true,
				        region: 'center',  
				        margin:'0 20 15 50',
						border:false,
						items:showNum,
				        listeners:{      
				        	'change':function(group,checked){
			            	me.checkValue  = checked;
				        	}
						}
				    },{
				    	xtype:'panel',
				    	itemId:'character',
				    	style:'margin-bottom:15px',
				    	html:'<p><font size="2" color="red">'+sc.home.BeyondPrintTip+'</font></p>',//您已超出打印上限，无法使用打印功能!
				    	hidden:true,
				    	border:0
				    },{
				    	xtype:'image',
				        itemId:'print',
						height:30,
						width:160,
						margin:'5 0 10 0', 
						src:'images/print.png',
						listeners:{
							click:function(evt){
								Ext.MessageBox.confirm(sc.setting.remind,sc.home.printFreeBefore+serviceConfig.canPrintCount+sc.home.printFreeAfter, function (btn) { //您还可以免费打印   份！是否继续？
									if(btn=="yes"){
                                        me.doPrint();
                                      }else{    
                                        me.initSecond();
                                      }
							});
								
							},
							element:'el',
							scope:me
						}
					},{
						xtype:'image',
				        itemId:'noprint',
						height:30,
						width:160,
						margin:'5 0 10 0', 
						src:'images/noprint.png',
						hidden:true,
				    	border:0
					},{
						xtype:'image',
						height:30,
						width:160,
						margin:'5 0 40 0',
						src:'images/homepage.png',
						listeners:{
							click:me.backHome,
							element:'el',
							scope:me
						}
					}
					]}
				]
			}]
		});
	/*	 if( pageCount == 0){//如果终端机没有纸张不显示打印按钮
			    Ext.getCmp('printControlArea').down('#btu').down('#character').show();
			    Ext.getCmp('printControlArea').down('#btu').down('#print').hide();
			    Ext.getCmp('printControlArea').down('#btu').down('#character').setHtml('<p><font size="2" color="red">该打印机已经没有纸张!</font></p>');
			    Ext.getCmp('printControlArea').down('#btu').down('#noprint').show();
		 }else */
		if(count == 0){
    		Ext.getCmp('printControlArea').down('#btu').down('#character').show();
    		Ext.getCmp('printControlArea').down('#btu').down('#print').hide();
    		Ext.getCmp('printControlArea').down('#btu').down('#noprint').show();
		}
		if(serviceConfig.fileError){
            Ext.getCmp('printControlArea').down('#btu').down('#print').hide();
            Ext.getCmp('printControlArea').down('#btu').down('#noprint').show();
		}
		this.add(printer);
	},
	doPrint:function(){
		var me =this;
		//先判断纸张够不够
		var printCount = 0;
		if(!me.checkValue.number){
			printCount = 1;
		}else{
			printCount= me.checkValue.number;
		}
	    var needPage = me.viewData.totalCount * printCount;//打印机纸张数小于所需纸张数  
	    if(me.pageCount < needPage){
	    	Ext.Msg.alert(sc.home.tip,sc.home.paperInsufficientTip);//终端机下纸张不足！！
	    	return;
	    }
//	    if(!me.readyFlag){
//			var vo = new HashMap();
//			var number = 'Initialize';
//			vo.put('number',number);
//			vo.put('ins_id',me.ins_id);
//			vo.put('task_id',me.task_id);
//			vo.put('templateId',me.templateId);
//			vo.put('serviceId',me.serviceId);
//			vo.put('templateType',me.templateType);
//			vo.put("printList","loadPrintList");
//			vo.put("filename",me.filename);
//			
//			Rpc({functionId:'SC000000004',success:function(res){
//				var serviceConfig = Ext.decode(res.responseText);
//				Ext.getCmp('container_box').add(me.createPrintDataBox(serviceConfig.printList));
//			}},vo);
//		}
		Ext.Msg.wait(sc.home.printTip,sc.home.tip);//正在打印,请稍候...
		me.doSuccessPrint();
//		if(!me.runner && !me.readyFlag){  
//	         me.runner = new Ext.util.TaskRunner();  
//	         me.onTaskRunner = me.runner.start({  
//	             run : function(){  
//	            	if(me.readyFlag){
//	            		me.doSuccessPrint();
//	            		me.runner.stop(me.onTaskRunner);
//	         		}
//	             },  
//	             interval : 1000 
//	         });  
//	         me.runner.start(me.onTaskRunner);  
//	     }else{
//	    	 me.doSuccessPrint();
//	     } 
	},
	doSuccessPrint:function(){
		var me = this;
		var printCount = 0;
		if(!me.checkValue.number){
			printCount = 1;
		}else{
			printCount= me.checkValue.number;
		}
		var pdfObj = document.getElementById("createPDF");
        for(var i=0;i<printCount;i++){
          pdfObj.printAll();
        }
	    var vo = new HashMap();
	    vo.put('pageCount',me.pageCount);
//		var myDoc = {
//	        settings:{
//	        	copies:printCount,//控制次数
//	        	collate:true,  // 逐份打印，即以 1,2,3,...,1,2,3,... ,1,2,3,... 顺序打印  
//	        	topMargin:0,
//	            leftMargin:0,
//	            bottomMargin:0,
//	            rightMargin:0
//	        },   // 设置上下左距页边距为10毫米，注意，单位是 1/10毫米
//	        documents: document,
//	        copyrights: '杰创软件拥有版权  www.jatools.com'
//	    };
//        document.getElementById("jatoolsPrinter").print(myDoc,false); // 直接打印，不弹出打印机设置对话框 
        Ext.Msg.hide();
    	vo.put('serviceId',me.serviceId);
		vo.put('printCount',printCount);
		vo.put('templatePage',me.viewData.totalCount);
		vo.put('ip',me.ip);
		Rpc({functionId:'SC000000005',success:me.PrintEnd,scope:me},vo);
	},
	PrintEnd:function(){
		Ext.Msg.alert(sc.home.tip,sc.home.printSuccessTip);//打印成功
		this.initSecond();
	},
	/**返回首页按钮事件*/
	backHome:function(){
		ServiceClientSecurity.resetTime();
		//通过id获取<中心区域>容器
		var serviceMainBox = Ext.getCmp('serviceMainBox');
		//移除并销毁当前对象
		serviceMainBox.removeAll(true);
		var servicePlatform = Ext.getCmp('servicePlatformCmp');
		//将服务面板显示到 容器中
		serviceMainBox.add(servicePlatform);
		//销毁首页按钮
		Ext.getCmp('homeContainer').destroy();
	}
});
	
	
	
	