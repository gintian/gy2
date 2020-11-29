/**
*流程说明编辑  hej 2016-4-11
*
**/
Ext.define('TemplateNavigation.EditExplain',{
	constructor:function(config){
 		EditExplain_me = this;
    	EditExplain_me.content = config.content;
    	EditExplain_me.tab_id = config.tab_id;
    	EditExplain_me.module_id = config.module_id;
    	EditExplain_me.callBackFunc = config.callBackFunc;
    	EditExplain_me.moduleparams = config.moduleparams;
    	EditExplain_me.sys_type = config.sys_type;
    	EditExplain_me.tab_ids = config.tab_ids;
    	EditExplain_me.issearchdb = config.issearchdb;
    	EditExplain_me.init();
 	},
 	init:function(){
   		var config = {};
   		config.toolbar = [
			['Source','DocProps','-','NewPage','Preview','-','Templates'],
			['Cut','Copy','Paste','PasteText','PasteWord','-','Print'],
			['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
			['Form','Checkbox','Radio','TextField','Textarea','Select','Button','ImageButton','HiddenField'],
			'/',
			['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
			['OrderedList','UnorderedList','-','Outdent','Indent'],
			['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
			['Link','Unlink','Anchor'],
			['Image','Flash','Table','Rule','Smiley','SpecialChar','PageBreak','UniversalKey'],
			'/',
			['Style','FontFormat','FontName','FontSize'],
			['TextColor','BGColor'],
			['FitWindow','-','About']];
		var CKEditor = Ext.getCmp('ckeditorid');
		if(CKEditor){
			CKEditor.setValue(EditExplain_me.content);
		}else{
	 		CKEditor = Ext.create("EHR.ckEditor.CKEditor",{
				id:'ckeditorid',
				padding:'30 0 0 0',
				//border:1,
				hidden:false,
				width:document.documentElement.clientWidth-238,
				height:document.documentElement.clientHeight-100,
				value:EditExplain_me.content,
				ckEditorConfig:config
			});
		}
   		var editWin = Ext.getCmp('ckeditorWin');
   		if(editWin){
   			//editWin.add();
   		}else{
			editWin = Ext.widget('window',{
				id:'ckeditorWin',
		    	title:'流程说明',
		    	buttonAlign:'center', 
				modal:true,
				resizable:false,
		    	closable:true,
				closeAction:'hide',
				layout:{type:'vbox',align:'center'},
		    	items:[{xtype:'panel',width:document.documentElement.clientWidth-238,
					        border:1,
					    	height:document.documentElement.clientHeight-100,
					    	items:[CKEditor]}
			    	],
		    	buttons:[{text:'确定',handler:function(){
		    				var template = Ext.getCmp('template');
		    				if(EditExplain_me.issearchdb)
		    					template = Ext.getCmp('templateform');
		    				var htl = Ext.getCmp('ckeditorid').getHtml();
		    				var map = new HashMap();
					    	map.put("content",htl);
					    	map.put("tabid",EditExplain_me.tab_id+'');
						    Rpc({functionId:'MB00006007',success:function(form,action){
						    	Ext.require('TemplateNavigation.SearchMoudle', function(){
									Ext.create("TemplateNavigation.SearchMoudle", {moduleparams:EditExplain_me.moduleparams,tab_ids:EditExplain_me.tab_ids,issearchdb:EditExplain_me.issearchdb,
									module_id:EditExplain_me.module_id,callBackFunc:EditExplain_me.issearchdb?templatenavigation.callBackModuleFnNoLeft:templatenavigation.callBackModuleFn,
									main_panel:template,sys_type:EditExplain_me.sys_type,editcontent:htl,editid:EditExplain_me.tab_id});
								});
								editWin.close();
						    }},map);
		    			}},
		    			{text:'取消',handler:function(){
		    					editWin.close();
		    			}}]
		    });
	    }
	    editWin.show();
 		//var panel = Ext.widget('panel',{
 		//	border:0,
 		//	layout:{type:'vbox',align:'center'},
 		//	items:[topEditor,CKEditor]
 		//});
 		if(EditExplain_me.callBackFunc){
            Ext.callback(eval(EditExplain_me.callBackFunc),null,[panel]);
		}
 	}
})