Ext.define('TemplateNavigation.TemplateForm',{
	constructor : function(config) {
		templateform = this;
		templateform.templateFPrarm = config.templateFPrarm;
		templateform.module_id = templateform.templateFPrarm.module_id;
		templateform.approve_flag = templateform.templateFPrarm.approve_flag;
		templateform.return_flag = templateform.templateFPrarm.return_flag;
		templateform.tab_id = templateform.templateFPrarm.tab_id;
		templateform.sys_type = templateform.templateFPrarm.sys_type;
		templateform.query_type = "";
		templateform.days = 1080;
		templateform.tabid = '';
		templateform.templatevp = Ext.getCmp('templateform');
		if(templateform.templatevp){
		}else{
			templateform.templatevp = Ext.create('Ext.container.Viewport',{
                autoScroll:false,
                id:'templateform',
                style:'backgroundColor:white',
                layout:'fit'
		    });
	    }
		if(templateform.tab_id.indexOf(',')!=-1){//走模板
			templateform.showModel(true);
		}else{
			var map = new HashMap();
			map.put("module_id",templateform.module_id);
			map.put("approve_flag",templateform.approve_flag);
			map.put("return_flag",templateform.return_flag);
			map.put("tab_id",templateform.tab_id);
			map.put("sys_type",templateform.sys_type);
		    Rpc({functionId:'MB00006017',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					if(result.temflag=='1'){
						templateform.dataList=result.dataList;
						templateform.templateinit(templateform.dataList);
					}else{
						templateform.showCard(templateform.tab_id);
					}
				}else{
					Ext.showAlert(result.message);
				}
		    }},map); 
		}
		
	},
	templateinit:function(obj){
		var records = [];
		for(var i=0;i<obj.length;i++){
			records.push(obj[i]);
		}
		var hashvo = new HashMap();
		hashvo.put("selectdata",records);
		Rpc({functionId:"MB00006009",scope:this,success:function(res){
			var result = Ext.decode(res.responseText);
			if(result.succeed){
				templateform.myMask = new Ext.LoadMask({
				    target : Ext.getCmp("templateform")
				});
				templateform.myMask.show();
		       	Ext.Loader.setConfig({
					enabled: true,
					paths: {
						'TemplateMainUL': rootPath+'/module/template/templatemain'
					}
				});
				var templateObj = new Object();
				templateObj.sys_type="1";
				templateObj.tab_id=result.tab_id;
				templateObj.return_flag="14";
				templateObj.module_id=templateform.module_id;
				templateObj.approve_flag="1";
				templateObj.task_id=result.taskIds;
			//	templateObj.sp_batch="1"; //20160905 dengcan 无用了，ins_id表示当前选中记录的单号
				templateObj.callBack_init="templateform.tempFunc";
				templateObj.callBack_close="templateform.goBack";
		  		Ext.require('TemplateMainUL.TemplateMain', function(){
					TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
				});
			}else{
				Ext.showAlert(result.message);
			}	
		}},hashvo);
	},
	showCard:function(tabid){
      	templateform.myMask = new Ext.LoadMask({
		    target : Ext.getCmp("templateform")
		});
		templateform.myMask.show();
       	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateMainUL': rootPath+'/module/template/templatemain'
			}
		});
		var templateObj = new Object();
		templateObj.sys_type="1";
		templateObj.tab_id=tabid;
		templateObj.return_flag="14";
		templateObj.module_id=templateform.module_id;
		templateObj.approve_flag=templateform.approve_flag;
		templateObj.callBack_init="templateform.tempFunc";
  		Ext.require('TemplateMainUL.TemplateMain', function(){
			TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
		});
	},
	tempFunc:function(){
		templateform.myMask.hide();
		Ext.getCmp("templateform").removeAll(false);
		Ext.getCmp("templateform").add(templateMain_me.mainPanel);
	},
	goBack:function(){
		Ext.getCmp("templateform").removeAll(true);
		//var store=Ext.data.StoreManager.lookup('templateform1_dataStore');
		//if(store.getCount()>1){
			//Ext.getCmp("templateform").add(templateform.templateObj.bodyPanel);
			//Ext.getCmp("templateform").setMargin('5 0 0 0');
			//store.reload();
		//}else{
			templateform.showCard(templateform.tab_id);
			//Ext.getCmp("templateform").setMargin('0 0 0 0');
		//}
		//DbTaskScope.query();
	},
	showModel:function(flag){
		var templateNav = new Object();
		templateNav.module_id=templateform.module_id;
		templateNav.return_flag=templateform.return_flag;
		templateNav.sys_type=templateform.sys_type;
		templateNav.tab_ids=templateform.tab_id;
		templateNav.issearchdb=flag;
		templateNav.href='';
  		Ext.require('TemplateNavigation.TemplateNavigation', function(){
			Ext.create("TemplateNavigation.TemplateNavigation", {templateNav:templateNav});
		});
	}
});