/**
 * 选择代码型组件
 * @createtime August 24, 2017 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('ReviewFileURL.CodeSelectPicker', {
	xtype: 'codeselectpicker',
	constructor : function() {
		var extraParams = {
	    				//codesource:me.codesource,
					codesetid:jobtitle_reviewdiff.w0575codesetid,
					multiple:true,
					onlySelectCodeset:false,
					rootVisible:false
	    	};
	    	var treeStore = Ext.create('Ext.data.TreeStore',{
	        	fields: ['text','id','codesetid','orgtype'], 
	            proxy: {   
	                type:'transaction',
	                extraParams:extraParams,
	                functionId:'ZJ100000131'
	            },
			    listeners:{
			       load:function(self,records){
			       		for(var k=0; k<records.length; k++){
			       		    if(jobtitle_reviewdiff.ctrl_param.indexOf(","+records[k].get("id")+",") > -1)
			       		        records[k].set("checked",true);
			       		}
			       }
			    }
	     });
		
		var docks = {
			xtype:'toolbar',dock:'bottom',
			items:[
				{xtype:'button',text:'确定',handler:''}/*,
				{xtype:'button',text:'取消',handler:''}*/
			]
		};
		var codeselectpicker = Ext.create('Ext.tree.Panel', {
			id :'codeselectpicker',
			buttonAlign:'center',
			bbar :['->',{xtype:'button',width:75,text:'确定',handler:this.checkTable,scope:this},'->'],
			store : treeStore,
			border:false,
			width : 250,
			minHeight : 250,
			rootVisible : false
		});
		
		return codeselectpicker;
	},
	checkTable:function(){
		var checked = Ext.getCmp('codeselectpicker').getChecked( );
        var idArray = [];
        var ids = '';
        for(var p in checked){
        	if(checked.hasOwnProperty(p)){
	        	var id = checked[p].get('id');
	        	idArray.push(id);
	        	ids += (id+',');
        	}
        }
        if(!Ext.isEmpty(ids)){
        	ids = ids.substring(0, ids.length-1);
        }
        /*idArray.sort(this.sortNumber);
		var ids = idArray.join(",");*/
		jobtitle_reviewdiff.setPersonNum(ids);
	},
	sortNumber:function (a, b) {
		return b - a
	}
});
