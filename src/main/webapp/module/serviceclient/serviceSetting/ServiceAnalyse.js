Ext.define('ServiceClient.serviceSetting.ServiceAnalyse',{
	extend:'Ext.panel.Panel',
	title:sc.setting.controlanalyse,//监控分析
	id:'controlanalyse',
	layout:'vbox',
	clientArray:[],
	services:'-1',
	dateType:'week',
	scrollable :'y',
	analyseKeyData:undefined,
	clientList:undefined,
	listGrid:'',
	width:undefined,
	myChart:'',
	gridConfig:'',
	listeners:{
		element:'el',
		click:function(){
			var serviceSelect = Ext.getCmp('serviceSelect');
			var qstCzmx = Ext.getCmp('qstCzmx');
			if(serviceSelect){
				serviceSelect.close();
			}
			if(qstCzmx){
				qstCzmx.close();
			}
		}
	},
	initComponent:function(){
		this.callParent();
		this.createSelfCss();//动态引入,复写样式
		this.tools=[{
			xtype:'image',height:15,width:15,
			itemId:'analyseCloseImg',
			src:'/module/serviceclient/images/close_mouseout.png',
			style:'cursor:pointer',
			listeners:[
				{
    				element:'el',
    				click:function(){
    					window.location.href="setting.html";
    				},
    				scope:this
			    },{
			    	element:'el',
                    mouseover:function(){
                    	var closeImg = this.query('#analyseCloseImg')[0];
                        closeImg.setSrc('/module/serviceclient/images/close_mouseover.png');
                    },
                    scope:this
			    },{
			    	element:'el',
                    mouseout:function(){
                    	var closeImg = this.query('#analyseCloseImg')[0];
                        closeImg.setSrc('/module/serviceclient/images/close_mouseout.png');
                    },
                    scope:this
			    }
			]
		}];
		this.initAnalyseData();
		this.width = Ext.getBody().getViewSize().width;
	},
	/**加载数据*/
	initAnalyseData:function(){
		var vo = new HashMap();
		vo.put('transType','init');
		Rpc({functionId:'SC000000003',success:this.renderData,scope:this},vo);
	},

	renderData:function(res){
		var resData = Ext.decode(res.responseText);
		this.clientList = resData.clientList;
		if(!this.clientList || this.clientList.length>0){
			this.clientArray.push(this.clientList[0].clientId);
		}
		var serviceList = resData.serviceList;
		var analyseData = resData.analyseData;
		this.createClientArea(this.clientList);
		this.createAnalyseArea(serviceList,analyseData);
	},
	
	/**
	 * 创建终端机显示区域
	 * 参数：
	 * clientList:终端数据集合
	 * [{
	 * 	 clientId:1,
		 name:’一号教学楼’,
		 ip:’xxx.xxx.xxx.xxx’,
		 pageCount:40 //剩余纸张数
	 * }]
	 * 
	 * 终端需要支持选中状态，用于选中不同终端，统计不同的数据。选中的数据要保存到对象全局变量analyseKeyData中，并调用
	 * reloadAnalyse方法刷新页面分析数据
	 */
	createClientArea:function(clientList){
		var me = this;
		var addImage = {
			xtype:'image',
			style:'float:left;cursor:pointer;',
			src:'/images/new_module/nocycleadd.png',
			width:45,
			height:45,
			margin:25,
			listeners:{
				click:{
					element:'el',
					fn:function(){
						//新增终端机window
						Ext.create('ServiceClient.serviceSetting.AddClient',{
							handler:me.addClient,
							clientList:this.clientList
						}).show();
						Ext.getCmp('clientAdd_header').setStyle({
							borderStyle:'hidden hidden solid hidden'
						});
					},
					scope:me
				}
			}
		};
		//终端机行panel
		var middlePanel = Ext.create('Ext.form.Panel',{
			id:'middleBox',
			layout:'hbox',//水平布局
			border:false,
			width:'100%',
			items:[{
				margin:'28 0 0 10',
				xtype:'component',
				html:"<font style='font-size:14px'>"+sc.setting.serviceend+"</font>",//服务终端
				width:'7%'
			},{
				xtype:'container',
				border:false,
				itemId:'rightBox',
				style:'position:relative !important;',
				width:'93%'
			}]
		});
		this.add(middlePanel);
		//获取存放终端的容器
		var clientBox = middlePanel.query("#rightBox")[0];
		for(var i=0;i<clientList.length;i++){
			var rightPanel = this.creatRightPanel(clientList[i],middlePanel,i)
			clientBox.add(rightPanel);//将生成的终端panel放入容器中
		}
		clientBox.add(addImage);
		
	},
	/**
	 *  创建终端机panel
	 *  client:终端信息
     *  middlePanel:终端机行panel
     *  i:便于通过id获取panel
	 */
	creatRightPanel:function(client,middlePanel,i){
		var me = this;
		var Id = Ext.id(undefined,"client_");
		var clickHandler = function(event){
			event.stopPropagation();//阻止冒泡事件，上级的单击事件不会被调用
			if(this.component.flag==1){
				var parent = Ext.getCmp(this.id).ownerCt;
				if(this.dom.innerText == sc.setting.edit){//点击编辑，做相应操作
					this.dom.innerText = sc.setting.save;
					parent.query('component')[1].hide();
					parent.query('numberfield')[0].show();
				}else{//点击保存的相应操作
					var clientId = parent.ownerCt.ownerCt.config.items[0].html;
					this.dom.innerText = sc.setting.edit;
					var pageCount = parent.query('numberfield')[0].value;
					if(pageCount==0){
						parent.query('component')[1].setHtml('0');
						parent.query('component')[1].show();
						parent.query('numberfield')[0].hide();
					}else{
						parent.query('component')[1].setHtml(pageCount);
						parent.query('component')[1].show();
						parent.query('numberfield')[0].hide();
					}
					if(right.form.isValid()){
					var map = new HashMap();
					map.put("transType",'updateClient');
					map.put("pageCount",pageCount);
					map.put("clientId",clientId);
					Rpc({functionId:'SC000000003',async:false,success:function(){
							Ext.MessageBox.alert(sc.setting.promptmessage,sc.setting.saveright);  //提示信息，保存成功
					}},map);}
				}
			}
		};
		var delImg = Ext.create('Ext.Img', {
			src: "/components/homewidget/images/del.png",
			style:'cursor:pointer;',
			id:'del'+Id,
			width:15,
			height:15,
			hidden:true,
			cls:'delImg',//定位样式、该样式在下面手动引入
			listeners:{
				render:function(){
          			this.getEl().on('mouseover',function(){//this对象指向formpanel对象
          				this.show();
          			},this);
          			this.getEl().on('mouseout',function(){
          				this.hide();
          			},this);
          		},
          		click:{
					element:'el',
					fn:function(event){
						event.stopPropagation();//阻止冒泡事件，上级的单击事件不会被调用 
						var own = this.component.ownerCt.ownerCt;//获取终端机panel
						var clientId = own.query('component')[0].config.html;//得到当前终端机id
						var map = new HashMap();
						map.put("clientId",clientId);
						map.put("transType",'deleteClient');
						 Ext.Msg.show({
		                        title:"<div style='margin-left:5px'>"+sc.setting.promptmessage+"</div>",//提示信息
		                        msg: sc.setting.groupdeletebefore+client.name+sc.setting.deleteclient,//确定删除《》终端吗？
		                        buttons: Ext.Msg.YESNO,
		                        buttonText: {
		        						yes: sc.setting.ok,//确定
		        						no: sc.setting.cancel//取消
		    						},
		                        fn: function(btn){
		                           if(btn!='yes'){
		                        	   return;
		                           }
		                           Rpc({functionId:'SC000000003',async:false,success:function(res){
		                        		var parent = own.ownerCt;
										parent.remove(own);
										Ext.Array.remove(me.clientList,client);
								}},map);
		                        },
		                        icon: Ext.MessageBox.QUESTION
		                    });
					}
				}
			}
		});
		//终端机panel
		var right = Ext.create('Ext.form.Panel',{
			itemId:"right_"+i,
			flag:0,//勾选标识，便于实现终端机多选功能，1:勾选   0:不勾选
			layout:'hbox',
			width:230,
			height:80,
			style:'float:left;',
			margin:'10 0 0 10',
			items:[{
				xtype:"component",
				html:client.clientId,//终端机id
				hidden:true
			},{
				xtype:'panel',
				width:70,
				height:80,
				border:false,
				items:[{
					margin:'12 0 0 5',
					xtype:'image',
					src:'/module/serviceclient/images/clientIcon.png',//终端机图标
					width:55,
					listeners:{
						render:function(){
		          			this.getEl().on('mouseover',function(){//this对象指向formpanel对象
		          				var delImg = this.ownerCt.query('image')[1];
		          				delImg.show();
		          			},this);
		          			this.getEl().on('mouseout',function(){
		          				var delImg = this.ownerCt.query('image')[1];
		          				delImg.hide();
		          			},this);
		          		}
					}
				},delImg]
			},{
				xtype:'panel',
				layout:'vbox',
				border:false,
				height:80,
				width:160,
				margin:'0 0 0 0',
				items:[{
					xtype:'component',
					layout:'vbox',
					margin:'11 0 0 12',
					width:118,
					height:39,
					html:"<div style='width:100%;font-size:15px;word-break:break-all;'>"+client.name+"</div>"//终端机名称
				},{ 
					layout:'hbox',
					xtype:'panel',
					border:false,
					height:30,
					items:[{
						margin:'0 0 0 10',
						xtype:'component',
						labelAlign:'right',
						html:"<font style='font-size:15px'>"+sc.setting.papercount+"</font>"//剩余纸张
					},{
						xtype:'component',
						margin:'3 0 0 4',
						width:41,
						html:client.pageCount//剩余纸张的数量
					},{
						xtype:'numberfield',
						width:45,
						step:1,
						minValue:0,
						maxValue:500,
						decimalPrecision:1,
						hidden:true,
						allowBlank: false,
						allowDecimals:false,//禁用小数
						value:client.pageCount,
						validator:function(e){
							var re = /\D/; 
							var save = Ext.getCmp('component_edit'+Id);
							if(re.test(e)||e==''||e>500){
								save.flag=2;
								save.setStyle({color:'#c1c1d7',backgroundColor:'#ffffff'});
							}else{
								save.flag=1;
								save.setStyle({color:'#0099cc'});
								return true;
							}
						},
						listeners:{
							element:'el',
							click:function(event){
								event.stopPropagation();//阻止冒泡事件，上级的单击事件不会被调用 
							}
						}
					},{
						xtype:'label',
						margin:2,
						flag:1,
						style:'text-align:left;font-size:13px;color:#0099cc;cursor:pointer;',
						id:'component_edit'+Id,
						html:sc.setting.edit,//编辑
						listeners:{
							render:function(){
								this.getEl().on('click',clickHandler);
							}
						}
					}]
				},{
					xtype:'image',
					width:16,
					height:16,
					margin:"-77 0 0 140",
					hidden:true,
					src:'/module/serviceclient/images/strue.png'
				}]
			}],
			listeners:{
				render:function(){//当初始化进来的时候，默认只勾选第一台终端机
					var temp = this.itemId.substring(6,7);
					if(temp == 0){
						this.query('image')[2].show();
						this.config.flag = 1;
					}
					this.getEl().on('click',function(){//在组件渲染时绑定click事件
      					if(this.config.flag==1){//如果标识为1，则赋值为0，得到勾选图标并隐藏
      						this.config.flag=0;
		            		var image = this.query('image')[2];
		            		image.hide();
		            	}else{//否则赋值为1，得到勾选图标并显示
		            		this.config.flag=1;
		            		var image = this.query('image')[2];
		            		image.show();
		            	}
	            		//获取本类的对象
		            	var owner = Ext.getCmp('controlanalyse');
		            	Ext.Array.erase(owner.clientArray,0,owner.clientArray.length);//抹除clientId数组元素
		            	var clientItemidAll = middlePanel.query("#rightBox")[0].items.keys;
		        		var index = clientItemidAll.length-2;
		        		var lastItemid = clientItemidAll[index];
		        		var maxItemid = parseInt(lastItemid.substring(6));
		            	for(var i=0;i<=maxItemid;i++){
		            		var clientPanel = middlePanel.query("#rightBox")[0].query("#"+"right_"+i)[0];//获取终端机panel
		            		if(clientPanel && clientPanel.config.flag==1){//如果勾选则把当前panel存放的clientId放到数组中
		            			owner.clientArray.push(clientPanel.config.items[0].html);
		            		}
		            	}
		            	var vo = new HashMap();
						vo.put("clientId",owner.clientArray+'');
						vo.put("selectedServiceId",owner.services);
						vo.put("dateType",owner.dateType);
						owner.analyseKeyData = vo;
						owner.reloadAnalyse();
          			},this);
          		}
			}
		});
		return right
	},
	/**
	 * 创建分析区域
	 * 参数：
	 * serviceList:服务数据集合，详细格式参考设计文档
	 * analyseData:分析数据，详细格式参考设计文档
	 * 
	 * 当选中的服务发生变化、选中的日期类型发生变化，将数据保存到对象全局变量analyseKeyData中，并调用
	 * reloadAnalyse方法刷新页面分析数据
	 */
	createAnalyseArea:function(serviceList,analyseData){
		Ext.getCmp('controlanalyse_header').setStyle({
			borderStyle:'hidden hidden hidden solid'
		});
		var me = this;
		var store = new Ext.data.ArrayStore({
			fields:['id','displayText'],
			data:[[1,sc.setting.tendencychart],[2,sc.setting.optionsdetail]]
		});
		var gridConfig = Ext.decode(analyseData.gridConfig);
        gridConfig.beforeBuildComp = function(grid){
            grid.tableConfig.scrollable = false;
        };
		me.listGrid = new BuildTableObj(gridConfig);
		me.listGrid.mainPanel.height = 600;
		me.listGrid.mainPanel.width = 870;
//		me.listGrid.mainPanel.hidden = true;
		var bottomPanel = Ext.create('Ext.form.Panel',{
			layout:'vbox',
			width:'100%',
			border:false,
			margin:'20 0 0 0 ',
			items:[{
				xtype:'panel',
				layout:'hbox',
				border:false,
				style:'cursor:pointer;',
				margin:'0 0 0 20',
                width:102,
				items:[{
					xtype:'component',
					html:"<font style='font-size:14px'>"+sc.setting.allservice+"</font>"//全部服务
				},{
					xtype:'image',
					src:'/module/serviceclient/images/xl.png'
				}],
				listeners:{
					click:{
						scope:me,
						element:'el',
						fn:function(event){
							event.stopPropagation();//阻止大panel的点击事件
							var serviceAll = bottomPanel.query('panel')[0];
							if(serviceList.length==0){
								Ext.MessageBox.alert(sc.setting.promptmessage,sc.home.emptyselect);//提示信息  没有可选择的打印服务
							}else{
								var qstCzmx = Ext.getCmp('qstCzmx');
								var serviceSelect = Ext.getCmp('serviceSelect');
								if(qstCzmx){
									qstCzmx.close();
								}
								if(!serviceSelect){
									Ext.create("ServiceClient.serviceSetting.SelectService",{
										serviceList:serviceList,
										dateType:this.dateType,
										clientId:this.clientArray
									}).showBy(serviceAll,'tl-bl');
								}
							}
						}
					}
				}
			},{
				xtype:'panel',
				layout:'hbox',
				border:false,
				items:[{
					margin:'20 0 0 40',
					xtype:'component',
					html:"<font style='font-size:14px'>"+sc.setting.time+"</font>"
				},{
					margin:'20 0 0 30',
					xtype:'component',
					html:sc.setting.sevenday,
				    style: {
			    		color:'#ffffff',
			    		cursor:'pointer',
			    		backgroundColor:'#ffa64d'
			        },
					listeners:{
						click:{
							element:'el',
							fn:function(){
								for(var i=2;i<4;i++){
									var changeComopent = this.component.ownerCt.query('component')[i];
									changeComopent.setStyle({
										backgroundColor:'#ffffff',
										color:'#0099cc'
									});
								}
								this.setStyle({
									backgroundColor:'#ffa64d',
									color:'#ffffff'
								});
								me.dateType = 'week';
								var vo = new HashMap();
								vo.put("clientId",me.clientArray+'');
								vo.put("selectedServiceId",me.services);
								vo.put("dateType",me.dateType);
								me.analyseKeyData = vo;
								me.reloadAnalyse();
							}
						}
					}
				},{
					margin:'20 0 0 30',
					xtype:'component',
					html:sc.setting.onemonth,
					style: {
			    		color:'#0099cc',
			    		cursor:'pointer'
			        },
					listeners:{
						click:{
							element:'el',
							fn:function(){
								for(var i=1;i<4;i+=2){
									var changeComopent = this.component.ownerCt.query('component')[i];
									changeComopent.setStyle({
										backgroundColor:'#ffffff',
										color:'#0099cc'
									});
								}
								this.setStyle({
									backgroundColor:'#ffa64d',
									color:'#ffffff'
								});
								me.dateType = 'month';
								var vo = new HashMap();
								vo.put("clientId",me.clientArray+'');
								vo.put("selectedServiceId",me.services);
								vo.put("dateType",me.dateType);
								me.analyseKeyData = vo;
								me.reloadAnalyse();
							}
						}
					}
				},{
					margin:'20 0 0 30',
					xtype:'component',
					html:sc.setting.recentlyyear,
					style: {
						color:'#0099cc',
						cursor:'pointer'
					},
					listeners:{
						click:{
							element:'el',
							fn:function(){
								for(var i=1;i<3;i++){
									var changeComopent = this.component.ownerCt.query('component')[i];
									changeComopent.setStyle({
										backgroundColor:'#ffffff',
										color:'#0099cc'
									});
								}
								this.setStyle({
									backgroundColor:'#ffa64d',
									color:'#ffffff'
								});
								me.dateType = 'year';
								var vo = new HashMap();
								vo.put("clientId",me.clientArray+'');
								vo.put("selectedServiceId",me.services);
								vo.put("dateType",me.dateType);
								me.analyseKeyData = vo;
								me.reloadAnalyse();
							}
						}
					}
				}]
			},{
				xtype:'panel',
				layout:'hbox',
				border:false,
				style:'cursor:pointer;',
				margin:'20 0 0 40',
                width:100,
				items:[{
					xtype:'component',
					html:"<font style='font-size:14px;'>"+sc.setting.tendencychart+"</font>"//趋势图
				},{
					xtype:'component',
					html:"<font style='font-size:14px;'>"+sc.setting.optionsdetail+"</font>"//操作明细
				},{
					xtype:'image',
					src:'/module/serviceclient/images/xl.png'
				}],
				listeners:{
					render:function(){
						this.query('component')[1].hide();
					},
					click:{
						scope:me,
						element:'el',
						fn:function(event){
							event.stopPropagation();//阻止冒泡
							var qstCzmx = Ext.getCmp('qstCzmx');
							var chartContainer = Ext.getCmp("chartContainer");
							var listPanel = Ext.getCmp("optionsDetail_mainPanel");
							if(!qstCzmx){
								var qc = bottomPanel.query('panel')[2];
								var qstCzmx=new Ext.Panel({
									id:'qstCzmx',
									layout:'vbox',
									shadow : false,
									style:'cursor:pointer',
								    floating: true,//悬浮
								    width: 96,
								    height: 62,
								    items:[{
								    	xtype:'panel',
								    	height:30,
								    	width:'100%',
								    	border:false,
								    	html:"<div style='font-size:14px;padding-left:16px;padding-top:5px;width:100%;height:30px;color:#0099cc'>"+sc.setting.tendencychart+"</div>",//趋势图
								    	listeners:{
								    		render:function(){
								    			this.getEl().on('mouseover',function(){
								    				me.mouse(this,1);
								    			},this);
								    			this.getEl().on('mouseout',function(){
								    				me.mouse(this,2);
								    			},this);
								    		},
								    		click:{
								    			element:'el',
								    			fn:function(){
									    			qc.query('component')[0].show();
									    			qc.query('component')[1].hide();
													listPanel.hide();
													chartContainer.show();
													qstCzmx.close();
								    			}
								    		}
								    	}
								    },{
								    	xtype:'panel',
								    	height:30,
								    	width:'100%',
								    	border:false,
								    	html:"<div style='font-size:14px;padding-left:16px;padding-top:5px;width:100%;height:30px;color:#0099cc'>"+sc.setting.optionsdetail+"</div>",//操作明细
								    	listeners:{
								    		render:function(){
								    			this.getEl().on('mouseover',function(){
								    				me.mouse(this,1);
								    			},this);
								    			this.getEl().on('mouseout',function(){
								    				me.mouse(this,2);
								    			},this);
								    		},
								    		click:{
								    			element:'el',
								    			fn:function(){
								    				qc.query('component')[0].hide();
									    			qc.query('component')[1].show();
													chartContainer.hide();
									    			if(dataPanel.items.length==1){
									    			    dataPanel.add(listPanel);
									    			}
													listPanel.show();
													qstCzmx.close();
								    			}
							    			}
								    	}
								    }],
									renderTo: Ext.getBody()
								}).showBy(qc,'tc-bc');
							}
						}
					}
				}
			}]
		});
		var chartComponent ={
				xtype:'component',
				id:'chartContainer',
				hidden:false,
				width:'100%',
				height:450,
				border:false,
				listeners:{
					resize:function(){
						if(me.config.myChart){
							me.config.myChart.resize();
						}
					}
				}
		};
		var dataPanel = Ext.create('Ext.container.Container',{
			id:"dataPanel",
			margin:'25 0 0 50',
			width:'100%',
			border:false
		});
		
		this.add(bottomPanel);
		dataPanel.add(chartComponent);
//		dataPanel.add(me.listGrid.mainPanel);
		this.add(dataPanel);
		this.buildChartObject(analyseData.chartData);
		
	},
	/**
	 * 趋势图和操作明细的移进移出事件
	 */
	mouse:function(owner,flag){
		if(flag==1){
			owner.setBodyStyle({
				background:'#F1F1F1'
			});
		}else{
			owner.setBodyStyle({
				background:'white'
			});
		}
	},
	/**
	 * 复写样式，不影响总体Css
	 */
	createSelfCss : function(){
		Ext.util.CSS.createStyleSheet(".delImg{ position:relative !important; left:50px !important;top:-62px;}","delImg");
	},
	/**
	 * 当选中分析key有变化时，刷新分析数据
	 * 读取全局变量analyseKeyData，刷新数据
	 */
	reloadAnalyse:function(){
		var me = this;
		var analyseKeyData = this.analyseKeyData;
		//选中的终端id，多个逗号隔开
		var selectedClientId = analyseKeyData.clientId;
		//选中的服务id，多个逗号隔开
		var selectedServiceId = analyseKeyData.selectedServiceId;
		//选中的日期类型
		var selectedDateType = analyseKeyData.dateType;
		var vo = new HashMap();
		vo.put("transType","analyse");
		vo.put("printClients",selectedClientId);
		vo.put("printServices",selectedServiceId);
		vo.put("dateType",selectedDateType);
		/**调用交易类获取分析数据，然后刷新界面分析图表*/
		Rpc({functionId:'SC000000003',async:false,success:function(res){
			var resData = Ext.decode(res.responseText);
			var analyseData = resData.analyseData;
			var chartData = analyseData.chartData;
			me.buildChartObject(chartData);
			me.listGrid.tablePanel.getStore().load();
		}},vo);
	},
	/**
	 * 添加终端
	 */
	addClient:function(map,noseMap){
		var me = this;
		var middlePanel = Ext.getCmp('middleBox');
        var newIndex = '';
        var clientItemidAll = middlePanel.query("#rightBox")[0].items.keys;
        if(clientItemidAll.length>1){
            var index = clientItemidAll.length-2;
            var lastItemid = clientItemidAll[index];
            newIndex = parseInt(lastItemid.substring(6))+1;
        }else{
            newIndex = 0;
        }
		var cmp = me.creatRightPanel(map,middlePanel,newIndex);
		var clientBox = middlePanel.query("#rightBox")[0];
		clientBox.insert(clientBox.items.length-1,cmp);
		me.clientList.push(map);
		return true;
	},
	/**
	 * 构建折线图
	 * chartData:折线图中的数据
	 */
	buildChartObject:function(chartData){
		var option;
		option = {
			tooltip: {
		        trigger: 'axis',
				confine:true,
				position: function (point, params, dom, rect, size) {
					// 鼠标坐标和提示框位置的参考坐标系是：以外层div的左上角那一点为原点，x轴向右，y轴向下
					// 提示框位置
					var x = 0; // x坐标位置
					var y = 0; // y坐标位置

					// 当前鼠标位置
					var pointX = point[0];
					var pointY = Ext.getCmp('chartContainer').getHeight()-point[1];
					// 外层div大小
					// var viewWidth = size.viewSize[0];
					// var viewHeight = size.viewSize[1];

					// 提示框大小
					var boxWidth = size.contentSize[0];
					var boxHeight = size.contentSize[1];

					// boxWidth > pointX 说明鼠标左边放不下提示框
					if (boxWidth > pointX) {
						x = pointX;
					} else { // 左边放的下
						x = pointX - boxWidth;
					}

					// boxHeight > pointY 说明鼠标下边放不下提示框
					if (boxHeight > pointY) {
						y = pointY;
					} else { // 下边放得下
						y = point[1];
					}

					return [x, y];
				},
//		        textStyle : {
//		            color: '#ca8622'
//		        },
		        //backgroundColor:'rgba(255,255,255,1)',
		        //在这里设置
		        formatter:function(params){
		        	var res='';
		        	for(var i=0;i<params.length;i++){
			        		//res= res +"<font color='"+params[i].color+"'>"+params[i].seriesName+":"+params[i].value+"(次)</font><br />";
		        		res= res +params[i].seriesName+":"+params[i].value+sc.setting.Times+"<br />";//sc.setting.Times (次)
		        	}
		        	return res;
		        },
		        axisPointer: {
		            type: 'none'
		        }
		    },
		    grid: {
		        left: 20,
		        containLabel: true
		    },
			color:['#338DC9','#EE7541','#2BD62B','#DBDC26','#8FbC8B','#D2B48C','#DC648A','#21B2AA','#B0C4DE','#DDA0DD','#9C9AFF','#9C3164','#FFB248','#1fcf03','#005eaa','#339ca8','#d9b014','#32a487','#333333','#FFB6C1','#FF69B4','#D8BFD8','#DDA0DD','#FF00FF'],
			legend:{
				data:chartData.legend.data,
				icon:'circle',
				left: 20
			},
		    calculable: true,
		    xAxis: [
		        {
		            type: 'category',
		            boundaryGap: false,
		            data: chartData.xAxisData
		        }
		    ],
		    yAxis: [
		        {
		            type: 'value'
		        }
		    ],
		    series: chartData.seriesData
		};
		this.config.myChart = echarts.init(document.getElementById('chartContainer'));
		this.config.myChart.setOption(option,true);
	}
});