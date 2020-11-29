/**
* 业务申请 页签生成js
* zhaoxg 2016-3-8
*/
Ext.define('TemplateNavigation.BusinessApply',{
   constructor:function(config){
   		if(Ext.getCmp("businessapply1_toolbar")){//由于点击页签重新加载表格工具，而页签的removeAll（true）未能销毁之前曾经加载过的，导致对象冲突，故有此判断
    		Ext.getCmp("businessapply1_toolbar").destroy();
    	}
    	BusinessApplyScope = this;
    	BusinessApplyScope.callBackFunc = config.callBackFunc;//回调函数（itemid，panel）  可用于该组件之渲染
    	BusinessApplyScope.itemid = config.itemid;//配合回调函数把该组件渲染到的位置
    	BusinessApplyScope.clienth = config.clienth;
    	BusinessApplyScope.module_id = config.module_id;
    	var map = new HashMap();
    	map.put("module_id",BusinessApplyScope.module_id);
		map.put("operationcode","");
	    Rpc({functionId:'MB00006005',async:false,success:function(form,action){
	    	var result = Ext.decode(form.responseText);
	    	var flag=result.succeed;
			if(flag==true){
				var templatejson=result.templatejson;
				BusinessApplyScope.templatejson = templatejson;
		    	var conditions=result.tableConfig;
				var obj = Ext.decode(conditions);
				var templateObj = new BuildTableObj(obj);
	  		  	BusinessApplyScope.init(templateObj);
	  		}else{
				Ext.showAlert(result.message);
			}
	    }},map);
    },
   	init:function(templateObj){
		var BusinessApplyPanel = Ext.create('Ext.panel.Panel', {
			id:'businessapplyId',		
			border : false,
			height:BusinessApplyScope.clienth,
			autoScroll:true,
			layout:'fit',
			items:[templateObj.getMainPanel()]
		})	
		if(BusinessApplyScope.callBackFunc){
            Ext.callback(eval(BusinessApplyScope.callBackFunc),null,[BusinessApplyScope.itemid,BusinessApplyPanel]);
		}
   	},
   	
   	getTopic:function(value, metaData, Record){//主题
   		var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id;
		var html="<a href='javascript:BusinessApplyScope.showCard(\""+tabid+"\",\""+ins_id+"\");'>"+value+"<a/>";
		return html;
	},
   	
	getSploop:function(value, metaData, Record){
		var tabid = Record.data.tabid;
		var ins_id = Record.data.ins_id;
		var task_id = Record.data.task_id;
		return "<a href=\"javascript:BusinessApplyScope.showCard('"+tabid+"','"+ins_id+"');\"  ><img src='"+rootPath+"/images/new_module/dealto_green.gif' width='16' height='16' border='0'></a>";
	},
	showCard:function(tabid,ins_id){
		BusinessApplyScope.myMask = Ext.getCmp("maskId");
      	if(!!!BusinessApplyScope.myMask){
	      	BusinessApplyScope.myMask = new Ext.LoadMask({
	      		id:"maskId",
			    target : Ext.getCmp("template")
			});
		}
		BusinessApplyScope.myMask.show();
       	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'TemplateMainUL': rootPath+'/module/template/templatemain'
			}
		});
		var templateObj = new Object();
		templateObj.sys_type="1";
		templateObj.tab_id=tabid;
		templateObj.return_flag="5";
		templateObj.module_id=BusinessApplyScope.module_id;
		templateObj.approve_flag="1";
		templateObj.task_id="0";
        templateObj.card_view_type="1";
	    templateObj.view_type="card";
		templateObj.callBack_init="BusinessApplyScope.tempFunc";
		templateObj.callBack_close="BusinessApplyScope.goBack";
  		Ext.require('TemplateMainUL.TemplateMain', function(){
			TemplateMainGlobal = Ext.create("TemplateMainUL.TemplateMain", {templPropety:templateObj});
		});
	},
   	tempFunc:function(){
		BusinessApplyScope.myMask.hide();
		Ext.getCmp("template").removeAll(false);
		Ext.getCmp("template").add(templateMain_me.mainPanel);
	},
	goBack:function(){
		Ext.getCmp("template").removeAll(true);
		Ext.getCmp("template").add(templatenavigation.tabs);
	}
  }
)