//修改ext默认图标src。防止连接www.sencha.com
	Ext.BLANK_IMAGE_URL='data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';
 
Ext.define('ConfigFileURL.Comp',{
	requires:['EHR.extWidget.proxy.TransactionProxy'],
        constructor:function(config){//构造方法
	    	flag=config.flag;//业务类标识
	    	this.value=config.value; //页面上已有的模版值
	    	
        	this.init();
        },
        //初始化数据
		 init:function(){
        	var treeStore = Ext.create('Ext.data.TreeStore', {
				proxy:{
				    	type: 'transaction',
		        		functionId:'ZC00004001',//交易类
				        extraParams:{
			        		value:this.value//参数
				        },
				        reader: {
				            type: 'json',
				            root: 'data'//返回值       	
				        }
				},
				root: {
					// 根节点的文本					
					text:zc.label.selectPn,
					expanded: true,
					icon:"/images/add_all.gif"
				}
			});
			treePanel = Ext.create('Ext.tree.Panel', {
				// 不使用Vista风格的箭头代表节点的展开/折叠状态
				useArrows: false,
				border:true,
				height:425,
				id:'treePanel',
				store: treeStore, // 指定该树所使用的TreeStore
				rootVisible: true // 指定根节点可见
			});
			var clickvalue=new Array();
			if(this.value.indexOf(",")>0){
				clickvalue=this.value.split(",");
			}else{
				clickvalue[0]=this.value;
			}
			//监听1，所有业务类共有监听
			treePanel.on("itemclick",function(view,record,item,index,e){//监听被点击的节点信息
				
						for(var e=0;e<clickvalue.length;e++){//遍历以前选中的记录
							if(clickvalue[e]==record.raw.text){
								Array.prototype.remove = function(dx){ //数组按下标删除元素
								    if(isNaN(dx)||dx>this.length){return false;} 
								    this.splice(dx,1); 
								}
								clickvalue.remove(e);//去掉相同值
							}
						}
				});
			//只能单选时的监听
			if(flag!="3"&&flag!="6"){
				//监听2，监听当前选中的节点
				treePanel.on('checkchange', function(node, checked) { //单选,子节点都展开时选择
					var nodes = treePanel.getChecked();//获得所有选中的记录
					Ext.each(nodes, function (node1) {//遍历选中的节点
						if(node.data.id!=node1.data.id) {//把除了当前选中的节点外都设置未选中
						
							node1.set("checked",false); //设为未选中状态
						}
					});
				}, treePanel);
				//监听3，监听当前点击的父节点
				treePanel.on('itemexpand', function(obj) { //单选,以前选中节点未展开
					var nodes = treePanel.getChecked();//获得所有被选中的模版
				
					Ext.each(nodes, function (node1) {//遍历
					
						if(node1.data.leaf) {//确定是子节点
							var id=node1.data.id.split(".");//从子节点中去其所属父节点的id
							if(id[1]==obj.data.id){         //当该子节点是当前展开的父节点下的，把其他子节点都设为未选中状态
								Ext.each(nodes, function (node2) {//遍历选中的节点
									var id1=node2.data.id.split(".");
									if(id1[1]!=obj.data.id){
										node2.set("checked",true); //设为未选中状态
										node1.set("checked",false);
									}
								});
								
							}
						}
					});
				});
			}
			//创建一个窗口
		   	var win=Ext.widget("window",{
		          title:zc.label.yewuWin,  
		          width:330,
		          height:500, 
		          layout:'fit',
		          resizable:false,//是否允许改变窗口大小
				  modal:true,//模态窗口
				  closable:true,//是否显示关闭按钮
				  closeAction:'destroy',//控制按钮是销毁（destroy）还是隐藏（hide）
				  border: false,
				  plain:true,//true则主体背景透明，false则主体有小差别的背景色，默认为false
		          items: [{
		         		xtype:'panel',
		         		border:false,
						items:[treePanel],
						buttons:[
			          		{xtype:'tbfill'},
			          		{
			          			text:common.button.ok,
			          			handler:function(){
			          			var nodes = treePanel.getChecked();
			          			var va = new Array();
			          			var i=0;
			          			
			          			Ext.each(nodes, function (node) {//遍历选中的节点
									if (node.data.leaf==true) {
										
										    va[i]=node.data.text;
									    	i++;
									}
								});
			          			
			          			//分别处理单选和多选情况
			          		     //1.可以多选的业务类
			          			if(flag=="3"||flag=="6"){
			          				if(clickvalue[0]==""||clickvalue[0]==" "){//以前为选模版，或者与当前选中模版相同时
			          					configFileGloble.sure(flag,va.toString());//点击确定后，根据flag标识给业务模版赋值
			          				}else{//以前选中的还有至少一个值时
			          					if(va==""||va==" "){//当前未选模版
			          						configFileGloble.sure(flag,clickvalue.toString());
			          					}else{//当前选中不为空
			          						for(var a=0;a<clickvalue.length;a++){//循环判断两个数组元素是否有重复的，去重
			          							for(var b=0;b<va.length;b++){
			          								Array.prototype.remove = function(dx){//数组按下标删除元素
			        								    if(isNaN(dx)||dx>this.length){return false;} 
			        								    this.splice(dx,1); 
			        								}
			          								if(clickvalue[a]==va[b]){
			          									clickvalue.remove(a);
			          								}
			          							}
			          						}
			          						//结果排序
			          						var maopao=new Array();
			          						if(clickvalue.length==0){//判断以前选的值是否还有
			          							maopao=(va.toString()).split(",");//以前的值已经没有了
			          						}else{//以前和当前都有选中时的结果
			          							maopao=(clickvalue.toString()+","+va.toString()).split(",");
			          						}
			          						    for(var c=0;c<maopao.length;c++){
			          						    	for(var d=c+1;d<maopao.length;d++){
			          						    		var maopao1=maopao[c].substring(0,maopao[c].indexOf("."));//截取id
			          						    		var maopao2=maopao[d].substring(0,maopao[d].indexOf("."));
			          						    		//alert("maopao1:"+maopao[c]+",maopao2:"+maopao[d]);
			          						    		if(Number(maopao1)>Number(maopao2)){//String转换int，比较大小
			          						    			var str=maopao[c]; 
			          						    			maopao[c]=maopao[d];
			          						    			maopao[d]=str;
			          						    		}
			          						    	}
			          						    }
			          						configFileGloble.sure(flag,maopao.toString());
			          					}
			          				}
			          			}
			          		//2.只能单选的业务类
			          			else{
			          				
			          				if(clickvalue[0]==""||clickvalue[0]==" "){//以前选中的模版为空时
			          				
			          						configFileGloble.sure(flag,va.toString());//点击确定后，根据flag标识给业务模版赋值,转换成字符串

			          				}else{//以前选中的模版不为空
			          					if(va.length==0){//下拉树为空
			          						configFileGloble.sure(flag,clickvalue.toString());//点击确定后，根据flag标识给业务模版赋值,转换成字符串
			          						
			          					}else{//下拉树不为空
			          					    configFileGloble.sure(flag,va.toString());
			          					}
			          				}
			          			}
			          			win.close();
			          			} 
			          		},          		
			          		{
			          			text:common.button.cancel,
			          			handler:function(){

			          				win.close();
			          			}
			          		},
			          		{xtype:'tbfill'}
			           ]
		          }]     
		    }); 

		    win.show();  
		 }
 });
