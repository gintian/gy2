 /*
  * 显示常用条件记录列表 
  * lis 
  * 2015-12-22
  * 
  *调用示例：
  *	Ext.require('EHR.complexcondition.GeneralCondition',function(){
  *		Ext.create("EHR.complexcondition.GeneralCondition",{primarykey:primarykey});
  *	});
  */
 Ext.define('EHR.complexcondition.GeneralCondition',{
	 requires:['EHR.extWidget.proxy.TransactionProxy'],
	 /**
	  * 构造方法
	  * config 构造参数
	  */
	 constructor:function(config){
	 	generalCondition_me = this;
	 	generalCondition_me.init();
 	 },
 	 
 	 init:function(){
 		//对应方案的数据集
     	var store = Ext.create('Ext.data.Store', {
     	    fields:['dataValue', 'dataName'],
     	    proxy:{
 		    	type: 'transaction',
 		    	functionId:'ZJ100000094',
 		        reader: {
 		            type: 'json',
 		            root: 'selectedCondlist'         	
 		        }
 		},
 		autoLoad: true
     	});
     	
     	//显示条件的panel
     	var conditionGrid = Ext.widget("gridpanel",{
     		id:'relationPanel',
     		store: store,
     		rowLines:true,
     		columnLines:true,
     		border:false,
     		width: 280,
   	        height:350,
 	        buttonAlign : 'center',
 	        multiSelect : true,
 	        selModel: Ext.create("Ext.selection.CheckboxModel", {
 	            mode: "multi",//multi,simple,single；默认为多选multi
 	            checkOnly: false,//如果值为true，则只用点击checkbox列才能选中此条记录
 	            allowDeselect: false,//如果值true，并且mode值为单选（single）时，可以通过点击checkbox取消对其的选择
 	            enableKeyNav: true
 	        }),
 	        columns: [
 	            {header: 'id', dataIndex: 'dataValue', hidden: true},
 	            {text: common.button.commonConditions, dataIndex: 'dataName',menuDisabled:true,flex: 2,
 	            	field: {
 	                xtype: 'textfield',
 	                allowBlank: false
 	            	}
 	            }
 	        ],
 	        listeners:{
     			'celldblclick':function(){
		     		generalCondition_me.enter(conditionGrid);
     			}
     		}
     	});
 		
 			//弹出窗口
    		generalCondition_me.win = Ext.widget("window",{
    			 title: common.button.commonConditions,//常用条件
	             minButtonWidth:30,//为fbar按钮默认宽度
	             modal:true,
	             resizable:false,
	             border:false,
	             closeAction:'destroy',				  
	             items: [{
	          		xtype:'panel',
	          		width: 280,
	       	        height:350,
	 				items:[conditionGrid]
	            }],
	            fbar: [{xtype:'tbfill'},{
	 	        	xtype: 'button',
	     			text:common.button.ok,//确定
	     			handler:function(){
	            		generalCondition_me.enter(conditionGrid);
	            	}
	     		},
	     		{
	     			xtype: 'button',
	     			text:common.button.todelete,
	     			handler:function(){
	     				generalCondition_me.deleteRelation(conditionGrid);//删除方案
	     			}
	     		},
	     		{
	     			xtype: 'button',
	     			text:common.button.cancel,
	     			handler:function(){
	         			generalCondition_me.win.close();
	     			}
	     		},{xtype:'tbfill'}]
	     });
	     generalCondition_me.win.show();          
 	 },
 	 
 	 //选中选择条件
 	 enter:function(conditionGrid){
 		var records  = conditionGrid.getSelectionModel().getSelection();
		   var store = conditionGrid.getStore();
	       if (records.length == 0) {
	           Ext.showAlert(common.msg.selectOneCondition); //"请选择一个常用条件！
	           return;
	       }else if(records.length > 1){
	    	   Ext.showAlert(common.msg.onlyOneCondition);   //"只能选择一个常用条件！"
	    	   return;
	       }else{
	    	   var ids = [];  
               	Ext.each(records ,function(record){ 
               		ids.push(record.data.dataValue);   
               	}); 
	    	    var map = new HashMap();
               	map.put("ids",ids);
               	map.put("flag","sel");
               	Rpc({functionId:'ZJ100000095',success:function(response,action){
               		var result = Ext.decode(response.responseText);  	
               		if (result.succeed) {
               			generalCondition_me.win.close();
               			Ext.Array.each(records, function(record) {
               				Ext.getCmp('shry').setValue(Ext.getCmp('shry').value+decode(result.expr));//使用值追加的方式
               			});  
               		} else {  
               			Ext.MessageBox.show({  
               				title : common.button.promptmessage,  
               				msg : common.label.deleteFailed+"！",
               				buttons: Ext.Msg.OK,
               				icon: Ext.MessageBox.INFO  
               			});  
               		}  
               	}},map);  
	       }
 	 },
 	 //删除选中的方案
 	   deleteRelation:function(conditionGrid){
 		   var records  = conditionGrid.getSelectionModel().getSelection();
 		   var store = conditionGrid.getStore();
 	       if (records.length == 0) {
 	           Ext.showAlert(common.msg.selecDeleteCon);  //"请选择常用条件！"
 	       }else{
 	       	Ext.showConfirm(common.msg.confirmDelCon, function(button, text) {  
 	               if (button == "yes") {  
 	               	var ids = [];  
 	               	Ext.each(records ,function(record){ 
 	               		ids.push(record.data.dataValue);   
 	               	});  
 	               	
 	               	var map = new HashMap();
 	               	map.put("ids",ids);
 	               	map.put("flag","del");
 	               	Rpc({functionId:'ZJ100000095',success:function(response,action){
 	               		var success = Ext.decode(response.responseText).succeed;  	
 	               		if (success) {  
 	               			Ext.Array.each(records, function(record) {
 	               				store.remove(record);// 后台删除后页面也删除
 	               			});  
 	               		} else {
 	               			Ext.showAlert(common.label.deleteFailed+"！");  
 	               		}  
 	               	}},map);  
 	               }
 	           })
 	       }
 	     }
 });
