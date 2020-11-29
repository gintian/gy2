/**
 * hej add 2016/2/16
 * 盒式报表九宫格简易显示
 */
Ext.define("BoxReportURL.SimpleSoduku", {
	extend:"Ext.panel.Panel",
	boxid:'',
	border:0,
	width:'100%',
	padding:'0 0 0 10',
	initComponent:function() {
		this.callParent();
		this.createSimplePanel();
	},
	createSimplePanel:function(){
		var me = this;
		me.setBodyStyle('background-color:#ffffff');
		//me.setHeight(194);
		me.setAutoScroll(true);
		me.setStyle({cursor : 'pointer'});
		var datelist = [];
		var vo = new HashMap();
		vo.put('cassetteid',me.boxid);
		vo.put('sodukusql','');
		vo.put('datelist',datelist);
		vo.put('flag','2');
		Rpc({functionId:'ZJ100000115',async:false,success:function(res){
				var resultObj = Ext.decode(res.responseText);
				var editmap = resultObj.editmap;
				var isempty = me.isEmptyObject(editmap);
				if(isempty==false){
					me.createMain(editmap);
				}
				
		},scope:this},vo);
	},
	createMain:function(editmap){
		var me = this;
		var sodukulist = editmap.sodukulist;//九宫格数据
		var latlist = editmap.latlist;//横向坐标
		var longlist = editmap.longlist;//纵向坐标
		var lateral_desc = editmap.lateral_desc;
		var longitudinal_desc = editmap.longitudinal_desc;
		var staff_view_url = editmap.staff_view_url;
		var rightnum = 0;
		var list = [];
		var s = undefined;
		var titlelabel = undefined;
		if(longitudinal_desc.length>longlist.length*2){
			longitudinal_desc = longitudinal_desc.substring(0,longlist.length*2);
		}
		list.push({xtype:'container',width:30,rowspan: longlist.length+1,items:[{xtype:'label',
						html:'<div style="width:20px;padding-left:7px;word-break:break-all;text-algin:left;font-size:16px !important;">'+longitudinal_desc+'</div>'
						}]});
		for(var i=0;i<longlist.length;i++){
			var longid = longlist[i].codeitemid;
			var longdesc = longlist[i].codeitemdesc;
			longdesc = longdesc.substring(0,2);
			var labelx = Ext.widget('container',{width:30,items:[{
				xtype:'label',
				html:'<div style="width:30px;padding-right:15px;word-break:break-all;text-algin:left;" >'+longdesc+'</div>'
			}]});	
			list.push(labelx);
			var liney = undefined;
			if(i==0){
				liney = Ext.widget('container',{width:10,height:53,padding:'5 0 0 0',layout:{type:'vbox',align:'center',pack:'center'},
				items:[{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/jiantou.png',height:11,width:10},
					{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/xian.png',height:53,width:2}]
			});
			}else{
				liney = Ext.widget('container',{width:10,height:53,layout:{type:'vbox',align:'center',pack:'center'},
				items:[{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/xian.png',height:53,width:2}]
			});
			}
				
			list.push(liney);
			var title = '';
			
			for(var j=0;j<latlist.length;j++){
				var subtitle = "";
				var latid = latlist[j].codeitemid;
				var xy = latid+'_'+longid;
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
							title='';
							subtitle = title;
						}else{
							title = soduku.title;
							var regExp = /^[a-z0-9]+$/i;
							if(regExp.test(title)==true){
								if(title.length>7){
									subtitle = title.substring(0,7);
								}else{
									subtitle = title;
								}
							}else{
								if(title.length>4){
									subtitle = title.substring(0,4);
								}else{
									subtitle = title;
								}
							}
						}
						}
				}
				if(subtitle==''){
					subtitle = '<br />';
				}
				if(num!='0'){
				}else{
					rightnum='0';
				}
				rightnum = rightnum+'';
				s = Ext.widget('panel',{
					id:'panel_'+xy,
				    padding:'0 5 0 5',
				    border:1,
				    width:70,
				    height:43,
					items:[{xtype:'container',layout:'vbox',padding:'5 0 5 0',
							items:[{xtype:'label',padding:'0 5 2 5',html:subtitle},{xtype:'box',padding:'0 8 0 0',id:'num_'+xy,style:{cursor : 'pointer'},rightnum:rightnum,
					html:(rightnum>0)?'<div title="查看更多" style="width:50px;color:#1b4a98;text-align:right;">'+rightnum+'</div>':'<div style="width:50px;color:#1b4a98;text-align:right;">'+rightnum+'</div>',
					listeners : {
						render : function() {
						var hi = this;
						this.getEl().on("click", function() {
								if(hi.rightnum>0)
									me.showimageWin(hi,hi.ownerCt.ownerCt,hi.id,sodukulist,staff_view_url);
								})}}}
								]}]
				});
						
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
			linex = Ext.widget('container',{width:80,height:16,padding:'0 0 0 0',layout:{type:'hbox',align:'middle',pack:'center'},
			items:[{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/xian-h.png',height:2,width:72},
				{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/jiantou-h.png',height:10,width:8}]
		});
		}else{
			linex = Ext.widget('container',{width:80,height:16,padding:'0 0 0 0',layout:{type:'hbox',align:'middle',pack:'center'},
			items:[{xtype:'image',src:rootPath+'/module/system/personalsoduku/images/xian-h.png',height:2,width:80}]
		});
		}
			
		list.push(linex);
		}
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		for(var p=0;p<latlist.length;p++){
			var labely = Ext.widget('container',{padding:0,width:70,layout:{type:'hbox',align:'middle',pack:'center'},items:[{xtype:'label',html:'<span style="height:20px;text-align:center">'+latlist[p].codeitemdesc+'</span>'
		}]});	
			list.push(labely);
		}
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		list.push({xtype:'container',width:20,html:'&nbsp;&nbsp;&nbsp;'});
		if(lateral_desc.length>latlist.length*4){
			lateral_desc = lateral_desc.substring(0,latlist.length*4);
		}
		list.push({xtype:'container',width:(latlist.length)*78,colspan: latlist.length+2,layout:{type:'hbox',align:'middle',pack:'center'},items:[{xtype:'label',style:'font-size:16px !important;',text:lateral_desc}]});

		var layout = {type:'table',
    			columns: latlist.length+3};
		me.setLayout(layout);
		me.add(list);
	},
	isEmptyObject: function(obj) { 
		for ( var name in obj ) { 
		return false; 
		} 
		return true; 
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
					panelitems = me.createPanelItems(xylist,staff_view_url);
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
					}else{//小于6个大于0个
						items.push({xtype:'container',width:55,height:95,
						layout:{type:'hbox',align:'middle',pack:'center'},items:[{xtype:'image',
										title:'上一张',margin:'0 10 0 10',src:rootPath+'/images/new_module/left.png'}]});
						var panelitems = [];
						panelitems = me.createPanelItems(xylist,staff_view_url);
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
						layout:{type:'hbox',align:'middle',pack:'center'},items:[{xtype:'image',title:'下一张',
						margin:'0 10 0 10',src:rootPath+'/images/new_module/right.png'}]});
					}
			}
		}
		var imageWin=Ext.widget('window',{
			flag:'win',
			x:aa.getX()-180,
			y:aa.getY()+45,
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
	createPanelItems:function(xylist,staff_view_url){
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
						backgroundImage:'url(/servlet/DisplayOleContent?nbase='+pubfnbase+'&a0100='+pubfa0100+'&imageResize='+$URL.encode("65`65")+'&caseNullImg=/images/photo.jpg);background-size:100% 100%;cursor:pointer;'
					},//border-radius: 50%;
					src:rootPath+'/module/system/personalsoduku/images/65.png',
				listeners:{render:function(){
				    var hi = this;
					this.getEl().on("click", function() {
									if(staff_view_url!=null&&staff_view_url!=''){
										if(staff_view_url.indexOf('&')!=-1){
											if(staff_view_url.indexOf('/synthesiscard')!=-1){//登记表
												var perURL = staff_view_url+'&userbase='+hi.nbase+'&a0100='+hi.carda0100;
											}else{
												var perURL = staff_view_url+'&userbase='+hi.nbase+'&a0100='+hi.pubfa0100;
											}
										}else{
											if(staff_view_url.indexOf('/synthesiscard')!=-1){
												var perURL = staff_view_url+'?userbase='+hi.nbase+'&a0100='+hi.carda0100;
											}else{
												var perURL = staff_view_url+'?userbase='+hi.nbase+'&a0100='+hi.pubfa0100;
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
		return panelitems;
	}
});