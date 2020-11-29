/**
*业务分类 模块详细 hej 2016-4-8
*
**/
Ext.define('TemplateNavigation.SearchMoudle',{
 	constructor:function(config){
 		SearchMoudle_me = this;
    	SearchMoudle_me.moduleparams = config.moduleparams;
    	SearchMoudle_me.module_id = config.module_id;
    	SearchMoudle_me.callBackFunc = config.callBackFunc;
    	SearchMoudle_me.sys_type = config.sys_type;
    	SearchMoudle_me.main_panel = config.main_panel;
    	SearchMoudle_me.editcontent = config.editcontent;
    	SearchMoudle_me.editid = config.editid;
    	SearchMoudle_me.tab_ids = config.tab_ids;
    	SearchMoudle_me.issearchdb = config.issearchdb;
    	SearchMoudle_me.init();
 	},
 	init:function(){
 		var map = new HashMap();
    	map.put("encryptParam",SearchMoudle_me.moduleparams);
    	map.put("module_id",SearchMoudle_me.module_id);
    	map.put("tab_ids",SearchMoudle_me.tab_ids);
    	//map.put("return_flag",SearchMoudle_me.return_flag);
	    Rpc({functionId:'MB00006006',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	SearchMoudle_me.templist = result.templist;
	    	SearchMoudle_me.createPanel();
	    }},map);
 	},
 	createPanel:function(){
		var id = "";
 	    if(SearchMoudle_me.moduleparams==undefined&&SearchMoudle_me.tab_ids!=""){
 	    	id = SearchMoudle_me.tab_ids;
 	    }else{
 	    	id = SearchMoudle_me.moduleparams;
 	    }
 		var spanel = Ext.getCmp(id); 
 		
 		if(spanel==undefined){
 			var list = new Array();
	 	    spanel = Ext.widget('panel',{
	 	    	id:id,
	 	    	border:1,
	 	    	layout:{type:'hbox',align:"center",pack:'center'},
	 	    	autoScroll:true
	 	    });
	 		var html = "";
	 		html+="<table  border='0' cellpadding='0' cellspacing='0' align='center' class='ListTable'>";
	 		
	 		html+="<tr>";
	 		for(var i=0;i<SearchMoudle_me.templist.length;i++){
		 		var content = SearchMoudle_me.templist[i].content;
		 		var tabid = SearchMoudle_me.templist[i].tabid;
		 		var timestamp = SearchMoudle_me.templist[i].timestamp;
		 		var id = "tem"+"_"+tabid+"_"+timestamp;//bug 43932 不同分类配置同一个模版，显示不正确。
		 		html+="<td align='left' id='"+id+"'  valign='top' class='RecordRow'>";
		 		html+=content+'</td>';
	 		}
	 		
	 		html+="<tr>";
	 		for(var k=0;k<SearchMoudle_me.templist.length;k++){
	 			var tabid = SearchMoudle_me.templist[k].tabid;
		 		var sp_flag = SearchMoudle_me.templist[k].sp_flag;
		 		var content = SearchMoudle_me.templist[k].content;
		 		//var return_flag = SearchMoudle_me.templist[k].return_flag;
		 		var view = SearchMoudle_me.templist[k].view;
		 		var ishave = SearchMoudle_me.templist[k].ishave;
		 		var isEdit = SearchMoudle_me.templist[k].isEdit;
		 		var isManage = SearchMoudle_me.templist[k].isManage;
		 		var timestamp = SearchMoudle_me.templist[k].timestamp;
				var id = "but"+"_"+tabid+"_"+timestamp;//bug 43932 不同分类配置同一个模版，显示不正确。
				html+="<td align='center' id='"+id+"' valign='middle' class='RecordRow'>";
		 		html+="</td>";
				var conitems = [];
				var width = 0;
	 			if(ishave==null||ishave!='1'){
	 				if(isEdit=='true'){
	 					conitems.push({xtype:'button',tabid:tabid,content:content,text:'流程说明',handler:function(){
					    		SearchMoudle_me.edit_inf(this.tabid,this.content);
					    	}});
			 			//html+="<INPUT type='button' class='mybutton' onclick='javascript:SearchMoudle_me.edit_inf("+tabid+","+return_flag+");' name='b_edit' value='流程说明' disabled='true'>";
			 		}
			 		if(isManage=='true'){
			 		}
	 			}else{
		 			if(isEdit=='true'){
		 				if(isManage=='true'){
		 					conitems.push({xtype:'button',tabid:tabid,content:content,text:'流程说明',margin:'0 5 0 0',handler:function(){
					    		SearchMoudle_me.edit_inf(this.tabid,this.content);
					    	}});
		 				}else{
		 					conitems.push({xtype:'button',tabid:tabid,content:content,text:'流程说明',handler:function(){
					    		SearchMoudle_me.edit_inf(this.tabid,this.content);
					    	}});
		 				}
			 		}
			 		if(isManage=='true'){
			 		    conitems.push({xtype:'button',view:view,tabid:tabid,text:'业务处理',handler:function(){
					    		SearchMoudle_me.fill_out(this.view,this.tabid);
					    	}});
			 			//html+="<INPUT type='button' class='mybutton' onclick='javascript:SearchMoudle_me.fill_out("+view+","+tabid+","+return_flag+");' name='bc_btn1' value='业务处理'>";
			 		}
	 			}
	 			if(isEdit=='true'&&isManage=='true'){
	 				width = 140;
	 			}else{
	 				width = 65;
	 			}
	 			var buttoncon = Ext.widget('container',{
	 					id:id,
	 					width:width,
	 					items:conitems
				});
				list.push(buttoncon);
		 	}
	 		html+="</tr>";
	 		html+="</table>";
	 		var conpanel = Ext.widget('panel',{
	 				padding:'30 0 0 0',
	 		        minHeight:document.body.clientHeight-10,
	 				border:0,
	 				html:html,
	 				listeners:{
			    		render:function(){
			    			for(var i = 0; i < list.length; i++){
			    				list[i].render(list[i].getId());
			    			}
			    		}
				    }
	 		});
	 		spanel.add(conpanel);
	 	}else{
	 		if(SearchMoudle_me.editcontent!=undefined&&SearchMoudle_me.editid!=undefined){
		 		for(var i=0;i<SearchMoudle_me.templist.length;i++){
			 		var tabid = SearchMoudle_me.templist[i].tabid;
			 		var timestamp = SearchMoudle_me.templist[i].timestamp;
			 		if(tabid==SearchMoudle_me.editid){
				 		var id = "tem"+"_"+tabid+"_"+timestamp;//bug 45476
				 		Ext.get(id).dom.innerHTML = SearchMoudle_me.editcontent;
				 		Ext.ComponentQuery.query("button[tabid='"+tabid+"']")[0].content = SearchMoudle_me.editcontent;
			 		}
		 		}
	 		}
	 	}
 		
 		if(SearchMoudle_me.callBackFunc){
            Ext.callback(eval(SearchMoudle_me.callBackFunc),null,[spanel]);
		}
 	},
 	/*
 	*流程说明
 	*/
 	edit_inf:function(tabid,content){
		//window.open("/general/template/search_module.do?b_edit=link&tabid="+tabid,"_blank");
		if(SearchMoudle_me.moduleparams==undefined&&SearchMoudle_me.tab_ids!=""){
			Ext.require('TemplateNavigation.EditExplain', function(){
			    Ext.create("TemplateNavigation.EditExplain", {module_id:SearchMoudle_me.module_id,sys_type:SearchMoudle_me.sys_type,tab_ids:SearchMoudle_me.tab_ids,issearchdb:SearchMoudle_me.issearchdb,
			    content:content,tab_id:tabid,return_flag:'6'});
			});
		}else{
			Ext.require('TemplateNavigation.EditExplain', function(){
			    Ext.create("TemplateNavigation.EditExplain", {module_id:SearchMoudle_me.module_id,moduleparams:SearchMoudle_me.moduleparams,sys_type:SearchMoudle_me.sys_type,issearchdb:SearchMoudle_me.issearchdb,
			    content:content,tab_id:tabid,return_flag:'6'});
			});
		}
		
	},
	/*
 	*业务处理
 	*/
	fill_out:function(view,tabid){
		SearchMoudle_me.myMask = Ext.getCmp("maskId");
		var targetform = Ext.getCmp("template");
		if(SearchMoudle_me.issearchdb)
			targetform = Ext.getCmp("templateform");
	    if(!!!SearchMoudle_me.myMask){
			SearchMoudle_me.myMask = new Ext.LoadMask({
				id:"maskId",
			    target : targetform
			});
	    }
	    SearchMoudle_me.myMask.show();
       	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateMainUL': rootPath+'/module/template/templatemain'
			}
		});
		if(SearchMoudle_me.issearchdb){
			var map = new HashMap();
			map.put("module_id",SearchMoudle_me.module_id);
			map.put("approve_flag",'1');
			//map.put("return_flag",templateform.return_flag);
			map.put("tab_id",tabid+'');
			//map.put("sys_type",templateform.sys_type);
		    Rpc({functionId:'MB00006017',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					if(result.temflag=='1'){
						SearchMoudle_me.dataList=result.dataList;
						var records = [];
						for(var i=0;i<SearchMoudle_me.dataList.length;i++){
							records.push(SearchMoudle_me.dataList[i]);
						}
						var hashvo = new HashMap();
						hashvo.put("selectdata",records);
						Rpc({functionId:"MB00006009",scope:this,success:function(res){
							var result = Ext.decode(res.responseText);
							if(result.succeed){
								var templateObj = new Object();
								templateObj.sys_type="1";
								templateObj.tab_id=result.tab_id;
								templateObj.return_flag="6";
								templateObj.module_id=SearchMoudle_me.module_id;
								templateObj.approve_flag="1";
								templateObj.task_id=result.taskIds;
								templateObj.callBack_init="SearchMoudle_me.tempFunc";
								templateObj.callBack_close="SearchMoudle_me.tempCallbackFunc";
						  		Ext.require('TemplateMainUL.TemplateMain', function(){
									TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
								});
							}else{
								Ext.showAlert(result.message);
							}	
						}},hashvo);
					}else{
						SearchMoudle_me.toTemplateMain(view,tabid);
					}
				}else{
					Ext.showAlert(result.message);
				}
		    }},map);
		}else{
			SearchMoudle_me.toTemplateMain(view,tabid);
		}
	},
	tempFunc:function(){
		SearchMoudle_me.myMask.destroy();
		SearchMoudle_me.main_panel.removeAll(false);
		SearchMoudle_me.main_panel.add(templateMain_me.mainPanel);
	},
	tempCallbackFunc:function(){
		var template = Ext.getCmp('template');
		if(SearchMoudle_me.issearchdb)
			template = Ext.getCmp('templateform');
		SearchMoudle_me.main_panel.removeAll(true);
		Ext.require('TemplateNavigation.SearchMoudle', function(){
			Ext.create("TemplateNavigation.SearchMoudle", {moduleparams:SearchMoudle_me.moduleparams,tab_ids:SearchMoudle_me.tab_ids,issearchdb:SearchMoudle_me.issearchdb,
			module_id:SearchMoudle_me.module_id,callBackFunc:SearchMoudle_me.issearchdb?templatenavigation.callBackModuleFnNoLeft:templatenavigation.callBackModuleFn,
			main_panel:template,sys_type:SearchMoudle_me.sys_type});
		});
	},
	toTemplateMain:function(view,tabid){
		var templateBean = new HashMap();
		templateBean.put('module_id',SearchMoudle_me.module_id+'');
		templateBean.put('sys_type',SearchMoudle_me.sys_type+'');
		templateBean.put('tab_id',tabid+'');
		templateBean.put('view_type',view);
		templateBean.put('approve_flag',"1");
		templateBean.put('return_flag','6');
		templateBean.put('callBack_init',SearchMoudle_me.tempFunc);
		templateBean.put('callBack_close',SearchMoudle_me.tempCallbackFunc);
		Ext.require('TemplateMainUL.TemplateMain', function(){
			    Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateBean});
		});
	}
})