var FeedBack = new Object();

/***
 * 反馈意见填写面板（设计方案改动，暂时不用）
 * userInfos:人员信息数组
 * 包含：a0101`nbase`a0100`zp_pos_id`link_id
 * var userInfos=new Array();
 *var userInfo = {a0101:record[i].data.a0101,
	            	nbase:record[i].data.nbase_e,
	            	a0100:record[i].data.a0100_e,
	                zp_pos_id:record[i].data.z0301_e,
	                link_id:"",
	                link_name:""};
	       
	    userInfos.push(userInfo);     
 */
FeedBack.write = function(userInfos,jsObjName)
{
	var title = "";
	var msg = "此处填写反馈给申请人";//提示信息
	var num = 0;
	for(var i=0;i<userInfos.length;i++)
	{
		num++;
		var userInfo = userInfos[i];
		if(i==0)
		{
			title = userInfo.link_name;
			msg += userInfo.a0101;
		}
	}
	if(title!="")
	{
		title += "(此处为对应招聘环节名称)-填写反馈结果";
	}else{
		title += "填写反馈结果";
	}
	if(num>1)
	{
		msg += "等"+num+"人的反馈结果";
	}else{
		msg += "的反馈结果";
	}
	var feedBackPanel=Ext.widget("window",{
        	 modal:true,
             title:title,
             region:'center',
             shadow:false,
             resizable:false,
             layout:'fit',
             buttonAlign: 'center',
             collapsible:false,
             titleCollapse:true,
             bodyStyle:'background-color:white',
             height:350,
             width:500,
             items:[{
             	xtype     : 'textareafield',
             	id        : 'content',
		        grow      : true,
		        padding   : '10 5 5 5',
		        emptyText : msg,
		        name      : 'message',
		        anchor    : '100%'
             }],
             buttons:[
                 {text:"确定",handler:function(){
	                var map = new HashMap();
				    map.put("userInfos",userInfos);
				    map.put("content",Ext.getCmp("content").getValue());
				    Rpc( {
						functionId : 'ZP0000002581',
						success :function(){
							feedBackPanel.close();
							if(jsObjName!="")
							{
								var datastore = Ext.data.StoreManager.lookup(jsObjName+'_dataStore');
	   		 					datastore.reload();
							}else{
								
							}
						}
					}, map);	
                 }},
                 {text:"关闭",handler:function(){feedBackPanel.close();}}
                 ], 
         listeners:{  
        	    'close':function(){
        	    }  
        	} 
         }).show();
	
}

Ext.define('EHR.ToolTipUL.ToolTip.feedback',{
	nbase:'',
	a0100:'',
	zp_pos_id:'',
	tipid:'',
	constructor:function(config) {
		tip_me=this;
		nbase=config.nbase;
		a0100=config.a0100;
		zp_pos_id=config.zp_pos_id;
		tipid=config.tipId;
		tip_me.fn = config.fn;
		tip_me.createTip(a0100,nbase,zp_pos_id,tipid);
	},
	createTip:function (a0100,nbase,zp_pos_id,tipid){
		var tooltip = Ext.getCmp('tool');
		if(tooltip!=null)
			tooltip.destroy();
		var toolTip = Ext.create('Ext.tip.ToolTip', {
		                    target: tipid,
						    id:'tool',
						    width: 490,
						    mouseOffset:[0,0],
						    autoHide: false,
						    shadow:false,
						    closable:true,
						    style :"background-color:white;border:1px #b0b0b0 solid; min-height:60;margin:0px;padding-left:10px;list-style:none;",
						    listeners:{
			    	                beforeshow:function updateTipBody(tip) {
									    		var map = new HashMap();
									    		map.put("nbase", nbase);
									    		map.put("a0100", a0100);
									    		map.put("zp_pos_id", zp_pos_id);
									    		Rpc({functionId:'ZP0000002003',success: function(form){
									 	  			var result = Ext.decode(form.responseText);	
										            	tip.update(result.queryFeedBack);
									    		}
									    		},map);
			    	                 }
			             }
	     });
		//edge浏览器使用onmouseover显示不出来
		Ext.callback(tip_me.fn,this,[toolTip]);
	   }	
});
