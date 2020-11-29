Ext.Loader.loadScript({url:'/module/officermanage/OfficerCardView.js'});
Ext.define("Officermanage.ManageView",{
	requires:['EHR.commonQuery.CommonQuery'],
	queryFields:null,
	dbNameList:null,
	defaultField:null,
	b0110:null,
	postStat:null,
	constructor:function(config){
	     var me =this;
	     var map=new HashMap();
		Rpc({functionId:'OM000000004',success:me.init,scope:me},map);
	},
	init:function(res){
		var me=this;
		res=Ext.decode(res.responseText);
		if(!res.flagType){
			Ext.showAlert(res.errorMsg);
			return;
		}
		me.b0110=res.b0110;
		me.postStat=res.postStat;
		me.dbNameList=res.dbNameList;
		me.queryFields=res.queryFields;
		me.defaultFields=res.defaultFields;
		managerGobal.mangeView=res.mangeView;
		me.tableObj = new BuildTableObj(Ext.decode(res.config));
		me.tableObj.insertItem(me.createQuery(),0);
		me.outFileBtn(me.tableObj)
		var tabid=me.tableObj.getMainPanel().items.items[0].items.items[2].id;
		Ext.getCmp(tabid).add(me.createTreePanel())//左侧组织机构树
		Ext.create('Ext.container.Viewport',{
					layout:'fit',
					items:[me.tableObj.getMainPanel()]
				});
	},
	createTreePanel:function(){
		var me=this;
		var extraParams = {
			ctrltype:'1',
			nmodule:'3',
			codesetid:me.postStat!=""?"UN":'@K',
			multiple:false
		};
		var treeStore = Ext.create('Ext.data.TreeStore',{
	    	fields: ['text','id','codesetid','orgtype','selectable'], 
	        proxy: {   
	            type:'transaction',
	            extraParams:extraParams,
	            functionId:'ZJ100000131'
	        }  
			});
		var panel=Ext.create("Ext.tree.Panel",{
			width:200,
			height:'100%',
			store:treeStore,
			collapsible: true, 
			collapseToolText:'收缩', 
			expandToolText:'展开',
			checkModel:'single',
			region:'west',
			title:me.postStat!=""?'任职单位':'组织机构',
			rootVisible:me.postStat!=""?true:false,
			root:{text:"组织机构",expanded: true},
			listeners:{
				itemclick:me.doSelect,
				scope:me
			}
		});
		return panel;
	},
	doSelect:function(view, record, node, rowIndex, e){
		var me=this;
		var map=new HashMap();
		map.put("flag","treeFlag");
		map.put("id",record.data.id);
		map.put("codesetid",record.data.codesetid);
		map.put("b0110",me.b0110);
		map.put("postStat",me.postStat);
		Rpc({functionId:'OM000000005',success:function(res){
			var res=Ext.decode(res.responseText);
			if(res.typeFlag){
				me.tableObj.tablePanel.getStore().loadPage(1);
			}else{
				Ext.showAlert(res.errMsg);
			}
		},scope:me},map);
		
	},
	renderJobName:function(obj,cell,record){
		obj=obj.replace(/,/g,'</br>');
		return obj;
	},
	renderA0101:function(obj,cell,record){
		return "<a style='cursor:pointer' onclick=\"managerGobal.createCardView('"+record.get('guidkey')+"','"+record.get('dbtype')+"','"+record.get('a0100')+"')\"><font color='#1B4A98'>"+record.get('a0101')+"</font></a>"
	},
	createCardView:function(guidkey,nbase,a0100){
		var data={guidkey:guidkey,nbase:nbase,A0100:a0100};
	    var window=Ext.create('Ext.window.Window',{
		   width:Ext.getBody().getWidth(),
		   height:Ext.getBody().getHeight(),
		   layout:'fit',
		   id:'manageView',
		   closable:false,
		   header:false
	    }).show();
		   window.add({xtype:'officerCardView',data:data,closePanelId:"manageView"});
	},
	openWindow:function(href,flag){
		   var width=Ext.getBody().getWidth();
		   var height=Ext.getBody().getHeight();
		   Ext.create('Ext.window.Window',{
			   width:width,
			   height:height,
	           html:'<iframe  height="100%" width="100%" '+(flag?"":'scrolling="no"')+'  frameborder="0"  src='+href+'></iframe>'
		   }).show();
	},
	renderColFunc:function(obj,cell,record){
		
		var arry=obj.split("`");
		var html=""
			if(obj!=""){
				html="<div style='overflow: hidden; white-space: nowrap; text-overflow: ellipsis;width:100%;height:50%;border-style:none none solid none;border-color:#d0d0d0;border-width:0 0 1px 0 '>"+arry[0]+"</div>"+arry[1];
			}
		
		return html;
	},
	reloadCol:function(obj,cell,record){
		obj=obj.replace(/\\r/g,"&nbsp;&nbsp;&nbsp;&nbsp;").replace(/\\n/g,"</br>");
		return obj;
	},
	//flag:false 导出部分 all 导出全部  exp_current 选中人员导出   filetype: all 多人一文档 1：一人一文档 
	//type xml word pdf 
	outLRMX:function(type,flag,filetype){//导出文件
		var me=this;
		var map=new HashMap();
		map.put("type",type)
		map.put("filetype",filetype)
		if(flag=='all'){//导出全部
			map.put("flag","all");
			Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
		}else{//导出部分
			var data=me.tableObj.tablePanel.getSelectionModel().getSelection();
			var arry=[];
			map.put("flag","false");
			if(data.length<1){
				Ext.showAlert(common.label.outFileMsg);
				return;
			}
			Ext.MessageBox.wait(common.label.expMSG, common.label.expWait)
			for(var i=0;i<data.length;i++){
				arry.push(data[i].data);
			}
			map.put("data",arry);
		}
		Rpc({functionId:'OM000000002',success:function(res){
			Ext.MessageBox.close();
			res=Ext.decode(res.responseText);
			if(res.Typeflag){
				window.location.target="_blank";
				window.location.href="/servlet/vfsservlet?fileid="+res.filename+"&fromjavafolder=true";
			}else{
				Ext.showAlert(res.errorMsg);
			}
		},scope:me},map);
	},
	loadData:function(type){//加载数据
		var me=this;
		var map=new HashMap();
		Ext.MessageBox.wait(common.label.refreshMSG, common.label.expWait);
		if(type=='all'){//加载全部
			map.put("flag","all");
		}else{//加载部分
			var data=me.tableObj.tablePanel.getSelectionModel().getSelection();
			var arry=[];
			map.put("flag","false");
			if(data.length<1){
				Ext.showAlert(common.label.outFileMsg);
				return;
			}
			for(var i=0;i<data.length;i++){
				arry.push(data[i].data.dbtype+data[i].data.a0100);
			}
			map.put("data",arry);
		}
		Rpc({functionId:'OM000000003',success:function(res){
			res=Ext.decode(res.responseText);
			Ext.MessageBox.close();
			if(res.typeFlag==true){
				me.tableObj.tablePanel.getStore().load({page:1});
			}else{
				Ext.showAlert(res.errorMsg);
			}
		},scope:me},map);
	},
	createQuery:function(){
		var me=this;
		var commonQuery = Ext.create("EHR.commonQuery.CommonQuery",{
			subModuleId:'OfficerManage_OfficerView_query',
			defaultQueryFields:me.defaultFields,
			optionalQueryFields:me.queryFields,
	        //通过设置field的codeData可以将不是代码的字段改为代码显示(只能是一级代码)
			beforeFieldRender:function(field){
			
				if(field.itemid=='Dbtype'){
					field.codeData=[];
					for(var i=0;i<me.dbNameList.length;i++){
						field.codeData.push({codeitemid:me.dbNameList[i].split('`')[1],codeitemdesc:me.dbNameList[i].split('`')[0]});
					}
					
				}
			},
			doQuery:function(items){//查询回调
				var map=new HashMap();
				map.put("params",items);
				Rpc({functionId:'OM000000005',success:function(res){
					res=Ext.decode(res.responseText);
					if(res.typeFlag){
						me.tableObj.tablePanel.getStore().load({page:1});
					}else{
						Ext.showAlert(res.errMsg);
					}
				},scope:me},map);
			}
		});
		return commonQuery;
	},
	outFileBtn:function(tableBuilder){//生成导出word pdf按钮
		    var me=this;
		    var currentPdf = common.button.cardOneFile.replace('{0}', 'PDF').replace('{1}', (this.inforkind == '2') ? common.button.cardOrg : common.button.cardPerson)// 当前人员生成pdf
		    var allPdf = common.button.cardAllFiles.replace('{0}', 'PDF').replace('{1}', (this.inforkind == '2') ? common.button.cardOrg : common.button.cardPerson)// 全部人员生成PDF
		    var partPersonPdf = common.button.cardFiles.replace('{0}', 'PDF').replace('{1}', (this.inforkind == '2') ? common.button.cardOrg : common.button.cardPerson)// 部分人员生成PDF
		    var onePersonOneDoc = common.button.cardDocument.replace('{0}', (this.inforkind == '2') ? common.button.cardOneOrgFile : common.button.cardPersonFile)// 一人一文档
		    var allPersonDoc = common.button.cardDocument.replace('{0}', (this.inforkind == '2') ? common.button.cardAllOrgFile : common.button.cardAllPersonFile)// 多人一文档
		    var currentWord = currentPdf.replace('PDF', 'WORD')// 当前人员生成Word
		    var allWord = allPdf.replace('PDF', 'WORD')// 全部人员生成Word
		    var partPersonWord = partPersonPdf.replace('PDF', 'WORD')// 部分人员生成Word
		    var menusPdf = Ext.create('Ext.menu.Menu', {
					   width: 150,
					   plain: true,
					   floating: true,
					   items: [
						   {
							   text: allPdf,
							   menu: [
								   {
									   text: onePersonOneDoc, //一人一文档
									   handler: function() {
										   me.outLRMX("pdf",'all','1')
									   }
								   },
								   { 
									   text: allPersonDoc,
									   handler: function() {
										   me.outLRMX("pdf",'all','all')
									   }

								   }
								    ]
						   },
						   {
							   text: partPersonPdf,
							   menu: [
								    {
								     text: onePersonOneDoc,
									 handler: function() {
										   me.outLRMX("pdf",'false','1')
									   }
								   },
								   {
									 text: allPersonDoc,
									 handler: function() {
										   me.outLRMX("pdf",'false','all')
									   }

								   }
							        ]
						   }
						   ]
		    })

		    var menusWord = Ext.create('Ext.menu.Menu', {
		    					width: 150,
							    plain: true,
							    floating: true,
							    items: [
								   {
									   text: allWord,
									   menu: [
										   {
							              text: onePersonOneDoc,
							              handler: function() {
							            	  me.outLRMX("word",'all','1')
											   }
										   },
										   {
							              text: allPersonDoc,
							              handler: function() {
							            	  me.outLRMX("word",'all','all')
										    }

										   }
										    ]
								   },
								   {
									   text: partPersonWord,
									   menu: [
											   {
									              text: onePersonOneDoc,
									              handler: function() {
									            	  me.outLRMX("word",'false','1')
												   }
											   },
											   {
									              text: allPersonDoc,
									              handler: function() {
									            	  me.outLRMX("word",'false','all')
											     }
											    }
										    ]
								   }
								   ]
		    })
		    var btnPdf = Ext.create('Ext.Button', {
		      width: 120,
		      text: common.button.toexport + 'PDF',
		      icon: '/images/outpdf.png',
		      iconCls: 'btn-Img-icon',
		      menu: menusPdf
		    })
		    var btnWord = Ext.create('Ext.Button', {
		      width: 120,
		      text: common.button.toexport + 'WORD',
		      icon: '/images/outword.png',
		      iconCls: 'btn-Img-icon',
		      menu: menusWord
		    })
		    tableBuilder.toolBar.add('-')
		    tableBuilder.toolBar.add(btnWord)
		 	tableBuilder.toolBar.add('-')
			tableBuilder.toolBar.add(btnPdf)
	}
})