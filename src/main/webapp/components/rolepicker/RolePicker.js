/**
*人事异动角色选择window lis 2016-4-23
*参数：callBackFunc：返回选择数据，multiple：是否多选
*
**/
Ext.define('EHR.rolepicker.RolePicker', {
    extend: 'Ext.window.Window',
    requires:["EHR.extWidget.proxy.TransactionProxy"],
    xtype:'rolepicker',
    /**
   		构造函数
    **/
    constructor:function(config){
        rolePick = this;
        if(config){
        	rolePick.callBackFunc = config.callBackFunc;//确定时返回选择数据的回调函数
        	rolePick.multiple = config.multiple;//是否多选
        }
		this.init();
	},
	init:function(){
		Rpc({functionId:'ZJ100000151',async:false,success:rolePick.getRoleTableOK},new HashMap());
	 	   var bodyPanel = rolePick.tableObj.getMainPanel();
	 	   var rolepanel = Ext.widget('panel', {
	 			buttonAlign : 'center',
	 			autoScroll:true,
	 			width:'100%',
	 			height:'100%',
	 		    border:false,
	 		    layout:'fit',
	 		    items:[bodyPanel]
	 		});
	 	   var roleWin = Ext.widget('window',{
	 		   	   width:550,
		           height:330,
		           title:'请选择',
		           resizable:false,//是否允许改变窗口大小
				   modal:true,//模态窗口,窗口遮住的页面不可编辑
				   closeAction:'destroy',//控制按钮是销毁（destroy）还是隐藏（hide）
				   border: false,
				   plain:true,//true则主体背景透明，false则主体有小差别的背景色，默认为false
		           layout:'fit',
		           items:[rolepanel],
		           buttons:[{xtype:'tbfill'},
		                    {text:common.button.ok,handler:function(){
		                    	var tablePanel=rolePick.tableObj.tablePanel;
		                    	var selectRecords=tablePanel.getSelectionModel().getSelection(true);
		                    	var isSelectAll = tablePanel.getSelectionModel().doSelectAll;
		                		var selectRecordCount = tablePanel.getStore().getCount();
		                		var roleIds = "";
		                		Ext.Array.each(selectRecords,function(record,index){
		                			roleIds += "," + record.get('role_id_e'); 
		                		});
		                		var records=new Array();
		                		if(isSelectAll){//如果是全选
		                			tablePanel.getStore().each(function(record,index){
		                				if(roleIds.indexOf(record.get('role_id_e'))<0)
		                					records.push(record)
		                					
		                			},rolePick);
		                		}else{
		                			records = selectRecords;
		                		}
		                		
		                		var selectRecords = [];
		        				for(var i=0;i<records.length;i++){
		        					selectRecords.push(records[i].data);
		        				}
		                		if((isSelectAll && selectRecordCount == selectRecords.length) || (!isSelectAll && selectRecords.length<1)){
		                			Ext.Msg.alert(common.button.promptmessage,common.msg.selectData);
		                		}else{
		                			Ext.callback(rolePick.callBackFunc, null,[selectRecords]);
		                			roleWin.close();
		                		}
	                  	}},{text:common.button.cancel,handler:function(){roleWin.close();}},{xtype:'tbfill'}]
	 	   }).show();
	},
	
	/**
	 * 生成角色表格
	 * @param form
	 * @param action
	 */
	getRoleTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		//配置多选框
		obj.beforeBuildComp=function(config){
			config.tableConfig.selModel={
					selType:'checkboxmodel',
					lockableScope:'both',
					checkOnly: true,     //只能通过checkbox选择
					mode:rolePick.multiple?'MULTI':'SINGLE',
					enableAllSelect:true};
		};
		rolePick.tableObj = new BuildTableObj(obj);
	}
});