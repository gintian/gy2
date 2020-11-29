Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'Card': '/module/card'
	}
});

Ext.define("Card.SearchCards",{
	requires:['Card.CardToolBar'],
	extend:'Ext.panel.Panel',
	width:'100%',
	height:'100%',
	layout:'hbox',
	//minHeight:500,
	tabMark:undefined,
    printViewProty:{},
    tabuilder:undefined,
    dblist:undefined,
    pageVariable:0,
    onlyName:undefined,
    dbname:'',
	constructor:function(config){
		searchCard_me=this;
		this.callParent();
		searchCard_me.A0100="";
		Ext.apply(cardGlobalBeanDefault,searchCard_me.cardFormProty);
        Ext.apply(this,config);
        var map=new HashMap();
       if(searchCard_me.cardFormProty.inforkind=="1"){//dblist 不传 改用直接通过后台取
    	   if(searchCard_me.cardFormProty.a0100!=''){//根据人员库查询人员范围
    		   map.put("dbname",searchCard_me.cardFormProty.a0100.split("`")[0]);
    	   }else{
    		   map.put("dbname","");
    	   }
       }
       map.put("tabid",searchCard_me.cardFormProty.tabid);
       map.put("inforkind",searchCard_me.cardFormProty.inforkind);
       map.put("plan_id",searchCard_me.cardFormProty.plan_id);
       map.put("temp_id",searchCard_me.cardFormProty.temp_id);
       map.put("fieldpurv",searchCard_me.cardFormProty.fieldpurv);
       map.put("a0100",searchCard_me.cardFormProty.a0100);
       map.put("zp_flag",searchCard_me.cardFormProty.zp_flag);
       Rpc({functionId:'CARD0000001',success:function(res){
    	   var rs=Ext.decode(res.responseText);
    	   if(rs.typeFlag){
    		   searchCard_me.personMapList=rs.personMapList;
        	   searchCard_me.printViewProty=rs.printViewProty;
        	   searchCard_me.printViewProty.dbType=rs.dbType;
        	   searchCard_me.cardFormProty.a0100=rs.a0100;
        	   searchCard_me.cardFormProty.fieldpurv=rs.fieldpurv;
        	   searchCard_me.dblist=rs.dblist;
        	   searchCard_me.onlyName=rs.onlyname;//人员唯一标识
        	   if(rs.btnFunction){
        		   cardGlobalBeanDefault.btnFunction=rs.btnFunction;
        	   }
        	   if(cardGlobalBeanDefault.inforkind!='7'&&cardGlobalBeanDefault.inforkind!='10'&&!cardGlobalBeanDefault.cardFlag){//我的薪酬 不加载表格控件只显示当前一个人信息
        		   //表格控件对象
        		   searchCard_me.tabuilder=Ext.decode(rs.tableConfig);
        	   }else{//查询我的薪酬 a0100为空时 重新获取
        		   if(searchCard_me.cardFormProty.a0100=='')
        			   cardGlobalBeanDefault.a0100=rs.a0100;
        	   }
        	   if(cardGlobalBeanDefault.inforkind=='5'){
        		   searchCard_me.cardFormProty.tabid=rs.tabid;
        	   }
        	   	searchCard_me.createMainCard();
	    	   }else{
	    		   Ext.showAlert(rs.msg);
	    	   }
    	   
       		},scope:searchCard_me},map);
      },
      listeners:{
    	  resize:function( e, width, height, oldWidth, oldHeight, eOpts){
    		 
    		  var tabPanel=Ext.getCmp('cardtabPanelId');
    		  var personPanel=Ext.getCmp('personPanel');
    		  if(searchCard_me.tabuilder&&cardGlobalBeanDefault.inforkind!='7'){
    			  tabPanel.setWidth(this.getWidth()-265);
    		  }
    		  
    	  }  
      },
      createMainCard:function(){//生成登记表主页
    	 Ext.util.CSS.createStyleSheet(".x-panel-default-framed{-webkit-border-radius: 4px;-moz-border-radius: 4px;-ms-border-radius: 4px;-o-border-radius: 4px;border-radius: 4px;padding: 0px 0px 0px 0px; border-width: 1px;border-style: solid;background-color: #f1f1f1;}");
     	 Ext.util.CSS.createStyleSheet(".x-tab.x-tab-active.x-tab-default-top{background-image:none;background-color:#f1f1f1;border-top: 1px solid #c5c5c5 !important;border-left: 1px solid #c5c5c5 !important;border-right: 1px solid #c5c5c5 !important;border-bottom: none;}");
    	 Ext.util.CSS.createStyleSheet(".x-tab-bar-top.x-tab-bar-plain>.x-tab-bar-strip-default {border-width: 1px 1px 0 1px;background-color:#f1f1f1 !important}");
    	 Ext.util.CSS.createStyleSheet(".x-tab-bar-strip-default {border-style:solid;border-color:#c5c5c5;background-color: #f1f1f1 !important}");
    	 
    	 Ext.util.CSS.createStyleSheet(".x-tab-active.x-tab .x-tab-default-top-ml{background-image:none;background-color:#f1f1f1}");
    	 Ext.util.CSS.createStyleSheet(".x-tab-active.x-tab .x-tab-default-top-mc{background-image:none;background-color:#f1f1f1}");
    	 Ext.util.CSS.createStyleSheet(".x-tab-active.x-tab .x-tab-default-top-mr{background-image:none;background-color:#f1f1f1}");
    	 
    	 Ext.util.CSS.createStyleSheet(".x-tab-active.x-tab .x-tab-default-top-tl{background-image:none;background-color:#f1f1f1}");
    	 Ext.util.CSS.createStyleSheet(".x-tab-active.x-tab .x-tab-default-top-tc{background-image:none;background-color:#f1f1f1}");
    	 Ext.util.CSS.createStyleSheet(".x-tab-active.x-tab .x-tab-default-top-tr{background-image:none;background-color:#f1f1f1}");
    	 
    	var cardPanel=Ext.create('Ext.tab.Panel',{//登记表内容  
			id:'cardtabPanelId',
			width:(searchCard_me.tabuilder&&cardGlobalBeanDefault.inforkind!='7')?'81%':'100%',
			height:"100%",
			minTabWidth:86,
			style:'border-top:0;border-left:0',
			activeTab : 0,
			tabBar:{
				defaults: {  
		            maxHeight: 25  
		        }  
			},
			listeners:{
				tabchange:function( tabPanel, newCard, oldCard, eOpts){
					searchCard_me.pageVariable=parseInt(newCard.itemid);
				}						
			}
			//frame:true
		});
		if(cardGlobalBeanDefault.inforkind!='7'&&cardGlobalBeanDefault.inforkind!='10'&&searchCard_me.tabuilder) {//我的薪酬 不加载表格控件只显示当前一个人信息
			
			//表格控件渲染
			 if(searchCard_me.tabuilder){
				 searchCard_me.tabuilder.simpleModel=true;
				 this.personListGrid = new BuildTableObj(searchCard_me.tabuilder);
				 if(cardGlobalBeanDefault.inforkind=='1'){
					 var obj={
							render:function(panel){
									panel.on({
										beforeitemmouseenter:function(view,record){
											var dbname=record.get('dbname').split('`')[1];
											var onlyName;
											var onlyNameField;
											if(searchCard_me.onlyName&&searchCard_me.onlyName!=''){
												onlyName=searchCard_me.onlyName.split('`')[1];
												onlyNameField=searchCard_me.onlyName.split('`')[0];
											}
											Ext.getCmp('ykcard_celltip').on({
												beforeshow: function(tip,obj){
													 var div = tip.triggerElement;
										        	    if (Ext.isEmpty(div))
										        	    	return false;
											        	if(div.offsetWidth < div.scrollWidth || div.offsetHeight < div.scrollHeight-4){
											        		tip.update("<div style='white-space:nowrap;overflow:hidden;'>"+
											        				"<div>"+common.label.cardDbName+":"+dbname+"<div>" +
											               ((searchCard_me.onlyName&&searchCard_me.onlyName!='')?("<div>"+onlyName+":"+record.get(onlyNameField)+"</div>"):"")
											        				+"</div>");
											        	}else
											        		return false;
												}
											});
										}
									
									})
								}
							}
						 this.personListGrid.tablePanel.on(obj);
				 }
			 }
			
			 this.personListGrid.tablePanel.selModel.on('selectionchange',function(obj,selected,opts){
				 if(selected.length==20){//全选时不加载数据 防止全选时影响加载速度
					 return;
				 }else{
					 if(selected.length>0){
						 var record=selected[selected.length-1];
						 var a0100=record.get("objid");
						 if(cardGlobalBeanDefault.inforkind=='1'){
							 var dbname=record.get('dbname').split('`')[0];
							 searchCard_me.personLoadMark(dbname,a0100);
						 }else
							 searchCard_me.personLoadMark("",a0100);
					 }
					
				 }
			 })
			 
			 this.personListGrid.tablePanel.getStore().on('load',function(store, records, successful, operation, eOpts){//表格控件加载数据需要时间 切换人员库时 表格控件加载完后再加载登记表内容
				 cardGlobalBeanDefault.a0100='';//切换后清空全局参数A0100
				 if(cardGlobalBeanDefault.inforkind=='1'&&records.length>0){
					 searchCard_me.dbname=records[0].get('dbname').split("`")[0];
				 }
				 searchCard_me.changeTabid(cardGlobalBeanDefault.tabid);
			 },searchCard_me);
			 
			var personPanel=Ext.create('Ext.panel.Panel',{
				width:265,
				id:'personPanel',
				height:'100%',
				border:false,
				layout:'fit',
				items:[this.personListGrid.getMainPanel()]
			})
			this.add(0,personPanel);//人员列表
		}
		
		var cardToolbar_me=Ext.create("Card.CardToolBar",{
							         	dblist:searchCard_me.dblist,
							         	inforkind:cardGlobalBeanDefault.inforkind,
							         	usedday:searchCard_me.printViewProty.usedday,
							         	version:searchCard_me.printViewProty.version,
							         	toolBarScope:searchCard_me
			});
			if(cardGlobalBeanDefault.inforkind!=9){
				this.addDocked(cardToolbar_me.getToolBar());//获取toolbar组件
			}
			this.add(1,cardPanel)
    	if(cardGlobalBeanDefault.isFitFlag){//是否自动填充整个页面
    		Ext.create('Ext.container.Viewport',{
						layout:'fit',
						renderTo:Ext.getBody,
						autoScroll:false,
						items:[this]
			    		});
    	}
    	
    	
    },personRendererFunc:function(A0101,cell,record){//人员添加链接跳转
    	if(cardGlobalBeanDefault.inforkind=='1'){
    		var dbname=record.get('dbname').split("`")[0];//人员库
    		var dbDesc=record.get('dbname').split("`")[1];
    		var b0110=record.get('b0110').split("`")[1];//单位
    		var e0122=record.get('e0122').split("`")[1];//部门
    		b0110=b0110?b0110:'';
    		e0122=e0122?e0122:'';
    		var desc=b0110+"/"+e0122;
    		if(!b0110||!e0122)
    			desc=b0110+e0122;
    		var guidkey=record.get('guidkey')//唯一标识
    		var a0100=record.get('objid');//人员id 
    		//photoUrl  flag
    		var map=new HashMap();
    		map.put("flag","photoUrl");
    		map.put("nbase",dbname);
    		map.put("a0100",a0100);
    		map.put("zp_flag",searchCard_me.cardFormProty.zp_flag);
    		var url="";
    		Rpc({functionId:'CARD0000001',async:false,success:function(res){
    			var rs=Ext.decode(res.responseText);
    			url=rs.photoUrl;
    		},scope:searchCard_me},map);
    		var div="<div style='width: 100%;height:50px;'>"+
    		"<div style='margin-top:2px;;width:46px;height:46px;;float:left;border-radius:50%; overflow:hidden;'><img style='width:46px;height:46px;' src='"+url+"'></img></div>"+//图片
    		"<div style='width:75%;height:100%;float:right;margin-top:0px'>"+
    		"<div style='width:100%;height:50%;margin-top:5px'>"+"<font style='color:#434343;;font-weight:bold;font-family:宋体;font-size:11pt;'>"+A0101+"</font></div>"+//姓名
    		"<div style='width:100%;height:49%;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;'><font color='#555050' >"+desc+"</font></div>"+//单位部门
    		"</div></div>";
    		return div;
    	}else{
    		var a0100=record.get('objid');
    		return "<div>"+A0101+"</div>";
    	}
//    	return "<a href='javascript:searchCard_me.personLoadMark("+'"'+a0100+'"'+")'>"+A0101+"</a>";
    },personLoadMark:function(dbname,a0100){//点击人员加载登记表内容
    	cardGlobalBeanDefault.a0100=a0100;
    	searchCard_me.dbname=dbname;
    	searchCard_me.changeTabid(cardGlobalBeanDefault.tabid);
    },loadBookMark:function(tabid,isFirst){//加载页签
    	var map=new HashMap();
    	map.put("tabid",tabid);
    	map.put("flag","bookMark");
    	map.put("inforkind",searchCard_me.cardFormProty.inforkind);
    	map.put("zp_flag",searchCard_me.cardFormProty.zp_flag);
    	if(searchCard_me.tabMark==''||!searchCard_me.tabMark){
    		Rpc({functionId:'CARD0000001',async:false,success:function(res){
    			var rs=Ext.decode(res.responseText);
    			searchCard_me.tabMark=rs.tabMark;
    			searchCard_me.createTabPanel(tabid);
    		},scope:searchCard_me},map);
    	}else{
    		searchCard_me.createTabPanel(tabid);
    	}
    	
    	
    },createTabPanel:function(tabid){
       var me=this;
       var tabPanel=Ext.getCmp("cardtabPanelId");
       if(searchCard_me.tabMark&&searchCard_me.tabMark.length==1){
		   tabPanel.getTabBar().setHidden(true)
	   }else{
		   tabPanel.getTabBar().setHidden(false)
	   }
 	   for (var i = 0; i < searchCard_me.tabMark.length; i++) {
			var array_element =searchCard_me.tabMark[i];
			tabPanel.add({//页签table 动态添加页签
				title:"<div style='letter-spacing:3px'>"+array_element.title+"</div>",
				itemid:array_element.pageid,
				bodyStyle:'background:#f1f1f1',
				autoScroll:true,//页签添加滚动条
				listeners:{
					afterrender:function(obj,opts){
												if(searchCard_me.autoSize)
													searchCard_me.analyseFont(obj,opts);
											 //  searchCard_me.pageVariable=parseInt(array_element.pageid);
												if(searchCard_me.tabMark.length==1){
													var pageId=document.getElementById("pageID");
													if(pageId){
														pageId.style.marginTop="10px";
														pageId.style.marginBottom="10px"
													}
											  	   }
											}

						  },
				html:searchCard_me.loadCards(tabid,array_element.pageid)
			});
		}
 	   if(searchCard_me.pageVariable&&searchCard_me.pageVariable!=0){
 		  tabPanel.setActiveTab(searchCard_me.pageVariable);
 	   } else{
 		   tabPanel.setActiveTab(0);
 	   }
    },
    analyseFont:function(obj,opts){
    	var me=this;
    	var card=Ext.getDom("card_"+obj.itemid);
    	var tables=card.getElementsByTagName("table");
    	for(var i=0;i<tables.length;i++){
    		var tabID=tables[i].id;//grid_table_no
    		if(tabID==='')
    			continue;
    		var table=$("#"+tabID);
    		if(table&&table.length>0&&table[0].childNodes[0]){
    			var tdID=table[0].childNodes[0].children[0].children[0].id;
    			var div=table[0].childNodes[0].children[0].children[0].children;
    			var fonts=table[0].childNodes[0].children[0].children[0].children[0].children;
    			var td=$("#"+tdID);
    			if(div[0].id.indexOf("grid_sub_")<0){//处理插入非子集
    				if(fonts&&fonts.length>0){
    					if(fonts.length==1){//单元格内只有一个内容时
    						if(fonts[0].id!==''){
    							var font=$("#"+fonts[0].id);
    							me.resizeGridFont(td,font,false,div);
    						}
    					}else{//单元格插入多条内容 最初/最近 xxx条
    						me.resizeGridFont(td,fonts,true,div);
    					}
    				}
    				
    			}else{
    				var table=div[0].childNodes;
    				var divH=$("#"+div[0].id).height();
    				if(table){
    					var tableH=$("#"+table[0].id).height();
    					if(tableH>divH){
    						if(table[0].childNodes[0]){
    							var tr=table[0].childNodes[0].childNodes;
    							if(tr){
    								for(var j=0;j<tr.length;j++){
    									var childTd=$("#"+tr[j].id)[0].childNodes;
    									for(var k=0;k<childTd.length;k++){
    										var childtd=$("#"+childTd[k].id);
    										var childdiv=$("#"+childtd[0].childNodes[0].id);
    										var childfont=$("#"+childdiv[0].childNodes[0].id);
    										me.resizeSubFont(childtd,childfont,childdiv);
    									}
    								}
    							}
    						}
    					}
    				}
    				
    			}
    		}
    	}
    },resizeSubFont:function(td,font,div){
    	var me=this;
		if(td[0].height<=font.height()){
			var size=parseInt(font.css('font-size'));
			if(size==5)//为1时直接返回
				return;
			font.css("font-size",size-1 +'px');
			if(td[0].height<=font.height()){
				if(Ext.isChrome&&size==12){
					var scroll=$("#"+div[0].id)[0].scrollHeight;
					var height=td[0].height;
					$("#"+div[0].id).css("transform","scale("+(height-6)/scroll+")");
					return;
				}else{
					me.resizeSubFont(td,font,div); 
				}
			}
		}
	
    },
    resizeGridFont:function(td,font,multiFlag,div){//非子集单元格高度重新计算  multiFlag 单元格多条记录标识
    	var me=this;
    	if(!multiFlag){
    		if(td[0].height<=font.height()){
    			var size=parseInt(font.css('font-size'));
    			if(size==5)//为1时直接返回
    				return;
    			font.css("font-size",size-1 +'px');
    			if(td[0].height<=font.height()){
    				if(Ext.isChrome&&size==12){
    					var scroll=$("#"+div[0].id)[0].scrollHeight;
    					var height=$("#"+div[0].id).height();
    					$("#"+div[0].id).css("transform","scale("+(height-6)/scroll+")");
    					return;
    				}else{
    					me.resizeGridFont(td,font,false,div); 
    				}
    			}
    		}
    	}else{//多个font处理
    		var height=me.getFontsHeight(font);
    		if(Ext.isChrome){
    			var scan=($("#"+div[0].id).height()-6)/$("#"+div[0].id)[0].scrollHeight;
    			if(scan<0.9)
    				$("#"+div[0].id).css("line-height","10px");
    		}else if(Ext.isSafari){
    				if(parseInt($("#"+font[0].id).css("font-size"))<10)
    					$("#"+div[0].id).css("line-height","8px");
    				else
    					$("#"+div[0].id).css("line-height","15px");
    		}else{
    			if(parseInt($("#"+font[0].id).css('font-size'))<10)
    				$("#"+div[0].id).css("line-height","5px");
    		}	
    		if(td[0].height<=height){
    			me.resizeFontSize(font);
    			if(td[0].height<=me.getFontsHeight(font)){
    				if(Ext.isChrome&&parseInt($("#"+font[0].id).css('font-size'))==12){//
    					var scroll=$("#"+div[0].id)[0].scrollHeight;
    					var height=$("#"+div[0].id).height();
    					$("#"+div[0].id).css("transform","scale("+(height-6)/scroll+")");
    					return;
    				}else{
    					if(parseInt($("#"+font[0].id).css("font-size"))>5)
    						me.resizeGridFont(td,font,true,div);
    				}
    			}
    		}
    	}
    },getFontsHeight:function(font){
    	var height=0;
		for(var i=0;i<font.length;i++){
			var font_c=$("#"+font[i].id);
			height+=font_c.height();
		}
		return height;
    },resizeFontSize:function(font){
    	
    	for(var i=0;i<font.length;i++){
			var font_c=$("#"+font[i].id);
			font_c.css("font-size",parseInt(font_c.css('font-size'))-1 +'px');
			
		}
    },
    changeTabid:function(tabid,flag){//页签切换
    	if(flag==true){
    		searchCard_me.pageVariable=0;
    	}
    	var cardPanel=Ext.getCmp('cardtabPanelId');
    	if(cardPanel&&(tabid&&tabid.indexOf(",")<0)){
    		cardPanel.removeAll([true]);//切换人员或者切换登记表 移除所有页签 重新加载页签
        	cardGlobalBeanDefault.tabid=tabid;
        	searchCard_me.loadBookMark(tabid);
    	}
    } ,
    loadCards:function(tabid,pageid){//
    
    	if(!pageid){
    		pageid=0;
    	}
    	var userbase='';
    	if(cardGlobalBeanDefault.inforkind=='1'||cardGlobalBeanDefault.inforkind=='7'||cardGlobalBeanDefault.inforkind=='10'||cardGlobalBeanDefault.inforkind=='9'){
    		if(cardGlobalBeanDefault.a0100!=''&&cardGlobalBeanDefault.a0100.indexOf('`')>-1){//单独显示一个人信息时a0100格式为Usr`A0100 表格控件选中操作时a0100为A0100无人员库前缀
    				userbase=cardGlobalBeanDefault.a0100.split('`')[0];
    		}else{
    			userbase=searchCard_me.dbname;
    		}
    	}
    	//this.personListGrid.tablePanel.getSelectionModel( ).selectAll();
    	//id 切换人员库默认查询第一个人或单位
    	 var id='';
    	 if(cardGlobalBeanDefault.a0100&&cardGlobalBeanDefault.a0100!=''){
    		 if(cardGlobalBeanDefault.a0100.indexOf('`')>-1)
    			 id=cardGlobalBeanDefault.a0100.split('`')[1];
    		 else
    			 id=cardGlobalBeanDefault.a0100;
    	 }else{
    		 if(this.personListGrid.tablePanel.getStore().getData().items.length>0)
    			 id=this.personListGrid.tablePanel.getStore().getData().items[0].data.objid;
    	 }
    	
    	 searchCard_me.A0100=id;//全局参数A0100
    	
    	 var map=new HashMap();
    	 map.put("bizDate",cardGlobalBeanDefault.bizDate);
    	 if(cardGlobalBeanDefault.inforkind=='10'){
    		 map.put("inforkind",'1');    		 
    	 }else{
    		 map.put("inforkind",cardGlobalBeanDefault.inforkind);
    	 }
    	 map.put("nid",id);
    	 map.put("nbase",userbase);
    	 map.put("pageid",pageid);
    	 map.put("tabid",tabid);
    	 map.put("fieldpurv",cardGlobalBeanDefault.fieldpurv);
    	 map.put("plan_id",cardGlobalBeanDefault.plan_id);
    	 var userAgent = window.navigator.userAgent; 
    	 if(userAgent.indexOf('Firefox') != -1)
    	 map.put("browser","Firefox");
    	 map.put("isMobile","1");
    	 map.put("cardtype","");
    	 map.put("zp_flag",searchCard_me.cardFormProty.zp_flag);
    	 var queryflag=cardGlobalBeanDefault.queryflag;
		 map.put("queryflag",(queryflag==''?'0':queryflag+''));
		 if(queryflag!='0'&&Ext.getCmp('countComId')){
			 var countFlag=Ext.getCmp('countComId').getSelection().data.countId;
			 if(countFlag!='times'){
				 var year=Ext.getCmp('yearComId').getSelection().data.yearId;
				 if(countFlag=='year'){
    				 map.put("year",year);
    			 }else if(countFlag=='month'){
    				 var month=Ext.getCmp('monthComId').getSelection().data.monthId;
    				 map.put("year",year);
    				 map.put("month",month);
    				 var cTimeCom=Ext.getCmp('countID');
    				 if(cTimeCom&&cTimeCom.getSelection()&&cTimeCom.getSelection().data){
    					 map.put("ctimes",(cTimeCom.getSelection().data.countId=='all'?'11':cTimeCom.getSelection().data.countId));
    				 }else{
    					 map.put("ctimes","11");
    				 }
    				 
    			 }else if(countFlag=='season'){
    				 var season=Ext.getCmp('seasonComId').getSelection().data.seasonId;
    				 map.put("year",year);
    				 map.put("season",season);
    			 }
			 }else{
				 var startDate=Ext.getCmp('startDateId').rawValue;
				 var endDate=Ext.getCmp('endDateId').rawValue;
				 if(!Ext.Date.parse(startDate,'Y-m-d')||!Ext.Date.parse(startDate,'Y-m-d')){
					 Ext.showAlert(common.label.errorDateMsg)
					 return
				 }
				 map.put("startDate",startDate);
    			 map.put("endDate",endDate);
			 }
		 }
    	var html="<div><p>空</p><div>";
    	
    	Rpc({functionId:'CARD0000004',async:false,success:function(res){
      	  		var rs=Ext.decode(res.responseText);
      	  		searchCard_me.autoSize=rs.autoSize;
      	  		html="<div id='card_"+pageid+"'  align=\"center\" style='width:100%;height:100%;background-color:#f1f1f1;'>"+rs.cardHtml+"</div>";
         		},scope:searchCard_me},map);
        return html;
    },
    getPersnList:function(){//根据选择的人员库查询 对应查询结果集
       var map=new HashMap();
       map.put("inforkind",cardGlobalBeanDefault.inforkind);
       map.put("plan_id",cardGlobalBeanDefault.plan_id); 
       map.put("flag","checkdbname");//切换人员库 人员列表控件重新显示
       map.put("zp_flag",searchCard_me.cardFormProty.zp_flag);
       Rpc({functionId:'CARD0000001',success:function(res){
    	   var rs=Ext.decode(res.responseText);
    	   rs.tableConfig.simpleModel=true;
    	   searchCard_me.reloadTabObj(rs.tableConfig);
    	     //考虑打印预览personlist数据塞入
       		},scope:searchCard_me},map);
      // var request=new Request({method:'post',onSuccess:getPersonlist,functionId:'07020100007'},hashvo);
    },reloadTabObj:function(tabObj){//抽取创建的表格控件 指定personPanelID 放入创建的表格控件
    	this.personListGrid.tablePanel.getStore().load({page:1});//bug 49774  
    },comSearch:function(value){//通用查询
    	
    	Ext.MessageBox.wait(common.label.comSearch+"...", common.msg.wait);
    	var map=new HashMap();
    	map.put("inforkind",cardGlobalBeanDefault.inforkind);
    	map.put("comSearch","1");
    	map.put("A0101",value);
    	
    	Rpc({functionId:'CARD0000003',success:function(res){
      	   var rs=Ext.decode(res.responseText);
      	   		if(rs.flagType){
      	   		   searchCard_me.getPersnList();
		      	   Ext.MessageBox.close();
      	   		}else{
		      	   Ext.MessageBox.close();
		      	   Ext.showAlert(rs.eMsg);
      	   		}
		      	   
         		},scope:searchCard_me},map);
    	
    },excecuteWord:function(flag,fileFlag,dataBean,files,selectType){
    	    //flag: false 单个人   all 全部人员  1 部分人员选中人员
    		//fileFlag pdf  word
    		//dataBean 存储日期
    		//files:all 多人一文档  1：一人一文档
    		//var me=this;
            var tab_id=cardGlobalBeanDefault.tabid;      
            if(tab_id==null||tab_id.length<=0||tab_id=="-1")
            {
               alert(common.label.searchCard+"！");
               return false;
            }
            var hashvo=new HashMap();
            //1 获取选中的人员 2 获取全部人员 3 获取当前人员
            //一人一文档 一人多文档
            if(flag=='all'||flag=='1'){
            	var nids=new Array();
            	var items;
            	if(flag=='all'){
            		items=this.personListGrid.tablePanel.getStore().getData().items;
            	}else{
            		items=this.personListGrid.tablePanel.getSelectionModel().getSelection();
            	}
            	//var items=personBox.getStore().getData().items;
            	if(items.length<1){
            		Ext.Msg.alert(common.button.promptmessage, common.msg.selectData);
            		return;
            	}
            	for (var i = 0; i < items.length; i++) {
            		if(cardGlobalBeanDefault.inforkind=='1')
            			nids.push(items[i].data.dbname.split('`')[0]+'`'+items[i].data.objid);
            		else
            			nids.push(items[i].data.objid);
				}
            	if(nids.length>0){
            		hashvo.put("nid",nids);
            	}
            }else{
            	// 当前人员：1 选中状态 2为选中默认加载
            	if(cardGlobalBeanDefault.inforkind=='7')
            		hashvo.put("nid",cardGlobalBeanDefault.a0100.split('`')[1]);
            	else{
            		if(!cardGlobalBeanDefault.cardFlag)
            			hashvo.put("nid",cardGlobalBeanDefault.a0100);
            		else{
            			if(cardGlobalBeanDefault.a0100.indexOf('`')>-1)
            				hashvo.put("nid",cardGlobalBeanDefault.a0100.split('`')[1]);
            		}
            			
            	}
            	if(!hashvo.get('nid')&&this.personListGrid&&this.personListGrid.tablePanel){
            		if(cardGlobalBeanDefault.inforkind=='1'){
            			if(this.personListGrid.tablePanel.getSelectionModel().getSelection().length>0){
            				var data=this.personListGrid.tablePanel.getSelectionModel().getSelection()[0].data;
            				hashvo.put("nid",data.dbname.split('`')[0]+"`"+data.objid);
            			}else{//没有选中 则默认取第一条记录
            				if(this.personListGrid.tablePanel.getStore().getData().items.length>0){
            					var data=this.personListGrid.tablePanel.getStore().getData().items[0].data;
            					hashvo.put("nid",data.dbname.split('`')[0]+"`"+data.objid);
            				}else{
            					Ext.showAlert(common.label.outFileMsg);
            					return;
            				}
            			}
            		}else{
            			if(this.personListGrid.tablePanel.getSelectionModel().getSelection().length>0)
            				hashvo.put("nid",this.personListGrid.tablePanel.getSelectionModel().getSelection()[0].data.objid);
            			else//没有选中 则默认取第一条记录
            				hashvo.put("nid",this.personListGrid.tablePanel.getStore().getData().items[0].data.objid);
            		}
            	}
            }
            hashvo.put("flagType",flag);
            hashvo.put("fileFlag",fileFlag);
            hashvo.put("flag",files);
            hashvo.put("cardid",tab_id);
            if(cardGlobalBeanDefault.queryflag=='1'){//传参逻辑先按照以前的处理，
            	hashvo.put("cyear",(dataBean?dataBean.year:''));
            }else if(cardGlobalBeanDefault.queryflag=='3'||cardGlobalBeanDefault.queryflag=='4'){
            	hashvo.put("cyear",(dataBean?dataBean.year:''));
            }
            hashvo.put("cmonth",(dataBean?dataBean.month:''));
            hashvo.put("season",(dataBean?dataBean.season:''));
            hashvo.put("ctimes",(dataBean?dataBean.count:''));
            hashvo.put("cdatestart",(dataBean?dataBean.startDate:''));
            hashvo.put("cdateend",(dataBean?dataBean.endDate:''));
            
            hashvo.put("userpriv","noinfo");
            if(cardGlobalBeanDefault.inforkind=='7')
            	hashvo.put("istype","0"); 
            else
            	hashvo.put("istype","1"); 
            hashvo.put("tabid",tab_id); 
    	    hashvo.put("infokind",cardGlobalBeanDefault.inforkind);
    	    hashvo.put("plan_id",cardGlobalBeanDefault.plan_id);
    	    hashvo.put("querytype",cardGlobalBeanDefault.queryflag+'');
    	    if(cardGlobalBeanDefault.inforkind=='1'){
    	    	var userbase;
    	    	if(cardGlobalBeanDefault.cardFlag){
    	    		userbase=cardGlobalBeanDefault.a0100.split('`')[0];
    	    	}else{
    	    		userbase=searchCard_me.dbname;
    	    	}
    	    	 hashvo.put("userbase",userbase);
    	    }else if(cardGlobalBeanDefault.inforkind=='7'||cardGlobalBeanDefault.inforkind=='10'){//我的薪酬
    	    	hashvo.put("userbase",cardGlobalBeanDefault.a0100.split('`')[0]);
    	    }else if(cardGlobalBeanDefault.inforkind=='5'){
    	    	 hashvo.put("userbase","Usr");
    	    }else{
    	    	if(flag!='all'&&flag!='1'&&(hashvo.get("nid")==null||hashvo.get("nid")==''))
    	    		hashvo.put("nid",cardGlobalBeanDefault.a0100);
    	    	hashvo.put("userbase","BK");
    	    }
    	    hashvo.put("autoSize",searchCard_me.autoSize+"");
    	    hashvo.put("officeOrWps",selectType+"");
    	    searchCard_me.showWait(true,fileFlag);
            Rpc({functionId:'CARD0000005',async:true,success:searchCard_me.showWord,scope:searchCard_me},hashvo); 
    	
    },
    showWord:function(outparamters){
    	var res=Ext.decode(outparamters.responseText);// 
    	searchCard_me.showWait(false);
    	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    	var isEdge = userAgent.indexOf("Edge") > -1;
    	if(res.succeed){
    	   	var url=res.url;
    	   	if(isEdge){
    	   		var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true",res.fileFlag);
    	   	}else{
	    	   	if(Ext.isSafari){
	    	   	  window.location.target="_blank";
	              window.location.href="/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true";
	    	   	}else{
	    	   	  var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true",res.fileFlag);
	    	   	}
    	   		
    	   	}
    	}else{
    		
    	}
    },
    showWait:function(flag,fileFlag){
   	 if(flag){
   	 	if(fileFlag=='pdf'){
   		 Ext.MessageBox.wait(common.label.expMSG.replace("{0}","PDF")+"...", common.msg.wait);
   	 	}else{
   		 Ext.MessageBox.wait(common.label.expMSG.replace("{0}","WORD")+"...", common.msg.wait);
   	 	}
   	 }
   	 else
   		 Ext.MessageBox.close(); 	 
   	},
   	showPrintCard:function()//打印预览
   	{//每次点击打印预览 重新请求查询数据
   		//var me=this;
   	   if(!AxManager.setup("axContainer", "CardPreview1", 0, 0, searchCard_me.showPrintCard, AxManager.cardpkgName))
   	       return false;
   	   if(!searchCard_me.initCard())
   	       return;
   	   var hashvo=new HashMap();
   	   var inforkind=searchCard_me.cardFormProty.inforkind; 
  	   if(!(searchCard_me.cardFormProty.a0100==''||searchCard_me.cardFormProty.a0100.indexOf('`')<0)){
  		   hashvo.put("a0100",searchCard_me.cardFormProty.a0100);
  	   }
  	   if(this.personListGrid&&this.personListGrid.tablePanel&&this.personListGrid.tablePanel.getSelectionModel().getSelection().length>0){
  		    //多人时 获取选中的人员数据
  		    var items=this.personListGrid.tablePanel.getSelectionModel().getSelection();
  		    var arry=[];
	  		for (var i = 0; i < items.length; i++) {
	    		if(cardGlobalBeanDefault.inforkind=='1'){
	    			arry.push(items[i].data.dbname.split('`')[0]+'`'+items[i].data.objid+"`"+items[i].data.objdesc);
	    		}else{
	    			arry.push(items[i].data.objid+"`"+items[i].data.objdesc);
	    		}
			}
	  		hashvo.put("selectArry",arry);
  	   }
  	   //this.personListGrid.tablePanel.getSelectionModel().getSelection();
   	   hashvo.put("inforkind",inforkind+"");
   	   hashvo.put("plan_id",searchCard_me.cardFormProty.plan_id); 
   	   Rpc({functionId:'CARD0000006',async:false,success:searchCard_me.printCard,scope:searchCard_me},hashvo); 
   	},
   	printCard:function(outparamters)//打印预览加载数据
   	{
   	  var rs=Ext.decode(outparamters.responseText);
   	  if(!rs.flagType){
   		  Ext.showAlert(rs.eMsg);
   		  return;
   	  }
   	  var personlist=rs.personlist; 
   	  var inforkind=searchCard_me.cardFormProty.inforkind;   
   	  var tab_id=searchCard_me.cardFormProty.tabid; 
   	  if(tab_id==null||tab_id.length<=0||tab_id=="-1")
   	  {
   	           alert(common.label.searchCard+"！");
   	           return false;
   	  }  
   	  var obj = document.getElementById('CardPreview1');  
   	  if(obj==null)
   	  {
   	      alert(common.label.dowmPlugin);
   	      return false;
   	  }
   	  obj.SetCardID(tab_id);
   	  obj.SetDataFlag(searchCard_me.cardFormProty.dataFlag);
   	  
   	  
   	  if(searchCard_me.cardFormProty.a0100==''){
 		 var dbBox=Ext.getCmp("dbBox");
   	   if(!dbBox){
   		 obj.SetNBASE("");
   	   }else{
   		 obj.SetNBASE(dbBox.getValue()==null?"":dbBox.getValue());
   	   }   
 	   }else{
 		  if(searchCard_me.cardFormProty.a0100.indexOf("`")>-1){
 			 obj.SetNBASE(searchCard_me.cardFormProty.a0100.split("`")[0]);
 		  }else
 			 obj.SetNBASE(""); 
 		  
 	   }	   
   	 
   	  obj.ClearObjs();  
   	  for(var i=0;i<personlist.length;i++)
   	  {     
   	     obj.AddObjId(personlist[i].dataName);
   	  }
   	 try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
   	  obj.ShowCardModal();
   	},
   	initCard:function()
   	{
   	      var DBType=searchCard_me.printViewProty.dbType;
   	      var UserName=searchCard_me.printViewProty.UserName;   
   	      var obj = document.getElementById('CardPreview1');
   	      if(obj==null)
   	      {
   	         return false;
   	      }   
   	      var superUser=searchCard_me.printViewProty.superUser;
   	      var menuPriv=searchCard_me.printViewProty.menuPriv;
   	      var tablePriv=searchCard_me.printViewProty.tablePriv;
   	      var isselfinfo=searchCard_me.printViewProty.isselfinfo;
   	      //obj.SetSelfInfo(isselfinfo); 
   	      obj.SetSuperUser(superUser); 
   	      obj.SetUserMenuPriv(menuPriv);
   	      obj.SetUserTablePriv(tablePriv);
   	      obj.SetURL(cardGlobalBeanDefault.url);
   	      obj.SetDBType(DBType);
   	      obj.SetUserName(searchCard_me.printViewProty.UserName);
   	      obj.SetUserFullName(searchCard_me.printViewProty.userFullName);
   	      obj.SetHrpVersion(searchCard_me.printViewProty.version);
   	      return true;
   	},
   	search:function(){//通用查询  绩效无通用查询功能  inforkind只考虑 A B K 
		 var map2 = new HashMap();
		 //key是1：A：信息主集和子集，B：组织机构主集和子集，K：职位信息主集和子集，
		 //Y:党组织，V：团组织，W：工会组织，h基准岗位，tableName是指标集表名，以逗号分隔；
		 if(cardGlobalBeanDefault.inforkind=='2'||cardGlobalBeanDefault.inforkind=='4'||cardGlobalBeanDefault.inforkind=='6'){
			if(cardGlobalBeanDefault.inforkind=='2') 
				map2.put(1, "b");
			else if(cardGlobalBeanDefault.inforkind=='4')
				map2.put(1, "k");
			else
				map2.put(1, "H");
		 }else{
			 map2.put(1, "a,b,k");//要显示的子集
		 }
		 var map = new HashMap();
		 map.put('salaryid', '');
		 map.put('condStr', '');//复杂条件，简单条件表达式
		 map.put('cexpr', '');//简单公式时：1*2
		 map.put('path', "2306514");
		 // 按检索条件和人员范围 
		 map.put('priv', "0");//手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
		 //map.put('filter_factor', this.templPropety.filter_factor);
		 if(cardGlobalBeanDefault.inforkind=='2'){
			 map.put('info_type',"2");
		 }else if(cardGlobalBeanDefault.inforkind=='4'){
			 map.put('info_type',"3");
		 }else if(cardGlobalBeanDefault.inforkind=='6'){
			 map.put('info_type',"3");
		 }else{
			 map.put('info_type',"1");
		 }
		 var imodule='0';
		 if(cardGlobalBeanDefault.inforkind=='4')
			 imodule='1';
		 Ext.require('EHR.selectfield.SelectField',function(){
			 Ext.create("EHR.selectfield.SelectField",{imodule:imodule,type:(cardGlobalBeanDefault.inforkind=='6')?"3":'1',queryType:"1",flag:'1',dataMap:map,comBoxDataInfoMap:map2,rightDataList:'',title:"选择指标",queryCallbackfunc:"searchCard_me.callBack"});
		 });

   	},
   	callBack:function(expr,checkValues,dataList,query_type,priv,filter_factor){
		var map = new HashMap();
		map.put('expr',expr);
		map.put('checkValues',checkValues);
		map.put('inforkind',cardGlobalBeanDefault.inforkind);//=1人员,=2单位,=3职位
		map.put('dataList',dataList);
		//map.put('tabid',templateTool_me.tab_id);
		map.put('query_type',query_type);
		map.put('filter_factor',filter_factor);
		map.put('chpriv',priv);
		Ext.MessageBox.wait(common.label.insertData+"...", common.msg.wait);
		Rpc({functionId:'CARD0000002',async:true,success:function(res){
			var res=Ext.decode(res.responseText);
			if(res.flagType){
				searchCard_me.getPersnList();
				Ext.MessageBox.close();
				//查询后重新加载表格控件
			}else{
				Ext.MessageBox.close();
				Ext.showAlert(res.eMsg);
			}
			
		}},map);

   	}
});