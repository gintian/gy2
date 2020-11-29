/**
 * hej add 2015/12/20
 * 盒式报表九宫格显示
 */
Ext.define("BoxReportURL.SodukuItem", {
	extend:"Ext.panel.Panel",
	requires:["EHR.extWidget.proxy.TransactionProxy"],
	casseid:'',
	tablepanel:undefined,
	viewport:undefined,
	tablestore:undefined,
	percentagecheck:undefined,
	enterflag:'',
	entertype:'',
	sodukusql:'',
	doctitle:'',
	analysis_interval:'',
	menuPanel:undefined,
	mainPanel:undefined,
	initComponent:function() {
		this.callParent();
		this.createMainPanel();
	},
	/**
	 * 创建主体部分
	 */
	createMainPanel:function(){
		var me = this;
		if(me.viewport!=''){
			var loading = new Ext.LoadMask({
	    		msg : '正在加载数据，请稍候......',
        		target : me.viewport
        }); 
        loading.show();
		}
		me.setLayout(Ext.create("Ext.layout.container.Border"));
		me.setBodyStyle('background-color:#ffffff');
		var datelist = [];
		var vo = new HashMap();
		vo.put('cassetteid',me.casseid);
		vo.put('sodukusql',me.sodukusql);
		vo.put('datelist',me.getDatelist());
		vo.put('flag','2');
		Rpc({functionId:'ZJ100000115',success:function(res){
				var resultObj = Ext.decode(res.responseText);
				var editmap = resultObj.editmap;
				var isempty = me.isEmptyObject(editmap);
				if(me.entertype=='detail'){
					if(isempty==true){
						loading.hide();
						Ext.Msg.alert("提示信息","盒式报表不存在，请检查门户定制设置！",function(){
					  			top.window.opener = top;
						        top.window.open('','_self','');
						        top.window.close();
				  		});
					}
					if(me.doctitle=='1'){
						document.title = editmap.cassette_name;
					}
				}
				var toolPanel = me.createToolpanel(editmap);
				//左侧
		    	me.menuPanel = me.createMenu(editmap);
    			//右侧
    			me.mainPanel = me.createMain(editmap);
    			me.add(toolPanel);
				me.add(me.menuPanel);
				me.add(me.mainPanel);
				if(me.viewport!=''){
					loading.hide();
				}
		},scope:this},vo);
	},
	/**
	 *创建头部panel
	 */
	createToolpanel:function(editmap){
		var me = this;
		var toolPanel = undefined;
		if(me.enterflag=='working'){
			toolPanel = Ext.widget('panel',{
				region: 'north',
				title:editmap.cassette_name,
				border:0,
				bodyPadding:0,
				bodyStyle:"border-left:none;",
				tools:[{type: 'close',
        				handler: function(){
        					me.viewport.removeAll(true);
							var configPanel = Ext.require('BoxReportURL.AddBoxReport', function(){
								var configPanel = Ext.create("BoxReportURL.AddBoxReport",{viewport:me.viewport,tablepanel:me.tablepanel,flag:1,editcasseid:me.casseid,tablestore:me.tablestore});
								me.viewport.add(configPanel);
        					})
        				}}]
			})
		}
		else if(me.enterflag=='analysis'){
			if(me.entertype=='detail'){
				toolPanel = Ext.widget('panel',{region: 'north',
					title:editmap.cassette_name,border:0,bodyPadding:0,bodyStyle:"border-left:none;"
				});
			}else{
				toolPanel = Ext.widget('panel',{
					region: 'north',
					title:editmap.cassette_name,
					border:0,
					bodyPadding:0,
					bodyStyle:"border-left:none;",
					tools:[{type: 'close',
        					handler: function(){
        						me.viewport.removeAll(true);
								me.viewport.add(me.tablepanel);
								me.tablestore.reload();
        					}}]
				});
			}
		};
		return toolPanel;
	},
	/**
	 * 创建左侧部分
	 */
	createMenu:function(editmap){
		var me = this;	
		me.menuPanel = Ext.widget('panel',{
			border:1,
			width:268,
			region:'west',
			//title:'显示方案',
			collapsible:true,
			resizable:false,
			autoScroll:true,
			header:false,
			collapseMode:'mini',
			split:true,
//			splite:true,
			bodyStyle:"border-left:none;border-top:none;",
			height:'100%',
    		layout:'vbox',
    		defaults:{
    			width:248
    		}
		});
		var panel = Ext.widget('container',{border:0,bodyPadding:0})
		me.menuPanel.add(panel);
		
		var treepanel = me.createCodeTree();
		//组织范围
		var orgflag = '';
		if(me.enterflag=='working'){
			orgflag = 'wflag';
		}else if(me.enterflag=='analysis'){
			orgflag = 'aflag';
		}
		var organPanel = Ext.widget('panel',{
					layout:'vbox',
					title:'组织范围',
//					padding:10,
					collapsed:true,
		    		collapsible:true,
					hideHeaders:true,
					border:0,
					height:280,
					autoScroll:true,
					flag:orgflag,
					items:treepanel,
					listeners:{
		    			beforeexpand:function(panel){
		    				var perlist = Ext.ComponentQuery.query("panel[flag='pers']");
							for(var i=0;i<perlist.length;i++){
								var perpanel = perlist[i];
								perpanel.collapse();
							}
		    			}
		    		}
				});
		me.menuPanel.items.get(0).insert(1,organPanel);
		//时间维度指标
		var dimension = editmap.time_dimension;
		//分析区间
		var analysis_interval = editmap.analysis_interval;
		var isempty = me.isEmptyObject(dimension);
		if(isempty==false){
			me.createTimepanel(analysis_interval);
		}
		
		//人员范围
		var personnel_range = editmap.personnel_range;
		if(personnel_range!=''){
			for(var i=0;i<personnel_range.length;i++){
				var range = personnel_range[i];
				var id = range.id;
				var name = range.name;
				var sflag = range.sflag;
				var slegend = range.slegend;
				var items = [];
				for(var j=0;j<slegend.length;j++){
					var flag = slegend[j].flag;
					var lexpr = slegend[j].lexpr;
					var legend = slegend[j].legend;
					var factor = slegend[j].factor;
					var infokind = slegend[j].infokind;
					var slecheckbox = Ext.widget('checkbox',{
						id:id+'_'+j,
						padding:'0 0 0 10',
						boxLabel:legend,
						lexpr:lexpr,
						factor:factor,
						infokind:infokind,
						flag:flag,
						sflag:sflag,
						name:id+'_'+j,
						listeners : {'change' : function(obj, ischecked) {
//								if(ischecked==true){
//									var cbgItem = this.ownerCt.items.items;
//									for(var k=0;k<cbgItem.length;k++){
//										if(cbgItem[k].name==obj.name){
//										}else{
//										cbgItem[k].setValue(false);
//										}
//										}
//								}
							var datelist = [];
							me.queryperson(datelist,0,'');
							}}
						})
						items.push(slecheckbox);
				}
				var personPanel = Ext.widget('panel',{
					layout:'vbox',
					title:name,
//					padding:10,
					collapsed:true,
		    		collapsible:true,
					hideHeaders:true,
					border:0,
					flag:'pers',
					leg:id,
					items:items,
					listeners:{
		    			beforeexpand:function(panel){
		    				var orgflag = '';
							if(me.enterflag=='working'){
								orgflag = 'wflag';
							}else if(me.enterflag=='analysis'){
								orgflag = 'aflag';
							}
		    				var orgpanel = Ext.ComponentQuery.query("panel[flag='"+orgflag+"']")[0];
		    				orgpanel.collapse();
		    				var perlist = Ext.ComponentQuery.query("panel[flag='pers']");
							for(var i=0;i<perlist.length;i++){
								var perpanel = perlist[i];
								if(perpanel.collapsed==false){
									perpanel.collapse();
								}
							}
		    			}
		    		}
				})
				me.menuPanel.items.get(0).add(personPanel);
			}
		}
		return me.menuPanel;
	},
	/**
	 * 右侧panel
	 */
	createMain:function(editmap){
		var me = this;
		var sodukulist = editmap.sodukulist;//九宫格数据
		var latlist = editmap.latlist;//横向坐标
		var longlist = editmap.longlist;//纵向坐标
		var lateral_desc = editmap.lateral_desc;
		var longitudinal_desc = editmap.longitudinal_desc;
		var percentage = editmap.percentage;
		var staff_view_url = editmap.staff_view_url;
		var staff_listview_url = editmap.staff_listview_url;
		var rightnum = 0;
		var scale = '0%';
		var subtitle = '添加标题';
		var list = [];
		var s = undefined;
		var titlelabel = undefined;
		if(longitudinal_desc.length>longlist.length*7){
			longitudinal_desc = longitudinal_desc.substring(0,longlist.length*7);
		}
		list.push({xtype:'container',width:40,rowspan: longlist.length+1,items:[{xtype:'label',
						html:'<div style="width:20px;padding-left:7px;word-break:break-all;text-algin:left;font-size:20px !important;">'+longitudinal_desc+'</div>'
						}]});
		for(var i=0;i<longlist.length;i++){
			var longid = longlist[i].codeitemid;
			var labelx = Ext.widget('container',{width:40,items:[{
				xtype:'label',
				html:'<div style="width:30px;padding-right:10px;word-break:break-all;text-algin:left;" >'+longlist[i].codeitemdesc+'</div>'
			}]});	
			list.push(labelx);
			var liney = undefined;
			if(i==0){
				liney = Ext.widget('container',{width:10,height:212,padding:'5 0 0 0',layout:{type:'vbox',align:'center',pack:'center'},
				items:[{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/jiantou.png',height:11,width:10},
					{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/xian.png',height:204,width:2}]
			});
			}else{
				liney = Ext.widget('container',{width:10,height:212,layout:{type:'vbox',align:'center',pack:'center'},
				items:[{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/xian.png',height:212,width:2}]
			});
			}
				
			list.push(liney);
			var title = '添加标题';
			
			for(var j=0;j<latlist.length;j++){
				//subtitle = "";
				var latid = latlist[j].codeitemid;
				var xy = latid+'_'+longid;
				if(sodukulist.length>0){
					var num = sodukulist[sodukulist.length-2];
					me.sodukusql = sodukulist[sodukulist.length-1];
					for(var n=0;n<sodukulist.length;n++){
						var soduku = sodukulist[n];
						var pxy = soduku.xy;
						
						if(pxy==xy){
							var peoplearr = soduku.xylist;
							if(peoplearr.length==0){
								rightnum = 0+'';
							}else{
								rightnum = peoplearr.length;
							}
							if(soduku.title==''){
								title='添加标题';
								if(me.entertype=='detail'){
									title = '';
								}else if(me.enterflag=='analysis'){
									title = '';
								}
								subtitle = title;
							}else{
								title = soduku.title;
								var regExp = /^[a-z0-9]+$/i;
								if(regExp.test(title)==true){
									if(title.length>20){
										subtitle = title.substring(0,20);
									}else{
										subtitle = title;
									}
								}else{
									if(title.length>13){
										subtitle = title.substring(0,13);
									}else{
										subtitle = title;
									}
								}
							}
							}
					}
					if(num!='0'){
						num = parseFloat(num);
						rightnum = parseFloat(rightnum); 
						scale = 0 ? "0%" : (Math.round(rightnum / num * 10000) / 100.00 + "%");//判断是否是NaN
					}else{
						rightnum='0';
						scale = '0%';
					}
				}else{
					if(me.entertype=='detail'){
						title = subtitle ='';
					}else if(me.enterflag=='analysis'){
						title = subtitle ='';
					}
				}
				
				rightnum = rightnum+'';
				scale = scale+'';
				if(percentage=='1'){//选中
					if(me.enterflag=='working'){
						if(rightnum>6){
							s = Ext.widget('panel',{
							id:'panel_'+xy,
						    header:{xtype:'panel',id:'title_'+xy,
									border:0,
									height:22,
									width:252,
									bodyStyle: 'background-color:#F5F5F5',
									layout:'hbox',
									items:[{xtype:'box',id:'lab_'+xy,padding:'0 0 0 5',width:170,titles:title,
										html:'<div title='+title+'>'+subtitle+'</div>',style : {cursor : 'pointer',overflow:'auto',color:'#1b4a98 !important'},listeners : {
													render : function() {
													var hi = this;
													this.getEl().on("click", function() {
														me.editTitle(hi,hi.ownerCt,hi.id);
													})}}},{xtype:'tbfill'},{xtype:'box',padding:'0 5 0 0',id:'num_'+xy,html:'<div title=查看更多>'+rightnum+'('+scale+')</div>',
													style:{cursor : 'pointer',color:'#1b4a98 !important'},listeners : {
													render : function() {
													var hi = this;
													this.getEl().on("click", function() {
															me.showimageWin(hi,hi.ownerCt.ownerCt,hi.id,sodukulist,staff_view_url);
													})}}}]
								},
						    padding:10,
						    border:1,
						    width:274,
						    height:212,
							layout:{type:'table',
							tdAttrs:{
	    			   			width:85
	    					},
							columns: 3}
						});
						}else{
						s = Ext.widget('panel',{
							id:'panel_'+xy,
						    header:{xtype:'panel',id:'title_'+xy,
									border:0,
									height:22,
									bodyStyle: 'background-color:#F5F5F5',
									layout:'hbox',
									items:[{xtype:'box',id:'lab_'+xy,padding:'0 0 0 5',width:170,titles:title,
										html:'<div title='+title+'>'+subtitle+'</div>',style : {cursor : 'pointer',color:'#1b4a98 !important'},listeners : {
													render : function() {
													var hi = this;
													this.getEl().on("click", function() {
														me.editTitle(hi,hi.ownerCt,hi.id);
													})}}},{xtype:'tbfill'},{xtype:'label',padding:'0 5 0 0',id:'num_'+xy,
													html:rightnum+'('+scale+')',style:{color:'#1b4a98 !important'}}]
								},
						    padding:10,
						    border:1,
						    width:274,
						    height:212,
							layout:{type:'table',
							tdAttrs:{
	    			   			width:85
	    					},
							columns: 3}
						});
						}
						
					}
					else if(me.enterflag=='analysis'){
						if(rightnum>6){
							s = Ext.widget('panel',{
							id:'panel_'+xy,
						    header:{xtype:'panel',id:'title_'+xy,
									border:0,
									height:22,
									bodyStyle: 'background-color:#F5F5F5',
									layout:'hbox',
									items:[{xtype:'label',id:'lab_'+xy,padding:'0 0 0 5',
										html:'<div title='+title+'>'+subtitle+'</div>',style : {color:'#1b4a98 !important'}},
										{xtype:'tbfill'},{xtype:'box',id:'num_'+xy,html:'<div title=查看更多>'+rightnum+'('+scale+')</div>',padding:'0 5 0 0',
										style:{cursor : 'pointer',color:'#1b4a98 !important'},listeners : {
													render : function() {
													var hi = this;
													this.getEl().on("click", function() {
															me.showimageWin(hi,hi.ownerCt.ownerCt,hi.id,sodukulist,staff_view_url);
													})}}}]
								},
						    padding:8,
						    border:1,
						    width:274,
						    height:212,
							layout:{type:'table',
							tdAttrs:{
	    			   			width:85
	    					},
							columns: 3}
						});
						}else{
							s = Ext.widget('panel',{
							id:'panel_'+xy,
						    header:{xtype:'panel',id:'title_'+xy,
									border:0,
									height:22,
									bodyStyle: 'background-color:#F5F5F5',
									layout:'hbox',
									items:[{xtype:'label',id:'lab_'+xy,padding:'0 0 0 5',
										html:'<div title='+title+'>'+subtitle+'</div>',style : {color:'#1b4a98 !important'}},
										{xtype:'tbfill'},{xtype:'label',id:'num_'+xy,
										html:rightnum+'('+scale+')',style:{color:'#1b4a98 !important'},padding:'0 5 0 0'}
										]
								},
						    padding:8,
						    border:1,
						    width:274,
						    height:212,
							layout:{type:'table',
							tdAttrs:{
	    			   			width:85
	    					},
							columns: 3}
						});
						}
						
					}
				}else{
					if(me.enterflag=='working'){
						if(rightnum>6){
							s = Ext.widget('panel',{
								id:'panel_'+xy,
							    header:{xtype:'panel',id:'title_'+xy,
										border:0,
										height:22,
										bodyStyle: 'background-color:#F5F5F5',
										layout:'hbox',
										items:[{xtype:'box',id:'lab_'+xy,padding:'0 0 0 5',width:180,titles:title,
										html:'<div title='+title+'>'+subtitle+'</div>',style : {cursor : 'pointer',color:'#1b4a98 !important'},listeners : {
														render : function() {
														var hi = this;
														this.getEl().on("click", function() {
															me.editTitle(hi,hi.ownerCt,hi.id);
														})}}},{xtype:'tbfill'},{xtype:'box',id:'num_'+xy,html:'<div title=查看更多>'+rightnum+'</div>',
														style:{cursor : 'pointer',color:'#1b4a98 !important'},listeners : {
														render : function() {
														var hi = this;
														this.getEl().on("click", function() {
																me.showimageWin(hi,hi.ownerCt.ownerCt,hi.id,sodukulist,staff_view_url);
														})}},padding:'0 5 0 0'}]
									},
							    padding:10,
							    border:1,
							    width:274,
						    	height:212,
								layout:{type:'table',
								tdAttrs:{
		    			   			width:85
		    					},
								columns: 3}
							});
						}else{
						s = Ext.widget('panel',{
								id:'panel_'+xy,
							    header:{xtype:'panel',id:'title_'+xy,
										border:0,
										height:22,
										bodyStyle: 'background-color:#F5F5F5',
										layout:'hbox',
										items:[{xtype:'box',id:'lab_'+xy,padding:'0 0 0 5',width:180,titles:title,
										html:'<div title='+title+'>'+subtitle+'</div>',style : {cursor : 'pointer',color:'#1b4a98 !important'},listeners : {
														render : function() {
														var hi = this;
														this.getEl().on("click", function() {
															me.editTitle(hi,hi.ownerCt,hi.id);
														})}}},{xtype:'tbfill'},{xtype:'label',id:'num_'+xy,html:rightnum,
														style:{color:'#1b4a98 !important'},padding:'0 5 0 0'}]
									},
							    padding:10,
							    border:1,
							    width:274,
						    	height:212,
								layout:{type:'table',
								tdAttrs:{
		    			   			width:85
		    					},
								columns: 3}
							});
						}
						
					}
					else if(me.enterflag=='analysis'){
						if(rightnum>6){
							s = Ext.widget('panel',{
							id:'panel_'+xy,
						    header:{xtype:'panel',id:'title_'+xy,
									border:0,
									height:22,
									bodyStyle: 'background-color:#F5F5F5',
									layout:'hbox',
									items:[{xtype:'label',id:'lab_'+xy,padding:'0 0 0 5',
										html:'<div title='+title+'>'+subtitle+'</div>',style : {color:'#1b4a98 !important'}},
										{xtype:'tbfill'},{xtype:'box',id:'num_'+xy,html:'<div title=查看更多>'+rightnum+'</div>',
										style:{cursor : 'pointer',color:'#1b4a98 !important'},listeners : {
													render : function() {
													var hi = this;
													this.getEl().on("click", function() {
															me.showimageWin(hi,hi.ownerCt.ownerCt,hi.id,sodukulist,staff_view_url);
												})}},padding:'0 5 0 0'}]
								},
						    padding:10,
						    border:1,
						    width:274,
						    height:212,
							layout:{type:'table',
							tdAttrs:{
	    			   			width:85
	    					},
							columns: 3}
						});
						}else{
							s = Ext.widget('panel',{
							id:'panel_'+xy,
						    header:{xtype:'panel',id:'title_'+xy,
									border:0,
									height:22,
									bodyStyle: 'background-color:#F5F5F5',
									layout:'hbox',
									items:[{xtype:'label',id:'lab_'+xy,padding:'0 0 0 5',
										html:'<div title='+title+'>'+subtitle+'</div>',style : {color:'#1b4a98 !important'}},
										{xtype:'tbfill'},{xtype:'label',id:'num_'+xy,html:rightnum,style:{color:'#1b4a98 !important'},padding:'0 5 0 0'}]
								},
						    padding:10,
						    border:1,
						    width:274,
						    height:212,
							layout:{type:'table',
							tdAttrs:{
	    			   			width:85
	    					},
							columns: 3}
						});
						}
						
					}
				}
				for(var k=0;k<sodukulist.length;k++){
					var soduku = sodukulist[k];
					var pxy = soduku.xy;
					if(pxy==xy){
						var peoplenum = 0;
						var peoplearr = soduku.xylist;
						if(peoplearr.length>0){
							peoplenum = peoplearr.length;
							if(peoplenum>6){
								peoplenum = 6;
							}
						for(var m=0;m<peoplenum;m++){
							var a0100 = peoplearr[m][0];//没有加密的编号
							var pubfa0100 = peoplearr[m][1];//用于照片展现 PubFunc.encrypt加密
							var carda0100 = peoplearr[m][2];//用于登记表展现
							var nbase = peoplearr[m][3];//没有加密的人员库前缀
							var pubfnbase = peoplearr[m][4];//加密的人员库前缀PubFunc.encrypt加密
							var imzgetitle = '';
							var a0101 = peoplearr[m][5];
							var e0122 = peoplearr[m][6];
							var e01a1 = peoplearr[m][7];
							var fileid = peoplearr[m][8];
							//xus 20/4/22 vfs改造 获取人员照片
							var personPhoto ='url(/images/photo.jpg);background-size:100% 100%;cursor:pointer;';
							if(fileid != ''){
								personPhoto = 'url(/servlet/vfsservlet?fileid='+fileid+'&imageResize='+$URL.encode("55`55")+');background-size:100% 100%;cursor:pointer;';
							}
							if(e0122!=null&&e0122!=''){
								imzgetitle += e0122+'/';
							}
							if(e01a1!=null&&e01a1!=''){
								imzgetitle += e01a1+'/';
							}
							if(a0101!=null&&a0101!=''){
								imzgetitle += a0101+'/';
							}
							if(imzgetitle!=''){
								var index = imzgetitle.lastIndexOf('/');
								imzgetitle = imzgetitle.substring(0,index);
							}
							var image = Ext.widget('container',{
								margin:'5 10 0 10',
								height:75,
								width:65,
								//layout:{type:'vbox',align:'center',pack:'center'},
								items:[{xtype:'image',title:imzgetitle,
								a0100:a0100,
								pubfa0100:pubfa0100,
								carda0100:carda0100,
								nbase:nbase,
								pubfnbase:pubfnbase,
								height:55,width:55,margin:'0 0 0 5',
								style:{
//									backgroundImage:'url(/servlet/DisplayOleContent?nbase='+pubfnbase+'&a0100='+pubfa0100+'&imageResize='+$URL.encode("55`55")+'&caseNullImg=/images/photo.jpg);background-size:100% 100%;cursor:pointer;'
									backgroundImage:personPhoto
								},//border-radius: 50%;
								src:rootPath+'/module/system/personalsoduku/images/55.png',listeners:{render:function(){
							    var hi = this;
										this.getEl().on("click", function() {
												if(staff_view_url!=null&&staff_view_url!=''){
													if(staff_view_url.indexOf('&')!=-1){
														if(staff_view_url.indexOf('/synthesiscard')!=-1){//登记表
															var perURL = staff_view_url+'&userbase='+$URL.encode(hi.nbase)+'&a0100='+$URL.encode(hi.carda0100);
														}else{
															var perURL = staff_view_url+'&userbase='+$URL.encode(hi.nbase)+'&a0100='+$URL.encode(hi.pubfa0100);
														}
													}else{
														if(staff_view_url.indexOf('/synthesiscard')!=-1){
															var perURL = staff_view_url+'?userbase='+$URL.encode(hi.nbase)+'&a0100='+$URL.encode(hi.carda0100);
														}else{
															var perURL = staff_view_url+'?userbase='+$URL.encode(hi.nbase)+'&a0100='+$URL.encode(hi.pubfa0100);
														}
													}
													window.open(perURL,"_blank","left=0,top=0,width="+(screen.availWidth-10)+",height="+(screen.availHeight-40)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
												}else{
													Ext.Msg.alert("提示信息","请填写个人信息URL!");
												}
								}, this);
							}}
							},{xtype:'label',html:'<div style="font-size:12px !important;text-align:center;">'+a0101+'</div>'}]});
							s.add(image);
						}
						}
					}
				}
				list.push(s);
			}
		}
		var labelempty = Ext.widget('container',{width:20,
						html:'&nbsp;&nbsp;&nbsp;'
					});
		list.push(labelempty);
		list.push({xtype:'container',width:20,padding:'0 0 0 0',layout:{type:'hbox',align:'middle',pack:'center'},
		items:[{xtype:'tbfill'},{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/lianjie.png',height:30,width:20}]
		});
		
		var linex  =undefined;
		for(var q=0;q<latlist.length;q++){
		if(q==latlist.length-1){
			linex = Ext.widget('container',{width:280,height:20,padding:'0 0 0 0',layout:{type:'hbox',align:'middle',pack:'center'},
			items:[{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/xian-h.png',height:2,width:272},
				{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/jiantou-h.png',height:10,width:8}]
		});
		}else{
			linex = Ext.widget('container',{width:280,height:20,padding:'0 0 0 0',layout:{type:'hbox',align:'middle',pack:'center'},
			items:[{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/xian-h.png',height:2,width:280}]
		});
		}
			
		list.push(linex);
		}
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		for(var p=0;p<latlist.length;p++){
			var labely = Ext.widget('container',{padding:10,width:280,layout:{type:'hbox',align:'middle',pack:'center'},items:[{xtype:'label',html:'<p><span style="text-align:center">'+latlist[p].codeitemdesc+'</span><p>'
		}]});	
			list.push(labely);
		}
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		if(lateral_desc.length>latlist.length*12){
			lateral_desc = lateral_desc.substring(0,latlist.length*12);
		}
		list.push({xtype:'container',width:(latlist.length)*280,colspan: latlist.length+2,layout:{type:'hbox',align:'middle',pack:'center'},items:[{xtype:'label',style:'font-size:20px !important;',text:lateral_desc}]});

		me.mainPanel = Ext.widget('panel',{
    		region:'center',
    		autoScroll:true,
    		height:'100%',
    		flex:1,
    		bodyStyle:"border-top:none;",
    		layout:{
    			type:'table',
//		    	tdAttrs:{
//		    		align:'center'
//		    	},
    			columns: latlist.length+3
    		},
    		items:list,
    		listeners: {
				  render: function(p){
					p.body.on('scroll', function(){
						var d=p.body.dom;
						var top = d.scrollTop;
						var items = p.items.items;
						for(var i=0;i<items.length;i++){
							var panel = items[i];
							var id = panel.getId();
							if(id.indexOf('panel_')!=-1){
								var xy = id.substring(6,id.length);
								var winlist = Ext.ComponentQuery.query("window[flag='win']");
								if(winlist.length>0){
								for(var i=0;i<winlist.length;i++){
									var win = winlist[i];
									win.close();
								}
							}
							}
						}
				}, p);}}
		});
		return me.mainPanel;
		
	},
	/**
	 * 添加和修改标题
	 * @param {} obj
	 * @param {} aa
	 * @param {} xy
	 */
	editTitle:function(obj,aa,xy){
		var me = this;
		var title = obj.titles;
		var text = Ext.widget('textfield', {
				value : title,
				padding:'0 0 0 5',
				listeners : {
					change:function(obj){
						var value = obj.getValue();
						if(value.indexOf(" ")!=-1){
							value = value.replace(" ", "");
							obj.setValue(value);
						}
					},
					blur : function() {
						var titleText = this.value;
						var subtitleText = '';
						if(titleText!=''){
							titleText = titleText.replace(/^\s+|\s+$/g, "");
							var regExp = /^[a-z0-9]+$/i;
							if(regExp.test(titleText)==true){
								if(titleText.length>20){
									subtitleText = titleText.substring(0,20);
								}else{
									subtitleText = titleText;
								}
							}else{
							if(titleText.length>13){
								subtitleText = titleText.substring(0,13);
							}else{
								subtitleText = titleText;
							}
							}
							obj.update('<div title='+titleText+'>'+subtitleText+'</div>');
							obj.titles = titleText;
							obj.setVisible(true);
							this.setVisible(false);
							var vo = new HashMap();
							vo.put('cassetteid',me.casseid);
							vo.put('xy',xy.substring(4,xy.length));
							vo.put('desc',titleText);
							Rpc({functionId:'ZJ100000116',success:function(res){
									var resultObj = Ext.decode(res.responseText);
									var editmap = resultObj.editmap;
							},scope:this},vo); 
						}else{
							Ext.Msg.alert("提示信息","请填写标题!");
						}
					},
				specialkey : function(field, e) {
                	if (e.getKey() == e.ENTER) { //触发了listener后，如果按回车，执行相应的方法
                 		var titleText = this.value;
						var subtitleText = '';
						if(titleText!=''){
							titleText = titleText.replace(/^\s+|\s+$/g, "");
							var regExp = /^[a-z0-9]+$/i;
							if(regExp.test(titleText)==true){
								if(titleText.length>20){
									subtitleText = titleText.substring(0,20);
								}else{
									subtitleText = titleText;
								}
							}else{
							if(titleText.length>13){
								subtitleText = titleText.substring(0,13);
							}else{
								subtitleText = titleText;
							}
							}
							obj.update('<div title='+titleText+'>'+subtitleText+'</div>');
							obj.setVisible(true);
							obj.titles = titleText;
							this.setVisible(false);
							var vo = new HashMap();
							vo.put('cassetteid',me.casseid);
							vo.put('xy',xy.substring(4,xy.length));
							vo.put('desc',titleText);
							Rpc({functionId:'ZJ100000116',success:function(res){
									var resultObj = Ext.decode(res.responseText);
									var editmap = resultObj.editmap;
							},scope:this},vo);
						}else{
							Ext.Msg.alert("提示信息","请填写标题!");
						}
                		}
            		}
				}
			});
		obj.setVisible(false);
		aa.insert(0,text);
		text.focus();
	},
	/**
	 * 更多图片展示
	 * @param {} obj
	 * @param {} aa
	 * @param {} xy
	 * @param {} list
	 * @param {} nbase
	 */
	showimageWin:function(obj,aa,xy,list,staff_view_url){
		var me = this;
		xy = xy.substring(4,xy.length);
		var items=[];
		var num=0;
		for(var j=0;j<list.length;j++){
			var soduku = list[j];
			var pxy = soduku.xy;
			if(pxy==xy){
				var xylist = soduku.xylist;
				var number = xylist.length;
				if(xylist.length>6){
					items.push({xtype:'container',width:55,height:95,
					layout:{type:'hbox',align:'middle',pack:'center'},items:[{xtype:'image',
									style : {cursor : 'pointer'},title:'上一张',margin:'0 10 0 10',src:rootPath+'/images/new_module/left.png',listeners : {
									render : function() {
										this.getEl().on('click', function() {
												var h = this;
												var panel = h.ownerCt.ownerCt.items.get(1);
												var items = h.ownerCt.ownerCt.items.get(1).items.items;//所有的
												if(num==0){
													return;
												}
												panel.insert(0,panelitems[num-1]);
												panel.remove(panelitems[num+5],false);
                                                num--;
										}, this);
									}
								}}]});
					var panelitems = [];
					for(var m=0;m<xylist.length;m++){
						var a0100 = xylist[m][0];//没有加密的编号
						var pubfa0100 = xylist[m][1];//用于照片展现 PubFunc.encrypt加密
						var carda0100 = xylist[m][2];//用于登记表展现
						var nbase = xylist[m][3];//没有加密的人员库前缀
						var pubfnbase = xylist[m][4];//加密的人员库前缀PubFunc.encrypt加密
						var imzgetitle = '';
						var a0101 = xylist[m][5];
						var e0122 = xylist[m][6];
						var e01a1 = xylist[m][7];
						var fileid = xylist[m][8];
						//xus 20/4/22 vfs改造 获取人员照片
						var personPhoto ='url(/images/photo.jpg);background-size:100% 100%;cursor:pointer;';
						if(fileid != ''){
							personPhoto = 'url(/servlet/vfsservlet?fileid='+fileid+'&imageResize='+$URL.encode("65`65")+');background-size:100% 100%;cursor:pointer;';
						}
						if(e0122!=null&&e0122!=''){
								imzgetitle += e0122+'/';
							}
						if(e01a1!=null&&e01a1!=''){
							imzgetitle += e01a1+'/';
						}
						if(a0101!=null&&a0101!=''){
							imzgetitle += a0101+'/';
						}
						if(imzgetitle!=''){
							var index = imzgetitle.lastIndexOf('/');
							imzgetitle = imzgetitle.substring(0,index);
						}
						var image = Ext.widget('container',{
							margin:'0 5 0 5',
							height:85,
							width:65,
							items:[{xtype:'image',
							title:imzgetitle,a0100:a0100,pubfa0100:pubfa0100,carda0100:carda0100,nbase:nbase,
							pubfnbase:pubfnbase,height:65,width:65,
							style:{
//									backgroundImage:'url(/servlet/DisplayOleContent?nbase='+pubfnbase+'&a0100='+pubfa0100+'&imageResize='+$URL.encode("65`65")+'&caseNullImg=/images/photo.jpg);background-size:100% 100%;cursor:pointer;'
									backgroundImage:personPhoto
								},//border-radius: 50%;
								src:rootPath+'/module/system/personalsoduku/images/65.png',
							listeners:{render:function(){
							    var hi = this;
								this.getEl().on("click", function() {
												if(staff_view_url!=null&&staff_view_url!=''){
													if(staff_view_url.indexOf('&')!=-1){
														if(staff_view_url.indexOf('/synthesiscard')!=-1){//登记表
															var perURL = staff_view_url+'&userbase='+$URL.encode(hi.nbase)+'&a0100='+$URL.encode(hi.carda0100);
														}else{
															var perURL = staff_view_url+'&userbase='+$URL.encode(hi.nbase)+'&a0100='+$URL.encode(hi.pubfa0100);
														}
													}else{
														if(staff_view_url.indexOf('/synthesiscard')!=-1){
															var perURL = staff_view_url+'?userbase='+$URL.encode(hi.nbase)+'&a0100='+$URL.encode(hi.carda0100);
														}else{
															var perURL = staff_view_url+'?&userbase='+$URL.encode(hi.nbase)+'&a0100='+$URL.encode(hi.pubfa0100);
														}
													}
													window.open(perURL,"_blank","left=0,top=0,width="+(screen.availWidth-10)+",height="+(screen.availHeight-40)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
												}else{
													Ext.Msg.alert("提示信息","请填写个人信息URL!");
												}
								}, this);
							}}
						},{xtype:'label',html:'<div style="font-size:12px !important;text-align:center;">'+a0101+'</div>'}]});
						panelitems.push(image);
					}
					var showitems=[];
					for(var i=0;i<6;i++){
						showitems.push(panelitems[i]);
					}
					var panel = Ext.widget('container',{
						header:false,
						layout:{type:'hbox',align:'middle'},
						width:450,
						height:95,
						items:showitems
					});
					items.push(panel);
					items.push({xtype:'container',width:55,height:95,
					layout:{type:'hbox',align:'middle',pack:'center'},items:[{xtype:'image',style : {cursor : 'pointer'},title:'下一张',margin:'0 10 0 10',src:rootPath+'/images/new_module/right.png',listeners : {
									render : function() {
										this.getEl().on('click', function() {
												var h = this;
												var panel = h.ownerCt.ownerCt.items.get(1);
												var items = h.ownerCt.ownerCt.items.get(1).items.items;//所有的
												if(num==panelitems.length-6){
													return;
												}
													panel.add(panelitems[num+6]);
                                                    panel.remove(panelitems[num],false);
                                                    num++;
										}, this);
									}
								}}]});
					}
			}
		}
		var imageWin=Ext.widget('window',{
			flag:'win',
			x:aa.getX()-140,
			y:aa.getY()+205,
			header:false,
			width:570,
			height:100,
//			padding:10,
			layout:{type:'hbox',align:'middle',pack:'center'},
			items:items,
			listeners:{
					render:function(){
						this.mon(Ext.getDoc(), {
			                mousewheel: this.hiddenIf,
			                mousedown: this.hiddenIf,
			                scope: this
			            });
					}
				},
			hiddenIf: function(e) {
		        var me = this;
		        if (!me.isDestroyed && !e.within(me.bodyEl, false, true) && !me.owns(e.target)) {
		        	me.close();
		        }
		    }
		}).show();
 
	},
	/**
	 * 创建选择树
	 * @param {} itemid
	 * @param {} codesetid
	 */
	createCodeTree:function(){
		var me= this;
		var codestore = Ext.create('Ext.data.TreeStore',{
	    	fields: ['text','id','codesetid'], 
	        proxy: Ext.create("EHR.extWidget.proxy.TransactionProxy",{   
	            extraParams:{
	            	codesetid  : 'UM',
	            	codesource : '',
	            	nmodule    : '4',
	            	ctrltype   : '3',
	            	parentid   : '',
	            	searchtext : encodeURI(""),
	            	multiple:true
	            },
	            functionId:'ZJ100000131'
	        })
	    });
		var selector = Ext.widget('treepanel',{
			id:'codetree',
			bodyStyle:'border-top:none',
			border:0,
	    	width:'100%',
	    	store:codestore,
	    	rootVisible: false,
			listeners:{
    			checkchange:function(node,checked, eOpts){
    				var datelist = [];
    				if(checked==true){
    					me.queryperson(datelist,0,'');
    				}
    				else{
    					me.queryperson(datelist,0,'');
    				}
    			}
    		}
	    });
		return selector;
	},
	/**
	 * 组装筛选条件
	 * @param {} datelist
	 * @param {} flag
	 */
	queryperson:function(datelist,flag,cancleflag){
		var me = this;
		if(flag==0){
			var data_picker = Ext.getCmp('data_picker');
			if(data_picker!=undefined){
				var checkflag = data_picker.checkflag;
				if(checkflag==false){//刚进来或者没有选择
					if(cancleflag=='C')
						datelist = [];
					else
						datelist = me.getDatelist();
				}else{//选择过 拼接
					var year = '';
					var quarter='';
					var month = '';
					var dataValue = data_picker.getValue();
					if(dataValue.indexOf('年')!=-1){
						year = dataValue.substring(0,dataValue.indexOf('年'));
					}
					if(dataValue.indexOf('年')!=-1&&dataValue.indexOf('季度')!=-1){
						quarter = dataValue.substring(dataValue.indexOf('年')+2,dataValue.indexOf('季度'));
					}
					if(dataValue.indexOf('年')!=-1&&dataValue.indexOf('月')!=-1){
						month = dataValue.substring(dataValue.indexOf('年')+2,dataValue.indexOf('月'));
					}
					datelist.push({year:year});
	        		datelist.push({quarter:quarter});
	        		datelist.push({month:month});
				}
			}
		}
		var personlist = [];
		//获得人员范围勾选项
		var panellist = Ext.ComponentQuery.query("panel[flag='pers']");
		for(var m=0;m<panellist.length;m++){
			var perpanel = panellist[m];
			var items = perpanel.items.items;
			var leg = perpanel.leg;
			var perlist = [];
			for(var n=0;n<items.length;n++){
				var check = items[n];
				if(check.getValue()==true){
					perlist.push({lexpr:check.lexpr,factor:check.factor,infokind:check.infokind,flag:check.flag,sflag:check.sflag});
				}
			}
			if(perlist.length>0){
				personlist.push({check:perlist});
			}
		}
		//获得组织范围勾选项
		var codelist = [];
		var codetree = Ext.getCmp('codetree');
		var checkarr =codetree.getChecked();
		for(var i=0;i<checkarr.length;i++){
			var checkdata = checkarr[i].data;
			var code=checkdata.id;
			var textValue = checkdata.text;
			var codesetid = checkdata.codesetid;
			codelist.push({codeitemid:code,codesetid:codesetid});
		}
		var vo = new HashMap();
			vo.put('cassetteid',me.casseid);
			vo.put('sodukusql',me.sodukusql);
			vo.put('flag','2');
			vo.put('perlist',personlist);
			vo.put('codelist',codelist);
			vo.put('datelist',datelist);
			Rpc({functionId:'ZJ100000115',success:function(res){
					var resultObj = Ext.decode(res.responseText);
					var editmap = resultObj.editmap;
					me.remove(me.mainPanel);
					me.add(me.createMain(editmap));
			},scope:this},vo);
	},
	/**
	 * 
	 * @param {} analysis_interval
	 */
	createTimepanel:function(analysis_interval){
		var me = this;
		if(analysis_interval!=''){
			analysis_interval = analysis_interval.replace("1","Y");
			analysis_interval = analysis_interval.replace("2","Q");
			analysis_interval = analysis_interval.replace("3","M");
		}else{//如果为空 默认按年显示
			analysis_interval = 'Y';
		}
		var Mypicker =Ext.require('EHR.extWidget.field.DateExtendField', function(){		
			var date_me =	Ext.create('EHR.extWidget.field.DateExtendField',{
				id:'data_picker',
				fieldLabel:'<strong style="font-size:12px;">时间</strong>',
				labelSeparator: '',
				checkflag:false,
				labelWidth:30,
				format:analysis_interval,
				matchFieldWidth:false,
                padding:'5 0 0 5',
                datechecked:function(year,quarter,month){
                	var datelist = [];
            		datelist.push({year:year});
            		datelist.push({quarter:quarter});
            		datelist.push({month:month});
                	me.queryperson(datelist,1,'');
                },
                scope:me,
                width : 236
 			});
 			var comPanel = Ext.widget('panel',{
 				header:false,
				border:0,
				layout:{
					type:'hbox',
					align:'middle',
					pack:'center'
				},
				items:[date_me/*,{xtype:'box',padding:'0 0 0 8',html:'<div title=清除时间筛选条件>清除</div>',style : {cursor : 'pointer',color:'#1b4a98 !important'},listeners : {
													render : function() {
													var hi = this;
													this.getEl().on("click", function() {
														var datelist = [];
														date_me.checkflag=false;
														var nowtime = me.getNowtime(analysis_interval);
														date_me.setRawValue(nowtime);
														me.queryperson(datelist,0,'C');
													    });
								}}}*/]
			});
 			me.menuPanel.items.get(0).insert(0,comPanel);
		});	
	},
	isEmptyObject: function(obj) { 
		
		for ( var name in obj ) { 
		return false; 
		} 
		return true; 
	},
	getNowtime:function(format){
		var me = this;
		var myDate = new Date();       
		var year = myDate.getFullYear();
		var month = parseInt(myDate.getMonth())+1;
		var currQuarter = Math.floor( ( month % 3 == 0 ? ( month / 3 ) : ( month / 3 + 1 ) ) );
		var date = '';
		if(format=='Y'||format==''){
			date = year+'年';
		}
		else if(format=='Y,M'||format=='Y,Q,M'||format=='Q,M'||format=='M'){
			date = year+'年 '+month+'月';
		}
		else if(format=='Y,Q'||format=='Q'){
			date = year+'年 '+currQuarter+'季度';
		}
		return date;
	},
	/**
	*临时添加第一次运行默认显示当前选择的时间分析区间所对应的当前时间的统计结果(解决显示所有数据有重复问题)
	*
	*/
	getDatelist:function(){
		var me = this;
		var myDate = new Date();       
		var year = myDate.getFullYear();
		var month = parseInt(myDate.getMonth())+1;
		var quarter = Math.floor( ( month % 3 == 0 ? ( month / 3 ) : ( month / 3 + 1 ) ) );
		var datelist = [];
		if(me.analysis_interval=='1'||me.analysis_interval==''){
			month = '';
			quarter = '';
		}
		else if(me.analysis_interval=='1,3'||me.analysis_interval=='3'){
			quarter = '';
		}
		else if(me.analysis_interval=='1,2'||me.analysis_interval=='2'){
			month = '';
		}
		else if(me.analysis_interval=='1,2,3'||me.analysis_interval=='2,3'){
		}
		datelist.push({year:year+''});
	    datelist.push({quarter:quarter+''});
	    datelist.push({month:month+''});
	    return datelist;
	}
}); 