Ext.define('ExpressOpinionsUL.ExpressOpinions',{
	constructor:function(config){
		expressopinion = this;
		expressopinion.task_id = config.task_id;
		expressopinion.approve_flag = config.approve_flag;
		expressopinion.tab_id = config.tab_id;
		var map = new HashMap();
    	map.put("task_id",expressopinion.task_id);
	    Rpc({functionId:'MB00002019',success:function(form,action){
	    	 var result = Ext.decode(form.responseText);
			 if(result.succeed){
			 	var topic = result.topic;//发表意见
				expressopinion.init(topic);
			 }
	    }},map);
	},
	init:function(topic,taskState){
		var title='';
		if(expressopinion.approve_flag=='3'){
			title = '报备意见';//MB.LABLE.checkinopinion;//报备意见
		}else if(expressopinion.approve_flag=='2'){
			title = '加签意见';//MB.LABLE.countersignopinion;//加签意见
		}
		var win = Ext.widget('window',{
			title:title,
			height:398,
			width:556,
			resizable:false,
	    	closable:true,
			modal:true,
			closeAction:'destroy',
			items:[{
				xtype:'textarea',
				height:'99%',
				width:'100%',
				value:topic
			}],
			buttonAlign:'center',
			buttons:[{text:common.button.save,handler:function(){
						var topic = this.ownerCt.ownerCt.items.get(0).getValue();
						if(topic.length>500){
							Ext.showAlert(title+"文本字数不能超过500！");
							return;
						}
						var map = new HashMap();
				    	map.put("topic",getEncodeStr(topic));
				    	map.put("task_id",expressopinion.task_id);
				    	map.put("approve_flag",expressopinion.approve_flag);
				    	map.put("tab_id", expressopinion.tab_id);
					    Rpc({functionId:'MB00002018',success:function(form,action){
					    	win.close();
					    	var result = Ext.decode(form.responseText);
					    	var unDealedTaskIds=result.unDealedTaskIds;//未处理单据号
			                if (unDealedTaskIds!=""){//如果有未处理完的人员，则刷新页面
			                	if(templateMain_me.templPropety.task_id==unDealedTaskIds){
			                		Ext.showAlert("请选中需要发表意见的记录!");
			                		return;
			                	}
			                	templateMain_me.templPropety.task_id=unDealedTaskIds; 
			                    templateTool_me.reLoadForm();
			                }
			                else { //如果人员都处理完了，则返回上一页面。
			                 	 	templateTool_me.returnBack();
			                }
					    }},map);
			}},{text:common.button.close,handler:function(){win.close()}}]
		}).show();
	}
})